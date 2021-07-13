package com.example.Conekto.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.Conekto.R;

public class SplashScreen1Activity extends AppCompatActivity {
    TextView next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen1);
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setStatusBarColor(SplashScreen1Activity.this.getColor(android.R.color.transparent));
        window.setBackgroundDrawableResource(R.drawable.login_bg);
        next = findViewById(R.id.txt_next1);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SplashScreen1Activity.this,
                        WelcomeActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
}