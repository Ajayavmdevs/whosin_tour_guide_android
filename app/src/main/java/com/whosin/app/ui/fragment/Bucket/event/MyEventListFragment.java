package com.whosin.app.ui.fragment.Bucket.event;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.whosin.app.R;
import com.whosin.app.comman.AppExecutors;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.FragmentMyEventListBinding;
import com.whosin.app.databinding.FrindListItemBinding;
import com.whosin.app.service.Repository.ChatRepository;
import com.whosin.app.service.models.BucketEventListModel;
import com.whosin.app.service.models.InviteFriendModel;
import com.whosin.app.ui.activites.bucket.EventListActivity;
import com.whosin.app.ui.activites.bucket.OutingListActivity;
import com.whosin.app.ui.activites.home.event.EventDetailsActivity;
import com.whosin.app.ui.activites.home.event.InviteGuestListBottomSheet;
import com.whosin.app.ui.fragment.comman.BaseFragment;

public class MyEventListFragment extends BaseFragment {

    private FragmentMyEventListBinding binding;

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    public void initUi(View view) {
        binding = FragmentMyEventListBinding.bind(view);
        binding.eventRecyclerView.setLayoutManager( new LinearLayoutManager( Graphics.context, LinearLayoutManager.VERTICAL, false ) );
    }

    @Override
    public void setListeners() {
        binding.tvSeeAll.setOnClickListener( view -> {
            Utils.preventDoubleClick( view );
            startActivity( new Intent( context, EventListActivity.class ) );
        });

        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            requestBucketList(false, true);
        });
    }

    @Override
    public void populateData(boolean getDataFromServer) {
        requestBucketList(true, true);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_my_event_list;
    }

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestBucketList(boolean isShow, boolean shouldRefresh) {
        binding.swipeRefreshLayout.setRefreshing(isShow);
        ChatRepository.shared( requireActivity() ).getBucketChatList( shouldRefresh, data -> {
            AppExecutors.get().mainThread().execute(() -> {
                hideProgress();
                binding.swipeRefreshLayout.setRefreshing(false);
                boolean isDataEmpty = data == null || data.getEventModels().isEmpty();
                binding.eventRecyclerView.setVisibility(isDataEmpty ? View.GONE : View.VISIBLE);
                binding.emptyPlaceHolderView.setVisibility(isDataEmpty ? View.VISIBLE : View.GONE);
//                if (data.getEventModels() != null && !data.getEventModels().isEmpty()) {
//                    eventListAdapter.updateData( data.getEventModels() );
//                }
//                binding.eventRecyclerView.setVisibility(data.getEventModels().isEmpty() ? View.GONE : View.VISIBLE);
//                binding.emptyPlaceHolderView.setVisibility(data.getEventModels().isEmpty() ? View.VISIBLE : View.GONE);
            });
        } );
    }

    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    public class EventUserListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new EventUserListAdapter.ViewHolder( UiUtils.getViewBy( parent, R.layout.frind_list_item ) );
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            EventUserListAdapter.ViewHolder viewHolder = (EventUserListAdapter.ViewHolder) holder;
            InviteFriendModel model = (InviteFriendModel) getItem( position );

            if (model != null) {
                if (model.getUser() != null) {
                    viewHolder.mBinding.txtUserName.setText( model.getUser().getFirstName() );
                    Graphics.loadImageWithFirstLetter( model.getUser().getImage(), viewHolder.mBinding.imgUserLogo, model.getUser().getFirstName() );
                }

                if (model.getInviteStatus().equals( "pending" )) {
                    viewHolder.mBinding.iconStatus.setImageResource( R.drawable.icon_pending );
                } else if (model.getInviteStatus().equals( "in" )) {
                    viewHolder.mBinding.iconStatus.setImageResource( R.drawable.icon_complete );
                } else if (model.getInviteStatus().equals( "out" )) {
                    viewHolder.mBinding.iconStatus.setImageResource( R.drawable.icon_deleted);
                }

                viewHolder.itemView.setOnClickListener( view -> {
                    Utils.preventDoubleClick( view );
                    InviteGuestListBottomSheet inviteGuestListBottomSheet = new InviteGuestListBottomSheet();
                    inviteGuestListBottomSheet.eventId = model.getEventId();
                    inviteGuestListBottomSheet.show( getChildFragmentManager(), "" );
                } );
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private FrindListItemBinding mBinding;

            public ViewHolder(@NonNull View itemView) {
                super( itemView );
                mBinding = FrindListItemBinding.bind( itemView );
            }
        }
    }

    // endregion
    // --------------------------------------
}