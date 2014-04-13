package com.yyscamper.cashnote.PayType;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.baidu.frontia.FrontiaData;
import com.yyscamper.cashnote.Enum.DataType;
import com.yyscamper.cashnote.Storage.StorageConst;
import com.yyscamper.cashnote.Storage.StorageObject;

import org.json.JSONObject;

public class PayPerson extends StorageObject {
    private long PayCount;
    private long AttendCount;
    private double Balance;
    private String Email;
    private String Phone;

    public String getName() {
        return getKey();
    }

    public void setName(String name) {
        setKey(name);
    }

    public long getPayCount() {
        return PayCount;
    }

    public long getAttendCount() {
        return AttendCount;
    }

    public double getBalance() {
        return Balance;
    }

    public String getEmail() {
        return Email;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPayCount(long payCount) {
        if (payCount < 0)
            payCount = 0;
        else
            PayCount = payCount;
    }

    public void setAttendCount(long attendCount) {
        if (attendCount < 0)
            attendCount = 0;
        else
            AttendCount = attendCount;
    }

    public void setBalance(double balance) {
        Balance = balance;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    private void commonInit() {
        PayCount = 0;
        AttendCount = 0;
        Balance = 0.0;
        Email = "unknown@gmail.com";
        Phone = "12892571479";
    }

	public PayPerson(String name) {
        super(DataType.PERSON, name);
		commonInit();
	}

    public PayPerson() {
        super(DataType.PERSON);
        commonInit();
    }

    public void copyFrom(PayPerson p) {
        super.copyFrom(p);
        PayCount = p.PayCount;
        AttendCount = p.AttendCount;
        Balance = p.Balance;
        Email = p.Email;
        Phone = p.Phone;
    }

    @Override
    public boolean validate() {
        return super.validate();
    }

    @Override
    public ContentValues convertToSqliteObject() {
        ContentValues values = super.convertToSqliteObject();
        values.put(StorageConst.KEY_PERSON_BALANCE, Balance);
        values.put(StorageConst.KEY_PERSON_ATTEND_COUNT, AttendCount);
        values.put(StorageConst.KEY_PERSON_PAY_COUNT, PayCount);
        values.put(StorageConst.KEY_PERSON_EMAIL, Email);
        values.put(StorageConst.KEY_PERSON_PHONE, Phone);
        return values;
    }

    @Override
    public boolean parseSqliteObject(Cursor c) {
        super.parseSqliteObject(c);
        Balance = c.getDouble(c.getColumnIndex(StorageConst.KEY_PERSON_BALANCE));
        AttendCount = c.getInt(c.getColumnIndex(StorageConst.KEY_PERSON_ATTEND_COUNT));
        PayCount = c.getInt(c.getColumnIndex(StorageConst.KEY_PERSON_PAY_COUNT));
        Email = c.getString(c.getColumnIndex(StorageConst.KEY_PERSON_EMAIL));
        Phone = c.getString(c.getColumnIndex(StorageConst.KEY_PERSON_PHONE));
        return true;
    }

    @Override
    public FrontiaData convertToFrontiaData() {
        FrontiaData values = super.convertToFrontiaData();
        values.put(StorageConst.KEY_PERSON_BALANCE, Balance);
        values.put(StorageConst.KEY_PERSON_ATTEND_COUNT, AttendCount);
        values.put(StorageConst.KEY_PERSON_PAY_COUNT, PayCount);
        values.put(StorageConst.KEY_PERSON_EMAIL, Email);
        values.put(StorageConst.KEY_PERSON_PHONE, Phone);
        return values;
    }

    @Override
    public boolean parseFrontiaData(FrontiaData data) {
        if (!super.parseFrontiaData(data))
            return false;

        JSONObject json = data.toJSON();
        try {
            Balance = json.getDouble(StorageConst.KEY_PERSON_BALANCE);
            AttendCount = json.getInt(StorageConst.KEY_PERSON_ATTEND_COUNT);
            PayCount = json.getInt(StorageConst.KEY_PERSON_PAY_COUNT);
            Email = json.getString(StorageConst.KEY_PERSON_EMAIL);
            Phone = json.getString(StorageConst.KEY_PERSON_PHONE);
        }
        catch (Throwable err) {
            return false;
        }

        return true;
    }
}
