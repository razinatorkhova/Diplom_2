package tests.apiorder;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.practikum.yandex.api.OrderApi;
import ru.practikum.yandex.api.UserApi;
import ru.practikum.yandex.model.lombok.LoginDataLombok;
import ru.practikum.yandex.model.lombok.OrderDataLombok;
import ru.practikum.yandex.model.lombok.UserDataLombok;

import java.util.List;

import static ru.practikum.yandex.model.generator.UserGenerator.getRandomUser;

@Feature("Get orders from specific user")
public class GetOrdersFromSpecificUserTest {
    protected String userAccessToken;
    protected String userRefreshToken;
    private final OrderApi orderApi = new OrderApi();
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
            new UserApi().deleteUser(userAccessToken);
        }
    }

    @DisplayName("Check get specific user's orders with authorization")
    @Description("Verifies that an authenticated user can retrieve their orders successfully with a 200 status code.")
    @Test
    public void GetSpecificUsersOrdersWithAuthorizationTest() {
        // Авторизация с использованием email и пароля из созданного пользователя
        LoginDataLombok loginDataLombok = new LoginDataLombok(userData.getEmail(), userData.getPassword());
        userAccessToken = userApi.loginAndGetAccessToken(loginDataLombok);

        List<String> ingredientIds = orderApi.getIngredients();
        List<String> selectedIngredients = ingredientIds.subList(0, 2);

        OrderDataLombok orderData = new OrderDataLombok(selectedIngredients);
        orderApi.createOrderLombok(userAccessToken, orderData);

        orderApi.getListOrdersLombok(userAccessToken);

    }

    @DisplayName("Check get specific users orders without authorization")
    @Description("Checks that an unauthorized user receives a 401 error when attempting to retrieve orders.")
    @Test
    public void CannotGetSpecificUsersOrdersWithoutAuthorizationTest() {

        orderApi.getListOrdersLombokWithoutAuthorization("");
    }
}


