package com.whosin.app.comman;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.whosin.app.service.models.StorySeenEventModel;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class Preferences {

    public static Preferences shared = Preferences.shared();

    private static volatile Preferences _instance = null;

    synchronized private static Preferences shared() {
        if (_instance == null) {
            _instance = new Preferences();
        }
        return _instance;
    }

    private SharedPreferences pref = null;
    private Context context;

    public Preferences() { }

    public void setContext(Context context){
        this.context = context;
        pref = Utils.getSharedPreferences(context);
    }

    public SharedPreferences getPref() {
        if (pref == null) {
            pref = Utils.getSharedPreferences(getContext());
        }
        return pref;
    }

    public Context getContext() {
        if (context == null) {
            return Graphics.context;
        }
        return context;
    }

    public void setString(String key, String value) {
        SharedPreferences.Editor editor = getPref().edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(String key) {
        return getPref().getString(key, "");
    }

    public void setDouble(String key, double value) {
        SharedPreferences.Editor editor = getPref().edit();
        editor.putString(key, value + "");
        editor.apply();
    }

    public Double getDouble(String key) {
        if (getPref().getString(key, "").length() > 0) {
            return Double.parseDouble(getPref().getString(key, ""));
        } else {
            return null;
        }
    }

    public void setBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = getPref().edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean getBoolean(String key) {
        return getPref().getBoolean(key, false);
    }

    public void setInt(String key, int value) {
        SharedPreferences.Editor editor = getPref().edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public int getInt(String key) {
        return getPref().getInt(key, 0);
    }

    public void setLong(String key, long value) {
        SharedPreferences.Editor editor = getPref().edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public long getLong(String key) {
        return getPref().getLong(key, 0);
    }

    public void setList(String key, List<String> value) {
        SharedPreferences.Editor editor = getPref().edit();
        editor.putString(key, TextUtils.join(", ", value));
        editor.apply();
    }

    public void addListItem(String key, String value) {
        SharedPreferences.Editor editor = getPref().edit();
        List<String> list = getList(key);
        if(!list.contains(value)) {
            if (key.equals("story_seen")) {
                EventBus.getDefault().post(new StorySeenEventModel(value));
            }
            list.add(value);
        }
        editor.putString(key, TextUtils.join(",", list));
        editor.apply();
    }

    public boolean containValueInList(String key, String value) {
        return getList(key).contains(value);
    }

    public List<String> getList(String key) {
        String json = getPref().getString(key, "");
        return new ArrayList<>(Arrays.asList(json.split(",")));
    }

    public boolean isExist(String key) {
        return getPref().contains(key);
    }

    public void increaseCount(String key) {
        int counts = getInt(key);
        setInt(key,counts + 1);
    }

    public Set<String> getAllKeys() {
        return getPref().getAll().keySet();
    }

    public void removeKey(String key) {
        SharedPreferences.Editor editor = getPref().edit();
        editor.remove(key);
        editor.apply();
    }

    public void clearData() {
//        getPref().edit().clear().apply();
        getPref().edit().clear().commit();
    }
}
