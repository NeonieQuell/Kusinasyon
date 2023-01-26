package com.neoniequellponce.kusinasyon.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.neoniequellponce.kusinasyon.R;
import com.neoniequellponce.kusinasyon.adapter.AdapterCircleMember;
import com.neoniequellponce.kusinasyon.database.DbCircle;
import com.neoniequellponce.kusinasyon.databinding.ActivityViewCircleBinding;
import com.neoniequellponce.kusinasyon.dialogs.DialogDeleteCircle;
import com.neoniequellponce.kusinasyon.dialogs.DialogEditCircleName;
import com.neoniequellponce.kusinasyon.model.ModelCircle;
import com.neoniequellponce.kusinasyon.model.ModelUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ActivityViewCircle extends AppCompatActivity {

    public static final String CIRCLE = "circle";
    private boolean mIsAdmin = false;

    private FirebaseUser mFirebaseUser;

    private ModelCircle mCircle;
    private List<ModelUser> mMemberList;

    private ActivityViewCircleBinding mBinding;

    private DialogDeleteCircle mDiaDeleteCircle;

    private AdapterCircleMember mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityViewCircleBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mCircle = getIntent().getParcelableExtra(CIRCLE);
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        mBinding.swipeLayout.setRefreshing(true);
        mBinding.llCircleInfo.setVisibility(View.GONE);

        setActionBar();
        setRecyclerView();
        getCircleMembers();

        mBinding.swipeLayout.setOnRefreshListener(this::getCircleMembers);
    }

    private void setActionBar() {
        setSupportActionBar(mBinding.appBar.toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("View Circle Info");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_ios_new);

        mBinding.appBar.toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setRecyclerView() {
        mMemberList = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mAdapter = new AdapterCircleMember(this, mMemberList);

        mBinding.recyclerView.setHasFixedSize(true);
        mBinding.recyclerView.setLayoutManager(layoutManager);
        mBinding.recyclerView.setAdapter(mAdapter);
    }

    private void getCircleMembers() {
        DbCircle dbCircle = new DbCircle();
        dbCircle.getCircleMembers(mCircle.getKey(), memberList -> {
            mMemberList.clear();
            mMemberList.addAll(memberList);

            dbCircle.getCircleAdmin(mCircle.getKey(), admin -> {
                //Show toolbar menu if admin
                if (mFirebaseUser.getUid().equals(admin.getUid())) mIsAdmin = true;
                invalidateOptionsMenu();

                //Remove admin from member list
                for (ModelUser member : mMemberList) {
                    if (member.getUid().equals(admin.getUid())) {
                        mMemberList.remove(member);
                        break;
                    }
                }

                setCirclePrimaryInfo();
                setAdminView(admin);
                mAdapter.notifyDataSetChanged();
                mBinding.swipeLayout.setRefreshing(false);
                mBinding.llCircleInfo.setVisibility(View.VISIBLE);
            });
        });
    }

    private void setCirclePrimaryInfo() {
        int size = mMemberList.size() + 1;
        String memberSize = "";

        if (size > 1) memberSize = size + " people";
        else memberSize = size + " person";

        mBinding.tvCircleName.setText(mCircle.getName());
        mBinding.tvMemberSize.setText(memberSize);
    }

    private void setAdminView(ModelUser admin) {
        Picasso.with(this).load(admin.getPhotoUrl()).into(mBinding.adminImg);
        mBinding.tvAdminName.setText(admin.getDisplayName());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_act_view_circle_info_toolbar, menu);

        MenuItem menuEditCircleName = menu.findItem(R.id
                .menu_act_view_circle_info_toolbar_edit_circle_name);

        MenuItem menuDeleteCircle = menu.findItem(R.id
                .menu_act_view_circle_info_toolbar_delete_circle);

        //Hide option if not admin
        if (!mIsAdmin) {
            menuEditCircleName.setVisible(false);
            menuDeleteCircle.setVisible(false);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String title = String.valueOf(item.getTitle());

        if (title.equalsIgnoreCase("Edit Circle Name")) {
            String oldName = String.valueOf(mBinding.tvCircleName.getText());

            DialogEditCircleName dialog;
            dialog = new DialogEditCircleName(this, mCircle.getKey(), oldName, newName -> {
                mBinding.tvCircleName.setText(newName);
                Toast.makeText(this, "The circle has been renamed", Toast.LENGTH_LONG).show();
            });
            dialog.show();
        } else if (title.equalsIgnoreCase("Delete Circle")) {
            mDiaDeleteCircle = new DialogDeleteCircle(this, mCircle.getKey(), () -> {
                mDiaDeleteCircle.dismiss();
                Toast.makeText(this, "Circle deleted", Toast.LENGTH_LONG).show();
                finish();
            });
            mDiaDeleteCircle.show();
        } else {
            Intent intent = new Intent(ActivityViewCircle.this, ActivityInviteUser.class);
            intent.putExtra(ActivityInviteUser.NAME, mCircle.getName());
            intent.putExtra(ActivityInviteUser.INVITE_CODE, mCircle.getInviteCode());
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
