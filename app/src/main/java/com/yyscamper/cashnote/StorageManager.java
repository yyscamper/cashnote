package com.yyscamper.cashnote;

import android.accounts.Account;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import com.yyscamper.cashnote.Enum.DataType;
import com.yyscamper.cashnote.Interface.GeneralResultCode;
import com.yyscamper.cashnote.Interface.StorageListener;
import com.yyscamper.cashnote.PayType.AccountBookParam;
import com.yyscamper.cashnote.PayType.PayAttendInfo;
import com.yyscamper.cashnote.PayType.PayHistory;
import com.yyscamper.cashnote.PayType.PayLocation;
import com.yyscamper.cashnote.PayType.PayPerson;
import com.yyscamper.cashnote.Storage.CacheStorage;
import com.yyscamper.cashnote.Storage.CloudStorage;
import com.yyscamper.cashnote.Storage.SqliteStorage;
import com.yyscamper.cashnote.Storage.StorageObject;
import com.yyscamper.cashnote.Util.Util;

import java.util.ArrayList;

/**
 * Created by yuanf on 2014-04-07.
 */
public class StorageManager {

    static CloudStorage mCloudStorage;
    static SqliteStorage mSqliteStorage;
    static CacheStorage mCacheStorage;

    public double getTotalMoney() {
        return mTotalMoney;
    }

    private double mTotalMoney = 0.0;

    private static StorageManager mInstance;

    public static StorageManager getInstance() {
        if (mInstance == null) {
            mInstance = new StorageManager();
        }
        return mInstance;
    }

    public void init(Context ctx) {
        if (mCacheStorage == null) {
            mCacheStorage = CacheStorage.getInstance();
            mCacheStorage.initStorage();
        }
        if (mSqliteStorage == null) {
            mSqliteStorage = new SqliteStorage(ctx, AccountBookParam.getCurrAccountBookName());
            mSqliteStorage.initStorage();
        }
        if (mCloudStorage == null) {
            mCloudStorage = new CloudStorage(ctx, AccountBookParam.getCurrAccountBookName());
            mCloudStorage.initStorage();
        }
    }

    public void close() {
        mCacheStorage.closeStorage();
        mSqliteStorage.closeStorage();
        mCloudStorage.closeStorage();
    }

    private StorageManager() {

    }

    public void debug_clear_all(Context ctx) {
        mSqliteStorage.clearAll();
        //clear(ctx, DataType.HISTORY);
        //clear(ctx, DataType.PERSON);
        //clear(ctx, DataType.LOCATION);
        //clear(ctx, DataType.LOCATION_GROUP);
    }

    public void debug_insert(Context ctx) {
        insert(ctx, new PayPerson("Felix"));
        insert(ctx, new PayPerson("Andy"));
        insert(ctx, new PayPerson("Simon"));
        insert(ctx, new PayPerson("Leo"));
        insert(ctx, new PayPerson("Alfred"));
        insert(ctx, new PayPerson("Tao"));
        insert(ctx, new PayPerson("Wayne"));

        insert(ctx, new PayLocation("兰州拉面"));
        insert(ctx, new PayLocation("四海游龙"));
        insert(ctx, new PayLocation("台味味"));
    }

    private void handleDownloadResult(DataType dataType, ArrayList items) {
        mCacheStorage.insert(dataType, items);
    }

    public void loadFromSqlite(final DataType dataType) {
        ArrayList<StorageObject> arrList = mSqliteStorage.getAll(dataType);
        mCacheStorage.clear(dataType);
        mCacheStorage.insert(dataType, arrList);
    }

    public void loadAllFromSqlite() {
        loadFromSqlite(DataType.PERSON);
        loadFromSqlite(DataType.LOCATION);
        loadFromSqlite(DataType.LOCATION_GROUP);
        loadFromSqlite(DataType.HISTORY);
    }

    public int loadFromCloud(final Context ctx, final DataType dataType) {
        TaskCloudLoad task = new TaskCloudLoad(ctx);
        task.execute(dataType);
        int result;
        try {
            result = task.get();
        }
        catch (Throwable err) {
            result = GeneralResultCode.RESULT_FAILED;
        }
        return result;
    }

    public int loadAllFromCloud(final Context ctx) {
        TaskCloudLoad task = new TaskCloudLoad(ctx);
        task.execute(DataType.PERSON, DataType.LOCATION, DataType.LOCATION_GROUP, DataType.HISTORY);
        int result;
        try {
            result = task.get();
        }
        catch (Throwable err) {
            result = GeneralResultCode.RESULT_FAILED;
        }
        return result;
    }

