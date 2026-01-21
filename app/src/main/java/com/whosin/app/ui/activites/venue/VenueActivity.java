package com.whosin.app.ui.activites.venue;

import static com.whosin.app.comman.Graphics.context;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.tapadoo.alerter.Alerter;
import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.ActivityVenueBinding;
import com.whosin.app.databinding.ExclusiveItemRecyclerBinding;
import com.whosin.app.databinding.InfoBottomsheetDialogBinding;
import com.whosin.app.databinding.ItemDiscountRecyclerBinding;
import com.whosin.app.databinding.ItemImageSlideRecyclerBinding;
import com.whosin.app.databinding.ItemPhoneNumberBinding;
import com.whosin.app.databinding.ItemRatingReviewRecyclerBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.AppSettingManager;
import com.whosin.app.service.manager.BlockUserManager;
import com.whosin.app.service.manager.PromoterProfileManager;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.CommonModel;
import com.whosin.app.service.models.ContactListModel;
import com.whosin.app.service.models.ContainerListModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.CurrentUserRatingModel;
import com.whosin.app.service.models.MessageEvent;
import com.whosin.app.service.models.OffersModel;
import com.whosin.app.service.models.PromoterVenueModel;
import com.whosin.app.service.models.SpecialOfferModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.models.VenueObjectModel;
import com.whosin.app.service.models.VenueTimingModel;
import com.whosin.app.service.models.VoucherModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.comman.BaseActivity;
import com.whosin.app.ui.activites.home.activity.SeeAllRatingReviewActivity;
import com.whosin.app.ui.activites.home.activity.WriteReviewActivity;
import com.whosin.app.ui.activites.offers.ClaimBrunchActivity;
import com.whosin.app.ui.activites.offers.ClaimOfferActivity;
import com.whosin.app.ui.activites.offers.VoucherDetailScreenActivity;
import com.whosin.app.ui.activites.reportedUser.ReportedUseSuccessDialog;
import com.whosin.app.ui.activites.venue.ui.BuyNowActivity;
import com.whosin.app.ui.adapter.OfferAdapter;
import com.whosin.app.ui.fragment.Chat.ReportAndBlockBottomSheet;
import com.whosin.app.ui.fragment.Chat.ReportBottomSheet;
import com.whosin.app.ui.fragment.PromoterCreateEvent.RequirementsAddDialog;
import com.whosin.app.ui.fragment.reviewSheet.UserFullReviewSheet;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class VenueActivity extends BaseActivity {

    private ActivityVenueBinding binding;
    private final RatingReviewAdapter<CurrentUserRatingModel> ratingReviewAdapter = new RatingReviewAdapter<>();
    private final VenueImageSlideAdapter imageSlideAdapter = new VenueImageSlideAdapter();
    private OfferAdapter<OffersModel> offerAdapter;
    private final DiscountOfferAdapter<SpecialOfferModel> discountOfferAdapter = new DiscountOfferAdapter<>();
    private final ExclusiveDealAdapter<VoucherModel> exclusiveDealAdapter = new ExclusiveDealAdapter<>();
    private List<String> galleryList = new ArrayList<>();
    private VenueObjectModel venueObjectModel;
    private String venueId = "";
    private InfoBottomsheetDialogBinding mBinding;
    private int page = 1;
    private final Handler ratingHandler = new Handler(Looper.getMainLooper());
    private Runnable ratingRunnable;
    private static final long RATING_DELAY = 800;


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    protected void initUi() {

        applyTranslations();

        setBackground();
        venueId = getIntent().getStringExtra("venueId");
        binding.venueContainer.isOpenVenue = false;
        setVenueModelData();
        if (venueObjectModel == null && SessionManager.shared.geHomeBlockData() != null) {
            Optional<VenueObjectModel> venueObjectModel1 = SessionManager.shared.geHomeBlockData().getVenues().stream().filter(p -> p.getId().equals(venueId)).findFirst();
            if (venueObjectModel1.isPresent()) {
                binding.venueContainer.setVenueDetail(venueObjectModel1.get());
                setVenueModelData();
            }
        }
        if (venueObjectModel != null) {
            binding.venueContainer.setVenueDetail(venueObjectModel);
            setVenueModelData();
        }

        setRatingReviewAdapter();
        reqVenueDetails(venueId);
        reqOfferDetails(venueId, false);
        requestDeals(venueId);

        Graphics.applyBlurEffect(activity, binding.blurView);

        AppSettingManager.shared.venueReloadCallBack = data -> {
            if (data) {
                finish();
            } else {
                reqVenueDetails(venueId);
            }
        };

    }


    @Override
    protected void setListeners() {

        binding.imageInfo.setOnClickListener(view -> {
            Utils.preventDoubleClick(view);
            if (venueObjectModel.getPhone() == null || venueObjectModel.getEmail() == null) {
                Toast.makeText(this, "Details Not Available", Toast.LENGTH_SHORT).show();
            } else {
                infoBottomSheetDialog();
            }
        });

        binding.tvMenu.setOnClickListener(view ->
                        startActivity(new Intent(activity, MenuActivity.class)
                                .putExtra("url", venueObjectModel.getMenuUrl())
                                .putExtra("name", venueObjectModel.getName()))
//                openUrlInChrome(venueObjectModel.getMenuUrl())

        );

        binding.locationLayout.setOnClickListener(view -> {
            if (isWazeInstalled(activity)) {
                showMapSheet();
            } else {
                openGoogleMap();
            }
        });

        binding.btnFollowButton.setOnClickListener(v -> reqFollowUnFollow());

        binding.ivClose.setOnClickListener(view -> onBackPressed());

        binding.shareVeneueLayout.setOnClickListener(v -> {

            startActivity(new Intent(activity, VenueShareActivity.class)
                    .putExtra("venue", new Gson().toJson(venueObjectModel))
                    .putExtra("type", "venue"));

        });

//        binding.layoutInvite.setOnClickListener( view -> {
//            Utils.preventDoubleClick( view );
//            InviteFriendBottomSheet inviteFriendDialog = new InviteFriendBottomSheet();
//            inviteFriendDialog.venueObjectModel = venueObjectModel;
//            inviteFriendDialog.offerList = offerAdapter.getData();
//            inviteFriendDialog.setShareListener( data -> {
//                AppExecutors.get().mainThread().execute( () -> {
//                } );
//            } );
//            inviteFriendDialog.callback = data -> {
//                if (data != null) {
//                    finish();
//                }
//            };
//            inviteFriendDialog.show( getSupportFragmentManager(), "1" );
//        } );

        binding.imgRecommandation.setOnClickListener(v -> reqRecommendation(venueId));
        binding.businessRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) binding.businessRecycler.getLayoutManager();

                if (linearLayoutManager != null && linearLayoutManager.findLastVisibleItemPosition() == offerAdapter.getData().size() - 1) {
                    if (offerAdapter.getData().size() % 30 == 0) {
                        page++;
                        reqOfferDetails(venueId, true);
                    }
                }
            }
        });

        binding.frequencyDays.setOnClickListener(v -> {
            Utils.preventDoubleClick(v);
            openVisitFrequency();
        });

        binding.editIcon.setOnClickListener(v -> {
            Utils.preventDoubleClick(v);
            openVisitFrequency();
        });
    }


    @Override
    protected void populateData() {
    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityVenueBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        Log.d("TAG", "MessageEvent: " + venueId);
        reqVenueDetails(venueId);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(CurrentUserRatingModel event) {
        Log.d("TAG", "MessageEvent: " + venueId);
        reqVenueDetails(venueId);
    }

    @Override
    protected Map<View, String> getTranslationMap() {
        Map<View, String> map = new HashMap<>();
        map.put(binding.txtInfo, "info");
        map.put(binding.tvLocation, "location");
        map.put(binding.txtShare, "share");
        map.put(binding.txtMenu, "invite");
        map.put(binding.tvOpen, "open");
        map.put(binding.tvMenu, "menu");
        map.put(binding.tvFrequencyLabel, "cm_user_visit_frequency");
        map.put(binding.tvFraturesTitle, "features");
        map.put(binding.tvCuisineTitle, "cuisine");
        map.put(binding.tvMusicTitle, "music");
        map.put(binding.tvThemeTitle, "theme");
        map.put(binding.tvDressCodeTitle, "dress_code");
        map.put(binding.tvExclusiveTitle, "exclusive_deals");
        map.put(binding.tvSubTitle, "buy_your_vocher_or_send_gift_to_your_friends");
        map.put(binding.tvTapToRate, "tap_to_rate");
        map.put(binding.txtReviewTitle, "write_review");
        map.put(binding.tvSeeAll, "see_all");
        map.put(binding.tvCheckOfferTitle, "check_outs_our_offers");
        map.put(binding.tvClickToClaimTitle, "click_to_claim");
        return map;
    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------


    private void openVisitFrequency(){
        RequirementsAddDialog dialog = new RequirementsAddDialog();
        dialog.requirementTitle = binding.tvFrequencyLabel.getText().toString();
        dialog.isFromCmFrequency = true;
        dialog.hintText = getValue("enter_text");
        dialog.editSting = binding.txtFrequency.getText().toString();
        dialog.callback = data -> {
            if (!Utils.isNullOrEmpty(data)) {
                binding.txtFrequency.setText(data);
                requestPromoterVenueSetFrequencyCmVisit(venueId, Integer.parseInt(data));
            }
        };
        dialog.show(getSupportFragmentManager(), "");
    }

    private void openGoogleMap() {
        String geoUri = "google.navigation:q=" + venueObjectModel.getLatLng() + "&label=" + venueObjectModel.getName() + "&directionsmode=driving";
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(geoUri));
        intent.setPackage("com.google.android.apps.maps"); // Specify the package to ensure it opens in Google Maps
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            String webUrl = "http://maps.google.com/maps?q=loc:" + venueObjectModel.getLatLng() + " (" + venueObjectModel.getName() + ")";
            Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(webUrl));

            // Try to set the package to open in Chrome
            webIntent.setPackage("com.android.chrome");
            if (webIntent.resolveActivity(getPackageManager()) != null) {
                // Chrome is available, so open the link in Chrome
                startActivity(webIntent);
            } else {
                // Chrome is not available, open the link in the default web browser
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(webUrl)));
            }

        }
    }


    private static boolean isWazeInstalled(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo("com.waze", PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void setBackground() {
        String venueImage = getIntent().getStringExtra("venueImage");
        Glide.with(context).asBitmap().load(venueImage).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                extractColorFromBitmap(resource);
            }
        });

    }

    private void extractColorFromBitmap(Bitmap bitmap) {
        Palette.from(bitmap).generate(palette -> {
            int dominantColor = palette.getDominantColor(ContextCompat.getColor(context, R.color.bg_color));
            int lightDominantColor = ColorUtils.blendARGB(dominantColor, 0xFFFFFFFF, 0.2f);
            GradientDrawable gradientDrawable = (GradientDrawable) binding.txtLayout.getBackground();
            int[] colors = gradientDrawable.getColors();
            colors[colors.length - 1] = lightDominantColor;
            gradientDrawable.setColors(colors);
            binding.txtLayout.setBackground(gradientDrawable);
        });
    }

    private void showMapSheet() {
        ArrayList<String> data = new ArrayList<>();
        data.add(getValue("google_maps"));
        data.add(getValue("waze"));
        Graphics.showActionSheet(activity, getValue("open_in_maps"), data, (data1, position) -> {
            switch (position) {
                case 0:
                    openGoogleMap();
                    break;
                case 1:
                    String wazeUri = "https://waze.com/ul?ll=" + venueObjectModel.getLatLng() + "&navigate=yes&zoom=17&text=" + venueObjectModel.getName() + "&dirflg=d";
                    Intent wazeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(wazeUri));
                    activity.startActivity(wazeIntent);
                    break;
            }
        });
    }

    private void setRatingReviewAdapter() {
        binding.ratingReviewRecycler.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        binding.ratingReviewRecycler.setAdapter(ratingReviewAdapter);

        offerAdapter = new OfferAdapter<>(this, getSupportFragmentManager(), OfferAdapter.OfferType.VENUE);
        binding.businessRecycler.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        binding.businessRecycler.setAdapter(offerAdapter);

        binding.exclusiveRecycler.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        binding.exclusiveRecycler.setAdapter(exclusiveDealAdapter);
    }

    private void setVenueImageSlideAdapter(VenueObjectModel data) {
        binding.imageSlideRecycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        binding.imageSlideRecycler.setAdapter(imageSlideAdapter);
        List<String> list = new ArrayList<>();

        //List<String> list = data.getGalleries();
        list.add(0, data.getCover());
        if (data.getGalleries() != null && !data.getGalleries().isEmpty()) {
            list.addAll(data.getGalleries());
        }
        galleryList = list;
        if (!list.isEmpty()) {
            imageSlideAdapter.updateData(list);
        }
    }

    private void infoBottomSheetDialog() {

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        mBinding = InfoBottomsheetDialogBinding.inflate(getLayoutInflater());
        bottomSheetDialog.setContentView(mBinding.getRoot());
        if (venueObjectModel.getEmail() != null && !venueObjectModel.getEmail().isEmpty()) {
            mBinding.tvMail.setText(venueObjectModel.getEmail());
        } else {
            mBinding.layoutEmail.setVisibility(View.GONE);
        }

        if (venueObjectModel.getPhone() != null && !venueObjectModel.getPhone().isEmpty()) {
            String phone = venueObjectModel.getPhone();
            String[] phoneNumbers;
            if (phone.contains(",")) {
                phoneNumbers = phone.split(",\\s*");
            } else {
                phoneNumbers = new String[]{phone};
            }

            mBinding.listRecycler.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
            PhoneNumberAdapter adapter = new PhoneNumberAdapter(phoneNumbers);
            mBinding.listRecycler.setAdapter(adapter);
        } else {
            mBinding.listRecycler.setVisibility(View.GONE);
        }


        mBinding.tvMail.setOnClickListener(view -> {
            Intent i = new Intent(Intent.ACTION_SENDTO);
            i.setType("message/rfc822");
            i.setData(Uri.parse("mailto:" + venueObjectModel.getEmail()));
            startActivity(i);
        });
        mBinding.layoutCancel.setOnClickListener(view -> {
            bottomSheetDialog.cancel();
        });
        bottomSheetDialog.show();
    }

    private void setDiscountAdapter() {
        binding.discountRecycler.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        binding.discountRecycler.setAdapter(discountOfferAdapter);

        if (!venueObjectModel.getSpecialOffers().isEmpty()) {
            discountOfferAdapter.updateData(venueObjectModel.getSpecialOffers());
        } else {
            binding.linearOffer.setVisibility(View.GONE);
        }


    }

    private void setVenueModelData() {

        if (venueObjectModel == null) {
            return;
        }

        if (SessionManager.shared.getUser().isPromoter() && PromoterProfileManager.shared.isFormPromoterVenue(venueId)) {
            binding.ilFrequency.setVisibility(View.VISIBLE);
            binding.txtFrequency.setText(String.valueOf(venueObjectModel.getFrequencyOfVisitForCm()));
        } else {
            binding.ilFrequency.setVisibility(View.GONE);
        }

        binding.venueContainer.setVenueDetail(venueObjectModel);

        Graphics.loadImage(venueObjectModel.getCover(), binding.imageVenue);

        int newColor;
        if (venueObjectModel.isRecommendation()) {
            newColor = ContextCompat.getColor(activity, R.color.brand_pink);
            binding.txtRecommend.setText(getValue("recommended"));
        } else {
            newColor = ContextCompat.getColor(activity, R.color.white);
            binding.txtRecommend.setText(getValue("recommend"));

        }
        binding.imgRecommandation.setColorFilter(newColor);

        if (Utils.isNullOrEmpty(venueObjectModel.getDressCode())) {
            binding.ilDressCode.setVisibility(View.GONE);

        } else {
            binding.ilDressCode.setVisibility(View.VISIBLE);
            binding.tvDressCode.setText(venueObjectModel.getDressCode());
        }




        if (venueObjectModel.getFeature() == null || venueObjectModel.getFeature().isEmpty()) {
            binding.ilFeatures.setVisibility(View.GONE);

        } else {
            binding.ilFeatures.setVisibility(View.VISIBLE);
            binding.txtFeatures.setText(TextUtils.join(", ", venueObjectModel.getFeature()));
        }
        if (venueObjectModel.getCuisine() == null || venueObjectModel.getCuisine().isEmpty()) {
            binding.ilCuisine.setVisibility(View.GONE);
        } else {
            binding.ilCuisine.setVisibility(View.VISIBLE);
            binding.txtCuisine.setText(TextUtils.join(", ", venueObjectModel.getCuisine()));
        }
        if (venueObjectModel.getMusic() == null || venueObjectModel.getMusic().isEmpty()) {
            binding.ilMusic.setVisibility(View.GONE);
        } else {
            binding.ilMusic.setVisibility(View.VISIBLE);
            binding.tvMusic.setText(TextUtils.join(", ", venueObjectModel.getMusic()));
        }

        if (venueObjectModel.getTheme() == null || venueObjectModel.getTheme().isEmpty()) {
            binding.ilTheme.setVisibility(View.GONE);
        } else {
            binding.ilTheme.setVisibility(View.VISIBLE);
            binding.tvTheme.setText(TextUtils.join(", ", venueObjectModel.getTheme()));
        }

        binding.tvOpen.setTextColor(getResources().getColor(venueObjectModel.isOpen() ? R.color.green : R.color.redColor));
        binding.tvOpen.setText(venueObjectModel.isOpen() ? getValue("open") : getValue("closed"));

        List<VenueTimingModel> models = venueObjectModel.getTiming();
        if (!models.isEmpty()) {
            String today = Utils.formatDate(new Date(), "EEE");
            Optional<VenueTimingModel> todayTiming = venueObjectModel.getTiming().stream().filter(p -> p.getDay().equalsIgnoreCase(today)).findFirst();
            if (todayTiming.isPresent()) {
                binding.txtTime.setText(String.format("%s - %s", todayTiming.get().getOpeningTime(), todayTiming.get().getClosingTime()));
            } else {
                binding.txtTime.setText("00:00 - 00:00");
            }
        } else {
            binding.ilOpenTime.setVisibility(View.GONE);
        }
        binding.ivTime.setOnClickListener(view -> timeDialog(venueObjectModel.getTiming()));

        binding.tvDescription.setText(venueObjectModel.getAbout());
        binding.tvDescription.post(() -> {
            int lineCount = binding.tvDescription.getLineCount();
            if (lineCount > 2) {
                Utils.makeTextViewResizable(binding.tvDescription, 3, 3, "..." + getValue("see_more") , true);
            }
        });

        if (venueObjectModel.getReviews() != null && !venueObjectModel.getReviews().isEmpty()) {
            if (venueObjectModel.getUsers() != null && !venueObjectModel.getUsers().isEmpty()) {
                binding.ratingReviewRecycler.setVisibility(View.VISIBLE);
                List<CurrentUserRatingModel> sortedList = venueObjectModel.getReviews().stream()
                        .sorted((r1, r2) -> {
                            String currentUserId = SessionManager.shared.getUser().getId();
                            boolean isR1CurrentUser = r1.getUserId().equals(currentUserId);
                            boolean isR2CurrentUser = r2.getUserId().equals(currentUserId);

                            if (isR1CurrentUser && !isR2CurrentUser) return -1;
                            else if (!isR1CurrentUser && isR2CurrentUser) return 1;
                            else return 0;
                        })
                        .collect(Collectors.toList());
//                ratingReviewAdapter.updateData(venueObjectModel.getReviews());
                ratingReviewAdapter.updateData(sortedList);
                List<CurrentUserRatingModel> matchingList = venueObjectModel.getReviews().stream()
                        .filter(ratingModel -> SessionManager.shared.getUser().getId().equals(ratingModel.getUserId()))
                        .collect(Collectors.toList());
                if (!matchingList.isEmpty()) {
                    binding.txtReviewTitle.setText(getValue("edit_review"));
                }else {
                    binding.txtReviewTitle.setText(getValue("write_review"));
                }
            }

        }else {
            binding.ratingReviewRecycler.setVisibility(View.GONE);
            binding.txtReviewTitle.setText(getValue("write_review"));
        }

        binding.rating.setOnRatingChangeListener(null);
        binding.rating.setRating(venueObjectModel.getCurrentUserRating().getStars());

        binding.rating.setOnRatingChangeListener((ratingBar, rating) -> {
            if (ratingRunnable != null) {
                ratingHandler.removeCallbacks(ratingRunnable);
            }
            ratingRunnable = () -> reqAddRatings((int) rating);
            ratingHandler.postDelayed(ratingRunnable, RATING_DELAY);
        });


        if (venueObjectModel.getAvgRatings() != 0) {
            binding.tvRate.setText(String.format("%.1f", venueObjectModel.getAvgRatings()));
        } else {
            binding.tvRate.setVisibility(View.GONE);
            binding.ImgRating.setVisibility(View.GONE);
        }

        binding.btnFollowButton.setVenueRequestStatus(venueObjectModel);

        if (venueObjectModel.getSpecialOffers() != null && !venueObjectModel.getSpecialOffers().isEmpty()) {
            setDiscountAdapter();
        } else {
            binding.linearOffer.setVisibility(View.GONE);
        }


        double i = venueObjectModel.getDistance();
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        String formattedDistance = decimalFormat.format(i);

        if (venueObjectModel.getDistance() == 0) {
            binding.tvLocation.setText(getValue("location"));
        } else {
            binding.tvLocation.setText(String.format("%skm", formattedDistance));

        }


        if (venueObjectModel.isIsAllowReview()) {
            binding.linearReview.setVisibility(View.VISIBLE);
            binding.tvSeeAll.setVisibility(View.VISIBLE);
        } else {
            binding.linearReview.setVisibility(View.GONE);
            binding.tvSeeAll.setVisibility(View.GONE);
        }

        if (venueObjectModel.isIsAllowRatting()) {
            binding.linearReview.setVisibility(View.VISIBLE);
        } else {
            binding.linearRating.setVisibility(View.GONE);
            binding.ratingReviewRecycler.setVisibility(View.GONE);
            binding.linearReview.setVisibility(View.GONE);
        }

        if (venueObjectModel.getMenuUrl() != null && !venueObjectModel.getMenuUrl().isEmpty()) {
            binding.menuLinear.setVisibility(View.VISIBLE);
        } else {
            binding.menuLinear.setVisibility(View.GONE);
        }

        int totalImages = venueObjectModel.getGalleries().size();
        if (totalImages > 5) {
            int countToShow = totalImages - 5;
            binding.tvCount.setText(String.format("+%s", countToShow));
            binding.tvCount.setVisibility(View.VISIBLE);
        } else {
            binding.tvCount.setVisibility(View.GONE);
        }

        binding.linearReview.setOnClickListener(view -> {
            Utils.preventDoubleClick(view);
            WriteReviewActivity bottomSheet = new WriteReviewActivity(venueObjectModel.getId(), venueObjectModel.getCurrentUserRating(), "venues");
            bottomSheet.activity = activity;
            bottomSheet.show(getSupportFragmentManager(), "1");
        });

        binding.tvSeeAll.setOnClickListener(view -> startActivity(new Intent(activity, SeeAllRatingReviewActivity.class)
                .putExtra("id", venueObjectModel.getId())
                .putExtra("type", "venues")
                .putExtra("currentUserRating", new Gson().toJson(venueObjectModel.getCurrentUserRating()))));
    }

    private void timeDialog(List<VenueTimingModel> timingDialogs) {
        VenueTimingDialog dialog = new VenueTimingDialog(timingDialogs, activity);
        dialog.show(getSupportFragmentManager(), "1");
    }

    private void openUrlInChrome(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.setPackage("com.android.chrome");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void openDeleteActionSheet(CurrentUserRatingModel model){
        ArrayList<String> data = new ArrayList<>();
        data.add(getValue("delete"));
        Graphics.showActionSheet(activity, activity.getString(R.string.app_name), getValue("close"), data, (data1, position1) -> {
            if (position1 == 0) {
                Graphics.showAlertDialogWithOkCancel(activity, activity.getString(R.string.app_name), getValue("delete_review_confirmation"), getValue("yes"), getValue("cancel"), isConfirmed -> {
                    if (isConfirmed) {
                        requestMyReviewDelete(model.getId());
                    }
                });
            }
        });
    }

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void reqVenueDetails(String id) {
        showProgress();
        DataService.shared(activity).requestVenueDetail(id, new RestCallback<ContainerModel<VenueObjectModel>>(this) {

            @Override
            public void result(ContainerModel<VenueObjectModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, R.string.service_message_something_wrong, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (model.getData() != null) {
                    venueObjectModel = model.getData();
                    setVenueModelData();
                    binding.constraintMain.setVisibility(View.VISIBLE);
                    setVenueImageSlideAdapter(model.getData());
                    if (!offerAdapter.getData().isEmpty()) {
                        offerAdapter.getData().forEach(p -> p.setVenue(venueObjectModel));
                    }
                }
            }
        });
    }

    private void reqRecommendation(String id) {
        showProgress();
        DataService.shared(activity).requestFeedRecommandation(id, "venue", new RestCallback<ContainerModel<UserDetailModel>>(this) {
            @Override
            public void result(ContainerModel<UserDetailModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, R.string.service_message_something_wrong, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (model.message.equals("recommendation added successfully!")) {
                    int newColor = ContextCompat.getColor(activity, R.color.brand_pink);
                    binding.txtRecommend.setText(getValue("recommended"));
                    binding.imgRecommandation.setColorFilter(newColor);
                    Alerter.create(activity).setTitle(getValue("thank_you")).setTitleAppearance(R.style.AlerterTitle).setTextAppearance(R.style.AlerterText).setText(setValue("recommending_toast",venueObjectModel.getName())).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                } else {
                    int newColor = ContextCompat.getColor(activity, R.color.white);
                    binding.imgRecommandation.setColorFilter(newColor);
                    binding.txtRecommend.setText(getValue("recommend"));
                    Alerter.create(activity).setTitle(getValue("oh_snap")).setTitleAppearance(R.style.AlerterTitle).setTextAppearance(R.style.AlerterText).setText(setValue("recommending_remove_toast",venueObjectModel.getName())).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                }

            }
        });
    }

    private void reqOfferDetails(String venueId, boolean showProgressBar) {
        if (showProgressBar) {
            binding.pagginationProgressBar.setVisibility(View.VISIBLE);
        }
        JsonObject object = new JsonObject();
        object.addProperty("venueId", venueId);
        object.addProperty("page", page);
        object.addProperty("limit", 30);
        object.addProperty("day", "all");
        Log.d("All Venue", "reqOfferDetails: " + object);
        Log.d("All Venue", "reqOfferDetails: " + SessionManager.shared.getToken());
        DataService.shared(activity).requestOfferList(object, new RestCallback<ContainerListModel<OffersModel>>(this) {
            @Override
            public void result(ContainerListModel<OffersModel> model, String error) {
                binding.pagginationProgressBar.setVisibility(View.GONE);
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (model.data != null && !model.data.isEmpty()) {
                    model.data.forEach(p -> p.setVenue(venueObjectModel));
                    List<OffersModel> offerList = offerAdapter.getData();
                    if (offerList == null) {
                        offerList = model.data;
                    } else {
                        offerList.addAll(model.data);
                    }
                    offerAdapter.updateData(offerList);
                } else {
                    if (offerAdapter.getData().isEmpty()) {
                        binding.businessRecycler.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    private void requestDeals(String venueId) {
        JsonObject object = new JsonObject();
        object.addProperty("venueId", venueId);
        DataService.shared(activity).requestCategoryDealList(object, new RestCallback<ContainerListModel<VoucherModel>>(this) {
            @Override
            public void result(ContainerListModel<VoucherModel> model, String error) {
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (model.data != null && !model.data.isEmpty()) {
                    exclusiveDealAdapter.updateData(model.data);
                } else {
                    binding.linearDeal.setVisibility(View.GONE);
                }
            }
        });

    }

    private void reqAddRatings(int rating) {

        DataService.shared(activity).requestAddRatings(venueId, rating, "venues", "", "", new RestCallback<ContainerModel<CurrentUserRatingModel>>(this) {
            @Override
            public void result(ContainerModel<CurrentUserRatingModel> model, String error) {
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, R.string.service_message_something_wrong, Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(activity, model.message, Toast.LENGTH_SHORT).show();
            }

        });

    }

    private void reqFollowUnFollow() {
        if (TextUtils.isEmpty(venueId)) {
            venueId = venueObjectModel.getId();
        }
        if (TextUtils.isEmpty(venueId)) {
            Alerter.create(activity).setTitle("Opps!").setTitleAppearance(R.style.AlerterTitle).setTextAppearance(R.style.AlerterText).setText("No venue found").setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
        }
        binding.btnFollowButton.requestFollowUnfollowVenue(venueObjectModel, activity, (success, message) -> {
            if (message.equals("Unfollowed!")) {
                venueObjectModel.setIsFollowing(false);
                Alerter.create(activity).setTitle(getValue("oh_snap")).setTitleAppearance(R.style.AlerterTitle).setTextAppearance(R.style.AlerterText).setText(setValue("unfollow_toast",venueObjectModel.getName())).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
            } else {
                venueObjectModel.setIsFollowing(true);
                Alerter.create(activity).setTitle(getValue("thank_you")).setText(setValue("follow_venue",venueObjectModel.getName())).setTextAppearance(R.style.AlerterText).setTitleAppearance(R.style.AlerterTitle).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
            }
            binding.btnFollowButton.setVenueRequestStatus(venueObjectModel);
            EventBus.getDefault().post(venueObjectModel);
        });
    }

    private void requestPromoterVenueSetFrequencyCmVisit(String venueId, int days) {
        showProgress();
        DataService.shared(activity).requestPromoterVenueSetFrequencyCmVisit(venueId, days, new RestCallback<ContainerModel<PromoterVenueModel>>(this) {

            @Override
            public void result(ContainerModel<PromoterVenueModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity,error, Toast.LENGTH_SHORT).show();
                    return;
                }

                Alerter.create(activity).setTitle("").setTitleAppearance(R.style.AlerterTitle).setTextAppearance(R.style.AlerterText).setText("Frequency Updated.").setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();

            }
        });
    }

    private void requestBlockUserAdd(String id,String userFullName) {
        DataService.shared(activity).requestBlockUser(id, new RestCallback<ContainerModel<CommonModel>>() {
            @Override
            public void result(ContainerModel<CommonModel> model, String error) {
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }
                Alerter.create(activity).setTitle(getValue("oh_snap")).setText(getValue("you_have_blocked") + userFullName).setTextAppearance(R.style.AlerterText).setTitleAppearance(R.style.AlerterTitle).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                BlockUserManager.addBlockUserId(id);
                reqVenueDetails(id);
                finish();
            }
        });
    }

    private void requestMyReviewDelete(String id) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", id);
        showProgress();
        DataService.shared(activity).requestMyReviewDelete(jsonObject, new RestCallback<ContainerModel<CurrentUserRatingModel>>(this) {
            @Override
            public void result(ContainerModel<CurrentUserRatingModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }

                reqVenueDetails(venueId);
            }
        });
    }

    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    public class VenueImageSlideAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = UiUtils.getViewBy(parent, R.layout.item_image_slide_recycler);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) view.getLayoutParams();
            layoutParams.rightMargin = view.getContext().getResources().getDimensionPixelSize(com.intuit.sdp.R.dimen._minus10sdp);
            if (viewType == 0) {
                layoutParams.width = view.getContext().getResources().getDimensionPixelSize(com.intuit.sdp.R.dimen._48sdp);
            }
            return new VenueImageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            VenueImageViewHolder viewHolder = (VenueImageViewHolder) holder;

            if (galleryList != null) {
                String image = galleryList.get(position);
                Graphics.loadRoundImage(image, viewHolder.mBinding.image);
                viewHolder.mBinding.image.setOnClickListener(v -> startActivity(new Intent(activity, VenueGalleryActivity.class).putExtra("galleries", new Gson().toJson(galleryList))));
            }

        }

        @Override
        public int getItemViewType(int position) {
            if (getItemCount() == position + 1) {
                return 0;
            } else {
                return 1;
            }
        }

        @Override
        public int getItemCount() {
            return Math.min(galleryList.size(), 6);
        }

        public class VenueImageViewHolder extends RecyclerView.ViewHolder {
            private final ItemImageSlideRecyclerBinding mBinding;

            public VenueImageViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemImageSlideRecyclerBinding.bind(itemView);
            }
        }
    }

    public class RatingReviewAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = UiUtils.getViewBy(parent, R.layout.item_rating_review_recycler);
            ViewGroup.LayoutParams params = view.getLayoutParams();
            int itemCount = getItemCount();
            if (itemCount > 1) {
                params.width = (int) (Graphics.getScreenWidth(context) * 0.85);
            } else {
                params.width = (int) (Graphics.getScreenWidth(context) * 0.90);
            }
            view.setLayoutParams(params);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            CurrentUserRatingModel model = (CurrentUserRatingModel) getItem(position);
            viewHolder.mBinding.txtReview.setText(model.getReview());
            viewHolder.mBinding.txtReply.setText(model.getReply());
            viewHolder.mBinding.replyLinear.setVisibility(View.GONE);
            viewHolder.mBinding.iconMenu.setVisibility(View.VISIBLE);
            viewHolder.mBinding.txtDate.setText(Utils.convertMainDateFormat(model.getCreatedAt()));
            viewHolder.mBinding.rating.setRating(model.getStars());

            Optional<ContactListModel> modelOptional = venueObjectModel.getUsers().stream().
                    filter(p -> p.getId().equals(model.getUserId())).findFirst();
            if (modelOptional.isPresent()) {
                Graphics.loadImageWithFirstLetter(modelOptional.get().getImage(),
                        viewHolder.mBinding.ivRating, modelOptional.get().getFirstName());
                viewHolder.mBinding.txtTitle.setText(modelOptional.get().getFullName());

            }

            viewHolder.mBinding.iconMenu.setOnClickListener(v -> {
                Optional<ContactListModel> contactListModel = venueObjectModel.getUsers().stream().
                        filter(p -> p.getId().equals(model.getUserId())).findFirst();
                if (contactListModel.isPresent() && contactListModel.get().getId().equals(SessionManager.shared.getUser().getId())){
                    openDeleteActionSheet(model);
                   return;
                }
                ReportAndBlockBottomSheet bottomSheet = new ReportAndBlockBottomSheet();
                bottomSheet.reportSheetCallBack = data -> {
                    if (data) {
                        ReportBottomSheet reportBottomSheet = new ReportBottomSheet();
                        reportBottomSheet.ratingModel = model;
                        reportBottomSheet.isFromChat = false;
                        reportBottomSheet.isOnlyReport = true;
                        reportBottomSheet.callback = data1 -> {
                            ReportedUseSuccessDialog dialog = new ReportedUseSuccessDialog();
                            dialog.callBack = data2 -> {
                                if (data2) {
                                    finish();
                                }
                            };
                            dialog.show(getSupportFragmentManager(),"");

                        };
                        reportBottomSheet.show(getSupportFragmentManager(), "");
                    }
                };
                bottomSheet.reportAndBlockCallBack = data -> {
                    if (data) {
                        ReportBottomSheet reportBottomSheet = new ReportBottomSheet();
                        reportBottomSheet.ratingModel = model;
                        reportBottomSheet.isFromChat = false;
                        reportBottomSheet.callback = data1 -> {
                            ReportedUseSuccessDialog dialog = new ReportedUseSuccessDialog();
                            dialog.callBack = data2 -> {
                                if (data2){
                                    contactListModel.ifPresent(listModel -> requestBlockUserAdd(listModel.getId(), viewHolder.mBinding.txtTitle.getText().toString()));

                                }
                            };
                        };
                        reportBottomSheet.show(getSupportFragmentManager(), "");
                    }
                };
                bottomSheet.callback = data -> {
                    if (data) {
                        Graphics.showAlertDialogWithOkCancel(activity, activity.getString(R.string.app_name), Utils.setLangValue("block_user_alert",viewHolder.mBinding.txtTitle.getText().toString()), Utils.getLangValue("yes"), Utils.getLangValue("no"), isConfirmed -> {
                            if (isConfirmed) {
                                contactListModel.ifPresent(listModel -> requestBlockUserAdd(listModel.getId(), viewHolder.mBinding.txtTitle.getText().toString()));
                            }
                        });
                    }
                };
                bottomSheet.show(getSupportFragmentManager(), "");
            });

            viewHolder.mBinding.getRoot().setOnClickListener(v -> {
                UserFullReviewSheet reviewSheet = new UserFullReviewSheet();
                modelOptional.ifPresent(userDetailModel -> reviewSheet.contactListModel = userDetailModel);
                reviewSheet.currentUserRatingModel = model;
                reviewSheet.callback = data -> {
                    if (data) reqVenueDetails(venueId);
                };
                reviewSheet.show(getSupportFragmentManager(),"");
            });

        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final ItemRatingReviewRecyclerBinding mBinding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemRatingReviewRecyclerBinding.bind(itemView);
            }
        }
    }

    public class DiscountOfferAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_discount_recycler));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            SpecialOfferModel model = (SpecialOfferModel) getItem(position);

            Log.d("TAG", "onBindViewHolder: " + model.getClaimCode());
            if (!Utils.isNullOrEmpty(String.valueOf(model.getDiscount()))) {
                viewHolder.mBinding.tvDiscount.setText(model.getDiscount() + "% OFF");
            } else {
                viewHolder.mBinding.tvDiscount.setText(" 0%\nOFF");
            }
            viewHolder.mBinding.tvOffer.setText(model.getTitle());
            viewHolder.mBinding.tvDescription.setText(model.getDescription());

            viewHolder.itemView.setOnClickListener(v -> {
                Utils.preventDoubleClick(v);
                if (model.getType().equals("total")) {
                    startActivity(new Intent(activity, ClaimOfferActivity.class).putExtra("specialOfferModel", new Gson().toJson(model)).putExtra("venueModel", new Gson().toJson(venueObjectModel)));
                } else {
                    startActivity(new Intent(activity, ClaimBrunchActivity.class).putExtra("specialOfferModel", new Gson().toJson(model)).putExtra("venueModel", new Gson().toJson(venueObjectModel)));
                }
            });
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final ItemDiscountRecyclerBinding mBinding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemDiscountRecyclerBinding.bind(itemView);
            }
        }
    }

    public class ExclusiveDealAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.exclusive_item_recycler));
            View view = UiUtils.getViewBy(parent, R.layout.exclusive_item_recycler);
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.width = (int) (Graphics.getScreenWidth(Graphics.context) * (getItemCount() > 1 ? 0.83 : 0.93));
            view.setLayoutParams(params);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            VoucherModel model = (VoucherModel) getItem(position);

            viewHolder.mBinding.tvSubTitle.setText(model.getTitle());
            viewHolder.mBinding.tvTitle.setText(model.getDescription());


