package ru.yandex.praktikum;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.mapper.ObjectMapperType;
import io.restassured.specification.RequestSpecification;
import org.junit.BeforeClass;

public class BaseApiTest {

    protected static RequestSpecification baseSpec;

    @BeforeClass
    public static void setUp() {
        RestAssured.baseURI = "https://stellarburgers.education-services.ru";

        RestAssured.config = RestAssuredConfig.config()
                .objectMapperConfig(
                        new ObjectMapperConfig(ObjectMapperType.GSON)
                );

        baseSpec = RestAssured
                .given()
                .config(RestAssured.config)
                .filter(new AllureRestAssured())
                .header("Content-Type", "application/json")
                .log().all();
    }
}
