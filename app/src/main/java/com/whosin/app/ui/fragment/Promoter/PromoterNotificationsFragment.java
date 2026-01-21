package com.whosin.app.ui.fragment.Promoter;

import static com.whosin.app.comman.AppConstants.TabOption.valueOf;

import android.graphics.Color;
import android.view.View;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabLayoutMediator;
import com.whosin.app.R;
import com.whosin.app.databinding.FragmentNotificationsBinding;
import com.whosin.app.service.manager.PromoterProfileManager;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.ui.fragment.Promoter.notification.PromoterNotificationEventFragment;
import com.whosin.app.ui.fragment.Promoter.notification.PromoterNotificationUserFragment;
import com.whosin.app.ui.fragment.comman.BaseFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


public class PromoterNotificationsFragment extends BaseFragment {

    private FragmentNotificationsBinding binding;

    private boolean isHideHeaderView = false;

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    public PromoterNotificationsFragment(boolean isHideHeaderView) {
        this.isHideHeaderView = isHideHeaderView;
    }

    @Override
    public void initUi(View view) {
        binding = FragmentNotificationsBinding.bind( view );

        if (isHideHeaderView) {
            int heightInPixels = (int) getResources().getDimension(R.dimen.promoter_notification_top);
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) binding.constraint.getLayoutParams();
            params.topMargin = heightInPixels;
            binding.constraint.setLayoutParams(params);
            binding.headerView.setVisibility(View.GONE);
        } else {
            if (!EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().register(this);
            }
            if (PromoterProfileManager.shared.promoterProfileModel != null && PromoterProfileManager.shared.promoterProfileModel.getProfile() != null) {
                binding.headerView.setUpData(requireActivity(), PromoterProfileManager.shared.promoterProfileModel.getProfile());
            }
            PromoterProfileManager.shared.callbackForHeader = data -> {
                if (isAdded()) {
                    binding.headerView.setUpData(requireActivity(), data);
                }
             };
        }

        binding.viewPager.setAdapter(new ViewPagerAdapter(requireActivity()));
        binding.viewPager.setUserInputEnabled(false);
        binding.viewPager.setOffscreenPageLimit(1);
        setupTabLayout();
    }

    @Override
    public void setListeners() {}

    @Override
    public void populateData(boolean getDataFromServer) {}

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_notifications;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UserDetailModel event) {
        if (PromoterProfileManager.shared.promoterProfileModel != null && PromoterProfileManager.shared.promoterProfileModel.getProfile() != null) {
            binding.headerView.setUpData(requireActivity(), PromoterProfileManager.shared.promoterProfileModel.getProfile());
        }
    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void setupTabLayout() {
        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText(getValue("users"));
                BadgeDrawable badgeDrawable = tab.getOrCreateBadge();
                badgeDrawable.setVisible(false);
                badgeDrawable.setNumber(3);
                customizeBadgeDrawable(badgeDrawable);
            } else if (position == 1) {
                tab.setText(getValue("events"));
                BadgeDrawable badgeDrawable = tab.getOrCreateBadge();
                badgeDrawable.setVisible(false);
                badgeDrawable.setNumber(1);
                customizeBadgeDrawable(badgeDrawable);
            }
        }).attach();
    }

    private void customizeBadgeDrawable(BadgeDrawable badgeDrawable) {
        badgeDrawable.setBackgroundColor( ContextCompat.getColor(requireContext(), R.color.date_red));
        badgeDrawable.setBadgeTextColor( Color.WHITE);
        badgeDrawable.setHorizontalOffset(-90);
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
                    return new PromoterNotificationUserFragment(isHideHeaderView);
                default:
                    return new PromoterNotificationEventFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }

    // endregion
    // --------------------------------------
}