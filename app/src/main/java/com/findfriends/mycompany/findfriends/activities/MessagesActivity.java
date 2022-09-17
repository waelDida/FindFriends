package com.findfriends.mycompany.findfriends.activities;

import android.app.Activity;
import android.content.Intent;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.findfriends.mycompany.findfriends.Adapters.ChatAdapter;
import com.findfriends.mycompany.findfriends.Api.ChatApi;
import com.findfriends.mycompany.findfriends.Api.UserApi;
import com.findfriends.mycompany.findfriends.Base.BaseActivity;
import com.findfriends.mycompany.findfriends.Models.Chat;
import com.findfriends.mycompany.findfriends.Models.Message;
import com.findfriends.mycompany.findfriends.Models.User;
import com.findfriends.mycompany.findfriends.R;
import com.findfriends.mycompany.findfriends.Utils.AppConstants;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesActivity extends BaseActivity implements ChatAdapter.Listener{

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_user_image)
    CircleImageView toolbarTravelerImage;
    @BindView(R.id.toolbar_user_name)
    TextView toolbarTravelerName;
    @BindView(R.id.chat_recycler)
    RecyclerView chatRecycler;
    @BindView(R.id.sendMessageEdit)
    TextInputEditText sendMessageEdit;


    private User user,currentUser, userFCM, currentUserFCM;
    private ChatAdapter chatAdapter;

    private String chatId;
    private int tagIndice;

    private String userNameFCM, userIdFCM, userImageFCM;
    private String currentUserIdFCM, currentUserNameFCM, currentUserImageFCM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        user = getIntent().getParcelableExtra(AppConstants.TRAVELER_TAG_FRIEND_FRAGMENT);
        currentUser = getIntent().getParcelableExtra(AppConstants.CURRENT_TRAVELER_TAG_FRIEND_FRAGMENT);
        tagIndice = getIntent().getIntExtra(AppConstants.INDICE_TAG,0);
        userNameFCM = getIntent().getStringExtra(AppConstants.USER_NAME);
        userImageFCM = getIntent().getStringExtra(AppConstants.USER_IMAGE);
        userIdFCM = getIntent().getStringExtra(AppConstants.USER_ID);
        currentUserIdFCM = getIntent().getStringExtra(AppConstants.CURRENT_USER_ID);
        currentUserNameFCM = getIntent().getStringExtra(AppConstants.CURRENT_USER_NAME);
        currentUserImageFCM = getIntent().getStringExtra(AppConstants.CURRENT_USER_IMAGE);
        if(user != null && currentUser != null){
            if(user.getImageList().isEmpty()){
                if(user.getGender().equals("male"))
                    Glide.with(this).load(getResources().getDrawable(R.drawable.user_male))
                            .into(toolbarTravelerImage);
                else
                    Glide.with(this).load(getResources().getDrawable(R.drawable.user_female))
                            .into(toolbarTravelerImage);
            }
            else
                Glide.with(this).load(user.getImageList().get(0))
                        .thumbnail(0.1f)
                        .into(toolbarTravelerImage);

            toolbarTravelerName.setText(user.getUserName());
            getChatRoomId(currentUser.getUid(),user.getUid());
        }
        else{
            Glide.with(this).load(userImageFCM)
                    .apply(RequestOptions.circleCropTransform())
                    .thumbnail(0.1f)
                    .into(toolbarTravelerImage);
            toolbarTravelerName.setText(userNameFCM);
            getChatRoomId(currentUserIdFCM,userIdFCM);
        }
    }
    @OnClick(R.id.back_arrow)
    public void onClick() {
        hideKeyboard();
        Intent intent = new Intent(MessagesActivity.this,LoginActivity.class);
        intent.putExtra(AppConstants.ACTIVITY_TAG,1);
        startActivity(intent);
        finish();
    }

    private void getChatRoomId(final String uid, final String fid) {
        ChatApi.getDocumentId(uid).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Chat chat = documentSnapshot.toObject(Chat.class);
                    if (chat.getSenderReceiverList().contains(fid)) {
                        chatId = documentSnapshot.getId();
                    }
                }
                configureChatRecycler(chatId,uid);
            }
        });

    }

    private void configureChatRecycler(final String chatRoomId, final String uid){
        if(currentUser != null && user != null){
            chatAdapter = new ChatAdapter(generateOptionForAdapter(ChatApi.getAllMessageForChat(chatRoomId)),
                    Glide.with(this),this,uid,currentUser,user,this);

            chatAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    chatRecycler.smoothScrollToPosition(chatAdapter.getItemCount());
                }
            });
            chatRecycler.setLayoutManager(new LinearLayoutManager(this));
            chatRecycler.setAdapter(chatAdapter);
        }
        else{
            UserApi.getUser(currentUserIdFCM).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    currentUserFCM = documentSnapshot.toObject(User.class);
                    UserApi.getUser(userIdFCM).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            userFCM = documentSnapshot.toObject(User.class);
                            testMethod(currentUserFCM,userFCM,chatRoomId,uid);
                        }
                    });
                }
            });
        }
    }

    private void testMethod(User cUser, User Fuser, String chatRoomId, String uid){
        chatAdapter = new ChatAdapter(generateOptionForAdapter(ChatApi.getAllMessageForChat(chatRoomId)),
                Glide.with(this),this,uid,cUser,Fuser,this);

        chatAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                chatRecycler.smoothScrollToPosition(chatAdapter.getItemCount());
            }
        });
        chatRecycler.setLayoutManager(new LinearLayoutManager(this));
        chatRecycler.setAdapter(chatAdapter);
    }

    private FirestoreRecyclerOptions<Message> generateOptionForAdapter(Query query){
        return new FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(query, Message.class)
                .setLifecycleOwner(this)
                .build();
    }

    @OnClick(R.id.send_message_image)
    public void OnSendMessageClick() {
        if(!sendMessageEdit.getText().toString().isEmpty()){
            if(user != null && currentUser != null){
                if(!user.getImageList().isEmpty() && !currentUser.getImageList().isEmpty())
                    ChatApi.createMessageForChat(chatId,sendMessageEdit.getText().toString().trim(),user.getUid(), user.getUserName(), user.getImageList().get(0),currentUser.getUid(),currentUser.getUserName(),currentUser.getImageList().get(0)).addOnFailureListener(onFailureListener());
                else if(user.getImageList().isEmpty() && currentUser.getImageList().isEmpty())
                    ChatApi.createMessageForChat(chatId,sendMessageEdit.getText().toString().trim(),user.getUid(), user.getUserName(), "",currentUser.getUid(),currentUser.getUserName(),"").addOnFailureListener(onFailureListener());
                else if(user.getImageList().isEmpty() && !currentUser.getImageList().isEmpty())
                    ChatApi.createMessageForChat(chatId,sendMessageEdit.getText().toString().trim(),user.getUid(), user.getUserName(), "",currentUser.getUid(),currentUser.getUserName(),currentUser.getImageList().get(0)).addOnFailureListener(onFailureListener());
                else if(!user.getImageList().isEmpty() && currentUser.getImageList().isEmpty())
                    ChatApi.createMessageForChat(chatId,sendMessageEdit.getText().toString().trim(),user.getUid(), user.getUserName(), user.getImageList().get(0),currentUser.getUid(),currentUser.getUserName(),"").addOnFailureListener(onFailureListener());


                sendMessageEdit.setText("");
            }
            else{
                ChatApi.createMessageForChat(chatId, sendMessageEdit.getText().toString().trim(),userIdFCM,userNameFCM,userImageFCM,currentUserIdFCM,currentUserNameFCM,currentUserImageFCM).addOnFailureListener(onFailureListener());
                sendMessageEdit.setText("");
            }
        }
    }
    private void hideKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager)this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if(inputMethodManager.isAcceptingText())
            inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
    }
    @Override
    public void onBackPressed() { }

    @Override
    public void onDataChanged() { }
}
