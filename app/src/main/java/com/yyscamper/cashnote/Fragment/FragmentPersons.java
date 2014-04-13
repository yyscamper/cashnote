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

import com.yyscamper.cashnote.Adapter.ListItemPersonAdapter;
import com.yyscamper.cashnote.DetailPersonActivity;
import com.yyscamper.cashnote.Enum.DataType;
import com.yyscamper.cashnote.Interface.GeneralResultCode;
import com.yyscamper.cashnote.PayType.PayComparator;
import com.yyscamper.cashnote.PayType.PayPerson;
import com.yyscamper.cashnote.PayType.StorageSelector;
import com.yyscamper.cashnote.R;
import com.yyscamper.cashnote.Storage.CacheStorage;
import com.yyscamper.cashnote.StorageManager;

/**
 * A placeholder fragment containing a simple view.
 */
public class FragmentPersons extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private ListView mViewPersonList;
    private int mSelectedPosition;
    private ListItemPersonAdapter mAdapter;
    private static final int REQ_CODE_PERSON_DETAIL = 10;
    public FragmentPersons() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_PERSON_DETAIL) {
            if (resultCode == Activity.RESULT_OK) {
                refreshData();
            }
        }
    }

    public void refreshData() {
        if (mViewPersonList == null)
            return;

        mAdapter = new ListItemPersonAdapter(this.getActivity(), CacheStorage.getInstance().getAllPersons(PayComparator.KeyAsc));
        mViewPersonList.setAdapter(mAdapter);
    }

    private PayPerson getSelectedPerson() {
        return (PayPerson)mAdapter.getItem(mSelectedPosition);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.single_list_view, container, false);
        mViewPersonList = (ListView)rootView.findViewById(R.id.listView);
        mViewPersonList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                mSelectedPosition = position;
                return false;
            }
        });

        mViewPersonList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PayPerson person = (PayPerson)mAdapter.getItem(position);
                startPersonDetailByMode(DetailPersonActivity.MODE_VIEW, person.getName());
            }
        });
        refreshData();
        this.registerForContextMenu(mViewPersonList);
        return rootView;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);

        if (v == mViewPersonList)
        {
            PayPerson person = getSelectedPerson();
            if (person != null) {
                menu.setHeaderTitle("成员(" + person.getName() + ")");
                menu.add(0, 1, Menu.NONE, "删除");
            }
        }
    }

    private void deleteHandle()
    {
        if (mSelectedPosition < 0 || mSelectedPosition >= mViewPersonList.getAdapter().getCount()) {
            return;
        }

        final PayPerson selectedItem = (PayPerson)mViewPersonList.getAdapter().getItem(mSelectedPosition);
        String message = String.format(getString(R.string.remove_person_message), selectedItem.getName());
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message);
        builder.setTitle(R.string.remove_person);
        builder.setPositiveButton("Yes", new AlertDialog.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                getActivity().setResult(getActivity().RESULT_OK);
                if (GeneralResultCode.RESULT_SUCCESS ==
                        StorageManager.getInstance().remove(getActivity(), DataType.PERSON, selectedItem.getName())) {
                    refreshData();
                }
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
            case 1: //Delete
                deleteHandle();
                return true;
            default:
                return super.onContextItemSelected(item);

        }
    }

    private void startPersonDetailByMode(int mode, String name) {
        Intent intent = new Intent(getActivity(), DetailPersonActivity.class);
        intent.putExtra(DetailPersonActivity.KEY_MODE, mode);
        if (mode != DetailPersonActivity.MODE_NEW) {
            intent.putExtra(DetailPersonActivity.KEY_NAME, name);
        }
        startActivityForResult(intent, REQ_CODE_PERSON_DETAIL);
    }
}