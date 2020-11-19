package com.example.myapplication;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.IPackageDeleteObserver;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;


import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {

    private Button mBtInstall;
    private Button mBtUninstall;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        verifyStoragePermissions();
        mBtInstall = findViewById(R.id.bt_install);
        mBtUninstall = findViewById(R.id.bt_uninstall);
        mBtInstall.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                //Give an example
//                silentInstall("/vendor/priv-app/CibnTv4K/CibnTv4K.apk");//Android 7.1
                PackageManagerCompatP.install(getApplicationContext(),"vendor/Askey/dailyyoga/dailyyoga.apk",getPackageManager());//Android 9.0
                PackageManagerCompatP.install(getApplicationContext(),"data/Askey/liyuanhang/liyuanhang.apk",getPackageManager());
         //       PackageManagerCompatP.install(getApplicationContext(),"/vendor/priv-app/CibnTv4K/CibnTv4K.apk",getPackageManager());
//                Intent intent = new Intent("com.april.test");
//                intent.putExtra("data", "hello data");
//                intent.putExtra("hotkey", 111);
//                sendBroadcast(intent);
            }
        });
        mBtUninstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Give an example
                silentUnInstall("com.example.testbroadcast");
            }
        });
        
    }

    /**
     * Request file read and write permissions
     */
    public void verifyStoragePermissions() {
        try {
            int permission = ActivityCompat.checkSelfPermission(this,
                    "android.permission.WRITE_EXTERNAL_STORAGE");

            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Install APK silently
     *
     * @param apkPath .apk file path
     * @return
     */
    public boolean silentInstall(String apkPath) {
        PackageManager packageManager = getApplicationContext().getPackageManager();
        Class<?> pmClz = packageManager.getClass();
        try {
            Class<?> aClass = Class.forName("android.app.PackageInstallObserver");
            Constructor<?> constructor = aClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            Object installObserver = constructor.newInstance();
            Method method = pmClz.getDeclaredMethod("installPackage", Uri.class, aClass, int.class, String.class);
            method.setAccessible(true);
            method.invoke(packageManager, Uri.fromFile(new File(apkPath)), installObserver, 2, null);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * UnInstall APK silently
     *
     * @param packageName
     * @return
     */
    public boolean silentUnInstall(String packageName) {
        PackageManager packageManager = getApplicationContext().getPackageManager();
        Class<?> pmClz = packageManager.getClass();
        try {
            Method method = pmClz.getDeclaredMethod("deletePackage", String.class, IPackageDeleteObserver.class, int.class);
            method.setAccessible(true);
            method.invoke(packageManager, packageName, null, 2);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static class ChangeKeyBoardReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("action.com.askey.change.keyboardtype")) {
                SharedPreferences sp = context.getSharedPreferences("org.mozc.android.inputmethod.japanese_preferences", Context.MODE_PRIVATE);
                String data = intent.getStringExtra("keyboard_layout_key");
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("pref_portrait_keyboard_layout_key", data);
                editor.commit();
                Log.d("april", "11data-->" + data);

            }
        }
    }

}