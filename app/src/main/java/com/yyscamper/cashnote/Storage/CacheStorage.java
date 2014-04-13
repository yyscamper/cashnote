package com.yyscamper.cashnote.Storage;

import android.location.Location;
import android.provider.ContactsContract;

import com.yyscamper.cashnote.Enum.DataType;
import com.yyscamper.cashnote.PayType.LocationGroup;
import com.yyscamper.cashnote.PayType.PayAttendInfo;
import com.yyscamper.cashnote.PayType.PayBaseManager;
import com.yyscamper.cashnote.PayType.PayHistory;
import com.yyscamper.cashnote.PayType.PayLocation;
import com.yyscamper.cashnote.PayType.PayPerson;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

/**
 * Created by yuanf on 2014-04-13.
 */
public class CacheStorage implements BaseStorage.SyncStorage {
    private PayBaseManager[] mManagers;
    private static CacheStorage mInstance;

    private CacheStorage() {
        mManagers = new PayBaseManager[DataType.SIZE.getValue()];
        for (int i = 0; i < mManagers.length; i++) {
            mManagers[i] = new PayBaseManager();
        }
    }

    public static CacheStorage getInstance() {
        if (mInstance == null)
            mInstance = new CacheStorage();
        return mInstance;
    }

    public boolean contains(DataType type, String key) {
        return mManagers[type.getValue()].contains(key);
    }

    public PayBaseManager getManager(DataType dataType) {
        return mManagers[dataType.getValue()];
    }

    @Override
    public StorageObject get(DataType type, String key) {
        if (!contains(type, key)) {
            return null;
        }
        return mManagers[type.getValue()].get(key);
    }

    @Override
    public ArrayList<StorageObject> getAll(DataType dataType) {
        return null;
    }

    @Override
    public int insert(StorageObject obj) {
        mManagers[obj.getType().getValue()].insert(obj);
        return 0;
    }

    @Override
    public int insert(DataType dataType, ArrayList<StorageObject> objList) {
        mManagers[dataType.getValue()].insert(objList);
        return 0;
    }

    @Override
    public int remove(DataType dataType, String key) {
        mManagers[dataType.getValue()].remove(key);
        return 0;
    }

    @Override
    public int remove(StorageObject obj) {
        return remove(obj.getType(), obj.getKey());
    }

    @Override
    public int clear(DataType dataType) {
        mManagers[dataType.getValue()].clear();
        return 0;
    }

    @Override
    public int clearAll() {
        for (int i = 0; i < mManagers.length; i++) {
            mManagers[i].clear();
        }
        return 0;
    }

    @Override
    public int update(String oldKey, StorageObject obj) {
        remove(obj.getType(), oldKey);
        insert(obj);
        return 0;
    }

    @Override
    public int update(StorageObject obj) {
        return update(obj.getKey(), obj);
    }

    @Override
    public int initStorage() {
        mManagers = new PayBaseManager[DataType.SIZE.getValue()];
        for (int i = 0; i < mManagers.length; i++) {
            mManagers[i] = new PayBaseManager();
        }
        return 0;
    }

    @Override
    public int closeStorage() {
        return 0;
    }

    public PayHistory getHistory(String key) {
        return (PayHistory)get(DataType.HISTORY, key);
    }

    public PayPerson getPerson(String key) {
        return (PayPerson)get(DataType.PERSON, key);
    }

    public PayLocation getLocation(String key) {
        return (PayLocation)get(DataType.LOCATION, key);
    }

    public LocationGroup getLocationGroup(String key) {
        return (LocationGroup)get(DataType.LOCATION_GROUP, key);
    }

    public StorageObject[] getAllInArray(DataType type, Comparator comparator) {
        return mManagers[type.getValue()].getValuesArray(comparator);
    }

    public ArrayList<StorageObject> getAllInList(DataType type, Comparator comparator) {
        return mManagers[type.getValue()].getValuesList(comparator);
    }

    public PayHistory[] getAllHistories(Comparator comparator) {
        StorageObject[] items = getAllInArray(DataType.HISTORY, comparator);
        PayHistory[] results = new PayHistory[items.length];
        System.arraycopy(items, 0, results, 0, items.length);
        return results;
    }

    public PayPerson[] getAllPersons(Comparator comparator) {
        StorageObject[] items = getAllInArray(DataType.PERSON, comparator);
        PayPerson[] results = new PayPerson[items.length];
        System.arraycopy(items, 0, results, 0, items.length);
        return results;
    }

    public PayLocation[] getAllLocations(Comparator comparator) {
        StorageObject[] items = getAllInArray(DataType.LOCATION, comparator);
        PayLocation[] results = new PayLocation[items.length];
        System.arraycopy(items, 0, results, 0, items.length);
        return results;
    }

    public LocationGroup[] getAllLocationGroups(Comparator comparator) {
        StorageObject[] items = getAllInArray(DataType.LOCATION_GROUP, comparator);
        LocationGroup[] results = new LocationGroup[items.length];
        System.arraycopy(items, 0, results, 0, items.length);
        return results;
    }

    public ArrayList<PayHistory> getAllHistoriesInList(Comparator comparator) {
        DataType dataType = DataType.HISTORY;
        ArrayList<PayHistory> results = new ArrayList<PayHistory>(mManagers[dataType.getValue()].size());
        for (Map.Entry<String, StorageObject> entry : mManagers[dataType.getValue()].getRawHashtable().entrySet()) {
            results.add((PayHistory)entry.getValue());
        }
        if (comparator != null) {
            Collections.sort(results, comparator);
        }
        return results;
    }

    public ArrayList<PayPerson> getAllPersonsInList(Comparator comparator) {
        DataType dataType = DataType.PERSON;
        ArrayList<PayPerson> results = new ArrayList<PayPerson>(mManagers[dataType.getValue()].size());
        for (Map.Entry<String, StorageObject> entry : mManagers[dataType.getValue()].getRawHashtable().entrySet()) {
            results.add((PayPerson)entry.getValue());
        }
        if (comparator != null) {
            Collections.sort(results, comparator);
        }
        return results;
    }

    public ArrayList<PayLocation> getAllLocationsInList(Comparator comparator) {
        DataType dataType = DataType.LOCATION;
        ArrayList<PayLocation> results = new ArrayList<PayLocation>(mManagers[dataType.getValue()].size());
        for (Map.Entry<String, StorageObject> entry : mManagers[dataType.getValue()].getRawHashtable().entrySet()) {
            results.add((PayLocation)entry.getValue());
        }
        if (comparator != null) {
            Collections.sort(results, comparator);
        }
        return results;
    }

    public ArrayList<LocationGroup> getAllLocationGroupsInList(Comparator comparator) {
        DataType dataType = DataType.HISTORY;
        ArrayList<LocationGroup> results = new ArrayList<LocationGroup>(mManagers[dataType.getValue()].size());
        for (Map.Entry<String, StorageObject> entry : mManagers[dataType.getValue()].getRawHashtable().entrySet()) {
            results.add((LocationGroup)entry.getValue());
        }
        if (comparator != null) {
            Collections.sort(results, comparator);
        }
        return results;
    }

    public String[] getAllPersonNames(Comparator comparator) {
        return mManagers[DataType.PERSON.getValue()].getKeysArray(comparator);
    }

    public String[] getAllLocationNames(Comparator comparator) {
        return mManagers[DataType.LOCATION.getValue()].getKeysArray(comparator);
    }

    public String[] getAllLocationGroupNames(Comparator comparator) {
        return mManagers[DataType.LOCATION_GROUP.getValue()].getKeysArray(comparator);
    }
}
