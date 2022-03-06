package ml.qingsu.fuckview.ui.activities;

import static android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
import static android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
import static android.content.pm.PackageManager.DONT_KILL_APP;
import static ml.qingsu.fuckview.Constant.ENABLE_LOG_NAME;
import static ml.qingsu.fuckview.Constant.ONLY_ONCE_NAME;
import static ml.qingsu.fuckview.Constant.PKG_NAME;
import static ml.qingsu.fuckview.Constant.STANDARD_MODE_NAME;
import static ml.qingsu.fuckview.Constant.SUPER_MODE_NAME;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.widget.Toast;

import com.jrummyapps.android.shell.Shell;

import java.util.Locale;

import de.psdev.licensesdialog.LicensesDialog;
import de.psdev.licensesdialog.licenses.ApacheSoftwareLicense20;
import de.psdev.licensesdialog.licenses.MITLicense;
import de.psdev.licensesdialog.model.Notice;
import de.psdev.licensesdialog.model.Notices;
import ml.qingsu.checkxposed.util.AlipayDonate;
import ml.qingsu.fuckview.Constant;
import ml.qingsu.fuckview.R;
import ml.qingsu.fuckview.utils.GnuAfferoGeneralPublicLicense30;

public class PreferencesActivity extends PreferenceActivity {
    private int clickTime = 0;
    public static final int RESULT_GUIDE = 0x100;
    private int versionClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("theme", false)) {
            setTheme(R.style.DayTheme);
        }
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preference);
        findPreference("super_mode").setOnPreferenceChangeListener((preference, o) -> {

            MainActivity.writePreferences(o.toString(), SUPER_MODE_NAME);
            return true;
        });
        findPreference("only_once").setOnPreferenceChangeListener((preference, newValue) -> {
            MainActivity.writePreferences(newValue.toString(), ONLY_ONCE_NAME);
            return true;
        });
        findPreference("standard_mode").setOnPreferenceChangeListener((preference, newValue) -> {
            MainActivity.writePreferences(newValue.toString(), STANDARD_MODE_NAME);
            return true;
        });
        findPreference("import").setOnPreferenceClickListener(preference -> {
            final AppCompatEditText editText = new AppCompatEditText(PreferencesActivity.this);
            editText.setHint(R.string.import_rules);
            new AlertDialog.Builder(PreferencesActivity.this)
                    .setTitle(R.string.conf_import_rules_name)
                    .setView(editText)
                    .setPositiveButton(R.string.OK, (dialogInterface, i) -> MainActivity.appendPreferences("\n" + editText.getText().toString(), Constant.LIST_NAME))
                    .setNegativeButton(R.string.cancel, null)
                    .show();
            return false;
        });
        findPreference("online_rules").setOnPreferenceClickListener(preference -> {
            setResult(RESULT_OK);
            finish();
            return false;
        });
        findPreference("about").setOnPreferenceClickListener(preference -> {

            clickTime++;

            if (clickTime >= 5 * 59) {
                clickTime = 0;

                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
                } catch (Throwable ignored) {

                }
                Toast.makeText(PreferencesActivity.this, R.string.give_me_five_stars, Toast.LENGTH_LONG).show();
            }
            preference.setSummary(String.format(Locale.CHINA, getString(R.string.conf_joke), clickTime / 59, clickTime % 59));
            return false;
        });
        findPreference("version").setSummary(getVersionName(this));
        findPreference("version").setOnPreferenceClickListener(preference -> {
            versionClickTime++;
            if (versionClickTime >= 5) {
                versionClickTime = 0;
                startActivity(new Intent(PreferencesActivity.this, ExperimentActivity.class));
            }
            return false;
        });
        findPreference("pay").setOnPreferenceClickListener(preference -> {
            if (!AlipayDonate.startAlipayClient(PreferencesActivity.this, "a6x06490c5kpcbnsr84hr23")) {
                new AlertDialog.Builder(PreferencesActivity.this)
                        .setMessage(R.string.no_alipay)
                        .setNegativeButton(R.string.no, null)
                        .setPositiveButton(R.string.thats_okay, (dialog, which) -> {

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                clipboardManager.setPrimaryClip(ClipData.newPlainText(null, "1278297578@qq.com"));
                            } else {
                                android.text.ClipboardManager clipboardManager = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                clipboardManager.setText("1278297578@qq.com");
                            }
                        })
                        .show();
            }
            return false;
        });
        findPreference("source").setOnPreferenceClickListener(preference -> {
            Notices notices = new Notices();
            notices.addNotice(new Notice("XposedBridge", "https://github.com/rovo89/XposedBridge", "Copyright 2013 rovo89, Tungstwenty", new ApacheSoftwareLicense20()));
            notices.addNotice(new Notice("Bugly", "http://jcenter.bintray.com/com/tencent/bugly", "Copyright 1998 - 2016 Tencent.", new ApacheSoftwareLicense20()));
            notices.addNotice(new Notice("EventBus", "https://github.com/greenrobot/EventBus", "Copyright (C) 2012-2017 Markus Junginger, greenrobot", new ApacheSoftwareLicense20()));
            notices.addNotice(new Notice("android-shell", "https://github.com/jrummyapps/android-shell", "Copyright (C) 2016 JRummy Apps Inc.\nCopyright (C) 2012-2015 Jorrit \"Chainfire\" Jongma", new ApacheSoftwareLicense20()));
            notices.addNotice(new Notice("RemotePreferences", "https://github.com/apsun/RemotePreferences", "Copyright (C) 2019 apsun", new MITLicense()));

            notices.addNotice(new Notice("fuckView", "https://github.com/w568w/fuckView", "Copyright (C) 2018 w568w", new GnuAfferoGeneralPublicLicense30()));
            new LicensesDialog.Builder(PreferencesActivity.this)
                    .setNotices(notices)
                    .setIncludeOwnLicense(true)
                    .build()
                    .show();

            return false;
        });
