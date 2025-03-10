package tests.apiauthregister;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Test;
import ru.practikum.yandex.api.UserApi;
import ru.practikum.yandex.model.lombok.UserDataLombok;

import static ru.practikum.yandex.model.generator.UserGenerator.getRandomUser;

@Feature("Create user")
public class CreateUserTest {

    protected String userAccessToken;
    UserApi userApi = new UserApi();

    @After
    public void cleanUp() {
        if (userAccessToken != null) {
            userApi.deleteUser(userAccessToken);
        }
    }

    @DisplayName("Check unique user can be created")
    @Description("This test verifies that a unique user can be successfully created in the system. It attempts to create a user with specific credentials and expects a successful response")
    @Test
    public void uniqueUserCanBeCreatedTest() {
        UserDataLombok userData = getRandomUser("Vlad54321", "password54321", "Vlad");
        userApi.createUserLombok(userData);
    }

    @DisplayName("Check cannot create two same users")
    @Description("This test checks that the same user cannot be created twice. It first creates a user and then attempts to create the same user again, expecting a forbidden response indicating that the user already exists.")
    @Test
    public void cannotCreateTwoSameUsersTest() {
        UserDataLombok userData = getRandomUser("Vlad54321", "password54321", "Vlad");
        userApi.createUserLombok(userData); // Create the first user
        userApi.cannotCreateTwoSameUsers(userData); // Attempt to create the same user
    }

    @DisplayName("Check cannot create user without Email")
    @Description("This test verifies that a user cannot be created without providing a login (email). It attempts to create a user with a null login and expects a forbidden response with a specific error message.")
    @Test
    public void cannotCreateUserWithoutEmailTest() {
        UserDataLombok userData = getRandomUser(null, "password54321", "Vlad");
        userApi.cannotCreateUserWithoutRequiredField(userData);
    }

    @DisplayName("Check cannot create user without Password")
    @Description("This test checks that a user cannot be created without providing a password. It attempts to create a user with a null password and expects a forbidden response with a specific error message.")
    @Test
    public void cannotCreateUserWithoutPasswordTest() {
        UserDataLombok userData = getRandomUser("Vlad54321", null, "Vlad");
        userApi.cannotCreateUserWithoutRequiredField(userData);
    }

    @DisplayName("Check cannot create user without Name")
    @Description("This test verifies that a user cannot be created without providing a name. It attempts to create a user with a null name and expects a forbidden response with a specific error message.")
    @Test
    public void cannotCreateUserWithoutNameTest() {
        UserDataLombok userData = getRandomUser("Vlad54321", "password54321", null);
        userApi.cannotCreateUserWithoutRequiredField(userData);
    }
}