package com.neoniequellponce.kusinasyon.activity;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Patterns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.neoniequellponce.kusinasyon.R;
import com.neoniequellponce.kusinasyon.database.DbUsers;
import com.neoniequellponce.kusinasyon.databinding.ActivityCreateAccountBinding;
import com.neoniequellponce.kusinasyon.dialogs.DialogLoading;
import com.neoniequellponce.kusinasyon.dialogs.DialogResult;
import com.neoniequellponce.kusinasyon.model.ModelUser;
import com.neoniequellponce.kusinasyon.utility.UtilPermission;
import com.squareup.picasso.Picasso;

public class ActivityCreateAccount extends AppCompatActivity implements View.OnClickListener {

    private int mTransition;

    private FirebaseAuth mFirebaseAuth;

    private ActivityCreateAccountBinding mBinding;
    private Handler mHandler;

    private DialogLoading mDiaLoading;
    private DialogResult mDiaResult;

    private Uri mImgUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityCreateAccountBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mTransition = getResources().getInteger(android.R.integer.config_shortAnimTime);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mHandler = new Handler(Looper.getMainLooper());
        mDiaLoading = new DialogLoading(this);

        setActionBar();

        mBinding.accountImg.setOnClickListener(this);
        mBinding.btnCreate.setOnClickListener(this);
    }

    private void setActionBar() {
        setSupportActionBar(mBinding.appBar.toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Create an Account");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_ios_new);

        mBinding.appBar.toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_create) createAccount();
        else setAccountImage();
    }

    private void setAccountImage() {
        if (UtilPermission.hasStoragePermission(getApplicationContext())) {
            if (resultLauncher != null) resultLauncher.launch("image/*");
        } else UtilPermission.requestStoragePermission(this);
    }

    private ActivityResultLauncher<String> resultLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
                if (result != null) {
                    Picasso.with(this).load(result).into(mBinding.accountImg);
                    mImgUri = result;
                }
            });

    private void createAccount() {
        if (mImgUri == null) Toast.makeText(this, "No photo selected", Toast.LENGTH_SHORT).show();
        else {
            String displayName = String.valueOf(mBinding.etDisplayName.getText()).trim();
            String email = String.valueOf(mBinding.etEmail.getText()).trim();
            String password = String.valueOf(mBinding.etPassword.getText()).trim();

            if (displayName.isEmpty()) {
                mBinding.etDisplayName.requestFocus();
                Toast.makeText(this, "Display name cannot be empty", Toast.LENGTH_SHORT).show();
            } else if (email.isEmpty()) {
                mBinding.etEmail.requestFocus();
                Toast.makeText(this, "Email cannot be empty", Toast.LENGTH_SHORT).show();
            } else if (password.isEmpty()) {
                mBinding.etPassword.requestFocus();
                Toast.makeText(this, "Password cannot be empty", Toast.LENGTH_SHORT).show();
            } else {
                //Check if email address format is valid
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show();
                } else {
                    //Check if password length is 8 at least characters
                    if (password.length() < 8) {
                        mBinding.etPassword.requestFocus();
                        Toast.makeText(this, "Password must be at least 8 characters",
                                Toast.LENGTH_SHORT).show();
                    } else createAccountHelper(displayName, email, password);
                }
            }
        }
    }

    private void createAccountHelper(String displayName, String email, String password) {
        mDiaLoading.show();
        mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ModelUser user = new ModelUser(
                                mFirebaseAuth.getUid(), email, displayName, "");

                        createUserInFirebase(user);
                    } else {
                        mDiaLoading.dismiss();

                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            Toast.makeText(this, "Account already exist",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Failed to create account",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(Throwable::printStackTrace);
    }

    private void createUserInFirebase(ModelUser user) {
        Uri imgUri = mImgUri;

        StorageReference storageRef = FirebaseStorage.getInstance().getReference("userAccountsImg");
        StorageReference fileRef = storageRef.child(user.getUid() + "." + getFileExtension(imgUri));

        //Upload image to Firebase Storage
        fileRef.putFile(imgUri)
                .addOnSuccessListener(taskSnapshot -> {
                    //Get image download url
                    fileRef.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                user.setPhotoUrl(String.valueOf(uri));

                                DbUsers dbUsers = new DbUsers();
                                dbUsers.addUser(user)
                                        .addOnSuccessListener(s -> {
                                            mDiaLoading.dismiss();

                                            mDiaResult = new DialogResult(this, () -> {
                                                mFirebaseAuth.signOut();
                                                mDiaResult.dismiss();
                                                finish();
                                            });
                                            mDiaResult.hideTitle();
                                            mDiaResult.setMessage(
                                                    "Your account has been successfully created.");

                                            mHandler.postDelayed(() ->
                                                    mDiaResult.show(), mTransition);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        resultLauncher = null;
    }
}
