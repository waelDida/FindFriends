package com.findfriends.mycompany.findfriends.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import androidx.core.app.NavUtils;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.findfriends.mycompany.findfriends.Api.UserApi;
import com.findfriends.mycompany.findfriends.Base.BaseActivity;
import com.findfriends.mycompany.findfriends.Models.User;
import com.findfriends.mycompany.findfriends.R;
import com.findfriends.mycompany.findfriends.Utils.AppConstants;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class EditUserActivity extends BaseActivity {

    private static final String PERMS = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final int RC_IMAGE_PERMS = 100;
    private static final int RC_CHOOSE_PHOTO = 200;
    private int imageIndice;
    private RequestManager requestManager;
    private List<ImageView> addDeleteList;
    private User currentUser;

    @BindView(R.id.main_image) ImageView mainImage;
    @BindView(R.id.main_add_delete) ImageView mainAddDelete;
    @BindView(R.id.first_image) ImageView firstImage;
    @BindView(R.id.first_add_delete) ImageView firstAddDelete;
    @BindView(R.id.second_image) ImageView secondImage;
    @BindView(R.id.second_add_delete) ImageView secondAddDelete;
    @BindView(R.id.third_image) ImageView thirdImage;
    @BindView(R.id.third_add_delete) ImageView thirdAddDelete;
    @BindView(R.id.fourth_image) ImageView fourthImage;
    @BindView(R.id.fourth_add_delete) ImageView fourthAddDelete;
    @BindView(R.id.fifth_image) ImageView fifthImage;
    @BindView(R.id.fifth_add_delete) ImageView fifthAddDelete;
    @BindView(R.id.mainProgress)
    ProgressBar mainProgress;
    @BindView(R.id.firstProgress) ProgressBar firstProgress;
    @BindView(R.id.secondProgress) ProgressBar secondProgress;
    @BindView(R.id.thirdProgress) ProgressBar thirdProgress;
    @BindView(R.id.forthProgress) ProgressBar fourthProgress;
    @BindView(R.id.fifthProgress) ProgressBar fifthProgress;
    @BindView(R.id.profile_activity_swipe)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.profile_image_layout)
    RelativeLayout relativeLayout;
    @BindView(R.id.profile_image_no_internet) RelativeLayout noInternetLayout;
    @BindView(R.id.about_me_layout)
    LinearLayout aboutmeLayout;
    @BindView(R.id.about_me_edit)
    TextInputEditText aboutMeEdit;
    @BindView(R.id.activity_edit)
    TextInputEditText activityEdit;

    public String mainImageUrlTag,firstImageUrlTag,secondImageUrlTag,thirdImageUrlTag,fourthImageUrlTag,fifthImageUrlTag;
    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);
        ButterKnife.bind(this);
        setTitle("Edit");
        user = getIntent().getParcelableExtra(AppConstants.PROFILE_USER_TAG);
        if(isNetworkAvailable()){
            updateWhenCreating();
        }
        else{
            noInternetLayout.setVisibility(View.VISIBLE);
        }

        configureSwipe();
    }

    private void configureSwipe(){
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(isNetworkAvailable()){
                    clearAllImages();
                    updateWhenCreating();
                    noInternetLayout.setVisibility(View.GONE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    },2000);
                }
                else{
                    noInternetLayout.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    },1000);
                }
            }
        });
    }

    @OnClick(R.id.main_add_delete)
    @AfterPermissionGranted(RC_IMAGE_PERMS)
    public void onMainImageClick(){
        imageIndice = 0;
        configureClickBehavior(mainImage,mainAddDelete,mainProgress,mainImageUrlTag);
    }

    @OnClick(R.id.first_add_delete)
    @AfterPermissionGranted(RC_IMAGE_PERMS)
    public void onFirstImageClick(){
        imageIndice = 1;
        configureClickBehavior(firstImage,firstAddDelete,firstProgress,firstImageUrlTag);
    }

    @OnClick(R.id.second_add_delete)
    @AfterPermissionGranted(RC_IMAGE_PERMS)
    public void onSecondImageClick(){
        imageIndice = 2;
        configureClickBehavior(secondImage,secondAddDelete,secondProgress,secondImageUrlTag);
    }

    @OnClick(R.id.third_add_delete)
    @AfterPermissionGranted(RC_IMAGE_PERMS)
    public void onThirdImageClick(){
        imageIndice = 3;
        configureClickBehavior(thirdImage,thirdAddDelete,thirdProgress,thirdImageUrlTag);
    }

    @OnClick(R.id.fourth_add_delete)
    @AfterPermissionGranted(RC_IMAGE_PERMS)
    public void onFourthImageClick(){
        imageIndice = 4;
        configureClickBehavior(fourthImage,fourthAddDelete,fourthProgress,fourthImageUrlTag);
    }

    @OnClick(R.id.fifth_add_delete)
    @AfterPermissionGranted(RC_IMAGE_PERMS)
    public void onFifthImageClick(){
        imageIndice = 5;
        configureClickBehavior(fifthImage,fifthAddDelete,fifthProgress,firstImageUrlTag);
    }

    private void configureClickBehavior(ImageView userImage, ImageView addDeleteImage,ProgressBar progressBar,String urlImageTag){
        if(userImage.getDrawable() == null)
            choosePhotoFromPhone();
        else{
            deletePhoto(userImage,addDeleteImage,progressBar,urlImageTag);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(imageIndice){
            case 0:
                handleResponse(requestCode,resultCode,data,mainProgress);
                break;
            case 1:
                handleResponse(requestCode,resultCode,data,firstProgress);
                break;
            case 2:
                handleResponse(requestCode,resultCode,data,secondProgress);
                break;
            case 3:
                handleResponse(requestCode,resultCode,data,thirdProgress);
                break;
            case 4:
                handleResponse(requestCode,resultCode,data,fourthProgress);
                break;
            case 5:
                handleResponse(requestCode,resultCode,data,fifthProgress);
                break;
        }
    }

    private void choosePhotoFromPhone(){
        if(!EasyPermissions.hasPermissions(this,PERMS)){
            EasyPermissions.requestPermissions(this,getString(R.string.popup_title_permission_files_access),RC_IMAGE_PERMS,PERMS);
            return;
        }
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,RC_CHOOSE_PHOTO);
    }

    private void handleResponse(int requestCode, int resultCode, Intent data,ProgressBar progressBar){
        if(requestCode == RC_CHOOSE_PHOTO){
            if(resultCode == RESULT_OK){
                if(isNetworkAvailable()){
                    Uri imageUri = data.getData();
                    uploadPhoto(imageUri,progressBar);
                }
                else{
                    Snackbar.make(relativeLayout,getString(R.string.popup_no_internet),Snackbar.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void uploadPhoto(final Uri imageUri, final ProgressBar progressBar){
        progressBar.setVisibility(View.VISIBLE);
        String uuid = UUID.randomUUID().toString();
        final String uid = this.getCurrentUser().getUid();
        final StorageReference imgRef = getStorageReference().child(uid+"/"+uuid);
        Bitmap bmp = null;
        try {
            bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
        byte[] data = baos.toByteArray();
        imgRef.putBytes(data).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return imgRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful()){
                    Uri downloadUri = task.getResult();
                    UserApi.updateImages(uid,downloadUri.toString());
                    updateWhenCreating();
                    progressBar.setVisibility(View.GONE);

                }
                else{
                    progressBar.setVisibility(View.GONE);
                    Snackbar.make(relativeLayout,getString(R.string.error_occured),Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void deletePhoto(final ImageView userImage, final ImageView addDeleteImage, final ProgressBar progressBar, final String urlImageTag) {
        if (this.isNetworkAvailable()) {
            progressBar.setVisibility(View.VISIBLE);
            if (this.getCurrentUser() != null) {
                UserApi.deleteImage(this.getCurrentUser().getUid(), urlImageTag).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressBar.setVisibility(View.GONE);
                        userImage.setImageDrawable(null);
                        addDeleteImage.setImageDrawable(getResources().getDrawable(R.drawable.add_imge));
                    }
                }).addOnFailureListener(this.onFailureListener());

                if(!urlImageTag.equals(currentUser.getProfileImg()+"?height=500"))
                    getDetailedStorageReference(urlImageTag).delete().addOnFailureListener(this.onFailureListener());

            }
        }
        else {
            Snackbar.make(relativeLayout,getString(R.string.popup_no_internet),Snackbar.LENGTH_SHORT).show();
        }
    }

    private void updateWhenCreating(){
        requestManager = Glide.with(getApplicationContext());
        if(user != null){
            activityEdit.setText(user.getActivity());
            aboutMeEdit.setText(user.getAboutMe());
        }

        if(this.getCurrentUser() != null){
            configureAddDeleteList();
            UserApi.getUser(this.getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    currentUser = documentSnapshot.toObject(User.class);
                    if(currentUser != null) {
                        if(currentUser.getImageList() != null){
                            if (currentUser.getImageList().size() > 0) {
                                int size = currentUser.getImageList().size();
                                switch (size) {
                                    case 1:
                                        requestManager.load(currentUser.getImageList().get(size - 1)).transition(DrawableTransitionOptions.withCrossFade()).into(mainImage);
                                        mainImageUrlTag = currentUser.getImageList().get(size - 1);
                                        setDeleteImages(addDeleteList.get(0));
                                        for (int i = 1; i < addDeleteList.size(); i++)
                                            setAddImage(addDeleteList.get(i));
                                        break;
                                    case 2:
                                        requestManager
                                                .load(currentUser.getImageList().get(size - 2))
                                                .thumbnail(0.1f)
                                                .transition(DrawableTransitionOptions.withCrossFade()).into(mainImage);
                                        requestManager
                                                .load(currentUser.getImageList().get(size - 1))
                                                .thumbnail(0.1f)
                                                .transition(DrawableTransitionOptions.withCrossFade()).into(firstImage);
                                        mainImageUrlTag = currentUser.getImageList().get(size - 2);
                                        firstImageUrlTag = currentUser.getImageList().get(size - 1);
                                        for (int i = 0; i < 2; i++)
                                            setDeleteImages(addDeleteList.get(i));
                                        for (int i = 2; i < addDeleteList.size(); i++)
                                            setAddImage(addDeleteList.get(i));
                                        break;
                                    case 3:
                                        requestManager
                                                .load(currentUser.getImageList().get(size - 3))
                                                .apply(new RequestOptions().override(250,350))
                                                .thumbnail(0.1f)
                                                .transition(DrawableTransitionOptions.withCrossFade()).into(mainImage);
                                        requestManager
                                                .load(currentUser.getImageList().get(size - 2))
                                                .apply(new RequestOptions().override(250,350))
                                                .thumbnail(0.1f)
                                                .transition(DrawableTransitionOptions.withCrossFade()).into(firstImage);
                                        requestManager
                                                .load(currentUser.getImageList().get(size - 1))
                                                .apply(new RequestOptions().override(250,350))
                                                .thumbnail(0.1f)
                                                .transition(DrawableTransitionOptions.withCrossFade()).into(secondImage);
                                        mainImageUrlTag = currentUser.getImageList().get(size - 3);
                                        firstImageUrlTag = currentUser.getImageList().get(size - 2);
                                        secondImageUrlTag = currentUser.getImageList().get(size - 1);
                                        for (int i = 0; i < 3; i++)
                                            setDeleteImages(addDeleteList.get(i));
                                        for (int i = 3; i < addDeleteList.size(); i++)
                                            setAddImage(addDeleteList.get(i));
                                        break;
                                    case 4:
                                        requestManager
                                                .load(currentUser.getImageList().get(size - 4))
                                                .thumbnail(0.1f)
                                                .transition(DrawableTransitionOptions.withCrossFade()).into(mainImage);
                                        requestManager
                                                .load(currentUser.getImageList().get(size - 3))
                                                .thumbnail(0.1f)
                                                .transition(DrawableTransitionOptions.withCrossFade()).into(firstImage);
                                        requestManager
                                                .load(currentUser.getImageList().get(size - 2))
                                                .thumbnail(0.1f)
                                                .transition(DrawableTransitionOptions.withCrossFade()).into(secondImage);
                                        requestManager
                                                .load(currentUser.getImageList().get(size - 1))
                                                .thumbnail(0.1f)
                                                .transition(DrawableTransitionOptions.withCrossFade()).into(thirdImage);
                                        mainImageUrlTag = currentUser.getImageList().get(size - 4);
                                        firstImageUrlTag = currentUser.getImageList().get(size - 3);
                                        secondImageUrlTag = currentUser.getImageList().get(size - 2);
                                        thirdImageUrlTag = currentUser.getImageList().get(size - 1);
                                        for (int i = 0; i < 4; i++)
                                            setDeleteImages(addDeleteList.get(i));
                                        for (int i = 4; i < addDeleteList.size(); i++)
                                            setAddImage(addDeleteList.get(i));

                                        break;
                                    case 5:

                                        requestManager
                                                .load(currentUser.getImageList().get(size - 5))
                                                .thumbnail(0.1f).transition(DrawableTransitionOptions.withCrossFade()).into(mainImage);
                                        requestManager
                                                .load(currentUser.getImageList().get(size - 4))
                                                .thumbnail(0.1f).transition(DrawableTransitionOptions.withCrossFade()).into(firstImage);
                                        requestManager
                                                .load(currentUser.getImageList().get(size - 3))
                                                .thumbnail(0.1f).transition(DrawableTransitionOptions.withCrossFade()).into(secondImage);
                                        requestManager
                                                .load(currentUser.getImageList().get(size - 2))
                                                .thumbnail(0.1f).transition(DrawableTransitionOptions.withCrossFade()).into(thirdImage);
                                        requestManager
                                                .load(currentUser.getImageList().get(size - 1))
                                                .thumbnail(0.1f).transition(DrawableTransitionOptions.withCrossFade()).into(fourthImage);
                                        mainImageUrlTag = currentUser.getImageList().get(size - 5);
                                        firstImageUrlTag = currentUser.getImageList().get(size - 4);
                                        secondImageUrlTag = currentUser.getImageList().get(size - 3);
                                        thirdImageUrlTag = currentUser.getImageList().get(size - 2);
                                        fourthImageUrlTag = currentUser.getImageList().get(size - 1);
                                        for (int i = 0; i < 5; i++)
                                            setDeleteImages(addDeleteList.get(i));
                                        for (int i = 5; i < addDeleteList.size(); i++)
                                            setAddImage(addDeleteList.get(i));
                                        break;
                                    case 6:
                                        requestManager
                                                .load(currentUser.getImageList().get(size - 6))
                                                .thumbnail(0.1f).transition(DrawableTransitionOptions.withCrossFade()).into(mainImage);
                                        requestManager
                                                .load(currentUser.getImageList().get(size - 5))
                                                .thumbnail(0.1f).transition(DrawableTransitionOptions.withCrossFade()).into(firstImage);
                                        requestManager
                                                .load(currentUser.getImageList().get(size - 4))
                                                .thumbnail(0.1f).transition(DrawableTransitionOptions.withCrossFade()).into(secondImage);
                                        requestManager
                                                .load(currentUser.getImageList().get(size - 3))
                                                .thumbnail(0.1f).transition(DrawableTransitionOptions.withCrossFade()).into(thirdImage);
                                        requestManager
                                                .load(currentUser.getImageList().get(size - 2))
                                                .thumbnail(0.1f).transition(DrawableTransitionOptions.withCrossFade()).into(fourthImage);
                                        requestManager
                                                .load(currentUser.getImageList().get(size - 1))
                                                .thumbnail(0.1f).transition(DrawableTransitionOptions.withCrossFade()).into(fifthImage);
                                        mainImageUrlTag = currentUser.getImageList().get(size - 6);
                                        firstImageUrlTag = currentUser.getImageList().get(size - 5);
                                        secondImageUrlTag = currentUser.getImageList().get(size - 4);
                                        thirdImageUrlTag = currentUser.getImageList().get(size - 3);
                                        fourthImageUrlTag = currentUser.getImageList().get(size - 2);
                                        fifthImageUrlTag = currentUser.getImageList().get(size - 1);
                                        for (int i = 0; i < 6; i++)
                                            setDeleteImages(addDeleteList.get(i));
                                        break;

                                }

                            } else {
                                for (ImageView imageView : addDeleteList)
                                    setAddImage(imageView);
                            }
                        }else{
                            for(ImageView imageView : addDeleteList)
                                setAddImage(imageView);
                        }
                    }
                }
            });
        }
    }

    private void clearAllImages(){
        mainImage.setImageDrawable(null);
        firstImage.setImageDrawable(null);
        secondImage.setImageDrawable(null);
        thirdImage.setImageDrawable(null);
        fourthImage.setImageDrawable(null);
        fifthImage.setImageDrawable(null);
    }

    private void configureAddDeleteList(){
        addDeleteList = new ArrayList<>();
        addDeleteList.add(mainAddDelete);
        addDeleteList.add(firstAddDelete);
        addDeleteList.add(secondAddDelete);
        addDeleteList.add(thirdAddDelete);
        addDeleteList.add(fourthAddDelete);
        addDeleteList.add(fifthAddDelete);
    }

    private void setDeleteImages(ImageView imageView){
        imageView.setImageDrawable(getResources().getDrawable(R.drawable.delete_imge));
    }
    private void setAddImage(ImageView imageView){
        imageView.setImageDrawable(getResources().getDrawable(R.drawable.add_imge));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);
        MenuItem item = menu.findItem(R.id.save_about_me);
        item.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                if(isNetworkAvailable()){
                    if(getCurrentUser() != null){
                        if(aboutMeEdit.getText() != null){
                            if(!aboutMeEdit.getText().toString().isEmpty() || !aboutMeEdit.getText().toString().trim().equals(currentUser.getAboutMe()))
                                UserApi.updateAboutMe(getCurrentUser().getUid(),aboutMeEdit.getText().toString().trim());
                        }
                        if(activityEdit.getText() != null){
                            if(!activityEdit.getText().toString().isEmpty() || activityEdit.getText().toString().trim().equals(currentUser.getActivity()))
                                UserApi.updateActivity(getCurrentUser().getUid(),activityEdit.getText().toString().trim());
                        }
                    }
                }
                hideKeyboard();
                NavUtils.navigateUpFromSameTask(this);
                break;
        }
        return true;
    }

    private void hideKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager)this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if(inputMethodManager.isAcceptingText())
            inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    public void onBackPressed() { }
}
