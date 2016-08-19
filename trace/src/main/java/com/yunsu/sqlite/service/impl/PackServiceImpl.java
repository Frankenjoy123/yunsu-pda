package com.yunsu.sqlite.service.impl;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.yunsu.entity.OrgAgency;
import com.yunsu.manager.GreenDaoManager;
import com.yunsu.sqlite.service.PackService;
import com.yunsu.common.util.Constants;
import com.yunsu.greendao.dao.PackDao;
import com.yunsu.greendao.entity.Pack;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by yunsu on 2016/8/8.
 */
public class PackServiceImpl implements PackService {

    private PackDao packDao=GreenDaoManager.getInstance().getDaoSession().getPackDao();
    private SQLiteDatabase db=GreenDaoManager.getInstance().getDb();
    SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    @Override
    public void insertPackData(Pack pack) {
        packDao.insert(pack);
    }

    @Override
    public void insertPackWithCheck(Pack pack) {
        HashSet<String> set=new HashSet();
        if (pack.getActionId().equals(Constants.Logistic.INBOUND_CODE)){
            set.add(Constants.Logistic.INBOUND_CODE);
            set.add(Constants.Logistic.REVOKE_INBOUND_CODE);
        }else
        {
            set.add(Constants.Logistic.OUTBOUND_CODE);
            set.add(Constants.Logistic.REVOKE_OUTBOUND_CODE);
        }
        QueryBuilder<Pack> queryBuilder=packDao.queryBuilder();
        queryBuilder.where(queryBuilder.and(PackDao.Properties.PackKey.eq(pack.getPackKey()), PackDao.Properties.ActionId.in(set)));
        List<Pack> packList=queryBuilder.list();
        if (packList!=null&&packList.size()>0){
            Pack resultPack=packList.get(0);
            resultPack.setActionId(pack.getActionId());
            resultPack.setAgency(pack.getAgency());
            resultPack.setStatus(Constants.DB.NOT_SYNC);
            resultPack.setSaveTime(dateFormat.format(new Date()));
            updatePack(resultPack);
        }
        else {
            pack.setSaveTime(dateFormat.format(new Date()));
            insertPackData(pack);
        }
    }

    @Override
    public void updatePack(Pack pack) {
        packDao.update(pack);
    }

    @Override
    public void revokePathData(Pack pack) {
        if (pack.getActionId().equals(Constants.Logistic.INBOUND_CODE)){
            pack.setActionId(Constants.Logistic.REVOKE_INBOUND_CODE);
        }else {
            pack.setActionId(Constants.Logistic.REVOKE_OUTBOUND_CODE);
        }
        pack.setStatus(Constants.DB.NOT_SYNC);
        pack.setSaveTime(dateFormat.format(new Date()));
        updatePack(pack);
    }

    @Override
    public Pack queryByKeyActionStatus(Pack pack) {
        QueryBuilder<Pack> queryBuilder=packDao.queryBuilder();
        queryBuilder.where(queryBuilder.and(PackDao.Properties.PackKey.eq(pack.getPackKey()),
                PackDao.Properties.ActionId.eq(pack.getActionId()),
                PackDao.Properties.Status.eq(pack.getStatus())
        ));
        return queryBuilder.unique();
    }

    @Override
    public Pack queryByKeyAction(Pack pack) {
        QueryBuilder<Pack> queryBuilder=packDao.queryBuilder();
        queryBuilder.where(PackDao.Properties.PackKey.eq(pack.getPackKey()),
                PackDao.Properties.ActionId.eq(pack.getActionId()));
        return queryBuilder.unique();
    }

    @Override
    public Pack queryRevokeOrNot(Pack pack) {
        HashSet<String> set=new HashSet<>();
        if (pack.getActionId().equals(Constants.Logistic.INBOUND_CODE) || pack.getActionId().equals(Constants.Logistic.REVOKE_INBOUND_CODE) ){
            set.add(Constants.Logistic.INBOUND_CODE);
            set.add(Constants.Logistic.REVOKE_INBOUND_CODE);
        }
        else {
            set.add(Constants.Logistic.OUTBOUND_CODE);
            set.add(Constants.Logistic.REVOKE_OUTBOUND_CODE);
        }

        QueryBuilder<Pack> queryBuilder=packDao.queryBuilder();
        queryBuilder.where(
                PackDao.Properties.PackKey.eq(pack.getPackKey()),
                PackDao.Properties.ActionId.in(set));

        return queryBuilder.unique();
    }

    @Override
    public List<Pack> queryPackListByActionStatus(Pack pack, int offset) {
        QueryBuilder<Pack> queryBuilder=packDao.queryBuilder();
        queryBuilder.where(queryBuilder.and(PackDao.Properties.Status.eq(pack.getStatus()),
                PackDao.Properties.ActionId.eq(pack.getActionId()))).limit(Constants.Logistic.LIMIT_ITEM)
        .offset(offset);
        return queryBuilder.list();
    }

