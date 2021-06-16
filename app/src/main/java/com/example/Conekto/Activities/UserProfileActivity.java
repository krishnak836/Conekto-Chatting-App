package com.example.Conekto.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.Conekto.R;

public class UserProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        this.setFinishOnTouchOutside(false);

    }
}