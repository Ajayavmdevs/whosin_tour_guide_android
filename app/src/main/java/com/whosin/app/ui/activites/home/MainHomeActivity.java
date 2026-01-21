package com.whosin.app.ui.activites.home;

import static com.whosin.app.comman.AppConstants.TabOption.valueOf;
import static com.whosin.app.comman.Graphics.context;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.google.android.material.tabs.TabLayout;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.onesignal.Continue;
import com.onesignal.OneSignal;
import com.onesignal.notifications.INotification;
import com.whosin.app.R;
import com.whosin.app.comman.AppConstants;
import com.whosin.app.comman.AppDelegate;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Preferences;
import com.whosin.app.comman.Utils;
import com.whosin.app.databinding.ActivityMainHomeBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.Repository.ChatRepository;
import com.whosin.app.service.manager.ActivityTrackerManager;
import com.whosin.app.service.manager.AdManger;
import com.whosin.app.service.manager.AppSettingManager;
import com.whosin.app.service.manager.BlockUserManager;
import com.whosin.app.service.manager.ChatManager;
import com.whosin.app.service.manager.CheckUserSession;
import com.whosin.app.service.manager.ComplementaryProfileManager;
import com.whosin.app.service.manager.ContactManager;
import com.whosin.app.service.manager.DialogManager;
import com.whosin.app.service.manager.GetNotificationManager;
import com.whosin.app.service.manager.LocationManager;
import com.whosin.app.service.manager.PromoterProfileManager;
import com.whosin.app.service.manager.PromotionalBannerManager;
import com.whosin.app.service.manager.RaynaTicketManager;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.AppSettingModel;
import com.whosin.app.service.models.ChatMessageModel;
import com.whosin.app.service.models.ChatModel;
import com.whosin.app.service.models.ComplimentaryProfileModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.InAppListUserModel;
import com.whosin.app.service.models.InAppNotificationModel;
import com.whosin.app.service.models.LoginRequestModel;
import com.whosin.app.service.models.MyWalletModel;
import com.whosin.app.service.models.NotificationModel;
import com.whosin.app.service.models.PromoterCirclesModel;
import com.whosin.app.service.models.PromoterEventModel;
import com.whosin.app.service.models.UpdateStatusModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.models.myCartModels.MyCartMainModel;
import com.whosin.app.service.models.rayna.RaynaCheckReviewModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.Notification.NotificaionActivity;
import com.whosin.app.ui.activites.Profile.OtherUserProfileActivity;
import com.whosin.app.ui.activites.Promoter.ComplementaryEventDetailActivity;
import com.whosin.app.ui.activites.Promoter.PromoterActivity;
import com.whosin.app.ui.activites.Promoter.PromoterMyProfile;
import com.whosin.app.ui.activites.auth.AuthenticationActivity;
import com.whosin.app.ui.activites.auth.TwoFactorAuthActivity;
import com.whosin.app.ui.activites.bucket.MyInvitationActivity;
import com.whosin.app.ui.activites.comman.BaseActivity;
import com.whosin.app.ui.activites.home.Chat.ChatMessageActivity;
import com.whosin.app.ui.activites.home.activity.ActivityListDetail;
import com.whosin.app.ui.activites.home.event.EventDetailsActivity;
import com.whosin.app.ui.activites.offers.OfferDetailActivity;
import com.whosin.app.ui.activites.offers.OfferDetailBottomSheet;
import com.whosin.app.ui.activites.offers.VoucherDetailScreenActivity;
import com.whosin.app.ui.activites.raynaTicket.RaynaTicketDetailActivity;
import com.whosin.app.ui.activites.venue.Bucket.BucketListDetailActivity;
import com.whosin.app.ui.activites.venue.VenueActivity;
import com.whosin.app.ui.activites.wallet.MyWalletActivity;
import com.whosin.app.ui.activites.wallet.WalletActivity;
import com.whosin.app.ui.controller.MiniVideoView;
import com.whosin.app.ui.fragment.ChatFragment;
import com.whosin.app.ui.fragment.HomeFragment;
import com.whosin.app.ui.fragment.InAppNotification.InAppNotificationDialog;
import com.whosin.app.ui.fragment.NewExploreFragment;
import com.whosin.app.ui.fragment.Profile.UserProfileFragment;
import com.whosin.app.ui.fragment.TicketReview.RaynaTicketReviewDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;


public class MainHomeActivity extends BaseActivity {
    private ActivityMainHomeBinding binding;

    private String[] REQUIRED_PERMISSIONS = {

    };

    private int currentPermissionIndex = 0;
    private static final int PERMISSION_REQUEST_CODE = 100;

    private AppUpdateManager appUpdateManager;
    private boolean isNotificationClicked = false;
    private List<InAppNotificationModel> inAppNotificationList = new ArrayList<>();
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable snapRunnable = () -> snapToNearestCorner(binding.miniPlayerContainer, binding.getRoot());
    private long lastGestureTime;

