package com.whosin.app.ui.fragment.wallet;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.tapadoo.alerter.Alerter;
import com.whosin.app.R;
import com.whosin.app.comman.AppConstants;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.FragmentMyItemsBinding;
import com.whosin.app.databinding.ItemMyTicketsBinding;
import com.whosin.app.databinding.ItemWalletPackageBinding;
import com.whosin.app.databinding.MyItemActivityRecyclerBinding;
import com.whosin.app.databinding.MyItemRecyclerBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.RaynaTicketManager;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.ContainerListModel;
import com.whosin.app.service.models.HomeTicketsModel;
import com.whosin.app.service.models.ItemModel;
import com.whosin.app.service.models.MyWalletModel;
import com.whosin.app.service.models.PackageModel;
import com.whosin.app.service.models.rayna.RaynaTourDetailModel;
import com.whosin.app.service.models.rayna.TourOptionDetailModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.home.activity.ActivityListDetail;
import com.whosin.app.ui.activites.home.event.EventDetailsActivity;
import com.whosin.app.ui.activites.offers.OfferDetailBottomSheet;
import com.whosin.app.ui.activites.offers.VoucherDetailScreenActivity;
import com.whosin.app.ui.activites.venue.VenueTimingDialog;
import com.whosin.app.ui.activites.wallet.PurchaseVoucherDetailActivity;
import com.whosin.app.ui.activites.wallet.RedeemActivity;
import com.whosin.app.ui.activites.wallet.WalletTicketDetailActivity;
import com.whosin.app.ui.fragment.comman.BaseFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class MyItemsFragment extends BaseFragment {

    private FragmentMyItemsBinding binding;
    private final MyItemsAdapter<MyWalletModel> itemsAdapter = new MyItemsAdapter<>();
    private boolean isApiCalled = false;



    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    public void initUi(View view) {

        EventBus.getDefault().register(this);
        binding = FragmentMyItemsBinding.bind(view);


        binding.emptyPlaceHolderView.setEmptyPlaceTxtTitle(getValue("wallet_empty_message"));

        binding.itemRecycler.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false));
        binding.itemRecycler.setAdapter(itemsAdapter);

    }

    @Override
    public void setListeners() {
        binding.swipeRefreshLayout.setOnRefreshListener(() -> requestMyItem(false));

    }

    @Override
    public void populateData(boolean getDataFromServer) {
        requestMyItem(false);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_my_items;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isApiCalled) {
            requestMyItem(true);
            isApiCalled = true;
        } else {
            requestMyItem(false);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MyWalletModel event) {
        requestMyItem(false);
    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void filterDate(List<MyWalletModel> data) {

        List<MyWalletModel> filteredList = data.stream()
                .filter(model ->
                        (model.getType().equals("deal") && model.getDeal() != null) ||
                                (model.getType().equals("activity") && model.getActivity() != null) ||
                                (model.getType().equals("offer") && model.getOffer() != null) ||
                                (model.getType().equals("event") && model.getEvent() != null) ||
                                (model.getType().equals("ticket") && model.getTicket() != null) ||
                                (model.getType().equals("whosin-ticket") && model.getWhosinTicket() != null) ||
                                (model.getType().equals("travel-desk") && model.getWhosinTicket() != null) ||
                                (model.getType().equals("big-bus") && model.getOctoTicket() != null) ||
                                (model.getType().equals("hero-balloon") && model.getOctoTicket() != null) ||
                                (model.getType().equals("juniper-hotel") && model.getJuniperHotel() != null)
                )
                .filter(model -> {
                    if ("offer".equals(model.getType())) {
                        return hasValidItems(model, model.getOffer() != null ? model.getOffer().getPackages() : null);
                    } else if ("event".equals(model.getType())) {
                        return hasValidItems(model, model.getEvent() != null ? model.getEvent().getPackages() : null);
                    }
                    return true;
                })
                .sorted(Comparator.comparing(MyWalletModel::getCreatedAt).reversed())
                .collect(Collectors.toList());

        if (!filteredList.isEmpty()) {
            itemsAdapter.updateData(filteredList);
        } else {
            binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
            binding.itemRecycler.setVisibility(View.GONE);
        }
    }


    private boolean hasValidItems(MyWalletModel model, List<PackageModel> packages) {
        if (packages == null || packages.isEmpty()) return false;

        List<String> validPackageIds = packages.stream()
                .filter(pkg -> pkg.getId() != null)
                .map(PackageModel::getId)
                .collect(Collectors.toList());

        return model.getItems().stream().anyMatch(item ->
                item.getQty() > 0 &&
                        item.getPackageId() != null &&
                        validPackageIds.contains(item.getPackageId())
        );
    }

    private void setLangText(ItemMyTicketsBinding mBinding){
        mBinding.tvBookingStatus.setText(getValue("cancelled"));
        mBinding.tvPaymentStatus.setText(getValue("paid"));
        mBinding.tvFinalAmountTitle.setText(getValue("final_amount"));
        mBinding.tvTitleDiscount.setText(getValue("discount"));
//        mBinding.tvTotalTitle.setText(getValue("total_amount"));
    }

    private void handleBookingStatus(Context context,ItemMyTicketsBinding mBinding, String bookingStatus, String paymentStatus) {
        switch (bookingStatus) {
            case "initiated":
                mBinding.tvInitiaedBookingStatus.setVisibility(View.VISIBLE);
                mBinding.tvInitiaedBookingStatus.setText(bookingStatus);
                mBinding.btnRedeem.setText(getValue("check_booking_status"));
                mBinding.btnRedeem.setEnabled(true);
                mBinding.btnRedeem.setTextColor(ContextCompat.getColor(context, R.color.white));
                break;

            case "rejected":
                mBinding.tvInitiaedBookingStatus.setVisibility(View.VISIBLE);
                mBinding.tvInitiaedBookingStatus.setText(getValue("pending"));
                mBinding.btnRedeem.setText(getValue("check_booking_status"));
                mBinding.btnRedeem.setEnabled(true);
                mBinding.btnRedeem.setTextColor(ContextCompat.getColor(context, R.color.white));
                break;

            case "confirmed":
                if ("paid".equals(paymentStatus)) {
                    mBinding.btnRedeem.setText(getValue("view_ticket"));
                    mBinding.btnRedeem.setBackgroundTintList(
                            ContextCompat.getColorStateList(context, R.color.button_pink));
                    mBinding.btnRedeem.setEnabled(true);
                    mBinding.btnRedeem.setTextColor(ContextCompat.getColor(context, R.color.white));
                    mBinding.tvInitiaedBookingStatus.setVisibility(View.GONE);
                }
                break;

            case "failed":
                if ("paid".equals(paymentStatus)) {
                    mBinding.tvInitiaedBookingStatus.setVisibility(View.VISIBLE);
                    mBinding.tvInitiaedBookingStatus.setText(getValue("pending"));
                    mBinding.btnRedeem.setText(getValue("check_booking_status"));
                    mBinding.btnRedeem.setEnabled(true);
                    mBinding.btnRedeem.setTextColor(ContextCompat.getColor(context, R.color.white));
                }else {
                    String title = "refunded".equals(paymentStatus) ? getValue("booking_failed_refunded") : getValue("booking_failed");
                    mBinding.btnRedeem.setText(title);
                    mBinding.btnRedeem.setTextColor(ContextCompat.getColor(context, R.color.red));
                    mBinding.btnRedeem.setBackgroundTintList(
                            ContextCompat.getColorStateList(context, R.color.transparent));
                    mBinding.btnRedeem.setEnabled(false);
                    mBinding.tvInitiaedBookingStatus.setVisibility(View.GONE);
                }

                break;
        }
    }


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestMyItem(boolean showLoader) {
        if (showLoader) {
            showProgress();
        }
        DataService.shared(requireActivity()).requestWalletMyItem(new RestCallback<ContainerListModel<MyWalletModel>>(this) {
            @Override
            public void result(ContainerListModel<MyWalletModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    binding.swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(requireActivity(), R.string.service_message_something_wrong, Toast.LENGTH_SHORT).show();
                    return;
                }
                binding.swipeRefreshLayout.setRefreshing(false);
                Log.d("Token", SessionManager.shared.getToken());
                if (model.data != null && !model.data.isEmpty()) {
                    binding.emptyPlaceHolderView.setVisibility(View.GONE);
                    binding.itemRecycler.setVisibility(View.VISIBLE);
                    filterDate(model.data);
                } else {
                    binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
                    binding.itemRecycler.setVisibility(View.GONE);
                }
            }
        });

    }

    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------


    public class MyItemsAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            switch (AppConstants.OrderListType.valueOf(viewType)) {
                case OFFER:
                    return new OfferBlockHolder(UiUtils.getViewBy(parent, R.layout.my_item_recycler));
                case ACTIVITY:
                    return new ActivityBlockHolder(UiUtils.getViewBy(parent, R.layout.my_item_activity_recycler));
                case DEAL:
                    return new DealBlockHolder(UiUtils.getViewBy(parent, R.layout.my_item_recycler));
                case EVENT:
                    return new EventBlockHolder(UiUtils.getViewBy(parent, R.layout.my_item_recycler));
                case TICKET:
                    return new TicketBlockHolder(UiUtils.getViewBy(parent, R.layout.item_my_tickets));
                case WHOSIN_TICKET:
                    return new WhosinTicketBlockHolder(UiUtils.getViewBy(parent, R.layout.item_my_tickets));
                case TRAVEL_DESK:
                    return new TravelDeskTicketBlockHolder(UiUtils.getViewBy(parent, R.layout.item_my_tickets));
                case BIG_BUS:
                case HERO_BALLOON:
                    return new BigBusTicketBlockHolder(UiUtils.getViewBy(parent, R.layout.item_my_tickets));
                case JUNIPER_HOTEL:
                    return new JuniperHotelBlockHolder(UiUtils.getViewBy(parent, R.layout.item_my_tickets));

            }
            return null;
        }

        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            MyWalletModel model = (MyWalletModel) getItem(position);
            boolean isLastItem = position == getItemCount() - 1;
            if (model.getOrderListType() == AppConstants.OrderListType.OFFER) {
                OfferBlockHolder viewHolder = (OfferBlockHolder) holder;
                viewHolder.setupData(model);
            } else if (model.getOrderListType() == AppConstants.OrderListType.ACTIVITY) {
                ActivityBlockHolder viewHolder = (ActivityBlockHolder) holder;
                viewHolder.setupData(model);
            } else if (model.getOrderListType() == AppConstants.OrderListType.DEAL) {
                DealBlockHolder viewHolder = (DealBlockHolder) holder;
                viewHolder.setupData(model);
            } else if (model.getOrderListType() == AppConstants.OrderListType.EVENT) {
                EventBlockHolder viewHolder = (EventBlockHolder) holder;
                viewHolder.setupData(model);
            } else if (model.getOrderListType() == AppConstants.OrderListType.TICKET) {
                TicketBlockHolder viewHolder = (TicketBlockHolder) holder;
                viewHolder.setupData(model);
            }else if (model.getOrderListType() == AppConstants.OrderListType.WHOSIN_TICKET) {
                WhosinTicketBlockHolder viewHolder = (WhosinTicketBlockHolder) holder;
                viewHolder.setupData(model);
            }else if (model.getOrderListType() == AppConstants.OrderListType.TRAVEL_DESK) {
                TravelDeskTicketBlockHolder viewHolder = (TravelDeskTicketBlockHolder) holder;
                viewHolder.setupData(model);
            }else if (model.getOrderListType() == AppConstants.OrderListType.BIG_BUS || model.getOrderListType() == AppConstants.OrderListType.HERO_BALLOON ) {
                BigBusTicketBlockHolder viewHolder = (BigBusTicketBlockHolder) holder;
                viewHolder.setupData(model);
            }else if (model.getOrderListType() == AppConstants.OrderListType.JUNIPER_HOTEL) {
                JuniperHotelBlockHolder viewHolder = (JuniperHotelBlockHolder) holder;
                viewHolder.setupData(model);
            }

            if (isLastItem) {
                int marginBottom = Utils.getMarginBottom(holder.itemView.getContext(), 0.10f);
                Utils.setBottomMargin(holder.itemView, marginBottom);
            } else {
                Utils.setBottomMargin(holder.itemView, 0);
            }
        }

        @Override
        public int getItemViewType(int position) {
            MyWalletModel model = (MyWalletModel) getItem(position);
            return model.getOrderListType().getValue();
        }

        public class OfferBlockHolder extends RecyclerView.ViewHolder {
            private final MyItemRecyclerBinding mBinding;
            private final PackageAdapter<PackageModel> packageAdapter = new PackageAdapter<>();

            public OfferBlockHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = MyItemRecyclerBinding.bind(itemView);
                mBinding.packageRecycler.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
                mBinding.packageRecycler.setAdapter(packageAdapter);
            }

            @SuppressLint("SetTextI18n")
            public void setupData(MyWalletModel model) {
                if (model.getOffer().getVenue() != null) {
                    mBinding.venueContainer.setVenueDetail(model.getOffer().getVenue());
                } else {
                    mBinding.venueContainer.setVisibility(View.GONE);
                }

                mBinding.btnTimeInfo.setVisibility(View.VISIBLE);
                mBinding.tvTypes.setText(model.getType());
                mBinding.txtTitle.setText(model.getOffer().getTitle());

                mBinding.tvDescription.setText(model.getOffer().getDescription());
                mBinding.tvDescription.post(() -> {
                    int lineCount = mBinding.tvDescription.getLineCount();
                    if (lineCount > 2) {
                        Utils.makeTextViewResizable(mBinding.tvDescription, 3, 3, ".. See More", true);
                    }
                });

                Graphics.loadImage(model.getOffer().getImage(), mBinding.imgCover);
                mBinding.txtDays.setText(model.getOffer().getDays());

                if (TextUtils.isEmpty(model.getOffer().getStartTime())) {
                    mBinding.startDate.setText("Ongoing");
                    mBinding.tillDateLayout.setVisibility(View.GONE);

                } else {
                    mBinding.tillDateLayout.setVisibility(View.VISIBLE);
                    mBinding.startDate.setText("From Date : " + Utils.convertMainDateFormat(model.getOffer().getStartTime()));
                    mBinding.endDate.setText("Till Date : " + Utils.convertMainDateFormat(model.getOffer().getEndTime()));
                }
                mBinding.txtTime.setText(model.getOffer().getOfferTiming());

                mBinding.tvDate.setText(Utils.convertDateFormat(model.getOffer().getEndTime(), AppConstants.DATEFORMAT_SHORT, AppConstants.DATEFORMAT_DD_MM_YYYY));

                mBinding.btnTimeInfo.setVisibility(model.getOffer().isShowTimeInfo() ? View.GONE : View.VISIBLE);
                mBinding.layoutTimeInfo.setOnClickListener(v -> {
                    if (!model.getOffer().isShowTimeInfo()) {
                        if (model == null) {
                            return;
                        }
                        if (model.getOffer().getVenue() == null) {
                            return;
                        }
                        VenueTimingDialog dialog = new VenueTimingDialog(model.getOffer().getVenue().getTiming(), getActivity());
                        dialog.show(getChildFragmentManager(), "1");
                    }
                });


//                mBinding.tvGift.setVisibility(model.getOffer().getPackages().isEmpty() ? View.GONE : View.VISIBLE);
                mBinding.btnRedeem.setVisibility(model.getOffer().getPackages().isEmpty() ? View.GONE : View.VISIBLE);

                if (model.getItems() != null && !model.getItems().isEmpty()) {
                    List<PackageModel> packageModelList = model.getItems().stream()
                            .flatMap(model1 -> model.getOffer().getPackages().stream()
                                    .filter(model2 -> model2.getId().equals(model1.getPackageId()))
                                    .peek(model2 -> {
                                        model2.setRemainingQty(model1.getRemainingQty());
                                        model2.setPrice(model1.getPrice());
                                    }))
                            .collect(Collectors.toList());
                    if (!packageModelList.isEmpty()) {
                        packageAdapter.updateData(packageModelList);
                    }
                } else {
                    mBinding.packageRecycler.setVisibility(View.GONE);
                }

                if (Utils.checkDateGoOrNot(model.getOffer().getEndTime())) {
                    mBinding.tvGift.setText("Expired!");
                    mBinding.tvGift.setBackgroundResource(android.R.color.transparent);
                    mBinding.tvGift.setEnabled(false);
                    mBinding.tvGift.setTypeface(null, Typeface.BOLD);
                    mBinding.tvGift.setTextColor(getResources().getColor(R.color.brand_pink));
                    mBinding.btnRedeem.setVisibility(View.GONE);
                } else {
                    mBinding.btnRedeem.setVisibility(View.VISIBLE);
//                    mBinding.tvGift.setVisibility(View.VISIBLE);

                }

                setListeners(model);

            }

            private void setListeners(MyWalletModel model) {

//                mBinding.tvGift.setOnClickListener( view -> {
//                    Utils.preventDoubleClick( view );
//                    if (model.getOffer().getPackages().size() > 1) {
//                        PackageBottomSheet packageBottomSheet = new PackageBottomSheet();
//                        packageBottomSheet.myWalletModel = model;
//                        packageBottomSheet.type = "gift";
//                        packageBottomSheet.callback = data -> {
//                            if (data != null) {
//                                requestMyItem();
//                            }
//                        };
//                        packageBottomSheet.show( getChildFragmentManager(), "1" );
//
//                    } else {
//                        SendGiftBottomSheet sendGiftBottomSheet = new SendGiftBottomSheet();
//                        sendGiftBottomSheet.myWalletModel = model;
//                        sendGiftBottomSheet.packageModel = model.getOffer().getPackages().get(0);
//                        sendGiftBottomSheet.callback = data -> {
//                            requestMyItem();
//                        };
//                        sendGiftBottomSheet.show( getChildFragmentManager(), "1" );
//                    }
//
//                } );

                mBinding.btnRedeem.setOnClickListener(view -> {
                    if (model.getOffer().getPackages().size() > 1) {
                        PackageBottomSheet packageBottomSheet = new PackageBottomSheet();
                        packageBottomSheet.myWalletModel = model;
                        packageBottomSheet.type = "redeem";
                        packageBottomSheet.callback = data -> {
                            if (data != null) {
                                requestMyItem(false);
                            }
                        };
                        packageBottomSheet.show(getChildFragmentManager(), "1");
                    } else {
                        Intent intent = new Intent(getActivity(), RedeemActivity.class);
                        intent.putExtra("itemList", new Gson().toJson(model));
                        intent.putExtra("packageModel", new Gson().toJson(model.getOffer().getPackages().get(0)));
                        activityLauncher.launch(intent, result -> {
                            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                                boolean isClose = result.getData().getBooleanExtra("close", false);
                                if (isClose) {
                                    requestMyItem(false);
                                }
                            }
                        });

                    }
                });

                mBinding.getRoot().setOnClickListener(v -> {
                    OfferDetailBottomSheet dialog = new OfferDetailBottomSheet();
                    dialog.offerId = model.getOfferId();
                    dialog.show(getChildFragmentManager(), "");
                    //Utils.requestvenueOfferDetail(requireActivity(),model.getOfferId());
                });

            }
        }

        public class EventBlockHolder extends RecyclerView.ViewHolder {
            private final MyItemRecyclerBinding mBinding;
            private final PackageAdapter<PackageModel> packageAdapter = new PackageAdapter<>();

            public EventBlockHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = MyItemRecyclerBinding.bind(itemView);
                mBinding.packageRecycler.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
                mBinding.packageRecycler.setAdapter(packageAdapter);
            }

            public void setupData(MyWalletModel model) {
                if (model.getEvent().getVenue() != null) {
                    mBinding.venueContainer.setVenueDetail(model.getEvent().getVenue());
                } else {
                    mBinding.venueContainer.setVisibility(View.GONE);
                }

                Graphics.loadImage(model.getEvent().getImage(), mBinding.imgCover);
                mBinding.txtTitle.setText(model.getEvent().getTitle());
                mBinding.tvDescription.setText(model.getEvent().getDescription());
                mBinding.tvTypes.setText(model.getType());

//                mBinding.tvGift.setVisibility(model.getEvent().getPackages().isEmpty() ? View.GONE : View.VISIBLE);
                mBinding.btnRedeem.setVisibility(model.getEvent().getPackages().isEmpty() ? View.GONE : View.VISIBLE);

                if (!model.getItems().isEmpty()) {
                    List<PackageModel> packageModelList = model.getItems().stream()
                            .flatMap(model1 -> model.getEvent().getPackages().stream()
                                    .filter(model2 -> model2.getId().equals(model1.getPackageId()))
                                    .peek(model2 -> {
                                        model2.setRemainingQty(model1.getRemainingQty());
                                        model2.setPrice(model1.getPrice());
                                    }))
                            .collect(Collectors.toList());
                    if (!packageModelList.isEmpty()) {
                        packageAdapter.updateData(packageModelList);
                    }
                } else {
                    mBinding.packageRecycler.setVisibility(View.GONE);
                }

                mBinding.startDate.setText(String.format("Reservation Date : %s", Utils.convertMainDateFormat(model.getEvent().getReservationTime())));
                mBinding.endDate.setText(String.format("Event Date : %s", Utils.convertMainDateFormat(model.getEvent().getEventTime())));
                mBinding.txtTime.setText(String.format("%s - %s", Utils.convertMainTimeFormat(model.getEvent().getReservationTime()), Utils.convertMainTimeFormat(model.getEvent().getEventTime())));
                mBinding.tvDate.setText(Utils.convertDateFormat(model.getEvent().getEventTime(), AppConstants.DATEFORMAT_SHORT, AppConstants.DATEFORMAT_DD_MM_YYYY));
                try {
                    Date day = Utils.stringToDate(model.getEvent().getEventTime(), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    String date = Utils.formatDate(day, "E");
                    mBinding.txtDays.setText(date);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                if (Utils.checkDateGoOrNot(model.getEvent().getEventTime())) {
                    mBinding.tvGift.setText("Expired!");
                    mBinding.tvGift.setBackgroundResource(android.R.color.transparent);
                    mBinding.tvGift.setEnabled(false);
                    mBinding.tvGift.setTypeface(null, Typeface.BOLD);
                    mBinding.tvGift.setTextColor(getResources().getColor(R.color.brand_pink));
                    mBinding.btnRedeem.setVisibility(View.GONE);
                } else {
                    mBinding.btnRedeem.setVisibility(View.VISIBLE);
//                    mBinding.tvGift.setVisibility(View.VISIBLE);
                }

                setListeners(model);

            }

            private void setListeners(MyWalletModel model) {

                mBinding.getRoot().setOnClickListener(view -> {
                    Intent intent = new Intent(requireActivity(), EventDetailsActivity.class);
                    intent.putExtra("eventId", model.getEventId());
                    if (model.getEvent().getEventsOrganizer() != null) {
                        intent.putExtra("name", model.getEvent().getEventsOrganizer().getName());
                        intent.putExtra("address", model.getEvent().getEventsOrganizer().getWebsite());
                        intent.putExtra("image", model.getEvent().getEventsOrganizer().getLogo());
                    }
                    startActivity(intent);

                });

                mBinding.btnRedeem.setOnClickListener(view -> {
                    if (model.getEvent().getPackages().size() > 1) {
                        PackageBottomSheet packageBottomSheet = new PackageBottomSheet();
                        packageBottomSheet.myWalletModel = model;
                        packageBottomSheet.type = "redeem";
                        packageBottomSheet.callback = data -> {
                            if (data != null) {
                                requestMyItem(false);
                            }
                        };
                        packageBottomSheet.show(getChildFragmentManager(), "1");
                    } else {
                        Intent intent = new Intent(getActivity(), RedeemActivity.class);
                        intent.putExtra("itemList", new Gson().toJson(model));
                        intent.putExtra("packageModel", new Gson().toJson(model.getEvent().getPackages().get(0)));
                        activityLauncher.launch(intent, result -> {
                            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                                boolean isClose = result.getData().getBooleanExtra("close", false);
                                if (isClose) {
                                    requestMyItem(false);
                                }
                            }
                        });
                    }

                });

//                mBinding.tvGift.setOnClickListener(view -> {
//                    Utils.preventDoubleClick( view );
//
//                    if (model.getEvent().getPackages().size() > 1) {
//                        PackageBottomSheet packageBottomSheet = new PackageBottomSheet();
//                        packageBottomSheet.myWalletModel = model;
//                        packageBottomSheet.type = "gift";
//                        packageBottomSheet.callback = data -> {
//                            if (data != null) {
//                                requestMyItem();
//                            }
//                        };
//                        packageBottomSheet.show(getChildFragmentManager(), "1");
//                    } else {
//                        SendGiftBottomSheet sendGiftBottomSheet = new SendGiftBottomSheet();
//                        sendGiftBottomSheet.myWalletModel = model;
//                        sendGiftBottomSheet.packageModel = model.getEvent().getPackages().get(0);
//                        sendGiftBottomSheet.callback = data -> {
//                            requestMyItem();
//                        };
//                        sendGiftBottomSheet.show(getChildFragmentManager(), "1");
//                    }
//
//                });

            }


        }

        public class ActivityBlockHolder extends RecyclerView.ViewHolder {
            private final MyItemActivityRecyclerBinding mBinding;

            public ActivityBlockHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = MyItemActivityRecyclerBinding.bind(itemView);
            }

            public void setupData(MyWalletModel model) {

                if (model.getActivity().getProvider() != null) {
                    Graphics.loadImageWithFirstLetter(model.getActivity().getProvider().getLogo(), mBinding.iconImg, model.getActivity().getProvider().getName());
                    mBinding.tvTitle.setText(model.getActivity().getProvider().getName());
                    mBinding.tvAddress.setText(model.getActivity().getProvider().getAddress());
                }

                mBinding.txtDescription.setText(model.getActivity().getDescription());
                mBinding.txtName.setText(model.getActivity().getName());
                Graphics.loadImage(model.getActivity().getGalleries().get(0), mBinding.imgGallary);

                if (!model.getItems().isEmpty()) {
                    int roundedDiscountValue = (int) Math.round(model.getItems().get(0).getPrice());
                    Utils.setStyledText(getActivity(), mBinding.tvDiscountPrice, String.valueOf(roundedDiscountValue));
                    mBinding.txtTime.setText(Utils.convertTimeFormat(model.getItems().get(0).getTime()));
                    mBinding.tvQty.setText(String.valueOf(model.getItems().stream().mapToInt(ItemModel::getRemainingQty).sum()));

                    String detectedFormat = Utils.detectDateFormat(model.getItems().get(0).getDate());
                    if (detectedFormat != null) {
                        String formattedDate = Utils.convertDateFormat(model.getItems().get(0).getDate(), detectedFormat, "E, dd MMM yyyy");
                        mBinding.txtDate.setText(formattedDate);
                    } else {
                        mBinding.txtDate.setText("");
                    }

                    showExpired(model);
                }

                setListeners(model);
            }


            private void setListeners(MyWalletModel model) {
                mBinding.btnRedeem.setOnClickListener(view -> {
                    startActivity(new Intent(context, PurchaseVoucherDetailActivity.class).putExtra("itemList", new Gson().toJson(model)));
                });

                mBinding.getRoot().setOnClickListener(v -> {
                    Intent intent = new Intent(requireActivity(), ActivityListDetail.class);
                    intent.putExtra("activityId", model.getActivity().getId()).putExtra("name", model.getActivity().getName());
                    intent.putExtra("image", model.getActivity().getProvider().getLogo());
                    intent.putExtra("title", model.getActivity().getProvider().getName());
                    intent.putExtra("address", model.getActivity().getProvider().getAddress());
                    startActivity(intent);
                });

//                mBinding.tvGift.setOnClickListener( view -> {
//                    Utils.preventDoubleClick( view );
//
//                    PackageModel packageModel = new PackageModel();
//                    packageModel.setRemainingQty( model.getItems().stream().mapToInt(ItemModel::getRemainingQty).sum() );
//                    SendGiftBottomSheet sendGiftBottomSheet = new SendGiftBottomSheet();
//                    sendGiftBottomSheet.myWalletModel = model;
//                    sendGiftBottomSheet.packageModel = packageModel;
//                    sendGiftBottomSheet.callback = data -> {
//                      requestMyItem();
//                    };
//                    sendGiftBottomSheet.show( getChildFragmentManager(), "1" );
//
//                } );

            }

            private void showExpired(MyWalletModel model) {
                String dateString = model.getItems().get(0).getDate();
                String timeString = model.getItems().get(0).getTime();
                String dateTimeString = dateString + " " + timeString;

                DateTimeFormatter formatter = null;

                if (dateString.length() == 10) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    }
                } else if (dateString.length() == 8) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
                    }
                } else {
                    // Handle the case when the date format is not recognized
                    throw new IllegalArgumentException("Unsupported date format");
                }

                try {
                    LocalDateTime dateTime = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        dateTime = LocalDateTime.parse(dateTimeString, formatter);
                    }

                    // Check if the specified date and time have expired
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        if (dateTime != null && dateTime.isBefore(LocalDateTime.now())) {
                            mBinding.tvGift.setText("Expired!");
                            mBinding.tvGift.setTypeface(null, Typeface.BOLD);
                            mBinding.tvGift.setBackgroundResource(android.R.color.transparent);
                            mBinding.tvGift.setEnabled(false);
                            mBinding.tvGift.setTextColor(getResources().getColor(R.color.brand_pink));
                        } else {
//                            mBinding.tvGift.setVisibility(View.VISIBLE);
                            mBinding.btnRedeem.setVisibility(View.VISIBLE);
                        }
                    }
                } catch (@SuppressLint({"NewApi", "LocalSuppress"}) DateTimeParseException e) {
                    // Handle the case when the parsing fails
                    e.printStackTrace();
                    // Handle the error accordingly
                }
            }

        }

        public class DealBlockHolder extends RecyclerView.ViewHolder {
            private final MyItemRecyclerBinding mBinding;
            private final PackageAdapter<PackageModel> packageAdapter = new PackageAdapter<>();

            public DealBlockHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = MyItemRecyclerBinding.bind(itemView);
                mBinding.packageRecycler.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
                mBinding.packageRecycler.setAdapter(packageAdapter);

            }

            public void setupData(MyWalletModel model) {

                mBinding.txtTitle.setVisibility(View.GONE);
                mBinding.tvDescription.setVisibility(View.GONE);

                if (model.getDeal().getVenue() != null) {
                    mBinding.venueContainer.setVenueDetail(model.getDeal().getVenue());
                } else {
                    mBinding.venueContainer.setVisibility(View.GONE);
                }

                Graphics.loadImage(model.getDeal().getImage(), mBinding.imgCover);
                mBinding.txtDays.setText(model.getDeal().getDays());
                mBinding.tvTypes.setText(model.getType());

                mBinding.startDate.setText(String.format("From Date : %s", Utils.convertDateFormat(model.getDeal().getStartDate(), AppConstants.DATEFORMAT_SHORT, AppConstants.DATEFORMT_MM_DATE)));
                mBinding.endDate.setText(String.format("Till Date : %s", Utils.convertDateFormat(model.getDeal().getEndDate(), AppConstants.DATEFORMAT_SHORT, AppConstants.DATEFORMT_MM_DATE)));
                mBinding.txtTime.setText(String.format("%s - %s", Utils.convertTimeFormat(model.getDeal().getStartTime()), Utils.convertTimeFormat(model.getDeal().getEndTime())));
                mBinding.tvDate.setText(Utils.convertDateFormat(model.getDeal().getEndDate(), AppConstants.DATEFORMAT_SHORT, AppConstants.DATEFORMAT_DD_MM_YYYY));

                setPakageAdapter(model);

                setListeners(model);

            }

            private void setListeners(MyWalletModel model) {
                mBinding.getRoot().setOnClickListener(v -> {
                    startActivity(new Intent(requireActivity(), VoucherDetailScreenActivity.class).putExtra("id", model.getDeal().getId()));
                });

                mBinding.btnRedeem.setOnClickListener(view -> {
                    startActivity(new Intent(context, PurchaseVoucherDetailActivity.class).putExtra("itemList", new Gson().toJson(model)));
                });

            }

            private void setPakageAdapter(MyWalletModel model) {
                if (!model.getItems().isEmpty()) {
                    int item = 0;
                    if (model.getItems() != null) {
                        item = model.getItems().stream().mapToInt(ItemModel::getRemainingQty).sum();
                    }
                    List<PackageModel> list = new ArrayList<>();
                    PackageModel packageModel = new PackageModel();
                    if (model.getDeal() != null) {
                        packageModel.setTitle(model.getDeal().getTitle());
                        packageModel.setDiscount(String.valueOf(model.getDeal().getDiscountValue()));
                        packageModel.setDescription(model.getDeal().getDescription());
                    }
                    packageModel.setRemainingQty(item);
                    packageModel.setPrice(model.getItems().get(0).getPrice());
                    list.add(packageModel);
                    if (list != null && !list.isEmpty()) {
                        packageAdapter.updateData(list);
                    }
                    showExpried(model);

//                    mBinding.tvGift.setOnClickListener( view -> {
//                        Utils.preventDoubleClick( view );
//                        SendGiftBottomSheet sendGiftBottomSheet = new SendGiftBottomSheet();
//                        sendGiftBottomSheet.myWalletModel = model;
//                        sendGiftBottomSheet.packageModel = packageModel;
//                        sendGiftBottomSheet.callback = data -> {
//                          requestMyItem();
//                        };
//                        sendGiftBottomSheet.show( getChildFragmentManager(), "1" );
//                    } );
                }
            }

            private void showExpried(MyWalletModel model) {
                String dateString = model.getDeal().getEndDate();
                String timeString = model.getDeal().getEndTime();
                String dateTimeString = dateString + " " + timeString;

                DateTimeFormatter formatter = null;

                if (dateString.length() == 10) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    }
                } else if (dateString.length() == 8) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
                    }
                } else {
                    // Handle the case when the date format is not recognized
                    throw new IllegalArgumentException("Unsupported date format");
                }

                try {
                    LocalDateTime dateTime = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        dateTime = LocalDateTime.parse(dateTimeString, formatter);
                    }

                    // Check if the specified date and time have expired
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        if (dateTime.isBefore(LocalDateTime.now())) {
                            mBinding.tvGift.setText("Expired!");
                            mBinding.tvGift.setTypeface(null, Typeface.BOLD);
                            mBinding.tvGift.setBackgroundResource(android.R.color.transparent);
                            mBinding.tvGift.setEnabled(false);
                            mBinding.tvGift.setTextColor(getResources().getColor(R.color.brand_pink));
                        }
                    }
                } catch (@SuppressLint({"NewApi", "LocalSuppress"}) DateTimeParseException e) {
                    // Handle the case when the parsing fails
                    e.printStackTrace();
                    // Handle the error accordingly
                }
            }
        }

        public class TicketBlockHolder extends RecyclerView.ViewHolder {

            private final ItemMyTicketsBinding mBinding;

            public TicketBlockHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemMyTicketsBinding.bind(itemView);
            }

            public void setupData(MyWalletModel model) {
                setLangText(mBinding);
                if (model == null) return;
                if (model.getTicket() == null) return;
                if (model.getTicket().getTourDetails() == null && model.getTicket().getTourDetails().isEmpty())
                    return;
                if (model.getTicket().getTourDetails().get(0) == null) return;
                TourOptionDetailModel tourOptionDetailModel = model.getTicket().getTourDetails().get(0).getTour();
                if (tourOptionDetailModel == null) return;

                if (tourOptionDetailModel.getCustomData() != null && tourOptionDetailModel.getCustomData().getImages() != null && !tourOptionDetailModel.getCustomData().getImages().isEmpty()) {
                    Graphics.loadImage(tourOptionDetailModel.getCustomData().getImages().get(0), mBinding.imgGallary);
                }

                if (tourOptionDetailModel.getCustomData() != null) {
                    mBinding.tvTourDescription.setText(Html.fromHtml(tourOptionDetailModel.getCustomData().getDescription()));
                } else {
                    mBinding.tvTourDescription.setVisibility(View.GONE);
                }


                mBinding.tvTourName.setText(tourOptionDetailModel.getTourName());

                mBinding.rvPassengersRecyclerView.setupData(model, getActivity());

                Utils.setStyledText(getActivity(), mBinding.tvAED, Utils.roundFloatValue(model.getTicket().getTotalAmount()));
                Utils.setStyledText(getActivity(), mBinding.tvFinalAmountAED, Utils.roundFloatValue(model.getTicket().getAmount()));
                Utils.setStyledText(getActivity(), mBinding.tvDiscountAED, Utils.roundFloatValue(model.getTicket().getDiscount()));


                if (model.getTicket().getDiscount() <= 0) {
                    mBinding.discountLinear.setVisibility(View.GONE);
                    mBinding.finalAmountLinear.setVisibility(View.GONE);
                } else {
                    mBinding.discountLinear.setVisibility(View.VISIBLE);
                    mBinding.finalAmountLinear.setVisibility(View.VISIBLE);
                }

                if (model.getTicket().getAmount() <= 0) {
                    mBinding.finalAmountLinear.setVisibility(View.GONE);
                } else {
                    mBinding.finalAmountLinear.setVisibility(View.VISIBLE);
                }

                String bookingStatus = model.getTicket().getBookingStatus();
                String paymentStatus = model.getTicket().getPaymentStatus();
                handleBookingStatus(context,mBinding, bookingStatus, paymentStatus);

//                switch (bookingStatus) {
//                    case "initiated":
//                        mBinding.tvInitiaedBookingStatus.setVisibility(View.VISIBLE);
//                        mBinding.tvInitiaedBookingStatus.setText(bookingStatus);
//                        mBinding.btnRedeem.setText(getValue("check_booking_status"));
//                        mBinding.btnRedeem.setEnabled(true);
//                        mBinding.btnRedeem.setTextColor(ContextCompat.getColor(context, R.color.white));
//                        break;
//                    case "rejected":
//                        mBinding.tvInitiaedBookingStatus.setVisibility(View.VISIBLE);
//                        mBinding.tvInitiaedBookingStatus.setText(getValue("pending"));
//                        mBinding.btnRedeem.setText(getValue("check_booking_status"));
//                        mBinding.btnRedeem.setEnabled(true);
//                        mBinding.btnRedeem.setTextColor(ContextCompat.getColor(context, R.color.white));
//                        break;
//
//                    case "confirmed":
//                        if ("paid".equals(paymentStatus)) {
//                            mBinding.btnRedeem.setText(getValue("view_ticket"));
//                            mBinding.btnRedeem.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.button_pink));
//                            mBinding.btnRedeem.setEnabled(true);
//                            mBinding.btnRedeem.setTextColor(ContextCompat.getColor(context, R.color.white));
//                            mBinding.tvInitiaedBookingStatus.setVisibility(View.GONE);
//                        }
//                        break;
//
//                    case "failed":
//                        String title = "refunded".equals(paymentStatus) ? getValue("booking_failed_refunded") : getValue("booking_failed");
//                        mBinding.btnRedeem.setText(title);
//                        mBinding.btnRedeem.setTextColor(ContextCompat.getColor(context, R.color.red));
//                        mBinding.btnRedeem.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.transparent));
//                        mBinding.btnRedeem.setEnabled(false);
//                        mBinding.tvInitiaedBookingStatus.setVisibility(View.GONE);
//                        break;
//                }


                mBinding.btnRedeem.setOnClickListener(view -> {
                    RaynaTicketManager.shared.callback = data -> {
                        if (!TextUtils.isEmpty(data)) {
                            Alerter.create(requireActivity()).setTitle(data).setTitleAppearance(R.style.AlerterTitle).setTextAppearance(R.style.AlerterText).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                        }
                    };
                    startActivity(new Intent(requireActivity(), WalletTicketDetailActivity.class).putExtra("model", new Gson().toJson(model)));
                });

                mBinding.getRoot().setOnClickListener(view -> {
                    Utils.preventDoubleClick(view);
                    startActivity(new Intent(requireActivity(), WalletTicketDetailActivity.class).putExtra("model", new Gson().toJson(model)));
                });

                mBinding.rvPassengersRecyclerView.setOnClickListener(view -> {
                    Utils.preventDoubleClick(view);
                    startActivity(new Intent(requireActivity(), WalletTicketDetailActivity.class).putExtra("model", new Gson().toJson(model)));
                });
            }
        }

        public class WhosinTicketBlockHolder extends RecyclerView.ViewHolder {

            private final ItemMyTicketsBinding mBinding;


            public WhosinTicketBlockHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemMyTicketsBinding.bind(itemView);
            }

            public void setupData(MyWalletModel model) {
                setLangText(mBinding);
                if (model == null) return;
                if (model.getWhosinTicket() == null) return;
                if (model.getWhosinTicket().getTourDetails() == null && model.getWhosinTicket().getTourDetails().isEmpty()) return;
                if (model.getWhosinTicket().getTourDetails().get(0) == null) return;

                mBinding.tvTypes.setText(model.getType());


                RaynaTourDetailModel tourOptionDetailModel = model.getWhosinTicket().getTourDetails().get(0);

                if (tourOptionDetailModel != null) {
                    HomeTicketsModel customModel = tourOptionDetailModel.getCustomData() != null
                            ? tourOptionDetailModel.getCustomData()
                            : tourOptionDetailModel.getCustomTicket();

                    if (customModel != null) {
                        List<String> images = customModel.getImages();
                        if (images != null && !images.isEmpty()) {
                            Graphics.loadImage(images.get(0), mBinding.imgGallary);
                        }
                        mBinding.tvTourName.setText(customModel.getTitle());
                        if (!TextUtils.isEmpty(customModel.getDescription())) {
                            mBinding.tvTourDescription.setText(Html.fromHtml(customModel.getDescription()));
                            mBinding.tvTourDescription.setVisibility(View.VISIBLE);
                        } else {
                            mBinding.tvTourDescription.setVisibility(View.GONE);
                        }
                    }
                }


                mBinding.rvPassengersRecyclerView.setupData(model, getActivity(),true);
                double totalTicketPrice = model.getWhosinTicket().getTourDetails().stream().mapToDouble(ticket -> ticket.getWhosinTotal() != null ? Double.parseDouble(ticket.getWhosinTotal()) : 0.0).sum();
                Utils.setStyledText(getActivity(), mBinding.tvAED, Utils.roundFloatValue((float) totalTicketPrice));
                Utils.setStyledText(getActivity(), mBinding.tvFinalAmountAED, Utils.roundFloatValue(model.getWhosinTicket().getAmount()));
                Utils.setStyledText(getActivity(), mBinding.tvDiscountAED, Utils.roundFloatValue(model.getWhosinTicket().getDiscount()));
                double totalAddonPrice = model.getWhosinTicket().getTourDetails().stream().filter(td -> td.getAddons() != null).flatMap(td -> td.getAddons().stream()).mapToDouble(addon -> addon.getWhosinTotal() != null ? Double.parseDouble(addon.getWhosinTotal()) : 0.0).sum();
                if (totalAddonPrice > 0) {
                    mBinding.addonAmountLinear.setVisibility(View.VISIBLE);
                    Utils.setStyledText(getActivity(), mBinding.tvAddonAmountAED, Utils.roundFloatValue((float) totalAddonPrice));
                } else {
                    mBinding.addonAmountLinear.setVisibility(View.GONE);
                }


                if (model.getWhosinTicket().getDiscount() <= 0) {
                    mBinding.discountLinear.setVisibility(View.GONE);
                    mBinding.finalAmountLinear.setVisibility(View.GONE);
                } else {
                    mBinding.discountLinear.setVisibility(View.VISIBLE);
                    mBinding.finalAmountLinear.setVisibility(View.VISIBLE);
                }

                if (model.getWhosinTicket().getAmount() <= 0) {
                    mBinding.finalAmountLinear.setVisibility(View.GONE);
                } else {
                    mBinding.finalAmountLinear.setVisibility(View.VISIBLE);
                }

                String bookingStatus = model.getWhosinTicket().getBookingStatus();
                String paymentStatus = model.getWhosinTicket().getPaymentStatus();
                handleBookingStatus(context,mBinding, bookingStatus, paymentStatus);

//                switch (bookingStatus) {
//                    case "initiated":
//                        mBinding.tvInitiaedBookingStatus.setVisibility(View.VISIBLE);
//                        mBinding.tvInitiaedBookingStatus.setText(bookingStatus);
//                        mBinding.btnRedeem.setText(getValue("check_booking_status"));
//                        mBinding.btnRedeem.setEnabled(true);
//                        mBinding.btnRedeem.setTextColor(ContextCompat.getColor(context, R.color.white));
//                        break;
//                    case "rejected":
//                        mBinding.tvInitiaedBookingStatus.setVisibility(View.VISIBLE);
//                        mBinding.tvInitiaedBookingStatus.setText(getValue("pending"));
//                        mBinding.btnRedeem.setText(getValue("check_booking_status"));
//                        mBinding.btnRedeem.setEnabled(true);
//                        mBinding.btnRedeem.setTextColor(ContextCompat.getColor(context, R.color.white));
//                        break;
//
//                    case "confirmed":
//                        if ("paid".equals(paymentStatus)) {
//                            mBinding.btnRedeem.setText(getValue("view_ticket"));
//                            mBinding.btnRedeem.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.button_pink));
//                            mBinding.btnRedeem.setEnabled(true);
//                            mBinding.btnRedeem.setTextColor(ContextCompat.getColor(context, R.color.white));
//                            mBinding.tvInitiaedBookingStatus.setVisibility(View.GONE);
//                        }
//                        break;
//
//                    case "failed":
//                        String title = "refunded".equals(paymentStatus) ? getValue("booking_failed_refunded") : getValue("booking_failed");
//                        mBinding.btnRedeem.setText(title);
//                        mBinding.btnRedeem.setTextColor(ContextCompat.getColor(context, R.color.red));
//                        mBinding.btnRedeem.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.transparent));
//                        mBinding.btnRedeem.setEnabled(false);
//                        mBinding.tvInitiaedBookingStatus.setVisibility(View.GONE);
//                        break;
//                }


                mBinding.btnRedeem.setOnClickListener(view -> {
                    RaynaTicketManager.shared.callback = data -> {
                        if (!TextUtils.isEmpty(data)) {
                            Alerter.create(requireActivity()).setTitle(data).setTitleAppearance(R.style.AlerterTitle).setTextAppearance(R.style.AlerterText).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                        }
                    };
                    startActivity(new Intent(requireActivity(), WalletTicketDetailActivity.class).putExtra("model", new Gson().toJson(model)).putExtra("isWhosinBooking",true));
                });

                mBinding.getRoot().setOnClickListener(view -> {
                    Utils.preventDoubleClick(view);
                    startActivity(new Intent(requireActivity(), WalletTicketDetailActivity.class).putExtra("model", new Gson().toJson(model)).putExtra("isWhosinBooking",true));
                });

                mBinding.rvPassengersRecyclerView.setOnClickListener(view -> {
                    Utils.preventDoubleClick(view);
                    startActivity(new Intent(requireActivity(), WalletTicketDetailActivity.class).putExtra("model", new Gson().toJson(model)).putExtra("isWhosinBooking",true));
                });
            }
        }

        public class TravelDeskTicketBlockHolder extends RecyclerView.ViewHolder {

            private final ItemMyTicketsBinding mBinding;


            public TravelDeskTicketBlockHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemMyTicketsBinding.bind(itemView);
            }

            public void setupData(MyWalletModel model) {
                setLangText(mBinding);
                if (model == null) return;

                mBinding.tvTypes.setVisibility(View.GONE);

                if (model.getTraveldeskTicket() == null) return;
                if (model.getTraveldeskTicket().getTourDetails() == null && model.getTraveldeskTicket().getTourDetails().isEmpty()) return;
                if (model.getTraveldeskTicket().getTourDetails().get(0) == null) return;


                RaynaTourDetailModel tourOptionDetailModel = model.getTraveldeskTicket().getTourDetails().get(0);

                if (tourOptionDetailModel != null) {
                    HomeTicketsModel travelDeskOptionDataModel = tourOptionDetailModel.getCustomTicket();

                    if (travelDeskOptionDataModel != null) {
                        String name = travelDeskOptionDataModel.getTitle();
                        mBinding.tvTourName.setText(!TextUtils.isEmpty(name) ? name : "");
                        String description = travelDeskOptionDataModel.getDescription();
                        if (!TextUtils.isEmpty(description)) {
                            mBinding.tvTourDescription.setText(Html.fromHtml(description));
                            mBinding.tvTourDescription.setVisibility(View.VISIBLE);
                        } else {
                            mBinding.tvTourDescription.setVisibility(View.GONE);
                        }

                        if (!travelDeskOptionDataModel.getImages().isEmpty()){
                            String image = travelDeskOptionDataModel.getImages().get(0);
                            Graphics.loadImage(image, mBinding.imgGallary);
                        }

                    } else {

                        mBinding.tvTourDescription.setVisibility(View.GONE);
                        mBinding.tvTourName.setText("");
                    }
                }


                mBinding.rvPassengersRecyclerView.setupData(true,model, getActivity());

                Utils.setStyledText(getActivity(), mBinding.tvAED, Utils.roundFloatValue(model.getTraveldeskTicket().getTotalAmount()));
                Utils.setStyledText(getActivity(), mBinding.tvFinalAmountAED, Utils.roundFloatValue(model.getTraveldeskTicket().getAmount()));
                Utils.setStyledText(getActivity(), mBinding.tvDiscountAED, Utils.roundFloatValue(model.getTraveldeskTicket().getDiscount()));


                if (model.getTraveldeskTicket().getDiscount() <= 0) {
                    mBinding.discountLinear.setVisibility(View.GONE);
                    mBinding.finalAmountLinear.setVisibility(View.GONE);
                } else {
                    mBinding.discountLinear.setVisibility(View.VISIBLE);
                    mBinding.finalAmountLinear.setVisibility(View.VISIBLE);
                }

                if (model.getTraveldeskTicket().getAmount() <= 0) {
                    mBinding.finalAmountLinear.setVisibility(View.GONE);
                } else {
                    mBinding.finalAmountLinear.setVisibility(View.VISIBLE);
                }

                String bookingStatus = model.getTraveldeskTicket().getBookingStatus();
                String paymentStatus = model.getTraveldeskTicket().getPaymentStatus();
                handleBookingStatus(context,mBinding, bookingStatus, paymentStatus);

//                switch (bookingStatus) {
//                    case "initiated":
//                        mBinding.tvInitiaedBookingStatus.setVisibility(View.VISIBLE);
//                        mBinding.tvInitiaedBookingStatus.setText(bookingStatus);
//                        mBinding.btnRedeem.setText(getValue("check_booking_status"));
//                        mBinding.btnRedeem.setEnabled(true);
//                        mBinding.btnRedeem.setTextColor(ContextCompat.getColor(context, R.color.white));
//                        break;
//                    case "rejected":
//                        mBinding.tvInitiaedBookingStatus.setVisibility(View.VISIBLE);
//                        mBinding.tvInitiaedBookingStatus.setText(getValue("pending"));
//                        mBinding.btnRedeem.setText(getValue("check_booking_status"));
//                        mBinding.btnRedeem.setEnabled(true);
//                        mBinding.btnRedeem.setTextColor(ContextCompat.getColor(context, R.color.white));
//                        break;
//
//                    case "confirmed":
//                        if ("paid".equals(paymentStatus)) {
//                            mBinding.btnRedeem.setText(getValue("view_ticket"));
//                            mBinding.btnRedeem.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.button_pink));
//                            mBinding.btnRedeem.setEnabled(true);
//                            mBinding.btnRedeem.setTextColor(ContextCompat.getColor(context, R.color.white));
//                            mBinding.tvInitiaedBookingStatus.setVisibility(View.GONE);
//                        }
//                        break;
//
//                    case "failed":
//                        String title = "refunded".equals(paymentStatus) ? getValue("booking_failed_refunded") : getValue("booking_failed");
//                        mBinding.btnRedeem.setText(title);
//                        mBinding.btnRedeem.setTextColor(ContextCompat.getColor(context, R.color.red));
//                        mBinding.btnRedeem.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.transparent));
//                        mBinding.btnRedeem.setEnabled(false);
//                        mBinding.tvInitiaedBookingStatus.setVisibility(View.GONE);
//                        break;
//                }


                mBinding.btnRedeem.setOnClickListener(view -> {
                    RaynaTicketManager.shared.callback = data -> {
                        if (!TextUtils.isEmpty(data)) {
                            Alerter.create(requireActivity()).setTitle(data).setTitleAppearance(R.style.AlerterTitle).setTextAppearance(R.style.AlerterText).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                        }
                    };
                    startActivity(new Intent(requireActivity(), WalletTicketDetailActivity.class).putExtra("model", new Gson().toJson(model)).putExtra("isTravelDeskBooking",true));
                });

                mBinding.getRoot().setOnClickListener(view -> {
                    Utils.preventDoubleClick(view);
                    startActivity(new Intent(requireActivity(), WalletTicketDetailActivity.class).putExtra("model", new Gson().toJson(model)).putExtra("isTravelDeskBooking",true));
                });

                mBinding.rvPassengersRecyclerView.setOnClickListener(view -> {
                    Utils.preventDoubleClick(view);
                    startActivity(new Intent(requireActivity(), WalletTicketDetailActivity.class).putExtra("model", new Gson().toJson(model)).putExtra("isTravelDeskBooking",true));
                });
            }
        }

        public class BigBusTicketBlockHolder extends RecyclerView.ViewHolder {

            private final ItemMyTicketsBinding mBinding;


            public BigBusTicketBlockHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemMyTicketsBinding.bind(itemView);
            }

            public void setupData(MyWalletModel model) {
                setLangText(mBinding);
                if (model == null) return;

                mBinding.tvTypes.setVisibility(View.GONE);

                if (model.getOctoTicket() == null) return;
                if (model.getOctoTicket().getTourDetails() == null && model.getOctoTicket().getTourDetails().isEmpty()) return;
                if (model.getOctoTicket().getTourDetails().get(0) == null) return;


                RaynaTourDetailModel tourOptionDetailModel = model.getOctoTicket().getTourDetails().get(0);

                if (tourOptionDetailModel != null) {
                    HomeTicketsModel travelDeskOptionDataModel = tourOptionDetailModel.getCustomTicket();

                    if (travelDeskOptionDataModel != null) {
                        String name = travelDeskOptionDataModel.getTitle();
                        mBinding.tvTourName.setText(!TextUtils.isEmpty(name) ? name : "");
                        String description = travelDeskOptionDataModel.getDescription();
                        if (!TextUtils.isEmpty(description)) {
                            mBinding.tvTourDescription.setText(Html.fromHtml(description));
                            mBinding.tvTourDescription.setVisibility(View.VISIBLE);
                        } else {
                            mBinding.tvTourDescription.setVisibility(View.GONE);
                        }

                        if (!travelDeskOptionDataModel.getImages().isEmpty()){
                            String image = travelDeskOptionDataModel.getImages().get(0);
                            Graphics.loadImage(image, mBinding.imgGallary);
                        }

                    } else {
                        mBinding.tvTourDescription.setVisibility(View.GONE);
                        mBinding.tvTourName.setText("");
                    }
                }


                mBinding.rvPassengersRecyclerView.setupDataForBigBus(model, getActivity());

                Utils.setStyledText(getActivity(), mBinding.tvAED, Utils.roundFloatValue(model.getOctoTicket().getTotalAmount()));
                Utils.setStyledText(getActivity(), mBinding.tvFinalAmountAED, Utils.roundFloatValue(model.getOctoTicket().getAmount()));
                Utils.setStyledText(getActivity(), mBinding.tvDiscountAED, Utils.roundFloatValue(model.getOctoTicket().getDiscount()));


                if (model.getOctoTicket().getDiscount() <= 0) {
                    mBinding.discountLinear.setVisibility(View.GONE);
                    mBinding.finalAmountLinear.setVisibility(View.GONE);
                } else {
                    mBinding.discountLinear.setVisibility(View.VISIBLE);
                    mBinding.finalAmountLinear.setVisibility(View.VISIBLE);
                }

                if (model.getOctoTicket().getAmount() <= 0) {
                    mBinding.finalAmountLinear.setVisibility(View.GONE);
                } else {
                    mBinding.finalAmountLinear.setVisibility(View.VISIBLE);
                }

                String bookingStatus = model.getOctoTicket().getBookingStatus();
                String paymentStatus = model.getOctoTicket().getPaymentStatus();
                handleBookingStatus(context,mBinding, bookingStatus, paymentStatus);
//                switch (bookingStatus) {
//                    case "initiated":
//                        mBinding.tvInitiaedBookingStatus.setVisibility(View.VISIBLE);
//                        mBinding.tvInitiaedBookingStatus.setText(bookingStatus);
//                        mBinding.btnRedeem.setText(getValue("check_booking_status"));
//                        mBinding.btnRedeem.setEnabled(true);
//                        mBinding.btnRedeem.setTextColor(ContextCompat.getColor(context, R.color.white));
//                        break;
//                    case "rejected":
//                        mBinding.tvInitiaedBookingStatus.setVisibility(View.VISIBLE);
//                        mBinding.tvInitiaedBookingStatus.setText(getValue("pending"));
//                        mBinding.btnRedeem.setText(getValue("check_booking_status"));
//                        mBinding.btnRedeem.setEnabled(true);
//                        mBinding.btnRedeem.setTextColor(ContextCompat.getColor(context, R.color.white));
//                        break;
//
//                    case "confirmed":
//                        if ("paid".equals(paymentStatus)) {
//                            mBinding.btnRedeem.setText(getValue("view_ticket"));
//                            mBinding.btnRedeem.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.button_pink));
//                            mBinding.btnRedeem.setEnabled(true);
//                            mBinding.btnRedeem.setTextColor(ContextCompat.getColor(context, R.color.white));
//                            mBinding.tvInitiaedBookingStatus.setVisibility(View.GONE);
//                        }
//                        break;
//
//                    case "failed":
//                        String title = "refunded".equals(paymentStatus) ? getValue("booking_failed_refunded") : getValue("booking_failed");
//                        mBinding.btnRedeem.setText(title);
//                        mBinding.btnRedeem.setTextColor(ContextCompat.getColor(context, R.color.red));
//                        mBinding.btnRedeem.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.transparent));
//                        mBinding.btnRedeem.setEnabled(false);
//                        mBinding.tvInitiaedBookingStatus.setVisibility(View.GONE);
//                        break;
//                }


                mBinding.btnRedeem.setOnClickListener(view -> {
                    RaynaTicketManager.shared.callback = data -> {
                        if (!TextUtils.isEmpty(data)) {
                            Alerter.create(requireActivity()).setTitle(data).setTitleAppearance(R.style.AlerterTitle).setTextAppearance(R.style.AlerterText).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                        }
                    };
                    startActivity(new Intent(requireActivity(), WalletTicketDetailActivity.class).putExtra("model", new Gson().toJson(model)).putExtra("isBigBusBooking",true));
                });

                mBinding.getRoot().setOnClickListener(view -> {
                    Utils.preventDoubleClick(view);
                    startActivity(new Intent(requireActivity(), WalletTicketDetailActivity.class).putExtra("model", new Gson().toJson(model)).putExtra("isBigBusBooking",true));
                });

                mBinding.rvPassengersRecyclerView.setOnClickListener(view -> {
                    Utils.preventDoubleClick(view);
                    startActivity(new Intent(requireActivity(), WalletTicketDetailActivity.class).putExtra("model", new Gson().toJson(model)).putExtra("isBigBusBooking",true));
                });
            }
        }

        public class JuniperHotelBlockHolder extends RecyclerView.ViewHolder {

            private final ItemMyTicketsBinding mBinding;


            public JuniperHotelBlockHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemMyTicketsBinding.bind(itemView);

            }

            public void setupData(MyWalletModel model) {
                setLangText(mBinding);

                if (model == null) return;

                mBinding.tvTypes.setVisibility(View.GONE);
                mBinding.rvPassengersRecyclerView.setVisibility(View.GONE);

                if (model.getJuniperHotel() == null) return;
                if (model.getJuniperHotel().getTourDetails() == null && model.getJuniperHotel().getTourDetails().isEmpty()) return;
                if (model.getJuniperHotel().getTourDetails().get(0) == null) return;


                RaynaTourDetailModel tourOptionDetailModel = model.getJuniperHotel().getTourDetails().get(0);

                if (tourOptionDetailModel != null) {

                    HomeTicketsModel travelDeskOptionDataModel = tourOptionDetailModel.getCustomTicket();

                    if (travelDeskOptionDataModel != null) {
                        String name = travelDeskOptionDataModel.getTitle();
                        mBinding.tvTourName.setText(!TextUtils.isEmpty(name) ? name : "");
                        String description = travelDeskOptionDataModel.getDescription();
                        if (!TextUtils.isEmpty(description)) {
                            mBinding.tvTourDescription.setText(Html.fromHtml(description));
                            mBinding.tvTourDescription.setVisibility(View.VISIBLE);
                        } else {
                            mBinding.tvTourDescription.setVisibility(View.GONE);
                        }

                        if (!travelDeskOptionDataModel.getImages().isEmpty()){
                            String image = travelDeskOptionDataModel.getImages().get(0);
                            Graphics.loadImage(image, mBinding.imgGallary);
                        }

                    } else {
                        mBinding.tvTourDescription.setVisibility(View.GONE);
                        mBinding.tvTourName.setText("");
                    }

                    mBinding.jpHotelDetailview.getRoot().setVisibility(View.VISIBLE);

                    String startDate = tourOptionDetailModel.getStartDate();
                    String endDate = tourOptionDetailModel.getEndDate();

                    mBinding.jpHotelDetailview.tvFromDate.setText(startDate);
                    mBinding.jpHotelDetailview.tvToDate.setText(endDate);

                    if (tourOptionDetailModel.getTourData() != null) {
                        String checkIn  = tourOptionDetailModel.getTourData().getCheckIn();
                        String checkOut = tourOptionDetailModel.getTourData().getCheckOut();

                        boolean hasCheckIn  = checkIn != null && !checkIn.trim().isEmpty();
                        boolean hasCheckOut = checkOut != null && !checkOut.trim().isEmpty();

                        if (hasCheckIn || hasCheckOut) {
                            mBinding.jpHotelDetailview.checkInCheckOutLayout.setVisibility(View.VISIBLE);

                            // Check-in section
                            if (hasCheckIn) {
                                mBinding.jpHotelDetailview.checkInLayout.setVisibility(View.VISIBLE);
                                mBinding.jpHotelDetailview.tvCheckInTime.setText(checkIn);
                            } else {
                                mBinding.jpHotelDetailview.checkInLayout.setVisibility(View.GONE);
                            }

                            // Check-out section
                            if (hasCheckOut) {
                                mBinding.jpHotelDetailview.checkOutLayout.setVisibility(View.VISIBLE);
                                mBinding.jpHotelDetailview.tvCheckOutTime.setText(checkOut);
                            } else {
                                mBinding.jpHotelDetailview.checkOutLayout.setVisibility(View.GONE);
                            }

                        } else {
                            // Dono empty/null
                            mBinding.jpHotelDetailview.checkInCheckOutLayout.setVisibility(View.GONE);
                        }
                    } else {
                        // TourData null
                        mBinding.jpHotelDetailview.checkInCheckOutLayout.setVisibility(View.GONE);
                    }

                    String guestName = Utils.setLangValue("numberOfPaxHotel",String.valueOf(tourOptionDetailModel.getAdult()),String.valueOf(tourOptionDetailModel.getChild()));
                    mBinding.jpHotelDetailview.tvGuestDetails.setText(guestName);

                }else {
                    mBinding.jpHotelDetailview.getRoot().setVisibility(View.GONE);
                }

                Utils.setStyledText(getActivity(), mBinding.tvAED, Utils.roundFloatValue(model.getJuniperHotel().getTotalAmount()));
                Utils.setStyledText(getActivity(), mBinding.tvFinalAmountAED, Utils.roundFloatValue(model.getJuniperHotel().getAmount()));
                Utils.setStyledText(getActivity(), mBinding.tvDiscountAED, Utils.roundFloatValue(model.getJuniperHotel().getDiscount()));


                if (model.getJuniperHotel().getDiscount() <= 0) {
                    mBinding.discountLinear.setVisibility(View.GONE);
                    mBinding.finalAmountLinear.setVisibility(View.GONE);
                } else {
                    mBinding.discountLinear.setVisibility(View.VISIBLE);
                    mBinding.finalAmountLinear.setVisibility(View.VISIBLE);
                }

                if (model.getJuniperHotel().getAmount() <= 0) {
                    mBinding.finalAmountLinear.setVisibility(View.GONE);
                } else {
                    mBinding.finalAmountLinear.setVisibility(View.VISIBLE);
                }

                String bookingStatus = model.getJuniperHotel().getBookingStatus();
                String paymentStatus = model.getJuniperHotel().getPaymentStatus();
                handleBookingStatus(context,mBinding, bookingStatus, paymentStatus);

                mBinding.btnRedeem.setOnClickListener(view -> {
                    RaynaTicketManager.shared.callback = data -> {
                        if (!TextUtils.isEmpty(data)) {
                            Alerter.create(requireActivity()).setTitle(data).setTitleAppearance(R.style.AlerterTitle).setTextAppearance(R.style.AlerterText).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                        }
                    };
                    startActivity(new Intent(requireActivity(), WalletTicketDetailActivity.class).putExtra("model", new Gson().toJson(model)).putExtra("isJPHotelBooking", true));
                });

                mBinding.getRoot().setOnClickListener(view -> {
                    Utils.preventDoubleClick(view);
                    startActivity(new Intent(requireActivity(), WalletTicketDetailActivity.class).putExtra("model", new Gson().toJson(model)).putExtra("isJPHotelBooking", true));
                });

            }
        }

    }

    private class PackageAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_wallet_package));
        }

        @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            PackageModel model = (PackageModel) getItem(position);
            viewHolder.mBinding.tvName.setText(Utils.notNullString(model.getTitle()));

            if (!TextUtils.isEmpty(model.getDescription().trim())) {
                viewHolder.mBinding.tvDescription.setText(Utils.notNullString(model.getDescription()).trim());
                viewHolder.mBinding.tvDescription.setVisibility(View.VISIBLE);
            } else {
                viewHolder.mBinding.tvDescription.setVisibility(View.GONE);
            }
            String modifiedString = model.getDiscount().contains("%") ? model.getDiscount() : model.getDiscount() + "%";
            if (model.getDiscount().equals("0")) {
                viewHolder.mBinding.tvDiscount.setVisibility(View.GONE);
            } else {
                viewHolder.mBinding.tvDiscount.setVisibility(View.VISIBLE);
                viewHolder.mBinding.tvDiscount.setText(Utils.notNullString(modifiedString));
            }
            int roundedDiscountValue = (int) Math.round(model.getPrice());

            Utils.setStyledText(getActivity(), viewHolder.mBinding.tvDiscountPrice, String.valueOf(roundedDiscountValue));
            viewHolder.mBinding.tvQty.setText(String.valueOf(model.getRemainingQty()));
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final ItemWalletPackageBinding mBinding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemWalletPackageBinding.bind(itemView);
            }
        }
    }


}

// endregion
// --------------------------------------
