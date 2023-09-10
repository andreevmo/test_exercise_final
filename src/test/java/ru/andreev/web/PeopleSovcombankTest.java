package ru.andreev.web;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.andreev.pages.PeopleSovcombankPage;
import ru.andreev.pages.PeopleSovcombankPage.VacancyParameter;

import java.util.Map;

import static ru.andreev.pages.PeopleSovcombankPage.VacancyParameter.COMPANY;
import static ru.andreev.pages.PeopleSovcombankPage.VacancyParameter.PLACE;

@Owner("AndreevMO")
@Feature("Сервис 'people.sovcombank'")
public class PeopleSovcombankTest extends BaseTest{

    private PeopleSovcombankPage peopleSovcombankPage;

    @BeforeEach
    void setup() {
        peopleSovcombankPage = new PeopleSovcombankPage();
    }

    @Test
    @Description("Проверить работу фильтра вакансий с параметрами 'Москва', 'Совкомбанк Технологии'")
    void testFilterVacancies() {
        peopleSovcombankPage
                .open()
                .clickToVacancies()
                .setFilterCity("Москва")
                .setFilterCompany("Совкомбанк Технологии")
                .clickSearch();

        peopleSovcombankPage.showAllVacancies();

        Map<String, String[]> vacancies = peopleSovcombankPage.getVacancies();
        Map<String, VacancyParameter> vacancyParameters = Map.of(
                "Москва", PLACE,
                "Совкомбанк Технологии", COMPANY
        );

        peopleSovcombankPage.checkVacanciesParameters(vacancies, vacancyParameters);
    }
}
