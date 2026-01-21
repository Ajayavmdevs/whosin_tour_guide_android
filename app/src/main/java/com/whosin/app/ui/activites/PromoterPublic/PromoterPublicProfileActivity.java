package com.whosin.app.ui.activites.PromoterPublic;

import static com.whosin.app.comman.Graphics.context;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import com.google.gson.Gson;
import com.king.image.imageviewer.ImageViewer;
import com.king.image.imageviewer.loader.GlideImageLoader;
import com.tapadoo.alerter.Alerter;
import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.ActivityPromoterPublicProfileBinding;
import com.whosin.app.databinding.ItemRatingReviewRecyclerBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.ChatModel;
import com.whosin.app.service.models.ContactListModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.CurrentUserRatingModel;
import com.whosin.app.service.models.FollowUnfollowModel;
import com.whosin.app.service.models.MessageEvent;
import com.whosin.app.service.models.PromoterAddRingModel;
import com.whosin.app.service.models.PromoterEventModel;
import com.whosin.app.service.models.PromoterProfileModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.Profile.OtherUserProfileActivity;
import com.whosin.app.ui.activites.Profile.ProfileFullScreenImageActivity;
import com.whosin.app.ui.activites.comman.BaseActivity;
import com.whosin.app.ui.activites.home.Chat.ChatMessageActivity;
import com.whosin.app.ui.activites.home.activity.SeeAllRatingReviewActivity;
import com.whosin.app.ui.activites.home.activity.WriteReviewActivity;
import com.whosin.app.ui.adapter.CmEventListAdapter;
import com.whosin.app.ui.fragment.reviewSheet.UserFullReviewSheet;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.imaginativeworld.whynotimagecarousel.listener.CarouselListener;
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PromoterPublicProfileActivity extends BaseActivity {
    private ActivityPromoterPublicProfileBinding binding;
    public PromoterProfileModel promoterProfileModel;
    private boolean isPromoterProfilePublic = false;
    private boolean isHeaderVisible = false;
    private int lastScrollY = 0;
    private final RatingReviewAdapter<CurrentUserRatingModel> ratingReviewAdapter = new RatingReviewAdapter<>();
    private String id = "";
    private CmEventListAdapter<PromoterEventModel> eventlistdapter;

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------
    @Override
    protected void initUi() {

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        isPromoterProfilePublic = getIntent().getBooleanExtra("isPromoterProfilePublic", false);
        boolean isFromOtherUserProfile = getIntent().getBooleanExtra("isFromOtherUserProfile", false);

        id = getIntent().getStringExtra("id");
        requestPromoterPublicProfile(id);


        if (isFromOtherUserProfile) {
            binding.profileswitchBtn.setVisibility(View.VISIBLE);
        }

        binding.rating.setOnTouchListener((v, event) -> true);

    }


    @Override
    protected void setListeners() {

        binding.ivClose.setOnClickListener(view -> {
            onBackPressed();
        });

        binding.joinMyRingConstraint.setOnClickListener(view -> {
            if (promoterProfileModel == null) {
                return;
            }
            if (promoterProfileModel.getProfile() == null) {
                return;
            }
            Utils.preventDoubleClick(view);
            if (binding.tvJoinRing.getText().equals("waiting for approval")) {
                return;
            }
            if (binding.tvJoinRing.getText().equals("Leave Ring")) {
                Graphics.showAlertDialogWithOkCancel(activity, activity.getString(R.string.app_name), "Are you sure want to leave ring?",
                        "Yes", "Cancel", aBoolean -> {
                            if (aBoolean) {
                                requestPromoterLeaveMyRing();
                            }
                        });
            } else {
                requestPromoterJoinMyRing();
            }
        });

        binding.editBtn.setOnClickListener(view -> {
            reqFollowUnFollow();
        });

        binding.profileswitchBtn.setOnClickListener(v -> {
            Utils.preventDoubleClick(v);
            startActivity(new Intent(activity, OtherUserProfileActivity.class).putExtra("friendId", promoterProfileModel.getProfile().getUserId()));
            finish();
        });

        binding.linearReview.setOnClickListener(view -> {
            Utils.preventDoubleClick(view);
            WriteReviewActivity bottomSheet = new WriteReviewActivity(promoterProfileModel.getProfile().getUserId(), promoterProfileModel.getReviewModel().getCurrentUserRating(), "promoter");
            bottomSheet.activity = activity;
            bottomSheet.show(getSupportFragmentManager(), "1");
        });

        binding.tvSeeAll.setOnClickListener(view -> startActivity(new Intent(activity, SeeAllRatingReviewActivity.class)
                .putExtra("id", promoterProfileModel.getProfile().getUserId())
                .putExtra("type", "promoter")
                .putExtra("currentUserRating", new Gson().toJson(promoterProfileModel.getReviewModel().getCurrentUserRating()))));


        binding.scroll.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            int dy = scrollY - lastScrollY;

            if (dy > 5 && !isHeaderVisible) {
                binding.headerLayout.setVisibility(View.VISIBLE);
                binding.blurView.setBlurEnabled(true);
                Graphics.applyBlurEffect(activity, binding.blurView);
                isHeaderVisible = true;
                if (promoterProfileModel != null) {
                    String name = promoterProfileModel.getProfile().getFirstName() + " " + promoterProfileModel.getProfile().getLastName();
                    Graphics.loadImageWithFirstLetter(promoterProfileModel.getProfile().getImage(), binding.headerIv, name);
                    binding.headertitle.setText(name);
                }

            } else if (dy < -5 && isHeaderVisible) {
                binding.blurView.setBackgroundColor(Color.TRANSPARENT);
                binding.blurView.setBackground(null);
                binding.blurView.setBlurEnabled(false);
                binding.headerLayout.setVisibility(View.GONE);
                isHeaderVisible = false;
            }

            if (scrollY == 0 && isHeaderVisible) {
                binding.headerLayout.setVisibility(View.GONE);
                binding.blurView.setBlurEnabled(false);
                binding.blurView.setBackgroundColor(Color.TRANSPARENT);
                binding.blurView.setBackground(null);
                isHeaderVisible = false;
            }

            lastScrollY = scrollY;
        });


        binding.chatLayoutButton.setOnClickListener(v -> {
            if (promoterProfileModel.getProfile() == null) {
                return;
            }
            Utils.preventDoubleClick(v);
            UserDetailModel userDetailModel = new UserDetailModel();
            userDetailModel.setId(promoterProfileModel.getProfile().getUserId());
            userDetailModel.setFirstName(promoterProfileModel.getProfile().getFirstName());
            userDetailModel.setLastName(promoterProfileModel.getProfile().getLastName());
            userDetailModel.setImage(promoterProfileModel.getProfile().getImage());
            ChatModel chatModel = new ChatModel(userDetailModel);
            Intent intent = new Intent(activity, ChatMessageActivity.class);
            intent.putExtra("chatModel", new Gson().toJson(chatModel));
            activity.startActivity(intent);
        });

        binding.imageCarousel.setCarouselListener(new CarouselListener() {
            @Nullable
            @Override
            public ViewBinding onCreateViewHolder(@NonNull LayoutInflater layoutInflater, @NonNull ViewGroup viewGroup) {
                return null;
            }

            @Override
            public void onBindViewHolder(@NonNull ViewBinding viewBinding, @NonNull CarouselItem carouselItem, int i) {

            }

            @Override
            public void onClick(int i, @NonNull CarouselItem carouselItem) {
                ImageViewer.load(promoterProfileModel.getProfile().getImages())
                        .selection(i)
                        .imageLoader(new GlideImageLoader())
                        .indicator(true)
                        .start(activity);
            }

            @Override
            public void onLongClick(int i, @NonNull CarouselItem carouselItem) {

            }
        });

        binding.imageProfile.setOnClickListener(v -> {
            Utils.preventDoubleClick(v);
            Intent intent = new Intent( activity, ProfileFullScreenImageActivity.class );
            intent.putExtra( ProfileFullScreenImageActivity.EXTRA_IMAGE_URL, promoterProfileModel.getProfile().getImage());
            startActivity( intent );
        });


    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityPromoterPublicProfileBinding.inflate(getLayoutInflater());
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
        if (!TextUtils.isEmpty(id)) {
            requestPromoterPublicProfile(id);
        }

    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------


    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------


    @SuppressLint("SetTextI18n")
    private void setUpData() {

        if (promoterProfileModel != null && promoterProfileModel.getProfile() != null) {
            String name = promoterProfileModel.getProfile().getFirstName() + " " + promoterProfileModel.getProfile().getLastName();
            Graphics.loadImageWithFirstLetter(promoterProfileModel.getProfile().getImage(), binding.imageProfile, name);
            Graphics.loadImageWithFirstLetter(promoterProfileModel.getProfile().getImage(), binding.headerIv, name);
            binding.tvUserName.setText(name);
            binding.tvBio.setText(promoterProfileModel.getProfile().getBio());
            binding.headertitle.setText(name);
        }

        binding.ratingReviewRecycler.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        binding.ratingReviewRecycler.setAdapter(ratingReviewAdapter);

        List<CurrentUserRatingModel> filteredReviews = promoterProfileModel.getReviewModel().getReviews().stream().filter(review -> promoterProfileModel.getReviewModel().getUsers().stream().anyMatch(user -> user.getId().equals(review.getUserId()))).collect(Collectors.toList());

        binding.rating.setOnRatingChangeListener(null);

        binding.rating.setRating(promoterProfileModel.getReviewModel().getAvgRating());

        eventlistdapter = new CmEventListAdapter<>(activity, "eventList");
        binding.eventListRecycleView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        binding.eventListRecycleView.setAdapter(eventlistdapter);
        if (promoterProfileModel.getEvents() != null && !promoterProfileModel.getEvents().isEmpty()) {
            binding.emptyPlaceHolderView.setVisibility(View.GONE);
            binding.eventListRecycleView.setVisibility(View.VISIBLE);
            eventlistdapter.updateData(promoterProfileModel.getEvents());
        } else {
            binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
            binding.eventListRecycleView.setVisibility(View.GONE);
        }

        if (!filteredReviews.isEmpty()) {
            ratingReviewAdapter.updateData(filteredReviews);
            List<CurrentUserRatingModel> matchingList = filteredReviews.stream()
                    .filter(ratingModel -> SessionManager.shared.getUser().getId().equals(ratingModel.getUserId()))
                    .collect(Collectors.toList());
            if (!matchingList.isEmpty()) {
                binding.txtReviewTitle.setText("Edit Review");
            }
        }

        binding.customRing.ringMemberCout = promoterProfileModel.getRings().getCount();
        binding.customRing.setUpData(promoterProfileModel.getRings().getList(), activity, getSupportFragmentManager(), isPromoterProfilePublic);

        if (promoterProfileModel.getProfile().getRingMember().equals("accepted")) {
            binding.tvJoinRing.setText("Leave Ring");
        } else if (promoterProfileModel.getProfile().getRingMember().equals("pending")) {
            binding.tvJoinRing.setText("waiting for approval");
        } else {
            binding.tvJoinRing.setText("Join My Ring");
        }
        binding.tvFollow.setText(Utils.followButtonTitle(promoterProfileModel.getProfile().getFollow()));

        setBanner();

    }

    private void setBanner() {
        if (promoterProfileModel == null) {
            return;
        }

        List<CarouselItem> carouselItems = new ArrayList<>();

        if (!promoterProfileModel.getProfile().getImages().isEmpty()) {
            for (String imageLink : promoterProfileModel.getProfile().getImages()) {
                if (!Utils.isNullOrEmpty(imageLink)) {
                    carouselItems.add(new CarouselItem(imageLink, "Static Banner Title"));
                }
            }
        }

        binding.imageCarousel.setVisibility(View.VISIBLE);
        binding.imageCarousel.registerLifecycle(getLifecycle());
        binding.imageCarousel.setData(carouselItems);


    }

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestPromoterJoinMyRing() {
        showProgress();
        DataService.shared(activity).requestPromoterJoinMyRing(promoterProfileModel.getProfile().getUserId(), new RestCallback<ContainerModel<PromoterAddRingModel>>(this) {
            @Override
            public void result(ContainerModel<PromoterAddRingModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(activity, model.message, Toast.LENGTH_SHORT).show();
                binding.tvJoinRing.setText("Leave Ring");
                requestPromoterPublicProfile(id);
            }
        });
    }

    private void requestPromoterLeaveMyRing() {
        showProgress();
        DataService.shared(activity).requestPromoterLeaveMyRing(promoterProfileModel.getProfile().getUserId(), new RestCallback<ContainerModel<PromoterAddRingModel>>(this) {
            @Override
            public void result(ContainerModel<PromoterAddRingModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(activity, model.message, Toast.LENGTH_SHORT).show();
                binding.tvJoinRing.setText("Join My Ring");
                requestPromoterPublicProfile(id);
            }
        });
    }

    public void requestPromoterPublicProfile(String id) {
        showProgress();
        DataService.shared(activity).requestGetProfile(id, new RestCallback<ContainerModel<PromoterProfileModel>>(this) {
            @Override
            public void result(ContainerModel<PromoterProfileModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (model.getData() != null) {
                    promoterProfileModel = model.getData();
                    binding.scroll.setVisibility(View.VISIBLE);
                    setUpData();
                }
            }
        });
    }

    private void reqFollowUnFollow() {
        DataService.shared(activity).requestUserFollowUnFollow(promoterProfileModel.getProfile().getUserId(), new RestCallback<ContainerModel<FollowUnfollowModel>>(this) {
            @Override
            public void result(ContainerModel<FollowUnfollowModel> model, String error) {
                if (!Utils.isNullOrEmpty(error) || model == null || model.getData() == null) {
                    Toast.makeText(activity, R.string.service_message_something_wrong, Toast.LENGTH_SHORT).show();
                    return;
                }
                promoterProfileModel.getProfile().setFollow(model.getData().getStatus());
                binding.tvFollow.setText(Utils.followButtonTitle(promoterProfileModel.getProfile().getFollow()));

                switch (model.getData().getStatus()) {
                    case "unfollowed":
                        Alerter.create(activity).setTitle("Oh Snap!").setTitleAppearance(R.style.AlerterTitle).setTextAppearance(R.style.AlerterText).setText("You have unfollowed " + promoterProfileModel.getProfile().getFullName()).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                        break;
                    case "approved":
                        Alerter.create(activity).setTitle("Thank you!").setText("For following " + promoterProfileModel.getProfile().getFullName()).setTextAppearance(R.style.AlerterText).setTitleAppearance(R.style.AlerterTitle).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                        break;
                    case "pending":
                        Alerter.create(activity).setTitle("Thank you!").setText("You have requested for follow " + promoterProfileModel.getProfile().getFullName()).setTextAppearance(R.style.AlerterText).setTitleAppearance(R.style.AlerterTitle).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                        break;
                    case "cancelled":
                        Alerter.create(activity).setTitle("Oh Snap!").setText("You have cancelled follow request of " + promoterProfileModel.getProfile().getFullName()).setTextAppearance(R.style.AlerterText).setTitleAppearance(R.style.AlerterTitle).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                        break;
                }

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
                params.width = (int) (Graphics.getScreenWidth(context) * 0.90);
            }
            view.setLayoutParams(params);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            CurrentUserRatingModel model = (CurrentUserRatingModel) getItem(position);
            if (model == null) {
                return;
            }
            boolean isLastItem = position == getItemCount() - 1;

            viewHolder.mBinding.txtReview.setText(model.getReview());
            viewHolder.mBinding.txtReply.setVisibility(View.VISIBLE);
            viewHolder.mBinding.txtReply.setText(model.getReply());
            Graphics.loadImageWithFirstLetter(promoterProfileModel.getProfile().getImage(), viewHolder.mBinding.image, promoterProfileModel.getProfile().getFullName());
            viewHolder.mBinding.tvTitle.setText(promoterProfileModel.getProfile().getFullName());
            viewHolder.mBinding.layoutReview.setBackgroundColor(Color.TRANSPARENT);
            viewHolder.mBinding.tvTitle.setPadding(0, 0, 0, 0);

            if (model.getReply() != null && !model.getReply().trim().isEmpty()) {
                viewHolder.mBinding.replyLinear.setVisibility(View.VISIBLE);
            } else {
                viewHolder.mBinding.replyLinear.setVisibility(View.INVISIBLE);
            }

            viewHolder.mBinding.txtDate.setText(Utils.convertMainDateFormatReview(model.getCreatedAt()));
            viewHolder.mBinding.rating.setRating(model.getStars());

            Optional<ContactListModel> modelOptional = promoterProfileModel.getReviewModel().getUsers().stream().filter(p -> p.getId().equals(model.getUserId())).findFirst();
            if (modelOptional.isPresent()) {
                Graphics.loadImageWithFirstLetter(modelOptional.get().getImage(), viewHolder.mBinding.ivRating, modelOptional.get().getFirstName());
                viewHolder.mBinding.txtTitle.setText(modelOptional.get().getFullName());
            }

            if (getItemCount() > 1) {
                if (isLastItem) {
                    int marginBottom = Utils.getMarginRight(holder.itemView.getContext(), 0.04f);
                    Utils.setRightMargin(holder.itemView, marginBottom);
                } else {
                    Utils.setRightMargin(holder.itemView, 0);
                }
            }

            viewHolder.mBinding.getRoot().setOnClickListener(v -> {
                UserFullReviewSheet reviewSheet = new UserFullReviewSheet();
                modelOptional.ifPresent(userDetailModel -> reviewSheet.contactListModel = userDetailModel);
                reviewSheet.currentUserRatingModel = model;
                reviewSheet.callback = data -> {
                    if (data) requestPromoterPublicProfile(id);
                };
                reviewSheet.show(getSupportFragmentManager(),"");
            });
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final ItemRatingReviewRecyclerBinding mBinding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemRatingReviewRecyclerBinding.bind(itemView);
            }
        }
    }


    // endregion
    // --------------------------------------
}