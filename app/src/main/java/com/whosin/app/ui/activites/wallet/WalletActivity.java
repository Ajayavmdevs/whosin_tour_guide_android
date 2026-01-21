package com.whosin.app.ui.activites.wallet;

import android.view.View;

import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.whosin.app.R;
import com.whosin.app.databinding.ActivityWalletBinding;
import com.whosin.app.ui.activites.comman.BaseActivity;
import com.whosin.app.ui.adapter.ViewPagerAdapter;
import com.whosin.app.ui.fragment.wallet.GiftsFragment;
import com.whosin.app.ui.fragment.wallet.HistoryFragment;
import com.whosin.app.ui.fragment.wallet.MyItemsFragment;

public class WalletActivity extends BaseActivity {

    private ActivityWalletBinding binding;


    @Override
    protected void initUi() {

        binding.tvTitle.setText(getValue("my_wallet"));

        boolean isOpenHistory = getIntent().getBooleanExtra("isOpenHistory",false);

        setViewPager(binding.viewPager,isOpenHistory);

        binding.tabLayout.setupWithViewPager(binding.viewPager);

        Glide.with(activity).load(R.drawable.icon_close_btn).into(binding.ivClose);

    }

    @Override
    protected void setListeners() {
        binding.ivClose.setOnClickListener(view -> {
            finish();
        });
    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityWalletBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void setViewPager(ViewPager viewPager,boolean isOpenHistory) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), 0);
        adapter.addFrag(new MyItemsFragment(), getValue("my_items"));
//        adapter.addFrag(new GiftsFragment(), "Gifts");
        adapter.addFrag(new HistoryFragment(), getValue("history"));
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(4);
        binding.tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 2) {
                    HistoryFragment historyFragment = (HistoryFragment) adapter.instantiateItem(viewPager, position);
                    historyFragment.giftHistoryList();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        if (isOpenHistory){
            binding.viewPager.setCurrentItem(1,true);
        }
    }


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------


    // endregion
    // --------------------------------------
    // region Adapter


    // --------------------------------------
    // endregion
}