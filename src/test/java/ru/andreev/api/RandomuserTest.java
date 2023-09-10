package ru.andreev.api;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.filter.log.LogDetail.BODY;
import static io.restassured.filter.log.LogDetail.URI;
import static java.lang.Integer.parseInt;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.assertj.core.api.Assertions.assertThat;

@Owner("AndreevMO")
@Feature("Сервис 'randomuser'")
public class RandomuserTest {

    private final RequestSpecification reqSpec = new RequestSpecBuilder()
            .setBaseUri("https://randomuser.me")
            .setBasePath("api")
            .log(URI)
            .addFilter(new AllureRestAssured())
            .build();
    private final ResponseSpecification resSpec = new ResponseSpecBuilder()
            .expectStatusCode(SC_OK)
            .log(BODY)
            .build();

    @ParameterizedTest
    @CsvFileSource(resources = "/random_user/inc_exc.csv")
    @Description("Проверить inc с корректными query params")
    public void incWithCorrectParamsTest(String param) {
        Response response = given()
                .queryParams("inc", param)
                .spec(reqSpec)
                .when()
                .get()
                .then()
                .spec(resSpec)
                .extract().response();

        Map<String, Object> mapUser = response.jsonPath().getMap("results[0]");
        assertThat(mapUser.size()).isEqualTo(1);
        assertThat(mapUser.get(param)).isNotNull();
    }

