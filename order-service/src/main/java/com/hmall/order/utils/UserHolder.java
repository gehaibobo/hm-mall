package com.hmall.order.utils;

public class UserHolder {
    public static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setUser(Long userId) {
        threadLocal.set(userId);
    }

    public static Long getUser() {
        return threadLocal.get();
    }

    public static void removeUser() {
        threadLocal.remove();
    }
}
