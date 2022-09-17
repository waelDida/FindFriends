package com.findfriends.mycompany.findfriends.Fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.findfriends.mycompany.findfriends.Adapters.UserDisplayAdapter;
import com.findfriends.mycompany.findfriends.Api.UserApi;
import com.findfriends.mycompany.findfriends.Models.User;
import com.findfriends.mycompany.findfriends.R;
import com.findfriends.mycompany.findfriends.Utils.AdsPref;
import com.findfriends.mycompany.findfriends.Utils.AppConstants;
import com.findfriends.mycompany.findfriends.Utils.GdprPref;
import com.findfriends.mycompany.findfriends.Utils.ItemClickSupport;
import com.findfriends.mycompany.findfriends.activities.LoginActivity;
import com.findfriends.mycompany.findfriends.activities.UserProfileActivity;
import com.google.ads.consent.ConsentForm;
import com.google.ads.consent.ConsentFormListener;
import com.google.ads.consent.ConsentInfoUpdateListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.consent.DebugGeography;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeFragment extends Fragment {


    @BindView(R.id.users_recycler)
    RecyclerView recycler;
    @BindView(R.id.no_internet_home_fragment)
    LinearLayout noInternetLayout;
    @BindView(R.id.home_fragment_progress)
    ProgressBar progressBar;
    @BindView(R.id.no_one_text)
    RelativeLayout noOneHereText;

    private User cUser;
    private Context context;

    private ConsentInformation consentInformation;
    private ConsentForm form;
    private GdprPref gdprPref;
    private AdsPref adsPref;

    private Activity act;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);
        act = getActivity();
        if(getContext() != null)
            context = getContext();
        consentInformation = ConsentInformation.getInstance(context);
        gdprPref = new GdprPref(context);
        adsPref = new AdsPref(context);
        if(isNetworkAvailable()) {
            configureDestinationRecyclerfirebase();
        }
        else{
            noInternetLayout.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.home_fragment_refresh)
    public void onRefreshClick(){
        if(isNetworkAvailable()){
            configureDestinationRecyclerfirebase();
        }
    }

    private void loadAds(){
        if(gdprPref.getStatus() == 1){
            if(act instanceof LoginActivity)
                ((LoginActivity) act).loadPersonalizedAds();
        }
        else if(gdprPref.getStatus() == 2){
            if(act instanceof LoginActivity)
                ((LoginActivity) act).loadNonPersonalizedAds();
        }
    }

    private void configureDestinationRecyclerfirebase() {
        progressBar.setVisibility(View.VISIBLE);
        if(noInternetLayout.getVisibility() == View.VISIBLE)
            noInternetLayout.setVisibility(View.GONE);
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            UserApi.getUser(FirebaseAuth.getInstance().getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    cUser = documentSnapshot.toObject(User.class);
                    if(cUser != null){
                        getAllUsers(cUser);
                    }
                }
            });
        }
    }

    private void getAllUsers(final User currentUser){
        UserApi.getAllUsers(currentUser.getShownGender()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<User> usersList = new ArrayList<>();
                for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    User user = documentSnapshot.toObject(User.class);
                    if(!user.getUid().equals(currentUser.getUid()) && !currentUser.getReportList().contains(user.getUid())
                            &&!currentUser.getLikesList().contains(user.getUid()) && !currentUser.getUnmatchedList().contains(user.getUid())){
                        Date date = null;
                        try {
                            date = convertToDate(user.getBirthday());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if(date != null){
                            if(calculateAge(date) >= Integer.valueOf(currentUser.getMinAge()) && calculateAge(date) <= Integer.valueOf(currentUser.getMaxAge())
                                    && (int)distance(currentUser.getLatitude(),currentUser.getLongitude(),user.getLatitude(),user.getLongitude()) <= currentUser.getMaxDistance())
                                usersList.add(user);
                        }
                        else{
                            Toast.makeText(getActivity(),"Can't calculate your age !",Toast.LENGTH_SHORT).show();
                        }


                    }
                }
                if(!usersList.isEmpty()){
                    setUpRecycler(usersList);
                }
                else{
                    progressBar.setVisibility(View.INVISIBLE);
                    noOneHereText.setVisibility(View.VISIBLE);
                }

                requestConsentInfo();
                loadAds();
            }
        });
    }

    private void setUpRecycler(List<User> list){
        RequestManager requestManager = Glide.with(context);
        UserDisplayAdapter adapter = new UserDisplayAdapter(requestManager,getContext());
        progressBar.setVisibility(View.INVISIBLE);
        recycler.setVisibility(View.VISIBLE);
        adapter.setList(list);
        recycler.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recycler.setAdapter(adapter);
        configureOnclickRecycleView(adapter);
    }

    private void configureOnclickRecycleView(final UserDisplayAdapter userDisplayAdapter){
        ItemClickSupport.addTo(recycler,R.layout.user_card_view).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        if(adsPref.getClickNumber() < 5){
                            openDetailActivity(userDisplayAdapter,position);
                            adsPref.setClickNumber(adsPref.getClickNumber() + 1);
                        }
                        else{
                            adsPref.setClickNumber(0);
                            if (act instanceof LoginActivity) {
                                if(((LoginActivity) act).isInterstitialLoaded()) {
                                    ((LoginActivity) act).showInterstitial();
                                }
                                else {
                                    openDetailActivity(userDisplayAdapter,position);
                                }
                            }
                        }
                    }
                }
        );
    }

    private void openDetailActivity(final UserDisplayAdapter adapter, int position){
        User user = adapter.getUser(position);
        Intent intent = new Intent(getActivity(),UserProfileActivity.class);
        intent.putExtra(AppConstants.USER_TAG,user);
        intent.putExtra(AppConstants.CURRENT_USER_TAG,cUser);
        startActivity(intent);
    }

    private void requestConsentInfo(){
        consentInformation.setDebugGeography(DebugGeography.DEBUG_GEOGRAPHY_EEA);
        String[] publisherIds = {"pub-9789704780798889"};
        consentInformation.requestConsentInfoUpdate(publisherIds, new ConsentInfoUpdateListener() {
            @Override
            public void onConsentInfoUpdated(ConsentStatus consentStatus) {
                switch (consentStatus) {
                    case PERSONALIZED:
                        consentInformation.setConsentStatus(ConsentStatus.PERSONALIZED);
                        gdprPref.setStatus(1);
                        break;
                    case NON_PERSONALIZED:
                        consentInformation.setConsentStatus(ConsentStatus.NON_PERSONALIZED);
                        gdprPref.setStatus(2);
                        break;
                    case UNKNOWN:
                        if (consentInformation.isRequestLocationInEeaOrUnknown()) {
                            requestConsent();
                        } else {
                            gdprPref.setStatus(1);
                        }
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onFailedToUpdateConsentInfo(String errorDescription) {
                // User's consent status failed to update.
            }
        });

    }

    private void requestConsent(){
        URL privacyUrl = null;
        try {
            // TODO: Replace with your app's privacy policy URL.
            privacyUrl = new URL("https://sites.google.com/view/privacy-policy-wapp/accueil");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            // Handle error.
        }

        if(getActivity() != null){
            form = new ConsentForm.Builder(getActivity(),privacyUrl)
                    .withListener(new ConsentFormListener() {
                        @Override
                        public void onConsentFormLoaded() {
                            // Consent form loaded successfully.
                            showForm();
                        }

                        @Override
                        public void onConsentFormOpened() {
                            // Consent form was displayed.
                        }

                        @Override
                        public void onConsentFormClosed(ConsentStatus consentStatus, Boolean userPrefersAdFree) {
                            switch (consentStatus) {
                                case PERSONALIZED:
                                    consentInformation.setConsentStatus(ConsentStatus.PERSONALIZED);
                                    gdprPref.setStatus(1);
                                    break;
                                case NON_PERSONALIZED:
                                    consentInformation.setConsentStatus(ConsentStatus.NON_PERSONALIZED);
                                    gdprPref.setStatus(2);
                                    break;
                            }
                        }
                        @Override
                        public void onConsentFormError(String errorDescription) {
                            // Consent form error.
                            //Log.d("TAG", "Consent form error " + errorDescription);
                        }
                    })
                    .withPersonalizedAdsOption()
                    .withNonPersonalizedAdsOption()
                    .build();
            form.load();
        }
    }

    private void showForm(){
        if(form != null){
            form.show();
        }
    }

    private int calculateAge(Date birthDate) {
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

    private static double distance(double lat1, double lon1, double lat2, double lon2){
        lat1 = Math.toRadians(lat1);
        lon1 = Math.toRadians(lon1);
        lat2 = Math.toRadians(lat2);
        lon2 = Math.toRadians(lon2);
        double earthRadius = 6371.01; //Kilometers
        return earthRadius * Math.acos(Math.sin(lat1)*Math.sin(lat2) + Math.cos(lat1)*Math.cos(lat2)*Math.cos(lon1 - lon2));
    }

    private Date convertToDate(String stringDate) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        return df.parse(stringDate);
    }

    protected boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
