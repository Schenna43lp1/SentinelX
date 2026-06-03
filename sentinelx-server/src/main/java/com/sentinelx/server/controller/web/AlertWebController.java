package com.sentinelx.server.controller.web;

import com.sentinelx.server.service.AlertService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/alerts")
public class AlertWebController {

    private final AlertService alertService;

    public AlertWebController(AlertService alertService) {
        this.alertService = alertService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("alerts", alertService.findAll());
        return "alerts/list";
    }

    @PostMapping("/{id}/ack")
    @PreAuthorize("hasRole('ADMIN')")
    public String acknowledge(@PathVariable Long id,
                              Authentication auth,
                              RedirectAttributes redirectAttributes) {
        alertService.acknowledge(id, auth.getName());
        redirectAttributes.addFlashAttribute("successMessage", "Alert acknowledged.");
        return "redirect:/alerts";
    }
}
