package com.yyscamper.cashnote.Storage;

import android.content.Context;

import com.baidu.frontia.*;
import com.baidu.frontia.api.FrontiaStorage;
import com.baidu.frontia.api.FrontiaStorageListener;
import com.yyscamper.cashnote.Enum.DataType;
import com.yyscamper.cashnote.Interface.GeneralResultCode;
import com.yyscamper.cashnote.Interface.StorageListener;
import com.yyscamper.cashnote.Util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuanf on 2014-04-07.
 */
public class CloudStorage implements BaseStorage.AsyncStorage {
    Context mContext;
    private String mAccountBookName;
    private FrontiaStorage mCloudStorage;
    private FrontiaStorageListener.DataInsertListener mDataInsertListener;
    private FrontiaStorageListener.DataOperationListener mDataOperationListener;
    private StorageListener.StorageOperationListener mStorageOperationListener = null;

    private void initCommonListener() {
        mDataInsertListener = new FrontiaStorageListener.DataInsertListener() {
            @Override
            public void onSuccess() {
                if (mStorageOperationListener != null) {
                    mStorageOperationListener.onOperationFinished(GeneralResultCode.RESULT_SUCCESS, 1, 0, null);
                }
            }

            @Override
            public void onFailure(int errCode, String errMsg) {
                if (mStorageOperationListener != null) {
                    mStorageOperationListener.onOperationFinished(GeneralResultCode.RESULT_FAILED, 0, errCode, errMsg);
                }
            }
        };

        mDataOperationListener = new FrontiaStorageListener.DataOperationListener() {
            @Override
            public void onSuccess(long count) {
                if (mStorageOperationListener != null) {
                    mStorageOperationListener.onOperationFinished(GeneralResultCode.RESULT_SUCCESS, count, 0, null);
                }
            }

            @Override
            public void onFailure(int errCode, String errMsg) {
                if (mStorageOperationListener != null) {
                    mStorageOperationListener.onOperationFinished(GeneralResultCode.RESULT_FAILED, 0, errCode, errMsg);
                }
            }
        };
    }

    public void setContext(Context ctx) {
        mContext = ctx;
    }

    public void setAccountBookName(String name) {
        mAccountBookName = name;
    }

    @Override
    public int initStorage() {
        boolean isInit = Frontia.init(mContext, "VdaXHq7zdLIGa63vmGrZVrtx");
        if (isInit)
            mCloudStorage = Frontia.getStorage();
        return isInit ? 0 : -1;
    }

    @Override
    public int closeStorage() {
        return 0;
    }

    public CloudStorage(Context ctx, String dbName) {
        mContext = ctx;
        mAccountBookName = dbName;
        initCommonListener();
    }

    private FrontiaQuery buildKeyQuery(DataType dataType, String keyName, String value) {
        FrontiaQuery q1 = new FrontiaQuery();
        q1.equals(StorageConst.KEY_DATA_TYPE, dataType.getValue());
        FrontiaQuery q2 = new FrontiaQuery();
        q2.equals(StorageConst.KEY_ACCOUNT_BOOK, mAccountBookName);
        FrontiaQuery q3 = new FrontiaQuery();
        q3.equals(keyName, value);
        return q2.and(q1).and(q3);
    }

    private FrontiaQuery buildAllQuery(DataType dataType) {
        FrontiaQuery q1 = new FrontiaQuery();
        q1.equals(StorageConst.KEY_DATA_TYPE, dataType.getValue());
        FrontiaQuery q2 = new FrontiaQuery();
        q2.equals(StorageConst.KEY_ACCOUNT_BOOK, mAccountBookName);
        return q2.and(q1);
    }

    @Override
    public int insert(StorageObject obj, StorageListener.StorageOperationListener listener) {
        FrontiaData values = obj.convertToFrontiaData();
        mStorageOperationListener = listener;
        mCloudStorage.insertData(values, mDataInsertListener);
        return 0;
    }

