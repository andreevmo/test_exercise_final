package ru.andreev.config;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;

import java.util.HashMap;
import java.util.Map;

import static com.codeborne.selenide.Configuration.*;
import static java.lang.System.getenv;

public class SelenideConfig {

    public static void setup() {
        if (getenv("IS_SELENOID") != null) {
            remote = "http://localhost:4444/wd/hub";
            Map<String, Boolean> options = new HashMap<>();
            options.put("enableVNC", true);
            options.put("enableVideo", Boolean.valueOf(getenv("IS_VIDEO")));
            options.put("enableLog", true);
            browserCapabilities.setCapability("selenoid:options", options);
        }
        browser = "chrome";
        browserSize = "1920x1080";
        timeout = 30 * 1000;
        pageLoadTimeout = 60 * 1000;
        downloadsFolder = "src/test/resources/downloads";
        fastSetValue = true;
        pageLoadStrategy = "eager";
        SelenideLogger.addListener("AllureSelenide",
                new AllureSelenide().screenshots(true).savePageSource(false));
    }
}
