package com.yyscamper.cashnote;

import java.util.Locale;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Looper;
import android.support.v13.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.baidu.frontia.FrontiaAccount;
import com.baidu.frontia.FrontiaApplication;
import com.yyscamper.cashnote.Fragment.FragmentDashboard;
import com.yyscamper.cashnote.Fragment.FragmentPayHistoryDetail;
import com.yyscamper.cashnote.Fragment.FragmentWhereDinner;
import com.yyscamper.cashnote.Storage.CloudStorage;
import com.yyscamper.cashnote.Storage.SqliteStorage;
import com.yyscamper.cashnote.Util.Util;

public class MainActivity extends Activity implements ActionBar.TabListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;
    SqliteStorage mSqliteStorage;
    CloudStorage mCloudStorage;
    String mDatabaseName = "cashnote_test";

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    private FrontiaAccount mUser;

    private void handleCreate() {
        setContentView(R.layout.activity_main);

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, addToCache a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

        StorageManager.getInstance().init(this);
    }

    private void handleAuthor() {
        final String TAG = "BaidACL";
        FrontiaApplication.
                initFrontiaApplication(getApplicationContext());

        handleCreate();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleAuthor();
    }

    @Override
    protected void onDestroy() {
        StorageManager.getInstance().close();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_locations) {
            Intent intent = new Intent(this, ListLocationActivity.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.action_persons) {
            Intent intent = new Intent(this, PersonManagerActivity.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.action_history) {
            Intent intent = new Intent(this, ListHistoryActivity.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.action_location_groups) {
            Intent intent = new Intent(this, LocationGroupActivity.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.action_debug_clear_db) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    StorageManager.getInstance().debug_clear_all(MainActivity.this);
                }
            }).start();
            StorageManager.getInstance().debug_clear_all(this);
            return true;
        }
        else if (id == R.id.action_debug_init_db) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Looper.prepare();
                    final ProgressDialog progressDialog = ProgressDialog.show(MainActivity.this,
                            "", "Selecting Radio Station", true);
                    StorageManager.getInstance().debug_insert(MainActivity.this);
                    progressDialog.dismiss();
                }
            }).start();
            return true;
        }
        else if (id == R.id.action_sync) {
            StorageManager.getInstance().loadAllFromCloud(this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        int callId = data.getIntExtra("call_id", -1);
        if (callId != -1) {
            Fragment f = this.getFragmentManager().findFragmentById(callId);
            f.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a FragmentPayHistoryDetail (defined as a static inner class below).
            switch (position)
            {
                case 0:
                    FragmentPayHistoryDetail f0 = new FragmentPayHistoryDetail(FragmentPayHistoryDetail.MODE_NEW, null);
                    return f0;
                case 1:
                    FragmentWhereDinner f1 = FragmentWhereDinner.getInstance();
                    return f1;
                case 2:
                    FragmentDashboard f2 = FragmentDashboard.getInstance();
                    return f2;
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.txtNewPayEntry).toUpperCase(l);
                case 1:
                    return "哪儿吃".toUpperCase(l);
                case 2:
                    return "统计".toUpperCase(l);
            }
            return null;
        }
    }
}
