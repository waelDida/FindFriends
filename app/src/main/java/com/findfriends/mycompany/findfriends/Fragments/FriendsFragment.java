package com.findfriends.mycompany.findfriends.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputEditText;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.findfriends.mycompany.findfriends.Adapters.UserMatchedAdapter;
import com.findfriends.mycompany.findfriends.Api.UserApi;
import com.findfriends.mycompany.findfriends.Models.User;
import com.findfriends.mycompany.findfriends.R;
import com.findfriends.mycompany.findfriends.Utils.AppConstants;
import com.findfriends.mycompany.findfriends.Utils.ItemClickSupport;
import com.findfriends.mycompany.findfriends.activities.LikesActivity;
import com.findfriends.mycompany.findfriends.activities.MessagesActivity;
import com.findfriends.mycompany.findfriends.activities.UserMatchedActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsFragment extends Fragment {

    @BindView(R.id.users_recycler)
    RecyclerView recycler;
    @BindView(R.id.no_internet_home_fragment)
    LinearLayout noInternetLayout;
    @BindView(R.id.home_fragment_progress)
    ProgressBar progressBar;
    @BindView(R.id.friend_fragment_layout) LinearLayout mainLayout;
    @BindView(R.id.searchEdit)
    TextInputEditText searchedit;
    @BindView(R.id.likesImage)
    CircleImageView likesImage;
    @BindView(R.id.users_message_recycler) RecyclerView chat_recycler;
    @BindView(R.id.likes_number)
    TextView likesNumber;
    @BindView(R.id.no_matches_layout)
    RelativeLayout noMatchesLayout;
    @BindView(R.id.message_title) TextView messageTitle;
    @BindView(R.id.hello_layout) LinearLayout helloLayout;

    private RequestManager requestManager;
    private User currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friends,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);
        if(getActivity() != null)
            requestManager = Glide.with(getActivity());
        if(isNetworkAvailable()) {
            progressBar.setVisibility(View.VISIBLE);
            getMatchedUsers();
        }
        else{
            noInternetLayout.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.home_fragment_refresh)
    public void onRefreshClick(){
        if(isNetworkAvailable()){
            progressBar.setVisibility(View.VISIBLE);
            noInternetLayout.setVisibility(View.INVISIBLE);
            getMatchedUsers();
        }
    }

    @OnClick(R.id.likesImage)
    public void onLikesClick(){
        Intent intent = new Intent(getActivity(),LikesActivity.class);
        intent.putExtra(AppConstants.CURRENT_TRAVELER_TAG_FRIEND_FRAGMENT,currentUser);
        startActivity(intent);
    }

    private void getMatchedUsers(){
        if(FirebaseAuth.getInstance().getUid() != null){
            UserApi.getUser(FirebaseAuth.getInstance().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(final DocumentSnapshot documentSnapshot) {
                    final List<User> matchedList = new ArrayList<>();
                    final List<User> nonMatchedList = new ArrayList<>();
                    currentUser = documentSnapshot.toObject(User.class);
                    if(currentUser != null){
                        displayChatList(currentUser);
                        if(currentUser.getShownGender().equals("male"))
                            likesImage.setImageResource(R.drawable.male_like);
                        else if (currentUser.getShownGender().equals("female"))
                            likesImage.setImageResource(R.drawable.female_like);
                        UserApi.getUsersHaveCurrentIdInLikesList(currentUser.getUid()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                progressBar.setVisibility(View.INVISIBLE);
                                mainLayout.setVisibility(View.VISIBLE);
                                for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                                    User user = documentSnapshot.toObject(User.class);
                                    if(currentUser.getLikesList().contains(user.getUid()))
                                        matchedList.add(user);
                                    else if(!currentUser.getUnmatchedList().contains(user.getUid()) && !currentUser.getReportList().contains(user.getUid()))
                                        nonMatchedList.add(user);
                                }
                                if(!matchedList.isEmpty()){
                                    setUpRecycler(matchedList);

                                }
                                if(!nonMatchedList.isEmpty())
                                    likesNumber.setText(String.valueOf(nonMatchedList.size()));
                                if(matchedList.isEmpty() && nonMatchedList.isEmpty()){
                                    mainLayout.setVisibility(View.INVISIBLE);
                                    noMatchesLayout.setVisibility(View.VISIBLE);
                                }
                            }
                        });

                    }
                }
            });
        }

    }

    private void displayChatList(User cUser){
        final List<User> users = new ArrayList<>();
        UserApi.getUsersHaveCurrentIdInChatList(cUser.getUid()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    User user = documentSnapshot.toObject(User.class);
                    if(!cUser.getUnmatchedList().contains(user.getUid()) && !cUser.getReportList().contains(user.getUid()))
                        users.add(user);
                }
                if(!users.isEmpty()){
                    messageTitle.setVisibility(View.VISIBLE);
                    helloLayout.setVisibility(View.GONE);
                    setUpChatRecycler(users);
                }
                else{
                    messageTitle.setVisibility(View.INVISIBLE);
                    helloLayout.setVisibility(View.VISIBLE);
                }

            }
        });
    }

    private void setUpRecycler(final List<User> list){
        final UserMatchedAdapter adapter = new UserMatchedAdapter(requestManager,getActivity());
        adapter.setList(list);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));
        recycler.setAdapter(adapter);
        configureOnclickRecycleView(adapter);
        searchedit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                List<User> filtredList = new ArrayList<>();
                for(User user : list){
                    if(user.getUserName().toLowerCase().contains(s.toString().toLowerCase()))
                        filtredList.add(user);
                }
                adapter.setFilter(filtredList);
            }
        });
    }

    private void configureOnclickRecycleView(final UserMatchedAdapter travelerMatchedAdapter) {
        ItemClickSupport.addTo(recycler, R.layout.matched_user_view).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        User user = travelerMatchedAdapter.getTraveler(position);
                        Intent intent = new Intent(getActivity(), UserMatchedActivity.class);
                        intent.putExtra(AppConstants.TRAVELER_TAG_FRIEND_FRAGMENT,user);
                        intent.putExtra(AppConstants.CURRENT_TRAVELER_TAG_FRIEND_FRAGMENT,currentUser);
                        startActivity(intent);
                    }
                }
        );
    }

    private void setUpChatRecycler(final List<User> list){
        final UserMatchedAdapter adapter = new UserMatchedAdapter(requestManager,getActivity());
        adapter.setList(list);
        chat_recycler.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));
        chat_recycler.setAdapter(adapter);
        configureOnClickChatRecycler(adapter);

    }

    private void configureOnClickChatRecycler(final UserMatchedAdapter travelerMatchedAdapter){
        ItemClickSupport.addTo(chat_recycler, R.layout.matched_user_view).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        User user = travelerMatchedAdapter.getTraveler(position);
                        UserApi.getUser(user.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                User userUpdated = documentSnapshot.toObject(User.class);
                                if(userUpdated == null){
                                    Toast.makeText(getActivity(),"Error occured",Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Intent intent = new Intent(getActivity(),MessagesActivity.class);
                                    intent.putExtra(AppConstants.TRAVELER_TAG_FRIEND_FRAGMENT,userUpdated);
                                    intent.putExtra(AppConstants.CURRENT_TRAVELER_TAG_FRIEND_FRAGMENT,currentUser);
                                    startActivity(intent);
                                }
                            }
                        });
                    }
                }
        );
    }

    protected boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
