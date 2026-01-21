package com.whosin.app.ui.fragment.CmProfile.CmBottomSheets;

import static com.whosin.app.comman.AppDelegate.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.whosin.app.R;
import com.whosin.app.comman.AppConstants;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.FragmentCmNotificationEventBottomSheetBinding;
import com.whosin.app.databinding.ItemPromoterNotificationEventUserListBinding;
import com.whosin.app.service.models.NotificationModel;
import com.whosin.app.service.models.PromoterListModel;

import java.util.List;

public class CmNotificationEventBottomSheet extends DialogFragment {

    private FragmentCmNotificationEventBottomSheetBinding binding;
    private PromoterNotificationEventUserAdapter<PromoterListModel> userAdapter = new PromoterNotificationEventUserAdapter<>();

    public List<PromoterListModel> models;

    public NotificationModel notificationModel;
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

    public void setListener() {
        binding.ivClose.setOnClickListener(v -> dismiss());

    }

    public void initUi(View view) {
        binding = FragmentCmNotificationEventBottomSheetBinding.bind(view);

        Glide.with(requireActivity()).load(R.drawable.icon_close_btn).into(binding.ivClose);

        binding.eventUsersRecycler.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        binding.eventUsersRecycler.setAdapter(userAdapter);
        userAdapter.updateData(models);

        if (notificationModel != null) {
            binding.timeDate.setText(Utils.changeDateFormat(notificationModel.getEvent().getDate(), AppConstants.DATEFORMAT_SHORT, AppConstants.DATEFORMT_MM_DATE + " | "));
            binding.startEndTime.setText(notificationModel.getEvent().getStartTime() + "-" + notificationModel.getEvent().getEndTime());

            if (notificationModel.getEvent().getVenueType().equals("custom")) {
                Graphics.loadImage(notificationModel.getEvent().getCustomVenue().getImage(), binding.profileImage);
                binding.userName.setText(notificationModel.getEvent().getCustomVenue().getName());
            } else {
                Graphics.loadImage(notificationModel.getEvent().getVenue().getCover(), binding.profileImage);
                binding.userName.setText(notificationModel.getEvent().getVenue().getName());

            }
        }
    }


    public int getLayoutRes() {
        return R.layout.fragment_promoter_notification_event_bottom_sheet;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = new BottomSheetDialog(requireActivity(), R.style.BottomSheetDialogThemeNoFloating);
        dialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
            FrameLayout bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            ViewGroup.LayoutParams layoutParam = bottomSheet.getLayoutParams();
            layoutParam.height = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParam.width = ViewGroup.LayoutParams.MATCH_PARENT;
            bottomSheet.setLayoutParams(layoutParam);
//            bottomSheet.setLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
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

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------



    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    private class PromoterNotificationEventUserAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_promoter_notification_event_user_list));

        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            PromoterListModel model1 = (PromoterListModel) getItem(position);
            Graphics.loadImageWithFirstLetter(model1.getImage(), viewHolder.binding.imgProfile, model1.getTitle());
            viewHolder.binding.userTitle.setText(model1.getTitle());
            viewHolder.binding.description.setText(model1.getDescription());
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private ItemPromoterNotificationEventUserListBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemPromoterNotificationEventUserListBinding.bind(itemView);
            }
        }
    }

    // endregion
    // --------------------------------------
}