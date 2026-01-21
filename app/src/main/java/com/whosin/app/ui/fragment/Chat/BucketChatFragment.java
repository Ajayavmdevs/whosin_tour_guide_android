package com.whosin.app.ui.fragment.Chat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
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
import com.whosin.app.databinding.FragmentBucketChatBinding;
import com.whosin.app.databinding.GroupChatLayoutBinding;
import com.whosin.app.databinding.ItemChatBucklistBinding;
import com.whosin.app.service.Repository.ChatRepository;
import com.whosin.app.service.models.BucketEventListModel;
import com.whosin.app.service.models.BucketListModel;
import com.whosin.app.service.models.ChatMessageModel;
import com.whosin.app.service.models.ChatModel;
import com.whosin.app.service.models.CreateBucketListModel;
import com.whosin.app.service.models.InviteFriendModel;
import com.whosin.app.ui.activites.home.Chat.ChatMessageActivity;
import com.whosin.app.ui.fragment.comman.BaseFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


public class BucketChatFragment extends BaseFragment {

    private FragmentBucketChatBinding binding;
    private final GroupChatListAdapter<BucketListModel> listAdapter = new GroupChatListAdapter<>();
    private String searchQuery = "";
    private Runnable runnable = () -> serachList();
    private Handler handler = new Handler();

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    public void initUi(View view) {
        EventBus.getDefault().register(this);
        binding = FragmentBucketChatBinding.bind( view );

        binding.edtSearch.setHint(getValue("find_groups"));
        binding.emptyPlaceHolderView.setEmptyPlaceTxtTitle(getValue("empty_group_chat_list"));

        binding.chatBucketRecycle.setLayoutManager( new LinearLayoutManager( requireActivity(), LinearLayoutManager.VERTICAL, false ) );
        binding.chatBucketRecycle.setAdapter( listAdapter );
        requestBucketList(true,true);
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
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_bucket_chat;
    }

