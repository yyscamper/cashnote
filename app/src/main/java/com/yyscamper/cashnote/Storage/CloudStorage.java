package com.yyscamper.cashnote.Storage;

import android.content.Context;
import android.widget.Toast;

import com.baidu.frontia.*;
import com.baidu.frontia.api.FrontiaStorage;
import com.baidu.frontia.api.FrontiaStorageListener;
import com.yyscamper.cashnote.PayType.LocationGroup;
import com.yyscamper.cashnote.PayType.PayHistory;
import com.yyscamper.cashnote.PayType.PayLocation;
import com.yyscamper.cashnote.PayType.PayPerson;
import com.yyscamper.cashnote.Util.Util;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuanf on 2014-04-07.
 */
public class CloudStorage {
    Context mContext;
    private String mDbName;
    private FrontiaStorage mCloudStorage;
    private FrontiaStorageListener.DataInsertListener mDataInsertListener;
    private FrontiaStorageListener.DataOperationListener mDataOperationListener;
    private int mCompleteResult = 0;
    public CloudStorage(Context ctx, String dbName) {
        mContext = ctx;
        mDbName = dbName;

        mDataInsertListener = new FrontiaStorageListener.DataInsertListener() {
            @Override
            public void onSuccess() {
                mCompleteResult = 1;
                Toast.makeText(mContext, "新增数据成功!", Toast.LENGTH_LONG);
            }

            @Override
            public void onFailure(int errCode, String errMsg) {
                mCompleteResult = -1;
                Util.showErrorDialog(mContext, "添加到云端数据时出错, 错误码=" + errCode +
                        ", 错误描述:" + errMsg);
            }
        };

        mDataOperationListener = new FrontiaStorageListener.DataOperationListener() {
            @Override
            public void onSuccess(long count) {
                mCompleteResult = 1;
                Toast.makeText(mContext, "数据操作成功! 共计操作" + count + "条数据.", Toast.LENGTH_LONG);
            }

            @Override
            public void onFailure(int errCode, String errMsg) {
                mCompleteResult = -1;
                Util.showErrorDialog(mContext, "删除或更新云端数据出错, 错误码=" + errCode +
                        ", 错误描述:" + errMsg);
            }
        };
    }

    public boolean init() {
        boolean isInit = Frontia.init(mContext, "VdaXHq7zdLIGa63vmGrZVrtx");
        if (isInit)
            mCloudStorage = Frontia.getStorage();
        return isInit;
    }

    public FrontiaData buildHistoryData(PayHistory item) {
        FrontiaData values = new FrontiaData();

        values.put(StorageConst.KEY_DATA_TYPE, StorageConst.DATA_TYPE_HISTORY);
        values.put(StorageConst.KEY_DB_NAME, mDbName);
        values.put(StorageConst.KEY_LAST_MODIFIY_TIME, item.LastModityTime.toMillis(true));
        values.put(StorageConst.KEY_STATUS, item.Status);

        values.put(StorageConst.KEY_PAY_UUID, item.UUIDString);
        values.put(StorageConst.KEY_PAY_TYPE, item.Type);
        values.put(StorageConst.KEY_PAY_MONEY, item.Money);
        values.put(StorageConst.KEY_PAY_PAYER_NAME, item.PayerName);
        values.put(StorageConst.KEY_PAY_ATTENDS, item.encodeAttends(item.Type));
        values.put(StorageConst.KEY_PAY_LOCATION, item.Location);
        values.put(StorageConst.KEY_PAY_DESCRIPTION, item.Description);
        values.put(StorageConst.KEY_PAY_TIME, item.PayTime.toMillis(true));

        return values;
    }

    public FrontiaData buildPersonData(PayPerson item) {
        FrontiaData values = new FrontiaData();

        values.put(StorageConst.KEY_DATA_TYPE, StorageConst.DATA_TYPE_PERSON);
        values.put(StorageConst.KEY_DB_NAME, mDbName);
        values.put(StorageConst.KEY_LAST_MODIFIY_TIME, item.LastModifyTime.toMillis(true));
        values.put(StorageConst.KEY_STATUS, item.Status);

        values.put(StorageConst.KEY_PERSON_NAME, item.Name);
        values.put(StorageConst.KEY_PERSON_BALANCE, item.Balance);
        values.put(StorageConst.KEY_PERSON_ATTEND_COUNT, item.AttendCount);
        values.put(StorageConst.KEY_PERSON_PAY_COUNT, item.PayCount);
        values.put(StorageConst.KEY_PERSON_EMAIL, item.Email);
        values.put(StorageConst.KEY_PERSON_PHONE, item.Phone);

        return values;
    }

