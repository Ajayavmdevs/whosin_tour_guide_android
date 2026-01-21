package com.whosin.app.ui.fragment.Bucket.outing;

import static android.app.Activity.RESULT_OK;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.whosin.app.R;
import com.whosin.app.comman.AppExecutors;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.AllOutingListItemBinding;
import com.whosin.app.databinding.FragmentAllOutingBinding;
import com.whosin.app.databinding.FrindListItemBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.CommanMsgModel;
import com.whosin.app.service.models.ContactListModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.InviteFriendModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.models.VenueObjectModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.Profile.OtherUserProfileActivity;
import com.whosin.app.ui.activites.bucket.MyInvitationActivity;
import com.whosin.app.ui.activites.bucket.OutingListActivity;
import com.whosin.app.ui.activites.bucket.TransferOwnershipBottomSheet;
import com.whosin.app.ui.activites.home.event.InviteGuestListBottomSheet;
import com.whosin.app.ui.fragment.comman.BaseFragment;
import com.whosin.app.ui.fragment.home.InviteFriendBottomSheet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AllOutingFragment extends BaseFragment {

    private FragmentAllOutingBinding binding;
    private final OutingListAdapter<InviteFriendModel> outingListAdapter = new OutingListAdapter<>();

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    public void initUi(View view) {
        binding = FragmentAllOutingBinding.bind( view );
        binding.MyOutingRecycler.setLayoutManager( new LinearLayoutManager( requireActivity(), LinearLayoutManager.VERTICAL, false ) );
        binding.MyOutingRecycler.setAdapter( outingListAdapter );
    }

    @Override
    public void setListeners() {
        
    }

    @Override
    public void populateData(boolean getDataFromServer) {

    }

    @Override
    public void onResume() {
        super.onResume();
        reloadData();
    }

    public void updateData(List<InviteFriendModel> data) {
        if (binding == null) { return; }
        if (data != null && !data.isEmpty()) {
            binding.emptyPlaceHolderView.setVisibility( View.GONE );
            binding.MyOutingRecycler.setVisibility( View.VISIBLE );
            outingListAdapter.updateData( data );
        } else {
            binding.emptyPlaceHolderView.setVisibility( View.VISIBLE );
            binding.MyOutingRecycler.setVisibility( View.GONE );
        }


    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_all_outing;
    }

    private void reloadData() {
        OutingListActivity parentActivity = (OutingListActivity) getActivity();
        if (parentActivity != null) {
            parentActivity.requestMyOutingList();
        }
    }

    // endregion
    // --------------------------------------
    // region pivate
    // --------------------------------------



    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    public class OutingListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder( UiUtils.getViewBy( parent, R.layout.all_outing_list_item ) );
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            InviteFriendModel model = (InviteFriendModel) getItem( position );

            if (model != null) {
                viewHolder.setCreatorValue( model );

                if (model.getVenue() != null) {
                    viewHolder.setVenueData( model.getVenue() );
                }

                if (model.getInvitedUser() != null && !model.getInvitedUser().isEmpty()) {
                    viewHolder.setInvitedUsers( model.getInvitedUser() );
                }

                if (model.getStatus().equals( "completed" )) {
                    viewHolder.mBinding.txtImOutBtn.setText( "completed" );
                    viewHolder.mBinding.btnImOut.setClickable( !model.getStatus().equals( "completed" ) );

                    viewHolder.mBinding.txtImOutBtn.setTextColor( ContextCompat.getColor(context, R.color.green) );
                    viewHolder.mBinding.btnImOut.setBackgroundResource( android.R.color.transparent );
                    viewHolder.mBinding.layoutEdit.setVisibility( View.GONE );
                } else {
                    viewHolder.mBinding.layoutEdit.setVisibility( model.isAllowEdit() ? View.VISIBLE : View.GONE );
                    viewHolder.setStatusData( model );
                }

                viewHolder.mBinding.layoutStatus.setVisibility( model.isOwnerOfOuting() ? View.GONE : View.VISIBLE );
                viewHolder.mBinding.txtStatus.setText( model.getStatus() );
                Log.d("TAG", "onBindViewHolder: "+model.getStatus());

                try {
                    SimpleDateFormat inputFormat = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US );
                    Date date = inputFormat.parse( model.getCreatedAt() );
                    SimpleDateFormat outputFormat = new SimpleDateFormat( "E, d MMM yyyy", Locale.US );
                    String formattedDate = outputFormat.format( date );
                    viewHolder.mBinding.createDate.setText( String.format( "Created date: %s", formattedDate ) );
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                String invitedId;
                if (model.isOwnerOfOuting()) {
                    invitedId = "";
                    viewHolder.mBinding.layoutStatus.setBackgroundColor( ContextCompat.getColor( getActivity(), R.color.button_pink ) );
                    viewHolder.mBinding.constraint.setBackground( ContextCompat.getDrawable( context, R.drawable.stroke_gradiant_line_me ) );
                } else {
                    ContactListModel invitedUser = model.getInvitedUser().stream().filter( model1 -> model1.getUserId().equals( SessionManager.shared.getUser().getId() ) ).findFirst().orElse( null );
                    invitedId = invitedUser.getInviteId();
                    if (invitedUser != null) {
                        viewHolder.mBinding.layoutStatus.setBackgroundColor( ContextCompat.getColor( getActivity(), invitedUser.getStatusColor() ) );
                        viewHolder.mBinding.constraint.setBackground( ContextCompat.getDrawable( context, invitedUser.getStatusBorder() ) );
                    }
                    viewHolder.mBinding.layout.setOnClickListener(view -> {
                        if (model.getUser() != null) {
                            startActivity(new Intent(requireActivity(), OtherUserProfileActivity.class).putExtra("friendId", model.getUser().getId()));
                        }
                    });
                }

                viewHolder.mBinding.tvOutingTitle.setText( model.getTitle() );
                viewHolder.mBinding.txtExtraGuest.setText( String.valueOf( model.getExtraGuest() ) );

                viewHolder.mBinding.txtDate.setText( Utils.convertDateFormat( model.getDate(), "yyyy-MM-dd" ) );
                viewHolder.mBinding.txtTime.setText( String.format( "%s - %s", Utils.convert24HourTimeFormat( model.getStartTime() ), Utils.convert24HourTimeFormat( model.getEndTime() ) ) );


                viewHolder.mBinding.layoutEdit.setOnClickListener( view -> {
                    Utils.preventDoubleClick( view );

                    InviteFriendBottomSheet inviteFriendDialog = new InviteFriendBottomSheet();
                    inviteFriendDialog.inviteFriendModel = model;
                    inviteFriendDialog.setShareListener( data -> AppExecutors.get().mainThread().execute( () -> reloadData()));

                    inviteFriendDialog.show( getChildFragmentManager(), "1" );
                } );

                viewHolder.mBinding.btnDeletePermanently.setOnClickListener(v -> {
                            Graphics.showAlertDialogWithOkCancel( context, getString(R.string.app_name), "Are you sure want to delete invitation ?", aBoolean -> {
                                if (aBoolean) {
                                    requestDeleteInvitation(invitedId);
                                }
                            });
                        });

                viewHolder.mBinding.btnImIn.setOnClickListener( view -> requestUpdateInviteStatus( model, "in" ));

                viewHolder.mBinding.btnImOut.setOnClickListener( view -> {
                    if (!model.isOwnerOfOuting()) {
                        requestUpdateInviteStatus( model, "out" );
                    } else {
                        viewHolder.changeOwenwerShip( model );
                        // TODO: need to handle change ownersheet
                    }
                } );

                viewHolder.mBinding.getRoot().setOnClickListener( view -> {
                    Intent intent = new Intent(getActivity(), MyInvitationActivity.class);
                    intent.putExtra("id", model.getId());
                    intent.putExtra("notificationType", "notification");
                    activityLauncher.launch( intent, result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            boolean isClose = result.getData().getBooleanExtra("close",false);
                            if (isClose) {
                                reloadData();
                            }
                        }
                    } );
                   // startActivity( new Intent( context, MyInvitationActivity.class ).putExtra("id", model.getId()).putExtra("notificationType", "notification") );
                } );

                viewHolder.mBinding.btnSeeAll.setOnClickListener( view -> {
                    Utils.preventDoubleClick( view );
                    InviteGuestListBottomSheet inviteGuestListBottomSheet = new InviteGuestListBottomSheet();
                    inviteGuestListBottomSheet.model = model.getInvitedUser();
                    inviteGuestListBottomSheet.type = "outing";
                    inviteGuestListBottomSheet.show(getChildFragmentManager(), "");
                });
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final AllOutingListItemBinding mBinding;
            private final FriendListAdapter<ContactListModel> friendListAdapter = new FriendListAdapter<>();


            public ViewHolder(@NonNull View itemView) {
                super( itemView );
                mBinding = AllOutingListItemBinding.bind( itemView );
                mBinding.friendRecycler.setLayoutManager( new LinearLayoutManager( requireActivity(), LinearLayoutManager.HORIZONTAL, false ) );
                mBinding.friendRecycler.setAdapter( friendListAdapter );

            }

            public void setCreatorValue(InviteFriendModel model) {
                mBinding.txtOutingDescribe.setVisibility( model.isOwnerOfOuting() ? View.GONE : View.VISIBLE );
                if (model.getUser() != null) {
                    mBinding.txtOutingTitle.setText( model.isOwnerOfOuting() ? "You created" : model.getUser().getFullName() );
                    Graphics.loadImageWithFirstLetter( model.getUser().getImage(), mBinding.imageLogo, model.getUser().getFullName() );
                } else {
                    mBinding.txtOutingTitle.setText( model.isOwnerOfOuting() ? "You created" : "" );
                    Graphics.loadImageWithFirstLetter( "", mBinding.imageLogo, SessionManager.shared.getUser().getFullName() );
                }

            }

            public void setVenueData(VenueObjectModel venue) {
                if (venue != null) {
                    mBinding.txtUserName.setText( venue.getName() );
                    mBinding.tvAddress.setText( venue.getAddress() );
                    Graphics.loadImage( venue.getCover(), mBinding.ivCover );
                    Graphics.loadImageWithFirstLetter( venue.getLogo(), mBinding.imgUserLogo, venue.getName() );
                }
            }

            public void setStatusData(InviteFriendModel model) {
                mBinding.btnImIn.setVisibility( View.GONE );
                mBinding.btnDeletePermanently.setVisibility( View.GONE );
                mBinding.btnImOut.setVisibility( View.VISIBLE );

                mBinding.txtImOutBtn.setText( model.getStatus().equals( "cancelled" ) ? "cancelled" : "Cancel Invitation" );
                mBinding.btnImOut.setBackgroundResource( model.getStatus().equals( "cancelled" ) ? android.R.color.transparent : R.drawable.cancel_button_stroke );
                mBinding.btnImOut.setClickable( !model.getStatus().equals( "cancelled" ) );
                mBinding.txtImOutBtn.setTextColor( model.getStatus().equals( "cancelled" ) ? getColor( R.color.brand_pink ) : getColor( R.color.white ) );


                if (!model.isOwnerOfOuting() && !model.getStatus().equals( "completed" )) {
                    ContactListModel invitedUser = model.getInvitedUser().stream().filter( model1 -> model1.getUserId().equals( SessionManager.shared.getUser().getId() ) ).findFirst().orElse( null );
                    if (invitedUser != null) {
                        mBinding.btnImIn.setVisibility( (invitedUser.getInviteStatus().equals( "pending" ) || invitedUser.getInviteStatus().equals( "out" )) ? View.VISIBLE : View.GONE );
                        mBinding.btnImOut.setVisibility( (invitedUser.getInviteStatus().equals( "pending" ) || invitedUser.getInviteStatus().equals( "in" )) ? View.VISIBLE : View.GONE );
                        mBinding.txtImOutBtn.setText( invitedUser.getInviteStatus().equals( "pending" ) ? "I'm OUT" : "Cancel" );
                    if (!model.isOwnerOfOuting()) {
                        mBinding.btnDeletePermanently.setVisibility( invitedUser.getInviteStatus().equals( "out" )  ? View.VISIBLE : View.GONE );

                    }
                    }
                }

            }

            public void setInvitedUsers(List<ContactListModel> invitedUser) {
                friendListAdapter.updateData( invitedUser );
            }

            public void changeOwenwerShip(InviteFriendModel model) {
                ArrayList<String> data = new ArrayList<>();
                data.add( "Change Ownership" );
                data.add( "Cancel Invitation" );
                Graphics.showActionSheet( requireActivity(), getString(R.string.app_name), data, (data1, position1) -> {
                    if (position1 == 0 ) {
                        TransferOwnershipBottomSheet transferOwnershipBottomSheet = new TransferOwnershipBottomSheet();
                        transferOwnershipBottomSheet.inviteFriendModel = model;
                        transferOwnershipBottomSheet.callback = (success, error) -> {
                            if (success) {
                                reloadData();
                            }
                        };
                        transferOwnershipBottomSheet.show( getChildFragmentManager(), "1" );
                    } else {
                        requestUpdateOuting( model.getId() );
                    }
                } );
            }

            private int getColor(int resId) {
                return ContextCompat.getColor(context, resId);
            }
        }

        private void requestUpdateInviteStatus(InviteFriendModel model, String status) {
            showProgress();
            DataService.shared( context ).requestUpdateInviteStatus( model.getId(), status, new RestCallback<ContainerModel<InviteFriendModel>>() {
                @Override
                public void result(ContainerModel<InviteFriendModel> model, String error) {
                    hideProgress();
                    if (!Utils.isNullOrEmpty( error ) || model == null) {
                        Toast.makeText( context, error, Toast.LENGTH_SHORT ).show();
                        return;
                    }
                    Toast.makeText( context, model.message, Toast.LENGTH_SHORT ).show();
                    reloadData();
                }
            } );
        }

        private void requestDeleteInvitation(String outingId) {
            showProgress();
            DataService.shared(context).requestDeleteInvitation(outingId, new RestCallback<ContainerModel<UserDetailModel>>() {
                @Override
                public void result(ContainerModel<UserDetailModel> model, String error) {
                    hideProgress();
                    if (!Utils.isNullOrEmpty(error) || model == null) {
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Toast.makeText(context, model.message, Toast.LENGTH_SHORT).show();
                    reloadData();

                }
            });
        }

        private void requestUpdateOuting(String outingId) {
            JsonObject object = new JsonObject();
            object.addProperty( "outingId", outingId );
            object.addProperty( "status", "cancelled" );
            DataService.shared( getActivity() ).requestUpdateOuting( object, new RestCallback<ContainerModel<InviteFriendModel>>() {
                @Override
                public void result(ContainerModel<InviteFriendModel> model, String error) {
                    if (!Utils.isNullOrEmpty( error ) || model == null) {
                        Log.d( "TAG", "result: " + error );
                        Toast.makeText( getContext(), error, Toast.LENGTH_SHORT ).show();
                        return;
                    }
                    reloadData();
                }
            } );
        }


    }

    public  class FriendListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {


        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder( UiUtils.getViewBy( parent, R.layout.frind_list_item ) );
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            ContactListModel model = (ContactListModel) getItem( position );
            Graphics.loadImageWithFirstLetter( model.getImage(), viewHolder.mBinding.imgUserLogo, model.getFirstName() );
            viewHolder.mBinding.txtUserName.setText( SessionManager.shared.getUser().getId().equals( model.getUserId() ) ? "Me" : model.getFirstName() );
            Utils.setInvitationStatus( viewHolder.mBinding.iconStatus, model.getInviteStatus() );


        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final FrindListItemBinding mBinding;

            public ViewHolder(@NonNull View itemView) {
                super( itemView );
                mBinding = FrindListItemBinding.bind( itemView );
            }


        }



    }

    // --------------------------------------
    // endregion

}