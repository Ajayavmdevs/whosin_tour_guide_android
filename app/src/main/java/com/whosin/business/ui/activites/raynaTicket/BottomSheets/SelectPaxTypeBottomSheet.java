package com.whosin.business.ui.activites.raynaTicket.BottomSheets;

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

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.whosin.business.R;
import com.whosin.business.comman.DiffAdapter;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Utils;
import com.whosin.business.comman.interfaces.CommanCallback;
import com.whosin.business.comman.ui.UiUtils;
import com.whosin.business.databinding.FragmentSelectPaxTypeBottomSheetBinding;
import com.whosin.business.databinding.ItemDaysSelectionBinding;
import com.whosin.business.service.models.RatingModel;

import java.util.ArrayList;
import java.util.List;


public class SelectPaxTypeBottomSheet extends DialogFragment {

    private FragmentSelectPaxTypeBottomSheetBinding binding;

    private final DaysAdapter<RatingModel> daysAdapter = new DaysAdapter<>();

    public CommanCallback<String> callback;

    public String selectedPaxType = "";


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
        binding = FragmentSelectPaxTypeBottomSheetBinding.bind(v);


        binding.dayRecycler.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));

        List<RatingModel> daysList = new ArrayList<>();
        daysList.add(new RatingModel("Adult"));
        daysList.add(new RatingModel("Child"));
        daysList.add(new RatingModel("Infant"));

        binding.dayRecycler.setAdapter(daysAdapter);
        daysAdapter.updateData(daysList);
    }

    private void setListener() {
        Glide.with(requireActivity()).load(R.drawable.icon_close_btn).into(binding.ivClose);

        binding.ivClose.setOnClickListener(view -> {
            Utils.preventDoubleClick(view);
            dismiss();
        });

    }

    private int getLayoutRes() {
        return R.layout.fragment_select_pax_type_bottom_sheet;
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

    public class DaysAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_days_selection));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            RatingModel ratingModel = (RatingModel) getItem(position);
            viewHolder.mBinding.tvUserName.setText(ratingModel.getImage());

            int iconResource = selectedPaxType.equals(ratingModel.getImage()) ? R.drawable.complete_icon : R.drawable.complete_icon_unselected;
            viewHolder.mBinding.selectedIcon.setImageResource(iconResource);

            viewHolder.itemView.setOnClickListener(view -> {
                if (callback != null) {
                    callback.onReceive(viewHolder.mBinding.tvUserName.getText().toString());
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