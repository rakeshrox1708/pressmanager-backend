package com.newspaper.System.controller;

import com.newspaper.System.dto.PaymentDTO;
import com.newspaper.System.dto.request.PaymentRequestDTO;
import com.newspaper.System.dto.response.BillResponseDTO;
import com.newspaper.System.dto.response.PaymentResponseDTO;
import com.newspaper.System.repository.PaymentRepository;
import com.newspaper.System.repository.UserRepository;
import com.newspaper.System.response.ApiResponse;
import com.newspaper.System.service.BillingService;
import com.newspaper.System.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/bill")
    public ApiResponse<BillResponseDTO> getBill(Authentication auth) {

        int userId = Integer.parseInt(auth.getName());

        return ApiResponse.success(
                "Bill fetched",
                paymentService.getBill(userId)
        );
    }
    @PostMapping("/pay")
    public ApiResponse<PaymentResponseDTO> pay(
            Authentication auth,
            @RequestBody PaymentRequestDTO dto) {

        int userId = Integer.parseInt(auth.getName());

        return ApiResponse.success(
                "Payment Successful",
                paymentService.makePayment(userId, dto)
        );
    }

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


}
