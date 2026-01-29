package com.whosin.business.ui.activites.setting;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.whosin.business.R;
import com.whosin.business.comman.AppConstants;
import com.whosin.business.comman.DiffAdapter;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Graphics;
import com.whosin.business.comman.Utils;
import com.whosin.business.comman.ui.UiUtils;
import com.whosin.business.databinding.ActivityMyReviewListBinding;
import com.whosin.business.databinding.ItemMyRatingListBinding;
import com.whosin.business.service.DataService;
import com.whosin.business.service.models.ContainerListModel;
import com.whosin.business.service.models.ContainerModel;
import com.whosin.business.service.models.CurrentUserRatingModel;
import com.whosin.business.service.rest.RestCallback;
import com.whosin.business.ui.activites.comman.BaseActivity;
import com.whosin.business.ui.activites.home.activity.WriteReviewActivity;
import com.whosin.business.ui.activites.raynaTicket.RaynaTicketDetailActivity;

import java.util.ArrayList;

public class MyReviewListActivity extends BaseActivity {

    private ActivityMyReviewListBinding binding;

    private final MyRatingListAdapter<CurrentUserRatingModel>  myRatingListAdapter = new MyRatingListAdapter<>();

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    protected void initUi() {

        binding.tvMyReviewsTitle.setText(getValue("my_reviews"));

        binding.myReviewList.setLayoutManager(new LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false));
        binding.myReviewList.setAdapter(myRatingListAdapter);
        requestMyReviewList(true);

    }

    @Override
    protected void setListeners() {

        binding.ivClose.setOnClickListener(v -> finish());

    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityMyReviewListBinding.inflate( getLayoutInflater() );
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

    private void requestMyReviewList(boolean isShowProgress) {
        if (isShowProgress) showProgress();
        DataService.shared(activity).requestMyReviewList( new RestCallback<ContainerListModel<CurrentUserRatingModel>>(this) {
            @Override
            public void result(ContainerListModel<CurrentUserRatingModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (model.data != null && !model.data.isEmpty()) {
                    myRatingListAdapter.updateData(model.data);
                    binding.emptyPlaceHolderView.setVisibility(View.GONE);
                    binding.myReviewList.setVisibility(View.VISIBLE);
                } else {
                    binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
                    binding.myReviewList.setVisibility(View.GONE);
                }

            }
        });
    }

    private void requestMyReviewDelete(String id) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id",id);
        showProgress();
        DataService.shared(activity).requestMyReviewDelete(jsonObject, new RestCallback<ContainerModel<CurrentUserRatingModel>>(this) {
            @Override
            public void result(ContainerModel<CurrentUserRatingModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }
                requestMyReviewList(false);
            }
        });
    }

    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    private class MyRatingListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = UiUtils.getViewBy(parent, R.layout.item_my_rating_list);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            ViewHolder viewHolder = (ViewHolder) holder;
            CurrentUserRatingModel model = (CurrentUserRatingModel) getItem(position);
            viewHolder.mBinding.txtReview.setText(model.getReview());
            viewHolder.mBinding.rating.setRating(model.getStars());
            viewHolder.mBinding.rating.setIsIndicator(true);

            viewHolder.mBinding.txtTitle.setText(model.getTitle());

            viewHolder.mBinding.reviewType.setText(model.getType());

            Graphics.loadImageWithFirstLetter(model.getImage(), viewHolder.mBinding.ivRating, model.getTitle());

            viewHolder.mBinding.txtDate.setText(Utils.changeDateFormat(model.getCreatedAt(), AppConstants.DATEFORMAT_LONG_TIME,"d MMM yyyy"));


            viewHolder.mBinding.iconMenu.setOnClickListener(v -> {
                ArrayList<String> data = new ArrayList<>();
                data.add(getValue("edit_review"));
                data.add(getValue("delete_review"));
                Graphics.showActionSheet(activity, model.getTitle(), getValue("cancel"), data, (data1, position1) -> {
                    switch (position1) {
                        case 0:
                            WriteReviewActivity bottomSheet = new WriteReviewActivity(model.getItemId(), model, model.getType());
                            bottomSheet.activity = activity;
                            bottomSheet.callback = data2 -> requestMyReviewList(false);
                            bottomSheet.show(getSupportFragmentManager(), "1");
                            break;
                        case 1:
                            Graphics.showAlertDialogWithOkCancel(activity, activity.getString(R.string.app_name), getValue("delete_review_confirmation"), getValue("yes"), getValue("cancel"), isConfirmed -> {
                                if (isConfirmed) {
                                    requestMyReviewDelete(model.getId());
                                }
                            });
                            break;
                    }
                });
            });


            viewHolder.mBinding.getRoot().setOnClickListener(v -> {
                if (model.getType().equals("ticket")) {
                    startActivity(new Intent(activity, RaynaTicketDetailActivity.class).putExtra("ticketId", model.getItemId()));
                }
            });

        }


        public class ViewHolder extends RecyclerView.ViewHolder {

            private final ItemMyRatingListBinding mBinding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemMyRatingListBinding.bind(itemView);
            }
        }
    }

    // endregion
    // --------------------------------------
}