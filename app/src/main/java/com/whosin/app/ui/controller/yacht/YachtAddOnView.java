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
import com.whosin.app.databinding.ItemAddOnDesignBinding;
import com.whosin.app.databinding.YachtAddOnViewBinding;
import com.whosin.app.service.models.YachtAddOnModel;
import com.whosin.app.service.models.YachtPackageModel;

import java.util.ArrayList;
import java.util.List;

public class YachtAddOnView extends ConstraintLayout {

    private YachtAddOnViewBinding binding;
    private Context context;
    private Activity activity;
    private final YachtAddOnAdapter<YachtAddOnModel> yachtAddOnAdapter = new  YachtAddOnAdapter<>();
    private List<String> yachtAddOnId = new ArrayList<>();



    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    public YachtAddOnView(Context context) {
        this(context, null);
    }

    public YachtAddOnView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public YachtAddOnView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, 0);
        this.context = context;
        View view  = LayoutInflater.from(context).inflate(R.layout.yacht_add_on_view, this, true);
        binding = YachtAddOnViewBinding.bind(view);
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------


    // endregion
    // --------------------------------------
    // region Public
    // --------------------------------------

    public void setupData(Activity activity , String title, List<YachtAddOnModel> yachtAddOnList) {
        this.activity = activity;
        if (yachtAddOnList == null || yachtAddOnList.isEmpty()){return;}
        if (binding == null) {return;}
        activity.runOnUiThread(() -> {
            binding.addOnTitle.setText(title);
            binding.addOnRecyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
            binding.addOnRecyclerView.setAdapter(yachtAddOnAdapter);
            binding.addOnRecyclerView.setNestedScrollingEnabled(false);
            yachtAddOnAdapter.updateData(yachtAddOnList);
        });


    }

    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------


    public class YachtAddOnAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {


        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = UiUtils.getViewBy(parent, R.layout.item_add_on_design);
            return new ViewHolder(view);
        }

        @SuppressLint({"SetTextI18n", "DefaultLocale"})
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            ViewHolder viewHolder = ( ViewHolder) holder;
            YachtAddOnModel model = (YachtAddOnModel) getItem(position);

            if (model != null) {
                activity.runOnUiThread(() -> {
                    viewHolder.mBinding.tvPrice.setText("AED " + model.getPrice());
                    viewHolder.mBinding.addOnTitle.setText(model.getTitle());


                    viewHolder.mBinding.getRoot().setOnClickListener(v -> {
                        viewHolder.setBackgroundColor(model);
                    });
                });
            } else {
                Log.d("HomeBlockLargeVenueView", "onBindViewHolder: empty model");
            }
        }


        public class ViewHolder extends RecyclerView.ViewHolder {

            private final ItemAddOnDesignBinding mBinding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemAddOnDesignBinding.bind(itemView);
            }

            private void setBackgroundColor(YachtAddOnModel model) {
                if (!yachtAddOnId.contains(model.getId())) {
                    mBinding.getRoot().setBackgroundResource(R.drawable.add_on_unselect_background);
                    mBinding.roundLinear.setBackgroundResource(R.color.white);
                    mBinding.tvPrice.setTextColor(ContextCompat.getColor(context, R.color.add_on_bg));
                    yachtAddOnId.add(model.getId());
                } else {
                    mBinding.getRoot().setBackgroundResource(R.drawable.add_on_background);
                    mBinding.roundLinear.setBackgroundResource(R.color.add_on_bg);
                    mBinding.tvPrice.setTextColor(ContextCompat.getColor(context, R.color.white));
                    yachtAddOnId.remove(model.getId());
                }
            }

        }



    }



    // --------------------------------------
    // endregion


}
