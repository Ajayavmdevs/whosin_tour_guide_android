package com.whosin.business.service.manager;

import androidx.annotation.NonNull;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.whosin.business.comman.Preferences;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SearchSuggestionStore {

    private static final String BASE_KEY = "com.whosin.business.searchSuggestion";

    @NonNull
    public static SearchSuggestionStore shared = getInstance();

    private static SearchSuggestionStore instance = null;

    @NonNull
    private static synchronized SearchSuggestionStore getInstance() {
        if (instance == null) {
            instance = new SearchSuggestionStore();
        }
        return instance;
    }

    private String storageKey(String key) {
        return BASE_KEY + "." + key.toLowerCase();
    }

    // Save suggestions for a key
    public static void save(String key, List<String> suggestions) {
        if (key == null || key.isEmpty()) return;
        Preferences.shared.setString(shared.storageKey(key), new Gson().toJson(suggestions));
    }

    // Retrieve suggestions for a key
    public static List<String> get(String key) {
        if (key == null || key.isEmpty()) return new ArrayList<>();

        List<String> suggestions = getListFromPreferences(shared.storageKey(key));
        if (!suggestions.isEmpty()) {
            return suggestions;
        }

        // Fallback: return filtered list from all suggestions
        List<String> allSuggestions = getAllSuggestions();
        List<String> filtered = new ArrayList<>();
        for (String item : allSuggestions) {
            if (item.toLowerCase().contains(key.toLowerCase())) {
                filtered.add(item);
            }
        }
        return filtered;
    }

    // Remove suggestions for a specific key
    public static void remove(String key) {
        if (key == null || key.isEmpty()) return;
        Preferences.shared.setString(shared.storageKey(key), null);
    }

    // Clear all suggestions
    public static void clearAll() {
        Set<String> allKeys = Preferences.shared.getAllKeys();
        for (String key : allKeys) {
            if (key.startsWith(BASE_KEY)) {
                Preferences.shared.setString(key, null);
            }
        }
    }

    // Get all suggestions across all stored keys
    public static List<String> getAllSuggestions() {
        Set<String> keys = Preferences.shared.getAllKeys();
        Set<String> result = new HashSet<>();

        for (String key : keys) {
            if (key.startsWith(BASE_KEY)) {
                List<String> list = getListFromPreferences(key);
                if (list != null) {
                    result.addAll(list);
                }
            }
        }

        return new ArrayList<>(result);
    }

    // Helper to parse JSON list from preferences
    private static List<String> getListFromPreferences(String key) {
        String json = Preferences.shared.getString(key);
        if (json == null) return new ArrayList<>();

        Type type = new TypeToken<List<String>>() {}.getType();
        List<String> list = new Gson().fromJson(json, type);
        return list != null ? list : new ArrayList<>();
    }
}
