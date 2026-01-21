package com.whosin.app.ui.activites.explore;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.comman.ui.VerticalSpaceItemDecoration;
import com.whosin.app.databinding.ExploreFilterBottomSheetBinding;
import com.whosin.app.databinding.ItemExploreFilterBinding;
import com.whosin.app.databinding.SelectDaysItemBinding;
import com.whosin.app.service.manager.AppSettingManager;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.AppSettingTitelCommonModel;
import com.whosin.app.service.models.CategoriesModel;
import com.whosin.app.service.models.CommanMsgModel;
import com.whosin.app.service.models.newExploreModels.ExploreObjectModel;

import java.util.ArrayList;
import java.util.List;

public class NewExploreFilterBottomSheet extends DialogFragment {

    private ExploreFilterBottomSheetBinding binding;

    private final PreferencesListAdapter<CommanMsgModel> listAdapter = new PreferencesListAdapter<>();

    public List<CategoriesModel> filterList = new ArrayList<>();

    private List<CategoriesModel> citiesList = new ArrayList<>();

    private List<CategoriesModel> categoriesModels = new ArrayList<>();

    public CommanCallback<List<CategoriesModel>> callback;


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutRes(), container, false);
        initUi(view);
        setListener();
        return view;
    }

    public void initUi(View view) {
        binding = ExploreFilterBottomSheetBinding.bind(view);


        binding.tvBucketTitle.setText(Utils.getLangValue("filter"));
        binding.filterBtn.setText(Utils.getLangValue("save"));

        binding.filterDataRecycler.setLayoutManager( new LinearLayoutManager( getActivity(), LinearLayoutManager.VERTICAL, false ) );
        binding.filterDataRecycler.setNestedScrollingEnabled(false);
        binding.filterDataRecycler.setAdapter( listAdapter );

        List<CommanMsgModel> data = new ArrayList<>();
        if (SessionManager.shared.geExploreBlockData() != null){
            ExploreObjectModel exploreObjectModel = SessionManager.shared.geExploreBlockData();

            if (!exploreObjectModel.getCities().isEmpty()) {
                data.add(new CommanMsgModel("Cities"));
                citiesList.addAll(exploreObjectModel.getCities());
            }
            if (!exploreObjectModel.getCategories().isEmpty()) {
                data.add(new CommanMsgModel("Categories"));
                categoriesModels.addAll(exploreObjectModel.getCategories());
            }

            listAdapter.updateData(data);
        }

    }

    public void setListener() {
        Glide.with(requireActivity()).load(R.drawable.icon_close_btn).into(binding.ivClose);
        binding.ivClose.setOnClickListener(view -> dismiss());

        binding.filterBtn.setOnClickListener(v -> {
            if (!filterList.isEmpty()) {
                if (callback != null) {
                    callback.onReceive(filterList);
                }
                dismiss();
            }
            else {
                Toast.makeText(requireActivity(), "select filter", Toast.LENGTH_SHORT).show();
            }

        });

    }

    public int getLayoutRes() {
        return R.layout.explore_filter_bottom_sheet;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = new BottomSheetDialog(requireActivity(), R.style.BottomSheetDialogThemeNoFloating);
        dialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
            FrameLayout bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            ViewGroup.LayoutParams layoutParam = bottomSheet.getLayoutParams();
            layoutParam.height = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParam.width = ViewGroup.LayoutParams.MATCH_PARENT;
            bottomSheet.setLayoutParams(layoutParam);
            if (bottomSheet != null) {
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
        return dialog;
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------



    // endregion
    // --------------------------------------
    // region public
    // --------------------------------------



    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    // --------------------------------------
    // region Adapter

    public class PreferencesListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        private static final int VIEW_TYPE_CITITES = 0;

        private static final int VIEW_TYPE_CATEGORIES = 1;


        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());

            switch (viewType) {
                case VIEW_TYPE_CITITES:
                    return new CitiesHolder(inflater.inflate(R.layout.item_explore_filter, parent, false));
                case VIEW_TYPE_CATEGORIES:
                    return new CategorisHolder(inflater.inflate(R.layout.item_explore_filter, parent, false));
                default:
                    throw new IllegalArgumentException("Invalid view type: " + viewType);
            }

        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            boolean isLastItem = position == getItemCount() - 1;

            switch (getItemViewType(position)) {
                case VIEW_TYPE_CITITES:
                    ((CitiesHolder) holder).setupData( );
                    break;
                case VIEW_TYPE_CATEGORIES:
                    ((CategorisHolder) holder).setupData();
                    break;
                default:
                    break;
            }

            if (isLastItem) {
                int marginBottom = Utils.getMarginBottom(holder.itemView.getContext(), 0.18f);
                Utils.setBottomMargin(holder.itemView, marginBottom);
            } else {
                Utils.setBottomMargin(holder.itemView, 0);
            }

        }


        public int getItemViewType(int position) {
            CommanMsgModel model = (CommanMsgModel) getItem(position);
            switch (model.message) {
                case "Cities":
                    return VIEW_TYPE_CITITES;
                case "Categories":
                    return VIEW_TYPE_CATEGORIES;
            }
            return super.getItemViewType(position);
        }

        public class CitiesHolder extends RecyclerView.ViewHolder {

            private final ItemExploreFilterBinding mBinding;

            private final ItemListAdapter<CategoriesModel> adapterCuisine = new ItemListAdapter<>();

            public CitiesHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemExploreFilterBinding.bind(itemView);
                setupRecyclerView(adapterCuisine, mBinding,true);
            }

            @SuppressLint("SetTextI18n")
            public void setupData() {
                mBinding.title.setText(Utils.getLangValue("cities"));
                adapterCuisine.updateData(citiesList);
            }

        }

        public class CategorisHolder extends RecyclerView.ViewHolder {
            private final ItemExploreFilterBinding mBinding;

            private final ItemListAdapter<CategoriesModel> adapterMusic = new ItemListAdapter<>();

            public CategorisHolder(@NonNull View itemView) {
                super(itemView);

                mBinding = ItemExploreFilterBinding.bind(itemView);
                setupRecyclerView(adapterMusic, mBinding,false);
            }

            @SuppressLint("SetTextI18n")
            public void setupData() {
                mBinding.title.setText(Utils.getLangValue("categories"));
                adapterMusic.updateData(categoriesModels);
            }
        }



        private void setupRecyclerView(RecyclerView.Adapter adapter, ItemExploreFilterBinding mBinding,boolean isFromCitiy) {

            int itemCount = isFromCitiy ? citiesList.size() : categoriesModels.size();
            int spanCount = itemCount > 10 ? 3 : (itemCount > 5 ? 2 : 1);

            StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.HORIZONTAL);
            mBinding.itemRecycleView.setLayoutManager(layoutManager);
            int spacing = getResources().getDimensionPixelSize(com.intuit.ssp.R.dimen._10ssp);
            mBinding.itemRecycleView.addItemDecoration(new VerticalSpaceItemDecoration(spacing));
            mBinding.itemRecycleView.setAdapter(adapter);
            mBinding.itemRecycleView.setHasFixedSize(false);
        }
    }


    public class ItemListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder( UiUtils.getViewBy( parent, R.layout.swipe_tag_item ) );
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            ViewHolder viewHolder = (ViewHolder) holder;
            CategoriesModel model = (CategoriesModel) getItem( position );

            if (!TextUtils.isEmpty(model.getName())){
                viewHolder.binding.iconText.setText(model.getName());
            }else {
                viewHolder.binding.iconText.setText(model.getTitle());
            }

//            viewHolder.binding.iconText.setText( isFromCiti ? model.getName() : model.getTitle() );

            viewHolder.itemView.setOnClickListener( view -> {
                boolean idFound = filterList.stream().anyMatch(ids -> ids.getId().equals(model.getId()));
                if (idFound) {
                    viewHolder.binding.linearMainView.setBackground( requireActivity().getResources().getDrawable( R.drawable.days_background ) );
                    filterList.remove(model);
                } else {
                    viewHolder.binding.linearMainView.setBackground( requireActivity().getResources().getDrawable( R.drawable.selected_bg ) );
                    filterList.add(model);
                }
            } );

            boolean idFound = filterList.stream().anyMatch(ids -> ids.getId().equals(model.getId()));
            if (!idFound) {
                viewHolder.binding.linearMainView.setBackground(requireActivity().getResources().getDrawable(R.drawable.days_background));
            } else {
                viewHolder.binding.linearMainView.setBackground(requireActivity().getResources().getDrawable(R.drawable.selected_bg));
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
