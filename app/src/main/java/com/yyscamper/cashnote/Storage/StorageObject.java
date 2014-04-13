package com.yyscamper.cashnote.Storage;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.format.Time;

import com.baidu.frontia.FrontiaACL;
import com.baidu.frontia.FrontiaData;
import com.yyscamper.cashnote.Enum.CloudStatus;
import com.yyscamper.cashnote.Enum.DataType;
import com.yyscamper.cashnote.PayType.AccountBookParam;
import com.yyscamper.cashnote.Enum.LocalStatus;

import org.json.JSONObject;

/**
 * Created by yuanf on 2014-04-12.
 */
public class StorageObject {
    private String Key;
    private DataType Type;
    private String AccountBookName;
    private CloudStatus CloudStatus;
    private LocalStatus LocalStatus;
    private Time LastModifyTime;
    private Time CreatedTime;
    private Time LastSyncTime;
    private String LastModifyUserID;
    private String CreatedUserID;
    private int Version;

    public boolean isDirty() {
        return Dirty;
    }

    public void setDirty(boolean dirty) {
        Dirty = dirty;
    }

    private boolean Dirty;

    private void commonInit() {
        AccountBookName = AccountBookParam.getCurrAccountBookName();
        CloudStatus = CloudStatus.NORMAL;
        LocalStatus = LocalStatus.UNKNOWN;
        Version = 1;
        Dirty = false;

        LastModifyTime = new Time();
        CreatedTime = new Time();
        LastSyncTime = new Time();
        LastModifyTime.setToNow();
        CreatedTime.setToNow();
        LastSyncTime.set(1, 1, 1900); //init sync time to very old

        CreatedUserID = AccountBookParam.getCurrUserID();
        LastModifyUserID = CreatedUserID;
    }

    public StorageObject(DataType type) {
        Key = "";
        Type = type;
        commonInit();
    }

