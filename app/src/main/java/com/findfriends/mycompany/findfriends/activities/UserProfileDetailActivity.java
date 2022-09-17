package com.findfriends.mycompany.findfriends.activities;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.findfriends.mycompany.findfriends.Adapters.ViewPagerAdapter;
import com.findfriends.mycompany.findfriends.Api.UserApi;
import com.findfriends.mycompany.findfriends.Base.BaseActivity;
import com.findfriends.mycompany.findfriends.Models.User;
import com.findfriends.mycompany.findfriends.R;
import com.findfriends.mycompany.findfriends.Utils.AppConstants;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.ParseException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.relex.circleindicator.CircleIndicator;

public class UserProfileDetailActivity extends BaseActivity {

    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.indicator)
    CircleIndicator circleIndicator;
    @BindView(R.id.text_about_me)
    TextView aboutMeText;
    @BindView(R.id.activity_text) TextView activityText;
    @BindView(R.id.distance_text) TextView distanceText;
    @BindView(R.id.text_name_age)
    TextView textNameAge;
    @BindView(R.id.user_no_photo)
    ImageView userNoPhoto;
    @BindView(R.id.activity_img) ImageView activityImg;

    private RequestManager requestManager;
    private User user;
    private int tag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_detail);
        getSupportActionBar().hide();
        ButterKnife.bind(this);
        requestManager = Glide.with(this);
        tag = getIntent().getIntExtra(AppConstants.ACTIVITY_TAG,0);
        user = getIntent().getParcelableExtra(AppConstants.TRAVELER_TAG_FRIEND_FRAGMENT);
        if(user != null)
            configureProfile();
    }
    @OnClick(R.id.return_back)
    public void onBackClick(){
        this.onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void configureProfile(){
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

        if(tag == 0){
            showAge();
            showDistance();
        }
        else{
            showAge();
            distanceText.setText("Less than 1 Km");
        }

        if(!user.getActivity().isEmpty())
            activityText.setText(user.getActivity());
        else{
            activityImg.setVisibility(View.GONE);
            activityText.setVisibility(View.GONE);
        }
    }

    private void showAge(){
        String nameAge="";
        if(user.getShowAge().equals("true")){
            try {
                nameAge = user.getUserName()+", "+String.valueOf(calculateAge(getDateFromString(user.getBirthday())));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        else{
            nameAge = user.getUserName();
        }
        textNameAge.setText(nameAge);
    }

    private void showDistance() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            UserApi.getUser(FirebaseAuth.getInstance().getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    User currentUser = documentSnapshot.toObject(User.class);
                    if (currentUser != null) {
                        double currentDistance = distance(currentUser.getLatitude(), currentUser.getLongitude(), user.getLatitude(), user.getLongitude());
                        if (currentDistance > 1) {
                            String str = String.valueOf((int) currentDistance) + " Km Away";
                            distanceText.setText(str);
                        } else
                            distanceText.setText("Less than 1 Km");
                    }

                }
            });
        }
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
