package com.whosin.app.ui.fragment.Promoter;

import static android.view.View.VISIBLE;
import static com.whosin.app.comman.AppDelegate.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.whosin.app.R;
import com.whosin.app.comman.AppConstants;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.FragmentPromoterEventHistoryBinding;
import com.whosin.app.databinding.ItemMyEventBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.PromoterProfileManager;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.CommanSearchModel;
import com.whosin.app.service.models.ContainerListModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.PromoterEventModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.Promoter.ComplementaryEventDetailActivity;
import com.whosin.app.ui.activites.Promoter.PromoterCreateEventActivity;
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
import java.util.stream.Collectors;

import retrofit2.Call;


public class PromoterEventHistoryFragment extends BaseFragment {

    private FragmentPromoterEventHistoryBinding binding;

    private final EventsAdapter<PromoterEventModel> eventsAdapter = new EventsAdapter<>();

    private List<PromoterEventModel> eventHistory  = new ArrayList<>();

    private int page = 1;

    private boolean isLoading = false;

    private Handler handler = new Handler();
    private String searchQuery = "";
    private Runnable runnable = () -> requestPromoterEventHistory(false,false);

    private Call<ContainerListModel<PromoterEventModel>> service = null;


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    public void initUi(View view) {
        binding = FragmentPromoterEventHistoryBinding.bind(view);

        binding.emptyPlaceHolderView.setEmptyPlaceTxtTitle(getValue("empty_event_history"));

        binding.eventsHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        binding.eventsHistoryRecyclerView.setAdapter(eventsAdapter);

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

//        binding.swipeRefreshLayout.setProgressViewOffset(false,0,220);

        if (PromoterProfileManager.shared.promoterProfileModel != null && PromoterProfileManager.shared.promoterProfileModel.getProfile() != null) {
            binding.headerView.setUpData(requireActivity(), PromoterProfileManager.shared.promoterProfileModel.getProfile());
        }


        requestPromoterEventHistory(true,false);
    }

