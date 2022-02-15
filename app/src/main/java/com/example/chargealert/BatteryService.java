package com.example.chargealert;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.BatteryManager;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import com.google.android.material.navigation.NavigationView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;


public class BatteryService extends Service {

public static   int AlertLevel=101;
    int battery_level;
    boolean isCharging=true;



    @Override
    public void onCreate() {
        super.onCreate();


        Log.d("Service Started","started...");
        if(readFile()!=null)
        {
            try {
                AlertLevel = Integer.parseInt(readFile());
            }catch (Exception e)
            {

            }

        }
        this.registerReceiver(this.batt_info,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {

        Thread t=new Thread()
        {
            public void run()
            {
                MediaPlayer  mp=MediaPlayer.create(BatteryService.this, Settings.System.DEFAULT_RINGTONE_URI);;

                try {
                    while (true)
                    {
                        Log.d("Battery Service ",battery_level+"--"+AlertLevel);
                        if(battery_level>=AlertLevel && !MainActivity.alarmStrated )
                        {
                            mp.start();
                            MainActivity.alarmStrated = true;

                        }
                        if(isCharging==false || MainActivity.alarmCancelled) {
                            mp.stop();

                        }
                        Thread.sleep(1000);
                    }
                }
                catch (Exception e)
                {}

            }
        };

        t.start();

        return startId;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    public IBinder onBind(Intent intent) {
        return null;
    }






    public String readFile()
    {
        String data="";
        try {
            String fname ="Demo.txt" ;
            FileInputStream fin = openFileInput(fname);
            InputStreamReader isr = new InputStreamReader(fin);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder s = new StringBuilder();
            String line = null;

            while ((line=br.readLine())!=null) {
                data+=line;
            }
            fin.close();
            isr.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return data;
    }


    BroadcastReceiver batt_info=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL,0);

            battery_level = level;
            int status = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED,-1);
            isCharging = (status==BatteryManager.BATTERY_STATUS_CHARGING || status==BatteryManager.BATTERY_PLUGGED_AC || status== BatteryManager.BATTERY_PLUGGED_USB);


        }
    };




}
