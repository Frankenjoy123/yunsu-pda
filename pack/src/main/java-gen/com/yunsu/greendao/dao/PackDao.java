package com.yunsu.greendao.dao;

import java.util.List;
import java.util.ArrayList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.SqlUtils;
import de.greenrobot.dao.internal.DaoConfig;

import com.yunsu.greendao.entity.ProductBase;
import com.yunsu.greendao.entity.Staff;

import com.yunsu.greendao.entity.Pack;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "PACK".
*/
public class PackDao extends AbstractDao<Pack, Long> {

    public static final String TABLENAME = "PACK";

    /**
     * Properties of entity Pack.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property PackKey = new Property(1, String.class, "packKey", false, "PACK_KEY");
        public final static Property Status = new Property(2, String.class, "status", false, "STATUS");
        public final static Property LastSaveTime = new Property(3, String.class, "lastSaveTime", false, "LAST_SAVE_TIME");
        public final static Property Standard = new Property(4, Integer.class, "standard", false, "STANDARD");
        public final static Property RealCount = new Property(5, Integer.class, "realCount", false, "REAL_COUNT");
        public final static Property StaffId = new Property(6, Long.class, "staffId", false, "STAFF_ID");
        public final static Property ProductBaseId = new Property(7, Long.class, "productBaseId", false, "PRODUCT_BASE_ID");
    };

    private DaoSession daoSession;


    public PackDao(DaoConfig config) {
        super(config);
    }
    
    public PackDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"PACK\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"PACK_KEY\" TEXT NOT NULL UNIQUE ," + // 1: packKey
                "\"STATUS\" TEXT," + // 2: status
                "\"LAST_SAVE_TIME\" TEXT," + // 3: lastSaveTime
                "\"STANDARD\" INTEGER," + // 4: standard
                "\"REAL_COUNT\" INTEGER," + // 5: realCount
                "\"STAFF_ID\" INTEGER," + // 6: staffId
                "\"PRODUCT_BASE_ID\" INTEGER);"); // 7: productBaseId
        // Add Indexes
        db.execSQL("CREATE INDEX " + constraint + "IDX_PACK__id ON PACK" +
                " (\"_id\");");
        db.execSQL("CREATE INDEX " + constraint + "IDX_PACK_PACK_KEY ON PACK" +
                " (\"PACK_KEY\");");
        db.execSQL("CREATE INDEX " + constraint + "IDX_PACK_STATUS ON PACK" +
                " (\"STATUS\");");
        db.execSQL("CREATE INDEX " + constraint + "IDX_PACK_LAST_SAVE_TIME ON PACK" +
                " (\"LAST_SAVE_TIME\");");
        db.execSQL("CREATE INDEX " + constraint + "IDX_PACK_STAFF_ID ON PACK" +
                " (\"STAFF_ID\");");
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"PACK\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Pack entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getPackKey());
 
        String status = entity.getStatus();
        if (status != null) {
            stmt.bindString(3, status);
        }
 
        String lastSaveTime = entity.getLastSaveTime();
        if (lastSaveTime != null) {
            stmt.bindString(4, lastSaveTime);
        }
 
        Integer standard = entity.getStandard();
        if (standard != null) {
            stmt.bindLong(5, standard);
        }
 
        Integer realCount = entity.getRealCount();
        if (realCount != null) {
            stmt.bindLong(6, realCount);
        }
 
        Long staffId = entity.getStaffId();
        if (staffId != null) {
            stmt.bindLong(7, staffId);
        }
 
        Long productBaseId = entity.getProductBaseId();
        if (productBaseId != null) {
            stmt.bindLong(8, productBaseId);
        }
    }

    @Override
    protected void attachEntity(Pack entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Pack readEntity(Cursor cursor, int offset) {
        Pack entity = new Pack( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getString(offset + 1), // packKey
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // status
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // lastSaveTime
            cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4), // standard
            cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5), // realCount
            cursor.isNull(offset + 6) ? null : cursor.getLong(offset + 6), // staffId
            cursor.isNull(offset + 7) ? null : cursor.getLong(offset + 7) // productBaseId
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Pack entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setPackKey(cursor.getString(offset + 1));
        entity.setStatus(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setLastSaveTime(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setStandard(cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4));
        entity.setRealCount(cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5));
        entity.setStaffId(cursor.isNull(offset + 6) ? null : cursor.getLong(offset + 6));
        entity.setProductBaseId(cursor.isNull(offset + 7) ? null : cursor.getLong(offset + 7));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(Pack entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(Pack entity) {
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
    
    private String selectDeep;

    protected String getSelectDeep() {
        if (selectDeep == null) {
            StringBuilder builder = new StringBuilder("SELECT ");
            SqlUtils.appendColumns(builder, "T", getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T0", daoSession.getStaffDao().getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T1", daoSession.getProductBaseDao().getAllColumns());
            builder.append(" FROM PACK T");
            builder.append(" LEFT JOIN STAFF T0 ON T.\"STAFF_ID\"=T0.\"_id\"");
            builder.append(" LEFT JOIN PRODUCT_BASE T1 ON T.\"PRODUCT_BASE_ID\"=T1.\"_id\"");
            builder.append(' ');
            selectDeep = builder.toString();
        }
        return selectDeep;
    }
    
    protected Pack loadCurrentDeep(Cursor cursor, boolean lock) {
        Pack entity = loadCurrent(cursor, 0, lock);
        int offset = getAllColumns().length;

        Staff staff = loadCurrentOther(daoSession.getStaffDao(), cursor, offset);
        entity.setStaff(staff);
        offset += daoSession.getStaffDao().getAllColumns().length;

        ProductBase productBase = loadCurrentOther(daoSession.getProductBaseDao(), cursor, offset);
        entity.setProductBase(productBase);

        return entity;    
    }

    public Pack loadDeep(Long key) {
        assertSinglePk();
        if (key == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder(getSelectDeep());
        builder.append("WHERE ");
        SqlUtils.appendColumnsEqValue(builder, "T", getPkColumns());
        String sql = builder.toString();
        
        String[] keyArray = new String[] { key.toString() };
        Cursor cursor = db.rawQuery(sql, keyArray);
        
        try {
            boolean available = cursor.moveToFirst();
            if (!available) {
                return null;
            } else if (!cursor.isLast()) {
                throw new IllegalStateException("Expected unique result, but count was " + cursor.getCount());
            }
            return loadCurrentDeep(cursor, true);
        } finally {
            cursor.close();
        }
    }
    
    /** Reads all available rows from the given cursor and returns a list of new ImageTO objects. */
    public List<Pack> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<Pack> list = new ArrayList<Pack>(count);
        
        if (cursor.moveToFirst()) {
            if (identityScope != null) {
                identityScope.lock();
                identityScope.reserveRoom(count);
            }
            try {
                do {
                    list.add(loadCurrentDeep(cursor, false));
                } while (cursor.moveToNext());
            } finally {
                if (identityScope != null) {
                    identityScope.unlock();
                }
            }
        }
        return list;
    }
    
    protected List<Pack> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }
    

    /** A raw-style query where you can pass any WHERE clause and arguments. */
    public List<Pack> queryDeep(String where, String... selectionArg) {
        Cursor cursor = db.rawQuery(getSelectDeep() + where, selectionArg);
        return loadDeepAllAndCloseCursor(cursor);
    }
 
}
