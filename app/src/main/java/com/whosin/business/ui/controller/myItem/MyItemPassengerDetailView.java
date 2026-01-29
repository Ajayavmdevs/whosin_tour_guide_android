package com.whosin.business.ui.controller.myItem;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.asynclayoutinflater.view.AsyncLayoutInflater;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.whosin.business.R;
import com.whosin.business.comman.DiffAdapter;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Utils;
import com.whosin.business.comman.ui.UiUtils;
import com.whosin.business.comman.ui.roundcornerlayout.CornerType;
import com.whosin.business.databinding.HomeBlockTicketViewBinding;
import com.whosin.business.databinding.ItemTourBookingBinding;
import com.whosin.business.service.models.MyWalletModel;
import com.whosin.business.service.models.rayna.RaynaTourDetailModel;
import com.whosin.business.ui.activites.wallet.WalletTicketDetailActivity;
import com.whosin.business.ui.adapter.BookingAddOnAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class MyItemPassengerDetailView extends ConstraintLayout {

    private HomeBlockTicketViewBinding binding;
    private Context context;
    private Activity activity;
    private List<RaynaTourDetailModel> raynaTourDetailModelList;
    private MyWalletModel myWalletModel;
    private TourBookingAdapter<RaynaTourDetailModel> ticketAdapter;
    private boolean isWhosinTicket = false;
    private boolean isTravelDesk = false;
    public boolean isFromHistory = false;
    public boolean isBigBusTicket = false;

    public MyItemPassengerDetailView(Context context) {
        this(context, null);
    }

    public MyItemPassengerDetailView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyItemPassengerDetailView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, 0);
        this.context = context;

        AsyncLayoutInflater asyncLayoutInflater = new AsyncLayoutInflater(context);
        asyncLayoutInflater.inflate(R.layout.home_block_ticket_view, this, (view, resid, parent) -> {
            binding = HomeBlockTicketViewBinding.bind(view);
            setupRecycleHorizontalManager(binding.ticketRecycle,LinearLayoutManager.VERTICAL);
            ticketAdapter = new TourBookingAdapter<>(activity);
            binding.ticketRecycle.setAdapter(ticketAdapter);
            if (raynaTourDetailModelList != null) {
                activity.runOnUiThread(() -> ticketAdapter.updateData(raynaTourDetailModelList));
            }

            MyItemPassengerDetailView.this.removeAllViews();
            MyItemPassengerDetailView.this.addView(view);
        });
    }


    private void setupRecycleHorizontalManager(RecyclerView recyclerView, int orientation) {
        recyclerView.setLayoutManager(new LinearLayoutManager(activity, orientation, false));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        recyclerView.offsetChildrenHorizontal(1);
    }


    public void setupData(MyWalletModel model, Activity activity) {
        this.raynaTourDetailModelList = model.getTicket().getTourDetails();
        this.myWalletModel = model;
        this.activity = activity;

        if (raynaTourDetailModelList == null) {
            return;
        }
        if (binding == null) {
            return;
        }

        ticketAdapter = new TourBookingAdapter<>(activity);
        binding.ticketRecycle.setAdapter(ticketAdapter);
        binding.ticketRecycle.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        activity.runOnUiThread(() -> {ticketAdapter.updateData(raynaTourDetailModelList);});

    }

    public void setupData(MyWalletModel model, Activity activity,boolean isWhosinTicket) {
        this.raynaTourDetailModelList = model.getWhosinTicket().getTourDetails();
        this.isWhosinTicket = isWhosinTicket;
        this.myWalletModel = model;
        this.activity = activity;

        if (raynaTourDetailModelList == null) {
            return;
        }
        if (binding == null) {
            return;
        }

        ticketAdapter = new TourBookingAdapter<>(activity);
        binding.ticketRecycle.setAdapter(ticketAdapter);
        binding.ticketRecycle.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        activity.runOnUiThread(() -> {ticketAdapter.updateData(raynaTourDetailModelList);});

    }

    public void setupData(boolean isTravelDeskTicket,MyWalletModel model, Activity activity) {
        this.raynaTourDetailModelList = model.getTraveldeskTicket().getTourDetails();
        this.isTravelDesk = isTravelDeskTicket;
        this.myWalletModel = model;
        this.activity = activity;

        if (raynaTourDetailModelList == null) {
            return;
        }
        if (binding == null) {
            return;
        }

        ticketAdapter = new TourBookingAdapter<>(activity);
        binding.ticketRecycle.setAdapter(ticketAdapter);
        binding.ticketRecycle.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        activity.runOnUiThread(() -> {ticketAdapter.updateData(raynaTourDetailModelList);});

    }


    public void setupDataForBigBus(MyWalletModel model, Activity activity) {
        this.raynaTourDetailModelList = model.getOctoTicket().getTourDetails();
        isBigBusTicket = true;
        this.myWalletModel = model;
        this.activity = activity;

        if (raynaTourDetailModelList == null) {
            return;
        }
        if (binding == null) {
            return;
        }

        ticketAdapter = new TourBookingAdapter<>(activity);
        binding.ticketRecycle.setAdapter(ticketAdapter);
        binding.ticketRecycle.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        activity.runOnUiThread(() -> {ticketAdapter.updateData(raynaTourDetailModelList);});

    }

    public class TourBookingAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        private final Activity activity;

        public TourBookingAdapter(Activity activity) {
            this.activity = activity;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = UiUtils.getViewBy(parent, R.layout.item_tour_booking);
            ViewGroup.LayoutParams params = view.getLayoutParams();
            view.setLayoutParams(params);
            return new ViewHolder(view);

        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            if (activity == null) {
                return;
            }
            ViewHolder viewHolder = (ViewHolder) holder;
            RaynaTourDetailModel model = (RaynaTourDetailModel) getItem(position);
            if (model == null) return;
            activity.runOnUiThread(() -> {

                if (isTravelDesk && model.getOptionData() != null) {
                    viewHolder.mBinding.tvOptionName.setText(Utils.notNullString(model.getOptionData().getName()));
                    String description = model.getOptionData().getDescription();
                    if (!TextUtils.isEmpty(description)) {
                        viewHolder.mBinding.tvOptionDescription.setText(Html.fromHtml(description, Html.FROM_HTML_MODE_LEGACY));
                    } else {
                        viewHolder.mBinding.tvOptionDescription.setText("");
                    }
                } else if (isBigBusTicket) {
                    if (model.getOptionData() != null) {
                        viewHolder.mBinding.tvOptionName.setText(Utils.notNullString(model.getOptionData().getTitle()));
                        String description = model.getOptionData().getShortDescription();
                        if (!TextUtils.isEmpty(description)) {
                            viewHolder.mBinding.tvOptionDescription.setText(Html.fromHtml(description, Html.FROM_HTML_MODE_LEGACY));
                        } else {
                            viewHolder.mBinding.tvOptionDescription.setText("");
                        }
                    }
                } else {
                    if (model.getTourOption() != null){

                        viewHolder.mBinding.tvOptionName.setText(Utils.notNullString(model.getTourOption().getOptionName()));

                        if (isWhosinTicket) {
                            viewHolder.mBinding.tvOptionDescription.setText(model.getTourOption().getDescription());
                        } else {
                            viewHolder.mBinding.tvOptionDescription.setText(model.getTourOption().getOptionDescription());
                        }
                    }else {
                        if (model.getOptionData() != null){
                            viewHolder.mBinding.tvOptionName.setText(Utils.notNullString(model.getOptionData().getDisplayName()));
                            viewHolder.mBinding.tvOptionDescription.setText(model.getOptionData().getOptionDescription());
                        }
                    }
                }

                if (model.getAddons() != null && !model.getAddons().isEmpty()) {
                    viewHolder.mBinding.addOnLayout.setVisibility(View.VISIBLE);
                    BookingAddOnAdapter<RaynaTourDetailModel> adapter = new BookingAddOnAdapter<>();
                    viewHolder.mBinding.recyclerViewAddOns.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
                    viewHolder.mBinding.recyclerViewAddOns.setAdapter(adapter);
                    adapter.updateData(model.getAddons());
                } else {
                    viewHolder.mBinding.addOnLayout.setVisibility(View.GONE);
                }

//                String guestName = Utils.setLangValue("numberOfPax",String.valueOf(model.getAdult()),String.valueOf(model.getChild()),String.valueOf(model.getInfant()));
                String guestName = Utils.setLangValue("numberOfPax",String.valueOf(model.getAdult()),model.getAdultTitle(),String.valueOf(model.getChild()),model.getChildTitle(),String.valueOf(model.getInfant()),model.getInfantTitle());
                viewHolder.mBinding.tvGuestDetails.setText(guestName);
//                viewHolder.mBinding.tvDate.setText(model.getTourDate());

//                String tourDate = model.getTourDate();
//
//                if (tourDate != null && !tourDate.isEmpty()) {
//                    if (tourDate.contains("T")) {
//                        try {
//                            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
//                            isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
//
//                            Date date = isoFormat.parse(tourDate);
//                            SimpleDateFormat desiredFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
//                            String formattedDate = desiredFormat.format(date);
//
//                            viewHolder.mBinding.tvDate.setText(formattedDate);
//                        } catch (ParseException e) {
//                            viewHolder.mBinding.tvDate.setText(tourDate);
//                        }
//                    } else {
//                        viewHolder.mBinding.tvDate.setText(tourDate);
//                    }
//                } else {
//                    viewHolder.mBinding.tvDate.setText("");
//                }

                String tourDate = model.getTourDate();

                if (tourDate != null && !tourDate.isEmpty()) {
                    if (tourDate.contains("T")) {
                        try {
                            Date date = null;

                            if (tourDate.endsWith("Z")) {
                                // Old format: 2025-10-10T03:00:00.000Z
                                SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
                                isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                                date = isoFormat.parse(tourDate);
                            } else if (tourDate.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}[+-]\\d{2}:\\d{2}")) {
                                // New format: 2025-10-10T03:00:00+04:00
                                SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.ENGLISH);
                                isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                                date = isoFormat.parse(tourDate);
                            }

                            if (date != null) {
                                SimpleDateFormat desiredFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                                viewHolder.mBinding.tvDate.setText(desiredFormat.format(date));
                            } else {
                                viewHolder.mBinding.tvDate.setText(tourDate);
                            }
                        } catch (ParseException e) {
                            viewHolder.mBinding.tvDate.setText(tourDate);
                        }
                    } else {
                        viewHolder.mBinding.tvDate.setText(tourDate);
                    }
                } else {
                    viewHolder.mBinding.tvDate.setText("");
                }


                if (isWhosinTicket) {
                    if (!TextUtils.isEmpty(model.getTimeSlot())) {
                        viewHolder.mBinding.tvTime.setText(model.getTimeSlot());
                    } else if (model.getTourOption().getAvailabilityType().equals("regular") && !TextUtils.isEmpty(model.getTourOption().getAvailabilityTime())) {
                        viewHolder.mBinding.tvTime.setText(model.getTourOption().getAvailabilityTime());
                    }

                } else if (isBigBusTicket) {
                    if (!TextUtils.isEmpty(model.getTimeSlot())){
                        viewHolder.mBinding.tvTime.setText(model.getTimeSlot());
                    }else {
                        viewHolder.mBinding.tvTime.setText(model.getStartTime());
                    }
                } else if (isTravelDesk) {
                    viewHolder.mBinding.tvTime.setText(model.getTimeSlot());
                } else {
                    viewHolder.mBinding.tvTime.setText(model.getStartTime());
                }


                boolean isFirst = position == 0;
                boolean isLast = position == getItemCount() - 1;

                if (isFirst) {
                    viewHolder.mBinding.getRoot().setCornerRadius(16f, CornerType.TOP_LEFT);
                    viewHolder.mBinding.getRoot().setCornerRadius(16f, CornerType.TOP_RIGHT);
                }
                if (isLast) {
                    viewHolder.mBinding.getRoot().setCornerRadius(16f, CornerType.BOTTOM_LEFT);
                    viewHolder.mBinding.getRoot().setCornerRadius(16f, CornerType.BOTTOM_RIGHT);
                    GradientDrawable background = (GradientDrawable) ContextCompat.getDrawable(activity, R.drawable.item_background);

                    if (background != null) {
                        background = (GradientDrawable) background.mutate();

                        float radius = Utils.dpToPx(activity, 10f);
                        float[] radii = new float[] {
                                0f, 0f,
                                0f, 0f,
                                radius, radius,
                                radius, radius
                        };
                        background.setCornerRadii(radii);
                        viewHolder.mBinding.labelLayout.setBackground(background);
                    }
                } else {
                    viewHolder.mBinding.labelLayout.setBackgroundResource(R.drawable.item_background);
                }
            });

            viewHolder.mBinding.getRoot().setOnClickListener(view -> {
                Utils.preventDoubleClick(view);
                if (isWhosinTicket){
                    activity.startActivity(new Intent(activity, WalletTicketDetailActivity.class).putExtra("model", new Gson().toJson(myWalletModel)).putExtra("isWhosinBooking",true).putExtra("isFromHistory",isFromHistory));
                } else if (isTravelDesk) {
                    activity.startActivity(new Intent(activity, WalletTicketDetailActivity.class).putExtra("model", new Gson().toJson(myWalletModel)).putExtra("isTravelDeskBooking",true).putExtra("isFromHistory",isFromHistory));
                }else if (isBigBusTicket) {
                    activity.startActivity(new Intent(activity, WalletTicketDetailActivity.class).putExtra("model", new Gson().toJson(myWalletModel)).putExtra("isBigBusBooking",true).putExtra("isFromHistory",isFromHistory));
                } else {
                    activity.startActivity(new Intent(activity, WalletTicketDetailActivity.class).putExtra("model", new Gson().toJson(myWalletModel)).putExtra("isFromHistory",isFromHistory));

                }
            });
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final ItemTourBookingBinding mBinding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemTourBookingBinding.bind(itemView);
            }

        }


    }

}
