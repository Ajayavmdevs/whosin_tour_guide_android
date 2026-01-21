package com.whosin.app.ui.activites.Promoter;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.whosin.app.R;
import com.whosin.app.comman.AppConstants;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.HorizontalSpaceItemDecoration;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.ActivityCompleteYoutProfileBinding;
import com.whosin.app.databinding.ItemPreferencesDesignBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.AppSettingManager;
import com.whosin.app.service.models.AppSettingTitelCommonModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.FromAndTillDateModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.comman.BaseActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CompleteYoutProfileActivity extends BaseActivity {

    private ActivityCompleteYoutProfileBinding binding;

    private final ItemListAdapter<AppSettingTitelCommonModel> adapter = new ItemListAdapter<>();

    private List<AppSettingTitelCommonModel> tmpList = new ArrayList<>();

    private List<String> filterList = new ArrayList<>();


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    protected void initUi() {

        applyTranslations();

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.HORIZONTAL);
        binding.itemRecycleView.setLayoutManager(layoutManager);
        int spacing = getResources().getDimensionPixelSize(com.intuit.ssp.R.dimen._10ssp);
        binding.itemRecycleView.addItemDecoration(new HorizontalSpaceItemDecoration(spacing));
        binding.itemRecycleView.setAdapter(adapter);
        binding.itemRecycleView.setHasFixedSize(false);


        if (!AppSettingManager.shared.getAppSettingData().getCuisine().isEmpty()) {
            tmpList.addAll(AppSettingManager.shared.getAppSettingData().getCuisine());
        }
        if (!AppSettingManager.shared.getAppSettingData().getMusic().isEmpty()) {
            tmpList.addAll(AppSettingManager.shared.getAppSettingData().getMusic());
        }
        if (!AppSettingManager.shared.getAppSettingData().getFeature().isEmpty()) {
            tmpList.addAll(AppSettingManager.shared.getAppSettingData().getFeature());
        }

        if (!tmpList.isEmpty()){
            adapter.updateData(tmpList);
        }


    }

    @Override
    protected void setListeners() {

        binding.btnSubmit.setOnClickListener(v -> checkData());

        binding.switchForavailable.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (compoundButton.isPressed()) {
                if (isChecked) {
                    binding.layoutCustomWeekDays.setVisibility(View.GONE);
                } else {
                    binding.layoutCustomWeekDays.setVisibility(View.VISIBLE);
                }
            }
        });

    }


    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityCompleteYoutProfileBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected Map<View, String> getTranslationMap() {
        Map<View, String> map = new HashMap<>();
        map.put(binding.tvCongratulationTitle, "congratulation");
        map.put(binding.tvSubTitle1, "approve_to_join_ring");
        map.put(binding.tvSubTitle2, "follow_step_complete_profile");
        map.put(binding.tvSubTitle3, "update_your_availabilities");
        map.put(binding.tvChooseYourPreTitle, "choose_your_preferences");
        map.put(binding.minFiveTitle, "min_option_tobeSelected");
        map.put(binding.tvNotification, "im_always_available");
        map.put(binding.btnSubmit, "submit");
        return map;
    }



    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------


    private void checkData() {

        if (filterList.size() < 5 ){
            Toast.makeText(activity, getValue("min_option_tobeSelected"), Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObject object = new JsonObject();

        JsonArray jsonArray = new JsonArray();
        filterList.forEach(jsonArray::add);


        JsonArray availabilitiesArray = new JsonArray();

        List<FromAndTillDateModel> dateList = new ArrayList<>();

        if (!binding.switchForavailable.isChecked()){
            if (!binding.layoutCustomWeekDays.dateList.isEmpty()){
                  dateList = binding.layoutCustomWeekDays.dateList.stream()
                        .map(model -> {
                            FromAndTillDateModel model1 = new FromAndTillDateModel();
                            model1.setFromDate(Utils.changeDateFormat(model.getFromDate(), AppConstants.DATEFORMAT_DD_MM_YYYY, AppConstants.DATEFORMAT_SHORT));
                            model1.setTillDate(Utils.changeDateFormat(model.getTillDate(), AppConstants.DATEFORMAT_DD_MM_YYYY, AppConstants.DATEFORMAT_SHORT));
                            return model1;
                        })
                        .collect(Collectors.toList());

                for (FromAndTillDateModel dateModel : dateList) {
                    JsonObject dateObject = new JsonObject();
                    if (dateModel.getFromDate().isEmpty()){
                        Toast.makeText(activity, getValue("select_from_date"), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (dateModel.getTillDate().isEmpty()){
                        Toast.makeText(activity, getValue("select_till_date"), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    dateObject.addProperty("fromDate", dateModel.getFromDate());
                    dateObject.addProperty("tillDate", dateModel.getTillDate());
                    availabilitiesArray.add(dateObject);
                }
            }
        }


        if (!binding.switchForavailable.isChecked()){
            if (dateList.isEmpty()){
                Toast.makeText(activity, getValue("select_available_date"), Toast.LENGTH_SHORT).show();
                return;
            }
        }

        object.add("preferences", jsonArray);

        object.addProperty("isAlwaysAvailable", binding.switchForavailable.isChecked());

        object.add("availabities", availabilitiesArray);

        Log.d("TAG", "checkData: " + object);

        requestPromoterRingUpdate(object);
    }




    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------


    private void requestPromoterRingUpdate(JsonObject object) {
        showProgress();
        DataService.shared(activity).requestPromoterRingUpdate(object, new RestCallback<ContainerModel<UserDetailModel>>(this) {
            @Override
            public void result(ContainerModel<UserDetailModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (model.getData() != null) {
                    Toast.makeText(activity, model.message, Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }


    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------


    private class ItemListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_preferences_design));
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            ViewHolder viewHolder = (ViewHolder) holder;
            AppSettingTitelCommonModel model = (AppSettingTitelCommonModel) getItem(position);
            viewHolder.binding.iconText.setText(model.getTitle());


            boolean idFound1 = filterList.stream().anyMatch(ids -> ids.equals(model.getId()));
            if (idFound1) {
                viewHolder.binding.linearMainView.setBackground(activity.getResources().getDrawable(R.drawable.selected_bg));
            } else {
                viewHolder.binding.linearMainView.setBackground(activity.getResources().getDrawable(R.drawable.preferences_tag_bg));
            }

            viewHolder.itemView.setOnClickListener(view -> {
                boolean idFound = filterList.stream().anyMatch(ids -> ids.equals(model.getId()));
                if (idFound) {
                    viewHolder.binding.linearMainView.setBackground(activity.getResources().getDrawable(R.drawable.preferences_tag_bg));
                    filterList.remove(model.getId());
                } else {
                    viewHolder.binding.linearMainView.setBackground(activity.getResources().getDrawable(R.drawable.selected_bg));
                    filterList.add(model.getId());
                }
            });


        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            private final ItemPreferencesDesignBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemPreferencesDesignBinding.bind(itemView);
            }
        }
    }


    // --------------------------------------
    // endregion


}