package com.neoniequellponce.kusinasyon.activity;

import android.app.Dialog;
import android.content.ContentResolver;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.neoniequellponce.kusinasyon.R;
import com.neoniequellponce.kusinasyon.adapter.AdapterViewPager;
import com.neoniequellponce.kusinasyon.database.DbCircle;
import com.neoniequellponce.kusinasyon.database.DbUsers;
import com.neoniequellponce.kusinasyon.databinding.ActivitySetRecipeBinding;
import com.neoniequellponce.kusinasyon.databinding.DialogKeyIngredientsBinding;
import com.neoniequellponce.kusinasyon.dialogs.DialogLoading;
import com.neoniequellponce.kusinasyon.dialogs.DialogResult;
import com.neoniequellponce.kusinasyon.dialogs.DialogVisibility;
import com.neoniequellponce.kusinasyon.fragment.FragmentSetRecipeDescription;
import com.neoniequellponce.kusinasyon.fragment.FragmentSetRecipeImage;
import com.neoniequellponce.kusinasyon.fragment.FragmentSetRecipeIngredients;
import com.neoniequellponce.kusinasyon.fragment.FragmentSetRecipeInstructions;
import com.neoniequellponce.kusinasyon.fragment.FragmentSetRecipeKeyIngredients;
import com.neoniequellponce.kusinasyon.fragment.FragmentSetRecipeNameAndOrigin;
import com.neoniequellponce.kusinasyon.model.ModelRecipe;
import com.neoniequellponce.kusinasyon.model.ModelUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ActivityShareToPublic extends AppCompatActivity {

    //region Fields
    public static final String CIRCLE_KEY = "key";
    private String mCircleKey;
    private long mBackPressedTime;
    private int mCurrentPosition = 0;

    private DbUsers mDbUsers;
    private FirebaseUser mUser;

    private ModelRecipe mRecipe;

    private ActivitySetRecipeBinding mActBinding;
    private DialogKeyIngredientsBinding mDiaKeyIngredBinding;
    private Handler mHandler;

    private Toast mToast;
    private MenuItem mMenuKeyIngredInfo;
    private DialogVisibility mDiaVisibility;
    private DialogLoading mDiaLoading;
    private DialogResult mDiaResult;
    private Dialog mDiaKeyIngred;

    private AdapterViewPager mAdapter;
    private FragmentSetRecipeNameAndOrigin mFragNameOrigin;
    private FragmentSetRecipeDescription mFragDesc;
    private FragmentSetRecipeIngredients mFragIngredients;
    private FragmentSetRecipeKeyIngredients mFragKeyIngredients;
    private FragmentSetRecipeInstructions mFragInstructions;
    private FragmentSetRecipeImage mFragImage;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActBinding = ActivitySetRecipeBinding.inflate(getLayoutInflater());
        setContentView(mActBinding.getRoot());

        mCircleKey = getIntent().getStringExtra(CIRCLE_KEY);

        mDbUsers = new DbUsers();
        mRecipe = new ModelRecipe();
        mHandler = new Handler(Looper.getMainLooper());

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        assert mUser != null;

        setActionBar();
        createFragments();
        setAdapter();
        setPagerProperty();
        setFragmentBundles();
        setDialogs();
        setRecipeDefaultValues();

        mActBinding.btnBack.setOnClickListener(v -> backStep());
        mActBinding.btnNext.setOnClickListener(v -> nextStep());
    }

    private void setActionBar() {
        setSupportActionBar(mActBinding.appBar.toolbar);

        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Share to Public");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_ios_new);

        mActBinding.appBar.toolbar.setNavigationOnClickListener(v -> super.onBackPressed());
    }

    private void createFragments() {
        mFragNameOrigin = new FragmentSetRecipeNameAndOrigin();
        mFragDesc = new FragmentSetRecipeDescription();
        mFragIngredients = new FragmentSetRecipeIngredients();
        mFragKeyIngredients = new FragmentSetRecipeKeyIngredients();
        mFragInstructions = new FragmentSetRecipeInstructions();
        mFragImage = new FragmentSetRecipeImage();
    }

    private void setAdapter() {
        mAdapter = new AdapterViewPager(getSupportFragmentManager(), getLifecycle());
        mAdapter.addFragment(mFragNameOrigin);
        mAdapter.addFragment(mFragDesc);
        mAdapter.addFragment(mFragIngredients);
        mAdapter.addFragment(mFragKeyIngredients);
        mAdapter.addFragment(mFragInstructions);
        mAdapter.addFragment(mFragImage);
    }

    private void setPagerProperty() {
        mActBinding.pager.setUserInputEnabled(false);
        mActBinding.pager.setAdapter(mAdapter);
        mActBinding.pager.setCurrentItem(mCurrentPosition, false);
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

        //Pass value to FragmentSetKeyIngredients
        Bundle bdKeyIngred = new Bundle();
        bdKeyIngred.putString(FragmentSetRecipeKeyIngredients.STEP, stepsArr[3]);
        bdKeyIngred.putBoolean(FragmentSetRecipeKeyIngredients.FOR_EDIT, false);

        //Pass value to FragmentInstructions
        Bundle bdInstruct = new Bundle();
        bdInstruct.putString(FragmentSetRecipeInstructions.STEP, stepsArr[4]);
        bdInstruct.putBoolean(FragmentSetRecipeInstructions.FOR_EDIT, false);

        //Pass value to FragmentSetRecipeImage
        Bundle bdImage = new Bundle();
        bdImage.putString(FragmentSetRecipeImage.STEP, stepsArr[5]);
        bdImage.putBoolean(FragmentSetRecipeImage.FOR_EDIT, false);

        mFragNameOrigin.setArguments(bdNameAndOrigin);
        mFragDesc.setArguments(bdDesc);
        mFragIngredients.setArguments(bdIngred);
        mFragKeyIngredients.setArguments(bdKeyIngred);
        mFragInstructions.setArguments(bdInstruct);
        mFragImage.setArguments(bdImage);
    }

    private void setDialogs() {
        //Dialog visibility
        mDiaVisibility = new DialogVisibility(this, visibility ->
                mRecipe.setVisibility(visibility), () -> mDiaVisibility.dismiss());

        setDiaKeyIngred();

        //Dialog loading
        mDiaLoading = new DialogLoading(this);

        //Dialog result
        mDiaResult = new DialogResult(this, () -> {
            mDiaResult.dismiss();
            finish();
        });

        String message = "Thanks for sharing!\nYour recipe will " +
                "be reviewed by other members for approval.";

        mDiaResult.setContent("For Approval", message);
    }

    private void setDiaKeyIngred() {
        mDiaKeyIngredBinding = DialogKeyIngredientsBinding.inflate(getLayoutInflater());

        mDiaKeyIngred = new Dialog(this);
        mDiaKeyIngred.setContentView(mDiaKeyIngredBinding.getRoot());
        mDiaKeyIngred.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDiaKeyIngred.getWindow().setWindowAnimations(R.style.CustomDialogAnimation);

        mDiaKeyIngredBinding.btnOk.setOnClickListener(v -> mDiaKeyIngred.dismiss());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_act_set_recipe_toolbar, menu);

        mMenuKeyIngredInfo = menu.findItem(R.id
                .menu_act_set_recipe_key_ingred_info);

        mMenuKeyIngredInfo.setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String title = String.valueOf(item.getTitle()).toLowerCase();
        if (title.equalsIgnoreCase("Key Ingredients")) mDiaKeyIngred.show();
        else mDiaVisibility.show();
        return super.onOptionsItemSelected(item);
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

    private void backStep() {
        if (mCurrentPosition != 0) mCurrentPosition -= 1;

        /*Show toolbar menu key ingredient info if the
         *current position is on key ingredient fragment*/
        mMenuKeyIngredInfo.setVisible(mCurrentPosition == 3);

        //If button text is "submit", revert to "next"
        if (mCurrentPosition != mAdapter.getItemCount() - 1) mActBinding.btnNext.setText("Next");

        mActBinding.pager.setCurrentItem(mCurrentPosition, true);
    }

    private void nextStep() {
        if (validToProceed()) {
            String btnText = String.valueOf(mActBinding.btnNext.getText());

            //On finish click
            if (btnText.equalsIgnoreCase("Finish")) uploadImage();

            //Change button text from "next" to "finish" when end is reached
            if (mCurrentPosition == mAdapter.getItemCount() - 2) {
                mActBinding.btnNext.setText("Finish");
            }

            //Go to next fragment
            if (mCurrentPosition != mAdapter.getItemCount() - 1) mCurrentPosition += 1;

            /*Show toolbar menu key ingredient info if the
             *current position is on key ingredient fragment*/
            mMenuKeyIngredInfo.setVisible(mCurrentPosition == 3);
            mActBinding.pager.setCurrentItem(mCurrentPosition, true);
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
                if (mFragKeyIngredients.getKeyIngredientList().isEmpty()) {
                    mFragKeyIngredients.requestFocusOnKeyIngredients();
                    Toast.makeText(this, getString(R.string.key_ingredients_cannot_be_empty),
                            Toast.LENGTH_SHORT).show();
                    return false;
                } else return true;
            case 4:
                if (mFragInstructions.getInstructionList().isEmpty()) {
                    mFragInstructions.requestFocusOnInstructions();
                    Toast.makeText(this, getString(R.string.instructions_cannot_be_empty),
                            Toast.LENGTH_SHORT).show();
                    return false;
                } else return true;
            case 5:
                if (mFragImage.getImageUri() == null) {
                    Toast.makeText(this, getString(R.string.image_cannot_be_empty),
                            Toast.LENGTH_SHORT).show();
                    return false;
                } else return true;
        }
        return false;
    }

    private void uploadImage() {
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
                            .addOnSuccessListener(uri -> setRecipePrimaryDetails(String.valueOf(uri)))
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

    private void setRecipePrimaryDetails(String imageUrl) {
        List<ModelUser> finalMemberList = new ArrayList<>();
        List<ModelUser> tempMemberList = new ArrayList<>();

        DbCircle dbCircle = new DbCircle();
        dbCircle.getCircleMembers(mCircleKey, memberList -> {
            finalMemberList.clear();
            tempMemberList.clear();
            tempMemberList.addAll(memberList);

            //Remove current user from list
            for (ModelUser user : tempMemberList) {
                if (mUser.getUid().equals(user.getUid())) {
                    tempMemberList.remove(user);
                    break;
                }
            }

            //Get 75% of members
            long size = Math.round(tempMemberList.size() * 0.75);

            Random random = new Random();
            for (long i = 0; i < size; i++) {
                int index = random.nextInt(tempMemberList.size());
                finalMemberList.add(tempMemberList.get(index));
                tempMemberList.remove(index);
            }

            //Get member keys
            List<String> voterKeyList = new ArrayList<>();
            for (ModelUser user : finalMemberList) voterKeyList.add(user.getUid());

            mRecipe.setVoters(voterKeyList);
            setRecipeSecondaryDetails(imageUrl);
            uploadRecipe();
        });
    }

    private void setRecipeSecondaryDetails(String imageUrl) {
        mRecipe.setKey(mDbUsers.getKey());
        mRecipe.setName(mFragNameOrigin.getName());
        mRecipe.setDescription(mFragDesc.getDescription());
        mRecipe.setImageUrl(imageUrl);
        mRecipe.setOrigin(mFragNameOrigin.getOrigin().toLowerCase());

        mRecipe.setPrivacy("public");
        mRecipe.setStatus("for approval");
        mRecipe.setInstructions(mFragInstructions.getInstructionList());
        mRecipe.setIngredients(mFragIngredients.getIngredientList());

        //Add assumed ingredient
        List<String> keyIngredientList = mFragKeyIngredients.getKeyIngredientList();
        if (!keyIngredientList.contains("water")) keyIngredientList.add("water");
        mRecipe.setKeyIngredients(keyIngredientList);
    }

    private void uploadRecipe() {
        mDbUsers.sharePublicRecipe(mRecipe, shared -> {
            mDiaLoading.dismiss();
            if (shared) mHandler.postDelayed(() -> mDiaResult.show(), 200);
            else {
                Toast.makeText(this, getString(R.string.failed_to_share_recipe),
                        Toast.LENGTH_SHORT).show();
            }
        });
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
