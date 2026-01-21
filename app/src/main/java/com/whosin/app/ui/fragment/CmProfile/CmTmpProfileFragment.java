package com.whosin.app.ui.fragment.CmProfile;

import static com.whosin.app.comman.AppDelegate.activity;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.FragmentCmTmpProfileBinding;
import com.whosin.app.databinding.ItemFilterCmProfileDesignBinding;
import com.whosin.app.databinding.ItemNewCategoryShapeBinding;
import com.whosin.app.databinding.ItemTicketRecyclerBinding;
import com.whosin.app.databinding.LayoutCmComplementryMygroupViewBinding;
import com.whosin.app.databinding.LayoutCmEventViewBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.RaynaTicketManager;
import com.whosin.app.service.models.CategoriesModel;
import com.whosin.app.service.models.ComplimentaryProfileModel;
import com.whosin.app.service.models.ContainerListModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.PromoterEventModel;
import com.whosin.app.service.models.RatingModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.models.newExploreModels.ExploreBlockModel;
import com.whosin.app.service.models.newExploreModels.ExploreObjectModel;
import com.whosin.app.service.models.rayna.RaynaTicketDetailModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.CmProfile.MyGroupAllUserActivity;
import com.whosin.app.ui.activites.raynaTicket.RaynaTicketListActivity;
import com.whosin.app.ui.adapter.ComplementaryEventsListAdapter;
import com.whosin.app.ui.fragment.CmProfile.CmBottomSheets.AddPlusOneGuestBottomSheet;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;


public class CmTmpProfileFragment extends BaseFragment {

    private FragmentCmTmpProfileBinding binding;

    private List<PromoterEventModel> eventList = new ArrayList<>();

    private final TicketAndEventListAdapter<RatingModel> ticketListAdapter = new TicketAndEventListAdapter<>();

    private final FilterListAdapter<RatingModel> filteristAdapter = new FilterListAdapter<>();

    private final PlusOneUserListAdapter<UserDetailModel> plusOneUserListAdapter = new PlusOneUserListAdapter<>();

    private String searchQuery = "";

    private final Runnable runnable = this::updateSerachEventList;

    private final Handler handler = new Handler();
    private String filterName = "";
    private List<PromoterEventModel> serachEventList = new ArrayList<>();
    private List<ExploreBlockModel> ticketsBlockList = new ArrayList<>();
    private List<ExploreBlockModel> ticketsCategoryList = new ArrayList<>();


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------


    @Override
    public void initUi(View view) {

        binding = FragmentCmTmpProfileBinding.bind(view);

        applyTranslations();

        requestCmProfileTicketsBlock();

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        binding.myGroupRecyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        binding.myGroupRecyclerView.setAdapter(plusOneUserListAdapter);

        requestPlusOneGroupList();

        binding.eventListNameRecyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        binding.eventListNameRecyclerView.setAdapter(ticketListAdapter);

        binding.filterRecycleView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        binding.filterRecycleView.setAdapter(filteristAdapter);


//        requestPromoterEventListUser();


    }

