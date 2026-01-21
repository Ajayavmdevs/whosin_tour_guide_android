package com.whosin.app.ui.activites.home;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.whosin.app.R;
import com.whosin.app.comman.AppConstants;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.ActivitySeeAllDetalisBinding;
import com.whosin.app.databinding.ActivityTypeDetalisListBinding;
import com.whosin.app.databinding.ItemActivityFeaturesBinding;
import com.whosin.app.databinding.ItemActivitySelectDateBinding;
import com.whosin.app.databinding.SwipeActivityTypeBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.AppSettingManager;
import com.whosin.app.service.models.ActivityAvailableFeatureModel;
import com.whosin.app.service.models.ActivityDetailModel;
import com.whosin.app.service.models.ActivitySlotModel;
import com.whosin.app.service.models.ActivityTypeModel;
import com.whosin.app.service.models.AppSettingActivityTypeModel;
import com.whosin.app.service.models.BannerModel;
import com.whosin.app.service.models.ContainerListModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.comman.BaseActivity;
import com.whosin.app.ui.activites.home.activity.ActivityListDetail;
import com.whosin.app.ui.activites.home.activity.BannerWebViewActivity;
import com.whosin.app.ui.activites.home.activity.YourOrderActivity;
import com.whosin.app.ui.activites.venue.Bucket.BucketListBottomSheet;
import com.whosin.app.ui.activites.venue.VenueBuyNowActivity;

