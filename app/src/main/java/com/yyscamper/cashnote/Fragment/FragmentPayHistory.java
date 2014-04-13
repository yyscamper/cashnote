package com.yyscamper.cashnote.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.yyscamper.cashnote.Adapter.ListItemHistoryAdapter;
import com.yyscamper.cashnote.DetailHistoryActivity;
import com.yyscamper.cashnote.PayType.*;

import com.yyscamper.cashnote.R;
import com.yyscamper.cashnote.Storage.CacheStorage;
import com.yyscamper.cashnote.StorageManager;

/**
 * A placeholder fragment containing a simple view.
 */
public class FragmentPayHistory extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final int REQ_CODE_HISTORY_DETAIL = 1;
    private static final int REQ_CODE_REMOVE_HISTORY = 2;

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static FragmentPayHistory mInstance;
    private ListView mViewHistoryList;
    private int mSelectedPosition;
    ListItemHistoryAdapter mAdapter;
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE_HISTORY_DETAIL) {
            if (resultCode == Activity.RESULT_OK) {
                mAdapter = new ListItemHistoryAdapter(getActivity(), CacheStorage.getInstance().getAllHistoriesInList(PayComparator.HistoryPayTimeDesc));
                mViewHistoryList.setAdapter(mAdapter);
            }
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

    private void refreshData() {
        if (mViewHistoryList == null)
            return;
        mAdapter = new ListItemHistoryAdapter(getActivity(), CacheStorage.getInstance().getAllHistoriesInList(PayComparator.HistoryPayTimeDesc));
        mViewHistoryList.setAdapter(mAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.single_list_view, container, false);
        mViewHistoryList = (ListView)rootView.findViewById(R.id.listView);
        refreshData();
        mViewHistoryList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                mSelectedPosition = i;
                return false;
            }
        });

        mViewHistoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PayHistory entry = (PayHistory)mAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), DetailHistoryActivity.class);
                intent.putExtra(DetailHistoryActivity.KEY_MODE, FragmentPayHistoryDetail.MODE_VIEW);
                intent.putExtra(DetailHistoryActivity.KEY_UUID, entry.getKey());
                startActivityForResult(intent, REQ_CODE_HISTORY_DETAIL);
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
        if (mSelectedPosition < 0 || mSelectedPosition >= mViewHistoryList.getAdapter().getCount()) {
            return;
        }
        final PayHistory history = (PayHistory)mViewHistoryList.getAdapter().getItem(mSelectedPosition);
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
                    StorageManager.getInstance().removePay(getActivity(), history.getKey());
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

    public void notifyNewEntryAdded(PayHistory entry) {
        refreshData();
    }
}
