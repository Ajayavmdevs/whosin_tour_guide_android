package com.whosin.app.ui.activites.home.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.JsonObject;
import com.whosin.app.R;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.BooleanResult;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.databinding.ActivityWriteReviewBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.AppSettingManager;
import com.whosin.app.service.manager.CheckUserSession;
import com.whosin.app.service.manager.RaynaTicketManager;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.manager.TranslationManager;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.CurrentUserRatingModel;
import com.whosin.app.service.models.MessageEvent;
import com.whosin.app.service.models.rayna.RaynaCheckReviewModel;
import com.whosin.app.service.models.rayna.RaynaTicketDetailModel;
import com.whosin.app.service.rest.RestCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.Optional;

public class WriteReviewActivity extends DialogFragment {
    private ActivityWriteReviewBinding binding;
    public CommanCallback<Integer> result;
    private String id = "";
    private CurrentUserRatingModel model;
    public String type = "";
    public CommanCallback<Boolean> callback;
    public String review;
    public Activity activity;
    public String ticketID = "";
    public boolean isRaynaReviewCheck = false;
    public String ticketName = "";

    // --------------------------------------
    // region LifeCycle
    //

    public WriteReviewActivity(String id, CurrentUserRatingModel model, String type) {
        this.id = id;
        this.model = model;
        this.type = type;
    }

