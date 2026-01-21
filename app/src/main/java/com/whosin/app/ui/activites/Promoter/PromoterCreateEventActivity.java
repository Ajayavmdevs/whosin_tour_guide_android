package com.whosin.app.ui.activites.Promoter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.kofigyan.stateprogressbar.StateProgressBar;
import com.whosin.app.R;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.databinding.ActivityPromoterCreateEventBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.PromoterProfileManager;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.PromoterCirclesModel;
import com.whosin.app.service.models.PromoterEventModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.CmProfile.AddToCircleBottomSheet;
import com.whosin.app.ui.activites.comman.BaseActivity;
import com.whosin.app.ui.fragment.PromoterCreateEvent.PromoterEventInfoFragment;
import com.whosin.app.ui.fragment.PromoterCreateEvent.PromoterEventRequirementFragment;
import com.whosin.app.ui.fragment.PromoterCreateEvent.PromoterInvitesFragment;
import com.whosin.app.ui.fragment.PromoterCreateEvent.PromoterPlusOneFragment;
import com.whosin.app.ui.fragment.PromoterCreateEvent.PromoterSocialFragment;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PromoterCreateEventActivity extends BaseActivity {

    private ActivityPromoterCreateEventBinding binding;

    public CommanCallback<Boolean> callback;

    private ViewPagerAdapter adapter;

    private int trackFragment = 0;

    private String saveToDraftId = "";


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------


    @Override
    protected void initUi() {


        applyTranslations();

        String[] descriptionData = {getValue("event_info"), getValue("requirements"), getValue("social"), getValue("invites"),getValue("plus_one")};
        binding.progressBar.setStateDescriptionData(descriptionData);
        binding.progressBar.setStateDescriptionTypeface("fonts/RobotoSlab-Light.ttf");
        binding.progressBar.setStateNumberTypeface("fonts/Questrial-Regular.ttf");


//        PromoterProfileManager.clearSaveDraftEvent();


        PromoterProfileManager.shared.promoterEventObject = new JsonObject();

        adapter = new ViewPagerAdapter(this);
        binding.viewPager.setAdapter(adapter);

        buttonHideAndShow();

        binding.viewPager.setUserInputEnabled(false);



        callback = data -> {
            if (data) {
                binding.nextButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(PromoterCreateEventActivity.this, R.color.brand_pink)));
            } else {
                binding.nextButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(PromoterCreateEventActivity.this, R.color.lighting_gray)));
            }
        };

        PromoterProfileManager.shared.requestPromoterEventGetCustomCategory(activity);

    }

    @Override
    protected void setListeners() {

        binding.ivClose.setOnClickListener(view -> {
            finish();
            PromoterProfileManager.shared.promoterEventObject = new JsonObject();
            PromoterProfileManager.shared.isEventSaveToDraft = false;
            PromoterProfileManager.shared.isEventRepost = false;
        });

        binding.nextButton.setOnClickListener(view -> {
            if (validateCurrentFragment()) {
                navigateToNextFragment();
            }
        });

        binding.backButton.setOnClickListener(view -> navigateToPreviousFragment());

        binding.btnSaveAndDraft.setOnClickListener(v -> {
            Utils.preventDoubleClick(v);
            if (binding.tvBackBtn.getText().equals(getValue("back"))) {
                navigateToPreviousFragment();
            } else {
                saveDraftEvent();

            }
        });


        binding.btnSaveDraft.setOnClickListener(v -> {
            Utils.preventDoubleClick(v);
            saveDraftEvent();
        });

    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityPromoterCreateEventBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onBackPressed() {
        PromoterProfileManager.shared.promoterEventObject = new JsonObject();
        PromoterProfileManager.shared.isEventSaveToDraft = false;
        PromoterProfileManager.shared.isEventRepost = false;
        PromoterProfileManager.shared.isEventEdit = false;
        super.onBackPressed();
    }

    @Override
    public void finish() {
        super.finish();
        PromoterProfileManager.shared.promoterEventObject = new JsonObject();
        PromoterProfileManager.shared.isEventSaveToDraft = false;
        PromoterProfileManager.shared.isEventRepost = false;
        PromoterProfileManager.shared.isEventEdit = false;
    }


    @Override
    protected Map<View, String> getTranslationMap() {
        Map<View, String> map = new HashMap<>();
        map.put(binding.tvTitle, "create_your_event");
        map.put(binding.tvSubTitle, "fill_your_event_information");
        map.put(binding.tvBackBtn, "back");
        map.put(binding.btnSaveAndDraft, "back");

        return map;
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    @SuppressLint("DefaultLocale")
    private void buttonHideAndShow() {

//        binding.tvNextStep.setText(String.format("Next Step (%d/5)", trackFragment + 1));
        binding.tvNextStep.setText(setValue("create_event_next_button",String.valueOf(trackFragment+1)));
        binding.progressBar.setCurrentStateNumber(trackFragment + 1);
        binding.btnSaveDraft.setVisibility(trackFragment == 0 ? View.GONE : View.VISIBLE);
        binding.ivBackForBackTv.setVisibility(trackFragment == 0 ? View.GONE : View.VISIBLE);
        binding.tvBackBtn.setText(trackFragment == 0 ? getValue("save_draft") : getValue("back"));
        binding.nextButton.setBackgroundTintList(trackFragment == 0 ?
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.lighting_gray)) :
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.brand_pink))
        );
        binding.tvTitle.setText(!PromoterProfileManager.shared.isEventEdit ? getValue("create_your_event") : getValue("update_your_event"));
        if (PromoterProfileManager.shared.isEventRepost){
            binding.tvTitle.setText(getValue("repost_your_event"));
        }
        if (PromoterProfileManager.shared.isEventEdit || PromoterProfileManager.shared.isEventRepost) {
            binding.btnSaveAndDraft.setVisibility(trackFragment == 0 ? View.GONE : View.VISIBLE);
            binding.btnSaveDraft.setVisibility(View.GONE);
            binding.nextButton.setBackgroundTintList(
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.brand_pink))
            );
        }
        if (trackFragment == 4 ) {
            binding.tvNextStep.setText(!PromoterProfileManager.shared.isEventEdit ? getValue("create_event") : getValue("update_event"));
            if (PromoterProfileManager.shared.isEventRepost){
                binding.tvNextStep.setText(getValue("repost_event"));
            }
        }

    }

    private boolean validateCurrentFragment() {
        int currentItem = binding.viewPager.getCurrentItem();

        String fragmentTag = "f" + currentItem;
        Fragment currentFragment = getSupportFragmentManager().findFragmentByTag(fragmentTag);
        if (currentFragment instanceof PromoterEventInfoFragment) {
            return ((PromoterEventInfoFragment) currentFragment).isDataValid();
        } else if (currentFragment instanceof PromoterEventRequirementFragment) {
            return ((PromoterEventRequirementFragment) currentFragment).isDataValid();
        } else if (currentFragment instanceof PromoterSocialFragment) {
            return ((PromoterSocialFragment) currentFragment).isDataValid();
        } else if (currentFragment instanceof PromoterInvitesFragment) {
            return ((PromoterInvitesFragment) currentFragment).isDataValid();
        }else if (currentFragment instanceof PromoterPlusOneFragment) {
            return ((PromoterPlusOneFragment) currentFragment).isDataValid();
        }
        return true;
    }

    private void navigateToNextFragment() {
        if (binding.viewPager.getCurrentItem() < adapter.getItemCount() - 1) {
            trackFragment++;
            buttonHideAndShow();
            binding.viewPager.setCurrentItem(binding.viewPager.getCurrentItem() + 1);
        } else {
            if (PromoterProfileManager.shared.isEventEdit) {
                boolean isPresentRepeatString = PromoterProfileManager.shared.promoterEventObject.has("repeat");
                if (isPresentRepeatString){
                    String repeat =  PromoterProfileManager.shared.promoterEventObject.get("repeat").getAsString();
                    if (repeat.equals("daily")){
                        String updateCurrentEvent = getValue("update_current_event");
                        String updateAllEvent = getValue("update_all_event");
                        String close = getValue("close");

                        Graphics.showAlertDialogForEventUdapte(activity, getValue("Update Event"),
                                getValue("want_to_update_this_event_only"),
                                updateCurrentEvent, close, updateAllEvent, action -> {
                                    if (action.equals(updateCurrentEvent)) {
                                        PromoterProfileManager.shared.promoterEventObject.addProperty("updateType", "current");
                                        requestPromoterEventUpdate();

                                    } else if (action.equals(updateAllEvent)) {
                                        PromoterProfileManager.shared.promoterEventObject.addProperty("updateType", "all");
                                        requestPromoterEventUpdate();

                                    } else if (action.equals(close)) {
                                        // just close
                                    }
                                });

                    }else {
                        if (PromoterProfileManager.shared.promoterEventObject.has("updateType")) PromoterProfileManager.shared.promoterEventObject.remove("updateType");
                       requestPromoterEventUpdate();
                    }
                }else {
                    if (PromoterProfileManager.shared.promoterEventObject.has("updateType")) PromoterProfileManager.shared.promoterEventObject.remove("updateType");
                    requestPromoterEventUpdate();
                }
            } else {
                if (PromoterProfileManager.shared.promoterEventObject.has("updateType")) PromoterProfileManager.shared.promoterEventObject.remove("updateType");
                requestPromoterInvitationCreate();
            }
        }
    }

    private void navigateToPreviousFragment() {
        if (binding.viewPager.getCurrentItem() > 0) {
            trackFragment--;
            buttonHideAndShow();
            binding.viewPager.setCurrentItem(binding.viewPager.getCurrentItem() - 1);
        }
    }

    private String generateUniqueId() {
        long currentTimeMillis = System.currentTimeMillis();
        int randomNumber = (int) (Math.random() * 1000000);
        return currentTimeMillis + "_" + randomNumber;
    }

    private void saveDraftEvent(){
        List<Fragment> fragments = getSupportFragmentManager().getFragments();

        for (Fragment fragment : fragments) {
            if (fragment instanceof PromoterEventInfoFragment) {
                ((PromoterEventInfoFragment) fragment).saveToDraft();
            } else if (fragment instanceof PromoterEventRequirementFragment) {
                ((PromoterEventRequirementFragment) fragment).saveToDraft();
            } else if (fragment instanceof PromoterSocialFragment) {
                ((PromoterSocialFragment) fragment).saveToDraft();
            } else if (fragment instanceof PromoterInvitesFragment) {
                ((PromoterInvitesFragment) fragment).saveToDraft();
            }else if (fragment instanceof PromoterPlusOneFragment) {
                ((PromoterPlusOneFragment) fragment).saveToDraft();
            }
        }

        if (PromoterProfileManager.shared.isEventSaveToDraft){
            String saveToDraftId = PromoterProfileManager.shared.promoterEventModel.getSaveToDraftId();
            if (!TextUtils.isEmpty(saveToDraftId)){
                PromoterProfileManager.shared.promoterEventObject.addProperty("saveToDraftId",saveToDraftId);
            }
        }

        String saveDraftString = PromoterProfileManager.shared.promoterEventObject.toString();
        if (!Utils.isNullOrEmpty(saveDraftString)){
            PromoterEventModel promoterEventModel = new Gson().fromJson(saveDraftString, PromoterEventModel.class);
            if (PromoterProfileManager.shared.isEventSaveToDraft){
                PromoterProfileManager.updateEventIntoDraft(promoterEventModel.getSaveToDraftId(),promoterEventModel);
            }else {
                promoterEventModel.setSaveToDraftId(generateUniqueId());
                PromoterProfileManager.addEventIntoDraft(promoterEventModel);
            }

        }
        EventBus.getDefault().post(new PromoterCirclesModel());
        PromoterProfileManager.shared.isEventSaveToDraft = false;
        finish();
    }


    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------


    private class ViewPagerAdapter extends FragmentStateAdapter {
        public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new PromoterEventInfoFragment();
                case 1:
                    return new PromoterEventRequirementFragment();
                case 2:
                    return new PromoterSocialFragment();
                case 3:
                    return new PromoterInvitesFragment();
                case 4:
                    return new PromoterPlusOneFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getItemCount() {
            return 5;
        }
    }


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestPromoterInvitationCreate() {
        Log.d("TAG", "requestPromoterInvitationCreate: " + PromoterProfileManager.shared.promoterEventObject);
        showProgress();
        if (PromoterProfileManager.shared.promoterEventModel != null &&!TextUtils.isEmpty(PromoterProfileManager.shared.promoterEventModel.getSaveToDraftId())){
            saveToDraftId = PromoterProfileManager.shared.promoterEventModel.getSaveToDraftId();
        }
//        if (PromoterProfileManager.shared.promoterEventObject.has("saveToDraftId")) {
//            saveToDraftId = PromoterProfileManager.shared.promoterEventObject.get("saveToDraftId").getAsString();
//            PromoterProfileManager.shared.promoterEventObject.remove("saveToDraftId");
//        }
        Log.d("TAG", "requestPromoterInvitationCreate: " + PromoterProfileManager.shared.promoterEventObject);
        DataService.shared(activity).requestPromoterInvitationCreate(PromoterProfileManager.shared.promoterEventObject, new RestCallback<ContainerModel<UserDetailModel>>(this) {
            @Override
            public void result(ContainerModel<UserDetailModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(activity, getValue("event_created_successfully"), Toast.LENGTH_SHORT).show();

                if (PromoterProfileManager.shared.isEventSaveToDraft){
                    PromoterProfileManager.removeEventIntoDraft(saveToDraftId);
                    EventBus.getDefault().post(new PromoterCirclesModel());
                }
                PromoterProfileManager.shared.isEventRepost = false;
                EventBus.getDefault().post(model.getData());
                finish();

            }
        });
    }

    private void requestPromoterEventUpdate() {
        showProgress();
        if (PromoterProfileManager.shared.promoterEventObject.has("saveToDraftId")) PromoterProfileManager.shared.promoterEventObject.remove("saveToDraftId");
        Log.d("TAG", "requestPromoterInvitationCreate: " + PromoterProfileManager.shared.promoterEventObject);
        DataService.shared(activity).requestPromoterEventupdate(PromoterProfileManager.shared.promoterEventObject, new RestCallback<ContainerModel<PromoterEventModel>>(this) {
            @Override
            public void result(ContainerModel<PromoterEventModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (model.getData() != null) {
                    PromoterProfileManager.shared.isEventEdit = false;
                    Toast.makeText(activity, getValue("event_updated_successfully"), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.putExtra("isReload", true);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }


    // endregion
    // --------------------------------------

}