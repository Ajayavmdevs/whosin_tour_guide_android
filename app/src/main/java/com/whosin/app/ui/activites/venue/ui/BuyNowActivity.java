package com.whosin.app.ui.activites.venue.ui;

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

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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
import com.whosin.app.databinding.ActivityBuyNowBinding;
import com.whosin.app.databinding.ItemBuyNowBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.AppSettingManager;
import com.whosin.app.service.models.CartModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.ExclusiveDealModel;
import com.whosin.app.service.models.PaymentCredentialModel;
import com.whosin.app.service.models.VenueMetaDataPromoCodeModel;
import com.whosin.app.service.models.VenueObjectModel;
import com.whosin.app.service.models.VenuePromoCodeModel;
import com.whosin.app.service.models.VoucherModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.comman.BaseActivity;
import com.whosin.app.ui.activites.offers.DisclaimerBottomSheet;
import com.whosin.app.ui.activites.raynaTicket.BottomSheets.SelectPaymentOptionBottomSheet;
import com.whosin.app.ui.activites.raynaTicket.BottomSheets.TabbyPaymentBottomSheet;
import com.whosin.app.ui.activites.wallet.WalletActivity;
import com.whosin.app.ui.fragment.wallet.PurchaseSuccessFragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class BuyNowActivity extends BaseActivity {

    private ActivityBuyNowBinding binding;
    private final BuyNowAdapter<VoucherModel> packageAdapter = new BuyNowAdapter<>();
    private ExclusiveDealModel dealModel;
    private VenueObjectModel venueDetail;
    private PaymentSheet paymentSheet;
    private List<VoucherModel> offersModelList = new ArrayList<>();
    private VoucherModel voucherModel;
    private boolean isShowBtn = false;
    private boolean isPromoCodeApply = false;
    private VenuePromoCodeModel promoCodeModel = null;

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    protected void initUi() {

        binding.packageRecycler.setLayoutManager( new LinearLayoutManager( activity, LinearLayoutManager.VERTICAL, false ) );
        binding.packageRecycler.setAdapter( packageAdapter );
        String venueModel = Utils.notNullString( getIntent().getStringExtra( "venueDetail" ) );
        venueDetail = new Gson().fromJson( venueModel, VenueObjectModel.class );

        String model = getIntent().getStringExtra( "offerModel" );
        voucherModel = new Gson().fromJson( model, VoucherModel.class );

        if (voucherModel != null) {
            offersModelList.add( voucherModel );
            if (voucherModel.getVenue() != null) {
                binding.venueContainer.setVenueDetail( voucherModel.getVenue() );
            } else {
                binding.venueContainer.setVenueDetail( venueDetail );
            }

            packageAdapter.updateData( offersModelList );
        } else {
            binding.venueContainer.setVenueDetail( venueDetail );
        }

        Utils.changeStatusBarColor( getWindow(), getResources().getColor( R.color.buy_screen_header ) );
        List<CartModel> cartListItem = CartModel.getCartHistory();
        int cartSize = !cartListItem.isEmpty() ? cartListItem.size() : 0;
        binding.cartItemLayout.setVisibility( cartSize > 0 ? View.VISIBLE : View.GONE );
        binding.tvCartItem.setText( String.valueOf( cartSize ) );


        paymentSheet = new PaymentSheet( BuyNowActivity.this, paymentSheetResult -> {
            if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
                Log.d( "TAG", "Canceled" );
            } else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
                Log.e( "TAG", "Got error: ", ((PaymentSheetResult.Failed) paymentSheetResult).getError() );
            } else if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
                AppSettingManager.shared.tmpCartList.clear();
                PurchaseSuccessFragment purchaseSuccessFragment = new PurchaseSuccessFragment();
                purchaseSuccessFragment.callBack = data -> {
                    finish();
                    if (data) {
                        startActivity(new Intent(Graphics.context, WalletActivity.class));
                    }
                };
                purchaseSuccessFragment.show( getSupportFragmentManager(), "" );

            }
        } );

        Graphics.applyBlurEffect( activity, binding.blurView );

    }

    @Override
    protected void setListeners() {

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

            if (AppSettingManager.shared.tmpCartList.stream().allMatch(p -> p.qty == 0)) {
                Toast.makeText(this, "Please select items before applying the promo code", Toast.LENGTH_SHORT).show();
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

        binding.ivClose.setOnClickListener( view -> {
            onBackPressed();
            AppSettingManager.shared.tmpCartList.clear();
        } );

        binding.imageMyCart.setOnClickListener( view -> {
            startActivity( new Intent( activity, MyCartActivity.class ) );
        } );

        binding.addToCartButton.setOnClickListener( view -> {
            List<CartModel> items = AppSettingManager.shared.tmpCartList.stream().filter( p -> p.qty > 0 ).collect( Collectors.toList() );
            if (!items.isEmpty()) {
                items.forEach( p -> {
                    CartModel.addToCart( p.id, p.type, p.voucherModel, null, p.voucherModel.getVenue(), p.qty, "", "", dealModel, 0, p.getDiscountAmount() );
                } );
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
                Toast.makeText( this, R.string.select_quantity, Toast.LENGTH_SHORT ).show();
            }

        } );

        binding.checkoutButton.setOnClickListener( view -> {
            if (AppSettingManager.shared.tmpCartList.isEmpty()) {
                Toast.makeText( this, "Please select items quantity to checkout", Toast.LENGTH_SHORT ).show();
                return;
            }
            if (AppSettingManager.shared.tmpCartList.stream().allMatch( p -> p.qty == 0 )) {
                Toast.makeText( this, "Please select items quantity to checkout", Toast.LENGTH_SHORT ).show();
                return;
            }
            List<CartModel> items = AppSettingManager.shared.tmpCartList.stream().filter( p -> p.qty > 0 ).collect( Collectors.toList() );
            if (items.isEmpty()) {
                Toast.makeText( this, "Please select items quantity to checkout", Toast.LENGTH_SHORT ).show();
                return;
            }

            JsonArray metaDate = new JsonArray();
            items.forEach( model -> {
                int price = model.qty * model.voucherModel.getDiscountedPrice();
                JsonObject itemMetaData = new JsonObject();
                itemMetaData.addProperty( "type", "deal" );
                itemMetaData.addProperty( "dealId", model.voucherModel.getId() );
                // itemMetaData.addProperty("voucherId", model.voucherModel.getId());
                itemMetaData.addProperty( "price", price );
                itemMetaData.addProperty( "qty", model.qty );
                itemMetaData.addProperty( "venueId", model.voucherModel.getVenueId() );
                metaDate.add( itemMetaData );
            } );

            int totalAmount = 0;

            if (isPromoCodeApply) {
                totalAmount = AppSettingManager.shared.tmpCartList.stream().mapToInt(p -> p.getQty() * Integer.parseInt(p.voucherModel.getActualPrice())).sum();
            } else {
                totalAmount = AppSettingManager.shared.tmpCartList.stream().mapToInt(p -> p.getQty() * p.voucherModel.getDiscountedPrice()).sum();
            }


            JsonObject params = new JsonObject();
            if (!TextUtils.isEmpty(binding.edtPromoCode.getText().toString()) && isPromoCodeApply){
                params.addProperty("promoCode", binding.edtPromoCode.getText().toString().trim());
                params.addProperty("totalAmount", String.valueOf(totalAmount));
            }

            if (promoCodeModel != null) {
                double discountedAmount = totalAmount - promoCodeModel.getTotalDiscount();
                params.addProperty("amount", discountedAmount);
                params.addProperty("discount", promoCodeModel.getTotalDiscount());
            } else {
                params.addProperty("amount", totalAmount);

            }

            params.addProperty( "currency", "aed" );
            params.add( "metadata", metaDate );

            SelectPaymentOptionBottomSheet bottmSheet = new SelectPaymentOptionBottomSheet();
            double price = Double.parseDouble(binding.tvPrice.getText().toString().replace("AED ", "").trim());
            bottmSheet.amount = price;
            bottmSheet.callback = data -> requestStripeToken(params,data);
            bottmSheet.show(getSupportFragmentManager(), "");
        } );

        if (voucherModel != null) {
            binding.tvTerms.setVisibility( (voucherModel.getDisclaimerTitle() != null &&
                    !voucherModel.getDisclaimerTitle().isEmpty()) ? View.VISIBLE : View.GONE );
        }

        binding.tvTerms.setOnClickListener( v -> {
            if (voucherModel != null) {
                if (voucherModel.getDisclaimerTitle() != null && !voucherModel.getDisclaimerTitle().isEmpty()) {
                    DisclaimerBottomSheet disclaimerBottomSheet = new DisclaimerBottomSheet();
                    disclaimerBottomSheet.disclaimerTitle = voucherModel.getDisclaimerTitle();
                    disclaimerBottomSheet.disclaimerDescription = voucherModel.getDisclaimerDescription();
                    disclaimerBottomSheet.show( getSupportFragmentManager(), "DisclaimerBottomSheet" );
                }
            }

        } );

    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityBuyNowBinding.inflate( getLayoutInflater() );
        return binding.getRoot();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AppSettingManager.shared.tmpCartList.clear();
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<CartModel> cartListItem = CartModel.getCartHistory();
        int cartSize = !cartListItem.isEmpty() ? cartListItem.size() : 0;
        binding.cartItemLayout.setVisibility( cartSize > 0 ? View.VISIBLE : View.GONE );
        binding.tvCartItem.setText( String.valueOf( cartSize ) );
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
        PaymentConfiguration.init( getApplicationContext(), model.publishableKey );
        final PaymentSheet.Configuration configuration = new PaymentSheet.Configuration.Builder("Whosin, Inc.").allowsDelayedPaymentMethods(true).build();

        paymentSheet.presentWithPaymentIntent( model.clientSecret, configuration );
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
            itemMetaData.addProperty("type", "deal");
            itemMetaData.addProperty("dealId",  model.voucherModel.getId());
            itemMetaData.addProperty("amount",  model.voucherModel.getActualPrice());
            if (Integer.parseInt(model.voucherModel.getDiscountValue()) != 0) itemMetaData.addProperty("discount",model.voucherModel.getDiscountValue());
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
            binding.btnApplyPromoCode.setText("Remove");
            binding.btnApplyPromoCode.setTextColor(ContextCompat.getColor(this,R.color.red));

            SpannableString styledPrice = Utils.getStyledText(activity,String.valueOf(promoCodeModel.getPromoDiscount()));
            SpannableStringBuilder fullText = new SpannableStringBuilder();
            fullText.append(styledPrice).append(" saved with ").append(binding.edtPromoCode.getText().toString());
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
        binding.btnApplyPromoCode.setText("Apply");
        binding.btnApplyPromoCode.setTextColor(ContextCompat.getColor(this,R.color.brand_pink));
        binding.btnApplyPromoCode.setVisibility(View.GONE);
        binding.layoutPromoCodeApply.setVisibility(View.GONE);
        binding.ivDropDown.setRotation(90f);
        resetCalcaution();
    }

    private void resetCalcaution(){
        int totalAmount = AppSettingManager.shared.tmpCartList.stream().mapToInt( p -> p.getQty() * p.voucherModel.getDiscountedPrice() ).sum();
        int totalAED = AppSettingManager.shared.tmpCartList.stream().mapToInt(p -> {int qty = p.getQty();
                    int actualPrice = Integer.parseInt(p.voucherModel.getActualPrice());
                    return qty * actualPrice;
                })
                .sum();

        int totalSaving = totalAED - totalAmount;

        Utils.setStyledText(activity,binding.tvYourSaving,String.valueOf(totalSaving));
        Utils.setStyledText(activity,binding.tvTotalDiscount,String.valueOf(totalSaving));

        binding.YourSavingLayout.setVisibility(totalSaving > 0 ? View.VISIBLE : View.GONE);

        binding.tvPrice.setVisibility( totalAmount > 0 ? View.VISIBLE : View.GONE );

        Utils.setStyledText(activity,binding.tvPrice,String.valueOf(totalAmount));


        int qtySum = AppSettingManager.shared.tmpCartList.stream().mapToInt(p -> p.qty).sum();


        binding.btnApplyPromoCode.setVisibility(qtySum > 0 ? View.VISIBLE : View.GONE);
        binding.YourSavingLayout.setVisibility(qtySum > 0 ? View.VISIBLE : View.GONE);


        if (qtySum == 0){
            binding.YourSavingLayout.setVisibility(View.GONE);
            binding.layoutPromoAndDiscount.setVisibility(View.GONE);
        }
    }

    private void updateAdapterDataAfterPromoCode() {
        if (promoCodeModel != null) {
            packageAdapter.notifyDataSetChanged();
        }
    }

    private VenueMetaDataPromoCodeModel getPromoCodeModel(String dealID) {
        if (promoCodeModel == null || promoCodeModel.getMetaData().isEmpty()) {
            return null;
        }
        return promoCodeModel.getMetaData().stream().filter(p -> p.getDealId().equals(dealID)).findFirst().orElse(null);
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
        } else if (AppConstants.GOOGLE_PAY == paymentMod || AppConstants.SAMSUNG_PAY == paymentMod) {
            jsonObject.addProperty("paymentMethod", "stripe");
        } else if (AppConstants.TABBY_PAYMENT == paymentMod) {
            jsonObject.addProperty("paymentMethod", "tabby");
        }
        showProgress();
        DataService.shared( this ).requestStripePaymentIntent( jsonObject, new RestCallback<ContainerModel<PaymentCredentialModel>>(this) {
            @Override
            public void result(ContainerModel<PaymentCredentialModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty( error ) || model == null) {
                    Toast.makeText( activity, error, Toast.LENGTH_SHORT ).show();
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
                    purchaseSuccessFragment.show( getSupportFragmentManager(), "" );
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
        } );
    }


    private void requestVenuePromoCode() {
        if (getJsonObject().isEmpty() || getJsonObject().isJsonNull()) return;
        binding.promoCodeProgressView.setVisibility(View.VISIBLE);
        binding.btnApplyPromoCode.setVisibility(View.GONE);
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


                    int totalAmount = AppSettingManager.shared.tmpCartList.stream().mapToInt( p -> p.getQty() * Integer.parseInt( p.voucherModel.getActualPrice()) ).sum();


                    binding.YourSavingLayout.setVisibility(model.getData().getTotalDiscount() > 0 ? View.VISIBLE : View.GONE);

                    Utils.setStyledText(activity,binding.tvYourSaving,String.valueOf(model.getData().getTotalDiscount()));

                    Float finalTotalAmount = totalAmount - (float) model.getData().getTotalDiscount();
                    binding.tvPrice.setVisibility(finalTotalAmount > 0 ? View.VISIBLE : View.GONE);

                    Utils.setStyledText(activity,binding.tvPrice,String.valueOf(finalTotalAmount));

                    updateAdapterDataAfterPromoCode();
                }

            }
        });
    }

    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------


    private class BuyNowAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder( UiUtils.getViewBy( parent, R.layout.item_buy_now ) );
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;

            VoucherModel model = (VoucherModel) getItem( position );

            if (model != null) {
                viewHolder.mBinding.tvTitle.setText( model.getTitle() );
                viewHolder.mBinding.tvDescription.setText( model.getDescription() );

                if (isPromoCodeApply && promoCodeModel != null && getPromoCodeModel(model.getId()) != null && !promoCodeModel.getPromoDiscountType().equals("flat")){
                    VenueMetaDataPromoCodeModel tmpModel = getPromoCodeModel(model.getId());
                    viewHolder.mBinding.tvDiscount.setText( String.format( "%s%%",(int) Math.round(tmpModel.getFinalDiscountInPercent()) ) );
                    double tvPrice = tmpModel.getFinalAmount() / tmpModel.getQty();
                    String formattedPrice = String.format(Locale.ENGLISH, "%.2f", tvPrice);
                    Utils.setStyledText(activity,viewHolder.mBinding.tvDiscountPrice, formattedPrice);
                }else {
                    viewHolder.mBinding.tvDiscount.setText( String.format( "%s%%", model.getDiscountValue() ) );
                    Utils.setStyledText(activity,viewHolder.mBinding.tvDiscountPrice, String.valueOf( model.getDiscountedPrice()));
                }


                if (model.getActualPrice() != null) {
                    if (!model.getActualPrice().equals( "0" )) {
                        Utils.setStyledText(activity,viewHolder.mBinding.tvOriginalPrice,String.valueOf(model.getActualPrice()));
                    } else {
                        viewHolder.mBinding.tvOriginalPrice.setVisibility( View.GONE );
                        int colorTransparent = viewHolder.itemView.getContext().getResources().getColor(R.color.transparent);
                        viewHolder.mBinding.roundLinear.setBackgroundColor(colorTransparent);
                    }
                } else {
                    if (model.getOriginalPrice() != 0) {
                        Utils.setStyledText(activity,viewHolder.mBinding.tvOriginalPrice,String.valueOf(model.getActualPrice()));
                    } else {
                        viewHolder.mBinding.tvOriginalPrice.setVisibility( View.GONE );
                        int colorTransparent = viewHolder.itemView.getContext().getResources().getColor(R.color.transparent);
                        viewHolder.mBinding.roundLinear.setBackgroundColor(colorTransparent);
                    }
                }

                viewHolder.mBinding.tvOriginalPrice.setPaintFlags( viewHolder.mBinding.tvOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG );

                if (!TextUtils.isEmpty(model.getImage())) {
                    Glide.with(activity).load(model.getImage()).into(viewHolder.mBinding.ivCover);
                } else {
                    if (model.getVenue() != null) {
                        venueDetail = model.getVenue();
                        Glide.with(activity).load(model.getVenue().getCover()).into(viewHolder.mBinding.ivCover);
                    } else {
                        model.setVenue(venueDetail);
                        Glide.with(activity).load(model.getImage()).into(viewHolder.mBinding.ivCover);
                    }
                }


                try {
                    Date date = Utils.stringToDate( model.getEndDate(), "yyyy-MM-dd" );
                    if (date != null) {
                        String endDate = Utils.formatDate( date, "E, dd MMM yyyy" );
                        viewHolder.mBinding.startDate.setText( endDate );
                    }
                } catch (Exception e) {
                    throw new RuntimeException( e );
                }


                if (model.getDiscountedPrice() == 0) {
                    viewHolder.mBinding.addQuantityLayout.setVisibility( View.GONE );
                    viewHolder.mBinding.roundLinear.setVisibility( View.GONE );
                } else {
                    viewHolder.mBinding.addQuantityLayout.setVisibility( View.VISIBLE );
                    viewHolder.mBinding.roundLinear.setVisibility( View.VISIBLE );
                }

                viewHolder.mBinding.ivPlus.setOnClickListener( v -> updateQuantity( model, 1, viewHolder ) );
                viewHolder.mBinding.ivMinus.setOnClickListener( v -> updateQuantity( model, -1, viewHolder ) );


                if (promoCodeModel != null) {
                    if (isPromoCodeApply && !promoCodeModel.getPromoDiscountType().equals("flat") && getPromoCodeModel(model.getId()) != null) {
                        viewHolder.mBinding.tvAfterPromoCodeApply.setVisibility(View.VISIBLE);
                        viewHolder.mBinding.tvAfterPromoCodeApply.setText("Promo code applied : " + (int) Math.round(getPromoCodeModel(model.getId()).getPromoDiscountInPercent()) + "% discount added");
                    } else {
                        viewHolder.mBinding.tvAfterPromoCodeApply.setVisibility(View.GONE);
                    }
                } else {
                    viewHolder.mBinding.tvAfterPromoCodeApply.setVisibility(View.GONE);

                }
            }
        }

        private void updateQuantity(VoucherModel model, int delta, ViewHolder viewHolder) {
            Optional<CartModel> obj = AppSettingManager.shared.tmpCartList.stream().filter( p -> p.id.equals( model.getId() ) ).findFirst();
            if (obj.isPresent()) {
                CartModel cartModel = obj.get();
                if (delta > 0) {
                    cartModel.qty += delta;
                } else if (delta < 0 && cartModel.qty > 0) {
                    cartModel.qty = cartModel.qty + delta;
                }
            } else {
                if (delta == 1) {
                    CartModel cartModel = new CartModel( model, dealModel, venueDetail, "", "", 1, model.getDiscountedPrice() );
                    AppSettingManager.shared.tmpCartList.add( cartModel );
                }
            }


            int totalQtyForItem = AppSettingManager.shared.tmpCartList.stream().filter( p -> p.id.equals( model.getId() ) ).mapToInt( p -> p.qty ).sum();
            if (totalQtyForItem >= 0) {
                viewHolder.mBinding.tvTotal.setText( String.valueOf( totalQtyForItem ) );
            }


            int totalAmount = AppSettingManager.shared.tmpCartList.stream().mapToInt( p -> p.getQty() * p.voucherModel.getDiscountedPrice() ).sum();
            int totalAED = AppSettingManager.shared.tmpCartList.stream().mapToInt(p -> {int qty = p.getQty();
                        int actualPrice = Integer.parseInt(p.voucherModel.getActualPrice());
                        return qty * actualPrice;
                    })
                    .sum();

            int totalSaving = totalAED - totalAmount;

            Utils.setStyledText(activity,binding.tvYourSaving,String.valueOf(totalSaving));
            Utils.setStyledText(activity,binding.tvTotalDiscount,String.valueOf(totalSaving));

            binding.YourSavingLayout.setVisibility(totalSaving > 0 ? View.VISIBLE : View.GONE);

            binding.tvPrice.setVisibility( totalAmount > 0 ? View.VISIBLE : View.GONE );

            Utils.setStyledText(activity,binding.tvPrice,String.valueOf(totalAmount));

            int qtySum = AppSettingManager.shared.tmpCartList.stream().mapToInt(p -> p.qty).sum();


            binding.btnApplyPromoCode.setVisibility(qtySum > 0 ? View.VISIBLE : View.GONE);
            binding.YourSavingLayout.setVisibility(qtySum > 0 ? View.VISIBLE : View.GONE);

            if (isPromoCodeApply){
                requestVenuePromoCode();
            }

            if (qtySum == 0){
                resetPromoCodeLayout();
                packageAdapter.notifyDataSetChanged();
                binding.YourSavingLayout.setVisibility(View.GONE);
                binding.layoutPromoAndDiscount.setVisibility(View.GONE);
            }


        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            private final ItemBuyNowBinding mBinding;
            public ViewHolder(@NonNull View itemView) {
                super( itemView );
                mBinding = ItemBuyNowBinding.bind( itemView );
            }
        }
    }

}


