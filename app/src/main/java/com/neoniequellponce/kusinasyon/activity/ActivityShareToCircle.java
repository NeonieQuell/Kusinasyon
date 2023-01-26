package com.neoniequellponce.kusinasyon.activity;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.neoniequellponce.kusinasyon.R;
import com.neoniequellponce.kusinasyon.adapter.AdapterViewPager;
import com.neoniequellponce.kusinasyon.database.DbCircle;
import com.neoniequellponce.kusinasyon.database.DbUsers;
import com.neoniequellponce.kusinasyon.databinding.ActivitySetRecipeBinding;
import com.neoniequellponce.kusinasyon.dialogs.DialogLoading;
import com.neoniequellponce.kusinasyon.dialogs.DialogResult;
import com.neoniequellponce.kusinasyon.dialogs.DialogVisibility;
import com.neoniequellponce.kusinasyon.fragment.FragmentSetRecipeDescription;
import com.neoniequellponce.kusinasyon.fragment.FragmentSetRecipeImage;
import com.neoniequellponce.kusinasyon.fragment.FragmentSetRecipeIngredients;
import com.neoniequellponce.kusinasyon.fragment.FragmentSetRecipeInstructions;
import com.neoniequellponce.kusinasyon.fragment.FragmentSetRecipeNameAndOrigin;
import com.neoniequellponce.kusinasyon.model.ModelRecipe;

public class ActivityShareToCircle extends AppCompatActivity {

    //region Fields
    public static final String KEY = "key";
    public static final String NAME = "name";
    private String mCircleKey;
    private String mCircleName;
    private long mBackPressedTime;
    private int mCurrentPosition = 0;

    private DbCircle mDbCircle;
    private ModelRecipe mRecipe;

    private ActivitySetRecipeBinding mBinding;
    private Handler mHandler;

    private Toast mToast;
    private DialogVisibility mDiaVisibility;
    private DialogLoading mDiaLoading;
    private DialogResult mDiaResult;

    private AdapterViewPager mAdapter;
    private FragmentSetRecipeNameAndOrigin mFragNameOrigin;
    private FragmentSetRecipeDescription mFragDesc;
    private FragmentSetRecipeIngredients mFragIngredients;
    private FragmentSetRecipeInstructions mFragInstructions;
    private FragmentSetRecipeImage mFragImage;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivitySetRecipeBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mCircleKey = getIntent().getStringExtra(KEY);
        mCircleName = getIntent().getStringExtra(NAME);

        mDbCircle = new DbCircle();
        mRecipe = new ModelRecipe();
        mHandler = new Handler(Looper.getMainLooper());

        setActionBar();
        createFragments();
        setAdapter();
        setPagerProperty();
        setFragmentBundles();
        setDialogs();
        setRecipeDefaultValues();

