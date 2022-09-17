package com.findfriends.mycompany.findfriends.activities;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.findfriends.mycompany.findfriends.Api.UserApi;
import com.findfriends.mycompany.findfriends.Base.BaseActivity;
import com.findfriends.mycompany.findfriends.Models.User;
import com.findfriends.mycompany.findfriends.R;
import com.findfriends.mycompany.findfriends.Utils.AppConstants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MatchActivity extends BaseActivity {



    @BindView(R.id.first_image)
    ImageView firstImage;
    @BindView(R.id.second_image) ImageView secondImage;
    @BindView(R.id.matched_text_details)
    TextView matchedTextDetails;

    private User user;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);
        ButterKnife.bind(this);
        currentUser = getIntent().getParcelableExtra(AppConstants.CURRENT_TRAVELER_TAG_FRIEND_FRAGMENT);
        user = getIntent().getParcelableExtra(AppConstants.TRAVELER_TAG_FRIEND_FRAGMENT);
        configureMatchLayout();
    }

    @OnClick(R.id.send_message_button)
    public void onSendMessage(){
        UserApi.updateChatList(currentUser.getUid(),user.getUid());
        Intent intent = new Intent(MatchActivity.this,MessagesActivity.class);
        intent.putExtra(AppConstants.TRAVELER_TAG_FRIEND_FRAGMENT,user);
        intent.putExtra(AppConstants.CURRENT_TRAVELER_TAG_FRIEND_FRAGMENT,currentUser);
        intent.putExtra(AppConstants.INDICE_TAG,1);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.keep_searching_button)
    public void onKeepSearching(){
        startActivity(new Intent(MatchActivity.this,LoginActivity.class));
        finish();
    }

    private void configureMatchLayout() {
        if(currentUser.getImageList().isEmpty()){
            if(currentUser.getGender().equals("male"))
                Glide.with(this).load(getResources().getDrawable(R.drawable.male_photo))
                        .apply(RequestOptions.circleCropTransform())
                        .into(firstImage);
            else
                Glide.with(this).load(getResources().getDrawable(R.drawable.female_photo))
                        .apply(RequestOptions.circleCropTransform())
                        .into(firstImage);
        }
        else
            Glide.with(this).load(currentUser.getImageList().get(0))
                    .apply(RequestOptions.circleCropTransform())
                    .into(firstImage);

        if(user.getImageList().isEmpty()){
            if(user.getGender().equals("male"))
                Glide.with(this).load(getResources().getDrawable(R.drawable.male_photo))
                        .apply(RequestOptions.circleCropTransform())
                        .into(secondImage);
            else
                Glide.with(this).load(getResources().getDrawable(R.drawable.female_photo))
                        .apply(RequestOptions.circleCropTransform())
                        .into(secondImage);
        }
        else
            Glide.with(this).load(user.getImageList().get(0))
                    .apply(RequestOptions.circleCropTransform())
                    .into(secondImage);

        String str = "You and "+user.getUserName()+" have liked eacher other.";
        matchedTextDetails.setText(str);
    }

    @Override
    public void onBackPressed() { }
}
