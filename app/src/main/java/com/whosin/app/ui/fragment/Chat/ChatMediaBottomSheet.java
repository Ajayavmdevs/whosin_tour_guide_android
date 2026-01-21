package com.whosin.app.ui.fragment.Chat;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.king.image.imageviewer.ImageViewer;
import com.king.image.imageviewer.loader.GlideImageLoader;
import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.GridSpacingItemDecoration;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.ChatMediaBottomSheetBinding;
import com.whosin.app.databinding.ItemGalleryGridBinding;
import com.whosin.app.service.models.ChatMessageModel;
import com.whosin.app.ui.activites.venue.VenueGalleryActivity;

import java.util.ArrayList;
import java.util.List;

public class ChatMediaBottomSheet extends DialogFragment {

    private ChatMediaBottomSheetBinding binding;

    private MediaAdapter<ChatMessageModel> adapter = new MediaAdapter();
    private List<ChatMessageModel> imageMessages ;


    public ChatMediaBottomSheet(List<ChatMessageModel> imageMessages) {

        this.imageMessages = imageMessages;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate( getLayoutRes(), container, false );
        initUi( view );
        setListener();
        return view;
    }

    private void initUi(View view) {
        binding = ChatMediaBottomSheetBinding.bind( view );
        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireActivity(), 3);
        binding.galleryRecycler.setLayoutManager(gridLayoutManager);
        binding.galleryRecycler.setAdapter(adapter);

        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.tab_padding);
        binding.galleryRecycler.addItemDecoration(new GridSpacingItemDecoration(requireActivity(), 3, spacingInPixels, false));
        adapter.updateData( imageMessages );



    }

    private void setListener() {
        binding.ivClose.setOnClickListener( view -> {dismiss();} );

    }

    private int getLayoutRes() {
        return R.layout.chat_media_bottom_sheet;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new BottomSheetDialog( requireActivity(), R.style.BottomSheetDialogThemeNoFloating );
    }

    public class MediaAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder( UiUtils.getViewBy( parent, R.layout.item_gallery_grid ) );
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            ViewHolder viewHolder = (ViewHolder) holder;
            ChatMessageModel model = (ChatMessageModel)getItem( position );

            viewHolder.binding.imgGallery.getRootView().setOnClickListener( view -> {
                ImageViewer.load(model.getMsg())
                        .selection(position)
                        .imageLoader(new GlideImageLoader())
                        .indicator(true)
                        .start(ChatMediaBottomSheet.this);
            });
//            Graphics.loadImage( model.getMsg(),viewHolder.binding.imgGallery);

            Glide.with( requireActivity() ).load( model.getMsg() ).into( viewHolder.binding.imgGallery );
        }
    }
    public class ViewHolder extends RecyclerView.ViewHolder{

        ItemGalleryGridBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super( itemView );
            binding = ItemGalleryGridBinding.bind( itemView );
        }
    }
}