package com.whosin.app.ui.activites.CmProfile;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.whosin.app.R;
import com.whosin.app.comman.AppConstants;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.ActivityEventPdfDownloadBinding;
import com.whosin.app.databinding.ItemMyRingViewBinding;
import com.whosin.app.databinding.ItemPlusOneInvitesBinding;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.InvitedUserModel;
import com.whosin.app.service.models.PromoterEventModel;
import com.whosin.app.ui.activites.Profile.ProfileFullScreenImageActivity;
import com.whosin.app.ui.activites.comman.BaseActivity;

public class EventPdfDownloadActivity extends BaseActivity {

    private ActivityEventPdfDownloadBinding binding;

    private PromoterEventModel promoterEventModel;

    private final MyOnePlusAdapter<InvitedUserModel> myOnePlusAdapter = new MyOnePlusAdapter<>();

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    protected void initUi() {

        binding.onePlusTv.setText(getValue("plus_one_invited"));
        binding.txtDescription.setText(getValue("you_are_confirmed"));
        binding.tvInviteByTitle.setText(getValue("invite_by"));
        binding.tvConfiremedTitle.setText(getValue("confirmed"));

        Graphics.applyBlurEffectOnClaimScreen(activity, binding.blurView);
        String model = getIntent().getStringExtra("eventModel");
        promoterEventModel = new Gson().fromJson(model, PromoterEventModel.class);

        Graphics.loadImageWithFirstLetter(SessionManager.shared.getUser().getImage(), binding.imgUserLogo, promoterEventModel.getUser().getName());
        binding.tvUser.setText(SessionManager.shared.getUser().getFullName());


        setDetail();
    }

    @Override
    protected void setListeners() {


        binding.ivClose.setOnClickListener(view -> {
            onBackPressed();
        });

        binding.imgUserLogo.setOnClickListener(view -> openLightbox(SessionManager.shared.getUser().getImage()));
        binding.eventIv.setOnClickListener(view -> {
            if (promoterEventModel.getVenueType().equals("venue")) {
                if (promoterEventModel.getVenue() != null) {
                    openLightbox(promoterEventModel.getVenue().getLogo());
                }
            } else {
                if (promoterEventModel.getCustomVenue() != null) {
                    openLightbox(promoterEventModel.getCustomVenue().getImage());
                }
            }
        });

        binding.imageVenue.setOnClickListener(view -> {
            if (promoterEventModel.getVenueType().equals("venue")) {
                if (promoterEventModel.getVenue() != null) {
                    if (!TextUtils.isEmpty(promoterEventModel.getImage())) {
                        openLightbox(promoterEventModel.getImage());
                    } else {
                        openLightbox(promoterEventModel.getVenue().getCover());
                    }
                }
            } else {
                if (promoterEventModel.getCustomVenue() != null) {
                    openLightbox(promoterEventModel.getCustomVenue().getImage());
                }
            }
        });



    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityEventPdfDownloadBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------


    private void setDetail() {

        if (promoterEventModel != null) {

            if (promoterEventModel.getVenueType().equals("venue")) {
                if (promoterEventModel.getVenue() != null) {
                    Log.d("ImageVenue", "Venue Cover: " + promoterEventModel.getVenue().getCover());
                    Graphics.loadImageWithFirstLetter(promoterEventModel.getVenue().getLogo(), binding.eventIv, promoterEventModel.getVenue().getName());
                    binding.tvEventTitle.setText(promoterEventModel.getVenue().getName());
                    binding.tvDescription.setText(promoterEventModel.getVenue().getAddress());
                    if (!TextUtils.isEmpty(promoterEventModel.getImage())){
                        Graphics.loadImage(promoterEventModel.getImage(), binding.imageVenue);
                    }else {
                        Graphics.loadImage(promoterEventModel.getVenue().getCover(), binding.imageVenue);
                    }

                }
            } else {
                if (promoterEventModel.getCustomVenue() != null) {
                    binding.tvEventTitle.setText(promoterEventModel.getCustomVenue().getName());
                    Graphics.loadImageWithFirstLetter(promoterEventModel.getCustomVenue().getImage(), binding.eventIv, promoterEventModel.getCustomVenue().getName());
                    binding.tvDescription.setText(promoterEventModel.getCustomVenue().getAddress());
                    Graphics.loadImage(promoterEventModel.getCustomVenue().getImage(), binding.imageVenue);
                }
            }
//            Graphics.loadRoundImage(promoterEventModel.getImage(), binding.imageVenue);

            String time = promoterEventModel.getStartTime() + "-" + promoterEventModel.getEndTime();
            binding.txtDate.setText(Utils.changeDateFormat(promoterEventModel.getDate(), AppConstants.DATEFORMAT_SHORT, AppConstants.DATEFORMT_DD_MM_DATE));
            binding.txtTime.setText(time);

            binding.txtDescription.setText(promoterEventModel.getDescription());

            if (promoterEventModel.getVenueType().equals("venue") && promoterEventModel.getVenue() != null) {
                binding.locationTv.setText(promoterEventModel.getVenue().getAddress());
            } else if (promoterEventModel.getCustomVenue() != null) {
                binding.locationTv.setText(promoterEventModel.getCustomVenue().getAddress());
            } else {
                binding.locationLinear.setVisibility(View.GONE);
            }

            binding.eventCategoriLayout.setVisibility(View.GONE);

            // Event Category
            if (!TextUtils.isEmpty(promoterEventModel.getCategory()) && !promoterEventModel.getCategory().equalsIgnoreCase("None")) {
                binding.eventCategori.setText(promoterEventModel.getCategory());
                binding.eventCategoriLayout.setVisibility(View.VISIBLE);
            } else {
                binding.eventCategoriLayout.setVisibility(View.GONE);
            }

            if (promoterEventModel.getPlusOneMembers() != null && !promoterEventModel.getPlusOneMembers().isEmpty()) {
                binding.plusOneLinearLayout.setVisibility(VISIBLE);
                myOnePlusAdapter.updateData(promoterEventModel.getPlusOneMembers());
            } else {
                binding.plusOneLinearLayout.setVisibility(GONE);
            }

        }
    }

    private void openLightbox(String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Intent intent = new Intent(activity, ProfileFullScreenImageActivity.class);
            intent.putExtra(ProfileFullScreenImageActivity.EXTRA_IMAGE_URL, imageUrl);
            startActivity(intent);
        }
    }


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    private class MyOnePlusAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_plus_one_invites));

        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            InvitedUserModel model = (InvitedUserModel) getItem(position);

            viewHolder.vBinding.tvName.setText(model.getFirstName());
            Graphics.loadImageWithFirstLetter(model.getImage(), viewHolder.vBinding.image, model.getFirstName());

            ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) viewHolder.itemView.getLayoutParams();
            int marginStart = getResources().getDimensionPixelSize(R.dimen.promoter_notification_top);
            marginParams.setMarginStart(marginStart);
            viewHolder.itemView.setLayoutParams(marginParams);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final ItemPlusOneInvitesBinding vBinding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                vBinding = ItemPlusOneInvitesBinding.bind(itemView);
            }
        }
    }

    // --------------------------------------
    // endregion

}