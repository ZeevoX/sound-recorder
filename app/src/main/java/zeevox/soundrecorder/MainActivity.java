package zeevox.soundrecorder;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.IOException;
import java.util.Calendar;

import static android.media.MediaRecorder.AudioSource.MIC;

public class MainActivity extends AppCompatActivity {

    public static final int PermissionsAudio = 0;
    public static final int PermissionsStorageR = 0;
    public static final int PermissionsStorageW = 0;
    public MediaRecorder mRecorder = null;
    public boolean recordingBool = false;
    public final int audioSource = MIC;
    public final int sampleRate = 0;
    public final int channelConfig = 0;
    public final int audioFormat = 0;
    private MediaPlayer mPlayer = null;

    public void startRecording() {
        Calendar c = Calendar.getInstance();
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setOutputFile(Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/recording"+ c.get(Calendar.YEAR) + c.get(Calendar.MONTH) + c.get(Calendar.DAY_OF_MONTH) + c.get(Calendar.HOUR_OF_DAY) + c.get(Calendar.MINUTE) +  c.get(Calendar.SECOND) + ".mp3");
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        try {
            mRecorder.prepare();
            mRecorder.start();
            recordingBool=true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopRecording() {
        mRecorder.stop();
        recordingBool=false;
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            //mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            //Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }

    public void checkAndRequestPermissionsAudio() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                !=PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    PermissionsAudio);
        }
    }

    public void checkAndRequestPermissionsStorage() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                !=PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PermissionsStorageR);
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                !=PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PermissionsStorageW);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabRecord);
        fab.setImageDrawable(MainActivity.this.getResources().getDrawable(R.drawable.ic_mic_white_48dp));
        fab.setBackgroundTintList(new ColorStateList(new int[][]{new int[]{0}}, new int[]{getResources().getColor(R.color.colorAccent)}));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAndRequestPermissionsAudio();
                checkAndRequestPermissionsStorage();
                if (!recordingBool) {
                    startRecording();
                    fab.setImageDrawable(MainActivity.this.getResources().getDrawable(R.drawable.ic_mic_off_white_48dp));
                    fab.setBackgroundTintList(new ColorStateList(new int[][]{new int[]{0}}, new int[]{getResources().getColor(R.color.colorAccentDark)}));
                    Snackbar.make(view, R.string.snackbar_recording, Snackbar.LENGTH_INDEFINITE)
                            .show();
                } else {
                    stopRecording();
                    fab.setImageDrawable(MainActivity.this.getResources().getDrawable(R.drawable.ic_mic_white_48dp));
                    fab.setBackgroundTintList(new ColorStateList(new int[][]{new int[]{0}}, new int[]{getResources().getColor(R.color.colorAccent)}));
                    Snackbar.make(view, R.string.snackbar_finished, Snackbar.LENGTH_SHORT)
                            .show();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
