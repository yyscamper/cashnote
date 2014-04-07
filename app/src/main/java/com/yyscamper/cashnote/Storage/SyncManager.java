package com.yyscamper.cashnote.Storage;

import com.yyscamper.cashnote.PayType.LocationGroup;
import com.yyscamper.cashnote.PayType.LocationGroupManager;
import com.yyscamper.cashnote.PayType.PayHistory;
import com.yyscamper.cashnote.PayType.PayHistoryManager;
import com.yyscamper.cashnote.PayType.PayLocation;
import com.yyscamper.cashnote.PayType.PayLocationManager;
import com.yyscamper.cashnote.PayType.PayPerson;
import com.yyscamper.cashnote.PayType.PayPersonManager;
import com.yyscamper.cashnote.PayType.StorageSelector;
import com.yyscamper.cashnote.PayType.SyncStatus;

import java.util.ArrayList;

/**
 * Created by yuanf on 2014-04-07.
 */
public class SyncManager {

    static CloudStorage mCloudStorage;
    public static void setCloudStorage(CloudStorage s) {
        mCloudStorage = s;
    }

    public static void syncHistory() {
        if (mCloudStorage == null) {
            return;
        }
        ArrayList<PayHistory> allItems = mCloudStorage.downloadAllPayHistories();
        PayHistoryManager.clear(StorageSelector.LOCAL_CACHE);
        for (PayHistory item : allItems) {
            if (item.Status != SyncStatus.SERVER_DELETED) {
                PayHistoryManager.add(item, StorageSelector.LOCAL_CACHE);
            }
        }
    }

    public static void syncPerson() {
        if (mCloudStorage == null) {
            return;
        }
        ArrayList<PayPerson> allItems = mCloudStorage.downloadAllPayPersons();
        PayPersonManager.clear(StorageSelector.LOCAL_CACHE);
        for (PayPerson item : allItems) {
            if (item.Status != SyncStatus.SERVER_DELETED) {
                PayPersonManager.add(item, StorageSelector.LOCAL_CACHE);
            }
        }
    }

    public static void syncLocation() {
        if (mCloudStorage != null) {
            return;
        }

        ArrayList<PayLocation> allItems = mCloudStorage.downloadAllPayLocations();
        PayLocationManager.clear(StorageSelector.LOCAL_CACHE);
        for (PayLocation item : allItems) {
            if (item.Status != SyncStatus.SERVER_DELETED) {
                PayLocationManager.add(item, StorageSelector.LOCAL_CACHE);
            }
        }
    }

    public static void syncLocationGroup() {
        if (mCloudStorage != null) {
            return;
        }

        ArrayList<LocationGroup> allItems = mCloudStorage.downloadAllLocationGroups();
        LocationGroupManager.clear(StorageSelector.LOCAL_CACHE);
        for (LocationGroup item : allItems) {
            if (item.Status != SyncStatus.SERVER_DELETED) {
                LocationGroupManager.add(item, StorageSelector.LOCAL_CACHE);
            }
        }
    }

    public static void syncAll() {
        syncPerson();
        syncLocation();
        syncHistory();
        syncLocationGroup();
    }
}
