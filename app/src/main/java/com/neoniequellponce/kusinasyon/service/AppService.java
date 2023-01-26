package com.neoniequellponce.kusinasyon.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppService extends Service {

    public static ExecutorService sPool = Executors.newFixedThreadPool(Runtime.getRuntime()
            .availableProcessors());
    
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        sPool.shutdownNow();
        super.onDestroy();
    }
}
