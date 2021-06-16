package com.example.Conekto.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.Conekto.Fragments.ChatListFragment;
import com.example.Conekto.Fragments.UserslistFragment;
import com.example.Conekto.Models.UsersDetailsModel;
import com.example.Conekto.R;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
    FloatingActionButton fab;
    BottomNavigationView bottomNavigationview;
    AppBarLayout appBarLayout;
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    Toolbar toolbar;
    CircleImageView profile_option;

    final Fragment fragment1 = new UserslistFragment();
    final Fragment fragment2 = new ChatListFragment();
    //    final Fragment fragment3 = new NotificationsFragment();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = fragment1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        fab = findViewById(R.id.fab);
        profile_option = findViewById(R.id.profile_option);
        bottomNavigationview = findViewById(R.id.bottomNavigationView);
        bottomNavigationview.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        bottomNavigationview.setBackground(null);
        bottomNavigationview.getMenu().getItem(2).setEnabled(false);
//        fm.beginTransaction().add(R.id.main_container, fragment3, "3").hide(fragment3).commit();
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
                UsersDetailsModel users = snapshot.getValue(UsersDetailsModel.class);
                assert users != null;
//                Toast.makeText(DashboardActivity.this, "User Login" + users.getFullname(), Toast.LENGTH_SHORT).show();
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
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.miHome:
                    fm.beginTransaction().hide(active).show(fragment1).commit();
                    active = fragment1;
                    return true;

                case R.id.miProfile:
                    fm.beginTransaction().hide(active).show(fragment2).commit();
                    active = fragment2;
                    return true;

                case R.id.miSettings:
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(DashboardActivity.this, LoginActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    SharedPreferences settings = getSharedPreferences("User", 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("LoggedIn", false);
                    editor.commit();
                    return true;

//                    case R.id.navigation_notifications:
//                        fm.beginTransaction().hide(active).show(fragment3).commit();
//                        active = fragment3;
//                        return true;
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

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle item selection
//        switch (item.getItemId()) {
//            case R.id.profile:
//                loadFragment(new ChatlistFragment());
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }

//    private void loadFragment(Fragment fragment) {
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.replace(R.id.framelayout, fragment);
//        transaction.addToBackStack(null);
//        transaction.commit();
//    }
//}
//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
//    }
////
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
////        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//}
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//            return true;
//        }
//
//    }
