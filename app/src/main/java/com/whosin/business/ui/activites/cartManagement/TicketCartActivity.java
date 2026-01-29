package com.whosin.business.ui.activites.cartManagement;

import android.annotation.SuppressLint;

import android.content.Intent;
import android.net.Uri;
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
import com.tapadoo.alerter.Alerter;
import com.whosin.business.R;
import com.whosin.business.comman.AppConstants;
import com.whosin.business.comman.DiffAdapter;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Graphics;
import com.whosin.business.comman.Utils;
import com.whosin.business.comman.ui.ContactUsBlockManager;
import com.whosin.business.service.models.ContactUsBlockModel;
import com.whosin.business.comman.ui.UiUtils;
import com.whosin.business.databinding.ActivityTicketCartBinding;
import com.whosin.business.databinding.ItemContactUsBlockBinding;
import com.whosin.business.databinding.ItemTourDetailForCartViewBinding;
import com.whosin.business.databinding.MyCartItemDesignBinding;
import com.whosin.business.databinding.MyCartPriceLayoutBinding;
import com.whosin.business.service.DataService;
import com.whosin.business.service.manager.AppSettingManager;
import com.whosin.business.service.manager.RaynaTicketManager;
import com.whosin.business.service.manager.SessionManager;
import com.whosin.business.service.models.BigBusModels.BigBusOptionsItemModel;
import com.whosin.business.service.models.ContainerModel;
import com.whosin.business.service.models.PaymentCredentialModel;
import com.whosin.business.service.models.VenuePromoCodeModel;
import com.whosin.business.service.models.myCartModels.MyCartItemsModel;
import com.whosin.business.service.models.myCartModels.MyCartMainModel;
import com.whosin.business.service.models.myCartModels.MyCartTourDetailsModel;
import com.whosin.business.service.models.rayna.RaynaTicketBookingModel;
import com.whosin.business.service.models.rayna.RaynaTicketDetailModel;
import com.whosin.business.service.models.rayna.TourOptionDetailModel;
import com.whosin.business.service.rest.RestCallback;
import com.whosin.business.ui.activites.auth.AuthenticationActivity;
import com.whosin.business.ui.activites.comman.BaseActivity;
import com.whosin.business.ui.activites.raynaTicket.BottomSheets.SelectPaymentOptionBottomSheet;
import com.whosin.business.ui.activites.raynaTicket.BottomSheets.TabbyPaymentBottomSheet;
import com.whosin.business.ui.activites.wallet.WalletActivity;
import com.whosin.business.ui.adapter.CartAddOnAdapter;
import com.whosin.business.ui.fragment.wallet.PurchaseSuccessFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import payment.sdk.android.PaymentClient;
import payment.sdk.android.cardpayment.CardPaymentData;
import payment.sdk.android.cardpayment.CardPaymentRequest;
import payment.sdk.android.googlepay.GooglePayConfig;
import payment.sdk.android.payments.PaymentsLauncher;
import payment.sdk.android.payments.PaymentsRequest;
import payment.sdk.android.payments.PaymentsResult;


import com.whosin.business.service.manager.LogManager;

public class TicketCartActivity extends BaseActivity {

    private ActivityTicketCartBinding binding;

    private final MyCartTicketListAdapter<DiffIdentifier> myCartTicketListAdapter = new MyCartTicketListAdapter<>();

    private MyCartMainModel myCartMainModel;

    private PaymentSheet paymentSheet;

    private boolean isPromoCodeApply = false;

    private VenuePromoCodeModel promoCodeModel = null;

    private boolean isShowBtn = false;

    private static final int NGENIUS_REQUEST_CODE = 3001;

