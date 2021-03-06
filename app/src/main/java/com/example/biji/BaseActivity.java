package com.example.biji;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

public abstract class BaseActivity extends AppCompatActivity {
    static final String TAG = "tag";
    public final String ACTION = "NIGHT_SWITCH";
    protected BroadcastReceiver receiver;
    protected IntentFilter filter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
//        Intent sintent=new Intent(this,lightSencer.class);
        setNightMode();
        filter = new IntentFilter();
        filter.addAction(ACTION);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "onReceive: needRefresh");
                needRefresh();
            }
        };
//        startService(sintent);
        registerReceiver(receiver, filter);
    }

    public boolean isNightMode(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        return sharedPreferences.getBoolean("nightMode", false);
    }
    public void setNightMode(){
        if(isNightMode()) {
            Intent s=new Intent(this,lightSencer.class);
            stopService(s);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            Log.d(TAG, "setNightMode: " + sharedPreferences.getBoolean("nightMode", false));
            this.setTheme(R.style.NightTheme);
        }
        else setTheme(R.style.DayTheme);
    }
    @Override
    protected void onDestroy() {

        super.onDestroy();
        Intent sintent=new Intent(this,lightSencer.class);
        unregisterReceiver(receiver);
        stopService(sintent);
    }
    protected abstract void needRefresh();
}