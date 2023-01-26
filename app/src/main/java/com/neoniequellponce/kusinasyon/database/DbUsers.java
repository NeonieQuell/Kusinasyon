package com.neoniequellponce.kusinasyon.database;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.neoniequellponce.kusinasyon.model.ModelRecipe;
import com.neoniequellponce.kusinasyon.model.ModelUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DbUsers {

    private DatabaseReference mDbRef;

    public DbUsers() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        mDbRef = firebaseDatabase.getReference("users");
    }

    //region Tasks
    public Task<Void> addUser(ModelUser user) {
        return mDbRef.child(user.getUid()).setValue(user);
    }

    public Task<Void> updateRecipe(ModelRecipe recipe) {
        return mDbRef.child(recipe.getAuthorUid()).child("recipes")
                .child(recipe.getKey()).setValue(recipe);
    }

    public Task<Void> deleteRecipe(ModelRecipe recipe) {
        return mDbRef.child(recipe.getAuthorUid()).child("recipes")
                .child(recipe.getKey()).removeValue();
    }

    public Task<Void> voteRecipe(ModelRecipe recipe) {
        return mDbRef.child(recipe.getAuthorUid()).child("recipes")
                .child(recipe.getKey()).setValue(recipe);
    }

    public Task<Void> rejectRecipe(ModelRecipe recipe) {
        return mDbRef.child(recipe.getAuthorUid()).child("recipes").child(recipe.getKey())
                .child("status").setValue("rejected");
    }
    //endregion

    //region Methods
    public String getKey() {
        return mDbRef.push().getKey();
    }

    public DatabaseReference getDbRef() {
        return mDbRef;
    }

    public void checkIfUserExist(OnUserExistListener listener) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;

        mDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listener.userExist(snapshot.hasChild(user.getUid()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getUserInfo(OnUserInfoGetListener listener) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;

        mDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listener.getUserInfo(snapshot.child(user.getUid()).getValue(ModelUser.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getUserRecipes(String uid, String privacy,
                               OnUserRecipesGetListener listener) {
        List<ModelRecipe> recipeList = new ArrayList<>();

        mDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                recipeList.clear();

                for (DataSnapshot userDs : snapshot.getChildren()) {
                    if (Objects.equals(userDs.getKey(), uid)) {
                        for (DataSnapshot recipeSnapshot :
                                userDs.child("recipes").getChildren()) {
                            ModelRecipe recipe = recipeSnapshot.getValue(ModelRecipe.class);
                            assert recipe != null;

                            if (recipe.getPrivacy().equalsIgnoreCase(privacy)) {
                                recipeList.add(recipe);
                            }
                        }

                        break;
                    }
                }

                listener.onGetUserRecipe(recipeList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void sharePublicRecipe(ModelRecipe recipe, OnRecipeShareListener listener) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;

        mDbRef.child(user.getUid()).child("recipes").child(recipe.getKey()).setValue(recipe)
                .addOnSuccessListener(s -> listener.onRecipeShare(true))
                .addOnFailureListener(e -> {
                    listener.onRecipeShare(false);
                    e.printStackTrace();
                });
    }

    public void getRecipeApproval(OnRecipeApprovalGetListener listener) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        List<ModelRecipe> recipeList = new ArrayList<>();

        mDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                recipeList.clear();
                for (DataSnapshot userDs : snapshot.getChildren()) {
                    for (DataSnapshot recipeDs :
                            userDs.child("recipes").getChildren()) {
                        if (recipeDs.hasChild("status")) {
                            //Check if recipe status is (for approval)
                            if (Objects.equals(recipeDs.child("status").getValue(),
                                    "for approval")) {
                                ModelRecipe recipe = recipeDs.getValue(ModelRecipe.class);
                                assert recipe != null;

                                if (Objects.equals(recipe.getVisibility(), "visible")) {
                                    //Check if user is one of the voters
                                    if (recipe.getVoters().contains(auth.getUid())) {
                                        if (recipeDs.hasChild("approvers")) {
                                            //Check if user is not present in approvers
                                            if (!recipe.getApprovers().contains(auth.getUid())) {
                                                recipeList.add(recipe);
                                            }
                                        } else if (recipeDs.hasChild("rejectors")) {
                                            //Check if user is not present in rejectors
                                            if (!recipe.getRejectors().contains(auth.getUid())) {
                                                recipeList.add(recipe);
                                            }
                                        } else {
                                            //Add recipe if it has no approver or rejector yet
                                            recipeList.add(recipe);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                listener.getRecipeApproval(recipeList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getApproversAndRejectors(ModelRecipe recipe,
                                         OnApproversAndRejectorsGetListener listener) {
        List<String> approverList = new ArrayList<>();
        List<String> rejectorList = new ArrayList<>();

        mDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                approverList.clear();
                rejectorList.clear();

                //Get approvers
                for (DataSnapshot approverDs : snapshot.child(recipe.getAuthorUid())
                        .child("recipes").child(recipe.getKey()).child("approvers").getChildren()) {
                    String approver = approverDs.getValue(String.class);
                    approverList.add(approver);
                }

                //Get rejectors
                for (DataSnapshot rejectorDs : snapshot.child(recipe.getAuthorUid())
                        .child("recipes").child(recipe.getKey()).child("rejectors").getChildren()) {
                    String rejector = rejectorDs.getValue(String.class);
                    rejectorList.add(rejector);
                }

                listener.getApproversAndRejectors(approverList, rejectorList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getVoters(ModelRecipe recipe, OnAllVotersGetListener listener) {
        List<String> voterList = new ArrayList<>();

        mDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                voterList.clear();

                for (DataSnapshot voterDs : snapshot.child(recipe.getAuthorUid())
                        .child("recipes").child(recipe.getKey()).child("voters").getChildren()) {
                    String voter = voterDs.getValue(String.class);
                    voterList.add(voter);
                }

                listener.getAllVoters(voterList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    //endregion

    //region Interfaces
    public interface OnUserExistListener {
        void userExist(boolean exist);
    }

    public interface OnUserInfoGetListener {
        void getUserInfo(ModelUser user);
    }

    public interface OnUserRecipesGetListener {
        void onGetUserRecipe(List<ModelRecipe> recipeList);
    }

    public interface OnRecipeShareListener {
        void onRecipeShare(boolean shared);
    }

    public interface OnRecipeApprovalGetListener {
        void getRecipeApproval(List<ModelRecipe> recipeList);
    }

    public interface OnApproversAndRejectorsGetListener {
        void getApproversAndRejectors(List<String> approverList, List<String> rejectorList);
    }

    public interface OnAllVotersGetListener {
        void getAllVoters(List<String> voterList);
    }
    //endregion
}
