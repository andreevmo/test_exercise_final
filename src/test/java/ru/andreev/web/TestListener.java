package ru.andreev.web;

import com.codeborne.selenide.Selenide;
import io.qameta.allure.Attachment;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.OutputType;

public class TestListener implements AfterTestExecutionCallback {

    @Override
    public void afterTestExecution(ExtensionContext extensionContext) {
        takeScreenshot();
    }

    @Attachment(value = "Screenshot", type = "image/png")
    byte[] takeScreenshot() {
        return Selenide.screenshot(OutputType.BYTES);
    }
}
