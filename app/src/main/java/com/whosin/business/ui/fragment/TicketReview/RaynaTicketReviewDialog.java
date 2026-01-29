package com.whosin.business.ui.fragment.TicketReview;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.JsonObject;
import com.whosin.business.R;
import com.whosin.business.comman.Graphics;
import com.whosin.business.comman.Utils;
import com.whosin.business.comman.interfaces.CommanCallback;
import com.whosin.business.databinding.ActivityWriteReviewBinding;
import com.whosin.business.databinding.FragmentRaynaTicketReviewDialogBinding;
import com.whosin.business.service.DataService;
import com.whosin.business.service.manager.AppSettingManager;
import com.whosin.business.service.manager.CheckUserSession;
import com.whosin.business.service.manager.SessionManager;
import com.whosin.business.service.models.ContainerModel;
import com.whosin.business.service.models.CurrentUserRatingModel;
import com.whosin.business.service.models.MessageEvent;
import com.whosin.business.service.models.rayna.RaynaCheckReviewModel;
import com.whosin.business.service.models.rayna.RaynaTicketDetailModel;
import com.whosin.business.service.rest.RestCallback;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class RaynaTicketReviewDialog extends DialogFragment {

    private FragmentRaynaTicketReviewDialogBinding binding;

    public CommanCallback<Integer> result;

    public String type = "";

    public CommanCallback<Boolean> callback;

    public Activity activity;

    public String ticketID = "";

    public String ticketName = "";


    // --------------------------------------
    // region LifeCycle
    //


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutRes(), container, false);
        initUi(view);
        setListener();
        return view;
    }

    private void initUi(View view) {

        binding = FragmentRaynaTicketReviewDialogBinding.bind(view);

        binding.etReview.setHint(Utils.getLangValue("write_review_placeholder"));
        binding.tvCancel.setText(Utils.getLangValue("not_now"));
        binding.tvSend.setText(Utils.getLangValue("submit"));

        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        if (activity == null){
            activity = requireActivity();
        }

        if (!TextUtils.isEmpty(ticketID)) {
            Optional<RaynaTicketDetailModel> tickModel = SessionManager.shared.geHomeBlockData().getTickets().stream().filter(p -> p.getId().equals(ticketID)).findFirst();
            if (tickModel.isPresent()) {
                setUpTicketData(tickModel.get());
            } else {
                requestTicketDetail(ticketID);
            }
        }


    }

    private void setListener() {

        binding.tvSend.setOnClickListener(view -> {

            float rating = binding.rating.getRating();
            if (rating > 0) {
                CheckUserSession.checkSessionAndProceed(activity, () -> requestAddRating(rating));
            } else {
                Toast.makeText(requireActivity(), Utils.getLangValue("please_give_rating"), Toast.LENGTH_SHORT).show();
            }
        });


        binding.tvCancel.setOnClickListener(v -> Graphics.showAlertDialogWithOkCancel(activity, activity.getString(R.string.app_name), Utils.setLangValue("would_you_like_skip",ticketName),
                Utils.getLangValue("yes"), Utils.getLangValue("no"), aBoolean -> {
                    if (aBoolean) {
                        requestCheckRaynaReview();
                    }
                }));


        binding.ivClose.setOnClickListener(v -> Graphics.showAlertDialogWithOkCancel(activity, activity.getString(R.string.app_name), Utils.setLangValue("would_you_like_skip",ticketName),
                Utils.getLangValue("yes"), Utils.getLangValue("no"), aBoolean -> {
                    if (aBoolean) {
                        requestCheckRaynaReview();
                    }
                }));
        

        binding.etReview.setOnTouchListener((v, event) -> {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        });
    }


    private int getLayoutRes() {
        return R.layout.fragment_rayna_ticket_review_dialog;
    }


    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        AppSettingManager.shared.isAlreadyOpenReviewSheet = false;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = new BottomSheetDialog(requireActivity(), R.style.BottomSheetDialogThemeNoFloating);
        dialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
            FrameLayout bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            assert bottomSheet != null;
            ViewGroup.LayoutParams layoutParam = bottomSheet.getLayoutParams();
            layoutParam.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            layoutParam.width = ViewGroup.LayoutParams.MATCH_PARENT;
            bottomSheet.setLayoutParams(layoutParam);
            BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        Objects.requireNonNull(dialog.getWindow()).setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        );

        return dialog;
    }



    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void setUpTicketData(RaynaTicketDetailModel model) {
        if (model.getImages() != null && !model.getImages().isEmpty()) {
            List<String> urls = model.getImages();
            urls.removeIf(Utils::isVideo);
            if (!urls.isEmpty()) Graphics.loadImage(urls.get(0), binding.ivLogo);
            ticketName = model.getTitle();
            binding.ticketTitleTv.setText(Utils.setLangValue("enjoyed_you_experience",model.getTitle()));
        }
    }


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------


    private void requestAddRating(float rating) {
        binding.progress.setVisibility(View.VISIBLE);
        DataService.shared(activity).requestAddRatings(ticketID, rating, type, binding.etReview.getText().toString(), "", new RestCallback<ContainerModel<CurrentUserRatingModel>>(this) {
            @Override
            public void result(ContainerModel<CurrentUserRatingModel> model, String error) {
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(requireActivity(), error, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (result != null) {
                    result.onReceive((int) rating);

                }
                if (callback != null) {
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
        DataService.shared(activity).requestRaynaCustomUserDetail(ticketId, new RestCallback<ContainerModel<RaynaTicketDetailModel>>(this) {
            @Override
            public void result(ContainerModel<RaynaTicketDetailModel> model, String error) {
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (model.getData() != null) {
                    setUpTicketData(model.getData());
                }
            }
        });
    }

    private void requestCheckRaynaReview() {
        binding.progress.setVisibility(View.VISIBLE);
        JsonObject object = new JsonObject();
        object.addProperty("customTicketId", ticketID);
        object.addProperty("status", "skipped");
        DataService.shared(activity).requestRaynaTicketUpdateReview(object, new RestCallback<ContainerModel<RaynaCheckReviewModel>>(this) {
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