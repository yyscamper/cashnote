package com.yyscamper.cashnote.PayType;

import java.util.*;
import android.text.format.*;

public class PayHistory {
    public String UUIDString;
    public int Type;
    public double Money;
    public String PayerName;
    public Time PayTime;
    public String Location;
    public String Description;
    public int Status;
    public Time LastSyncTime;
    public Time LastModityTime;
    public PayAttendInfo[] AttendsInfo;
    public static final int TYPE_NORMAL_AVG = 0;
    public static final int TYPE_NORMAL_NOT_AVG = 1;

	public PayHistory() {
        UUIDString = UUID.randomUUID().toString();
		Money = 0.0;
		PayTime = new Time();
        PayTime.setToNow();
        PayerName = null;
        AttendsInfo = null;
		Location = "";
		Description = "";
        Status = SyncStatus.LOCAL_NEW;
        Type = TYPE_NORMAL_AVG;
        LastSyncTime = new Time();
        LastSyncTime.set(0, 0, 0, 1, 1, 2000);
        LastModityTime = new Time();
        LastModityTime.setToNow();
	}

    public static PayHistory buildAvgHistory(String uuid, double totalMoney, String payName, String[] attNames, Time t, String location, String desc) {
        PayHistory history = new PayHistory();
        if (uuid != null) {
            history.UUIDString = uuid;
        }
        history.Money = totalMoney;
        history.Type = TYPE_NORMAL_AVG;
        history.PayerName = payName;
        history.setAttendNames(attNames);
        history.Location = location;
        history.Description = desc;
        history.Status = SyncStatus.LOCAL_NEW;
        if (t != null) {
            history.PayTime = t;
        }
        return history;
    }

    public static PayHistory buildNotAvgHistory(String uuid, String payName, PayAttendInfo[] attInfos, Time t, String location, String desc) {
        PayHistory history = new PayHistory();
        history.Type = TYPE_NORMAL_NOT_AVG;
        if (uuid != null) {
            history.UUIDString = uuid;
        }
        history.PayerName = payName;
        history.AttendsInfo = attInfos;
        history.Location = location;
        history.Description = desc;
        history.Money = history.calcMoneySum();
        history.Status = SyncStatus.LOCAL_NEW;
        if (t != null) {
            history.PayTime = t;
        }
        return history;
    }

    private void setAttendNames(String[] attNames) {
        PayAttendInfo[] atts = new PayAttendInfo[attNames.length];
        for (int i = 0; i < attNames.length; i++) {
            atts[i] = new PayAttendInfo(attNames[i], 0.0);
        }
        AttendsInfo = atts;
    }

    public String[] getAttendNames() {
        String[] arr = new String[AttendsInfo.length];
        int i = 0;
        for (PayAttendInfo p : AttendsInfo) {
            arr[i++] = p.getName();
        }
        return arr;
    }

    public double calcMoneySum() {
        if (Type == TYPE_NORMAL_AVG) {
            return Money;
        }
        else {
            double sum = 0.0;
            for (PayAttendInfo p : AttendsInfo) {
                sum += p.getMoney();
            }
            return sum;
        }
    }

    private boolean validateMoneySum() {
        if (Type == TYPE_NORMAL_AVG) {
            return true;
        }
        else {
            double sum = calcMoneySum();
            if (Math.abs(sum - Money) < 0.01) {
                return true;
            }
        }
        return false;
    }

    public boolean validate()
    {
        return  (this.Money > 0 && this.PayerName != null && this.PayerName.trim().length() > 0
                &&  this.AttendsInfo != null && this.AttendsInfo.length > 0 && validateMoneySum());
    }

    public String encodeAttends(int type) {
        StringBuffer sb = new StringBuffer();
        if (type == TYPE_NORMAL_AVG) {
            for (PayAttendInfo p : AttendsInfo) {
                sb.append(p.getName());
                sb.append(",");
            }
        }
        else {
            for (PayAttendInfo p : AttendsInfo) {
                sb.append(p.toString());
                sb.append(",");
            }
        }
        if (sb.length() > 0)
        {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    public void decodeAttends(String str, int type) {
        String[] arrStr = str.split(",");
        if (type == TYPE_NORMAL_AVG) {
            setAttendNames(arrStr);
        }
        else {
            PayAttendInfo[] arr = new PayAttendInfo[arrStr.length];
            for (int i = 0; i < arr.length; i++) {
                arr[i] = PayAttendInfo.decode(arrStr[i]);
            }
            AttendsInfo = arr;
        }
    }

    public PayAttendInfo[] getAttendsInfo() {
        return AttendsInfo;
    }

    public PayPerson getPayer() {
        return PayPersonManager.get(this.PayerName);
    }
    public PayPerson[] getAttends() {
        PayPerson[] arr = new PayPerson[AttendsInfo.length];
        int i = 0;
        for (PayAttendInfo p : AttendsInfo) {
            arr[i++] = PayPersonManager.get(p.getName());
        }
        return arr;
    }

    public static boolean compare(PayHistory h1, PayHistory h2) {
       if (Math.abs(h1.Money - h2.Money) > 0.01
             || h1.PayerName.compareTo(h2.PayerName) != 0
             || h1.Location.compareTo(h2.Location) != 0
             || h1.AttendsInfo.length != h2.AttendsInfo.length
             || h1.Description.compareTo(h2.Description) != 0
             || Time.compare(h1.PayTime, h2.PayTime) != 0) {
           return false;
       }

       for (int i = 0; i < h1.AttendsInfo.length; i++) {
           if (h1.AttendsInfo[i].getName().compareTo(h2.AttendsInfo[i].getName()) != 0 ||
               Math.abs(h1.AttendsInfo[i].getMoney() - h2.AttendsInfo[i].getMoney()) > 0.01) {
               return false;
           }
       }
       return true;
    }
}
