package com.zeevox.recorder;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

public class MainActivity extends AppCompatActivity {

    public boolean recordingState = false;
    private MediaRecorder mRecorder = null;
    public int recordSession = 0;
    public static final int REQUEST_MULTIPLE_PERMISSIONS_ID = 456;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final FloatingActionButton fabRecord = (FloatingActionButton) findViewById(R.id.floatingActionButtonRecord);
        fabRecord.setImageResource(R.drawable.mic_white);
        fabRecord.setBackgroundTintList(new ColorStateList(new int[][]{new int[]{0}}, new int[]{getResources().getColor(R.color.colorAccent)}));
        fabRecord.setOnClickListener(view -> {
            if (!recordingState) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_DENIED && ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_DENIED && ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_DENIED) {
                    startRecording();
                } else {
                    permissionsCheckRecording();
                }
            } else {
                stopRecording();
            }
        });
        tutorialFAB();
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
                .setTarget(findViewById(R.id.floatingActionButtonRecord))
                .setPrimaryText(R.string.tutorial_fabrecord_title)
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
        FloatingActionButton fabRecord = (FloatingActionButton) findViewById(R.id.floatingActionButtonRecord);
        fabRecord.setImageResource(R.drawable.stop_white);
        fabRecord.setBackgroundTintList(new ColorStateList(new int[][]{new int[]{0}}, new int[]{getResources().getColor(R.color.colorAccentDark)}));
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
        FloatingActionButton fabRecord = (FloatingActionButton) findViewById(R.id.floatingActionButtonRecord);
        fabRecord.setImageResource(R.drawable.mic_white);
        fabRecord.setBackgroundTintList(new ColorStateList(new int[][]{new int[]{0}}, new int[]{getResources().getColor(R.color.colorAccent)}));
        recordingState=false;
        dialogRename();
        //renameDialog();
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
                        .onPositive((dialog, which) -> permissionsRequestRecording())
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

    public void dialogBeta() {

        new MaterialDialog.Builder(this)
                .title(R.string.dialog_join_beta_title)
                .content(R.string.dialog_join_beta_content)
                .iconRes(R.drawable.bug)
                .checkBoxPromptRes(R.string.action_dont_ask_again, false, null)
                .negativeText(R.string.action_no_thanks)
                .positiveText(R.string.action_ok)
                .onPositive((dialog, which) -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/apps/testing/com.ezcode.recorder"));
                    startActivity(intent);
                })
                .onNegative((dialog, which) -> dialog.dismiss())
                .show();
    }

    public void dialogErrorRecording() {

        new MaterialDialog.Builder(this)
                .title(R.string.dialog_error_recording_title)
                .content(R.string.dialog_error_recording_content)
                .negativeText(R.string.action_dismiss)
                .positiveText(R.string.action_try_again)
                .onPositive((dialog, which) -> permissionsCheckRecording())
                .onNegative((dialog, which) -> dialog.dismiss())
                .show();
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

        new MaterialDialog.Builder(this)
                .title(R.string.dialog_rename_file_title)
                .content(R.string.dialog_rename_file_content)
                .alwaysCallInputCallback()
                .inputRangeRes(1, 60, R.color.colorAccent)
                .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME | InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                .positiveText(R.string.action_save)
                .canceledOnTouchOutside(false)
                .onPositive((dialog, which) -> {
                    //CREATES THE RECORDINGS DIRECTORY
                    File directory = new File(Environment.getExternalStorageDirectory() + File.separator + "Recordings");
                    directory.mkdirs();

                    //RENAMES THE TEMP FILE
                    File temp = new File(Environment.getExternalStorageDirectory() + File.separator, "temp.tmp");
                    File dest = new File(Environment.getExternalStorageDirectory() + File.separator + "Recordings" + File.separator, dialog.getInputEditText() + ".3gpp");
                    temp.renameTo(dest);
                })
                .negativeText(R.string.action_delete)
                .onNegative((dialog, which) -> {

                    //DELETES THE TEMP FILE
                    File file = new File(Environment.getExternalStorageDirectory() + File.separator, "temp.tmp");
                    boolean deleted = file.delete();
                })
                .input(R.string.dialog_rename_file_hint, R.string.dialog_rename_file_prefill, false, (dialog, input) -> {
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
                })
                .show();
    }

    public void renameDialog() {
        final EditText userEditText = new EditText(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int second = c.get(Calendar.SECOND);
        builder.setCancelable(false);
        builder.setTitle(R.string.dialog_rename_file_title);
        builder.setMessage(R.string.dialog_rename_file_content);
        builder.setView(userEditText);
        builder.setNegativeButton(R.string.action_cancel, (dialogInterface, i) -> {
            File file = new File(Environment.getExternalStorageDirectory() + File.separator, "temp.tmp");
            boolean deleted = file.delete();
        });
        builder.setPositiveButton(R.string.action_save, (dialogInterface, i) -> {
            File directory = new File(Environment.getExternalStorageDirectory() + File.separator + "Recordings");
            directory.mkdirs();
            String userInput = userEditText.getText().toString();
            File temp = new File(Environment.getExternalStorageDirectory() + File.separator, "temp.tmp");
            File dest = new File(Environment.getExternalStorageDirectory() + File.separator + "Recordings" + File.separator, userInput + ".3gpp");
            temp.renameTo(dest);
        });
        builder.show();
    }
}