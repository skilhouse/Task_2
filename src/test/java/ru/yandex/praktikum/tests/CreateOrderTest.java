package ru.yandex.praktikum.tests;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.praktikum.BaseApiTest;
import ru.yandex.praktikum.client.OrderClient;
import ru.yandex.praktikum.client.UserClient;
import ru.yandex.praktikum.model.Order;
import ru.yandex.praktikum.model.User;
import com.github.javafaker.Faker;

import java.util.Collections;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class CreateOrderTest extends BaseApiTest {

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

    private List<String> getAnyIngredients() {
        return given()
                .spec(baseSpec)
                .when()
                .get("/api/ingredients")
                .then()
                .statusCode(200)
                .extract()
                .path("data._id[0..1]");
    }

    @Test
    @DisplayName("Создание заказа с авторизацией и ингредиентами")
    public void createOrderWithAuthAndIngredientsSuccess() {
        List<String> ingredients = getAnyIngredients();
        Order order = new Order(ingredients);

        ValidatableResponse response = orderClient.createOrder(accessToken, order);

        response
                .statusCode(200)
                .body("success", is(true))
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без авторизации, но с ингредиентами")
    public void createOrderWithoutAuthButWithIngredients() {
        List<String> ingredients = getAnyIngredients();
        Order order = new Order(ingredients);

        ValidatableResponse response = orderClient.createOrderWithoutAuth(order);

        response
                .statusCode(200)  // в доке именно так
                .body("success", is(true));
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов возвращает 400")
    public void createOrderWithoutIngredientsReturns400() {
        Order order = new Order(Collections.emptyList());

        ValidatableResponse response = orderClient.createOrder(accessToken, order);

        response
                .statusCode(400)
                .body("success", is(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиентов возвращает 400")
    public void createOrderWithWrongHashReturns400() {
        Order order = new Order(List.of("invalid_hash"));

        ValidatableResponse response = orderClient.createOrder(accessToken, order);

        response
                .statusCode(400)
                .body("success", is(false));
    }
}
