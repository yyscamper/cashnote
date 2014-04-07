package com.yyscamper.cashnote.PayType;

import android.text.format.Time;

public class PayPerson {
	public String Name;
    public long PayCount;
    public long AttendCount;
    public double Balance;
    public Time LastModifyTime;
    public int Status;
    public String Email;
    public String Phone;

	public PayPerson(String name) {
		Name = name;
		PayCount = 0;
		AttendCount = 0;
        Balance = 0.0;
        LastModifyTime = new Time();
        LastModifyTime.setToNow();
        Status = SyncStatus.UNKNOWN;

        Email = "unknown@gmail.com";
        Phone = "12892571479";
	}

    public PayPerson() {
        LastModifyTime = new Time();
        LastModifyTime.setToNow();
        Status = SyncStatus.UNKNOWN;
        Email = "unknown@gmail.com";
        Phone = "12892571479";
    }

    public void copyFrom(PayPerson p) {
        Name = p.Name;
        PayCount = p.PayCount;
        AttendCount = p.AttendCount;
        Balance = p.Balance;
        Email = p.Email;
        Phone = p.Phone;
        Status = p.Status;
        LastModifyTime.set(p.LastModifyTime);
    }

    public boolean validate() {
        return true;
    }
}
