package ru.practikum.yandex.model.generator;

import io.qameta.allure.Step;
import org.apache.commons.lang3.RandomStringUtils;
import ru.practikum.yandex.model.lombok.UserDataLombok;

public class UserGenerator {
    @Step("Generate random user")
    public static UserDataLombok getRandomUser() {
        String email = RandomStringUtils.randomAlphabetic(8) + "@gmail.com"; // Генерация email
        String password = RandomStringUtils.randomAlphabetic(8);
        String name = RandomStringUtils.randomAlphabetic(8);

        return new UserDataLombok(email, password, name);
    }

    @Step("Generate random user")
    public static UserDataLombok getRandomUser(String emailParam, String passwordParam,
                                               String nameParam) {
        String email = (emailParam != null) ? emailParam + RandomStringUtils.randomAlphabetic(5) + "@gmail.com" : ""; // Генерация email
        String password = (passwordParam != null) ? passwordParam + RandomStringUtils.randomAlphabetic(5) : "";
        String name = (nameParam != null) ? nameParam + RandomStringUtils.randomAlphabetic(5) : "";

        return new UserDataLombok(email, password, name);
    }
}
