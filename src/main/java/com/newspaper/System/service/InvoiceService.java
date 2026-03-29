package com.newspaper.System.service;

import com.newspaper.System.model.*;
import com.newspaper.System.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class InvoiceService {

    @Autowired private SubscriptionRepository subscriptionRepo;
    @Autowired private DeliveryLogRepository deliveryRepo;
    @Autowired private InvoiceRepository invoiceRepo;
    @Autowired private EmailService emailService;

    public void generateInvoicesForPreviousMonth() {

        LocalDate now = LocalDate.now();
        LocalDate previous = now.minusMonths(1);

        int month = previous.getMonthValue();
        int year = previous.getYear();

        LocalDate startOfPrevious =
                previous.withDayOfMonth(1);

        LocalDate endOfPrevious =
                previous.withDayOfMonth(previous.lengthOfMonth());

        List<Subscription> activeSubs =
                subscriptionRepo.findByStatus("ACTIVE");

        for (Subscription sub : activeSubs) {

            // ✅ Skip if subscription started after billing month
            if (sub.getStartDate().isAfter(endOfPrevious)) {
                continue;
            }

            // ✅ Avoid duplicate invoice
            if (invoiceRepo.existsBySubscriptionAndBillingMonthAndBillingYear(
                    sub, month, year)) {
                continue;
            }

            double amount = calculateAmount(sub, startOfPrevious, endOfPrevious);

            if (amount <= 0) continue;

            Invoice invoice = new Invoice();
            invoice.setSubscription(sub);
            invoice.setBillingMonth(month);
            invoice.setBillingYear(year);
            invoice.setPeriodStart(startOfPrevious);
            invoice.setPeriodEnd(endOfPrevious);
            invoice.setBaseAmount(amount);
            invoice.setLateFee(0);
            invoice.setTotalAmount(amount);
            invoice.setStatus("UNPAID");
            invoice.setGeneratedDate(LocalDate.now());
            invoice.setDueDate(LocalDate.now().plusDays(10));

            invoiceRepo.save(invoice);

            String email = sub.getUser().getEmail();

            if (email != null && !email.isBlank()) {
                try {
                    emailService.sendInvoiceEmail(email, invoice);
                } catch (Exception e) {
                    System.out.println("Email failed: " + e.getMessage());
                }
            }
        }
    }

    private double calculateAmount(
            Subscription sub,
            LocalDate start,
            LocalDate end) {

        if ("DAILY".equalsIgnoreCase(sub.getBillingType())) {

            int delivered =
                    deliveryRepo.countBySubscriptionAndStatusAndDeliveryDateBetween(
                            sub, "DELIVERED", start, end);

            return delivered *
                    sub.getNewspaper().getDailyPrice();
        }

        if ("MONTHLY".equalsIgnoreCase(sub.getBillingType())) {
            return sub.getNewspaper().getMonthlyPrice();
        }

        return 0;
    }

    public void markOverdueInvoices() {

        List<Invoice> unpaid = invoiceRepo.findByStatus("UNPAID");

        for (Invoice inv : unpaid) {

            if (LocalDate.now().isAfter(inv.getDueDate())) {

                inv.setStatus("OVERDUE");

                double lateFee = inv.getBaseAmount() * 0.02;
                inv.setLateFee(lateFee);
                inv.setTotalAmount(inv.getBaseAmount() + lateFee);

                invoiceRepo.save(inv);
            }
        }
    }
}