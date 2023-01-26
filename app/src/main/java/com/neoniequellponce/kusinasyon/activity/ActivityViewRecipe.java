package com.neoniequellponce.kusinasyon.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.neoniequellponce.kusinasyon.R;
import com.neoniequellponce.kusinasyon.adapter.AdapterViewPager;
import com.neoniequellponce.kusinasyon.databinding.ActivityViewRecipeBinding;
import com.neoniequellponce.kusinasyon.dialogs.DialogManageRecipeAuthor;
import com.neoniequellponce.kusinasyon.dialogs.DialogResult;
import com.neoniequellponce.kusinasyon.dialogs.DialogSpeakNow;
import com.neoniequellponce.kusinasyon.fragment.FragmentRecipeDetails;
import com.neoniequellponce.kusinasyon.fragment.FragmentRecipeIngredients;
import com.neoniequellponce.kusinasyon.fragment.FragmentRecipeInstructions;
import com.neoniequellponce.kusinasyon.model.ModelRecipe;
import com.neoniequellponce.kusinasyon.service.AppService;
import com.neoniequellponce.kusinasyon.utility.UtilPermission;
import com.neoniequellponce.kusinasyon.voice.AzureSpeechService;
import com.neoniequellponce.kusinasyon.voice.PorcupineHandler;
import com.neoniequellponce.kusinasyon.voice.SpeechHandler;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import ai.picovoice.porcupine.PorcupineManagerCallback;

