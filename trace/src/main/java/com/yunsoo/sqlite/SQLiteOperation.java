package com.yunsoo.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yunsoo.entity.OrgAgency;
import com.yunsoo.util.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Frank zhou on 2015/7/14.
 */
public class SQLiteOperation {
    public static void insertPackData(SQLiteDatabase db,String pack_key,String product_keys,String time){
        db.execSQL("insert into pack values(null,?,?,?)",new Object[]{pack_key,product_keys,time});
    }
    public static void insertPathData(SQLiteDatabase db,String pack_key,String action_id,String time){
        db.execSQL("insert into path values(null,?,?,?,?,?)",new String[]{pack_key,action_id,null,null,time});
    }
    public static void insertPathData(SQLiteDatabase db,String pack_key,String action_id,String agency,String time){
        db.execSQL("insert into path values(null,?,?,?,?,?)",new String[]{pack_key,action_id,agency,null,time});
    }
    public static void insertPathData(SQLiteDatabase db,String pack_key,String action_id,String agency,String status,String time){
        List<String> keyList=queryKey(db,pack_key,null,null,Constants.DB.NOT_SYNC);
        if (keyList!=null && keyList.size()>0){
            checkNotSyncDate(db,pack_key,action_id,agency,time);
        }else {
            db.execSQL("insert into path values(null,?,?,?,?,?)",new String[]{pack_key,action_id,agency,status,time});
        }
    }

    public static void checkNotSyncDate(SQLiteDatabase db,String pack_key,String action_id,String agency,String time){
        ContentValues values=new ContentValues();
        values.put(Constants.DB.ACTION_ID_COLUMN,action_id);
        values.put(Constants.DB.AGENCY_COLUMN,agency);
        values.put(Constants.DB.TIME_COLUMN,time);
        db.update(Constants.DB.PATH_TABLE,values,"pack_key=? and status=?",new String[]{pack_key,Constants.DB.NOT_SYNC});

    }

    public static void updatePathData(SQLiteDatabase db,String pack_key,String status){
        updatePathData(db,pack_key,null,null,status,null);
    }

    public static void updatePathData(SQLiteDatabase db,String pack_key,String action_id,String agency,String status,String time){
        ContentValues contentValues=new ContentValues();
        if(pack_key!=null){
            contentValues.put(Constants.DB.PACK_KEY_COLUMN,pack_key);
        }
        if(action_id!=null){
            contentValues.put(Constants.DB.ACTION_ID_COLUMN,action_id);
        }
        if(agency!=null){
            contentValues.put(Constants.DB.AGENCY_COLUMN,agency);
        }
        if (status!=null){
            contentValues.put(Constants.DB.STATUS_COLUMN,status);
        }
        if (time!=null){
            contentValues.put(Constants.DB.TIME_COLUMN,time);
        }
        db.update(Constants.DB.PATH_TABLE, contentValues, Constants.DB.PACK_KEY_COLUMN+" = ?", new String[]{pack_key});
    }

    public static List<String>  queryKey(SQLiteDatabase db,String pack_key,String action_id,String agency,String status){

        StringBuilder builder=new StringBuilder();
        List<String> args=new ArrayList<>();
        String[] argArr = new String[0];
        if (pack_key!=null){
            builder.append(Constants.DB.PACK_KEY_COLUMN+" =? "+" AND ");
            args.add(pack_key);
        }
        if (action_id!=null){
            builder.append(Constants.DB.ACTION_ID_COLUMN+" =? "+" AND ");
            args.add(action_id);
        }
        if (agency!=null){
            builder.append(Constants.DB.AGENCY_COLUMN+" =? "+" AND ");
            args.add(agency);
        }
        if (status!=null){
            builder.append(Constants.DB.STATUS_COLUMN+" =? ");
            args.add(status);
        }
        if (args.size()>0){
            argArr=new String[args.size()];
            for(int i=0;i<args.size();i++){
                argArr[i]=args.get(i);
            }
        }
        Cursor cursor=db.query(true, Constants.DB.PATH_TABLE,new String[]{Constants.DB.PACK_KEY_COLUMN},
                builder.toString(),argArr,null,null,null,null);
        List<String> keyList= new ArrayList<>();
        if (cursor!=null){
            while(cursor.moveToNext()){
                String key = cursor.getString(cursor.getColumnIndex(Constants.DB.PACK_KEY_COLUMN));
                keyList.add(key);
            }
        }
        return keyList;
    }

