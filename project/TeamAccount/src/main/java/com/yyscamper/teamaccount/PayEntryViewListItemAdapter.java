package com.yyscamper.teamaccount;

import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import com.yyscamper.teamaccount.PayType.*;
import java.util.*;

import android.widget.*;

/**
 * Created by YuanYu on 14-3-16.
 */
public class PayEntryViewListItemAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<PayEntry> mEntryList;
    private LayoutInflater mInflater;

    public PayEntryViewListItemAdapter(Context ctx, ArrayList<PayEntry> arrList)
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
            convertView = mInflater.inflate(R.layout.pay_entry_view_list_item, null);
        }

        PayEntry entry = mEntryList.get(position);
        TextView payDateView = (TextView) convertView.findViewById(R.id.textViewPayDate);
        payDateView.setText(entry.PayTime.format("%Y-%m-%-d %A"));

        TextView payerNameView = (TextView) convertView.findViewById(R.id.textViewPayerName);
        payerNameView.setText(entry.Payer.Name);

        TextView locationView = (TextView) convertView.findViewById(R.id.textViewLocation);
        locationView.setText(entry.Place);

        TextView moneyView = (TextView) convertView.findViewById(R.id.textViewMoney);
        moneyView.setText(String.valueOf(entry.Money));

        StringBuffer sb = new StringBuffer();
        for (PayPerson p : entry.AttendPersons) {
            sb.append(p.Name);
            sb.append(',');
        }
        TextView attendView = (TextView) convertView.findViewById(R.id.textViewAttendPersons);
        attendView.setText(sb.toString());

        return convertView;
    }
}
