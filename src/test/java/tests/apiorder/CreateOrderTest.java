package tests.apiorder;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Test;
import ru.practikum.yandex.api.OrderApi;
import ru.practikum.yandex.api.UserApi;
import ru.practikum.yandex.model.lombok.LoginDataLombok;
import ru.practikum.yandex.model.lombok.OrderDataLombok;
import ru.practikum.yandex.model.lombok.UserDataLombok;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static ru.practikum.yandex.model.generator.UserGenerator.getRandomUser;

@Feature("Create order")
public class CreateOrderTest {
    private static String userAccessToken;
    private String userRefreshToken;
    private final OrderApi orderApi = new OrderApi();
    private static final UserApi userApi = new UserApi();

    @After
    public void cleanUp() {
        if (userRefreshToken != null) userApi.logoutUser(userRefreshToken);
    }

    @AfterClass
    public static void tearDown() {
        if (userAccessToken != null) userApi.deleteUser(userAccessToken);
    }

    private UserDataLombok setupUser(String username, String password) {
        UserDataLombok userData = getRandomUser(username, password, username);
        userApi.createUserLombok(userData)
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("success", is(true));
        return userData;
    }

    private void loginUser(UserDataLombok userData) {
        LoginDataLombok loginData = new LoginDataLombok(userData.getEmail(), userData.getPassword());
        userApi.loginUser(loginData)
                .log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("success", is(true));
    }

    private List<String> fetchIngredients() {
        return orderApi.getIngredients(null)
                .assertThat().statusCode(HttpStatus.SC_OK).body("success", is(true))
                .extract().jsonPath().getList("data._id");
    }

    @DisplayName("Create order with authorization and ingredients")
    @Test
    public void createOrderWithAuthorizationAndIngredients() {
        UserDataLombok userData = setupUser("Vlad54321", "password54321");
        loginUser(userData);
        List<String> selectedIngredients = fetchIngredients().subList(0, 2);

        orderApi.createOrderLombok(new OrderDataLombok(selectedIngredients))
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("success", is(true));
    }

    @DisplayName("Create order without authorization with ingredients")
    @Description("Bug.Expected:401 Unauthorized. Actual:200 Ok")
    @Test
    public void createAnOrderWithoutAuthorizationWithIngredientsTest() {

        UserDataLombok userData = setupUser("Vlad54321", "password54321");
        List<String> ingredientIds = fetchIngredients(); // Replace getIngredients() with fetchIngredients()
        List<String> selectedIngredients = ingredientIds.subList(0, 2);
        OrderDataLombok orderData = new OrderDataLombok(selectedIngredients);
        orderApi.createOrderLombok(orderData)
                .assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body("success", is(false));
    }


    @DisplayName("Create order with authorization and without ingredients")
    @Test
    public void createOrderWithAuthorizationWithoutIngredientsTest() {
        UserDataLombok userData = setupUser("Vlad54321", "password54321");
        loginUser(userData);
        fetchIngredients(); // Заменяем getIngredients() на fetchIngredients()

        OrderDataLombok orderData = new OrderDataLombok(null);
        orderApi.createOrderLombok(orderData)
                .assertThat().statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("success", is(false))
                .body("message", is("Ingredient ids must be provided"));
    }


    @DisplayName("Create order with authorization and invalid ingredient hash")
    @Description("Bug.Expected:500 Internal Server Error. Actual:400 Bad Request")
    @Test
    public void createOrderWithAuthorizationAndInvalidIngredientHashTest() {
        UserDataLombok userData = setupUser("Vlad54321", "password54321");
        loginUser(userData);

        OrderDataLombok orderData = new OrderDataLombok(List.of("01c0c5a71d1f82001bdaaa6d", "01c0c5a71d1f82001bdaaa6f"));
        OrderApi orderApi = new OrderApi();
        ValidatableResponse orderResponse = orderApi.createOrderLombok(orderData);
        orderResponse.log().all()
                .assertThat()
                .statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }
}