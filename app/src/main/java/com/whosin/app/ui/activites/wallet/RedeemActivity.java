package com.whosin.app.ui.activites.wallet;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.whosin.app.R;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.databinding.ActivityRedeemBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.models.CommanMsgModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.ItemModel;
import com.whosin.app.service.models.MyWalletModel;
import com.whosin.app.service.models.PackageModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.comman.BaseActivity;

public class RedeemActivity extends BaseActivity {

    private ActivityRedeemBinding binding;
    private MyWalletModel myWalletModel;
    private PackageModel packageModel;
    private int val = 0;
    private String code = "";
    public CommanCallback<Boolean> callBack;

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    protected void initUi() {
        Graphics.applyBlurEffectOnClaimScreen(activity, binding.blurView);
        String model = getIntent().getStringExtra("itemList");
        String pModel = getIntent().getStringExtra("packageModel");
        myWalletModel = new Gson().fromJson(model, MyWalletModel.class);
        packageModel = new Gson().fromJson(pModel, PackageModel.class);
        setDetail();
    }

    @Override
    protected void setListeners() {

        binding.closeBtn.setOnClickListener(v -> {
            onBackPressed();
        });


        binding.ivPlus.setOnClickListener(view -> {

            if (val ==  Integer.parseInt((String) binding.tvRemainingQty.getText())) {

            } else {
                val++;
                binding.tvQty.setText(String.valueOf(val));
            }
        });

        binding.ivMinus.setOnClickListener(view -> {
            if (val == 0) {
//                Toast.makeText(activity, "Please Select quantity", Toast.LENGTH_SHORT).show();
            } else {
                val--;
                binding.tvQty.setText(String.valueOf(val));
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
                    code = binding.pinView.getText().toString();
                }
            }
        });

        binding.redeemNowButton.setOnClickListener(view -> {
            String qtyText = binding.tvQty.getText().toString().trim();
            int qtyValue = Integer.parseInt(qtyText);
            if (qtyValue == 0) {
                Toast.makeText(activity, "Please select a quantity", Toast.LENGTH_SHORT).show();
                return;
            }
            if (Utils.isNullOrEmpty(binding.tvQty.getText().toString().trim())) {
                Toast.makeText(activity, getString(R.string.enter_your_first_claimcode), Toast.LENGTH_SHORT).show();
                return;
            }
            if (Utils.isNullOrEmpty(code)) {
                Toast.makeText(activity, getString(R.string.enter_your_first_claimcode), Toast.LENGTH_SHORT).show();
                return;
            }

            requestRedeemNow(binding.tvQty.getText().toString().trim());
        });
    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityRedeemBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void setDetail() {

        if (!Utils.isNullOrEmpty(packageModel.getTitle())) {
            binding.txtPackageName.setText(packageModel.getTitle());
        } else {
            binding.txtPackageName.setVisibility(View.GONE);
        }
        if (!Utils.isNullOrEmpty(packageModel.getDescription())) {
            binding.tvPackageDescription.setText(packageModel.getDescription());
        } else {
            binding.tvPackageDescription.setVisibility(View.GONE);
        }

        String modifiedString = packageModel.getDiscount().contains("%") ? packageModel.getDiscount() : packageModel.getDiscount() + "%";
        if (packageModel.getDiscount().equals("0")) {
           binding.tvPrice.setVisibility(View.GONE);
        } else {
            binding.tvPrice.setVisibility(View.VISIBLE);
            binding.tvPrice.setText(Utils.notNullString(modifiedString));
        }

        if (myWalletModel != null){
            if (myWalletModel.getItems() != null && !myWalletModel.getItems().isEmpty() && packageModel != null){
                for (ItemModel model : myWalletModel.getItems()){
                    if (model.getPackageId().equals(packageModel.getId())) {
                        binding.tvRemainingQty.setText(String.valueOf(model.getRemainingQty()));
                    }
                }
            }
        }

        if (myWalletModel.getType().equals("event")) {
            if (myWalletModel.getEvent() != null) {
                binding.tvTitle.setText(myWalletModel.getEvent().getVenue().getName());
                binding.tvAddress.setText(myWalletModel.getEvent().getVenue().getAddress());
                Graphics.loadImageWithFirstLetter(myWalletModel.getEvent().getVenue().getLogo(), binding.iconImg, myWalletModel.getEvent().getVenue().getName());
                binding.txtOfferName.setText(myWalletModel.getEvent().getTitle());
                binding.tvDescription.setText(myWalletModel.getEvent().getDescription());
            }
        } else if (myWalletModel.getType().equals("offer")) {
            if (myWalletModel.getOffer() != null) {
                binding.tvTitle.setText(myWalletModel.getOffer().getVenue().getName());
                binding.tvAddress.setText(myWalletModel.getOffer().getVenue().getAddress());
                Graphics.loadImageWithFirstLetter(myWalletModel.getOffer().getVenue().getLogo(), binding.iconImg, myWalletModel.getOffer().getVenue().getName());
                binding.txtOfferName.setText(myWalletModel.getOffer().getTitle());
                binding.tvDescription.setText(myWalletModel.getOffer().getDescription());

            }
        }
    }

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------
    private void requestRedeemNow(String qty) {
        JsonObject object = new JsonObject();
        object.addProperty("packageId", packageModel.getId());
        object.addProperty("qty", qty);
        object.addProperty("claimCode", code);
        object.addProperty("type",myWalletModel.getType());
        showProgress();
        DataService.shared(activity).requestPackageRedeem(object, new RestCallback<ContainerModel<CommanMsgModel>>(this) {
            @Override
            public void result(ContainerModel<CommanMsgModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    Log.d("TAG", "result: "+error);
                    return;
                }
                Toast.makeText(activity, model.message, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.putExtra("close",true);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    // endregion
    // --------------------------------------
}