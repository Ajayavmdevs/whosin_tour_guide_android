package com.whosin.app.ui.activites.Profile;

import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.whosin.app.R;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.databinding.ActivityFollowingBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.ContactListModel;
import com.whosin.app.service.models.ContainerListModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.comman.BaseActivity;
import com.whosin.app.ui.adapter.FriendsAdapter;

import java.util.List;

public class FollowingActivity extends BaseActivity {

    private ActivityFollowingBinding binding;

    private FriendsAdapter<ContactListModel> followingListAdapter;

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    protected void initUi() {

        binding.tvFollowingTitle.setText(getValue("following"));

        binding.followingList.setLayoutManager( new LinearLayoutManager( activity, LinearLayoutManager.VERTICAL, false ) );
        followingListAdapter = new FriendsAdapter<>(activity);
        followingListAdapter.setCallback(data -> {
            if (Boolean.TRUE.equals(data)) {
                requestFollowingList();
            }
        });
        binding.followingList.setAdapter( followingListAdapter );

        String userId = getIntent().getStringExtra( "id" );
        if (TextUtils.isEmpty( userId )) {
            return;
        }
        if (SessionManager.shared.getUser().getId().equals(userId)) {
            List<ContactListModel> followingList = SessionManager.shared.getFollowingData();
            if (!followingList.isEmpty() ) {
                followingListAdapter.updateData(followingList);
            }
        }
    }

    @Override
    protected void setListeners() {

        binding.ivBack.setOnClickListener( view -> onBackPressed() );

    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityFollowingBinding.inflate( getLayoutInflater() );
        return binding.getRoot();
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestFollowingList();
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    public void requestFollowingList() {
        String userId = getIntent().getStringExtra( "id" );
        if (TextUtils.isEmpty( userId )) {
            return;
        }
        if (followingListAdapter.getData().isEmpty()){
            showProgress();
        }
        DataService.shared( activity ).requestFollowingList( userId, new RestCallback<ContainerListModel<ContactListModel>>(this) {
            @Override
            public void result(ContainerListModel<ContactListModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty( error ) || model == null) {
                    Toast.makeText( activity, R.string.service_message_something_wrong, Toast.LENGTH_SHORT ).show();
                    return;
                }
                if (model.data != null && !model.data.isEmpty()) {
                    binding.followingList.setVisibility( View.VISIBLE );
                    binding.emptyPlaceHolderView.setVisibility( View.GONE );
                    if (userId.equals(SessionManager.shared.getUser().getId())) {
                        SessionManager.shared.saveFollowingData(model.data);
                    }
                    followingListAdapter.updateData( model.data );
                } else {
                    binding.followingList.setVisibility( View.GONE );
                    binding.emptyPlaceHolderView.setVisibility( View.VISIBLE );
                }
            }
        } );
    }

    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

}