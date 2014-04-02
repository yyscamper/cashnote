package com.yyscamper.cashnote;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;


public class PersonManagerActivity extends Activity {
    final int REQ_CODE_NEW_PERSON = 1;
    FragmentPersons mFragPerson = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_persons_list);
        mFragPerson = new FragmentPersons();
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, mFragPerson)
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.persons_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_new_person) {
            Intent intent = new Intent(this, PersonDetailActivity.class);
            intent.putExtra("mode", PersonDetailActivity.MODE_NEW);
            startActivityForResult(intent, REQ_CODE_NEW_PERSON);
            return true;
        }
        else if (id == R.id.action_edit_person) {
            return  true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_NEW_PERSON) {
            if (resultCode != RESULT_OK ) {
                return;
            }
            mFragPerson.refreshData();
        }
    }
}
