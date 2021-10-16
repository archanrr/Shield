package com.example.shield;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    BroadcastReceiver testreceiver;
    BroadcastService broadcastService;
    IntentFilter filter;
    SharedPreferences sp;
    int count =0;
    public TextView textView;
    ProgressBar progressBar;
    Messenger mMessenger;
    EditText editText1;
    EditText editText2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        sp = getSharedPreferences("shref",MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putInt("Archan",666);
        edit.apply();

        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.txtView);
        editText1 = (EditText) findViewById(R.id.add1);
        editText2 = (EditText) findViewById(R.id.add2);
        progressBar = (ProgressBar) findViewById(R.id.simpleProgressBar);
        progressBar.setMax(100);
        final int[] progress = {0};
        Handler handler = new Handler(getMainLooper());
        LinearLayout ly = (LinearLayout) findViewById(R.id.layoutinside);
            new Thread(new Runnable(){
                @Override
                public void run() {
                    ly.setVisibility(LinearLayout.GONE);
                    for (int i = 0; i < 100; i++) {
                        int v= i;
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                //Toast.makeText(MainActivity.this, "hi archan.....", Toast.LENGTH_SHORT).show();
                                progressBar.setProgress(progress[0]++);
                                textView.setText(""+progress[0]);

                                if(progress[0]==100) {
                                    progressBar.setVisibility(ProgressBar.GONE);
                                    ly.setVisibility(LinearLayout.VISIBLE);
                                }
                            }
                        });
                        Log.d("Archan",""+progress[0]);
                        //Toast.makeText(MainActivity.this, "hhhh"+progress[0], Toast.LENGTH_SHORT).show();
                    }
                }
            }).start();

        Toast.makeText(this, "Oncreate Called", Toast.LENGTH_SHORT).show();
        //textView.setText(""+55555);
        testreceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction() == BroadcastService.ACTION){
                    int value = intent.getIntExtra("result",0);
                    textView.setText(""+value);
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(BroadcastService.ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(testreceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(testreceiver);
    }

    public void RegisterBattery(View view) {

    }

    public void AddNewService(View view) {
        Toast.makeText(this, ""+sp.getAll(), Toast.LENGTH_SHORT).show();
        startService(new Intent(this,BroadcastService.class));

    }
    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Toast.makeText(MainActivity.this, "Service Connected", Toast.LENGTH_SHORT).show();
            BroadcastService.BroadcastBinder binder = (BroadcastService.BroadcastBinder) service;
            broadcastService = (BroadcastService) binder.getServiceBinder();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Toast.makeText(MainActivity.this, "Service Disconnected", Toast.LENGTH_SHORT).show();
        }
    };

    public void StopService(View view) {
        Toast.makeText(this, "Unbind Service", Toast.LENGTH_SHORT).show();
        unbindService(serviceConnection);
    }

    public void Add2Nos(View view) {
        Bundle bundle = new Bundle();
        bundle.putInt("a",Integer.valueOf(String.valueOf(editText1.getText())));
        bundle.putInt("b",Integer.valueOf(String.valueOf(editText2.getText())));
        //Intent intent = new Intent();
        bindService(new Intent(this,BroadcastService.class),serviceConnection,BIND_AUTO_CREATE);
        broadcastService.findSumOf2Nos(bundle);
    }
}