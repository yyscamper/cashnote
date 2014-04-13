package com.yyscamper.cashnote.PayType;

import java.util.*;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.format.*;

import com.baidu.frontia.FrontiaData;
import com.yyscamper.cashnote.Enum.DataType;
import com.yyscamper.cashnote.Storage.CacheStorage;
import com.yyscamper.cashnote.Storage.StorageConst;
import com.yyscamper.cashnote.Storage.StorageObject;

import org.json.JSONObject;

public class PayHistory extends StorageObject {
    private int PayType;
    private double Money;
    private String PayerName;
    private Time PayTime;
    private String LocationName;
    private String Description;
    private PayAttendInfo[] AttendsInfo;

    public static final int PAY_TYPE_NORMAL_AVG = 0;
    public static final int PAY_TYPE_NORMAL_NOT_AVG = 1;

    public int getPayType() { return PayType; }
    public double getMoney() { return Money; }
    public String getPayerName() { return PayerName; }
    public Time getPayTime() { return PayTime; }
    public String getLocationName() { return LocationName; }
    public String getDescription() { return Description; }

    public PayPerson getPayer() {
        return CacheStorage.getInstance().getPerson(this.PayerName);
    }

    public PayAttendInfo[] getAttendsInfo() {
        return AttendsInfo;
    }


    public PayPerson[] getAttendPersons() {
        PayPerson[] arr = new PayPerson[AttendsInfo.length];
        int i = 0;
        for (PayAttendInfo p : AttendsInfo) {
            arr[i++] = CacheStorage.getInstance().getPerson(PayerName);
        }
        return arr;
    }

    public String[] getAttendNames() {
        String[] arr = new String[AttendsInfo.length];
        int i = 0;
        for (PayAttendInfo p : AttendsInfo) {
            arr[i++] = p.getName();
        }
        return arr;
    }

    public PayLocation getLocation() {
        return CacheStorage.getInstance().getLocation(LocationName);
    }

    public void setPayType(int payType) {
        PayType = payType;
    }

    public void setMoney(double money) {
        if (money < 0 || Math.abs(money) <= 0.01) {
            money = 0;
        }
        else {
            Money = money;
        }
    }

    public void setPayerName(String payerName) {
        PayerName = payerName;
    }

    public void setPayTime(Time payTime) {
        PayTime = payTime;
    }

    public void setLocationName(String location) {
        LocationName = location;
    }

    public void setDescription(String description) {
        Description = description;
    }

    private void setAttendNames(String[] attNames) {
        PayAttendInfo[] atts = new PayAttendInfo[attNames.length];
        for (int i = 0; i < attNames.length; i++) {
            atts[i] = new PayAttendInfo(attNames[i], 0.0);
        }
        AttendsInfo = atts;
    }

    public PayHistory() {
        super(DataType.HISTORY, UUID.randomUUID().toString());
		Money = 0.0;
		PayTime = new Time();
        PayTime.setToNow();
        PayerName = null;
        AttendsInfo = null;
		LocationName = "";
		Description = "";
        PayType = PAY_TYPE_NORMAL_AVG;
	}

    public static PayHistory buildAvgHistory(String uuid, double totalMoney, String payName, String[] attNames, Time t, String location, String desc) {
        PayHistory history = new PayHistory();
        if (uuid != null) {
            history.setKey(uuid);
        }
        history.Money = totalMoney;
        history.PayType = PAY_TYPE_NORMAL_AVG;
        history.PayerName = payName;
        history.setAttendNames(attNames);
        history.LocationName = location;
        history.Description = desc;
        if (t != null) {
            history.PayTime = t;
        }
        return history;
    }

    public static PayHistory buildNotAvgHistory(String uuid, String payName, PayAttendInfo[] attInfos, Time t, String location, String desc) {
        PayHistory history = new PayHistory();
        history.PayType = PAY_TYPE_NORMAL_NOT_AVG;
        if (uuid != null) {
            history.setKey(uuid);
        }
        history.PayerName = payName;
        history.AttendsInfo = attInfos;
        history.LocationName = location;
        history.Description = desc;
        history.Money = history.calcMoneySum();
        if (t != null) {
            history.PayTime = t;
        }
        return history;
    }



