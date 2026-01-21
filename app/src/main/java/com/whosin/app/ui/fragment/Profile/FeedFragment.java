package com.whosin.app.ui.fragment.Profile;

import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.tapadoo.alerter.Alerter;
import com.whosin.app.R;
import com.whosin.app.comman.AppConstants;
import com.whosin.app.comman.AppExecutors;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.EventItemOfferFeedBinding;
import com.whosin.app.databinding.FragmentFeedBinding;
import com.whosin.app.databinding.ItemFriendsUpdateFeedBinding;
import com.whosin.app.databinding.ItemVenueOfferFeedsBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.AppSettingManager;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.AppSettingTitelCommonModel;
import com.whosin.app.service.models.ContainerListModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.FollowUnfollowModel;
import com.whosin.app.service.models.MyUserFeedModel;
import com.whosin.app.service.models.PackageModel;
import com.whosin.app.service.models.VenueObjectModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.Profile.OtherUserProfileActivity;
import com.whosin.app.ui.activites.home.event.EventDetailsActivity;
import com.whosin.app.ui.activites.offers.OfferDetailBottomSheet;
import com.whosin.app.ui.activites.venue.Bucket.BucketListBottomSheet;
import com.whosin.app.ui.activites.venue.VenueActivity;
import com.whosin.app.ui.activites.venue.VenueBuyNowActivity;
import com.whosin.app.ui.activites.venue.VenueShareActivity;
import com.whosin.app.ui.activites.venue.VenueTimingDialog;
import com.whosin.app.ui.adapter.OfferPackagesAdapter;
import com.whosin.app.ui.fragment.comman.BaseFragment;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FeedFragment extends BaseFragment {

    private FragmentFeedBinding binding;

    private final FeedAdapter<MyUserFeedModel> feedAdapter = new FeedAdapter<>();

    private List<MyUserFeedModel> usedFeedData = new ArrayList<>();

    private int page = 1;

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    public void initUi(View view) {

        binding = FragmentFeedBinding.bind(view);

        binding.emptyPlaceHolderView.setEmptyPlaceTxtTitle(getValue("feed_fragment_empty_message"));

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        binding.recyclerView.setAdapter(feedAdapter);

        Thread backgroundThread = new Thread(() -> {
            List<MyUserFeedModel> venueList = SessionManager.shared.getProfileFeed();
            if(venueList != null){
                usedFeedData = venueList;
                filterData();
            }
        });
        backgroundThread.start();

    }

    @Override
    public void setListeners() {
        binding.swipeRefreshLayout.setOnRefreshListener(() -> requestMyUserFeed(false));

        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) binding.recyclerView.getLayoutManager();
                if (linearLayoutManager == null) { return; }
                int lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();

                if (lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == feedAdapter.getData().size() - 1 && (feedAdapter.getData().size() % 30 == 0 && (!feedAdapter.getData().isEmpty()))){
                    page++;
                    requestMyUserFeed(true);
                }

            }
        });
    }

    @Override
    public void populateData(boolean getDataFromServer) {
        requestMyUserFeed(false);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_feed;
    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void filterData() {
        if (usedFeedData.isEmpty()){return;}
        List<MyUserFeedModel> otherUserFeed = usedFeedData.stream().filter(model -> model.getType().equals("friend_updates") || model.getType().equals("venue_updates") || model.getType().equals("event_checkin")).collect(Collectors.toList());
        AppExecutors.get().mainThread().execute(() -> {
            feedAdapter.updateData(otherUserFeed);
            hideProgress();
        });

    }


    private void setTitleFromList(List<String> categoryIds, List<AppSettingTitelCommonModel> commonModels, String type, TextView textView) {
        if (categoryIds == null || categoryIds.isEmpty() || commonModels == null) {
            textView.setVisibility(View.GONE);
            return;
        }
        Thread backgroundThread = new Thread(() -> {
            String cuisineText = commonModels.stream().filter(p -> categoryIds.contains(p.getId())).map(AppSettingTitelCommonModel::getTitle).collect(Collectors.joining(", "));
            if (!cuisineText.isEmpty()) {
                SpannableString spannableString = new SpannableString(type + cuisineText);
                ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.WHITE);
                spannableString.setSpan(colorSpan, 0, type.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                requireActivity().runOnUiThread(() -> {
                    textView.setText(spannableString);
                    textView.setVisibility(View.VISIBLE);
                });
            } else {
                requireActivity().runOnUiThread(() -> textView.setVisibility(View.GONE));
            }
        });
        backgroundThread.start();
    }

    private void updateVenueFollowStatus(boolean followStatus, String venueId) {
        feedAdapter.getData().forEach(p -> {
            if (p.getVenue() != null && (p.getVenue().getId().equals(venueId))) {
                p.getVenue().setFollowing(followStatus);
            }
        });
        feedAdapter.notifyDataSetChanged();
    }


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestMyUserFeed(boolean showPaginationLoader) {
        if (showPaginationLoader){
            binding.pagginationProgressBar.setVisibility(View.VISIBLE);
        }
        if (feedAdapter.getData().isEmpty()) {
            showProgress();
        }
        DataService.shared(requireActivity()).requestUserFeed(page, new RestCallback<ContainerListModel<MyUserFeedModel>>(this) {
            @Override
            public void result(ContainerListModel<MyUserFeedModel> model, String error) {
                hideProgress();
                binding.pagginationProgressBar.setVisibility(View.GONE);
                binding.swipeRefreshLayout.setRefreshing( false );
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (model.data != null && !model.data.isEmpty()) {
                    if (page == 1) { usedFeedData.clear(); }
                    usedFeedData.addAll(model.data);
                    SessionManager.shared.saveProfileFeed( model.data );
                    filterData();
                }
                binding.swipeRefreshLayout.setVisibility(usedFeedData.isEmpty() ? View.GONE : View.VISIBLE);
                binding.recyclerView.setVisibility(usedFeedData.isEmpty() ? View.GONE : View.VISIBLE);
                binding.emptyPlaceHolderView.setVisibility(usedFeedData.isEmpty() ? View.VISIBLE : View.GONE);
            }
        });
    }



    // --------------------------------------
    // region Adapter
    // --------------------------------------

    public class FeedAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            switch (AppConstants.UserFeedType.valueOf(viewType)) {
                case FRIENDS_UPDATE:
                    return new FriendUpdatesHolder(UiUtils.getViewBy(parent, R.layout.item_friends_update_feed));
                case VENUE_UPDATE:
                    return new VenueOfferHolder(UiUtils.getViewBy(parent, R.layout.item_venue_offer_feeds));
                case EVENY_UPDATE:
                    return new EventViewHolder(UiUtils.getViewBy(parent, R.layout.event_item_offer_feed));

            }
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            boolean isLastItem = position == getItemCount() - 1;
            MyUserFeedModel model = (MyUserFeedModel) getItem(position);
            if (model != null) {
                if (model.getBlockType().equals(AppConstants.UserFeedType.FRIENDS_UPDATE)) {
                    FriendUpdatesHolder viewHolder = (FriendUpdatesHolder) holder;
                    viewHolder.setData(model);
                } else if (model.getBlockType().equals(AppConstants.UserFeedType.VENUE_UPDATE)) {
                    VenueOfferHolder venueOfferHolder = (VenueOfferHolder) holder;
                    venueOfferHolder.setData(model);
                } else if (model.getBlockType().equals(AppConstants.UserFeedType.EVENY_UPDATE)) {
                    EventViewHolder viewHolder = (EventViewHolder) holder;
                    viewHolder.setupData(model);
                }

                if (isLastItem) {
                    int marginBottom = Utils.getMarginBottom(holder.itemView.getContext(), 0.10f);
                    Utils.setBottomMargin(holder.itemView, marginBottom);
                } else {
                    Utils.setBottomMargin(holder.itemView, 0);
                }
            }
        }

        public class FriendUpdatesHolder extends RecyclerView.ViewHolder {
            private final ItemFriendsUpdateFeedBinding mBinding;

            public FriendUpdatesHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemFriendsUpdateFeedBinding.bind(itemView);
            }

            public void setData(MyUserFeedModel model) {
                if (model == null) {
                    return;
                }
                if (model.getVenue() != null) {
                    mBinding.venueContainer.setVenueDetail(model.getVenue());
                    mBinding.btnFollowButton.setVenueRequestStatus(model.getVenue());
                    Graphics.loadImage(model.getVenue().getCover(), mBinding.img);

                    setTitleFromList(model.getVenue().getCuisine(), AppSettingManager.shared.getAppSettingData().getCuisine(), getValue("cuisine"), mBinding.tvCuisine);
                    setTitleFromList(model.getVenue().getMusic(), AppSettingManager.shared.getAppSettingData().getMusic(), getValue("music"), mBinding.tvMusic);
                    setTitleFromList(model.getVenue().getFeature(), AppSettingManager.shared.getAppSettingData().getFeature(), getValue("features"), mBinding.tvFeature);

                    mBinding.tvDressCode.setText(MessageFormat.format("{0}{1}", getValue("dress_code"), model.getVenue().getDressCode()));
                    mBinding.tvDressCode.setVisibility(TextUtils.isEmpty(model.getVenue().getDressCode()) ? View.GONE : View.VISIBLE);
                }

                mBinding.tvTime.setText(Utils.getTimeAgo(model.getCreatedAt(), requireActivity()));
                if (model.getUser() != null) {
                    mBinding.tvUserName.setText(Html.fromHtml(model.getUser().getFirstName() + " " + model.getUser().getLastName() + getValue("venue_followed")));
                    Graphics.loadImageWithFirstLetter(model.getUser().getImage(), mBinding.ivForFollow,model.getUser().getFullName());
                }
                setListeners(model);

            }

            private void setListeners(MyUserFeedModel model) {
                mBinding.btnFollowButton.setOnClickListener(v -> {
                    if (model.getVenue() == null) {
                        return;
                    }
                    mBinding.btnFollowButton.requestFollowUnfollowVenue( model.getVenue(),requireActivity(), (success, message) -> {
                        if (message.equals("Unfollowed!")) {
                            updateVenueFollowStatus(false, model.getVenue().getId());
                            Alerter.create(requireActivity()).setTitle(getValue("oh_snap")).setTitleAppearance(R.style.AlerterTitle).setTextAppearance(R.style.AlerterText).setText(setValue("unfollow_toast",model.getVenue().getName())).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                        } else {
                            updateVenueFollowStatus(true, model.getVenue().getId());
                            Alerter.create(requireActivity()).setTitle(getValue("thank_you")).setText(setValue("following_toast",model.getVenue().getName())).setTextAppearance(R.style.AlerterText).setTitleAppearance(R.style.AlerterTitle).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                        }
                    });
                });

                itemView.setOnClickListener(view -> startActivity(new Intent(requireActivity(), VenueActivity.class).putExtra("venueId", model.getVenue().getId())));

                mBinding.layout.setOnClickListener(view -> startActivity(new Intent(requireActivity(), OtherUserProfileActivity.class).putExtra("friendId", model.getUser().getId())));
            }

        }

        public class VenueOfferHolder extends RecyclerView.ViewHolder {

            private final ItemVenueOfferFeedsBinding binding;

            private final OfferPackagesAdapter<PackageModel> adapter = new OfferPackagesAdapter<>();

            public VenueOfferHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemVenueOfferFeedsBinding.bind(itemView);

                binding.dateTitleFrom.setText(getValue("and_from"));
                binding.tvTillDateTitle.setText(getValue("and_till"));

                binding.buttonOne.setText(getValue("invite_your_friends"));
                binding.buttonTwo.setText(getValue("claim_discount"));
                binding.buttonThree.setText(getValue("invite_your_friends"));

                binding.venueSubRecycler.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false));
                binding.venueSubRecycler.setAdapter(adapter);
            }

            public void setData(MyUserFeedModel model) {
                if (model.getOffer() == null) {
                    return;
                }
                binding.tvUserName.setText(Html.fromHtml(model.getVenue().getName() + getValue("added_new_offer")));
                binding.txtTitle.setText(model.getOffer().getTitle());
                binding.tvDescription.setText(model.getOffer().getDescription());
                binding.txtDays.setText(model.getOffer().getDays());

                if (TextUtils.isEmpty(model.getOffer().getStartTime())) {
                    binding.startDate.setText(getValue("ongoing"));
                    binding.layoutEndDate.setVisibility(View.GONE);
                } else {
                    binding.layoutEndDate.setVisibility(View.VISIBLE);
                    binding.startDate.setText(Utils.convertMainDateFormat(model.getOffer().getStartTime()));
                    binding.endDate.setText(Utils.convertMainDateFormat(model.getOffer().getEndTime()));

                }
                binding.txtOfferTime.setText(model.getOffer().getOfferTiming());
                binding.btnTimeInfo.setVisibility(model.getOffer().isShowTimeInfo() ? View.GONE : View.VISIBLE);
                Graphics.loadImage(model.getOffer().getImage(), binding.img);
                binding.tvTime.setText(Utils.getTimeAgo(model.getCreatedAt(), requireContext()));

                if (model.getOffer().getVenue() != null) {
                    if (!model.getOffer().getVenue().getLogo().isEmpty()) {
                        Graphics.loadImage(model.getOffer().getVenue().getLogo(), binding.ivForFollow);
                    } else {
                        Graphics.loadImageWithFirstLetter(model.getOffer().getVenue().getLogo(), binding.ivForFollow, model.getOffer().getVenue().getName());
                    }
                }

                Utils.setupOfferButtons(model.getOffer(), binding.buttonOne, binding.buttonTwo, binding.buttonThree);

                View.OnClickListener buttonClick = v -> {
                    Utils.preventDoubleClick(v);
                    if (model.getVenue() == null) { return; }
                    TextView button = (TextView) v;
                    String buttonText = button.getText().toString();
                    if(buttonText.equalsIgnoreCase(getValue("buy_now"))) {
                        startActivity(new Intent(requireActivity(), VenueBuyNowActivity.class).putExtra("venueObjectModel", new Gson().toJson(model.getOffer().getVenue())).putExtra("offerModel", new Gson().toJson(model.getOffer())));
                    } else if(buttonText.equalsIgnoreCase(getValue("claim_discount"))) {
                        Utils.openClaimScreen(model.getOffer().getSpecialOfferModel(), model.getOffer().getVenue(), requireActivity());
                    }
//                    else {
//                        Utils.openInviteButtonSheet(model.getOffer(), model.getOffer().getVenue(),getChildFragmentManager());
//                    }
                };

                binding.buttonOne.setOnClickListener(buttonClick);
                binding.buttonTwo.setOnClickListener(buttonClick);
                binding.buttonThree.setOnClickListener(buttonClick);


                if (!model.getOffer().getPackages() .isEmpty()) {
                    adapter.updateData(model.getOffer().getPackages());
                    binding.venueSubRecycler.setVisibility(View.VISIBLE);
                }

                setListeners(model);
            }


            private void setListeners(MyUserFeedModel model) {
                binding.iconMenu.setOnClickListener(v -> {
                    Utils.preventDoubleClick(v);
                    ArrayList<String> data = new ArrayList<>();
                    data.add(getValue("share_venue"));
                    data.add(getValue("share_offer"));
                    Graphics.showActionSheet(requireContext(), model.getOffer().getTitle(), data, (data1, position1) -> {
                        switch (position1) {
                            case 0:
                                BucketListBottomSheet dialog = new BucketListBottomSheet();
                                dialog.offerId = model.getOffer().getId();
                                dialog.show(getChildFragmentManager(), "");
                                break;
                            case 1:
                                startActivity( new Intent( requireActivity(), VenueShareActivity.class ).putExtra( "venue", new Gson().toJson( model.getVenue() ) )
                                        .putExtra( "type", "venue" ) );
                                break;
                            case 2:
                                startActivity( new Intent( requireActivity(), VenueShareActivity.class ).putExtra( "offer", new Gson().toJson( model.getOffer() ) )
                                        .putExtra( "type", "offer" ) );
                                break;
                        }
                    });

                });

                itemView.setOnClickListener(v -> {
                    OfferDetailBottomSheet dialog = new OfferDetailBottomSheet();
                    dialog.offerId =model.getOffer().getId();
                    dialog.show(getChildFragmentManager(), "");
                });

                binding.layout.setOnClickListener(view -> {
                    if (model.getUser() != null) {
                        startActivity(new Intent(requireActivity(), OtherUserProfileActivity.class).putExtra("friendId", model.getUser().getId()));
                    }
                });

                binding.layoutTimeInfo.setOnClickListener(v -> {
                    if (!model.getOffer().isShowTimeInfo()) {
                        if (model.getOffer() == null) {
                            return;
                        }
                        if (model.getOffer().getVenue() == null) {
                            return;
                        }
                        VenueTimingDialog dialog = new VenueTimingDialog(model.getOffer().getVenue().getTiming(), getActivity());
                        dialog.show(getChildFragmentManager(), "1");
                    }
                });

            }
        }

        public class EventViewHolder extends RecyclerView.ViewHolder {

            private final EventItemOfferFeedBinding binding;

            public EventViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = EventItemOfferFeedBinding.bind(itemView);
            }

            public void setupData(MyUserFeedModel model) {

                if (model.getUser() != null) {
                    binding.tvUserName.setText(Html.fromHtml(model.getUser().getFirstName() + " " + model.getUser().getLastName() + getValue("checked_in_bold")));
                    Graphics.loadImage(model.getUser().getImage(), binding.ivForFollow);
                }

                binding.tvTime.setText(Utils.getTimeAgo(model.getCreatedAt(), requireContext()));
                Graphics.loadImage(model.getEvent().getImage(), binding.img);

                if (!model.getEvent().getEventOrg().isEmpty()) {
                    binding.tvEventEmail.setText(model.getEvent().getEventOrg().get(0).getEmail());
                    binding.tvTitle.setText(model.getEvent().getEventOrg().get(0).getName());
                    Graphics.loadRoundImage(model.getEvent().getEventOrg().get(0).getCover(), binding.imgEventOrg);
                }

                if (model.getEvent().getVenue() != null) {
                    binding.txtUserName.setText(model.getEvent().getVenue().getName());
                    binding.tvAddress.setText(model.getEvent().getVenue().getAddress());
                    Graphics.loadRoundImage(model.getEvent().getVenue().getLogo(), binding.imgUserLogo);
                }

                binding.txtDate.setText(Utils.convertMainDateFormat(model.getEvent().getEventTime()));
                binding.txtTime.setText(Utils.convertMainTimeFormat(model.getEvent().getReservationTime()) + " - " + Utils.convertMainTimeFormat(model.getEvent().getEventTime()));

                binding.tvEventTitle.setText(model.getEvent().getTitle());
                binding.tvEventDes.setText(model.getEvent().getDescription());

                binding.layout.setOnClickListener(view -> startActivity(new Intent(requireActivity(), OtherUserProfileActivity.class).putExtra("friendId", model.getUser().getId())));

                itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(requireActivity(), EventDetailsActivity.class);
                    intent.putExtra("eventId", model.getEvent().getId());
                    if (model.getEvent().getEventOrg() != null) {
                        intent.putExtra("name", model.getEvent().getEventOrg().get(0).getName());
                        intent.putExtra("address", model.getEvent().getEventOrg().get(0).getWebsite());
                        intent.putExtra("image", model.getEvent().getEventOrg().get(0).getLogo());
                    }
                    if (model.getVenue() != null) {
                        intent.putExtra("venueId", model.getVenue().getId());
                        intent.putExtra("venueModel", new Gson().toJson(model.getVenue()));
                    }
                    startActivity(intent);
                });
            }
        }

        @Override
        public int getItemViewType(int position) {
            MyUserFeedModel model = (MyUserFeedModel) getItem(position);
            return model.getBlockType().getValue();
        }

    }

    // endregion
    // --------------------------------------
}