package com.whosin.business.ui.fragment.Chat;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.JsonObject;
import com.whosin.business.R;
import com.whosin.business.comman.DiffAdapter;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Graphics;
import com.whosin.business.comman.Utils;
import com.whosin.business.comman.interfaces.CommanCallback;
import com.whosin.business.comman.ui.UiUtils;
import com.whosin.business.databinding.FragmentReportBottomSheetBinding;
import com.whosin.business.databinding.FragmentShareDocumentBottomSheetBinding;
import com.whosin.business.databinding.ItemFriendsChatBinding;
import com.whosin.business.databinding.ItemReportDesignViewBinding;
import com.whosin.business.service.DataService;
import com.whosin.business.service.manager.BlockUserManager;
import com.whosin.business.service.models.ChatModel;
import com.whosin.business.service.models.ContainerModel;
import com.whosin.business.service.models.CurrentUserRatingModel;
import com.whosin.business.service.models.RatingModel;
import com.whosin.business.service.models.UserDetailModel;
import com.whosin.business.service.rest.RestCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ReportBottomSheet extends DialogFragment {

    private FragmentReportBottomSheetBinding binding;

    private final ReportListAdapter<RatingModel> reportListAdapter = new ReportListAdapter<>();

    public CommanCallback<Boolean> callback = null;

    public Boolean isOnlyReport = false;

    public ChatModel chatModel = null;

    public CurrentUserRatingModel ratingModel = null;


    public Boolean isFromChat = false;


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutRes(), container, false);
        initUi(view);
        setListener();
        return view;
    }

    private void setListener() {

        binding.ivClose.setOnClickListener(v -> dismiss());

        binding.scrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            v.getParent().requestDisallowInterceptTouchEvent(v.canScrollVertically(-1));
        });


        binding.btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(reportListAdapter.selectedReportTitle)){
                    Toast.makeText(requireActivity(), Utils.getLangValue("please_select_reason_to_report"), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(binding.editGetOtherTxt.getText().toString())){
                    Toast.makeText(requireActivity(), Utils.getLangValue("please_enter_message_to_report"), Toast.LENGTH_SHORT).show();
                    return;
                }

                Graphics.showAlertDialogWithOkCancel(requireActivity(), requireActivity().getString(R.string.app_name), Utils.getLangValue("are_you_sure_you_want_to_report"), Utils.getLangValue("yes"), Utils.getLangValue("no"), isConfirmed -> {
                    if (isConfirmed) {
                        requestUserReportAdd();
                    }
                });

            }
        });

    }


    private void initUi(View view) {
        binding = FragmentReportBottomSheetBinding.bind(view);

        binding.tvTitle.setText(Utils.getLangValue("report"));
        binding.mainTitle1.setText(Utils.getLangValue("select_a_problem_to_report"));
        binding.mainTitle2.setText(Utils.getLangValue("report_info_msg"));

        binding.editGetOtherTxt.setHint(Utils.getLangValue("please_describe_the_issue"));
        binding.tvNext.setHint(Utils.getLangValue("submit"));

        Glide.with(requireActivity()).load(R.drawable.icon_close).into(binding.ivClose);
        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        binding.reportListRecycleView.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false));
        binding.reportListRecycleView.setAdapter(reportListAdapter);

        List<RatingModel> reportList = new ArrayList<>();

        String[] reasons = {
                "Harassment/Abuse",
                "Hate Speech",
                "Sexual/Inappropriate Content",
                "Threats",
                "Spam/Scams",
                "Fake or Impersonation",
                "Misinformation",
                "Other"
        };

        for (String reason : reasons) {
            reportList.add(new RatingModel(reason));
        }

        reportListAdapter.updateData(reportList);

    }

    public int getLayoutRes() {
        return R.layout.fragment_report_bottom_sheet;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = new BottomSheetDialog(requireActivity(), R.style.BottomSheetDialogThemeNoFloating);
        dialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
            FrameLayout bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            ViewGroup.LayoutParams layoutParam = bottomSheet.getLayoutParams();
            layoutParam.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            layoutParam.width = ViewGroup.LayoutParams.MATCH_PARENT;
            bottomSheet.setLayoutParams(layoutParam);
            if (bottomSheet != null) {
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
        return dialog;
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private JsonObject getJsonObject() {

        JsonObject jsonObject = new JsonObject();


        jsonObject.addProperty("reason", reportListAdapter.selectedReportTitle);
        jsonObject.addProperty("message", binding.editGetOtherTxt.getText().toString());

        if (isFromChat) {
            jsonObject.addProperty("type", "chat");
            if (chatModel != null && chatModel.getLastMsg() != null) {
                jsonObject.addProperty("typeId", chatModel.getLastMsg().getId());
                jsonObject.addProperty("userId", chatModel.getUser().getId());
            }

        } else {
            jsonObject.addProperty("type", "review");
            if (ratingModel != null) {
                jsonObject.addProperty("typeId", ratingModel.getId());
                jsonObject.addProperty("userId", ratingModel.getUserId());

            }
        }

        return jsonObject;
    }


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestUserReportAdd() {
        if (getJsonObject().isEmpty() || getJsonObject().isJsonNull()) return;
        binding.progressView.setVisibility(View.VISIBLE);
        DataService.shared(requireActivity()).requestUserReportAdd(getJsonObject(), new RestCallback<ContainerModel<UserDetailModel>>(requireActivity()) {
            @Override
            public void result(ContainerModel<UserDetailModel> model, String error) {
                binding.progressView.setVisibility(View.GONE);
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(requireActivity(), error, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (isOnlyReport) {
                    String userId = null;

                    if (isFromChat && chatModel != null) {
                        userId = chatModel.getUser().getId();
                    } else if (ratingModel != null) {
                        userId = ratingModel.getUserId();
                    }

                    if (userId != null) {
                        BlockUserManager.addBlockUserId(userId);
                    }
                }


                if (callback != null) {
                    callback.onReceive(true);
                }
                dismiss();
            }
        });
    }




    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------


    public class ReportListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        public String selectedReportTitle = "";

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_report_design_view));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;

            RatingModel model = (RatingModel) getItem(position);
            viewHolder.binding.reportTitle.setText(model.getImage());


            viewHolder.binding.ivCheck.setOnCheckedChangeListener(null);

            boolean isSelected = !TextUtils.isEmpty(selectedReportTitle) && selectedReportTitle.equals(model.getImage());
            viewHolder.binding.ivCheck.setChecked(isSelected);
            viewHolder.binding.ivCheck.setButtonDrawable(isSelected ? R.drawable.check_box_select_unselect_owner : R.drawable.complete_icon_unselected);



            viewHolder.binding.ivCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedReportTitle = model.getImage();
                } else {
                    selectedReportTitle = "";
                }
                notifyDataSetChanged();
            });

            viewHolder.binding.getRoot().setOnClickListener(v -> {
                if (selectedReportTitle.equals(model.getImage())) {
                    selectedReportTitle = "";
                } else {
                    selectedReportTitle = model.getImage();
                }
                notifyDataSetChanged();
            });

        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final ItemReportDesignViewBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemReportDesignViewBinding.bind(itemView);
            }
        }

    }

    // endregion
    // --------------------------------------
}