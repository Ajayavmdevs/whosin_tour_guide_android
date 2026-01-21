package com.whosin.app.ui.activites.Profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.tapadoo.alerter.Alerter;
import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.ActivityBlockUserListBinding;
import com.whosin.app.databinding.ItemBlockUserListBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.BlockUserManager;
import com.whosin.app.service.models.CommonModel;
import com.whosin.app.service.models.ContainerListModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.InviteFriendModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.bucket.OutingListActivity;
import com.whosin.app.ui.activites.comman.BaseActivity;

import java.util.List;

public class BlockUserListActivity extends BaseActivity {


    private ActivityBlockUserListBinding binding;

    private final BlockedUserListAdapter<UserDetailModel> adapter = new BlockedUserListAdapter<>();

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------


    @Override
    protected void initUi() {

        binding.blockUserListTitle.setText(getValue("blocked_users_list"));
        binding.emptyPlaceHolderView.setEmptyPlaceTxtTitle(getValue("no_blocked_users"));

        binding.blockUserRecycler.setLayoutManager( new LinearLayoutManager( activity, LinearLayoutManager.VERTICAL, false ) );
        binding.blockUserRecycler.setAdapter( adapter );

        requestBlockUserList();
        adapter.notifyDataSetChanged();

    }

    @Override
    protected void setListeners() {

        binding.ivClose.setOnClickListener( v -> {
            onBackPressed();
        } );
    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityBlockUserListBinding.inflate( getLayoutInflater() );
        return binding.getRoot();
    }
  /*  private void reloadData() {
        BlockUserListActivity parentActivity = (BlockUserListActivity)activity;
        if (parentActivity != null) {
            showProgress();
            parentActivity.requestBlockUserList();
        }
    }*/
    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------
    public void updateData(List<UserDetailModel> data) {
        if (data != null && !data.isEmpty()) {
            binding.emptyPlaceHolderView.setVisibility( View.GONE );
            binding.blockUserRecycler.setVisibility( View.VISIBLE );
            adapter.updateData( data );
        } else {
            binding.emptyPlaceHolderView.setVisibility( View.VISIBLE );
            binding.blockUserRecycler.setVisibility( View.GONE );
        }


    }

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestBlockUserList() {
        showProgress();
        DataService.shared( activity ).requestUserBlockList( new RestCallback<ContainerListModel<UserDetailModel>>(this) {
            @Override
            public void result(ContainerListModel<UserDetailModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty( error ) || model == null) {
                    Toast.makeText( activity, R.string.service_message_something_wrong, Toast.LENGTH_SHORT ).show();
                    return;
                }
                if (model.data != null && !model.data.isEmpty()) {
                    binding.emptyPlaceHolderView.setVisibility( View.GONE );
                    binding.blockUserRecycler.setVisibility( View.VISIBLE );
                    adapter.updateData( model.data );
//                    updateData( model.data );
//                    reloadData();
                } else {
                    binding.emptyPlaceHolderView.setVisibility( View.VISIBLE );
                    binding.blockUserRecycler.setVisibility( View.GONE );
                }
//                updateData( model.data );
            }
        } );
    }

    private void requestBlockUserRemove(String id, String name) {
        DataService.shared( activity ).requestUserBlockRemove( id, new RestCallback<ContainerModel<CommonModel>>(this) {
            @Override
            public void result(ContainerModel<CommonModel> model, String error) {
                if (!Utils.isNullOrEmpty( error ) || model == null) {
                    Toast.makeText( activity, R.string.service_message_something_wrong, Toast.LENGTH_SHORT ).show();
                    return;
                }
                BlockUserManager.deleteBlockUserId(id);
                Alerter.create( activity ).setTitle( "Thank You!" ).setText("You have Unblocked" + name ).setTextAppearance( R.style.AlerterText ).setTitleAppearance( R.style.AlerterTitle ).setBackgroundColorRes( R.color.AlerterSuccessBg ).hideIcon().show();
                requestBlockUserList();
            }
        } );
    }

    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    public class BlockedUserListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder( UiUtils.getViewBy( parent, R.layout.item_block_user_list ) );

        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            UserDetailModel model = (UserDetailModel) getItem( position );
            Graphics.loadImageWithFirstLetter( model.getImage(), viewHolder.binding.ivUserProfile, model.getFullName() );
            viewHolder.binding.tvUserName.setText( model.getFullName() );
            viewHolder.binding.tvFollow.setOnClickListener( v -> {

                Graphics.showAlertDialogWithOkCancel( activity, getString(R.string.app_name), Utils.setLangValue("block_user_alert",model.getFullName()), aBoolean -> {
                    if (aBoolean) {
                        requestBlockUserRemove( model.getId(), model.getFullName() );
                    }
                } );
            } );

        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private ItemBlockUserListBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super( itemView );
                binding = ItemBlockUserListBinding.bind( itemView );
            }
        }

    }


    // endregion
    // --------------------------------------

}