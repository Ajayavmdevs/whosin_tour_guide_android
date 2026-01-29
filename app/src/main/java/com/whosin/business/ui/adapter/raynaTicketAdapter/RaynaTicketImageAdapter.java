package com.whosin.business.ui.adapter.raynaTicketAdapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.whosin.business.R;
import com.whosin.business.comman.Graphics;
import com.whosin.business.comman.Utils;
import com.whosin.business.comman.interfaces.CommanCallback;
import com.whosin.business.databinding.PagerItemBinding;
import com.whosin.business.service.models.rayna.RaynaTicketDetailModel;
import com.whosin.business.ui.activites.raynaTicket.RaynaTicketDetailActivity;

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
