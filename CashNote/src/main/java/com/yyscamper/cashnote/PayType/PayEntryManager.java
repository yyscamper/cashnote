package com.yyscamper.cashnote.PayType;

import java.util.*;

/**
 * Created by YuanYu on 14-3-16.
 */
public class PayEntryManager
{
    private static ArrayList<PayEntry> _allEntries = new ArrayList<PayEntry>();

    public static void clear()
    {
        _allEntries.clear();
    }

    public static int size()
    {
        return _allEntries.size();
    }

    public static void add(PayEntry entry)
    {
        _allEntries.add(entry);
    }

    public static void remove(PayEntry entry)
    {
        _allEntries.remove(entry);
    }

    public static void remove(int index)
    {
        _allEntries.remove(index);
    }

    public static PayEntry get(int index)
    {
        if (index >= 0 && index < _allEntries.size()) {
            PayEntry entry = _allEntries.get(index);
            return entry;
        }
        return null;
    }

    public static ArrayList<PayEntry> getAll()
    {
        return _allEntries;
    }
}
