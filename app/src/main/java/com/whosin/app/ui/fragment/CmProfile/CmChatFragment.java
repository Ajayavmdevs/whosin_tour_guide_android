package com.whosin.app.ui.fragment.CmProfile;

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
import com.whosin.app.databinding.FragmentCmChatBinding;
import com.whosin.app.databinding.ItemCmChatDesignBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.ComplementaryProfileManager;
import com.whosin.app.service.models.BucketEventListModel;
import com.whosin.app.service.models.ChatModel;
import com.whosin.app.service.models.ComplimentaryProfileModel;
import com.whosin.app.service.models.ContactListModel;
import com.whosin.app.service.models.ContainerListModel;
import com.whosin.app.service.models.EventOrgDateModel;
import com.whosin.app.service.models.InviteFriendModel;
import com.whosin.app.service.models.PromoterChatModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.Promoter.ComplementaryEventDetailActivity;
import com.whosin.app.ui.activites.home.Chat.ChatMessageActivity;
import com.whosin.app.ui.fragment.comman.BaseFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class CmChatFragment extends BaseFragment {

    private FragmentCmChatBinding binding;

    private final PromoterChatListAdapter<PromoterChatModel> chatListAdapter = new PromoterChatListAdapter<>();

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    public void initUi(View view) {
        binding = FragmentCmChatBinding.bind( view );

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        requestChatPromoterContactList(true);
        binding.chatRecycler.setLayoutManager( new LinearLayoutManager( requireActivity(), LinearLayoutManager.VERTICAL, false ) );
        binding.chatRecycler.setAdapter( chatListAdapter );

        binding.swipeRefreshLayout.setProgressViewOffset(false,0,220);
        binding.headerView.isFromCM = true;
        binding.headerView.activity = requireActivity();
        if (ComplementaryProfileManager.shared.complimentaryProfileModel != null && ComplementaryProfileManager.shared.complimentaryProfileModel.getProfile() != null) {
            binding.headerView.setUpData(requireActivity(), ComplementaryProfileManager.shared.complimentaryProfileModel.getProfile());
        }

        ComplementaryProfileManager.shared.callbackForHeader = data -> {
            binding.headerView.setUpData(requireActivity(), data);
        };

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
        return R.layout.fragment_cm_chat;
    }

    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ComplimentaryProfileModel model) {
        requestChatPromoterContactList(false);
        if (ComplementaryProfileManager.shared.complimentaryProfileModel != null && ComplementaryProfileManager.shared.complimentaryProfileModel.getProfile() != null) {
            binding.headerView.setUpData(requireActivity(), ComplementaryProfileManager.shared.complimentaryProfileModel.getProfile());
        }
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void openGroupChat(PromoterChatModel model){

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
        DataService.shared( requireActivity() ).requestChatPromoterContactList( false, new RestCallback<ContainerListModel<PromoterChatModel>>(this) {
            @Override
            public void result(ContainerListModel<PromoterChatModel> model, String error) {
                hideProgress();
                binding.swipeRefreshLayout.setRefreshing(false);
                if (!Utils.isNullOrEmpty( error ) || model == null) {
                    Toast.makeText( activity, error, Toast.LENGTH_SHORT ).show();
                    return;
                }
                if (model.data != null && !model.data.isEmpty()) {
                    binding.chatRecycler.setVisibility( View.VISIBLE );
                    binding.emptyPlaceHolderView.setVisibility( View.GONE );
                    chatListAdapter.updateData( model.data );
                } else {
                    binding.chatRecycler.setVisibility( View.GONE );
                    binding.emptyPlaceHolderView.setVisibility( View.VISIBLE );
                }

            }
        } );
    }

    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------


    private final class PromoterChatListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder( UiUtils.getViewBy( parent, R.layout.item_cm_chat_design ) );
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            PromoterChatModel model = (PromoterChatModel) getItem( position );
            if (model == null){
                return;
            }
            viewHolder.binding.roundCount.setVisibility( View.GONE );
            viewHolder.binding.userName.setText( model.getVenueName() );
            Graphics.loadImage( model.getVenueImage(), viewHolder.binding.profileImage );
            viewHolder.binding.timeDate.setText( Utils.changeDateFormat( model.getDate(), AppConstants.DATEFORMAT_SHORT, AppConstants.DATEFORMT_MM_DATE + " | " ) );
            viewHolder.binding.startEndTime.setText(String.format("%s - %s", model.getStartTime(), model.getEndTime()));

            viewHolder.binding.roundCount.setVisibility( View.GONE );

            Graphics.loadImageWithFirstLetter(model.getOwner().getImage(), viewHolder.binding.imageProfile,model.getOwner().getFullName());
            viewHolder.binding.tvUserName.setText(String.format("%s %s", model.getOwner().getFirstName(), model.getOwner().getLastName()));


            viewHolder.binding.btnGroupChat.setOnClickListener( view -> {
                Utils.preventDoubleClick( view );
                if (model.getOwner() == null) {
                    return;
                }
                openGroupChat(model);

            } );

            viewHolder.binding.getRoot().setOnClickListener(v -> {
                Utils.preventDoubleClick(v);
                Intent intent = new Intent(activity, ComplementaryEventDetailActivity.class);
                intent.putExtra("eventId", model.getId());
                intent.putExtra("type", "complementary");
                activity.startActivity(intent);

            });



            boolean isLastItem = getItemCount() - 1 == position;

            if (isLastItem) {
                int marginBottom = Utils.getMarginBottom( holder.itemView.getContext(), 0.12f );
                Utils.setBottomMargin( holder.itemView, marginBottom );
            } else {
                Utils.setBottomMargin( holder.itemView, 0 );
            }


        }

        public final class ViewHolder extends RecyclerView.ViewHolder {

            private final ItemCmChatDesignBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super( itemView );
                binding = ItemCmChatDesignBinding.bind( itemView );

            }

        }
    }



    // endregion
    // --------------------------------------
}