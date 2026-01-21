package com.whosin.app.ui.activites.search;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper;
import com.google.android.gms.common.util.CollectionUtils;
import com.google.android.material.tabs.TabLayout;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.tapadoo.alerter.Alerter;
import com.whosin.app.R;
import com.whosin.app.comman.AppConstants;
import com.whosin.app.comman.AppExecutors;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.HorizontalSpaceItemDecoration;
import com.whosin.app.comman.Preferences;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.BooleanResult;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.comman.ui.UserInfoView;
import com.whosin.app.comman.ui.roundcornerlayout.CornerType;
import com.whosin.app.databinding.ActivitySearchBinding;
import com.whosin.app.databinding.AllSearchOfferItemBinding;
import com.whosin.app.databinding.AllSearchVenueDesginItemBinding;
import com.whosin.app.databinding.CategoryListItemBinding;
import com.whosin.app.databinding.FrindListItemBinding;
import com.whosin.app.databinding.HomeExclusiveDealItemBinding;
import com.whosin.app.databinding.IteamSearchEventBinding;
import com.whosin.app.databinding.ItemAllSerachTicketBinding;
import com.whosin.app.databinding.ItemCategoriesBinding;
import com.whosin.app.databinding.ItemCategoriesContainerCompenetentBinding;
import com.whosin.app.databinding.ItemCustomComponentBinding;
import com.whosin.app.databinding.ItemCustomComponentRecyclerBinding;
import com.whosin.app.databinding.ItemCustomOfferContainerComponentBinding;
import com.whosin.app.databinding.ItemHomeAdViewBinding;
import com.whosin.app.databinding.ItemHomeEventRecyclerBinding;
import com.whosin.app.databinding.ItemHomeExclusiveDealRecyclerBinding;
import com.whosin.app.databinding.ItemHomeOutingRecyclerBinding;
import com.whosin.app.databinding.ItemHomeSuggestedUserRecyclerBinding;
import com.whosin.app.databinding.ItemHomeTestActivityBlockBinding;
import com.whosin.app.databinding.ItemHomeVenueSuggestionRecyclerBinding;
import com.whosin.app.databinding.ItemLargeContainerCompententBinding;
import com.whosin.app.databinding.ItemLargeOfferBlockBinding;
import com.whosin.app.databinding.ItemLargeVenueComoponentRecyclerBinding;
import com.whosin.app.databinding.ItemRecentSearchBinding;
import com.whosin.app.databinding.ItemRecentSearchTabBinding;
import com.whosin.app.databinding.ItemRecentSearchUserBinding;
import com.whosin.app.databinding.ItemSearchActivityBinding;
import com.whosin.app.databinding.ItemSearchSuggestionLayoutBinding;
import com.whosin.app.databinding.ItemSearchVenueBinding;
import com.whosin.app.databinding.ItemSpecialVenueLayoutBinding;
import com.whosin.app.databinding.ItemTicketRecyclerBinding;
import com.whosin.app.databinding.ItemVenueRecyclerBinding;
import com.whosin.app.databinding.OutingListItemBinding;
import com.whosin.app.databinding.SearchUserItemBinding;
import com.whosin.app.databinding.TestActivityItemBinding;
import com.whosin.app.databinding.TicketShimmerPlaceholderBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.LogManager;
import com.whosin.app.service.manager.RaynaTicketManager;
import com.whosin.app.service.manager.SearchSuggestionStore;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.ActivityDetailModel;
import com.whosin.app.service.models.BannerModel;
import com.whosin.app.service.models.CategoriesModel;
import com.whosin.app.service.models.ChatModel;
import com.whosin.app.service.models.CommanSearchModel;
import com.whosin.app.service.models.ContactListModel;
import com.whosin.app.service.models.ContainerListModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.CustomComponentModel;
import com.whosin.app.service.models.EventModel;
import com.whosin.app.service.models.ExclusiveDealModel;
import com.whosin.app.service.models.FollowUnfollowModel;
import com.whosin.app.service.models.HomeBlockModel;
import com.whosin.app.service.models.HomeObjectModel;
import com.whosin.app.service.models.InviteFriendModel;
import com.whosin.app.service.models.OffersModel;
import com.whosin.app.service.models.PackageModel;
import com.whosin.app.service.models.RatingModel;
import com.whosin.app.service.models.SearchEventModel;
import com.whosin.app.service.models.SearchHistoryModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.models.VenueObjectModel;
import com.whosin.app.service.models.VideoComponentModel;
import com.whosin.app.service.models.rayna.RaynaTicketDetailModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.CmProfile.CmPublicProfileActivity;
import com.whosin.app.ui.activites.Profile.OtherUserProfileActivity;
import com.whosin.app.ui.activites.PromoterPublic.PromoterPublicProfileActivity;
import com.whosin.app.ui.activites.Story.StoryViewActivity;
import com.whosin.app.ui.activites.bucket.MyInvitationActivity;
import com.whosin.app.ui.activites.bucket.OutingListActivity;
import com.whosin.app.ui.activites.category.CategoryActivity;
import com.whosin.app.ui.activites.comman.BaseActivity;
import com.whosin.app.ui.activites.home.Chat.ChatMessageActivity;
import com.whosin.app.ui.activites.home.SeeAllDetalisActivity;
import com.whosin.app.ui.activites.home.activity.ActivityListDetail;
import com.whosin.app.ui.activites.home.activity.YourOrderActivity;
import com.whosin.app.ui.activites.home.event.EventDetailsActivity;
import com.whosin.app.ui.activites.home.event.EventOrganizerDetailsActivity;
import com.whosin.app.ui.activites.home.event.InviteGuestListBottomSheet;
import com.whosin.app.ui.activites.offers.OfferDetailBottomSheet;
import com.whosin.app.ui.activites.offers.VoucherDetailScreenActivity;
import com.whosin.app.ui.activites.raynaTicket.RaynaTicketDetailActivity;
import com.whosin.app.ui.activites.raynaTicket.RaynaTicketListActivity;
import com.whosin.app.ui.activites.venue.Bucket.BucketListBottomSheet;
import com.whosin.app.ui.activites.venue.SmallVenueComponentSeeAllActivity;
import com.whosin.app.ui.activites.venue.VenueShareActivity;
import com.whosin.app.ui.activites.venue.VenueTimingDialog;
import com.whosin.app.ui.activites.venue.ui.BuyNowActivity;
import com.whosin.app.ui.adapter.OfferPackagesAdapter;
import com.whosin.app.ui.adapter.raynaTicketAdapter.RaynaTicketImageAdapter;
import com.whosin.app.ui.fragment.home.InviteFriendBottomSheet;


import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.TimeZone;
import java.util.stream.Collectors;

import io.realm.RealmList;
import retrofit2.Call;

public class SearchActivity extends BaseActivity {
    private ActivitySearchBinding binding;
    private List<CommanSearchModel> commanSearch = new ArrayList<>();
    private SearchResultAdapter searchResultAdapter = new SearchResultAdapter();
    private final AllSearchResultAdapter<CommanSearchModel> allSearchResultAdapter = new AllSearchResultAdapter<>();
    private final TicketShimmerEffectAdapter<RatingModel> shimmerEffectAdapter = new TicketShimmerEffectAdapter<>();
    private final SearchHistoryAdapter<SearchHistoryModel> historyAdapter = new SearchHistoryAdapter<>();
    private final SearchTabHistory<RatingModel> searchTabHistory = new SearchTabHistory<>();
    private Call<ContainerListModel<CommanSearchModel>> service = null;
     private SearchHomeBlockAdapter<HomeBlockModel> searchHomeBlockAdapter;
    private HomeObjectModel searchHomeObjectModel;
    private JsonObject searchJsonObject = new JsonObject();
    private Handler handler = new Handler();
    private Handler ticketHandler = new Handler();
    private final Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    private CommanCallback<Boolean> releaseVideoPlayerCallBack;
    private boolean ignoreTextChange = false;
    private Call<ContainerModel<List<String>>> suggestionService = null;

    private SearchPopupAdapter adapter = new SearchPopupAdapter(this);


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------


    @Override
    protected void initUi() {

        binding.tvSearchTitle.setText(getValue("search"));
        binding.tvRecentSearches.setText(getValue("recent_searches"));
        binding.clearAllRecentSearch.setText(getValue("clear_all"));
        binding.edtSearch.setHint(getValue("where_do_you_want_to_go"));
        binding.emptyPlaceHolderView.setEmptyPlaceTxtTitle(getValue("empty_search"));

        binding.searchHomeRecycleView.setLayoutManager( new LinearLayoutManager( activity, LinearLayoutManager.VERTICAL, false ) );
        searchHomeBlockAdapter = new SearchHomeBlockAdapter<>();
        binding.searchHomeRecycleView.setAdapter( searchHomeBlockAdapter );

//        suggestionPopup = new SearchSuggestionPopup(this,binding.edtSearch);

        binding.edtSearch.setAdapter(adapter);
        binding.edtSearch.setDropDownBackgroundResource(R.drawable.dropdown_bg);
        binding.edtSearch.setDropDownAnchor(R.id.edtSearch);
        binding.edtSearch.setDropDownVerticalOffset(0);
        binding.edtSearch.setDropDownHeight(700);


        if (SessionManager.shared.getSearchBlockData() != null){
            searchHomeObjectModel = SessionManager.shared.getSearchBlockData();
            if (searchHomeObjectModel != null && !searchHomeObjectModel.getBlocks().isEmpty()) {
                filterData(searchHomeObjectModel.getBlocks());
            }
        }

        requestSearchGetHomeBlock();

        binding.searchResultRecycleView.setLayoutManager( new LinearLayoutManager( activity, LinearLayoutManager.VERTICAL, false ) );

        binding.searchHistoryRecycler.setLayoutManager( new LinearLayoutManager( activity, LinearLayoutManager.VERTICAL, false ) );
        binding.searchHistoryRecycler.setAdapter( historyAdapter );

        binding.searchTabRecycler.setLayoutManager( new LinearLayoutManager( activity, LinearLayoutManager.HORIZONTAL, false ) );
        binding.searchTabRecycler.setAdapter( searchTabHistory );
        Utils.hideKeyboard( activity );
    }


