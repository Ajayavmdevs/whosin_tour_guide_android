package com.whosin.app.ui.fragment.Promoter.PromoterBootmSheets;

import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.adevinta.leku.LocationPickerActivity;
import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.JsonObject;
import com.whosin.app.R;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.databinding.FragmentPromoterCustomEventBottomSheetBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.ImageUploadModel;
import com.whosin.app.service.rest.RestCallback;

import java.util.ArrayList;

public class PromoterCustomEventBottomSheet extends DialogFragment {

    private FragmentPromoterCustomEventBottomSheetBinding binding;

    private String coverImageUrl;

    public CommanCallback<JsonObject> callback;

    public boolean isCustomEventEdit = false;

    public JsonObject object;

    private static final int LOCATION_PICKER_REQUEST_CODE = 1;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.OtpDialogStyle);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutRes(), container, false);
        initUi(view);
        setListener();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes(params);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ((BottomSheetDialog) getDialog()).getBehavior().setState(STATE_EXPANDED);
    }


    public void initUi(View view) {
        binding = FragmentPromoterCustomEventBottomSheetBinding.bind(view);

        binding.tvBucketTitle.setText(Utils.getLangValue("custom_venue"));
        binding.tvLocation.setText(Utils.getLangValue("location"));
        binding.title.setText(Utils.getLangValue("add_cover_image"));
        binding.tvDescription.setText(Utils.getLangValue("venue_description"));
        binding.btnVenueLocation.setHint(Utils.getLangValue("add_your_address"));
        binding.description.setHint(Utils.getLangValue("add_your_description"));
        binding.venueName.setTitle(Utils.getLangValue("venue_name"));
        binding.venueName.setHint(Utils.getLangValue("name_your_venue"));
        binding.btnCreate.setTxtTitle(Utils.getLangValue("create"));

        checkLocationPermissions();
        Glide.with(requireActivity()).load(R.drawable.icon_close_btn).into(binding.ivClose);


        if (isCustomEventEdit && object != null) {
            binding.venueName.setText(object.get("name").getAsString());
            binding.btnVenueLocation.setText(object.get("address").getAsString());
            binding.description.setText(object.get("description").getAsString());
            Graphics.loadImage(object.get("image").getAsString(), binding.imgCover);

            coverImageUrl = object.get("image").getAsString();

            binding.btnCreate.setTxtTitle(Utils.getLangValue("update"));

        }


    }


    public void setListener() {
        binding.ivClose.setOnClickListener(v -> dismiss());

        binding.btnUploadImage.setOnClickListener(v -> {
            Utils.preventDoubleClick(v);
            getImagePicker();

        });

        binding.layoutVenueLocation.setOnClickListener(v -> {
            Utils.preventDoubleClick(v);
            openLocationPicker();
        });


        binding.btnCreate.setOnClickListener(v -> {
            Utils.preventDoubleClick(v);

            if (Utils.isNullOrEmpty(binding.venueName.getText())) {
                Toast.makeText(requireActivity(), Utils.getLangValue("please_enter_circle_name"), Toast.LENGTH_SHORT).show();
                return;
            }

            if (Utils.isNullOrEmpty(binding.btnVenueLocation.getText().toString())) {
                Toast.makeText(requireActivity(), Utils.getLangValue("please_enter_location"), Toast.LENGTH_SHORT).show();
                return;
            }

            if (Utils.isNullOrEmpty(coverImageUrl)) {
                Toast.makeText(requireActivity(), Utils.getLangValue("please_upload_cover_image"), Toast.LENGTH_SHORT).show();
                return;
            }

            binding.btnCreate.startProgress();

            if (object == null) object = new JsonObject();
            object.addProperty("name", binding.venueName.getText());
            object.addProperty("address", binding.btnVenueLocation.getText().toString());
            object.addProperty("description", binding.description.getText().toString().trim());
            object.addProperty("image", coverImageUrl);

            if (callback != null) {
                callback.onReceive(object);
                binding.btnCreate.stopProgress();
                dismiss();
            }


        });
    }

    public int getLayoutRes() {
        return R.layout.fragment_promoter_custom_event_bottom_sheet;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new BottomSheetDialog(requireActivity(), R.style.BottomSheetDialogThemeNoFloating);
    }

    ActivityResultLauncher<Intent> startActivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            if (result.getData() != null) {
                Uri imageData = result.getData().getData();
                requestImageUpload(imageData);
            }
        }
    });

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOCATION_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                double latitude = data.getDoubleExtra("latitude", 0.0);
                double longitude = data.getDoubleExtra("longitude", 0.0);
                String address = data.getStringExtra("location_address");

                if (object == null) object = new JsonObject();
                object.addProperty("latitude", latitude);
                object.addProperty("longitude", longitude);
                binding.btnVenueLocation.setText(address);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                Toast.makeText(requireActivity(), "Location permission required", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void getImagePicker() {
        ArrayList<String> data = new ArrayList<>();
        data.add(Utils.getLangValue("gallery"));
        data.add(Utils.getLangValue("camera"));
        Graphics.showActionSheet(requireActivity(), Utils.getLangValue("choose_any_one"), data, (data1, position) -> {
            switch (position) {
                case 0:
                    ImagePicker.with(requireActivity()).galleryOnly().cropSquare().createIntent(intent -> {
                        startActivity.launch(intent);
                        return null;
                    });
                    break;
                case 1:
                    ImagePicker.with(requireActivity()).cameraOnly().cropSquare().createIntent(intent -> {
                        startActivity.launch(intent);
                        return null;
                    });
                    break;
            }
        });
    }


    private void checkLocationPermissions() {
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void openLocationPicker() {
        double latitude = 25.2048493  , longitude = 55.2707828;
        if (object != null && object.has("latitude") && object.has("longitude")) {
            latitude = object.get("latitude").getAsDouble();
            longitude = object.get("longitude").getAsDouble();
        }
        Intent intent = new LocationPickerActivity.Builder(requireActivity())
                .withLocation(latitude, longitude)
                .withGeolocApiKey(getString(R.string.geoloc_api_key))
                .withGooglePlacesApiKey(getString(R.string.places_api_key))
                .withGoogleTimeZoneEnabled()
                .build();

        startActivityForResult(intent, LOCATION_PICKER_REQUEST_CODE);
    }

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    public void requestImageUpload(Uri imageUri) {
        Graphics.showProgress(requireActivity());
        DataService.shared(requireActivity()).requestUploadImage(requireActivity(), imageUri, new RestCallback<ContainerModel<ImageUploadModel>>(this) {
            @Override
            public void result(ContainerModel<ImageUploadModel> model, String error) {
                Graphics.hideProgress(requireActivity());
                if (!Utils.isNullOrEmpty(error)) {
                    Toast.makeText(requireActivity(), error, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (model.getData() != null) {
                    coverImageUrl = model.getData().getUrl();
                    Graphics.loadImage(coverImageUrl, binding.imgCover);
                    binding.ivPicker.setVisibility(View.GONE);
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


//fragment_promoter_custom_event_bottom_sheet