    public static List<String> queryDistinctAction(SQLiteDatabase db){
        Cursor cursor=db.query(true, Constants.DB.PATH_TABLE,new String[]{Constants.DB.ACTION_ID_COLUMN},
                Constants.DB.STATUS_COLUMN+"=?",new String[]{Constants.DB.NOT_SYNC},Constants.DB.ACTION_ID_COLUMN,null,null,null);
        List<String> actionList= new ArrayList<>();
        if (cursor!=null){
            while(cursor.moveToNext()){
                String agency = cursor.getString(cursor.getColumnIndex(Constants.DB.ACTION_ID_COLUMN));
                if (agency!=null){
                    actionList.add(agency);
                }
            }
        }
        return actionList;
    }

    public static List<String> queryDistinctAgency(SQLiteDatabase db){
//        db.execSQL("SELECT DISTINCT agency FROM path");
        Cursor cursor=db.query(true, Constants.DB.PATH_TABLE,new String[]{Constants.DB.AGENCY_COLUMN},
                Constants.DB.STATUS_COLUMN+"=?",new String[]{Constants.DB.NOT_SYNC},Constants.DB.AGENCY_COLUMN,null,null,null);
        List<String> agencyList= new ArrayList<>();
        if (cursor!=null){
            while(cursor.moveToNext()){
                String agency = cursor.getString(cursor.getColumnIndex(Constants.DB.AGENCY_COLUMN));
                if (agency!=null){
                    agencyList.add(agency);
                }
            }
        }
        return agencyList;
    }

    public static List<String>  queryPackKeyByActionAndAgency(SQLiteDatabase db,String action,String agency){
//        Cursor c = db.rawQuery("select * from user where username=?",new Stirng[]{"Jack Johnson"});
        Cursor cursor=db.query(true, Constants.DB.PATH_TABLE,new String[]{Constants.DB.PACK_KEY_COLUMN},
                Constants.DB.STATUS_COLUMN+" =? AND "+Constants.DB.AGENCY_COLUMN+" =? AND "+Constants.DB.ACTION_ID_COLUMN+" =? "
                ,new String[]{Constants.DB.NOT_SYNC,agency,action},null,null,null,null);
        List<String> keyList= new ArrayList<>();
        if (cursor!=null){
            while(cursor.moveToNext()){
                String key = cursor.getString(cursor.getColumnIndex(Constants.DB.PACK_KEY_COLUMN));
                keyList.add(key);
            }
        }
        return keyList;
    }

    public static List<String>  queryPackKeyByAction(SQLiteDatabase db,String action){
//        Cursor c = db.rawQuery("select * from user where username=?",new Stirng[]{"Jack Johnson"});
        Cursor cursor=db.query(true, Constants.DB.PATH_TABLE,new String[]{Constants.DB.PACK_KEY_COLUMN},
                Constants.DB.STATUS_COLUMN+" =? AND "+Constants.DB.ACTION_ID_COLUMN+" =? "
                ,new String[]{Constants.DB.NOT_SYNC,action},null,null,null,null);
        List<String> keyList= new ArrayList<>();
        if (cursor!=null){
            while(cursor.moveToNext()){
                String key = cursor.getString(cursor.getColumnIndex(Constants.DB.PACK_KEY_COLUMN));
                keyList.add(key);
            }
        }
        return keyList;
    }

    public static Map<String,OrgAgency>  queryOrgAgentCount(SQLiteDatabase db, String date){
        Map<String , OrgAgency> orgAgencyMap=new HashMap<>();
//        Cursor c = db.rawQuery("select agency , action_id , count(*) as count  from path where date(last_save_time)=? and status=? group by agency , action_id", new String[]{date,Constants.DB.SYNC});

        Cursor c = db.rawQuery("select agency , action_id , count(*) as count  from path where date(last_save_time)=? group by agency , action_id", new String[]{date});

        if (c!=null){
            while(c.moveToNext()){
                OrgAgency orgAgency;
                String agency=c.getString(0);
                if (orgAgencyMap.containsKey(agency)){
                    orgAgency=orgAgencyMap.get(agency);
                }else {
                    orgAgency=new OrgAgency();
                    orgAgency.setId(agency);
                }
                String action=c.getString(1);
                int count=c.getInt(2);
                switch (action){
                    case Constants.Logistic.INBOUND_CODE:
                        orgAgency.setInbound_count(count);
                        break;
                    case Constants.Logistic.OUTBOUND_CODE:
                        orgAgency.setOutbound_count(count);
                        break;
                }
                orgAgencyMap.put(agency,orgAgency);
            }
        }
        return orgAgencyMap;
    }



}
