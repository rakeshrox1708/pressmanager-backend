package com.newspaper.System.repository;

import com.newspaper.System.model.Newspaper;
import com.newspaper.System.model.Subscription;
import com.newspaper.System.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, Integer> {
    List<Subscription> findByUser(User user);

    @Query("""
SELECT s FROM Subscription s
WHERE s.user.area.areaId = :areaId
AND s.status = 'ACTIVE'
""")
    List<Subscription> findActiveSubscriptionsByArea(@Param("areaId") int areaId);

    List<Subscription> findByUserAndStatus(User user, String status);

    List<Subscription> findByStatus(String status);

    boolean existsByUserAndNewspaperAndStatus(
            User user,
            Newspaper newspaper,
            String status
    );

}
