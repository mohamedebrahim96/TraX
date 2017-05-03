package com.example.home.yandextranslateapi.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Home on 2017-04-30.
 */

public class Translate {
    @SerializedName("text")
    public List<String> text ;

    public List<String> getText() {
        return text;
    }

    public void setText(List<String> text) {
        this.text = text;
    }
}
