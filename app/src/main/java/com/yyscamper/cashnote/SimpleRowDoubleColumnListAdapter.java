package com.yyscamper.cashnote;

import android.app.ActionBar;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.yyscamper.cashnote.PayType.*;
import com.yyscamper.cashnote.Util.ValuePair;

/**
 * Created by YuanYu on 14-3-16.
 */
public class SimpleRowDoubleColumnListAdapter extends BaseAdapter {
    private ValuePair[] mValues;
    private Context mContext;
    private LayoutInflater mInflater;
    private float mLeftWeight;
    private float mRightWeight;

    public SimpleRowDoubleColumnListAdapter(Context ctx, ValuePair[] valArr)
    {
        this.mContext = ctx;
        this.mLeftWeight = 0.5f;
        this.mRightWeight = 0.5f;
        mInflater = LayoutInflater.from(ctx);
        mValues = valArr;
    }

    public SimpleRowDoubleColumnListAdapter(Context ctx, ValuePair[] valArr, float leftWeight, float rightWeight)
    {
        this.mContext = ctx;
        this.mLeftWeight = leftWeight;
        this.mRightWeight = rightWeight;
        mInflater = LayoutInflater.from(ctx);
        mValues = valArr;
    }

    @Override
    public int getCount()
    {
        return mValues.length;
    }

    @Override
    public Object getItem(int position)
    {
        return mValues[position];
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
            convertView = mInflater.inflate(R.layout.row_with_two_columns, null);
        }

        ValuePair val = mValues[position];
        TextView viewLeft = (TextView) convertView.findViewById(R.id.textViewColumnLeft);
        TextView viewRight = (TextView) convertView.findViewById(R.id.textViewColumnRight);
        viewLeft.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, mLeftWeight));
        viewRight.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, mRightWeight));
        if (val != null && val.Value0 != null)
            viewLeft.setText(val.Value0.toString());
        else
            viewLeft.setText("");

        if (val != null && val.Value1 != null)
            viewRight.setText(val.Value1.toString());
        else
            viewRight.setText("");
        return convertView;
    }
}
