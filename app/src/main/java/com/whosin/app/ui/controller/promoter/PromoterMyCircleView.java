package com.whosin.app.ui.controller.promoter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.asynclayoutinflater.view.AsyncLayoutInflater;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
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
import com.whosin.app.databinding.LayoutMyCircleViewBinding;
import com.whosin.app.service.models.PromoterCirclesModel;
import com.whosin.app.ui.activites.Promoter.CirclesDetailActivity;
import com.whosin.app.ui.activites.Promoter.CirclesUserDetailActivity;
import com.whosin.app.ui.fragment.Promoter.PromoterBootmSheets.PromoterCreateYourCircleBottomSheet;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PromoterMyCircleView extends ConstraintLayout {

    private LayoutMyCircleViewBinding binding;

    private Context context;

    private Activity activity;

    private FragmentManager supportFragmentManager;

    private List<PromoterCirclesModel> promoterCircleList;

    private CustomMyCircaleAdapter<PromoterCirclesModel> customMyCircaleAdapter;

    public List<String> filterIdList = new ArrayList<>();

    public boolean isFromSubAdmin = false;

    public boolean isPinkBackground = false;
    private boolean isHideButton = false;
    private boolean isSelectRings = false;
    private boolean isAllSelect = true;
    private boolean isPublic = false;
    public boolean selectAllCircles = false;


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    public PromoterMyCircleView(Context context) {
        this(context, null);
    }

    public PromoterMyCircleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PromoterMyCircleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.offer_info_view_loader, this, true);

        AsyncLayoutInflater asyncLayoutInflater = new AsyncLayoutInflater(context);
        asyncLayoutInflater.inflate(R.layout.layout_my_circle_view, this, (view, resid, parent) -> {
            binding = LayoutMyCircleViewBinding.bind(view);

            binding.tvMyCirclesTitle.setText(Utils.getLangValue("my_circles"));
            binding.tvAddMoreTitle.setText(Utils.getLangValue("add_more"));
            binding.tvForSeeAllBtn.setText(Utils.getLangValue("see_all"));

            if (isHideButton) {
                binding.btnAddMore.setVisibility(GONE);
                binding.tvForSeeAllBtn.setText(Utils.getLangValue("select_all"));
            }

            if (isFromSubAdmin){
                binding.btnAddMore.setVisibility(GONE);
            }

            if (isPinkBackground) {
                binding.seeAllBtn.setBackgroundColor(ContextCompat.getColor(activity, R.color.brand_pink));
                binding.btnAddMore.setVisibility(View.GONE);
            }

            binding.btnAddMore.setOnClickListener(v -> {
                PromoterCreateYourCircleBottomSheet bootomSheet = new PromoterCreateYourCircleBottomSheet();
                bootomSheet.show(supportFragmentManager, "");
            });

            if (isSelectRings) {setButtonTitle();}

            binding.seeAllBtn.setOnClickListener(v -> {
                if (isSelectRings) {
                    if (isAllSelect) {
                        filterIdList.clear();
                        List<String> allIds = promoterCircleList.stream().map(PromoterCirclesModel::getId).collect(Collectors.toList());
                        filterIdList.addAll(allIds);
                        binding.tvForSeeAllBtn.setText(Utils.getLangValue("deselect_all"));
                        selectAllCircles = true;
                        isAllSelect = false;
                    } else {
                        filterIdList.clear();
                        isAllSelect = true;
                        selectAllCircles = false;
                        binding.tvForSeeAllBtn.setText(Utils.getLangValue("select_all"));
                    }
                    customMyCircaleAdapter.notifyDataSetChanged();

                } else {
                    activity.startActivity(new Intent(activity, CirclesUserDetailActivity.class)
                                    .putExtra("isFromSubAdmin",isFromSubAdmin)
                            .putExtra("promoterList", new Gson().toJson(promoterCircleList)));
                }
            });

            setUpRecycleView(promoterCircleList);

            PromoterMyCircleView.this.removeAllViews();
            PromoterMyCircleView.this.addView(view);

        });
    }

    // endregion
    // --------------------------------------
    // region Public
    // --------------------------------------



    public void setUpData(List<PromoterCirclesModel> list, Activity activity, FragmentManager childFragmentManager) {
        this.promoterCircleList = list;
        this.activity = activity;
        this.supportFragmentManager = childFragmentManager;

        if (binding == null) {
            return;
        }

        setUpRecycleView(list);

    }

    public void setUpDataForRing(List<PromoterCirclesModel> list, Activity activity, FragmentManager childFragmentManager, boolean isHideButton, boolean isRingSelect) {
        this.promoterCircleList = list;
        this.activity = activity;

        this.isHideButton = isHideButton;
        this.isSelectRings = isRingSelect;
        this.supportFragmentManager = childFragmentManager;


        if (binding == null) {
            return;
        }

        if (isHideButton) {
            binding.btnAddMore.setVisibility(GONE);
            binding.tvForSeeAllBtn.setText(Utils.getLangValue("select_all"));
        }

        if (isPinkBackground) {
            binding.seeAllBtn.setBackgroundColor(ContextCompat.getColor(activity, R.color.brand_pink));
            binding.btnAddMore.setVisibility(View.GONE);
        }

        setUpRecycleView(list);

    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------


    private void setButtonTitle(){
        if (isSelectRings) {

            boolean areFilterIdListNotEmpty = filterIdList != null && !filterIdList.isEmpty();
            boolean arePromoterCircleListNotEmpty = promoterCircleList != null && !promoterCircleList.isEmpty();

            if (areFilterIdListNotEmpty && arePromoterCircleListNotEmpty) {
                if (filterIdList.size() == promoterCircleList.size()) {
                    binding.tvForSeeAllBtn.setText(Utils.getLangValue("deselect_all"));
                    isAllSelect = false;
                } else {
                    binding.tvForSeeAllBtn.setText(Utils.getLangValue("select_all"));
                    isAllSelect = true;
                }
            } else {
                binding.tvForSeeAllBtn.setText(Utils.getLangValue("select_all"));
                isAllSelect = true;
            }

        }
    }

    private void setUpRecycleView(List<PromoterCirclesModel> promoterCircleList){
        customMyCircaleAdapter = new CustomMyCircaleAdapter<>();
        if (promoterCircleList != null && !promoterCircleList.isEmpty()) {
            if (promoterCircleList.size() > 3) {
                binding.myCircalRecycler.setLayoutManager(new GridLayoutManager(activity, 2, GridLayoutManager.HORIZONTAL, false));
            } else {
                binding.myCircalRecycler.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
            }
        }

        binding.myCircalRecycler.setAdapter(customMyCircaleAdapter);
        if (promoterCircleList != null && !promoterCircleList.isEmpty()) {
            activity.runOnUiThread(() -> customMyCircaleAdapter.updateData(promoterCircleList));
            binding.myCircalRecycler.setVisibility(VISIBLE);
            binding.emptyPlaceHolderView.setVisibility(GONE);
        }else {
            binding.emptyPlaceHolderView.setVisibility(VISIBLE);
            binding.myCircalRecycler.setVisibility(GONE);
        }
    }

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------


    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    private class CustomMyCircaleAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_my_circle));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            ViewHolder viewHolder = (ViewHolder) holder;

            PromoterCirclesModel model = (PromoterCirclesModel) getItem(position);

            if (model == null){return;}

            Graphics.loadImageWithFirstLetter(model.getAvatar(), viewHolder.mBinding.image, model.getTitle());

            viewHolder.mBinding.tvName.setText(model.getTitle());

            viewHolder.mBinding.tvMemberCount.setText(String.valueOf(model.getTotalMembers()));

            boolean idFound1 = filterIdList.stream().anyMatch(ids -> ids.equals(model.getId()));
            if (idFound1) {
                int borderColor = getResources().getColor(R.color.date_red);
                viewHolder.mBinding.image.setBorderColor(borderColor);
                float borderWidthInPixels = getResources().getDimensionPixelSize(R.dimen.border_width);
                viewHolder.mBinding.image.setBorderWidth((int) borderWidthInPixels);
            } else {
                viewHolder.mBinding.image.setBorderWidth(0);
            }

            viewHolder.itemView.setOnClickListener(view -> {
                if (isSelectRings) {
                    boolean idFound = filterIdList.stream().anyMatch(ids -> ids.equals(model.getId()));
                    if (idFound) {
                        viewHolder.mBinding.image.setBorderWidth(0);
                        filterIdList.remove(model.getId());

                    } else {
                        int borderColor = getResources().getColor(R.color.date_red);
                        viewHolder.mBinding.image.setBorderColor(borderColor);
                        float borderWidthInPixels = getResources().getDimensionPixelSize(R.dimen.border_width);
                        viewHolder.mBinding.image.setBorderWidth((int) borderWidthInPixels);
                        if (!filterIdList.contains(model.getId())) {
                            filterIdList.add(model.getId());
                        }
//                        filterIdList.add(model.getId());
                    }

                    setButtonTitle();

//                    if (filterIdList.isEmpty()) {
//                        isAllSelect = true;
//                        binding.tvForSeeAllBtn.setText("Select all");
//                    }
                } else {
                    if (isFromSubAdmin){return;}
                    if (!isPublic) {
                        activity.startActivity(new Intent(activity, CirclesDetailActivity.class)
                                .putExtra("promoterModel", new Gson().toJson(model)));
                    }
                }
            });

        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final ItemMyCircleBinding mBinding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemMyCircleBinding.bind(itemView);
            }
        }
    }

    // endregion
    // --------------------------------------

}