    @Override
    protected void setListeners() {

        binding.edtSearch.setOnDismissListener(() -> {
            binding.edtSearch.setBackgroundResource(R.drawable.rounded_full_bg_search);
        });

        binding.edtSearch.setOnDismissListener(() -> {
            if (binding.edtSearch.isPerformingCompletion()) {
                ignoreTextChange = true;
            }
        });

        binding.edtSearch.setOnItemClickListener((parent, view, position, id) -> {
            String selectedText = adapter.getItem(position);
            if (!TextUtils.isEmpty(selectedText)) {
                if (searchRunnable != null) {
                    handler.removeCallbacks(searchRunnable);
                }
                addSearchTitle(selectedText);
                ignoreTextChange = true;
                suggestionService.cancel();
                binding.edtSearch.setText(selectedText);
                binding.edtSearch.dismissDropDown();
                binding.edtSearch.setSelection( binding.edtSearch.getText().length() );
                Utils.hideKeyboard( activity );
                binding.edtSearch.setBackgroundResource(R.drawable.rounded_full_bg_search);
                requestCommanSearch(selectedText);
                ignoreTextChange = false;
            }
        });

        binding.editLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(binding.edtSearch.getText().toString())){
                    showRecentSearch();
                }
                binding.tvCancel.setVisibility( View.VISIBLE );
                binding.edtSearch.setCursorVisible(true);
                binding.edtSearch.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.showSoftInput(binding.edtSearch, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        });

        binding.tvCancel.setOnClickListener( view -> {
            searchHandler.removeCallbacks(searchRunnable);
            binding.edtSearch.setBackgroundResource(R.drawable.rounded_full_bg_search);
            ignoreTextChange = true;
            binding.edtSearch.clearFocus();
            binding.edtSearch.getText().clear();
            binding.tvCancel.setVisibility( View.GONE );
            binding.edtSearch.setCursorVisible(false);
            // Make other views visible after the animation
            binding.recentSearchLinear.setVisibility( View.GONE );
            binding.searchTypeLayout.setVisibility(View.GONE);
            binding.headerConstraint.setVisibility( View.VISIBLE );
            binding.searchHomeRecycleView.setVisibility( View.VISIBLE );
            binding.emptyPlaceHolderView.setVisibility( View.GONE );
            Utils.hideKeyboard( activity );
            binding.edtSearch.post(() -> ignoreTextChange = false);
        } );

        binding.ivBack.setOnClickListener( view -> activity.onBackPressed() );

        binding.clearAllRecentSearch.setOnClickListener( view -> {
            clearSearchText();
            SearchHistoryModel.clearHistory();
            binding.recentSearchLinear.setVisibility( View.GONE );
            binding.emptyPlaceHolderView.setVisibility( View.VISIBLE );

        } );

        binding.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (binding.edtSearch.isPerformingCompletion()) {
                    return;
                }
                if (ignoreTextChange) return;
                final String searchText = s.toString().trim();

                if (searchRunnable != null) {
                    handler.removeCallbacks(searchRunnable);
                }

                if (!TextUtils.isEmpty(searchText)){
                    List<String> list = SearchSuggestionStore.get(binding.edtSearch.getText().toString());
                    if (!list.isEmpty()) {
                        adapter.updateItems(list);
                        binding.edtSearch.setBackgroundResource(R.drawable.rounded_top_only_bg);
                    }else {
                        binding.edtSearch.dismissDropDown();
                        binding.edtSearch.setBackgroundResource(R.drawable.rounded_full_bg_search );
                    }
                    searchRunnable = () -> requestRaynaSearchSuggestions();
                    handler.postDelayed(searchRunnable, 300);
                }else {
                    binding.edtSearch.setBackgroundResource(R.drawable.rounded_full_bg_search);
                    showRecentSearch();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString())){
                    binding.edtSearch.setBackgroundResource(R.drawable.rounded_full_bg_search);
                }
            }
        });

        binding.edtSearch.setOnFocusChangeListener( (view, b) -> {
            if (b) {
                if (TextUtils.isEmpty(binding.edtSearch.getText().toString())){
                    showRecentSearch();
                    playPauseVideos(false);
                    binding.tvCancel.setVisibility( View.VISIBLE );
                    binding.edtSearch.setCursorVisible(true);
                }
            }
        } );

        binding.edtSearch.setOnEditorActionListener( (v, actionId, event) -> {
            Utils.preventDoubleClick(v);
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String searchQuery = binding.edtSearch.getText().toString();
                if (!searchQuery.isEmpty()) {
                    requestCommanSearch(searchQuery);
                    addSearchTitle( searchQuery );
                    Utils.hideKeyboard( v, activity );
                    return true;
                }
            }
            return false;
        } );

        RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (layoutManager != null) {
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                    int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();

                    for (int i = firstVisibleItemPosition; i <= lastVisibleItemPosition; i++) {
                        RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(i);
                        if (viewHolder instanceof SearchHomeBlockAdapter.HomeAdHolder) {
                            View itemView = layoutManager.findViewByPosition(i);
                            if (itemView != null) {
                                int itemHeight = itemView.getHeight();
                                int visibleHeight = Math.min(recyclerView.getHeight(), itemView.getBottom()) - Math.max(0, itemView.getTop());
                                double visiblePercentage = (double) visibleHeight / itemHeight;

                                SearchHomeBlockAdapter.HomeAdHolder newVideoViewHolder = (SearchHomeBlockAdapter.HomeAdHolder) viewHolder;
                                newVideoViewHolder.onItemVisibilityChanged(visiblePercentage >= 0.7);
                            }
                        }
                    }
                }
            }
        };

        RecyclerView.OnScrollListener scrollListenerForSearchResult = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (layoutManager != null) {
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                    int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();

                    for (int i = firstVisibleItemPosition; i <= lastVisibleItemPosition; i++) {
                        RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(i);
                        if (viewHolder instanceof SearchResultAdapter.HomeAdHolder) {
                            View itemView = layoutManager.findViewByPosition(i);
                            if (itemView != null) {
                                int itemHeight = itemView.getHeight();
                                int visibleHeight = Math.min(recyclerView.getHeight(), itemView.getBottom()) - Math.max(0, itemView.getTop());
                                double visiblePercentage = (double) visibleHeight / itemHeight;

                                SearchResultAdapter.HomeAdHolder newVideoViewHolder = (SearchResultAdapter.HomeAdHolder) viewHolder;
                                newVideoViewHolder.onItemVisibilityChanged(visiblePercentage >= 0.7);
                            }
                        }
                    }
                }
            }
        };

        binding.searchHomeRecycleView.addOnScrollListener(scrollListener);
        binding.searchResultRecycleView.addOnScrollListener(scrollListenerForSearchResult);

    }


    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivitySearchBinding.inflate( getLayoutInflater() );
        return binding.getRoot();

    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void playPauseVideos(boolean isPlay) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) binding.searchHomeRecycleView.getLayoutManager();
        if (layoutManager != null) {
            int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
            int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();

            for (int i = firstVisibleItemPosition; i <= lastVisibleItemPosition; i++) {
                RecyclerView.ViewHolder viewHolder = binding.searchHomeRecycleView.findViewHolderForAdapterPosition(i);
                if (viewHolder instanceof SearchHomeBlockAdapter.HomeAdHolder) {
                    SearchHomeBlockAdapter.HomeAdHolder homeAdHolder = (SearchHomeBlockAdapter.HomeAdHolder) viewHolder;
                    if (isPlay) {
                        View itemView = layoutManager.findViewByPosition(i);
                        if (itemView != null) {
                            int itemHeight = itemView.getHeight();
                            int visibleHeight = Math.min(binding.searchHomeRecycleView.getHeight(), itemView.getBottom()) - Math.max(0, itemView.getTop());
                            double visiblePercentage = (double) visibleHeight / itemHeight;
                            if (visiblePercentage >= 0.6) {
                                homeAdHolder.onItemVisibilityChanged(true);
                            }
                        }
                    } else {
                        homeAdHolder.onItemVisibilityChanged(false);
                    }
                }

            }
        }
    }

    private void showRecentSearch() {
        allSearchResultAdapter.clearAdapter();
        searchResultAdapter.clearAdapter();
        binding.emptyPlaceHolderView.setVisibility( View.GONE );
        List<SearchHistoryModel> history = SearchHistoryModel.getHistory();
        List<RatingModel> search = getHistory();

        binding.headerConstraint.setVisibility( View.GONE );
        binding.searchHomeRecycleView.setVisibility( View.GONE );
        binding.recentSearchLinear.setVisibility( View.VISIBLE );
        binding.tvCancel.setVisibility( View.VISIBLE );
        binding.edtSearch.setCursorVisible(true);

        if ((history == null || history.isEmpty()) && (search == null || search.isEmpty())) {
            binding.recentSearchLinear.setVisibility( View.GONE );
            binding.emptyPlaceHolderView.setVisibility( View.VISIBLE );
            return;
        }

        binding.ilRecent.setVisibility( View.VISIBLE );


        if (!CollectionUtils.isEmpty( history )) {
            historyAdapter.updateData( history );
        } else {
            binding.ilRecent.setVisibility( View.GONE );
        }

        if (!CollectionUtils.isEmpty( search )) {
            searchTabHistory.updateData( search );
        } else {
            binding.searchTabRecycler.setVisibility( View.GONE );
        }

    }

    private void setTabTitle(List<CommanSearchModel> commanSearch, String selectedCategory) {

        CommanSearchModel ticketItem = null;

        Iterator<CommanSearchModel> iterator = commanSearch.iterator();
        while (iterator.hasNext()) {
            CommanSearchModel model = iterator.next();
            if ("ticket".equalsIgnoreCase(model.getType())) {
                ticketItem = model;
                iterator.remove();
                break;
            }
        }

        // Add it to the beginning if found
        if (ticketItem != null) {
            commanSearch.add(0, ticketItem);
        }

        binding.tabLayout.removeAllTabs();

        TabLayout.Tab allTab = binding.tabLayout.newTab().setText( "ALL" );
        binding.tabLayout.addTab( allTab );

        TabLayout.Tab ticketTab = null;
        List<TabLayout.Tab> otherTabs = new ArrayList<>();


        for (CommanSearchModel commanSearchModel : commanSearch) {
            String capitalizedTitle = commanSearchModel.getType().substring(0, 1).toUpperCase() + commanSearchModel.getType().substring(1);
            TabLayout.Tab tab = binding.tabLayout.newTab().setText(capitalizedTitle);

            if (capitalizedTitle.equalsIgnoreCase("Ticket")) {
                ticketTab = tab;
            } else {
                otherTabs.add(tab);
            }
        }


        if (ticketTab != null) {
            binding.tabLayout.addTab(ticketTab);
        }

        for (TabLayout.Tab tab : otherTabs) {
            binding.tabLayout.addTab(tab);
        }

//        commanSearch.forEach( commanSearchModel -> {
//            String capitalizedTitle = commanSearchModel.getType().substring( 0, 1 ).toUpperCase() + commanSearchModel.getType().substring( 1 );
//            TabLayout.Tab tab = binding.tabLayout.newTab().setText( capitalizedTitle );
//            binding.tabLayout.addTab( tab );
//        } );

        if (selectedCategory.equals( "All" )) {
            binding.searchResultRecycleView.setAdapter( allSearchResultAdapter );
            allSearchResultAdapter.updateData( commanSearch );
            Log.d( "DataLoad", "onTabSelected: " + true );
        }

        int selectedTabIndex = -1;
        if (!selectedCategory.isEmpty()) {
            for (int i = 0; i < commanSearch.size(); i++) {
                if (commanSearch.get( i ).getType().equalsIgnoreCase( selectedCategory )) {
                    selectedTabIndex = i + 1; // Add 1 to account for the "ALL" tab
                    break;
                }
            }
        }

        if (!selectedCategory.isEmpty()) {
            if (selectedTabIndex != -1) {
                binding.tabLayout.getTabAt( selectedTabIndex ).select();
            }
        }

        binding.tabLayout.addOnTabSelectedListener( new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                String selectedTabText = tab.getText().toString();
                String modifiedTabText = Character.toLowerCase( selectedTabText.charAt( 0 ) ) + selectedTabText.substring( 1 );

                CommanSearchModel result = commanSearch.stream().filter( p -> p.getType().equalsIgnoreCase( modifiedTabText ) ).findFirst().orElse( null );


                if (result != null) {
                    binding.searchResultRecycleView.setAdapter( searchResultAdapter );

                    List<Object> mixedList = null;

                    if (result.getTicket() != null && !result.getTicket().isEmpty()) {
                        mixedList = new ArrayList<>(result.getTicket());
                    } else if (result.getVenues() != null && !result.getVenues().isEmpty()) {
                        mixedList = new ArrayList<>(result.getVenues());
                    } else if (result.getUsers() != null && !result.getUsers().isEmpty()) {
                        mixedList = new ArrayList<>(result.getUsers());
                    } else if (result.getOffers() != null && !result.getOffers().isEmpty()) {
                        mixedList = new ArrayList<>(result.getOffers());
                    } else if (result.getEvents() != null && !result.getEvents().isEmpty()) {
                        mixedList = new ArrayList<>(result.getEvents());
                    } else if (result.getActivity() != null && !result.getActivity().isEmpty()) {
                        mixedList = new ArrayList<>(result.getActivity());
                    }

                    if (mixedList != null) {
                        int insertIndex = Math.min(mixedList.size(), 5);
                        mixedList.add(insertIndex, new BannerModel());
                        searchResultAdapter.updateData(mixedList);
                    }


                } else {
                    binding.searchResultRecycleView.setAdapter( allSearchResultAdapter );
                    allSearchResultAdapter.updateData( commanSearch );
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        } );
    }

    private static void addSearchTitle(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            return;
        }

        List<RatingModel> existingHistory = getHistory();
        if (existingHistory == null) {
            existingHistory = new ArrayList<>();

        }

        boolean termExists = false;
        for (RatingModel model : existingHistory) {
            if (model.getImage().equals( searchText )) {
                termExists = true;
                break;
            }
        }

        if (!termExists) {

            if (existingHistory.size() > 9) {
                existingHistory.remove( existingHistory.size() - 1 );
            }

            RatingModel newModel = new RatingModel( searchText );
            newModel.setImage( searchText );
            existingHistory.add( 0, newModel );

            String json = new Gson().toJson( existingHistory );
            Preferences.shared.setString( "search_text", json );
        }
    }

    private static List<RatingModel> getHistory() {
        String json = Preferences.shared.getString( "search_text" );
        if (json != null) {
            try {
                Type type = new TypeToken<List<RatingModel>>() {
                }.getType();
                return new Gson().fromJson( json, type );

            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>();
    }

    private static void clearSearchText() {
        Preferences.shared.setString( "search_text", "" );
    }


    private void setItemWidth(int itemCount, View view) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width = (int) (Graphics.getScreenWidth(activity) * (itemCount > 1 ? 0.89 : 0.93));
        view.setLayoutParams(params);
    }

    private void filterData(List<HomeBlockModel> searchHomeBlocks) {
        // TODO : type of list will remove totally from home-block
        Thread backgroundThread = new Thread(() -> {
            List<String> removeTypes = Arrays.asList("nearby", "video");
            searchHomeBlocks.removeIf(model -> removeTypes.contains(model.getType()));
            searchHomeBlocks.removeIf(model -> {
                switch (model.getType()) {
                    case "video":
                        List<VideoComponentModel> videoList = model.getVideos().stream().filter(m -> {
                            if (TextUtils.isEmpty(m.getVenueId())) {
                                return false;
                            } else {
                                Optional<VenueObjectModel> venueObjectModel = SessionManager.shared.geHomeBlockData().getVenues().stream().filter(p -> p.getId().equals(m.getVenueId())).findFirst();
                                venueObjectModel.ifPresent(m::setVenue);
                                return true;
                            }
                        }).collect(Collectors.toList());
                        return videoList.isEmpty();
                    case "activity":
                        List<ActivityDetailModel> activityList = searchHomeObjectModel.getActivities().stream().filter(p -> model.getActivities() != null && model.getActivities().contains(p.getId())).collect(Collectors.toList());
                        model.activityList = activityList;
                        return activityList.isEmpty();
                    case "deal":
                        return (model.getDeals() == null || model.getDeals().isEmpty());
                    case "custom-offer":
                        return ((model.getCustomOffers() == null || model.getCustomOffers().isEmpty()) || (searchHomeObjectModel.getOffers().stream().filter(p -> model.getOffers() != null && model.getOffers().contains(p.getId())).collect(Collectors.toList()).isEmpty()));
                    case "custom-venue":
                        return (model.getCustomVenues() == null || model.getCustomVenues().isEmpty());
                    case "event":
                        List<EventModel> events = searchHomeObjectModel.getEvents().stream().filter(p -> model.getEvents().contains(p.getId()) && p.getCustomVenue() != null ).collect(Collectors.toList());
                        model.eventList = events;
                        return events.isEmpty();
                    case "stories":
                        return (model.getStories() == null || model.getStories().isEmpty());
                    case "suggested-venues":
                        return (model.getSuggestedVenue() == null || model.getSuggestedVenue().isEmpty());
                    case "categories":
                        return (model.getCategories() == null || model.getCategories().isEmpty());
                    case "my-outing":
                        if(model.getMyOuting().isEmpty()) { return true; }
                        List<InviteFriendModel> filterData = model.getMyOuting().stream().filter(p -> p.getVenue() != null && p.getUser() != null).collect(Collectors.toList());
                        return filterData.isEmpty();
                    case "venue":
                        if (model.getVenues().isEmpty()) { return true; }
                        List<VenueObjectModel> venues = new ArrayList<>();
                        model.getVenues().forEach( p -> {
                            Optional<VenueObjectModel> venue = searchHomeObjectModel.getVenues().stream().filter(v -> v.getId().equalsIgnoreCase(p)).findFirst();
                            venue.ifPresent(venues::add);
                        });
                        model.venueList = venues;
                        return venues.isEmpty();
                    case "custom-components":
                        return (model.getCustomComponentModelList() == null || model.getCustomComponentModelList().isEmpty());
                    case "offer":
                        if (model.getOffers().isEmpty()) { return true; }
                        List<OffersModel> offers = new ArrayList<>(); //homeObjectModel.getOffers().stream().filter(p -> model.getOffers() != null && model.getOffers().contains(p.getId())).collect(Collectors.toList());
                        model.getOffers().forEach(p -> {
                            Optional<OffersModel> offer = searchHomeObjectModel.getOffers().stream().filter(v -> v.getId().equalsIgnoreCase(p)).findFirst();
                            offer.ifPresent(offers::add);
                        });
                        model.offerList = offers;
                        return offers.isEmpty();
                    case "ticket":
                        if (model.getTickets() == null || model.getTickets().isEmpty()) { return true; }
                        List<RaynaTicketDetailModel> tmpTicket = new ArrayList<>();
                        model.getTickets().forEach(p -> {
                            if (SessionManager.shared.geHomeBlockData() != null) {
                                Optional<RaynaTicketDetailModel> ticket = searchHomeObjectModel.getTickets().stream().filter(v -> v.getId().equals(p)).findFirst();
                                ticket.ifPresent(tmpTicket::add);
                            }
                        });
                        model.ticketList = tmpTicket;
                        return tmpTicket.isEmpty();
                    default:
                        return false;
                }
            });


            AppExecutors.get().mainThread().execute(() -> {
                if (!searchHomeBlocks.isEmpty() && !binding.edtSearch.hasFocus()) {

                    if (searchHomeBlocks != null && !searchHomeBlocks.isEmpty()) {
                        HomeBlockModel completeProfileBanner = new HomeBlockModel();
                        completeProfileBanner.setType(AppConstants.ADTYPE);

                        int insertIndex;
                        if (searchHomeBlocks.size() > 5) {
                            insertIndex = 5;
                        } else {
                            insertIndex = searchHomeBlocks.size();
                        }

                        searchHomeBlocks.add(insertIndex, completeProfileBanner);
                    }


                    if (binding.tvCancel.getVisibility() == View.GONE){
                        searchHomeBlockAdapter.updateData(searchHomeBlocks);
                    }

                } else {
                    binding.searchHomeRecycleView.setVisibility(View.GONE);
                }
            });
        });
        backgroundThread.start();
    }

    private void setVenueInfo(String venueId, UserInfoView infoView) {
        Thread backgroundThread = new Thread(() -> {
            Optional<VenueObjectModel> venueObjectModel = searchHomeObjectModel.getVenues().stream().filter(p -> p.getId().equals(venueId)).findFirst();
            venueObjectModel.ifPresent(objectModel -> AppExecutors.get().mainThread().execute(() -> {
                infoView.setVenueDetail(objectModel);
            }));
        });
        backgroundThread.start();
    }

    private void setupRecycleHorizontalManager(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        int spacing = getResources().getDimensionPixelSize(com.intuit.ssp.R.dimen._10ssp);
        recyclerView.addItemDecoration(new HorizontalSpaceItemDecoration(spacing));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        recyclerView.offsetChildrenHorizontal(1);
        recyclerView.offsetChildrenVertical(1);
    }


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------


    private void requestCommanSearch(String query) {
        // Cancel previous service request if it exists
        if (service != null) {
            service.cancel();
        }
        if (suggestionService != null){
            suggestionService.cancel();
        }

        // Update searchJsonObject efficiently
        searchJsonObject.remove("query");
        if (!TextUtils.isEmpty(query)) {
            searchJsonObject.addProperty("query", query);
        }

        // Update UI visibility
        binding.edtSearch.setBackgroundResource(R.drawable.rounded_full_bg_search);
        binding.searchTypeLayout.setVisibility(View.VISIBLE);
        binding.recentSearchLinear.setVisibility(View.GONE);
        binding.tabViewLine.setVisibility(View.GONE);
        binding.tabLayout.setVisibility(View.GONE);
        binding.emptyPlaceHolderView.setVisibility(View.GONE);
        binding.searchResultRecycleView.setVisibility(View.VISIBLE);
        List<RatingModel> dummyList = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            RatingModel rating = new RatingModel();
            dummyList.add(rating);
        }
        shimmerEffectAdapter.updateData(dummyList);
        binding.searchResultRecycleView.setAdapter(shimmerEffectAdapter);


        service = DataService.shared(activity).requestCommanSearch(searchJsonObject,
                new RestCallback<ContainerListModel<CommanSearchModel>>(this) {
                    @Override
                    public void result(ContainerListModel<CommanSearchModel> model, String error) {
                        // Handle error or null model
                        if (!Utils.isNullOrEmpty(error) || model == null) {
                            Toast.makeText(activity, R.string.service_message_something_wrong, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Clear previous data
                        if (commanSearch != null) {
                            commanSearch.clear();
                            binding.tabLayout.removeAllTabs();
                        }
                        binding.searchHomeRecycleView.setVisibility(View.GONE);

                        // Handle empty or null data
                        if (model.data == null || model.data.isEmpty()) {
                            binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
                            binding.tabLayout.setVisibility(View.GONE);
                            binding.tabViewLine.setVisibility(View.GONE);
                            binding.searchResultRecycleView.setVisibility(View.GONE);
                            return;
                        }

                        // Filter out yacht type in-place
                        model.data.removeIf(p -> "yacht".equals(p.getType()));
                        commanSearch.addAll(model.data);

                        // Check if all items are of the same type
                        String firstType = model.data.isEmpty() ? null : model.data.get(0).getType();
                        boolean allSameType = firstType != null && model.data.stream()
                                .allMatch(p -> firstType.equals(p.getType()));

                        if (allSameType && !model.data.isEmpty()) {
                            binding.searchResultRecycleView.setVisibility(View.VISIBLE);
                            binding.emptyPlaceHolderView.setVisibility(View.GONE);
                            binding.tabLayout.setVisibility(View.GONE);
                            binding.tabViewLine.setVisibility(View.GONE);

                            CommanSearchModel result = model.data.get(0);
                            List<Object> mixedList = getNonEmptyList(result);

                            if (mixedList != null) {
                                int insertIndex = Math.min(mixedList.size(), 5);
                                mixedList.add(insertIndex, new BannerModel());
                                searchResultAdapter.updateData(mixedList);
                                binding.searchResultRecycleView.setAdapter(searchResultAdapter);
                            }
                        } else {
                            binding.searchResultRecycleView.setVisibility(View.VISIBLE);
                            binding.emptyPlaceHolderView.setVisibility(View.GONE);
                            binding.tabLayout.setVisibility(View.VISIBLE);
                            binding.tabViewLine.setVisibility(View.VISIBLE);
                            setTabTitle(commanSearch, "All");
                        }
                    }

                    // Helper method to get the first non-empty list
                    private List<Object> getNonEmptyList(CommanSearchModel result) {
                        if (result == null) return null;
                        if (result.getTicket() != null && !result.getTicket().isEmpty()) return new ArrayList<>(result.getTicket());
                        if (result.getVenues() != null && !result.getVenues().isEmpty()) return new ArrayList<>(result.getVenues());
                        if (result.getUsers() != null && !result.getUsers().isEmpty()) return new ArrayList<>(result.getUsers());
                        if (result.getOffers() != null && !result.getOffers().isEmpty()) return new ArrayList<>(result.getOffers());
                        if (result.getEvents() != null && !result.getEvents().isEmpty()) return new ArrayList<>(result.getEvents());
                        if (result.getActivity() != null && !result.getActivity().isEmpty()) return new ArrayList<>(result.getActivity());
                        return null;
                    }
                });
    }

    private void requestRaynaSearchSuggestions() {
        if (suggestionService != null) {
            suggestionService.cancel();
        }
        suggestionService = DataService.shared(activity).requestRaynaSearchSuggestions(binding.edtSearch.getText().toString(),new RestCallback<ContainerModel<List<String>>>(this) {
            @Override
            public void result(ContainerModel<List<String>> model, String error) {
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    return;
                }

                if (model.getData() != null && !model.getData().isEmpty()) {
                    SearchSuggestionStore.save(binding.edtSearch.getText().toString(),model.data);
                    if (ignoreTextChange) return;
                    adapter.updateItems(model.getData());
                    if (!model.getData().isEmpty()) {
                        binding.edtSearch.setBackgroundResource(R.drawable.rounded_top_only_bg);
                    } else {
                        binding.edtSearch.setBackgroundResource(R.drawable.rounded_full_bg_search);
                    }
                }else {
                    if (!binding.edtSearch.isPopupShowing()){
                        binding.edtSearch.setBackgroundResource(R.drawable.rounded_full_bg_search);
                    }else {
                        binding.edtSearch.setBackgroundResource(R.drawable.rounded_top_only_bg);
                    }
                }
            }
        });
    }

    private void reqOfferFollowUnFollow(String id, BooleanResult callBack) {
        DataService.shared(activity).requestVenueFollow(id, new RestCallback<ContainerModel<FollowUnfollowModel>>(this) {
            @Override
            public void result(ContainerModel<FollowUnfollowModel> model, String error) {

                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, R.string.service_message_something_wrong, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (model.message.equals("Unfollowed!")) {
                    callBack.success(false, "");
                } else {
                    callBack.success(true, "");
                }

            }
        });
    }

    private void reqRecommendation(String id, VenueObjectModel venueModel) {
        showProgress();
        DataService.shared(activity).requestFeedRecommandation(id, "venue", new RestCallback<ContainerModel<UserDetailModel>>(this) {
            @Override
            public void result(ContainerModel<UserDetailModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, R.string.service_message_something_wrong, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (model.message.equals( "recommendation added successfully!" )) {
                    venueModel.setRecommendation(true);
                    Alerter.create(activity).setTitle("Thank you!").setTitleAppearance(R.style.AlerterTitle).setTextAppearance(R.style.AlerterText).setText("for recommended " + venueModel.getName() + "to your friends!").setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                } else {
                    venueModel.setRecommendation(false);
                    Alerter.create(activity).setTitle("Oh Snap!!").setTitleAppearance(R.style.AlerterTitle).setTextAppearance(R.style.AlerterText).setText("you have removed recommendation of " + venueModel.getName()).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                }

                searchResultAdapter.notifyDataSetChanged();
                allSearchResultAdapter.notifyDataSetChanged();

            }
        });
    }

    private void requestSearchGetHomeBlock() {
        if (searchHomeBlockAdapter.getData() == null && searchHomeBlockAdapter.getData().isEmpty()){
            showProgress();
        }
        DataService.shared(activity).requestSearchGetHomeBlock(new RestCallback<ContainerModel<HomeObjectModel>>(this) {
            @Override
            public void result(ContainerModel<HomeObjectModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (model.getData() != null) {
                    SessionManager.shared.saveSearchBlockData(model.getData());
                    searchHomeObjectModel = model.getData();
                }

                if (searchHomeObjectModel != null && !searchHomeObjectModel.getBlocks().isEmpty()) {
                    filterData(searchHomeObjectModel.getBlocks());
                }

                boolean isEmpty = searchHomeObjectModel == null || searchHomeObjectModel.getBlocks() == null || searchHomeObjectModel.getBlocks().isEmpty();
                binding.emptyPlaceHolderView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
                binding.searchHomeRecycleView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);

            }
        });
    }


    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------



    // endregion
    // --------------------------------------
    // region Home Search Adapter
    // --------------------------------------


    // -------------------------------------   Home Search Adapter ------------------------------------ //

    public class SearchHomeBlockAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            switch (AppConstants.HomeBlockType.valueOf(viewType)) {
                case OFFER_SMALL:
                case OFFER_LARGE:
                    return new LargeOfferBlockHolder(UiUtils.getViewBy(parent, R.layout.item_large_offer_block));
                case VENUE_SMALL:
                    return new SmallVenueBlockHolder(UiUtils.getViewBy(parent, R.layout.item_special_venue_layout));
                case CUSTOM_OFFER:
                    return new CustomOfferHolder(UiUtils.getViewBy(parent, R.layout.item_large_container_compentent));
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
                case TICKET:
                    return new TicketHolder(UiUtils.getViewBy(parent, R.layout.item_ticket_recycler));
                case HOME_AD:
                    return new HomeAdHolder(UiUtils.getViewBy(parent, R.layout.item_home_ad_view));
                default:
                    return new LargeVenueBlockHolder(UiUtils.getViewBy(parent, R.layout.item_large_venue_comoponent_recycler));
            }

        }

        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            HomeBlockModel model = (HomeBlockModel) getItem(position);
            boolean isLastItem = position == getItemCount() - 1;
            if (model.getBlockType() == AppConstants.HomeBlockType.VENUE_SMALL) {
                SmallVenueBlockHolder venueViewHolder = (SmallVenueBlockHolder) holder;
                venueViewHolder.setupData(model.venueList);
                venueViewHolder.mBinding.txtTitle.setText(model.getTitle());
                venueViewHolder.mBinding.seeAll.setOnClickListener( view -> {
                    List<VenueObjectModel> list = model.venueList;
                    startActivity( new Intent( activity, SmallVenueComponentSeeAllActivity.class )
                            .putExtra( "venueModel", new Gson().toJson( list ) )
                            .putExtra( "title",model.getTitle() ));
                } );
            }
            else if (model.getBlockType() == AppConstants.HomeBlockType.VENUE_LARGE) {
                LargeVenueBlockHolder viewHolder = (LargeVenueBlockHolder) holder;
                viewHolder.mBinding.venueListView.setupData(model.venueList, SearchActivity.this, getSupportFragmentManager());
                viewHolder.mBinding.txtTitle.setText(model.getTitle());
                viewHolder.mBinding.txtSubTitle.setText(model.getDescription());
            }
            else if (model.getBlockType() == AppConstants.HomeBlockType.OFFER_LARGE || model.getBlockType() == AppConstants.HomeBlockType.OFFER_SMALL) {
                LargeOfferBlockHolder viewHolder = (LargeOfferBlockHolder) holder;
                viewHolder.mBinding.txtTitle.setText(model.getTitle());
                viewHolder.mBinding.txtSubTitle.setText(model.getDescription());
                viewHolder.mBinding.offerListView.setupData(model.offerList, SearchActivity.this, getSupportFragmentManager());
//            viewHolder.setupData(model.offerList);
            }
            else if (model.getBlockType() == AppConstants.HomeBlockType.CUSTOM_VENUE) {
                LargeContainerHolder viewHolder = (LargeContainerHolder) holder;
                model.getCustomVenues().removeIf(p -> searchHomeObjectModel.getVenues().stream().noneMatch(o -> o.getId().equals(p.getVenueId())));
                viewHolder.mBinding.customVenueView.setupData(model.getCustomVenues(), SearchActivity.this, getSupportFragmentManager());
            }
            else if (model.getBlockType() == AppConstants.HomeBlockType.CUSTOM_OFFER) {
                CustomOfferHolder viewHolder = (CustomOfferHolder) holder;
                viewHolder.mBinding.offerView.setupData(model.getCustomOffers(), SearchActivity.this, getSupportFragmentManager());
//            viewHolder.setupData(model.getCustomOffers());
            } else if (model.getBlockType() == AppConstants.HomeBlockType.CUSTOM_COMPOMENTS) {
                CustomComponentBlockHolder componentHolder = (CustomComponentBlockHolder) holder;
                componentHolder.setupData(model);
            } else if (model.getBlockType() == AppConstants.HomeBlockType.DEALS) {
                ExclusiveDealBlockHolder exclusiveDealViewHolder = (ExclusiveDealBlockHolder) holder;
                exclusiveDealViewHolder.setupData(model.getDeals());
                exclusiveDealViewHolder.mBinding.txtTitle.setText(model.getTitle());
                exclusiveDealViewHolder.mBinding.txtSubTitle.setText(model.getDescription());
            } else if (model.getBlockType() == AppConstants.HomeBlockType.CATEGORIES) {
                CategoriesBlockHolder categoriesContainerHolder = (CategoriesBlockHolder) holder;
                categoriesContainerHolder.setupData(model.getHomeBlockCategory());
            }
            else if (model.getBlockType() == AppConstants.HomeBlockType.ACTIVITIES) {
                TestActivityBlockHolder testActivityBlockHolder = (TestActivityBlockHolder) holder;
                testActivityBlockHolder.setupData(model.activityList);
                testActivityBlockHolder.mBinding.txtTitle.setText(model.getTitle());
                testActivityBlockHolder.mBinding.txtSubTitle.setText(model.getDescription());
            } else if (model.getBlockType() == AppConstants.HomeBlockType.EVENTS) {
                EventBlockHolder eventBlockHolder = (EventBlockHolder) holder;
//            eventBlockHolder.setupData(model.eventList);
                eventBlockHolder.mBinding.eventListView.setupData(model.eventList, SearchActivity.this, getSupportFragmentManager());
                eventBlockHolder.mBinding.txtTitle.setText(model.getTitle());
                eventBlockHolder.mBinding.txtSubTitle.setText(model.getDescription());
            }
            else if (model.getBlockType() == AppConstants.HomeBlockType.MY_OUTING) {
                MyOutingBlockHolder myOutingBlockHolder = (MyOutingBlockHolder) holder;
                myOutingBlockHolder.setupData(model.getMyOuting());
                myOutingBlockHolder.mBinding.txtTitle.setText(model.getTitle());
                myOutingBlockHolder.mBinding.txtSubTitle.setText(model.getDescription());
            } else if (model.getBlockType() == AppConstants.HomeBlockType.SUGGESTED_USERS) {
                SuggestedUserBlockHolder suggestedUserBlockHolder = (SuggestedUserBlockHolder) holder;
                suggestedUserBlockHolder.setupData(model.getSuggestedUsers());
                suggestedUserBlockHolder.mBinding.txtTitle.setText(model.getTitle());
            } else if (model.getBlockType() == AppConstants.HomeBlockType.VENUE_SUGGESTION) {
                SuggestedVenueViewHolder suggestedVenueViewHolder = (SuggestedVenueViewHolder) holder;
                suggestedVenueViewHolder.setupData(model.getSuggestedVenue());
                suggestedVenueViewHolder.mBinding.txtTitle.setText(model.getTitle());
            }else if (model.getBlockType() == AppConstants.HomeBlockType.TICKET) {
                TicketHolder viewHolder = (TicketHolder) holder;
                viewHolder.binding.userTitle.setText(model.getTitle());
                viewHolder.binding.description.setText(model.getDescription());
                viewHolder.setupData(model.getTicketList(),model.getType());
            }else if (model.getBlockType() == AppConstants.HomeBlockType.HOME_AD) {
                HomeAdHolder viewHolder = (HomeAdHolder) holder;
                viewHolder.mBinding.adView.setupAppLifeCycleCallback(holder.itemView,SearchActivity.class);
                viewHolder.setupData();
            }


            if (isLastItem) {
                int marginBottom = Utils.getMarginBottom(holder.itemView.getContext(), 0.10f);
                Utils.setBottomMargin(holder.itemView, marginBottom);
            } else {
                Utils.setBottomMargin(holder.itemView, 0);
            }
        }

        @Override
        public int getItemViewType(int position) {
            HomeBlockModel model = (HomeBlockModel) getItem(position);
            return model.getBlockType().getValue();
        }

        public class HomeAdHolder extends RecyclerView.ViewHolder {

            private final ItemHomeAdViewBinding mBinding;

            public HomeAdHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemHomeAdViewBinding.bind(itemView);

            }

            public void setupData() {
                activity.runOnUiThread(() -> {
                    mBinding.adView.activity = activity;
                    mBinding.adView.seUpData(activity);
                });
            }

            public void onItemVisibilityChanged(boolean isVisible) {
                mBinding.adView.onItemVisibilityChanged(isVisible);
            }

        }

        public class LargeContainerHolder extends RecyclerView.ViewHolder {
            private final ItemLargeContainerCompententBinding mBinding;
//        private final CustomVenueAdapter<CustomVenueModel> adapter = new CustomVenueAdapter<>();

            public LargeContainerHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemLargeContainerCompententBinding.bind(itemView);
//            setupRecycleHorizontalManager(mBinding.recyclerView);
//            mBinding.recyclerView.setAdapter(adapter);
            }

