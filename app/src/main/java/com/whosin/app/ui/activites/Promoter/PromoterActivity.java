package com.whosin.app.ui.activites.Promoter;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.databinding.ActivityPromoterBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.AppSettingManager;
import com.whosin.app.service.manager.PromoterCmApplyManager;
import com.whosin.app.service.models.ComplimentaryProfileModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.ImageListUploadModel;
import com.whosin.app.service.models.ImageUploadModel;
import com.whosin.app.service.models.PromoterCirclesModel;
import com.whosin.app.service.models.RatingModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.comman.BaseActivity;
import com.whosin.app.ui.fragment.PromoterCmApply.PromoterCmBasicInfoFragment;
import com.whosin.app.ui.fragment.PromoterCmApply.PromoterCmImagesFragment;
import com.whosin.app.ui.fragment.PromoterCmApply.PromoterCmSoicalInfoFragmnet;
import com.whosin.app.ui.fragment.PromoterCmApply.PromoterCmUserBioFragment;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PromoterActivity extends BaseActivity {

    private ActivityPromoterBinding binding;

    private PromoterCmApplyManager promoterCmApplyManager = PromoterCmApplyManager.shared;

    private ViewPagerAdapter adapter;

    private int trackFragment = 0;


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------


    @Override
    protected void initUi() {

        promoterCmApplyManager.imageListsAdapter = new ArrayList<>();
        promoterCmApplyManager.uploadImageList = new ArrayList<>();

        adapter = new ViewPagerAdapter(this);
        binding.viewPager.setAdapter(adapter);

        binding.viewPager.setUserInputEnabled(false);


        promoterCmApplyManager.isPromoter = getIntent().getBooleanExtra("isPromoter", false);
        promoterCmApplyManager.isEditProfile = getIntent().getBooleanExtra("isEditProfile", false);
        promoterCmApplyManager.isFromNotification = getIntent().getBooleanExtra("isFromNotification", false);
        promoterCmApplyManager.notificationTypeId = getIntent().getStringExtra("notificationTypeId");

        String user = getIntent().getStringExtra("userProfileModel");
        promoterCmApplyManager.userDetailModel = new Gson().fromJson(user, UserDetailModel.class);


        binding.tvNextStep.setText(getValue("next"));
        binding.tvMainTitle.setText(setValue("about_you",String.valueOf((1))));

         if (promoterCmApplyManager.isEditProfile) binding.tvMainTitle.setText(getValue("edit_your_profile"));



    }

    @Override
    protected void setListeners() {

        binding.ivBack.setOnClickListener(view -> {
            if (binding.viewPager.getCurrentItem() > 0) {
                trackFragment--;
                buttonHideAndShow();
                binding.viewPager.setCurrentItem(binding.viewPager.getCurrentItem() - 1);
//                if (!promoterCmApplyManager.isEditProfile) binding.tvMainTitle.setText("About you " + (trackFragment + 1) + "/4");
                if (!promoterCmApplyManager.isEditProfile) binding.tvMainTitle.setText(setValue("about_you",String.valueOf((trackFragment+1))));
            } else {
//                if (!promoterCmApplyManager.isEditProfile) binding.tvMainTitle.setText("About you 1/4");
                if (!promoterCmApplyManager.isEditProfile) binding.tvMainTitle.setText(setValue("about_you",String.valueOf((1))));
                if (!promoterCmApplyManager.isEditProfile) {
                    Graphics.showAlertDialogWithOkCancel(activity, getValue("unsaved_changes"), getValue("loss_information"),
                            getValue("yes"), getValue("no"), aBoolean -> {
                                if (aBoolean) {
                                    promoterCmApplyManager.clearManager();
                                    finish();
                                }
                            });
                } else {
                    promoterCmApplyManager.clearManager();
                    finish();
                }
            }
        });

        binding.nextButton.setOnClickListener(view -> {
            if (validateCurrentFragment()) {
                navigateToNextFragment();
            }
        });
    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityPromoterBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onBackPressed() {
        if (!promoterCmApplyManager.isEditProfile) {
            Graphics.showAlertDialogWithOkCancel(activity, getValue("unsaved_changes"), getValue("loss_information"),
                    getValue("yes"), getValue("no"), aBoolean -> {
                        if (aBoolean) {
                            promoterCmApplyManager.clearManager();
                            super.onBackPressed();
                        }
                    });
        } else {
            promoterCmApplyManager.clearManager();
            super.onBackPressed();
        }
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void navigateToNextFragment() {
        if (binding.viewPager.getCurrentItem() < adapter.getItemCount() - 1) {
            trackFragment++;
            buttonHideAndShow();
            binding.viewPager.setCurrentItem(binding.viewPager.getCurrentItem() + 1);
        } else {
            uploadImage();
        }
        if (!promoterCmApplyManager.isEditProfile) binding.tvMainTitle.setText(setValue("about_you",String.valueOf((trackFragment + 1))));
    }


    @SuppressLint("DefaultLocale")
    private void buttonHideAndShow() {

        binding.tvNextStep.setText(getValue("next"));
        if (trackFragment == 3) {
            String tmpText = promoterCmApplyManager.isEditProfile ? getValue("update") : getValue("apply_now");
            binding.tvNextStep.setText(tmpText);
        }

    }

    private boolean validateCurrentFragment() {
        int currentItem = binding.viewPager.getCurrentItem();

        String fragmentTag = "f" + currentItem;
        Fragment currentFragment = getSupportFragmentManager().findFragmentByTag(fragmentTag);
        if (currentFragment instanceof PromoterCmImagesFragment) {
            return ((PromoterCmImagesFragment) currentFragment).isDataValid();
        } else if (currentFragment instanceof PromoterCmBasicInfoFragment) {
            return ((PromoterCmBasicInfoFragment) currentFragment).isDataValid();
        } else if (currentFragment instanceof PromoterCmSoicalInfoFragmnet) {
            return ((PromoterCmSoicalInfoFragmnet) currentFragment).isDataValid();
        } else if (currentFragment instanceof PromoterCmUserBioFragment) {
            return ((PromoterCmUserBioFragment) currentFragment).isDataValid();
        }
        return true;
    }

    private void callApiForUpdateAndCreate() {

        if (promoterCmApplyManager.object.has("images"))
            promoterCmApplyManager.object.remove("images");

        JsonArray jsonArray = new JsonArray();
        promoterCmApplyManager.uploadImageList.forEach(jsonArray::add);
        promoterCmApplyManager.object.add("images", jsonArray);
        Log.d("callApiForUpdateAndCreate", "callApiForUpdateAndCreate: " + jsonArray);


        if (promoterCmApplyManager.isPromoter) {
            if (promoterCmApplyManager.isEditProfile) {
                requestCmPromoterUpdate(promoterCmApplyManager.object);
            } else {
                requestPromoterRequestCreate(promoterCmApplyManager.object);
            }
        } else {
            if (promoterCmApplyManager.isEditProfile) {
                promoterCmApplyManager.object.add("preferences", new JsonArray());
                promoterCmApplyManager.object.addProperty("isAlwaysAvailable", false);
                promoterCmApplyManager.object.add("availabities", new JsonArray());
                requestPromoterRingUpdate(promoterCmApplyManager.object);
            } else {
                if (promoterCmApplyManager.isFromNotification && !Utils.isNullOrEmpty(promoterCmApplyManager.notificationTypeId)) {
                    promoterCmApplyManager.object.addProperty("referredBy", promoterCmApplyManager.notificationTypeId);
                }
                requestPromoterRingRequestCreate(promoterCmApplyManager.object);
            }
        }

    }


    private void uploadImage() {

        List<Uri> imageList = promoterCmApplyManager.imageListsAdapter.stream().map(RatingModel::getUri).filter(Objects::nonNull).collect(Collectors.toList());

        if (!promoterCmApplyManager.object.has("image")) {
            requestUploadImage(promoterCmApplyManager.profileImageUri, true);
        } else {
            if (imageList.isEmpty()) {
                callApiForUpdateAndCreate();
            } else if (imageList.size() == 1) {
                requestUploadImage(imageList.get(0), false);
            } else {
                requestUploadListImages();
            }
        }


    }

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestUploadImage(Uri uri, boolean isProfileImageUpload) {
        showProgress();
        DataService.shared(activity).requestUploadImage(activity, uri, new RestCallback<ContainerModel<ImageUploadModel>>(this) {
            @Override
            public void result(ContainerModel<ImageUploadModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error)) {
                    hideProgress();
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (model.getData() != null && model.getData().getUrl() != null && !model.getData().getUrl().isEmpty()) {
                    if (isProfileImageUpload) {
                        promoterCmApplyManager.isAvatarImage = false;
                        promoterCmApplyManager.object.addProperty("image", model.getData().getUrl());
                        uploadImage();
                    } else {
                        promoterCmApplyManager.uploadImageList.add(model.getData().getUrl());
                        callApiForUpdateAndCreate();
                    }

                }

            }
        });
    }

    private void requestUploadListImages() {

        List<Uri> imageList = promoterCmApplyManager.imageListsAdapter.stream().map(RatingModel::getUri).filter(Objects::nonNull).collect(Collectors.toList());
        Log.d("imageList", "requestUploadListImages: " + imageList.size());
        Log.d("imageList", "requestUploadListImages: " + imageList.size());

        showProgress();
        DataService.shared(activity).requestUploadListImages(activity, imageList, new RestCallback<ContainerModel<ImageListUploadModel>>(this) {
            @Override
            public void result(ContainerModel<ImageListUploadModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error)) {
                    hideProgress();
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (model.getData() != null && model.getData().getUrl() != null && !model.getData().getUrl().isEmpty()) {
                    promoterCmApplyManager.uploadImageList.addAll(model.getData().getUrl());
                    Log.d("requestUploadImage", "callApiForUpdateAndCreate: " + model.getData().getUrl());
                    callApiForUpdateAndCreate();
                }

            }
        });
    }

    private void requestPromoterRequestCreate(JsonObject object) {
        showProgress();
        DataService.shared(activity).requestPromoterRequestCreate(object, new RestCallback<ContainerModel<UserDetailModel>>(this) {
            @Override
            public void result(ContainerModel<UserDetailModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (model.getData() != null) {
                    AppSettingManager.shared.reloadHomeFragment.onReceive(true);
                    EventBus.getDefault().post(new ComplimentaryProfileModel());
                    Toast.makeText(activity, model.message, Toast.LENGTH_SHORT).show();
                    promoterCmApplyManager.clearManager();
                    finish();
                }
            }
        });
    }

    private void requestPromoterRingRequestCreate(JsonObject object) {
        showProgress();
        Log.d("requestPromoterRingRequestCreate", "requestPromoterRingRequestCreate: " + object);
        DataService.shared(activity).requestPromoterRingRequestCreate(object, new RestCallback<ContainerModel<UserDetailModel>>(null) {
            @Override
            public void result(ContainerModel<UserDetailModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (model.getData() != null) {
                    AppSettingManager.shared.reloadHomeFragment.onReceive(true);
                    Toast.makeText(activity, model.message, Toast.LENGTH_SHORT).show();
                    Toast.makeText(activity, getValue("thank_you_for_showing_interest"), Toast.LENGTH_SHORT).show();
                    promoterCmApplyManager.clearManager();
                    finish();
                }
            }
        });
    }

    private void requestCmPromoterUpdate(JsonObject object) {
        DataService.shared(activity).requestCmPromoterUpdate(object, new RestCallback<ContainerModel<UserDetailModel>>(this) {
            @Override
            public void result(ContainerModel<UserDetailModel> model, String error) {

                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (model.getData() != null) {
                    Toast.makeText(activity, model.message, Toast.LENGTH_SHORT).show();
                    EventBus.getDefault().post(new PromoterCirclesModel());
                    promoterCmApplyManager.clearManager();
                    finish();
                }

            }
        });
    }

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
                    EventBus.getDefault().post(new ComplimentaryProfileModel());
                    promoterCmApplyManager.clearManager();
                    finish();
                }
            }
        });
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
                    return new PromoterCmImagesFragment();
                case 1:
                    return new PromoterCmBasicInfoFragment();
                case 2:
                    return new PromoterCmSoicalInfoFragmnet();
                case 3:
                    return new PromoterCmUserBioFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getItemCount() {
            return 4;
        }
    }


    // --------------------------------------
    // endregion
}


