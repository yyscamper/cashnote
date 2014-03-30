package com.yyscamper.cashnote;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import com.yyscamper.cashnote.PayType.LocationGroup;
import com.yyscamper.cashnote.PayType.LocationGroupManager;
import com.yyscamper.cashnote.PayType.PayLocation;
import com.yyscamper.cashnote.PayType.PayLocationManager;
import com.yyscamper.cashnote.PayType.PayPerson;
import com.yyscamper.cashnote.PayType.PayPersonManager;
import com.yyscamper.cashnote.Util.LeftRightValue;
import com.yyscamper.cashnote.Util.Util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * A placeholder fragment containing a simple view.
 */
public class FragmentRandomSelectLocation extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private TextView mViewRandomLocationName;
    private Button mButtonStartStop;
    private Button mButtonChooseGroup;
    private TextView mViewGroupName;
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final int REQ_CODE_SELECT_GROUP = 1;
    private static FragmentRandomSelectLocation mInstance = null;
    private RandomLocationTask mBackgroundTask;
    private Runnable mThread;
    private boolean mThreadStopFlag = false;
    private LocationGroup mCurrLocationGroup;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static FragmentRandomSelectLocation getInstance() {
        if (mInstance == null)
            mInstance = new FragmentRandomSelectLocation();
        return mInstance;
    }

    private FragmentRandomSelectLocation() {

    }

    private void updateGroupNameHeader() {
        if (mCurrLocationGroup == null) {
            mViewGroupName.setText("全部餐馆 (" + PayLocationManager.size() + ")");
        }
        else {
            mViewGroupName.setText(mCurrLocationGroup.Name + " (" + mCurrLocationGroup.getChildrenCount() + ")");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE_SELECT_GROUP) {
            if (resultCode != Activity.RESULT_OK) {
                return;
            }
            String groupName = data.getStringExtra("selected_item");
            mCurrLocationGroup = LocationGroupManager.get(groupName);
            updateGroupNameHeader();
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void setSectionNumber(int sectionNumber) {
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        this.setArguments(args);
    }

    public LeftRightValue[] convertTopLocationChildren(PayLocation[] locs) {
        ArrayList<LeftRightValue> list = new ArrayList<LeftRightValue>();
        for (PayLocation loc : locs) {
            if (loc != null) {
                list.add(new LeftRightValue(loc.Name, loc.AttendCount));
            }
        }
        LeftRightValue[] arr = new LeftRightValue[list.size()];
        list.toArray(arr);
        return arr;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_random_select_location, container, false);
        mViewRandomLocationName = (TextView)rootView.findViewById(R.id.textViewRandomLocationName);
        mButtonStartStop = (Button)rootView.findViewById(R.id.buttonStartStop);
        mButtonChooseGroup = (Button)rootView.findViewById(R.id.buttonChooseGroup);
        mViewGroupName = (TextView)rootView.findViewById(R.id.textViewGroupName);

        updateGroupNameHeader();

        mButtonStartStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBackgroundTask != null && !mBackgroundTask.isCancelled()) {
                    mBackgroundTask.cancel(true);
                    mButtonStartStop.setText("Start");
                }
                else {
                    mBackgroundTask = new RandomLocationTask();
                    mButtonStartStop.setText("Stop");
                    if (mCurrLocationGroup != null) {
                        mBackgroundTask.execute(mCurrLocationGroup.getChildrenArray());
                    }
                    else {
                        mBackgroundTask.execute(PayLocationManager.getAllNames());
                    }
                }

            }
        });

        mButtonChooseGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LocationGroupActivity.class);
                intent.putExtra("selection_mode", true);
                startActivityForResult(intent, REQ_CODE_SELECT_GROUP);
            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    public class RandomLocationTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            publishProgress("");
            while (true) {
                for (String p : params) {
                    publishProgress(p);
                    try {
                        Thread.sleep(50);
                    }
                    catch (InterruptedException e) {
                        return p;
                    }
                }
            }
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            mViewRandomLocationName.setText(values[0]);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }
}