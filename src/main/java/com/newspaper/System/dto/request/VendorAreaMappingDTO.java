package com.newspaper.System.dto.request;

import java.util.List;

public class VendorAreaMappingDTO {
    public String vendorName;
    public List<String> areas;

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public List<String> getAreas() {
        return areas;
    }

    public void setAreas(List<String> areas) {
        this.areas = areas;
    }
}
