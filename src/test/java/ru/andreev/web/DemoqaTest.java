package ru.andreev.web;

import com.codeborne.selenide.Condition;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.andreev.pages.DemoqaPage;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.not;

@Owner("AndreevMO")
@Feature("Сервис 'demoqa'")
public class DemoqaTest extends BaseTest {

    private DemoqaPage demoqaPage;

    @BeforeEach
    void setup() {
        demoqaPage = new DemoqaPage();
    }

    @Test
    @Description("Проверить форму со всем заполненными полями")
    void correctFieldsTest() {
        demoqaPage
                .open()
                .writeAllFields(true)
                .clickSubmit()
                .checkModalTitle(Condition.visible);

        demoqaPage.checkFieldsInModalWindow(false);
    }

    @Test
    @Description("Проверить обязательные к заполнению поля (Name: First Name, Last Name; Gender, Mobile)")
    void requiredFieldsTest() {
        demoqaPage
                .open()
                .clickSubmit()
                .checkModalTitle(not(exist));

        demoqaPage.checkColorBorderRequiredFields();
    }

    @Test
    @Description("Проверить необязательные к заполнению поля " +
            "(Email, Subjects, Hobbies, Picture, Current Address, State and City)")
    void notRequiredFields() {
        demoqaPage
                .open()
                .writeRequiredFields()
                .clickSubmit()
                .checkModalTitle(Condition.visible);

        demoqaPage.checkFieldsInModalWindow(true);
    }

    @Test
    @Description("Проверить поле email с невалидным адресом")
    void incorrectEmailTest() {
        demoqaPage
                .open()
                .writeAllFields(false)
                .clickSubmit()
                .checkModalTitle(not(exist));

        demoqaPage.checkColorBorderEmailField();
    }
}
