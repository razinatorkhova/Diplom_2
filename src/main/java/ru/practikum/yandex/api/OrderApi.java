package ru.practikum.yandex.api;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.*;


public class OrderApi extends RestApi {

    @Step("Create order")
    public ValidatableResponse createOrderLombok(String userAccessToken, Object orderBody) {
        return given()
                .spec(requestSpecification())
                .header("Authorization", userAccessToken)
                .body(orderBody)
                .when()
                .post(Endpoints.CREATE_ORDER_URI)
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("success", is(true));
    }

    @Step("Create order with ingredients without authorization")
    public ValidatableResponse createOrderWithoutAuthorizationWithIngredients(String userAccessToken, Object orderBody) {
        return given()
                .spec(requestSpecification())
                .header("Authorization", userAccessToken)
                .body(orderBody)
                .when()
                .post(Endpoints.CREATE_ORDER_URI)
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body("success", is(false))
                .body("message", is("You should be authorised"));
    }

    @Step("Create order with authorization without ingredients")
    public ValidatableResponse createOrderWithAuthorizationWithoutIngredients(String userAccessToken, Object orderBody) {
        return given()
                .spec(requestSpecification())
                .header("Authorization", userAccessToken)
                .body(orderBody)
                .when()
                .post(Endpoints.CREATE_ORDER_URI)
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("success", is(false))
                .body("message", is("Ingredient ids must be provided"));
    }

    @Step("Create order with authorization with invalid ingredients")
    public ValidatableResponse createOrderWithAuthorizationWithInvalidIngredients(String userAccessToken, Object orderBody) {
        return given()
                .spec(requestSpecification())
                .header("Authorization", userAccessToken)
                .body(orderBody)
                .when()
                .post(Endpoints.CREATE_ORDER_URI)
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }

    @Step("Get ingredients")
    public List<String> getIngredients() {
        ValidatableResponse response = given()
                .spec(requestSpecification())
                .when()
                .get(Endpoints.GET_DATA_INGREDIENTS_URI)
                .then()
                .statusCode(HttpStatus.SC_OK);

        return response.extract().jsonPath().getList("data._id");
    }

    @Step("Get list of orders from specific user")
    public ValidatableResponse getListOrdersLombok(String userAccessToken) {
        return given()
                .spec(requestSpecification())
                .header("Authorization", userAccessToken)
                .and()
                .when()
                .get(Endpoints.CREATE_ORDER_URI)
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("success", is(true))
                .body("orders", is(not(empty())));
    }

    @Step("Get list of orders from specific user without authorization")
    public ValidatableResponse getListOrdersLombokWithoutAuthorization(String userAccessToken) {
        return given()
                .spec(requestSpecification())
                .header("Authorization", userAccessToken)
                .and()
                .when()
                .get(Endpoints.CREATE_ORDER_URI)
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body("success", is(false))
                .body("message", is("You should be authorised"));
    }
}