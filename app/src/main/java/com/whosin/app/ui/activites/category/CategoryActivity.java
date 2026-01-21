package com.whosin.app.ui.activites.category;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.tapadoo.alerter.Alerter;
import com.whosin.app.R;
import com.whosin.app.comman.AppExecutors;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Preferences;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.BooleanResult;
import com.whosin.app.comman.ui.PreCachingLayoutManager;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.ActivityCategoryBinding;
import com.whosin.app.databinding.NewItemDesignExclusiveBinding;
import com.whosin.app.databinding.SwipeTagItemBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.models.BannerModel;
import com.whosin.app.service.models.CategoriesModel;
import com.whosin.app.service.models.ContainerListModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.FollowUnfollowModel;
import com.whosin.app.service.models.OffersModel;
import com.whosin.app.service.models.RatingModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.models.VenueObjectModel;
import com.whosin.app.service.models.VoucherModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.comman.BaseActivity;
import com.whosin.app.ui.activites.offers.OfferDetailBottomSheet;
import com.whosin.app.ui.activites.offers.VoucherDetailScreenActivity;
import com.whosin.app.ui.activites.raynaTicket.RaynaTicketDetailActivity;
import com.whosin.app.ui.activites.venue.ui.BuyNowActivity;
import com.whosin.app.ui.adapter.OfferAdapter;
import com.whosin.app.ui.fragment.HomeFragment;

