package com.yyscamper.cashnote.PayType;

import android.text.format.Time;

import com.yyscamper.cashnote.Util.Util;

import java.util.Comparator;

/**
 * Created by yuanf on 2014-04-13.
 */
public class PayComparator {
    public static Comparator StringAsc = new Comparator() {
        @Override
        public int compare(Object lhs, Object rhs) {
            return lhs.toString().compareTo(rhs.toString());
        }
    };

    public static Comparator StringDesc = new Comparator() {
        @Override
        public int compare(Object lhs, Object rhs) {
            return rhs.toString().compareTo(lhs.toString());
        }
    };

    public static Comparator KeyAsc = StringAsc;
    public static Comparator KeyDesc = StringDesc;

    public static Comparator HistoryPayTimeAsc = new Comparator() {
        @Override
        public int compare(Object lhs, Object rhs) {
            Time v1 = ((PayHistory)lhs).getPayTime();
            Time v2 = ((PayHistory)rhs).getPayTime();
            return Time.compare(v1, v2);
        }
    };

    public static Comparator HistoryPayTimeDesc = new Comparator() {
        @Override
        public int compare(Object lhs, Object rhs) {
            Time v1 = ((PayHistory)lhs).getPayTime();
            Time v2 = ((PayHistory)rhs).getPayTime();
            return Time.compare(v2, v1);
        }
    };

    public static Comparator PersonBalanceAsc = new Comparator() {
        @Override
        public int compare(Object lhs, Object rhs) {
            double v1 = ((PayPerson)lhs).getBalance();
            double v2 = ((PayPerson)rhs).getBalance();
            return Double.compare(v1, v2);
        }
    };

    public static Comparator PersonBalanceDesc = new Comparator() {
        @Override
        public int compare(Object lhs, Object rhs) {
            double v1 = ((PayPerson)lhs).getBalance();
            double v2 = ((PayPerson)rhs).getBalance();
            return Double.compare(v2, v1);
        }
    };

    public static Comparator PersonAttendCountAsc = new Comparator() {
        @Override
        public int compare(Object lhs, Object rhs) {
            long v1 = ((PayPerson)lhs).getAttendCount();
            long v2 = ((PayPerson)rhs).getAttendCount();
            if (v1 == v2)
                return 0;
            else
                return v1 > v2 ? 1 : -1;
        }
    };

    public static Comparator PersonAttendCountDesc = new Comparator() {
        @Override
        public int compare(Object lhs, Object rhs) {
            long v1 = ((PayPerson)lhs).getAttendCount();
            long v2 = ((PayPerson)rhs).getAttendCount();
            if (v1 == v2)
                return 0;
            else
                return v2 > v1 ? 1 : -1;
        }
    };

    public static Comparator LocationAttendCountAsc = new Comparator() {
        @Override
        public int compare(Object lhs, Object rhs) {
            long v1 = ((PayLocation)lhs).getAttendCount();
            long v2 = ((PayLocation)rhs).getAttendCount();
            if (v1 == v2)
                return 0;
            else
                return v1 > v2 ? 1 : -1;
        }
    };

    public static Comparator LocationAttendCountDesc = new Comparator() {
        @Override
        public int compare(Object lhs, Object rhs) {
            long v1 = ((PayLocation)lhs).getAttendCount();
            long v2 = ((PayLocation)rhs).getAttendCount();
            if (v1 == v2)
                return 0;
            else
                return v2 > v1 ? 1 : -1;
        }
    };
}
