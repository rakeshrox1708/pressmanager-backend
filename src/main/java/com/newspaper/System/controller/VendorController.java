package com.newspaper.System.controller;

import com.newspaper.System.dto.DeliveryDTO;
import com.newspaper.System.dto.response.DeliveryResponseDTO;
import com.newspaper.System.model.Area;
import com.newspaper.System.model.DeliveryLog;
import com.newspaper.System.model.Subscription;
import com.newspaper.System.model.Vendor;
import com.newspaper.System.repository.DeliveryLogRepository;
import com.newspaper.System.repository.SubscriptionRepository;
import com.newspaper.System.repository.VendorRepository;
import com.newspaper.System.service.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vendor")
//@PreAuthorize("hasRole('ROLE_VENDOR')")
public class VendorController {


    @Autowired
    private VendorService vendorService;

    @GetMapping("/deliveries")
    public List<DeliveryResponseDTO> deliveries(
            Authentication auth,
            @RequestParam(required = false) String date) {

        return vendorService.getDeliveries(
                Integer.parseInt(auth.getName()),
                date
        );
    }

    @PostMapping("/deliver")
    public void markDelivery(
            @RequestParam int subscriptionId,
            @RequestParam String status) {

        vendorService.markDelivery(subscriptionId, status);
    }

    @GetMapping("/weekly-stats")
    public Map<String, Integer> weekly(Authentication auth) {

        return vendorService.getWeeklyStats(
                Integer.parseInt(auth.getName())
        );
    }

}

