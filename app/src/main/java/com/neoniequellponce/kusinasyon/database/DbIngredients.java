package com.neoniequellponce.kusinasyon.database;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.neoniequellponce.kusinasyon.model.ModelIngredientGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DbIngredients {

    private DatabaseReference mDbRef;

    public DbIngredients() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mDbRef = database.getReference("ingredients");
    }

    public void getIngredients(OnIngredientGroupsGetListener listener) {
        List<ModelIngredientGroup> ingredGroupList = new ArrayList<>();

        mDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ingredGroupList.clear();

                for (DataSnapshot ingredGroupDs : snapshot.getChildren()) {
                    ModelIngredientGroup ingredGroup;
                    ingredGroup = ingredGroupDs.getValue(ModelIngredientGroup.class);
                    assert ingredGroup != null;

                    Collections.sort(ingredGroup.getMembers());
                    ingredGroupList.add(ingredGroup);
                }

                listener.getIngredientGroups(ingredGroupList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public interface OnIngredientGroupsGetListener {
        void getIngredientGroups(List<ModelIngredientGroup> ingredientGroupList);
    }
}
