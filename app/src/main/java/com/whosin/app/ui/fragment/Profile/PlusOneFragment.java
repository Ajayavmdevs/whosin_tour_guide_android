package com.whosin.app.ui.fragment.Profile;

import static com.whosin.app.comman.AppDelegate.activity;

import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.whosin.app.R;
import com.whosin.app.comman.Utils;
import com.whosin.app.databinding.FragmentPlusOneBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.models.ContainerListModel;
import com.whosin.app.service.models.PromoterEventModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.CmProfile.MyGroupAllUserActivity;
import com.whosin.app.ui.adapter.PlusOneUserListAdapter;
import com.whosin.app.ui.adapter.PlusOneEventListAdapter;
import com.whosin.app.ui.fragment.comman.BaseFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;

public class PlusOneFragment extends BaseFragment {

    private FragmentPlusOneBinding binding;

    private PlusOneEventListAdapter<PromoterEventModel> plusOneEventListAdapter;

    private PlusOneUserListAdapter<UserDetailModel> plusOneUserListAdapter;


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------


    @Override
    public void initUi(View view) {

        binding = FragmentPlusOneBinding.bind(view);

        applyTranslations();

        binding.emptyPlaceHolderView.setEmptyPlaceTxtTitle(getValue("empty_event_profile"));

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        plusOneEventListAdapter = new PlusOneEventListAdapter<>(requireActivity());
        binding.eventRecycleView.setLayoutManager(new LinearLayoutManager(requireActivity(),LinearLayoutManager.HORIZONTAL,false));
        binding.eventRecycleView.setAdapter(plusOneEventListAdapter);

        plusOneUserListAdapter = new PlusOneUserListAdapter<>(requireActivity());
        binding.myGroupRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity(),LinearLayoutManager.HORIZONTAL,false));
        binding.myGroupRecyclerView.setAdapter(plusOneUserListAdapter);

        requestPromoterPlusOneList();
        plusOneGroupListUser();

    }

    @Override
    public void setListeners() {

        binding.seeAllConstraint.setOnClickListener(view -> {
            Utils.preventDoubleClick(view);
            startActivity(new Intent(activity, MyGroupAllUserActivity.class).putExtra("isFromPlusOne",true));
        });

    }

    @Override
    public void populateData(boolean getDataFromServer) {

    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_plus_one;
    }

    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(PromoterEventModel  model) {
        requestPromoterPlusOneList();
        plusOneGroupListUser();
    }

    @Override
    protected Map<View, String> getTranslationMap() {
        Map<View, String> map = new HashMap<>();
        map.put(binding.tvMyGroup, "my_plus_one_group");
        map.put(binding.tvSeeAllTitle, "see_all");
        map.put(binding.tvFilterTitle, "plus_one_events");
        return map;
    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestPromoterPlusOneList() {
        showProgress();
        DataService.shared(requireActivity()).requestPromoterPlusOneList(new RestCallback<ContainerListModel<PromoterEventModel>>(this) {
            @Override
            public void result(ContainerListModel<PromoterEventModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(requireActivity(), error, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (model.data != null && !model.data.isEmpty()) {
                    binding.eventRecycleView.setVisibility(View.VISIBLE);
                    binding.tvFilterTitle.setVisibility(View.VISIBLE);
                    plusOneEventListAdapter.updateData(model.data);
                    binding.emptyPlaceHolderView.setVisibility(View.GONE);

                } else {
                    binding.eventRecycleView.setVisibility(View.GONE);
                    binding.tvFilterTitle.setVisibility(View.GONE);
                    binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void plusOneGroupListUser(){
        showProgress();
        DataService.shared(requireActivity()).requestPromoterPlusOneGroupListUser(new RestCallback<ContainerListModel<UserDetailModel>>(this) {
            @Override
            public void result(ContainerListModel<UserDetailModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(requireActivity(), error, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (model.data != null && !model.data.isEmpty()) {
                    binding.groupAddAndListViewLayout.setVisibility(View.VISIBLE);
                    binding.myGroupLayout.setVisibility(View.VISIBLE);
                    plusOneUserListAdapter.updateData(model.data);
                    binding.tvMyGroupCount.setText(setValue("my_plusone_group_count",String.valueOf(model.data.size())));
                } else {
                    binding.groupAddAndListViewLayout.setVisibility(View.GONE);
                    binding.myGroupLayout.setVisibility(View.GONE);
                }
            }
        });
    }

    // --------------------------------------
    // region Adapter
    // --------------------------------------





    // endregion
    // --------------------------------------
}