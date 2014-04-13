package com.yyscamper.cashnote.PayType;

import android.content.ContentValues;
import android.content.res.Resources;
import android.database.Cursor;
import android.text.format.*;

import com.baidu.frontia.FrontiaData;
import com.yyscamper.cashnote.Enum.DataType;
import com.yyscamper.cashnote.Storage.StorageConst;
import com.yyscamper.cashnote.Storage.StorageObject;

import org.json.JSONObject;

/**
 * Created by YuanYu on 14-3-16.
 */
public class PayLocation extends StorageObject {
    private int AttendCount;
    private Time LastAttendTime;

    public int getAttendCount() {
        return AttendCount;
    }

    public Time getLastAttendTime() {
        return LastAttendTime;
    }

    public void setAttendCount(int attendCount) {
        if (attendCount >= 0)
            AttendCount = attendCount;
        else
            AttendCount = 0;
    }

    public void setLastAttendTime(Time lastAttendTime) {
        LastAttendTime = lastAttendTime;
    }

    public PayLocation(String name)
    {
        super(DataType.LOCATION, name);
        this.AttendCount = 0;
        LastAttendTime = new Time();
        LastAttendTime.setToNow();
    }

    public PayLocation() {
        super(DataType.LOCATION);
        LastAttendTime = new Time();
        LastAttendTime.setToNow();
    }

    public String getName() {
        return getKey();
    }

    public void setName(String name) {
        setKey(name);
    }

    public void updateLastAttendTime(Time t) {
        if (t == null)
            return;
        if (Time.compare(LastAttendTime, t) < 0) {
            LastAttendTime = t;
        }
    }

    public void copyFrom(PayLocation loc) {
        super.copyFrom(loc);
        this.AttendCount = loc.AttendCount;
        this.LastAttendTime.set(loc.LastAttendTime);
    }

    @Override
    public boolean validate() {
        return super.validate();
    }

    @Override
    public ContentValues convertToSqliteObject() {
        ContentValues values = super.convertToSqliteObject();
        values.put(StorageConst.KEY_LOCATION_ATTEND_COUNT, AttendCount);
        values.put(StorageConst.KEY_LOCATION_LAST_ATTEND_TIME, LastAttendTime.toMillis(true));
        return values;
    }

    @Override
    public boolean parseSqliteObject(Cursor c) {
        if (!super.parseSqliteObject(c))
            return false;
        AttendCount = c.getInt(c.getColumnIndex(StorageConst.KEY_LOCATION_ATTEND_COUNT));
        long t = c.getLong(c.getColumnIndex(StorageConst.KEY_LOCATION_LAST_ATTEND_TIME));
        LastAttendTime.set(t);
        return true;
    }

    @Override
    public FrontiaData convertToFrontiaData() {
        FrontiaData values = super.convertToFrontiaData();
        values.put(StorageConst.KEY_LOCATION_ATTEND_COUNT, AttendCount);
        values.put(StorageConst.KEY_LOCATION_LAST_ATTEND_TIME, LastAttendTime.toMillis(true));
        return values;
    }

    @Override
    public boolean parseFrontiaData(FrontiaData data) {
        if (!super.parseFrontiaData(data)) {
            return false;
        }
        JSONObject json = data.toJSON();
        try {
            AttendCount = json.getInt(StorageConst.KEY_LOCATION_ATTEND_COUNT);
            long t = json.getLong(StorageConst.KEY_LOCATION_LAST_ATTEND_TIME);
            LastAttendTime.set(t);
        }
        catch (Throwable err) {
            return false;
        }
        return true;
    }
}