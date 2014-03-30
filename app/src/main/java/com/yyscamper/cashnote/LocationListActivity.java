package com.yyscamper.cashnote;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.yyscamper.cashnote.PayType.PayLocation;
import com.yyscamper.cashnote.PayType.PayLocationManager;
import com.yyscamper.cashnote.PayType.StorageSelector;
import com.yyscamper.cashnote.Util.Util;


public class LocationListActivity extends Activity {
    private ListView mViewLocations;
    private PayLocation mSelectedLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_list);
        mViewLocations = (ListView)findViewById(R.id.listViewLocations);
        LocationListItemAdapter adapter = new LocationListItemAdapter(this);
        mViewLocations.setAdapter(adapter);

        mViewLocations.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                mSelectedLocation = (PayLocation)mViewLocations.getAdapter().getItem(position);
                return false;
            }
        });

        this.registerForContextMenu(mViewLocations);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.location_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_add_location) {
            addHandle();
            return true;
        }
        else if (id == R.id.action_edit_location) {
            return true;
        }
        else if (id == R.id.action_remove_location) {
            deleteHandle();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addHandle()
    {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(getString(R.string.add_location));
        final EditText input = new EditText(this);
        String inputLocationName = null;
        dialog.setView(input);

        dialog.setPositiveButton(getString(R.string.txtOK), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String str = input.getText().toString().trim();
                if (str.length() == 0) {
                    Util.ShowErrorDialog(LocationListActivity.this, "The location name cannot be empty!", "Error");
                    return;
                }
                else if (PayLocationManager.contains(str)) {
                    Util.ShowErrorDialog(LocationListActivity.this, "The location has already existed!", "Error");
                    return;
                }
                PayLocationManager.add(new PayLocation(str), StorageSelector.ALL);
                ((LocationListItemAdapter)(mViewLocations.getAdapter())).refreshData();
            }
        });
        dialog.setNegativeButton(getString(R.string.txtCancle), null);
        dialog.show();
    }

    private void deleteHandle()
    {
        if (mSelectedLocation == null) {
            return;
        }
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(getString(R.string.add_location));
        dialog.setMessage("Are you sure want to delete the location \"" + mSelectedLocation.Name + "\"?");

        dialog.setPositiveButton(getString(R.string.txtOK), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (PayLocationManager.remove(mSelectedLocation.Name, StorageSelector.ALL)) {
                    ((LocationListItemAdapter)(mViewLocations.getAdapter())).refreshData();
                }
            }
        });
        dialog.setNegativeButton(getString(R.string.txtCancle), null);
        dialog.show();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v == mViewLocations)
        {
            menu.setHeaderTitle("Location Operation");
            menu.add(0, 1, Menu.NONE, "Edit");
            menu.add(0, 2, Menu.NONE, "Delete");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        switch (item.getItemId())
        {
            case 1: //Edit pay entry
                return true;
            case 2: //Delete pay entry
                deleteHandle();
                return true;
            default:
                return super.onContextItemSelected(item);

        }
    }
}
