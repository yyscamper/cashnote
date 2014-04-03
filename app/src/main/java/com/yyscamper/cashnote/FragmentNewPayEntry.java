package com.yyscamper.cashnote;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.content.Intent;
import android.text.format.*;

import com.yyscamper.cashnote.PayType.*;
import com.yyscamper.cashnote.Util.Util;

import java.util.Calendar;

/**
 * A placeholder fragment containing a simple view.
 */
public class FragmentNewPayEntry extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final int RequestCodeSelectAttendPersons = 1;
    private static final int RequestCodeSelectLocation = 2;
    private static final int RequestCodeSelectPayer = 3;
    private static final int RequestCodeSelectAttendPersonsNotAvg = 10;

    private static FragmentNewPayEntry mInstance;
    private String[] mSelectedAttend = new String[0];
    private Time     mCurrentTime;

    private EditText mViewInputMoney;
    private EditText mViewDescription;
    private TextView mViewSelectPayer;
    private TextView mViewSelectAttends;
    private TextView mViewSelectTime;
    private TextView mViewSelectLocation;
    private Button   mViewSave;
    private RadioButton mRadioButtonAvg;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static FragmentNewPayEntry getInstance() {
        if (mInstance == null)
            mInstance = new FragmentNewPayEntry();
        return mInstance;
    }

    private FragmentNewPayEntry() {

    }

    public void setSectionNumber(int sectionNumber) {
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        this.setArguments(args);
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
        mCurrentTime = new Time();
        mCurrentTime.normalize(false);
        mCurrentTime.setToNow();
        mViewSelectTime.setText(Util.formatDate(mCurrentTime));

        mRadioButtonAvg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mViewInputMoney.setEnabled(b);
            }
        });

        mViewSelectPayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSelectPayer();
            }
        });

        mViewSelectAttends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSelectAttends();
            }
        });

        mViewSelectTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSelectTime();
            }
        });

        mViewSelectLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSelectLocation();
            }
        });

        mViewSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNewPayHistory();
            }
        });
        return rootView;
    }

    private void showAlertDialog(String message, String title)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message);
        builder.setTitle(title);
        builder.setPositiveButton("OK", new AlertDialog.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void saveNewPayHistory()
    {
        double money;
        String desc, loc, payerName;
        String[] attNames;

        if (mViewInputMoney.getText().toString().trim().length() <= 0) {
            showAlertDialog("The input money should not be empty!", "Error Input Money");
            return;
        }
        try {
            money = Double.valueOf(mViewInputMoney.getText().toString().trim());
        } catch (Exception err) {
            err.printStackTrace();
            showAlertDialog(err.getMessage(), "Error");
            return;
        }

        desc = mViewDescription.getText().toString();
        loc = mViewSelectLocation.getText().toString();
        payerName = mViewSelectPayer.getText().toString();

        if (mSelectedAttend == null || mSelectedAttend.length <= 0)
        {
            showAlertDialog("You didn't select any attend persons!", "Error");
            return;
        }
        attNames =  mSelectedAttend.clone();
        PayHistory entry = PayHistory.buildAvgHistory(null, money, payerName, attNames, mCurrentTime, loc, desc);

        if (!AccountBook.addPay(entry, StorageSelector.ALL))
        {
            showAlertDialog("Add entry to account book failed, please check the data format", "Error");
            return;
        }

        mSelectedAttend = new String[0];
        mViewInputMoney.setText("");
        mViewDescription.setText("");
        mViewSelectLocation.setText("");
        mViewSelectPayer.setText("");
        mViewSelectAttends.setText("");

        FragmentPayHistory.getInstance().notifyNewEntryAdded(entry);

        Toast.makeText(getActivity(), "Save success", Toast.LENGTH_LONG);
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
            startActivityForResult(intent, RequestCodeSelectAttendPersonsNotAvg);
        }
    }

    private void handleSelectTime() {
        DatePickerDialog dpd = new DatePickerDialog(
                getActivity(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        mCurrentTime.set(dayOfMonth, monthOfYear, year);
                        mCurrentTime.normalize(false);
                        mViewSelectTime.setText(mCurrentTime.format("%Y-%m-%d %A"));
                    }
                },
                mCurrentTime.year,
                mCurrentTime.month,
                mCurrentTime.monthDay
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
            mViewSelectAttends.setText(Util.stringArrayJoin(mSelectedAttend, ", "));
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
            PayAttendInfo[] atts = (PayAttendInfo[])data.getParcelableArrayExtra("results");
            StringBuilder sb = new StringBuilder();
            for (PayAttendInfo p : atts)
                sb.append(p.toString() + ",");
            if (sb.length() > 0)
                sb.deleteCharAt(sb.length()-1);
            mViewSelectAttends.setText(sb.toString());
        }
    }
}
