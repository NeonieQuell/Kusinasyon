package com.neoniequellponce.kusinasyon.utility;

import android.content.Context;

import com.neoniequellponce.kusinasyon.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UtilTextArea {

    private UtilTextArea() {

    }

    public static List<String> format(Context context, String text) {
        String[] tempArr = text.trim().split("\n");

        //Put input string array to arraylist object
        List<String> tempList = new ArrayList<>(Arrays.asList(tempArr));
        List<String> finalList = new ArrayList<>();

        String splitter = context.getString(R.string.splitter_sign);

        //Get only the strings starting on a specific character
        for (int i = 0; i < tempList.size(); i++) {
            if (tempList.get(i).startsWith(splitter)) finalList.add(tempList.get(i).trim());
        }

        //Remove # after getting the qualified strings
        for (int i = 0; i < finalList.size(); i++) {
            if (finalList.get(i).contains(splitter)) {
                finalList.set(i, finalList.get(i).replaceAll(splitter, ""));
            }
        }

        return finalList;
    }
}
