package com.yyscamper.cashnote;

import android.app.*;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import com.yyscamper.cashnote.PayType.*;

public class SelectPersonActivity extends Activity {
	private static final int RESULT_CANCLED = 0;
	ListView _lstView = null;
	int mCallId;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_persons);
		
		_lstView = (ListView)findViewById(R.id.listViewPersons);
		_lstView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_checked,
                PayPersonManager.getAllSortedNames()));

        if (getIntent().hasExtra("pre_selected_persons"))
        {
            String[] arrSelected = getIntent().getStringArrayExtra("pre_selected_persons");
            for (String str : arrSelected) {
                for (int i = 0; i < _lstView.getAdapter().getCount(); i++) {
                    String name = (String) _lstView.getAdapter().getItem(i);
                    if (str.compareToIgnoreCase(name) == 0) {
                        _lstView.setItemChecked(i, true);
                        break;
                    }
                }
            }
        }

        getIntent().getIntExtra("call_id", -1);
		
		//((Button)findViewById(R.id.bntOK)).setOnClickListener(this);
		//((Button)findViewById(R.id.btnCancle)).setOnClickListener(this);

        getActionBar().setDisplayHomeAsUpEnabled(true);
		
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.menu_select_persons, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void acceptSelection() {
        Intent outIntent = new Intent();
        SparseBooleanArray sels = _lstView.getCheckedItemPositions();
        String[] outArr = new String[sels.size()];

        if (sels.size() <= 0) {
            outIntent.putExtra("select_persons", outArr);
            setResult(RESULT_CANCLED, outIntent);
            this.finish();
            return;
        }

        for (int i = 0; i < sels.size(); i++)
        {
            outArr[i] = _lstView.getAdapter().getItem(sels.keyAt(i)).toString();
        }
        outIntent.putExtra("select_persons", outArr);
        outIntent.putExtra("call_id", mCallId);
        setResult(RESULT_OK, outIntent);
        this.finish();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_accept) {
            acceptSelection();
            return true;
        }
        else {
            return super.onOptionsItemSelected(item);
        }
    }

    /*
    @Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bntOK:
			Intent outIntent = new Intent();
			SparseBooleanArray sels = _lstView.getCheckedItemPositions();
			String[] outArr = new String[sels.size()];
			
			if (sels.size() <= 0) {
				outIntent.putExtra("select_persons", outArr);
				setResult(RESULT_CANCLED, outIntent);
				this.finish();
				return;
			}
			
			for (int i = 0; i < sels.size(); i++)
			{
				outArr[i] = _lstView.getAdapter().getItem(sels.keyAt(i)).toString();
			}
			outIntent.putExtra("select_persons", outArr);
            outIntent.putExtra("call_id", mCallId);
			setResult(RESULT_OK, outIntent);
			this.finish();
			break;
		case R.id.btnCancle:
			setResult(RESULT_CANCLED, null);
			this.finish();
		}
		
	}
	*/
}
