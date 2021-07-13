package com.krishna.Conekto.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.krishna.Conekto.Fragments.AboutFragment;
import com.krishna.Conekto.Fragments.ChatListFragment;
import com.krishna.Conekto.Fragments.UserslistFragment;
import com.krishna.Conekto.Models.UsersDetailsModel;
import com.krishna.Conekto.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class DashboardActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationview;
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    CircleImageView profile_option;
    TextView btnSignOut;

    final Fragment fragment1 = new ChatListFragment();
    final Fragment fragment2 = new UserslistFragment();
    final Fragment fragment3 = new AboutFragment();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = fragment1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        profile_option = findViewById(R.id.profile_option);
        btnSignOut = findViewById(R.id.signout);
        bottomNavigationview = findViewById(R.id.bottomNavigationView);
        bottomNavigationview.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        bottomNavigationview.setBackground(null);
        fm.beginTransaction().add(R.id.framelayout, fragment3, "3").hide(fragment3).commit();
        fm.beginTransaction().add(R.id.framelayout, fragment2, "2").hide(fragment2).commit();
        fm.beginTransaction().add(R.id.framelayout, fragment1, "1").commit();
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setStatusBarColor(DashboardActivity.this.getColor(android.R.color.white));
//        window.setBackgroundDrawableResource(R.color.white);
//        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                UsersDetailsModel usersDetailsModel = snapshot.getValue(UsersDetailsModel.class);
                if (usersDetailsModel.getImage().equals("default")) {
                    profile_option.setImageResource(R.drawable.ic_person);
                } else {
                    Glide.with(getApplicationContext()).load(usersDetailsModel.getImage()).into(profile_option);
                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
        profile_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("status", "offline");
                reference.updateChildren(hashMap);
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(DashboardActivity.this, LoginActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                SharedPreferences settings = getSharedPreferences("User", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("LoggedIn", false);
                editor.commit();
            }
        });
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.chats:
                    fm.beginTransaction().hide(active).show(fragment1).commit();
                    active = fragment1;
                    return true;

                case R.id.Users:
                    fm.beginTransaction().hide(active).show(fragment2).commit();
                    active = fragment2;
                    return true;

                case R.id.About:
                    fm.beginTransaction().hide(active).show(fragment3).commit();
                    active = fragment3;
                    return true;
            }
            return false;
        }
    };

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private void CheckingForStatus(String status) {
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        CheckingForStatus("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        CheckingForStatus("offline");
    }
}
