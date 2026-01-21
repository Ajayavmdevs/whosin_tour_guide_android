package com.whosin.app.ui.activites.home.Chat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Preferences;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.ActivityChatWallpaperBinding;
import com.whosin.app.databinding.ItemWallpaperDesginBinding;
import com.whosin.app.service.models.RatingModel;
import com.whosin.app.ui.activites.comman.BaseActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import me.jfenn.colorpickerdialog.dialogs.ColorPickerDialog;

public class ChatWallpaperActivity extends BaseActivity {

    private ActivityChatWallpaperBinding binding;
    private String chatId;
    private final WallpaperAdapter<RatingModel> wallpaperAdapter = new WallpaperAdapter<>();

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    ActivityResultLauncher<Intent> startActivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {

            if (result.getData() != null) {
                Uri imageData = result.getData().getData();
                try {
                    assert imageData != null;
                    InputStream inputStream = getContentResolver().openInputStream(imageData);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    Preferences.shared.setString("imageUri", bitmapToBase64(bitmap));
                    Intent intent = new Intent(activity, PreviewChatWallpaperActivity.class);
                    intent.putExtra("chatId", chatId);
                    activityLauncher.launch(intent, activityResult -> {
                        if (activityResult.getResultCode() == RESULT_OK && activityResult.getData() != null) {
                            boolean isClose = activityResult.getData().getBooleanExtra("close", false);
                            if (isClose) {
                                finish();
                            }
                        }
                    });
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }


            }
        }
    });

    @Override
    protected void initUi() {

        Preferences.shared.setString("imageUri", "");

        binding.tvWallpaperTitle.setText(getValue("wallpaper"));
        binding.selectPhoto.setText(getValue("select_photos"));
        binding.selectColor.setText(getValue("select_color"));

        chatId = getIntent().getStringExtra("id");
        setWallpaper();
    }


    @Override
    protected void setListeners() {
        binding.selectColor.setOnClickListener(view -> showColorPickerDialog());
        binding.selectPhoto.setOnClickListener(view -> {
            ImagePicker.with(activity).galleryOnly().createIntent(intent -> {
                startActivity.launch(intent);
                return null;
            });
        });

        binding.ivBack.setOnClickListener(view -> onBackPressed());
    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityChatWallpaperBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------


    private void setWallpaper() {
        ArrayList<RatingModel> list = new ArrayList<>();
        list.add(new RatingModel(String.valueOf(R.drawable.img_chat_wallpaper_1)));
        list.add(new RatingModel(String.valueOf(R.drawable.img_chat_wallpaper_2)));
        list.add(new RatingModel(String.valueOf(R.drawable.img_chat_wallpaper_3)));
        list.add(new RatingModel(String.valueOf(R.drawable.img_chat_wallpaper_4)));
        list.add(new RatingModel(String.valueOf(R.drawable.img_chat_wallpaper_5)));
        list.add(new RatingModel(String.valueOf(R.drawable.img_chat_wallpaper_6)));
        list.add(new RatingModel(String.valueOf(R.drawable.img_chat_wallpaper_7)));
        list.add(new RatingModel(String.valueOf(R.drawable.img_chat_wallpaper_8)));
        list.add(new RatingModel(String.valueOf(R.drawable.img_chat_wallpaper_9)));
        list.add(new RatingModel(String.valueOf(R.drawable.img_chat_wallpaper_10)));
        list.add(new RatingModel(String.valueOf(R.drawable.img_chat_wallpaper_11)));
        list.add(new RatingModel(String.valueOf(R.drawable.img_chat_wallpaper_12)));
        list.add(new RatingModel(String.valueOf(R.drawable.img_chat_wallpaper_13)));
        list.add(new RatingModel(String.valueOf(R.drawable.img_chat_wallpaper_15)));
        list.add(new RatingModel(String.valueOf(R.drawable.img_chat_wallpaper_16)));
        list.add(new RatingModel(String.valueOf(R.drawable.img_chat_wallpaper_17)));
        list.add(new RatingModel(String.valueOf(R.drawable.img_chat_wallpaper_18)));
        list.add(new RatingModel(String.valueOf(R.drawable.img_chat_wallpaper_19)));
        list.add(new RatingModel(String.valueOf(R.drawable.img_chat_wallpaper_20)));
        list.add(new RatingModel(String.valueOf(R.drawable.img_chat_wallpaper_21)));
        list.add(new RatingModel(String.valueOf(R.drawable.img_chat_wallpaper_22)));
        binding.wallpaperRecycleView.setLayoutManager(new GridLayoutManager(this,3,LinearLayoutManager.VERTICAL,false));
        binding.wallpaperRecycleView.setAdapter(wallpaperAdapter);
        wallpaperAdapter.updateData(list);
    }


    private void showColorPickerDialog() {

        new ColorPickerDialog()
                .withColor(Color.RED)
                .withListener((dialog, color) -> {
                    createCanvasForSelectedColor(color);
                })
                .show(getSupportFragmentManager(), "colorPicker");
    }

    private void createCanvasForSelectedColor(int color) {
        int width = 400; // Width of the canvas
        int height = 400; // Height of the canvas
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor((Integer) color);
        Preferences.shared.setString("imageUri", bitmapToBase64(bitmap));
        Intent intent = new Intent(activity, PreviewChatWallpaperActivity.class);
        intent.putExtra("chatId", chatId);
        activityLauncher.launch(intent, result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                boolean isClose = result.getData().getBooleanExtra("close", false);
                if (isClose) {
                    finish();
                }
            }
        });
    }

    private String bitmapToBase64(Bitmap bitmap) {
        Preferences.shared.setString("imageUri", "");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }



    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------



    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------


    public class WallpaperAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder( UiUtils.getViewBy( parent, R.layout.item_wallpaper_desgin ) );
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            RatingModel model = (RatingModel) getItem(position);
            int drawableResourceId = Integer.parseInt(model.getImage());
            viewHolder.binding.wallpaperImage.setImageResource(drawableResourceId);

            viewHolder.binding.wallpaperImage.setOnClickListener(view -> {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), drawableResourceId);
                Preferences.shared.setString("imageUri", bitmapToBase64(bitmap));
                Intent intent = new Intent(activity, PreviewChatWallpaperActivity.class);
                intent.putExtra("chatId", chatId);
                activityLauncher.launch(intent, result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        boolean isClose = result.getData().getBooleanExtra("close", false);
                        if (isClose) {
                            finish();
                        }
                    }
                });
            });
        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            private final ItemWallpaperDesginBinding binding;
            public ViewHolder(@NonNull View itemView) {
                super( itemView );
                binding = ItemWallpaperDesginBinding.bind(itemView);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    // endregion
    // --------------------------------------


}