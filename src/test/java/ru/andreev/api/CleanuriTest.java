package ru.andreev.api;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.http.HttpStatus;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;

@Owner("AndreevMO")
@Feature("Сервис 'cleanuri'")
public class CleanuriTest {

    private final RequestSpecification reqSpec = new RequestSpecBuilder()
            .setBaseUri("https://cleanuri.com/")
            .setBasePath("/api/v1/shorten")
            .setContentType(ContentType.JSON)
            .log(LogDetail.URI)
            .addFilter(new AllureRestAssured())
            .build();
    private final ResponseSpecification resSpec = new ResponseSpecBuilder()
            .log(LogDetail.BODY)
            .build();

    private SoftAssertions sa;

    @BeforeEach
    public void setup() {
        sa = new SoftAssertions();
    }


    @ParameterizedTest
    @CsvFileSource(resources = "/clean_uri/incorrect_url.csv")
    @Description("Проверить выполнение запроса с urls некорректной формы")
    public void testIncorrectUrl(String body) {
        Response response = given()
                .spec(reqSpec.body(body))
                .when()
                .post()
                .then()
                .spec(resSpec.statusCode(SC_BAD_REQUEST))
                .extract().response();

        Map<String, Object> answer = response.jsonPath().getMap("");

        sa.assertThat(answer.size()).isEqualTo(1);
        sa.assertThat(answer.get("error")).isNotNull();
        sa.assertAll();
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/clean_uri/correct_url.csv")
    @Description("Проверить выполнение запроса с urls корректной формы")
    public void testCorrectUrl(String body) {
        Response response = given()
                .spec(reqSpec.body(body))
                .when()
                .post()
                .then()
                .spec(resSpec.statusCode(HttpStatus.SC_OK))
                .extract().response();

        Map<String, Object> answer = response.jsonPath().getMap("");

        sa.assertThat(answer.size()).isEqualTo(1);
        sa.assertThat(answer.get("result_url")).isNotNull();
        sa.assertThat((String) answer.get("result_url"))
                .matches((result) -> result.startsWith("https://cleanuri.com/"));
        sa.assertAll();
    }
}
