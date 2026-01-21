package com.whosin.app.ui.activites.offers;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.whosin.app.R;
import com.whosin.app.comman.AppConstants;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.ActivityClaimHistoryBinding;
import com.whosin.app.databinding.LayoutClaimBrunchHistoryBinding;
import com.whosin.app.databinding.LayoutClaimTotalHistoryBinding;
import com.whosin.app.databinding.LayoutSuccessBrunchListBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.models.BrunchModel;
import com.whosin.app.service.models.ClaimHistoryModel;
import com.whosin.app.service.models.ContainerListModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.comman.BaseActivity;

import java.util.Locale;

import io.realm.internal.Util;

public class ClaimHistoryActivity extends BaseActivity {

    private ActivityClaimHistoryBinding binding;

    private final ClaimHistoryAdapter<ClaimHistoryModel> claimHistoryAdapter = new ClaimHistoryAdapter<>();

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    protected void initUi() {

        binding.nameControl.setText(getValue("claim_history"));
        binding.emptyPlaceHolderView.setEmptyPlaceTxtTitle(getValue("empty_claim_history"));

        Graphics.applyBlurEffect(activity, binding.blurView);
        binding.rvClaimHistory.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        binding.rvClaimHistory.setAdapter(claimHistoryAdapter);
        claimHistory();
    }

    @Override
    protected void setListeners() {
        Glide.with(activity).load(R.drawable.icon_close_btn).into(binding.ivClose);
        binding.swipeRefreshLayout.setOnRefreshListener(() -> claimHistory());
        binding.ivClose.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityClaimHistoryBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void claimHistory() {
        showProgress();
        DataService.shared(activity).requestClaimHistory(new RestCallback<ContainerListModel<ClaimHistoryModel>>(this) {
            @Override
            public void result(ContainerListModel<ClaimHistoryModel> model, String error) {
                binding.swipeRefreshLayout.setRefreshing(false);
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (model.data != null && !model.data.isEmpty()) {
                    binding.rvClaimHistory.setVisibility(View.VISIBLE);
                    binding.emptyPlaceHolderView.setVisibility(View.GONE);
                    claimHistoryAdapter.updateData(model.data);
                } else {
                    binding.rvClaimHistory.setVisibility(View.GONE);
                    binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
                }

            }
        });
    }


    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------


    public class ClaimHistoryAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            switch (AppConstants.CLAIMTYPE.valueOf(viewType)) {
                case CLAIM_TOTAL:
                    return new ClaimTotalBlockHolder(UiUtils.getViewBy(parent, R.layout.layout_claim_total_history));
                case CLAIM_BRUNCH:
                    return new ClaimBrunchBlockHolder(UiUtils.getViewBy(parent, R.layout.layout_claim_brunch_history));
            }
            return null;
        }

        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ClaimHistoryModel model = (ClaimHistoryModel) getItem(position);
            boolean isLastItem = position == getItemCount() - 1;
            boolean isFirstItem = position == 0;
            if (model.getClaimType() == AppConstants.CLAIMTYPE.CLAIM_TOTAL) {
                ClaimTotalBlockHolder viewHolder = (ClaimTotalBlockHolder) holder;
                viewHolder.setupData(model);
            } else if (model.getClaimType() == AppConstants.CLAIMTYPE.CLAIM_BRUNCH) {
                ClaimBrunchBlockHolder viewHolder = (ClaimBrunchBlockHolder) holder;
                viewHolder.setupData(model);
            }

            if (isLastItem) {
                int marginBottom = Utils.getMarginBottom(holder.itemView.getContext(), 0.12f);
                Utils.setBottomMargin(holder.itemView, marginBottom);
            } else {
                Utils.setBottomMargin(holder.itemView, 0);
            }

