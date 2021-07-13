package com.krishna.Conekto.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.krishna.Conekto.Activities.ChatActivity;
import com.krishna.Conekto.Models.UsersDetailsModel;
import com.krishna.Conekto.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UsersViewHolder> {
    Context context;
    UsersAdapter.onRecylerViewItemClickListener mItemClickListener;
    private ArrayList<UsersDetailsModel> usersDetailsModel;
    private boolean CheckStatus;

    public UsersAdapter(ArrayList<UsersDetailsModel> usersDetailsModel, Context context, boolean status) {
        this.usersDetailsModel = usersDetailsModel;
        this.context = context;
        this.CheckStatus = status;
    }

    public void setOnItemClickListener(UsersAdapter.onRecylerViewItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.conversation, viewGroup, false);
        return new UsersViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersViewHolder ChatListViewHolder, int i) {
        UsersDetailsModel cInfo = usersDetailsModel.get(i);
        ChatListViewHolder.vchat_username.setText(cInfo.getFullname());
        if (CheckStatus) {
            if (cInfo.getStatus().equals("online")) {
                ChatListViewHolder.vStatus.setVisibility(View.VISIBLE);
            } else {
                ChatListViewHolder.vStatus.setVisibility(View.GONE);
            }
        } else {
            ChatListViewHolder.vStatus.setVisibility(View.GONE);
        }
        if (cInfo.getImage().equals("default")) {
            ChatListViewHolder.vedt_profileimg.setImageResource(R.drawable.ic_person);
        } else {
            Glide.with(context).asBitmap().load(cInfo.getImage()).fitCenter()
                    .override(200, 200)
                    .thumbnail(.1f)
                    .placeholder(R.drawable.ic_person)
                    .into(ChatListViewHolder.vedt_profileimg);

        }
        ChatListViewHolder.vuser_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("name", cInfo.getFullname());
                intent.putExtra("uid", cInfo.getUid());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return usersDetailsModel.size();
    }

    public interface onRecylerViewItemClickListener {
        void onItemClickListener(View view, int position);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class UsersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected CircleImageView vedt_profileimg;
        protected TextView vchat_username;
        protected TextView vchat_lastmsg;
        protected TextView vchat_msgTime;
        protected ConstraintLayout vuser_chat;
        protected TextView vStatus;

        public UsersViewHolder(@NonNull View v) {
            super(v);
            vedt_profileimg = (CircleImageView) v.findViewById(R.id.profile);
            vchat_username = (TextView) v.findViewById(R.id.username);
            vchat_lastmsg = (TextView) v.findViewById(R.id.lastMsg);
            vchat_msgTime = (TextView) v.findViewById(R.id.msgTime);
            vuser_chat = (ConstraintLayout) v.findViewById(R.id.user_chat);
            vStatus = (TextView) v.findViewById(R.id.status_dot);
            vedt_profileimg.setOnClickListener(this);
            vuser_chat.setOnClickListener(this);
        }

        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClickListener(v, getAdapterPosition());
            }
        }
    }
}
