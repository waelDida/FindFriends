package com.findfriends.mycompany.findfriends.Fragments;


import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.findfriends.mycompany.findfriends.Api.UserApi;
import com.findfriends.mycompany.findfriends.Models.User;
import com.findfriends.mycompany.findfriends.R;
import com.findfriends.mycompany.findfriends.Utils.AppConstants;
import com.findfriends.mycompany.findfriends.activities.EditUserActivity;
import com.findfriends.mycompany.findfriends.activities.SettingActivity;
import com.findfriends.mycompany.findfriends.activities.UserProfileDetailActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    @BindView(R.id.profile_image) CircleImageView profileImage;
    @BindView(R.id.profile_fragment_progress)
    ProgressBar progressBar;
    @BindView(R.id.no_internet_profile_fragment)
    LinearLayout noInternetLayout;
    @BindView(R.id.main_layout) RelativeLayout mainLayout;
    @BindView(R.id.name_age_text)
    TextView nameAgeText;
    @BindView(R.id.profession) TextView professionText;



    private FragmentActivity activity;
    private User currentUser;
    private RequestManager requestManager;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);
        if(getActivity() != null){
            requestManager = Glide.with(getActivity());
            activity = getActivity();
        }
        if(isNetworkAvailable()){
            updateUIwhenCreate();
            noInternetLayout.setVisibility(View.GONE);
        }
        else{
            noInternetLayout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }

    }
    @OnClick(R.id.setting_img)
    public void onSettingClick(){
        Intent intent = new Intent(getActivity(), SettingActivity.class);
        intent.putExtra(AppConstants.PROFILE_USER_TAG,currentUser);
        startActivity(intent);
    }

    @OnClick(R.id.edit_img)
    public void onEditClick(){
        profileImage.setClickable(false);
        Intent intent = new Intent(getActivity(), EditUserActivity.class);
        intent.putExtra(AppConstants.PROFILE_USER_TAG,currentUser);
        startActivity(intent);
    }

    @OnClick(R.id.profile_image)
    public void onProfileClick(){
        Intent intent = new Intent(getActivity(),UserProfileDetailActivity.class);
        intent.putExtra(AppConstants.TRAVELER_TAG_FRIEND_FRAGMENT,currentUser);
        intent.putExtra(AppConstants.ACTIVITY_TAG,5);
        startActivity(intent);
    }

    private void updateUIwhenCreate() {
        mainLayout.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            UserApi.getUser(FirebaseAuth.getInstance().getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    currentUser = documentSnapshot.toObject(User.class);
                    if (currentUser != null) {
                        if (!currentUser.getImageList().isEmpty()) {
                            requestManager
                                    .load(currentUser.getImageList().get(0))
                                    .apply(new RequestOptions().placeholder(new ColorDrawable(activity.getResources().getColor(R.color.loadImageColor))).override(300, 300))
                                    .into(profileImage);
                        } else if (currentUser.getGender().equals("male")) {
                            requestManager
                                    .load(activity.getResources().getDrawable(R.drawable.user_male))
                                    .apply(new RequestOptions().placeholder(new ColorDrawable(activity.getResources().getColor(R.color.loadImageColor))))
                                    .into(profileImage);
                        } else if (currentUser.getGender().equals("female")) {
                            requestManager
                                    .load(activity.getResources().getDrawable(R.drawable.user_female))
                                    .apply(new RequestOptions().placeholder(new ColorDrawable(activity.getResources().getColor(R.color.loadImageColor))))
                                    .thumbnail(0.5f)
                                    .into(profileImage);
                        }
                        String str = null;
                        if(currentUser.getShowAge().equals("true")){
                            str = currentUser.getUserName();
                        }
                        else{
                            try {
                                str = currentUser.getUserName()+", "+String.valueOf(calculateAge(getDateFromString(currentUser.getBirthday())));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                        nameAgeText.setText(str);
                        professionText.setText(currentUser.getActivity());
                        progressBar.setVisibility(View.INVISIBLE);
                        mainLayout.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(getActivity() != null){
            requestManager = Glide.with(getActivity());
            activity = getActivity();
        }
    }

    private void refresh(){
        if(isNetworkAvailable()){
            noInternetLayout.setVisibility(View.GONE);
            updateUIwhenCreate();
        }
    }

    @OnClick(R.id.profile_fragment_refresh)
    public void onRefreshClick(){
        refresh();
    }

    private int calculateAge(Date birthDate){
        Calendar birth = Calendar.getInstance();
        birth.setTime(birthDate);
        Calendar today = Calendar.getInstance();
        int yearDifference = today.get(Calendar.YEAR)
                - birth.get(Calendar.YEAR);

        if (today.get(Calendar.MONTH) < birth.get(Calendar.MONTH)) {
            yearDifference--;
        } else {
            if (today.get(Calendar.MONTH) == birth.get(Calendar.MONTH)
                    && today.get(Calendar.DAY_OF_MONTH) < birth
                    .get(Calendar.DAY_OF_MONTH)) {
                yearDifference--;
            }
        }
        return yearDifference;
    }

    private Date getDateFromString(String stringDate) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        return df.parse(stringDate);
    }

    protected boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
