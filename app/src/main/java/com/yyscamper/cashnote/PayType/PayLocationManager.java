package com.yyscamper.cashnote.PayType;

import com.yyscamper.cashnote.Storage.CloudStorage;
import com.yyscamper.cashnote.Storage.LocalStorage;

import java.util.*;
/**
 * Created by YuanYu on 14-3-16.
 */
public class PayLocationManager {
    private static Hashtable<String, PayLocation> mAllLocations = new Hashtable<String, PayLocation>();

    private static LocalStorage mLocalStorage;
    private static CloudStorage mCloudStorage;
    private static PayLocation[] mTopLocations = new PayLocation[3];

    public static void setLocalStorage(LocalStorage s) {
        mLocalStorage = s;
    }
    public static void setCloudStorage(CloudStorage s) { mCloudStorage = s; }

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
            if (mCloudStorage != null) {
                mCloudStorage.insertPayLocation(p);
            }
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
            if (mCloudStorage != null) {
                mCloudStorage.removePayLocation(name);
            }
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
            if (mCloudStorage != null) {
                mCloudStorage.clearAllPayLocations();
            }
        }
        return true;
    }


    public static boolean update(String oldName, PayLocation p, StorageSelector storageSelect) {
        if (p == null || !p.validate()) {
            return false;
        }

        if (storageSelect == StorageSelector.CACHE || storageSelect == StorageSelector.ALL) {
            p.LastModifyTime.setToNow();
            if (oldName.equalsIgnoreCase(p.Name)) {
                p.Status = SyncStatus.CACHE_UPDATED;
                PayLocation oldItem = mAllLocations.get(oldName);
                oldItem.copyFrom(p);
            }
            else {
                mAllLocations.remove(oldName);
                p.Status = SyncStatus.CACHE_NEW;
                mAllLocations.put(p.Name, p);
            }
        }

        if (storageSelect == StorageSelector.LOCAL || storageSelect == StorageSelector.ALL) {
            p.Status = SyncStatus.LOCAL_UPDATED;
            mLocalStorage.updatePayLocation(p);
        }

        if (storageSelect == StorageSelector.CLOUD || storageSelect == StorageSelector.ALL) {
            if (mCloudStorage != null) {
                mCloudStorage.updatePayLocation(oldName, p);
            }
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

    public static PayLocation[] getTopLocatons() {
        ArrayList<PayLocation> arrList = new ArrayList<PayLocation>(mAllLocations.size());
        for (PayLocation p : mAllLocations.values()) {
            arrList.add(p);
        }
        Collections.sort(arrList, new Comparator() {
            public int compare(Object o1, Object o2) {
                PayLocation p1 = (PayLocation)o1;
                PayLocation p2 = (PayLocation)o2;
                if (p1.AttendCount > p2.AttendCount) {
                    return 1;
                }
                else if (p1.AttendCount < p2.AttendCount) {
                    return -1;
                }
                else {
                    return 0;
                }
            }
        });
        PayLocation[] topResults = new PayLocation[3];
        int j = 0;
        for (PayLocation p : arrList) {
            topResults[j++] = p;
            if (j >= 3) {
                break;
            }
        }
        return topResults;
    }

    public static boolean contains(String name) {
        return mAllLocations.containsKey(name);
    }
}
