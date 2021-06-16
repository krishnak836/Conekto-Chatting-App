package com.example.Conekto.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.Conekto.Models.UsersDetailsModel;
import com.example.Conekto.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

public class SignUpActivity extends AppCompatActivity {
    EditText edtPassword, edtEmail, edtfullname;
    TextView txtLoginInfo, info_text_img, btnSubmit;
    FirebaseAuth mFirebaseAuth;
    FirebaseUser firebaseUser;
    Task<Void> reference;
    ProgressDialog custom_progress_dialog;
//    FirebaseFirestore mFirebaseStore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        custom_progress_dialog = new ProgressDialog(SignUpActivity.this);
        custom_progress_dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        edtEmail = findViewById(R.id.et_email);
        edtPassword = findViewById(R.id.et_password);
        edtfullname = findViewById(R.id.et_fullname);
        btnSubmit = findViewById(R.id.btnSubmit);
        txtLoginInfo = findViewById(R.id.txtLoginInfo);
        info_text_img = findViewById(R.id.info_text);
        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//        mFirebaseStore = FirebaseFirestore.getInstance();
        Window window = this.getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        txtLoginInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
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
        custom_progress_dialog.setCancelable(false);
        final String sFullname = edtfullname.getText().toString().trim();
        final String sEmail = edtEmail.getText().toString().trim();
        final String sPassword = edtPassword.getText().toString().trim();

        if (sFullname.isEmpty()) {
            edtfullname.setError("Fullname required");
            edtfullname.requestFocus();
            return;
        }
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
        mFirebaseAuth.createUserWithEmailAndPassword(sEmail, sPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    edtEmail.setText("");
                    edtfullname.setText("");
                    edtPassword.setText("");
                    UsersDetailsModel user = new UsersDetailsModel(firebaseUser.getUid(),sFullname,sEmail, "default","offline");
                    reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).setValue(user)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        custom_progress_dialog.hide();
                                        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }


                            });
//                    mFirebaseStore.collection("Users")
//                            .add(user)
//                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                                @Override
//                                public void onSuccess(DocumentReference documentReference) {
//                                    custom_progress_dialog.hide();
//                                    Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
//                                    startActivity(intent);
//                                    finish();
//                                }
//                            })
//                            .addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    Toast.makeText(SignUpActivity.this, "Not able to Register", Toast.LENGTH_SHORT).show();
//                                }
//                            });
                } else {
                    edtEmail.setText("");
                    edtPassword.setText("");
                    edtfullname.setText("");
                    custom_progress_dialog.hide();
                    Toast.makeText(SignUpActivity.this, "Not able to Register", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
