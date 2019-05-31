package com.blogspot.zone4apk.bookstash.ViewPager;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.blogspot.zone4apk.bookstash.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewPagerAdapter extends PagerAdapter {
    private Context context;
    private String description = "", imageUrl = "";
    private ArrayList<ViewPagerItem> slideItems;

    public ViewPagerAdapter(Context context) {
        this.context = context;
        slideItems = new ArrayList<>();
        DatabaseReference pagerDatabase = FirebaseDatabase.getInstance().getReference()
                .child("DashboardSliderDatabase");
        pagerDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                slideItems.clear();
                Iterable<DataSnapshot> slides = dataSnapshot.getChildren();
                for (DataSnapshot slide : slides) {
                    description = String.valueOf(slide.child("description").getValue());
                    imageUrl = String.valueOf(slide.child("imageUrl").getValue());
                    ViewPagerItem viewPagerItem = new ViewPagerItem();
                    viewPagerItem.setDescription(description);
                    viewPagerItem.setImageUrl(imageUrl);
                    slideItems.add(viewPagerItem);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Error loading data
            }
        });

    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.item_view_pager, container, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.swipe_image);
        TextView textView = (TextView) view.findViewById(R.id.swipe_textview);
        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress_bar_swipe_content);
        imageUrl = slideItems.get(position).getImageUrl();
        description = slideItems.get(position).getDescription();
        Glide.with(context).load(imageUrl)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.VISIBLE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                }).into(imageView);
        textView.setText(description);
        container.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        return slideItems.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        View view = (View) object;
        container.removeView(view);
    }
}
