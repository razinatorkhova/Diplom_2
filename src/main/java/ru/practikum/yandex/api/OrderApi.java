package ru.practikum.yandex.api;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;

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
                .then();
    }

    @Step("Create order with ingredients without authorization")
    public ValidatableResponse createOrderWithoutAuthorizationWithIngredients(String userAccessToken, Object orderBody) {
        return given()
                .spec(requestSpecification())
                .header("Authorization", userAccessToken)
                .body(orderBody)
                .when()
                .post(Endpoints.CREATE_ORDER_URI)
                .then();
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
                .then();
    }

    //Ассерты
    @Step("User should be authorised response")
    public void assertUserShouldBeAuthorised(ValidatableResponse response) {
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body("success", is(false))
                .body("message", is("You should be authorised"));
    }

    @Step("Check response ingredient ids must be provided")
    public void assertIngredientIdsMustBeProvided(ValidatableResponse response) {
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("success", is(false))
                .body("message", is("Ingredient ids must be provided"));
    }

    @Step("Check response ingredient ids must be provided")
    public void assertResponseStatusInterrnalServerError(ValidatableResponse response) {
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }

    @Step("Check response orders is not empty")
    public void assertResponsesBodyHaveNotEmptyOrder(ValidatableResponse response) {
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("success", is(true))
                .body("orders", is(not(empty())));
    }
}