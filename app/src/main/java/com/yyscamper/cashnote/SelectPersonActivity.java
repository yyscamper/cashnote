package com.yyscamper.cashnote;

import android.app.*;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.yyscamper.cashnote.PayType.*;

public class SelectPersonActivity extends Activity implements AdapterView.OnItemClickListener, SearchView.OnQueryTextListener {
	ListView mListView = null;
    SearchView mSearchView = null;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_list_view);
		mListView = (ListView)findViewById(R.id.listView);
        mSearchView = (SearchView)findViewById(R.id.searchView);
        int choiceMode = getIntent().getIntExtra("choice_mode", ListView.CHOICE_MODE_SINGLE);
        if (choiceMode == ListView.CHOICE_MODE_MULTIPLE) {
            mListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice,
                    PayPersonManager.getAllSortedNames()));
            mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        }
        else {
            mListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice,
                    PayPersonManager.getAllSortedNames()));
            mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        }

        if (choiceMode == ListView.CHOICE_MODE_MULTIPLE) {
            if (getIntent().hasExtra("pre_selected_items")) {
                String[] arrSelected = getIntent().getStringArrayExtra("pre_selected_items");
                if (arrSelected != null) {
                    for (String str : arrSelected) {
                        for (int i = 0; i < mListView.getAdapter().getCount(); i++) {
                            String name = (String) mListView.getAdapter().getItem(i);
                            if (str.compareToIgnoreCase(name) == 0) {
                                mListView.setItemChecked(i, true);
                                break;
                            }
                        }
                    }
                }
            }
        }

        if (choiceMode == ListView.CHOICE_MODE_SINGLE) {
            mListView.setOnItemClickListener(this);
        }

        mListView.setTextFilterEnabled(true);

        mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setQueryHint("搜索");
        mSearchView.setFocusable(false);
	}

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mListView.getChoiceMode() == ListView.CHOICE_MODE_MULTIPLE) {
                acceptSelection();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mListView.getChoiceMode() == ListView.CHOICE_MODE_MULTIPLE) {
            this.getMenuInflater().inflate(R.menu.menu_select_persons, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    private void acceptSelection() {
        Intent outIntent = new Intent();
        SparseBooleanArray sels = mListView.getCheckedItemPositions();
        String[] outArr = new String[sels.size()];

        if (sels.size() <= 0) {
            outIntent.putExtra("selected_items", outArr);
            setResult(Activity.RESULT_CANCELED, outIntent);
            this.finish();
            return;
        }

        for (int i = 0; i < sels.size(); i++)
        {
            outArr[i] = mListView.getAdapter().getItem(sels.keyAt(i)).toString();
        }
        outIntent.putExtra("selected_items", outArr);
        setResult(RESULT_OK, outIntent);
        this.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_accept) {
            acceptSelection();
            this.finish();
        }
        else if (item.getItemId() == R.id.action_select_all) {
            for (int i = 0; i < mListView.getCount(); i++)
                mListView.setItemChecked(i, true);
        }
        else if (item.getItemId() == R.id.action_unselect_all) {
            for (int i = 0; i < mListView.getCount(); i++)
                mListView.setItemChecked(i, false);
        }
        else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String str = mListView.getAdapter().getItem(position).toString();
        Intent outIntent = new Intent();
        outIntent.putExtra("selected_item", str);
        setResult(RESULT_OK, outIntent);
        this.finish();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (TextUtils.isEmpty(newText)) {
            mListView.clearTextFilter();
        } else {
            mListView.setFilterText(newText.toString());
        }
        return true;
    }
}
