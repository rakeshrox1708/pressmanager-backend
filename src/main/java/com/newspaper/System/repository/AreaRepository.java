package com.newspaper.System.repository;

import com.newspaper.System.model.Area;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AreaRepository extends JpaRepository<Area, Integer> {

    List<Area> findByCity_CityId(int cityId);
}
