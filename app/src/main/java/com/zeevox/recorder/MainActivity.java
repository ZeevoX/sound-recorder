package com.zeevox.recorder;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

import com.github.fafaldo.fabtoolbar.widget.FABToolbarLayout;
public class MainActivity extends AppCompatActivity {

    public boolean recordingState = false;
    private MediaRecorder mRecorder = null;
    public static final int REQUEST_MULTIPLE_PERMISSIONS_ID = 456;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*if (BuildConfig.BUILD_TYPE == "dogfood" || BuildConfig.BUILD_TYPE == "beta") {
            Intent infoIntent = new Intent(MainActivity.this, InfoActivity.class);
            startActivity(infoIntent);
        }*/
        final FABToolbarLayout layout = (FABToolbarLayout) findViewById(R.id.fabtoolbar);
        FloatingActionButton fabRecord = (FloatingActionButton) findViewById(R.id.fabtoolbar_fab);
        tutorialFAB();
        ImageView one = (ImageView) findViewById(R.id.one);
        one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layout.hide();
            }
        });
        ImageView two = (ImageView) findViewById(R.id.two);
        two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layout.hide();
            }
        });
        ImageView three = (ImageView) findViewById(R.id.three);
        three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopRecording();
                layout.hide();
            }
        });
        fabRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_DENIED && ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_DENIED && ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_DENIED) {
                    startRecording();
                    layout.show();
                } else {
                    permissionsCheckRecording();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {

            case R.id.menu_item_icon_settings:
                Intent settingsIconIntent = new Intent(MainActivity.this, com.zeevox.recorder.SettingsActivity.class);
                startActivity(settingsIconIntent);
                return true;

            case R.id.menu_item_help:
                Intent helpIntent = new Intent(MainActivity.this, com.zeevox.recorder.HelpActivity.class);
                startActivity(helpIntent);
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void tutorialFAB() {

        new MaterialTapTargetPrompt.Builder(MainActivity.this, R.style.AppTheme)
                .setTarget(findViewById(R.id.fabtoolbar_fab))
                .setPrimaryText(R.string.tutorial_fabrecord_title)
                .setBackgroundColourFromRes(R.color.colorAccent)
                .setSecondaryText(R.string.tutorial_fabrecord_subtext)
                .setOnHidePromptListener(new MaterialTapTargetPrompt.OnHidePromptListener() {
                    @Override
                    public void onHidePrompt(MotionEvent event, boolean tappedTarget) {
                        //DUN DUN DUN
                    }

                    @Override
                    public void onHidePromptComplete() {

                    }
                })
                .show();
    }

    public void startRecording() {
        recordingState = true;
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        String tempFileName = Environment.getExternalStorageDirectory() + File.separator + "temp.tmp";
        mRecorder.setOutputFile(tempFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);

        try {

            mRecorder.prepare();
            mRecorder.start();
            recordingState=true;

        } catch (IOException e) {

            e.printStackTrace();

        }
    }

    public void stopRecording() {
        mRecorder.stop();
        recordingState=false;
        dialogRename();
    }

    public void permissionsCheckRecording() {

        int permissionStorageWrite = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionStorageRead = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionRecordAudio = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO);

        if (permissionRecordAudio == PackageManager.PERMISSION_DENIED || permissionStorageRead == PackageManager.PERMISSION_DENIED || permissionStorageWrite == PackageManager.PERMISSION_DENIED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.RECORD_AUDIO)
                    || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                //Show permission explanation
                new MaterialDialog.Builder(this)
                        .content(R.string.dialog_permissions_record_content)
                        .positiveText(R.string.action_ok)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                permissionsRequestRecording();
                            }
                        })
                        .show();

            } else {

                //Request needed permissions
                ActivityCompat.requestPermissions(this, new String[]{
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_MULTIPLE_PERMISSIONS_ID);
            }
        } else {
            startRecording();
        }
    }

    public void permissionsRequestRecording() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_MULTIPLE_PERMISSIONS_ID);
    }


    public void dialogRename() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int second = c.get(Calendar.SECOND);
        final String fileNameDefault = "SoundRecording " + year + "" + month + "" + day + " " + hour + "" + minute + "" + second;
        final String[] name = {""};
        new MaterialDialog.Builder(this)
                .title(R.string.dialog_rename_file_title)
                .content(R.string.dialog_rename_file_content)
                .alwaysCallInputCallback()
                .inputRangeRes(1, 60, R.color.colorAccent)
                .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME | InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                .input("Enter a filename...", "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        //PREVENT NULL FILENAME
                        if (input.toString().isEmpty()){
                            dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                        } else {
                            dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                        }
                        if (input.length()>60) {
                            dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                        } else {
                            dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                        }
                        name[0] = input.toString();
                    }
                })
                .positiveText(R.string.action_save)
                .canceledOnTouchOutside(false)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Log.d("Dialog", "positive");
                        File directory = new File(Environment.getExternalStorageDirectory() + File.separator + "Recordings");
                        directory.mkdirs();
                        String userInput = name[0];
                        Log.d("File", userInput);
                        File temp = new File(Environment.getExternalStorageDirectory() + File.separator, "temp.tmp");
                        File dest = new File(Environment.getExternalStorageDirectory() + File.separator + "Recordings" + File.separator, userInput + ".3gpp");
                        temp.renameTo(dest);
                    }
                })
                .negativeText(R.string.action_delete)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Log.d("Dialog", "negative");
                        //DELETES THE TEMP FILE
                        File file = new File(Environment.getExternalStorageDirectory() + File.separator, "temp.tmp");
                        boolean deleted = file.delete();
                    }
                })
                .show();
    }
}