public class ActivityViewRecipe extends AppCompatActivity
        implements DialogManageRecipeAuthor.OnRecipeDeleteListener {

    //region Fields
    public static final String RECIPE = "recipe";
    public static final int RQ_EDIT_CONTENT = 1;
    private int mRecipeInstructionToken = 1;
    private long mBackPressedTime;
    private boolean mIsPorcupineOn = false;
    private boolean mIsCookingStarted = false;

    private ModelRecipe mRecipe;
    private HashMap<Integer, Object> mRecipeInstructMap;

    private PorcupineHandler mPorcupineHandler;
    private AzureSpeechService mAzureSpeech;
    private MediaPlayer mMediaPlayer;
    private SpeechHandler mSpeechHandler;
    private SpeechResultRunnable mSpeechResultRun;

    private ActivityViewRecipeBinding mBinding;
    private Handler mHandler;

    private DialogManageRecipeAuthor mDiaManage;
    private DialogResult mDiaResult;
    private DialogSpeakNow mDiaSpeakNow;
    private Toast mToastPressBackTwice;

    private AdapterViewPager mAdapter;
    private FragmentRecipeDetails mFragDetails;
    private FragmentRecipeIngredients mFragIngredients;
    private FragmentRecipeInstructions mFragInstructions;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityViewRecipeBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mRecipe = getIntent().getParcelableExtra(RECIPE);
        mHandler = new Handler(Looper.getMainLooper());
        mDiaManage = new DialogManageRecipeAuthor(this, this, this);
        mDiaManage.setRecipe(mRecipe);

        //region Methods
        setFullScreen();
        setBottomSheet();
        checkIfUserIsAuthor();
        createFragments();
        setAdapter();
        setFragmentBundles();
        setRecipeInstructionMap();
        //endregion

        mBinding.fabBack.setOnClickListener(v -> finish());
        mBinding.fabAssistant.setOnClickListener(v -> fabAction());
        Picasso.with(this).load(mRecipe.getImageUrl()).into(mBinding.recipeImg);
        mBinding.btmSheet.tabLayout.addOnTabSelectedListener(tabSelection);
        mBinding.btmSheet.pager.setAdapter(mAdapter);
        mBinding.btmSheet.pager.registerOnPageChangeCallback(pageChange);
    }

    private void setFullScreen() {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setStatusBarColor(Color.TRANSPARENT);

        ViewCompat.setOnApplyWindowInsetsListener(mBinding.rlParent, (v, insets) -> {
            ViewGroup.MarginLayoutParams fabBackParams = (ViewGroup.MarginLayoutParams)
                    mBinding.fabBack.getLayoutParams();

            ViewGroup.MarginLayoutParams fabAssistantParams = (ViewGroup.MarginLayoutParams)
                    mBinding.fabAssistant.getLayoutParams();

            fabBackParams.setMargins(0, insets.getSystemWindowInsetTop(), 0, 0);
            fabAssistantParams.setMargins(0, insets.getSystemWindowInsetTop(), 0, 0);

            mBinding.fabBack.setLayoutParams(fabBackParams);
            mBinding.fabAssistant.setLayoutParams(fabAssistantParams);
            insets.consumeSystemWindowInsets();
            return insets;
        });
    }

    private void setBottomSheet() {
        BottomSheetBehavior<MaterialCardView> sheetBehavior;
        sheetBehavior = BottomSheetBehavior.from(mBinding.btmSheet.cvContainer);
        sheetBehavior.setHideable(false);
        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        // Set bottom sheet minimum height
        int sheetBehaviourMinHeight = (int) (getResources()
                .getDisplayMetrics().heightPixels * 0.65d);

        sheetBehavior.setPeekHeight(sheetBehaviourMinHeight, true);

        mBinding.btmSheet.tvRecipeName.setText(mRecipe.getName());
        mBinding.btmSheet.btnManage.setOnClickListener(v -> mDiaManage.show());
    }

    private void checkIfUserIsAuthor() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (!Objects.equals(auth.getUid(), mRecipe.getAuthorUid())) {
            mBinding.btmSheet.btnManage.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RQ_EDIT_CONTENT) {
            if (resultCode == RESULT_OK) finish();
        }
    }

    private void createFragments() {
        mFragDetails = new FragmentRecipeDetails();
        mFragIngredients = new FragmentRecipeIngredients();
        mFragInstructions = new FragmentRecipeInstructions();
    }

    private void setAdapter() {
        mAdapter = new AdapterViewPager(getSupportFragmentManager(), getLifecycle());
        mAdapter.addFragment(mFragDetails);
        mAdapter.addFragment(mFragIngredients);
        mAdapter.addFragment(mFragInstructions);
    }

    private void setFragmentBundles() {
        Bundle bdDetails = new Bundle();
        bdDetails.putString(FragmentRecipeDetails.DESCRIPTION, mRecipe.getDescription());
        bdDetails.putString(FragmentRecipeDetails.AUTHOR_UID, mRecipe.getAuthorUid());
        bdDetails.putString(FragmentRecipeDetails.AUTHOR, mRecipe.getAuthor());
        bdDetails.putString(FragmentRecipeDetails.ORIGIN, mRecipe.getOrigin());
        bdDetails.putString(FragmentRecipeDetails.VISIBILITY, mRecipe.getVisibility());

        Bundle bdIngred = new Bundle();
        bdIngred.putStringArrayList(FragmentRecipeIngredients.INGREDIENTS,
                (ArrayList<String>) mRecipe.getIngredients());

        Bundle bdInstruct = new Bundle();
        bdInstruct.putStringArrayList(FragmentRecipeInstructions.INSTRUCTIONS,
                (ArrayList<String>) mRecipe.getInstructions());

        mFragDetails.setArguments(bdDetails);
        mFragIngredients.setArguments(bdIngred);
        mFragInstructions.setArguments(bdInstruct);
    }

    private void setRecipeInstructionMap() {
        //Put recipe instructions in hashmap for assistant
        mRecipeInstructMap = new HashMap<>();
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < mRecipe.getInstructions().size(); i++) {
            builder.delete(0, builder.length());
            builder.append(mRecipe.getInstructions().get(i));
            mRecipeInstructMap.put(i + 1, String.valueOf(builder));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mIsPorcupineOn) stopAssistantResources();
    }

    private void stopAssistantResources() {
        mPorcupineHandler.stopPorcupine();
        mAzureSpeech.stopSynthesizing();
        mAzureSpeech.release();
    }

    private void fabAction() {
        if (!mIsPorcupineOn) {
            try {
                if (UtilPermission.hasRecordPermission(getApplicationContext())) {
                    porcupineOn();
                } else UtilPermission.requestRecordPermission(this);
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else porcupineOff();
    }

    private void porcupineOn() {
        mMediaPlayer = MediaPlayer.create(this, R.raw.sound_picovoice_wake_word_detect);

        //Set fab icon to active
        mBinding.fabAssistant.setImageResource(R.drawable.ic_mic);
        mBinding.fabAssistant.setColorFilter(ContextCompat.getColor(this,
                android.R.color.holo_green_dark));

        mDiaSpeakNow = new DialogSpeakNow(this);

        AppService.sPool.execute(() -> {
            //Set volume to max
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            int volume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume,
                    AudioManager.FLAG_SHOW_UI);

            mSpeechResultRun = new SpeechResultRunnable();

            runOnUiThread(() -> mSpeechHandler = new SpeechHandler(this,
                    speechRecognition));

            mPorcupineHandler = new PorcupineHandler(this);
            mPorcupineHandler.setPorcupineManagerCallback(porcupineCallback);

            mAzureSpeech = new AzureSpeechService();

            mPorcupineHandler.startPorcupine();
            mIsPorcupineOn = true;
        });
    }

    private void porcupineOff() {
        AppService.sPool.execute(() -> {
            stopAssistantResources();
            mIsPorcupineOn = false;
        });

        //Set fab icon to inactive
        mBinding.fabAssistant.setImageResource(R.drawable.ic_mic_off);
        mBinding.fabAssistant.setColorFilter(ContextCompat.getColor(this, R.color.sonic_silver_600));
    }

    @Override
    public void onRecipeDelete() {
        mDiaResult = new DialogResult(this, () -> {
            mDiaResult.dismiss();
            finish();
        });

        String message = "The recipe named " + mRecipe.getName() + " is now deleted.";
        mDiaResult.setContent("Recipe Deleted", message);

        mHandler.postDelayed(() -> mDiaResult.show(), 200);
    }

    @Override
    public void onBackPressed() {
        if (mIsPorcupineOn) {
            if (mBackPressedTime + 2000 > System.currentTimeMillis()) {
                mToastPressBackTwice.cancel();
                super.onBackPressed();
                return;
            } else {
                mToastPressBackTwice = Toast.makeText(this, getString(R.string.press_back_twice),
                        Toast.LENGTH_SHORT);
                mToastPressBackTwice.show();
            }
            mBackPressedTime = System.currentTimeMillis();
        } else super.onBackPressed();
    }

    private TabLayout.OnTabSelectedListener tabSelection = new
            TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    mBinding.btmSheet.pager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            };

    private ViewPager2.OnPageChangeCallback pageChange = new
            ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    mBinding.btmSheet.tabLayout.getTabAt(position).select();

                    // Remove overscroll on horizontal swipe in viewpager
                    View child = mBinding.btmSheet.pager.getChildAt(position);
                    if (child instanceof RecyclerView) {
                        child.setOverScrollMode(View.OVER_SCROLL_NEVER);
                    }
                }
            };

    private PorcupineManagerCallback porcupineCallback = keywordIndex -> {
        mSpeechHandler.startListening();
        mMediaPlayer.start();
    };

    private RecognitionListener speechRecognition = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle params) {
            mDiaSpeakNow.show();
        }

        @Override
        public void onBeginningOfSpeech() {

        }

        @Override
        public void onRmsChanged(float rmsdB) {

        }

        @Override
        public void onBufferReceived(byte[] buffer) {

        }

        @Override
        public void onEndOfSpeech() {
            mDiaSpeakNow.dismiss();
        }

        @Override
        public void onError(int error) {
            mDiaSpeakNow.dismiss();
        }

        @Override
        public void onResults(Bundle results) {
            List<String> resultList;
            resultList = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

            if (!resultList.isEmpty()) {
                mSpeechResultRun.setInput(resultList.get(0).toLowerCase().trim());
                AppService.sPool.execute(mSpeechResultRun);
            }
        }

        @Override
        public void onPartialResults(Bundle partialResults) {

        }

        @Override
        public void onEvent(int eventType, Bundle params) {

        }
    };

    private class SpeechResultRunnable implements Runnable {

        private StringBuilder mInput;
        private StringBuilder mOutput;

        public SpeechResultRunnable() {
            mInput = new StringBuilder();
            mOutput = new StringBuilder();
        }

        public void setInput(String input) {
            if (mInput.length() > 0) mInput.delete(0, mInput.length());
            mInput.append(input);
        }

        @Override
        public void run() {
            //Clear string builder content
            if (mOutput.length() > 0) mOutput.delete(0, mOutput.length());

            /*Case 1: Check if input speech contains "GO TO STEP" phrase
             *Case 2: Perform other commands*/
            if (String.valueOf(mInput).contains("go to step")) {
                /*Get instruction number based on index
                 *"GO TO STEP (N)"*/
                int key = Integer.parseInt(String.valueOf(mInput).substring(11));

                //Check if input instruction number is valid
                if (mRecipeInstructMap.containsKey(key)) {
                    mOutput.append("Step").append(" ").append(key).append(", ")
                            .append(mRecipeInstructMap.get(key));
                } else mOutput.append("There is not step ").append(key);

                mAzureSpeech.speak(String.valueOf(mOutput));
            } else {
                AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                int volume;

                try {
                    String command = String.valueOf(mInput).toLowerCase();

                    switch (command) {
                        case "start cooking":
                            if (mIsCookingStarted) {
                                mOutput.append("You already started cooking.");
                                mAzureSpeech.speak(String.valueOf(mOutput));
                            } else {
                                speakInstruction();
                                mIsCookingStarted = true;
                            }
                            break;
                        case "what's next":
                            if (!mIsCookingStarted) {
                                mOutput.append("You are already on the first step.");
                                mAzureSpeech.speak(String.valueOf(mOutput));
                            } else {
                                mRecipeInstructionToken++;
                                speakInstruction();
                            }
                            break;
                        case "go back":
                            if (!mIsCookingStarted) {
                                mOutput.append("You did not start cooking yet.");
                                mAzureSpeech.speak(String.valueOf(mOutput));
                            } else if (mRecipeInstructionToken == 1) {
                                mOutput.append("You are already on the first step.");
                                mAzureSpeech.speak(String.valueOf(mOutput));
                            } else {
                                --mRecipeInstructionToken;
                                speakInstruction();
                            }
                            break;
                        case "repeat current instruction":
                            if (!mIsCookingStarted) {
                                mOutput.append("You did not start cooking yet.");
                                mAzureSpeech.speak(String.valueOf(mOutput));
                            } else speakInstruction();
                            break;
                        case "volume up":
                            volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) + 1;
                            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume,
                                    AudioManager.FLAG_SHOW_UI);
                            break;
                        case "volume half":
                            int fullVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                            volume = fullVolume / 2;
                            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume,
                                    AudioManager.FLAG_SHOW_UI);
                            break;
                        case "volume max":
                            volume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume,
                                    AudioManager.FLAG_SHOW_UI);
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    mOutput.append("I didn't understand.");
                    mAzureSpeech.speak(String.valueOf(mOutput));
                }
            }
        }

        private void speakInstruction() {
            mOutput.append("Step").append(" ")
                    .append(mRecipeInstructionToken).append(", ")
                    .append(mRecipeInstructMap.get(mRecipeInstructionToken));

            mAzureSpeech.speak(String.valueOf(mOutput));
        }
    }
}
