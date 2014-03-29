package com.yyscamper.cashnote;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.yyscamper.cashnote.PayType.PayPersonManager;

/**
 * A placeholder fragment containing a simple view.
 */
public class FragmentPersons extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private TextView mViewTotalPayCount;
    private TextView mViewTotalMoney;
    private ListView mViewPersonList;
    private int mSortType = PayPersonManager.SORT_MONEY_ASCENDING;
    private static FragmentPersons mInstance;

    private static final String ARG_SECTION_NUMBER = "section_number";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static FragmentPersons getInstance() {
        if (mInstance == null)
            mInstance = new FragmentPersons();
        return mInstance;
    }

    private FragmentPersons() {

    }

    public void setSectionNumber(int sectionNumber) {
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        this.setArguments(args);
    }

    private void refreshPersonsListData() {
        if (mViewPersonList == null)
            return;

        PersonListItemAdapter adapter = new PersonListItemAdapter(this.getActivity(), PayPersonManager.getAllPersons(mSortType), mSortType);
        mViewPersonList.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_persons, container, false);
        mViewPersonList = (ListView)rootView.findViewById(R.id.listViewPersons);
        refreshPersonsListData();
        return rootView;
    }
}