//        public void setupData(List<CustomVenueModel> customVenues) {
//            customVenues.removeIf(p -> searchHomeObjectModel.getVenues().stream().noneMatch(o -> o.getId().equals(p.getVenueId())));
//            adapter.updateData(customVenues);
//        }
        }

        public class CustomOfferHolder extends RecyclerView.ViewHolder {
            private final ItemCustomOfferContainerComponentBinding mBinding;
//        private final CustomOfferAdapter<CustomVenueModel> adapter = new CustomOfferAdapter<>();

            public CustomOfferHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemCustomOfferContainerComponentBinding.bind(itemView);
//            setupRecycleHorizontalManager(mBinding.recyclerView);
//            mBinding.recyclerView.setAdapter(adapter);
            }

//        public void setupData(List<CustomVenueModel> customVenues) {
//            adapter.updateData(customVenues);
//        }
        }

        public class CategoriesBlockHolder extends RecyclerView.ViewHolder {
            private final ItemCategoriesContainerCompenetentBinding mBinding;
            private final CategoriesAdapter<CategoriesModel> adapter = new CategoriesAdapter<>();

            public CategoriesBlockHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemCategoriesContainerCompenetentBinding.bind(itemView);
                setupRecycleHorizontalManager(mBinding.categoriesRecycler);
                mBinding.categoriesRecycler.setAdapter(adapter);
            }

            public void setupData(List<CategoriesModel> categoriesList) {
                adapter.updateData(categoriesList);
            }
        }

        public class SmallVenueBlockHolder extends RecyclerView.ViewHolder {
            private final ItemSpecialVenueLayoutBinding mBinding;
            private final SmallVenueBlockAdapter<VenueObjectModel> adapter = new SmallVenueBlockAdapter<>();

            public SmallVenueBlockHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemSpecialVenueLayoutBinding.bind(itemView);
                mBinding.venueRecycler.setLayoutManager(new GridLayoutManager(activity, 4, LinearLayoutManager.HORIZONTAL, false));
                mBinding.venueRecycler.setAdapter(adapter);
            }

            public void setupData(List<VenueObjectModel> modelList) {
                adapter.updateData(modelList);
            }
        }

        public class ExclusiveDealBlockHolder extends RecyclerView.ViewHolder {
            private final ItemHomeExclusiveDealRecyclerBinding mBinding;
            private final ExclusiveDealAdapter<ExclusiveDealModel> adapter = new ExclusiveDealAdapter<>();

            public ExclusiveDealBlockHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemHomeExclusiveDealRecyclerBinding.bind(itemView);
                setupRecycleHorizontalManager(mBinding.recyclerView);
                GravitySnapHelper snapHelper = new GravitySnapHelper(Gravity.START);
                snapHelper.setSnapLastItem(true);
                snapHelper.setSnapToPadding(true);
                snapHelper.attachToRecyclerView(mBinding.recyclerView);
                mBinding.recyclerView.setAdapter(adapter);
            }

            public void setupData(List<ExclusiveDealModel> deals) {
                adapter.updateData(deals);
            }
        }

        public class TestActivityBlockHolder extends RecyclerView.ViewHolder {
            private final ItemHomeTestActivityBlockBinding mBinding;
            private final TestActivityAdapter<ActivityDetailModel> adapter = new TestActivityAdapter<>();
            public TestActivityBlockHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemHomeTestActivityBlockBinding.bind(itemView);
                setupRecycleHorizontalManager(mBinding.testActivityRecycler);
                GravitySnapHelper snapHelper = new GravitySnapHelper(Gravity.START);
                snapHelper.setSnapLastItem(true);
                snapHelper.setSnapToPadding(true);
                snapHelper.attachToRecyclerView(mBinding.testActivityRecycler);
                mBinding.testActivityRecycler.setAdapter(adapter);
                mBinding.txtSeeAll.setOnClickListener(view -> startActivity(new Intent(activity, SeeAllDetalisActivity.class)));
            }

            public void setupData(List<ActivityDetailModel> activities) {
                adapter.updateData(activities);
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
                mBinding.txtSeeAll.setOnClickListener(view -> {
                    startActivity(new Intent(activity, OutingListActivity.class));
                });
            }
        }

        public class SuggestedUserBlockHolder extends RecyclerView.ViewHolder {
            private final ItemHomeSuggestedUserRecyclerBinding mBinding;

            public SuggestedUserBlockHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemHomeSuggestedUserRecyclerBinding.bind(itemView);

            }

            public void setupData(List<UserDetailModel> suggestedUsers) {
                mBinding.suggestedUserView.setSuggestedUser( suggestedUsers,activity,getSupportFragmentManager(), (success, error) -> {} );
            }
        }

        public class SuggestedVenueViewHolder extends RecyclerView.ViewHolder {
            private final ItemHomeVenueSuggestionRecyclerBinding mBinding;

            public SuggestedVenueViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemHomeVenueSuggestionRecyclerBinding.bind(itemView);
            }

            public void setupData(List<VenueObjectModel> suggestedVenue) {
                mBinding.suggestedVenue.setSuggestedVenue( suggestedVenue,activity,getSupportFragmentManager(), (success, error1) -> {});
            }

        }

        public class EventBlockHolder extends RecyclerView.ViewHolder {

            private final ItemHomeEventRecyclerBinding mBinding;
            //        private final EventAdapter<EventModel> adapter = new EventAdapter<>();
            public EventBlockHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemHomeEventRecyclerBinding.bind(itemView);
//            setupRecycleHorizontalManager(mBinding.eventRecycler);
//            GravitySnapHelper snapHelper = new GravitySnapHelper(Gravity.START);
//            snapHelper.setSnapLastItem(true);
//            snapHelper.setSnapToPadding(true);
//            snapHelper.attachToRecyclerView(mBinding.eventRecycler);
//            mBinding.eventRecycler.setAdapter(adapter);
            }

