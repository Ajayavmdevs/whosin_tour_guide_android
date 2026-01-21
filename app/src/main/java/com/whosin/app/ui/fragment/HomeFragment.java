package com.whosin.app.ui.fragment;

import static com.whosin.app.comman.AppDelegate.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper;
import com.google.gson.Gson;
import com.whosin.app.R;
import com.whosin.app.comman.AppConstants;
import com.whosin.app.comman.AppConstants.HomeBlockType;
import com.whosin.app.comman.AppExecutors;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.FilterManager;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.HorizontalSpaceItemDecoration;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.ContactUsBlockManager;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.FragmentHomeBinding;
import com.whosin.app.databinding.FrindListItemBinding;
import com.whosin.app.databinding.HomeDealNewChangesBinding;
import com.whosin.app.databinding.ItemApplyNowDesignBinding;
import com.whosin.app.databinding.ItemAsyncVideoComponentBinding;
import com.whosin.app.databinding.ItemCategoriesBinding;
import com.whosin.app.databinding.ItemCategoriesContainerCompenetentBinding;
import com.whosin.app.databinding.ItemCmHomeEventBinding;
import com.whosin.app.databinding.ItemCompeleteProfileBannerRecyclerBinding;
import com.whosin.app.databinding.ItemContactUsBlockBinding;
import com.whosin.app.databinding.ItemCustomComponentBinding;
import com.whosin.app.databinding.ItemCustomComponentRecyclerBinding;
import com.whosin.app.databinding.ItemCustomOfferContainerComponentBinding;
import com.whosin.app.databinding.ItemHomeAdViewBinding;
import com.whosin.app.databinding.ItemHomeEventRecyclerBinding;
import com.whosin.app.databinding.ItemHomeExclusiveDealRecyclerBinding;
import com.whosin.app.databinding.ItemHomeOutingRecyclerBinding;
import com.whosin.app.databinding.ItemHomeStoryRecyclerBinding;
import com.whosin.app.databinding.ItemHomeSuggestedUserRecyclerBinding;
import com.whosin.app.databinding.ItemHomeTestActivityBlockBinding;
import com.whosin.app.databinding.ItemHomeVenueSuggestionRecyclerBinding;
import com.whosin.app.databinding.ItemLargeContainerCompententBinding;
import com.whosin.app.databinding.ItemLargeOfferBlockBinding;
import com.whosin.app.databinding.ItemLargeVenueComoponentRecyclerBinding;
import com.whosin.app.databinding.ItemNewCategoryShapeBinding;
import com.whosin.app.databinding.ItemNewExploreBigCategoryDesignBinding;
import com.whosin.app.databinding.ItemNewExploreCitiesRecycleBinding;
import com.whosin.app.databinding.ItemNewExploreCityBinding;
import com.whosin.app.databinding.ItemNewExploreCustomComponentRecycleBinding;
import com.whosin.app.databinding.ItemRaynaCategoriesBinding;
import com.whosin.app.databinding.ItemSpecialVenueLayoutBinding;
import com.whosin.app.databinding.ItemStoriesContainerBinding;
import com.whosin.app.databinding.ItemSubscriptionBannerRecyclerBinding;
import com.whosin.app.databinding.ItemTicketRecyclerBinding;
import com.whosin.app.databinding.ItemVenueRecyclerBinding;
import com.whosin.app.databinding.ItemYachtsBlockRecyclerBinding;
import com.whosin.app.databinding.OutingListItemBinding;
import com.whosin.app.databinding.TestActivityItemBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.AppSettingManager;
import com.whosin.app.service.manager.ComplementaryProfileManager;
import com.whosin.app.service.manager.GetNotificationManager;
import com.whosin.app.service.manager.RaynaTicketManager;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.ActivityDetailModel;
import com.whosin.app.service.models.BannerModel;
import com.whosin.app.service.models.CategoriesModel;
import com.whosin.app.service.models.ComplimentaryProfileModel;
import com.whosin.app.service.models.ContactListModel;
import com.whosin.app.service.models.ContactUsBlockModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.CustomComponentModel;
import com.whosin.app.service.models.ExclusiveDealModel;
import com.whosin.app.service.models.HomeBlockModel;
import com.whosin.app.service.models.HomeObjectModel;
import com.whosin.app.service.models.InviteFriendModel;
import com.whosin.app.service.models.MemberShipModel;
import com.whosin.app.service.models.MessageEvent;
import com.whosin.app.service.models.PromoterEventModel;
import com.whosin.app.service.models.SizeModel;
import com.whosin.app.service.models.StoryObjectModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.models.VenueObjectModel;
import com.whosin.app.service.models.YachtDetailModel;
import com.whosin.app.service.models.myCartModels.MyCartItemsModel;
import com.whosin.app.service.models.myCartModels.MyCartMainModel;
import com.whosin.app.service.models.rayna.RaynaTicketDetailModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.Notification.NotificaionActivity;
import com.whosin.app.ui.activites.Profile.OtherUserProfileActivity;
import com.whosin.app.ui.activites.Profile.UpdateProfileActivity;
import com.whosin.app.ui.activites.Promoter.PromoterActivity;
import com.whosin.app.ui.activites.Story.StoryViewActivity;
import com.whosin.app.ui.activites.bucket.MyInvitationActivity;
import com.whosin.app.ui.activites.bucket.OutingListActivity;
import com.whosin.app.ui.activites.cartManagement.TicketCartActivity;
import com.whosin.app.ui.activites.category.CategoryActivity;
import com.whosin.app.ui.activites.explore.ExploreDetailActivity;
import com.whosin.app.ui.activites.home.HomeMenuActivity;
import com.whosin.app.ui.activites.home.SeeAllDetalisActivity;
import com.whosin.app.ui.activites.home.activity.ActivityListDetail;
import com.whosin.app.ui.activites.home.activity.YourOrderActivity;
import com.whosin.app.ui.activites.home.event.InviteGuestListBottomSheet;
import com.whosin.app.ui.activites.offers.VoucherDetailScreenActivity;
import com.whosin.app.ui.activites.raynaTicket.RaynaTicketDetailActivity;
import com.whosin.app.ui.activites.raynaTicket.RaynaTicketListActivity;
import com.whosin.app.ui.activites.search.SearchActivity;
import com.whosin.app.ui.activites.venue.SmallVenueComponentSeeAllActivity;
import com.whosin.app.ui.activites.venue.ui.BuyNowActivity;
import com.whosin.app.ui.adapter.ComplementaryEventsListAdapter;
import com.whosin.app.ui.fragment.comman.BaseFragment;
import com.whosin.app.ui.fragment.home.InviteFriendBottomSheet;
import com.whosin.app.ui.fragment.home.SubscriptionPlanBottomSheet;
import com.whosin.app.ui.fragment.home.VideoPreCaching;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

        binding.navbar.getSettingBtn().setOnClickListener(view1 -> startActivity(new Intent(requireActivity(), HomeMenuActivity.class)));

        binding.navbar.getLinearBtn().setOnClickListener(v -> {
            startActivity(new Intent(requireActivity(), TicketCartActivity.class));
        });

        binding.navbar.getLeftBtn().setOnClickListener(view1 -> startActivity(new Intent(requireActivity(), NotificaionActivity.class)));

        binding.navbar.getSearchBtn().setOnClickListener(view -> startActivity(new Intent(requireActivity(), SearchActivity.class)));

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
    public void onMessageEvent(ComplimentaryProfileModel model) {
        SessionManager.shared.getCurrentUserProfile((success1, error1) -> {
        });
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

    private void setItemWidth(int itemCount, View view) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width = (int) (screenWidth * (itemCount > 1 ? 0.89 : 0.93));
        view.setLayoutParams(params);
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
                case OFFER_SMALL:
                case OFFER_LARGE:
                    return new LargeOfferBlockHolder(UiUtils.getViewBy(parent, R.layout.item_large_offer_block));
                case VENUE_SMALL:
                    return new SmallVenueBlockHolder(UiUtils.getViewBy(parent, R.layout.item_special_venue_layout));
                case CUSTOM_OFFER:
                    return new CustomOfferHolder(UiUtils.getViewBy(parent, R.layout.item_custom_offer_container_component));
                case CUSTOM_VENUE:
                    return new LargeContainerHolder(UiUtils.getViewBy(parent, R.layout.item_large_container_compentent));
                case CUSTOM_COMPOMENTS:
                    return new CustomComponentBlockHolder(UiUtils.getViewBy(parent, R.layout.item_custom_component_recycler));
                case DEALS:
                    return new ExclusiveDealBlockHolder(UiUtils.getViewBy(parent, R.layout.item_home_exclusive_deal_recycler));
                case VENUE_LARGE:
                    return new LargeVenueBlockHolder(UiUtils.getViewBy(parent, R.layout.item_large_venue_comoponent_recycler));
                case CATEGORIES:
                    return new CategoriesBlockHolder(UiUtils.getViewBy(parent, R.layout.item_categories_container_compenetent));
                case STORIES:
                    return new StoriesContainerHolder(UiUtils.getViewBy(parent, R.layout.item_stories_container));
                case ACTIVITIES:
                    return new TestActivityBlockHolder(UiUtils.getViewBy(parent, R.layout.item_home_test_activity_block));
                case EVENTS:
                    return new EventBlockHolder(UiUtils.getViewBy(parent, R.layout.item_home_event_recycler));
                case MY_OUTING:
                    return new MyOutingBlockHolder(UiUtils.getViewBy(parent, R.layout.item_home_outing_recycler));
                case SUGGESTED_USERS:
                    return new SuggestedUserBlockHolder(UiUtils.getViewBy(parent, R.layout.item_home_suggested_user_recycler));
                case VENUE_SUGGESTION:
                    return new SuggestedVenueViewHolder(UiUtils.getViewBy(parent, R.layout.item_home_venue_suggestion_recycler));
                case COMPLETE_PROFILE:
                    return new CompleteProfileBannerViewHolder(UiUtils.getViewBy(parent, R.layout.item_compelete_profile_banner_recycler));
                case MEMBERSHIP_PACKAGE:
                    return new SubscriptionBannerViewHolder(UiUtils.getViewBy(parent, R.layout.item_subscription_banner_recycler));
                case YACTCH:
                    return new YachtsBlockHolder(UiUtils.getViewBy(parent, R.layout.item_yachts_block_recycler));
                case YACTCH_OFFERS:
                    return new YachtsOfferBlockHolder(UiUtils.getViewBy(parent, R.layout.item_yachts_block_recycler));
                case APPLY_PROMOTER:
                    return new ApplyPromoterHolder(UiUtils.getViewBy(parent, R.layout.item_apply_now_design));
                case APPLY_RING:
                    return new ApplyRingHolder(UiUtils.getViewBy(parent, R.layout.item_apply_now_design));
                case PROMOTER_EVENT:
                    return new PromoterEventHolder(UiUtils.getViewBy(parent, R.layout.item_cm_home_event));
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
                case HOME_AD:
                    return new HomeAdHolder(UiUtils.getViewBy(parent, R.layout.item_home_ad_view));
                case CUSTOM_COMPONENT:
                    return new CusTomAsyncVideoViewHolder(UiUtils.getViewBy(parent, R.layout.item_new_explore_custom_component_recycle));
                case CONTACT_US:
                    return new ContactUsHolder(UiUtils.getViewBy(parent, R.layout.item_contact_us_block));

            }
            return new LargeVenueBlockHolder(UiUtils.getViewBy(parent, R.layout.item_large_venue_comoponent_recycler));
        }

        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            HomeBlockModel model = (HomeBlockModel) getItem(position);
            if (model.getBlockType() == HomeBlockType.VIDEO) {
                AsyncVideoViewHolder viewHolder = (AsyncVideoViewHolder) holder;
                viewHolder.binding.videoControl.title = model.getTitle();
                viewHolder.binding.videoControl.setupData(model.getVideos(), holder.itemView, getActivity());
            }else if (model.getBlockType() == HomeBlockType.HOME_AD) {
                HomeAdHolder viewHolder = (HomeAdHolder) holder;
                viewHolder.setupData();
            } else if (model.getBlockType() == HomeBlockType.VENUE_SMALL) {
                SmallVenueBlockHolder venueViewHolder = (SmallVenueBlockHolder) holder;
                venueViewHolder.setupData(model.venueList);
                venueViewHolder.mBinding.txtTitle.setText(model.getTitle());
                venueViewHolder.mBinding.seeAll.setOnClickListener(view -> {
                    List<VenueObjectModel> list = model.venueList;
                    startActivity(new Intent(requireActivity(), SmallVenueComponentSeeAllActivity.class).putExtra("venueModel", new Gson().toJson(list)).putExtra("title", model.getTitle()));
                });
            } else if (model.getBlockType() == HomeBlockType.VENUE_LARGE) {
                LargeVenueBlockHolder viewHolder = (LargeVenueBlockHolder) holder;
                viewHolder.setupData(model.venueList);
                viewHolder.mBinding.txtTitle.setText(model.getTitle());
                viewHolder.mBinding.txtSubTitle.setText(model.getDescription());
            } else if (model.getBlockType() == HomeBlockType.OFFER_LARGE || model.getBlockType() == HomeBlockType.OFFER_SMALL) {
                LargeOfferBlockHolder viewHolder = (LargeOfferBlockHolder) holder;
                viewHolder.mBinding.txtTitle.setText(model.getTitle());
                viewHolder.mBinding.txtSubTitle.setText(model.getDescription());
                viewHolder.mBinding.offerListView.activity = requireActivity();
                viewHolder.mBinding.offerListView.setupData(model.offerList, requireActivity(), getChildFragmentManager());
            } else if (model.getBlockType() == HomeBlockType.CUSTOM_VENUE) {
                LargeContainerHolder viewHolder = (LargeContainerHolder) holder;
                model.getCustomVenues().removeIf(p -> SessionManager.shared.geHomeBlockData().getVenues().stream().noneMatch(o -> o.getId().equals(p.getVenueId())));
                viewHolder.mBinding.customVenueView.setupData(model.getCustomVenues(), getActivity(), getChildFragmentManager());
            } else if (model.getBlockType() == HomeBlockType.CUSTOM_OFFER) {
                CustomOfferHolder viewHolder = (CustomOfferHolder) holder;
                viewHolder.mBinding.offerView.setupData(model.getCustomOffers(), getActivity(), getChildFragmentManager());
            } else if (model.getBlockType() == HomeBlockType.CUSTOM_COMPOMENTS) {
                CustomComponentBlockHolder componentHolder = (CustomComponentBlockHolder) holder;
                componentHolder.setupData(model);
            } else if (model.getBlockType() == HomeBlockType.DEALS) {
                ExclusiveDealBlockHolder exclusiveDealViewHolder = (ExclusiveDealBlockHolder) holder;
                exclusiveDealViewHolder.setupData(model.getDeals());
                exclusiveDealViewHolder.mBinding.txtTitle.setText(model.getTitle());
                exclusiveDealViewHolder.mBinding.txtSubTitle.setText(model.getDescription());
            } else if (model.getBlockType() == HomeBlockType.STORIES) {
                StoriesContainerHolder storiesContainerHolder = (StoriesContainerHolder) holder;
                storiesContainerHolder.setupData(model.getStories());
            } else if (model.getBlockType() == HomeBlockType.CATEGORIES) {
                CategoriesBlockHolder categoriesContainerHolder = (CategoriesBlockHolder) holder;
                categoriesContainerHolder.setupData(model.getHomeBlockCategory());
            }  else if (model.getBlockType() == HomeBlockType.TICKET_CATEGORY) {
                TicketCategoriesBlockHolder categoriesContainerHolder = (TicketCategoriesBlockHolder) holder;
                categoriesContainerHolder.setupData(model);
            } else if (model.getBlockType() == HomeBlockType.ACTIVITIES) {
                TestActivityBlockHolder testActivityBlockHolder = (TestActivityBlockHolder) holder;
                testActivityBlockHolder.setupData(model.activityList);
                testActivityBlockHolder.mBinding.txtTitle.setText(model.getTitle());
                testActivityBlockHolder.mBinding.txtSubTitle.setText(model.getDescription());
            } else if (model.getBlockType() == HomeBlockType.EVENTS) {
                EventBlockHolder eventBlockHolder = (EventBlockHolder) holder;
                eventBlockHolder.mBinding.eventListView.setupData(model.eventList, getActivity(), getChildFragmentManager());
                eventBlockHolder.mBinding.txtTitle.setText(model.getTitle());
                eventBlockHolder.mBinding.txtSubTitle.setText(model.getDescription());
            } else if (model.getBlockType() == HomeBlockType.MY_OUTING) {
                MyOutingBlockHolder myOutingBlockHolder = (MyOutingBlockHolder) holder;
                myOutingBlockHolder.setupData(model.getMyOuting());
                myOutingBlockHolder.mBinding.txtTitle.setText(model.getTitle());
                myOutingBlockHolder.mBinding.txtSubTitle.setText(model.getDescription());
            } else if (model.getBlockType() == HomeBlockType.SUGGESTED_USERS) {
                SuggestedUserBlockHolder suggestedUserBlockHolder = (SuggestedUserBlockHolder) holder;
                suggestedUserBlockHolder.setupData(model.getSuggestedUsers());
                suggestedUserBlockHolder.mBinding.txtTitle.setText(model.getTitle());
            } else if (model.getBlockType() == HomeBlockType.VENUE_SUGGESTION) {
                SuggestedVenueViewHolder SuggestedVenueViewHolder = (SuggestedVenueViewHolder) holder;
                SuggestedVenueViewHolder.setupData(model.getSuggestedVenue());
                SuggestedVenueViewHolder.mBinding.txtTitle.setText(model.getTitle());
            } else if (model.getBlockType() == HomeBlockType.COMPLETE_PROFILE) {
                CompleteProfileBannerViewHolder completeProfileBannerViewHolder = (CompleteProfileBannerViewHolder) holder;
                completeProfileBannerViewHolder.setupData();
            } else if (model.getBlockType() == HomeBlockType.MEMBERSHIP_PACKAGE) {
                SubscriptionBannerViewHolder subscriptionBannerViewHolder = (SubscriptionBannerViewHolder) holder;
                subscriptionBannerViewHolder.setupData(model.memberShipList);
            } else if (model.getBlockType() == HomeBlockType.YACTCH) {
                YachtsBlockHolder viewHolder = (YachtsBlockHolder) holder;
                viewHolder.setupData(model.yachtList);
                viewHolder.binding.txtTitle.setText(model.getTitle());
                viewHolder.binding.txtSubTitle.setText(model.getDescription());
            } else if (model.getBlockType() == HomeBlockType.YACTCH_OFFERS) {
                YachtsOfferBlockHolder viewHolder = (YachtsOfferBlockHolder) holder;
//                List<YachtDetailModel> yachtDetail = model.yachtOfferList.stream().map(YachtsOfferModel::getYacht).collect(Collectors.toList());
                List<YachtDetailModel> yachtDetail = model.yachtOfferList.stream()
                        .map(yachtOfferModel -> {
                            YachtDetailModel yachtDetailModel = yachtOfferModel.getYacht();
                            yachtDetailModel.setYachtOfferId(yachtOfferModel.getId());
                            return yachtDetailModel;
                        })
                        .collect(Collectors.toList());

                viewHolder.setupData(yachtDetail);
                viewHolder.binding.txtTitle.setText(model.getTitle());
                viewHolder.binding.txtSubTitle.setText(model.getDescription());
            } else if (model.getBlockType() == HomeBlockType.APPLY_PROMOTER) {
                ApplyPromoterHolder viewHolder = (ApplyPromoterHolder) holder;
                viewHolder.setupData(model);
            } else if (model.getBlockType() == HomeBlockType.APPLY_RING) {
                ApplyRingHolder viewHolder = (ApplyRingHolder) holder;
                viewHolder.setupData(model);
            } else if (model.getBlockType() == HomeBlockType.PROMOTER_EVENT) {
                PromoterEventHolder viewHolder = (PromoterEventHolder) holder;
                viewHolder.binding.txtTitle.setText(model.getTitle());
                viewHolder.binding.txtSubTitle.setText(model.getDescription());
                viewHolder.setupData(model.getPromoterEvents(), model);
            } else if (model.getBlockType() == HomeBlockType.TICKET || model.getBlockType() == HomeBlockType.JUNIPER_HOTEL) {
                TicketHolder viewHolder = (TicketHolder) holder;
                viewHolder.binding.userTitle.setText(model.getTitle());
                viewHolder.binding.description.setText(model.getDescription());
//                hideAndShowTitle(model, viewHolder.binding.userTitle, model.getTitle());
//                hideAndShowTitle(model, viewHolder.binding.description, model.getDescription());
                viewHolder.setupData(model.getTicketList(),model.getType());
            }else if (model.getBlockType() == HomeBlockType.TICKET_FAVORITE) {
                FavTicketHolder viewHolder = (FavTicketHolder) holder;
                viewHolder.binding.userTitle.setText(model.getTitle());
                viewHolder.binding.description.setText(model.getDescription());
//                hideAndShowTitle(model, viewHolder.binding.userTitle, model.getTitle());
//                hideAndShowTitle(model, viewHolder.binding.description, model.getDescription());
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

        public class LargeContainerHolder extends RecyclerView.ViewHolder {
            private final ItemLargeContainerCompententBinding mBinding;

            public LargeContainerHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemLargeContainerCompententBinding.bind(itemView);
            }
        }

        public class CustomOfferHolder extends RecyclerView.ViewHolder {
            private final ItemCustomOfferContainerComponentBinding mBinding;

            public CustomOfferHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemCustomOfferContainerComponentBinding.bind(itemView);
            }
        }

        public class CategoriesBlockHolder extends RecyclerView.ViewHolder {

            private final ItemCategoriesContainerCompenetentBinding mBinding;

            private final CategoriesAdapter<CategoriesModel> adapter = new CategoriesAdapter<>();

            public CategoriesBlockHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemCategoriesContainerCompenetentBinding.bind(itemView);
                mBinding.tvTitle.setText(getValue("categories"));
                requireActivity().runOnUiThread(() -> {
                    setupRecycleHorizontalManager(mBinding.categoriesRecycler);
                    mBinding.categoriesRecycler.setNestedScrollingEnabled(true);
                    mBinding.categoriesRecycler.setAdapter(adapter);
                });
            }

            public void setupData(List<CategoriesModel> categoriesList) {
                requireActivity().runOnUiThread(() -> adapter.updateData(categoriesList));
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

        public class SmallVenueBlockHolder extends RecyclerView.ViewHolder {
            private final ItemSpecialVenueLayoutBinding mBinding;
            private final SmallVenueBlockAdapter<VenueObjectModel> adapter = new SmallVenueBlockAdapter<>();

            public SmallVenueBlockHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemSpecialVenueLayoutBinding.bind(itemView);
                requireActivity().runOnUiThread(() -> {
                    mBinding.venueRecycler.setLayoutManager(new GridLayoutManager(requireActivity(), 4, LinearLayoutManager.HORIZONTAL, false));
                    mBinding.venueRecycler.setAdapter(adapter);
                });
            }

            public void setupData(List<VenueObjectModel> modelList) {
                requireActivity().runOnUiThread(() -> adapter.updateData(modelList));
            }
        }

        public class ExclusiveDealBlockHolder extends RecyclerView.ViewHolder {
            private final ItemHomeExclusiveDealRecyclerBinding mBinding;
            private ExclusiveDealAdapter<ExclusiveDealModel> adapter;

            public ExclusiveDealBlockHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemHomeExclusiveDealRecyclerBinding.bind(itemView);
                requireActivity().runOnUiThread(() -> {
                    setupRecycleHorizontalManager(mBinding.recyclerView);
                    GravitySnapHelper snapHelper = new GravitySnapHelper(Gravity.START);
                    snapHelper.setSnapLastItem(true);
                    snapHelper.setSnapToPadding(true);
                    snapHelper.attachToRecyclerView(mBinding.recyclerView);
                });
            }

            public void setupData(List<ExclusiveDealModel> deals) {
                Log.d("ExclusiveDealAdapter", "setupData: " + deals.size());
                requireActivity().runOnUiThread(() -> {
                    adapter = new ExclusiveDealAdapter<>();
                    mBinding.recyclerView.setAdapter(adapter);
                    adapter.updateData(deals);
                });

            }
        }

        public class TestActivityBlockHolder extends RecyclerView.ViewHolder {
            private final ItemHomeTestActivityBlockBinding mBinding;
            private final TestActivityAdapter<ActivityDetailModel> adapter = new TestActivityAdapter<>();

            public TestActivityBlockHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemHomeTestActivityBlockBinding.bind(itemView);
                requireActivity().runOnUiThread(() -> {
                    setupRecycleHorizontalManager(mBinding.testActivityRecycler);
                    GravitySnapHelper snapHelper = new GravitySnapHelper(Gravity.START);
                    snapHelper.setSnapLastItem(true);
                    snapHelper.setSnapToPadding(true);
                    snapHelper.attachToRecyclerView(mBinding.testActivityRecycler);
                    mBinding.testActivityRecycler.setAdapter(adapter);
                });
                mBinding.txtSeeAll.setOnClickListener(view -> startActivity(new Intent(getActivity(), SeeAllDetalisActivity.class)));
            }

            public void setupData(List<ActivityDetailModel> activities) {
                requireActivity().runOnUiThread(() -> adapter.updateData(activities));
            }
        }

        public class MyOutingBlockHolder extends RecyclerView.ViewHolder {
            private final ItemHomeOutingRecyclerBinding mBinding;
            private final OutingListAdapter<InviteFriendModel> adapter = new OutingListAdapter<>();

            public MyOutingBlockHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemHomeOutingRecyclerBinding.bind(itemView);
                setupRecycleHorizontalManager(mBinding.outingRecycler);
                mBinding.outingRecycler.setAdapter(adapter);
            }

            public void setupData(List<InviteFriendModel> outingList) {
                adapter.updateData(outingList);
                mBinding.txtSeeAll.setOnClickListener(view -> startActivity(new Intent(context, OutingListActivity.class)));
            }
        }

        public class SuggestedUserBlockHolder extends RecyclerView.ViewHolder {
            private final ItemHomeSuggestedUserRecyclerBinding mBinding;

            public SuggestedUserBlockHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemHomeSuggestedUserRecyclerBinding.bind(itemView);
            }

            public void setupData(List<UserDetailModel> suggestedUsers) {
                mBinding.suggestedUserView.setSuggestedUser(suggestedUsers, requireActivity(), getChildFragmentManager(), (success, error) -> {
                });
            }
        }

        public class SuggestedVenueViewHolder extends RecyclerView.ViewHolder {
            private final ItemHomeVenueSuggestionRecyclerBinding mBinding;

            public SuggestedVenueViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemHomeVenueSuggestionRecyclerBinding.bind(itemView);

            }

            public void setupData(List<VenueObjectModel> suggestedVenue) {
                mBinding.suggestedVenue.setSuggestedVenue(suggestedVenue, requireActivity(), getChildFragmentManager(), (success, error) -> {
                });
            }
        }

        public class CompleteProfileBannerViewHolder extends RecyclerView.ViewHolder {
            private final ItemCompeleteProfileBannerRecyclerBinding mBinding;

            public CompleteProfileBannerViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemCompeleteProfileBannerRecyclerBinding.bind(itemView);
            }

            public void setupData() {
                Graphics.loadImageWithFirstLetter(SessionManager.shared.getUser().getImage(), mBinding.image, SessionManager.shared.getUser().getFirstName());
                mBinding.getRoot().setOnClickListener(view -> startActivity(new Intent(requireActivity(), UpdateProfileActivity.class)));
            }
        }

        public class SubscriptionBannerViewHolder extends RecyclerView.ViewHolder {
            private final ItemSubscriptionBannerRecyclerBinding mBinding;

            public SubscriptionBannerViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemSubscriptionBannerRecyclerBinding.bind(itemView);
            }

            public void setupData(List<MemberShipModel> memberShipList) {
                if (memberShipList != null && !memberShipList.isEmpty()) {
                    mBinding.txtTitle.setText(memberShipList.get(0).getTitle());
                    mBinding.txtSubTitle.setText(memberShipList.get(0).getSubTitle());

                    mBinding.getRoot().setOnClickListener(v -> {
                        Utils.preventDoubleClick(v);
                        SubscriptionPlanBottomSheet subscriptionPlanDialog = new SubscriptionPlanBottomSheet();
                        subscriptionPlanDialog.memberShipList = memberShipList;
                        subscriptionPlanDialog.callback = data -> {
                        };
                        subscriptionPlanDialog.show(getChildFragmentManager(), "1");
                    });
                }
            }
        }

        public class EventBlockHolder extends RecyclerView.ViewHolder {

            private final ItemHomeEventRecyclerBinding mBinding;

            public EventBlockHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemHomeEventRecyclerBinding.bind(itemView);
            }
        }

        public class LargeVenueBlockHolder extends RecyclerView.ViewHolder {
            private final ItemLargeVenueComoponentRecyclerBinding mBinding;

            public LargeVenueBlockHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemLargeVenueComoponentRecyclerBinding.bind(itemView);
            }

            public void setupData(List<VenueObjectModel> venues) {
                mBinding.venueListView.setupData(venues, requireActivity(), getChildFragmentManager());
//                requireActivity().runOnUiThread( () ->  );
            }
        }

        public class CustomComponentBlockHolder extends RecyclerView.ViewHolder {

            private final ItemCustomComponentRecyclerBinding mBinding;

            private final CustomComponentAdapter<CustomComponentModel> adapter = new CustomComponentAdapter<>();

            public CustomComponentBlockHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemCustomComponentRecyclerBinding.bind(itemView);
                requireActivity().runOnUiThread(() -> {
                    mBinding.customRecycler.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false));
                    GravitySnapHelper snapHelper = new GravitySnapHelper(Gravity.START);
                    snapHelper.setSnapLastItem(true);
                    snapHelper.setSnapToPadding(true);
                    snapHelper.attachToRecyclerView(mBinding.customRecycler);
                    mBinding.customRecycler.setAdapter(adapter);
                });
