package com.whosin.app.ui.fragment;

import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;
import com.whosin.app.R;
import com.whosin.app.comman.Graphics;
import com.whosin.app.databinding.FragmentChatBinding;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.ui.fragment.Chat.BucketChatFragment;
import com.whosin.app.ui.fragment.Chat.EventChatFragment;
import com.whosin.app.ui.fragment.Chat.FriendsFragment;
import com.whosin.app.ui.fragment.comman.BaseFragment;


public class ChatFragment extends BaseFragment {

    private FragmentChatBinding binding;

    private boolean isFromSubAdmin = false;

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    public ChatFragment(){}

    public ChatFragment(boolean isFromSubAdmin){
      this.isFromSubAdmin = isFromSubAdmin;
    }



    @Override
    public void initUi(View view) {
        binding = FragmentChatBinding.bind(view);

        binding.navbar.setText(getValue("chat"));

        if (isFromSubAdmin) {
            int marginInPixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, context.getResources().getDisplayMetrics());
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) binding.navbar.getLayoutParams();
            params.topMargin = marginInPixels;
            binding.navbar.setLayoutParams(params);

            setTabLayoutForSubAdmin();
            setFragmentForSubAdmin();
        } else {
            setTabLayout();
            setFragment();
        }

    }

    @Override
    public void setListeners() {

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (isFromSubAdmin){
                    setFragmentForSubAdmin();
                }else {
                    setFragment();
                }

            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

    }

    @Override
    public void populateData(boolean getDataFromServer) {

    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_chat;
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void setTabLayout() {

        TabLayout.Tab tab1 = binding.tabLayout.newTab();
        tab1.setCustomView(R.layout.custom_tab_layout);
        binding.tabLayout.addTab(tab1);

        TabLayout.Tab tab2 = binding.tabLayout.newTab();
        tab2.setCustomView(R.layout.custom_tab_layout);
        binding.tabLayout.addTab(tab2);

        if (SessionManager.shared.getUser().isPromoter() || SessionManager.shared.getUser().isRingMember()){
            TabLayout.Tab tab3 = binding.tabLayout.newTab();
            tab3.setCustomView(R.layout.custom_tab_layout);
            binding.tabLayout.addTab(tab3);
        }

        for (int i = 0; i < binding.tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = binding.tabLayout.getTabAt(i);
            View customTabView = tab.getCustomView();
            if (customTabView != null) {
                TextView tvTabText = customTabView.findViewById(R.id.tvTabText);
                ImageView imageNotificationCount = customTabView.findViewById(R.id.imageNotificationCount);

                if (SessionManager.shared.getUser().isPromoter() || SessionManager.shared.getUser().isRingMember()) {
                    switch (i) {
                        case 0:
                            tvTabText.setText(getValue("friends"));
                            break;
                        case 1:
                            if (SessionManager.shared.getUser().isPromoter()) {
                                tvTabText.setText(getValue("promoter"));
                            } else if (SessionManager.shared.getUser().isRingMember()) {
                                tvTabText.setText(getValue("complimentary"));
                            }
                            break;
                        case 2:
                            tvTabText.setText(getValue("group_chat"));
                            break;
                    }
                } else {
                    if (i == 0) {
                        tvTabText.setText(getValue("friends"));
                    } else if (i == 1) {
                        tvTabText.setText(getValue("group_chat"));
                    }
                }
                imageNotificationCount.setImageResource(R.drawable.mycart_background);
                imageNotificationCount.setVisibility(View.GONE);


            }
        }

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                setFragment();
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setFragment() {
        int selectedTabPosition = binding.tabLayout.getSelectedTabPosition();
        boolean isSpecialUser = SessionManager.shared.getUser().isPromoter() || SessionManager.shared.getUser().isRingMember();

        Fragment fragment;
        if (selectedTabPosition == 0) {
            fragment = new FriendsFragment();
        } else if (selectedTabPosition == 1) {
            fragment = isSpecialUser ? new EventChatFragment() : new BucketChatFragment();
        } else {
            fragment = new BucketChatFragment();
        }

        Graphics.replaceFragment(this, binding.container.getId(), fragment);
    }


    private void setTabLayoutForSubAdmin() {

        TabLayout.Tab tab1 = binding.tabLayout.newTab();
        tab1.setCustomView(R.layout.custom_tab_layout);
        binding.tabLayout.addTab(tab1);

        TabLayout.Tab tab2 = binding.tabLayout.newTab();
        tab2.setCustomView(R.layout.custom_tab_layout);
        binding.tabLayout.addTab(tab2);


        for (int i = 0; i < binding.tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = binding.tabLayout.getTabAt(i);
            View customTabView = tab.getCustomView();
            if (customTabView != null) {
                TextView tvTabText = customTabView.findViewById(R.id.tvTabText);
                ImageView imageNotificationCount = customTabView.findViewById(R.id.imageNotificationCount);
                if (i == 0) {
                    tvTabText.setText(getValue("complimentary"));
                } else if (i == 1) {
                    tvTabText.setText(getValue("events"));
                }
                imageNotificationCount.setImageResource(R.drawable.mycart_background);
                imageNotificationCount.setVisibility(View.GONE);


            }
        }


    }

    private void setFragmentForSubAdmin() {
        int selectedTabPosition = binding.tabLayout.getSelectedTabPosition();

        Fragment fragment;
        if (selectedTabPosition == 0) {
            fragment = new FriendsFragment();
        } else {
            fragment = new EventChatFragment(true);
        }

        Graphics.replaceFragment(this, binding.container.getId(), fragment);
    }


    // endregion
    // --------------------------------------

}