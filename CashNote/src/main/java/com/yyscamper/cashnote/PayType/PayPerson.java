package com.yyscamper.cashnote.PayType;

public class PayPerson {
	public String Name;
	public long PayCount;
	public long AttendCount;
	public double Balance;
	
	public PayPerson(String name) {
		Name = name;
		PayCount = 0;
		AttendCount = 0;
        Balance = 0.0;
	}
}