    private PaymentsLauncher paymentsLauncher;

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    protected void initUi() {

        applyTranslations();
        paymentsLauncher = new PaymentsLauncher(this, paymentsResult -> {
            Double totalPrice = 0.0;
            String id;
            if (myCartMainModel != null && myCartMainModel.getItems() != null) {
                totalPrice = myCartMainModel.getItems().stream().mapToDouble(MyCartItemsModel::getAmount).sum();
                id = myCartMainModel.getItems().get(0).get_id();
            } else {
                id = "";
            }
            if(paymentsResult instanceof PaymentsResult.Authorised || paymentsResult instanceof PaymentsResult.Success) {
                PurchaseSuccessFragment purchaseSuccessFragment = new PurchaseSuccessFragment();
                Double finalTotalPrice = totalPrice;
                purchaseSuccessFragment.callBackForRaynaBooking = data1 -> {
                    if (data1) {
                        LogManager.shared.logTicketEvent(LogManager.LogEventType.purchase, id , "Purchase Success", finalTotalPrice, null, "AED");
                        startActivity(new Intent(Graphics.context, WalletActivity.class));
                        RaynaTicketManager.shared.finishAllActivities();
                        RaynaTicketManager.shared.clearManager();
                        finish();

                    }
                };
                purchaseSuccessFragment.show(getSupportFragmentManager(), "");
            }
            else if (paymentsResult instanceof PaymentsResult.Failed){
                String error = ((PaymentsResult.Failed) paymentsResult).getError();
                LogManager.shared.logTicketEvent(LogManager.LogEventType.paymentFailed, id, "Purchase Cancelled", totalPrice, null, "AED");
                Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
            } else {
                LogManager.shared.logTicketEvent(LogManager.LogEventType.paymentFailed, id, "Purchase Failure", totalPrice, null, "AED");
                Toast.makeText(activity, "Payment failed, Please try again.", Toast.LENGTH_SHORT).show();
            }
        });


        paymentSheet = new PaymentSheet(TicketCartActivity.this, paymentSheetResult -> {
            Double totalPrice = 0.0;
            String id;
            if (myCartMainModel != null && myCartMainModel.getItems() != null) {
                totalPrice = myCartMainModel.getItems().stream().mapToDouble(MyCartItemsModel::getAmount).sum();
                id = myCartMainModel.getItems().get(0).get_id();
            } else {
                id = "";
            }

            if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
                Log.d("TAG", "Canceled");
                LogManager.shared.logTicketEvent(LogManager.LogEventType.paymentCancelled, id, "Cart Checkout", totalPrice, null, "AED");
            } else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
                Log.e("TAG", "Got error: ", ((PaymentSheetResult.Failed) paymentSheetResult).getError());
                LogManager.shared.logTicketEvent(LogManager.LogEventType.paymentFailed, id, "Cart Checkout", totalPrice, null, "AED");
            } else if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
                LogManager.shared.logTicketEvent(LogManager.LogEventType.purchase, id, "Cart Checkout", totalPrice, null, "AED");
                SessionManager.shared.clearTicketCartData();
                binding.myCartItemRecycleView.getRecycledViewPool().clear();
                myCartTicketListAdapter.updateData(new ArrayList<>());
                binding.buttonsLayout.setVisibility(View.GONE);
                binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
                PurchaseSuccessFragment purchaseSuccessFragment = new PurchaseSuccessFragment();
                purchaseSuccessFragment.callBackForRaynaBooking = data -> {
                    if (data) {
                        startActivity(new Intent(Graphics.context, WalletActivity.class));
                        finish();

                    }
                };
                purchaseSuccessFragment.show(getSupportFragmentManager(), "");

            }
        });

        Graphics.applyBlurEffect(activity,binding.blurView);

        boolean showAlert = getIntent().getBooleanExtra("showAlert",false);
        if (showAlert){
            Alerter.create(activity).setTitle(getValue("added_to_cart")).setText(getValue("item_added_successfully"))
                    .setTitleAppearance(R.style.AlerterTitle).setTextAppearance(R.style.AlerterText)
                    .setBackgroundColorRes(R.color.white_color)
                    .hideIcon()
                    .show();
        }

        binding.myCartItemRecycleView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        binding.myCartItemRecycleView.setAdapter(myCartTicketListAdapter);

        if (SessionManager.shared.geMyCartTicketData() != null){
            myCartMainModel = SessionManager.shared.geMyCartTicketData();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
            myCartMainModel.getItems().sort((o1, o2) -> {
                try {
                    Date date1 = sdf.parse(o1.getCreatedAt());
                    Date date2 = sdf.parse(o2.getCreatedAt());
                    assert date2 != null;
                    return date2.compareTo(date1);
                } catch (Exception e) {
                    return 0;
                }
            });
            ArrayList<DiffIdentifier> items = new ArrayList<>();
            items.addAll(myCartMainModel.getItems());
            if (myCartMainModel.getContactUsBlock() != null) {
                items.add(myCartMainModel.getContactUsBlock());
            }
            myCartTicketListAdapter.updateData(items);
            updateTotalAmout();
            Utils.showViews(binding.myCartItemRecycleView, binding.buttonsLayout);
            Utils.hideViews(binding.emptyPlaceHolderView);
        }

        requestMyCartList(SessionManager.shared.geMyCartTicketData() == null);


    }

    @Override
    protected void setListeners() {

        binding.ivClose.setOnClickListener(v -> finish());

        binding.roundCartButton.setOnClickListener(view -> {
            Utils.preventDoubleClick(view);
            Double totalPrice = 0.0;
            String id;
            if (myCartMainModel != null && myCartMainModel.getItems() != null) {
                totalPrice = myCartMainModel.getItems().stream().mapToDouble(MyCartItemsModel::getAmount).sum();
                id = myCartMainModel.getItems().get(0).get_id();
            } else {
                id = "";
            }
            LogManager.shared.logTicketEvent(LogManager.LogEventType.paymentInitiated, id, "Cart Checkout", totalPrice, null, "AED");

            SelectPaymentOptionBottomSheet bottmSheet = new SelectPaymentOptionBottomSheet();
            bottmSheet.isFromRaynaTicket = true;
            double checkTabbyAmount = 0;
            if (isPromoCodeApply && promoCodeModel != null && !TextUtils.isEmpty(promoCodeModel.getAmount())) {
                try {
                    checkTabbyAmount = Double.parseDouble(promoCodeModel.getAmount());
                } catch (NumberFormatException e) {
                    checkTabbyAmount = 0;
                }
            } else if (myCartMainModel != null && myCartMainModel.getItems() != null) {
                checkTabbyAmount = myCartMainModel.getItems().stream()
                        .mapToDouble(MyCartItemsModel::getAmount)
                        .sum();
            }

            bottmSheet.amount = checkTabbyAmount;
            bottmSheet.callback = this::requestCartTicketBooking;
            bottmSheet.show(getSupportFragmentManager(), "");
        });

        binding.btnApplyPromoCode.setOnClickListener(view -> {
            Utils.preventDoubleClick(view);
            if (binding.btnApplyPromoCode.getText().toString().equals(getValue("remove"))) {
                requestRemoveCartSubscription();
                return;
            }

            if (TextUtils.isEmpty(binding.edtPromoCode.getText().toString())){
                Toast.makeText(this, getValue("please_enter_promo_code"), Toast.LENGTH_SHORT).show();
                return;
            }
            requestPromoCode();
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

        RaynaTicketManager.shared.cartReloadCallBack = data -> {
            if (data){
                requestMyCartList(true);
            }
        };

    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityTicketCartBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1005 && resultCode == RESULT_OK) {
            requestMyCartList(true);
            return;
        }
        try {
            onNGenuesCardPaymentResponse(CardPaymentData.getFromIntent(data));
        } catch (Exception ignored) {
        }
    }

    @Override
    protected Map<View, String> getTranslationMap() {
        Map<View, String> map = new HashMap<>();
        map.put(binding.tvTitle, "my_cart");
        map.put(binding.tvCheckoutTitle, "checkOut ");
        map.put(binding.edtPromoCode, "enter_promo_code");
        map.put(binding.tvTotalSavingTitle, "saved_with");
        map.put(binding.btnApplyPromoCode, "apply");
        map.put(binding.tvDiscountTitle, "discount");

        binding.emptyPlaceHolderView.setEmptyPlaceTxtTitle(getValue("empty_cart_message"));

        return map;
    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private RaynaTicketDetailModel getTicketModel(String id) {
        if (myCartMainModel == null || myCartMainModel.getCustomTickets() == null) return null;
        return myCartMainModel.getCustomTickets().stream().filter(p -> id.equals(p.getId())).findFirst().orElse(null);
    }

    private void updateTotalAmout() {
        if (myCartMainModel == null || myCartMainModel.getItems().isEmpty()) return;

        float amount = (float) myCartMainModel.getItems().stream()
                .mapToDouble(MyCartItemsModel::getAmount)
                .sum();

        float discount = (float) myCartMainModel.getItems().stream()
                .mapToDouble(MyCartItemsModel::getDiscount)
                .sum();

        discount = Utils.convertIntoCurrenctCurrency(discount);
        amount = Utils.convertIntoCurrenctCurrency(amount);

        binding.YourSavingLayout.setVisibility(discount > 0 ? View.VISIBLE : View.GONE);
        Utils.setStyledText(activity,binding.tvYourSaving,String.valueOf(Utils.roundFloatValue(discount)));
        Utils.setStyledText(activity,binding.tvTotalDiscount,String.valueOf(Utils.roundFloatValue(discount)));
        Utils.setStyledText(activity, binding.tvPrice,String.valueOf(Utils.roundFloatValue(amount)));
    }

    private void removeItemSheet(MyCartItemsModel model, String title) {
        ArrayList<String> data = new ArrayList<>();
        data.add(getValue("remove_item"));
        Graphics.showActionSheet(activity, getValue("choose_option"), data, (data1, position1) -> {
            if (position1 == 0) {
                String message = setValue("remove_item_message", title);
                Graphics.alertDialogYesNoBtnWithUIFlag(activity, getString(R.string.app_name), message, false, getValue("no"),getValue("yes"), aBoolean -> {
                    if (aBoolean) {
                        requestRemoveCartItem(model,title);
                    }
                });

            }
        });
    }

    private JsonObject getJsonObject() {
        JsonObject object = new JsonObject();

        if (isPromoCodeApply && promoCodeModel != null){
            object.addProperty("totalAmount",promoCodeModel.getTotalAmount());
            object.addProperty("discount",promoCodeModel.getTotalDiscount());
            object.addProperty("amount",promoCodeModel.getAmount());
            object.add("promoCodeData",new Gson().toJsonTree(promoCodeModel.getMetaData()).getAsJsonArray());
            object.addProperty("promoCode", binding.edtPromoCode.getText().toString());
        }
        JsonArray idArray = new JsonArray();

        if (myCartMainModel != null && myCartMainModel.getItems() != null) {
            myCartMainModel.getItems().forEach(item -> {
                if (item.get_id() != null) {
                    idArray.add(item.get_id());
                }
            });
        }
        object.add("cartIds", idArray);
        return object;
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
        final PaymentSheet.Configuration configuration = new PaymentSheet.Configuration.Builder("Whosin, Inc.")
                .allowsDelayedPaymentMethods(true).googlePay(googlePayConfiguration).build();
        paymentSheet.presentWithPaymentIntent(model.clientSecret, configuration);
    }

    private void tabbyCheckOut(PaymentCredentialModel model) {
        TabbyPaymentBottomSheet sheet = new TabbyPaymentBottomSheet();
        sheet.paymentTabbyModel = model.getTabbyModel();
        sheet.callback = p -> {
            Double totalPrice = 0.0;
            String id;
            if (myCartMainModel != null && myCartMainModel.getItems() != null) {
                totalPrice = myCartMainModel.getItems().stream().mapToDouble(MyCartItemsModel::getAmount).sum();
                id = myCartMainModel.getItems().get(0).get_id();
            } else {
                id = "";
            }
            if (!TextUtils.isEmpty(p)) {
                switch (p) {
                    case "success":
                        LogManager.shared.logTicketEvent(LogManager.LogEventType.purchase, id, "Purchase Success", totalPrice, null, "AED");
                        SessionManager.shared.clearTicketCartData();
                        binding.myCartItemRecycleView.getRecycledViewPool().clear();
                        myCartTicketListAdapter.updateData(new ArrayList<>());
                        binding.buttonsLayout.setVisibility(View.GONE);
                        binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
                        PurchaseSuccessFragment purchaseSuccessFragment = new PurchaseSuccessFragment();
                        purchaseSuccessFragment.callBackForRaynaBooking = q -> {
                            if (q) {
                                startActivity(new Intent(Graphics.context, WalletActivity.class));
                                finish();
                            }
                        };
                        purchaseSuccessFragment.show(getSupportFragmentManager(), "");

                        break;
                    case "cancel":
                        LogManager.shared.logTicketEvent(LogManager.LogEventType.paymentCancelled, id, "Purchase Cancelled", totalPrice, null, "AED");
                        break;
                    case "failure":
                        LogManager.shared.logTicketEvent(LogManager.LogEventType.paymentFailed, id, "Purchase Failure", totalPrice, null, "AED");
                        break;
                }
            }
        };
        sheet.show(getSupportFragmentManager(), "");
    }

    private void openActionSheet(String id,RaynaTicketDetailModel raynaTicketDetailModel,String itemId,String title) {
        ArrayList<String> data = new ArrayList<>();
        data.add(getValue("edit"));
        data.add(getValue("remove_item"));
        Graphics.showActionSheet(activity, getValue("choose_option"), data, (data1, position1) -> {
            if (position1 == 0) {
                Intent intent = new Intent(activity,EditCartOptionActivity.class);
                Optional<MyCartItemsModel> tmpMyCartModel = myCartMainModel.getItems().stream().filter(p -> p.get_id().equals(itemId)).findFirst();
                Gson gson = new Gson();
                String raynaJson = gson.toJson(raynaTicketDetailModel);
                if (tmpMyCartModel.isPresent()){
                    String tourJson = gson.toJson(tmpMyCartModel.get());
                    intent.putExtra("myCartDetailModel",tourJson);
                    intent.putExtra("tourOptionId",id);
                }
                intent.putExtra("raynaTicketModel",raynaJson);

                startActivityForResult(intent,1005);
            }else {
                String message = getValue("remove_item_message");
                Graphics.alertDialogYesNoBtnWithUIFlag(activity, getString(R.string.app_name), message, false, getValue("no"), getValue("yes"), aBoolean -> {
                    if (aBoolean) {
                        JsonObject object = new JsonObject();
                        object.addProperty("id",itemId);
                        object.addProperty("optionId",id);
                        requestCartOptionRemove(object,title, itemId);
                    }
                });
            }
        });
    }

    // endregion
    // --------------------------------------
    // region Private Method For PromoCode
    // --------------------------------------

    @SuppressLint("SetTextI18n")
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

//            binding.tvApplyPromoCode.setText(promoCodeModel.getPromoDiscount() +  " AED saved with " + binding.edtPromoCode.getText().toString());
            binding.tvApplyPromoCode.setText(fullText);
            binding.tvApplyPromoCode.setVisibility(View.VISIBLE);
            binding.imgValidation.setVisibility(View.GONE);
            binding.edtPromoCode.setVisibility(View.GONE);
            binding.layoutPromoCodeApply.setVisibility(View.VISIBLE);

            Utils.setStyledText(activity,binding.tvPromoCode,String.valueOf(promoCodeModel.getPromoDiscount()));
            Utils.setStyledText(activity,binding.tvYourSaving,String.valueOf(promoCodeModel.getTotalDiscount()));

//            binding.tvPromoCode.setText( "AED " + promoCodeModel.getPromoDiscount());
//            binding.tvYourSaving.setText( "AED " + promoCodeModel.getTotalDiscount());
        }
    }

    @SuppressLint("SetTextI18n")
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
        binding.YourSavingLayout.setVisibility(View.GONE);
        updateTotalAmout();
    }

    private JsonObject getPromoJsonObject(){
        JsonObject object = new JsonObject();

        JsonArray metaDate = new JsonArray();
        for (MyCartItemsModel q : myCartMainModel.getItems()) {
            JsonObject itemMetaData = new JsonObject();
            itemMetaData.addProperty("_id", q.get_id());
            itemMetaData.addProperty("type", "ticket");
            itemMetaData.addProperty("ticketId", q.getCustomTicketId());
            itemMetaData.addProperty("amount", q.getAmount());
            itemMetaData.addProperty("discount", q.getDiscount());
            itemMetaData.addProperty("qty", 1);
            metaDate.add(itemMetaData);
        }

        object.add("metadata",metaDate);
        object.addProperty("promoCode",binding.edtPromoCode.getText().toString().trim());

        Log.d("JsonObject", "getJsonObject: " + object);
        return object;
    }


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestMyCartList(boolean isShowLoader) {
        if (isShowLoader) showProgress();
        DataService.shared(activity).requestMyCartList(new RestCallback<ContainerModel<MyCartMainModel>>(this) {
            @Override
            public void result(ContainerModel<MyCartMainModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    if (error.equals("Cart is empty!")){
                        Utils.hideViews(binding.myCartItemRecycleView, binding.buttonsLayout);
                        Utils.showViews(binding.emptyPlaceHolderView);
                        SessionManager.shared.clearTicketCartData();
                    }
                    return;
                }

                if (model.getData() != null) {
                    LogManager.shared.logTicketEvent(LogManager.LogEventType.viewCart, "view_cart", "View Cart", null, null, "AED");
                    myCartMainModel = model.getData();
                    SessionManager.shared.saveTicketCartData(model.getData());
                    if (myCartMainModel.getItems() != null && !myCartMainModel.getItems().isEmpty()) {

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
                        myCartMainModel.getItems().sort((o1, o2) -> {
                            try {
                                Date date1 = sdf.parse(o1.getCreatedAt());
                                Date date2 = sdf.parse(o2.getCreatedAt());
                                assert date2 != null;
                                return date2.compareTo(date1);
                            } catch (Exception e) {
                                return 0;
                            }
                        });


                        ArrayList<DiffIdentifier> items = new ArrayList<>();
                        items.addAll(myCartMainModel.getItems());
                        if (myCartMainModel.getContactUsBlock() != null && myCartMainModel.getContactUsBlock().isEnabled(ContactUsBlockModel.ContactBlockScreens.CART)) {
                            items.add(myCartMainModel.getContactUsBlock());
                        }
                        myCartTicketListAdapter.updateData(items);
                        updateTotalAmout();
                        Utils.showViews(binding.myCartItemRecycleView, binding.buttonsLayout);
                        Utils.hideViews(binding.emptyPlaceHolderView);
                    } else {
                        Utils.hideViews(binding.myCartItemRecycleView, binding.buttonsLayout);
                        Utils.showViews(binding.emptyPlaceHolderView);
                    }
                } else {
                    Utils.hideViews(binding.myCartItemRecycleView, binding.buttonsLayout);
                    Utils.showViews(binding.emptyPlaceHolderView);
                    SessionManager.shared.clearTicketCartData();
                }
            }
        });
    }
    private void requestRemoveCartItem(MyCartItemsModel model,String title){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id",model.get_id());
        showProgress();
        DataService.shared(activity).requestCartRemove(jsonObject, new RestCallback<ContainerModel<RaynaTicketBookingModel>>(this) {
            @Override
            public void result(ContainerModel<RaynaTicketBookingModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }
                LogManager.shared.logTicketEvent(LogManager.LogEventType.removeCart, model.getData().getId(), title, null, null, "AED");
                Alerter.create(activity)
                        .setTitle(getValue("item_removed"))
                        .setText(getValue("ticket_removed_successfully"))
                        .setTitleAppearance(R.style.AlerterTitle)
                        .setTextAppearance(R.style.AlerterText)
                        .setBackgroundColorRes(R.color.white_color)
                        .hideIcon()
                        .show();
                requestMyCartList(false);
            }
        });
    }
    private void requestCartTicketBooking(int paymentMod) {
        Double totalPrice = 0.0;
        String id;
        if (myCartMainModel != null && myCartMainModel.getItems() != null) {
            totalPrice = myCartMainModel.getItems().stream().mapToDouble(MyCartItemsModel::getAmount).sum();
            id = myCartMainModel.getItems().get(0).get_id();
        } else {
            id = "";
        }
        LogManager.shared.logTicketEvent(LogManager.LogEventType.paymentInitiated, id, "Cart Checkout", totalPrice, null, "AED");
        JsonObject object = getJsonObject();
        if (AppConstants.CARD_PAYMENT == paymentMod) {
            if (AppSettingManager.shared.getAppSettingData().isAllowStripePayments()) {
                object.addProperty("paymentMethod", "stripe");
            } else {
                object.addProperty("paymentMethod", "ngenius");
            }
        } else if (AppConstants.PAY_WITH_LINK == paymentMod) {
            object.addProperty("paymentMethod", "stripe");
        } else if (AppConstants.TABBY_PAYMENT == paymentMod) {
            object.addProperty("paymentMethod", "tabby");
        }else if (AppConstants.GOOGLE_PAY == paymentMod || AppConstants.SAMSUNG_PAY == paymentMod) {
            if (AppSettingManager.shared.getAppSettingData().isAllowStripePayments()) {
                object.addProperty("paymentMethod", "stripe");
            } else {
                object.addProperty("paymentMethod", "ngenius");
            }
        } else if (AppConstants.NGINUES_PAY == paymentMod) {
            object.addProperty("paymentMethod", "ngenius");
        }

        Log.d("object", "requestCartTicketBooking: " + object);
        showProgress();
        DataService.shared(activity).requestCartBooking(object, new RestCallback<ContainerModel<PaymentCredentialModel>>(this) {
            @Override
            public void result(ContainerModel<PaymentCredentialModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    if (error.contains("Session expired, please login again!")){
                        Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), getValue("session_expired"), aBoolean -> {
                            if (aBoolean) {
                                showProgress();
                                SessionManager.shared.logout(activity, (success, log_out_error) -> {
                                    hideProgress();
                                    if (!Utils.isNullOrEmpty(log_out_error)) {
                                        Toast.makeText(activity, log_out_error, Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    startActivity( new Intent( activity, AuthenticationActivity.class ).addFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK ).addFlags( Intent.FLAG_ACTIVITY_NEW_TASK ) );
                                    finish();
                                });
                            }
                        });
                    }else {
                        Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), error);
                    }
                    return;
                }

                if (model.getData() != null) {
                    if (AppConstants.CARD_PAYMENT == paymentMod) {
                        if (AppSettingManager.shared.getAppSettingData().isAllowStripePayments()) {
                            startStripeCheckOut(model.getData());
                        } else {
                            startNginiusGpayPayment(model.getData(), false);
                        }
                    } else if (AppConstants.PAY_WITH_LINK == paymentMod) {
                        startStripeCheckOut(model.getData());
                    }else if (AppConstants.GOOGLE_PAY == paymentMod || AppConstants.SAMSUNG_PAY == paymentMod) {
                        if (AppSettingManager.shared.getAppSettingData().isAllowStripePayments()) {
                            startGooglePayCheckOut(model.getData());
                        } else {
                            startNginiusGpayPayment(model.getData(), true);
                        }
                    } else if (AppConstants.TABBY_PAYMENT == paymentMod) {
                        if (Utils.isAvailableTabby(model.getData().getTabbyModel())) {
                            tabbyCheckOut(model.getData());
                        } else {
                            Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), getValue("tabby_payment_failed"));
                        }
                    } else if (AppConstants.NGINUES_PAY == paymentMod) {
                        startNgeniusPayment(model.getData());
                    }

                }
            }
        });
    }
    private void startNginiusGpayPayment(PaymentCredentialModel model, boolean isGooglePay) {
        PaymentCredentialModel.LinksModel links = model.getLinks();
        String authUrl = links.paymentAuthorization.href;
        String paymentUrl = links.payment.href;

        GooglePayConfig googlePayConfig = new GooglePayConfig(
                GooglePayConfig.Environment.Production,
                false,
                new GooglePayConfig.BillingAddressConfig(),
                "BCR2DN4TW7MZ7T3F"
        );

        PaymentsRequest.Builder paymentRequest = new PaymentsRequest.Builder();
        paymentRequest.payPageUrl(paymentUrl);
        paymentRequest.gatewayAuthorizationUrl(authUrl);
        if (isGooglePay) {
            paymentRequest.setGooglePayConfig(googlePayConfig);
        }

        paymentsLauncher.launch(paymentRequest.build());
    }
    private void startNgeniusPayment(PaymentCredentialModel model) {

        PaymentCredentialModel.LinksModel links = model.getLinks();

        if (links == null || links.payment == null || links.paymentAuthorization == null) {
            Toast.makeText(this, "Invalid payment data", Toast.LENGTH_SHORT).show();
            return;
        }

        String paymentUrl = links.payment.href;
        String authUrl = links.paymentAuthorization.href;

        String code = extractCode(paymentUrl);

        if (code == null) {
            Toast.makeText(this, "Invalid payment URL", Toast.LENGTH_SHORT).show();
            return;
        }

        CardPaymentRequest.Builder builder = new CardPaymentRequest.Builder();
        builder.code(code);
        builder.gatewayUrl(authUrl);
        PaymentClient client = new PaymentClient(this, model.getId());

        client.launchCardPayment(builder.build(), 123);
    }

    private String extractCode(String url) {
        if (url == null) return null;

        try {
            Uri uri = Uri.parse(url);
            return uri.getQueryParameter("code");
        } catch (Exception e) {
            return null;
        }
    }
    private void onNGenuesCardPaymentResponse(CardPaymentData data) {
        hideProgress();
        Double totalPrice = 0.0;
        String id;
        if (myCartMainModel != null && myCartMainModel.getItems() != null) {
            totalPrice = myCartMainModel.getItems().stream().mapToDouble(MyCartItemsModel::getAmount).sum();
            id = myCartMainModel.getItems().get(0).get_id();
        } else {
            id = "";
        }
        switch (data.getCode()) {
            case CardPaymentData.STATUS_PAYMENT_AUTHORIZED, CardPaymentData.STATUS_PAYMENT_CAPTURED:
                LogManager.shared.logTicketEvent(LogManager.LogEventType.purchase, id, "Purchase Success", totalPrice, null, "AED");
                PurchaseSuccessFragment purchaseSuccessFragment = new PurchaseSuccessFragment();
                purchaseSuccessFragment.callBackForRaynaBooking = data1 -> {
                    if (data1) {
                        startActivity(new Intent(Graphics.context, WalletActivity.class));
                        RaynaTicketManager.shared.finishAllActivities();
                        RaynaTicketManager.shared.clearManager();
                        finish();

                    }
                };
                purchaseSuccessFragment.show(getSupportFragmentManager(), "");
            case CardPaymentData.STATUS_PAYMENT_FAILED:
                LogManager.shared.logTicketEvent(LogManager.LogEventType.paymentFailed, id, "Purchase Failed", totalPrice, null, "AED");
                Toast.makeText(activity, "Payment failed, Please try again.", Toast.LENGTH_SHORT).show();
                break;
            case CardPaymentData.STATUS_GENERIC_ERROR:
                LogManager.shared.logTicketEvent(LogManager.LogEventType.paymentFailed, id, "Purchase Cancel", totalPrice, null, "AED");
                Toast.makeText(activity, "Something went wrong while payment process, Please try again.", Toast.LENGTH_SHORT).show();
                break;
            default:
                throw new IllegalArgumentException("Unknown payment response (" + data.getReason() + ")");
        }
    }
    private void requestCartOptionRemove(JsonObject object,String title, String id){
        showProgress();
        DataService.shared(activity).requestCartOptionRemove(object, new RestCallback<ContainerModel<RaynaTicketBookingModel>>(this) {
            @Override
            public void result(ContainerModel<RaynaTicketBookingModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }
                LogManager.shared.logTicketEvent(LogManager.LogEventType.removeCart,id , title, 0.0, null, "AED");
                Alerter.create(activity)
                        .setTitle(getValue("item_removed"))
                        .setText(getValue("option_removed_successfully"))
                        .setTitleAppearance(R.style.AlerterTitle)
                        .setTextAppearance(R.style.AlerterText)
                        .setBackgroundColorRes(R.color.white_color)
                        .hideIcon()
                        .show();
                requestMyCartList(false);
            }
        });
    }

    private void requestPromoCode() {
        if (getPromoJsonObject().isEmpty() || getPromoJsonObject().isJsonNull()) return;
        binding.promoCodeProgressView.setVisibility(View.VISIBLE);
        binding.btnApplyPromoCode.setVisibility(View.GONE);
        DataService.shared(activity).requestVenuePromoCode(getPromoJsonObject(), new RestCallback<ContainerModel<VenuePromoCodeModel>>(this) {
            @SuppressLint("SetTextI18n")
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

//                    binding.promoCodeLinear.setVisibility(View.VISIBLE);


                    binding.YourSavingLayout.setVisibility(model.getData().getTotalDiscount() > 0 ? View.VISIBLE : View.GONE);
                    Utils.setStyledText(activity,binding.tvPrice,Utils.roundFloatValue(Float.valueOf(model.getData().getAmount())));
//                    binding.finalAmountAed.setText("AED " + String.valueOf(model.getData().getMetaData().get(0).getFinalAmount()));
//                    binding.promoCodeAed.setText("AED " + model.getData().getPromoDiscount());
//                    binding.tvTotalDiscount.setText("AED " + model.getData().getItemsDiscount());
//                    binding.discountAed.setText("AED " + Utils.roundDoubleValueToDouble(model.getData().getItemsDiscount()));

//                    Utils.setStyledText(activity,binding.finalAmountAed,String.valueOf(model.getData().getMetaData().get(0).getFinalAmount()));
//                    Utils.setStyledText(activity,binding.promoCodeAed,String.valueOf(model.getData().getPromoDiscount()));
                    Utils.setStyledText(activity,binding.tvTotalDiscount,String.valueOf(model.getData().getItemsDiscount()));
//                    Utils.setStyledText(activity,binding.discountAed,String.valueOf(Utils.roundDoubleValueToDouble(model.getData().getItemsDiscount())));

                }

            }
        });
    }

    private void requestRemoveCartSubscription() {
        showProgress();
        DataService.shared(activity).requestRemoveCartSubscription(new RestCallback<ContainerModel<MyCartMainModel>>(this) {
            @Override
            public void result(ContainerModel<MyCartMainModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }
                resetPromoCodeLayout();
            }
        });
    }


    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    private class MyCartTicketListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {
        private static final int VIEW_TYPE_ITEM = 0;
        private static final int VIEW_TYPE_CONTACT_US = 1;

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == VIEW_TYPE_CONTACT_US) {
                return new ContactUsViewHolder(UiUtils.getViewBy(parent, R.layout.item_contact_us_block));
            }
            View view = UiUtils.getViewBy(parent, R.layout.my_cart_item_design);
            return new ViewHolder(view);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (getItemViewType(position) == VIEW_TYPE_CONTACT_US) {
                ContactUsBlockModel model = (ContactUsBlockModel) getItem(position);
                ContactUsViewHolder contactUsViewHolder = (ContactUsViewHolder) holder;
                ContactUsBlockManager.setupContactUsBlock(activity, contactUsViewHolder.binding, model, ContactUsBlockModel.ContactBlockScreens.CART);
                return;
            }
            ViewHolder viewHolder = (ViewHolder) holder;
            MyCartItemsModel model = (MyCartItemsModel) getItem(position);
            if (model == null) return;

            RaynaTicketDetailModel raynaTicketDetailModel = getTicketModel(model.getCustomTicketId());

            if (raynaTicketDetailModel != null){
                raynaTicketDetailModel.assignTourObject();
                viewHolder.binding.tvTicketName.setText(raynaTicketDetailModel.getTitle());

                String description = raynaTicketDetailModel.getDescription();
                if (!TextUtils.isEmpty(description)) {
                    viewHolder.binding.tvTicketDescription.setText(Html.fromHtml(description, Html.FROM_HTML_MODE_LEGACY));
                } else {
                    viewHolder.binding.tvTicketDescription.setText("");
                }

                if (raynaTicketDetailModel.getImages() != null && !raynaTicketDetailModel.getImages().isEmpty()) {
                    for (String image : raynaTicketDetailModel.getImages()) {
                        if (!Utils.isVideo(image)) {
                            Graphics.loadImage(image, viewHolder.binding.imgGallary);
                            break;
                        }
                    }
                }

                viewHolder.setUpData(raynaTicketDetailModel,model);
            }

