package com.whosin.app.ui.fragment.Promoter;

import static android.app.Activity.RESULT_OK;
import static android.view.View.VISIBLE;
import static com.whosin.app.comman.AppConstants.TabOption.valueOf;
import static com.whosin.app.comman.AppDelegate.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.whosin.app.R;
import com.whosin.app.comman.AppConstants;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.FragmentPromoterMyEventsBinding;
import com.whosin.app.databinding.ItemMyEventBinding;
import com.whosin.app.databinding.ItemPromoterEventTabBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.PromoterProfileManager;
import com.whosin.app.service.models.ContainerListModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.InvitedUserModel;
import com.whosin.app.service.models.PromoterEventModel;
import com.whosin.app.service.models.PromoterNewEventDetailModel;
import com.whosin.app.service.models.PromoterNewEventListModel;
import com.whosin.app.service.models.RatingModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.models.VenueObjectModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.Promoter.ComplementaryEventDetailActivity;
import com.whosin.app.ui.activites.Promoter.PromoterCreateEventActivity;
import com.whosin.app.ui.activites.venue.VenueShareActivity;
import com.whosin.app.ui.fragment.comman.BaseFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import retrofit2.Call;

public class PromoterMyEventsFragment extends BaseFragment {

    private FragmentPromoterMyEventsBinding binding;

    private final EventsAdapter<PromoterNewEventDetailModel> eventsAdapter = new EventsAdapter<PromoterNewEventDetailModel>();

    private MyEventsTabAdapetr<RatingModel> myEventsTabAdapter = new MyEventsTabAdapetr<>();

    private List<PromoterNewEventDetailModel> eventList = new ArrayList<>();

    private List<InvitedUserModel> circlesList = new ArrayList<>();

    private List<VenueObjectModel> venuesList = new ArrayList<>();

    private List<InvitedUserModel> usersListList = new ArrayList<>();

    private Call<ContainerModel<PromoterNewEventListModel>> service = null;

    private int page = 1;

    private boolean isLoading = false;

    private String filterName = "Last Added";

    private Handler handler = new Handler();
    private String searchQuery = "";
    private String sortBy = "";
    private Runnable runnable = () -> requestPromoterEventList(false,false);

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    public void initUi(View view) {
        binding = FragmentPromoterMyEventsBinding.bind(view);

        binding.emptyPlaceHolderView.setEmptyPlaceTxtTitle(getValue("empty_event_list"));

        binding.eventsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        binding.eventsRecyclerView.setAdapter(eventsAdapter);


        ArrayList<RatingModel> tab = new ArrayList<>();
        tab.add(new RatingModel("Last Added"));
        tab.add(new RatingModel("Starting Soon"));
        binding.eventTabRecycleView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.eventTabRecycleView.setAdapter(myEventsTabAdapter);
        myEventsTabAdapter.updateData(tab);

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        requestPromoterEventList(true, false);


    }

