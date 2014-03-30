package com.yyscamper.cashnote.Util;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Paint;
import android.text.TextUtils;
import android.text.format.Time;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by yuanf on 2014-03-29.
 */
public class Util {
    public static String formatDisplayTime(Time t)
    {
        return String.format("%04d-%02d-%02d %02d:%02d", t.year, t.month + 1, t.monthDay, t.hour, t.minute);
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
