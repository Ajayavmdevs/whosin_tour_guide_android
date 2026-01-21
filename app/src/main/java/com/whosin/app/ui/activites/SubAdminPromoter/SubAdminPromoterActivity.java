
package com.whosin.app.ui.activites.SubAdminPromoter;

import static com.whosin.app.comman.AppConstants.SubAdminTabOption.valueOf;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.whosin.app.R;
import com.whosin.app.comman.AppConstants;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Preferences;
import com.whosin.app.comman.Utils;
import com.whosin.app.databinding.ActivitySubAdminPromoterBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.Repository.ChatRepository;
import com.whosin.app.service.manager.AppSettingManager;
import com.whosin.app.service.manager.ChatManager;
import com.whosin.app.service.manager.ContactManager;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.ChatMessageModel;
import com.whosin.app.service.models.ChatModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.auth.AuthenticationActivity;
import com.whosin.app.ui.activites.comman.BaseActivity;
import com.whosin.app.ui.activites.home.Chat.ChatMessageActivity;
import com.whosin.app.ui.fragment.ChatFragment;
import com.whosin.app.ui.fragment.SubAdminPromoter.SubAdminNotificationFragment;
import com.whosin.app.ui.fragment.SubAdminPromoter.SubAdminProfileFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Objects;

public class SubAdminPromoterActivity extends BaseActivity {

    private ActivitySubAdminPromoterBinding binding;

    private String[] REQUIRED_PERMISSIONS = {

    };
    private int currentPermissionIndex = 0;
    private static final int PERMISSION_REQUEST_CODE = 100;


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @SuppressLint("DefaultLocale")
    @Override
    protected void initUi() {

        EventBus.getDefault().register(this);

        Graphics.applyBlurEffect(activity,binding.blurView);

        AppSettingManager.shared.setContext(activity);
        ChatManager.shared.connect();
        ChatManager.shared.syncChatMessages(true);
        getFirebaseToken();

        new Handler().postDelayed(() -> {
            handleNotification();
        }, 500);

        new Handler().postDelayed(() -> {
            if (!checkAllPermissionsGranted()) {
                requestNextPermission();
            }
        }, 1000);

        setupBottomTab();

        binding.viewPager.setAdapter(new ViewPagerAdapter(this));
        binding.viewPager.setUserInputEnabled(false);
        binding.viewPager.setOffscreenPageLimit(1);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
    }

    @Override
    protected void setListeners() {

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.setIcon(getTabBarIconForId(valueOf(tab.getId()), true));
                binding.viewPager.setCurrentItem(tab.getId(), false);

                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tab.view.getChildAt(0).getLayoutParams();
                params.bottomMargin = 0;
                tab.view.getChildAt(0).setLayoutParams(params);

                if (tab.getPosition() == 0){
                    binding.btnLogOut.setVisibility(View.VISIBLE);
                }else {
                    binding.btnLogOut.setVisibility(View.GONE);
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.setIcon(getTabBarIconForId(AppConstants.SubAdminTabOption.valueOf(tab.getId()), false));
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tab.view.getChildAt(0).getLayoutParams();
                params.bottomMargin = 0;
                tab.view.getChildAt(0).setLayoutParams(params);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        binding.btnLogOut.setOnClickListener(view -> {
            Utils.preventDoubleClick(view);
            Graphics.showAlertDialogWithOkCancel(activity, getString(R.string.app_name), "Are you sure you want to Logout?", aBoolean -> {
                if (aBoolean) {
                    showProgress();
                    SessionManager.shared.logout(activity, (success, error) -> {
                        hideProgress();
                        if (!Utils.isNullOrEmpty(error)) {
                            Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        startActivity(new Intent(activity, AuthenticationActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        finish();
                    });
                }
            });
        });


    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivitySubAdminPromoterBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setChatCount();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String event) {
        setChatCount();
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void setChatCount() {
        long count = ChatRepository.shared(Graphics.context).getAllUnrealMessageCountForSubAdmin();
        binding.tabLayout.getTabAt(1).getOrCreateBadge().setNumber((int) count);
        binding.tabLayout.getTabAt(1).getBadge().setBadgeTextColor(getColor(R.color.white));
        binding.tabLayout.getTabAt(1).getBadge().setBackgroundColor(getColor(R.color.red));
        binding.tabLayout.getTabAt(1).getBadge().setVisible(count != 0);
    }

    private void setupBottomTab() {

        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Profile").setIcon(getTabBarIconForId(Objects.requireNonNull(valueOf(0)), true)).setId(0));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Chat").setIcon(getTabBarIconForId(Objects.requireNonNull(valueOf(1)), false)).setId(1));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Notification").setIcon(getTabBarIconForId(Objects.requireNonNull(valueOf(2)), false)).setId(2));

        for (int i = 0; i < binding.tabLayout.getTabCount(); i++) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) binding.tabLayout.getTabAt(i).view.getChildAt(0).getLayoutParams();
            params.bottomMargin = 0;
            binding.tabLayout.getTabAt(i).view.getChildAt(0).setLayoutParams(params);
        }
    }


    @DrawableRes
    private int getTabBarIconForId(AppConstants.SubAdminTabOption option, boolean isSelected) {
        switch (option) {
            case Home:
                return isSelected ? R.drawable.icon_person_fil : R.drawable.icon_profile;
            case Chat:
                return isSelected ? R.drawable.icon_tab_chat_fill : R.drawable.icon_tab_chat_line;
            case Notification:
                return isSelected ? R.drawable.icon_notification_fil : R.drawable.icon_notification_for_promoter;
        }
        return isSelected ? R.drawable.icon_person_fil : R.drawable.icon_profile;
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
    }

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void getFirebaseToken() {
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                return;
            }
            String token = task.getResult();
            requestUpdateDeviceToken(token);
            subscribeToTopic("sendtoall");
            subscribeToTopic("sendtoandroid");
        });
    }

    private void requestUpdateDeviceToken(String token) {
        if (TextUtils.isEmpty(SessionManager.shared.getToken())) {
            return;
        }
        String deviceId = Utils.getDeviceUniqueId(this);
        DataService.shared(this).requestUpdateFcmToken(token, deviceId, new RestCallback<ContainerModel<UserDetailModel>>(this) {
            @Override
            public void result(ContainerModel<UserDetailModel> model, String error) {
            }
        });
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
            switch (AppConstants.SubAdminTabOption.valueOf(position)) {
                case Home:
                    return new SubAdminProfileFragment();
                case Chat:
                    return new ChatFragment(true);
                case Notification:
                    return new SubAdminNotificationFragment();
                default:
                    return new SubAdminProfileFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }


    // --------------------------------------
    // endregion


}