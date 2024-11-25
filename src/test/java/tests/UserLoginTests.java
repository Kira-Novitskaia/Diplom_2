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

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;

public class UserLoginTests {

    private ApiClient apiClient;
    private User createdUser;
    private String createdUserToken;

    @Before
    public void setUp() {
        apiClient = new ApiClient();
        createdUser = DataFactory.createUniqueUser(); // Создаем уникального пользователя
        Response response = apiClient.createUser(createdUser); // Создаем пользователя через API
        createdUserToken = response.path("accessToken"); // Сохраняем токен
    }

    @After
    public void tearDown() {
        if (createdUserToken != null) {
            apiClient.deleteUser(createdUserToken); // Удаляем пользователя после тестов
        }
    }

    @Test
    @DisplayName("Логин под существующим пользователем")
    @Description("Проверка успешного входа в систему для существующего пользователя")
    public void testLoginExistingUser() {
        Response loginResponse = apiClient.loginUser(createdUser.getEmail(), createdUser.getPassword()); // Используем email и пароль созданного пользователя
        loginResponse.then().statusCode(SC_OK); // Проверяем, что логин успешен
    }

    @Test
    @DisplayName("Логин с неверными данными")
    @Description("Проверка, что нельзя войти в систему с неверным паролем")
    public void testLoginWithInvalidCredentials() {
        Response loginResponse = apiClient.loginUser(createdUser.getEmail(), "wrongPassword"); // Используем неверный пароль
        loginResponse.then().statusCode(SC_UNAUTHORIZED); // Ожидаем ошибку 401
    }
}
