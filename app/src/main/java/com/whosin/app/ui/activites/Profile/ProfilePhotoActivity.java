package com.whosin.app.ui.activites.Profile;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;

import com.google.gson.JsonObject;

import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.databinding.ActivityProfilePhotoBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.ImageUploadModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.auth.SelectPrefrenceActivity;
import com.whosin.app.ui.activites.comman.BaseActivity;

import java.util.ArrayList;

public class ProfilePhotoActivity extends BaseActivity {
    private ActivityProfilePhotoBinding binding;
    private UserDetailModel userDetailModel = new UserDetailModel();
    private String uploadImageUrl = "";

    @Override
    protected void initUi() {

    }

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    protected void setListeners() {
        binding.navbar.getBackBtn().setOnClickListener(v -> onBackPressed());
        binding.ivPicker.setOnClickListener(view -> {
            getImagePicker();
        });

        binding.imageNext.setOnClickListener(view -> {
           if( binding.ivProfile.getImageMatrix() == null){
               Toast.makeText( this, "Please Select Image", Toast.LENGTH_SHORT ).show();

           }else{
               requestImageUpdate(uploadImageUrl);
           }



        });

    }
    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityProfilePhotoBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    ActivityResultLauncher<Intent> startActivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {

            if (result.getData() != null) {
                Uri imageData = result.getData().getData();
                requestImageUpload(imageData);
            }
        }
    });

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void getImagePicker() {
        ArrayList<String> data = new ArrayList<>();
        data.add("Gallery");
        data.add("Camera");
        Graphics.showActionSheet(activity, "Choose Any One", data, (data1, position) -> {
            switch (position) {
                case 0:
                    ImagePicker.with(activity).galleryOnly().cropSquare().createIntent(intent -> {
                        startActivity.launch(intent);
                        return null;
                    });
                    break;
                case 1:
                    ImagePicker.with(activity).cameraOnly().cropSquare().createIntent(intent -> {
                        startActivity.launch(intent);
                        return null;
                    });
                    break;
            }
        });
    }

    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    public void requestImageUpload(Uri uri) {
        showProgress();
        DataService.shared(activity).requestUploadImage(activity, uri, new RestCallback<ContainerModel<ImageUploadModel>>(this) {
            @Override
            public void result(ContainerModel<ImageUploadModel> model, String error) {
                if (!Utils.isNullOrEmpty(error)) {
                    hideProgress();
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (model.getData() != null &&  !model.getData().getUrl().isEmpty()){
                    uploadImageUrl = model.getData().getUrl();
                    Graphics.loadImage(model.getData().getUrl() , binding.ivProfile);
                    hideProgress();
                }else {
                    hideProgress();
                }

            }
        });
    }

    private void requestImageUpdate(String image){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("image", image );

        SessionManager.shared.updateProfile( activity,jsonObject,(success, error) -> {
            if (!Utils.isNullOrEmpty(error)) {
                Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                return;
            }
            if(success){
                startActivity( new Intent( activity, SelectPrefrenceActivity.class ) );

            }
        });
    }




}