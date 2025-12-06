package ru.yandex.praktikum.tests;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.praktikum.client.UserClient;
import ru.yandex.praktikum.model.User;
import ru.yandex.praktikum.model.UserCredentials;

import static org.hamcrest.Matchers.*;

public class LoginUserTest {

    private final UserClient userClient = new UserClient();
    private String accessToken;
    private User user;

    private String generateEmail() {
        return "user" + System.currentTimeMillis() + "@test.ru";
    }

    @Before
    public void setUpUser() {
        user = new User(generateEmail(), "password", "Name");
        ValidatableResponse response = userClient.createUser(user);
        accessToken = response.extract().path("accessToken");
    }

    @After
    public void cleanUp() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Успешный логин под существующим пользователем")
    public void loginExistingUserSuccess() {
        ValidatableResponse response =
                userClient.login(new UserCredentials(user.getEmail(), user.getPassword()));

        response
                .statusCode(200)
                .body("success", is(true))
                .body("accessToken", notNullValue());
    }

    @Test
    @DisplayName("Логин с неверным логином/паролем возвращает 401")
    public void loginWithWrongCredsReturns401() {
        ValidatableResponse response =
                userClient.login(new UserCredentials(user.getEmail(), "wrong"));

        response
                .statusCode(401)
                .body("success", is(false))
                .body("message", equalTo("email or password are incorrect"));
    }
}
