package com.yyscamper.cashnote;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.KeyListener;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.yyscamper.cashnote.PayType.PayPerson;
import com.yyscamper.cashnote.PayType.PayPersonManager;
import com.yyscamper.cashnote.PayType.StorageSelector;
import com.yyscamper.cashnote.Util.Util;


public class PersonDetailActivity extends Activity {
    public static final int MODE_VIEW = 0x00;
    public static final int MODE_NEW = 0x01;
    public static final int MODE_EDIT = 0x02;

    private int mMode;
    private PayPerson mPerson;

    private EditText mEditName;
    private EditText mEditPhone;
    private EditText mEditEmail;
    private EditText mEditBalance;
    private EditText mEditPayCount;
    private EditText mEditAttendCount;

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

        mMode = getIntent().getIntExtra("mode", MODE_VIEW);
        if (mMode == MODE_VIEW || mMode == MODE_EDIT) {
            if (!getIntent().hasExtra("name")) {
                Util.ShowErrorDialog(this, "没有指定要显示或编辑的人名!", "错误");
                this.finish();
                return;
            }
            String name = getIntent().getStringExtra("name");
            mPerson = PayPersonManager.get(name);
            if (mPerson == null) {
                Util.ShowErrorDialog(this, "没有找到对应的成员信息!", "错误");
                this.finish();
                return;
            }
        }

        mEditBalance.setKeyListener(null);
        mEditPayCount.setKeyListener(null);
        mEditAttendCount.setKeyListener(null);
        mEditBalance.setFocusable(false);
        mEditPayCount.setFocusable(false);
        mEditAttendCount.setFocusable(false);

        mEditEmail.setTag(mEditEmail.getKeyListener());
        mEditPhone.setTag(mEditPhone.getKeyListener());
        mEditName.setTag(mEditPhone.getKeyListener());

        mEditEmail.setAutoLinkMask(Linkify.EMAIL_ADDRESSES);
        mEditPhone.setAutoLinkMask(Linkify.PHONE_NUMBERS);

       updateViewData();
    }

    private void updateViewData() {
        if (mMode == MODE_VIEW || mMode == MODE_EDIT) {
            mEditName.setText(mPerson.Name);
            mEditPhone.setText(mPerson.Phone);
            mEditEmail.setText(mPerson.Email);
            mEditBalance.setText(String.format("%.1f", mPerson.Balance));
            mEditPayCount.setText(String.valueOf(mPerson.PayCount));
            mEditAttendCount.setText(String.valueOf(mPerson.AttendCount));
        }
        else {
            mEditName.setText("");
            mEditPhone.setText("");
            mEditEmail.setText("");
            mEditBalance.setText(String.format("0.0"));
            mEditPayCount.setText(String.valueOf(0));
            mEditAttendCount.setText(String.valueOf(0));
        }
        boolean flag = (mMode != MODE_VIEW);

        mEditName.setFocusable(flag);
        mEditPhone.setFocusable(flag);
        mEditEmail.setFocusable(flag);
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.person_detail, menu);
        menu.findItem(R.id.action_accept).setVisible(mMode != MODE_VIEW);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        else if (id == R.id.action_accept) {
            handleNewPerson();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void handleNewPerson() {
        if (mMode != MODE_NEW)
            return;

        String name = mEditName.getText().toString().trim();
        String phone = mEditPhone.getText().toString().trim();
        String email = mEditEmail.getText().toString().trim();

        if (name.length() == 0) {
            Util.ShowErrorDialog(this, "姓名不能为空！", "错误");
            setResult(RESULT_CANCELED);
            return;
        }

        if (PayPersonManager.contains(name)) {
            Util.ShowErrorDialog(this, "该成员已经存在了！", "错误");
            setResult(RESULT_CANCELED);
            return;
        }
        PayPerson person = new PayPerson(name);
        person.Email = email;
        person.Phone = phone;
        PayPersonManager.add(person, StorageSelector.ALL);
        Toast.makeText(this, "新建成功", Toast.LENGTH_SHORT);
        setResult(RESULT_OK);
        this.finish();
    }
}
