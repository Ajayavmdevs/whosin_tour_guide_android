package com.whosin.app.ui.activites.search;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.adevinta.leku.LocationPickerActivity;
import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.comman.ui.VerticalSpaceItemDecoration;
import com.whosin.app.databinding.FragmentSerachFilterBottomSheetBinding;
import com.whosin.app.databinding.ItemExploreFilterBinding;
import com.whosin.app.databinding.SelectDaysItemBinding;
import com.whosin.app.service.manager.AppSettingManager;
import com.whosin.app.service.models.AppSettingTitelCommonModel;
import com.whosin.app.service.models.CommanMsgModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class SerachFilterBottomSheet extends DialogFragment {

    private FragmentSerachFilterBottomSheetBinding binding;

    private final PreferencesListAdapter<CommanMsgModel> listAdapter = new PreferencesListAdapter<>();

    public List<AppSettingTitelCommonModel> filterList = new ArrayList<>();

    public CommanCallback<List<AppSettingTitelCommonModel>> callback;

    public CommanCallback<AppSettingTitelCommonModel> locationCallback;

    private FusedLocationProviderClient fusedLocationClient;

    private static final int LOCATION_PICKER_REQUEST_CODE = 1;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    public double latitude = 0.0, longitude = 0.0;

    public int minValue = 0;

    public int maxValue = 2000;

    public int radius = 0;

    public String locationName = "";

    public boolean isClickLocaion = false;


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

    @SuppressLint("DefaultLocale")
    public void initUi(View view) {
        binding = FragmentSerachFilterBottomSheetBinding.bind(view);


        binding.rangeSeekBar.setTrackActiveTintList(ColorStateList.valueOf(getResources().getColor(R.color.brand_pink)));
        binding.rangeSeekBar.setTrackInactiveTintList(ColorStateList.valueOf(getResources().getColor(R.color.grey)));
        binding.rangeSeekBar.setValues(0.0F, 2000.0F);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        checkLocationPermissions();

        binding.filterDataRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        binding.filterDataRecycler.setNestedScrollingEnabled(false);
        binding.filterDataRecycler.setAdapter(listAdapter);

        binding.tvPriceRange.setText(String.format("Price Range : %d - %d AED", 0, 2000));


        List<CommanMsgModel> data = new ArrayList<>();
        if (!AppSettingManager.shared.venueFilterModel.getCategories().isEmpty()) {
            data.add(new CommanMsgModel("Categories"));
        }
        if (!AppSettingManager.shared.venueFilterModel.getThemes().isEmpty()) {
            data.add(new CommanMsgModel("Themes"));
        }
        if (!AppSettingManager.shared.venueFilterModel.getCuisine().isEmpty()) {
            data.add(new CommanMsgModel("Cuisine"));
        }
        if (!AppSettingManager.shared.venueFilterModel.getMusic().isEmpty()) {
            data.add(new CommanMsgModel("Music"));
        }
        if (!AppSettingManager.shared.venueFilterModel.getFeature().isEmpty()) {
            data.add(new CommanMsgModel("Feature"));
        }
        listAdapter.updateData(data);


        Optional<AppSettingTitelCommonModel> radiusModel = filterList.stream().filter(p -> p.getId().equals("1")).findFirst();
        if (radiusModel.isPresent()) {
            binding.seekBar.setProgress(radiusModel.get().getMaxDistance());
            radius = radiusModel.get().getMaxDistance();
            binding.startRadiusTextView.setText(String.format("%d Km", radius));
        }

        Optional<AppSettingTitelCommonModel> model = filterList.stream().filter(p -> p.getId().equals("2")).findFirst();
        if (model.isPresent()) {
            binding.rangeSeekBar.setValues((float) model.get().getStartingPrice(), (float) model.get().getEndingPrice());
            binding.tvPriceRange.setText(String.format("Price Range : %d - %d AED", model.get().getStartingPrice(), model.get().getEndingPrice()));
            minValue = model.get().getStartingPrice();
            maxValue = model.get().getEndingPrice();
        }

        if (!TextUtils.isEmpty(locationName)) {
            binding.slectedLocation.setText(locationName);
        }


    }

    @SuppressLint("DefaultLocale")
    public void setListener() {
        Glide.with(requireActivity()).load(R.drawable.icon_close_btn).into(binding.ivClose);

        binding.ivClose.setOnClickListener(view -> dismiss());

        binding.filterBtn.setOnClickListener(v -> {
            if (callback != null) {
                if (!filterList.isEmpty()) callback.onReceive(filterList);
                if (isClickLocaion){
                    if (latitude != 0.0 && longitude != 0.0) {
                        if (locationCallback != null) {
                            AppSettingTitelCommonModel locationModel = new AppSettingTitelCommonModel();
                            locationModel.setLatitude(latitude);
                            locationModel.setLongitude(longitude);
                            locationModel.setTitle(binding.slectedLocation.getText().toString());
                            locationCallback.onReceive(locationModel);
                        }
                    }
                }
                dismiss();
            }
        });

        binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                radius = seekBar.getProgress();
                binding.startRadiusTextView.setText(String.format("%d Km", seekBar.getProgress()));

                filterList.removeIf(p -> p.getId().equals("1"));
                AppSettingTitelCommonModel radiusModel = new AppSettingTitelCommonModel();
                radiusModel.setId("1");
                radiusModel.setMaxDistance(radius);
                radiusModel.setTitle("Radius : " + radius);
                filterList.add(radiusModel);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        binding.idConstraight.setOnClickListener(v -> {
            Utils.preventDoubleClick(v);
            openLocationPicker();
        });

        binding.rangeSeekBar.addOnChangeListener((slider, value, fromUser) -> {
            List<Float> values = slider.getValues();
            minValue = Math.round(values.get(0));
            maxValue = Math.round(values.get(1));

            binding.tvPriceRange.setText(String.format("Price Range : %d - %d AED", minValue, maxValue));
            filterList.removeIf(p -> p.getId().equals("2"));

            AppSettingTitelCommonModel priceModel = new AppSettingTitelCommonModel();
            priceModel.setId("2");
            priceModel.setStartingPrice(minValue);
            priceModel.setEndingPrice(maxValue);
            priceModel.setTitle("Price Range : " + minValue + "-" + maxValue + " AED");

            filterList.add( priceModel);
        });


    }

    public int getLayoutRes() {
        return R.layout.fragment_serach_filter_bottom_sheet;
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOCATION_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                isClickLocaion = true;
                latitude = data.getDoubleExtra("latitude", 0.0);
                longitude = data.getDoubleExtra("longitude", 0.0);
                String address = data.getStringExtra("location_address");
                binding.slectedLocation.setText(address);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();

            } else {
                Toast.makeText(requireActivity(), "Location permission required", Toast.LENGTH_SHORT).show();
            }
        }
    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------


    private void checkLocationPermissions() {
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            if (latitude == 0.0 && longitude ==  0.0){
                getLastLocation();
            }
        }
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(requireActivity(), location -> {
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        binding.slectedLocation.setText("Current Location");
                    }
                });
    }

    private void openLocationPicker() {
        Intent intent = new LocationPickerActivity.Builder(requireActivity())
                .withLocation(latitude, longitude)
                .withGeolocApiKey(getString(R.string.geoloc_api_key))
                .withGooglePlacesApiKey(getString(R.string.places_api_key))
                .withGoogleTimeZoneEnabled()
                .build();


        startActivityForResult(intent, LOCATION_PICKER_REQUEST_CODE);
    }


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
    // --------------------------------------

    public class PreferencesListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {
        private static final int VIEW_TYPE_CATEGORIES = 0;
        private static final int VIEW_TYPE_THEMES = 1;
        private static final int VIEW_TYPE_CUISINE = 2;
        private static final int VIEW_TYPE_MUSIC = 3;
        private static final int VIEW_TYPE_FEATURE = 4;


        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());

            switch (viewType) {
                case VIEW_TYPE_CATEGORIES:
                    return new CategoriesHolder(inflater.inflate(R.layout.item_explore_filter, parent, false));
                case VIEW_TYPE_THEMES:
                    return new ThemeHolder(inflater.inflate(R.layout.item_explore_filter, parent, false));
                case VIEW_TYPE_CUISINE:
                    return new  CuisineHolder(inflater.inflate(R.layout.item_explore_filter, parent, false));
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
                case VIEW_TYPE_CATEGORIES:
                    ((CategoriesHolder) holder).setupData(AppSettingManager.shared.venueFilterModel.getCategories());
                    break;
                case VIEW_TYPE_THEMES:
                    ((ThemeHolder) holder).setupData(AppSettingManager.shared.venueFilterModel.getThemes());
                    break;
                case VIEW_TYPE_CUISINE:
                    ((CuisineHolder) holder).setupData(AppSettingManager.shared.venueFilterModel.getCuisine());
                    break;
                case VIEW_TYPE_MUSIC:
                    ((MusicHolder) holder).setupData(AppSettingManager.shared.venueFilterModel.getMusic());
                    break;
                default:
                    ((FeatureHolder) holder).setupData(AppSettingManager.shared.venueFilterModel.getFeature());
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
                case "Categories":
                    return VIEW_TYPE_CATEGORIES;
                case "Themes":
                    return VIEW_TYPE_THEMES;
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

        public class ThemeHolder extends RecyclerView.ViewHolder {
            private final ItemExploreFilterBinding mBinding;

            private final ItemListAdapter<AppSettingTitelCommonModel> adapterFeature = new ItemListAdapter<>();

            public ThemeHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemExploreFilterBinding.bind(itemView);
                setupRecyclerView(adapterFeature, mBinding);
            }

            public void setupData(List<AppSettingTitelCommonModel> featureList) {
                mBinding.title.setText("Themes");
                adapterFeature.updateData(featureList);
            }
        }

        public class CategoriesHolder extends RecyclerView.ViewHolder {
            private final ItemExploreFilterBinding mBinding;

            private final ItemListAdapter<AppSettingTitelCommonModel> adapterFeature = new ItemListAdapter<>();

            public CategoriesHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemExploreFilterBinding.bind(itemView);
                setupRecyclerView(adapterFeature, mBinding);
            }

            public void setupData(List<AppSettingTitelCommonModel> featureList) {
                mBinding.title.setText("Categories");
                adapterFeature.updateData(featureList);
            }
        }


        private void setupRecyclerView(RecyclerView.Adapter adapter, ItemExploreFilterBinding mBinding) {

            StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.HORIZONTAL);
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