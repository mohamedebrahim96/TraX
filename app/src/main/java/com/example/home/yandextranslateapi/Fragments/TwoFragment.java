package com.example.home.yandextranslateapi.Fragments;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.home.yandextranslateapi.R;
import com.example.home.yandextranslateapi.model.ApiInterface;
import com.example.home.yandextranslateapi.model.Translate;
import com.example.home.yandextranslateapi.model.retrofit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Home on 2017-05-02.
 */

public class TwoFragment extends Fragment implements View.OnClickListener{

    public final static String API_KEY = "trnsl.1.1.20170430T032408Z.e362fa2d40c20cd4.045bf84463e8aad182474966f437027f6072c69e";

    TextView text;
    EditText edit_text;
    String f;
    Button ar,de,es;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_two, container, false);
        text = (TextView) view.findViewById(R.id.text);
        edit_text = (EditText) view.findViewById(R.id.edit_text);


        ar = (Button)view.findViewById(R.id.ar);
        ar.setOnClickListener(this);
        de = (Button)view.findViewById(R.id.de);
        de.setOnClickListener(this);
        es = (Button)view.findViewById(R.id.es);
        es.setOnClickListener(this);

        // Inflate the layout for this fragment
        return view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ar: {
                trans("ar");
                break;
            }

            case R.id.de: {
                trans("de");
                break;
            }
            case R.id.es: {
                trans("tt");
                break;
            }

        }
    }

    public void trans(String s)
    {
        ApiInterface apiService = retrofit.getClient().create(ApiInterface.class);
        f = edit_text.getText().toString();
        Call<Translate> call = apiService.getTranslation(API_KEY,f,s);
        call.enqueue(new Callback<Translate>() {
            @Override
            public void onResponse(Call<Translate>call, Response<Translate> response) {
                String word = response.body().getText().get(0).toString();
                text.setText(word);
            }
            @Override
            public void onFailure(Call<Translate>call, Throwable t) {

            }
        });
    }
}