            if (isFirstItem) {
                int topBottom = Utils.getMarginTop(holder.itemView.getContext(), 0.09f);
                Utils.setTopMargin(holder.itemView, topBottom);
            } else {
                Utils.setTopMargin(holder.itemView, 30);
            }
        }


        @Override
        public int getItemViewType(int position) {
            ClaimHistoryModel model = (ClaimHistoryModel) getItem(position);
            return model.getClaimType().getValue();
        }

        public class ClaimTotalBlockHolder extends RecyclerView.ViewHolder {
            private final LayoutClaimTotalHistoryBinding mBinding;

            public ClaimTotalBlockHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = LayoutClaimTotalHistoryBinding.bind(itemView);
            }

            public void setupData(ClaimHistoryModel model) {
                if (model != null) {
                    if (model.getVenue() != null) {
                        mBinding.userVenueContainer.setVenueDetail(model.getVenue());
                    }

                    mBinding.txtPX.setText(model.getTotalPerson() != null ? model.getTotalPerson() : "0");

                    mBinding.txtClaimId.setText(model.getClaimId());
                    String date = null;
                    try {
                        date = Utils.formatDate(Utils.stringToDate(model.getCreatedAt(), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"), "dd MMM yyyy HH:mm");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    mBinding.txtDate.setText(date);
                    if (model.getBillAmount() != null) {
                        if (!TextUtils.isEmpty(model.getBillAmount())) {
                            try {
                                double originalPrice = Double.parseDouble(model.getBillAmount());
                                mBinding.txtTotalAmount.setText(String.format(Locale.ENGLISH,"AED %.2f", originalPrice));
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        } else {
                            mBinding.txtTotalAmount.setText("AED 0");
                        }
                    }

                    if (model.getSpecialOffer() != null){
                        mBinding.txtDiscount.setText("AED " + Utils.formatAmount(model.getSpecialOffer().getPricePerPerson()));
                        mBinding.tvDiscountLabel.setText(Utils.isNullOrEmpty(String.valueOf(model.getSpecialOffer().getDiscount())) ? "0%" : model.getSpecialOffer().getDiscount() + "%");
                    }
                }
            }
        }

        public class ClaimBrunchBlockHolder extends RecyclerView.ViewHolder {
            private final LayoutClaimBrunchHistoryBinding mBinding;
            private BrunchAdapter<BrunchModel> brunchAdapter = new BrunchAdapter();

            public ClaimBrunchBlockHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = LayoutClaimBrunchHistoryBinding.bind(itemView);
                mBinding.rvBrunchList.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
                mBinding.rvBrunchList.setAdapter(brunchAdapter);

            }

            public void setupData(ClaimHistoryModel model) {

                if (model != null) {
                    mBinding.userVenueContainer.setVenueDetail(model.getVenue());

                    if (model != null && model.getSpecialOffer() != null && String.valueOf(model.getSpecialOffer().getDiscount()) != null) {
                        mBinding.tvDiscountLabel.setText(Utils.isNullOrEmpty(String.valueOf(model.getSpecialOffer().getDiscount())) ? "0%" : String.valueOf(model.getSpecialOffer().getDiscount()) + "%");
                    }
                    String date = null;
                    try {
                        date = Utils.formatDate(Utils.stringToDate(model.getCreatedAt(), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"), "dd MMM yyyy HH:mm");
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    mBinding.txtDate.setText(date);
                    mBinding.txtClaimId.setText(model.getClaimId());

                    brunchAdapter.updateData(model.getBrunch());

                    int totalAmount = brunchAdapter.getData().stream().mapToInt(p -> Utils.stringToInt(p.getAmount())).sum();
                    mBinding.txtTotalAmount.setText("AED "+totalAmount);

                    int discountAmount = brunchAdapter.getData().stream().mapToInt(BrunchModel::getDiscountValue).sum();
                    mBinding.txtDiscount.setText("AED "+discountAmount);

                    mBinding.rvBrunchList.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            binding.swipeRefreshLayout.setEnabled(false);

                            if (model.getBrunch().size() > 3) {
                                binding.rvClaimHistory.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                    @Override
                                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                        super.onScrolled(recyclerView, dx, dy);
                                        mBinding.rvBrunchList.scrollBy(dx, dy);
                                    }
                                });
                            }
                            return true;
                        }
                    });

                    if (model.getBrunch().size() > 3) {
                        int itemHeight = getResources().getDimensionPixelSize(R.dimen.item_height);
                        int recyclerViewHeight = itemHeight;

                        mBinding.rvBrunchList.getLayoutParams().height = recyclerViewHeight;
                        mBinding.rvBrunchList.requestLayout();

                    } else {
                        mBinding.rvBrunchList.getLayoutParams().height = RecyclerView.LayoutParams.WRAP_CONTENT;
                        mBinding.rvBrunchList.requestLayout();

                    }

                }
            }

            @SuppressLint("DefaultLocale")
            private void updateTotalAmount() {
                double totalAmount = brunchAdapter.getData().stream().mapToInt(p -> p.getQty() * Utils.stringToInt(p.getAmount())).sum();
                double discountCharges = brunchAdapter.getData().stream().mapToInt(p -> p.getQty() * Utils.stringToInt(p.getDiscount())).sum();
                double totalDiscountedPrice = brunchAdapter.getData().stream().mapToInt(p -> ((p.getQty() * Utils.stringToInt(p.getAmount())) * p.getDiscountValue()) / 100).sum();
                mBinding.txtTotalAmount.setText("AED " + Utils.formatAmount(String.valueOf(totalAmount)));
                mBinding.txtDiscount.setText("AED " + Utils.formatAmount(String.valueOf(discountCharges)));
            }

            public class BrunchAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

                @NonNull
                @Override
                public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = UiUtils.getViewBy(parent, R.layout.layout_success_brunch_list);
                    return new BrunchAdapter.ViewHolder(view);
                }

                @Override
                public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                    ViewHolder viewHolder = (ViewHolder) holder;
                    BrunchModel model = (BrunchModel) getItem(position);

                    viewHolder.mBinding.txtName.setText(model.getItem());
                    int original = Utils.stringToInt(model.getAmount()) + model.getDiscountValue(); //Integer.parseInt(model.getAmount()) + Integer.parseInt(model.getDiscount());
                    viewHolder.mBinding.txtOriginalPrice.setText(String.format(Locale.ENGLISH,"AED %d", original));
                    viewHolder.mBinding.txtOriginalPrice.setPaintFlags(viewHolder.mBinding.txtOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    viewHolder.mBinding.tvQty.setText(String.valueOf(model.getQty()));
                    viewHolder.mBinding.txtDiscountCharges.setText(String.format(Locale.ENGLISH,"Discount Charges (AED %d/px)", model.getPricePerBrunch()));

                    double originalPrice = Double.parseDouble(model.getAmount());
                    double discountPercentage = Double.parseDouble(model.getDiscount());
                    double discountAmount = (originalPrice * discountPercentage) / 100;
                    double discountedPrice = originalPrice - discountAmount;
                    viewHolder.mBinding.txtNewPrice.setText(String.format("%s AED", Utils.formatAmount(model.getAmount())));

                    String strDiscountedPrice = Utils.formatAmount(String.valueOf(discountedPrice));

                    int integerValue = (int) discountedPrice;

                    model.setDiscountedPrice(integerValue);
                    updateTotalAmount();
                }

                public class ViewHolder extends RecyclerView.ViewHolder {
                    private LayoutSuccessBrunchListBinding mBinding;

                    public ViewHolder(@NonNull View itemView) {
                        super(itemView);
                        mBinding = LayoutSuccessBrunchListBinding.bind(itemView);
                    }
                }
            }

        }
    }


    // endregion
    // --------------------------------------
}