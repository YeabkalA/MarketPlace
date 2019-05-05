package com.example.yeabkalwubshit.marketplace;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Handler;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class IntroSplashScreen extends Activity {
    private static int SPLASH_TIME_OUT = 4000;
    private LinearLayout layout;
    private TextView introText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_splash_screen);

        layout = findViewById(R.id.mainback);
        introText = findViewById(R.id.introText);

        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            openFeeds();
            return;
        }

        final Animator animator = new Animator();

//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                layout.setBackgroundDrawable(new ColorDrawable(animator.getBackgroundColor()));
//                introText.setBackgroundDrawable(new ColorDrawable(animator.getTextColor()));
//                try {
//                    Thread.sleep(500);
//                } catch(Exception e) {}
//            }
//        });
//        thread.start();

        new Handler().postDelayed(new Runnable() {
            public void run() {
                Intent homePageInent = new Intent(
                        IntroSplashScreen.this,
                        LoginActivity.class
                );
                startActivity(homePageInent);
                finish();

            }
        }, SPLASH_TIME_OUT);
    }

    private void openFeeds() {
        Intent intent = new Intent(IntroSplashScreen.this, Feed.class);
        startActivity(intent);
    }
}
