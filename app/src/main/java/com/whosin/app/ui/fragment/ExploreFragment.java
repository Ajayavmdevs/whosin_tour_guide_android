package com.whosin.app.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Paint;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.whosin.app.comman.AppConstants;
import com.whosin.app.comman.AppExecutors;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.BooleanResult;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.FilterTagItemBinding;
import com.whosin.app.databinding.FragmentExploreBinding;
import com.whosin.app.databinding.IteamSearchEventBinding;
import com.whosin.app.databinding.ItemExploreOfferBinding;
import com.whosin.app.databinding.ItemExploreVenueSuggestionRecyclerBinding;
import com.whosin.app.databinding.ItemHomeSuggestedUserRecyclerBinding;
import com.whosin.app.databinding.ItemSearchActivityBinding;
import com.whosin.app.databinding.SelectDaysItemBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.AppSettingManager;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.ActivityDetailModel;
import com.whosin.app.service.models.AppSettingTitelCommonModel;
import com.whosin.app.service.models.CommanSearchModel;
import com.whosin.app.service.models.ContainerListModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.ExploreModel;
import com.whosin.app.service.models.FollowUnfollowModel;
import com.whosin.app.service.models.OffersModel;
import com.whosin.app.service.models.PackageModel;
import com.whosin.app.service.models.SearchEventModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.models.VenueObjectModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.explore.ExploreFilterBottomSheet;
import com.whosin.app.ui.activites.home.activity.ActivityListDetail;
import com.whosin.app.ui.activites.home.event.EventDetailsActivity;
import com.whosin.app.ui.activites.home.event.EventOrganizerDetailsActivity;
import com.whosin.app.ui.activites.offers.OfferDetailBottomSheet;
import com.whosin.app.ui.activites.venue.Bucket.BucketListBottomSheet;
import com.whosin.app.ui.activites.venue.VenueActivity;
import com.whosin.app.ui.activites.venue.VenueShareActivity;
import com.whosin.app.ui.adapter.OfferPackagesAdapter;
import com.whosin.app.ui.fragment.comman.BaseFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import retrofit2.Call;

public class ExploreFragment extends BaseFragment {
    private FragmentExploreBinding binding;
    private final CommanExploreAdapter<ExploreModel> commanExploreAdapter = new CommanExploreAdapter<>();
    private final PreferencesListAdapter<AppSettingTitelCommonModel> listAdapter = new PreferencesListAdapter<>();
    private List<AppSettingTitelCommonModel> commonModels = new ArrayList<>();
    private String dateBefore = "";
    // private String tabId = "0";
    private List<String> tabIds = new ArrayList<>();
    private List<ExploreModel> list = new ArrayList<>();
    private List<ExploreModel> filterList = new ArrayList<>();
    ;
    private int mRecordLimit = 30;
    private List<UserDetailModel> suggestedUsers = new ArrayList<>();
    private List<VenueObjectModel> suggestedVenues = new ArrayList<>();

    private Call<ContainerListModel<ExploreModel>> service = null;

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------


    @Override
    public void initUi(View view) {
        binding = FragmentExploreBinding.bind(view);

        binding.navbar.setText(getValue("explore"));
        binding.edtSearch.setHint(getValue("explore"));
        binding.emptyPlaceHolderView.setEmptyPlaceTxtTitle(getValue("empty_explore_page"));

        setExploreAdapter();
        requestCommanExplore(false);
        requestSuggestedVenue();
        requestSuggestedUser();
        binding.prefrenceRecycler.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.prefrenceRecycler.setAdapter(listAdapter);
    }

