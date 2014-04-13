package com.yyscamper.cashnote.Interface;

import com.yyscamper.cashnote.Storage.StorageObject;

import java.util.ArrayList;

/**
 * Created by yuanf on 2014-04-13.
 */
public interface StorageListener {
    public interface StorageGetAllListener {
        public void onGetAllResults(int result, ArrayList items, int errCode, String errMsg);
    }

    public interface StorageGetListener {
        public void onGetResult(int result, StorageObject obj, int errCode, String errMsg);
    }

    public interface StorageOperationListener {
        public void onOperationFinished(int result, long count, int errCode, String errMsg);
    }
}
