package ru.yandex.praktikum.tests;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Test;
import ru.yandex.praktikum.client.UserClient;
import ru.yandex.praktikum.model.User;
import com.github.javafaker.Faker;

import static org.hamcrest.Matchers.*;

public class CreateUserTest {

    private final UserClient userClient = new UserClient();
    private String accessToken;

    private final Faker faker = new Faker();

    @After
    public void cleanUp() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Можно создать уникального пользователя")
    @Description("200 OK, success=true, возвращаются токены")
    public void createUniqueUserSuccess() {
        User user = new User(
                faker.internet().emailAddress(),
                faker.internet().password(8, 12, true, true, true),
                faker.name().fullName()
        );

        ValidatableResponse response = userClient.createUser(user);
        accessToken = response.extract().path("accessToken");

        response
                .statusCode(200)
                .body("success", is(true))
                .body("user.email", equalTo(user.getEmail()));
    }

    @Test
    @DisplayName("Нельзя создать уже зарегистрированного пользователя")
    public void createAlreadyExistUserReturns403() {
        User user = new User(
                faker.internet().emailAddress(),
                faker.internet().password(8, 12, true, true, true),
                faker.name().fullName()
        );

        accessToken = userClient.createUser(user)
                .extract().path("accessToken");

        ValidatableResponse response = userClient.createUser(user);

        response
                .statusCode(403)
                .body("success", is(false))
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Нельзя создать пользователя без обязательного поля")
    public void createUserWithoutRequiredFieldReturnsError() {
        User user = new User("",
                faker.internet().password(8, 12, true, true, true),
                faker.name().fullName()
        );

        ValidatableResponse response = userClient.createUser(user);

        response
                .statusCode(403)
                .body("success", is(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
}
