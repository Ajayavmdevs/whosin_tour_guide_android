package com.whosin.app.ui.adapter.raynaTicketAdapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.whosin.app.R;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.databinding.PagerItemBinding;
import com.whosin.app.service.models.rayna.RaynaTicketDetailModel;
import com.whosin.app.ui.activites.raynaTicket.RaynaTicketDetailActivity;

import java.util.List;

import coil.Coil;
import coil.ImageLoader;
import coil.request.ImageRequest;


public class RaynaTicketImageAdapter extends PagerAdapter {

    private Activity activity;
    private RaynaTicketDetailModel raynaTicketDetailModel;
    private List<String> images;
    private boolean isComeFromChat = false;
    private CommanCallback<Boolean> callback;

    public RaynaTicketImageAdapter(Activity activity, RaynaTicketDetailModel raynaTicketDetailModel , List<String> images) {
        this.activity = activity;
        this.raynaTicketDetailModel = raynaTicketDetailModel;
        this.images = images;
    }

    public RaynaTicketImageAdapter(Activity activity, RaynaTicketDetailModel raynaTicketDetailModel , List<String> images,CommanCallback<Boolean> callback) {
        this.activity = activity;
        this.raynaTicketDetailModel = raynaTicketDetailModel;
        this.images = images;
        this.callback = callback;
    }

    public RaynaTicketImageAdapter(Activity activity, RaynaTicketDetailModel raynaTicketDetailModel , List<String> images,boolean isFromChat) {
        this.activity = activity;
        this.raynaTicketDetailModel = raynaTicketDetailModel;
        this.images = images;
        this.isComeFromChat = isFromChat;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from( activity );
        PagerItemBinding binding = PagerItemBinding.inflate( inflater, container, false );


        binding.aedLayout.setVisibility(View.GONE);
        binding.ivMenu.setVisibility(View.GONE);
        binding.blackShadowView.setVisibility(View.GONE);
        binding.topBlackShadow.setVisibility(View.GONE);

        String bannerModel = images.get( position );

        if (isComeFromChat) {
            Glide.with(activity)
                    .asBitmap()
                    .load(bannerModel)
                    .apply(new RequestOptions().disallowHardwareConfig())
                    .into(binding.imageView);
        } else {

            ImageRequest request = new ImageRequest.Builder(activity)
                    .data(bannerModel)
                    .target(binding.imageView)
                    .placeholder(Graphics.getAnimatedDrawable())
                    .error(R.drawable.gery_image)
                    .build();

            ImageLoader imageLoader = Coil.imageLoader(activity);
            imageLoader.enqueue(request);
        }






        binding.getRoot().setOnClickListener(view -> {
            if (callback != null){
                callback.onReceive(true);
            }
            Utils.preventDoubleClick(view);
            activity.startActivity(new Intent(activity, RaynaTicketDetailActivity.class).putExtra("ticketId",raynaTicketDetailModel.getId()));
        });

        container.addView( binding.getRoot() );
        return binding.getRoot();

    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView( (View) object );
    }
}
