package com.whosin.app.ui.fragment.Bucket;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Paint;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.whosin.app.R;
import com.whosin.app.comman.AppConstants;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.ActivityTypeDetalisListBinding;
import com.whosin.app.databinding.FragmentBucketActivitesBinding;
import com.whosin.app.databinding.ImageListBinding;
import com.whosin.app.databinding.ItemActivityFeaturesBinding;
import com.whosin.app.databinding.ItemActivitySelectDateBinding;
import com.whosin.app.service.models.ActivityAvailableFeatureModel;
import com.whosin.app.service.models.ActivityDetailModel;
import com.whosin.app.service.models.ActivityFetchModel;
import com.whosin.app.service.models.ActivitySlotModel;
import com.whosin.app.service.models.ContactListModel;
import com.whosin.app.service.models.CreateBucketListModel;
import com.whosin.app.ui.activites.home.Chat.ChatProfileActivity;
import com.whosin.app.ui.activites.home.activity.ActivityListDetail;
import com.whosin.app.ui.activites.home.activity.YourOrderActivity;
import com.whosin.app.ui.activites.venue.Bucket.BucketListBottomSheet;
import com.whosin.app.ui.activites.venue.Bucket.BucketListDetailActivity;
import com.whosin.app.ui.fragment.Profile.RemoveBucketDialog;
import com.whosin.app.ui.fragment.comman.BaseFragment;

import java.util.ArrayList;


public class BucketActivitiesFragment extends BaseFragment {

    private CreateBucketListModel bucketListModel;

    private FragmentBucketActivitesBinding binding;
    private AvailableFeatureAdapter featureAdapter = new AvailableFeatureAdapter<>();


    private ActivityListAdapter activityListAdapter ;


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    public BucketActivitiesFragment(CreateBucketListModel bucketListModel) {
        this.bucketListModel = bucketListModel;
    }

    @Override
    public void initUi(View view) {
        binding = FragmentBucketActivitesBinding.bind(view);
        setBucketDetailAdapter();

    }

    @Override
    public void setListeners() {
//        binding.eventListRecycler.setNestedScrollingEnabled(false);
    }

    @Override
    public void populateData(boolean getDataFromServer) {


    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_bucket_activites;
    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void reloadData() {
        BucketListDetailActivity parentActivity = (BucketListDetailActivity) getActivity();
        if (parentActivity != null) {
            parentActivity.requestBucketDetail(bucketListModel.getId());
        }
    }

    private void setBucketDetailAdapter() {
        binding.activityListRecycler.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false));
        activityListAdapter  = new ActivityListAdapter();
        binding.activityListRecycler.setAdapter(activityListAdapter);

