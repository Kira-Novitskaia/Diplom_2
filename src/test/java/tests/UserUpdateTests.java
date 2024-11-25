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

public class UserUpdateTests {

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
    @DisplayName("Изменение данных пользователя с авторизацией")
    @Description("Проверка возможности изменения данных пользователя с авторизацией")
    public void testUpdateUserWithAuthorization() {
        User updatedUser = new User("updated.email@example.com", "newPassword123", "UpdatedUser");
        Response response = apiClient.updateUser(createdUserToken, updatedUser);
        response.then().statusCode(SC_OK)
                .body("user.email", equalTo(updatedUser.getEmail()))
                .body("user.name", equalTo(updatedUser.getName()));
    }

    @Test
    @DisplayName("Изменение данных пользователя без авторизации")
    @Description("Проверка ошибки при попытке изменения данных пользователя без авторизации")
    public void testUpdateUserWithoutAuthorization() {
        User updatedUser = new User("updated.email@example.com", "newPassword123", "UpdatedUser");
        Response response = apiClient.updateUser(null, updatedUser);
        response.then().statusCode(SC_UNAUTHORIZED)
                .body("message", equalTo("You should be authorised"));
    }
}