    public double calcMoneySum() {
        if (PayType == PAY_TYPE_NORMAL_AVG) {
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
        if (PayType == PAY_TYPE_NORMAL_AVG) {
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
        boolean result = super.validate();
        if (!result) {
            return false;
        }
        return  (this.Money > 0 && this.PayerName != null && this.PayerName.trim().length() > 0
                &&  this.AttendsInfo != null && this.AttendsInfo.length > 0 && validateMoneySum());
    }



    public static boolean compare(PayHistory h1, PayHistory h2) {
       if (Math.abs(h1.Money - h2.Money) > 0.01
             || h1.PayerName.compareTo(h2.PayerName) != 0
             || h1.LocationName.compareTo(h2.LocationName) != 0
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

    public String encodeAttends(int type) {
        StringBuffer sb = new StringBuffer();
        if (type == PAY_TYPE_NORMAL_AVG) {
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
        if (type == PAY_TYPE_NORMAL_AVG) {
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


    @Override
    public ContentValues convertToSqliteObject() {
        ContentValues values = super.convertToSqliteObject();
        values.put(StorageConst.KEY_HISTORY_TYPE, PayType);
        values.put(StorageConst.KEY_HISTORY_MONEY, Money);
        values.put(StorageConst.KEY_HISTORY_PAYER_NAME, PayerName);
        values.put(StorageConst.KEY_HISTORY_LOCATION, LocationName);
        values.put(StorageConst.KEY_HISTORY_ATTENDS, encodeAttends(PayType));
        values.put(StorageConst.KEY_HISTORY_TIME, PayTime.toMillis(true));
        values.put(StorageConst.KEY_HISTORY_DESCRIPTION, Description);
        return values;
    }

    @Override
    public boolean parseSqliteObject(Cursor c) {
        if(!super.parseSqliteObject(c)) {
            return false;
        }
        PayType = c.getInt(c.getColumnIndex(StorageConst.KEY_HISTORY_TYPE));
        Money = c.getDouble(c.getColumnIndex(StorageConst.KEY_HISTORY_MONEY));
        PayerName = c.getString(c.getColumnIndex(StorageConst.KEY_HISTORY_PAYER_NAME));
        LocationName = c.getString(c.getColumnIndex(StorageConst.KEY_HISTORY_LOCATION));
        long t = c.getLong(c.getColumnIndex(StorageConst.KEY_HISTORY_TIME));
        PayTime.set(t);
        Description = c.getString(c.getColumnIndex(StorageConst.KEY_HISTORY_DESCRIPTION));
        decodeAttends(c.getString(c.getColumnIndex(StorageConst.KEY_HISTORY_ATTENDS)), PayType);
        return true;
    }

    @Override
    public FrontiaData convertToFrontiaData() {
        FrontiaData values = super.convertToFrontiaData();
        values.put(StorageConst.KEY_HISTORY_TYPE, PayType);
        values.put(StorageConst.KEY_HISTORY_MONEY, Money);
        values.put(StorageConst.KEY_HISTORY_PAYER_NAME, PayerName);
        values.put(StorageConst.KEY_HISTORY_LOCATION, LocationName);
        values.put(StorageConst.KEY_HISTORY_ATTENDS, encodeAttends(PayType));
        values.put(StorageConst.KEY_HISTORY_TIME, PayTime.toMillis(true));
        values.put(StorageConst.KEY_HISTORY_DESCRIPTION, Description);
        return values;
    }

    @Override
    public boolean parseFrontiaData(FrontiaData data) {
        if (!super.parseFrontiaData(data))
            return false;
        JSONObject json = data.toJSON();
        try {
            PayType = json.getInt(StorageConst.KEY_HISTORY_TYPE);
            Money = json.getDouble(StorageConst.KEY_HISTORY_MONEY);
            PayerName = json.getString(StorageConst.KEY_HISTORY_PAYER_NAME);
            LocationName = json.getString(StorageConst.KEY_HISTORY_LOCATION);
            Description = json.getString(StorageConst.KEY_HISTORY_DESCRIPTION);
            long t = json.getLong(StorageConst.KEY_HISTORY_TIME);
            PayTime.set(t);
            decodeAttends(json.getString(StorageConst.KEY_HISTORY_ATTENDS), PayType);
        }
        catch (Throwable err) {
            return false;
        }
        return true;
    }
}
