package com.whosin.business.ui.commonBottomSheets;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.whosin.business.BuildConfig;
import com.whosin.business.R;
import com.whosin.business.comman.Utils;
import com.whosin.business.databinding.DialogChatOptionsBinding;
import com.whosin.business.service.models.ChatModel;
import com.whosin.business.service.models.ContactUsBlockModel;
import com.whosin.business.service.models.UserDetailModel;
import com.whosin.business.ui.activites.home.Chat.ChatMessageActivity;

public class ChatOptionsBottomSheet extends BottomSheetDialogFragment {

    private ContactUsBlockModel model;

    public ChatOptionsBottomSheet(ContactUsBlockModel model) {
        this.model = model;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.OtpDialogStyle);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new BottomSheetDialog(requireContext(),
                R.style.BottomSheetDialogThemeNoFloating);
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        DialogChatOptionsBinding chatBinding =
                DialogChatOptionsBinding.inflate(inflater, container, false);

        setupClicks(chatBinding);

        return chatBinding.getRoot();
    }

    private void setupClicks(DialogChatOptionsBinding chatBinding) {

        chatBinding.tvAdminChat.setOnClickListener(v -> {
            dismiss();
            Utils.preventDoubleClick(v);

            UserDetailModel user = new UserDetailModel();
            String adminID = BuildConfig.isLive
                    ? "65c0d6ad1ccb8aa07703d3aa"
                    : "67e3ec4d073aaccac53fe908";

            user.setId(adminID);
            user.setFirstName("Whosin Admin");
            user.setImage("https://whosin-bucket.nyc3.digitaloceanspaces.com/file/1721896083557_image-1721896083557.jpg");

            ChatModel chatModel = new ChatModel(user);

            Intent intent = new Intent(requireContext(), ChatMessageActivity.class);
            intent.putExtra("chatModel", new Gson().toJson(chatModel));
            intent.putExtra("isFromRaynaTicket", true);
            startActivity(intent);
        });

        chatBinding.tvWhatsappChat.setOnClickListener(v -> {
            dismiss();

            String phone = "971554373163";
            String message = "Hello, I need a customized itinerary!";
            String encoded = Uri.encode(message);

            Intent wa = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("whatsapp://send?phone=" + phone + "&text=" + encoded));
            wa.setPackage("com.whatsapp");

            Intent w4b = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("whatsapp://send?phone=" + phone + "&text=" + encoded));
            w4b.setPackage("com.whatsapp.w4b");

            if (wa.resolveActivity(requireContext().getPackageManager()) != null) {
                startActivity(wa);
            } else if (w4b.resolveActivity(requireContext().getPackageManager()) != null) {
                startActivity(w4b);
            } else {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://wa.me/" + phone + "?text=" + encoded)));
            }
        });
    }
}

