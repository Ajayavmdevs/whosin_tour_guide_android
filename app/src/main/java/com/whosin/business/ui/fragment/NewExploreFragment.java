package com.whosin.business.ui.fragment;

import static com.whosin.business.comman.AppDelegate.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.whosin.business.R;
import com.whosin.business.comman.AppConstants;
import com.whosin.business.comman.DiffAdapter;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.ExploreFilterManager;
import com.whosin.business.comman.Graphics;
import com.whosin.business.comman.HorizontalSpaceItemDecoration;
import com.whosin.business.comman.Utils;
import com.whosin.business.comman.ui.ContactUsBlockManager;
import com.whosin.business.comman.ui.UiUtils;
import com.whosin.business.databinding.FragmentNewExploreBinding;
import com.whosin.business.databinding.ItemHomeAdViewBinding;
import com.whosin.business.databinding.ItemLayoutEmptyHolderBinding;
import com.whosin.business.databinding.ItemNewCategoryShapeBinding;
import com.whosin.business.databinding.ItemNewExploreBigCategoryDesignBinding;
import com.whosin.business.databinding.ItemNewExploreCitiesRecycleBinding;
import com.whosin.business.databinding.ItemNewExploreCityBinding;
import com.whosin.business.databinding.ItemNewExploreCustomComponentRecycleBinding;
import com.whosin.business.databinding.ItemTicketNewExploreRecycleViewBinding;
import com.whosin.business.service.DataService;
import com.whosin.business.service.manager.AppSettingManager;
import com.whosin.business.service.manager.RaynaTicketManager;
import com.whosin.business.service.manager.SessionManager;
import com.whosin.business.service.models.BannerModel;
import com.whosin.business.service.models.CategoriesModel;
import com.whosin.business.service.models.ContactUsBlockModel;
import com.whosin.business.service.models.ContainerModel;
import com.whosin.business.service.models.SizeModel;
import com.whosin.business.service.models.newExploreModels.ExploreBlockModel;
import com.whosin.business.service.models.newExploreModels.ExploreObjectModel;
import com.whosin.business.service.models.rayna.RaynaTicketDetailModel;
import com.whosin.business.service.rest.RestCallback;
import com.whosin.business.ui.activites.explore.ExploreDetailActivity;
import com.whosin.business.ui.activites.explore.ExploreTicketActivity;
import com.whosin.business.ui.activites.explore.NewExploreFilterBottomSheet;
import com.whosin.business.ui.activites.raynaTicket.RaynaTicketDetailActivity;
import com.whosin.business.ui.activites.raynaTicket.RaynaTicketListActivity;
import com.whosin.business.ui.fragment.comman.BaseFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class NewExploreFragment extends BaseFragment {

    private FragmentNewExploreBinding binding;

    private final ExploreAdapter<ExploreBlockModel> exploreAdapter = new ExploreAdapter<>();

    private List<ExploreBlockModel> exploreBlockList = new ArrayList<>();

    private Fragment fragment;
    private boolean isApiCalled = false;


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------


    @Override
    public void initUi(View view) {
        binding = FragmentNewExploreBinding.bind(view);

        binding.tvExploreTitle.setText(getValue("explore"));
        binding.edtSearch.setHint(getValue("explore"));
        binding.emptyPlaceHolderView.setEmptyPlaceTxtTitle(getValue("empty_explore_page"));

        fragment = this;

        Graphics.applyBlurEffect(activity,binding.blurView);

        binding.newExploreRecyleView.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false));
        binding.newExploreRecyleView.setAdapter(exploreAdapter);

