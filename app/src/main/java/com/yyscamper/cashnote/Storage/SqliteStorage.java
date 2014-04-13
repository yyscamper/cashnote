package com.yyscamper.cashnote.Storage;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;

import com.yyscamper.cashnote.Enum.DataType;
import com.yyscamper.cashnote.PayType.*;
import com.yyscamper.cashnote.Util.Util;

import android.database.*;
import android.provider.ContactsContract;

import java.util.ArrayList;

/**
 * Created by yuanf on 2014-03-22.
 */
public class SqliteStorage extends SQLiteOpenHelper implements BaseStorage.SyncStorage {
    public static final int DB_VERSION = 1;

    private static final String CREATE_TABLE_COMMON =
              StorageConst.KEY_KEY + " char(128) primary key, "
            + StorageConst.KEY_DATA_TYPE + " integer not null, "
            + StorageConst.KEY_ACCOUNT_BOOK + " text not null, "
            + StorageConst.KEY_VERSION + " integer not null, "
            + StorageConst.KEY_CLOUD_STATUS + " integer not null, "
            + StorageConst.KEY_CREATED_TIME + " timestamp not null, "
            + StorageConst.KEY_LAST_MODIFIY_TIME + " timestamp not null, "
            + StorageConst.KEY_LAST_SYNC_TIME + " timestamp not null, "
            + StorageConst.KEY_CREATED_USER_ID + " char(128) not null, "
            + StorageConst.KEY_LAST_MODIFY_USER_ID + " char(128) not null, ";

    private static final String CREATE_PAY_HISTORY_TABLE =
            "create table " + StorageConst.TABLE_HISTORY
            + " ( "
            + CREATE_TABLE_COMMON
            + StorageConst.KEY_HISTORY_TYPE + " integer not null, "
            + StorageConst.KEY_HISTORY_MONEY + " double not null, "
            + StorageConst.KEY_HISTORY_PAYER_NAME + " nvarchar(128) not null, "
            + StorageConst.KEY_HISTORY_ATTENDS + " text not null, "
            + StorageConst.KEY_HISTORY_LOCATION + " nvarchar(256), "
            + StorageConst.KEY_HISTORY_DESCRIPTION + " text"
            + " )";

    private static final String CREATE_PAY_PERSONS_TABLE =
            "create table " + StorageConst.TABLE_PERSON
            + " ( "
            + CREATE_TABLE_COMMON
            + StorageConst.KEY_PERSON_BALANCE + " double not null, "
            + StorageConst.KEY_PERSON_ATTEND_COUNT + " integer not null, "
            + StorageConst.KEY_PERSON_PAY_COUNT + " integer not null, "
            + StorageConst.KEY_PERSON_EMAIL + " char(128), "
            + StorageConst.KEY_PERSON_PHONE + " char(32)"
            + " )";

    private static final String CREATE_LOCATION_GROUPS_TABLE =
            "create table " + StorageConst.TABLE_LOCATION
            + " ( "
            + CREATE_TABLE_COMMON
            + StorageConst.KEY_LOCATION_ATTEND_COUNT + " integer not null, "
            + StorageConst.KEY_LOCATION_LAST_ATTEND_TIME + " timestamp not null"
            + " )";

    private static final String CREATE_PAY_LOCATIONS_TABLE =
            "create table " + StorageConst.TABLE_LOCATION_GROUP
            + " ("
            + CREATE_TABLE_COMMON
            + StorageConst.KEY_LOCATION_GROUP_CHILDREN + " text"
            + " )";

    private static final String CREATE_ACCOUNT_BOOK_TABLE =
            "create table " + StorageConst.TABLE_ACCOUNT_BOOK
            + " ("
            + CREATE_TABLE_COMMON
            + " )";

    public SqliteStorage(Context context, String name) {
        super(context, name, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PAY_HISTORY_TABLE);
        db.execSQL(CREATE_PAY_LOCATIONS_TABLE);
        db.execSQL(CREATE_PAY_PERSONS_TABLE);
        db.execSQL(CREATE_LOCATION_GROUPS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + StorageConst.TABLE_HISTORY);
        db.execSQL("drop table if exists " + StorageConst.TABLE_PERSON);
        db.execSQL("drop table if exists " + StorageConst.TABLE_LOCATION_GROUP);
        db.execSQL("drop table if exists " + StorageConst.TABLE_LOCATION);
        onCreate(db);
    }

