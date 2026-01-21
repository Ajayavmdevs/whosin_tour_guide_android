package com.whosin.app.ui.activites.Promoter;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.whosin.app.R;
import com.whosin.app.comman.AppConstants;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.FragmentPromoterRecentBottomSheetBinding;
import com.whosin.app.databinding.ItemRecentActivityBinding;
import com.whosin.app.service.models.LogsModel;

import java.util.ArrayList;
import java.util.List;

public class PromoterRecentBottomSheet extends DialogFragment {

    private FragmentPromoterRecentBottomSheetBinding binding;

    private final RecentListAdapter<LogsModel> recentListAdapter = new RecentListAdapter<>();

    public List<LogsModel> logsModels = new ArrayList<>();

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
        binding = FragmentPromoterRecentBottomSheetBinding.bind(view);

        binding.tvTitle.setText(Utils.getLangValue("recent_activity"));
        binding.emptyPlaceHolderView.setEmptyPlaceTxtTitle(Utils.getLangValue("no_users_available"));

        Glide.with(requireActivity()).load(R.drawable.icon_close_btn).into(binding.ivClose);

        binding.recentRecycler.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false));
        binding.recentRecycler.setAdapter(recentListAdapter);

        if (logsModels != null && !logsModels.isEmpty()) {
            recentListAdapter.updateData(logsModels);
        }

    }

    public int getLayoutRes() {
        return R.layout.fragment_promoter_recent_bottom_sheet;
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
            BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
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

    private class RecentListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {
        

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_recent_activity));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            LogsModel model = (LogsModel) getItem(position);

            if (model == null) return;

            String date = Utils.convertToCustomFormat(model.getDateTime());

            if (model.getType().equals("invite")){
                viewHolder.binding.statusTv.setText(model.getSubType().toUpperCase() + "  -");
                viewHolder.binding.dateTimeTv.setText(date);
            }

        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            private final ItemRecentActivityBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemRecentActivityBinding.bind(itemView);
            }
        }
    }
    // endregion
    // --------------------------------------

}