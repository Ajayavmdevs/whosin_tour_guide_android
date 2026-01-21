package com.whosin.app.ui.controller.raynaTicketsView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.asynclayoutinflater.view.AsyncLayoutInflater;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.HorizontalSpaceItemDecoration;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.ActivityRaynaCustomCategoryBinding;
import com.whosin.app.databinding.ItemCategoriesBinding;
import com.whosin.app.databinding.ItemCategoryCircleBinding;
import com.whosin.app.databinding.ItemExploreFilterBinding;
import com.whosin.app.databinding.ItemNewExploreCategoryItemBinding;
import com.whosin.app.databinding.ItemNewExploreCityBinding;
import com.whosin.app.databinding.ItemNewRaynaCategoriesBinding;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.BannerModel;
import com.whosin.app.service.models.CategoriesModel;
import com.whosin.app.service.models.CommanMsgModel;
import com.whosin.app.service.models.HomeBlockModel;
import com.whosin.app.service.models.newExploreModels.ExploreBlockModel;
import com.whosin.app.ui.activites.explore.ExploreDetailActivity;
import com.whosin.app.ui.activites.raynaTicket.RaynaTicketDetailActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RaynaCategoryView extends ConstraintLayout {

    private ActivityRaynaCustomCategoryBinding binding;

    private Context context;

    private Activity activity;

    public String categoryType = "";

    public boolean isSetBottomPaddingForRecView = false;

    private ExploreBlockModel exploreBlockModel = null;

    private HomeBlockModel homeBlockModel = null;

    private boolean isFromHomeBlock = false;

    private final RaynaCategoryListAdapter<CategoriesModel> categoryListAdapter = new RaynaCategoryListAdapter<>();

    private List<CategoriesModel> categoriesList = new ArrayList<>();



    public RaynaCategoryView(Context context) {
        this(context, null);
    }

    public RaynaCategoryView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RaynaCategoryView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, 0);
        this.context = context;

        View view = LayoutInflater.from(context).inflate(R.layout.activity_rayna_custom_category, this, false);
        binding = ActivityRaynaCustomCategoryBinding.bind(view);


        binding.categoriesRecycler.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

        setupPadding();

        // Show titles if models exist
        if (exploreBlockModel != null) {
            hideAndShowTitle(exploreBlockModel, binding.userTitle, exploreBlockModel.getTitle());
            hideAndShowTitle(exploreBlockModel, binding.description, exploreBlockModel.getDescription());
        } else if (homeBlockModel != null) {
            hideAndShowTitle(homeBlockModel, binding.userTitle, homeBlockModel.getTitle());
            hideAndShowTitle(homeBlockModel, binding.description, homeBlockModel.getDescription());
        }

        // Setup adapter
        binding.categoriesRecycler.setAdapter(categoryListAdapter);
        if (categoriesList != null && !categoriesList.isEmpty()) {
            categoryListAdapter.updateData(categoriesList);
        }

        removeAllViews();
        addView(view);

    }

    public void setUpData(Activity activity, String categoryType, ExploreBlockModel model,List<CategoriesModel> categoriesModels){
        this.activity = activity;
        this.categoriesList = categoriesModels;
        this.categoryType = categoryType;
        this.exploreBlockModel = model;


        if (categoriesModels == null || categoriesModels.isEmpty()){
            return;
        }

        if (binding == null){
            return;
        }
        setupPadding();

        hideAndShowTitle(model,binding.userTitle,model.getTitle());
        hideAndShowTitle(model,binding.description,model.getDescription());

        activity.runOnUiThread(() -> categoryListAdapter.updateData(categoriesModels));

    }

    public void setUpData(Activity activity, String categoryType, HomeBlockModel model, List<CategoriesModel> categoriesModels,boolean isFromHomeBlock){
        this.activity = activity;
        this.categoriesList = categoriesModels;
        this.categoryType = categoryType;
        this.homeBlockModel = model;
        this.isFromHomeBlock = isFromHomeBlock;

        if (categoriesModels == null || categoriesModels.isEmpty()){
            return;
        }

        if (binding == null){
            return;
        }

        setupPadding();

        hideAndShowTitle(model,binding.userTitle,model.getTitle());
        hideAndShowTitle(model,binding.description,model.getDescription());

        activity.runOnUiThread(() -> categoryListAdapter.updateData(categoriesModels));

    }

    private void setupPadding() {
        if (!isSetBottomPaddingForRecView) return;
        int spacing = getResources().getDimensionPixelSize(com.intuit.ssp.R.dimen._10ssp);
        binding.categoriesRecycler.setClipToPadding(false);
        binding.categoriesRecycler.setPadding(0,0,0,spacing);
    }

    private void hideAndShowTitle(ExploreBlockModel model, TextView textView, String title) {
        if (model.isShowTitle() && !TextUtils.isEmpty(title)) {
            textView.setText(title);
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);
        }
    }

    private void hideAndShowTitle(HomeBlockModel model, TextView textView, String title) {
        if (model.isShowTitle() && !TextUtils.isEmpty(title)) {
            textView.setText(title);
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);
        }
    }


    public class RaynaCategoryListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        private static final int VIEW_TYPE_RECTANGLE = 0;

        private static final int VIEW_TYPE_CIRCLE = 1;

        private static final int VIEW_TYPE_SQUARE = 2;


        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            switch (viewType) {
                case VIEW_TYPE_RECTANGLE:
                    View view = UiUtils.getViewBy(parent, R.layout.item_new_rayna_categories);
                    view.getLayoutParams().width = (int) (Graphics.getScreenWidth(activity) * 0.40);
                    return new RectangleCategoryHolder(view);
                case VIEW_TYPE_CIRCLE:
                    View view2 = UiUtils.getViewBy(parent, R.layout.item_category_circle);
                    double tmpWidth = 0.30;
                    view2.getLayoutParams().width = (int) (Graphics.getScreenWidth(activity) * tmpWidth);
                    return new CircleCategoryHolder(view2);
                case VIEW_TYPE_SQUARE:
                    View view3 = UiUtils.getViewBy(parent, R.layout.item_new_explore_category_item);
                    double tmpWidth2 = 0.32;
                    view3.getLayoutParams().width = (int) (Graphics.getScreenWidth(activity) * tmpWidth2);
                    return new SquareCategoryHolder(view3);
                default:
                    View view4 = UiUtils.getViewBy(parent, R.layout.item_new_explore_category_item);
                    double tmpWidth3 = 0.32;
                    view4.getLayoutParams().width = (int) (Graphics.getScreenWidth(activity) * tmpWidth3);
                    return new SquareCategoryHolder(view4);
            }

        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            CategoriesModel model = (CategoriesModel) getItem(position);
            switch (getItemViewType(position)) {
                case VIEW_TYPE_RECTANGLE:
                    ((RectangleCategoryHolder) holder).setupData(model);
                    break;
                case VIEW_TYPE_CIRCLE:
                    ((CircleCategoryHolder) holder).setupData(model);
                    break;
                default:
                    ((SquareCategoryHolder) holder).setupData(model);
                    break;
            }

        }


        public int getItemViewType(int position) {
            switch (categoryType) {
                case "rectangular":
                    return VIEW_TYPE_RECTANGLE;
                case "rounded":
                    return VIEW_TYPE_CIRCLE;
                case "square":
                    return VIEW_TYPE_SQUARE;
                default:
                    return VIEW_TYPE_CIRCLE;
            }
//            return super.getItemViewType(position);
        }

        public class RectangleCategoryHolder extends RecyclerView.ViewHolder {

            private final ItemNewRaynaCategoriesBinding mBinding;

            public RectangleCategoryHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemNewRaynaCategoriesBinding.bind(itemView);

            }

            @SuppressLint("SetTextI18n")
            public void setupData(CategoriesModel model) {

                mBinding.txtCategories.setText(model.getTitle());

                if (!TextUtils.isEmpty(model.getImage())){
                    Graphics.loadImage(model.getImage(), mBinding.bgImageView);
                }else {
                    if (model.getColor() != null && !TextUtils.isEmpty(model.getColor().getStartColor()) && !TextUtils.isEmpty(model.getColor().getEndColor())) {
                        setGradientBackground(mBinding.bgImageView, model.getColor().getStartColor(), model.getColor().getEndColor());
                    }
                }

                mBinding.getRoot().setOnClickListener(view -> {
                    Intent intent = new Intent(activity, ExploreDetailActivity.class);
                    intent.putExtra("categoryModel", new Gson().toJson(model));
                    intent.putExtra("isCity", false);
                    intent.putExtra("isFromHomeBlock", isFromHomeBlock);
                    activity.startActivity(intent);
                });
            }


            private void setGradientBackground(View view, String startColor, String endColor) {
                try {
                    int startColorInt = Color.parseColor(startColor);
                    int endColorInt = Color.parseColor(endColor);

                    GradientDrawable gradientDrawable = new GradientDrawable(
                            GradientDrawable.Orientation.LEFT_RIGHT,
                            new int[]{startColorInt, endColorInt}
                    );

                    view.setBackground(gradientDrawable);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }

        }

        public class CircleCategoryHolder extends RecyclerView.ViewHolder {

            private final ItemCategoryCircleBinding mBinding;

            public CircleCategoryHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemCategoryCircleBinding.bind(itemView);

            }

            public void setupData(CategoriesModel model) {

                mBinding.tvTitle.setText(model.getTitle());

                if (!TextUtils.isEmpty(model.getImage())) {
                    Graphics.loadImage(model.getImage(), mBinding.image);
                }

                mBinding.getRoot().setOnClickListener(view -> {
                    Intent intent = new Intent(activity, ExploreDetailActivity.class);
                    intent.putExtra("categoryModel", new Gson().toJson(model));
                    intent.putExtra("isCity", false);
                    intent.putExtra("isFromHomeBlock", isFromHomeBlock);
                    activity.startActivity(intent);
                });
            }
        }

        public class SquareCategoryHolder extends RecyclerView.ViewHolder {

            private final ItemNewExploreCategoryItemBinding mBinding;

            public SquareCategoryHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemNewExploreCategoryItemBinding.bind(itemView);
            }

            public void setupData(CategoriesModel model) {

                mBinding.tvTitle.setText(model.getTitle());

                if (!TextUtils.isEmpty(model.getImage())) {
                    Graphics.loadImage(model.getImage(), mBinding.image);
                }

                mBinding.getRoot().setOnClickListener(view -> {
                    Intent intent = new Intent(activity, ExploreDetailActivity.class);
                    intent.putExtra("categoryModel", new Gson().toJson(model));
                    intent.putExtra("isCity", false);
                    intent.putExtra("isFromHomeBlock", isFromHomeBlock);
                    activity.startActivity(intent);
                });
            }
        }


    }


}
