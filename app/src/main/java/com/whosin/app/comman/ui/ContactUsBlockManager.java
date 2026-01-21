package com.whosin.app.comman.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.FragmentActivity;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.whosin.app.BuildConfig;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Preferences;
import com.whosin.app.comman.Utils;
import com.whosin.app.databinding.DialogChatOptionsBinding;
import com.whosin.app.databinding.ItemContactUsBlockBinding;
import com.whosin.app.service.models.ChatModel;
import com.whosin.app.service.models.ContactUsBlockModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.ui.activites.home.Chat.ChatMessageActivity;
import com.whosin.app.ui.commonBottomSheets.ChatOptionsBottomSheet;

import java.util.List;

public class ContactUsBlockManager {

    public static void setupContactUsBlock(
            Context context,
            ItemContactUsBlockBinding binding,
            ContactUsBlockModel block,
            ContactUsBlockModel.ContactBlockScreens screenType
    ) {

        ContactUsBlockModel model = block;
        if (model == null) {
            hideView(binding.getRoot());
            return;
        }

        binding.getRoot().setVisibility(View.VISIBLE);

        if (model.getMedia() != null) {
            ContactUsBlockModel.MediaModel media = model.getMedia();
            Double h = media.getHeight() != null && media.getHeight() > 0 ? media.getHeight() : model.height(screenType);
            if (h != null && h > 0) {
                int px = (int) Utils.dpToPx(context, h.floatValue());
                if (binding.eventVideoView.getVisibility() == View.VISIBLE) {
                    ViewGroup.LayoutParams params = binding.eventVideoView.getLayoutParams();
                    params.height = px;
                    binding.eventVideoView.setLayoutParams(params);
                } else {
                    ViewGroup.LayoutParams params = binding.ivBackground.getLayoutParams();
                    params.height = px;
                    binding.ivBackground.setLayoutParams(params);
                }
            }
            String type = Utils.notNullString(media.getType());
            if (type.equalsIgnoreCase("image")) {
                binding.eventVideoView.setVisibility(View.GONE);
                binding.ivBackground.setVisibility(View.VISIBLE);
                Graphics.loadImage(media.getUrl(), binding.ivBackground);
            } else if (type.equalsIgnoreCase("video") || type.equalsIgnoreCase("color")) {
                if (type.equalsIgnoreCase("video") && !TextUtils.isEmpty(media.getUrl())) {
                    binding.ivBackground.setVisibility(View.GONE);
                    binding.eventVideoView.setVisibility(View.VISIBLE);
                    ExoPlayer player = new ExoPlayer.Builder(context).build();
                    binding.eventVideoView.setPlayer(player);
                    boolean isMute = Preferences.shared.getBoolean("isMute");
                    player.setVolume(isMute ? 0f : 1f);
                    player.setRepeatMode(Player.REPEAT_MODE_ALL);
                    player.setMediaItem(MediaItem.fromUri(media.getUrl()));
                    player.prepare();
                    player.play();
                    binding.getRoot().setTag(player);
                } else {
                    binding.eventVideoView.setVisibility(View.GONE);
                    binding.ivBackground.setVisibility(View.VISIBLE);
                    String hex = Utils.notNullString(media.getBackgroundColor());
                    Graphics.applyGradientBackground(binding.ivBackground, Utils.isNullOrEmpty(hex) ? "#191919" : hex);
                }
            } else {
                String url = Utils.isNullOrEmpty(media.getUrl()) ? media.getUrl() : "";
                if (!Utils.isNullOrEmpty(url) && !Utils.isVideo(url)) {
                    binding.eventVideoView.setVisibility(View.GONE);
                    binding.ivBackground.setVisibility(View.VISIBLE);
                    Graphics.loadImage(url, binding.ivBackground);
                } else {
                    binding.eventVideoView.setVisibility(View.GONE);
                    binding.ivBackground.setVisibility(View.VISIBLE);
                    String hex = Utils.notNullString(media.getBackgroundColor());
                    Graphics.applyGradientBackground(binding.ivBackground, Utils.isNullOrEmpty(hex) ? "#191919" : hex);
                }
            }
        }

        binding.description.setText(model.getDesc());
        binding.userTitle.setText(model.getTitle());

        List<ContactUsBlockModel.CTAModel> ctas = model.getCta();
        if (ctas != null && !ctas.isEmpty()) {
            String text = ctas.get(0).getText();
            if (!android.text.TextUtils.isEmpty(text)) {
                binding.ctaButton.setText(text);
            }
        }

        binding.ctaButton.setOnClickListener(v -> showChatOptions(context, model));
    }

    private static void hideView(View view) {
        view.setVisibility(View.GONE);
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params != null) {
            params.height = 0;
            view.setLayoutParams(params);
        }
    }

    private static void showChatOptions(Context context, ContactUsBlockModel model) {
        new ChatOptionsBottomSheet(model)
                .show(((FragmentActivity) context).getSupportFragmentManager(),
                        "ChatOptionsBottomSheet");

//        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
//        DialogChatOptionsBinding chatBinding = DialogChatOptionsBinding.inflate(LayoutInflater.from(context));
//        bottomSheetDialog.setContentView(chatBinding.getRoot());
//
//
//
//        chatBinding.tvAdminChat.setOnClickListener(v -> {
//            bottomSheetDialog.dismiss();
//            Utils.preventDoubleClick(v);
//            UserDetailModel model1 = new UserDetailModel();
//            String adminID = BuildConfig.isLive ? "65c0d6ad1ccb8aa07703d3aa" : "67e3ec4d073aaccac53fe908";
//            model1.setId(adminID);
//            model1.setFirstName("Whosin Admin");
//            model1.setImage("https://whosin-bucket.nyc3.digitaloceanspaces.com/file/1721896083557_image-1721896083557.jpg");
//            ChatModel chatModel = new ChatModel(model1);
//            Intent intent = new Intent(context, ChatMessageActivity.class);
//            intent.putExtra("chatModel", new Gson().toJson(chatModel));
//            intent.putExtra("isFromRaynaTicket", true);
//            context.startActivity(intent);
//        });
//
//        chatBinding.tvWhatsappChat.setOnClickListener(v -> {
//            bottomSheetDialog.dismiss();
//
//            String defaultPhone = "+971554373163";
//            String phone = defaultPhone;
//            String message = "Hello, I need a customized itinerary!";
//
//            String digits = phone.replace("+", "").replace(" ", "");
//            String encodedMessage = Uri.encode(message);
//
//            Intent whatsappIntent = new Intent(Intent.ACTION_VIEW,
//                    Uri.parse("whatsapp://send?phone=" + digits + "&text=" + encodedMessage));
//            whatsappIntent.setPackage("com.whatsapp");
//            Intent whatsappBusinessIntent = new Intent(Intent.ACTION_VIEW,
//                    Uri.parse("whatsapp://send?phone=" + digits + "&text=" + encodedMessage));
//            whatsappBusinessIntent.setPackage("com.whatsapp.w4b");
//
//            if (whatsappIntent.resolveActivity(context.getPackageManager()) != null) {
//                context.startActivity(whatsappIntent);
//            } else if (whatsappBusinessIntent.resolveActivity(context.getPackageManager()) != null) {
//                context.startActivity(whatsappBusinessIntent);
//            } else {
//                Intent webIntent = new Intent(Intent.ACTION_VIEW,
//                        Uri.parse("https://wa.me/" + digits + "?text=" + encodedMessage));
//                context.startActivity(webIntent);
//            }
//        });
//
//
//        bottomSheetDialog.show();
    }
}