import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SeeAllDetalisActivity extends BaseActivity {

    private ActivitySeeAllDetalisBinding binding;
    private List<AppSettingActivityTypeModel> activityTypeModel = new ArrayList<>();
    private final ActivityTypeAdapter<AppSettingActivityTypeModel> adapter = new ActivityTypeAdapter<>();
    private final ActivityTypeListAdapter<ActivityDetailModel> activityTypeListAdapter = new ActivityTypeListAdapter<>();
    private final AvailableFeatureAdapter<ActivityAvailableFeatureModel> featureAdapter = new AvailableFeatureAdapter<>();
    private int selectedPosition = 0;
    private int lastSelectedPosition = -1;
    private int currentPage = 1;

    // --------------------------------------
    // region LifeCycle
    //


    @Override
    protected void initUi() {

        binding.txtTitleToolbar.setText(getValue("our_activities"));
        binding.emptyPlaceHolderView.setEmptyPlaceTxtTitle(getValue("activity_empty"));

        setListeners();
        getActivityType();

        requestBannerList();
        binding.activityListRecycler.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        binding.activityListRecycler.setAdapter(activityTypeListAdapter);
        requestActivityList("");
    }

    @Override
    protected void setListeners() {
        binding.ivClose.setOnClickListener(view -> onBackPressed());
    }


    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivitySeeAllDetalisBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void getActivityType() {
        binding.typeRecycler.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        binding.typeRecycler.setAdapter(adapter);
        if (activityTypeModel.isEmpty()) {
            AppSettingActivityTypeModel appSettingActivityTypeModel = new AppSettingActivityTypeModel();
            appSettingActivityTypeModel.setTitle("All");
            appSettingActivityTypeModel.setId("");
            activityTypeModel.add(0, appSettingActivityTypeModel);
        }
        if (AppSettingManager.shared.getAppSettingData().getActivityType() != null && !AppSettingManager.shared.getAppSettingData().getActivityType().isEmpty()) {
            activityTypeModel.addAll(AppSettingManager.shared.getAppSettingData().getActivityType());
            if (activityTypeModel != null && !activityTypeModel.isEmpty()) {
                adapter.updateData(activityTypeModel);
            }
        }
    }

    private void getType(List<BannerModel> data) {
        if (data.get(0).getType().equals("link")) {
            binding.cardView.setOnClickListener(view -> startActivity(new Intent(activity, BannerWebViewActivity.class).putExtra("link", data.get(0).getLink().toString())));
        } else {
            binding.cardView.setOnClickListener(view -> {
                startActivity(new Intent(activity, ActivityListDetail.class));
//                activity.overridePendingTransition(R.anim.slide_up, R.anim.fade_out);
            });
        }
    }


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------


    private void requestActivityList(String id) {
        showProgress();
        DataService.shared(activity).requestActivityList(id, new RestCallback<ContainerListModel<ActivityDetailModel>>(this) {
            @Override
            public void result(ContainerListModel<ActivityDetailModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, R.string.service_message_something_wrong, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (model.data != null && !model.data.isEmpty()) {
                    activityTypeListAdapter.updateData(model.data);
                    binding.activityListRecycler.setVisibility(View.VISIBLE);
                    binding.linear.setVisibility(View.VISIBLE);
                    binding.cardView.setVisibility(View.VISIBLE);
                    binding.emptyPlaceHolderView.setVisibility(View.GONE);
                } else {
                    binding.activityListRecycler.setVisibility(View.GONE);
                    binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
                }


            }
        });
    }


    private void requestBannerList() {
        DataService.shared(activity).requestBannerList(new RestCallback<ContainerListModel<BannerModel>>(this) {
            @Override
            public void result(ContainerListModel<BannerModel> model, String error) {
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, R.string.service_message_something_wrong, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (model.data != null) {
                    binding.imageCarousel.registerLifecycle(getLifecycle());
                    List<CarouselItem> carouselItems = new ArrayList<>();
                    for (BannerModel imageUrl : model.data) {
                        carouselItems.add(new CarouselItem(imageUrl.getImage()));
                    }
                    binding.imageCarousel.setData(carouselItems);
                    getType(model.data);
                }
            }
        });
    }


    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    public class ActivityTypeAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {


        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.swipe_activity_type));

        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            AppSettingActivityTypeModel appSettingActivityTypeModel = (AppSettingActivityTypeModel) getItem(position);

            if (appSettingActivityTypeModel != null) {
                viewHolder.mBinding.iconText.setText(appSettingActivityTypeModel.getTitle());

                viewHolder.mBinding.getRoot().setOnClickListener(view -> {
                    lastSelectedPosition = selectedPosition;
                    selectedPosition = holder.getBindingAdapterPosition();
                    notifyItemChanged(lastSelectedPosition);
                    notifyItemChanged(selectedPosition);

                });

                if (selectedPosition == holder.getBindingAdapterPosition()) {
                    requestActivityList(appSettingActivityTypeModel.getId());
                    viewHolder.mBinding.linearMainView.setBackground(activity.getResources().getDrawable(R.drawable.green_button_bg_activity));
                    currentPage = 1;
                } else {
                    viewHolder.mBinding.linearMainView.setBackground(activity.getResources().getDrawable(R.drawable.light_black_bg));
                }
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final SwipeActivityTypeBinding mBinding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = SwipeActivityTypeBinding.bind(itemView);
            }
        }
    }


    public class ActivityTypeListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.activity_type_detalis_list));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            ActivityDetailModel model = (ActivityDetailModel) getItem(position);
            if (model != null) {
                viewHolder.mBinding.tvName.setText(model.getName());

                viewHolder.mBinding.tvTitle.setText(model.getProvider().getName());
                viewHolder.mBinding.tvAddress.setText(model.getProvider().getAddress());
                Graphics.loadRoundImage(model.getProvider().getLogo(), viewHolder.mBinding.iconImg);

           /* if (model.getActivityTime() != null) {
                viewHolder.mBinding.tvRate.setText( String.format( "%.1f", model.getAvgRating() ) );
            } else {
                viewHolder.mBinding.ratingLayout.setVisibility( View.GONE );
            }
*/

                viewHolder.mBinding.tvDescription.setText(model.getDescription());

                viewHolder.mBinding.tvAED.setText(String.valueOf(model.getPrice()));
                viewHolder.mBinding.tvAED.setPaintFlags(viewHolder.mBinding.tvAED.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                viewHolder.mBinding.tvPrice.setText(String.valueOf(model.getPrice() - model.getPrice() * Integer.parseInt(model.getDiscount().split("%")[0]) / 100));

                if (model.getAvilableDays().size() == 7) {
                    viewHolder.mBinding.tvDays.setText("All days");
                } else {

                    viewHolder.mBinding.tvDays.setText(model.getAvilableDays().toString().replaceAll("\\[", "")
                            .replaceAll("\\]", ""));
                }


                Graphics.loadImage(model.getGalleries().get(0), viewHolder.mBinding.ivCover);

                Graphics.applyBlurEffect(activity, viewHolder.mBinding.blurView);

                viewHolder.mBinding.tvStartTime.setText(Utils.convertMainDateFormat(model.getStartDate()));
                viewHolder.mBinding.tvEndDate.setText(Utils.convertMainDateFormat(model.getEndDate()));

                viewHolder.mBinding.availableFeatureRecycler.setLayoutManager(new GridLayoutManager(activity, 2, LinearLayoutManager.VERTICAL, false));
                viewHolder.mBinding.availableFeatureRecycler.setAdapter(featureAdapter);
                featureAdapter.updateData(model.getAvilableFeatures());

                if (model.getActivityTime().getType().equals("slot")) {

                    if (!model.getActivityTime().getSlot().isEmpty()) {
                        viewHolder.adapter.updateData(model.getActivityTime().getSlot());
                        viewHolder.mBinding.ilActivityTime.setVisibility(View.GONE);
                    }

                } else {
                    viewHolder.mBinding.startTime.setText(String.valueOf(model.getActivityTime().getStart()));
                    viewHolder.mBinding.endTime.setText(String.valueOf(model.getActivityTime().getEnd()));
                    viewHolder.mBinding.txtSlotTitle.setVisibility(View.GONE);
                }


                viewHolder.itemView.setOnClickListener(view -> {
                    Intent intent = new Intent(activity, ActivityListDetail.class);
                    intent.putExtra("activityId", model.getId()).putExtra("name", model.getName());
                    intent.putExtra("image", model.getProvider().getLogo());
                    intent.putExtra("title", model.getProvider().getName());
                    intent.putExtra("address", model.getProvider().getAddress());
                    startActivity(intent);
                });


                viewHolder.mBinding.btnBucketList.setOnClickListener(view -> {
                    Utils.preventDoubleClick( view );
                    BucketListBottomSheet dialog = new BucketListBottomSheet();
                    dialog.activityId = model.getId();
                    dialog.show(getSupportFragmentManager(), "");
                });

                viewHolder.mBinding.btnBuyNow.setOnClickListener(view -> {
                    startActivity(new Intent(activity, YourOrderActivity.class).putExtra("activityModel", new Gson().toJson(model)));
                });
                Graphics.applyBlurEffect(activity, viewHolder.mBinding.blurView);

                boolean isExpried = Utils.isFutureDate(model.getEndDate(), AppConstants.DATEFORMAT_LONG_TIME);
                viewHolder.mBinding.card.setVisibility(!isExpried ? View.GONE : View.VISIBLE);

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

                try {
                    Date givenDate = dateFormat.parse(model.getReservationEnd());
                    Date currentDate = new Date();
                    if (givenDate.before(currentDate)) {
                        viewHolder.mBinding.layoutBuyNow.setVisibility(View.GONE);
                        viewHolder.mBinding.layoutExpired.setVisibility(View.VISIBLE);
                    } else if (givenDate.after(currentDate)) {
                        viewHolder.mBinding.layoutBuyNow.setVisibility(View.VISIBLE);
                        viewHolder.mBinding.layoutExpired.setVisibility(View.GONE);
                        viewHolder.mBinding.btnBuyNow.setOnClickListener(view -> {
                            startActivity(new Intent(activity, YourOrderActivity.class).putExtra("activityModel", new Gson().toJson(model)));
                        });
                    } else {
                        viewHolder.mBinding.btnBuyNow.setOnClickListener(view -> {
                            startActivity(new Intent(activity, YourOrderActivity.class).putExtra("activityModel", new Gson().toJson(model)));
                        });
                        viewHolder.mBinding.layoutBuyNow.setVisibility(View.VISIBLE);
                        viewHolder.mBinding.layoutExpired.setVisibility(View.GONE);
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final ActivityTypeDetalisListBinding mBinding;

            private final AvailableSlotAdapter<ActivitySlotModel> adapter = new AvailableSlotAdapter<>();

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ActivityTypeDetalisListBinding.bind(itemView);
                mBinding.slotRecycler.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
                mBinding.slotRecycler.setAdapter(adapter);
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
//            Graphics.loadImage( model.getIcon(),viewHolder.binding.ivLogo );
            Glide.with(activity).load(model.getIcon()).into(viewHolder.binding.ivLogo);

        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final ItemActivityFeaturesBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemActivityFeaturesBinding.bind(itemView);
            }
        }
    }


    public class AvailableSlotAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_activity_select_date));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            ViewHolder viewHolder = (ViewHolder) holder;
            ActivitySlotModel model = (ActivitySlotModel) getItem(position);
            viewHolder.binding.iconText.setText(model.getTime());
            viewHolder.binding.linearMainView.setBackground(activity.getResources().getDrawable(R.drawable.slot_bg));


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