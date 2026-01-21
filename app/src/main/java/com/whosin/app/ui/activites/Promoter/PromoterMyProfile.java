package com.whosin.app.ui.activites.Promoter;

import static com.whosin.app.comman.AppConstants.PromoterTabOption.valueOf;

import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.google.android.material.tabs.TabLayout;
import com.whosin.app.R;
import com.whosin.app.comman.AppConstants;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.databinding.ActivityPromoterMyProfileBinding;
import com.whosin.app.service.manager.PromoterProfileManager;
import com.whosin.app.ui.fragment.Promoter.PromoterChatFragment;
import com.whosin.app.ui.fragment.Promoter.PromoterEventHistoryFragment;
import com.whosin.app.ui.fragment.Promoter.PromoterMyEventsFragment;
import com.whosin.app.ui.fragment.Promoter.PromoterMyProfileFragment;
import com.whosin.app.ui.fragment.Promoter.PromoterNotificationsFragment;
import com.whosin.app.ui.fragment.comman.BaseFragment;

import java.util.Objects;

public class PromoterMyProfile extends BaseFragment {

    private ActivityPromoterMyProfileBinding binding;


    public CommanCallback<Boolean> callback;

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------



    @Override
    public void initUi(View view) {

        binding = ActivityPromoterMyProfileBinding.bind(view);


        setupBottomTab();

        binding.viewPager.setAdapter(new ViewPagerAdapter(requireActivity()));
        binding.viewPager.setUserInputEnabled(false);
        binding.viewPager.setOffscreenPageLimit(1);

        int defaultTabPosition = 0;


        binding.tabLayout.selectTab(binding.tabLayout.getTabAt(defaultTabPosition));
        binding.viewPager.setCurrentItem(defaultTabPosition, false);
        Objects.requireNonNull(binding.tabLayout.getTabAt(defaultTabPosition)).setIcon(
                getTabBarIconForId(Objects.requireNonNull(valueOf(defaultTabPosition)), true)
        );


        callback = data -> {
            if (data){
                PromoterProfileManager.shared.setProfileCallBack.onReceive(true);
            }
        };
    }

    @Override
    public void setListeners() {

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                int tabPosition = tab.getPosition();
                binding.viewPager.setCurrentItem(tabPosition, false);

                tab.setIcon(getTabBarIconForId(Objects.requireNonNull(valueOf(tab.getId())), true));
                binding.viewPager.setCurrentItem(tab.getId(), false);

                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tab.view.getChildAt(0).getLayoutParams();
                params.bottomMargin = 0;
                tab.view.getChildAt(0).setLayoutParams(params);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.setIcon(getTabBarIconForId(Objects.requireNonNull(valueOf(tab.getId())), false));
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
    public void populateData(boolean getDataFromServer) {

    }


    @Override
    public int getLayoutRes() {
        return R.layout.activity_promoter_my_profile;
    }



    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------



    private void setupBottomTab() {

        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(getValue("my_profile")).setIcon(getTabBarIconForId(Objects.requireNonNull(valueOf(0)), false)).setId(0));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(getValue("my_event")).setIcon(getTabBarIconForId(Objects.requireNonNull(valueOf(1)), false)).setId(1));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(getValue("event_history")).setIcon(getTabBarIconForId(Objects.requireNonNull(valueOf(2)), false)).setId(2));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(getValue("messages")).setIcon(getTabBarIconForId(Objects.requireNonNull(valueOf(3)), false)).setId(3));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(getValue("notifications")).setIcon(getTabBarIconForId(Objects.requireNonNull(valueOf(4)), false)).setId(4));

        for (int i = 0; i < binding.tabLayout.getTabCount(); i++) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) Objects.requireNonNull(binding.tabLayout.getTabAt(i)).view.getChildAt(0).getLayoutParams();
            params.bottomMargin = 0;
            Objects.requireNonNull(binding.tabLayout.getTabAt(i)).view.getChildAt(0).setLayoutParams(params);
        }
    }


    @DrawableRes
    private int getTabBarIconForId(AppConstants.PromoterTabOption option, boolean isSelected) {
        switch (option) {
            case Profile:
                return isSelected ? R.drawable.icon_person_fil : R.drawable.icon_profile;
            case Event:
                return isSelected ? R.drawable.icon_promoter_event_selected: R.drawable.icon_promoter_event_unselected;
            case Event_History:
                return isSelected ? R.drawable.icon_promoter_history_event_selected : R.drawable.icon_promoter_history_event_unselected;
            case Chat:
                return isSelected ? R.drawable.icon_cm_chat_selected : R.drawable.icon_cm_chat;
            case Notification:
                return isSelected ? R.drawable.icon_notification_fil : R.drawable.icon_notification_for_promoter;
        }
        return isSelected ? R.drawable.icon_person_fil : R.drawable.icon_profile;
    }


    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    private static class ViewPagerAdapter extends FragmentStateAdapter {
        private final Fragment[] fragments;

        public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
            fragments = new Fragment[5];
            fragments[0] = new PromoterMyProfileFragment();
            fragments[1] = new PromoterMyEventsFragment();
            fragments[2] = new PromoterEventHistoryFragment();
            fragments[3] = new PromoterChatFragment();
            fragments[4] = new PromoterNotificationsFragment(false);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return fragments[position];
        }

        @Override
        public int getItemCount() {
            return 5;
        }

    }

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------



    // endregion
    // --------------------------------------
}