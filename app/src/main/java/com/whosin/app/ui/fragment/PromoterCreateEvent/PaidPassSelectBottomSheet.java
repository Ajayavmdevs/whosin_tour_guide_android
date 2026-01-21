package com.whosin.app.ui.fragment.PromoterCreateEvent;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.FragmentPaidPassSelectBottomSheetBinding;
import com.whosin.app.databinding.FragmentSelectDaysBottomSheetBinding;
import com.whosin.app.databinding.ItemDaysSelectionBinding;
import com.whosin.app.service.manager.PromoterProfileManager;
import com.whosin.app.service.models.PromoterPaidPassModel;
import com.whosin.app.service.models.RatingModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class PaidPassSelectBottomSheet extends DialogFragment {

    private FragmentPaidPassSelectBottomSheetBinding binding;

    private final PaidPassListAdapter<PromoterPaidPassModel> paidPassAdapter = new PaidPassListAdapter<>();

    public CommanCallback<String> callback;

    public String selectedPaidPassTitle = "";

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
        binding = FragmentPaidPassSelectBottomSheetBinding.bind(v);

        binding.userName.setText(Utils.getLangValue("select_paid_pass"));

        binding.paidPassRecycleView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        binding.paidPassRecycleView.setAdapter(paidPassAdapter);
        paidPassAdapter.updateData(PromoterProfileManager.shared.promoterPaidPassList);
    }

    private void setListener() {
        Glide.with(requireActivity()).load(R.drawable.icon_close_btn).into(binding.ivClose);
        binding.ivClose.setOnClickListener(view -> {
            Utils.preventDoubleClick(view);
            dismiss();
        });

    }

    private int getLayoutRes() {
        return R.layout.fragment_paid_pass_select_bottom_sheet;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new BottomSheetDialog(requireActivity(), R.style.BottomSheetDialogThemeNoFloating);
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------




    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    public class PaidPassListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_days_selection));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            PromoterPaidPassModel model = (PromoterPaidPassModel) getItem(position);
            String paidPassTitle = model.getTitle();
            viewHolder.mBinding.tvUserName.setText(paidPassTitle);

            int iconResource = selectedPaidPassTitle.equals(paidPassTitle) ? R.drawable.complete_icon : R.drawable.complete_icon_unselected;
            viewHolder.mBinding.selectedIcon.setImageResource(iconResource);


            holder.itemView.setOnClickListener(view -> {
               if (callback != null){
                   callback.onReceive(paidPassTitle);
                   dismiss();
               }
            });
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final ItemDaysSelectionBinding mBinding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemDaysSelectionBinding.bind(itemView);
            }
        }
    }

    // endregion
    // --------------------------------------


}