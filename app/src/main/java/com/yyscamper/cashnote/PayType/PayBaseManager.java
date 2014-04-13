package com.yyscamper.cashnote.PayType;

import com.yyscamper.cashnote.Storage.StorageObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by yuanf on 2014-04-13.
 */
public class PayBaseManager {
    Hashtable<String, StorageObject> RawHashtable;

    public PayBaseManager() {
        RawHashtable = new Hashtable<String, StorageObject>();
    }

    public Hashtable<String, StorageObject> getRawHashtable() {
        return RawHashtable;
    }

    public void setRawHashtable(Hashtable<String, StorageObject> rawHashtable) {
        RawHashtable = rawHashtable;
    }

    public int size() {
        return RawHashtable.size();
    }

    public boolean contains(String key) {
        return RawHashtable.containsKey(key);
    }

    public void clear() {
        RawHashtable.clear();
    }

    public void remove(String key) {
        RawHashtable.remove(key);
    }

    public void insert(StorageObject obj) {
        RawHashtable.put(obj.getKey(), obj);
    }

    public void insert(ArrayList<StorageObject> arrList) {
        for (StorageObject obj : arrList) {
            insert(obj);
        }
    }

    public void update(String oldKey, StorageObject obj) {
        remove(oldKey);
        insert(obj);
    }

    public void update(StorageObject obj) {
        remove(obj.getKey());
        insert(obj);
    }

    public StorageObject get(String key) {
        if (!RawHashtable.containsKey(key)) {
            return null;
        }
        return RawHashtable.get(key);
    }

    public ArrayList<String> getKeysList() {
        ArrayList<String> arrList = new ArrayList<String>(RawHashtable.size());
        for (String key : RawHashtable.keySet()) {
            arrList.add(key);
        }
        return arrList;
    }

    public ArrayList<String> getKeysList(final Comparator comparator) {
        ArrayList<String> arrList = getKeysList();
        if (comparator == null) {
            return arrList;
        }
        Collections.sort(arrList, comparator);
        return arrList;
    }

    public String[] getKeysArray(final Comparator comparator) {
        ArrayList<String> arrList = getKeysList(comparator);
        String[] arr = new String[arrList.size()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = arrList.get(i);
        }
        return arr;
    }

    public String[] getKeysArray() {
        return getKeysArray(null);
    }

    public ArrayList<StorageObject> getValuesList() {
        ArrayList<StorageObject> arrlist = new ArrayList<StorageObject>(RawHashtable.size());
        for (Map.Entry<String, StorageObject> entry : RawHashtable.entrySet()) {
            arrlist.add(entry.getValue());
        }
        return arrlist;
    }

    public StorageObject[] getValuesArray() {
        StorageObject[] arr = new StorageObject[RawHashtable.size()];
        int i = 0;
        for (Map.Entry<String, StorageObject> entry : RawHashtable.entrySet()) {
            arr[i++] = entry.getValue();
        }
        return arr;
    }

    public ArrayList<StorageObject> getValuesList(Comparator comparator) {
        ArrayList<StorageObject> arrlist = getValuesList();
        if (comparator != null) {
            Collections.sort(arrlist, comparator);
        }
        return arrlist;
    }

    public StorageObject[] getValuesArray(Comparator comparator) {
        ArrayList<StorageObject> arrList = getValuesList(comparator);
        StorageObject[] arr = new StorageObject[arrList.size()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = arrList.get(i);
        }
        return arr;
    }
}
