package com.whosin.app.ui.fragment.Promoter.PromoterBootmSheets;

import static com.whosin.app.comman.AppDelegate.activity;
import static com.whosin.app.comman.Graphics.context;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.whosin.app.R;
import com.whosin.app.comman.AppConstants;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.FragmentPromoterNotificationEventBottomSheetBinding;
import com.whosin.app.databinding.ItemPromoterNotificationEventUserListBinding;
import com.whosin.app.databinding.ItemPromoterNotificationEventUserListForBottomSheetBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.models.ChatModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.CustomVenueModel;
import com.whosin.app.service.models.InvitedUserModel;
import com.whosin.app.service.models.MainNotificationModel;
import com.whosin.app.service.models.NotificationModel;
import com.whosin.app.service.models.PromoterAddRingModel;
import com.whosin.app.service.models.PromoterListModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.home.Chat.ChatMessageActivity;

import java.util.List;
import java.util.stream.Collectors;


public class PromoterNotificationEventBottomSheet extends DialogFragment {

    private FragmentPromoterNotificationEventBottomSheetBinding binding;

    private PromoterNotificationEventUserAdapter<PromoterListModel> userAdapter;

    public List<PromoterListModel> models;

    public CommanCallback<Boolean> callback;

    public NotificationModel notificationModel;

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutRes(), container, false);
        initUi(view);
        setListener();
        return view;
    }

    public void setListener() {
        binding.ivClose.setOnClickListener(v -> dismiss());

    }

    public void initUi(View view) {
        binding = FragmentPromoterNotificationEventBottomSheetBinding.bind(view);

        Glide.with(requireActivity()).load(R.drawable.icon_close_btn).into(binding.ivClose);



        if (notificationModel != null) {

            if (notificationModel.getEvent().getVenueType().equals("custom")) {
                if (notificationModel.getEvent().getCustomVenue() != null) {
                    Graphics.loadImage(notificationModel.getEvent().getCustomVenue().getImage(), binding.profileImage);
                    binding.userName.setText(notificationModel.getEvent().getCustomVenue().getName());
                }
            } else {
                if (notificationModel.getEvent().getVenue() != null) {
                    Graphics.loadImage(notificationModel.getEvent().getVenue().getCover(), binding.profileImage);
                    binding.userName.setText(notificationModel.getEvent().getVenue().getName());
                }
            }
            binding.timeDate.setText(Utils.changeDateFormat(notificationModel.getEvent().getDate(), AppConstants.DATEFORMAT_SHORT, AppConstants.DATEFORMT_MM_DATE + " | "));
            binding.startEndTime.setText(notificationModel.getEvent().getStartTime() + "-" + notificationModel.getEvent().getEndTime());

            binding.tvMaxInvitee.setText(String.format("%s seats", String.valueOf(notificationModel.getEvent().getMaxInvitee())));


            userAdapter = new PromoterNotificationEventUserAdapter<>(notificationModel.getEvent().isConfirmationRequired());
            binding.eventUsersRecycler.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
            binding.eventUsersRecycler.setAdapter(userAdapter);
            userAdapter.updateData(models);

        }
    }


    public int getLayoutRes() {
        return R.layout.fragment_promoter_notification_event_bottom_sheet;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = new BottomSheetDialog(requireActivity(), R.style.BottomSheetDialogThemeNoFloating);
        dialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
            FrameLayout bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            ViewGroup.LayoutParams layoutParam = bottomSheet.getLayoutParams();
            layoutParam.height = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParam.width = ViewGroup.LayoutParams.MATCH_PARENT;
            bottomSheet.setLayoutParams(layoutParam);
            BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
        });
        return dialog;
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestPromoterRingUpdateStatus(String status, String id, ItemPromoterNotificationEventUserListForBottomSheetBinding vBinding) {
        if (status.equals("rejected")){
            vBinding.btnRejected.startProgress();
        }else {
            vBinding.btnApprove.startProgress();
        }
        DataService.shared(activity).requestPromoterInviteUpdateStatus(id, status, new RestCallback<ContainerModel<InvitedUserModel>>(this) {
            @Override
            public void result(ContainerModel<InvitedUserModel> model, String error) {
                vBinding.btnRejected.stopProgress();
                vBinding.btnApprove.stopProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(context, model.message, Toast.LENGTH_SHORT).show();

                if (status.equals("rejected")){
                    userAdapter.getData().removeIf(p -> p.getTypeId().equals(id));
                }else {
                    userAdapter.getData().stream()
                            .filter(p -> p.getTypeId().equals(id))
                            .forEach(p -> p.setPromoterStatus("accepted"));
                }
                if (callback != null){
                    callback.onReceive(true);
                }
                userAdapter.notifyDataSetChanged();
            }
        });
    }


    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    private class PromoterNotificationEventUserAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        private boolean isConfirmationRequired = false;

        public PromoterNotificationEventUserAdapter(boolean isConfirmationRequired) {
            this.isConfirmationRequired = isConfirmationRequired;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_promoter_notification_event_user_list_for_bottom_sheet));

        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            PromoterListModel model = (PromoterListModel) getItem(position);
            if (model == null) {
                return;
            }
            Graphics.loadImageWithFirstLetter(model.getImage(), viewHolder.binding.imgProfile, model.getTitle());
            viewHolder.binding.userTitle.setText(model.getTitle());
            viewHolder.binding.description.setText(model.getDescription());

            viewHolder.binding.btnApprove.setVisibility( isConfirmationRequired ? View.VISIBLE : View.GONE);
            viewHolder.binding.btnApprove.setVisibility( model.getPromoterStatus().equals("accepted") ? View.GONE : View.VISIBLE);


            viewHolder.binding.btnRejected.setOnClickListener(view -> {
                viewHolder.binding.btnRejected.setOnClickListener(v -> {
                    Utils.preventDoubleClick(v);
                    Graphics.showAlertDialogWithOkCancel(requireActivity(), getString(R.string.app_name), "Are you sure want to reject " + model.getTitle() + " ?",
                            "Yes", "Cancel", aBoolean -> {
                                if (aBoolean) {
                                    requestPromoterRingUpdateStatus("rejected", model.getTypeId(), viewHolder.binding);
                                }
                            });

                });
            });

            viewHolder.binding.btnApprove.setOnClickListener(v -> {
                Utils.preventDoubleClick(v);
                int count = Math.toIntExact(userAdapter.getData().stream()
                        .filter(p -> p.getPromoterStatus().equals("accepted"))
                        .count());
                if (notificationModel.getEvent().getMaxInvitee() >= count){
                    requestPromoterRingUpdateStatus("accepted", model.getTypeId(), viewHolder.binding);
                }else {
                    Graphics.showAlertDialogWithOkButton(requireActivity(), "WHOS'IN", "Event is Full.", aBoolean -> {
                        if (aBoolean) {
                        }
                    });
                }
            });

            viewHolder.binding.viewMessage.setOnClickListener(view -> {
                UserDetailModel userDetailModel = new UserDetailModel();
                userDetailModel.setId(model.getUserId());
                userDetailModel.setFirstName(model.getTitle());
                userDetailModel.setImage(model.getImage());
                ChatModel chatModel = new ChatModel(userDetailModel);
                Intent intent = new Intent(activity, ChatMessageActivity.class);
                intent.putExtra("chatModel", new Gson().toJson(chatModel));
                startActivity(intent);
            });

        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private ItemPromoterNotificationEventUserListForBottomSheetBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemPromoterNotificationEventUserListForBottomSheetBinding.bind(itemView);
            }
        }
    }

    // endregion
    // --------------------------------------
}