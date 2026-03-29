package com.newspaper.System.repository;

import com.newspaper.System.model.TokenBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenBlacklistRepository
        extends JpaRepository<TokenBlacklist, Long> {

    boolean existsByToken(String token);
}