    @Override
    public void onResume() {
        super.onResume();
        serachList();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(BucketListModel event) {
//        requestBucketList(true, false);
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void serachList(){
        if (!TextUtils.isEmpty(searchQuery)) {
            ChatRepository.shared( requireActivity() ).getBucketChatList( false, data -> {
                if (getActivity() == null) { return; }
                if (data != null) {
                    List<BucketListModel> tmpList = new ArrayList<>();
                    if (data.getBucketsModels() != null && !data.getBucketsModels().isEmpty()) {
                        BucketListModel bucketModel = new BucketListModel();
                        List<CreateBucketListModel> sortedConversations = data.getBucketsModels().stream().filter(p ->  p.getName().trim().toLowerCase().contains(searchQuery.toLowerCase().trim())).collect( Collectors.toList() );
                        bucketModel.setBucketsModels( sortedConversations );
                        tmpList.add( bucketModel );
                    }

                    if (data.getEventModels() != null && !data.getEventModels().isEmpty()) {
                        BucketListModel eventModel = new BucketListModel();
                        List<BucketEventListModel> sortedConversations = data.getEventModels().stream().filter(p ->  p.getTitle().trim().toLowerCase().contains(searchQuery.toLowerCase().trim())).collect( Collectors.toList() );
                        sortedConversations.removeIf( p -> !Utils.isFutureDate( p.getEventTime(), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" ) );
                        eventModel.setEventModels( sortedConversations );
                        tmpList.add( eventModel );
                    }

                    if (data.getOutingModels() != null && !data.getOutingModels().isEmpty()) {
                        BucketListModel outingModel = new BucketListModel();
                        List<InviteFriendModel> sortedConversations = data.getOutingModels().stream().filter(p ->  p.getVenue().getName().trim().toLowerCase().contains(searchQuery.toLowerCase().trim())).collect( Collectors.toList() );
                        sortedConversations.removeIf( p -> {
                            String dateTime = p.getDate() + " " + p.getStartTime();
                            return !Utils.isFutureDate( dateTime, "yyyy-MM-dd HH:mm" );
                        } );
                        List<InviteFriendModel> updatedlist = sortedConversations.stream().filter(p -> p.getUser() != null).collect(Collectors.toList());
                        outingModel.setOutingModels( updatedlist );
                        tmpList.add( outingModel );
                    }

                    getActivity().runOnUiThread(() -> {
                        if (tmpList.isEmpty()) {
                            binding.chatBucketRecycle.setVisibility( View.GONE );
                            binding.emptyPlaceHolderView.setVisibility( View.VISIBLE );
                            return;
                        }
                        binding.chatBucketRecycle.setVisibility( View.VISIBLE );
                        binding.emptyPlaceHolderView.setVisibility( View.GONE );
                        listAdapter.updateData( tmpList );
                    });


                } else {
                    getActivity().runOnUiThread(() -> {
                        listAdapter.updateData(new ArrayList<>());
                        binding.chatBucketRecycle.setVisibility(View.GONE);
                        binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
                    });
                }
            } );

        }else {
           requestBucketList(true,true);
        }

    }

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestBucketList(boolean shouldRefrsh ,boolean showProgress) {
        if (showProgress) binding.progressBar.setVisibility(View.VISIBLE);
        ChatRepository.shared( requireActivity() ).getBucketChatList( shouldRefrsh, data -> {
            binding.progressBar.setVisibility(View.GONE);
            if (getActivity() == null) { return; }
            if (data != null) {
                List<BucketListModel> tmpList = new ArrayList<>();
                if (data.getBucketsModels() != null && !data.getBucketsModels().isEmpty()) {
                    BucketListModel bucketModel = new BucketListModel();
                    List<CreateBucketListModel> sortedConversations = data.getBucketsModels().stream().sorted( Comparator.comparing( conversation -> conversation.getLastMsg().getDate(), Comparator.reverseOrder() ) ).collect( Collectors.toList() );
                    bucketModel.setBucketsModels( sortedConversations );
                    tmpList.add( bucketModel );
                }

                if (data.getEventModels() != null && !data.getEventModels().isEmpty()) {
                    BucketListModel eventModel = new BucketListModel();
                    List<BucketEventListModel> sortedConversations = data.getEventModels().stream().sorted( Comparator.comparing( conversation -> conversation.getLastMsg().getDate(), Comparator.reverseOrder() ) ).collect( Collectors.toList() );
                    sortedConversations.removeIf( p -> !Utils.isFutureDate( p.getEventTime(), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" ) );
                    eventModel.setEventModels( sortedConversations );
                    tmpList.add( eventModel );
                }

                if (data.getOutingModels() != null && !data.getOutingModels().isEmpty()) {
                    BucketListModel outingModel = new BucketListModel();
                    List<InviteFriendModel> sortedConversations = data.getOutingModels().stream().sorted( Comparator.comparing( conversation -> conversation.getLastMsg().getDate(), Comparator.reverseOrder() ) ).collect( Collectors.toList() );
                    sortedConversations.removeIf( p -> {
                        String dateTime = p.getDate() + " " + p.getStartTime();
                        return !Utils.isFutureDate( dateTime, "yyyy-MM-dd HH:mm" );
                    } );
                    List<InviteFriendModel> updatedlist = sortedConversations.stream().filter(p -> p.getUser() != null).collect(Collectors.toList());
                    outingModel.setOutingModels( updatedlist );
                    tmpList.add( outingModel );
                }

                getActivity().runOnUiThread(() -> {
                    if (tmpList.isEmpty()) {
                        binding.chatBucketRecycle.setVisibility( View.GONE );
                        binding.emptyPlaceHolderView.setVisibility( View.VISIBLE );
                        return;
                    }
                    binding.chatBucketRecycle.setVisibility( View.VISIBLE );
                    binding.emptyPlaceHolderView.setVisibility( View.GONE );
                    listAdapter.updateData( tmpList );
                });


            } else {
                getActivity().runOnUiThread(() -> {
                    listAdapter.updateData(new ArrayList<>());
                    binding.chatBucketRecycle.setVisibility(View.GONE);
                    binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
                });
            }
        } );
    }


    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    public class GroupChatListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {
        private static final int VIEW_TYPE_BUCKET = 0;
        private static final int VIEW_TYPE_EVENT = 1;
        private static final int VIEW_TYPE_OUTING = 2;

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from( parent.getContext() );

            switch (viewType) {
                case VIEW_TYPE_BUCKET:
                    return new BucketHolder( inflater.inflate( R.layout.group_chat_layout, parent, false ) );
                case VIEW_TYPE_EVENT:
                    return new EventHolder( inflater.inflate( R.layout.group_chat_layout, parent, false ) );
                case VIEW_TYPE_OUTING:
                    return new OutingHolder( inflater.inflate( R.layout.group_chat_layout, parent, false ) );
                default:
                    throw new IllegalArgumentException( "Invalid view type: " + viewType );
            }

        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            boolean isLastItem = position == getItemCount() - 1;
            BucketListModel bucketListModel = (BucketListModel) getItem( position );
            if (getItemViewType( position ) == VIEW_TYPE_BUCKET) {
                ((BucketHolder) holder).setupData( bucketListModel.getBucketsModels() );
            } else if (getItemViewType( position ) == VIEW_TYPE_EVENT) {
                ((EventHolder) holder).setupData( bucketListModel.getEventModels() );
            } else {
                ((OutingHolder) holder).setupData( bucketListModel.getOutingModels() );
            }

            if (isLastItem) {
                int marginBottom = Utils.getMarginBottom( holder.itemView.getContext(), 0.18f );
                Utils.setBottomMargin( holder.itemView, marginBottom );
            } else {
                Utils.setBottomMargin( holder.itemView, 0 );
            }

        }


        public int getItemViewType(int position) {
            BucketListModel bucketListModel = (BucketListModel) getItem( position );

            if (!bucketListModel.getBucketsModels().isEmpty()) {
                return VIEW_TYPE_BUCKET;
            } else if (!bucketListModel.getEventModels().isEmpty()) {
                return VIEW_TYPE_EVENT;
            } else if(!bucketListModel.getOutingModels().isEmpty()){
                return VIEW_TYPE_OUTING;
            }
            return super.getItemViewType(position);
        }

        public class BucketHolder extends RecyclerView.ViewHolder {
            private final GroupChatLayoutBinding mBinding;
            private final BucketChatList<CreateBucketListModel> adapter = new BucketChatList<>();

            public BucketHolder(@NonNull View itemView) {
                super( itemView );
                mBinding = GroupChatLayoutBinding.bind( itemView );
                mBinding.chatListRecycleView.setLayoutManager( new LinearLayoutManager( requireActivity(), LinearLayoutManager.VERTICAL, false ) );
                mBinding.chatListRecycleView.setAdapter( adapter );
            }


            @SuppressLint("SetTextI18n")
            public void setupData(List<CreateBucketListModel> bucketsModels) {
                if (!bucketsModels.isEmpty()) {
                    mBinding.titleContainer.setVisibility( View.VISIBLE );
                    mBinding.chatListRecycleView.setVisibility( View.VISIBLE );
                    mBinding.chatListTitle.setText(setValue("bucket_chat_item",String.valueOf(bucketsModels.size())));
                    adapter.updateData( bucketsModels );
                } else {
                    mBinding.titleContainer.setVisibility( View.GONE );
                    mBinding.chatListRecycleView.setVisibility( View.GONE );
                }

            }

        }

        public class EventHolder extends RecyclerView.ViewHolder {
            private final GroupChatLayoutBinding mBinding;
            private final EventChatList<BucketEventListModel> adapter = new EventChatList<>();


            public EventHolder(@NonNull View itemView) {
                super( itemView );
                mBinding = GroupChatLayoutBinding.bind( itemView );
                mBinding.chatListRecycleView.setLayoutManager( new LinearLayoutManager( requireActivity(), LinearLayoutManager.VERTICAL, false ) );
                mBinding.chatListRecycleView.setAdapter( adapter );
            }

            @SuppressLint("SetTextI18n")
            public void setupData(List<BucketEventListModel> eventModels) {
                if (!eventModels.isEmpty()) {
                    mBinding.titleContainer.setVisibility( View.VISIBLE );
                    mBinding.chatListRecycleView.setVisibility( View.VISIBLE );
                    mBinding.chatListTitle.setText(setValue("event_chat_item",String.valueOf(eventModels.size())));
                    adapter.updateData( eventModels );
                } else {
                    mBinding.titleContainer.setVisibility( View.GONE );
                    mBinding.chatListRecycleView.setVisibility( View.GONE );
                }
            }
        }

        public class OutingHolder extends RecyclerView.ViewHolder {
            private final GroupChatLayoutBinding mBinding;
            private final OutingChatList<InviteFriendModel> adapter = new OutingChatList<>();

            public OutingHolder(@NonNull View itemView) {
                super( itemView );
                mBinding = GroupChatLayoutBinding.bind( itemView );
                mBinding.chatListRecycleView.setLayoutManager( new LinearLayoutManager( requireActivity(), LinearLayoutManager.VERTICAL, false ) );
                mBinding.chatListRecycleView.setAdapter( adapter );
            }

            public void setupData(List<InviteFriendModel> outingModels) {
                if (!outingModels.isEmpty()) {
                    mBinding.titleContainer.setVisibility( View.VISIBLE );
                    mBinding.chatListRecycleView.setVisibility( View.VISIBLE );
                    mBinding.chatListTitle.setText( setValue("outing_chat_item",String.valueOf(outingModels.size())));

                    adapter.updateData( outingModels );
                } else {
                    mBinding.titleContainer.setVisibility( View.GONE );
                    mBinding.chatListRecycleView.setVisibility( View.GONE );
                }
            }
        }
    }

    public class BucketChatList<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder( UiUtils.getViewBy( parent, R.layout.item_chat_bucklist ) );
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            CreateBucketListModel model = (CreateBucketListModel) getItem( position );

            if (model != null) {
                viewHolder.binding.tvChatUserName.setText( model.getName() );
                if (Utils.isValidUrl( model.getCoverImage() )) {
                    viewHolder.binding.profileContainer.setVisibility( View.VISIBLE );
                    viewHolder.binding.ivCover.setVisibility( View.GONE );
                    Graphics.loadImage( model.getCoverImage(), viewHolder.binding.ivUserProfile );
                } else {
                    viewHolder.binding.ivCover.setVisibility( View.VISIBLE );
                    viewHolder.binding.profileContainer.setVisibility( View.GONE );
                    Graphics.loadImageWithFirstLetter( model.getCoverImage(), viewHolder.binding.ivCover, model.getName() );
                }
            }
            viewHolder.binding.tvChatTime.setText( "" );
            viewHolder.binding.messageContainer.setVisibility( View.GONE );
            ChatMessageModel lastMsgModel = ChatRepository.shared( Graphics.context ).getLastMessages( model.getId() );
            if (lastMsgModel != null) {
                viewHolder.binding.messageContainer.setVisibility( View.VISIBLE );
                viewHolder.binding.tvChatTime.setText( lastMsgModel.getDate( AppConstants.DATEFORMAT_24HOUR ) );
                if (lastMsgModel.getType().equals( AppConstants.MsgType.IMAGE.getId() )) {
                    viewHolder.binding.ivIcon.setVisibility( View.VISIBLE );
                    Glide.with( requireActivity() ).load( R.drawable.photo_msg ).into( viewHolder.binding.ivIcon );
                    viewHolder.binding.tvMsg.setText( "Photo" );
                } else if (lastMsgModel.getType().equals( AppConstants.MsgType.AUDIO.getId() )) {
                    viewHolder.binding.ivIcon.setVisibility( View.VISIBLE );
                    Glide.with( requireActivity() ).load( R.drawable.voice_record ).into( viewHolder.binding.ivIcon );
                    viewHolder.binding.tvMsg.setText( "Voice" );
                } else {
                    viewHolder.binding.ivIcon.setVisibility( View.GONE );
                    viewHolder.binding.tvMsg.setText( lastMsgModel.getMsg() );
                }

                long unReadCount = ChatRepository.shared(Graphics.context).getUnrealMessageCount(model.getId());
                viewHolder.binding.unreadCountContainer.setVisibility(unReadCount > 0 ? View.VISIBLE : View.GONE);
                viewHolder.binding.tvNotReadChatNumber.setText(String.valueOf(unReadCount));
            }

            viewHolder.binding.mainLayout.setOnClickListener( view -> {
                ChatModel chatModel = new ChatModel( model );
                Intent intent = new Intent( requireActivity(), ChatMessageActivity.class );
                intent.putExtra( "chatModel", new Gson().toJson( chatModel ) );
                startActivity( intent );
            } );
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final ItemChatBucklistBinding binding;
            public ViewHolder(@NonNull View itemView) {
                super( itemView );
                binding = ItemChatBucklistBinding.bind( itemView );
            }
        }
    }

    public class EventChatList<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder( UiUtils.getViewBy( parent, R.layout.item_chat_bucklist ) );
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            BucketEventListModel model = (BucketEventListModel) getItem( position );
            if (model == null) { return; }
            viewHolder.binding.profileContainer.setVisibility(View.VISIBLE);
            viewHolder.binding.ivCover.setVisibility(View.GONE);

            if (model.getOrg() != null){
                viewHolder.binding.eventOrganizeName.setVisibility(View.VISIBLE);
                viewHolder.binding.eventOrganizeName.setText(model.getOrg().getName());

            }

            viewHolder.binding.tvChatUserName.setText(model.getTitle());
            model.setTitle(viewHolder.binding.tvChatUserName.getText().toString());

            Graphics.loadImage( model.getImage(), viewHolder.binding.ivUserProfile );

            viewHolder.binding.tvChatTime.setText( "" );
            ChatMessageModel lastMsgModel = ChatRepository.shared( Graphics.context ).getLastMessages( model.getId() );
            viewHolder.binding.messageContainer.setVisibility( View.GONE );
            if (lastMsgModel != null) {
                viewHolder.binding.messageContainer.setVisibility( View.VISIBLE );
                viewHolder.binding.tvChatTime.setText( lastMsgModel.getDate( AppConstants.DATEFORMAT_24HOUR ) );
                if (lastMsgModel.getType().equals( AppConstants.MsgType.IMAGE.getId() )) {
                    viewHolder.binding.ivIcon.setVisibility( View.VISIBLE );
                    Glide.with( requireActivity() ).load( R.drawable.photo_msg ).into( viewHolder.binding.ivIcon );
                    viewHolder.binding.tvMsg.setText( "Photo" );
                } else if (lastMsgModel.getType().equals( AppConstants.MsgType.AUDIO.getId() )) {
                    viewHolder.binding.ivIcon.setVisibility( View.VISIBLE );
                    Glide.with( requireActivity() ).load( R.drawable.voice_record ).into( viewHolder.binding.ivIcon );
                    viewHolder.binding.tvMsg.setText( "Voice" );
                } else {
                    viewHolder.binding.ivIcon.setVisibility( View.GONE );
                    viewHolder.binding.tvMsg.setText( lastMsgModel.getMsg() );

                }

                long unReadCount = ChatRepository.shared(Graphics.context).getUnrealMessageCount(model.getId());
                viewHolder.binding.unreadCountContainer.setVisibility(unReadCount > 0 ? View.VISIBLE : View.GONE);
                viewHolder.binding.tvNotReadChatNumber.setText(String.valueOf(unReadCount));
            }

            viewHolder.binding.getRoot().setOnClickListener( v -> {
                if (model == null) { return; }
                ChatModel chatModel = new ChatModel( model );
                Intent intent = new Intent( requireActivity(), ChatMessageActivity.class );
                intent.putExtra( "chatModel", new Gson().toJson( chatModel ) );
                startActivity( intent );
            } );
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final ItemChatBucklistBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super( itemView );
                binding = ItemChatBucklistBinding.bind( itemView );

            }


        }
    }

