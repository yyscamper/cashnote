package com.yyscamper.cashnote.PayType;

import java.util.*;
import android.text.format.*;
public class PayEntry {
    public String UUIDString;
    public int Status;
    public int Type;
	public double Money;
    public String PayerName;
    public String[] AttendPersonNames;
	public Time PayTime;
	public String Location;
	public String Description;

    public static final int STATUS_NORMAL = 0;
    public static final int STATUS_DELETED = 1;
    public static final int STATUS_SAVE_IN_LOCAL = 2;
    public static final int STATUS_SAVE_IN_SERVER = 3;
    public static final int TYPE_NORMAL = 0;

	public PayEntry() {
        UUIDString = UUID.randomUUID().toString();
		Money = 0.0;
		PayTime = new Time();
        PayTime.setToNow();
        PayerName = null;
        AttendPersonNames = null;
		Location = "";
		Description = "";
        Status = STATUS_NORMAL;
        Type = TYPE_NORMAL;
	}
	
	public PayEntry(double money, String payName, String[] attPersons, Time t, String location, String desc) {
		this.Money = money;
        if (t == null)
        {
            PayTime = new Time();
            PayTime.setToNow();
        }
        else
        {
            this.PayTime = t;
        }
        this.PayerName = payName;
		this.AttendPersonNames = attPersons;
		this.Location = location;
		this.Description = desc;
        Status = STATUS_NORMAL;
        UUIDString = UUID.randomUUID().toString();
        Type = TYPE_NORMAL;
	}

    public PayEntry(String uuid, double money, String payName, String[] attPersons, Time t, String location, String desc) {
        this.Money = money;
        if (t == null)
        {
            PayTime = new Time();
            PayTime.setToNow();
        }
        else
        {
            this.PayTime = t;
        }
        this.PayerName = payName;
        this.AttendPersonNames = attPersons;
        this.Location = location;
        this.Description = desc;
        Status = STATUS_NORMAL;
        UUIDString = uuid;
        Type = TYPE_NORMAL;
    }

    public boolean validate()
    {
        return  (this.Money > 0 && this.PayerName != null && this.PayerName.trim().length() > 0
                &&  this.AttendPersonNames != null && this.AttendPersonNames.length > 0);
    }

    public String getAttendPersonNameString()
    {
        StringBuffer sb = new StringBuffer();
        for (String p : this.AttendPersonNames)
        {
            sb.append(p);
            sb.append(',');
        }
        if (sb.length() > 0)
        {
            sb.deleteCharAt(sb.length() - 1);
        }

        return sb.toString();
    }

    public void setAttendPersonNames(String str) {
        this.AttendPersonNames = str.split(",");
    }

    public PayPerson getPayer() {
        return PayPersonManager.get(this.PayerName);
    }
}
