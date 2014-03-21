package com.yyscamper.teamaccount.PayType;

/**
 * Created by YuanYu on 14-3-16.
 */
public class PayLocation {
    public String Name;
    public int AttendCount;

    public PayLocation(String name)
    {
        this.Name = name;
        this.AttendCount = 0;
    }
}