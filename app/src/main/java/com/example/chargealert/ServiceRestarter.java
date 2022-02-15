package com.example.chargealert;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ServiceRestarter extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
//        Log.d("Broadcast", "onReceive: "+intent.getAction());
//        if(intent.getAction().equals("Battery"))
//            context.startService(new Intent(context,BatteryService.class));
//        if(intent.getAction().equals("ShowStopBtn"))
//        {
//            Intent i = new Intent(context,MainActivity.class);
//            i.putExtra("StopBtn","YES");
//            context.startActivity(i);
//        }
    }
}
