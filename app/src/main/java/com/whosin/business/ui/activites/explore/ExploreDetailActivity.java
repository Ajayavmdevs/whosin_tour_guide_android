package com.whosin.business.ui.activites.explore;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.whosin.business.R;
import com.whosin.business.comman.DiffAdapter;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Utils;
import com.whosin.business.comman.ui.UiUtils;
import com.whosin.business.databinding.ActivityExploreDetailBinding;
import com.whosin.business.databinding.SelectDaysItemBinding;
import com.whosin.business.service.DataService;
import com.whosin.business.service.manager.SessionManager;
import com.whosin.business.service.models.CategoriesModel;
import com.whosin.business.service.models.ContainerListModel;
import com.whosin.business.service.models.rayna.RaynaTicketDetailModel;
import com.whosin.business.service.rest.RestCallback;
import com.whosin.business.ui.activites.comman.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class ExploreDetailActivity extends BaseActivity {

    private ActivityExploreDetailBinding binding;
    private final ItemListAdapter<CategoriesModel> adapterTag = new ItemListAdapter<>();
    private CategoriesModel categoriesModel;
    private List<CategoriesModel> filterList = new ArrayList<>();
    private List<RaynaTicketDetailModel> ticketList = new ArrayList<>();
    private boolean isCity;
    private int currentPage = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private static final int PAGE_SIZE = 10;
    private boolean isFromHomeBlock = false;

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    protected void initUi() {
        String model = Utils.notNullString( getIntent().getStringExtra( "categoryModel" ) );
        String title = Utils.notNullString( getIntent().getStringExtra( "title" ) );
        isFromHomeBlock = getIntent().getBooleanExtra( "isFromHomeBlock",false);
        isCity = getIntent().getBooleanExtra("isCity", false);
        categoriesModel = new Gson().fromJson(model, CategoriesModel.class);
        List<CategoriesModel> TagList = new ArrayList<>();

        if (SessionManager.shared.geExploreBlockData() != null) {
            List<CategoriesModel> allCities;
            if(isCity) {
                allCities = SessionManager.shared.geExploreBlockData().getCategories();
            }
            else {
                allCities = SessionManager.shared.geExploreBlockData().getCities();
            }
            if (!allCities.isEmpty()) {
                TagList.addAll(allCities);
            }
        }

        if (categoriesModel != null) {
            binding.titleTv.setText(isCity ? categoriesModel.getName() : categoriesModel.getTitle());
            if (TextUtils.isEmpty(binding.titleTv.getText().toString())) binding.titleTv.setText(title);
        } else if (!TextUtils.isEmpty(title)) {
            binding.titleTv.setText(title);
        }


        binding.itemRecycleView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        binding.itemRecycleView.setAdapter(adapterTag);
        if (!isFromHomeBlock){
            adapterTag.updateData(TagList);
        }else {
            binding.itemRecycleView.setVisibility(View.GONE);
        }
        binding.ticketRecyclerView.isVertical = true;
        binding.ticketRecyclerView.activity = activity;
        requestTicketList();
    }

    @Override
    protected void setListeners() {
        binding.ivClose.setOnClickListener(view -> {
            onBackPressed();
        });

//        binding.ticketRecyclerView.post(() -> {
//            RecyclerView recyclerView = binding.ticketRecyclerView.getRecyclerView();
//            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//                @Override
//                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                    super.onScrolled(recyclerView, dx, dy);
//                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
//                    int lastVisible = layoutManager.findLastVisibleItemPosition();
//                    int totalItems = binding.ticketRecyclerView.getAdapterCount();
//
//                    if (!isLoading && lastVisible >= totalItems - 1 && totalItems >= PAGE_SIZE) {
//                        if (totalItems % PAGE_SIZE == 0) {
//                            isLoading = true;
//                            currentPage++;
//                            requestTicketList();
//                        }
//                    }
//                }
//            });
//        });

        binding.ticketRecyclerView.post(() -> {
            RecyclerView recyclerView = binding.ticketRecyclerView.getRecyclerView();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    int lastVisible = layoutManager.findLastVisibleItemPosition();
                    int totalItems = binding.ticketRecyclerView.getAdapterCount();

                    // Trigger API when user scrolls to last item
                    if (!isLoading && !isLastPage && lastVisible >= totalItems - 1) {
                        currentPage++;
                        requestTicketList();
                    }
                }
            });
        });

    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityExploreDetailBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private JsonObject getJsonObject() {
        JsonObject object = new JsonObject();
        object.addProperty("page", currentPage);
        object.addProperty("limit", PAGE_SIZE);
        object.addProperty("search", "");

        if (!isFromHomeBlock){
            JsonArray filterIds = new JsonArray();
            filterList.stream()
                    .map(CategoriesModel::getId)
                    .filter(id -> id != null && !id.isEmpty())
                    .forEach(filterIds::add);

            JsonArray categoryIdsArray = new JsonArray();
            if (categoriesModel != null && categoriesModel.getId() != null) {
                categoryIdsArray.add(categoriesModel.getId());
            }

            if (isCity) {
                object.add("cityIds", categoryIdsArray);
                object.add("categoryIds", filterIds);
            } else {
                object.add("cityIds", filterIds);
                object.add("categoryIds", categoryIdsArray);
            }
        }

        if (isFromHomeBlock && categoriesModel != null){
            JsonArray categoryIdsArray = new JsonArray();
            if (!TextUtils.isEmpty(categoriesModel.getId())){
                categoryIdsArray.add(categoriesModel.getId());
            }
            object.add("categoryIds", categoryIdsArray);
        }

        return object;
    }



    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestTicketList() {
        if (isLoading || isLastPage) return;
        isLoading = true;
        if (currentPage == 1){
            showProgress();
        }else {
            binding.progressBar.setVisibility(View.VISIBLE);
        }

        Log.d("TAG", "requestTicketList: "+getJsonObject());
        DataService.shared(activity).requestGetRaynaCustomTicketList(getJsonObject(), new RestCallback<ContainerListModel<RaynaTicketDetailModel>>(this) {
            @Override
            public void result(ContainerListModel<RaynaTicketDetailModel> model, String error) {
                hideProgress();
                binding.progressBar.setVisibility(View.GONE);
                isLoading = false;

                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (model.data != null && !model.data.isEmpty()) {
                    ticketList.addAll(model.data);
                    if (currentPage != 1) {
                        binding.ticketRecyclerView.setTicketPagginationData(model.data,false);
                    }
                } else {
                    isLastPage = true;
                    binding.ticketRecyclerView.setTicketPagginationData(new ArrayList<>(),true);
                }

                if (!ticketList.isEmpty()) {
                    if (currentPage == 1) {
                        binding.ticketRecyclerView.setupData(ticketList, activity, true, false);
                    }
                    binding.ticketRecyclerView.setVisibility(View.VISIBLE);
                    binding.emptyPlaceHolderView.setVisibility(View.GONE);
                } else {
                    binding.ticketRecyclerView.setVisibility(View.GONE);
                    binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
                }

            }
        });
    }


    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------


    private class ItemListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ItemListAdapter.ViewHolder( UiUtils.getViewBy( parent, R.layout.swipe_tag_item ) );
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            CategoriesModel model = (CategoriesModel) getItem( position );
            if(isCity) {
                viewHolder.binding.iconText.setText(model.getTitle());
            }
            else {
                viewHolder.binding.iconText.setText(model.getName());
            }

            viewHolder.itemView.setOnClickListener( view -> {
                int realPosition = viewHolder.getBindingAdapterPosition();
                if (realPosition == RecyclerView.NO_POSITION) return;
                boolean idFound = filterList.stream().anyMatch(ids -> ids.getId().equals(model.getId()));
                if (idFound) {
                    viewHolder.binding.linearMainView.setBackground(ContextCompat.getDrawable(view.getContext(), R.drawable.days_background));
                    filterList.remove(model);
                } else {
                    viewHolder.binding.linearMainView.setBackground(ContextCompat.getDrawable(view.getContext(), R.drawable.selected_bg));
                    filterList.add(model);
                }
//                currentPage = 1;
//                isLastPage = false;
//                ticketList.clear();
//                requestTicketList();
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    currentPage = 1;
                    isLastPage = false;
                    ticketList.clear();
                    requestTicketList();
                }, 100);

            } );

            boolean idFound = filterList.stream().anyMatch(ids -> ids.getId().equals(model.getId()));
            if (!idFound) {
                viewHolder.binding.linearMainView.setBackground(activity.getResources().getDrawable(R.drawable.days_background));
            } else {
                viewHolder.binding.linearMainView.setBackground(activity.getResources().getDrawable(R.drawable.selected_bg));
            }
        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            private final SelectDaysItemBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super( itemView );
                binding = SelectDaysItemBinding.bind( itemView );
            }
        }
    }


    // endregion
    // --------------------------------------

}