package com.whosin.app.ui.fragment.PromoterCmApply;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.gson.JsonArray;
import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.FragmentPromoterCmImagesBinding;
import com.whosin.app.databinding.LayoutUploadedImageDesignBinding;
import com.whosin.app.service.manager.PromoterCmApplyManager;
import com.whosin.app.service.manager.PromoterProfileManager;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.RatingModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.ui.activites.Promoter.PromoterActivity;
import com.whosin.app.ui.fragment.comman.BaseFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PromoterCmImagesFragment extends BaseFragment {

    private FragmentPromoterCmImagesBinding binding;

    private final ImageListsAdapter<RatingModel> imageListsAdapter = new ImageListsAdapter<>();

    private PromoterCmApplyManager manager = PromoterCmApplyManager.shared;

    private UserDetailModel userDetailModel = manager.userDetailModel;



    // --------------------------------------
    // region LifeCycle
    // --------------------------------------


    @Override
    public void initUi(View view) {

        binding = FragmentPromoterCmImagesBinding.bind(view);

        applyTranslations();

        binding.uploadedImagesRecycleView.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.uploadedImagesRecycleView.setAdapter(imageListsAdapter);

        if (manager.isEditProfile) {
            if (userDetailModel == null) {
                return;
            }
            binding.layoutFname.setText(userDetailModel.getFirstName());
            binding.layoutLastName.setText(userDetailModel.getLastName());

            if (userDetailModel.getImage() != null && !TextUtils.isEmpty(userDetailModel.getImage())) {
                Graphics.loadImage(userDetailModel.getImage(), binding.ivProfile);
                binding.btnAddProfileImage.setVisibility(View.GONE);
                manager.object.addProperty("image",userDetailModel.getImage());
            }


            if (userDetailModel.getImages() != null && !userDetailModel.getImages().isEmpty()) {
                for (String st : userDetailModel.getImages()) {
                    manager.imageListsAdapter.add(new RatingModel(null, st));
                    manager.uploadImageList.add(st);
                }
            }

            if (!manager.imageListsAdapter.isEmpty()){
                imageListsAdapter.updateData(manager.imageListsAdapter);
            }



        }else {

            setValueFormSession();

            if (!Utils.isNullOrEmpty(SessionManager.shared.getUser().getImage())) {
                binding.btnAddProfileImage.setVisibility(View.GONE);
                Graphics.loadImage(SessionManager.shared.getUser().getImage(), binding.ivProfile);
                manager.object.addProperty("image",SessionManager.shared.getUser().getImage());
                manager.isAvatarImage = true;
            }else {
                binding.btnProfileImageEdit.setVisibility(View.GONE);
            }
        }



    }

    @Override
    public void setListeners() {

        binding.btnUploadImage.setOnClickListener(v -> {
            getImagePicker();
        });


        binding.btnAddProfileImage.setOnClickListener(v -> {
            getImagePickerForProfile();
        });

        binding.btnProfileImageEdit.setOnClickListener(v -> {
            getImagePickerForProfile();
        });

        binding.btnUploadImage.setOnClickListener(v -> {
            getImagePicker();
        });
    }

    @Override
    public void populateData(boolean getDataFromServer) {

    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_promoter_cm_images;
    }


    ActivityResultLauncher<Intent> startActivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            if (result.getData() != null) {
                Uri imageData = result.getData().getData();
                manager.imageListsAdapter.add(new RatingModel(imageData, ""));
                imageListsAdapter.updateData(manager.imageListsAdapter);
            }
        }
    });

    ActivityResultLauncher<Intent> startActivityForProfile = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            if (result.getData() != null) {
                Uri imageData = result.getData().getData();
                if (manager.object.has("image")) manager.object.remove("image");
                manager.profileImageUri = imageData;
                manager.isAvatarImage = true;
                binding.ivProfile.setImageURI(imageData);
                binding.btnAddProfileImage.setVisibility(View.GONE);
                binding.btnProfileImageEdit.setVisibility(View.VISIBLE);
            }
        }
    });

    @Override
    protected Map<View, String> getTranslationMap() {
        Map<View, String> map = new HashMap<>();
        map.put(binding.tvUpdateProfileTitle, "update_profile_picture");
        map.put(binding.minPhotoTitle, "update_profile_3picture");

        binding.layoutFname.setTitle(getValue("firstname"));
        binding.layoutFname.setHint(getValue("enter_your_first_name"));

        binding.layoutLastName.setTitle(getValue("lastname"));
        binding.layoutLastName.setHint(getValue("enter_your_last_name"));

        return map;
    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void getImagePicker() {
        ArrayList<String> data = new ArrayList<>();
        data.add(getValue("gallery"));
        data.add(getValue("camera"));
        Graphics.showActionSheet(requireActivity(), getValue("choose_any_one"), data, (data1, position) -> {
            switch (position) {
                case 0:
                    ImagePicker.with(requireActivity()).galleryOnly().crop(1100, 1500).createIntent(intent -> {
                        startActivity.launch(intent);
                        return null;
                    });
                    break;
                case 1:
                    ImagePicker.with(requireActivity()).cameraOnly().crop(1100, 1500).createIntent(intent -> {
                        startActivity.launch(intent);
                        return null;
                    });
                    break;
            }
        });
    }

    private void getImagePickerForProfile() {
        ArrayList<String> data = new ArrayList<>();
        data.add(getValue("gallery"));
        data.add(getValue("camera"));
        Graphics.showActionSheet(requireActivity(), getValue("choose_any_one"), data, (data1, position) -> {
            switch (position) {
                case 0:
                    ImagePicker.with(requireActivity()).galleryOnly().crop(1100, 1500).createIntent(intent -> {
                        startActivityForProfile.launch(intent);
                        return null;
                    });
                    break;
                case 1:
                    ImagePicker.with(requireActivity()).cameraOnly().crop(1100, 1500).createIntent(intent -> {
                        startActivityForProfile.launch(intent);
                        return null;
                    });
                    break;
            }
        });
    }



    private void setValueFormSession() {

        UserDetailModel model = SessionManager.shared.getUser();
        if (model == null) {
            return;
        }

        binding.layoutFname.setText(model.getFirstName());
        binding.layoutLastName.setText(model.getLastName());

    }

    public boolean isDataValid() {

        if (!manager.object.has("image") && !manager.isAvatarImage) {
            Toast.makeText(requireActivity(), getValue("update_profile_picture"), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (manager.imageListsAdapter != null && !manager.imageListsAdapter.isEmpty()) {
            if (manager.imageListsAdapter.size() < 3) {
                Toast.makeText(requireActivity(), getValue("please_minimum_3_pictures_are_required"), Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            Toast.makeText(requireActivity(), getValue("please_minimum_3_pictures_are_required"), Toast.LENGTH_SHORT).show();
            return false;
        }


        if (Utils.isNullOrEmpty(binding.layoutFname.getText())) {
            Toast.makeText(requireActivity(), getValue("enter_first_name"), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (Utils.isNullOrEmpty(binding.layoutLastName.getText())) {
            Toast.makeText(requireActivity(), getValue("enter_last_name"), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (manager.object.has("images")) {
            manager.object.remove("images");
        }


        manager.object.addProperty("first_name", binding.layoutFname.getText());
        manager.object.addProperty("last_name", binding.layoutLastName.getText());

        return true;
    }


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------


    // --------------------------------------
    // region Adapter
    // --------------------------------------

    private class ImageListsAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.layout_uploaded_image_design));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            ViewHolder viewHolder = (ViewHolder) holder;
            RatingModel model = (RatingModel) getItem(position);


            if (model.getUri() != null) {
                viewHolder.binding.imagePicker.setImageURI(model.getUri());
            } else {
                Graphics.loadImageWithFirstLetter(model.getImage(), viewHolder.binding.imagePicker, "W");
            }

            viewHolder.binding.closeBtn.setOnClickListener(v -> {
                int adapterPosition = viewHolder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    if (model.getUri() == null) {
                        manager.uploadImageList.removeIf(p -> p.contains(model.getImage()));
                    }else {
                        if (model.getUri() != null){
//                            manager.imageListsAdapter.removeIf(p -> p.getUri().equals(model.getUri()));
                            manager.imageListsAdapter.remove(position);
                        }
                    }
                    removeItem(position);
                    notifyDataSetChanged();
                }
            });
        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            private final LayoutUploadedImageDesignBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = LayoutUploadedImageDesignBinding.bind(itemView);
            }
        }
    }


    // endregion
    // --------------------------------------

}