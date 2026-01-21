package com.whosin.app.ui.activites.CmProfile;

import static com.whosin.app.comman.AppConstants.CmProfileTabOption.valueOf;
import static com.whosin.app.comman.AppDelegate.activity;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.whosin.app.R;
import com.whosin.app.comman.AppConstants;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.databinding.ActivityCmProfileBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.ComplementaryProfileManager;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.ComplimentaryProfileModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.NotificationModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.comman.BaseActivity;
import com.whosin.app.ui.fragment.CmProfile.CmChatFragment;
import com.whosin.app.ui.fragment.CmProfile.CmEventsFragment;
import com.whosin.app.ui.fragment.CmProfile.CmMyProfileFragment;
import com.whosin.app.ui.fragment.CmProfile.CmNotificationFragment;

import org.greenrobot.eventbus.EventBus;

import java.util.Objects;

public class CmProfileActivity extends BaseActivity {

    private ActivityCmProfileBinding binding;

    private String notificationId = "";

    public CommanCallback<Boolean> callback;

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    protected void initUi() {

        notificationId = Utils.notNullString(getIntent().getStringExtra("id"));

        ComplementaryProfileManager.shared.complimentaryProfileModel = SessionManager.shared.getCmUserProfile();

        setupBottomTab();

        binding.cmViewPager.setAdapter(new ViewPagerAdapter(this));
        binding.cmViewPager.setUserInputEnabled(false);
        binding.cmViewPager.setOffscreenPageLimit(1);

        int defaultTabPosition = 0;

        if (!notificationId.isEmpty()) {
            defaultTabPosition = 2;
        }

        binding.cmTabLayout.selectTab(binding.cmTabLayout.getTabAt(defaultTabPosition));
        binding.cmViewPager.setCurrentItem(defaultTabPosition, false);
        binding.cmTabLayout.getTabAt(defaultTabPosition).setIcon(
                getTabBarIconForId(AppConstants.CmProfileTabOption.valueOf(defaultTabPosition), true)
        );


        callback = data -> {
            if (data){finish();}
        };


    }

    @Override
    protected void setListeners() {
        binding.cmTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                int tabPosition = tab.getPosition();
                binding.cmViewPager.setCurrentItem(tabPosition, false);

                tab.setIcon(getTabBarIconForId(AppConstants.CmProfileTabOption.valueOf(tab.getId()), true));
                binding.cmViewPager.setCurrentItem(tab.getId(), false);

                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tab.view.getChildAt(0).getLayoutParams();
                params.bottomMargin = 0;
                tab.view.getChildAt(0).setLayoutParams(params);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.setIcon(getTabBarIconForId(AppConstants.CmProfileTabOption.valueOf(tab.getId()), false));
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
        binding = ActivityCmProfileBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }



    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {}


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------




    private void setupBottomTab() {

        binding.cmTabLayout.addTab(binding.cmTabLayout.newTab().setText("Explorer ").setIcon(getTabBarIconForId(Objects.requireNonNull(valueOf(0)), false)).setId(0));
        binding.cmTabLayout.addTab(binding.cmTabLayout.newTab().setText("Notifications").setIcon(getTabBarIconForId(Objects.requireNonNull(valueOf(1)), false)).setId(1));
        binding.cmTabLayout.addTab(binding.cmTabLayout.newTab().setText("Message").setIcon(getTabBarIconForId(Objects.requireNonNull(valueOf(2)), false)).setId(2));
        binding.cmTabLayout.addTab(binding.cmTabLayout.newTab().setText("My Profile").setIcon(getTabBarIconForId(Objects.requireNonNull(valueOf(3)), false)).setId(3));

        for (int i = 0; i < binding.cmTabLayout.getTabCount(); i++) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) binding.cmTabLayout.getTabAt(i).view.getChildAt(0).getLayoutParams();
            params.bottomMargin = 0;
            binding.cmTabLayout.getTabAt(i).view.getChildAt(0).setLayoutParams(params);
        }
    }


    @DrawableRes
    private int getTabBarIconForId(AppConstants.CmProfileTabOption option, boolean isSelected) {
        switch (option) {
            case Event:
                return isSelected ? R.drawable.icon_promoter_event_selected: R.drawable.icon_promoter_event_unselected;
            case Notification:
                return isSelected ? R.drawable.icon_notification_fil : R.drawable.icon_notification_for_promoter;
            case Chat:
                return isSelected ? R.drawable.icon_cm_chat_selected : R.drawable.icon_cm_chat;
            case Profile:
                return isSelected ? R.drawable.icon_person_fil : R.drawable.icon_profile;

        }
        return isSelected ? R.drawable.icon_person_fil : R.drawable.icon_profile;
    }


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------


    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    private static class ViewPagerAdapter extends FragmentStateAdapter {

        private Fragment[] fragments = new Fragment[4];

        public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (fragments[position] == null) {
                switch (position) {
                    case 0:
                        fragments[0] = new CmEventsFragment();
                        break;
                    case 1:
                        fragments[1] = new CmNotificationFragment(false);
                        break;
                    case 2:
                        fragments[2] = new CmChatFragment();
                        break;
                    case 3:
                        fragments[3] = new CmMyProfileFragment();
                        break;
                }
            }
            return fragments[position];
        }

        @Override
        public int getItemCount() {
            return 4;
        }

    }

    // endregion
    // --------------------------------------

}