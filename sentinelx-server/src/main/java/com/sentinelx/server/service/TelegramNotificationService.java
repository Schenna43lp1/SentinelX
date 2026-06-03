package com.sentinelx.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Service
public class TelegramNotificationService {

    private static final Logger log = LoggerFactory.getLogger(TelegramNotificationService.class);
    private static final String TELEGRAM_API = "https://api.telegram.org/bot%s/sendMessage";

    private final SettingService settingService;
    private final HttpClient httpClient;

    public TelegramNotificationService(SettingService settingService) {
        this.settingService = settingService;
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    }

    public void sendAlert(String title, String message) {
        String botToken = settingService.getValue(SettingService.KEY_TELEGRAM_BOT_TOKEN, "");
        String chatId = settingService.getValue(SettingService.KEY_TELEGRAM_CHAT_ID, "");

        if (botToken.isBlank() || chatId.isBlank()) {
            log.debug("Telegram not configured, skipping notification");
            return;
        }

        String text = String.format("🚨 *SentinelX Alert*%n*%s*%n%s", escapeMarkdown(title), escapeMarkdown(message));
        String json = String.format(
            "{\"chat_id\":\"%s\",\"text\":\"%s\",\"parse_mode\":\"Markdown\"}",
            chatId, text.replace("\"", "\\\"").replace("\n", "\\n")
        );

        String url = String.format(TELEGRAM_API, botToken);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .timeout(Duration.ofSeconds(15))
            .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                log.warn("Telegram API returned status {}: {}", response.statusCode(), response.body());
            } else {
                log.info("Telegram alert sent: {}", title);
            }
        } catch (Exception e) {
            log.error("Failed to send Telegram alert: {}", e.getMessage());
        }
    }

    private String escapeMarkdown(String text) {
        // Escape Telegram MarkdownV1 special chars (only * and _ are special in v1)
        return text.replace("*", "\\*").replace("_", "\\_");
    }
}
