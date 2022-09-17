package com.findfriends.mycompany.findfriends.activities;

import android.content.Intent;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.findfriends.mycompany.findfriends.Api.UserApi;
import com.findfriends.mycompany.findfriends.Base.BaseActivity;
import com.findfriends.mycompany.findfriends.R;
import com.findfriends.mycompany.findfriends.Utils.AppConstants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PhoneConfirmation extends BaseActivity {

    @BindView(R.id.phone_confirmation_layout)
    RelativeLayout mainLayout;
    @BindView(R.id.phone_number)
    TextView phoneNumberText;
    @BindView(R.id.sms_code_edit)
    EditText smsCodeEdit;
    @BindView(R.id.confirmation_progress)
    ProgressBar progressBar;
    @BindView(R.id.resend_text)
    TextView resendText;

    private String verificationId;
    private PhoneAuthProvider.ForceResendingToken resendToken;
    private FirebaseAuth mAuth;
    private String phoneNumber;
    private  String code;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_confirmation);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        phoneNumber = getIntent().getStringExtra(PhoneVerification.PHONE_NUMBER);
        phoneNumberText.setText(phoneNumber);
        sendVerificationCode(phoneNumber);
    }

    @OnClick(R.id.login_button)
    public void onLoginClick(){
       // startActivity(new Intent(PhoneConfirmation.this,DataPhoneActivity.class));
        String code = smsCodeEdit.getText().toString().trim();
        if(code.isEmpty()){
            smsCodeEdit.setError("Enter your code");
            smsCodeEdit.requestFocus();
        }
        else{
            progressBar.setVisibility(View.VISIBLE);
            verifyCode(code);
        }

    }

    @OnClick(R.id.resend_text)
    public void onResendClick(){
        resendCode();
    }

    private void sendVerificationCode(String phoneNumber){
        progressBar.setVisibility(View.VISIBLE);
        resendText.setClickable(false);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                mCallback);
    }

    private void verifyCode(String code){
        try{
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId,code);
            signInWithCredentials(credential,code);
        }
        catch (Exception e){
            Toast toast = Toast.makeText(getApplicationContext(), "Verification Code is wrong, try again", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }
    }

    private void signInWithCredentials(PhoneAuthCredential credential, final String code) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    UserApi.getUser(FirebaseAuth.getInstance().getUid()).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                DocumentSnapshot document = task.getResult();
                                if(document.exists()){
                                    getMessageToken();
                                    Intent intent = new Intent(PhoneConfirmation.this,LoginActivity.class);
                                    //intent.putExtra(AppConstants.LAST_LOCATION_TAG,1);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    UserApi.updateVerificationId(FirebaseAuth.getInstance().getUid(),verificationId);
                                    UserApi.updateSmsCode(FirebaseAuth.getInstance().getUid(),code);
                                    //finish();
                                    startActivity(intent);
                                }else{
                                    Intent intent = new Intent(PhoneConfirmation.this,DataPhoneActivity.class);
                                    //intent.putExtra(AppConstants.LAST_LOCATION_TAG,1);
                                    intent.putExtra(AppConstants.VERIFICATION_ID,verificationId);
                                    intent.putExtra(AppConstants.SMS_CODE,code);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    //finish();
                                    startActivity(intent);
                                }
                            }
                        }
                    });
                }
                else{
                    Snackbar.make(mainLayout,"Error occurred",Snackbar.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void getMessageToken() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(PhoneConfirmation.this,
                new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        String newToken = instanceIdResult.getToken();
                        UserApi.updateRegistrationToken(FirebaseAuth.getInstance().getUid(), newToken);
                    }
                });
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
            resendToken = forceResendingToken;
            progressBar.setVisibility(View.GONE);
            resendText.setClickable(true);
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            code = phoneAuthCredential.getSmsCode();
            if(code != null){
                smsCodeEdit.setText(code);
                verifyCode(code);
            }
            if(progressBar.getVisibility() == View.VISIBLE)
                progressBar.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Snackbar.make(mainLayout,e.getMessage(),Snackbar.LENGTH_SHORT).show();
            if(progressBar.getVisibility() == View.VISIBLE)
                progressBar.setVisibility(View.INVISIBLE);
        }
    };

    public void resendCode(){
        progressBar.setVisibility(View.VISIBLE);
        resendText.setClickable(false);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                mCallback,
                resendToken);
    }
}
