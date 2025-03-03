package tests.apiauthregister;

import io.qameta.allure.Feature;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.practikum.yandex.api.UserApi;
import ru.practikum.yandex.model.lombok.UserDataLombok;

import static org.hamcrest.CoreMatchers.is;
import static ru.practikum.yandex.model.generator.UserGenerator.getRandomUser;

@Feature("Create user")
public class CreateUserTest {

    protected String userAccessToken;
    protected UserApi userApi;

    @Before
    public void setUp() {
        userApi = new UserApi();
    }

    @After
    public void cleanUp() {
        if (userAccessToken != null) {
            userApi.deleteUser(userAccessToken);
        }
    }

    private void assertResponse(ValidatableResponse response, int expectedStatus, String expectedMessage) {
        response.log().all()
                .assertThat()
                .statusCode(expectedStatus)
                .body("success", is(expectedMessage == null));
        if (expectedMessage != null) {
            response.assertThat().body("message", is(expectedMessage));
        }
    }

    @DisplayName("Check unique user can be created")
    @Test
    public void uniqueUserCanBeCreatedTest() {
        UserDataLombok userData = getRandomUser("Vlad54321", "password54321", "Vlad");
        ValidatableResponse response = userApi.createUserLombok(userData);
        userAccessToken = response.extract().path("accessToken");
        assertResponse(response, HttpStatus.SC_OK, null);
    }

    @DisplayName("Check cannot create two same users")
    @Test
    public void cannotCreateTwoSameUsersTest() {
        UserDataLombok userData = getRandomUser("Vlad54321", "password54321", "Vlad");
        userApi.createUserLombok(userData); // Create the first user
        ValidatableResponse response = userApi.createUserLombok(userData); // Attempt to create the same user
        assertResponse(response, HttpStatus.SC_FORBIDDEN, "User already exists");
    }

    @DisplayName("Check cannot create user without Login")
    @Test
    public void cannotCreateUserWithoutLoginTest() {
        UserDataLombok userData = getRandomUser(null, "password54321", "Vlad");
        ValidatableResponse response = userApi.createUserLombok(userData);
        assertResponse(response, HttpStatus.SC_FORBIDDEN, "Email, password and name are required fields");
    }

    @DisplayName("Check cannot create user without Password")
    @Test
    public void cannotCreateUserWithoutPasswordTest() {
        UserDataLombok userData = getRandomUser("Vlad54321", null, "Vlad");
        ValidatableResponse response = userApi.createUserLombok(userData);
        assertResponse(response, HttpStatus.SC_FORBIDDEN, "Email, password and name are required fields");
    }

    @DisplayName("Check cannot create user without Name")
    @Test
    public void cannotCreateUserWithoutNameTest() {
        UserDataLombok userData = getRandomUser("Vlad54321", "password54321", null);
        ValidatableResponse response = userApi.createUserLombok(userData);
        assertResponse(response, HttpStatus.SC_FORBIDDEN, "Email, password and name are required fields");
    }
}