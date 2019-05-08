package com.example.yeabkalwubshit.marketplace.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.yeabkalwubshit.marketplace.R;
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

        // todo(yeabkal) make `Animator` class free of Thread issues
        /*
        final Animator animator = new Animator(layout, introText);
        animator.start();
         */

        new Handler().postDelayed(new Runnable() {
            public void run() {
                Intent homePageInent = new Intent(
                        IntroSplashScreen.this,
                        LoginActivity.class
                );
                if(FirebaseAuth.getInstance().getCurrentUser() != null) {
                    openFeeds();
                    return;
                }
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
