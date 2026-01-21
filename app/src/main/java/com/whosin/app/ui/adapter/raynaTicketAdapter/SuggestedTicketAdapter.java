package com.whosin.app.ui.adapter.raynaTicketAdapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.ItemSuggestedTicketBinding;
import com.whosin.app.service.manager.LogManager;
import com.whosin.app.service.manager.RaynaTicketManager;
import com.whosin.app.service.models.HomeTicketsModel;
import com.whosin.app.ui.activites.raynaTicket.RaynaTicketDetailActivity;

import java.util.List;
import java.util.Locale;

public class SuggestedTicketAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {
    private final Activity activity;
    private final FragmentManager fragmentManager;

    public SuggestedTicketAdapter(Activity activity, FragmentManager fragmentManager) {
        this.activity = activity;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = UiUtils.getViewBy(parent, R.layout.item_suggested_ticket);
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width = (int) (Graphics.getScreenWidth(activity) * 0.60);
        view.setLayoutParams(params);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (activity == null) {
            return;
        }
        ViewHolder viewHolder = (ViewHolder) holder;
        HomeTicketsModel model = (HomeTicketsModel) getItem(position);
        if (model == null) return;

        if (model.getImages() != null && !model.getImages().isEmpty()) {
            List<String> urls = model.getImages();
            urls.removeIf(Utils::isVideo);
            viewHolder.setupData(model.getImages(), model, activity);
        }

        viewHolder.mBinding.overlayTitle.setText(Utils.notNullString(model.getTitle()));
        viewHolder.mBinding.tvStatingTile.setText(String.format("%s ", Utils.getLangValue("starting")));
        String startingAmount = model.getStartingAmount() != null ? String.valueOf(model.getStartingAmount()) : "N/A";
        Integer discountInt = model.getDiscount() != null ? model.getDiscount() : 0;
        viewHolder.mBinding.tvRecentlyAddedTitle.setText(Utils.getLangValue("recently_added"));

        double rating = model.getAvg_ratings();
        viewHolder.mBinding.ticketRatingLayout.setVisibility(rating != 0 ? View.VISIBLE : View.GONE);
        if (rating != 0) {
            viewHolder.mBinding.tvRate.setText(String.format(Locale.ENGLISH, "%.1f", Math.floor(rating * 10) / 10.0));
        }
        viewHolder.mBinding.ticketTag.setVisibility(model.isTicketRecentlyAdded() ? View.VISIBLE : View.GONE);

        if (startingAmount.equals("N/A") || startingAmount.equals("0")) {
            Utils.setStyledText(activity, viewHolder.mBinding.tvAED, "0");
            viewHolder.mBinding.roundLinear.setVisibility(View.GONE);
            viewHolder.mBinding.startingFromLayout.setVisibility(View.GONE);
            viewHolder.mBinding.tvDiscount.setVisibility(View.GONE);
            viewHolder.mBinding.ticketFromAmount.setText(Utils.getStyledText(activity, "0"));
            viewHolder.mBinding.discountText.setVisibility(View.GONE);
        } else {
            String rounded = Utils.roundFloatValue(Float.valueOf(startingAmount));
            Utils.setStyledText(activity, viewHolder.mBinding.tvAED, rounded);
            viewHolder.mBinding.roundLinear.setVisibility(View.VISIBLE);
            viewHolder.mBinding.startingFromLayout.setVisibility(View.VISIBLE);
            viewHolder.mBinding.ticketFromAmount.setText(Utils.getStyledText(activity, rounded));
            if (discountInt != 0) {
                try {
                    float current = Float.parseFloat(startingAmount);
                    float original = current / (1f - (discountInt / 100f));
                    String originalRounded = Utils.roundFloatValue(original);
                    SpannableString strikeAmount = Utils.getStyledText(activity, originalRounded);
                    strikeAmount.setSpan(new StrikethroughSpan(), 0, strikeAmount.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    viewHolder.mBinding.discountText.setVisibility(View.VISIBLE);
                    viewHolder.mBinding.discountText.setText(strikeAmount);
                } catch (Exception ignored) {
                    viewHolder.mBinding.discountText.setVisibility(View.GONE);
                }
            } else {
                viewHolder.mBinding.discountText.setVisibility(View.GONE);
            }
            if (discountInt != 0) {
                viewHolder.mBinding.tvDiscount.setVisibility(View.VISIBLE);
                viewHolder.mBinding.tvDiscount.setText(discountInt + "%");
            } else {
                viewHolder.mBinding.tvDiscount.setVisibility(View.GONE);
            }
        }

        viewHolder.mBinding.getRoot().setOnClickListener(view -> {
            Utils.preventDoubleClick(view);
            RaynaTicketManager.shared.clearManager();
            activity.startActivity(new Intent(activity, RaynaTicketDetailActivity.class).putExtra("ticketId", model.getId()));
        });
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder instanceof ViewHolder) {
            ((ViewHolder) holder).stopSliding();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemSuggestedTicketBinding mBinding;
        private Runnable slideRunnable;
        private ViewPager.OnPageChangeListener pageChangeListener;
        private final Handler handler = new Handler(android.os.Looper.getMainLooper());
        private boolean isFavorite = false;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mBinding = ItemSuggestedTicketBinding.bind(itemView);
        }

        public void stopSliding() {
            if (slideRunnable != null) {
                handler.removeCallbacks(slideRunnable);
            }
        }

        public void setupData(List<String> images, HomeTicketsModel homeTicketsModel, Activity activity) {
            stopSliding();
            if (pageChangeListener != null) {
                mBinding.viewPager.removeOnPageChangeListener(pageChangeListener);
            }

            RaynaHomeTicketImageAdapter adapter = new RaynaHomeTicketImageAdapter(activity, homeTicketsModel, images);
            mBinding.viewPager.setAdapter(adapter);

            slideRunnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        int nextPage = mBinding.viewPager.getCurrentItem() + 1;
                        if (nextPage == adapter.getCount()) {
                            nextPage = 0;
                        }
                        mBinding.viewPager.setCurrentItem(nextPage, true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        handler.postDelayed(this, 4000);
                    }
                }
            };
            handler.postDelayed(slideRunnable, 4000);

            pageChangeListener = new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    handler.removeCallbacks(slideRunnable);
                    handler.postDelayed(slideRunnable, 4000);
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            };
            mBinding.viewPager.addOnPageChangeListener(pageChangeListener);

            Graphics.setFavoriteIcon(activity, isFavorite, mBinding.ivFavourite);
            mBinding.btnFavorite.setOnClickListener(v -> {
                Utils.preventDoubleClick(v);
                showProgress(true);
                RaynaTicketManager.shared.requestRaynaTicketFavorite(activity, homeTicketsModel.getId(), (success, error) -> {
                    showProgress(false);
                    if (success) {
                        isFavorite = !isFavorite;
                        if (isFavorite) {
                            LogManager.shared.logTicketEvent(LogManager.LogEventType.addToWishlist, homeTicketsModel.getId(), homeTicketsModel.getTitle(), 0.0, null, "AED");
                        }
                        Graphics.setFavoriteIcon(activity, isFavorite, mBinding.ivFavourite);
                    }
                });
            });
        }

        private void showProgress(boolean show) {
            mBinding.ivFavourite.setVisibility(show ? View.GONE : View.VISIBLE);
            mBinding.favTicketProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }
}
