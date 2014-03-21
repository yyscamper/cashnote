package com.yyscamper.teamaccount;


//import com.baidu.frontia.Frontia;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import android.view.*;
import android.app.*;
import android.text.format.*;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;

import com.baidu.frontia.api.FrontiaStorage;
import com.baidu.frontia.api.FrontiaStorageListener;
import com.yyscamper.teamaccount.PayType.*;

import java.util.*;
import com.baidu.frontia.*;

public class MainActivity extends ActionBarActivity implements OnClickListener
{
	private static final int RequestCodeSelectAttendPersons = 1;
	private Time mCurrentTime;
    private String[] mSelectedAttend = new String[0];

    private EditText mViewInputMoney;
    private EditText mViewDescription;
    private Button   mViewSelectPayTime;
    private Button   mViewSelectAttend;
    private Spinner  mViewSelectPayer;
    private Spinner  mViewSelectLocation;

    private FrontiaStorage mCloudStorage;

	//private String _apiKey = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        FrontiaApplication.
                initFrontiaApplication(getApplicationContext());

        boolean isInit = Frontia.init(getApplicationContext(), "My2KfGgbVQx4Rg0NnTpywumF");
        if(isInit){//Frontia is successfully initialized.
            //Use Frontia
            mCloudStorage = Frontia.getStorage();
        }
        else {
            showAlertDialog("Initial baidu cloud service failed!", "Error");
        }

        AccountBook.init();
        mCurrentTime = new Time();
        mCurrentTime.setToNow();
        setContentView(R.layout.activity_main);

        mViewInputMoney = (EditText)findViewById(R.id.editTextInputMoney);
        mViewDescription = (EditText)findViewById(R.id.editTextPayDescription);
        mViewSelectPayTime = (Button)findViewById(R.id.btnSelectPayTime);
        mViewSelectAttend = (Button)findViewById(R.id.btnSelectAttendPersons);
        mViewSelectPayer = (Spinner)findViewById(R.id.spinnerSelectPayer);
        mViewSelectLocation = (Spinner)findViewById(R.id.spinnerSelectLocation);

        setupViews();

        if (savedInstanceState != null)
        {
            ((EditText)findViewById(R.id.editTextInputMoney)).setText(savedInstanceState.getString("money"));
            ((EditText)findViewById(R.id.editTextPayDescription)).setText(savedInstanceState.getString("description"));
            //mCurrentTime = (Time)savedInstanceState.get("time");
            //((Button)findViewById(R.id.btnSelectPayTime)).setText(mCurrentTime.format("%Y-%m-%-d %A"));
        }

