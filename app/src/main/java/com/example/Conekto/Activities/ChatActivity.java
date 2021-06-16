package com.example.Conekto.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.Conekto.Adapters.MessageAdapter;
import com.example.Conekto.Models.MessageModel;
import com.example.Conekto.Models.UsersDetailsModel;
import com.example.Conekto.R;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    AppBarLayout appBarLayout;
    ImageView attachment;
    String name, receiverId;
    TextView txtname;
    CircleImageView imageView;
    ImageView sendBtn;
    RecyclerView rv;
    ArrayList<MessageModel> messages;
    EditText edtmessageBox;
    MessageAdapter messageAdapter;
    DatabaseReference db_ref;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        appBarLayout = findViewById(R.id.app_bar);
        appBarLayout.setBackground(null);
        sendBtn = findViewById(R.id.sendBtn);
        edtmessageBox = findViewById(R.id.messageBox);
        attachment = findViewById(R.id.attachment);
        txtname = findViewById(R.id.name);
        imageView = findViewById(R.id.profile_image);
        rv = findViewById(R.id.chat_rv);
        rv.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        rv.setLayoutManager(linearLayoutManager);
        name = getIntent().getStringExtra("name");
        receiverId = getIntent().getStringExtra("uid");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        db_ref = FirebaseDatabase.getInstance().getReference("Users").child(receiverId);
        db_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                UsersDetailsModel usersDetailsModel = snapshot.getValue(UsersDetailsModel.class);
                assert usersDetailsModel != null;
                txtname.setText(usersDetailsModel.getFullname());
                if (usersDetailsModel.getImage().equals("default")) {
                    imageView.setImageResource(R.drawable.profile);
                } else {
                    Glide.with(ChatActivity.this)
                            .load(usersDetailsModel.getImage()).into(imageView);
                }
                ReadMessages(firebaseUser.getUid(), receiverId, usersDetailsModel.getImage(), usersDetailsModel.getFullname());
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = edtmessageBox.getText().toString();
                if (!message.equals("")) {
                    sendMessages(firebaseUser.getUid(), receiverId, message);
                } else {
                    Toast.makeText(ChatActivity.this, "Please send a non-empty message", Toast.LENGTH_SHORT).show();
                }
                edtmessageBox.setText("");
            }
        });
        attachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    private void sendMessages(String senderId, String receiverId, String message) {
        db_ref = FirebaseDatabase.getInstance().getReference();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date date = new Date();
        String time = formatter.format(date);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", senderId);
        hashMap.put("receiver", receiverId);
        hashMap.put("message", message);
        hashMap.put("time", time);
        db_ref.child("Chats").push().setValue(hashMap);

// Adding user to latest chat
        final DatabaseReference myChatRef = FirebaseDatabase.getInstance().getReference("Chatlist").child(senderId).child(receiverId);
        myChatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    myChatRef.child("id").setValue(receiverId);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    private void ReadMessages(String myId, String userid, String imageUrl, String name) {
        messages = new ArrayList<>();
        db_ref = FirebaseDatabase.getInstance().getReference("Chats");
        db_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                messages.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    MessageModel messageModel = snapshot1.getValue(MessageModel.class);

                    if (messageModel.getReceiver().equals(myId) && messageModel.getSender().equals(userid) ||
                            messageModel.getReceiver().equals(userid) && messageModel.getSender().equals(myId)) {
                        messages.add(messageModel);
                    }
                    messageAdapter = new MessageAdapter(ChatActivity.this, messages, imageUrl, name);
                    rv.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void CheckingForStatus(String status) {
        db_ref = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        db_ref.updateChildren(hashMap);
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

//    public String getCurrentTime() {
//        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
//        Date date = new Date();
//        return formatter.format(date);
//    }
}