    public int insert(final Context ctx, final StorageObject item) {
        if (item == null) {
            Util.showErrorDialog(ctx, "你所要添加的内容不能是NULL");
            return GeneralResultCode.RESULT_FAILED;
        }

        if (!item.validate()) {
            Util.showErrorDialog(ctx, "你所要添加的内容验证失败！");
            return GeneralResultCode.RESULT_FAILED;
        }

        if (mCacheStorage.contains(item.getType(), item.getKey())) {
            Util.showErrorDialog(ctx, "你所要添加的内容已经存在!");
            return GeneralResultCode.RESULT_FAILED;
        }

        ProgressDialog progView = new ProgressDialog(ctx);
        progView.show();
        final ProgressControl progCtrl = new ProgressControl();
        progCtrl.setStart();

        final StorageListener.StorageOperationListener operationListener = new StorageListener.StorageOperationListener() {
            @Override
            public void onOperationFinished(int result, long count, int errCode, String errMsg) {
                if (result != GeneralResultCode.RESULT_SUCCESS) {
                    Util.showErrorDialog(ctx, "添加" + Util.DataTypeToString(item.getType()) + "出错，错误码:" + errCode + ", 错误信息:" + errMsg);
                    progCtrl.setCancle();
                    return;
                }

                if (mSqliteStorage != null) {
                    mSqliteStorage.insert(item);
                }
                mCacheStorage.insert(item);
                progCtrl.setFinished();
            }
        };

        mCloudStorage.insert(item, operationListener);
        return progCtrl.waitToEnd(progView);
    }

    public int remove(Context ctx, final DataType dataType, final String key) {
        if (!mCacheStorage.contains(dataType, key)) {
            Util.showErrorDialog(ctx, "你所要删除的内容并不存在!");
            return GeneralResultCode.RESULT_FAILED;
        }
        return GeneralResultCode.RESULT_SUCCESS;
    }

    public int update(Context ctx, final String oldKey, final StorageObject obj) {
        if (!mCacheStorage.contains(obj.getType(), oldKey)) {
            Util.showErrorDialog(ctx, "你所要更新的内容并不存在!");
            return GeneralResultCode.RESULT_FAILED;
        }
        return GeneralResultCode.RESULT_SUCCESS;
    }

    public int clear(Context ctx, final DataType dataType) {
        return GeneralResultCode.RESULT_SUCCESS;
    }

    //return the dirty object list
    private ArrayList<StorageObject> handleInsertPay(Context ctx, PayHistory history) {
        if (history == null || !history.validate()) {
            return null;
        }

        PayPerson payer = history.getPayer();
        PayPerson[] attends = history.getAttendPersons();
        PayLocation loc = history.getLocation();
        PayAttendInfo[] attendsInfo = history.getAttendsInfo();

        if (payer == null || attends == null || loc == null || attends.length == 0) {
            Util.showErrorDialog(ctx, "付款者/参与成员/地点为空！");
            return null;
        }

        payer.setBalance(payer.getBalance() - history.getMoney());
        payer.setPayCount(payer.getPayCount() + 1);

        if (history.getPayType() == PayHistory.PAY_TYPE_NORMAL_AVG) {
            double avg = history.getMoney() / attends.length;
            for (int i = 0; i < attends.length; i++) {
                attends[i].setAttendCount(attends[i].getAttendCount() + 1);
                attends[i].setBalance(attends[i].getBalance() - avg);
            }
        }
        else {
            for (int i = 0; i < attends.length; i++) {
                attends[i].setAttendCount(attends[i].getAttendCount() + 1);
                attends[i].setBalance(attends[i].getBalance() - attendsInfo[i].getMoney());
            }
        }

        loc.setAttendCount(loc.getAttendCount() + 1);
        loc.setLastAttendTime(history.getPayTime());

        mTotalMoney += history.getMoney();

        ArrayList<StorageObject> dirtyList = new ArrayList<StorageObject>();
        payer.setDirty(true);
        dirtyList.add(payer);
        for (PayPerson p : attends) {
            if (p != payer) {
                p.setDirty(true);
                dirtyList.add(p);
            }
        }
        loc.setDirty(true);
        dirtyList.add(loc);
        return dirtyList;
    }

