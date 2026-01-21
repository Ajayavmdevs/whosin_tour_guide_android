package com.whosin.app.ui.activites.Promoter;

import android.content.Intent;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.tapadoo.alerter.Alerter;
import com.whosin.app.R;
import com.whosin.app.comman.AppConstants;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.databinding.ActivityCirclesDetailBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.CreateBucketListModel;
import com.whosin.app.service.models.InvitedUserModel;
import com.whosin.app.service.models.PromoterCirclesModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.comman.BaseActivity;
import com.whosin.app.ui.adapter.MyCirclesDetailAdapter;
import com.whosin.app.ui.fragment.Promoter.PromoterBootmSheets.AddUserBottomSheet;
import com.whosin.app.ui.fragment.Promoter.PromoterBootmSheets.PromoterCreateYourCircleBottomSheet;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CirclesDetailActivity extends BaseActivity {

    private ActivityCirclesDetailBinding binding;

    private MyCirclesDetailAdapter adapter;

    private PromoterCirclesModel promoterCirclesModel;

    private List<String> selectedUserId = new ArrayList<>();

    private List<String> alreadySelectedMemberId = new ArrayList<>();

    private List<UserDetailModel> tmpList = new ArrayList<>();

    private boolean isReloadProfileApi = true;

    private Handler handler = new Handler();

    private String searchQuery = "";
    private Runnable runnable = () -> updateSerachEventList();


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------
    @Override
    protected void initUi() {

        binding.editTvSearch.setHint(getValue("find_users"));
        binding.emptyPlaceHolderView.setEmptyPlaceTxtTitle(getValue("your_member_list_looking_bit_empty"));
        binding.tvAddUserTitle.setText(getValue("add_user"));

        String promoterModel = getIntent().getStringExtra("promoterModel");
        promoterCirclesModel = new Gson().fromJson(promoterModel, PromoterCirclesModel.class);
        adapter = new MyCirclesDetailAdapter(activity,getSupportFragmentManager(), promoterCirclesModel.getId() != null ? promoterCirclesModel.getId() : "", AppConstants.ContextType.CIRCLE_DETAIL, (CommanCallback<Boolean>) data -> {
            if (data) {
                EventBus.getDefault().post(new PromoterCirclesModel());
                requestPromoterCirclesDetail();
            }
        });
        binding.circleRecycler.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        binding.circleRecycler.setAdapter(adapter);
        setPromoterDetail();
        requestPromoterCirclesDetail();

    }

    @Override
    protected void setListeners() {

        binding.ivClose.setOnClickListener(view -> {
            onBackPressed();
        });

        binding.addUserBtn.setOnClickListener(view -> {
            Utils.preventDoubleClick(view);
            AddUserBottomSheet bottomSheet = new AddUserBottomSheet();
            if (!alreadySelectedMemberId.isEmpty()){
                bottomSheet.alreadySelectedMemberId = alreadySelectedMemberId;
            }
            bottomSheet.setShareListener(data -> {
                if (!data.isEmpty()) {
                    selectedUserId.clear();
                    selectedUserId.addAll(data.stream().map(UserDetailModel::getUserId).collect(Collectors.toList()));
                    requestPromoterCircleAddMember(promoterCirclesModel.getId(), selectedUserId);
                } else {
                    selectedUserId.clear();
                }
            });
            bottomSheet.show(getSupportFragmentManager(), "");
        });

        binding.ivMenu.setOnClickListener(view -> {
            Utils.preventDoubleClick(view);
            ArrayList<String> data = new ArrayList<>();
            data.add(getValue("edit"));
            data.add(getValue("delete_circle"));
            Graphics.showActionSheet(activity, promoterCirclesModel.getTitle(), data, (data1, position1) -> {
                switch (position1) {
                    case 0:
                        openCircleSheet(promoterCirclesModel);
                        break;
                    case 1:
                        Graphics.showAlertDialogWithOkCancel(activity, getString(R.string.app_name), setValue("delete_circle_alert",promoterCirclesModel.getTitle()),
                                getValue("yes"), getValue("cancel"), aBoolean -> {
                                    if (aBoolean) {
                                        requestPromoterCircleDelete(promoterCirclesModel.getId(), promoterCirclesModel.getTitle());
                                    }
                                });
                        break;
                }

            });

        });

        binding.editTvSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                searchQuery = editable.toString();
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, 200);
            }
        });

    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityCirclesDetailBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onBackPressed() {
        if (isReloadProfileApi) {
            Intent intent1 = new Intent();
            intent1.putExtra("upatedModel", new Gson().toJson(promoterCirclesModel));
            intent1.putExtra("isReloadCirclesDetail", true);
            setResult(RESULT_OK, intent1);
            finish();
        }
        super.onBackPressed();
    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void setPromoterDetail() {

        binding.tvName.setText(promoterCirclesModel.getTitle());
        if (promoterCirclesModel.getDescription() != null && !promoterCirclesModel.getDescription().isEmpty()) {
            binding.subTitle.setText(promoterCirclesModel.getDescription());
        } else {
            binding.subTitle.setVisibility(View.GONE);
        }
        Graphics.loadRoundImage(promoterCirclesModel.getAvatar(), binding.image);
    }

    private void openCircleSheet(PromoterCirclesModel model) {
        PromoterCreateYourCircleBottomSheet bottomSheet = new PromoterCreateYourCircleBottomSheet();
        CreateBucketListModel bucketListModel = new CreateBucketListModel();
        bucketListModel.setName(model.getTitle());
        bucketListModel.setCoverImage(model.getAvatar());
        bucketListModel.setDescription(model.getDescription());
        bottomSheet.isEdit = true;
        bottomSheet.model = bucketListModel;
        bottomSheet.id = model.getId();
        bottomSheet.updateCircleCallBack = data -> {
            if (data != null) {
                model.setAvatar(data.getAvatar());
                model.setTitle(data.getTitle());
                model.setDescription(data.getDescription());
                setPromoterDetail();
                isReloadProfileApi = true;
                EventBus.getDefault().post(new PromoterCirclesModel());
            }
        };
        bottomSheet.show(getSupportFragmentManager(), "");
    }

    private void updateSerachEventList() {
        List<UserDetailModel> userList;

        if (TextUtils.isEmpty(searchQuery)) {
            userList = tmpList;
        } else {
            userList = tmpList.stream().filter(model -> model.getFullName().trim().toLowerCase().contains(searchQuery.toLowerCase().trim())).collect(Collectors.toList());
        }
        if (!userList.isEmpty()) {
            adapter.updateData(userList);
            binding.circleRecycler.setVisibility(View.VISIBLE);
            binding.emptyPlaceHolderView.setVisibility(View.GONE);
        } else {
            binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
            binding.circleRecycler.setVisibility(View.GONE);
        }
    }

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestPromoterCirclesDetail() {
        showProgress();
        DataService.shared(activity).requestPromoterCircleDetail(promoterCirclesModel.getId(), new RestCallback<ContainerModel<PromoterCirclesModel>>(this) {
            @Override
            public void result(ContainerModel<PromoterCirclesModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }
                alreadySelectedMemberId.clear();
                if (model.getData() != null && model.getData().getMembers() != null && !model.getData().getMembers().isEmpty()) {
                    promoterCirclesModel.setTotalMembers(model.getData().getMembers().size());
                    tmpList.clear();
                    tmpList.addAll(model.getData().getMembers());
                    adapter.updateData(model.getData().getMembers());
                    alreadySelectedMemberId = model.getData().getMembers().stream().map(UserDetailModel::getId).collect(Collectors.toList());
                } else {
                    alreadySelectedMemberId.clear();
                    binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
                    binding.circleRecycler.setVisibility(View.GONE);
                }

            }
        });

    }

    private void requestPromoterCircleDelete(String id, String name) {
        showProgress();
        DataService.shared(activity).requestPromoterDeleteCircle(id, new RestCallback<ContainerModel<PromoterCirclesModel>>(this) {
            @Override
            public void result(ContainerModel<PromoterCirclesModel> model1, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model1 == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }
                Alerter.create(activity).setTitle("Oh Snap!!").setTitleAppearance(R.style.AlerterTitle).setTextAppearance(R.style.AlerterText).setText("You have deleted " + name).hideIcon().show();
                Intent intent1 = new Intent();
                intent1.putExtra("upatedModel",new Gson().toJson(promoterCirclesModel));
                intent1.putExtra("isReloadCirclesDetail",true);
                intent1.putExtra("isDeleteCircle",true);
                setResult(RESULT_OK, intent1);
                finish();
                EventBus.getDefault().post(new PromoterCirclesModel());
                EventBus.getDefault().post(new UserDetailModel());
            }
        });
    }

    private void requestPromoterCircleAddMember(String id, List<String> selectedUserId) {
        DataService.shared(activity).requestPromoterCircleAddMember(id, selectedUserId, new RestCallback<ContainerModel<PromoterCirclesModel>>() {
            @Override
            public void result(ContainerModel<PromoterCirclesModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (model.getData() != null) {
                    Toast.makeText(CirclesDetailActivity.this, model.message, Toast.LENGTH_SHORT).show();
                    EventBus.getDefault().post(new PromoterCirclesModel());
                    EventBus.getDefault().post(new UserDetailModel());
                    requestPromoterCirclesDetail();
                }
            }
        });
    }


    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------


    // --------------------------------------
    // endregion

}