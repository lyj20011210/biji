package com.example.biji;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.util.SortedMap;

public class lightSencer extends Service implements SensorEventListener{
    private String TAG="lightchange";
    private SensorManager sensorManager;
    private Sensor sensor;;
    private int flag=0;
    public lightSencer() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sensorManager=(SensorManager)getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(this,sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT),SensorManager.SENSOR_DELAY_FASTEST);
        sensor=sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

//        Log.d(TAG, "onCreate: service");
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onSensorChanged(SensorEvent event) {
        float value=event.values[0];
        if(value<30&&flag<=0)
        {
            flag++;
            Log.d(TAG, "onSensorChanged: 发送通知！");
            Context context=getApplicationContext();
            String channelId="channel";
            Notification notification=new NotificationCompat.Builder(this,channelId)
                    .setContentTitle("太暗了！")
                    .setContentText("请切换至黑夜模式，对眼睛更好哦")
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.ic_baseline_adb_24)
                    .setVibrate(new long[]{100,250,100,250,100,250,100,250,100,250,100,250,100,250})
                    .setWhen(System.currentTimeMillis())
                    .setChannelId(channelId)
                    .build();
            NotificationManager notificationManager=(NotificationManager)context.getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel channel=new NotificationChannel(channelId,"channel",NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
            notificationManager.notify(1,notification);

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
