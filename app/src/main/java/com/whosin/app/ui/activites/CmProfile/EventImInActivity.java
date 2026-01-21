package com.whosin.app.ui.activites.CmProfile;

import android.annotation.SuppressLint;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.databinding.ActivityEventImInBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.ComplementaryProfileManager;
import com.whosin.app.service.models.ComplimentaryProfileModel;
import com.whosin.app.service.models.ContainerListModel;
import com.whosin.app.service.models.NotificationModel;
import com.whosin.app.service.models.PromoterEventModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.comman.BaseActivity;
import com.whosin.app.ui.adapter.CmEventListAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EventImInActivity extends BaseActivity {

    private ActivityEventImInBinding binding;

    private CmEventListAdapter<PromoterEventModel> eventlistdapter;

    private int eventTypeId = 0;

    private boolean isCallEventApi = false;


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------


    @Override
    protected void initUi() {
        Graphics.applyBlurEffect(activity, binding.blureView);
        eventlistdapter = new CmEventListAdapter<>(activity, "eventList");
        binding.eventsImInRecycleView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        binding.eventsImInRecycleView.setAdapter(eventlistdapter);

        eventTypeId = getIntent().getIntExtra("eventTypeId", 0);

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }



        String title = getIntent().getStringExtra("title");

        if (!TextUtils.isEmpty(title)) {
            binding.headerTitle.setText(title);
        }

        isCallEventApi = getIntent().getBooleanExtra("isCallEventApi", false);

        if (isCallEventApi) {
            requestPromoterEventListUser();
        } else {
            if (ComplementaryProfileManager.shared.eventList != null && !ComplementaryProfileManager.shared.eventList.isEmpty()) {
                eventlistdapter.updateData(ComplementaryProfileManager.shared.eventList);
            } else {
                binding.eventsImInRecycleView.setVisibility(View.GONE);
                binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
            }
        }


    }

    @Override
    protected void setListeners() {

        binding.ivClose.setOnClickListener(v -> {
                    finish();
                    ComplementaryProfileManager.shared.eventList = new ArrayList<>();
                }
        );

    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityEventImInBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ComplementaryProfileManager.shared.eventList = new ArrayList<>();
    }


    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NotificationModel model) {
        if (isCallEventApi){
//            requestPromoterEventListUser();
            filterData(new ArrayList<>());
        }
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void filterData(List<PromoterEventModel> data) {
        List<PromoterEventModel> filteredList;

        if (ComplementaryProfileManager.shared.complimentaryProfileModel == null){return;}

         switch (eventTypeId) {
            case 0:
                filteredList = ComplementaryProfileManager.shared.complimentaryProfileModel.getInEvents();
//                filteredList = data.stream()
//                        .filter(p -> "accepted".equals(p.getInvite().getPromoterStatus()) && "in".equals(p.getInvite().getInviteStatus()))
//                        .collect(Collectors.toList());
                break;
            case 1:
                filteredList = ComplementaryProfileManager.shared.complimentaryProfileModel.getSpeciallyForMeEvents();

//                filteredList = data.stream()
//                        .filter(p -> "private".equals(p.getType()) && !p.getInvite().getPromoterStatus().equals("rejected"))
//                        .collect(Collectors.toList());
                break;
            case 2:
                filteredList = ComplementaryProfileManager.shared.complimentaryProfileModel.getWishlistEvents();

//                filteredList = data.stream()
//                        .filter(p -> p.isWishlisted() && !p.getStatus().equals("in-progress"))
//                        .collect(Collectors.toList());
                break;
            case 3:
                filteredList = ComplementaryProfileManager.shared.complimentaryProfileModel.getInterestedEvents();

//                filteredList = data.stream()
//                        .filter(p -> "pending".equals(p.getInvite().getPromoterStatus()) && "in".equals(p.getInvite().getInviteStatus()))
//                        .collect(Collectors.toList());
                break;
            default:
                filteredList = new ArrayList<>();
                break;
        }

        if (!filteredList.isEmpty()) {filteredList.removeIf(p -> "cancelled".equals(p.getStatus()) || "completed".equals(p.getStatus()));}


         if (filteredList.isEmpty()) {
            binding.eventsImInRecycleView.setVisibility(View.GONE);
            binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
        } else {
            eventlistdapter.updateData(filteredList);
            binding.eventsImInRecycleView.setVisibility(View.VISIBLE);
            binding.emptyPlaceHolderView.setVisibility(View.GONE);
        }
    }


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestPromoterEventListUser() {
        showProgress();

        DataService.shared(activity).requestPromoterEventListUser(new RestCallback<ContainerListModel<PromoterEventModel>>(this) {
            @SuppressLint("NewApi")
            @Override
            public void result(ContainerListModel<PromoterEventModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (model.data != null && !model.data.isEmpty()) {
                    binding.eventsImInRecycleView.setVisibility(View.VISIBLE);
                    binding.emptyPlaceHolderView.setVisibility(View.GONE);
                    filterData(model.data);
                } else {
                    binding.eventsImInRecycleView.setVisibility(View.GONE);
                    binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
                }

            }
        });
    }

    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------


    // endregion
    // --------------------------------------
}


