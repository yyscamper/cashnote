package com.yyscamper.cashnote.PayType;

import android.app.Application;
import android.location.Location;
import android.widget.Toast;

import com.yyscamper.cashnote.Interface.PayHistoryDetailListener;
import com.yyscamper.cashnote.Storage.CloudStorage;
import com.yyscamper.cashnote.Storage.LocalStorage;
import com.yyscamper.cashnote.Storage.SyncManager;
import com.yyscamper.cashnote.Util.Util;

import java.util.ArrayList;

public class AccountBook {
	private static double mTotalMoney = 0.0;
    private static LocalStorage mLocalStorage;
    private static CloudStorage mCloudStorage;
    public static void setLocalStorage(LocalStorage s) {
        mLocalStorage = s;
    }
    public static void setCloudStorage(CloudStorage s) { mCloudStorage = s; }
    public static void debug_clear_db() {

        try {
            PayLocationManager.clear(StorageSelector.ALL);
            PayPersonManager.clear(StorageSelector.ALL);
            PayHistoryManager.clear(StorageSelector.ALL);
            LocationGroupManager.clear(StorageSelector.ALL);
        }
        catch (Throwable err) {
            err.printStackTrace();
        }
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

        addPay(PayHistory.buildAvgHistory(null, 100, "Felix", new String[]{"Felix", "Andy", "Alfred", "Simon"},
                null, "稻谷鸡", "Here we are"), storageSelector);
        addPay(PayHistory.buildAvgHistory(null, 60, "Andy", new String[]{"Felix", "Andy", "Leo"},
                null, "兰州拉面", "Not so bad"),  storageSelector);

        PayAttendInfo[] atts = new PayAttendInfo[4];
        atts[0] = new PayAttendInfo("Felix", 20);
        atts[1] = new PayAttendInfo("Andy", 30);
        atts[2] = new PayAttendInfo("Leo", 40);
        atts[3] = new PayAttendInfo("Simon", 50);
        PayHistory entry = PayHistory.buildNotAvgHistory(null, "Felix", atts, null, "稻谷鸡", "一般般好吃的啦");
        entry.Money = entry.calcMoneySum();
        addPay(entry, storageSelector);

        atts = new PayAttendInfo[5];
        atts[0] = new PayAttendInfo("Felix", 19);
        atts[1] = new PayAttendInfo("Andy", 15);
        atts[2] = new PayAttendInfo("Leo", 15);
        atts[3] = new PayAttendInfo("Simon", 20);
        atts[4] = new PayAttendInfo("Tao", 18);
        entry = PayHistory.buildNotAvgHistory(null, "Simon", atts, null, "汉堡王", "有点点撑的啊");
        entry.Money = entry.calcMoneySum();
        addPay(entry, storageSelector);

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

        PayHistoryManager.setCloudStorage(mCloudStorage);
        PayPersonManager.setCloudStorage(mCloudStorage);
        PayLocationManager.setCloudStorage(mCloudStorage);
        LocationGroupManager.setCloudStorage(mCloudStorage);
        SyncManager.setCloudStorage(mCloudStorage);

        try {
            /*SyncManager.syncPerson();
            SyncManager.syncLocation();
            SyncManager.syncHistory();
            SyncManager.syncLocationGroup();*/
            mLocalStorage.reloadAllPersons();
            mLocalStorage.reloadAllLocations();
            mLocalStorage.reloadAllHistories();
            mLocalStorage.realodAllLocationGroups();
        }
        catch (Throwable err) {

        }
    }

    public static boolean addPay(PayHistory entry, StorageSelector selector)
    {
        if (entry == null || !entry.validate())
            return false;

        if (!PayPersonManager.contains(entry.PayerName)) {
            PayPersonManager.add(entry.PayerName, selector);
        }
        for (PayAttendInfo str : entry.AttendsInfo) {
            if (!PayPersonManager.contains(str.getName())) {
                PayPersonManager.add(str.getName(), selector);
            }
        }
        if (!PayLocationManager.contains(entry.Location)) {
            PayLocationManager.add(entry.Location, selector);
        }

        PayPerson payer = entry.getPayer();
        PayPerson[] attends = entry.getAttends();
        PayLocation loc = PayLocationManager.get(entry.Location);
        if (entry.Type == PayHistory.TYPE_NORMAL_AVG) {
            double avg = entry.Money / attends.length;
            for (int i = 0; i < attends.length; i++) {
                attends[i].AttendCount++;
                attends[i].Balance -= avg;
            }
        }
        else {
            //entry.Money = entry.calcMoneySum();
            for (int i = 0; i < attends.length; i++) {
                attends[i].AttendCount++;
                attends[i].Balance -= entry.AttendsInfo[i].getMoney();
            }
        }
        mTotalMoney += entry.Money;

        //update history, money, person balance
        payer.PayCount++;
        payer.Balance += entry.Money;

        //update location
        loc.AttendCount++;
        loc.updateLastAttendTime(entry.PayTime);

        PayHistoryManager.add(entry, selector);
        PayPersonManager.update(payer.Name, payer, selector);
        for (PayPerson p : attends) {
            if (!p.Name.equals(payer.Name)) {
                PayPersonManager.update(payer.Name, payer, selector);
            }
        }
        PayLocationManager.update(loc.Name, loc, selector);
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

        if (entry.Type == PayHistory.TYPE_NORMAL_AVG) {
            double avg = entry.Money / attends.length;
            for (int i = 0; i < attends.length; i++) {
                if (attends[i].AttendCount > 0)
                    attends[i].AttendCount--;
                attends[i].Balance += avg;
            }
        }
        else {
            for (int i = 0; i < attends.length; i++) {
                if (attends[i].AttendCount > 0)
                    attends[i].AttendCount--;
                attends[i].Balance += entry.AttendsInfo[i].getMoney();
            }
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
            PayLocationManager.update(loc.Name, loc, selector);
        PayPersonManager.update(payer.Name, payer, selector);
        for (PayPerson p : attends) {
            PayPersonManager.update(p.Name, p, selector);
        }
        PayLocationManager.update(loc.Name, loc, selector);
        return true;
    }

    public static double getTotalMoney()
    {
        return mTotalMoney;
    }
}
