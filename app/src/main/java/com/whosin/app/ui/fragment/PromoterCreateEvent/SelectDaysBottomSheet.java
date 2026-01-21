package com.whosin.app.ui.fragment.PromoterCreateEvent;

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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.FragmentSelectDaysBottomSheetBinding;
import com.whosin.app.databinding.ItemDaysSelectionBinding;
import com.whosin.app.service.models.RatingModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SelectDaysBottomSheet extends DialogFragment {

    private FragmentSelectDaysBottomSheetBinding binding;

    private final DaysAdapter<RatingModel> daysAdapter = new DaysAdapter<>();

    public CommanCallback<List<String>> callback;

    public List<String> selectedDays = new ArrayList<>();

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
        binding = FragmentSelectDaysBottomSheetBinding.bind(v);

        updateButtonText();

        binding.dayRecycler.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));

        List<RatingModel> daysList = new ArrayList<>();
        daysList.add(new RatingModel("sunday"));
        daysList.add(new RatingModel("monday"));
        daysList.add(new RatingModel("tuesday"));
        daysList.add(new RatingModel("wednesday"));
        daysList.add(new RatingModel("thursday"));
        daysList.add(new RatingModel("friday"));
        daysList.add(new RatingModel("saturday"));

        binding.dayRecycler.setAdapter(daysAdapter);
        daysAdapter.updateData(daysList);
    }

    private void setListener() {
        Glide.with(requireActivity()).load(R.drawable.icon_close_btn).into(binding.ivClose);
        binding.ivClose.setOnClickListener(view -> {
            Utils.preventDoubleClick(view);
            dismiss();
        });

        binding.seletAllDays.setOnClickListener(view -> {
            Utils.preventDoubleClick(view);
            if (binding.tvSelectAll.getText().toString().equals("Select All")) {
                selectedDays.clear();
                addAllDays();
            }else {
                selectedDays.clear();
            }
            daysAdapter.notifyDataSetChanged();
            updateButtonText();
        });

        binding.constraintDone.setOnClickListener(view -> {
            if (selectedDays.isEmpty()) {
                Toast.makeText(getContext(), "Please select day", Toast.LENGTH_SHORT).show();
                return;
            }
            if (callback != null) {
                callback.onReceive(selectedDays);
            }
            dismiss();
        });


    }

    private int getLayoutRes() {
        return R.layout.fragment_select_days_bottom_sheet;
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

    private void updateButtonText() {
        if (selectedDays.size() == 7) {
            binding.tvSelectAll.setText("Deselect All");
        } else {
            binding.tvSelectAll.setText("Select All");
        }
    }

    private void addAllDays(){
        Collections.addAll(selectedDays, "sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday");
    }


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
            String dayName = ratingModel.getImage();
            String formattedDayName = dayName.substring(0, 1).toUpperCase() + dayName.substring(1).toLowerCase();
            viewHolder.mBinding.tvUserName.setText(formattedDayName);

            int iconResource = selectedDays.contains(dayName) ? R.drawable.complete_icon : R.drawable.complete_icon_unselected;
            viewHolder.mBinding.selectedIcon.setImageResource(iconResource);

            
            holder.itemView.setOnClickListener(view -> {
                if (selectedDays.contains(dayName)) {
                    selectedDays.remove(dayName);
                } else {
                    selectedDays.add(dayName);
                }
                notifyItemChanged(position);
                updateButtonText();
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