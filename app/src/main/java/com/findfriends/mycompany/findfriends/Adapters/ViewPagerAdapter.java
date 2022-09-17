package com.findfriends.mycompany.findfriends.Adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.util.List;

public class ViewPagerAdapter extends PagerAdapter {

    private Context context;
    private List<String> imageUrls;
    private RequestManager requestManager;

    public ViewPagerAdapter(Context context, List<String> imageUrls, RequestManager requestManager){
        this.context = context;
        this.imageUrls = imageUrls;
        this.requestManager = requestManager;
    }
    @Override
    public int getCount() {
        return imageUrls.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ImageView imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        requestManager.load(imageUrls.get(position))
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView);
        container.addView(imageView);
        return imageView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
