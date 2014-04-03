package com.yyscamper.cashnote;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.yyscamper.cashnote.PayType.PayAttendInfo;
import com.yyscamper.cashnote.PayType.PayPersonManager;


public class AttendInfoInputActivity extends Activity {
    ListView mListView;
    AttendInfoInputAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attend_info_input);
        mListView = (ListView)findViewById(R.id.listView);
        mAdapter = new AttendInfoInputAdapter(this, PayPersonManager.getAllPersons(), PayPersonManager.SORT_NAME_ASCENDING);
        mListView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.attend_info_input, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_accept) {
            Intent intent = new Intent();
            intent.putExtra("results", mAdapter.getSelectedAtttendInfo());
            setResult(RESULT_OK, intent);
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
