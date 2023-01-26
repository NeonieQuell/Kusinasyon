package com.neoniequellponce.kusinasyon.voice;

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import com.microsoft.cognitiveservices.speech.AudioDataStream;
import com.microsoft.cognitiveservices.speech.Connection;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechSynthesisOutputFormat;
import com.microsoft.cognitiveservices.speech.SpeechSynthesisResult;
import com.microsoft.cognitiveservices.speech.SpeechSynthesizer;
import com.neoniequellponce.kusinasyon.service.AppService;

public class AzureSpeechService {

    private boolean mIsStopped = false;

    private SpeechConfig mSpeechConfig;
    private SpeechSynthesizer mSynthesizer;
    private Connection mConnection;
    private AudioTrack mAudioTrack;
    private final Object mSynchronizedObj = new Object();
    private SpeakingRunnable mSpeakingRunnable;

    //Constructor
    public AzureSpeechService() {
        mSpeakingRunnable = new SpeakingRunnable();

        createSynthesizer();
        preConnect();
        setAudioTrack();
    }

    private void createSynthesizer() {
        try {
            if (mSynthesizer != null) {
                mSpeechConfig.close();
                mSynthesizer.close();
                mConnection.close();
            }

            mSpeechConfig = SpeechConfig.fromSubscription("{subscription key here}",
                    "eastus");

            // Use 24k Hz format for higher quality
            mSpeechConfig.setSpeechSynthesisOutputFormat(SpeechSynthesisOutputFormat
                    .Raw24Khz16BitMonoPcm);

            // Set voice name
            mSpeechConfig.setSpeechSynthesisVoiceName("en-US-AshleyNeural");
            mSynthesizer = new SpeechSynthesizer(mSpeechConfig, null);
            mConnection = Connection.fromSpeechSynthesizer(mSynthesizer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void preConnect() {
        if (mConnection != null) mConnection.openConnection(true);
    }

    private void setAudioTrack() {
        mAudioTrack = new AudioTrack(
                new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .build(),
                new AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(24000)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build(),
                AudioTrack.getMinBufferSize(
                        24000,
                        AudioFormat.CHANNEL_OUT_MONO,
                        AudioFormat.ENCODING_PCM_16BIT) * 2,
                AudioTrack.MODE_STREAM,
                AudioManager.AUDIO_SESSION_ID_GENERATE
        );
    }

    public void speak(String text) {
        mSpeakingRunnable.setContent(text);
        AppService.sPool.execute(mSpeakingRunnable);
    }

    public void release() {
        try {
            if (mSynthesizer != null) {
                mSynthesizer.close();
                mConnection.close();
            }

            if (mSpeechConfig != null) mSpeechConfig.close();

            if (mAudioTrack != null) {
                mAudioTrack.flush();
                mAudioTrack.stop();
                mAudioTrack.release();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class SpeakingRunnable implements Runnable {

        private String mContent;

        public void setContent(String content) {
            mContent = content;
        }

        @Override
        public void run() {
            if (mSynthesizer != null) {
                try {
                    mAudioTrack.play();
                    synchronized (mSynchronizedObj) {
                        mIsStopped = false;
                    }

                    SpeechSynthesisResult result = mSynthesizer.StartSpeakingTextAsync(
                            mContent).get();

                    AudioDataStream audioDataStream = AudioDataStream.fromResult(result);

                    // Set the chunk size to 50 ms. 24000 * 16 * 0.05 / 8 = 2400
                    byte[] buffer = new byte[2400];
                    while (!mIsStopped) {
                        long len = 0L;

                        try {
                            len = audioDataStream.readData(buffer);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (len == 0) {
                            break;
                        }
                        mAudioTrack.write(buffer, 0, (int) len);
                    }
                    audioDataStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    assert (false);
                }
            }
        }
    }

    public void stopSynthesizing() {
        if (mSynthesizer != null) mSynthesizer.StopSpeakingAsync();

        if (mAudioTrack != null) {
            synchronized (mSynchronizedObj) {
                mIsStopped = true;
            }
            mAudioTrack.pause();
            mAudioTrack.flush();
        }
    }
}
