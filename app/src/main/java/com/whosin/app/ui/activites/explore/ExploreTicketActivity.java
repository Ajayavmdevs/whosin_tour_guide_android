package com.whosin.app.ui.activites.explore;

import static android.view.View.VISIBLE;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.tapadoo.alerter.Alerter;
import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.HorizontalSpaceItemDecoration;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.comman.ui.VerticalSpaceItemDecoration;
import com.whosin.app.databinding.ActivityExploreTicketBinding;
import com.whosin.app.databinding.FilterTagItemBinding;
import com.whosin.app.databinding.ItemAdViewExploreTickerViewBinding;
import com.whosin.app.databinding.ItemNewExploreTicketBinding;
import com.whosin.app.databinding.TicketExploreShimerPlaceholderBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.AppSettingManager;
import com.whosin.app.service.manager.LogManager;
import com.whosin.app.service.manager.RaynaTicketManager;
import com.whosin.app.service.manager.SearchSuggestionStore;
import com.whosin.app.service.models.CategoriesModel;
import com.whosin.app.service.models.ContainerListModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.RatingModel;
import com.whosin.app.service.models.rayna.RaynaTicketDetailModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.comman.BaseActivity;
import com.whosin.app.ui.activites.raynaTicket.RaynaTicketDetailActivity;
import com.whosin.app.ui.adapter.raynaTicketAdapter.RaynaTicketImageAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;

public class ExploreTicketActivity extends BaseActivity {

    private ActivityExploreTicketBinding binding;
    private final PreferencesListAdapter<CategoriesModel> listAdapter = new PreferencesListAdapter<>();
    private final TicketAdapter<RaynaTicketDetailModel> ticketAdapter = new TicketAdapter<>();
    private final TicketShimmerEffectAdapter<RatingModel> shimmerEffectAdapter = new TicketShimmerEffectAdapter<>();
    private List<CategoriesModel> filterList = new ArrayList<>();
    private Call<ContainerListModel<RaynaTicketDetailModel>> service = null;
    private List<RaynaTicketDetailModel> ticketList = new ArrayList<>();
    private String searchQuery = "";
    private int page = 1;
    private boolean isLoading = false;
    private Runnable runnable = this::requestRaynaSearchSuggestions;
    private Handler handlerForSerach = new Handler();
    private boolean isShowAdd = false;
    private CommanCallback<Boolean> callback;
    private Call<ContainerModel<List<String>>> suggestionService = null;
    private SearchPopupAdapter adapter = new SearchPopupAdapter(this);
    private boolean ignoreTextChange = false;

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------


