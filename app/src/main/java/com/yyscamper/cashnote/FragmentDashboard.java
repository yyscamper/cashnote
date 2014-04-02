package com.yyscamper.cashnote;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import com.yyscamper.cashnote.PayType.PayLocation;
import com.yyscamper.cashnote.PayType.PayLocationManager;
import com.yyscamper.cashnote.PayType.PayPersonManager;
import com.yyscamper.cashnote.Util.ValuePair;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class FragmentDashboard extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private TextView mViewNextPayer;
    private ListView mListViewTopDebt;
    private ListView mListViewTopSurplus;
    private ListView mListViewTopLocation;

    private static FragmentDashboard mInstance;
    private static String[] ExpandGroupNames = new String[] {"Top 3 Deficit Persons", "Top 3 Surplus Persons", "Top 3 Locations"};
    private static final String ARG_SECTION_NUMBER = "section_number";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static FragmentDashboard getInstance() {
        if (mInstance == null)
            mInstance = new FragmentDashboard();
        return mInstance;
    }

    private FragmentDashboard() {

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

    public void refreshTopDebt() {
        ValuePair[] topDebt = PayPersonManager.getTopDebetPersons();
        SimpleRowDoubleColumnListAdapter debtAdapter = new SimpleRowDoubleColumnListAdapter(getActivity(), topDebt);
        mListViewTopDebt.setAdapter(debtAdapter);
    }

    public void refreshTopSurplus() {
        ValuePair[] topSurplus = PayPersonManager.getTopSurplusPersons();
        SimpleRowDoubleColumnListAdapter surplusAdapter = new SimpleRowDoubleColumnListAdapter(getActivity(), topSurplus);
        mListViewTopSurplus.setAdapter(surplusAdapter);
    }

    public void refreshTopLocation() {
        ValuePair[] topLocation = convertTopLocationChildren(PayLocationManager.getTopLocatons());
        SimpleRowDoubleColumnListAdapter locationAdapter = new SimpleRowDoubleColumnListAdapter(getActivity(), topLocation);
        mListViewTopLocation.setAdapter(locationAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);
        mListViewTopDebt = (ListView)rootView.findViewById(R.id.listViewTopDebet);
        mListViewTopSurplus = (ListView)rootView.findViewById(R.id.listViewTopSurplus);
        mListViewTopLocation = (ListView)rootView.findViewById(R.id.listViewTopLocation);
        refreshTopDebt();
        refreshTopLocation();
        refreshTopSurplus();
        return rootView;
    }
}