//            viewHolder.bindAmount(viewHolder.binding.ticketTotalAmount, model.getTotalAmount(), "Total Amount");
            viewHolder.bindAmount(viewHolder.binding.ticketDiscountAmount, model.getDiscount(), getValue("discount"));
            float totalAddonPrice =
                    (float) model.getTourDetails()
                            .stream()
                            .filter(td -> td.getAddons() != null)
                            .flatMap(td -> td.getAddons().stream())
                            .mapToDouble(MyCartTourDetailsModel::getWhosinTotal)
                            .sum();
            float ticketAmount = (float) model.getTourDetails()
                    .stream()
                    .mapToDouble(MyCartTourDetailsModel::getWhosinTotal)
                    .sum();
            viewHolder.bindAmount(viewHolder.binding.ticketTotalAmount, Utils.convertIntoCurrenctCurrency(ticketAmount), getValue("Ticket Amount"));
            viewHolder.bindAmount(viewHolder.binding.ticketAddOnAmount, totalAddonPrice, getValue("Add-On Amount"));
//            viewHolder.bindAmount(viewHolder.binding.ticketFinalAmount, model.getAmount(), "Final Amount");
            viewHolder.bindAmount(viewHolder.binding.ticketFinalAmount,Utils.convertIntoCurrenctCurrency( model.getAmount()), getValue("final_amount"));


            viewHolder.binding.iconMenu.setOnClickListener(v -> removeItemSheet(model,viewHolder.binding.tvTicketName.getText().toString()));

        }

        @Override
        public int getItemViewType(int position) {
            if (getItem(position) instanceof ContactUsBlockModel) {
                return VIEW_TYPE_CONTACT_US;
            }
            return VIEW_TYPE_ITEM;
        }

        @Override
        public int getItemCount() {
            return super.getItemCount();
        }

        public class ContactUsViewHolder extends RecyclerView.ViewHolder {
            final ItemContactUsBlockBinding binding;
            public ContactUsViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemContactUsBlockBinding.bind(itemView);
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final MyCartItemDesignBinding binding;

            private TourBookingAdapter<MyCartTourDetailsModel> tourBookingAdapter;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = MyCartItemDesignBinding.bind(itemView);
            }

            private void setUpData(RaynaTicketDetailModel raynaTicketDetailModel,MyCartItemsModel model){
                tourBookingAdapter = new TourBookingAdapter<>(raynaTicketDetailModel,model.get_id());
                binding.ticketRecycle.setLayoutManager(new LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false));
                binding.ticketRecycle.setAdapter(tourBookingAdapter);
                tourBookingAdapter.updateData(model.getTourDetails());
            }


            private void bindAmount(MyCartPriceLayoutBinding binding, float value, String title) {
                if (value != 0) {
                    binding.getRoot().setVisibility(View.VISIBLE);
                    binding.tvPriceTitle.setText(title);
                    Utils.setStyledText(activity, binding.tvPrice, Utils.roundFloatValue(value));
                } else {
                    binding.getRoot().setVisibility(View.GONE);
                }
            }
        }
    }

    private class TourBookingAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        private RaynaTicketDetailModel raynaTicketDetailModel;

        private String itemID = "";

        private TourBookingAdapter(RaynaTicketDetailModel raynaTicketDetailModel,String id){
            this.raynaTicketDetailModel = raynaTicketDetailModel;
            this.itemID = id;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = UiUtils.getViewBy(parent, R.layout.item_tour_detail_for_cart_view);
            ViewGroup.LayoutParams params = view.getLayoutParams();
            view.setLayoutParams(params);
            return new ViewHolder(view);

        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            if (activity == null) {
                return;
            }
            ViewHolder viewHolder = (ViewHolder) holder;
            MyCartTourDetailsModel model = (MyCartTourDetailsModel) getItem(position);
            if (model == null) return;


            TourOptionDetailModel tourOptionDetailModel = viewHolder.getTourDataModel(model.getOptionId());
            BigBusOptionsItemModel bigBusOptionModel = viewHolder.getBigBusTourDataModel(model.getOptionId());

            if (tourOptionDetailModel != null){
                if (raynaTicketDetailModel.getBookingType().equals("rayna")){
                    viewHolder.mBinding.tvOptionName.setText(Utils.notNullString(tourOptionDetailModel.getOptionName()));
                    String description = tourOptionDetailModel.getOptionDescription();
                    if (!TextUtils.isEmpty(description)) {
                        viewHolder.mBinding.tvOptionDescription.setText(Html.fromHtml(description, Html.FROM_HTML_MODE_LEGACY));
                    } else {
                        viewHolder.mBinding.tvOptionDescription.setText("");
                    }
                } else if (raynaTicketDetailModel.getBookingType().equals("whosin-ticket")) {
                    viewHolder.mBinding.tvOptionName.setText(Utils.notNullString(tourOptionDetailModel.getDisplayName()));

                    String description = tourOptionDetailModel.getOptionDescription();
                    if (!TextUtils.isEmpty(description)) {
                        viewHolder.mBinding.tvOptionDescription.setText(Html.fromHtml(description, Html.FROM_HTML_MODE_LEGACY));
                    } else {
                        viewHolder.mBinding.tvOptionDescription.setText("");
                    }

                } else {

                    if (raynaTicketDetailModel.getBookingType().equals("whosin")){
                        viewHolder.mBinding.tvOptionName.setText(Utils.notNullString(tourOptionDetailModel.getTitle()));
                    }else {
                        viewHolder.mBinding.tvOptionName.setText(Utils.notNullString(tourOptionDetailModel.getName()));
                    }


                    String description = tourOptionDetailModel.getDescription();
                    if (!TextUtils.isEmpty(description)) {
                        viewHolder.mBinding.tvOptionDescription.setText(Html.fromHtml(description, Html.FROM_HTML_MODE_LEGACY));
                    } else {
                        viewHolder.mBinding.tvOptionDescription.setText("");
                    }

                }
            } else if (bigBusOptionModel != null) {
                if (raynaTicketDetailModel.getBookingType().equals("octo") || raynaTicketDetailModel.getBookingType().equals("big-bus")
                        || raynaTicketDetailModel.getBookingType().equals("hero-balloon")) {
                    viewHolder.mBinding.tvOptionName.setText(Utils.notNullString(bigBusOptionModel.getTitle()));
                    String description = bigBusOptionModel.getShortDescription();
                    viewHolder.mBinding.tvOptionDescription.setText(!TextUtils.isEmpty(description)
                            ? Html.fromHtml(description, Html.FROM_HTML_MODE_LEGACY)
                            : "");
                }
            }

            viewHolder.mBinding.tvTime.setText(model.getTimeSlot());


            String guestName = Utils.setLangValue("numberOfPax",String.valueOf(model.getAdult()),model.getAdultTitle(),String.valueOf(model.getChild()),model.getChildTitle(),String.valueOf(model.getInfant()),model.getInfantTitle());
            viewHolder.mBinding.tvGuestDetails.setText(guestName);
            String date = Utils.changeDateFormat(model.getTourDate(), AppConstants.DATEFORMAT_LONG_TIME,AppConstants.DATEFORMT_EEE_d_MMM_yyyy);
            viewHolder.mBinding.tvDate.setText(date);

//            Utils.setStyledText(activity,viewHolder.mBinding.tvPrice,Utils.roundFloatValue(model.getWhosinTotal()));
            Utils.setStyledText(activity,viewHolder.mBinding.tvPrice,Utils.roundFloatValue(Utils.convertIntoCurrenctCurrency(model.getWhosinTotal())));
            if (!model.getAddons().isEmpty()) {
                viewHolder.mBinding.addOnLayout.setVisibility(View.VISIBLE);
                CartAddOnAdapter<MyCartTourDetailsModel> adapter = new CartAddOnAdapter<>();
                viewHolder.mBinding.recyclerViewAddOns.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
                viewHolder.mBinding.recyclerViewAddOns.setAdapter(adapter);
                viewHolder.mBinding.addOnAmountView.setVisibility(View.VISIBLE);
                float totalAddonPrice =
                        (float) model.getAddons()
                                .stream()
                                .filter(Objects::nonNull)
                                .mapToDouble(MyCartTourDetailsModel::getWhosinTotal)
                                .sum();

                Utils.setStyledText(activity,viewHolder.mBinding.tvAddonAmountAED,Utils.roundFloatValue(Utils.convertIntoCurrenctCurrency(totalAddonPrice)));
                adapter.updateData(model.getAddons());
            } else {
                viewHolder.mBinding.addOnLayout.setVisibility(View.GONE);
                viewHolder.mBinding.addOnAmountView.setVisibility(View.GONE);
            }

            viewHolder.mBinding.iconMenu.setOnClickListener(v -> openActionSheet(model.getOptionId(),raynaTicketDetailModel,itemID,viewHolder.mBinding.tvOptionName.getText().toString()));

        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final ItemTourDetailForCartViewBinding mBinding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemTourDetailForCartViewBinding.bind(itemView);
            }

            private TourOptionDetailModel getTourDataModel(String optionId) {
                if (raynaTicketDetailModel == null || raynaTicketDetailModel.getOptions() == null) return null;

                String bookingType = raynaTicketDetailModel.getBookingType();
                return raynaTicketDetailModel.getOptions().stream()
                        .filter(option -> {
                            switch (bookingType) {
                                case "rayna":
                                case "whosin-ticket":
                                    return optionId.equals(option.getTourOptionId());
                                case "whosin":
                                    return optionId.equals(String.valueOf(option.getId()));
                                default:
                                    return optionId.equals(String.valueOf(option.getMyCartId()));
                            }
                        })
                        .findFirst()
                        .orElse(null);
            }

            private BigBusOptionsItemModel getBigBusTourDataModel(String optionId) {
                if (raynaTicketDetailModel == null || raynaTicketDetailModel.getBigBusTourDataModels() == null)
                    return null;

                return raynaTicketDetailModel.getBigBusTourDataModels().stream()
                        .filter(bigBusModel -> bigBusModel.getOptions() != null)
                        .flatMap(bigBusModel -> bigBusModel.getOptions().stream())
                        .filter(opt -> optionId.equals(opt.getId()))
                        .findFirst()
                        .orElse(null);
            }
        }


    }

    // --------------------------------------
    // endregion
}
