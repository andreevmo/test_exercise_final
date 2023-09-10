package ru.andreev.web;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import ru.andreev.config.SelenideConfig;

import static com.codeborne.selenide.WebDriverRunner.closeWebDriver;
import static com.codeborne.selenide.WebDriverRunner.hasWebDriverStarted;

public class BaseTest {

    @BeforeEach
    void beforeEach() {
        SelenideConfig.setup();
    }

    @AfterEach
    void afterEach() {
        if (hasWebDriverStarted()) {
            closeWebDriver();
        }
    }
}
