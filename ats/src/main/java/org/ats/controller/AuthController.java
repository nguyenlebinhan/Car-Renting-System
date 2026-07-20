package org.ats.controller;

import jakarta.validation.Valid;
import org.ats.security.request.LoginRequest;
import org.ats.security.request.RegisterRequest;
import org.ats.services.account.AccountService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/v1/auth")
public class AuthController {
    private final AccountService accountService;

    public AuthController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        return "auth/login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registerRequest") RegisterRequest request,
                           BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) return "auth/register";
        if (!accountService.register(request)) {
            model.addAttribute("error", "Tên tài khoản, email, CCCD hoặc số giấy phép lái xe đã được đăng ký.");
            return "auth/register";
        }
        redirect.addFlashAttribute("success", "Đăng ký thành công. Bạn có thể đăng nhập.");
        return "redirect:/v1/auth/login";
    }
}
