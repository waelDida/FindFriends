package com.findfriends.mycompany.findfriends.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.os.Bundle;
import androidx.viewpager.widget.ViewPager;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.findfriends.mycompany.findfriends.Adapters.MainPagerAdapter;
import com.findfriends.mycompany.findfriends.Api.UserApi;
import com.findfriends.mycompany.findfriends.Base.BaseActivity;
import com.findfriends.mycompany.findfriends.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.relex.circleindicator.CircleIndicator;

public class MainActivity extends BaseActivity {


    @BindView(R.id.relativeLayout)
    RelativeLayout mainLayout;
    @BindView(R.id.facebook_login_button)
    Button facebookLoginButton;
    @BindView(R.id.phone_number_login_button)
    Button phoneLoginButton;
    @BindView(R.id.main_progress)
    ProgressBar progress;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.indicator)
    CircleIndicator circleIndicator;
    @BindView(R.id.privacy_service_text)TextView pptext;
    private CallbackManager mCallbackManager;
    private FirebaseAuth mAuth;
    private static final int REQUEST_CODE_ASK_PERMISSIONS= 222;
    private int buttonTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        getSupportActionBar().hide();
        String termsLink = "<html>By signing in, you agree with our <a href=\"https://sites.google.com/view/privacy-policy-wapp/accueil\">Terms of service</a> and <a href=\"https://sites.google.com/view/privacy-policy-wapp/accueil\">Privacy policy</html>";
        mAuth = FirebaseAuth.getInstance();
        mCallbackManager = CallbackManager.Factory.create();
        pptext.setText(Html.fromHtml(termsLink));
        pptext.setMovementMethod(LinkMovementMethod.getInstance());
        int deleteTag = getIntent().getIntExtra(SettingActivity.SETTING_DELETE_TAG,0);
        if(deleteTag == 1)
            Snackbar.make(mainLayout,"Account deleted successfully",Snackbar.LENGTH_SHORT).show();
        setUpViewPager();
    }


    private void facebookLogin(){
        facebookLoginButton.setEnabled(false);
        phoneLoginButton.setEnabled(false);
        LoginManager.getInstance().logInWithReadPermissions(MainActivity.this,Arrays.asList("email","public_profile"));
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                //Bundle is use for passing data as K/V pair like a Map
                Bundle bundle=new Bundle();
                //Fields is the key of bundle with values that matched the proper Permissions Reference provided by Facebook
                bundle.putString("fields","id,email,first_name");

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken()
                        , new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                try {
                                    String firstName = response.getJSONObject().getString("first_name");
                                    handleFacebookAccessToken(loginResult.getAccessToken(),firstName);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                request.setParameters(bundle);
                request.executeAsync();

            }

            @Override
            public void onCancel() {
                facebookLoginButton.setEnabled(true);
                phoneLoginButton.setEnabled(true);
            }

            @Override
            public void onError(FacebookException error) {
                Snackbar.make(mainLayout,"Error occurred"+error,Snackbar.LENGTH_LONG).show();
                facebookLoginButton.setEnabled(true);
                phoneLoginButton.setEnabled(true);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(isCurrentUserLogged())
            startActivity(new Intent(this,LoginActivity.class));
    }

    @OnClick(R.id.facebook_login_button)
    public void onFacebookClick(){
        buttonTag = 2;
        if(Build.VERSION.SDK_INT >= 23){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED ){
                requestLocationPermission();
            }
            else{
                facebookLogin();
            }
        }
        else
            facebookLogin();
    }

    @OnClick(R.id.phone_number_login_button)
    public void onPhoneNumberClick(){
        buttonTag = 1;
        if(Build.VERSION.SDK_INT >= 23){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED ){
                requestLocationPermission();
            }else{
                startActivity(new Intent(MainActivity.this, PhoneVerification.class));
            }
        }
        else
            startActivity(new Intent(MainActivity.this, PhoneVerification.class));
    }

    private void handleFacebookAccessToken(AccessToken token, final String firstName) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        AccessToken.setCurrentAccessToken(token);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if(progress.getVisibility() == View.INVISIBLE)
                                progress.setVisibility(View.VISIBLE);
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            if(user != null){
                                UserApi.getUser(user.getUid()).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if(task.isSuccessful()){
                                            DocumentSnapshot document = task.getResult();
                                            if(document.exists()){
                                                getMessageToken();
                                                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                            else{
                                                createUserInFirestore(firstName,"","","");
                                                getMessageToken();
                                                Intent intent = new Intent(MainActivity.this,DataFacebookActivity.class);
                                                startActivity(intent);
                                                finish();

                                            }
                                        }
                                    }
                                });
                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            facebookLoginButton.setEnabled(true);
                            phoneLoginButton.setEnabled(true);
                            if(progress.getVisibility() == View.VISIBLE)
                                progress.setVisibility(View.INVISIBLE);

                        }

                        // ...
                    }
                });
    }
    private void getMessageToken(){
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(MainActivity.this,
                new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        String newToken = instanceIdResult.getToken();
                        UserApi.updateRegistrationToken(FirebaseAuth.getInstance().getUid(),newToken);
                    }
                });
    }

    private void requestLocationPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("The app needs you permission to access location")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_CODE_ASK_PERMISSIONS);

                        }
                    }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).show();
        }
        else{
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_ASK_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CODE_ASK_PERMISSIONS){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if(buttonTag == 1)
                    startActivity(new Intent(MainActivity.this,PhoneVerification.class));
                else
                    facebookLogin();
            }
            else{
                Snackbar.make(mainLayout,"permission denied",Snackbar.LENGTH_SHORT).show();
            }
        }
    }


    private void createUserInFirestore(String name, String gender, String birthday, String shownGender) {
        if (this.getCurrentUser() != null) {
            String uid = this.getCurrentUser().getUid();
            String urlPicture = (this.getCurrentUser().getPhotoUrl() != null) ? this.getCurrentUser().getPhotoUrl().toString() : null;
            UserApi.createUser(uid,name,urlPicture,gender,birthday,"18","35",shownGender,getCurrentDate(Calendar.getInstance().getTime()),
                    "true",new ArrayList<String>(),new ArrayList<String>(),new ArrayList<String>(),new ArrayList<String>(),
                    new ArrayList<String>(),new ArrayList<String>(),"","",0.0,0.0,50).addOnFailureListener(this.onFailureListener());
            if(this.getCurrentUser().getPhotoUrl() != null){
                UserApi.updateImages(getCurrentUser().getUid(),getCurrentUser().getPhotoUrl().toString()+ "?height=500");
            }
        }
    }

    private void setUpViewPager(){
        List<Integer> promoList = new ArrayList<>();
        promoList.add(R.layout.promo_layout_first);
        promoList.add(R.layout.promo_layout_third);
        promoList.add(R.layout.promo_layout_second);
        MainPagerAdapter adapter = new MainPagerAdapter(this,promoList,Glide.with(this));
        viewPager.setAdapter(adapter);
        circleIndicator.setViewPager(viewPager);
    }

    private String getCurrentDate(Date date){
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        return formatter.format(date);
    }
}
