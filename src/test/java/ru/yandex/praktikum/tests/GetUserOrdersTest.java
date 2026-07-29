package ru.yandex.praktikum.tests;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.praktikum.client.OrderClient;
import ru.yandex.praktikum.client.UserClient;
import ru.yandex.praktikum.model.User;
import com.github.javafaker.Faker;

import static org.hamcrest.Matchers.*;

public class GetUserOrdersTest {

    private final UserClient userClient = new UserClient();
    private final OrderClient orderClient = new OrderClient();
    private final Faker faker = new Faker();
    private String accessToken;

    @Before
    public void createUser() {
        User user = new User(
                faker.internet().emailAddress(),
                faker.internet().password(8, 12, true, true, true),
                faker.name().fullName()
        );

        accessToken = userClient.createUser(user)
                .extract().path("accessToken");
    }

    @After
    public void cleanUp() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Авторизованный пользователь может получить свои заказы")
    public void authorisedUserCanGetOrders() {
        ValidatableResponse response = orderClient.getUserOrders(accessToken);

        response
                .statusCode(200)
                .body("success", is(true))
                .body("orders", notNullValue());
    }

    @Test
    @DisplayName("Неавторизованный пользователь не может получить заказы")
    public void notAuthorisedUserGets401() {
        ValidatableResponse response = orderClient.getUserOrdersWithoutAuth();

        response
                .statusCode(401)
                .body("success", is(false))
                .body("message", equalTo("You should be authorised"));
    }
}
