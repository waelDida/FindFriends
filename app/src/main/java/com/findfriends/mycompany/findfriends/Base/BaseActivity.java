package com.findfriends.mycompany.findfriends.Base;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;

import com.findfriends.mycompany.findfriends.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public abstract class BaseActivity extends AppCompatActivity {

    @Nullable
    protected FirebaseUser getCurrentUser(){
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    protected boolean isCurrentUserLogged(){
        return (this.getCurrentUser() != null);
    }

    protected OnFailureListener onFailureListener(){
        return new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),getString(R.string.unknown_erro),Toast.LENGTH_SHORT).show();
            }
        };
    }

    protected StorageReference getStorageReference(){
        return FirebaseStorage.getInstance().getReference();
    }


    protected StorageReference getDetailedStorageReference(String url){
        return FirebaseStorage.getInstance().getReferenceFromUrl(url);
    }

    protected  boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    protected int calculateAge(Date birthDate){
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

    protected Date getDateFromString(String stringDate) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        return df.parse(stringDate);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
