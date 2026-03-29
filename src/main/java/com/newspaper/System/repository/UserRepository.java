package com.newspaper.System.repository;

import com.newspaper.System.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByPhone(String phone);
}
