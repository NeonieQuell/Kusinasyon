package com.neoniequellponce.kusinasyon.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.neoniequellponce.kusinasyon.R;
import com.neoniequellponce.kusinasyon.database.DbCircle;
import com.neoniequellponce.kusinasyon.databinding.ActivityJoinCircleBinding;
import com.neoniequellponce.kusinasyon.dialogs.DialogLoading;
import com.neoniequellponce.kusinasyon.model.ModelCircle;
import com.neoniequellponce.kusinasyon.model.ModelCircleMember;

import java.util.List;
import java.util.Objects;

public class ActivityJoinCircle extends AppCompatActivity {

    private ActivityJoinCircleBinding mBinding;
    private Handler mHandler;

    private DialogLoading mDiaLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityJoinCircleBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mHandler = new Handler(Looper.getMainLooper());
        mDiaLoading = new DialogLoading(this);

        setActionBar();

        mBinding.btnSubmit.setOnClickListener(v -> submitCode());
    }

    private void setActionBar() {
        setSupportActionBar(mBinding.appBar.toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Join a Circle");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_ios_new);

        mBinding.appBar.toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void submitCode() {
        String inviteCode = String.valueOf(mBinding.etInviteCode.getText()).trim();

        if (inviteCode.isEmpty()) {
            mBinding.etInviteCode.setText("");
            mBinding.etInviteCode.requestFocus();
            Toast.makeText(this, "Invite code cannot be empty", Toast.LENGTH_SHORT).show();
        } else {
            mDiaLoading.show();
            DbCircle dbCircle = new DbCircle();
            dbCircle.joinCircle(inviteCode, (circleExist, memberList, circle) ->
                    joinCircle(isValidToJoin(circleExist, memberList), circle));
        }
    }

    private void joinCircle(boolean isValidToJoin, ModelCircle circle) {
        if (isValidToJoin) {
            mHandler.postDelayed(() -> {
                mDiaLoading.dismiss();
                Intent intent = new Intent(this, ActivityJoinCircleConfirm.class);
                intent.putExtra(ActivityJoinCircleConfirm.CIRCLE, circle);
                startActivityForResult(intent, 1);
            }, 1500);
        }
    }

    private boolean isValidToJoin(boolean circleExist, List<ModelCircleMember> memberList) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;

        //Check if input circle invite code exist on database
        if (circleExist) {
            for (int i = 0; i < memberList.size(); i++) {
                ModelCircleMember member = memberList.get(i);

                //Check if user is member already of the circle
                if (Objects.equals(user.getUid(), member.getUid())) {
                    mHandler.postDelayed(() -> {
                        mDiaLoading.dismiss();
                        Toast.makeText(this, "You are already a member of this circle",
                                Toast.LENGTH_SHORT).show();
                    }, 1500);
                    return false;
                }
            }
        } else {
            mHandler.postDelayed(() -> {
                mDiaLoading.dismiss();
                Toast.makeText(this, "Sorry! That code is not valid", Toast.LENGTH_SHORT).show();
            }, 1500);
            return false;
        }
        //Return true by default
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) finish();
        }
    }
}
