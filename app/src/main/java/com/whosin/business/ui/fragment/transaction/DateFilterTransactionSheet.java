package com.whosin.business.ui.fragment.transaction;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.whosin.business.R;
import com.whosin.business.comman.interfaces.CommanCallback;
import com.whosin.business.databinding.FragmentDateFilterTransactionSheetBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class DateFilterTransactionSheet extends DialogFragment {
    private FragmentDateFilterTransactionSheetBinding binding;

    public CommanCallback<List<String>> callback;

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
        binding = FragmentDateFilterTransactionSheetBinding.bind(v);

        Calendar maxDate = Calendar.getInstance();
        binding.materialCalenderView.setMaximumDate(maxDate);

    }

    private void setListener() {
        binding.materialCalenderView.setOnDayClickListener(eventDay -> {
            if (eventDay.getCalendar().after(Calendar.getInstance())) {
                return; // future date disable
            }
            List<Calendar> selectedDates = binding.materialCalenderView.getSelectedDates();
            showAndSetDate(selectedDates);
        });


        binding.btnDone.setOnClickListener(view -> {
            List<Calendar> selectedDates = binding.materialCalenderView.getSelectedDates();
            if (selectedDates.isEmpty()) {
                Toast.makeText(getContext(), "Please select date", Toast.LENGTH_SHORT).show();
                return;
            }
            
            List<String> formattedDates = new ArrayList<>();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            
            // Sort dates to ensure start and end are correct
            Collections.sort(selectedDates, (c1, c2) -> c1.compareTo(c2));
            
            for (Calendar calendar : selectedDates) {
                formattedDates.add(dateFormat.format(calendar.getTime()));
            }

            if (callback != null) {
                callback.onReceive(formattedDates);
            }
            dismiss();
        });


    }



    private void showAndSetDate(List<Calendar> calendars) {
        if (calendars == null || calendars.isEmpty()) return;

        binding.btnDone.setVisibility(View.VISIBLE);

        Collections.sort(calendars, (c1, c2) -> c1.compareTo(c2));
    }

    private int getLayoutRes() {
        return R.layout.fragment_date_filter_transaction_sheet;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getView() != null) {
            getView().post(() -> {
                View parent = (View) getView().getParent();
                BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(parent);
                int peekHeight = parent.getHeight();
                behavior.setPeekHeight(peekHeight);
            });
        }
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

}
