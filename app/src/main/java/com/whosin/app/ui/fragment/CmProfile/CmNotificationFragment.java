package com.whosin.app.ui.fragment.CmProfile;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.whosin.app.comman.AppDelegate.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;
import com.tapadoo.alerter.Alerter;
import com.whosin.app.R;
import com.whosin.app.comman.AppConstants;
import com.whosin.app.comman.AppExecutors;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.databinding.FragmentCmNotificationBinding;
import com.whosin.app.databinding.ItemCmUserListBinding;
import com.whosin.app.databinding.ItemComplementryProfileBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.ComplementaryProfileManager;
import com.whosin.app.service.models.CommonModel;
import com.whosin.app.service.models.ComplimentaryProfileModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.EventInOutPenaltyModel;
import com.whosin.app.service.models.MainNotificationModel;
import com.whosin.app.service.models.NotificationModel;
import com.whosin.app.service.models.PaymentCredentialModel;
import com.whosin.app.service.models.PromoterAddRingModel;
import com.whosin.app.service.models.PromoterEventModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.Promoter.ComplementaryEventDetailActivity;
import com.whosin.app.ui.activites.PromoterPublic.PromoterPublicProfileActivity;
import com.whosin.app.ui.activites.raynaTicket.BottomSheets.SelectPaymentOptionBottomSheet;
import com.whosin.app.ui.activites.raynaTicket.BottomSheets.TabbyPaymentBottomSheet;
import com.whosin.app.ui.fragment.CmProfile.CmBottomSheets.AddPlusOneGuestBottomSheet;
import com.whosin.app.ui.fragment.CmProfile.CmBottomSheets.EventFAQDialog;
import com.whosin.app.ui.fragment.CmProfile.CmBottomSheets.FAQBottomSheet;
import com.whosin.app.ui.fragment.Promoter.PromoterBootmSheets.CancellationPenaltyDialog;
import com.whosin.app.ui.fragment.comman.BaseFragment;
import com.whosin.app.ui.fragment.wallet.PurchaseSuccessFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

public class CmNotificationFragment extends BaseFragment {

    private FragmentCmNotificationBinding binding;
    private boolean isChangeBG = false;
    private final NotificationAdapter<NotificationModel> notificationAdapter = new NotificationAdapter<>();

    private PaymentSheet paymentSheet;

    private NotificationModel notificationModel;

    private ItemComplementryProfileBinding mBinding;

    public CmNotificationFragment(boolean isChangeBG) {
        this.isChangeBG = isChangeBG;
    }

    public CmNotificationFragment() {
    }



