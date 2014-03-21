package com.yyscamper.teamaccount.PayType;

import java.util.*;
/**
 * Created by YuanYu on 14-3-16.
 */
public class PayLocationManager {
    private static Hashtable<String, PayLocation> _allLocations = new Hashtable<String, PayLocation>();

    public static void clear()
    {
        if (_allLocations != null)
            _allLocations.clear();
    }

    public static PayLocation add(String name)
    {
        if (name == null || name.trim().length() <= 0)
            return null;

        if (!_allLocations.containsKey(name)) {
            PayLocation loc = new PayLocation(name);
            _allLocations.put(name, loc);
            return loc;
        }
        else {
            return _allLocations.get(name);
        }
    }

    public static void remove(String name)
    {
        if (_allLocations != null && _allLocations.containsKey(name))
            _allLocations.remove(name);
    }

    public static int size()
    {
        if (_allLocations != null)
            return _allLocations.size();
        else
            return 0;
    }

    public static PayLocation get(String name)
    {
        if (_allLocations.containsKey(name))
            return _allLocations.get(name);
        else
            return null;
    }

    public static void increaseAttend(String name, int increaseVal)
    {
        if (_allLocations.containsKey(name))
        {
            PayLocation loc = _allLocations.get(name);
            loc.AttendCount += increaseVal;
            if (loc.AttendCount < 0)
                loc.AttendCount = 0;
        }
    }

    public static String[] getAllNames()
    {
        String[] arr = new String[_allLocations.keySet().size()];
        _allLocations.keySet().toArray(arr);
        return arr;
    }
}