    public FrontiaData buildLocationData(PayLocation item) {
        FrontiaData values = new FrontiaData();

        values.put(StorageConst.KEY_DATA_TYPE, StorageConst.DATA_TYPE_LOCATION);
        values.put(StorageConst.KEY_DB_NAME, mDbName);
        values.put(StorageConst.KEY_LAST_MODIFIY_TIME, item.LastModifyTime.toMillis(true));
        values.put(StorageConst.KEY_STATUS, item.Status);

        values.put(StorageConst.KEY_LOCATION_NAME, item.Name);
        values.put(StorageConst.KEY_LOCATION_ATTEND_COUNT, item.AttendCount);
        values.put(StorageConst.KEY_LOCATION_LAST_ATTEND_TIME, item.LastAttendTime.toMillis(true));

        return values;
    }

    public FrontiaData buildLocationGropuData(LocationGroup item) {

        FrontiaData values = new FrontiaData();

        values.put(StorageConst.KEY_DATA_TYPE, StorageConst.DATA_TYPE_LOCATION_GROUP);
        values.put(StorageConst.KEY_DB_NAME, mDbName);
        values.put(StorageConst.KEY_LAST_MODIFIY_TIME, item.LastModifyTime.toMillis(true));
        values.put(StorageConst.KEY_STATUS, item.Status);

        values.put(StorageConst.KEY_LOCATION_GROUP_NAME, item.Name);
        values.put(StorageConst.KEY_LOCATION_GROUP_CHILDREN, Util.stringArrayJoin(item.getChildrenArray(), ","));

        return values;
    }

    public PayHistory parseHistoryData(FrontiaData values) {
        PayHistory item = new PayHistory();
        JSONObject json = values.toJSON();
        long timeMs;
        try {
            //Common fields
            int dataType = json.getInt(StorageConst.KEY_DATA_TYPE);
            if (dataType != StorageConst.DATA_TYPE_HISTORY) {
                return null;
            }
            String dbName = json.getString(StorageConst.KEY_DB_NAME);
            if (!dbName.equalsIgnoreCase(mDbName)) {
                return null;
            }
            item.Status = json.getInt(StorageConst.KEY_STATUS);
            timeMs = json.getLong(StorageConst.KEY_LAST_MODIFIY_TIME);
            item.LastModityTime.set(timeMs);

            //specified fileds
            item.UUIDString = json.getString(StorageConst.KEY_PAY_UUID);
            item.Money = json.getDouble(StorageConst.KEY_PAY_MONEY);
            item.PayerName = json.getString(StorageConst.KEY_PAY_PAYER_NAME);
            item.Location = json.getString(StorageConst.KEY_PAY_LOCATION);
            if (json.has(StorageConst.KEY_PAY_DESCRIPTION)) { //the description can be empty
                item.Description = json.getString(StorageConst.KEY_PAY_DESCRIPTION);
            }
            item.Type = json.getInt(StorageConst.KEY_PAY_TYPE);
            item.decodeAttends(json.getString(StorageConst.KEY_PAY_ATTENDS), item.Type);
            timeMs = json.getLong(StorageConst.KEY_PAY_TIME);
            item.PayTime.set(timeMs);
            return item;
        }
        catch (Throwable err) {
            return null;
        }
    }

    public PayPerson parsePersonData(FrontiaData values) {
        PayPerson item = new PayPerson();
        JSONObject json = values.toJSON();
        long timeMs;
        try {
            //Common fields
            int dataType = json.getInt(StorageConst.KEY_DATA_TYPE);
            if (dataType != StorageConst.DATA_TYPE_PERSON) {
                return null;
            }
            String dbName = json.getString(StorageConst.KEY_DB_NAME);
            if (!dbName.equalsIgnoreCase(mDbName)) {
                return null;
            }
            item.Status = json.getInt(StorageConst.KEY_STATUS);
            timeMs = json.getLong(StorageConst.KEY_LAST_MODIFIY_TIME);
            item.LastModifyTime.set(timeMs);

            //specified fileds
            item.Name = json.getString(StorageConst.KEY_PERSON_NAME);
            item.Balance = json.getDouble(StorageConst.KEY_PERSON_BALANCE);
            item.PayCount = json.getInt(StorageConst.KEY_PERSON_PAY_COUNT);
            item.AttendCount = json.getInt(StorageConst.KEY_PERSON_ATTEND_COUNT);
            item.Email = json.getString(StorageConst.KEY_PERSON_EMAIL);
            item.Phone = json.getString(StorageConst.KEY_PERSON_PHONE);

            return item;
        }
        catch (Throwable err) {
            return null;
        }
    }

