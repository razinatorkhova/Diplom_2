package ru.practikum.yandex.api;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import ru.practikum.yandex.model.lombok.OrderDataLombok;

import java.util.List;

import static io.restassured.RestAssured.given;


public class OrderApi extends RestApi {
    /**
     * URI для создания заказа
     */
    public static final String CREATE_ORDER_URI = "/api/orders";
    /**
     * URI для получения данных об ингредиентах
     */
    public static final String GET_DATA_INGREDIENTS_URI = "/api/ingredients";

    @Step("Create order")
    public ValidatableResponse createOrderLombok(OrderDataLombok order) {
        return given()
                .spec(requestSpecification())
                .and()
                .body(order)
                .when()
                .post(CREATE_ORDER_URI)
                .then();
    }

    @Step("Get ingredients")
    public ValidatableResponse getIngredients(List<String> ingredients) {
        return given()
                .spec(requestSpecification())
                .when()
                .get(GET_DATA_INGREDIENTS_URI)
                .then();
    }

    @Step("Get list of orders from specific user")
    public ValidatableResponse getListOrdersLombok(String userAccessToken) {
        return given()
                .spec(requestSpecification())
                .header("Authorization", userAccessToken)
                .and()
                .when()
                .get(CREATE_ORDER_URI)
                .then();
    }
}
