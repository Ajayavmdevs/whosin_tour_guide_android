package com.whosin.app.ui.activites.offers;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.ActivityClaimSuccessBinding;
import com.whosin.app.databinding.LayoutSuccessBrunchListBinding;
import com.whosin.app.service.models.BrunchModel;
import com.whosin.app.service.models.ClaimSpecialOfferModel;
import com.whosin.app.service.models.SpecialOfferModel;
import com.whosin.app.ui.activites.comman.BaseActivity;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClaimSuccessActivity extends BaseActivity {

    private ActivityClaimSuccessBinding binding;
    private SpecialOfferModel specialOfferModel;
    private ClaimSpecialOfferModel claimSpecialOfferModel;
    private String title, subtitle, image,amount="" ,discountCharges;
    private BrunchAdapter<BrunchModel> brunchAdapter = new BrunchAdapter();

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    protected void initUi() {

        applyTranslations();

        Graphics.applyBlurEffectOnClaimScreen(activity, binding.blurView);
        binding.rvBrunchList.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        binding.rvBrunchList.setAdapter(brunchAdapter);
        title = getIntent().getStringExtra("title");
        subtitle = getIntent().getStringExtra("address");
        image = getIntent().getStringExtra("image");
        discountCharges = getIntent().getStringExtra("discountCharges");
        String specialOffer = getIntent().getStringExtra("specialOfferModel");
        specialOfferModel = new Gson().fromJson(specialOffer, SpecialOfferModel.class);
        String claimSpecialOffer = getIntent().getStringExtra("claimSpecialModel");
        claimSpecialOfferModel = new Gson().fromJson(claimSpecialOffer, ClaimSpecialOfferModel.class);
        String brunchList = getIntent().getStringExtra("brunchList");
        setDetail(brunchList);
    }

    @Override
    protected void setListeners() {
        binding.closeBtn.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityClaimSuccessBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }



    @Override
    protected Map<View, String> getTranslationMap() {
        Map<View, String> map = new HashMap<>();
        map.put(binding.tvClaimIdTitle, "claimId");
        map.put(binding.tvClaimedSuccessTitle, "claimed_successfuly");
        map.put(binding.claimTitle, "total_discount_on");
        map.put(binding.tvDiscountTitle, "discounts");
        map.put(binding.startNumberTextView, "total_amount");
        map.put(binding.tvDiscounTitle, "discount_charges");
        return map;
    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    @SuppressLint("DefaultLocale")
    private void updateTotalAmount() {
        double totalAmount = brunchAdapter.getData().stream().mapToInt(p -> p.getQty() * Utils.stringToInt(p.getAmount())).sum();
        double totalDiscountedPrice = brunchAdapter.getData().stream().mapToInt(BrunchModel::getDiscountValue).sum();
        binding.txtTotalAmount.setText("AED "+ Utils.formatAmount(String.valueOf(totalAmount)));
        binding.txtDiscount.setText("AED "+Utils.formatAmount(String.valueOf(totalDiscountedPrice)));
    }
    private void setDetail(String brunchList) {
        binding.tvTitle.setText(title);
        binding.tvAddress.setText(subtitle);
        if (specialOfferModel == null) { return; }
        Graphics.loadImageWithFirstLetter(image, binding.iconImg, title);

        if (Utils.isNullOrEmpty(String.valueOf(specialOfferModel.getDiscount()))) {
            binding.tvDiscountLabel.setText("0%");
            binding.txtDiscount.setText("AED 0");
        }
        else {
            double discountPercentage = Double.parseDouble(String.valueOf(specialOfferModel.getDiscount()));
            binding.tvDiscountLabel.setText(specialOfferModel.getDiscount()+"%");
            if (claimSpecialOfferModel != null) {
                if (claimSpecialOfferModel.getBillAmount() != null) {
                    double originalPrice = Double.parseDouble(String.valueOf(claimSpecialOfferModel.getBillAmount()));
                    double discountAmount = (originalPrice * discountPercentage) / 100;
                    int billTotal = (int) discountAmount;
                    binding.txtDiscount.setText("AED " + Utils.formatAmount(String.valueOf(billTotal)));
                }
                binding.tvClaimId.setText(claimSpecialOfferModel.getClaimId());
            }
        }

        binding.txtName.setText(specialOfferModel.getTitle());
        binding.txtDescription.setText(specialOfferModel.getDescription());


        if (specialOfferModel.getType().equals("brunch")) {
            binding.txtDescription.setVisibility(View.GONE);
            binding.rvBrunchList.setVisibility(View.VISIBLE);
            binding.layoutClaimTotal.setVisibility(View.GONE);
            binding.roundLinear.setVisibility(View.GONE);

            binding.discountChargeLayout.setVisibility(View.VISIBLE);
            binding.tvDiscountCharges.setText(discountCharges);
            Type listType = new TypeToken<List<BrunchModel>>() {}.getType();
            Gson gson = new Gson();
            List<BrunchModel> branchList = gson.fromJson(brunchList, listType);
            brunchAdapter.updateData(branchList);
        }
        else {
            binding.layoutTotal.setVisibility(View.GONE);
            binding.view3.setVisibility(View.GONE);
            binding.view2.setVisibility(View.GONE);
            binding.layoutDiscount.setVisibility(View.GONE);

            if (amount != null && !amount.isEmpty()) {
                binding.txtTotalAmount.setText("AED " + Utils.formatAmount(amount));
            }
            else {
                binding.txtTotalAmount.setText("AED 0");
            }
            if (claimSpecialOfferModel != null) {
                binding.txtPX.setText(claimSpecialOfferModel.getTotalPerson() + "   px");
            }
            binding.txtDescription.setVisibility(View.VISIBLE);
            binding.rvBrunchList.setVisibility(View.GONE);
            binding.layoutClaimTotal.setVisibility(View.VISIBLE);

            binding.claimTitle.setText(getValue("number_of_people"));

            binding.discountChargeLayout.setVisibility(View.VISIBLE);


            binding.tvDiscounTitle.setText(setValue("discount_charges_title",String.valueOf(specialOfferModel.getPricePerPerson())));
            binding.tvDiscountCharges.setText(discountCharges.equals("AED 0") ? getValue("free") : discountCharges);

        }
    }

    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    public class BrunchAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = UiUtils.getViewBy(parent, R.layout.layout_success_brunch_list);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            BrunchModel model = (BrunchModel) getItem(position);

            if (model != null) {
                viewHolder.mBinding.txtName.setText(model.getItem());
                int original = Utils.stringToInt(model.getAmount()) + model.getDiscountValue();
                viewHolder.mBinding.txtOriginalPrice.setText("AED " + original);
                viewHolder.mBinding.txtOriginalPrice.setPaintFlags(viewHolder.mBinding.txtOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                viewHolder.mBinding.tvQty.setText(String.valueOf(model.getQty()));

                double originalPrice = Double.parseDouble(model.getAmount());
                double discountPercentage = Double.parseDouble(model.getDiscount());
                double discountAmount = (originalPrice * discountPercentage) / 100;
                double discountedPrice = originalPrice - discountAmount;
                int intDiscount = (int) discountedPrice;
                String strDiscountedPrice = Utils.formatAmount(String.valueOf(intDiscount));
                model.setDiscountedPrice(Utils.stringToInt(strDiscountedPrice));
                updateTotalAmount();
                viewHolder.mBinding.txtNewPrice.setText(Utils.formatAmount(String.valueOf(model.getAmount())) + " AED");
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private LayoutSuccessBrunchListBinding mBinding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = LayoutSuccessBrunchListBinding.bind(itemView);
            }
        }
    }

    // endregion
}