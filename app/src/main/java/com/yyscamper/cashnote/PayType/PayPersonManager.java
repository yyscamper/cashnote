package com.yyscamper.cashnote.PayType;

import com.yyscamper.cashnote.Storage.LocalStorage;
import com.yyscamper.cashnote.Util.ValuePair;

import java.util.*;
/**
 * Created by YuanYu on 14-3-16.
 */
public class PayPersonManager {
    public static int SORT_NAME_ASCENDING = 0;
    public static int SORT_NAME_DESCENDING = 1;
    public static int SORT_MONEY_ASCENDING = 2;
    public static int SORT_MONEY_DESCENDING = 3;
    public static int SORT_PAY_COUNT_ASCENDING = 4;
    public static int SORT_PAY_COUNT_DESCENDING = 5;
    public static int SORT_ATTEND_COUNT_ASCENDING = 6;
    public static int SORT_ATTEND_COUNT_DESCENDING = 7;

    private static ValuePair[] mTopSurplusPersons = new ValuePair[3];
    private static ValuePair[] mTopDebetPersons = new ValuePair[3];
    private static ValuePair mMostDebetPerson = null;

    private static Hashtable<String, PayPerson> mAllPersons = new Hashtable<String, PayPerson>();

    private static LocalStorage mLocalStorage;

    public static void setLocalStorage(LocalStorage s) {
        mLocalStorage = s;
    }

    public static boolean contains(String name) {
        return mAllPersons.containsKey(name);
    }

    public static int size() {
        return mAllPersons.size();
    }

    public static boolean add(PayPerson p, StorageSelector storageSelect) {
        if (p == null || !p.validate() || mAllPersons.containsKey(p.Name)) {
            return false;
        }

        if (storageSelect == StorageSelector.CACHE || storageSelect == StorageSelector.ALL) {
            mAllPersons.put(p.Name, p);
            p.Status = SyncStatus.CACHE_NEW;
            updateTopResult(p);
        }

        if (storageSelect == StorageSelector.LOCAL || storageSelect == StorageSelector.ALL) {
            if (mLocalStorage != null) {
                p.Status = SyncStatus.LOCAL_NEW;
                mLocalStorage.insertPayPerson(p);
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
        if (trimName.length() == 0 || mAllPersons.containsKey(trimName)) {
            return false;
        }
        PayPerson p = new PayPerson(trimName);
        return add(p, selector);
    }

    public static boolean remove(String name, StorageSelector storageSelect) {
        if (name == null) {
            return false;
        }
        String trimName = name.trim();

        if (storageSelect == StorageSelector.CACHE || storageSelect == StorageSelector.ALL) {
            if (mAllPersons.containsKey(trimName)) {
                mAllPersons.remove(trimName);
            }
        }

        if (storageSelect == StorageSelector.LOCAL || storageSelect == StorageSelector.ALL) {
            mLocalStorage.removePayPerson(trimName);
        }

        if (storageSelect == StorageSelector.CLOUD || storageSelect == StorageSelector.ALL) {
            //TODO:
        }
        return true;
    }

    public static boolean clear(StorageSelector storageSelect) {
        if (storageSelect == StorageSelector.CACHE || storageSelect == StorageSelector.ALL) {
            mAllPersons.clear();
        }

        if (storageSelect == StorageSelector.LOCAL || storageSelect == StorageSelector.ALL) {
            mLocalStorage.clearPayPersons();
        }

        if (storageSelect == StorageSelector.CLOUD || storageSelect == StorageSelector.ALL) {
            //TODO:
        }
        return true;
    }


    public static boolean update(PayPerson p, StorageSelector storageSelect) {
        if (p == null || !p.validate()) {
            return false;
        }

        if (storageSelect == StorageSelector.CACHE || storageSelect == StorageSelector.ALL) {
            p.Status = SyncStatus.CACHE_UPDATED;
            //do nothing
        }

        if (storageSelect == StorageSelector.LOCAL || storageSelect == StorageSelector.ALL) {
            p.Status = SyncStatus.LOCAL_UPDATED;
            mLocalStorage.updatePayPerson(p);
        }

        if (storageSelect == StorageSelector.CLOUD || storageSelect == StorageSelector.ALL) {
            //TODO:
        }

        return true;
    }

    public static int addRange(String[] names, StorageSelector selector)
    {
        int cnt = 0;
        for (int i = 0; i < names.length; i++)
        {
            if (add(names[i], selector))
                cnt++;
        }
        return cnt;
    }

    public static PayPerson get(String name)
    {
        if (mAllPersons.containsKey(name))
            return mAllPersons.get(name);
        else
            return null;
    }

    public static String[] getAllNames()
    {
        String[] arr = new String[mAllPersons.keySet().size()];
        mAllPersons.keySet().toArray(arr);
        return arr;
    }

    public static String[] getAllSortedNames()
    {
        ArrayList<String> alNames = new ArrayList<String>();
        for (String str : mAllPersons.keySet())
            alNames.add(str);
        Collections.sort(alNames, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((String)o1).compareToIgnoreCase((String)o2);
            }
        });

        String[] arr = new String[alNames.size()];
        alNames.toArray(arr);
        return arr;
    }

