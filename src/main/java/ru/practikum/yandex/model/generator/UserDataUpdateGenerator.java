package ru.practikum.yandex.model.generator;

import io.qameta.allure.Step;
import org.apache.commons.lang3.RandomStringUtils;
import ru.practikum.yandex.model.lombok.UserDataUpdateLombok;

public class UserDataUpdateGenerator {

    @Step("Generate random user")
    public static UserDataUpdateLombok getUpdatedRandomUser() {
        String email = RandomStringUtils.randomAlphabetic(8) + "@gmail.com"; // Генерация email
        String name = RandomStringUtils.randomAlphabetic(8);

        return new UserDataUpdateLombok(email, name);
    }
}