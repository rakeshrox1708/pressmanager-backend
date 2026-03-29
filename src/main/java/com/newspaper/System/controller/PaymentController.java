package com.newspaper.System.controller;

import com.newspaper.System.dto.PaymentDTO;
import com.newspaper.System.dto.PaymentVerifyDTO;
import com.newspaper.System.dto.request.PaymentRequestDTO;
import com.newspaper.System.dto.request.SubscriptionPaymentDTO;
import com.newspaper.System.dto.response.BillResponseDTO;
import com.newspaper.System.dto.response.PaymentResponseDTO;
import com.newspaper.System.model.Invoice;
import com.newspaper.System.model.Newspaper;
import com.newspaper.System.repository.InvoiceRepository;
import com.newspaper.System.repository.NewspaperRepository;
import com.newspaper.System.repository.PaymentRepository;
import com.newspaper.System.repository.UserRepository;
import com.newspaper.System.response.ApiResponse;
import com.newspaper.System.service.BillingService;
import com.newspaper.System.service.PaymentService;
import com.newspaper.System.service.RazorpayService;
import com.newspaper.System.service.UserService;
import com.razorpay.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
//@PreAuthorize("hasRole('ROLE_USER')")
public class PaymentController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PaymentRepository paymentRepo;

    @Autowired
    private BillingService billingService;

    //new for daily based newspaper
    @Autowired
    private PaymentService paymentService;

    @Autowired
    private RazorpayService razorpayService;

    @Autowired
    private InvoiceRepository invoiceRepo;

    @Autowired
    private UserService userService ;

    @Autowired
    private NewspaperRepository newspaperRepo;

    @GetMapping("/bill")
    public ApiResponse<BillResponseDTO> getBill(Authentication auth) {

        int userId = Integer.parseInt(auth.getName());

        return ApiResponse.success(
                "Bill fetched",
                paymentService.getBill(userId)
        );
    }
//    @PostMapping("/pay")
//    public ApiResponse<PaymentResponseDTO> pay(
//            Authentication auth,
//            @RequestBody PaymentRequestDTO dto) {
//
//        int userId = Integer.parseInt(auth.getName());
//
//        return ApiResponse.success(
//                "Payment Successful",
//                paymentService.makePayment(userId, dto)
//        );
//    }

    @GetMapping("/history")
    public List<PaymentResponseDTO> history(Authentication auth) {
        return paymentService.history(
                Integer.parseInt(auth.getName())
        );
    }

    @GetMapping("/all")
    public List<PaymentDTO> getAllPayments() {

        return paymentRepo.findAll().stream().map(p -> {

            PaymentDTO dto = new PaymentDTO();

            dto.paymentId = p.getPaymentId();

            dto.userId = p.getInvoice()
                    .getSubscription()
                    .getUser()
                    .getUserId();

            dto.userName = p.getInvoice()
                    .getSubscription()
                    .getUser()
                    .getName();

            dto.amount = p.getAmount();
            dto.status = p.getStatus();

            return dto;

        }).toList();
    }

    @PostMapping("/create-order/{invoiceId}")
    public Map<String, Object> createOrder(@PathVariable Long invoiceId) throws Exception {

        Invoice invoice = invoiceRepo.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        Order order = razorpayService.createOrder(invoice.getTotalAmount());

        Map<String, Object> response = new HashMap<>();
        response.put("orderId", order.get("id"));
        response.put("amount", invoice.getTotalAmount());

        return response;
    }

    @PostMapping("/verify")
    public PaymentResponseDTO verifyPayment(@RequestBody PaymentVerifyDTO dto) {

        String data = dto.getOrderId() + "|" + dto.getPaymentId();

        String generatedSignature =
                calculateHMAC(razorpayService.getSecret(), data);

        if (!generatedSignature.equals(dto.getSignature())) {
            throw new RuntimeException("Payment verification failed ❌");
        }

        // ✅ Mark payment SUCCESS
        return paymentService.payInvoice(dto.getInvoiceId(), "ONLINE");
    }
    private String calculateHMAC(String secret, String data) {

        try {
            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");

            javax.crypto.spec.SecretKeySpec secretKey =
                    new javax.crypto.spec.SecretKeySpec(secret.getBytes(), "HmacSHA256");

            mac.init(secretKey);

            byte[] raw = mac.doFinal(data.getBytes());

            StringBuilder hex = new StringBuilder();

            for (byte b : raw) {
                hex.append(String.format("%02x", b));
            }

            return hex.toString();

        } catch (Exception e) {
            throw new RuntimeException("HMAC calculation failed", e);
        }
    }
    @PostMapping("/create-order-for-subscription")
    public Map<String, Object> createOrderForSubscription(@RequestBody Map<String, Integer> req) throws Exception {

        int newspaperId = req.get("newspaperId");

        Newspaper paper = newspaperRepo.findById(newspaperId).orElseThrow();

        Order order = razorpayService.createOrder(paper.getMonthlyPrice());

        Map<String, Object> response = new HashMap<>();
        response.put("orderId", order.get("id"));
        response.put("amount", paper.getMonthlyPrice());

        return response;
    }

    @PostMapping("/verify-subscription")
    public void verifySubscription(
            @RequestBody SubscriptionPaymentDTO dto,
            Authentication auth) {

        int userId = Integer.parseInt(auth.getName());

        String data = dto.getOrderId() + "|" + dto.getPaymentId();

        String generatedSignature =
                calculateHMAC(razorpayService.getSecret(), data);

        if (!generatedSignature.equals(dto.getSignature())) {
            throw new RuntimeException("Payment failed ❌");
        }

        // ✅ secure userId from token
        userService.subscribe(
                userId,
                dto.getNewspaperId(),
                "MONTHLY",
                "ONLINE"
        );
    }
}
