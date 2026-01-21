package com.whosin.app.ui.activites.yacht;

import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.databinding.ActivityYachtOfferDetailBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.YachtsOfferModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.comman.BaseActivity;
import com.whosin.app.ui.controller.yacht.YachtAddOnView;
import com.whosin.app.ui.controller.yacht.YachtPackagesView;

import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;
import java.util.stream.Collectors;

public class YachtOfferDetailActivity extends BaseActivity {

    private ActivityYachtOfferDetailBinding binding;

    private final int SCROLL_THRESHOLD = 10;

    private boolean blurViewVisible = false;


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------


    @Override
    protected void initUi() {
        String yachtOfferID = getIntent().getStringExtra("yachtOfferId");
        requestYachtOfferDetail(yachtOfferID);

    }

    @Override
    protected void setListeners() {

        binding.ivClose.setOnClickListener(v -> {
            finish();
        });

        binding.nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            int scrollDistance = scrollY - oldScrollY;

            if (Math.abs(scrollDistance) > SCROLL_THRESHOLD) {
                if (scrollDistance > 0) {
                    if (!blurViewVisible) {
                        binding.blurView.setBlurEnabled(true);
                        Graphics.applyBlurEffect(activity, binding.blurView);
                        blurViewVisible = true;
                    }
                } else {
                    Log.d("TAG", "setListeners: " + scrollY);
                    if (scrollY <= 10) {
                        if (blurViewVisible) {
                            binding.blurView.setBackgroundColor(Color.TRANSPARENT);
                            binding.blurView.setBackground(null);
                            binding.blurView.setBlurEnabled(false);
                            blurViewVisible = false;
                        }
                    }
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
        binding = ActivityYachtOfferDetailBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------


    private void setDetail(YachtsOfferModel data) {

        binding.tvName.setText(data.getYacht().getYachtClub().getName());
        binding.tvAddress.setText(data.getYacht().getYachtClub().getAddress());

        List<CarouselItem> carouselItems = data.getYacht().getYachtClub().getGalleries().stream().map(CarouselItem::new).collect(Collectors.toList());
        binding.imageCarousel.registerLifecycle(getLifecycle());
        binding.imageCarousel.setData(carouselItems);


        if (data.getYacht().getSpecifications() != null && !data.getYacht().getSpecifications().isEmpty()) {
            binding.specificationView.setupData(data.getYacht().getSpecifications(), activity);
        } else {
            binding.specificationView.setVisibility(View.GONE);
        }


        if (data.getYacht().getYachtClub().getAbout() != null && !data.getYacht().getYachtClub().getAbout().isEmpty()) {
            binding.tvDescription.setText(data.getYacht().getYachtClub().getAbout());
            binding.tvDescription.post(() -> {
                int lineCount = binding.tvDescription.getLineCount();
                if (lineCount > 3) {
                    Utils.makeTextViewResizable(binding.tvDescription, 3, 3, "...More", true);
                }
            });
        } else {
            binding.tvDescription.setVisibility(View.GONE);
        }


        if (data.getYacht().getFeatures() != null && !data.getYacht().getFeatures().isEmpty()) {
            binding.featureRecycler.setupData("Features & Specs", data.getYacht().getFeatures(), activity, getSupportFragmentManager(), StaggeredGridLayoutManager.HORIZONTAL, 3);
        } else {
            binding.featureRecycler.setVisibility(View.GONE);
        }

        if (data.getPackages() != null && !data.getPackages().isEmpty()) {
            if (data.getPackageType().equals("hourly")) {
                binding.yachtPackagesListView.setupData(activity, "Available Packages", data.getPackages(), YachtPackagesView.PackageType.hourly);
            } else {
                binding.yachtPackagesListView.setupData(activity, "Available Packages", data.getPackages(), YachtPackagesView.PackageType.fixed);
            }
        } else {
            binding.yachtPackagesListView.setVisibility(View.GONE);
        }

        if (data.getAddOns() != null && !data.getAddOns().isEmpty()) {
            binding.yachatAddOnView.setupData(activity, "Get extra add-ons", data.getAddOns());
        } else {
            binding.yachatAddOnView.setVisibility(View.GONE);
        }

        if (data.getImportantNotice() != null && !data.getImportantNotice().isEmpty()) {
            binding.importantMessageView.setupData(activity, "IMPORTANT NOTICE", data.getImportantNotice());
        } else {
            binding.importantMessageView.setVisibility(View.GONE);
        }

        if (data.getDisclaimer() != null && !data.getDisclaimer().isEmpty()) {
            binding.disclaimerView.setupData(activity, "\uD83D\uDEA8 DISCLAIMER", data.getDisclaimer());
        } else {
            binding.disclaimerView.setVisibility(View.GONE);
        }


        try {
            JSONArray jsonArray = new JSONArray(data.getNeedToKnow());
            StringBuilder formattedString = new StringBuilder();
            for (int i = 0; i < jsonArray.length(); i++) {
                String currentString = jsonArray.getString(i);
                formattedString.append("•  ").append(currentString);
                if (i < jsonArray.length() - 1) {
                    formattedString.append("\n");
                }
            }
            binding.needToKnowView.setupData(activity, "NEED TO KNOW", formattedString.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestYachtOfferDetail(String offerId) {
        showProgress();
        DataService.shared(activity).requestYachtOfferDetail(offerId, new RestCallback<ContainerModel<YachtsOfferModel>>(this) {
            @Override
            public void result(ContainerModel<YachtsOfferModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (model.getData() != null) {
                    binding.nestedScrollView.setVisibility(View.VISIBLE);
                    setDetail(model.getData());
                }
            }
        });
    }


    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------


    // --------------------------------------
    // endregion


}