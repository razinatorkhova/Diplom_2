package tests.apiauthlogin;

import io.qameta.allure.Feature;
import io.qameta.allure.junit4.DisplayName;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Test;
import ru.practikum.yandex.api.UserApi;
import ru.practikum.yandex.model.lombok.LoginDataLombok;
import ru.practikum.yandex.model.lombok.UserDataLombok;

import static org.hamcrest.CoreMatchers.is;
import static ru.practikum.yandex.model.generator.UserGenerator.getRandomUser;

@Feature("Login user")
public class LoginUserTest {

    private static String userAccessToken;
    private String userRefreshToken;
    private final UserApi userApi = new UserApi();

    @After
    public void cleanUp() {
        if (userRefreshToken != null) {
            userApi.logoutUser(userRefreshToken);
        }
    }

    @AfterClass
    public static void tearDown() {
        if (userAccessToken != null) {
            new UserApi().deleteUser(userAccessToken);
        }
    }

    @DisplayName("Check user can be authorized")
    @Test
    public void userCanBeAuthorizedTest() {
        UserDataLombok userData = getRandomUser("Vlad54321", "password54321", "Vlad");

        userApi.createUserLombok(userData)
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("success", is(true));

        LoginDataLombok loginData = new LoginDataLombok(userData.getEmail(), userData.getPassword());
        userApi.loginUser(loginData)
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("success", is(true));
    }

    @Test
    @DisplayName("Check cannot Authorized user with incorrect Email")
    public void cannotAuthorizedUserWithIncorrectEmailTest() {
        assertUnauthorizedUser(new LoginDataLombok("Vlad55554321", "password54321"));
    }

    @Test
    @DisplayName("Check cannot Authorized user with incorrect Password")
    public void cannotAuthorizedUserWithIncorrectPasswordTest() {
        assertUnauthorizedUser(new LoginDataLombok("Vlad54321", "password55554321"));
    }

    private void assertUnauthorizedUser(LoginDataLombok loginData) {
        userApi.loginUser(loginData)
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body("success", is(false));
    }
}
