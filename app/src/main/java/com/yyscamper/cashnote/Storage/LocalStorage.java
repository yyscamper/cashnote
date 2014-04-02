package com.yyscamper.cashnote.Storage;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.text.format.*;

import com.yyscamper.cashnote.PayType.*;
import com.yyscamper.cashnote.Util.Util;

import android.database.*;
/**
 * Created by yuanf on 2014-03-22.
 */
public class LocalStorage extends SQLiteOpenHelper {

    public static final int DB_VERSION = 1;

    public static final String KEY_PAY_MONEY = "money";
    public static final String KEY_PAY_PAYER_NAME = "payer_name";
    public static final String KEY_PAY_ATTENDS = "attends";
    public static final String KEY_PAY_LOCATION = "location";
    public static final String KEY_PAY_TIME = "time";
    public static final String KEY_PAY_DESCRIPTION = "description";
    public static final String KEY_PAY_UUID = "uuid";
    public static final String KEY_PAY_STATUS = "status";
    public static final String KEY_PAY_TYPE = "type";
    public static final String KEY_PAY_LAST_MODIFIED_TIME = "last_modify_time";

    public static final String KEY_PERSON_NAME = "name";
    public static final String KEY_PERSON_ATTEND_COUNT = "attend_count";
    public static final String KEY_PERSON_PAY_COUNT = "pay_count";
    public static final String KEY_PERSON_BLANCE = "blance";
    public static final String KEY_PERSON_LAST_MODIFIED_TIME = "last_modify_time";
    public static final String KEY_PERSON_STATUS = "status";
    public static final String KEY_PERSON_EMAIL = "email";
    public static final String KEY_PERSON_PHONE ="phone";

    public static final String KEY_LOCATION_NAME = "name";
    public static final String KEY_LOCATION_ATTEND_COUNT = "attend_account";
    public static final String KEY_LOCATION_LAST_ATTEND_TIME = "last_attend_time";
    public static final String KEY_LOCATION_LAST_MODIFIED_TIME = "last_modify_time";
    public static final String KEY_LOCATION_STATUS = "status";

    public static final String KEY_PARAM_ID = "id";
    public static final String KEY_PARAM_LAST_MODIFIED_TIME_FOR_PAY_HISTORY = "last_modify_time_for_pay_history";
    public static final String KEY_PARAM_LAST_MODIFIED_TIME_FOR_PAY_PERSONS = "last_modify_time_for_pay_persons";
    public static final String KEY_PARAM_LAST_MODIFIED_TIME_FOR_PAY_LOCATIONS = "last_modify_time_for_pay_locations";
    public static final String KEY_PARAM_STATUS = "status";
    public static final String KEY_PARAM_LAST_MODIFIED_TIME = "last_modify_time";

    public static final String KEY_LOCATION_GROUP_NAME = "group_name";
    public static final String KEY_LOCATION_GROUP_CHILDREN = "children";

    public static String TABLE_PAY_HISTORY = "pay_history";
    public static String TABLE_PAY_PERSONS = "pay_persons";
    public static String TABLE_PAY_LOCATIONS = "pay_locations";
    public static String TABLE_GLOBAL_PARAMETERS = "global_params";
    public static String TABLE_LOCATION_GROUPS = "location_groups";

    private static final String CREATE_PAY_HISTORY_TABLE = "create table " + TABLE_PAY_HISTORY + " (" +
            KEY_PAY_UUID + " char(129) primary key, " +
            KEY_PAY_MONEY + " double not null, " +
            KEY_PAY_PAYER_NAME + " nvarchar(128) not null, " +
            KEY_PAY_LOCATION + " nvarchar(256), " +
            KEY_PAY_TYPE + " integer not null, " +
            KEY_PAY_STATUS + " integer not null, " +
            KEY_PAY_TIME + " timestamp not null, " +
            KEY_PAY_LAST_MODIFIED_TIME + " timestamp  not null, " +
            KEY_PAY_ATTENDS + " text not null, " +
            KEY_PAY_DESCRIPTION + " text" +
            ")";

    private static final String CREATE_PAY_PERSONS_TABLE = "create table " + TABLE_PAY_PERSONS + "(" +
            KEY_PERSON_NAME + " nvarchar(128) primary key, " +
            KEY_PERSON_BLANCE + " double not null, " +
            KEY_PERSON_ATTEND_COUNT + " integer not null, " +
            KEY_PERSON_PAY_COUNT + " integer not null, " +
            KEY_PERSON_STATUS + " integer not null, " +
            KEY_PERSON_LAST_MODIFIED_TIME + " timestamp not null, " +
            KEY_PERSON_EMAIL + " char(129), " +
            KEY_PERSON_PHONE + " char(32)" +
            ")";


