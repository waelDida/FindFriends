package com.findfriends.mycompany.findfriends.activities;

import android.content.Intent;
import android.location.Location;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import com.findfriends.mycompany.findfriends.Api.UserApi;
import com.findfriends.mycompany.findfriends.Base.BaseActivity;
import com.findfriends.mycompany.findfriends.Fragments.FriendsFragment;
import com.findfriends.mycompany.findfriends.Fragments.HomeFragment;
import com.findfriends.mycompany.findfriends.Fragments.ProfileFragment;
import com.findfriends.mycompany.findfriends.Models.User;
import com.findfriends.mycompany.findfriends.R;
import com.findfriends.mycompany.findfriends.Utils.AppConstants;
import com.findfriends.mycompany.findfriends.Utils.GdprPref;
import com.findfriends.mycompany.findfriends.Views.BottomNavigationViewHelper;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends BaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigation;

    Fragment HomeFragment;
    Fragment FriendsFragment;
    Fragment ProfileFragment;
    Fragment activeFragment;

    public InterstitialAd mInterstitialAd;
    private GdprPref gdprPref;
    private FusedLocationProviderClient fusedLocationClient;


    private boolean requestingLocationUpdates;
    private LocationCallback mLocationCallback;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private Location updatedLocation;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigation);
        bottomNavigation.setOnNavigationItemSelectedListener(this);
        checkUserAvailable();
        int activityTag = getIntent().getIntExtra(AppConstants.ACTIVITY_TAG, 0);
        String activityTagFCM = getIntent().getStringExtra(AppConstants.ACTIVITY_TAG_FCM);
        if(activityTag == 0){
            if(activityTagFCM != null){
                bottomNavigation.setSelectedItemId(R.id.action_friends);
                loadFragment(new FriendsFragment());
            }
            else{
                loadFragment(new HomeFragment());
            }
        }
        else if(activityTag == 5){
            bottomNavigation.setSelectedItemId(R.id.action_profile);
            loadFragment(new ProfileFragment());
        }
        else{
            bottomNavigation.setSelectedItemId(R.id.action_friends);
            loadFragment(new FriendsFragment());
        }

        gdprPref = new GdprPref(this);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitila_id_test));
        mInterstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdClosed() {
                if(gdprPref.getStatus() == 1)
                    loadPersonalizedAds();
                else if(gdprPref.getStatus() == 2)
                    loadNonPersonalizedAds();
                else
                    loadPersonalizedAds();

            }
        });

        fusedLocationClient =  LocationServices.getFusedLocationProviderClient(this);
        getLocationRequest();
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    updatedLocation = location;
                    if(updatedLocation.getLatitude() != 0.0 && updatedLocation.getLongitude() != 0.0){
                        //updateLocation(latitude,longitude,updatedLocation);
                        if(user.getLatitude() != updatedLocation.getLatitude())
                            UserApi.updateLocation(user.getUid(),"latitude",updatedLocation.getLatitude());

                        if(user.getLongitude() != updatedLocation.getLongitude())
                            UserApi.updateLocation(user.getUid(),"longitude",updatedLocation.getLongitude());

                        stopLocationUpdates();


                    }
                    else{
                        Log.i("LOCATION","the last latitude "+lastLocation.getLatitude());
                        Log.i("LOCATION","the last  longitude "+lastLocation.getLongitude());
                    }
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        getLastLocation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(activeFragment instanceof ProfileFragment)
            loadFirstFragment(new ProfileFragment());
    }

    public void loadPersonalizedAds(){
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }

    public void loadNonPersonalizedAds(){
        Bundle extras = new Bundle();
        extras.putString("npa", "1");
        mInterstitialAd.loadAd(new AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter.class, extras).build());
    }

    public boolean isInterstitialLoaded(){
        return mInterstitialAd.isLoaded();
    }

    public void showInterstitial(){
        mInterstitialAd.show();
    }

    private void loadFragment(Fragment fragment){
        if(fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.login_container, fragment)
                    .commit();
        }
    }
    private void loadFirstFragment(Fragment fragment){
        if(fragment != null)
            getSupportFragmentManager().beginTransaction().add(R.id.login_container,fragment)
                    .commit();
    }

    private void checkUserAvailable(){
        if(getCurrentUser() == null){
            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            UserApi.getUser(getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    user = documentSnapshot.toObject(User.class);
                    if(user == null){
                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        if(user.getBirthday().isEmpty()){
                            Intent intent = new Intent(LoginActivity.this,DataFacebookActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        startLocationUpdates();
                    }
                }
            });
        }
    }

    private void getLastLocation(){
        try{
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                lastLocation = location;
                            }
                        }
                    });
        }catch (SecurityException e){ e.getMessage();}
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_home:
                HomeFragment = new HomeFragment();
                loadFragment(HomeFragment);
                activeFragment = HomeFragment;
                if(requestingLocationUpdates)
                    stopLocationUpdates();
                return true;
            case R.id.action_friends:
                FriendsFragment = new FriendsFragment();
                loadFragment(FriendsFragment);
                activeFragment = FriendsFragment;
                if(requestingLocationUpdates)
                    stopLocationUpdates();
                return true;
            case R.id.action_profile:
                ProfileFragment = new ProfileFragment();
                loadFragment(ProfileFragment);
                activeFragment = ProfileFragment;
                if(requestingLocationUpdates)
                    stopLocationUpdates();
                return true;
        }
        return false;
    }

    private void getLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void startLocationUpdates() {
        requestingLocationUpdates = true;
        try{
            fusedLocationClient.requestLocationUpdates(locationRequest,
                    mLocationCallback,
                    null );
        }catch(SecurityException e){e.getMessage();}
    }

    private void stopLocationUpdates() {
        requestingLocationUpdates = false;
        fusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(requestingLocationUpdates)
            stopLocationUpdates();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}

