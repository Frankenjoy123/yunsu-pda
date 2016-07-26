package com.yunsoo.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.yunsoo.entity.AuthUser;
import com.yunsoo.entity.OrgAgency;
import com.yunsoo.sqlite.MyDataBaseHelper;
import com.yunsoo.sqlite.SQLiteOperation;
import com.yunsoo.util.Constants;
import com.yunsoo.util.YSFile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class LogisticManager extends BaseManager {

    private static LogisticManager logisticManager;
    private List<Map<String,String>> actionList;

    private List<OrgAgency> agencies;

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
        this.agencies=new ArrayList<>();
    }

    public List<Map<String, String>> getActionList() {
        if (actionList==null||actionList.size()<1){
            actionList=new ArrayList<>();
            Map<String, String> map1=new HashMap();
            map1.put(Constants.Logistic.INBOUND_CODE,Constants.Logistic.INBOUND);
            Map<String, String> map2=new HashMap();
            map2.put(Constants.Logistic.OUTBOUND_CODE,Constants.Logistic.OUTBOUND);
            actionList.add(map1);
            actionList.add(map2);
        }
        return actionList;
    }


    public List<OrgAgency> getAgencies() {
        return agencies;
    }

    public void saveLogisticAction(JSONObject object){
        Log.d("Org",object.toString());
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
        agencies.clear();
        for (int i=0;i<array.length();i++){
            JSONObject orgAgencyObject=array.optJSONObject(i);
            JSONObject recordObject=new JSONObject();
            String id=orgAgencyObject.optString("id");
            String name=orgAgencyObject.optString("name");
            try {
                recordObject.put("id",id);
                recordObject.put("name",name);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            OrgAgency agency=new OrgAgency();
            agency.setId(id);
            agency.setName(name);
            agencies.add(agency);
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
                    String id=orgAgencyObject.optString("id");
                    String name=orgAgencyObject.optString("name");
                    OrgAgency agency=new OrgAgency();
                    agency.setId(id);
                    agency.setName(name);
                    agencies.add(agency);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static void createLogisticFile(Context context) {
        MyDataBaseHelper dataBaseHelper = new MyDataBaseHelper(context, Constants.SQ_DATABASE, null, 1);
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        List<String> actionList = SQLiteOperation.queryDistinctAction(db);
        List<String> agencyList = SQLiteOperation.queryDistinctAgency(db);
        List<String> keyList=null;
        if (actionList!=null&&actionList.contains(Constants.Logistic.INBOUND_CODE)){
            int index=0;
            do {
                keyList=SQLiteOperation.queryPackKeyByAction(db,Constants.Logistic.INBOUND_CODE,String.valueOf(index*Constants.Logistic.LIMIT_ITEM));
                buildYSFile(db,Constants.Logistic.INBOUND_CODE,keyList);
                index++;
            }while (keyList.size()<Constants.Logistic.LIMIT_ITEM);
        }

        if (actionList!=null&&actionList.contains(Constants.Logistic.OUTBOUND_CODE)
                &&agencyList!=null&&agencyList.size()>0){
            for (int j=0;j<agencyList.size();j++){
                int index=0;
                do {
                    keyList=SQLiteOperation.queryPackKeyByActionAndAgency(db,Constants.Logistic.OUTBOUND_CODE,agencyList.get(j),String.valueOf(index*Constants.Logistic.LIMIT_ITEM));
                    buildYSFile(db,Constants.Logistic.OUTBOUND_CODE,agencyList.get(j),keyList);
                    index++;
                }while (keyList.size()<Constants.Logistic.LIMIT_ITEM);
            }
        }

        dataBaseHelper.close();
    }


    private static void buildYSFile(SQLiteDatabase db,String actionId,String agencyId,List<String> keyList){
        if (actionId!=null&&keyList!=null&&keyList.size()>0){
            YSFile ysFile=new YSFile(YSFile.EXT_TF);
            ysFile.putHeader("file_type","trace");
            ysFile.putHeader("org_id",SessionManager.getInstance().getAuthUser().getOrgId());
            ysFile.putHeader("count", String.valueOf(keyList.size()));
            ysFile.putHeader("action",actionId);
            if (actionId.equals(Constants.Logistic.INBOUND_CODE)){
                ysFile.putHeader("source","storage_name");
                ysFile.putHeader("storage_name","default");
            }else if (actionId.equals(Constants.Logistic.OUTBOUND_CODE)){
                if (agencyId!=null){
                    ysFile.putHeader("source","agent_id");
                    ysFile.putHeader("agent_id",agencyId);
                }
            }
            SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            Date date=new Date();
            ysFile.putHeader("date",dateFormat.format(date));

            StringBuilder builder=new StringBuilder();
            for(String key : keyList){
                builder.append(key);
                builder.append("\r\n");
//                SQLiteOperation.updatePathData(db,key,Constants.DB.SYNC);
            }
            SQLiteOperation.batchUpdateStatus(db,keyList,actionId,Constants.DB.SYNC);
            ysFile.setContent(builder.toString().getBytes(Charset.forName("UTF-8")));

            try {

                String folderName = android.os.Environment.getExternalStorageDirectory() +
                        Constants.YUNSOO_FOLDERNAME+Constants.PATH_SYNC_TASK_FOLDER;
                File path_task_folder = new File(folderName);
                if (!path_task_folder.exists())
                    path_task_folder.mkdirs();

                StringBuilder fileNameBuilder=new StringBuilder("Path_");
                fileNameBuilder.append(actionId);
                fileNameBuilder.append("_");
                fileNameBuilder.append(DeviceManager.getInstance().getDeviceId());
                fileNameBuilder.append("_");
                fileNameBuilder.append(FileManager.getInstance().getPathFileLastIndex() + 1);
                fileNameBuilder.append(".tf");

                File file=new File(path_task_folder,fileNameBuilder.toString());
                FileOutputStream fos = new FileOutputStream(file);
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                bos.write(ysFile.toBytes());
                bos.flush();
                bos.close();
                fos.close();

                FileManager.getInstance().savePathFileIndex(FileManager.getInstance().getPathFileLastIndex() + 1);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private static void buildYSFile(SQLiteDatabase db,String actionId,List<String> keyList){
        buildYSFile(db,actionId,null,keyList);
    }
}