    @Override
    public void setListeners() {

        binding.imgFilter.setOnClickListener(v -> {
            Utils.preventDoubleClick(v);
            ExploreFilterBottomSheet dialog = new ExploreFilterBottomSheet();
            dialog.setShareListener(data -> {
            });
            dialog.filterList = commonModels;
            dialog.callback = data -> {
                binding.prefrenceConstraint.setVisibility(View.VISIBLE);
                tabIds = data.stream().map(AppSettingTitelCommonModel::getId).collect(Collectors.toList());
                dateBefore = "";
                requestCommanExplore(false);
                commonModels = data;
                AppExecutors.get().mainThread().execute(() -> listAdapter.updateData(commonModels));
            };
            dialog.show(getChildFragmentManager(), "");
        });

        binding.exploreRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) binding.exploreRecycler.getLayoutManager();
                int lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
                if (lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == commanExploreAdapter.getData().size() - 1 && (commanExploreAdapter.getData().size() % mRecordLimit == 0)) {
                    String lastDate = list.get(list.size() - 1).getCreatedAt();
                    if (!Objects.equals(dateBefore, lastDate)) {
                        dateBefore = lastDate;
                        requestCommanExplore(true);
                    }
                }
            }
        });

        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            dateBefore = "";
            requestCommanExplore(false);
        });

        binding.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Filter the data based on the search query
            }

            @Override
            public void afterTextChanged(Editable s) {
//                searchData(s.toString());
                requestCommanExplore(true);
            }
        });

    }

    @Override
    public void populateData(boolean getDataFromServer) {

    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_explore;
    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------
    private void searchData(String query) {
        if (!TextUtils.isEmpty(query) && filterList != null) {
            List<ExploreModel> searchList = filterList.stream()
                    .filter(model ->
                            (model.getOffer() != null && model.getOffer().getTitle().toLowerCase().contains(query.toLowerCase())) ||
                                    (model.getEvent() != null && model.getEvent().getTitle().toLowerCase().contains(query.toLowerCase())) ||
                                    (model.getActivity() != null && model.getActivity().getName().toLowerCase().contains(query.toLowerCase()))
                    ).collect(Collectors.toList());

            list = searchList;
            reloadData();
        } else {
            list = filterList;
            reloadData();
//            commanExploreAdapter.updateData(filterList);
        }
    }

    private void setExploreAdapter() {
        binding.exploreRecycler.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false));
        binding.exploreRecycler.setAdapter(commanExploreAdapter);
        binding.exploreRecycler.setItemViewCacheSize(60);
        binding.exploreRecycler.getRecycledViewPool().setMaxRecycledViews(0, 0);
        binding.exploreRecycler.setHasFixedSize(true);
        binding.exploreRecycler.setNestedScrollingEnabled(false);
    }

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void reloadData() {
        List<ExploreModel> newList = new ArrayList<>();
        if (!suggestedVenues.isEmpty()) {
            ExploreModel model1 = new ExploreModel();
            model1.setType("suggested_venue");
            model1.setVenus(suggestedVenues);
            newList.add(model1);
        }
        if (!suggestedUsers.isEmpty()) {
            ExploreModel model1 = new ExploreModel();
            model1.setType("suggested_user");
            model1.setUsers(suggestedUsers);
            newList.add(model1);
        }
        if (!list.isEmpty()) {
            newList.addAll(list);
            binding.emptyPlaceHolderView.setVisibility(View.GONE);
        } else {
            binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
        }

        if (!newList.isEmpty()) {
            //filterList = newList;
            AppExecutors.get().mainThread().execute(() -> commanExploreAdapter.updateData(newList));
        } else {
            binding.exploreRecycler.setVisibility(View.GONE);
            binding.swipeRefreshLayout.setVisibility(View.GONE);
            binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
        }
    }

    private void requestSuggestedVenue() {
        DataService.shared(context).requestSuggestedVenue(SessionManager.shared.getUser().getId(), new RestCallback<ContainerListModel<VenueObjectModel>>(this) {
            @Override
            public void result(ContainerListModel<VenueObjectModel> model, String error) {
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (model != null && model.data != null) {
                    suggestedVenues = model.data;
                }
                reloadData();
            }
        });
    }

    private void requestSuggestedUser() {
        DataService.shared(context).requestSuggestedUser(SessionManager.shared.getUser().getId(), new RestCallback<ContainerListModel<UserDetailModel>>(this) {
            @Override
            public void result(ContainerListModel<UserDetailModel> model, String error) {
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (model != null && model.data != null) {
                    suggestedUsers = model.data;
                }
                reloadData();
            }
        });
    }

    private void requestCommanExplore(boolean showBelowProgressbar) {
        if (service != null) {
            service.cancel();
        }
        if (TextUtils.isEmpty(dateBefore)) {
            binding.swipeRefreshLayout.setRefreshing(true);
        }
        if (showBelowProgressbar) {
            binding.progressBar.setVisibility(View.VISIBLE);
        }
        service = DataService.shared(requireActivity()).requestCommanExplore(binding.edtSearch.getText().toString(),dateBefore, mRecordLimit, tabIds, new RestCallback<ContainerListModel<ExploreModel>>(this) {
            @Override
            public void result(ContainerListModel<ExploreModel> model, String error) {
                binding.swipeRefreshLayout.setRefreshing(false);
                binding.progressBar.setVisibility(View.GONE);
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(requireActivity(), error, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (model.data != null && !model.data.isEmpty()) {
                    binding.exploreRecycler.setVisibility(View.VISIBLE);
                    binding.emptyPlaceHolderView.setVisibility(View.GONE);
                    if (!dateBefore.isEmpty()) {
                        model.data.forEach(p -> {
                            if (p.getOffer() != null) {
                                p.getOffer().isAvailableToBuy();
                            }
                        });
                        list.addAll(model.data);
                        reloadData();
                    } else {
                        list = model.data;
                        model.data.forEach(p -> {
                            if (p.getOffer() != null) {
                                p.getOffer().isAvailableToBuy();
                            }
                        });
                        reloadData();
                        binding.exploreRecycler.scrollToPosition(0);
                    }
                    filterList = list;
                } else {
                    if (!Utils.isNullOrEmpty(binding.edtSearch.getText().toString())){
                        list = new ArrayList<>();
                    }
                    reloadData();
                }
            }
        });
    }

    private void reqFollowUnFollow(VenueObjectModel venueObjectModel, BooleanResult callBack) {
        showProgress();
        DataService.shared(requireActivity()).requestVenueFollow(venueObjectModel.getId(), new RestCallback<ContainerModel<FollowUnfollowModel>>(null) {
            @Override
            public void result(ContainerModel<FollowUnfollowModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(requireActivity(), R.string.service_message_something_wrong, Toast.LENGTH_SHORT).show();
                    return;
                }
                callBack.success(!model.message.equals("Unfollowed!"), "");
                if (!model.message.equals("Unfollowed!")) {
                    Alerter.create(requireActivity()).setTitle("Thank you!").setText("For following " + venueObjectModel.getName()).setTextAppearance(R.style.AlerterText).setTitleAppearance(R.style.AlerterTitle).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                    venueObjectModel.setIsFollowing(true);
                } else {
                    Alerter.create(requireActivity()).setTitle("Oh Snap!").setTitleAppearance(R.style.AlerterTitle).setTextAppearance(R.style.AlerterText).setText("You have unfollowed " + venueObjectModel.getName()).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                    venueObjectModel.setIsFollowing(false);
                }
            }
        });
    }

    private void reqRecommendation(String id, VenueObjectModel venueModel) {
        showProgress();
        DataService.shared(context).requestFeedRecommandation(id, "venue", new RestCallback<ContainerModel<UserDetailModel>>(this) {
            @Override
            public void result(ContainerModel<UserDetailModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(context, R.string.service_message_something_wrong, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!venueModel.isRecommendation()) {
                    venueModel.setRecommendation(true);
                    Alerter.create(getActivity()).setTitle("Thank you!").setTitleAppearance(R.style.AlerterTitle).setTextAppearance(R.style.AlerterText).setText("for recommended " + venueModel.getName() + "to your friends!").setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                } else {
                    venueModel.setRecommendation(false);
                    Alerter.create(getActivity()).setTitle("Oh Snap!!").setTitleAppearance(R.style.AlerterTitle).setTextAppearance(R.style.AlerterText).setText("you have removed recommendation of " + venueModel.getName()).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                }
            }
        });
    }

    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    public class CommanExploreAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            switch (Objects.requireNonNull(AppConstants.ExploreResultType.valueOf(viewType))) {
                case OFFER:
                    return new OfferBlockHolder(UiUtils.getViewBy(parent, R.layout.item_explore_offer));
                case EVENT:
                    return new EventBlockHolder(UiUtils.getViewBy(parent, R.layout.iteam_search_event));
                case ACTIVITY:
                    return new ActivityBlockHolder(UiUtils.getViewBy(parent, R.layout.item_search_activity));
                case SUGGESTED_VENUE:
                    return new SuggestedVenueViewHolder(UiUtils.getViewBy(parent, R.layout.item_explore_venue_suggestion_recycler));
                case SUGGESTED_USER:
                    return new SuggestedUserViewHolder(UiUtils.getViewBy(parent, R.layout.item_home_suggested_user_recycler));
            }
            return new OfferBlockHolder(UiUtils.getViewBy(parent, R.layout.category_list_item));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ExploreModel model = (ExploreModel) getItem(position);

            if (model.getBlockType() == AppConstants.ExploreResultType.OFFER) {
                OfferBlockHolder offerBlockHolder = (OfferBlockHolder) holder;
                offerBlockHolder.setupData(model.getOffer());
                offerBlockHolder.binding.tvLastDate.setText(Utils.getTimeAgo(model.getCreatedAt(), getContext()));
            } else if (model.getBlockType() == AppConstants.ExploreResultType.EVENT) {
                EventBlockHolder eventBlockHolder = (EventBlockHolder) holder;
                eventBlockHolder.setupData(model.getEvent());
                eventBlockHolder.binding.tvLastDate.setText(Utils.getTimeAgo(model.getCreatedAt(), getContext()));
            } else if (model.getBlockType() == AppConstants.ExploreResultType.ACTIVITY) {
                ActivityBlockHolder activityBlockHolder = (ActivityBlockHolder) holder;
                activityBlockHolder.setupData(model.getActivity());
                activityBlockHolder.binding.tvLastDate.setText(Utils.getTimeAgo(model.getCreatedAt(), getContext()));
            } else if (model.getBlockType() == AppConstants.ExploreResultType.SUGGESTED_VENUE) {
                SuggestedVenueViewHolder activityBlockHolder = (SuggestedVenueViewHolder) holder;
                activityBlockHolder.setupData(model.getVenus());
            } else if (model.getBlockType() == AppConstants.ExploreResultType.SUGGESTED_USER) {
                SuggestedUserViewHolder activityBlockHolder = (SuggestedUserViewHolder) holder;
                activityBlockHolder.setupData(model.getUsers());
            }

        }

        @Override
        public int getItemViewType(int position) {
            ExploreModel model = (ExploreModel) getItem(position);
            return model.getBlockType().getValue();
        }

        public class EventBlockHolder extends RecyclerView.ViewHolder {
            private final IteamSearchEventBinding binding;
            private final OfferPackagesAdapter<PackageModel> packageAdapter = new OfferPackagesAdapter<>();

            public EventBlockHolder(@NonNull View itemView) {
                super(itemView);
                binding = IteamSearchEventBinding.bind(itemView);
                binding.venueSubRecycler.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
                binding.venueSubRecycler.setAdapter(packageAdapter);
            }

            @SuppressLint("SetTextI18n")
            public void setupData(SearchEventModel model) {

                if (model != null) {
                    if (model.getVenue() != null) {
                        binding.venueContainer.setVenueDetail(model.getVenue());
                    }

                    if (model.getOrgData() != null) {
                        Graphics.loadRoundImage(model.getOrgData().getLogo(), binding.imgOrg);
                        binding.tvOrgName.setText(model.getOrgData().getName());
                        binding.tvWebsite.setText(model.getOrgData().getWebsite() != null ? model.getOrgData().getWebsite() : "");
                        binding.tvWebsite.setVisibility(model.getOrgData().getWebsite() != null ? View.VISIBLE : View.GONE);
                    }

                    binding.eventTitle.setText(model.getTitle());

                    Graphics.loadImage(model.getImage(), binding.ivCover);
                    if (!TextUtils.isEmpty(model.getDescription().trim())) {
                        binding.tvDescription.setText(model.getDescription());
                        binding.tvDescription.setVisibility(View.VISIBLE);
                        binding.tvDescription.post( () -> {
                            int lineCount = binding.tvDescription.getLineCount();
                            if (lineCount > 2) {
                                Utils.makeTextViewResizable( binding.tvDescription, 3, 3, ".. See More", true );
                            }
                        } );
                    } else {
                        binding.tvDescription.setVisibility(View.GONE);
                    }

                    Graphics.loadImage(model.getImage(), binding.ivCover);
                    setPackageDetalis(model.getPackages());

                    binding.endTime.setText(Utils.convertMainDateFormat(model.getEventTime()));
                    binding.startTime.setText(Utils.convertMainTimeFormat(model.getReservationTime()) + " - " + Utils.convertMainTimeFormat(model.getEventTime()));

                    binding.linearEvent.setOnClickListener(v -> {
                        if (model.getOrgData() != null) {
                            startActivity(new Intent(requireActivity(), EventOrganizerDetailsActivity.class).putExtra("org_id", model.getOrgId()).putExtra("type", "events_organizers").putExtra("name", model.getOrgData().getName()).putExtra("webSite", model.getOrgData().getWebsite()).putExtra("image", model.getOrgData().getLogo()));
                        }
                    });

                    itemView.setOnClickListener(v -> {
                        Intent intent = new Intent(requireActivity(), EventDetailsActivity.class);
                        if (model.getOrgData() != null) {
                            intent.putExtra("eventId", model.getId());
                            intent.putExtra("name", model.getOrgData().getName());
                            intent.putExtra("address", model.getOrgData().getWebsite());
                            intent.putExtra("image", model.getOrgData().getLogo());
                            intent.putExtra("venueModel", new Gson().toJson(model.getVenue()));
                            intent.putExtra("venueId", model.getOrgData().getId());
                            startActivity(intent);
                        }
                    });

                    binding.linearHeader.setOnClickListener(v -> {
                        if (model.getVenue() != null) {
                            Graphics.openVenueDetail(requireActivity(), model.getVenue().getId());
                        }
                    });

                    if (model.getEventTime() != null) {
                        if (Utils.isFutureDate(model.getEventTime(), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")) {
                            Utils.setTimer(model.getEventTime(), binding.countTimer);
                            binding.layoutTimer.setVisibility(View.VISIBLE);
                        } else {
                            binding.layoutTimer.setVisibility(View.INVISIBLE);
                        }
                    }
                }
            }

            private void setPackageDetalis(List<PackageModel> packages) {
                if (packages != null && !packages.isEmpty()) {
                    binding.venueSubRecycler.setVisibility(View.VISIBLE);
                    packageAdapter.updateData(packages);
                } else {
                    binding.venueSubRecycler.setVisibility(View.GONE);
                }

            }
        }

        public class ActivityBlockHolder extends RecyclerView.ViewHolder {
            private final ItemSearchActivityBinding binding;

            public ActivityBlockHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemSearchActivityBinding.bind(itemView);
            }

            public void setupData(ActivityDetailModel model) {

                binding.tvName.setText(model.getName());
                binding.tvAddress.setText(model.getDescription());

                binding.tvTitle.setText(model.getProvider().getName());
                binding.tvAddress.setText(model.getProvider().getAddress());

                Graphics.loadRoundImage(model.getProvider().getLogo(), binding.iconImg);
                Graphics.loadImage(model.getGalleries().get(0), binding.ivCover);

                binding.tvAED.setText(String.valueOf(model.getPrice()));
                binding.tvAED.setPaintFlags(binding.tvAED.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                binding.tvPrice.setText(String.valueOf(model.getPrice() - model.getPrice() * Integer.parseInt(model.getDiscount().split("%")[0]) / 100));

                if (model.getDiscount().equals("0")) {
                    binding.tvAED.setVisibility(View.GONE);
                } else {
                    binding.tvAED.setVisibility(View.VISIBLE);
                }

                binding.tvStartTime.setText(Utils.convertMainDateFormat(model.getStartDate()));
                binding.tvEndDate.setText(Utils.convertMainDateFormat(model.getEndDate()));

                binding.tvLastDate.setText(Utils.getTimeAgo(model.getCreatedAt(), getContext()));


                itemView.setOnClickListener(view -> {
                    Intent intent = new Intent(getActivity(), ActivityListDetail.class);
                    intent.putExtra("activityId", model.getId())
                            .putExtra("type", "activities")
                            .putExtra("name", model.getName())
                            .putExtra("image", model.getProvider().getLogo())
                            .putExtra("title", model.getProvider().getName())
                            .putExtra("address", model.getProvider().getAddress()
                            );
                    startActivity(intent);
                });

            }

        }

        public class OfferBlockHolder extends RecyclerView.ViewHolder {

            private final ItemExploreOfferBinding binding;

            public OfferBlockHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemExploreOfferBinding.bind(itemView);
            }

            private void setOfferInfo(OffersModel model) {
                binding.offerInfoView.setOfferDetail(model, requireActivity(), getChildFragmentManager());
                binding.offerButtonView.setupButtons(model, requireActivity(), getChildFragmentManager());
                binding.txtTitle.setText(model.getTitle());
                binding.tvDescription.setText(model.getDescription());
                binding.tvDescription.post( () -> {
                    int lineCount = binding.tvDescription.getLineCount();
                    if (lineCount > 2) {
                        Utils.makeTextViewResizable( binding.tvDescription, 3, 3, ".. See More", true );
                    }
                } );
            }

            public void setupData(OffersModel model) {
                setOfferInfo(model);
                if (model.getVenue() != null) {
                    binding.venueContainer.setVenueDetail(model.getVenue());
                }

                binding.getRoot().setOnClickListener(v -> {
                    Utils.preventDoubleClick(v);
                    OfferDetailBottomSheet dialog = new OfferDetailBottomSheet();
                    dialog.offerId = model.getId();
                    dialog.show(getChildFragmentManager(), "");
                });

                binding.layoutVenueDetail.setOnClickListener(v -> {
                    Utils.preventDoubleClick(v);
                    if (context != null) {
                        startActivity(new Intent(context, VenueActivity.class).putExtra("venueId", model.getVenue().getId()));
                    }
                });

                binding.iconMenu.setOnClickListener(view -> {
                    Utils.preventDoubleClick(view);
                    ArrayList<String> data = new ArrayList<>();
                    data.add("Add to Bucketlist");
                    if (model.getVenue().isIsFollowing()) {
                        data.add("UnFollow");
                    } else {
                        data.add("Follow");
                    }
                    data.add("Share Venue");
                    data.add("Share Offer");
                    data.add(model.getVenue().isRecommendation() ? "Remove recommendation" : "Add recommendation");
                    Graphics.showActionSheet(getContext(), model.getVenue().getName(), data, (data1, position1) -> {
                        switch (position1) {
                            case 0:
                                BucketListBottomSheet dialog = new BucketListBottomSheet();
                                dialog.offerId = model.getId();
                                dialog.show(getChildFragmentManager(), "");
                                break;
                            case 1:
                                reqFollowUnFollow(model.getVenue(), (success, error) -> {
                                    notifyDataSetChanged();
                                });
                                break;
                            case 2:
                                startActivity(new Intent(requireActivity(), VenueShareActivity.class).putExtra("venue", new Gson().toJson(model.getVenue()))
                                        .putExtra("type", "venue"));
                                break;
                            case 3:
                                startActivity(new Intent(requireActivity(), VenueShareActivity.class).putExtra("offer", new Gson().toJson(model))
                                        .putExtra("type", "offer"));
                                break;
                            case 4:
                                reqRecommendation(model.getVenue().getId(), model.getVenue());
                                break;
                        }

                    });
                });
            }
        }

        public class SuggestedUserViewHolder extends RecyclerView.ViewHolder {
            private final ItemHomeSuggestedUserRecyclerBinding mBinding;

            public SuggestedUserViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemHomeSuggestedUserRecyclerBinding.bind(itemView);
            }

            public void setupData(List<UserDetailModel> suggestedUsers) {
                mBinding.suggestedUserView.setSuggestedUser(suggestedUsers, requireActivity(), getChildFragmentManager(), (success, error) -> {
                });

            }
        }

        public class SuggestedVenueViewHolder extends RecyclerView.ViewHolder {
            private final ItemExploreVenueSuggestionRecyclerBinding mBinding;

            public SuggestedVenueViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemExploreVenueSuggestionRecyclerBinding.bind(itemView);

            }

            public void setupData(List<VenueObjectModel> suggestedVenue) {
                mBinding.suggestedVenue.setSuggestedVenue(suggestedVenue, requireActivity(), getChildFragmentManager(), (success, error) -> {
                });
            }
        }
    }

    public class PreferencesListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.filter_tag_item));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            AppSettingTitelCommonModel model = (AppSettingTitelCommonModel) getItem(position);
            boolean isLastItem = position == getItemCount() - 1;
            viewHolder.binding.iconText.setText(model.getTitle());

            viewHolder.binding.linearMainView.setOnClickListener(view -> {
                commonModels.remove(model);
                listAdapter.notifyDataSetChanged();
                dateBefore = "";
                tabIds = commonModels.stream().map(AppSettingTitelCommonModel::getId).collect(Collectors.toList());

                requestCommanExplore(false);
                if (commonModels.isEmpty()) {
                    binding.prefrenceConstraint.setVisibility(View.GONE);
                }
            });

            viewHolder.binding.linearMainView.setBackground(requireActivity().getResources().getDrawable(R.drawable.filter_tag_bg));

            viewHolder.binding.imgRemove.setOnClickListener(v -> {
                commonModels.remove(model);
                listAdapter.notifyDataSetChanged();
                dateBefore = "";
                tabIds = commonModels.stream().map(AppSettingTitelCommonModel::getId).collect(Collectors.toList());
                requestCommanExplore(false);
                if (commonModels.isEmpty()) {
                    binding.prefrenceConstraint.setVisibility(View.GONE);
                }
            });

            if (isLastItem) {
                int marginBottom = Utils.getMarginRight(holder.itemView.getContext(), 0.02f);
                Utils.setRightMargin(holder.itemView, marginBottom);
            } else {
                Utils.setRightMargin(holder.itemView, 0);
            }

        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            private final FilterTagItemBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = FilterTagItemBinding.bind(itemView);
            }
        }
    }


    // endregion
    // --------------------------------------

}