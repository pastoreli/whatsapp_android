package com.pastoreli.whatsapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pastoreli.whatsapp.R;
import com.pastoreli.whatsapp.model.User;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class SelectedGroupAdapter extends RecyclerView.Adapter<SelectedGroupAdapter.MyViewHolder> {

    private List<User> selectMembers;
    private Context context;

    public SelectedGroupAdapter(List<User> selectMembers, Context context) {
        this.selectMembers = selectMembers;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_selected_group, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        User user = selectMembers.get(position);

        holder.textViewUserName.setText(user.getName());

        if(user.getPhoto() == null)
            holder.circleImageUserPhoto.setImageResource(R.drawable.padrao);
        else {
            Glide.with(context)
                .load( user.getPhoto() )
                .into( holder.circleImageUserPhoto );
        }
    }

    @Override
    public int getItemCount() {
        return selectMembers.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView circleImageUserPhoto;
        TextView textViewUserName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            circleImageUserPhoto = itemView.findViewById(R.id.circleImageUserPhoto);
            textViewUserName = itemView.findViewById(R.id.textViewUserName);

        }

    }

}
