package com.findfriends.mycompany.findfriends.Adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.RequestManager;

import java.util.List;

public class MainPagerAdapter extends PagerAdapter {

    private Context context;
    private List<Integer> imageUrls;
    private RequestManager requestManager;

    private LayoutInflater layoutInflater;

    public MainPagerAdapter(Context context, List<Integer> imageUrls, RequestManager requestManager){
        this.context = context;
        this.imageUrls = imageUrls;
        this.requestManager = requestManager;
    }
    @Override
    public int getCount() {
        return imageUrls.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        /*ImageView imageView = new ImageView(context);
        requestManager.load(context.getResources().getDrawable(imageUrls.get(position)))
                .apply(new RequestOptions().centerCrop())
                .into(imageView);
        container.addView(imageView);
        return imageView;*/
        layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = layoutInflater.inflate(imageUrls.get(position),container,false);
        container.addView(v);
        return v;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
}
