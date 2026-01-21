package com.whosin.app.ui.controller.promoter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.asynclayoutinflater.view.AsyncLayoutInflater;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.ItemMyRingViewBinding;
import com.whosin.app.databinding.ItemPlusViewBinding;
import com.whosin.app.databinding.LayoutMyRingViewBinding;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.ui.activites.CmProfile.CmPublicProfileActivity;
import com.whosin.app.ui.activites.Promoter.MyRingDetailActivity;

import java.util.List;

public class PromoterMyRingView extends ConstraintLayout {
    private LayoutMyRingViewBinding binding;
    private Context context;
    private Activity activity;
    private FragmentManager supportFragmentManager;
    private List<UserDetailModel> myRingList;
    private boolean isPublicProfile = false;
    public int ringMemberCout = 0;
    public boolean isFromSubAdminPromoter = false;
    private CustomMyRingAdapter<UserDetailModel> customMyRingAdapter;

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    public PromoterMyRingView(Context context) {
        this(context, null);
    }

    public PromoterMyRingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressLint("DefaultLocale")
    public PromoterMyRingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.offer_info_view_loader, this, true);

        AsyncLayoutInflater asyncLayoutInflater = new AsyncLayoutInflater(context);
        asyncLayoutInflater.inflate(R.layout.layout_my_ring_view, this, (view, resid, parent) -> {
            binding = LayoutMyRingViewBinding.bind(view);

            binding.tvMyRingsTitle.setText(Utils.getLangValue("promoter_my_rings"));
            binding.tvSeeAllTitle.setText(Utils.getLangValue("see_all"));
            binding.emptyPlaceHolderView.setEmptyPlaceTxtTitle(Utils.getLangValue("ring_list_empty"));

            customMyRingAdapter = new CustomMyRingAdapter<>(supportFragmentManager);
            binding.myRingRecycler.setLayoutManager(new GridLayoutManager(activity, 5));
            binding.myRingRecycler.setAdapter(customMyRingAdapter);

            if (myRingList != null && !myRingList.isEmpty()) {
                activity.runOnUiThread(() -> customMyRingAdapter.updateData(myRingList));
            }

            if (myRingList != null && !myRingList.isEmpty()) {
                if (ringMemberCout == 0) {
                    binding.ringCount.setText(Utils.setLangValue("user_count",String.valueOf(myRingList.size())));
                } else {
                    binding.ringCount.setText(Utils.setLangValue("user_count",String.valueOf(ringMemberCout)));
                }

                binding.roundLinear.setOnClickListener(view1 -> {
                    activity.startActivity(new Intent(activity, MyRingDetailActivity.class)
                                    .putExtra("isFromSubAdmin",isFromSubAdminPromoter)
                            .putExtra("UserDetailModel", new Gson().toJson(myRingList)));
                });
            }
            if (myRingList != null) {
                setUpData(myRingList, activity, supportFragmentManager);
                setUpData(myRingList, activity, supportFragmentManager, isPublicProfile);
            }

            PromoterMyRingView.this.removeAllViews();
            PromoterMyRingView.this.addView(view);


        });
    }


    // endregion
    // --------------------------------------
    // region Public
    // --------------------------------------

    public void setUpData(List<UserDetailModel> list, Activity activity, FragmentManager supportFragmentManager) {
        this.myRingList = list;
        this.activity = activity;
        this.supportFragmentManager = supportFragmentManager;


        if (binding == null) {
            return;
        }

        if (myRingList != null && !myRingList.isEmpty()) {
            customMyRingAdapter.updateData(myRingList);
            binding.myRingRecycler.setVisibility(VISIBLE);
            binding.emptyPlaceHolderView.setVisibility(View.GONE);
        } else {
            binding.myRingRecycler.setVisibility(View.GONE);
            binding.roundLinear.setVisibility(View.GONE);
            binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
        }

    }

    public void setUpData(List<UserDetailModel> list, Activity activity, FragmentManager supportFragmentManager, boolean isPublic) {
        this.myRingList = list;
        this.activity = activity;
        this.supportFragmentManager = supportFragmentManager;
        this.isPublicProfile = isPublic;


        if (binding == null) {
            return;
        }

        if (isPublic) {
            customMyRingAdapter = new CustomMyRingAdapter<>(supportFragmentManager, isPublic);
            binding.myRingRecycler.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
            binding.myRingRecycler.setAdapter(customMyRingAdapter);
            if (myRingList != null && !myRingList.isEmpty()) {
                imagesCount(myRingList);
                customMyRingAdapter.updateData(myRingList);
                binding.myRingRecycler.setVisibility(VISIBLE);
                binding.emptyPlaceHolderView.setVisibility(GONE);
            } else {
                binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
                binding.myRingRecycler.setVisibility(GONE);


            }
            binding.roundLinear.setVisibility(GONE);
        }

    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void imagesCount(List<UserDetailModel> myRingList) {
        if (myRingList != null && !myRingList.isEmpty()) {
            if (ringMemberCout == 0) {
                binding.ringCount.setText(Utils.setLangValue("user_count",String.valueOf(myRingList.size())));
            } else {
                binding.ringCount.setText(Utils.setLangValue("user_count",String.valueOf(ringMemberCout)));
            }

            binding.roundLinear.setOnClickListener(view1 -> {
                activity.startActivity(new Intent(activity, MyRingDetailActivity.class).putExtra("UserDetailModel", new Gson().toJson(myRingList)));
            });
        }
    }

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------


    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    private final class CustomMyRingAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {
        private static final int VIEW_TYPE_IMAGE = 1;
        private static final int VIEW_TYPE_PLUS = 0;

        private final FragmentManager fragmentManager;
        private boolean isPublic;

        public CustomMyRingAdapter(FragmentManager fragmentManager) {
            this.fragmentManager = fragmentManager;
        }

        public CustomMyRingAdapter(FragmentManager fragmentManager, boolean isPublic) {
            this.fragmentManager = fragmentManager;
            this.isPublic = isPublic;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (isPublic) {
                if (viewType == VIEW_TYPE_IMAGE) {
                    View view = UiUtils.getViewBy(parent, R.layout.item_my_ring_view);
                    RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) view.getLayoutParams();
                    if (isPublic) {
                        layoutParams.rightMargin = view.getContext().getResources().getDimensionPixelSize(com.intuit.sdp.R.dimen._minus35sdp);
                        layoutParams.width = view.getContext().getResources().getDimensionPixelSize(com.intuit.sdp.R.dimen._68sdp);
                    }
                    return new ViewHolder(view);
                } else {
                    View view = UiUtils.getViewBy(parent, R.layout.item_plus_view);
                    return new PlusViewHolder(view);
                }
            } else {
                View view = UiUtils.getViewBy(parent, R.layout.item_my_ring_view);
                RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) view.getLayoutParams();
                if (isPublic) {
                    layoutParams.rightMargin = view.getContext().getResources().getDimensionPixelSize(com.intuit.sdp.R.dimen._minus35sdp);
                    if (viewType == 0) {
                        layoutParams.width = view.getContext().getResources().getDimensionPixelSize(com.intuit.sdp.R.dimen._68sdp);
                    }
                }
                return new ViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof ViewHolder) {
                ViewHolder viewHolder = (ViewHolder) holder;

                if (!isPublic && position >= 10) {
                    return;
                }

                UserDetailModel model = (UserDetailModel) getItem(position);
                Graphics.loadImageWithFirstLetter(model.getImage(), viewHolder.mBinding.image, model.getFirstName());
                viewHolder.mBinding.tvName.setText(model.getFirstName());
                viewHolder.mBinding.tvName.setVisibility(isPublic ? GONE : VISIBLE);

                viewHolder.mBinding.getRoot().setOnClickListener(v -> {
                    if (isPublic) {
                        return;
                    }
                    activity.startActivity(new Intent(activity, CmPublicProfileActivity.class)
                            .putExtra("isFromSubAdmin" ,isFromSubAdminPromoter)
                            .putExtra("promoterUserId", model.getUserId()));
                });
            } else if (holder instanceof PlusViewHolder) {
                PlusViewHolder plusViewHolder = (PlusViewHolder) holder;
                if (myRingList != null && !myRingList.isEmpty() && isPublic) {
                    int totalImages = ringMemberCout == 0 ? myRingList.size() : ringMemberCout;
                    if (totalImages > 6) {
                        int countToShow = totalImages - 6;
                        plusViewHolder.mBinding.tvCount.setText(String.format("+%s", countToShow));
                        plusViewHolder.mBinding.layoutCount.setVisibility(View.VISIBLE);
                    } else {
                        plusViewHolder.mBinding.layoutCount.setVisibility(View.GONE);
                    }
                }
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (isPublic) {
                if (position == 6 && myRingList.size() > 6) {
                    return VIEW_TYPE_PLUS;
                } else {
                    return VIEW_TYPE_IMAGE;
                }
            } else {
                if (getItemCount() == position + 1) {
                    return 0;
                } else {
                    return 1;
                }
            }
        }

        @Override
        public int getItemCount() {
            if (isPublic && myRingList != null) {
                return Math.min(myRingList.size(), 6) + (myRingList.size() > 6 ? 1 : 0);
            } else {
                if (myRingList != null && !myRingList.isEmpty()){
                    int listSize = myRingList.size();
                    if (listSize > 10) {
                        return 10;
                    }
                    return listSize;
                }

            }
            return 0;
        }

    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemMyRingViewBinding mBinding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mBinding = ItemMyRingViewBinding.bind(itemView);
        }
    }

    public class PlusViewHolder extends RecyclerView.ViewHolder {
        private final ItemPlusViewBinding mBinding;

        public PlusViewHolder(@NonNull View itemView) {
            super(itemView);
            mBinding = ItemPlusViewBinding.bind(itemView);
        }
    }
}

// endregion
// --------------------------------------


