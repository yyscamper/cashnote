package com.yyscamper.cashnote.Storage;

import android.text.format.Time;

/**
 * Created by yuanf on 2014-04-07.
 */
public class TeamStore {
    private String mName;
    private Time mLastModifyTime;

    public TeamStore(String name) {
        mName = name;
        mLastModifyTime = new Time();
        mLastModifyTime.setToNow();
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public Time getLastModifyTime() {
        return mLastModifyTime;
    }

    public void setLastModifyTime(Time t) {
        mLastModifyTime = t;
    }
}
