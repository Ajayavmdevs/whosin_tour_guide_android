package com.whosin.app.ui.activites.home.activity;

import static com.whosin.app.comman.Graphics.context;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.tapadoo.alerter.Alerter;
import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.ActivitySeeAllRatingReviewBinding;
import com.whosin.app.databinding.ItemRatingReviewRecyclerBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.AppSettingManager;
import com.whosin.app.service.manager.BlockUserManager;
import com.whosin.app.service.manager.RaynaTicketManager;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.CommonModel;
import com.whosin.app.service.models.ContactListModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.CurrentUserRatingModel;
import com.whosin.app.service.models.MessageEvent;
import com.whosin.app.service.models.ReviewModel;
import com.whosin.app.service.models.ReviewReplayModel;
import com.whosin.app.service.models.TotalRatingModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.Promoter.ReviewReplayDialog;
import com.whosin.app.ui.activites.auth.AuthenticationActivity;
import com.whosin.app.ui.activites.comman.BaseActivity;
import com.whosin.app.ui.activites.reportedUser.ReportedUseSuccessDialog;
import com.whosin.app.ui.fragment.Chat.ReportAndBlockBottomSheet;
import com.whosin.app.ui.fragment.Chat.ReportBottomSheet;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class SeeAllRatingReviewActivity extends BaseActivity {
    private ActivitySeeAllRatingReviewBinding binding;
    private final RatingReviewAdapter<CurrentUserRatingModel> reviewAdapter = new RatingReviewAdapter<>();
    private List<ContactListModel> detailModel = new ArrayList<>();
    private String id = "", type = "";
    private CurrentUserRatingModel userRatingModel;
    String rateDate = "";
    private float start = 0;
    private boolean isMyProfile = false;
    private boolean ignoreNextRatingChange = false;
    private final Handler ratingHandler = new Handler(Looper.getMainLooper());
    private Runnable ratingRunnable;
    private static final long RATING_DELAY = 800;



    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    protected void initUi() {

        applyTranslations();

        isMyProfile = getIntent().getBooleanExtra("isMyProfile", false);
        id = Utils.notNullString(getIntent().getStringExtra("id"));
        type = Utils.notNullString(getIntent().getStringExtra("type"));
        start = getIntent().getFloatExtra("start", 0);
        boolean isEnableReview  = getIntent().getBooleanExtra("isEnableReview", false);
        boolean isEnableRating  = getIntent().getBooleanExtra("isEnableRating", false);

        if (type.equals("ticket") && !isEnableReview){
            binding.linearReview.setEnabled(false);
            binding.linearReview.setClickable(false);
            binding.linearReview.setAlpha(0.5f);
        }

        String model = Utils.notNullString(getIntent().getStringExtra("currentUserRating"));
        userRatingModel = new Gson().fromJson(model, CurrentUserRatingModel.class);

        binding.ratingReviewRecycler.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        binding.ratingReviewRecycler.setAdapter(reviewAdapter);
        requestRatingSummary(true);

        binding.rating.setOnRatingChangeListener(null);
        if ((userRatingModel != null)) {
            binding.rating.setRating(userRatingModel.getStars());

            if (type.equals("ticket") && !isEnableRating){
                binding.rating.setIsIndicator(true);
            }else {
                binding.rating.setOnRatingChangeListener((ratingBar, rating) -> {
                    if (ignoreNextRatingChange) {
                        ignoreNextRatingChange = false;
                        return;
                    }

                    // Cancel previous pending API call
                    if (ratingRunnable != null) {
                        ratingHandler.removeCallbacks(ratingRunnable);
                    }

                    // Schedule new API call after delay
                    ratingRunnable = () -> requestAddRating((int) rating);
                    ratingHandler.postDelayed(ratingRunnable, RATING_DELAY);
                });


                binding.rating.setIsIndicator(false);
            }


        }
    }

    @Override
    protected void setListeners() {

        Glide.with(activity).load(R.drawable.icon_close_btn).into(binding.ivClose);
        binding.ivClose.setOnClickListener(v -> onBackPressed());

        binding.linearReview.setOnClickListener(view -> {
            Utils.preventDoubleClick(view);
            if (Utils.isGuestLogin()) {
                Graphics.showAlertDialogWithOkCancel(activity, getString(R.string.app_name), getValue("login_required_for_review"), getValue("cancel"), getValue("Login"), isConfirmed -> {
                    if (!isConfirmed) {
                        Intent intent = new Intent(this, AuthenticationActivity.class);
                        intent.putExtra("isGuestLogin", true);
                        activityLauncher.launch(intent, result -> {
                            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                                openRatingSheet();
                            }
                        });
                    }
                });
            } else {
                openRatingSheet();
            }
        });
    }


    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivitySeeAllRatingReviewBinding.inflate(getLayoutInflater());
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
        requestRatingList();
        if (type.equals("ticket") && RaynaTicketManager.shared.callbackForReload != null) {
            RaynaTicketManager.shared.callbackForReload.onReceive(true);
        }
    }



    @Override
    protected Map<View, String> getTranslationMap() {
        Map<View, String> map = new HashMap<>();
        map.put(binding.tvRatingReviewTitle, "rating_and_reviews");
        map.put(binding.tvTapToRateTitle, "tap_to_rate");
        map.put(binding.txtReviewTitle, "write_review");

        binding.emptyPlaceHolderView.setEmptyPlaceTxtTitle(getValue("there_is_no_review_available"));
        return map;
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void openRatingSheet(){
        WriteReviewActivity bottomSheet = new WriteReviewActivity(id, userRatingModel, type);
        bottomSheet.activity = activity;
        bottomSheet.result = data -> {
            if (data != null) {
                binding.rating.setRating(data);
                if (userRatingModel != null) {
                    userRatingModel.setStars(data);
                }
            }
        };
        bottomSheet.show(getSupportFragmentManager(), "1");
    }

    private int parsePercentage(String percentage) {
        try {
            String cleanPercentage = percentage.replaceAll("[^0-9.]", "");
            return Math.round(Float.parseFloat(cleanPercentage));
        } catch (NumberFormatException | NullPointerException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private void reviewSummary(TotalRatingModel model) {

        binding.tvTotalRating.setText(String.format(Locale.ENGLISH, "%d %s", model.getTotalRating(),getValue("ratings")));

        // Set progress bars from 5-star to 1-star
        int[] percentages = {
                parsePercentage(model.getSummary().get5().getPercentage()),
                parsePercentage(model.getSummary().get4().getPercentage()),
                parsePercentage(model.getSummary().get3().getPercentage()),
                parsePercentage(model.getSummary().get2().getPercentage()),
                parsePercentage(model.getSummary().get1().getPercentage())
        };

        ProgressBar[] bars = {
                binding.progress, binding.progress1, binding.progress2,
                binding.progress3, binding.progress4
        };

        for (int i = 0; i < bars.length; i++) {
            if (bars[i].getProgress() != percentages[i]) {
                bars[i].setProgress(percentages[i]);
            }
        }

        String avgStr = new BigDecimal(String.valueOf(model.getAvgRating()))
                .setScale(1, RoundingMode.HALF_UP)
                .toString();

        if (!avgStr.equals(binding.txtStar.getText().toString())) {
            binding.txtStar.setText(avgStr);
        }

        binding.tvOutOfRate.setText(getValue("out_of_five"));
    }

    private void HandleData(ReviewModel data) {
        List<CurrentUserRatingModel> allReviews = data.getReviews();
        List<ContactListModel> allUsers = data.getUsers();

        // Match users with reviews (if any)
        List<CurrentUserRatingModel> reviewList = allUsers.stream()
                .flatMap(user -> allReviews.stream()
                        .filter(review -> review.getUserId().equals(user.getId())))
                .sorted(Comparator.comparing(CurrentUserRatingModel::getCreatedAt).reversed())
                .collect(Collectors.toList());

        boolean hasReviews = !reviewList.isEmpty();
        boolean isOwnProfile = isMyProfile;

        // Show/Hide views based on list state
        binding.linearReview.setVisibility(isOwnProfile ? View.GONE : View.VISIBLE);
        binding.linearRating.setVisibility(isOwnProfile ? View.GONE : View.VISIBLE);
        binding.emptyPlaceHolderView.setVisibility(hasReviews ? View.GONE : View.VISIBLE);
        binding.ratingReviewRecycler.setVisibility(hasReviews ? View.VISIBLE : View.GONE);

        if (hasReviews) {
            activity.runOnUiThread(() -> {
                List<CurrentUserRatingModel> sortedList = reviewList.stream()
                        .sorted((r1, r2) -> {
                            String currentUserId = SessionManager.shared.getUser().getId();
                            boolean isR1CurrentUser = r1.getUserId().equals(currentUserId);
                            boolean isR2CurrentUser = r2.getUserId().equals(currentUserId);

                            if (isR1CurrentUser && !isR2CurrentUser) return -1;
                            else if (!isR1CurrentUser && isR2CurrentUser) return 1;
                            else return 0;
                        })
                        .collect(Collectors.toList());

                reviewAdapter.updateData(sortedList);

                String userId = SessionManager.shared.getUser().getId();

                Optional<CurrentUserRatingModel> userReviewOpt = reviewList.stream()
                        .filter(review -> userId.equals(review.getUserId()))
                        .findFirst();

                binding.txtReviewTitle.setText(userReviewOpt.isPresent() ? getValue("edit_review") : getValue("write_review"));

                userReviewOpt.ifPresent(model -> userRatingModel = model);
            });
        } else {
            binding.txtReviewTitle.setText(getValue("write_review"));
        }

        detailModel = allUsers;
    }

    private void openDeleteActionSheet(String model){
        ArrayList<String> data = new ArrayList<>();
        data.add(getValue("delete"));
        Graphics.showActionSheet(activity, activity.getString(R.string.app_name), getValue("close"), data, (data1, position1) -> {
            if (position1 == 0) {
                Graphics.showAlertDialogWithOkCancel(activity, activity.getString(R.string.app_name), getValue("delete_review_confirm"), getValue("yes"), getValue("cancel"), isConfirmed -> {
                    if (isConfirmed) {
                        requestMyReviewDelete(model);
                    }
                });
            }
        });
    }


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestRatingList() {
        DataService.shared(activity).requestRatingList(type, id, 30, 1, new RestCallback<ContainerModel<ReviewModel>>(this) {
            @Override
            public void result(ContainerModel<ReviewModel> model, String error) {
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, R.string.service_message_something_wrong, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (model.getData() != null) {
                    HandleData(model.getData());
                }
                if (id.equals(SessionManager.shared.getUser().getId())) {
                    binding.linearRating.setVisibility(View.GONE);
                    binding.linearReview.setVisibility(View.GONE);
                    binding.rating.setRating(start);
                    binding.rating.setIsIndicator(true);
                }

            }
        });
    }


    private void requestRatingSummary(boolean isCallRatingListApi) {
        showProgress();
        DataService.shared(activity).requestRatingSummary(id, new RestCallback<ContainerModel<TotalRatingModel>>(this) {
            @Override
            public void result(ContainerModel<TotalRatingModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, R.string.service_message_something_wrong, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (model.getData() != null){
                    reviewSummary(model.getData());
                }

                EventBus.getDefault().post(new MessageEvent());
                binding.linearSummary.setVisibility(View.VISIBLE);
                if (isCallRatingListApi){
                    requestRatingList();
                }
                if (type.equals("ticket") && RaynaTicketManager.shared.callbackForReload != null) {
                    RaynaTicketManager.shared.callbackForReload.onReceive(true);
                }
            }
        });
    }


    private void requestAddRating(int rating) {
        DataService.shared(activity).requestAddRatings(id, rating, type, "", "", new RestCallback<ContainerModel<CurrentUserRatingModel>>(this) {
            @Override
            public void result(ContainerModel<CurrentUserRatingModel> model, String error) {
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }
                requestRatingSummary(false);
                EventBus.getDefault().post(new MessageEvent());
                userRatingModel = model.getData();
                Toast.makeText(activity, model.message, Toast.LENGTH_SHORT).show();

                if (type.equals("ticket") && RaynaTicketManager.shared.callbackForReload != null) {
                    RaynaTicketManager.shared.callbackForReload.onReceive(true);
                }
            }
        });

    }

    private void requestBlockUserAdd(String id,String userFullName) {
        DataService.shared(activity).requestBlockUser(id, new RestCallback<ContainerModel<CommonModel>>(this) {
            @Override
            public void result(ContainerModel<CommonModel> model, String error) {
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }
                Alerter.create(activity).setTitle(getValue("oh_snap")).setText(getValue("you_have_blocked") + userFullName).setTextAppearance(R.style.AlerterText).setTitleAppearance(R.style.AlerterTitle).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                BlockUserManager.addBlockUserId(id);
                requestRatingList();
                if (AppSettingManager.shared.venueReloadCallBack != null){
                    AppSettingManager.shared.venueReloadCallBack.onReceive(true);
                }
                finish();

            }
        });
    }

    private void requestMyReviewDelete(String id) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", id);
        showProgress();
        DataService.shared(activity).requestMyReviewDelete(jsonObject, new RestCallback<ContainerModel<CurrentUserRatingModel>>(this) {
            @Override
            public void result(ContainerModel<CurrentUserRatingModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }
                requestRatingSummary(false);
                ignoreNextRatingChange = true;
                binding.rating.setRating(0);
                if (AppSettingManager.shared.venueReloadCallBack != null){
                    AppSettingManager.shared.venueReloadCallBack.onReceive(false);
                }
                requestRatingList();
            }
        });
    }


    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    private class RatingReviewAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = UiUtils.getViewBy(parent, R.layout.item_rating_review_recycler);
            ViewGroup.LayoutParams params = view.getLayoutParams();
            int itemCount = getItemCount();
            if (itemCount > 1) {
                params.width = (int) (Graphics.getScreenWidth(context) * 0.85);
            } else {
                params.width = (int) (Graphics.getScreenWidth(context) * 0.93);
            }
            params.width = (int) (Graphics.getScreenWidth(context) * 0.93);
            view.setLayoutParams(params);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            ViewHolder viewHolder = (ViewHolder) holder;
            CurrentUserRatingModel model = (CurrentUserRatingModel) getItem(position);
            viewHolder.mBinding.txtReview.setText(model.getReview());
            viewHolder.mBinding.rating.setRating(model.getStars());
            viewHolder.mBinding.iconMenu.setVisibility(View.VISIBLE);
            Optional<ContactListModel> venueObjectModel = detailModel.stream().
                    filter(p -> p.getId().equals(model.getUserId())).findFirst();
            if (venueObjectModel.isPresent()) {
                Graphics.loadImageWithFirstLetter(venueObjectModel.get().getImage(),
                        viewHolder.mBinding.ivRating, venueObjectModel.get().getFirstName());
                viewHolder.mBinding.txtTitle.setText(venueObjectModel.get().getFullName());
            } else {
                viewHolder.mBinding.getRoot().setVisibility(View.GONE);

            }
            viewHolder.mBinding.rating.setIsIndicator(true);


            Date date;
            try {
                date = Utils.stringToDate(model.getCreatedAt(), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                rateDate = Utils.formatDate(date, "dd MMM yyyy");

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            viewHolder.mBinding.txtDate.setText(rateDate);

            if (isMyProfile) {
                viewHolder.mBinding.replyLinear.setVisibility(View.VISIBLE);
            } else {
                // viewHolder.mBinding.replyLinear.setVisibility(View.GONE);
            }

            viewHolder.mBinding.txtReply.setText(model.getReply());
            Graphics.loadImageWithFirstLetter(SessionManager.shared.getUser().getImage(), viewHolder.mBinding.image, SessionManager.shared.getUser().getFullName());


            if (model.getReply() != null && !model.getReply().isEmpty()) {
                viewHolder.mBinding.layoutReview.setBackgroundColor(Color.TRANSPARENT);
                viewHolder.mBinding.tvTitle.setText(SessionManager.shared.getUser().getFullName());
                viewHolder.mBinding.tvTitle.setPadding(0, 0, 0, 0);
                viewHolder.mBinding.txtReply.setVisibility(View.VISIBLE);
                viewHolder.mBinding.linearEditDelete.setVisibility(isMyProfile ? View.VISIBLE : View.GONE);
            } else {
                if (isMyProfile) {
                    viewHolder.mBinding.txtReply.setVisibility(View.INVISIBLE);
                    viewHolder.mBinding.linearEditDelete.setVisibility(View.GONE);
                    viewHolder.mBinding.tvTitle.setText(getValue("reply"));
                    viewHolder.mBinding.layoutReview.setBackgroundColor(ContextCompat.getColor(activity, R.color.brand_pink));
                    viewHolder.mBinding.tvTitle.setPadding(10, 1, 10, 1);
                }
                else {
                    viewHolder.mBinding.replyLinear.setVisibility(View.GONE);
                }
            }

            viewHolder.mBinding.layoutReview.setOnClickListener(v -> {
                Utils.preventDoubleClick(v);
                if (model.getReply().isEmpty()) {
                    ReviewReplayDialog dialog = new ReviewReplayDialog();
                    dialog.replayId = model.getId();
                    dialog.isPromoter = false;
                    dialog.show(getSupportFragmentManager(), "");
                }
            });

            viewHolder.mBinding.ivEdit.setOnClickListener(v -> {
                Utils.preventDoubleClick(v);
                ReviewReplayDialog dialog = new ReviewReplayDialog();
                dialog.replayId = model.getId();
                dialog.isPromoter = false;
                dialog.replay = model.getReply();
                dialog.show(getSupportFragmentManager(), "");
            });

            viewHolder.mBinding.ivDelete.setOnClickListener(v -> {
                Utils.preventDoubleClick(v);
                Graphics.showAlertDialogWithOkCancel(activity, getString(R.string.app_name), getValue("are_you_sure_delete_review"),
                        getValue("yes_delete"), getValue("cancel"), aBoolean -> {
                            if (aBoolean) {
                                requestDeleteReview(model.getId());
                            }
                        });
            });

            viewHolder.mBinding.iconMenu.setOnClickListener(v -> {
                Optional<ContactListModel> contactListModel = detailModel.stream().
                        filter(p -> p.getId().equals(model.getUserId())).findFirst();

                if (contactListModel.isPresent() && contactListModel.get().getId().equals(SessionManager.shared.getUser().getId())) {
                    openDeleteActionSheet(model.getId());
                    return;
                }
                ReportAndBlockBottomSheet bottomSheet = new ReportAndBlockBottomSheet();
                bottomSheet.reportSheetCallBack = data -> {
                    if (data) {
                        ReportBottomSheet reportBottomSheet = new ReportBottomSheet();
                        reportBottomSheet.ratingModel = model;
                        reportBottomSheet.isFromChat = false;
                        reportBottomSheet.isOnlyReport = true;
                        reportBottomSheet.callback = data1 -> {
                            ReportedUseSuccessDialog dialog = new ReportedUseSuccessDialog();
                            dialog.callBack = data2 -> {
                                if (data2) {
                                    if (AppSettingManager.shared.venueReloadCallBack != null) {
                                        AppSettingManager.shared.venueReloadCallBack.onReceive(true);
                                    }
                                    finish();
                                }
                            };
                            dialog.show(getSupportFragmentManager(), "");

                        };
                        reportBottomSheet.show(getSupportFragmentManager(), "");
                    }
                };
                bottomSheet.reportAndBlockCallBack = data -> {
                    if (data) {
                        ReportBottomSheet reportBottomSheet = new ReportBottomSheet();
                        reportBottomSheet.ratingModel = model;
                        reportBottomSheet.isFromChat = false;
                        reportBottomSheet.callback = data1 -> {
                            contactListModel.ifPresent(listModel -> requestBlockUserAdd(listModel.getId(), viewHolder.mBinding.txtTitle.getText().toString()));
                        };
                        reportBottomSheet.show(getSupportFragmentManager(), "");
                    }
                };
                bottomSheet.callback = data -> {
                    if (data) {
                        Graphics.showAlertDialogWithOkCancel(activity, activity.getString(R.string.app_name), Utils.setLangValue("block_user_alert",viewHolder.mBinding.txtTitle.getText().toString()), Utils.getLangValue("yes"), Utils.getLangValue("no"), isConfirmed -> {
                            if (isConfirmed) {
                                contactListModel.ifPresent(listModel -> requestBlockUserAdd(listModel.getId(), viewHolder.mBinding.txtTitle.getText().toString()));
                            }
                        });
                    }
                };
                bottomSheet.show(getSupportFragmentManager(), "");
            });

            if (type.equals("ticket")){
                viewHolder.mBinding.txtReview.setMaxLines(Integer.MAX_VALUE);
                viewHolder.mBinding.txtReview.setSingleLine(false);

            }
        }

        private void requestDeleteReview(String replayId) {
            Graphics.showProgress(activity);
            DataService.shared(activity).requestDeleteReview(replayId, new RestCallback<ContainerModel<ReviewReplayModel>>(null) {
                @Override
                public void result(ContainerModel<ReviewReplayModel> model, String error) {
                    Graphics.hideProgress(activity);
                    if (!Utils.isNullOrEmpty(error) || model == null) {
                        Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    EventBus.getDefault().post(new MessageEvent());
                }
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