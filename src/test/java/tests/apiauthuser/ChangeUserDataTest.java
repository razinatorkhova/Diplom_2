package tests.apiauthuser;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.practikum.yandex.api.UserApi;
import ru.practikum.yandex.model.generator.UserDataUpdateGenerator;
import ru.practikum.yandex.model.lombok.LoginDataLombok;
import ru.practikum.yandex.model.lombok.UserDataLombok;
import ru.practikum.yandex.model.lombok.UserDataUpdateLombok;

import static ru.practikum.yandex.model.generator.UserGenerator.getRandomUser;

@Feature("Change user data")
public class ChangeUserDataTest {

    protected String userAccessToken;
    protected String userRefreshToken;
    UserApi userApi = new UserApi();
    UserDataLombok userData;
    String nonExistentEmail = "nonexistentemail@example.com";

    @Before
    public void setUp() {
        userData = getRandomUser("Vlad54321", "password54321", "Vlad");
        userApi.createUserLombok(userData);
    }

    @After
    public void cleanUp() {

        if (userRefreshToken != null) {
            // Разлогинивание пользователя по его refreshToken
            userApi.logoutUser(userRefreshToken);
        }
        if (userAccessToken != null) {
            userApi.deleteUser(userAccessToken);
        }
    }

    @Test
    @DisplayName("User can update name with authorization")
    @Description("This test verifies that a user can successfully update their name when authorized.")
    public void userCanUpdateNameWithAuthorizationTest() {

        LoginDataLombok loginDataLombok = new LoginDataLombok(userData.getEmail(), userData.getPassword());
        userAccessToken = userApi.loginAndGetAccessToken(loginDataLombok); // получение AccessToken

        // Генерация случайного имени пользователя
        String randomName = UserDataUpdateGenerator.getUpdatedRandomUser().getName();

        // Обновление имени пользователя
        UserDataUpdateLombok userUpdatedLombok = new UserDataUpdateLombok(null, randomName);
        userApi.canUpdateNameWithAuthorization(userUpdatedLombok, userAccessToken, randomName);
    }

    @Test
    @DisplayName("User can update email with authorization")
    @Description("This test verifies that a user can successfully update their email when authorized.")
    public void userCanUpdateEmailWithAuthorizationTest() {

        LoginDataLombok loginDataLombok = new LoginDataLombok(userData.getEmail(), userData.getPassword());
        userAccessToken = userApi.loginAndGetAccessToken(loginDataLombok);

        // Генерация случайного имени пользователя
        String randomEmail = UserDataUpdateGenerator.getUpdatedRandomUser().getEmail();

        // Обновление email пользователя
        UserDataUpdateLombok userUpdatedLombok = new UserDataUpdateLombok(randomEmail, null);
        userApi.canUpdateEmailWithAuthorization(userUpdatedLombok, userAccessToken, randomEmail);
    }

    @Test
    @DisplayName("User cannot update name without authorization")
    @Description("This test verifies that a user cannot change their name when not authorized.")
    public void userCannotChangeNameWithoutAuthorizationTest() {
        // Попытка изменения имени без авторизации (не используем userAccessToken)
        UserDataUpdateLombok userUpdatedLombok = new UserDataUpdateLombok(null, "NewName");
        userApi.cannotUpdateDataWithoutAuthorization(userUpdatedLombok, null);
    }

    @Test
    @DisplayName("User cannot update email without authorization")
    @Description("This test verifies that a user cannot change their email when not authorized.")
    public void userCannotChangeEmailWithoutAuthorizationTest() {
        // Попытка изменения email без авторизации (не используется userAccessToken)
        UserDataUpdateLombok userUpdatedLombok = new UserDataUpdateLombok(nonExistentEmail, null);
        userApi.cannotUpdateDataWithoutAuthorization(userUpdatedLombok, null);
    }
}
