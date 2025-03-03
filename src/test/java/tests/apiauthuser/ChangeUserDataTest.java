package tests;

import io.qameta.allure.Feature;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Test;
import ru.practikum.yandex.api.UserApi;
import ru.practikum.yandex.model.lombok.LoginDataLombok;
import ru.practikum.yandex.model.lombok.UserDataLombok;
import ru.practikum.yandex.model.lombok.UserDataUpdateLombok;

import static org.hamcrest.CoreMatchers.is;
import static ru.practikum.yandex.model.generator.UserDataUpdateGenerator.getUpdatedRandomUser;
import static ru.practikum.yandex.model.generator.UserGenerator.getRandomUser;

@Feature("Change user data")
public class ChangeUserDataTest {

    protected static String userAccessToken;
    protected String userRefreshToken;

    @After
    public void cleanUp() {
        UserApi userApi = new UserApi();
        if (userRefreshToken != null) {
            // Разлогиниваем пользователя по его refreshToken
            userApi.logoutUser(userRefreshToken);
        }
           if (userAccessToken != null) {
            userApi.deleteUser(userAccessToken);
        }
    }
    @Test
    @DisplayName("User can update data with authorization")
    public void userCanUpdateDataWithAuthorizationTest() {
        UserDataLombok userDataLombok = getRandomUser("Vlad54321", "password54321", "Vlad");
        UserApi userApi = new UserApi();

        // Создание пользователя
        ValidatableResponse createResponse = userApi.createUserLombok(userDataLombok);
        createResponse.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("success", is(true));
        // Авторизация пользователя
        LoginDataLombok loginDataLombok = new LoginDataLombok(userDataLombok.getEmail(), userDataLombok.getPassword());
        ValidatableResponse loginResponse = userApi.loginUser(loginDataLombok);
        loginResponse.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("success", is(true));
        userAccessToken = loginResponse.extract().path("accessToken");

        // Генерация новых email и name
        UserDataUpdateLombok userUpdatedLombok = getUpdatedRandomUser();

        // Обновление данных пользователя
        ValidatableResponse updatedResponse = userApi.updatedDataUser(userUpdatedLombok, userAccessToken);
        updatedResponse.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("success", is(true));

        // Проверка, что данные обновлены
        ValidatableResponse getUserResponse = userApi.getUserData(userAccessToken);
        getUserResponse.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("user.email", is(userUpdatedLombok.getEmail().toLowerCase()))
                .body("user.name", is(userUpdatedLombok.getName()));
    }

    @Test
    @DisplayName("User cannot change their data without authorization")
    public void userCannotChangeDataWithoutAuthorizationTest() {
        // Попытка изменения данных без авторизации
        UserDataUpdateLombok updatedUserData = new UserDataUpdateLombok("updatedemail@gmail.com", "UpdatedName");
        UserApi userApi = new UserApi();

        ValidatableResponse changeResponse = userApi.changeUserData("", updatedUserData);
        changeResponse.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body("success", is(false));
    }

}
