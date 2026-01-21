package com.whosin.app.ui.controller.promoter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.asynclayoutinflater.view.AsyncLayoutInflater;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.HorizontalSpaceItemDecoration;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.ItemUserRingBinding;
import com.whosin.app.databinding.UserInvitedItemBinding;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.InvitedUserModel;
import com.whosin.app.service.models.PromoterChatModel;
import com.whosin.app.service.models.PromoterCirclesModel;
import com.whosin.app.service.models.PromoterEventModel;
import com.whosin.app.ui.activites.CmProfile.CmPublicProfileActivity;
import com.whosin.app.ui.activites.Promoter.CirclesDetailActivity;
import com.whosin.app.ui.activites.Promoter.EventUserApproveRejectBottomSheet;
import com.whosin.app.ui.fragment.Promoter.PromoterBootmSheets.PromoterChatUserBottomSheetFragment;

import java.util.List;


public class PromoterEventUserInviteView extends ConstraintLayout {
    private UserInvitedItemBinding binding;
    private Context context;
    private Activity activity;
    private FragmentManager supportFragmentManager;
    private List<InvitedUserModel> myRingList;
    private String eventTitleText;
    public int maxInvitee = 0;
    private boolean isCircleSetData = false;
    private boolean isInvitedUser = false;
    private int circleImageViewHeight;
    private int circleImageViewWidth;
    private boolean isCircleNotClickable = false;
    private boolean isSeeAllShow = false;

    public int totalInMembers = 0;

    public int getTotalInvitedUsers = 0;

    public PromoterEventModel promoterEventModel = null;
    public boolean isFormInterestedMembers = false;
    public boolean isOpenProfile = false;
    public boolean isFromHistoryEvent = false;
    public boolean isFromUsersInvited = false;
    public boolean isFromPlusOneUser = false;
    public CommanCallback<Boolean> callback;
    public boolean isCancellesUser = false;
    public boolean isFormEventHistory = false;

    public PromoterChatModel model;
    private CustomMyRingAdapter<InvitedUserModel> customMyRingAdapter;

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------


    public PromoterEventUserInviteView(Context context) {
        this(context, null);
    }

