package com.findfriends.mycompany.findfriends.activities;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.findfriends.mycompany.findfriends.Base.BaseActivity;
import com.findfriends.mycompany.findfriends.R;
import com.hbb20.CountryCodePicker;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PhoneVerification extends BaseActivity {

    public static final String PHONE_NUMBER = "phone_number_verif_conf";


    @BindView(R.id.country_code_picker)
    CountryCodePicker countryCodePicker;
    @BindView(R.id.phone_number_edit)
    EditText phoneNummberEdit;
    @BindView(R.id.phone_verification_layout)
    RelativeLayout mainLayout;
    @BindView(R.id.send_phone_number_button)
    Button sendButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verification);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.send_phone_number_button)
    public void onPhoneNumberSend(){
        if(!phoneNummberEdit.getText().toString().isEmpty()){
            sendButton.setClickable(false);
            String phoneNumber = "+"+countryCodePicker.getSelectedCountryCode()+phoneNummberEdit.getText().toString();
            Intent intent = new Intent(PhoneVerification.this,PhoneConfirmation.class);
            intent.putExtra(PHONE_NUMBER,phoneNumber);
            startActivity(intent);
        }
        else{
            sendButton.setClickable(true);
            phoneNummberEdit.setError("Enter your phone number");
            phoneNummberEdit.requestFocus();
        }

    }
}