    @Override
    public void setListeners() {
        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            if (!eventHistory.isEmpty()) eventHistory.clear();
            page = 1;
            requestPromoterEventHistory(false,false);
        });

        binding.eventsHistoryRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) {
                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) binding.eventsHistoryRecyclerView.getLayoutManager();
                    assert linearLayoutManager != null;
                    int lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();

                    if (lastVisibleItemPosition != RecyclerView.NO_POSITION) {
                        if (lastVisibleItemPosition == eventsAdapter.getData().size() - 1) {
                            if (!isLoading && eventsAdapter.getData().size() % 20 == 0 && !eventsAdapter.getData().isEmpty()) {
                                isLoading = true;
                                page++;
                                requestPromoterEventHistory(false,true);
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
                    if (page == 1) eventHistory.clear();
                    handler.removeCallbacks(runnable);
                    handler.postDelayed(runnable, 200);
                } else {
                    searchQuery = "";
                    requestPromoterEventHistory(false,false);
                }
            }
        });


    }


    @Override
    public void populateData(boolean getDataFromServer) {

    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_promoter_event_history;
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
        if (!eventHistory.isEmpty()) eventHistory.clear();
        page = 1;
        requestPromoterEventHistory(false,false);
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

    private void onApiCallComplete() {
        isLoading = false;
    }

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------


    private void requestPromoterEventHistory(boolean isShowProgress,boolean isPagination) {
        if (service != null) {
            service.cancel();
        }
        if (isShowProgress) {
            showProgress();
        } else if (isPagination) {
            binding.progressBar.setVisibility(VISIBLE);
        } else {
            binding.swipeRefreshLayout.setRefreshing(true);
        }
        service = DataService.shared(requireActivity()).requestPromoterEventHistory(searchQuery,page, new RestCallback<ContainerListModel<PromoterEventModel>>(this) {
            @Override
            public void result(ContainerListModel<PromoterEventModel> model, String error) {
                hideProgress();
                binding.progressBar.setVisibility(View.GONE);
                binding.swipeRefreshLayout.setRefreshing(false);
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(requireActivity(), error, Toast.LENGTH_SHORT).show();
                    onApiCallComplete();
                    return;
                }

                onApiCallComplete();
                if (model.data != null && !model.data.isEmpty()) {
                    eventHistory.addAll(model.data);
                }
                if (!eventHistory.isEmpty()) eventsAdapter.updateData(eventHistory);
                binding.eventsHistoryRecyclerView.setVisibility(eventsAdapter.getData().isEmpty() ? View.GONE : VISIBLE);
                binding.emptyPlaceHolderView.setVisibility(eventsAdapter.getData().isEmpty() ? VISIBLE : View.GONE);
            }
        });
    }

    private void requestPromoterEventDelete(String id) {
        showProgress();
        DataService.shared(requireActivity()).requestPromoterEventDelete(id, new RestCallback<ContainerModel<PromoterEventModel>>(this) {
            @Override
            public void result(ContainerModel<PromoterEventModel> model, String error) {
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    hideProgress();
                    Toast.makeText(requireActivity(), error, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (model.status == 1) {
                    Toast.makeText(requireActivity(), model.message, Toast.LENGTH_SHORT).show();
                    eventsAdapter.getData().removeIf(p -> p.getId().equals(id));
                    eventsAdapter.notifyDataSetChanged();
                    hideProgress();
                }
            }
        });
    }

    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    private class EventsAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_my_event));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            PromoterEventModel model = (PromoterEventModel) getItem(position);

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

            viewHolder.vBinding.confirmTv.setVisibility(View.GONE);
            viewHolder.vBinding.btnEventCancel.setVisibility(View.GONE);
            viewHolder.vBinding.txtEventStatus.setVisibility(View.GONE);
            viewHolder.vBinding.userIntersetdView.setVisibility(View.GONE);


            viewHolder.vBinding.userInvitedView.setVisibility(View.GONE);
            viewHolder.vBinding.userCircledView.setVisibility(View.GONE);
            viewHolder.vBinding.userSeatsdView.setVisibility(View.GONE);
            viewHolder.vBinding.plusOneMemberView.setVisibility(View.GONE);

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
                viewHolder.vBinding.userInvitedView.getTotalInvitedUsers = model.getTotalInvitedUsers();
                viewHolder.vBinding.userInvitedView.setVisibility(View.VISIBLE);
                viewHolder.vBinding.userInvitedView.isOpenProfile = true;
                viewHolder.vBinding.userInvitedView.isFromHistoryEvent = true;
                viewHolder.vBinding.userInvitedView.setUpData(model.getInvitedUsers(), requireActivity(), getChildFragmentManager(), false, false);
            }

            if (model.getInvitedCircles() != null && !model.getInvitedCircles().isEmpty()) {
                viewHolder.vBinding.userCircledView.setVisibility(View.VISIBLE);
                viewHolder.vBinding.userCircledView.setUpData(model.getInvitedCircles(), requireActivity(), getChildFragmentManager(), true, false);
            }

            if (model.getInMembers() != null && !model.getInMembers().isEmpty()) {
                viewHolder.vBinding.userSeatsdView.maxInvitee = model.getMaxInvitee();
                viewHolder.vBinding.userSeatsdView.totalInMembers = model.getTotalInMembers();
                viewHolder.vBinding.userSeatsdView.isOpenProfile = true;
                viewHolder.vBinding.userSeatsdView.isFromHistoryEvent = true;
                viewHolder.vBinding.userSeatsdView.setUpData(model.getInMembers(), requireActivity(), getChildFragmentManager(), false, true);
                viewHolder.vBinding.userSeatsdView.setVisibility(View.VISIBLE);
            }


            if (model.isConfirmationRequired() && model.getInterestedMembers() != null && !model.getInterestedMembers().isEmpty()) {
                viewHolder.vBinding.userIntersetdView.maxInvitee = model.getMaxInvitee();
                viewHolder.vBinding.userIntersetdView.getTotalInvitedUsers = model.getTotalInterestedMembers();
                viewHolder.vBinding.userIntersetdView.isOpenProfile = true;
                viewHolder.vBinding.userIntersetdView.isFromHistoryEvent = true;
                viewHolder.vBinding.userIntersetdView.setUpData(model.getInterestedMembers(), requireActivity(), getChildFragmentManager(), false, false);
                viewHolder.vBinding.userIntersetdView.setVisibility(View.VISIBLE);
            }

            if (model.getPlusOneMembers() != null && !model.getPlusOneMembers().isEmpty()) {
                viewHolder.vBinding.plusOneMemberView.maxInvitee = model.getMaxInvitee();
                viewHolder.vBinding.plusOneMemberView.getTotalInvitedUsers = model.getPlusOneMembers().size();
                viewHolder.vBinding.plusOneMemberView.isOpenProfile = true;
                viewHolder.vBinding.plusOneMemberView.isFromHistoryEvent = true;
                viewHolder.vBinding.plusOneMemberView.setUpData(model.getPlusOneMembers(), requireActivity(), getChildFragmentManager(), false, false);
                viewHolder.vBinding.plusOneMemberView.setVisibility(View.VISIBLE);
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
                data.add(getValue("delete"));
                data.add(getValue("repost"));
                Graphics.showActionSheet(requireActivity(), getString(R.string.app_name), data, (data1, position1) -> {
                    switch (position1) {
                        case 0:
                            Graphics.showAlertDialogWithOkCancel(requireActivity(), activity.getString(R.string.app_name), getValue("are_you_sure_want_to_delete_event"), aBoolean -> {
                                if (aBoolean) {
                                    requestPromoterEventDelete(model.getId());
                                }
                            });
                            break;
                        case 1:
                            PromoterProfileManager.shared.isEventRepost = true;
                            PromoterProfileManager.shared.promoterEventModel = model;
                            Intent intent = new Intent(activity, PromoterCreateEventActivity.class);
                            activity.startActivity(intent);
                            break;
                    }
                });
            });


            viewHolder.vBinding.getRoot().setOnClickListener(v -> {
                Utils.preventDoubleClick(v);
                Intent intent = new Intent(requireActivity(), ComplementaryEventDetailActivity.class);
                intent.putExtra("eventId", model.getId());
                intent.putExtra("type", "Promoter");
                intent.putExtra("isFromHistory", true);
                startActivity(intent);
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
    // endregion
    // --------------------------------------

}