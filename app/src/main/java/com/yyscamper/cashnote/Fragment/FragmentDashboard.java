package com.yyscamper.cashnote.Fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.yyscamper.cashnote.Adapter.DoubleColumnRowAdapter;
import com.yyscamper.cashnote.Enum.DataType;
import com.yyscamper.cashnote.PayType.PayComparator;
import com.yyscamper.cashnote.PayType.PayHistory;
import com.yyscamper.cashnote.PayType.PayLocation;
import com.yyscamper.cashnote.PayType.PayPerson;
import com.yyscamper.cashnote.R;
import com.yyscamper.cashnote.Storage.CacheStorage;
import com.yyscamper.cashnote.Storage.StorageObject;
import com.yyscamper.cashnote.Util.ValuePair;

import java.util.ArrayList;

import javax.security.auth.callback.CallbackHandler;

/**
 * A placeholder fragment containing a simple view.
 */
public class FragmentDashboard extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private ListView mListViewTopDebt;
    private ListView mListViewTopSurplus;
    private ListView mListViewTopLocation;
    private final static int ROW_HEIGHT = 32;
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


    public void refreshTopDebtSurplus() {
        PayPerson[] sortedPersons = CacheStorage.getInstance().getAllPersons(PayComparator.PersonBalanceAsc);
        ValuePair[] topDebt = new  ValuePair[3];
        ValuePair[] topSurplus = new ValuePair[3];

        for (int i = 0; i < 3 && i < sortedPersons.length; i++) {
            PayPerson p = sortedPersons[i];
            topDebt[i] = new ValuePair(p.getName(), p.getBalance());
        }

        int j = 0;
        for (int i = sortedPersons.length-1; i >= 0 && j < 3; i--) {
            PayPerson p = sortedPersons[i];
            topSurplus[j++] = new ValuePair(p.getName(), p.getBalance());
        }

        DoubleColumnRowAdapter debtAdapter = new DoubleColumnRowAdapter(getActivity(), topDebt, 1, 1, ROW_HEIGHT);
        mListViewTopDebt.setAdapter(debtAdapter);

        DoubleColumnRowAdapter surplusAdapter = new DoubleColumnRowAdapter(getActivity(), topSurplus, 1, 1, ROW_HEIGHT);
        mListViewTopSurplus.setAdapter(surplusAdapter);
    }

    public void refreshTopLocation() {
        PayLocation[] sortedLocations = CacheStorage.getInstance().getAllLocations(PayComparator.LocationAttendCountDesc);
        ValuePair[] topLocation = new ValuePair[3];
        for (int i = 0; i < 3 && i < sortedLocations.length; i++) {
            PayLocation p = sortedLocations[i];
            topLocation[i] = new ValuePair(p.getName(), p.getAttendCount());
        }
        DoubleColumnRowAdapter locationAdapter = new DoubleColumnRowAdapter(getActivity(), topLocation, 1, 1, ROW_HEIGHT);
        mListViewTopLocation.setAdapter(locationAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);
        mListViewTopDebt = (ListView)rootView.findViewById(R.id.listViewTopDebet);
        mListViewTopSurplus = (ListView)rootView.findViewById(R.id.listViewTopSurplus);
        mListViewTopLocation = (ListView)rootView.findViewById(R.id.listViewTopLocation);
        refreshTopDebtSurplus();
        refreshTopLocation();
        return rootView;
    }
}