package com.whosin.app.ui.fragment.Promoter.PromoterBootmSheets;

import static com.whosin.app.comman.AppDelegate.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.whosin.app.databinding.FragmentPromoterChatUserBottomSheetBinding;
import com.whosin.app.databinding.ItemPromoterNotificationEventUserListForBottomSheetBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.models.ChatModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.InvitedUserModel;
import com.whosin.app.service.models.PromoterChatModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.CmProfile.CmPublicProfileActivity;
import com.whosin.app.ui.activites.home.Chat.ChatMessageActivity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class PromoterChatUserBottomSheetFragment extends DialogFragment {


    private FragmentPromoterChatUserBottomSheetBinding binding;

    private final PromoterChatUserAdapter<InvitedUserModel> chatUserAdapter = new PromoterChatUserAdapter<>();
    private List<InvitedUserModel> tmpList = new ArrayList<>();
    public PromoterChatModel chatModel;

    public CommanCallback<Boolean> callback;

    private Handler handler = new Handler();
    private String searchQuery = "";
    private Runnable runnable = () -> updateSearchEventList();

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutRes(), container, false);
        initUi(view);
        setListener();
        return view;
    }


    private void initUi(View view) {
        binding = FragmentPromoterChatUserBottomSheetBinding.bind(view);

        binding.emptyPlaceHolderView.setEmptyPlaceTxtTitle(Utils.getLangValue("no_users_available"));
        binding.editTvSearch.setHint(Utils.getLangValue("find_users"));

        binding.eventUsersRecycler.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        binding.eventUsersRecycler.setAdapter(chatUserAdapter);


        if (chatModel != null) {
            binding.timeDate.setText(Utils.changeDateFormat(chatModel.getDate(), AppConstants.DATEFORMAT_SHORT, AppConstants.DATEFORMT_MM_DATE + " | "));
            binding.startEndTime.setText(chatModel.getStartTime() + "-" + chatModel.getEndTime());

            Graphics.loadImage(chatModel.getVenueImage(), binding.profileImage);
            binding.userName.setText(chatModel.getVenueName());


            if (chatModel.getUsers() != null && !chatModel.getUsers().isEmpty()) {
                tmpList.addAll(chatModel.getUsers());
                chatUserAdapter.updateData(chatModel.getUsers());
            }

        }

    }

    private void setListener() {
        binding.ivClose.setOnClickListener(view -> dismiss());

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
                }else {
                    if (!tmpList.isEmpty()) {
                        chatUserAdapter.updateData(tmpList);
                        binding.eventUsersRecycler.setVisibility(View.VISIBLE);
                        binding.emptyPlaceHolderView.setVisibility(View.GONE);
                    }else {
                        binding.eventUsersRecycler.setVisibility(View.GONE);
                        binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
                    }
                }

            }
        });

    }

    private int getLayoutRes() {
        return R.layout.fragment_promoter_chat_user_bottom_sheet;
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
            if (bottomSheet != null) {
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
        return dialog;
    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void updateSearchEventList() {
        List<InvitedUserModel> filteredEventList = tmpList.stream()
                .filter(event -> event.getFullName().toLowerCase().contains(searchQuery.toLowerCase()))
                .collect(Collectors.toList());

        if (!filteredEventList.isEmpty()) {
            chatUserAdapter.updateData(filteredEventList);
            binding.eventUsersRecycler.setVisibility(View.VISIBLE);
            binding.emptyPlaceHolderView.setVisibility(View.GONE);
        } else {
            binding.eventUsersRecycler.setVisibility(View.GONE);
            binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
        }
    }


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestPromoterRingUpdateStatus(String status, String id, ItemPromoterNotificationEventUserListForBottomSheetBinding vBinding) {
        vBinding.btnRejected.startProgress();
        DataService.shared(activity).requestPromoterInviteUpdateStatus(id, status, new RestCallback<ContainerModel<InvitedUserModel>>(this) {
            @Override
            public void result(ContainerModel<InvitedUserModel> model, String error) {
                vBinding.btnRejected.stopProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }

                chatUserAdapter.getData().removeIf(p -> p.getId().equals(id));
                tmpList.removeIf(p -> p.getId().equals(id));
                if (callback != null) {
                    callback.onReceive(true);
                }
                chatUserAdapter.notifyDataSetChanged();
                
            }
        });
    }

    // --------------------------------------
    // region Adapter
    // --------------------------------------

    private class PromoterChatUserAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_promoter_notification_event_user_list_for_bottom_sheet));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            InvitedUserModel model = (InvitedUserModel) getItem(position);

            if (model == null) {
                return;
            }
            viewHolder.binding.description.setVisibility(View.GONE);

            Graphics.loadImageWithFirstLetter(model.getImage(), viewHolder.binding.imgProfile, model.getFullName());
            viewHolder.binding.userTitle.setText(model.getFullName());

            viewHolder.binding.viewMessage.setOnClickListener(view -> {
                UserDetailModel model1 = new UserDetailModel();
                model1.setId(model.getUserId());
                model1.setFirstName(model.getFirstName());
                model1.setLastName(model.getLastName());
                model1.setImage(model.getImage());
                ChatModel chatModel = new ChatModel(model1);
                Intent intent = new Intent(activity, ChatMessageActivity.class);
                intent.putExtra("chatModel", new Gson().toJson(chatModel));
                startActivity(intent);
            });

            viewHolder.binding.getRoot().setOnClickListener(v -> {
                Utils.preventDoubleClick(v);
                activity.startActivity(new Intent(activity, CmPublicProfileActivity.class).putExtra("promoterUserId", model.getUserId()));

            });


            viewHolder.binding.btnRejected.setOnClickListener(v -> {
                Graphics.showAlertDialogWithOkCancel(requireActivity(), getString(R.string.app_name), Utils.setLangValue("reject_confirm_alert",model.getFullName()),
                        Utils.getLangValue("yes"), Utils.getLangValue("Cancel"), aBoolean -> {
                            if (aBoolean) {
                                requestPromoterRingUpdateStatus("rejected", model.getId(), viewHolder.binding);
                            }
                        });
            });


        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final ItemPromoterNotificationEventUserListForBottomSheetBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemPromoterNotificationEventUserListForBottomSheetBinding.bind(itemView);
            }
        }
    }

    // endregion
    // --------------------------------------

}