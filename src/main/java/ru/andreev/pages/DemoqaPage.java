package ru.andreev.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import io.qameta.allure.Step;
import org.assertj.core.api.SoftAssertions;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static com.codeborne.selenide.Selenide.*;
import static java.lang.String.format;
import static java.time.Duration.ofSeconds;
import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static ru.andreev.pages.BasePage.INPUT_BY_ID;
import static ru.andreev.pages.elements.TableElements.ROW_BY_LABEL_VALUE;

public class DemoqaPage {

    private final String address_demoqa = "https://demoqa.com/automation-practice-form";
    private final String gender = "//input[@value='%s']/../label";
    private final String hobbySport = "//input[@id='hobbies-checkbox-1']/../label";
    private final String hobbyReading = "//input[@id='hobbies-checkbox-2']/../label";
    private final String currentAddress = "//textarea[@id='currentAddress']";
    private final String state = "//div[@id='state']//input";
    private final String city = "//div[@id='city']//input";
    private final String submit = "//button[@id='submit']";
    private final String filePath = System.getProperty("user.dir") + "/src/test/resources/demoqa/";
    private final String modalTitle = "//div[text()='Thanks for submitting the form']";
    private final Logger logger = Logger.getLogger("logger");
    private SoftAssertions sa;

    private final Map<String, String> dataStudent = Map.ofEntries(
            entry("Student Name", "Maxim Andreev"),
            entry("Student Email", "maxim_525@mail.ru"),
            entry("Mobile", "9518888479"),
            entry("Date of Birth", "27 December,1994"),
            entry("Subjects", "Economics"),
            entry("Gender", "Male"),
            entry("Hobbies", "Sports, Reading"),
            entry("Address", "Saratov, Romantikov, 44, 175"),
            entry("State and City", "NCR Delhi"),
            entry("Picture", "testFile")
    );

    @Step("Открыть страницу по адресу '" + address_demoqa + "'")
    public DemoqaPage open() {
        Selenide.open(address_demoqa);
        return this;
    }

    private final List<String> requiredFields = List.of("Student Name", "Mobile", "Date of Birth", "Gender");

    @Step("Заполнить все поля формы")
    public DemoqaPage writeAllFields(boolean isCorrectEmail) {
        String[] fullName = dataStudent.get("Student Name").split(" ");
        $(By.xpath(format(INPUT_BY_ID, "firstName"))).setValue(fullName[0]);
        $(By.xpath(format(INPUT_BY_ID, "lastName"))).setValue(fullName[1]);
        String email = isCorrectEmail ? dataStudent.get("Student Email") : "maxim_525mail.ru";
        $(By.xpath(format(INPUT_BY_ID, "userEmail"))).setValue(email);
        $(By.xpath(format(INPUT_BY_ID, "userNumber"))).setValue(dataStudent.get("Mobile"));
        $(By.xpath(format(INPUT_BY_ID, "dateOfBirthInput"))).press(Keys.CONTROL, "a");
        $(By.xpath(format(INPUT_BY_ID, "dateOfBirthInput"))).sendKeys(dataStudent.get("Date of Birth"));
        $(By.xpath(format(INPUT_BY_ID, "dateOfBirthInput"))).pressEnter();
        $(By.xpath(format(INPUT_BY_ID, "subjectsInput"))).sendKeys(dataStudent.get("Subjects"));
        $(By.xpath(format(INPUT_BY_ID, "subjectsInput"))).pressEnter();
        $(By.xpath(format(gender, "Male"))).click();
        $(By.xpath(hobbySport)).click();
        $(By.xpath(hobbyReading)).click();
        $(By.xpath(format(INPUT_BY_ID, "uploadPicture")))
                .uploadFile(new File(filePath + dataStudent.get("Picture")));
        $(By.xpath(currentAddress)).setValue(dataStudent.get("Address"));
        String[] stateAndCity = dataStudent.get("State and City").split(" ");
        $(By.xpath(state)).sendKeys(stateAndCity[0], Keys.ENTER);
        $(By.xpath(city)).sendKeys(stateAndCity[1], Keys.ENTER);
        return this;
    }

    @Step("Заполнить только обязательные поля")
    public DemoqaPage writeRequiredFields() {
        String[] fullName = dataStudent.get("Student Name").split(" ");
        $(By.xpath(format(INPUT_BY_ID, "firstName"))).setValue(fullName[0]);
        $(By.xpath(format(INPUT_BY_ID, "lastName"))).setValue(fullName[1]);
        $(By.xpath(format(INPUT_BY_ID, "userNumber"))).setValue(dataStudent.get("Mobile"));
        $(By.xpath(format(gender, "Male"))).click();
        $(By.xpath(format(INPUT_BY_ID, "dateOfBirthInput"))).press(Keys.CONTROL, "a");
        $(By.xpath(format(INPUT_BY_ID, "dateOfBirthInput"))).sendKeys(dataStudent.get("Date of Birth"));
        $(By.xpath(format(INPUT_BY_ID, "dateOfBirthInput"))).pressEnter();
        return this;
    }

    @Step("Кликнуть на кнопку 'Submit'")
    public DemoqaPage clickSubmit() {
        $(By.xpath(submit)).click();
        return this;
    }

    @Step("Проверить, что поле 'Thanks for submitting the form' в состоянии '{0}'")
    public void checkModalTitle(Condition condition) {
        $(By.xpath(modalTitle)).should(condition, ofSeconds(10));
    }

    @Step("Проверить содержание таблицы на модальном окне")
    public void checkFieldsInModalWindow(boolean isOnlyRequired) {
        sa = new SoftAssertions();
        dataStudent.entrySet().stream()
                .filter(e -> !isOnlyRequired || requiredFields.contains(e.getKey()))
                .forEach(e -> {
                    logger.info("\nПоле '" + e.getKey() + "' должно содержать '" + e.getValue() + "'");
                    String field = $$(By.xpath(format(ROW_BY_LABEL_VALUE, e.getKey()))).get(1).getText();
                    sa.assertThat(field).isEqualTo(e.getValue());
                });
        sa.assertAll();
    }

    @Step("Проверить, что цвет рамок обязательных полей стал красным")
    public void checkColorBorderRequiredFields() {
        sleep(2000); // пауза, чтобы цвет успел отрисоваться
        sa = new SoftAssertions();
        List.of("firstName", "lastName", "userNumber").forEach(id -> {
            String colorFirstname = $(By.xpath(format(INPUT_BY_ID, id))).getCssValue("border-color");
            sa.assertThat(colorFirstname).isEqualTo("rgb(220, 53, 69)");
        });
        List.of("Male", "Female", "Other").forEach(id -> {
            String colorMale = $(By.xpath(format(gender, id))).getCssValue("color");
            sa.assertThat(colorMale).isEqualTo("rgba(220, 53, 69, 1)");
        });
        sa.assertAll();
    }

    @Step("Проверить, что цвет пооля Email стал красным")
    public void checkColorBorderEmailField() {
        sleep(2000); // пауза , чтобы цвет успел отрисоваться
        String colorFirstname = $(By.xpath(format(INPUT_BY_ID, "userEmail"))).getCssValue("border-color");
        assertThat(colorFirstname).isEqualTo("rgb(220, 53, 69)");
    }
}
