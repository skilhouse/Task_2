package ru.yandex.praktikum.client;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import ru.yandex.praktikum.BaseApiTest;
import ru.yandex.praktikum.model.User;
import ru.yandex.praktikum.model.UserCredentials;

import static io.restassured.RestAssured.given;

public class UserClient extends BaseApiTest {

    private static final String REGISTER_PATH = "/api/auth/register";
    private static final String LOGIN_PATH = "/api/auth/login";
    private static final String USER_PATH = "/api/auth/user";
    private static final String LOGOUT_PATH = "/api/auth/logout";

    @Step("Создать пользователя")
    public ValidatableResponse createUser(User user) {
        return given()
                .spec(baseSpec)
                .body(user)
                .when()
                .post(REGISTER_PATH)
                .then();
    }

    @Step("Логин пользователя")
    public ValidatableResponse login(UserCredentials creds) {
        return given()
                .spec(baseSpec)
                .body(creds)
                .when()
                .post(LOGIN_PATH)
                .then();
    }

    @Step("Обновить данные пользователя")
    public ValidatableResponse updateUser(String accessToken, User newData) {
        return given()
                .spec(baseSpec)
                .header("Authorization", accessToken)
                .body(newData)
                .when()
                .patch(USER_PATH)
                .then();
    }

    @Step("Удалить пользователя")
    public ValidatableResponse deleteUser(String accessToken) {
        return given()
                .spec(baseSpec)
                .header("Authorization", accessToken)
                .when()
                .delete(USER_PATH)
                .then();
    }

    @Step("Выход из системы")
    public ValidatableResponse logout(String refreshToken) {
        return given()
                .spec(baseSpec)
                .body("{\"token\":\"" + refreshToken + "\"}")
                .when()
                .post(LOGOUT_PATH)
                .then();
    }
}
