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
import com.neoniequellponce.kusinasyon.model.ModelCircle;
import com.neoniequellponce.kusinasyon.model.ModelCircleMember;
import com.neoniequellponce.kusinasyon.model.ModelRecipe;
import com.neoniequellponce.kusinasyon.model.ModelSharedRecipe;
import com.neoniequellponce.kusinasyon.model.ModelUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DbCircle {

    private FirebaseDatabase mDatabase;
    private DatabaseReference mDbRef;

    private DbUsers mDbUsers;
    private List<ModelRecipe> mSharedRecipeList;

    public DbCircle() {
        mDatabase = FirebaseDatabase.getInstance();
        mDbRef = mDatabase.getReference("circles");

        mDbUsers = new DbUsers();
        mSharedRecipeList = new ArrayList<>();
    }

    //region Tasks
    public Task<Void> createCircle(ModelCircle circle) {
        return mDbRef.child(circle.getKey()).setValue(circle);
    }

    public Task<Void> addMember(ModelCircleMember member) {
        return mDbRef.child(member.getCircleKey()).child("members")
                .child(member.getUid()).setValue(member);
    }

    public Task<Void> updateCircleName(String circleKey, String circleName) {
        return mDbRef.child(circleKey).child("name").setValue(circleName);
    }

    public Task<Void> leaveCircle(String circleKey, String memberUid) {
        return mDbRef.child(circleKey).child("members").child(memberUid).removeValue();
    }

    public Task<Void> deleteCircle(String circleKey) {
        return mDbRef.child(circleKey).removeValue();
    }

    public Task<Void> deleteRecipe(String circleKey, String recipeKey) {
        return mDbRef.child(circleKey).child("recipes").child(recipeKey).removeValue();
    }
    //endregion

    //region Methods
    public String getKey() {
        return mDbRef.push().getKey();
    }

    public void getCircles(FirebaseAuth auth, OnCirclesGetListener listener) {
        List<ModelCircle> circleList = new ArrayList<>();

        mDbRef.orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                circleList.clear();

                for (DataSnapshot circleDs : snapshot.getChildren()) {
                    if (circleDs.child("members").child(auth.getUid()).exists()) {
                        ModelCircle circle = circleDs.getValue(ModelCircle.class);
                        circleList.add(circle);
                    }
                }

                listener.getCircles(circleList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getCircleMembers(String circleKey, OnCircleMembersGetListener listener) {
        List<String> memberKeys = new ArrayList<>();

        mDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                memberKeys.clear();

                for (DataSnapshot circleDs : snapshot.getChildren()) {
                    //Get circle members' keys
                    if (Objects.equals(circleDs.getKey(), circleKey)) {
                        for (DataSnapshot memberDs : circleDs.child("members").getChildren()) {
                            memberKeys.add(memberDs.getKey());
                        }

                        getCircleMembersHelper(memberKeys, listener);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getCircleMembersHelper(List<String> memberKeys,
                                        OnCircleMembersGetListener listener) {
        List<ModelUser> memberList = new ArrayList<>();

        //Get member info from users collection using members' keys
        DatabaseReference dbRef = mDatabase.getReference("users");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                memberList.clear();

                for (int i = 0; i < memberKeys.size(); i++) {
                    for (DataSnapshot memberDs : snapshot.getChildren()) {
                        if (Objects.equals(memberDs.getKey(), memberKeys.get(i))) {
                            ModelUser member = memberDs.getValue(ModelUser.class);
                            memberList.add(member);
                        }
                    }
                }

                listener.getCircleMembers(memberList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void joinCircle(String inviteCode, OnCircleMemberAddListener listener) {
        List<ModelCircleMember> memberList = new ArrayList<>();

        mDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                memberList.clear();

                boolean exist = false;
                ModelCircle circle = null;

                for (DataSnapshot circleDs : snapshot.getChildren()) {
                    String code = String.valueOf(circleDs.child("inviteCode").getValue());

                    //Check if input is equal to circle's invite code
                    if (code.equals(inviteCode)) {
                        //Get members of the matched circle
                        for (DataSnapshot memberDs : circleDs.child("members").getChildren()) {
                            ModelCircleMember member = memberDs.getValue(ModelCircleMember.class);
                            memberList.add(member);
                        }

                        exist = true;

                        //Get the circle to be joined by the user
                        circle = circleDs.getValue(ModelCircle.class);
                        break;
                    }
                }

                listener.onAddCircleMember(exist, memberList, circle);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getCircleAdmin(String circleKey, OnCircleAdminGetListener listener) {
        mDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot circleDs : snapshot.getChildren()) {
                    String resultKey = String.valueOf(circleDs.child("key").getValue());

                    if (resultKey.equals(circleKey)) {
                        String adminUid = String.valueOf(circleDs.child("admin").getValue());

                        DatabaseReference databaseRef = mDatabase.getReference("users");
                        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                ModelUser admin = null;

                                for (DataSnapshot userDs : snapshot.getChildren()) {
                                    if (adminUid.equals(userDs.getKey())) {
                                        admin = userDs.getValue(ModelUser.class);
                                        break;
                                    }
                                }

                                listener.getCircleAdmin(admin);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void shareRecipe(ModelRecipe recipe, String circleKey,
                            OnRecipeShareListener listener) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;

        mDbUsers.getDbRef().child(user.getUid()).child("recipes")
                .child(recipe.getKey()).setValue(recipe)
                .addOnSuccessListener(s -> {
                    ModelSharedRecipe sharedRecipe = new ModelSharedRecipe();
                    sharedRecipe.setKey(recipe.getKey());
                    sharedRecipe.setAuthorUid(recipe.getAuthorUid());

                    mDbRef.child(circleKey).child("recipes")
                            .child(sharedRecipe.getKey()).setValue(sharedRecipe)
                            .addOnSuccessListener(s1 -> listener.onRecipeShare(true))
                            .addOnFailureListener(e -> {
                                listener.onRecipeShare(false);
                                e.printStackTrace();
                            });
                })
                .addOnFailureListener(e -> {
                    listener.onRecipeShare(false);
                    e.printStackTrace();
                });
    }

    public void getSharedRecipes(String circleKey, OnSharedRecipesGetListener listener) {
        List<ModelSharedRecipe> sharedRecipeList = new ArrayList<>();

        mDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sharedRecipeList.clear();

                //Get circles
                for (DataSnapshot circleDs : snapshot.getChildren()) {
                    //Check if current circle snapshot is the intended circle destination
                    if (Objects.equals(circleDs.getKey(), circleKey)) {
                        /*Check if circle has "recipes" child key
                         *If yes, get shared recipes
                         *If no, return empty list*/
                        if (circleDs.hasChild("recipes")) {
                            //Get shared recipe model in circle
                            for (DataSnapshot circleRecipeDs :
                                    circleDs.child("recipes").getChildren()) {
                                ModelSharedRecipe sharedRecipe;
                                sharedRecipe = circleRecipeDs.getValue(ModelSharedRecipe.class);
                                sharedRecipeList.add(sharedRecipe);
                            }

                            //Get full recipe in user collection
                            getSharedRecipesHelper(sharedRecipeList, listener);
                        } else {
                            mSharedRecipeList.clear();
                            listener.getSharedRecipes(mSharedRecipeList);
                        }

                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getSharedRecipesHelper(List<ModelSharedRecipe> sharedRecipeList,
                                        OnSharedRecipesGetListener listener) {
        mDbUsers.getDbRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mSharedRecipeList.clear();

                //Get user snapshot
                for (DataSnapshot userDs : snapshot.getChildren()) {
                    //Loop through sharedRecipeList
                    for (ModelSharedRecipe sharedRecipe : sharedRecipeList) {
                        //Check if current author of shared recipe is equal to current user snapshot
                        if (Objects.equals(sharedRecipe.getAuthorUid(), userDs.getKey())) {
                            //Loop through recipes of the user
                            for (DataSnapshot userRecipeDs :
                                    userDs.child("recipes").getChildren()) {
                                /*Check if the recipe of the user is equal to
                                 *the recipe in the circle*/
                                if (Objects.equals(sharedRecipe.getKey(), userRecipeDs.getKey())) {
                                    ModelRecipe recipe;
                                    recipe = userRecipeDs.getValue(ModelRecipe.class);
                                    assert recipe != null;

                                    if (Objects.equals(recipe.getVisibility(), "visible")) {
                                        mSharedRecipeList.add(recipe);
                                    }
                                }
                            }
                        }
                    }
                }
                
                listener.getSharedRecipes(mSharedRecipeList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    //endregion

    //region Interfaces
    public interface OnCirclesGetListener {
        void getCircles(List<ModelCircle> circleList);
    }

    public interface OnCircleMembersGetListener {
        void getCircleMembers(List<ModelUser> memberList);
    }

    public interface OnCircleMemberAddListener {
        void onAddCircleMember(boolean circleExist, List<ModelCircleMember> memberList,
                               ModelCircle circle);
    }

    public interface OnCircleAdminGetListener {
        void getCircleAdmin(ModelUser admin);
    }

    public interface OnRecipeShareListener {
        void onRecipeShare(boolean shared);
    }

    public interface OnSharedRecipesGetListener {
        void getSharedRecipes(List<ModelRecipe> recipeList);
    }
    //endregion
}
