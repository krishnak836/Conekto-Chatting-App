package com.example.Conekto.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.Conekto.Models.MessageModel;
import com.example.Conekto.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter {
    Context context;
    ArrayList<MessageModel> messageModels;
    final int MSG_SEND = 1;
    final int MSG_RECEIVE = 2;
    FirebaseUser fuser;
    String ImgUrl;
    String name;


    public MessageAdapter(Context context, ArrayList<MessageModel> messageModels, String ImgUrl, String name) {
        this.context = context;
        this.messageModels = messageModels;
        this.ImgUrl = ImgUrl;
        this.name = name;
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup viewGroup, int viewType) {
        if (viewType == MSG_SEND) {
            View itemView = LayoutInflater.
                    from(context).
                    inflate(R.layout.send_message, viewGroup, false);
            return new SentViewHolder(itemView);
        } else {
            View itemView = LayoutInflater.
                    from(context).
                    inflate(R.layout.receive_message, viewGroup, false);
            return new ReceiveViewHolder(itemView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if (messageModels.get(position).getSender().equals(fuser.getUid())) {
            return MSG_SEND;
        } else {
            return MSG_RECEIVE;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        MessageModel cInfo = messageModels.get(position);
        if (holder.getClass() == SentViewHolder.class) {
            SentViewHolder sentViewHolder = (SentViewHolder) holder;
            sentViewHolder.vchat_msgTime_send.setText(cInfo.getTime());
            sentViewHolder.vchat_msg_send.setText(cInfo.getMessage());
            if (position == messageModels.size() - 1) {
                if (cInfo.isSeen()) {
                    ((SentViewHolder) holder).isSeen.setText("Seen");
                } else {
                    ((SentViewHolder) holder).isSeen.setText("Delivered");
                }
            } else {
                ((SentViewHolder) holder).isSeen.setVisibility(View.GONE);
            }
        } else {
            ReceiveViewHolder receiveViewHolder = (ReceiveViewHolder) holder;
            receiveViewHolder.vchat_msg_rec.setText(cInfo.getMessage());
            receiveViewHolder.vchat_username.setText(name);
            receiveViewHolder.vchat_msgTime_rec.setText(cInfo.getTime());
            if (ImgUrl.equals("default")) {
                receiveViewHolder.vedt_profileimg.setImageResource(R.drawable.ic_person);
            } else {
                Glide.with(context).load(ImgUrl).into(receiveViewHolder.vedt_profileimg);
            }
        }
    }

    @Override
    public int getItemCount() {
        return messageModels.size();
    }

    public static class SentViewHolder extends RecyclerView.ViewHolder {
        protected CircleImageView vedt_profileimg;
        protected TextView vchat_msg_send;
        protected TextView vchat_msgTime_send;
        protected CardView vmessage_card_send;
        protected TextView isSeen;

        public SentViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            vchat_msg_send = (TextView) itemView.findViewById(R.id.text_gchat_message_send);
            vchat_msgTime_send = (TextView) itemView.findViewById(R.id.text_gchat_timestamp_send);
            vmessage_card_send = (CardView) itemView.findViewById(R.id.card_gchat_message_send);
            isSeen = (TextView) itemView.findViewById(R.id.send_status);
        }
    }

    public static class ReceiveViewHolder extends RecyclerView.ViewHolder {
        protected CircleImageView vedt_profileimg;
        protected TextView vchat_username;
        protected TextView vchat_msgTime_rec;
        protected TextView vchat_msg_rec;
        protected CardView vmessage_card_rec;

        public ReceiveViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            vedt_profileimg = itemView.findViewById(R.id.image_gchat_profile_rec);
            vchat_username = itemView.findViewById(R.id.text_gchat_user_rec);
            vchat_msg_rec = itemView.findViewById(R.id.text_gchat_message_rec);
            vchat_msgTime_rec = itemView.findViewById(R.id.text_gchat_timestamp_rec);
            vmessage_card_rec = itemView.findViewById(R.id.card_gchat_message_rec);
        }
    }
}


