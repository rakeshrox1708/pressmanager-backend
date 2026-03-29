package com.newspaper.System.service;

import com.newspaper.System.dto.response.DeliveryResponseDTO;
import com.newspaper.System.model.*;
import com.newspaper.System.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class VendorService {

    @Autowired
    private VendorRepository vendorRepo;

    @Autowired
    private SubscriptionRepository subscriptionRepo;

    @Autowired
    private DeliveryLogRepository deliveryRepo;

    @Autowired
    private AreaRepository areaRepo;

    // ===============================
    // 1️⃣ Get Deliveries (Date Wise)
    // ===============================
    public List<DeliveryResponseDTO> getDeliveries(int vendorId, String date) {

        Vendor vendor = vendorRepo.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        LocalDate selectedDate =
                (date != null && !date.isBlank())
                        ? LocalDate.parse(date)
                        : LocalDate.now();

        List<DeliveryResponseDTO> result = new ArrayList<>();

        for (Area area : vendor.getAreas()) {

            List<Subscription> subscriptions =
                    subscriptionRepo.findActiveSubscriptionsByArea(area.getAreaId());

            for (Subscription sub : subscriptions) {

                boolean delivered =
                        deliveryRepo.existsBySubscriptionAndDeliveryDate(
                                sub,
                                selectedDate
                        );

                DeliveryResponseDTO dto = new DeliveryResponseDTO();
                dto.subscriptionId = sub.getSubscriptionId();
                dto.newspaperName = sub.getNewspaper().getName();
                dto.userName = sub.getUser().getName();
                dto.phone = sub.getUser().getPhone();
                dto.address = sub.getUser().getAddress();
                dto.areaName = sub.getUser().getArea().getName();
                dto.status = delivered ? "DELIVERED" : "PENDING";
                dto.deliveryDate = selectedDate;

                result.add(dto);
            }
        }

        return result;
    }

    // ===============================
    // 2️⃣ Mark Delivery
    // ===============================
    public void markDelivery(int subscriptionId, String status) {

        Subscription sub = subscriptionRepo.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        LocalDate today = LocalDate.now();

        boolean alreadyMarked =
                deliveryRepo.existsBySubscriptionAndDeliveryDate(sub, today);

        if (alreadyMarked) {
            throw new RuntimeException("Delivery already marked today");
        }

        DeliveryLog log = new DeliveryLog();
        log.setSubscription(sub);
        log.setDeliveryDate(today);
        log.setStatus(status.toUpperCase());

        deliveryRepo.save(log);
    }

    // ===============================
    // 3️⃣ Weekly Stats
    // ===============================
    public Map<String, Integer> getWeeklyStats(int vendorId) {

        Vendor vendor = vendorRepo.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        Map<String, Integer> stats = new LinkedHashMap<>();

        for (int i = 6; i >= 0; i--) {

            LocalDate date = LocalDate.now().minusDays(i);
            int deliveredCount = 0;

            for (Area area : vendor.getAreas()) {

                List<Subscription> subs =
                        subscriptionRepo.findActiveSubscriptionsByArea(area.getAreaId());

                for (Subscription sub : subs) {
                    if (deliveryRepo.existsBySubscriptionAndDeliveryDate(sub, date)) {
                        deliveredCount++;
                    }
                }
            }

            stats.put(date.toString(), deliveredCount);
        }

        return stats;
    }
}