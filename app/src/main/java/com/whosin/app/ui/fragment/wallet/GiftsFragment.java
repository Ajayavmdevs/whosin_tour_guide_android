package com.whosin.app.ui.fragment.wallet;

import static android.app.Activity.RESULT_OK;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.whosin.app.R;
import com.whosin.app.comman.AppConstants;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.FragmentGiftsBinding;
import com.whosin.app.databinding.GiftActivityRecyclerBinding;
import com.whosin.app.databinding.GiftRecyclerBinding;
import com.whosin.app.databinding.ItemWalletPackageBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.ContainerListModel;
import com.whosin.app.service.models.ItemModel;
import com.whosin.app.service.models.MyWalletModel;
import com.whosin.app.service.models.PackageModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.Profile.OtherUserProfileActivity;
import com.whosin.app.ui.activites.home.activity.ActivityListDetail;
import com.whosin.app.ui.activites.home.event.EventDetailsActivity;
import com.whosin.app.ui.activites.offers.OfferDetailBottomSheet;
import com.whosin.app.ui.activites.offers.VoucherDetailScreenActivity;
import com.whosin.app.ui.activites.venue.VenueTimingDialog;
import com.whosin.app.ui.activites.wallet.PurchaseVoucherDetailActivity;
import com.whosin.app.ui.activites.wallet.RedeemActivity;
import com.whosin.app.ui.fragment.comman.BaseFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

public class GiftsFragment extends BaseFragment {

