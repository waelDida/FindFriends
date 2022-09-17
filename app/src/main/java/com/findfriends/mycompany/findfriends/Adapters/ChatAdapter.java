package com.findfriends.mycompany.findfriends.Adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bumptech.glide.RequestManager;
import com.findfriends.mycompany.findfriends.R;
import com.findfriends.mycompany.findfriends.Models.Message;
import com.findfriends.mycompany.findfriends.Models.User;
import com.findfriends.mycompany.findfriends.Views.MessageViewHolder;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class ChatAdapter extends FirestoreRecyclerAdapter<Message,MessageViewHolder> {

    //FOR DATA
    private final RequestManager glide;
    private final String idCurrentUser;

    private User currentUser, user;


    //FOR COMMUNICATION
    private Listener callback;

    private Context context;

    public ChatAdapter(@NonNull FirestoreRecyclerOptions<Message> options,RequestManager glide, Listener callback, String idCurrentUser, User currentUser, User user, Context context) {
        super(options);
        this.glide = glide;
        this.callback = callback;
        this.idCurrentUser = idCurrentUser;
        this.currentUser = currentUser;
        this.user = user;
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull MessageViewHolder holder, int position, @NonNull Message model) {
        holder.updateWithMessage(model, this.idCurrentUser, this.glide, currentUser, user,context);
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MessageViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_chat_item, parent, false));
    }

    public interface Listener {
        void onDataChanged();
    }
}
