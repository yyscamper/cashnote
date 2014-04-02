package com.yyscamper.cashnote;

import android.content.*;
import android.view.*;
import com.yyscamper.cashnote.PayType.*;
import com.yyscamper.cashnote.Util.Util;

import java.util.*;

import android.widget.*;

/**
 * Created by YuanYu on 14-3-16.
 */
public class PayHistoryListItemAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<PayHistory> mEntryList;
    private LayoutInflater mInflater;

    public PayHistoryListItemAdapter(Context ctx, ArrayList<PayHistory> arrList)
    {
        this.mContext = ctx;
        this.mEntryList = arrList;
        mInflater = LayoutInflater.from(ctx);
    }

    @Override
    public int getCount()
    {
        return mEntryList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return mEntryList.get(position);
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

    public void removeAt(int postion)
    {
        if (postion >= 0 && postion < mEntryList.size()) {
            mEntryList.remove(postion);
            notifyDataSetChanged();
        }

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.pay_list_item, null);
        }

        PayHistory entry = mEntryList.get(position);
        TextView payDateView = (TextView) convertView.findViewById(R.id.textViewPayDate);
        payDateView.setText(Util.formatDate(entry.PayTime));

        TextView payerNameView = (TextView) convertView.findViewById(R.id.textViewPayerName);
        payerNameView.setText(entry.PayerName);

        TextView locationView = (TextView) convertView.findViewById(R.id.textViewLocation);
        locationView.setText(entry.Location);

        TextView moneyView = (TextView) convertView.findViewById(R.id.textViewMoney);
        moneyView.setText(String.format("%.1f", entry.Money));

        TextView attendView = (TextView) convertView.findViewById(R.id.textViewAttendPersons);
        attendView.setText(entry.getAttendPersonNameString());

        return convertView;
    }
}