    @Override
    public int initStorage() {
        return 0;
    }

    @Override
    public int closeStorage() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
        return 0;
    }

    private String findTableName(DataType dataType) {
        switch (dataType) {
            case PERSON:
                return StorageConst.TABLE_PERSON;
            case HISTORY:
                return StorageConst.TABLE_HISTORY;
            case LOCATION:
                return StorageConst.TABLE_LOCATION;
            case LOCATION_GROUP:
                return StorageConst.TABLE_LOCATION_GROUP;
            case ACCOUNT_BOOK:
                return StorageConst.TABLE_ACCOUNT_BOOK;
            default:
                return null;
        }
    }

    private String findSqlCreateCommand(DataType dataType) {
        switch (dataType) {
            case PERSON:
                return CREATE_PAY_PERSONS_TABLE;
            case HISTORY:
                return CREATE_PAY_HISTORY_TABLE;
            case LOCATION:
                return CREATE_PAY_LOCATIONS_TABLE;
            case LOCATION_GROUP:
                return CREATE_LOCATION_GROUPS_TABLE;
            case ACCOUNT_BOOK:
                return CREATE_ACCOUNT_BOOK_TABLE;
            default:
                return null;
        }
    }

    @Override
    public int insert(StorageObject obj) {
        String tableName = findTableName(obj.getType());
        if (tableName == null)
            return -1;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = obj.convertToSqliteObject();
        long rowId = db.insert(tableName, null, values);
        return 0;
    }

    @Override
    public int insert(DataType dataType, ArrayList<StorageObject> objList) {
        String tableName = findTableName(dataType);
        if (tableName == null)
            return -1;
        SQLiteDatabase db = this.getWritableDatabase();
        for (StorageObject obj : objList) {
            ContentValues values = obj.convertToSqliteObject();
            db.insert(tableName, null, values);
        }
        return 0;
    }

    @Override
    public int remove(DataType dataType, String key) {
        String tableName = findTableName(dataType);
        if (tableName == null)
            return -1;
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(tableName, StorageConst.KEY_KEY + " = ?", new String[] {key});
        return 0;
    }

    @Override
    public int remove(StorageObject obj) {
        return remove(obj.getType(), obj.getKey());
    }

    @Override
    public int clear(DataType dataType) {
        String tableName = findTableName(dataType);
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("drop table if exists " + tableName);
        String createCommand = findSqlCreateCommand(dataType);
        db.execSQL(createCommand);
        return 0;
    }

    @Override
    public int clearAll() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("drop table if exists " + StorageConst.TABLE_HISTORY);
        db.execSQL("drop table if exists " + StorageConst.TABLE_PERSON);
        db.execSQL("drop table if exists " + StorageConst.TABLE_LOCATION_GROUP);
        db.execSQL("drop table if exists " + StorageConst.TABLE_LOCATION);
        onCreate(db);
        return 0;
    }

    @Override
    public int update(String oldKey, StorageObject obj) {
        String tableName = findTableName(obj.getType());
        if (tableName == null)
            return -1;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = obj.convertToSqliteObject();
        db.update(tableName, values,
                StorageConst.KEY_KEY + " = ?", new String[] {obj.getKey()});
        return -1;
    }

    public int update(StorageObject obj) {
        return update(obj.getKey(), obj);
    }

    @Override
    public StorageObject get(DataType dataType, String key) {
        String tableName = findTableName(dataType);
        String selectQuery = "SELECT * FROM " + tableName + " where " + StorageConst.KEY_KEY + "=?";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery(selectQuery, new String[] {key});
        if (c.moveToFirst()) {
            StorageObject obj = Util.createByType(dataType);
            obj.parseSqliteObject(c);
            return obj;
        }
        return null;
    }

    @Override
    public ArrayList<StorageObject> getAll(DataType dataType) {
        String tableName = findTableName(dataType);
        String selectQuery = "SELECT * FROM " + tableName;
        ArrayList<StorageObject> arrList = new ArrayList<StorageObject>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                StorageObject obj = Util.createByType(dataType);
                obj.parseSqliteObject(c);
                arrList.add(obj);
            } while (c.moveToNext());
        }
        return arrList;
    }

}
