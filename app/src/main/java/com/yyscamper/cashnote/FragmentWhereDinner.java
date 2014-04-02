package com.yyscamper.cashnote;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.yyscamper.cashnote.PayType.LocationGroup;
import com.yyscamper.cashnote.PayType.LocationGroupManager;
import com.yyscamper.cashnote.PayType.PayLocation;
import com.yyscamper.cashnote.PayType.PayLocationManager;
import com.yyscamper.cashnote.Util.ValuePair;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class FragmentWhereDinner extends Fragment implements View.OnClickListener {
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
    private static FragmentWhereDinner mInstance = null;
    private RandomLocationTask mBackgroundTask;
    private Runnable mThread;
    private boolean mThreadStopFlag = false;
    private LocationGroup mCurrLocationGroup;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static FragmentWhereDinner getInstance() {
        if (mInstance == null)
            mInstance = new FragmentWhereDinner();
        return mInstance;
    }

    private FragmentWhereDinner() {

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

    public ValuePair[] convertTopLocationChildren(PayLocation[] locs) {
        ArrayList<ValuePair> list = new ArrayList<ValuePair>();
        for (PayLocation loc : locs) {
            if (loc != null) {
                list.add(new ValuePair(loc.Name, loc.AttendCount));
            }
        }
        ValuePair[] arr = new ValuePair[list.size()];
        list.toArray(arr);
        return arr;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_where_dinner, container, false);
        mViewRandomLocationName = (TextView)rootView.findViewById(R.id.textViewRandomLocationName);
        mButtonStartStop = (Button)rootView.findViewById(R.id.buttonStartStop);
        mButtonChooseGroup = (Button)rootView.findViewById(R.id.buttonChooseGroup);
        mViewGroupName = (TextView)rootView.findViewById(R.id.textViewGroupName);

        updateGroupNameHeader();

        mButtonStartStop.setOnClickListener(this);
        mViewRandomLocationName.setOnClickListener(this);

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

    @Override
    public void onClick(View v) {
        if (mBackgroundTask != null && !mBackgroundTask.isCancelled()) {
            mBackgroundTask.cancel(true);
            mButtonStartStop.setText("开始");
            mButtonChooseGroup.setEnabled(true);
        }
        else {
            mBackgroundTask = new RandomLocationTask();
            mButtonStartStop.setText("停止");
            mButtonChooseGroup.setEnabled(false);
            if (mCurrLocationGroup != null) {
                mBackgroundTask.execute(mCurrLocationGroup.getChildrenArray());
            }
            else {
                mBackgroundTask.execute(PayLocationManager.getAllNames());
            }
        }
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