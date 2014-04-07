package com.yyscamper.cashnote;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.yyscamper.cashnote.PayType.PayHistory;
import com.yyscamper.cashnote.Storage.CloudStorage;
import com.yyscamper.cashnote.Storage.SyncManager;

import java.util.ArrayList;


public class SyncActivity extends Activity {

    private Button mButtonSyncHistory;
    private CloudStorage mCloudStorage;

    public void setCloudStorage(CloudStorage s) {
        mCloudStorage = s;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);

        mButtonSyncHistory = (Button)findViewById(R.id.buttonSyncHistory);
        mButtonSyncHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCloudStorage != null) {
                    SyncManager.syncHistory();
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sync, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
