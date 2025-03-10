package ru.practikum.yandex.api;

public class Endpoints {
    /**
     * URI для создания заказа
     */
    public static final String CREATE_ORDER_URI = "/api/orders";
    /**
     * URI для получения данных об ингредиентах
     */
    public static final String GET_DATA_INGREDIENTS_URI = "/api/ingredients";
    /**
     * URI Стартовой страницы
     */
    public static final String BASE_URI = "https://stellarburgers.nomoreparties.site";
    /**
     * URI для создания пользователя
     */
    public static final String CREATE_USER_URI = "/api/auth/register";
    /**
     * URI для авторизации пользователя
     */
    public static final String LOGIN_USER_URI = "/api/auth/login";
    /**
     * URI для получения, обновления и удаления пользователя
     */
    public static final String GET_CHANGE_DELETE_DATA_USER_URI = "/api/auth/user";
    /**
     * URI для выхода из системы
     */
    public static final String LOGOUT_USER_URI = "/api/auth/logout";
    }