    @Override
    public void setListeners() {
        binding.editTvSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                searchQuery = editable.toString();
                if (!editable.toString().isEmpty()) {
                    handler.removeCallbacks(runnable);
                    handler.postDelayed(runnable, 200);
                } else {
                    filteredEventList(filterName);
                }

            }
        });

        binding.ivAddMember.setOnClickListener(view -> {
            Utils.preventDoubleClick(view);
            AddPlusOneGuestBottomSheet bottomSheet = new AddPlusOneGuestBottomSheet();
            bottomSheet.type = "MyPlusOne";
            bottomSheet.show(getChildFragmentManager(),"");
        });

        binding.seeAllConstraint.setOnClickListener(view -> {
            Utils.preventDoubleClick(view);
            startActivity(new Intent(activity, MyGroupAllUserActivity.class));
        });
    }

    @Override
    public void populateData(boolean getDataFromServer) {

    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_cm_tmp_profile;
    }

    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ComplimentaryProfileModel model) {
        requestPlusOneGroupList();
        requestCmProfileTicketsBlock();
    }

    @Override
    protected Map<View, String> getTranslationMap() {
        Map<View, String> map = new HashMap<>();
        map.put(binding.tvMyGroup, "my_plus_one");
        map.put(binding.tvSeeAllTitle, "see_all");
        map.put(binding.tvFilterTitle, "filters");
        map.put(binding.editTvSearch, "search_favorite_events");

        binding.emptyPlaceHolderView.setEmptyPlaceTxtTitle(getValue("empty_event_list"));

        return map;
    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void setPromoterGroupEventList(List<PromoterEventModel> data) {

        List<RatingModel> eventList = new ArrayList<>();
        eventList.clear();
        eventList.addAll(data.stream()
                .filter(p -> p.getVenueType().equals("venue") || p.getVenueType().equals("custom"))  // Filter both venue types
                .collect(Collectors.groupingBy(p ->
                        "venue".equals(p.getVenueType()) ? p.getVenueId() : p.getCustomVenue().getName()))  // Group by VenueId or CustomVenue name
                .entrySet().stream()
                .map(entry -> new RatingModel("event",entry.getValue()))  // Map the grouped events to RatingModel
                .collect(Collectors.toList()));

        Collections.sort(eventList, (model1, model2) -> {
            PromoterEventModel event1 = model1.getPromoterEventModelList().get(0);
            PromoterEventModel event2 = model2.getPromoterEventModelList().get(0);

            int dateComparison = event1.getDate().compareTo(event2.getDate());
            if (dateComparison != 0) {
                return dateComparison;
            }
            return event1.getStartTime().compareTo(event2.getStartTime());
        });

        setTicketEventCategory(eventList);

    }


    private void filteredEventList(String filterTitle) {
        serachEventList.clear();
        if (TextUtils.isEmpty(filterTitle)) {
            binding.eventListNameRecyclerView.setVisibility(View.VISIBLE);
            binding.emptyPlaceHolderView.setVisibility(View.GONE);
            setPromoterGroupEventList(eventList);
            return;
        }
        List<PromoterEventModel> eventFilterList = new ArrayList<>();
        if (filterTitle.equalsIgnoreCase("Near me")) {
            eventFilterList = getSortedEventsByDistance();
        } else if (filterTitle.equalsIgnoreCase("Starting Soon")) {
            eventFilterList = getSortedEventsByDateAndTime();
        } else {
            eventFilterList = eventList.stream().filter(event -> event.getCategory().equalsIgnoreCase(filterTitle)).collect(Collectors.toList());
        }


        if (!eventFilterList.isEmpty()) {
            serachEventList.clear();
            serachEventList = eventFilterList;
            setPromoterGroupEventList(eventFilterList);
            binding.eventListNameRecyclerView.setVisibility(View.VISIBLE);
            binding.emptyPlaceHolderView.setVisibility(View.GONE);
        } else {
            binding.eventListNameRecyclerView.setVisibility(View.GONE);
            binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
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

    private void updateSerachEventList() {
        List<PromoterEventModel> filteredEventList = serachEventList.stream()
                .filter(event -> {
                    String customVenueName = event.getCustomVenue() != null ? event.getCustomVenue().getName() : "";
                    String venueName = event.getVenue() != null ? event.getVenue().getName() : "";

                    return customVenueName.toLowerCase().contains(searchQuery.toLowerCase()) ||
                            venueName.toLowerCase().contains(searchQuery.toLowerCase());
                })
                .collect(Collectors.toList());

        if (!filteredEventList.isEmpty()) {
            setPromoterGroupEventList(filteredEventList);
            binding.eventListNameRecyclerView.setVisibility(View.VISIBLE);
            binding.emptyPlaceHolderView.setVisibility(View.GONE);
        } else {
            binding.eventListNameRecyclerView.setVisibility(View.GONE);
            binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);

        }

    }

    private void addFilterBasedOnEventCategory(List<PromoterEventModel> data) {
        List<RatingModel> filterList = new ArrayList<>();
        filterList.add(new RatingModel("Starting Soon"));
        filterList.add(new RatingModel("Near me"));

        List<String> categoryList = data.stream()
                .map(PromoterEventModel::getCategory)
                .filter(category -> category != null && !category.trim().isEmpty() && !"none".equalsIgnoreCase(category.trim())) // Exclude null, empty, blank, and "none"
                .distinct()
                .collect(Collectors.toList());

        if (!categoryList.isEmpty()) {
            categoryList.forEach(category -> filterList.add(new RatingModel(category)));
        }

        filteristAdapter.updateData(filterList);

    }

    private List<ExploreBlockModel> performFiltering(ExploreObjectModel data) {
        List<ExploreBlockModel> filteredList = new ArrayList<>(data.getBlocks());
        filteredList.removeIf(model -> model.getType().equals("ticket-category"));
        filteredList.removeIf(model -> {
            if (model.getType().equals("ticket")) {
                if (model.getTickets() == null || model.getTickets().isEmpty()) {
                    return true;
                }
                List<RaynaTicketDetailModel> tmpTicket = new ArrayList<>();
                model.getTickets().forEach(p -> {
                    Optional<RaynaTicketDetailModel> ticket = data.getTickets()
                            .stream()
                            .filter(v -> v.getId().equals(p))
                            .findFirst();
                    ticket.ifPresent(tmpTicket::add);
                });
                model.cmTicketList = tmpTicket;
                return tmpTicket.isEmpty();
            }
            return false;
        });

        return filteredList;
    }

    private List<ExploreBlockModel> performFilteringForCategory(ExploreObjectModel data) {
        List<ExploreBlockModel> filteredList = new ArrayList<>(data.getBlocks());
        filteredList.removeIf(model -> model.getType().equals("ticket"));
        filteredList.removeIf(model -> {
            if (model.getType().equals("ticket-category")) {
                if (model.getTicketCategories() == null || model.getTicketCategories().isEmpty()) {
                    return true;
                }
                List<CategoriesModel> tmpTicket = new ArrayList<>();
                model.getTicketCategories().forEach(p -> {
                    Optional<CategoriesModel> ticket = data.getTicketCategories()
                            .stream()
                            .filter(v -> v.getId().equals(p))
                            .findFirst();
                    ticket.ifPresent(tmpTicket::add);
                });
                model.categoryList = tmpTicket;
                return tmpTicket.isEmpty();
            }
            return false;
        });

        return filteredList;
    }

    private void setTicketEventCategory(List<RatingModel> eventList) {
        List<RatingModel> tmpEventListType = new ArrayList<>(eventList);

        List<RatingModel> ticketModel = new ArrayList<>();
        for (ExploreBlockModel exploreObjectModel : ticketsBlockList) {
            ticketModel.add(new RatingModel("ticket", exploreObjectModel));
        }

        List<RatingModel> ticketCategoryModel = new ArrayList<>();
        for (ExploreBlockModel exploreObjectModel : ticketsCategoryList) {
            ticketCategoryModel.add(new RatingModel("ticket-category", exploreObjectModel));
        }

        List<RatingModel> finalList = new ArrayList<>();
        int eventIndex = 0;
        int ticketIndex = 0;
        int categoryIndex = 0;
        int eventsSinceLastFeatured = 0;
        boolean firstFeaturedPlaced = false;
        boolean useTicketNext = true;
        Random random = new Random();

        int totalEvent = tmpEventListType.size();
        int totalTicket = ticketModel.size();
        int totalCategory = ticketCategoryModel.size();

        // Add first featured component at the start if available
        RatingModel firstFeatured = getNextFeatured(ticketModel, categoryIndex, ticketIndex, useTicketNext);
        if (firstFeatured != null) {
            finalList.add(firstFeatured);
            firstFeaturedPlaced = true;
            if ("ticket".equals(firstFeatured.getType())) {
                ticketIndex++;
                useTicketNext = false;
            } else {
                categoryIndex++;
                useTicketNext = true;
            }
        }

        if (totalEvent < 4) {
            while (eventIndex < totalEvent) {
                finalList.add(tmpEventListType.get(eventIndex++));
            }

            if (!firstFeaturedPlaced && (ticketIndex < totalTicket || categoryIndex < totalCategory)) {
                int insertPos = totalEvent > 1 ? random.nextInt(totalEvent - 1) + 1 : 1;
                RatingModel randomFeatured = getNextFeatured(ticketModel, categoryIndex, ticketIndex, useTicketNext);
                if (randomFeatured != null && insertPos < finalList.size()) {
                    finalList.add(insertPos, randomFeatured);
                    if ("ticket".equals(randomFeatured.getType())) {
                        ticketIndex++;
                        useTicketNext = false;
                    } else {
                        categoryIndex++;
                        useTicketNext = true;
                    }
                }
            }

        } else {
            while (eventIndex < totalEvent) {
                finalList.add(tmpEventListType.get(eventIndex++));
                eventsSinceLastFeatured++;

                if (eventsSinceLastFeatured > 3) {
                    eventsSinceLastFeatured = 0;

                    boolean prevIsFeatured = isFeatured(finalList.get(finalList.size() - 1));
                    if (!prevIsFeatured) {
                        RatingModel featured = getNextFeatured(ticketModel, categoryIndex, ticketIndex, useTicketNext);
                        if (featured != null) {
                            finalList.add(featured);
                            if ("ticket".equals(featured.getType())) {
                                ticketIndex++;
                                useTicketNext = false;
                            } else {
                                categoryIndex++;
                                useTicketNext = true;
                            }
                        }
                    }
                }
            }
        }

        ticketListAdapter.updateData(finalList);
    }

    private RatingModel getNextFeatured(List<RatingModel> ticketModel, int categoryIndex, int ticketIndex, boolean useTicketNext) {
        if (useTicketNext && ticketIndex < ticketModel.size()) {
            return ticketModel.get(ticketIndex);
        } else if (!useTicketNext && categoryIndex < ticketsCategoryList.size()) {
            return new RatingModel("ticket-category", ticketsCategoryList.get(categoryIndex));
        } else if (ticketIndex < ticketModel.size()) {
            return ticketModel.get(ticketIndex);
        } else if (categoryIndex < ticketsCategoryList.size()) {
            return new RatingModel("ticket-category", ticketsCategoryList.get(categoryIndex));
        }
        return null;
    }

    private boolean isFeatured(RatingModel model) {
        return "ticket".equals(model.getType()) || "ticket-category".equals(model.getType());
    }

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestCmProfileTicketsBlock() {
        showProgress();
        DataService.shared(requireActivity()).requestCmProfileTicketsBlock(new RestCallback<ContainerModel<ExploreObjectModel>>(requireActivity()) {
            @Override
            public void result(ContainerModel<ExploreObjectModel> model, String error) {
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(requireActivity(), error, Toast.LENGTH_SHORT).show();
                    hideProgress();
                    requestPromoterEventListUser();
                    return;
                }

                if (model.getData() != null) {
                    ticketsCategoryList = performFilteringForCategory(model.getData());
                    ticketsBlockList = performFiltering(model.getData());
                    requestPromoterEventListUser();
                }
            }
        });
    }


    private void requestPromoterEventListUser() {
        showProgress();
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
                    addFilterBasedOnEventCategory(model.data);
                    eventList = new ArrayList<>();
                    eventList.addAll(model.data);
                    if (!TextUtils.isEmpty(filterName)) {
                        filteredEventList(filterName);
                    } else {
                        filteredEventList("Starting Soon");
                        filterName = "Starting Soon";
                    }

//                    binding.eventListNameRecyclerView.setVisibility(View.VISIBLE);
//                    binding.emptyPlaceHolderView.setVisibility(View.GONE);
//                    setPromoterGroupEventList(model.data);
                } else {
                    binding.eventListNameRecyclerView.setVisibility(View.GONE);
                    binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
                }

            }
        });
    }

    private void requestPlusOneGroupList() {
        DataService.shared(requireActivity()).requestPromoterPlusOneMyGroup(new RestCallback<ContainerListModel<UserDetailModel>>(this) {
            @Override
            public void result(ContainerListModel<UserDetailModel> model, String error) {
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(requireActivity(), error, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (model.data != null && !model.data.isEmpty()) {
                    plusOneUserListAdapter.updateData(model.data);
                    long pendingCount = model.data.stream()
                            .filter(user -> "pending".equalsIgnoreCase(user.getPlusOneStatus()) ||
                                    "pending".equalsIgnoreCase(user.getAdminStatusOnPlusOne()))
                            .count();
                    binding.tvMyGroupCount.setText(setValue("my_plusone_count",String.valueOf(model.data.size()),String.valueOf(pendingCount)));
                }
            }
        });
    }


    // --------------------------------------
    // region Adapter
    // --------------------------------------

    private class PlusOneUserListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.layout_cm_complementry_mygroup_view));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            UserDetailModel model = (UserDetailModel) getItem(position);

            Graphics.loadImageWithFirstLetter(model.getImage(), viewHolder.mBinding.ivPlusOne, model.getFirstName());
            viewHolder.mBinding.civName.setText(model.getFirstName());

        }


        public class ViewHolder extends RecyclerView.ViewHolder {

            private final LayoutCmComplementryMygroupViewBinding mBinding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = LayoutCmComplementryMygroupViewBinding.bind(itemView);

            }
        }
    }

    private class FilterListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        private int selectedPosition = 0;

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_filter_cm_profile_design));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

            ViewHolder viewHolder = (ViewHolder) holder;
            RatingModel model = (RatingModel) getItem(position);
            viewHolder.binding.tvFilter.setText(model.getImage());

            if (position == selectedPosition) {
                viewHolder.selectedBackground();
            } else {
                viewHolder.unselectBackground();
            }

            viewHolder.itemView.getRootView().setOnClickListener(view -> {
                if (selectedPosition == position) return;
                int previousSelectedPosition = selectedPosition;
                selectedPosition = position;
                notifyItemChanged(previousSelectedPosition);
                notifyItemChanged(selectedPosition);
                filterName = model.getImage();
                filteredEventList(model.getImage());
            });

        }


        public class ViewHolder extends RecyclerView.ViewHolder {

            private final ItemFilterCmProfileDesignBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemFilterCmProfileDesignBinding.bind(itemView);
            }

            private void unselectBackground() {
                binding.mainLayout.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.event_timer_color));
                binding.subLayout.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.event_timer_color));
                binding.tvFilter.setTextColor(ContextCompat.getColor(requireActivity(), R.color.white));
            }

            private void selectedBackground() {
                binding.mainLayout.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.brand_pink));
                binding.subLayout.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.black));
                binding.tvFilter.setTextColor(ContextCompat.getColor(requireActivity(), R.color.brand_pink));
            }
        }
    }


    private class TicketAndEventListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        private static final int VIEW_TYPE_EVENT = 0;

        private static final int VIEW_TYPE_TICKET = 1;

        private static final int VIEW_TYPE_TICKET_CATEGORIES = 2;


        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == VIEW_TYPE_EVENT) {
                return new ViewHolder(UiUtils.getViewBy(parent, R.layout.layout_cm_event_view));
            } else if (viewType == VIEW_TYPE_TICKET_CATEGORIES) {
                return new TicketCategoriesBlockHolder(UiUtils.getViewBy(parent, R.layout.item_new_category_shape));
            }else {
                return new TicketHolder(UiUtils.getViewBy(parent, R.layout.item_ticket_recycler));
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            RatingModel model = (RatingModel) getItem(position);
            if (getItemViewType(position) == VIEW_TYPE_TICKET) {
                TicketHolder ticketHolder = (TicketHolder) holder;
                ticketHolder.setupData(model,model.getType());
            } else if (getItemViewType(position) == VIEW_TYPE_TICKET_CATEGORIES) {
                TicketCategoriesBlockHolder ticketHolder = (TicketCategoriesBlockHolder) holder;
                ticketHolder.setupData(model);
            } else {
                ViewHolder viewHolder2 = (ViewHolder) holder;
                viewHolder2.setRecycleView(model);
            }

        }

        public int getItemViewType(int position) {
            RatingModel model = (RatingModel) getItem(position);
            switch (model.getType()) {
                case "event":
                    return VIEW_TYPE_EVENT;
                case "ticket":
                    return VIEW_TYPE_TICKET;
                case "ticket-category":
                    return VIEW_TYPE_TICKET_CATEGORIES;
            }
            return super.getItemViewType(position);
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


        public class TicketHolder extends RecyclerView.ViewHolder {

            private final ItemTicketRecyclerBinding binding;

            public TicketHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemTicketRecyclerBinding.bind(itemView);
                binding.seeAll.setText(getValue("see_all"));
            }

            public void setupData(RatingModel ticket,String type) {
                requireActivity().runOnUiThread(() -> {
                    binding.userTitle.setText(ticket.getExploreBlockModel().getTitle());
                    binding.description.setText(ticket.getExploreBlockModel().getDescription());
                    binding.ticketRecyclerView.isVertical = false;
                    binding.ticketRecyclerView.activity = activity;
                    binding.ticketRecyclerView.setupData(ticket.getExploreBlockModel().cmTicketList, requireActivity(), false, false);
                    binding.seeAll.setOnClickListener(v -> {
                        RaynaTicketManager.shared.raynaTicketList.clear();
                        Utils.preventDoubleClick(v);
                        RaynaTicketManager.shared.raynaTicketList.addAll(ticket.getExploreBlockModel().cmTicketList);
                        Intent intent = new Intent(activity, RaynaTicketListActivity.class);
                        intent.putExtra("Description", binding.userTitle.getText().toString());
                        intent.putExtra("type", type);
                        activity.startActivity(intent);
                    });
                });
            }
        }

        public class TicketCategoriesBlockHolder extends RecyclerView.ViewHolder {

            private final ItemNewCategoryShapeBinding mBinding;

            public TicketCategoriesBlockHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemNewCategoryShapeBinding.bind(itemView);

            }

            public void setupData(RatingModel model) {
                mBinding.categoryView.isSetBottomPaddingForRecView = true;
                mBinding.categoryView.categoryType = model.getExploreBlockModel().getShape();
                requireActivity().runOnUiThread(() -> mBinding.categoryView.setUpData(requireActivity(),model.getExploreBlockModel().getShape(),model.getExploreBlockModel(),model.getExploreBlockModel().categoryList));
            }
        }

    }




}