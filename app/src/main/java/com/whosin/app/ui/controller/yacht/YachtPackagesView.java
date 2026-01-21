package com.whosin.app.ui.controller.yacht;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.ActivityYachtPackagesViewBinding;
import com.whosin.app.databinding.ItemYachtPackagesDesignBinding;
import com.whosin.app.service.models.YachtPackageModel;

import java.util.ArrayList;
import java.util.List;

public class YachtPackagesView extends ConstraintLayout {

    private ActivityYachtPackagesViewBinding binding;
    private Context context;
    private Activity activity;
    private final YachtPackageAdapter<YachtPackageModel> yachtPackageAdapter = new YachtPackageAdapter<>();
    private List<String> packagesId = new ArrayList<>();
    private PackageType packageType;
    public enum PackageType {
        hourly, fixed
    }


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------


    public YachtPackagesView(Context context) {
        this(context, null);
    }

    public YachtPackagesView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public YachtPackagesView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, 0);
        this.context = context;
        View view  = LayoutInflater.from(context).inflate(R.layout.activity_yacht_packages_view, this, true);
        binding = ActivityYachtPackagesViewBinding.bind(view);
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------


    // endregion
    // --------------------------------------
    // region Public
    // --------------------------------------


    public void setupData(Activity activity ,String title,List<YachtPackageModel> yachtOfferList, PackageType type) {
        this.activity = activity;
        if (yachtOfferList == null || yachtOfferList.isEmpty()){return;}
        if (binding == null) {return;}
        activity.runOnUiThread(() -> {
            binding.packageTitle.setText(title);
            packageType = type;
            binding.packageRecyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
            binding.packageRecyclerView.setAdapter(yachtPackageAdapter);
            binding.packageRecyclerView.setNestedScrollingEnabled(false);
            yachtPackageAdapter.updateData(yachtOfferList);
        });


    }


    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------


    public class YachtPackageAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        private int currentHours = 0;

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = UiUtils.getViewBy(parent, R.layout.item_yacht_packages_design);
            return new ViewHolder(view);
        }

        @SuppressLint({"SetTextI18n", "DefaultLocale"})
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            ViewHolder viewHolder = (ViewHolder) holder;
            YachtPackageModel model = (YachtPackageModel) getItem(position);

            if (model != null) {
                activity.runOnUiThread(() -> {
                    viewHolder.mBinding.packageTitle.setText(model.getTitle());
                    viewHolder.mBinding.packagesDescription.setText(model.getDescription());

                    double discountedAmount = model.getAmount() - (model.getAmount() * ((double) model.getDiscount() / 100.0));


                    if (packageType == PackageType.hourly) {
                        viewHolder.mBinding.durationLayout.setVisibility(View.VISIBLE);
                        viewHolder.mBinding.addQuantity.setVisibility(View.VISIBLE);
                        viewHolder.mBinding.roundLinear.setVisibility(View.GONE);

                        viewHolder.mBinding.tvDurationAvailable.setText(String.format("Min.%dhr - Max.%dhrs(check availability)", model.getMinimumHour(), model.getMaximumHour()));
                        viewHolder.mBinding.tvTotalHours.setText(model.getMinimumHour() + "hrs");
                        currentHours = model.getMinimumHour();
                        viewHolder.mBinding.packageTotal.setText("AED " + (double) model.getPricePerHour() * (double) currentHours);


                    } else {
                        viewHolder.mBinding.durationLayout.setVisibility(View.GONE);
                        viewHolder.mBinding.addQuantity.setVisibility(View.GONE);
                        viewHolder.mBinding.roundLinear.setVisibility(View.VISIBLE);

                        viewHolder.mBinding.tvPrice.setText("AED " + model.getPricePerHour());

                    }


                    viewHolder.mBinding.ivPlus.setOnClickListener(v -> {
                        if (currentHours < model.getMaximumHour()) {
                            currentHours++;
                            viewHolder.packageCalculation(model);
                        }
                    });

                    viewHolder.mBinding.ivMinus.setOnClickListener(v -> {
                        if (currentHours > model.getMinimumHour()) {
                            currentHours--;
                            viewHolder.packageCalculation(model);
                        }
                    });

                    viewHolder.mBinding.getRoot().setOnClickListener(v -> {
                        if (packageType == PackageType.fixed) {
                            viewHolder.setBackgroundColor(model);
                        }
                    });


                });
            } else {
                Log.d("HomeBlockLargeVenueView", "onBindViewHolder: empty model");
            }
        }


        public class ViewHolder extends RecyclerView.ViewHolder {

            private final ItemYachtPackagesDesignBinding mBinding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemYachtPackagesDesignBinding.bind(itemView);
            }

            private void setBackgroundColor(YachtPackageModel model) {
                if (!packagesId.contains(model.getId())) {
                    mBinding.mainLayout.setBackgroundResource(R.color.brand_pink);
                    mBinding.roundLinear.setBackgroundResource(R.color.white);
                    mBinding.tvPrice.setTextColor(ContextCompat.getColor(context, R.color.brand_pink));
                    packagesId.add(model.getId());
                } else {
                    mBinding.mainLayout.setBackgroundResource(R.color.package_color);
                    mBinding.roundLinear.setBackgroundResource(R.color.brand_pink);
                    mBinding.tvPrice.setTextColor(ContextCompat.getColor(context, R.color.white));
                    packagesId.remove(model.getId());
                }
            }

            @SuppressLint({"DefaultLocale", "SetTextI18n"})
            private void packageCalculation(YachtPackageModel model) {
                mBinding.tvTotalHours.setText(String.format("%dhrs", currentHours));
                mBinding.packageDuration.setText(String.format("%dhrs", currentHours));
                mBinding.packageTotal.setText("AED " + (double) model.getPricePerHour() * (double) currentHours);
            }

        }



    }



    // --------------------------------------
    // endregion



}