package com.example.shield;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BroadcastService extends Service {
    Handler handler;
    HandlerThread handlerThread;
    LocalBroadcastManager localBroadcastManager;
    public static final String ACTION = "com.service.action";

    private IBinder serviceBinder;

    class BroadcastBinder extends Binder{
        BroadcastService getServiceBinder(){
            return BroadcastService.this;
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        return serviceBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handlerThread = new HandlerThread("myHandlerThread");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        serviceBinder = new BroadcastBinder();

    }
    public void findSumOf2Nos(Bundle bundle){
        final int[] sum = {0};
        final int a = 0;
        handler.post(new Runnable() {
            @Override
            public void run() {
                sum[0] = bundle.getInt("a") + bundle.getInt("b");
                Log.d("Archan",""+sum[0]);
                Intent i = new Intent();
                i.setAction(BroadcastService.ACTION);
                i.putExtra("result",sum[0]);
                try{
                    localBroadcastManager.sendBroadcast(i);
                } catch (Exception e){
                    Log.d("Archan",""+e);
                }
            }
        });
        Log.d("Archan",""+a);
        return;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "OnStartCommandCalled", Toast.LENGTH_SHORT).show();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
    }
}