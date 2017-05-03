package com.example.home.yandextranslateapi.model;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Home on 2017-04-30.
 */

public interface ApiInterface {
    @GET("api/v1.5/tr.json/translate")
    Call<Translate> getTranslation(@Query("key") String apiKey, @Query("text") String text, @Query("lang") String lang);
}
