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
import com.findfriends.mycompany.findfriends.Views.UserMatchedViewHolder;

import java.util.List;

public class UserMatchedAdapter extends RecyclerView.Adapter<UserMatchedViewHolder> {

    private List<User> list;
    private RequestManager requestManager;
    private Context context;

    public UserMatchedAdapter(RequestManager requestManager, Context context){
        this.requestManager = requestManager;
        this.context = context;
    }

    @NonNull
    @Override
    public UserMatchedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.matched_user_view,parent,false);
        return new UserMatchedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserMatchedViewHolder holder, int position) {
        User user = list.get(position);
        holder.Bind(user,requestManager,context);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setList(List<User> list){
        this.list = list;
    }

    public User getTraveler(int position){
        return list.get(position);
    }

    public void setFilter(List<User> filtredList){
        list = filtredList;
        notifyDataSetChanged();
    }
}
