package org.ats.controller;
import jakarta.validation.Valid;
import org.ats.config.AppConstants;
import org.ats.dto.car.response.CarDTO;
import org.ats.dto.car.response.CarResponse;
import org.ats.dto.carRental.request.CarRentalRequest;
import org.ats.dto.carRental.response.CarRentalReport;
import org.ats.dto.customer.request.ProfileUpdateRequest;
import org.ats.dto.customer.response.CustomerProfileResponse;
import org.ats.dto.review.request.ReviewRequest;
import org.ats.entities.user.Account;
import org.ats.services.car.CarRentalService;
import org.ats.services.car.CarService;
import org.ats.services.car.ReviewService;
import org.ats.services.customer.CustomerService;
import org.ats.utils.AuthUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
@RequestMapping("/v1/customer")
public class CustomerController {
    private final CarService carService;
    private final AuthUtils authUtils;
    private final CustomerService customerService;
    private final CarRentalService rentalService;
    private final ReviewService reviewService;

    public CustomerController(CarService carService, AuthUtils authUtils,
                              CustomerService customerService, CarRentalService rentalService,
                              ReviewService reviewService) {
        this.carService = carService;
        this.authUtils = authUtils;
        this.customerService = customerService;
        this.rentalService = rentalService;
        this.reviewService = reviewService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model,
                            @RequestParam(name = "pageNumber",defaultValue = AppConstants.PAGE_NUMBER,required = false) Integer pageNumber,
                            @RequestParam(name = "pageSize",defaultValue = AppConstants.PAGE_SIZE,required = false) Integer pageSize,
                            @RequestParam(name = "sortBy",defaultValue = AppConstants.SORT_CAR_BY,required = false) String sortBy,
                            @RequestParam(name = "sortOrder",defaultValue = AppConstants.SORT_DIR,required = false) String sortOrder) {
        model.addAttribute("account", authUtils.loggedInAccount());
        model.addAttribute("carResponse", carService.getAllCarInfo(pageNumber, pageSize, sortBy, sortOrder));
        return "customer/dashboard";
    }

    @GetMapping("/car/list")
    public String cars(Model model,
                       @RequestParam(name = "pageNumber",defaultValue = AppConstants.PAGE_NUMBER,required = false) Integer pageNumber,
                       @RequestParam(name = "pageSize",defaultValue = AppConstants.PAGE_SIZE,required = false) Integer pageSize,
                       @RequestParam(name = "sortBy",defaultValue = AppConstants.SORT_CAR_BY,required = false) String sortBy,
                       @RequestParam(name = "sortOrder",defaultValue = AppConstants.SORT_DIR,required = false) String sortOrder) {
        Account account = authUtils.loggedInAccount();
        CarResponse response = carService.getAllCarInfo(pageNumber, pageSize, sortBy, sortOrder);
        List<Integer> carIds = response.getContent().stream().map(CarDTO::getCarId).toList();
        model.addAttribute("account", account);
        model.addAttribute("carResponse", response);
        model.addAttribute("carRentalStatuses", rentalService.getRentalStatuses(
                account.getCustomer().getCustomerId(), carIds));
        return "customer/car_info_list";
    }

    @PostMapping ("/car/rent")
    public String rentCar(@Valid @ModelAttribute("carRentalRequest") CarRentalRequest request,
                          BindingResult result, RedirectAttributes redirect) {
        Account account = authUtils.loggedInAccount();
        request.setCustomerId(account.getCustomer().getCustomerId());
        if (result.hasErrors()) {
            redirect.addFlashAttribute("error", "Thông tin thuê xe không hợp lệ.");
            return "redirect:/v1/customer/car/list";
        }
        if (!rentalService.rentCar(request)) {
            redirect.addFlashAttribute("error", "Không thể thuê xe: hãy kiểm tra ngày thuê và tình trạng xe.");
            return "redirect:/v1/customer/car/list";
        }
        redirect.addFlashAttribute("success", "Đã tạo yêu cầu thuê xe. Vui lòng chờ quản trị viên xác nhận.");
        return "redirect:/v1/customer/rentals/history";
    }

    @GetMapping("/rentals/history")
    public String history(Model model,
                          @RequestParam(name = "pageNumber",defaultValue = AppConstants.PAGE_NUMBER,required = false) Integer pageNumber,
                          @RequestParam(name = "pageSize",defaultValue = AppConstants.PAGE_SIZE,required = false) Integer pageSize) {
        Account account = authUtils.loggedInAccount();
        Integer customerId = account.getCustomer().getCustomerId();
        CarRentalReport report = rentalService.getCustomerHistory(customerId, pageNumber, pageSize);
        List<Integer> rentalIds = report.getContent().stream().map(rental -> rental.getCarRentalId()).toList();
        model.addAttribute("account", account);
        model.addAttribute("carRentalReport", report);
        model.addAttribute("reviewsByRental", reviewService.getReviewsByRentalIds(customerId, rentalIds));
        return "customer/rental/history";
    }

