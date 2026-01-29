package com.whosin.business.service.manager;

import android.content.Context;
import android.content.res.Configuration;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.whosin.business.R;
import com.whosin.business.comman.Utils;
import com.whosin.business.service.DataService;
import com.whosin.business.service.models.ContainerModel;
import com.whosin.business.service.rest.RestCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

public class TranslationManager {

    @NonNull
    public static TranslationManager shared = TranslationManager.getInstance();

    private static TranslationManager instance = null;

    private Context context;

    private final Map<String, Map<String,String>> responsMap = new HashMap<>();

    private final Map<String, String> translations = new HashMap<>();

    private final Map<String, String> engTranslations = new HashMap<>();

    private final Map<String, String> localTranslations = new HashMap<>();


    // --------------------------------------
    // region Singleton
    // --------------------------------------

    @NonNull
    private static synchronized TranslationManager getInstance() {
        if (instance == null) {
            instance = new TranslationManager();
        }
        return instance;
    }

    // --------------------------------------
    // region Public
    // --------------------------------------

    public void changeLang() {
        String lang = Utils.getLang();

        loadEngFromRaw(context, R.raw.en);
        loadLocalJsonFile();

        Map<String, Map<String, String>> sessionData = SessionManager.shared.getLangData();
        if (sessionData != null && !sessionData.isEmpty()) {
            responsMap.clear();
            responsMap.putAll(sessionData);
        }

        if (!responsMap.isEmpty() && !TextUtils.isEmpty(lang)) {
            translations.clear();

            Map<String, String> tmpLang = responsMap.get(lang);

            if (tmpLang != null && !tmpLang.isEmpty()) {
                translations.putAll(tmpLang);
            }
        }

        setLocale(lang);
    }

    public void setLocale(String langCode) {
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.setLocale(locale);

        context.getResources().updateConfiguration(
                config,
                context.getResources().getDisplayMetrics()
        );
    }

    public static Context updateLocale(Context context) {
        String currentLang = Utils.getLang();
        Locale locale = new Locale(currentLang);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.setLocale(locale);

        return context.createConfigurationContext(config);
    }

    //Load local json file
    public void loadLocalJsonFile() {
        int rawResId = 0;
        String lang = Utils.getLang();
        if (!TextUtils.isEmpty(lang)) {
            switch (lang) {
                case "hi":
                    rawResId = R.raw.hi;
                    break;
                case "ar":
                    rawResId = R.raw.ar;
                    break;
                case "de":
                    rawResId = R.raw.de;
                    break;
                case "zh":
                    rawResId = R.raw.zh;
                    break;
                case "ru":
                    rawResId = R.raw.ru;
                    break;
                default:
                    rawResId = R.raw.en;
                    break;
            }
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(context.getResources().openRawResource(rawResId))
        )) {
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            localTranslations.clear();

            JSONObject jsonObject = new JSONObject(removeDuplicateKeys(builder.toString()));
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                localTranslations.put(key, jsonObject.getString(key));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    //Load local Eng json file
    public void loadEnglishTranslations(String jsonString) {
        engTranslations.clear();
        try {
            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, String>>() {}.getType();

            // Parse JSON into Map (duplicates automatically resolved, last one wins)
            Map<String, String> map = gson.fromJson(jsonString, type);

            if (map != null) {
                engTranslations.putAll(map);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadEngFromRaw(Context context, int rawResId) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(context.getResources().openRawResource(rawResId))
        )) {
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            loadEnglishTranslations(removeDuplicateKeys(builder.toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Remove DuplicateKeys from json
    private String removeDuplicateKeys(String jsonString) {
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
        JsonObject cleanObject = new JsonObject();

        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            cleanObject.add(entry.getKey(), entry.getValue()); // keeps last
        }

        return cleanObject.toString();
    }


    // Get value by key
    public String get(String key, String defaultValue) {
        if (translations.containsKey(key)) {
            return translations.get(key);
        }else if (localTranslations.containsKey(key)) {
            return localTranslations.get(key);
        } else if (engTranslations.containsKey(key)) {
            return engTranslations.get(key);
        } else {
            if (key != null && !key.isEmpty()) {
                String formatted = key.replace("_", " ");
                formatted = formatted.substring(0, 1).toUpperCase() + formatted.substring(1);
                return formatted;
            }
            return defaultValue;
        }
    }


    // Shortcut if no default is needed
    public String get(String key) {
        return get(key, key);
    }


    // endregion
    // --------------------------------------
    // region Data Service
    // --------------------------------------


    public void reequestCommanLang() {
        changeLang();
        DataService.shared(context).requestCommanLang(new RestCallback<ContainerModel<Map<String, Map<String, String>>>>(null) {
            @Override
            public void result(ContainerModel<Map<String, Map<String, String>>> model, String error) {
                try {
                    if (!Utils.isNullOrEmpty(error) || model == null) {
                        Toast.makeText(context,
                                Utils.isNullOrEmpty(error) ? "Unknown error" : error,
                                Toast.LENGTH_SHORT
                        ).show();
                        return;
                    }

                    if (model.getData() == null) {
                        Toast.makeText(context, "No data found in response", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (model.getData().isEmpty()) {
                        Toast.makeText(context, "Empty language data", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    responsMap.clear();
                    responsMap.putAll(model.getData());
                    SessionManager.shared.saveLangData(model.getData());
                    changeLang();


                } catch (Exception e) {
                    Toast.makeText(context, "Parsing error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    // endregion
    // --------------------------------------


    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void clearManager() {
        responsMap.clear();
        translations.clear();
        localTranslations.clear();
    }
}
