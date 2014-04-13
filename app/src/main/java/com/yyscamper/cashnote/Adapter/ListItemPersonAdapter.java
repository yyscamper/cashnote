package com.yyscamper.cashnote.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.yyscamper.cashnote.PayType.*;
import com.yyscamper.cashnote.R;
import com.yyscamper.cashnote.Storage.StorageObject;
import com.yyscamper.cashnote.Util.Util;

import java.util.Comparator;

/**
 * Created by YuanYu on 14-3-16.
 */
public class ListItemPersonAdapter extends BaseAdapter {
    private Context mContext;
    private PayPerson[] mPersonArray;
    private LayoutInflater mInflater;

    public ListItemPersonAdapter(Context ctx, PayPerson[] perArr)
    {
        this.mContext = ctx;
        this.mPersonArray = perArr;
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
            convertView = mInflater.inflate(R.layout.list_item_person, null);
        }

        PayPerson person = (PayPerson)mPersonArray[position];


        TextView personNameView = (TextView) convertView.findViewById(R.id.textViewName);
        TextView personBalanceView = (TextView) convertView.findViewById(R.id.textViewBalance);
        TextView personPayCountView = (TextView) convertView.findViewById(R.id.textViewPayCount);
        TextView personAttendCountView = (TextView) convertView.findViewById(R.id.textViewAttendCount);

        personNameView.setText(person.getName());
        personBalanceView.setText(Util.formatPrettyDouble(person.getBalance()));
        personPayCountView.setText("付款:" + String.valueOf(person.getPayCount()));
        personAttendCountView.setText("出席:" + String.valueOf(person.getAttendCount()));

        return convertView;
    }
}
