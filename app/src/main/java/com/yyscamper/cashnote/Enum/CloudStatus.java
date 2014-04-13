package com.yyscamper.cashnote.Enum;

/**
 * Created by yuanf on 2014-04-13.
 */
public enum CloudStatus {
    NORMAL (0),
    DELETED (1);

    private int Value;

    CloudStatus(int value) {
        Value = value;
    }

    public int getValue() {
        return Value;
    }

    public CloudStatus parse(int value) {
        if (value == 1)
            return DELETED;
        else
            return NORMAL;
    }
}
