package com.example.Conekto.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.Conekto.Adapters.UsersAdapter;
import com.example.Conekto.Models.ChatListModel;
import com.example.Conekto.Models.UsersDetailsModel;
import com.example.Conekto.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ChatListFragment extends Fragment {
    private UsersAdapter usersAdapter;
    View view;
    private ArrayList<UsersDetailsModel> usersDetailsList;
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    private ArrayList<ChatListModel> chatList;
    RecyclerView rv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_chat_list, container, false);
        rv = view.findViewById(R.id.chat_list_rv);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        chatList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Chatlist").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                chatList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    ChatListModel chatListModel = snapshot1.getValue(ChatListModel.class);
                    chatList.add(chatListModel);
                }
                chatList();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
        return view;
    }

    private void chatList() {
        // all recent chats
        usersDetailsList = new ArrayList<>();
        reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                usersDetailsList.clear();
                for(DataSnapshot snapshot1:snapshot.getChildren()) {
                    UsersDetailsModel usersDetailsModel = snapshot1.getValue(UsersDetailsModel.class);
                    for(ChatListModel chatList: chatList){
                        if(usersDetailsModel.getUid().equals(chatList.getId())){
                            usersDetailsList.add(usersDetailsModel);
                        }

                    }
                }
                usersAdapter =new UsersAdapter(usersDetailsList, getContext(),true);
                rv.setAdapter(usersAdapter);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}