package com.whosin.business.ui.activites.raynaTicket.BottomSheets;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.whosin.business.R;
import com.whosin.business.comman.DiffAdapter;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Graphics;
import com.whosin.business.comman.Utils;
import com.whosin.business.comman.interfaces.CommanCallback;
import com.whosin.business.comman.ui.UiUtils;
import com.whosin.business.databinding.FragmentSelectPaymentOptionBottmSheetBinding;
import com.whosin.business.databinding.ItemSelectPaymentOptionsBinding;
import com.whosin.business.service.manager.AppSettingManager;
import com.whosin.business.service.manager.SessionManager;
import com.whosin.business.service.manager.TranslationManager;
import com.whosin.business.service.models.rayna.PaymentSelectModel;
import com.whosin.business.ui.activites.Profile.UpdateProfileActivity;
import com.whosin.business.ui.activites.home.activity.BannerWebViewActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class SelectPaymentOptionBottomSheet extends DialogFragment {

    private FragmentSelectPaymentOptionBottmSheetBinding binding;

    private final PaymentListAdapter<PaymentSelectModel> paymentListAdapter = new PaymentListAdapter<>();

    public CommanCallback<Integer> callback;

    public Boolean isFromRaynaTicket = false;

    public double amount = 0;



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

        binding = FragmentSelectPaymentOptionBottmSheetBinding.bind(v);

        binding.tvSelectPaymentTitle.setText(Utils.getLangValue("select_payment_options"));


        binding.paymentListRecycleView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));


        List<PaymentSelectModel> paymentSelectModelList = new ArrayList<>();

        paymentSelectModelList.add(new PaymentSelectModel(1,R.drawable.icon_credit_card,Utils.getLangValue("pay_with_card"),Utils.getLangValue("pay_with_credit_or_debit_card"),true));
//        paymentSelectModelList.add(new PaymentSelectModel(2,R.drawable.icon_link_pay,Utils.getLangValue("pay_with_link"),Utils.getLangValue("secure_payment_with_link"),false));

//        paymentSelectModelList.add(new PaymentSelectModel(6,R.drawable.ngenius_icon,Utils.getLangValue("pay_with_ngenius"),Utils.getLangValue("secure_payment_ngenius_gateway"),false));

        if (AppSettingManager.shared.getAppSettingData() != null && AppSettingManager.shared.getAppSettingData().isAllowTabbyPayments()){
            paymentSelectModelList.add(new PaymentSelectModel(3,R.drawable.icon_tabby_pay,Utils.getLangValue("tabby"),Utils.getLangValue("pay_in_4_no_interest_no_fees"),false));
        }

        paymentSelectModelList.add(new PaymentSelectModel(4,R.drawable.icon_google_pay,Utils.getLangValue("google_pay"),Utils.getLangValue("pay_with_google_pay"),false));
