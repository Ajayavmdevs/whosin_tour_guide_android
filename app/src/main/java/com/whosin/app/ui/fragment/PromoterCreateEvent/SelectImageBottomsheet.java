package com.whosin.app.ui.fragment.PromoterCreateEvent;

import static android.view.View.VISIBLE;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.GridSpacingItemDecoration;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.FragmentSelectImageBottomsheetBinding;
import com.whosin.app.databinding.ItemGalleryGridBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.rest.RestCallback;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class SelectImageBottomsheet extends DialogFragment {

    private FragmentSelectImageBottomsheetBinding binding;

    private EventGalleryAdapter adapter;

    public String venueId = "";

    public CommanCallback<String> callback;


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutRes(), container, false);
        initUi(view);
        setListener();
        return view;
    }

    private void initUi(View v) {
        binding = FragmentSelectImageBottomsheetBinding.bind(v);

        requestGetVenueMediaUrls(venueId);
    }

    private void setListener() {
        binding.ivClose.setOnClickListener(view -> dismiss());

        binding.chooseBtn.setOnClickListener(v -> {
            Utils.preventDoubleClick(v);
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/* video/*");
            String[] mimeTypes = {"image/*", "video/*"};
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            startActivityLauncher.launch(intent);

        });


    }

    private int getLayoutRes() {
        return R.layout.fragment_select_image_bottomsheet;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = new BottomSheetDialog(requireActivity(), R.style.BottomSheetDialogThemeNoFloating);
        dialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
            FrameLayout bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            ViewGroup.LayoutParams layoutParam = bottomSheet.getLayoutParams();
            layoutParam.height = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParam.width = ViewGroup.LayoutParams.MATCH_PARENT;
            bottomSheet.setLayoutParams(layoutParam);
            if (bottomSheet != null) {
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
        return dialog;
    }


    ActivityResultLauncher<Intent> startActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri selectedUri = result.getData().getData();
                    if (selectedUri != null) {
                        String mimeType = requireActivity().getContentResolver().getType(selectedUri);
                        if (mimeType != null) {
                            if (mimeType.startsWith("image/")) {
                                handleFileUpload(selectedUri, "image");
                            } else if (mimeType.startsWith("video/")) {
                                handleFileUpload(selectedUri, "video");
                            }
                        }
                    }
                }
            }
    );



    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void handleFileUpload(Uri uri, String type) {
        File file = null;
        try {
            file = new File(String.valueOf(Utils.createFileFromUri(uri,type,requireActivity())));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (file.exists()) {
            requestFileSend(file, type);
        } else {
            Toast.makeText(requireActivity(), "Failed to retrieve the file", Toast.LENGTH_SHORT).show();
        }
    }




    // endregion
    // --------------------------------------
    // region Data Service
    // --------------------------------------

    private void requestGetVenueMediaUrls(String venueId) {
        if (TextUtils.isEmpty(venueId)){
            binding.emptyPlaceHolderView.setVisibility(VISIBLE);
            return;
        }
        Graphics.showProgress(requireActivity());
        DataService.shared(requireActivity()).requestGetVenueMediaUrls(venueId, new RestCallback<ContainerModel<List<String>>>(this) {
            @Override
            public void result(ContainerModel<List<String>> model, String error) {
                Graphics.hideProgress(requireActivity());
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(requireActivity(), error, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (model.data != null && !model.data.isEmpty()) {
                    adapter = new EventGalleryAdapter(model.data);
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 3);
                    binding.eventRecycler.setLayoutManager(gridLayoutManager);
                    binding.eventRecycler.setAdapter(adapter);

                    int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.tab_padding);
                    binding.eventRecycler.addItemDecoration(new GridSpacingItemDecoration(requireContext(), 3, spacingInPixels, false));
                    adapter.updateData(model.data);
                }

                binding.eventRecycler.setVisibility(model.data == null || model.data.isEmpty() ? View.GONE : View.VISIBLE);
                binding.emptyPlaceHolderView.setVisibility(model.data == null || model.data.isEmpty() ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void requestFileSend(File file, String type) {
        binding.progressBar.setVisibility(VISIBLE);
        DataService.shared( requireActivity() ).requestChatUploadList( requireActivity(), file, type, new RestCallback<ContainerModel<String>>(this) {
            @Override
            public void result(ContainerModel<String> model, String error) {
                binding.progressBar.setVisibility(View.GONE);
                if (!Utils.isNullOrEmpty( error )) {
                    Toast.makeText( requireActivity(), error, Toast.LENGTH_SHORT ).show();
                    return;
                }

                if (!TextUtils.isEmpty(model.data) && callback != null) {
                    callback.onReceive(model.data);
                    if (!file.delete()) {
                        Log.w("FileDeletion", "Failed to delete the file: " + file.getAbsolutePath());
                    }
                    dismiss();
                }
            }
        } );
    }


    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------
    public class EventGalleryAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        private List<String>  imageList ;

        public EventGalleryAdapter(List<String> imageList) {
            this.imageList = imageList;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_gallery_grid));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            String image = imageList.get(position);
            if (viewHolder.isVideo(image)){
                viewHolder.binding.videoPlayIcon.setVisibility(VISIBLE);
                Glide.with(requireActivity()).asBitmap().load(viewHolder.getVideoThumbnail(image)).into(viewHolder.binding.imgGallery);
            }else {
                viewHolder.binding.videoPlayIcon.setVisibility(View.GONE);
                Graphics.loadImage(image, viewHolder.binding.imgGallery);
            }

            viewHolder.binding.imgGallery.getRootView().setOnClickListener(view -> {
                if (callback != null){
                    callback.onReceive(image);
                    dismiss();
                }

            });



        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final ItemGalleryGridBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemGalleryGridBinding.bind(itemView);
            }

            private boolean isVideo(String url) {
                return url.endsWith(".mp4") || url.endsWith(".avi") || url.endsWith(".mov");
            }


            private Bitmap getVideoThumbnail(String videoUrl) {
                return ThumbnailUtils.createVideoThumbnail(videoUrl, MediaStore.Video.Thumbnails.MINI_KIND);
            }


        }
    }
    // endregion
    // --------------------------------------
}