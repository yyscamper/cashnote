package com.yyscamper.cashnote;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.yyscamper.cashnote.Fragment.FragmentPayHistoryDetail;
import com.yyscamper.cashnote.Interface.PayHistoryDetailListener;
import com.yyscamper.cashnote.PayType.PayHistory;
import com.yyscamper.cashnote.PayType.StorageSelector;
import com.yyscamper.cashnote.Storage.CacheStorage;
import com.yyscamper.cashnote.Util.Util;


public class DetailHistoryActivity extends Activity implements PayHistoryDetailListener {
    public static final String KEY_MODE = "mode";
    public static final String KEY_UUID = "uuid";
    public static final String KEY_OP = "op_code";
    public static final int OP_NONE = 0;
    public static final int OP_ADDED = 1;
    public static final int OP_MODIFIED = 2;
    public static final int OP_DELETED = 3;
    private int mMode;
    private PayHistory mHistory;
    private DialogInterface.OnClickListener mPositiveListen = null;
    private FragmentPayHistoryDetail mFragment;
    private Menu mMenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_single_container);
        mMode = getIntent().getIntExtra(KEY_MODE, FragmentPayHistoryDetail.MODE_VIEW);
        if (mMode != FragmentPayHistoryDetail.MODE_NEW) {
            String uuid = getIntent().getStringExtra(KEY_UUID);
            mHistory = CacheStorage.getInstance().getHistory(uuid);
            if (mHistory == null) {
                Util.showErrorDialog(this, "未能查找到对应的历史记录", "错误");
                return;
            }
        }

        if (savedInstanceState == null) {
            mFragment = new FragmentPayHistoryDetail(mMode, mHistory, this);
            getFragmentManager().beginTransaction()
                    .add(R.id.container, mFragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail_item_menu, menu);
        mMenu = menu;
        return true;
    }

    private void handleDelete() {
        if (mPositiveListen == null) {
            mPositiveListen = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (mHistory != null) {
                        StorageManager.getInstance().removePay(getApplication(), mHistory.getKey());
                    }
                }
            };
        }
        Util.showConfirmDialog(this, "你确定要删除该条消费记录吗?", "删除确认", mPositiveListen);
    }

    public void handleEdit() {
        FragmentPayHistoryDetail newFragment = new FragmentPayHistoryDetail(FragmentPayHistoryDetail.MODE_EDIT, mHistory, this);
        getFragmentManager().beginTransaction()
                .remove(mFragment)
                .add(R.id.container, newFragment)
                .commit();
        mFragment = newFragment;
        mMenu.findItem(R.id.action_edit).setEnabled(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_edit) {
            handleEdit();
            return true;
        }
        else if (id == R.id.action_remove) {
            handleDelete();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void OnPayHistoryChanged(int action, PayHistory oldHistory, PayHistory newHistory) {
        if (action == PayHistoryDetailListener.ACTION_MODIFIED) {
            if (newHistory != null && !PayHistory.compare(newHistory, oldHistory)) {
                mHistory = newHistory;

                Intent intent = new Intent();
                intent.putExtra(KEY_UUID, mHistory.getKey());
                intent.putExtra(KEY_OP, OP_MODIFIED);
                setResult(RESULT_OK, intent);
                finish();
            }
            mMenu.findItem(R.id.action_edit).setEnabled(true);
        }
    }
}
