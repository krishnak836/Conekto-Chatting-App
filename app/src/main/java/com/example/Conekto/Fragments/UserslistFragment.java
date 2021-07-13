package com.example.Conekto.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.Conekto.Adapters.UsersAdapter;
import com.example.Conekto.Models.UsersDetailsModel;
import com.example.Conekto.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class UserslistFragment extends Fragment {
    FirebaseFirestore db;
    ArrayList<UsersDetailsModel> users;
    UsersAdapter usersAdapter;
    RecyclerView rv;
    View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_users_list, container, false);
        db = FirebaseFirestore.getInstance();
        rv = (RecyclerView) view.findViewById(R.id.Users_list_rv);
        rv.setHasFixedSize(true);
        rv.setAdapter(usersAdapter);
        users = new ArrayList<>();

        ReadUsers();
        return view;
    }

    private void ReadUsers() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                users.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    UsersDetailsModel user = snapshot1.getValue(UsersDetailsModel.class);
                    assert user != null;
                    if (!user.getUid().equals(firebaseUser.getUid())) {
                        System.out.println(firebaseUser.getUid());
                        users.add(user);
                    }
                    usersAdapter = new UsersAdapter(users, getContext(), false);
                    rv.setAdapter(usersAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}