package com.newspaper.System.service;

import com.newspaper.System.dto.MonthlySummaryDTO;
import com.newspaper.System.dto.response.*;
import com.newspaper.System.model.*;
import com.newspaper.System.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class UserService {

    @Autowired private UserRepository userRepo;
    @Autowired private NewspaperRepository newspaperRepo;
    @Autowired private SubscriptionRepository subscriptionRepo;
    @Autowired private PaymentRepository paymentRepo;
    @Autowired private DeliveryLogRepository deliveryRepo;
    @Autowired
    private InvoiceRepository invoiceRepo;

    // ====================================================
    // 1️⃣ Subscribe
    // ====================================================

    @Transactional
    public SubscriptionResponseDTO subscribe(int userId,
                                             int newspaperId,
                                             String billingType,
                                             String paymentMode) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Newspaper paper = newspaperRepo.findById(newspaperId)
                .orElseThrow(() -> new RuntimeException("Newspaper not found"));

        // ✅ Prevent duplicate subscription
        boolean alreadySubscribed =
                subscriptionRepo.existsByUserAndNewspaperAndStatus(
                        user,
                        paper,
                        "ACTIVE"
                );

        if (alreadySubscribed) {
            throw new RuntimeException(
                    "Already subscribed to this newspaper"
            );
        }

        Subscription sub = new Subscription();
        sub.setUser(user);
        sub.setNewspaper(paper);
        sub.setStartDate(LocalDate.now());
        sub.setStatus("ACTIVE");
        sub.setBillingType(billingType.toUpperCase());

        sub = subscriptionRepo.save(sub);

        if ("MONTHLY".equalsIgnoreCase(billingType)) {

            LocalDate now = LocalDate.now();

            Invoice invoice = new Invoice();
            invoice.setSubscription(sub);
            invoice.setBillingMonth(now.getMonthValue());
            invoice.setBillingYear(now.getYear());
            invoice.setPeriodStart(now.withDayOfMonth(1));
            invoice.setPeriodEnd(now.withDayOfMonth(now.lengthOfMonth()));
            invoice.setBaseAmount(paper.getMonthlyPrice());
            invoice.setLateFee(0);
            invoice.setTotalAmount(paper.getMonthlyPrice());
            invoice.setStatus("PAID");
            invoice.setGeneratedDate(now);
            invoice.setDueDate(now);
            invoice.setPaidDate(now);

            invoice = invoiceRepo.save(invoice);

            Payment payment = new Payment();
            payment.setInvoice(invoice);
            payment.setAmount(invoice.getTotalAmount());
            payment.setPaymentMode(paymentMode);
            payment.setStatus("SUCCESS");
            payment.setPaymentDate(now);

            paymentRepo.save(payment);
        }

        return mapSubscription(sub);
    }

    // ====================================================
    // 2️⃣ Get All Newspapers
    // ====================================================

    public List<NewspaperResponseDTO> getAllNewspapers() {

        return newspaperRepo.findAll()
                .stream()
                .map(this::mapNewspaper)
                .toList();
    }

    // ====================================================
    // 3️⃣ Get User Subscriptions
    // ====================================================

    public List<SubscriptionResponseDTO> getSubscriptions(int userId) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return subscriptionRepo.findByUser(user)
                .stream()
                .map(this::mapSubscription)
                .toList();
    }

    // ====================================================
    // 4️⃣ Delivery History
    // ====================================================

    public List<DeliveryResponseDTO> deliveryHistory(int userId) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return deliveryRepo.findBySubscription_User(user)
                .stream()
                .map(log -> {
                    DeliveryResponseDTO dto = new DeliveryResponseDTO();
                    dto.subscriptionId = log.getSubscription().getSubscriptionId();
                    dto.newspaperName = log.getSubscription().getNewspaper().getName();
                    dto.deliveryDate = log.getDeliveryDate();
                    dto.status = log.getStatus();
                    return dto;
                })
                .toList();
    }

    // ====================================================
    // 🔁 MAPPERS
    // ====================================================

    private SubscriptionResponseDTO mapSubscription(Subscription sub) {

        SubscriptionResponseDTO dto = new SubscriptionResponseDTO();
        dto.subscriptionId = sub.getSubscriptionId();
        dto.newspaperName = sub.getNewspaper().getName();
        dto.startDate = sub.getStartDate();
        dto.endDate = sub.getEndDate();
        dto.status = sub.getStatus();
        dto.billingType = sub.getBillingType();

        return dto;
    }

    private NewspaperResponseDTO mapNewspaper(Newspaper paper) {

        NewspaperResponseDTO dto = new NewspaperResponseDTO();
        dto.newspaperId = paper.getNewspaperId();
        dto.name = paper.getName();
        dto.language = paper.getLanguage();
        dto.dailyPrice = paper.getDailyPrice();
        dto.monthlyPrice = paper.getMonthlyPrice();

        return dto;
    }

    @Transactional
    public SubscriptionResponseDTO pauseSubscription(int subscriptionId) {

        Subscription sub = subscriptionRepo.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        sub.setStatus("PAUSED");

        return mapSubscription(subscriptionRepo.save(sub));
    }

    @Transactional
    public SubscriptionResponseDTO resumeSubscription(int subscriptionId) {

        Subscription sub = subscriptionRepo.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        sub.setStatus("ACTIVE");

        return mapSubscription(subscriptionRepo.save(sub));
    }

    public MonthlySummaryDTO getMonthlySummary(int userId) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        LocalDate now = LocalDate.now();

        LocalDate start = now.withDayOfMonth(1);
        LocalDate end = now.withDayOfMonth(now.lengthOfMonth());

        int delivered = deliveryRepo
                .countBySubscription_UserAndStatusAndDeliveryDateBetween(
                        user,
                        "DELIVERED",
                        start,
                        end
                );

        int totalDays = now.lengthOfMonth();

        MonthlySummaryDTO dto = new MonthlySummaryDTO();
        dto.delivered = delivered;
        dto.missed = totalDays - delivered;

        return dto;
    }
}