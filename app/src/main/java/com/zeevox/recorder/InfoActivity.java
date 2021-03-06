package com.zeevox.recorder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

public class InfoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String buildType = BuildConfig.BUILD_TYPE;
        switch (buildType) {
            case "dogfood":
                setContentView(R.layout.activity_info_dogfood);
                fabSetup();
                break;
            case "beta":
                setContentView(R.layout.activity_info_beta);
                fabSetup();
                break;
            case "debug":
                dialogError();
                break;
            default:
                returnToMain();
                break;
        }
    }

    public void fabSetup() {
        FloatingActionButton floatingActionButtonDone = (FloatingActionButton) findViewById(R.id.infoFABDone);
        floatingActionButtonDone.setClickable(true);
        floatingActionButtonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnToMain();
            }
        });
    }
    public void returnToMain() {
        Intent MainActivityIntent = new Intent(InfoActivity.this, MainActivity.class);
        startActivity(MainActivityIntent);
        finish();
    }

    public void dialogError() {
        new MaterialDialog.Builder(this)
                .title(R.string.dialog_info_title)
                .content(R.string.dialog_info_content)
                .positiveText(R.string.action_yes)
                .negativeText(R.string.action_no_thanks)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        returnToMain();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        dialogChooseActivity();
                    }
                })
                .show();
    }

    public void dialogChooseActivity() {
        new MaterialDialog.Builder(this)
                .canceledOnTouchOutside(false)
                .title(R.string.dialog_info_choose_title)
                .content(R.string.dialog_info_choose_content)
                .positiveText(R.string.dialog_info_choose_positive)
                .neutralText(R.string.dialog_info_choose_neutral)
                .negativeText(R.string.dialog_info_choose_negative)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        setContentView(R.layout.activity_info_beta);
                        fabSetup();
                        dialog.dismiss();
                    }
                })
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        returnToMain();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        setContentView(R.layout.activity_info_dogfood);
                        fabSetup();
                        dialog.dismiss();
                    }
                })
                .show();
    }
}
