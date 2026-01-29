package com.whosin.business.ui.fragment.reviewSheet;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.JsonObject;
import com.tapadoo.alerter.Alerter;
import com.whosin.business.R;
import com.whosin.business.comman.Graphics;
import com.whosin.business.comman.Utils;
import com.whosin.business.comman.interfaces.CommanCallback;
import com.whosin.business.databinding.FragmentUserFullReviewSheetBinding;
import com.whosin.business.service.DataService;
import com.whosin.business.service.manager.BlockUserManager;
import com.whosin.business.service.manager.SessionManager;
import com.whosin.business.service.models.CommonModel;
import com.whosin.business.service.models.ContactListModel;
import com.whosin.business.service.models.ContainerModel;
import com.whosin.business.service.models.CurrentUserRatingModel;
import com.whosin.business.service.models.UserDetailModel;
import com.whosin.business.service.rest.RestCallback;
import com.whosin.business.ui.activites.reportedUser.ReportedUseSuccessDialog;
import com.whosin.business.ui.fragment.Chat.ReportAndBlockBottomSheet;
import com.whosin.business.ui.fragment.Chat.ReportBottomSheet;

import java.util.ArrayList;
import java.util.Objects;

public class UserFullReviewSheet extends DialogFragment {

    private FragmentUserFullReviewSheetBinding binding;

    private Activity activity;

    public CurrentUserRatingModel currentUserRatingModel = null;

    public UserDetailModel userDetailModel = null;

    public ContactListModel contactListModel = null;

    public CommanCallback<Boolean> callback;

    private String userId = "";

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
        binding = FragmentUserFullReviewSheetBinding.bind(v);

        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        binding.ratingTitle.setText(Utils.getLangValue("rating_and_reviews"));

        if (activity == null) {
            activity = requireActivity();
        }

        if (currentUserRatingModel != null){

            binding.txtReview.setText(currentUserRatingModel.getReview());

            binding.txtDate.setText(Utils.convertMainDateFormatReview(currentUserRatingModel.getCreatedAt()));

            binding.rating.setRating(currentUserRatingModel.getStars());
            binding.rating.setIsIndicator(true);
        }

        if (userDetailModel != null) {
            Graphics.loadImageWithFirstLetter(userDetailModel.getImage(), binding.ivUser, userDetailModel.getFirstName());
            binding.txtTitle.setText(userDetailModel.getFullName());
            userId = userDetailModel.getId();
        }else {
            if (contactListModel != null){
                Graphics.loadImageWithFirstLetter(contactListModel.getImage(), binding.ivUser, contactListModel.getFirstName());
                binding.txtTitle.setText(contactListModel.getFullName());
                userId = contactListModel.getId();
            }
        }
    }

    private void setListener() {

        binding.iconMenu.setOnClickListener(v -> {
            if (userId.equals(SessionManager.shared.getUser().getId())){
                openDeleteActionSheet();
                return;
            }
            ReportAndBlockBottomSheet bottomSheet = new ReportAndBlockBottomSheet();
            bottomSheet.reportSheetCallBack = data -> {
                if (data) {
                    ReportBottomSheet reportBottomSheet = new ReportBottomSheet();
                    reportBottomSheet.ratingModel = currentUserRatingModel;
                    reportBottomSheet.isFromChat = false;
                    reportBottomSheet.isOnlyReport = true;
                    reportBottomSheet.callback = data1 -> {
                        ReportedUseSuccessDialog dialog = new ReportedUseSuccessDialog();
                        dialog.callBack = data2 -> {
                            if (data2) {
                               closeSheet();
                            }
                        };
                        dialog.show(getChildFragmentManager(),"");

                    };
                    reportBottomSheet.show(getChildFragmentManager(), "");
                }
            };
            bottomSheet.reportAndBlockCallBack = data -> {
                if (data) {
                    ReportBottomSheet reportBottomSheet = new ReportBottomSheet();
                    reportBottomSheet.ratingModel = currentUserRatingModel;
                    reportBottomSheet.isFromChat = false;
                    reportBottomSheet.callback = data1 -> {
                        ReportedUseSuccessDialog dialog = new ReportedUseSuccessDialog();
                        dialog.callBack = data2 -> {
                            if (data2){
                                requestBlockUserAdd(userId, binding.txtTitle.getText().toString());
                            }
                        };
                    };
                    reportBottomSheet.show(getChildFragmentManager(), "");
                }
            };
            bottomSheet.callback = data -> {
                if (data) {
                    Graphics.showAlertDialogWithOkCancel(activity, activity.getString(R.string.app_name), Utils.setLangValue("block_user_alert",binding.txtTitle.getText().toString()), Utils.getLangValue("yes"), Utils.getLangValue("no"), isConfirmed -> {
                        if (isConfirmed) {
                            requestBlockUserAdd(userId, binding.txtTitle.getText().toString());
                        }
                    });
                }
            };
            bottomSheet.show(getChildFragmentManager(), "");
        });


    }

    public int getLayoutRes() {
        return R.layout.fragment_user_full_review_sheet;
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
        return dialog;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------


    private void openDeleteActionSheet(){
        ArrayList<String> data = new ArrayList<>();
        data.add(Utils.getLangValue("delete"));
        Graphics.showActionSheet(activity, activity.getString(R.string.app_name), Utils.getLangValue("close"), data, (data1, position1) -> {
            if (position1 == 0) {
                Graphics.showAlertDialogWithOkCancel(activity, activity.getString(R.string.app_name), Utils.getLangValue("delete_review_confirm"), Utils.getLangValue("yes"), Utils.getLangValue("cancel"), isConfirmed -> {
                    if (isConfirmed) {
                        requestMyReviewDelete(currentUserRatingModel.getId());
                    }
                });
            }
        });
    }

    private void closeSheet(){
        if (callback != null){
            callback.onReceive(true);
        }
        dismiss();
    }

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestMyReviewDelete(String id) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", id);
//        showProgress();
        DataService.shared(activity).requestMyReviewDelete(jsonObject, new RestCallback<ContainerModel<CurrentUserRatingModel>>(this) {
            @Override
            public void result(ContainerModel<CurrentUserRatingModel> model, String error) {
//                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }

                closeSheet();
            }
        });
    }


    private void requestBlockUserAdd(String id,String userFullName) {
        DataService.shared(activity).requestBlockUser(id, new RestCallback<ContainerModel<CommonModel>>(this) {
            @Override
            public void result(ContainerModel<CommonModel> model, String error) {
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }
                Alerter.create(activity).setTitle(Utils.getLangValue("oh_snap")).setText(Utils.getLangValue("you_have_blocked") + userFullName).setTextAppearance(R.style.AlerterText).setTitleAppearance(R.style.AlerterTitle).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                BlockUserManager.addBlockUserId(id);
                closeSheet();
            }
        });
    }

    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------


    // endregion
    // --------------------------------------

}