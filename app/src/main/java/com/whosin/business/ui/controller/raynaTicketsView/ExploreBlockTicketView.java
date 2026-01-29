package com.whosin.business.ui.controller.raynaTicketsView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.asynclayoutinflater.view.AsyncLayoutInflater;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.tapadoo.alerter.Alerter;
import com.whosin.business.R;
import com.whosin.business.comman.DiffAdapter;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Graphics;
import com.whosin.business.comman.HorizontalSpaceItemDecoration;
import com.whosin.business.comman.Utils;
import com.whosin.business.comman.ui.UiUtils;
import com.whosin.business.comman.ui.VerticalSpaceItemDecoration;
import com.whosin.business.databinding.HomeBlockTicketViewBinding;
import com.whosin.business.databinding.ItemNewExploreTicketBinding;
import com.whosin.business.service.manager.LogManager;
import com.whosin.business.service.manager.RaynaTicketManager;
import com.whosin.business.service.models.rayna.RaynaTicketDetailModel;
import com.whosin.business.ui.activites.raynaTicket.RaynaTicketDetailActivity;
import com.whosin.business.ui.adapter.raynaTicketAdapter.RaynaTicketImageAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class ExploreBlockTicketView extends ConstraintLayout {

    private HomeBlockTicketViewBinding binding;
    private Context context;
    public Activity activity;
    private List<RaynaTicketDetailModel> exploreTicketsModelList;
    private ExploreBlockTicketAdapter<RaynaTicketDetailModel> ticketAdapter;
    public boolean isVertical = false;
    private boolean isBootomPadding = false;
    public boolean isFromFavBlock = false;


    public ExploreBlockTicketView(Context context) {
        this(context, null);
    }

    public ExploreBlockTicketView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExploreBlockTicketView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, 0);
        this.context = context;

        AsyncLayoutInflater asyncLayoutInflater = new AsyncLayoutInflater(context);
        asyncLayoutInflater.inflate(R.layout.home_block_ticket_view, this, (view, resid, parent) -> {
            binding = HomeBlockTicketViewBinding.bind(view);
            setupRecycleHorizontalManager(binding.ticketRecycle, isVertical ? LinearLayoutManager.VERTICAL : LinearLayoutManager.HORIZONTAL);
            ticketAdapter = new ExploreBlockTicketAdapter<>();
            binding.ticketRecycle.setAdapter(ticketAdapter);
            if (exploreTicketsModelList != null) {
                activity.runOnUiThread(() -> ticketAdapter.updateData(exploreTicketsModelList));
            }

            ExploreBlockTicketView.this.removeAllViews();
            ExploreBlockTicketView.this.addView(view);
        });
    }


    public RecyclerView getRecyclerView() {
        if (binding == null) {
            throw new IllegalStateException("RecyclerView is not yet initialized. Ensure that the layout is inflated before accessing it.");
        }
        return binding.ticketRecycle;
    }


    private void setupRecycleHorizontalManager(RecyclerView recyclerView, int orientation) {
        recyclerView.setLayoutManager(new LinearLayoutManager(activity, orientation, false));
        int spacing = getResources().getDimensionPixelSize(com.intuit.ssp.R.dimen._10ssp);
        recyclerView.addItemDecoration(new HorizontalSpaceItemDecoration(spacing));
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(spacing));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        recyclerView.offsetChildrenHorizontal(1);
    }


    public void setupData(List<RaynaTicketDetailModel> ticket, Activity activity, boolean isVertical,boolean isPadding) {
        this.exploreTicketsModelList = ticket;
        this.activity = activity;
        this.isVertical = isVertical;
        this.isBootomPadding = isPadding;

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        if (exploreTicketsModelList == null) {
            return;
        }
        if (binding == null) {
            return;
        }


        if (ticketAdapter == null) {
            ticketAdapter = new ExploreBlockTicketAdapter<>();
            binding.ticketRecycle.setAdapter(ticketAdapter);
            binding.ticketRecycle.setLayoutManager(
                    new LinearLayoutManager(activity, isVertical ? LinearLayoutManager.VERTICAL : LinearLayoutManager.HORIZONTAL, false)
            );
        }
        activity.runOnUiThread(() -> ticketAdapter.updateData(exploreTicketsModelList));

    }

    public void setTicketPagginationData(List<RaynaTicketDetailModel> ticket,boolean isLastPosition){
        if (!ticket.isEmpty()){
            exploreTicketsModelList.addAll(ticket);
            if (ticketAdapter != null) ticketAdapter.updateDataForPaggination(exploreTicketsModelList);
        }
        if (isLastPosition){
            isBootomPadding = true;
            binding.ticketRecycle.setPadding(0, 0, 0, Utils.getMarginBottom(context, 0.18f));
        }

    }


    public void reloadTicketData(RaynaTicketDetailModel model){
        if (activity == null) {
            return;
        }
        activity.runOnUiThread(() -> {
            if (ticketAdapter != null && ticketAdapter.getData() != null && !ticketAdapter.getData().isEmpty()) {
                for (int i = 0; i < ticketAdapter.getData().size(); i++) {
                    RaynaTicketDetailModel item = ticketAdapter.getData().get(i);
                    if (model.getId().equals(item.getId())) {
                        item.setIs_favorite(model.isIs_favorite());
                        ticketAdapter.notifyItemChanged(i);
                        break;
                    }
                }
            }
        });

    }

    public int getAdapterCount(){
        if (ticketAdapter != null && ticketAdapter.getData() != null && !ticketAdapter.getData().isEmpty()) {
            return ticketAdapter.getData().size();
        }
        return 0;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(RaynaTicketDetailModel event) {
        if (event == null) {return;}
        reloadTicketData(event);
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        RaynaTicketDetailModel event = EventBus.getDefault().getStickyEvent(RaynaTicketDetailModel.class);
        if (event != null) {
            post(() -> reloadTicketData(event));
        }

    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBus.getDefault().unregister(this);
    }


    public class ExploreBlockTicketAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = UiUtils.getViewBy(parent, R.layout.item_new_explore_ticket);
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.width = (int) (Graphics.getScreenWidth(activity) * (isVertical || getItemCount() <= 1 ? 0.93 : 0.89));
            view.setLayoutParams(params);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            RaynaTicketDetailModel model = (RaynaTicketDetailModel) getItem(position);
            if (model != null) {
                ((ViewHolder) holder).bind(model, position);
            }
        }

        @Override
        public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
            super.onViewDetachedFromWindow(holder);
            if (holder.getClass() == ViewHolder.class) {
                ((ViewHolder) holder).cleanup();
            }
        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            private final ItemNewExploreTicketBinding mBinding;
            private final Handler handler = new Handler(Looper.getMainLooper());
            private Runnable autoScrollRunnable;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemNewExploreTicketBinding.bind(itemView);
            }

            public void bind(RaynaTicketDetailModel model, int position) {

                mBinding.tvFromTitle.setText(Utils.getLangValue("from"));
                mBinding.tvStatingTile.setText(String.format("%s ", Utils.getLangValue("starting")));
                mBinding.tvRecentlyAddedTitle.setText(Utils.getLangValue("recently_added"));

                // Title and City
                mBinding.txtTitle.setText(Utils.notNullString(model.getTitle()));
                mBinding.ticketAddress.setText(model.getCity());

                // Ratings
                double rating = model.getAvg_ratings();
                mBinding.ticketRatingLayout.setVisibility(rating != 0 ? View.VISIBLE : View.GONE);
                if (rating != 0) {
                    mBinding.tvRate.setText(String.format(Locale.ENGLISH, "%.1f", Math.floor(rating * 10) / 10.0));
                }

                // Recently added tag
                mBinding.ticketTag.setVisibility(model.isTicketRecentlyAdded() ? View.VISIBLE : View.GONE);

                // Price and Discount
                String amount = model.getStartingAmount() != null ? String.valueOf(model.getStartingAmount()) : "0";
                Utils.setStyledText(activity, mBinding.tvAED, Utils.roundFloatValue(Float.parseFloat(amount)));
                if (TextUtils.isEmpty(amount) || amount.equals("0")) {
                    mBinding.roundLinear.setVisibility(GONE);
                    mBinding.startingFromLayout.setVisibility(GONE);
                } else {
                    mBinding.roundLinear.setVisibility(View.VISIBLE);
                    mBinding.startingFromLayout.setVisibility(View.VISIBLE);
                }

                mBinding.ticketFromAmount.setText(model.getDiscountAndStartingAmount(activity, mBinding.discountText));
                mBinding.tvDiscount.setVisibility(model.getDiscount() != 0 ? View.VISIBLE : View.GONE);
                if (model.getDiscount() != 0) {
                    mBinding.tvDiscount.setText(model.getDiscount() + "%");
                }

                // Favorite icon
                Graphics.setFavoriteIcon(activity, model.isIs_favorite(), mBinding.ivFavourite);
                mBinding.btnFavorite.setOnClickListener(v -> toggleFavorite(model, position));

                // Bottom margin for last item
                Utils.setBottomMargin(itemView, position == getItemCount() - 1 && isBootomPadding
                        ? Utils.getMarginBottom(itemView.getContext(), 0.18f) : 0);

                // Click to open details
                itemView.setOnClickListener(v -> {
                    Utils.preventDoubleClick(v);
                    activity.startActivity(new Intent(activity, RaynaTicketDetailActivity.class)
                            .putExtra("ticketId", model.getId()));
                });

                // Setup image view pager
                List<String> images = model.getImages().stream().filter(img -> !Utils.isVideo(img)).collect(Collectors.toList());
                setupImagePager(images, model);
            }

            private void toggleFavorite(RaynaTicketDetailModel model, int position) {
                showProgress(true);
                RaynaTicketManager.shared.requestRaynaTicketFavorite(activity, model.getId(), (success, error) -> {
                    showProgress(false);
                    if (success) {
                        model.setIs_favorite(!model.isIs_favorite());
                        if (!isFromFavBlock) {
                            notifyItemChanged(position);
                        }
                        if (success) {
                            LogManager.shared.logTicketEvent(LogManager.LogEventType.addToWishlist, model.getId(), model.getTitle(), 0.0, null, "AED");
                        }
                        Alerter.create(activity)
                                .setTitle(model.isIs_favorite() ? Utils.getLangValue("thank_you") : Utils.getLangValue("oh_snap"))
                                .setText(model.isIs_favorite() ? Utils.setLangValue("add_favourite",model.getTitle()) : Utils.setLangValue("remove_favourite",model.getTitle()))
                                .setTitleAppearance(R.style.AlerterTitle)
                                .setTextAppearance(R.style.AlerterText)
                                .setBackgroundColorRes(R.color.white_color)
                                .hideIcon()
                                .show();
                        EventBus.getDefault().postSticky(model);
                    } else {
                        Toast.makeText(activity, error != null ? error : Utils.getLangValue("something_wrong"), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            private void setupImagePager(List<String> images, RaynaTicketDetailModel model) {
                mBinding.viewPager.setAdapter(new RaynaTicketImageAdapter(activity, model, images));
                if (autoScrollRunnable != null) {
                    handler.removeCallbacks(autoScrollRunnable);
                }
                autoScrollRunnable = () -> {
                    if (mBinding.viewPager.getAdapter() != null) {
                        int next = (mBinding.viewPager.getCurrentItem() + 1) % mBinding.viewPager.getAdapter().getCount();
                        mBinding.viewPager.setCurrentItem(next, true);
                        handler.postDelayed(this::cleanup, 4000);
                    }
                };
                handler.postDelayed(autoScrollRunnable, 4000);
                mBinding.viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        handler.removeCallbacks(autoScrollRunnable);
                        handler.postDelayed(autoScrollRunnable, 4000);
                    }
                });
            }

            private void showProgress(boolean show) {
                mBinding.ivFavourite.setVisibility(show ? View.GONE : View.VISIBLE);
                mBinding.favTicketProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            }

            public void cleanup() {
                if (autoScrollRunnable != null) {
                    handler.removeCallbacks(autoScrollRunnable);
                }
            }
        }

    }
}




