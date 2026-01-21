package com.whosin.app.ui.activites.bucket;

import android.view.View;

import com.whosin.app.databinding.ActivityEventListBinding;
import com.whosin.app.ui.activites.comman.BaseActivity;
import com.whosin.app.ui.adapter.ViewPagerAdapter;
import com.whosin.app.ui.fragment.Bucket.event.HistoryEventFragment;
import com.whosin.app.ui.fragment.Bucket.event.UpcomingEventFragment;

public class EventListActivity extends BaseActivity {

    private ActivityEventListBinding binding;

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    protected void initUi() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), 0);
        adapter.addFrag(new UpcomingEventFragment(), "Upcoming");
        adapter.addFrag(new HistoryEventFragment(), "History");
        binding.ViewPager.setAdapter(adapter);
        binding.tabLayout.setupWithViewPager(binding.ViewPager);
    }

    @Override
    protected void setListeners() {
        binding.ivClose.setOnClickListener(view -> {
            onBackPressed();
        });
    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityEventListBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    // --------------------------------------
    // endregion

}