package com.neoniequellponce.kusinasyon.database;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.neoniequellponce.kusinasyon.model.ModelRecipe;
import com.neoniequellponce.kusinasyon.model.ModelSharedRecipe;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class DbPublicRecipes {

    private DatabaseReference mDbRefPubRec;
    private DatabaseReference mDbRefSysRec;

    private DbUsers mDbUsers;

    public DbPublicRecipes() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mDbRefPubRec = database.getReference("publicRecipes");
        mDbRefSysRec = database.getReference("systemRecipes");

        mDbUsers = new DbUsers();
    }

    public void getRecipesByOrigin(String origin, OnPublicRecipesGetListener listener) {
        mDbRefPubRec.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot originDs : snapshot.getChildren()) {
                    //Check if input origin is equal to current snapshot
                    if (String.valueOf(originDs.child("origin").getValue())
                            .equalsIgnoreCase(origin)) {

                        List<String> sysRecipeKeyList = new ArrayList<>();
                        List<ModelSharedRecipe> sharedRecipeList = new ArrayList<>();

                        //Get key of recipes
                        for (DataSnapshot recipeDs : originDs.child("recipes").getChildren()) {
                            /*Case 1: System Recipes
                             *Case 2: User Recipes*/
                            if (!recipeDs.hasChild("authorUid")) {
                                String key = recipeDs.getKey();
                                sysRecipeKeyList.add(key);
                            } else {
                                ModelSharedRecipe sharedRecipe;
                                sharedRecipe = recipeDs.getValue(ModelSharedRecipe.class);
                                sharedRecipeList.add(sharedRecipe);
                            }
                        }
                        //Get recipes in systemRecipes collection
                        getSystemRecipes(sysRecipeKeyList, listener);

                        //Get public shared recipes from users
                        if (!sharedRecipeList.isEmpty()) {
                            getUserRecipes(sharedRecipeList, listener);
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

    private void getSystemRecipes(List<String> sysRecipeKeyList,
                                  OnPublicRecipesGetListener listener) {
        List<ModelRecipe> sysRecipeList = new ArrayList<>();

        mDbRefSysRec.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sysRecipeList.clear();

                for (DataSnapshot recipeDs : snapshot.getChildren()) {
                    for (String key : sysRecipeKeyList) {
                        if (Objects.equals(recipeDs.getKey(), key)) {
                            ModelRecipe recipe = recipeDs.getValue(ModelRecipe.class);
                            sysRecipeList.add(recipe);
                        }
                    }
                }

                Comparator<ModelRecipe> comparator = Comparator.comparing(ModelRecipe::getName);
                sysRecipeList.sort(comparator);
                listener.getSystemRecipes(sysRecipeList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getUserRecipes(List<ModelSharedRecipe> sharedRecipeList,
                                OnPublicRecipesGetListener listener) {
        List<ModelRecipe> userRecipeList = new ArrayList<>();

        mDbUsers.getDbRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userRecipeList.clear();

                for (DataSnapshot userDs : snapshot.getChildren()) {
                    for (ModelSharedRecipe sharedRecipe : sharedRecipeList) {
                        if (Objects.equals(userDs.getKey(), sharedRecipe.getAuthorUid())) {
                            for (DataSnapshot recipeDs : userDs.child("recipes").getChildren()) {
                                if (Objects.equals(recipeDs.getKey(), sharedRecipe.getKey())) {
                                    ModelRecipe recipe = recipeDs.getValue(ModelRecipe.class);
                                    assert recipe != null;

                                    if (Objects.equals(recipe.getVisibility(), "visible")) {
                                        userRecipeList.add(recipe);
                                    }
                                }
                            }
                        }
                    }
                }

                listener.getUserRecipes(userRecipeList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getAllRecipes(OnPublicRecipesGetListener listener) {
        mDbRefPubRec.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> sysRecipeKeyList = new ArrayList<>();
                List<ModelSharedRecipe> sharedRecipeList = new ArrayList<>();

                for (DataSnapshot originDs : snapshot.getChildren()) {
                    for (DataSnapshot recipeDs : originDs.child("recipes").getChildren()) {
                        /*Case 1: System Recipes
                         *Case 2: User Recipes*/
                        if (!recipeDs.hasChild("authorUid")) {
                            String key = recipeDs.getKey();
                            sysRecipeKeyList.add(key);
                        } else {
                            ModelSharedRecipe sharedRecipe;
                            sharedRecipe = recipeDs.getValue(ModelSharedRecipe.class);
                            sharedRecipeList.add(sharedRecipe);
                        }
                    }
                }
                //Get recipes in systemRecipes collection
                getSystemRecipes(sysRecipeKeyList, listener);

                //Get public shared recipes from users
                if (!sharedRecipeList.isEmpty()) getUserRecipes(sharedRecipeList, listener);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void deleteRecipe(ModelRecipe recipe, OnRecipeDeleteListener listener) {
        mDbRefPubRec.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot originDs : snapshot.getChildren()) {
                    String originValue = String.valueOf(originDs.child("origin").getValue());

                    if (originValue.equalsIgnoreCase(recipe.getOrigin())) {
                        String key = originDs.getKey();
                        assert key != null;

                        mDbRefPubRec.child(key).child("recipes")
                                .child(recipe.getKey()).removeValue()
                                .addOnSuccessListener(s -> listener.onRecipeDelete(true))
                                .addOnFailureListener(e -> {
                                    e.printStackTrace();
                                    listener.onRecipeDelete(false);
                                });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void addApprovedRecipe(ModelRecipe recipe) {
        mDbRefPubRec.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot originDs : snapshot.getChildren()) {
                    if (Objects.equals(originDs.child("origin").getValue(), recipe.getOrigin())) {
                        ModelSharedRecipe sharedRecipe = new ModelSharedRecipe();
                        sharedRecipe.setKey(recipe.getKey());
                        sharedRecipe.setAuthorUid(recipe.getAuthorUid());

                        String originKey = String.valueOf(originDs.child("key").getValue());

                        mDbRefPubRec.child(originKey).child("recipes")
                                .child(sharedRecipe.getKey()).setValue(sharedRecipe)
                                .addOnSuccessListener(s -> {
                                    //Change recipe status after being approved
                                    DbUsers dbUsers = new DbUsers();
                                    dbUsers.getDbRef().child(recipe.getAuthorUid())
                                            .child("recipes").child(recipe.getKey())
                                            .child("status").setValue("approved");
                                })
                                .addOnFailureListener(Throwable::printStackTrace);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public interface OnPublicRecipesGetListener {
        void getSystemRecipes(List<ModelRecipe> systemRecipeList);

        void getUserRecipes(List<ModelRecipe> userRecipeList);
    }

    public interface OnRecipeDeleteListener {
        void onRecipeDelete(boolean deleted);
    }
}
