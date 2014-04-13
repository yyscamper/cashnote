package com.yyscamper.cashnote.Storage;

import com.yyscamper.cashnote.Enum.DataType;
import com.yyscamper.cashnote.Interface.StorageListener;

import java.util.ArrayList;

/**
 * Created by yuanf on 2014-04-13.
 */
public abstract interface BaseStorage {
    public abstract int initStorage();
    public abstract int closeStorage();
    public abstract interface SyncStorage extends BaseStorage {
        public abstract int insert(StorageObject obj);
        public abstract int insert(DataType dataType, ArrayList<StorageObject> objList);
        public abstract int remove(DataType dataType, String key);
        public abstract int remove(StorageObject obj);
        public abstract int clear(DataType dataType);
        public abstract int clearAll();
        public abstract int update(String oldKey, StorageObject obj);
        public abstract int update(StorageObject obj);
        public abstract StorageObject get(DataType dataType, String key);
        public abstract ArrayList<StorageObject> getAll(DataType dataType);
    }

    public abstract interface AsyncStorage extends BaseStorage {
        public abstract int insert(StorageObject obj, StorageListener.StorageOperationListener listener);
        public abstract int remove(DataType dataType, String key, StorageListener.StorageOperationListener listener);
        public abstract int remove(StorageObject obj, StorageListener.StorageOperationListener listener);
        public abstract int clear(DataType dataType, StorageListener.StorageOperationListener listener);
        public abstract int clearAll(StorageListener.StorageOperationListener listener);
        public abstract int update(String oldKey, StorageObject obj, StorageListener.StorageOperationListener listener);
        public abstract int update(StorageObject obj, StorageListener.StorageOperationListener listener);
        public abstract int get(DataType dataType, String key, StorageListener.StorageGetListener listener);
        public abstract int getAll(DataType dataType, StorageListener.StorageGetAllListener listener);
    }
}