//        paymentSelectModelList.add(new PaymentSelectModel(5,R.drawable.icon_samsung_pay,"Samsung pay","Pay with samsung pay",false));


        binding.paymentListRecycleView.setAdapter(paymentListAdapter);
        paymentListAdapter.updateData(paymentSelectModelList);

        changeBtnTitleAndImage();
    }

    private void setListener() {


        binding.iconClose.setOnClickListener(view -> {
            Utils.preventDoubleClick(view);
            dismiss();
        });


        binding.paymentButton.setOnClickListener(v -> {
            Utils.preventDoubleClick(v);
            Optional<PaymentSelectModel> model = paymentListAdapter.getData().stream().filter(PaymentSelectModel::isSelect).findFirst();
            if (model.isPresent()){
                if (callback != null){
                    if (model.get().getId() == 3 && !isFromRaynaTicket) {
                       String email = SessionManager.shared.getUser().getEmail();
                       String phone_number = SessionManager.shared.getUser().getPhone();
                       if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(phone_number)){
                           callback.onReceive(model.get().getId());
                           dismiss();
                       } else {
                           Graphics.showAlertDialogWithOkCancel(requireActivity(), getString(R.string.app_name),
                                   Utils.getLangValue("email_and_phone_required_for_tabby_payment"),
                                   Utils.getLangValue("complete_profile"), Utils.getLangValue("cancel"), isConfirmed -> {
                                       if (isConfirmed) {
                                           startActivity(new Intent(requireActivity(), UpdateProfileActivity.class));
                                           dismiss();
                                       } else {
                                           dismiss();
                                       }
                                   });

                       }
                    } else {
                        callback.onReceive(model.get().getId());
                        dismiss();
                    }


                }
            }
        });

    }

    private int getLayoutRes() {
        return R.layout.fragment_select_payment_option_bottm_sheet;
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


    private void changeBtnTitleAndImage(){

        Optional<PaymentSelectModel> model = paymentListAdapter.getData().stream().filter(PaymentSelectModel::isSelect).findFirst();
        if (model.isPresent()){
           binding.ivPaymentIcon.setImageResource(model.get().getImage());
           binding.tvPaymentTitle.setText(model.get().getTitle());
//           if (model.get().getId() == 2){
//               Glide.with(this).load(R.drawable.icon_white_link_pay).into(binding.ivPaymentIcon);
//
////               binding.ivPaymentIcon.setImageResource(R.drawable.icon_white_link_pay);
//               binding.tvPaymentTitle.setText("Pay via Link");
//           }

            if (model.get().getId() == 3){
                Glide.with(this).load(R.drawable.icon_white_tabby).into(binding.ivPaymentIcon);
//                binding.ivPaymentIcon.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.icon_white_tabby));
                binding.tvPaymentTitle.setText(Utils.getLangValue("pay_with_tabby"));
            }
            if (model.get().getId() == 4){
                Glide.with(this).load(R.drawable.icon_google_pay_white).into(binding.ivPaymentIcon);
//                binding.ivPaymentIcon.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.icon_white_tabby));
                binding.tvPaymentTitle.setText(Utils.getLangValue("pay_with_google_pay"));
            }
//            if (model.get().getId() == 5){
//                Glide.with(this).load(R.drawable.icon_samsung_pay).into(binding.ivPaymentIcon);
////                binding.ivPaymentIcon.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.icon_white_tabby));
//                binding.tvPaymentTitle.setText("Pay with Samsung pay");
//            }
        }

    }

    private String setValue(String key, String value) {
        String template = TranslationManager.shared.get(key);
        if (template == null) return "";
        return template.replaceAll("\\{.*?\\}", value);
    }


    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    private class PaymentListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_select_payment_options));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;

            PaymentSelectModel model = (PaymentSelectModel) getItem(position);

            viewHolder.mBinding.tvTitle.setText(model.getTitle());

            viewHolder.mBinding.tvDescription.setText(model.getDescription());

            viewHolder.mBinding.ivPayment.setImageDrawable(ContextCompat.getDrawable(requireContext(), model.getImage()));

            if (model.getId() == 3){
                boolean isEnabled = amount >= 10;
                viewHolder.mBinding.btnSelectPayment.setEnabled(isEnabled);
                viewHolder.mBinding.getRoot().setEnabled(isEnabled);

                float alphaValue = isEnabled ? 1.0f : 0.7f;
                viewHolder.mBinding.btnSelectPayment.setAlpha(alphaValue);
                viewHolder.mBinding.getRoot().setAlpha(alphaValue);


                viewHolder.mBinding.tvMinimumAmout.setVisibility(isEnabled ? View.GONE : View.VISIBLE);
                viewHolder.mBinding.tvLearnMore.setVisibility(View.VISIBLE);

                String selectedCurrency = SessionManager.shared.getUser().getCurrency();
                if (TextUtils.isEmpty(selectedCurrency)){
                    selectedCurrency = "AED";
                }
                viewHolder.mBinding.tvLearnMore.setText(setValue("minimum_amount_is",selectedCurrency));

                SpannableString content = new SpannableString(Utils.getLangValue("learn_more"));
                content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                viewHolder.mBinding.tvLearnMore.setText(content);

            }else {
                viewHolder.mBinding.tvMinimumAmout.setVisibility(View.GONE);
                viewHolder.mBinding.tvLearnMore.setVisibility(View.GONE);
            }

            viewHolder.mBinding.btnSelectPayment.setOnCheckedChangeListener(null);
            viewHolder.mBinding.btnSelectPayment.setChecked(model.isSelect());


            if (model.isSelect()) {
                viewHolder.mBinding.btnSelectPayment.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(viewHolder.mBinding.btnSelectPayment.getContext(), R.color.brand_pink)));
            } else {
                viewHolder.mBinding.btnSelectPayment.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(viewHolder.mBinding.btnSelectPayment.getContext(), R.color.white)));
            }

            viewHolder.mBinding.btnSelectPayment.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    for (int i = 0; i < getItemCount(); i++) {
                        PaymentSelectModel item = (PaymentSelectModel) getItem(i);
                        item.setSelect(i == position);
                    }
                    notifyDataSetChanged();
                    changeBtnTitleAndImage();
                }
            });


            viewHolder.mBinding.tvLearnMore.setOnClickListener(v -> {
                String lang = Utils.getLang();
                String langCode = "en";

                if ("ar".equalsIgnoreCase(lang)) {
                    langCode = "ar";
                }

                String baseUrl = "https://checkout.tabby.ai/promos/product-page/installments/" + langCode + "/";

//                String baseUrl = "https://checkout.tabby.ai/promos/product-page/installments/en/";
                String price = String.valueOf(amount); // amount should be a valid float/double/string
                String currency = "AED";
                String merchantCode = "WMARE";
                String publicKey = "pk_test_0195a8d6-d236-5bb9-c1b8-c397c0ae1dcd";

                Uri uri = Uri.parse(baseUrl).buildUpon()
                        .appendQueryParameter("price", price)
                        .appendQueryParameter("currency", currency)
                        .appendQueryParameter("merchant_code", merchantCode)
                        .appendQueryParameter("public_key", publicKey)
                        .build();

                startActivity(new Intent(requireActivity(), BannerWebViewActivity.class).putExtra("link",uri.toString()));
            });

            viewHolder.mBinding.getRoot().setOnClickListener(v -> {
                for (int i = 0; i < getItemCount(); i++) {
                    PaymentSelectModel item = (PaymentSelectModel) getItem(i);
                    item.setSelect(i == position);
                }
                notifyDataSetChanged();
                changeBtnTitleAndImage();
            });

        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final ItemSelectPaymentOptionsBinding mBinding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemSelectPaymentOptionsBinding.bind(itemView);
            }
        }
    }

    // endregion
    // --------------------------------------


}