package com.yyscamper.cashnote.Enum;

import com.yyscamper.cashnote.Util.ValuePair;

/**
 * Created by yuanf on 2014-04-13.
 */
public enum DataType {
    UNKNOWN(0),
    HISTORY(1),
    PERSON (2),
    LOCATION(3),
    LOCATION_GROUP(4),
    ACCOUNT_BOOK(5),
    SIZE(5);

    private int Value;

    DataType(int value) {
        Value = value;
    }

    public int getValue() {
        return Value;
    }

    public static DataType parse(int val) {
        switch (val) {
            case 1:
                return HISTORY;
            case 2:
                return PERSON;
            case 3:
                return LOCATION;
            case 4:
                return LOCATION_GROUP;
            case 5:
                return ACCOUNT_BOOK;
            default:
                return UNKNOWN;
        }
    }
}
