package com.findfriends.mycompany.findfriends.Adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.bumptech.glide.RequestManager;
import com.findfriends.mycompany.findfriends.Models.User;
import com.findfriends.mycompany.findfriends.R;
import com.findfriends.mycompany.findfriends.Views.UserViewHolder;

import java.util.List;

public class UserDisplayAdapter extends RecyclerView.Adapter<UserViewHolder> {

    private List<User> list;
    private RequestManager requestManager;
    private Context context;

    public UserDisplayAdapter(RequestManager requestManager, Context context){
        this.requestManager = requestManager;
        this.context = context;
    }

    public void setList(List<User> list){
        this.list = list;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.user_card_view,parent,false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = list.get(position);
        holder.bind(user,requestManager,context);
    }

    public User getUser(int position){
        return list.get(position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
