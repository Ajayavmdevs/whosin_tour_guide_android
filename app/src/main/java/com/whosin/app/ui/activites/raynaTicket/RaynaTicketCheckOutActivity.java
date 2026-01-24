package com.whosin.app.ui.activites.raynaTicket;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Paint;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.samsung.android.sdk.samsungpay.v2.SamsungPay;
import com.samsung.android.sdk.samsungpay.v2.SamsungPayBase;
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
import com.whosin.app.databinding.ActivityRaynaTicketCheckOutBinding;
import com.whosin.app.databinding.ItemCheckoutTourOptionsDetailsBinding;
import com.whosin.app.databinding.ItemGuestDetailBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.AppSettingManager;
import com.whosin.app.service.manager.LogManager;
import com.whosin.app.service.manager.RaynaTicketManager;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.BigBusModels.BigBusOptionsItemModel;
import com.whosin.app.service.models.BigBusModels.BigBusUnitsItemModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.PaymentCredentialModel;
import com.whosin.app.service.models.RatingModel;
import com.whosin.app.service.models.TravelDeskModels.TravelDeskOptionDataModel;
import com.whosin.app.service.models.VenuePromoCodeModel;
import com.whosin.app.service.models.rayna.RaynaPassengerModel;
import com.whosin.app.service.models.rayna.RaynaTicketBookingModel;
import com.whosin.app.service.models.rayna.RaynaTicketDetailModel;
import com.whosin.app.service.models.rayna.TourOptionsModel;
import com.whosin.app.service.models.whosinTicketModel.WhosinTicketTourOptionModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.auth.AuthenticationActivity;
import com.whosin.app.ui.activites.cartManagement.TicketCartActivity;
import com.whosin.app.ui.activites.comman.BaseActivity;
import com.whosin.app.ui.activites.home.activity.SeeAllRatingReviewActivity;
import com.whosin.app.ui.activites.raynaTicket.BottomSheets.CancellationPolicyBottomSheet;
import com.whosin.app.ui.activites.raynaTicket.BottomSheets.RaynaMoreInfoBottomSheet;
import com.whosin.app.ui.activites.raynaTicket.BottomSheets.ReadMoreBottomSheet;
import com.whosin.app.ui.activites.raynaTicket.BottomSheets.SelectPaymentOptionBottomSheet;
import com.whosin.app.ui.activites.raynaTicket.BottomSheets.TabbyPaymentBottomSheet;
import com.whosin.app.ui.activites.wallet.WalletActivity;
import com.whosin.app.ui.adapter.AddOnAdapter;
import com.whosin.app.ui.adapter.raynaTicketAdapter.RaynaGalleryAdapter;
import com.whosin.app.ui.fragment.wallet.PurchaseSuccessFragment;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import payment.sdk.android.PaymentClient;
import payment.sdk.android.cardpayment.CardPaymentData;
import payment.sdk.android.cardpayment.CardPaymentRequest;
import payment.sdk.android.googlepay.GooglePayConfig;
import payment.sdk.android.payments.PaymentsLauncher;
import payment.sdk.android.payments.PaymentsRequest;
import payment.sdk.android.payments.PaymentsResult;
import payment.sdk.android.payments.PaymentsResultCallback;
import payment.sdk.android.samsungpay.SamsungPayClient;


public class RaynaTicketCheckOutActivity extends BaseActivity {

    private ActivityRaynaTicketCheckOutBinding binding;
    private final GuestDetailAdapter<RaynaPassengerModel> guestDetailAdapter = new GuestDetailAdapter<>();
    private final TourOptionDetailsAdapter<TourOptionsModel> tourOptionDetailsAdapter = new TourOptionDetailsAdapter<>();
    private final WhosinCustomTourOptionDetailsAdapter<WhosinTicketTourOptionModel> whosinCustomTourOptionDetailsAdapter = new WhosinCustomTourOptionDetailsAdapter<>();
    private final BigBusTourOptionDetailsAdapter<BigBusOptionsItemModel> bigBusTourOptionDetailsAdapter = new BigBusTourOptionDetailsAdapter<>();
    private final TravelDeskTourOptionDetailsAdapter<TravelDeskOptionDataModel> travelDeskTourOptionDetailsAdapter = new TravelDeskTourOptionDetailsAdapter<>();
    private RaynaTicketDetailModel raynaTicketDetailModel = RaynaTicketManager.shared.raynaTicketDetailModel;
    private boolean isPromoCodeApply = false;
    private VenuePromoCodeModel promoCodeModel = null;
    private boolean isShowBtn = false;
    private PaymentSheet paymentSheet;
    private boolean isWhosinTypeTicket = false;
    private static final int NGENIUS_REQUEST_CODE = 3001;

