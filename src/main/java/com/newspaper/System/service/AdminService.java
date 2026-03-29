package com.newspaper.System.service;

import com.newspaper.System.dto.request.*;
import com.newspaper.System.dto.response.*;
import com.newspaper.System.model.*;
import com.newspaper.System.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    @Autowired
    private NewspaperRepository newspaperRepo;
    @Autowired private VendorRepository vendorRepo;
    @Autowired private AreaRepository areaRepo;
    @Autowired private StateRepository stateRepo;
    @Autowired private CityRepository cityRepo;
    @Autowired private PaymentRepository paymentRepo;
    @Autowired private PasswordEncoder encoder;
    @Autowired private UserRepository userRepo;
    @Autowired private SubscriptionRepository subscriptionRepo;

    // =======================
    // NEWSPAPER
    // =======================

    public NewspaperResponseDTO addNewspaper(NewspaperRequestDTO dto) {

        Newspaper paper = new Newspaper();
        paper.setName(dto.name);
        paper.setLanguage(dto.language);
        paper.setDailyPrice(dto.dailyPrice);
        paper.setMonthlyPrice(dto.monthlyPrice);

        return mapNewspaper(newspaperRepo.save(paper));
    }

    public NewspaperResponseDTO updateNewspaper(int id, NewspaperRequestDTO dto) {

        Newspaper paper = newspaperRepo.findById(id).orElseThrow();

        paper.setName(dto.name);
        paper.setLanguage(dto.language);
        paper.setDailyPrice(dto.dailyPrice);
        paper.setMonthlyPrice(dto.monthlyPrice);

        return mapNewspaper(newspaperRepo.save(paper));
    }

    public void deleteNewspaper(int id) {
        newspaperRepo.deleteById(id);
    }

    public List<NewspaperResponseDTO> getAllNewspapers() {
        return newspaperRepo.findAll()
                .stream()
                .map(this::mapNewspaper)
                .toList();
    }

    // =======================
    // VENDOR
    // =======================

    public VendorResponseDTO addVendor(VendorRequestDTO dto) {

        Vendor vendor = new Vendor();
        vendor.setName(dto.name);
        vendor.setPhone(dto.phone);
        vendor.setPassword(encoder.encode(dto.password));

        return mapVendor(vendorRepo.save(vendor));
    }

    public List<VendorResponseDTO> getAllVendors() {
        return vendorRepo.findAll()
                .stream()
                .map(this::mapVendor)
                .toList();
    }

    public void toggleVendor(int id) {
        Vendor vendor = vendorRepo.findById(id).orElseThrow();
        vendor.setActive(!vendor.isActive());
        vendorRepo.save(vendor);
    }

    public void resetVendorPassword(int id) {
        Vendor vendor = vendorRepo.findById(id).orElseThrow();
        vendor.setPassword(encoder.encode("Vendor@123"));
        vendorRepo.save(vendor);
    }

    public List<VendorResponseDTO> getVendorsByArea(int areaId) {

        Area area = areaRepo.findById(areaId).orElseThrow();

        return vendorRepo.findByAreasContaining(area)
                .stream()
                .map(this::mapVendor)
                .toList();
    }

    // =======================
    // ASSIGN / UNASSIGN
    // =======================

    public void assignVendor(AssignVendorRequestDTO dto) {

        Vendor vendor = vendorRepo.findById(dto.vendorId).orElseThrow();
        Area area = areaRepo.findById(dto.areaId).orElseThrow();

        if (!vendor.getAreas().contains(area)) {
            vendor.getAreas().add(area);
            vendorRepo.save(vendor);
        }
    }

    public void unassignVendor(AssignVendorRequestDTO dto) {

        Vendor vendor = vendorRepo.findById(dto.vendorId).orElseThrow();
        Area area = areaRepo.findById(dto.areaId).orElseThrow();

        vendor.getAreas().remove(area);
        vendorRepo.save(vendor);
    }

    // =======================
    // STATE / CITY / AREA
    // =======================

    public State addState(State state) {
        return stateRepo.save(state);
    }

    public City addCity(CityRequestDTO dto) {

        State state = stateRepo.findById(dto.stateId).orElseThrow();

        City city = new City();
        city.setName(dto.name);
        city.setState(state);

        return cityRepo.save(city);
    }

    public AreaResponseDTO addArea(AreaRequestDTO dto) {

        City city = cityRepo.findById(dto.cityId).orElseThrow();

        Area area = new Area();
        area.setName(dto.name);
        area.setPincode(dto.pincode);
        area.setCity(city);

        return mapArea(areaRepo.save(area));
    }

    // =======================
    // REVENUE
    // =======================

    public Double getTotalRevenue() {
        Double total = paymentRepo.getTotalRevenue();
        System.out.println("TOTAL REVENUE = " + total); // debug
        return total;
    }

    // =======================
    // MAPPERS
    // =======================

    private NewspaperResponseDTO mapNewspaper(Newspaper paper) {
        NewspaperResponseDTO dto = new NewspaperResponseDTO();
        dto.newspaperId = paper.getNewspaperId();
        dto.name = paper.getName();
        dto.language = paper.getLanguage();
        dto.dailyPrice = paper.getDailyPrice();
        dto.monthlyPrice = paper.getMonthlyPrice();
        return dto;
    }

    private VendorResponseDTO mapVendor(Vendor vendor) {
        VendorResponseDTO dto = new VendorResponseDTO();
        dto.vendorId = vendor.getVendorId();
        dto.name = vendor.getName();
        dto.phone = vendor.getPhone();
        dto.active = vendor.isActive();
        dto.areas = vendor.getAreas()
                .stream()
                .map(Area::getName)
                .toList();
        return dto;
    }

    private AreaResponseDTO mapArea(Area area) {

        AreaResponseDTO dto = new AreaResponseDTO();
        dto.setAreaId(area.getAreaId());
        dto.setName(area.getName());
        dto.setPincode(area.getPincode());
        dto.setCityName(area.getCity() != null
                ? area.getCity().getName()
                : null);

        return dto;
    }

    public List<VendorAreaMappingDTO> getVendorAreaMapping() {

        return vendorRepo.findAll()
                .stream()
                .map(v -> {
                    VendorAreaMappingDTO dto = new VendorAreaMappingDTO();
                    dto.vendorName = v.getName();
                    dto.areas = v.getAreas()
                            .stream()
                            .map(Area::getName)
                            .toList();
                    return dto;
                })
                .toList();
    }

    public List<AreaResponseDTO> getVendorAreas(int vendorId) {

        Vendor vendor = vendorRepo.findById(vendorId).orElseThrow();

        return vendor.getAreas()
                .stream()
                .map(this::mapArea)
                .toList();
    }

    public List<VendorSimpleDTO> getAreaVendors(int areaId) {

        Area area = areaRepo.findById(areaId).orElseThrow();

        return area.getVendors()
                .stream()
                .map(v -> {
                    VendorSimpleDTO dto = new VendorSimpleDTO();
                    dto.vendorId = v.getVendorId();
                    dto.name = v.getName();
                    dto.phone = v.getPhone();
                    dto.active = v.isActive();
                    return dto;
                })
                .toList();
    }

    public List<AreaResponseDTO> getAllAreas() {

        return areaRepo.findAll()
                .stream()
                .map(this::mapArea)
                .toList();
    }

    public List<SubscriptionAdminDTO> getUserSubscriptions(int userId) {

        User user = userRepo.findById(userId).orElseThrow();

        return subscriptionRepo.findByUser(user)
                .stream()
                .map(sub -> {

                    SubscriptionAdminDTO dto = new SubscriptionAdminDTO();
                    dto.subscriptionId = sub.getSubscriptionId();
                    dto.userName = sub.getUser().getName();
                    dto.newspaperName = sub.getNewspaper().getName();
                    dto.billingType = sub.getBillingType();
                    dto.status = sub.getStatus();
                    dto.startDate = sub.getStartDate().toString();
                    dto.endDate = sub.getEndDate() != null ?
                            sub.getEndDate().toString() : null;

                    return dto;
                })
                .toList();
    }

    public List<PaymentAdminDTO> getUserPayments(int userId) {

        return paymentRepo
                .findByInvoice_Subscription_User_UserId(userId)
                .stream()
                .map(this::mapPayment)
                .toList();
    }
    public List<PaymentAdminDTO> getPaymentsByMonth(int month, int year) {

        return paymentRepo
                .findByInvoice_BillingMonthAndInvoice_BillingYear(month, year)
                .stream()
                .map(this::mapPayment)
                .toList();
    }

    private PaymentAdminDTO mapPayment(Payment payment) {

        PaymentAdminDTO dto = new PaymentAdminDTO();

        dto.paymentId = payment.getPaymentId();

        // 🔥 Get user via invoice → subscription → user
        dto.userName = payment.getInvoice()
                .getSubscription()
                .getUser()
                .getName();

        // 🔥 Get newspaper via invoice → subscription
        dto.newspaperName = payment.getInvoice()
                .getSubscription()
                .getNewspaper()
                .getName();

        dto.amount = payment.getAmount();
        dto.paymentMode = payment.getPaymentMode();
        dto.status = payment.getStatus();

        // 🔥 Get billing info from invoice
        dto.billingMonth = payment.getInvoice().getBillingMonth();
        dto.billingYear = payment.getInvoice().getBillingYear();

        dto.paymentDate = payment.getPaymentDate().toString();

        return dto;
    }

}
