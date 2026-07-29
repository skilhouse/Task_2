package ru.yandex.praktikum.tests;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.praktikum.client.UserClient;
import ru.yandex.praktikum.model.User;
import com.github.javafaker.Faker;

import static org.hamcrest.Matchers.*;

public class UpdateUserTest {

    private final UserClient userClient = new UserClient();
    private final Faker faker = new Faker();

    private String accessToken;
    private User user;

    @Before
    public void createAndLoginUser() {
        user = new User(
                faker.internet().emailAddress(),
                faker.internet().password(8, 12, true, true, true),
                faker.name().fullName()
        );

        ValidatableResponse createResp = userClient.createUser(user);
        accessToken = createResp.extract().path("accessToken");
    }

    @After
    public void cleanUp() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Можно изменить любые данные авторизованного пользователя")
    public void updateUserWithAuthSuccess() {
        User newData = new User(
                faker.internet().emailAddress(),
                faker.internet().password(8, 12, true, true, true),
                faker.name().fullName()
        );

        ValidatableResponse response = userClient.updateUser(accessToken, newData);

        response
                .statusCode(200)
                .body("success", is(true))
                .body("user.email", equalTo(newData.getEmail()))
                .body("user.name", equalTo(newData.getName()));
    }

    @Test
    @DisplayName("Нельзя изменить данные без авторизации")
    public void updateUserWithoutAuthReturnsError() {
        User newData = new User(
                faker.internet().emailAddress(),
                faker.internet().password(8, 12, true, true, true),
                faker.name().fullName()
        );

        ValidatableResponse response = userClient.updateUser("", newData);

        response
                .statusCode(401)
                .body("success", is(false))
                .body("message", equalTo("You should be authorised"));
    }
}
