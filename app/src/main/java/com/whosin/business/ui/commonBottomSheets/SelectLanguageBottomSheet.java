package com.whosin.business.ui.commonBottomSheets;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.whosin.business.R;
import com.whosin.business.comman.DiffAdapter;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Graphics;
import com.whosin.business.comman.Utils;
import com.whosin.business.comman.interfaces.CommanCallback;
import com.whosin.business.comman.ui.UiUtils;
import com.whosin.business.databinding.FragmentSelectLanguageBottomSheetBinding;
import com.whosin.business.databinding.ItemLangSelectBinding;
import com.whosin.business.service.manager.AppSettingManager;
import com.whosin.business.service.manager.TranslationManager;
import com.whosin.business.service.models.LanguagesModel;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

public class SelectLanguageBottomSheet extends DialogFragment {

    private FragmentSelectLanguageBottomSheetBinding binding;

    private Activity activity;

    private int selectedSlotPosition = -1;

    private final CurrencyListAdapter<LanguagesModel> currencyListAdapter = new CurrencyListAdapter<>();

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

        binding = FragmentSelectLanguageBottomSheetBinding.bind(v);


        binding.languageTitle.setText(TranslationManager.shared.get("select_language"));
        binding.updateBtn.setTxtTitle(TranslationManager.shared.get("update"));

        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        if (activity == null) {
            activity = requireActivity();
        }

        binding.langListRecycler.setLayoutManager(new LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false));
        binding.langListRecycler.setAdapter(currencyListAdapter);
        if (AppSettingManager.shared.getAppSettingData() != null && !AppSettingManager.shared.getAppSettingData().getLanguages().isEmpty()){
            List<LanguagesModel> languages = AppSettingManager.shared.getAppSettingData().getLanguages();
            String langCode = Utils.getLang();
            if (!TextUtils.isEmpty(langCode)){
                selectedSlotPosition = IntStream.range(0, languages.size()).filter(i -> languages.get(i).getCode().equals(langCode)).findFirst().orElse(-1);
            }
            currencyListAdapter.updateData(languages);
        }


    }

    private void setListener() {

        binding.currencyConstraint.setOnClickListener(v -> {
            Utils.preventDoubleClick(v);
            if (selectedSlotPosition == -1) return;
            if (currencyListAdapter.getData() != null && !currencyListAdapter.getData().isEmpty()){
                String langCode = currencyListAdapter.getData().get(selectedSlotPosition).getCode();
                String langName = currencyListAdapter.getData().get(selectedSlotPosition).getName();
//                if (callback != null){
//                    callback.onReceive(langCode);
//                    dismiss();
//                }
                if (callback != null) {
                    Graphics.showAlertDialogWithOkCancel(activity, Utils.getLangValue("confirm_language"), Utils.setLangValue("language_update_dialog",langName),
                            Utils.getLangValue("yes"), Utils.getLangValue("cancel"), isConfirmed -> {
                                if (isConfirmed) {
                                    callback.onReceive(langCode);
                                    dismiss();
                                }
                            });
                }
            }
        });

    }

    public int getLayoutRes() {
        return R.layout.fragment_select_language_bottom_sheet;
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
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_lang_select));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            ViewHolder viewHolder = (ViewHolder) holder;

            LanguagesModel model = (LanguagesModel) getItem(position);

            viewHolder.binding.viewLine1.setVisibility( position == getItemCount() - 1 ? View.GONE : View.VISIBLE);

            viewHolder.binding.tvLangName.setText(model.getName());

            Graphics.loadImageWithFirstLetter(model.getFlag(),viewHolder.binding.ivLangFlag,model.getName());

            String nativeName = model.getNative_name();
            String code = model.getCode();

            if (!TextUtils.isEmpty(nativeName) || !TextUtils.isEmpty(code)) {
                StringBuilder nameBuilder = new StringBuilder("(");
                if (!TextUtils.isEmpty(nativeName)) {
                    nameBuilder.append(nativeName);
                    if (!TextUtils.isEmpty(code)) {
                        nameBuilder.append(" - ");
                    }
                }
                if (!TextUtils.isEmpty(code)) {
                    nameBuilder.append(code);
                }
                nameBuilder.append(")");

                viewHolder.binding.tvLangNativeName.setText(nameBuilder.toString());
                viewHolder.binding.tvLangNativeName.setVisibility(View.VISIBLE);
            } else {
                viewHolder.binding.tvLangNativeName.setVisibility(View.GONE);
            }



            viewHolder.binding.btnSelectLang.setOnCheckedChangeListener(null);
            viewHolder.binding.btnSelectLang.setChecked(selectedSlotPosition == position);

            if (position == selectedSlotPosition) {
                viewHolder.binding.btnSelectLang.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(viewHolder.binding.btnSelectLang.getContext(), R.color.ticket_selected_colour)));
            } else {
                viewHolder.binding.btnSelectLang.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(viewHolder.binding.btnSelectLang.getContext(), R.color.white)));
            }

            viewHolder.itemView.setOnClickListener(v -> {
                Utils.preventDoubleClick(v);
                selectedSlotPosition = position;
                notifyDataSetChanged();
            });

            viewHolder.binding.btnSelectLang.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedSlotPosition = position;
                    notifyDataSetChanged();
                }
            });

        }


        public class ViewHolder extends RecyclerView.ViewHolder {

            private final ItemLangSelectBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemLangSelectBinding.bind(itemView);
            }
        }
    }

    // endregion
    // --------------------------------------

}