//                Utils.smoothScrollToPosition(mBinding.customRecycler, 0);
            }

            public void setupData(HomeBlockModel model) {
                mBinding.txtTitle.setText(model.getTitle());
                mBinding.txtSubTitle.setText(model.getDescription());
                adapter.updateData(model.getCustomComponentModelList());
            }
        }

        public class LargeOfferBlockHolder extends RecyclerView.ViewHolder {
            private final ItemLargeOfferBlockBinding mBinding;

            public LargeOfferBlockHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemLargeOfferBlockBinding.bind(itemView);
            }
        }

        public class YachtsBlockHolder extends RecyclerView.ViewHolder {

            private final ItemYachtsBlockRecyclerBinding binding;


            public YachtsBlockHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemYachtsBlockRecyclerBinding.bind(itemView);

            }

            public void setupData(List<YachtDetailModel> yacht) {
                requireActivity().runOnUiThread(() -> binding.yachtListView.setupData(yacht, requireActivity(),
                        getChildFragmentManager(), false));
            }
        }

        public class YachtsOfferBlockHolder extends RecyclerView.ViewHolder {

            private final ItemYachtsBlockRecyclerBinding binding;


            public YachtsOfferBlockHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemYachtsBlockRecyclerBinding.bind(itemView);

            }

            public void setupData(List<YachtDetailModel> yacht) {
                requireActivity().runOnUiThread(() -> binding.yachtListView.setupData(yacht, requireActivity(),
                        getChildFragmentManager(), true));
            }
        }

        public class ApplyPromoterHolder extends RecyclerView.ViewHolder {

            private final ItemApplyNowDesignBinding binding;


            public ApplyPromoterHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemApplyNowDesignBinding.bind(itemView);

            }

            public void setupData(HomeBlockModel model) {
                binding.txtTitle.setText(model.getTitle());
                binding.txtSubTitle.setText(model.getDescription());
                if (!Utils.isNullOrEmpty(model.getColor())) {
                    String colorString = model.getColor();
                    int color = Color.parseColor(colorString);
                    binding.btnApply.setBackgroundColor(color);
                }

                Graphics.loadImage(model.getBackgroundImage(), binding.ivBackground);
                binding.btnApplyTv.setText("APPLY FOR PROMOTER");

                if (model.getApplicationStatus().equals("pending")) {
                    binding.btnApply.setVisibility(View.GONE);
                    binding.txtTitle.setText("Your application is pending.");
                    binding.txtSubTitle.setText("Please wait for confirmation before taking further action.");
                } else {
                    binding.btnApply.setVisibility(View.VISIBLE);
                }

                binding.btnApply.setOnClickListener(v -> {
                    Utils.preventDoubleClick(v);
                    startActivity(new Intent(requireActivity(), PromoterActivity.class).putExtra("isPromoter", true));
                });
            }
        }

        public class ApplyRingHolder extends RecyclerView.ViewHolder {

            private final ItemApplyNowDesignBinding binding;


            public ApplyRingHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemApplyNowDesignBinding.bind(itemView);

            }

            public void setupData(HomeBlockModel model) {
                binding.txtTitle.setText(model.getTitle());
                binding.txtSubTitle.setText(model.getDescription());
                if (!Utils.isNullOrEmpty(model.getColor())) {
                    String colorString = model.getColor();
                    int color = Color.parseColor(colorString);
                    binding.btnApply.setBackgroundColor(color);
                }

                if (!TextUtils.isEmpty(model.getBackgroundImage())) {
                    Graphics.loadImage(model.getBackgroundImage(), binding.ivBackground);
                } else {
                    Graphics.applyGradientBackground(binding.ivBackground,model.getColor());
//                    binding.ivBackground.setBackgroundColor(Color.parseColor(model.getColor()));
                }

                binding.btnApplyTv.setText("Apply now");

                Log.d("HomeBlock", "setupData: " + new Gson().toJson(model));

                if (model.getApplicationStatus().equals("pending")) {
                    binding.btnApply.setVisibility(View.GONE);
                    binding.txtTitle.setText("Your application is pending.");
                    binding.txtSubTitle.setText("Please wait for confirmation before taking further action.");
                } else {
                    binding.btnApply.setVisibility(View.VISIBLE);
                }

                binding.btnApply.setOnClickListener(v -> {
                    Utils.preventDoubleClick(v);
                    startActivity(new Intent(requireActivity(), PromoterActivity.class).putExtra("isPromoter", false));
                });

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


    }

    public class PromoterEventHolder extends RecyclerView.ViewHolder {

        private final ItemCmHomeEventBinding binding;

        private ComplementaryEventsListAdapter<PromoterEventModel> eventlistdapter;

        public PromoterEventHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemCmHomeEventBinding.bind(itemView);
            binding.seeAll.setText(getValue("see_all"));

        }

        public void setupData(List<PromoterEventModel> promoterEventModelList, HomeBlockModel model) {
            eventlistdapter = new ComplementaryEventsListAdapter<>(requireActivity());
            binding.eventImInRecycleView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            binding.eventImInRecycleView.setAdapter(eventlistdapter);
            eventlistdapter.updateData(promoterEventModelList);


            binding.seeAll.setOnClickListener(v -> {
                Utils.preventDoubleClick(v);
                ComplementaryProfileManager.shared.setProfileCallBack.onReceive(true);
            });
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

    public class CategoriesAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = UiUtils.getViewBy(parent, R.layout.item_categories);
            view.getLayoutParams().width = (int) (screenWidth * 0.40);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            CategoriesModel model = (CategoriesModel) getItem(position);
            viewHolder.mBinding.txtCategories.setText(model.getTitle());
            if (TextUtils.isEmpty(model.getImage())) {
                int color = Color.parseColor(model.getColor().getStartColor());
                viewHolder.mBinding.bgImageView.setBackgroundColor(color);
            } else {
                Graphics.loadImage(model.getImage(), viewHolder.mBinding.bgImageView);
            }
            viewHolder.itemView.setOnClickListener(view -> startActivity(new Intent(requireActivity(), CategoryActivity.class).putExtra("categoryId", model.getId()).putExtra("image", model.getImage())));
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final ItemCategoriesBinding mBinding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemCategoriesBinding.bind(itemView);
            }
        }
    }

    public class SmallVenueBlockAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = UiUtils.getViewBy(parent, R.layout.item_venue_recycler);
            setItemWidth(2, view);
            return new VenueViewHolder(view);
        }

        private boolean hasStory(VenueObjectModel venueObjectModel) {
            if (venueObjectModel.getStories() != null && (!venueObjectModel.getStories().isEmpty())) {
                return true;
            }
            HomeObjectModel homeObjectModel1 = SessionManager.shared.geHomeBlockData();
            if (homeObjectModel1 != null) {
                Optional<VenueObjectModel> tmpVenue = homeObjectModel1.getStories().stream().filter(p -> Objects.equals(p.getId(), venueObjectModel.getId())).findFirst();
                return tmpVenue.isPresent();
            }
            return false;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            VenueViewHolder viewHolder = (VenueViewHolder) holder;
            VenueObjectModel model = (VenueObjectModel) getItem(position);
            if (model != null) {
                viewHolder.mBinding.txtName.setText(model.getName());
                viewHolder.mBinding.txtAddress.setText(model.getAddress());
                Graphics.loadRoundImage(model.getLogo(), viewHolder.mBinding.image);
                Thread backgroundThread = new Thread(() -> {
                    if (hasStory(model)) {
                        requireActivity().runOnUiThread(() -> Graphics.setStoryRing(model.getId(), viewHolder.mBinding.roundLinear));
                    }
                });
                backgroundThread.start();
                viewHolder.mBinding.btnContinue.setOnClickListener(v -> Graphics.openVenueDetail(requireActivity(), model.getId()));
                viewHolder.mBinding.roundLinear.setOnClickListener(view -> {
                    HomeObjectModel homeObjectModel = SessionManager.shared.geHomeBlockData();
                    List<VenueObjectModel> matchingStories = homeObjectModel.getStories().stream().filter(model1 -> model1.getId().equals(model.getId())).collect(Collectors.toList());
                    if (!matchingStories.isEmpty()) {
                        Intent intent = new Intent(context, StoryViewActivity.class);
                        intent.putExtra("stories", new Gson().toJson(matchingStories));
                        intent.putExtra("selectedPosition", 0);
                        context.startActivity(intent);
                    }
                });
            }
        }

        public class VenueViewHolder extends RecyclerView.ViewHolder {
            private final ItemVenueRecyclerBinding mBinding;

            public VenueViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemVenueRecyclerBinding.bind(itemView);
            }
        }
    }

    public class CustomComponentAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = UiUtils.getViewBy(parent, R.layout.item_custom_component);
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.width = (int) (screenWidth * 0.80);
            view.setLayoutParams(params);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            CustomViewHolder viewHolder = (CustomViewHolder) holder;

            CustomComponentModel model = (CustomComponentModel) getItem(position);
            viewHolder.binding.tvTitle.setText(model.getTitle());
            viewHolder.binding.tvDescription.setText(model.getDescription());
            Graphics.loadImage(model.getImage(), viewHolder.binding.ivCover);
            viewHolder.binding.tvPrice.setText(Utils.addPercentage(model.getBadge()));


            activity.runOnUiThread(() -> {
                if (model.getType().equals("venue") && model.getVenueObjectModel() != null) {
                    viewHolder.binding.venueContainer.setVenueDetail(model.getVenueObjectModel());
                } else if (model.getType().equals("ticket") && model.getRaynaTicketDetailModel() != null) {
                    viewHolder.binding.venueContainer.setTicketDetail(model.getRaynaTicketDetailModel());
                }
            });


            viewHolder.binding.getRoot().setOnClickListener(v -> {
                Utils.preventDoubleClick(v);
                if (model.getType().equals("venue")){
                    Graphics.openVenueDetail(requireActivity(), model.getVenueId());
                } else if (model.getType().equals("ticket")) {
                    startActivity(new Intent(requireActivity(), RaynaTicketDetailActivity.class).putExtra("ticketId",model.getTicketId()));
                }
            });


        }

        public class CustomViewHolder extends RecyclerView.ViewHolder {

            private ItemCustomComponentBinding binding;

            public CustomViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemCustomComponentBinding.bind(itemView);
            }
        }
    }

    public class ExclusiveDealAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = UiUtils.getViewBy(parent, R.layout.home_deal_new_changes);
            ViewGroup.LayoutParams params = view.getLayoutParams();
            Log.d("ExclusiveDealAdapter", "onCreateViewHolder: " + getItemCount());
            params.width = (int) (screenWidth * (getItemCount() > 1 ? 0.83 : 0.93));
            view.setLayoutParams(params);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;

            ExclusiveDealModel model = (ExclusiveDealModel) getItem(position);


            viewHolder.mBinding.roundBlur.isColorTranspart = true;
            viewHolder.mBinding.roundBlur.activity = requireActivity();
            viewHolder.mBinding.tvTitle.setText(model.getDescription());
            viewHolder.mBinding.tvSubTitle.setText(model.getTitle());
            Graphics.loadImage(model.getImage(), viewHolder.mBinding.cover);
            Graphics.loadImage(model.getImage(), viewHolder.mBinding.backGroundImageForOffer);


            if (model.getDiscountedPrice() != 0) {
                viewHolder.mBinding.tvAED.setText(String.valueOf( model.getDiscountedPrice() + " AED"));
                viewHolder.mBinding.roundLinear.setVisibility(View.VISIBLE);

            } else {
                viewHolder.mBinding.tvAED.setVisibility(View.GONE);
                viewHolder.mBinding.roundLinear.setVisibility(View.GONE);
            }


            viewHolder.mBinding.venueContainer.setVenueDetail(model.getVenue());

            viewHolder.mBinding.roundLinear.setOnClickListener(v -> {
                Optional<VenueObjectModel> venueObjectModel = SessionManager.shared.geHomeBlockData().getVenues().stream().filter(p -> p.getId().equals(model.getVenueId())).findFirst();
                if (venueObjectModel.isPresent()) {
                    Intent intent = new Intent(getActivity(), BuyNowActivity.class);
                    intent.putExtra("dealId", model.getId())
                            .putExtra("venueDetail", new Gson().toJson(venueObjectModel.get()))
                            .putExtra("offerModel", new Gson().toJson(model));
                    startActivity(intent);
                }
            });



            viewHolder.mBinding.getRoot().setOnClickListener(v -> startActivity(new Intent(requireActivity(), VoucherDetailScreenActivity.class).putExtra("id", model.getId())));

            if (model.getEndDate() != null) {
                viewHolder.mBinding.roundBlur.setVisibility(View.VISIBLE);
                viewHolder.mBinding.roundBlur.setUpData( model.getEndDate(),  model.getEndTime());
            }else {
                viewHolder.mBinding.roundBlur.setVisibility(View.GONE);
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private HomeDealNewChangesBinding mBinding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = HomeDealNewChangesBinding.bind(itemView);
            }
        }
    }

    public class TestActivityAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = UiUtils.getViewBy(parent, R.layout.test_activity_item);
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.width = (int) (screenWidth * (getItemCount() > 1 ? 0.83 : 0.93));
            view.setLayoutParams(params);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            ActivityDetailModel model = (ActivityDetailModel) getItem(position);

            viewHolder.mBinding.tvTitle.setText(model.getName());
            viewHolder.mBinding.textName.setText(model.getProvider().getName());
            viewHolder.mBinding.txtAddress.setText(model.getProvider().getAddress());
            Graphics.loadRoundImage(model.getProvider().getLogo(), viewHolder.mBinding.imgLogo);
            Graphics.loadImage(model.getGalleries().get(0), viewHolder.mBinding.cover);
            if (model.getAvgRating() != 0) {
                viewHolder.mBinding.tvRate.setText(String.format("%.1f", model.getAvgRating()));
            } else {
                viewHolder.mBinding.linearRate.setVisibility(View.GONE);
            }

            viewHolder.mBinding.tvAED.setVisibility(model.getDiscount().equals("0") ? View.GONE : View.VISIBLE);
            viewHolder.mBinding.tvAED.setPaintFlags(viewHolder.mBinding.tvAED.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            int discount = Integer.parseInt(model.getDiscount());
            int amount = model.getPrice();
            int value = discount * amount / 100;
            int discountPrice = amount - value;

            if ("0".equals(model.getDiscount())) {
                viewHolder.mBinding.tvAED.setVisibility(View.GONE);
                viewHolder.mBinding.tvPrice.setText(String.valueOf(model.getPrice()));
            } else {
                if (amount == discountPrice) {
                    viewHolder.mBinding.tvAED.setVisibility(View.GONE);
                    viewHolder.mBinding.tvPrice.setText(String.valueOf(model.getPrice()));
                } else {
                    viewHolder.mBinding.tvAED.setVisibility(View.VISIBLE);
                    viewHolder.mBinding.tvAED.setText(String.valueOf(model.getPrice()));
                    viewHolder.mBinding.tvPrice.setText(String.valueOf(discountPrice));

                }
            }

            viewHolder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), ActivityListDetail.class);
                intent.putExtra("activityId", model.getId()).putExtra("type", "activities");
                intent.putExtra("name", model.getName());
                intent.putExtra("image", model.getProvider().getLogo());
                intent.putExtra("title", model.getProvider().getName());
                intent.putExtra("address", model.getProvider().getAddress());
                startActivity(intent);
            });
            viewHolder.mBinding.buyNowBtn.setOnClickListener(view -> startActivity(new Intent(requireActivity(), YourOrderActivity.class).putExtra("activityModel", new Gson().toJson(model))));
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TestActivityItemBinding mBinding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = TestActivityItemBinding.bind(itemView);
            }
        }

    }

    public class OutingListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = UiUtils.getViewBy(parent, R.layout.outing_list_item);
            setItemWidth(getItemCount(), view);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            InviteFriendModel model = (InviteFriendModel) getItem(position);

            viewHolder.mBinding.txtCancelled.setVisibility(View.GONE);
            viewHolder.mBinding.btnCancel.setVisibility(View.GONE);
            viewHolder.mBinding.layoutPending.setVisibility(View.GONE);

            if (model.getVenue() != null) {
                viewHolder.mBinding.txtUserName.setText(model.getVenue().getName());
                viewHolder.mBinding.tvAddress.setText(model.getVenue().getAddress());
                Graphics.loadImage(model.getVenue().getCover(), viewHolder.mBinding.ivCover);
                Graphics.loadImageWithFirstLetter(model.getVenue().getLogo(), viewHolder.mBinding.imgUserLogo, model.getVenue().getName());
            }

            viewHolder.mBinding.tvOutingTitle.setText(model.getTitle());
            viewHolder.mBinding.txtExtraGuest.setText(String.valueOf(model.getExtraGuest()));
            viewHolder.mBinding.createDate.setText(String.format("Created date: %s", Utils.convertMainDateFormat(model.getCreatedAt())));
            viewHolder.mBinding.txtDate.setText(Utils.convertDateFormat(model.getDate(), "yyyy-MM-dd"));

            if (model.getStartTime() != null && model.getEndTime() != null) {
                viewHolder.mBinding.txtTime.setText(String.format("%s - %s", model.getStartTime(), Utils.convert24HourTimeFormat(model.getEndTime())));
            } else {
                viewHolder.mBinding.txtTime.setText("Time not available");
            }

            if (model.getInvitedUser() != null) {
                viewHolder.friendListAdapter.updateData(model.getInvitedUser());
            }

            if (model.isOwnerOfOuting()) {
                if (model.getUser() != null) {
                    Graphics.loadImageWithFirstLetter(model.getUser().getImage(), viewHolder.mBinding.imageLogo, model.getUser().getFirstName());
                }
                viewHolder.mBinding.txtOutingDescribe.setText("You created");
                viewHolder.mBinding.constraint.setBackground(ContextCompat.getDrawable(context, R.drawable.stroke_gradiant_line_me));
                viewHolder.mBinding.txtOutingTitle.setVisibility(View.GONE);
                viewHolder.mBinding.layoutStatus.setVisibility(View.INVISIBLE);
                if (model.getStatus().equals("completed") || model.getStatus().equals("cancelled")) {
                    viewHolder.mBinding.layoutEdit.setVisibility(View.GONE);
                } else {
                    viewHolder.mBinding.layoutEdit.setVisibility(View.VISIBLE);
                }
            } else {
                if (model.getUser() != null) {
                    viewHolder.mBinding.txtOutingTitle.setVisibility(View.VISIBLE);
                    viewHolder.mBinding.txtOutingTitle.setText(model.getUser().getFirstName());
                    viewHolder.mBinding.txtOutingDescribe.setVisibility(View.VISIBLE);
                    viewHolder.mBinding.txtOutingDescribe.setText("invited you to");
                    Graphics.loadImageWithFirstLetter(model.getUser().getImage(), viewHolder.mBinding.imageLogo, model.getUser().getFirstName());
                }
                viewHolder.mBinding.txtStatus.setText(model.getStatus());
                Thread backgroundThread = new Thread(() -> {
                    ContactListModel invitedUser = model.getInvitedUser().stream().filter(model1 -> model1.getUserId().equals(SessionManager.shared.getUser().getId())).findFirst().orElse(null);
                    if (invitedUser != null) {
                        AppExecutors.get().mainThread().execute(() -> {
                            if (invitedUser.getInviteStatus().equals("pending")) {
                                viewHolder.mBinding.layoutStatus.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.pending_yellow));
                                viewHolder.mBinding.constraint.setBackground(ContextCompat.getDrawable(context, R.drawable.stroke_gradiant_line_pending));
                            } else if (invitedUser.getInviteStatus().equals("in")) {
                                viewHolder.mBinding.layoutStatus.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.in_green));
                                viewHolder.mBinding.constraint.setBackground(ContextCompat.getDrawable(context, R.drawable.stroke_gradiant_line_in));
                            } else {
                                viewHolder.mBinding.layoutStatus.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.out_red));
                                viewHolder.mBinding.constraint.setBackground(ContextCompat.getDrawable(context, R.drawable.stroke_gradiant_line_out));
                            }
                        });
                    }
                });
                backgroundThread.start();

                viewHolder.mBinding.layoutEdit.setVisibility(View.GONE);
                viewHolder.mBinding.layoutStatus.setVisibility(View.VISIBLE);

                viewHolder.mBinding.layout2.setOnClickListener(view -> {
                    if (model.getUser() != null) {
                        startActivity(new Intent(requireActivity(), OtherUserProfileActivity.class).putExtra("friendId", model.getUser().getId()));
                    }
                });
            }

            viewHolder.mBinding.btnSeeAll.setOnClickListener(v -> {
                Utils.preventDoubleClick(v);
                InviteGuestListBottomSheet inviteGuestListBottomSheet = new InviteGuestListBottomSheet();
                inviteGuestListBottomSheet.model = model.getInvitedUser();
                inviteGuestListBottomSheet.type = "outing";
                inviteGuestListBottomSheet.show(getChildFragmentManager(), "");
            });

            viewHolder.mBinding.layoutEdit.setOnClickListener(view -> {
                Utils.preventDoubleClick(view);
                InviteFriendBottomSheet inviteFriendDialog = new InviteFriendBottomSheet();
                inviteFriendDialog.inviteFriendModel = model;
                inviteFriendDialog.setShareListener(data -> {
                    AppExecutors.get().mainThread().execute(() -> {
                        notifyDataSetChanged();
                    });
                });
                inviteFriendDialog.show(getChildFragmentManager(), "1");
            });

            viewHolder.mBinding.getRoot().setOnClickListener(view -> startActivity(new Intent(context, MyInvitationActivity.class).putExtra("id", model.getId()).putExtra("notificationType", "notification")));
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final OutingListItemBinding mBinding;
            private final FriendListAdapter<ContactListModel> friendListAdapter = new FriendListAdapter<>();

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = OutingListItemBinding.bind(itemView);
                mBinding.friendRecycler.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false));
                mBinding.friendRecycler.setAdapter(friendListAdapter);
                mBinding.friendRecycler.setNestedScrollingEnabled(false);
            }
        }
    }

    public static class FriendListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.frind_list_item));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            ContactListModel model = (ContactListModel) getItem(position);
            if (model == null) {
                return;
            }
            if (SessionManager.shared.getUser().getId().equals(model.getUserId())) {
                viewHolder.mBinding.txtUserName.setText("Me");
            } else {
                viewHolder.mBinding.txtUserName.setText(model.getFirstName());
            }
            Graphics.loadImageWithFirstLetter(model.getImage(), viewHolder.mBinding.imgUserLogo, model.getFirstName());
            switch (model.getInviteStatus()) {
                case "pending":
                    viewHolder.mBinding.iconStatus.setImageResource(R.drawable.icon_pending);
                    break;
                case "in":
                    viewHolder.mBinding.iconStatus.setImageResource(R.drawable.icon_complete);
                    break;
                case "out":
                    viewHolder.mBinding.iconStatus.setImageResource(R.drawable.icon_deleted);
                    break;
            }

        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private FrindListItemBinding mBinding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = FrindListItemBinding.bind(itemView);
            }
        }
    }

    public class TicketCategoriesAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = UiUtils.getViewBy(parent, R.layout.item_rayna_categories);
            view.getLayoutParams().width = (int) (screenWidth * 0.40);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            CategoriesModel model = (CategoriesModel) getItem(position);
            viewHolder.mBinding.txtCategories.setText(model.getTitle());


            if (TextUtils.isEmpty(model.getImage())) {
                int color = Color.parseColor(model.getColor().getStartColor());
                viewHolder.mBinding.bgImageView.setBackgroundColor(color);
            } else {
                Graphics.loadImage(model.getImage(), viewHolder.mBinding.bgImageView);
            }



            viewHolder.mBinding.getRoot().setOnClickListener(v -> {
                Utils.preventDoubleClick(v);
                Intent intent = new Intent(requireActivity(), ExploreDetailActivity.class);
                intent.putExtra("isCity", false);
                intent.putExtra("categoryModel", new Gson().toJson(model));
                intent.putExtra("title", model.getTitle());
                startActivity(intent);
            });


        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final ItemRaynaCategoriesBinding mBinding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemRaynaCategoriesBinding.bind(itemView);
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