    public PromoterEventUserInviteView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressLint("SetTextI18n")
    public PromoterEventUserInviteView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PromoterEventUserInviteView, 0, 0);
        String titleText = a.getString(R.styleable.PromoterEventUserInviteView_eventTitleText);
        int titleTextStyleResId = a.getResourceId(R.styleable.PromoterEventUserInviteView_promoterTitleTextStyle, 0);
        int tvNameTextStyleResId = a.getResourceId(R.styleable.PromoterEventUserInviteView_tvNameTextStyle, 0);
        isCircleNotClickable = a.getBoolean(R.styleable.PromoterEventUserInviteView_isCircleNotClickable, false);
        isSeeAllShow = a.getBoolean(R.styleable.PromoterEventUserInviteView_isSeeAllShow, false);

        int backgroundColor = a.getColor(R.styleable.PromoterEventUserInviteView_backgroundColor, ContextCompat.getColor(context, R.color.features_bg));
        LayoutInflater.from(context).inflate(R.layout.offer_info_view_loader, this, true);

        circleImageViewWidth = a.getDimensionPixelSize(R.styleable.PromoterEventUserInviteView_circleImageViewWidth, getResources().getDimensionPixelSize(com.intuit.ssp.R.dimen._38ssp));
        circleImageViewHeight = a.getDimensionPixelSize(R.styleable.PromoterEventUserInviteView_circleImageViewHeight, getResources().getDimensionPixelSize(com.intuit.ssp.R.dimen._38ssp));
        a.recycle();


        AsyncLayoutInflater asyncLayoutInflater = new AsyncLayoutInflater(context);
        asyncLayoutInflater.inflate(R.layout.user_invited_item, this, (view, resid, parent) -> {
            binding = UserInvitedItemBinding.bind(view);
            setupRecycleHorizontalManager(binding.eventUsersRecycler);


            customMyRingAdapter = new CustomMyRingAdapter<>(supportFragmentManager, tvNameTextStyleResId);
            binding.eventUsersRecycler.setAdapter(customMyRingAdapter);


            if (myRingList != null) {
                activity.runOnUiThread(() -> customMyRingAdapter.updateData(myRingList));
            }
            if (binding != null) {
                binding.userTitle.setText(titleText);
                binding.getRoot().setBackgroundColor(backgroundColor);

                if (titleTextStyleResId != 0) {
                    binding.userTitle.setTextAppearance(context, titleTextStyleResId);
                }

                if (isCircleSetData || isFormInterestedMembers || isCancellesUser) {
                    binding.textCount.setVisibility(View.GONE);
                } else {
                    binding.textCount.setVisibility(View.VISIBLE);

                    if (isInvitedUser) {
                        int count = maxInvitee - totalInMembers;
                        binding.textCount.setText(String.format("(%d remaining)", count));
                        binding.userTitle.setText(String.format("%d spots", maxInvitee));
                    } else {
                        binding.textCount.setText(String.format("(%d)", getTotalInvitedUsers));
                    }

                }

                if (eventTitleText != null && !eventTitleText.isEmpty()) {
                    binding.userTitle.setText(eventTitleText);
                }
                if (myRingList != null && !myRingList.isEmpty() && isSeeAllShow) {
                    binding.tvSeeAll.setVisibility(View.VISIBLE);

                } else {
                    binding.tvSeeAll.setVisibility(View.GONE);
                }
            }

            binding.tvSeeAll.setOnClickListener(view1 -> {
                PromoterChatUserBottomSheetFragment bottomSheet = new PromoterChatUserBottomSheetFragment();
                bottomSheet.chatModel = model;
                bottomSheet.callback = data -> {
                    if (data) {
                        callback.onReceive(true);
                    }
                };
                bottomSheet.show(supportFragmentManager, "");
            });
            PromoterEventUserInviteView.this.removeAllViews();
            PromoterEventUserInviteView.this.addView(view);
        });


    }

    @SuppressLint("DefaultLocale")
    public void setUpData(List<InvitedUserModel> list, Activity activity, FragmentManager supportFragmentManager, boolean isCircle, boolean isInvitedUser) {
        this.myRingList = list;
        this.activity = activity;
        this.isCircleSetData = isCircle;
        this.isInvitedUser = isInvitedUser;
        this.supportFragmentManager = supportFragmentManager;

        if (binding == null) {
            return;
        }
        customMyRingAdapter.updateData(myRingList);


        if (isCircleSetData || isFormInterestedMembers || isCancellesUser || isFromPlusOneUser) {
            binding.textCount.setVisibility(View.GONE);
        } else {
            binding.textCount.setVisibility(View.VISIBLE);

            if (isInvitedUser) {
                int count = maxInvitee - totalInMembers;
                binding.textCount.setText(String.format("(%d remaining)", count));
                binding.userTitle.setText(String.format("%d spots", maxInvitee));
            } else {
                binding.textCount.setText(String.format("(%d)", getTotalInvitedUsers));
            }
        }


    }

    // endregion
    // --------------------------------------
    // region Public
    // --------------------------------------

    public void setTitle(String title){
        if (binding != null){
            binding.userTitle.setText(title);
        }
    }

    private void setupRecycleHorizontalManager(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        int spacing = getResources().getDimensionPixelSize(com.intuit.ssp.R.dimen._5ssp);
        recyclerView.addItemDecoration(new HorizontalSpaceItemDecoration(spacing));
    }


    private class CustomMyRingAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        private final FragmentManager fragmentManager;
        private int tvNameTextStyleResId;

        public CustomMyRingAdapter(FragmentManager fragmentManager, int tvNameTextStyleResId) {
            this.fragmentManager = fragmentManager;
            this.tvNameTextStyleResId = tvNameTextStyleResId;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_user_ring));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;

            InvitedUserModel model = (InvitedUserModel) getItem(position);
            if (model == null) {
                return;
            }

            if (isCircleSetData) {
                viewHolder.mBinding.tvName.setText(model.getTitle());
                Graphics.loadImageWithFirstLetter(model.getAvatar(), viewHolder.mBinding.image, model.getTitle());
            } else {
                viewHolder.mBinding.tvName.setText(model.getFirstName());
                Graphics.loadImageWithFirstLetter(model.getImage(), viewHolder.mBinding.image, model.getFirstName());
            }


            viewHolder.mBinding.getRoot().setOnClickListener(v -> {
                if (isCircleNotClickable || isFromPlusOneUser) {
                    return;
                }
                if (isCancellesUser) {
                    if (!model.getUserId().equals(SessionManager.shared.getUser().getId())) {
                        if (isOpenProfile) {
                            activity.startActivity(new Intent(activity, CmPublicProfileActivity.class).putExtra("promoterUserId", model.getUserId()));
                        } else {
                            openApproveRejectSheet();
                        }
                    }
                    return;
                }
                if (!model.getUserId().equals(SessionManager.shared.getUser().getId())) {
                    if (isOpenProfile) {
                        String id = isFromHistoryEvent ? model.getUserId() : model.getId();
                        activity.startActivity(new Intent(activity, CmPublicProfileActivity.class).putExtra("promoterUserId", id));
                    } else {
                        openApproveRejectSheet();
                    }
                } else {
                    if (isCircleSetData) {
                        openCirclesDetailActivity(model);
                    }
                }

            });


            if (tvNameTextStyleResId != 0) {
                viewHolder.mBinding.tvName.setTextAppearance(viewHolder.itemView.getContext(), tvNameTextStyleResId);
            }
            ViewGroup.LayoutParams layoutParams = viewHolder.mBinding.image.getLayoutParams();
            layoutParams.width = circleImageViewWidth;
            layoutParams.height = circleImageViewHeight;
            viewHolder.mBinding.image.setLayoutParams(layoutParams);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final ItemUserRingBinding mBinding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemUserRingBinding.bind(itemView);
            }
        }


        private void openApproveRejectSheet() {
            EventUserApproveRejectBottomSheet bottomSheet = new EventUserApproveRejectBottomSheet();
            bottomSheet.eventId = promoterEventModel.getId();
            bottomSheet.modelList = myRingList;
            bottomSheet.promoterEventModel = promoterEventModel;
            bottomSheet.isFormInterestedMembers = isFormInterestedMembers;
            bottomSheet.isFromUserInvited = isFromUsersInvited;
            bottomSheet.isFromPlusOneUsers = isFromPlusOneUser;
            bottomSheet.isFormEventHistory = isFormEventHistory;
            bottomSheet.isCancelled = isCancellesUser;
            bottomSheet.callback = data -> {
                if (callback != null && data) {
                    callback.onReceive(true);
                }
            };
            bottomSheet.show(supportFragmentManager, "");
        }

        private void openCirclesDetailActivity(InvitedUserModel model) {
            PromoterCirclesModel promoterCirclesModel = new PromoterCirclesModel();
            promoterCirclesModel.setId(model.getId());
            promoterCirclesModel.setTitle(model.getTitle());
            promoterCirclesModel.setAvatar(model.getAvatar());
            promoterCirclesModel.setDescription(model.getDescription());
            Intent intent = new Intent(activity, CirclesDetailActivity.class);
            intent.putExtra("promoterModel", new Gson().toJson(promoterCirclesModel));
            activity.startActivity(intent);
        }
    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------


    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------


}
