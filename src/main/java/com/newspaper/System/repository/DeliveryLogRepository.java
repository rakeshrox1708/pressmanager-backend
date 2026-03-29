package com.newspaper.System.repository;

import com.newspaper.System.model.DeliveryLog;
import com.newspaper.System.model.Subscription;
import com.newspaper.System.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DeliveryLogRepository extends JpaRepository<DeliveryLog, Integer> {

    boolean existsBySubscriptionAndDeliveryDate(
            Subscription subscription,
            LocalDate deliveryDate
    );

    List<DeliveryLog> findBySubscription_User(User user);

    int countBySubscription_UserAndStatusAndDeliveryDateBetween(
            User user,
            String status,
            LocalDate startDate,
            LocalDate endDate
    );

    //To calculate the daily based bill for the newspaper per month
    int countBySubscriptionAndStatusAndDeliveryDateBetween(
            Subscription subscription,
            String status,
            LocalDate start,
            LocalDate end
    );


}
