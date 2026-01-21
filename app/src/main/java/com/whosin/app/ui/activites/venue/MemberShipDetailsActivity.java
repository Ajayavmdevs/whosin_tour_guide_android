package com.whosin.app.ui.activites.venue;

import android.view.View;

import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.whosin.app.R;
import com.whosin.app.databinding.ActivityMemberShipDetailsBinding;
import com.whosin.app.ui.activites.comman.BaseActivity;
import com.whosin.app.ui.activites.venue.ui.PaymentActivity;
import com.whosin.app.ui.adapter.ViewPagerAdapter;
import com.whosin.app.ui.fragment.PackagePlan.MorePlanFragment;
import com.whosin.app.ui.fragment.PackagePlan.PlanDetailFragment;
import com.whosin.app.ui.fragment.PackagePlan.PopularPlanFragment;

import java.util.Date;

public class MemberShipDetailsActivity extends BaseActivity {


    private ActivityMemberShipDetailsBinding binding;

    private Date date;
    private String validity = "";
    private boolean showDialog = false;


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------


    @Override
    protected void initUi() {

//        new Handler().postDelayed(() -> setTabLayOut(), 100);
//        setTabLayOut();
//        setFragment();


        setViewPager( binding.viewPager );
        binding.tabLayout.setupWithViewPager( binding.viewPager );

    }

    @Override
    protected void setListeners() {

        binding.ivClose.setOnClickListener( view -> {
            onBackPressed();
        } );
        Glide.with( activity ).load( R.drawable.icon_close_btn ).into( binding.ivClose );
    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityMemberShipDetailsBinding.inflate( getLayoutInflater() );
        return binding.getRoot();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showDialog = getIntent().getBooleanExtra("showDialog",false);
        if (showDialog){
            PlanDetailFragment planDetailFragment = new PlanDetailFragment();
            planDetailFragment.show(getSupportFragmentManager(),"");
            showDialog = false;
        }
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------


   /* private void setTabLayOut() {
        binding.tabLayout.addTab( binding.tabLayout.newTab().setText( "POPULAR" ) );
        binding.tabLayout.addTab( binding.tabLayout.newTab().setText( "MORE PLAN" ) );
        binding.tabLayout.addOnTabSelectedListener( new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                setFragment();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        } );
    }*/

/*
    private void setFragment() {

        if (binding.tabLayout.getSelectedTabPosition() == 0) {
            Graphics.replaceFragment( this, binding.container.getId(), new PopularPlanFragment() );
        } else if (binding.tabLayout.getSelectedTabPosition() == 1) {
            Graphics.replaceFragment( this, binding.container.getId(), new MorePlanFragment() );
        }

    }
*/

    private void setViewPager(ViewPager viewPager){
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), 0);
        adapter.addFrag(new PopularPlanFragment(), "POPULAR");
        adapter.addFrag(new MorePlanFragment(), "MORE PLAN");
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit( 3 );

        binding.tabLayout.setupWithViewPager(viewPager);
    }


    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------


}