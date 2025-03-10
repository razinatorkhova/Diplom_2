package ru.practikum.yandex.api;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.apache.http.HttpStatus;
import ru.practikum.yandex.model.lombok.LoginDataLombok;
import ru.practikum.yandex.model.lombok.UserDataLombok;
import ru.practikum.yandex.model.lombok.UserDataUpdateLombok;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

public class UserApi extends RestApi {

    @Step("Create user")
    public ValidatableResponse createUserLombok(UserDataLombok userData) {
        return given()
                .spec(requestSpecification())
                .and()
                .body(userData)
                .when()
                .post(Endpoints.CREATE_USER_URI)
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("success", is(true));
    }

    @Step("Check can not create two same users")
    public ValidatableResponse cannotCreateTwoSameUsers(UserDataLombok userData) {
        return given()
                .spec(requestSpecification())
                .and()
                .body(userData)
                .when()
                .post(Endpoints.CREATE_USER_URI)
                .then().log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .body("success", is(false))
                .body("message", is("User already exists"));
    }

    @Step("Check cannot create user without required field")
    public ValidatableResponse cannotCreateUserWithoutRequiredField(UserDataLombok userData) {
        return given()
                .spec(requestSpecification())
                .and()
                .body(userData)
                .when()
                .post(Endpoints.CREATE_USER_URI)
                .then().log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .body("success", is(false))
                .body("message", is("Email, password and name are required fields"));
    }

    @Step("Authorized user")
    public ValidatableResponse loginUser(LoginDataLombok user) {
        return given()
                .spec(requestSpecification())
                .and()
                .body(user)
                .when()
                .post(Endpoints.LOGIN_USER_URI)
                .then().log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("success", is(true));
    }

    @Step("Check login and get accessToken response")
    public String loginAndGetAccessToken(LoginDataLombok loginData) {
        ValidatableResponse loginResponse = loginUser(loginData);
        return loginResponse.extract().path("accessToken");
    }

    @Step("Check cannot Authorized user with incorrect required field")
    public ValidatableResponse cannotAuthorizedUserWithIncorrectRequiredField(LoginDataLombok user) {
        return given()
                .spec(requestSpecification())
                .and()
                .body(user)
                .when()
                .post(Endpoints.LOGIN_USER_URI)
                .then().log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body("success", is(false))
                .body("message", is("email or password are incorrect"));
    }

    @Step("Check can update name with authorization")
    public ValidatableResponse canUpdateNameWithAuthorization(UserDataUpdateLombok userDataUpdate, String userAccessToken, String expectedName) {
        RequestSpecification request = given()
                .spec(requestSpecification())
                .body(userDataUpdate);

        // Добавляем заголовок только если userAccessToken не null
        if (userAccessToken != null) {
            request.header("Authorization", userAccessToken);
        }

        return request
                .when()
                .patch(Endpoints.GET_CHANGE_DELETE_DATA_USER_URI)
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("user.name", is(expectedName));
    }

    @Step("Check can update email with authorization")
    public ValidatableResponse canUpdateEmailWithAuthorization(UserDataUpdateLombok userDataUpdate, String userAccessToken, String expectedEmail) {
        RequestSpecification request = given()
                .spec(requestSpecification())
                .body(userDataUpdate);

        // Добавляем заголовок только если userAccessToken не null
        if (userAccessToken != null) {
            request.header("Authorization", userAccessToken);
        }

        return request
                .when()
                .patch(Endpoints.GET_CHANGE_DELETE_DATA_USER_URI)
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("user.email", is(expectedEmail.toLowerCase()));
    }

    @Step("Check unauthorized update data response")
    public ValidatableResponse cannotUpdateDataWithoutAuthorization(UserDataUpdateLombok user, String userAccessToken) {
        RequestSpecification request = given()
                .spec(requestSpecification())
                .body(user);
        // Добавляем заголовок только если userAccessToken не null
        if (userAccessToken != null) {
            request.header("Authorization", userAccessToken);
        }
        return request
                .when()
                .patch(Endpoints.GET_CHANGE_DELETE_DATA_USER_URI)
                .then()
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body("success", is(false))
                .body("message", is("You should be authorised"));
    }

    @Step("Delete user")
    public ValidatableResponse deleteUser(String userAccessToken) {
        return given()
                .spec(requestSpecification())
                .header("Authorization", userAccessToken) // Добавляем токен в заголовки
                .when()
                .delete(Endpoints.GET_CHANGE_DELETE_DATA_USER_URI)
                .then();
    }

    @Step("Logout user")
    public ValidatableResponse logoutUser(String userRefreshToken) {
        // Создание JSON тела запроса
        String requestBody = String.format("{\"token\": \"%s\"}", userRefreshToken);

        return given()
                .header("Content-Type", "application/json") // Указание заголовка, если это необходимо
                .body(requestBody)
                .when()
                .post(Endpoints.LOGOUT_USER_URI)
                .then();
    }
}
