package com.yyscamper.cashnote.PayType;

import java.util.*;
import android.text.format.*;

public class PayHistory {
    public String UUIDString;
    public int Type;
	public double Money;
    public String PayerName;
    public String[] AttendPersonNames;
	public Time PayTime;
	public String Location;
	public String Description;
    public int Status;
    public Time LastSyncTime;
    public Time LastLocalModifyTime;

    public static final int TYPE_NORMAL = 0;

	public PayHistory() {
        UUIDString = UUID.randomUUID().toString();
		Money = 0.0;
		PayTime = new Time();
        PayTime.setToNow();
        PayerName = null;
        AttendPersonNames = null;
		Location = "";
		Description = "";
        Status = SyncStatus.LOCAL_NEW;
        Type = TYPE_NORMAL;
        LastSyncTime = new Time();
        LastSyncTime.set(0, 0, 0, 1, 1, 2000);
        LastLocalModifyTime = new Time();
        LastLocalModifyTime.setToNow();
	}
	
	public PayHistory(double money, String payName, String[] attPersons, Time t, String location, String desc) {
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
        Status = SyncStatus.LOCAL_NEW;
        UUIDString = UUID.randomUUID().toString();
        Type = TYPE_NORMAL;
        LastSyncTime = new Time();
        LastSyncTime.set(0, 0, 0, 1, 1, 2000);
        LastLocalModifyTime = new Time();
        LastLocalModifyTime.setToNow();
	}

    public PayHistory(String uuid, double money, String payName, String[] attPersons, Time t, String location, String desc) {
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
        Status = SyncStatus.LOCAL_NEW;
        UUIDString = uuid;
        Type = TYPE_NORMAL;
        LastSyncTime = new Time();
        LastSyncTime.set(0, 0, 0, 1, 1, 2000);
        LastLocalModifyTime = new Time();
        LastLocalModifyTime.setToNow();
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
    public PayPerson[] getAttends() {
        PayPerson[] arr = new PayPerson[AttendPersonNames.length];
        int i = 0;
        for (String name : AttendPersonNames) {
            arr[i++] = PayPersonManager.get(name);
        }
        return arr;
    }
}
