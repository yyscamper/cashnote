package com.yyscamper.cashnote.PayType;

/**
 * Created by yuanf on 2014-03-30.
 */
public enum StorageSelector {
    NONE(0x00),
    CACHE(0x01),
    LOCAL(0x02),
    CLOUD(0x04),
    LOCAL_CLOUD(0x02 | 0x04),
    ALL(0xFFFFFFFF);

    private int mValue;
    private StorageSelector(int val) {
        mValue = val;
    }

    public int getValue() { return mValue; }

    public static StorageSelector fromValue(int value) {
        if (value == 0x01) {
            return CACHE;
        }
        else if (value == 0x02) {
            return LOCAL;
        }
        else if (value == 0x03) {
            return CLOUD;
        }
        else if ((value & 0x07) == 0x07) {
            return ALL;
        }
        else {
            return NONE;
        }
    }
}
