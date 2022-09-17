package com.findfriends.mycompany.findfriends.Adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.RequestManager;
import com.findfriends.mycompany.findfriends.R;
import com.findfriends.mycompany.findfriends.Views.UserMatchedViewHolder;

public class UserMatchedMessageAdapter extends RecyclerView.Adapter<UserMatchedViewHolder> {

    private RequestManager requestManager;
    private Context context;

    public UserMatchedMessageAdapter(RequestManager requestManager, Context context) {
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
    public void onBindViewHolder(@NonNull UserMatchedViewHolder userMatchedViewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