//        requestNewExplore(true);

    }

    @Override
    public void setListeners() {

        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            binding.swipeRefreshLayout.setRefreshing(true);
            requestNewExplore(false);
        });

        binding.serachLayout.setOnClickListener(v -> {
            startActivity(new Intent(requireActivity(),ExploreTicketActivity.class));
            activity.overridePendingTransition(0, 0);
        });


        binding.mainSearchLayout.setOnClickListener(v -> {
            startActivity(new Intent(requireActivity(),ExploreTicketActivity.class));
            activity.overridePendingTransition(0, 0);
        });


        binding.imgFilter.setOnClickListener(v -> {
            Utils.preventDoubleClick(v);
            NewExploreFilterBottomSheet dialog = new NewExploreFilterBottomSheet();
            dialog.callback = data -> {
                AppSettingManager.shared.filterList.clear();
                AppSettingManager.shared.filterList = data;
                startActivity(new Intent(requireActivity(),ExploreTicketActivity.class));
                activity.overridePendingTransition(0, 0);
            };
            dialog.show(getChildFragmentManager(), "");
        });

        RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (layoutManager != null) {
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                    int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                    if (lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == exploreAdapter.getData().size() - 1 && (exploreBlockList.size() != exploreAdapter.getData().size())) {
                        // loadData();
                    }

                    for (int i = firstVisibleItemPosition; i <= lastVisibleItemPosition; i++) {
                        RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(i);
                        if (viewHolder instanceof ExploreAdapter.AsyncVideoViewHolder) {
                            View itemView = layoutManager.findViewByPosition(i);
                            if (itemView != null) {
                                int itemHeight = itemView.getHeight();
                                int visibleHeight = Math.min(recyclerView.getHeight(), itemView.getBottom()) - Math.max(0, itemView.getTop());
                                double visiblePercentage = (double) visibleHeight / itemHeight;

                                ExploreAdapter.AsyncVideoViewHolder newVideoViewHolder = (ExploreAdapter.AsyncVideoViewHolder) viewHolder;
                                if (newVideoViewHolder.isFromVideo()) newVideoViewHolder.onItemVisibilityChanged(visiblePercentage >= 0.7);
                            }
                        } else if (viewHolder instanceof  ExploreAdapter.HomeAdHolder) {
                            View itemView = layoutManager.findViewByPosition(i);
                            if (itemView != null) {
                                int itemHeight = itemView.getHeight();
                                int visibleHeight = Math.min(recyclerView.getHeight(), itemView.getBottom()) - Math.max(0, itemView.getTop());
                                double visiblePercentage = (double) visibleHeight / itemHeight;

                                ExploreAdapter.HomeAdHolder newVideoViewHolder = (ExploreAdapter.HomeAdHolder) viewHolder;
                                newVideoViewHolder.onItemVisibilityChanged(visiblePercentage >= 0.7);
                            }
                        }
                    }
                }
            }
        };

        binding.newExploreRecyleView.addOnScrollListener(scrollListener);


        binding.edtSearch.setOnClickListener(v -> {
            playPauseVideos(false);
            startActivity(new Intent(requireActivity(), ExploreTicketActivity.class));
            activity.overridePendingTransition(0, 0);
        });

        binding.serachLayout.setOnClickListener(v -> {
            playPauseVideos(false);
            startActivity(new Intent(requireActivity(), ExploreTicketActivity.class));
            activity.overridePendingTransition(0, 0);
        });

    }

    @Override
    public void populateData(boolean getDataFromServer) {

    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_new_explore;
    }

    @Override
    public void onPause() {
        super.onPause();
        playPauseVideos(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        playPauseVideos(true);
        if (!isApiCalled) {
            requestNewExplore(true);
            isApiCalled = true;
        } else {
            requestNewExplore(false);
        }

    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void setupRecycleHorizontalManager(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false));
        int spacing = getResources().getDimensionPixelSize(com.intuit.ssp.R.dimen._10ssp);
        recyclerView.addItemDecoration(new HorizontalSpaceItemDecoration(spacing));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        recyclerView.offsetChildrenHorizontal(1);
    }

    private void setHomeBlock(ExploreObjectModel model) {
        if (model != null) {
            new ExploreFilterManager().filterInBackground(model.getBlocks(), data -> {
                if (!data.isEmpty()) {
                    exploreBlockList = data;
                    loadData();
                }
            });
        }
    }

    private void loadData() {
        if (getActivity() == null) {
            return;
        }
        if (exploreBlockList != null && exploreBlockList.size() > 3) {
            ExploreBlockModel completeProfileBanner = new ExploreBlockModel();
            completeProfileBanner.setType(AppConstants.ADTYPE);

            int insertIndex = Math.min(exploreBlockList.size(), 5);

            exploreBlockList.add(insertIndex, completeProfileBanner);
        }
        getActivity().runOnUiThread(() -> exploreAdapter.updateData(exploreBlockList));
    }

    private void hideAndShowTitle(ExploreBlockModel model, TextView textView, String title) {
        if (model.isShowTitle() && !TextUtils.isEmpty(title)) {
            textView.setText(title);
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);
        }
    }


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestNewExplore(boolean showLoader) {
        if (showLoader) {
            showProgress();
        }
        DataService.shared(context).requestNewExplore(new RestCallback<ContainerModel<ExploreObjectModel>>(this) {
            @Override
            public void result(ContainerModel<ExploreObjectModel> model, String error) {
                hideProgress();
                binding.swipeRefreshLayout.setRefreshing(false);
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                    Log.d("requestNewExplore", "result: " + error);
                    return;
                }

                if (model.getData() != null) {
                    activity.runOnUiThread(() -> SessionManager.shared.saveExploreBlockData(model.getData()));
                    setHomeBlock(model.getData());
                }

            }
        });
    }

    private void playPauseVideos(boolean isPlay) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) binding.newExploreRecyleView.getLayoutManager();
        if (layoutManager != null) {
            int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
            int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
            if (lastVisibleItemPosition != RecyclerView.NO_POSITION) {
                exploreAdapter.getData().size();
            }

            for (int i = firstVisibleItemPosition; i <= lastVisibleItemPosition; i++) {
                RecyclerView.ViewHolder viewHolder = binding.newExploreRecyleView.findViewHolderForAdapterPosition(i);
                if (viewHolder instanceof ExploreAdapter.AsyncVideoViewHolder) {
                    ExploreAdapter.AsyncVideoViewHolder newVideoViewHolder = (ExploreAdapter.AsyncVideoViewHolder) viewHolder;
                    if (isPlay) {
                        View itemView = layoutManager.findViewByPosition(i);
                        if (itemView != null) {
                            int itemHeight = itemView.getHeight();
                            int visibleHeight = Math.min(binding.newExploreRecyleView.getHeight(), itemView.getBottom()) - Math.max(0, itemView.getTop());
                            double visiblePercentage = (double) visibleHeight / itemHeight;
                            if (visiblePercentage >= 0.6) {
                                if (newVideoViewHolder.isFromVideo()) newVideoViewHolder.onItemVisibilityChanged(true);
                            }
                        }
                    } else {
                        if (newVideoViewHolder.isFromVideo())  newVideoViewHolder.onItemVisibilityChanged(false);
                    }
                } else if (viewHolder instanceof ExploreAdapter.HomeAdHolder) {
                    ExploreAdapter.HomeAdHolder newVideoViewHolder = (ExploreAdapter.HomeAdHolder) viewHolder;
                    if (isPlay) {
                        View itemView = layoutManager.findViewByPosition(i);
                        if (itemView != null) {
                            int itemHeight = itemView.getHeight();
                            int visibleHeight = Math.min(binding.newExploreRecyleView.getHeight(), itemView.getBottom()) - Math.max(0, itemView.getTop());
                            double visiblePercentage = (double) visibleHeight / itemHeight;
                            if (visiblePercentage >= 0.6) {
                                newVideoViewHolder.onItemVisibilityChanged(true);
                            }
                        }
                    } else {
                        newVideoViewHolder.onItemVisibilityChanged(false);
                    }
                }
            }
        }
    }

    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------


    private class ExploreAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return switch (Objects.requireNonNull(AppConstants.ExploreBlockType.valueOf(viewType))) {
                case TICKET, JUNIPER_HOTEL ->
                        new TicketHolder(UiUtils.getViewBy(parent, R.layout.item_ticket_new_explore_recycle_view));
                case CITY ->
                        new CitiesHolder(UiUtils.getViewBy(parent, R.layout.item_new_explore_cities_recycle));
                case CATEGORY ->
                        new CategoriesHolder(UiUtils.getViewBy(parent, R.layout.item_new_category_shape));
                case BIG_CATEGORY ->
                        new BigCategoriesHolder(UiUtils.getViewBy(parent, R.layout.item_new_explore_cities_recycle));
                case SMALL_CATEGORY ->
                        new SmallCategoriesHolder(UiUtils.getViewBy(parent, R.layout.item_new_explore_cities_recycle));
                case BANNER ->
                        new BannersHolder(UiUtils.getViewBy(parent, R.layout.item_new_explore_cities_recycle));
                case CUSTOM_COMPONENT ->
                        new AsyncVideoViewHolder(UiUtils.getViewBy(parent, R.layout.item_new_explore_custom_component_recycle));
                case HOME_AD ->
                        new HomeAdHolder(UiUtils.getViewBy(parent, R.layout.item_home_ad_view));
                case CONTACT_US ->
                        new ContactUsHolder(UiUtils.getViewBy(parent, R.layout.item_contact_us_block));
                default ->
                        new EmptyHolder(UiUtils.getViewBy(parent, R.layout.item_layout_empty_holder));
            };
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ExploreBlockModel model = (ExploreBlockModel) getItem(position);

            if (model.getBlockType() == AppConstants.ExploreBlockType.TICKET || model.getBlockType() == AppConstants.ExploreBlockType.JUNIPER_HOTEL) {
                TicketHolder viewHolder = (TicketHolder) holder;
                hideAndShowTitle(model, viewHolder.binding.userTitle, model.getTitle());
                hideAndShowTitle(model, viewHolder.binding.description, model.getDescription());
                viewHolder.setupData(model.getTicketList(),model.getType());
            } else if (model.getBlockType() == AppConstants.ExploreBlockType.CITY) {
                CitiesHolder viewHolder = (CitiesHolder) holder;
                hideAndShowTitle(model, viewHolder.mBinding.userTitle, model.getTitle());
                hideAndShowTitle(model, viewHolder.mBinding.description, model.getDescription());
                viewHolder.setupData(model.citiesList);
            } else if (model.getBlockType() == AppConstants.ExploreBlockType.CATEGORY) {
                CategoriesHolder viewHolder = (CategoriesHolder) holder;
                viewHolder.setupData(model);
            } else if (model.getBlockType() == AppConstants.ExploreBlockType.BIG_CATEGORY) {
                BigCategoriesHolder viewHolder = (BigCategoriesHolder) holder;
                hideAndShowTitle(model, viewHolder.mBinding.userTitle, model.getTitle());
                hideAndShowTitle(model, viewHolder.mBinding.description, model.getDescription());
                viewHolder.setupData(model.bigCategoryList, model.getSize());
            } else if (model.getBlockType() == AppConstants.ExploreBlockType.SMALL_CATEGORY) {
                SmallCategoriesHolder viewHolder = (SmallCategoriesHolder) holder;
                hideAndShowTitle(model, viewHolder.mBinding.userTitle, model.getTitle());
                hideAndShowTitle(model, viewHolder.mBinding.description, model.getDescription());
                viewHolder.setupData(model.smallCategoryList, model.getSize());
            } else if (model.getBlockType() == AppConstants.ExploreBlockType.BANNER) {
                BannersHolder viewHolder = (BannersHolder) holder;
                hideAndShowTitle(model, viewHolder.mBinding.userTitle, model.getTitle());
                hideAndShowTitle(model, viewHolder.mBinding.description, model.getDescription());
                viewHolder.setupData(model.bannerList, model.getSize());
            } else if (model.getBlockType() == AppConstants.ExploreBlockType.CUSTOM_COMPONENT) {
                AsyncVideoViewHolder viewHolder = (AsyncVideoViewHolder) holder;
                hideAndShowTitle(model, viewHolder.binding.userTitle, model.getTitle());
                hideAndShowTitle(model, viewHolder.binding.description, model.getDescription());
                viewHolder.binding.videoControl.setupData(model.customComponentModelList, holder.itemView, getActivity());
                viewHolder.setRatio(model);
                String type = model.customComponentModelList.get(0).getMediaType();
                viewHolder.binding.videoControl.hideLayouts(!type.equals("video"));
            }else if (model.getBlockType() == AppConstants.ExploreBlockType.HOME_AD) {
                HomeAdHolder viewHolder = (HomeAdHolder) holder;
                viewHolder.setupData();
            } else if (model.getBlockType() == AppConstants.ExploreBlockType.CONTACT_US) {
                ContactUsHolder viewHolder = (ContactUsHolder) holder;
                hideAndShowTitle(model, viewHolder.binding.userTitle, model.getTitle());
                hideAndShowTitle(model, viewHolder.binding.description, model.getDescription());
                viewHolder.setupData(model);
            }
        }

        @Override
        public int getItemViewType(int position) {
            ExploreBlockModel model = (ExploreBlockModel) getItem(position);
            return model.getBlockType().getValue();
        }

        @Override
        public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
            if (holder instanceof ExploreAdapter.AsyncVideoViewHolder) {
                ((AsyncVideoViewHolder) holder).binding.videoControl.releasePlayer();
            }
            super.onViewRecycled(holder);
        }


        public class TicketHolder extends RecyclerView.ViewHolder {

            private final ItemTicketNewExploreRecycleViewBinding binding;

            public TicketHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemTicketNewExploreRecycleViewBinding.bind(itemView);
            }

            public void setupData(List<RaynaTicketDetailModel> ticket,String type) {
                requireActivity().runOnUiThread(() -> {
                    binding.ticketRecyclerView.isVertical = false;
                    binding.ticketRecyclerView.activity = activity;
                    binding.ticketRecyclerView.setupData(ticket, requireActivity(), false,false);
                    binding.seeAll.setOnClickListener(v -> {
                        RaynaTicketManager.shared.raynaTicketList.clear();
                        Utils.preventDoubleClick(v);
                        RaynaTicketManager.shared.raynaTicketList.addAll(ticket);
                        Intent intent = new Intent(activity, RaynaTicketListActivity.class);
                        intent.putExtra("Description", binding.userTitle.getText().toString());
                        intent.putExtra("type", type);
                        activity.startActivity(intent);
                    });
                });
            }
        }

        public class EmptyHolder extends RecyclerView.ViewHolder {

            private final ItemLayoutEmptyHolderBinding binding;

            public EmptyHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemLayoutEmptyHolderBinding.bind(itemView);
            }
        }

        public class CitiesHolder extends RecyclerView.ViewHolder {

            private final ItemNewExploreCitiesRecycleBinding mBinding;

            private final CitiesAdapter<CategoriesModel> adapter = new CitiesAdapter<>();

            public CitiesHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemNewExploreCitiesRecycleBinding.bind(itemView);
                requireActivity().runOnUiThread(() -> {
                    setupRecycleHorizontalManager(mBinding.categoriesRecycler);
                    mBinding.categoriesRecycler.setNestedScrollingEnabled(true);
                    mBinding.categoriesRecycler.setAdapter(adapter);
                });
            }

            public void setupData(List<CategoriesModel> citiesList) {
                requireActivity().runOnUiThread(() -> adapter.updateData(citiesList));
            }
        }

        public class CategoriesHolder extends RecyclerView.ViewHolder {

            private final ItemNewCategoryShapeBinding mBinding;


            public CategoriesHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemNewCategoryShapeBinding.bind(itemView);

            }

            public void setupData(ExploreBlockModel model) {
//                mBinding.categoryView.categoryType = model.getShape();
               requireActivity().runOnUiThread(() -> mBinding.categoryView.setUpData(requireActivity(),model.getShape(),model,model.categoryList));
            }
        }

        public class BigCategoriesHolder extends RecyclerView.ViewHolder {

            private final ItemNewExploreCitiesRecycleBinding mBinding;

            private BigCategoryAdapter<BannerModel> adapter;

            public BigCategoriesHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemNewExploreCitiesRecycleBinding.bind(itemView);

            }

            public void setupData(List<BannerModel> bannerModels, SizeModel model) {
                requireActivity().runOnUiThread(() -> {
                    if (adapter == null) {
                        adapter = new BigCategoryAdapter<>(model);
                        setupRecycleHorizontalManager(mBinding.categoriesRecycler);
                        mBinding.categoriesRecycler.setNestedScrollingEnabled(false);
                        mBinding.categoriesRecycler.setHasFixedSize(true);
                        mBinding.categoriesRecycler.setAdapter(adapter);
                    }
                    adapter.updateData(bannerModels);
                });
            }

        }

        public class SmallCategoriesHolder extends RecyclerView.ViewHolder {

            private final ItemNewExploreCitiesRecycleBinding mBinding;

            private BigCategoryAdapter<BannerModel> adapter;

            public SmallCategoriesHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemNewExploreCitiesRecycleBinding.bind(itemView);

            }

            public void setupData(List<BannerModel> bannerModels, SizeModel model) {
                requireActivity().runOnUiThread(() -> {
                    if (adapter == null) {
                        adapter = new BigCategoryAdapter<>(model);
                        setupRecycleHorizontalManager(mBinding.categoriesRecycler);
                        mBinding.categoriesRecycler.setNestedScrollingEnabled(false);
                        mBinding.categoriesRecycler.setHasFixedSize(true);
                        mBinding.categoriesRecycler.setAdapter(adapter);
                    }
                    adapter.updateData(bannerModels);
                });
            }

        }

        public class BannersHolder extends RecyclerView.ViewHolder {

            private final ItemNewExploreCitiesRecycleBinding mBinding;

            private BigCategoryAdapter<BannerModel> adapter;

            public BannersHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemNewExploreCitiesRecycleBinding.bind(itemView);

            }

            public void setupData(List<BannerModel> bannerModels, SizeModel model) {
                requireActivity().runOnUiThread(() -> {
                    if (adapter == null) {
                        adapter = new BigCategoryAdapter<>(model);
                        setupRecycleHorizontalManager(mBinding.categoriesRecycler);
                        mBinding.categoriesRecycler.setNestedScrollingEnabled(false);
                        mBinding.categoriesRecycler.setHasFixedSize(true);
                        mBinding.categoriesRecycler.setAdapter(adapter);
                    }
                    adapter.updateData(bannerModels);
                });
            }

        }

        public class ContactUsHolder extends RecyclerView.ViewHolder {

            private final com.whosin.business.databinding.ItemContactUsBlockBinding binding;

            public ContactUsHolder(@NonNull View itemView) {
                super(itemView);
                binding = com.whosin.business.databinding.ItemContactUsBlockBinding.bind(itemView);
            }

            public void setupData(ExploreBlockModel exploreBlockModel) {
                ContactUsBlockManager.setupContactUsBlock(
                        requireContext(),
                        binding,
                        exploreBlockModel.getContactUsBlock().get(0),
                        ContactUsBlockModel.ContactBlockScreens.EXPLORE
                );
            }
        }

        public class AsyncVideoViewHolder extends RecyclerView.ViewHolder {

            private ItemNewExploreCustomComponentRecycleBinding binding;

            public AsyncVideoViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemNewExploreCustomComponentRecycleBinding.bind(itemView);
            }

            public void onItemVisibilityChanged(boolean isVisible) {
                binding.videoControl.onItemVisibilityChanged(isVisible);
            }

            public boolean isFromVideo(){
               return binding.videoControl.isVideo();
            }

            public void setRatio(ExploreBlockModel model) {
                String ratio = "16:9";


                if (model.getSize() != null && model.getSize().getRatio() != null) {
                    ratio = model.getSize().getRatio();
                }

                if (!ratio.contains(":")) {
                    ratio = "16:9";
                }

                try {
                    String[] parts = ratio.split(":");
                    float widthRatio = Float.parseFloat(parts[0]);
                    float heightRatio = Float.parseFloat(parts[1]);

                    // Get screen width
                    int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;

                    // Calculate height based on ratio
                    int calculatedHeight = (int) (screenWidth * (heightRatio / widthRatio));

                    // Apply new height to videoControl
                    ViewGroup.LayoutParams params = binding.videoControl.getLayoutParams();
                    params.height = calculatedHeight;
                    binding.videoControl.setLayoutParams(params);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }




        }

        public class HomeAdHolder extends RecyclerView.ViewHolder {

            private final ItemHomeAdViewBinding mBinding;

            public HomeAdHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemHomeAdViewBinding.bind(itemView);

            }

            public void setupData() {
                requireActivity().runOnUiThread(() -> {
                    mBinding.adView.activity = activity;
                    mBinding.adView.seUpData(activity,fragment);
                });
            }

            public void onItemVisibilityChanged(boolean isVisible) {
                mBinding.adView.onItemVisibilityChanged(isVisible);
            }

        }

    }


    // endregion
    // --------------------------------------
    // region Sub Adapter
    // --------------------------------------

    private class CitiesAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = UiUtils.getViewBy(parent, R.layout.item_new_explore_city);
            view.getLayoutParams().width = (int) (Graphics.getScreenWidth(activity) * 0.35);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            CategoriesModel model = (CategoriesModel) getItem(position);
            viewHolder.mBinding.txtCategories.setText(model.getName());


            if (model.getColor() != null && !TextUtils.isEmpty(model.getColor().getStartColor()) && !TextUtils.isEmpty(model.getColor().getEndColor())) {
                viewHolder.setGradientBackground(viewHolder.mBinding.bgImageView,model.getColor().getStartColor(),model.getColor().getEndColor());
            } else {
                Graphics.loadImage(model.getImage(), viewHolder.mBinding.bgImageView);
            }

            viewHolder.itemView.setOnClickListener(view -> {
                Intent intent = new Intent(requireActivity(), ExploreDetailActivity.class);
                intent.putExtra("categoryModel", new Gson().toJson(model));
                intent.putExtra("isCity", true);
                startActivity(intent);
            });
         }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final ItemNewExploreCityBinding mBinding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemNewExploreCityBinding.bind(itemView);
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
    }

    public class BigCategoryAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        private SizeModel sizeModel;

        public BigCategoryAdapter(SizeModel model) {
            this.sizeModel = model;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = UiUtils.getViewBy(parent, R.layout.item_new_explore_big_category_design);
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.width = (int) (Graphics.getScreenWidth(activity) * (getItemCount() > 1 ? 0.89 : 0.93));
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            view.setLayoutParams(params);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            BannerModel model = (BannerModel) getItem(position);

            // Calculate item width
            int itemWidth = (int) (Graphics.getScreenWidth(activity) * (getItemCount() > 1 ? 0.89 : 0.93));

            // Set itemView width
            ViewGroup.LayoutParams layoutParams = viewHolder.itemView.getLayoutParams();
            if (layoutParams == null) {
                layoutParams = new ViewGroup.LayoutParams(itemWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
            }
            layoutParams.width = itemWidth;
            viewHolder.itemView.setLayoutParams(layoutParams);

            // Default ratio
//            String ratio = "16:9";
            String ratio = "1:1";
            if (sizeModel != null && !TextUtils.isEmpty(sizeModel.getRatio())) {
                ratio = sizeModel.getRatio();
            }

            // Calculate height based on ratio
            try {
                String[] parts = ratio.split(":");
                float widthRatio = Float.parseFloat(parts[0]);
                float heightRatio = Float.parseFloat(parts[1]);

                // Calculate height (height = width * (heightRatio / widthRatio))
                float aspectRatio = heightRatio / widthRatio;
                int calculatedHeight = (int) (itemWidth * aspectRatio);


                // Apply dimensions to ImageView
//                ViewGroup.LayoutParams imageParams = viewHolder.mBinding.ivBigCategory.getLayoutParams();
//                imageParams.width = itemWidth;
//                imageParams.height = calculatedHeight;
//                viewHolder.mBinding.ivBigCategory.setLayoutParams(imageParams);

                viewHolder.mBinding.ivBigCategory.getLayoutParams().height = calculatedHeight;
                viewHolder.mBinding.ivBigCategory.requestLayout();


                // Ensure scaleType is set
                viewHolder.mBinding.ivBigCategory.setScaleType(ImageView.ScaleType.CENTER_CROP);

            } catch (Exception e) {
                int calculatedHeight = (int) (itemWidth * (9f / 16f));
                ViewGroup.LayoutParams imageParams = viewHolder.mBinding.ivBigCategory.getLayoutParams();
                imageParams.width = itemWidth;
                imageParams.height = calculatedHeight;
                viewHolder.mBinding.ivBigCategory.setLayoutParams(imageParams);
                viewHolder.mBinding.ivBigCategory.setScaleType(ImageView.ScaleType.CENTER_CROP);

            }

            viewHolder.mBinding.ivBigCategory.requestLayout();


            // Set text
            viewHolder.mBinding.tvSubTitle.setText(model.getDescription());
            viewHolder.mBinding.tvTitle.setText(model.getTitle());

            // Load image
            String imageUrl = !TextUtils.isEmpty(model.getMedia()) && !Utils.isVideo(model.getMedia())
                    ? model.getMedia()
                    : model.getThumbnail();
            if (!TextUtils.isEmpty(imageUrl)) {
                // Ensure image loading respects dimensions
                Graphics.loadImage(imageUrl, viewHolder.mBinding.ivBigCategory);
            }

            // Click listener
            viewHolder.mBinding.getRoot().setOnClickListener(v -> {
                Utils.preventDoubleClick(v);
                switch (model.getType()) {
                    case "ticket":
                        Intent ticketIntent = new Intent(activity, RaynaTicketDetailActivity.class)
                                .putExtra("ticketId", model.getTypeId());
                        activity.startActivity(ticketIntent);
                        break;
                    case "category":
                    case "small-category":
                    case "big-category":
                        if (SessionManager.shared.geExploreBlockData() != null) {
                            Optional<CategoriesModel> categoriesModel = SessionManager.shared.geExploreBlockData()
                                    .getCategories()
                                    .stream()
                                    .filter(m -> m.getId().equals(model.getTypeId()))
                                    .findFirst();
                            Intent intent = new Intent(activity, ExploreDetailActivity.class);
                            intent.putExtra("isCity", false);
                            if (categoriesModel.isPresent()) {
                                intent.putExtra("categoryModel", new Gson().toJson(categoriesModel.get()));
                            } else {
                                intent.putExtra("title", model.getTitle());
                            }
                            activity.startActivity(intent);
                        }
                        break;
                    case "city":
                        Optional<BannerModel> citiModel = SessionManager.shared.geExploreBlockData()
                                .getBanners()
                                .stream()
                                .filter(m -> m.getId().equals(model.getId()))
                                .findFirst();
                        Intent intent = new Intent(activity, ExploreDetailActivity.class);
                        intent.putExtra("isCity", true);
                        citiModel.ifPresent(bannerModel -> intent.putExtra("categoryModel", new Gson().toJson(bannerModel)));
                        intent.putExtra("title", model.getTitle());
                        activity.startActivity(intent);
                        break;
                    case "contact-us":
                        break;
                }
            });
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final ItemNewExploreBigCategoryDesignBinding mBinding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemNewExploreBigCategoryDesignBinding.bind(itemView);
            }
        }
    }


    // endregion
    // --------------------------------------
}
