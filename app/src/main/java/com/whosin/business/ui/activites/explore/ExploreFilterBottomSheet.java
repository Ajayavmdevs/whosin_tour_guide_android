package com.whosin.business.ui.activites.explore;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.whosin.business.R;
import com.whosin.business.comman.DiffAdapter;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Utils;
import com.whosin.business.comman.interfaces.CommanCallback;
import com.whosin.business.comman.ui.UiUtils;
import com.whosin.business.comman.ui.VerticalSpaceItemDecoration;
import com.whosin.business.databinding.ExploreFilterBottomSheetBinding;
import com.whosin.business.databinding.ItemExploreFilterBinding;
import com.whosin.business.databinding.SelectDaysItemBinding;
import com.whosin.business.service.manager.AppSettingManager;
import com.whosin.business.service.models.AppSettingTitelCommonModel;
import com.whosin.business.service.models.CommanMsgModel;

import java.util.ArrayList;
import java.util.List;

public class ExploreFilterBottomSheet extends DialogFragment {

    private ExploreFilterBottomSheetBinding binding;

    private final PreferencesListAdapter<CommanMsgModel> listAdapter = new PreferencesListAdapter<>();

    public List<AppSettingTitelCommonModel> filterList = new ArrayList<>();

    public CommanCallback<List<AppSettingTitelCommonModel>> callback;

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

        binding.filterDataRecycler.setLayoutManager( new LinearLayoutManager( getActivity(), LinearLayoutManager.VERTICAL, false ) );
        binding.filterDataRecycler.setNestedScrollingEnabled(false);
        binding.filterDataRecycler.setAdapter( listAdapter );
        List<CommanMsgModel> data = new ArrayList<>();
        if (!AppSettingManager.shared.getAppSettingData().getCuisine().isEmpty()) {
            data.add(new CommanMsgModel("Cuisine"));
        }
        if (!AppSettingManager.shared.getAppSettingData().getMusic().isEmpty()) {
            data.add(new CommanMsgModel("Music"));
        }
        if (!AppSettingManager.shared.getAppSettingData().getFeature().isEmpty()) {
            data.add(new CommanMsgModel("Feature"));
        }
        listAdapter.updateData(data);
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


    public void setShareListener(CommanCallback<List<AppSettingTitelCommonModel>> listener) {
         this.callback = listener;
    }

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    // --------------------------------------
    // region Adapter

    public class PreferencesListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {
        private static final int VIEW_TYPE_CUISINE = 0;
        private static final int VIEW_TYPE_MUSIC = 1;
        private static final int VIEW_TYPE_FEATURE = 2;

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());

            switch (viewType) {
                case VIEW_TYPE_CUISINE:
                    return new CuisineHolder(inflater.inflate(R.layout.item_explore_filter, parent, false));
                case VIEW_TYPE_MUSIC:
                    return new MusicHolder(inflater.inflate(R.layout.item_explore_filter, parent, false));
                case VIEW_TYPE_FEATURE:
                    return new FeatureHolder(inflater.inflate(R.layout.item_explore_filter, parent, false));
                default:
                    throw new IllegalArgumentException("Invalid view type: " + viewType);
            }

        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            boolean isLastItem = position == getItemCount() - 1;

            switch (getItemViewType(position)) {
                case VIEW_TYPE_CUISINE:
                    ((CuisineHolder) holder).setupData(AppSettingManager.shared.getAppSettingData().getCuisine());
                    break;
                case VIEW_TYPE_MUSIC:
                    ((MusicHolder) holder).setupData(AppSettingManager.shared.getAppSettingData().getMusic());
                    break;
                default:
                    ((FeatureHolder) holder).setupData(AppSettingManager.shared.getAppSettingData().getFeature());
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
                case "Cuisine":
                    return VIEW_TYPE_CUISINE;
                case "Music":
                    return VIEW_TYPE_MUSIC;
                case "Feature":
                    return VIEW_TYPE_FEATURE;
            }
            return super.getItemViewType(position);
        }

        public class CuisineHolder extends RecyclerView.ViewHolder {
            private final ItemExploreFilterBinding mBinding;
            private final ItemListAdapter<AppSettingTitelCommonModel> adapterCuisine = new ItemListAdapter<>();

            public CuisineHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemExploreFilterBinding.bind(itemView);
                setupRecyclerView(adapterCuisine, mBinding);
            }

            @SuppressLint("SetTextI18n")
            public void setupData(List<AppSettingTitelCommonModel> cuisineList) {
                mBinding.title.setText("Cuisine");
                adapterCuisine.updateData(cuisineList);
            }

        }

        public class MusicHolder extends RecyclerView.ViewHolder {
            private final ItemExploreFilterBinding mBinding;
            private final ItemListAdapter<AppSettingTitelCommonModel> adapterMusic = new ItemListAdapter<>();

            public MusicHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemExploreFilterBinding.bind(itemView);
                setupRecyclerView(adapterMusic, mBinding);
            }

            @SuppressLint("SetTextI18n")
            public void setupData(List<AppSettingTitelCommonModel> musicList) {
                mBinding.title.setText("Music");
                adapterMusic.updateData(musicList);
            }
        }

        public class FeatureHolder extends RecyclerView.ViewHolder {
            private final ItemExploreFilterBinding mBinding;

            private final ItemListAdapter<AppSettingTitelCommonModel> adapterFeature = new ItemListAdapter<>();

            public FeatureHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemExploreFilterBinding.bind(itemView);
                setupRecyclerView(adapterFeature, mBinding);
            }

            public void setupData(List<AppSettingTitelCommonModel> featureList) {
                mBinding.title.setText("Feature");
                adapterFeature.updateData(featureList);
            }
        }

        private void setupRecyclerView(RecyclerView.Adapter adapter, ItemExploreFilterBinding mBinding) {

            StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.HORIZONTAL);
            mBinding.itemRecycleView.setLayoutManager(layoutManager);
//            mBinding.itemRecycleView.setHorizontalScrollBarEnabled(true);
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
            AppSettingTitelCommonModel model = (AppSettingTitelCommonModel) getItem( position );
            viewHolder.binding.iconText.setText( model.getTitle() );

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