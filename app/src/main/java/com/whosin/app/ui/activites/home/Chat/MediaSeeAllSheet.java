package com.whosin.app.ui.activites.home.Chat;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.FragmentMediaSeeAllSheetBinding;
import com.whosin.app.databinding.FragmentOtpVerificationBottomSheetBinding;
import com.whosin.app.databinding.MediaItemBinding;
import com.whosin.app.service.models.ChatMessageModel;

import java.lang.reflect.Type;
import java.util.List;

public class MediaSeeAllSheet extends DialogFragment {
    private FragmentMediaSeeAllSheetBinding binding;
    private final MediaImageAdapter<ChatMessageModel> mediaImageAdapter = new MediaImageAdapter<>();
    public String media;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle( DialogFragment.STYLE_NORMAL, R.style.DialogStyle );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutRes(), container, false);
        initUi(view);
        setupListener();
        return view;
    }

    private void initUi(View view) {
        binding = FragmentMediaSeeAllSheetBinding.bind( view );
        binding.mediaRecycleView.setLayoutManager(new GridLayoutManager( requireContext(),3,RecyclerView.VERTICAL,false ));
        binding.mediaRecycleView.setAdapter( mediaImageAdapter );

        if (!TextUtils.isEmpty(media)){
            Type type = new TypeToken<List<ChatMessageModel>>() {}.getType();
            List<ChatMessageModel> imageMessages = new Gson().fromJson(media, type);
            mediaImageAdapter.updateData(imageMessages);
        }
    }

    private void setupListener(){

    }


//    @Override
//    public void onStart() {
//        super.onStart();
//        if (getView() != null) {
//            getView().post(() -> {
//                View parent = (View) getView().getParent();
//                BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(parent);
//                int peekHeight = parent.getHeight();
//                behavior.setPeekHeight(peekHeight);
//            });
//        }
//    }

    public int getLayoutRes() {
        return R.layout.fragment_media_see_all_sheet;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new BottomSheetDialog(requireActivity(), R.style.BottomSheetDialogThemeNoFloating);
    }




    public class MediaImageAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder( UiUtils.getViewBy( parent, R.layout.media_item ) );
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            ChatMessageModel model = (ChatMessageModel) getItem( position );
            Graphics.loadImage(model.getMsg(),viewHolder.binding.ivMedia);

        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final MediaItemBinding binding;
            public ViewHolder(@NonNull View itemView) {
                super( itemView );
                binding = MediaItemBinding.bind( itemView );
            }
        }
    }



}