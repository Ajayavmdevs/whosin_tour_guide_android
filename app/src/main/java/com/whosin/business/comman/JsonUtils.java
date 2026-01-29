package com.whosin.business.comman;

import com.google.gson.Gson;
import java.lang.reflect.Type;

public class JsonUtils {

    public interface JsonCallback<T> {
        void onSuccess(T result);
    }

    public static <T> void parseJsonInBackground(final String jsonString, final Type typeOfT, final JsonCallback<T> callback) {
        new Thread(() -> {
            try {
                Gson gson = new Gson();
                T result = gson.fromJson(jsonString, typeOfT);
                callback.onSuccess(result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }


}

