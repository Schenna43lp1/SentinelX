package com.sentinelx.server.controller.web;

import com.sentinelx.server.service.SettingService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/settings")
@PreAuthorize("hasRole('ADMIN')")
public class SettingsWebController {

    private final SettingService settingService;

    public SettingsWebController(SettingService settingService) {
        this.settingService = settingService;
    }

    @GetMapping
    public String settingsPage(Model model) {
        model.addAttribute("telegramBotToken",
            settingService.getValue(SettingService.KEY_TELEGRAM_BOT_TOKEN, ""));
        model.addAttribute("telegramChatId",
            settingService.getValue(SettingService.KEY_TELEGRAM_CHAT_ID, ""));
        model.addAttribute("cpuThreshold",
            settingService.getValue(SettingService.KEY_CPU_THRESHOLD, "90"));
        model.addAttribute("ramThreshold",
            settingService.getValue(SettingService.KEY_RAM_THRESHOLD, "90"));
        model.addAttribute("agentTimeoutSeconds",
            settingService.getValue(SettingService.KEY_AGENT_TIMEOUT_SECONDS, "60"));
        return "settings";
    }

    @PostMapping
    public String saveSettings(
            @RequestParam(value = "telegramBotToken", required = false, defaultValue = "") String telegramBotToken,
            @RequestParam(value = "telegramChatId", required = false, defaultValue = "") String telegramChatId,
            @RequestParam(value = "cpuThreshold", defaultValue = "90") String cpuThreshold,
            @RequestParam(value = "ramThreshold", defaultValue = "90") String ramThreshold,
            @RequestParam(value = "agentTimeoutSeconds", defaultValue = "60") String agentTimeoutSeconds,
            RedirectAttributes redirectAttributes) {

        settingService.setValue(SettingService.KEY_TELEGRAM_BOT_TOKEN, telegramBotToken);
        settingService.setValue(SettingService.KEY_TELEGRAM_CHAT_ID, telegramChatId);
        settingService.setValue(SettingService.KEY_CPU_THRESHOLD, cpuThreshold);
        settingService.setValue(SettingService.KEY_RAM_THRESHOLD, ramThreshold);
        settingService.setValue(SettingService.KEY_AGENT_TIMEOUT_SECONDS, agentTimeoutSeconds);

        redirectAttributes.addFlashAttribute("successMessage", "Settings saved successfully.");
        return "redirect:/settings";
    }
}
