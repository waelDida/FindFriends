package com.findfriends.mycompany.findfriends.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.core.app.NavUtils;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.findfriends.mycompany.findfriends.Api.ChatApi;
import com.findfriends.mycompany.findfriends.Api.UserApi;
import com.findfriends.mycompany.findfriends.Base.BaseActivity;
import com.findfriends.mycompany.findfriends.Models.Chat;
import com.findfriends.mycompany.findfriends.Models.User;
import com.findfriends.mycompany.findfriends.R;
import com.findfriends.mycompany.findfriends.Utils.AppConstants;
import com.findfriends.mycompany.findfriends.Utils.GdprPref;
import com.firebase.ui.auth.AuthUI;
import com.google.ads.consent.ConsentForm;
import com.google.ads.consent.ConsentFormListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.consent.DebugGeography;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.yahoo.mobile.client.android.util.rangeseekbar.RangeSeekBar;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.psdev.licensesdialog.LicensesDialog;

public class SettingActivity extends BaseActivity implements SeekBar.OnSeekBarChangeListener{
    public final static String SETTING_DELETE_TAG = "setting_delete_tag";

    @BindView(R.id.logOut)
    Button logOutBtn;
    @BindView(R.id.delete)
    Button deleteBtn;
    @BindView(R.id.licenses_text)
    TextView licensesText;
    @BindView(R.id.privacy_policy_text) TextView privacyPolicyText;
    @BindView(R.id.terms_text) TextView termsText;
    @BindView(R.id.setting_seekbar)
    com.yahoo.mobile.client.android.util.rangeseekbar.RangeSeekBar rangeSeekBar;
    @BindView(R.id.min_age) TextView minAge;
    @BindView(R.id.max_age) TextView maxAge;
    @BindView(R.id.men_switch)
    Switch menSwitch;
    @BindView(R.id.women_switch) Switch womenSwitch;
    @BindView(R.id.setting_main_layout)
    LinearLayout mainLayout;
    @BindView(R.id.ads_settings) TextView adsSettings;
    @BindView(R.id.distance_text) TextView distanceText;
    @BindView(R.id.max_distance_seek_bar)
    SeekBar distanceSeekbar;
    @BindView(R.id.show_age_switch) Switch showAgeSwitch;

    private final static int sign_out_task = 10;
    private final static int delete_task = 20;

    private int min, max;
    private User currentUser;
    private int loginIndice;
    private String verificationId,smsCode;
    private ConsentForm form;
    private ConsentInformation consentInformation;
    private GdprPref gdprPref;
    private int progressValue;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setTitle("Settings");
        ButterKnife.bind(this);
        gdprPref = new GdprPref(this);
        currentUser = getIntent().getParcelableExtra(AppConstants.PROFILE_USER_TAG);
        verificationId = currentUser.getVerificationId();
        smsCode = currentUser.getSmsCode();
        setupOptions();
        consentInformation = ConsentInformation.getInstance(this);
        consentInformation.setDebugGeography(DebugGeography.DEBUG_GEOGRAPHY_EEA);
        if(!consentInformation.isRequestLocationInEeaOrUnknown())
            adsSettings.setVisibility(View.GONE);

