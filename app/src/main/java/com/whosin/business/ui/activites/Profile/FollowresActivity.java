package com.whosin.business.ui.activites.Profile;

import static android.view.View.VISIBLE;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.whosin.business.R;
import com.whosin.business.comman.Graphics;
import com.whosin.business.comman.Utils;
import com.whosin.business.databinding.ActivityFollowresBinding;
import com.whosin.business.service.DataService;
import com.whosin.business.service.manager.SessionManager;
import com.whosin.business.service.models.ContactListModel;
import com.whosin.business.service.models.ContainerListModel;
import com.whosin.business.service.models.UserDetailModel;
import com.whosin.business.service.rest.RestCallback;
import com.whosin.business.ui.activites.comman.BaseActivity;
import com.whosin.business.ui.adapter.FriendsAdapter;

import java.util.List;
import java.util.Objects;

public class FollowresActivity extends BaseActivity {

    private ActivityFollowresBinding binding;
    private FriendsAdapter<ContactListModel> followersListAdaptere;

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    protected void initUi() {

        binding.tvFollowersTitle.setText(getValue("followers"));

        String id = getIntent().getStringExtra( "id" );
        binding.followersList.setLayoutManager( new LinearLayoutManager( activity, LinearLayoutManager.VERTICAL, false ) );
        followersListAdaptere = new FriendsAdapter<>(activity);
        Utils.setSelectedStatus(Utils.setType.NONE);
        binding.followersList.setAdapter( followersListAdaptere );
        if (Objects.equals(SessionManager.shared.getUser().getId(), id)) {
            List<ContactListModel> followerList = SessionManager.shared.getFollowersData();
            if (!followerList.isEmpty()) {
                followersListAdaptere.updateData(followerList);
            }
        }
        requestUserFollowList();
    }

    @Override
    protected void setListeners() {
        binding.ivBack.setOnClickListener( view -> onBackPressed() );
        binding.constrainFollowRequest.setOnClickListener( view -> startActivity( new Intent( activity, FollowRequestActivity.class ) ) );
    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityFollowresBinding.inflate( getLayoutInflater() );
        return binding.getRoot();
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestFollowersList();
        requestUserFollowList();
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestUserFollowList() {
        DataService.shared( activity ).requestUserFollowList(new RestCallback<ContainerListModel<UserDetailModel>>(this) {
            @Override
            public void result(ContainerListModel<UserDetailModel> model, String error) {
                if (!Utils.isNullOrEmpty( error ) || model == null) {
                    Toast.makeText( activity, error, Toast.LENGTH_SHORT ).show();
                    return;
                }
                if (model.data != null && !model.data.isEmpty()) {
                    binding.constrainFollowRequest.setVisibility(VISIBLE);
                    Graphics.loadImageWithFirstLetter(model.data.get(0).getImage(),binding.userIcon,model.data.get(0).getFirstName());
                    if (model.data.size() == 1) {
                        binding.userDescription.setText(model.data.get(0).getFullName());
                    }
                    else {
                         binding.userDescription.setText(setValue("user_description",model.data.get(0).getFullName(),String.valueOf(model.data.size() -1)));
                    }
                }
                else {
                    binding.constrainFollowRequest.setVisibility(View.GONE);
                }
            }
        } );
    }

    private void requestFollowersList() {
        String id = getIntent().getStringExtra( "id" );
        if (TextUtils.isEmpty( id )) {
            return;
        }

        if (followersListAdaptere.getData().isEmpty()) {
            showProgress();
        }
        DataService.shared( activity ).requestFollowersList( id, new RestCallback<ContainerListModel<ContactListModel>>(this) {
            @Override
            public void result(ContainerListModel<ContactListModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty( error ) || model == null) {
                    Toast.makeText( activity, R.string.service_message_something_wrong, Toast.LENGTH_SHORT ).show();
                    return;
                }
                if (model.data != null) {
                    if (!model.data.isEmpty()) {
                        binding.followersList.setVisibility( View.VISIBLE );
                        binding.emptyPlaceHolderView.setVisibility( View.GONE );
                        if (id.equals(SessionManager.shared.getUser().getId())) {
                             SessionManager.shared.saveFollowersData( model.data );
                        }
                        followersListAdaptere.updateData( model.data );
                    } else {
                        binding.followersList.setVisibility( View.GONE );
                        binding.emptyPlaceHolderView.setVisibility( View.VISIBLE );
                    }
                }
            }
        } );
    }

    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

}