package com.yyscamper.cashnote.PayType;

import android.location.Location;

import com.yyscamper.cashnote.Storage.LocalStorage;

public class AccountBook {
	private static double mTotalMoney = 0.0;
    private static LocalStorage mLocalStorage;

    public static void setLocalStorage(LocalStorage s) {
        mLocalStorage = s;
    }

    public static void debug_clear_db() {
        PayLocationManager.clear(StorageSelector.ALL);
        PayPersonManager.clear(StorageSelector.ALL);
        PayHistoryManager.clear(StorageSelector.ALL);
        LocationGroupManager.clear(StorageSelector.ALL);
    }

    public static void debug_init_db() {
        StorageSelector storageSelector = StorageSelector.ALL;
        PayPersonManager.add("Alfred", storageSelector);
        PayPersonManager.add("Andy", storageSelector);
        PayPersonManager.add("Felix", storageSelector);
        PayPersonManager.add("Leo", storageSelector);
        PayPersonManager.add("Peter", storageSelector);
        PayPersonManager.add("Simon", storageSelector);
        PayPersonManager.add("Tao", storageSelector);
        PayPersonManager.add("Wayne", storageSelector);
        PayPersonManager.add("Wenrey", storageSelector);
        PayPersonManager.add("Phoebe", storageSelector);
        PayPersonManager.add("Lyne", storageSelector);

        PayLocationManager.add("耶里夏丽", storageSelector);
        PayLocationManager.add("稻谷鸡", storageSelector);
        PayLocationManager.add("吉祥馄饨", storageSelector);
        PayLocationManager.add("西贝筱面村", storageSelector);
        PayLocationManager.add("权金城", storageSelector);
        PayLocationManager.add("大成制面", storageSelector);
        PayLocationManager.add("斗香园", storageSelector);
        PayLocationManager.add("台味味", storageSelector);
        PayLocationManager.add("蜀面", storageSelector);
        PayLocationManager.add("兰州拉面", storageSelector);
        PayLocationManager.add("苏州汤包馆", storageSelector);
        PayLocationManager.add("四海游龙", storageSelector);
        PayLocationManager.add("汉堡王", storageSelector);

        addPay(new PayHistory(100, "Felix", new String[] {"Felix", "Andy", "Alfred", "Simon"},
                null, "稻谷鸡", "Here we are"), storageSelector);
        addPay(new PayHistory(60, "Andy", new String[] {"Felix", "Andy", "Leo"},
                null, "兰州拉面", "Not so bad"),  storageSelector);

        LocationGroup group1 = new LocationGroup("多人就餐");
        group1.setChildren(new String[] {"耶里夏丽", "西贝筱面村", "稻谷鸡", "望湘园"});
        LocationGroup group2 = new LocationGroup("近处就餐");
        group2.setChildren(new String[] {"大成制面", "丽豪", "5德火锅", "兰州拉面", "吉祥馄饨"});
        LocationGroup group3 = new LocationGroup("加班可报销");
        group3.setChildren(new String[] {"丽豪", "稻谷鸡", "5德火锅"});

        LocationGroupManager.add(group1, storageSelector);
        LocationGroupManager.add(group2, storageSelector);
        LocationGroupManager.add(group3, storageSelector);
    }

    public static void init()
    {
        mTotalMoney = 0.0;

        PayHistoryManager.setLocalStorage(mLocalStorage);
        PayPersonManager.setLocalStorage(mLocalStorage);
        PayLocationManager.setLocalStorage(mLocalStorage);
        LocationGroupManager.setLocalStorage(mLocalStorage);
        mLocalStorage.reloadAllPersons();
        mLocalStorage.reloadAllLocations();
        mLocalStorage.reloadAllHistories();
        mLocalStorage.realodAllLocationGroups();
    }

    public static boolean addPay(PayHistory entry, StorageSelector selector)
    {
        if (entry == null || !entry.validate())
            return false;

        if (!PayPersonManager.contains(entry.PayerName)) {
            PayPersonManager.add(entry.PayerName, selector);
        }
        for (String str : entry.AttendPersonNames) {
            if (!PayPersonManager.contains(str)) {
                PayPersonManager.add(str, selector);
            }
        }
        if (!PayLocationManager.contains(entry.Location)) {
            PayLocationManager.add(entry.Location, selector);
        }

        PayPerson payer = entry.getPayer();
        PayPerson[] attends = entry.getAttends();
        PayLocation loc = PayLocationManager.get(entry.Location);

        //update history, money, person balance
        mTotalMoney += entry.Money;
        payer.PayCount++;
        payer.Balance += entry.Money;
        double avg = entry.Money / entry.AttendPersonNames.length;
        for (PayPerson p : attends)
        {
            p.AttendCount++;
            p.Balance -= avg;
        }

        //update location
        loc.AttendCount++;
        loc.updateLastAttendTime(entry.PayTime);

        PayHistoryManager.add(entry, selector);
        PayPersonManager.update(payer, selector);
        for (PayPerson p : attends) {
            if (!p.Name.equals(payer.Name)) {
                PayPersonManager.update(p, selector);
            }
        }
        PayLocationManager.update(loc, selector);
        return true;
    }

    public static boolean removePay(String uuid, StorageSelector selector)
    {
        PayHistory entry = PayHistoryManager.get(uuid);
        PayPerson payer = entry.getPayer();
        PayPerson[] attends = entry.getAttends();
        PayLocation loc = PayLocationManager.get(entry.Location);

        if (payer.PayCount > 0)
            payer.PayCount--;
        payer.Balance -= entry.Money;
        double avg = entry.Money / entry.AttendPersonNames.length;
        for (PayPerson p : attends)
        {
            if (p.AttendCount > 0)
                p.AttendCount--;
            p.Balance += avg;
        }

        if (loc != null)
        {
            if (loc.AttendCount > 0)
                loc.AttendCount--;
        }

        mTotalMoney -= entry.Money;
        if (mTotalMoney < 0)
            mTotalMoney = 0.0;

        PayHistoryManager.remove(uuid, selector);
        if (loc != null)
            PayLocationManager.update(loc, selector);
        PayPersonManager.update(payer, selector);
        for (PayPerson p : attends) {
            PayPersonManager.update(p, selector);
        }
        PayLocationManager.update(loc, selector);
        return true;
    }

    public static double getTotalMoney()
    {
        return mTotalMoney;
    }
}
