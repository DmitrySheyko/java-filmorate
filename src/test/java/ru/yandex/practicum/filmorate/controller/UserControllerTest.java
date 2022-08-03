package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    static UserController userController;
    static User testUser;

    @BeforeAll
    static void beforeAll() {
        userController = new UserController();
    }

    @BeforeEach
    void beforeEach() {
        testUser = new User();
        testUser.setName("Ivan");
        testUser.setLogin("TestUser");
        testUser.setBirthday("2010-10-10");
        testUser.setEmail("Ivan@ivan.ru");
    }

    @Test
    void shouldApproveUserWithCorrectData() throws ValidationException {
        assertTrue(userController.checkIsUserDataCorrect(testUser),
                "Корректная версия User не прошла проверку");
    }

    @Test
    void shouldDeclineUserWithIncorrectEmail() {
        testUser.setEmail(null);
        assertThrows(ValidationException.class, () -> userController.checkIsUserDataCorrect(testUser));
        testUser.setEmail("");
        assertThrows(ValidationException.class, () -> userController.checkIsUserDataCorrect(testUser));
        testUser.setEmail(" ");
        assertThrows(ValidationException.class, () -> userController.checkIsUserDataCorrect(testUser));
        testUser.setEmail("Ivan.ivan.ru");
        assertThrows(ValidationException.class, () -> userController.checkIsUserDataCorrect(testUser));
    }

    @Test
    void shouldDeclineUserWithIncorrectLogin() {
        testUser.setLogin(null);
        assertThrows(ValidationException.class, () -> userController.checkIsUserDataCorrect(testUser));
        testUser.setLogin("");
        assertThrows(ValidationException.class, () -> userController.checkIsUserDataCorrect(testUser));
        testUser.setLogin(" ");
        assertThrows(ValidationException.class, () -> userController.checkIsUserDataCorrect(testUser));
        testUser.setLogin("Ivan ");
        assertThrows(ValidationException.class, () -> userController.checkIsUserDataCorrect(testUser));
    }

    @Test
    void shouldDeclineUserWithIncorrectBirthDay() {
        testUser.setBirthday(null);
        assertThrows(ValidationException.class, () -> userController.checkIsUserDataCorrect(testUser));
        testUser.setBirthday("2023-10-10");
        assertThrows(ValidationException.class, () -> userController.checkIsUserDataCorrect(testUser));
    }

    @Test
    void shouldReplaceEmptyNameByLogin() throws ValidationException {
        testUser.setLogin("CorrectLogin");
        testUser.setName("");
        userController.checkIsUserDataCorrect(testUser);
        assertEquals("CorrectLogin", testUser.getName());
    }
}