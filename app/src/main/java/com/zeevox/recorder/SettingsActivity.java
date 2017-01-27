package com.zeevox.recorder;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.io.File;

import de.psdev.licensesdialog.LicensesDialog;
import de.psdev.licensesdialog.licenses.ApacheSoftwareLicense20;
import de.psdev.licensesdialog.licenses.MITLicense;
import de.psdev.licensesdialog.model.Notice;
import de.psdev.licensesdialog.model.Notices;

public class SettingsActivity extends PreferenceActivity {

    public int buildPress = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarSettings);
        toolbar.setTitle(R.string.action_settings);
        toolbar.inflateMenu(R.menu.menu_settings);
        findViewById(R.id.menu_item_settings_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        addPreferencesFromResource(R.xml.preferences_settings_activity);

        //SETUP

        int buildNumber = BuildConfig.VERSION_CODE;
        Preference preferenceBuildNumber = findPreference("key_app_build");
        preferenceBuildNumber.setSummary("Build " + buildNumber);
        String buildVersion = BuildConfig.VERSION_NAME;
        Preference preferenceBuildVersion = findPreference("key_app_version");
        preferenceBuildVersion.setSummary("Version " + buildVersion);

        String buildType = BuildConfig.BUILD_TYPE;
        switch (buildType) {
            case "beta": {
                Preference preferenceJoinBeta = findPreference("key_join_beta");
                preferenceJoinBeta.setEnabled(false);
                preferenceJoinBeta.setSummary(R.string.settings_beta_summary_beta);
                Preference preferenceAppType = findPreference("key_app_release_type");
                preferenceAppType.setSummary(R.string.app_release_type_beta);
                break;
            }
            case "dogfood": {
                Preference preferenceJoinBeta = findPreference("key_join_beta");
                preferenceJoinBeta.setEnabled(false);
                preferenceJoinBeta.setSummary(R.string.settings_beta_summary_dogfood);
                Preference preferenceAppType = findPreference("key_app_release_type");
                preferenceAppType.setSummary(R.string.app_release_type_dogfood);
                break;
            }
            case "debug": {
                Preference preferenceJoinBeta = findPreference("key_join_beta");
                preferenceJoinBeta.setEnabled(false);
                preferenceJoinBeta.setSummary(R.string.settings_beta_summary_debug);
                Preference preferenceAppType = findPreference("key_app_release_type");
                preferenceAppType.setSummary(R.string.app_release_type_debug);
                break;
            }
            default: {
                Preference preferenceJoinBeta = findPreference("key_join_beta");
                preferenceJoinBeta.setEnabled(true);
                Preference preferenceAppType = findPreference("key_app_release_type");
                preferenceAppType.setSummary(R.string.app_release_type_stable);
                break;
            }
        }

        for(int x = 0; x < getPreferenceScreen().getPreferenceCount(); x++){
            PreferenceCategory lol = (PreferenceCategory) getPreferenceScreen().getPreference(x);
            for(int y = 0; y < lol.getPreferenceCount(); y++){
                Preference pref = lol.getPreference(y);
                pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        String key = preference.getKey();
                        switch (key) {
                            case "key_preference_list_app_theme":
                                break;
                            case "key_clear_cache":
                                clearCache();
                                break;
                            case "key_join_beta":
                                dialogBeta();
                                break;
                            case "key_app_copyright":
                                dialogLicenses();
                                break;
                            case "key_app_build":
                                if (buildPress < 7) {
                                    buildPress = buildPress + 1;
                                } else if (buildPress >= 7) {
                                    buildPress = 0;
                                    Toast.makeText(SettingsActivity.this, "Advanced settings are now enabled. Enjoy!", Toast.LENGTH_SHORT).show();
                                    Intent infoActivityIntent = new Intent(SettingsActivity.this, InfoActivity.class);
                                    startActivity(infoActivityIntent);
                                }
                                break;
                        }
                        return false;
                    }
                });
            }
        }
    }

    public void clearCache() {
        Log.i("myTag", "CLEARING CACHE");
        ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle(SettingsActivity.this.getString(R.string.dialog_progress_clear_cache));
        mProgressDialog.setMessage(SettingsActivity.this.getString(R.string.dialog_progress_clear_cache_please_wait));
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        try {
            try {
                File dir = this.getCacheDir();
                if (dir != null && dir.isDirectory()) {
                    deleteDir(dir);
                }
            } catch (Exception e) {
                e.printStackTrace();
                mProgressDialog.dismiss();
                dialogClearCacheError();
                //TODO: Send bug report
            }
        } catch (Exception e) {
            e.printStackTrace();
            mProgressDialog.dismiss();
        }
        mProgressDialog.dismiss();
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                boolean success = deleteDir(new File(dir, aChildren));
                if (!success) {
                    return false;
                }
            }
            // The directory is now empty so we delete it
            return dir.delete();
        }
        return false;
    }

    public void dialogClearCacheError() {
        new MaterialDialog.Builder(this)
                .title(R.string.dialog_clear_cache_error_title)
                .content(R.string.dialog_clear_cache_error_text)
                .positiveText(R.string.action_yes)
                .negativeText(R.string.action_no_thanks)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        clearCache();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public void dialogLicenses() {

        final Notices notices = new Notices();
        notices.addNotice(new Notice("Sound Recorder", "https://play.google.com/store/apps/details?id=com.zeevox.recorder", "Copyright (c) 2016-2017 Timothy Langer", new MITLicense()));
        notices.addNotice(new Notice("MaterialDialogs API", "https://github.com/afollestad/material-dialogs", "Copyright (c) 2014-2016 Aidan Michael Follestad", new MITLicense()));
        notices.addNotice(new Notice("MaterialTapTargetPrompt", "https://github.com/sjwall/MaterialTapTargetPrompt", "Copyright (c) 2016 Samuel Wall", new ApacheSoftwareLicense20()));

        new LicensesDialog.Builder(this)
                .setNotices(notices)
                .setIncludeOwnLicense(true)
                .build()
                .show();
    }

    public void dialogBeta() {

        new MaterialDialog.Builder(this)
                .title(R.string.dialog_join_beta_title)
                .content(R.string.dialog_join_beta_content)
                .iconRes(R.drawable.bug)
                .negativeText(R.string.action_no_thanks)
                .positiveText(R.string.action_yeah_sure)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/apps/testing/com.ezcode.recorder"));
                        startActivity(intent);
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }
}
