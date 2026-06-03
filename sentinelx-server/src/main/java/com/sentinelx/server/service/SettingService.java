package com.sentinelx.server.service;

import com.sentinelx.server.domain.entity.Setting;
import com.sentinelx.server.repository.SettingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class SettingService {

    // Setting keys used throughout the application
    public static final String KEY_TELEGRAM_BOT_TOKEN = "telegram.bot.token";
    public static final String KEY_TELEGRAM_CHAT_ID = "telegram.chat.id";
    public static final String KEY_CPU_THRESHOLD = "alert.cpu.threshold";
    public static final String KEY_RAM_THRESHOLD = "alert.ram.threshold";
    public static final String KEY_AGENT_TIMEOUT_SECONDS = "agent.timeout.seconds";

    private final SettingRepository settingRepository;

    public SettingService(SettingRepository settingRepository) {
        this.settingRepository = settingRepository;
    }

    @Transactional(readOnly = true)
    public Optional<String> getValue(String key) {
        return settingRepository.findByKey(key).map(Setting::getValue);
    }

    @Transactional(readOnly = true)
    public String getValue(String key, String defaultValue) {
        return getValue(key).orElse(defaultValue);
    }

    @Transactional(readOnly = true)
    public double getDoubleValue(String key, double defaultValue) {
        return getValue(key).map(v -> {
            try { return Double.parseDouble(v); }
            catch (NumberFormatException e) { return defaultValue; }
        }).orElse(defaultValue);
    }

    @Transactional(readOnly = true)
    public long getLongValue(String key, long defaultValue) {
        return getValue(key).map(v -> {
            try { return Long.parseLong(v); }
            catch (NumberFormatException e) { return defaultValue; }
        }).orElse(defaultValue);
    }

    @Transactional
    public void setValue(String key, String value) {
        Setting setting = settingRepository.findByKey(key).orElseGet(() -> {
            Setting s = new Setting();
            s.setKey(key);
            return s;
        });
        setting.setValue(value);
        settingRepository.save(setting);
    }

    @Transactional(readOnly = true)
    public java.util.List<Setting> findAll() {
        return settingRepository.findAll();
    }
}
