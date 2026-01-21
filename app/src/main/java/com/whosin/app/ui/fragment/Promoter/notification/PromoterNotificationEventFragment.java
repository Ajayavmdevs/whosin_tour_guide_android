package com.whosin.app.ui.fragment.Promoter.notification;

import static android.app.Activity.RESULT_OK;
import static com.whosin.app.comman.AppDelegate.activity;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.whosin.app.R;
import com.whosin.app.comman.AppConstants;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.FragmentPromoterNotificationEventBinding;
import com.whosin.app.databinding.ItemPromoterNotificationEventBinding;
import com.whosin.app.databinding.ItemPromoterNotificationEventUserListBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.models.ChatModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.InvitedUserModel;
import com.whosin.app.service.models.MainNotificationModel;
import com.whosin.app.service.models.NotificationModel;
import com.whosin.app.service.models.PromoterListModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.CmProfile.CmPublicProfileActivity;
import com.whosin.app.ui.activites.Promoter.ComplementaryEventDetailActivity;
import com.whosin.app.ui.activites.Promoter.EventUserApproveRejectBottomSheet;
import com.whosin.app.ui.activites.home.Chat.ChatMessageActivity;
import com.whosin.app.ui.fragment.comman.BaseFragment;

public class PromoterNotificationEventFragment extends BaseFragment {

    private FragmentPromoterNotificationEventBinding binding;

    private final PromoterNotificationEventAdapter<NotificationModel> adapter = new PromoterNotificationEventAdapter<>();

    private PromoterNotificationEventUserAdapter<PromoterListModel> userAdapter;

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    public void initUi(View view) {
        binding = FragmentPromoterNotificationEventBinding.bind(view);

        binding.emptyPlaceHolderView.setEmptyPlaceTxtTitle(getValue("your_event_list_empty"));

        setAdapter();
        requestPromoterEventNotification(true);
    }

    @Override
    public void setListeners() {
        binding.swipeRefreshLayout.setOnRefreshListener(() -> requestPromoterEventNotification(false));
    }

