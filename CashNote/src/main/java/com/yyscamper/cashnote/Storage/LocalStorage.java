package com.yyscamper.cashnote.Storage;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.text.format.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import com.yyscamper.cashnote.PayType.*;

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
    public static final String KEY_PAY_TIME_YEAR_MONTH = "time_year_month";
    public static final String KEY_PAY_TIME_YEAR = "time_year";
    public static final String KEY_PAY_DESCRIPTION = "description";
    public static final String KEY_PAY_UUID = "uuid";
    public static final String KEY_PAY_STATUS = "status";
    public static final String KEY_PAY_TYPE = "type";

    public static final String KEY_PERSON_NAME = "name";
    public static final String KEY_PERSON_ATTEND_COUNT = "attend_count";
    public static final String KEY_PERSON_PAY_COUNT = "pay_count";
    public static final String KEY_PERSON_BLANCE = "blance";

    public static final String KEY_LOCATION_NAME = "name";
    public static final String KEY_LOCATION_ATTEND_COUNT = "attend_account";
    public static final String KEY_LOCATION_LAST_ATTEND_TIME = "last_attend_time";

    public static final String KEY_PARAM_ID = "id";
    public static final String KEY_PARAM_LAST_MODIFIED_TIME_FOR_PAY_HISTORY = "last_modify_time_for_pay_history";
    public static final String KEY_PARAM_LAST_MODIFIED_TIME_FOR_PAY_PERSONS = "last_modify_time_for_pay_persons";
    public static final String KEY_PARAM_LAST_MODIFIED_TIME_FOR_PAY_LOCATIONS = "last_modify_time_for_pay_locations";
    public static final String KEY_PARAM_STATUS = "status";

    public static String TABLE_PAY_HISTORY = "pay_history";
    public static String TABLE_PAY_PERSONS = "pay_persons";
    public static String TABLE_PAY_LOCATIONS = "pay_locations";
    public static String TABLE_GLOBAL_PARAMETERS = "global_params";

    private static final String CREATE_PAY_HISTORY_TABLE = "create table " + TABLE_PAY_HISTORY + " (" +
            KEY_PAY_UUID + " char(129) primary key, " +
            KEY_PAY_MONEY + " double not null, " +
            KEY_PAY_PAYER_NAME + " nvarchar(128) not null, " +
            KEY_PAY_LOCATION + " nvarchar(256), " +
            KEY_PAY_DESCRIPTION + " text, " +
            KEY_PAY_TIME + " integer not null, " +
            KEY_PAY_TIME_YEAR + " integer not null, " +
            KEY_PAY_TIME_YEAR_MONTH + " integer not null, " +
            KEY_PAY_ATTENDS + " text not null, " +
            KEY_PAY_STATUS + " integer not null, " +
            KEY_PAY_TYPE + " integer not null" +
            ")";

    private static final String CREATE_PAY_PERSONS_TABLE = "create table " + TABLE_PAY_PERSONS + "(" +
            KEY_PERSON_NAME + " nvarchar(128) primary key, " +
            KEY_PERSON_BLANCE + " double not null, " +
            KEY_PERSON_ATTEND_COUNT + " integer not null, " +
            KEY_PERSON_PAY_COUNT + " integer not null" +
            ")";


    private static final String CREATE_PAY_LOCATIONS_TABLE = "create table " + TABLE_PAY_LOCATIONS + "(" +
            KEY_LOCATION_NAME + " text primary key, " +
            KEY_LOCATION_ATTEND_COUNT + " integer, " +
            KEY_LOCATION_LAST_ATTEND_TIME + " timestamp)";

    private static final String CREATE_GLOBAL_PARAMETERS_TABLE = "create table " + TABLE_GLOBAL_PARAMETERS + "(" +
            KEY_PARAM_ID + " integer primary key autoincrement, " +
            KEY_PARAM_STATUS + " integer not null, " +
            KEY_PARAM_LAST_MODIFIED_TIME_FOR_PAY_HISTORY + " timestamp, " +
            KEY_PARAM_LAST_MODIFIED_TIME_FOR_PAY_PERSONS + " timestamp, " +
            KEY_PARAM_LAST_MODIFIED_TIME_FOR_PAY_LOCATIONS + " timestamp)";

    public LocalStorage(Context context, String name) {
        super(context, name, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PAY_HISTORY_TABLE);
        db.execSQL(CREATE_PAY_LOCATIONS_TABLE);
        db.execSQL(CREATE_PAY_PERSONS_TABLE);
        db.execSQL(CREATE_GLOBAL_PARAMETERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_PAY_HISTORY);
        db.execSQL("drop table if exists " + TABLE_PAY_LOCATIONS);
        db.execSQL("drop table if exists " + TABLE_PAY_PERSONS);
        db.execSQL("drop table if exists " + TABLE_GLOBAL_PARAMETERS);
        onCreate(db);
    }

    public ContentValues createContentFromPayEntry(PayEntry entry) {
        ContentValues values = new ContentValues();
        values.put(KEY_PAY_UUID, entry.UUIDString);
        values.put(KEY_PAY_MONEY, entry.Money);
        values.put(KEY_PAY_PAYER_NAME, entry.PayerName);
        values.put(KEY_PAY_ATTENDS, entry.getAttendPersonNameString());
        values.put(KEY_PAY_LOCATION, entry.Location);
        values.put(KEY_PAY_DESCRIPTION, entry.Description);
        values.put(KEY_PAY_TIME, entry.PayTime.toMillis(true));
        values.put(KEY_PAY_TIME_YEAR,getYearKey(entry.PayTime));
        values.put(KEY_PAY_TIME_YEAR_MONTH, getYearMonthKey(entry.PayTime));
        values.put(KEY_PAY_STATUS, entry.Status);
        values.put(KEY_PAY_TYPE, entry.Type);
        return values;
    }

    public long insertPayEntry(PayEntry entry) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = createContentFromPayEntry(entry);
        long rowId = db.insert(TABLE_PAY_HISTORY, null, values);
        return rowId;
    }

    public int updatePayEntry(PayEntry entry) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = createContentFromPayEntry(entry);
        return db.update(TABLE_PAY_HISTORY, values, KEY_PAY_UUID + " = ?",
                new String[] {entry.UUIDString});
    }

    public void removePayEntry(String uuidString) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PAY_HISTORY, KEY_PAY_UUID + " = ? ", new String[] {uuidString});
    }

    public void reloadAllPayEntries() {
        String selectQuery = "SELECT * FROM " + TABLE_PAY_HISTORY;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            PayEntryManager.clear();
            do {
                PayEntry entry = new PayEntry();
                entry.UUIDString = c.getString(c.getColumnIndex(KEY_PAY_UUID));
                entry.Money = c.getDouble(c.getColumnIndex(KEY_PAY_MONEY));
                entry.PayerName = c.getString(c.getColumnIndex(KEY_PAY_PAYER_NAME));
                entry.Location = c.getString(c.getColumnIndex(KEY_PAY_LOCATION));
                entry.Description = c.getString(c.getColumnIndex(KEY_PAY_DESCRIPTION));
                entry.Status = c.getInt(c.getColumnIndex(KEY_PAY_STATUS));
                entry.Type = c.getInt(c.getColumnIndex(KEY_PAY_TYPE));
                entry.setAttendPersonNames(c.getString(c.getColumnIndex(KEY_PAY_ATTENDS)));
                long timeMs = c.getLong(c.getColumnIndex(KEY_PAY_TIME));
                entry.PayTime.set(timeMs);
                PayEntryManager.add(entry);
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
