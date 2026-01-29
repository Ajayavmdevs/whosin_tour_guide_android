package com.whosin.business.comman;

import android.os.AsyncTask;
import android.util.Log;

import com.whosin.business.comman.interfaces.CommanCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class IPInfoTask extends AsyncTask<Void, Void, String> {

    private static final String TAG = "IPInfoTask";
    private static final String API_URL = "https://ipinfo.io/json";
    private CommanCallback<String> callback;

    public IPInfoTask(CommanCallback<String> callback) {
        this.callback = callback;
    }

    @Override
    protected String doInBackground(Void... voids) {
        StringBuilder result = new StringBuilder();
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        try {
            URL url = new URL(API_URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.connect();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
            } else {
                Log.e(TAG, "HTTP error code: " + responseCode);
            }
        } catch (IOException e) {
            Log.e(TAG, "Error fetching data", e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result.toString();
    }

    @Override
    protected void onPostExecute(String result) {
        try {
            JSONObject json = new JSONObject(result);
            String city = json.getString("city");
            String country = json.getString("country");
            String address = city + ", " + country;
            Log.d(TAG, "Address: " + address);
            if (callback != null) {
                callback.onReceive(address);
            }
        } catch (JSONException e) {
            callback.onReceive("Not found");
        }
    }
}

