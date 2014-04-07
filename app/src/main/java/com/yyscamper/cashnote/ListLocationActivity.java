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

import com.yyscamper.cashnote.Adapter.ListItemLocationAdapter;
import com.yyscamper.cashnote.PayType.PayLocation;
import com.yyscamper.cashnote.PayType.PayLocationManager;
import com.yyscamper.cashnote.PayType.StorageSelector;
import com.yyscamper.cashnote.Util.Util;


public class ListLocationActivity extends Activity {
    private ListView mViewLocations;
    private PayLocation mSelectedLocation;
    private ListItemLocationAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_list_view);
        mViewLocations = (ListView)findViewById(R.id.listView);
        mAdapter = new ListItemLocationAdapter(this);
        mViewLocations.setAdapter(mAdapter);

        mViewLocations.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                mSelectedLocation = (PayLocation)mAdapter.getItem(position);
                return false;
            }
        });

        this.registerForContextMenu(mViewLocations);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.single_new_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_new) {
            addHandle();
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
                    Util.showErrorDialog(ListLocationActivity.this, "地点不能为空!");
                    return;
                }
                else if (PayLocationManager.contains(str)) {
                    Util.showErrorDialog(ListLocationActivity.this, "你输入的地点已经存在，请重新输入");
                    return;
                }
                PayLocationManager.add(new PayLocation(str), StorageSelector.ALL);
                mAdapter.refreshData();
            }
        });
        dialog.setNegativeButton(getString(R.string.txtCancle), null);
        dialog.show();
    }

    private void editHandle() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(getString(R.string.add_location));
        final EditText input = new EditText(this);
        input.setText(mSelectedLocation.Name);
        dialog.setView(input);

        dialog.setPositiveButton(getString(R.string.txtOK), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String str = input.getText().toString().trim();
                if (str.length() == 0) {
                    Util.showErrorDialog(ListLocationActivity.this, "地点不能为空!");
                    return;
                }
                else if (!str.equalsIgnoreCase(mSelectedLocation.Name) && PayLocationManager.contains(str)) {
                    Util.showErrorDialog(ListLocationActivity.this, "你输入的地点已经存在，请重新输入");
                    return;
                }
                PayLocation newLoc = new PayLocation();
                newLoc.copyFrom(mSelectedLocation);
                newLoc.Name= str;
                PayLocationManager.update(mSelectedLocation.Name, newLoc, StorageSelector.ALL);
                mAdapter.refreshData();
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
        dialog.setTitle(mSelectedLocation.Name);
        dialog.setMessage("你确定要删除该地点吗？");

        dialog.setPositiveButton(getString(R.string.txtOK), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (PayLocationManager.remove(mSelectedLocation.Name, StorageSelector.ALL)) {
                    ((ListItemLocationAdapter)(mViewLocations.getAdapter())).refreshData();
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
            menu.setHeaderTitle(mSelectedLocation.Name);
            menu.add(0, 1, Menu.NONE, "编辑");
            menu.add(0, 2, Menu.NONE, "删除");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        switch (item.getItemId())
        {
            case 1: //Edit pay entry
                editHandle();
                return true;
            case 2: //Delete pay entry
                deleteHandle();
                return true;
            default:
                return super.onContextItemSelected(item);

        }
    }
}
