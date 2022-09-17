package com.findfriends.mycompany.findfriends.Views;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.findfriends.mycompany.findfriends.Models.Message;
import com.findfriends.mycompany.findfriends.Models.User;
import com.findfriends.mycompany.findfriends.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MessageViewHolder extends RecyclerView.ViewHolder {

    //ROOT VIEW
    @BindView(R.id.activity_mentor_chat_item_root_view)
    RelativeLayout rootView;

    //PROFILE CONTAINER
    @BindView(R.id.activity_mentor_chat_item_profile_container)
    FrameLayout profileContainer;
    @BindView(R.id.activity_mentor_chat_item_profile_container_profile_image)
    ImageView imageViewProfile;

    //MESSAGE CONTAINER
    @BindView(R.id.activity_mentor_chat_item_message_container) RelativeLayout messageContainer;

    //TEXT MESSAGE CONTAINER
    @BindView(R.id.activity_mentor_chat_item_message_container_text_message_container)
    LinearLayout textMessageContainer;
    @BindView(R.id.activity_mentor_chat_item_message_container_text_message_container_text_view)
    TextView textViewMessage;

    //DATE TEXT
    //@BindView(R.id.activity_mentor_chat_item_message_container_text_view_date) TextView textViewDate;


    private final int colorCurrentUser;
    private final int colorRemoteUser;

    public MessageViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        colorCurrentUser = ContextCompat.getColor(itemView.getContext(), R.color.currentMessageColor);
        colorRemoteUser = ContextCompat.getColor(itemView.getContext(), R.color.colorPrimaryMessage);
    }

    public void updateWithMessage(Message message, String currentUserId, RequestManager glide, User currentUser, User user, Context context){

        // Check if current user is the sender
        Boolean isCurrentUser = message.getSenderId().equals(currentUserId);

        // Update message TextView
        this.textViewMessage.setTextColor(isCurrentUser ? context.getResources().getColor(R.color.white) : context.getResources().getColor(R.color.senderTextColor));
        this.textViewMessage.setText(message.getMessageText());
        this.textViewMessage.setTextAlignment(isCurrentUser ? View.TEXT_ALIGNMENT_TEXT_END : View.TEXT_ALIGNMENT_TEXT_START);

        if(isCurrentUser){
            if(currentUser.getImageList().isEmpty()){
                if(currentUser.getGender().equals("male"))
                    glide.load(context.getResources().getDrawable(R.drawable.user_male)).apply(RequestOptions.circleCropTransform()).into(imageViewProfile);
                else
                    glide.load(context.getResources().getDrawable(R.drawable.user_female)).apply(RequestOptions.circleCropTransform()).into(imageViewProfile);
            }

            else
                glide.load(currentUser.getImageList().get(0))
                        .apply(new RequestOptions().override(250,350))
                        .thumbnail(0.1f)
                        .into(imageViewProfile);

        }
        else{
            if(user.getImageList().isEmpty()){
                if(user.getGender().equals("male"))
                    glide.load(context.getResources().getDrawable(R.drawable.user_male)).apply(RequestOptions.circleCropTransform()).into(imageViewProfile);
                else
                    glide.load(context.getResources().getDrawable(R.drawable.user_female)).apply(RequestOptions.circleCropTransform()).into(imageViewProfile);
            }
            else
                glide.load(user.getImageList().get(0))
                        .apply(new RequestOptions().override(250,350))
                        .thumbnail(0.1f)
                        .into(imageViewProfile);
        }


        //Update Message Bubble Color Background
        ((GradientDrawable) textMessageContainer.getBackground()).setColor(isCurrentUser ? colorCurrentUser : colorRemoteUser);


        // Update all views alignment depending is current user or not
        this.updateDesignDependingUser(isCurrentUser);
    }

    private void updateDesignDependingUser(Boolean isSender){
        // PROFILE CONTAINER
        RelativeLayout.LayoutParams paramsLayoutHeader = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        paramsLayoutHeader.addRule(isSender ? RelativeLayout.ALIGN_PARENT_RIGHT : RelativeLayout.ALIGN_PARENT_LEFT);
        this.profileContainer.setLayoutParams(paramsLayoutHeader);

        // MESSAGE CONTAINER
        RelativeLayout.LayoutParams paramsLayoutContent = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        paramsLayoutContent.addRule(isSender ? RelativeLayout.LEFT_OF : RelativeLayout.RIGHT_OF, R.id.activity_mentor_chat_item_profile_container);
        this.messageContainer.setLayoutParams(paramsLayoutContent);

        this.rootView.requestLayout();
    }
}
