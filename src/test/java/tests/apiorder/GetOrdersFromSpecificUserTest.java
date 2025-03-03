package tests.apiorder;

import io.qameta.allure.Feature;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.junit.AfterClass;
import org.junit.Test;
import ru.practikum.yandex.api.OrderApi;
import ru.practikum.yandex.api.UserApi;
import ru.practikum.yandex.model.lombok.LoginDataLombok;
import ru.practikum.yandex.model.lombok.OrderDataLombok;
import ru.practikum.yandex.model.lombok.UserDataLombok;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.emptyArray;
import static org.hamcrest.Matchers.not;
import static ru.practikum.yandex.model.generator.UserGenerator.getRandomUser;

@Feature("Получение заказов конкретного пользователя")
public class GetOrdersFromSpecificUserTest {
    private static String userAccessToken;
    private static final UserApi userApi = new UserApi();
    private static final OrderApi orderApi = new OrderApi();

    @AfterClass
    public static void tearDown() {
        if (userAccessToken != null) {
            userApi.deleteUser(userAccessToken);
        }
    }

    @DisplayName("Получить заказы конкретного пользователя с авторизацией")
    @Test
    public void getSpecificUsersOrdersWithAuthorizationTest() {
        UserDataLombok userData = createUser("Vlad54321", "password54321", "Vlad");
        userAccessToken = loginUser(userData);

        List<String> selectedIngredients = getIngredients().subList(0, 2);
        createOrder(selectedIngredients);

        validateUserOrders();
    }

    @DisplayName("Получить заказы конкретного пользователя без авторизации")
    @Test
    public void getSpecificUsersOrdersWithoutAuthorizationTest() {
        orderApi.getListOrdersLombok("").assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body("success", is(false))
                .body("message", is("You should be authorised"));
    }

    private UserDataLombok createUser(String username, String password, String name) {
        UserDataLombok userData = getRandomUser(username, password, name);
        userApi.createUserLombok(userData).assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("success", is(true));
        return userData;
    }

    private String loginUser(UserDataLombok userData) {
        LoginDataLombok loginData = new LoginDataLombok(userData.getEmail(), userData.getPassword());
        ValidatableResponse loginResponse = userApi.loginUser(loginData);
        loginResponse.assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("success", is(true));
        return loginResponse.extract().path("accessToken");
    }

    private void createOrder(List<String> selectedIngredients) {
        OrderDataLombok orderData = new OrderDataLombok(selectedIngredients);
        orderApi.createOrderLombok(orderData).assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("success", is(true));
    }

    private void validateUserOrders() {
        orderApi.getListOrdersLombok(userAccessToken).assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("success", is(true))
                .body("orders", is(not(emptyArray())));
    }

    private List<String> getIngredients() {
        ValidatableResponse response = orderApi.getIngredients(null);
        response.assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("success", is(true));
        return response.extract().jsonPath().getList("data._id");
    }
}
