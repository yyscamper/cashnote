package com.yyscamper.cashnote.PayType;

import android.text.format.*;

/**
 * Created by YuanYu on 14-3-16.
 */
public class PayLocation {
    public String Name;
    public int AttendCount;
    public Time LastAttendTime;
    public Time LastModifyTime;
    public int Status;

    public PayLocation(String name)
    {
        this.Name = name;
        this.AttendCount = 0;
        LastAttendTime = new Time();
        LastAttendTime.setToNow();
        LastModifyTime = new Time();
        LastModifyTime.setToNow();
        Status = SyncStatus.UNKNOWN;
    }

    public PayLocation() {
        LastModifyTime = new Time();
        LastModifyTime.setToNow();
        LastAttendTime = new Time();
        LastAttendTime.setToNow();
        Status = SyncStatus.UNKNOWN;
    }

    public boolean validate() {
        return true;
    }

    public void updateLastAttendTime(Time t) {
        if (t == null)
            return;
        if (Time.compare(LastAttendTime, t) < 0) {
            LastAttendTime = t;
        }
    }

    public void copyFrom(PayLocation loc) {
        this.Name = loc.Name;
        this.AttendCount = loc.AttendCount;
        this.LastAttendTime.set(loc.LastAttendTime);
        this.LastModifyTime.set(loc.LastModifyTime);
        this.Status = loc.Status;
    }
}