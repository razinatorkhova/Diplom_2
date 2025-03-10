package tests.apiauthlogin;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.practikum.yandex.api.UserApi;
import ru.practikum.yandex.model.lombok.LoginDataLombok;
import ru.practikum.yandex.model.lombok.UserDataLombok;

import static ru.practikum.yandex.model.generator.UserGenerator.getRandomUser;

@Feature("Login user")
public class LoginUserTest {

    protected String userAccessToken;
    protected String userRefreshToken;
    UserApi userApi = new UserApi();
    UserDataLombok userData;

    @Before
    public void setUp() {
        userData = getRandomUser("Vlad54321", "password54321", "Vlad");
        userApi.createUserLombok(userData);
    }

    @After
    public void cleanUp() {
        if (userRefreshToken != null) {
            userApi.logoutUser(userRefreshToken);
        }
        if (userAccessToken != null) {
            userApi.deleteUser(userAccessToken);
        }
    }

    @DisplayName("Check user can be authorized")
    @Description("This test verifies that a user can successfully log in using the correct email and password.")
    @Test
    public void userCanBeAuthorizedTest() {
        // Авторизация с использованием email и пароля из созданного пользователя
        LoginDataLombok user = new LoginDataLombok(userData.getEmail(), userData.getPassword());
        ValidatableResponse response = userApi.loginUser(user);
        userApi.assertValidateSuccessfulResponse(response);
    }

    @DisplayName("Check cannot Authorized user with incorrect Email")
    @Description("This test verifies that a user cannot log in when an incorrect email is provided.")
    @Test
    public void cannotAuthorizedUserWithIncorrectEmailTest() {

        LoginDataLombok user = new LoginDataLombok("Vlad000001", userData.getPassword());
        ValidatableResponse response = userApi.loginUser(user);
        userApi.assertUnauthorizedWithIncorrectRequiredField(response);
    }

    @DisplayName("Check cannot Authorized user with incorrect Password")
    @Description("This test verifies that a user cannot log in when an incorrect password is provided.")
    @Test
    public void cannotAuthorizedUserWithIncorrectPasswordTest() {

        LoginDataLombok user = new LoginDataLombok(userData.getEmail(), "password00001");
        ValidatableResponse response = userApi.loginUser(user);
        userApi.assertUnauthorizedWithIncorrectRequiredField(response);
    }
}
