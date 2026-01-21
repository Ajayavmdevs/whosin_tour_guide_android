package com.whosin.app.ui.fragment.CmProfile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.FragmentCmEventsBinding;
import com.whosin.app.databinding.ItemCmEventNameCountBinding;
import com.whosin.app.databinding.ItemCmExploreTabDesignBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.ComplementaryProfileManager;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.AppSettingTitelCommonModel;
import com.whosin.app.service.models.ComplimentaryProfileModel;
import com.whosin.app.service.models.ContainerListModel;
import com.whosin.app.service.models.PromoterEventModel;
import com.whosin.app.service.models.RatingModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.CmProfile.EventImInActivity;
import com.whosin.app.ui.adapter.CmEventListAdapter;
import com.whosin.app.ui.fragment.comman.BaseFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


public class CmEventsFragment extends BaseFragment {

    private FragmentCmEventsBinding binding;

    private CmEventListAdapter<PromoterEventModel> eventlistdapter;

    private ItemListAdapter<RatingModel> itemListAdapter = new ItemListAdapter<>();

    private List<RatingModel> filterList = new ArrayList<>();

    private List<PromoterEventModel> eventList = new ArrayList<>();




    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    public void initUi(View view) {

        binding = FragmentCmEventsBinding.bind(view);

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        eventlistdapter = new CmEventListAdapter<>(requireActivity(),"eventList");
        binding.eventListRecycleView.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false));
        binding.eventListRecycleView.setAdapter(eventlistdapter);

        binding.eventFilterTabRecycleView.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.eventFilterTabRecycleView.setAdapter(itemListAdapter);

        List<RatingModel> tabTitle = new ArrayList<>();
        tabTitle.add(new RatingModel(0,"Near me"));
        tabTitle.add(new RatingModel(1,"Starting Soon"));
        itemListAdapter.updateData(tabTitle);

        binding.swipeRefreshLayout.setProgressViewOffset(false,0,220);
        binding.headerView.isFromCM = true;
        if (ComplementaryProfileManager.shared.complimentaryProfileModel != null && ComplementaryProfileManager.shared.complimentaryProfileModel.getProfile() != null) {
            binding.headerView.setUpData(requireActivity(), ComplementaryProfileManager.shared.complimentaryProfileModel.getProfile());
            binding.headerView.userDetailModel = ComplementaryProfileManager.shared.complimentaryProfileModel.getProfile();
        }

        requestPromoterEventListUser(true);

    }

    @Override
    public void setListeners() {
        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            requestPromoterEventListUser(false);
        });
    }

    @Override
    public void populateData(boolean getDataFromServer) {

    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_cm_events;
    }

    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ComplimentaryProfileModel model) {
        requestPromoterEventListUser(false);
        if (ComplementaryProfileManager.shared.complimentaryProfileModel != null && ComplementaryProfileManager.shared.complimentaryProfileModel.getProfile() != null) {
            binding.headerView.setUpData(requireActivity(), ComplementaryProfileManager.shared.complimentaryProfileModel.getProfile());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ComplementaryProfileManager.shared.complimentaryProfileModel != null && ComplementaryProfileManager.shared.complimentaryProfileModel.getProfile() != null) {
            binding.headerView.setUpData(requireActivity(), ComplementaryProfileManager.shared.complimentaryProfileModel.getProfile());
            binding.headerView.userDetailModel = ComplementaryProfileManager.shared.complimentaryProfileModel.getProfile();
        }
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------
    
    private void filterEventListData(){
        binding.eventListRecycleView.setVisibility(View.VISIBLE);
        binding.emptyPlaceHolderView.setVisibility(View.GONE);

        if (filterList.isEmpty()){
            eventlistdapter.updateData(eventList); 
        }else {
            RatingModel model = filterList.get(0);
            if (model.getId() == 0){
                List<PromoterEventModel> sortedEventsByDistance  = getSortedEventsByDistance();
                if (!sortedEventsByDistance.isEmpty()){
                    eventlistdapter.updateData(sortedEventsByDistance);
                }else {
                    binding.eventListRecycleView.setVisibility(View.GONE);
                    binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
                }
            } else if (model.getId() == 1) {
                List<PromoterEventModel> sortedEvents = getSortedEventsByDateAndTime();
                if (!sortedEvents.isEmpty()){
                    eventlistdapter.updateData(sortedEvents);
                }else {
                    binding.eventListRecycleView.setVisibility(View.GONE);
                    binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private List<PromoterEventModel> getSortedEventsByDateAndTime() {
        List<PromoterEventModel> sortedEvents = new ArrayList<>(eventList);
        Collections.sort(sortedEvents, (e1, e2) -> {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Date event1DateTime = dateFormat.parse(e1.getDate() + " " + e1.getStartTime());
                Date event2DateTime = dateFormat.parse(e2.getDate() + " " + e2.getStartTime());
                return event1DateTime.compareTo(event2DateTime);
            } catch (ParseException ex) {
                ex.printStackTrace();
            }
            return 0;
        });
        return sortedEvents;
    }

    private List<PromoterEventModel> getSortedEventsByDistance() {
        List<PromoterEventModel> sortedEvents = new ArrayList<>(eventList);
        Collections.sort(sortedEvents, Comparator.comparingDouble(PromoterEventModel::getDistance));
        return sortedEvents;
    }


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestPromoterEventListUser(boolean isShowProgress) {
        eventList = new ArrayList<>();
        if (isShowProgress) {
            showProgress();
        } else {
            binding.swipeRefreshLayout.setRefreshing(true);
        }
        DataService.shared(requireActivity()).requestPromoterEventListUser(new RestCallback<ContainerListModel<PromoterEventModel>>(this) {
            @SuppressLint("NewApi")
            @Override
            public void result(ContainerListModel<PromoterEventModel> model, String error) {
                hideProgress();
                binding.swipeRefreshLayout.setRefreshing(false);
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(requireActivity(), error, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (model.data != null && !model.data.isEmpty()) {
                    binding.eventListRecycleView.setVisibility(View.VISIBLE);
                    binding.emptyPlaceHolderView.setVisibility(View.GONE);
                    model.data.removeIf(p -> "cancelled".equals(p.getStatus()) || "completed".equals(p.getStatus()));
                    if (!model.data.isEmpty()){
                        eventlistdapter.updateData(model.data);
                        eventList.addAll(model.data);
                    }else {
                        binding.eventListRecycleView.setVisibility(View.GONE);
                        binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
                    }
                } else {
                    binding.eventListRecycleView.setVisibility(View.GONE);
                    binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
                }

            }
        });
    }


    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    public class ItemListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder( UiUtils.getViewBy( parent, R.layout.item_cm_explore_tab_design ) );
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            ViewHolder viewHolder = (ViewHolder) holder;
            RatingModel model = (RatingModel) getItem( position );
            viewHolder.mbinding.tvTabTitle.setText(model.getName());

            boolean idFound = filterList.stream().anyMatch(ids -> ids.getId() == position);
            if (!idFound) {
                viewHolder.mbinding.getRoot().setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.promoter_profile_btn_bg));
            } else {
                viewHolder.mbinding.getRoot().setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.brand_pink));
            }

            viewHolder.mbinding.getRoot().setOnClickListener( view -> {
                boolean id = filterList.stream().anyMatch(ids -> ids.getId() == position);
                if (id) {
                    viewHolder.mbinding.getRoot().setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.promoter_profile_btn_bg));
                    filterList.remove(model);
                } else {
                    viewHolder.mbinding.getRoot().setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.brand_pink));
                    filterList.clear();
                    filterList.add(model);
                }
                filterEventListData();
                notifyDataSetChanged();
            } );

        }


        public class ViewHolder extends RecyclerView.ViewHolder {

            private final ItemCmExploreTabDesignBinding mbinding;

            public ViewHolder(@NonNull View itemView) {
                super( itemView );
                mbinding = ItemCmExploreTabDesignBinding.bind( itemView );
            }
        }
    }



    // endregion
    // --------------------------------------
}