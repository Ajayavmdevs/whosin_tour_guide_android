package com.whosin.app.ui.activites.venue.Bucket;

import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.JsonObject;
import com.ncorti.slidetoact.SlideToActView;
import com.whosin.app.R;
import com.whosin.app.comman.AppExecutors;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.HorizontalSpaceItemDecoration;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.CreateBucketListBottomDialogBinding;
import com.whosin.app.databinding.ItemSelectContactBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.models.BucketListModel;
import com.whosin.app.service.models.ContactListModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.CreateBucketListModel;
import com.whosin.app.service.models.ImageUploadModel;
import com.whosin.app.service.models.MessageEvent;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.rest.RestCallback;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class CreateBucketListBottomDialog extends DialogFragment implements SlideToActView.OnSlideCompleteListener {

    private CreateBucketListBottomDialogBinding binding;
    public List<ContactListModel> selectedUsers = new ArrayList<>();
    private String uploadBucketImageUrl = "";
    private Uri imageUri;
    private SelectContactAdapter<ContactListModel> contactAdapter = new SelectContactAdapter<>();
    private ProgressDialog progressDialog;
    public CreateBucketListModel bucketModel;
    public boolean isEdit = false;
    public CommanCallback<Boolean> callback;

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
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable( Color.TRANSPARENT));
        ((BottomSheetDialog) getDialog()).getBehavior().setState(STATE_EXPANDED);
    }

    public void initUi(View view) {
        binding = CreateBucketListBottomDialogBinding.bind(view);

        if (isEdit) {
            editBucket();
        }

        binding.tvBucketTitle.setText(isEdit ? "Update Bucket" : "Create BucketList");
        binding.createBucket.setText(isEdit ? "Swipe to update" : "Swipe to create");

        binding.contactRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        int spacing = getResources().getDimensionPixelSize(com.intuit.ssp.R.dimen._5ssp);
        binding.contactRecycler.addItemDecoration(new HorizontalSpaceItemDecoration(spacing));
        binding.contactRecycler.setAdapter(contactAdapter);
        if (!selectedUsers.isEmpty()) {
            contactAdapter.updateData(selectedUsers);
        }

        Glide.with( requireActivity() ).load( R.drawable.icon_close_btn ).into( binding.ivClose );

    }


    public void setListener() {
        binding.imagePicker.setOnClickListener(view -> {
            Utils.preventDoubleClick( view );
            getImagePicker();
        });

        binding.ivClose.setOnClickListener( view -> dismiss() );

        binding.layoutContact.setOnClickListener(view -> {
            Utils.preventDoubleClick( view );
            ContactShareBottomSheet contactDialog = new ContactShareBottomSheet();
            contactDialog.defaultUsersList = selectedUsers.stream().map(ContactListModel::getId).collect(Collectors.toList());
            contactDialog.setShareListener(data -> {
                selectedUsers = data;
                AppExecutors.get().mainThread().execute(() -> {
                    contactAdapter.updateData(selectedUsers);
                    contactAdapter.notifyDataSetChanged();
                });

            });

            contactDialog.show(getChildFragmentManager(), "1");
        });

        binding.createBucket.setOnSlideCompleteListener(slideToActView -> {
            if (!isEdit) {
                if (imageUri == null) {
                    Toast.makeText(requireContext(), "Please Select Cover Image", Toast.LENGTH_SHORT).show();
                    resetSlider(slideToActView);
                } else if (binding.edName.getText().toString().isEmpty()) {
                    Toast.makeText(requireContext(), "Please Add List Name", Toast.LENGTH_SHORT).show();
                    resetSlider(slideToActView);
                } else {
                    requestImageUpload();
                }
            } else {
                if (imageUri == null) {
                    if (TextUtils.isEmpty(binding.edName.getText().toString())){
                        Toast.makeText(requireContext(), "Please Add List Name", Toast.LENGTH_SHORT).show();
                        resetSlider(slideToActView);
                    }else {
                        requestBucketUpdate();
                    }
                } else {
                    requestImageUpload();
                }
            }

        });
    }

    public int getLayoutRes() {
        return R.layout.create_bucket_list_bottom_dialog;
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
                imageUri = imageData;
                binding.imagePicker.setImageURI(imageData);
            }
        }
    });

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void editBucket() {
        binding.edName.setText(!bucketModel.getName().isEmpty() ? bucketModel.getName() : "");
        binding.ivPicker.setVisibility(!bucketModel.getCoverImage().isEmpty() ? View.GONE : View.VISIBLE);
        if (!bucketModel.getCoverImage().isEmpty()) {
            Graphics.loadImage(bucketModel.getCoverImage(), binding.imagePicker);
        }
    }

    private void getImagePicker() {
        ArrayList<String> data = new ArrayList<>();
        data.add("Gallery");
        data.add("Camera");
        Graphics.showActionSheet(requireActivity(), "Choose Any One", data, (data1, position) -> {
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

    public void onSlideComplete(@NonNull SlideToActView slideToActView) {
        CreateBucketListBottomDialog dialog = new CreateBucketListBottomDialog();
        dialog.show(getChildFragmentManager(), "1");
        resetSlider(slideToActView);
    }

    private void resetSlider(SlideToActView slideToActView) {
        Animation animation = new TranslateAnimation(0, 0, 0, 0);
        animation.setDuration(0);
        slideToActView.startAnimation(animation);
        slideToActView.resetSlider();
    }


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------


    private void requestCreateBucket() {
        String name = binding.edName.getText().toString();
        String userIds = TextUtils.join(",", selectedUsers.stream().map(ContactListModel::getId).collect(Collectors.toList()));
        DataService.shared(requireActivity()).requestCreateBucketList(name, userIds, uploadBucketImageUrl, new RestCallback<ContainerModel<CreateBucketListModel>>(this) {
            @Override
            public void result(ContainerModel<CreateBucketListModel> model, String error) {
                binding.progressBar.setVisibility(View.GONE);
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(requireActivity(), error, Toast.LENGTH_SHORT).show();
                    return;
                }
                EventBus.getDefault().post(new MessageEvent());
                model.getData();
                if (callback != null) {
                    callback.onReceive(true);
                }
                Toast.makeText(requireActivity(), model.message, Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
    }

    private void requestBucketUpdate() {
        binding.progressBar.setVisibility(View.VISIBLE);
        String userIds = TextUtils.join(",", selectedUsers.stream().map(ContactListModel::getId).collect(Collectors.toList()));
        JsonObject object = new JsonObject();
        object.addProperty("id", bucketModel.getId());
        object.addProperty("image", bucketModel.getCoverImage());
        object.addProperty("name", binding.edName.getText().toString());
        object.addProperty("userIds", userIds);
        DataService.shared(requireActivity()).requestBucketUpdate(object, new RestCallback<ContainerModel<BucketListModel>>(this) {
            @Override
            public void result(ContainerModel<BucketListModel> model, String error) {
                binding.progressBar.setVisibility(View.GONE);
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(requireActivity(), error, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (callback != null) {
                    callback.onReceive(true);
                }
                Toast.makeText( requireActivity(), model.message, Toast.LENGTH_SHORT ).show();
                dismiss();

            }
        });
    }


    public void requestImageUpload() {
        binding.progressBar.setVisibility(View.VISIBLE);
        DataService.shared(requireActivity()).requestUploadImage(requireActivity(), imageUri, new RestCallback<ContainerModel<ImageUploadModel>>(this) {
            @Override
            public void result(ContainerModel<ImageUploadModel> model, String error) {
                binding.progressBar.setVisibility(View.GONE);
                if (!Utils.isNullOrEmpty(error)) {
                    Toast.makeText(requireActivity(), error, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (model.getData() != null) {
                    uploadBucketImageUrl = model.getData().getUrl();
                    if (isEdit) {
                        bucketModel.setCoverImage(uploadBucketImageUrl);
                        requestBucketUpdate();
                    } else {
                        requestCreateBucket();
                    }
                }

            }
        });
    }


    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    public static class SelectContactAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_select_contact));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            ContactListModel model = (ContactListModel) getItem(position);
            String formattedName = model.getFullName().replace(" ", "\n");
            viewHolder.binding.txtName.setText(formattedName);
            Graphics.loadImageWithFirstLetter(model.getImage(), viewHolder.binding.ivContact, model.getFirstName());
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            private final ItemSelectContactBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemSelectContactBinding.bind(itemView);
            }
        }
    }


    // endregion
    // --------------------------------------


}