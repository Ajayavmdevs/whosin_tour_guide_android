package com.whosin.app.ui.activites.venue;

import android.content.Intent;
import android.graphics.Paint;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;
import com.whosin.app.R;
import com.whosin.app.comman.AppConstants;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.ActivityVenueBuyNowBinding;
import com.whosin.app.databinding.VenueBuyItemBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.AppSettingManager;
import com.whosin.app.service.manager.RaynaTicketManager;
import com.whosin.app.service.models.CartModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.OffersModel;
import com.whosin.app.service.models.PackageModel;
import com.whosin.app.service.models.PaymentCredentialModel;
import com.whosin.app.service.models.VenueMetaDataPromoCodeModel;
import com.whosin.app.service.models.VenueObjectModel;
import com.whosin.app.service.models.VenuePromoCodeModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.comman.BaseActivity;
import com.whosin.app.ui.activites.offers.DisclaimerBottomSheet;
import com.whosin.app.ui.activites.raynaTicket.BottomSheets.SelectPaymentOptionBottomSheet;
import com.whosin.app.ui.activites.raynaTicket.BottomSheets.TabbyPaymentBottomSheet;
import com.whosin.app.ui.activites.venue.ui.MyCartActivity;
import com.whosin.app.ui.activites.wallet.WalletActivity;
import com.whosin.app.ui.fragment.wallet.PurchaseSuccessFragment;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class VenueBuyNowActivity extends BaseActivity {
    private ActivityVenueBuyNowBinding binding;
    private final VenuePackageAdapter<PackageModel> venuePackageAdapter = new VenuePackageAdapter<>();
    private PaymentSheet paymentSheet;
    private OffersModel offersModel;
    private VenueObjectModel venueObjectModel;
    private boolean isShowBtn = false;
    private boolean isPromoCodeApply = false;
    private VenuePromoCodeModel promoCodeModel = null;



    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    protected void initUi() {

        applyTranslations();

        Utils.changeStatusBarColor(getWindow(), getResources().getColor(R.color.buy_screen_header));

        List<CartModel> cartListItem = CartModel.getCartHistory();
        int cartSize = cartListItem.size();
        binding.cartItemLayout.setVisibility(cartSize > 0 ? View.VISIBLE : View.GONE);
        binding.tvCartItem.setText(String.valueOf(cartSize));
        binding.btnApplyPromoCode.setVisibility(View.GONE);

        String model = Utils.notNullString(getIntent().getStringExtra("offerModel"));
        String venueModel = Utils.notNullString(getIntent().getStringExtra("venueObjectModel"));
        offersModel = new Gson().fromJson(model, OffersModel.class);
        venueObjectModel = new Gson().fromJson(venueModel, VenueObjectModel.class);

        setDetail();

        paymentSheet = new PaymentSheet(VenueBuyNowActivity.this, paymentSheetResult -> {
            if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
                Log.d("TAG", "Canceled");
            } else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
                Log.e("TAG", "Got error: ", ((PaymentSheetResult.Failed) paymentSheetResult).getError());
            } else if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
                AppSettingManager.shared.tmpCartList.clear();
                PurchaseSuccessFragment purchaseSuccessFragment = new PurchaseSuccessFragment();
                purchaseSuccessFragment.callBack = data -> {
                    finish();
                    if (data) {
                        startActivity(new Intent(Graphics.context, WalletActivity.class));
                    }
                };
                purchaseSuccessFragment.show(getSupportFragmentManager(), "");
            }
        });



    }

    @Override
    protected void setListeners() {

        binding.btnApplyPromoCode.setOnClickListener(view -> {
            Utils.preventDoubleClick(view);
            if (binding.btnApplyPromoCode.getText().toString().equals("Remove")){
                resetPromoCodeLayout();
                venuePackageAdapter.notifyDataSetChanged();
                return;
            }
            if (TextUtils.isEmpty(binding.edtPromoCode.getText().toString())){
                Toast.makeText(this, getValue("please_enter_promo_code"), Toast.LENGTH_SHORT).show();
                return;
            }

            if (AppSettingManager.shared.tmpCartList.stream().allMatch(p -> p.qty == 0)) {
                Toast.makeText(this, getValue("select_item_before_applying_promo_code"), Toast.LENGTH_SHORT).show();
                return;
            }

            requestVenuePromoCode();
        });

        binding.edtPromoCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (isShowBtn){
                    binding.imgValidation.setVisibility(View.GONE);
                    binding.tvValidationError.setVisibility(View.GONE);
                    binding.btnApplyPromoCode.setVisibility(View.VISIBLE);
                    binding.layout.setBackground(ContextCompat.getDrawable(activity,R.drawable.promo_stroke_bg));
                }

                binding.btnApplyPromoCode.setVisibility(charSequence.length() == 0 ? View.GONE : View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        binding.YourSavingLayout.setOnClickListener(view -> {
            Utils.preventDoubleClick(view);
            if (binding.layoutPromoAndDiscount.getVisibility() == View.GONE){
                binding.layoutPromoAndDiscount.setVisibility(View.VISIBLE);
                binding.ivDropDown.setRotation(90f);
            }else {
                binding.layoutPromoAndDiscount.setVisibility(View.GONE);
                binding.ivDropDown.setRotation(270f);
            }
        });

        binding.tvTermsCondition.setOnClickListener(v -> {
            if (!offersModel.getDisclaimerTitle().isEmpty()) {
                DisclaimerBottomSheet disclaimerBottomSheet = new DisclaimerBottomSheet();
                disclaimerBottomSheet.disclaimerTitle = offersModel.getDisclaimerTitle();
                disclaimerBottomSheet.disclaimerDescription = offersModel.getDisclaimerDescription();
                disclaimerBottomSheet.show(getSupportFragmentManager(), "DisclaimerBottomSheet");
            }
        });

        binding.ivClose.setOnClickListener(view -> {
            onBackPressed();
            AppSettingManager.shared.tmpCartList.clear();
        });

        binding.imageMyCart.setOnClickListener(view -> startActivity(new Intent(activity, MyCartActivity.class)));

        binding.addToCartButton.setOnClickListener(view -> {
            List<CartModel> items = AppSettingManager.shared.tmpCartList.stream().filter(p -> p.qty > 0).collect(Collectors.toList());
            if (!items.isEmpty()) {
                items.forEach(p -> CartModel.addToCart(p.id, p.type, p.packageModel, offersModel, p.venueModel, p.qty, "", "", null, p.maxQty, p.getDiscountAmount()));
                Intent intent = new Intent(activity, MyCartActivity.class);
                activityLauncher.launch( intent, result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        boolean isClose = result.getData().getBooleanExtra("close",false);
                        if (isClose) {
                            AppSettingManager.shared.tmpCartList.clear();
                            finish();
                        }
                    }
                } );
            } else {
                Toast.makeText(this,getValue("please_select_quantity_add_to_cart"), Toast.LENGTH_SHORT).show();

            }

        });

        binding.checkoutButton.setOnClickListener(view -> {
            if (AppSettingManager.shared.tmpCartList.isEmpty()) {
                Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), getValue("select_package"));
                return;
            }
            if (AppSettingManager.shared.tmpCartList.stream().allMatch(p -> p.qty == 0)) {
                Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), getValue("select_package"));
                return;
            }

            List<CartModel> items = AppSettingManager.shared.tmpCartList.stream().filter(p -> p.qty > 0).collect(Collectors.toList());
            if (items.isEmpty()) {
                Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), getValue("select_package"));
                return;
            }


            JsonArray metaDate = new JsonArray();
            AtomicReference<Double> totalAmount = new AtomicReference<>(0.0);
            items.forEach(model -> {
                String venueId = (model.venueModel != null && model.venueModel.getId() != null) ? model.venueModel.getId() : "";
                JsonObject itemMetaData = new JsonObject();
                itemMetaData.addProperty("type", model.type);
                itemMetaData.addProperty("offerId", model.packageModel.getOfferId());
                itemMetaData.addProperty("packageId", model.id);
                itemMetaData.addProperty("price", Double.parseDouble(String.valueOf(model.getDiscountAmount())) * model.qty);
                itemMetaData.addProperty("qty", model.qty);
                itemMetaData.addProperty("venueId", venueId);
                double newAmount = 0;
                if (isPromoCodeApply){
                    newAmount   = totalAmount.get() + (Double.parseDouble(String.valueOf(model.getPackageModel().getAmount())) * model.qty);
                }else {
                    newAmount   = totalAmount.get() + (Double.parseDouble(String.valueOf(model.getDiscountAmount())) * model.qty);
                }
                totalAmount.set(newAmount);

                metaDate.add(itemMetaData);
            });

            JsonObject params = new JsonObject();
            if (!TextUtils.isEmpty(binding.edtPromoCode.getText().toString()) && isPromoCodeApply){
                params.addProperty("promoCode", binding.edtPromoCode.getText().toString().trim());
                params.addProperty("totalAmount", String.valueOf(totalAmount));
            }

            if (promoCodeModel != null) {
                double discountedAmount = totalAmount.get() - promoCodeModel.getTotalDiscount();
                totalAmount.set(discountedAmount);
                params.addProperty("discount", promoCodeModel.getTotalDiscount());
            }

            params.addProperty("amount", String.valueOf(totalAmount));
            params.addProperty("currency", "aed");
            params.add("metadata", metaDate);


            Log.d("MetaDat", "setListeners: " + params);

            SelectPaymentOptionBottomSheet bottmSheet = new SelectPaymentOptionBottomSheet();
            bottmSheet.amount = totalAmount.get();
            bottmSheet.callback = data -> requestStripeToken(params,data);
            bottmSheet.show(getSupportFragmentManager(), "");
        });
    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityVenueBuyNowBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<CartModel> cartListItem = CartModel.getCartHistory();
        int cartSize = !cartListItem.isEmpty() ? cartListItem.size() : 0;
        binding.cartItemLayout.setVisibility(cartSize > 0 ? View.VISIBLE : View.GONE);
        binding.tvCartItem.setText(String.valueOf(cartSize));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AppSettingManager.shared.tmpCartList.clear();
    }

    @Override
    protected Map<View, String> getTranslationMap() {
        Map<View, String> map = new HashMap<>();
        map.put(binding.tvFromTitle, "and_from");
        map.put(binding.tvTillTitle, "and_till");
        map.put(binding.btnApplyPromoCode, "apply");
        map.put(binding.edtPromoCode, "enter_promo_code");
        map.put(binding.tvValidationError, "invalid_promo");
        map.put(binding.tvTotalSavingTitle, "your_total_savings");
        map.put(binding.tvPromoCodeTitle, "promo_code");
        map.put(binding.tvDiscountTitle, "discount");
        map.put(binding.tvCheckOutTitle, "checkOut");

        binding.addToCartButton.setTxtTitle(getValue("addToCart"));
        binding.tvTermsCondition.setText(Html.fromHtml(getValue("terms"), Html.FROM_HTML_MODE_LEGACY));

        return map;
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void setDetail() {
        binding.venueContainer.setVenueDetail(venueObjectModel);

        binding.packageRecycler.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        binding.packageRecycler.setAdapter(venuePackageAdapter);

        if (offersModel.getPackages() != null && !offersModel.getPackages().isEmpty()) {
            List<PackageModel> list = offersModel.getPackages().stream().filter(PackageModel::isAllowSale).collect(Collectors.toList());
            if (!list.isEmpty()) {
                venuePackageAdapter.updateData(list);
            } else {
                binding.packageRecycler.setVisibility(View.GONE);
            }
        } else {
            binding.packageRecycler.setVisibility(View.GONE);
        }

        binding.tvTermsCondition.setVisibility(!offersModel.getDisclaimerTitle().isEmpty() ? View.VISIBLE : View.GONE);

        if (!offersModel.getImage().isEmpty()) {
            Graphics.loadImage(offersModel.getImage(), binding.img);
        } else {
            binding.img.setVisibility(View.GONE);
            binding.cardView.setBackgroundColor(ContextCompat.getColor(this, R.color.lightGray));
        }

        binding.txtDays.setText(offersModel.getDays());

        if (TextUtils.isEmpty(offersModel.getStartTime())) {
            binding.startDate.setText(getValue("ongoing"));
            binding.tvFromTitle.setVisibility(View.GONE);
            binding.tillDateLayout.setVisibility(View.GONE);
        } else {
            binding.tvFromTitle.setVisibility(View.VISIBLE);
            binding.tillDateLayout.setVisibility(View.VISIBLE);
            binding.startDate.setText(Utils.convertMainDateFormat(offersModel.getStartTime()));
            binding.endDate.setText(Utils.convertMainDateFormat(offersModel.getEndTime()));
        }
        binding.txtOfferTime.setText(offersModel.getOfferTiming());

        binding.btnTimeInfo.setVisibility(offersModel.isShowTimeInfo() ? View.GONE : View.VISIBLE);
        binding.layoutTimeInfo.setOnClickListener(v -> {
            if (!offersModel.isShowTimeInfo()) {
                if (venueObjectModel == null) {
                    return;
                }
                VenueTimingDialog dialog = new VenueTimingDialog(venueObjectModel.getTiming(), activity);
                dialog.show(getSupportFragmentManager(), "1");
            }
        });

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

    private void startGooglePayCheckOut(PaymentCredentialModel model) {
        if (model.publishableKey == null || model.clientSecret == null) {
            Toast.makeText(activity, "Invalid payment configuration", Toast.LENGTH_SHORT).show();
            return;
        }
        PaymentConfiguration.init(activity, model.publishableKey);
        final PaymentSheet.GooglePayConfiguration googlePayConfiguration = new PaymentSheet.GooglePayConfiguration(AppConstants.GPAY_ENV, AppConstants.GPAY_REGION);
        final PaymentSheet.Configuration configuration = new PaymentSheet.Configuration.Builder("Whosin, Inc.").allowsDelayedPaymentMethods(true).googlePay(googlePayConfiguration).build();
        paymentSheet.presentWithPaymentIntent(model.clientSecret, configuration);
    }


    private void tabbyCheckOut(PaymentCredentialModel model) {
        TabbyPaymentBottomSheet sheet = new TabbyPaymentBottomSheet();
        sheet.paymentTabbyModel = model.getTabbyModel();
        sheet.callback = p -> {
            if (!TextUtils.isEmpty(p)) {
                switch (p) {
                    case "success":
                        PurchaseSuccessFragment purchaseSuccessFragment = new PurchaseSuccessFragment();
                        purchaseSuccessFragment.callBack = data -> {
                            finish();
                            if (data) {
                                startActivity(new Intent(Graphics.context, WalletActivity.class));
                            }
                        };
                        purchaseSuccessFragment.show(getSupportFragmentManager(), "");

                        break;
                    case "cancel":

                        break;
                    case "failure":

                        break;
                }
            }
        };
        sheet.show(getSupportFragmentManager(), "");
    }

    private JsonObject getJsonObject(){
        JsonObject object = new JsonObject();
        object.addProperty("promoCode",binding.edtPromoCode.getText().toString().trim());

        List<CartModel> items = AppSettingManager.shared.tmpCartList.stream().filter(p -> p.qty > 0).collect(Collectors.toList());

        JsonArray metaDate = new JsonArray();
        items.forEach(model -> {
            JsonObject itemMetaData = new JsonObject();
            itemMetaData.addProperty("type", "package");
            itemMetaData.addProperty("packageId", model.getPackageModel().getId());
            itemMetaData.addProperty("amount", model.getPackageModel().getAmount());
            if (Integer.parseInt(model.getPackageModel().getDiscount()) != 0) itemMetaData.addProperty("discount",model.packageModel.getDiscount());
            if (model.qty != 0) itemMetaData.addProperty("qty",model.qty);
            metaDate.add(itemMetaData);
        });
        object.add("metadata",metaDate);
        Log.d("JsonObject", "getJsonObject: " + object);
        return object;
    }

    private void updatePromoCodeUI(boolean isSuccess, String message) {
        isShowBtn = true;
        binding.imgValidation.setVisibility(View.VISIBLE);
        binding.imgValidation.setImageDrawable(ContextCompat.getDrawable(activity, isSuccess ? R.drawable.complete_icon : R.drawable.icon_wrong_promocode));
        binding.layout.setBackground(ContextCompat.getDrawable(activity, isSuccess ? R.drawable.promo_stroke_selected_bg : R.drawable.wrong_promo_code_bg));
        binding.tvValidationError.setVisibility(isSuccess ? View.GONE : View.VISIBLE);
        if (!isSuccess) {
            binding.tvValidationError.setText(message);
        }

        if (isSuccess){
            binding.btnApplyPromoCode.setVisibility(View.VISIBLE);
            binding.ivPromoCode.setImageDrawable(ContextCompat.getDrawable(activity,R.drawable.complete_icon));
            binding.btnApplyPromoCode.setText(getValue("remove"));
            binding.btnApplyPromoCode.setTextColor(ContextCompat.getColor(this,R.color.red));

            SpannableString styledPrice = Utils.getStyledText(activity,String.valueOf(promoCodeModel.getPromoDiscount()));
            SpannableStringBuilder fullText = new SpannableStringBuilder();
            fullText.append(styledPrice).append(getValue("saved_with")).append(binding.edtPromoCode.getText().toString());
            binding.tvApplyPromoCode.setText(fullText);

            binding.tvApplyPromoCode.setVisibility(View.VISIBLE);
            binding.imgValidation.setVisibility(View.GONE);
            binding.edtPromoCode.setVisibility(View.GONE);
            binding.layoutPromoCodeApply.setVisibility(View.VISIBLE);


            Utils.setStyledText(activity,binding.tvPromoCode,String.valueOf(promoCodeModel.getPromoDiscount()));
            Utils.setStyledText(activity,binding.tvYourSaving,String.valueOf(promoCodeModel.getTotalDiscount()));
            Utils.setStyledText(activity,binding.tvTotalDiscount,String.valueOf(promoCodeModel.getItemsDiscount()));
        }
    }

    private void resetPromoCodeLayout(){
        isPromoCodeApply = false;
        binding.imgValidation.setVisibility(View.GONE);
        binding.layout.setBackground(ContextCompat.getDrawable(activity,R.drawable.promo_stroke_bg));
        binding.btnApplyPromoCode.setVisibility(View.VISIBLE);
        binding.tvValidationError.setVisibility(View.GONE);
        binding.edtPromoCode.setText("");
        binding.ivPromoCode.setImageDrawable(ContextCompat.getDrawable(activity,R.drawable.icon_promo_code));
        binding.tvApplyPromoCode.setVisibility(View.GONE);
        binding.edtPromoCode.setVisibility(View.VISIBLE);
        binding.btnApplyPromoCode.setText(getValue("apply"));
        binding.btnApplyPromoCode.setTextColor(ContextCompat.getColor(this,R.color.brand_pink));
        binding.btnApplyPromoCode.setVisibility(View.GONE);
        binding.layoutPromoCodeApply.setVisibility(View.GONE);
        binding.ivDropDown.setRotation(90f);
        resetCalcaution();
    }

    private void resetCalcaution(){
        Float totalAmount = AppSettingManager.shared.tmpCartList.stream().map(p -> p.qty * Float.parseFloat(String.valueOf(p.getDiscountAmount()))).reduce(0f, Float::sum);

        Float totalAed = AppSettingManager.shared.tmpCartList.stream().map(p -> p.qty * Float.parseFloat(String.valueOf(p.packageModel.getAmount()))).reduce(0f, Float::sum);

        Float savingAed = totalAed - totalAmount;

        binding.tvYourSaving.setText("AED " + savingAed);
        binding.YourSavingLayout.setVisibility(savingAed > 0 ? View.VISIBLE : View.GONE);

        binding.tvTotalDiscount.setText("AED " + savingAed);

        binding.tvTotalPrice.setVisibility(totalAmount > 0 ? View.VISIBLE : View.GONE);

        Utils.setStyledText(activity,binding.tvTotalPrice,String.valueOf(totalAmount));

        int qtySum = AppSettingManager.shared.tmpCartList.stream()
                .mapToInt(p -> p.qty)
                .sum();

        binding.btnApplyPromoCode.setVisibility(qtySum > 0 ? View.VISIBLE : View.GONE);
    }

    private void updateAdapterDataAfterPromoCode() {
        if (promoCodeModel != null) {
            venuePackageAdapter.notifyDataSetChanged();
        }
    }

    private VenueMetaDataPromoCodeModel getPromoCodeModel(String packageId) {
        if (promoCodeModel == null || promoCodeModel.getMetaData().isEmpty()) {
            return null;
        }
        return promoCodeModel.getMetaData().stream().filter(p -> p.getPackageId().equals(packageId)).findFirst().orElse(null);
    }



    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestStripeToken(JsonObject jsonObject,int paymentMod) {

        if (AppConstants.CARD_PAYMENT == paymentMod) {
            jsonObject.addProperty("paymentMethod", "stripe");
        } else if (AppConstants.PAY_WITH_LINK == paymentMod) {
            jsonObject.addProperty("paymentMethod", "stripe");
        }else if (AppConstants.GOOGLE_PAY == paymentMod || AppConstants.SAMSUNG_PAY == paymentMod) {
            jsonObject.addProperty("paymentMethod", "stripe");
        } else if (AppConstants.TABBY_PAYMENT == paymentMod) {
            jsonObject.addProperty("paymentMethod", "tabby");
        }

        showProgress();
        DataService.shared(activity).requestStripePaymentIntent(jsonObject, new RestCallback<ContainerModel<PaymentCredentialModel>>(this) {
            @Override
            public void result(ContainerModel<PaymentCredentialModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), error);
                    return;
                }
                if (Objects.equals(model.message, "Vip User Order Successfully Created!")) {
                    AppSettingManager.shared.tmpCartList.clear();
                    PurchaseSuccessFragment purchaseSuccessFragment = new PurchaseSuccessFragment();
                    purchaseSuccessFragment.callBack = data -> {
                        finish();
                        if (data) {
                            startActivity(new Intent(Graphics.context, WalletActivity.class));
                        }
                    };
                    purchaseSuccessFragment.show(getSupportFragmentManager(), "");
                }
                else if (model.getData() != null) {
                    if (AppConstants.CARD_PAYMENT == paymentMod) {
                        startStripeCheckOut(model.getData());
                    } else if (AppConstants.PAY_WITH_LINK == paymentMod) {
                        startStripeCheckOut(model.getData());
                    }else if (AppConstants.GOOGLE_PAY == paymentMod || AppConstants.SAMSUNG_PAY == paymentMod) {
                        startGooglePayCheckOut(model.getData());
                    } else if (AppConstants.TABBY_PAYMENT == paymentMod) {
                        if (Utils.isAvailableTabby(model.getData().getTabbyModel())) {
                            tabbyCheckOut(model.getData());
                        } else {
                            Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), getValue("tabby_payment_failed"));
                        }
                    }
                }

            }
        });
    }


    private void requestVenuePromoCode() {
        JsonObject object = getJsonObject();
        if (object.isEmpty() || object.isJsonNull()) return;
        binding.promoCodeProgressView.setVisibility(View.VISIBLE);
        binding.btnApplyPromoCode.setVisibility(View.GONE);
        DataService.shared(activity).requestVenuePromoCode(object, new RestCallback<ContainerModel<VenuePromoCodeModel>>(this) {
            @Override
            public void result(ContainerModel<VenuePromoCodeModel> model, String error) {
                binding.promoCodeProgressView.setVisibility(View.GONE);
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    promoCodeModel = null;
                    updatePromoCodeUI(false,error);
                    isPromoCodeApply = false;
                    return;
                }


                if (model.getData() != null){
                    isPromoCodeApply = true;
                    promoCodeModel = model.getData();
                    updatePromoCodeUI(true,"");

                    Float totalAmount = AppSettingManager.shared.tmpCartList.stream().map(p -> p.qty * Float.parseFloat(String.valueOf(p.getPackageModel().getAmount()))).reduce(0f, Float::sum);
                    binding.YourSavingLayout.setVisibility(model.getData().getTotalDiscount() > 0 ? View.VISIBLE : View.GONE);
//                    binding.tvYourSaving.setText("AED " + model.getData().getTotalDiscount());
                    Utils.setStyledText(activity,binding.tvYourSaving,String.valueOf(model.getData().getTotalDiscount()));

                    Float finalTotalAmount = totalAmount - (float) model.getData().getTotalDiscount();
                    binding.tvTotalPrice.setVisibility(finalTotalAmount > 0 ? View.VISIBLE : View.GONE);
//                    binding.tvTotalPrice.setText(String.valueOf(finalTotalAmount));
                    Utils.setStyledText(activity,binding.tvTotalPrice,String.valueOf(finalTotalAmount));

                    updateAdapterDataAfterPromoCode();

                }

            }
        });
    }




    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------


    private class VenuePackageAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.venue_buy_item));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            PackageModel model = (PackageModel) getItem(position);

            if (model != null) {

                if (TextUtils.isEmpty(model.getDescription())) {
                    viewHolder.mBinding.tvDescription.setVisibility(View.GONE);
                } else {
                    viewHolder.mBinding.tvDescription.setText(model.getDescription());
                }

                double discount = 0;

                if (isPromoCodeApply && promoCodeModel != null && getPromoCodeModel(model.getId()) != null && !promoCodeModel.getPromoDiscountType().equals("flat")){
                    VenueMetaDataPromoCodeModel tmpModel = getPromoCodeModel(model.getId());
                    discount = tmpModel.getFinalDiscountInPercent();
                }else {
                    discount = Double.parseDouble(model.getDiscount());
                }

                double amount = Integer.parseInt(model.getAmount());
                double value = discount * amount / 100;
                double discountPrice = amount - value;
                int roundedDiscountValue = (int) Math.round(discountPrice);



                if ("0".equals(model.getDiscount())) {
                    viewHolder.mBinding.tvDiscount.setVisibility(View.GONE);
                    int colorTransparent = viewHolder.itemView.getContext().getResources().getColor(R.color.transparent);
                    viewHolder.mBinding.roundLinear.setBackgroundColor(colorTransparent);
                } else {
                    viewHolder.mBinding.tvDiscount.setText((int) Math.round(discount) + "%");
                    viewHolder.mBinding.tvDiscount.setVisibility(View.VISIBLE);
                }


                if ("0".equals(model.getDiscount())) {
                    viewHolder.mBinding.tvOriginalPrice.setVisibility(View.GONE);
                    Utils.setStyledText(activity,viewHolder.mBinding.tvPrice,String.valueOf(model.getAmount()));
                } else {
                    if (amount == discountPrice) {
                        viewHolder.mBinding.tvOriginalPrice.setVisibility(View.GONE);
                        Utils.setStyledText(activity,viewHolder.mBinding.tvPrice,String.valueOf(model.getAmount()));
                    } else {
                        viewHolder.mBinding.tvOriginalPrice.setVisibility(View.VISIBLE);
                        Utils.setStyledText(activity,viewHolder.mBinding.tvOriginalPrice,String.valueOf(model.getAmount()));
                        if (isPromoCodeApply && !promoCodeModel.getPromoDiscountType().equals("flat") && getPromoCodeModel(model.getId()) != null){
                            VenueMetaDataPromoCodeModel tmpModel = getPromoCodeModel(model.getId());
                            if (tmpModel != null){
                                double tvPrice = tmpModel.getFinalAmount() / tmpModel.getQty();
                                String formattedPrice = String.format(Locale.ENGLISH, "%.2f", tvPrice);
                                Utils.setStyledText(activity,viewHolder.mBinding.tvPrice, formattedPrice);
                            }

                        }else {
                            Utils.setStyledText(activity,viewHolder.mBinding.tvPrice,String.valueOf(roundedDiscountValue));
                        }


                    }
                }

                if (discountPrice == 0) {
                    viewHolder.mBinding.addQuantity.setVisibility(View.GONE);
                    viewHolder.mBinding.roundLinear.setVisibility(View.GONE);
                } else {
                    viewHolder.mBinding.addQuantity.setVisibility(View.VISIBLE);
                    viewHolder.mBinding.roundLinear.setVisibility(View.VISIBLE);
                }

                viewHolder.mBinding.tvName.setText(model.getTitle());
                viewHolder.mBinding.tvOriginalPrice.setPaintFlags(viewHolder.mBinding.tvOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                viewHolder.mBinding.ivPlus.setOnClickListener(v -> updateQuantity(model, 1, viewHolder, roundedDiscountValue));
                viewHolder.mBinding.ivMinus.setOnClickListener(v -> updateQuantity(model, -1, viewHolder, roundedDiscountValue));

                viewHolder.mBinding.tvMaxQty.setText(model.isShowLeftQtyAlert() ? getValue("remaining_quantity") + model.getRemainingQty() : "");
                viewHolder.mBinding.tvMaxQty.setVisibility(model.isShowLeftQtyAlert() ? View.VISIBLE : View.GONE);
                viewHolder.mBinding.tvMaxQty.setTextColor(model.getRemainingQty() <= 3 ? getResources().getColor(R.color.red) : getResources().getColor(R.color.amber_color));

                if (model.getRemainingQty() == 0) {
                    viewHolder.mBinding.addQuantity.setVisibility(View.GONE);
                    viewHolder.mBinding.soldOutLayout.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.mBinding.addQuantity.setVisibility(View.VISIBLE);
                    viewHolder.mBinding.soldOutLayout.setVisibility(View.GONE);
                }

                if (promoCodeModel != null) {
                    if (isPromoCodeApply && !promoCodeModel.getPromoDiscountType().equals("flat") && getPromoCodeModel(model.getId()) != null) {
                        viewHolder.mBinding.tvAfterPromoCodeApply.setVisibility(View.VISIBLE);
                        viewHolder.mBinding.tvAfterPromoCodeApply.setText(setValue("promo_code_applied_discount_added",String.valueOf((int) Math.round(Objects.requireNonNull(getPromoCodeModel(model.getId())).getPromoDiscountInPercent()))));
                    } else {
                        viewHolder.mBinding.tvAfterPromoCodeApply.setVisibility(View.GONE);
                    }
                } else {
                    viewHolder.mBinding.tvAfterPromoCodeApply.setVisibility(View.GONE);

                }


            }
        }

        private void updateQuantity(PackageModel model, int delta, ViewHolder viewHolder, int discountPrice) {

            Optional<CartModel> obj = AppSettingManager.shared.tmpCartList.stream().filter(p -> p.id.equals(model.getId())).findFirst();
            if (obj.isPresent()) {
                CartModel cartModel = obj.get();

                if (delta > 0) {
                    // Increase quantity, but not beyond maxQuantity
                    cartModel.qty = Math.min(cartModel.qty + delta, cartModel.maxQty);
                } else if (delta < 0 && cartModel.qty > 0) {
                    // Decrease quantity down to 0
                    cartModel.qty = Math.max(cartModel.qty + delta, 0);
                }

            } else {
                if (delta == 1) {
                    if (venueObjectModel != null) {
                        CartModel cartModel = new CartModel(model, offersModel, venueObjectModel, 1, model.getRemainingQty(), discountPrice);
                        AppSettingManager.shared.tmpCartList.add(cartModel);
                    }

                }
            }

            int totalQtyForItem = AppSettingManager.shared.tmpCartList.stream().filter(p -> p.id.equals(model.getId())).mapToInt(p -> p.qty).sum();
            if (totalQtyForItem >= 0) {
                viewHolder.mBinding.tvTotal.setText(String.valueOf(totalQtyForItem));
            }

            Float totalAmount = AppSettingManager.shared.tmpCartList.stream().map(p -> p.qty * Float.parseFloat(String.valueOf(p.getDiscountAmount()))).reduce(0f, Float::sum);

            Float totalAed = AppSettingManager.shared.tmpCartList.stream().map(p -> p.qty * Float.parseFloat(String.valueOf(p.packageModel.getAmount()))).reduce(0f, Float::sum);

            Float savingAed = totalAed - totalAmount;

//            binding.tvYourSaving.setText("AED " + savingAed);
            Utils.setStyledText(activity,binding.tvYourSaving,String.valueOf(savingAed));
            binding.YourSavingLayout.setVisibility(savingAed > 0 ? View.VISIBLE : View.GONE);

//            binding.tvTotalDiscount.setText("AED " + savingAed);
            Utils.setStyledText(activity,binding.tvTotalDiscount,String.valueOf(savingAed));

            binding.tvTotalPrice.setVisibility(totalAmount > 0 ? View.VISIBLE : View.GONE);
//            binding.tvTotalPrice.setText(String.valueOf(totalAmount));
            Utils.setStyledText(activity,binding.tvTotalPrice,String.valueOf(totalAmount));

            int qtySum = AppSettingManager.shared.tmpCartList.stream()
                    .mapToInt(p -> p.qty)
                    .sum();

            binding.btnApplyPromoCode.setVisibility(qtySum > 0 ? View.VISIBLE : View.GONE);
            binding.YourSavingLayout.setVisibility(qtySum > 0 ? View.VISIBLE : View.GONE);


            if (qtySum == 0){
                resetPromoCodeLayout();
                venuePackageAdapter.notifyDataSetChanged();
                binding.YourSavingLayout.setVisibility(View.GONE);
                binding.layoutPromoAndDiscount.setVisibility(View.GONE);
            }

            if (isPromoCodeApply){
                requestVenuePromoCode();
            }
        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            private final VenueBuyItemBinding mBinding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = VenueBuyItemBinding.bind(itemView);
            }
        }
    }


    // --------------------------------------
    // endregion
    // --------------------------------------
}