package com.yyscamper.teamaccount.Cloud;

import java.util.*;
import com.baidu.frontia.*;
import com.baidu.frontia.api.*;
import com.yyscamper.teamaccount.PayType.PayEntry;
import com.yyscamper.teamaccount.PayType.PayPerson;
import com.yyscamper.teamaccount.PayType.PayPersonManager;

import android.text.format.*;

import org.json.JSONArray;

/**
 * Created by YuanYu on 14-3-19.
 */
public class CloudStorageManager {

    private FrontiaStorage mCloudStorage;

    public CloudStorageManager(FrontiaStorage storage)
    {
        mCloudStorage = storage;
    }

    public void UploadPayEntry(PayEntry entry, FrontiaStorageListener.DataInsertListener listen)
    {
        final FrontiaData data = new FrontiaData();
        data.put("type", "PayEntry");
        data.put("money", String.valueOf(entry.Money));
        data.put("payer", entry.Payer.Name);
        data.put("attend", entry.getAttendNameString());
        data.put("location", entry.Place);
        data.put("description", entry.Description);
        data.put("pay_time", entry.PayTime.toString());
        Time time_push = new Time();
        time_push.setToNow();
        data.put("upload_time", time_push);

        mCloudStorage.insertData(data, listen);
    }

    public void DownloadAllPayEntries(FrontiaStorageListener.DataInfoListener listen)
    {
        FrontiaQuery query = new FrontiaQuery();
        query.equals("type", "PayEntry");
        mCloudStorage.findData(query, listen);
    }

    public PayEntry ParsePayEntry(FrontiaData data)
    {
        PayEntry entry = new PayEntry();

        String str;

        try {

            if (data.containsKey("type")) {
                str = (String) data.get("type");
                if (!str.equalsIgnoreCase("PayEntry"))
                    return null;
            } else {
                return null;
            }

            if (data.containsKey("money")) {
                entry.Money = Double.parseDouble((String) data.get("money"));
            } else {
                return null;
            }

            if (data.containsKey("payer"))
                entry.Payer = PayPersonManager.add((String) data.get("payer"));
            else {
                return null;
            }

            if (data.containsKey("attend"))
            {
                str = (String)data.get("attend");
                String[] arr = str.split(",");
                ArrayList<PayPerson> listPersons = new ArrayList<PayPerson>();
                for (String s : arr)
                {
                    String trims = s.trim();
                    if (trims.length() > 0)
                        listPersons.add(PayPersonManager.add(trims));
                }
                PayPerson[] arrPersons = new PayPerson[listPersons.size()];
                listPersons.toArray(arrPersons);
                entry.AttendPersons = arrPersons;
            }
            else {
                return null;
            }

            if (data.containsKey("location")) {
                entry.Place = (String)data.get("location");
            }
            else {
                entry.Place = "";
            }

            if (data.containsKey("description")) {
                entry.Description = (String)data.get("description");
            }
            else {
                entry.Description = "";
            }

            if (data.containsKey("pay_time")) {
                str = (String)data.get("pay_time");
                Time t = new Time();
                t.parse(str);
                entry.PayTime = t;
            }
            else {
                entry.PayTime = new Time();
                entry.PayTime.set(0, 0, 0, 1, 0, 1900);
            }

            if (!entry.validate())
                return null;
            return entry;
        }
        catch (Exception err)
        {
            return null;
        }
    }
}
