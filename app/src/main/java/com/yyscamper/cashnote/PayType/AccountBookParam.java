package com.yyscamper.cashnote.PayType;

import com.yyscamper.cashnote.Enum.DataType;
import com.yyscamper.cashnote.Storage.StorageObject;

import java.util.UUID;

/**
 * Created by yuanf on 2014-04-12.
 */
public class AccountBookParam extends StorageObject {
    private static String mCurrUserID = "debug";
    private static String CurrAccountBookName = "cashnote_test";

    private AccountBookParam() {
        super(DataType.ACCOUNT_BOOK, UUID.randomUUID().toString());
        mCurrUserID = "debug";
    }

    public static String getCurrUserID() { return mCurrUserID; }
    public static String getCurrAccountBookName() {
        return CurrAccountBookName;
    }
}
