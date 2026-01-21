package com.whosin.app.ui.fragment.PromoterCreateEvent;

import static com.whosin.app.comman.AppDelegate.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.GridSpacingItemDecoration;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.FragmentSelectVenueBottomSheetBinding;
import com.whosin.app.databinding.ItemPromoterSelectVenueDesignBinding;
import com.whosin.app.databinding.PagerItemBinding;
import com.whosin.app.service.models.VenueObjectModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


public class SelectVenueBottomSheet extends DialogFragment {

    private FragmentSelectVenueBottomSheetBinding binding;

    public List<VenueObjectModel> venueList;
    private List<VenueObjectModel> filteredList = new ArrayList<>();
    private VenueAdapter<VenueObjectModel> venueAdapter = new VenueAdapter<>();

    public CommanCallback<VenueObjectModel> callback;

    public String venueId = "";

    private Handler handler = new Handler();
    private String searchQuery = "";
    private Runnable runnable = () -> updateList();

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

    private void initUi(View v) {
        binding = FragmentSelectVenueBottomSheetBinding.bind(v);

        binding.tvBucketTitle.setText(Utils.getLangValue("my_venues"));
        binding.editTvSearch.setHint(Utils.getLangValue("find_venues"));
        binding.tvDoneTitle.setText(Utils.getLangValue("done"));
        binding.emptyPlaceHolderView.setEmptyPlaceTxtTitle(Utils.getLangValue("there_is_no_venue_available"));

        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 2);
        binding.venueRecycler.setLayoutManager(gridLayoutManager);
        binding.venueRecycler.setAdapter(venueAdapter);

        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.promoter_notification_top);
        binding.venueRecycler.addItemDecoration(new GridSpacingItemDecoration(requireContext(), 2, spacingInPixels, false));

        if (venueList != null) {
            if (!venueList.isEmpty()) {
                venueAdapter.updateData(venueList);
                binding.emptyPlaceHolderView.setVisibility(View.GONE);
            } else {
                binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
            }
        } else {
            binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
        }
    }

    private void setListener() {
        binding.ivClose.setOnClickListener(view -> dismiss());

        binding.doneBtn.setOnClickListener(v -> {
            Utils.preventDoubleClick(v);
            if (!TextUtils.isEmpty(venueId)) {
                if (venueList == null ) return;
                Optional<VenueObjectModel> venueModel = venueList.stream().filter(p -> p.getId().equals(venueId)).findFirst();
                if (venueModel.isPresent()){
                    callback.onReceive(venueModel.get());
                    dismiss();
                }

            } else {
                Toast.makeText(getContext(), "Please select a venue first", Toast.LENGTH_SHORT).show();
            }
        });

        binding.editTvSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                searchQuery = editable.toString();
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, 1000);
            }
        });

    }

    private void updateList() {
        if (venueList == null || venueList.isEmpty()) {
            showEmptyState();
            return;
        }

        filteredList.clear();
        if (searchQuery == null || searchQuery.isEmpty()) {
            filteredList.addAll(venueList);
        } else {
            for (VenueObjectModel venue : venueList) {
                if (venue != null && venue.getName() != null &&
                        venue.getName().toLowerCase().contains(searchQuery.toLowerCase())) {
                    filteredList.add(venue);
                }
            }
        }

        venueAdapter.updateData(filteredList);
        toggleEmptyState(!filteredList.isEmpty());
    }

    private void showEmptyState() {
        binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
        binding.venueRecycler.setVisibility(View.GONE);
    }

    private void toggleEmptyState(boolean hasItems) {
        binding.emptyPlaceHolderView.setVisibility(hasItems ? View.GONE : View.VISIBLE);
        binding.venueRecycler.setVisibility(hasItems ? View.VISIBLE : View.GONE);
    }



    private int getLayoutRes() {
        return R.layout.fragment_select_venue_bottom_sheet;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = new BottomSheetDialog(requireActivity(), R.style.BottomSheetDialogThemeNoFloating);
        dialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
            FrameLayout bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            assert bottomSheet != null;
            ViewGroup.LayoutParams layoutParam = bottomSheet.getLayoutParams();
            layoutParam.height = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParam.width = ViewGroup.LayoutParams.MATCH_PARENT;
            bottomSheet.setLayoutParams(layoutParam);
            BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
        });
        return dialog;
    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------


    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    private class VenueAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_promoter_select_venue_design));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            VenueObjectModel model = (VenueObjectModel) getItem(position);

            viewHolder.mBinding.ivSelectVenue.setOnCheckedChangeListener(null);
            if (!Utils.isNullOrEmpty(venueId)) {
                viewHolder.mBinding.ivSelectVenue.setChecked(model.getId().equals(venueId));
            }else {
                viewHolder.mBinding.ivSelectVenue.setChecked(false);
            }

            viewHolder.mBinding.venueContainer.setVenueDetail(model);

            if (!model.getGalleries().isEmpty() && model.getGalleries() != null) {
                viewHolder.mBinding.viewPager.setVisibility(View.VISIBLE);
                viewHolder.setupData(model.getGalleries(), model);
            } else {
                List<String> coverImageList = Collections.singletonList(model.getLogo());
                viewHolder.setupData(coverImageList, model);
            }


            viewHolder.mBinding.ivSelectVenue.setOnClickListener(v -> {
                Utils.preventDoubleClick(v);
                if (!TextUtils.isEmpty(venueId) && venueId.equals(model.getId())) {
                    venueId = "";
                } else {
                    venueId = model.getId();
                }
                venueAdapter.notifyDataSetChanged();
            });

            viewHolder.mBinding.getRoot().setOnClickListener(v -> {
                Utils.preventDoubleClick(v);
                if (!TextUtils.isEmpty(venueId) && venueId.equals(model.getId())) {
                    venueId = "";
                } else {
                    venueId = model.getId();
                }
                venueAdapter.notifyDataSetChanged();
            });
        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            private final ItemPromoterSelectVenueDesignBinding mBinding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemPromoterSelectVenueDesignBinding.bind(itemView);
            }

            public void setupData(List<String> bannerModels, VenueObjectModel model) {
                MyPagerAdapter adapter = new MyPagerAdapter(activity, bannerModels, model.getId());
                mBinding.viewPager.setAdapter(adapter);
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            int nextPage = mBinding.viewPager.getCurrentItem() + 1;
                            if (nextPage == adapter.getCount()) {
                                nextPage = 0;
                            }
                            mBinding.viewPager.setCurrentItem(nextPage, true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            handler.postDelayed(this, 4000);
                        }
                    }
                };
                handler.postDelayed(runnable, 4000);

                mBinding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    }

                    @Override
                    public void onPageSelected(int position) {
                        handler.removeCallbacks(runnable);
                        handler.postDelayed(runnable, 4000);
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                    }
                });
            }
        }
    }

    public class MyPagerAdapter extends PagerAdapter {

        private Context context;

        private List<String> bannerModels;

        private String id;


        public MyPagerAdapter(Context context, List<String> bannerModels, String id) {
            this.context = context;
            this.bannerModels = bannerModels;
            this.id = id;
        }

        @Override
        public int getCount() {
            return bannerModels.size();
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            LayoutInflater inflater = LayoutInflater.from( requireActivity() );

            PagerItemBinding binding = PagerItemBinding.inflate( inflater, container, false );

            binding.aedLayout.setVisibility(View.GONE);
            binding.ivMenu.setVisibility(View.GONE);
            binding.topBlackShadow.setVisibility(View.GONE);
            binding.blackShadowView.setVisibility(View.GONE);

            String bannerModel = bannerModels.get( position );
            if (!TextUtils.isEmpty(bannerModel)) {
                Glide.with(requireActivity()).load(bannerModel).into(binding.imageView);
            }


            binding.getRoot().setOnClickListener(v -> {
                Utils.preventDoubleClick(v);
                if (!TextUtils.isEmpty(venueId) && venueId.equals(id)) {
                    venueId = "";
                } else {
                    venueId = id;
                }
                venueAdapter.notifyDataSetChanged();
            });



            container.addView( binding.getRoot() );
            return binding.getRoot();

        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView( (View) object );
        }
    }


    // endregion
    // --------------------------------------

}