package com.whosin.app.ui.activites.venue.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Paint;
import android.text.Editable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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
import com.whosin.app.comman.Preferences;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.ActivityMyCartBinding;
import com.whosin.app.databinding.CartSelectItemBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.models.CartModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.PaymentCredentialModel;
import com.whosin.app.service.models.VenueMetaDataPromoCodeModel;
import com.whosin.app.service.models.VenuePromoCodeModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.comman.BaseActivity;
import com.whosin.app.ui.activites.home.activity.ActivityListDetail;
import com.whosin.app.ui.activites.home.event.EventDetailsActivity;
import com.whosin.app.ui.activites.offers.OfferDetailBottomSheet;
import com.whosin.app.ui.activites.offers.VoucherDetailScreenActivity;
import com.whosin.app.ui.activites.raynaTicket.BottomSheets.SelectPaymentOptionBottomSheet;
import com.whosin.app.ui.activites.raynaTicket.BottomSheets.TabbyPaymentBottomSheet;
import com.whosin.app.ui.activites.wallet.WalletActivity;
import com.whosin.app.ui.fragment.wallet.PurchaseSuccessFragment;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class MyCartActivity extends BaseActivity {

    private ActivityMyCartBinding binding;
    private MyCartAdapter<CartModel> packageAdapter;
    private PaymentSheet paymentSheet;
    private boolean isShowBtn = false;
    private boolean isPromoCodeApply = false;
    private VenuePromoCodeModel promoCodeModel = null;


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    protected void initUi() {
        Graphics.applyBlurEffect(activity, binding.blurView);
        binding.cartRecycler.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        packageAdapter = new MyCartAdapter<>();
        binding.cartRecycler.setAdapter(packageAdapter);
        List<CartModel> cartItemList = CartModel.getCartHistory();
        if (!cartItemList.isEmpty()) {
            binding.cartRecycler.setVisibility(View.VISIBLE);
            binding.emptyPlaceHolderView.setVisibility(View.GONE);
            packageAdapter.updateData(cartItemList);
            updateTotalAmount(null);
            updateSavingText();
        } else {
            binding.cartRecycler.setVisibility(View.GONE);
            binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
            binding.roundCartButton.setVisibility(View.GONE);
            binding.promoLayout.setVisibility(View.GONE);
        }


        paymentSheet = new PaymentSheet(MyCartActivity.this, paymentSheetResult -> {
            if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
                Log.d("TAG", "Canceled");
            } else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
                Log.e("TAG", "Got error: ", ((PaymentSheetResult.Failed) paymentSheetResult).getError());
            } else if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
                CartModel.clearCart();
                PurchaseSuccessFragment purchaseSuccessFragment = new PurchaseSuccessFragment();
                purchaseSuccessFragment.closeCartActivity = true;
                purchaseSuccessFragment.callBack = data -> {
                    CartModel.clearCart();
                    Intent intent = new Intent();
                    intent.putExtra("close",true);
                    setResult(RESULT_OK, intent);
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

        binding.ivClose.setOnClickListener(v -> {
            onBackPressed();
        });

        binding.roundCartButton.setOnClickListener(view -> {
            List<CartModel> cartItemList = CartModel.getCartHistory();

            if (cartItemList.isEmpty()) {
                Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), "Add Pakage Into Cart");
                return;
            }
            JsonArray metaDate = new JsonArray();
            AtomicReference<Double> totalAmount = new AtomicReference<>(0.0);
            cartItemList.forEach(model -> {
                JsonObject itemMetaData = new JsonObject();
                itemMetaData.addProperty("type", model.type);
                if (Objects.equals(model.type, "offer")) {
                    if (model.getVenueModel() != null) {
                        itemMetaData.addProperty("offerId", model.packageModel.getOfferId());
                        itemMetaData.addProperty("packageId", model.id);
                        itemMetaData.addProperty("price", Double.parseDouble(String.valueOf(model.getDiscountAmount())) * model.qty);
                        itemMetaData.addProperty("qty", model.qty);
                        itemMetaData.addProperty("venueId", model.venueModel.getId());
                        double newAmount = 0;
                        if (isPromoCodeApply){
                            newAmount   = totalAmount.get() + (Double.parseDouble(String.valueOf(model.getPackageModel().getAmount())) * model.qty);
                        }else {
                            newAmount   = totalAmount.get() + (Double.parseDouble(String.valueOf(model.getDiscountAmount())) * model.qty);
                        }

                        totalAmount.set(newAmount);
                    }

                } else if (Objects.equals(model.type, "deal")) {
                    if (model.getVoucherModel() != null && !model.getVoucherModel().getVenueId().isEmpty()) {
                        int price = model.qty * model.voucherModel.getDiscountedPrice();
                        itemMetaData.addProperty("dealId", model.voucherModel.getId());
                        itemMetaData.addProperty("price", price);
                        itemMetaData.addProperty("qty", model.qty);
                        itemMetaData.addProperty("venueId", model.voucherModel.getVenueId());
                        double newAmount = 0;
                        if (isPromoCodeApply){
                            newAmount  = totalAmount.get() + (Double.parseDouble(String.valueOf(model.getVoucherModel().getActualPrice())) * model.qty);
                        }else {
                            newAmount  = totalAmount.get() + (Double.parseDouble(String.valueOf(model.getVoucherModel().getDiscountedPrice())) * model.qty);
                        }

                        totalAmount.set(newAmount);
                    }

                } else if (Objects.equals(model.type, "event")) {
                    itemMetaData.addProperty("eventId", model.venueModel.getEventID());
                    itemMetaData.addProperty("packageId", model.getPackageModel().getId());
                    itemMetaData.addProperty("venueId", model.venueModel.getId());
                    itemMetaData.addProperty("qty", model.getQty());
                    itemMetaData.addProperty("price",Double.parseDouble(String.valueOf(model.getDiscountAmount())) * model.qty);
                    double newAmount = 0;
                    if (isPromoCodeApply){
                        newAmount   = totalAmount.get() + (Double.parseDouble(String.valueOf(model.getPackageModel().getAmount())) * model.qty);
                    }else {
                        newAmount   = totalAmount.get() + (Double.parseDouble(String.valueOf(model.getDiscountAmount())) * model.qty);
                    }
                    totalAmount.set(newAmount);

                } else if (Objects.equals(model.type, "activity")) {
                    int price = Integer.parseInt(String.valueOf(model.getActivityModel().getPrice() - model.getActivityModel().getPrice() * Integer.parseInt(model.getActivityModel().getDiscount().split("%")[0]) / 100));
                    itemMetaData.addProperty("activityId", model.getActivityModel().getId());
                    itemMetaData.addProperty("activityType", model.getActivityModel().getActivityTime().getType());
                    itemMetaData.addProperty("date", model.date);
                    itemMetaData.addProperty("time", model.getTime());
                    itemMetaData.addProperty("reservedSeat", model.getQty());
                    itemMetaData.addProperty("price", price);
                    itemMetaData.addProperty("type", "activity");
                    double newAmount = totalAmount.get() + (Double.parseDouble(String.valueOf(price)) * model.qty);
                    totalAmount.set(newAmount);
                }

                metaDate.add(itemMetaData);
            });

            JsonObject params = new JsonObject();
            if (!TextUtils.isEmpty(binding.edtPromoCode.getText().toString()) && isPromoCodeApply && promoCodeModel != null){
                params.addProperty("promoCode", binding.edtPromoCode.getText().toString().trim());
                params.addProperty("totalAmount", String.valueOf(totalAmount));

                double discountedAmount = totalAmount.get() - promoCodeModel.getTotalDiscount();
                totalAmount.set(discountedAmount);
                params.addProperty("discount", promoCodeModel.getTotalDiscount());
            }

            params.addProperty("amount", String.valueOf(totalAmount));
            params.addProperty("currency", "aed");
            params.add("metadata", metaDate);
            Log.d("MainObject", "setListeners: " + params);

            SelectPaymentOptionBottomSheet bottmSheet = new SelectPaymentOptionBottomSheet();
            bottmSheet.amount = totalAmount.get();
            bottmSheet.callback = data -> requestStripeToken(params,data);
            bottmSheet.show(getSupportFragmentManager(), "");
        });

        binding.btnApplyPromoCode.setOnClickListener(view -> {
            Utils.preventDoubleClick(view);
            if (binding.btnApplyPromoCode.getText().toString().equals("Remove")){
                resetPromoCodeLayout();
                packageAdapter.notifyDataSetChanged();
                return;
            }
            if (TextUtils.isEmpty(binding.edtPromoCode.getText().toString())){
                Toast.makeText(this, "Please enter a promo code.", Toast.LENGTH_SHORT).show();
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

    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityMyCartBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

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
                            CartModel.clearCart();
                            Intent intent = new Intent();
                            intent.putExtra("close",true);
                            setResult(RESULT_OK, intent);
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

    private void updateTotalAmount(CartModel cartModel) {
        List<CartModel> cartItemList = CartModel.getCartHistory();
        if (cartItemList.isEmpty()) {
            return;
        }

        if (cartModel != null) {
            cartItemList.stream().filter(p -> Objects.equals(p.id, cartModel.id)).findFirst().ifPresent(c -> c.qty = cartModel.qty);
            Preferences.shared.setString("add_cart_item", new Gson().toJson(cartItemList));
        }


        if (isPromoCodeApply){
            requestVenuePromoCode();
        }else {
            double totalAmount = cartItemList.stream().mapToDouble(CartModel::getPriceAmount).sum();
            String formattedAmount = String.format(Locale.ENGLISH, "%.2f", totalAmount);
//            binding.tvPrice.setText(String.format(Locale.ENGLISH, "AED %.2f", totalAmount));
            Utils.setStyledText(activity,binding.tvPrice,formattedAmount);

            updateSavingText();
        }
    }

    private void increaseQuality(CartModel model, TextView textView) {
        int quantity = model.getQty() + 1;
        int maxQuantity = model.getMaxQty();
        if (maxQuantity == 0) {
            model.setQty(quantity);
            textView.setText(String.valueOf(quantity));
            updateTotalAmount(model);
            return;
        }
        if (quantity <= maxQuantity) {
            model.setQty(quantity);
            textView.setText(String.valueOf(quantity));
            updateTotalAmount(model);
        }


    }

    private void decreaseQuality(CartModel model, TextView textView) {
        int quantity = model.getQty() - 1;
        model.setQty(quantity);
        textView.setText(String.valueOf(quantity));

        if (quantity == 0) {
            updateTotalAmount(model);
            List<CartModel> cartItemList = CartModel.getCartHistory();
            cartItemList.removeIf(cartModel -> cartModel.id.equals(model.getId()));
            Preferences.shared.setString("add_cart_item", new Gson().toJson(cartItemList));
            packageAdapter.updateData(cartItemList);
            binding.emptyPlaceHolderView.setVisibility(cartItemList.isEmpty() ? View.VISIBLE : View.GONE);
            binding.cartRecycler.setVisibility(cartItemList.isEmpty() ? View.GONE : View.VISIBLE);
            binding.roundCartButton.setVisibility(cartItemList.isEmpty() ? View.GONE : View.VISIBLE);
            binding.YourSavingLayout.setVisibility(cartItemList.isEmpty() ? View.GONE : View.VISIBLE);
            binding.promoLayout.setVisibility(cartItemList.isEmpty() ? View.GONE : View.VISIBLE);
        }

        updateTotalAmount(model);

    }

    private int discountValue(double discount, String originalAmount) {
        double amount = Integer.parseInt(originalAmount);
        double value = discount * amount / 100;
        double discountPrice = amount - value;
        return  (int) Math.round(discountPrice);
    }

    private void updateSavingText() {
        List<CartModel> cartItemList = CartModel.getCartHistory();

        float totalAmount = calculateTotal(cartItemList, true);
        float totalAed = calculateTotal(cartItemList, false);
        float savingAed = totalAed - totalAmount;

//        binding.tvYourSaving.setText("AED " + savingAed);
        Utils.setStyledText(activity,binding.tvYourSaving,String.valueOf(savingAed));
        binding.YourSavingLayout.setVisibility(savingAed > 0 ? View.VISIBLE : View.GONE);
//        binding.tvTotalDiscount.setText("AED " + savingAed);
        Utils.setStyledText(activity,binding.tvTotalDiscount,String.valueOf(savingAed));

        double finalTotalAmount = cartItemList.stream().mapToDouble(CartModel::getPriceAmount).sum();
//        binding.tvPrice.setText(String.format(Locale.ENGLISH, "AED %.2f", finalTotalAmount));
        String formattedAmount = String.format(Locale.ENGLISH, "%.2f", finalTotalAmount);
        Utils.setStyledText(activity,binding.tvPrice,formattedAmount);
    }

    private float calculateTotal(List<CartModel> cartItemList, boolean isDiscounted) {
        return cartItemList.stream()
                .filter(p -> List.of("offer", "event", "deal").contains(p.getType()))
                .map(p -> p.getQty() * (isDiscounted ? getDiscountedPrice(p) : getActualPrice(p)))
                .reduce(0f, Float::sum);
    }

    private float getDiscountedPrice(CartModel p) {
        if ("deal".equals(p.getType())) return Float.parseFloat(String.valueOf(p.voucherModel.getDiscountedPrice()));
        return Float.parseFloat(String.valueOf(p.getDiscountAmount()));
    }

    private float getActualPrice(CartModel p) {
        if ("deal".equals(p.getType())) return Float.parseFloat(p.voucherModel.getActualPrice());
        return Float.parseFloat(String.valueOf(p.packageModel.getAmount()));
    }

    private JsonObject getJsonObject(){
        JsonObject object = new JsonObject();
        object.addProperty("promoCode",binding.edtPromoCode.getText().toString().trim());
        List<CartModel> cartItemList = CartModel.getCartHistory();
        JsonArray metaDate = new JsonArray();

        cartItemList.forEach(model -> {
            JsonObject itemMetaData = new JsonObject();

            switch (model.getType()) {
                case "offer":
                    itemMetaData.addProperty("type", "package");
                    itemMetaData.addProperty("packageId", model.getPackageModel().getId());
                    itemMetaData.addProperty("amount", model.getPackageModel().getAmount());
                    if (Integer.parseInt(model.getPackageModel().getDiscount()) != 0) itemMetaData.addProperty("discount",model.packageModel.getDiscount());
                    if (model.qty != 0) itemMetaData.addProperty("qty",model.qty);
                    break;
                case "event":
                    itemMetaData.addProperty("type", "event");
                    itemMetaData.addProperty("packageId", model.getPackageModel().getId());
                    itemMetaData.addProperty("amount", model.getPackageModel().getAmount());
                    if (Integer.parseInt(model.getPackageModel().getDiscount()) != 0) itemMetaData.addProperty("discount",model.packageModel.getDiscount());
                    if (model.qty != 0) itemMetaData.addProperty("qty",model.qty);
                    break;
                case "deal":
                    itemMetaData.addProperty("type", "deal");
                    itemMetaData.addProperty("dealId",  model.voucherModel.getId());
                    itemMetaData.addProperty("amount",  model.voucherModel.getActualPrice());
                    if (Integer.parseInt(model.voucherModel.getDiscountValue()) != 0) itemMetaData.addProperty("discount",model.voucherModel.getDiscountValue());
                    if (model.qty != 0) itemMetaData.addProperty("qty",model.qty);
                    break;
            }

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
            binding.btnApplyPromoCode.setText("Remove");
            binding.btnApplyPromoCode.setTextColor(ContextCompat.getColor(this,R.color.red));

            SpannableString styledPrice = Utils.getStyledText(activity,String.valueOf(promoCodeModel.getPromoDiscount()));
            SpannableStringBuilder fullText = new SpannableStringBuilder();
            fullText.append(styledPrice).append(" saved with ").append(binding.edtPromoCode.getText().toString());
            binding.tvApplyPromoCode.setText(fullText);
//            binding.tvApplyPromoCode.setText(promoCodeModel.getPromoDiscount() +  " AED saved with " + binding.edtPromoCode.getText().toString());
            binding.tvApplyPromoCode.setVisibility(View.VISIBLE);
            binding.imgValidation.setVisibility(View.GONE);
            binding.edtPromoCode.setVisibility(View.GONE);
            binding.layoutPromoCodeApply.setVisibility(View.VISIBLE);
//            binding.tvPromoCode.setText( "AED " + promoCodeModel.getPromoDiscount());
//            binding.tvTotalDiscount.setText( "AED " + promoCodeModel.getItemsDiscount());

            Utils.setStyledText(activity,binding.tvPromoCode,String.valueOf(promoCodeModel.getPromoDiscount()));
            Utils.setStyledText(activity,binding.tvYourSaving,String.valueOf(promoCodeModel.getTotalDiscount()));
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
        binding.btnApplyPromoCode.setText("Apply");
        binding.btnApplyPromoCode.setTextColor(ContextCompat.getColor(this,R.color.brand_pink));
        binding.btnApplyPromoCode.setVisibility(View.GONE);
        binding.layoutPromoCodeApply.setVisibility(View.GONE);
        binding.ivDropDown.setRotation(90f);
        updateSavingText();
    }

    private VenueMetaDataPromoCodeModel getPromoCodeModel(String id , boolean isDealID) {
        if (promoCodeModel == null || promoCodeModel.getMetaData().isEmpty()) {
            return null;
        }

        return promoCodeModel.getMetaData().stream()
                .filter(p -> isDealID ? p.getDealId().equals(id) : p.getPackageId().equals(id))
                .findFirst()
                .orElse(null);

    }

    private boolean isPromoApply(String id , boolean isDeal){
        return isPromoCodeApply && promoCodeModel != null && getPromoCodeModel(id,isDeal) != null && !promoCodeModel.getPromoDiscountType().equals("flat");
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
                    Log.d("TAG", "showAlertDialogWithOkButton: " + error);
                    Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), error);
                    return;
                }
                if (Objects.equals(model.message, "Vip User Order Successfully Created!")) {
                    CartModel.clearCart();
                    PurchaseSuccessFragment purchaseSuccessFragment = new PurchaseSuccessFragment();
                    purchaseSuccessFragment.closeCartActivity = true;
                    purchaseSuccessFragment.callBack = data -> {
                        CartModel.clearCart();
                        Intent intent = new Intent();
                        intent.putExtra("close",true);
                        setResult(RESULT_OK, intent);
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
                            Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), "Sorry, Tabby is unable to approve this purchase, plesae use an alternative paymetnt method for your orders.");
                        }
                    }
                }
            }
        });
    }

    private void requestVenuePromoCode() {
        if (getJsonObject().isEmpty() || getJsonObject().isJsonNull()) return;
        binding.promoCodeProgressView.setVisibility(View.VISIBLE);
        binding.btnApplyPromoCode.setVisibility(View.GONE);
        Log.d("PromoCode", "requestVenuePromoCode: " + getJsonObject());
        DataService.shared(activity).requestVenuePromoCode(getJsonObject(), new RestCallback<ContainerModel<VenuePromoCodeModel>>(this) {
            @Override
            public void result(ContainerModel<VenuePromoCodeModel> model, String error) {
                binding.promoCodeProgressView.setVisibility(View.GONE);
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    promoCodeModel = null;
                    updatePromoCodeUI(false,error);
                    return;
                }

                if (model.getData() != null){
                    isPromoCodeApply = true;
                    promoCodeModel = model.getData();

                    updatePromoCodeUI(true,"");

                    List<CartModel> cartItemList = CartModel.getCartHistory();

//                    binding.tvYourSaving.setText("AED " + promoCodeModel.getTotalDiscount());
                    Utils.setStyledText(activity,binding.tvYourSaving,String.valueOf(promoCodeModel.getTotalDiscount()));
                    binding.YourSavingLayout.setVisibility(promoCodeModel.getTotalDiscount() > 0 ? View.VISIBLE : View.GONE);


                    AtomicReference<Double> totalAmount = new AtomicReference<>(0.0);
                    cartItemList.forEach(cartModel -> {
                        if (Objects.equals(cartModel.type, "offer")) {
                            if (cartModel.getVenueModel() != null) {
                                double newAmount = totalAmount.get() + (Double.parseDouble(String.valueOf( cartModel.getPackageModel().getAmount())) * cartModel.qty);
                                totalAmount.set(newAmount);
                            }
                        } else if (Objects.equals(cartModel.type, "deal")) {
                            if (cartModel.getVoucherModel() != null && !cartModel.getVoucherModel().getVenueId().isEmpty()) {
                                double newAmount = totalAmount.get() + (Double.parseDouble(String.valueOf(cartModel.getVoucherModel().getActualPrice())) * cartModel.qty);
                                totalAmount.set(newAmount);
                            }
                        } else if (Objects.equals(cartModel.type, "event")) {
                            double newAmount = totalAmount.get() + (Double.parseDouble(String.valueOf( cartModel.getPackageModel().getAmount())) * cartModel.qty);
                            totalAmount.set(newAmount);

                        }
                    });

                    double discountedAmount = totalAmount.get() - promoCodeModel.getTotalDiscount();
                    String formattedAmount = String.format(Locale.ENGLISH, "%.2f", discountedAmount);

//                    binding.tvPrice.setText(String.format(Locale.ENGLISH, "AED %.2f", discountedAmount));
                    Utils.setStyledText(activity,binding.tvPrice,formattedAmount);

                    packageAdapter.notifyDataSetChanged();
                }

            }
        });
    }


    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    private class MyCartAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            switch (Objects.requireNonNull(AppConstants.CartBlockType.valueOf(viewType))) {
                case DEAL:
                    return new DealHolder(UiUtils.getViewBy(parent, R.layout.cart_select_item_));
                case OFFER:
                    return new OfferHolder(UiUtils.getViewBy(parent, R.layout.cart_select_item_));
                case ACTIVITY:
                    return new ActivityHolder(UiUtils.getViewBy(parent, R.layout.cart_select_item_));
                case Event:
                    return new EventHolder(UiUtils.getViewBy(parent, R.layout.cart_select_item_));
            }
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            CartModel model = (CartModel) getItem(position);
            if (model.getBlockType() == AppConstants.CartBlockType.DEAL) {
                DealHolder viewHolder = (DealHolder) holder;
                viewHolder.setupData(model);
            } else if (model.getBlockType() == AppConstants.CartBlockType.OFFER) {
                OfferHolder viewHolder = (OfferHolder) holder;
                viewHolder.setupData(model);
            } else if (model.getBlockType() == AppConstants.CartBlockType.ACTIVITY) {
                ActivityHolder viewHolder = (ActivityHolder) holder;
                viewHolder.setupData(model);
            } else if (model.getBlockType() == AppConstants.CartBlockType.Event) {
                EventHolder viewHolder = (EventHolder) holder;
                viewHolder.setupData(model);
            }

        }

        @Override
        public int getItemViewType(int position) {
            CartModel model = (CartModel) getItem(position);
            return model.getBlockType().getValue();
        }

        public class DealHolder extends RecyclerView.ViewHolder {
            private final CartSelectItemBinding mBinding;

            public DealHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = CartSelectItemBinding.bind(itemView);
            }

            @SuppressLint({"SetTextI18n", "DefaultLocale"})
            public void setupData(CartModel model) {
                if (model != null) {
                    mBinding.tvType.setText(model.getType().substring(0, 1).toUpperCase() + model.getType().substring(1));

                    if (model.getVoucherModel().getVenue() != null) {
                        if (model.getVoucherModel().getVenue().getName() != null) {
                            mBinding.tvVenueName.setText(model.getVoucherModel().getVenue().getName());
                        }
                        if (model.getVoucherModel().getVenue().getAddress() != null) {
                            mBinding.tvVenueAddress.setText(model.getVoucherModel().getVenue().getAddress());
                        }
                        if (model.getVoucherModel().getVenue().getLogo() != null) {
                            Graphics.loadImage(model.getVoucherModel().getVenue().getLogo(), mBinding.imgVenueLogo);
                        }
                    }


                    if (isPromoApply(model.getVoucherModel().getId(),true)){
                        VenueMetaDataPromoCodeModel tmpModel = getPromoCodeModel(model.getVoucherModel().getId(),true);
                        mBinding.tvDiscount.setText( String.format( "%s%%",(int) Math.round(tmpModel.getFinalDiscountInPercent()) ) );
                        mBinding.tvDiscountPrice.setText( String.valueOf( tmpModel.getFinalAmount() / model.qty ) );
                    }else {
                        String discountValue = model.getVoucherModel().getDiscountValue();
                        if (!TextUtils.isEmpty(discountValue) && !discountValue.equals("0")) {
                            mBinding.tvDiscount.setText(String.format("%s%%", discountValue));
                        } else {
                            mBinding.tvDiscount.setVisibility(View.GONE);
                        }
//                        mBinding.tvDiscountPrice.setText(String.format("AED %d", model.getVoucherModel().getDiscountedPrice()));
                        Utils.setStyledText(activity,mBinding.tvDiscountPrice,String.valueOf(model.getVoucherModel().getDiscountedPrice()));

                    }

                    mBinding.tvOriginalPrice.setPaintFlags(mBinding.tvOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

//                    mBinding.tvOriginalPrice.setText("AED " + model.getVoucherModel().getActualPrice());
                    Utils.setStyledText(activity,mBinding.tvOriginalPrice,String.valueOf(model.getVoucherModel().getActualPrice()));


                    mBinding.tvTitle.setText(model.getVoucherModel().getTitle());
                    mBinding.tvDescription.setText(model.getVoucherModel().getDescription());
                    mBinding.tvQty.setText(String.valueOf(model.getQty()));

                    mBinding.ivPlus.setOnClickListener(view -> increaseQuality(model, mBinding.tvQty));

                    mBinding.ivMinus.setOnClickListener(view -> decreaseQuality(model, mBinding.tvQty));


                    mBinding.getRoot().setOnClickListener(view -> {
                        startActivity(new Intent(activity, VoucherDetailScreenActivity.class).putExtra("id", model.getVoucherModel().getId()));
                    });

                    if (promoCodeModel != null) {
                        if (isPromoCodeApply && !promoCodeModel.getPromoDiscountType().equals("flat") && getPromoCodeModel(model.getVoucherModel().getId(),true) != null) {
                            mBinding.tvAfterPromoCodeApply.setVisibility(View.VISIBLE);
                            mBinding.tvAfterPromoCodeApply.setText("Promo code applied : " + (int) Math.round(getPromoCodeModel(model.getVoucherModel().getId(),true).getPromoDiscountInPercent()) + "% discount added");
                        } else {
                            mBinding.tvAfterPromoCodeApply.setVisibility(View.GONE);
                        }
                    } else {
                        mBinding.tvAfterPromoCodeApply.setVisibility(View.GONE);
                    }


                }
            }
        }

        public class OfferHolder extends RecyclerView.ViewHolder {
            private final CartSelectItemBinding mBinding;

            public OfferHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = CartSelectItemBinding.bind(itemView);
            }

            public void setupData(CartModel model) {
                if (model != null) {
                    mBinding.tvType.setText(String.format("%s%s", model.getType().substring(0, 1).toUpperCase(), model.getType().substring(1)));
                    if (model.getVenueModel() != null) {
                        mBinding.tvVenueName.setText(model.getVenueModel().getName());
                        mBinding.tvVenueAddress.setText(model.getVenueModel().getAddress());
                        Graphics.loadImage(model.getVenueModel().getLogo(), mBinding.imgVenueLogo);
                    }
//                    mBinding.tvOriginalPrice.setText(String.format("AED %s", model.getPackageModel().getAmount()));
                    Utils.setStyledText(activity,mBinding.tvOriginalPrice,String.valueOf(model.getPackageModel().getAmount()));
                    mBinding.tvOriginalPrice.setPaintFlags(mBinding.tvOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                    if (isPromoApply(model.packageModel.getId(),false)){
                        VenueMetaDataPromoCodeModel tmpModel = getPromoCodeModel(model.packageModel.getId(),false);
                        assert tmpModel != null;
                        double discount = tmpModel.getFinalDiscountInPercent();
                        if (discount != 0) {
                            mBinding.tvDiscount.setText(Utils.notNullString((int) Math.round(discount) + "%"));
//                            mBinding.tvDiscountPrice.setText(String.format("AED %s", (int) Math.round(tmpModel.getFinalAmount() / model.qty)));
                            if (model.qty != 0) {
                                int unitPrice = (int) Math.round(tmpModel.getFinalAmount() / model.qty);
                                Utils.setStyledText(activity, mBinding.tvDiscountPrice, String.valueOf(unitPrice));
                            } else {
                                mBinding.tvDiscountPrice.setText("N/A");
                            }


                        } else {
                            mBinding.tvDiscount.setVisibility(View.GONE);

                        }
                    }else {
//                        mBinding.tvDiscountPrice.setText(String.format("AED %s", model.getDiscountAmount()));
                        Utils.setStyledText(activity,mBinding.tvDiscountPrice,String.valueOf(model.getDiscountAmount()));
                        String discount = model.getPackageModel().getDiscount();
                        if (!discount.equals("0")) {
                            String modifiedString = discount.contains("%") ? discount : discount + "%";
                            mBinding.tvDiscount.setText(Utils.notNullString(modifiedString));
                        } else {
                            mBinding.tvDiscount.setVisibility(View.GONE);
                        }

                    }



                    mBinding.tvTitle.setText(model.getPackageModel().getTitle());
                    mBinding.tvDescription.setVisibility(View.GONE);
                    mBinding.tvQty.setText(String.valueOf(model.getQty()));

                    mBinding.ivPlus.setOnClickListener(view -> increaseQuality(model, mBinding.tvQty));
                    mBinding.ivMinus.setOnClickListener(view -> decreaseQuality(model, mBinding.tvQty));

                    itemView.setOnClickListener(view -> {
                        OfferDetailBottomSheet dialog = new OfferDetailBottomSheet();
                        dialog.offerId =model.getOfferModel().getId();
                        dialog.show(getSupportFragmentManager(), "");
                    });


                    if (promoCodeModel != null) {
                        if (isPromoCodeApply && !promoCodeModel.getPromoDiscountType().equals("flat") && getPromoCodeModel(model.getPackageModel().getId(),false) != null) {
                            mBinding.tvAfterPromoCodeApply.setVisibility(View.VISIBLE);
                            mBinding.tvAfterPromoCodeApply.setText("Promo code applied : " + (int) Math.round(getPromoCodeModel(model.getPackageModel().getId(),false).getPromoDiscountInPercent()) + "% discount added");
                        } else {
                            mBinding.tvAfterPromoCodeApply.setVisibility(View.GONE);
                        }
                    } else {
                        mBinding.tvAfterPromoCodeApply.setVisibility(View.GONE);

                    }


                }
            }
        }

        public class EventHolder extends RecyclerView.ViewHolder {
            private final CartSelectItemBinding mBinding;

            public EventHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = CartSelectItemBinding.bind(itemView);
            }

            public void setupData(CartModel model) {
                if (model != null) {
                    mBinding.tvType.setText(String.format("%s%s", model.getType().substring(0, 1).toUpperCase(), model.getType().substring(1)));
                    if (model.getVenueModel() != null) {
                        mBinding.tvVenueName.setText(model.getVenueModel().getName());
                        mBinding.tvVenueAddress.setText(model.getVenueModel().getAddress());
                        Graphics.loadImage(model.getVenueModel().getLogo(), mBinding.imgVenueLogo);
                    }

                    mBinding.tvOriginalPrice.setPaintFlags(mBinding.tvOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
//                    mBinding.tvOriginalPrice.setText(String.format("AED %s", model.getPackageModel().getAmount()));
                    Utils.setStyledText(activity,mBinding.tvOriginalPrice,String.valueOf(model.getPackageModel().getAmount()));



                    if (isPromoApply(model.packageModel.getId(),false)){
                        VenueMetaDataPromoCodeModel tmpModel = getPromoCodeModel(model.packageModel.getId(),false);
                        assert tmpModel != null;
                        double discount = tmpModel.getFinalDiscountInPercent();
                        if (discount != 0) {
                            mBinding.tvDiscount.setText(Utils.notNullString((int) Math.round(discount) + "%"));
//                            mBinding.tvDiscountPrice.setText(String.format("AED %s", (int) Math.round(tmpModel.getFinalAmount() / model.qty)));
                            if (model.qty != 0) {
                                int unitPrice = (int) Math.round(tmpModel.getFinalAmount() / model.qty);
                                Utils.setStyledText(activity, mBinding.tvDiscountPrice, String.valueOf(unitPrice));
                            } else {
                                // Fallback in case of error
                                mBinding.tvDiscountPrice.setText("N/A");
                            }


                        } else {
                            mBinding.tvDiscount.setVisibility(View.GONE);

                        }
                    }else {
//                        mBinding.tvDiscountPrice.setText(String.format("AED %s", model.getDiscountAmount()));
                        Utils.setStyledText(activity,mBinding.tvDiscountPrice,String.valueOf(model.getDiscountAmount()));
                        String discount = model.getPackageModel().getDiscount();
                        if (!discount.equals("0")) {
                            String modifiedString = discount.contains("%") ? discount : discount + "%";
                            mBinding.tvDiscount.setText(Utils.notNullString(modifiedString));
                        } else {
                            mBinding.tvDiscount.setVisibility(View.GONE);
                        }

                    }


                    mBinding.tvTitle.setText(model.getPackageModel().getTitle());
                    mBinding.tvDescription.setVisibility(View.GONE);
                    mBinding.tvQty.setText(String.valueOf(model.getQty()));

                    mBinding.ivPlus.setOnClickListener(view -> increaseQuality(model, mBinding.tvQty));
                    mBinding.ivMinus.setOnClickListener(view -> decreaseQuality(model, mBinding.tvQty));

                    mBinding.getRoot().setOnClickListener(v -> {
                        Intent intent = new Intent(activity, EventDetailsActivity.class);
                        intent.putExtra("eventId", model.getPackageModel().getEventId());
                        intent.putExtra("venueName", model.getVenueModel().getName());
                        intent.putExtra("venueAddress", model.getVenueModel().getAddress());
                        intent.putExtra("venueImage", model.getVenueModel().getLogo());
                        intent.putExtra("venueId", model.getVenueModel().getId());
                        startActivity(intent);
                    });

                    if (promoCodeModel != null) {
                        if (isPromoCodeApply && !promoCodeModel.getPromoDiscountType().equals("flat") && getPromoCodeModel(model.getPackageModel().getId(),false) != null) {
                            mBinding.tvAfterPromoCodeApply.setVisibility(View.VISIBLE);
                            mBinding.tvAfterPromoCodeApply.setText("Promo code applied : " + (int) Math.round(getPromoCodeModel(model.getPackageModel().getId(),false).getPromoDiscountInPercent()) + "% discount added");
                        } else {
                            mBinding.tvAfterPromoCodeApply.setVisibility(View.GONE);
                        }
                    } else {
                        mBinding.tvAfterPromoCodeApply.setVisibility(View.GONE);

                    }


                }
            }
        }

        public class ActivityHolder extends RecyclerView.ViewHolder {
            private final CartSelectItemBinding mBinding;

            public ActivityHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = CartSelectItemBinding.bind(itemView);
            }

            public void setupData(CartModel model) {
                if (model != null) {
                    mBinding.tvType.setText(String.format("%s%s", model.getType().substring(0, 1).toUpperCase(), model.getType().substring(1)));
                    mBinding.tvVenueName.setText(model.getActivityModel().getProvider().getName());
                    mBinding.tvVenueAddress.setText(model.getActivityModel().getProvider().getAddress());
                    Graphics.loadImage(model.getActivityModel().getProvider().getLogo(), mBinding.imgVenueLogo);

                    mBinding.tvOriginalPrice.setPaintFlags(mBinding.tvOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
//                    mBinding.tvOriginalPrice.setText(String.format("AED %s", model.getActivityModel().getPrice()));
//                    mBinding.tvDiscountPrice.setText(String.format("AED %s", model.getDiscountAmount()));

                    Utils.setStyledText(activity,mBinding.tvOriginalPrice,String.valueOf(model.getActivityModel().getPrice()));
                    Utils.setStyledText(activity,mBinding.tvDiscountPrice,String.valueOf(model.getDiscountAmount()));

//                    mBinding.tvDiscountPrice.setText(String.format("AED %s", model.getActivityModel().getPrice() - model.getActivityModel().getPrice() * Integer.parseInt(model.getActivityModel().getDiscount().split("%")[0]) / 100));


                    if (!model.getActivityModel().getDiscount().equals("0")) {
                        mBinding.tvDiscount.setText(Utils.addPercentage(model.getActivityModel().getDiscount()));
                    } else {
                        mBinding.tvDiscount.setVisibility(View.GONE);
                    }

                    if (!model.getDate().isEmpty()) {
                        mBinding.tvDate.setVisibility(View.VISIBLE);
                        mBinding.tvDate.setText("Activity Date : " + Utils.convertDateFormat(model.getDate(), "yyyy-MM-dd", "dd-MM-yyyy"));
                    } else {
                        mBinding.tvDate.setVisibility(View.GONE);
                    }

                    if (!model.getTime().isEmpty()) {
                        mBinding.tvTime.setVisibility(View.VISIBLE);
                        mBinding.tvTime.setText("Activity Time : " + model.getTime());
                    } else {
                        mBinding.tvTime.setVisibility(View.GONE);
                    }

                    mBinding.tvTitle.setText(model.getActivityModel().getName());
                    mBinding.tvDescription.setText(model.getActivityModel().getDescription());
                    mBinding.tvQty.setText(String.valueOf(model.getQty()));

                    mBinding.ivPlus.setOnClickListener(view -> increaseQuality(model, mBinding.tvQty));
                    mBinding.ivMinus.setOnClickListener(view -> decreaseQuality(model, mBinding.tvQty));

                    itemView.setOnClickListener(view -> {
                        Intent intent = new Intent(activity, ActivityListDetail.class);
                        intent.putExtra("activityId", model.getId()).putExtra("type", "activities")
                                .putExtra("name", model.getActivityModel().getName())
                                .putExtra("image", model.getActivityModel().getProvider().getLogo())
                                .putExtra("title", model.getActivityModel().getProvider().getName())
                                .putExtra("address", model.getActivityModel().getProvider().getAddress());
                        startActivity(intent);
//                        activity.overridePendingTransition(R.anim.slide_up, R.anim.fade_out);
                    });

                }
            }
        }

    }

    // endregion
    // --------------------------------------

}