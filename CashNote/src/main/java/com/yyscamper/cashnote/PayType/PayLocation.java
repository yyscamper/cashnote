package com.yyscamper.cashnote.PayType;

import android.text.format.*;

/**
 * Created by YuanYu on 14-3-16.
 */
public class PayLocation {
    public String Name;
    public int AttendCount;
    public Time LastAttendTime;

    public PayLocation(String name)
    {
        this.Name = name;
        this.AttendCount = 0;
    }
}