package com.whosin.app.ui.fragment.Bucket.event;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.BucketEventListItemBinding;
import com.whosin.app.databinding.FragmentHistoryEventBinding;
import com.whosin.app.databinding.FragmentUpcomingEventBinding;
import com.whosin.app.databinding.FrindListItemBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.BucketEventListModel;
import com.whosin.app.service.models.ContainerListModel;
import com.whosin.app.service.models.InviteFriendModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.Profile.OtherUserProfileActivity;
import com.whosin.app.ui.activites.home.event.EventDetailsActivity;
import com.whosin.app.ui.activites.home.event.InviteGuestListBottomSheet;
import com.whosin.app.ui.fragment.comman.BaseFragment;

import java.util.List;
import java.util.stream.Collectors;


public class HistoryEventFragment extends BaseFragment {

    private FragmentHistoryEventBinding binding;
    private EventListAdapter<BucketEventListModel> eventListAdapter = new EventListAdapter<>();

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    public void initUi(View view) {
        binding = FragmentHistoryEventBinding.bind(view);
        setAdapter();
        showProgress();
        requestEvents();
    }

    @Override
    public void setListeners() {
        binding.swipeRefreshLayout.setOnRefreshListener(() -> requestEvents());
    }

    @Override
    public void populateData(boolean getDataFromServer) {

    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_history_event;
    }