    @GetMapping("/reviews")
    public String reviews(Model model,
                          @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER) Integer pageNumber,
                          @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE) Integer pageSize) {
        Account account = authUtils.loggedInAccount();
        model.addAttribute("account", account);
        model.addAttribute("reviewPage", reviewService.getCustomerReviews(
                account.getCustomer().getCustomerId(), pageNumber, pageSize));
        return "customer/review/list";
    }

    @GetMapping("/reviews/create")
    public String createReviewForm(@RequestParam Integer rentalId, Model model) {
        Account account = authUtils.loggedInAccount();
        model.addAttribute("account", account);
        model.addAttribute("reviewRequest", reviewService.prepareCreateForm(
                account.getCustomer().getCustomerId(), rentalId));
        model.addAttribute("editMode", false);
        return "customer/review/form";
    }

    @PostMapping("/reviews/create")
    public String createReview(@Valid @ModelAttribute("reviewRequest") ReviewRequest request,
                               BindingResult result, Model model, RedirectAttributes redirect) {
        Account account = authUtils.loggedInAccount();
        if (result.hasErrors()) {
            model.addAttribute("account", account);
            model.addAttribute("editMode", false);
            return "customer/review/form";
        }
        try {
            reviewService.createReview(account.getCustomer().getCustomerId(), request);
            redirect.addFlashAttribute("success", "Đã thêm đánh giá thành công.");
            return "redirect:/v1/customer/reviews";
        } catch (IllegalArgumentException exception) {
            model.addAttribute("account", account);
            model.addAttribute("editMode", false);
            model.addAttribute("error", exception.getMessage());
            return "customer/review/form";
        }
    }

    @GetMapping("/reviews/edit")
    public String editReviewForm(@RequestParam Integer id, Model model) {
        Account account = authUtils.loggedInAccount();
        model.addAttribute("account", account);
        model.addAttribute("reviewRequest", reviewService.getReviewForm(
                account.getCustomer().getCustomerId(), id));
        model.addAttribute("editMode", true);
        return "customer/review/form";
    }

    @PostMapping("/reviews/edit")
    public String editReview(@RequestParam Integer id,
                             @Valid @ModelAttribute("reviewRequest") ReviewRequest request,
                             BindingResult result, Model model, RedirectAttributes redirect) {
        Account account = authUtils.loggedInAccount();
        if (result.hasErrors()) {
            model.addAttribute("account", account);
            model.addAttribute("editMode", true);
            return "customer/review/form";
        }
        try {
            reviewService.updateReview(account.getCustomer().getCustomerId(), id, request);
            redirect.addFlashAttribute("success", "Đã cập nhật đánh giá.");
            return "redirect:/v1/customer/reviews";
        } catch (IllegalArgumentException exception) {
            model.addAttribute("account", account);
            model.addAttribute("editMode", true);
            model.addAttribute("error", exception.getMessage());
            return "customer/review/form";
        }
    }

    @PostMapping("/reviews/delete")
    public String deleteReview(@RequestParam Integer id, RedirectAttributes redirect) {
        Account account = authUtils.loggedInAccount();
        reviewService.deleteReview(account.getCustomer().getCustomerId(), id);
        redirect.addFlashAttribute("success", "Đã xóa đánh giá.");
        return "redirect:/v1/customer/reviews";
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        Account account = authUtils.loggedInAccount();
        CustomerProfileResponse profile = customerService.getOwnProfile(account);
        model.addAttribute("account", account);
        model.addAttribute("ownProfile", profile);
        model.addAttribute("profileUpdateRequest", new ProfileUpdateRequest(profile.getCustomerId(),
                profile.getCustomerName(), profile.getMobile(), profile.getBirthday(), profile.getIdentityCard(),
                profile.getLicenceNumber(), profile.getLicenceDate()));
        return "customer/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@Valid @ModelAttribute("profileUpdateRequest") ProfileUpdateRequest request,
                                BindingResult result, Model model, RedirectAttributes redirect) {
        Account account = authUtils.loggedInAccount();
        CustomerProfileResponse own = customerService.getOwnProfile(account);
        request.setCustomerId(own.getCustomerId());
        if (result.hasErrors()) {
            model.addAttribute("account", account);
            model.addAttribute("ownProfile", own);
            model.addAttribute("openEditModal", true);
            return "customer/profile";
        }
        if (customerService.updateCustomerProfile(request)) {
            redirect.addFlashAttribute("success", "Cập nhật hồ sơ thành công.");
        } else {
            redirect.addFlashAttribute("error", "CCCD hoặc giấy phép lái xe đã được sử dụng.");
        }
        return "redirect:/v1/customer/profile";
    }
}
