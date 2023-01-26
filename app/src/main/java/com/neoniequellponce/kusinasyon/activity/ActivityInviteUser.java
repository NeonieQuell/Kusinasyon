package com.neoniequellponce.kusinasyon.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.neoniequellponce.kusinasyon.R;
import com.neoniequellponce.kusinasyon.databinding.ActivityInviteUserBinding;

public class ActivityInviteUser extends AppCompatActivity {

    public static final String NAME = "name";
    public static final String INVITE_CODE = "inviteCode";

    private ActivityInviteUserBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityInviteUserBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        Intent intent = getIntent();
        String circleName = intent.getStringExtra(NAME);
        String inviteCode = intent.getStringExtra(INVITE_CODE);

        String name = "Invite members to the " + circleName + " circle";

        setActionBar();

        mBinding.tvHeader.setText(name);
        mBinding.tvInviteCode.setText(String.valueOf(inviteCode));
    }

    private void setActionBar() {
        setSupportActionBar(mBinding.appBar.toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Invite Code");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_ios_new);

        mBinding.appBar.toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }
}
