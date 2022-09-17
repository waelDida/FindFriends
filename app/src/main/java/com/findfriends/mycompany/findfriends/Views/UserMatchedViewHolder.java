package com.findfriends.mycompany.findfriends.Views;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.findfriends.mycompany.findfriends.Models.User;
import com.findfriends.mycompany.findfriends.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserMatchedViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.user_matched_image)
    CircleImageView userImage;
    @BindView(R.id.user_matched_name)
    TextView userName;

    public UserMatchedViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    public void Bind(User user, RequestManager requestManager, Context context){
        if(user.getImageList().isEmpty()){
            if(user.getGender().equals("male"))
                requestManager.load(context.getResources().getDrawable(R.drawable.male_photo))
                        .into(userImage);
            else
                requestManager.load(context.getResources().getDrawable(R.drawable.female_photo))
                        .into(userImage);
        }
        else
            requestManager
                    .load(user.getImageList().get(0))
                    .apply(new RequestOptions().override(250,350))
                    .thumbnail(0.25f)
                    .into(userImage);

        userName.setText(user.getUserName());
    }
}
