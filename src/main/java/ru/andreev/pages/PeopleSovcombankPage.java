package ru.andreev.pages;

import com.codeborne.selenide.Selenide;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import static com.codeborne.selenide.Selenide.*;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static ru.andreev.pages.BasePage.*;

public class PeopleSovcombankPage {

    private final String addressPeopleSovcombank = "https://people.sovcombank.ru/vacancies";
    private final String buttonVacancies = "//a[@href='/vacancies']/button";
    private final Logger logger = Logger.getLogger("logger");

    @Step("Открыть страницу по адресу '" + addressPeopleSovcombank + "'")
    public PeopleSovcombankPage open() {
        Selenide.open(addressPeopleSovcombank);
        return this;
    }

    @Step("Кликнуть на кнопку 'Вакансии'")
    public PeopleSovcombankPage clickToVacancies() {
        $(By.xpath(buttonVacancies)).click();
        return this;
    }

    @Step("Установить фильтр города в значение '{0}")
    public PeopleSovcombankPage setFilterCity(String filterCity) {
        $(By.xpath(format(INPUT_BY_PLACEHOLDER, "Все города"))).sendKeys(filterCity);
        $(By.xpath(format(P_BY_CONTAINS_TEXT, filterCity))).click();
        return this;
    }

    @Step("Установить фильтр города в значение '{0}")
    public PeopleSovcombankPage setFilterCompany(String filterCompany) {
        $(By.xpath(format(INPUT_BY_PLACEHOLDER, "Все компании"))).parent().click();
        $(By.xpath(format(DIV_BY_TEXT, filterCompany))).click();
        return this;
    }

    @Step("Кликнуть на кнопку 'Найти'")
    public void clickSearch() {
        $(By.xpath(format(SPAN_BY_TEXT, "Найти"))).click();
        sleep(1000); // пауза, чтобы вакансии успели обновиться
    }

    @Step("Кликать на кнопку 'Еще' пока она есть для загрузки всех вакансии")
    public void showAllVacancies() {
        if ($(By.xpath(format(DIV_BY_CONTAINS_CLASS, "load-more"))).exists()) {
            $(By.xpath(format(DIV_BY_CONTAINS_CLASS, "load-more"))).click();
        }
    }

    @Step("Получить вакансии")
    public Map<String, String[]> getVacancies() {
        Map<String, String[]> vacancies = new HashMap<>();
        int countVacancies = $$(By.xpath(format(DIV_BY_CLASS, "block-vacancy"))).size();
        for (int i = 0; i < countVacancies; i++) {
            String titleVacancies = $$(By.xpath(format(DIV_BY_CLASS, "block-vacancy"))).get(i).find("div").getText();
            String paramsVacancies = $$(By.xpath(format(DIV_BY_CLASS, "block-vacancy"))).get(i).find("p").getText();
            vacancies.put(titleVacancies, paramsVacancies.split(" • "));
        }
        return vacancies;
    }

    @Step("Проверить вакансии на соответствие заданным параметрам: \n {1}")
    public void checkVacanciesParameters(Map<String, String[]> vacancies, Map<String, VacancyParameter> fieldsToCheck) {
        Collection<String[]> paramsVacancies = vacancies.values();
        paramsVacancies.forEach(params -> fieldsToCheck.forEach((k, v) -> {
            logger.info("\nparams:" + Arrays.toString(params) + " должен содержать:\nk-" + k + "\nv-" + v);
            if (!params[v.num].equals("Вся Россия")) {
                assertThat(params[v.num]).contains(k);
            }
        }));
    }

    public enum VacancyParameter {
        PLACE(0), COMPANY(1), DIRECTION(2), EXP(3), DAY(4);

        final int num;

        VacancyParameter(int num) {
            this.num = num;
        }
    }
}