    //return the dirty object list
    private ArrayList<StorageObject> handleRemovePay(Context ctx, PayHistory history) {
        if (history == null || !history.validate()) {
            return null;
        }

        PayPerson payer = history.getPayer();
        PayPerson[] attends = history.getAttendPersons();
        PayLocation loc = history.getLocation();
        PayAttendInfo[] attendsInfo = history.getAttendsInfo();

        if (payer == null || attends == null || loc == null || attends.length == 0) {
            Util.showErrorDialog(ctx, "付款者/参与成员/地点为空！");
            return null;
        }

        payer.setBalance(payer.getBalance() + history.getMoney());
        payer.setPayCount(payer.getPayCount() - 1);

        if (history.getPayType() == PayHistory.PAY_TYPE_NORMAL_AVG) {
            double avg = history.getMoney() / attends.length;
            for (int i = 0; i < attends.length; i++) {
                attends[i].setAttendCount(attends[i].getAttendCount() - 1);
                attends[i].setBalance(attends[i].getBalance() + avg);
            }
        }
        else {
            for (int i = 0; i < attends.length; i++) {
                attends[i].setAttendCount(attends[i].getAttendCount() - 1);
                attends[i].setBalance(attends[i].getBalance() + attendsInfo[i].getMoney());
            }
        }

        loc.setAttendCount(loc.getAttendCount() - 1);

        mTotalMoney -= history.getMoney();

        ArrayList<StorageObject> dirtyList = new ArrayList<StorageObject>();
        payer.setDirty(true);
        dirtyList.add(payer);
        for (PayPerson p : attends) {
            if (p != payer) {
                p.setDirty(true);
                dirtyList.add(p);
            }
        }
        loc.setDirty(true);
        dirtyList.add(loc);
        return dirtyList;
    }

    private void updateDirtyList(final ArrayList<StorageObject> dirtyList) {
        if (dirtyList == null) {
            return;
        }
    }

    public int insertPay(Context ctx, PayHistory history) {
        ArrayList<StorageObject> dirtyList = handleInsertPay(ctx, history);
        if (dirtyList == null) {
            return GeneralResultCode.RESULT_FAILED;
        }
        insert(ctx, history);
        updateDirtyList(dirtyList);
        return GeneralResultCode.RESULT_SUCCESS;
    }

    public int removePay(Context ctx, String key) {
        PayHistory history = mCacheStorage.getHistory(key);
        if (history == null) {
            return GeneralResultCode.RESULT_FAILED;
        }
        ArrayList<StorageObject> dirtyList = handleRemovePay(ctx, history);
        if (dirtyList == null) {
            return GeneralResultCode.RESULT_FAILED;
        }
        remove(ctx, DataType.HISTORY, key);
        updateDirtyList(dirtyList);
        return GeneralResultCode.RESULT_SUCCESS;
    }

    public class ProgressControl {
        int flag;

        final int IDLE = 0;
        final int STARTED = 1;
        final int FINISHED = 2;
        final int CANCLED = 3;

        public ProgressControl() {
            flag = IDLE;
        }

        public void setStart() {
            flag = STARTED;
        }

        public void setCancle() {
            flag = CANCLED;
        }

        public void setFinished() {
            flag = FINISHED;
        }

        public boolean isStarted() {
            return (flag == STARTED);
        }
        public boolean isFinished() {
            return (flag == FINISHED);
        }
        public boolean isCancled() {
            return (flag == CANCLED);
        }

        public int waitToEnd(ProgressDialog progDiag) {
            int result = GeneralResultCode.RESULT_CANCLED;

            while(true) {
                if (isFinished()) {
                    result = GeneralResultCode.RESULT_SUCCESS;
                    break;
                }
                else if (isCancled()) {
                    result = GeneralResultCode.RESULT_CANCLED;
                    break;
                }
                else {
                    try {
                        Thread.sleep(50, 0);
                    }
                    catch (Throwable err) {
                        result = GeneralResultCode.RESULT_CANCLED;
                        break;
                    }
                }
            }
            if (progDiag != null)
                progDiag.dismiss();
            return result;
        }
    }

    private class TaskCloudLoad extends AsyncTask<DataType, Integer, Integer> {
        ProgressDialog mProgessView;
        private Context mContext;
        public TaskCloudLoad(Context ctx) {
            super();
            mContext = ctx;
            mProgessView = new ProgressDialog(ctx);
            mProgessView.setCancelable(true);
            mProgessView.setMessage("正在加载...");
        }

