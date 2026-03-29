package com.newspaper.System.repository;

import com.newspaper.System.model.City;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CityRepository extends JpaRepository<City, Integer> {

    List<City> findByState_StateId(int stateId);
}