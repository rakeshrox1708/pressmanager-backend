package com.newspaper.System.repository;

import com.newspaper.System.model.Area;
import com.newspaper.System.model.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VendorRepository extends JpaRepository<Vendor, Integer> {

    @Query("SELECT v FROM Vendor v JOIN v.areas a WHERE a.areaId = :areaId")
    Vendor findVendorByArea(@Param("areaId") int areaId);

    Vendor findByPhone(String phone);

    List<Vendor> findByAreasContaining(Area area);
}