//        public void setupData(List<EventModel> eventDetailModels) {
//            AppExecutors.get().mainThread().execute(() -> adapter.updateData(eventDetailModels));
//        }
        }

        public class LargeVenueBlockHolder extends RecyclerView.ViewHolder {
            private final ItemLargeVenueComoponentRecyclerBinding mBinding;

            public LargeVenueBlockHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemLargeVenueComoponentRecyclerBinding.bind(itemView);
            }
        }

        public class CustomComponentBlockHolder extends RecyclerView.ViewHolder {

            private final ItemCustomComponentRecyclerBinding mBinding;
            private final CustomComponentAdapter<CustomComponentModel> adapter = new CustomComponentAdapter<>();
            public CustomComponentBlockHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemCustomComponentRecyclerBinding.bind(itemView);
                mBinding.customRecycler.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
                GravitySnapHelper snapHelper = new GravitySnapHelper(Gravity.START);
                snapHelper.setSnapLastItem(true);
                snapHelper.setSnapToPadding(true);
                snapHelper.attachToRecyclerView(mBinding.customRecycler);
                mBinding.customRecycler.setAdapter(adapter);
                Utils.smoothScrollToPosition(mBinding.customRecycler, 0);

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

        public class TicketHolder extends RecyclerView.ViewHolder {

            private final ItemTicketRecyclerBinding binding;

            public TicketHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemTicketRecyclerBinding.bind(itemView);
                binding.seeAll.setText(getValue("see_all"));
            }

            public void setupData(List<RaynaTicketDetailModel> ticket,String type) {
                activity.runOnUiThread(() -> {
                    binding.ticketRecyclerView.isVertical = false;
                    binding.ticketRecyclerView.activity = activity;
                    binding.ticketRecyclerView.setupData(ticket, activity, false, false);
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


    }

    public class CategoriesAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = UiUtils.getViewBy(parent, R.layout.item_categories);
            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((Activity) activity).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            view.getLayoutParams().width = (int) (displayMetrics.widthPixels * 0.40);
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
            viewHolder.itemView.setOnClickListener(view -> {
                startActivity(new Intent(activity, CategoryActivity.class).putExtra("categoryId", model.getId()).putExtra("image", model.getImage()));
            });
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
            if (venueObjectModel.getStories() != null) {
                if (!venueObjectModel.getStories().isEmpty()) {
                    return true;
                }
            }
            HomeObjectModel homeObjectModel = SessionManager.shared.geHomeBlockData();
            if (homeObjectModel != null) {
                Optional<VenueObjectModel> tmpVenue = homeObjectModel.getStories().stream().filter(p -> Objects.equals(p.getId(), venueObjectModel.getId())).findFirst();
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
                        activity.runOnUiThread(() -> Graphics.setStoryRing(model.getId(), viewHolder.mBinding.roundLinear));
                    }
                });
                backgroundThread.start();
                viewHolder.mBinding.btnContinue.setOnClickListener(v -> Graphics.openVenueDetail(activity, model.getId()));
                viewHolder.mBinding.roundLinear.setOnClickListener(view -> {
                    HomeObjectModel homeObjectModel = SessionManager.shared.geHomeBlockData();
                    List<VenueObjectModel> matchingStories = homeObjectModel.getStories().stream().filter(model1 -> model1.getId().equals(model.getId())).collect(Collectors.toList());
                    if (!matchingStories.isEmpty()) {
                        Intent intent = new Intent(activity, StoryViewActivity.class);
                        intent.putExtra("stories", new Gson().toJson(matchingStories));
                        intent.putExtra("selectedPosition", 0);
                        activity.startActivity(intent);
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
            int screenWidth = Graphics.getScreenWidth(activity);
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
            Optional<VenueObjectModel> tVenueModel = searchHomeObjectModel.getVenues().stream().filter(p -> p.getId().equalsIgnoreCase(model.getVenue())).findFirst();
            if (tVenueModel.isPresent()) {
                viewHolder.binding.venueContainer.setVenueDetail(tVenueModel.get());
                viewHolder.binding.tvView.setOnClickListener(view -> {
                    Graphics.openVenueDetail(activity,tVenueModel.get().getId());
                });
            }

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
            View view = UiUtils.getViewBy(parent, R.layout.home_exclusive_deal_item);
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.width = (int) (Graphics.getScreenWidth(activity) * (getItemCount() > 1 ? 0.83 : 0.93));
            view.setLayoutParams(params);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;

            ExclusiveDealModel model = (ExclusiveDealModel) getItem(position);
            viewHolder.mBinding.tvTitle.setText(model.getDescription());
            viewHolder.mBinding.tvSubTitle.setText(model.getTitle());
            Graphics.loadImage(model.getImage(), viewHolder.mBinding.cover);
            if (model.getActualPrice() != 0) {
                viewHolder.mBinding.tvAED.setText(String.valueOf(model.getActualPrice()));
            } else {
                viewHolder.mBinding.tvAED.setVisibility(View.GONE);
            }

            if (model.getActualPrice() == model.getDiscountedPrice()) {
                viewHolder.mBinding.tvAED.setVisibility(View.GONE);
                viewHolder.mBinding.tvPrice.setText(String.valueOf(model.getActualPrice()));
            } else {
                viewHolder.mBinding.tvAED.setVisibility(View.VISIBLE);
            }

            viewHolder.mBinding.tvPrice.setText(String.valueOf(model.getDiscountedPrice()));
            viewHolder.mBinding.tvAED.setPaintFlags(viewHolder.mBinding.tvAED.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            // Graphics.applyBlurEffect(activity, viewHolder.mBinding.blurView);
            // Graphics.applyBlurEffect(activity, viewHolder.mBinding.blurViewTime);

            setVenueInfo(model.getVenueId(), viewHolder.mBinding.venueContainer);

            viewHolder.mBinding.buyNowBtn.setOnClickListener(v -> {
                Optional<VenueObjectModel> venueObjectModel = searchHomeObjectModel.getVenues().stream().filter(p -> p.getId().equals(model.getVenueId())).findFirst();
                if (venueObjectModel.isPresent()) {
                    Intent intent = new Intent(activity, BuyNowActivity.class);
                    intent.putExtra("dealId", model.getId())
                            .putExtra("venueDetail", new Gson().toJson(venueObjectModel.get()))
                            .putExtra("offerModel", new Gson().toJson(model));
                    startActivity(intent);
                }
            });

            viewHolder.mBinding.getRoot().setOnClickListener(v -> {
               /* Optional<VenueObjectModel> venueObjectModel = homeObjectModel.getVenues().stream().filter(p -> p.getId().equals(model.getVenueId())).findFirst();
                if (venueObjectModel.isPresent()) {
                    startActivity(new Intent(requireActivity(), VoucherDetailScreenActivity.class).putExtra("id", model.getId()));
                }*/
                startActivity(new Intent(activity, VoucherDetailScreenActivity.class).putExtra("id", model.getId()));

            });

            if (model.getEndDate() != null) {
                String combinedDateTime = model.getEndDate() + "T" + model.getEndTime() + ":00.000Z";
                Utils.setTimer(combinedDateTime, viewHolder.mBinding.countTimer);
                try {
                    Date givenDate =  Utils.stringToDate(combinedDateTime, AppConstants.DATEFORMAT_LONG_TIME);
                    Date currentDate = new Date();
                    if (givenDate.before(currentDate)) {
                        viewHolder.mBinding.roundBlur.setVisibility(View.INVISIBLE);
                    } else if (givenDate.after(currentDate)) {
                        viewHolder.mBinding.roundBlur.setVisibility(View.VISIBLE);
                    } else {
                        viewHolder.mBinding.roundBlur.setVisibility(View.INVISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private HomeExclusiveDealItemBinding mBinding;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = HomeExclusiveDealItemBinding.bind(itemView);
            }
        }
    }

    public class TestActivityAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = UiUtils.getViewBy(parent, R.layout.test_activity_item);
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.width = (int) (Graphics.getScreenWidth(activity) * (getItemCount() > 1 ? 0.83 : 0.93));
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

            if (model.getDiscount().equals("0")) {
                viewHolder.mBinding.tvAED.setVisibility(View.GONE);
            } else {
                viewHolder.mBinding.tvAED.setVisibility(View.VISIBLE);
            }
            viewHolder.mBinding.tvAED.setPaintFlags(viewHolder.mBinding.tvAED.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            int discount = Integer.parseInt(model.getDiscount());
            int amount = model.getPrice();
            int value = discount * amount / 100;
            int discountPrice = amount - value;

            if ("0".equals(model.getDiscount())) {
                viewHolder.mBinding.tvAED.setVisibility(View.GONE);
                viewHolder.mBinding.tvPrice.setText(String.valueOf(model.getPrice()));
            }
            else {
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
                Intent intent = new Intent(activity, ActivityListDetail.class);
                intent.putExtra("activityId", model.getId()).putExtra("type", "activities");
                intent.putExtra("name", model.getName());
                intent.putExtra("image", model.getProvider().getLogo());
                intent.putExtra("title", model.getProvider().getName());
                intent.putExtra("address", model.getProvider().getAddress());
                startActivity(intent);
            });

            viewHolder.mBinding.buyNowBtn.setOnClickListener(view -> startActivity(new Intent(activity, YourOrderActivity.class).putExtra("activityModel", new Gson().toJson(model))));

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
                viewHolder.mBinding.txtTime.setText(String.format("%s - %s", model.getStartTime(), Utils.convertTimeFormat(model.getEndTime())));
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
                viewHolder.mBinding.constraint.setBackground(ContextCompat.getDrawable(activity, R.drawable.stroke_gradiant_line_me));
                viewHolder.mBinding.txtOutingTitle.setVisibility(View.GONE);
                viewHolder.mBinding.layoutStatus.setVisibility(View.INVISIBLE);
                if (model.getStatus().equals("completed") || model.getStatus().equals("cancelled")) {
                    viewHolder.mBinding.layoutEdit.setVisibility(View.GONE);
                } else {
                    viewHolder.mBinding.layoutEdit.setVisibility(View.VISIBLE);
                }
            }
            else {
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
                                viewHolder.mBinding.layoutStatus.setBackgroundColor(ContextCompat.getColor(activity, R.color.pending_yellow));
                                viewHolder.mBinding.constraint.setBackground(ContextCompat.getDrawable(activity, R.drawable.stroke_gradiant_line_pending));
                            }
                            else if (invitedUser.getInviteStatus().equals("in")) {
                                viewHolder.mBinding.layoutStatus.setBackgroundColor(ContextCompat.getColor(activity, R.color.in_green));
                                viewHolder.mBinding.constraint.setBackground(ContextCompat.getDrawable(activity, R.drawable.stroke_gradiant_line_in));
                            }
                            else {
                                viewHolder.mBinding.layoutStatus.setBackgroundColor(ContextCompat.getColor(activity, R.color.out_red));
                                viewHolder.mBinding.constraint.setBackground(ContextCompat.getDrawable(activity, R.drawable.stroke_gradiant_line_out));
                            }
                        });
                    }
                });
                backgroundThread.start();

                viewHolder.mBinding.layoutEdit.setVisibility(View.GONE);
                viewHolder.mBinding.layoutStatus.setVisibility(View.VISIBLE);

                viewHolder.mBinding.layout2.setOnClickListener(view -> {
                    if (model.getUser() != null) {
                        if (SessionManager.shared.getUser().isRingMember() && model.getUser().isPromoter()) {
                            startActivity(new Intent(activity, PromoterPublicProfileActivity.class)
                                    .putExtra("isFromOtherUserProfile", true)
                                    .putExtra("isPromoterProfilePublic", true).putExtra("id", model.getUser().getId()));
                        } else if (SessionManager.shared.getUser().isPromoter() && model.getUser().isRingMember()) {
                            startActivity(new Intent(activity, CmPublicProfileActivity.class)
                                    .putExtra("isFromOtherUserProfile", true)
                                    .putExtra("promoterUserId", model.getUser().getId()));
                        } else {
                            startActivity(new Intent(activity, OtherUserProfileActivity.class).putExtra("friendId", model.getUser().getId()));
                        }
                    }
                });
            }

            viewHolder.mBinding.btnSeeAll.setOnClickListener(v -> {
                Utils.preventDoubleClick(v);
                InviteGuestListBottomSheet inviteGuestListBottomSheet = new InviteGuestListBottomSheet();
                inviteGuestListBottomSheet.model = model.getInvitedUser();
                inviteGuestListBottomSheet.type = "outing";
                inviteGuestListBottomSheet.show(getSupportFragmentManager(), "");
            });

            viewHolder.mBinding.layoutEdit.setOnClickListener(view -> {
                Utils.preventDoubleClick( view );
                InviteFriendBottomSheet inviteFriendDialog = new InviteFriendBottomSheet();
                inviteFriendDialog.inviteFriendModel = model;
                inviteFriendDialog.setShareListener(data -> {
                    AppExecutors.get().mainThread().execute(() -> {
                        notifyDataSetChanged();
                    });
                });
                inviteFriendDialog.show(getSupportFragmentManager(), "1");
            });

            viewHolder.mBinding.getRoot().setOnClickListener(view -> startActivity(new Intent(activity, MyInvitationActivity.class).putExtra("id", model.getId()).putExtra("notificationType", "notification")));
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final OutingListItemBinding mBinding;
            private final FriendListAdapter<ContactListModel> friendListAdapter = new FriendListAdapter<>();
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = OutingListItemBinding.bind(itemView);
                mBinding.friendRecycler.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
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
            if (model == null) { return; }
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


    // endregion
    // --------------------------------------
    // region Search Result Adapter
    // --------------------------------------


    // endregion
    // --------------------------------------
    // region Search Adapter
    // --------------------------------------

    public class SearchResultAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            switch (AppConstants.SearchResultType.valueOf( viewType )) {
                case TICKET:
                    return new TicketBlockHolder( UiUtils.getViewBy( parent, R.layout.item_all_serach_ticket ) );
                case VENUE:
                    return new VenueBlockHolder( UiUtils.getViewBy( parent, R.layout.item_search_venue ) );
                case OFFER:
                    return new OfferBlockHolder( UiUtils.getViewBy( parent, R.layout.category_list_item ) );
                case USER:
                    return new UserBlockHolder( UiUtils.getViewBy( parent, R.layout.search_user_item ) );
                case EVENT:
                    return new EventBlockHolder( UiUtils.getViewBy( parent, R.layout.iteam_search_event ) );
                case ACTIVITY:
                    return new ActivityBlockHolder( UiUtils.getViewBy( parent, R.layout.item_search_activity ) );
                case HOME_AD:
                    return new HomeAdHolder(UiUtils.getViewBy(parent, R.layout.item_home_ad_view));
            }
            return null;
        }

        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            boolean isLastItem = position == getItemCount() - 1;

            if (getItem( position ) instanceof VenueObjectModel) {
                VenueObjectModel model = (VenueObjectModel) getItem( position );
                VenueBlockHolder venueBlockHolder = (VenueBlockHolder) holder;
                venueBlockHolder.setupData( model );
            } else if (getItem( position ) instanceof ContactListModel) {
                ContactListModel model = (ContactListModel) getItem( position );
                UserBlockHolder userBlockHolder = (UserBlockHolder) holder;
                userBlockHolder.setupData( model );
            } else if (getItem( position ) instanceof OffersModel) {
                OffersModel model = (OffersModel) getItem( position );
                OfferBlockHolder offerBlockHolder = (OfferBlockHolder) holder;
                offerBlockHolder.setupData( model );
            } else if (getItem( position ) instanceof SearchEventModel) {
                SearchEventModel model = (SearchEventModel) getItem( position );
                EventBlockHolder eventBlockHolder = (EventBlockHolder) holder;
                eventBlockHolder.setupData( model );
            } else if (getItem( position ) instanceof ActivityDetailModel) {
                ActivityDetailModel model = (ActivityDetailModel) getItem( position );
                ActivityBlockHolder activityBlockHolder = (ActivityBlockHolder) holder;
                activityBlockHolder.setupData( model );
            }else if (getItem( position ) instanceof RaynaTicketDetailModel) {
                RaynaTicketDetailModel model = (RaynaTicketDetailModel) getItem( position );
                TicketBlockHolder ticketBlockHolder = (TicketBlockHolder) holder;
                ticketBlockHolder.setupData( model );
            }else if (getItem( position ) instanceof BannerModel) {
                HomeAdHolder homeAdHolder = (HomeAdHolder) holder;
                homeAdHolder.setupData();
            }

            if (isLastItem) {
                int marginBottom = getMarginBottom( holder.itemView.getContext() );
                Utils.setBottomMargin( holder.itemView, marginBottom );
            } else {
                Utils.setBottomMargin( holder.itemView, 0 );
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (getItem(position) instanceof RaynaTicketDetailModel) {
                return AppConstants.SearchResultType.TICKET.getValue();
            } else if (getItem(position) instanceof VenueObjectModel) {
                return AppConstants.SearchResultType.VENUE.getValue();
            } else if (getItem(position) instanceof ContactListModel) {
                return AppConstants.SearchResultType.USER.getValue();
            } else if (getItem(position) instanceof OffersModel) {
                return AppConstants.SearchResultType.OFFER.getValue();
            } else if (getItem(position) instanceof SearchEventModel) {
                return AppConstants.SearchResultType.EVENT.getValue();
            } else if (getItem(position) instanceof ActivityDetailModel) {
                return AppConstants.SearchResultType.ACTIVITY.getValue();
            }else if (getItem(position) instanceof BannerModel) {
                return AppConstants.SearchResultType.HOME_AD.getValue();
            }
            return AppConstants.SearchResultType.NONE.getValue();


        }

        public int getMarginBottom(Context context) {
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            int screenHeight = displayMetrics.heightPixels;
            float scaleFactor = 0.06f;
            return (int) (screenHeight * scaleFactor);
        }


        public class VenueBlockHolder extends RecyclerView.ViewHolder {

            private final ItemSearchVenueBinding binding;

            public VenueBlockHolder(@NonNull View itemView) {
                super( itemView );
                binding = ItemSearchVenueBinding.bind( itemView );
            }

            @SuppressLint("SetTextI18n")
            public void setupData(VenueObjectModel model) {

                binding.venueContainer.setVenueDetail(model, data -> {
                    if (data){
                        SearchHistoryModel.addRecord( model.getId(), "venue", model.getName(), model.getAddress(), model.getLogo(), "" ,model,null);
                    }
                });

                binding.tvDiscription.setText( TextUtils.join( ", ", model.getCuisine() ) );
                DecimalFormat decimalFormat = new DecimalFormat( "#.##" );
                binding.tvDistance.setText( decimalFormat.format( model.getDistance() ) + " Km" );

                binding.getRoot().setOnClickListener( view -> {
                    SearchHistoryModel.addRecord( model.getId(), "venue", model.getName(), model.getAddress(), model.getLogo(), "" ,model,null);
                    Graphics.openVenueDetail(activity,  model.getId() );
                } );

                binding.btnFollowing.setText(model.isIsFollowing() ? "Following" : "Follow");

                binding.btnFollowing.setOnClickListener( view -> {
                    reqOfferFollowUnFollow( model.getId(), (success, error) -> {
                        if (success) {
                            Alerter.create( activity ).setTitle( "Thank you!" ).setText( "For following " + model.getName() ).setTextAppearance( R.style.AlerterText ).setTitleAppearance( R.style.AlerterTitle ).setBackgroundColorRes( R.color.AlerterSuccessBg ).hideIcon().show();
                            model.setIsFollowing( true );
                            notifyDataSetChanged();
                        } else {
                            Alerter.create( activity ).setTitle( "Oh Snap!" ).setTitleAppearance( R.style.AlerterTitle ).setTextAppearance( R.style.AlerterText ).setText( "You have unfollowed " + model.getName() ).setBackgroundColorRes( R.color.AlerterSuccessBg ).hideIcon().show();
                            model.setIsFollowing( false );
                            binding.btnFollowing.setText( "Follow" );
                            notifyDataSetChanged();
                        }
                    } );
                } );

            }

        }

        public class OfferBlockHolder extends RecyclerView.ViewHolder {

            private final CategoryListItemBinding binding;
            private final OfferPackagesAdapter<PackageModel> packageAdapter = new OfferPackagesAdapter<>();


            public OfferBlockHolder(@NonNull View itemView) {
                super( itemView );
                binding = CategoryListItemBinding.bind( itemView );
                binding.venueSubRecycler.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
                binding.venueSubRecycler.setAdapter(packageAdapter);
            }

            public void setupData(OffersModel model) {
                float cornerRadius = activity.getResources().getDimensionPixelSize(com.intuit.sdp.R.dimen._8sdp);
                binding.constraint.setCornerRadius(cornerRadius, CornerType.ALL);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                int marginHorizontal = getResources().getDimensionPixelSize(com.intuit.sdp.R.dimen._8sdp);
                int marginTop = getResources().getDimensionPixelSize(com.intuit.sdp.R.dimen._15sdp);
                layoutParams.setMargins(marginHorizontal, marginTop, marginHorizontal, 0);
                binding.constraint.setLayoutParams(layoutParams);

                if (model.getVenue() != null) {
                    binding.venueContainer.setVenueDetail(model.getVenue(), data -> {
                        if (data){
                            SearchHistoryModel.addRecord( model.getId(), "offer", model.getTitle(), model.getDescription(), model.getImage(), model.getVenue().getId(),null,null );
                        }
                    });
                }

                binding.txtTitle.setText( model.getTitle() );
                binding.tvDescription.setText( model.getDescription() );

                binding.offerInfoView.setOfferDetail(model, SearchActivity.this, getSupportFragmentManager());
                binding.offerButtonView.setupButtons(model, SearchActivity.this, getSupportFragmentManager());

                itemView.setOnClickListener( v -> {
                    SearchHistoryModel.addRecord( model.getId(), "offer", model.getTitle(), model.getDescription(), model.getImage(), model.getVenue().getId(),null,null );
                    OfferDetailBottomSheet dialog = new OfferDetailBottomSheet();
                    dialog.offerId = model.getId();
                    dialog.show(getSupportFragmentManager(), "");
                } );

                if (!model.getPackages().isEmpty()) {
                    packageAdapter.updateData(model.getPackages());
                    binding.venueSubRecycler.setVisibility(View.VISIBLE);
                } else {
                    binding.venueSubRecycler.setVisibility(View.GONE);
                }

                binding.venueSubRecycler.setVisibility( View.GONE );
                binding.tvLastDate.setVisibility( View.GONE );

                binding.iconMenu.setOnClickListener( view -> {
                    Utils.preventDoubleClick( view );
                    ArrayList<String> data = new ArrayList<>();
                    data.add( "Add To BucketList" );
                    data.add(model.getVenue().isIsFollowing() ? "UnFollow" : "Follow");
                    data.add( "Share Venue" );
                    data.add( "Share Offer" );

                    data.add(model.getVenue().isRecommendation() ? "Remove recommendation" : "Add recommendation");
                    Graphics.showActionSheet( activity, getString(R.string.app_name), data, (data1, position1) -> {
                        switch (position1) {
                            case 0:
                                BucketListBottomSheet dialog = new BucketListBottomSheet();
                                dialog.offerId = model.getId();
                                dialog.show( getSupportFragmentManager(), "" );
                                break;
                            case 1:
                                reqOfferFollowUnFollow( model.getVenue().getId(), (success, error) -> {
                                    if (success) {
                                        Alerter.create( activity ).setTitle( "Thank you!" ).setText( "For following " + model.getVenue().getName() ).setTextAppearance( R.style.AlerterText ).setTitleAppearance( R.style.AlerterTitle ).setBackgroundColorRes( R.color.AlerterSuccessBg ).hideIcon().show();
                                        model.getVenue().setIsFollowing( true );
                                        notifyDataSetChanged();
                                    } else {
                                        Alerter.create( activity ).setTitle( "Oh Snap!" ).setTitleAppearance( R.style.AlerterTitle ).setTextAppearance( R.style.AlerterText ).setText( "You have unfollowed " + model.getVenue().getName() ).setBackgroundColorRes( R.color.AlerterSuccessBg ).hideIcon().show();
                                        model.getVenue().setIsFollowing( false );
                                        notifyDataSetChanged();
                                    }
                                } );
                                break;
                            case 2:
                                startActivity( new Intent( activity, VenueShareActivity.class ).putExtra( "venue", new Gson().toJson( model.getVenue() ) )
                                        .putExtra( "type", "venue" ) );
                                break;
                            case 3:
                                startActivity( new Intent( activity, VenueShareActivity.class ).putExtra( "offer", new Gson().toJson( model ) )
                                        .putExtra( "type", "offer" ) );
                                break;
                            case 4:
                                reqRecommendation(model.getVenue().getId(),model.getVenue());
                                break;
                        }

                    } );
                } );
            }
        }

        public class UserBlockHolder extends RecyclerView.ViewHolder {
            private final SearchUserItemBinding binding;

            public UserBlockHolder(@NonNull View itemView) {
                super( itemView );
                binding = SearchUserItemBinding.bind( itemView );

            }

            public void setupData(ContactListModel model) {
                binding.tvUserName.setText( model.getFullName() );
                Graphics.loadImageWithFirstLetter( model.getImage(), binding.ivUserProfile, model.getFullName() );

                binding.getRoot().setOnClickListener( view -> {
                    if (!Utils.isUser( model.getId() )) {
                        SearchHistoryModel.addRecord( model.getId(), "user", model.getFullName(), "", model.getImage(), "" ,null,model);
                        if (SessionManager.shared.getUser().isRingMember() && model.isPromoter()) {
                            startActivity(new Intent(activity, PromoterPublicProfileActivity.class)
                                    .putExtra("isFromOtherUserProfile", true)
                                    .putExtra("isPromoterProfilePublic", true).putExtra("id", model.getId()));
                        } else if (SessionManager.shared.getUser().isPromoter() && model.isRingMember()) {
                            startActivity(new Intent(activity, CmPublicProfileActivity.class)
                                    .putExtra("isFromOtherUserProfile", true)
                                    .putExtra("promoterUserId", model.getId()));
                        } else {
                            startActivity(new Intent(activity, OtherUserProfileActivity.class).putExtra("friendId", model.getId()));
                        }
//                        startActivity( new Intent( activity, OtherUserProfileActivity.class ).putExtra( "friendId", model.getId() ) );
                    }

                } );

                if (model.isSynced()) {
                    binding.optionContainer.setVisibility(model.isSynced() ? View.VISIBLE : View.GONE);
                    binding.optionContainer.setupView(model, activity, data -> {
                        searchResultAdapter.notifyDataSetChanged();
                        allSearchResultAdapter.notifyDataSetChanged();
                    });
                }

                if (Utils.isUser( model.getId() )) {
                    binding.optionContainer.setVisibility( View.GONE );
                }
                binding.optionContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openChat(model);
                    }
                });

            }


            private void openChat(ContactListModel listModel){
                ChatModel chatModel = new ChatModel();
                chatModel.setImage( listModel.getImage() );
                chatModel.setTitle( listModel.getFullName() );
                chatModel.setChatType( "friend" );
                RealmList<String> idList = new RealmList<>();
                idList.add( SessionManager.shared.getUser().getId());
                idList.add( listModel.getId());
                chatModel.setMembers(idList);
                Collections.sort(idList);
                String joinedString = String.join(",", idList);
                chatModel.setChatId(joinedString);

                Intent intent = new Intent(activity, ChatMessageActivity.class);
                intent.putExtra("chatModel", new Gson().toJson(chatModel));
                intent.putExtra("isChatId", true);
                intent.putExtra( "type","friend" );
                startActivity(intent);
            }

        }

        public class EventBlockHolder extends RecyclerView.ViewHolder {
            private final IteamSearchEventBinding binding;

            public EventBlockHolder(@NonNull View itemView) {
                super( itemView );
                binding = IteamSearchEventBinding.bind( itemView );
            }

            public void setupData(SearchEventModel model) {

                binding.eventTitle.setText( model.getTitle() );
                binding.tvDescription.setText( model.getDescription() );
                if (model.getVenue() != null) {
//                    Graphics.applyBlurEffect(activity, binding.blurView);
                    binding.venueContainer.setVenueDetail(model.getVenue(), new CommanCallback<Boolean>() {
                        @Override
                        public void onReceive(Boolean data) {
                            if (data){
                                SearchHistoryModel.addRecord( model.getId(), "event", model.getOrgData().getName(), model.getOrgData().getWebsite(), model.getOrgData().getLogo(), "",null ,null);
                            }
                        }
                    });
                }
                Graphics.loadImage( model.getImage(), binding.ivCover );

                binding.endTime.setText( Utils.convertMainDateFormat( model.getEventTime() ) );
                binding.startTime.setText( Utils.convertMainTimeFormat( model.getReservationTime() ) + " - " + Utils.convertTimeFormat( model.getEventTime() ) );

//                Graphics.applyBlurEffect(activity, binding.venueBlurView);
                if (model.getOrgData() != null) {
                    Graphics.loadRoundImage(model.getOrgData().getLogo(), binding.imgOrg);
                    binding.tvOrgName.setText(model.getOrgData().getName());
                    binding.tvWebsite.setText(model.getOrgData().getWebsite() != null ? model.getOrgData().getWebsite() : "");
                    binding.tvWebsite.setVisibility(model.getOrgData().getWebsite() != null ? View.VISIBLE : View.GONE);
                }

                binding.linearEvent.setOnClickListener(v -> {
                    if (model.getOrgData() != null) {
                        startActivity(new Intent(activity, EventOrganizerDetailsActivity.class).putExtra("org_id", model.getOrgId()).putExtra("type", "events_organizers").putExtra("name", model.getOrgData().getName()).putExtra("webSite", model.getOrgData().getWebsite()).putExtra("image", model.getOrgData().getLogo()));
                    }
                });

                if (model.getEventTime() != null) {
                    Utils.setTimer( model.getEventTime(), binding.countTimer );
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    try {
                        Date givenDate = dateFormat.parse(model.getEventTime());
                        Date currentDate = new Date();
                        if (givenDate.before(currentDate)) {
                            binding.layoutTimer.setVisibility(View.INVISIBLE);
                        } else  if (givenDate.after(currentDate)) {
                            binding.layoutTimer.setVisibility(View.VISIBLE);
                        } else {
                            binding.layoutTimer.setVisibility(View.INVISIBLE);
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                itemView.setOnClickListener( v -> {
                    SearchHistoryModel.addRecord( model.getId(), "event", model.getOrgData().getName(), model.getOrgData().getWebsite(), model.getOrgData().getLogo(), "",null ,null);

                    Intent intent = new Intent( activity, EventDetailsActivity.class );
                    intent.putExtra( "eventId", model.getId() );
                    intent.putExtra( "name", model.getOrgData().getName() );
                    intent.putExtra( "address", model.getOrgData().getWebsite() );
                    intent.putExtra( "image", model.getOrgData().getLogo() );
                    intent.putExtra( "venueName", model.getVenue().getName() );
                    intent.putExtra( "venueAddress", model.getVenue().getAddress() );
                    intent.putExtra( "venueImage", model.getVenue().getLogo() );
                    startActivity( intent );
                } );


            }

        }

        public class ActivityBlockHolder extends RecyclerView.ViewHolder {

            private final ItemSearchActivityBinding binding;

            public ActivityBlockHolder(@NonNull View itemView) {
                super( itemView );
                binding = ItemSearchActivityBinding.bind( itemView );

            }

            public void setupData(ActivityDetailModel model) {
                binding.tvName.setText( model.getName() );
                binding.tvPrice.setText( String.valueOf( model.getPrice() ) );

                if (model.getProvider() != null) {
                    binding.tvTitle.setText( model.getProvider().getName() );
                    binding.tvAddress.setText( model.getProvider().getAddress() );
                    Graphics.loadRoundImage( model.getProvider().getLogo(), binding.iconImg );
                }

                Graphics.loadImage( model.getGalleries().get( 0 ), binding.ivCover );
//                Graphics.applyBlurEffect( activity, binding.blurView );

                binding.tvStartTime.setText( Utils.convertMainDateFormat( model.getStartDate() ) );
                binding.tvEndDate.setText( Utils.convertMainDateFormat( model.getEndDate() ) );

                itemView.setOnClickListener( view -> {

                    SearchHistoryModel.addRecord( model.getId(), "activity", model.getName(), model.getDescription(), model.getGalleries().get( 0 ), "",null,null );

                    Intent intent = new Intent( activity, ActivityListDetail.class );
                    intent.putExtra( "activityId", model.getId() ).putExtra( "type", "activities" )
                            .putExtra( "name", model.getName() )
                            .putExtra( "image", model.getProvider().getLogo() )
                            .putExtra( "title", model.getProvider().getName() )
                            .putExtra( "address", model.getProvider().getAddress()
                            );
                    startActivity( intent );
                } );
            }

        }

        public class TicketBlockHolder extends RecyclerView.ViewHolder {

            private final ItemAllSerachTicketBinding binding;

            public TicketBlockHolder(@NonNull View itemView) {
                super( itemView );
                binding = ItemAllSerachTicketBinding.bind( itemView );
                binding.tvFromTitle.setText(Utils.getLangValue("from"));
                binding.tvStatingTile.setText(String.format("%s ", Utils.getLangValue("starting")));
                binding.tvRecentlyAddedTitle.setText(Utils.getLangValue("recently_added"));

            }

            public void setupData(RaynaTicketDetailModel model) {
                if (model.getImages() != null && !model.getImages().isEmpty()) {
                    List<String> urls = model.getImages();
                    urls.removeIf(Utils::isVideo);
                    setupData(model.getImages(),model);
                }

                binding.ticketAddress.setText(model.getCity());

                if (model.getAvg_ratings() != 0){
                    double truncatedRating = Math.floor(model.getAvg_ratings() * 10) / 10.0;
                    binding.tvRate.setText(String.format(Locale.ENGLISH, "%.1f", truncatedRating));
                    binding.ticketRatingLayout.setVisibility(View.VISIBLE);
                }else {
                    binding.ticketRatingLayout.setVisibility(View.GONE);
                }

                Graphics.setFavoriteIcon(activity,model.isIs_favorite(),binding.ivFavourite);

                String discount = String.valueOf(model.getDiscount());

                if (!"0".equals(discount)) {
                    binding.tvDiscount.setText(discount.contains("%") ? discount : discount + "%");
                    binding.tvDiscount.setVisibility(View.VISIBLE);
                } else {
                    binding.tvDiscount.setVisibility(View.GONE);
                }

                binding.ticketTag.setVisibility(model.isTicketRecentlyAdded() ? View.VISIBLE : View.GONE);

                binding.txtTitle.setText(Utils.notNullString(model.getTitle()));

                String startingAmount = model.getStartingAmount() != null ? String.valueOf(model.getStartingAmount()) : "N/A";

                if (!"N/A".equals(startingAmount)) {
                    binding.ticketFromAmount.setText(model.getDiscountAndStartingAmount(activity,binding.discountText));
                } else {
                    SpannableString styledPrice = Utils.getStyledText(activity, "0");
                    SpannableStringBuilder fullText = new SpannableStringBuilder()
                            .append(styledPrice);
                    binding.ticketFromAmount.setText(fullText);
                }



                if (startingAmount.equals("N/A")) {
                    Utils.setStyledText(activity, binding.tvAED, "0");
                } else {
                    Utils.setStyledText(activity, binding.tvAED, Utils.roundFloatValue(Float.valueOf(startingAmount)));
                }

                if (TextUtils.isEmpty(startingAmount) || startingAmount.equals("0") || startingAmount.equals("N/A")) {
                    binding.roundLinear.setVisibility(View.GONE);
                    binding.startingFromLayout.setVisibility(View.GONE);
                } else {
                    binding.roundLinear.setVisibility(View.VISIBLE);
                    binding.startingFromLayout.setVisibility(View.VISIBLE);
                }

                binding.getRoot().setOnClickListener(view -> {
                    Utils.preventDoubleClick(view);
                    activity.startActivity(new Intent(activity, RaynaTicketDetailActivity.class).putExtra("ticketId",model.getId()));
                    String image = "";
                    if (!model.getImages().isEmpty()){
                        image = model.getImages().get(0);
                    }
                    SearchHistoryModel.addRecord( model.getId(), "ticket", model.getTitle(), model.getCity(),image, "" ,null,null,model);
                });

                binding.btnFavorite.setOnClickListener(v -> {
                    showProgress(true);
                    RaynaTicketManager.shared.requestRaynaTicketFavorite(activity, model.getId(), (success, error) -> {
                        if (success) {
                            showProgress(false);
                            boolean newState = !model.isIs_favorite();
                            model.setIs_favorite(newState);
                            if (newState) {
                                LogManager.shared.logTicketEvent(LogManager.LogEventType.addToWishlist, model.getId(), model.getTitle(), 0.0, null, "AED");
                            }
                            String title = newState ? Utils.getLangValue("thank_you") :  Utils.getLangValue("oh_snap");
//                            String message = model.getTitle() + (newState ? " has been added to your favorites." : " has been removed from your favorites.");
                            String message = newState ? Utils.setLangValue("add_favourite",model.getTitle()) : Utils.setLangValue("remove_favourite",model.getTitle());

                            Alerter.create(activity)
                                    .setTitle(title)
                                    .setText(message)
                                    .setTitleAppearance(R.style.AlerterTitle)
                                    .setTextAppearance(R.style.AlerterText)
                                    .setBackgroundColorRes(R.color.white_color)
                                    .hideIcon()
                                    .show();

                            notifyDataSetChanged();
                            EventBus.getDefault().postSticky(model);
                        } else {
                            showProgress(false);
                            Toast.makeText(activity, error != null ? error : "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    });
                });


            }

            private void showProgress(boolean isShowLoader){
                binding.ivFavourite.setVisibility(isShowLoader ? View.GONE : View.VISIBLE);
                binding.favTicketProgressBar.setVisibility(isShowLoader ? View.VISIBLE : View.GONE);
            }

            public void setupData(List<String> imaegs , RaynaTicketDetailModel raynaTicketDetailModel) {
                RaynaTicketImageAdapter adapter = new RaynaTicketImageAdapter(activity, raynaTicketDetailModel, imaegs, data -> {
                    String image = "";
                    if (!imaegs.isEmpty()){
                        image = imaegs.get(0);
                    }
                    SearchHistoryModel.addRecord( raynaTicketDetailModel.getId(), "ticket", raynaTicketDetailModel.getTitle(), raynaTicketDetailModel.getCity(),image, "" ,null,null,raynaTicketDetailModel);
                });
                binding.viewPager.setAdapter( adapter );
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            int nextPage = binding.viewPager.getCurrentItem() + 1;
                            if (nextPage == adapter.getCount()) {
                                nextPage = 0;
                            }
                            binding.viewPager.setCurrentItem( nextPage, true );
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            handler.postDelayed( this, 4000 );
                        }
                    }
                };
                handler.postDelayed( runnable, 4000 );

                binding.viewPager.addOnPageChangeListener( new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        handler.removeCallbacks( runnable );
                        handler.postDelayed( runnable, 4000 );
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                    }
                } );

            }

        }

        public class HomeAdHolder extends RecyclerView.ViewHolder {

            private final ItemHomeAdViewBinding mBinding;

            public HomeAdHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemHomeAdViewBinding.bind(itemView);
                mBinding.adView.setupAppLifeCycleCallback(itemView,SearchActivity.class);
                releaseVideoPlayerCallBack = data -> {
                  if (data){
                      mBinding.adView.relaseAllplayer();
                  }
                };

            }

            public void setupData() {
                activity.runOnUiThread(() -> {
                    mBinding.adView.activity = activity;
                    mBinding.adView.seUpData(activity);
                });
            }

            public void onItemVisibilityChanged(boolean isVisible) {
                mBinding.adView.onItemVisibilityChanged(isVisible);
            }

        }

        public void clearAdapter(){
            List<Object> list = new ArrayList<>();
            searchResultAdapter.updateData(list);
            commanSearch.clear();
            binding.searchResultRecycleView.getRecycledViewPool().clear();
            binding.searchResultRecycleView.setAdapter(null);
            binding.searchResultRecycleView.setAdapter(searchResultAdapter);
            binding.tabLayout.removeAllTabs();
        }

    }

    // endregion
    // --------------------------------------
    // region All Search Adapter
    // --------------------------------------

    public class AllSearchResultAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            switch (AppConstants.SearchResultType.valueOf( viewType )) {
                case VENUE:
                    return new AllSearchVenueBlockHolder( UiUtils.getViewBy( parent, R.layout.all_search_venue_desgin_item ) );
                case OFFER:
                    return new AllSearchOfferBlockHolder( UiUtils.getViewBy( parent, R.layout.all_search_venue_desgin_item ) );
                case USER:
                    return new AllSearchUserBlockHolder( UiUtils.getViewBy( parent, R.layout.all_search_venue_desgin_item ) );
                case EVENT:
                    return new AllSearchEventBlockHolder( UiUtils.getViewBy( parent, R.layout.all_search_venue_desgin_item ) );
                case ACTIVITY:
                    return new AllSearchActivityBlockHolder( UiUtils.getViewBy( parent, R.layout.all_search_venue_desgin_item ) );
                case TICKET:
                    return new AllSearchTicketBlockHolder( UiUtils.getViewBy( parent, R.layout.all_search_venue_desgin_item ) );

            }
            return new AllSearchVenueBlockHolder( UiUtils.getViewBy( parent, R.layout.all_search_venue_desgin_item ) );
        }

        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            CommanSearchModel commanSearchModel = (CommanSearchModel) getItem( position );

            if (commanSearchModel.getBlockType() == AppConstants.SearchResultType.VENUE) {
                AllSearchVenueBlockHolder venueBlockHolder = (AllSearchVenueBlockHolder) holder;
                venueBlockHolder.setupData( commanSearchModel );
            } else if (commanSearchModel.getBlockType() == AppConstants.SearchResultType.OFFER) {
                AllSearchOfferBlockHolder allSearchOfferBlockHolder = (AllSearchOfferBlockHolder) holder;
                allSearchOfferBlockHolder.setupData( commanSearchModel );
            } else if (commanSearchModel.getBlockType() == AppConstants.SearchResultType.USER) {
                AllSearchUserBlockHolder allSearchOfferBlockHolder = (AllSearchUserBlockHolder) holder;
                allSearchOfferBlockHolder.setupData( commanSearchModel );
            } else if (commanSearchModel.getBlockType() == AppConstants.SearchResultType.EVENT) {
                AllSearchEventBlockHolder allSearchOfferBlockHolder = (AllSearchEventBlockHolder) holder;
                allSearchOfferBlockHolder.setupData( commanSearchModel );
            } else if (commanSearchModel.getBlockType() == AppConstants.SearchResultType.ACTIVITY) {
                AllSearchActivityBlockHolder allSearchOfferBlockHolder = (AllSearchActivityBlockHolder) holder;
                allSearchOfferBlockHolder.setupData( commanSearchModel );
            }else if (commanSearchModel.getBlockType() == AppConstants.SearchResultType.TICKET) {
                AllSearchTicketBlockHolder allSearchTicketBlockHolder = (AllSearchTicketBlockHolder) holder;
                allSearchTicketBlockHolder.setupData( commanSearchModel );
            }


        }

        @Override
        public int getItemViewType(int position) {
            CommanSearchModel model = (CommanSearchModel) getItem( position );
            return model.getBlockType().getValue();
        }

        public void clearAdapter(){
            allSearchResultAdapter.updateData(Collections.emptyList());
            commanSearch.clear();
            binding.searchResultRecycleView.getRecycledViewPool().clear();
            binding.searchResultRecycleView.setAdapter(null);
            binding.searchResultRecycleView.setAdapter(allSearchResultAdapter);
            binding.tabLayout.removeAllTabs();

        }

        public class AllSearchVenueBlockHolder extends RecyclerView.ViewHolder {
            private final AllSearchVenueDesginItemBinding binding;

            private final AllVenueSearchAdapter<VenueObjectModel> adapter = new AllVenueSearchAdapter<>();

            public AllSearchVenueBlockHolder(@NonNull View itemView) {
                super( itemView );
                binding = AllSearchVenueDesginItemBinding.bind( itemView );
                binding.allVenueRecycler.setLayoutManager( new LinearLayoutManager( activity, LinearLayoutManager.HORIZONTAL, false ) );
                binding.allVenueRecycler.setAdapter( adapter );
            }

            @SuppressLint("SetTextI18n")
            public void setupData(CommanSearchModel model) {
                adapter.updateData( model.getVenues() );
                binding.txtTitle.setText( model.getType().substring( 0, 1 ).toUpperCase() + model.getType().substring( 1 ) );
                binding.txtSeeAll.setOnClickListener( view -> setTabTitle( commanSearch, model.getType() ) );

            }
        }

        public class AllSearchOfferBlockHolder extends RecyclerView.ViewHolder {
            private final AllSearchVenueDesginItemBinding binding;
            private final AllOfferSearchAdapter<OffersModel> adapter = new AllOfferSearchAdapter<>();

            public AllSearchOfferBlockHolder(@NonNull View itemView) {
                super( itemView );
                binding = AllSearchVenueDesginItemBinding.bind( itemView );
                binding.allVenueRecycler.setLayoutManager( new LinearLayoutManager( activity, LinearLayoutManager.HORIZONTAL, false ) );
                binding.allVenueRecycler.setAdapter( adapter );
            }

            @SuppressLint("SetTextI18n")
            public void setupData(CommanSearchModel model) {
                adapter.updateData( model.getOffers() );
                binding.txtTitle.setText( model.getType().substring( 0, 1 ).toUpperCase() + model.getType().substring( 1 ) );
                binding.txtSeeAll.setOnClickListener( view -> setTabTitle( commanSearch, model.getType() ) );

            }
        }

        public class AllSearchUserBlockHolder extends RecyclerView.ViewHolder {
            private final AllSearchVenueDesginItemBinding binding;

            private final AllSeachUserAdapter<ContactListModel> adapter = new AllSeachUserAdapter<>();

            public AllSearchUserBlockHolder(@NonNull View itemView) {
                super( itemView );
                binding = AllSearchVenueDesginItemBinding.bind( itemView );
                binding.allVenueRecycler.setLayoutManager( new GridLayoutManager( activity, 4, LinearLayoutManager.HORIZONTAL, false ) );
                binding.allVenueRecycler.setAdapter( adapter );
            }

            @SuppressLint("SetTextI18n")
            public void setupData(CommanSearchModel model) {
                adapter.updateData( model.getUsers() );
                binding.txtTitle.setText( model.getType().substring( 0, 1 ).toUpperCase() + model.getType().substring( 1 ) );
                binding.txtSeeAll.setOnClickListener( view -> setTabTitle( commanSearch, model.getType() ) );

            }
        }

        public class AllSearchEventBlockHolder extends RecyclerView.ViewHolder {
            private final AllSearchVenueDesginItemBinding binding;
            private final AllSearchEventAdapter<SearchEventModel> adapter = new AllSearchEventAdapter<>();

            public AllSearchEventBlockHolder(@NonNull View itemView) {
                super( itemView );
                binding = AllSearchVenueDesginItemBinding.bind( itemView );
                binding.allVenueRecycler.setLayoutManager( new LinearLayoutManager( activity, LinearLayoutManager.HORIZONTAL, false ) );
                binding.allVenueRecycler.setAdapter( adapter );
            }

            @SuppressLint("SetTextI18n")
            public void setupData(CommanSearchModel model) {
                adapter.updateData( model.getEvents() );
                binding.txtTitle.setText( model.getType().substring( 0, 1 ).toUpperCase() + model.getType().substring( 1 ) );
                binding.txtSeeAll.setOnClickListener( view -> setTabTitle( commanSearch, model.getType() ) );

            }
        }

        public class AllSearchActivityBlockHolder extends RecyclerView.ViewHolder {
            private final AllSearchVenueDesginItemBinding binding;
            private final AllSearchActivityAdapter<ActivityDetailModel> adapter = new AllSearchActivityAdapter<>();

            public AllSearchActivityBlockHolder(@NonNull View itemView) {
                super( itemView );
                binding = AllSearchVenueDesginItemBinding.bind( itemView );
                binding.allVenueRecycler.setLayoutManager( new LinearLayoutManager( activity, LinearLayoutManager.HORIZONTAL, false ) );
                binding.allVenueRecycler.setAdapter( adapter );
            }

            @SuppressLint("SetTextI18n")
            public void setupData(CommanSearchModel model) {
                adapter.updateData( model.getActivity() );
                binding.txtTitle.setText( model.getType().substring( 0, 1 ).toUpperCase() + model.getType().substring( 1 ) );
                binding.txtSeeAll.setOnClickListener( view -> setTabTitle( commanSearch, model.getType() ) );

            }
        }

        public class AllSearchTicketBlockHolder extends RecyclerView.ViewHolder {

            private final AllSearchVenueDesginItemBinding binding;

            private final AllSearchTicketAdapter<RaynaTicketDetailModel> adapter = new AllSearchTicketAdapter<>();

            public AllSearchTicketBlockHolder(@NonNull View itemView) {
                super( itemView );
                binding = AllSearchVenueDesginItemBinding.bind( itemView );
                binding.allVenueRecycler.setLayoutManager( new LinearLayoutManager( activity, LinearLayoutManager.HORIZONTAL, false ) );
                binding.allVenueRecycler.setAdapter( adapter );
            }

            @SuppressLint("SetTextI18n")
            public void setupData(CommanSearchModel model) {
                adapter.updateData( model.getTicket() );
                binding.txtTitle.setText( model.getType().substring( 0, 1 ).toUpperCase() + model.getType().substring( 1 ) );
                binding.txtSeeAll.setOnClickListener( view -> setTabTitle( commanSearch, model.getType() ) );
            }
        }

    }

    public class AllVenueSearchAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = UiUtils.getViewBy( parent, R.layout.item_search_venue );
            ViewGroup.LayoutParams params = view.getLayoutParams();
            double space = getItemCount() > 1 ? 0.85 : 0.94;
            params.width = (int) (Graphics.getScreenWidth( activity ) * space);
            view.setLayoutParams( params );
            return new ViewHolder( view );

        }

        @SuppressLint("DefaultLocale")
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            VenueObjectModel model = (VenueObjectModel) getItem( position );

            viewHolder.binding.venueContainer.setVenueDetail(model, new CommanCallback<Boolean>() {
                @Override
                public void onReceive(Boolean data) {
                    if (data){
                        SearchHistoryModel.addRecord( model.getId(), "venue", model.getName(), model.getAddress(), model.getCover(), "" ,model,null);
                    }
                }
            });

            DecimalFormat decimalFormat = new DecimalFormat( "#.##" );
            viewHolder.binding.tvDistance.setText( decimalFormat.format( model.getDistance() ) + " Km" );

            viewHolder.binding.tvDiscription.setText( TextUtils.join( ", ", model.getCuisine() ) );

            viewHolder.binding.btnFollowing.setText(model.isIsFollowing() ? "Following" : "Follow");


            viewHolder.binding.btnFollowing.setOnClickListener( view -> {
                reqOfferFollowUnFollow( model.getId(), (success, error) -> {
                    if (success) {
                        Alerter.create( activity ).setTitle( "Thank you!" ).setText( "For following " + model.getName() ).setTextAppearance( R.style.AlerterText ).setTitleAppearance( R.style.AlerterTitle ).setBackgroundColorRes( R.color.AlerterSuccessBg ).hideIcon().show();
                        model.setIsFollowing( true );
                        notifyDataSetChanged();
                    } else {
                        Alerter.create( activity ).setTitle( "Oh Snap!" ).setTitleAppearance( R.style.AlerterTitle ).setTextAppearance( R.style.AlerterText ).setText( "You have unfollowed " + model.getName() ).setBackgroundColorRes( R.color.AlerterSuccessBg ).hideIcon().show();
                        model.setIsFollowing( false );
                        notifyDataSetChanged();
                    }
                } );
            } );


            viewHolder.binding.getRoot().setOnClickListener( view -> {
                SearchHistoryModel.addRecord( model.getId(), "venue", model.getName(), model.getAddress(), model.getCover(), "" ,model,null);
                Graphics.openVenueDetail(activity,  model.getId() );
            } );


        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final ItemSearchVenueBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super( itemView );
                binding = ItemSearchVenueBinding.bind( itemView );
            }


        }


    }

    public class AllOfferSearchAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = UiUtils.getViewBy( parent, R.layout.all_search_offer_item );
            ViewGroup.LayoutParams params = view.getLayoutParams();
            double space = getItemCount() > 1 ? 0.85 : 0.94;
            params.width = (int) (Graphics.getScreenWidth( activity ) * space);
            view.setLayoutParams( params );
            return new ViewHolder( view );
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            OffersModel model = (OffersModel) getItem( position );
            viewHolder.binding.venueContainer.setVenueDetail(model.getVenue(), new CommanCallback<Boolean>() {
                @Override
                public void onReceive(Boolean data) {
                    if (data){
                        SearchHistoryModel.addRecord( model.getId(), "offer", model.getTitle(), model.getDescription(), model.getImage(), model.getVenue().getId(),null,null );
                    }
                }
            });
            Graphics.loadImage( model.getImage(), viewHolder.binding.imgCover );


            viewHolder.binding.txtTitle.setText( model.getTitle() );
            viewHolder.binding.tvDescription.setText( model.getDescription() );
            viewHolder.binding.txtDays.setText( model.getDays() );

            if (TextUtils.isEmpty(model.getStartTime())) {
                viewHolder.binding.startDate.setText("Ongoing");
                viewHolder.binding.dateTitleFrom.setVisibility(View.GONE);
                viewHolder.binding.tillDateLayout.setVisibility(View.GONE);
            } else {
                viewHolder.binding.dateTitleFrom.setVisibility(View.VISIBLE);
                viewHolder.binding.tillDateLayout.setVisibility(View.VISIBLE);
                viewHolder.binding.startDate.setText(Utils.convertMainDateFormat(model.getStartTime()));
                viewHolder.binding.endDate.setText(Utils.convertMainDateFormat(model.getEndTime()));
            }

            viewHolder.binding.offerButtonView.setupButtons(model, SearchActivity.this, getSupportFragmentManager());

            viewHolder.binding.txtOfferTime.setText(model.getOfferTiming());
            viewHolder.binding.btnTimeInfo.setVisibility(model.isShowTimeInfo() ? View.GONE : View.VISIBLE);
            viewHolder.binding.layoutTimeInfo.setOnClickListener(v -> {
                if (!model.isShowTimeInfo()) {
                    if (model == null) {
                        return;
                    }
                    if (model.getVenue() == null) {
                        return;
                    }
                    VenueTimingDialog dialog = new VenueTimingDialog(model.getVenue().getTiming(), activity);
                    dialog.show(getSupportFragmentManager(), "1");
                }
            });

            viewHolder.binding.tvLastDate.setVisibility( View.GONE );

            viewHolder.binding.getRoot().setOnClickListener( v -> {
                SearchHistoryModel.addRecord( model.getId(), "offer", model.getTitle(), model.getDescription(), model.getImage(), model.getVenue().getId(),null,null );
                OfferDetailBottomSheet dialog = new OfferDetailBottomSheet();
                dialog.offerId = model.getId();
                dialog.show(getSupportFragmentManager(), "");

            } );

            viewHolder.binding.btnMenu.setOnClickListener( view -> {
                Utils.preventDoubleClick( view );
                ArrayList<String> data = new ArrayList<>();
                data.add( "Add To BucketList" );
                data.add(model.getVenue().isIsFollowing() ? "UnFollow" : "Follow");
                data.add( "Share Venue" );
                data.add( "Share Offer" );
                data.add(model.getVenue().isRecommendation() ? "Remove recommendation" : "Add recommendation");
                Graphics.showActionSheet( activity, model.getVenue().getName(), data, (data1, position1) -> {
                    switch (position1) {
                        case 0:
                            BucketListBottomSheet dialog = new BucketListBottomSheet();
                            dialog.offerId = model.getId();
                            dialog.show( getSupportFragmentManager(), "" );
                            break;
                        case 1:
                            reqOfferFollowUnFollow( model.getVenue().getId(), (success, error) -> {
                                if (success) {
                                    Alerter.create( activity ).setTitle( "Thank you!" ).setText( "For following " + model.getVenue().getName() ).setTextAppearance( R.style.AlerterText ).setTitleAppearance( R.style.AlerterTitle ).setBackgroundColorRes( R.color.AlerterSuccessBg ).hideIcon().show();
                                    model.getVenue().setIsFollowing( true );
                                    notifyDataSetChanged();
                                } else {
                                    Alerter.create( activity ).setTitle( "Oh Snap!" ).setTitleAppearance( R.style.AlerterTitle ).setTextAppearance( R.style.AlerterText ).setText( "You have unfollowed " + model.getVenue().getName() ).setBackgroundColorRes( R.color.AlerterSuccessBg ).hideIcon().show();
                                    model.getVenue().setIsFollowing( false );
                                    notifyDataSetChanged();
                                }
                            } );
                            break;
                        case 2:
                            startActivity( new Intent( activity, VenueShareActivity.class ).putExtra( "venue", new Gson().toJson( model.getVenue() ) )
                                    .putExtra( "type", "venue" ) );
                            break;
                        case 3:
                            startActivity( new Intent( activity, VenueShareActivity.class ).putExtra( "offer", new Gson().toJson( model) )
                                    .putExtra( "type", "offer" ) );
                            break;
                        case 4:
                            reqRecommendation(model.getVenue().getId(),model.getVenue());
                            break;
                    }

                } );
            } );



        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final AllSearchOfferItemBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super( itemView );
                binding = AllSearchOfferItemBinding.bind( itemView );
            }
        }
    }

    public class AllSeachUserAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = UiUtils.getViewBy( parent, R.layout.search_user_item );
            ViewGroup.LayoutParams params = view.getLayoutParams();
            double space = getItemCount() > 1 ? 0.85 : 0.94;
            params.width = (int) (Graphics.getScreenWidth( activity ) * space);
            view.setLayoutParams( params );
            return new ViewHolder( view );
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            ContactListModel listModel = (ContactListModel) getItem( position );

            viewHolder.vBinding.tvUserName.setText( listModel.getFullName() );
            Graphics.loadImageWithFirstLetter( listModel.getImage(), viewHolder.vBinding.ivUserProfile, listModel.getFullName() );

            viewHolder.vBinding.getRoot().setOnClickListener( view -> {
                if (!Utils.isUser( listModel.getId() )) {
                    SearchHistoryModel.addRecord( listModel.getId(), "user", listModel.getFullName(), "", listModel.getImage(), "",null,listModel );
                    if (SessionManager.shared.getUser().isRingMember() && listModel.isPromoter()) {
                        startActivity(new Intent(activity, PromoterPublicProfileActivity.class)
                                .putExtra("isFromOtherUserProfile", true)
                                .putExtra("isPromoterProfilePublic", true).putExtra("id", listModel.getId()));
                    } else if (SessionManager.shared.getUser().isPromoter() && listModel.isRingMember()) {
                        startActivity(new Intent(activity, CmPublicProfileActivity.class)
                                .putExtra("isFromOtherUserProfile", true)
                                .putExtra("promoterUserId", listModel.getId()));
                    } else {
                        startActivity(new Intent(activity, OtherUserProfileActivity.class).putExtra("friendId", listModel.getId()));
                    }
//                    startActivity( new Intent( activity, OtherUserProfileActivity.class ).putExtra( "friendId", listModel.getId() ) );
                }
            } );

            if (listModel.isSynced()) {
                viewHolder.vBinding.optionContainer.setVisibility(listModel.isSynced() ? View.VISIBLE : View.GONE);
                viewHolder.vBinding.optionContainer.setupView(listModel, activity, data -> {
                    searchResultAdapter.notifyDataSetChanged();
                    allSearchResultAdapter.notifyDataSetChanged();
                });
            }


            if (Utils.isUser( listModel.getId() )) {
                viewHolder.vBinding.optionContainer.setVisibility( View.GONE );
            }

        }

        private void openChat(ContactListModel listModel){
            ChatModel chatModel = new ChatModel();
            chatModel.setImage( listModel.getImage() );
            chatModel.setTitle( listModel.getFullName() );
            chatModel.setChatType( "friend" );
            RealmList<String> idList = new RealmList<>();
            idList.add( SessionManager.shared.getUser().getId());
            idList.add( listModel.getId());
            chatModel.setMembers(idList);
            Collections.sort(idList);
            String joinedString = String.join(",", idList);
            chatModel.setChatId(joinedString);

            Intent intent = new Intent(activity, ChatMessageActivity.class);
            intent.putExtra("chatModel", new Gson().toJson(chatModel));
            intent.putExtra("isChatId", true);
            intent.putExtra( "type","friend" );
            startActivity(intent);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final SearchUserItemBinding vBinding;

            public ViewHolder(@NonNull View itemView) {
                super( itemView );
                vBinding = SearchUserItemBinding.bind( itemView );
            }
        }

    }

    public class AllSearchEventAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = UiUtils.getViewBy( parent, R.layout.iteam_search_event );
            ViewGroup.LayoutParams params = view.getLayoutParams();
            double space = getItemCount() > 1 ? 0.85 : 0.94;
            params.width = (int) (Graphics.getScreenWidth( activity ) * space);
            view.setLayoutParams( params );
            return new ViewHolder( view );
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            ViewHolder viewHolder = (ViewHolder) holder;
            SearchEventModel model = (SearchEventModel) getItem( position );
            viewHolder.binding.eventTitle.setText( model.getTitle() );
            viewHolder.binding.tvDescription.setText( model.getDescription() );

            if (model.getVenue() != null) {
//                Graphics.applyBlurEffect(activity, viewHolder.binding.blurView);
                viewHolder.binding.venueContainer.setVenueDetail(model.getVenue(), new CommanCallback<Boolean>() {
                    @Override
                    public void onReceive(Boolean data) {
                        if (data){
                            SearchHistoryModel.addRecord( model.getId(), "event", model.getOrgData().getName(), model.getOrgData().getWebsite(), model.getOrgData().getLogo(), "",null ,null);
                        }
                    }
                });
            }

