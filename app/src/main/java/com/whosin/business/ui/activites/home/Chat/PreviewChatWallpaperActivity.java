package com.whosin.business.ui.activites.home.Chat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;

import com.whosin.business.comman.Graphics;
import com.whosin.business.comman.Preferences;
import com.whosin.business.databinding.ActivityPreviewChatWallpaperBinding;
import com.whosin.business.service.models.ChatWallpaperModel;
import com.whosin.business.ui.activites.comman.BaseActivity;

public class PreviewChatWallpaperActivity extends BaseActivity {

    private ActivityPreviewChatWallpaperBinding binding;
    private String imagePath;


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    protected void initUi() {

        binding.setWallpaper.setText(getValue("set_wallpaper"));
        binding.tvPreviewTitle.setText(getValue("preview"));

        Graphics.applyBlurEffect(activity, binding.blurView);
//        imagePath = getIntent().getStringExtra("imageUri");
        imagePath = Preferences.shared.getString("imageUri");
        if(!TextUtils.isEmpty(imagePath)) {
//            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
//            binding.previewImage.setImageBitmap(bitmap);

            byte[] decodedBytes = Base64.decode(imagePath, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            binding.previewImage.setImageBitmap(bitmap);
        }
    }

    @Override
    protected void setListeners() {
        binding.ivBack.setOnClickListener(view -> onBackPressed());

        binding.setWallpaper.setOnClickListener(view -> {
            String chatId =  getIntent().getStringExtra("chatId");
            ChatWallpaperModel.addRecord(chatId,imagePath);
            Intent intent = new Intent();
            intent.putExtra("close",true);
            setResult(RESULT_OK, intent);
            finish();
        });

    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityPreviewChatWallpaperBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

}