        @Override
        protected Integer doInBackground(DataType... params) {
            mProgessView.setMax(params.length);
            mProgessView.show();

            final ProgressControl progCtrl = new ProgressControl();

            for (int i = 0; i < params.length; i++) {
                final DataType dataType = params[i];
                final int progVal = i;
                progCtrl.setStart();

                final StorageListener.StorageGetAllListener syncDownloadListener = new StorageListener.StorageGetAllListener() {
                    @Override
                    public void onGetAllResults(int result, ArrayList items, int errCode, String errMsg) {
                        if (result != GeneralResultCode.RESULT_SUCCESS || items == null) {
                            Util.showErrorDialog(mContext, "同步" + Util.DataTypeToString(dataType) + "出错，错误码:" + errCode + ", 错误信息:" + errMsg);
                            progCtrl.setCancle();
                            return;
                        }
                        mCacheStorage.clear(dataType);
                        mCacheStorage.insert(dataType, items);

                        mSqliteStorage.clear(dataType);
                        mSqliteStorage.insert(dataType, items);

                        progCtrl.setFinished();
                        publishProgress(progVal);
                    }
                };

                mCloudStorage.getAll(dataType, syncDownloadListener);
                int result = progCtrl.waitToEnd(mProgessView);
                if (result != GeneralResultCode.RESULT_SUCCESS) {
                    return result;
                }
            }
            return GeneralResultCode.RESULT_SUCCESS;
        }

        @Override
        protected void onProgressUpdate(Integer... progresses) {
            mProgessView.setProgress(progresses[0]);
        }
    }


    public class TaskInsert extends AsyncTask<StorageObject, Integer, Integer> {
        ProgressDialog mProgessView;
        private Context mContext;
        public TaskInsert(Context ctx) {
            super();
            mContext = ctx;
            mProgessView = new ProgressDialog(ctx);
            mProgessView.setCancelable(true);
            mProgessView.setMessage("正在处理...");
        }

        @Override
        protected Integer doInBackground(StorageObject... params) {
            mProgessView.setMax(params.length);
            mProgessView.show();

            final ProgressControl progCtrl = new ProgressControl();

            for (int i = 0; i < params.length; i++) {
                final StorageObject item = params[i];
                final int progVal = i;
                progCtrl.setStart();

                final StorageListener.StorageOperationListener operationListener = new StorageListener.StorageOperationListener() {
                    @Override
                    public void onOperationFinished(int result, long count, int errCode, String errMsg) {
                    if (result != GeneralResultCode.RESULT_SUCCESS) {
                        Util.showErrorDialog(mContext, "添加" + Util.DataTypeToString(item.getType()) + "出错，错误码:" + errCode + ", 错误信息:" + errMsg);
                        progCtrl.setCancle();
                        return;
                    }

                    if (mSqliteStorage != null) {
                        mSqliteStorage.insert(item);
                    }
                    mCacheStorage.insert(item);
                    progCtrl.setFinished();
                    }
                };

                mCloudStorage.insert(item, operationListener);
                int result = progCtrl.waitToEnd(mProgessView);
                if (result != GeneralResultCode.RESULT_SUCCESS) {
                    return result;
                }
            }
            return GeneralResultCode.RESULT_SUCCESS;
        }
    }

    private class TaskRemove extends AsyncTask<String, Integer, Integer> {
        ProgressDialog mProgessView;
        private DataType mDataType;
        private Context mContext;
        public TaskRemove(Context ctx, DataType dataType) {
            super();
            mContext = ctx;
            mDataType = dataType;
            mProgessView = new ProgressDialog(ctx);
            mProgessView.setCancelable(true);
            mProgessView.setMessage("正在处理...");
        }

        @Override
        protected Integer doInBackground(String... params) {
            mProgessView.show();
            final ProgressControl progCtrl = new ProgressControl();
            final String key = params[0];

            final StorageListener.StorageOperationListener operationListener = new StorageListener.StorageOperationListener() {
                @Override
                public void onOperationFinished(int result, long count, int errCode, String errMsg) {
                    if (result != GeneralResultCode.RESULT_SUCCESS) {
                        Util.showErrorDialog(mContext, "删除失败，错误码:" + errCode + ", 错误信息:" + errMsg);
                        progCtrl.setCancle();
                        return;
                    }

                    if (mSqliteStorage != null) {
                        mSqliteStorage.remove(mDataType, key);
                    }
                    mCacheStorage.remove(mDataType, key);
                    progCtrl.setFinished();
                }
            };

            mCloudStorage.remove(mDataType, key, operationListener);
            return progCtrl.waitToEnd(mProgessView);
        }
    }

