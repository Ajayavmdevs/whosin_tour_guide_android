package com.whosin.app.ui.activites.JpHotel;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
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
import com.whosin.app.databinding.ActivityJpHotelCheckOutBinding;
import com.whosin.app.databinding.ItemCheckoutJpSubHotelRoomBinding;
import com.whosin.app.databinding.JpHotelGuestPrviewViewBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.AppSettingManager;
import com.whosin.app.service.manager.JPTicketManager;
import com.whosin.app.service.manager.LogManager;
import com.whosin.app.service.manager.RaynaTicketManager;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.JuniperHotelModels.JPHotelPolicyRuleModel;
import com.whosin.app.service.models.JuniperHotelModels.JPPassengerModel;
import com.whosin.app.service.models.JuniperHotelModels.JpHotelPriceModel;
import com.whosin.app.service.models.JuniperHotelModels.JpHotelRoomModel;
import com.whosin.app.service.models.PaymentCredentialModel;
import com.whosin.app.service.models.RatingModel;
import com.whosin.app.service.models.VenuePromoCodeModel;
import com.whosin.app.service.models.rayna.RaynaTicketDetailModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.auth.AuthenticationActivity;
import com.whosin.app.ui.activites.comman.BaseActivity;
import com.whosin.app.ui.activites.home.activity.SeeAllRatingReviewActivity;
import com.whosin.app.ui.activites.raynaTicket.BottomSheets.ReadMoreBottomSheet;
import com.whosin.app.ui.activites.raynaTicket.BottomSheets.SelectPaymentOptionBottomSheet;
import com.whosin.app.ui.activites.raynaTicket.BottomSheets.TabbyPaymentBottomSheet;
import com.whosin.app.ui.activites.raynaTicket.RaynaGalleryViewActivity;
import com.whosin.app.ui.activites.wallet.WalletActivity;
import com.whosin.app.ui.adapter.JPHotelCancellationPolicyAdapter;
import com.whosin.app.ui.adapter.JPHotelGuestDetailAdapter;
import com.whosin.app.ui.adapter.raynaTicketAdapter.RaynaGalleryAdapter;
import com.whosin.app.ui.fragment.wallet.PurchaseSuccessFragment;

import java.util.HashMap;
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


public class JpHotelCheckOutActivity extends BaseActivity {

    private ActivityJpHotelCheckOutBinding binding;

    private PaymentSheet paymentSheet;

    private RaynaGalleryAdapter<RatingModel> galleryAdapter;

    private final RaynaTicketDetailModel raynaTicketDetailModel = RaynaTicketManager.shared.raynaTicketDetailModel;

    private final JPTicketManager jpTicketManager = JPTicketManager.shared;

    private final JPHotelGuestDetailAdapter<JPPassengerModel> guestDetailAdapter = new JPHotelGuestDetailAdapter<>();

    private final JPHotelCancellationPolicyAdapter<JPHotelPolicyRuleModel> jpHotelCancellationPolicyAdapter = new JPHotelCancellationPolicyAdapter<>();

    private final HotelRoomListAdapter<JpHotelRoomModel> hotelRoomListAdapter = new HotelRoomListAdapter<>();

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

