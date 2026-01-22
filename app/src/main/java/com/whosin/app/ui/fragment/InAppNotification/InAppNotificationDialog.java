package com.whosin.app.ui.fragment.InAppNotification;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.gson.Gson;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.roundcornerlayout.CornerType;
import com.whosin.app.comman.ui.roundcornerlayout.RoundCornerLinearLayout;
import com.whosin.app.databinding.FragmentInAppNotificationDialogBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.IANComponentModel;
import com.whosin.app.service.models.InAppNotificationModel;
import com.whosin.app.service.models.NotificationModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.comman.BaseActivity;
import com.whosin.app.ui.activites.raynaTicket.RaynaTicketDetailActivity;
import com.whosin.app.ui.activites.wallet.WalletActivity;


public class InAppNotificationDialog extends BaseActivity {

    private FragmentInAppNotificationDialogBinding binding;

    private InAppNotificationModel model = null;

    private boolean isFullScreen = false;

    private ActivityResultLauncher<Intent> launcher;


    // --------------------------------------
    // region Life Cycle
    // --------------------------------------

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initUi() {

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#33000000")));


        String eventJson = getIntent().getStringExtra("eventModelJson");

        if (!TextUtils.isEmpty(eventJson)) {
            Gson gson = new Gson();
            model = gson.fromJson(eventJson, InAppNotificationModel.class);
        }



        if (model != null) {

            if (!TextUtils.isEmpty(model.getId())){
                requestInAppNotifiactionRead(model.getId());
            }


            boolean isClassic = "classic".equalsIgnoreCase(model.getLayout());
            if ("full-screen".equalsIgnoreCase(model.getViewType())) {
                isFullScreen = true;
            }
            binding.feedLayout.setVisibility(isClassic ? View.GONE : View.VISIBLE);
            binding.classicLayout.setVisibility(isClassic ? View.VISIBLE : View.GONE);
            if (isClassic) {

                if (isFullScreen) {
                    makeClassicLayoutFullScreen();
                }

                setTextLabelValue(binding.tvTitle, model.getTitle());
                setTextLabelValue(binding.tvPSubTitle, model.getSubtitle());
                setTextLabelValue(binding.tvDescription, model.getDescription());
                setButtonValue(binding.button1Rounded,binding.button1, model.getButton1());
                setButtonValue(binding.button2Rounded,binding.button2, model.getButton2());
                if (!TextUtils.isEmpty(model.getImage()) && Utils.validateUrl(model.getImage())) {
                    binding.inAppImage.setVisibility(View.VISIBLE);
                    Graphics.loadCoilImage(activity,binding.inAppImage,model.getImage());
                } else {
                    binding.inAppImage.setVisibility(View.GONE);
                }

                if (model.getBackground() != null && !TextUtils.isEmpty(model.getBackground().getImage()) && Utils.validateUrl(model.getBackground().getImage())) {
                    binding.inAppBgImage.setVisibility(View.VISIBLE);
                    Graphics.loadCoilImage(activity, binding.inAppBgImage, model.getBackground().getImage());
                } else {
                    binding.inAppBgImage.setVisibility(View.GONE);
                }

                binding.classicLayout.setBackgroundColor(safeParseColor(model.getBackground() != null ? model.getBackground().getColor() : null));
            } else {


                if (isFullScreen) {
                    makeFeedLayoutFullScreen();

                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) binding.innerFeedLayout.getLayoutParams();
                    if (isFullScreen) {
                        params.height = 0;
                    } else {
                        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    }

                    binding.innerFeedLayout.setLayoutParams(params);

                }

                setTextLabelValue(binding.tvFeedTitle, model.getTitle());
                setTextLabelValue(binding.tvFeedSubTitle, model.getSubtitle());
                setTextLabelValue(binding.tvFeedDescription, model.getDescription());
                setButtonValue(binding.button1RoundedFeed,binding.feedsButton1, model.getButton1());
                setButtonValue(binding.button2RoundedFeed,binding.feedsButton2, model.getButton2());
                if (!TextUtils.isEmpty(model.getImage()) && Utils.validateUrl(model.getImage())) {
                    binding.inAppFeedImage.setVisibility(View.VISIBLE);
                    Graphics.loadCoilImage(activity,binding.inAppFeedImage,model.getImage());
                } else {
                    binding.inAppFeedImage.setVisibility(View.GONE);
                }
                if (model.getBackground() != null && !TextUtils.isEmpty(model.getBackground().getImage()) && Utils.validateUrl(model.getBackground().getImage())) {
                    binding.inAppFeedBgImage.setVisibility(View.VISIBLE);
                    Graphics.loadCoilImage(activity, binding.inAppFeedBgImage, model.getBackground().getImage());

                    if ("full-screen".equalsIgnoreCase(model.getViewType())) {
                        int heightInSdp = 380;
                        int heightInPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, heightInSdp, activity.getResources().getDisplayMetrics());
                        ViewGroup.LayoutParams params = binding.inAppFeedImage.getLayoutParams();
                        params.height = heightInPx;
                        binding.inAppFeedImage.setLayoutParams(params);

                    }

                } else {
                    binding.inAppFeedBgImage.setVisibility(View.GONE);

                }

                binding.feedLayout.setBackgroundColor(safeParseColor(model.getBackground() != null ? model.getBackground().getColor() : null));

            }
        }


        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> finish());

    }

    @Override
    protected void setListeners() {

        binding.ivClose.setOnClickListener(view -> finish());

        binding.ivFeedClose.setOnClickListener(view -> finish());

        binding.button1.setOnClickListener(v -> {
            if (model != null) {
                openView(activity, model.getButton1());
            }
        });
        binding.button2.setOnClickListener(v -> {
            if (model != null) {
                openView(activity, model.getButton2());
            }
        });
        binding.feedsButton1.setOnClickListener(v -> {
            if (model != null) {
                openView(activity, model.getButton1());
            }
        });
        binding.feedsButton2.setOnClickListener(v -> {
            if (model != null) {
                openView(activity, model.getButton2());
            }
        });
    }



    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = FragmentInAppNotificationDialogBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @SuppressWarnings("MissingSuperCall")
    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1005){
            finish();
        }
    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------


    private void setTextLabelValue(TextView label, IANComponentModel model) {
        if (model == null) {
            label.setText("");
            return;
        }

        label.setText(model.getText());

        try {
            label.setTextColor(Color.parseColor(model.getColor()));
        } catch (IllegalArgumentException e) {
            label.setTextColor(Color.BLACK);
        }

        String alignment = model.getAlignment().toLowerCase();
        switch (alignment) {
            case "center":
                label.setGravity(Gravity.CENTER);
                break;
            case "right":
                label.setGravity(Gravity.END);
                break;
            default:
                label.setGravity(Gravity.START);
                break;
        }
    }

    private void setButtonValue(RoundCornerLinearLayout linearLayout ,TextView button, IANComponentModel model) {
        if (model == null || model.getText().isEmpty()) {
            button.setVisibility(View.GONE);
            linearLayout.setVisibility(View.GONE);
            return;
        }
        button.setText(model.getText());

        try {
            button.setTextColor(Color.parseColor(model.getColor()));
        } catch (IllegalArgumentException e) {
            button.setTextColor(Color.BLACK);
        }

        try {
            button.setBackgroundColor(Color.parseColor(model.getBgColor()));
        } catch (IllegalArgumentException e) {
            button.setBackgroundColor(Color.TRANSPARENT);
        }

        button.setVisibility(View.VISIBLE);
        linearLayout.setVisibility(View.VISIBLE);
    }

    private void openView(Context context, IANComponentModel view) {
        if (view == null || view.getAction().isEmpty()) return;
        Intent intent = null;
        switch (view.getAction()) {
            case "link":
                try {
                    Uri webpage = Uri.parse(view.getData());
                    Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);
                    if (webIntent.resolveActivity(context.getPackageManager()) != null) {
                        context.startActivity(webIntent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            case "ticket":
                intent = new Intent(context, RaynaTicketDetailActivity.class);
                intent.putExtra("ticketId", view.getData());
                break;
            case "ticket-booking":
                intent = new Intent(context, WalletActivity.class);
                break;

            default:
                break;
        }

        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            launcher.launch(intent);
        }

    }

    private int safeParseColor(String colorString) {
        if (!TextUtils.isEmpty(colorString)) {
            try {
                return Color.parseColor(colorString);
            } catch (IllegalArgumentException ignored) {

            }
        }
        return Color.parseColor("#ffffff");
    }

    private void makeClassicLayoutFullScreen() {
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) binding.classicLayout.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        params.topMargin = 0;
        params.bottomMargin = 0;
        params.setMargins(0, 0, 0, 0);
        binding.classicLayout.setLayoutParams(params);
        binding.classicLayout.setPadding(0, 0, 0, 0);
        binding.classicLayout.setCornerRadius(0, CornerType.ALL);
    }

    private void makeFeedLayoutFullScreen() {
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) binding.feedLayout.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        params.topMargin = 0;
        params.bottomMargin = 0;
        params.setMargins(0, 0, 0, 0);
        binding.feedLayout.setLayoutParams(params);
        binding.feedLayout.setPadding(0, 0, 0, 0);
        binding.feedLayout.setCornerRadius(0, CornerType.ALL);
    }


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestInAppNotifiactionRead(String id) {
        DataService.shared(activity).requestInAppNotifiactionRead(id, new RestCallback<ContainerModel<NotificationModel>>(this) {
            @Override
            public void result(ContainerModel<NotificationModel> model, String error) {
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    // --------------------------------------
    // region Adapter
    // --------------------------------------


    // endregion
    // --------------------------------------

}