    private FragmentGiftsBinding binding;
    private final VoucherGiftListAdapter<MyWalletModel> giftListAdapter = new VoucherGiftListAdapter<>();

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    public void initUi(View view) {
        binding = FragmentGiftsBinding.bind(view);
        binding.giftRecycler.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false));
        binding.giftRecycler.setAdapter(giftListAdapter);
    }

    @Override
    public void setListeners() {
        binding.swipeRefreshLayout.setOnRefreshListener(() -> giftVoucherList());
    }

    @Override
    public void populateData(boolean getDataFromServer) {
        giftVoucherList();
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_gifts;
    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void filterData(List<MyWalletModel> data){
        List<MyWalletModel> filteredList = data.stream().filter(model -> (model.getType().equals("deal") && model.getDeal() != null) || (model.getType().equals("activity") && model.getActivity() != null) || (model.getType().equals("offer") && model.getOffer() != null) || (model.getEvent() != null)).sorted(Comparator.comparing(MyWalletModel::getCreatedAt)).collect(Collectors.toList());
        if (!filteredList.isEmpty()) {
            giftListAdapter.updateData(filteredList);
        } else {
            binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
            binding.giftRecycler.setVisibility(View.GONE);
        }

    }


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void giftVoucherList() {
        Log.d("TAG", "giftVoucherList: "+ SessionManager.shared.getToken());
        DataService.shared(requireActivity()).requestWalletGift(new RestCallback<ContainerListModel<MyWalletModel>>(this) {
            @Override
            public void result(ContainerListModel<MyWalletModel> model, String error) {
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    binding.swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(requireActivity(), R.string.service_message_something_wrong, Toast.LENGTH_SHORT).show();
                    return;
                }
                binding.swipeRefreshLayout.setRefreshing(false);
                if (model.data != null && !model.data.isEmpty()) {
                    binding.emptyPlaceHolderView.setVisibility(View.GONE);
                    binding.giftRecycler.setVisibility(View.VISIBLE);
                    filterData(model.data);
                 } else {
                    binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
                    binding.giftRecycler.setVisibility(View.GONE);
                }

            }
        });


    }


    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------


    public class VoucherGiftListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            switch (Objects.requireNonNull(AppConstants.OrderListType.valueOf(viewType))) {
                case OFFER:
                    return new OfferBlockHolder(UiUtils.getViewBy(parent, R.layout.gift_recycler));
                case ACTIVITY:
                    return new ActivityBlockHolder(UiUtils.getViewBy(parent, R.layout.gift_activity_recycler));
                case DEAL:
                    return new DealBlockHolder(UiUtils.getViewBy(parent, R.layout.gift_recycler));
                case EVENT:
                    return new EventBlockHolder(UiUtils.getViewBy(parent, R.layout.gift_recycler));

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


    }

    public class OfferBlockHolder extends RecyclerView.ViewHolder {
        private final GiftRecyclerBinding mBinding;
        private final PackageAdapter<PackageModel> packageAdapter = new PackageAdapter<>();

        public OfferBlockHolder(@NonNull View itemView) {
            super(itemView);
            mBinding = GiftRecyclerBinding.bind(itemView);
            mBinding.packageRecycler.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
            mBinding.packageRecycler.setAdapter(packageAdapter);
        }

        public void setupData(MyWalletModel model) {

            if (model.getOffer() != null)
            mBinding.btnTimeInfo.setVisibility(View.VISIBLE);
            if (model.getOffer().getVenue() != null) {
                mBinding.venueContainer.setVenueDetail(model.getOffer().getVenue());
            }else {
                mBinding.venueContainer.setVisibility(View.GONE);
            }
            mBinding.tvDate.setText(Utils.convertDateFormat(model.getOffer().getEndTime(), AppConstants.DATEFORMAT_SHORT, AppConstants.DATEFORMAT_DD_MM_YYYY));

            mBinding.btnRedeem.setVisibility(model.getOffer().getPackages().isEmpty() ? View.GONE : View.VISIBLE);

            mBinding.tvTypes.setText(model.getType());
            mBinding.txtTitle.setText(model.getOffer().getTitle());
            mBinding.tvDescription.setText(model.getOffer().getDescription());
            mBinding.tvDescription.post( () -> {
                int lineCount = mBinding.tvDescription.getLineCount();
                if (lineCount > 2) { Utils.makeTextViewResizable( mBinding.tvDescription, 3, 3, ".. See More", true );}
            } );
            Graphics.loadImage(model.getOffer().getImage(), mBinding.imgCover);
            mBinding.txtDays.setText(model.getOffer().getDays());


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

            if (TextUtils.isEmpty(model.getOffer().getStartTime())) {
                mBinding.startDate.setText("Ongoing");
                mBinding.tillDateLayout.setVisibility(View.GONE);

            } else {
                mBinding.tillDateLayout.setVisibility(View.VISIBLE);
                mBinding.startDate.setText("From : "+Utils.convertMainDateFormat(model.getOffer().getStartTime()));
                mBinding.endDate.setText("Till : "+Utils.convertMainDateFormat(model.getOffer().getEndTime()));
            }
            mBinding.txtOfferTime.setText(model.getOffer().getOfferTiming());


            if (model.getItems() != null && !model.getItems().isEmpty()) {

                List<PackageModel> packageModelList = new ArrayList<>();
                model.getItems().forEach(p -> {
                    if (p.getPackageId() != null && model.getOffer() != null) {
                        if (model.getOffer().getPackages() != null) {
                            PackageModel tmpPackage = model.getOffer().getPackages().stream().filter(a -> a.getId().equals(p.getPackageId())).findFirst().orElse(null);
                            if (tmpPackage != null) {
                                tmpPackage.setPrice(p.getPrice());
                                tmpPackage.setRemainingQty(p.getRemainingQty());
                                packageModelList.add(tmpPackage);
                            }
                        }
                    }
                });

                List<String> list = model.getItems()
                        .stream()
                        .filter(model1 -> model1.getGiftMessage() != null)
                        .flatMap(model1 -> model1.getGiftMessage().stream())
                        .filter(s -> s != null && !s.isEmpty())
                        .collect(Collectors.toList());

                if (!list.isEmpty()) {
                    String finalText = String.join("\n", list);

                    if (!finalText.isEmpty()) {
                        mBinding.messageLayout.setVisibility(View.VISIBLE);
                        mBinding.tvGiftMessage.setText(finalText);
                    } else {
                        mBinding.messageLayout.setVisibility(View.GONE);
                    }
                }
                else {
                    mBinding.messageLayout.setVisibility(View.GONE);
                }

                if (!packageModelList.isEmpty()) {
                    packageAdapter.updateData(packageModelList);
                }
            } else {
                mBinding.packageRecycler.setVisibility(View.GONE);
            }


            if (model.getGiftBy() != null) {
                Graphics.loadImageWithFirstLetter(model.getGiftBy().getImage(), mBinding.ivUserProfile, model.getGiftBy().getFirstName());
                mBinding.tvUserName.setText(model.getGiftBy().getFullName());
            } else {
                mBinding.layoutGifedBy.setVisibility(View.GONE);
            }

            if (Utils.checkDateGoOrNot(model.getOffer().getEndTime())){
                mBinding.tvRedeem.setText("Expired!");
                mBinding.tvRedeem.setBackgroundResource(android.R.color.transparent);
                mBinding.tvRedeem.setEnabled(false);
                mBinding.tvRedeem.setTypeface(null, Typeface.BOLD);
                mBinding.tvRedeem.setTextColor(getResources().getColor(R.color.brand_pink));
            }else{
                mBinding.btnRedeem.setVisibility(View.VISIBLE);

            }
            setListeners(model);
        }

        private void setListeners(MyWalletModel model) {

            mBinding.getRoot().setOnClickListener(v -> {
                // Utils.requestvenueOfferDetail(requireActivity(), model.getOfferId());
                OfferDetailBottomSheet dialog = new OfferDetailBottomSheet();
                dialog.offerId = model.getOfferId();
                dialog.show(getChildFragmentManager(), "");
            });

            mBinding.layoutGifedBy.setOnClickListener(view -> {
                startActivity(new Intent(requireActivity(), OtherUserProfileActivity.class).putExtra("friendId", model.getGiftBy().getId()));
            });

            mBinding.btnRedeem.setOnClickListener(view -> {
                if (model.getOffer().getPackages().size() > 1) {
                    PackageBottomSheet packageBottomSheet = new PackageBottomSheet();
                    packageBottomSheet.myWalletModel = model;
                    packageBottomSheet.type = "redeem";
                    packageBottomSheet.callback = data -> {
                        if (data != null) {
                            giftVoucherList();
                        }
                    };
                    packageBottomSheet.show(getChildFragmentManager(), "1");
                } else {
                    Intent intent = new Intent(getActivity(), RedeemActivity.class);
                    intent.putExtra("itemList",new Gson().toJson(model));
                    intent.putExtra("packageModel", new Gson().toJson( model.getOffer().getPackages().get( 0 )));
                    activityLauncher.launch( intent, result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            boolean isClose = result.getData().getBooleanExtra("close",false);
                            if (isClose) {
                                giftVoucherList();
                            }
                        }
                    } );
                }
            });

        }


    }

    public class EventBlockHolder extends RecyclerView.ViewHolder {
        private final GiftRecyclerBinding mBinding;
        private final PackageAdapter<PackageModel> packageAdapter = new PackageAdapter<>();

        public EventBlockHolder(@NonNull View itemView) {
            super(itemView);
            mBinding = GiftRecyclerBinding.bind(itemView);
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


            mBinding.startDate.setText(String.format("Reservation Date  : %s", Utils.convertMainDateFormat(model.getEvent().getReservationTime())));
            mBinding.endDate.setText(String.format("Event Date : %s", Utils.convertMainDateFormat(model.getEvent().getEventTime())));
            mBinding.txtOfferTime.setText(String.format("%s - %s", Utils.convertMainTimeFormat(model.getEvent().getReservationTime()), Utils.convertMainTimeFormat(model.getEvent().getEventTime())));
            mBinding.tvDate.setText(Utils.convertDateFormat(model.getEvent().getEventTime(), AppConstants.DATEFORMAT_SHORT, AppConstants.DATEFORMAT_DD_MM_YYYY));
            try {
                Date day = Utils.stringToDate(model.getEvent().getEventTime(), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                String date = Utils.formatDate(day, "E");
                mBinding.txtDays.setText(date);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }


            if (model.getItems() != null && !model.getItems().isEmpty()) {
                List<PackageModel> packageModelList = model.getItems().stream()
                        .flatMap(model1 -> model.getEvent().getPackages().stream()
                                .filter(model2 -> model2.getId().equals(model1.getPackageId()))
                                 .peek(model2 -> {
                                    model2.setRemainingQty(model1.getRemainingQty());
                                    model2.setPrice(model1.getPrice());
                                 })
                        )
                        .collect(Collectors.toList());

                List<String> list = model.getItems()
                        .stream()
                        .filter(model1 -> model1.getGiftMessage() != null)
                        .flatMap(model1 -> model1.getGiftMessage().stream())
                        .filter(s -> s != null && !s.isEmpty())
                        .collect(Collectors.toList());

                if (!list.isEmpty()) {
                    String finalText = String.join("\n", list);

                    if (!finalText.isEmpty()) {
                        mBinding.messageLayout.setVisibility(View.VISIBLE);
                        mBinding.tvGiftMessage.setText(finalText);
                    } else {
                        mBinding.messageLayout.setVisibility(View.GONE);
                    }
                }
                else {
                    mBinding.messageLayout.setVisibility(View.GONE);
                }

                if (!packageModelList.isEmpty()) {
                    packageAdapter.updateData(packageModelList);
                }
            } else {
                mBinding.packageRecycler.setVisibility(View.GONE);
            }

            mBinding.btnRedeem.setVisibility(model.getEvent().getPackages().isEmpty() ? View.GONE : View.VISIBLE);

            if (model.getGiftBy() != null) {
                Graphics.loadImageWithFirstLetter(model.getGiftBy().getImage(), mBinding.ivUserProfile, model.getGiftBy().getFirstName());
                mBinding.tvUserName.setText(model.getGiftBy().getFullName());
            } else {
                mBinding.layoutGifedBy.setVisibility(View.GONE);
            }

            if (Utils.checkDateGoOrNot(model.getEvent().getEventTime())){
                mBinding.tvRedeem.setText("Expired!");
                mBinding.tvRedeem.setBackgroundResource(android.R.color.transparent);
                mBinding.tvRedeem.setEnabled(false);
                mBinding.tvRedeem.setTypeface(null, Typeface.BOLD);
                mBinding.tvRedeem.setTextColor(getResources().getColor(R.color.brand_pink));
            }else{
                mBinding.btnRedeem.setVisibility(View.VISIBLE);

            }

            setListeners(model);

        }

        private void setListeners(MyWalletModel model) {

            mBinding.getRoot().setOnClickListener(view -> {
                Intent intent = new Intent(requireActivity(), EventDetailsActivity.class);
                intent.putExtra("eventId", model.getEventId());
                if (model.getEvent().getEventsOrganizer()!= null) {
                    intent.putExtra("name",  model.getEvent().getEventsOrganizer().getName());
                    intent.putExtra("address",model.getEvent().getEventsOrganizer().getWebsite());
                    intent.putExtra("image", model.getEvent().getEventsOrganizer().getLogo());
                }
                startActivity(intent);
            });

            mBinding.layoutGifedBy.setOnClickListener(view -> {
                startActivity(new Intent(requireActivity(), OtherUserProfileActivity.class).putExtra("friendId", model.getGiftBy().getId()));

            });

            mBinding.btnRedeem.setOnClickListener(view -> {
                if (model.getEvent().getPackages().size() > 1) {
                    PackageBottomSheet packageBottomSheet = new PackageBottomSheet();
                    packageBottomSheet.myWalletModel = model;
                    packageBottomSheet.type = "redeem";
                    packageBottomSheet.callback = data -> {
                        if (data != null) {
                            giftVoucherList();
                        }
                    };
                    packageBottomSheet.show(getChildFragmentManager(), "1");
                } else {
                    Intent intent = new Intent(getActivity(), RedeemActivity.class);
                    intent.putExtra("itemList",new Gson().toJson(model));
                    intent.putExtra("packageModel", new Gson().toJson( model.getEvent().getPackages().get( 0 )));
                    activityLauncher.launch( intent, result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            boolean isClose = result.getData().getBooleanExtra("close",false);
                            if (isClose) {
                                giftVoucherList();
                            }
                        }
                    } );
                }
            });

        }
    }

    public class ActivityBlockHolder extends RecyclerView.ViewHolder {
        private final GiftActivityRecyclerBinding mBinding;

        public ActivityBlockHolder(@NonNull View itemView) {
            super(itemView);
            mBinding = GiftActivityRecyclerBinding.bind(itemView);
        }

        @SuppressLint("DefaultLocale")
        public void setupData(MyWalletModel model) {

            mBinding.tvDiscountPrice.setVisibility(View.GONE);

            if (model.getActivity().getProvider() != null) {
                Graphics.loadRoundImage(model.getActivity().getProvider().getLogo(), mBinding.iconImg);
                mBinding.tvTitle.setText(model.getActivity().getProvider().getName());
                mBinding.tvAddress.setText(model.getActivity().getProvider().getAddress());
            }

            mBinding.txtDescription.setText(model.getActivity().getDescription());
            mBinding.txtName.setText(model.getActivity().getName());
            Graphics.loadImage(model.getActivity().getGalleries().get(0), mBinding.imgGallary);


            if (model.getItems() != null && !model.getItems().isEmpty()) {
                ItemModel itemObject = model.getItems().get(0);
                if (itemObject == null) { return; }

                mBinding.txtTime.setText(Utils.convertTimeFormat(itemObject.getTime()));
                mBinding.tvQty.setText(String.valueOf(model.getItems().stream().mapToInt(ItemModel::getQty).sum()));

                String detectedFormat = Utils.detectDateFormat(itemObject.getDate());
                if (itemObject.getGiftMessage() != null && !itemObject.getGiftMessage().isEmpty() && !itemObject.getGiftMessage().equals(Arrays.asList(""))) {
                    mBinding.tvGiftMessage.setText(String.format("Message : %s", TextUtils.join("\n", itemObject.getGiftMessage())));
                    mBinding.messageLayout.setVisibility(View.VISIBLE);
                }else {
                    mBinding.messageLayout.setVisibility(View.GONE);
                }

                if (detectedFormat != null) {
                    String formattedDate = Utils.convertDateFormat(itemObject.getDate(), detectedFormat, "E, dd MMM yyyy");
                    mBinding.txtDate.setText(formattedDate);
                } else {
                    mBinding.txtDate.setText("");
                }
            }

            if (model.getGiftBy() != null) {
                Graphics.loadRoundImage(model.getGiftBy().getImage(), mBinding.ivUserProfile);
                mBinding.tvUserName.setText(model.getGiftBy().getFullName());
            } else {
                mBinding.layoutGifedBy.setVisibility(View.GONE);
            }
            setListeners(model);
        }

        private void setListeners(MyWalletModel model) {
            mBinding.getRoot().setOnClickListener(v -> {
                Intent intent = new Intent(requireActivity(), ActivityListDetail.class);
                intent.putExtra("activityId", model.getActivity().getId()).putExtra("name", model.getActivity().getName());
                if (model.getActivity().getProvider() != null) {
                    intent.putExtra("image", model.getActivity().getProvider().getLogo());
                    intent.putExtra("title", model.getActivity().getProvider().getName());
                    intent.putExtra("address", model.getActivity().getProvider().getAddress());
                }
                startActivity(intent);
            });

            mBinding.layoutGifedBy.setOnClickListener(view -> {
                startActivity(new Intent(requireActivity(), OtherUserProfileActivity.class).putExtra("friendId", model.getGiftBy().getId()));

            });

            mBinding.btnRedeem.setOnClickListener(view -> {
                startActivity(new Intent(context, PurchaseVoucherDetailActivity.class).putExtra("itemList", new Gson().toJson(model)));
            });
        }

    }

    public class DealBlockHolder extends RecyclerView.ViewHolder {
        private final GiftRecyclerBinding mBinding;
        private final PackageAdapter<PackageModel> packageAdapter = new PackageAdapter<>();

        public DealBlockHolder(@NonNull View itemView) {
            super(itemView);
            mBinding = GiftRecyclerBinding.bind(itemView);
            mBinding.packageRecycler.setLayoutManager( new LinearLayoutManager( context, LinearLayoutManager.VERTICAL, false ) );
            mBinding.packageRecycler.setAdapter( packageAdapter );
        }

        public void setupData(MyWalletModel model) {
            if (model != null) {
                mBinding.txtTitle.setVisibility(View.GONE);
                mBinding.tvDescription.setVisibility(View.GONE);

                if (model.getDeal().getVenue() != null) {
                    mBinding.venueContainer.setVenueDetail(model.getDeal().getVenue());
                }
                Graphics.loadImage(model.getDeal().getImage(), mBinding.imgCover);
                mBinding.txtDays.setText(model.getDeal().getDays());
                mBinding.tvTypes.setText(model.getType());

                mBinding.startDate.setText(String.format("From : %s", Utils.convertDateFormat(model.getDeal().getStartDate(), AppConstants.DATEFORMAT_SHORT, AppConstants.DATEFORMT_MM_DATE)));
                mBinding.endDate.setText(String.format("Till : %s", Utils.convertDateFormat(model.getDeal().getEndDate(), AppConstants.DATEFORMAT_SHORT, AppConstants.DATEFORMT_MM_DATE)));
                mBinding.txtOfferTime.setText(String.format("%s - %s", Utils.convertTimeFormat(model.getDeal().getStartTime()), Utils.convertTimeFormat(model.getDeal().getEndTime())));
                mBinding.tvDate.setText(Utils.convertDateFormat(model.getDeal().getEndDate(), AppConstants.DATEFORMAT_SHORT, AppConstants.DATEFORMAT_DD_MM_YYYY));

                if (model.getItems() != null && !model.getItems().isEmpty()) {
                    List<String> list = model.getItems()
                            .stream()
                            .filter(model1 -> model1.getGiftMessage() != null)
                            .flatMap(model1 -> model1.getGiftMessage().stream())
                            .filter(s -> s != null && !s.isEmpty())
                            .collect(Collectors.toList());

                    if (!list.isEmpty()) {
                        String finalText = String.join("\n", list);

                        if (!finalText.isEmpty()) {
                            mBinding.messageLayout.setVisibility(View.VISIBLE);
                            mBinding.tvGiftMessage.setText(finalText);
                        } else {
                            mBinding.messageLayout.setVisibility(View.GONE);
                        }
                    }
                    else {
                        mBinding.messageLayout.setVisibility(View.GONE);
                    }
                }


                if (model.getGiftBy() != null) {
                    Graphics.loadImageWithFirstLetter(model.getGiftBy().getImage(), mBinding.ivUserProfile, model.getGiftBy().getFirstName());
                    mBinding.tvUserName.setText(model.getGiftBy().getFullName());
                } else {
                    mBinding.layoutGifedBy.setVisibility(View.GONE);
                }

                setListeners(model);

                setPackageAdapter(model);
            }
        }

        private void setListeners(MyWalletModel model) {
            mBinding.getRoot().setOnClickListener(v ->
                    startActivity(new Intent(requireActivity(), VoucherDetailScreenActivity.class).putExtra("id", model.getDeal().getId())));
            mBinding.layoutGifedBy.setOnClickListener(view -> startActivity(new Intent(requireActivity(), OtherUserProfileActivity.class).putExtra("friendId", model.getGiftBy().getId())));
            mBinding.btnRedeem.setOnClickListener(view -> startActivity(new Intent(context, PurchaseVoucherDetailActivity.class).putExtra("itemList", new Gson().toJson(model))));
        }

        private void setPackageAdapter(MyWalletModel model) {
            List<PackageModel> list = new ArrayList<>();
            PackageModel packageModel = new PackageModel();
            if (model.getDeal() != null) {
                packageModel.setTitle(model.getDeal().getTitle());
                packageModel.setAmount(String.valueOf(model.getDeal().getDiscountedPrice()));
                packageModel.setDiscount(String.valueOf(model.getDeal().getDiscountValue()));
                packageModel.setDescription(model.getDeal().getDescription());
            }
            if (!model.getItems().isEmpty()) {
                packageModel.setRemainingQty(model.getItems().get(0).getRemainingQty());
                packageModel.setPrice(model.getItems().get(0).getPrice());

            }
            list.add(packageModel);
            if (list != null && !list.isEmpty()) {
                packageAdapter.updateData(list);
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
            if (model != null) {
                viewHolder.mBinding.tvName.setText(Utils.notNullString(model.getTitle()));
                viewHolder.mBinding.tvDiscountPrice.setVisibility(View.GONE);

                if (!TextUtils.isEmpty(model.getDescription().trim())) {
                    viewHolder.mBinding.tvDescription.setText(Utils.notNullString(model.getDescription()));
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
                viewHolder.mBinding.tvQty.setText(String.valueOf(model.getRemainingQty()));
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final ItemWalletPackageBinding mBinding;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemWalletPackageBinding.bind(itemView);
            }
        }
    }


    // endregion
    // --------------------------------------
}