import org.imaginativeworld.whynotimagecarousel.listener.CarouselListener;
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CategoryActivity extends BaseActivity {

    private ActivityCategoryBinding binding;
    private final CategoryDealAdapter<VoucherModel> dealAdapter = new CategoryDealAdapter<>();
    private OfferAdapter<OffersModel> offerAdapter;
    private String categoryId = "";
    private CategoriesModel categoriesModel;
    private int selectedPosition = 0;
    private int lastSelectedPosition = -1;
    private final DaysAdapter<RatingModel> daysAdapter = new DaysAdapter<>();
    private final List<OffersModel> offerList = new ArrayList<>() ;
    private int currentPage = 1;
    private String days = "all";

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    protected void initUi() {
        categoryId = getIntent().getStringExtra("categoryId");
        if (TextUtils.isEmpty(categoryId)) {
            Graphics.showAlertDialogWithOkButton(Graphics.context,"Oops!", "Invalid category id");
            finish();
        }

        Graphics.applyBlurEffect(activity, binding.blurView);
        offerAdapter = new OfferAdapter<>(this, getSupportFragmentManager());

        PreCachingLayoutManager layoutManager = new PreCachingLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setExtraLayoutSpace(Graphics.getScreenHeight(this) * 4);
        binding.offerListRecycler.setLayoutManager(layoutManager);

        binding.offerListRecycler.setAdapter(offerAdapter);


        if (Preferences.shared.isExist("category_" + categoryId)) {
            Thread backgroundThread = new Thread(() -> {
                String categoryJson = Preferences.shared.getString("category_" + categoryId);
                if (!TextUtils.isEmpty(categoryJson)) {
                    categoriesModel = new Gson().fromJson(categoryJson, CategoriesModel.class);
                    AppExecutors.get().mainThread().execute(() -> {
                        setCategoriesModel();
                        dealAdapter.updateData(categoriesModel.getDeals());
                    });
                }
            });
            backgroundThread.start();

        }

        if (Preferences.shared.isExist("category_offer_" + categoryId)) {
            Thread backgroundThread = new Thread(() -> {
                String offerJson = Preferences.shared.getString("category_offer_" + categoryId);
                if (!TextUtils.isEmpty(offerJson)) {
                    Type list = new TypeToken<List<OffersModel>>() {}.getType();
                    List<OffersModel> offers = new Gson().fromJson(offerJson, list);
                    AppExecutors.get().mainThread().execute(() -> {
                        if (!offers.isEmpty()) { hideProgress();}
                        binding.emptyPlaceHolderView.setVisibility(View.GONE);
                        binding.offerListRecycler.setVisibility(View.VISIBLE);
                        offerAdapter.updateData(offers);
                        binding.dayContainer.setVisibility(View.VISIBLE);
                    });
                }
            });
            backgroundThread.start();

        }

        Thread backgroundThread = new Thread(() -> {

        });
        backgroundThread.start();

        setupDaysAdapter();
        reqCategoryDetail(categoryId);
        reqCategoryOfferList("all",false);

    }

    @Override
    protected void setListeners() {

        binding.offerListRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) binding.offerListRecycler.getLayoutManager();

                if (linearLayoutManager != null && linearLayoutManager.findLastVisibleItemPosition() == offerAdapter.getData().size() - 1 && (offerAdapter.getData().size() % 30 == 0)) {
                    currentPage++;
                    reqCategoryOfferList(days,true);
                }
            }
        });

        binding.ivClose.setOnClickListener(v -> {
            onBackPressed();
            finish();
        });
    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityCategoryBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setBackground();
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void setBackground() {
        String categoryImage = getIntent().getStringExtra("image");
        if (TextUtils.isEmpty(categoryImage)) {
            return;
        }
        Glide.with(Graphics.context).asBitmap().load(categoryImage).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                extractColorFromBitmap(resource);
            }
        });
    }

    private  void extractColorFromBitmap(Bitmap bitmap) {
        Palette.from(bitmap).generate(palette -> {
            int dominantColor = palette.getDominantColor(ContextCompat.getColor(Graphics.context, android.R.color.black));
            int lightDominantColor = ColorUtils.blendARGB(dominantColor, 0xFFFFFFFF, 0.2f);
            GradientDrawable gradientDrawable = (GradientDrawable) binding.mainLayout.getBackground();
            int[] colors = gradientDrawable.getColors();
            colors[colors.length - 1] = lightDominantColor;
            gradientDrawable.setColors(colors);
            binding.mainLayout.setBackground(gradientDrawable);
        });
    }

    private void setupDaysAdapter() {
        binding.dayRecycler.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        List<RatingModel> daysList = Arrays.asList("All", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").stream().map(RatingModel::new).collect(Collectors.toList());
        binding.dayRecycler.setAdapter(daysAdapter);
        daysAdapter.updateData(daysList);

    }

    private void setCategoriesModel() {
        binding.tvTitle.setText(categoriesModel.getTitle());
        binding.emptyPlaceHolderView.setEmptyPlaceTxtTitle(setValue("siesta_message",categoriesModel.getTitle()));
    }

    private void openLinkInBrowser(String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        try {
            startActivity(browserIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No browser found", Toast.LENGTH_SHORT).show();
        }
    }

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void reqCategoryDetail(String categoryId) {
        DataService.shared(activity).requestCategoryDetail(categoryId, new RestCallback<ContainerModel<CategoriesModel>>(this) {
            @Override
            public void result(ContainerModel<CategoriesModel> model, String error) {
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    return;
                }
                if (model.getData() != null){
                    categoriesModel = model.getData();
                    Preferences.shared.setString("category_" + categoryId, new Gson().toJson(categoriesModel));
                    setCategoriesModel();
                }
            }
        });

    }

    private void reqCategoryOfferList(String day , boolean showProgressBar) {
        Log.d("TAG", "reqCategoryOfferList: "+day);
        if (offerAdapter.getData().isEmpty()) {
            showProgress();
        } else {
            if (!TextUtils.isEmpty(day)) {
                List<OffersModel> models = ((List<OffersModel>) offerAdapter.getData()).stream().filter(p -> {
                    if (TextUtils.isEmpty(p.getDays())) {
                        return false;
                    }
                    if (p.getDays().equals("All days")) {
                        return true;
                    }
                    return p.getDays().contains(day);
                }).collect(Collectors.toList());
                if (!models.isEmpty()) {
                    offerAdapter.updateData(models);
                }
            }
        }

        if (showProgressBar){
            binding.pagginationProgressBar.setVisibility(View.VISIBLE);
        }
        JsonObject object = new JsonObject();
        object.addProperty("categoryId", categoryId);
        object.addProperty("page", currentPage);
        object.addProperty("limit", 30);
        object.addProperty("day", day.toLowerCase());

        DataService.shared(activity).requestOfferList(object, new RestCallback<ContainerListModel<OffersModel>>(this) {
            @Override
            public void result(ContainerListModel<OffersModel> model, String error) {
                hideProgress();
                binding.pagginationProgressBar.setVisibility(View.GONE);
                binding.dayContainer.setVisibility(View.VISIBLE);
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    if (offerAdapter.getData().isEmpty()) {
                        Toast.makeText(activity, R.string.service_message_something_wrong, Toast.LENGTH_SHORT).show();
                    }
                    return;
                }
                if (model.data != null && !model.data.isEmpty()) {
                    if (day.equals("all") && currentPage == 1) {
                        Preferences.shared.setString("category_offer_" + categoryId, new Gson().toJson(model.data));
                    }
                    if (currentPage == 1) { offerList.clear(); }
                    offerList.addAll(model.data);
                    binding.emptyPlaceHolderView.setVisibility(View.GONE);
                    binding.offerListRecycler.setVisibility(View.VISIBLE);
                    offerAdapter.updateData(offerList);
                    offerAdapter.notifyDataSetChanged();
                } else {
                    if (offerList.isEmpty()){
                        binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
                        binding.offerListRecycler.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    public class DaysAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.select_days_item));

        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            RatingModel ratingModel = (RatingModel) getItem(position);
            viewHolder.mBinding.iconText.setText(ratingModel.getImage());

            viewHolder.mBinding.getRoot().setOnClickListener(view -> {
                currentPage = 1;
                reqCategoryOfferList(ratingModel.getImage(),false);
                days = ratingModel.getImage();
                lastSelectedPosition = selectedPosition;
                selectedPosition = holder.getBindingAdapterPosition();
                notifyItemChanged(lastSelectedPosition);
                notifyItemChanged(selectedPosition);

            });

            if (selectedPosition == holder.getBindingAdapterPosition()) {
                viewHolder.mBinding.linearMainView.setBackground(activity.getResources().getDrawable(R.drawable.swipe_tag_bg));
                currentPage = 1;
            } else {
                viewHolder.mBinding.linearMainView.setBackground(activity.getResources().getDrawable(R.drawable.days_background));
            }

        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private SwipeTagItemBinding mBinding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = SwipeTagItemBinding.bind(itemView);
            }
        }
    }

    private class CategoryDealAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = UiUtils.getViewBy(parent, R.layout.new_item_design_exclusive);
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.width = (int) (Graphics.getScreenWidth(Graphics.context) * (getItemCount() > 1 ? 0.83 : 0.93));
            view.setLayoutParams(params);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            VoucherModel model = (VoucherModel) getItem(position);


            viewHolder.mBinding.roundBlur.isColorTranspart = true;
            viewHolder.mBinding.roundBlur.activity = activity;

            viewHolder.mBinding.tvSubTitle.setText(model.getTitle());
            viewHolder.mBinding.tvTitle.setText(model.getDescription());

            viewHolder.mBinding.venueContainer.setVenueDetail(model.getVenue());

            viewHolder.mBinding.tvAED.setText(String.valueOf(model.getDiscountedPrice() + " AED"));


            viewHolder.mBinding.getRoot().setOnClickListener(view -> {
                startActivity(new Intent(activity, VoucherDetailScreenActivity.class).putExtra("id", model.getId()));
            });

            viewHolder.mBinding.roundLinear.setOnClickListener(view -> startActivity(new Intent(activity,
                    BuyNowActivity.class).putExtra("deals", model.getId()).putExtra("offerModel", new Gson().toJson(model))));

            Graphics.loadImage(model.getImage(), viewHolder.mBinding.cover);

            if (model.getEndDate() != null) {
                viewHolder.mBinding.roundBlur.setVisibility(View.VISIBLE);
                viewHolder.mBinding.roundBlur.setUpData( model.getEndDate(),  model.getEndTime());
            }else {
                viewHolder.mBinding.roundBlur.setVisibility(View.GONE);
            }

        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final NewItemDesignExclusiveBinding mBinding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = NewItemDesignExclusiveBinding.bind(itemView);
            }
        }
    }
    
    // --------------------------------------
    // endregion
}