    @Test
    @Description("Проверить inc с некорректным query param")
    public void incWithIncorrectParamTest() {
        Response response = given()
                .queryParams("inc", "someParam")
                .spec(reqSpec)
                .when()
                .get()
                .then()
                .spec(resSpec)
                .extract().response();

        Map<String, Object> mapUser = response.jsonPath().getMap("results[0]");

        assertThat(mapUser.size()).isEqualTo(0);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/random_user/inc_exc.csv")
    @Description("Проверить exc с корректными query params")
    public void excTest(String param) {
        Response response = given()
                .queryParams("exc", param)
                .spec(reqSpec)
                .when()
                .get()
                .then()
                .spec(resSpec)
                .extract().response();

        Map<String, Object> mapUser = response.jsonPath().getMap("results[0]");
        assertThat(mapUser.size()).isEqualTo(11);
        assertThat(mapUser.get(param)).isNull();
    }

    @ParameterizedTest
    @ValueSource(strings = {"male", "female"})
    @Description("Проверить установку gender c корректными значениями")
    public void testGenderWithCorrectValue(String param) {
        Response response = given()
                .queryParams("gender", param)
                .spec(reqSpec)
                .when()
                .get()
                .then()
                .spec(resSpec)
                .extract().response();

        Map<String, Object> mapUser = response.jsonPath().getMap("results[0]");
        assertThat(((String) mapUser.get("gender"))).isEqualTo(param);
    }

    @Test
    @Description("Проверить установку gender c некорректным значением")
    public void testGenderWithIncorrectValue() {
        String gender = "malefemale";
        Response response = given()
                .queryParams("gender", gender)
                .spec(reqSpec)
                .when()
                .get()
                .then()
                .spec(resSpec)
                .extract().response();

        Map<String, Object> mapUser = response.jsonPath().getMap("results[0]");
        assertThat(((String) mapUser.get("gender"))).isNotEqualTo(gender);
    }

    @ParameterizedTest
    @ValueSource(strings = {"upper:[A-Z]+", "lower:[a-z]+", "number:[0-9]+", "upper,lower,number:[A-z0-9]+"})
    @Description("Проверить установку password (символы, цифры)")
    public void testPasswordCase(String param) {
        String[] params = param.split(":");
        Response response = given()
                .queryParams("password", params[0])
                .spec(reqSpec)
                .when()
                .get()
                .then()
                .spec(resSpec)
                .extract().response();

        Map<String, Object> userMap = response.jsonPath().getMap("results[0].login");
        assertThat(((String) userMap.get("password")).matches(params[1])).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"8-8:8", "16-16:16"})
    @Description("Проверить установку password (длина)")
    public void testPasswordLength(String param) {
        String[] params = param.split(":");
        Response response = given()
                .queryParams("password", params[0])
                .spec(reqSpec)
                .when()
                .get()
                .then()
                .spec(resSpec)
                .extract().response();

        Map<String, Object> userMap = response.jsonPath().getMap("results[0].login");
        assertThat(((String) userMap.get("password")).length()).isEqualTo(parseInt(params[1]));
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "100", "1000"})
    @Description("Проверить установку количества пользователей с корректными значениями")
    public void testCountUsersWithCorrectValue(String param) {
        Response response = given()
                .queryParams("results", param)
                .spec(reqSpec)
                .when()
                .get()
                .then()
                .spec(resSpec)
                .extract().response();

        List<Map<String, Object>> userList = response.jsonPath().getList("results");
        assertThat(userList.size()).isEqualTo(parseInt(param));
    }

    @ParameterizedTest
    @ValueSource(strings = {"-1", "001", "0"})
    @Description("Проверить установку количества пользователей с некорректными значениями")
    public void testCountUsersWithIncorrectValue(String param) {
        Response response = given()
                .queryParams("results", param)
                .spec(reqSpec)
                .when()
                .get()
                .then()
                .spec(resSpec)
                .extract().response();

        List<Map<String, Object>> userList = response.jsonPath().getList("results");
        assertThat(userList.size()).isEqualTo(1);
    }

    @Test
    @Description("Проверить блок инфо (выкл)")
    public void testInfoBlockOff() {
        Response response = given()
                .queryParams("noinfo", "")
                .spec(reqSpec)
                .when()
                .get()
                .then()
                .spec(resSpec)
                .extract().response();

        Map<String, Object> answer = response.jsonPath().get();
        assertThat(answer.get("info")).isNull();
    }

    @Test
    @Description("Проверить блок инфо (вкл)")
    public void testInfoBlockOn() {
        Response response = given()
                .spec(reqSpec)
                .when()
                .get()
                .then()
                .spec(resSpec)
                .extract().response();

        Map<String, Object> answer = response.jsonPath().get();
        assertThat(answer.get("info")).isNotNull();
    }

    @Test
    @Description("Проверить установку корректной версии")
    public void testVersionCorrectValue() {
        Response response = given()
                .spec(reqSpec.basePath("api/1.1"))
                .get()
                .then()
                .spec(resSpec)
                .extract().response();

        Map<String, Object> info = response.jsonPath().getMap("info");
        assertThat(info.get("version")).isEqualTo("1.1");
    }

    @Test
    @Description("Проверить установку несуществующей версии")
    public void testVersionImpossibleValue() {
        given()
                .spec(reqSpec.basePath("ru/andreev/api/-10.1"))
                .when()
                .get()
                .then()
                .spec(resSpec.statusCode(SC_NOT_FOUND))
                .extract().response();
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/random_user/nat.csv")
    @Description("Проверить имеющиеся национальности")
    public void testNationCorrect(String param) {
        Response response = given()
                .queryParams("nat", param)
                .spec(reqSpec)
                .when()
                .get()
                .then()
                .spec(resSpec)
                .extract().response();

        Map<String, Object> userMap = response.jsonPath().getMap("results[0]");
        assertThat(userMap.get("nat")).isEqualTo(param);
    }

    @Test
    @Description("Проверить национальность с несуществующим значением")
    public void testNationIncorrectValue() {
        Response response = given()
                .queryParams("nat", "123")
                .spec(reqSpec)
                .when()
                .get()
                .then()
                .spec(resSpec)
                .extract().response();

        Map<String, Object> userMap = response.jsonPath().getMap("results[0]");
        assertThat(userMap.get("nat")).isNotEqualTo("123");
    }

    @Test
    @Description("Проверить передачу нескольких параметров")
    public void testSomeParams() {
        Response response = given()
                .queryParams(Map.of(
                        "gender", "female",
                        "nat", "US",
                        "inc", "name,gender,location,phone,nat",
                        "results", "10"
                ))
                .spec(reqSpec)
                .when()
                .get()
                .then()
                .spec(resSpec)
                .extract().response();

        List<Map<String, Object>> results = response.jsonPath().getList("results");
        assertThat(results.size()).isEqualTo(10);
        SoftAssertions sa = new SoftAssertions();
        results.forEach(user -> {
            sa.assertThat(user.get("name")).isNotNull();
            sa.assertThat(user.get("location")).isNotNull();
            sa.assertThat(user.get("phone")).isNotNull();
            sa.assertThat(user.get("gender")).isEqualTo("female");
            sa.assertThat(user.get("nat")).isEqualTo("US");
        });
        sa.assertAll();
    }

    @Test
    @Description("Проверить подстановку несуществующего параметра в queryString")
    public void testIncorrectParam() {
        Response response = given()
                .queryParams("something", "123")
                .spec(reqSpec)
                .when()
                .get()
                .then()
                .spec(resSpec)
                .extract().response();

        Map<String, Object> results = response.jsonPath().getMap("results[0]");
        assertThat(results.get("something")).isNull();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "PrettyJSON:application/json; charset=utf-8",
            "JSON:application/json; charset=utf-8",
            "CSV:text/csv; charset=utf-8",
            "YAML:text/x-yaml; charset=utf-8",
            "XML:text/xml; charset=utf-8"
    })
    @Description("Проверить форматы возвращаемых данных")
    public void testFormatData(String param) {
        String[] params = param.split(":");
        Response response = given()
                .queryParams("format", params[0])
                .spec(reqSpec)
                .when()
                .get()
                .then()
                .spec(resSpec)
                .extract().response();

        assertThat(response.contentType()).isEqualTo(params[1]);
    }
}
