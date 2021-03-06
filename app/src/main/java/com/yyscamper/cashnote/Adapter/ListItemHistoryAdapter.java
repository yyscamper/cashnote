package com.yyscamper.cashnote.Adapter;

import android.content.*;
import android.view.*;
import com.yyscamper.cashnote.PayType.*;
import com.yyscamper.cashnote.R;
import com.yyscamper.cashnote.Util.Util;

import java.util.*;

import android.widget.*;

/**
 * Created by YuanYu on 14-3-16.
 */
public class ListItemHistoryAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<PayHistory> mEntryList;
    private LayoutInflater mInflater;

    public ListItemHistoryAdapter(Context ctx, ArrayList<PayHistory> arrList)
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
            convertView = mInflater.inflate(R.layout.list_item_history, null);
        }

        PayHistory entry = mEntryList.get(position);
        TextView payDateView = (TextView) convertView.findViewById(R.id.textViewPayDate);
        payDateView.setText(Util.formatDate(entry.getPayTime()));

        TextView payerNameView = (TextView) convertView.findViewById(R.id.textViewPayerName);
        payerNameView.setText(entry.getPayerName());

        TextView locationView = (TextView) convertView.findViewById(R.id.textViewLocation);
        locationView.setText(entry.getLocationName());

        TextView moneyView = (TextView) convertView.findViewById(R.id.textViewMoney);
        moneyView.setText(String.format("%.1f", entry.getMoney()));

        return convertView;
    }
}
