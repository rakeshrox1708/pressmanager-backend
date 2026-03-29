package com.newspaper.System.repository;

import com.newspaper.System.model.Payment;
import com.newspaper.System.model.Subscription;
import com.newspaper.System.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    List<Payment> findByInvoice_Subscription_User_UserId(int userId);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = 'SUCCESS'")
    Double getTotalRevenue();

    boolean existsByInvoice_Subscription_SubscriptionIdAndInvoice_BillingMonthAndInvoice_BillingYear(
            int subscriptionId,
            int billingMonth,
            int billingYear
    );
    List<Payment> findByInvoice_BillingMonthAndInvoice_BillingYear(int month, int year);
}

