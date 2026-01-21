package com.whosin.app.ui.fragment.CmProfile;

import static com.whosin.app.comman.AppDelegate.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import com.google.gson.Gson;
import com.king.image.imageviewer.ImageViewer;
import com.king.image.imageviewer.loader.GlideImageLoader;
import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.FragmentCmMyProfileBinding;
import com.whosin.app.databinding.ItemCmEventNameCountBinding;
import com.whosin.app.databinding.ItemCmEventsDesignBinding;
import com.whosin.app.databinding.ItemCmEventsWishlistBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.ComplementaryProfileManager;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.ComplimentaryProfileModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.NotificationModel;
import com.whosin.app.service.models.PromoterEventModel;
import com.whosin.app.service.models.RatingModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.CmProfile.CmProfileActivity;
import com.whosin.app.ui.activites.CmProfile.EventImInActivity;
import com.whosin.app.ui.activites.Profile.ProfileFullScreenImageActivity;
import com.whosin.app.ui.activites.Promoter.PromoterActivity;
import com.whosin.app.ui.activites.home.activity.SeeAllRatingReviewActivity;
import com.whosin.app.ui.adapter.CmEventListAdapter;
import com.whosin.app.ui.fragment.comman.BaseFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.imaginativeworld.whynotimagecarousel.listener.CarouselListener;
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;

import java.util.ArrayList;
import java.util.List;

public class CmMyProfileFragment extends BaseFragment {

    private FragmentCmMyProfileBinding binding;

    private cmEventListsAdapter<RatingModel> eventLists = new cmEventListsAdapter<>();

    private ItemListAdapter<RatingModel> ItemListAdapter = new ItemListAdapter<>();

    private ComplimentaryProfileModel profileModel;




    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    public void initUi(View view) {

        binding = FragmentCmMyProfileBinding.bind(view);

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }


        binding.eventListRecycleView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        binding.eventListRecycleView.setAdapter(eventLists);


        binding.eventListNameRecyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        binding.eventListNameRecyclerView.setAdapter(ItemListAdapter);