    public class OutingChatList<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder( UiUtils.getViewBy( parent, R.layout.item_chat_bucklist ) );
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            InviteFriendModel model = (InviteFriendModel) getItem( position );

            if (model.getUser() != null){
                viewHolder.binding.eventOrganizeName.setVisibility(View.VISIBLE);
                viewHolder.binding.eventOrganizeName.setText(model.getUser().getFullName());
            }else {
                viewHolder.binding.eventOrganizeName.setVisibility(View.GONE);
            }

            if (model.getVenue() != null) {
                viewHolder.binding.profileContainer.setVisibility(View.VISIBLE);
                viewHolder.binding.ivCover.setVisibility(View.GONE);
                Graphics.loadImage( model.getVenue().getCover(), viewHolder.binding.ivUserProfile );
                viewHolder.binding.tvChatUserName.setText(model.getVenue().getName());
                model.setTitle(viewHolder.binding.tvChatUserName.getText().toString());
            } else {
                viewHolder.binding.profileContainer.setVisibility(View.GONE);
                viewHolder.binding.ivCover.setVisibility(View.VISIBLE);
                Graphics.loadImageWithFirstLetter( "", viewHolder.binding.ivCover, model.getTitle() );
            }

            viewHolder.binding.tvChatTime.setText( "" );
            viewHolder.binding.messageContainer.setVisibility( View.GONE );
            ChatMessageModel lastMsgModel = ChatRepository.shared( Graphics.context ).getLastMessages( model.getId() );
            if (lastMsgModel != null) {
                viewHolder.binding.messageContainer.setVisibility( View.VISIBLE );
                viewHolder.binding.tvChatTime.setText( lastMsgModel.getDate( AppConstants.DATEFORMAT_24HOUR ) );
                if (lastMsgModel.getType().equals( AppConstants.MsgType.IMAGE.getId() )) {
                    viewHolder.binding.ivIcon.setVisibility( View.VISIBLE );
                    Glide.with( requireActivity() ).load( R.drawable.photo_msg ).into( viewHolder.binding.ivIcon );
                    viewHolder.binding.tvMsg.setText( "Photo" );
                } else if (lastMsgModel.getType().equals( AppConstants.MsgType.AUDIO.getId() )) {
                    viewHolder.binding.ivIcon.setVisibility( View.VISIBLE );
                    Glide.with( requireActivity() ).load( R.drawable.voice_record ).into( viewHolder.binding.ivIcon );
                    viewHolder.binding.tvMsg.setText( "Voice" );
                } else {
                    viewHolder.binding.ivIcon.setVisibility( View.GONE );
                    viewHolder.binding.tvMsg.setText( lastMsgModel.getMsg() );
                }

                long unReadCount = ChatRepository.shared(Graphics.context).getUnrealMessageCount(model.getId());
                viewHolder.binding.unreadCountContainer.setVisibility(unReadCount > 0 ? View.VISIBLE : View.GONE);
                viewHolder.binding.tvNotReadChatNumber.setText(String.valueOf(unReadCount));
            }

            viewHolder.binding.getRoot().setOnClickListener( view -> {
                ChatModel chatModel = new ChatModel(model);
                Intent intent = new Intent( requireActivity(), ChatMessageActivity.class );
                intent.putExtra( "chatModel", new Gson().toJson( chatModel ) );
                startActivity( intent );
            } );
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final ItemChatBucklistBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super( itemView );
                binding = ItemChatBucklistBinding.bind( itemView );
            }
        }
    }

    // endregion
    // --------------------------------------
}