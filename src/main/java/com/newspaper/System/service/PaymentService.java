package com.newspaper.System.service;

import com.newspaper.System.dto.request.PaymentRequestDTO;
import com.newspaper.System.dto.response.BillResponseDTO;
import com.newspaper.System.dto.response.PaymentResponseDTO;
import com.newspaper.System.model.Invoice;
import com.newspaper.System.model.Payment;
import com.newspaper.System.model.Subscription;
import com.newspaper.System.model.User;
import com.newspaper.System.repository.InvoiceRepository;
import com.newspaper.System.repository.PaymentRepository;
import com.newspaper.System.repository.SubscriptionRepository;
import com.newspaper.System.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private BillingService billingService;

    @Autowired
    private SubscriptionRepository subscriptionRepo;

    @Autowired
    private InvoiceRepository invoiceRepo;

    @Autowired
    private EmailService emailService;

//    public BillResponseDTO getBill(int userId) {
//
//        User user = userRepo.findById(userId)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        List<Subscription> activeSubs =
//                subscriptionRepo.findByUserAndStatus(user, "ACTIVE");
//
//        if (activeSubs.isEmpty()) {
//            return new BillResponseDTO(0, null);
//        }
//
//        Subscription subscription = activeSubs.get(0);
//
//        double amount = billingService.calculateMonthlyBill(user);
//
//        return new BillResponseDTO(amount, subscription.getSubscriptionId());
//    }
//
//    public PaymentResponseDTO makePayment(int userId, PaymentRequestDTO dto) {
//
//        User user = userRepo.findById(userId)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        // 🔥 GET SUBSCRIPTION FROM REQUEST
//        Subscription subscription = subscriptionRepo.findById(dto.getSubscriptionId())
//                .orElseThrow(() -> new RuntimeException("Subscription not found"));
//
//        // Optional safety check (recommended)
//        if (subscription.getUser().getUserId() != userId) {
//            throw new RuntimeException("Subscription does not belong to user");
//        }
//
//        LocalDate now = LocalDate.now();
//
//        boolean alreadyPaid =
//                paymentRepo.existsByUserAndBillingMonthAndBillingYear(
//                        user,
//                        now.getMonthValue(),
//                        now.getYear()
//                );
//
//        if (alreadyPaid) {
//            throw new RuntimeException("Already paid for this month");
//        }
//
//        double amount = billingService.calculateMonthlyBill(user);
//
//        Payment payment = new Payment();
//        payment.setUser(user);
//        payment.setSubscription(subscription);   // ✅ ADD HERE
//        payment.setAmount(amount);
//        payment.setPaymentDate(now);
//        payment.setPaymentMode(dto.getPaymentMode());
//        payment.setStatus("SUCCESS");
//        payment.setBillingMonth(now.getMonthValue());
//        payment.setBillingYear(now.getYear());
//
//        payment = paymentRepo.save(payment);
//
//        PaymentResponseDTO response = new PaymentResponseDTO();
//        response.setPaymentId(payment.getPaymentId());
//        response.setAmount(payment.getAmount());
//        response.setPaymentDate(payment.getPaymentDate());
//        response.setPaymentMode(payment.getPaymentMode());
//        response.setStatus(payment.getStatus());
//        response.setBillingMonth(payment.getBillingMonth());
//        response.setBillingYear(payment.getBillingYear());
//
//        return response;
//    }
//
//    public List<PaymentResponseDTO> history(int userId) {
//
//        User user = userRepo.findById(userId).orElseThrow();
//
//        return paymentRepo.findByUser(user)
//                .stream()
//                .map(p -> {
//                    PaymentResponseDTO dto = new PaymentResponseDTO();
//                    dto.paymentId = p.getPaymentId();
//                    dto.amount = p.getAmount();
//                    dto.paymentDate = p.getPaymentDate();
//                    dto.paymentMode = p.getPaymentMode();
//                    dto.status = p.getStatus();
//                    dto.billingMonth = p.getBillingMonth();
//                    dto.billingYear = p.getBillingYear();
//                    return dto;
//                }).toList();
//    }

    public BillResponseDTO getBill(int userId) {

        List<Invoice> unpaid =
                invoiceRepo.findByUserAndStatus(userId, "UNPAID");

        if (unpaid.isEmpty()) {
            return null;
        }

        Invoice invoice = unpaid.get(0);

        BillResponseDTO dto = new BillResponseDTO();
        dto.setInvoiceId(invoice.getInvoiceId());
        dto.setNewspaperName(
                invoice.getSubscription()
                        .getNewspaper()
                        .getName()
        );
        dto.setBillingType(
                invoice.getSubscription()
                        .getBillingType()
        );
        dto.setBillingMonth(invoice.getBillingMonth());
        dto.setBillingYear(invoice.getBillingYear());
        dto.setBaseAmount(invoice.getBaseAmount());
        dto.setLateFee(invoice.getLateFee());
        dto.setTotalAmount(invoice.getTotalAmount());
        dto.setStatus(invoice.getStatus());

        return dto;
    }

    public PaymentResponseDTO makePayment(int userId, PaymentRequestDTO dto) {

        // dto.getSubscriptionId() will now be invoiceId
        Invoice invoice = invoiceRepo.findById(dto.getInvoiceId())
                .orElseThrow(() -> new RuntimeException("Invoice not found: " + dto.getInvoiceId()));

        if ("PAID".equals(invoice.getStatus())) {
            throw new RuntimeException("Already paid");
        }

        Payment payment = new Payment();
        payment.setInvoice(invoice);
        payment.setAmount(invoice.getTotalAmount());
        payment.setPaymentMode(dto.getPaymentMode());
        payment.setStatus("SUCCESS");
        payment.setPaymentDate(LocalDate.now());

        paymentRepo.save(payment);

        invoice.setStatus("PAID");
        invoice.setPaidDate(LocalDate.now());
        invoiceRepo.save(invoice);

        PaymentResponseDTO response = new PaymentResponseDTO();
        response.setPaymentId(payment.getPaymentId());
        response.setAmount(payment.getAmount());
        response.setPaymentDate(payment.getPaymentDate());
        response.setPaymentMode(payment.getPaymentMode());
        response.setStatus(payment.getStatus());
        response.setBillingMonth(invoice.getBillingMonth());
        response.setBillingYear(invoice.getBillingYear());

        return response;
    }

    public List<PaymentResponseDTO> history(int userId) {

        List<Invoice> invoices =
                invoiceRepo.findByUser(userId);

        return invoices.stream().map(inv -> {

            PaymentResponseDTO dto = new PaymentResponseDTO();
            dto.paymentId = inv.getInvoiceId();
            dto.amount = inv.getTotalAmount();
            dto.billingMonth = inv.getBillingMonth();
            dto.billingYear = inv.getBillingYear();
            dto.status = inv.getStatus();
            dto.paymentDate = inv.getPaidDate();
            dto.paymentMode = null; // optional

            return dto;

        }).toList();
    }

    public PaymentResponseDTO payInvoice(Long invoiceId, String mode) {

        Invoice invoice = invoiceRepo.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        if ("PAID".equals(invoice.getStatus())) {
            throw new RuntimeException("Already paid");
        }

        Payment payment = new Payment();
        payment.setInvoice(invoice);
        payment.setAmount(invoice.getTotalAmount());
        payment.setPaymentMode(mode);
        payment.setStatus("SUCCESS");
        payment.setPaymentDate(LocalDate.now());

        paymentRepo.save(payment);

        invoice.setStatus("PAID");
        invoice.setPaidDate(LocalDate.now());
        invoiceRepo.save(invoice);

        String email = invoice.getSubscription()
                .getUser()
                .getEmail();

        if (email != null && !email.isBlank()) {
            emailService.sendPaymentConfirmation(email, invoice);
        }

        PaymentResponseDTO dto = new PaymentResponseDTO();
        dto.setPaymentId(payment.getPaymentId());
        dto.setAmount(payment.getAmount());
        dto.setPaymentMode(payment.getPaymentMode());
        dto.setStatus(payment.getStatus());
        dto.setPaymentDate(payment.getPaymentDate());

        return dto;
    }
}