    private static final String CREATE_PAY_LOCATIONS_TABLE = "create table " + TABLE_PAY_LOCATIONS + "(" +
            KEY_LOCATION_NAME + " nvarchar(256) primary key, " +
            KEY_LOCATION_ATTEND_COUNT + " integer not null, " +
            KEY_LOCATION_STATUS + " integer not null, " +
            KEY_LOCATION_LAST_ATTEND_TIME + " timestamp, " +
            KEY_LOCATION_LAST_MODIFIED_TIME + " timestamp not null" +
            ")";

    private static final String CREATE_GLOBAL_PARAMETERS_TABLE = "create table " + TABLE_GLOBAL_PARAMETERS + "(" +
            KEY_PARAM_ID + " integer primary key autoincrement, " +
            KEY_PARAM_STATUS + " integer not null, " +
            KEY_PARAM_LAST_MODIFIED_TIME + " timestamp not null, " +
            KEY_PARAM_LAST_MODIFIED_TIME_FOR_PAY_HISTORY + " timestamp not null, " +
            KEY_PARAM_LAST_MODIFIED_TIME_FOR_PAY_PERSONS + " timestamp not null, " +
            KEY_PARAM_LAST_MODIFIED_TIME_FOR_PAY_LOCATIONS + " timestamp not null"+
            ")";

    private static final String CREATE_LOCATION_GROUPS_TABLE = "create table " + TABLE_LOCATION_GROUPS + "(" +
            KEY_LOCATION_GROUP_NAME + " nvarchar(256) primary key, " +
            KEY_LOCATION_GROUP_CHILDREN + " text"  +
            ")";

    public LocalStorage(Context context, String name) {
        super(context, name, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PAY_HISTORY_TABLE);
        db.execSQL(CREATE_PAY_LOCATIONS_TABLE);
        db.execSQL(CREATE_PAY_PERSONS_TABLE);
        db.execSQL(CREATE_GLOBAL_PARAMETERS_TABLE);
        db.execSQL(CREATE_LOCATION_GROUPS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_PAY_HISTORY);
        db.execSQL("drop table if exists " + TABLE_PAY_LOCATIONS);
        db.execSQL("drop table if exists " + TABLE_PAY_PERSONS);
        db.execSQL("drop table if exists " + TABLE_GLOBAL_PARAMETERS);
        db.execSQL("drop table if exists " + TABLE_LOCATION_GROUPS);
        onCreate(db);
    }

