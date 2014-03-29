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

/**
 * A placeholder fragment containing a simple view.
 */
public class FragmentNewPayEntry extends Fragment implements View.OnClickListener {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final int RequestCodeSelectAttendPersons = 1;

    private static FragmentNewPayEntry mInstance;
    private String[] mSelectedAttend = new String[0];
    private Time     mCurrentTime;

    private EditText mViewInputMoney;
    private EditText mViewDescription;
    private Button   mViewSelectPayTime;
    private Button   mViewSelectAttend;
    private Spinner  mViewSelectPayer;
    private Spinner  mViewSelectLocation;
    private Button   mViewSave;
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

    private void refreshData() {

        String[] allPersonNames = PayPersonManager.getAllSortedNames();
        ArrayAdapter<String> spinAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, allPersonNames);
        mViewSelectPayer.setAdapter(spinAdapter);
        spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        String[] allLocations = PayLocationManager.getAllNames();
        ArrayAdapter<String> spinSelectLocationAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, allLocations);
        mViewSelectLocation.setAdapter(spinSelectLocationAdapter);
        spinSelectLocationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_new_pay_entry, container, false);
        mViewSave = (Button)rootView.findViewById(R.id.btnSavePayEntry);
        mViewInputMoney = (EditText)rootView.findViewById(R.id.editTextInputMoney);
        mViewDescription = (EditText)rootView.findViewById(R.id.editTextPayDescription);
        mViewSelectPayTime = (Button)rootView.findViewById(R.id.btnSelectPayTime);
        mViewSelectAttend = (Button)rootView.findViewById(R.id.btnSelectAttendPersons);
        mViewSelectPayer = (Spinner)rootView.findViewById(R.id.spinnerSelectPayer);
        mViewSelectLocation = (Spinner)rootView.findViewById(R.id.spinnerSelectLocation);

        refreshData();

        mViewSave.setOnClickListener(this);
        mViewSelectPayTime.setOnClickListener(this);
        mViewSelectAttend.setOnClickListener(this);
        mCurrentTime = new Time();
        mCurrentTime.setToNow();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshSelectAttendView();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSelectAttendPersons:
                Intent intent = new Intent(getActivity(), SelectPersonActivity.class);
                intent.putExtra("call_id", this.getId());
                intent.putExtra("pre_selected_persons", mSelectedAttend);
                startActivityForResult(intent, RequestCodeSelectAttendPersons);
                break;
            case R.id.btnSelectPayTime:
                new DatePickerDialog(
                        getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                mCurrentTime.set(dayOfMonth, monthOfYear, year);
                                mViewSelectPayTime.setText(mCurrentTime.format("%Y-%m-%d %A"));
                            }
                        },
                        mCurrentTime.year,
                        mCurrentTime.month,
                        mCurrentTime.monthDay
                ).show();
                break;
            case R.id.btnSavePayEntry:
                saveNewPayEntry();
                break;
        }
    }

    private void refreshSelectAttendView() {
        StringBuffer sb = new StringBuffer();
        for (String str : mSelectedAttend) {
            sb.append(str);
            sb.append(",");
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        mViewSelectAttend.setText(sb.toString());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (RequestCodeSelectAttendPersons == requestCode) {
            if (resultCode != Activity.RESULT_OK)
                return;
            mSelectedAttend = data.getStringArrayExtra("select_persons");
            refreshSelectAttendView();
        }
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

    private void saveNewPayEntry()
    {
        PayEntry entry = new PayEntry();
        if (mViewInputMoney.getText().toString().trim().length() <= 0) {
            showAlertDialog("The input money should not be empty!", "Error Input Money");
            return;
        }
        try {
            entry.Money = Double.valueOf(mViewInputMoney.getText().toString().trim());
        } catch (Exception err) {
            err.printStackTrace();
            showAlertDialog(err.getMessage(), "Error");
            return;
        }

        entry.Description = mViewDescription.getText().toString();
        entry.Location = mViewSelectLocation.getSelectedItem().toString();
        entry.PayerName = mViewSelectPayer.getSelectedItem().toString();

        if (mSelectedAttend == null || mSelectedAttend.length <= 0)
        {
            showAlertDialog("You didn't select any attend persons!", "Error");
            return;
        }
        entry.AttendPersonNames =  mSelectedAttend.clone();

        if (!AccountBook.addPay(entry))
        {
            showAlertDialog("Add entry to account book failed, please check the data format", "Error");
            return;
        }

        mSelectedAttend = new String[0];
        mViewInputMoney.setText("");
        mViewDescription.setText("");
        mViewSelectAttend.setText("");

        FragmentPayHistory.getInstance().notifyNewEntryAdded(entry);

        Toast.makeText(getActivity(), "Save success", Toast.LENGTH_LONG);
    }
}