        distanceSeekbar.setOnSeekBarChangeListener(this);
        distanceSeekbar.setProgress(currentUser.getMaxDistance());
        String distance = currentUser.getMaxDistance()+"Km";
        distanceText.setText(distance);
    }

    private void setupOptions(){
        if(currentUser.getShowAge().equals("true"))
            showAgeSwitch.setChecked(true);
        else
            showAgeSwitch.setChecked(false);
        if(currentUser.getShownGender().isEmpty()){
            if(currentUser.getGender().equals("male")){
                menSwitch.setChecked(false);
                womenSwitch.setChecked(true);
            }
            else{
                womenSwitch.setChecked(false);
                menSwitch.setChecked(true);
            }
        }
        else if(currentUser.getShownGender().equals("male")){
            menSwitch.setChecked(true);
            womenSwitch.setChecked(false);
        }
        else{
            menSwitch.setChecked(false);
            womenSwitch.setChecked(true);
        }

        menSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!menSwitch.isChecked()){
                    womenSwitch.setChecked(true);
                    menSwitch.setChecked(false);
                    UserApi.updateShownGender(currentUser.getUid(),"female").addOnFailureListener(onFailureListener());
                }
                else{
                    womenSwitch.setChecked(false);
                    menSwitch.setChecked(true);
                    UserApi.updateShownGender(currentUser.getUid(),"male").addOnFailureListener(onFailureListener());

                }
            }
        });
        womenSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!womenSwitch.isChecked()){
                    menSwitch.setChecked(true);
                    womenSwitch.setChecked(false);
                    UserApi.updateShownGender(currentUser.getUid(),"male").addOnFailureListener(onFailureListener());
                }
                else{
                    menSwitch.setChecked(false);
                    womenSwitch.setChecked(true);
                    UserApi.updateShownGender(currentUser.getUid(),"female").addOnFailureListener(onFailureListener());
                }
            }
        });

        menSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(menSwitch.isChecked()){
                    womenSwitch.setChecked(false);
                    UserApi.updateShownGender(currentUser.getUid(),"male").addOnFailureListener(onFailureListener());
                }
                else{
                    womenSwitch.setChecked(true);
                    UserApi.updateShownGender(currentUser.getUid(),"female").addOnFailureListener(onFailureListener());
                }

            }
        });
        womenSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(womenSwitch.isChecked()){
                    menSwitch.setChecked(false);
                    UserApi.updateShownGender(currentUser.getUid(),"female").addOnFailureListener(onFailureListener());
                }
                else{
                    menSwitch.setChecked(true);
                    UserApi.updateShownGender(currentUser.getUid(),"male").addOnFailureListener(onFailureListener());
                }
            }
        });
        showAgeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(showAgeSwitch.isChecked())
                    UserApi.updateUserInfo(currentUser.getUid(),"showAge","true");
                else
                    UserApi.updateUserInfo(currentUser.getUid(),"showAge","false");
            }
        });
        rangeSeekBar.setSelectedMaxValue(Integer.valueOf(currentUser.getMaxAge()));
        rangeSeekBar.setSelectedMinValue(Integer.valueOf(currentUser.getMinAge()));
        minAge.setText(currentUser.getMinAge());
        maxAge.setText(currentUser.getMaxAge());
        rangeSeekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar bar, Object minValue, Object maxValue) {
                min = (int) minValue;
                max = (int) maxValue;
                maxAge.setText(String.valueOf(max));
                minAge.setText(String.valueOf(min));
            }
        });
    }

    @OnClick(R.id.share_text)
    public void onShareClick(){
        final String appPackageName = getApplicationContext().getPackageName();
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        String link = "https://play.google.com/store/apps/details?id=" + appPackageName;
        sendIntent.putExtra(Intent.EXTRA_TEXT,link);
        startActivity(Intent.createChooser(sendIntent, "Share FriendTravel"));
    }

    @OnClick(R.id.ads_settings)
    public void onAdsSettingsClick(){
        requestConsent();
    }

    @OnClick(R.id.licenses_text)
    public void onLicensesClick(){
        new LicensesDialog.Builder(this)
                .setNotices(R.raw.notices)
                .build()
                .show();
    }


    @OnClick(R.id.privacy_policy_text)
    public void onPrivicyPolicyClick(){
        String url = "YOUR PRIVACY POLICY LINK";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);

    }

    @OnClick(R.id.terms_text)
    public void onTermsClick(){
        String url="YOUR TERMS OF SERVICE LINK";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    @OnClick(R.id.logOut)
    public void logOut() {
        this.signOutUser();
    }

    @OnClick(R.id.delete)
    public void deleteAccount() {
        if(currentUser.getProfileImg() == null)
            loginIndice = 1;
        else
            loginIndice = 0;
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_delete_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView title = dialog.findViewById(R.id.dialog_title);
        TextView text = dialog.findViewById(R.id.dialog_text);
        title.setText(getString(R.string.delete_account_text));
        text.setText(getString(R.string.delete_text_confirmation));
        Button yesButton = dialog.findViewById(R.id.yes_dialog_button);
        Button noButton = dialog.findViewById(R.id.no_dialog_button);
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                deleteUser();
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

    private void signOutUser() {
        AuthUI.getInstance().signOut(this).addOnSuccessListener(this.listener(sign_out_task));
    }

    private void deleteUser() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(isNetworkAvailable())
            deleteUserData(user);
        else
            Toast.makeText(SettingActivity.this,"No internet connection",Toast.LENGTH_SHORT).show();
    }

    private void deleteUserData(FirebaseUser user){
        for(String imageUrl: currentUser.getImageList())
            if(!imageUrl.equals(currentUser.getProfileImg()+"?height=500"))
                getDetailedStorageReference(imageUrl).delete();
        deleteChat();
        UserApi.deleteUser(currentUser.getUid());
        if(loginIndice == 0)
            completeDeleteFB(user);
        else
            completeDeletePH(user);
    }

    private void completeDeleteFB(final FirebaseUser user){
        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    returnToMainActivity();
                }
                else{
                    AccessToken token = AccessToken.getCurrentAccessToken();
                    AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
                    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                deleteUserAuth(user);
                            }
                            else{
                                Toast.makeText(SettingActivity.this, "Try to re authenticate first", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private void completeDeletePH(final FirebaseUser user){
        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    returnToMainActivity();
                    Log.i("TAG","account deleted directly");
                }
                else{
                    Log.i("TAG","the phone number is "+user.getPhoneNumber());
                    if(user.getPhoneNumber() != null){
                        AuthCredential credential = PhoneAuthProvider.getCredential(verificationId,smsCode);
                        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    deleteUserAuth(user);
                                }
                                else{
                                    Toast.makeText(SettingActivity.this, "Try to re authenticate first", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            }
        });

    }

    private void deleteUserAuth(FirebaseUser user){
        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    returnToMainActivity();
                    Log.i("TAG","account deleted after re authenticate");
                }
                else{
                    Log.i("TAG","delete failed after re authenticate");
                }
            }
        });

    }

    private void returnToMainActivity(){
        Intent intent = new Intent(SettingActivity.this,MainActivity.class);
        intent.putExtra(SETTING_DELETE_TAG,1);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_date,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                saveData();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveData(){
        if(min == 0 || max == 0){
            UserApi.updateMinAge(FirebaseAuth.getInstance().getUid(),currentUser.getMinAge()).addOnFailureListener(onFailureListener());
            UserApi.updateMaxAge(FirebaseAuth.getInstance().getUid(),currentUser.getMaxAge()).addOnFailureListener(onFailureListener());
        }
        else{
            if(min != Integer.valueOf(currentUser.getMinAge()) || max != Integer.valueOf(currentUser.getMaxAge())){
                UserApi.updateMinAge(FirebaseAuth.getInstance().getUid(),String.valueOf(min)).addOnFailureListener(onFailureListener());
                UserApi.updateMaxAge(FirebaseAuth.getInstance().getUid(),String.valueOf(max)).addOnFailureListener(onFailureListener());
            }
        }
        if(progressValue != currentUser.getMaxDistance())
            UserApi.updateMaxDistance(currentUser.getUid(),progressValue);
    }

    private OnSuccessListener<Void> listener(final int origin) {
        return new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                switch (origin) {
                    case sign_out_task:
                        startActivity(new Intent(SettingActivity.this, MainActivity.class));
                        finish();
                        break;
                    case delete_task:
                        Intent deleteIntent = new Intent(SettingActivity.this, MainActivity.class);
                        startActivity(deleteIntent);
                        finish();
                        break;
                    default:
                        break;
                }
            }
        };
    }

    private void deleteChat(){
        ChatApi.getDocumentId(currentUser.getUid()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<String> chatsIds = new ArrayList<>();
                for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    Chat chat = documentSnapshot.toObject(Chat.class);
                    if(chat.getSenderReceiverList().contains(currentUser.getUid()))
                        chatsIds.add(documentSnapshot.getId());
                }
                for (String id : chatsIds)
                    ChatApi.deleteChat(id);
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

        form = new ConsentForm.Builder(this,privacyUrl)
                .withListener(new ConsentFormListener() {
                    @Override
                    public void onConsentFormLoaded() {
                        // Consent form loaded successfully.
                        //Log.i("Tag","consent form loaded successfully");
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

    private void showForm(){
        if(form != null){
            form.show();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        int x = 2;
        progressValue = x + i;
        String distance = progressValue+"Km";
        distanceText.setText(distance);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        saveData();
    }
}
