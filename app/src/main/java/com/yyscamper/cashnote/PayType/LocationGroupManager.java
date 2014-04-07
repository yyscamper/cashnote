package com.yyscamper.cashnote.PayType;

import android.location.Location;

import com.yyscamper.cashnote.Storage.CloudStorage;
import com.yyscamper.cashnote.Storage.LocalStorage;

import java.util.Hashtable;

/**
 * Created by yuanf on 2014-03-30.
 */
public class LocationGroupManager {
    static Hashtable<String, LocationGroup> mAllLocationGroups = new Hashtable<String, LocationGroup>();
    static LocalStorage mLocalStorage;
    static CloudStorage mCloudStorage;
    public static int size() { return mAllLocationGroups.size();}
    public static void setLocalStorage(LocalStorage s) { mLocalStorage = s; }
    public static void setCloudStorage(CloudStorage s) { mCloudStorage = s; }
    public static boolean contains(String name) {
        return mAllLocationGroups.containsKey(name);
    }
    public static LocationGroup get(String groupName) {
        if (mAllLocationGroups.containsKey(groupName)) {
            return mAllLocationGroups.get(groupName);
        }
        return null;
    }

    public static String[] getGroupNames() {
        String[] arr = new String[mAllLocationGroups.size()];
        mAllLocationGroups.keySet().toArray(arr);
        return arr;
    }

    public static boolean add(LocationGroup p, StorageSelector storageSelect) {
        if (p == null || !p.validate() || mAllLocationGroups.containsKey(p.Name)) {
            return false;
        }

        if (storageSelect == StorageSelector.CACHE || storageSelect == StorageSelector.ALL) {
            mAllLocationGroups.put(p.Name, p);
        }

        if (storageSelect == StorageSelector.LOCAL || storageSelect == StorageSelector.ALL) {
            if (mLocalStorage != null) {
                mLocalStorage.insertLocationGroup(p);
            }
        }

        if (storageSelect == StorageSelector.CLOUD || storageSelect == StorageSelector.ALL) {
            if (mCloudStorage != null) {
                mCloudStorage.insertLocationGroup(p);
            }
        }
        return true;
    }

    public static boolean remove(String name, StorageSelector storageSelect) {
        if (name == null) {
            return false;
        }
        if (storageSelect == StorageSelector.CACHE || storageSelect == StorageSelector.ALL) {
            if (mAllLocationGroups.containsKey(name)) {
                mAllLocationGroups.remove(name);
            }
        }

        if (storageSelect == StorageSelector.LOCAL || storageSelect == StorageSelector.ALL) {
            mLocalStorage.removeLocationGroup(name);
        }

        if (storageSelect == StorageSelector.CLOUD || storageSelect == StorageSelector.ALL) {
            if (mCloudStorage != null) {
                mCloudStorage.removeLocationGroup(name);
            }
        }
        return true;
    }

    public static boolean clear(StorageSelector storageSelect) {
        if (storageSelect == StorageSelector.CACHE || storageSelect == StorageSelector.ALL) {
            mAllLocationGroups.clear();
        }

        if (storageSelect == StorageSelector.LOCAL || storageSelect == StorageSelector.ALL) {
            mLocalStorage.clearLocationGroups();
        }

        if (storageSelect == StorageSelector.CLOUD || storageSelect == StorageSelector.ALL) {
            if (mCloudStorage != null) {
                mCloudStorage.clearAllLocationGroups();
            }
        }
        return true;
    }


    public static boolean update(String oldName, LocationGroup p, StorageSelector storageSelect) {
        if (p == null || !p.validate()) {
            return false;
        }

        if (storageSelect == StorageSelector.CACHE || storageSelect == StorageSelector.ALL) {
            //do nothing
        }

        if (storageSelect == StorageSelector.LOCAL || storageSelect == StorageSelector.ALL) {
            mLocalStorage.updateLocationGroup(p);
        }

        if (storageSelect == StorageSelector.CLOUD || storageSelect == StorageSelector.ALL) {
            if (mCloudStorage != null) {
                mCloudStorage.updateLocationGroup(oldName, p);
            }
        }

        return true;
    }
}
