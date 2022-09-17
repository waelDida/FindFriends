package com.findfriends.mycompany.findfriends.Views;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.findfriends.mycompany.findfriends.Models.User;
import com.findfriends.mycompany.findfriends.R;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.user_image)
    ImageView userImage;
    @BindView(R.id.user_name_age)
    TextView userNameAge;

    public UserViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    public void bind(User user, RequestManager requestManager, Context context){
        RequestOptions options = new RequestOptions().placeholder(new ColorDrawable(context.getResources().getColor(R.color.loadImageColor)))
                .override(250,350);
        if(user.getImageList().isEmpty()){
            if(user.getGender().equals("male"))
                requestManager.load(context.getResources().getDrawable(R.drawable.male_photo))
                        .into(userImage);
            else
                requestManager.load(context.getResources().getDrawable(R.drawable.female_photo))
                        .into(userImage);
        }

        else
            requestManager.load(user.getImageList().get(0))
                    .apply(options)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(userImage);

        String str = null;
        if(user.getShowAge().equals("false")){
            str = user.getUserName();
        }
        else{
            try {
                str = user.getUserName()+", "+String.valueOf(calculateAge(getDateFromString(user.getBirthday())));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        userNameAge.setText(str);
    }
    private int calculateAge(Date birthDate){
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

    private Date getDateFromString(String stringDate) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        return df.parse(stringDate);
    }
}
