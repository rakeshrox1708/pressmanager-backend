package com.newspaper.System.controller;

import com.newspaper.System.dto.request.*;
import com.newspaper.System.dto.response.*;
import com.newspaper.System.model.*;
import com.newspaper.System.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
//@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {

    @Autowired
    private AdminService adminService;

    // Newspaper
    @PostMapping("/newspaper")
    public NewspaperResponseDTO add(@RequestBody NewspaperRequestDTO dto) {
        return adminService.addNewspaper(dto);
    }

    @PutMapping("/newspaper/{id}")
    public NewspaperResponseDTO update(@PathVariable int id,
                                       @RequestBody NewspaperRequestDTO dto) {
        return adminService.updateNewspaper(id, dto);
    }

    @DeleteMapping("/newspaper/{id}")
    public void delete(@PathVariable int id) {
        adminService.deleteNewspaper(id);
    }

    // Vendor
    @PostMapping("/vendor")
    public VendorResponseDTO addVendor(@RequestBody VendorRequestDTO dto) {
        return adminService.addVendor(dto);
    }

    @GetMapping("/vendors")
    public List<VendorResponseDTO> vendors() {
        return adminService.getAllVendors();
    }

    @PutMapping("/vendor/toggle/{id}")
    public void toggle(@PathVariable int id) {
        adminService.toggleVendor(id);
    }

    @PutMapping("/vendor/reset/{id}")
    public void reset(@PathVariable int id) {
        adminService.resetVendorPassword(id);
    }

    @GetMapping("/userSubscriptions/{userId}")
    public List<SubscriptionAdminDTO> getUserSubscriptions(@PathVariable int userId) {
        return adminService.getUserSubscriptions(userId);
    }

    @GetMapping("/userPayments/{userId}")
    public List<PaymentAdminDTO> getUserPayments(@PathVariable int userId) {
        return adminService.getUserPayments(userId);
    }

    @GetMapping("/paymentsByMonth")
    public List<PaymentAdminDTO> getByMonth(@RequestParam int month,
                                            @RequestParam int year) {
        return adminService.getPaymentsByMonth(month, year);
    }

    // Revenue
    @GetMapping("/totalRevenue")
    public Double revenue() {
        return adminService.getTotalRevenue();
    }

    @GetMapping("/vendorsByArea/{areaId}")
    public List<VendorResponseDTO> vendorsByArea(@PathVariable int areaId) {
        return adminService.getVendorsByArea(areaId);
    }

    @GetMapping("/newspapers")
    public List<NewspaperResponseDTO> list() {
        return adminService.getAllNewspapers();
    }

    @PostMapping("/assign")
    public void assign(@RequestBody AssignVendorRequestDTO dto) {
        adminService.assignVendor(dto);
    }

    @DeleteMapping("/unassign")
    public void unassign(@RequestBody AssignVendorRequestDTO dto) {
        adminService.unassignVendor(dto);
    }

    @GetMapping("/vendorAreas/{vendorId}")
    public List<AreaResponseDTO> getVendorAreas(@PathVariable int vendorId) {
        return adminService.getVendorAreas(vendorId);
    }

    @GetMapping("/areaVendors/{areaId}")
    public List<VendorSimpleDTO> getAreaVendors(@PathVariable int areaId) {
        return adminService.getAreaVendors(areaId);
    }

    @GetMapping("/areas")
    public List<AreaResponseDTO> getAllAreas() {
        return adminService.getAllAreas();
    }

    @GetMapping("/vendor-areas")
    public List<VendorAreaMappingDTO> vendorAreaMapping() {
        return adminService.getVendorAreaMapping();
    }

    // State / City / Area
    @PostMapping("/state")
    public State addState(@RequestBody State state) {
        return adminService.addState(state);
    }

    @PostMapping("/city")
    public City addCity(@RequestBody CityRequestDTO dto) {
        return adminService.addCity(dto);
    }

    @PostMapping("/area")
    public AreaResponseDTO addArea(@RequestBody AreaRequestDTO dto) {
        return adminService.addArea(dto);
    }

}

