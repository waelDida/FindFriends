package com.findfriends.mycompany.findfriends.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.findfriends.mycompany.findfriends.Api.UserApi;
import com.findfriends.mycompany.findfriends.Base.BaseActivity;
import com.findfriends.mycompany.findfriends.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.text.ParseException;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DataFacebookActivity extends BaseActivity {

    @BindView(R.id.main_layout_personal_data)
    RelativeLayout mainLayout;
    @BindView(R.id.birthday_edit)
    TextView birthdayEdit;
    @BindView(R.id.male_switch)
    Switch maleSwitch;
    @BindView(R.id.female_switch)
    Switch femaleSwitch;
    @BindView(R.id.progress)
    ProgressBar progress;
    @BindView(R.id.save_button)
    Button saveButton;

    private String birthday, gender, shownGender;

    private Location lastLocation;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_data);
        ButterKnife.bind(this);
        fusedLocationClient =  LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getLastLocation();
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
                                Log.i("LOCATION","the latitude is "+location.getLatitude());
                                Log.i("LOCATION","the longitude is "+location.getLongitude());
                                if(getCurrentUser() != null){
                                    UserApi.updateLocation(getCurrentUser().getUid(),"latitude",lastLocation.getLatitude());
                                    UserApi.updateLocation(getCurrentUser().getUid(),"longitude",lastLocation.getLongitude());
                                }

                            }
                        }
                    });
        }catch (SecurityException e){ e.getMessage();}
    }

    @OnClick(R.id.save_button)
    public void onSaveClick() {
        if(birthdayEdit.getText().toString().equals("Birthday")){
            Snackbar.make(mainLayout,"Select your birthday",Snackbar.LENGTH_SHORT).show();
        }
        else if(!maleSwitch.isChecked() && !femaleSwitch.isChecked()){
            Snackbar.make(mainLayout,"Select your gender",Snackbar.LENGTH_SHORT).show();
        }
        else{
            if(isNetworkAvailable()){
                int currentAge = 0;
                progress.setVisibility(View.VISIBLE);
                saveButton.setClickable(false);
                try {
                    currentAge = calculateAge(getDateFromString(birthdayEdit.getText().toString()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if(currentAge>= 18){
                    if(maleSwitch.isChecked()){
                        gender = "male";
                        shownGender = "female";
                    }
                    else{
                        gender  = "female";
                        shownGender = "male";
                    }
                    birthday = birthdayEdit.getText().toString();
                    if(getCurrentUser() != null){
                        UserApi.updateUserInfo(getCurrentUser().getUid(),"birthday",birthday);
                        UserApi.updateUserInfo(getCurrentUser().getUid(),"gender",gender);
                        UserApi.updateUserInfo(getCurrentUser().getUid(),"shownGender",shownGender);
                    }
                    getMessageToken();
                    progress.setVisibility(View.INVISIBLE);
                    Intent intent = new Intent(DataFacebookActivity.this,LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
                else if (currentAge <= 0) {
                    Snackbar.make(mainLayout, "Invalid age!", Snackbar.LENGTH_SHORT).show();
                    progress.setVisibility(View.INVISIBLE);
                    saveButton.setClickable(true);
                }
                else{
                    Snackbar.make(mainLayout,"Your age is "+currentAge+" You are under 18",Snackbar.LENGTH_SHORT).show();
                    progress.setVisibility(View.INVISIBLE);
                    saveButton.setClickable(true);
                }
            }
            else{
                Snackbar.make(mainLayout,getResources().getString(R.string.no_internet),Snackbar.LENGTH_SHORT).show();
                progress.setVisibility(View.INVISIBLE);
                saveButton.setClickable(true);
            }
        }
    }

    @OnClick(R.id.birthday_edit)
    public void onBirthdayClick(){
        //hideKeyboard();
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                DataFacebookActivity.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                mDatasetListener,
                year,month,day
        );
        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        datePickerDialog.show();
    }

    private DatePickerDialog.OnDateSetListener
            mDatasetListener = new DatePickerDialog.OnDateSetListener(){

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            String date = String.format("%02d",(month+1))+"/"+String.format("%02d",dayOfMonth)+"/"+year;
            birthdayEdit.setText(date);
        }
    };

    private void getMessageToken(){
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(DataFacebookActivity.this,
                new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        String newToken = instanceIdResult.getToken();
                        UserApi.updateRegistrationToken(FirebaseAuth.getInstance().getUid(),newToken);
                    }
                });
    }

   /* private void hideKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager)this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if(inputMethodManager.isAcceptingText())
            inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
    }*/
}