    public static PayPerson[] getAllPersons()
    {
        PayPerson[] arr = new PayPerson[mAllPersons.values().size()];
        mAllPersons.values().toArray(arr);
        return arr;
    }

    public static PayPerson[] getAllPersons(int sortType)
    {
        final int theSortType = sortType;
        ArrayList<PayPerson> alPersons = new ArrayList<PayPerson>();
        for (PayPerson p : mAllPersons.values())
        alPersons.add(p);
        Collections.sort(alPersons, new Comparator() {
            public int compare(Object o1, Object o2)
            {
                PayPerson p1 = (PayPerson)o1;
                PayPerson p2 = (PayPerson)o2;
                if (theSortType == SORT_MONEY_ASCENDING)
                {
                    int m1 = (int)(p1.Balance * 1000);
                    int m2 = (int)(p2.Balance * 1000);
                    if (m1 > m2)
                        return 1;
                    else if (m1 < m2)
                        return -1;
                    else
                        return 0;
                }
                else if (theSortType == SORT_MONEY_DESCENDING)
                {
                    int m1 = (int)(p1.Balance * 1000);
                    int m2 = (int)(p2.Balance * 1000);
                    if (m1 > m2)
                        return -1;
                    else if (m1 < m2)
                        return 1;
                    else
                        return 0;
                }
                else if (theSortType == SORT_NAME_DESCENDING)
                {
                    return (-p1.Name.compareToIgnoreCase(p2.Name));
                }
                else if (theSortType == SORT_NAME_ASCENDING)
                {
                    return p1.Name.compareToIgnoreCase(p2.Name);
                }
                else if (theSortType == SORT_PAY_COUNT_ASCENDING)
                {
                    if (p1.PayCount > p2.PayCount)
                        return 1;
                    else if (p1.PayCount < p2.PayCount)
                        return -1;
                    else
                        return 0;
                }
                else if (theSortType == SORT_PAY_COUNT_DESCENDING)
                {
                    if (p1.PayCount > p2.PayCount)
                        return -1;
                    else if (p1.PayCount < p2.PayCount)
                        return 1;
                    else
                        return 0;
                }
                else if (theSortType == SORT_ATTEND_COUNT_ASCENDING)
                {
                    if (p1.AttendCount > p2.AttendCount)
                        return 1;
                    else if (p1.AttendCount < p2.AttendCount)
                        return -1;
                    else
                        return 0;
                }
                else
                {
                    if (p1.AttendCount > p2.AttendCount)
                        return -1;
                    else if (p1.AttendCount < p2.AttendCount)
                        return 1;
                    else
                        return 0;
                }
            }
        });

        PayPerson[] arrPersons = new PayPerson[alPersons.size()];
        alPersons.toArray(arrPersons);
        return arrPersons;
    }

    public static void updateTopResult(PayPerson p)
    {
        for (int i = 0; i < mTopSurplusPersons.length; i++) {
            if (mTopSurplusPersons[i] != null) {
                Double v = (Double)mTopSurplusPersons[i].Value1;
                if (p.Balance > v.doubleValue()) {
                    for (int j = mTopSurplusPersons.length-1; j > i; j--) {
                        mTopSurplusPersons[j] = mTopSurplusPersons[j-1];
                    }
                    mTopSurplusPersons[i] = new ValuePair(p.Name, Double.valueOf(p.Balance));
                    break;
                }
            }
            else
            {
                mTopSurplusPersons[i] = new ValuePair(p.Name, Double.valueOf(p.Balance));
                break;
            }
        }

        for (int i = 0; i < mTopDebetPersons.length; i++) {
            if (mTopDebetPersons[i] != null) {
                Double v = (Double)mTopDebetPersons[i].Value1;
                if (p.Balance < v.doubleValue()) {
                    for (int j = mTopDebetPersons.length-1; j > i; j--) {
                        mTopDebetPersons[j] = mTopDebetPersons[j-1];
                    }
                    mTopDebetPersons[i] = new ValuePair(p.Name, Double.valueOf(p.Balance));
                    break;
                }
            }
            else
            {
                mTopDebetPersons[i] = new ValuePair(p.Name, Double.valueOf(p.Balance));
                break;
            }
        }

        mMostDebetPerson = mTopDebetPersons[0];
    }

    public static ValuePair[] getTopSurplusPersons() {
        return mTopSurplusPersons;
    }

    public static ValuePair[] getTopDebetPersons() {
        return mTopDebetPersons;
    }

    public static ValuePair getMostDebetPerson() {
        return mMostDebetPerson;
    }
}
