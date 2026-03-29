package com.newspaper.System.controller;

import com.newspaper.System.dto.MonthlySummaryDTO;
import com.newspaper.System.dto.request.SubscribeRequestDTO;
import com.newspaper.System.dto.response.DeliveryResponseDTO;
import com.newspaper.System.dto.response.NewspaperResponseDTO;
import com.newspaper.System.dto.response.SubscriptionResponseDTO;
import com.newspaper.System.model.Subscription;
import com.newspaper.System.model.User;
import com.newspaper.System.repository.DeliveryLogRepository;
import com.newspaper.System.repository.NewspaperRepository;
import com.newspaper.System.repository.SubscriptionRepository;
import com.newspaper.System.repository.UserRepository;
import com.newspaper.System.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
//@PreAuthorize("hasRole('ROLE_USER')")
public class UserController {

    @Autowired
    private NewspaperRepository newspaperRepo;

    @Autowired
    private SubscriptionRepository subscriptionRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private DeliveryLogRepository deliveryRepo;

    @Autowired
    private UserService userService;

    @GetMapping("/newspapers")
    public List<NewspaperResponseDTO> getAllNewspapers() {
        return userService.getAllNewspapers();
    }

    @PostMapping("/subscribe")
    public SubscriptionResponseDTO subscribe(
            Authentication auth,
            @RequestBody SubscribeRequestDTO dto) {

        return userService.subscribe(
                Integer.parseInt(auth.getName()),
                dto.getNewspaperId(),
                dto.getBillingType(),
                dto.getPaymentMode()
        );
    }


    // View subscriptions
    @GetMapping("/subscriptions/{userId}")
    public List<Subscription> getSubscriptions(@PathVariable int userId) {
        User user = userRepo.findById(userId).orElseThrow();
        return subscriptionRepo.findByUser(user);
    }

    @GetMapping("/subscriptions")
    public List<SubscriptionResponseDTO> getSubscriptions(
            Authentication auth) {

        return userService.getSubscriptions(
                Integer.parseInt(auth.getName())
        );
    }

    @PutMapping("/pause/{subscriptionId}")
    public SubscriptionResponseDTO pause(
            @PathVariable int subscriptionId) {

        return userService.pauseSubscription(subscriptionId);
    }

    @PutMapping("/resume/{subscriptionId}")
    public SubscriptionResponseDTO resume(
            @PathVariable int subscriptionId) {

        return userService.resumeSubscription(subscriptionId);
    }

    @GetMapping("/delivery-history")
    public List<DeliveryResponseDTO> deliveryHistory(
            Authentication auth) {

        return userService.deliveryHistory(
                Integer.parseInt(auth.getName())
        );
    }

    @GetMapping("/monthly-summary")
    public MonthlySummaryDTO monthlySummary(Authentication auth) {

        return userService.getMonthlySummary(
                Integer.parseInt(auth.getName())
        );
    }
}