    public PayLocation parseLocationData(FrontiaData values) {
        PayLocation item = new PayLocation();
        JSONObject json = values.toJSON();
        long timeMs;
        try {
            //Common fields
            int dataType = json.getInt(StorageConst.KEY_DATA_TYPE);
            if (dataType != StorageConst.DATA_TYPE_LOCATION) {
                return null;
            }
            String dbName = json.getString(StorageConst.KEY_DB_NAME);
            if (!dbName.equalsIgnoreCase(mDbName)) {
                return null;
            }
            item.Status = json.getInt(StorageConst.KEY_STATUS);
            timeMs = json.getLong(StorageConst.KEY_LAST_MODIFIY_TIME);
            item.LastModifyTime.set(timeMs);

            //specified fileds
            item.Name = json.getString(StorageConst.KEY_LOCATION_NAME);
            item.AttendCount = json.getInt(StorageConst.KEY_LOCATION_ATTEND_COUNT);
            timeMs = json.getLong(StorageConst.KEY_LOCATION_LAST_ATTEND_TIME);
            item.LastAttendTime.set(timeMs);

            return item;
        }
        catch (Throwable err) {
            return null;
        }
    }

    public LocationGroup parseLocationGroupData(FrontiaData values) {
        LocationGroup item = new LocationGroup();
        JSONObject json = values.toJSON();
        long timeMs;
        try {
            //Common fields
            int dataType = json.getInt(StorageConst.KEY_DATA_TYPE);
            if (dataType != StorageConst.DATA_TYPE_LOCATION_GROUP) {
                return null;
            }
            String dbName = json.getString(StorageConst.KEY_DB_NAME);
            if (!dbName.equalsIgnoreCase(mDbName)) {
                return null;
            }
            item.Status = json.getInt(StorageConst.KEY_STATUS);
            timeMs = json.getLong(StorageConst.KEY_LAST_MODIFIY_TIME);
            item.LastModifyTime.set(timeMs);

            //specified fileds
            item.Name = json.getString(StorageConst.KEY_LOCATION_GROUP_NAME);
            item.setChildren(json.getString(StorageConst.KEY_LOCATION_GROUP_CHILDREN).split(","));

            return item;
        }
        catch (Throwable err) {
            return null;
        }
    }

    public FrontiaQuery buildKeyQuery(int dataType, String keyName, String value) {
        FrontiaQuery q1 = new FrontiaQuery();
        q1.equals(StorageConst.KEY_DATA_TYPE, dataType);
        FrontiaQuery q2 = new FrontiaQuery();
        q2.equals(StorageConst.KEY_DB_NAME, mDbName);
        FrontiaQuery q3 = new FrontiaQuery();
        q3.equals(keyName, value);
        return q2.and(q1).and(q3);
    }

    public FrontiaQuery buildAllQuery(int dataType) {
        FrontiaQuery q1 = new FrontiaQuery();
        q1.equals(StorageConst.KEY_DATA_TYPE, dataType);
        FrontiaQuery q2 = new FrontiaQuery();
        q2.equals(StorageConst.KEY_DB_NAME, mDbName);
        return q2.and(q1);
    }

    public void insertPayHistory(PayHistory item) {
        FrontiaData values = buildHistoryData(item);
        mCloudStorage.insertData(values, mDataInsertListener);
    }

    public void insertPayPerson(PayPerson item) {
        FrontiaData values = buildPersonData(item);
        mCloudStorage.insertData(values, mDataInsertListener);
    }

    public void insertPayLocation(PayLocation item) {
        FrontiaData values = buildLocationData(item);
        mCloudStorage.insertData(values, mDataInsertListener);
    }

