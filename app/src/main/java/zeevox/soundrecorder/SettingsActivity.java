package zeevox.soundrecorder;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final Button resetButton = (Button) findViewById(R.id.settings_reset);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //resetPreferences();
            }
        });
        final Button commitButton = (Button) findViewById(R.id.settings_save);
        commitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //resetPreferences();
            }
        });
    }

    /*SharedPreferences pref = getApplicationContext().getSharedPreferences("SettingsPreferences", MODE_PRIVATE);
    SharedPreferences.Editor editor = pref.edit();

    private void resetPreferences() {
        editor.clear();
        editor.commit();
    }*/
}