    public StorageObject(DataType type, String key) {
        Key = key;
        Type = type;
        commonInit();
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public DataType getType() {
        return Type;
    }

    public void setType(DataType type) {
        Type = type;
    }

    public String getAccountBookName() {
        return AccountBookName;
    }

    public void setAccountBookName(String accountBookName) {
        AccountBookName = accountBookName;
    }

    public CloudStatus getCloudStatus() {
        return CloudStatus;
    }

    public void setCloudStatus(CloudStatus cloudStatus) {
        CloudStatus = cloudStatus;
    }

    public LocalStatus getLocalStatus() {
        return LocalStatus;
    }

    public void setLocalStatus(LocalStatus localStatus) {
        LocalStatus = localStatus;
    }

    public Time getLastModifyTime() {
        return LastModifyTime;
    }

    public void setLastModifyTime(Time lastModifyTime) {
        LastModifyTime = lastModifyTime;
    }

    public Time getCreatedTime() {
        return CreatedTime;
    }

    public void setCreatedTime(Time createdTime) {
        CreatedTime = createdTime;
    }

    public Time getLastSyncTime() {
        return LastSyncTime;
    }

    public void setLastSyncTime(Time lastSyncTime) {
        LastSyncTime = lastSyncTime;
    }

    public String getLastModifyUserID() {
        return LastModifyUserID;
    }

    public void setLastModifyUserID(String lastModifyUserID) {
        LastModifyUserID = lastModifyUserID;
    }

    public String getCreatedUserID() {
        return CreatedUserID;
    }

    public void setCreatedUserID(String createdUserID) {
        CreatedUserID = createdUserID;
    }

    public int getVersion() {
        return Version;
    }

    public void setVersion(int version) {
        Version = version;
    }

    protected ContentValues convertToSqliteObject() {
        ContentValues values = new ContentValues();
        values.put(StorageConst.KEY_KEY, Key);
        values.put(StorageConst.KEY_DATA_TYPE, Type.getValue());
        values.put(StorageConst.KEY_VERSION, Version);
        values.put(StorageConst.KEY_ACCOUNT_BOOK, AccountBookName);
        values.put(StorageConst.KEY_CLOUD_STATUS, CloudStatus.getValue());
        values.put(StorageConst.KEY_CREATED_TIME, CreatedTime.toMillis(true));
        values.put(StorageConst.KEY_LAST_MODIFIY_TIME, LastModifyTime.toMillis(true));
        values.put(StorageConst.KEY_CREATED_USER_ID, CreatedUserID);
        values.put(StorageConst.KEY_LAST_MODIFY_USER_ID, LastModifyUserID);

        values.put(StorageConst.KEY_LAST_SYNC_TIME, LastSyncTime.toMillis(true));
        return values;
    }

    protected boolean parseSqliteObject(Cursor c) {
        long t;
        Key = c.getString(c.getColumnIndex(StorageConst.KEY_KEY));
        Type = DataType.parse(c.getInt(c.getColumnIndex(StorageConst.KEY_DATA_TYPE)));
        Version = c.getInt(c.getColumnIndex(StorageConst.KEY_VERSION));
        AccountBookName = c.getString(c.getColumnIndex(StorageConst.KEY_ACCOUNT_BOOK));
        CloudStatus = CloudStatus.parse(c.getInt(c.getColumnIndex(StorageConst.KEY_CLOUD_STATUS)));
        CreatedUserID = c.getString(c.getColumnIndex(StorageConst.KEY_CREATED_USER_ID));
        LastModifyUserID = c.getString(c.getColumnIndex(StorageConst.KEY_LAST_MODIFY_USER_ID));
        t = c.getLong(c.getColumnIndex(StorageConst.KEY_CREATED_TIME));
        CreatedTime.set(t);
        t = c.getLong(c.getColumnIndex(StorageConst.KEY_LAST_MODIFIY_TIME));
        LastModifyTime.set(t);

        t = c.getLong(c.getColumnIndex(StorageConst.KEY_LAST_SYNC_TIME));
        LastSyncTime.set(t);
        return true;
    }

    private void setPublicACL(FrontiaData data) {
        FrontiaACL rwACL = new FrontiaACL();
        rwACL.setPublicReadable(true);
        rwACL.setPublicWritable(true);
        data.setACL(rwACL);
    }

    protected FrontiaData convertToFrontiaData() {
        FrontiaData values = new FrontiaData();
        setPublicACL(values);

        values.put(StorageConst.KEY_KEY, Key);
        values.put(StorageConst.KEY_DATA_TYPE, Type.getValue());
        values.put(StorageConst.KEY_VERSION, Version);
        values.put(StorageConst.KEY_ACCOUNT_BOOK, AccountBookName);
        values.put(StorageConst.KEY_CLOUD_STATUS, CloudStatus.getValue());
        values.put(StorageConst.KEY_CREATED_TIME, CreatedTime.toMillis(true));
        values.put(StorageConst.KEY_LAST_MODIFIY_TIME, LastModifyTime.toMillis(true));
        values.put(StorageConst.KEY_CREATED_USER_ID, CreatedUserID);
        values.put(StorageConst.KEY_LAST_MODIFY_USER_ID, LastModifyUserID);

        //Cloud don't need the lastSyncTime
        //values.put(StorageConst.KEY_LAST_SYNC_TIME, LastSyncTime.toMillis(true));
        return values;
    }

    protected boolean parseFrontiaData(FrontiaData data) {
        JSONObject json = data.toJSON();
        long t;
        try {
            Key = json.getString(StorageConst.KEY_KEY);
            Type = DataType.parse(json.getInt(StorageConst.KEY_DATA_TYPE));
            Version = json.getInt(StorageConst.KEY_VERSION);
            AccountBookName = json.getString(StorageConst.KEY_ACCOUNT_BOOK);
            CloudStatus = CloudStatus.parse(json.getInt(StorageConst.KEY_CLOUD_STATUS));
            CreatedUserID = json.getString(StorageConst.KEY_CREATED_USER_ID);
            LastModifyUserID = json.getString(StorageConst.KEY_LAST_MODIFY_USER_ID);
            t = json.getLong(StorageConst.KEY_CREATED_TIME);
            CreatedTime.set(t);
            t = json.getLong(StorageConst.KEY_LAST_MODIFIY_TIME);
            LastModifyTime.set(t);

            //t = json.getLong(StorageConst.KEY_LAST_SYNC_TIME);
            //LastSyncTime.set(t);
            return true;
        }
        catch (Throwable err) {
            return false;
        }
    }

    public boolean validate() {
        return true;
    }

    protected void copyFrom(StorageObject obj) {
        Key = obj.Key;
        Version = obj.Version;
        Type = obj.Type;
        LocalStatus = obj.LocalStatus;
        AccountBookName = obj.AccountBookName;
        CloudStatus = obj.CloudStatus;
        CreatedUserID = obj.CreatedUserID;
        LastModifyUserID = obj.LastModifyUserID;
        CreatedTime.set(obj.CreatedTime);
        LastModifyTime.set(obj.LastModifyTime);
        LastSyncTime.set(obj.LastSyncTime);
    }
}
