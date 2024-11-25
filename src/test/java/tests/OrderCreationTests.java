package tests;

import api.ApiClient;
import data.DataFactory;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import models.Order;
import models.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;

public class OrderCreationTests {

    private ApiClient apiClient;
    private String createdUserToken;

    @Before
    public void setUp() {
        apiClient = new ApiClient();
        User user = DataFactory.createUniqueUser();
        Response userResponse = apiClient.createUser(user);
        createdUserToken = userResponse.path("accessToken"); // Сохраняем токен пользователя
    }

    @After
    public void tearDown() {
        if (createdUserToken != null) {
            apiClient.deleteUser(createdUserToken); // Удаляем пользователя после тестов
        }
    }

    @Test
    @DisplayName("Создание заказа с авторизацией")
    @Description("Проверка успешного создания заказа с авторизацией и корректным токеном")
    public void testCreateOrderAuthorized() {
        // Создаем заказ с валидными ингредиентами
        Order order = DataFactory.createOrderWithValidIngredients();

        // Отправляем запрос на создание заказа
        Response response = apiClient.createOrder(createdUserToken, order);

        // Проверяем, что заказ успешно создан
        response.then().statusCode(SC_OK);

        // Логируем ответ
        System.out.println("Код ответа: " + response.getStatusCode());
        System.out.println("Тело ответа: " + response.getBody().asString());
    }

    @Test
    @DisplayName("Создание заказа с валидными ингредиентами")
    @Description("Проверка создания заказа с корректным списком ингредиентов")
    public void testCreateOrderWithValidIngredients() {
        // Создаем заказ с валидными ингредиентами
        Order order = DataFactory.createOrderWithValidIngredients();

        // Отправляем запрос на создание заказа
        Response response = apiClient.createOrder(createdUserToken, order);

        // Проверяем, что заказ успешно создан
        response.then().statusCode(SC_OK);
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    @Description("Проверка ошибки при создании заказа без ингредиентов")
    public void testCreateOrderWithoutIngredients() {
        // Создаем заказ без ингредиентов
        Order order = DataFactory.createOrderWithoutIngredients();

        // Отправляем запрос на создание заказа
        Response response = apiClient.createOrder(createdUserToken, order);

        // Проверяем, что сервер возвращает ошибку 400
        response.then().statusCode(SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    @Description("Проверка ошибки при создании заказа без токена авторизации")
    public void testCreateOrderWithoutAuthorization() {
        // Создаем заказ с валидными ингредиентами
        Order order = DataFactory.createOrderWithValidIngredients();

        // Отправляем запрос на создание заказа без токена
        Response response = apiClient.createOrder(null, order);

        // Проверяем, что сервер возвращает ошибку 401
        response.then().statusCode(SC_UNAUTHORIZED);
    }

    @Test
    @DisplayName("Создание заказа с неверными ингредиентами")
    @Description("Проверка ошибки при создании заказа с неверными идентификаторами ингредиентов")
    public void testCreateOrderWithInvalidIngredients() {
        // Создаем заказ с неверными ингредиентами
        Order order = DataFactory.createOrderWithInvalidIngredients();

        // Отправляем запрос на создание заказа
        Response response = apiClient.createOrder(createdUserToken, order);

        // Проверяем, что сервер возвращает ошибку 400
        response.then().statusCode(SC_INTERNAL_SERVER_ERROR);
    }
}
