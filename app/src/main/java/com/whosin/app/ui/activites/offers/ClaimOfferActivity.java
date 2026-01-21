package com.whosin.app.ui.activites.offers;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;
import com.whosin.app.R;
import com.whosin.app.comman.AppConstants;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.databinding.ActivityClaimOfferBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.AppSettingManager;
import com.whosin.app.service.manager.RaynaTicketManager;
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
import com.whosin.app.ui.activites.venue.ui.BuyNowActivity;
import com.whosin.app.ui.activites.wallet.WalletActivity;
import com.whosin.app.ui.fragment.wallet.PurchaseSuccessFragment;

import org.jsoup.Jsoup;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;


public class ClaimOfferActivity extends BaseActivity {

    private ActivityClaimOfferBinding binding;
    private String totalPerson = "", claimCode = "";
    private SpecialOfferModel specialOfferModel;
    private ClaimSpecialOfferModel claimOfferModel;
    private VenueObjectModel venueObjectModel;
    private PaymentSheet paymentSheet;
    private int billTotal;
    private String amount= "0";

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    protected void initUi() {

        applyTranslations();

        Graphics.applyBlurEffectOnClaimScreen(activity, binding.blurView);
        String model = getIntent().getStringExtra("specialOfferModel");
        specialOfferModel = new Gson().fromJson(model, SpecialOfferModel.class);
        String venue = getIntent().getStringExtra("venueModel");
        venueObjectModel = new Gson().fromJson(venue, VenueObjectModel.class);
        setDetails(venueObjectModel);

        Utils.setStyledText(activity,binding.txtTotal,"0");

        paymentSheet = new PaymentSheet(ClaimOfferActivity.this, paymentSheetResult -> {
            if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
                Utils.hideKeyboard(activity);
                Log.d("TAG", "Canceled");
            } else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
                Utils.hideKeyboard(activity);
                Log.e("TAG", "Got error: ", ((PaymentSheetResult.Failed) paymentSheetResult).getError());
            } else if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
                startActivity(new Intent(activity, ClaimSuccessActivity.class)
                        .putExtra("claimSpecialModel", new Gson().toJson(claimOfferModel))
                        .putExtra("discountCharges", binding.txtTotal.getText().toString())
                        .putExtra("specialOfferModel", new Gson().toJson(specialOfferModel))
                        .putExtra("title", venueObjectModel.getName()).putExtra("address", venueObjectModel
                                .getAddress()).putExtra("image", venueObjectModel.getLogo()));
                finish();
            }
        });

    }

    @Override
    protected void setListeners() {
        binding.closeBtn.setOnClickListener(v -> {
            onBackPressed();
        });


        binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                totalPerson = String.valueOf(seekBar.getProgress());
                binding.startNumberTextView.setText(totalPerson + "");
                calculate(totalPerson);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

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
            if (Utils.isNullOrEmpty(totalPerson)) {
                AlertDialogBox alertDialogBox = new AlertDialogBox(getValue("please_select_number_of_person"));
                alertDialogBox.show(getSupportFragmentManager(), "1");
                return;
            }
            if (Utils.isNullOrEmpty(claimCode)) {
                AlertDialogBox alertDialogBox = new AlertDialogBox(getValue("please_enter_claim_code"));
                alertDialogBox.show(getSupportFragmentManager(), "1");
                return;
            }


            if (billTotal != 0){
                SelectPaymentOptionBottomSheet bottmSheet = new SelectPaymentOptionBottomSheet();
                bottmSheet.amount = (double) billTotal;
                bottmSheet.callback = data ->requestClaimNow(amount,data);
                bottmSheet.show(getSupportFragmentManager(), "");
            }else {
                requestClaimNow(amount,1);
            }



        });
    }

    private void calculate(String totalPerson) {
        try {
            double person = Double.parseDouble(totalPerson);
            double amount = Double.parseDouble(specialOfferModel.getPricePerPerson());
            double total = person * amount;
            billTotal = (int) total;
//            binding.txtTotal.setText("AED "+billTotal);
            Utils.setStyledText(activity,binding.txtTotal,String.valueOf(billTotal));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityClaimOfferBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected Map<View, String> getTranslationMap() {
        Map<View, String> map = new HashMap<>();
        map.put(binding.txtDescription, "for_users_on_total_bill");
        map.put(binding.tvNumberOfPeopleTitle, "number_of_people");
        map.put(binding.tvChargePerPersonTitle, "charges_per_person");
        map.put(binding.tvClaimNowTitle, "claim_now");
        map.put(binding.askStaffTitle, "claim_code_request_the_bill_venue");
        return map;
    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void setDetails(VenueObjectModel venueObjectModel) {

        if (venueObjectModel != null) {

            binding.venueContainer.setVenueDetail(venueObjectModel);

        }

        Optional<PageModel> claimTitle = AppSettingManager.shared.getAppSettingData().getPages().stream()
                .filter(page -> "claim-title".equals(page.getTitle()))
                .findFirst();
        if (claimTitle.isPresent()){
            binding.tvTitleForClaim.setText(claimTitle.get().getDescription());
        }


        Optional<PageModel> optionalPage = AppSettingManager.shared.getAppSettingData().getPages().stream()
                .filter(page -> "claim-message".equals(page.getTitle()))
                .findFirst();

        if (optionalPage.isPresent()) {
            String htmlText = optionalPage.get().getDescription();
            String plainText = Jsoup.parse(htmlText).text();
            binding.tvMessage.setText(plainText);        } else {
            binding.tvMessage.setVisibility(View.GONE);
        }

        if (specialOfferModel.getMaxPersonAllowed() != null) {
            binding.seekBar.setMax(Integer.parseInt(specialOfferModel.getMaxPersonAllowed()));
            binding.endNumberTextView.setText(specialOfferModel.getMaxPersonAllowed());
        }
        else {
            binding.seekBar.setMax(0);
            binding.endNumberTextView.setText("0");
        }

        if (Utils.isNullOrEmpty(String.valueOf(specialOfferModel.getDiscount()  ))) {
            binding.txtDiscount.setText("0% " + getValue("Discount"));
            binding.tvDiscountLabel.setText("0%");
        } else {
            binding.txtDiscount.setText(specialOfferModel.getDiscount() + "% " + getValue("discount"));
            binding.tvDiscountLabel.setText(specialOfferModel.getDiscount() + "%");
        }
//        binding.txtPricePerPerson.setText("(AED "+specialOfferModel.getPricePerPerson()+"/px)");

        SpannableString styledPrice = Utils.getStyledText(activity,specialOfferModel.getPricePerPerson());
        SpannableStringBuilder fullText = new SpannableStringBuilder();
        fullText.append("(").append(styledPrice).append(styledPrice).append("/px)");

        binding.txtPricePerPerson.setText(styledPrice);

        // binding.txtDescription.setText(specialOfferModel.getDescription());

    }

    private void startStripeCheckOut(PaymentCredentialModel model) {
        if (model == null){return;}
        if (model.publishableKey == null || model.clientSecret == null){return;}
        if (model.publishableKey.isEmpty()){return;}
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
        if (model == null){return;}
        TabbyPaymentBottomSheet sheet = new TabbyPaymentBottomSheet();
        sheet.paymentTabbyModel = model;
        sheet.callback = p -> {
            if (!TextUtils.isEmpty(p)) {
                switch (p) {
                    case "success":
                        startActivity(new Intent(activity, ClaimSuccessActivity.class)
                                .putExtra("claimSpecialModel", new Gson().toJson(claimOfferModel))
                                .putExtra("discountCharges", binding.txtTotal.getText().toString())
                                .putExtra("specialOfferModel", new Gson().toJson(specialOfferModel))
                                .putExtra("title", venueObjectModel.getName()).putExtra("address", venueObjectModel
                                        .getAddress()).putExtra("image", venueObjectModel.getLogo()));
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

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestClaimNow(String amount,int paymentMod) {

        showProgress();
        JsonObject object = new JsonObject();
        object.addProperty("specialOfferId", specialOfferModel.getId());
        object.addProperty("venueId", venueObjectModel.getId());
        object.addProperty("type", "total");
        object.addProperty("billAmount", "");
        object.addProperty("amount", billTotal);
        object.addProperty("totalPerson", totalPerson);
        object.addProperty("claimCode", claimCode);
        object.addProperty("currency", "aed");

        if (AppConstants.CARD_PAYMENT == paymentMod) {
            object.addProperty("paymentMethod", "stripe");
        } else if (AppConstants.PAY_WITH_LINK == paymentMod) {
            object.addProperty("paymentMethod", "stripe");
        } else if (AppConstants.TABBY_PAYMENT == paymentMod) {
            object.addProperty("paymentMethod", "tabby");
        }else if (AppConstants.GOOGLE_PAY == paymentMod || AppConstants.SAMSUNG_PAY == paymentMod) {
            object.addProperty("paymentMethod", "stripe");
        }

        Log.d("requestClaimNow", "requestClaimNow: " + object);

        DataService.shared(activity).requestClaimSpecialOffer(object, new RestCallback<ContainerModel<ClaimOfferModel>>(this) {
            @Override
            public void result(ContainerModel<ClaimOfferModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity,error, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (Objects.equals(model.message, "Vip User Order Successfully Created!")) {
                    claimOfferModel = model.getData().getResponse();
                    startActivity(new Intent(activity, ClaimSuccessActivity.class)
                            .putExtra("claimSpecialModel", new Gson().toJson(claimOfferModel))
                            .putExtra("discountCharges", binding.txtTotal.getText().toString())
                            .putExtra("specialOfferModel", new Gson().toJson(specialOfferModel))
                            .putExtra("title", venueObjectModel.getName()).putExtra("address", venueObjectModel
                                    .getAddress()).putExtra("image", venueObjectModel.getLogo()));
                    finish();
                }
                else if (model.getData() != null) {
                    claimOfferModel = model.getData().getResponse();
                    if (billTotal != 0 ) {
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
                    }
                    else {
                        startActivity(new Intent(activity, ClaimSuccessActivity.class).putExtra("claimSpecialModel", new Gson().toJson(claimOfferModel))
                                .putExtra("discountCharges", binding.txtTotal.getText().toString())
                                .putExtra("specialOfferModel", new Gson().toJson(specialOfferModel)).putExtra("title", venueObjectModel.getName()).putExtra("address", venueObjectModel.getAddress()).putExtra("image", venueObjectModel.getLogo()).putExtra("amount",amount ));
                        finish();
                    }
                }

            }
        });
    }


    // endregion
}