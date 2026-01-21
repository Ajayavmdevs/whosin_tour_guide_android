package com.whosin.app.ui.controller.promoter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.asynclayoutinflater.view.AsyncLayoutInflater;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.ItemMyCircleBinding;
import com.whosin.app.databinding.ItemRequirementSubItemDesignBinding;
import com.whosin.app.databinding.LayoutRequirementViewBinding;
import com.whosin.app.service.models.PromoterCirclesModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.ui.activites.Promoter.CirclesDetailActivity;
import com.whosin.app.ui.fragment.PromoterCreateEvent.RequirementsAddDialog;

import java.util.ArrayList;
import java.util.List;

public class PromoterRequirementsView extends ConstraintLayout {

    private LayoutRequirementViewBinding binding;

    private RequirementsAdapter adapter;

    private Context context;

    private Activity activity;

    private FragmentManager supportFragmentManager;

    public List<String> requirementList = new ArrayList<>();

    private boolean isAllowed = false;

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------


    public PromoterRequirementsView(Context context) {
        this(context, null);
    }

    public PromoterRequirementsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PromoterRequirementsView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RequirementsField, 0, 0);

        String titleText = a.getString(R.styleable.RequirementsField_main_title_name);
        String subTitle = a.getString(R.styleable.RequirementsField_subTitleText);
        boolean showTitle = a.getBoolean(R.styleable.RequirementsField_show_main_title, false);
        Drawable formIcon = a.getDrawable(R.styleable.RequirementsField_requirementsFieldIcon);

        View view = LayoutInflater.from(context).inflate(R.layout.layout_requirement_view, this);
        binding = LayoutRequirementViewBinding.bind(view);

        if (!Utils.isNullOrEmpty(subTitle)) {
            binding.subTitleText.setText(subTitle);
        }

        if (showTitle && !Utils.isNullOrEmpty(titleText)) {
            binding.tvTitle.setText(titleText);
            binding.tvTitle.setVisibility(View.VISIBLE);
        } else {
            binding.tvTitle.setVisibility(View.GONE);
        }

        if (formIcon != null) {
            binding.ivRequirements.setImageDrawable(formIcon);
        }

    }

    // endregion
    // --------------------------------------
    // region Public
    // --------------------------------------

    public void setTitles(String title,String subTitle){
        binding.tvTitle.setText(title);
        binding.subTitleText.setText(subTitle);

        binding.addMoreOptionsTitle.setText(Utils.getLangValue("add_mores_options"));
        binding.addMultipleRequirementsTitle.setText(Utils.getLangValue("add_multiple_requirements"));
    }

    public void setUpData(List<String> listOfItem ,Activity activity, FragmentManager supportFragmentManager, boolean isAllowedForIconSet) {
        this.activity = activity;

        this.supportFragmentManager = supportFragmentManager;

        this.isAllowed = isAllowedForIconSet;

        if (binding == null) {
            return;
        }

        adapter = new RequirementsAdapter(supportFragmentManager, requirementList);
        binding.addedItemRecycleview.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        binding.addedItemRecycleview.setAdapter(adapter);
        if (listOfItem != null && !listOfItem.isEmpty()) {
            requirementList.addAll(listOfItem);
            adapter.updateData(requirementList);
        }

        if (!requirementList.isEmpty()){
            adapter.updateData(requirementList);
        }


        binding.addMoreLayout.setOnClickListener(v -> {
            RequirementsAddDialog dialog = new RequirementsAddDialog();
            dialog.requirementTitle = binding.subTitleText.getText().toString();
            dialog.callback = data -> {
                if (!Utils.isNullOrEmpty(data)) {
                    requirementList.add(data);
                    adapter.updateData(requirementList);
                }
            };
            dialog.show(supportFragmentManager, "");
        });

    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------


    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------


    private class RequirementsAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        private final FragmentManager fragmentManager;

        private List<String> list;

        public RequirementsAdapter(FragmentManager fragmentManager, List<String> tmpList) {
            this.fragmentManager = fragmentManager;
            this.list = tmpList;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_requirement_sub_item_design));

        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            ViewHolder viewHolder = (ViewHolder) holder;

            viewHolder.mBinding.tvRequirement.setText(list.get(position));

            int drawableId = isAllowed ? R.drawable.icon_add_requirements : R.drawable.icon_not_requirements;
            viewHolder.mBinding.ivForRequirement.setImageDrawable(ContextCompat.getDrawable(activity, drawableId));

            viewHolder.mBinding.btnRemove.setOnClickListener(v -> {
                requirementList.remove(position);
                adapter.updateData(requirementList);
            });

            viewHolder.mBinding.btnEdit.setOnClickListener(v -> {
                RequirementsAddDialog dialog = new RequirementsAddDialog();
                dialog.requirementTitle = binding.subTitleText.getText().toString();
                dialog.isEdit = true;
                dialog.editSting = list.get(position);
                dialog.callback = data -> {
                    if (!Utils.isNullOrEmpty(data)) {
                        requirementList.set(position, data);
                        adapter.updateData(requirementList);
                    }
                };
                dialog.show(supportFragmentManager, "");
            });

        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final ItemRequirementSubItemDesignBinding mBinding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemRequirementSubItemDesignBinding.bind(itemView);
            }
        }
    }


    // endregion
    // --------------------------------------

}
