package com.whosin.app.ui.activites.Promoter;

import static com.whosin.app.comman.AppDelegate.activity;
import static com.whosin.app.comman.Graphics.context;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

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
import com.whosin.app.databinding.FragmentEventUserApproveRejectBottomSheetBinding;
import com.whosin.app.databinding.ItemPlusOneApproveRejectDesignBinding;
import com.whosin.app.databinding.ItemPromoterNotificationEventUserListForBottomSheetBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.models.ChatModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.InvitedUserModel;
import com.whosin.app.service.models.PromoterEventInviteModel;
import com.whosin.app.service.models.PromoterEventModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.CmProfile.CmPublicProfileActivity;
import com.whosin.app.ui.activites.home.Chat.ChatMessageActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


public class EventUserApproveRejectBottomSheet extends DialogFragment {

    private FragmentEventUserApproveRejectBottomSheetBinding binding;

    private PromoterNotificationEventUserAdapter<InvitedUserModel> userAdapter;

    private List<InvitedUserModel> tmpList = new ArrayList<>();

    private List<UserDetailModel> userDetailList = new ArrayList<>();

    public String eventId = "";

    public List<InvitedUserModel> modelList;

    public CommanCallback<Boolean> callback;

    public PromoterEventModel promoterEventModel;

    public boolean isFormInterestedMembers = false;

    public boolean isFromUserInvited = false;
    public boolean isFromPlusOneUsers = false;
    public boolean isFormEventHistory = false;
    public boolean isCancelled = false;

    private int page = 1;

    private Handler handler = new Handler();
    private String searchQuery = "";
    private Runnable runnable = () -> updateSerachEventList();

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

