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
                .then();
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

    @Step("Check can not create two same users")
    public ValidatableResponse cannotCreateTwoSameUsers(UserDataLombok userData) {
        return given()
                .spec(requestSpecification())
                .and()
                .body(userData)
                .when()
                .post(Endpoints.CREATE_USER_URI)
                .then();
    }

    @Step("Authorized user")
    public ValidatableResponse loginUser(LoginDataLombok user) {
        return given()
                .spec(requestSpecification())
                .and()
                .body(user)
                .when()
                .post(Endpoints.LOGIN_USER_URI)
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

    @Step("Check login and get accessToken response")
    public String loginAndGetAccessToken(LoginDataLombok loginData) {
        ValidatableResponse loginResponse = loginUser(loginData);
        return loginResponse.extract().path("accessToken");
    }

    @Step("Check can update name")
    public ValidatableResponse canUpdateName(UserDataUpdateLombok userDataUpdate, String userAccessToken, String expectedName) {
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
                .then();
    }

    //Ассерты
    @Step("Successful response")
    public void assertValidateSuccessfulResponse(ValidatableResponse response) {
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("success", is(true));
    }

    @Step("Forbidden user already exists response")
    public void assertForbiddenUserAlreadyExistsResponse(ValidatableResponse response) {
        response.log().all()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .body("success", is(false))
                .body("message", is("User already exists"));
    }

    @Step("Forbidden invalid required field response")
    public void assertForbiddenInvalidRequiredFieldResponse(ValidatableResponse response) {
        response.log().all()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .body("success", is(false))
                .body("message", is("Email, password and name are required fields"));
    }

    @Step("Unauthorized with incorrect required field response")
    public void assertUnauthorizedWithIncorrectRequiredField(ValidatableResponse response) {
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body("success", is(false))
                .body("message", is("email or password are incorrect"));
    }

    @Step("Update name with authorization response")
    public void assertUpdateNameWithAuthorization(ValidatableResponse response, String expectedName) {
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("user.name", is(expectedName));
    }

    @Step("Update email with authorization response")
    public void assertUpdateEmailWithAuthorization(ValidatableResponse response, String expectedEmail) {
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("user.email", is(expectedEmail.toLowerCase()));
    }

    @Step("Update data without authorization response")
    public void assertUpdateDataWithoutAuthorization(ValidatableResponse response) {
        response.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body("success", is(false))
                .body("message", is("You should be authorised"));
    }
}
