package com.newspaper.System.repository;

import com.newspaper.System.model.Invoice;
import com.newspaper.System.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    boolean existsBySubscriptionAndBillingMonthAndBillingYear(
            Subscription sub, int month, int year);

    boolean existsBySubscription_SubscriptionIdAndBillingMonthAndBillingYearAndStatus(
            int subscriptionId,
            int billingMonth,
            int billingYear,
            String status
    );

    List<Invoice> findByStatus(String status);

    @Query("""
       SELECT i FROM Invoice i
       WHERE i.subscription.user.userId = :userId
       AND i.status = :status
       """)
    List<Invoice> findByUserAndStatus(
            @Param("userId") int userId,
            @Param("status") String status);

    @Query("""
       SELECT i FROM Invoice i
       WHERE i.subscription.user.userId = :userId
       """)
    List<Invoice> findByUser(@Param("userId") int userId);
}