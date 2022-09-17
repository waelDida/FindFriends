package com.findfriends.mycompany.findfriends.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import com.google.android.material.snackbar.Snackbar;
import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.findfriends.mycompany.findfriends.Adapters.ViewPagerAdapter;
import com.findfriends.mycompany.findfriends.Api.ChatApi;
import com.findfriends.mycompany.findfriends.Api.UserApi;
import com.findfriends.mycompany.findfriends.Base.BaseActivity;
import com.findfriends.mycompany.findfriends.Models.User;
import com.findfriends.mycompany.findfriends.R;
import com.findfriends.mycompany.findfriends.Utils.AppConstants;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.relex.circleindicator.CircleIndicator;

public class UserProfileActivity extends BaseActivity {

    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.indicator)
    CircleIndicator circleIndicator;
    @BindView(R.id.like_traveler_layout)
    LinearLayout layout;
    @BindView(R.id.love_image_button)
    ImageButton loveImageButton;
    @BindView(R.id.return_image_button) ImageButton deleteImageButton;
    @BindView(R.id.return_back) ImageButton returnBack;
    @BindView(R.id.image_tag)
    ImageView likeTag;
    @BindView(R.id.text_about_me)
    TextView aboutMeText;
    @BindView(R.id.activity_text) TextView activityText;
    @BindView(R.id.distance_text) TextView distanceText;
    @BindView(R.id.report_button)
    Button reportButton;
    @BindView(R.id.text_name_age) TextView textNameAge;
    @BindView(R.id.activity_traveler_main)
    RelativeLayout relativeLayout;
    @BindView(R.id.user_no_photo)
    ImageView userNoPhoto;
    @BindView(R.id.activity_img)
    ImageView activityImg;

    private User user;
    private User currentUser;
    //private User currentUserOff;
    private Animation returnButtonAnim,likeSymbolAnim;
    private int activityTag;
    private RequestManager requestManager;
    private double currentDistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        getSupportActionBar().hide();
        ButterKnife.bind(this);
        requestManager = Glide.with(this);
        activityTag = getIntent().getIntExtra(AppConstants.ACTIVITY_TAG,0);
        configureProfile();
        setUpLikeLayout();

        returnButtonAnim = AnimationUtils.loadAnimation(this,R.anim.anim);
        likeSymbolAnim = AnimationUtils.loadAnimation(this,R.anim.like_anim);
    }

    private void setUpLikeLayout(){
        if(!isNetworkAvailable())
            layout.setVisibility(View.INVISIBLE);
    }

    private void configureProfile(){
        user = getIntent().getParcelableExtra(AppConstants.USER_TAG);
        //currentUserOff = getIntent().getParcelableExtra(AppConstants.CURRENT_USER_TAG);
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            UserApi.getUser(FirebaseAuth.getInstance().getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    currentUser = documentSnapshot.toObject(User.class);
                    if(currentUser != null){
                        currentDistance = distance(currentUser.getLatitude(),currentUser.getLongitude(),user.getLatitude(),user.getLongitude());
                        if(currentDistance > 1){
                            String str = String.valueOf((int)currentDistance)+ getString(R.string.km_away);
                            distanceText.setText(str);
                        }
                        else
                            distanceText.setText(getString(R.string.less_than_km));
                    }

                }
            });
        }

        if(user.getImageList().isEmpty()){
            userNoPhoto.setVisibility(View.VISIBLE);
            if(user.getGender().equals("female"))
                requestManager.load(getResources().getDrawable(R.drawable.female_photo))
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(userNoPhoto);
            else
                requestManager.load(getResources().getDrawable(R.drawable.male_photo))
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(userNoPhoto);
        }
        else{
            userNoPhoto.setVisibility(View.INVISIBLE);
            ViewPagerAdapter adapter = new ViewPagerAdapter(this,user.getImageList(),requestManager);
            viewPager.setAdapter(adapter);
            if(adapter.getCount() > 1)
                circleIndicator.setViewPager(viewPager);
        }
        aboutMeText.setText(user.getAboutMe());
        reportButton.setText(String.valueOf("REPORT "+user.getUserName()));
        String nameAge="";
        if(user.getShowAge().equals("false")){
            nameAge = user.getUserName();
        }
        else{
            try {
                nameAge = user.getUserName()+", "+String.valueOf(calculateAge(getDateFromString(user.getBirthday())));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        textNameAge.setText(nameAge);
        if(!user.getActivity().isEmpty())
            activityText.setText(user.getActivity());
        else{
            activityImg.setVisibility(View.GONE);
            activityText.setVisibility(View.GONE);
        }



    }

    @OnClick(R.id.love_image_button)
    public void onLoveClick(){
        if(isNetworkAvailable()){
            setUpLike();
        }
        else
            Snackbar.make(relativeLayout,getString(R.string.popup_no_internet),Snackbar.LENGTH_SHORT).show();
    }

    private void setUpLike(){
        imageButtonsBehavior();
        setUplikeDetailed();
    }

    private void setUplikeDetailed(){
        UserApi.getUser(user.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User userUpdated = documentSnapshot.toObject(User.class);
                if (userUpdated != null) {
                    UserApi.updateLikesList(currentUser.getUid(), user.getUid()).addOnFailureListener(onFailureListener());
                    if(userUpdated.getLikesList().contains(currentUser.getUid())){
                        List<String> senderReceiverList = new ArrayList<>();
                        senderReceiverList.add(currentUser.getUid());
                        senderReceiverList.add(userUpdated.getUid());
                        ChatApi.createChat(senderReceiverList, user.getUid(), currentUser.getUserName(), getCurrentDate(Calendar.getInstance().getTime()));
                        if (activityTag == 0)
                            configureMatchLayout();
                    }
                }
            }
        });
    }


    @OnClick(R.id.return_image_button)
    public void ondeleteClick(){
        if(activityTag == 0){
            this.onBackPressed();
        }
        else{
            Intent intent = new Intent(UserProfileActivity.this,LikesActivity.class);
            startActivity(intent);
        }
    }

    @OnClick(R.id.return_back)
    public void onReturnClick(){
        if(activityTag == 0){
            Intent intent = new Intent(UserProfileActivity.this,LoginActivity.class);
            startActivity(intent);
        }
        else{
            Intent intent = new Intent(UserProfileActivity.this,LikesActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        if(activityTag == 0){
            if(returnBack.getVisibility() == View.VISIBLE){
                Intent intent = new Intent(UserProfileActivity.this,LoginActivity.class);
                startActivity(intent);
            }
            else
                super.onBackPressed();
        }
    }

    @OnClick(R.id.report_button)
    public void onReportClick(){
        if(getCurrentUser() != null){
            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.custom_delete_dialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            TextView title = dialog.findViewById(R.id.dialog_title);
            TextView text = dialog.findViewById(R.id.dialog_text);
            title.setText(getString(R.string.report_text));
            text.setText(getString(R.string.report_text_confirmation));
            Button yesButton = dialog.findViewById(R.id.yes_dialog_button);
            Button noButton = dialog.findViewById(R.id.no_dialog_button);
            yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    UserApi.getUser(user.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            User user = documentSnapshot.toObject(User.class);
                            if(user == null)
                                Toast.makeText(UserProfileActivity.this,"Error occured",Toast.LENGTH_SHORT).show();
                            else{
                                imageButtonReportBehavior();
                                UserApi.updateReportList(getCurrentUser().getUid(),user.getUid()).addOnFailureListener(onFailureListener());
                            }

                        }
                    });
                }
            });
            noButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }
    private void imageButtonsBehavior(){
        likeTag.setVisibility(View.VISIBLE);
        returnBack.setVisibility(View.VISIBLE);
        returnBack.setAnimation(returnButtonAnim);
        likeTag.setAnimation(likeSymbolAnim);
        loveImageButton.setClickable(false);
        deleteImageButton.setClickable(false);
        loveImageButton.setBackground(getResources().getDrawable(R.drawable.heart_grey));
        deleteImageButton.setBackground(getResources().getDrawable(R.drawable.delete_grey));
        reportButton.setEnabled(false);
    }

    private void imageButtonReportBehavior(){
        returnBack.setVisibility(View.VISIBLE);
        returnBack.setAnimation(returnButtonAnim);
        loveImageButton.setClickable(false);
        deleteImageButton.setClickable(false);
        loveImageButton.setBackground(getResources().getDrawable(R.drawable.heart_grey));
        deleteImageButton.setBackground(getResources().getDrawable(R.drawable.delete_grey));
        reportButton.setEnabled(false);
    }

    private void configureMatchLayout(){
        returnBack.setVisibility(View.INVISIBLE);
        returnBack.setEnabled(false);
        Intent intent = new Intent(UserProfileActivity.this,MatchActivity.class);
        intent.putExtra(AppConstants.TRAVELER_TAG_FRIEND_FRAGMENT,user);
        intent.putExtra(AppConstants.CURRENT_TRAVELER_TAG_FRIEND_FRAGMENT,currentUser);
        startActivity(intent);
    }

    private String getCurrentDate(Date date){
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        return formatter.format(date);
    }

    private static double distance(double lat1, double lon1, double lat2, double lon2){
        lat1 = Math.toRadians(lat1);
        lon1 = Math.toRadians(lon1);
        lat2 = Math.toRadians(lat2);
        lon2 = Math.toRadians(lon2);
        double earthRadius = 6371.01; //Kilometers
        return earthRadius * Math.acos(Math.sin(lat1)*Math.sin(lat2) + Math.cos(lat1)*Math.cos(lat2)*Math.cos(lon1 - lon2));
    }
}
