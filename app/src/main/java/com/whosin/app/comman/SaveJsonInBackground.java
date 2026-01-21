package com.whosin.app.comman;

import android.os.AsyncTask;
import com.google.gson.Gson;
import java.util.Map;


public class SaveJsonInBackground<T> extends AsyncTask<Map.Entry<String, T>, Void, String> {

    @Override
    protected String doInBackground(Map.Entry<String, T>... entries) {
        Gson gson = new Gson();
        Map.Entry<String, T> entry = entries[0];
        String json = gson.toJson(entry.getValue());
        String key = entry.getKey();
        Preferences.shared.setString(key, json);
        return key;
    }

    @Override
    protected void onPostExecute(String key) {
    }

}
