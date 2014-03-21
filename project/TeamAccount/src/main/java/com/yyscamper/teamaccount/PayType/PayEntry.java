package com.yyscamper.teamaccount.PayType;

import java.util.*;
import android.text.format.*;
public class PayEntry {
	public double Money;
	public Time PayTime;
	public PayPerson Payer;
	public PayPerson[] AttendPersons;
	public String Place;
	public String Description;
    public String CreateName = "Unknown";
	
	public PayEntry() {
		Money = 0.0;
		PayTime = new Time();
        PayTime.setToNow();
		Payer = null;
		AttendPersons = null;
		Place = "";
		Description = "";
	}
	
	public PayEntry(double money, String payName, String[] attPersons, Time t, String place, String desc) {
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
        this.Payer = PayPersonManager.add(payName);
		this.AttendPersons = PayPersonManager.addRange(attPersons);
		this.Place = place;
		this.Description = desc;
	}

    public boolean validate()
    {
        return  (this.Money > 0 && this.Payer != null &&  this.AttendPersons.length > 0);
    }

    public String getAttendNameString()
    {
        StringBuffer sb = new StringBuffer();
        for (PayPerson p : this.AttendPersons)
        {
            sb.append(p.Name);
            sb.append(',');
        }
        if (sb.length() > 0)
        {
            sb.deleteCharAt(sb.length() - 1);
        }

        return sb.toString();
    }
}
