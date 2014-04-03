package com.yyscamper.cashnote;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import com.yyscamper.cashnote.PayType.*;

import java.util.ArrayList;

/**
 * Created by YuanYu on 14-3-16.
 */
public class AttendInfoInputAdapter extends BaseAdapter {
    private Context mContext;
    private PayPerson[] mPersonArray;
    private View[] mViewArray;
    private LayoutInflater mInflater;
    private int mSortType = PayPersonManager.SORT_MONEY_ASCENDING;
    private boolean mSelectedFlag[];
    public AttendInfoInputAdapter(Context ctx, PayPerson[] perArr, int sortType)
    {
        this.mContext = ctx;
        this.mPersonArray = perArr;
        this.mSortType = sortType;
        this.mViewArray = new View[perArr.length];
        this.mSelectedFlag = new boolean[perArr.length];
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
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.attend_info_input, null);
            mViewArray[position] = convertView;
        }

        PayPerson person = mPersonArray[position];
        final ViewGroup groupView = parent;
        TextView personNameView = (TextView) convertView.findViewById(R.id.textViewName);
        final EditText moneyInputView = (EditText) convertView.findViewById(R.id.editTextMoney);
        CheckBox selectedView = (CheckBox) convertView.findViewById(R.id.checkBoxSelected);

        selectedView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                moneyInputView.setEnabled(b);
                /*
                if (b) {
                    groupView.setBackgroundColor(Color.BLUE);
                }
                else {
                    groupView.setBackgroundColor(Color.WHITE);
                }
                */
                mSelectedFlag[position] = b;
            }
        });
        selectedView.setChecked(false);
        personNameView.setText(person.Name);
        moneyInputView.setText("");

        return convertView;
    }

    public void selectItem(int position) {
        CheckBox selectedView = (CheckBox)mViewArray[position].findViewById(R.id.checkBoxSelected);
        selectedView.setChecked(true);
    }

    public void toggleSelected(int position) {
        CheckBox selectedView = (CheckBox)mViewArray[position].findViewById(R.id.checkBoxSelected);
        selectedView.setChecked(!mSelectedFlag[position]);
    }

    public PayAttendInfo[] getSelectedAtttendInfo() {
        int len = 0;
        for (boolean b : mSelectedFlag) {
            len += (b ? 1 : 0);
        }
        PayAttendInfo[] atts = new PayAttendInfo[len];
        int j = 0;
        for (int i = 0; i < mSelectedFlag.length; i++) {
            if (!mSelectedFlag[i])
                continue;
            View v = mViewArray[i];
            EditText moneyInput = (EditText)v.findViewById(R.id.editTextMoney);
            String str = moneyInput.getText().toString();
            double money;
            if (str.trim().length() == 0) {
                money = 0.0;
            }
            else {
                money = Double.parseDouble(str);
            }
            PayAttendInfo info = new PayAttendInfo(mPersonArray[i].Name, money);
            atts[j++] = info;
        }
        return atts;
    }
}
