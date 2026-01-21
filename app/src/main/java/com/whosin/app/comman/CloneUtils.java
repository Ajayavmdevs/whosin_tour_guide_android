package com.whosin.app.comman;

import java.lang.reflect.Field;

public class CloneUtils {
    public static <T> T cloneObject(T original) {
        try {
            T copy = (T) original.getClass().getDeclaredConstructor().newInstance();
            for (Field field : original.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                field.set(copy, field.get(original)); // shallow copy
            }
            return copy;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
