package org.ats.controller;

import jakarta.validation.Valid;
import org.ats.config.AppConstants;
import org.ats.dto.admin.response.DashboardDTO;
import org.ats.dto.car.request.CarCreationRequest;
import org.ats.dto.car.request.CarProducerRequest;
import org.ats.dto.customer.request.UserCreationRequest;
import org.ats.services.car.CarRentalService;
import org.ats.services.car.CarProducerService;
import org.ats.services.car.CarService;
import org.ats.services.customer.CustomerService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/v1/admin")
public class AdminController {
    private final CustomerService customerService;
    private final CarService carService;
    private final CarProducerService producerService;
    private final CarRentalService rentalService;

    public AdminController(CustomerService customerService, CarService carService,
                           CarProducerService producerService, CarRentalService rentalService) {
        this.customerService = customerService;
        this.carService = carService;
        this.producerService = producerService;
        this.rentalService = rentalService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model,
                            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
                            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
                            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_CUSTOMER_BY, required = false) String sortBy,
                            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder) {
        model.addAttribute("dashboardDTO", new DashboardDTO(customerService.countAllCustomer(),
                carService.countAllCars(), rentalService.countTotalCarRental()));
        model.addAttribute("profileResponse",
                customerService.getAllInformation(pageNumber, pageSize, sortBy, sortOrder));
        return "admin/dashboard";
    }


    @GetMapping("/customer/create")
    public String createCustomerForm(Model model) {
        if (!model.containsAttribute("customerRequest")) model.addAttribute("customerRequest", new UserCreationRequest());
        model.addAttribute("editMode", false);
        return "admin/customer/form";
    }

    @PostMapping("/customer/create")
    public String createCustomer(@Valid @ModelAttribute("customerRequest") UserCreationRequest request,
                                 BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors() || !customerService.createCustomer(request)) {
            if (!result.hasErrors()) model.addAttribute("error", "Tên tài khoản, email, CCCD hoặc số giấy phép đã tồn tại.");
            model.addAttribute("editMode", false);
            return "admin/customer/form";
        }
        redirect.addFlashAttribute("success", "Đã thêm khách hàng thành công.");
        return "redirect:/v1/admin/dashboard";
    }

    @GetMapping("/customer/edit")
    public String editCustomerForm(@RequestParam(name = "id") Integer id, Model model) {
        model.addAttribute("customerRequest", customerService.getCustomerForm(id));
        model.addAttribute("editMode", true);
        return "admin/customer/form";
    }

    @PostMapping("/customer/edit")
    public String editCustomer(@RequestParam(name = "id") Integer id,
                               @Valid @ModelAttribute("customerRequest") UserCreationRequest request,
                               BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors() || !customerService.updateCustomer(id, request)) {
            if (!result.hasErrors()) model.addAttribute("error", "Dữ liệu bị trùng hoặc mật khẩu mới chưa đủ 6 ký tự.");
            model.addAttribute("editMode", true);
            return "admin/customer/form";
        }
        redirect.addFlashAttribute("success", "Đã cập nhật khách hàng.");
        return "redirect:/v1/admin/dashboard";
    }

    @PostMapping("/customer/delete")
    public String deleteCustomer(@RequestParam(name = "id") Integer id, RedirectAttributes redirect) {
        if (customerService.deleteCustomer(id)) {
            redirect.addFlashAttribute("success", "Đã xóa khách hàng.");
        } else {
            redirect.addFlashAttribute("error", "Không thể xóa khách hàng đã có lịch sử thuê xe.");
        }
        return "redirect:/v1/admin/dashboard";
    }

    @GetMapping("/producer/list")
    public String producers(Model model) {
        model.addAttribute("producers", producerService.getAllProducers());
        return "admin/producer/list";
    }

    @GetMapping("/producer/create")
    public String createProducerForm(Model model) {
        model.addAttribute("producerRequest", new CarProducerRequest());
        model.addAttribute("editMode", false);
        return "admin/producer/form";
    }

    @PostMapping("/producer/create")
    public String createProducer(@Valid @ModelAttribute("producerRequest") CarProducerRequest request,
                                 BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors() || !producerService.createProducer(request)) {
            if (!result.hasErrors()) model.addAttribute("error", "Tên hãng xe đã tồn tại.");
            model.addAttribute("editMode", false);
            return "admin/producer/form";
        }
        redirect.addFlashAttribute("success", "Đã thêm hãng xe thành công.");
        return "redirect:/v1/admin/producer/list";
    }

    @GetMapping("/producer/edit")
    public String editProducerForm(@RequestParam(name = "id") Integer id, Model model) {
        model.addAttribute("producerRequest", producerService.getProducerForm(id));
        model.addAttribute("editMode", true);
        return "admin/producer/form";
    }

    @PostMapping("/producer/edit")
    public String editProducer(@RequestParam(name = "id") Integer id,
                               @Valid @ModelAttribute("producerRequest") CarProducerRequest request,
                               BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors() || !producerService.updateProducer(id, request)) {
            if (!result.hasErrors()) model.addAttribute("error", "Tên hãng xe đã tồn tại.");
            model.addAttribute("editMode", true);
            return "admin/producer/form";
        }
        redirect.addFlashAttribute("success", "Đã cập nhật hãng xe.");
        return "redirect:/v1/admin/producer/list";
    }

    @PostMapping("/producer/delete")
    public String deleteProducer(@RequestParam(name = "id") Integer id, RedirectAttributes redirect) {
        if (producerService.deleteProducer(id)) {
            redirect.addFlashAttribute("success", "Đã xóa hãng xe.");
        } else {
            redirect.addFlashAttribute("error", "Không thể xóa hãng xe đang được sử dụng bởi xe trong hệ thống.");
        }
        return "redirect:/v1/admin/producer/list";
    }

    @GetMapping("/car/list")
    public String cars(Model model,
                       @RequestParam(name = "pageNumber",defaultValue = AppConstants.PAGE_NUMBER,required = false) Integer pageNumber,
                       @RequestParam(name = "pageSize",defaultValue = AppConstants.PAGE_SIZE,required = false) Integer pageSize,
                       @RequestParam(name = "sortBy",defaultValue = AppConstants.SORT_CAR_BY,required = false) String sortBy,
                       @RequestParam(name = "sortOrder",defaultValue = AppConstants.SORT_DIR,required = false) String sortOrder) {
        model.addAttribute("carResponse", carService.getAllCarInfo(pageNumber, pageSize, sortBy, sortOrder));
        return "admin/car/list";
    }

    @GetMapping("/car/create")
    public String createCarForm(Model model) {
        model.addAttribute("carCreationRequest", new CarCreationRequest());
        model.addAttribute("editMode", false);
        model.addAttribute("producers", carService.getAllProducers());
        model.addAttribute("carStatuses", List.of("AVAILABLE", "INACTIVE"));
        return "admin/car/form";
    }

    @PostMapping("/car/create")
    public String createCar(@Valid @ModelAttribute("carCreationRequest") CarCreationRequest request,
                            BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors() || !carService.createNewCar(request)) {
            if (!result.hasErrors()) model.addAttribute("error", "Tên xe đã tồn tại.");
            model.addAttribute("editMode", false);
            model.addAttribute("producers", carService.getAllProducers());
            model.addAttribute("carStatuses", List.of("AVAILABLE", "INACTIVE"));
            return "admin/car/form";
        }
        redirect.addFlashAttribute("success", "Đã thêm xe thành công.");
        return "redirect:/v1/admin/car/list";
    }

    @GetMapping("/car/edit")
    public String editCarForm(@RequestParam(name = "id") Integer id, Model model) {
        model.addAttribute("carCreationRequest", carService.getCarForm(id));
        model.addAttribute("editMode", true);
        model.addAttribute("producers", carService.getAllProducers());
        model.addAttribute("carStatuses", List.of("AVAILABLE", "INACTIVE"));
        return "admin/car/form";
    }

    @PostMapping("/car/edit")
    public String editCar(@RequestParam(name = "id") Integer id,
                          @Valid @ModelAttribute("carCreationRequest") CarCreationRequest request,
                          BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors() || !carService.updateCar(id, request)) {
            if (!result.hasErrors()) model.addAttribute("error", "Tên xe đã tồn tại.");
            model.addAttribute("editMode", true);
            model.addAttribute("producers", carService.getAllProducers());
            model.addAttribute("carStatuses", List.of("AVAILABLE", "INACTIVE"));
            return "admin/car/form";
        }
        redirect.addFlashAttribute("success", "Đã cập nhật xe.");
        return "redirect:/v1/admin/car/list";
    }

    @PostMapping("/car/delete")
    public String deleteCar(@RequestParam(name = "id") Integer id, RedirectAttributes redirect) {
        redirect.addFlashAttribute("success", carService.deleteOrRetireCar(id));
        return "redirect:/v1/admin/car/list";
    }

    @GetMapping("/rental/list")
    public String rentals(Model model,
                          @RequestParam(name = "pageNumber",defaultValue = AppConstants.PAGE_NUMBER,required = false) Integer pageNumber,
                          @RequestParam(name = "pageSize",defaultValue = AppConstants.PAGE_SIZE,required = false) Integer pageSize,
                          @RequestParam(name = "sortBy",defaultValue = "pickUpDate",required = false) String sortBy,
                          @RequestParam(name = "sortOrder",defaultValue = "desc",required = false) String sortOrder) {
        model.addAttribute("today", LocalDate.now());
        model.addAttribute("carRentalReport", rentalService.getAllRentals(pageNumber, pageSize, sortBy, sortOrder));
        return "admin/rental/list";
    }

    @PostMapping("/rental/status")
    public String updateRentalStatus(@RequestParam(name = "id") Integer id,
                                     @RequestParam String status,
                                     @RequestParam(required = false)
                                     @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate actualReturnDate,
                                     RedirectAttributes redirect) {
        try {
            rentalService.updateRentalStatus(id, status, actualReturnDate);
            redirect.addFlashAttribute("success", "Đã cập nhật trạng thái giao dịch.");
        } catch (IllegalArgumentException exception) {
            redirect.addFlashAttribute("error", exception.getMessage());
        }
        return "redirect:/v1/admin/rental/list";
    }

    @GetMapping("/car-rental/report")
    public String report(Model model,
                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                         @RequestParam(name = "pageNumber",defaultValue = AppConstants.PAGE_NUMBER,required = false) Integer pageNumber,
                         @RequestParam(name = "pageSize",defaultValue = AppConstants.PAGE_SIZE,required = false) Integer pageSize) {
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        if (startDate != null && endDate != null) {
            model.addAttribute("carRentalReport", rentalService.getCarRentalReport(startDate, endDate, pageNumber, pageSize));
        }
        return "admin/rental/report";
    }

}
