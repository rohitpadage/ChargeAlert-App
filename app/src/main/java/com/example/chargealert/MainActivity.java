package com.example.chargealert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
//import android.widget.Toolbar;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import android.content.pm.ApplicationInfo;


public class MainActivity<firststart> extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    Context c;
    TextView bat_per,set_per,c2_txt,c2_batt;
    int level;
    SeekBar seekBar;
    Button set,stop;
    CardView c2;
    IntentFilter intentFilter;
    Intent intent;
    int battery_level = 0;
    boolean isCharging=false;
    int final_per;
    public static boolean terminate=false;
    CardView fc;
    ImageView batt_icon;
    public static  boolean alarmStrated = false;
    public static boolean alarmCancelled = false;
    Thread t;

  public   DrawerLayout dl;
   public Toolbar new_tool;
    public NavigationView nav;

//Activity p;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nav=findViewById(R.id.nav_view);

        dl=findViewById(R.id.drawer);
        new_tool=findViewById(R.id.new_toolbar);
        setSupportActionBar(new_tool);
//
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowTitleEnabled(true);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,dl,new_tool,R.string.nav1,R.string.nav2);
        dl.addDrawerListener(toggle);
      toggle.syncState();





        batt_icon=(ImageView)findViewById(R.id.batt_icon);

        fc=(CardView)findViewById(R.id.fc);

        bat_per=(TextView)findViewById(R.id.cur_batt);


        set_per=(TextView)findViewById(R.id.set_per);

        c2_txt=(TextView)findViewById(R.id.c2_txt);

        c2_batt=(TextView)findViewById(R.id.c2_batt);


        set=(Button)findViewById(R.id.set);
        stop=(Button)findViewById(R.id.stop);
        c2=(CardView)findViewById(R.id.card_view2);




        NavigationView nv=findViewById(R.id.nav_view);
        nv.setNavigationItemSelectedListener(this);




        SharedPreferences prefs=getSharedPreferences("prefs",MODE_PRIVATE);
        boolean firststart=prefs.getBoolean("firststart",true);

        if(firststart)
        {
            showStartDialog();
        }









        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                if(isCharging!=false)
                {
                    alarmCancelled = false;
                    BatteryService.AlertLevel=seekBar.getProgress();
                    writeFile(""+seekBar.getProgress());
                    c2.setVisibility(View.VISIBLE);
                    c2_txt.setVisibility(View.VISIBLE);
                    c2_batt.setVisibility(View.VISIBLE);

                    c2_batt.setText(String.valueOf(final_per)+"%");
                    startService(new Intent(getApplicationContext(),BatteryService.class));


                }


            }
        });



        if(!readFile().equals(""))
        {
            int i=Integer.parseInt(readFile());
            c2.setVisibility(View.VISIBLE);
            c2_txt.setVisibility(View.VISIBLE);
            c2_batt.setVisibility(View.VISIBLE);
            c2_batt.setText(i+"%");

        }

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarmCancelled = true;
            //    stopService(new Intent(MainActivity.this,BatteryService.class));
               // alarmCancelled = false;
                cancel();
                stop.setVisibility(View.INVISIBLE);
                writeFile("");
                c2_batt.setText("0");
            }
        });


        seekBar=(SeekBar)findViewById(R.id.seekbar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d("AAA",""+progress+" "+battery_level+" "+isCharging);
                if(!(progress>battery_level) || isCharging==false)
                {
                    seekBar.setProgress(battery_level);
                    set_per.setText(String.valueOf(battery_level));
                }
                else
                {
                    set_per.setText(String.valueOf(progress));
                }

                final_per=progress;

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                    if(isCharging==false)
                        Toast.makeText(getApplicationContext(),"Connect Charger",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        try {
            this.registerReceiver(this.batt_info,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        }catch (Exception e)
        {

        }

    }

    public void writeFile(String f)
    {
        try{
            FileOutputStream fos;
            fos=openFileOutput("Demo.txt", Context.MODE_PRIVATE);
            fos.write(f.getBytes());
            fos.close();
          //  Toast.makeText(MainActivity.this,"File saved",Toast.LENGTH_LONG).show();
        }
        catch(Exception e)
        {
            Toast.makeText(MainActivity.this,"File error"+e,Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
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
            bat_per.setText(level+"%");

            if(level<20) {
                batt_icon.setImageResource(R.drawable.battery_down);
            }
            if(level==100)
            {
                batt_icon.setImageResource(R.drawable.battery_full);
            }

            battery_level = level;

            int status = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED,-1);
            isCharging = (status==BatteryManager.BATTERY_STATUS_CHARGING || status==BatteryManager.BATTERY_PLUGGED_AC || status== BatteryManager.BATTERY_PLUGGED_USB);

            if(intent.getIntExtra(BatteryManager.EXTRA_PLUGGED,0)==0)
            {
                fc.setCardBackgroundColor(Color.LTGRAY);
                seekBar.setFocusable(false);
            }
            else{
                fc.setCardBackgroundColor(Color.GREEN);
            }
            try {
                int settedLevel = Integer.parseInt(c2_batt.getText().toString().substring(0, 2));
                if (settedLevel == level) {
                    stop.setVisibility(View.VISIBLE);

                }
            }catch (Exception e)
            {

            }



            if(!isCharging)
            {
                writeFile("");

                cancel();
                Toast.makeText(getApplicationContext(),"Alarm disabled due to discharge the plug",Toast.LENGTH_LONG).show();
              //  stopService(new Intent(getApplicationContext(),BatteryService.class));
            }
            seekBar.setProgress(level);

        }
    };

    public void cancel()
    {
        c2.setVisibility(View.GONE);
        c2_txt.setVisibility(View.GONE);
        c2_batt.setVisibility(View.GONE);
        stop.setVisibility(View.INVISIBLE);
    }


   protected void onDestroy()
    {
        cancel();
        Log.d("Activity Destroyed","destroy");
        super.onDestroy();
        this.unregisterReceiver(batt_info);
       // cancel();

    }




    private void showStartDialog()
    {
        new AlertDialog.Builder(this)
                .setTitle("Instructions when using the App"+ "\n")
                .setMessage(

                        "1. You can interact with Functionality of App only if your device kept on charging " + "\n\n"+

                                "2. Simply You have to set battery level & you will get alarm ⏰ when your device battery level reaches specified level " + "\n\n"+

                                "3. You can stop alarm by interacting with  stop button " + "\n\n"+

                                "4. if you stop charging then application clears battery level  & you will not get any alarm " + "\n\n"+

                                "5.Dont remove app from background "+ "\n"
                )
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create().show();

        SharedPreferences prefs=getSharedPreferences("prefs",MODE_PRIVATE);
        SharedPreferences.Editor editor=prefs.edit();
        editor.putBoolean("firststart",false);
        editor.apply();

    }



    public void onBackPressed() {
        if(dl.isDrawerOpen(GravityCompat.START))
        {
            dl.closeDrawer(GravityCompat.START);
        }
        else
            super.onBackPressed();

    }








    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        int id=item.getItemId();

        if(id==R.id.n1) {
            dl.closeDrawer(GravityCompat.START);
        }

        if(id==R.id.n2)
        {
            Intent email=new Intent(Intent.ACTION_SEND);
            email.putExtra(Intent.EXTRA_EMAIL,new String[]{"swaraj.kurapati@gmail.com"});
            email.putExtra(Intent.EXTRA_SUBJECT,"Charge Alert Feedback");
            email.putExtra(Intent.EXTRA_TEXT," ");
            email.setType("message/rfc822");
            email.setPackage("com.google.android.gm");
           // startActivity(Intent.createChooser(email,"Choose App: "));
           startActivity(email);
        }





        if(id==R.id.n5)
            finish();


        //return false;

        dl=findViewById(R.id.drawer);
        dl.closeDrawer(GravityCompat.START);
        return true;

    }




}
