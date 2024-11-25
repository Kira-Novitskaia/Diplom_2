package tests;

import api.ApiClient;
import data.DataFactory;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import models.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;

public class OrderRetrievalTests {

    private ApiClient apiClient;
    private String createdUserToken;

    @Before
    public void setUp() {
        apiClient = new ApiClient();
        User user = DataFactory.createUniqueUser();
        createdUserToken = apiClient.createUser(user).path("accessToken");
    }

    @After
    public void tearDown() {
        if (createdUserToken != null) {
            apiClient.deleteUser(createdUserToken);
        }
    }

    @Test
    @DisplayName("Получение заказов авторизованного пользователя")
    @Description("Проверка успешного получения заказов для авторизованного пользователя")
    public void testGetOrdersWithAuthorization() {
        Response response = apiClient.getUserOrders(createdUserToken);
        response.then().statusCode(SC_OK)
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Получение заказов неавторизованного пользователя")
    @Description("Проверка ошибки при попытке получить заказы без авторизации")
    public void testGetOrdersWithoutAuthorization() {
        Response response = apiClient.getUserOrders(null);
        response.then().statusCode(SC_UNAUTHORIZED)
                .body("message", equalTo("You should be authorised"));
    }
}
