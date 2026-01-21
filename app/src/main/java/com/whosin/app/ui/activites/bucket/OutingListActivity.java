package com.whosin.app.ui.activites.bucket;

import android.view.View;
import android.widget.Toast;

import com.whosin.app.comman.Utils;
import com.whosin.app.databinding.ActivityOutingListBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.models.ContainerListModel;
import com.whosin.app.service.models.InviteFriendModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.comman.BaseActivity;
import com.whosin.app.ui.adapter.ViewPagerAdapter;
import com.whosin.app.ui.fragment.Bucket.outing.AllOutingFragment;

import java.util.stream.Collectors;

public class OutingListActivity extends BaseActivity {

    private ActivityOutingListBinding binding;

    private final AllOutingFragment allOutingFragment = new AllOutingFragment();
    private final AllOutingFragment createdOutingFragment = new AllOutingFragment();
    private final AllOutingFragment invitedOutingFragment = new AllOutingFragment();
    private final AllOutingFragment historyOutingFragment = new AllOutingFragment();

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------


    @Override
    protected void initUi() {
        binding.ViewPager.setOffscreenPageLimit(3);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), 0);
        adapter.addFrag(allOutingFragment, "All");
        adapter.addFrag(createdOutingFragment, "Created");
        adapter.addFrag(invitedOutingFragment, "Invitation");
        adapter.addFrag(historyOutingFragment, "History");
        binding.ViewPager.setAdapter(adapter);
        binding.tabLayout.setupWithViewPager(binding.ViewPager);
        showProgress();
        requestMyOutingList();
    }

    @Override
    protected void setListeners() {
        binding.ivClose.setOnClickListener(view -> onBackPressed());

        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            requestMyOutingList();
        });
    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityOutingListBinding.inflate(getLayoutInflater());
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

    public void requestMyOutingList() {

        DataService.shared(this).requestMyOutingList(new RestCallback<ContainerListModel<InviteFriendModel>>(this) {
            @Override
            public void result(ContainerListModel<InviteFriendModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(OutingListActivity.this, error, Toast.LENGTH_SHORT).show();
                    return;
                }
                binding.swipeRefreshLayout.setRefreshing( false );
                if (model.data != null && !model.data.isEmpty()) {
                    allOutingFragment.updateData(model.data.stream().filter(p -> !p.getStatus().equals("completed") && !p.getStatus().equals("cancelled")).collect(Collectors.toList()));
                    createdOutingFragment.updateData(model.data.stream()
                            .filter(p -> p.isOwnerOfOuting() &&
                                    (!p.getStatus().equals("completed") && !p.getStatus().equals("cancelled")))
                            .collect(Collectors.toList()));
                    invitedOutingFragment.updateData(model.data.stream().filter(p -> !p.isOwnerOfOuting() && (!p.getStatus().equals("completed") && !p.getStatus().equals("cancelled"))).collect(Collectors.toList()));
                    historyOutingFragment.updateData(model.data.stream().filter(p -> p.getStatus().equals("completed") || p.getStatus().equals("cancelled")).collect(Collectors.toList()));
                }
                else {
                    allOutingFragment.updateData(null);
                    createdOutingFragment.updateData(null);
                    invitedOutingFragment.updateData(null);
                    historyOutingFragment.updateData(null);
                }
            }
        });
    }

    // --------------------------------------
    // endregion

}