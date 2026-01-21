package com.whosin.app.ui.fragment.Promoter;

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
import com.whosin.app.databinding.FragmentPromoterChatBinding;
import com.whosin.app.databinding.ItemPromoterChatBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.PromoterProfileManager;
import com.whosin.app.service.models.BucketEventListModel;
import com.whosin.app.service.models.ChatModel;
import com.whosin.app.service.models.ContactListModel;
import com.whosin.app.service.models.ContainerListModel;
import com.whosin.app.service.models.EventOrgDateModel;
import com.whosin.app.service.models.InviteFriendModel;
import com.whosin.app.service.models.PromoterChatModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.Promoter.ComplementaryEventDetailActivity;
import com.whosin.app.ui.activites.home.Chat.ChatMessageActivity;
import com.whosin.app.ui.fragment.comman.BaseFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class PromoterChatFragment extends BaseFragment {


    private FragmentPromoterChatBinding binding;

    private final PromoterChatListAdapter<PromoterChatModel> chatListAdapter = new PromoterChatListAdapter<>();


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    public void initUi(View view) {
        binding = FragmentPromoterChatBinding.bind(view);

        binding.emptyPlaceHolderView.setEmptyPlaceTxtTitle(getValue("empty_event_chat"));

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        binding.chatRecycler.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false));
        binding.chatRecycler.setAdapter(chatListAdapter);


        binding.swipeRefreshLayout.setProgressViewOffset(false,0,220);
        if (PromoterProfileManager.shared.promoterProfileModel != null && PromoterProfileManager.shared.promoterProfileModel.getProfile() != null) {
            binding.headerView.setUpData(requireActivity(), PromoterProfileManager.shared.promoterProfileModel.getProfile());
        }

        PromoterProfileManager.shared.callbackForHeader = data -> {
            if (isAdded()){
                binding.headerView.setUpData(requireActivity(), data);
            }
        };

        requestChatPromoterContactList(true);


    }

    @Override
    public void setListeners() {
        binding.swipeRefreshLayout.setOnRefreshListener(() -> requestChatPromoterContactList(false));
    }

    @Override
    public void populateData(boolean getDataFromServer) {

    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_promoter_chat;
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


    private void openGroupChat(PromoterChatModel model) {

        BucketEventListModel bucketEventListModel = new BucketEventListModel();
        bucketEventListModel.setId(model.getId());
        bucketEventListModel.setImage(model.getVenueImage());
        bucketEventListModel.setTitle(model.getVenueName());
        bucketEventListModel.setPromoter(true);

        EventOrgDateModel eventOrgDateModel = new EventOrgDateModel();
        eventOrgDateModel.setName(model.getOwner().getFirstName() + " " + model.getOwner().getLastName());


        List<InviteFriendModel> invitedFriendId = new ArrayList<>();

        model.getUsers().stream()
                .filter(p -> p.getInviteStatus().equals("in") && p.getPromoterStatus().equals("accepted"))
                .forEach(p -> {
                    InviteFriendModel inviteFriendModel = new InviteFriendModel();
                    inviteFriendModel.setId(p.getUserId());
                    invitedFriendId.add(inviteFriendModel);
                });

        bucketEventListModel.setInvitedUsers(invitedFriendId);

        List<ContactListModel> contactListModel = new ArrayList<>();

        ContactListModel contactModel = new ContactListModel();
        contactModel.setUserId(model.getOwner().getId());

        bucketEventListModel.setAdmins(contactListModel);


        ChatModel chatModel = new ChatModel(bucketEventListModel, "promoter_event");
        Intent intent = new Intent(requireActivity(), ChatMessageActivity.class);
        intent.putExtra("chatModel", new Gson().toJson(chatModel));
        startActivity(intent);
    }


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestChatPromoterContactList(boolean isShowProgress) {
        if (isShowProgress) {
            showProgress();
        } else {
            binding.swipeRefreshLayout.setRefreshing(true);
        }
        DataService.shared(requireActivity()).requestChatPromoterContactList(true, new RestCallback<ContainerListModel<PromoterChatModel>>(this) {
            @Override
            public void result(ContainerListModel<PromoterChatModel> model, String error) {
                hideProgress();
                binding.swipeRefreshLayout.setRefreshing(false);
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (model.data != null && !model.data.isEmpty()) {
                    model.data.forEach(notification -> {
                        notification.getUsers().removeIf(user -> !("in".equals(user.getInviteStatus()) && "accepted".equals(user.getPromoterStatus())));
                    });
                    chatListAdapter.updateData(model.data);
                } else {
                    binding.chatRecycler.setVisibility(View.GONE);
                    binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
                }

            }
        });
    }



    // --------------------------------------
    // region Adapter
    // --------------------------------------

    private final class PromoterChatListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_promoter_chat));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            PromoterChatModel model = (PromoterChatModel) getItem(position);

            viewHolder.binding.btnGroupChat.setTxtTitle(getValue("group_chat"));

            viewHolder.binding.roundCount.setVisibility(View.GONE);
            viewHolder.binding.userName.setText(model.getVenueName());
            Graphics.loadImage(model.getVenueImage(), viewHolder.binding.profileImage);
            viewHolder.binding.timeDate.setText(Utils.changeDateFormat(model.getDate(), AppConstants.DATEFORMAT_SHORT, AppConstants.DATEFORMT_MM_DATE + " | "));
            viewHolder.binding.startEndTime.setText(model.getStartTime() + " - " + model.getEndTime());

            viewHolder.binding.roundCount.setVisibility(View.GONE);

            if (model.getUsers() != null && !model.getUsers().isEmpty()) {

                viewHolder.binding.userInvitedView.setVisibility(View.VISIBLE);
                viewHolder.binding.userInvitedView.model = model;
                viewHolder.binding.userInvitedView.getTotalInvitedUsers = model.getUsers().size();
                viewHolder.binding.userInvitedView.callback = data -> {
                    if (data){
                        requestChatPromoterContactList(false);
                    }
                };
                viewHolder.binding.userInvitedView.isOpenProfile = true;
                viewHolder.binding.userInvitedView.setUpData(model.getUsers(), requireActivity(), getChildFragmentManager(), false, false);

            } else {
                viewHolder.binding.userInvitedView.setVisibility(View.GONE);
            }


            viewHolder.binding.btnGroupChat.setOnClickListener(view -> {
                if (model.getOwner() == null) {
                    return;
                }
                openGroupChat(model);
            });

            boolean isLastItem = getItemCount() - 1 == position;

            if (isLastItem) {
                int marginBottom = Utils.getMarginBottom(holder.itemView.getContext(), 0.12f);
                Utils.setBottomMargin(holder.itemView, marginBottom);
            } else {
                Utils.setBottomMargin(holder.itemView, 0);
            }

            viewHolder.binding.linear.setOnClickListener(view -> {
                Utils.preventDoubleClick(view);
                Intent intent = new Intent(getActivity(), ComplementaryEventDetailActivity.class);
                intent.putExtra("eventId", model.getId());
                intent.putExtra("type", "Promoter");
                activityLauncher.launch(intent, result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        boolean isReload = result.getData().getBooleanExtra("isReload", false);
                        if (isReload) {
                            requestChatPromoterContactList(false);
                        }
                    }
                });
            });
        }

        public final class ViewHolder extends RecyclerView.ViewHolder {

            private final ItemPromoterChatBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemPromoterChatBinding.bind(itemView);

            }
        }
    }



    // endregion
    // --------------------------------------
}