package com.yyscamper.cashnote.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.content.Intent;
import android.text.format.*;

import com.yyscamper.cashnote.AttendInfoInputActivity;
import com.yyscamper.cashnote.Interface.PayHistoryDetailListener;
import com.yyscamper.cashnote.PayType.*;
import com.yyscamper.cashnote.R;
import com.yyscamper.cashnote.SelectLocationActivity;
import com.yyscamper.cashnote.SelectPersonActivity;
import com.yyscamper.cashnote.Util.Util;

import java.util.Calendar;

/**
 * A placeholder fragment containing a simple view.
 */
public class FragmentPayHistoryDetail extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    public static final int MODE_VIEW = 0;
    public static final int MODE_EDIT = 1;
    public static final int MODE_NEW = 2;
    private int mMode = MODE_NEW;
    private PayHistory mCurrPayEntry;

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final int RequestCodeSelectAttendPersons = 1;
    private static final int RequestCodeSelectLocation = 2;
    private static final int RequestCodeSelectPayer = 3;
    private static final int RequestCodeSelectAttendPersonsNotAvg = 10;
    private String[] mSelectedAttend = new String[0];
    private Time mCurrTime;
    private PayAttendInfo[] mPayAttends = null;

    private EditText mViewInputMoney;
    private EditText mViewDescription;
    private TextView mViewSelectPayer;
    private TextView mViewSelectAttends;
    private TextView mViewSelectTime;
    private TextView mViewSelectLocation;
    private Button   mViewSave;
    private RadioButton mRadioButtonAvg;
    private RadioButton mRadioButtonNotAvg;
    private TextView mViewAttendsTitle;
    private PayHistoryDetailListener mActionListener;

    public FragmentPayHistoryDetail(int mode, PayHistory entry) {
        this.mMode = mode;
        this.mCurrPayEntry = entry;
        this.mActionListener = null;
    }

    public FragmentPayHistoryDetail(int mode, PayHistory entry, PayHistoryDetailListener listener) {
        this.mMode = mode;
        this.mCurrPayEntry = entry;
        this.mActionListener = listener;
    }

    private void switchAttendsView() {
        String str = "";
        final String joinStr = ", ";
        if (mRadioButtonAvg.isChecked()) {
            if (mSelectedAttend != null && mSelectedAttend.length > 0) {
                str = Util.stringArrayJoin(mSelectedAttend, joinStr);
            }
        }
        else {
            if (mPayAttends != null && mPayAttends.length > 0) {
                StringBuffer sb = new StringBuffer();
                for (PayAttendInfo p : mPayAttends) {
                    sb.append(p.toString() + ", ");
                }
                if (sb.length() > 0) {
                    sb.delete(sb.length() - joinStr.length(), sb.length());
                }
                str = sb.toString();
            }
        }
        mViewSelectAttends.setText(str);
    }

    private void switchAvgMode() {
        boolean avgMode = mRadioButtonAvg.isChecked();
        if (mMode != MODE_VIEW) {
            mViewInputMoney.setEnabled(avgMode);
        }
        else {
            mViewInputMoney.setEnabled(false);
        }
        if (avgMode) {
            mViewAttendsTitle.setText("成员");
        }
        else {
            mViewAttendsTitle.setText("成员与金额");
        }
        switchAttendsView();
    }

    private void initViewData() {
        switch (mMode) {
            case MODE_VIEW:
            case MODE_EDIT:
                mRadioButtonAvg.setChecked(mCurrPayEntry.Type == PayHistory.TYPE_NORMAL_AVG);
                mRadioButtonNotAvg.setChecked(mCurrPayEntry.Type != PayHistory.TYPE_NORMAL_AVG);
                mViewInputMoney.setText(Util.formatPrettyDouble(mCurrPayEntry.Money));
                mViewSelectPayer.setText(mCurrPayEntry.PayerName);
                mSelectedAttend = mCurrPayEntry.getAttendNames();
                mPayAttends = mCurrPayEntry.getAttendsInfo();
                mCurrTime = mCurrPayEntry.PayTime;
                mViewSelectTime.setText(Util.formatDate(mCurrTime));
                mViewSelectLocation.setText(mCurrPayEntry.Location);
                mViewDescription.setText(mCurrPayEntry.Description);
                if (mMode == MODE_EDIT) {
                    mViewSave.setText("保存修改");
                }
                switchAvgMode();
                break;
            case MODE_NEW:
                mRadioButtonAvg.setChecked(true);
                mRadioButtonNotAvg.setChecked(false);
                mViewInputMoney.setText("0.0");
                mViewSelectPayer.setText("");
                mViewSelectAttends.setText("");
                mSelectedAttend = null;
                mPayAttends = null;
                mViewSelectLocation.setText("");
                mCurrTime = new Time();
                mCurrTime.setToNow();
                mViewSelectTime.setText(Util.formatDate(mCurrTime));
                mViewDescription.setText("");
                mViewSave.setText("保存");
                switchAvgMode();
                break;
        }
    }

    private void setViewEnableStatus() {
        boolean flag = !(mMode == MODE_VIEW);
        boolean saveButtonFlag = (mMode != MODE_VIEW);
        mRadioButtonAvg.setEnabled(flag);
        mRadioButtonNotAvg.setEnabled(flag);
        mViewInputMoney.setEnabled(flag);
        mViewSelectPayer.setEnabled(flag);
        mViewSelectAttends.setEnabled(flag);
        mViewSelectTime.setEnabled(flag);
        mViewSelectLocation.setEnabled(flag);
        mViewDescription.setEnabled(flag);
        mViewSave.setVisibility(saveButtonFlag ? View.VISIBLE : View.INVISIBLE);

        mRadioButtonAvg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                switchAvgMode();
            }
        });

        mViewSelectPayer.setOnClickListener(!flag ? null : new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSelectPayer();
            }
        });
        mViewSelectAttends.setOnClickListener(!flag ? null : new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSelectAttends();
            }
        });
        mViewSelectTime.setOnClickListener(!flag ? null : new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSelectTime();
            }
        });
        mViewSelectLocation.setOnClickListener(!flag ? null : new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSelectLocation();
            }
        });

        mViewSave.setOnClickListener(!saveButtonFlag ? null : new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PayHistory newEntry = savePayEntry();
                if (newEntry != null) {
                    if (mMode == MODE_EDIT) {
                        mMode = MODE_VIEW;
                        setViewEnableStatus();
                        if (mActionListener != null) {
                            mActionListener.OnPayHistoryChanged(PayHistoryDetailListener.ACTION_MODIFIED, mCurrPayEntry, newEntry);
                        }
                    }
                    else if (mMode == MODE_NEW) {
                        if (mActionListener != null) {
                            mActionListener.OnPayHistoryChanged(PayHistoryDetailListener.ACTION_ADDE, null, newEntry);
                        }
                    }
                    mCurrPayEntry = newEntry;
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_new_pay_entry, container, false);
        mViewSave = (Button)rootView.findViewById(R.id.btnSavePayEntry);
        mViewInputMoney = (EditText)rootView.findViewById(R.id.editTextInputMoney);
        mViewDescription = (EditText)rootView.findViewById(R.id.editTextPayDescription);
        mViewSelectPayer = (TextView)rootView.findViewById(R.id.textViewSelectPayer);
        mViewSelectAttends = (TextView)rootView.findViewById(R.id.textViewSelectAttends);
        mViewSelectTime = (TextView)rootView.findViewById(R.id.textViewSelectTime);
        mViewSelectLocation = (TextView)rootView.findViewById(R.id.textViewSelectLocation);
        mRadioButtonAvg = (RadioButton)rootView.findViewById(R.id.radioButtonAvg);
        mRadioButtonNotAvg = (RadioButton)rootView.findViewById(R.id.radioButtonNotAvg);
        mViewAttendsTitle = (TextView)rootView.findViewById(R.id.textViewAttends);

        initViewData();
        setViewEnableStatus();

        return rootView;
    }

    private void showAlertDialog(String message, String title)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message);
        builder.setTitle(title);
        builder.setPositiveButton("确定", new AlertDialog.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private PayHistory savePayEntry()
    {
        double money;
        String desc, loc, payerName;
        String[] attNames;

        if (mMode != MODE_NEW  && mMode != MODE_EDIT) {
            return null;
        }

        if (mViewInputMoney.getText().toString().trim().length() <= 0) {
            showAlertDialog("总金额不能为空！", "错误");
            return null;
        }
        try {
            money = Double.parseDouble(mViewInputMoney.getText().toString().trim());
        } catch (Exception err) {
            err.printStackTrace();
            showAlertDialog(err.getMessage(), "错误");
            return null;
        }

        if (money < 0 || money < 0.01) {
            Util.showErrorDialog(getActivity(), "总金额不能为负数或0！", "错误");
            return null;
        }

        payerName = mViewSelectPayer.getText().toString();
        if (payerName.length() == 0) {
            Util.showErrorDialog(getActivity(), "你没有指定付款人!", "错误");
            return null;
        }

        if (mRadioButtonAvg.isChecked() && (mSelectedAttend == null || mSelectedAttend.length == 0)
                || !mRadioButtonAvg.isChecked() && (mPayAttends == null || mPayAttends.length == 0)) {
            Util.showErrorDialog(getActivity(), "你没有选择此次消费的成员", "错误");
            return null;
        }

        loc = mViewSelectLocation.getText().toString().trim();
        if (loc.length() == 0) {
            Util.showErrorDialog(getActivity(), "你没有选择地点", "错误");
            return null;
        }
        desc = mViewDescription.getText().toString().trim();

        PayHistory entry = null;
        if (mRadioButtonAvg.isChecked()) {
            attNames = mSelectedAttend.clone();
            entry = PayHistory.buildAvgHistory(null, money, payerName, attNames, mCurrTime, loc, desc);
        }
        else {
            entry = PayHistory.buildNotAvgHistory(null, payerName, mPayAttends, mCurrTime, loc, desc);
        }

        if (mMode == MODE_EDIT) {
            entry.UUIDString = mCurrPayEntry.UUIDString;
            AccountBook.removePay(entry.UUIDString, StorageSelector.ALL);
        }

        if (!AccountBook.addPay(entry, StorageSelector.ALL)) {
            showAlertDialog("添加到消费记录时出错！", "Error");
            return null;
        }

        if (mMode == MODE_NEW) {
            Toast.makeText(getActivity(), "保存成功", Toast.LENGTH_LONG);
            mSelectedAttend = new String[0];
            mViewInputMoney.setText("");
            mViewDescription.setText("");
            mViewSelectLocation.setText("");
            mViewSelectPayer.setText("");
            mViewSelectAttends.setText("");
        } else if (mMode == MODE_EDIT) {
            Toast.makeText(getActivity(), "修改成功", Toast.LENGTH_LONG);
        }
        return entry;
    }

    private void handleSelectPayer() {
        Intent intent = new Intent(getActivity(), SelectPersonActivity.class);
        intent.putExtra("choice_mode", ListView.CHOICE_MODE_SINGLE);
        startActivityForResult(intent, RequestCodeSelectPayer);
    }

    private void handleSelectAttends() {
        if (mRadioButtonAvg.isChecked()) {
            Intent intent = new Intent(getActivity(), SelectPersonActivity.class);
            intent.putExtra("pre_selected_items", mSelectedAttend);
            intent.putExtra("choice_mode", ListView.CHOICE_MODE_MULTIPLE);
            startActivityForResult(intent, RequestCodeSelectAttendPersons);
        }
        else {
            Intent intent = new Intent(getActivity(), AttendInfoInputActivity.class);
            if (mPayAttends != null && mPayAttends.length > 0) {
                intent.putExtra(AttendInfoInputActivity.KEY_PRE_SELECTED_ITEMS, mPayAttends);
            }
            startActivityForResult(intent, RequestCodeSelectAttendPersonsNotAvg);
        }
    }

    private void handleSelectTime() {
        DatePickerDialog dpd = new DatePickerDialog(
                getActivity(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        mCurrTime.set(dayOfMonth, monthOfYear, year);
                        mCurrTime.normalize(false);
                        mViewSelectTime.setText(Util.formatDate(mCurrTime));
                    }
                },
                mCurrTime.year,
                mCurrTime.month,
                mCurrTime.monthDay
        );
        dpd.getDatePicker().setMaxDate(Calendar.getInstance().getTimeInMillis());
        dpd.show();
    }

    private void handleSelectLocation() {
        Intent intent = new Intent(getActivity(), SelectLocationActivity.class);
        startActivityForResult(intent, RequestCodeSelectLocation);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (RequestCodeSelectPayer == requestCode) {
            if (resultCode != Activity.RESULT_OK)
                return;
            String str = data.getStringExtra("selected_item");
            mViewSelectPayer.setText(str);
        }else if (RequestCodeSelectAttendPersons == requestCode) {
            if (resultCode != Activity.RESULT_OK)
                return;
            mSelectedAttend = data.getStringArrayExtra("selected_items");
            switchAttendsView();
        }
        else if (RequestCodeSelectLocation == requestCode) {
            if (resultCode != Activity.RESULT_OK)
                return;
            String selected_loc = data.getStringExtra("selected_item");
            mViewSelectLocation.setText(selected_loc);
        }
        else if (RequestCodeSelectAttendPersonsNotAvg == requestCode) {
            if (resultCode != Activity.RESULT_OK)
                return;
            Parcelable[] parArr = data.getParcelableArrayExtra(AttendInfoInputActivity.KEY_RESULTS);
            PayAttendInfo[] atts = new PayAttendInfo[parArr.length];
            System.arraycopy(parArr, 0, atts, 0, parArr.length);
            mPayAttends = atts;
            switchAttendsView();
            double sumMoney = Util.calcMoneySum(mPayAttends);
            mViewInputMoney.setText(String.format("%.1f", sumMoney));
        }
    }
}
