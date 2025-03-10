package tests.apiorder;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
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

@Feature("Create order")
public class CreateOrderTest {
    protected String userAccessToken;
    protected String userRefreshToken;
    OrderApi orderApi = new OrderApi();
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

    @DisplayName("Create order with authorization and ingredients")
    @Description("Test to create an order with authorization and valid ingredients.")
    @Test
    public void createOrderWithAuthorizationAndIngredients() {
        LoginDataLombok loginDataLombok = new LoginDataLombok(userData.getEmail(), userData.getPassword());
        userAccessToken = userApi.loginAndGetAccessToken(loginDataLombok);

        List<String> ingredientIds = orderApi.getIngredients();
        List<String> selectedIngredients = ingredientIds.subList(0, 2);

        OrderDataLombok orderData = new OrderDataLombok(selectedIngredients);
        ValidatableResponse response = orderApi.createOrderLombok(userAccessToken, orderData);
        userApi.assertValidateSuccessfulResponse(response);
    }

    @DisplayName("Create order without authorization with ingredients")
    @Description("Bug.Test to create an order without authorization using ingredients. Expected: 401 Unauthorized.Actual:200 Ok")
    @Test
    public void cannotCreateAnOrderWithoutAuthorizationWithIngredientsTest() {

        List<String> ingredientIds = orderApi.getIngredients();
        List<String> selectedIngredients = ingredientIds.subList(0, 2);

        OrderDataLombok orderData = new OrderDataLombok(selectedIngredients);
        ValidatableResponse response = orderApi.createOrderLombok("", orderData);
        orderApi.assertUserShouldBeAuthorised(response);
    }

    @DisplayName("Create order with authorization and without ingredients")
    @Description("Test to create an order with authorization but without any ingredients. Expected: 400 Bad Request with message 'Ingredient ids must be provided'")
    @Test
    public void cannotCreateOrderWithAuthorizationWithoutIngredientsTest() {
        LoginDataLombok loginDataLombok = new LoginDataLombok(userData.getEmail(), userData.getPassword());
        userAccessToken = userApi.loginAndGetAccessToken(loginDataLombok);

        OrderDataLombok orderData = new OrderDataLombok(null);
        ValidatableResponse response = orderApi.createOrderLombok(userAccessToken, orderData);
        orderApi.assertIngredientIdsMustBeProvided(response);
    }

    @DisplayName("Create order with authorization and invalid ingredient hash")
    @Description("Test to create an order with authorization using invalid ingredient hashes.Expected:500 Internal Server Error.")
    @Test
    public void cannotCreateOrderWithAuthorizationAndInvalidIngredientHashTest() {
        LoginDataLombok loginDataLombok = new LoginDataLombok(userData.getEmail(), userData.getPassword());
        userAccessToken = userApi.loginAndGetAccessToken(loginDataLombok);

        OrderDataLombok orderData = new OrderDataLombok(List.of("01c0c5a71d1f8206d", "01f82001bdaaa6f"));
        ValidatableResponse response = orderApi.createOrderLombok(userAccessToken, orderData);
        orderApi.assertResponseStatusInterrnalServerError(response);
    }
}