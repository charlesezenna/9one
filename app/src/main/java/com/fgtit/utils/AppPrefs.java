package com.fgtit.utils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.fgtit.app.AppClass;
import com.fgtit.data.UserItem;
import com.fgtit.network.DeptsResponse;
import com.fgtit.network.StatesResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.fgtit.service.ConnectService.TAG;

public class AppPrefs {
    private static AppPrefs appPrefs;
    private static final String LIST_OF_USERS = "LIST_OF_USERS";
    private static final String LIST_OF_FACULTY_DEPTS = "LIST_OF_FACULTY_DEPTS";
    private static final String SCHOOL = "SCHOOL";
    private static final String LIST_OF_LGAS_STATES = "LIST_OF_LGAS_STATES";
    private static final String nevsid_key = "nevsid";
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    private AppPrefs() {
        pref = PreferenceManager.getDefaultSharedPreferences(AppClass.getInstance());
        editor = pref.edit();

    }

    public static AppPrefs get() {
        if (appPrefs == null)
            appPrefs = new AppPrefs();
        return appPrefs;
    }

    public void setNevsid(String nevsid) {
        editor.putString(nevsid_key, nevsid);
        editor.apply();
    }

    public String getNevsid() {
        return pref.getString(nevsid_key, "4");
    }

    public void setSchool(String school) {
        editor.putString(SCHOOL, school);
        editor.apply();
    }

    public String getSchool() {
        return pref.getString(SCHOOL, "");
    }

    public void saveUsers(List<UserItem> userItems) {
        editor.putString(LIST_OF_USERS, new Gson().toJson(userItems));
        editor.apply();
    }

    public List<UserItem> getUsers() {
        String json = pref.getString(LIST_OF_USERS, "");
        if (json == null || json.isEmpty())
            return new ArrayList<UserItem>();
        Type listType = new TypeToken<List<UserItem>>() {
        }.getType();
        return new Gson().fromJson(json, listType);
    }

    public void saveLgaStates(List<StatesResponse> stateResponses) {
        editor.putString(LIST_OF_LGAS_STATES, new Gson().toJson(stateResponses));
        editor.apply();
    }

    public List<StatesResponse> getStateLgas() {
        String json = pref.getString(LIST_OF_LGAS_STATES, "");
        Log.d(TAG, json);
        if (json == null || json.isEmpty())
            return new ArrayList<StatesResponse>();
        Type listType = new TypeToken<List<StatesResponse>>() {
        }.getType();
        return new Gson().fromJson(json, listType);
    }

    public void saveFacultyDeps(List<DeptsResponse> deptsResponses) {
        editor.putString(LIST_OF_FACULTY_DEPTS, new Gson().toJson(deptsResponses));
        editor.apply();
    }

    public List<DeptsResponse> getFacultyDeps() {
        String json = pref.getString(LIST_OF_FACULTY_DEPTS, "");
        if (json == null || json.isEmpty())
            return new ArrayList<DeptsResponse>();
        Type listType = new TypeToken<List<DeptsResponse>>() {
        }.getType();
        return new Gson().fromJson(json, listType);
    }

    public <T> void saveAsJson(T data, Class<T> clazz) {
        editor.putString(clazz.getSimpleName(), new Gson().toJson(data));
        editor.apply();
    }

    public <T> T fromJson(Class<T> clazz) {
        String json = pref.getString(clazz.getSimpleName(), "");
        return new Gson().fromJson(json, clazz);
    }
}