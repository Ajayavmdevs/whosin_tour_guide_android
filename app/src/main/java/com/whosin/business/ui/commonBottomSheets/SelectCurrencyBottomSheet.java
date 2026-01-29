package com.whosin.business.ui.commonBottomSheets;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.whosin.business.R;
import com.whosin.business.comman.CustomTypefaceSpan;
import com.whosin.business.comman.DiffAdapter;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Graphics;
import com.whosin.business.comman.Utils;
import com.whosin.business.comman.interfaces.CommanCallback;
import com.whosin.business.comman.ui.UiUtils;
import com.whosin.business.databinding.FragmentSelectCurrencyBottomSheetBinding;
import com.whosin.business.databinding.ItemCurrencySelectBinding;
import com.whosin.business.service.manager.AppSettingManager;
import com.whosin.business.service.manager.SessionManager;
import com.whosin.business.service.manager.TranslationManager;
import com.whosin.business.service.models.CurrencyModel;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;


public class SelectCurrencyBottomSheet extends DialogFragment {

    private FragmentSelectCurrencyBottomSheetBinding binding;

    private Activity activity;

    private int selectedSlotPosition = -1;

    private final CurrencyListAdapter<CurrencyModel> currencyListAdapter = new CurrencyListAdapter<>();

    public CommanCallback<String> callback;

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

        binding = FragmentSelectCurrencyBottomSheetBinding.bind(v);

        binding.currencyTitle.setText(TranslationManager.shared.get("select_currency"));
        binding.updateBtn.setTxtTitle(TranslationManager.shared.get("update"));

        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        if (activity == null) {
            activity = requireActivity();
        }


        binding.currencyListRecycler.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        binding.currencyListRecycler.setAdapter(currencyListAdapter);
        if (AppSettingManager.shared.getAppSettingData() != null && !AppSettingManager.shared.getAppSettingData().getCurrencies().isEmpty()) {
            List<CurrencyModel> currency = AppSettingManager.shared.getAppSettingData().getCurrencies();
            String selectedCurrency = SessionManager.shared.getUser().getCurrency();
            if (!TextUtils.isEmpty(selectedCurrency)) {
                selectedSlotPosition = IntStream.range(0, currency.size()).filter(i -> selectedCurrency.equals(currency.get(i).getCurrency())).findFirst().orElse(-1);
            }
            currencyListAdapter.updateData(currency);
        }


    }

    private void setListener() {

        binding.currencyConstraint.setOnClickListener(v -> {
            Utils.preventDoubleClick(v);
            if (selectedSlotPosition == -1) return;
            if (currencyListAdapter.getData() != null && !currencyListAdapter.getData().isEmpty()) {
                String currency = currencyListAdapter.getData().get(selectedSlotPosition).getCurrency();
                if (callback != null) {
                    Graphics.showAlertDialogWithOkCancel(activity, Utils.getLangValue("confirm_currency"), Utils.setLangValue("currency_update_dialog",currency),
                            Utils.getLangValue("yes"), Utils.getLangValue("cancel"), isConfirmed -> {
                                if (isConfirmed) {
                                    callback.onReceive(currency);
                                    dismiss();
                                }
                            });
                }
            }

        });

    }

    public int getLayoutRes() {
        return R.layout.fragment_select_currency_bottom_sheet;
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


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------


    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    private class CurrencyListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_currency_select));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            ViewHolder viewHolder = (ViewHolder) holder;

            CurrencyModel model = (CurrencyModel) getItem(position);
            
            viewHolder.binding.viewLine1.setVisibility( position == getItemCount() - 1 ? View.GONE : View.VISIBLE);

            viewHolder.binding.tvCurrency.setText(model.getCurrency());

            if (model.getCurrency().equals("AED")) {
                SpannableString spannable = new SpannableString("D" + model.getRate());
                Typeface typeface = ResourcesCompat.getFont(activity, R.font.aed_regular);
                if (typeface != null) {
                    spannable.setSpan(new CustomTypefaceSpan(typeface), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                viewHolder.binding.tvCurrencyRate.setText(spannable);
            } else {
                viewHolder.binding.tvCurrencyRate.setText(model.getSymbol() + model.getRate());
            }


            Graphics.loadImageWithFirstLetter(model.getFlag(),viewHolder.binding.ivFlag,model.getCurrency());


            viewHolder.binding.btnSelectTimeSlot.setOnCheckedChangeListener(null);
            viewHolder.binding.btnSelectTimeSlot.setChecked(selectedSlotPosition == position);

            if (position == selectedSlotPosition) {
                viewHolder.binding.btnSelectTimeSlot.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(viewHolder.binding.btnSelectTimeSlot.getContext(), R.color.ticket_selected_colour)));
            } else {
                viewHolder.binding.btnSelectTimeSlot.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(viewHolder.binding.btnSelectTimeSlot.getContext(), R.color.white)));
            }

            viewHolder.itemView.setOnClickListener(v -> {
                Utils.preventDoubleClick(v);
                selectedSlotPosition = position;
                notifyDataSetChanged();
            });

            viewHolder.binding.btnSelectTimeSlot.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedSlotPosition = position;
                    notifyDataSetChanged();
                }
            });

        }


        public class ViewHolder extends RecyclerView.ViewHolder {

            private final ItemCurrencySelectBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemCurrencySelectBinding.bind(itemView);
            }
        }
    }

    // endregion
    // --------------------------------------

}