    @Override
    public int remove(DataType dataType, String key, StorageListener.StorageOperationListener listener) {
        FrontiaQuery query = buildKeyQuery(dataType, StorageConst.KEY_KEY, key);
        mStorageOperationListener = listener;
        mCloudStorage.deleteData(query, mDataOperationListener);
        return 0;
    }

    @Override
    public int remove(StorageObject obj, StorageListener.StorageOperationListener listener) {
        return remove(obj.getType(), obj.getKey(), listener);
    }

    @Override
    public int clear(DataType dataType, StorageListener.StorageOperationListener listener) {
        FrontiaQuery query = buildAllQuery(dataType);
        mStorageOperationListener = listener;
        mCloudStorage.deleteData(query, mDataOperationListener);
        return 0;
    }

    @Override
    public int clearAll(StorageListener.StorageOperationListener listener) {
        FrontiaQuery query = new FrontiaQuery();
        query.equals(StorageConst.KEY_ACCOUNT_BOOK, mAccountBookName);
        mStorageOperationListener = listener;
        mCloudStorage.deleteData(query, mDataOperationListener);
        return 0;
    }

    @Override
    public int update(String key, StorageObject obj, StorageListener.StorageOperationListener listener) {
        FrontiaQuery query = buildKeyQuery(obj.getType(), StorageConst.KEY_KEY, key);
        FrontiaData values = obj.convertToFrontiaData();
        mStorageOperationListener = listener;
        mCloudStorage.updateData(query, values, mDataOperationListener);
        return 0;
    }

    @Override
    public int update(StorageObject obj, StorageListener.StorageOperationListener listener) {
        FrontiaQuery query = buildKeyQuery(obj.getType(), StorageConst.KEY_KEY, obj.getKey());
        FrontiaData values = obj.convertToFrontiaData();
        mStorageOperationListener = listener;
        mCloudStorage.updateData(query, values, mDataOperationListener);
        return 0;
    }

    @Override
    public int get(final DataType dataType, String key, final StorageListener.StorageGetListener listener) {
        FrontiaQuery query = buildKeyQuery(dataType, StorageConst.KEY_KEY, key);
        mCloudStorage.findData(query, new FrontiaStorageListener.DataInfoListener() {
            @Override
            public void onSuccess(List<FrontiaData> frontiaDatas) {
                if (frontiaDatas != null && frontiaDatas.size() > 0) {
                    StorageObject item = Util.createByType(dataType);
                    item.parseFrontiaData(frontiaDatas.get(0));
                    if (listener != null) {
                        listener.onGetResult(GeneralResultCode.RESULT_SUCCESS, item, 0, null);
                        return;
                    }
                }
                if (listener != null) {
                    listener.onGetResult(GeneralResultCode.RESULT_FAILED, null, -1, "Unknown error");
                }
            }

            @Override
            public void onFailure(int errCode, String errMsg) {
                if (listener != null) {
                    listener.onGetResult(GeneralResultCode.RESULT_FAILED, null, errCode, errMsg);
                }
            }
        });
        return 0;
    }

    @Override
    public int getAll(final DataType dataType, final StorageListener.StorageGetAllListener listener) {
        FrontiaQuery query = buildAllQuery(dataType);
        final ArrayList listItems = new ArrayList();
        mCloudStorage.findData(query,
                new FrontiaStorageListener.DataInfoListener() {
                    @Override
                    public void onSuccess(List<FrontiaData> dataList) {
                        for(FrontiaData d : dataList) {
                            StorageObject item = Util.createByType(dataType);
                            if (item != null) {
                                item.parseFrontiaData(d);
                                listItems.add(item);
                            }
                        }
                        if (listener != null) {
                            listener.onGetAllResults(GeneralResultCode.RESULT_SUCCESS, listItems, 0, null);
                        }
                    }

                    @Override
                    public void onFailure(int errCode, String errMsg) {
                        if (listener != null) {
                            listener.onGetAllResults(GeneralResultCode.RESULT_FAILED, null, errCode, errMsg);
                        }
                    }
                }
        );
        return 0;
    }
}
