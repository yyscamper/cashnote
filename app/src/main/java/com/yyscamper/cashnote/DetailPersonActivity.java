package com.yyscamper.cashnote;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.KeyListener;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CacheManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.yyscamper.cashnote.Enum.DataType;
import com.yyscamper.cashnote.Interface.GeneralResultCode;
import com.yyscamper.cashnote.PayType.PayPerson;
import com.yyscamper.cashnote.PayType.StorageSelector;
import com.yyscamper.cashnote.Storage.CacheStorage;
import com.yyscamper.cashnote.Util.Util;


public class DetailPersonActivity extends Activity {
    public static final int MODE_VIEW = 0x00;
    public static final int MODE_NEW = 0x01;
    public static final int MODE_EDIT = 0x02;
    public static final String KEY_MODE = "mode";
    public static final String KEY_NAME = "name";
    private int mMode;
    private PayPerson mPerson;

    private EditText mEditName;
    private EditText mEditPhone;
    private EditText mEditEmail;
    private EditText mEditBalance;
    private EditText mEditPayCount;
    private EditText mEditAttendCount;
    private Button mButtonSave;

    private DialogInterface.OnClickListener mRemoveClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_detail);

        mEditName = (EditText)findViewById(R.id.editTextName);
        mEditPhone = (EditText)findViewById(R.id.editTextPhone);
        mEditEmail = (EditText)findViewById(R.id.editTextEmail);
        mEditBalance = (EditText)findViewById(R.id.editTextBalance);
        mEditPayCount = (EditText)findViewById(R.id.editTextPayCount);
        mEditAttendCount = (EditText)findViewById(R.id.editTextAttendCount);
        mButtonSave = (Button)findViewById(R.id.buttonSave);

        mMode = getIntent().getIntExtra(KEY_MODE, MODE_VIEW);
        if (mMode == MODE_VIEW || mMode == MODE_EDIT) {
            if (!getIntent().hasExtra(KEY_NAME)) {
                Util.showErrorDialog(this, "没有指定要显示或编辑的人名!", "错误");
                this.finish();
                return;
            }
            String name = getIntent().getStringExtra(KEY_NAME);
            mPerson = CacheStorage.getInstance().getPerson(name);
            if (mPerson == null) {
                Util.showErrorDialog(this, "没有找到对应的成员信息!", "错误");
                this.finish();
                return;
            }
        }

        mEditBalance.setKeyListener(null);
        mEditPayCount.setKeyListener(null);
        mEditAttendCount.setKeyListener(null);
        mEditBalance.setBackgroundColor(Color.LTGRAY);
        mEditPayCount.setBackgroundColor(Color.LTGRAY);
        mEditAttendCount.setBackgroundColor(Color.LTGRAY);

        mEditEmail.setTag(mEditEmail.getKeyListener());
        mEditPhone.setTag(mEditPhone.getKeyListener());
        mEditName.setTag(mEditPhone.getKeyListener());

        mEditEmail.setAutoLinkMask(Linkify.EMAIL_ADDRESSES);
        mEditPhone.setAutoLinkMask(Linkify.PHONE_NUMBERS);

        mButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleNewEditPerson();
            }
        });

        initData();
        setEnableStatus();
    }

    private void initData() {
        switch (mMode) {
            case MODE_EDIT:
            case MODE_VIEW:
                mEditName.setText(mPerson.getName());
                mEditPhone.setText(mPerson.getPhone());
                mEditEmail.setText(mPerson.getEmail());
                mEditBalance.setText(Util.formatPrettyDouble(mPerson.getBalance()));
                mEditPayCount.setText(String.valueOf(mPerson.getPayCount()));
                mEditAttendCount.setText(String.valueOf(mPerson.getAttendCount()));
                break;
            case MODE_NEW:
                mEditName.setText("");
                mEditPhone.setText("");
                mEditEmail.setText("");
                mEditBalance.setText(String.format("0.0"));
                mEditPayCount.setText(String.valueOf(0));
                mEditAttendCount.setText(String.valueOf(0));
                break;
        }
    }

    private void setEnableStatus() {
        boolean flag = (mMode != MODE_VIEW);
        if (flag) {
            mEditEmail.setKeyListener((KeyListener)mEditEmail.getTag());
            mEditPhone.setKeyListener((KeyListener)mEditPhone.getTag());
            mEditName.setKeyListener((KeyListener)mEditName.getTag());
        }
        else {
            mEditEmail.setKeyListener(null);
            mEditPhone.setKeyListener(null);
            mEditName.setKeyListener(null);
        }

        if (mMode == MODE_VIEW) {
            mButtonSave.setVisibility(View.INVISIBLE);
        }
        else {
            mButtonSave.setVisibility(View.VISIBLE);
            if (mMode == MODE_EDIT) {
                mButtonSave.setText("保存修改");
            }
            else {
                mButtonSave.setText("保存");
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail_item_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_edit) {
            if (mMode == MODE_VIEW) {
                mMode = MODE_EDIT;
                setEnableStatus();
            }
            else if (mMode == MODE_EDIT) {
                mMode = MODE_VIEW;
                setEnableStatus();
            }
            return true;
        }
        else if (id == R.id.action_remove) {
            handleRemovePerson();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void handleRemovePerson() {
        if (mRemoveClickListener == null) {
            mRemoveClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (mPerson != null) {
                        if (GeneralResultCode.RESULT_SUCCESS ==
                            StorageManager.getInstance().remove(getApplication(), DataType.PERSON, mPerson.getName())) {
                            setResult(RESULT_OK);
                            finish();
                        }
                    }
                }
            };
        }
        Util.showConfirmDialog(this, "你确定要删除该成员吗?", "确认删除", mRemoveClickListener);
    }

    private void handleNewEditPerson() {
        if (mMode == MODE_NEW)
            return;

        String name = mEditName.getText().toString().trim();
        String phone = mEditPhone.getText().toString().trim();
        String email = mEditEmail.getText().toString().trim();

        if (name.length() == 0) {
            Util.showErrorDialog(this, "姓名不能为空！", "错误");
            setResult(RESULT_CANCELED);
            return;
        }

        if (mMode == MODE_NEW && CacheStorage.getInstance().contains(DataType.PERSON, name)) {
            Util.showErrorDialog(this, "你所要添加的新成员已经存在了！", "错误");
            setResult(RESULT_CANCELED);
            return;
        }

        if (mMode == MODE_EDIT && !name.equalsIgnoreCase(mPerson.getName()) && CacheStorage.getInstance().contains(DataType.PERSON, name)) {
            Util.showErrorDialog(this, "你修改后的成员名称已经存在了，请换一个名称！", "错误");
            setResult(RESULT_CANCELED);
            return;
        }

        PayPerson person = new PayPerson(name);
        person.setEmail(email);
        person.setPhone(phone);

        if (mMode == MODE_NEW) {
            person.setBalance(0.0);
            person.setAttendCount(0);
            person.setPayCount(0);
            if (GeneralResultCode.RESULT_SUCCESS ==
                StorageManager.getInstance().insert(this, person)) {
                Toast.makeText(this, "新建成员成功", Toast.LENGTH_LONG);
            }
        }
        else {
            person.setBalance(mPerson.getBalance());
            person.setAttendCount(mPerson.getAttendCount());
            person.setPayCount(mPerson.getPayCount());
            if (GeneralResultCode.RESULT_SUCCESS ==
                StorageManager.getInstance().update(this, mPerson.getName(), person)) {
                Toast.makeText(this, "修改成员成功", Toast.LENGTH_LONG);
            }
        }
        setResult(RESULT_OK);
        this.finish();
    }
}
