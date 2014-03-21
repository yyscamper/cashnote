package com.yyscamper.teamaccount;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yyscamper.teamaccount.PayType.*;

import java.util.ArrayList;

/**
 * Created by YuanYu on 14-3-16.
 */
public class PayPersonViewListItemAdapter extends BaseAdapter {
    private Context mContext;
    private PayPerson[] mPersonArray;
    private LayoutInflater mInflater;
    private int mSortType = PayPersonManager.SORT_MONEY_ASCENDING;

    public PayPersonViewListItemAdapter(Context ctx, PayPerson[] perArr, int sortType)
    {
        this.mContext = ctx;
        this.mPersonArray = perArr;
        this.mSortType = sortType;
        mInflater = LayoutInflater.from(ctx);
    }

    @Override
    public int getCount()
    {
        return mPersonArray.length;
    }

    @Override
    public Object getItem(int position)
    {
        return mPersonArray[position];
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public int getViewTypeCount()
    {
        return 1;
    }

    @Override
    public int getItemViewType(int position)
    {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.pay_person_list_view_item, null);
        }

        PayPerson person = mPersonArray[position];


        TextView personNameView = (TextView) convertView.findViewById(R.id.textViewName);
        TextView personBalanceView = (TextView) convertView.findViewById(R.id.textViewBalance);
        TextView personPayCountView = (TextView) convertView.findViewById(R.id.textViewPayCount);
        TextView personAttendCountView = (TextView) convertView.findViewById(R.id.textViewAttendCount);

        personNameView.setText(person.Name);
        personBalanceView.setText(String.format("%.1f", person.Balance));
        personPayCountView.setText("Pay:" + String.valueOf(person.PayCount));
        personAttendCountView.setText("Attend:" + String.valueOf(person.AttendCount));

        return convertView;
    }
}
