package com.yyscamper.cashnote;

import android.app.ActionBar;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.yyscamper.cashnote.PayType.*;
import com.yyscamper.cashnote.Util.Util;

import java.util.ArrayList;

/**
 * Created by YuanYu on 14-3-16.
 */
public class ListPayFieldsInputAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;

    public static final int POS_SELECT_PAYER = 0;
    public static final int POS_SELECT_ATTENDS = 1;
    public static final int POS_SELECT_TIME = 2;
    public static final int POS_SELECT_LOCATION = 3;

    public static final String[] FIELDS_NAMES = new String[] {"付款人","参与者","时间","地点"};

    private View[] mSavedViewTable = new View[4];

    public ListPayFieldsInputAdapter(Context ctx)
    {
        this.mContext = ctx;
        mInflater = LayoutInflater.from(ctx);
    }

    @Override
    public int getCount()
    {
        return 4;
    }

    @Override
    public Object getItem(int position)
    {
        return getField(position);
    }

    public String getField(int position) {
        if (mSavedViewTable[position] == null) {
            return "";
        }
        TextView tv = (TextView)mSavedViewTable[position].findViewById(R.id.textViewColumnRight);
        return tv.getText().toString();
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    public void setField(int position, String str) {
        if (mSavedViewTable[position] == null) {
            return;
        }
        TextView tv = (TextView)mSavedViewTable[position].findViewById(R.id.textViewColumnRight);
        tv.setText(str);
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
            convertView = mInflater.inflate(R.layout.row_with_two_columns, null);
            TextView leftView = (TextView)convertView.findViewById(R.id.textViewColumnLeft);
            TextView rightView = (TextView)convertView.findViewById(R.id.textViewColumnRight);
            LinearLayout.LayoutParams leftParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, 0.3f);
            leftView.setLayoutParams(leftParam);
            LinearLayout.LayoutParams rightParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, 0.7f);
            rightView.setLayoutParams(rightParam);
            leftView.setText(FIELDS_NAMES[position]);

            switch (position) {
                case POS_SELECT_PAYER:
                    rightView.setText("");
                    break;
                case POS_SELECT_ATTENDS:
                    rightView.setText("");
                    break;
                case POS_SELECT_TIME:
                    rightView.setText(Util.formatCurrentTime());
                    break;
                case POS_SELECT_LOCATION:
                    rightView.setText("");
                    break;
            }
        }

        mSavedViewTable[position] = convertView;
        return convertView;
    }
}
