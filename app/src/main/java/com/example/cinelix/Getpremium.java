package com.example.cinelix;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.airbnb.lottie.LottieAnimationView;

public class Getpremium extends AppCompatActivity {
    LottieAnimationView Lottie;
    Button pay;

    private static final int TEZ_REQUEST_CODE = 123;

    private static final String GOOGLE_TEZ_PACKAGE_NAME = "com.google.android.apps.nbu.paisa.user";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getpremium);

        Lottie=findViewById(R.id.Lottie);
        pay=findViewById(R.id.pay);

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri =
                        new Uri.Builder()
                                .scheme("upi")
                                .authority("pay")
                                .appendQueryParameter("pa", "princeiitbombay-1@okaxis")
                                .appendQueryParameter("pn", "Prince")
//                                .appendQueryParameter("mc", "7481050951")
//                                .appendQueryParameter("tr", String.valueOf(System.currentTimeMillis()))
                                .appendQueryParameter("tn", "Mooviz premium")
                                .appendQueryParameter("am", "11")
                                .appendQueryParameter("cu", "INR")
                                .build();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(uri);
                intent.setPackage(GOOGLE_TEZ_PACKAGE_NAME);
                startActivityForResult(intent, TEZ_REQUEST_CODE);
            }
        });
    }
}