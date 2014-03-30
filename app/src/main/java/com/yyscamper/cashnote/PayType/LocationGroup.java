package com.yyscamper.cashnote.PayType;

import java.util.ArrayList;

/**
 * Created by yuanf on 2014-03-30.
 */
public class LocationGroup {
    public String Name;
    public ArrayList<String> mChildren;

    public LocationGroup() {
        mChildren = new ArrayList<String>();
    }

    public LocationGroup(String name) {
        Name = name;
        mChildren = new ArrayList<String>();
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

    public boolean validate() {
        return true;
    }
}
