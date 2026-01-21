package com.whosin.app.ui.fragment.Promoter;

import static com.whosin.app.comman.AppDelegate.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
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
import com.whosin.app.databinding.FragmentMyProfileBinding;
import com.whosin.app.databinding.ItemMyProfileMyRingViewBinding;
import com.whosin.app.databinding.ItemPomoterProfileMyCirclesViewBinding;
import com.whosin.app.databinding.ItemPromoterProfileVenueItemBinding;
import com.whosin.app.databinding.ItemPromoterRatingViewBinding;
import com.whosin.app.databinding.ItemRatingReviewRecyclerBinding;
import com.whosin.app.databinding.ItemSaveDraftHolderItemBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.CheckUserSession;
import com.whosin.app.service.manager.PromoterProfileManager;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.ContactListModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.CurrentUserRatingModel;
import com.whosin.app.service.models.MyPlanContainerModel;
import com.whosin.app.service.models.PromoterAddRingModel;
import com.whosin.app.service.models.PromoterCirclesModel;
import com.whosin.app.service.models.PromoterEventModel;
import com.whosin.app.service.models.PromoterProfileModel;
import com.whosin.app.service.models.ReviewReplayModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.Profile.ProfileFullScreenImageActivity;
import com.whosin.app.ui.activites.Promoter.PromoterActivity;
import com.whosin.app.ui.activites.Promoter.PromoterCreateEventActivity;
import com.whosin.app.ui.activites.Promoter.PromoterMyProfile;
import com.whosin.app.ui.activites.Promoter.ReviewReplayDialog;
import com.whosin.app.ui.activites.home.activity.SeeAllRatingReviewActivity;
import com.whosin.app.ui.activites.venue.Bucket.ContactShareBottomSheet;
import com.whosin.app.ui.adapter.SaveDraftEventAdapter;
import com.whosin.app.ui.fragment.ProfileFragment;
import com.whosin.app.ui.fragment.comman.BaseFragment;
import com.whosin.app.ui.fragment.reviewSheet.UserFullReviewSheet;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class PromoterMyProfileFragment extends BaseFragment {

    private FragmentMyProfileBinding binding;

    private final MyProfileAdapter<MyPlanContainerModel> profileAdapter = new MyProfileAdapter<>();

    public PromoterProfileModel promoterProfileModel;

    private boolean blurViewVisible = false;


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------


    @Override
    public void initUi(View view) {

        binding = FragmentMyProfileBinding.bind(view);

        applyTranslations();

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        binding.recyclerView.setAdapter(profileAdapter);

        binding.swipeRefreshLayout.setProgressViewOffset(false,0,220);


        if (SessionManager.shared.isPromoterSubAdmin()) binding.editBtn.setVisibility(View.GONE);


        CheckUserSession.checkSessionAndProceed(requireActivity(), () -> requestPromoterGetProfile(true));



    }

    @Override
    public void setListeners() {


        binding.swipeRefreshLayout.setOnRefreshListener(() -> requestPromoterGetProfile(false));

        binding.ivClose.setOnClickListener(v -> {
            PromoterProfileManager.shared.setProfileCallBack.onReceive(true);
        });


//        binding.profileswitchBtn.setOnClickListener(v -> {
//            startActivity(new Intent(activity, ProfileFragment.class));
//        });

        binding.eventConstraint.setOnClickListener(view -> {
            startActivity(new Intent(activity, PromoterCreateEventActivity.class));
            PromoterProfileManager.shared.isEventEdit = false;
        });

        binding.constraintAddToRing.setOnClickListener(view -> {
            Utils.preventDoubleClick(view);
            ContactShareBottomSheet contactDialog = new ContactShareBottomSheet();
            contactDialog.isChangeTitle = true;
            contactDialog.defaultUsersList = PromoterProfileManager.shared.promoterProfileModel.getRings().getList().stream()
                    .map(UserDetailModel::getUserId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            contactDialog.myWallet = true;
            contactDialog.callbackForAddToRing = data -> {
                if (data != null) {
                    if (!data.getId().equals(SessionManager.shared.getUser().getId())) {
                        requestPromoterAddToRing(data.getId());
                    }
                }
            };
            contactDialog.show(getChildFragmentManager(), "1");
        });



        binding.editBtn.setOnClickListener(v -> {
            PromoterProfileModel promoterProfileModel = PromoterProfileManager.shared.promoterProfileModel;
            if (promoterProfileModel == null) {
                return;
            }
            startActivity(new Intent(activity, PromoterActivity.class)
                    .putExtra("isPromoter", true)
                    .putExtra("isEditProfile", true)
                    .putExtra("userProfileModel", new Gson().toJson(promoterProfileModel.getProfile())));
        });


        binding.nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            int scrollDistance = scrollY - oldScrollY;
            if (Math.abs(scrollDistance) > 10) {
                if (scrollDistance > 0) {
                    if (!blurViewVisible) {
                        binding.headerBlurView.setBlurEnabled(true);
                        Graphics.applyBlurEffect(requireActivity(), binding.headerBlurView);
                        binding.headerLayout.setVisibility(View.VISIBLE);
                        String name = promoterProfileModel.getProfile().getFirstName() + " " + promoterProfileModel.getProfile().getLastName();
                        binding.headertitle.setText(name);
                        Graphics.loadImageWithFirstLetter(promoterProfileModel.getProfile().getImage(), binding.headerIv, name);
                        blurViewVisible = true;
                    }
                } else {
                    if (scrollY <= 10) {
                        if (blurViewVisible) {
                            binding.headerBlurView.setBackgroundColor(Color.TRANSPARENT);
                            binding.headerBlurView.setBackground(null);
                            binding.headerBlurView.setBlurEnabled(false);
                            binding.headerLayout.setVisibility(View.GONE);
                            blurViewVisible = false;
                        }
                    }
                }
            }
        });

        binding.ivShare.setOnClickListener(v -> {
            Utils.preventDoubleClick(v);
            if (promoterProfileModel == null) {return;}
            if (promoterProfileModel.getProfile() == null){return;}

            Utils.generateDynamicLinks(activity, promoterProfileModel.getProfile());
        });


        binding.imageProfile.setOnClickListener(v -> {
            Utils.preventDoubleClick(v);
            Intent intent = new Intent( activity, ProfileFullScreenImageActivity.class );
            intent.putExtra( ProfileFullScreenImageActivity.EXTRA_IMAGE_URL, promoterProfileModel.getProfile().getImage());
            startActivity( intent );
        });
    }

    @Override
    public void populateData(boolean getDataFromServer) {

    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_my_profile;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(PromoterCirclesModel model) {
        CheckUserSession.checkSessionAndProceed(requireActivity(), () -> requestPromoterGetProfile(true));
    }

    @Override
    protected Map<View, String> getTranslationMap() {
        Map<View, String> map = new HashMap<>();
        map.put(binding.promoterText, "promoter");
        map.put(binding.tvEditProfile, "edit_profile");
        map.put(binding.addToRingTitle, "add_to_ring");
        map.put(binding.createEventTItle, "create_event");
        return map;
    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------


    private void setUpData() {

        if (promoterProfileModel == null){return;}

        String name = promoterProfileModel.getProfile().getFirstName() + " " + promoterProfileModel.getProfile().getLastName();
        binding.tvUserName.setText(name);
        binding.tvBio.setText(promoterProfileModel.getProfile().getBio());

        Graphics.loadImageWithFirstLetter(promoterProfileModel.getProfile().getImage(), binding.imageProfile, name);

        List<MyPlanContainerModel> tmpList = new ArrayList<>();

        tmpList.add(new MyPlanContainerModel("1"));
        tmpList.add(new MyPlanContainerModel("2"));
        tmpList.add(new MyPlanContainerModel("3"));
        tmpList.add(new MyPlanContainerModel("4"));
        tmpList.add(new MyPlanContainerModel("5"));

        profileAdapter.updateData(tmpList);
    }





    // endregion
    // --------------------------------------
    // region Public
    // --------------------------------------



    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------


    private void requestPromoterGetProfile(boolean isShowProgressBar) {
        if (isShowProgressBar) {
            showProgress();
        } else {
            binding.swipeRefreshLayout.setRefreshing(true);
        }
        DataService.shared(requireActivity()).requestPromoterGetProfile(new RestCallback<ContainerModel<PromoterProfileModel>>(this) {
            @Override
            public void result(ContainerModel<PromoterProfileModel> model, String error) {
                hideProgress();
                binding.swipeRefreshLayout.setRefreshing(false);
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(requireActivity(), error, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (model.getData() != null) {
                    PromoterProfileManager.shared.promoterProfileModel = model.getData();
                    promoterProfileModel = model.getData();
//                    PromoterProfileManager.shared.callbackForHeader.onReceive(model.getData().getProfile());
                    SessionManager.shared.savePromoterUserData(model.getData(),requireContext());
                    setUpData();
                    EventBus.getDefault().post(model.getData().getProfile());
                }
            }
        });
    }


    private void requestPromoterAddToRing(String memberId) {
        DataService.shared(requireActivity()).requestPromoterAddToRing(memberId, new RestCallback<ContainerModel<PromoterAddRingModel>>(this) {
            @Override
            public void result(ContainerModel<PromoterAddRingModel> model, String error) {
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(requireActivity(), error, Toast.LENGTH_SHORT).show();
                    return;
                }

                Alerter.create( requireActivity() ).setText(getValue("request_sent_successfully")).setTextAppearance( R.style.AlerterText ).setTitleAppearance( R.style.AlerterTitle ).setBackgroundColorRes( R.color.AlerterSuccessBg ).hideIcon().show();

            }
        });
    }



    // --------------------------------------
    // region Adapter
    // --------------------------------------

    public class MyProfileAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());

            switch (viewType) {
                case 1:
                    return new SaveDraftEventHolder(inflater.inflate(R.layout.item_save_draft_holder_item, parent, false));
                case 2:
                    return new MyRingHolder(inflater.inflate(R.layout.item_my_profile_my_ring_view, parent, false));
                case 3:
                    return new MyCirclesHolder(inflater.inflate(R.layout.item_pomoter_profile_my_circles_view, parent, false));
                case 4:
                    return new MyVenuesHolder(inflater.inflate(R.layout.item_promoter_profile_venue_item, parent, false));
                default:
                    return new RatingReviewsHolder(inflater.inflate(R.layout.item_promoter_rating_view, parent, false));
            }

        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            boolean isLastItem = position == getItemCount() - 1;
            MyPlanContainerModel model = (MyPlanContainerModel) getItem(position);


            if (getItemViewType(position) == 1) {
                ((SaveDraftEventHolder) holder).setupData();
            } else if (getItemViewType(position) == 2) {
                ((MyRingHolder) holder).setupData();
            } else if (getItemViewType(position) == 3) {
                MyCirclesHolder viewHolder = (MyCirclesHolder) holder;
                viewHolder.setupData();
            } else if (getItemViewType(position) == 4) {
                MyVenuesHolder viewHolder = (MyVenuesHolder) holder;
                viewHolder.setupData();
            } else {
                RatingReviewsHolder viewHolder = (RatingReviewsHolder) holder;
                viewHolder.setupData();
            }


//            if (isLastItem) {
//                int marginBottom = Utils.getMarginBottom(holder.itemView.getContext(), 0.18f);
//                Utils.setBottomMargin(holder.itemView, marginBottom);
//            } else {
//                Utils.setBottomMargin(holder.itemView, 0);
//            }
        }


        public int getItemViewType(int position) {
            MyPlanContainerModel model = (MyPlanContainerModel) getItem(position);
            switch (model.getId()) {
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


        public class MyRingHolder extends RecyclerView.ViewHolder {
            private final ItemMyProfileMyRingViewBinding mBinding;

            public MyRingHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemMyProfileMyRingViewBinding.bind(itemView);

            }

            private void setupData() {
                mBinding.customeRing.ringMemberCout = promoterProfileModel.getRings().getCount();
                mBinding.customeRing.setUpData(promoterProfileModel.getRings().getList(), activity, getChildFragmentManager());
                mBinding.maleCount.setText(String.valueOf(promoterProfileModel.getRings().getMaleCount()));
                mBinding.femaleCount.setText(String.valueOf(promoterProfileModel.getRings().getFemaleCount()));
                mBinding.otherGenderCount.setText(String.valueOf(promoterProfileModel.getRings().getPreferNotToSay()));

            }
        }

        public class MyCirclesHolder extends RecyclerView.ViewHolder {

            private final ItemPomoterProfileMyCirclesViewBinding mBinding;

            public MyCirclesHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemPomoterProfileMyCirclesViewBinding.bind(itemView);

            }

            private void setupData() {
                mBinding.customCircale.setUpData(promoterProfileModel.getCircles(), activity, getChildFragmentManager());
            }

        }

        public class RatingReviewsHolder extends RecyclerView.ViewHolder {
            private final ItemPromoterRatingViewBinding mBinding;

            private final RatingReviewAdapter<CurrentUserRatingModel> ratingReviewAdapter = new RatingReviewAdapter<>();


            public RatingReviewsHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemPromoterRatingViewBinding.bind(itemView);
                mBinding.ratingReviewRecycler.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
                mBinding.ratingReviewRecycler.setAdapter(ratingReviewAdapter);

                mBinding.rating.setOnTouchListener((v, event) -> true);


            }

            private void setupData() {
                if (!promoterProfileModel.getReviewModel().getReviews().isEmpty()) {
                    mBinding.rating.setRating(promoterProfileModel.getReviewModel().getAvgRating());
                    ratingReviewAdapter.updateData(promoterProfileModel.getReviewModel().getReviews());
                } else {
                    mBinding.ratingReviewRecycler.setVisibility(View.GONE);
                    mBinding.tvSeeAll.setVisibility(View.GONE);
                    mBinding.tvRate.setVisibility(View.GONE);
                    mBinding.rating.setVisibility(View.GONE);
                }

                mBinding.tvSeeAll.setOnClickListener(view -> startActivity(new Intent(activity, SeeAllRatingReviewActivity.class)
                        .putExtra("id", promoterProfileModel.getProfile().getUserId())
                        .putExtra("type", "promoter")
                        .putExtra("isMyProfile", true)
                        .putExtra("start", promoterProfileModel.getReviewModel().getAvgRating())
                        .putExtra("currentUserRating", new Gson().toJson(promoterProfileModel.getReviewModel().getCurrentUserRating()))));
            }
        }


        public class MyVenuesHolder extends RecyclerView.ViewHolder {
            private final ItemPromoterProfileVenueItemBinding mBinding;

            public MyVenuesHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemPromoterProfileVenueItemBinding.bind(itemView);
            }

            private void setupData() {
                if (promoterProfileModel.getVenues().getList() != null && !promoterProfileModel.getVenues().getList().isEmpty()) {
                    mBinding.myVenue.setVisibility(View.VISIBLE);
                    mBinding.myVenue.setUpData(promoterProfileModel.getVenues().getList(), activity, getChildFragmentManager());
                } else {
                    mBinding.myVenue.setVisibility(View.GONE);
                }
            }
        }

        public class SaveDraftEventHolder extends RecyclerView.ViewHolder {
            private final ItemSaveDraftHolderItemBinding mBinding;

            private final SaveDraftEventAdapter<PromoterEventModel> saveDraftAdapter;


            public SaveDraftEventHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemSaveDraftHolderItemBinding.bind(itemView);
                saveDraftAdapter = new SaveDraftEventAdapter<>(requireActivity());
                mBinding.saveToDraftRecycleView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
                mBinding.saveToDraftRecycleView.setAdapter(saveDraftAdapter);
            }

            private void setupData() {
                List<PromoterEventModel> tmpList = PromoterProfileManager.getSaveToDraftEventList();
                if (!tmpList.isEmpty()) {
                    mBinding.saveToDraftRecycleView.setVisibility(View.VISIBLE);
                    saveDraftAdapter.updateData(tmpList);
                } else {
                    mBinding.saveToDraftRecycleView.setVisibility(View.GONE);
                }
            }
        }


    }


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
            boolean isLastItem = position == getItemCount() - 1;

            viewHolder.mBinding.txtReview.setText(model.getReview());
            viewHolder.mBinding.txtReply.setText(model.getReply());
            viewHolder.mBinding.rating.setRating(model.getStars());

            Optional<ContactListModel> matchingUser = promoterProfileModel.getReviewModel().getUsers().stream()
                    .filter(user -> model.getUserId().equals(user.getId()))
                    .findFirst();

            if (matchingUser.isPresent()) {
                ContactListModel user = matchingUser.get();
                viewHolder.mBinding.txtTitle.setText(user.getFullName());
                Graphics.loadImageWithFirstLetter(user.getImage(), viewHolder.mBinding.ivRating, user.getFullName());
            }

            Graphics.loadImageWithFirstLetter(SessionManager.shared.getUser().getImage(), viewHolder.mBinding.image, SessionManager.shared.getUser().getFullName());
            viewHolder.mBinding.txtDate.setText(Utils.convertMainDateFormatReview(model.getCreatedAt()));
            viewHolder.mBinding.rating.setRating(model.getStars());

            if (model.getReply() != null && !model.getReply().isEmpty()) {
                viewHolder.mBinding.layoutReview.setBackgroundColor(Color.TRANSPARENT);
                viewHolder.mBinding.tvTitle.setText(SessionManager.shared.getUser().getFullName());
                viewHolder.mBinding.tvTitle.setPadding(0, 0, 0, 0);
                viewHolder.mBinding.txtReply.setVisibility(View.VISIBLE);
                viewHolder.mBinding.linearEditDelete.setVisibility(View.VISIBLE);
            } else {
                viewHolder.mBinding.txtReply.setVisibility(View.INVISIBLE);
                viewHolder.mBinding.linearEditDelete.setVisibility(View.GONE);
                viewHolder.mBinding.tvTitle.setText("Reply");
                viewHolder.mBinding.layoutReview.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.brand_pink));
                viewHolder.mBinding.tvTitle.setPadding(10, 1, 10, 1);
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
                matchingUser.ifPresent(userDetailModel -> reviewSheet.contactListModel = userDetailModel);
                reviewSheet.currentUserRatingModel = model;
                reviewSheet.callback = data -> {
                    if (data)PromoterProfileManager.shared.setProfileCallBack.onReceive(true);
                };
                reviewSheet.show(getChildFragmentManager(),"");
            });

            viewHolder.mBinding.layoutReview.setOnClickListener(v -> {
                Utils.preventDoubleClick(v);
                if (model.getReply().isEmpty()) {
                    ReviewReplayDialog dialog = new ReviewReplayDialog();
                    dialog.replayId = model.getId();
                    dialog.isPromoter = true;
                    dialog.show(getChildFragmentManager(), "");
                }
            });

            viewHolder.mBinding.ivEdit.setOnClickListener(v -> {
                Utils.preventDoubleClick(v);
                ReviewReplayDialog dialog = new ReviewReplayDialog();
                dialog.replayId = model.getId();
                dialog.replay = model.getReply();
                dialog.isPromoter = true;
                dialog.show(getChildFragmentManager(), "");
            });

            viewHolder.mBinding.ivDelete.setOnClickListener(v -> {
                Utils.preventDoubleClick(v);
                Graphics.showAlertDialogWithOkCancel(requireActivity(), getString(R.string.app_name), "Are you sure you want to delete the review?",
                        "Yes,delete", "Cancel", aBoolean -> {
                            if (aBoolean) {
                                requestDeleteReview(model.getId());
                            }
                        });
            });
        }

        private void requestDeleteReview(String replayId) {
            Graphics.showProgress(requireActivity());
            DataService.shared(requireActivity()).requestDeleteReview(replayId, new RestCallback<ContainerModel<ReviewReplayModel>>(null) {
                @Override
                public void result(ContainerModel<ReviewReplayModel> model, String error) {
                    Graphics.hideProgress(requireActivity());
                    if (!Utils.isNullOrEmpty(error) || model == null) {
                        Toast.makeText(requireActivity(), error, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    EventBus.getDefault().post(new PromoterCirclesModel());
                }
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