    @Override
    protected void initUi() {

        binding.edtSearch.setHint(getValue("explore"));
        binding.emptyPlaceHolderView.setEmptyPlaceTxtTitle(getValue("search_experiences"));

        Graphics.applyBlurEffect(activity,binding.blurView);

        binding.edtSearch.setAdapter(adapter);
        binding.edtSearch.setDropDownBackgroundResource(R.drawable.dropdown_bg);
        binding.edtSearch.setDropDownAnchor(R.id.edtSearch);
        binding.edtSearch.setDropDownVerticalOffset(0);
        binding.edtSearch.setDropDownHeight(400);

        setupRecycleHorizontalManager(binding.ticketRecyclerView);
        binding.ticketRecyclerView.setAdapter(ticketAdapter);

        binding.selectedItemList.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        binding.selectedItemList.setAdapter(listAdapter);

        Utils.showSoftKeyboard(activity,binding.getRoot());
        binding.edtSearch.setCursorVisible(true);
        binding.edtSearch.requestFocus();


        if (!AppSettingManager.shared.filterList.isEmpty()){
            filterList.addAll(AppSettingManager.shared.filterList);
            binding.emptyPlaceHolderView.setVisibility(View.GONE);
            binding.selectedItemList.setVisibility(View.VISIBLE);
            listAdapter.updateData(filterList);
            requestTicketList(false);
            AppSettingManager.shared.filterList.clear();
        }else {
            binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
        }

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
                searchQuery = selectedText;
                if (runnable != null) {
                    handlerForSerach.removeCallbacks(runnable);
                }

                binding.ticketRecyclerView.setVisibility(View.GONE);
                page = 1;
                ticketList.clear();
                relasePlayerCallBack();
                isShowAdd = false;
                ignoreTextChange = true;
                suggestionService.cancel();
                binding.edtSearch.setText(selectedText);
                binding.edtSearch.dismissDropDown();
                binding.edtSearch.setSelection( binding.edtSearch.getText().length() );
                Utils.hideKeyboard( activity );
                binding.edtSearch.setBackgroundResource(R.drawable.rounded_full_bg_search);
                requestTicketList(false);
                ignoreTextChange = false;
            }
        });

        binding.imgFilter.setOnClickListener(v -> {
            Utils.preventDoubleClick(v);
            NewExploreFilterBottomSheet dialog = new NewExploreFilterBottomSheet();
            dialog.callback = data -> {
                binding.selectedItemList.setVisibility(View.VISIBLE);
                filterList = data;
                listAdapter.updateData(data);
                page = 1;
                ticketList.clear();
                relasePlayerCallBack();
                isShowAdd = false;
                requestTicketList(false);
            };
            dialog.filterList = filterList;
            dialog.show(getSupportFragmentManager(), "");
        });

        binding.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (binding.edtSearch.isPerformingCompletion()) {
                    return;
                }
                if (ignoreTextChange) return;
                String query = s.toString().trim();
                searchQuery = query;

                if (!query.isEmpty()) {
                    List<String> list = SearchSuggestionStore.get(binding.edtSearch.getText().toString());
                    if (!list.isEmpty()) {
                        adapter.updateItems(list);
                        binding.edtSearch.setBackgroundResource(R.drawable.rounded_top_only_bg);
                    }else {
                        binding.edtSearch.dismissDropDown();
                        binding.edtSearch.setBackgroundResource(R.drawable.rounded_full_bg_search );
                    }
                    handlerForSerach.removeCallbacks(runnable);
                    handlerForSerach.postDelayed(runnable, 400);
                }else {
                    if (filterList.isEmpty() && searchQuery.isEmpty()){
                        binding.ticketRecyclerView.setVisibility(View.GONE);
                        binding.emptyPlaceHolderView.setVisibility(VISIBLE);
                        if (service != null) service.cancel();
                        if (suggestionService != null) suggestionService.cancel();
                        binding.edtSearch.setBackgroundResource(R.drawable.rounded_full_bg_search);
                        playPauseVideos(false);
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    binding.emptyPlaceHolderView.setVisibility(View.GONE);
                    if (filterList.isEmpty() && searchQuery.isEmpty()){
                        binding.ticketRecyclerView.setVisibility(View.GONE);
                        binding.emptyPlaceHolderView.setVisibility(VISIBLE);
                        if (service != null) service.cancel();
                        if (suggestionService != null) suggestionService.cancel();
                        playPauseVideos(false);
                        binding.edtSearch.setBackgroundResource(R.drawable.rounded_full_bg_search);
                    }
                }
            }
        });

        binding.imgBack.setOnClickListener(v -> {
            Utils.preventDoubleClick(v);
            finish();
            overridePendingTransition(0, 0);
        });

        binding.ticketRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int lastVisible = layoutManager.findLastVisibleItemPosition();
                int totalItems = ticketAdapter.getItemCount();

                if (!isLoading && lastVisible == totalItems - 1 && totalItems % 20 == 0 && !ticketAdapter.getData().isEmpty()) {
                    isLoading = true;
                    page++;
                    requestTicketList(true); // Ensure this method resets isLoading = false when complete
                }
            }
        });


        binding.edtSearch.setOnEditorActionListener((textView, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                ignoreTextChange = true;
                binding.edtSearch.dismissDropDown();
                binding.edtSearch.setBackgroundResource(R.drawable.rounded_full_bg_search );
                if (!TextUtils.isEmpty(searchQuery)) {
                    ticketList.clear();
                    requestTicketList(false);
                }
                Utils.hideKeyboard(activity);
                ignoreTextChange = false;
                return true;
            }
            return false;
        });

        binding.serachLayout.setOnClickListener(v -> {
            binding.edtSearch.setCursorVisible(true);
            binding.edtSearch.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(binding.edtSearch, InputMethodManager.SHOW_IMPLICIT);
            }
        });


        binding.searchLayout.setOnClickListener(v -> {
            binding.edtSearch.setCursorVisible(true);
            binding.edtSearch.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(binding.edtSearch, InputMethodManager.SHOW_IMPLICIT);
            }
        });


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
                        if (viewHolder instanceof TicketAdapter.HomeAdHolder) {
                            View itemView = layoutManager.findViewByPosition(i);
                            if (itemView != null) {
                                int itemHeight = itemView.getHeight();
                                int visibleHeight = Math.min(recyclerView.getHeight(), itemView.getBottom()) - Math.max(0, itemView.getTop());
                                double visiblePercentage = (double) visibleHeight / itemHeight;

                                TicketAdapter.HomeAdHolder newVideoViewHolder = (TicketAdapter.HomeAdHolder) viewHolder;
                                newVideoViewHolder.onItemVisibilityChanged(visiblePercentage >= 0.7);
                            }
                        }
                    }
                }
            }
        };


        binding.ticketRecyclerView.addOnScrollListener(scrollListener);
    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityExploreTicketBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }

    @Override
    public void onPause() {
        super.onPause();
        playPauseVideos(false);
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------


    private JsonObject getJsonObject() {
        JsonObject object = new JsonObject();
        object.addProperty("page", page);
        object.addProperty("limit", 20);
        object.addProperty("search", searchQuery);
        object.add("cityIds", getFilteredCityIds(filterList));
        object.add("categoryIds", getFilteredCategoryIds(filterList));
        return object;
    }

    private JsonArray getFilteredCityIds(List<CategoriesModel> filterList) {
        JsonArray filterIds = new JsonArray();
        filterList.stream()
                .filter(model -> !TextUtils.isEmpty(model.getName()))
                .map(CategoriesModel::getId)
                .filter(id -> id != null && !id.isEmpty())
                .forEach(filterIds::add);
        return filterIds;
    }

    private JsonArray getFilteredCategoryIds(List<CategoriesModel> filterList) {
        JsonArray filterIds = new JsonArray();
        filterList.stream()
                .filter(model -> TextUtils.isEmpty(model.getName()))
                .map(CategoriesModel::getId)
                .filter(id -> id != null && !id.isEmpty())
                .forEach(filterIds::add);
        return filterIds;
    }

    private void setupRecycleHorizontalManager(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        int spacing = getResources().getDimensionPixelSize(com.intuit.ssp.R.dimen._10ssp);
        recyclerView.addItemDecoration(new HorizontalSpaceItemDecoration(spacing));
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(spacing));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        recyclerView.offsetChildrenHorizontal(1);
    }

    private void playPauseVideos(boolean isPlay) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) binding.ticketRecyclerView.getLayoutManager();
        if (layoutManager != null) {
            int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
            int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();

            for (int i = firstVisibleItemPosition; i <= lastVisibleItemPosition; i++) {
                RecyclerView.ViewHolder viewHolder = binding.ticketRecyclerView.findViewHolderForAdapterPosition(i);
                 if (viewHolder instanceof TicketAdapter.HomeAdHolder) {
                    TicketAdapter.HomeAdHolder homeAdHolder = (TicketAdapter.HomeAdHolder) viewHolder;
                    if (isPlay) {
                        View itemView = layoutManager.findViewByPosition(i);
                        if (itemView != null) {
                            int itemHeight = itemView.getHeight();
                            int visibleHeight = Math.min(binding.ticketRecyclerView.getHeight(), itemView.getBottom()) - Math.max(0, itemView.getTop());
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

     private void relasePlayerCallBack(){
        if (callback != null){
            callback.onReceive(true);
            binding.ticketRecyclerView.setAdapter(null);
            binding.ticketRecyclerView.setAdapter(ticketAdapter);
        }
     }

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestTicketList(boolean isShowBelowLoader) {
        if (service != null) {
            service.cancel();
        }
        if (isShowBelowLoader) {
            binding.progressBar.setVisibility(View.VISIBLE);
        }
        binding.edtSearch.setBackgroundResource(R.drawable.rounded_full_bg_search );
        if(ticketList.isEmpty()){
            binding.ticketRecyclerView.setVisibility(VISIBLE);
            binding.emptyPlaceHolderView.setVisibility(View.GONE);
            List<RatingModel> dummyList = new ArrayList<>();
            for (int i = 1; i <= 10; i++) {
                RatingModel rating = new RatingModel();
                dummyList.add(rating);
            }
            shimmerEffectAdapter.updateData(dummyList);
            binding.ticketRecyclerView.setAdapter(shimmerEffectAdapter);
        }

        service = DataService.shared(activity).requestGetRaynaCustomTicketList(getJsonObject(), new RestCallback<ContainerListModel<RaynaTicketDetailModel>>(this) {
            @Override
            public void result(ContainerListModel<RaynaTicketDetailModel> model, String error) {
                binding.progressBar.setVisibility(View.GONE);
                isLoading = false;
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    Log.d("requestNewExplore", "result: " + error);
                    return;
                }

                if (model.data != null && !model.data.isEmpty()){
                    ticketList.addAll(model.data);
                }

                if (ticketList != null && !ticketList.isEmpty() && !isShowAdd) {
                    RaynaTicketDetailModel raynaTicketDetailModel = new RaynaTicketDetailModel();
                    int insertIndex = Math.min(ticketList.size(), 5);
                    ticketList.add(insertIndex, raynaTicketDetailModel);
                    isShowAdd = true;
                }

                if (ticketList != null && !ticketList.isEmpty() && (!TextUtils.isEmpty(searchQuery)) || !filterList.isEmpty()){
                    binding.ticketRecyclerView.setAdapter(ticketAdapter);
                    ticketAdapter.updateData(ticketList);
                }

                if (!TextUtils.isEmpty(searchQuery) || !filterList.isEmpty()){
                    binding.ticketRecyclerView.setVisibility(ticketAdapter.getData().isEmpty() ? View.GONE : VISIBLE);
                    binding.emptyPlaceHolderView.setVisibility(ticketAdapter.getData().isEmpty() ? VISIBLE : View.GONE);
                }
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


    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    private class PreferencesListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.filter_tag_item));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            CategoriesModel model = (CategoriesModel) getItem(position);
            boolean isLastItem = position == getItemCount() - 1;
            if (!TextUtils.isEmpty(model.getName())){
                viewHolder.binding.iconText.setText(model.getName());
            }else {
                viewHolder.binding.iconText.setText(model.getTitle());
            }

            viewHolder.binding.linearMainView.setBackground(activity.getResources().getDrawable(R.drawable.filter_tag_bg));

            viewHolder.binding.imgRemove.setOnClickListener(v -> {
                filterList.remove(model);
                listAdapter.notifyDataSetChanged();
                page = 1;
                ticketList.clear();
                relasePlayerCallBack();
                isShowAdd = false;
                if (!filterList.isEmpty() || !searchQuery.isEmpty()){
                    requestTicketList(false);
                }else {
                    binding.emptyPlaceHolderView.setVisibility(VISIBLE);
                    binding.ticketRecyclerView.setVisibility(View.GONE);
                    ticketList.clear();
                    relasePlayerCallBack();
                    isShowAdd = false;
                    binding.ticketRecyclerView.getRecycledViewPool().clear();
                    ticketAdapter.updateData(new ArrayList<>());
                }
            });

            if (isLastItem) {
                int marginBottom = Utils.getMarginRight(holder.itemView.getContext(), 0.02f);
                Utils.setRightMargin(holder.itemView, marginBottom);
            } else {
                Utils.setRightMargin(holder.itemView, 0);
            }

        }

        public void removeData(){
            listAdapter.updateData(new ArrayList<>());
        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            private final FilterTagItemBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = FilterTagItemBinding.bind(itemView);
            }
        }
    }

    private class TicketAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        private final Handler handler = new Handler(Looper.getMainLooper());
        private final Map<ViewPager, Runnable> autoScrollMap = new HashMap<>();

        public TicketAdapter() {
            setHasStableIds(true);
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            if (viewType == 0) {
                view = UiUtils.getViewBy(parent, R.layout.item_new_explore_ticket);
                ViewGroup.LayoutParams params = view.getLayoutParams();
                params.width = (int) (Graphics.getScreenWidth(activity) * 0.93);
                view.setLayoutParams(params);
                return new ViewHolder(view);
            } else {
                view = UiUtils.getViewBy(parent, R.layout.item_ad_view_explore_ticker_view);
                return new HomeAdHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            RaynaTicketDetailModel model = (RaynaTicketDetailModel) getItem(position);
            if (!TextUtils.isEmpty(model.getId())) {
                ViewHolder viewHolder = (ViewHolder) holder;

                List<String> cleanImages = new ArrayList<>();
                if (model.getImages() != null) {
                    for (String url : model.getImages()) {
                        if (!Utils.isVideo(url)) cleanImages.add(url);
                    }
                }
                viewHolder.setupData(cleanImages, model);

                double rating = model.getAvg_ratings();
                if (rating != 0) {
                    double truncatedRating = Math.floor(rating * 10) / 10.0;
                    viewHolder.mBinding.tvRate.setText(String.format(Locale.ENGLISH, "%.1f", truncatedRating));
                    viewHolder.mBinding.ticketRatingLayout.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.mBinding.ticketRatingLayout.setVisibility(View.GONE);
                }

                viewHolder.mBinding.ticketTag.setVisibility(model.isTicketRecentlyAdded() ? View.VISIBLE : View.GONE);
                viewHolder.mBinding.txtTitle.setText(Utils.notNullString(model.getTitle()));
                viewHolder.mBinding.ticketAddress.setText(model.getCity());

                String startingAmount = model.getStartingAmount() != null ? String.valueOf(model.getStartingAmount()) : "N/A";

                if (model.getDiscount() != 0) {
                    viewHolder.mBinding.tvDiscount.setVisibility(View.VISIBLE);
                    viewHolder.mBinding.tvDiscount.setText(model.getDiscount() + "%");
                } else {
                    viewHolder.mBinding.tvDiscount.setVisibility(View.GONE);
                }

                if (!"N/A".equals(startingAmount)) {
                    Utils.setStyledText(activity, viewHolder.mBinding.tvAED, Utils.roundFloatValue(Float.parseFloat(startingAmount)));
                    viewHolder.mBinding.ticketFromAmount.setText(model.getDiscountAndStartingAmount(activity, viewHolder.mBinding.discountText));
                } else {
                    Utils.setStyledText(activity, viewHolder.mBinding.tvAED, "0");
                    viewHolder.mBinding.ticketFromAmount.setText(Utils.getStyledText(activity, "0"));
                }


                if (TextUtils.isEmpty(startingAmount) || startingAmount.equals("0") || startingAmount.equals("N/A")) {
                    viewHolder.mBinding.roundLinear.setVisibility(View.GONE);
                    viewHolder.mBinding.startingFromLayout.setVisibility(View.GONE);
                } else {
                    viewHolder.mBinding.roundLinear.setVisibility(View.VISIBLE);
                    viewHolder.mBinding.startingFromLayout.setVisibility(View.VISIBLE);
                }

                Graphics.setFavoriteIcon(activity, model.isIs_favorite(), viewHolder.mBinding.ivFavourite);

                viewHolder.mBinding.getRoot().setOnClickListener(view -> {
                    Utils.preventDoubleClick(view);
                    activity.startActivity(new Intent(activity, RaynaTicketDetailActivity.class).putExtra("ticketId", model.getId()));
                });

                viewHolder.mBinding.btnFavorite.setOnClickListener(view -> {
                    Utils.preventDoubleClick(view);
                    viewHolder.showProgress(true);
                    RaynaTicketManager.shared.requestRaynaTicketFavorite(activity, model.getId(), (success, error) -> {
                        viewHolder.showProgress(false);
                        if (success) {
                            boolean newState = !model.isIs_favorite();
                            model.setIs_favorite(newState);
//                            String title = newState ? "Thank you!!" : "Oh Snap!!";
//                            String message = model.getTitle() + (newState ? " has been added to your favorites." : " has been removed from your favorites.");
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
                            Toast.makeText(activity, error != null ? error : "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    });
                });

            } else {
                HomeAdHolder viewHolder = (HomeAdHolder) holder;
                viewHolder.setupData();
            }
        }

        @Override
        public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
            if (holder.getClass() == TicketAdapter.ViewHolder.class) {
                TicketAdapter.ViewHolder viewHolder = (TicketAdapter.ViewHolder) holder;
                ViewPager viewPager = viewHolder.mBinding.viewPager;
                Runnable runnable = autoScrollMap.remove(viewPager);
                if (runnable != null) {
                    handler.removeCallbacks(runnable);
                }
            }
            super.onViewRecycled(holder);
        }

        @Override
        public int getItemViewType(int position) {
            RaynaTicketDetailModel model = (RaynaTicketDetailModel) getItem(position);
            return TextUtils.isEmpty(model.getId()) ? 1 : 0;
        }

        @Override
        public long getItemId(int position) {
            RaynaTicketDetailModel model = (RaynaTicketDetailModel) getItem(position);
            return model.getId() != null ? model.getId().hashCode() : super.getItemId(position);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final ItemNewExploreTicketBinding mBinding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemNewExploreTicketBinding.bind(itemView);
                mBinding.tvFromTitle.setText(Utils.getLangValue("from"));
                mBinding.tvStatingTile.setText(String.format("%s ", Utils.getLangValue("starting")));
                mBinding.tvRecentlyAddedTitle.setText(Utils.getLangValue("recently_added"));
            }

            public void setupData(List<String> images, RaynaTicketDetailModel model) {
                RaynaTicketImageAdapter adapter = new RaynaTicketImageAdapter(activity, model, images);
                mBinding.viewPager.setAdapter(adapter);

                Runnable oldRunnable = autoScrollMap.get(mBinding.viewPager);
                if (oldRunnable != null) {
                    handler.removeCallbacks(oldRunnable);
                }

                Runnable autoScrollRunnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            int nextPage = mBinding.viewPager.getCurrentItem() + 1;
                            if (nextPage == adapter.getCount()) nextPage = 0;
                            mBinding.viewPager.setCurrentItem(nextPage, true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            handler.postDelayed(this, 4000);
                        }
                    }
                };

                autoScrollMap.put(mBinding.viewPager, autoScrollRunnable);
                handler.postDelayed(autoScrollRunnable, 4000);

                mBinding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override public void onPageScrolled(int position, float offset, int offsetPixels) {}
                    @Override public void onPageScrollStateChanged(int state) {}

                    @Override
                    public void onPageSelected(int position) {
                        handler.removeCallbacks(autoScrollRunnable);
                        handler.postDelayed(autoScrollRunnable, 4000);
                    }
                });
            }

            private void showProgress(boolean isShowLoader) {
                mBinding.ivFavourite.setVisibility(isShowLoader ? View.GONE : View.VISIBLE);
                mBinding.favTicketProgressBar.setVisibility(isShowLoader ? View.VISIBLE : View.GONE);
            }
        }

        public class HomeAdHolder extends RecyclerView.ViewHolder {
            private final ItemAdViewExploreTickerViewBinding mBinding;

            public HomeAdHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemAdViewExploreTickerViewBinding.bind(itemView);
            }

            public void setupData() {
                mBinding.adView.isNotSetSpace = true;
                mBinding.adView.activity = activity;
                mBinding.adView.seUpData(activity);

                callback = data -> {
                    if (data) relaseAllPlayer();
                };
            }

            public void onItemVisibilityChanged(boolean isVisible) {
                mBinding.adView.onItemVisibilityChanged(isVisible);
            }

            public void relaseAllPlayer() {
                mBinding.adView.relaseAllplayer();
            }
        }
    }

    private class TicketShimmerEffectAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            view = UiUtils.getViewBy(parent, R.layout.ticket_explore_shimer_placeholder);
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.width = (int) (Graphics.getScreenWidth(activity) * 0.93);
            view.setLayoutParams(params);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            RatingModel model = (RatingModel) getItem( position );
            viewHolder.binding.shimmerLayout.startShimmer();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final TicketExploreShimerPlaceholderBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = TicketExploreShimerPlaceholderBinding.bind(itemView);
            }
        }

    }

    private static class SearchPopupAdapter extends BaseAdapter implements Filterable {
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