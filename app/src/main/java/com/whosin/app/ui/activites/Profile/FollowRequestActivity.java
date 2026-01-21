package com.whosin.app.ui.activites.Profile;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.ActivityFollowRequestBinding;
import com.whosin.app.databinding.FollowRequestItemBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.models.CommonModel;
import com.whosin.app.service.models.ContainerListModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.comman.BaseActivity;

public class FollowRequestActivity extends BaseActivity {

    private ActivityFollowRequestBinding binding;
    private final FollowRequestAdapter<UserDetailModel> followRequestAdapter = new FollowRequestAdapter<>();

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    protected void initUi() {

        binding.tvFollowRequestTitle.setText(getValue("follow_requests"));

        binding.FollowRequestRecycler.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        binding.FollowRequestRecycler.setAdapter(followRequestAdapter);
        requestUserFollowList(true);
    }

    @Override
    protected void setListeners() {
        binding.headerLayout.setOnClickListener(view -> onBackPressed());
    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityFollowRequestBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestUserFollowList(boolean isLoading) {
        if (isLoading) {
            showProgress();
        }
        DataService.shared( activity ).requestUserFollowList(new RestCallback<ContainerListModel<UserDetailModel>>(this) {
            @Override
            public void result(ContainerListModel<UserDetailModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty( error ) || model == null) {
                    Toast.makeText( activity, error, Toast.LENGTH_SHORT ).show();
                    return;
                }
                if (model.data != null) {
                    followRequestAdapter.updateData(model.data);
                }
                else {

                }
            }
        } );
    }

    private void reqFollowRequestAction(String status,UserDetailModel userDetailModel) {
        showProgress();
        DataService.shared(activity).requestUserFollowAction(userDetailModel.getId(),status, new RestCallback<ContainerModel<CommonModel>>(this) {
            @Override
            public void result(ContainerModel<CommonModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText( activity, error, Toast.LENGTH_SHORT ).show();
                    return;
                }
                Toast.makeText(activity, model.message, Toast.LENGTH_SHORT).show();
                requestUserFollowList(false);
                followRequestAdapter.notifyDataSetChanged();
            }
        });
    }

    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    private class FollowRequestAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.follow_request_item));
        }

        @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            UserDetailModel model = (UserDetailModel) getItem(position);

            viewHolder.mBinding.tvApprove.setText(getValue("confirm"));
            viewHolder.mBinding.tvReject.setText(getValue("delete"));

            Graphics.loadImage(model.getImage(), viewHolder.mBinding.userIcon);
            viewHolder.mBinding.notificationTitle.setText(model.getFullName());
            viewHolder.mBinding.tvApprove.setOnClickListener(v -> {
                reqFollowRequestAction("approved",model);
            });

            viewHolder.mBinding.tvReject.setOnClickListener(v -> {
                reqFollowRequestAction("rejected",model);
            });

            viewHolder.itemView.setOnClickListener(v -> {
                startActivity(new Intent(activity, OtherUserProfileActivity.class).putExtra("friendId", model.getId()));
            });
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private FollowRequestItemBinding mBinding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = FollowRequestItemBinding.bind(itemView);
            }
        }

    }

    // endregion
    // --------------------------------------
}