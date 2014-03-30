package com.yyscamper.cashnote.PayType;

/**
 * Created by yuanf on 2014-03-29.
 */
public class SyncStatus {
    public static final int UNKNOWN = 0x00;
    public static final int SERVER_SYNCED = 0x01;
    public static final int SERVER_UPDATED = 0x02;
    public static final int SERVER_DELETED = 0x03;
    public static final int LOCAL_NEW = 0x10;
    public static final int LOCAL_UPDATED = 0x11;
    public static final int LOCAL_DELETED = 0x12;
    public static final int CACHE_NEW = 0x20;
    public static final int CACHE_UPDATED = 0x21;
    public static final int CACHE_DELETED = 0x22;
}

