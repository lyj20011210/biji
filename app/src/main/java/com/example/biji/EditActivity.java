package com.example.biji;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EditActivity extends BaseActivity {

    EditText et;

    private Toolbar myToolbar;
    private String old_content = "";
    private String old_time = "";
    private int old_Tag = 1;
    private long id = 0;
    private int openMode = 0;
    private int tag = 1;
    public Intent intent = new Intent(); // message to be sent
    private boolean tagChange = false;
    private String content = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_layout);
        myToolbar = findViewById(R.id.my_Toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //设置toolbar取代actionbar

        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoSetMessage();
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        et = findViewById(R.id.et);
        Intent getIntent = getIntent();
        openMode = getIntent.getIntExtra("mode", 0);

        if (openMode == 3) {//打开已存在的note
            id = getIntent.getLongExtra("id", 0);
            old_content = getIntent.getStringExtra("content");
            old_time = getIntent.getStringExtra("time");
            old_Tag = getIntent.getIntExtra("tag", 1);
            et.setText(old_content);
            et.setSelection(old_content.length());

        }

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                new AlertDialog.Builder(EditActivity.this)
                        .setMessage("删除吗？")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (openMode == 4) {
                                    intent.putExtra("mode", -1);
                                    setResult(RESULT_OK, intent);
                                } else {
                                    intent.putExtra("mode", 2);
                                    intent.putExtra("id", id);
                                    setResult(RESULT_OK, intent);
                                }
                                finish();
                            }
                        }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create().show();
                break;
            case R.id.message:
                new AlertDialog.Builder(EditActivity.this)
                        .setMessage("添加短信吗？")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                //获取权限
                                if (ContextCompat.checkSelfPermission(EditActivity.this,
                                        Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(EditActivity.this,
                                            new String[]{Manifest.permission.READ_SMS}, 1);
                                } else {

                                    Uri uri = Uri.parse("content://sms/");
                                    ContentResolver cr = getContentResolver();
                                    String[] projection = new String[]{"_id", "address", "person", "body", "date", "type"};
                                    Cursor cur = cr.query(uri, projection, null, null, "date desc");
                                    if (null == cur) {
                                        Log.i("ooc", "************cur == null");
                                        return;
                                    }
                                    while (cur.moveToNext()) {
                                        @SuppressLint("Range") String number = cur.getString(cur.getColumnIndex("address"));//手机号
                                        @SuppressLint("Range") String name = cur.getString(cur.getColumnIndex("person"));//联系人姓名列表
                                        @SuppressLint("Range") String body = cur.getString(cur.getColumnIndex("body"));//短信内容
                                        content = content + number + "\n" + body + "\n\n";
//                                    Log.d("tar", content);

                                    }
                                    et.setText(content);
                                }
                            }
                        }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create().show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void needRefresh() {
        Log.d(TAG, "needRefresh: Edit");
        setNightMode();
        Intent intent = new Intent(this, EditActivity.class);
        startActivity(intent);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            autoSetMessage();
            //Intent intent = new Intent();
            //intent.putExtra("content", et.getText().toString());
            //intent.putExtra("time", dateToStr());
            setResult(RESULT_OK, intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void autoSetMessage() {
        if (openMode == 4) {
            if (et.getText().toString().length() == 0) {
                intent.putExtra("mode", -1); //nothing new happens.
            } else {
                intent.putExtra("mode", 0); // new one note;
                intent.putExtra("content", et.getText().toString());
                intent.putExtra("time", dateToStr());
                intent.putExtra("tag", tag);
            }
        } else {
            if (et.getText().toString().equals(old_content) && !tagChange)
                intent.putExtra("mode", -1); // edit nothing
            else {
                intent.putExtra("mode", 1); //edit the content
                intent.putExtra("content", et.getText().toString());
                intent.putExtra("time", dateToStr());
                intent.putExtra("id", id);
                intent.putExtra("tag", tag);
            }
        }
    }

    public String dateToStr() {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(date);
    }
}