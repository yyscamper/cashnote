package com.yyscamper.cashnote;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;

import com.yyscamper.cashnote.PayType.PayLocation;
import com.yyscamper.cashnote.PayType.PayLocationManager;
import com.yyscamper.cashnote.PayType.StorageSelector;
import com.yyscamper.cashnote.R;
import com.yyscamper.cashnote.Util.Util;

public class SelectLocationActivity extends Activity implements AdapterView.OnItemClickListener {
    private MultiAutoCompleteTextView mFilterLocationView;
    private ListView mListLocationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_location);
        mFilterLocationView = (MultiAutoCompleteTextView)findViewById(R.id.multiAutoCompTextViewInputLocation);
        mListLocationView = (ListView)findViewById(R.id.listViewLocations);

        ArrayAdapter filterAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, PayLocationManager.getAllNames());
        mFilterLocationView.setAdapter(filterAdapter);

        int choiceMode = getIntent().getIntExtra("choice_mode", ListView.CHOICE_MODE_SINGLE);
        ArrayAdapter listAdapter;
        if (choiceMode == ListView.CHOICE_MODE_MULTIPLE) {
            listAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, PayLocationManager.getAllNames());
            mListLocationView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            String[] preSelectedItems = getIntent().getStringArrayExtra("pre_selected_items");
            if (preSelectedItems != null) {
                for (String item : preSelectedItems) {
                    for (int j = 0; j < mListLocationView.getCount(); j++) {
                        if (mListLocationView.getAdapter().getItem(j).toString().equals(item)) {
                            mListLocationView.setItemChecked(j, true);
                        }
                    }
                }
            }
        }
        else {
            mListLocationView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            listAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_single_choice, PayLocationManager.getAllNames());
        }
        mListLocationView.setAdapter(listAdapter);
        mListLocationView.setOnItemClickListener(this);
        mFilterLocationView.setOnItemClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (mListLocationView.getChoiceMode() == ListView.CHOICE_MODE_MULTIPLE) {
            getMenuInflater().inflate(R.menu.select_location, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_accept) {
            completeChoice();
            return true;
        }
        else if (id == R.id.action_new_location) {
            return true;
        }
        else if (id == R.id.action_select_all) {
            for (int i = 0; i < mListLocationView.getCount(); i++) {
                mListLocationView.setItemChecked(i, true);
            }
            return true;
        }
        else if (id == R.id.action_select_all) {
            for (int i = 0; i < mListLocationView.getCount(); i++) {
                mListLocationView.setItemChecked(i, false);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void completeChoice() {
        if (mListLocationView.getChoiceMode() != ListView.CHOICE_MODE_MULTIPLE) {
            return;
        }
        Intent outIntent = new Intent();
        SparseBooleanArray sels = mListLocationView.getCheckedItemPositions();
        String[] outArr = new String[sels.size()];

        if (sels.size() <= 0) {
            outIntent.putExtra("selected_items", outArr);
            setResult(RESULT_CANCELED, outIntent);
            this.finish();
            return;
        }

        for (int i = 0; i < sels.size(); i++)
        {
            outArr[i] = mListLocationView.getAdapter().getItem(sels.keyAt(i)).toString();
        }
        outIntent.putExtra("selected_items", outArr);
        setResult(RESULT_OK, outIntent);
        this.finish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mListLocationView.getChoiceMode() == ListView.CHOICE_MODE_SINGLE) {
            Intent outIntent = new Intent();
            outIntent.putExtra("selected_item", mListLocationView.getAdapter().getItem(position).toString());
            setResult(RESULT_OK, outIntent);
            this.finish();
        }
    }

    private void handleNewLocation()
    {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("新增地点");
        final EditText input = new EditText(this);
        input.setHint("请在此输入餐饮的名称");
        dialog.setView(input);

        dialog.setPositiveButton(getString(R.string.txtOK), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String str = input.getText().toString().trim();
                if (str.length() == 0) {
                    Util.ShowErrorDialog(getApplication(), "The location name cannot be empty!", "Error");
                    return;
                }
                else if (PayLocationManager.contains(str)) {
                    Util.ShowErrorDialog(getApplication(), "The location has already existed!", "Error");
                    return;
                }
                PayLocationManager.add(new PayLocation(str), StorageSelector.ALL);
            }
        });
        dialog.setNegativeButton(getString(R.string.txtCancle), null);
        dialog.show();
    }
}
