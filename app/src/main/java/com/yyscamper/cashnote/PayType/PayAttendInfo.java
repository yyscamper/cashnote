package com.yyscamper.cashnote.PayType;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Snowing on 2014/4/3.
 */
public class PayAttendInfo implements Parcelable {
    private String mName;
    private double mMoney;

    public PayAttendInfo() {

    }

    public PayAttendInfo(Parcel in) {
        mName = in.readString();
        mMoney = in.readDouble();
    }

    public PayAttendInfo(String mName, double money) {
        this.mName = mName;
        this.mMoney = money;
    }

    public String getName() { return mName;}
    public void setName(String mName) { this.mName = mName; }
    public double getMoney() { return mMoney; }
    public void setMoney(double money) { mMoney = money; }

    public String toString() {
        int iMoney = (int)mMoney;
        if (Math.abs(mMoney - iMoney) < 0.1) {
            return String.format("%s:%d", mName, iMoney);
        }
        else {
            return String.format("%s:%.1f", mName, mMoney);
        }
    }

    public String encode() {
        return String.format("%s:%.2f", mName, mMoney);
    }

    public static PayAttendInfo decode(String str) {
        if (str == null || str.length() <= 3) {
            return null;
        }
        int pos = str.indexOf(':');
        String name = str.substring(0, pos);
        String strMoney = str.substring(pos + 1, str.length());
        double money = Double.parseDouble(strMoney);
        return new PayAttendInfo(name, money);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mName);
        parcel.writeDouble(mMoney);
    }

    public static final Parcelable.Creator<PayAttendInfo> CREATOR = new ClassLoaderCreator<PayAttendInfo>() {

        @Override
        public PayAttendInfo createFromParcel(Parcel parcel, ClassLoader classLoader) {
            return new PayAttendInfo(parcel);
        }

        @Override
        public PayAttendInfo createFromParcel(Parcel parcel) {
            return new PayAttendInfo(parcel);
        }

        @Override
        public PayAttendInfo[] newArray(int size) {
            return new PayAttendInfo[size];
        }
    };
}