    @Override
    public void populateData(boolean getDataFromServer) {

    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_promoter_notification_event;
    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------
    private void setAdapter() {
        binding.eventRecyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        binding.eventRecyclerView.setAdapter(adapter);
    }


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestPromoterEventNotification(boolean isShowProgress) {
        if (isShowProgress) {
            showProgress();
        } else {
            binding.swipeRefreshLayout.setRefreshing(true);
        }
        DataService.shared(activity).requestPromoterEventNotification(new RestCallback<ContainerModel<MainNotificationModel>>(this) {
            @Override
            public void result(ContainerModel<MainNotificationModel> model, String error) {
                hideProgress();
                binding.swipeRefreshLayout.setRefreshing(false);
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (model.getData() != null) {
                    if (model.getData().getNotification() != null && !model.getData().getNotification().isEmpty()){
                        model.getData().getNotification().forEach(notification -> notification.getList().removeIf(user ->
                                !("in".equals(user.getInviteStatus()) && !"rejected".equals(user.getPromoterStatus()))
                        ));

                        adapter.updateData(model.getData().getNotification());
                        binding.emptyPlaceHolderView.setVisibility(View.GONE);
                        binding.eventRecyclerView.setVisibility(View.VISIBLE);

                    }else {
                        binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
                        binding.eventRecyclerView.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    private void requestPromoterRingUpdateStatus(String status, String id, ItemPromoterNotificationEventUserListBinding vBinding) {
        if (status.equals("rejected")){
            vBinding.btnRejected.startProgress();
        }else {
            vBinding.btnApprove.startProgress();
        }

        DataService.shared(activity).requestPromoterInviteUpdateStatus(id, status, new RestCallback<ContainerModel<InvitedUserModel>>(this) {
            @Override
            public void result(ContainerModel<InvitedUserModel> model, String error) {
                vBinding.btnRejected.stopProgress();
                vBinding.btnApprove.startProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }
                requestPromoterEventNotification(false);
                Toast.makeText(context, model.message, Toast.LENGTH_SHORT).show();

            }
        });
    }

    // --------------------------------------
    // region Adapter
    // --------------------------------------

    private class PromoterNotificationEventAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_promoter_notification_event));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            NotificationModel model = (NotificationModel) getItem(position);

            viewHolder.binding.tvSeeAllTitle.setText(getValue("see_all"));
            viewHolder.binding.description.setText(getValue("joined_your_event"));

            viewHolder.binding.timeDate.setText(Utils.changeDateFormat(model.getEvent().getDate(), AppConstants.DATEFORMAT_SHORT,AppConstants.DATEFORMT_MM_DATE + " | "));
            viewHolder.binding.startEndTime.setText(String.format("%s - %s", model.getEvent().getStartTime(), model.getEvent().getEndTime()));

            if (model.getEvent().getVenueType().equals("custom")) {
                if (model.getEvent().getCustomVenue() != null){
                    Graphics.loadImage(model.getEvent().getCustomVenue().getImage(), viewHolder.binding.profileImage);
                    viewHolder.binding.userName.setText(model.getEvent().getCustomVenue().getName());
                }
            } else {
                if (model.getEvent().getVenue() != null){
                    Graphics.loadImage(model.getEvent().getVenue().getLogo(), viewHolder.binding.profileImage);
                    viewHolder.binding.userName.setText(model.getEvent().getVenue().getName());
                }
            }


            viewHolder.binding.tvMaxInvitee.setText(String.format("%s %s", model.getEvent().getMaxInvitee(), getValue("seats")));


            if (model.getList() != null && !model.getList().isEmpty()) {
                if (model.getList().size() >= 2) {
                    viewHolder.binding.seeAll.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.binding.seeAll.setVisibility(View.GONE);
                }
            }else {
                viewHolder.binding.seeAll.setVisibility(View.GONE);
            }


            viewHolder.binding.seeAll.setOnClickListener(view -> {
                Utils.preventDoubleClick( view );
                EventUserApproveRejectBottomSheet bottomSheet = new EventUserApproveRejectBottomSheet();
                bottomSheet.eventId = model.getEvent().getId();
                bottomSheet.promoterEventModel = model.getEvent();
                bottomSheet.callback = data -> {
                   if (data) requestPromoterEventNotification(false);
                };
                bottomSheet.show(getChildFragmentManager(), "");
//                if(!adapter.getData().get(position).getList().isEmpty()){
//                    PromoterNotificationEventBottomSheet bottomSheet = new PromoterNotificationEventBottomSheet();
//                    bottomSheet.callback = data -> {
//                      if (data){requestPromoterEventNotification(false);}
//                    };
//                    bottomSheet.models = adapter.getData().get(position).getList();
//                    bottomSheet.notificationModel = model;
//                    bottomSheet.show(getChildFragmentManager(), "");
//                }

            });


            viewHolder.binding.getRoot().setOnClickListener(view -> {
                Utils.preventDoubleClick(view);
                Intent intent = new Intent(getActivity(), ComplementaryEventDetailActivity.class);
                intent.putExtra("eventId", model.getId());
                intent.putExtra("type","Promoter");
                activityLauncher.launch(intent, result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        boolean isReload = result.getData().getBooleanExtra("isReload", false);
                        if (isReload) {
                            requestPromoterEventNotification(false);
                        }
                    }
                });
            });

            viewHolder.setEvenUserData(model);

            boolean isLastItem = getItemCount() - 1 == position;

            if (isLastItem) {
                int marginBottom = Utils.getMarginBottom(holder.itemView.getContext(), 0.12f);
                Utils.setBottomMargin(holder.itemView, marginBottom);
            } else {
                Utils.setBottomMargin(holder.itemView, 0);
            }


        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final ItemPromoterNotificationEventBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemPromoterNotificationEventBinding.bind(itemView);
            }

            private void setEvenUserData(NotificationModel model) {
                userAdapter = new PromoterNotificationEventUserAdapter<>(model.getEvent().isConfirmationRequired(),model.getEvent().getMaxInvitee());
                binding.eventUsersRecycler.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
                binding.eventUsersRecycler.setAdapter(userAdapter);
                userAdapter.updateData(model.getList().size() > 2 ? model.getList().subList(0, 2) : model.getList());

            }
        }
    }


    private class PromoterNotificationEventUserAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        private final boolean isConfirmationRequired;
        private final int maxInvite;

        public PromoterNotificationEventUserAdapter(boolean isConfirmationRequired,int maxInvitee) {
            this.isConfirmationRequired = isConfirmationRequired;
            this.maxInvite = maxInvitee;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_promoter_notification_event_user_list));

        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            PromoterListModel listModel = (PromoterListModel) getItem(position);
            if (listModel == null){return;}

            viewHolder.binding.viewMessage.setText(getValue("promoter_message"));
            viewHolder.binding.btnRejected.setTxtTitle(getValue("reject"));
            viewHolder.binding.btnApprove.setTxtTitle(getValue("approve"));

            Graphics.loadImageWithFirstLetter(listModel.getImage(), viewHolder.binding.imgProfile, listModel.getTitle());
            viewHolder.binding.userTitle.setText(listModel.getTitle());
            viewHolder.binding.description.setText(listModel.getDescription());

            viewHolder.binding.btnApprove.setVisibility( isConfirmationRequired ? View.VISIBLE : View.GONE);
            viewHolder.binding.btnApprove.setVisibility( listModel.getPromoterStatus().equals("accepted") ? View.GONE : View.VISIBLE);

            viewHolder.binding.btnRejected.setOnClickListener(v -> {
                Utils.preventDoubleClick(v);
                Graphics.showAlertDialogWithOkCancel(requireActivity(), getString(R.string.app_name), setValue("reject_confirm_alert",listModel.getTitle()),
                        getValue("yes"), getValue("cancel"), aBoolean -> {
                            if (aBoolean) {
                                requestPromoterRingUpdateStatus("rejected", listModel.getTypeId(), viewHolder.binding);
                            }
                        });
            });

            viewHolder.binding.btnApprove.setOnClickListener(v -> {
                Utils.preventDoubleClick(v);
                int count = Math.toIntExact(userAdapter.getData().stream()
                        .filter(p -> p.getPromoterStatus().equals("accepted"))
                        .count());
                if (maxInvite >= count){
                    requestPromoterRingUpdateStatus("accepted", listModel.getTypeId(), viewHolder.binding);
                }else {
                    Graphics.showAlertDialogWithOkButton(requireActivity(), getString(R.string.app_name), getValue("event_full"), aBoolean -> {
                    });
                }
            });


            viewHolder.binding.viewMessage.setOnClickListener(view -> {
                UserDetailModel userDetailModel = new UserDetailModel();
                userDetailModel.setId(listModel.getUserId());
                userDetailModel.setFirstName(listModel.getTitle());
                userDetailModel.setImage(listModel.getImage());
                ChatModel chatModel = new ChatModel(userDetailModel);
                Intent intent = new Intent(activity, ChatMessageActivity.class);
                intent.putExtra("chatModel", new Gson().toJson(chatModel));
                startActivity(intent);
            });

            viewHolder.binding.getRoot().setOnClickListener(view -> activity.startActivity(new Intent(activity, CmPublicProfileActivity.class)
                    .putExtra("promoterUserId", listModel.getUserId())));

        }



        public class ViewHolder extends RecyclerView.ViewHolder {

            private final ItemPromoterNotificationEventUserListBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemPromoterNotificationEventUserListBinding.bind(itemView);

            }
        }
    }

    // endregion
    // --------------------------------------
}