    private PaymentsLauncher paymentsLauncher;


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    protected void initUi() {

        applyTranslations();
        paymentsLauncher = new PaymentsLauncher(this, paymentsResult -> {
            if(paymentsResult instanceof PaymentsResult.Authorised || paymentsResult instanceof PaymentsResult.Success) {
                PurchaseSuccessFragment purchaseSuccessFragment = new PurchaseSuccessFragment();
                purchaseSuccessFragment.callBackForRaynaBooking = data1 -> {
                    if (data1) {
                        LogManager.shared.logTicketEvent(LogManager.LogEventType.purchase, "purchase_success", "Purchase Success", null, null, "AED");
                        startActivity(new Intent(Graphics.context, WalletActivity.class));
                        RaynaTicketManager.shared.finishAllActivities();
                        RaynaTicketManager.shared.clearManager();
                        finish();

                    }
                };
                purchaseSuccessFragment.show(getSupportFragmentManager(), "");
            }
            else if (paymentsResult instanceof PaymentsResult.Failed){
                LogManager.shared.logTicketEvent(LogManager.LogEventType.paymentFailed, "purchase_failure", "Purchase Failure", null, null, "AED");
                String error = ((PaymentsResult.Failed) paymentsResult).getError();
                Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
            } else {
                LogManager.shared.logTicketEvent(LogManager.LogEventType.paymentFailed, "purchase_failure", "Purchase Failure", null, null, "AED");
                Toast.makeText(activity, "Payment failed, Please try again.", Toast.LENGTH_SHORT).show();
            }
        });

        paymentSheet = new PaymentSheet(RaynaTicketCheckOutActivity.this, paymentSheetResult -> {
            if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
                LogManager.shared.logTicketEvent(LogManager.LogEventType.paymentCancelled, "purchase_cancelled", "Purchase Cancelled", null, null, "AED");
                Log.d("TAG", "Canceled");
            } else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
                LogManager.shared.logTicketEvent(LogManager.LogEventType.paymentFailed, "purchase_failure", "Purchase Failure", null, null, "AED");
                Log.e("TAG", "Got error: ", ((PaymentSheetResult.Failed) paymentSheetResult).getError());
            } else if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
                PurchaseSuccessFragment purchaseSuccessFragment = new PurchaseSuccessFragment();
                purchaseSuccessFragment.callBackForRaynaBooking = data -> {
                    if (data) {
                        LogManager.shared.logTicketEvent(LogManager.LogEventType.purchase, "purchase_success", "Purchase Success", null, null, "AED");
                        startActivity(new Intent(Graphics.context, WalletActivity.class));
                        RaynaTicketManager.shared.finishAllActivities();
                        RaynaTicketManager.shared.clearManager();
                        finish();

                    }
                };
                purchaseSuccessFragment.show(getSupportFragmentManager(), "");

            }
        });

        setDetail();

        RaynaTicketManager.shared.activityList.add(activity);

        binding.constraintHeader.tvTitle.setText(getValue("checkout"));

        binding.mainConstraint.setVisibility(View.VISIBLE);

        binding.guestDetailView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        binding.guestDetailView.setAdapter(guestDetailAdapter);


        binding.tourOptionRecycleView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        if (raynaTicketDetailModel != null){
            switch (raynaTicketDetailModel.getBookingType()) {
                case "travel-desk":
                    binding.tourOptionRecycleView.setAdapter(travelDeskTourOptionDetailsAdapter);
                    travelDeskTourOptionDetailsAdapter.updateData(RaynaTicketManager.shared.selectTravelDeskOptionDataModels);
                    break;
                case "whosin-ticket":
                    binding.tourOptionRecycleView.setAdapter(whosinCustomTourOptionDetailsAdapter);
                    whosinCustomTourOptionDetailsAdapter.updateData(RaynaTicketManager.shared.selectedTourModelForWhosin);
                    break;
                case "big-bus":
                case "hero-balloon":
                    binding.tourOptionRecycleView.setAdapter(bigBusTourOptionDetailsAdapter);
                    bigBusTourOptionDetailsAdapter.updateData(RaynaTicketManager.shared.selectedTourModelForBigBus);
                    break;
                default:
                    binding.tourOptionRecycleView.setAdapter(tourOptionDetailsAdapter);
                    tourOptionDetailsAdapter.updateData(RaynaTicketManager.shared.selectedTourModel);
                    break;
            }
        }



        Gson gson = new Gson();
        Type passengerListType = new TypeToken<List<RaynaPassengerModel>>() {}.getType();
        String passengersJson = gson.toJson(gson.fromJson(RaynaTicketManager.shared.object, Map.class).get("passengers"));
        List<RaynaPassengerModel> passengers = gson.fromJson(passengersJson, passengerListType);
        guestDetailAdapter.updateData(passengers);

        updatePrices();

    }

    @Override
    protected void setListeners() {

        binding.constraintHeader.ivClose.setOnClickListener(view -> {
            Utils.preventDoubleClick(view);
            finish();
        });

        binding.btnBackBtn.setOnClickListener(view -> {
            Utils.preventDoubleClick(view);
            finish();
        });

        binding.btnCheckOut.setOnClickListener(view -> {
            Utils.preventDoubleClick(view);

            if (raynaTicketDetailModel != null) {
                Double price = 0.0;
                try {
                    Object amount = raynaTicketDetailModel.getStartingAmount();
                    if (amount instanceof Number) {
                        price = ((Number) amount).doubleValue();
                    } else if (amount instanceof String) {
                        price = Double.parseDouble((String) amount);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                LogManager.shared.logTicketEvent(LogManager.LogEventType.paymentInitiated, raynaTicketDetailModel.getId(), raynaTicketDetailModel.getTitle(), price, null, "AED");
            }

            if (Utils.isGuestLogin()) {
                Graphics.showAlertDialogWithOkCancel(activity, getString(R.string.app_name), "Are you sure want to continue\nwith guest login?", "Continue", "Login", isConfirmed -> {
                    if (isConfirmed) {
                        openPaymentSelectSheet();
                    } else {
                        Intent intent = new Intent(this, AuthenticationActivity.class);
                        intent.putExtra("isGuestLogin", true);
                        activityLauncher.launch(intent, result -> {
                            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                                openPaymentSelectSheet();
                            }
                        });
                    }
                });
            } else {
                openPaymentSelectSheet();
            }
        });

        binding.btnApplyPromoCode.setOnClickListener(view -> {
            Utils.preventDoubleClick(view);
            if (binding.btnApplyPromoCode.getText().toString().equals(getValue("remove"))){
                resetPromoCodeLayout();
                return;
            }
            if (TextUtils.isEmpty(binding.edtPromoCode.getText().toString())){
                Toast.makeText(this, getValue("please_enter_promo_code"), Toast.LENGTH_SHORT).show();
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


        binding.btnAddToCart.setOnClickListener(v -> {
            String id = "";
            String title = "";
            Double price = 0.0;
            if (raynaTicketDetailModel != null) {
                id = raynaTicketDetailModel.getId();
                title = raynaTicketDetailModel.getTitle();
                try {
                    Object amount = raynaTicketDetailModel.getStartingAmount();
                    if (amount instanceof Number) {
                        price = ((Number) amount).doubleValue();
                    } else if (amount instanceof String) {
                        price = Double.parseDouble((String) amount);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            LogManager.shared.logTicketEvent(LogManager.LogEventType.addToCart, id, title, price, null, "AED");
            requestCartAdd();
        });

    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityRaynaTicketCheckOutBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        RaynaTicketManager.shared.activityList.remove(activity);
    }

    @Override
    protected Map<View, String> getTranslationMap() {
        Map<View, String> map = new HashMap<>();
        map.put(binding.tvGuestTitle, "guest_details");
        map.put(binding.btnBackBtn, "back");
        map.put(binding.btnAddToCart, "addToCart");
        map.put(binding.tvNextStep, "checkOut");
        map.put(binding.edtPromoCode, "enter_promo_code");

        map.put(binding.tvTotalAmout, "total_amount");
        map.put(binding.discountTv, "discount");
        map.put(binding.tvPromoCodeTitle, "promo_code");
        map.put(binding.finalAmountTv, "final_amount");

        map.put(binding.tvTotalSavingTitle, "saved_with");
        map.put(binding.tvPromoCodeTitleForPromoLayout, "promo_code");
        map.put(binding.tvDiscountTitleForPromoLayout, "discount");
        map.put(binding.pricePerTv, "price_per_trip");

        map.put(binding.btnApplyPromoCode, "apply");
        return map;
    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------


    @SuppressLint("SetTextI18n")
    private void setDetail() {

        if (raynaTicketDetailModel == null) return;

        isWhosinTypeTicket = raynaTicketDetailModel.getBookingType().equals("whosin");

        binding.tvTickeTitle.setText(raynaTicketDetailModel.getTitle());

        if (!raynaTicketDetailModel.getImages().isEmpty()) {
            Graphics.loadImage(raynaTicketDetailModel.getImages().get(0), binding.ivTicketImage);
        }

        String description = raynaTicketDetailModel.getDescription();
        String text = description.replaceAll("<[^>]*>", "").trim();
        if (!TextUtils.isEmpty(text)){
            binding.tvTickeDescription.setVisibility(View.VISIBLE);
        }else {
            binding.tvTickeDescription.setVisibility(View.GONE);
        }

        binding.tvTickeDescription.setText(Html.fromHtml(raynaTicketDetailModel.getDescription()));

        Utils.addSeeMore(binding.tvTickeDescription, Html.fromHtml(raynaTicketDetailModel.getDescription()), 3, "... See More" , v -> {
            ReadMoreBottomSheet bottomSheet = new ReadMoreBottomSheet();
            bottomSheet.title = getValue("description");
            bottomSheet.formattedDescription = raynaTicketDetailModel.getDescription();
            bottomSheet.show(getSupportFragmentManager(),"");
        });

    }

    private void openPaymentSelectSheet(){
        SelectPaymentOptionBottomSheet bottmSheet = new SelectPaymentOptionBottomSheet();
        bottmSheet.isFromRaynaTicket = true;

        double checkTabbyAmount = 0;

        AtomicReference<Float> finalAmount = new AtomicReference<>(0f);

        if (isPromoCodeApply && promoCodeModel != null) {
            checkTabbyAmount = Utils.roundDoubleValueToDouble(promoCodeModel.getMetaData().get(0).getFinalAmount());
        } else {
            AtomicReference<Float> pricePerTrip = new AtomicReference<>(0f);
            switch (raynaTicketDetailModel.getBookingType()) {
                case "travel-desk":
                    RaynaTicketManager.shared.selectTravelDeskOptionDataModels.forEach(q -> {
                        finalAmount.set(finalAmount.get() + getWhosinTotalForTravelDesk(q));
                        pricePerTrip.set(pricePerTrip.get() + q.getPricePerTrip());
                    });
                    finalAmount.set(finalAmount.get() + pricePerTrip.get());
                    break;
                case "big-bus":
                case "hero-balloon":
                    RaynaTicketManager.shared.selectedTourModelForBigBus.forEach(q -> finalAmount.set(finalAmount.get() + getWhosinTotalForBigBus(q)));
                    break;
                case "whosin-ticket":
                    RaynaTicketManager.shared.selectedTourModelForWhosin.forEach(q -> finalAmount.set(finalAmount.get() + getWhosinCustomTicketTotal(q)));
                    break;
                default:
                    RaynaTicketManager.shared.selectedTourModel.forEach(q -> finalAmount.set(finalAmount.get() + getWhosinTotal(q)));
                    break;
            }

            checkTabbyAmount = finalAmount.get();
        }
        bottmSheet.amount = checkTabbyAmount;
        bottmSheet.callback = this::requestRaynaTourBooking;
        bottmSheet.show(getSupportFragmentManager(), "");
    }

    private float getServiceTotal(TourOptionsModel model) {
        float adult = model.getTmpAdultValue() * model.getAdultPriceRayna();
        float child = model.getTmpChildValue() * model.getChildPriceRayna();
        float infants = model.getTmpInfantValue() * model.getInfantPriceRayna();

        return Utils.roundFloatToFloat(adult + child + infants);
    }

    private float getServiceTotalForWhosinType(TourOptionsModel model) {
        float adult = model.getTmpAdultValue() * model.getWithoutDiscountAdultPrice();
        float child = model.getTmpChildValue() * model.getWithoutDiscountChildPrice();
        float infants = model.getTmpInfantValue() * model.getWithoutDiscountInfantPrice();

        return Utils.roundFloatToFloat(adult + child + infants);
    }

    private float getServiceTotalForWhosinCustomType(WhosinTicketTourOptionModel model) {
        float adult = model.getTmpAdultValue() * model.getWithoutDiscountAdultPrice();
        float child = model.getTmpChildValue() * model.getWithoutDiscountChildPrice();
        float infants = model.getTmpInfantValue() * model.getWithoutDiscountInfantPrice();

        return Utils.roundFloatToFloat(adult + child + infants);
    }

    private float getWhosinTotal(TourOptionsModel model) {
        float adult = model.getTmpAdultValue() *  Utils.roundFloatToFloat(model.getAdultPrice());
        float child = model.getTmpChildValue() *  Utils.roundFloatToFloat(model.getChildPrice());
        float infants = model.getTmpInfantValue() *  Utils.roundFloatToFloat(model.getInfantPrice());

        return Utils.roundFloatToFloat(adult + child + infants);
    }

    private float getWhosinCustomTicketTotal(WhosinTicketTourOptionModel model) {
        float adult = model.getTmpAdultValue() *  Utils.roundFloatToFloat(model.getAdultPrice());
        float child = model.getTmpChildValue() *  Utils.roundFloatToFloat(model.getChildPrice());
        float infants = model.getTmpInfantValue() *  Utils.roundFloatToFloat(model.getInfantPrice());

        return Utils.roundFloatToFloat(adult + child + infants);
    }

    private float getWhosinTotalForTravelDesk(TravelDeskOptionDataModel model) {
        float adult = model.getTmpAdultValue() *  Utils.roundFloatToFloat(model.getAdultPrice());
        float child = model.getTmpChildValue() *  Utils.roundFloatToFloat(model.getChildPrice());
        float infants = model.getTmpInfantValue() *  Utils.roundFloatToFloat(model.getInfantPrice());

        return Utils.roundFloatToFloat(adult + child + infants);
    }

    private float getWhosinTotalForBigBus(BigBusOptionsItemModel model) {
        float adult = model.getTmpAdultValue() *  Utils.roundFloatToFloat(model.getAdultPrice());
        float child = model.getTmpChildValue() *  Utils.roundFloatToFloat(model.getChildPrice());
        float infants = model.getTmpInfantValue() *  Utils.roundFloatToFloat(model.getInfantPrice());

        return Utils.roundFloatToFloat(adult + child + infants);
    }

    private float getServiceTotalForTravelDesk(TravelDeskOptionDataModel model) {
        float adult = model.getTmpAdultValue() *  model.getPricePerAdultTravelDesk();
        float child = model.getTmpChildValue() *  model.getPricePerChildTravelDesk();
        float infants = model.getTmpInfantValue() *  model.getPricePerTripTravelDesk();

        return Utils.roundFloatToFloat(adult + child + infants);
    }

    private float getWithoutDiscountPrice(TourOptionsModel model) {
        float adult = model.getTmpAdultValue() * Utils.roundFloatToFloat(model.getWithoutDiscountAdultPrice());
        float child = model.getTmpChildValue() * Utils.roundFloatToFloat(model.getWithoutDiscountChildPrice());
        float infants = model.getTmpInfantValue() * Utils.roundFloatToFloat(model.getWithoutDiscountInfantPrice());

        return adult + child + infants;

    }

    private float getWhosinCustomWithoutDiscountPrice(WhosinTicketTourOptionModel model) {
        float adult = model.getTmpAdultValue() * Utils.roundFloatToFloat(model.getWithoutDiscountAdultPrice());
        float child = model.getTmpChildValue() * Utils.roundFloatToFloat(model.getWithoutDiscountChildPrice());
        float infants = model.getTmpInfantValue() * Utils.roundFloatToFloat(model.getWithoutDiscountInfantPrice());

        return adult + child + infants;

    }

    private float getBigBusWithoutDiscountPrice(BigBusOptionsItemModel model) {
        float adult = model.getTmpAdultValue() * Utils.roundFloatToFloat(model.getWithoutDiscountAdultPrice());
        float child = model.getTmpChildValue() * Utils.roundFloatToFloat(model.getWithoutDiscountChildPrice());
        float infants = model.getTmpInfantValue() * Utils.roundFloatToFloat(model.getWithoutDiscountInfantPrice());

        return adult + child + infants;

    }

    private float getWithoutDiscountPriceForReavelDesk(TravelDeskOptionDataModel model) {
        float adult = model.getTmpAdultValue() * Utils.roundFloatToFloat(model.getWithoutDiscountAdultPrice());
        float child = model.getTmpChildValue() * Utils.roundFloatToFloat(model.getWithoutDiscountChildPrice());
        float infants = model.getTmpInfantValue() * Utils.roundFloatToFloat(model.getWithoutDiscountInfantPrice());

        return adult + child + infants;

    }

    private JsonObject getJsonObject() {
        JsonObject object = new JsonObject();
//        object.addProperty("currency", "aed");
        object.addProperty("currency", Utils.getCurrency());
        object.addProperty("bookingType", raynaTicketDetailModel.getBookingType());

        AtomicReference<Float> totalAmount = new AtomicReference<>(0f);
        AtomicReference<Float> discountAmount = new AtomicReference<>(0f);
        AtomicReference<Float> finalAmount = new AtomicReference<>(0f);

        RaynaTicketManager.shared.selectedTourModel.forEach(q->{
            totalAmount.set(totalAmount.get() + getWithoutDiscountPrice(q));
            discountAmount.set(discountAmount.get() + getWhosinTotal(q));
            finalAmount.set(finalAmount.get() + getWhosinTotal(q));
        });

        RaynaTicketManager.shared.selectedAddonModels.forEach(addon -> {
            float price = addon.updatePrice();
            totalAmount.set(totalAmount.get() + price);
            discountAmount.set(discountAmount.get() + price);
            finalAmount.set(finalAmount.get() + price);
        });




        float discount = totalAmount.get() - discountAmount.get();
        if (discount < 0){
            discount = 0;
        }

        totalAmount.set(Utils.changeCurrencyToAED(totalAmount.get()));
        finalAmount.set(Utils.changeCurrencyToAED(finalAmount.get()));

        object.addProperty("customTicketId", raynaTicketDetailModel.getId());
        object.addProperty("promoCode",binding.edtPromoCode.getText().toString());

        object.addProperty("totalAmount",totalAmount.get());

        if (isPromoCodeApply && promoCodeModel != null) {
            object.addProperty("discount",promoCodeModel.getTotalDiscount());
            object.addProperty("amount", Utils.roundDoubleValueToDouble(promoCodeModel.getMetaData().get(0).getFinalAmount()));
            object.add("promoCodeData",new Gson().toJsonTree(promoCodeModel.getMetaData()).getAsJsonArray());
        } else {

            object.addProperty("discount", Utils.roundFloatValue(discount));
            object.addProperty("amount", finalAmount.get());
        }

        object.add("passengers", RaynaTicketManager.shared.object.getAsJsonArray("passengers"));

        JsonArray tourDetails = new JsonArray();
        final JsonArray allAddons = RaynaTicketManager.shared.object.has("Addons")
                ? RaynaTicketManager.shared.object.getAsJsonArray("Addons")
                : null;

        if (isWhosinTypeTicket){
            RaynaTicketManager.shared.selectedTourModel.forEach( p -> {
                JsonObject tourDetailObject = new JsonObject();

                tourDetailObject.addProperty("tourId", p.getCustomTicketId());
                tourDetailObject.addProperty("optionId", p.get_id());
                tourDetailObject.addProperty("pickup","None");
                tourDetailObject.addProperty("tourDate", p.getTourOptionSelectDate());

                tourDetailObject.addProperty("serviceTotal", getServiceTotalForWhosinType(p));
                tourDetailObject.addProperty("whosinTotal",Utils.changeCurrencyToAED(getWhosinTotal(p)));

                tourDetailObject.addProperty("adult", p.getTmpAdultValue());
                if (p.getTmpAdultValue() != 0){
                    tourDetailObject.addProperty("adultRate", p.getWithoutDiscountAdultPrice());
                }else {
                    tourDetailObject.addProperty("adultRate", 0);
                }

                tourDetailObject.addProperty("child", p.getTmpChildValue());
                if (p.getTmpChildValue() != 0) {
                    tourDetailObject.addProperty("childRate",p.getWithoutDiscountChildPrice());
                }else {
                    tourDetailObject.addProperty("childRate",0);
                }
                tourDetailObject.addProperty("infant", p.getTmpInfantValue());
                tourDetailObject.addProperty("transferId", 0);

                if (p.getAvailabilityType().equals("slot") && p.getSlotModelForWhosinTicket() != null){
                    tourDetailObject.addProperty("timeSlotId", p.getSlotModelForWhosinTicket().getId());
                    tourDetailObject.addProperty("timeSlot", p.getSlotModelForWhosinTicket().getAvailabilityTime());
                    tourDetailObject.addProperty("startTime", p.getSlotModelForWhosinTicket().getAvailabilityTime());
                }else {
                    tourDetailObject.addProperty("timeSlotId", 0);
                    tourDetailObject.addProperty("timeSlot", p.getAvailabilityTime());
                    tourDetailObject.addProperty("startTime", p.getAvailabilityTime());
                }

                tourDetailObject.addProperty("adult_title", p.getAdultTitle());
                tourDetailObject.addProperty("child_title", p.getChildTitle());
                tourDetailObject.addProperty("infant_title", p.getInfantTitle());

                JsonArray optionAddons = new JsonArray();
                if (allAddons != null && p.getAddons() != null && !p.getAddons().isEmpty()) {
                    List<String> optionAddonIds = p.getAddons().stream()
                            .map(TourOptionsModel::get_id)
                            .filter(id -> !TextUtils.isEmpty(id))
                            .collect(Collectors.toList());
                    for (int i = 0; i < allAddons.size(); i++) {
                        JsonObject addonObj = allAddons.get(i).getAsJsonObject();
                        if (addonObj.has("optionId") && optionAddonIds.contains(addonObj.get("optionId").getAsString())) {
                            optionAddons.add(addonObj);
                        }
                    }
                }
                tourDetailObject.add("Addons", optionAddons);

                tourDetails.add(tourDetailObject);

            });

        }else {
            RaynaTicketManager.shared.selectedTourModel.forEach( p -> {
                JsonObject tourDetailObject = new JsonObject();

                tourDetailObject.addProperty("tourId", p.getTourId());
                tourDetailObject.addProperty("optionId", p.getTourOptionId());
                tourDetailObject.addProperty("pickup", p.getPickUpLocation());
                tourDetailObject.addProperty("tourDate", p.getTourOptionSelectDate());
                tourDetailObject.addProperty("serviceTotal", getServiceTotal(p));
                tourDetailObject.addProperty("whosinTotal",Utils.changeCurrencyToAED(getWhosinTotal(p)));

                tourDetailObject.addProperty("adult", p.getTmpAdultValue());
                if (p.getTmpAdultValue() != 0){
                    tourDetailObject.addProperty("adultRate", p.getAdultPriceRayna());
                }else {
                    tourDetailObject.addProperty("adultRate", 0);
                }

                tourDetailObject.addProperty("child", p.getTmpChildValue());
                if (p.getTmpChildValue() != 0) {
                    tourDetailObject.addProperty("childRate",p.getChildPriceRayna());
                }else {
                    tourDetailObject.addProperty("childRate",0);
                }
                tourDetailObject.addProperty("infant", p.getTmpInfantValue());
                tourDetailObject.addProperty("transferId", p.getSelectedTransferId());

                if (p.getRaynaTimeSlotModel() != null) {
                    tourDetailObject.addProperty("timeSlotId", p.getRaynaTimeSlotModel().getTimeSlotId());
                    tourDetailObject.addProperty("timeSlot", p.getRaynaTimeSlotModel().getTimeSlot());
                    tourDetailObject.addProperty("startTime", p.getRaynaTimeSlotModel().getTimeSlot());
                } else {
                    tourDetailObject.addProperty("timeSlotId", 0);
                    tourDetailObject.addProperty("timeSlot", p.getSlotText());
                    tourDetailObject.addProperty("startTime", p.getStartTime());
                }
                tourDetailObject.addProperty("adult_title", p.getAdultTitle());
                tourDetailObject.addProperty("child_title", p.getChildTitle());
                tourDetailObject.addProperty("infant_title", p.getInfantTitle());

                JsonArray optionAddons = new JsonArray();
                if (allAddons != null && p.getAddons() != null && !p.getAddons().isEmpty()) {
                    List<String> optionAddonIds = p.getAddons().stream()
                            .map(TourOptionsModel::get_id)
                            .filter(id -> !TextUtils.isEmpty(id))
                            .collect(Collectors.toList());
                    for (int i = 0; i < allAddons.size(); i++) {
                        JsonObject addonObj = allAddons.get(i).getAsJsonObject();
                        if (addonObj.has("optionId") && optionAddonIds.contains(addonObj.get("optionId").getAsString())) {
                            optionAddons.add(addonObj);
                        }
                    }
                }
                tourDetailObject.add("Addons", optionAddons);

                tourDetails.add(tourDetailObject);

            });

        }


        object.add("TourDetails", tourDetails);

        object.add("cancellationPolicy", RaynaTicketManager.shared.cancellationObject);


        return object;
    }

    private JsonObject getJsonObjectForWhsoinCustomTicket() {
        JsonObject object = new JsonObject();
        object.addProperty("currency", Utils.getCurrency());
        object.addProperty("bookingType", raynaTicketDetailModel.getBookingType());

        AtomicReference<Float> totalAmount = new AtomicReference<>(0f);
        AtomicReference<Float> discountAmount = new AtomicReference<>(0f);
        AtomicReference<Float> finalAmount = new AtomicReference<>(0f);

        RaynaTicketManager.shared.selectedTourModelForWhosin.forEach(q->{
            totalAmount.set(totalAmount.get() + getWhosinCustomWithoutDiscountPrice(q));
            discountAmount.set(discountAmount.get() + getWhosinCustomTicketTotal(q));
            finalAmount.set(finalAmount.get() + getWhosinCustomTicketTotal(q));
        });

        RaynaTicketManager.shared.selectedAddonModels.forEach(addon -> {
            float price = addon.updatePrice();
            totalAmount.set(totalAmount.get() + price);
            discountAmount.set(discountAmount.get() + price);
            finalAmount.set(finalAmount.get() + price);
        });


        float discount = totalAmount.get() - discountAmount.get();
        if (discount < 0){
            discount = 0;
        }

        totalAmount.set(Utils.changeCurrencyToAED(totalAmount.get()));
        finalAmount.set(Utils.changeCurrencyToAED(finalAmount.get()));

        object.addProperty("customTicketId", raynaTicketDetailModel.getId());


        object.addProperty("promoCode",binding.edtPromoCode.getText().toString());

        object.addProperty("totalAmount",Utils.roundFloatValue(totalAmount.get()));

        if (isPromoCodeApply && promoCodeModel != null) {
            object.addProperty("discount",promoCodeModel.getTotalDiscount());
            object.addProperty("amount", Utils.roundDoubleValueToDouble(promoCodeModel.getMetaData().get(0).getFinalAmount()));
            object.add("promoCodeData",new Gson().toJsonTree(promoCodeModel.getMetaData()).getAsJsonArray());
        } else {

            object.addProperty("discount", Utils.roundFloatValue(discount));
            object.addProperty("amount", Utils.roundFloatValue(finalAmount.get()));
        }

        object.add("passengers", RaynaTicketManager.shared.object.getAsJsonArray("passengers"));

        JsonArray tourDetails = new JsonArray();

        RaynaTicketManager.shared.selectedTourModelForWhosin.forEach( p -> {
            JsonObject tourDetailObject = new JsonObject();

            tourDetailObject.addProperty("tourId", p.getTourId());
            tourDetailObject.addProperty("optionId", p.getTourOptionId());
            tourDetailObject.addProperty("pickup", p.getPickUpLocation());
            tourDetailObject.addProperty("tourDate", p.getTourOptionSelectDate());
            tourDetailObject.addProperty("serviceTotal", Utils.changeCurrencyToAED(getServiceTotalForWhosinCustomType(p)));
            tourDetailObject.addProperty("whosinTotal", Utils.changeCurrencyToAED(getWhosinCustomTicketTotal(p)));

            tourDetailObject.addProperty("adult", p.getTmpAdultValue());
            if (p.getTmpAdultValue() != 0){
                tourDetailObject.addProperty("adultRate", p.getWithoutDiscountAdultPrice());
            }else {
                tourDetailObject.addProperty("adultRate", 0);
            }

            tourDetailObject.addProperty("child", p.getTmpChildValue());
            if (p.getTmpChildValue() != 0) {
                tourDetailObject.addProperty("childRate",p.getWithoutDiscountChildPrice());
            }else {
                tourDetailObject.addProperty("childRate",0);
            }
            tourDetailObject.addProperty("infant", p.getTmpInfantValue());
            tourDetailObject.addProperty("transferId", 0);

            if (p.getIsSlot() && p.getRaynaTimeSlotModel() != null) {
                tourDetailObject.addProperty("timeSlotId", p.getRaynaTimeSlotModel().getSlotId());
                tourDetailObject.addProperty("timeSlot", p.getRaynaTimeSlotModel().getTimeSlot());
                tourDetailObject.addProperty("startTime", p.getRaynaTimeSlotModel().getTimeSlot());
            } else {
                tourDetailObject.addProperty("timeSlotId", 0);
                tourDetailObject.addProperty("timeSlot", p.getSlotText());
                tourDetailObject.addProperty("startTime", p.getStartTime());
            }
            tourDetailObject.addProperty("adult_title", p.getAdultTitle());
            tourDetailObject.addProperty("child_title", p.getChildTitle());
            tourDetailObject.addProperty("infant_title", p.getInfantTitle());

            if (RaynaTicketManager.shared.object.has("Addons")) {
                tourDetailObject.add("Addons", RaynaTicketManager.shared.object.getAsJsonArray("Addons"));
            }
            tourDetails.add(tourDetailObject);

        });


        object.add("TourDetails", tourDetails);
        object.add("cancellationPolicy", RaynaTicketManager.shared.cancellationObject);

        return object;
    }

    private JsonObject getJsonObjectForTravelDesk() {
        JsonObject object = new JsonObject();
        object.addProperty("currency", Utils.getCurrency());
        object.addProperty("bookingType", raynaTicketDetailModel.getBookingType());

        AtomicReference<Float> totalAmount = new AtomicReference<>(0f);
        AtomicReference<Float> discountAmount = new AtomicReference<>(0f);
        AtomicReference<Float> finalAmount = new AtomicReference<>(0f);
        AtomicReference<Float> pricePerTrip = new AtomicReference<>(0f);

        RaynaTicketManager.shared.selectTravelDeskOptionDataModels.forEach(q->{
            totalAmount.set(totalAmount.get() + getWithoutDiscountPriceForReavelDesk(q));
            discountAmount.set(discountAmount.get() + getWhosinTotalForTravelDesk(q));
            finalAmount.set(finalAmount.get() + getWhosinTotalForTravelDesk(q));
            pricePerTrip.set(pricePerTrip.get() + q.getPricePerTrip());
        });

        RaynaTicketManager.shared.selectedAddonModels.forEach(addon -> {
            float price = addon.updatePrice();
            totalAmount.set(totalAmount.get() + price);
            discountAmount.set(discountAmount.get() + price);
            finalAmount.set(finalAmount.get() + price);
        });

        float discount = totalAmount.get() - discountAmount.get();
        if (discount < 0){
            discount = 0;
        }

        totalAmount.set(Utils.changeCurrencyToAED(totalAmount.get() + pricePerTrip.get()));
        finalAmount.set(Utils.changeCurrencyToAED(finalAmount.get() + pricePerTrip.get()));

        object.addProperty("customTicketId", raynaTicketDetailModel.getId());
        object.addProperty("promoCode",binding.edtPromoCode.getText().toString());
        object.addProperty("totalAmount",Utils.roundFloatValue(totalAmount.get()));

        if (isPromoCodeApply && promoCodeModel != null) {
            object.addProperty("discount",promoCodeModel.getTotalDiscount());
            object.addProperty("amount", Utils.roundDoubleValueToDouble(promoCodeModel.getMetaData().get(0).getFinalAmount()));
            object.add("promoCodeData",new Gson().toJsonTree(promoCodeModel.getMetaData()).getAsJsonArray());
        } else {

            object.addProperty("discount", Utils.roundFloatValue(discount));
            object.addProperty("amount", Utils.roundFloatValue(finalAmount.get()));
        }

        object.add("passengers", RaynaTicketManager.shared.object.getAsJsonArray("passengers"));

        JsonArray tourDetails = new JsonArray();

        RaynaTicketManager.shared.selectTravelDeskOptionDataModels.forEach( p -> {
            JsonObject tourDetailObject = new JsonObject();
            tourDetailObject.addProperty("tourId", String.valueOf(p.getTourId()));
            tourDetailObject.addProperty("optionId", String.valueOf(p.getId()));
            tourDetailObject.addProperty("tourDate", p.getTourOptionSelectDate());

            tourDetailObject.addProperty("serviceTotal", getServiceTotalForTravelDesk(p) + p.getPricePerTripTravelDesk());

//            tourDetailObject.addProperty("whosinTotal", getWhosinTotalForTravelDesk(p) + p.getPricePerTrip());
            tourDetailObject.addProperty("whosinTotal", Utils.changeCurrencyToAED(getWhosinTotalForTravelDesk(p) + p.getPricePerTrip()));

            tourDetailObject.addProperty("adult", p.getTmpAdultValue());
            if (p.getTmpAdultValue() != 0){
                tourDetailObject.addProperty("adultRate", p.getAdultPrice());
            }else {
                tourDetailObject.addProperty("adultRate", 0);

            }

            tourDetailObject.addProperty("child", p.getTmpChildValue());
            if (p.getTmpChildValue() != 0) {
                tourDetailObject.addProperty("childRate",p.getChildPrice());
            }else {
                tourDetailObject.addProperty("childRate",0);
            }
            tourDetailObject.addProperty("infant", p.getTmpInfantValue());
            tourDetailObject.addProperty("transferId", 0);
            tourDetailObject.addProperty("departureTime", "");

            if (p.getTravelDeskPickUpListModel() != null){
                tourDetailObject.addProperty("pickup",p.getTravelDeskPickUpListModel().getName());
                if (p.getTravelDeskPickUpListModel().getId() != 0){
                    tourDetailObject.addProperty("hotelId",p.getTravelDeskPickUpListModel().getId());
                }
            }

            if (p.getTravelDeskAvailabilityModel() != null){
                tourDetailObject.addProperty("timeSlotId", String.valueOf(p.getTravelDeskAvailabilityModel().getAvailability().getTimeSlotId()));
                tourDetailObject.addProperty("timeSlot", p.getTravelDeskAvailabilityModel().getSlotText());
                tourDetailObject.addProperty("startTime", p.getTravelDeskAvailabilityModel().getAvailability().getStartTime());
                tourDetailObject.addProperty("endTime", p.getTravelDeskAvailabilityModel().getAvailability().getEndTime());
            }
            tourDetailObject.addProperty("message", p.getMessage());
            tourDetailObject.addProperty("adult_title", p.getAdultTitle());
            tourDetailObject.addProperty("child_title", p.getChildTitle());
            tourDetailObject.addProperty("infant_title", p.getInfantTitle());

            if (RaynaTicketManager.shared.object.has("Addons")) {
                tourDetailObject.add("Addons", RaynaTicketManager.shared.object.getAsJsonArray("Addons"));
            }
            tourDetails.add(tourDetailObject);

        });


        object.add("TourDetails", tourDetails);

        object.add("cancellationPolicy", RaynaTicketManager.shared.cancellationObject);

        return object;
    }

    private JsonObject getJsonObjectForBigBus() {
        JsonObject object = new JsonObject();
        object.addProperty("currency", Utils.getCurrency());
        object.addProperty("bookingType", "octo");

        AtomicReference<Float> totalAmount = new AtomicReference<>(0f);
        AtomicReference<Float> discountAmount = new AtomicReference<>(0f);
        AtomicReference<Float> finalAmount = new AtomicReference<>(0f);


        RaynaTicketManager.shared.selectedTourModelForBigBus.forEach(q->{
            totalAmount.set(totalAmount.get() + getBigBusWithoutDiscountPrice(q));
            discountAmount.set(discountAmount.get() + getWhosinTotalForBigBus(q));
            finalAmount.set(finalAmount.get() + getWhosinTotalForBigBus(q));
        });

        RaynaTicketManager.shared.selectedAddonModels.forEach(addon -> {
            float price = addon.updatePrice();
            totalAmount.set(totalAmount.get() + price);
            discountAmount.set(discountAmount.get() + price);
            finalAmount.set(finalAmount.get() + price);
        });

        float discount = totalAmount.get() - discountAmount.get();
        if (discount < 0){
            discount = 0;
        }

        totalAmount.set(Utils.changeCurrencyToAED(totalAmount.get()));
        finalAmount.set(Utils.changeCurrencyToAED(finalAmount.get()));

        object.addProperty("customTicketId", raynaTicketDetailModel.getId());
        object.addProperty("promoCode",binding.edtPromoCode.getText().toString());
        object.addProperty("totalAmount",Utils.roundFloatValue(totalAmount.get()));

        if (isPromoCodeApply && promoCodeModel != null) {
            object.addProperty("discount",promoCodeModel.getTotalDiscount());
            object.addProperty("amount", Utils.roundDoubleValueToDouble(promoCodeModel.getMetaData().get(0).getFinalAmount()));
            object.add("promoCodeData",new Gson().toJsonTree(promoCodeModel.getMetaData()).getAsJsonArray());
        } else {

            object.addProperty("discount", Utils.roundFloatValue(discount));
            object.addProperty("amount", Utils.roundFloatValue(finalAmount.get()));
        }

        object.add("passengers", RaynaTicketManager.shared.object.getAsJsonArray("passengers"));

        JsonArray tourDetails = new JsonArray();

        RaynaTicketManager.shared.selectedTourModelForBigBus.forEach( p -> {
            JsonObject tourDetailObject = new JsonObject();
            tourDetailObject.addProperty("tourId", p.getTourId());
            tourDetailObject.addProperty("optionId", p.getId());

//            tourDetailObject.addProperty("serviceTotal", getBigBusWithoutDiscountPrice(p));
//            tourDetailObject.addProperty("whosinTotal", getWhosinTotalForBigBus(p));

            tourDetailObject.addProperty("serviceTotal", Utils.changeCurrencyToAED(getBigBusWithoutDiscountPrice(p)));
            tourDetailObject.addProperty("whosinTotal", Utils.changeCurrencyToAED(getWhosinTotalForBigBus(p)));

            tourDetailObject.addProperty("adult", p.getTmpAdultValue());
            if (p.getTmpAdultValue() != 0) {
                tourDetailObject.addProperty("adultRate", p.getAdultPrice());
                BigBusUnitsItemModel unit = p.getUnitByType(AppConstants.ADULTS);
                if (unit != null) {
                    tourDetailObject.addProperty("adultId", unit.getId());
                } else {
                    tourDetailObject.addProperty("adultId", "");
                }

            } else {
                tourDetailObject.addProperty("adultRate", 0);
                tourDetailObject.addProperty("adultId", "");
            }

            tourDetailObject.addProperty("child", p.getTmpChildValue());
            if (p.getTmpChildValue() != 0) {
                tourDetailObject.addProperty("childRate", p.getChildPrice());
                BigBusUnitsItemModel unit = p.getUnitByType(AppConstants.CHILD);
                if (unit != null) {
                    tourDetailObject.addProperty("childId", unit.getId());
                } else {
                    tourDetailObject.addProperty("childId", "");
                }
            } else {
                tourDetailObject.addProperty("childRate", 0);
                tourDetailObject.addProperty("childId", "");
            }

            tourDetailObject.addProperty("infant", p.getTmpInfantValue());
            if (p.getTmpInfantValue() != 0) {
                BigBusUnitsItemModel unit = p.getUnitByType(AppConstants.INFANT);
                if (unit != null) {
                    tourDetailObject.addProperty("infantId", unit.getId());
                } else {
                    tourDetailObject.addProperty("infantId", "");
                }
            } else {
                tourDetailObject.addProperty("infantId", "");
            }


            tourDetailObject.addProperty("transferId", 0);
            if (raynaTicketDetailModel.getBookingType().equals("hero-balloon") && p.getPickupPointsModel() != null) {
                tourDetailObject.addProperty("pickup", p.getPickUpPoint());
            } else {
                tourDetailObject.addProperty("pickup", "");
            }

            tourDetailObject.addProperty("timeSlotId", 0);
            tourDetailObject.addProperty("hotelId", 0);

            if (p.getTimeModel() != null){
                tourDetailObject.addProperty("startTime", p.getTimeModel().getOpeningHours().get(0).getFrom());
                tourDetailObject.addProperty("tourDate", p.getTimeModel().getId());
                tourDetailObject.addProperty("timeSlot", p.getTimeModel().getOpeningHours().get(0).getFrom() + " - " + p.getTimeModel().getOpeningHours().get(0).getTo());
            }

            tourDetailObject.addProperty("adult_title", p.getAdultTitle());
            tourDetailObject.addProperty("child_title", p.getChildTitle());
            tourDetailObject.addProperty("infant_title", p.getInfantTitle());

            tourDetails.add(tourDetailObject);

        });


        object.add("TourDetails", tourDetails);

        return object;
    }

    private void startStripeCheckOut(PaymentCredentialModel model) {
        if (model.publishableKey == null || model.clientSecret == null) {
            Toast.makeText(activity, "Invalid payment configuration", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            PaymentConfiguration.init(activity, model.publishableKey);
            final PaymentSheet.Configuration configuration = new PaymentSheet.Configuration.Builder("Whosin, Inc.").allowsDelayedPaymentMethods(true).build();
            paymentSheet.presentWithPaymentIntent(model.clientSecret, configuration);
        }
        catch (Exception e){
            Log.d("TAG", "startStripeCheckOut: "+ e.getMessage());
            e.printStackTrace();
        }

    }

    private void startGooglePayCheckOut(PaymentCredentialModel model) {
        LogManager.shared.logTicketEvent(LogManager.LogEventType.paymentInitiated, "", "", null, null, "AED");
        PaymentConfiguration.init(activity, model.publishableKey);
        final PaymentSheet.GooglePayConfiguration googlePayConfiguration = new PaymentSheet.GooglePayConfiguration(AppConstants.GPAY_ENV, AppConstants.GPAY_REGION);
        final PaymentSheet.Configuration configuration = new PaymentSheet.Configuration.Builder("Whosin, Inc.")
                .allowsDelayedPaymentMethods(true).googlePay(googlePayConfiguration).build();
        paymentSheet.presentWithPaymentIntent(model.clientSecret, configuration);
    }

    private void tabbyCheckOut(PaymentCredentialModel model) {
        LogManager.shared.logTicketEvent(LogManager.LogEventType.paymentInitiated, model.getId(), "Payment Initiated", null, null, "AED");
        TabbyPaymentBottomSheet sheet = new TabbyPaymentBottomSheet();
        sheet.paymentTabbyModel = model.getTabbyModel();
        sheet.callback = p -> {
            if (!TextUtils.isEmpty(p)) {
                if (p.equals("success")) {
                    PurchaseSuccessFragment purchaseSuccessFragment = new PurchaseSuccessFragment();
                    purchaseSuccessFragment.callBackForRaynaBooking = q -> {
                        if (q) {
                            LogManager.shared.logTicketEvent(LogManager.LogEventType.purchase, model.getId(), "Purchase Success", null, null, "AED");
                            startActivity(new Intent(Graphics.context, WalletActivity.class));
                            RaynaTicketManager.shared.finishAllActivities();
                            RaynaTicketManager.shared.clearManager();
                            finish();

                        }
                    };
                    purchaseSuccessFragment.show(getSupportFragmentManager(), "");

                } else if (p.equals("cancel")) {
                    LogManager.shared.logTicketEvent(LogManager.LogEventType.paymentCancelled, model.getId(), "Purchase Cancelled", null, null, "AED");
                } else if (p.equals("failure")) {
                    LogManager.shared.logTicketEvent(LogManager.LogEventType.paymentFailed, model.getId(), "Purchase Faukure", null, null, "AED");
                }
            }
        };
        sheet.show(getSupportFragmentManager(), "");
    }

    @SuppressLint("DefaultLocale")
    private float setTicketDetails(TextView title, TextView value, String type, int count, float price,TextView originalValue,float originalprice,String paxTitle) {
//        title.setText(String.format("%d x %s", count, type));

        String key = switch (type.toLowerCase()) {
            case "adults" -> "adult_count";
            case "childs" -> "child_count";
            default -> "infant_count";
        };
        title.setText(setValue(key, String.valueOf(count),paxTitle));

        float totalPrice = count * Utils.roundFloatToFloat(price);
        if (count != 0){
            originalValue.setVisibility(View.VISIBLE);
            Utils.setStyledText(activity,originalValue,Utils.roundFloatValue(totalPrice));
            originalValue.setPaintFlags(originalValue.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            if (price == 0 || originalprice >= price){
                originalValue.setVisibility(GONE);
            }
        }else {
            originalValue.setVisibility(View.GONE);
        }

        float tmpPrice = count * Utils.roundFloatToFloat(originalprice);
        Utils.setStyledText(activity,value,String.format(Utils.roundFloatValue(tmpPrice)));
        return totalPrice;
    }

    @SuppressLint("DefaultLocale")
    private float setTicketDetailsForTravelDesk(TextView title, TextView value, String type, int count, float price,TextView originalValue,float originalprice,String paxTitle) {

        String key = switch (type.toLowerCase()) {
            case "adults" -> "adult_count";
            case "childs" -> "child_count";
            default -> "infant_count";
        };
        title.setText(setValue(key, String.valueOf(count),paxTitle));

        float totalPrice = count * Utils.roundFloatToFloat(price);
        if (count != 0){
            originalValue.setVisibility(View.VISIBLE);
            Utils.setStyledText(activity,originalValue,Utils.roundFloatValue(totalPrice));
            originalValue.setPaintFlags(originalValue.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            if (price == 0){
                originalValue.setVisibility(GONE);
            }
        }else {
            originalValue.setVisibility(View.GONE);
        }

        if (originalprice >= price){
            originalValue.setVisibility(GONE);
        }

        float tmpPrice = count * Utils.roundFloatToFloat(originalprice);
        Utils.setStyledText(activity,value,String.format(Utils.roundFloatValue(tmpPrice)));

//        originalValue.setVisibility(View.GONE);
//        value.setVisibility(View.GONE);
        return totalPrice;
    }

    @SuppressLint("SetTextI18n")
    private void updatePrices(){

        binding.promoCodeLinear.setVisibility(GONE);

        AtomicReference<Float> totalAmount = new AtomicReference<>(0f);
        AtomicReference<Float> addOnTotal = new AtomicReference<>(0f);
        AtomicReference<Float> discountAmount = new AtomicReference<>(0f);
        AtomicReference<Float> finalAmount = new AtomicReference<>(0f);
        AtomicReference<Float> pricePerTrip = new AtomicReference<>(0f);


        switch (raynaTicketDetailModel.getBookingType()) {
            case "travel-desk":
                binding.pricePerTripLinear.setVisibility(VISIBLE);
                RaynaTicketManager.shared.selectTravelDeskOptionDataModels.forEach(q -> {
                    totalAmount.set(totalAmount.get() + getWithoutDiscountPriceForReavelDesk(q));
                    discountAmount.set(discountAmount.get() + getWhosinTotalForTravelDesk(q));
                    finalAmount.set(finalAmount.get() + getWhosinTotalForTravelDesk(q));
                    pricePerTrip.set(pricePerTrip.get() + q.getPricePerTrip());
                });

                RaynaTicketManager.shared.selectedAddonModels.forEach(addon -> {
                    float price = addon.updatePrice();
                    totalAmount.set(totalAmount.get() + price);
                    discountAmount.set(discountAmount.get() + price);
                    finalAmount.set(finalAmount.get() + price);
                    addOnTotal.set(addOnTotal.get() + price);
                });
                break;
            case "whosin-ticket":
                RaynaTicketManager.shared.selectedTourModelForWhosin.forEach(q -> {
                    totalAmount.set(totalAmount.get() + getWhosinCustomWithoutDiscountPrice(q));
                    discountAmount.set(discountAmount.get() + getWhosinCustomTicketTotal(q));
                    finalAmount.set(finalAmount.get() + getWhosinCustomTicketTotal(q));
                });

                RaynaTicketManager.shared.selectedAddonModels.forEach(addon -> {
                    float price = addon.updatePrice();
                    totalAmount.set(totalAmount.get() + price);
                    discountAmount.set(discountAmount.get() + price);
                    finalAmount.set(finalAmount.get() + price);
                    addOnTotal.set(addOnTotal.get() + price);
                });
                break;
            case "big-bus":
            case "hero-balloon":
                RaynaTicketManager.shared.selectedTourModelForBigBus.forEach(q -> {
                    totalAmount.set(totalAmount.get() + getBigBusWithoutDiscountPrice(q));
                    discountAmount.set(discountAmount.get() + getWhosinTotalForBigBus(q));
                    finalAmount.set(finalAmount.get() + getWhosinTotalForBigBus(q));
                });

                RaynaTicketManager.shared.selectedAddonModels.forEach(addon -> {
                    float price = addon.updatePrice();
                    totalAmount.set(totalAmount.get() + price);
                    discountAmount.set(discountAmount.get() + price);
                    finalAmount.set(finalAmount.get() + price);
                });
                break;
            default:
                RaynaTicketManager.shared.selectedTourModel.forEach(q -> {
                    totalAmount.set(totalAmount.get() + getWithoutDiscountPrice(q));
                    discountAmount.set(discountAmount.get() + getWhosinTotal(q));
                    finalAmount.set(finalAmount.get() + getWhosinTotal(q));
                });

                RaynaTicketManager.shared.selectedAddonModels.forEach(addon -> {
                    float price = addon.updatePrice();
                    totalAmount.set(totalAmount.get() + price);
                    discountAmount.set(discountAmount.get() + price);
                    finalAmount.set(finalAmount.get() + price);
                    addOnTotal.set(addOnTotal.get() + price);
                });
                break;
        }

        float discount = totalAmount.get() - discountAmount.get();

        if (raynaTicketDetailModel.getBookingType().equals("travel-desk")) {
            finalAmount.set(finalAmount.get() + pricePerTrip.get());
            totalAmount.set(totalAmount.get() + pricePerTrip.get());
            Utils.setStyledText(activity, binding.priecPerAed, String.valueOf(pricePerTrip.get()));
        }


        Utils.setStyledText(activity,binding.discountAed,Utils.roundFloatValue(discount));
        Utils.setStyledText(activity, binding.finalAmountAed, Utils.roundFloatValue(finalAmount.get()));
        Utils.setStyledText(activity,binding.addonPriceAed,Utils.roundFloatValue(addOnTotal.get()));
        Utils.setStyledText(activity,binding.totalAmoutAed,Utils.roundFloatValue(totalAmount.get()));

        binding.addOnPriceLinear.setVisibility(addOnTotal.get() > 0f ? View.VISIBLE : View.GONE);

        if (discount <= 0){
            binding.discountLinear.setVisibility(GONE);
        }else {
            binding.discountLinear.setVisibility(VISIBLE);
        }
    }

    // endregion
    // --------------------------------------
    // region Private : Promo Code Methods
    // --------------------------------------

    private JsonObject getPromoJsonObject(){
        JsonObject object = new JsonObject();

        AtomicReference<Float> finalAmount = new AtomicReference<>(0f);
        AtomicReference<Float> pricePerTrip = new AtomicReference<>(0f);

        switch (raynaTicketDetailModel.getBookingType()) {
            case "travel-desk":
                RaynaTicketManager.shared.selectTravelDeskOptionDataModels.forEach(q -> {
                    finalAmount.set(finalAmount.get() + getWhosinTotalForTravelDesk(q));
                    pricePerTrip.set(pricePerTrip.get() + q.getPricePerTrip());
                });
                RaynaTicketManager.shared.selectedAddonModels.forEach(addon -> {
                    finalAmount.set(finalAmount.get() + addon.updatePrice());
                });
                finalAmount.set(finalAmount.get() + pricePerTrip.get());
                break;
            case "whosin-ticket":
                RaynaTicketManager.shared.selectedTourModelForWhosin.forEach(q -> {
                    finalAmount.set(finalAmount.get() + getWhosinCustomTicketTotal(q));
                });
                RaynaTicketManager.shared.selectedAddonModels.forEach(addon -> {
                    finalAmount.set(finalAmount.get() + addon.updatePrice());
                });
                break;
            case "big-bus":
            case "hero-balloon":
                RaynaTicketManager.shared.selectedTourModelForBigBus.forEach(q -> {
                    finalAmount.set(finalAmount.get() + getWhosinTotalForBigBus(q));
                });
                RaynaTicketManager.shared.selectedAddonModels.forEach(addon -> {
                    finalAmount.set(finalAmount.get() + addon.updatePrice());
                });
                break;
            default:
                RaynaTicketManager.shared.selectedTourModel.forEach(q -> {
                    finalAmount.set(finalAmount.get() + getWhosinTotal(q));
                });
                RaynaTicketManager.shared.selectedAddonModels.forEach(addon -> {
                    finalAmount.set(finalAmount.get() + addon.updatePrice());
                });
                break;
        }

        object.addProperty("promoCode",binding.edtPromoCode.getText().toString().trim());
        JsonArray metaDate = new JsonArray();
        JsonObject itemMetaData = new JsonObject();
        itemMetaData.addProperty("type", "ticket");
        itemMetaData.addProperty("ticketId",  raynaTicketDetailModel.getId());
        itemMetaData.addProperty("amount", finalAmount.get());
        itemMetaData.addProperty("discount",raynaTicketDetailModel.getDiscount());
        itemMetaData.addProperty("qty",1);

        metaDate.add(itemMetaData);

        object.add("metadata",metaDate);
        Log.d("JsonObject", "getJsonObject: " + object);
        return object;
    }

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
        updatePrices();
    }

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestRaynaTourBooking(int paymentMod) {
        JsonObject object;
        switch (raynaTicketDetailModel.getBookingType()) {
            case "travel-desk":
                object = getJsonObjectForTravelDesk();
                break;
            case "whosin-ticket":
                object = getJsonObjectForWhsoinCustomTicket();
                break;
            case "big-bus":
            case "hero-balloon":
                object = getJsonObjectForBigBus();
                break;
            default:
                object = getJsonObject();
                break;
        }

        Log.d("object", "requestRaynaTourBooking: " + object);
        if (AppConstants.CARD_PAYMENT == paymentMod) {
            object.addProperty("paymentMethod", AppSettingManager.shared.getAppSettingData().isAllowStripePayments() ? "stripe" : "ngenius");
        } else if (AppConstants.PAY_WITH_LINK == paymentMod) {
            object.addProperty("paymentMethod", "stripe");
        } else if (AppConstants.TABBY_PAYMENT == paymentMod) {
            object.addProperty("paymentMethod", "tabby");
        }else if (AppConstants.GOOGLE_PAY == paymentMod || AppConstants.SAMSUNG_PAY == paymentMod) {
            object.addProperty("paymentMethod",  AppSettingManager.shared.getAppSettingData().isAllowStripePayments() ? "stripe" : "ngenius");
        }
//        else if (AppConstants.NGINUES_PAY == paymentMod) {
//            object.addProperty("paymentMethod", "ngenius");
//        }
        Log.d("object", "requestRaynaTourBooking: " + object);

        showProgress();
        DataService.shared(activity).requestRaynaTourBooking(object, new RestCallback<ContainerModel<PaymentCredentialModel>>(this) {
            @Override
            public void result(ContainerModel<PaymentCredentialModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    if (error.contains("Session expired, please login again!")){
                        Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), "Session expired, please login again!", aBoolean -> {
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
                    } else if (AppConstants.GOOGLE_PAY == paymentMod || AppConstants.SAMSUNG_PAY == paymentMod) {
                        if (AppSettingManager.shared.getAppSettingData().isAllowStripePayments()) {
                            startGooglePayCheckOut(model.getData());
                        } else {
                            startNginiusGpayPayment(model.getData(), true);
                        }
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

    private void startNginiusGpayPayment(PaymentCredentialModel model, boolean isGooglePay) {
        LogManager.shared.logTicketEvent(LogManager.LogEventType.paymentInitiated, "", "", null, null, "AED");
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
        //if (isGooglePay) {
        paymentRequest.setGooglePayConfig(googlePayConfig);
//        }

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

    private void requestVenuePromoCode() {
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

                    binding.promoCodeLinear.setVisibility(View.VISIBLE);


                    binding.YourSavingLayout.setVisibility(model.getData().getTotalDiscount() > 0 ? View.VISIBLE : View.GONE);

//                    binding.finalAmountAed.setText("AED " + String.valueOf(model.getData().getMetaData().get(0).getFinalAmount()));
//                    binding.promoCodeAed.setText("AED " + model.getData().getPromoDiscount());
//                    binding.tvTotalDiscount.setText("AED " + model.getData().getItemsDiscount());
//                    binding.discountAed.setText("AED " + Utils.roundDoubleValueToDouble(model.getData().getItemsDiscount()));

                    Utils.setStyledText(activity,binding.finalAmountAed,String.valueOf(model.getData().getMetaData().get(0).getFinalAmount()));
                    Utils.setStyledText(activity,binding.promoCodeAed,String.valueOf(model.getData().getPromoDiscount()));

//                    Utils.setStyledText(activity,binding.discountAed,String.valueOf(Utils.roundDoubleValueToDouble(model.getData().getItemsDiscount())));
                    double discount = Utils.roundDoubleValueToDouble(model.getData().getItemsDiscount());

                    if (discount == 0) {
                        binding.discountLinear.setVisibility(View.GONE);
                    } else {
                        binding.discountLinear.setVisibility(View.VISIBLE);
                        Utils.setStyledText(activity, binding.discountAed, String.valueOf(discount));
                    }

                    if (discount == 0) {
                        binding.promoCodeDiscountLayout.setVisibility(View.GONE);
                    } else {
                        binding.promoCodeDiscountLayout.setVisibility(View.VISIBLE);
                        Utils.setStyledText(activity, binding.discountAed, String.valueOf(discount));
                        Utils.setStyledText(activity,binding.tvTotalDiscount,String.valueOf(model.getData().getItemsDiscount()));
                    }




                }

            }
        });
    }

    private void requestCartAdd(){
        JsonObject object;
        switch (raynaTicketDetailModel.getBookingType()) {
            case "travel-desk":
                object = getJsonObjectForTravelDesk();
                break;
            case "whosin-ticket":
                object = getJsonObjectForWhsoinCustomTicket();
                break;
            case "big-bus":
            case "hero-balloon":
                object = getJsonObjectForBigBus();
                object.addProperty("bookingType", raynaTicketDetailModel.getBookingType());
                break;
            default:
                object = getJsonObject();
                break;
        }
        showProgress();
        DataService.shared(activity).requestCartAdd(object,new RestCallback<ContainerModel<RaynaTicketBookingModel>>(this) {
            @Override
            public void result(ContainerModel<RaynaTicketBookingModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }
                startActivity(new Intent(activity, TicketCartActivity.class).putExtra("showAlert",true));
                RaynaTicketManager.shared.finishAllActivities();
                RaynaTicketManager.shared.clearManager();
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            onNGenuesCardPaymentResponse(CardPaymentData.getFromIntent(data));
        } catch (Exception ex) {
        }
    }

    private void onNGenuesCardPaymentResponse(CardPaymentData data) {
        hideProgress();
        switch (data.getCode()) {
            case CardPaymentData.STATUS_PAYMENT_AUTHORIZED, CardPaymentData.STATUS_PAYMENT_CAPTURED:
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
                break;
            case CardPaymentData.STATUS_PAYMENT_FAILED:
                Toast.makeText(activity, "Payment failed, Please try again.", Toast.LENGTH_SHORT).show();
                break;
            case CardPaymentData.STATUS_GENERIC_ERROR:
                Toast.makeText(activity, "Something went wrong while payment process, Please try again.", Toast.LENGTH_SHORT).show();
                break;
            default:
                throw new IllegalArgumentException("Unknown payment response (" + data.getReason() + ")");
        }
    }


    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------


    private class GuestDetailAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_guest_detail));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            RaynaPassengerModel model = (RaynaPassengerModel) getItem(position);
            if (model == null) return;

            String guestName = model.getPrefix() + " " + model.getFirstName() + " " + model.getLastName() + " (" + model.getPaxType() + ")";
            if (position == 0) {
                guestName = guestName + " (Primary Guest)";
            }
            viewHolder.binding.tvGuestName.setText(guestName);

        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final ItemGuestDetailBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemGuestDetailBinding.bind(itemView);
            }
        }
    }

    private class TourOptionDetailsAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_checkout_tour_options_details));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            TourOptionsModel model = (TourOptionsModel) getItem(position);
            if (model == null) return;

            viewHolder.binding.btnMoreInfoView.setText(getValue("Inclusions & Details"));
            viewHolder.binding.totalAmoutTv.setText(getValue("Ticket Amount"));
            viewHolder.binding.discountTv.setText(getValue("discount"));
            viewHolder.binding.finalAmountTv.setText(getValue("final_amount"));

            boolean isWhosinTypeBooking = raynaTicketDetailModel.getBookingType().equals("whosin");

            if (!TextUtils.isEmpty(model.getTourOptionSelectDate())) viewHolder.binding.tvTourDate.setText(model.getTourOptionSelectDate());

//            if (TextUtils.isEmpty(raynaTicketDetailModel.getCity())){
            viewHolder.binding.locationLayout.setVisibility(GONE);
//            }else {
//                viewHolder.binding.tvAddress.setText(raynaTicketDetailModel.getCity());
//                viewHolder.binding.locationLayout.setVisibility(VISIBLE);
//            }


            if (isWhosinTypeBooking){
                if (model.getAvailabilityType().equals("regular")){
                    viewHolder.binding.tvTourTime.setText(model.getAvailabilityTime());
                } else if (model.getAvailabilityType().equals("slot")) {
                    viewHolder.binding.tvTourTime.setText(model.getSlotModelForWhosinTicket().getAvailabilityTime());
                }
            }else {
                if (model.getIsSlot() && model.getRaynaTimeSlotModel() != null){
                    viewHolder.binding.tvTourTime.setText(model.getRaynaTimeSlotModel().getTimeSlot());
                } else if (!TextUtils.isEmpty(model.getSlotText())) {
                    viewHolder.binding.tvTourTime.setText(model.getSlotText());
                }
            }


            viewHolder.binding.tvType.setText(model.getTransType());
            viewHolder.binding.totalPersonLayout.setVisibility(TextUtils.isEmpty(model.getTransType()) ? GONE : VISIBLE);

            if (raynaTicketDetailModel.getBookingType().equals("whosin")) {
                viewHolder.binding.titleOptionName.setText(model.getTitle());
                if (!TextUtils.isEmpty(model.getDescription())) {
                    viewHolder.binding.tvOptionDetail.setVisibility(VISIBLE);
                    viewHolder.binding.tvOptionDetail.setText(model.getDescription());
                } else {
                    viewHolder.binding.tvOptionDetail.setVisibility(GONE);
                }
            } else {
                if (model.getOptionDetail() != null) {
                    viewHolder.binding.titleOptionName.setText(model.getOptionDetail().getOptionName());
                    if (!TextUtils.isEmpty(model.getOptionDetail().getOptionDescription())) {
                        viewHolder.binding.tvOptionDetail.setVisibility(VISIBLE);
                        viewHolder.binding.tvOptionDetail.setText(model.getOptionDetail().getOptionDescription());
                    } else {
                        viewHolder.binding.tvOptionDetail.setVisibility(GONE);

                    }
                }
            }


            float adultPrice = setTicketDetails(viewHolder.binding.totalAdults.tvTitle,viewHolder.binding.totalAdults.tvTitleValue, "Adults", model.getTmpAdultValue(), model.getWithoutDiscountAdultPrice(),viewHolder.binding.totalAdults.tvOriginalValue,model.getAdultPrice(),model.getAdultTitle());
            float childPrice = setTicketDetails(viewHolder.binding.totalChilds.tvTitle,viewHolder.binding.totalChilds.tvTitleValue, "Childs", model.getTmpChildValue(), model.getWithoutDiscountChildPrice(),viewHolder.binding.totalChilds.tvOriginalValue,model.getChildPrice(),model.getChildTitle());
            float infantPrice = setTicketDetails(viewHolder.binding.totalInfants.tvTitle,viewHolder.binding.totalInfants.tvTitleValue,"Infants", model.getTmpInfantValue(), model.getWithoutDiscountInfantPrice(),viewHolder.binding.totalInfants.tvOriginalValue,model.getInfantPrice(),model.getInfantTitle());


            float totalAmount = adultPrice + childPrice + infantPrice;

            float discount = totalAmount - getWhosinTotal(model);

            Utils.setStyledText(activity,viewHolder.binding.discountAed,Utils.roundFloatValue(discount));

            if (totalAmount == discount) {
                viewHolder.binding.discountLinear.setVisibility(View.GONE);
                viewHolder.binding.finalAmountLinear.setVisibility(View.GONE);
            }

            if (discount <= 0){
                viewHolder.binding.discountLinear.setVisibility(View.GONE);
            }



            Utils.setStyledText(activity,viewHolder.binding.totalAmoutAed,Utils.roundFloatValue(totalAmount));

            String cancellationPolicy = null;

            if ("whosin".equals(raynaTicketDetailModel.getBookingType())) {
                cancellationPolicy = model.getCancellationPolicy();
            } else if (model.getOptionDetail() != null) {
                cancellationPolicy = model.getOptionDetail().getCancellationPolicy();
            }

            boolean isNonRefundable = !TextUtils.isEmpty(cancellationPolicy) && cancellationPolicy.equalsIgnoreCase("Non Refundable");

            viewHolder.binding.cancellationPolicyLayout.setVisibility(GONE);
            viewHolder.binding.addOnLiner.setVisibility(View.GONE);
            viewHolder.binding.addOnLayout.setVisibility(View.GONE);

            if (model.getAddons() != null && !model.getAddons().isEmpty()) {

                List<TourOptionsModel> allAddons = RaynaTicketManager.shared.selectedAddonModels;
                List<TourOptionsModel> matchedAddons = new ArrayList<>();

                if (allAddons != null && !allAddons.isEmpty()) {
                    for (TourOptionsModel selectedAddon : model.getAddons()) {
                        if (selectedAddon == null || selectedAddon.get_id() == null) continue;
                        for (TourOptionsModel addon : allAddons) {
                            if (addon != null && addon.get_id() != null && addon.get_id().equals(selectedAddon.get_id())) {
                                matchedAddons.add(addon);
                                break;
                            }
                        }
                    }
                }

                viewHolder.addOnAdapter.isReadOnly = true;
                float addOnPrice = matchedAddons.stream().map(q -> q.updatePrice()).reduce(0f, Float::sum);

                if (!matchedAddons.isEmpty()) {
                    Utils.setStyledText(activity,viewHolder.binding.finalAmountAed,Utils.roundFloatValue(getWhosinTotal(model) + addOnPrice));
                    viewHolder.addOnAdapter.updateData(matchedAddons);
                    viewHolder.binding.addOnLiner.setVisibility(View.VISIBLE);
                    Utils.setStyledText(activity,viewHolder.binding.addonaed,Utils.roundFloatValue(addOnPrice));
                    viewHolder.binding.addOnLayout.setVisibility(View.VISIBLE);
                } else {
                    Utils.setStyledText(activity,viewHolder.binding.finalAmountAed,Utils.roundFloatValue(getWhosinTotal(model)));
                    viewHolder.addOnAdapter.updateData(new ArrayList<>());
                    viewHolder.binding.addOnLiner.setVisibility(View.GONE);
                    viewHolder.binding.addOnLayout.setVisibility(View.GONE);
                }

            } else {
                viewHolder.addOnAdapter.isReadOnly = true;
                viewHolder.binding.addOnLiner.setVisibility(GONE);
                viewHolder.binding.addOnLayout.setVisibility(View.GONE);
                Utils.setStyledText(activity,viewHolder.binding.finalAmountAed,Utils.roundFloatValue(getWhosinTotal(model)));
            }



            viewHolder.binding.btnMoreInfo.setOnClickListener(v -> {
                Utils.preventDoubleClick(v);
                RaynaMoreInfoBottomSheet bottomSheet = new RaynaMoreInfoBottomSheet();
                bottomSheet.activity = activity;
                bottomSheet.tourOptionsModel = model;
                bottomSheet.isNonRefundable = isNonRefundable;
                bottomSheet.isFromRaynaWhosinTicket = raynaTicketDetailModel.getBookingType().equals("whosin");
                bottomSheet.show(getSupportFragmentManager(),"");
            });

        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final ItemCheckoutTourOptionsDetailsBinding binding;

            private AddOnAdapter<TourOptionsModel> addOnAdapter = new AddOnAdapter<>();

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemCheckoutTourOptionsDetailsBinding.bind(itemView);
                binding.recyclerViewAddOns.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
                binding.recyclerViewAddOns.setAdapter(addOnAdapter);
            }
        }
    }

    private class WhosinCustomTourOptionDetailsAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_checkout_tour_options_details));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            WhosinTicketTourOptionModel model = (WhosinTicketTourOptionModel) getItem(position);
            if (model == null) return;

            viewHolder.binding.btnMoreInfoView.setText(getValue("Inclusions & Details"));
            viewHolder.binding.totalAmoutTv.setText(getValue("Ticket Amount"));
            viewHolder.binding.discountTv.setText(getValue("discount"));
            viewHolder.binding.finalAmountTv.setText(getValue("final_amount"));

            if (!TextUtils.isEmpty(model.getTourOptionSelectDate())) viewHolder.binding.tvTourDate.setText(model.getTourOptionSelectDate());

            if (TextUtils.isEmpty(raynaTicketDetailModel.getCity())){
                viewHolder.binding.locationLayout.setVisibility(GONE);
            }else {
                viewHolder.binding.tvAddress.setText(raynaTicketDetailModel.getCity());
                viewHolder.binding.locationLayout.setVisibility(VISIBLE);
            }

            if (model.getIsSlot() && model.getRaynaTimeSlotModel() != null){
                viewHolder.binding.tvTourTime.setText(model.getRaynaTimeSlotModel().getTimeSlot());
            } else if (!TextUtils.isEmpty(model.getSlotText())) {
                viewHolder.binding.tvTourTime.setText(model.getSlotText());
            }


            viewHolder.binding.tvType.setText(model.getTransType());
            viewHolder.binding.totalPersonLayout.setVisibility(TextUtils.isEmpty(model.getTransType()) ? GONE : VISIBLE);

            viewHolder.binding.titleOptionName.setText(model.getDisplayName());
            if (!TextUtils.isEmpty(model.getOptionDescription())) {
                viewHolder.binding.tvOptionDetail.setVisibility(VISIBLE);
                viewHolder.binding.tvOptionDetail.setText(model.getOptionDescription());
            } else {
                viewHolder.binding.tvOptionDetail.setVisibility(GONE);
            }



            float adultPrice = setTicketDetails(viewHolder.binding.totalAdults.tvTitle,viewHolder.binding.totalAdults.tvTitleValue, "Adults", model.getTmpAdultValue(), model.getWithoutDiscountAdultPrice(),viewHolder.binding.totalAdults.tvOriginalValue,model.getAdultPrice(),model.getAdultTitle());
            float childPrice = setTicketDetails(viewHolder.binding.totalChilds.tvTitle,viewHolder.binding.totalChilds.tvTitleValue, "Childs", model.getTmpChildValue(), model.getWithoutDiscountChildPrice(),viewHolder.binding.totalChilds.tvOriginalValue,model.getChildPrice(),model.getChildTitle());
            float infantPrice = setTicketDetails(viewHolder.binding.totalInfants.tvTitle,viewHolder.binding.totalInfants.tvTitleValue,"Infants", model.getTmpInfantValue(), model.getWithoutDiscountInfantPrice(),viewHolder.binding.totalInfants.tvOriginalValue,model.getInfantPrice(),model.getInfantTitle());


            float totalAmount = adultPrice + childPrice + infantPrice;


            if (raynaTicketDetailModel.getDiscount() == 0) {
                viewHolder.binding.discountLinear.setVisibility(View.GONE);
                Utils.setStyledText(activity,viewHolder.binding.finalAmountAed,Utils.roundFloatValue(totalAmount));

            } else {

                float discount = totalAmount - getWhosinCustomTicketTotal(model);

                Utils.setStyledText(activity,viewHolder.binding.discountAed,Utils.roundFloatValue(discount));

                if (totalAmount == discount) {
                    viewHolder.binding.discountLinear.setVisibility(View.GONE);
                    viewHolder.binding.finalAmountLinear.setVisibility(View.GONE);
                }

                if (discount <= 0){
                    viewHolder.binding.discountLinear.setVisibility(View.GONE);
                }

                Utils.setStyledText(activity,viewHolder.binding.finalAmountAed,Utils.roundFloatValue(getWhosinCustomTicketTotal(model)));

            }

            Utils.setStyledText(activity,viewHolder.binding.totalAmoutAed,Utils.roundFloatValue(totalAmount));

            String cancellationPolicy = null;

            if ("whosin".equals(raynaTicketDetailModel.getBookingType())) {
                cancellationPolicy = model.getCancellationPolicy();
            } else if (model.getOptionDetail() != null) {
                cancellationPolicy = model.getOptionDetail().getCancellationPolicy();
            }

            boolean isNonRefundable = !TextUtils.isEmpty(cancellationPolicy) && cancellationPolicy.equalsIgnoreCase("Non Refundable");

            viewHolder.binding.cancellationPolicyLayout.setVisibility(GONE);


            viewHolder.binding.btnMoreInfo.setOnClickListener(v -> {
                Utils.preventDoubleClick(v);
                RaynaMoreInfoBottomSheet bottomSheet = new RaynaMoreInfoBottomSheet();
                bottomSheet.activity = activity;
                bottomSheet.whosinTicketTourOptionModel = model;
                bottomSheet.isFromRaynaWhosinCustomTicket = true;
                bottomSheet.isNonRefundable = isNonRefundable;
                bottomSheet.show(getSupportFragmentManager(),"");
            });
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final ItemCheckoutTourOptionsDetailsBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemCheckoutTourOptionsDetailsBinding.bind(itemView);
            }
        }
    }

    private class TravelDeskTourOptionDetailsAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_checkout_tour_options_details));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            TravelDeskOptionDataModel model = (TravelDeskOptionDataModel) getItem(position);
            if (model == null) return;

            viewHolder.binding.btnMoreInfoView.setText(getValue("Inclusions & Details"));
            viewHolder.binding.totalAmoutTv.setText(getValue("Ticket Amount"));
            viewHolder.binding.discountTv.setText(getValue("discount"));
            viewHolder.binding.finalAmountTv.setText(getValue("final_amount"));

            if (!TextUtils.isEmpty(model.getTourOptionSelectDate())) viewHolder.binding.tvTourDate.setText(model.getTourOptionSelectDate());

            if (TextUtils.isEmpty(raynaTicketDetailModel.getCity())){
                viewHolder.binding.locationLayout.setVisibility(GONE);
            }else {
                viewHolder.binding.tvAddress.setText(raynaTicketDetailModel.getCity());
                viewHolder.binding.locationLayout.setVisibility(VISIBLE);
            }

            viewHolder.binding.tvTourTime.setText(model.getTravelDeskAvailabilityModel().getSlotText());

            viewHolder.binding.tvType.setVisibility(GONE);
            viewHolder.binding.totalPersonLayout.setVisibility(GONE);

            viewHolder.binding.titleOptionName.setText(model.getName());
            if (!TextUtils.isEmpty(model.getDescription())) {
                viewHolder.binding.tvOptionDetail.setVisibility(View.VISIBLE);
                Utils.addSeeMore(viewHolder.binding.tvOptionDetail, Html.fromHtml(model.getDescription()), 3, "... "  + getValue("see_more"), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ReadMoreBottomSheet bottomSheet = new ReadMoreBottomSheet();
                        bottomSheet.title = getValue("description");
                        bottomSheet.formattedDescription = model.getDescription();
                        bottomSheet.show(getSupportFragmentManager(),"");
                    }
                });
            } else {
                viewHolder.binding.tvOptionDetail.setVisibility(View.GONE);
            }

            float adultPrice = setTicketDetailsForTravelDesk(viewHolder.binding.totalAdults.tvTitle,viewHolder.binding.totalAdults.tvTitleValue, "Adults", model.getTmpAdultValue(), model.getWithoutDiscountAdultPrice(),viewHolder.binding.totalAdults.tvOriginalValue,model.getAdultPrice(),model.getAdultTitle());
            float childPrice = setTicketDetailsForTravelDesk(viewHolder.binding.totalChilds.tvTitle,viewHolder.binding.totalChilds.tvTitleValue, "Childs", model.getTmpChildValue(), model.getWithoutDiscountChildPrice(),viewHolder.binding.totalChilds.tvOriginalValue,model.getChildPrice(),model.getChildTitle());
            float infantPrice = setTicketDetailsForTravelDesk(viewHolder.binding.totalInfants.tvTitle,viewHolder.binding.totalInfants.tvTitleValue,"Infants", model.getTmpInfantValue(), model.getWithoutDiscountInfantPrice(),viewHolder.binding.totalInfants.tvOriginalValue,model.getInfantPrice(),model.getInfantTitle());

            viewHolder.binding.pricePerTrip.tvTitle.setText(getValue("price_per_trip"));
            viewHolder.binding.pricePerTrip.tvOriginalValue.setVisibility(GONE);
            Utils.setStyledText(activity,viewHolder.binding.pricePerTrip.tvTitleValue,Utils.roundFloatValue(model.getPricePerTrip()));
            viewHolder.binding.pricePerTrip.getRoot().setVisibility(VISIBLE);

            float totalAmount = adultPrice + childPrice + infantPrice + model.getPricePerTrip();


            float discount = totalAmount - (getWhosinTotalForTravelDesk(model) + model.getPricePerTrip());

            Utils.setStyledText(activity,viewHolder.binding.discountAed,Utils.roundFloatValue(discount));

            if (totalAmount == discount) {
                viewHolder.binding.discountLinear.setVisibility(View.GONE);
            }

            if (discount <= 0){
                viewHolder.binding.discountLinear.setVisibility(View.GONE);
            }

            Utils.setStyledText(activity,viewHolder.binding.finalAmountAed,Utils.roundFloatValue(model.getPricePerTrip() + getWhosinTotalForTravelDesk(model)));
            Utils.setStyledText(activity,viewHolder.binding.totalAmoutAed,Utils.roundFloatValue(totalAmount));


