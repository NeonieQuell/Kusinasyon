package com.neoniequellponce.kusinasyon.voice;

import android.content.Context;
import android.util.Log;

import ai.picovoice.porcupine.PorcupineException;
import ai.picovoice.porcupine.PorcupineManager;
import ai.picovoice.porcupine.PorcupineManagerCallback;

public class PorcupineHandler {

    private static final String TAG = "PorcupineHandler";

    private PorcupineManager mPorcupineManager = null;
    private PorcupineManagerCallback mPorcupineManagerCallBack;

    private Context mContext;

    //Constructor
    public PorcupineHandler(Context context) {
        mContext = context;
    }

    public void setPorcupineManagerCallback(PorcupineManagerCallback porcupineManagerCallback) {
        mPorcupineManagerCallBack = porcupineManagerCallback;
    }

    public void startPorcupine() {
        try {
            mPorcupineManager = new PorcupineManager.Builder()
                    .setAccessKey("{access key here}")
                    .setKeywordPath("assistant_friday.ppn")
                    .setSensitivity(0.9f)
                    .build(mContext, mPorcupineManagerCallBack);
            mPorcupineManager.start();
            Log.d(TAG, "startPorcupine: porcupine start");
        } catch (PorcupineException e) {
            e.printStackTrace();
        }
    }

    public void stopPorcupine() {
        if (mPorcupineManager != null) {
            try {
                mPorcupineManager.stop();
                Log.d(TAG, "stopPorcupine: porcupine stop");
            } catch (PorcupineException e) {
                e.printStackTrace();
            }
            mPorcupineManager.delete();
        }
    }
}