    public void insertLocationGroup(LocationGroup item) {
        FrontiaData values = buildLocationGropuData(item);
        mCloudStorage.insertData(values, mDataInsertListener);
    }

    public boolean insertObject(Object obj) {
        FrontiaData values = null;
        if (obj instanceof PayHistory) {
            values = buildHistoryData((PayHistory)obj);
        }
        else if (obj instanceof PayPerson) {
            values = buildPersonData((PayPerson)obj);
        }
        else if (obj instanceof PayLocation) {
            values = buildLocationData((PayLocation) obj);
        }
        else if (obj instanceof LocationGroup) {
            values = buildLocationGropuData((LocationGroup)obj);
        }
        else {
            return false;
        }
        mCloudStorage.insertData(values, mDataInsertListener);
        return true;
    }

    public void removePayHistory(String uuid) {
        FrontiaQuery query = buildKeyQuery(StorageConst.DATA_TYPE_HISTORY, StorageConst.KEY_PAY_UUID, uuid);
        mCloudStorage.deleteData(query, mDataOperationListener);
    }

    public void removePayPerson(String name) {
        FrontiaQuery query = buildKeyQuery(StorageConst.DATA_TYPE_PERSON, StorageConst.KEY_PERSON_NAME, name);
        mCloudStorage.deleteData(query, mDataOperationListener);
    }

    public void removePayLocation(String name) {
        FrontiaQuery query = buildKeyQuery(StorageConst.DATA_TYPE_LOCATION, StorageConst.KEY_LOCATION_NAME, name);
        mCloudStorage.deleteData(query, mDataOperationListener);
    }

    public void removeLocationGroup(String name) {
        FrontiaQuery query = buildKeyQuery(StorageConst.DATA_TYPE_LOCATION_GROUP, StorageConst.KEY_LOCATION_GROUP_NAME, name);
        mCloudStorage.deleteData(query, mDataOperationListener);
    }

    public void clearAllPayHistories() {
        FrontiaQuery query = buildAllQuery(StorageConst.DATA_TYPE_HISTORY);
        mCloudStorage.deleteData(query, mDataOperationListener);
    }

    public void clearAllPayPersons() {
        FrontiaQuery query = buildAllQuery(StorageConst.DATA_TYPE_PERSON);
        mCloudStorage.deleteData(query, mDataOperationListener);
    }

    public void clearAllPayLocations() {
        FrontiaQuery query = buildAllQuery(StorageConst.DATA_TYPE_LOCATION);
        mCloudStorage.deleteData(query, mDataOperationListener);
    }

    public void clearAllLocationGroups() {
        FrontiaQuery query = buildAllQuery(StorageConst.DATA_TYPE_LOCATION_GROUP);
        mCloudStorage.deleteData(query, mDataOperationListener);
    }

    public void clearAll() {
        clearAllPayHistories();
        clearAllPayPersons();
        clearAllPayLocations();
        clearAllLocationGroups();
    }

    public void updatePayHistory(String uuid, PayHistory item) {
        FrontiaQuery query = buildKeyQuery(StorageConst.DATA_TYPE_HISTORY, StorageConst.KEY_PAY_UUID, uuid);
        FrontiaData values = buildHistoryData(item);
        mCloudStorage.updateData(query, values, mDataOperationListener);
    }

    public void updatePayPerson(String key, PayPerson item) {
        FrontiaQuery query = buildKeyQuery(StorageConst.DATA_TYPE_PERSON, StorageConst.KEY_PERSON_NAME, key);
        FrontiaData values = buildPersonData(item);
        mCloudStorage.updateData(query, values, mDataOperationListener);
    }

    public void updatePayLocation(String key, PayLocation item) {
        FrontiaQuery query = buildKeyQuery(StorageConst.DATA_TYPE_LOCATION, StorageConst.KEY_LOCATION_NAME, key);
        FrontiaData values = buildLocationData(item);
        mCloudStorage.updateData(query, values, mDataOperationListener);
    }

    public void updateLocationGroup(String key, LocationGroup item) {
        FrontiaQuery query = buildKeyQuery(StorageConst.DATA_TYPE_LOCATION_GROUP, StorageConst.KEY_LOCATION_GROUP_NAME, key);
        FrontiaData values = buildLocationGropuData(item);
        mCloudStorage.updateData(query, values, mDataOperationListener);
    }

