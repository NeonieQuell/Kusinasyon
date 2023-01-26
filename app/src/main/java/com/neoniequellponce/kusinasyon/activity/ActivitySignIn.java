package com.neoniequellponce.kusinasyon.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.neoniequellponce.kusinasyon.R;
import com.neoniequellponce.kusinasyon.database.DbUsers;
import com.neoniequellponce.kusinasyon.databinding.ActivitySignInBinding;
import com.neoniequellponce.kusinasyon.dialogs.DialogLoading;
import com.neoniequellponce.kusinasyon.dialogs.DialogResult;
import com.neoniequellponce.kusinasyon.model.ModelUser;

public class ActivitySignIn extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ActivitySignIn";
    private final int REQUEST_CODE = 1000;
    private int mTransition;

    private DbUsers mDbUsers;

    private ActivitySignInBinding mBinding;
    private Handler mHandler;

    private DialogLoading mDialogLoading;
    private DialogResult mDialogResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkLastSignedInAccount();

        mBinding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mTransition = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mDbUsers = new DbUsers();
        mHandler = new Handler(Looper.getMainLooper());
        mDialogLoading = new DialogLoading(this);

        mBinding.btnSignIn.setOnClickListener(this);
        mBinding.btnContinueWithGoogle.setOnClickListener(this);
        mBinding.tvCreateAnAccount.setOnClickListener(this);
    }

    private void checkLastSignedInAccount() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            if (user.isEmailVerified()) updateUI();
        }
    }

    private void updateUI() {
        Intent intent = new Intent(getApplicationContext(), ActivityMain.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sign_in:
                mDialogLoading.show();
                signInUsingCredential();
                break;
            case R.id.btn_continue_with_google:
                mDialogLoading.show();
                continueWithGoogle();
                break;
            case R.id.tv_create_an_account:
                Intent intent = new Intent(this, ActivityCreateAccount.class);
                startActivity(intent);
                break;
        }
    }

    private void signInUsingCredential() {
        String email = String.valueOf(mBinding.etEmail.getText()).trim();
        String password = String.valueOf(mBinding.etPassword.getText()).trim();

        if (email.isEmpty()) {
            mBinding.etEmail.requestFocus();
            Toast.makeText(this, "Email cannot be empty", Toast.LENGTH_SHORT).show();
        } else if (password.isEmpty()) {
            mBinding.etPassword.requestFocus();
            Toast.makeText(this, "Password cannot be empty", Toast.LENGTH_SHORT).show();
        } else signInUsingCredentialHelper(email, password);
    }

    private void signInUsingCredentialHelper(String email, String password) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        assert firebaseUser != null;

                        if (firebaseUser.isEmailVerified()) {
                            ModelUser user = new ModelUser();
                            user.setUid(firebaseUser.getUid());
                            checkIfUserExist(user);
                        } else {
                            firebaseUser.sendEmailVerification()
                                    .addOnSuccessListener(s -> {
                                        mDialogResult = new DialogResult(this,
                                                () -> mDialogResult.dismiss());

                                        mDialogResult.hideTitle();
                                        mDialogResult.setMessage(
                                                "Please check your email's " +
                                                        "inbox or spam folder for verification"
                                        );

                                        mDialogLoading.dismiss();

                                        mHandler.postDelayed(
                                                () -> mDialogResult.show(), mTransition
                                        );
                                    })
                                    .addOnFailureListener(e -> {
                                        mDialogLoading.dismiss();
                                        e.printStackTrace();
                                    });
                            //auth.signOut();
                        }
                    } else {
                        mDialogLoading.dismiss();

                        Exception ex = task.getException();
                        if (ex instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                        }

                        if (ex instanceof FirebaseAuthInvalidUserException) {
                            Toast.makeText(this, "Account does not exist",
                                    Toast.LENGTH_SHORT).show();
                        }

                        Log.d(TAG, "signIn: Task Unsuccessful");
                        Log.d(TAG, "signIn: " + ex);
                    }
                })
                .addOnFailureListener(e -> {
                    mDialogLoading.dismiss();
                    e.printStackTrace();
                });
    }

    private void checkIfUserExist(ModelUser user) {
        mDbUsers.checkIfUserExist(exist -> {
            if (exist) {
                mDialogLoading.dismiss();
                updateUI();
            } else {
                mDbUsers.addUser(user)
                        .addOnSuccessListener(s -> {
                            mDialogLoading.dismiss();
                            updateUI();
                        })
                        .addOnFailureListener(e -> {
                            mDialogLoading.dismiss();
                            e.printStackTrace();
                        });
            }
        });
    }

    private void continueWithGoogle() {
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleSignInClient gsc = GoogleSignIn.getClient(this, gso);
        startActivityForResult(gsc.getSignInIntent(), REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                continueWithGoogleHelper(account);
            } catch (ApiException e) {
                Toast.makeText(this, "Sign in error", Toast.LENGTH_SHORT).show();
                mDialogLoading.dismiss();
                e.printStackTrace();
            }
        }
    }

    private void continueWithGoogleHelper(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        assert firebaseUser != null;

                        ModelUser user = new ModelUser(
                                firebaseUser.getUid(),
                                firebaseUser.getEmail(),
                                firebaseUser.getDisplayName(),
                                String.valueOf(account.getPhotoUrl())
                        );

                        checkIfUserExist(user);
                    } else {
                        mDialogLoading.dismiss();
                        Log.d(TAG, "signIn: Task Unsuccessful");
                        Log.d(TAG, "signIn: " + task.getException());
                    }
                })
                .addOnFailureListener(e -> {
                    mDialogLoading.dismiss();
                    e.printStackTrace();

                });
    }
}
