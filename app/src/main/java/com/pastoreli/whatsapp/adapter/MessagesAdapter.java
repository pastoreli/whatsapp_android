package com.pastoreli.whatsapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseUser;
import com.pastoreli.whatsapp.R;
import com.pastoreli.whatsapp.helpers.UserFirebaseHelper;
import com.pastoreli.whatsapp.model.Message;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MyViewHolder> {

    private List<Message> messages;
    private Context context;

    private static final int TYPE_SENDER = 0;
    private static final int TYPE_DESTINATARY = 1;

    public MessagesAdapter(List<Message> messageList, Context context) {
        this.messages = messageList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        int layoutId = viewType == TYPE_SENDER ? R.layout.adapter_message_sender : R.layout.adapter_message_destinatary;

        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);

        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Message message = messages.get(position);

        if( message.getName().isEmpty() )
            holder.textDisplayName.setVisibility(View.GONE);
        else {
            holder.textDisplayName.setText(message.getName());
            holder.textDisplayName.setVisibility(View.VISIBLE);
        }

        if( message.getImage() == null ) {
            holder.textMessage.setText(message.getMessage());
            holder.imageMessage.setVisibility(View.GONE);
        } else {
            Glide.with(context)
                    .load(message.getImage())
                    .into(holder.imageMessage);
            holder.textMessage.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {

        Message message = messages.get(position);
        String idUser = UserFirebaseHelper.getUserId();

        if( idUser.equals(message.getIdUser()) )
            return TYPE_SENDER;

        return TYPE_DESTINATARY;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textDisplayName, textMessage;
        ImageView imageMessage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            textDisplayName = itemView.findViewById(R.id.textDisplayName);
            textMessage = itemView.findViewById(R.id.textMessage);
            imageMessage = itemView.findViewById(R.id.imageMessage);

        }
    }

}
