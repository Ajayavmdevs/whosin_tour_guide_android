package com.whosin.app.ui.activites.raynaTicket;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.whosin.app.comman.Graphics.context;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.tapadoo.alerter.Alerter;
import com.whosin.app.BuildConfig;
import com.whosin.app.R;
import com.whosin.app.comman.AppConstants;
import com.whosin.app.comman.CloneUtils;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.comman.ui.ContactUsBlockManager;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.comman.ui.VerticalSpaceItemDecoration;
import com.whosin.app.databinding.ActivityRaynaTicketDetailBinding;
import com.whosin.app.databinding.ItemRatingReviewRecyclerBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.AppSettingManager;
import com.whosin.app.service.manager.BlockUserManager;
import com.whosin.app.service.manager.JPTicketManager;
import com.whosin.app.service.manager.LogManager;
import com.whosin.app.service.manager.RaynaTicketManager;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.BigBusModels.BigBusOptionsItemModel;
import com.whosin.app.service.models.ChatModel;
import com.whosin.app.service.models.CommonModel;
import com.whosin.app.service.models.ContactUsBlockModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.CurrentUserRatingModel;
import com.whosin.app.service.models.RatingModel;
import com.whosin.app.service.models.TravelDeskModels.TravelDeskOptionDataModel;
import com.whosin.app.service.models.TravelDeskModels.TravelDeskTourDataModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.models.YachtFeatureModel;
import com.whosin.app.service.models.rayna.RaynaTicketDetailModel;
import com.whosin.app.service.models.rayna.TourDataModel;
import com.whosin.app.service.models.whosinTicketModel.WhosinTicketTourOptionModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.JpHotel.JPHotelDatePaxSelectActivity;
import com.whosin.app.ui.activites.auth.AuthenticationActivity;
import com.whosin.app.ui.activites.bigBusTicket.BigBusTourOptionActivity;
import com.whosin.app.ui.activites.comman.BaseActivity;
import com.whosin.app.ui.activites.home.Chat.ChatMessageActivity;
import com.whosin.app.ui.activites.home.activity.SeeAllRatingReviewActivity;
import com.whosin.app.ui.activites.home.activity.WriteReviewActivity;
import com.whosin.app.ui.activites.raynaTicket.BottomSheets.ReadMoreBottomSheet;
import com.whosin.app.ui.activites.reportedUser.ReportedUseSuccessDialog;
import com.whosin.app.ui.activites.travelDeskTicket.TravelDeskTourOptionActivity;
import com.whosin.app.ui.activites.venue.VenueShareActivity;
import com.whosin.app.ui.activites.whosinTicket.WhosinCustomTicketTourOptionActivity;
import com.whosin.app.ui.activites.whosinTicket.WhosinTicketTourOptionActivity;
import com.whosin.app.ui.adapter.raynaTicketAdapter.RaynaTicketDetailGalleryAdapter;
import com.whosin.app.ui.adapter.raynaTicketAdapter.RaynaTicketFeaturesAdapter;
import com.whosin.app.ui.adapter.raynaTicketAdapter.TicketTagesAdapter;
import com.whosin.app.ui.controller.raynaTicketsView.SuggestedTicketView;
import com.whosin.app.ui.controller.raynaTicketsView.TicketDescriptionsView;
import com.whosin.app.ui.fragment.Chat.ReportAndBlockBottomSheet;
import com.whosin.app.ui.fragment.Chat.ReportBottomSheet;
import com.whosin.app.ui.fragment.reviewSheet.UserFullReviewSheet;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class RaynaTicketDetailActivity extends BaseActivity implements OnMapReadyCallback {

    private ActivityRaynaTicketDetailBinding binding;

    private RaynaTicketDetailModel raynaTicketDetailModel;

    private RaynaTicketFeaturesAdapter<YachtFeatureModel> raynaTicketFeaturesAdapter;

    private RaynaTicketFeaturesAdapter<YachtFeatureModel> raynaTicketWhatsIncludeAdapter;

    private final RatingReviewAdapter<CurrentUserRatingModel> ratingReviewAdapter = new RatingReviewAdapter<>();

    private final TicketTagesAdapter<RatingModel> ticketTagsAdapter = new TicketTagesAdapter<>();

    private RaynaTicketDetailGalleryAdapter raynaGalleryAdapter;

    private Handler handler = new Handler();

    private GoogleMap gMap;

    private double latitude, longitude;

    private boolean blurViewVisible = false;
    private final Handler ratingHandler = new Handler(Looper.getMainLooper());
    private Runnable ratingRunnable;
    private static final long RATING_DELAY = 800;


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    protected void initUi() {


        applyTranslations();

        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );


        RaynaTicketManager.shared.clearManager();
        RaynaTicketManager.shared.activityList.add(activity);

        Utils.hideViews(binding.mainConstraint,binding.btnFavorite,binding.ivShare);

        Graphics.applyBlurEffect(activity, binding.blurView);
        Graphics.applyBlurEffect(activity, binding.favTicketblurView);
        Graphics.applyBlurEffect(activity, binding.shareTicketblurView);

        raynaTicketFeaturesAdapter = new RaynaTicketFeaturesAdapter<>(activity);
        setupRecyclerView(binding.featureRecycler, raynaTicketFeaturesAdapter);

        raynaTicketWhatsIncludeAdapter = new RaynaTicketFeaturesAdapter<>(activity);
        setupRecyclerView(binding.whatIncluesRecycler, raynaTicketWhatsIncludeAdapter);

//        binding.ticketOptionsRecycler.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));


        binding.ticketTagesRecycleView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        binding.ticketTagesRecycleView.setAdapter(ticketTagsAdapter);

        raynaGalleryAdapter = new RaynaTicketDetailGalleryAdapter(activity, (CommanCallback<Integer>) data -> startActivity(new Intent(activity, RaynaGalleryViewActivity.class).putExtra("model",new Gson().toJson(raynaTicketDetailModel)).putExtra("scrollToPosition",data)));

        binding.raynaGalleryRecyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        binding.raynaGalleryRecyclerView.setAdapter(raynaGalleryAdapter);
        PagerSnapHelper snapHelper = new PagerSnapHelper ();
        snapHelper.attachToRecyclerView(binding.raynaGalleryRecyclerView);

        binding.mapView.onCreate(saveBundle);

        String ticketId = getIntent().getStringExtra("ticketId");
        Log.d("ticketId", "initUi: " + ticketId);
        if (!TextUtils.isEmpty(ticketId)) {
            requestTicketDetail(ticketId);
        }

        AppSettingManager.shared.venueReloadCallBack = data -> {
            if (data) {
                finish();
            } else {
                requestTicketDetail(raynaTicketDetailModel.getId());
            }
        };

        binding.suggestedTicketView.initData(this, getSupportFragmentManager(), ticketId);

    }

    @Override
    protected void setListeners() {

        binding.ivClose.setOnClickListener(view -> {
            RaynaTicketManager.shared.clearManager();
            onBackPressed();
        });

        binding.btnGetTicket.setOnClickListener(view -> {
            Utils.preventDoubleClick(view);

            String id = "";
            String title = "";
            Double price = 0.0;
            if (raynaTicketDetailModel != null) {
                id = raynaTicketDetailModel.getId();
                title = raynaTicketDetailModel.getTitle();
                try {
                    Object amount = raynaTicketDetailModel.getStartingAmount();
                    if (amount instanceof Number) {
                        price = ((Number) amount).doubleValue();
                    } else if (amount instanceof String) {
                        price = Double.parseDouble((String) amount);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            LogManager.shared.logTicketEvent(LogManager.LogEventType.getTicket, id, title, price, null, "AED");

            if (raynaTicketDetailModel.getBookingType().equals("whosin")) {

                startActivity(new Intent(activity, WhosinTicketTourOptionActivity.class));
                 
            } else if (raynaTicketDetailModel.getBookingType().equals("whosin-ticket")) {
                if (raynaTicketDetailModel.getWhosinTicketTourDataList().isEmpty()) return;
                List<WhosinTicketTourOptionModel> tmpList = raynaTicketDetailModel.getWhosinTicketTourDataList().get(0).getOptionData();

                List<WhosinTicketTourOptionModel> deepCopyList = tmpList.stream()
                        .map(CloneUtils::cloneObject)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

                RaynaTicketManager.shared.whosinCustomTicketTourOption.clear();
                RaynaTicketManager.shared.whosinCustomTicketTourOption.addAll(deepCopyList);

                startActivity(new Intent(activity, WhosinCustomTicketTourOptionActivity.class));
                
            }else if (raynaTicketDetailModel.getBookingType().equals("big-bus") || raynaTicketDetailModel.getBookingType().equals("hero-balloon")) {
                if (raynaTicketDetailModel.getBigBusTourDataModels().isEmpty()) return;
                List<BigBusOptionsItemModel> tmpList = raynaTicketDetailModel.getBigBusTourDataModels().get(0).getOptions();

                List<BigBusOptionsItemModel> deepCopyList = tmpList.stream()
                        .map(CloneUtils::cloneObject)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

                RaynaTicketManager.shared.bigBusTicketTourOption.clear();
                RaynaTicketManager.shared.bigBusTicketTourOption.addAll(deepCopyList);

                startActivity(new Intent(activity, BigBusTourOptionActivity.class));

            } else if (raynaTicketDetailModel.getBookingType().equals("travel-desk")) {

                List<TravelDeskTourDataModel> originalList = raynaTicketDetailModel.getTravelDeskTourDataModelList();

                if (originalList == null) return;
                if (originalList.isEmpty()) return;

                List<TravelDeskOptionDataModel> optionDataModel = raynaTicketDetailModel.getTravelDeskTourDataModelList().get(0).getOptionDataModel();

                if (optionDataModel == null) return;
                if (optionDataModel.isEmpty()) return;

                List<TravelDeskOptionDataModel> deepCopyList = optionDataModel.stream().map(CloneUtils::cloneObject).filter(Objects::nonNull).collect(Collectors.toList());

                RaynaTicketManager.shared.travelDeskOptionDataModels.clear();
                RaynaTicketManager.shared.travelDeskOptionDataModels.addAll(deepCopyList);

                startActivity(new Intent(activity, TravelDeskTourOptionActivity.class));
                
            } else if (raynaTicketDetailModel.getBookingType().equals("juniper-hotel")) {
                JPTicketManager.shared.clearManager();
                JPTicketManager.shared.activityList.add(activity);
                startActivity(new Intent(activity, JPHotelDatePaxSelectActivity.class));
            } else {
                RaynaTicketManager.shared.object.addProperty(AppConstants.DATE, new SimpleDateFormat("EEE, dd MMM yyyy", Locale.ENGLISH).format(new Date()));
                startActivity(new Intent(activity, RaynaTicketTourOptionActivity.class));
            }
        });

        binding.totalReviewsTv.setOnClickListener( view -> startActivity( new Intent( activity, SeeAllRatingReviewActivity.class )
                .putExtra( "id", raynaTicketDetailModel.getId() )
                .putExtra( "type", "ticket" )
                .putExtra( "isEnableReview", raynaTicketDetailModel.isEnableReview())
                .putExtra( "currentUserRating", new Gson().toJson( raynaTicketDetailModel.getCurrentUserRatingModel() ) ) ) );

        binding.tvSeeAll.setOnClickListener( view -> startActivity( new Intent( activity, SeeAllRatingReviewActivity.class )
                .putExtra( "id", raynaTicketDetailModel.getId() )
                .putExtra( "type", "ticket" )
                .putExtra( "isEnableReview", raynaTicketDetailModel.isEnableReview())
                .putExtra( "currentUserRating", new Gson().toJson( raynaTicketDetailModel.getCurrentUserRatingModel() ) ) ) );

        binding.linearReview.setOnClickListener(v -> {
            Utils.preventDoubleClick(v);
            if (Utils.isGuestLogin()) {
                Graphics.showAlertDialogWithOkCancel(activity, getString(R.string.app_name), getValue("login_required_for_review"), getValue("cancel"), getValue("login"), isConfirmed -> {
                    if (!isConfirmed) {
                        Intent intent = new Intent(this, AuthenticationActivity.class);
                        intent.putExtra("isGuestLogin", true);
                        activityLauncher.launch(intent, result -> {
                            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                                openRatingSheet();
                            }
                        });
                    }
                });
            } else {
                openRatingSheet();
            }
        });

        binding.mapView.setOnClickListener(view -> {
            if (Utils.isWazeInstalled(activity)) {
                showMapSheet();
            } else {
                openGoogleMap();
            }
        });


        binding.raynaGalleryRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager == null || raynaGalleryAdapter == null) return;

                int totalItems = raynaGalleryAdapter.getItemCount();
                if (totalItems == 0) return;

                int centerPosition = getCenterItemPosition(recyclerView);
                int dotSelection = 0;

                if (totalItems >= 3) {
                    if (centerPosition == 0) {
                        dotSelection = 0;
                    } else if (centerPosition == totalItems - 1) {
                        dotSelection = 2;
                    } else {
                        dotSelection = 1;
                    }
                } else if (totalItems == 2) {
                    dotSelection = (centerPosition == 0) ? 0 : 1;
                } else if (totalItems == 1) {
                    dotSelection = 0;
                }

                binding.dotsIndicator.setDotSelection(dotSelection);

             }


            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager == null) return;

                int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
                int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();

                // Manage video playback for visible items
                for (int i = firstVisiblePosition; i <= lastVisiblePosition; i++) {
                    RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(i);
                    if (viewHolder instanceof RaynaTicketDetailGalleryAdapter.RaynaVideoHolder) {
                        View itemView = viewHolder.itemView;
                        if (UiUtils.isView90PercentVisibleHorizontally(recyclerView, itemView)) {
                            ((RaynaTicketDetailGalleryAdapter.RaynaVideoHolder) viewHolder).startVideo();
                        } else {
                            ((RaynaTicketDetailGalleryAdapter.RaynaVideoHolder) viewHolder).pauseVideo();
                        }
                    }
                }
            }

            private int getCenterItemPosition(RecyclerView recyclerView) {
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager == null) return RecyclerView.NO_POSITION;

                int minDistance = Integer.MAX_VALUE;
                int center = recyclerView.getWidth() / 2;
                int centerItemPosition = RecyclerView.NO_POSITION;

                for (int i = layoutManager.findFirstVisibleItemPosition(); i <= layoutManager.findLastVisibleItemPosition(); i++) {
                    View itemView = layoutManager.findViewByPosition(i);
                    if (itemView == null) continue;

                    int itemCenter = (itemView.getLeft() + itemView.getRight()) / 2;
                    int distance = Math.abs(itemCenter - center);

                    if (distance < minDistance) {
                        minDistance = distance;
                        centerItemPosition = i;
                    }
                }

                return centerItemPosition;
            }
        });


        binding.mainConstraint.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            controlVideoPlayback();
            if (scrollY > 30 && !blurViewVisible) {
                showBlurHeader();
            } else if (scrollY <= 30 && blurViewVisible) {
                hideBlurHeader();
            }
        });

        binding.ivShare.setOnClickListener(v -> startActivity(new Intent(activity, VenueShareActivity.class)
                .putExtra("rayna", new Gson().toJson(raynaTicketDetailModel))
                .putExtra("type", "ticket")));


        binding.btnFavorite.setOnClickListener(v -> {
            showProgress(true);
            RaynaTicketManager.shared.requestRaynaTicketFavorite(activity, raynaTicketDetailModel.getId(), (success, error) -> {
                if (success) {
                    showProgress(false);
                    boolean newState = !raynaTicketDetailModel.isIs_favorite();
                    raynaTicketDetailModel.setIs_favorite(newState);
                    if (newState) {
                        LogManager.shared.logTicketEvent(LogManager.LogEventType.addToWishlist, raynaTicketDetailModel.getId(), raynaTicketDetailModel.getTitle(), 0.0, null, "AED");
                    }
                    String title = newState ? getValue("thank_you") : getValue("oh_snap");
                    String message = newState ? setValue("add_favourite",raynaTicketDetailModel.getTitle()) : setValue("remove_favourite",raynaTicketDetailModel.getTitle());
                    Alerter.create(activity)
                            .setTitle(title)
                            .setText(message)
                            .setTitleAppearance(R.style.AlerterTitle)
                            .setTextAppearance(R.style.AlerterText)
                            .setBackgroundColorRes(R.color.white_color)
                            .hideIcon()
                            .show();
                    EventBus.getDefault().postSticky(raynaTicketDetailModel);
                    setFavoriteIcon(activity,raynaTicketDetailModel.isIs_favorite(),binding.ivFavourite);
                } else {
                    showProgress(false);
                    Toast.makeText(activity, error != null ? error : getValue("something_wrong"), Toast.LENGTH_SHORT).show();
                }
            });
        });

        binding.btnMessageAdmin.setOnClickListener(v -> {
            Utils.preventDoubleClick(v);
            UserDetailModel model1 = new UserDetailModel();
            String adminID = BuildConfig.isLive ? "65c0d6ad1ccb8aa07703d3aa" : "67e3ec4d073aaccac53fe908";
            model1.setId(adminID);
            model1.setFirstName("Whosin Admin");
            model1.setImage("https://whosin-bucket.nyc3.digitaloceanspaces.com/file/1721896083557_image-1721896083557.jpg");
            ChatModel chatModel = new ChatModel(model1);
            Intent intent = new Intent(activity, ChatMessageActivity.class);
            intent.putExtra("chatModel", new Gson().toJson(chatModel));
            intent.putExtra("isFromRaynaTicket", true);
            intent.putExtra("ticketChatJSON", getObject());
            startActivity(intent);
        });

    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityRaynaTicketDetailBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        RaynaTicketManager.shared.activityList.remove(activity);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;
        LatLng location = new LatLng(longitude, latitude);

        gMap.getUiSettings().setAllGesturesEnabled(false);
        gMap.getUiSettings().setZoomControlsEnabled(false);
        gMap.getUiSettings().setScrollGesturesEnabled(false);
        gMap.getUiSettings().setRotateGesturesEnabled(false);
        gMap.getUiSettings().setTiltGesturesEnabled(false);
        gMap.getUiSettings().setZoomGesturesEnabled(false);

        gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        String placeName = getAddressFromLatLng(latitude, longitude);
        gMap.addMarker(new MarkerOptions()
                .position(location)
                .title(placeName));

        gMap.setOnMarkerClickListener(marker1 -> true);


        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 17));


        gMap.setOnMapClickListener(latLng -> {
            if (Utils.isWazeInstalled(activity)) {
                showMapSheet();
            } else {
                openGoogleMap();
            }
        });

        binding.tvTicketLocationAdress.setText(placeName);
        binding.tvTicketLocationAdress.setVisibility(!TextUtils.isEmpty(placeName) ? VISIBLE : GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.mapView.onResume();
        if (raynaGalleryAdapter != null) {
            raynaGalleryAdapter.resumeAllVideos();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        binding.mapView.onPause();
        if (raynaGalleryAdapter != null) {
            raynaGalleryAdapter.pauseAllVideos();
        }
    }

    @Override
    protected void onDestroy() { super.onDestroy();
        binding.mapView.onDestroy();
        if (raynaGalleryAdapter != null) {
            raynaGalleryAdapter.releaseAllPlayers();
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        RaynaTicketDetailModel event = EventBus.getDefault().getStickyEvent(RaynaTicketDetailModel.class);
        if (event != null && raynaTicketDetailModel != null && !TextUtils.isEmpty(event.getId()) && !TextUtils.isEmpty(raynaTicketDetailModel.getId())) {
            if (raynaTicketDetailModel.getId().equals(event.getId())) {
                raynaTicketDetailModel.setIs_favorite(event.isIs_favorite());
                setFavoriteIcon(activity,event.isIs_favorite(),binding.ivFavourite);
            }
        }
    }

    @Override
    protected void onStop() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(RaynaTicketDetailModel event) {
        if (event == null) {
            return;
        }
        if (raynaTicketDetailModel != null && !TextUtils.isEmpty(event.getId()) && !TextUtils.isEmpty(raynaTicketDetailModel.getId())) {
            raynaTicketDetailModel.setIs_favorite(event.isIs_favorite());
            setFavoriteIcon(activity,event.isIs_favorite(),binding.ivFavourite);
        }
    }


    @Override
    public void onLowMemory() { super.onLowMemory(); binding.mapView.onLowMemory(); }

    @Override
    protected boolean shouldApplyTopInsetPadding() {
        return false;
    }

    @Override
    protected Map<View, String> getTranslationMap() {
        Map<View, String> map = new HashMap<>();
        map.put(binding.tvGetYourTicketTitle, "get_your_ticket_now");
        map.put(binding.tvContactUsTitle, "contact_us_button");
        map.put(binding.tvTicketLocationTitle, "location");
        map.put(binding.tvSeeAll, "see_all");
        map.put(binding.tvRate, "rating_and_reviews");
        map.put(binding.tvStatingFromTitle, "starting_from");
        map.put(binding.tvFeaturesTitle, "feature");
        map.put(binding.tvWhatIncludesTitle, "whats_included");

        return map;
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    @SuppressLint("SetTextI18n")
    private void setUpData() {
        if (raynaTicketDetailModel == null) return;

        String id = raynaTicketDetailModel.getId();
        String title = raynaTicketDetailModel.getTitle();
        Double price = 0.0;
        try {
            Object amount = raynaTicketDetailModel.getStartingAmount();
            if (amount instanceof Number) {
                price = ((Number) amount).doubleValue();
            } else if (amount instanceof String) {
                price = Double.parseDouble((String) amount);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        loadMap();

        ContactUsBlockManager.setupContactUsBlock(
                activity,
                binding.contactUsBlock,
                raynaTicketDetailModel.getContactUsBlock(),
                ContactUsBlockModel.ContactBlockScreens.DETAILS
        );

        setFavoriteIcon(activity,raynaTicketDetailModel.isIs_favorite(),binding.ivFavourite);

        if (raynaTicketDetailModel != null && raynaTicketDetailModel.getImages() != null && !raynaTicketDetailModel.getImages().isEmpty()){
            if (raynaTicketDetailModel.getImages().size() == 2) binding.dotsIndicator.initDots(2);
            if (raynaTicketDetailModel.getImages().size() >= 3) binding.dotsIndicator.initDots(3);
            if (raynaTicketDetailModel.getImages().size() >= 2) {
                binding.dotsIndicator.setVisibility(VISIBLE);
            } else {
                binding.dotsIndicator.setVisibility(GONE);
            }
            raynaGalleryAdapter.updateData(raynaTicketDetailModel.getImages().stream().map(RatingModel::new).collect(Collectors.toList()));
        }else {
            binding.dotsIndicator.setVisibility(GONE);
        }

        String discount = String.valueOf(raynaTicketDetailModel.getDiscount());

        if (!"0".equals(discount)) {
            binding.tvDiscount.setText(discount.contains("%") ? discount : discount + "%");
            binding.tvDiscount.setVisibility(View.VISIBLE);
        } else {
            binding.tvDiscount.setVisibility(View.GONE);
        }



        float rating = (float) Math.ceil(raynaTicketDetailModel.getAvg_ratings() * 10) / 10;
        binding.rating.setStepSize(0.1f);
        binding.rating.setRating(rating);
        binding.rating.setIsIndicator(true);
        binding.rating.setOnRatingChangeListener(null);
        double truncatedRating = Math.floor(raynaTicketDetailModel.getAvg_ratings() * 10) / 10.0;
        binding.avgRatingsTv.setText(String.format(Locale.ENGLISH, "%.1f", truncatedRating));

        binding.totalReviewsTv.setText(setValue("review_count",String.valueOf(raynaTicketDetailModel.getReviews().size())));

        binding.totalRating.setOnRatingChangeListener(null);
        binding.totalRating.setRating(raynaTicketDetailModel.getCurrentUserRatingModel().getStars());
        if (raynaTicketDetailModel.isEnableRating()) {
            binding.totalRating.setIsIndicator(false);
            binding.totalRating.setOnRatingChangeListener((ratingBar, rating1) -> {
                if (ratingRunnable != null) {
                    ratingHandler.removeCallbacks(ratingRunnable);
                }
                ratingRunnable = () -> reqAddRatings((int) rating1);
                ratingHandler.postDelayed(ratingRunnable, RATING_DELAY);
            });
        } else {
            binding.totalRating.setIsIndicator(true);
        }

        if (raynaTicketDetailModel.getTags() != null && !raynaTicketDetailModel.getTags().isEmpty()){
            List<RatingModel> tags = new ArrayList<>();
            for (String tag : raynaTicketDetailModel.getTags()){
                tags.add(new RatingModel(tag));
            }
            ticketTagsAdapter.updateData(tags);
        }else {
            binding.ticketTagesRecycleView.setVisibility(GONE);
        }


        if (raynaTicketDetailModel.isEnableRating() || raynaTicketDetailModel.isEnableReview() || raynaTicketDetailModel.isReviewVisible()) {
            binding.writeReviewLayout.setVisibility(View.VISIBLE);
            binding.ratingReviewRecycler.setVisibility(View.VISIBLE);
            binding.ratingTitleLayout.setVisibility(View.VISIBLE);
            binding.tvSeeAll.setVisibility(View.VISIBLE);


            if (!raynaTicketDetailModel.isReviewVisible() || raynaTicketDetailModel.getReviews().isEmpty()) {
                binding.ratingReviewRecycler.setVisibility(View.GONE);
                binding.tvSeeAll.setVisibility(View.GONE);
            }

            if (!raynaTicketDetailModel.isEnableReview()) {
                binding.writeReviewLayout.setVisibility(View.GONE);
            }

            if (!raynaTicketDetailModel.isEnableRating()) {
                binding.totalRating.setVisibility(View.GONE);
            }

        } else {
            binding.writeReviewLayout.setVisibility(View.GONE);
            binding.ratingReviewRecycler.setVisibility(View.GONE);
            binding.ratingTitleLayout.setVisibility(View.GONE);
        }

        if (binding.writeReviewLayout.getVisibility() == GONE && binding.ratingReviewRecycler.getVisibility() == GONE) {
            binding.ratingRoundLayout.setVisibility(GONE);
        }

        binding.rating.setVisibility(raynaTicketDetailModel.isEnableRating() ? View.VISIBLE : View.GONE);
        binding.avgRatingsTv.setVisibility(raynaTicketDetailModel.isEnableRating() ? View.VISIBLE : View.GONE);
        binding.totalReviewsTv.setVisibility(raynaTicketDetailModel.isReviewVisible() && !raynaTicketDetailModel.getReviews().isEmpty() ? View.VISIBLE : View.GONE);


        binding.totalReviewsTv.setClickable(raynaTicketDetailModel.isReviewVisible());

        if (raynaTicketDetailModel.getCurrentUserRatingModel() != null && !TextUtils.isEmpty(raynaTicketDetailModel.getCurrentUserRatingModel().getReview())){
            binding.txtReviewTitle.setText(getValue("edit_review"));
        }else {
            binding.txtReviewTitle.setText(getValue("write_review"));
        }

        SpannableStringBuilder tmpString = raynaTicketDetailModel.getDiscountAndStartingAmount(activity,binding.tvDiscountAmount);
        binding.tvAmoutStart.setText(tmpString);

        String startingAmount = raynaTicketDetailModel.getStartingAmount() != null ? String.valueOf(raynaTicketDetailModel.getStartingAmount()) : "N/A";
        if (TextUtils.isEmpty(startingAmount) || startingAmount.equals("0") || startingAmount.equals("N/A")) {
            binding.startingFromLayout.setVisibility(View.GONE);
        } else {
            binding.startingFromLayout.setVisibility(View.VISIBLE);
        }

        binding.tvTickeTitle.setText(raynaTicketDetailModel.getTitle());


        if (raynaTicketDetailModel != null) {
            String description = raynaTicketDetailModel.getDescription();
            String text = description != null ? description.replaceAll("<[^>]*>", "").trim() : "";

            if (!TextUtils.isEmpty(text)) {
                binding.tvTickeDescription.setVisibility(View.VISIBLE);
            } else {
                binding.tvTickeDescription.setVisibility(View.GONE);
            }
        } else {
            binding.tvTickeDescription.setVisibility(View.GONE);
        }


        binding.tvTickeDescription.setText(Html.fromHtml(raynaTicketDetailModel.getDescription(), Html.FROM_HTML_MODE_LEGACY));


        if (rating == 0){
            binding.ratingLayout.setVisibility(GONE);
        }


        if (raynaTicketDetailModel.getTourDataModel() != null && !TextUtils.isEmpty(raynaTicketDetailModel.getTourDataModel().getDuration())){
            binding.ticketDuration.getRoot().setVisibility(View.VISIBLE);
            binding.ticketDuration.tvTitle.setImageDrawable(ContextCompat.getDrawable(activity,R.drawable.icon_timer));
            binding.ticketDuration.tvTitleValue.setText(raynaTicketDetailModel.getTourDataModel().getDuration());
        }else {
            binding.ticketDuration.getRoot().setVisibility(View.GONE);
        }


        if (!TextUtils.isEmpty(raynaTicketDetailModel.getDeparturePoint())) {
            binding.ticketOpratingHours.getRoot().setVisibility(View.VISIBLE);
            binding.ticketOpratingHours.tvTitle.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.icon_event_calender));
            binding.ticketOpratingHours.tvTitleValue.setText(raynaTicketDetailModel.getDeparturePoint());
        } else if (!TextUtils.isEmpty(raynaTicketDetailModel.getDuration())) {
            binding.ticketOpratingHours.getRoot().setVisibility(View.VISIBLE);
            binding.ticketOpratingHours.tvTitle.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.icon_event_calender));
            binding.ticketOpratingHours.tvTitleValue.setText(raynaTicketDetailModel.getDuration());
        } else {
            binding.ticketOpratingHours.getRoot().setVisibility(View.GONE);
        }



        if (raynaTicketDetailModel.getTourDataModel() != null && !TextUtils.isEmpty(raynaTicketDetailModel.getTourDataModel().getCityName())){
            binding.ticketCityName.getRoot().setVisibility(View.VISIBLE);
            binding.ticketCityName.tvTitleValue.setText(raynaTicketDetailModel.getTourDataModel().getCityName());
        }else if (!TextUtils.isEmpty(raynaTicketDetailModel.getCity())){
            binding.ticketCityName.getRoot().setVisibility(View.VISIBLE);
            binding.ticketCityName.tvTitleValue.setText(raynaTicketDetailModel.getCity());
        }else {
            binding.ticketCityName.getRoot().setVisibility(View.GONE);
        }


        if (raynaTicketDetailModel.getTourDataModel() != null &&!TextUtils.isEmpty(raynaTicketDetailModel.getTourDataModel().getCityTourType())){
            binding.ticketTourType.getRoot().setVisibility(View.VISIBLE);
            binding.ticketTourType.tvTitle.setImageDrawable(ContextCompat.getDrawable(activity,R.drawable.icon_pick_up));
            binding.ticketTourType.tvTitleValue.setText(raynaTicketDetailModel.getTourDataModel().getCityTourType());
        }else if (!TextUtils.isEmpty(raynaTicketDetailModel.getCityTourType())){
            binding.ticketTourType.getRoot().setVisibility(View.VISIBLE);
            binding.ticketTourType.tvTitle.setImageDrawable(ContextCompat.getDrawable(activity,R.drawable.icon_pick_up));
            binding.ticketTourType.tvTitleValue.setText(raynaTicketDetailModel.getCityTourType());
        }else {
            binding.ticketTourType.getRoot().setVisibility(View.GONE);
        }



        binding.ticketCancellationPolicy.getRoot().setVisibility(View.GONE);

//        if (!TextUtils.isEmpty(raynaTicketDetailModel.getCancellationPolicy())){
//            binding.ticketCancellationPolicy.getRoot().setVisibility(View.VISIBLE);
//            binding.ticketCancellationPolicy.tvTitle.setText("Cancellation  : ");
//            binding.ticketCancellationPolicy.tvTitleValue.setText(raynaTicketDetailModel.getCancellationPolicy());
//        }else {
//            binding.ticketCancellationPolicy.getRoot().setVisibility(View.GONE);
//        }

        setUpInformation(getValue("overview"),raynaTicketDetailModel.getOverview(),binding.ticketOverViewLayout);
        setUpInformation(getValue("inclusion"),raynaTicketDetailModel.getInclusion(),binding.ticketInclusionViewLayout);
        setUpInformation(getValue("exclusion"),raynaTicketDetailModel.getTourExclusion(),binding.ticketExclusionViewLayout);
        setUpInformation(getValue("important_information"),raynaTicketDetailModel.getImportantInformation(),binding.ticketImportantInfoViewLayout);
        setUpInformation(getValue("useful_information"),raynaTicketDetailModel.getUsefulInformation(),binding.ticketUsefulInfoViewLayout);
        setUpInformation(getValue("faq_details"),raynaTicketDetailModel.getFaqDetails(),binding.ticketFAQDetailsViewLayout);
        setUpInformation(getValue("how_to_redeem"),raynaTicketDetailModel.getHowToRedeem(),binding.ticketHowToRedeemViewLayout);
        setUpInformation(getValue("rayna_advantage"),raynaTicketDetailModel.getRaynaToursAdvantage(),binding.ticketRaynaTourAdvantageViewLayout);

        if (!raynaTicketDetailModel.getFeatures().isEmpty()) {
            binding.ticketFeaturesLayout.setVisibility(View.VISIBLE);
            raynaTicketFeaturesAdapter.updateData(raynaTicketDetailModel.getFeatures());
        } else {
            binding.ticketFeaturesLayout.setVisibility(View.GONE);
        }

        if (!raynaTicketDetailModel.getWhatsInclude().isEmpty()) {
            binding.ticketWhatIncludesLayout.setVisibility(View.VISIBLE);
            raynaTicketWhatsIncludeAdapter.updateData(raynaTicketDetailModel.getWhatsInclude());
        } else {
            binding.ticketWhatIncludesLayout.setVisibility(View.GONE);
        }


//        if (raynaTicketDetailModel.getBookingType().equals("whosin")) {
//            binding.ticketOptionsRecycler.setAdapter(whosinTicketOptionsAdapter);
//        } else if (raynaTicketDetailModel.getBookingType().equals("travel-desk")) {
//            binding.ticketOptionsRecycler.setAdapter(travelDeskTicketOptionsAdapter);
//        } else {
//            binding.ticketOptionsRecycler.setAdapter(raynaTicketOptionsAdapter);
//        }

//        if (raynaTicketDetailModel.getBookingType().equals("whosin")) {
//            if (raynaTicketDetailModel.getOptionData() != null && !raynaTicketDetailModel.getOptionData().isEmpty()) {
//                binding.ticketsOptionsLayout.setVisibility(View.VISIBLE);
//                whosinTicketOptionsAdapter.updateData(raynaTicketDetailModel.getOptionData());
//            } else {
//                binding.ticketsOptionsLayout.setVisibility(View.GONE);
//            }
//        } else if (raynaTicketDetailModel.getBookingType().equals("travel-desk")) {
//            if (raynaTicketDetailModel.getTravelDeskTourDataModelList() != null && !raynaTicketDetailModel.getTravelDeskTourDataModelList().isEmpty()) {
//                binding.ticketsOptionsLayout.setVisibility(View.VISIBLE);
//                if (raynaTicketDetailModel.getTravelDeskTourDataModelList().get(0).getOptionDataModel() != null && !raynaTicketDetailModel.getTravelDeskTourDataModelList().get(0).getOptionDataModel().isEmpty()) {
//                    travelDeskTicketOptionsAdapter.updateData(raynaTicketDetailModel.getTravelDeskTourDataModelList().get(0).getOptionDataModel());
//                } else {
//                    binding.ticketsOptionsLayout.setVisibility(View.GONE);
//                }
//            } else {
//                binding.ticketsOptionsLayout.setVisibility(View.GONE);
//            }
//        } else {
//            if (raynaTicketDetailModel.getTourOptionData() != null && !raynaTicketDetailModel.getTourOptionData().isEmpty()) {
//                binding.ticketsOptionsLayout.setVisibility(View.VISIBLE);
//                raynaTicketOptionsAdapter.updateData(raynaTicketDetailModel.getTourOptionData());
//            } else {
//                binding.ticketsOptionsLayout.setVisibility(View.GONE);
//            }
//        }



        binding.ratingReviewRecycler.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        binding.ratingReviewRecycler.setAdapter(ratingReviewAdapter);
        List<CurrentUserRatingModel> sortedList = raynaTicketDetailModel.getReviews().stream()
                .sorted((r1, r2) -> {
                    String currentUserId = SessionManager.shared.getUser().getId();
                    boolean isR1CurrentUser = r1.getUserId().equals(currentUserId);
                    boolean isR2CurrentUser = r2.getUserId().equals(currentUserId);

                    if (isR1CurrentUser && !isR2CurrentUser) return -1;
                    else if (!isR1CurrentUser && isR2CurrentUser) return 1;
                    else return 0;
                })
                .collect(Collectors.toList());
//        ratingReviewAdapter.updateData(raynaTicketDetailModel.getReviews());
        ratingReviewAdapter.updateData(sortedList);


        Utils.addSeeMore(binding.tvTickeDescription, Html.fromHtml(raynaTicketDetailModel.getDescription()), 3, "..."+ getValue("see_more"), v -> {
            ReadMoreBottomSheet bottomSheet = new ReadMoreBottomSheet();
            bottomSheet.title = getValue("description");
            bottomSheet.formattedDescription = raynaTicketDetailModel.getDescription();
            bottomSheet.show(getSupportFragmentManager(),"");
        });





    }


    public static void applyMapLocale(Context context, String langCode) {
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.setLocale(locale);

        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }


    private void openRatingSheet(){
        WriteReviewActivity bottomSheet = new WriteReviewActivity(raynaTicketDetailModel.getId(), raynaTicketDetailModel.getCurrentUserRatingModel(), "ticket");
        bottomSheet.result = data -> requestTicketDetail(raynaTicketDetailModel.getId());
        bottomSheet.activity = activity;
        bottomSheet.show(getSupportFragmentManager(), "1");
    }

    private void showProgress(boolean isShowLoader){
        binding.ivFavourite.setVisibility(isShowLoader ? View.GONE : View.VISIBLE);
        binding.favTicketProgressBar.setVisibility(isShowLoader ? View.VISIBLE : View.GONE);
    }

    private void setFavoriteIcon(Context context, boolean isFavorite, ImageView imageView) {
        int drawableId = isFavorite ? R.drawable.icon_heart_withfill : R.drawable.icon_heart_withoutfil;
        int tintColor = ContextCompat.getColor(context, isFavorite ? R.color.brand_pink : R.color.white_color);

        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (drawable != null) {
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable, tintColor);
            imageView.setImageDrawable(drawable);
        }
    }

    private void controlVideoPlayback() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) binding.raynaGalleryRecyclerView.getLayoutManager();
        if (layoutManager == null) return;

        int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
        int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();

        for (int i = firstVisiblePosition; i <= lastVisiblePosition; i++) {
            RecyclerView.ViewHolder viewHolder = binding.raynaGalleryRecyclerView.findViewHolderForAdapterPosition(i);
            if (viewHolder instanceof RaynaTicketDetailGalleryAdapter.RaynaVideoHolder) {
                View itemView = viewHolder.itemView;
                if (UiUtils.is90PercentVisible(binding.raynaGalleryRecyclerView, itemView)) {
                    ((RaynaTicketDetailGalleryAdapter.RaynaVideoHolder) viewHolder).startVideo();
                } else {
                    ((RaynaTicketDetailGalleryAdapter.RaynaVideoHolder) viewHolder).pauseVideo();
                }
            }
        }
    }


    private void setupRecyclerView(RecyclerView recyclerView, RaynaTicketFeaturesAdapter<?> adapter) {
        recyclerView.setAdapter(adapter);

        int itemCount = adapter.getItemCount();
        int spanCount = Math.max(Math.min(itemCount, 3), 1);

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(true);

        int spacing = getResources().getDimensionPixelSize(com.intuit.ssp.R.dimen._5ssp);
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(spacing));
    }

    private void setUpInformation(String title, String description, TicketDescriptionsView view){
//        if (!TextUtils.isEmpty(description)) {
//            view.setVisibility(View.VISIBLE);
//            view.setUpData(activity, getSupportFragmentManager(), title, description);
//        } else {
//            view.setVisibility(View.GONE);
//        }
        if (!isMeaningfulHtml(description)) {
            view.setVisibility(View.GONE);
        } else {
            view.setVisibility(View.VISIBLE);
            view.setUpData(activity, getSupportFragmentManager(), title, description);
        }
    }

    private boolean isMeaningfulHtml(String html) {
        if (TextUtils.isEmpty(html)) return false;
        String plainText = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY).toString().trim();
        return !TextUtils.isEmpty(plainText);
    }

    private void loadMap(){
        if (TextUtils.isEmpty(raynaTicketDetailModel.getLatitude())  && TextUtils.isEmpty(raynaTicketDetailModel.getLongitude())){
            binding.ticketMapViewLayout.setVisibility(View.GONE);
            return;
        }
        binding.ticketMapViewLayout.setVisibility(View.VISIBLE);
        latitude = Double.parseDouble(raynaTicketDetailModel.getLatitude());
        longitude = Double.parseDouble(raynaTicketDetailModel.getLongitude());
        binding.mapView.getMapAsync(RaynaTicketDetailActivity.this);
    }

    private String getAddressFromLatLng(double lat, double lng) {
//        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        String lang = Utils.getLang();
        Geocoder geocoder = new Geocoder(this,new Locale(lang));
        try {
            List<Address> addresses = geocoder.getFromLocation(lng, lat, 1);
            if (addresses != null && !addresses.isEmpty()) {
                return addresses.get(0).getAddressLine(0); // Full Address
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String cityName = "";
        String countryName = "";

        if (raynaTicketDetailModel != null && raynaTicketDetailModel.getTourDataModel() != null) {
            TourDataModel tourData = raynaTicketDetailModel.getTourDataModel();
            cityName = !TextUtils.isEmpty(tourData.getCityName()) ? tourData.getCityName() : "";
            countryName = !TextUtils.isEmpty(tourData.getCountryName()) ? tourData.getCountryName() : "";
        }

        if (TextUtils.isEmpty(cityName) && TextUtils.isEmpty(countryName)) {
            return "";
        } else {
            return cityName + " (" + countryName + ")";
        }

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
                    String wazeUri = "https://waze.com/ul?ll=" + raynaTicketDetailModel.getLatLng() + "&navigate=yes&zoom=17&text=" + "&dirflg=d";
                    Intent wazeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(wazeUri));
                    activity.startActivity(wazeIntent);
                    break;
            }
        });
    }

    private void openGoogleMap() {
        String latLong = raynaTicketDetailModel.getLongitude() + "," + raynaTicketDetailModel.getLatitude();

        String geoUri = "geo:" + latLong + "?q=" + latLong;


        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
        intent.setPackage("com.google.android.apps.maps");

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            String webUrl = "https://www.google.com/maps?q=" + latLong;
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(webUrl)));
        }
    }

    private String getObject(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("_id", raynaTicketDetailModel.getId());
        jsonObject.addProperty("title", raynaTicketDetailModel.getTitle());
        jsonObject.addProperty("description", raynaTicketDetailModel.getDescription());
        jsonObject.addProperty("city", raynaTicketDetailModel.getCity());
        Object startingAmount = raynaTicketDetailModel.getStartingAmount();
//                    jsonObject.addProperty("startingAmount", startingAmount != null ? startingAmount.toString() : "");
        if (startingAmount != null) {
            try {
                double amount = Double.parseDouble(startingAmount.toString());
                jsonObject.addProperty("startingAmount", amount);
            } catch (NumberFormatException e) {
                jsonObject.addProperty("startingAmount", 0.0);
            }
        } else {
            jsonObject.addProperty("startingAmount", 0.0);
        }
        jsonObject.add("images", new Gson().toJsonTree(raynaTicketDetailModel.getImages()).getAsJsonArray());
        jsonObject.addProperty("discount",raynaTicketDetailModel.getDiscount());
        return jsonObject.toString();
    }

    private void openDeleteActionSheet(CurrentUserRatingModel model){
        ArrayList<String> data = new ArrayList<>();
        data.add(getValue("delete"));
        Graphics.showActionSheet(activity, activity.getString(R.string.app_name), "Close", data, (data1, position1) -> {
            if (position1 == 0) {
                Graphics.showAlertDialogWithOkCancel(activity, activity.getString(R.string.app_name), getValue("delete_review_confirm"), getValue("yes"), getValue("cancel"), isConfirmed -> {
                    if (isConfirmed) {
                        requestMyReviewDelete(model.getId());
                    }
                });
            }
        });
    }

    private void requestBlockUserAdd(String id,String userFullName) {
        DataService.shared(activity).requestBlockUser(id, new RestCallback<ContainerModel<CommonModel>>(this) {
            @Override
            public void result(ContainerModel<CommonModel> model, String error) {
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }
                Alerter.create(activity).setTitle("Oh Snap!").setText("You have blocked" + userFullName).setTextAppearance(R.style.AlerterText).setTitleAppearance(R.style.AlerterTitle).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                BlockUserManager.addBlockUserId(id);
                requestTicketDetail(raynaTicketDetailModel.getId());
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

                requestTicketDetail(raynaTicketDetailModel.getId());
            }
        });
    }

    private void showBlurHeader() {
        blurViewVisible = true;
        binding.headerViewLayout.setBackgroundColor(ContextCompat.getColor(activity, R.color.card_color));
        int white20 = ContextCompat.getColor(activity, R.color.white_20);
        binding.favTicketblurView.setOverlayColor(white20);
        binding.blurView.setOverlayColor(white20);
        binding.shareTicketblurView.setOverlayColor(white20);
        binding.tvHeaderTicketTitle.setText(raynaTicketDetailModel.getTitle());
        binding.tvHeaderTicketTitle.setVisibility(View.VISIBLE);
    }

    private void hideBlurHeader() {
        blurViewVisible = false;
        binding.headerViewLayout.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
        int black20 = ContextCompat.getColor(activity, R.color.black_20);
        binding.favTicketblurView.setOverlayColor(black20);
        binding.blurView.setOverlayColor(black20);
        binding.shareTicketblurView.setOverlayColor(black20);
        binding.tvHeaderTicketTitle.setVisibility(View.GONE);
    }


    // endregion
    // --------------------------------------
    // region API Calls
    // --------------------------------------

    private void requestTicketDetail(String ticketId) {
        showProgress();
        DataService.shared(activity).requestRaynaCustomUserDetail(ticketId, new RestCallback<ContainerModel<RaynaTicketDetailModel>>(this) {
            @Override
            public void result(ContainerModel<RaynaTicketDetailModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (model.getData() != null) {
                    model.getData().assignTourObject();
                    raynaTicketDetailModel = model.getData();
                    
                    Object amount = raynaTicketDetailModel.getStartingAmount();
                    Double price = 0.0;
                    if (amount != null) {
                        try {
                            price = Double.parseDouble(amount.toString());
                        } catch (NumberFormatException e) {
                            price = 0.0;
                        }
                    }
                    LogManager.shared.logTicketEvent(LogManager.LogEventType.viewTicket, raynaTicketDetailModel.getId(), raynaTicketDetailModel.getTitle(), price, null, "AED");
                    RaynaTicketManager.shared.raynaTicketDetailModel = model.getData();
                    setUpData();
                    Utils.showViews(binding.mainConstraint,binding.btnFavorite,binding.ivShare,binding.getTicketBtnLayout);
                } else {
                    RaynaTicketManager.shared.raynaTicketDetailModel = null;
                    binding.mainConstraint.setVisibility(View.GONE);
                    binding.getTicketBtnLayout.setVisibility(View.GONE);
                }
            }
        });
    }

    private void reqAddRatings(int rating) {

        DataService.shared(activity).requestAddRatings(raynaTicketDetailModel.getId(), rating, "ticket", "", "", new RestCallback<ContainerModel<CurrentUserRatingModel>>(this) {
            @Override
            public void result(ContainerModel<CurrentUserRatingModel> model, String error) {
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, R.string.service_message_something_wrong, Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(activity, model.message, Toast.LENGTH_SHORT).show();
                if (model.getData() != null) {
                    ratingReviewAdapter.getData().stream()
                            .filter(p -> p.getUserId().equals(model.getData().getUserId()))
                            .findFirst()
                            .ifPresent(p -> {
                                p.setStars(model.getData().getStars());
                            });
                    ratingReviewAdapter.notifyDataSetChanged();

                    if (raynaTicketDetailModel.getCurrentUserRatingModel() != null){
                        if (raynaTicketDetailModel.getCurrentUserRatingModel().getUserId().equals(SessionManager.shared.getUser().getId())){
                            raynaTicketDetailModel.getCurrentUserRatingModel().setStars(model.getData().getStars());
                        }
                    }

                }
            }

        });

    }

    // endregion
    // --------------------------------------
    // region Ticket Rating Adapter
    // --------------------------------------

    private class RatingReviewAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = UiUtils.getViewBy(parent, R.layout.item_rating_review_recycler);
            ViewGroup.LayoutParams params = view.getLayoutParams();
            int itemCount = getItemCount();
            if (itemCount > 1) {
                params.width = (int) (Graphics.getScreenWidth(context) * 0.74);
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
            if (model == null) {
                return;
            }
            boolean isLastItem = position == getItemCount() - 1;

            viewHolder.mBinding.linearReply.setVisibility(View.GONE);

            viewHolder.mBinding.txtReview.setText(model.getReview());
            viewHolder.mBinding.txtReview.setMaxLines(3);
            viewHolder.mBinding.txtReview.setEllipsize(TextUtils.TruncateAt.END);
            viewHolder.mBinding.txtReply.setVisibility(View.VISIBLE);
            viewHolder.mBinding.txtReply.setText(model.getReply());

            viewHolder.mBinding.iconMenu.setVisibility(View.VISIBLE);
            viewHolder.mBinding.layoutReview.setBackgroundColor(Color.TRANSPARENT);
            viewHolder.mBinding.tvTitle.setPadding(0, 0, 0, 0);

            if (model.getReply() != null && !model.getReply().trim().isEmpty()) {
                viewHolder.mBinding.replyLinear.setVisibility(View.VISIBLE);
            } else {
                viewHolder.mBinding.replyLinear.setVisibility(GONE);
            }

            viewHolder.mBinding.txtDate.setText(Utils.convertMainDateFormatReview(model.getCreatedAt()));
            viewHolder.mBinding.rating.setRating(model.getStars());

            Optional<UserDetailModel> modelOptional = Optional.empty();
            if (raynaTicketDetailModel != null && raynaTicketDetailModel.getUsers() != null && !raynaTicketDetailModel.getUsers().isEmpty()) {
                modelOptional = raynaTicketDetailModel.getUsers().stream()
                        .filter(p -> p.getId() != null && p.getId().equals(model.getUserId()))
                        .findFirst();
            }
            if (modelOptional.isPresent()) {
                Graphics.loadImageWithFirstLetter(modelOptional.get().getImage(), viewHolder.mBinding.ivRating, modelOptional.get().getFirstName());
                viewHolder.mBinding.txtTitle.setText(modelOptional.get().getFullName());
            }else {
                Graphics.loadImageWithFirstLetter("", viewHolder.mBinding.ivRating, "T");
            }

            if (getItemCount() > 1) {
                if (isLastItem) {
                    int marginBottom = Utils.getMarginRight(holder.itemView.getContext(), 0.04f);
                    Utils.setRightMargin(holder.itemView, marginBottom);
                } else {
                    Utils.setRightMargin(holder.itemView, 0);
                }
            }


            Optional<UserDetailModel> finalModelOptional = modelOptional;
            viewHolder.mBinding.iconMenu.setOnClickListener(v -> {
                if (finalModelOptional.isPresent() && finalModelOptional.get().getId().equals(SessionManager.shared.getUser().getId())){
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
                                   requestTicketDetail(raynaTicketDetailModel.getId());
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
                                    finalModelOptional.ifPresent(listModel -> requestBlockUserAdd(listModel.getId(), viewHolder.mBinding.txtTitle.getText().toString()));
                                }
                            };
                        };
                        reportBottomSheet.show(getSupportFragmentManager(), "");
                    }
                };
                bottomSheet.callback = data -> {
                    if (data) {
                        Graphics.showAlertDialogWithOkCancel(activity, activity.getString(R.string.app_name), setValue("block_user_alert",viewHolder.mBinding.txtTitle.getText().toString()), getValue("yes"), getValue("no"), isConfirmed -> {
                            if (isConfirmed) {
                                finalModelOptional.ifPresent(listModel -> requestBlockUserAdd(listModel.getId(), viewHolder.mBinding.txtTitle.getText().toString()));
                            }
                        });
                    }
                };
                bottomSheet.show(getSupportFragmentManager(), "");
            });

            Optional<UserDetailModel> finalModelOptional1 = modelOptional;
            viewHolder.mBinding.getRoot().setOnClickListener(v -> {
                UserFullReviewSheet reviewSheet = new UserFullReviewSheet();
                finalModelOptional1.ifPresent(userDetailModel -> reviewSheet.userDetailModel = userDetailModel);
                reviewSheet.currentUserRatingModel = model;
                reviewSheet.callback = data -> {
                  if (data) requestTicketDetail(raynaTicketDetailModel.getId());
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

    // endregion
    // --------------------------------------
}
