package com.whosin.app.ui.fragment.wallet;

import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.Html;
import android.text.TextUtils;
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
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.FragmentHistoryBinding;
import com.whosin.app.databinding.GiftActivityRecyclerBinding;
import com.whosin.app.databinding.HistoryRecyclerBinding;
import com.whosin.app.databinding.ItemMyTicketsBinding;
import com.whosin.app.databinding.ItemWalletPackageBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.RaynaTicketManager;
import com.whosin.app.service.models.ContainerListModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.HomeTicketsModel;
import com.whosin.app.service.models.ItemModel;
import com.whosin.app.service.models.MyWalletModel;
import com.whosin.app.service.models.NotificationModel;
import com.whosin.app.service.models.PackageModel;
import com.whosin.app.service.models.rayna.RaynaTourDetailModel;
import com.whosin.app.service.models.rayna.TourOptionDetailModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.home.activity.ActivityListDetail;
import com.whosin.app.ui.activites.venue.VenueTimingDialog;
import com.whosin.app.ui.activites.wallet.WalletTicketDetailActivity;
import com.whosin.app.ui.fragment.comman.BaseFragment;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class HistoryFragment extends BaseFragment {

    private FragmentHistoryBinding binding;

    private final HistoryListAdapter<MyWalletModel> historyListAdapter = new HistoryListAdapter<>();

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    public void initUi(View view) {
        binding = FragmentHistoryBinding.bind( view );

        binding.emptyPlaceHolderView.setEmptyPlaceTxtTitle(getValue("empty_history"));

        binding.historyRecycler.setLayoutManager( new LinearLayoutManager( requireActivity(), LinearLayoutManager.VERTICAL, false ) );
        binding.historyRecycler.setAdapter( historyListAdapter );

    }

    @Override
    public void setListeners() {

        binding.swipeRefreshLayout.setOnRefreshListener(this::giftHistoryList);

    }

    @Override
    public void populateData(boolean getDataFromServer) {
        giftHistoryList();
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_history;
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------



    private void filterData(List<MyWalletModel> data){
        List<MyWalletModel> filteredList = data.stream()
                .filter(this::isValidWalletItem)
                .sorted(Comparator.comparing(MyWalletModel::getCreatedAt))
                .collect(Collectors.toList());
        Comparator<MyWalletModel> dateComparator = Comparator.comparing(MyWalletModel::getCreatedAt).reversed();
        filteredList.sort(dateComparator);

        if (!filteredList.isEmpty()) {
            historyListAdapter.updateData(filteredList);
        } else {
            binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
            binding.historyRecycler.setVisibility(View.GONE);
        }

    }

    private boolean isValidWalletItem(MyWalletModel model) {
        if (model == null) return false;

        String type = model.getType();
        if (type == null) return model.getEvent() != null;

        switch (type) {
            case "ticket":
                return model.getTicket() != null;

            case "big-bus":
            case "hero-balloon":
                return model.getOctoTicket() != null;

            case "juniper-hotel":
                return model.getJuniperHotel() != null;

            case "whosin-ticket":
                return model.getWhosinTicket() != null;

            case "travel-desk":
                return model.getTraveldeskTicket() != null;

            default:
                return model.getEvent() != null;
        }
    }


    private void openActionSheet(MyWalletModel model) {
        ArrayList<String> data = new ArrayList<>();
        data.add(getValue("delete"));
        Graphics.showActionSheet(requireActivity(), requireActivity().getString(R.string.app_name), data, (data1, position1) -> {
            if (position1 == 0) {
                Graphics.showAlertDialogWithOkCancel(requireActivity(), getString(R.string.app_name), getValue("delete_order_history_confirm"),getValue("yes"), getValue("no"), isConfirmed -> {
                    if (isConfirmed) {
                        requestDeleteSubscriptionOrder(model);
                    }
                });

            }
        });
    }

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    public void giftHistoryList() {
        DataService.shared( requireActivity() ).requestHistoryList( new RestCallback<ContainerListModel<MyWalletModel>>(this) {
            @Override
            public void result(ContainerListModel<MyWalletModel> model, String error) {

                if (!Utils.isNullOrEmpty( error ) || model == null) {
                    binding.swipeRefreshLayout.setRefreshing( false );
                    if (Graphics.context != null) {
                        Toast.makeText(Graphics.context, error, Toast.LENGTH_SHORT).show();
                        binding.emptyPlaceHolderView.setVisibility( View.VISIBLE );
                        binding.historyRecycler.setVisibility( View.GONE );
                    }
//                    if (isHistoryPresent != null) isHistoryPresent.onReceive(false);
                    return;
                }

                binding.swipeRefreshLayout.setRefreshing( false );
                if (model.data != null && !model.data.isEmpty()) {
                    binding.emptyPlaceHolderView.setVisibility( View.GONE );
                    binding.historyRecycler.setVisibility( View.VISIBLE );
                    filterData(model.data);
//                    if (isHistoryPresent != null) isHistoryPresent.onReceive(true);
                } else {
                    binding.emptyPlaceHolderView.setVisibility( View.VISIBLE );
                    binding.historyRecycler.setVisibility( View.GONE );
//                    if (isHistoryPresent != null) isHistoryPresent.onReceive(false);
                }

            }
        } );


    }

    private void requestDeleteSubscriptionOrder(MyWalletModel myWalletModel) {
        List<String> id = new ArrayList<>();
        id.add(myWalletModel.getOrderId());
        showProgress();
        DataService.shared(requireActivity()).requestDeleteSubscriptionOrder(id, new RestCallback<ContainerModel<NotificationModel>>(this) {
            @Override
            public void result(ContainerModel<NotificationModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(requireActivity(), error, Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(requireActivity(), model.message, Toast.LENGTH_SHORT).show();
                if (historyListAdapter.getData() != null && !historyListAdapter.getData().isEmpty()) {
                    historyListAdapter.getData().removeIf(p -> p.getOrderId().equals(myWalletModel.getOrderId()));
                    historyListAdapter.notifyDataSetChanged();
                    binding.swipeRefreshLayout.setVisibility(historyListAdapter.getData().isEmpty() ? View.GONE : VISIBLE);
                    binding.emptyPlaceHolderView.setVisibility(historyListAdapter.getData().isEmpty() ? VISIBLE : View.GONE);
//                    if (isHistoryPresent != null){
//                        isHistoryPresent.onReceive(!historyListAdapter.getData().isEmpty());
//                    }
                }
            }
        });
    }

    private void requestDeleteSubscriptionOrder(List<MyWalletModel> notificationList) {
        List<String> id = new ArrayList<>();
        for (MyWalletModel model : notificationList) {
            if (!TextUtils.isEmpty(model.getOrderId())) id.add(model.getOrderId());
        }
        showProgress();
        DataService.shared(requireActivity()).requestDeleteSubscriptionOrder(id, new RestCallback<ContainerModel<NotificationModel>>(this) {
            @Override
            public void result(ContainerModel<NotificationModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(requireActivity(), error, Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(requireActivity(), model.message, Toast.LENGTH_SHORT).show();
                if (historyListAdapter.getData() != null && !historyListAdapter.getData().isEmpty()) {
                    historyListAdapter.getData().clear();
                    historyListAdapter.notifyDataSetChanged();
                    giftHistoryList();
                }
            }
        });
    }

    private void setLangText(ItemMyTicketsBinding mBinding){
        mBinding.tvBookingStatus.setText(getValue("cancelled"));
        mBinding.tvPaymentStatus.setText(getValue("paid"));
        mBinding.tvFinalAmountTitle.setText(getValue("final_amount"));
        mBinding.tvTitleDiscount.setText(getValue("discount"));
        mBinding.tvTotalTitle.setText(getValue("total_amount"));
    }

    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    private class HistoryListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            switch (AppConstants.OrderListType.valueOf( viewType )) {
                case TICKET:
                    return new TicketBlockHolder( UiUtils.getViewBy( parent, R.layout.item_my_tickets ) );
                case WHOSIN_TICKET:
                    return new WhosinTicketBlockHolder(UiUtils.getViewBy(parent, R.layout.item_my_tickets));
                case TRAVEL_DESK:
                    return new TravelDeskTicketBlockHolder(UiUtils.getViewBy(parent, R.layout.item_my_tickets));
                case BIG_BUS:
                case HERO_BALLOON:
                    return new BigBusTicketBlockHolder(UiUtils.getViewBy(parent, R.layout.item_my_tickets));
                case JUNIPER_HOTEL:
                    return new JPHotelBlockHolder(UiUtils.getViewBy(parent, R.layout.item_my_tickets));
            }
            return null;
        }

        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            MyWalletModel model = (MyWalletModel) getItem( position );
            boolean isLastItem = position == getItemCount() - 1;
            if (model.getOrderListType() == AppConstants.OrderListType.TICKET) {
                TicketBlockHolder viewHolder = (TicketBlockHolder) holder;
                viewHolder.mBinding.iconMenu.setVisibility(View.VISIBLE);
                viewHolder.mBinding.iconMenu.setOnClickListener(v -> openActionSheet(model));
                viewHolder.setupData( model );
            }else if (model.getOrderListType() == AppConstants.OrderListType.WHOSIN_TICKET) {
                WhosinTicketBlockHolder viewHolder = (WhosinTicketBlockHolder) holder;
                viewHolder.mBinding.iconMenu.setVisibility(View.VISIBLE);
                viewHolder.mBinding.iconMenu.setOnClickListener(v -> openActionSheet(model));
                viewHolder.setupData(model);
            }else if (model.getOrderListType() == AppConstants.OrderListType.TRAVEL_DESK) {
                TravelDeskTicketBlockHolder viewHolder = (TravelDeskTicketBlockHolder) holder;
                viewHolder.mBinding.iconMenu.setVisibility(View.VISIBLE);
                viewHolder.mBinding.iconMenu.setOnClickListener(v -> openActionSheet(model));
                viewHolder.setupData(model);
            }else if (model.getOrderListType() == AppConstants.OrderListType.BIG_BUS || model.getOrderListType() == AppConstants.OrderListType.HERO_BALLOON) {
                BigBusTicketBlockHolder viewHolder = (BigBusTicketBlockHolder) holder;
                viewHolder.mBinding.iconMenu.setVisibility(View.VISIBLE);
                viewHolder.mBinding.iconMenu.setOnClickListener(v -> openActionSheet(model));
                viewHolder.setupData(model);
            }else if (model.getOrderListType() == AppConstants.OrderListType.JUNIPER_HOTEL) {
                JPHotelBlockHolder viewHolder = (JPHotelBlockHolder) holder;
                viewHolder.mBinding.iconMenu.setVisibility(View.VISIBLE);
                viewHolder.mBinding.iconMenu.setOnClickListener(v -> openActionSheet(model));
                viewHolder.setupData(model);
            }


            if (isLastItem) {
                int marginBottom = Utils.getMarginBottom( holder.itemView.getContext(), 0.10f );
                Utils.setBottomMargin( holder.itemView, marginBottom );
            } else {
                Utils.setBottomMargin( holder.itemView, 0 );
            }
        }

        @Override
        public int getItemViewType(int position) {
            MyWalletModel model = (MyWalletModel) getItem( position );
            return model.getOrderListType().getValue();
        }

        public class TicketBlockHolder extends RecyclerView.ViewHolder {

            private final ItemMyTicketsBinding mBinding;


            public TicketBlockHolder(@NonNull View itemView) {
                super( itemView );
                mBinding = ItemMyTicketsBinding.bind( itemView );
                setLangText(mBinding);
            }

            public void setupData(MyWalletModel model) {
                mBinding.btnRedeem.setVisibility(View.GONE);
                mBinding.bookingStatusLayout.setVisibility(View.VISIBLE);

                if (model == null) return;
                if (model.getTicket() == null) return;
                if (model.getTicket().getTourDetails() == null && model.getTicket().getTourDetails().isEmpty()) return;
                if (model.getTicket().getTourDetails().get(0) == null) return;

                TourOptionDetailModel tourOptionDetailModel = model.getTicket().getTourDetails().get(0).getTour();

                mBinding.rvPassengersRecyclerView.isFromHistory = true;
                mBinding.rvPassengersRecyclerView.setupData(model, getActivity());

                if (tourOptionDetailModel != null && tourOptionDetailModel.getCustomData() != null && tourOptionDetailModel.getCustomData().getImages() != null && !tourOptionDetailModel.getCustomData().getImages().isEmpty()){
                    Graphics.loadImage(tourOptionDetailModel.getCustomData().getImages().get(0), mBinding.imgGallary);
                }

                if (tourOptionDetailModel != null && tourOptionDetailModel.getCustomData() != null){
                    mBinding.tvTourDescription.setText(Html.fromHtml(tourOptionDetailModel.getCustomData().getDescription()));
                } else {
                    mBinding.tvTourDescription.setVisibility(View.GONE);
                }

                mBinding.tvTourName.setText(tourOptionDetailModel.getTourName());

                Utils.setStyledText(getActivity(),mBinding.tvAED,Utils.roundFloatValue(model.getTicket().getTotalAmount()));
                Utils.setStyledText(getActivity(),mBinding.tvFinalAmountAED,Utils.roundFloatValue(model.getTicket().getAmount()));
                Utils.setStyledText(getActivity(),mBinding.tvDiscountAED,Utils.roundFloatValue(model.getTicket().getDiscount()));



                if (model.getTicket().getDiscount() <= 0) {
                    mBinding.discountLinear.setVisibility(View.GONE);
                    mBinding.finalAmountLinear.setVisibility(View.GONE);
                } else {
                    mBinding.discountLinear.setVisibility(View.VISIBLE);
                    mBinding.finalAmountLinear.setVisibility(View.VISIBLE);
                }

                if (model.getTicket().getAmount() <= 0){
                    mBinding.finalAmountLinear.setVisibility(View.GONE);
                }else {
                    mBinding.finalAmountLinear.setVisibility(View.VISIBLE);
                }

                if (!TextUtils.isEmpty(model.getTicket().getBookingStatus())){
                    mBinding.tvBookingStatus.setVisibility(View.VISIBLE);
                    mBinding.tvBookingStatus.setText(model.getTicket().getBookingStatus());
                }else {
                    mBinding.tvBookingStatus.setVisibility(View.GONE);
                }

                if (!TextUtils.isEmpty(model.getTicket().getPaymentStatus())){
                    mBinding.tvPaymentStatus.setVisibility(View.VISIBLE);
                    mBinding.tvPaymentStatus.setText( "(" + model.getTicket().getPaymentStatus() + ")");
                }else {
                    mBinding.tvPaymentStatus.setVisibility(View.GONE);
                }

                if (Objects.equals(model.getTicket().getPaymentStatus(), "paid") && Objects.equals(model.getTicket().getBookingStatus(), "completed")){
                    mBinding.tvPaymentStatus.setVisibility(View.GONE);
                    mBinding.tvBookingStatus.setVisibility(View.VISIBLE);
                    mBinding.tvBookingStatus.setText(model.getTicket().getBookingStatus());
                }


                mBinding.getRoot().setOnClickListener(view -> {
                    Utils.preventDoubleClick(view);
                    startActivity(new Intent(requireActivity(), WalletTicketDetailActivity.class).putExtra("model", new Gson().toJson(model)).putExtra("isFromHistory",true));
                });

                mBinding.getRoot().setOnClickListener(view -> {
                    Utils.preventDoubleClick(view);
                    startActivity(new Intent(requireActivity(), WalletTicketDetailActivity.class).putExtra("model", new Gson().toJson(model)).putExtra("isFromHistory",true));
                });

                mBinding.rvPassengersRecyclerView.setOnClickListener(view -> {
                    Utils.preventDoubleClick(view);
                    startActivity(new Intent(requireActivity(), WalletTicketDetailActivity.class).putExtra("model", new Gson().toJson(model)).putExtra("isFromHistory",true));
                });
            }

        }

        public class WhosinTicketBlockHolder extends RecyclerView.ViewHolder {

            private final ItemMyTicketsBinding mBinding;


            public WhosinTicketBlockHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemMyTicketsBinding.bind(itemView);
                setLangText(mBinding);
            }

            public void setupData(MyWalletModel model) {
                mBinding.btnRedeem.setVisibility(View.GONE);
                mBinding.bookingStatusLayout.setVisibility(View.VISIBLE);

                if (model == null) return;
                if (model.getWhosinTicket() == null) return;
                if (model.getWhosinTicket().getTourDetails() == null && model.getWhosinTicket().getTourDetails().isEmpty()) return;
                if (model.getWhosinTicket().getTourDetails().get(0) == null) return;

                mBinding.tvTypes.setText(model.getType());


//                RaynaTourDetailModel tourOptionDetailModel = model.getWhosinTicket().getTourDetails().get(0);
//
//                if (tourOptionDetailModel != null && tourOptionDetailModel.getCustomData() != null && tourOptionDetailModel.getCustomData().getImages() != null && !tourOptionDetailModel.getCustomData().getImages().isEmpty()) {
//                    Graphics.loadImage(tourOptionDetailModel.getCustomData().getImages().get(0), mBinding.imgGallary);
//                }
//
//
//                if (tourOptionDetailModel != null){
//                    HomeTicketsModel customModel = tourOptionDetailModel.getCustomData();
//                    if (customModel != null && !TextUtils.isEmpty(customModel.getDescription())) {
//                        mBinding.tvTourDescription.setText(Html.fromHtml(customModel.getDescription()));
//                        mBinding.tvTourDescription.setVisibility(View.VISIBLE);
//                        mBinding.tvTourName.setText(customModel.getTitle());
//                    } else {
//                        mBinding.tvTourDescription.setVisibility(View.GONE);
//                    }
//
//
//
//                }

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

                        if (!TextUtils.isEmpty(customModel.getDescription())) {
                            mBinding.tvTourDescription.setText(Html.fromHtml(customModel.getDescription()));
                            mBinding.tvTourName.setText(customModel.getTitle());
                            mBinding.tvTourDescription.setVisibility(View.VISIBLE);
                        } else {
                            mBinding.tvTourDescription.setVisibility(View.GONE);
                        }
                    }
                }

                mBinding.rvPassengersRecyclerView.isFromHistory = true;
                mBinding.rvPassengersRecyclerView.setupData(model, getActivity(),true);

                Utils.setStyledText(getActivity(), mBinding.tvAED, Utils.roundFloatValue(model.getWhosinTicket().getTotalAmount()));
                Utils.setStyledText(getActivity(), mBinding.tvFinalAmountAED, Utils.roundFloatValue(model.getWhosinTicket().getAmount()));
                Utils.setStyledText(getActivity(), mBinding.tvDiscountAED, Utils.roundFloatValue(model.getWhosinTicket().getDiscount()));


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

                if (!TextUtils.isEmpty(model.getWhosinTicket().getBookingStatus())){
                    mBinding.tvBookingStatus.setVisibility(View.VISIBLE);
                    mBinding.tvBookingStatus.setText(model.getWhosinTicket().getBookingStatus());
                }else {
                    mBinding.tvBookingStatus.setVisibility(View.GONE);
                }

                if (!TextUtils.isEmpty(model.getWhosinTicket().getPaymentStatus())){
                    mBinding.tvPaymentStatus.setVisibility(View.VISIBLE);
                    mBinding.tvPaymentStatus.setText( "(" + model.getWhosinTicket().getPaymentStatus() + ")");
                }else {
                    mBinding.tvPaymentStatus.setVisibility(View.GONE);
                }

                if (Objects.equals(model.getWhosinTicket().getPaymentStatus(), "paid") && Objects.equals(model.getWhosinTicket().getBookingStatus(), "completed")){
                    mBinding.tvPaymentStatus.setVisibility(View.GONE);
                    mBinding.tvBookingStatus.setVisibility(View.VISIBLE);
                    mBinding.tvBookingStatus.setText(model.getWhosinTicket().getBookingStatus());
                }

                mBinding.btnRedeem.setOnClickListener(view -> {
                    RaynaTicketManager.shared.callback = data -> {
                        if (!TextUtils.isEmpty(data)) {
                            Alerter.create(requireActivity()).setTitle(data).setTitleAppearance(R.style.AlerterTitle).setTextAppearance(R.style.AlerterText).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                        }
                    };
                    startActivity(new Intent(requireActivity(), WalletTicketDetailActivity.class).putExtra("model", new Gson().toJson(model)).putExtra("isWhosinBooking",true).putExtra("isFromHistory",true));
                });

                mBinding.getRoot().setOnClickListener(view -> {
                    Utils.preventDoubleClick(view);
                    startActivity(new Intent(requireActivity(), WalletTicketDetailActivity.class).putExtra("model", new Gson().toJson(model)).putExtra("isWhosinBooking",true).putExtra("isFromHistory",true));
                });

                mBinding.rvPassengersRecyclerView.setOnClickListener(view -> {
                    Utils.preventDoubleClick(view);
                    startActivity(new Intent(requireActivity(), WalletTicketDetailActivity.class).putExtra("model", new Gson().toJson(model)).putExtra("isWhosinBooking",true).putExtra("isFromHistory",true));
                });
            }
        }

        public class TravelDeskTicketBlockHolder extends RecyclerView.ViewHolder {

            private final ItemMyTicketsBinding mBinding;


            public TravelDeskTicketBlockHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemMyTicketsBinding.bind(itemView);
                setLangText(mBinding);
            }

            public void setupData(MyWalletModel model) {
                mBinding.btnRedeem.setVisibility(View.GONE);
                mBinding.bookingStatusLayout.setVisibility(View.VISIBLE);
                mBinding.tvTypes.setVisibility(View.GONE);

                if (model == null) return;
                if (model.getTraveldeskTicket() == null) return;
                if (model.getTraveldeskTicket().getTourDetails() == null && model.getTraveldeskTicket().getTourDetails().isEmpty()) return;
                if (model.getTraveldeskTicket().getTourDetails().get(0) == null) return;

                mBinding.tvTypes.setText(model.getType());


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

                mBinding.rvPassengersRecyclerView.isFromHistory = true;
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

                if (!TextUtils.isEmpty(model.getTraveldeskTicket().getBookingStatus())){
                    mBinding.tvBookingStatus.setVisibility(View.VISIBLE);
                    mBinding.tvBookingStatus.setText(model.getTraveldeskTicket().getBookingStatus());
                }else {
                    mBinding.tvBookingStatus.setVisibility(View.GONE);
                }

                if (!TextUtils.isEmpty(model.getTraveldeskTicket().getPaymentStatus())){
                    mBinding.tvPaymentStatus.setVisibility(View.VISIBLE);
                    mBinding.tvPaymentStatus.setText( "(" + model.getTraveldeskTicket().getPaymentStatus() + ")");
                }else {
                    mBinding.tvPaymentStatus.setVisibility(View.GONE);
                }

                if (Objects.equals(model.getTraveldeskTicket().getPaymentStatus(), "paid") && Objects.equals(model.getTraveldeskTicket().getBookingStatus(), "completed")){
                    mBinding.tvPaymentStatus.setVisibility(View.GONE);
                    mBinding.tvBookingStatus.setVisibility(View.VISIBLE);
                    mBinding.tvBookingStatus.setText(model.getTraveldeskTicket().getBookingStatus());
                }

                mBinding.btnRedeem.setOnClickListener(view -> {
                    RaynaTicketManager.shared.callback = data -> {
                        if (!TextUtils.isEmpty(data)) {
                            Alerter.create(requireActivity()).setTitle(data).setTitleAppearance(R.style.AlerterTitle).setTextAppearance(R.style.AlerterText).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                        }
                    };
                    startActivity(new Intent(requireActivity(), WalletTicketDetailActivity.class).putExtra("model", new Gson().toJson(model)).putExtra("isTravelDeskBooking",true).putExtra("isFromHistory",true));
                });

                mBinding.getRoot().setOnClickListener(view -> {
                    Utils.preventDoubleClick(view);
                    startActivity(new Intent(requireActivity(), WalletTicketDetailActivity.class).putExtra("model", new Gson().toJson(model)).putExtra("isTravelDeskBooking",true).putExtra("isFromHistory",true));
                });

                mBinding.rvPassengersRecyclerView.setOnClickListener(view -> {
                    Utils.preventDoubleClick(view);
                    startActivity(new Intent(requireActivity(), WalletTicketDetailActivity.class).putExtra("model", new Gson().toJson(model)).putExtra("isTravelDeskBooking",true).putExtra("isFromHistory",true));
                });
            }
        }

        public class BigBusTicketBlockHolder extends RecyclerView.ViewHolder {

            private final ItemMyTicketsBinding mBinding;


            public BigBusTicketBlockHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemMyTicketsBinding.bind(itemView);
                setLangText(mBinding);
            }

            public void setupData(MyWalletModel model) {
                if (model == null) return;

                mBinding.btnRedeem.setVisibility(View.GONE);
                mBinding.bookingStatusLayout.setVisibility(View.VISIBLE);
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


                mBinding.rvPassengersRecyclerView.isFromHistory = true;
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

                if (!TextUtils.isEmpty(model.getOctoTicket().getBookingStatus())){
                    mBinding.tvBookingStatus.setVisibility(View.VISIBLE);
                    mBinding.tvBookingStatus.setText(model.getOctoTicket().getBookingStatus());
                }else {
                    mBinding.tvBookingStatus.setVisibility(View.GONE);
                }

                if (!TextUtils.isEmpty(model.getOctoTicket().getPaymentStatus())){
                    mBinding.tvPaymentStatus.setVisibility(View.VISIBLE);
                    mBinding.tvPaymentStatus.setText( "(" + model.getOctoTicket().getPaymentStatus() + ")");
                }else {
                    mBinding.tvPaymentStatus.setVisibility(View.GONE);
                }

                if (Objects.equals(model.getOctoTicket().getPaymentStatus(), "paid") && Objects.equals(model.getOctoTicket().getBookingStatus(), "completed")){
                    mBinding.tvPaymentStatus.setVisibility(View.GONE);
                    mBinding.tvBookingStatus.setVisibility(View.VISIBLE);
                    mBinding.tvBookingStatus.setText(model.getOctoTicket().getBookingStatus());
                }

                mBinding.btnRedeem.setOnClickListener(view -> {
                    RaynaTicketManager.shared.callback = data -> {
                        if (!TextUtils.isEmpty(data)) {
                            Alerter.create(requireActivity()).setTitle(data).setTitleAppearance(R.style.AlerterTitle).setTextAppearance(R.style.AlerterText).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                        }
                    };
                    startActivity(new Intent(requireActivity(), WalletTicketDetailActivity.class).putExtra("model", new Gson().toJson(model)).putExtra("isBigBusBooking",true).putExtra("isFromHistory",true));
                });

                mBinding.getRoot().setOnClickListener(view -> {
                    Utils.preventDoubleClick(view);
                    startActivity(new Intent(requireActivity(), WalletTicketDetailActivity.class).putExtra("model", new Gson().toJson(model)).putExtra("isBigBusBooking",true).putExtra("isFromHistory",true));
                });

                mBinding.rvPassengersRecyclerView.setOnClickListener(view -> {
                    Utils.preventDoubleClick(view);
                    startActivity(new Intent(requireActivity(), WalletTicketDetailActivity.class).putExtra("model", new Gson().toJson(model)).putExtra("isBigBusBooking",true).putExtra("isFromHistory",true));
                });
            }
        }

        public class JPHotelBlockHolder extends RecyclerView.ViewHolder {

            private final ItemMyTicketsBinding mBinding;


            public JPHotelBlockHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemMyTicketsBinding.bind(itemView);
                setLangText(mBinding);
            }

            public void setupData(MyWalletModel model) {
                if (model == null) return;

                mBinding.btnRedeem.setVisibility(View.GONE);
                mBinding.bookingStatusLayout.setVisibility(View.VISIBLE);
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

                    mBinding.jpHotelDetailview.getRoot().setVisibility(VISIBLE);
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

                if (!TextUtils.isEmpty(model.getJuniperHotel().getBookingStatus())){
                    mBinding.tvBookingStatus.setVisibility(View.VISIBLE);
                    mBinding.tvBookingStatus.setText(model.getJuniperHotel().getBookingStatus());
                }else {
                    mBinding.tvBookingStatus.setVisibility(View.GONE);
                }

                if (!TextUtils.isEmpty(model.getJuniperHotel().getPaymentStatus())){
                    mBinding.tvPaymentStatus.setVisibility(View.VISIBLE);
                    mBinding.tvPaymentStatus.setText( "(" + model.getJuniperHotel().getPaymentStatus() + ")");
                }else {
                    mBinding.tvPaymentStatus.setVisibility(View.GONE);
                }

                if (Objects.equals(model.getJuniperHotel().getPaymentStatus(), "paid") && Objects.equals(model.getJuniperHotel().getBookingStatus(), "completed")){
                    mBinding.tvPaymentStatus.setVisibility(View.GONE);
                    mBinding.tvBookingStatus.setVisibility(View.VISIBLE);
                    mBinding.tvBookingStatus.setText(model.getJuniperHotel().getBookingStatus());
                }

                mBinding.btnRedeem.setOnClickListener(view -> {
                    RaynaTicketManager.shared.callback = data -> {
                        if (!TextUtils.isEmpty(data)) {
                            Alerter.create(requireActivity()).setTitle(data).setTitleAppearance(R.style.AlerterTitle).setTextAppearance(R.style.AlerterText).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                        }
                    };
                    startActivity(new Intent(requireActivity(), WalletTicketDetailActivity.class).putExtra("model", new Gson().toJson(model)).putExtra("isJPHotelBooking",true).putExtra("isFromHistory",true));
                });

                mBinding.getRoot().setOnClickListener(view -> {
                    Utils.preventDoubleClick(view);
                    startActivity(new Intent(requireActivity(), WalletTicketDetailActivity.class).putExtra("model", new Gson().toJson(model)).putExtra("isJPHotelBooking",true).putExtra("isFromHistory",true));
                });

            }
        }

    }

    // endregion
    // --------------------------------------
}