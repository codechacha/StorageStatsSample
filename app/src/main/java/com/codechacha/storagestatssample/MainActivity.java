package com.codechacha.storagestatssample;

import android.app.usage.StorageStats;
import android.app.usage.StorageStatsManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.os.UserHandle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import static android.os.storage.StorageManager.UUID_DEFAULT;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    public static String TAG = "MainActivity";
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Runnable r = new Runnable() {
            public void run() {
                getStorageStatsForPackage();
                getStorageStatsForUid();
                getStorageStatsForUser();
            }
        };
        handler.post(r);
    }

    private void getStorageStatsForPackage() {
        StorageStatsManager statsManager = getSystemService(StorageStatsManager.class);
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> ris = getPackageManager().queryIntentActivities(intent, 0);
        Log.d(TAG, "getStorageStatsForPackage");
        if (ris != null) {
            for (ResolveInfo ri : ris) {
                UUID uuid = ri.activityInfo.applicationInfo.storageUuid;
                String packageName = ri.activityInfo.packageName;
                UserHandle user = android.os.Process.myUserHandle();
                Log.d(TAG, "packageName: " + packageName);
                try {
                    StorageStats stats = statsManager.queryStatsForPackage(
                            uuid, packageName, user);
                    Log.d(TAG, "App size: " + stats.getAppBytes() +
                            ", Cache size: " + stats.getCacheBytes() +
                            ", Data size: " + stats.getDataBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }

        }
    }


    private void getStorageStatsForUid() {
        StorageStatsManager statsManager = getSystemService(StorageStatsManager.class);
        int systemUid = 1000;
        try {
            StorageStats stats = statsManager.queryStatsForUid(
                    UUID_DEFAULT, systemUid);
            Log.d(TAG, "[System UID] App size: " + stats.getAppBytes() +
                    ", Cache size: " + stats.getCacheBytes() +
                    ", Data size: " + stats.getDataBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void getStorageStatsForUser() {
        StorageStatsManager statsManager = getSystemService(StorageStatsManager.class);
        UserHandle user = android.os.Process.myUserHandle();
        try {
            StorageStats stats = statsManager.queryStatsForUser(
                    UUID_DEFAULT, user);
            Log.d(TAG, "[Current User] App size: " + stats.getAppBytes() +
                    ", Cache size: " + stats.getCacheBytes() +
                    ", Data size: " + stats.getDataBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
