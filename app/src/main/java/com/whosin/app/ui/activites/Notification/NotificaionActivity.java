package com.whosin.app.ui.activites.Notification;


import static com.whosin.app.comman.AppConstants.TabOption.valueOf;

import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.whosin.app.R;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.databinding.ActivityNotificaionBinding;
import com.whosin.app.service.manager.AppSettingManager;
import com.whosin.app.service.manager.RaynaTicketManager;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.ui.activites.comman.BaseActivity;
import com.whosin.app.ui.fragment.CmProfile.CmNotificationFragment;
import com.whosin.app.ui.fragment.Notification.NotificationFragment;
import com.whosin.app.ui.fragment.Promoter.PromoterNotificationsFragment;

public class NotificaionActivity extends BaseActivity {

    private ActivityNotificaionBinding binding;

    private CommanCallback<Boolean> callback;

    private CommanCallback<Boolean> isNotificationListPresent;

    public boolean isShowNotification = false;


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    protected void initUi() {

        binding.tvNotificationTitle.setText(getValue("notifications"));
        binding.btnDeleteAll.setText(getValue("delete_all"));

        String notificationId = Utils.notNullString(getIntent().getStringExtra("id"));

        if (!SessionManager.shared.getUser().isPromoter() && !SessionManager.shared.getUser().isRingMember()) {
            binding.constraint.setVisibility(View.GONE);
            binding.viewPager.setAdapter(new SingleFragmentStateAdapter(this));
        } else {
            binding.viewPager.setAdapter(new ViewPagerAdapter(this));

        }
        binding.viewPager.setUserInputEnabled(false);
        binding.viewPager.setOffscreenPageLimit(1);
        Graphics.applyBlurEffect(activity, binding.blurView);
        setupTabLayout();


        if (!TextUtils.isEmpty(notificationId)){
            binding.viewPager.setCurrentItem(1, false);
        }

        callback = data -> {
            if (data) {
                if (RaynaTicketManager.shared.walletRedirectCallBack != null) {
                    RaynaTicketManager.shared.walletRedirectCallBack.onReceive(true);
                }
                finish();
            }
        };


        isNotificationListPresent = data -> {
            binding.btnDeleteAll.setVisibility(data ? View.VISIBLE : View.GONE);
            isShowNotification = data;
        };

    }

    @Override
    protected void setListeners() {
        binding.ivClose.setOnClickListener(view -> onBackPressed());

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if (isShowNotification){
                    if (position == 0) {
                        binding.btnDeleteAll.setVisibility(View.VISIBLE);
                    } else if (position == 1) {
                        binding.btnDeleteAll.setVisibility(View.GONE);
                    }
                }else {
                    binding.btnDeleteAll.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        binding.btnDeleteAll.setOnClickListener(v -> {
            if (AppSettingManager.shared.deleteNotificationCallBack != null){
                AppSettingManager.shared.deleteNotificationCallBack.onReceive(true);
            }
        });


    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityNotificaionBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void setupTabLayout() {

        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText(getValue("normal"));
            } else if (position == 1) {
                if(SessionManager.shared.getUser().isPromoter()) {
                        tab.setText(getValue("promoter"));
                }
                else if (SessionManager.shared.getUser().isRingMember()) {
                    tab.setText(getValue("complimentary"));
                }
            }
        }).attach();
    }


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------


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
                    return new NotificationFragment(callback,isNotificationListPresent);
                default:
                    if (SessionManager.shared.getUser().isPromoter()) {
                        return new PromoterNotificationsFragment(true);

                    } else if (SessionManager.shared.getUser().isRingMember()) {
                        return new CmNotificationFragment(true);
                    }

            }
            return new NotificationFragment(callback,isNotificationListPresent);
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }

    public class SingleFragmentStateAdapter extends FragmentStateAdapter {
        public SingleFragmentStateAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return new NotificationFragment(callback,isNotificationListPresent);
        }

        @Override
        public int getItemCount() {
            return 1;
        }
    }

     // endregion
    // --------------------------------------
}