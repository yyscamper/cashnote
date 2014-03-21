package com.yyscamper.teamaccount;

import android.app.*;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import android.support.v7.app.ActionBarActivity;
import com.yyscamper.teamaccount.PayType.PayPersonManager;

public class SelectPersonActivity extends ActionBarActivity implements OnClickListener {
	private static final int RESULT_CANCLED = 0;
	ListView _lstView = null;
	
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
		
		((Button)findViewById(R.id.bntOK)).setOnClickListener(this);
		((Button)findViewById(R.id.btnCancle)).setOnClickListener(this);
		
	}

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
			setResult(RESULT_OK, outIntent);
			this.finish();
			break;
		case R.id.btnCancle:
			setResult(RESULT_CANCLED, null);
			this.finish();
		}
		
	}
}