    // --------------------------------------
    // region Life Cycle
    // --------------------------------------

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        saveIntentData(intent);
        handleDeepLink();
    }

    @Override
    protected void initUi() {

        AppDelegate.activity = this;
        Graphics.activity = this;
        Graphics.fragmentManager = this.getSupportFragmentManager();

        OneSignal.getNotifications().requestPermission(false, Continue.none());


        OneSignal.getNotifications().addClickListener(event -> {
            isNotificationClicked = true;
            INotification notification = event.getNotification();
            JSONObject additionalData = notification.getAdditionalData();
            if (additionalData != null){
                JsonObject gsonObject = JsonParser.parseString(additionalData.toString()).getAsJsonObject();
                handleNotification(gsonObject,true);
            }
        });

        OneSignal.getNotifications().addForegroundLifecycleListener(iNotificationWillDisplayEvent -> {
            INotification notification = iNotificationWillDisplayEvent.getNotification();
            JSONObject additionalData = notification.getAdditionalData();
            if (additionalData != null){
                JsonObject gsonObject = JsonParser.parseString(additionalData.toString()).getAsJsonObject();
                handleNotification(gsonObject,false);
            }
        });

        appUpdateManager = AppUpdateManagerFactory.create(this);



        new Handler(Looper.getMainLooper()).postDelayed(() -> CheckUserSession.checkSessionAndProceed(activity, () -> {
            requestInAppListUser();
            requestCheckRaynaReview();
            requestMyCartList();
        }), 1000);


        // Register listener to listen for state updates
        appUpdateManager.registerListener(installStateUpdatedListener);
        saveIntentData(getIntent());
        if (SessionManager.shared.getToken().isEmpty()) {
            startActivity(new Intent(this, AuthenticationActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        } else {
            handleDeepLink();
        }

        setupBottomTab();
        EventBus.getDefault().register(this);
        AppSettingManager.shared.setContext(activity);
        BlockUserManager.shared.requestBlockUserList(activity);
        PromotionalBannerManager.shared.requestPromotionalBanner(activity);
        ChatManager.shared.connect();
        ChatManager.shared.syncChatMessages(SessionManager.shared.getUser().isPromoter());
        LocationManager.shared.requestLocation(activity);
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
//            ContactManager.shared.requestContact(activity);
//        }
        getOneSignalPlayerId();
        Preferences.shared.setBoolean("isSubscriptionDialogShown", false);


        new Handler().postDelayed(this::checkAuthRequest, 500);

        new Handler().postDelayed(() -> {
            if (!checkAllPermissionsGranted()) {
                requestNextPermission();
            }
        }, 1000);

        runOnUiThread(() -> ComplementaryProfileManager.shared.complimentaryProfileModel = SessionManager.shared.getCmUserProfile());

        if (SessionManager.shared.getUser().isRingMember()) {
            binding.confiremedEventView.setVisibility(View.VISIBLE);
            binding.confiremedEventView.setUpData(activity);
        } else {
            binding.confiremedEventView.setVisibility(View.GONE);
        }

        ComplementaryProfileManager.shared.setProfileCallBack = data -> {
            if (data) {
                ImageView imgView = new ImageView(MainHomeActivity.this);
                imgView.setImageResource(R.drawable.icon_profile_fill);
                imgView.setPadding(0, 15, 0, 15);
                binding.tabLayout.getTabAt(2).setCustomView(imgView);
                binding.tabLayout.getTabAt(2).select();
                binding.viewPager.setCurrentItem(2);
            }
        };

        PromoterProfileManager.shared.setProfileCallBack = data -> {
            if (data) {
                binding.bottomAppBar.setVisibility(View.VISIBLE);
                ImageView imgView = new ImageView(MainHomeActivity.this);
                imgView.setImageResource(R.drawable.icon_profile_without_fill);
                imgView.setPadding(0, 15, 0, 15);
                binding.tabLayout.getTabAt(2).setCustomView(imgView);
                binding.tabLayout.getTabAt(0).select();
                binding.viewPager.setCurrentItem(0);
            }
        };

        RaynaTicketManager.shared.walletRedirectCallBack = data -> {
          if (data) {
              if (binding.tabLayout.getTabCount() > 0) {
                  binding.tabLayout.selectTab(binding.tabLayout.getTabAt(binding.tabLayout.getTabCount() - 1));
              }
          }
        };

        if (SessionManager.shared.getUser().isPromoter()){
            PromoterProfileManager.shared.requestPromoterVenues(this);
            PromoterProfileManager.shared.requestSubAdminList(this);
        }


        AdManger.shared.requestAdList(activity, (data, position) -> {
            if (position == 1 && data != null && !data.isEmpty()){
                binding.miniVideoView.setVisibility(View.VISIBLE);
                binding.miniPlayerContainer.setVisibility(View.VISIBLE);
                binding.miniVideoView.setupData(data,binding.getRoot(),activity);
                binding.miniVideoView.onItemVisibilityChanged(true);
//                binding.miniVideoView.setOnMiniPlayerGestureListener((deltaX, deltaY) -> {
//                    View mini = binding.miniPlayerContainer;
//
//                    float newX = mini.getX() + deltaX;
//                    float newY = mini.getY() + deltaY;
//
//                    // Boundaries (optional)
//                    int maxX = binding.getRoot().getWidth() - mini.getWidth();
//                    int maxY = binding.getRoot().getHeight() - mini.getHeight();
//
//                    newX = Math.max(0, Math.min(newX, maxX));
//                    newY = Math.max(0, Math.min(newY, maxY));
//
//                    mini.setX(newX);
//                    mini.setY(newY);
//                });
                binding.miniVideoView.setOnMiniPlayerGestureListener((deltaX, deltaY) -> {
                    View mini = binding.miniPlayerContainer;

                    float newX = mini.getX() + deltaX;
                    float newY = mini.getY() + deltaY;

                    // Boundaries
                    int maxX = binding.getRoot().getWidth() - mini.getWidth();
                    int maxY = binding.getRoot().getHeight() - mini.getHeight();

                    newX = Math.max(0, Math.min(newX, maxX));
                    newY = Math.max(0, Math.min(newY, maxY));

                    mini.setX(newX);
                    mini.setY(newY);

                    // Reset snap timer
                    handler.removeCallbacks(snapRunnable);
                    lastGestureTime = System.currentTimeMillis();
                    handler.postDelayed(() -> {
                        if (System.currentTimeMillis() - lastGestureTime >= 50) {
                            snapToNearestCorner(binding.miniPlayerContainer, binding.getRoot());
                        }
                    }, 50);
                });
                binding.miniVideoView.closeCallBack = data1 -> {
                    if (data1) binding.miniVideoView.setVisibility(View.GONE);
                    if (data1) binding.miniPlayerContainer.setVisibility(View.GONE);
                };
            }else {
                binding.miniVideoView.setVisibility(View.GONE);
                binding.miniPlayerContainer.setVisibility(View.GONE);
            }
        });

        RaynaTicketManager.shared.cartReloadCallBack = data -> {
            if (data){
                requestMyCartList();
            }
        };

    }

    @Override
    protected void setListeners() {

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getId() == 5) {

                } else {
                    tab.setIcon(getTabBarIconForId(valueOf(tab.getId()), true));
                    binding.viewPager.setCurrentItem(tab.getId(), false);
                }
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tab.view.getChildAt(0).getLayoutParams();
                params.bottomMargin = 0;
                tab.view.getChildAt(0).setLayoutParams(params);
                if (tab.getId() == 3) {
                    GetNotificationManager.shared.requestUpdatesCount("wallet");
                }

                if (SessionManager.shared.getUser().isRingMember()){
                    if (tab.getId() == 0) {
                        binding.confiremedEventView.setVisibility(View.VISIBLE);
                    } else {
                        binding.confiremedEventView.setVisibility(View.GONE);
                    }
                }


                ImageView imgView = new ImageView(MainHomeActivity.this);
                imgView.setImageResource(tab.getId() == 2 ? R.drawable.icon_profile_fill : R.drawable.icon_profile_without_fill);
                imgView.setPadding(0, 15, 0, 15);
                binding.tabLayout.getTabAt(2).setCustomView(imgView);

                if (tab.getId() == 2){
                    if (SessionManager.shared.getUser().isPromoter()){
                        binding.bottomAppBar.setVisibility(View.GONE);
                    }else {
                        binding.bottomAppBar.setVisibility(View.VISIBLE);
                    }
                }
                
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (tab.getId() != 5) {
                    tab.setIcon(getTabBarIconForId(valueOf(tab.getId()), false));
                }
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tab.view.getChildAt(0).getLayoutParams();
                params.bottomMargin = 0;
                tab.view.getChildAt(0).setLayoutParams(params);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityMainHomeBinding.inflate(getLayoutInflater());
        binding.viewPager.setAdapter(new ViewPagerAdapter(this));
        binding.viewPager.setUserInputEnabled(false);
        binding.viewPager.setOffscreenPageLimit(1);

        if (SessionManager.shared.getUser().isRingMember()) {
//            binding.mainImg.setForeground(getResources().getDrawable(R.drawable.center_icon_slected, null));
        }

        return binding.getRoot();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setChatCount();
        setWalletCount();
//        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
//            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
//                // An update is available, prompt the user to start the update flow
//                try {
//                    appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.FLEXIBLE, this, 103);
//                } catch (IntentSender.SendIntentException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).addOnFailureListener(e -> {
//            e.printStackTrace();
//        });

        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                boolean forceUpdate = false;
                if (AppSettingManager.shared.getAppSettingData() != null) {
                    forceUpdate = AppSettingManager.shared.getAppSettingData().isForceUpdate();
                }
                int updateType = forceUpdate ? AppUpdateType.IMMEDIATE : AppUpdateType.FLEXIBLE;

                if (appUpdateInfo.isUpdateTypeAllowed(updateType)) {
                    try {
                        appUpdateManager.startUpdateFlowForResult(
                                appUpdateInfo,
                                updateType,
                                this,
                                103
                        );
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).addOnFailureListener(e -> {
            e.printStackTrace();
        });
    }

    @Override
    public void onBackPressed() {
        if (isTaskRoot() && !isFinishing()) {
//            Toast.makeText(this, "App is being closed by back button", Toast.LENGTH_SHORT).show();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 103) {
            if (resultCode != RESULT_OK) {
                Toast.makeText(this, "Update is required to continue using the app.", Toast.LENGTH_SHORT).show();
                if (AppUpdateType.IMMEDIATE == AppUpdateType.IMMEDIATE) {
                    finish(); // For immediate updates, close the app if the user cancels the update
                }
            }
        }

        if (requestCode == 1005){
            if (!inAppNotificationList.isEmpty()) {
                InAppNotificationModel currentNotification = inAppNotificationList.get(0);
                String eventJson = new Gson().toJson(currentNotification);
                Intent intent = new Intent(getApplicationContext(), InAppNotificationDialog.class);
                intent.putExtra("eventModelJson", eventJson);
                intent.putExtra("isFromHome", true);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }
    }


    //  endregion
    // --------------------------------------
    // region data/service
    // --------------------------------------

    private void requestUpdateDeviceToken(String playerID) {
        if (!AppSettingManager.shared.callHomeCommanApi) return;
        if (TextUtils.isEmpty(SessionManager.shared.getToken())) {
            return;
        }
        if (TextUtils.isEmpty(playerID)) {
            return;
        }
        OneSignal.login(SessionManager.shared.getUser().getId());
        Log.d("Onesignal", "requestUpdateDeviceToken: " + playerID);
        Log.d("Onesignal", "token: " + SessionManager.shared.getToken());
        String deviceId = Utils.getDeviceUniqueId(this);
        DataService.shared(this).requestUpdateFcmToken(playerID, deviceId, new RestCallback<ContainerModel<UserDetailModel>>(this) {
            @Override
            public void result(ContainerModel<UserDetailModel> model, String error) {

            }

        });


    }

    private void requestCheckRaynaReview() {
        if (!AppSettingManager.shared.callHomeCommanApi) return;
        DataService.shared(this).requestRaynaCheckReview(new RestCallback<ContainerModel<RaynaCheckReviewModel>>(this) {
            @Override
            public void result(ContainerModel<RaynaCheckReviewModel> model, String error) {
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    return;
                }

                if (model.data != null && model.getData() != null){
                    if (model.getData().getReviewStatus().equals("pending")){
                        RaynaTicketReviewDialog bottomSheet = new RaynaTicketReviewDialog();
                        bottomSheet.ticketID = model.data.getCustomTicketId();
                        bottomSheet.ticketName = model.getData().getTicketName();
                        bottomSheet.type = "ticket";
                        bottomSheet.activity = activity;
                        bottomSheet.show(getSupportFragmentManager(), "");
                        AppSettingManager.shared.isAlreadyOpenReviewSheet = true;
                    }
                }
            }
        });
    }

    private void requestInAppListUser() {
        if (!AppSettingManager.shared.callHomeCommanApi) return;
        DataService.shared(activity).requestInAppListUser(1, new RestCallback<ContainerModel<InAppListUserModel>>(this) {
            @Override
            public void result(ContainerModel<InAppListUserModel> model, String error) {
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    return;
                }

                if (model.getData() != null && model.getData().getList() != null && !model.getData().getList().isEmpty()) {
                    inAppNotificationList = model.getData().getList().stream().filter(p -> !p.isReadStatus()).collect(Collectors.toList());
                    if (!inAppNotificationList.isEmpty()) {
                        if (isNotificationClicked) return;
                        InAppNotificationModel currentNotification = inAppNotificationList.get(0);
                        String eventJson = new Gson().toJson(currentNotification);
                        Intent intent = new Intent(activity, InAppNotificationDialog.class);
                        intent.putExtra("eventModelJson", eventJson);
                        intent.putExtra("isFromHome", true);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }

                }
            }
        });
    }

    private void requestMyCartList() {
        if (!AppSettingManager.shared.callHomeCommanApi) return;
        DataService.shared(activity).requestMyCartList(new RestCallback<ContainerModel<MyCartMainModel>>(this) {
            @Override
            public void result(ContainerModel<MyCartMainModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    return;
                }

                if (model.getData() != null) {
                    SessionManager.shared.saveTicketCartData(model.getData());
                }
            }
        });
    }


    //  endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void snapToNearestCorner(View mini, View parent) {
        int maxX = parent.getWidth() - mini.getWidth();
        int maxY = parent.getHeight() - mini.getHeight();

        float currentX = mini.getX();
        float currentY = mini.getY();

        // Convert 16 SDP to pixels
        int bottomMargin = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                56, // SDP value
                mini.getContext().getResources().getDisplayMetrics()
        );

        // Define the four corners (x, y) with margin for bottom corners
        float[][] corners = {
                {0, 0},                       // Top-left
                {maxX, 0},                    // Top-right
                {0, maxY - bottomMargin},     // Bottom-left with margin
                {maxX, maxY - bottomMargin}   // Bottom-right with margin
        };

        // Find the nearest corner
        float minDistance = Float.MAX_VALUE;
        float[] nearestCorner = corners[0];

        for (float[] corner : corners) {
            float distance = (float) Math.sqrt(
                    Math.pow(currentX - corner[0], 2) + Math.pow(currentY - corner[1], 2)
            );
            if (distance < minDistance) {
                minDistance = distance;
                nearestCorner = corner;
            }
        }

        // Set position instantly (no animation)
        mini.setX(nearestCorner[0]);
        mini.setY(nearestCorner[1]);
    }


    private void saveIntentData(Intent intent) {
        if (intent == null) { return; }
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            Uri data = intent.getData();
            if (data != null) {
                Preferences.shared.removeKey("venueId_deeplink");
                Preferences.shared.removeKey("userId_deeplink");
                Preferences.shared.removeKey("offerId_deeplink");
                Preferences.shared.removeKey("raynaTicket_deeplink");
                Preferences.shared.removeKey("raynaTicketDetail_deeplink");
                List<String> paths = data.getPathSegments();
                String path = data.getLastPathSegment();
                if (paths.contains("v")) {
                    Preferences.shared.setString("venueId_deeplink", path);
                }
                else if (paths.contains("u")) {
                    Preferences.shared.setString("userId_deeplink", path);
                }
                else if (paths.contains("o")) {
                    Preferences.shared.setString("offerId_deeplink", path);
                }else if (paths.contains("p")) {
                    Preferences.shared.setString("promoterEventId_deeplink", path);
                }else if (paths.contains("t")) {
                    Preferences.shared.setString("raynaTicketDetail_deeplink", path);
                } else if (paths.contains("invoice")) {
                    Preferences.shared.setString("raynaTicket_deeplink", path);
                }
            }
        }
    }

    private void handleDeepLink() {
        if (Preferences.shared.isExist("venueId_deeplink")) {
            String venueId = Preferences.shared.getString("venueId_deeplink");
            if (!TextUtils.isEmpty(venueId)) {
                new Handler().postDelayed(() -> {
                    Preferences.shared.setString("venueId_deeplink", "");
                    Preferences.shared.removeKey("venueId_deeplink");
                    startActivity(new Intent(this, VenueActivity.class).putExtra("venueId", venueId));
                }, 500);
            }
        } else if (Preferences.shared.isExist("userId_deeplink")) {
            String userId = Preferences.shared.getString("userId_deeplink");
            if (!TextUtils.isEmpty(userId)) {
                new Handler().postDelayed(() -> {
                    Preferences.shared.setString("userId_deeplink", "");
                    Preferences.shared.removeKey("userId_deeplink");
                    startActivity(new Intent(activity, OtherUserProfileActivity.class).putExtra("friendId", userId));
                }, 500);
            }
        } else if (Preferences.shared.isExist("offerId_deeplink")) {
            String offerId = Preferences.shared.getString("offerId_deeplink");
            if (!TextUtils.isEmpty(offerId)) {
                new Handler().postDelayed(() -> {
                    Preferences.shared.setString("offerId_deeplink", "");
                    Preferences.shared.removeKey("offerId_deeplink");
                    OfferDetailBottomSheet dialog = new OfferDetailBottomSheet();
                    dialog.offerId = offerId;
                    dialog.show( getSupportFragmentManager(), "" );
                }, 500);
            }
        }else if (Preferences.shared.isExist("promoterEventId_deeplink")) {
            String userId = Preferences.shared.getString("promoterEventId_deeplink");
            if (!TextUtils.isEmpty(userId)) {
                if (SessionManager.shared.getUser().isRingMember() || SessionManager.shared.getUser().isPromoter()){
                    new Handler().postDelayed(() -> {
                        Preferences.shared.setString("promoterEventId_deeplink", "");
                        Preferences.shared.removeKey("promoterEventId_deeplink");
                        String type  = SessionManager.shared.getUser().isPromoter() ? "Promoter" : "complementary";
                        startActivity(new Intent(activity, ComplementaryEventDetailActivity.class)
                                .putExtra("type", type)
                                .putExtra("eventId", userId));
                    }, 500);
                }

            }
        }else if (Preferences.shared.isExist("raynaTicket_deeplink")) {
            String ticketID = Preferences.shared.getString("raynaTicket_deeplink");
            if (!TextUtils.isEmpty(ticketID)){
                new Handler().postDelayed(() -> {
                    Preferences.shared.setString("raynaTicket_deeplink", "");
                    Preferences.shared.removeKey("raynaTicket_deeplink");
                    if (binding.tabLayout.getTabCount() > 0) {
                        binding.tabLayout.selectTab(binding.tabLayout.getTabAt(binding.tabLayout.getTabCount() - 1));
                    }
                }, 500);
            }

        } else if (Preferences.shared.isExist("raynaTicketDetail_deeplink")) {
            String ticketId = Preferences.shared.getString("raynaTicketDetail_deeplink");
            if (!TextUtils.isEmpty(ticketId)) {
                new Handler().postDelayed(() -> {
                    Preferences.shared.setString("raynaTicketDetail_deeplink", "");
                    Preferences.shared.removeKey("raynaTicketDetail_deeplink");
                    startActivity(new Intent(activity, RaynaTicketDetailActivity.class).putExtra("ticketId",ticketId));
                }, 500);
            }
        }
    }

    private void checkAuthRequest() {
        AppSettingModel data = AppSettingManager.shared.getAppSettingData();
        if (!data.getLoginRequests().isEmpty()) {
            LoginRequestModel loginRequest = data.getLoginRequests().get(0);
            if (!loginRequest.getMetadata().getDeviceId().equals(Utils.getDeviceUniqueId(this))) {
                Intent intent = new Intent(this, TwoFactorAuthActivity.class);
                intent.putExtra("metadata", new Gson().toJson(data.getLoginRequests().get(0)));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
    }

    private void getOneSignalPlayerId() {

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            String id = null;
            OneSignal.getUser();
            OneSignal.getUser().getOnesignalId();
            id = OneSignal.getUser().getOnesignalId();
            requestUpdateDeviceToken(id);
        }, 3500);


//        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
//        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
//            if (!task.isSuccessful()) {
//                return;
//            }
//            String token = task.getResult();
//            requestUpdateDeviceToken(token);
//            subscribeToTopic("sendtoall");
//            subscribeToTopic("sendtoandroid");
//        });
    }

    private void subscribeToTopic(String topic) {
        FirebaseMessaging.getInstance().subscribeToTopic(topic).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("FCM", "Subscribed to " + topic);
            } else {
                Log.e("FCM", "Subscription to " + topic + " failed", task.getException());
            }
        });
    }

    private void setupBottomTab() {

        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(getValue("home")).setIcon(getTabBarIconForId(valueOf(0), true)).setId(0));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(getValue("chat")).setIcon(getTabBarIconForId(valueOf(1), false)).setId(1));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("  ").setIcon(getTabBarIconForId(valueOf(2), false)).setId(2));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(getValue("explore")).setIcon(getTabBarIconForId(valueOf(3), false)).setId(3));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(getValue("home_tab_wallet")).setIcon(getTabBarIconForId(valueOf(4), false)).setId(4));

        for (int i = 0; i < binding.tabLayout.getTabCount(); i++) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) binding.tabLayout.getTabAt(i).view.getChildAt(0).getLayoutParams();
            params.bottomMargin = 0;
            binding.tabLayout.getTabAt(i).view.getChildAt(0).setLayoutParams(params);

            if (i == 2) {
                ImageView imgView= new ImageView(this);
                imgView.setImageResource(R.drawable.icon_profile_without_fill);
                imgView.setPadding(0,15,0,15);
                binding.tabLayout.getTabAt(2).setCustomView(imgView);
            }
        }
    }

    private void setChatCount() {
        long count = ChatRepository.shared(Graphics.context).getAllUnrealMessageCount();
        binding.tabLayout.getTabAt(1).getOrCreateBadge().setNumber((int) count);
        binding.tabLayout.getTabAt(1).getBadge().setBadgeTextColor(getColor(R.color.white));
        binding.tabLayout.getTabAt(1).getBadge().setBackgroundColor(getColor(R.color.red));
        binding.tabLayout.getTabAt(1).getBadge().setVisible(count != 0);
    }

    private void setWalletCount() {
        UpdateStatusModel statusModel = GetNotificationManager.shared.statusModel;
        if (statusModel != null) {
            binding.tabLayout.getTabAt(4).getOrCreateBadge().clearNumber();
            binding.tabLayout.getTabAt(4).getBadge().setBackgroundColor(getColor(R.color.red));
            binding.tabLayout.getTabAt(4).getBadge().setVisible(statusModel.isWallet());

//            if (statusModel.isEvent() || statusModel.isBucket() || statusModel.isOuting()) {
//                binding.indicatorView.setVisibility(View.VISIBLE);
//            } else {
//                binding.indicatorView.setVisibility(View.GONE);
//            }
        }
    }

    @DrawableRes
    private int getTabBarIconForId(AppConstants.TabOption option, boolean isSelected) {
        switch (option) {
            case Home:
                return isSelected ? R.drawable.icon_tab_home_fill : R.drawable.icon_tab_home_line;
            case Chat:
                return isSelected ? R.drawable.icon_tab_chat_fill : R.drawable.icon_tab_chat_line;
            case Profile:
                return isSelected ? R.drawable.icon_profile_fill : R.drawable.icon_profile_without_fill;
            case Explore:
                return isSelected ? R.drawable.icon_tab_explore_fill : R.drawable.icon_tab_explore_line;
            case Wallet:
                return isSelected ? R.drawable.icon_tab_wallet_fill : R.drawable.icon_tab_wallet_line;
        }
        return isSelected ? R.drawable.icon_tab_home_fill : R.drawable.icon_tab_home_line;
    }

    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New Version Available");
        builder.setMessage("A new version of the app is available. Please update to continue using the app.");
        builder.setPositiveButton("Update", (dialog, which) -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.whosin.me"));
            startActivity(intent);
        });
        builder.setCancelable(false);
        builder.show();
    }

    private InstallStateUpdatedListener installStateUpdatedListener = state -> {
        if (state.installStatus() == InstallStatus.DOWNLOADED) {

        }
    };


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String event) {
        setChatCount();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UpdateStatusModel event) {
        setWalletCount();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ComplimentaryProfileModel event) {
        binding.confiremedEventView.setUpData(activity);
        SessionManager.shared.getCurrentUserProfile((success, error) ->{
            if (success){
//                binding.mainImg.setForeground(getResources().getDrawable(R.drawable.center_icon_slected, null));

            }
        });

    }

    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    private class ViewPagerAdapter extends FragmentStateAdapter {
        public ViewPagerAdapter(FragmentActivity fa) {
            super(fa);
        }

        @Override
        public Fragment createFragment(int position) {
            switch (valueOf(position)) {
                case Home:
                    return new HomeFragment();
                case Chat:
                    return new ChatFragment();
                case Profile:
                    if (SessionManager.shared.getUser().isPromoter()){
                        return new PromoterMyProfile();
                    }else {
                        return new UserProfileFragment();
                    }
                case Explore:
                    return new NewExploreFragment();
//                    return new ExploreFragment();
                default:
                    return new MyWalletActivity();
            }
        }

        @Override
        public int getItemCount() {
            return 5;
        }
    }

    // endregion
    // --------------------------------------

    private void handleNotification() {

        if (Preferences.shared.isExist("push_notification_msg")) {
            String msgData = Preferences.shared.getString("push_notification_msg");
            if (!TextUtils.isEmpty(msgData)) {
                ChatMessageModel model = new Gson().fromJson(msgData, ChatMessageModel.class);
                ChatModel chatModel = new ChatModel();
                chatModel.setChatId(model.getChatId());
                chatModel.setChatType(model.getChatType());
                if (model.getChatType().equals("friend")) {
                    chatModel.setImage(model.getAuthorImage());
                    chatModel.setTitle(model.getAuthorName());
                    chatModel.setMembers(model.getMembers());
                } else if (model.getChatType().equals("bucket")) {
                    chatModel.setMembers(model.getMembers());
                }else if (model.getChatType().equals("promoter_event")) {
                    chatModel.setImage(model.getAuthorImage());
                    chatModel.setTitle(model.getAuthorName());
                    chatModel.setComplementry(true);
                }
                Intent intent = new Intent(this, ChatMessageActivity.class);
                intent.putExtra("chatModel", new Gson().toJson(chatModel));
                intent.putExtra("type", chatModel.getChatType());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                Preferences.shared.removeKey("push_notification_msg");
            }
        }
        else if (Preferences.shared.isExist("push_notification_data")) {
            String msgData = Preferences.shared.getString("push_notification_data");
            if (!TextUtils.isEmpty(msgData)) {
                Preferences.shared.removeKey("push_notification_data");
                JsonObject jsonObject = new Gson().fromJson(msgData, JsonObject.class);
                if (jsonObject != null && jsonObject.has("type") && jsonObject.has("id")) {
                    String type = jsonObject.get("type").getAsString();
                    String id = jsonObject.get("id").getAsString();
                    if (type == null && type.isEmpty()) {
                        return;
                    }
                    if (id == null && id.isEmpty()){
                        return;
                    }
                    if (Objects.equals(type, "bucket")) {
                        Intent intent = new Intent(this, BucketListDetailActivity.class);
                        intent.putExtra("bucketId", id);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else if (Objects.equals(type, "event")) {
                        Intent intent = new Intent(this, EventDetailsActivity.class);
                        intent.putExtra("eventId", id);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else if (Objects.equals(type, "outing")) {
                        Intent intent = new Intent(this, MyInvitationActivity.class);
                        intent.putExtra("id", id);
                        intent.putExtra("notificationType", "notification");
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else if (Objects.equals(type, "activity")) {
                        Intent intent = new Intent(this, ActivityListDetail.class);
                        intent.putExtra("activityId", id);
                        intent.putExtra("notificationType", "notification");
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else if (Objects.equals(type, "deal")) {
                        Intent intent = new Intent(this, VoucherDetailScreenActivity.class);
                        intent.putExtra("id", id);
                        intent.putExtra("notificationType", "notification");
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else if (Objects.equals(type, "offer")) {
                        OfferDetailBottomSheet dialog = new OfferDetailBottomSheet();
                        dialog.offerId = id;
                        dialog.show(getSupportFragmentManager(), "");
                    } else if (Objects.equals(type, "follow")) {
                        Intent intent = new Intent(this, OtherUserProfileActivity.class);
                        intent.putExtra("friendId", id);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else if (Objects.equals(type, "invite")) {
                        Intent intent = new Intent(this, ComplementaryEventDetailActivity.class);
                        intent.putExtra("eventId", id);
                        intent.putExtra("type","Promoter");
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }else if (Objects.equals(type, "promoter-event")) {
                        Intent intent = new Intent(this, ComplementaryEventDetailActivity.class);
                        intent.putExtra("eventId", id);
                        intent.putExtra("type","complementary");
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }else if (Objects.equals(type, "add-to-ring")) {
                        if (SessionManager.shared.getUser().isRingMember()) {
                            Intent intent = new Intent(this, NotificaionActivity.class);
                            intent.putExtra("id", id);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(this, PromoterActivity.class);
                            intent.putExtra("isPromoter", false);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }

                    }else if (Objects.equals(type, "join-my-ring")) {
                        if (SessionManager.shared.getUser().isPromoter()){
//                            Intent intent = new Intent(this, PromoterMyProfile.class);
//                            intent.putExtra("promoterUserId", id);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            startActivity(intent);
                            binding.tabLayout.setVisibility(View.GONE);
                            binding.tabLayout.getTabAt(2).select();
                            binding.viewPager.setCurrentItem(2);
                        }

                    } else if (Objects.equals(type,"add-to-plusone")) {
                        Intent intent = new Intent(this, NotificaionActivity.class);
                        intent.putExtra("isPromoter", false);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else if (Objects.equals(type,"plusone-accepted")) {
                        binding.tabLayout.setVisibility(View.GONE);
                        binding.tabLayout.getTabAt(2).select();
                        binding.viewPager.setCurrentItem(2);
                    }
                    else if (Objects.equals(type, "review-ticket")) {
                        if (!TextUtils.isEmpty(id)) {
                            Activity activity = ActivityTrackerManager.getInstance().getCurrentActivity();
                            if (activity != null && !activity.isFinishing() && !AppSettingManager.shared.isAlreadyOpenReviewSheet) {
                                FragmentManager fragmentManager = ((FragmentActivity) activity).getSupportFragmentManager();
                                RaynaTicketReviewDialog bottomSheet = new RaynaTicketReviewDialog();
                                bottomSheet.ticketID = id;
                                bottomSheet.ticketName = "";
                                bottomSheet.type = "ticket";
                                bottomSheet.activity = activity;
                                bottomSheet.show(fragmentManager, "");
                                AppSettingManager.shared.isAlreadyOpenReviewSheet = true;
                            }
                        }
                    }
                }
            }
        }
    }

    private void requestNextPermission() {
        if (currentPermissionIndex < REQUIRED_PERMISSIONS.length) {
            String permission = REQUIRED_PERMISSIONS[currentPermissionIndex];
            if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
                currentPermissionIndex++;
                requestNextPermission();
                return;
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                // Permission has been denied previously, don't show the dialog again, move to the next permission
                currentPermissionIndex++;
                requestNextPermission();
                return;
            }
            ActivityCompat.requestPermissions(this, new String[]{permission}, PERMISSION_REQUEST_CODE);
        }
    }

    private boolean checkAllPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            currentPermissionIndex++;
            requestNextPermission();
        }
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
//            ContactManager.shared.requestContact(activity);
//        }
    }


    private void handleNotification(JsonObject jsonObject, boolean isClick) {
        if (isClick) {
            isNotificationClicked = true;
            handleNotificationClick(jsonObject);
        } else {
            handleForegroundNotification(jsonObject);
        }
    }

    private void handleForegroundNotification(JsonObject jsonObject) {
        if (jsonObject == null || !jsonObject.has("type")) return;

        String type = jsonObject.get("type").getAsString();
        String id = jsonObject.has("id") ? jsonObject.get("id").getAsString() : "";

        switch (type) {
            case "join-my-ring":
                EventBus.getDefault().post(new NotificationModel());
                EventBus.getDefault().post(new ComplimentaryProfileModel());
                EventBus.getDefault().post(new PromoterCirclesModel());
                break;

            case "plusone-accepted":
            case "add-to-plusone":
                if (SessionManager.shared.getUser().isRingMember()) {
                    EventBus.getDefault().post(new ComplimentaryProfileModel());
                } else {
                    EventBus.getDefault().post(new PromoterEventModel());
                }
                break;

            case "plusone-remove":
            case "circle-remove":
            case "ring-remove":
            case "plusone-leave":
            case "cm-leave-ring":
                if (!SessionManager.shared.getUser().isPromoter() && !SessionManager.shared.isSubAdmin()) {
                    DialogManager.getInstance(context).showRestartAppDialog("remove");
                }
                break;

            case "promoter-request":
            case "promoter-request-accepted":
                DialogManager.getInstance(context).showRestartAppDialog("promoter");
                break;

            case "promoter-subadmin-remove":
                DialogManager.getInstance(context).showRestartAppDialog("subadmin-remove");
                break;

            case "ring-accepted":
            case "ring-request-accepted":
                DialogManager.getInstance(context).showRestartAppDialog("complimentary");
                break;
            case "ticket":
                EventBus.getDefault().post(new MyWalletModel());
                break;

            case "review-ticket":
                if (!TextUtils.isEmpty(id)) {
                    Activity activity = ActivityTrackerManager.getInstance().getCurrentActivity();
                    if (activity != null && !activity.isFinishing() && !AppSettingManager.shared.isAlreadyOpenReviewSheet) {
                        FragmentManager fragmentManager = ((FragmentActivity) activity).getSupportFragmentManager();
                        RaynaTicketReviewDialog bottomSheet = new RaynaTicketReviewDialog();
                        bottomSheet.ticketID = id;
                        bottomSheet.ticketName = "";
                        bottomSheet.type = "ticket";
                        bottomSheet.activity = activity;
                        bottomSheet.show(fragmentManager, "");
                        AppSettingManager.shared.isAlreadyOpenReviewSheet = true;
                    }
                }
                break;
        }
    }

    private void handleNotificationClick(JsonObject jsonObject) {
//        Activity currentActivity = ActivityTrackerManager.getInstance().getCurrentActivity();
        if (jsonObject == null) return;

        if (jsonObject.has("message") && jsonObject.get("message").isJsonObject()) {
            JsonObject messageObject = jsonObject.getAsJsonObject("message");
            ChatMessageModel model = new Gson().fromJson(messageObject, ChatMessageModel.class);
            ChatModel chatModel = new ChatModel();
            chatModel.setChatId(model.getChatId());
            chatModel.setChatType(model.getChatType());

            if ("friend".equals(model.getChatType())) {
                chatModel.setImage(model.getAuthorImage());
                chatModel.setTitle(model.getAuthorName());
                chatModel.setMembers(model.getMembers());
            } else if ("bucket".equals(model.getChatType())) {
                chatModel.setMembers(model.getMembers());
            } else if ("promoter_event".equals(model.getChatType())) {
                chatModel.setImage(model.getAuthorImage());
                chatModel.setTitle(model.getAuthorName());
                chatModel.setComplementry(true);
            }

            Intent intent = new Intent(context, ChatMessageActivity.class);
            intent.putExtra("chatModel", new Gson().toJson(chatModel));
            intent.putExtra("type", chatModel.getChatType());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivityForResult(intent,1005);
            Preferences.shared.removeKey("push_notification_msg");

        } else if (jsonObject.has("type") && jsonObject.has("id")) {
            String type = jsonObject.get("type").getAsString();
            String id = jsonObject.get("id").getAsString();
            if (TextUtils.isEmpty(type) || TextUtils.isEmpty(id)) return;

            Intent intent = null;
            switch (type) {
                case "bucket":
                    intent = new Intent(context, BucketListDetailActivity.class);
                    intent.putExtra("bucketId", id);
                    break;
                case "venue":
                    intent = new Intent(context, VenueActivity.class);
                    intent.putExtra("venueId", id);
                    break;
                case "event":
                    intent = new Intent(context, EventDetailsActivity.class);
                    intent.putExtra("eventId", id);
                    break;
                case "outing":
                    intent = new Intent(context, MyInvitationActivity.class);
                    intent.putExtra("id", id);
                    intent.putExtra("notificationType", "notification");
                    break;
                case "activity":
                    intent = new Intent(context, ActivityListDetail.class);
                    intent.putExtra("activityId", id);
                    intent.putExtra("notificationType", "notification");
                    break;
                case "deal":
                    intent = new Intent(context, VoucherDetailScreenActivity.class);
                    intent.putExtra("id", id);
                    intent.putExtra("notificationType", "notification");
                    break;
                case "offer":
                    intent = new Intent(context, OfferDetailActivity.class);
                    intent.putExtra("offerId", id);
                    break;
                case "follow":
                    intent = new Intent(context, OtherUserProfileActivity.class);
                    intent.putExtra("friendId", id);
                    break;
                case "invite":
                    intent = new Intent(context, ComplementaryEventDetailActivity.class);
                    intent.putExtra("eventId", id);
                    intent.putExtra("type", "Promoter");
                    break;
                case "promoter-event":
                    intent = new Intent(context, ComplementaryEventDetailActivity.class);
                    intent.putExtra("eventId", id);
                    intent.putExtra("type", "complementary");
                    break;
                case "add-to-ring":
                    if (SessionManager.shared.getUser().isRingMember()) {
                        intent = new Intent(context, NotificaionActivity.class);
                        intent.putExtra("id", id);
                    } else {
                        intent = new Intent(context, PromoterActivity.class);
                        intent.putExtra("isPromoter", false);
                    }
                    break;
                case "ticket":
                    intent = new Intent(context, RaynaTicketDetailActivity.class);
                    intent.putExtra("ticketId", id);
                    break;
                case "review-ticket":
                    if (!TextUtils.isEmpty(id)) {
                        Activity activity = ActivityTrackerManager.getInstance().getCurrentActivity();
                        if (activity != null && !activity.isFinishing() && !AppSettingManager.shared.isAlreadyOpenReviewSheet) {
                            FragmentManager fragmentManager = ((FragmentActivity) activity).getSupportFragmentManager();
                            RaynaTicketReviewDialog bottomSheet = new RaynaTicketReviewDialog();
                            bottomSheet.ticketID = id;
                            bottomSheet.ticketName = "";
                            bottomSheet.type = "ticket";
                            bottomSheet.activity = activity;
                            bottomSheet.show(fragmentManager, "");
                            AppSettingManager.shared.isAlreadyOpenReviewSheet = true;
                        }
                    }
                    return;
                case "ring-accepted":
                case "ring-request-accepted":
                    DialogManager.getInstance(context).showRestartAppDialog("complimentary");
                    return;
                case "ticket-booking":
                    if (binding.tabLayout.getTabCount() > 0) {
                        binding.tabLayout.selectTab(binding.tabLayout.getTabAt(binding.tabLayout.getTabCount() - 1));
                    }
                    return;
                case "cancel-booking":
                    if (binding.tabLayout.getTabCount() > 0 && AppSettingManager.shared.walletHistoryCallBack != null) {
                        binding.tabLayout.selectTab(binding.tabLayout.getTabAt(binding.tabLayout.getTabCount() - 1));
                        if (AppSettingManager.shared.walletHistoryCallBack != null){
                            AppSettingManager.shared.walletHistoryCallBack.onReceive(true);
                        }
                    }else {
                        startActivity(new Intent(this, WalletActivity.class).putExtra("isOpenHistory",true));
                    }
                    return;
            }

            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent,1005);
            }
        }
    }


}