    private class TaskUpdate extends AsyncTask<StorageObject, Integer, Integer> {
        ProgressDialog mProgessView;
        private String mOldKey;
        private Context mContext;

        public TaskUpdate(Context ctx, String oldKey) {
            super();
            mContext = ctx;
            mOldKey = oldKey;
            mProgessView = new ProgressDialog(ctx);
            mProgessView.setCancelable(true);
            mProgessView.setMessage("正在处理...");
        }

        @Override
        protected Integer doInBackground(StorageObject... params) {
            mProgessView.show();
            final ProgressControl progCtrl = new ProgressControl();
            final StorageObject item = params[0];

            final StorageListener.StorageOperationListener operationListener = new StorageListener.StorageOperationListener() {
                @Override
                public void onOperationFinished(int result, long count, int errCode, String errMsg) {
                    if (result != GeneralResultCode.RESULT_SUCCESS) {
                        Util.showErrorDialog(mContext, "更新失败，错误码:" + errCode + ", 错误信息:" + errMsg);
                        progCtrl.setCancle();
                        return;
                    }

                    if (mSqliteStorage != null) {
                        mSqliteStorage.update(mOldKey, item);
                    }
                    mCacheStorage.update(mOldKey, item);
                    progCtrl.setFinished();
                }
            };
            mCloudStorage.update(mOldKey, item, operationListener);
            return progCtrl.waitToEnd(mProgessView);
        }
    }

    private class TaskClear extends AsyncTask<DataType, Integer, Integer> {
        ProgressDialog mProgessView;
        private Context mContext;
        public TaskClear(Context ctx) {
            super();
            mContext = ctx;
            mProgessView = new ProgressDialog(ctx);
            mProgessView.setCancelable(true);
            mProgessView.setMessage("正在处理...");
        }

        @Override
        protected Integer doInBackground(DataType... params) {
            mProgessView.show();
            final DataType dataType = params[0];

            final ProgressControl progCtrl = new ProgressControl();
            progCtrl.setStart();

            final StorageListener.StorageOperationListener operationListener = new StorageListener.StorageOperationListener() {
                @Override
                public void onOperationFinished(int result, long count, int errCode, String errMsg) {
                    if (result != GeneralResultCode.RESULT_SUCCESS) {
                        progCtrl.setCancle();
                        Util.showErrorDialog(mContext, "清空失败，错误码:" + errCode + ", 错误信息:" + errMsg);
                        return;
                    }

                    if (mSqliteStorage != null) {
                        mSqliteStorage.clear(dataType);
                    }
                    mCacheStorage.clear(dataType);
                    progCtrl.setFinished();
                }
            };

            mCloudStorage.clear(dataType, operationListener);
            return progCtrl.waitToEnd(mProgessView);
        }
    }

    private class TaskUpdateDirtyList extends AsyncTask<ArrayList<StorageObject>, Integer, Integer> {
        ProgressDialog mProgessView;
        private Context mContext;
        public TaskUpdateDirtyList(Context ctx) {
            mContext = ctx;
            mProgessView = new ProgressDialog(ctx);
            mProgessView.setCancelable(true);
            mProgessView.setMessage("正在处理...");
        }

        @Override
        protected Integer doInBackground(ArrayList<StorageObject>... params) {
            ArrayList<StorageObject> dirtyList = params[0];

            mProgessView.setMax(dirtyList.size());
            mProgessView.setProgress(0);

            final ProgressControl progCtrl = new ProgressControl();
            progCtrl.setStart();

            final StorageListener.StorageOperationListener operationListener = new StorageListener.StorageOperationListener() {
                @Override
                public void onOperationFinished(int result, long count, int errCode, String errMsg) {
                if (result != GeneralResultCode.RESULT_SUCCESS) {
                    Util.showErrorDialog(mContext, "添加失败，错误码:" + errCode + ", 错误信息:" + errMsg);
                    progCtrl.setCancle();
                    return;
                }
                mProgessView.setProgress(mProgessView.getProgress() + 1);
                progCtrl.setFinished();
                }
            };

            for (int i = 0; i < dirtyList.size(); i++) {
                StorageObject sobj = dirtyList.get(i);
                mProgessView.setMessage("更新" + sobj.getType().toString() + ", " + sobj.getKey() + "...");
                progCtrl.setStart();
                mCloudStorage.update(sobj, operationListener);
                int result = progCtrl.waitToEnd(null);
                if (result != GeneralResultCode.RESULT_SUCCESS) {
                    return result;
                }
            }
            return GeneralResultCode.RESULT_SUCCESS;
        }
    }
}
