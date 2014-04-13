package com.yyscamper.cashnote;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.yyscamper.cashnote.Adapter.DoubleColumnRowAdapter;
import com.yyscamper.cashnote.PayType.PayAttendInfo;
import com.yyscamper.cashnote.PayType.PayComparator;
import com.yyscamper.cashnote.PayType.PayPerson;
import com.yyscamper.cashnote.Storage.CacheStorage;
import com.yyscamper.cashnote.Util.Util;
import com.yyscamper.cashnote.Util.ValuePair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;


public class AttendInfoInputActivity extends Activity implements AdapterView.OnItemClickListener {
    ListView mListView;
    DoubleColumnRowAdapter mAdapter;
    public static final String KEY_PRE_SELECTED_ITEMS = "pre_selected_item";
    public static final String KEY_RESULTS = "results";
    public static Hashtable<String, Double> mResultTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.single_list_view);
        mListView = (ListView)findViewById(R.id.listView);
        mResultTable = new Hashtable<String, Double>();

        PayPerson[] allPersons = CacheStorage.getInstance().getAllPersons(PayComparator.KeyAsc);
        ValuePair[] vpArr = new ValuePair[allPersons.length];
        PayAttendInfo[] preAtts = null;
        if (getIntent().hasExtra(KEY_PRE_SELECTED_ITEMS)) {
            Parcelable[] preSels = getIntent().getParcelableArrayExtra(KEY_PRE_SELECTED_ITEMS);
            preAtts = new PayAttendInfo[preSels.length];
            System.arraycopy(preSels, 0, preAtts, 0, preSels.length);
            for (PayAttendInfo p : preAtts) {
                mResultTable.put(p.getName(), new Double(p.getMoney()));
            }
        }

        for (int i = 0; i < allPersons.length; i++) {
            if (mResultTable.containsKey(allPersons[i].getName())) {
                double money = mResultTable.get(allPersons[i].getName()).doubleValue();
                vpArr[i] = new ValuePair(allPersons[i].getName(), new Double(money));
            }
            else {
                vpArr[i] = new ValuePair(allPersons[i].getName(), new Double(0.0));
            }
        }

        mAdapter = new DoubleColumnRowAdapter(this, vpArr);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(this);

        /*mAdapter = new AttendInfoInputAdapter(this, PayPersonManager.getAllPersons(), PayPersonManager.SORT_NAME_ASCENDING);
        mListView.setAdapter(mAdapter);

        if (getIntent().hasExtra(KEY_PRE_SELECTED_ITEMS)) {
            Parcelable[] preSels = getIntent().getParcelableArrayExtra(KEY_PRE_SELECTED_ITEMS);
            for (Parcelable p : preSels) {
                PayAttendInfo pai = (PayAttendInfo)p;
                mAdapter.initItem(pai.getName(), pai.getMoney());
            }
        }*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.single_accept_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_accept) {
            Intent intent = new Intent();
            try {
                PayAttendInfo[] atts = getResult();
                intent.putExtra(KEY_RESULTS, atts);
            }
            catch (Throwable err) {
                Util.showErrorDialog(this, err.getMessage(), "错误");
            }
            setResult(RESULT_OK, intent);
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private PayAttendInfo[] getResult() {
        ArrayList<String> alList = new ArrayList<String>();
        for (String str : mResultTable.keySet())
            alList.add(str);

        Collections.sort(alList, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((String) o1).compareToIgnoreCase((String) o2);
            }
        });

        PayAttendInfo[] resultArr = new PayAttendInfo[alList.size()];
        int i = 0;
        for (String strKey : alList) {
            resultArr[i++] = new PayAttendInfo(strKey, mResultTable.get(strKey).doubleValue());
        }
        return resultArr;
    }

    @Override
    public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
        final TextView tvLeft = (TextView)view.findViewById(R.id.textViewColumnLeft);
        final TextView tvRight = (TextView)view.findViewById(R.id.textViewColumnRight);
        final String strLeft = tvLeft.getText().toString();

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("输入成员\"" + strLeft + "\"的金额");
        final EditText input = new EditText(this);
        if (mResultTable.containsKey(strLeft)) {
            double money = mResultTable.get(strLeft).doubleValue();
            input.setText(Util.formatPrettyDouble(money));
        }
        input.setFocusableInTouchMode(true);
        input.setFocusable(true);
        input.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        input.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);
        dialog.setView(input);
        input.requestFocus();
        dialog.setPositiveButton(getString(R.string.txtOK), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String str = input.getText().toString().trim();

                if (str.length() == 0) {
                    if (mResultTable.containsKey(strLeft)) {
                        mResultTable.remove(strLeft);
                    }
                    tvRight.setText(str);
                    mAdapter.updateRightValue(position, new Double(0.0));
                }
                else {
                    try {
                        double val = Double.parseDouble(str);
                        tvRight.setText(Util.formatPrettyDouble(val));
                        if (Math.abs(val) < 0.01) {
                            if (mResultTable.containsKey(strLeft)) {
                                mResultTable.remove(strLeft);
                            }
                            mAdapter.updateRightValue(position, new Double(0.0));
                        }
                        else {
                            if (mResultTable.containsKey(strLeft)) {
                                mResultTable.remove(strLeft);
                            }
                            mResultTable.put(strLeft, new Double(val));
                            mAdapter.updateRightValue(position, new Double(val));
                        }
                    } catch (Throwable err) {
                        Util.showErrorDialog(getApplication(), "输入的金额格式不正确!", "错误");
                    }
                }
            }
        });
        dialog.setNegativeButton(getString(R.string.txtCancle), null);
        dialog.show();
    }
}
