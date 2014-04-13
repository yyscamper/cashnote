package com.yyscamper.cashnote;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.yyscamper.cashnote.Enum.DataType;
import com.yyscamper.cashnote.Interface.GeneralResultCode;
import com.yyscamper.cashnote.PayType.LocationGroup;
import com.yyscamper.cashnote.PayType.PayComparator;
import com.yyscamper.cashnote.PayType.StorageSelector;
import com.yyscamper.cashnote.Storage.CacheStorage;
import com.yyscamper.cashnote.Util.Util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Hashtable;


public class LocationGroupActivity extends Activity {
    private static final int MENU_REMOVE_GROUP = 1000;
    private static final int MENU_CLONE_GROUP = 1001;
    private static final int MENU_REMOVE_CHILD = 1100;

    ListView mListViewGroupNames;
    ListView mListViewChildrenNames;
    int mCurrGroupPosition = -1;
    int mCurrChildPostion = -1;
    private boolean mIsGroupSelectionMode = false;
    private static final int REQUEST_CODE_SELECT_CHILDREN = 1;

    Hashtable<String, ArrayAdapter<String>> mChildrenAdapterTable = new Hashtable<String, ArrayAdapter<String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_location_group);
            mListViewGroupNames = (ListView)findViewById(R.id.listViewGroupNames);
            mListViewChildrenNames = (ListView)findViewById(R.id.listViewChildrenNames);
            mListViewGroupNames.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            mListViewChildrenNames.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            updateGroupData();

            mIsGroupSelectionMode = getIntent().getBooleanExtra("selection_mode", false);

            if (getIntent().hasExtra("group")) {
                String gname = getIntent().getStringExtra("group");
                selectGroup(gname);
            }
            else {
                selectGroup(0);
            }

        mListViewGroupNames.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCurrGroupPosition = position;
                mListViewGroupNames.setItemChecked(mCurrGroupPosition, true);
                updateChildrenData();
            }
        });

        /*
        mListViewChildrenNames.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCurrChildPostion = position;
                mListViewChildrenNames.setItemChecked(mCurrChildPostion, true);
            }
        });
        */
        mListViewChildrenNames.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                mCurrChildPostion = position;
                mListViewChildrenNames.setItemChecked(mCurrChildPostion, true);
                return false;
            }
        });

        registerForContextMenu(mListViewChildrenNames);
        registerForContextMenu(mListViewGroupNames);
    }

    private void updateGroupData() {
        ArrayAdapter<String> groupAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice,
                CacheStorage.getInstance().getAllLocationGroupNames(PayComparator.KeyAsc));
        mListViewGroupNames.setAdapter(groupAdapter);
    }

    private void selectGroup(String groupName) {
        for (int i = 0; i < mListViewGroupNames.getAdapter().getCount(); i++) {
            if (groupName.equals(mListViewGroupNames.getAdapter().getItem(i).toString())) {
                mListViewGroupNames.setSelection(i);
                mCurrGroupPosition = i;
                mListViewGroupNames.setItemChecked(mCurrGroupPosition, true);
                mCurrChildPostion = -1;
                updateChildrenData();
            }
        }
    }

    private void selectGroup(int postion) {
        if (postion >= 0 && postion < mListViewGroupNames.getCount()) {
            mCurrGroupPosition = postion;
            mListViewGroupNames.setSelection(postion);
            mListViewGroupNames.setItemChecked(postion, true);
            mCurrChildPostion = -1;
            updateChildrenData();
        }
    }

    private String getGroupName(int postion) {
        return mListViewGroupNames.getAdapter().getItem(postion).toString();
    }

    private LocationGroup getGroup(int positon) {
        return CacheStorage.getInstance().getLocationGroup(getGroupName(positon));
    }

    private String getCurrChildName() {
        if (mCurrGroupPosition < 0 || mCurrChildPostion < 0) {
            return null;
        }
        return mListViewChildrenNames.getAdapter().getItem(mCurrChildPostion).toString();
    }

    private void updateChildrenData() {
        if (mListViewGroupNames.getAdapter().getCount() <= 0) {
            return;
        }
        ArrayAdapter<String> adapter;
        String groupName = mListViewGroupNames.getAdapter().getItem(mCurrGroupPosition).toString();
        if (mChildrenAdapterTable.containsKey(groupName)) {
            adapter = mChildrenAdapterTable.get(groupName);
        }
        else {
            LocationGroup group = CacheStorage.getInstance().getLocationGroup(groupName);
            String[] childrenName = group.getChildrenArray();
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, childrenName);
            mChildrenAdapterTable.put(groupName, adapter);
        }
        mListViewChildrenNames.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.location_group, menu);
        menu.findItem(R.id.action_accept).setVisible(mIsGroupSelectionMode);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_new_group) {
            handleNewGroup();
            return true;
        }
        else if (id == R.id.action_edit_group) {
            handleSelectChildren();
            return true;
        }
        else if (id == R.id.action_accept) {
            if (mIsGroupSelectionMode) {
                Intent outIntent = new Intent();
                outIntent.putExtra("selected_item", getGroupName(mCurrGroupPosition));
                setResult(RESULT_OK, outIntent);
                finish();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v == mListViewGroupNames) {
            menu.add(1, MENU_REMOVE_GROUP, 0, "删除分组");
            menu.add(1, MENU_CLONE_GROUP, 1, "复制分组");
        }
        else if (v == mListViewChildrenNames) {
            menu.add(2, MENU_REMOVE_CHILD, 0, "删除地点");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_REMOVE_GROUP:
                handleRemoveGroup();
                break;
            case MENU_REMOVE_CHILD:
                handleRemoveChild();
                break;
            case MENU_CLONE_GROUP:
                handleCloneGroup();
                break;
            default:
                return super.onContextItemSelected(item);
        }
        return true;
    }


    private void handleNewGroup()
    {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("新建分组");
        final EditText input = new EditText(this);
        input.setHint("请在此输入分组名称");
            dialog.setView(input);

            dialog.setPositiveButton(getString(R.string.txtOK), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String str = input.getText().toString().trim();
                    if (str.length() == 0) {
                        Util.showErrorDialog(getApplication(), "分组的名称不能为空！", "错误");
                        return;
                    }
                    else if (CacheStorage.getInstance().contains(DataType.LOCATION_GROUP, str)) {
                        Util.showErrorDialog(getApplication(), "该分组名称已经存在了，请重新输入!", "错误");
                        return;
                    }
                    if (GeneralResultCode.RESULT_SUCCESS ==
                            StorageManager.getInstance().insert(getApplication(), new LocationGroup(str))) {
                        updateGroupData();
                        selectGroup(str);
                    }
                }
        });
        dialog.setNegativeButton(getString(R.string.txtCancle), null);
        dialog.show();
    }

    private void handleRemoveGroup() {
        if (mCurrGroupPosition < 0) {
            return;
        }
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        final String groupName = getGroupName(mCurrGroupPosition);
        dialog.setTitle("删除分组 " + groupName);
        dialog.setMessage("你确定要删除分组 \"" + groupName + "\"吗?");

        dialog.setPositiveButton(getString(R.string.txtOK), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (GeneralResultCode.RESULT_SUCCESS ==
                StorageManager.getInstance().remove(getApplication(), DataType.LOCATION_GROUP, groupName)) {
                    updateGroupData();
                    //select the next group
                    if (mCurrGroupPosition >= mListViewGroupNames.getCount() - 1) {
                        selectGroup(0);
                    } else {
                        selectGroup(mCurrGroupPosition + 1);
                    }
                }
            }
        });
        dialog.setNegativeButton(getString(R.string.txtCancle), null);
        dialog.show();
    }

    void handleCloneGroup() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("复制分组");
        final EditText input = new EditText(this);
        input.setHint("请在此输入分组名称");
        dialog.setView(input);

        dialog.setPositiveButton(getString(R.string.txtOK), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String str = input.getText().toString().trim();
                if (str.length() == 0) {
                    Util.showErrorDialog(getApplication(), "分组的名称不能为空！", "错误");
                    return;
                }
                else if (CacheStorage.getInstance().contains(DataType.LOCATION_GROUP, str)) {
                    Util.showErrorDialog(getApplication(), "该分组名称已经存在了，请重新输入!", "错误");
                    return;
                }
                LocationGroup cloneGroup = new LocationGroup(str);
                ArrayList<String> selChildren = getGroup(mCurrGroupPosition).getChildrenList();
                for (String name : selChildren) {
                    cloneGroup.add(new String(name));
                }
                if (GeneralResultCode.RESULT_SUCCESS ==
                        StorageManager.getInstance().insert(getApplication(), cloneGroup)) {
                    updateGroupData();
                    selectGroup(str);
                }
            }
        });
        dialog.setNegativeButton(getString(R.string.txtCancle), null);
        dialog.show();
    }

    void handleRemoveChild() {
        if (mCurrGroupPosition < 0 || mCurrChildPostion < 0) {
            return;
        }

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        final LocationGroup locGroup = getGroup(mCurrGroupPosition);
        final String childName = getCurrChildName();
        dialog.setTitle("删除地点 " + childName);
        dialog.setMessage("你确定要从分组\"" + locGroup.getName() + "\"中删除地点\"" + childName + "\"吗?");

        dialog.setPositiveButton(getString(R.string.txtOK), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                locGroup.removeChild(childName);
                mChildrenAdapterTable.remove(locGroup.getName()); //remove the item from quick look-up table, so next time it can create a new one.
                updateChildrenData();
                StorageManager.getInstance().update(getApplication(), locGroup.getName(), locGroup);
            }
        });
        dialog.setNegativeButton(getString(R.string.txtCancle), null);
        dialog.show();
    }

    void handleSelectChildren() {
        LocationGroup group = getGroup(mCurrGroupPosition);
        Intent intent = new Intent(this, SelectLocationActivity.class);
        intent.putExtra("choice_mode", ListView.CHOICE_MODE_MULTIPLE);
        intent.putExtra("pre_selected_items", group.getChildrenArray());
        startActivityForResult(intent, REQUEST_CODE_SELECT_CHILDREN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_SELECT_CHILDREN) {
            if (resultCode != RESULT_OK) {
                return;
            }
            if (data != null && data.hasExtra("selected_items")) {
                String[] selChildrenNames = data.getStringArrayExtra("selected_items");
                if (selChildrenNames != null) {
                    LocationGroup group = getGroup(mCurrGroupPosition);
                    group.setChildren(selChildrenNames);
                    mChildrenAdapterTable.remove(group.getName());
                    selectGroup(mCurrGroupPosition);
                    StorageManager.getInstance().update(getApplication(), group.getName(), group);
                }
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
