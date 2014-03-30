package com.yyscamper.cashnote.PayType;

import com.yyscamper.cashnote.Storage.LocalStorage;
import com.yyscamper.cashnote.Util.LeftRightValue;

import java.util.*;
/**
 * Created by YuanYu on 14-3-16.
 */
public class PayLocationManager {
    private static Hashtable<String, PayLocation> mAllLocations = new Hashtable<String, PayLocation>();

    private static LocalStorage mLocalStorage;
    private static PayLocation[] mTopLocations = new PayLocation[3];

    public static void setLocalStorage(LocalStorage s) {
        mLocalStorage = s;
    }

    public static boolean add(PayLocation p, StorageSelector storageSelect) {
        if (p == null || !p.validate() || mAllLocations.containsKey(p.Name)) {
            return false;
        }

        if (storageSelect == StorageSelector.CACHE || storageSelect == StorageSelector.ALL) {
            mAllLocations.put(p.Name, p);
            p.Status = SyncStatus.CACHE_NEW;
        }

        if (storageSelect == StorageSelector.LOCAL || storageSelect == StorageSelector.ALL) {
            if (mLocalStorage != null) {
                p.Status = SyncStatus.LOCAL_NEW;
                mLocalStorage.insertPayLocation(p);
            }
        }

        if (storageSelect == StorageSelector.CLOUD || storageSelect == StorageSelector.ALL) {
            //TODO:
        }
        return true;
    }

    public static boolean add(String name, StorageSelector selector) {
        if (name == null)
            return false;
        String trimName = name.trim();
        if (trimName.length() == 0 || mAllLocations.containsKey(trimName)) {
            return false;
        }
        PayLocation p = new PayLocation(trimName);
        return add(p, selector);
    }

    public static boolean remove(String name, StorageSelector storageSelect) {
        if (name == null) {
            return false;
        }
        String trimName = name.trim();

        if (storageSelect == StorageSelector.CACHE || storageSelect == StorageSelector.ALL) {
            if (mAllLocations.containsKey(trimName)) {
                mAllLocations.remove(trimName);
            }
        }

        if (storageSelect == StorageSelector.LOCAL || storageSelect == StorageSelector.ALL) {
            mLocalStorage.removePayLocation(trimName);
        }

        if (storageSelect == StorageSelector.CLOUD || storageSelect == StorageSelector.ALL) {
            //TODO:
        }
        return true;
    }

    public static boolean clear(StorageSelector storageSelect) {
        if (storageSelect == StorageSelector.CACHE || storageSelect == StorageSelector.ALL) {
            mAllLocations.clear();
        }

        if (storageSelect == StorageSelector.LOCAL || storageSelect == StorageSelector.ALL) {
            mLocalStorage.clearPayLocations();
        }

        if (storageSelect == StorageSelector.CLOUD || storageSelect == StorageSelector.ALL) {
            //TODO:
        }
        return true;
    }


    public static boolean update(PayLocation p, StorageSelector storageSelect) {
        if (p == null || !p.validate()) {
            return false;
        }

        if (storageSelect == StorageSelector.CACHE || storageSelect == StorageSelector.ALL) {
            p.Status = SyncStatus.CACHE_UPDATED;
            //do nothing
        }

        if (storageSelect == StorageSelector.LOCAL || storageSelect == StorageSelector.ALL) {
            p.Status = SyncStatus.LOCAL_UPDATED;
            mLocalStorage.updatePayLocation(p);
        }

        if (storageSelect == StorageSelector.CLOUD || storageSelect == StorageSelector.ALL) {
            //TODO:
        }

        return true;
    }

    public static int size()
    {
        if (mAllLocations != null)
            return mAllLocations.size();
        else
            return 0;
    }

    public static PayLocation get(String name)
    {
        if (mAllLocations.containsKey(name))
            return mAllLocations.get(name);
        else
            return null;
    }

    public static void increaseAttendCount(String name, int increaseVal)
    {
        if (mAllLocations.containsKey(name))
        {
            PayLocation loc = mAllLocations.get(name);
            loc.AttendCount += increaseVal;
            if (loc.AttendCount < 0)
                loc.AttendCount = 0;
        }
    }

    public static String[] getAllNames()
    {
        String[] arr = new String[mAllLocations.keySet().size()];
        mAllLocations.keySet().toArray(arr);
        return arr;
    }

    public static PayLocation[] getAll() {
        PayLocation[] arr = new PayLocation[mAllLocations.values().size()];
        mAllLocations.values().toArray(arr);
        return arr;
    }

    public static void updateTopResult(PayLocation p) {
        for (int i = 0; i < mTopLocations.length; i++) {
            if (mTopLocations[i] != null) {
                if (p.AttendCount > mTopLocations[i].AttendCount) {
                    for (int j = mTopLocations.length - 1; j > i; j--) {
                        mTopLocations[j] = mTopLocations[j - 1];
                    }
                    mTopLocations[i] = p;
                    break;
                }
            } else {
                mTopLocations[i] = p;
                break;
            }
        }
    }

    public static PayLocation[] getTopLocatons() {
        return mTopLocations;
    }

    public static boolean contains(String name) {
        return mAllLocations.containsKey(name);
    }
}
