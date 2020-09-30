package de.jbamberger.filesizes;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    FilesAdapter adapter;

    @RequiresApi(Build.VERSION_CODES.M)
    private boolean hasPermission(String permission) {
        return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boolean hasStoragePermission = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                hasStoragePermission = true;
            } else {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                try {
                    startActivityForResult(intent, Constants.ACTION_REQUEST_MANAGE_STORAGE_PERMISSIONS);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(this, "Could not request necessary permissions!", Toast.LENGTH_LONG).show();
                }
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                hasStoragePermission = true;
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        Constants.ACTION_REQUEST_STORAGE_PERMISSIONS);
            }
        }

        if (hasStoragePermission) {
            init();
//        } else {
//            showNoPermissionNotice();
        }
    }

    @Override
    public void onBackPressed() {
        if (adapter.navUp()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Constants.ACTION_REQUEST_STORAGE_PERMISSIONS
                && permissions.length == 1
                && permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                init();
            } else {
                Toast.makeText(this, "Could not get permissions!", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == Constants.ACTION_REQUEST_MANAGE_STORAGE_PERMISSIONS) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                throw new IllegalStateException(
                        "ACTION_REQUEST_MANAGE_STORAGE_PERMISSIONS is only valid for SDK 30 and above.");
            }
            if (Environment.isExternalStorageManager()) {
                init();
            } else {
                Toast.makeText(this, "Could not get permissions!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void init() {
        RecyclerView rv = findViewById(R.id.list);
        rv.setLayoutManager(new LinearLayoutManager(this));

        Item rootItem = new Item(null, Environment.getExternalStorageDirectory());
        this.adapter = new FilesAdapter(getSupportActionBar(), rootItem);
        rv.setAdapter(adapter);
    }

}
