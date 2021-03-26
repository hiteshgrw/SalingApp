package com.example.saleservice;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class splashscreen extends AppCompatActivity {
    Animation topanim,bottomanim;
    private ImageView imageView;
    private TextView textView,textView1;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splashscreen);
        imageView = findViewById(R.id.ivsplashimg);
        imageView.setClipToOutline(true);
        textView = findViewById(R.id.tvsplashsc1);
        textView1 = findViewById(R.id.tvsplashsc2);
        progressBar = findViewById(R.id.progressBar);
        topanim = AnimationUtils.loadAnimation(this,R.anim.topanim);
        bottomanim = AnimationUtils.loadAnimation(this,R.anim.bottomanim);
        imageView.setAnimation(topanim);
        textView.setAnimation(bottomanim);
        textView1.setAnimation(bottomanim);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
                startActivity(new Intent(splashscreen.this,MainPage.class));
            }
        },5000);
    }
}
