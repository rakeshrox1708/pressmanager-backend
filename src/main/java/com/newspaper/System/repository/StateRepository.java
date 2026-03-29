package com.newspaper.System.repository;

import com.newspaper.System.model.State;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StateRepository extends JpaRepository<State, Integer> {
}