//            viewHolder.binding.btnCancellationPolicy.setText(!raynaTicketDetailModel.getFreeCancellation() ? "Non Refundable" : "Cancellation Policy");
//            int bgColor = ContextCompat.getColor(activity, !raynaTicketDetailModel.getFreeCancellation() ? R.color.ticket_non_ref_colour : R.color.cancellation_policy__bg);
//            viewHolder.binding.cancellationPolicyLayout.setBackgroundColor(bgColor);
            viewHolder.binding.cancellationPolicyLayout.setVisibility(GONE);


            viewHolder.binding.btnMoreInfo.setOnClickListener(v -> {
                Utils.preventDoubleClick(v);
                RaynaMoreInfoBottomSheet bottomSheet = new RaynaMoreInfoBottomSheet();
                bottomSheet.travelDeskOptionDataModel = model;
                bottomSheet.activity = activity;
                bottomSheet.isFromTravelDeskTicket = true;
                bottomSheet.isNonRefundable = !raynaTicketDetailModel.getFreeCancellation();
                bottomSheet.show(getSupportFragmentManager(),"");
            });

            viewHolder.binding.cancellationPolicyLayout.setOnClickListener(view -> {
                if (!raynaTicketDetailModel.getFreeCancellation()){
                    return;
                }

                Utils.preventDoubleClick(view);
                CancellationPolicyBottomSheet cancellationPolicyBottomSheet = new CancellationPolicyBottomSheet();
                cancellationPolicyBottomSheet.travelDeskOptionDataModel = model;
                cancellationPolicyBottomSheet.activity = activity;
                cancellationPolicyBottomSheet.isTravelDeskTicket = true;
                cancellationPolicyBottomSheet.show(getSupportFragmentManager(),"1");
            });


        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final ItemCheckoutTourOptionsDetailsBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemCheckoutTourOptionsDetailsBinding.bind(itemView);
            }
        }
    }

    private class BigBusTourOptionDetailsAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_checkout_tour_options_details));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            BigBusOptionsItemModel model = (BigBusOptionsItemModel) getItem(position);
            if (model == null) return;

            viewHolder.binding.btnMoreInfoView.setText(getValue("Inclusions & Details"));
            viewHolder.binding.totalAmoutTv.setText(getValue("Ticket Amount"));
            viewHolder.binding.discountTv.setText(getValue("discount"));
            viewHolder.binding.finalAmountTv.setText(getValue("final_amount"));

            if (!TextUtils.isEmpty(model.getTourOptionSelectDate())) viewHolder.binding.tvTourDate.setText(model.getTourOptionSelectDate());

            if (TextUtils.isEmpty(raynaTicketDetailModel.getCity())){
                viewHolder.binding.locationLayout.setVisibility(GONE);
            }else {
                viewHolder.binding.tvAddress.setText(raynaTicketDetailModel.getCity());
                viewHolder.binding.locationLayout.setVisibility(VISIBLE);
            }

            viewHolder.binding.tvTourTime.setText(model.getSlotText());

            viewHolder.binding.tvType.setVisibility(GONE);
            viewHolder.binding.totalPersonLayout.setVisibility(GONE);

            viewHolder.binding.titleOptionName.setText(model.getTitle());
            if (!TextUtils.isEmpty(model.getShortDescription())) {
                viewHolder.binding.tvOptionDetail.setVisibility(View.VISIBLE);
                Utils.addSeeMore(viewHolder.binding.tvOptionDetail, Html.fromHtml(model.getShortDescription()), 3, "... " + getValue("see_more"), v -> {
                    ReadMoreBottomSheet bottomSheet = new ReadMoreBottomSheet();
                    bottomSheet.title = getValue("description");
                    bottomSheet.formattedDescription = model.getShortDescription();
                    bottomSheet.show(getSupportFragmentManager(),"");
                });
            } else {
                viewHolder.binding.tvOptionDetail.setVisibility(View.GONE);
            }

            float adultPrice = setTicketDetailsForTravelDesk(viewHolder.binding.totalAdults.tvTitle,viewHolder.binding.totalAdults.tvTitleValue, "Adults", model.getTmpAdultValue(), model.getWithoutDiscountAdultPrice(),viewHolder.binding.totalAdults.tvOriginalValue,model.getAdultPrice(),model.getAdultTitle());
            float childPrice = setTicketDetailsForTravelDesk(viewHolder.binding.totalChilds.tvTitle,viewHolder.binding.totalChilds.tvTitleValue, "Childs", model.getTmpChildValue(), model.getWithoutDiscountChildPrice(),viewHolder.binding.totalChilds.tvOriginalValue,model.getChildPrice(),model.getChildTitle());
            float infantPrice = setTicketDetailsForTravelDesk(viewHolder.binding.totalInfants.tvTitle,viewHolder.binding.totalInfants.tvTitleValue,"Infants", model.getTmpInfantValue(), model.getWithoutDiscountInfantPrice(),viewHolder.binding.totalInfants.tvOriginalValue,model.getInfantPrice(),model.getInfantTitle());


            viewHolder.binding.pricePerTrip.getRoot().setVisibility(GONE);

            float totalAmount = adultPrice + childPrice + infantPrice;


            float discount = totalAmount - getWhosinTotalForBigBus(model);

            Utils.setStyledText(activity,viewHolder.binding.discountAed,Utils.roundFloatValue(discount));

            if (totalAmount == discount) {
                viewHolder.binding.discountLinear.setVisibility(View.GONE);
            }

            if (discount <= 0){
                viewHolder.binding.discountLinear.setVisibility(View.GONE);
            }

            Utils.setStyledText(activity,viewHolder.binding.finalAmountAed,Utils.roundFloatValue(getWhosinTotalForBigBus(model)));
            Utils.setStyledText(activity,viewHolder.binding.totalAmoutAed,Utils.roundFloatValue(totalAmount));


            viewHolder.binding.cancellationPolicyLayout.setVisibility(GONE);


            viewHolder.binding.btnMoreInfo.setOnClickListener(v -> {
                Utils.preventDoubleClick(v);
                RaynaMoreInfoBottomSheet bottomSheet = new RaynaMoreInfoBottomSheet();
                bottomSheet.bigBusOptionsItemModel = model;
                bottomSheet.activity = activity;
                bottomSheet.isFromBigBusTicket = true;
                bottomSheet.isNonRefundable = model.isNonRefundable();
                bottomSheet.show(getSupportFragmentManager(),"");
            });

        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final ItemCheckoutTourOptionsDetailsBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemCheckoutTourOptionsDetailsBinding.bind(itemView);
            }
        }
    }

    // endregion
    // --------------------------------------

}
