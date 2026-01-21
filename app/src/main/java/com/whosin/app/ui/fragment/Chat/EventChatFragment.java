package com.whosin.app.ui.fragment.Chat;

import static com.whosin.app.comman.AppDelegate.activity;

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
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.whosin.app.R;
import com.whosin.app.comman.AppConstants;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.FragmentEventChatBinding;
import com.whosin.app.databinding.ItemChatPromoterBinding;
import com.whosin.app.databinding.ItemFriendsChatBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.Repository.ChatRepository;
import com.whosin.app.service.Repository.UserRepository;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.BucketEventListModel;
import com.whosin.app.service.models.BucketListModel;
import com.whosin.app.service.models.ChatMessageModel;
import com.whosin.app.service.models.ChatResponseModel;
import com.whosin.app.service.models.ChatModel;
import com.whosin.app.service.models.ContactListModel;
import com.whosin.app.service.models.ContainerListModel;
import com.whosin.app.service.models.CreateBucketListModel;
import com.whosin.app.service.models.EventOrgDateModel;
import com.whosin.app.service.models.InviteFriendModel;
import com.whosin.app.service.models.OffersModel;
import com.whosin.app.service.models.PromoterChatModel;
import com.whosin.app.service.models.PromoterEventModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.models.VenueObjectModel;
import com.whosin.app.service.models.rayna.RaynaTicketDetailModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.home.Chat.ChatMessageActivity;
import com.whosin.app.ui.fragment.comman.BaseFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;


public class EventChatFragment extends BaseFragment {

    private FragmentEventChatBinding binding;

    private final ChatEventListAdapter<PromoterChatModel> chatEventListAdapter = new ChatEventListAdapter<>();

    private final ChatPromoterListAdapter<ChatModel> chatPromoterListAdapter = new ChatPromoterListAdapter<>();

    private List<PromoterChatModel> eventChatList = new ArrayList<>();

    private List<ChatModel> promoterChatList = new ArrayList<>();

    private boolean isFromSubAdmin = false;

    private String searchQuery = "";
    private Runnable runnable = () -> searchList();
    private Handler handler = new Handler();


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    public EventChatFragment(){}

    public EventChatFragment(boolean isFromSubAdmin){
        this.isFromSubAdmin = isFromSubAdmin;
    }


