package com.yyscamper.cashnote.Util;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Paint;
import android.text.TextUtils;
import android.text.format.Time;
import android.widget.TextView;

import java.util.ArrayList;
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

    public static void ShowErrorDialog(Context ctx, String msg, String title) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(ctx);
        dialog.setTitle(title);
        dialog.setMessage(msg);
        dialog.setPositiveButton("OK", null);
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
}