//        findPreference("qq").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//            @Override
//            public boolean onPreferenceClick(Preference preference) {
//                try {
//                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://jq.qq.com/?_wv=1027&k=4EepPOs")));
//                } catch (Throwable ignored) {
//
//                }
//                return false;
//            }
//        });
        findPreference("guide").setOnPreferenceClickListener(preference -> {
            try {
                setResult(RESULT_GUIDE);
                finish();
            } catch (ActivityNotFoundException a) {
                a.printStackTrace();
                Toast.makeText(PreferencesActivity.this, getString(R.string.unsupport_of_package), Toast.LENGTH_SHORT).show();
            }
            return false;
        });
        findPreference("log").setOnPreferenceClickListener(preference -> {
            new Thread(() -> {
                final String bug = String.format(Locale.CHINA, "Logcat:\n\n%s\n\n" +
                        "=================\n\n" +
                        "XposedLog:\n\n%s\n\n" +
                        "=================\n\n" +
                        "Phone:\n\n%s\n\n", getLogcatInfo(), getXposedLogInfo(), getPhoneInfo());
                runOnUiThread(() -> shareText(bug));
            }).start();
            return false;
        });
        findPreference("theme").setOnPreferenceClickListener(preference -> {
            finish();
            Intent restart = new Intent(PreferencesActivity.this, MainActivity.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                restart.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            } else {
                restart.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            startActivity(restart);
            return false;
        });
        findPreference("icon").setOnPreferenceChangeListener((preference, newValue) -> {
            PackageManager pm = getPackageManager();
            boolean usingOld = "old".equals(newValue);
            pm.setComponentEnabledSetting(new ComponentName(getBaseContext(), PKG_NAME + ".ui.activities.MainActivity")
                    , usingOld ? COMPONENT_ENABLED_STATE_DISABLED : COMPONENT_ENABLED_STATE_ENABLED, DONT_KILL_APP);
            pm.setComponentEnabledSetting(new ComponentName(getBaseContext(), PKG_NAME + ".ui.activities.MainActivityOldIcon")
                    , usingOld ? COMPONENT_ENABLED_STATE_ENABLED : COMPONENT_ENABLED_STATE_DISABLED, DONT_KILL_APP);

            return true;
        });
        findPreference("enable_log").setOnPreferenceChangeListener((preference, newValue) -> {
            MainActivity.writePreferences(newValue.toString(), ENABLE_LOG_NAME);
            return true;
        });
    }

    private String getLogcatInfo() {
        return Shell.SU.run("logcat -d -v time").getStdout();
    }

    private String getXposedLogInfo() {
        return Shell.SU.run("cat /data/data/de.robv.android.xposed.installer/log/error.log").getStdout();
    }

    private String getPhoneInfo() {
        return String.format(Locale.CHINA, "版本:%s(%s)\n" +
                        "Android版本:%s\n" +
                        "指纹:%s\n",
                getVersionName(this), getVersionCode(this),
                System.getProperty("ro.build.version.release"),
                Build.FINGERPRINT);
    }

    /**
     * 返回版本名字
     * 对应build.gradle中的versionName
     *
     * @param context
     * @return
     */
    public static String getVersionName(Context context) {
        String versionName = "";
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionName = packInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }

    /**
     * 返回版本号
     * 对应build.gradle中的versionCode
     *
     * @param context
     * @return
     */
    public static String getVersionCode(Context context) {
        String versionCode = "";
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionCode = String.valueOf(packInfo.versionCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    private void shareText(String text) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);
        sendIntent.setType("*/*");
        try {
            Intent chooserIntent = Intent.createChooser(sendIntent, "选择分享途径");
            if (chooserIntent == null) {
                return;
            }
            startActivity(chooserIntent);
        } catch (Exception e) {
            startActivity(sendIntent);
        }
    }
}
