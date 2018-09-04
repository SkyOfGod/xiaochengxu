package com.cailanzi.utils;


import com.cailanzi.pojo.entities.User;

//转发前后的线程不一样
public class UserThreadLocal {

    private static final ThreadLocal<User> USER = new ThreadLocal<User>();

    public static void set(User user) {
        USER.set(user);
    }

    public static User get() {
        return USER.get();
    }

    public static Integer getUserId() {
        return USER.get().getId();
    }

}
