package org.ats.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice(basePackages = "org.ats.controller")
public class MyGlobalException {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String ResourceNotFoundException (ResourceNotFoundException e, Model model){
        model.addAttribute("message", e.getMessage());
        return "error/404";
    }

    @ExceptionHandler(Exception.class)
    public String handleWebException(Exception ex ,Model model){
        ex.printStackTrace();

        String friendlyMessage = ex.getMessage() != null ? ex.getMessage() : "Lỗi hệ thống nội bộ";
        model.addAttribute("error","Hệ thống có lỗi xảy ra: " + friendlyMessage);

        return "error/error-page";
    }
}