    @Override
    public void onResume() {
        super.onResume();
        requestEvents();
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------
    private void setAdapter() {
        binding.historyRecycler.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false));
        binding.historyRecycler.setAdapter(eventListAdapter);

    }

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestEvents() {

        DataService.shared(requireActivity()).requestEventsHistory(new RestCallback<ContainerListModel<BucketEventListModel>>(this) {
            @Override
            public void result(ContainerListModel<BucketEventListModel> model, String error) {
                hideProgress();
                binding.swipeRefreshLayout.setRefreshing(false);
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(requireActivity(), error, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (model.data != null && !model.data.isEmpty()) {
                    binding.emptyPlaceHolderView.setVisibility(View.GONE);
                    binding.historyRecycler.setVisibility(View.VISIBLE);
                    List<BucketEventListModel> eventHistory = model.data.stream().filter(p -> p.getVenue() != null).collect(Collectors.toList());
                    if (!eventHistory.isEmpty()){
                        eventListAdapter.updateData(eventHistory);
                    }
                } else {
                    binding.historyRecycler.setVisibility(View.GONE);
                    binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    public class EventListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new EventListAdapter.ViewHolder(UiUtils.getViewBy(parent, R.layout.event_list_item));

        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            EventListAdapter.ViewHolder viewHolder = (EventListAdapter.ViewHolder) holder;
            BucketEventListModel model = (BucketEventListModel) getItem(position);

            if (model != null) {

                if (model.getVenue() != null && model.getVenue().getName() != null) {
                    viewHolder.mBinding.layoutVenue.setVisibility(View.VISIBLE);
                    viewHolder.mBinding.txtUserName.setText(model.getVenue().getName());
                    viewHolder.mBinding.tvAddress.setText(model.getVenue().getAddress());
                    Graphics.loadImageWithFirstLetter(model.getVenue().getLogo(), viewHolder.mBinding.imgUserLogo, model.getVenue().getName());
                }
                else {
                    viewHolder.mBinding.layoutVenue.setVisibility(View.GONE);

                }

                viewHolder.mBinding.tvDescription.setText(model.getDescription());
                viewHolder.mBinding.txtEventName.setText(model.getTitle());
                Graphics.loadImage(model.getImage(), viewHolder.mBinding.ivCover);

                if (model.getOrg() != null) {
                    viewHolder.mBinding.txtOrgName.setText(model.getOrg().getName());
                    Graphics.loadImageWithFirstLetter(model.getOrg().getLogo(), viewHolder.mBinding.imgOrgImage, model.getOrg().getName());
                }

                if (model.getInvitedUsers() != null && !model.getInvitedUsers().isEmpty()) {
                    InviteFriendModel invitedUser = model.getInvitedUsers().stream().filter(model1 -> model1.getUserId().equals(SessionManager.shared.getUser().getId())).findFirst().orElse(null);

                    if (model.getInvitedUsers().stream().anyMatch(model1 -> model1.getUserId().equals(SessionManager.shared.getUser().getId()))) {

                        if (invitedUser != null) {
                            if (model.getInvitedUsers().size() > 1) {
                                model.getInvitedUsers().remove(invitedUser);
                                model.getInvitedUsers().add(0, invitedUser);
                            }
                        }
                        model.getInvitedUsers().removeIf(item -> item.getUser() == null);
                        viewHolder.eventUserListAdapter.updateData(model.getInvitedUsers());
                    }
                    viewHolder.mBinding.txtDate.setText(Utils.convertMainDateFormat(model.getEventTime()));
                    viewHolder.mBinding.txtTime.setText(Utils.convertMainTimeFormat(model.getReservationTime()) + " - " + Utils.convertMainTimeFormat(model.getEventTime()));

                    if (model.getEvent_status().equals("completed")) {
                        viewHolder.mBinding.txtStatus.setText("completed");
                        viewHolder.mBinding.layoutStatus.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.in_green));
                        viewHolder.mBinding.constraint.setBackground(ContextCompat.getDrawable(context, R.drawable.stroke_gradiant_line_in));
                    } else if (model.getEvent_status().equals("cancelled")) {
                        viewHolder.mBinding.txtStatus.setText("cancelled");
                        viewHolder.mBinding.layoutStatus.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.status_pink));
                        viewHolder.mBinding.constraint.setBackground(ContextCompat.getDrawable(context, R.drawable.event_stroke_gradiant_line_out));
                    } else {
                        if (invitedUser.getInviteStatus().equals("pending")) {
                            viewHolder.mBinding.txtStatus.setText("pending");
                            viewHolder.mBinding.layoutStatus.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.pending_yellow));
                            viewHolder.mBinding.constraint.setBackground(ContextCompat.getDrawable(context, R.drawable.stroke_gradiant_line_pending));
                        } else if (invitedUser.getInviteStatus().equals("in")) {
                            viewHolder.mBinding.txtStatus.setText("in");
                            viewHolder.mBinding.layoutStatus.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.in_green));
                            viewHolder.mBinding.constraint.setBackground(ContextCompat.getDrawable(context, R.drawable.stroke_gradiant_line_in));
                        } else {
                            viewHolder.mBinding.txtStatus.setText(invitedUser.getInviteStatus());
                            viewHolder.mBinding.layoutStatus.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.status_pink));
                            viewHolder.mBinding.constraint.setBackground(ContextCompat.getDrawable(context, R.drawable.event_stroke_gradiant_line_out));
                        }
                    }
                }

                viewHolder.mBinding.getRoot().setOnClickListener(view -> {
                    String orgName = (model.getOrg() != null && model.getOrg().getName() != null) ? model.getOrg().getName() : "";
                    String orgWebsite = (model.getOrg() != null && model.getOrg().getWebsite() != null) ? model.getOrg().getWebsite() : "";
                    String orgLogo = (model.getOrg() != null && model.getOrg().getLogo() != null) ? model.getOrg().getLogo() : "";

                    startActivity(new Intent(context, EventDetailsActivity.class)
                            .putExtra("eventsList", new Gson().toJson(model))
                            .putExtra("name", orgName)
                            .putExtra("address", orgWebsite)
                            .putExtra("image", orgLogo)
                    );
                });
            }

        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private BucketEventListItemBinding mBinding;
            private EventUserListAdapter eventUserListAdapter;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = BucketEventListItemBinding.bind(itemView);
                eventUserListAdapter = new EventUserListAdapter<>();
                mBinding.friendRecycler.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false));
                mBinding.friendRecycler.setAdapter(eventUserListAdapter);
                mBinding.friendRecycler.setNestedScrollingEnabled(false);
            }
        }
    }

    public class EventUserListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.frind_list_item));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            InviteFriendModel model = (InviteFriendModel) getItem(position);

            if (model != null) {
                if (model.getUser() != null) {
                    viewHolder.mBinding.txtUserName.setText(model.getUser().getFirstName());
                    Graphics.loadImageWithFirstLetter(model.getUser().getImage(), viewHolder.mBinding.imgUserLogo, model.getUser().getFirstName());
                }

                if (model.getInviteStatus().equals("pending")) {
                    viewHolder.mBinding.iconStatus.setImageResource(R.drawable.icon_pending);
                } else if (model.getInviteStatus().equals("in")) {
                    viewHolder.mBinding.iconStatus.setImageResource(R.drawable.icon_complete);
                } else if (model.getInviteStatus().equals("out")) {
                    viewHolder.mBinding.iconStatus.setImageResource(R.drawable.icon_deleted);
                }

                viewHolder.itemView.setOnClickListener(view -> {
                    Utils.preventDoubleClick( view );
                    InviteGuestListBottomSheet inviteGuestListBottomSheet = new InviteGuestListBottomSheet();
                    inviteGuestListBottomSheet.eventId = model.getEventId();
                    inviteGuestListBottomSheet.show(getChildFragmentManager(), "");                });
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private FrindListItemBinding mBinding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = FrindListItemBinding.bind(itemView);
            }
        }
    }


    // --------------------------------------
    // endregion

}