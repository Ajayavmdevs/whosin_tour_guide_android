package com.whosin.app.ui.fragment.Promoter.PromoterBootmSheets;

import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
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
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.whosin.app.R;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Preferences;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.databinding.FragmentPromoterCreateYourCircleBootomSheetBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.CreateBucketListModel;
import com.whosin.app.service.models.ImageUploadModel;
import com.whosin.app.service.models.PromoterCirclesModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.Promoter.PromoterMyProfile;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;


public class PromoterCreateYourCircleBottomSheet extends DialogFragment {

    private FragmentPromoterCreateYourCircleBootomSheetBinding binding;

    private String coverImageUrl = "";

    public boolean isEdit = false;

    public CreateBucketListModel model = new CreateBucketListModel();

    public String id = "";

    public CommanCallback<PromoterCirclesModel> updateCircleCallBack;


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setStyle( DialogFragment.STYLE_NORMAL, R.style.OtpDialogStyle );
        getActivity().getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE );
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
        binding = FragmentPromoterCreateYourCircleBootomSheetBinding.bind(view);

        binding.tvBucketTitle.setText(Utils.getLangValue("create_your_circle"));
        binding.title.setText(Utils.getLangValue("add_cover_image"));

        binding.layoutCircleName.setHint(Utils.getLangValue("name_your_circle"));
        binding.layoutCircleName.setTitle(Utils.getLangValue("circle_name"));

        binding.layoutDescriptionName.setHint(Utils.getLangValue("add_your_description"));
        binding.layoutDescriptionName.setTitle(Utils.getLangValue("description"));

        binding.btnCreate.setTxtTitle(Utils.getLangValue("create"));

        Glide.with(requireActivity()).load(R.drawable.icon_close_btn).into(binding.ivClose);


        if(isEdit){
            binding.btnCreate.setTxtTitle(Utils.getLangValue("update"));
            binding.tvBucketTitle.setText( Utils.getLangValue("edit_your_circle"));
            binding.addUserBtn.setVisibility( View.GONE );
            binding.ivPicker.setVisibility( View.GONE );
            binding.btnUploadImage.setBackground(null);
            setUpEditData();
        }else {
            binding.addUserBtn.setUpData( requireActivity(), getChildFragmentManager());
        }


    }


    public void setListener() {

        binding.ivClose.setOnClickListener(v -> dismiss());

        binding.btnUploadImage.setOnClickListener(v -> {
            Utils.preventDoubleClick(v);
            getImagePicker();

        });

        binding.addUserBtn.setOnClickListener( view -> {
            Utils.preventDoubleClick(view);
        } );

        binding.btnCreate.setOnClickListener(v -> {
            Utils.preventDoubleClick(v);
            if (isEdit) {
                requestPromoterUpdateCircle();
            } else {
                if (Utils.isNullOrEmpty( coverImageUrl )) {
                    Toast.makeText( requireActivity(), Utils.getLangValue("please_upload_cover_image"), Toast.LENGTH_SHORT ).show();
                    return;
                }

                if (Utils.isNullOrEmpty( binding.layoutCircleName.getText() )) {
                    Toast.makeText( requireActivity(), Utils.getLangValue("please_enter_circle_name"), Toast.LENGTH_SHORT ).show();
                    return;
                }

                requestPromoterCreateCircle();
            }

        } );

    }

    public int getLayoutRes() {
        return R.layout.fragment_promoter_create_your_circle_bootom_sheet;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new BottomSheetDialog(requireActivity(), R.style.BottomSheetDialogThemeNoFloating);
    }

    ActivityResultLauncher<Intent> startActivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            if (result.getData() != null) {
                binding.ivPicker.setVisibility(View.GONE);
                Uri imageData = result.getData().getData();
                binding.imgCover.setImageURI(imageData);
                requestImageUpload(imageData);
            }
        }
    });


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------


    private void setUpEditData() {
        if (binding != null && model != null) {
            binding.layoutCircleName.setText( model.getName() );
            binding.layoutDescriptionName.setText( model.getDescription() );
            Graphics.loadImageWithFirstLetter( model.getCoverImage(), binding.imgCover, model.getName() );

        }

    }

    private void getImagePicker() {
        ArrayList<String> data = new ArrayList<>();
        data.add(Utils.getLangValue("gallery"));
        data.add(Utils.getLangValue("camera"));
        Graphics.showActionSheet( requireActivity(), Utils.getLangValue("choose_any_one"), data, (data1, position) -> {
            switch (position) {
                case 0:
                    ImagePicker.with( requireActivity() ).galleryOnly().cropSquare().createIntent( intent -> {
                        startActivity.launch( intent );
                        return null;
                    } );
                    break;
                case 1:
                    ImagePicker.with( requireActivity() ).cameraOnly().cropSquare().createIntent( intent -> {
                        startActivity.launch( intent );
                        return null;
                    } );
                    break;
            }
        });
    }

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestImageUpload(Uri imageUri) {
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
                }

            }
        });
    }

    private void requestPromoterCreateCircle() {
        binding.btnCreate.startProgress();

        JsonObject object = new JsonObject();
        object.addProperty( "title", binding.layoutCircleName.getText() );
        object.addProperty( "avatar", coverImageUrl );

        if (!Utils.isNullOrEmpty(binding.layoutDescriptionName.getText().trim())){
            object.addProperty( "description", binding.layoutDescriptionName.getText() );
        }

        if (!binding.addUserBtn.selctedUserId.isEmpty()){
            JsonArray members = new JsonArray();
            binding.addUserBtn.selctedUserId.forEach(members::add);

            object.add( "members", members );
        }

        DataService.shared( requireActivity() ).requestPromoterCreateCircle( object, new RestCallback<ContainerModel<CreateBucketListModel>>(this) {
            @Override
            public void result(ContainerModel<CreateBucketListModel> model, String error) {
                binding.btnCreate.stopProgress();
                if (!Utils.isNullOrEmpty( error )) {
                    Toast.makeText( requireActivity(), error, Toast.LENGTH_SHORT ).show();
                    return;
                }

                EventBus.getDefault().post(new PromoterCirclesModel());
                dismiss();
            }
        } );
    }

    private void requestPromoterUpdateCircle() {
        binding.btnCreate.startProgress();
        JsonObject object = new JsonObject();
        object.addProperty( "id", id );
        object.addProperty( "title", binding.layoutCircleName.getText() );
        object.addProperty( "avatar", coverImageUrl );
        object.addProperty( "description", binding.layoutDescriptionName.getText() );


        DataService.shared( requireActivity() ).requestPromoterUpdateCircle( object, new RestCallback<ContainerModel<PromoterCirclesModel>>(this) {
            @Override
            public void result(ContainerModel<PromoterCirclesModel> model, String error) {
                binding.btnCreate.stopProgress();
                if (!Utils.isNullOrEmpty( error )) {
                    Toast.makeText( requireActivity(), error, Toast.LENGTH_SHORT ).show();
                    return;
                }

                if (updateCircleCallBack != null){
                    updateCircleCallBack.onReceive(model.getData());
                }

                Preferences.shared.setBoolean( "dataReload", true );
                dismiss();
            }
        } );
    }


    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------


    // endregion
    // --------------------------------------

}