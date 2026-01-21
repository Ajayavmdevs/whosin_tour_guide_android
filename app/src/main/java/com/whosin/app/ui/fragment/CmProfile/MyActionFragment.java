package com.whosin.app.ui.fragment.CmProfile;

import static com.whosin.app.comman.AppDelegate.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.TextUtils;
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
import com.whosin.app.databinding.FragmentMyActionBinding;
import com.whosin.app.databinding.ItemMyActionTabDesignBinding;
import com.whosin.app.databinding.LayoutCmEventViewBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.models.ComplimentaryProfileModel;
import com.whosin.app.service.models.ContainerListModel;
import com.whosin.app.service.models.PromoterEventModel;
import com.whosin.app.service.models.RatingModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.CmProfile.MyGroupAllUserActivity;
import com.whosin.app.ui.adapter.ComplementaryEventsListAdapter;
import com.whosin.app.ui.adapter.PlusOneEventListAdapter;
import com.whosin.app.ui.adapter.PlusOneUserListAdapter;
import com.whosin.app.ui.fragment.comman.BaseFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MyActionFragment extends BaseFragment {

    private FragmentMyActionBinding binding;

    private ItemListAdapter<RatingModel> itemListAdapter = new ItemListAdapter<>();

    private List<PromoterEventModel> eventList = new ArrayList<>();

    private FilterListAdapter<RatingModel> filterAdapter = new FilterListAdapter<>();

    private int selectedPosition = 0;

    private String filterName = "";

    private PlusOneEventListAdapter<PromoterEventModel> plusOneEventListAdapter;

    private PlusOneUserListAdapter<UserDetailModel> plusOneUserListAdapter;


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    public void initUi(View view) {

        binding = FragmentMyActionBinding.bind(view);

        applyTranslations();

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        binding.eventListRecycleView.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false));
        binding.eventListRecycleView.setAdapter(itemListAdapter);

        binding.eventFilterTabRecycleView.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.eventFilterTabRecycleView.setAdapter(filterAdapter);

        plusOneEventListAdapter = new PlusOneEventListAdapter<>(requireActivity());
        binding.eventRecycleView.setLayoutManager(new LinearLayoutManager(requireActivity(),LinearLayoutManager.HORIZONTAL,false));
        binding.eventRecycleView.setAdapter(plusOneEventListAdapter);

        plusOneUserListAdapter = new PlusOneUserListAdapter<>(requireActivity());
        binding.myGroupRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity(),LinearLayoutManager.HORIZONTAL,false));
        binding.myGroupRecyclerView.setAdapter(plusOneUserListAdapter);

        requestPromoterPlusOneList();
        plusOneGroupListUser();

        requestPromoterEventListUser(true);

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
        return R.layout.fragment_my_action;
    }

    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ComplimentaryProfileModel model) {
        requestPromoterEventListUser(false);
        requestPromoterPlusOneList();
        plusOneGroupListUser();
    }

    @Override
    protected Map<View, String> getTranslationMap() {
        Map<View, String> map = new HashMap<>();
        map.put(binding.tvMyGroup, "my_plus_one_group");
        map.put(binding.tvSeeAllTitle, "see_all");
        map.put(binding.tvEventTitle, "plus_one_events");

        binding.emptyPlaceHolderView.setEmptyPlaceTxtTitle(getValue("you_not_applied_any_event_yet"));

        return map;
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void setCounterForFilter() {
        List<RatingModel> filterList = new ArrayList<>();
        List<PromoterEventModel> tmpList = eventList.stream().filter(p -> p.getInvite().getInviteStatus().equals("in") && p.getInvite().getPromoterStatus().equals("accepted")).collect(Collectors.toList());
        filterList.add(new RatingModel("Events I’m In", tmpList.size()));
        List<PromoterEventModel> pending = eventList.stream().filter(p -> (p.getInvite().getInviteStatus().equals("in") && p.getInvite().getPromoterStatus().equals("pending")) && !p.isEventFull()).collect(Collectors.toList());
        filterList.add(new RatingModel("Pending Events", pending.size()));
        List<PromoterEventModel> onMyList = eventList.stream().filter(PromoterEventModel::isWishlisted).collect(Collectors.toList());
        filterList.add(new RatingModel("On My List", onMyList.size()));
        filterAdapter.updateData(filterList);
    }

    private void setfilterSelectionEvent(String tabTitle) {
        binding.txtHighLite.setText(setSectionTitle(tabTitle));
        List<PromoterEventModel> tmpList = new ArrayList<>();

        if (tabTitle.equalsIgnoreCase("Events I’m In")) {
            tmpList = eventList.stream().filter(p -> p.getInvite().getInviteStatus().equals("in") && p.getInvite().getPromoterStatus().equals("accepted")).collect(Collectors.toList());
        } else if (tabTitle.equalsIgnoreCase("Pending Events")) {
            tmpList = eventList.stream().filter(p -> (p.getInvite().getInviteStatus().equals("in") && p.getInvite().getPromoterStatus().equals("pending")) && !p.isEventFull()).collect(Collectors.toList());
        } else if (tabTitle.equalsIgnoreCase("On My List")) {
            tmpList = eventList.stream().filter(PromoterEventModel::isWishlisted).collect(Collectors.toList());
        }  else {
            tmpList = eventList;
        }


        if (!tmpList.isEmpty()) {
            setPromoterGroupEventList(tmpList);
            binding.txtHighLite.setVisibility(View.VISIBLE);
            binding.eventListRecycleView.setVisibility(View.VISIBLE);
            binding.emptyPlaceHolderView.setVisibility(View.GONE);
        } else {
            binding.eventListRecycleView.setVisibility(View.GONE);
            binding.txtHighLite.setVisibility(View.GONE);
            binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
            binding.emptyPlaceHolderView.setEmptyPlaceTxtTitle(getValue("you_not_applied_any_event_yet"));
        }

    }

    private void setPromoterGroupEventList(List<PromoterEventModel> data) {
        List<RatingModel> eventList = new ArrayList<>();
        eventList.clear();
        eventList.addAll(data.stream()
                .filter(p -> p.getVenueType().equals("venue") || p.getVenueType().equals("custom"))
                .collect(Collectors.groupingBy(p ->
                        "venue".equals(p.getVenueType()) ? p.getVenueId() : p.getCustomVenue().getName()))
                .entrySet().stream()
                .map(entry -> new RatingModel(entry.getValue()))  // Map the grouped events to RatingModel
                .collect(Collectors.toList()));
        itemListAdapter.updateData(eventList);
    }

    private String setSectionTitle(String _status) {
        if (_status.equals("Events I’m In")) {
            return "Events I'm In";
        } else if (_status.equals("Pending Events")) {
            return "Pending (Waiting admin approval)";
        } else if (_status.equals("On My List")) {
            return "Event's On my list";
        }
        return "";
    }


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestPromoterEventListUser(boolean isShowProgress) {
        eventList = new ArrayList<>();
        if (isShowProgress) {
            showProgress();
        }
        DataService.shared(requireActivity()).requestPromoterEventListUser(new RestCallback<ContainerListModel<PromoterEventModel>>(this) {
            @SuppressLint("NewApi")
            @Override
            public void result(ContainerListModel<PromoterEventModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(requireActivity(), error, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (model.data != null && !model.data.isEmpty()) {
                    binding.eventListRecycleView.setVisibility(View.VISIBLE);
                    binding.emptyPlaceHolderView.setVisibility(View.GONE);
                    model.data.removeIf(p -> "cancelled".equals(p.getStatus()) || "completed".equals(p.getStatus()));
                    if (!model.data.isEmpty()) {
                        eventList.addAll(model.data);
                        setCounterForFilter();
                        binding.txtHighLite.setVisibility(View.VISIBLE);
                        if (!TextUtils.isEmpty(filterName)) {
                            setfilterSelectionEvent(filterName);
                        } else {
                            setfilterSelectionEvent("Events I’m In");
                        }
                    } else {
                        binding.txtHighLite.setVisibility(View.GONE);
                        binding.eventListRecycleView.setVisibility(View.GONE);
                        binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
                    }
                } else {
                    binding.eventListRecycleView.setVisibility(View.GONE);
                    binding.txtHighLite.setVisibility(View.GONE);
                    binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
                }

            }
        });
    }

    private void requestPromoterPlusOneList() {
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
                    binding.tvEventTitle.setVisibility(View.VISIBLE);
                    plusOneEventListAdapter.updateData(model.data);

                } else {
                    binding.eventRecycleView.setVisibility(View.GONE);
                    binding.tvEventTitle.setVisibility(View.GONE);
                }
            }
        });
    }

    private void plusOneGroupListUser(){
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


    private class FilterListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_my_action_tab_design));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

            ViewHolder viewHolder = (ViewHolder) holder;
            RatingModel model = (RatingModel) getItem(position);

            if (model.getCount() == 0){
                viewHolder.binding.tvTabTitle.setText(model.getType());
            }else {
                viewHolder.binding.tvTabTitle.setText(model.getType() + " (" + model.getCount() + ") ");
            }


            if (position == selectedPosition) {
                viewHolder.selectedBackground();
            } else {
                viewHolder.unselectBackground();
            }

            viewHolder.binding.getRoot().setOnClickListener(v -> {
                Utils.preventDoubleClick(v);
                if (selectedPosition == position) {
                    selectedPosition = 0;
                } else {
                    selectedPosition = position;
                }
                setfilterSelectionEvent(filterAdapter.getData().get(selectedPosition).getType());
                filterName = filterAdapter.getData().get(selectedPosition).getType();
                notifyDataSetChanged();
            });


        }


        public class ViewHolder extends RecyclerView.ViewHolder {

            private final ItemMyActionTabDesignBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemMyActionTabDesignBinding.bind(itemView);
            }

            private void unselectBackground() {
                binding.mainLayout.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.white));
                binding.subLayout.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.filter_tab_color));
                binding.tvTabTitle.setTextAppearance(context, R.style.txt11Regular);
            }

            private void selectedBackground() {
                binding.mainLayout.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.brand_pink));
                binding.subLayout.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.brand_pink));
                binding.tvTabTitle.setTextAppearance(context, R.style.txt11Bold);
            }
        }
    }

    private class ItemListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.layout_cm_event_view));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            RatingModel model = (RatingModel) getItem(position);
            viewHolder.setRecycleView(model);
        }


        public class ViewHolder extends RecyclerView.ViewHolder {

            private final LayoutCmEventViewBinding mBinding;

            private ComplementaryEventsListAdapter<PromoterEventModel> cmEventListAdapter = new ComplementaryEventsListAdapter<>(requireActivity());


            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = LayoutCmEventViewBinding.bind(itemView);
                mBinding.eventRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));

            }

            private void setRecycleView(RatingModel model) {
                mBinding.eventRecyclerView.setLayoutManager(
                        new LinearLayoutManager(context, model.getPromoterEventModelList().size() == 1 ? LinearLayoutManager.VERTICAL : LinearLayoutManager.HORIZONTAL, false)
                );
                mBinding.eventRecyclerView.setAdapter(cmEventListAdapter);
                cmEventListAdapter.updateData(model.getPromoterEventModelList());
            }
        }
    }

    // endregion
    // --------------------------------------
}