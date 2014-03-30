package com.yyscamper.cashnote;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.yyscamper.cashnote.PayType.*;
import com.yyscamper.cashnote.Util.Util;

import java.util.ArrayList;

/**
 * Created by YuanYu on 14-3-16.
 */
public class LocationListItemAdapter extends BaseAdapter {
    private Context mContext;
    private PayLocation[] mAllLocatoins;
    private LayoutInflater mInflater;
    private int mSortType = 0;

    public LocationListItemAdapter(Context ctx)
    {
        this.mContext = ctx;
        this.mAllLocatoins = PayLocationManager.getAll();
        this.mSortType = 0;
        mInflater = LayoutInflater.from(ctx);
    }

    @Override
    public int getCount()
    {
        return mAllLocatoins.length;
    }

    @Override
    public Object getItem(int position)
    {
        return mAllLocatoins[position];
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
            convertView = mInflater.inflate(R.layout.location_list_item, null);
        }

        PayLocation location = mAllLocatoins[position];

        TextView viewName = (TextView) convertView.findViewById(R.id.textViewLocationName);
        TextView viewAttendCount = (TextView) convertView.findViewById(R.id.textViewAttendCount);
        TextView viewLastAttendTime = (TextView) convertView.findViewById(R.id.textViewLastAttendTime);

        viewName.setText(location.Name);
        viewAttendCount.setText("AttendCount:" + String.valueOf(location.AttendCount));
        viewLastAttendTime.setText("LastTime:" + Util.formatDisplayTime(location.LastAttendTime));

        return convertView;
    }

    public void refreshData() {
        this.mAllLocatoins = PayLocationManager.getAll();
        notifyDataSetChanged();
    }
}