    public ArrayList<PayHistory> downloadAllPayHistories() {
        FrontiaQuery query = buildAllQuery(StorageConst.DATA_TYPE_HISTORY);
        final ArrayList<PayHistory> listItems = new ArrayList<PayHistory>();
                    mCloudStorage.findData(query,
                            new FrontiaStorageListener.DataInfoListener() {

                        @Override
                        public void onSuccess(List<FrontiaData> dataList) {
                            for(FrontiaData d : dataList) {
                                PayHistory parseResult = parseHistoryData(d);
                                if (parseResult != null) {
                                    listItems.add(parseResult);
                                }
                            }
                        }

                    @Override
                    public void onFailure(int errCode, String errMsg) {
                        Util.showErrorDialog(mContext, "云端加载历史记录出错, 错误码=" + errCode +
                        ", 错误描述:" + errMsg);
                    }
                }
        );
        return listItems;
    }

    public ArrayList<PayPerson> downloadAllPayPersons() {
        FrontiaQuery query = buildAllQuery(StorageConst.DATA_TYPE_PERSON);
        final ArrayList<PayPerson> listItems = new ArrayList<PayPerson>();
        mCompleteResult = 0;
        mCloudStorage.findData(query,
                new FrontiaStorageListener.DataInfoListener() {

                    @Override
                    public void onSuccess(List<FrontiaData> dataList) {
                        for(FrontiaData d : dataList) {
                            PayPerson parseResult = parsePersonData(d);
                            if (parseResult != null) {
                                listItems.add(parseResult);
                            }
                        }
                        mCompleteResult = 1;
                    }

                    @Override
                    public void onFailure(int errCode, String errMsg) {
                        Util.showErrorDialog(mContext, "云端加载成员列表出错, 错误码=" + errCode +
                                ", 错误描述:" + errMsg);
                        mCompleteResult = -1;
                    }
                }
        );

        /*
        while(mCompleteResult == 0) {
            try {
                Thread.sleep(10, 0);
            }
            catch (Exception err) {

            }
        }
        if (mCompleteResult < 0) {
            listItems.clear();
            return listItems;
        } */
        return listItems;
    }

    public ArrayList<PayLocation> downloadAllPayLocations() {
        FrontiaQuery query = buildAllQuery(StorageConst.DATA_TYPE_LOCATION);
        final ArrayList<PayLocation> listItems = new ArrayList<PayLocation>();
        mCompleteResult = 0;
        mCloudStorage.findData(query,
                new FrontiaStorageListener.DataInfoListener() {

                    @Override
                    public void onSuccess(List<FrontiaData> dataList) {
                        for(FrontiaData d : dataList) {
                            PayLocation parseResult = parseLocationData(d);
                            if (parseResult != null) {
                                listItems.add(parseResult);
                            }
                        }
                        mCompleteResult = 1;
                    }

                    @Override
                    public void onFailure(int errCode, String errMsg) {
                        mCompleteResult = -1;
                        Util.showErrorDialog(mContext, "云端加载地点列表出错, 错误码=" + errCode +
                                ", 错误描述:" + errMsg);
                    }
                }
        );

        while(mCompleteResult == 0) {
            try {
                Thread.sleep(10, 0);
            }
            catch (Exception err) {

            }
        }
        if (mCompleteResult < 0) {
            listItems.clear();
            return listItems;
        }

        return listItems;
    }

    public ArrayList<LocationGroup> downloadAllLocationGroups() {
        FrontiaQuery query = buildAllQuery(StorageConst.DATA_TYPE_LOCATION_GROUP);
        final ArrayList<LocationGroup> listItems = new ArrayList<LocationGroup>();
        mCloudStorage.findData(query,
                new FrontiaStorageListener.DataInfoListener() {

                    @Override
                    public void onSuccess(List<FrontiaData> dataList) {
                        for(FrontiaData d : dataList) {
                            LocationGroup parseResult = parseLocationGroupData(d);
                            if (parseResult != null) {
                                listItems.add(parseResult);
                            }
                        }
                    }

                    @Override
                    public void onFailure(int errCode, String errMsg) {
                        Util.showErrorDialog(mContext, "云端加载地点分组出错, 错误码=" + errCode +
                                ", 错误描述:" + errMsg);
                    }
                }
        );
        return listItems;
    }
}
