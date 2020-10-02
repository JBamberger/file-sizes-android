package de.jbamberger.filesizes;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    ProgressBar progressBar;
    TextView progressText;

    FilesAdapter adapter;
    FileInfoProvider fileInfoProvider;

    @RequiresApi(Build.VERSION_CODES.M)
    private boolean hasPermission(String permission) {
        return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressBar = findViewById(R.id.progressBar);
        progressText = findViewById(R.id.progressText);
        setProgress(true);

        fileInfoProvider = ((FileSizeApp) this.getApplicationContext()).getResourceLocator()
                .getFileInfoProvider();

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.select_root) {
            FileSelectionFragment.newInstance().show(getSupportFragmentManager(), null);
        }
        return super.onOptionsItemSelected(item);
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

        File rootFile = Environment.getExternalStorageDirectory();
        fileInfoProvider.loadInfo(rootFile, (rootItem) -> {
            this.adapter = new FilesAdapter(getSupportActionBar(), rootItem);
            rv.setAdapter(adapter);
            setProgress(false);
        });


    }

    private void setProgress(boolean visible) {
        int state = visible ? View.VISIBLE : View.GONE;
        progressBar.setVisibility(state);
        progressText.setVisibility(state);
    }
}
