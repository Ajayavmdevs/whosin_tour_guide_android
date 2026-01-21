package com.whosin.app.ui.activites.home.activity;

import androidx.fragment.app.DialogFragment;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;
import com.whosin.app.R;
import com.whosin.app.comman.AppConstants;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.databinding.ActivityOrderConfirmationDialogBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.models.ActivityDetailModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.PaymentCredentialModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.raynaTicket.BottomSheets.SelectPaymentOptionBottomSheet;
import com.whosin.app.ui.activites.raynaTicket.BottomSheets.TabbyPaymentBottomSheet;
import com.whosin.app.ui.fragment.wallet.PurchaseSuccessFragment;

import java.util.Objects;

public class OrderConfirmationDialog extends DialogFragment {

    private ActivityOrderConfirmationDialogBinding binding;
    private ActivityDetailModel activityDetailModel;
    public CommanCallback<Boolean> callback;
    private AlertDialog dialog = null;
    private PaymentSheet paymentSheet;
    private int totalSeat , discountPrice;
    private String date = "";
    private String time = "";


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------


    public OrderConfirmationDialog(ActivityDetailModel model, int totalSeat, String date, String time) {
        this.activityDetailModel = model;
        this.totalSeat = totalSeat;
        this.date = date;
        this.time = time;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutRes(), container, false);
        initUi(view);
        setListeners();
        return view;
    }


    public void initUi(View view) {
        binding = ActivityOrderConfirmationDialogBinding.bind(view);

        binding.tvTitle.setText(Utils.getLangValue("order_confirmation"));
        binding.tvDateAndTimeTitle.setText(Utils.getLangValue("date_and_time"));
        binding.tvTotalPriceTitle.setText(Utils.getLangValue("total_price"));
        binding.btnBuyNow.setText(Utils.getLangValue("confirm"));
        binding.btnEdit.setText(Utils.getLangValue("edit"));

        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Glide.with(requireActivity()).load(R.drawable.icon_close).into(binding.ivClose);

        binding.tvSubTitle.setText(activityDetailModel.getProvider().getName());
        binding.tvName.setText(activityDetailModel.getName());
        binding.tvTotalSheet.setText(String.valueOf(totalSeat));
        discountPrice = Integer.parseInt(String.valueOf(activityDetailModel.getPrice() - activityDetailModel.getPrice() * Integer.parseInt(activityDetailModel.getDiscount().split("%")[0]) / 100));
        binding.tvPrice.setText(String.valueOf(totalSeat * discountPrice));
        binding.tvDate.setText(date);
        binding.tvTime.setText(time);


        paymentSheet = new PaymentSheet(OrderConfirmationDialog.this, paymentSheetResult -> {
            if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
                Log.d("TAG", "Canceled");
            } else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
                Log.e("TAG", "Got error: ", ((PaymentSheetResult.Failed) paymentSheetResult).getError());
            } else if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
                PurchaseSuccessFragment purchaseSuccessFragment = new PurchaseSuccessFragment();
                purchaseSuccessFragment.callBack = data -> {
                    if (data){
                        dismiss();
                        callback.onReceive(true);
                    }
                };
                purchaseSuccessFragment.show(getChildFragmentManager(), "");
            }
        });

    }

    public void setListeners() {
        binding.ivClose.setOnClickListener(v -> getDialog().onBackPressed());
        binding.btnEdit.setOnClickListener(v -> getDialog().onBackPressed());

        binding.btnBuyNow.setOnClickListener(view -> {
            if (activityDetailModel == null) {
                return;
            }

            JsonArray metaData = new JsonArray();
            JsonObject item = new JsonObject();
            int price = discountPrice * totalSeat;
            item.addProperty("activityId", activityDetailModel.getId());
            item.addProperty("activityType", activityDetailModel.getActivityTime().getType());
            item.addProperty("date", date);
            item.addProperty("time", time);
            item.addProperty("reservedSeat", totalSeat);
            item.addProperty("price", price);
            item.addProperty("type", "activity");


            metaData.add(item);
            JsonObject params = new JsonObject();
            params.addProperty("amount", totalSeat * discountPrice);
            params.addProperty("currency", "aed");
            params.add("metadata", metaData);



            SelectPaymentOptionBottomSheet bottmSheet = new SelectPaymentOptionBottomSheet();
            bottmSheet.amount = (double) totalSeat * discountPrice;
            bottmSheet.callback = data -> requestStripeToken(params,data);
            bottmSheet.show(getChildFragmentManager(), "");

        });

    }


    public int getLayoutRes() {
        return R.layout.activity_order_confirmation_dialog;
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------


    private void startStripeCheckOut(PaymentCredentialModel model) {
        if (model.publishableKey == null || model.clientSecret == null) {
            Toast.makeText(requireActivity(), "Invalid payment configuration", Toast.LENGTH_SHORT).show();
            return;
        }
        PaymentConfiguration.init(requireActivity(), model.publishableKey);
        final PaymentSheet.Configuration configuration = new PaymentSheet.Configuration.Builder("Whosin, Inc.").allowsDelayedPaymentMethods(true).build();
        paymentSheet.presentWithPaymentIntent(model.clientSecret, configuration);
    }

    private void startGooglePayCheckOut(PaymentCredentialModel model) {
        if (model.publishableKey == null || model.clientSecret == null) {
            Toast.makeText(requireActivity(), "Invalid payment configuration", Toast.LENGTH_SHORT).show();
            return;
        }
        PaymentConfiguration.init(requireActivity(), model.publishableKey);
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
                            if (data) {
                                dismiss();
                                callback.onReceive(true);
                            }
                        };
                        purchaseSuccessFragment.show(getChildFragmentManager(), "");

                        break;
                    case "cancel":

                        break;
                    case "failure":

                        break;
                }
            }
        };
        sheet.show(getChildFragmentManager(), "");
    }


    private void showProgress() {
        if (getActivity() == null || getActivity().isFinishing()) {
            return;
        }
        if (dialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setCancelable(false);
            builder.setView(R.layout.layout_loading_dialog);
            dialog = builder.create();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }
        dialog.show();
    }


    private void hideProgress() {
        if (dialog != null && getActivity() != null && !getActivity().isFinishing()) {
            dialog.dismiss();
        }
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
        } else if (AppConstants.TABBY_PAYMENT == paymentMod) {
            jsonObject.addProperty("paymentMethod", "tabby");
        }else if (AppConstants.GOOGLE_PAY == paymentMod || AppConstants.SAMSUNG_PAY == paymentMod) {
            jsonObject.addProperty("paymentMethod", "stripe");
        }

        showProgress();
        DataService.shared(requireActivity()).requestStripePaymentIntent(jsonObject, new RestCallback<ContainerModel<PaymentCredentialModel>>(this) {
            @Override
            public void result(ContainerModel<PaymentCredentialModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                    }
                    return;
                }
                if (Objects.equals(model.message, "Vip User Order Successfully Created!")) {
                    PurchaseSuccessFragment purchaseSuccessFragment = new PurchaseSuccessFragment();
                    purchaseSuccessFragment.callBack = data -> {
                        if (data){
                            dismiss();
                            callback.onReceive(true);
                        }
                    };
                    purchaseSuccessFragment.show(getChildFragmentManager(), "");
                }
                else if (model.getData() != null) {
                    if (AppConstants.CARD_PAYMENT == paymentMod) {
                        startStripeCheckOut(model.getData());
                    } else if (AppConstants.PAY_WITH_LINK == paymentMod) {
                        startStripeCheckOut(model.getData());
                    }  else if (AppConstants.GOOGLE_PAY == paymentMod || AppConstants.SAMSUNG_PAY == paymentMod) {
                        startGooglePayCheckOut(model.getData());
                    } else if (AppConstants.TABBY_PAYMENT == paymentMod) {
                        if (Utils.isAvailableTabby(model.getData().getTabbyModel())) {
                            tabbyCheckOut(model.getData());
                        } else {
                            Graphics.showAlertDialogWithOkButton(requireActivity(), getString(R.string.app_name), Utils.getLangValue("tabby_payment_failed"));
                        }
                    }
                }

            }
        });
    }

    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------


}