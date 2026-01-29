package com.whosin.business.ui.activites.home;

import android.view.View;

import androidx.viewpager.widget.ViewPager;

import com.whosin.business.R;
import com.whosin.business.databinding.ActivityContactUsBinding;
import com.whosin.business.ui.activites.comman.BaseActivity;
import com.whosin.business.ui.adapter.ViewPagerAdapter;
import com.whosin.business.ui.fragment.Contact.InBoxFragment;
import com.whosin.business.ui.fragment.Contact.SendMessageFragment;

public class ContactUsActivity extends BaseActivity {

    private ActivityContactUsBinding binding;

    private ViewPagerAdapter adapter;


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    protected void initUi() {

        binding.tvContactUsTitle.setText(getValue("contact_us"));

        setViewPager( binding.viewPagerContactUs );
        binding.tabLayout.setupWithViewPager( binding.viewPagerContactUs );
    }

    @Override
    protected void setListeners() {
        binding.ivClose.setOnClickListener( v -> {
            onBackPressed();
        } );


    }

    @Override
    protected void populateData() {

    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityContactUsBinding.inflate( getLayoutInflater() );
        return binding.getRoot();
    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------
    public void switchToInboxFragment() {
        binding.viewPagerContactUs.setCurrentItem(1);
    }

    private void setViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter( getSupportFragmentManager(), 0 );
        adapter.addFrag( new SendMessageFragment(), getValue("send_message"));
        adapter.addFrag( new InBoxFragment(), getValue("inbox") );
        viewPager.setAdapter( adapter );
        viewPager.setOffscreenPageLimit( 2 );
        binding.tabLayout.setupWithViewPager( viewPager );
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
    // --------------------------------------

}