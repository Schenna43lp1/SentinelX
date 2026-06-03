package com.sentinelx.server.controller.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice(assignableTypes = {
    NodeWebController.class,
    DashboardController.class,
    AlertWebController.class,
    SettingsWebController.class
})
public class WebExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(WebExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleNotFound(IllegalArgumentException ex,
                                 HttpServletRequest request,
                                 RedirectAttributes redirectAttributes) {
        log.warn("Web controller error at {}: {}", request.getRequestURI(), ex.getMessage());
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        return "redirect:/nodes";
    }

    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        log.warn("Access denied at {}: {}", request.getRequestURI(), ex.getMessage());
        return "redirect:/nodes";
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneral(Exception ex,
                                HttpServletRequest request,
                                RedirectAttributes redirectAttributes) {
        log.error("Unhandled error at {}", request.getRequestURI(), ex);
        redirectAttributes.addFlashAttribute("errorMessage",
            "An unexpected error occurred: " + ex.getMessage());
        return "redirect:/nodes";
    }
}
