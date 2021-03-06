package com.yunsu.greendao.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.yunsu.greendao.entity.Express;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "EXPRESS".
*/
public class ExpressDao extends AbstractDao<Express, Long> {

    public static final String TABLENAME = "EXPRESS";

    /**
     * Properties of entity Express.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property PackKey = new Property(1, String.class, "packKey", false, "PACK_KEY");
        public final static Property ExpressKey = new Property(2, String.class, "expressKey", false, "EXPRESS_KEY");
        public final static Property CreateTime = new Property(3, String.class, "createTime", false, "CREATE_TIME");
    };


    public ExpressDao(DaoConfig config) {
        super(config);
    }
    
    public ExpressDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"EXPRESS\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"PACK_KEY\" TEXT," + // 1: packKey
                "\"EXPRESS_KEY\" TEXT," + // 2: expressKey
                "\"CREATE_TIME\" TEXT);"); // 3: createTime
        // Add Indexes
        db.execSQL("CREATE INDEX " + constraint + "IDX_EXPRESS_PACK_KEY ON EXPRESS" +
                " (\"PACK_KEY\");");
        db.execSQL("CREATE INDEX " + constraint + "IDX_EXPRESS_EXPRESS_KEY ON EXPRESS" +
                " (\"EXPRESS_KEY\");");
        db.execSQL("CREATE INDEX " + constraint + "IDX_EXPRESS_CREATE_TIME ON EXPRESS" +
                " (\"CREATE_TIME\");");
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"EXPRESS\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Express entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String packKey = entity.getPackKey();
        if (packKey != null) {
            stmt.bindString(2, packKey);
        }
 
        String expressKey = entity.getExpressKey();
        if (expressKey != null) {
            stmt.bindString(3, expressKey);
        }
 
        String createTime = entity.getCreateTime();
        if (createTime != null) {
            stmt.bindString(4, createTime);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Express readEntity(Cursor cursor, int offset) {
        Express entity = new Express( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // packKey
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // expressKey
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3) // createTime
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Express entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setPackKey(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setExpressKey(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setCreateTime(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(Express entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(Express entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
