package com.neoniequellponce.kusinasyon.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.neoniequellponce.kusinasyon.R;
import com.neoniequellponce.kusinasyon.database.DbCircle;
import com.neoniequellponce.kusinasyon.databinding.ActivityCreateCircleBinding;
import com.neoniequellponce.kusinasyon.dialogs.DialogLoading;
import com.neoniequellponce.kusinasyon.dialogs.DialogResult;
import com.neoniequellponce.kusinasyon.model.ModelCircle;
import com.neoniequellponce.kusinasyon.model.ModelCircleMember;

public class ActivityCreateCircle extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DbCircle mDbCircle;

    private ActivityCreateCircleBinding mBinding;
    private Handler mHandler;

    private DialogLoading mDiaLoading;
    private DialogResult mDiaResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityCreateCircleBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        mDbCircle = new DbCircle();
        mHandler = new Handler(Looper.getMainLooper());
        mDiaLoading = new DialogLoading(this);

        setActionBar();

        mBinding.btnCreate.setOnClickListener(v -> createCircle());
    }

    private void setActionBar() {
        setSupportActionBar(mBinding.appBar.toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Create a Circle");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_ios_new);

        mBinding.appBar.toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void createCircle() {
        String name = String.valueOf(mBinding.etCircleName.getText()).trim();

        if (name.isEmpty()) {
            mBinding.etCircleName.setText("");
            mBinding.etCircleName.requestFocus();
            Toast.makeText(this, "Circle name cannot be empty", Toast.LENGTH_SHORT).show();
        } else {
            mDiaLoading.show();

            //Set invite code of circle based on time milliseconds
            String inviteCode = String.valueOf(System.currentTimeMillis());

            ModelCircle circle = new ModelCircle(mDbCircle.getKey(),
                    inviteCode, name, mAuth.getUid());

            createCircleHelper(circle);
        }
    }

    private void createCircleHelper(ModelCircle circle) {
        mDbCircle.createCircle(circle)
                .addOnSuccessListener(s -> {
                    ModelCircleMember member = new ModelCircleMember(circle.getKey(),
                            mAuth.getUid(), "admin");

                    //Add current user as admin to members
                    mDbCircle.addMember(member)
                            .addOnSuccessListener(s1 ->
                                    mHandler.postDelayed(() -> {
                                        mDiaLoading.dismiss();

                                        mDiaResult = new DialogResult(this, () -> {
                                            mDiaResult.dismiss();
                                            mBinding.etCircleName.setText("");
                                        });

                                        String message;
                                        message = "The circle named " + circle.getName() +
                                                " has been created.";

                                        mDiaResult.setContent("Circle Created", message);
                                        mHandler.postDelayed(() -> mDiaResult.show(), 200);
                                    }, 1500))
                            .addOnFailureListener(e -> {
                                mDiaLoading.dismiss();
                                e.printStackTrace();
                            });
                })
                .addOnFailureListener(e -> {
                    mDiaLoading.dismiss();
                    e.printStackTrace();
                });
    }
}