//            Graphics.applyBlurEffect(activity, viewHolder.binding.venueBlurView);
            Graphics.loadImage( model.getImage(), viewHolder.binding.ivCover );
            viewHolder.binding.tvLastDate.setVisibility( View.GONE );
            String startTime, endTime;


            if (model.getOrgData() != null) {
                Graphics.loadImageWithFirstLetter(model.getOrgData().getLogo(), viewHolder.binding.imgOrg, model.getOrgData().getName());
                viewHolder.binding.tvOrgName.setText(model.getOrgData().getName());
                viewHolder.binding.tvWebsite.setText(model.getOrgData().getWebsite() != null ? model.getOrgData().getWebsite() : "");
                viewHolder.binding.tvWebsite.setVisibility(model.getOrgData().getWebsite() != null ? View.VISIBLE : View.GONE);
            }

            viewHolder.binding.linearEvent.setOnClickListener(v -> {
                if (model.getOrgData() != null) {
                    startActivity(new Intent(activity, EventOrganizerDetailsActivity.class).putExtra("org_id", model.getOrgId()).putExtra("type", "events_organizers").putExtra("name", model.getOrgData().getName()).putExtra("webSite", model.getOrgData().getWebsite()).putExtra("image", model.getOrgData().getLogo()));
                }
            });

            if (model.getEventTime() != null) {
                Utils.setTimer( model.getEventTime(), viewHolder.binding.countTimer );
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                try {
                    Date givenDate = dateFormat.parse(model.getEventTime());
                    Date currentDate = new Date();
                    if (givenDate.before(currentDate)) {
                        viewHolder.binding.layoutTimer.setVisibility(View.INVISIBLE);
                    } else  if (givenDate.after(currentDate)) {
                        viewHolder.binding.layoutTimer.setVisibility(View.VISIBLE);
                    } else {
                        viewHolder.binding.layoutTimer.setVisibility(View.INVISIBLE);
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            viewHolder.itemView.setOnClickListener( view -> {
                SearchHistoryModel.addRecord( model.getId(), "event", model.getOrgData().getName(), model.getOrgData().getWebsite(), model.getOrgData().getLogo(), "",null ,null);
                startActivity( new Intent( activity, EventDetailsActivity.class )
                        .putExtra( "name", model.getTitle() )
                        .putExtra( "eventId", model.getId() )
                        .putExtra( "image", model.getImage())

                        .putExtra( "address",model.getDescription() )
                );
            } );

            try {
                startTime = Utils.formatDate( Utils.stringToDate( model.getReservationTime(), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" ), "HH:mm" );
                endTime = Utils.formatDate( Utils.stringToDate( model.getEventTime(), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" ), "HH:mm" );

                SimpleDateFormat inputFormat = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US );
                inputFormat.setTimeZone( TimeZone.getTimeZone( "UTC" ) );
                Date date = inputFormat.parse( model.getEventTime() );

                // Format the Date object into the desired output format
                SimpleDateFormat outputFormat = new SimpleDateFormat( "EEE, dd MMM yyyy", Locale.US );
                String formattedDate = outputFormat.format( date );

                viewHolder.binding.endTime.setText( formattedDate );
                viewHolder.binding.startTime.setText( startTime + " " + endTime );


            } catch (Exception e) {
                throw new RuntimeException( e );
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final IteamSearchEventBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super( itemView );
                binding = IteamSearchEventBinding.bind( itemView );
            }
        }
    }

    public class AllSearchActivityAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = UiUtils.getViewBy( parent, R.layout.item_search_activity );
            ViewGroup.LayoutParams params = view.getLayoutParams();
            double space = getItemCount() > 1 ? 0.85 : 0.94;
            params.width = (int) (Graphics.getScreenWidth( activity ) * space);
            view.setLayoutParams( params );
            return new ViewHolder( view );

        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            ViewHolder viewHolder = (ViewHolder) holder;
            ActivityDetailModel model = (ActivityDetailModel) getItem( position );
            viewHolder.binding.tvName.setText( model.getName() );
            viewHolder.binding.tvAddress.setText( model.getDescription() );
            viewHolder.binding.tvPrice.setText( String.valueOf( model.getPrice() ) );


            if (model.getProvider() != null) {
                viewHolder.binding.tvTitle.setText( model.getProvider().getName() );
                viewHolder.binding.tvAddress.setText( model.getProvider().getAddress() );
                Graphics.loadRoundImage( model.getProvider().getLogo(), viewHolder.binding.iconImg );

            }

            Graphics.loadImage( model.getGalleries().get( 0 ), viewHolder.binding.ivCover );
//            Graphics.applyBlurEffect( activity, viewHolder.binding.blurView );
            viewHolder.binding.tvLastDate.setVisibility( View.GONE );

            viewHolder.binding.tvStartTime.setText( Utils.convertMainDateFormat( model.getStartDate() ) );
            viewHolder.binding.tvEndDate.setText( Utils.convertMainDateFormat( model.getEndDate() ) );


            viewHolder.itemView.setOnClickListener( view -> {
                SearchHistoryModel.addRecord( model.getId(), "activity", model.getName(), model.getDescription(), model.getGalleries().get( 0 ), "" ,null,null);
                Intent intent = new Intent( activity, ActivityListDetail.class );
                intent.putExtra( "activityId", model.getId() )
                        .putExtra( "type", "activities" )
                        .putExtra( "name", model.getName() )
                        .putExtra( "image", model.getProvider().getLogo() )
                        .putExtra( "title", model.getProvider().getName() )
                        .putExtra( "address", model.getProvider().getAddress()
                        );
                startActivity( intent );
            } );

        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final ItemSearchActivityBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super( itemView );
                binding = ItemSearchActivityBinding.bind( itemView );
            }
        }
    }

    public class AllSearchTicketAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = UiUtils.getViewBy(parent, R.layout.item_all_serach_ticket);
            ViewGroup.LayoutParams params = view.getLayoutParams();
            double space = getItemCount() > 1 ? 0.85 : 0.94;
            params.width = (int) (Graphics.getScreenWidth( activity ) * space);
            view.setLayoutParams( params );
            return new ViewHolder(view);

        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (activity == null) {
                return;
            }
            ViewHolder viewHolder = (ViewHolder) holder;
            RaynaTicketDetailModel model = (RaynaTicketDetailModel) getItem(position);
            if (model == null) return;

            activity.runOnUiThread(() -> {

                if (model.getAvg_ratings() != 0){
                    double truncatedRating = Math.floor(model.getAvg_ratings() * 10) / 10.0;
                    viewHolder.mBinding.tvRate.setText(String.format(Locale.ENGLISH, "%.1f", truncatedRating));
                    viewHolder.mBinding.ticketRatingLayout.setVisibility(View.VISIBLE);
                }else {
                    viewHolder.mBinding.ticketRatingLayout.setVisibility(View.GONE);
                }

                viewHolder.mBinding.ticketTag.setVisibility(model.isTicketRecentlyAdded() ? View.VISIBLE : View.GONE);


                viewHolder.mBinding.ticketAddress.setText(model.getCity());


                if (model.getImages() != null && !model.getImages().isEmpty()) {
                    List<String> urls = model.getImages();
                    urls.removeIf(Utils::isVideo);
                    viewHolder.setupData(model.getImages(),model);
                }

                String discount = String.valueOf(model.getDiscount());

                if (!"0".equals(discount)) {
                    viewHolder.mBinding.tvDiscount.setText(discount.contains("%") ? discount : discount + "%");
                    viewHolder.mBinding.tvDiscount.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.mBinding.tvDiscount.setVisibility(View.GONE);
                }


                viewHolder.mBinding.txtTitle.setText(Utils.notNullString(model.getTitle()));

                String startingAmount = model.getStartingAmount() != null ? String.valueOf(model.getStartingAmount()) : "N/A";


                if (!"N/A".equals(startingAmount)) {
                    viewHolder.mBinding.ticketFromAmount.setText(model.getDiscountAndStartingAmount(activity,viewHolder.mBinding.discountText));
                } else {
                    SpannableString styledPrice = Utils.getStyledText(activity, "0");
                    SpannableStringBuilder fullText = new SpannableStringBuilder()
                            .append(styledPrice);
                    viewHolder.mBinding.ticketFromAmount.setText(fullText);
                }


                if (startingAmount.equals("N/A")) {
                    Utils.setStyledText(activity, viewHolder.mBinding.tvAED, "0");
                } else {
                    Utils.setStyledText(activity, viewHolder.mBinding.tvAED, Utils.roundFloatValue(Float.valueOf(startingAmount)));
                }

                if (TextUtils.isEmpty(startingAmount) || startingAmount.equals("0") || startingAmount.equals("N/A")) {
                    viewHolder.mBinding.roundLinear.setVisibility(View.GONE);
                    viewHolder.mBinding.startingFromLayout.setVisibility(View.GONE);
                } else {
                    viewHolder.mBinding.roundLinear.setVisibility(View.VISIBLE);
                    viewHolder.mBinding.startingFromLayout.setVisibility(View.VISIBLE);
                }


                viewHolder.mBinding.getRoot().setOnClickListener(view -> {
                    Utils.preventDoubleClick(view);
                    activity.startActivity(new Intent(activity, RaynaTicketDetailActivity.class).putExtra("ticketId",model.getId()));
                    String image = "";
                    if (!model.getImages().isEmpty()){
                        image = model.getImages().get(0);
                    }
                    SearchHistoryModel.addRecord( model.getId(), "ticket", model.getTitle(), model.getCity(),image, "" ,null,null,model);

                });

                Graphics.setFavoriteIcon(activity,model.isIs_favorite(),viewHolder.mBinding.ivFavourite);

                viewHolder.mBinding.ivFavourite.setOnClickListener(v -> {
                    viewHolder.showProgress(true);
                    RaynaTicketManager.shared.requestRaynaTicketFavorite(activity, model.getId(), (success, error) -> {
                        if (success) {
                            viewHolder.showProgress(false);
                            boolean newState = !model.isIs_favorite();
                            model.setIs_favorite(newState);
                            if (newState) {
                                LogManager.shared.logTicketEvent(LogManager.LogEventType.addToWishlist, model.getId(), model.getTitle(), 0.0, null, "AED");
                            }
                            String title = newState ? Utils.getLangValue("thank_you") :  Utils.getLangValue("oh_snap");
//                            String message = model.getTitle() + (newState ? " has been added to your favorites." : " has been removed from your favorites.");
                            String message = newState ? Utils.setLangValue("add_favourite",model.getTitle()) : Utils.setLangValue("remove_favourite",model.getTitle());

                            Alerter.create(activity)
                                    .setTitle(title)
                                    .setText(message)
                                    .setTitleAppearance(R.style.AlerterTitle)
                                    .setTextAppearance(R.style.AlerterText)
                                    .setBackgroundColorRes(R.color.white_color)
                                    .hideIcon()
                                    .show();

                            notifyItemChanged(position);
                            EventBus.getDefault().postSticky(model);
                        } else {
                            viewHolder.showProgress(false);
                            Toast.makeText(activity, error != null ? error : "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    });
                });

            });
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final ItemAllSerachTicketBinding mBinding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemAllSerachTicketBinding.bind(itemView);
                mBinding.tvFromTitle.setText(Utils.getLangValue("from"));
                mBinding.tvStatingTile.setText(String.format("%s ", Utils.getLangValue("starting")));
                mBinding.tvRecentlyAddedTitle.setText(Utils.getLangValue("recently_added"));
            }

            private void showProgress(boolean isShowLoader){
                mBinding.ivFavourite.setVisibility(isShowLoader ? View.GONE : View.VISIBLE);
                mBinding.favTicketProgressBar.setVisibility(isShowLoader ? View.VISIBLE : View.GONE);
            }

            public void setupData(List<String> imaegs , RaynaTicketDetailModel raynaTicketDetailModel) {
                RaynaTicketImageAdapter adapter = new RaynaTicketImageAdapter(activity, raynaTicketDetailModel, imaegs, data -> {
                    String image = "";
                    if (!imaegs.isEmpty()){
                        image = imaegs.get(0);
                    }
                    SearchHistoryModel.addRecord( raynaTicketDetailModel.getId(), "ticket", raynaTicketDetailModel.getTitle(), raynaTicketDetailModel.getCity(),image, "" ,null,null,raynaTicketDetailModel);
                });
                mBinding.viewPager.setAdapter( adapter );
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            int nextPage = mBinding.viewPager.getCurrentItem() + 1;
                            if (nextPage == adapter.getCount()) {
                                nextPage = 0;
                            }
                            mBinding.viewPager.setCurrentItem( nextPage, true );
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            ticketHandler.postDelayed( this, 4000 );
                        }
                    }
                };
                ticketHandler.postDelayed( runnable, 4000 );

                mBinding.viewPager.addOnPageChangeListener( new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        ticketHandler.removeCallbacks( runnable );
                        ticketHandler.postDelayed( runnable, 4000 );
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                    }
                } );

            }
        }
    }

    // endregion
    // --------------------------------------
    // region History Result Adapter
    // --------------------------------------


    public class SearchHistoryAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == 1) {
                return new ViewHolder( UiUtils.getViewBy( parent, R.layout.item_recent_search ) );
            } else {
                return new UserHolder( UiUtils.getViewBy( parent, R.layout.item_recent_search_user ) );
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            SearchHistoryModel model = (SearchHistoryModel) getItem( position );
            if (getItemViewType( position ) == 1) {
                ViewHolder viewHolder = (ViewHolder) holder;
                viewHolder.binding.tvName.setText( model.getTitle() );
                String result = model.getType().substring( 0, 1 ).toUpperCase() + model.getType().substring( 1 );
                viewHolder.binding.subTitle.setText( result );

                Graphics.loadImageWithFirstLetter( model.getImage(), viewHolder.binding.ivSearchCover, model.getTitle() );

                viewHolder.binding.constraintRecent.setOnClickListener( v -> {
                    Utils.preventDoubleClick(v);
                    if (model.getType().equals( "offer" )) {
                        OfferDetailBottomSheet dialog = new OfferDetailBottomSheet();
                        dialog.offerId = model.getId();
                        dialog.show(getSupportFragmentManager(), "");
                        //startActivity(new Intent(activity, OfferDetailActivity.class).putExtra("offerId",  model.getId()));
                    } else if (model.getType().equals( "venue" )) {
                        Graphics.openVenueDetail(activity,  model.getId() );
                    } else if (model.getType().equals( "event" )) {
                        startActivity( new Intent( activity, EventDetailsActivity.class )
                                .putExtra( "name", model.getTitle() )
                                .putExtra( "eventId", model.getId() )
                                .putExtra( "image", model.getImage() )
                                .putExtra( "address", result )

                        );
                    } else if (model.getType().equals( "activity" )) {
                        startActivity( new Intent( activity, ActivityListDetail.class )
                                .putExtra( "activityId", model.getId() )
                                .putExtra( "type", "activities" )
                                .putExtra( "name", model.getTitle() )
                                .putExtra( "image", model.getImage() )
                                .putExtra( "title", model.getTitle() )
                                .putExtra( "address", result )

                        );
//                        activity.overridePendingTransition( R.anim.slide_up, R.anim.fade_out );
                    } else if (model.getType().equals( "user" )) {
                        startActivity( new Intent( activity, OtherUserProfileActivity.class ) );
                    } else if (model.getType().equals("ticket")) {
                        startActivity(new Intent(activity, RaynaTicketDetailActivity.class).putExtra("ticketId",model.getId()));
                    }
                } );


                viewHolder.binding.ivClear.setOnClickListener( v -> {
                    SearchHistoryModel.removeRecord( model.getId() );
                    showRecentSearch();
                } );
            } else {
                UserHolder viewHolder = (UserHolder) holder;
                viewHolder.binding.tvName.setText( model.getTitle() );
                String result = model.getType().substring( 0, 1 ).toUpperCase() + model.getType().substring( 1 );
                viewHolder.binding.subTitle.setText( result );
                Graphics.loadImageWithFirstLetter( model.getImage(), viewHolder.binding.userProfile, model.getTitle() );

                viewHolder.binding.constraintRecent.setOnClickListener( v -> {
                    if (model.getContactListModel() == null){return ;}
                    if (SessionManager.shared.getUser().isRingMember() && model.getContactListModel().isPromoter()) {
                        startActivity(new Intent(activity, PromoterPublicProfileActivity.class)
                                .putExtra("isFromOtherUserProfile", true)
                                .putExtra("isPromoterProfilePublic", true).putExtra("id", model.getContactListModel().getId()));
                    } else if (SessionManager.shared.getUser().isPromoter() && model.getContactListModel().isRingMember()) {
                        startActivity(new Intent(activity, CmPublicProfileActivity.class)
                                .putExtra("isFromOtherUserProfile", true)
                                .putExtra("promoterUserId", model.getContactListModel().getId()));
                    } else {
                        startActivity(new Intent(activity, OtherUserProfileActivity.class).putExtra("friendId", model.getContactListModel().getId()));
                    }
//                    startActivity( new Intent( activity, OtherUserProfileActivity.class ).putExtra( "friendId", model.getId() ) );
                } );

                viewHolder.binding.ivClear.setOnClickListener( v -> {
                    SearchHistoryModel.removeRecord( model.getId() );
                    showRecentSearch();
                } );
            }
        }

        public int getItemViewType(int position) {
            SearchHistoryModel model = (SearchHistoryModel) getItem( position );
            if (model.getType().equals( "user" )) {
                return 0;
            } else {
                return 1;
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final ItemRecentSearchBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super( itemView );
                binding = ItemRecentSearchBinding.bind( itemView );
            }

        }

        public class UserHolder extends RecyclerView.ViewHolder {

            private final ItemRecentSearchUserBinding binding;

            public UserHolder(@NonNull View itemView) {
                super( itemView );
                binding = ItemRecentSearchUserBinding.bind( itemView );
            }

        }
    }

    public class SearchTabHistory<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder( UiUtils.getViewBy( parent, R.layout.item_recent_search_tab ) );

        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            RatingModel model = (RatingModel) getItem( position );
            viewHolder.binding.tvSearch.setText( model.getImage() );

            viewHolder.itemView.setOnClickListener( view -> {
                ignoreTextChange = true;
                binding.edtSearch.setText( model.getImage() );
                binding.edtSearch.setSelection( binding.edtSearch.getText().length() );
                Utils.hideKeyboard( activity );
                requestCommanSearch( model.getImage() );
                ignoreTextChange = false;
            } );

        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final ItemRecentSearchTabBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super( itemView );
                binding = ItemRecentSearchTabBinding.bind( itemView );
            }
        }

    }


    // endregion
    // --------------------------------------
    // region Filter Result Adapter
    // --------------------------------------

    private class TicketShimmerEffectAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.ticket_shimmer_placeholder));

        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            RatingModel model = (RatingModel) getItem( position );
            Log.d("RatingModel", "onBindViewHolder: " + 111111);
            viewHolder.binding.shimmerLayout.startShimmer();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final TicketShimmerPlaceholderBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = TicketShimmerPlaceholderBinding.bind(itemView);
            }
        }

    }

    public static class SearchPopupAdapter extends BaseAdapter implements Filterable {
        private final Context context;
        private List<String> items = new ArrayList<>();

        public SearchPopupAdapter(Context context) {
            this.context = context;
        }

        public void updateItems(List<String> newItems) {
            this.items = newItems;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public String getItem(int position) {
            if (position >= items.size()) {
                return "";
            }
            return items.get(position);
        }


        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(context).inflate(R.layout.search_popup_menu_item, parent, false);
            TextView textView = view.findViewById(R.id.popup_item);
            View divider = view.findViewById(R.id.headerSeparatorView);
            if (position == getCount() - 1) {
                divider.setVisibility(View.GONE);
            } else {
                divider.setVisibility(View.VISIBLE);
            }

            textView.setText(items.get(position));
            return view;
        }

        // 🔁 Add dummy Filter so AutoCompleteTextView accepts this adapter
        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();
                    results.values = items;
                    results.count = items.size();
                    return results;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    // No need to update here since we call updateItems() manually
                }
            };
        }
    }



    // endregion
    // --------------------------------------

}




