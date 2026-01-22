package com.whosin.app.ui.activites.home.activity;

import static com.whosin.app.comman.Graphics.context;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tapadoo.alerter.Alerter;
import com.whosin.app.R;
import com.whosin.app.comman.AppConstants;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.ActivityListDetailBinding;
import com.whosin.app.databinding.ItemRatingReviewRecyclerBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.ActivityDetailModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.CurrentUserRatingModel;
import com.whosin.app.service.models.MessageEvent;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.comman.BaseActivity;
import com.whosin.app.ui.fragment.reviewSheet.UserFullReviewSheet;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ActivityListDetail extends BaseActivity {

    private ActivityListDetailBinding binding;

    private ActivityDetailModel activityModel;
    private String activityId = "", type = "";
    private String  name;
    private RatingReviewAdapter<CurrentUserRatingModel> reviewAdapter = new RatingReviewAdapter();


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------


    @Override
    protected void initUi() {


        applyTranslations();

        activityId = getIntent().getStringExtra("activityId");
        type = getIntent().getStringExtra("type");
        name = getIntent().getStringExtra("name");
        String title = getIntent().getStringExtra("title");
        String address = getIntent().getStringExtra("address");
        String image = getIntent().getStringExtra("image");

        binding.tvName.setText(name != null && !name.isEmpty() ? name : "");
        Graphics.loadImageWithFirstLetter(image != null && !image.isEmpty() && title != null && !title.isEmpty() ? image : "", binding.iconImg, title != null && !title.isEmpty() ? title : "");
        binding.tvTitle.setText(title != null && !title.isEmpty() ? title : "");
        binding.tvAddress.setText(address != null && !address.isEmpty() ? address : "");
        Log.d("TAG", "initUi: "+activityId);
        requestActivityDetail(activityId);

    }

    @Override
    protected void setListeners() {
        binding.ivClose.setOnClickListener(view -> {
            onBackPressed();
//            overridePendingTransition(R.anim.fade_out, R.anim.slide_down);
        });

        binding.btnBucketList.setOnClickListener(view -> {
            Utils.preventDoubleClick( view );
        });
        binding.btnBuyNow.setOnClickListener(view -> {

        });

        binding.linearReview.setOnClickListener(view -> {
Utils.preventDoubleClick( view );
            WriteReviewActivity bottomSheet = new WriteReviewActivity(activityModel.getId(), activityModel.getCurrentUserRating(), "activities");
            bottomSheet.show(getSupportFragmentManager(), "1");

        });

        binding.tvSeeAll.setOnClickListener(view -> startActivity(new Intent(activity,
                SeeAllRatingReviewActivity.class)
                .putExtra("id", activityModel.getId())
                .putExtra("type", "activities")
                .putExtra("currentUserRating", new Gson().toJson(activityModel.getCurrentUserRating()))));

        binding.imgRecommandation.setOnClickListener(v -> {
            reqRecommendation(activityModel.getId());
        });


    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityListDetailBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        requestActivityDetail(activityId);
    }

    @Override
    public void onResume() {

        super.onResume();
        requestActivityDetail(activityId);
    }

    @Override
    protected Map<View, String> getTranslationMap() {
        Map<View, String> map = new HashMap<>();
        map.put(binding.tvTapToRateTitle, "tap_to_rate");
        map.put(binding.txtReviewTitle, "write_review");
        map.put(binding.tvSeeAll, "see_all");
        map.put(binding.tvReservationFromTitle, "reservation_from");
        map.put(binding.tvReservationToTitle, "reservation_to");
        map.put(binding.tvActivityStartDateTitle, "activity_start_date");
        map.put(binding.tvActivityEndDate, "activity_end_date");
        map.put(binding.tvDaysTitle, "available_days");
        map.put(binding.btnBucketList, "bucketlist");
        map.put(binding.btnBuyNow, "buy_now");
        map.put(binding.tvExpiredTitle, "expired");

        return map;
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void getActivityModel() {


        binding.txtDescription.setText(activityModel.getDescription());

        if (activityModel.getAvgRating() != 0) {
            binding.tvRate.setText(String.format("%.1f", activityModel.getAvgRating()));
        } else {
            binding.ratingLayout.setVisibility(View.GONE);
        }

        binding.tvName.setText(activityModel.getName());

        if (activityModel.getProvider() != null) {
            Graphics.loadImageWithFirstLetter(activityModel.getProvider().getLogo(), binding.iconImg, activityModel.getProvider().getName());
            binding.tvTitle.setText(activityModel.getProvider().getName());
            binding.tvAddress.setText(activityModel.getProvider().getAddress());
        }

        if (activityModel.getDisclaimerTitle() != null && !activityModel.getDisclaimerTitle().isEmpty()) {
            binding.tvDisclaimerTitle.setText(activityModel.getDisclaimerTitle());
            binding.tvDisclaimerDescription.setText(activityModel.getDisclaimerDescription());
            binding.tvDisclaimerDescription.post(() -> {
                int lineCount = binding.tvDisclaimerDescription.getLineCount();
                if (lineCount > 2) {
                    Utils.makeTextViewResizable(binding.tvDisclaimerDescription, 3, 3, ".. " + getValue("see_more"), true);
                }
            });
        }
        else {
            binding.tvDisclaimerTitle.setVisibility(View.GONE);
            binding.tvDisclaimerDescription.setVisibility(View.GONE);
        }

        binding.tvRating.setText(String.format("%.1f", activityModel.getAvgRating()));

        if (activityModel.getRecommendation()){
            int newColor = ContextCompat.getColor(activity, R.color.brand_pink);
            binding.imgRecommandation.setColorFilter(newColor);
        }else {
            int newColor = ContextCompat.getColor(activity, R.color.white);
            binding.imgRecommandation.setColorFilter(newColor);
        }

        binding.tvDays.setText(activityModel.getAvilableDays().toString().replaceAll("\\[", "")
                .replaceAll("\\]", ""));

        Graphics.loadImage(activityModel.getGalleries().get(0), binding.ivCover);
        Graphics.applyBlurEffect(activity, binding.blurView);

        binding.tvReservationStart.setText(Utils.convertMainDateFormat(activityModel.getReservationStart()));
        binding.tvReservationEnd.setText(Utils.convertMainDateFormat(activityModel.getReservationEnd()));
        binding.tvStartDate.setText(Utils.convertMainDateFormat(activityModel.getStartDate()));
        binding.tvEndDate.setText(Utils.convertMainDateFormat(activityModel.getEndDate()));

        binding.availableFeatureRecycler.setLayoutManager(new GridLayoutManager(activity, 2, LinearLayoutManager.VERTICAL, false));

        if (activityModel.getDiscount().equals("0")) {
            binding.tvAED.setVisibility(View.GONE);
        } else {
            binding.tvAED.setVisibility(View.VISIBLE);
        }

        binding.tvAED.setText(String.valueOf(activityModel.getPrice()));
        binding.tvAED.setPaintFlags(binding.tvAED.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        binding.tvPrice.setText(String.valueOf(activityModel.getPrice() - activityModel.getPrice() * Integer.parseInt(activityModel.getDiscount().split("%")[0]) / 100));

        binding.ratingReviewRecycler.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        binding.ratingReviewRecycler.setAdapter(reviewAdapter);
        List<CurrentUserRatingModel> list = activityModel.getUsers().stream()
                .filter(user -> !activityModel.getReviews().isEmpty())
                .flatMap(user -> activityModel.getReviews().stream()
                        .filter(review -> review.getUserId().equals(user.getId())))
                .collect(Collectors.toList());


        if (!list.isEmpty()) {
            activity.runOnUiThread(() -> {
                reviewAdapter.updateData(list);
                reviewAdapter.notifyDataSetChanged();
            });
        }

        List<CurrentUserRatingModel> matchingList = list.stream()
                .filter(ratingModel -> SessionManager.shared.getUser().getId().equals(ratingModel.getUserId()))
                .collect(Collectors.toList());

        if (!matchingList.isEmpty()) {
            binding.txtReviewTitle.setText(getValue("edit_review"));
        }

        SessionManager.shared.getUser().getId();
        binding.rating.setOnRatingChangeListener(null);
        binding.rating.setRating(activityModel.getCurrentUserRating().getStars());
        binding.rating.setOnRatingChangeListener((ratingBar, rating) -> {

            requestAddRating((int) rating);
        });

        boolean isExpried = Utils.isFutureDate(activityModel.getReservationEnd(), AppConstants.DATEFORMAT_LONG_TIME);
        boolean isExpriedEndDate = Utils.isFutureDate(activityModel.getEndDate(), AppConstants.DATEFORMAT_LONG_TIME);
        binding.bucketListCardView.setVisibility(!isExpriedEndDate ? View.GONE : View.VISIBLE);
        binding.buyNowLayout.setVisibility(!isExpried ? View.GONE : View.VISIBLE);
        binding.layoutExpired.setVisibility(!isExpried ? View.VISIBLE : View.GONE);



    }

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void reqRecommendation(String id) {
        showProgress();
        DataService.shared(activity).requestFeedRecommandation(id, "activity", new RestCallback<ContainerModel<UserDetailModel>>(this) {
            @Override
            public void result(ContainerModel<UserDetailModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, R.string.service_message_something_wrong, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (model.message.equals("recommendation added successfully!")){
                    int newColor = ContextCompat.getColor(activity, R.color.brand_pink);
                    binding.imgRecommandation.setColorFilter(newColor);
                    Alerter.create(activity).setTitle(getValue("thank_you")).setTitleAppearance(R.style.AlerterTitle).setTextAppearance(R.style.AlerterText).setText(setValue("recommending_toast",name)).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                }else {
                    int newColor = ContextCompat.getColor(activity, R.color.white);
                    binding.imgRecommandation.setColorFilter(newColor);
                    Alerter.create(activity).setTitle(getValue("oh_snap")).setTitleAppearance(R.style.AlerterTitle).setTextAppearance(R.style.AlerterText).setText(setValue("recommending_remove_toast",name)).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                }


            }
        });
    }

    private void requestActivityDetail(String id) {

        showProgress();
        Log.d("TAG", "requestActivityDetail: "+id);
        DataService.shared(activity).requestActivityDetail(id, new RestCallback<ContainerModel<ActivityDetailModel>>(this) {
            @Override
            public void result(ContainerModel<ActivityDetailModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Log.d("TAG", "result: "+error);
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }
                activityModel = model.getData();
                getActivityModel();
                binding.linear.setVisibility(View.VISIBLE);
            }
        });
    }

    private void requestAddRating(int rating) {
        DataService.shared(activity).requestAddRatings(activityId, rating, type, "", "", new RestCallback<ContainerModel<CurrentUserRatingModel>>(this) {
            @Override
            public void result(ContainerModel<CurrentUserRatingModel> model, String error) {
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, R.string.service_message_something_wrong, Toast.LENGTH_SHORT).show();
                    return;
                }
                EventBus.getDefault().post(new MessageEvent());
                Toast.makeText(activity, model.message, Toast.LENGTH_SHORT).show();
            }
        });

    }


    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    public class RatingReviewAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = UiUtils.getViewBy(parent, R.layout.item_rating_review_recycler);

            ViewGroup.LayoutParams params = view.getLayoutParams();
            int itemCount = getItemCount();
            if (itemCount > 1) {
                params.width = (int) (Graphics.getScreenWidth(context) * 0.85);
            } else {
                params.width = (int) (Graphics.getScreenWidth(context) * 0.90);
            }
            view.setLayoutParams(params);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            ViewHolder viewHolder = (ViewHolder) holder;
            CurrentUserRatingModel model = (CurrentUserRatingModel) getItem(position);
            viewHolder.mBinding.txtReview.setText(model.getReview());
            viewHolder.mBinding.replyLinear.setVisibility( View.GONE );
            if (activityModel.getUsers() != null && !activityModel.getUsers().isEmpty()) {
                viewHolder.mBinding.txtTitle.setText(Utils.notNullString(activityModel.getUsers().get(position).getFullName()));
                Graphics.loadImageWithFirstLetter(activityModel.getUsers().get(position).getImage(), viewHolder.mBinding.ivRating, activityModel.getUsers().get(position).getFirstName());
            } else {
                viewHolder.mBinding.txtTitle.setVisibility(View.GONE);
            }

            if (model.getReply() != null) {
                viewHolder.mBinding.txtReply.setText(model.getReply());
            } else {
                viewHolder.mBinding.linearReply.setVisibility(View.GONE);
            }

            viewHolder.mBinding.txtDate.setText(Utils.convertMainDateFormat(model.getCreatedAt()));

            viewHolder.mBinding.rating.setRating(model.getStars());

            viewHolder.mBinding.rating.setIsIndicator(true);

            viewHolder.mBinding.getRoot().setOnClickListener(v -> {
                UserFullReviewSheet reviewSheet = new UserFullReviewSheet();
                reviewSheet.userDetailModel = activityModel.getUsers().get(position);
                reviewSheet.currentUserRatingModel = model;
                reviewSheet.callback = data -> {
                    if (data) requestActivityDetail(activityId);
                };
                reviewSheet.show(getSupportFragmentManager(),"");
            });
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private ItemRatingReviewRecyclerBinding mBinding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemRatingReviewRecyclerBinding.bind(itemView);
            }
        }
    }


    // endregion
    // --------------------------------------

}