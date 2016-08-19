package com.yunsu.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.yunsu.entity.OrgAgency;
import com.yunsu.sqlite.service.PackService;
import com.yunsu.sqlite.service.impl.PackServiceImpl;
import com.yunsu.common.manager.DeviceManager;
import com.yunsu.common.util.Constants;
import com.yunsu.common.manager.BaseManager;
import com.yunsu.common.manager.SessionManager;
import com.yunsu.common.util.YSFile;
import com.yunsu.greendao.entity.Pack;

import org.apache.log4j.Logger;
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

    private PackService packService;

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
        packService=new PackServiceImpl();
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
                Logger logger=Logger.getLogger(LogisticManager.class);
                logger.error(e.getMessage());
                logger.error(e.toString());
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
                Logger logger=Logger.getLogger(LogisticManager.class);
                logger.error(e.getMessage());
                logger.error(e.toString());
                e.printStackTrace();
            }
        }
    }

    public  void createLogisticFile() {
        List<String> actionList = packService.queryDistinctAction();
        for (String action :actionList){
            switch (action){

                case Constants.Logistic.INBOUND_CODE:
                case Constants.Logistic.REVOKE_INBOUND_CODE:
                    List<Pack> resultPackList=null;
                    int index=0;
                    Pack queryPack=new Pack();
                    queryPack.setStatus(Constants.DB.NOT_SYNC);
                    queryPack.setActionId(action);
                    do {
                        resultPackList=packService.queryPackListByActionStatus(queryPack,index*Constants.Logistic.LIMIT_ITEM);
                        buildYSFile(resultPackList);
                        index++;
                    }while (resultPackList!=null&&resultPackList.size()==Constants.Logistic.LIMIT_ITEM);
                    break;

                case Constants.Logistic.OUTBOUND_CODE:
                case Constants.Logistic.REVOKE_OUTBOUND_CODE:
                    List<String> agencyList = packService.queryDistinctAgency(action);
                    if (agencyList!=null&&agencyList.size()>0){
                        for (int j=0;j<agencyList.size();j++){
                            index=0;
                            queryPack=new Pack();
                            queryPack.setStatus(Constants.DB.NOT_SYNC);
                            queryPack.setActionId(action);
                            queryPack.setAgency(agencyList.get(j));
                            do {
                                resultPackList=packService.queryPackKeyByActionAgencyStatus(queryPack,index*Constants.Logistic.LIMIT_ITEM);
                                buildYSFile(resultPackList);
                                index++;
                            }while (resultPackList!=null&&resultPackList.size()==Constants.Logistic.LIMIT_ITEM);
                        }
                    }
                    break;
            }
        }






    }


    public  void buildYSFile(List<Pack> packList){
        if (packList!=null&&packList.size()>0){
            String actionId=packList.get(0).getActionId();
           YSFile ysFile=  buildYunsuFileDetail(packList);
            String folderName = android.os.Environment.getExternalStorageDirectory() +
                    Constants.YUNSOO_FOLDERNAME+Constants.PATH_SYNC_TASK_FOLDER;
            File path_task_folder = new File(folderName);
            createFileByYunsuFile(path_task_folder,ysFile,actionId);
        }
    }

    /**
     * 创建YSFile
     * @param packList
     * @return
     */
    private YSFile buildYunsuFileDetail(List<Pack> packList){
        YSFile ysFile=new YSFile(YSFile.EXT_TF);
        ysFile.putHeader("file_type","trace");
        ysFile.putHeader("org_id", SessionManager.getInstance().getAuthUser().getOrgId());
        ysFile.putHeader("count", String.valueOf(packList.size()));
        String actionId=packList.get(0).getActionId();
        String agencyId=packList.get(0).getAgency();
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
        for(Pack pack : packList){
            builder.append(pack.getPackKey());
            builder.append("\r\n");
        }
        packService.batchUpdateStatus(packList,Constants.DB.SYNC);
        ysFile.setContent(builder.toString().getBytes(Charset.forName("UTF-8")));
        return ysFile;
    }

    /**
     * 根据YSFile生成文件
     * @param path_task_folder
     * @param ysFile
     * @param actionId
     */
    private void createFileByYunsuFile(File path_task_folder,YSFile ysFile,String actionId){
        try {

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
            Logger logger=Logger.getLogger(LogisticManager.class);
            logger.error(ex.getMessage());
            logger.error(ex.toString());
            ex.printStackTrace();
        }
    }


    /**
     * 将准备清理的数据库数据以文件的形式存储
     * @param packList
     */
    public void catheDbWithFile(List<Pack> packList){
        if (packList!=null&&packList.size()>0){
            StringBuilder builder=new StringBuilder();
            for (Pack pack:packList){
                builder.append(pack.getId());
                builder.append(",");
                builder.append(pack.getPackKey());
                builder.append(",");
                builder.append(pack.getActionId());
                builder.append(",");
                builder.append(pack.getAgency());
                builder.append(",");
                builder.append(pack.getStatus());
                builder.append(",");
                builder.append(pack.getSaveTime());
                builder.append("\r\n");
            }
            SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd'T'HHmmss-SSS'Z'");
            String folderName = android.os.Environment.getExternalStorageDirectory() +
                    Constants.YUNSOO_FOLDERNAME+Constants.PATH_CACHE_DB;
            File path_cache_folder = new File(folderName);
            try {
                if (!path_cache_folder.exists())
                    path_cache_folder.mkdirs();
                File file=new File(path_cache_folder,dateFormat.format(new Date()));
                FileOutputStream fos = new FileOutputStream(file);
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                bos.write(builder.toString().getBytes());
                bos.flush();
                bos.close();
                fos.close();
            } catch (Exception ex) {
                Logger logger=Logger.getLogger(LogisticManager.class);
                logger.error(ex.getMessage());
                logger.error(ex.toString());
                ex.printStackTrace();
            }
        }
    }


}
