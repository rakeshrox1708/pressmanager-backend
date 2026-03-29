package com.newspaper.System.service;

import com.newspaper.System.model.Subscription;
import com.newspaper.System.model.User;
import com.newspaper.System.repository.DeliveryLogRepository;
import com.newspaper.System.repository.InvoiceRepository;
import com.newspaper.System.repository.PaymentRepository;
import com.newspaper.System.repository.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BillingService {

    @Autowired
    private SubscriptionRepository subscriptionRepo;

    @Autowired
    private DeliveryLogRepository deliveryRepo;

    @Autowired
    private InvoiceRepository invoiceRepo;

    public double calculateMonthlyBill(User user) {

        LocalDate now = LocalDate.now();
        LocalDate start = now.withDayOfMonth(1);
        LocalDate end = now.withDayOfMonth(now.lengthOfMonth());

        double total = 0;

        List<Subscription> subs =
                subscriptionRepo.findByUserAndStatus(user, "ACTIVE");

        for (Subscription sub : subs) {

            // 🔥 Check payment per subscription
            boolean alreadyPaid =
                    invoiceRepo.existsBySubscription_SubscriptionIdAndBillingMonthAndBillingYearAndStatus(
                            sub.getSubscriptionId(),
                            now.getMonthValue(),
                            now.getYear(),
                            "PAID"
                    );

            if (alreadyPaid) {
                continue; // Skip this subscription only
            }

            // DAILY (Postpaid)
            if ("DAILY".equalsIgnoreCase(sub.getBillingType())) {

                int deliveredDays =
                        deliveryRepo.countBySubscriptionAndStatusAndDeliveryDateBetween(
                                sub,
                                "DELIVERED",
                                start,
                                end
                        );

                total += deliveredDays * sub.getNewspaper().getDailyPrice();
            }

            // MONTHLY (Prepaid)
            else if ("MONTHLY".equalsIgnoreCase(sub.getBillingType())) {

                total += sub.getNewspaper().getMonthlyPrice();
            }
        }

        return total;
    }

}

