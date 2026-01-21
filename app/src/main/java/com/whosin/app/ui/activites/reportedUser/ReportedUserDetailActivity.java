package com.whosin.app.ui.activites.reportedUser;

import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.whosin.app.comman.AppConstants;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.databinding.ActivityReportedUserDetailBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.ReportUseListModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.comman.BaseActivity;

import java.util.HashMap;
import java.util.Map;

public class ReportedUserDetailActivity extends BaseActivity {

    private ActivityReportedUserDetailBinding binding;

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    protected void initUi() {

        applyTranslations();

        Utils.hideViews(binding.detailLayout,binding.userLayout);
        String id = getIntent().getStringExtra("id");
        if (!TextUtils.isEmpty(id)) requestUserReportDetail(id);

    }

    @Override
    protected void setListeners() {

        binding.ivClose.setOnClickListener(view -> finish());

    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityReportedUserDetailBinding.inflate( getLayoutInflater() );
        return binding.getRoot();
    }

    @Override
    protected Map<View, String> getTranslationMap() {
        Map<View, String> map = new HashMap<>();
        map.put(binding.mainTitle, "report_detail");
        map.put(binding.tvReportIdTitle, "reportID");
        map.put(binding.tvReasonTitle, "reason");
        map.put(binding.tvMessageTitle, "message");
        map.put(binding.tvReportUserTitle, "report_user");

        return map;
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void setUpData(ReportUseListModel model){

        String date = Utils.changeDateFormat(model.getCreatedAt(), AppConstants.DATEFORMAT_LONG_TIME,AppConstants.DATEFORMT_EEE_d_MMM_yyyy);
        binding.reportDate.setText(date);

        binding.reportTitle.setText(model.getTitle());
        binding.reportId.setText(model.getReporterId());
        binding.reason.setText(model.getReason());
        binding.message.setText(model.getMessage());

        if (model.getReportUserModel() != null){
            Graphics.loadImageWithFirstLetter(model.getReportUserModel().getImage(),binding.reportUserImg,model.getReportUserModel().getFullName());
            binding.reportUserTitle.setText(model.getReportUserModel().getFullName());

            if (!TextUtils.isEmpty(model.getReportUserModel().getEmail())){
                binding.reportUserEmail.setText(model.getReportUserModel().getEmail());
                binding.reportUserEmail.setVisibility(View.VISIBLE);
            }else {
                binding.reportUserEmail.setVisibility(View.GONE);
            }

        }

        if (model.getType().equals("review") && model.getReviewModel() != null) {
            binding.ratingLayout.setVisibility(View.VISIBLE);

            binding.txtReview.setText(model.getReviewModel().getReview());
            binding.rating.setRating(model.getReviewModel().getStars());

            Graphics.loadImageWithFirstLetter(model.getReviewModel().getImage(),
                    binding.ivRating, model.getReviewModel().getTitle());

            binding.rating.setIsIndicator(true);

            binding.txtTitle.setText(model.getReviewModel().getTitle());

            binding.txtDate.setText(Utils.changeDateFormat(model.getCreatedAt(), AppConstants.DATEFORMAT_LONG_TIME,AppConstants.DATEFORMT_EEE_d_MMM_yyyy));

        } else {
            binding.ratingLayout.setVisibility(View.GONE);
        }

    }

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestUserReportDetail(String id) {
        showProgress();
        JsonObject object = new JsonObject();
        object.addProperty("id",id);
        DataService.shared(activity).requestReportDetail(object, new RestCallback<ContainerModel<ReportUseListModel>>(this) {
            @Override
            public void result(ContainerModel<ReportUseListModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }
                Utils.showViews(binding.detailLayout,binding.userLayout);
                if (model.data != null){
                    setUpData(model.getData());
                }

            }
        });
    }


    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    // endregion
    // --------------------------------------
}