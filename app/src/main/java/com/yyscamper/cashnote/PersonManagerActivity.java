package com.yyscamper.cashnote;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.yyscamper.cashnote.Fragment.FragmentPersons;


public class PersonManagerActivity extends Activity {
    final int REQ_CODE_NEW_PERSON = 1;

    FragmentPersons mFragPerson = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_single_container);
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
            startPersonDetailByMode(DetailPersonActivity.MODE_NEW, null);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startPersonDetailByMode(int mode, String name) {
        Intent intent = new Intent(this, DetailPersonActivity.class);
        intent.putExtra(DetailPersonActivity.KEY_MODE, mode);
        if (mode != DetailPersonActivity.MODE_NEW) {
            intent.putExtra(DetailPersonActivity.KEY_NAME, name);
        }
        startActivityForResult(intent, REQ_CODE_NEW_PERSON);
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
