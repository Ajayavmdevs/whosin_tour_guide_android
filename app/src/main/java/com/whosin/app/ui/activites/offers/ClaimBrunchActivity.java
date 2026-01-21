package com.whosin.app.ui.activites.offers;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.Toast;

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
import com.whosin.app.databinding.ActivityClaimBrunchBinding;
import com.whosin.app.databinding.LayoutBrunchListBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.AppSettingManager;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.BrunchListModel;
import com.whosin.app.service.models.BrunchPackageModel;
import com.whosin.app.service.models.ClaimOfferModel;
import com.whosin.app.service.models.ClaimSpecialOfferModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.PageModel;
import com.whosin.app.service.models.PaymentCredentialModel;
import com.whosin.app.service.models.PaymentTabbyModel;
import com.whosin.app.service.models.SpecialOfferModel;
import com.whosin.app.service.models.VenueObjectModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.bucket.AlertDialogBox;
import com.whosin.app.ui.activites.comman.BaseActivity;
import com.whosin.app.ui.activites.raynaTicket.BottomSheets.SelectPaymentOptionBottomSheet;
import com.whosin.app.ui.activites.raynaTicket.BottomSheets.TabbyPaymentBottomSheet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class ClaimBrunchActivity extends BaseActivity {
    private ActivityClaimBrunchBinding binding;
    private int totalAmount = 0;
    private int discountCharges = 0;
    private String claimCode = "";
    private SpecialOfferModel specialOfferModel;
    private VenueObjectModel venueObjectModel;
    private BrunchAdapter<BrunchPackageModel> brunchAdapter = new BrunchAdapter();
    private JsonArray brunchArray = new JsonArray();
    private int totalDiscountedPrice = 0;

    private ClaimSpecialOfferModel claimOfferModel;
    private PaymentSheet paymentSheet;
    private boolean showPackage = true;

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    protected void initUi() {

        applyTranslations();

        String model = getIntent().getStringExtra("specialOfferModel");
        specialOfferModel = new Gson().fromJson(model, SpecialOfferModel.class);
        String venue = getIntent().getStringExtra("venueModel");
        venueObjectModel = new Gson().fromJson(venue, VenueObjectModel.class);

        binding.rvBrunchList.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        binding.rvBrunchList.setAdapter(brunchAdapter);
        setDetails();
        getBrunchList();
        Graphics.applyBlurEffectOnClaimScreen(activity, binding.blurView);

        binding.rvBrunchList.setVisibility(showPackage ? View.VISIBLE : View.GONE);
        binding.btnDropDown.setRotationX(showPackage ? 171f : 0f);

        Utils.setStyledText(activity,binding.txtTotalAmount,"0");
        Utils.setStyledText(activity,binding.txtTotal,"0");
        Utils.setStyledText(activity,binding.txtDiscount,"0");
        Utils.setStyledText(activity,binding.txtPackages,"0");


        paymentSheet = new PaymentSheet(ClaimBrunchActivity.this, paymentSheetResult -> {
            if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
                Utils.hideKeyboard(activity);
                Log.d("TAG", "Canceled");
            } else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
                Log.e("TAG", "Got error: ", ((PaymentSheetResult.Failed) paymentSheetResult).getError());
            } else if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
                startActivity(new Intent(activity, ClaimSuccessActivity.class)
                        .putExtra("claimSpecialModel", new Gson().toJson(claimOfferModel))
                        .putExtra("discountCharges",binding.txtTotal.getText().toString())
                        .putExtra("specialOfferModel", new Gson().toJson(specialOfferModel)).putExtra("title", venueObjectModel.getName()).putExtra("address", venueObjectModel.getAddress()).putExtra("image", venueObjectModel.getLogo()).putExtra("brunchList", brunchArray.toString()));
                finish();
            }
        });

    }


    @Override
    protected void setListeners() {
        binding.closeBtn.setOnClickListener(v -> onBackPressed());

        binding.pinView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (binding.pinView.getText().toString().length() == 4) {
                    claimCode = binding.pinView.getText().toString();
                    Utils.hideKeyboard(activity);
                }
            }
        });

        binding.claimNowButton.setOnClickListener(view -> {

            List<BrunchPackageModel> packages = brunchAdapter.getData().stream().filter(p -> p.getQuantity() > 0).collect(Collectors.toList());
            if (packages.isEmpty()) {
                Toast.makeText(this, getValue("please_select_quantity"), Toast.LENGTH_SHORT).show();
                return;
            }

            if (Utils.isNullOrEmpty(claimCode)) {
                AlertDialogBox alertDialogBox = new AlertDialogBox(getValue("Please enter claim code"));
                alertDialogBox.show(getSupportFragmentManager(), "1");
                return;
            }

            if (SessionManager.shared.getUser().isVip()) {
                requestClaimNow(1);
            }
            else {

                String amount = binding.txtTotal.getText().toString().trim().replace("D", "");
                SpannableString styledPrice = Utils.getStyledText(activity,amount);
                SpannableStringBuilder fullText = new SpannableStringBuilder();
                fullText.append(getValue("you_are_paying")).append(styledPrice).append(getValue("charges_for_special"));

                Graphics.alertDialogYesNoBtnWithUIFlag(activity, "WHOS'IN", fullText, false, getValue("cancel"), getValue("yes"), aBoolean -> {
                    if (aBoolean) {
                        int total = TextUtils.isEmpty(binding.txtTotal.getText().toString().trim()) ? 0 : Integer.parseInt(binding.txtTotal.getText().toString().trim().replace("D", ""));
                        if (total != 0){
                            SelectPaymentOptionBottomSheet bottmSheet = new SelectPaymentOptionBottomSheet();
                            bottmSheet.amount =(double) discountCharges;
                            bottmSheet.callback = data -> requestClaimNow(data);
                            bottmSheet.show(getSupportFragmentManager(), "");
                        }else {
                            requestClaimNow(1);
                        }

                    }
                });
            }
        });

        binding.layout.setOnClickListener(view -> {
            if (showPackage){
                binding.rvBrunchList.setVisibility(View.GONE);
                binding.btnDropDown.setRotationX(0f);
                showPackage = false;
            } else {
                binding.rvBrunchList.setVisibility(View.VISIBLE);
                binding.btnDropDown.setRotationX(171f);
                showPackage = true;
            }
        });
    }


    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityClaimBrunchBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected Map<View, String> getTranslationMap() {
        Map<View, String> map = new HashMap<>();
        map.put(binding.txtNoData, "no_package_available");
        map.put(binding.tvPakagesTitle, "packages");
        map.put(binding.tvSavingTitle, "savings");
        map.put(binding.startNumberTextView, "total_amount");
        map.put(binding.tvChargesPerClaimTitle, "charges_per_claim");
        map.put(binding.tvClaimNowTitle, "claim_now");
        map.put(binding.tvAsTheStaff, "claim_code_request_the_bill_venue");
        return map;
    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void setDetails() {
        if (venueObjectModel != null) {
            binding.venueContainer.setVenueDetail(venueObjectModel);
        }

        Optional<PageModel> claimTitle = AppSettingManager.shared.getAppSettingData().getPages().stream().filter(page -> "claim-title".equals(page.getTitle())).findFirst();
        if (claimTitle.isPresent()){
            binding.tvTitleForClaim.setText(claimTitle.get().getDescription());
        }

        Optional<PageModel> optionalPage = AppSettingManager.shared.getAppSettingData().getPages().stream().filter(page -> "claim-message".equals(page.getTitle())).findFirst();
        if (optionalPage.isPresent()) {
            binding.tvMessage.setText(optionalPage.get().getDescription());
        } else {
            binding.tvMessage.setVisibility(View.GONE);
        }
    }

    private void startStripeCheckOut(PaymentCredentialModel model) {
        if (model.publishableKey == null || model.clientSecret == null) {
            Toast.makeText(activity, "Invalid payment configuration", Toast.LENGTH_SHORT).show();
            return;
        }
        PaymentConfiguration.init(getApplicationContext(), model.publishableKey);
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


    private void tabbyCheckOut(PaymentTabbyModel model) {
        TabbyPaymentBottomSheet sheet = new TabbyPaymentBottomSheet();
        sheet.paymentTabbyModel = model;
        sheet.callback = p -> {
            if (!TextUtils.isEmpty(p)) {
                switch (p) {
                    case "success":
                        startActivity(new Intent(activity, ClaimSuccessActivity.class)
                                .putExtra("claimSpecialModel", new Gson().toJson(claimOfferModel))
                                .putExtra("discountCharges",binding.txtTotal.getText().toString())
                                .putExtra("specialOfferModel", new Gson().toJson(specialOfferModel)).putExtra("title", venueObjectModel.getName()).putExtra("address", venueObjectModel.getAddress()).putExtra("image", venueObjectModel.getLogo()).putExtra("brunchList", brunchArray.toString()));
                        finish();
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

    private double getDiscount(BrunchPackageModel model) {
        try {
            double originalPrice = Double.parseDouble(model.getAmount());
            double discountPercentage = Double.parseDouble(model.getDiscount());
            return ((originalPrice * discountPercentage) / 100);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private double getDiscountAmount(BrunchPackageModel model) {
        try {
            double originalPrice = Double.parseDouble(model.getAmount());
            double discountAmount = getDiscount(model);
            return originalPrice - discountAmount;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void getBrunchList() {

        showProgress();
        DataService.shared(activity).requestBrunchBySpecialOffer(specialOfferModel.getId(), new RestCallback<ContainerModel<SpecialOfferModel>>(this) {
            @Override
            public void result(ContainerModel<SpecialOfferModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (model.getData() != null) {
                    specialOfferModel = model.getData();
                    BrunchListModel currentBrunch = model.getData().getOffers();
                    if (currentBrunch != null && currentBrunch.getPackages().stream().anyMatch(BrunchPackageModel::getAllowClaim)) {
                        binding.layoutBrunch.setVisibility(View.VISIBLE);
                        binding.txtName.setText(currentBrunch.getTitle());
                        if (!currentBrunch.getPackages().isEmpty()) {
                            binding.rvBrunchList.setVisibility(View.VISIBLE);
                            binding.view1.setVisibility(View.VISIBLE);
                            currentBrunch.getPackages().removeIf(packageModel -> !packageModel.getAllowClaim());
                            brunchAdapter.updateData(currentBrunch.getPackages());
                        } else {
                            binding.rvBrunchList.setVisibility(View.GONE);
                            binding.view1.setVisibility(View.GONE);
                        }
                    } else {
                        Graphics.showAlertDialogWithOkButton(activity,  getString(R.string.app_name), getValue("no_offer_available_for_branch"), aBoolean -> {
                            if (aBoolean) { finish(); }
                        });
                    }
                } else {
                    Graphics.showAlertDialogWithOkButton(activity,  getString(R.string.app_name), getValue("no_offer_available_for_branch"), aBoolean -> {
                        if (aBoolean) { finish(); }
                    });
                }
            }
        });

    }

    private void requestClaimNow(int paymentMod) {
        List<BrunchPackageModel> packages = brunchAdapter.getData().stream().filter(p -> p.getQuantity() > 0).collect(Collectors.toList());
        JsonArray brunchArray = new JsonArray();
        packages.forEach( p -> {
            JsonObject brunchObject = new JsonObject();
            brunchObject.addProperty("qty", p.getQuantity());
            brunchObject.addProperty("amount", p.getDiscountedPrice());
//            if (p.getDiscount().equals("50")) {
//                brunchObject.addProperty("qty", p.getQuantity() * 2);
//                brunchObject.addProperty("amount", p.getDiscountedPrice() * 2);
//            } else {
//                brunchObject.addProperty("qty", p.getQuantity());
//                brunchObject.addProperty("amount", p.getDiscountedPrice());
//            }
            brunchObject.addProperty("discount", getDiscount(p));
            brunchObject.addProperty("itemId", p.getId());
            brunchObject.addProperty("item", p.getTitle());

            brunchObject.addProperty("pricePerBrunch", p.getPricePerBrunch());
            brunchArray.add(brunchObject);
        });
        Utils.hideKeyboard(activity);
        JsonObject object = new JsonObject();
        object.addProperty("specialOfferId", specialOfferModel.getId());
        object.addProperty("venueId", venueObjectModel.getId());
        object.addProperty("type", "brunch");
        object.addProperty("amount", discountCharges);
        object.add("brunch", brunchArray);
        object.addProperty("claimCode", claimCode);
        object.addProperty("currency", "aed");
        object.addProperty("billAmount", "");

        if (AppConstants.CARD_PAYMENT == paymentMod) {
            object.addProperty("paymentMethod", "stripe");
        } else if (AppConstants.PAY_WITH_LINK == paymentMod) {
            object.addProperty("paymentMethod", "stripe");
        } else if (AppConstants.TABBY_PAYMENT == paymentMod) {
            object.addProperty("paymentMethod", "tabby");
        }else if (AppConstants.GOOGLE_PAY == paymentMod || AppConstants.SAMSUNG_PAY == paymentMod) {
            object.addProperty("paymentMethod", "stripe");
        }

        Log.d("JsonObject", "requestClaimNow: " + object );

        showProgress();
        DataService.shared(activity).requestClaimSpecialOffer(object, new RestCallback<ContainerModel<ClaimOfferModel>>(this) {
            @Override
            public void result(ContainerModel<ClaimOfferModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity,  error,Toast.LENGTH_SHORT).show();
                    return;
                }

                claimOfferModel = model.getData().getResponse();
                if (Objects.equals(model.message, "Vip User Order Successfully Created!")) {
                    startActivity(new Intent(activity, ClaimSuccessActivity.class)
                            .putExtra("claimSpecialModel", new Gson().toJson(claimOfferModel))
                            .putExtra("discountCharges",binding.txtTotal.getText().toString())
                            .putExtra("specialOfferModel", new Gson().toJson(specialOfferModel)).putExtra("title", venueObjectModel.getName()).putExtra("address", venueObjectModel.getAddress()).putExtra("image", venueObjectModel.getLogo()).putExtra("brunchList", brunchArray.toString()));
                    finish();
                } else if (model.getData() != null) {
                    int total = TextUtils.isEmpty(binding.txtTotal.getText().toString().trim()) ? 0 : Integer.parseInt(binding.txtTotal.getText().toString().trim().replace("D ", ""));
                    if (total != 0){
                        if (AppConstants.CARD_PAYMENT == paymentMod) {
                            startStripeCheckOut(model.getData().getObjToSend());
                        } else if (AppConstants.PAY_WITH_LINK == paymentMod) {
                            startStripeCheckOut(model.getData().getObjToSend());
                        }else if (AppConstants.GOOGLE_PAY == paymentMod || AppConstants.SAMSUNG_PAY == paymentMod) {
                            startGooglePayCheckOut(model.getData().getObjToSend());
                        } else if (AppConstants.TABBY_PAYMENT == paymentMod) {
                            if (model.getData().getTabbyModel() != null && !TextUtils.isEmpty(model.getData().getTabbyModel().getWeb_url())) {
                                tabbyCheckOut(model.getData().getTabbyModel());
                            } else {
                                Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), getValue("tabby_payment_failed"));
                            }
                        }
                    }else {
                        startActivity(new Intent(activity, ClaimSuccessActivity.class)
                                .putExtra("claimSpecialModel", new Gson().toJson(claimOfferModel))
                                .putExtra("discountCharges",binding.txtTotal.getText().toString())
                                .putExtra("specialOfferModel", new Gson().toJson(specialOfferModel)).putExtra("title", venueObjectModel.getName()).putExtra("address", venueObjectModel.getAddress()).putExtra("image", venueObjectModel.getLogo()).putExtra("brunchList", brunchArray.toString()));
                        finish();
                    }


                }

            }
        });
    }

    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    public class BrunchAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = UiUtils.getViewBy(parent, R.layout.layout_brunch_list);
            return new BrunchAdapter.ViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            BrunchPackageModel model = (BrunchPackageModel) getItem(position);
            viewHolder.mBinding.txtName.setText(model.getTitle());

            SpannableString styledPrice = Utils.getStyledText(activity,String.valueOf(model.getPricePerBrunch()));
            SpannableStringBuilder fullText = new SpannableStringBuilder();
            fullText.append(getValue("charges_per_claim"));
            fullText.append(" ( ").append(styledPrice).append(" )");

//            viewHolder.mBinding.txtDiscountChanges.setText(String.format(Locale.ENGLISH,
//                    "Charges per claim " + "(" + "AED " + model.getPricePerBrunch() + ")"));

            viewHolder.mBinding.txtDiscountChanges.setText(fullText);




            double discountAmount = getDiscount(model);
            double finalPrice = getDiscountAmount(model);

            int roundedPrice = (int) Math.ceil(finalPrice);

//            model.setDiscountedPrice((int) finalPrice);
            model.setDiscountedPrice(roundedPrice);

            if (model.getDiscount().equals("50")) {
//                viewHolder.mBinding.txtNewPrice.setText(String.format("AED %s", model.getAmount()));
                Utils.setStyledText(activity,viewHolder.mBinding.txtNewPrice,model.getAmount());
            } else {
//                viewHolder.mBinding.txtNewPrice.setText(String.format("AED %s", roundedPrice));
                Utils.setStyledText(activity,viewHolder.mBinding.txtNewPrice,String.valueOf(roundedPrice));
            }


            if (discountAmount > 0) {
//                viewHolder.mBinding.txtOriginalPrice.setText(String.format("AED %s", Utils.formatAmount(model.getAmount())));
                Utils.setStyledText(activity,viewHolder.mBinding.txtOriginalPrice,Utils.formatAmount(model.getAmount()));
                viewHolder.mBinding.txtOriginalPrice.setPaintFlags(viewHolder.mBinding.txtOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                viewHolder.mBinding.txtOriginalPrice.setVisibility(View.VISIBLE);
                viewHolder.mBinding.txtNewPrice.setVisibility(View.VISIBLE);

                if (model.getDiscount().equals("50")) {
                    viewHolder.mBinding.txtOriginalPrice.setVisibility(View.GONE);
                } else {
                    viewHolder.mBinding.txtOriginalPrice.setVisibility(View.VISIBLE);
                }

            }
            else {
                viewHolder.mBinding.txtOriginalPrice.setVisibility(View.GONE);
                viewHolder.mBinding.txtNewPrice.setVisibility(View.VISIBLE);
            }



            viewHolder.mBinding.tvQty.setText("0");
            viewHolder.mBinding.ivPlus.setOnClickListener(view -> {
                if (model.getQuantity() < 8) {
                    model.setDiscount50("50".equals(model.getDiscount()));
                    model.setQuantity(model.getQuantity() + 1);
                    viewHolder.mBinding.tvQty.setText(String.valueOf(model.getQuantity()));
                    updateTotalAmount( );
                }
            });

            viewHolder.mBinding.ivMinus.setOnClickListener(view -> {
                if (model.getQuantity() > 0) {
                    model.setDiscount50("50".equals(model.getDiscount()));
                    model.setQuantity(model.getQuantity() - 1);
                    viewHolder.mBinding.tvQty.setText(String.valueOf(model.getQuantity()));
                    updateTotalAmount();
                }
            });

        }

        @SuppressLint("DefaultLocale")
        private void updateTotalAmount() {
            totalAmount = brunchAdapter.getData().stream().mapToInt(p ->p.getQuantity() * p.getDiscountedPrice()
//                    p.isDiscount50() ? ( p.getQuantity() * 2) * p.getDiscountedPrice() :  p.getQuantity() * p.getDiscountedPrice()
            ).sum();

//            binding.txtTotalAmount.setText(String.format("AED %d", totalAmount));
            Utils.setStyledText(activity,binding.txtTotalAmount,String.valueOf(totalAmount));

            discountCharges = brunchAdapter.getData().stream().mapToInt(p -> p.getQuantity() * p.getPricePerBrunch()).sum();
//            binding.txtTotal.setText("AED " + Utils.formatAmount(String.valueOf(discountCharges)));
            Utils.setStyledText(activity,binding.txtTotal,Utils.formatAmount(String.valueOf(discountCharges)));

            totalDiscountedPrice = brunchAdapter.getData().stream().mapToInt(p -> ((p.getQuantity() * Integer.parseInt(p.getAmount())) * p.getDiscountValue()) / 100).sum();
//            binding.txtDiscount.setText("AED " + Utils.formatAmount(String.valueOf(totalDiscountedPrice)));
            Utils.setStyledText(activity,binding.txtDiscount,Utils.formatAmount(String.valueOf(totalDiscountedPrice)));

            int packages = brunchAdapter.getData().stream().mapToInt(p -> p.getQuantity() * Integer.parseInt(p.getAmount())).sum();
//            binding.txtPackages.setText("AED " + Utils.formatAmount(String.valueOf(packages)));
            Utils.setStyledText(activity,binding.txtPackages,Utils.formatAmount(String.valueOf(packages)));
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private LayoutBrunchListBinding mBinding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = LayoutBrunchListBinding.bind(itemView);

            }
        }
    }

    // endregion
    // --------------------------------------
}