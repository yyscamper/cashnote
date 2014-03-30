package com.yyscamper.cashnote.PayType;

import com.yyscamper.cashnote.Storage.LocalStorage;

import java.util.*;

/**
 * Created by YuanYu on 14-3-16.
 */
public class PayHistoryManager
{
    private static Hashtable<String, PayHistory> mAllHistories = new Hashtable<String, PayHistory>();
    private static LocalStorage mLocalStorage = null;

    public static void setLocalStorage(LocalStorage s) {
        mLocalStorage = s;
    }

    public static int size() {
        return mAllHistories.size();
    }

    public static boolean contains(String uuid) {
        return mAllHistories.containsKey(uuid);
    }

    public static PayHistory get(String uuid) {
        return mAllHistories.get(uuid);
    }

    public static PayHistory[] getAllInArray() {
        PayHistory[] arr = new PayHistory[mAllHistories.size()];
        mAllHistories.values().toArray(arr);
        return arr;
    }

    public static ArrayList<PayHistory> getAllInList() {
        ArrayList<PayHistory> list = new ArrayList<PayHistory>();
        for (PayHistory h : mAllHistories.values())
            list.add(h);
        return list;
    }

    public static boolean add(PayHistory p, StorageSelector storageSelect) {
        if (p == null || !p.validate() || mAllHistories.containsKey(p.UUIDString)) {
            return false;
        }

        if (storageSelect == StorageSelector.CACHE || storageSelect == StorageSelector.ALL) {
            mAllHistories.put(p.UUIDString, p);
            p.Status = SyncStatus.CACHE_NEW;
        }

        if (storageSelect == StorageSelector.LOCAL || storageSelect == StorageSelector.ALL) {
            if (mLocalStorage != null) {
                p.Status = SyncStatus.LOCAL_NEW;
                mLocalStorage.insertPayHistory(p);
            }
        }

        if (storageSelect == StorageSelector.CLOUD || storageSelect == StorageSelector.ALL) {
            //TODO:
        }
        return true;
    }

    public static boolean remove(String uuid, StorageSelector storageSelect) {
        if (uuid == null) {
            return false;
        }
        String trimUuid = uuid.trim();

        if (storageSelect == StorageSelector.CACHE || storageSelect == StorageSelector.ALL) {
            if (mAllHistories.containsKey(trimUuid)) {
                mAllHistories.remove(trimUuid);
            }
        }

        if (storageSelect == StorageSelector.LOCAL || storageSelect == StorageSelector.ALL) {
            mLocalStorage.removePayHistory(trimUuid);
        }

        if (storageSelect == StorageSelector.CLOUD || storageSelect == StorageSelector.ALL) {
            //TODO:
        }
        return true;
    }

    public static boolean clear(StorageSelector storageSelect) {
        if (storageSelect == StorageSelector.CACHE || storageSelect == StorageSelector.ALL) {
            mAllHistories.clear();
        }

        if (storageSelect == StorageSelector.LOCAL || storageSelect == StorageSelector.ALL) {
            mLocalStorage.clearPayHistories();
        }

        if (storageSelect == StorageSelector.CLOUD || storageSelect == StorageSelector.ALL) {
            //TODO:
        }
        return true;
    }


    public static boolean update(PayHistory p, StorageSelector storageSelect) {
        if (p == null || !p.validate()) {
            return false;
        }

        if (storageSelect == StorageSelector.CACHE || storageSelect == StorageSelector.ALL) {
            p.Status = SyncStatus.CACHE_UPDATED;
            //do nothing
        }

        if (storageSelect == StorageSelector.LOCAL || storageSelect == StorageSelector.ALL) {
            p.Status = SyncStatus.LOCAL_UPDATED;
            mLocalStorage.updatePayHistory(p);
        }

        if (storageSelect == StorageSelector.CLOUD || storageSelect == StorageSelector.ALL) {
            //TODO:
        }

        return true;
    }
}
