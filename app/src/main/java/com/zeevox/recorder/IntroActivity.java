package com.zeevox.recorder;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.heinrichreimersoftware.materialintro.app.NavigationPolicy;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;

public class IntroActivity extends com.heinrichreimersoftware.materialintro.app.IntroActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setFullscreen(false);
        setSkipEnabled(false);
        setFinishEnabled(true);
        setFinishOnTouchOutside(false);
        setNavigationPolicy(new NavigationPolicy() {
            @Override
            public boolean canGoForward(int i) {
                return true;
            }

            @Override
            public boolean canGoBackward(int i) {
                return true;
            }
        });
        addSlide(new SimpleSlide.Builder()
                .title(R.string.intro_welcome_title)
                .description(R.string.intro_welcome_description)
                .background(R.color.colorAccentDark)
                .backgroundDark(R.color.colorAccentDarkDark)
                .build());
        addSlide(new SimpleSlide.Builder()
                .title(R.string.intro_responsive_title)
                .description(R.string.intro_responsive_description)
                .image(R.drawable.responsive)
                .background(R.color.colorIntroResponsiveBackground)
                .backgroundDark(R.color.colorIntroResponsiveBackgroundDark)
                .build());
        addSlide(new SimpleSlide.Builder()
                .title(R.string.intro_personalize_title)
                .description(R.string.intro_personalize_description)
                .image(R.drawable.personalize)
                .background(R.color.colorIntroPersonalizeBackground)
                .backgroundDark(R.color.colorIntroPersonalizeBackgroundDark)
                .build());
        boolean permissionCheckStorageW = ContextCompat.checkSelfPermission(IntroActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED;
        boolean permissionCheckStorageR = ContextCompat.checkSelfPermission(IntroActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED;
        boolean permissionCheckRecordAudio = ContextCompat.checkSelfPermission(IntroActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED;
        if (permissionCheckStorageR || permissionCheckStorageW) {
            addSlide(new SimpleSlide.Builder()
                    .title(R.string.intro_permission_storage_title)
                    .description(R.string.intro_permission_storage_description)
                    .image(R.drawable.permission_storage)
                    .background(R.color.colorIntroStorageBackground)
                    .backgroundDark(R.color.colorIntroStorageBackgroundDark)
                    .permissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
                    .build());
        }
        if (permissionCheckRecordAudio) {
            addSlide(new SimpleSlide.Builder()
                    .title(R.string.intro_permission_record_audio_title)
                    .description(R.string.intro_permission_record_audio_description)
                    .image(R.drawable.permission_audio)
                    .background(R.color.colorIntroAudioBackground)
                    .backgroundDark(R.color.colorIntroAudioBackgroundDark)
                    .permission(Manifest.permission.RECORD_AUDIO)
                    .build());
        }
    }
}