        binding.constraintHeader.tvTitle.setText(getValue("checkout"));
        applyTranslations();
        paymentsLauncher = new PaymentsLauncher(this, paymentsResult -> {
            Double price = 0.0;
            try {
                if (raynaTicketDetailModel != null && raynaTicketDetailModel.getStartingAmount() != null) {
                    price = Double.parseDouble(String.valueOf(raynaTicketDetailModel.getStartingAmount()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if(paymentsResult instanceof PaymentsResult.Authorised || paymentsResult instanceof PaymentsResult.Success) {
                if (raynaTicketDetailModel != null) {
                    LogManager.shared.logTicketEvent(LogManager.LogEventType.purchase, raynaTicketDetailModel.getId(), raynaTicketDetailModel.getTitle(), price, null, "AED");
                }
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
            }
            else if (paymentsResult instanceof PaymentsResult.Failed){
                if (raynaTicketDetailModel != null) {
                    LogManager.shared.logTicketEvent(LogManager.LogEventType.paymentFailed, raynaTicketDetailModel.getId(), raynaTicketDetailModel.getTitle(), price, null, "AED");
                }
                String error = ((PaymentsResult.Failed) paymentsResult).getError();
                Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
            } else {
                if (raynaTicketDetailModel != null) {
                    LogManager.shared.logTicketEvent(LogManager.LogEventType.paymentFailed, raynaTicketDetailModel.getId(), raynaTicketDetailModel.getTitle(), price, null, "AED");
                }
                Toast.makeText(activity, "Payment failed, Please try again.", Toast.LENGTH_SHORT).show();
            }
        });

        paymentSheet = new PaymentSheet(JpHotelCheckOutActivity.this, paymentSheetResult -> {
            Double price = 0.0;
            try {
                if (raynaTicketDetailModel != null && raynaTicketDetailModel.getStartingAmount() != null) {
                    price = Double.parseDouble(String.valueOf(raynaTicketDetailModel.getStartingAmount()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
                if (raynaTicketDetailModel != null) {
                    LogManager.shared.logTicketEvent(LogManager.LogEventType.paymentCancelled, raynaTicketDetailModel.getId(), raynaTicketDetailModel.getTitle(), price, null, "AED");
                }
                Log.d("TAG", "Canceled");
            } else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
                if (raynaTicketDetailModel != null) {
                    LogManager.shared.logTicketEvent(LogManager.LogEventType.paymentFailed, raynaTicketDetailModel.getId(), raynaTicketDetailModel.getTitle(), price, null, "AED");
                }
                Log.e("TAG", "Got error: ", ((PaymentSheetResult.Failed) paymentSheetResult).getError());
            } else if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
                if (raynaTicketDetailModel != null) {
                    LogManager.shared.logTicketEvent(LogManager.LogEventType.purchase, raynaTicketDetailModel.getId(), raynaTicketDetailModel.getTitle(), price, null, "AED");
                }
                PurchaseSuccessFragment purchaseSuccessFragment = new PurchaseSuccessFragment();
                purchaseSuccessFragment.callBackForRaynaBooking = data -> {
                    if (data) {
                        startActivity(new Intent(Graphics.context, WalletActivity.class));
                        jpTicketManager.finishAllActivities();
                        jpTicketManager.clearManager();
                        finish();

                    }
                };
                purchaseSuccessFragment.show(getSupportFragmentManager(), "");

            }
        });

        galleryAdapter = new RaynaGalleryAdapter<>(activity, data -> startActivity(new Intent(activity, RaynaGalleryViewActivity.class).putExtra("model", new Gson().toJson(raynaTicketDetailModel)).putExtra("scrollToPosition", data)));
        binding.raynaGalleryRecyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        binding.raynaGalleryRecyclerView.setAdapter(galleryAdapter);
        PagerSnapHelper snapHelper = new PagerSnapHelper ();
        snapHelper.attachToRecyclerView(binding.raynaGalleryRecyclerView);

        setDetail();

        binding.guestDetailView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        binding.guestDetailView.setAdapter(guestDetailAdapter);
        if (!jpTicketManager.guestList.isEmpty()) guestDetailAdapter.updateData(jpTicketManager.guestList);

        binding.jpHotelDetailview.cancellationView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        binding.jpHotelDetailview.cancellationView.setAdapter(jpHotelCancellationPolicyAdapter);

        binding.jpHotelDetailview.hotelRoomRecycleView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        binding.jpHotelDetailview.hotelRoomRecycleView.setAdapter(hotelRoomListAdapter);

        setHotelDetails();
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
                    if (raynaTicketDetailModel.getStartingAmount() != null) {
                        price = Double.parseDouble(String.valueOf(raynaTicketDetailModel.getStartingAmount()));
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


        binding.totalReviewsTv.setOnClickListener( view -> startActivity( new Intent( activity, SeeAllRatingReviewActivity.class )
                .putExtra( "id", raynaTicketDetailModel.getId() )
                .putExtra( "type", "ticket" )
                .putExtra( "isEnableReview", raynaTicketDetailModel.isEnableReview())
                .putExtra( "currentUserRating", new Gson().toJson( raynaTicketDetailModel.getCurrentUserRatingModel() ) ) ) );

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

        binding.raynaGalleryRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager == null || galleryAdapter == null) return;

                int totalItems = galleryAdapter.getItemCount();
                if (totalItems == 0) return;

                int centerPosition = getCenterItemPosition(recyclerView);
                int dotSelection = 0;

                if (totalItems >= 3) {
                    if (centerPosition == 0) {
                        dotSelection = 0;
                    } else if (centerPosition == totalItems - 1) {
                        dotSelection = 2;
                    } else {
                        dotSelection = 1;
                    }
                } else if (totalItems == 2) {
                    dotSelection = (centerPosition == 0) ? 0 : 1;
                } else if (totalItems == 1) {
                    dotSelection = 0;
                }

                binding.dotsIndicator.setDotSelection(dotSelection);

            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager == null) return;

                int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
                int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();

                // Manage video playback for visible items
                for (int i = firstVisiblePosition; i <= lastVisiblePosition; i++) {
                    RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(i);
                    if (viewHolder instanceof RaynaGalleryAdapter.RaynaVideoHolder) {
                        View itemView = viewHolder.itemView;
                        if (UiUtils.isView90PercentVisibleHorizontally(recyclerView, itemView)) {
                            ((RaynaGalleryAdapter.RaynaVideoHolder) viewHolder).startVideo();
                        } else {
                            ((RaynaGalleryAdapter.RaynaVideoHolder) viewHolder).pauseVideo();
                        }
                    }
                }
            }

            private int getCenterItemPosition(RecyclerView recyclerView) {
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager == null) return RecyclerView.NO_POSITION;

                int minDistance = Integer.MAX_VALUE;
                int center = recyclerView.getWidth() / 2;
                int centerItemPosition = RecyclerView.NO_POSITION;

                for (int i = layoutManager.findFirstVisibleItemPosition(); i <= layoutManager.findLastVisibleItemPosition(); i++) {
                    View itemView = layoutManager.findViewByPosition(i);
                    if (itemView == null) continue;

                    int itemCenter = (itemView.getLeft() + itemView.getRight()) / 2;
                    int distance = Math.abs(itemCenter - center);

                    if (distance < minDistance) {
                        minDistance = distance;
                        centerItemPosition = i;
                    }
                }

                return centerItemPosition;
            }
        });

