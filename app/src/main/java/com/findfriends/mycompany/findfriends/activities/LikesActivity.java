package com.findfriends.mycompany.findfriends.activities;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.findfriends.mycompany.findfriends.Adapters.UserDisplayAdapter;
import com.findfriends.mycompany.findfriends.Api.UserApi;
import com.findfriends.mycompany.findfriends.Base.BaseActivity;
import com.findfriends.mycompany.findfriends.Models.User;
import com.findfriends.mycompany.findfriends.R;
import com.findfriends.mycompany.findfriends.Utils.AppConstants;
import com.findfriends.mycompany.findfriends.Utils.ItemClickSupport;
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

public class LikesActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.users_recycler)
    RecyclerView recycler;
    @BindView(R.id.no_internet_home_fragment)
    LinearLayout noInternetLayout;
    @BindView(R.id.home_fragment_progress)
    ProgressBar progressBar;
    @BindView(R.id.no_one_text)
    RelativeLayout noOneHereText;

    private RequestManager requestManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_likes);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        requestManager = Glide.with(this);
        if(isNetworkAvailable()){
            configureRecyclerfirebase();
        }
        else
            noInternetLayout.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.likes_back_arrow)
    public void onClick() {
        Intent intent = new Intent(LikesActivity.this,LoginActivity.class);
        intent.putExtra(AppConstants.ACTIVITY_TAG,1);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.home_fragment_refresh)
    public void onRefreshClick(){
        if(isNetworkAvailable())
            configureRecyclerfirebase();
    }

    private void configureRecyclerfirebase() {
        progressBar.setVisibility(View.VISIBLE);
        if(noInternetLayout.getVisibility() == View.VISIBLE)
            noInternetLayout.setVisibility(View.GONE);

        UserApi.getUser(FirebaseAuth.getInstance().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                final User cUser = documentSnapshot.toObject(User.class);
                if(cUser != null){
                    UserApi.getUsersHaveCurrentIdInLikesList(FirebaseAuth.getInstance().getUid()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            final List<User> usersList = new ArrayList<>();
                            progressBar.setVisibility(View.INVISIBLE);
                            for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                                User user = documentSnapshot.toObject(User.class);
                                if(!cUser.getLikesList().contains(user.getUid()) && !cUser.getReportList().contains(user.getUid()) && !cUser.getUnmatchedList().contains(user.getUid())
                                        )
                                    usersList.add(user);
                            }
                            if(!usersList.isEmpty()){
                                setUpRecycler(usersList);
                            }
                            else{
                                noOneHereText.setVisibility(View.VISIBLE);
                            }

                        }
                    });

                }

            }
        });
    }

    private void setUpRecycler(List<User> list){
        UserDisplayAdapter adapter = new UserDisplayAdapter(requestManager,this);
        adapter.setList(list);
        recycler.setLayoutManager(new GridLayoutManager(this, 2));
        recycler.setAdapter(adapter);
        configureOnclickRecycleView(adapter);
    }

    private void configureOnclickRecycleView(final UserDisplayAdapter travelersDisplayAdapter){
        ItemClickSupport.addTo(recycler,R.layout.matched_user_view).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        User user = travelersDisplayAdapter.getUser(position);
                        Intent intent = new Intent(LikesActivity.this,UserProfileActivity.class);
                        intent.putExtra(AppConstants.USER_TAG,user);
                        intent.putExtra(AppConstants.ACTIVITY_TAG,1);
                        startActivity(intent);
                    }
                }
        );
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
