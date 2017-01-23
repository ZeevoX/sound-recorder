package com.zeevox.recorder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreenActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        int SPLASH_TIME_OUT = 700;
        new Handler().postDelayed(() -> {
            if (BuildConfig.BUILD_TYPE.contentEquals("beta") ||  BuildConfig.BUILD_TYPE.contentEquals("dogfood")) {
                Intent infoIntent = new Intent(SplashScreenActivity.this, InfoActivity.class);
                startActivity(infoIntent);
                finish();
            } else {
                Intent mainIntent = new Intent(SplashScreenActivity.this, MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
