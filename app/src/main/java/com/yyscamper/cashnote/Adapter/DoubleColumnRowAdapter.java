package com.yyscamper.cashnote.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yyscamper.cashnote.R;
import com.yyscamper.cashnote.Util.Util;
import com.yyscamper.cashnote.Util.ValuePair;

/**
 * Created by YuanYu on 14-3-16.
 */
public class DoubleColumnRowAdapter extends BaseAdapter {
    private ValuePair[] mValues;
    private Context mContext;
    private LayoutInflater mInflater;
    private float mLeftWeight;
    private float mRightWeight;
    private int mHeight = 0;
    public DoubleColumnRowAdapter(Context ctx, ValuePair[] valArr)
    {
        this.mContext = ctx;
        this.mLeftWeight = 1.0f;
        this.mRightWeight = 1.0f;
        mInflater = LayoutInflater.from(ctx);
        mValues = valArr;
    }

    public DoubleColumnRowAdapter(Context ctx, ValuePair[] valArr, float leftWeight, float rightWeight)
    {
        this.mContext = ctx;
        this.mLeftWeight = leftWeight;
        this.mRightWeight = rightWeight;
        mInflater = LayoutInflater.from(ctx);
        mValues = valArr;
    }


    public DoubleColumnRowAdapter(Context ctx, ValuePair[] valArr, float leftWeight, float rightWeight, int height)
    {
        this.mContext = ctx;
        this.mLeftWeight = leftWeight;
        this.mRightWeight = rightWeight;
        this.mHeight = height;
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

    public void updateRightValue(int position, Object newVal) {
        mValues[position].Value1 = newVal;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.double_column_row_view, null);
        }

        ValuePair val = mValues[position];
        TextView viewLeft = (TextView) convertView.findViewById(R.id.textViewColumnLeft);
        TextView viewRight = (TextView) convertView.findViewById(R.id.textViewColumnRight);
        LinearLayout container = (LinearLayout) convertView.findViewById(R.id.container);
        /*viewLeft.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, mLeftWeight));
        viewRight.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, mRightWeight));
                */
        if (val != null && val.Value0 != null)
            viewLeft.setText(val.Value0.toString());
        else
            viewLeft.setText("");

        if (mHeight != 0)
            container.setMinimumHeight(mHeight);

        if (val != null && val.Value1 != null) {
            if (val.Value1 instanceof Double) {
                viewRight.setText(Util.formatPrettyDouble(((Double)val.Value1).doubleValue()));
            }
            else {
                viewRight.setText(val.Value1.toString());
            }
        }
        else
            viewRight.setText("");
        return convertView;
    }
}
