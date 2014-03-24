package com.yyscamper.cashnote;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.yyscamper.cashnote.PayType.*;

import com.yyscamper.cashnote.PayType.PayEntryManager;

/**
 * A placeholder fragment containing a simple view.
 */
public class FragmentPayHistory extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static FragmentPayHistory mInstance;
    private ListView mViewHistoryList;
    private int mSelectedPosition;
    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static FragmentPayHistory getInstance() {
        if (mInstance == null)
            mInstance = new FragmentPayHistory();
        return mInstance;
    }

    private FragmentPayHistory() {

    }

    public void setSectionNumber(int sectionNumber) {
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        this.setArguments(args);
    }

    private void refreshData() {
        if (mViewHistoryList == null)
            return;
        PayHistoryListItemAdapter adapter = new PayHistoryListItemAdapter(getActivity(), PayEntryManager.getAll());
        mViewHistoryList.setAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pay_history, container, false);
        mViewHistoryList = (ListView)rootView.findViewById(R.id.listViewPayHistory);
        refreshData();
        mViewHistoryList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                mSelectedPosition = i;
                return false;
            }
        });
        this.registerForContextMenu(mViewHistoryList);
        return rootView;
    }

        @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);

        if (v == mViewHistoryList)
        {
            menu.setHeaderTitle("Pay Entry Operation");
            menu.add(0, 1, Menu.NONE, "Edit");
            menu.add(0, 2, Menu.NONE, "Delete");
        }
    }

    private void showDeletePayEntryConfirmDialog(String message, String title)
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message);
        builder.setTitle(title);
        builder.setPositiveButton("Yes", new AlertDialog.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                getActivity().setResult(getActivity().RESULT_OK);
                if (mSelectedPosition < 0) {
                    return;
                }
                else
                {
                    AccountBook.removePay(mSelectedPosition);
                    refreshData();
                }

                //dialog.dismiss();
            }
        });
        builder.setNegativeButton("No", new AlertDialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getActivity().setResult(getActivity().RESULT_CANCELED);
                //dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        switch (item.getItemId())
        {
            case 1: //Edit pay entry
                return true;
            case 2: //Delete pay entry
                showDeletePayEntryConfirmDialog("Are you sure want to delete the selected entry?", "Delete Pay Confirm");
                return true;
            default:
                return super.onContextItemSelected(item);

        }
    }

    public void notifyNewEntryAdded(PayEntry entry) {
        refreshData();
    }
}