    public void clearPayPersons() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("drop table if exists " + TABLE_PAY_PERSONS);
        db.execSQL(CREATE_PAY_PERSONS_TABLE);
    }

    public void clearPayLocations() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("drop table if exists " + TABLE_PAY_LOCATIONS);
        db.execSQL(CREATE_PAY_LOCATIONS_TABLE);
    }

    public void clearPayHistories() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("drop table if exists " + TABLE_PAY_HISTORY);
        db.execSQL(CREATE_PAY_HISTORY_TABLE);
    }

    public void clearGlobalParams() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("drop table if exists " + TABLE_GLOBAL_PARAMETERS);
        db.execSQL(CREATE_GLOBAL_PARAMETERS_TABLE);
    }

    public void clearLocationGroups() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("drop table if exists " + TABLE_LOCATION_GROUPS);
        db.execSQL(CREATE_LOCATION_GROUPS_TABLE);
    }

    public ContentValues createContentFromPayEntry(PayHistory entry) {
        ContentValues values = new ContentValues();
        values.put(KEY_PAY_UUID, entry.UUIDString);
        values.put(KEY_PAY_MONEY, entry.Money);
        values.put(KEY_PAY_PAYER_NAME, entry.PayerName);
        values.put(KEY_PAY_ATTENDS, entry.getAttendPersonNameString());
        values.put(KEY_PAY_LOCATION, entry.Location);
        values.put(KEY_PAY_DESCRIPTION, entry.Description);
        values.put(KEY_PAY_TIME, entry.PayTime.toMillis(true));
        values.put(KEY_PAY_LAST_MODIFIED_TIME, entry.LastLocalModifyTime.toMillis(true));
        values.put(KEY_PAY_STATUS, entry.Status);
        values.put(KEY_PAY_TYPE, entry.Type);
        return values;
    }

    public ContentValues createContentFromPayPerson(PayPerson person) {
        ContentValues values = new ContentValues();
        values.put(KEY_PERSON_NAME, person.Name);
        values.put(KEY_PERSON_BLANCE, person.Balance);
        values.put(KEY_PERSON_ATTEND_COUNT, person.AttendCount);
        values.put(KEY_PERSON_PAY_COUNT, person.PayCount);
        values.put(KEY_PERSON_LAST_MODIFIED_TIME, person.LastModifyTime.toMillis(true));
        values.put(KEY_PERSON_STATUS, person.Status);
        values.put(KEY_PERSON_EMAIL, person.Email);
        values.put(KEY_PERSON_PHONE, person.Phone);
        return values;
    }

    public ContentValues createContentFromPayLocation(PayLocation location) {
        ContentValues values = new ContentValues();
        values.put(KEY_LOCATION_NAME, location.Name);
        values.put(KEY_LOCATION_ATTEND_COUNT, location.AttendCount);
        values.put(KEY_LOCATION_LAST_ATTEND_TIME, location.LastAttendTime.toMillis(true));
        values.put(KEY_LOCATION_LAST_MODIFIED_TIME, location.LastModifyTime.toMillis(true));
        values.put(KEY_LOCATION_STATUS, location.Status);
        return values;
    }

    public ContentValues createContentFromLocationGroup(LocationGroup group) {
        ContentValues values = new ContentValues();
        values.put(KEY_LOCATION_GROUP_NAME, group.Name);
        values.put(KEY_LOCATION_GROUP_CHILDREN, Util.stringArrayJoin(group.getChildrenArray(), ","));
        return values;
    }

    public long insertPayHistory(PayHistory entry) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = createContentFromPayEntry(entry);
        long rowId = db.insert(TABLE_PAY_HISTORY, null, values);
        return rowId;
    }

    public long insertPayPerson(PayPerson person) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = createContentFromPayPerson(person);
        long rowId = db.insert(TABLE_PAY_PERSONS, null, values);
        return rowId;
    }

    public long insertPayLocation(PayLocation location) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = createContentFromPayLocation(location);
        long rowId = db.insert(TABLE_PAY_LOCATIONS, null, values);
        return rowId;
    }

    public long insertLocationGroup(LocationGroup group) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = createContentFromLocationGroup(group);
        long rowId = db.insert(TABLE_LOCATION_GROUPS, null, values);
        return rowId;
    }

    public int updatePayHistory(PayHistory entry) {
        SQLiteDatabase db = this.getWritableDatabase();
        entry.Status = SyncStatus.LOCAL_UPDATED;
        ContentValues values = createContentFromPayEntry(entry);
        return db.update(TABLE_PAY_HISTORY, values, KEY_PAY_UUID + " = ?",
                new String[] {entry.UUIDString});
    }

    public int updatePayPerson(PayPerson person) {
        SQLiteDatabase db = this.getWritableDatabase();
        person.Status = SyncStatus.LOCAL_UPDATED;
        ContentValues values = createContentFromPayPerson(person);
        return db.update(TABLE_PAY_PERSONS, values, KEY_PERSON_NAME + " = ?",
                new String[] {person.Name});
    }

    public int updatePayLocation(PayLocation locaton) {
        SQLiteDatabase db = this.getWritableDatabase();
        locaton.Status = SyncStatus.LOCAL_UPDATED;
        ContentValues values = createContentFromPayLocation(locaton);
        return db.update(TABLE_PAY_LOCATIONS, values, KEY_LOCATION_NAME + " = ?",
                new String[] {locaton.Name});
    }

    public int updateLocationGroup(LocationGroup group) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = createContentFromLocationGroup(group);
        return db.update(TABLE_LOCATION_GROUPS, values, KEY_LOCATION_GROUP_NAME + " = ?",
                new String[] {group.Name});
    }

    public void removePayHistory(String uuidString) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PAY_HISTORY, KEY_PAY_UUID + " = ? ", new String[] {uuidString});
    }

    public void removePayPerson(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PAY_PERSONS, KEY_PERSON_NAME + " = ? ", new String[] {name});
    }

    public void removePayLocation(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PAY_LOCATIONS, KEY_LOCATION_NAME + " = ? ", new String[] {name});
    }

    public void removeLocationGroup(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LOCATION_GROUPS, KEY_LOCATION_GROUP_NAME + " = ? ", new String[] {name});
    }

    public void reloadAllHistories() {
        String selectQuery = "SELECT * FROM " + TABLE_PAY_HISTORY;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            PayHistoryManager.clear(StorageSelector.CACHE);
            do {
                PayHistory entry = new PayHistory();
                entry.Status = c.getInt(c.getColumnIndex(KEY_PAY_STATUS));
                entry.UUIDString = c.getString(c.getColumnIndex(KEY_PAY_UUID));
                entry.Money = c.getDouble(c.getColumnIndex(KEY_PAY_MONEY));
                entry.PayerName = c.getString(c.getColumnIndex(KEY_PAY_PAYER_NAME));
                entry.Location = c.getString(c.getColumnIndex(KEY_PAY_LOCATION));
                entry.Description = c.getString(c.getColumnIndex(KEY_PAY_DESCRIPTION));
                entry.Type = c.getInt(c.getColumnIndex(KEY_PAY_TYPE));
                entry.setAttendPersonNames(c.getString(c.getColumnIndex(KEY_PAY_ATTENDS)));
                long timeMs = c.getLong(c.getColumnIndex(KEY_PAY_TIME));
                entry.PayTime.set(timeMs);
                timeMs = c.getLong(c.getColumnIndex(KEY_PAY_LAST_MODIFIED_TIME));
                entry.LastLocalModifyTime.set(timeMs);
                PayHistoryManager.add(entry, StorageSelector.CACHE);
            } while (c.moveToNext());
        }
    }

    public void reloadAllPersons() {
        String selectQuery = "SELECT * FROM " + TABLE_PAY_PERSONS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            PayPersonManager.clear(StorageSelector.CACHE);
            do {
                PayPerson person = new PayPerson();
                person.Name = c.getString(c.getColumnIndex(KEY_PERSON_NAME));
                person.Balance = c.getDouble(c.getColumnIndex(KEY_PERSON_BLANCE));
                person.AttendCount = c.getInt(c.getColumnIndex(KEY_PERSON_ATTEND_COUNT));
                person.PayCount = c.getInt(c.getColumnIndex(KEY_PERSON_PAY_COUNT));
                person.Status = c.getInt(c.getColumnIndex(KEY_PERSON_STATUS));
                person.Email = c.getString(c.getColumnIndex(KEY_PERSON_EMAIL));
                person.Phone = c.getString(c.getColumnIndex(KEY_PERSON_PHONE));
                long timeMs = c.getLong(c.getColumnIndex(KEY_PERSON_LAST_MODIFIED_TIME));
                person.LastModifyTime.set(timeMs);
                PayPersonManager.add(person, StorageSelector.CACHE);
            } while (c.moveToNext());
        }
    }

    public void reloadAllLocations() {
        String selectQuery = "SELECT * FROM " + TABLE_PAY_LOCATIONS;
        SQLiteDatabase db = this.getReadableDatabase();
        PayLocationManager.clear(StorageSelector.CACHE);
        Cursor c = null;
        try {
            c = db.rawQuery(selectQuery, null);
        }
        catch (Throwable err) {
            return;
        }
        if (c.moveToFirst()) {

            do {
                PayLocation loc = new PayLocation();
                loc.Name = c.getString(c.getColumnIndex(KEY_LOCATION_NAME));
                loc.AttendCount = c.getInt(c.getColumnIndex(KEY_LOCATION_ATTEND_COUNT));
                loc.Status = c.getInt(c.getColumnIndex(KEY_LOCATION_STATUS));
                long timeMs = c.getLong(c.getColumnIndex(KEY_LOCATION_LAST_ATTEND_TIME));
                loc.LastAttendTime.set(timeMs);
                timeMs = c.getLong(c.getColumnIndex(KEY_LOCATION_LAST_MODIFIED_TIME));
                loc.LastModifyTime.set(timeMs);
                PayLocationManager.add(loc, StorageSelector.CACHE);
            } while (c.moveToNext());
        }
    }

    public void realodAllLocationGroups() {
        String selectQuery = "SELECT * FROM " + TABLE_LOCATION_GROUPS;
        SQLiteDatabase db = this.getReadableDatabase();
        LocationGroupManager.clear(StorageSelector.CACHE);
        Cursor c = null;
        try {
            c = db.rawQuery(selectQuery, null);
        }
        catch (Throwable err) {
            return;
        }
        if (c.moveToFirst()) {
            do {
                LocationGroup group = new LocationGroup();
                group.Name = c.getString(c.getColumnIndex(KEY_LOCATION_GROUP_NAME));
                String strChildren = c.getString(c.getColumnIndex(KEY_LOCATION_GROUP_CHILDREN));
                String[] childrenNames = strChildren.split(",");
                group.setChildren(childrenNames);
                LocationGroupManager.add(group, StorageSelector.CACHE);
            } while (c.moveToNext());
        }
    }

    // closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

    private String formatTimestamp(Time t) {
        return String.format("%04d-%02d-%02d %02d:%02d:%02d",  t.year, (t.month +1), t.monthDay, t.hour, t.minute, t.second);
    }

    private int getYearKey(Time t) {
        return t.year;
    }

    private int getYearMonthKey(Time t) {
        return t.year * 100 + (t.month + 1);
    }
}