    @Override
    public List<Pack> queryPackKeyByActionAgencyStatus(Pack pack, int offset) {
        QueryBuilder<Pack> queryBuilder=packDao.queryBuilder();
        queryBuilder.where(queryBuilder.and(PackDao.Properties.Status.eq(pack.getStatus()),
                PackDao.Properties.ActionId.eq(pack.getActionId()),
                PackDao.Properties.Agency.eq(pack.getAgency()))).limit(Constants.Logistic.LIMIT_ITEM)
                .offset(offset);
        return queryBuilder.list();
    }

    @Override
    public void batchUpdateStatus(List<Pack> packList, String status) {
        for (Pack pack : packList) {
            pack.setStatus(status);
        }
        packDao.updateInTx(packList);
    }

    @Override
    public void batchDelete(List<Pack> packList) {
        packDao.deleteInTx(packList);
    }

    @Override
    public List<String> queryDistinctAction() {

        Cursor cursor=db.query(true, Constants.DB.PACK_TABLE,new String[]{Constants.DB.ACTION_ID_COLUMN},
                Constants.DB.STATUS_COLUMN+"=?",new String[]{Constants.DB.NOT_SYNC},Constants.DB.ACTION_ID_COLUMN,null,null,null);
        List<String> actionList= new ArrayList<>();
        if (cursor!=null){
            while(cursor.moveToNext()){
                String agency = cursor.getString(0);
                if (agency!=null){
                    actionList.add(agency);
                }
            }
        }
        return actionList;
    }

    @Override
    public List<String> queryDistinctAgency(String action) {
        Cursor cursor=db.query(true, Constants.DB.PACK_TABLE,new String[]{Constants.DB.AGENCY_COLUMN},
                Constants.DB.STATUS_COLUMN+"=? " +" and " + Constants.DB.ACTION_ID_COLUMN+" =?",new String[]{Constants.DB.NOT_SYNC,action},Constants.DB.AGENCY_COLUMN,null,null,null);
        List<String> agencyList= new ArrayList<>();
        if (cursor!=null){
            while(cursor.moveToNext()){
                String agency = cursor.getString(0);
                if (agency!=null){
                    agencyList.add(agency);
                }
            }
        }
        return agencyList;
    }

    @Override
    public Map<String, OrgAgency> queryOrgAgentCount(String date) {
        Log.d("TIME","queryOrgAgentCount start:");
        Map<String , OrgAgency> orgAgencyMap=new HashMap<>();
        Log.d("TIME","just rawQuerty start");
        Cursor c = db.rawQuery("select agency  , count(*) as count  from Pack where date(save_time)=? and action_id=? group by agency",
                new String[]{date,Constants.Logistic.OUTBOUND_CODE});
        Log.d("TIME","just rawQuerty end");

        Log.d("TIME","data put start");
        if (c!=null){
            while(c.moveToNext()) {
                OrgAgency orgAgency=new OrgAgency();;
                Log.d("TIME","parse data from cursor start");
                String agencyId=c.getString(0);
                int count=c.getInt(1);
                Log.d("TIME","parse data from cursor end");
                orgAgency.setId(agencyId);
                orgAgency.setOutbound_count(count);
                orgAgencyMap.put(agencyId,orgAgency);
            }
        }
        Log.d("TIME","data put end");

        Log.d("TIME","queryOrgAgentCount end");
        return orgAgencyMap;
    }

    @Override
    public Map<String, Integer> queryInOutCount(String date) {
        Log.d("TIME","queryInOutCount start");
        Map<String,Integer> countMap=new HashMap<>();
        Cursor c = db.rawQuery("select  action_id , count(*) as count  from Pack where date(save_time)=? and action_id in ('inbound' ,'outbound' ) group by action_id", new String[]{date});
        if (c!=null){
            while (c.moveToNext()){
                String action=c.getString(0);
                int count=c.getInt(1);
                countMap.put(action,count);
            }
        }
        Log.d("TIME","queryInOutCount end");
        return countMap;
    }

    @Override
    public void deleteBeforeDate(String date) {
        try {
            db.execSQL("delete from Pack where  date(save_time) < ?",new String[]{date});
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Pack> queryBeforeData(String date,int offset) {
        QueryBuilder<Pack> queryBuilder=packDao.queryBuilder();
        queryBuilder.where((PackDao.Properties.SaveTime).lt(date)).limit(Constants.Logistic.LIMIT_ITEM)
                .offset(offset);
        return queryBuilder.list();
    }

}
