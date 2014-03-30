package com.yyscamper.cashnote;

import android.app.Activity;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.yyscamper.cashnote.PayType.PayLocation;
import com.yyscamper.cashnote.PayType.PayLocationManager;
import com.yyscamper.cashnote.PayType.PayPerson;
import com.yyscamper.cashnote.PayType.PayPersonManager;
import com.yyscamper.cashnote.PayType.StorageSelector;
import com.yyscamper.cashnote.Util.Util;

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
    private int mSelectedPosition;

    private static final String ARG_SECTION_NUMBER = "section_number";

    public FragmentPersons() {

    }

    public void setSectionNumber(int sectionNumber) {
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        this.setArguments(args);
    }

    private void refreshData() {
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
        mViewPersonList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                mSelectedPosition = position;
                return false;
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
            menu.setHeaderTitle("Person Operation");
            menu.add(0, 1, Menu.NONE, "Edit");
            menu.add(0, 2, Menu.NONE, "Delete");
        }
    }

    private void addHandle() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle(getString(R.string.add_person));
        final EditText input = new EditText(getActivity());
        dialog.setView(input);

        dialog.setPositiveButton(getString(R.string.txtOK), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String str = input.getText().toString().trim();
                if (str.length() == 0) {
                    Util.ShowErrorDialog(getActivity(), "The person name cannot be empty!", "Error");
                    return;
                }
                else if (PayPersonManager.contains(str)) {
                    Util.ShowErrorDialog(getActivity(), "The person has already existed!", "Error");
                    return;
                }
                refreshData();
            }
        });
        dialog.setNegativeButton(getString(R.string.txtCancle), null);
        dialog.show();
    }

    private void deleteHandle()
    {
        if (mSelectedPosition < 0 || mSelectedPosition >= mViewPersonList.getAdapter().getCount()) {
            return;
        }

        final PayPerson selectedItem = (PayPerson)mViewPersonList.getAdapter().getItem(mSelectedPosition);
        String message = String.format(getString(R.string.remove_person_message), selectedItem.Name);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message);
        builder.setTitle(R.string.remove_person);
        builder.setPositiveButton("Yes", new AlertDialog.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                getActivity().setResult(getActivity().RESULT_OK);
                PayPersonManager.remove(selectedItem.Name, StorageSelector.ALL);
                refreshData();
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
                deleteHandle();
                return true;
            default:
                return super.onContextItemSelected(item);

        }
    }
}