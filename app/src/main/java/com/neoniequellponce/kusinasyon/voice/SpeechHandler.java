package com.neoniequellponce.kusinasyon.voice;

import android.content.Context;
import android.content.Intent;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

import java.util.Locale;

public class SpeechHandler {

    private SpeechRecognizer mSpeechRecognizer;
    private Intent mSpeechIntent;
    private RecognitionListener mRecognitionListener;

    private Context mContext;

    //Constructor
    public SpeechHandler(Context context, RecognitionListener recognitionListener) {
        mContext = context;
        mRecognitionListener = recognitionListener;
        setSpeechRecognizer();
    }

    private void setSpeechRecognizer() {
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(mContext);

        mSpeechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                Locale.getDefault());
        mSpeechIntent.putExtra(RecognizerIntent
                .EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 5000);

        mSpeechRecognizer.setRecognitionListener(mRecognitionListener);
    }

    public void startListening() {
        mSpeechRecognizer.startListening(mSpeechIntent);
    }
}
