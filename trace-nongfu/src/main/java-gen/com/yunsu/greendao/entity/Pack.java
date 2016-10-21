package com.yunsu.greendao.entity;

import com.yunsu.greendao.dao.DaoSession;
import de.greenrobot.dao.DaoException;

import com.yunsu.greendao.dao.MaterialDao;
import com.yunsu.greendao.dao.PackDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "PACK".
 */
public class Pack {

    private Long id;
    /** Not-null value. */
    private String packKey;
    private String actionId;
    private String agency;
    private String status;
    private String saveTime;
    private Integer count;
    private String type;
    private Long materialId;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient PackDao myDao;

    private Material material;
    private Long material__resolvedKey;


    public Pack() {
    }

    public Pack(Long id) {
        this.id = id;
    }

    public Pack(Long id, String packKey, String actionId, String agency, String status, String saveTime, Integer count, String type, Long materialId) {
        this.id = id;
        this.packKey = packKey;
        this.actionId = actionId;
        this.agency = agency;
        this.status = status;
        this.saveTime = saveTime;
        this.count = count;
        this.type = type;
        this.materialId = materialId;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getPackDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /** Not-null value. */
    public String getPackKey() {
        return packKey;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setPackKey(String packKey) {
        this.packKey = packKey;
    }

    public String getActionId() {
        return actionId;
    }

    public void setActionId(String actionId) {
        this.actionId = actionId;
    }

    public String getAgency() {
        return agency;
    }

    public void setAgency(String agency) {
        this.agency = agency;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSaveTime() {
        return saveTime;
    }

    public void setSaveTime(String saveTime) {
        this.saveTime = saveTime;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Long materialId) {
        this.materialId = materialId;
    }

    /** To-one relationship, resolved on first access. */
    public Material getMaterial() {
        Long __key = this.materialId;
        if (material__resolvedKey == null || !material__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            MaterialDao targetDao = daoSession.getMaterialDao();
            Material materialNew = targetDao.load(__key);
            synchronized (this) {
                material = materialNew;
            	material__resolvedKey = __key;
            }
        }
        return material;
    }

    public void setMaterial(Material material) {
        synchronized (this) {
            this.material = material;
            materialId = material == null ? null : material.getId();
            material__resolvedKey = materialId;
        }
    }

    /** Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context. */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.delete(this);
    }

    /** Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context. */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.update(this);
    }

    /** Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context. */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.refresh(this);
    }

}
