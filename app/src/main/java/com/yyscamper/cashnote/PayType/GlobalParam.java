package com.yyscamper.cashnote.PayType;

/**
 * Created by yuanf on 2014-04-12.
 */
public class GlobalParam {
    private String mCurrDBName = "cashnote_test";

    private static GlobalParam mInstance = null;

    private GlobalParam() {

    }

    public static GlobalParam getInstance() {
        if (mInstance == null) {
            mInstance = new GlobalParam();
        }
        return mInstance;
    }

    public String getCurrDBName() { return  mCurrDBName; }
    public void   setCurrDBName(String name) { mCurrDBName = name; }
}