        //Frontia.init(this.getApplicationContext(), _apiKey);


    }

    private void saveDataToCloud()
    {
        final FrontiaData[] datas = new FrontiaData[4];
        datas[0] = new FrontiaData();
        datas[0].put("money", 120);
        datas[0].put("payer", "Felix");

        datas[1] = new FrontiaData();
        datas[1].put("money", 100);
        datas[1].put("payer", "Alfred");

        datas[2] = new FrontiaData();
        datas[2].put("money", 80);
        datas[2].put("payer", "Andy");

        datas[3] = new FrontiaData();
        datas[3].put("money", 360);
        datas[3].put("payer", "Leo");

        for (int i = 0; i < datas.length; i++)
        {
            mCloudStorage.insertData(datas[i],
            new FrontiaStorageListener.DataInsertListener() {
                @Override
                public void onSuccess() {
                    showAlertDialog("data save success!", "Success");
                };

                @Override
                public void onFailure(int errCode, String errMsg) {
                    showAlertDialog("data save failed. errCode=" + String.valueOf(errCode) +",ErrorMessage:" + errMsg, "Success");
                }
            });
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("money", mViewInputMoney.getText().toString());
        savedInstanceState.putString("description", mViewDescription.getText().toString());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        if (savedInstanceState != null)
        {
            mViewInputMoney.setText(savedInstanceState.getString("money"));
            mViewDescription.setText(savedInstanceState.getString("description"));
            //mCurrentTime = (Time)savedInstanceState.get("time");
            //mViewSelectPayTime.setText(mCurrentTime.format("%Y-%m-%-d %A"));
        }

        super.onRestoreInstanceState(savedInstanceState);
    }


    private void setupViews()
    {

        String[] allPersonNames = PayPersonManager.getAllSortedNames();
        Spinner spinSelectPayer = (Spinner)findViewById(R.id.spinnerSelectPayer);
        ArrayAdapter<String> spinAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, allPersonNames);
        spinSelectPayer.setAdapter(spinAdapter);
        spinAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        String[] allLocations = PayLocationManager.getAllNames();
        Spinner spinSelectLocation = (Spinner)findViewById(R.id.spinnerSelectLocation);
        ArrayAdapter<String> spinSelectLocationAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, allLocations);
        spinSelectLocation.setAdapter(spinSelectLocationAdapter);
        spinSelectLocationAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        Button btnSelectAttPersons = (Button)findViewById(R.id.btnSelectAttendPersons);
        btnSelectAttPersons.setOnClickListener(this);

        ((Button)findViewById(R.id.btnSavePayEntry)).setOnClickListener(this);
        ((Button)findViewById(R.id.btnSelectPayTime)).setOnClickListener(this);

		/*Button btnSelectPayer = (Button)this.findViewById(R.id.btnSelectAttendPersons);
		btnSelectPayer.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent it = new Intent(null, SelectPersonActivity.class);
				Bundle bundle = new Bundle();
				bundle.putBoolean("multi_select", false);
				it.putExtras(bundle);
				startActivity(it);
			}
			
		});
		*/
	}

	@Override
	public void onClick(View v)
    {
		switch (v.getId()) {
            case R.id.btnSelectAttendPersons:
                Intent intent = new Intent(MainActivity.this, SelectPersonActivity.class);
                Bundle bundle = new Bundle();
                bundle.putStringArray("pre_selected_persons", mSelectedAttend);
                intent.putExtras(bundle);
                startActivityForResult(intent, RequestCodeSelectAttendPersons);
                break;

            case R.id.btnSelectPayTime:
                Calendar c = Calendar.getInstance();
                new DatePickerDialog(
                        MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                mCurrentTime.set(dayOfMonth, monthOfYear, year);
                                Button btn = (Button) findViewById(R.id.btnSelectPayTime);
                                btn.setText(mCurrentTime.format("%Y-%m-%-d %A"));
                            }
                        },
                        c.get(Calendar.YEAR),
                        c.get(Calendar.MONDAY),
                        c.get(Calendar.DAY_OF_MONTH)
                ).show();
                break;
            case R.id.btnSavePayEntry:
                PayEntry entry = new PayEntry();
                try {
                    entry.Money = Double.valueOf(((EditText) findViewById(R.id.editTextInputMoney)).getText().toString());
                } catch (Exception err) {
                    err.printStackTrace();
                    showAlertDialog(err.getMessage(), "Error");
                    return;
                }

                entry.Description = ((TextView) findViewById(R.id.editTextPayDescription)).getText().toString();
                entry.Place = ((Spinner) findViewById(R.id.spinnerSelectLocation)).getSelectedItem().toString();
                entry.Payer = PayPersonManager.add(
                        ((Spinner) findViewById(R.id.spinnerSelectPayer)).getSelectedItem().toString());
                if (entry.Payer == null) {
                    showAlertDialog("The Payer is invalid or empty!", "Error");
                    return;
                }
                if (mSelectedAttend == null || mSelectedAttend.length <= 0)
                {
                    showAlertDialog("You didn't select any attend persons!", "Error");
                    return;
                }
                entry.AttendPersons = new PayPerson[mSelectedAttend.length];
                for (int i = 0; i < mSelectedAttend.length; i++) {
                    entry.AttendPersons[i] = PayPersonManager.add(mSelectedAttend[i]);
                }
                if (!AccountBook.addPay(entry))
                {
                    showAlertDialog("Add entry to account book failed, please check the data format", "Error");
                    return;
                }

                Intent intentPayList = new Intent(MainActivity.this, PayEntryListViewActivity.class);
                startActivity(intentPayList);

			    break;
            case R.id.imageButtonPayHistory:
                Intent bintent = new Intent(MainActivity.this, PayEntryListViewActivity.class);
                startActivity(bintent);
                break;
            case R.id.imageButtonPersons:
                Intent cintent = new Intent(MainActivity.this, PayPersonListActivity.class);
                startActivity(cintent);
                break;

            case R.id.imageButtonSync:

                break;
		    default:
		}
	}

    private void showAlertDialog(String message, String title)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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
	
	 @Override
	 protected void onActivityResult(int requestCode, int resultCode, Intent data)
     {
		super.onActivityResult(requestCode, resultCode, data);
		if (RequestCodeSelectAttendPersons == requestCode) {
			if (resultCode != Activity.RESULT_OK)
				return;
			mSelectedAttend = data.getStringArrayExtra("select_persons");
			StringBuffer sb = new StringBuffer();
			for (String str : mSelectedAttend) {
				sb.append(str);
				sb.append(",");
			}
			if (sb.length() > 0) {
				sb.deleteCharAt(sb.length() - 1);
			}
			
			Button btnSelectAttend = (Button)findViewById(R.id.btnSelectAttendPersons);
            btnSelectAttend.setText(sb.toString());
		}
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main_action, menu);
        return true;
    }

    private void loadCloudData()
    {
        FrontiaQuery query = new FrontiaQuery();

        mCloudStorage.findData(query,
                new FrontiaStorageListener.DataInfoListener() {

                    @Override
                    public void onSuccess(List<FrontiaData> dataList) {
                        if (null != mViewDescription) {
                            StringBuilder sb = new StringBuilder();
                            int i = 0;
                            for(FrontiaData d : dataList){
                                sb.append(i).append(":").append(d.toJSON().toString()).append("\n");
                                i++;
                            }
                            mViewDescription.setText("find data\n"
                                    + sb.toString());
                        }
                    }

                    @Override
                    public void onFailure(int errCode, String errMsg) {
                        if (null != mViewDescription) {
                            mViewDescription.setText("errCode:" + errCode
                                    + ", errMsg:" + errMsg);
                        }
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.action_history)
        {
            Intent intent = new Intent(MainActivity.this, PayEntryListViewActivity.class);
            startActivity(intent);
            return true;
        }
        else if (item.getItemId() == R.id.action_person)
        {
            Intent intent = new Intent(MainActivity.this, PayPersonListActivity.class);
            startActivity(intent);
            return true;
        }
        else if (item.getItemId() == R.id.action_sync)
        {
            saveDataToCloud();
            return true;
        }
        else if (item.getItemId() == R.id.action_settings)
        {
            loadCloudData();
            return true;
        }
        else if (item.getItemId() == R.id.action_help)
        {
            return true;
        }
        else
        {
            return super.onOptionsItemSelected(item);
        }
    }
}
