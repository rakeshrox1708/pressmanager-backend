package com.newspaper.System.controller;

import com.newspaper.System.dto.response.PaymentResponseDTO;
import com.newspaper.System.model.Invoice;
import com.newspaper.System.repository.InvoiceRepository;
import com.newspaper.System.service.InvoiceService;
import com.newspaper.System.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    @Autowired
    private InvoiceRepository invoiceRepo;

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/unpaid")
    public List<Invoice> getUnpaid(Authentication auth) {

        int userId = Integer.parseInt(auth.getName());

        return invoiceRepo.findByUserAndStatus(
                userId, "UNPAID");
    }

    @GetMapping("/generate-test")
    public String testGenerate() {
        invoiceService.generateInvoicesForPreviousMonth();
        return "Invoices generated manually";
    }

    @PostMapping("/pay/{invoiceId}")
    public PaymentResponseDTO pay(
            @PathVariable Long invoiceId,
            @RequestParam String mode) {

        return paymentService.payInvoice(invoiceId, mode);
    }
}
