package com.whosin.app.ui.activites.home.activity;

import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;
import com.whosin.app.R;
import com.whosin.app.comman.Utils;
import com.whosin.app.databinding.ActivityBuyPackagePlanBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.MemberShipModel;
import com.whosin.app.service.models.PaymentCredentialModel;
import com.whosin.app.service.models.PromoCodeModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.comman.BaseActivity;
import com.whosin.app.ui.fragment.PackagePlan.PlanDetailFragment;
import com.whosin.app.ui.fragment.home.MembershipInfoBottomSheet;

import java.util.HashMap;
import java.util.Map;

public class BuyPackagePlanActivity extends BaseActivity {

    private ActivityBuyPackagePlanBinding binding;
    private MemberShipModel memberShipModel;
    private PaymentSheet paymentSheet;

    private String finalAmount="";

    private String promoCode = "";

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    protected void initUi() {

        applyTranslations();

        String model = getIntent().getStringExtra("membershipModel");
        if (model != null && !model.isEmpty()) {
            memberShipModel = new Gson().fromJson(model, MemberShipModel.class);
            setDetail(memberShipModel);
        }

        paymentSheet = new PaymentSheet(BuyPackagePlanActivity.this, paymentSheetResult -> {
            if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
                Log.d("TAG", "Canceled");
            } else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
                Log.e("TAG", "Got error: ", ((PaymentSheetResult.Failed) paymentSheetResult).getError());
            } else if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
                PlanDetailFragment dialog = new PlanDetailFragment();
                dialog.memberShipModel = memberShipModel;
                dialog.callBack = data -> {
                    Intent intent = new Intent();
                    intent.putExtra("close",true);
                    setResult(RESULT_OK, intent);
                    finish();
                };
                dialog.show( getSupportFragmentManager(), "PlanDetailFragment" );
            }
        });
    }

    @Override
    protected void setListeners() {
        binding.ivClose.setOnClickListener(view -> onBackPressed());

        binding.layoutInfo.setOnClickListener(v -> {
            Utils.preventDoubleClick( v );
            MembershipInfoBottomSheet membershipInfoBottomSheet = new MembershipInfoBottomSheet();
            membershipInfoBottomSheet.model = memberShipModel;
            membershipInfoBottomSheet.show(getSupportFragmentManager(), "1");
        });

        binding.checkoutButton.setOnClickListener(v -> {
            if (memberShipModel == null) {
                return;
            }
            requestPurchaseMembership();
        });

        binding.edtPromoCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    binding.layout.setBackgroundResource(R.drawable.focus_promo_edittext_bg);
                } else {
                    binding.layout.setBackgroundResource(R.drawable.promo_edittext_bg);
                }
            }
        });

        binding.edtPromoCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.tvValidationError.setVisibility(View.GONE);
                binding.imgValidation.setImageResource(R.drawable.complete_icon_unselected);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().isEmpty()) {
                    binding.imgValidation.setImageResource(R.drawable.complete_icon_unselected);
                    binding.tvValidationError.setVisibility(View.GONE);
                }
            }
        });

        binding.edtPromoCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (!binding.edtPromoCode.getText().toString().isEmpty()) {
                        requestSubscriptionPromoCodeValidation(binding.edtPromoCode.getText().toString());
                    }
                    else {
                        binding.tvValidationError.setText(getValue("please_enter_promo_code"));
                        binding.tvValidationError.setVisibility(View.VISIBLE);
                        binding.layoutPromoDetails.setVisibility(View.GONE);
                    }
                    return true;
                }
                return false;
            }
        });
    }



    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityBuyPackagePlanBinding.inflate( getLayoutInflater() );
        return binding.getRoot();
    }



    @Override
    protected Map<View, String> getTranslationMap() {
        Map<View, String> map = new HashMap<>();
        map.put(binding.txtTitle, "subscription_plan");
        map.put(binding.tvValidityTitle, "validity");
        map.put(binding.tvValidationError, "invalid_promo");
        map.put(binding.edtPromoCode, "enter_promo_code");
        map.put(binding.tvDiscountTitle, "discount");
        map.put(binding.tvTotal, "total_amount");
        map.put(binding.tvCheckOutTitle, "checkout");
        map.put(binding.tvInfo, "terms_condition_applies");

        return map;
    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void setDetail(MemberShipModel model) {
        if (model != null ) {
            finalAmount = String.valueOf(model.getActualPrice());
            binding.tvPackageTitle.setText(model.getTitle());
            binding.tvSubTitle.setText(model.getSubTitle());
            binding.tvValidity.setText(model.getTime());
            binding.tvPrice.setText(model.getActualPrice() + " AED");
            binding.tvTotalPrice.setText(model.getActualPrice() + " AED");
            binding.tvDiscount.setText(model.getDiscountText());
        }
    }

    private void startStripeCheckOut(PaymentCredentialModel model) {
        if (model.publishableKey == null || model.clientSecret == null) {
            Toast.makeText(activity, "Invalid payment configuration", Toast.LENGTH_SHORT).show();
            return;
        }
        PaymentConfiguration.init(activity, model.publishableKey);
        final PaymentSheet.Configuration configuration = new PaymentSheet.Configuration.Builder("Whosin, Inc.").allowsDelayedPaymentMethods(true).build();
        paymentSheet.presentWithPaymentIntent(model.clientSecret, configuration);
    }

    private void showProgressBar() {
        binding.progressView.setVisibility(View.VISIBLE);
        binding.imgValidation.setVisibility(View.GONE);
    }

    private void hideProgressBar() {
        binding.progressView.setVisibility(View.GONE);
        binding.imgValidation.setVisibility(View.VISIBLE);
        Utils.hideKeyboard(activity);
        binding.layout.setBackgroundResource(R.drawable.promo_edittext_bg);
        binding.layout.clearFocus();
    }

    private void setPromoDetail(PromoCodeModel data) {
        binding.tvValidationError.setVisibility(View.GONE);
        binding.imgValidation.setImageResource(R.drawable.complete_icon);
        binding.layoutPromoDetails.setVisibility(View.VISIBLE);
        binding.tvPromoDiscountAmount.setText(data.getDiscountAmount()+" AED");
        binding.tvDiscountAmount.setText(data.getFinalAmount()+" AED");
        String modifiedString = String.valueOf(data.getPromoCodeInfo().getDiscountPercentage()).contains( "%" ) ? String.valueOf(data.getPromoCodeInfo().getDiscountPercentage()) :data.getPromoCodeInfo().getDiscountPercentage() + "%";
        binding.tvPromoDiscount.setText(Utils.notNullString( modifiedString ));
        binding.tvTotalPrice.setText(data.getFinalAmount() + " AED");
        finalAmount = String.valueOf(data.getFinalAmount());
        promoCode = data.getPromoCodeInfo().getPromoCode();
    }

    private void handleErrorData(String error) {
        binding.tvValidationError.setVisibility(View.VISIBLE);
        binding.tvValidationError.setText(error);
        binding.imgValidation.setImageResource(R.drawable.icon_wrong_promocode);
        binding.layoutPromoDetails.setVisibility(View.GONE);
    }

    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestPurchaseMembership() {
        showProgress();
        DataService.shared(activity).requestPurchaseMembership(memberShipModel.getId(),finalAmount,promoCode,"AED", new RestCallback<ContainerModel<PaymentCredentialModel>>(this) {
            @Override
            public void result(ContainerModel<PaymentCredentialModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (model.getData() != null) {
                    PlanDetailFragment dialog = new PlanDetailFragment();
                    dialog.memberShipModel = memberShipModel;
                    dialog.callBack = data -> {
                        Intent intent = new Intent();
                        intent.putExtra("close",true);
                        setResult(RESULT_OK, intent);
                        finish();
                    };
                    dialog.show( getSupportFragmentManager(), "PlanDetailFragment" );
                    //startStripeCheckOut(model.getData());
                }
            }
        });
    }

    private void requestSubscriptionPromoCodeValidation(String promoCode) {
        showProgressBar();
        DataService.shared(activity).requestSubscriptionPromoCodeValidation(promoCode,String.valueOf(memberShipModel.getActualPrice()),"membershipPackage",memberShipModel.getId(),"false", new RestCallback<ContainerModel<PromoCodeModel>>(this) {
            @Override
            public void result(ContainerModel<PromoCodeModel> model, String error) {
                hideProgressBar();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    handleErrorData(error);
                    return;
                }

                if (model.getData() != null) {
                    setPromoDetail(model.getData());
                }
            }
        });
    }




    // endregion
    // --------------------------------------

}