package com.findfriends.mycompany.findfriends.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import com.google.android.material.textfield.TextInputEditText;
import androidx.core.app.NavUtils;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.findfriends.mycompany.findfriends.Api.ChatApi;
import com.findfriends.mycompany.findfriends.Api.UserApi;
import com.findfriends.mycompany.findfriends.Base.BaseActivity;
import com.findfriends.mycompany.findfriends.Models.Chat;
import com.findfriends.mycompany.findfriends.Models.User;
import com.findfriends.mycompany.findfriends.R;
import com.findfriends.mycompany.findfriends.Utils.AppConstants;
import com.findfriends.mycompany.findfriends.Utils.MonthNames;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.base.Splitter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserMatchedActivity extends BaseActivity {

    @BindView(R.id.user_name)TextView userName;
    @BindView(R.id.user_image) CircleImageView userImage;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.toolbar_user_image) CircleImageView toolbarUserImage;
    @BindView(R.id.toolbar_user_name) TextView toolbarUserName;
    @BindView(R.id.sendMessageEdit)
    TextInputEditText sendMessageEdit;
    @BindView(R.id.send_message_layout)
    RelativeLayout sendMessageLayout;
    @BindView(R.id.match_date) TextView matchDate;
    private User user;
    private User currentUser;
    private String chatId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_matched);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        user = getIntent().getParcelableExtra(AppConstants.TRAVELER_TAG_FRIEND_FRAGMENT);
        currentUser = getIntent().getParcelableExtra(AppConstants.CURRENT_TRAVELER_TAG_FRIEND_FRAGMENT);
        getChatRoomId(currentUser.getUid());
        if(currentUser.getChatList() != null){
            if(!currentUser.getChatList().contains(user.getUid())){
                sendMessageLayout.setVisibility(View.VISIBLE);
            }
        }
        else
            sendMessageLayout.setVisibility(View.VISIBLE);

        if(user != null){
            String str = getResources().getString(R.string.match_text)+" "+user.getUserName();
            userName.setText(str);
            if(user.getImageList().isEmpty()){
                if(user.getGender().equals("male")){
                    Glide.with(this)
                            .load(getResources().getDrawable(R.drawable.male_photo))
                            .into(userImage);
                    Glide.with(this)
                            .load(getResources().getDrawable(R.drawable.user_male))
                            .into(toolbarUserImage);
                }
                else{
                    Glide.with(this)
                            .load(getResources().getDrawable(R.drawable.female_photo))
                            .into(userImage);
                    Glide.with(this)
                            .load(getResources().getDrawable(R.drawable.user_female))
                            .into(toolbarUserImage);
                }
            }
            else{
                Glide.with(this)
                        .load(user.getImageList().get(0))
                        .into(userImage);
                Glide.with(this)
                        .load(user.getImageList().get(0))
                        .thumbnail(0.1f)
                        .into(toolbarUserImage);
            }
            toolbarUserName.setText(user.getUserName());
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @OnClick(R.id.back_arrow)
    public void onBackArrowClick(){
        finish();
    }

    @OnClick(R.id.send_message_image)
    public void OnSendMessageClick(){
        UserApi.getUser(user.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User userUpdated = documentSnapshot.toObject(User.class);
                if(userUpdated == null){
                    Toast.makeText(UserMatchedActivity.this,"Error occured",Toast.LENGTH_SHORT).show();
                }
                else{
                    if(!sendMessageEdit.getText().toString().isEmpty()){
                        if(!user.getImageList().isEmpty() && !currentUser.getImageList().isEmpty())
                            ChatApi.createMessageForChat(chatId,sendMessageEdit.getText().toString().trim(),user.getUid(), user.getUserName(), user.getImageList().get(0),currentUser.getUid(),currentUser.getUserName(),currentUser.getImageList().get(0)).addOnFailureListener(onFailureListener());
                        else if(user.getImageList().isEmpty() && currentUser.getImageList().isEmpty())
                            ChatApi.createMessageForChat(chatId,sendMessageEdit.getText().toString().trim(),user.getUid(), user.getUserName(), "",currentUser.getUid(),currentUser.getUserName(),"").addOnFailureListener(onFailureListener());
                        else if(user.getImageList().isEmpty() && !currentUser.getImageList().isEmpty())
                            ChatApi.createMessageForChat(chatId,sendMessageEdit.getText().toString().trim(),user.getUid(), user.getUserName(), "",currentUser.getUid(),currentUser.getUserName(),currentUser.getImageList().get(0)).addOnFailureListener(onFailureListener());
                        else if(!user.getImageList().isEmpty() && currentUser.getImageList().isEmpty())
                            ChatApi.createMessageForChat(chatId,sendMessageEdit.getText().toString().trim(),user.getUid(), user.getUserName(), user.getImageList().get(0),currentUser.getUid(),currentUser.getUserName(),"").addOnFailureListener(onFailureListener());

                        Intent intent = new Intent(UserMatchedActivity.this,MessagesActivity.class);
                        intent.putExtra(AppConstants.TRAVELER_TAG_FRIEND_FRAGMENT,user);
                        intent.putExtra(AppConstants.CURRENT_TRAVELER_TAG_FRIEND_FRAGMENT,currentUser);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });

    }

    @OnClick(R.id.user_image)
    public void onImageClick(){
        Intent intent = new Intent(UserMatchedActivity.this,UserProfileDetailActivity.class);
        intent.putExtra(AppConstants.TRAVELER_TAG_FRIEND_FRAGMENT,user);
        intent.putExtra(AppConstants.CURRENT_TRAVELER_TAG_FRIEND_FRAGMENT,currentUser);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.user_match_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.report_action:
                final Dialog dialog = new Dialog(this);
                dialog.setContentView(R.layout.custom_delete_dialog);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                TextView title = dialog.findViewById(R.id.dialog_title);
                TextView text = dialog.findViewById(R.id.dialog_text);
                title.setText(getString(R.string.report_text));
                text.setText(getString(R.string.report_text_confirmation));
                Button yesButton = dialog.findViewById(R.id.yes_dialog_button);
                Button noButton = dialog.findViewById(R.id.no_dialog_button);
                yesButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        UserApi.getUser(user.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                User user = documentSnapshot.toObject(User.class);
                                if(user == null)
                                    Toast.makeText(UserMatchedActivity.this,"Error occured",Toast.LENGTH_SHORT).show();
                                else
                                    reportTraveler();
                                Intent intent = new Intent(UserMatchedActivity.this,LoginActivity.class);
                                intent.putExtra(AppConstants.ACTIVITY_TAG,1);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }
                });
                noButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                return true;
            case R.id.unmatch_action:
                final Dialog dialogUnmatch = new Dialog(this);
                dialogUnmatch.setContentView(R.layout.custom_delete_dialog);
                dialogUnmatch.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                TextView titleUnmatch = dialogUnmatch.findViewById(R.id.dialog_title);
                TextView textUnmatch = dialogUnmatch.findViewById(R.id.dialog_text);
                titleUnmatch.setText(getString(R.string.unmatch_text));
                textUnmatch.setText(getString(R.string.unmatch_text_confirmation));
                Button yesButtonUnmatch = dialogUnmatch.findViewById(R.id.yes_dialog_button);
                Button noButtonUnmatch = dialogUnmatch.findViewById(R.id.no_dialog_button);
                yesButtonUnmatch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogUnmatch.dismiss();
                        UserApi.getUser(user.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                User user = documentSnapshot.toObject(User.class);
                                if(user != null)
                                    unMatchTraveler();
                                else
                                    Toast.makeText(UserMatchedActivity.this,"Error occured",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(UserMatchedActivity.this,LoginActivity.class);
                                intent.putExtra(AppConstants.ACTIVITY_TAG,1);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }
                });
                noButtonUnmatch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogUnmatch.dismiss();
                    }
                });
                dialogUnmatch.show();
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void unMatchTraveler(){
        UserApi.deleteFromLikesList(FirebaseAuth.getInstance().getUid(),user.getUid()).addOnFailureListener(onFailureListener());
        UserApi.updateUnmatchedList(FirebaseAuth.getInstance().getUid(),user.getUid()).addOnFailureListener(onFailureListener());
        UserApi.deleteFromChatList(FirebaseAuth.getInstance().getUid(),user.getUid()).addOnFailureListener(onFailureListener());
        ChatApi.deleteChat(chatId).addOnFailureListener(onFailureListener());
    }

    private void reportTraveler(){
        UserApi.deleteFromLikesList(FirebaseAuth.getInstance().getUid(),user.getUid()).addOnFailureListener(onFailureListener());
        UserApi.updateReportList(FirebaseAuth.getInstance().getUid(),user.getUid()).addOnFailureListener(onFailureListener());
        UserApi.deleteFromChatList(FirebaseAuth.getInstance().getUid(),user.getUid()).addOnFailureListener(onFailureListener());
        ChatApi.deleteChat(chatId).addOnFailureListener(onFailureListener());
    }

    private void getChatRoomId(String uid){
        ChatApi.getDocumentId(uid).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    Chat chat = documentSnapshot.toObject(Chat.class);
                    if(chat.getSenderReceiverList().contains(user.getUid())){
                        chatId= documentSnapshot.getId();
                    }
                    List<String> formatDateList = Splitter.on("/").splitToList(chat.getDate());
                    String dateStr = MonthNames.theMonth(Integer.valueOf(formatDateList.get(0)) - 1)+" "+formatDateList.get(1)+", "
                            +formatDateList.get(2);
                    matchDate.setText(dateStr);
                }
            }
        });
    }
}
