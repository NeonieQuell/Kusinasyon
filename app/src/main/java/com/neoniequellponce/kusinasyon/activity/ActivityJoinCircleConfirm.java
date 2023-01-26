package com.neoniequellponce.kusinasyon.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.neoniequellponce.kusinasyon.database.DbCircle;
import com.neoniequellponce.kusinasyon.databinding.ActivityJoinCircleConfirmBinding;
import com.neoniequellponce.kusinasyon.dialogs.DialogLoading;
import com.neoniequellponce.kusinasyon.dialogs.DialogResult;
import com.neoniequellponce.kusinasyon.model.ModelCircle;
import com.neoniequellponce.kusinasyon.model.ModelCircleMember;
import com.squareup.picasso.Picasso;

public class ActivityJoinCircleConfirm extends AppCompatActivity {

    public static final String CIRCLE = "circle";

    private FirebaseAuth mAuth;
    private DbCircle mDbCircle;

    private ModelCircle mCircle;

    private ActivityJoinCircleConfirmBinding mBinding;
    private Handler mHandler;

    private DialogResult mDiaResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityJoinCircleConfirmBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mCircle = getIntent().getParcelableExtra(CIRCLE);
        mAuth = FirebaseAuth.getInstance();
        mDbCircle = new DbCircle();
        mHandler = new Handler(Looper.getMainLooper());

        setAdminInfo();

        String header = "Great. You're about to join\nthe " + mCircle.getName() + " circle.";

        mBinding.tvHeader.setText(header);
        mBinding.btnJoin.setOnClickListener(v -> joinCircle());
        mBinding.btnCancel.setOnClickListener(v -> finish());
    }

    private void setAdminInfo() {
        mDbCircle.getCircleAdmin(mCircle.getKey(), admin -> {
            Picasso.with(this).load(admin.getPhotoUrl()).into(mBinding.accountImg);
            mBinding.tvAccountName.setText(admin.getDisplayName());
        });
    }

    private void joinCircle() {
        //Dialog loading
        Dialog diaLoading = new DialogLoading(this);
        diaLoading.show();

        //Dialog result
        mDiaResult = new DialogResult(this, () -> {
            setResult(RESULT_OK);
            mDiaResult.dismiss();
            finish();
        });

        //Create model of circle member
        ModelCircleMember member = new ModelCircleMember(mCircle.getKey(),
                mAuth.getUid(), "member");

        mDbCircle.addMember(member)
                .addOnSuccessListener(s ->
                        mHandler.postDelayed(() -> {
                            diaLoading.dismiss();

                            String message = "You have joined the circle named " +
                                    mCircle.getName() + " successfully.";

                            //Show dialog joined circle
                            mHandler.postDelayed(() -> {
                                mDiaResult.setContent("Circle Joined", message);
                                mDiaResult.show();
                            }, 200);
                        }, 1500))
                .addOnFailureListener(e -> mHandler.postDelayed(() -> {
                    diaLoading.dismiss();
                    e.printStackTrace();
                }, 1500));
    }
}
