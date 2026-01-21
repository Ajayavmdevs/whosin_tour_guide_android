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
import com.whosin.app.databinding.BucketEventListItemBinding;
import com.whosin.app.databinding.FragmentMyEventListBinding;
import com.whosin.app.databinding.FrindListItemBinding;
import com.whosin.app.service.Repository.ChatRepository;
import com.whosin.app.service.models.BucketEventListModel;
import com.whosin.app.service.models.InviteFriendModel;
import com.whosin.app.ui.activites.bucket.EventListActivity;
import com.whosin.app.ui.activites.bucket.OutingListActivity;
import com.whosin.app.ui.activites.home.event.EventDetailsActivity;
import com.whosin.app.ui.activites.home.event.InviteGuestListBottomSheet;
import com.whosin.app.ui.fragment.Bucket.BucketListFragment;
import com.whosin.app.ui.fragment.comman.BaseFragment;

public class MyEventListFragment extends BaseFragment {

    private FragmentMyEventListBinding binding;
    private final EventListAdapter<BucketEventListModel> eventListAdapter = new EventListAdapter<>();

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    public void initUi(View view) {
        binding = FragmentMyEventListBinding.bind(view);
        binding.eventRecyclerView.setLayoutManager( new LinearLayoutManager( Graphics.context, LinearLayoutManager.VERTICAL, false ) );
        binding.eventRecyclerView.setAdapter(eventListAdapter);
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
                if (data != null && data.getEventModels() != null && !data.getEventModels().isEmpty()) {
                    eventListAdapter.updateData(data.getEventModels());
                }
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

    public class EventListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = UiUtils.getViewBy( parent, R.layout.bucket_event_list_item );
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.width = (int) (Graphics.getScreenWidth( context ) * 0.93);
            view.setLayoutParams( params );
            return new EventListAdapter.ViewHolder( view );
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            EventListAdapter.ViewHolder viewHolder = (EventListAdapter.ViewHolder) holder;
            boolean isLastItem = position == getItemCount() - 1;
            BucketEventListModel model = (BucketEventListModel) getItem( position );

            if (model.getVenue() != null) {
                viewHolder.mBinding.txtUserName.setText( model.getVenue().getName() );
                viewHolder.mBinding.tvAddress.setText( model.getVenue().getAddress() );
                Graphics.loadRoundImage( model.getVenue().getLogo(), viewHolder.mBinding.imgUserLogo);
            }

            viewHolder.mBinding.tvDescription.setText( model.getDescription() );
            viewHolder.mBinding.txtEventName.setText( model.getTitle() );
            Graphics.loadImage( model.getImage(), viewHolder.mBinding.ivCover );

            if (model.getOrg() != null) {
                viewHolder.mBinding.txtOrgName.setText( model.getOrg().getName() );
                Graphics.loadRoundImage( model.getOrg().getLogo(), viewHolder.mBinding.imgOrgImage);
            }

            viewHolder.mBinding.txtDate.setText( Utils.convertMainDateFormat( model.getEventTime() ) );
            viewHolder.mBinding.txtTime.setText( Utils.convertMainTimeFormat( model.getReservationTime() ) + " - " + Utils.convertMainTimeFormat( model.getEventTime() ) );

            if (model.getEvent_status().equals( "completed" )) {
                viewHolder.mBinding.txtStatus.setText( "completed" );
                viewHolder.mBinding.layoutStatus.setBackgroundColor( ContextCompat.getColor( getActivity(), R.color.in_green ) );
                viewHolder.mBinding.constraint.setBackground( ContextCompat.getDrawable( context, R.drawable.stroke_gradiant_line_in ) );
            } else if (model.getEvent_status().equals( "cancelled" )) {
                viewHolder.mBinding.txtStatus.setText( "cancelled" );
                viewHolder.mBinding.layoutStatus.setBackgroundColor( ContextCompat.getColor( getActivity(), R.color.status_pink ) );
                viewHolder.mBinding.constraint.setBackground( ContextCompat.getDrawable( context, R.drawable.event_stroke_gradiant_line_out ) );
            } else {
                if (model.getMyInvitationStatus() != null) {
                    if (model.getMyInvitationStatus().equals( "pending" )) {
                        viewHolder.mBinding.txtStatus.setText( "pending" );
                        viewHolder.mBinding.layoutStatus.setBackgroundColor( ContextCompat.getColor( getActivity(), R.color.pending_yellow ) );
                        viewHolder.mBinding.constraint.setBackground( ContextCompat.getDrawable( context, R.drawable.stroke_gradiant_line_pending ) );
                    } else if (model.getMyInvitationStatus().equals( "in" )) {
                        viewHolder.mBinding.txtStatus.setText( "in" );
                        viewHolder.mBinding.layoutStatus.setBackgroundColor( ContextCompat.getColor( getActivity(), R.color.in_green ) );
                        viewHolder.mBinding.constraint.setBackground( ContextCompat.getDrawable( context, R.drawable.stroke_gradiant_line_in ) );
                    } else {
                        viewHolder.mBinding.txtStatus.setText( model.getMyInvitationStatus() );
                        viewHolder.mBinding.layoutStatus.setBackgroundColor( ContextCompat.getColor( getActivity(), R.color.status_pink ) );
                        viewHolder.mBinding.constraint.setBackground( ContextCompat.getDrawable( context, R.drawable.event_stroke_gradiant_line_out ) );
                    }
                }
            }

            if (model.getInvitedUsers() != null) {
                model.getInvitedUsers().removeIf( item -> item.getUser() == null );
                viewHolder.eventUserListAdapter.updateData( model.getInvitedUsers() );
            }

            viewHolder.mBinding.getRoot().setOnClickListener( view -> {
                String orgName = (model.getOrg() != null && model.getOrg().getName() != null) ? model.getOrg().getName() : "";
                String orgWebsite = (model.getOrg() != null && model.getOrg().getWebsite() != null) ? model.getOrg().getWebsite() : "";
                String orgLogo = (model.getOrg() != null && model.getOrg().getLogo() != null) ? model.getOrg().getLogo() : "";

                startActivity( new Intent( context, EventDetailsActivity.class )
                        .putExtra( "eventsList", new Gson().toJson( model ) )
                        .putExtra( "name", orgName )
                        .putExtra( "address", orgWebsite )
                        .putExtra( "image", orgLogo )
                );
            } );

            if (isLastItem) {
                int marginBottom = Utils.getMarginBottom( holder.itemView.getContext(), 0.10f );
                Utils.setBottomMargin( holder.itemView, marginBottom );
            } else {
                Utils.setBottomMargin( holder.itemView, 0 );
            }

            int topBottom = Utils.getMarginTop( holder.itemView.getContext(), 0.01f );
            Utils.setTopMargin( holder.itemView, topBottom );

        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private BucketEventListItemBinding mBinding;
            private EventUserListAdapter eventUserListAdapter;

            public ViewHolder(@NonNull View itemView) {
                super( itemView );
                mBinding = BucketEventListItemBinding.bind( itemView );
                eventUserListAdapter = new EventUserListAdapter<>();
                mBinding.friendRecycler.setLayoutManager( new LinearLayoutManager( requireActivity(), LinearLayoutManager.HORIZONTAL, false ) );
                mBinding.friendRecycler.setAdapter( eventUserListAdapter );
                mBinding.friendRecycler.setNestedScrollingEnabled( false );
            }
        }
    }

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