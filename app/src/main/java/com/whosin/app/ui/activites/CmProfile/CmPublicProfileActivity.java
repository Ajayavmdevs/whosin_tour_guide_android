package com.whosin.app.ui.activites.CmProfile;

import static com.whosin.app.comman.Graphics.context;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
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
import com.tapadoo.alerter.Alert;
import com.tapadoo.alerter.Alerter;
import com.whosin.app.R;
import com.whosin.app.comman.CountryCode;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.ActivityCmPublicProfileBinding;
import com.whosin.app.databinding.ItemAdminPromoterRingForcmDesignBinding;
import com.whosin.app.databinding.ItemRatingReviewRecyclerBinding;
import com.whosin.app.databinding.ItemSocialAccountAddDesignBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.CheckUserSession;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.ChatModel;
import com.whosin.app.service.models.ComplimentaryProfileModel;
import com.whosin.app.service.models.ContactListModel;
import com.whosin.app.service.models.ContainerListModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.CurrentUserRatingModel;
import com.whosin.app.service.models.InvitedUserModel;
import com.whosin.app.service.models.MessageEvent;
import com.whosin.app.service.models.PromoterAddRingModel;
import com.whosin.app.service.models.PromoterCirclesModel;
import com.whosin.app.service.models.SocialAccountsToMentionModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.Profile.OtherUserProfileActivity;
import com.whosin.app.ui.activites.Profile.ProfileFullScreenImageActivity;
import com.whosin.app.ui.activites.comman.BaseActivity;
import com.whosin.app.ui.activites.home.Chat.ChatMessageActivity;
import com.whosin.app.ui.activites.home.activity.SeeAllRatingReviewActivity;
import com.whosin.app.ui.activites.home.activity.WriteReviewActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.imaginativeworld.whynotimagecarousel.listener.CarouselListener;
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class CmPublicProfileActivity extends BaseActivity {
    private ActivityCmPublicProfileBinding binding;
    public ComplimentaryProfileModel complimentaryProfileModel;
    private List<SocialAccountsToMentionModel> socialAccountList = new ArrayList<>();
    private final SocialItemListAdapter<SocialAccountsToMentionModel> adapter = new SocialItemListAdapter<>();
    private final CirclesListAdapter<InvitedUserModel> circlesListAdapter = new CirclesListAdapter<>();
    private final RatingReviewAdapter<CurrentUserRatingModel> ratingReviewAdapter = new RatingReviewAdapter<>();
    private boolean isHeaderVisible = false;
    private int lastScrollY = 0;
    private String promoterId = "";


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    protected void initUi() {

        binding.tvSwitchToPersonalTitle.setText(getValue("switch_to_personal"));
        binding.promoterText.setText(getValue("complimentary"));
        binding.tvChatTitle.setText(getValue("chat"));
        binding.tvCircle.setText(getValue("add_to_circle"));
        binding.tvName.setText(getValue("profile_score"));
        binding.tvRate.setText(getValue("rating_and_reviews"));
        binding.txtReviewTitle.setText(getValue("write_review"));
        binding.tvSeeAll.setText(getValue("see_all"));
        binding.tvTitleSocial.setText(getValue("social_accounts"));

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        boolean isFromSubAdmin = getIntent().getBooleanExtra("isFromSubAdmin",false);
        if (isFromSubAdmin){
            binding.editProfileLinear.setVisibility(View.GONE);
        }


        binding.addSocialItemRecycleview.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        binding.addSocialItemRecycleview.setAdapter(adapter);

        binding.circleListRecycleView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        binding.circleListRecycleView.setAdapter(circlesListAdapter);

        promoterId = getIntent().getStringExtra("promoterUserId");
        boolean isFromOtherUserProfile = getIntent().getBooleanExtra("isFromOtherUserProfile", false);

        if (isFromOtherUserProfile){
            binding.profileswitchBtn.setVisibility(View.VISIBLE);
        }

        if (!Utils.isNullOrEmpty(promoterId)){
            CheckUserSession.checkSessionAndProceed(activity, () -> requestGetProfile(promoterId));
        }

        binding.rating.setOnTouchListener((v, event) -> true);


    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void setListeners() {

        binding.scrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            int dy = scrollY - lastScrollY;

            if (dy > 5 && !isHeaderVisible) {
                binding.headerLayout.setVisibility(View.VISIBLE);
                binding.blurView.setBlurEnabled(true);
                Graphics.applyBlurEffect( activity, binding.blurView );
                isHeaderVisible = true;
                if (complimentaryProfileModel != null && complimentaryProfileModel.getProfile() != null){
                    String name = complimentaryProfileModel.getProfile().getFirstName() + " " + complimentaryProfileModel.getProfile().getLastName();
                    Graphics.loadImageWithFirstLetter(complimentaryProfileModel.getProfile().getImage(), binding.headerIv, name);
                    binding.headertitle.setText(name);
                }
            } else if (dy < -5 && isHeaderVisible) {
                binding.blurView.setBackgroundColor(Color.TRANSPARENT);
                binding.blurView.setBackground(null);
                binding.blurView.setBlurEnabled(false);
                binding.headerLayout.setVisibility(View.GONE);
                isHeaderVisible = false;
            }

            lastScrollY = scrollY;
        });


        binding.constraintAddToRing.setOnClickListener(view -> {
            if (binding.tvRingStatus.getText().equals(getValue("waiting_for_approval"))) {
                return;
            }
            if (binding.tvRingStatus.getText().equals(getValue("remove_from_ring"))) {
                Graphics.showAlertDialogWithOkCancel(activity, activity.getString(R.string.app_name), getValue("remove_from_ring_alert"),
                        getValue("yes"), getValue("cancel"), aBoolean -> {
                            if (aBoolean) {
                                requestPromoterMyRingRemoveMember(complimentaryProfileModel.getProfile().getUserId());
                            }
                        });
            } else {
                requestPromoterAddToRing(complimentaryProfileModel.getProfile().getUserId());
            }
        });

        binding.ivClose.setOnClickListener(v -> {
            finish();
        });

        binding.linearReview.setOnClickListener( view -> {
            Utils.preventDoubleClick( view );
            WriteReviewActivity bottomSheet = new WriteReviewActivity( complimentaryProfileModel.getProfile().getUserId(), complimentaryProfileModel.getReview().getCurrentUserRating(), "complimentary" );
            bottomSheet.activity = activity;
            bottomSheet.show( getSupportFragmentManager(), "1" );
        } );

        binding.tvSeeAll.setOnClickListener( view -> startActivity( new Intent( activity, SeeAllRatingReviewActivity.class )
                .putExtra( "id", complimentaryProfileModel.getProfile().getUserId() )
                .putExtra( "type", "complimentary" )
                .putExtra( "currentUserRating", new Gson().toJson( complimentaryProfileModel.getReview().getCurrentUserRating() ) ) ) );

        binding.rating.setOnRatingChangeListener( null );
//        binding.rating.setOnRatingChangeListener( (ratingBar, rating) -> reqAddRatings( (int) rating ) );


        binding.constraintChat.setOnClickListener(view -> {
            if (complimentaryProfileModel == null) {
                return;
            }
            if (complimentaryProfileModel.getProfile() == null) {
                return;
            }
            UserDetailModel model1 = new UserDetailModel();
            model1.setId(complimentaryProfileModel.getProfile().getUserId());
            model1.setFirstName(complimentaryProfileModel.getProfile().getFirstName());
            model1.setLastName(complimentaryProfileModel.getProfile().getLastName());
            model1.setImage(complimentaryProfileModel.getProfile().getImage());
            ChatModel chatModel = new ChatModel(model1);
            Intent intent = new Intent(activity, ChatMessageActivity.class);
            intent.putExtra("chatModel", new Gson().toJson(chatModel));
            startActivity(intent);
        });

        binding.profileswitchBtn.setOnClickListener(v -> {
            Utils.preventDoubleClick(v);
            startActivity(new Intent(activity, OtherUserProfileActivity.class).putExtra("friendId", promoterId));
            finish();
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
                ImageViewer.load(complimentaryProfileModel.getProfile().getImages())
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
            intent.putExtra( ProfileFullScreenImageActivity.EXTRA_IMAGE_URL, complimentaryProfileModel.getProfile().getImage());
            startActivity( intent );
        });

        binding.constraintAddToCircle.setOnClickListener(view -> {
            Utils.preventDoubleClick(view);
            AddToCircleBottomSheet addToCircleBottomSheet = new AddToCircleBottomSheet();
            addToCircleBottomSheet.userId = complimentaryProfileModel.getProfile().getUserId();
            addToCircleBottomSheet.listener = data -> {
                EventBus.getDefault().post(new PromoterCirclesModel());
                if (data.equals("true")){
                    String message = getValue("user_added_in_circle");
                    Alerter.create(activity).setTitle(message).setTitleAppearance(R.style.AlerterTitle).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                    CheckUserSession.checkSessionAndProceed(activity, () -> requestGetProfile(promoterId));
                }

            };
            if (!findCircle().isEmpty()) {
                addToCircleBottomSheet.alreadyAddedCircleIds = findCircle();
            }
            addToCircleBottomSheet.show(getSupportFragmentManager(), "");
        });


    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityCmPublicProfileBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register( this );
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister( this );
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        CheckUserSession.checkSessionAndProceed(activity, () -> requestGetProfile(promoterId));
    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void setUpData(ComplimentaryProfileModel data) {
        if (data == null){return;}
        if (data.getProfile() == null){return;}

        if (data.getProfile().getImage() != null) {
            String name = data.getProfile().getFirstName() + " " + data.getProfile().getLastName();
            Graphics.loadImageWithFirstLetter(data.getProfile().getImage(), binding.imageProfile, name);
        }

        binding.tvUserName.setText(String.format("%s %s", data.getProfile().getFirstName(), data.getProfile().getLastName()));
        binding.tvBio.setText(data.getProfile().getBio());
        binding.tvDescription.setText(data.getProfile().getBio());

        binding.tvCmEmail.setText(data.getProfile().getEmail().trim());
        binding.tvCmGender.setText(data.getProfile().getGender().trim());
        binding.tvCmNationality.setText(data.getProfile().getNationality().trim());

        if (!TextUtils.isEmpty(data.getProfile().getAddress().trim())) {
            binding.tvCmAdress.setText(data.getProfile().getAddress().trim());
        } else {
            binding.adressLayout.setVisibility(View.GONE);
            binding.adressViewLine.setVisibility(View.GONE);
        }

        if (calculateAge(data.getProfile().getDateOfBirth()) == 0) {
            binding.ageLayout.setVisibility(View.GONE);
        } else {
            binding.ageLayout.setVisibility(View.VISIBLE);
            binding.tvCmAge.setText(calculateAge(data.getProfile().getDateOfBirth()) + " Years");
        }




//        binding.tvNationality.setText(data.getProfile().getAddress());

        binding.countryCode.setCountryPreference(data.getProfile().getCountryCode());

        String countryCode = CountryCode.getCountryCodeByName(data.getProfile().getNationality());
        Log.d("TAG", "setUpData: " + getCountryCodeByName(data.getProfile().getNationality()));
        binding.countryCode.setClickable(false);
        binding.countryCode.setFocusable(false);
        binding.countryCode.setEnabled(false);

        try {
            binding.countryCode.setCountryForNameCode(countryCode);
        } catch (Exception e) {
            e.printStackTrace();
        }

        binding.countryCode.setCountryForNameCode(countryCode);

        List<CarouselItem> carouselItems = new ArrayList<>();

        if (!data.getProfile().getImages().isEmpty()) {
            for (String imageLink : data.getProfile().getImages()) {
                if (!Utils.isNullOrEmpty(imageLink)){
                    carouselItems.add(new CarouselItem(imageLink, "Static Banner Title"));
                }
            }
        }

        if (carouselItems.isEmpty()){
            binding.imageCarousel.setVisibility(View.GONE);
//            binding.backgroundCoverIv.setVisibility(View.VISIBLE);
        }else {
            binding.imageCarousel.setVisibility(View.VISIBLE);
//            binding.backgroundCoverIv.setVisibility(View.GONE);

            binding.imageCarousel.registerLifecycle(getLifecycle());
            binding.imageCarousel.setData(carouselItems);
        }

        if (data.isAdminPromoter() && data.getRings().getList() != null && !data.getRings().getList().isEmpty()){
            binding.circleListRecycleView.setVisibility(View.VISIBLE);
            Optional<UserDetailModel> model = data.getRings().getList().stream().filter(p -> p.getUserId().equals(SessionManager.shared.getUser().getId())).findFirst();
            if (model.isPresent()){
                if (model.get().getCircles() != null && !model.get().getCircles().isEmpty()){
                    circlesListAdapter.updateData(model.get().getCircles());
                }else {
                    binding.circleListRecycleView.setVisibility(View.GONE);
                }
            }else {
                binding.circleListRecycleView.setVisibility(View.GONE);
            }
        }else {
            binding.circleListRecycleView.setVisibility(View.GONE);
        }



        socialAccountList.clear();
        if (!Utils.isNullOrEmpty(data.getProfile().getFacebook())) {
            socialAccountList.add(new SocialAccountsToMentionModel("facebook", data.getProfile().getFacebook()));
        }
        if (!Utils.isNullOrEmpty(data.getProfile().getInstagram())) {
            socialAccountList.add(new SocialAccountsToMentionModel("instagram", data.getProfile().getInstagram()));
        }
        if (!Utils.isNullOrEmpty(data.getProfile().getYoutube())) {
            socialAccountList.add(new SocialAccountsToMentionModel("youtube", data.getProfile().getYoutube()));
        }
        if (!Utils.isNullOrEmpty(data.getProfile().getTiktok())) {
            socialAccountList.add(new SocialAccountsToMentionModel("tiktok", data.getProfile().getTiktok()));
        }



        if (!socialAccountList.isEmpty()){
            adapter.updateData(socialAccountList);
        }else {
            binding.addSocialItemRecycleview.setVisibility(View.GONE);
            binding.tvTitleSocial.setVisibility(View.GONE);
        }

        if (data.getScore() != null){
            binding.profilePunctuality.setUpData( data.getScore().getPunctuality(), activity,getSupportFragmentManager() );
            binding.profileActivity.setUpData( data.getScore().getPunctuality(), activity,getSupportFragmentManager() );
            binding.profileValue.setUpData( data.getScore().getPunctuality(), activity,getSupportFragmentManager() );
        }

        binding.ratingReviewRecycler.setLayoutManager( new LinearLayoutManager( activity, LinearLayoutManager.HORIZONTAL, false ) );
        binding.ratingReviewRecycler.setAdapter( ratingReviewAdapter );

        List<CurrentUserRatingModel> filteredReviews = data.getReview().getReviews().stream().filter( review -> data.getReview().getUsers().stream().anyMatch( user -> user.getId().equals( review.getUserId() ) ) ).collect( Collectors.toList() );
        binding.rating.setRating(data.getReview().getAvgRating());

        if (!filteredReviews.isEmpty()) {
            ratingReviewAdapter.updateData( filteredReviews );
            List<CurrentUserRatingModel> matchingList = filteredReviews.stream()
                    .filter( ratingModel -> SessionManager.shared.getUser().getId().equals( ratingModel.getUserId() ) )
                    .collect( Collectors.toList() );
            if (!matchingList.isEmpty()) {
                binding.txtReviewTitle.setText( getValue("edit_review") );
            }
        }

//        if (!Utils.isNullOrEmpty(data.getProfile().getMyRingStatus())) {
//            String status = data.getProfile().getMyRingStatus();
//            if (status.equals("accepted")) {
//                binding.tvRingStatus.setText("Remove from Ring");
//            } else if (status.equals("pending")) {
//                binding.tvRingStatus.setText("Waiting for approval");
//            } else if (status.equals("none") || status.equals("rejected")) {
//                binding.tvRingStatus.setText("Add to my ring");
//            }
//        }

        if (data.getProfile().getMyRingStatus().equals("accepted") && data.getProfile().getRingPromoterStatus().equals("accepted")){
            binding.constraintAddToCircle.setVisibility(View.VISIBLE);
        }else {
            binding.constraintAddToCircle.setVisibility(View.GONE);
        }

        updateButtonForCMJoinRing(data.getProfile().getMyRingStatus(),data.getProfile().getRingPromoterStatus());
    }

    public static String getCountryCodeByName(String countryName) {
        if (countryName == null || countryName.isEmpty()) {
            return null;
        }

        String countryCode = null;
        String countryNameLowerCase = countryName.toLowerCase();

        for (String iso : Locale.getISOCountries()) {
            Locale locale = new Locale("", iso);
            String name = locale.getDisplayCountry().toLowerCase();
            if (name.equals(countryNameLowerCase)) {
                countryCode = iso;
                break;
            }
        }

        return countryCode;
    }

    private List<String> findCircle() {
        Optional<UserDetailModel> model = complimentaryProfileModel.getRings().getList().stream().filter(p -> p.getUserId().equals(SessionManager.shared.getUser().getId())).findFirst();
        if (model.isPresent()) {
            if (model.get().getCircles() != null && !model.get().getCircles().isEmpty()) {
                return model.get().getCircles().stream().map(InvitedUserModel::getId).filter(Objects::nonNull).collect(Collectors.toList());
            }
        }
        return new ArrayList<>();
    }


    private void updateButtonForCMJoinRing(String status, String promoterStatus) {
        switch (status) {
            case "accepted":
                if ("pending".equals(promoterStatus)) {
                    binding.tvRingStatus.setText(getValue("waiting_for_approval"));
                    binding.constraintChat.setVisibility(View.GONE);
                } else if ("rejected".equals(promoterStatus)) {
                    binding.tvRingStatus.setText(getValue("add_to_my_ring"));
                    binding.constraintChat.setVisibility(View.GONE);
                } else if ("accepted".equals(promoterStatus)) {
                    binding.tvRingStatus.setText(getValue("remove_from_ring"));
                    binding.constraintChat.setVisibility(View.VISIBLE);
                }
                break;

            case "pending":
                if ("accepted".equals(promoterStatus)) {
                    binding.tvRingStatus.setText(getValue("waiting_for_approval"));
                    binding.constraintChat.setVisibility(View.GONE);
                } else {
                    binding.tvRingStatus.setText(getValue("waiting_for_approval"));
                    binding.constraintChat.setVisibility(View.GONE);
                }
                break;

            case "none":
            case "rejected":
                binding.tvRingStatus.setText(getValue("add_to_my_ring"));
                binding.constraintChat.setVisibility(View.GONE);
                break;

            default:
                binding.tvRingStatus.setText(getValue("add_to_my_ring"));
                binding.constraintChat.setVisibility(View.GONE);
                break;
        }
    }

    public int calculateAge(String dateOfBirth) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        try {
            Date dob = sdf.parse(dateOfBirth);
            if (dob == null) return 0;

            Calendar today = Calendar.getInstance();

            Calendar birthDate = Calendar.getInstance();
            birthDate.setTime(dob);

            int age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);

            if (today.get(Calendar.DAY_OF_YEAR) < birthDate.get(Calendar.DAY_OF_YEAR)) {
                age--;
            }

            return age;
        } catch (ParseException e) {
            e.printStackTrace();
            return 0; // Return 0 if parsing fails
        }
    }


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestGetProfile(String id) {
        showProgress();
        DataService.shared(activity).requestComplimentaryPublicProfile(id, new RestCallback<ContainerModel<ComplimentaryProfileModel>>(this) {
            @Override
            public void result(ContainerModel<ComplimentaryProfileModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (model.getData() != null) {
                    binding.scrollView.setVisibility(View.VISIBLE);
                    complimentaryProfileModel = model.getData();
                    setUpData(model.getData());
                }else {
                    binding.scrollView.setVisibility(View.GONE);
                }
            }
        });
    }

    private void requestPromoterAddToRing(String memberId) {
        showProgress();
        DataService.shared(activity).requestPromoterAddToRing(memberId, new RestCallback<ContainerModel<PromoterAddRingModel>>(this) {
            @Override
            public void result(ContainerModel<PromoterAddRingModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (model.getData() != null){
                    if (!TextUtils.isEmpty(model.getData().getPrmoterStatus()) && model.getData().getPrmoterStatus().equals("accepted") && model.getData().getMemberStatus().equals("accepted")){
                        binding.tvRingStatus.setText(getValue("remove_from_ring"));
                        binding.constraintChat.setVisibility(View.VISIBLE);

                    }else {
                        binding.tvRingStatus.setText(getValue("waiting_for_approval"));
                        binding.constraintChat.setVisibility(View.GONE);
                    }

                    EventBus.getDefault().post(new PromoterCirclesModel());
                }
            }
        });
    }

    public void requestPromoterMyRingRemoveMember(String id) {
        Graphics.showProgress(activity);
        DataService.shared(activity).requestPromoterMyRingRemoveMember(id, new RestCallback<ContainerListModel<UserDetailModel>>(this) {
            @Override
            public void result(ContainerListModel<UserDetailModel> model, String error) {
                Graphics.hideProgress(activity);
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (model.status == 1) {
                    binding.tvRingStatus.setText(getValue("add_to_my_ring"));
                    binding.constraintChat.setVisibility(View.GONE);
                    EventBus.getDefault().post(new PromoterCirclesModel());
                }
            }
        });
    }

    private void requestPromoterCircleAddMember(String id, List<String> selectedUserId) {
        DataService.shared(activity).requestPromoterCircleAddMember(id, selectedUserId, new RestCallback<ContainerModel<PromoterCirclesModel>>() {
            @Override
            public void result(ContainerModel<PromoterCirclesModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (model.getData() != null) {
                    Toast.makeText(CmPublicProfileActivity.this, model.message, Toast.LENGTH_SHORT).show();
                    EventBus.getDefault().post(new PromoterCirclesModel());
                    EventBus.getDefault().post(new UserDetailModel());
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
            View view = UiUtils.getViewBy( parent, R.layout.item_rating_review_recycler );
            ViewGroup.LayoutParams params = view.getLayoutParams();
            int itemCount = getItemCount();
            if (itemCount > 1) {
                params.width = (int) (Graphics.getScreenWidth( context ) * 0.85);
            } else {
                params.width = (int) (Graphics.getScreenWidth( context ) * 0.90);
            }
            view.setLayoutParams( params );
            return new ViewHolder( view );
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            CurrentUserRatingModel model = (CurrentUserRatingModel) getItem( position );
            boolean isLastItem = position == getItemCount() - 1;

            viewHolder.mBinding.txtReview.setText( model.getReview() );
            viewHolder.mBinding.txtReply.setVisibility( View.VISIBLE );
            viewHolder.mBinding.txtReply.setText( model.getReply() );
            Graphics.loadImageWithFirstLetter(complimentaryProfileModel.getProfile().getImage(),viewHolder.mBinding.image,complimentaryProfileModel.getProfile().getFullName());
            viewHolder.mBinding.tvTitle.setText( complimentaryProfileModel.getProfile().getFullName() );
            viewHolder.mBinding.layoutReview.setBackgroundColor(Color.TRANSPARENT);
            viewHolder.mBinding.tvTitle.setPadding(0, 0, 0, 0);

            if (model.getReply() != null && !model.getReply().trim().isEmpty()) {
                viewHolder.mBinding.replyLinear.setVisibility( View.VISIBLE );
            }
            else {
                viewHolder.mBinding.replyLinear.setVisibility( View.INVISIBLE );
            }

            viewHolder.mBinding.txtDate.setText( Utils.convertMainDateFormatReview( model.getCreatedAt() ) );
            viewHolder.mBinding.rating.setRating( model.getStars() );

            Optional<ContactListModel> modelOptional = complimentaryProfileModel.getReview().getUsers().stream().filter(p -> p.getId().equals( model.getUserId() ) ).findFirst();
            if (modelOptional.isPresent()) {
                Graphics.loadImageWithFirstLetter( modelOptional.get().getImage(), viewHolder.mBinding.ivRating, modelOptional.get().getFirstName() );
                viewHolder.mBinding.txtTitle.setText( modelOptional.get().getFullName() );
            }

            if (getItemCount() > 1) {
                if (isLastItem) {
                    int marginBottom = Utils.getMarginRight(holder.itemView.getContext(), 0.04f);
                    Utils.setRightMargin(holder.itemView, marginBottom);
                } else {
                    Utils.setRightMargin(holder.itemView, 0);
                }
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final ItemRatingReviewRecyclerBinding mBinding;

            public ViewHolder(@NonNull View itemView) {
                super( itemView );
                mBinding = ItemRatingReviewRecyclerBinding.bind( itemView );
            }
        }
    }


    private class SocialItemListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_social_account_add_design));
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

            ViewHolder viewHolder = (ViewHolder) holder;
            SocialAccountsToMentionModel model = (SocialAccountsToMentionModel) getItem(position);

            viewHolder.binding.socialEditText.setSingleLine(false);
            viewHolder.binding.socialEditText.setFocusable(false);
            viewHolder.binding.socialEditText.setClickable(true);
            viewHolder.binding.socialEditText.setCursorVisible(false);

            viewHolder.binding.socialEditText.setText(model.getAccount());

            if (!Utils.isNullOrEmpty(model.getPlatform())) {
                Drawable drawable = Utils.getPlatformIcon(model.getPlatform());
                viewHolder.binding.socialEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, null, null, null);
            } else {
                viewHolder.binding.socialEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null);
            }


            viewHolder.binding.socialEditText.setOnClickListener(v -> {
                Utils.preventDoubleClick(v);
                Utils.openSoicalSheet(activity, model.getPlatform(), model.getAccount());
            });

            viewHolder.binding.roundLayout.setOnClickListener(v -> {
                Utils.preventDoubleClick(v);
                Utils.openSoicalSheet(activity, model.getPlatform(), model.getAccount());
            });


        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            private final ItemSocialAccountAddDesignBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemSocialAccountAddDesignBinding.bind(itemView);
            }
        }
    }


    private class CirclesListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_admin_promoter_ring_forcm_design));
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

            ViewHolder viewHolder = (ViewHolder) holder;
            InvitedUserModel model = (InvitedUserModel) getItem(position);
            viewHolder.binding.tvCircleTitle.setText(model.getTitle());
            Graphics.loadImageWithFirstLetter(model.getAvatar(),viewHolder.binding.circleAvatarImage,model.getTitle());

        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            private final ItemAdminPromoterRingForcmDesignBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemAdminPromoterRingForcmDesignBinding.bind(itemView);
            }
        }
    }


    // --------------------------------------
    // endregion
}