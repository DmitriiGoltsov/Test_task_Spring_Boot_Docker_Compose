package com.goltsov.test_task.test_task.util;

import org.springframework.stereotype.Component;

@Component
public class NamePaths {

    private static final String USERS_PATH = "/users";
    private static final String LOGIN_PATH = "/login";
    private static final String STATUSES_PATH = "/statuses";
    private static final String TASKS_PATH = "/tasks";
    private static final String COMMENTARIES_PATH = "/commentaries";

    public static String getCommentaryPath() {
        return COMMENTARIES_PATH;
    }

    public static String getUsersPath() {
        return USERS_PATH;
    }
    public static String getLoginPath() {
        return LOGIN_PATH;
    }
    public static String getStatusesPath() {
        return STATUSES_PATH;
    }
    public static String getTasksPath() {
        return TASKS_PATH;
    }
}