    public WriteReviewActivity() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle);

        if (activity == null){
            activity = requireActivity();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutRes(), container, false);
        initUi(view);
        setListener();
        return view;
    }

    private void initUi(View view) {
        binding = ActivityWriteReviewBinding.bind(view);

        binding.tvCancel.setText(TranslationManager.shared.get("cancel"));
        binding.tvRate.setText(TranslationManager.shared.get("tap_star_to_rate"));
        binding.etReview.setHint(TranslationManager.shared.get("write_review_placeholder"));
        binding.writeReviewTitle.setText(Utils.getLangValue("write_review_title"));
        binding.tvSend.setText(Utils.getLangValue("send"));


        if (model != null) {
            binding.rating.setRating(model.getStars());

            binding.etReview.setText(model.getReview());
        }


        if (!TextUtils.isEmpty(ticketID)) {
            Optional<RaynaTicketDetailModel> tickModel = SessionManager.shared.geHomeBlockData().getTickets().stream().filter(p -> p.getId().equals(ticketID)).findFirst();
            if (tickModel.isPresent()) {
                setUpTicketData(tickModel.get());
            } else {
                requestTicketDetail(ticketID);
            }
        } else {
            binding.ticketLayout.setVisibility(View.GONE);
        }


    }

    private void setListener() {

        binding.tvSend.setOnClickListener(view -> {

            float rating = binding.rating.getRating();
            if (rating > 0) {
                CheckUserSession.checkSessionAndProceed(activity,() -> requestAddRating(rating));
            } else {
                Toast.makeText(requireActivity(), TranslationManager.shared.get("please_give_rating"), Toast.LENGTH_SHORT).show();
            }
        });


        binding.tvCancel.setOnClickListener(v -> {
            if (isRaynaReviewCheck) {
                Graphics.showAlertDialogWithOkCancel(activity, activity.getString(R.string.app_name), "Would you like to skip the review for " + ticketName+ "?",
                        TranslationManager.shared.get("yes"), TranslationManager.shared.get("no"), aBoolean -> {
                            if (aBoolean) {
                                requestCheckRaynaReview();
                            }
                        });
            } else {
                AppSettingManager.shared.isAlreadyOpenReviewSheet = false;
                dismiss();
            }
        });


        binding.etReview.setOnTouchListener((v, event) -> {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        });
    }


    private int getLayoutRes() {

        return R.layout.activity_write_review;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        return new BottomSheetDialog(requireActivity(), R.style.BottomSheetDialogThemeNoFloating);
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        AppSettingManager.shared.isAlreadyOpenReviewSheet = false;
    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void setUpTicketData(RaynaTicketDetailModel model){
        binding.ticketLayout.setVisibility(View.VISIBLE);
        if (model.getImages() != null && !model.getImages().isEmpty()) {
            List<String> urls = model.getImages();
            urls.removeIf(Utils::isVideo);
            if (!urls.isEmpty()) Graphics.loadImage(urls.get(0),binding.ivTicket);
            ticketName = model.getTitle();
            binding.txtTitle.setText(Utils.notNullString(model.getTitle()));
            String cleanText = Html.fromHtml(model.getDescription()).toString().trim();
            binding.ticketDesciption.setText(Utils.notNullString(cleanText));
            binding.ticketAddress.setText(model.getCity());
            String startingAmount = model.getStartingAmount() != null ? String.valueOf(model.getStartingAmount()) : "N/A";

            if (model.getDiscount() != 0) {
                binding.tvDiscount.setVisibility(View.VISIBLE);
                binding.tvDiscount.setText(String.valueOf(model.getDiscount()) + "%");
            } else {
                binding.tvDiscount.setVisibility(View.GONE);
            }


            if (!"N/A".equals(startingAmount)) {
                String amount = Utils.roundFloatValue(Float.valueOf(startingAmount));
                SpannableString styledPrice = Utils.getStyledText(activity, amount);
                SpannableStringBuilder fullText = new SpannableStringBuilder()
                        .append("From ")
                        .append(styledPrice);

                binding.ticketFromAmount.setText(fullText);
            } else {
                SpannableString styledPrice = Utils.getStyledText(activity, "0");
                SpannableStringBuilder fullText = new SpannableStringBuilder()
                        .append("From ")
                        .append(styledPrice);
                binding.ticketFromAmount.setText(fullText);
            }
        }
    }


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------


    private void requestAddRating(float rating) {
        binding.progress.setVisibility(View.VISIBLE);
        DataService.shared(activity).requestAddRatings(id, rating, type, binding.etReview.getText().toString(), "", new RestCallback<ContainerModel<CurrentUserRatingModel>>(this) {
            @Override
            public void result(ContainerModel<CurrentUserRatingModel> model, String error) {
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(requireActivity(), error, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (result != null) {
                    result.onReceive((int)rating);

                }
                if (callback != null){
                    callback.onReceive(true);
                }
                EventBus.getDefault().post(new MessageEvent());
                AppSettingManager.shared.isAlreadyOpenReviewSheet = false;
                Toast.makeText(activity, model.message, Toast.LENGTH_SHORT).show();
                binding.progress.setVisibility(View.GONE);
                dismiss();


            }
        });

    }

    private void requestTicketDetail(String ticketId) {
        binding.ticketProgressBar.setVisibility(View.VISIBLE);
        DataService.shared(activity).requestRaynaCustomUserDetail(ticketId, new RestCallback<ContainerModel<RaynaTicketDetailModel>>(this) {
            @Override
            public void result(ContainerModel<RaynaTicketDetailModel> model, String error) {
                binding.ticketProgressBar.setVisibility(View.GONE);
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (model.getData() != null) {
                    setUpTicketData(model.getData());
                } else {
                    binding.ticketLayout.setVisibility(View.GONE);
                }
            }
        });
    }

    private void requestCheckRaynaReview() {
        binding.progress.setVisibility(View.VISIBLE);
        JsonObject object = new JsonObject();
        object.addProperty("customTicketId",ticketID);
        object.addProperty("status","skipped");
        DataService.shared(activity).requestRaynaTicketUpdateReview(object,new RestCallback<ContainerModel<RaynaCheckReviewModel>>(this) {
            @Override
            public void result(ContainerModel<RaynaCheckReviewModel> model, String error) {
                binding.progress.setVisibility(View.GONE);
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    AppSettingManager.shared.isAlreadyOpenReviewSheet = false;
                    dismiss();
                    return;
                }
                Toast.makeText(activity, model.message, Toast.LENGTH_SHORT).show();
                AppSettingManager.shared.isAlreadyOpenReviewSheet = false;
                dismiss();
            }
        });
    }

    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

}
