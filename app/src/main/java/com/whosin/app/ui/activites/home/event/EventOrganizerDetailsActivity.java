package com.whosin.app.ui.activites.home.event;

import static com.whosin.app.comman.Graphics.context;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.tapadoo.alerter.Alerter;
import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.ActivityEventOrganizerDetailsBinding;
import com.whosin.app.databinding.ItemEventDetailBinding;
import com.whosin.app.databinding.ItemImageSlideRecyclerBinding;
import com.whosin.app.databinding.ItemRatingReviewRecyclerBinding;
import com.whosin.app.databinding.ItemSubVenueBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.BucketEventListModel;
import com.whosin.app.service.models.ContactListModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.CurrentUserRatingModel;
import com.whosin.app.service.models.EventModel;
import com.whosin.app.service.models.EventOrgDateModel;
import com.whosin.app.service.models.FollowUnfollowModel;
import com.whosin.app.service.models.PackageModel;
import com.whosin.app.service.models.VenueObjectModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.comman.BaseActivity;
import com.whosin.app.ui.activites.home.activity.SeeAllRatingReviewActivity;
import com.whosin.app.ui.activites.home.activity.WriteReviewActivity;
import com.whosin.app.ui.activites.venue.Bucket.BucketListBottomSheet;
import com.whosin.app.ui.activites.venue.VenueGalleryActivity;
import com.whosin.app.ui.adapter.OfferPackagesAdapter;
import com.whosin.app.ui.fragment.reviewSheet.UserFullReviewSheet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class EventOrganizerDetailsActivity extends BaseActivity {
    private ActivityEventOrganizerDetailsBinding binding;
    private String orgId = "", type = "", name, webSite, image;
    private EventOrgDateModel orgDateModel;
    private EventImageSlideAdapter imageSlideAdapter = new EventImageSlideAdapter();
    private final RatingReviewAdapter<CurrentUserRatingModel> ratingReviewAdapter = new RatingReviewAdapter<>();
    private final EventOrganizerDetailAdapter<BucketEventListModel> eventAdapter = new EventOrganizerDetailAdapter<>();

    private boolean isFollow;
    List<String> imageList;

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    protected void initUi() {

        applyTranslations();

        orgId = getIntent().getStringExtra("org_id");
        type = getIntent().getStringExtra("type");
        name = getIntent().getStringExtra("name");
        webSite = getIntent().getStringExtra("webSite");
        image = getIntent().getStringExtra("image");

        binding.tvTitle.setText(name);
        binding.tvWebsite.setText(webSite);
        Graphics.loadImageWithFirstLetter(image, binding.iconImg, name);
        Graphics.applyBlurEffect(activity, binding.blurViewHeader);

        // Rating Recyclerview
        binding.ratingReviewRecycler.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        binding.ratingReviewRecycler.setAdapter(ratingReviewAdapter);

        // Event Recyclerview
        binding.eventRecycler.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        binding.eventRecycler.setAdapter(eventAdapter);

        // ImageSlider Recyclerview
        binding.imageSlideRecycler.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        binding.imageSlideRecycler.setAdapter(imageSlideAdapter);

    }

    @Override
    protected void setListeners() {
        binding.ivBack.setOnClickListener(view -> onBackPressed());

        binding.linearReview.setOnClickListener(view -> {
            Utils.preventDoubleClick( view );

            WriteReviewActivity bottomSheet = new WriteReviewActivity(orgDateModel.getId(), orgDateModel.getCurrentUserRating(), "events_organizers");
            bottomSheet.activity = activity;
            bottomSheet.result = data -> {
                requestEventOrganizerDetail();
            };
            bottomSheet.show(getSupportFragmentManager(), "1");
        });

        binding.tvSeeAll.setOnClickListener(view -> startActivity(new Intent(activity,
                SeeAllRatingReviewActivity.class)
                .putExtra("id", orgDateModel.getId())
                .putExtra("type", "events_organizers")
                .putExtra("currentUserRating", new Gson().toJson(orgDateModel.getCurrentUserRating()))));

        binding.tvFollow.setOnClickListener(v -> reqFollowUnFollow());


    }

    @Override
    protected void populateData() {
        requestEventOrganizerDetail();
    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityEventOrganizerDetailsBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }



    @Override
    protected Map<View, String> getTranslationMap() {
        Map<View, String> map = new HashMap<>();
        map.put(binding.tvDescriptionTitle, "description");
        map.put(binding.tvTapToRateTitle, "tap_to_rate");
        map.put(binding.txtReviewTitle, "write_review");
        map.put(binding.tvSeeAll, "see_all");
        return map;
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void getEventOrganizerData(EventOrgDateModel orgDateModel) {

        binding.tvDescription.setText(orgDateModel.getDescription());
        binding.tvDescription.post(() -> {
            int lineCount = binding.tvDescription.getLineCount();
            if (lineCount > 2) {
                Utils.makeTextViewResizable(binding.tvDescription, 3, 3, "..." + getValue("see_more"), true);
            }
        });

        Graphics.loadImage(orgDateModel.getCover(), binding.imageVenue);

        if (orgDateModel.getAvgRatings() != 0) {
            binding.tvRate.setText(String.format("%.1f", orgDateModel.getAvgRatings()));

        } else {
            binding.tvRate.setVisibility(View.GONE);
            binding.ImgRating.setVisibility(View.GONE);
        }

        if (orgDateModel.isIsFollowing()) {
            binding.tvFollow.setText(getValue("following"));
            isFollow = true;
        } else {
            binding.tvFollow.setText(getValue("follow"));
            isFollow = false;
        }

        imageList = new ArrayList<>();
        if (orgDateModel.getGalleries().isEmpty()) {
            imageList.add(orgDateModel.getCover());
        } else {
            imageList.addAll(orgDateModel.getGalleries());
        }

        imageSlideAdapter.updateData(imageList);

        List<CurrentUserRatingModel> list = orgDateModel.getUsers().stream()
                .filter(user -> !orgDateModel.getReviews().isEmpty())
                .flatMap(user -> orgDateModel.getReviews().stream()
                        .filter(review -> review.getUserId().equals(user.getId())))
                .collect(Collectors.toList());


        if (!list.isEmpty()) {
            activity.runOnUiThread(() -> {
                ratingReviewAdapter.updateData(list);
                ratingReviewAdapter.notifyDataSetChanged();
                List<CurrentUserRatingModel> matchingList = list.stream()
                        .filter( ratingModel -> SessionManager.shared.getUser().getId().equals( ratingModel.getUserId() ) )
                        .collect( Collectors.toList() );

                if (!matchingList.isEmpty()) {
                    binding.txtReviewTitle.setText(getValue("edit_review"));
                }
            });
        }

        binding.rating.setOnRatingChangeListener(null);
        binding.rating.setRating(orgDateModel.getCurrentUserRating().getStars());
        binding.rating.setOnRatingChangeListener((ratingBar, rating) -> {
            requestAddRating((int) rating);

        });
    }


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestEventOrganizerDetail() {
        binding.progressBar.setVisibility(View.VISIBLE);
        DataService.shared(activity).requestEventOrganizerDetail(orgId, new RestCallback<ContainerModel<EventOrgDateModel>>(this) {
            @Override
            public void result(ContainerModel<EventOrgDateModel> model, String error) {
                binding.progressBar.setVisibility(View.GONE);
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }

                orgDateModel = model.getData();
                getEventOrganizerData(model.getData());
                binding.constraintMain.setVisibility(View.VISIBLE);
                eventAdapter.updateData(model.getData().getEvents());

            }
        });
    }

    private void requestAddRating(int rating) {
        DataService.shared(activity).requestAddRatings(orgId, rating, type, "", "", new RestCallback<ContainerModel<CurrentUserRatingModel>>(this) {
            @Override
            public void result(ContainerModel<CurrentUserRatingModel> model, String error) {
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(activity, model.message, Toast.LENGTH_SHORT).show();
                requestEventOrganizerDetail();
            }
        });

    }

    private void reqFollowUnFollow() {
        showProgress();
        DataService.shared(activity).requestEventFollow(orgId, new RestCallback<ContainerModel<FollowUnfollowModel>>(this) {
            @Override
            public void result(ContainerModel<FollowUnfollowModel> model, String error) {
        hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, R.string.service_message_something_wrong, Toast.LENGTH_SHORT).show();
                    return;
                }
                model.getData();


                if (isFollow) {
                    binding.tvFollow.setText(getValue("follow"));
                    Alerter.create(activity).setTitle(getValue("oh_snap")).setTitleAppearance(R.style.AlerterTitle)
                            .setTextAppearance(R.style.AlerterText).setText(getValue("unfollow_toast")).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();

                    isFollow = false;

                } else {
                    binding.tvFollow.setText(getValue("following"));
                    Alerter.create(activity).setTitle(getValue("thank_you")).setTextAppearance(R.style.AlerterText).setTitleAppearance(R.style.AlerterTitle).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                    isFollow = true;
                }

            }
        });
    }


    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    public class EventImageSlideAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = UiUtils.getViewBy(parent, R.layout.item_image_slide_recycler);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) view.getLayoutParams();
            layoutParams.rightMargin = view.getContext().getResources().getDimensionPixelSize(com.intuit.sdp.R.dimen._minus10sdp);
            if (viewType == 0) {
                layoutParams.width = view.getContext().getResources().getDimensionPixelSize(com.intuit.sdp.R.dimen._48sdp);
            }
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;

            String image = imageList.get(position);

            Graphics.loadRoundImage(image, viewHolder.mBinding.image);

            viewHolder.mBinding.image.setOnClickListener(v -> {
                startActivity(new Intent(activity, VenueGalleryActivity.class)
                        .putExtra("galleries", new Gson().toJson(imageList)));

            });
        }

        @Override
        public int getItemViewType(int position) {

            if (getItemCount() == position + 1) {
                return 0;
            } else {
                return 1;

            }
        }

        @Override
        public int getItemCount() {
            if (imageList.size() > 6) {
                return 6;
            } else {
                return imageList.size();
            }
        }


        public class ViewHolder extends RecyclerView.ViewHolder {

            private ItemImageSlideRecyclerBinding mBinding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemImageSlideRecyclerBinding.bind(itemView);
            }
        }
    }

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
            viewHolder.mBinding.txtReply.setText(model.getReply());
            viewHolder.mBinding.replyLinear.setVisibility( View.GONE );
            Optional<ContactListModel> venueObjectModel = orgDateModel.getUsers().stream().
                    filter(p -> p.getId().equals(model.getUserId())).findFirst();
            if (venueObjectModel.isPresent()) {
                Graphics.loadImageWithFirstLetter(venueObjectModel.get().getImage(),
                        viewHolder.mBinding.ivRating, venueObjectModel.get().getFirstName());
                viewHolder.mBinding.txtTitle.setText(venueObjectModel.get().getFullName());
            }

            if (model.getReply() != null) {
                viewHolder.mBinding.txtReply.setText(model.getReply());
            } else {
                viewHolder.mBinding.linearReply.setVisibility(View.GONE);
            }

            viewHolder.mBinding.rating.setRating(model.getStars());
            viewHolder.mBinding.rating.setIsIndicator(true);

            viewHolder.mBinding.getRoot().setOnClickListener(v -> {
                UserFullReviewSheet reviewSheet = new UserFullReviewSheet();
                venueObjectModel.ifPresent(userDetailModel -> reviewSheet.contactListModel = userDetailModel);
                reviewSheet.currentUserRatingModel = model;
                reviewSheet.callback = data -> {
                    if (data) requestEventOrganizerDetail();
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

    public class EventOrganizerDetailAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_event_detail));

        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            BucketEventListModel model = (BucketEventListModel) getItem(position);
            viewHolder.binding.tvName.setText(model.getTitle());
            viewHolder.binding.tvDescription.setText(model.getDescription());
            Graphics.loadImage(model.getImage(), viewHolder.binding.ivCover);

            try {
                viewHolder.binding.startTime.setText(Utils.convertMainTimeFormat(model.getReservationTime()));
                viewHolder.binding.eventEndTime.setText(Utils.convertMainTimeFormat(model.getEventTime()));
                viewHolder.binding.evenyDate.setText(new SimpleDateFormat("EEE, dd MMM yyyy", Locale.ENGLISH).format(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH).parse(model.getEventTime())));

            } catch (ParseException e) {
                e.printStackTrace();
            }

            viewHolder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(activity, EventDetailsActivity.class);
                intent.putExtra("eventId", model.getId());
                if (model.getOrg() != null) {
                    intent.putExtra("name", model.getOrg().getName());
                    intent.putExtra("address", model.getOrg().getWebsite());
                    intent.putExtra("image", model.getOrg().getLogo());
                }
                startActivity(intent);

            });

            Graphics.applyBlurEffect(activity, viewHolder.binding.blurView);
            Graphics.applyBlurEffect(activity, viewHolder.binding.blurViewTime);

            if (model.getPackages() != null && !model.getPackages().isEmpty()) {
                viewHolder.binding.btnBuyNow.setVisibility(View.VISIBLE);
                viewHolder.binding.packageRecycler.setVisibility(View.VISIBLE);
                viewHolder.packageAdapter.updateData(model.getPackages());
            } else {
                viewHolder.binding.btnBuyNow.setVisibility(View.GONE);
                viewHolder.binding.packageRecycler.setVisibility(View.GONE);
            }

            if (model.getEventTime() != null) {
                Utils.setTimer(model.getEventTime(), viewHolder.binding.countTimer);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                try {
                    Date givenDate = dateFormat.parse(model.getEventTime());
                    Date currentDate = new Date();
                    if (givenDate.before(currentDate)) {
                        viewHolder.binding.btnBuyNow.setVisibility(View.GONE);
                        viewHolder.binding.layoutExpired.setVisibility(View.VISIBLE);
                        viewHolder.binding.roundBlur.setVisibility(View.INVISIBLE);
                        viewHolder.binding.card.setVisibility(View.INVISIBLE);

                    } else if (givenDate.after(currentDate)) {
                        viewHolder.binding.layoutExpired.setVisibility(View.GONE);
                        viewHolder.binding.roundBlur.setVisibility(View.VISIBLE);
                        viewHolder.binding.card.setVisibility(View.VISIBLE);
                        if (model.getPackages() != null && !model.getPackages().isEmpty()) {
                            viewHolder.binding.btnBuyNow.setVisibility(View.VISIBLE);
                        }
                    } else {
                        viewHolder.binding.btnBuyNow.setVisibility(View.GONE);
                        viewHolder.binding.layoutExpired.setVisibility(View.VISIBLE);
                        viewHolder.binding.roundBlur.setVisibility(View.INVISIBLE);
                        viewHolder.binding.card.setVisibility(View.INVISIBLE);
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            if (model.getVenue() != null) {
                viewHolder.binding.venueContainer.setVenueDetail(model.getVenue());
            }

            viewHolder.binding.btnBuyNow.setOnClickListener(view -> {
                if (model.getVenue() != null) {
                    startActivity(new Intent(activity, EventBuyNowActivity.class).putExtra("eventListDetail", new Gson().toJson(model)).putExtra("venueModel", new Gson().toJson(model.getVenue())));
                }
            });

            viewHolder.binding.btnBucketList.setOnClickListener(view -> {
                Utils.preventDoubleClick( view );
                BucketListBottomSheet dialog = new BucketListBottomSheet();
                dialog.eventId = model.getId();
                dialog.show(getSupportFragmentManager(), "");
            });

        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private ItemEventDetailBinding binding;

            private final OfferPackagesAdapter<PackageModel> packageAdapter = new OfferPackagesAdapter<>();

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemEventDetailBinding.bind(itemView);
                binding.packageRecycler.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
                binding.packageRecycler.setAdapter(packageAdapter);
            }
        }
    }

    // endregion
    // --------------------------------------

}