package com.yyscamper.cashnote.PayType;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.format.Time;

import com.baidu.frontia.FrontiaData;
import com.yyscamper.cashnote.Enum.DataType;
import com.yyscamper.cashnote.Storage.StorageConst;
import com.yyscamper.cashnote.Storage.StorageObject;
import com.yyscamper.cashnote.StorageManager;
import com.yyscamper.cashnote.Util.Util;

import java.util.ArrayList;

/**
 * Created by yuanf on 2014-03-30.
 */
public class LocationGroup extends StorageObject {
    private ArrayList<String> mChildren;

    public LocationGroup() {
        super(DataType.LOCATION_GROUP);
        mChildren = new ArrayList<String>();
    }

    public LocationGroup(String name) {
        super(DataType.LOCATION_GROUP, name);
        mChildren = new ArrayList<String>();
    }

    public String getName() {
        return getKey();
    }

    public void setName(String name) {
        setKey(name);
    }

    public int getChildrenCount() {
        if (mChildren == null)
            return 0;
        return mChildren.size();
    }
    public void add(String location) {
        mChildren.add(location);
    }

    public void removeChild(String location) {
        mChildren.remove(location);
    }

    public void setChildren(ArrayList<String> items) {
        mChildren = items;
    }

    public void setChildren(String[] items) {
        mChildren.clear();
        for (String str : items) {
            mChildren.add(str);
        }
    }

    public ArrayList<String> getChildrenList() {
        return mChildren;
    }

    public String[] getChildrenArray() {
        String[] arr = new String[mChildren.size()];
        mChildren.toArray(arr);
        return arr;
    }

    public void copyFrom(LocationGroup p) {
        super.copyFrom(p);
        mChildren.clear();
        for (String str : p.mChildren) {
            mChildren.add(str);
        }
    }

    @Override
    public boolean validate() {
        return super.validate();
    }

    @Override
    public ContentValues convertToSqliteObject() {
        ContentValues values = super.convertToSqliteObject();
        values.put(StorageConst.KEY_LOCATION_GROUP_CHILDREN, Util.stringArrayJoin(getChildrenArray(), ","));
        return values;
    }

    @Override
    public boolean parseSqliteObject(Cursor c) {
        if (!super.parseSqliteObject(c))
            return false;
        String str = c.getString(c.getColumnIndex(StorageConst.KEY_LOCATION_GROUP_CHILDREN));
        String[] strArr = str.split(",");
        setChildren(strArr);
        return true;
    }

    @Override
    public FrontiaData convertToFrontiaData() {
        FrontiaData values = super.convertToFrontiaData();
        values.put(StorageConst.KEY_LOCATION_GROUP_CHILDREN, Util.stringArrayJoin(getChildrenArray(), ","));
        return values;
    }

    @Override
    public boolean parseFrontiaData(FrontiaData data) {
        if (!super.parseFrontiaData(data))
            return false;
        String str = data.get(StorageConst.KEY_LOCATION_GROUP_CHILDREN).toString();
        String[] strArr = str.split(",");
        setChildren(strArr);
        return true;
    }
}
