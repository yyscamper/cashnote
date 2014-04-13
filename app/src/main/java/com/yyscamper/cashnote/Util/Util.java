package com.yyscamper.cashnote.Util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.format.Time;

import com.yyscamper.cashnote.Enum.DataType;
import com.yyscamper.cashnote.PayType.LocationGroup;
import com.yyscamper.cashnote.PayType.PayAttendInfo;
import com.yyscamper.cashnote.PayType.PayHistory;
import com.yyscamper.cashnote.PayType.PayLocation;
import com.yyscamper.cashnote.PayType.PayPerson;
import com.yyscamper.cashnote.Storage.StorageObject;

import java.util.Calendar;

/**
 * Created by yuanf on 2014-03-29.
 */
public class Util {
    public static final String[] WEEK_STRING = new String[] {
        "星期日","星期一","星期二","星期三","星期四","星期五","星期六"
    };

    public static String formatDisplayTime(Time t)
    {
        t.normalize(false);
        return String.format("%04d-%02d-%02d %02d:%02d", t.year, t.month + 1, t.monthDay, t.hour, t.minute);
    }

    public static String formatDate(Time t)
    {
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        t.normalize(false);
        if (thisYear == t.year) {
            return String.format("%02d-%02d %s", t.month + 1, t.monthDay, WEEK_STRING[t.weekDay]);
        }
        else {
            return String.format("%04d-%02d-%02d %s", t.year, t.month + 1, t.monthDay, WEEK_STRING[t.weekDay]);
        }
    }

    public static String formatCurrentTime() {
        Time t = new Time();
        t.setToNow();
        return formatDisplayTime(t);
    }

    public static void showErrorDialog(Context ctx, String msg, String title) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(ctx);
        dialog.setTitle(title);
        dialog.setMessage(msg);
        dialog.setPositiveButton("OK", null);
        dialog.show();
    }

    public static void showErrorDialog(Context ctx, String msg) {
        showErrorDialog(ctx, msg, "错误");
    }

    public static void showConfirmDialog(Context ctx, String msg, String title, DialogInterface.OnClickListener positiveListen) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(ctx);
        dialog.setTitle(title);
        dialog.setMessage(msg);

        dialog.setPositiveButton("确定", positiveListen);
        dialog.setNegativeButton("取消", null);
        dialog.show();
    }

    public static String stringArrayJoin(String[] arr, String joinStr) {
        StringBuffer sb = new StringBuffer();
        int i;
        for (i = 0; i < arr.length; i++) {
            sb.append(arr[i]);
            if (i != arr.length-1) {
                sb.append(joinStr);
            }
        }
        return sb.toString();
    }

    public static double calcMoneySum(PayAttendInfo[] atts) {
        if (atts == null) {
            return 0.0;
        }
        double sum = 0;
        for (PayAttendInfo p : atts) {
            sum += p.getMoney();
        }
        return sum;
    }

    public ValuePair[] convertArrayPayAttendInfoToValuePair(PayAttendInfo[] atts) {
        ValuePair[] vps = new ValuePair[atts.length];
        for (int i = 0; i < atts.length; i++) {
            vps[i] = new ValuePair(atts[i].getName(), atts[i].getMoney());
        }
        return vps;
    }

    public static String formatPrettyDouble(double val) {
        if (Math.abs(val) < 0.01) {
            return "";
        }
        int ival = (int)val;
        double delta = Math.abs(val - ival);
        if (delta < 0.01) {
            return String.format("%d", ival);
        }
        else {
            return String.format("%.1f", val);
        }
    }

    public static String DataTypeToString(DataType dataType) {
        return dataType.toString();
    }

    public static StorageObject createByType(DataType type) {
        switch (type) {
            case HISTORY:
                return new PayHistory();
            case LOCATION_GROUP:
                return new LocationGroup();
            case LOCATION:
                return new PayLocation();
            case PERSON:
                return new PayPerson();
            default:
                return null;
        }
    }

    public static boolean isZero(double val) {
        return (Math.abs(val) <= 0.01);
    }
}
