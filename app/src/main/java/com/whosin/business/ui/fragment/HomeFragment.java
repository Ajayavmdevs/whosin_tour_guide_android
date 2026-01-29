package com.whosin.business.ui.fragment;

import static com.whosin.business.comman.AppDelegate.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.whosin.business.R;
import com.whosin.business.comman.AppConstants;
import com.whosin.business.comman.AppConstants.HomeBlockType;
import com.whosin.business.comman.AppExecutors;
import com.whosin.business.comman.DiffAdapter;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.FilterManager;
import com.whosin.business.comman.Graphics;
import com.whosin.business.comman.HorizontalSpaceItemDecoration;
import com.whosin.business.comman.Utils;
import com.whosin.business.comman.ui.ContactUsBlockManager;
import com.whosin.business.comman.ui.UiUtils;
import com.whosin.business.databinding.FragmentHomeBinding;
import com.whosin.business.databinding.ItemAsyncVideoComponentBinding;
import com.whosin.business.databinding.ItemContactUsBlockBinding;
import com.whosin.business.databinding.ItemHomeAdViewBinding;
import com.whosin.business.databinding.ItemHomeStoryRecyclerBinding;
import com.whosin.business.databinding.ItemLayoutEmptyHolderBinding;
import com.whosin.business.databinding.ItemNewCategoryShapeBinding;
import com.whosin.business.databinding.ItemNewExploreBigCategoryDesignBinding;
import com.whosin.business.databinding.ItemNewExploreCitiesRecycleBinding;
import com.whosin.business.databinding.ItemNewExploreCityBinding;
import com.whosin.business.databinding.ItemNewExploreCustomComponentRecycleBinding;
import com.whosin.business.databinding.ItemStoriesContainerBinding;
import com.whosin.business.databinding.ItemTicketRecyclerBinding;
import com.whosin.business.service.DataService;
import com.whosin.business.service.manager.AppSettingManager;
import com.whosin.business.service.manager.GetNotificationManager;
import com.whosin.business.service.manager.RaynaTicketManager;
import com.whosin.business.service.manager.SessionManager;
import com.whosin.business.service.models.BannerModel;
import com.whosin.business.service.models.CategoriesModel;
import com.whosin.business.service.models.ContactUsBlockModel;
import com.whosin.business.service.models.ContainerModel;
import com.whosin.business.service.models.HomeBlockModel;
import com.whosin.business.service.models.HomeObjectModel;
import com.whosin.business.service.models.MessageEvent;
import com.whosin.business.service.models.SizeModel;
import com.whosin.business.service.models.StoryObjectModel;
import com.whosin.business.service.models.VenueObjectModel;
import com.whosin.business.service.models.myCartModels.MyCartItemsModel;
import com.whosin.business.service.models.myCartModels.MyCartMainModel;
import com.whosin.business.service.models.rayna.RaynaTicketDetailModel;
import com.whosin.business.service.rest.RestCallback;
import com.whosin.business.ui.activites.Notification.NotificaionActivity;
import com.whosin.business.ui.activites.Story.StoryViewActivity;
import com.whosin.business.ui.activites.cartManagement.TicketCartActivity;
import com.whosin.business.ui.activites.explore.ExploreDetailActivity;
import com.whosin.business.ui.activites.home.MainHomeActivity;
import com.whosin.business.ui.activites.raynaTicket.RaynaTicketDetailActivity;
import com.whosin.business.ui.activites.raynaTicket.RaynaTicketListActivity;
import com.whosin.business.ui.activites.search.SearchFragment;
import com.whosin.business.ui.fragment.comman.BaseFragment;
import com.whosin.business.ui.fragment.home.VideoPreCaching;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class HomeFragment extends BaseFragment {
    private FragmentHomeBinding binding;
    private HomeBlockAdapter<HomeBlockModel> homeBlockAdapter;
    private int screenWidth;
    private List<HomeBlockModel> homeBlockList = new ArrayList<>();
    private int limit = 20;
    private Fragment fragment;
    float lastX;
    float lastY;

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    public void initUi(View view) {
        binding = FragmentHomeBinding.bind(view);
        Utils.hideKeyboard(view, requireActivity());
        screenWidth = Graphics.getScreenWidth(Graphics.context);
        fragment = this;

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false);
        binding.mainRecycler.setLayoutManager(layoutManager);
        homeBlockAdapter = new HomeBlockAdapter<>();
        binding.mainRecycler.setAdapter(homeBlockAdapter);
        binding.mainRecycler.setHasFixedSize(true);


        AppSettingManager.shared.reloadHomeFragment = data -> {
            if (data) {
                reqHomeBlocks(false);
            }
        };
    }

    @Override
    public void setListeners() {

        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            binding.swipeRefreshLayout.setRefreshing(true);
            reqHomeBlocks(false);
        });

        binding.navbar.getSettingBtn().setOnClickListener(v -> {
            if (getActivity() instanceof MainHomeActivity) {
                ((MainHomeActivity) getActivity()).openProfileTab();
            }
        });

        binding.navbar.getLinearBtn().setOnClickListener(v -> {
            startActivity(new Intent(requireActivity(), TicketCartActivity.class));
        });

        binding.navbar.getLeftBtn().setOnClickListener(view1 -> startActivity(new Intent(requireActivity(), NotificaionActivity.class)));

        binding.navbar.getSearchBtn().setOnClickListener(view -> startActivity(new Intent(requireActivity(), SearchFragment.class)));

        RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (layoutManager != null) {
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                    int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                    if (lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == homeBlockAdapter.getData().size() - 1 && (homeBlockList.size() != homeBlockAdapter.getData().size())) {
                        limit = limit + 1;
                        // loadData();
                    }

                    for (int i = firstVisibleItemPosition; i <= lastVisibleItemPosition; i++) {
                        RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(i);
                        if (viewHolder instanceof HomeBlockAdapter.AsyncVideoViewHolder) {
                            View itemView = layoutManager.findViewByPosition(i);
                            if (itemView != null) {
                                int itemHeight = itemView.getHeight();
                                int visibleHeight = Math.min(recyclerView.getHeight(), itemView.getBottom()) - Math.max(0, itemView.getTop());
                                double visiblePercentage = (double) visibleHeight / itemHeight;

                                HomeBlockAdapter.AsyncVideoViewHolder newVideoViewHolder = (HomeBlockAdapter.AsyncVideoViewHolder) viewHolder;
                                newVideoViewHolder.onItemVisibilityChanged(visiblePercentage >= 0.7);
                            }
                        } else if (viewHolder instanceof HomeBlockAdapter.HomeAdHolder) {
                            View itemView = layoutManager.findViewByPosition(i);
                            if (itemView != null) {
                                int itemHeight = itemView.getHeight();
                                int visibleHeight = Math.min(recyclerView.getHeight(), itemView.getBottom()) - Math.max(0, itemView.getTop());
                                double visiblePercentage = (double) visibleHeight / itemHeight;

                                HomeBlockAdapter.HomeAdHolder newVideoViewHolder = (HomeBlockAdapter.HomeAdHolder) viewHolder;
                                newVideoViewHolder.onItemVisibilityChanged(visiblePercentage >= 0.7);
                            }
                        }else if (viewHolder instanceof HomeBlockAdapter.CusTomAsyncVideoViewHolder) {
                            View itemView = layoutManager.findViewByPosition(i);
                            if (itemView != null) {
                                int itemHeight = itemView.getHeight();
                                int visibleHeight = Math.min(binding.mainRecycler.getHeight(), itemView.getBottom()) - Math.max(0, itemView.getTop());
                                double visiblePercentage = (double) visibleHeight / itemHeight;

                                HomeBlockAdapter.CusTomAsyncVideoViewHolder newVideoViewHolder = (HomeBlockAdapter.CusTomAsyncVideoViewHolder) viewHolder;
                                if (newVideoViewHolder.isFromVideo()) newVideoViewHolder.onItemVisibilityChanged(visiblePercentage >= 0.7);
                            }
                        }
                    }
                }
            }
        };

        binding.mainRecycler.addOnScrollListener(scrollListener);

        binding.swipeRefreshLayout.setOnTouchListener((v, event) -> {
            int action = event.getActionMasked();
            switch (action) {
                case MotionEvent.ACTION_MOVE:
                    float dx = event.getX() - lastX;
                    float dy = event.getY() - lastY;
                    if (Math.abs(dx) > Math.abs(dy)) {
                        binding.mainRecycler.requestDisallowInterceptTouchEvent(true);
                    } else {
                        binding.mainRecycler.requestDisallowInterceptTouchEvent(false);
                    }
                    break;
                case MotionEvent.ACTION_DOWN:
                    lastX = event.getX();
                    lastY = event.getY();
                    break;
            }
            return false;
        });
    }

    private void playPauseVideos(boolean isPlay) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) binding.mainRecycler.getLayoutManager();
        if (layoutManager != null) {
            int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
            int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
            if (lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == homeBlockAdapter.getData().size() - 1) {
                limit = limit + 1;
//                loadData();
            }

            for (int i = firstVisibleItemPosition; i <= lastVisibleItemPosition; i++) {
                RecyclerView.ViewHolder viewHolder = binding.mainRecycler.findViewHolderForAdapterPosition(i);
                if (viewHolder instanceof HomeBlockAdapter.AsyncVideoViewHolder) {
                    HomeBlockAdapter.AsyncVideoViewHolder newVideoViewHolder = (HomeBlockAdapter.AsyncVideoViewHolder) viewHolder;
                    if (isPlay) {
                        View itemView = layoutManager.findViewByPosition(i);
                        if (itemView != null) {
                            int itemHeight = itemView.getHeight();
                            int visibleHeight = Math.min(binding.mainRecycler.getHeight(), itemView.getBottom()) - Math.max(0, itemView.getTop());
                            double visiblePercentage = (double) visibleHeight / itemHeight;
                            if (visiblePercentage >= 0.6) {
                                newVideoViewHolder.onItemVisibilityChanged(true);
                            }
                        }
                    } else {
                        newVideoViewHolder.onItemVisibilityChanged(false);
                    }
                } else if (viewHolder instanceof HomeBlockAdapter.HomeAdHolder) {
                    HomeBlockAdapter.HomeAdHolder homeAdHolder = (HomeBlockAdapter.HomeAdHolder) viewHolder;
                    if (isPlay) {
                        View itemView = layoutManager.findViewByPosition(i);
                        if (itemView != null) {
                            int itemHeight = itemView.getHeight();
                            int visibleHeight = Math.min(binding.mainRecycler.getHeight(), itemView.getBottom()) - Math.max(0, itemView.getTop());
                            double visiblePercentage = (double) visibleHeight / itemHeight;
                            if (visiblePercentage >= 0.6) {
                                homeAdHolder.onItemVisibilityChanged(true);
                            }
                        }
                    } else {
                        homeAdHolder.onItemVisibilityChanged(false);
                    }
                }else if (viewHolder instanceof HomeBlockAdapter.CusTomAsyncVideoViewHolder) {
                    View itemView = layoutManager.findViewByPosition(i);
                    if (itemView != null) {
                        int itemHeight = itemView.getHeight();
                        int visibleHeight = Math.min(binding.mainRecycler.getHeight(), itemView.getBottom()) - Math.max(0, itemView.getTop());
                        double visiblePercentage = (double) visibleHeight / itemHeight;

                        HomeBlockAdapter.CusTomAsyncVideoViewHolder newVideoViewHolder = (HomeBlockAdapter.CusTomAsyncVideoViewHolder) viewHolder;
                        if (newVideoViewHolder.isFromVideo()) newVideoViewHolder.onItemVisibilityChanged(visiblePercentage >= 0.7);
                    }
                }

            }
        }
    }

    @Override
    public void populateData(boolean getDataFromServer) {
        if (SessionManager.shared.geHomeBlockData() != null) {
            setHomeBlock(SessionManager.shared.geHomeBlockData());
            reqHomeBlocks(false);
        } else {
            reqHomeBlocks(true);
        }
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_home;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        reqHomeBlocks(false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(RaynaTicketDetailModel model) {
        if (model == null || homeBlockAdapter.getData() == null) return;

        List<HomeBlockModel> blocks = homeBlockAdapter.getData();
        Optional<HomeBlockModel> favBlockOpt = blocks.stream()
                .filter(block -> "favorite_ticket".equals(block.getType()))
                .findFirst();

        if (!favBlockOpt.isPresent()) {
            reqHomeBlocks(false);
            return;
        }

        HomeBlockModel favBlock = favBlockOpt.get();
        List<RaynaTicketDetailModel> favList = favBlock.favTicketList;
        if (favList == null) return;

        boolean exists = favList.stream().anyMatch(item -> item.getId().equals(model.getId()));

        if (model.isIs_favorite()) {
            if (!exists) favList.add(model);
        } else {
            favList.removeIf(item -> item.getId().equals(model.getId()));

            // If no favorites remain, remove the block entirely
            if (favList.isEmpty()) {
                blocks.removeIf(block -> "favorite_ticket".equals(block.getType()));
                reqHomeBlocks(false);
                return;
            }
        }

        int position = -1;
        for (int i = 0; i < blocks.size(); i++) {
            if ("favorite_ticket".equals(blocks.get(i).getType())) {
                position = i;
                break;
            }
        }
        if (position != -1) {
            homeBlockAdapter.notifyItemChanged(position);
        }

    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);

        RaynaTicketDetailModel event = EventBus.getDefault().getStickyEvent(RaynaTicketDetailModel.class);
        if (event != null) {
            reqHomeBlocks(false);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        playPauseVideos(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.navbar.setgetGreetingText(Utils.getGreeting());
        Graphics.loadImageWithFirstLetterWithPink(SessionManager.shared.getUser().getImage(), binding.navbar.getSettingBtn(), SessionManager.shared.getUser().getFullName());

        int count = 0;
        MyCartMainModel cartData = SessionManager.shared.geMyCartTicketData();
        if (cartData != null) {
            List<MyCartItemsModel> items = cartData.getItems();
            if (items != null && !items.isEmpty()) {
                count = items.size();
            }
        }

        binding.navbar.setCartItem(String.valueOf(count));
        binding.navbar.setItemCartVisible(count > 0);


        if (homeBlockAdapter.getItemCount() > 0) {
            homeBlockAdapter.notifyItemChanged(0);
        }
        GetNotificationManager.shared.requestCount(binding.navbar, context);
        playPauseVideos(true);
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void startCacheMedia(HomeObjectModel model) {
        List<StoryObjectModel> stories = new ArrayList<>();
        model.getStories().forEach(v -> stories.addAll(v.getStories()));
        List<String> urls = stories.stream().filter(v -> v.getMediaType().equalsIgnoreCase("video")).map(StoryObjectModel::getMediaUrl).collect(Collectors.toList());
        AppExecutors.get().networkIO().execute(() -> VideoPreCaching.shared(context).addItems(urls));

        List<String> imageUrls = stories.stream().filter(v -> v.getMediaType().equalsIgnoreCase("photo")).map(StoryObjectModel::getMediaUrl).collect(Collectors.toList());
        imageUrls.forEach(p -> {
            if (getActivity() != null && !getActivity().isDestroyed()) {
                Glide.with(getActivity()).downloadOnly().load(p).submit();
            }
        });
    }

    private void setHomeBlock(HomeObjectModel model) {
        if (model != null) {
            new FilterManager().filterInBackground(model.getHomeBlocks(), data -> {
                if (!data.isEmpty()) {
                    Optional<HomeBlockModel> ringModel = data.stream()
                            .filter(p -> p != null && p.getType() != null && p.getType().equals("apply-ring"))
                            .findFirst();

                    if (ringModel.isPresent()) {
                        if (ringModel.get().getApplicationStatus() != null && !ringModel.get().getApplicationStatus().isEmpty() && ringModel.get().getApplicationStatus().equals("pending")) {
                            data.removeIf(p -> p != null && p.getType() != null && p.getType().equals("complete-profile"));
                        }
                    }
                    homeBlockList = data;
                    loadData();
                }
            });
        }
    }

    private void loadData() {
        if (getActivity() == null) {
            return;
        }
        if (homeBlockList != null && !homeBlockList.isEmpty()) {
            HomeBlockModel completeProfileBanner = new HomeBlockModel();
            completeProfileBanner.setType(AppConstants.ADTYPE);

            int insertIndex = Math.min(homeBlockList.size(), 5);

            homeBlockList.add(insertIndex, completeProfileBanner);
        }

        AppExecutors.get().mainThread().execute(() -> homeBlockAdapter.updateData(homeBlockList));

//        getActivity().runOnUiThread(() -> homeBlockAdapter.updateData(homeBlockList));
    }

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void reqHomeBlocks(boolean showLoader) {

        if (showLoader) {
            showProgress();
        }
        limit = 20;
        DataService.shared(requireActivity()).requestHomeBlockList(new RestCallback<>( requireActivity()) {
            @Override
            public void result(ContainerModel<HomeObjectModel> model, String error) {
                hideProgress();
                binding.swipeRefreshLayout.setRefreshing(false);
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(requireActivity(), error, Toast.LENGTH_SHORT).show();
                    return;
                }
                AppExecutors.get().mainThread().execute(() -> {
                    if (model.getData() != null) {
                        activity.runOnUiThread(() -> SessionManager.shared.saveHomeBlockData(model.getData()));
                        setHomeBlock(model.getData());
                        startCacheMedia(model.getData());
                    } else {
                        setHomeBlock(SessionManager.shared.geHomeBlockData());
                    }
                });
            }
        });
    }

    private void setupRecycleHorizontalManager(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false));
        int spacing = getResources().getDimensionPixelSize(com.intuit.ssp.R.dimen._10ssp);
        recyclerView.addItemDecoration(new HorizontalSpaceItemDecoration(spacing));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        recyclerView.offsetChildrenHorizontal(1);
    }

    private void hideAndShowTitle(HomeBlockModel model, TextView textView, String title) {
        if (model.isShowTitle() && !TextUtils.isEmpty(title)) {
            textView.setText(title);
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);
        }
    }



    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    public class HomeBlockAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            switch (HomeBlockType.valueOf(viewType)) {
                case VIDEO:
                    return new AsyncVideoViewHolder(UiUtils.getViewBy(parent, R.layout.item_async_video_component));
                case STORIES:
                    return new StoriesContainerHolder(UiUtils.getViewBy(parent, R.layout.item_stories_container));
                case TICKET:
                case JUNIPER_HOTEL:
                    return new TicketHolder(UiUtils.getViewBy(parent, R.layout.item_ticket_recycler));
                case TICKET_FAVORITE:
                    return new FavTicketHolder(UiUtils.getViewBy(parent, R.layout.item_ticket_recycler));
                case TICKET_CATEGORY:
                    return new TicketCategoriesBlockHolder(UiUtils.getViewBy(parent, R.layout.item_new_category_shape));
                case CITY:
                    return new CitiesHolder(UiUtils.getViewBy(parent, R.layout.item_new_explore_cities_recycle));
                case BIG_CATEGORY:
                    return new BigCategoriesHolder(UiUtils.getViewBy(parent, R.layout.item_new_explore_cities_recycle));
                case SMALL_CATEGORY:
                    return new SmallCategoriesHolder(UiUtils.getViewBy(parent, R.layout.item_new_explore_cities_recycle));
                case BANNER:
                    return new BannersHolder(UiUtils.getViewBy(parent, R.layout.item_new_explore_cities_recycle));
                case CUSTOM_COMPONENT:
                    return new CusTomAsyncVideoViewHolder(UiUtils.getViewBy(parent, R.layout.item_new_explore_custom_component_recycle));
                case CONTACT_US:
                    return new ContactUsHolder(UiUtils.getViewBy(parent, R.layout.item_contact_us_block));

            }
            return new EmptyHolder(UiUtils.getViewBy(parent, R.layout.item_layout_empty_holder));
        }

        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            HomeBlockModel model = (HomeBlockModel) getItem(position);
            if (model.getBlockType() == HomeBlockType.VIDEO) {
                AsyncVideoViewHolder viewHolder = (AsyncVideoViewHolder) holder;
                viewHolder.binding.videoControl.title = model.getTitle();
                viewHolder.binding.videoControl.setupData(model.getVideos(), holder.itemView, getActivity());
            } else if (model.getBlockType() == HomeBlockType.STORIES) {
                StoriesContainerHolder storiesContainerHolder = (StoriesContainerHolder) holder;
                storiesContainerHolder.setupData(model.getStories());
            } else if (model.getBlockType() == HomeBlockType.TICKET || model.getBlockType() == HomeBlockType.JUNIPER_HOTEL) {
                TicketHolder viewHolder = (TicketHolder) holder;
                viewHolder.binding.userTitle.setText(model.getTitle());
                viewHolder.binding.description.setText(model.getDescription());
                viewHolder.setupData(model.getTicketList(),model.getType());
            } else if (model.getBlockType() == HomeBlockType.TICKET_CATEGORY) {
                TicketCategoriesBlockHolder categoriesContainerHolder = (TicketCategoriesBlockHolder) holder;
                categoriesContainerHolder.setupData(model);
            } else if (model.getBlockType() == HomeBlockType.TICKET_FAVORITE) {
                FavTicketHolder viewHolder = (FavTicketHolder) holder;
                viewHolder.binding.userTitle.setText(model.getTitle());
                viewHolder.binding.description.setText(model.getDescription());
                viewHolder.setupData(model.favTicketList);
            }else if (model.getBlockType() == HomeBlockType.CITY) {
                CitiesHolder viewHolder = (CitiesHolder) holder;
                hideAndShowTitle(model, viewHolder.mBinding.userTitle, model.getTitle());
                hideAndShowTitle(model, viewHolder.mBinding.description, model.getDescription());
                viewHolder.setupData(model.citiesList);
            }else if (model.getBlockType() == HomeBlockType.BIG_CATEGORY) {
                BigCategoriesHolder viewHolder = (BigCategoriesHolder) holder;
                hideAndShowTitle(model, viewHolder.mBinding.userTitle, model.getTitle());
                hideAndShowTitle(model, viewHolder.mBinding.description, model.getDescription());
                viewHolder.setupData(model.bigCategoryList, model.getSize());
            }else if (model.getBlockType() == HomeBlockType.SMALL_CATEGORY) {
                SmallCategoriesHolder viewHolder = (SmallCategoriesHolder) holder;
                hideAndShowTitle(model, viewHolder.mBinding.userTitle, model.getTitle());
                hideAndShowTitle(model, viewHolder.mBinding.description, model.getDescription());
                viewHolder.setupData(model.smallCategoryList, model.getSize());
            }else if (model.getBlockType() == HomeBlockType.BANNER) {
                BannersHolder viewHolder = (BannersHolder) holder;
                hideAndShowTitle(model, viewHolder.mBinding.userTitle, model.getTitle());
                hideAndShowTitle(model, viewHolder.mBinding.description, model.getDescription());
                viewHolder.setupData(model.bannerList, model.getSize());
            } else if (model.getBlockType() == HomeBlockType.CUSTOM_COMPONENT) {
                CusTomAsyncVideoViewHolder viewHolder = (CusTomAsyncVideoViewHolder) holder;
                hideAndShowTitle(model, viewHolder.binding.userTitle, model.getTitle());
                hideAndShowTitle(model, viewHolder.binding.description, model.getDescription());
                viewHolder.binding.videoControl.setupData(model.exploreCustomComponent, holder.itemView, getActivity());
                viewHolder.setRatio(model);
                String type = model.exploreCustomComponent.get(0).getMediaType();
                viewHolder.binding.videoControl.hideLayouts(!type.equals("video"));
            } else if (model.getBlockType() == HomeBlockType.CONTACT_US) {
                ContactUsHolder viewHolder = (ContactUsHolder) holder;
                hideAndShowTitle(model, viewHolder.binding.userTitle, model.getTitle());
                hideAndShowTitle(model, viewHolder.binding.description, model.getDescription());
                viewHolder.setupData(model.getContactUsBlock().get(0));
            }

        }

        @Override
        public int getItemViewType(int position) {
            HomeBlockModel model = (HomeBlockModel) getItem(position);
            return model.getBlockType().getValue();
        }

        @Override
        public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
            if (holder.getItemViewType() == 5) {
                AsyncVideoViewHolder viewHolder = (AsyncVideoViewHolder) holder;
                viewHolder.binding.videoControl.releasePlayer();
            }
            super.onViewRecycled(holder);
        }


        public class CusTomAsyncVideoViewHolder extends RecyclerView.ViewHolder {

            private ItemNewExploreCustomComponentRecycleBinding binding;

            public CusTomAsyncVideoViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemNewExploreCustomComponentRecycleBinding.bind(itemView);
            }

            public void onItemVisibilityChanged(boolean isVisible) {
                binding.videoControl.onItemVisibilityChanged(isVisible);
            }

            public boolean isFromVideo(){
                return binding.videoControl.isVideo();
            }

            public void setRatio(HomeBlockModel model) {
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

        public class AsyncVideoViewHolder extends RecyclerView.ViewHolder {

            private ItemAsyncVideoComponentBinding binding;

            public AsyncVideoViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemAsyncVideoComponentBinding.bind(itemView);
            }

            public void onItemVisibilityChanged(boolean isVisible) {
                binding.videoControl.onItemVisibilityChanged(isVisible);
            }
        }

        public class StoriesContainerHolder extends RecyclerView.ViewHolder {
            private final ItemStoriesContainerBinding mBinding;
            private final StoryListAdapter<VenueObjectModel> adapter = new StoryListAdapter<>();

            public StoriesContainerHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemStoriesContainerBinding.bind(itemView);
                requireActivity().runOnUiThread(() -> {
                    setupRecycleHorizontalManager(mBinding.recyclerView);
                    mBinding.recyclerView.setAdapter(adapter);
                });

            }

            public void setupData(List<VenueObjectModel> venueList) {
                requireActivity().runOnUiThread(() -> adapter.updateData(venueList));
            }
        }

        public class TicketCategoriesBlockHolder extends RecyclerView.ViewHolder {

            private final ItemNewCategoryShapeBinding mBinding;

            public TicketCategoriesBlockHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemNewCategoryShapeBinding.bind(itemView);

//                requireActivity().runOnUiThread(() -> {
//                    setupRecycleHorizontalManager(mBinding.categoriesRecycler);
//                    mBinding.categoriesRecycler.setNestedScrollingEnabled(true);
//                    mBinding.categoriesRecycler.setAdapter(adapter);
//                });
            }

            public void setupData(HomeBlockModel model) {
                mBinding.categoryView.categoryType = model.getShape();
                requireActivity().runOnUiThread(() -> mBinding.categoryView.setUpData(requireActivity(),model.getShape(),model,model.ticketCategory,true));
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


        public class TicketHolder extends RecyclerView.ViewHolder {

            private final ItemTicketRecyclerBinding binding;

            public TicketHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemTicketRecyclerBinding.bind(itemView);
                binding.seeAll.setText(getValue("see_all"));
            }

            public void setupData(List<RaynaTicketDetailModel> ticket,String type) {
                requireActivity().runOnUiThread(() -> {
                    binding.ticketRecyclerView.isVertical = false;
                    binding.ticketRecyclerView.activity = activity;
                    binding.ticketRecyclerView.setupData(ticket, requireActivity(), false, false);
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

        public class FavTicketHolder extends RecyclerView.ViewHolder {

            private final ItemTicketRecyclerBinding binding;

            public FavTicketHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemTicketRecyclerBinding.bind(itemView);
                binding.seeAll.setText(getValue("see_all"));
            }

            public void setupData(List<RaynaTicketDetailModel> ticket) {
                requireActivity().runOnUiThread(() -> {
                    binding.ticketRecyclerView.isFromFavBlock = true;
                    binding.ticketRecyclerView.isVertical = false;
                    binding.ticketRecyclerView.activity = activity;
                    binding.ticketRecyclerView.setupData(ticket, requireActivity(), false, false);
                    binding.seeAll.setOnClickListener(v -> {
                        RaynaTicketManager.shared.raynaTicketList.clear();
                        Utils.preventDoubleClick(v);
                        RaynaTicketManager.shared.raynaTicketList.addAll(ticket);
                        Intent intent = new Intent(activity, RaynaTicketListActivity.class);
                        intent.putExtra("Description", binding.userTitle.getText().toString());
                        activity.startActivity(intent);
                    });
                });
            }
        }

        public class ContactUsHolder extends RecyclerView.ViewHolder {

            private final ItemContactUsBlockBinding binding;

            public ContactUsHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemContactUsBlockBinding.bind(itemView);
            }

            public void setupData(ContactUsBlockModel blocks) {
                ContactUsBlockManager.setupContactUsBlock(
                        requireContext(),
                        binding,
                        blocks,
                        ContactUsBlockModel.ContactBlockScreens.HOME
                );
            }
        }

        public class EmptyHolder extends RecyclerView.ViewHolder {

            ItemLayoutEmptyHolderBinding binding;

            public EmptyHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemLayoutEmptyHolderBinding.bind(itemView);
            }
        }


    }

    public class StoryListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new StoriesHolder(UiUtils.getViewBy(parent, R.layout.item_home_story_recycler));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            VenueObjectModel model = (VenueObjectModel) getItem(position);
            StoriesHolder viewHolder = (StoriesHolder) holder;
            requireActivity().runOnUiThread(() -> {
                viewHolder.mBinding.tvName.setText(model.getName());
                if (!TextUtils.isEmpty(model.getSmallLogo())){
                    Graphics.loadRoundImage(model.getSmallLogo(), viewHolder.mBinding.ivProfile);
                }else {
                    viewHolder.mBinding.ivProfile.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.social_app_icon));
                }

                Graphics.setStoryRing(model.getId(), viewHolder.mBinding.roundBorder);
            });

            viewHolder.mBinding.ivProfile.setOnClickListener(view -> {
                Intent intent = new Intent(requireActivity(), StoryViewActivity.class);
                intent.putExtra("stories", new Gson().toJson(getData()));
                intent.putExtra("selectedPosition", position);
                startActivity(intent);
            });
        }

        public class StoriesHolder extends RecyclerView.ViewHolder {
            private final ItemHomeStoryRecyclerBinding mBinding;

            public StoriesHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemHomeStoryRecyclerBinding.bind(itemView);
            }
        }
    }

    public class BigCategoryAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        private SizeModel sizeModel;

        public BigCategoryAdapter(SizeModel model){
            this.sizeModel = model;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = UiUtils.getViewBy(parent, R.layout.item_new_explore_big_category_design);
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.width = (int) (Graphics.getScreenWidth(activity) * (getItemCount() > 1 ? 0.89 : 0.93));
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
            String ratio = "16:9";
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


            viewHolder.mBinding.tvSubTitle.setText(model.getDescription());
            viewHolder.mBinding.tvTitle.setText(model.getTitle());

            viewHolder.itemView.setLayoutParams(layoutParams);

            String imageUrl = !TextUtils.isEmpty(model.getMedia()) && !Utils.isVideo(model.getMedia())
                    ? model.getMedia()
                    : model.getThumbnail();

            if (!TextUtils.isEmpty(imageUrl)) {
                Graphics.loadImage(imageUrl, viewHolder.mBinding.ivBigCategory);
            }


            viewHolder.mBinding.getRoot().setOnClickListener(v -> {
                Utils.preventDoubleClick(v);
                switch (model.getType()) {
                    case "ticket":
                        startActivity(new Intent(requireActivity(), RaynaTicketDetailActivity.class).putExtra("ticketId", model.getTypeId()));
                        break;
                    case "category":
                    case "small-category":
                    case "big-category":
                        if (SessionManager.shared.geExploreBlockData() != null) {
                            Optional<CategoriesModel> categoriesModel = SessionManager.shared.geExploreBlockData().getCategories().stream().filter(m -> m.getId().equals(model.getTypeId())).findFirst();
                            Intent intent = new Intent(requireActivity(), ExploreDetailActivity.class);
                            intent.putExtra("isCity", false);
                            if (categoriesModel.isPresent()) {
                                intent.putExtra("categoryModel", new Gson().toJson(categoriesModel.get()));
                            } else {
                                intent.putExtra("title", model.getTitle());
                            }
                            startActivity(intent);

                        }
                        break;
                    case "city":
                        Optional<BannerModel> citiModel = SessionManager.shared.geExploreBlockData().getBanners().stream().filter(m -> m.getId().equals(model.getId())).findFirst();
                        Intent intent = new Intent(requireActivity(), ExploreDetailActivity.class);
                        intent.putExtra("isCity", true);
                        citiModel.ifPresent(bannerModel -> intent.putExtra("categoryModel", new Gson().toJson(bannerModel)));
                        intent.putExtra("title", model.getTitle());
                        startActivity(intent);
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



    // endregion
    // --------------------------------------

}


