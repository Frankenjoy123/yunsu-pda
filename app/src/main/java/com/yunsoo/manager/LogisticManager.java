package com.yunsoo.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.yunsoo.entity.AuthUser;
import com.yunsoo.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class LogisticManager extends BaseManager {

    private static LogisticManager logisticManager;

    private List<Map<Integer,String>> actionList;

    private List<String> organizationAgencyList;

    public static LogisticManager initializeInstance(Context context) {

        if (logisticManager == null) {
            synchronized (LogisticManager.class) {
                if (logisticManager == null) {
                    logisticManager = new LogisticManager();
                    logisticManager.context = context;
                }
            }
        }
        return logisticManager;
    }

    public LogisticManager() {
        this.actionList = new ArrayList<>();
        this.organizationAgencyList=new ArrayList<>();
    }

    public List<Map<Integer, String>> getActionList() {
        if (actionList==null||actionList.size()<1){
            actionList=new ArrayList<>();
            Map<Integer, String> map1=new HashMap();
            map1.put(100,"入库");
            Map<Integer, String> map2=new HashMap();
            map2.put(200,"出库");
            actionList.add(map1);
            actionList.add(map2);
        }
        return actionList;
    }

    public List<String> getOrganizationAgencyList() {
        return organizationAgencyList;
    }

    public void saveLogisticAction(JSONObject object){
        JSONArray array=object.optJSONArray("array");
        JSONArray recordArray=new JSONArray();
        actionList.clear();
        for (int i=0;i<array.length();i++){
            JSONObject actionObject=array.optJSONObject(i);
            int id=actionObject.optInt("id");
            JSONObject recordObject=new JSONObject();
            try {
                recordObject.put("id",id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String name=actionObject.optString("name");
            try {
                recordObject.put("name",name);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Map map=new HashMap();
            map.put(id,name);
            actionList.add(map);
            recordArray.put(recordObject);
        }

        Editor editor = context.getSharedPreferences(Constants.Preference.PREF_LOGISTIC, Context.MODE_PRIVATE).edit();
        editor.putString(Constants.Preference.LOGISTIC_ACTION, recordArray.toString());
        editor.commit();
    }

    public void saveOrganizationAgency(JSONObject object){
        JSONArray array=object.optJSONArray("array");
        JSONArray recordArray=new JSONArray();
        organizationAgencyList.clear();
        for (int i=0;i<array.length();i++){
            JSONObject orgAgencyObject=array.optJSONObject(i);
            JSONObject recordObject=new JSONObject();
            String name=orgAgencyObject.optString("name");
            try {
                recordObject.put("name",name);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            organizationAgencyList.add(name);
            recordArray.put(recordObject);
        }

        Editor editor = context.getSharedPreferences(Constants.Preference.PREF_LOGISTIC, Context.MODE_PRIVATE).edit();
        editor.putString(Constants.Preference.LOGISTIC_ORG_AGENCY, recordArray.toString());
        editor.commit();
    }


    public static LogisticManager getInstance() {
        if (logisticManager == null) {
            Log.w("LogisticManager", "LogisticManager instance has not been initialized");
        }
        return logisticManager;
    }

    public void restore() {
        actionList.clear();

        SharedPreferences preferences = context.getSharedPreferences(Constants.Preference.PREF_LOGISTIC,
                Context.MODE_PRIVATE);

        String actionInfo = preferences.getString(Constants.Preference.LOGISTIC_ACTION, null);
        if (actionInfo!=null){
            try {
                JSONArray recordArray=new JSONArray(actionInfo);
                for (int i=0;i<recordArray.length();i++){
                    JSONObject actionObject=recordArray.optJSONObject(i);
                    int id=actionObject.optInt("id");
                    String name=actionObject.optString("name");
                    Map map=new HashMap();
                    map.put(id,name);
                    actionList.add(map);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        String orgAgencyInfo = preferences.getString(Constants.Preference.LOGISTIC_ORG_AGENCY, null);
        if (orgAgencyInfo!=null){
            try {
                JSONArray recordArray=new JSONArray(orgAgencyInfo);
                for (int i=0;i<recordArray.length();i++){
                    JSONObject orgAgencyObject=recordArray.optJSONObject(i);
                    String name=orgAgencyObject.optString("name");
                    organizationAgencyList.add(name);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