        requestComplimentaryProfile(true);

    }

    @Override
    public void setListeners() {

        binding.tvSeeAll.setOnClickListener(view -> startActivity(new Intent(activity, SeeAllRatingReviewActivity.class)
                .putExtra("id", profileModel.getProfile().getUserId())
                .putExtra("type", "complimentary")
                .putExtra("isMyProfile", true)
                .putExtra("start", profileModel.getReview().getAvgRating())
                .putExtra("currentUserRating", new Gson().toJson(profileModel.getReview().getCurrentUserRating()))));


        binding.swipeRefreshLayout.setOnRefreshListener(() -> requestComplimentaryProfile(false));


//        binding.profileswitchBtn.setOnClickListener(v -> {
//            startActivity(new Intent(activity, ProfileFragment.class));
//            CmProfileActivity activity = (CmProfileActivity) getActivity();
//            if (activity != null) {
//                activity.callback.onReceive(true);
//            }
//        });


        binding.cmEditProfile.setOnClickListener(v -> {
            Utils.preventDoubleClick(v);
            if (profileModel.getProfile() == null){return;}
            startActivity(new Intent(activity, PromoterActivity.class).putExtra("isPromoter", false)
                    .putExtra("isEditProfile", true)
                    .putExtra("userProfileModel", new Gson().toJson(profileModel.getProfile())));
        });

        binding.nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            private boolean disableScrollHandling = false;

            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (disableScrollHandling) {
                    return;
                }

                int scrollDistance = scrollY - oldScrollY;

                if (Math.abs(scrollDistance) > 10) {
                    if (scrollDistance > 0) {
                        binding.headerBlurView.setVisibility(View.VISIBLE);
                    } else {
                        if (scrollY <= 10) {
                            binding.headerBlurView.setVisibility(View.GONE);
                        }
                    }
                }
            }
        });

        binding.ivClose.setOnClickListener(v -> {
            CmProfileActivity activity = (CmProfileActivity) getActivity();
            if (activity != null) {
                activity.callback.onReceive(true);
            }
        });

        binding.ivShare.setOnClickListener(v -> {
            Utils.preventDoubleClick(v);
            if (profileModel == null) {return;}
            if (profileModel.getProfile() == null){return;}

            Utils.generateDynamicLinks(activity, profileModel.getProfile());
        });

        binding.cmImageProfile.setOnClickListener(v -> {
            Utils.preventDoubleClick(v);
            Intent intent = new Intent( activity, ProfileFullScreenImageActivity.class );
            intent.putExtra( ProfileFullScreenImageActivity.EXTRA_IMAGE_URL, profileModel.getProfile().getImage());
            startActivity( intent );
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
                ImageViewer.load(profileModel.getProfile().getImages())
                        .selection(i)
                        .imageLoader(new GlideImageLoader())
                        .indicator(true)
                        .start(requireActivity());
            }

            @Override
            public void onLongClick(int i, @NonNull CarouselItem carouselItem) {

            }
        });


    }

    @Override
    public void populateData(boolean getDataFromServer) {

    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_cm_my_profile;

    }


    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ComplimentaryProfileModel model) {
        requestComplimentaryProfile(false);
//        ComplementaryProfileManager.shared.requestPromoterUpdateInviteStatus(requireActivity());
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    @SuppressLint("NewApi")
    private void setProfileData() {
        if (profileModel == null) {
            return;
        }

        Graphics.loadImageWithFirstLetter(profileModel.getProfile().getImage(), binding.cmImageProfile, profileModel.getProfile().getFullName());
        binding.cmDescription.setText(profileModel.getProfile().getBio());
        binding.cmUserName.setText(profileModel.getProfile().getFullName());

        Graphics.applyBlurEffect(requireActivity(), binding.headerBlurView);
        binding.headerBlurView.setVisibility(View.GONE);
        binding.headertitle.setText(String.format("%s %s", profileModel.getProfile().getFirstName(), profileModel.getProfile().getLastName()));
        Graphics.loadImageWithFirstLetter(profileModel.getProfile().getImage(), binding.headerIv, profileModel.getProfile().getFullName());

        if (profileModel.getScore() != null) {
            binding.profilePunctuality.setUpData(profileModel.getScore().getPunctuality(), requireActivity(), getChildFragmentManager());
            binding.profileActivity.setUpData(profileModel.getScore().getActivity(), requireActivity(), getChildFragmentManager());
            binding.profileValue.setUpData(profileModel.getScore().getValue(), requireActivity(), getChildFragmentManager());
        }

        setBanner();

        List<RatingModel> eventList = new ArrayList<>();


        List<PromoterEventModel> inEvents = profileModel.getInEvents();
        if (inEvents != null && !inEvents.isEmpty()) {
            inEvents.removeIf(p -> "cancelled".equals(p.getStatus()) || "completed".equals(p.getStatus()));
            boolean hasEvents = !inEvents.isEmpty();
            if (hasEvents) {
                eventList.add(new RatingModel("1", inEvents));
            } else {
                eventList.add(new RatingModel("1", new ArrayList<>()));
            }
        }


        List<PromoterEventModel> wishlistEvents = profileModel.getWishlistEvents();
        if (wishlistEvents != null && !wishlistEvents.isEmpty()) {
            wishlistEvents.removeIf(p -> "cancelled".equals(p.getStatus()) || "completed".equals(p.getStatus()));
            boolean hasWishlistEvents = !wishlistEvents.isEmpty();
            if (hasWishlistEvents) {
                eventList.add(new RatingModel("2", wishlistEvents));
            }else {
                eventList.add(new RatingModel("2", new ArrayList<>()));
            }
        }

        if (profileModel.getSpeciallyForMeEvents() != null && !profileModel.getSpeciallyForMeEvents().isEmpty()){
            eventList.add(new RatingModel("3", profileModel.getSpeciallyForMeEvents()));
        }else {
            eventList.add(new RatingModel("3", new ArrayList<>()));
        }

        if (profileModel.getInterestedEvents() != null && !profileModel.getInterestedEvents().isEmpty()){
            eventList.add(new RatingModel("4", profileModel.getInterestedEvents()));
        }else {
            eventList.add(new RatingModel("4", new ArrayList<>()));
        }




        boolean allListsEmpty = eventList.stream()
                .allMatch(event -> event.getPromoterEventModelList() == null || event.getPromoterEventModelList().isEmpty());

        if (allListsEmpty) {
            binding.eventListRecycleView.setVisibility(View.GONE);
        } else {
            binding.eventListRecycleView.setVisibility(View.VISIBLE);
            eventLists.updateData(eventList);
        }


        if (profileModel.getCounter() != null){
            List<RatingModel> tmpList = new ArrayList<>();
            tmpList.add(new RatingModel("Events Im In",profileModel.getCounter().getEventsImIn()));
            tmpList.add(new RatingModel("Specially for me",profileModel.getCounter().getSpeciallyForMe()));
            tmpList.add(new RatingModel("My List",wishlistEvents.size()));
            tmpList.add(new RatingModel("Im Interested",profileModel.getCounter().getImInterested()));

            ItemListAdapter.updateData(tmpList);

        }



    }


    private void setBanner() {
        if (profileModel == null) {
            return;
        }

        List<CarouselItem> carouselItems = new ArrayList<>();


        if (!profileModel.getProfile().getImages().isEmpty()) {
            for (String imageLink : profileModel.getProfile().getImages()) {
                carouselItems.add(new CarouselItem(imageLink, "Static Banner Title"));
            }
        } else {
            carouselItems.add(new CarouselItem(R.drawable.app_icon));
        }

        binding.imageCarousel.registerLifecycle(getLifecycle());
        binding.imageCarousel.setData(carouselItems);
    }


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestComplimentaryProfile(boolean isShowProgress) {
        if (isShowProgress) {
            showProgress();
        } else {
            binding.swipeRefreshLayout.setRefreshing(true);
        }
        DataService.shared(activity).requestComplimentaryProfile(new RestCallback<ContainerModel<ComplimentaryProfileModel>>(this) {
            @Override
            public void result(ContainerModel<ComplimentaryProfileModel> model, String error) {
                hideProgress();
                binding.swipeRefreshLayout.setRefreshing(false);
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    return;
                }
                if (model.getData() != null) {
                    SessionManager.shared.saveCmUserData(model.getData());
                    ComplementaryProfileManager.shared.complimentaryProfileModel = model.getData();
                    profileModel = model.getData();
//                    ComplementaryProfileManager.shared.callbackForHeader.onReceive(model.getData().getProfile());
                    EventBus.getDefault().post(new NotificationModel());
                    setProfileData();
                }
            }
        });
    }

    // --------------------------------------
    // region Adapter
    // --------------------------------------


    public class cmEventListsAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());

            switch (viewType) {
                case 1:
                    return new EventsImInHolder(inflater.inflate(R.layout.item_cm_events_design, parent, false));
                case 2:
                    return new EventsWishlistEventsHolder(inflater.inflate(R.layout.item_cm_events_wishlist, parent, false));
                case 3:
                    return new EventsSpeciallyForMe(inflater.inflate(R.layout.item_cm_events_design, parent, false));
                case 4:
                    return new EventsImInterested(inflater.inflate(R.layout.item_cm_events_design, parent, false));
                default:
                    return new EventsImInHolder(inflater.inflate(R.layout.item_cm_events_design, parent, false));
            }

        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            RatingModel model = (RatingModel) getItem(position);

            boolean isLastItem = position == getItemCount() - 1;


            switch (getItemViewType(position)) {
                case 1:
                    EventsImInHolder viewHolder1 = (EventsImInHolder) holder;
                    viewHolder1.setupData(model);
                    break;
                case 2:
                    EventsWishlistEventsHolder viewHolder2 = (EventsWishlistEventsHolder) holder;
                    viewHolder2.setupData(model);
                    break;
                case 3:
                    EventsSpeciallyForMe viewHolder3 = (EventsSpeciallyForMe) holder;
                    viewHolder3.setupData(model);
                    break;
                case 4:
                    EventsImInterested viewHolder4 = (EventsImInterested) holder;
                    viewHolder4.setupData(model);
                    break;
                default:
                    EventsImInHolder defaultViewHolder = (EventsImInHolder) holder;
                    defaultViewHolder.setupData(model);
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
            RatingModel model = (RatingModel) getItem(position);
            switch (model.getType()) {
                case "1":
                    return 1;
                case "2":
                    return 2;
                case "3":
                    return 3;
                case "4":
                    return 4;
                default:
                    return 5;
            }
        }


        public class EventsImInHolder extends RecyclerView.ViewHolder {

            private final ItemCmEventsDesignBinding mBinding;

            private CmEventListAdapter<PromoterEventModel> eventlistdapter;

            public EventsImInHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemCmEventsDesignBinding.bind(itemView);
                eventlistdapter = new CmEventListAdapter<>(requireActivity(), "profileEventIn");
                mBinding.eventImInRecycleView.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false));
                mBinding.eventImInRecycleView.setAdapter(eventlistdapter);

            }

            private void setupData(RatingModel model) {

                mBinding.eventTitle.setText("Events I’m IN");

                if (!model.getPromoterEventModelList().isEmpty()) {
                    mBinding.getRoot().setVisibility(View.VISIBLE);
                    eventlistdapter.updateData(model.getPromoterEventModelList());
                } else {
                    mBinding.getRoot().setVisibility(View.GONE);
                }

                mBinding.eventInTitleLayout.setOnClickListener(v -> {
                    ComplementaryProfileManager.shared.eventList = eventlistdapter.getData();
                    requireActivity().startActivity(new Intent(requireActivity(), EventImInActivity.class).putExtra("title", "Events I’m IN"));
                });


            }
        }

        public class EventsWishlistEventsHolder extends RecyclerView.ViewHolder {

            private final ItemCmEventsWishlistBinding mBinding;

            public EventsWishlistEventsHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemCmEventsWishlistBinding.bind(itemView);
            }

            private void setupData(RatingModel model) {

                if (!model.getPromoterEventModelList().isEmpty()) {
                    mBinding.getRoot().setVisibility(View.VISIBLE);
                    mBinding.myEventList.type = "myEventList";
                    mBinding.myEventList.setUpData(model.getPromoterEventModelList(), requireActivity());
                } else {
                    mBinding.getRoot().setVisibility(View.GONE);
                }


            }
        }

        public class EventsSpeciallyForMe extends RecyclerView.ViewHolder {

            private final ItemCmEventsDesignBinding vBinding;

            private CmEventListAdapter<PromoterEventModel> eventlistdapter;

            public EventsSpeciallyForMe(@NonNull View itemView) {
                super(itemView);
                vBinding = ItemCmEventsDesignBinding.bind(itemView);
                eventlistdapter = new CmEventListAdapter<>(requireActivity(), "profileEventIn");
                vBinding.eventImInRecycleView.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false));
                vBinding.eventImInRecycleView.setAdapter(eventlistdapter);

                vBinding.btnEventImInSeeAll.setOnClickListener(v -> {
                    Utils.preventDoubleClick(v);
                    ComplementaryProfileManager.shared.eventList = eventlistdapter.getData();
                    startActivity(new Intent(requireActivity(), EventImInActivity.class).putExtra("title", "Specially for me"));
                });

            }

            private void setupData(RatingModel model) {
                vBinding.eventTitle.setText("Specially for me");

                if (!model.getPromoterEventModelList().isEmpty()) {
                    vBinding.getRoot().setVisibility(View.VISIBLE);
                    eventlistdapter.updateData(model.getPromoterEventModelList());
                } else {
                    vBinding.getRoot().setVisibility(View.GONE);
                }
            }

        }

        public class EventsImInterested extends RecyclerView.ViewHolder {
            private final ItemCmEventsDesignBinding iBinding;

            private CmEventListAdapter<PromoterEventModel> eventlistdapter;

            public EventsImInterested(@NonNull View itemView) {
                super(itemView);
                iBinding = ItemCmEventsDesignBinding.bind(itemView);
                eventlistdapter = new CmEventListAdapter<>(requireActivity(), "profileEventIn");
                iBinding.eventImInRecycleView.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false));
                iBinding.eventImInRecycleView.setAdapter(eventlistdapter);

                iBinding.btnEventImInSeeAll.setOnClickListener(v -> {
                    Utils.preventDoubleClick(v);
                    ComplementaryProfileManager.shared.eventList = eventlistdapter.getData();
                    startActivity(new Intent(requireActivity(), EventImInActivity.class).putExtra("title", "I’m Interested"));
                });
            }

            private void setupData(RatingModel model) {
                iBinding.eventTitle.setText("I’m Interested");

                if (!model.getPromoterEventModelList().isEmpty()) {
                    iBinding.getRoot().setVisibility(View.VISIBLE);
                    eventlistdapter.updateData(model.getPromoterEventModelList());
                } else {
                    iBinding.getRoot().setVisibility(View.GONE);
                }
            }
        }

    }


    public class ItemListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder( UiUtils.getViewBy( parent, R.layout.item_cm_event_name_count ) );
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            ViewHolder viewHolder = (ViewHolder) holder;
            RatingModel model = (RatingModel) getItem( position );

            viewHolder.binding.iconText.setText( model.getType());
            viewHolder.binding.tvCount.setText(String.valueOf(model.getCount()));

            viewHolder.binding.notificationCountLayout.setVisibility(model.getCount() == 0 ? View.GONE: View.VISIBLE);

            viewHolder.binding.getRoot().setOnClickListener(v -> {
                Utils.preventDoubleClick(v);
                startActivity(new Intent(requireActivity(), EventImInActivity.class)
                        .putExtra("isCallEventApi",true)
                        .putExtra("eventTypeId" , position)
                        .putExtra("title", model.getType()));
            });

        }


        public class ViewHolder extends RecyclerView.ViewHolder {

            private final ItemCmEventNameCountBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super( itemView );
                binding = ItemCmEventNameCountBinding.bind( itemView );
            }
        }
    }



    // endregion
    // --------------------------------------
}