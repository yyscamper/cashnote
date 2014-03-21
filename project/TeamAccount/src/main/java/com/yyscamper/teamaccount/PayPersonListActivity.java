package com.yyscamper.teamaccount;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.*;
import com.yyscamper.teamaccount.PayType.PayPerson;
import com.yyscamper.teamaccount.PayType.PayPersonManager;

public class PayPersonListActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_person_list);

        performSort(PayPersonManager.SORT_MONEY_ASCENDING);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.pay_person_list, menu);
        return true;
    }

    private void performSort(int sortType)
    {
        ListView listPersonsView = (ListView)findViewById(R.id.listViewPersons);
        PayPersonViewListItemAdapter listPersonsViewAdapter = new PayPersonViewListItemAdapter(this, PayPersonManager.getAllPersons(sortType), sortType);
        listPersonsView.setAdapter(listPersonsViewAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        int sortType;
        if (id == R.id.menu_sort_by_money_ascending) {
            sortType = PayPersonManager.SORT_MONEY_ASCENDING;
            performSort(sortType);
            return true;
        }
        else if (id == R.id.menu_sort_by_money_descending) {
            sortType = PayPersonManager.SORT_MONEY_DESCENDING;
            performSort(sortType);
            return true;
        }
        else if (id == R.id.menu_sort_by_name_ascending) {
            sortType = PayPersonManager.SORT_NAME_ASCENDING;
            performSort(sortType);
            return true;
        }
        else if (id == R.id.menu_sort_by_name_descending) {
            sortType = PayPersonManager.SORT_NAME_DESCENDING;
            performSort(sortType);
            return true;
        }
        else if (id == R.id.menu_sort_by_pay_count_ascending)
        {
            sortType = PayPersonManager.SORT_PAY_COUNT_ASCENDING;
            performSort(sortType);
            return true;
        }
        else if (id == R.id.menu_sort_by_pay_count_descending)
        {
            sortType = PayPersonManager.SORT_PAY_COUNT_DESCENDING;
            performSort(sortType);
            return true;
        }
        else if (id == R.id.menu_sort_by_attend_count_ascending)
        {
            sortType = PayPersonManager.SORT_ATTEND_COUNT_ASCENDING;
            performSort(sortType);
            return true;
        }
        else if (id == R.id.menu_sort_by_attend_count_descending)
        {
            sortType = PayPersonManager.SORT_ATTEND_COUNT_DESCENDING;
            performSort(sortType);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