        binding.eventUsersRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) binding.eventUsersRecycler.getLayoutManager();
                assert linearLayoutManager != null;
                int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                int lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();

                if (firstVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition != RecyclerView.NO_POSITION) {
                    if (lastVisibleItemPosition == userAdapter.getData().size() - 1) {
                        if (userAdapter.getData().size() % 50 == 0) {
                            if (!userAdapter.getData().isEmpty()) {
                                page++;
                                requestPromoterInviteList(false);
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
                    handler.removeCallbacks(runnable);
                    handler.postDelayed(runnable, 200);
                } else {
                    if (!tmpList.isEmpty()) {
                        addUserDetailInModel(tmpList);
                        binding.eventUsersRecycler.setVisibility(View.VISIBLE);
                        binding.emptyPlaceHolderView.setVisibility(View.GONE);
                    } else {
                        binding.eventUsersRecycler.setVisibility(View.GONE);
                        binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
                    }
                }

            }
        });

    }

    public void initUi(View view) {
        binding = FragmentEventUserApproveRejectBottomSheetBinding.bind(view);

        binding.editTvSearch.setHint(Utils.getLangValue("find_users"));
        binding.emptyPlaceHolderView.setEmptyPlaceTxtTitle(Utils.getLangValue("no_users_available"));

        Glide.with(requireActivity()).load(R.drawable.icon_close_btn).into(binding.ivClose);

        if (promoterEventModel != null) {

            if (promoterEventModel.getVenueType().equals("custom")) {
                if (promoterEventModel.getCustomVenue() != null) {
                    Graphics.loadImage(promoterEventModel.getCustomVenue().getImage(), binding.profileImage);
                    binding.userName.setText(promoterEventModel.getCustomVenue().getName());
                }
            } else {
                if (promoterEventModel.getVenue() != null) {
                    Graphics.loadImage(promoterEventModel.getVenue().getCover(), binding.profileImage);
                    binding.userName.setText(promoterEventModel.getVenue().getName());
                }
            }
            binding.timeDate.setText(Utils.changeDateFormat(promoterEventModel.getDate(), AppConstants.DATEFORMAT_SHORT, AppConstants.DATEFORMT_MM_DATE + " | "));
            binding.startEndTime.setText(String.format("%s-%s", promoterEventModel.getStartTime(), promoterEventModel.getEndTime()));

            binding.tvMaxInvitee.setText(String.format("%s %s", promoterEventModel.getMaxInvitee(), Utils.getLangValue("seats")));


            userAdapter = new PromoterNotificationEventUserAdapter<>();
            binding.eventUsersRecycler.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
            binding.eventUsersRecycler.setAdapter(userAdapter);

            requestPromoterInviteList(true);
//            userAdapter.updateData(modelList);

        }

    }


    public int getLayoutRes() {
        return R.layout.fragment_event_user_approve_reject_bottom_sheet;
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


    private void addUserDetailInModel(List<InvitedUserModel> userList) {
        List<InvitedUserModel> tmpList = new ArrayList<>(userList);
        Map<String, UserDetailModel> userDetailMap = userDetailList.stream()
                .collect(Collectors.toMap(UserDetailModel::getId, Function.identity()));

        tmpList.stream()
                .filter(p -> p.getUserDetailModel() == null)
                .forEach(p -> {
                    UserDetailModel matchedUser = userDetailMap.get(p.getUserId());
                    if (matchedUser != null) {
                        p.setUserDetailModel(matchedUser);
                    }
                });

        tmpList.stream()
                .filter(p -> "accepted".equals(p.getPromoterStatus()))
                .forEach(p -> {
                    if (p.getPlusOneInvite() != null && !p.getPlusOneInvite().isEmpty()) {
                        p.getPlusOneInvite().stream()
                                .filter(plusOne -> plusOne.getUserDetailModel() == null)
                                .forEach(plusOne -> {
                                    UserDetailModel matchedPlusOneUser = userDetailMap.get(plusOne.getUserId());
                                    if (matchedPlusOneUser != null) {
                                        plusOne.setUserDetailModel(matchedPlusOneUser);
                                    }
                                });
                    }
                });


        userAdapter.updateData(tmpList);
    }


    private void updateSerachEventList() {
        List<InvitedUserModel> filteredEventList = tmpList.stream()
                .filter(event -> event.getUserDetailModel() != null &&
                        event.getUserDetailModel().getFullName().toLowerCase().contains(searchQuery.toLowerCase()))
                .collect(Collectors.toList());


        if (!filteredEventList.isEmpty()) {
            addUserDetailInModel(filteredEventList);
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


    private void requestPromoterInviteList(boolean isShoeProgress) {
        if (isShoeProgress) {
            binding.mainProgressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.VISIBLE);
        }

        DataService.shared(requireActivity()).requestPromoterInviteList(page, eventId, new RestCallback<ContainerModel<PromoterEventInviteModel>>(this) {
            @Override
            public void result(ContainerModel<PromoterEventInviteModel> model, String error) {
                Graphics.hideProgress(requireActivity());
                binding.mainProgressBar.setVisibility(View.GONE);
                binding.progressBar.setVisibility(View.GONE);
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (model.data != null) {

                    userDetailList.addAll(model.getData().getUsers());

                    if (isFromUserInvited) {
                        tmpList.addAll(model.data.getInvitedUsers());
                    } else if (isFormInterestedMembers) {
                        tmpList.addAll(model.data.getInterestedMembers());
                    } else if (isFromPlusOneUsers) {
                        tmpList.addAll(model.data.getPlusOneInvites());
                    } else if (isCancelled) {
                        tmpList.addAll(model.data.getInvitedUsers().stream()
                                .filter(user -> user.isCancelAfterConfirm())
                                .collect(Collectors.toList()));
                    } else {
                        tmpList.addAll(model.data.getInMembers());
                    }

                    if (!tmpList.isEmpty()) {
                        binding.eventUsersRecycler.setVisibility(View.VISIBLE);
                        binding.emptyPlaceHolderView.setVisibility(View.GONE);
                        addUserDetailInModel(tmpList);
//                        userAdapter.updateData(tmpList);
                    } else {
                        binding.eventUsersRecycler.setVisibility(View.GONE);
                        binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
                    }


                }


            }
        });
    }


    private void requestPromoterRingUpdateStatus(String status, String id, ItemPromoterNotificationEventUserListForBottomSheetBinding vBinding) {
        if (status.equals("rejected")) {
            vBinding.btnRejected.startProgress();
        } else {
            vBinding.btnApprove.startProgress();
        }
        DataService.shared(requireActivity()).requestPromoterInviteUpdateStatus(id, status, new RestCallback<ContainerModel<InvitedUserModel>>(this) {
            @Override
            public void result(ContainerModel<InvitedUserModel> model, String error) {
                vBinding.btnRejected.stopProgress();
                vBinding.btnApprove.stopProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(context, model.message, Toast.LENGTH_SHORT).show();

                if (status.equals("rejected")) {
                    userAdapter.getData().removeIf(p -> p.getId().equals(id));
                    tmpList.removeIf(p -> p.getId().equals(id));
                } else {
                    userAdapter.getData().stream().filter(p -> p.getId().equals(id)).forEach(p -> p.setPromoterStatus("accepted"));
                    tmpList.stream().filter(p -> p.getId().equals(id)).forEach(p -> p.setPromoterStatus("accepted"));
                    if (isFormInterestedMembers) {
                        userAdapter.getData().removeIf(p -> p.getId().equals(id));
                        tmpList.removeIf(p -> p.getId().equals(id));
                    }
                }
                if (callback != null) {
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

            viewHolder.binding.tvInviteStatus.setText(Utils.getLangValue("in"));
            viewHolder.binding.viewMessage.setText(Utils.getLangValue("message"));

            viewHolder.binding.btnRejected.setTxtTitle(Utils.getLangValue("reject"));
            viewHolder.binding.btnApprove.setTxtTitle(Utils.getLangValue("approve"));


            viewHolder.binding.inviteStatusLayout.setVisibility(View.VISIBLE);
            Graphics.loadImageWithFirstLetter(model.getUserDetailModel().getImage(), viewHolder.binding.imgProfile, model.getUserDetailModel().getFullName());
            viewHolder.binding.userTitle.setText(model.getUserDetailModel().getFullName());

            if (model.getLogs() != null && !model.getLogs().isEmpty()) {
                String date = Utils.convertToCustomFormat(model.getLogs().get(0).getDateTime());

                if (!Utils.isNullOrEmpty(model.getLogs().get(0).getDateTime())) {
                    viewHolder.binding.dateTimeTv.setVisibility(View.VISIBLE);
                    viewHolder.binding.dateTimeTv.setText(date);
                    viewHolder.binding.dateTimeTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_time_info, 0);
                } else {
                    viewHolder.binding.dateTimeTv.setVisibility(View.GONE);
                }
            } else {
                viewHolder.binding.dateTimeTv.setVisibility(View.GONE);
            }

            String status = promoterEventModel.isConfirmationRequired() ? model.getPromoterStatus() : model.getInviteStatus();

            if (model.getPromoterStatus().equals("pending") && model.getInviteStatus().equals("pending")) {
                viewHolder.binding.inviteStatusLayout.setVisibility(View.GONE);
                viewHolder.binding.description.setText(Utils.getLangValue("added_in_event"));
            } else if (model.isCancelAfterConfirm() || (model.getPromoterStatus().equals("rejected") || status.equals("out"))) {
                viewHolder.binding.tvInviteStatus.setText(Utils.getLangValue("out"));
                viewHolder.binding.inviteStatusLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.red));
            } else if (status.equals("in") || status.equals("accepted")) {
                viewHolder.binding.tvInviteStatus.setText(Utils.getLangValue("confirmed"));
                viewHolder.binding.description.setText(Utils.getLangValue("has_join_event"));
                viewHolder.binding.inviteStatusLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.light_green_bg));
            } else if (model.getPromoterStatus().equals("pending")) {
                viewHolder.binding.tvInviteStatus.setText(Utils.getLangValue("pending"));
                viewHolder.binding.description.setText(Utils.getLangValue("added_in_event"));
                viewHolder.binding.inviteStatusLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.light_yellow));
            }

            if (promoterEventModel.isConfirmationRequired()) {
                viewHolder.binding.btnRejected.setVisibility(View.VISIBLE);
                viewHolder.binding.btnApprove.setVisibility(View.VISIBLE);
                if (status.equals("accepted")) {
                    viewHolder.binding.btnApprove.setVisibility(View.GONE);
                    viewHolder.binding.btnRejected.setVisibility(View.VISIBLE);
                } else if (status.equals("pending")) {
                    viewHolder.binding.btnRejected.setVisibility(View.VISIBLE);
                    viewHolder.binding.btnApprove.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.binding.btnApprove.setVisibility(View.VISIBLE);
                    viewHolder.binding.btnRejected.setVisibility(View.GONE);
                }
            } else {
                if (status.equals("in")) {
                    viewHolder.binding.btnApprove.setVisibility(View.GONE);
                    viewHolder.binding.btnRejected.setVisibility(View.VISIBLE);
                } else if (status.equals("pending")) {
                    viewHolder.binding.btnApprove.setVisibility(View.GONE);
                    viewHolder.binding.btnRejected.setVisibility(View.GONE);
                }

            }

            if (model.getPromoterStatus().equals("accepted")) {
                if (model.getPlusOneInvite() != null && !model.getPlusOneInvite().isEmpty()) {
                    viewHolder.setUpPlusOneData(model);
                } else {
                    viewHolder.binding.plusOneLayout.setVisibility(View.GONE);
                }

            } else {
                viewHolder.binding.plusOneLayout.setVisibility(View.GONE);
            }

            viewHolder.binding.buttonsLayout.setVisibility(isFromUserInvited || isFormEventHistory || isCancelled ? View.GONE : View.VISIBLE);

            viewHolder.binding.btnRejected.setOnClickListener(v -> {
                Utils.preventDoubleClick(v);
                Graphics.showAlertDialogWithOkCancel(requireActivity(), getString(R.string.app_name), Utils.setLangValue("reject_confirm_alert",model.getTitle()),
                        Utils.getLangValue("yes"), Utils.getLangValue("cancel"), aBoolean -> {
                            if (aBoolean) {
                                requestPromoterRingUpdateStatus("rejected", model.getId(), viewHolder.binding);
                            }
                        });

            });

            viewHolder.binding.btnApprove.setOnClickListener(v -> {
                Utils.preventDoubleClick(v);
                requestPromoterRingUpdateStatus("accepted", model.getId(), viewHolder.binding);

//                int count = Math.toIntExact(userAdapter.getData().stream()
//                        .filter(p -> promoterEventModel.isConfirmationRequired() ? p.getPromoterStatus().equals("accepted") : p.getInviteStatus().equals("in"))
//                        .count());
//                if (promoterEventModel.getMaxInvitee() >= count) {
//                    if (isFromPlusOneUsers) {
//                        requestPromoterPlusOneInviteStatus("accepted", model.getId(), viewHolder.binding);
//                    } else {
//                        requestPromoterRingUpdateStatus("accepted", model.getId(), viewHolder.binding);
//                    }
//                } else {
//                    Graphics.showAlertDialogWithOkButton(requireActivity(), "WHOS'IN", "Event is Full.", aBoolean -> {
//                        if (aBoolean) {
//                        }
//                    });
//                }
            });

            viewHolder.binding.viewMessage.setOnClickListener(view -> {
                UserDetailModel userDetailModel = new UserDetailModel();
                userDetailModel.setId(model.getUserDetailModel().getId());
                userDetailModel.setFirstName(model.getUserDetailModel().getFullName());
                userDetailModel.setImage(model.getUserDetailModel().getImage());
                ChatModel chatModel = new ChatModel(userDetailModel);
                Intent intent = new Intent(activity, ChatMessageActivity.class);
                intent.putExtra("chatModel", new Gson().toJson(chatModel));
                startActivity(intent);
            });

            viewHolder.binding.dateTimeTv.setOnClickListener(view -> {
                PromoterRecentBottomSheet promoterRecentBottomSheet = new PromoterRecentBottomSheet();
                promoterRecentBottomSheet.logsModels = model.getLogs();
                promoterRecentBottomSheet.show(getParentFragmentManager(), "");
            });

            viewHolder.binding.getRoot().setOnClickListener(view -> {
                activity.startActivity(new Intent(activity, CmPublicProfileActivity.class).putExtra("promoterUserId", model.getUserId()));
            });

        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final ItemPromoterNotificationEventUserListForBottomSheetBinding binding;

            private PlusOneUserListAdapter<InvitedUserModel> plusOneUserListAdapter = new PlusOneUserListAdapter<>();

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemPromoterNotificationEventUserListForBottomSheetBinding.bind(itemView);
                binding.plusOneUseRecycleView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
                binding.plusOneUseRecycleView.setAdapter(plusOneUserListAdapter);
            }

            private void setUpPlusOneData(InvitedUserModel model) {
                binding.plusOneLayout.setVisibility(View.VISIBLE);
                plusOneUserListAdapter.updateData(model.getPlusOneInvite());
            }
        }
    }


    private class PlusOneUserListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {


        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_plus_one_approve_reject_design));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            InvitedUserModel model = (InvitedUserModel) getItem(position);
            if (model == null) {
                return;
            }

            viewHolder.binding.description.setText(Utils.getLangValue("invite_event_plusOne"));

            viewHolder.binding.inviteStatusLayout.setVisibility(View.VISIBLE);
            Graphics.loadImageWithFirstLetter(model.getUserDetailModel().getImage(), viewHolder.binding.imgProfile, model.getUserDetailModel().getFullName());
            viewHolder.binding.userTitle.setText(model.getUserDetailModel().getFullName());

            String status = model.getInviteStatus();

            if (status.equals("in")) {
                viewHolder.binding.tvInviteStatus.setText(Utils.getLangValue("in"));
                viewHolder.binding.inviteStatusLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.light_green_bg));
            } else if (model.getPromoterStatus().equals("pending")) {
                viewHolder.binding.tvInviteStatus.setText(Utils.getLangValue("pending"));
                viewHolder.binding.inviteStatusLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.light_yellow));
            } else if (status.equals("out")) {
                viewHolder.binding.inviteStatusLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.red));
            }

            viewHolder.binding.viewLine.setVisibility(position == 0 ? View.GONE : View.VISIBLE);

            viewHolder.binding.getRoot().setOnClickListener(view -> {
                activity.startActivity(new Intent(activity, CmPublicProfileActivity.class).putExtra("promoterUserId", model.getUserId()));
            });

        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final ItemPlusOneApproveRejectDesignBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemPlusOneApproveRejectDesignBinding.bind(itemView);
            }
        }
    }

    // endregion
    // --------------------------------------


}