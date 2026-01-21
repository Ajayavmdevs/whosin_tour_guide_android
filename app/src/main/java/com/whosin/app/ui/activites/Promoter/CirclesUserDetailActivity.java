package com.whosin.app.ui.activites.Promoter;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tapadoo.alerter.Alerter;
import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.ActivityCircelsUserDetailBinding;
import com.whosin.app.databinding.ItemCircelsDetailUserListBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.CreateBucketListModel;
import com.whosin.app.service.models.PromoterCirclesModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.comman.BaseActivity;
import com.whosin.app.ui.fragment.Promoter.PromoterBootmSheets.PromoterCreateYourCircleBottomSheet;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CirclesUserDetailActivity extends BaseActivity {

    private ActivityCircelsUserDetailBinding binding;

    private MyCirclesDetailAdapter<PromoterCirclesModel> adapter = new MyCirclesDetailAdapter<>();

    private List<PromoterCirclesModel> promoterList = new ArrayList<>();

    private boolean isFromSubAdmin = false;


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    protected void initUi() {


        applyTranslations();

        String promoter = getIntent().getStringExtra("promoterList");
        if (!TextUtils.isEmpty(promoter)) {
            promoterList = new Gson().fromJson(promoter, new TypeToken<List<PromoterCirclesModel>>() {
            }.getType());
        }


        isFromSubAdmin = getIntent().getBooleanExtra("isFromSubAdmin",false);

        binding.circlesRecycler.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.VERTICAL, false));
        binding.circlesRecycler.setAdapter(adapter);
        if (promoterList != null && !promoterList.isEmpty()) {
            adapter.updateData(promoterList);
            binding.emptyPlaceHolderView.setVisibility(View.GONE);

        } else {
            binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
            binding.circlesRecycler.setVisibility(View.GONE);
        }


    }

    @Override
    protected void setListeners() {
        binding.ivClose.setOnClickListener(view -> {
            onBackPressed();
        });

    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityCircelsUserDetailBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
    @Subscribe
    public void onPromoterCirclesModelEvent(PromoterCirclesModel event) {
        // Reload your data here
        adapter.notifyDataSetChanged();
    }

    @Override
    protected Map<View, String> getTranslationMap() {
        Map<View, String> map = new HashMap<>();
        map.put(binding.tvMyCirclesTitle, "my_circles");

        binding.emptyPlaceHolderView.setEmptyPlaceTxtTitle(getValue("your_circles_list_looking_bit_empty"));

        return map;
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void openCircleSheet(PromoterCirclesModel model) {
        PromoterCreateYourCircleBottomSheet bootomSheet = new PromoterCreateYourCircleBottomSheet();
        CreateBucketListModel bucketListModel = new CreateBucketListModel();
        bucketListModel.setName(model.getTitle());
        bucketListModel.setCoverImage(model.getAvatar());
        bucketListModel.setDescription(model.getDescription());
        bootomSheet.isEdit = true;
        bootomSheet.model = bucketListModel;
        bootomSheet.id = model.getId();
        bootomSheet.updateCircleCallBack = data -> {
            if (data != null) {
                updateCircleDeatil(data, false);
            }
        };
        bootomSheet.show(getSupportFragmentManager(), "");
    }

    private void updateCircleDeatil(PromoterCirclesModel data, boolean isFromUserMember){
        if (adapter.getData() != null && !adapter.getData().isEmpty()) {
            adapter.getData().stream()
                    .filter(promoterCirclesModel -> promoterCirclesModel.getId().equals(data.getId()))
                    .findFirst()
                    .ifPresent(promoterCirclesModel -> {
                        promoterCirclesModel.setAvatar(data.getAvatar());
                        promoterCirclesModel.setTitle(data.getTitle());
                        promoterCirclesModel.setDescription(data.getDescription());
                        if (isFromUserMember)promoterCirclesModel.setTotalMembers(data.getTotalMembers());
                        adapter.notifyDataSetChanged();
                    });
        }
    }

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestPromoterCircleDelete(String id, String name) {
        showProgress();
        DataService.shared(activity).requestPromoterDeleteCircle(id, new RestCallback<ContainerModel<PromoterCirclesModel>>(this) {
            @Override
            public void result(ContainerModel<PromoterCirclesModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }

                Alerter.create(activity).setTitle(getValue("oh_snap")).setTitleAppearance(R.style.AlerterTitle).setTextAppearance(R.style.AlerterText).setText(setValue("delete_circle_snake_bar",name)).hideIcon().show();
                if (adapter.getData() != null && !adapter.getData().isEmpty()) {
                    adapter.getData().removeIf(promoterCirclesModel -> promoterCirclesModel.getId().equals(id));
                    adapter.notifyDataSetChanged();
                    EventBus.getDefault().post(new PromoterCirclesModel());
                }
            }
        });
    }


    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    private class MyCirclesDetailAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_circels_detail_user_list));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            PromoterCirclesModel model = (PromoterCirclesModel) getItem(position);

            viewHolder.binding.ivMenu.setVisibility(isFromSubAdmin ? View.GONE : View.VISIBLE);

            viewHolder.binding.tvName.setText(model.getTitle());

            if (model.getDescription() != null && !model.getDescription().isEmpty()) {
                viewHolder.binding.subTitle.setVisibility(View.VISIBLE);
                viewHolder.binding.subTitle.setText(model.getDescription());
            } else {
                viewHolder.binding.subTitle.setVisibility(View.GONE);
            }

            Graphics.loadImageWithFirstLetter(model.getAvatar(), viewHolder.binding.ivCover, model.getTitle());

            viewHolder.binding.tvCount.setText(setValue("user_count",String.valueOf(model.getTotalMembers())));
            viewHolder.binding.tvCount.setVisibility(model.getTotalMembers() == 0 ? View.GONE : View.VISIBLE);

            viewHolder.binding.ivMenu.setOnClickListener(view -> {
                Utils.preventDoubleClick(view);
                ArrayList<String> data = new ArrayList<>();
                data.add(getValue("edit"));
                data.add(getValue("delete_circle"));
                Graphics.showActionSheet(activity, model.getTitle(), data, (data1, position1) -> {
                    switch (position1) {
                        case 0:
                            openCircleSheet(model);
                            break;
                        case 1:
                            Graphics.showAlertDialogWithOkCancel(activity, getString(R.string.app_name), setValue("delete_circle_alert",model.getTitle()),
                                    getValue("yes"), getValue("cancel"), aBoolean -> {
                                        if (aBoolean) {
                                            requestPromoterCircleDelete(model.getId(), model.getTitle());
                                        }
                                    });
                            break;
                    }

                });

            });


            viewHolder.itemView.setOnClickListener(view -> {
                Utils.preventDoubleClick(view);
                if (isFromSubAdmin){return;}
                Intent intent = new Intent(activity, CirclesDetailActivity.class);
                intent.putExtra("promoterModel", new Gson().toJson(model));
                activityLauncher.launch(intent, result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        boolean isReload = result.getData().getBooleanExtra("isReloadCirclesDetail", false);
                        boolean isDeleteCircle = result.getData().getBooleanExtra("isDeleteCircle", false);
                        String upatedModel = result.getData().getStringExtra("upatedModel");
                        if (isReload && !Utils.isNullOrEmpty(upatedModel)) {
                            PromoterCirclesModel updatedCircleModel;
                            updatedCircleModel = new Gson().fromJson(upatedModel, PromoterCirclesModel.class);
                            if (isDeleteCircle) {
                                if (adapter.getData() != null && !adapter.getData().isEmpty()) {
                                    adapter.getData().removeIf(promoterCirclesModel -> promoterCirclesModel.getId().equals(updatedCircleModel.getId()));
                                    adapter.notifyDataSetChanged();
                                }
                            } else {
                                updateCircleDeatil(updatedCircleModel, true);
                            }
                        }
                    }
                });
            });

        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final ItemCircelsDetailUserListBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemCircelsDetailUserListBinding.bind(itemView);

            }

        }


    }



    // --------------------------------------
    // endregion

}