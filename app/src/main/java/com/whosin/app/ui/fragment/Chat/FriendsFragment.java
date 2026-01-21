package com.whosin.app.ui.fragment.Chat;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
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
import com.whosin.app.databinding.FragmentFriendsBinding;
import com.whosin.app.databinding.ItemFriendsChatBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.Repository.ChatRepository;
import com.whosin.app.service.Repository.UserRepository;
import com.whosin.app.service.manager.BlockUserManager;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.ChatMessageModel;
import com.whosin.app.service.models.ChatModel;
import com.whosin.app.service.models.ContactListModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.OffersModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.models.VenueObjectModel;
import com.whosin.app.service.models.rayna.RaynaTicketDetailModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.home.Chat.ChatMessageActivity;
import com.whosin.app.ui.activites.venue.Bucket.ContactShareBottomSheet;
import com.whosin.app.ui.fragment.comman.BaseFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import io.realm.RealmList;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;


public class FriendsFragment extends BaseFragment {

    private FragmentFriendsBinding binding;

    private final ChatFriendsListAdapter<ChatModel> chatFriendsListAdapter = new ChatFriendsListAdapter<>();
    private String searchQuery = "";
    private Runnable runnable = () -> serachList();
    private Handler handler = new Handler();

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    public void initUi(View view) {
        EventBus.getDefault().register(this);
        binding = FragmentFriendsBinding.bind(view);

        binding.edtSearch.setHint(getValue("find_friends"));
        binding.emptyPlaceHolderView.setEmptyPlaceTxtTitle(getValue("empty_chat_list"));

        binding.chatFriendRecycle.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false));
        binding.chatFriendRecycle.setAdapter(chatFriendsListAdapter);
        binding.chatFriendRecycle.setNestedScrollingEnabled(false);

        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                if (chatFriendsListAdapter.getData() != null && !chatFriendsListAdapter.getData().isEmpty()) {
                    ChatModel model = chatFriendsListAdapter.getData().get(viewHolder.getAbsoluteAdapterPosition());
                    if (direction == ItemTouchHelper.LEFT) {
                        Graphics.showAlertDialogWithOkCancel(requireActivity(), getString(R.string.app_name), getValue("delete_chat_confirmation"), aBoolean -> {
                            if (aBoolean) {
                                requestDeleteChat(model.getChatId());
                            } else {
                                requestChatList();
                            }
                        });

                    }
                }

            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.brand_pink))
                        .addActionIcon(R.drawable.icon_delete)
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }


        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(binding.chatFriendRecycle);
    }

    @Override
    public void setListeners() {
        binding.addChatFriend.setOnClickListener(view -> {
            Utils.preventDoubleClick( view );
            ContactShareBottomSheet contactDialog = new ContactShareBottomSheet();
            contactDialog.myWallet = true;
            contactDialog.setShareListener(data -> {
                if (!data.isEmpty()) {
                    ContactListModel model = data.get(0);
                    ChatModel chatModel = new ChatModel();
                    chatModel.setImage(model.getImage());
                    chatModel.setTitle(model.getFullName());
                    chatModel.setChatType("friend");
                    RealmList<String> idList = new RealmList<>();
                    idList.add(SessionManager.shared.getUser().getId());
                    idList.add(model.getId());
                    chatModel.setMembers(idList);
                    Collections.sort(idList);
                    String joinedString = String.join(",", idList);
                    chatModel.setChatId(joinedString);

                    if(!model.getId().equals( SessionManager.shared.getUser().getId()) ){
                        startActivity( new Intent(requireActivity(),ChatMessageActivity.class)
                                .putExtra("chatModel", new Gson().toJson(chatModel)) );
                    }

                }
            });
            contactDialog.show(getChildFragmentManager(), "1");
        });

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

    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_friends;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        serachList();
//        requestChatList();
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String event) {
        requestChatList();
    }

    private void serachList(){
        if (!TextUtils.isEmpty(searchQuery)) {
            List<ChatModel> filteredList = chatFriendsListAdapter.getData().stream()
                    .filter(p -> p.getUser().getFullName().trim().toLowerCase().contains(searchQuery.toLowerCase().trim()))
                    .collect(Collectors.toList());
            if (!filteredList.isEmpty()) {
                binding.chatFriendRecycle.setVisibility(View.VISIBLE);
                binding.emptyPlaceHolderView.setVisibility(View.GONE);
                if (SessionManager.shared.getUser().isRingMember()){
                    filteredList.removeIf(p -> p.getUser().isPromoter());
                }
                chatFriendsListAdapter.updateData(filteredList);
            } else {
                binding.chatFriendRecycle.setVisibility(View.GONE);
                binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
            }
        }else {
            requestChatList();
        }

    }

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestChatList() {

        List<String> notExistUserList = new ArrayList<>();
        List<ChatModel> chatList = new ArrayList<>(ChatRepository.shared(requireActivity()).getFriendChatList());
        if (!chatList.isEmpty()) {
            binding.emptyPlaceHolderView.setVisibility(View.GONE);
            if (SessionManager.shared.getUser().isRingMember()){
                chatList.removeIf(p -> p.getUser() != null && p.getUser().isPromoter());
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
            chatList.removeIf(p -> p.getChatType().equals("promoter_event"));
        }
        else {
            binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
        }

        chatList.removeIf(p -> {
            if (p == null || p.getUser() == null || p.getUser().getId() == null || p.getUser().getId().isEmpty()) {
                return false;
            }
            return BlockUserManager.shared.isUserBlocked(p.getUser().getId());
        });


        List<ChatModel> sortedConversations = chatList.stream().filter(conversation -> conversation.getLastMsg() != null).sorted(Comparator.comparing(conversation -> conversation.getLastMsg().getDate(), Comparator.reverseOrder())).collect(Collectors.toList());
        if (sortedConversations.isEmpty()){
            binding.chatFriendRecycle.setVisibility(View.GONE);
            binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
            return;
        }


        if (notExistUserList.isEmpty()) {
            if (!chatList.isEmpty()) {
                binding.chatFriendRecycle.setVisibility(View.VISIBLE);
                binding.emptyPlaceHolderView.setVisibility(View.GONE);
                chatFriendsListAdapter.updateData(sortedConversations);
            } else {
                binding.chatFriendRecycle.setVisibility(View.GONE);
                binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
            }
        } else {
            UserRepository.shared(context).fetchUsers(notExistUserList, data -> {
                if (!chatList.isEmpty()) {
                    binding.chatFriendRecycle.setVisibility(View.VISIBLE);
                    binding.emptyPlaceHolderView.setVisibility(View.GONE);
                    sortedConversations.removeIf(p -> p.getUser() == null);
                    chatFriendsListAdapter.updateData(sortedConversations);
                } else {
                    binding.chatFriendRecycle.setVisibility(View.GONE);
                    binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    private void requestDeleteChat(String chatId) {
        showProgress();
        DataService.shared( requireActivity() ).requestDeleteChat(chatId, new RestCallback<ContainerModel<ChatModel>>(this) {
            @Override
            public void result(ContainerModel<ChatModel> model, String error) {
                hideProgress();
                if (getActivity() == null) { return; }
                if (!Utils.isNullOrEmpty( error ) || model == null) {
                    Toast.makeText( getActivity(), error, Toast.LENGTH_SHORT ).show();
                    return;
                }
                ChatRepository.shared( getActivity() ).clearChat( chatId, data -> requestChatList());

            }
        } );
    }

    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    public class ChatFriendsListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_friends_chat));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            boolean isLastItem = position == getItemCount() - 1;
            ChatModel model = (ChatModel) getItem(position);
            UserDetailModel userModel = model.getUser();
            if (userModel != null) {
                Graphics.loadImageWithFirstLetter(userModel.getImage(), viewHolder.binding.ivUserProfile, userModel.getFullName());
                viewHolder.binding.tvChatUserName.setText(userModel.getFullName());
            }
            viewHolder.binding.linearNumber.setVisibility(View.GONE);
            ChatMessageModel lastMsgModel = model.getLastMsg();
            if (lastMsgModel != null) {
                viewHolder.binding.tvChatTime.setText(lastMsgModel.getDate(AppConstants.DATEFORMAT_24HOUR));
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

            if (isLastItem) {
                int marginBottom = Utils.getMarginBottom(holder.itemView.getContext(),0.22f);
                Utils.setBottomMargin(holder.itemView, marginBottom);
            } else {
                Utils.setBottomMargin(holder.itemView, 0);
            }

        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private ItemFriendsChatBinding binding;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemFriendsChatBinding.bind(itemView);
            }
        }

    }

    // --------------------------------------
    // endregion
    // --------------------------------------
}

