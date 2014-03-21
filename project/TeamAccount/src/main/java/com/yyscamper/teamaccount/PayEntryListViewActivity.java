package com.yyscamper.teamaccount;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import com.yyscamper.teamaccount.PayType.*;

public class PayEntryListViewActivity extends ActionBarActivity {

    private int mSelectedPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_entry_list_view);

        refreshAllViewData();

        ListView payListView = (ListView)findViewById(R.id.listViewPayEntryList);
        this.registerForContextMenu(payListView);

        payListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                mSelectedPosition = i;
                return false;
            }
        });
    }

    private void refreshAllViewData()
    {
        TextView totalPayCountView = (TextView)findViewById(R.id.textViewTotalPayCount);
        TextView totalMoneyView = (TextView)findViewById(R.id.textViewTotalPayMoney);

        totalPayCountView.setText("Total Count:" + String.valueOf(PayEntryManager.size()));
        totalMoneyView.setText("Total Money:" + String.format("%.1f", AccountBook.getTotalMoney()));

        ListView payListView = (ListView)findViewById(R.id.listViewPayEntryList);
        PayEntryViewListItemAdapter payListViewAdapter = new PayEntryViewListItemAdapter(this, PayEntryManager.getAll());
        payListView.setAdapter(payListViewAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.pay_entry_list_view, menu);
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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);

        if (v == findViewById(R.id.listViewPayEntryList))
        {
            menu.setHeaderTitle("Pay Entry Operation");
            menu.add(0, 1, Menu.NONE, "Edit");
            menu.add(0, 2, Menu.NONE, "Delete");
        }
    }

    private void showDeletePayEntryConfirmDialog(String message, String title)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setTitle(title);
        builder.setPositiveButton("Yes", new AlertDialog.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                setResult(RESULT_OK);

                ListView payListView = (ListView)findViewById(R.id.listViewPayEntryList);
                if (mSelectedPosition < 0) {
                    return;
                }
                else
                {
                    AccountBook.removePay(mSelectedPosition);
                    refreshAllViewData();
                }

                //dialog.dismiss();
            }
        });
        builder.setNegativeButton("No", new AlertDialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setResult(RESULT_CANCELED);
                //dialog.dismiss();
            }
        });
        builder.create().show();
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
                showDeletePayEntryConfirmDialog("Are you sure want to delete the selected entry?", "Delete Pay Confirm");
                return true;
            default:
                return super.onContextItemSelected(item);

        }
    }

}