    @Override
    public void setListeners() {
        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            if (!eventList.isEmpty()) eventList.clear();
            page = 1;
            requestPromoterEventList(false, false);
        });

        binding.eventsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) {
                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) binding.eventsRecyclerView.getLayoutManager();
                    assert linearLayoutManager != null;
                    int lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();

                    if (lastVisibleItemPosition != RecyclerView.NO_POSITION) {
                        if (lastVisibleItemPosition == eventsAdapter.getData().size() - 1) {
                            if (!isLoading && eventsAdapter.getData().size() % 20 == 0 && !eventsAdapter.getData().isEmpty()) {
                                isLoading = true;
                                page++;
                                requestPromoterEventList(false, true);
                            }
                        }
                    }
                }
            }
        });

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
                    if (page == 1) eventList.clear();
                    handler.removeCallbacks(runnable);
                    handler.postDelayed(runnable, 200);
                } else {
                    searchQuery = "";
                    requestPromoterEventList(false,false);
                }

            }
        });


    }



    @Override
    public void populateData(boolean getDataFromServer) {

    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_promoter_my_events;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UserDetailModel event) {
        if (PromoterProfileManager.shared.promoterProfileModel != null && PromoterProfileManager.shared.promoterProfileModel.getProfile() != null) {
            binding.headerView.setUpData(requireActivity(), PromoterProfileManager.shared.promoterProfileModel.getProfile());
        }
        if (!eventList.isEmpty()) eventList.clear();
        page = 1;
        requestPromoterEventList(false, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (PromoterProfileManager.shared.promoterProfileModel != null && PromoterProfileManager.shared.promoterProfileModel.getProfile() != null) {
            binding.headerView.setUpData(requireActivity(), PromoterProfileManager.shared.promoterProfileModel.getProfile());
        }
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private VenueObjectModel getVenueObjectModel(String id) {
        return venuesList.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElse(new VenueObjectModel());  
    }
    
    private InvitedUserModel getUserDetailModel(String userId) {
        return usersListList.stream()
                .filter(p -> p.getId().equals(userId))
                .findFirst()
                .orElse(new InvitedUserModel()); 
    }

    private InvitedUserModel getCircleDetailModel(String id) {
        return circlesList.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElse(new InvitedUserModel());
    }

    private PromoterEventModel getPromoterEventModel(PromoterNewEventDetailModel model) {
        List<InvitedUserModel> circlesList = new ArrayList<>();
        if (model.getInvitedCircles() != null && !model.getInvitedCircles().isEmpty()) {
            for (String st : model.getInvitedCircles()) {
                InvitedUserModel model1 = getCircleDetailModel(st);
                circlesList.add(model1);
            }
        }

        Gson gson = new Gson();
        String jsonString = gson.toJson(model);

        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
        jsonObject.remove("invitedCircles");
        jsonObject.add("invitedCircles", gson.toJsonTree(circlesList));
        return gson.fromJson(jsonObject, PromoterEventModel.class);
    }



    private void onApiCallComplete() {
        isLoading = false;
    }


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestPromoterEventList(boolean isShowProgress, boolean isPaggination) {
        if (service != null) {
            service.cancel();
        }
        sortBy = filterName.equalsIgnoreCase("Last Added") ? "" : "startingSoon";
        if (isShowProgress) {
            if (!eventList.isEmpty()) eventList.clear();
            page = 1;
            showProgress();
        } else if (isPaggination) {
            binding.progressBar.setVisibility(VISIBLE);
        } else {
            if (!eventList.isEmpty()) eventList.clear();
            page = 1;
            binding.swipeRefreshLayout.setRefreshing(true);
        }
        service = DataService.shared(requireActivity()).requestPromoterEventList(searchQuery,page,sortBy, new RestCallback<ContainerModel<PromoterNewEventListModel>>(this) {
            @Override
            public void result(ContainerModel<PromoterNewEventListModel> model, String error) {
                hideProgress();
                binding.swipeRefreshLayout.setRefreshing(false);
                binding.progressBar.setVisibility(View.GONE);
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    onApiCallComplete();
                    return;
                }

                onApiCallComplete();



                if (model.data != null) {
                    eventList.addAll(model.data.getEvents());
                    venuesList.addAll(model.data.getVenues());
                    usersListList.addAll(model.data.getUsers());
                    circlesList.addAll(model.data.getCircles());

                }

                eventList.stream().filter(p -> p.getVenueType().equals("venue")).forEach(p -> {VenueObjectModel matchedUser = getVenueObjectModel(p.getVenueId());if (matchedUser != null) {p.setVenue(matchedUser);}});
                if (!eventList.isEmpty()) eventsAdapter.updateData(eventList);
                binding.eventsRecyclerView.setVisibility(eventsAdapter.getData().isEmpty() ? View.GONE : VISIBLE);
                binding.emptyPlaceHolderView.setVisibility(eventsAdapter.getData().isEmpty() ? VISIBLE : View.GONE);
            }
        });
    }

    private void requestPromoterEventCancel(String id, ItemMyEventBinding vBinding, boolean allEventDelete) {
        vBinding.btnCancelProgressView.setVisibility(View.VISIBLE);
        vBinding.cancelButtonTitle.setVisibility(View.INVISIBLE);
        DataService.shared(requireActivity()).requestPromoterEventCancel(id, allEventDelete, new RestCallback<ContainerModel<PromoterEventModel>>(this) {
            @Override
            public void result(ContainerModel<PromoterEventModel> model, String error) {
                vBinding.btnCancelProgressView.setVisibility(View.GONE);
                vBinding.cancelButtonTitle.setVisibility(View.INVISIBLE);
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (model.status == 1) {
//                    eventsAdapter.getData().removeIf(p -> p.getId().equals(id));
//                    eventsAdapter.notifyDataSetChanged();
                    requestPromoterEventList(false,false);
                }

            }
        });
    }

    private void requestPromoterEventComplete(String id, ItemMyEventBinding vBinding) {
        vBinding.btnCompleteProgressView.setVisibility(View.VISIBLE);
        vBinding.completeButtonTitle.setVisibility(View.INVISIBLE);
        DataService.shared(requireActivity()).requestPromoterEventComplete(id, new RestCallback<ContainerModel<PromoterEventModel>>(this) {
            @Override
            public void result(ContainerModel<PromoterEventModel> model, String error) {
                vBinding.btnCompleteProgressView.setVisibility(View.GONE);
                vBinding.completeButtonTitle.setVisibility(View.INVISIBLE);
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (model.status == 1) {
                    eventsAdapter.getData().removeIf(p -> p.getId().equals(id));
                    eventsAdapter.notifyDataSetChanged();
//                    requestPromoterEventList(false);
                }

            }
        });
    }


    // --------------------------------------
    // region Adapter
    // --------------------------------------


    private class EventsAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_my_event));
        }

        @SuppressLint({"SetTextI18n", "NewApi"})
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            PromoterNewEventDetailModel model = (PromoterNewEventDetailModel) getItem(position);

            boolean isLastItem = getItemCount() - 1 == position;

            if (model == null) {
                return;
            }

            viewHolder.vBinding.cancelButtonTitle.setText(getValue("cancel"));
            viewHolder.vBinding.completeButtonTitle.setText(getValue("complete"));
            viewHolder.vBinding.txtEventStatus.setText(getValue("cancelled"));
            viewHolder.vBinding.userInvitedView.setTitle(getValue("users_invited"));
            viewHolder.vBinding.userCircledView.setTitle(getValue("circles_invited"));
            viewHolder.vBinding.plusOneMemberView.setTitle(getValue("plus_one_members"));
            viewHolder.vBinding.userIntersetdView.setTitle(getValue("interested_users"));