        if (bucketListModel.getActivity() !=null && !bucketListModel.getActivity().isEmpty()) {
            activityListAdapter.updateData(bucketListModel.getActivity());
            binding.emptyPlaceHolderView.setVisibility(View.GONE);
            binding.activityListRecycler.setVisibility(View.VISIBLE);
        }
        else {
            binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
            binding.activityListRecycler.setVisibility(View.GONE);
        }
    }


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------


    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    public class ActivityListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {


        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.activity_type_detalis_list));
        }
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            boolean isLastItem = position == getItemCount() - 1;
            ActivityDetailModel model = (ActivityDetailModel) getItem(position);

            viewHolder.mBinding.roundLinear.setVisibility(View.GONE);
            viewHolder.mBinding.optionBucket.setVisibility(View.VISIBLE);

            viewHolder.mBinding.tvName.setText(model.getName());

            viewHolder.mBinding.tvTitle.setText(model.getProvider().getName());
            viewHolder.mBinding.tvAddress.setText(model.getProvider().getAddress());
            Graphics.loadRoundImage(model.getProvider().getLogo(), viewHolder.mBinding.iconImg);

            viewHolder.mBinding.tvRate.setText(String.format("%.1f", model.getAvgRating()));

            viewHolder.mBinding.slotRecycler.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            viewHolder.mBinding.slotRecycler.setAdapter(viewHolder.activitySlotAdapter);

            if (model.getActivityTime().getSlot() != null && !model.getActivityTime().getSlot().isEmpty()) {
                viewHolder.mBinding.txtSlotTitle.setVisibility(View.VISIBLE);
                viewHolder.mBinding.slotRecycler.setVisibility(View.VISIBLE);
                viewHolder.activitySlotAdapter.updateData(model.getActivityTime().getSlot());
            }
            else {
                viewHolder.mBinding.txtSlotTitle.setVisibility(View.GONE);
                viewHolder.mBinding.slotRecycler.setVisibility(View.GONE);
            }

            viewHolder.mBinding.tvDescription.setText(model.getDescription());

            viewHolder.mBinding.tvAED.setText(String.valueOf(model.getPrice()));
            viewHolder.mBinding.tvAED.setPaintFlags( viewHolder.mBinding.tvAED.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG );
            viewHolder.mBinding.tvPrice.setText(String.valueOf(model.getPrice() - model.getPrice() * Integer.parseInt(model.getDiscount().split("%")[0]) / 100));

            if (model.getAvilableDays().size() == 7) {
                viewHolder.mBinding.tvDays.setText("All days");
            } else {

                viewHolder.mBinding.tvDays.setText(model.getAvilableDays().toString().replaceAll("\\[", "")
                        .replaceAll("\\]", ""));
            }
            Graphics.loadImage(model.getGalleries().get(0), viewHolder.mBinding.ivCover);

            Graphics.applyBlurEffect(requireActivity(), viewHolder.mBinding.blurView);


            try {
                viewHolder.mBinding.tvStartTime.setText(Utils.convertMainDateFormat(model.getStartDate()));
                viewHolder.mBinding.tvEndDate.setText(Utils.convertMainDateFormat(model.getEndDate()));
             } catch (Exception e) {
                throw new RuntimeException(e);
            }

            viewHolder.mBinding.availableFeatureRecycler.setLayoutManager(new GridLayoutManager(requireActivity(), 2, LinearLayoutManager.VERTICAL, false));
            viewHolder.mBinding.availableFeatureRecycler.setAdapter(featureAdapter);
            featureAdapter.updateData(model.getAvilableFeatures());

            viewHolder.mBinding.startTime.setText(Utils.convertMainTimeFormat(model.getReservationStart()) );
            viewHolder.mBinding.endTime.setText(Utils.convertMainTimeFormat(model.getReservationEnd()));

            viewHolder.mBinding.optionBucket.setOnClickListener( view -> {
                Utils.preventDoubleClick( view );

                ArrayList<String> data = new ArrayList<>();
                data.add( "Remove" );
                data.add( "Move to another bucket" );
                Graphics.showActionSheet( getContext(), getString(R.string.app_name), data, (data1, position1) -> {
                    switch (position1) {
                        case 0:
                            RemoveBucketDialog dialog = new RemoveBucketDialog(bucketListModel.getId());
                            dialog.activityId = model.getId();
                            dialog.callback = data2 -> {
                                 reloadData();
                            };
                            dialog.show( getChildFragmentManager(), "" );
                            break;
                        case 1:
                            BucketListBottomSheet bucketDialog = new BucketListBottomSheet();
                            bucketDialog.activityId = model.getId();
                            bucketDialog.isBucketRemove = true;
                            bucketDialog.bucketId = bucketListModel.getId();
                            bucketDialog.callBack = data2 -> {
                                if (data2) {
                                    reloadData();
                                }
                            };

                            bucketDialog.show(getChildFragmentManager(), "");
                            break;
                    }
                } );

            } );

            viewHolder.itemView.setOnClickListener(view -> {
                Intent intent = new Intent(requireActivity(), ActivityListDetail.class);
                intent.putExtra("activityId", model.getId())
                        .putExtra( "name",model.getName())
                        .putExtra( "image", model.getProvider().getLogo() )
                        .putExtra( "title", model.getProvider().getName() )
                        .putExtra( "address", model.getProvider().getAddress());
                startActivity(intent);
//                getActivity().overridePendingTransition(R.anim.slide_up, R.anim.fade_out);
            });


            viewHolder.mBinding.btnBucketList.setOnClickListener(view -> {
                Utils.preventDoubleClick( view );

                BucketListBottomSheet dialog = new BucketListBottomSheet();
                dialog.activityId = model.getId();
//                dialog.isBucketRemove = true;
                dialog.show(getChildFragmentManager() , "");
            });


            boolean isExpried = Utils.isFutureDate(model.getReservationEnd(), AppConstants.DATEFORMAT_LONG_TIME);
//            viewHolder.mBinding.card.setVisibility(!isExpried ? View.GONE : View.VISIBLE);
            viewHolder.mBinding.layoutBuyNow.setVisibility(!isExpried ? View.GONE : View.VISIBLE);
            viewHolder.mBinding.layoutExpired.setVisibility(!isExpried ? View.VISIBLE : View.GONE);



            viewHolder.mBinding.btnBuyNow.setOnClickListener(view -> {
                startActivity( new Intent(requireActivity(), YourOrderActivity.class).putExtra( "activityModel",new Gson().toJson(model)));
            });

            if (isLastItem) {
                int marginBottom = Utils.getMarginBottom(holder.itemView.getContext(), 0.10f);
                Utils.setBottomMargin(holder.itemView, marginBottom);
            } else {
                Utils.setBottomMargin(holder.itemView, 0);
            }

        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private ActivityTypeDetalisListBinding mBinding;
             private final SelectActivitySlotAdapter<ActivitySlotModel> activitySlotAdapter = new SelectActivitySlotAdapter<>();


            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ActivityTypeDetalisListBinding.bind(itemView);

            }
        }
    }


    public class AvailableFeatureAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_activity_features));

        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            ActivityAvailableFeatureModel model = (ActivityAvailableFeatureModel) getItem(position);
            viewHolder.binding.tvName.setText(model.getFeature());
            Glide.with(requireActivity()).load(model.getIcon()).into(viewHolder.binding.ivLogo);

        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private ItemActivityFeaturesBinding binding;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemActivityFeaturesBinding.bind(itemView);
            }
        }
    }

    public class SelectActivitySlotAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {
        private int selectedPositionSlot = -1;
        private String time;

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_activity_select_date));
        }

        @SuppressLint({"SimpleDateFormat", "UseCompatLoadingForDrawables", "NotifyDataSetChanged"})
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

            ViewHolder viewHolder = (ViewHolder) holder;
            ActivitySlotModel model = (ActivitySlotModel) getItem(position);
            viewHolder.binding.iconText.setText(model.getTime());

            if (position == selectedPositionSlot) {
                viewHolder.binding.linearMainView.setBackground(context.getResources().getDrawable(R.drawable.green_button_bg));
            } else {
                viewHolder.binding.linearMainView.setBackground(context.getResources().getDrawable(R.drawable.light_black_bg));
            }
            viewHolder.binding.getRoot().setOnClickListener(view -> {
                selectedPositionSlot = position;
                time = model.getTime();
                notifyDataSetChanged();
            });
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final ItemActivitySelectDateBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemActivitySelectDateBinding.bind(itemView);
            }
        }
    }


}