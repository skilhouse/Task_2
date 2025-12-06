package ru.yandex.praktikum.client;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import ru.yandex.praktikum.BaseApiTest;
import ru.yandex.praktikum.model.Order;

import static io.restassured.RestAssured.given;

public class OrderClient extends BaseApiTest {

    private static final String ORDER_PATH = "/api/orders";

    @Step("Создать заказ")
    public ValidatableResponse createOrder(String accessToken, Order order) {
        return given()
                .spec(baseSpec)
                .header("Authorization", accessToken)
                .body(order)
                .when()
                .post(ORDER_PATH)
                .then();
    }

    @Step("Создать заказ без авторизации")
    public ValidatableResponse createOrderWithoutAuth(Order order) {
        return given()
                .spec(baseSpec)
                .body(order)
                .when()
                .post(ORDER_PATH)
                .then();
    }

    @Step("Получить заказы пользователя")
    public ValidatableResponse getUserOrders(String accessToken) {
        return given()
                .spec(baseSpec)
                .header("Authorization", accessToken)
                .when()
                .get(ORDER_PATH)
                .then();
    }

    @Step("Получить заказы пользователя без авторизации")
    public ValidatableResponse getUserOrdersWithoutAuth() {
        return given()
                .spec(baseSpec)
                .when()
                .get(ORDER_PATH)
                .then();
    }
}
