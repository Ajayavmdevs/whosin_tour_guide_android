package com.whosin.app.ui.controller.raynaTicketsView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.asynclayoutinflater.view.AsyncLayoutInflater;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.HorizontalSpaceItemDecoration;
import com.whosin.app.comman.RaynaTicketCalculator;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.comman.ui.VerticalSpaceItemDecoration;
import com.whosin.app.databinding.HomeBlockTicketViewBinding;
import com.whosin.app.databinding.ItemGetTicketHomeBlockBinding;
import com.whosin.app.service.manager.LogManager;
import com.whosin.app.service.manager.RaynaTicketManager;
import com.whosin.app.service.models.rayna.RaynaTicketDetailModel;
import com.whosin.app.ui.activites.raynaTicket.RaynaTicketDetailActivity;
import com.whosin.app.ui.adapter.raynaTicketAdapter.RaynaTicketImageAdapter;

import java.util.List;

public class HomeBlockTicketView extends ConstraintLayout {

    private HomeBlockTicketViewBinding binding;
    private Context context;
    private Activity activity;
    private FragmentManager supportFragmentManager;
    private List<RaynaTicketDetailModel> homeTicketsModelList;
    private HomeBlockTicketAdapter<RaynaTicketDetailModel> ticketAdapter;
    public boolean isVertical = false;
    public Lifecycle lifecycle;
    private Handler handler = new Handler();



    public HomeBlockTicketView(Context context) {
        this(context, null);
    }

    public HomeBlockTicketView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HomeBlockTicketView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, 0);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.offer_info_view_loader, this, true);

        AsyncLayoutInflater asyncLayoutInflater = new AsyncLayoutInflater(context);
        asyncLayoutInflater.inflate(R.layout.home_block_ticket_view, this, (view, resid, parent) -> {
            binding = HomeBlockTicketViewBinding.bind(view);
            setupRecycleHorizontalManager(binding.ticketRecycle, isVertical ? LinearLayoutManager.VERTICAL: LinearLayoutManager.HORIZONTAL);
            ticketAdapter = new HomeBlockTicketAdapter<>(activity, supportFragmentManager);
            binding.ticketRecycle.setAdapter(ticketAdapter);
            if (homeTicketsModelList != null) {
                activity.runOnUiThread(() -> ticketAdapter.updateData(homeTicketsModelList));
            }
            HomeBlockTicketView.this.removeAllViews();
            HomeBlockTicketView.this.addView(view);
        });
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


    public void setupData(List<RaynaTicketDetailModel> ticket, Activity activity, FragmentManager fragmentManager, int orientation) {
        this.homeTicketsModelList = ticket;
        this.activity = activity;
        this.supportFragmentManager = fragmentManager;

        if (homeTicketsModelList == null) {
            return;
        }
        if (binding == null) {
            return;
        }

        activity.runOnUiThread(() -> {
            ticketAdapter.updateData(homeTicketsModelList);
        });
    }



    public class HomeBlockTicketAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {
        private final Activity activity;
        private final FragmentManager fragmentManager;

        public HomeBlockTicketAdapter(Activity activity, FragmentManager fragmentManager) {
            this.activity = activity;
            this.fragmentManager = fragmentManager;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = UiUtils.getViewBy(parent, R.layout.item_get_ticket_home_block);
            ViewGroup.LayoutParams params = view.getLayoutParams();
            if (isVertical) {
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                params.width = (int) (Graphics.getScreenWidth(activity) * (getItemCount() > 1 ? 0.93 : 0.93));
                view.setLayoutParams(params);
            } else {
                params.width = (int) (Graphics.getScreenWidth(activity) * (getItemCount() > 1 ? 0.89 : 0.93));
                view.setLayoutParams(params);
            }

            return new ViewHolder(view);

        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            if (activity == null) {
                return;
            }
            ViewHolder viewHolder = (ViewHolder) holder;
            RaynaTicketDetailModel model = (RaynaTicketDetailModel) getItem(position);
            if (model == null) return;
            activity.runOnUiThread(() -> {
                if (model.getImages() != null && !model.getImages().isEmpty()) {
                    List<String> urls = model.getImages();
                    urls.removeIf(Utils::isVideo);
                    viewHolder.setupData(model.getImages(),model);
                }

                String discount = String.valueOf(model.getDiscount());

                if (!"0".equals(discount)) {
                    viewHolder.mBinding.tvDiscount.setText(discount.contains("%") ? discount : discount + "%");
                    viewHolder.mBinding.tvDiscount.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.mBinding.tvDiscount.setVisibility(View.GONE);
                }

                viewHolder.mBinding.txtTitle.setText(Utils.notNullString(model.getTitle()));
                viewHolder.mBinding.tvDescription.setText(Html.fromHtml(model.getDescription()));
                String startingAmount = model.getStartingAmount() != null ? String.valueOf(model.getStartingAmount()) : "N/A";


                if (startingAmount.equals("N/A")) {
                    Utils.setStyledText(activity, viewHolder.mBinding.tvAED, "0");
                } else {
                    Utils.setStyledText(activity, viewHolder.mBinding.tvAED, Utils.roundFloatValue(Float.valueOf(startingAmount)));
                }


                viewHolder.mBinding.ticketConstraint.setOnClickListener(view -> {
                    Double price = 0.0;
                    try {
                        if (model.getStartingAmount() != null) {
                            price = Double.parseDouble(String.valueOf(model.getStartingAmount()));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    RaynaTicketManager.shared.clearManager();
                    activity.startActivity(new Intent(activity, RaynaTicketDetailActivity.class).putExtra("ticketId",model.getId()));
                });

                viewHolder.mBinding.getRoot().setOnClickListener(view -> {
                    Utils.preventDoubleClick(view);
                    Double price = 0.0;
                    try {
                        if (model.getStartingAmount() != null) {
                            price = Double.parseDouble(String.valueOf(model.getStartingAmount()));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    activity.startActivity(new Intent(activity, RaynaTicketDetailActivity.class).putExtra("ticketId",model.getId()));
                });

            });
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final ItemGetTicketHomeBlockBinding mBinding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemGetTicketHomeBlockBinding.bind(itemView);
            }


            public void setupData(List<String> imaegs , RaynaTicketDetailModel raynaTicketDetailModel) {
                RaynaTicketImageAdapter adapter = new RaynaTicketImageAdapter( activity, raynaTicketDetailModel,imaegs );
                mBinding.viewPager.setAdapter( adapter );
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            int nextPage = mBinding.viewPager.getCurrentItem() + 1;
                            if (nextPage == adapter.getCount()) {
                                nextPage = 0;
                            }
                            mBinding.viewPager.setCurrentItem( nextPage, true );
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            handler.postDelayed( this, 4000 );
                        }
                    }
                };
                handler.postDelayed( runnable, 4000 );

                mBinding.viewPager.addOnPageChangeListener( new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        handler.removeCallbacks( runnable );
                        handler.postDelayed( runnable, 4000 );
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                    }
                } );

            }


        }


    }

}

