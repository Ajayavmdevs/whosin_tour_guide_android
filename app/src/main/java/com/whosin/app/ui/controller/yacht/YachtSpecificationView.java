package com.whosin.app.ui.controller.yacht;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.comman.ui.VerticalSpaceItemDecoration;
import com.whosin.app.databinding.ItemYachtSpecificationsBinding;
import com.whosin.app.databinding.YachtSpecificationViewBinding;
import com.whosin.app.service.models.YachtsSpecificationModel;

import java.util.List;

public class YachtSpecificationView extends ConstraintLayout {

    private final YachtSpecificationViewBinding binding;
    private Context context;
    private Activity activity;
    private List<YachtsSpecificationModel> yachtFeatureModels;

    private SpecificationsListAdapter<YachtsSpecificationModel> yachtFeatureAdapter;


    public YachtSpecificationView(Context context) {
        this(context, null);
    }

    public YachtSpecificationView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public YachtSpecificationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, 0);
        this.context = context;
        View view = LayoutInflater.from(context).inflate(R.layout.yacht_specification_view, this);
        binding = YachtSpecificationViewBinding.bind(view);
    }

    public void setupData(List<YachtsSpecificationModel> yacht, Activity activity) {
        this.yachtFeatureModels = yacht;
        this.activity = activity;
        if (yachtFeatureModels == null) {
            return;
        }
        if (binding == null) {
            return;
        }

        yachtFeatureAdapter = new SpecificationsListAdapter<>();
        binding.specificationRecycler.setAdapter(yachtFeatureAdapter);

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        binding.specificationRecycler.setLayoutManager(layoutManager);
        binding.specificationRecycler.setNestedScrollingEnabled(false);
        int spacing = getResources().getDimensionPixelSize(com.intuit.ssp.R.dimen._5ssp);
        binding.specificationRecycler.addItemDecoration(new VerticalSpaceItemDecoration(spacing));


        if (yachtFeatureAdapter != null) {
            activity.runOnUiThread(() -> yachtFeatureAdapter.updateData(yachtFeatureModels));
        }

    }


    private class SpecificationsListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_yacht_specifications));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            ViewHolder viewHolder = (ViewHolder) holder;
            YachtsSpecificationModel model = (YachtsSpecificationModel) getItem(position);

            viewHolder.binding.tvTitle.setVisibility(model.isShowTitle() ? View.VISIBLE : View.GONE);
            if (model.isShowTitle()) {
                viewHolder.binding.tvTitle.setText(String.format("%s : ", model.getTitle()));
            }
            viewHolder.binding.iconText.setText(model.getValue());


        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final ItemYachtSpecificationsBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemYachtSpecificationsBinding.bind(itemView);
            }
        }
    }

}