    // --------------------------------------
    // region LifeCycle
    // --------------------------------------
    @Override
    public void initUi(View view) {

        binding = FragmentCmNotificationBinding.bind(view);

        binding.emptyPlaceHolderView.setEmptyPlaceTxtTitle(getValue("empty_notifications"));

        EventBus.getDefault().register(this);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        binding.recyclerView.setAdapter(notificationAdapter);
        if (isChangeBG) {
            binding.getRoot().setBackgroundColor(getResources().getColor(R.color.transparent, null));
            binding.headerView.setVisibility(GONE);
            binding.recyclerView.setPadding(binding.recyclerView.getPaddingLeft(), 0, binding.recyclerView.getPaddingRight(), binding.recyclerView.getPaddingBottom());
        }

        binding.swipeRefreshLayout.setProgressViewOffset(false, 0, 220);
        binding.headerView.isFromCM = true;
        binding.headerView.activity = requireActivity();

        if (ComplementaryProfileManager.shared.complimentaryProfileModel != null && ComplementaryProfileManager.shared.complimentaryProfileModel.getProfile() != null) {
            binding.headerView.setUpData(requireActivity(), ComplementaryProfileManager.shared.complimentaryProfileModel.getProfile());
        }

        paymentSheet = new PaymentSheet(CmNotificationFragment.this, paymentSheetResult -> {
            if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
                Log.d("TAG", "Canceled");
            } else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
                Log.e("TAG", "Got error: ", ((PaymentSheetResult.Failed) paymentSheetResult).getError());
            } else if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
                requestPromoterUpdateInviteStatus(notificationModel, "in", mBinding);
            }
        });


        requestCmUserNotification(true);
    }

    @Override
    public void setListeners() {
        binding.swipeRefreshLayout.setOnRefreshListener(() -> requestCmUserNotification(false));
    }

    @Override
    public void populateData(boolean getDataFromServer) {

    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_cm_notification;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NotificationModel event) {
        requestCmUserNotification(false);
        if (ComplementaryProfileManager.shared.complimentaryProfileModel != null && ComplementaryProfileManager.shared.complimentaryProfileModel.getProfile() != null) {
            binding.headerView.setUpData(requireActivity(), ComplementaryProfileManager.shared.complimentaryProfileModel.getProfile());
        }
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void showPenaltyDialog(EventInOutPenaltyModel data,NotificationModel notificationModel, ItemComplementryProfileBinding binding) {
        CancellationPenaltyDialog dialog = new CancellationPenaltyDialog();
        dialog.callback = data1 -> {
            if (data1) {
                this.notificationModel = notificationModel;
                this.mBinding = binding;
                SelectPaymentOptionBottomSheet bottmSheet = new SelectPaymentOptionBottomSheet();
                bottmSheet.amount = (double) data.getAmount();
                bottmSheet.callback = p -> requestStripeToken(data,p);
                bottmSheet.show(getChildFragmentManager(), "");
            }else {
                showFAQDialog(notificationModel.getEvent().getFaq());
            }
        };
        dialog.model = data;
        dialog.show(getChildFragmentManager(),"");

    }

    private void startStripeCheckOut(PaymentCredentialModel model) {
        if (model.publishableKey == null || model.clientSecret == null) {
            Toast.makeText(activity, "Invalid payment configuration", Toast.LENGTH_SHORT).show();
            return;
        }
        PaymentConfiguration.init(activity, model.publishableKey);
        final PaymentSheet.Configuration configuration = new PaymentSheet.Configuration.Builder("Whosin, Inc.").allowsDelayedPaymentMethods(true).build();

        paymentSheet.presentWithPaymentIntent(model.clientSecret, configuration);
    }

    private void startGooglePayCheckOut(PaymentCredentialModel model) {
        if (model.publishableKey == null || model.clientSecret == null) {
            Toast.makeText(activity, "Invalid payment configuration", Toast.LENGTH_SHORT).show();
            return;
        }
        PaymentConfiguration.init(activity, model.publishableKey);
        final PaymentSheet.GooglePayConfiguration googlePayConfiguration = new PaymentSheet.GooglePayConfiguration(AppConstants.GPAY_ENV, AppConstants.GPAY_REGION);
        final PaymentSheet.Configuration configuration = new PaymentSheet.Configuration.Builder("Whosin, Inc.").allowsDelayedPaymentMethods(true).googlePay(googlePayConfiguration).build();
        paymentSheet.presentWithPaymentIntent(model.clientSecret, configuration);
    }


    private void tabbyCheckOut(PaymentCredentialModel model) {
        TabbyPaymentBottomSheet sheet = new TabbyPaymentBottomSheet();
        sheet.paymentTabbyModel = model.getTabbyModel();
        sheet.callback = p -> {
            if (!TextUtils.isEmpty(p)) {
                switch (p) {
                    case "success":
                        requestPromoterUpdateInviteStatus(notificationModel, "in", mBinding);
                        break;
                    case "cancel":

                        break;
                    case "failure":

                        break;
                }
            }
        };
        sheet.show(getChildFragmentManager(), "");
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateRecycleItem(PromoterEventModel model, String status, boolean fromWishlisted) {
        if (notificationAdapter.getData() != null && !notificationAdapter.getData().isEmpty()) {
            notificationAdapter.getData().forEach(s -> {
                if (s.getEvent() != null && s.getType().equals("event-invitation")) {
                    if (s.getEvent().getId().equals(model.getId())) {
                        if (fromWishlisted) {
                            s.getEvent().setWishlisted(!model.isWishlisted());
                        } else {
                            s.getEvent().getInvite().setInviteStatus(status);
                        }
                    }
                }

            });
            notificationAdapter.notifyDataSetChanged();
            EventBus.getDefault().post(new ComplimentaryProfileModel());
            EventBus.getDefault().post(new NotificationModel());

        }
    }


    private void showFAQDialog(String faq){
        FAQBottomSheet dialog = new FAQBottomSheet();
        dialog.faqsting = faq;
        dialog.show(getChildFragmentManager(),"");
    }

    private void showCancelDialog(String message,String faq,NotificationModel notificationModel, String inviteStatus, ItemComplementryProfileBinding binding) {
        EventFAQDialog dialog = new EventFAQDialog();
        dialog.message = message;
        dialog.faqCallBack = data -> {
            showFAQDialog(faq);
        };
        dialog.callback = data -> {
            if (data) {
                requestPromoterUpdateInviteStatus(notificationModel, "out", binding);
            }
        };
        dialog.show(getChildFragmentManager(),"");
    }


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestCmUserNotification(boolean isShowProgress) {
        if (isShowProgress) {
            showProgress();
        } else {
            binding.swipeRefreshLayout.setRefreshing(true);
        }
        DataService.shared(requireActivity()).requestCmUserNotification(new RestCallback<ContainerModel<MainNotificationModel>>(this) {
            @SuppressLint("NewApi")
            @Override
            public void result(ContainerModel<MainNotificationModel> model, String error) {
                hideProgress();
                binding.swipeRefreshLayout.setRefreshing(false);
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(requireActivity(), error, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (model.data != null) {
                    if (model.data.getNotification() != null && !model.data.getNotification().isEmpty()) {
                        binding.recyclerView.setVisibility(View.VISIBLE);
                        binding.emptyPlaceHolderView.setVisibility(GONE);
                        model.data.getNotification().removeIf(p -> "event-invitation".equals(p.getType()) && ("cancelled".equals(p.getEvent().getStatus()) || "completed".equals(p.getEvent().getStatus())));
                        assert model.getData() != null;
                        if (!model.getData().getNotification().isEmpty()){
                            notificationAdapter.updateData(model.data.getNotification());
                        }else {
                            binding.recyclerView.setVisibility(View.GONE);
                            binding.emptyPlaceHolderView.setVisibility(VISIBLE);
                        }
                    } else {
                        binding.recyclerView.setVisibility(View.GONE);
                        binding.emptyPlaceHolderView.setVisibility(VISIBLE);
                    }


                }
            }
        });
    }

    private void requestPromoterRingUpdateMemberStatus(String status, String id, ItemCmUserListBinding vBinding, boolean isApprove) {
        if (isApprove) {
            vBinding.btnApprove.startProgress();
        } else {
            vBinding.btnRejected.startProgress();
        }
        DataService.shared(requireActivity()).requestPromoterRingUpdateMemberStatus(id, status, new RestCallback<ContainerModel<PromoterAddRingModel>>(this) {
            @Override
            public void result(ContainerModel<PromoterAddRingModel> model, String error) {
                vBinding.btnApprove.stopProgress();
                vBinding.btnRejected.stopProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(requireActivity(), error, Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(requireActivity(), model.message, Toast.LENGTH_SHORT).show();
                requestCmUserNotification(true);
            }
        });
    }

    private void requestPromoterUpdateInviteStatus(NotificationModel notificationModel, String inviteStatus, ItemComplementryProfileBinding binding) {
        if (inviteStatus.equals("in")) {
            binding.eventImIn.startProgress();
        } else {
            binding.btnImOut.startProgress();
        }
        DataService.shared(requireActivity()).requestPromoterUpdateInviteStatus(notificationModel.getEvent().getInvite().getId(), inviteStatus, new RestCallback<ContainerModel<EventInOutPenaltyModel>>(this) {
            @Override
            public void result(ContainerModel<EventInOutPenaltyModel> model, String error) {
                if (inviteStatus.equals("in")) {
                    binding.eventImIn.stopProgress();
                } else {
                    binding.btnImOut.stopProgress();
                }
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (model.message.equals("cancellation-penalty")){
                    showPenaltyDialog(model.data,notificationModel, binding);
                }else {
                    if (inviteStatus.equals("in")) {
                        String titleMsg = (notificationModel.getEvent() != null && notificationModel.getEvent().isConfirmationRequired())
                                ? getValue("thank_you_for_showing_interest")
                                : getValue("thank_you_for_joining");

                        String subtitleMsg = (notificationModel.getEvent() != null && notificationModel.getEvent().isConfirmationRequired())
                                ? getValue("admin_will_review_request")
                                : getValue("check_details_and_be_on_time");
                        Alerter.create(requireActivity()).setTitle(titleMsg).setText(subtitleMsg).setTextAppearance(R.style.AlerterText).setTitleAppearance(R.style.AlerterTitle).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                    } else {
                        Alerter.create(requireActivity()).setTitle(getValue("invitation_cancel_successfully")).setTitleAppearance(R.style.AlerterTitle).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                    }
                    EventBus.getDefault().post(new ComplimentaryProfileModel());
                    EventBus.getDefault().post(new NotificationModel());
                    requestCmUserNotification(false);
                }


            }
        });
    }

    private void requestPromoterToggleWishList(PromoterEventModel promoterEventModel, ItemComplementryProfileBinding binding) {
        binding.btnWishList.startProgress();
        DataService.shared(requireActivity()).requestPromoterToggleWishList(promoterEventModel.getId(), "event", new RestCallback<ContainerModel<CommonModel>>(this) {
            @Override
            public void result(ContainerModel<CommonModel> model, String error) {
                binding.btnWishList.stopProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (promoterEventModel.isWishlisted()) {
                    Alerter.create(activity).setText(getValue("remove_from_the_wishlist")).setTextAppearance(R.style.AlerterText).setTitleAppearance(R.style.AlerterTitle).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                } else {
                    Alerter.create(activity).setText(getValue("item_add_wishlist")).setTextAppearance(R.style.AlerterText).setTitleAppearance(R.style.AlerterTitle).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                }
                updateRecycleItem(promoterEventModel, "", true);
            }
        });
    }

    private void requestStripeToken(EventInOutPenaltyModel model,int paymentMod) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("amount",model.getAmount());
        jsonObject.addProperty("currency",model.getCurrency());
        jsonObject.addProperty("type",model.getType());

        if (AppConstants.CARD_PAYMENT == paymentMod) {
            jsonObject.addProperty("paymentMethod", "stripe");
        } else if (AppConstants.PAY_WITH_LINK == paymentMod) {
            jsonObject.addProperty("paymentMethod", "stripe");
        }  else if (AppConstants.GOOGLE_PAY == paymentMod || AppConstants.SAMSUNG_PAY == paymentMod) {
            jsonObject.addProperty("paymentMethod", "stripe");
        } else if (AppConstants.TABBY_PAYMENT == paymentMod) {
            jsonObject.addProperty("paymentMethod", "tabby");
        }

        showProgress();
        DataService.shared(requireActivity()).requestPromoterStripePayment(jsonObject, new RestCallback<ContainerModel<PaymentCredentialModel>>(this) {
            @Override
            public void result(ContainerModel<PaymentCredentialModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(requireActivity(), error, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (model.getData() != null){
                    if (AppConstants.CARD_PAYMENT == paymentMod) {
                        startStripeCheckOut(model.getData());
                    } else if (AppConstants.PAY_WITH_LINK == paymentMod) {
                        startStripeCheckOut(model.getData());
                    }  else if (AppConstants.GOOGLE_PAY == paymentMod || AppConstants.SAMSUNG_PAY == paymentMod) {
                        startGooglePayCheckOut(model.getData());
                    } else if (AppConstants.TABBY_PAYMENT == paymentMod) {
                        if (Utils.isAvailableTabby(model.getData().getTabbyModel())) {
                            tabbyCheckOut(model.getData());
                        } else {
                            Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), getValue("tabby_payment_failed"));
                        }
                    }
                }
            }
        });
    }


    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    public class NotificationAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());

            switch (viewType) {
                case 1:
                    return new AddToRingHolder(inflater.inflate(R.layout.item_cm_user_list, parent, false));
                case 2:
                    return new EventInviteHolder(inflater.inflate(R.layout.item_complementry_profile, parent, false));
                default:
                    return new AddToRingHolder(inflater.inflate(R.layout.item_notification_user_list, parent, false));
            }

        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            boolean isLastItem = position == getItemCount() - 1;
            NotificationModel model = (NotificationModel) getItem(position);

            if (getItemViewType(position) == 1) {
                ((AddToRingHolder) holder).setupData(model);
            } else if (getItemViewType(position) == 2) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    ((EventInviteHolder) holder).setupData(model);
                }
            }


            if (isLastItem) {
                int marginBottom = Utils.getMarginBottom(holder.itemView.getContext(), 0.18f);
                Utils.setBottomMargin(holder.itemView, marginBottom);
            } else {
                Utils.setBottomMargin(holder.itemView, 0);
            }

        }


        public int getItemViewType(int position) {
            NotificationModel model = (NotificationModel) getItem(position);
            switch (model.getType()) {
                case "add-to-ring":
                    return 1;
                case "event-invitation":
                    return 2;
                default:
                    return 0;
            }
        }


        public class AddToRingHolder extends RecyclerView.ViewHolder {

            private final ItemCmUserListBinding mBinding;

            public AddToRingHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemCmUserListBinding.bind(itemView);

                mBinding.viewProfile.setText(getValue("view_profile"));
                mBinding.btnApprove.setTxtTitle(getValue("approve"));
                mBinding.btnRejected.setTxtTitle(getValue("reject"));

            }

            private void setupData(NotificationModel model) {
                if (model == null) {
                    return;
                }

                if (model.getRequestStatus() != null && !model.getRequestStatus().isEmpty()) {
                    mBinding.buttonsLayout.setVisibility(model.getRequestStatus().equals("pending") ? View.VISIBLE : View.GONE);
                }

                String firstLetter = "";
                if (model.getTitle() != null && !model.getTitle().isEmpty()) {
                    String[] words = model.getTitle().split("\\s+");
                    if (words.length > 0 && !words[0].isEmpty()) {
                        firstLetter = String.valueOf(words[0].charAt(0));
                    }
                }
                mBinding.userName.setText(model.getTitle());
                Graphics.loadImageWithFirstLetter(model.getImage(), mBinding.imgProfile, firstLetter);

                mBinding.descriptionTv.setText(model.getDescription());

                mBinding.btnApprove.setOnClickListener(v -> requestPromoterRingUpdateMemberStatus("accepted", model.getTypeId(), mBinding, true));

                mBinding.btnRejected.setOnClickListener(v -> requestPromoterRingUpdateMemberStatus("rejected", model.getTypeId(), mBinding, false));

                mBinding.getRoot().setOnClickListener(view -> activity.startActivity(new Intent(activity,
                        PromoterPublicProfileActivity.class).
                        putExtra("isPromoterProfilePublic", true).putExtra("id", model.getTypeId())));


            }
        }

        public class EventInviteHolder extends RecyclerView.ViewHolder {

            private final ItemComplementryProfileBinding mBinding;

            public EventInviteHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemComplementryProfileBinding.bind(itemView);

            }

            @SuppressLint("SetTextI18n")
            private void hideAndShowButtons(PromoterEventModel model) {
                AppExecutors.get().mainThread().execute(() -> {

                    mBinding.txtEventStatus.setVisibility(GONE);
                    String promoterStatus = model.getInvite().getPromoterStatus();
                    String status = model.getStatus();

                    if (status.equals("in-progress") || status.equals("completed") || status.equals("cancelled")) {
                        handleSpecialEventStatus(promoterStatus, model);
                        return;
                    }

                    mBinding.buttonLinear.setVisibility(VISIBLE);
                    mBinding.outFavButtonsLayout.setVisibility(VISIBLE);

                    updateButtonVisibility(model);
                });
            }

            @SuppressLint("SetTextI18n")
            private void handleSpecialEventStatus(String promoterStatus, PromoterEventModel model) {
                mBinding.txtEventStatus.setVisibility(VISIBLE);
                mBinding.eventImIn.setVisibility(GONE);
                mBinding.btnImOut.setVisibility(GONE);
                mBinding.outFavButtonsLayout.setVisibility(GONE);
                mBinding.interestConstraint.setVisibility(GONE);

                if (model.getStatus().equals("in-progress")) {
                    if (!model.isSpotClosed() && model.getSpotCloseType().equals("manual") && !model.getInvite().getPromoterStatus().equals("accepted") && Utils.isSpotOpen(model.getSpotCloseAt())) {
                        mBinding.txtEventStatus.setVisibility(GONE);
                        mBinding.buttonLinear.setVisibility(VISIBLE);
                        mBinding.outFavButtonsLayout.setVisibility(VISIBLE);
                        updateButtonVisibility(model);
                    }else {
                        mBinding.txtEventStatus.setText(getValue("event_started"));
                        mBinding.txtEventStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.green_medium));
                    }

                } else if (model.getStatus().equals("cancelled")) {
                    mBinding.txtEventStatus.setText(getValue("cancelled"));
                    mBinding.txtEventStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.delete_red));
                } else if (model.getStatus().equals("completed")) {
                    mBinding.txtEventStatus.setText(getValue("completed"));
                    mBinding.txtEventStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.green_medium));
                }


            }

            private void updateButtonVisibility(PromoterEventModel model) {
                boolean isPending = model.getInvite().getInviteStatus().equals("pending");
                boolean isOut = model.getInvite().getInviteStatus().equals("out");
                boolean isIn = model.getInvite().getInviteStatus().equals("in");
                int count = model.getMaxInvitee() - model.getTotalInMembers();
                boolean isEventFull = false;
                if (!model.getInvite().getPromoterStatus().equals("accepted")) {
                    isEventFull = ("rejected".equals(model.getInvite().getPromoterStatus()) || count <= 0 || model.getStatus().equals("cancelled") || model.isEventFull());
                    if (model.isSpotClosed() && !model.getInvite().getInviteStatus().equals("in")) {
                        isEventFull = true;
                    }
                }


                mBinding.eventImIn.setVisibility(isPending || isOut || isEventFull ? VISIBLE : GONE);
                mBinding.btnImOut.setVisibility(isEventFull ? GONE : ((isIn) ? VISIBLE : GONE));
                mBinding.btnWishList.setVisibility(isEventFull ? GONE : VISIBLE);
                mBinding.btnWishList.setTxtTitle(model.isWishlisted() ? getValue("remove_from_list") : getValue("add_to_my_list"));

                String tmpStatus = model.getInvite().getPromoterStatus().equals("accepted") ? getValue("confirmed") : getValue("pending");
                mBinding.btnImOut.setTxtTitle(model.isConfirmationRequired() ? tmpStatus :getValue("confirmed"));
                if (mBinding.btnImOut.getTxtTitle().equals(getValue("confirmed"))) {
                    mBinding.btnImOut.setBgColor(itemView.getContext().getColor(R.color.im_in));
                } else {
                    mBinding.btnImOut.setBgColor(itemView.getContext().getColor(R.color.amber_color));
                }

                String eventFullText = "";
                if (!"in".equals(model.getInvite().getPromoterStatus())) {
                    eventFullText = getValue("event_full");
                }
                if (model.isSpotClosed() && !model.getInvite().getInviteStatus().equals("in")) {
                    eventFullText = getValue("sorry_event_is_full");
                }
                mBinding.eventImIn.setTxtTitle(isEventFull ? eventFullText : (model.isConfirmationRequired() ? getValue("interested") : getValue("im_in")));

                int imInColor = model.isConfirmationRequired() ? R.color.intrested_color : R.color.im_in;
                int color = isEventFull ? R.color.event_full_color : imInColor;
                mBinding.eventImIn.setBgColor(itemView.getContext().getColor(color));

                if (isEventFull) {
                    mBinding.interestConstraint.setVisibility(GONE);
                }


            }

            private void updateVenueDetails(String address, String name) {
                mBinding.subTitleText.setText(address);
                mBinding.titleText.setText(name);
            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @SuppressLint("DefaultLocale")
            private void setupData(NotificationModel model) {
                if (model == null) {
                    return;
                }
                if (model.getEvent() == null) {
                    return;
                }

                if (model.getEvent().isConfirmationRequired() && model.getEvent().getInvite().getInviteStatus().equals("in")) {
                    mBinding.interestConstraint.setVisibility(View.VISIBLE);
                    if (model.getEvent().getInvite().getPromoterStatus().equals("accepted")) {
                        mBinding.interestedTv.setText(getValue("confirmed"));
                        mBinding.interestConstraint.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.im_in));
                    } else {
                        mBinding.interestedTv.setText(getValue("pending"));
                        mBinding.interestConstraint.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.amber_color));
                    }
                } else if (model.getEvent().getInvite().getInviteStatus().equals("in") && model.getEvent().getInvite().getPromoterStatus().equals("accepted")) {
                    mBinding.interestConstraint.setVisibility(View.VISIBLE);
                    mBinding.interestedTv.setText(getValue("im_in_space"));
                    mBinding.interestConstraint.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.im_in));
                } else {
                    if (Utils.isNewEvent(model.getEvent().getCreatedAt(), model.getEvent().getCloneId())) {
                        mBinding.interestConstraint.setVisibility(VISIBLE);
                        mBinding.interestedTv.setText(getValue("new_space"));
                        mBinding.interestConstraint.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.red));
                    } else {
                        mBinding.interestConstraint.setVisibility(View.GONE);
                    }

                }

                hideAndShowButtons(model.getEvent());


                if ("custom".equals(model.getEvent().getVenueType()) && model.getEvent().getCustomVenue() != null) {
                    updateVenueDetails(model.getEvent().getCustomVenue().getAddress(), model.getEvent().getCustomVenue().getName());
                    Graphics.loadRoundImage(model.getEvent().getCustomVenue().getImage(), mBinding.image);
                    Graphics.loadImage(model.getEvent().getCustomVenue().getImage(), mBinding.imgOffer);
                } else if (model.getEvent().getVenue() != null) {
                    updateVenueDetails(model.getEvent().getVenue().getAddress(), model.getEvent().getVenue().getName());
                    Graphics.loadRoundImage(model.getEvent().getVenue().getLogo(), mBinding.image);
                    Graphics.loadImage(model.getEvent().getVenue().getCover(), mBinding.imgOffer);

                }


                if (model.getEvent().getUser() != null) {
                    Graphics.loadImageWithFirstLetter(model.getEvent().getUser().getImage(), mBinding.imageProfile, model.getEvent().getUser().getFullName());
                    mBinding.tvUserName.setText(model.getEvent().getUser().getFullName());
                }

                mBinding.btnWishList.setOnClickListener(view -> requestPromoterToggleWishList(model.getEvent(), mBinding));

                mBinding.btnImOut.setOnClickListener(view -> {
                    Utils.preventDoubleClick(view);
                    ArrayList<String> data = new ArrayList<>();
                    data.add(getValue("cancel_confirmation"));

                    Graphics.showActionSheetRedTitle(requireActivity(), activity.getString(R.string.app_name), data, (data1, position1) -> {
                        if (data1.equals(getValue("cancel_confirmation"))) {
                            if (model.getEvent().getInvite().getInviteStatus().equals("pending")) {
                                requestPromoterUpdateInviteStatus(model, "out", mBinding);
                                return;
                            }

                            String tmpString = "";
                            try {
                                tmpString = String.valueOf(Utils.stringToDateWithUTCForEvent(model.getEvent().getInvite().getUpdatedAt(), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            if (!TextUtils.isEmpty(tmpString) && Utils.checkUpdateTimeAndShowAlert(tmpString)) {
                                Graphics.showAlertDialogWithOkButton(requireActivity(), getValue("please_wait"), getValue("wait_before_changing_response"));
                            } else {
                                if (!model.getEvent().isConfirmationRequired() && Utils.isDateToday(model.getEvent().getDate())) {
                                    if (Utils.isWithinDubaiTimeTwoHours(model.getEvent().getStartTime())) {
                                        String title = (model.getEvent().getVenueType().equals("venue") && model.getEvent().getVenue() != null) ?
                                                model.getEvent().getVenue().getName() :
                                                (model.getEvent().getCustomVenue() != null) ? model.getEvent().getCustomVenue().getName() : null;
                                        Graphics.showAlertDialogWithOkButton(requireActivity(), getValue("cancellation_not_allowed"),
                                                setValue("cannot_cancel_value_less_than_2_hours",title));
                                    } else {
                                        showCancelDialog(getValue("confirm_cancel_attendance_warning"),model.getEvent().getFaq(),model, "out", mBinding);
//                                        Graphics.showAlertDialogWithOkCancel(requireActivity(), getString(R.string.app_name), title, "Yes", "No", isConfirmed -> {
//                                            if (isConfirmed) {
//                                                requestPromoterUpdateInviteStatus(model, "out", mBinding);
//                                            }
//                                        });
                                    }
                                } else {
                                    String title = getValue("confirm_cancel_attendance_warning");
                                    if ( mBinding.btnImOut.getTxtTitle().equals(getValue("pending"))) {
                                        title = getValue("cancel_your_interest");
                                    }

                                    showCancelDialog(title,model.getEvent().getFaq(),model, "out", mBinding);

//                                    Graphics.showAlertDialogWithOkCancel(requireActivity(), getString(R.string.app_name), title, "Yes", "No", isConfirmed -> {
//                                        if (isConfirmed) {
//                                            requestPromoterUpdateInviteStatus(model, "out", mBinding);
//                                        }
//                                    });
                                }
                            }
                        }
                    });
                });

                mBinding.eventImIn.setOnClickListener(view -> {
                    if (mBinding.eventImIn.getTxtTitle().equalsIgnoreCase(getValue("im_in")) || mBinding.eventImIn.getTxtTitle().equalsIgnoreCase(getValue("interested"))) {
                        if (model.getEvent().getInvite().getInviteStatus().equalsIgnoreCase(getValue("pending"))) {
                            String message = mBinding.eventImIn.getTxtTitle().equalsIgnoreCase(getValue("interested")) ? getValue("confirm_mark_interested_event") : getValue("spot_reserved_alert");
                            Graphics.showAlertDialogWithOkCancel(requireActivity(), activity.getString(R.string.app_name), message, getValue("yes"), getValue("no"), isConfirmed -> {
                                if (isConfirmed) {
                                    if (model.getEvent().isPlusOneAccepted() && model.getEvent().isPlusOneMandatory()) {
                                        showPlusOneInviteSheet(model);
                                    } else {
                                        requestPromoterUpdateInviteStatus(model, "in", mBinding);
                                    }
                                }
                            });
                            return;
                        }

                        String tmpString;
                        try {
                            tmpString = String.valueOf(Utils.stringToDateWithUTCForEvent(model.getEvent().getInvite().getUpdatedAt(), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        if (!TextUtils.isEmpty(tmpString) && Utils.checkUpdateTimeAndShowAlert(tmpString)) {
                            Graphics.showAlertDialogWithOkButton(requireActivity(), getValue("please_wait"), getValue("wait_before_changing_response"));
                        } else {
                            String message = mBinding.eventImIn.getTxtTitle().equals(getValue("interested"))
                                    ? getValue("confirm_mark_interested_event") : getValue("spot_reserved_alert");
                            Graphics.showAlertDialogWithOkCancel(requireActivity(), activity.getString(R.string.app_name), message, getValue("yes"), getValue("no"), isConfirmed -> {
                                if (isConfirmed) {
                                    if (model.getEvent().isPlusOneAccepted() && model.getEvent().isPlusOneMandatory()) {
                                        showPlusOneInviteSheet(model);
                                    } else {
                                        requestPromoterUpdateInviteStatus(model, "in", mBinding);
                                    }
                                }
                            });

                        }
                    }
                });

                int colorTransparent = itemView.getContext().getResources().getColor(R.color.transparent);
                mBinding.getRoot().setBackgroundColor(colorTransparent);
//                mBinding.txtTillDate.setText(String.format("%d Spot(s)", model.getEvent().getMaxInvitee()));

                if (activity != null) {
                    mBinding.tvTime.setText(Utils.getTimeAgoForEvent(model.getEvent().getCreatedAt(), activity));
                }
                mBinding.txtOfferTime.setText(String.format("%s - %s", model.getEvent().getStartTime(), model.getEvent().getEndTime()));

                mBinding.txtFromDate.setText(Utils.changeDateFormat(model.getEvent().getDate(), AppConstants.DATEFORMAT_SHORT, AppConstants.DATEFORMT_MM_DATE));

                mBinding.offerDescription.setText(model.getEvent().getDescription());
                mBinding.offerDescription.post(() -> {
                    int lineCount = mBinding.offerDescription.getLineCount();
                    if (lineCount > 2) {
                        Utils.makeTextViewResizable(mBinding.offerDescription, 3, 3, "..." + getValue("see_more") , true);
                    }
                });


                mBinding.profileLinear.setOnClickListener(view -> activity.startActivity(new Intent(activity,
                        PromoterPublicProfileActivity.class).
                        putExtra("isPromoterProfilePublic", true).putExtra("id", model.getEvent().getUser().getId())));

                mBinding.mainLayout.setOnClickListener(v -> {
                    Utils.preventDoubleClick(v);
                    Intent intent = new Intent(requireActivity(), ComplementaryEventDetailActivity.class);
                    intent.putExtra("eventId", model.getEvent().getId());
                    intent.putExtra("type", "complementary");
                    activity.startActivity(intent);

                });

            }

            private void showPlusOneInviteSheet(NotificationModel model) {
                AddPlusOneGuestBottomSheet bottomSheet = new AddPlusOneGuestBottomSheet();
                bottomSheet.eventID = model.getEvent().getId();
                bottomSheet.isInvitePlusOne = true;
                bottomSheet.promoterEventModel = model.getEvent();
                bottomSheet.callback = data -> {
                    if (data.equalsIgnoreCase("CallEventIn")) {
                        requestPromoterUpdateInviteStatus(model, "in", mBinding);
                    }
                };
                bottomSheet.show(getChildFragmentManager(), "AddPlusOneGuestBottomSheet");
            }

        }

    }


    // endregion
    // --------------------------------------

}