//            viewHolder.mBinding.tvAED.setPaintFlags(viewHolder.mBinding.tvAED.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

//            if (model.getDiscountedPrice() == Integer.parseInt(model.getActualPrice())) {
//                viewHolder.mBinding.tvAED.setVisibility(View.GONE);
//                viewHolder.mBinding.tvPrice.setText(String.valueOf(model.getActualPrice()));
//            } else {
//                viewHolder.mBinding.tvAED.setVisibility(View.VISIBLE);
//            }
//            viewHolder.mBinding.tvAED.setText(String.valueOf(model.getActualPrice()));


            SpannableString styledPrice = Utils.getStyledText(activity,String.valueOf(model.getDiscountedPrice()));
            SpannableStringBuilder fullText = new SpannableStringBuilder();
            fullText.append(getValue("from")).append(styledPrice);
            viewHolder.mBinding.buyNowBtn.setText(fullText);

            Graphics.loadImage(model.getImage(), viewHolder.mBinding.ivCover);

            if (position % 2 == 0) {
                viewHolder.mBinding.linear.setBackgroundResource(R.drawable.pink_orange_gradiant);
            } else {
                viewHolder.mBinding.linear.setBackgroundResource(R.drawable.multi_green_color_gradiant);
            }

            viewHolder.mBinding.buyNowBtn.setOnClickListener(view ->
                    startActivity(new Intent(activity, BuyNowActivity.class).putExtra("deals", model.getId())
                            .putExtra("venueDetail", new Gson().toJson(model.getVenue())).putExtra("offerModel", new Gson().toJson(model))));
            viewHolder.mBinding.getRoot().setOnClickListener(view -> startActivity(new Intent(activity, VoucherDetailScreenActivity.class).putExtra("id", model.getId())));

        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final ExclusiveItemRecyclerBinding mBinding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ExclusiveItemRecyclerBinding.bind(itemView);
            }
        }
    }

    public class PhoneNumberAdapter extends RecyclerView.Adapter<PhoneNumberAdapter.ViewHolder> {
        private String[] phoneNumbers;

        public PhoneNumberAdapter(String[] phoneNumbers) {
            this.phoneNumbers = phoneNumbers;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_phone_number, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.binding.tvPhone.setText(phoneNumbers[position]);
            holder.itemView.setOnClickListener(view -> {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + phoneNumbers[position]));
                startActivity(callIntent);
            });
        }

        @Override
        public int getItemCount() {
            return phoneNumbers.length;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private ItemPhoneNumberBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemPhoneNumberBinding.bind(itemView);
            }
        }
    }

    // endregion
    // --------------------------------------
}