    @Override
    public void initUi(View view) {
        binding = FragmentEventChatBinding.bind(view);

        binding.edtSearch.setHint(getValue("find_friends"));
        binding.chatPromoterTitle.setText(getValue("promoter"));
        binding.emptyPlaceHolderView.setEmptyPlaceTxtTitle(getValue("empty_chat_list"));

        EventBus.getDefault().register(this);

        binding.chatEventRecycler.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false));
        binding.chatEventRecycler.setAdapter(chatEventListAdapter);

        binding.chatPromoterRecycler.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false));
        binding.chatPromoterRecycler.setAdapter(chatPromoterListAdapter);
    }

    @Override
    public void setListeners() {
        binding.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                searchQuery = editable.toString();
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, 200);

            }
        });

    }

    @Override
    public void populateData(boolean getDataFromServer) {
        requestChatList();
        requestChatPromoterContactList(true);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_event_chat;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (TextUtils.isEmpty(searchQuery)){
            requestChatList();
            requestChatPromoterContactList(false);
        }else {
            searchList();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(BucketListModel event) {
        requestChatPromoterContactList(false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String event) {
        requestChatList();
    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void searchList(){
        if (!TextUtils.isEmpty(searchQuery)){
            if (SessionManager.shared.getUser().isRingMember() && !promoterChatList.isEmpty()){
                List<ChatModel> filteredList = promoterChatList.stream()
                        .filter(p -> p.getUser().getFullName().trim().toLowerCase().contains(searchQuery.toLowerCase().trim()))
                        .collect(Collectors.toList());

                if (!filteredList.isEmpty()){
                    binding.promoterChatContainer.setVisibility(View.VISIBLE);
                    chatPromoterListAdapter.updateData(filteredList);
                }else {
                    binding.promoterChatContainer.setVisibility(View.GONE);
                }
            }

            List<PromoterChatModel> tmpList = eventChatList.stream()
                    .filter(p -> p.getVenueName().trim().toLowerCase().contains(searchQuery.toLowerCase().trim()))
                    .collect(Collectors.toList());
            if (!tmpList.isEmpty()) {
                binding.chatEventRecycler.setVisibility( View.VISIBLE );
                binding.chatListTitle.setVisibility( View.VISIBLE );
                binding.chatListTitle.setText(String.format("Event Chat (%d)", tmpList.size()));
                binding.emptyPlaceHolderView.setVisibility( View.GONE );
                tmpList.stream().filter(conversation -> conversation.getLastMessage() != null).sorted(Comparator.comparing(conversation -> conversation.getLastMessage().getDate(), Comparator.reverseOrder())).collect(Collectors.toList());
                chatEventListAdapter.updateData(tmpList );
            } else {
                binding.chatEventRecycler.setVisibility( View.GONE );
                binding.chatListTitle.setVisibility( View.GONE );
            }
        }else {
            requestChatList();
            if (!eventChatList.isEmpty()) {
                binding.chatEventRecycler.setVisibility( View.VISIBLE );
                binding.chatListTitle.setVisibility( View.VISIBLE );
                binding.chatListTitle.setText(String.valueOf("Event Chat (" + eventChatList.size() + ")"));
                binding.emptyPlaceHolderView.setVisibility( View.GONE );
                eventChatList.stream().filter(conversation -> conversation.getLastMessage() != null).sorted(Comparator.comparing(conversation -> conversation.getLastMessage().getDate(), Comparator.reverseOrder())).collect(Collectors.toList());
                chatEventListAdapter.updateData(eventChatList);
            } else {
                binding.chatEventRecycler.setVisibility( View.GONE );
                binding.chatListTitle.setVisibility( View.GONE );
                binding.emptyPlaceHolderView.setVisibility( View.VISIBLE );
            }

        }

    }

    private void showEmptyState(boolean isShowEmptyState) {
        binding.chatEventRecycler.setVisibility(isShowEmptyState ? View.GONE : View.VISIBLE);
        binding.chatListTitle.setVisibility(isShowEmptyState ? View.GONE : View.VISIBLE);
        binding.emptyPlaceHolderView.setVisibility(isShowEmptyState ? View.VISIBLE : View.GONE);
    }


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestChatList() {
        if (!SessionManager.shared.getUser().isRingMember()){
            binding.promoterChatContainer.setVisibility(View.GONE);
            return;}
        List<String> notExistUserList = new ArrayList<>();
        List<ChatModel> chatList = new ArrayList<>(ChatRepository.shared(requireActivity()).getFriendChatList());
        if (!chatList.isEmpty()) {
            if (SessionManager.shared.getUser().isRingMember()){
                chatList.removeIf(p -> p.getUser() != null && !p.getUser().isPromoter());
            }
            chatList.removeIf(p -> {
                if (p.getMembers().isEmpty()) {
                    return true;
                }
                return p.getMembers().size() == 2 && (p.getMembers().get(0).equals(p.getMembers().get(1)));
            });
            chatList.forEach(p -> {
                String userId = p.getNotExistUserId();
                if (userId != null) {
                    notExistUserList.add(userId);
                }
            });
        }
        else {
            binding.promoterChatContainer.setVisibility(View.GONE);
        }

        List<ChatModel> sortedConversations = chatList.stream().filter(conversation -> conversation.getLastMsg() != null).sorted(Comparator.comparing(conversation -> conversation.getLastMsg().getDate(), Comparator.reverseOrder())).collect(Collectors.toList());
        if (sortedConversations.isEmpty()){
            binding.promoterChatContainer.setVisibility(View.GONE);
            return;
        }
        if (notExistUserList.isEmpty()) {
            if (!chatList.isEmpty()) {
                binding.promoterChatContainer.setVisibility(View.VISIBLE);
                binding.emptyPlaceHolderView.setVisibility(View.GONE);
                promoterChatList.clear();
                promoterChatList.addAll(sortedConversations);
                chatPromoterListAdapter.updateData(sortedConversations);
            } else {
                binding.promoterChatContainer.setVisibility(View.GONE);

            }
        } else {
            UserRepository.shared(context).fetchUsers(notExistUserList, data -> {
                if (!chatList.isEmpty()) {
                    binding.promoterChatContainer.setVisibility(View.VISIBLE);
                    sortedConversations.removeIf(p -> p.getUser() == null);
                    sortedConversations.removeIf(p -> !p.getUser().isPromoter());
                    binding.promoterChatContainer.setVisibility(sortedConversations.isEmpty() ? View.GONE : View.VISIBLE);
                    promoterChatList.clear();
                    promoterChatList.addAll(sortedConversations);
                    chatPromoterListAdapter.updateData(sortedConversations);
                } else {
                    binding.promoterChatContainer.setVisibility(View.GONE);
                }
            });
        }
    }

    private void requestChatPromoterContactList(boolean isShowProgress) {
        if (isShowProgress) {
            binding.progressBar.setVisibility(View.VISIBLE);
        }
        DataService.shared(requireActivity()).requestChatPromoterContactList(SessionManager.shared.getUser().isPromoter() || isFromSubAdmin, new RestCallback<ContainerListModel<PromoterChatModel>>(this) {
            @Override
            public void result(ContainerListModel<PromoterChatModel> model, String error) {
                binding.progressBar.setVisibility(View.GONE);
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (model.data != null && !model.data.isEmpty()) {
                    eventChatList.clear();
                    eventChatList.addAll(model.data);
                    showEmptyState(false);
                    binding.chatListTitle.setText(String.valueOf("Event Chat (" + model.data.size() + ")"));
                    chatEventListAdapter.updateData(model.data);
                } else {
                    if (promoterChatList.isEmpty()){
                        showEmptyState(true);
                    }
                }

            }
        });
    }

    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    private class ChatEventListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_chat_promoter));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            boolean isLastItem = position == getItemCount() - 1;
            PromoterChatModel model = (PromoterChatModel) getItem(position);

            if (model == null) {
                return;
            }

            viewHolder.binding.tvChatUserName.setText(model.getVenueName());
            viewHolder.binding.eventDescription.setText(model.getDescription());
            Graphics.loadImage(model.getVenueImage(), viewHolder.binding.ivUserProfile);

            viewHolder.binding.tvDate.setText(Utils.changeDateFormat(model.getDate(), AppConstants.DATEFORMAT_SHORT, AppConstants.DATEFORMT_EEE_d_MMM_yyyy));
            viewHolder.binding.tvTime.setText(String.format("%s - %s", model.getStartTime(), model.getEndTime()));

            ChatMessageModel lastMsgModel = ChatRepository.shared(Graphics.context).getLastMessages(model.getId());
//            ChatMessageModel lastMsgModel = model.getLastMessage();
            if (lastMsgModel != null) {
                viewHolder.binding.messageContainer.setVisibility(View.VISIBLE);
                viewHolder.binding.messageAndCountLayout.setVisibility(View.VISIBLE);
//                viewHolder.binding.constraint.setVisibility(View.VISIBLE);

                if (TextUtils.isEmpty(lastMsgModel.getDate())) {
                    viewHolder.binding.tvChatTime.setVisibility(View.GONE);
                } else {
                    viewHolder.binding.tvChatTime.setVisibility(View.VISIBLE);
                    viewHolder.binding.tvChatTime.setText(lastMsgModel.getDate(AppConstants.DATEFORMAT_DAYMONTHYEARSHORT_NOT_CROSS));
                }

                if (!TextUtils.isEmpty(lastMsgModel.getAuthorName())){
                  viewHolder.binding.ownerName.setText("~ " + lastMsgModel.getAuthorName() + " :");
                  viewHolder.binding.ownerName.setVisibility(View.VISIBLE);
                }else {
                    viewHolder.binding.ownerName.setVisibility(View.GONE);
                }
                if (lastMsgModel.getType().equals(AppConstants.MsgType.IMAGE.getId())) {
                    viewHolder.binding.ivIcon.setVisibility(View.VISIBLE);
                    Glide.with(requireActivity()).load(R.drawable.photo_msg).into(viewHolder.binding.ivIcon);
                    viewHolder.binding.tvMsg.setText("Photo");
                } else if (lastMsgModel.getType().equals(AppConstants.MsgType.AUDIO.getId())) {
                    viewHolder.binding.ivIcon.setVisibility(View.VISIBLE);
                    Glide.with(requireActivity()).load(R.drawable.voice_record).into(viewHolder.binding.ivIcon);
                    viewHolder.binding.tvMsg.setText("Voice");
                } else {
                    viewHolder.binding.ivIcon.setVisibility(View.GONE);
                    viewHolder.binding.tvMsg.setText(lastMsgModel.getMsg());
                }

                long unReadCount = ChatRepository.shared(Graphics.context).getUnrealMessageCount(model.getId());
                viewHolder.binding.unreadCountContainer.setVisibility(unReadCount > 0 ? View.VISIBLE : View.GONE);
                viewHolder.binding.tvNotReadChatNumber.setText(String.valueOf(unReadCount));
            } else {
                viewHolder.binding.messageContainer.setVisibility(View.GONE);
                viewHolder.binding.messageAndCountLayout.setVisibility(View.GONE);
                viewHolder.binding.tvChatTime.setVisibility(View.GONE);
//                viewHolder.binding.constraint.setVisibility(View.GONE);
            }

            viewHolder.itemView.setOnClickListener(view -> {
                Utils.preventDoubleClick(view);
                if (SessionManager.shared.getUser().isPromoter() || isFromSubAdmin) {
                    viewHolder.openGroupChatForPromoter(model);
                } else {
                    viewHolder.openGroupChatForCm(model);
                }
            });


            if (isLastItem) {
                int marginBottom = Utils.getMarginBottom(holder.itemView.getContext(), 0.22f);
                Utils.setBottomMargin(holder.itemView, marginBottom);
            } else {
                Utils.setBottomMargin(holder.itemView, 0);
            }

        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final ItemChatPromoterBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemChatPromoterBinding.bind(itemView);

            }

            private void openGroupChatForCm(PromoterChatModel model){

                BucketEventListModel bucketEventListModel = new BucketEventListModel();

                bucketEventListModel.setId(model.getId());

                if (model.getOwner() != null){
                    bucketEventListModel.setImage(model.getOwner().getImage());
                    String name = model.getOwner().getFirstName() + " " + model.getOwner().getLastName();
                    bucketEventListModel.setTitle(name);
                }

                bucketEventListModel.setComplementry(true);


                EventOrgDateModel eventOrgDateModel = new EventOrgDateModel();
                eventOrgDateModel.setName(model.getVenueName());
                eventOrgDateModel.setCover(model.getVenueImage());
                bucketEventListModel.setOrg(eventOrgDateModel);


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
                contactModel.setUserId(model.getUserId());
                contactListModel.add(contactModel);

                bucketEventListModel.setAdmins(contactListModel);



                ChatModel chatModel = new ChatModel(bucketEventListModel ,"promoter_event");
                Intent intent = new Intent( activity, ChatMessageActivity.class );
                intent.putExtra( "chatModel", new Gson().toJson( chatModel ) );
                startActivity( intent );
            }

            private void openGroupChatForPromoter(PromoterChatModel model) {

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


        }

    }

    private class ChatPromoterListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_friends_chat));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;

            ChatModel model = (ChatModel) getItem(position);
            UserDetailModel userModel = model.getUser();
            if (userModel != null) {
                Graphics.loadImageWithFirstLetter(userModel.getImage(), viewHolder.binding.ivUserProfile, userModel.getFullName());
                viewHolder.binding.tvChatUserName.setText(userModel.getFullName());
            }
            viewHolder.binding.linearNumber.setVisibility(View.GONE);
            ChatMessageModel lastMsgModel = model.getLastMsg();
            if (lastMsgModel != null) {
                viewHolder.binding.tvChatTime.setTextColor(ContextCompat.getColor(context, R.color.white));

                viewHolder.binding.tvChatTime.setText(lastMsgModel.getDate(AppConstants.DATEFORMAT_DAYMONTHYEARSHORT_NOT_CROSS));
                if (lastMsgModel.getType().equals(AppConstants.MsgType.IMAGE.getId())) {
                    viewHolder.binding.ivIcon.setVisibility(View.VISIBLE);
                    Glide.with(Graphics.context).load(R.drawable.photo_msg).into(viewHolder.binding.ivIcon);
                    viewHolder.binding.tvMsg.setText("Photo");
                } else if (lastMsgModel.getType().equals(AppConstants.MsgType.AUDIO.getId())) {
                    viewHolder.binding.ivIcon.setVisibility(View.VISIBLE);
                    Glide.with(Graphics.context).load(R.drawable.voice_record).into(viewHolder.binding.ivIcon);
                    viewHolder.binding.tvMsg.setText("Voice");
                } else if (lastMsgModel.getType().equals(AppConstants.MsgType.VENUE.getId())) {
                    viewHolder.binding.ivIcon.setVisibility(View.GONE);
                    VenueObjectModel venueObjectModel = new Gson().fromJson(lastMsgModel.getMsg(), VenueObjectModel.class);
                    Glide.with(Graphics.context).load(venueObjectModel.getLogo()).into(viewHolder.binding.ivIcon);
                    if (lastMsgModel.isSent()) {
                        viewHolder.binding.tvMsg.setText(String.format("You shared %s venue", venueObjectModel.getName()));
                    } else {
                        viewHolder.binding.tvMsg.setText(String.format("Shared %s venue", venueObjectModel.getName()));
                    }
                } else if (lastMsgModel.getType().equals(AppConstants.MsgType.STORY.getId())) {
                    viewHolder.binding.ivIcon.setVisibility(View.GONE);
                    VenueObjectModel venueObjectModel = new Gson().fromJson(lastMsgModel.getMsg(), VenueObjectModel.class);
                    if (lastMsgModel.isSent()) {
                        viewHolder.binding.tvMsg.setText(String.format("You shared %s story", venueObjectModel.getName()));
                    } else {
                        viewHolder.binding.tvMsg.setText(String.format("Shared %s story", venueObjectModel.getName()));
                    }
                } else if (lastMsgModel.getType().equals(AppConstants.MsgType.USER.getId())) {
                    viewHolder.binding.ivIcon.setVisibility(View.GONE);
                    UserDetailModel userDetailModel = new Gson().fromJson(lastMsgModel.getMsg(), UserDetailModel.class);
                    if (lastMsgModel.isSent()) {
                        viewHolder.binding.tvMsg.setText(String.format("You shared %s profile", userDetailModel.getFullName()));
                    } else {
                        viewHolder.binding.tvMsg.setText(String.format("Shared %s profile", userDetailModel.getFullName()));
                    }
                }
                else if (lastMsgModel.getType().equals(AppConstants.MsgType.OFFER.getId())) {
                    viewHolder.binding.ivIcon.setVisibility(View.GONE);
                    OffersModel offerModel = new Gson().fromJson(lastMsgModel.getMsg(), OffersModel.class);
                    if (lastMsgModel.isSent()) {
                        viewHolder.binding.tvMsg.setText(String.format("You shared %s offer", offerModel.getTitle()));
                    } else {
                        viewHolder.binding.tvMsg.setText(String.format("Shared %s offer", offerModel.getTitle()));
                    }
                }
                else if (lastMsgModel.getType().equals(AppConstants.MsgType.PROMOTEEvent.getId())) {
                    viewHolder.binding.ivIcon.setVisibility(View.GONE);
//                    OffersModel offerModel = new Gson().fromJson(lastMsgModel.getMsg(), OffersModel.class);
                    if (lastMsgModel.isSent()) {
                        viewHolder.binding.tvMsg.setText(String.format("You shared venue Event"));
                    } else {
                        viewHolder.binding.tvMsg.setText(String.format("Shared venue Event"));
                    }
                }else if (lastMsgModel.getType().equals(AppConstants.MsgType.TICKET.getId())) {
                    viewHolder.binding.ivIcon.setVisibility(View.GONE);
                    RaynaTicketDetailModel raynaTicketDetailModel = new Gson().fromJson(lastMsgModel.getMsg(), RaynaTicketDetailModel.class);
                    if (lastMsgModel.isSent()) {
                        viewHolder.binding.tvMsg.setText(String.format("You shared %s ticket", raynaTicketDetailModel.getTitle()));
                    } else {
                        viewHolder.binding.tvMsg.setText(String.format("Shared %s ticket", raynaTicketDetailModel.getTitle()));
                    }
                }
                else {
                    viewHolder.binding.ivIcon.setVisibility(View.GONE);
                    viewHolder.binding.tvMsg.setText(lastMsgModel.getMsg());
                }

                long unReadCount = model.getUnrealMessageCount();
                viewHolder.binding.linearNumber.setVisibility(unReadCount > 0 ? View.VISIBLE : View.GONE);
                viewHolder.binding.tvNotReadChatNumber.setText(String.valueOf(unReadCount));
            }

            viewHolder.itemView.setOnClickListener(view -> {
                UserDetailModel tmpUserModel = model.getUser();
                if (getActivity() == null) { return; }
                if (userModel == null) { return; }
                model.setImage( tmpUserModel.getImage() );
                model.setTitle( tmpUserModel.getFullName() );
                Intent intent = new Intent(getActivity(), ChatMessageActivity.class);
                intent.putExtra("chatModel", new Gson().toJson(model));
                intent.putExtra("isChatId", true);
                intent.putExtra( "type","friend" );
                startActivity(intent);
            });



        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private ItemFriendsChatBinding binding;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemFriendsChatBinding.bind(itemView);
            }
        }

    }



// endregion
// --------------------------------------

}

