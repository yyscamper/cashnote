package com.yyscamper.teamaccount.PayType;

import java.util.*;

public class AccountBook {
	private static double mTotalMoney = 0.0;

    public static void init()
    {
        mTotalMoney = 0.0;

        PayPersonManager.add("Alfred");
        PayPersonManager.add("Andy");
        PayPersonManager.add("Felix");
        PayPersonManager.add("Leo");
        PayPersonManager.add("Peter");
        PayPersonManager.add("Simon");
        PayPersonManager.add("Tao");
        PayPersonManager.add("Wayne");
        PayPersonManager.add("Wenrey");
        PayPersonManager.add("Phoebe");
        PayPersonManager.add("Lyne");


        PayLocationManager.add("耶里夏丽");
        PayLocationManager.add("稻谷鸡");
        PayLocationManager.add("吉祥馄饨");
        PayLocationManager.add("西贝筱面村");
        PayLocationManager.add("权金城");
        PayLocationManager.add("大成制面");
        PayLocationManager.add("斗香园");
        PayLocationManager.add("台味味");
        PayLocationManager.add("蜀面");
        PayLocationManager.add("兰州拉面");
        PayLocationManager.add("苏州汤包馆");
        PayLocationManager.add("四海游龙");
        PayLocationManager.add("汉堡王");


        addPay(new PayEntry(100, "Felix", new String[] {"Felix", "Andy", "Alfred", "Simon"},
                null, "稻谷鸡", "Here we are"));
        addPay(new PayEntry(60, "Andy", new String[] {"Felix", "Andy", "Leo"},
                null, "兰州拉面", "Not so bad"));
    }

    public static boolean addPay(PayEntry entry)
    {
        if (entry == null || !entry.validate())
            return false;
        PayEntryManager.add(entry);
        entry.Payer.PayCount++;
        entry.Payer.Balance += entry.Money;
        double avg = entry.Money / entry.AttendPersons.length;
        for (PayPerson p : entry.AttendPersons)
        {
            p.AttendCount++;
            p.Balance -= avg;
        }
        PayLocation loc = PayLocationManager.add(entry.Place);
        if (loc != null)
        {
            loc.AttendCount++;
        }

        mTotalMoney += entry.Money;
        return true;
    }

    public static boolean removePay(int index)
    {
        PayEntry entry = PayEntryManager.get(index);

        if (entry.Payer.PayCount > 0)
            entry.Payer.PayCount--;
        entry.Payer.Balance -= entry.Money;
        double avg = entry.Money / entry.AttendPersons.length;
        for (PayPerson p : entry.AttendPersons)
        {
            if (p.AttendCount > 0)
                p.AttendCount--;
            p.Balance += avg;
        }

        PayLocation loc = PayLocationManager.get(entry.Place);
        if (loc != null)
        {
            if (loc.AttendCount > 0)
                loc.AttendCount--;
        }

        mTotalMoney -= entry.Money;
        if (mTotalMoney < 0)
            mTotalMoney = 0.0;
        PayEntryManager.remove(index);
        return true;
    }

    public static double getTotalMoney()
    {
        return mTotalMoney;
    }
}
