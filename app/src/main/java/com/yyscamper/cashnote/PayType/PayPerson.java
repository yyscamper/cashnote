package com.yyscamper.cashnote.PayType;

import android.text.format.Time;

public class PayPerson {
	public String Name;
	public long PayCount;
	public long AttendCount;
	public double Balance;
    public Time LastModifyTime;
	public int Status;
	public PayPerson(String name) {
		Name = name;
		PayCount = 0;
		AttendCount = 0;
        Balance = 0.0;
        LastModifyTime = new Time();
        LastModifyTime.setToNow();
        Status = SyncStatus.UNKNOWN;
	}

    public PayPerson() {
        LastModifyTime = new Time();
        LastModifyTime.setToNow();
        Status = SyncStatus.UNKNOWN;
    }

    public boolean validate() {
        return true;
    }
}