        mBinding.btnBack.setOnClickListener(v -> backStep());
        mBinding.btnNext.setOnClickListener(v -> nextStep());
    }

    private void setActionBar() {
        setSupportActionBar(mBinding.appBar.toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Share to " + mCircleName);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_ios_new);

        mBinding.appBar.toolbar.setNavigationOnClickListener(v -> super.onBackPressed());
    }

    private void createFragments() {
        mFragNameOrigin = new FragmentSetRecipeNameAndOrigin();
        mFragDesc = new FragmentSetRecipeDescription();
        mFragIngredients = new FragmentSetRecipeIngredients();
        mFragInstructions = new FragmentSetRecipeInstructions();
        mFragImage = new FragmentSetRecipeImage();
    }

    private void setAdapter() {
        mAdapter = new AdapterViewPager(getSupportFragmentManager(), getLifecycle());
        mAdapter.addFragment(mFragNameOrigin);
        mAdapter.addFragment(mFragDesc);
        mAdapter.addFragment(mFragIngredients);
        mAdapter.addFragment(mFragInstructions);
        mAdapter.addFragment(mFragImage);
    }

    private void setPagerProperty() {
        mBinding.pager.setUserInputEnabled(false);
        mBinding.pager.setAdapter(mAdapter);
        mBinding.pager.setCurrentItem(mCurrentPosition, false);
    }

    private void setFragmentBundles() {
        //Set step indicators on each fragment
        String[] stepsArr = new String[mAdapter.getItemCount()];
        StringBuilder builder = new StringBuilder();

        for (int i = 1; i < stepsArr.length + 1; i++) {
            builder.delete(0, builder.length());
            builder.append("Step ").append(i).append(" of ").append(mAdapter.getItemCount());

            stepsArr[i - 1] = String.valueOf(builder);
        }

        //Pass value to FragmentSetNameAndOrigin
        Bundle bdNameAndOrigin = new Bundle();
        bdNameAndOrigin.putString(FragmentSetRecipeNameAndOrigin.STEP, stepsArr[0]);
        bdNameAndOrigin.putBoolean(FragmentSetRecipeNameAndOrigin.FOR_EDIT, false);

        //Pass value to FragmentSetDescription
        Bundle bdDesc = new Bundle();
        bdDesc.putString(FragmentSetRecipeDescription.STEP, stepsArr[1]);
        bdDesc.putBoolean(FragmentSetRecipeDescription.FOR_EDIT, false);

        //Pass value to FragmentSetIngredients
        Bundle bdIngred = new Bundle();
        bdIngred.putString(FragmentSetRecipeIngredients.STEP, stepsArr[2]);
        bdIngred.putBoolean(FragmentSetRecipeIngredients.FOR_EDIT, false);

        //Pass value to FragmentInstructions
        Bundle bdInstruct = new Bundle();
        bdInstruct.putString(FragmentSetRecipeInstructions.STEP, stepsArr[3]);
        bdInstruct.putBoolean(FragmentSetRecipeInstructions.FOR_EDIT, false);

        //Pass value to FragmentSetRecipeImage
        Bundle bdImage = new Bundle();
        bdImage.putString(FragmentSetRecipeImage.STEP, stepsArr[4]);
        bdImage.putBoolean(FragmentSetRecipeImage.FOR_EDIT, false);

        mFragNameOrigin.setArguments(bdNameAndOrigin);
        mFragDesc.setArguments(bdDesc);
        mFragIngredients.setArguments(bdIngred);
        mFragInstructions.setArguments(bdInstruct);
        mFragImage.setArguments(bdImage);
    }

    private void setDialogs() {
        //Dialog visibility
        mDiaVisibility = new DialogVisibility(this, visibility ->
                mRecipe.setVisibility(visibility), () -> mDiaVisibility.dismiss());

        //Dialog loading
        mDiaLoading = new DialogLoading(this);

        //Dialog result
        mDiaResult = new DialogResult(this, () -> {
            mDiaResult.dismiss();
            finish();
        });
        mDiaResult.setContent("Recipe Shared",
                "Thanks for sharing!\nYour recipe is now at your circle.");
    }

    private void setRecipeDefaultValues() {
        //Set recipe author and authorUid
        DbUsers dbUsers = new DbUsers();
        dbUsers.getUserInfo(user -> {
            mRecipe.setAuthor(user.getDisplayName());
            mRecipe.setAuthorUid(user.getUid());
        });
        //Set recipe visibility by default
        mRecipe.setVisibility("visible");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_act_set_recipe_toolbar, menu);

        //Hide key ingredient info
        MenuItem menuKeyIngredInfo = menu.findItem(R.id
                .menu_act_set_recipe_key_ingred_info);

        menuKeyIngredInfo.setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String title = String.valueOf(item.getTitle()).toLowerCase();
        if (title.equals("visibility")) mDiaVisibility.show();
        return super.onOptionsItemSelected(item);
    }

    private void backStep() {
        if (mCurrentPosition != 0) mCurrentPosition -= 1;

        //If button text is "submit", revert to "next"
        if (mCurrentPosition != mAdapter.getItemCount() - 1) mBinding.btnNext.setText("Next");

        mBinding.pager.setCurrentItem(mCurrentPosition, true);
    }

    private void nextStep() {
        if (validToProceed()) {
            String btnText = String.valueOf(mBinding.btnNext.getText());

            //On finish click
            if (btnText.equalsIgnoreCase("Finish")) shareRecipe();

            //Change button text from "next" to "finish" when end is reached
            if (mCurrentPosition == mAdapter.getItemCount() - 2) mBinding.btnNext.setText("Finish");

            //Go to next fragment
            if (mCurrentPosition != mAdapter.getItemCount() - 1) mCurrentPosition += 1;

            mBinding.pager.setCurrentItem(mCurrentPosition, true);
        }
    }

    private boolean validToProceed() {
        //Check if inputs are not empty
        switch (mCurrentPosition) {
            case 0:
                if (mFragNameOrigin.getName().equals("")) {
                    mFragNameOrigin.requestFocusOnName();
                    Toast.makeText(this, getString(R.string.name_cannot_be_empty),
                            Toast.LENGTH_SHORT).show();
                    return false;
                } else return true;
            case 1:
                if (mFragDesc.getDescription().equals("")) {
                    mFragDesc.requestFocusOnDescription();
                    Toast.makeText(this, getString(R.string.description_cannot_be_empty),
                            Toast.LENGTH_SHORT).show();
                    return false;
                } else return true;
            case 2:
                if (mFragIngredients.getIngredientList().isEmpty()) {
                    mFragIngredients.requestFocusOnIngredients();
                    Toast.makeText(this, getString(R.string.ingredients_cannot_be_empty),
                            Toast.LENGTH_SHORT).show();
                    return false;
                } else return true;
            case 3:
                if (mFragInstructions.getInstructionList().isEmpty()) {
                    mFragInstructions.requestFocusOnInstructions();
                    Toast.makeText(this, getString(R.string.instructions_cannot_be_empty),
                            Toast.LENGTH_SHORT).show();
                    return false;
                } else return true;
            case 4:
                if (mFragImage.getImageUri() == null) {
                    Toast.makeText(this, getString(R.string.image_cannot_be_empty),
                            Toast.LENGTH_SHORT).show();
                    return false;
                } else return true;
        }
        return false;
    }

    private void shareRecipe() {
        mDiaLoading.show();

        Uri imgUri = mFragImage.getImageUri();

        StorageReference storageRef = FirebaseStorage.getInstance().getReference("userRecipesImg");
        StorageReference fileRef = storageRef.child(System.currentTimeMillis() +
                "." + getFileExtension(imgUri));

        //Upload image to Firebase Storage
        fileRef.putFile(imgUri)
                .addOnSuccessListener(taskSnapshot -> {
                    //Get image download url
                    fileRef.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                //Set recipe details
                                setRecipeDetails(String.valueOf(uri));

                                //Add recipe to circle
                                mDbCircle.shareRecipe(mRecipe, mCircleKey, shared -> {
                                    mDiaLoading.dismiss();

                                    if (shared) mHandler.postDelayed(() -> mDiaResult.show(), 200);
                                    else {
                                        Toast.makeText(ActivityShareToCircle.this,
                                                getString(R.string.failed_to_share_recipe),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                            })
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

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void setRecipeDetails(String imageUrl) {
        mRecipe.setKey(mDbCircle.getKey());
        mRecipe.setName(mFragNameOrigin.getName());
        mRecipe.setDescription(mFragDesc.getDescription());
        mRecipe.setImageUrl(imageUrl);
        mRecipe.setOrigin(mFragNameOrigin.getOrigin().toLowerCase());

        mRecipe.setPrivacy("private");
        mRecipe.setInstructions(mFragInstructions.getInstructionList());
        mRecipe.setIngredients(mFragIngredients.getIngredientList());

        mRecipe.setCircleKey(mCircleKey);
    }

    @Override
    public void onBackPressed() {
        if (mBackPressedTime + 2000 > System.currentTimeMillis()) {
            mToast.cancel();
            super.onBackPressed();
            return;
        } else {
            mToast = Toast.makeText(this, getString(R.string.press_back_twice), Toast.LENGTH_SHORT);
            mToast.show();
        }
        mBackPressedTime = System.currentTimeMillis();
    }
}
