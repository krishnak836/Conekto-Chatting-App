package com.example.Conekto.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Conekto.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

public class LoginActivity extends AppCompatActivity {
    EditText edtEmail, edtPassword;
    private TextView txtLoginInfo, btnSubmit;
    FirebaseAuth mFirebaseAuth;
    ProgressDialog custom_progress_dialog;
    FirebaseFirestore mFirebaseStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        custom_progress_dialog = new ProgressDialog(LoginActivity.this);
        custom_progress_dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        edtEmail = findViewById(R.id.et_email);
        edtPassword = findViewById(R.id.et_password);
        SharedPreferences settings = getSharedPreferences("User", 0);
        boolean isLoggedIn = settings.getBoolean("LoggedIn", false);

        if (isLoggedIn) {
            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
            startActivity(intent);
        }
        btnSubmit = findViewById(R.id.btnSubmit);
        txtLoginInfo = findViewById(R.id.txtLoginInfo);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseStore = FirebaseFirestore.getInstance();
        Window window = this.getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        txtLoginInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit();
            }
        });
    }

    private void submit() {
        custom_progress_dialog.show();
        custom_progress_dialog.setContentView(R.layout.custom_progress_dialog);
        final String sEmail = edtEmail.getText().toString().trim();
        final String sPassword = edtPassword.getText().toString().trim();
        if (sEmail.isEmpty()) {
            edtEmail.setError("Email required");
            edtEmail.requestFocus();
            return;
        }
        if (sPassword.isEmpty()) {
            edtPassword.setError("Password required");
            edtPassword.requestFocus();
            return;
        }
        mFirebaseAuth.signInWithEmailAndPassword(sEmail, sPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    edtEmail.setText("");
                    edtPassword.setText("");
                    SharedPreferences settings = getSharedPreferences("User", 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("LoggedIn", true);
                    editor.commit();
                    custom_progress_dialog.hide();
                    Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    edtEmail.setText("");
                    edtPassword.setText("");
                    custom_progress_dialog.hide();
                    Toast.makeText(LoginActivity.this, "Credentials not matched", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}