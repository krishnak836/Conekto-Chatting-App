package com.krishna.Conekto.Activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.krishna.Conekto.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CheckingActivity extends AppCompatActivity {
    private static int CHECKING_SCREEN_TIME_OUT = 2000;
    Handler handler;
    Boolean isLoggedIn;
    TextView textView;
    ConstraintLayout checking_layout;
    FirebaseAuth firebaseAuth;
    SharedPreferences sharedPreferences;
    FirebaseAuth.AuthStateListener mAuthListener;
    public static final int PERMISSIONS_REQUEST_CODE = 1;
    String permission[] = {Manifest.permission.READ_EXTERNAL_STORAGE};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setStatusBarColor(CheckingActivity.this.getColor(android.R.color.white));
        setContentView(R.layout.activity_checking);
        checking_layout = findViewById(R.id.checking_layout);
        textView = findViewById(R.id.app_name);
        TextPaint paint = textView.getPaint();
        float width = paint.measureText("Tianjin, China");

        Shader textShader = new LinearGradient(0, 0, width, textView.getTextSize(),
                new int[]{
                        Color.parseColor("#F97C3C"),
                        Color.parseColor("#FDB54E"),
                        Color.parseColor("#64B678"),
                        Color.parseColor("#478AEA"),
                        Color.parseColor("#8446CC"),
                }, null, Shader.TileMode.CLAMP);
        textView.getPaint().setShader(textShader);
        sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
        isLoggedIn = sharedPreferences.getBoolean("LoggedIn", false);
        try {
            if (isNetworkConnected()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (check_permission_granted()) {
                        handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent;
                                if (isLoggedIn) {
                                    intent = new Intent(CheckingActivity.this, DashboardActivity.class);
                                } else {
                                    intent = new Intent(CheckingActivity.this, SplashScreen1Activity.class);
                                }
                                startActivity(intent);
                                finish();
                            }
                        }, CHECKING_SCREEN_TIME_OUT);
                    }
                } else {
                    check_credential();
                }
            } else {
                Intent intent = new Intent(CheckingActivity.this, NoInternetActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Permission", "Permission Exception");
        }
    }

    boolean check_permission_granted() {
        // check permission Are granted
        ArrayList<String> permission_list = new ArrayList<String>();
        for (int i = 0; i < permission.length; i++) {
            if (ContextCompat.checkSelfPermission(this, permission[i]) != PackageManager.PERMISSION_GRANTED) {
                permission_list.add(permission[i]);
            }
        }
        if (!permission_list.isEmpty()) {
            ActivityCompat.requestPermissions(this, permission_list.toArray(new String[permission_list.size()]), PERMISSIONS_REQUEST_CODE);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            HashMap<String, Integer> permissionResult = new HashMap<>();
            int denied_count = 0;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    permissionResult.put(permissions[i], grantResults[i]);
                    denied_count++;
                }
            }
            if (denied_count == 0) {
                check_credential();
            } else {
                for (Map.Entry<String, Integer> entry : permissionResult.entrySet()) {
                    String prem_name = entry.getKey();
//                    int perm_result = entry.getValue();
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, prem_name)) {
                        showDialog("Permissions are required to run App ");
                    } else {
                        explainDialog(" Permissions are required to run App.Go to app settings.");
                    }
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void showDialog(String s) {
        final android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(this);
        dialog.setMessage(s)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        check_permission_granted();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        finish();
                    }
                });
        dialog.show();
    }

    private void explainDialog(String msg) {
        final android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(this);
        dialog.setMessage(msg)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        //  permissionsclass.requestPermission(type,code);
                        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", getPackageName(), null));
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        finish();
                    }
                });
        dialog.show();
    }

    void check_credential() {
        try {
            handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isNetworkConnected()) {
                        Intent intent;
                        if (isLoggedIn) {
                            intent = new Intent(CheckingActivity.this, DashboardActivity.class);
                        } else {
                            intent = new Intent(CheckingActivity.this, SplashScreen1Activity.class);
                        }
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(getApplicationContext(), NoInternetActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                        showSnackbar("No Internet Connection!");

                    }
                }
            }, CHECKING_SCREEN_TIME_OUT);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Checking Exception", "Checking Screen Exception");
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        check_credential();
    }

    public void showSnackbar(String msg) {
        Snackbar snackbar = Snackbar.make(checking_layout, "" + msg, Snackbar.LENGTH_LONG).setDuration(5000);
        snackbar.show();
    }
}