        binding.mainConstraint.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            controlVideoPlayback();
        });

    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityJpHotelCheckOutBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected Map<View, String> getTranslationMap() {
        Map<View, String> map = new HashMap<>();
        map.put(binding.tvStartingFrom, "starting_from");
        map.put(binding.tvGuestTitle, "guest_details");
        map.put(binding.btnBackBtn, "back");
        map.put(binding.tvNextStep, "checkOut");
        map.put(binding.edtPromoCode, "enter_promo_code");


        map.put(binding.jpHotelDetailview.discountTv, "service_fees");
        map.put(binding.jpHotelDetailview.finalAmountTv, "final_amount");
        map.put(binding.jpHotelDetailview.totalAmoutTv, "base_price");

        map.put(binding.jpHotelDetailview.tvCancellationTitle, "cancellation_time");
        map.put(binding.jpHotelDetailview.tvRefundTitle, "refund");

        map.put(binding.tvTotalSavingTitle, "saved_with");
        map.put(binding.tvPromoCodeTitleForPromoLayout, "promo_code");
        map.put(binding.tvDiscountTitleForPromoLayout, "discount");

        map.put(binding.btnApplyPromoCode, "apply");
        return map;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (galleryAdapter != null) {
            galleryAdapter.resumeAllVideos();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (galleryAdapter != null) {
            galleryAdapter.pauseAllVideos();
        }
    }

    @Override
    protected void onDestroy() { super.onDestroy();
        if (galleryAdapter != null) {
            galleryAdapter.releaseAllPlayers();
        }
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    @SuppressLint("SetTextI18n")
    private void setDetail() {

        if (raynaTicketDetailModel == null) return;

        if (!raynaTicketDetailModel.getImages().isEmpty()){
            if (raynaTicketDetailModel.getImages().size() == 2) binding.dotsIndicator.initDots(2);
            if (raynaTicketDetailModel.getImages().size() >= 3) binding.dotsIndicator.initDots(3);
            if (raynaTicketDetailModel.getImages().size() >= 2) {
                binding.dotsIndicator.setVisibility(VISIBLE);
            } else {
                binding.dotsIndicator.setVisibility(GONE);
            }
            galleryAdapter.updateData(raynaTicketDetailModel.getImages().stream().map(RatingModel::new).collect(Collectors.toList()));
        }else {
            binding.dotsIndicator.setVisibility(GONE);
        }


        float rating = (float) Math.ceil(raynaTicketDetailModel.getAvg_ratings() * 10) / 10;
        binding.rating.setStepSize(0.1f);
        binding.rating.setRating(rating);
        binding.rating.setIsIndicator(true);
        binding.rating.setOnRatingChangeListener(null);
        binding.avgRatingsTv.setText(String.format(Locale.ENGLISH, "%.1f", rating));

        binding.totalReviewsTv.setText(setValue("review_count",String.valueOf(raynaTicketDetailModel.getReviews().size())));

        binding.rating.setVisibility(raynaTicketDetailModel.isEnableRating() ? View.VISIBLE : View.GONE);
        binding.avgRatingsTv.setVisibility(raynaTicketDetailModel.isEnableRating() ? View.VISIBLE : View.GONE);
        binding.totalReviewsTv.setVisibility(raynaTicketDetailModel.isReviewVisible() ? View.VISIBLE : View.GONE);

        if (raynaTicketDetailModel.getReviews().isEmpty()) binding.totalReviewsTv.setVisibility(GONE);

        if (raynaTicketDetailModel.getAvg_ratings() == 0.0){
            binding.ratingLayout.setVisibility(GONE);
        }

        Object amount = raynaTicketDetailModel.getStartingAmount();
        Float floatAmount = null;

        if (amount instanceof Float) {
            floatAmount = (Float) amount;
        } else if (amount instanceof Double) {
            floatAmount = ((Double) amount).floatValue();
        } else if (amount instanceof String) {
            try {
                floatAmount = Float.parseFloat((String) amount);
            } catch (NumberFormatException ignored) {
            }
        }

        Utils.setStyledText(activity,binding.tvAmoutStart,Utils.roundFloatValue(floatAmount));


        String discount = String.valueOf(raynaTicketDetailModel.getDiscount());
        if (!"0".equals(discount)) {
            binding.tvDiscount.setText(discount.contains("%") ? discount : discount + "%");
            binding.tvDiscount.setVisibility(View.VISIBLE);
        } else {
            binding.tvDiscount.setVisibility(View.GONE);
        }

        binding.tvTickeTitle.setText(raynaTicketDetailModel.getTitle());

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
            finalAmount.set(Float.valueOf(jpTicketManager.priceModel.getNett()));
            checkTabbyAmount = finalAmount.get();
        }
        bottmSheet.amount = checkTabbyAmount;
        bottmSheet.callback = this::requestRaynaTourBooking;
        bottmSheet.show(getSupportFragmentManager(), "");
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
            Double price = 0.0;
            try {
                if (raynaTicketDetailModel != null && raynaTicketDetailModel.getStartingAmount() != null) {
                    price = Double.parseDouble(String.valueOf(raynaTicketDetailModel.getStartingAmount()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (!TextUtils.isEmpty(p)) {
                if (p.equals("success")) {
                    if (raynaTicketDetailModel != null) {
                        LogManager.shared.logTicketEvent(LogManager.LogEventType.purchase, raynaTicketDetailModel.getId(), raynaTicketDetailModel.getTitle(), price, null, "AED");
                    }
                    PurchaseSuccessFragment purchaseSuccessFragment = new PurchaseSuccessFragment();
                    purchaseSuccessFragment.callBackForRaynaBooking = q -> {
                        if (q) {
                            startActivity(new Intent(Graphics.context, WalletActivity.class));
                            RaynaTicketManager.shared.finishAllActivities();
                            RaynaTicketManager.shared.clearManager();
                            finish();

                        }
                    };
                    purchaseSuccessFragment.show(getSupportFragmentManager(), "");

                } else if (p.equals("cancel")) {
                    if (raynaTicketDetailModel != null) {
                        LogManager.shared.logTicketEvent(LogManager.LogEventType.paymentCancelled, raynaTicketDetailModel.getId(), raynaTicketDetailModel.getTitle(), price, null, "AED");
                    }

                } else if (p.equals("failure")) {
                    if (raynaTicketDetailModel != null) {
                        LogManager.shared.logTicketEvent(LogManager.LogEventType.paymentFailed, raynaTicketDetailModel.getId(), raynaTicketDetailModel.getTitle(), price, null, "AED");
                    }

                }
            }
        };
        sheet.show(getSupportFragmentManager(), "");
    }

    private void controlVideoPlayback() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) binding.raynaGalleryRecyclerView.getLayoutManager();
        if (layoutManager == null) return;

        int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
        int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();

        for (int i = firstVisiblePosition; i <= lastVisiblePosition; i++) {
            RecyclerView.ViewHolder viewHolder = binding.raynaGalleryRecyclerView.findViewHolderForAdapterPosition(i);
            if (viewHolder instanceof RaynaGalleryAdapter.RaynaVideoHolder) {
                View itemView = viewHolder.itemView;
                if (UiUtils.is90PercentVisible(binding.raynaGalleryRecyclerView, itemView)) {
                    ((RaynaGalleryAdapter.RaynaVideoHolder) viewHolder).startVideo();
                } else {
                    ((RaynaGalleryAdapter.RaynaVideoHolder) viewHolder).pauseVideo();
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void updatePrices(){
        binding.jpHotelDetailview.promoCodeLinear.setVisibility(GONE);
        if (jpTicketManager.priceModel != null) {
            binding.jpHotelDetailview.roundPriceLayout.setVisibility(View.VISIBLE);
            JpHotelPriceModel priceModel = jpTicketManager.priceModel;

            setPrice(binding.jpHotelDetailview.totalAmoutLayout,binding.jpHotelDetailview.basePriceAed, priceModel.getAmount());
            setPrice(binding.jpHotelDetailview.discountLinear,binding.jpHotelDetailview.serviceFeesAed, priceModel.getServiceTaxes() != null ? priceModel.getServiceTaxes().getAmount() : null);
            setPrice(binding.jpHotelDetailview.finalAmountLinear,binding.jpHotelDetailview.finalAmountAed, priceModel.getNett());

        } else {
            binding.jpHotelDetailview.roundPriceLayout.setVisibility(View.GONE);
        }

    }

    private void setHotelDetails(){

        if (jpTicketManager.jpHotelBookingRuleModel == null) return;

        // Start and End Date
        if (jpTicketManager.jpHotelBookingRuleModel.getHotelBookingRequiredFields() != null) {
            binding.jpHotelDetailview.startAndEndDateLayout.setVisibility(VISIBLE);
            binding.jpHotelDetailview.viewLine1.setVisibility(VISIBLE);
            binding.jpHotelDetailview.tvStartDate.setText(setValue("start_date_jp",jpTicketManager.jpHotelBookingRuleModel.getHotelBookingRequiredFields().getStartDate()));
            binding.jpHotelDetailview.tvEndDate.setText(setValue("end_date_jp",jpTicketManager.jpHotelBookingRuleModel.getHotelBookingRequiredFields().getEndDate()));
        } else {
            binding.jpHotelDetailview.startAndEndDateLayout.setVisibility(GONE);
            binding.jpHotelDetailview.viewLine1.setVisibility(GONE);
        }


        // Check In - Out
        if (jpTicketManager.checkTime != null) {

            String checkIn  = jpTicketManager.checkTime.getCheckIn();
            String checkOut = jpTicketManager.checkTime.getCheckOut();

            boolean hasCheckIn  = checkIn != null && !checkIn.trim().isEmpty();
            boolean hasCheckOut = checkOut != null && !checkOut.trim().isEmpty();

            if (hasCheckIn || hasCheckOut) {
                binding.jpHotelDetailview.checkInOutLayout.setVisibility(View.VISIBLE);
                binding.jpHotelDetailview.viewLine1.setVisibility(View.VISIBLE);

                // Check-in
                if (hasCheckIn) {
                    binding.jpHotelDetailview.tvCheckIn.setVisibility(View.VISIBLE);
                    binding.jpHotelDetailview.tvCheckIn.setText(setValue("check_in", checkIn));
                } else {
                    binding.jpHotelDetailview.tvCheckIn.setVisibility(View.GONE);
                }

                // Check-out
                if (hasCheckOut) {
                    binding.jpHotelDetailview.tvCheckOut.setVisibility(View.VISIBLE);
                    binding.jpHotelDetailview.tvCheckOut.setText(setValue("check_out", checkOut));
                } else {
                    binding.jpHotelDetailview.tvCheckOut.setVisibility(View.GONE);
                }

            } else {
                binding.jpHotelDetailview.checkInOutLayout.setVisibility(View.GONE);
                binding.jpHotelDetailview.viewLine1.setVisibility(View.GONE);
            }
        } else {
            binding.jpHotelDetailview.checkInOutLayout.setVisibility(View.GONE);
            binding.jpHotelDetailview.viewLine1.setVisibility(View.GONE);
        }


        // Selected room information
        if (jpTicketManager.jpHotelBookingRuleModel.getPriceInformation() != null && !jpTicketManager.jpHotelBookingRuleModel.getPriceInformation().isEmpty()) {

            String fullName = jpTicketManager.jpHotelBookingRuleModel.getPriceInformation().get(0).getBoard().getFullBoardName();
            binding.jpHotelDetailview.tvHotelBoardName.setText(fullName);
            hotelRoomListAdapter.updateData(jpTicketManager.jpHotelBookingRuleModel.getPriceInformation().get(0).hotelRooms);
        }

        // Cancellation Time and policy
        boolean nonRefundable = jpTicketManager.nonRefundable.equalsIgnoreCase("true");
        if (!nonRefundable && jpTicketManager.jpHotelBookingRuleModel.getCancellationPolicy() != null) {
            jpHotelCancellationPolicyAdapter.updateData(jpTicketManager.jpHotelBookingRuleModel.getCancellationPolicy().getPolicyRules());
            binding.jpHotelDetailview.cardConstraint.setVisibility(VISIBLE);
        } else {
            binding.jpHotelDetailview.cardConstraint.setVisibility(GONE);
        }

        updatePrices();

    }

    private void setPrice(LinearLayout layout,TextView tv, String value) {
        if (!TextUtils.isEmpty(value)) {
            tv.setVisibility(View.VISIBLE);
            layout.setVisibility(View.VISIBLE);
            tv.setText(Utils.getStyledText(activity, value));
        } else {
            tv.setVisibility(View.GONE);
            layout.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            onNGenuesCardPaymentResponse(CardPaymentData.getFromIntent(data));
        } catch (Exception ex) {
        }
    }

    // endregion
    // --------------------------------------
    // region Private : Promo Code Methods
    // --------------------------------------

    private JsonObject getPromoJsonObject() {
        JsonObject object = new JsonObject();

        object.addProperty("promoCode", binding.edtPromoCode.getText().toString().trim());
        JsonArray metaDate = new JsonArray();
        JsonObject itemMetaData = new JsonObject();
        itemMetaData.addProperty("type", "juniper-hotel");
        itemMetaData.addProperty("ticketId", raynaTicketDetailModel.getId());

        if (jpTicketManager.priceModel != null) {
            itemMetaData.addProperty("amount", jpTicketManager.priceModel.getNett());
        } else {
            itemMetaData.addProperty("amount", 0);
        }
        itemMetaData.addProperty("discount", raynaTicketDetailModel.getDiscount());
        itemMetaData.addProperty("qty", 1);

        metaDate.add(itemMetaData);

        object.add("metadata", metaDate);
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
        binding.promoCodeDiscountLayout.setVisibility(View.GONE);
        binding.ivDropDown.setRotation(90f);

        binding.YourSavingLayout.setVisibility(View.GONE);
        updatePrices();
    }

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestRaynaTourBooking(int paymentMod) {
        JsonObject object  = jpTicketManager.getBookingObject(isPromoCodeApply,promoCodeModel);
        object.addProperty("promoCode",binding.edtPromoCode.getText().toString());
        switch (paymentMod) {
            case AppConstants.TABBY_PAYMENT:
                object.addProperty("paymentMethod", "tabby");
                break;
//            case AppConstants.NGINUES_PAY:
//                object.addProperty("paymentMethod", "ngenius");
//                break;
            case AppConstants.CARD_PAYMENT:
            case AppConstants.PAY_WITH_LINK:
            case AppConstants.GOOGLE_PAY:
            case AppConstants.SAMSUNG_PAY:
                if (AppSettingManager.shared.getAppSettingData().isAllowStripePayments()) {
                    object.addProperty("paymentMethod", "stripe");
                } else {
                    object.addProperty("paymentMethod", "ngenius");
                }
                break;
        }

        Log.d("object", "requestRaynaTourBooking: " + object);

        showProgress();
        DataService.shared(activity).requestRaynaTourBooking(object, new RestCallback<>(this) {
            @Override
            public void result(ContainerModel<PaymentCredentialModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    if (error.contains("Session expired, please login again!")) {
                        Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), "Session expired, please login again!", aBoolean -> {
                            if (aBoolean) {
                                showProgress();
                                SessionManager.shared.logout(activity, (success, log_out_error) -> {
                                    hideProgress();
                                    if (!Utils.isNullOrEmpty(log_out_error)) {
                                        Toast.makeText(activity, log_out_error, Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    startActivity(new Intent(activity, AuthenticationActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                    finish();
                                });
                            }
                        });
                    } else {
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
//                    else if (AppConstants.NGINUES_PAY == paymentMod) {
//                        startNgeniusPayment(model.getData());
//                    }

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
        Double price = 0.0;
        try {
            if (raynaTicketDetailModel != null && raynaTicketDetailModel.getStartingAmount() != null) {
                price = Double.parseDouble(String.valueOf(raynaTicketDetailModel.getStartingAmount()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        switch (data.getCode()) {
            case CardPaymentData.STATUS_PAYMENT_AUTHORIZED, CardPaymentData.STATUS_PAYMENT_CAPTURED:
                if (raynaTicketDetailModel != null) {
                    LogManager.shared.logTicketEvent(LogManager.LogEventType.purchase, raynaTicketDetailModel.getId(), raynaTicketDetailModel.getTitle(), price, null, "AED");
                }
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
                if (raynaTicketDetailModel != null) {
                    LogManager.shared.logTicketEvent(LogManager.LogEventType.paymentFailed, raynaTicketDetailModel.getId(), raynaTicketDetailModel.getTitle(), price, null, "AED");
                }
                Toast.makeText(activity, "Payment failed, Please try again.", Toast.LENGTH_SHORT).show();
                break;
            case CardPaymentData.STATUS_GENERIC_ERROR:
                if (raynaTicketDetailModel != null) {
                    LogManager.shared.logTicketEvent(LogManager.LogEventType.paymentFailed, raynaTicketDetailModel.getId(), raynaTicketDetailModel.getTitle(), price, null, "AED");
                }
                Toast.makeText(activity, "Something went wrong while payment process, Please try again.", Toast.LENGTH_SHORT).show();
                break;
            default:
                throw new IllegalArgumentException("Unknown payment response (" + data.getReason() + ")");
        }
    }


    private void requestVenuePromoCode() {
        if (getPromoJsonObject().isEmpty() || getPromoJsonObject().isJsonNull()) return;
        binding.promoCodeProgressView.setVisibility(View.VISIBLE);
        binding.btnApplyPromoCode.setVisibility(View.GONE);
        DataService.shared(activity).requestVenuePromoCode(getPromoJsonObject(), new RestCallback<>(this) {
            @SuppressLint("SetTextI18n")
            @Override
            public void result(ContainerModel<VenuePromoCodeModel> model, String error) {
                binding.promoCodeProgressView.setVisibility(View.GONE);
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    promoCodeModel = null;
                    updatePromoCodeUI(false, error);
                    return;
                }


                if (model.getData() != null) {
                    isPromoCodeApply = true;
                    promoCodeModel = model.getData();

                    updatePromoCodeUI(true, "");

                    binding.jpHotelDetailview.promoCodeLinear.setVisibility(View.VISIBLE);


                    binding.YourSavingLayout.setVisibility(model.getData().getTotalDiscount() > 0 ? View.VISIBLE : View.GONE);
                    Utils.setStyledText(activity, binding.jpHotelDetailview.finalAmountAed, String.valueOf(model.getData().getMetaData().get(0).getFinalAmount()));
                    Utils.setStyledText(activity, binding.jpHotelDetailview.promoCodeAed, String.valueOf(model.getData().getPromoDiscount()));

                }

            }
        });
    }

    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------


    private class HotelRoomListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_checkout_jp_sub_hotel_room));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            ViewHolder viewHolder = (ViewHolder) holder;

            JpHotelRoomModel model = (JpHotelRoomModel) getItem(position);

            if (model == null) return;

            if (!TextUtils.isEmpty(model.getFullName())) {
                viewHolder.binding.tvHotelName.setVisibility(View.VISIBLE);
                viewHolder.binding.tvHotelName.setText(model.getFullName());
            } else {
                viewHolder.binding.tvHotelName.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(model.getPaxCount())) {
                viewHolder.binding.tvHotelAdultChildCount.setVisibility(View.VISIBLE);
                viewHolder.binding.tvHotelAdultChildCount.setText(model.getPaxCount());
            } else {
                viewHolder.binding.tvHotelAdultChildCount.setVisibility(View.GONE);
            }

            int lastPos = getItemCount() - 1;
            viewHolder.binding.viewLine1.setVisibility(position == lastPos ? View.GONE : View.VISIBLE);

        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final ItemCheckoutJpSubHotelRoomBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemCheckoutJpSubHotelRoomBinding.bind(itemView);
            }
        }

    }

    // endregion
    // --------------------------------------
}