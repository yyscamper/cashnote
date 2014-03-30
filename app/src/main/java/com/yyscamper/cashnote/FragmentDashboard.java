package com.yyscamper.cashnote;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

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
public class FragmentDashboard extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private TextView mViewNextPayer;
        private ExpandableListView mViewExpandResult;
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

    private void refreshData() {
        LeftRightValue[] topDeficits = PayPersonManager.getTopDebetPersons();
        LeftRightValue[] topSurpluss = PayPersonManager.getTopSurplusPersons();
        LeftRightValue[] topLocatons = convertTopLocationChildren(PayLocationManager.getTopLocatons());
        ArrayList<LeftRightValue[]> listChildren = new ArrayList<LeftRightValue[]>();
        listChildren.add(topDeficits);
        listChildren.add(topSurpluss);
        listChildren.add(topLocatons);
        DashboardExpandableAdapter adapter = new DashboardExpandableAdapter(getActivity(), ExpandGroupNames, listChildren);

        mViewExpandResult.setAdapter(adapter);
        for (int i = 0; i < ExpandGroupNames.length; i++) {
            mViewExpandResult.expandGroup(i);
        }

        LeftRightValue mostDebet = PayPersonManager.getMostDebetPerson();
        if (mostDebet != null)
            mViewNextPayer.setText("Next Payer is:" + mostDebet.LeftValue.toString() + "(" + mostDebet.RightValue.toString() + ")");
        else
            mViewNextPayer.setText("Next Payer is ???");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);
        mViewNextPayer = (TextView)rootView.findViewById(R.id.textViewNextPayer);
        mViewExpandResult = (ExpandableListView)rootView.findViewById(R.id.expandableListViewResult);
        refreshData();
        return rootView;
    }
}