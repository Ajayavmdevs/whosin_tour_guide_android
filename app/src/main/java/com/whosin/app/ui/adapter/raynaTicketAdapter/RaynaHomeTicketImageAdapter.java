package com.whosin.app.ui.adapter.raynaTicketAdapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.whosin.app.R;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.databinding.PagerItemBinding;
import com.whosin.app.service.models.HomeTicketsModel;
import com.whosin.app.ui.activites.raynaTicket.RaynaTicketDetailActivity;

import java.util.List;

import coil.ImageLoader;
import coil.request.ImageRequest;

public class RaynaHomeTicketImageAdapter extends PagerAdapter {

    private final Activity activity;
    private final HomeTicketsModel model;
    private final List<String> images;

    public RaynaHomeTicketImageAdapter(Activity activity, HomeTicketsModel model, List<String> images) {
        this.activity = activity;
        this.model = model;
        this.images = images;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        PagerItemBinding binding = PagerItemBinding.inflate(inflater, container, false);

        binding.aedLayout.setVisibility(View.GONE);
        binding.ivMenu.setVisibility(View.GONE);
        binding.blackShadowView.setVisibility(View.GONE);
        binding.topBlackShadow.setVisibility(View.GONE);
        binding.constraint.setVisibility(View.GONE);
        String url = images.get(position);

        ImageRequest request = new ImageRequest.Builder(activity)
                .data(url)
                .target(binding.imageView)
                .placeholder(Graphics.getAnimatedDrawable())
                .error(R.drawable.gery_image)
                .build();

        ImageLoader imageLoader = coil.Coil.imageLoader(activity);
        imageLoader.enqueue(request);

        binding.getRoot().setOnClickListener(view -> {
            Utils.preventDoubleClick(view);
            activity.startActivity(new Intent(activity, RaynaTicketDetailActivity.class).putExtra("ticketId", model.getId()));
        });

        container.addView(binding.getRoot());
        return binding.getRoot();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