//            viewHolder.vBinding.cancelledUsers.setTitle(getValue("cancelled_users"));

            viewHolder.vBinding.userInvitedView.setVisibility(View.GONE);
            viewHolder.vBinding.userCircledView.setVisibility(View.GONE);
            viewHolder.vBinding.userSeatsdView.setVisibility(View.GONE);
            viewHolder.vBinding.userIntersetdView.setVisibility(View.GONE);
            viewHolder.vBinding.plusOneMemberView.setVisibility(View.GONE);

            viewHolder.vBinding.txtEventStatus.setVisibility(View.GONE);
            viewHolder.vBinding.btnEventCancel.setVisibility(View.GONE);
            viewHolder.vBinding.btnEventComplete.setVisibility(View.GONE);
            viewHolder.vBinding.cancelButtonTitle.setVisibility(View.GONE);
            viewHolder.vBinding.completeButtonTitle.setVisibility(View.GONE);
            viewHolder.vBinding.editIcon.setVisibility(View.VISIBLE);

            requireActivity().runOnUiThread(() -> {
                if (model.getStatus().equals("cancelled")) {
                    viewHolder.vBinding.editIcon.setVisibility(View.GONE);
                    viewHolder.vBinding.txtEventStatus.setVisibility(View.VISIBLE);
                } else if (model.getStatus().equals("in-progress")) {
                    viewHolder.vBinding.editIcon.setVisibility(VISIBLE);
                    viewHolder.vBinding.btnEventComplete.setVisibility(View.VISIBLE);
                    viewHolder.vBinding.completeButtonTitle.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.vBinding.btnEventCancel.setVisibility(View.VISIBLE);
                    viewHolder.vBinding.cancelButtonTitle.setVisibility(View.VISIBLE);
                }
            });

            if (model.getVenueType().equals("venue")) {
                if (model.getVenue() != null) {
                    Graphics.loadImageWithFirstLetter(model.getVenue().getLogo(), viewHolder.vBinding.eventIv, model.getVenue().getName());
                    viewHolder.vBinding.titleText.setText(model.getVenue().getName());
                }

            } else {
                if (model.getCustomVenue() != null) {
                    viewHolder.vBinding.titleText.setText(model.getCustomVenue().getName());
                    Graphics.loadImageWithFirstLetter(model.getCustomVenue().getImage(), viewHolder.vBinding.eventIv, model.getCustomVenue().getName());
                }
            }

            String time = model.getStartTime() + " - " + model.getEndTime();
            viewHolder.vBinding.tvDateTime.setText(Utils.changeDateFormat(model.getDate(), AppConstants.DATEFORMAT_SHORT, AppConstants.DATEFORMT_MM_DATE) + " | " + time);
            viewHolder.vBinding.tvMaxInvite.setText(String.format("%d seats", model.getMaxInvitee()));

            if (model.getInvitedUsers() != null && !model.getInvitedUsers().isEmpty()) {
                 List<InvitedUserModel>  invitedUsers = model.getInvitedUsers().stream()
                        .map(tmp -> getUserDetailModel(tmp.getUserId()))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                viewHolder.vBinding.userInvitedView.getTotalInvitedUsers = model.getTotalInvitedUsers();
                viewHolder.vBinding.userInvitedView.setVisibility(View.VISIBLE);
                viewHolder.vBinding.userInvitedView.isOpenProfile = true;
                viewHolder.vBinding.userInvitedView.setUpData(invitedUsers, requireActivity(), getChildFragmentManager(), false, false);
            }

            if (model.getInvitedCircles() != null && !model.getInvitedCircles().isEmpty()) {
               List<InvitedUserModel> circlesList = new ArrayList<>();
               for (String st : model.getInvitedCircles()){
                   InvitedUserModel model1 = getCircleDetailModel(st);
                   circlesList.add(model1);
               }
                viewHolder.vBinding.userCircledView.setVisibility(View.VISIBLE);
                viewHolder.vBinding.userCircledView.setUpData(circlesList, requireActivity(), getChildFragmentManager(), true, false);
            }

            if (model.getInMembers() != null && !model.getInMembers().isEmpty()) {
                List<InvitedUserModel>  inMember = model.getInMembers().stream()
                        .map(tmp -> getUserDetailModel(tmp.getUserId()))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                viewHolder.vBinding.userSeatsdView.maxInvitee = model.getMaxInvitee();
                viewHolder.vBinding.userSeatsdView.totalInMembers = model.getTotalInMembers();
                viewHolder.vBinding.userSeatsdView.isOpenProfile = true;
                viewHolder.vBinding.userSeatsdView.setUpData(inMember, requireActivity(), getChildFragmentManager(), false, true);
                viewHolder.vBinding.userSeatsdView.setVisibility(View.VISIBLE);
            }

            if (model.isConfirmationRequired() && model.getInterestedMembers() != null && !model.getInterestedMembers().isEmpty()) {
                List<InvitedUserModel>  interestedMember = model.getInterestedMembers().stream()
                        .map(tmp -> getUserDetailModel(tmp.getUserId()))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                viewHolder.vBinding.userIntersetdView.maxInvitee = model.getMaxInvitee();
                viewHolder.vBinding.userIntersetdView.getTotalInvitedUsers = model.getTotalInterestedMembers();
                viewHolder.vBinding.userIntersetdView.isOpenProfile = true;
                viewHolder.vBinding.userIntersetdView.setUpData(interestedMember, requireActivity(), getChildFragmentManager(), false, false);
                viewHolder.vBinding.userIntersetdView.setVisibility(View.VISIBLE);
            }

            if (model.getPlusOneMembers() != null && !model.getPlusOneMembers().isEmpty()) {
                List<InvitedUserModel>  plusOneMember = model.getPlusOneMembers().stream()
                        .map(tmp -> getUserDetailModel(tmp.getUserId()))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                viewHolder.vBinding.plusOneMemberView.maxInvitee = model.getMaxInvitee();
                viewHolder.vBinding.plusOneMemberView.getTotalInvitedUsers = model.getPlusOneMembers().size();
                viewHolder.vBinding.plusOneMemberView.isOpenProfile = true;
                viewHolder.vBinding.plusOneMemberView.setUpData(plusOneMember, requireActivity(), getChildFragmentManager(), false, false);
                viewHolder.vBinding.plusOneMemberView.setVisibility(View.VISIBLE);
            }

            if (model.isConfirmationRequired()) {
                viewHolder.vBinding.confirmTv.setText(getValue("show_interest"));
                viewHolder.vBinding.confirmTv.setTextColor(ContextCompat.getColor(requireActivity(), R.color.amber_color));
            } else {
                viewHolder.vBinding.confirmTv.setText(getValue("confirmed"));
                viewHolder.vBinding.confirmTv.setTextColor(ContextCompat.getColor(requireActivity(), R.color.confrim_green));
            }

            if (isLastItem) {
                int marginBottom = Utils.getMarginBottom(holder.itemView.getContext(), 0.12f);
                Utils.setBottomMargin(holder.itemView, marginBottom);
            } else {
                Utils.setBottomMargin(holder.itemView, 0);
            }

            viewHolder.vBinding.editIcon.setOnClickListener(view -> {
                Utils.preventDoubleClick(view);
                ArrayList<String> data = new ArrayList<>();
                data.add(getValue("edit"));
                data.add(getValue("share"));
                Graphics.showActionSheet(requireActivity(), getString(R.string.app_name), getValue("close"),data, (data1, position1) -> {
                    switch (position1) {
                        case 0:
                            PromoterProfileManager.shared.isEventEdit = true;
                            PromoterProfileManager.shared.promoterEventModel = getPromoterEventModel(model);
                            Intent intent = new Intent(requireActivity(), PromoterCreateEventActivity.class);
                            activityLauncher.launch(intent, result -> {
                                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                                    boolean isReload = result.getData().getBooleanExtra("isReload", false);
                                    if (isReload) {
                                        requestPromoterEventList(false, false);
                                    }
                                }
                            });

                            break;
                        case 1:
                            startActivity( new Intent( activity, VenueShareActivity.class )
                                    .putExtra( "promoterEvent", new Gson().toJson( model ) )
                                    .putExtra( "type", "promoterEvent" ) );
                            break;
                    }
                });
            });

            viewHolder.vBinding.btnEventComplete.setOnClickListener(v -> {
                Utils.preventDoubleClick(v);
                requestPromoterEventComplete(model.getId(), viewHolder.vBinding);
            });

            viewHolder.vBinding.btnEventCancel.setOnClickListener(view -> {
                Utils.preventDoubleClick(view);

                String deleteEvent = getValue("delete_event");
                String deleteCurrentEvent = getValue("delete_current_event");
                String deleteAllEvent = getValue("delete_all_event");
                String cancel = getValue("cancel");

                Graphics.showAlertDialogForDeleteEvent(activity,
                        deleteEvent,
                        getValue("event_cancellation_confirm_alert"),
                        deleteCurrentEvent,
                        cancel,
                        deleteAllEvent,
                        action -> {

                            if (action.equals(deleteCurrentEvent)) {
                                Graphics.showAlertDialogWithOkCancel(requireActivity(),
                                        getValue("confirm_delete"),
                                        getValue("are_you_sure_cancel_this_event"),
                                        isConfirmed -> {
                                            if (isConfirmed) {
                                                requestPromoterEventCancel(model.getId(), viewHolder.vBinding,false);
                                            }
                                        });

                            } else if (action.equals(deleteAllEvent)) {
                                Graphics.showAlertDialogWithOkCancel(requireActivity(),
                                        getValue("confirm_delete"),
                                        getValue("are_you_sure_cancel_all_recurring_events"),
                                        isConfirmed -> {
                                            if (isConfirmed) {
                                                requestPromoterEventCancel(model.getId(), viewHolder.vBinding,true);
                                            }
                                        });

                            } else if (action.equals(cancel)) {
                                // do nothing, just close
                            }
                        });
            });


            viewHolder.vBinding.getRoot().setOnClickListener(v -> {
                Utils.preventDoubleClick(v);
                Intent intent = new Intent(requireActivity(), ComplementaryEventDetailActivity.class);
                intent.putExtra("eventId", model.getId());
                intent.putExtra("type", "Promoter");
                startActivity(intent);
//                activityLauncher.launch(intent, result -> {
//                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
//                        boolean isReload = result.getData().getBooleanExtra("isReload", false);
//                        if (isReload) {
//                            requestPromoterEventList(false);
//                        }
//                    }
//                });
            });


        }


        public class ViewHolder extends RecyclerView.ViewHolder {

            private final ItemMyEventBinding vBinding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                vBinding = ItemMyEventBinding.bind(itemView);
            }
        }
    }

    private class MyEventsTabAdapetr<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        private int selectedPosition = 0;

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_promoter_event_tab));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            RatingModel model = (RatingModel) getItem(position);
            viewHolder.vBinding.tvTabTitle.setText(model.getImage());

            viewHolder.vBinding.tabBackground.setBackgroundColor(ContextCompat.getColor(activity, position == selectedPosition ? R.color.brand_pink :
                    R.color.promoter_profile_btn_bg));


            viewHolder.vBinding.getRoot().setOnClickListener(view -> {
                int previousPosition = selectedPosition;
                selectedPosition = position;
                filterName = model.getImage();
                requestPromoterEventList(false,false);
                notifyItemChanged(previousPosition);
                notifyItemChanged(selectedPosition);

            });
        }


        public class ViewHolder extends RecyclerView.ViewHolder {

            private ItemPromoterEventTabBinding vBinding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                vBinding = ItemPromoterEventTabBinding.bind(itemView);
            }
        }
    }


    // endregion
    // --------------------------------------
}