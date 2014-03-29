package com.yyscamper.cashnote.PayType;

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

    private static Hashtable<String, PayPerson> _allPersons = new Hashtable<String, PayPerson>();

    public static void clear()
    {
        _allPersons.clear();
    }

    public static PayPerson add(String name)
    {
        if (_allPersons.containsKey(name))
            return _allPersons.get(name);

        PayPerson p= new PayPerson(name);
        _allPersons.put(name, p);
        return p;
    }

    public static boolean contains(String name)
    {
        return _allPersons.containsKey(name);
    }

    public static void remove(String name)
    {
        if (!_allPersons.containsKey(name))
            return;
        _allPersons.remove(name);
    }

    public static int size()
    {
        return _allPersons.size();
    }

    public static PayPerson get(String name)
    {
        if (_allPersons.containsKey(name))
            return _allPersons.get(name);
        else
            return null;
    }

    public static PayPerson[] addRange(String[] names)
    {
        PayPerson[] arr = new PayPerson[names.length];

        for (int i = 0; i < names.length; i++)
        {
            arr[i] = add(names[i]);
        }
        return arr;
    }

    public static String[] getAllNames()
    {
        String[] arr = new String[_allPersons.keySet().size()];
        _allPersons.keySet().toArray(arr);
        return arr;
    }

    public static String[] getAllSortedNames()
    {
        ArrayList<String> alNames = new ArrayList<String>();
        for (String str :_allPersons.keySet())
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
        PayPerson[] arr = new PayPerson[_allPersons.values().size()];
        _allPersons.values().toArray(arr);
        return arr;
    }

    public static PayPerson[] getAllPersons(int sortType)
    {
        final int theSortType = sortType;
        ArrayList<PayPerson> alPersons = new ArrayList<PayPerson>();
        for (PayPerson p : _allPersons.values())
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
}
