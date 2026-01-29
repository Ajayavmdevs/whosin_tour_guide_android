package com.whosin.business.ui.activites.Story;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.whosin.business.databinding.ActivityStoryViewBinding;
import com.whosin.business.service.models.MessageEvent;
import com.whosin.business.service.models.VenueObjectModel;
import com.whosin.business.ui.activites.comman.BaseActivity;
import com.whosin.business.ui.fragment.home.StoryFragment;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class StoryViewActivity extends BaseActivity {

    private ActivityStoryViewBinding binding;
    private int currentIndex;
    private List<VenueObjectModel> venueList;
    private ViewPagerAdapter pagerAdapter;

    // --------------------------------------
    // region Life Cycle
    // --------------------------------------

    @Override
    protected void initUi() {

        getWindow().addFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN );

        currentIndex = getIntent().getIntExtra( "selectedPosition", 0 );
        String stories = getIntent().getStringExtra( "stories" );
        if (!TextUtils.isEmpty(stories)) {
            venueList = new Gson().fromJson(stories, new TypeToken<List<VenueObjectModel>>() {
            }.getType());
        }

        binding.viewPager.setOffscreenPageLimit(1);
        pagerAdapter = new ViewPagerAdapter(this);
        binding.viewPager.setAdapter(pagerAdapter);
        binding.viewPager.setCurrentItem(currentIndex,false);

        EventBus.getDefault().post(new MessageEvent());
    }

    @Override
    protected void setListeners() {

        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                // Notify the current fragment about its visibility
                Fragment currentFragment = getSupportFragmentManager().findFragmentByTag("f" + position);
                if (currentFragment instanceof StoryFragment) {
                    ((StoryFragment) currentFragment).onFragmentVisible();
                }

                Fragment previousFragment = getSupportFragmentManager().findFragmentByTag("f" + (position - 1));
                Fragment nextFragment = getSupportFragmentManager().findFragmentByTag("f" + (position + 1));

                if (previousFragment instanceof StoryFragment) {
                    ((StoryFragment) previousFragment).onFragmentInvisible();
                }

                if (nextFragment instanceof StoryFragment) {
                    ((StoryFragment) nextFragment).onFragmentInvisible();
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
//                Log.d("StoryFragment", "onPageScrolled: " + position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                Log.d("StoryFragment", "onPageScrollStateChanged: " + state);
                if (state == 0) {
                    Fragment currentFragment = getSupportFragmentManager().findFragmentByTag("f" + binding.viewPager.getCurrentItem());
                    if (currentFragment instanceof StoryFragment) {
                        ((StoryFragment) currentFragment).restartStory();
                    }
                }
            }
        });

    }


    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityStoryViewBinding.inflate( getLayoutInflater() );
        return binding.getRoot();
    }

    // endregion
    // --------------------------------------
    // region Private
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
            return new StoryFragment(venueList.get(position), binding.viewPager);
        }

        @Override
        public int getItemCount() {
            return venueList.size();
        }
    }


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

  /*  public void onSwipeBottom() {
        Toast.makeText(activity, "bottom", Toast.LENGTH_SHORT).show();
    }*/
}


