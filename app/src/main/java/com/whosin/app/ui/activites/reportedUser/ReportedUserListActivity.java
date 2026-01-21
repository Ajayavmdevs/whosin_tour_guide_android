package com.whosin.app.ui.activites.reportedUser;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.whosin.app.R;
import com.whosin.app.comman.AppConstants;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.ActivityReportedUserListBinding;
import com.whosin.app.databinding.ItemReportedUserListItemBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.BlockUserManager;
import com.whosin.app.service.models.ContainerListModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.ReportUseListModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.comman.BaseActivity;

import java.util.ArrayList;


public class ReportedUserListActivity extends BaseActivity {

    private ActivityReportedUserListBinding binding;

    private final ReportedUserListAdapter<ReportUseListModel> adapter = new  ReportedUserListAdapter<>();

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------


    @Override
    protected void initUi() {


        binding.emptyPlaceHolderView.setEmptyPlaceTxtTitle(getValue("no_reported_users"));
        binding.tvReportedUseListTitle.setText(getValue("reported_users_list"));


        binding.reportUserRecycler.setLayoutManager( new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false ) );
        binding.reportUserRecycler.setAdapter( adapter );

        requestReportUserList();

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
        binding = ActivityReportedUserListBinding.inflate( getLayoutInflater() );
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

    private void requestReportUserList() {
        showProgress();
        DataService.shared(activity).requestReportList(new RestCallback<ContainerListModel<ReportUseListModel>>(this) {
            @Override
            public void result(ContainerListModel<ReportUseListModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (model.data != null && !model.data.isEmpty()) {
                    binding.emptyPlaceHolderView.setVisibility(View.GONE);
                    binding.reportUserRecycler.setVisibility(View.VISIBLE);
                    adapter.updateData(model.data);

                } else {
                    binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
                    binding.reportUserRecycler.setVisibility(View.GONE);
                }
            }
        });
    }

    private void requestReportUserRemove(String id, String userID) {
        showProgress();
        DataService.shared(activity).requestUserReportRemove(id, new RestCallback<ContainerModel<UserDetailModel>>(this) {
            @Override
            public void result(ContainerModel<UserDetailModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (model.status == 1) {
                    requestReportUserList();
                    BlockUserManager.deleteBlockUserId(userID);
                }

            }
        });
    }

    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    private class ReportedUserListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_reported_user_list_item));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            ViewHolder viewHolder = (ViewHolder) holder;

            ReportUseListModel model = (ReportUseListModel) getItem(position);

            Graphics.applyBlurEffect(activity,viewHolder.binding.blurView);

            viewHolder.binding.title.setText(Utils.notNullString(model.getTitle()));

            viewHolder.binding.reason.setText(Utils.notNullString(model.getReason()));

            if (!TextUtils.isEmpty(model.getStatus())){
                viewHolder.binding.statusLayout.setVisibility(View.VISIBLE);
                viewHolder.binding.tvStatus.setText(model.getStatus());

                switch (model.getStatus()) {
                    case "pending":
                        viewHolder.binding.statusLayout.setBackgroundColor(ContextCompat.getColor(activity, R.color.amber_color));
                        break;
                    case "actioned":
                        viewHolder.binding.statusLayout.setBackgroundColor(ContextCompat.getColor(activity, R.color.red));
                        break;
                    case "reviewed":
                        viewHolder.binding.statusLayout.setBackgroundColor(ContextCompat.getColor(activity, R.color.brand_pink));
                        break;
                    case "dismissed":
                        viewHolder.binding.statusLayout.setBackgroundColor(ContextCompat.getColor(activity, R.color.light_blue_400));
                        break;
                }

            }else {
                viewHolder.binding.statusLayout.setVisibility(View.GONE);
            }


            viewHolder.binding.iconMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<String> data = new ArrayList<>();
                    data.add("Delete");
                    Graphics.showActionSheet(activity, getString(R.string.app_name), data, (data1, position1) -> {
                        if (position1 == 0) {
                            Graphics.showAlertDialogWithOkCancel(activity, activity.getString(R.string.app_name), getValue("delete_report_confirmation"), getValue("yes"), getValue("no"), isConfirmed -> {
                                if (isConfirmed) {
                                    requestReportUserRemove(model.get_id(), model.getUserId());

                                }
                            });

                        }
                    });

                }
            });

            viewHolder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(activity,ReportedUserDetailActivity.class).putExtra("id",model.get_id()));
                }
            });


            String date = Utils.changeDateFormat(model.getCreatedAt(), AppConstants.DATEFORMAT_LONG_TIME,"d MMM yyyy");
            viewHolder.binding.reportDate.setText(date);

            if (model.getType().equals("review") && model.getReviewModel() != null){
                Graphics.loadRoundImage(model.getReviewModel().getImage(),viewHolder.binding.imgProfile);
            }


        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final ItemReportedUserListItemBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemReportedUserListItemBinding.bind(itemView);
            }
        }


    }


    // endregion
    // --------------------------------------

}