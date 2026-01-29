package com.whosin.business.ui.activites.wallet;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.whosin.business.R;
import com.whosin.business.comman.DiffAdapter;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Graphics;
import com.whosin.business.comman.TimerUtils;
import com.whosin.business.comman.Utils;
import com.whosin.business.comman.ui.UiUtils;
import com.whosin.business.databinding.ActivityWalletTicketDetailBinding;
import com.whosin.business.databinding.ItemJpSubHotelRoomBinding;
import com.whosin.business.databinding.ItemTicketDetailBinding;
import com.whosin.business.service.DataService;
import com.whosin.business.service.manager.RaynaTicketManager;
import com.whosin.business.service.models.ContainerListModel;
import com.whosin.business.service.models.ContainerModel;
import com.whosin.business.service.models.HomeTicketsModel;
import com.whosin.business.service.models.JuniperHotelModels.JPHotelPolicyRuleModel;
import com.whosin.business.service.models.JuniperHotelModels.JPPassengerModel;
import com.whosin.business.service.models.JuniperHotelModels.JpHotelRoomModel;
import com.whosin.business.service.models.MyWalletModel;
import com.whosin.business.service.models.rayna.RaynaTicketBookingModel;
import com.whosin.business.service.models.rayna.RaynaTicketDownloadModel;
import com.whosin.business.service.models.rayna.RaynaTourDetailModel;
import com.whosin.business.service.models.rayna.TourOptionDetailModel;
import com.whosin.business.service.models.rayna.TourOptionsModel;
import com.whosin.business.service.models.whosinTicketModel.RaynaWhosinBookingRulesModel;
import com.whosin.business.service.rest.RestCallback;
import com.whosin.business.ui.activites.JpHotel.JPHotelFeaturesSheet;
import com.whosin.business.ui.activites.comman.BaseActivity;
import com.whosin.business.ui.activites.raynaTicket.BottomSheets.CancelBookingDialog;
import com.whosin.business.ui.activites.raynaTicket.BottomSheets.CancellationPolicyBottomSheet;
import com.whosin.business.ui.adapter.BookingAddOnAdapter;
import com.whosin.business.ui.adapter.JPHotelCancellationPolicyAdapter;
import com.whosin.business.ui.adapter.JPHotelGuestDetailAdapter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TimeZone;
import java.util.stream.Collectors;

public class WalletTicketDetailActivity extends BaseActivity {

    private ActivityWalletTicketDetailBinding binding;

    private MyWalletModel myWalletModel;

    private final TourBookingAdapter<RaynaTourDetailModel> ticketAdapter = new TourBookingAdapter<>();

    private final TravelDeskTourBookingAdapter<RaynaTourDetailModel> travelDeskTourBookingAdapter = new TravelDeskTourBookingAdapter<>();

    private final HotelRoomListAdapter<JpHotelRoomModel> hotelRoomListAdapter = new HotelRoomListAdapter<>();

    private final BigBusTourBookingAdapter<RaynaTourDetailModel> bigBusTourBookingAdapter = new BigBusTourBookingAdapter<>();

    private final JPHotelGuestDetailAdapter<JPPassengerModel> guestDetailAdapter = new JPHotelGuestDetailAdapter<>();

    private final JPHotelCancellationPolicyAdapter<JPHotelPolicyRuleModel> jpHotelCancellationPolicyAdapter = new JPHotelCancellationPolicyAdapter<>();

    private boolean isWhosinBooking = false;

    private boolean isTravelDeskBooking = false;

    private boolean isBigBusBooking = false;

    private boolean isJPHotelBooking = false;

    private boolean isFromHistory = false;

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    protected void initUi() {

        applyTranslations();

        String model = getIntent().getStringExtra("model");
        isWhosinBooking = getIntent().getBooleanExtra("isWhosinBooking",false);
        isTravelDeskBooking = getIntent().getBooleanExtra("isTravelDeskBooking",false);
        isBigBusBooking = getIntent().getBooleanExtra("isBigBusBooking",false);
        isJPHotelBooking = getIntent().getBooleanExtra("isJPHotelBooking",false);
        isFromHistory = getIntent().getBooleanExtra("isFromHistory",false);
        myWalletModel = new Gson().fromJson(model, MyWalletModel.class);
        binding.constraintHeader.tvTitle.setText(getValue("ticket_details"));

        binding.successMessage.setText(Html.fromHtml(getValue("booking_success_text"), Html.FROM_HTML_MODE_LEGACY));

        boolean notClickAndEnable = false;

        if (myWalletModel != null) {
            String status = null;
            String paymentStatus = null;

            if (isWhosinBooking && myWalletModel.getWhosinTicket() != null) {
                status = myWalletModel.getWhosinTicket().getBookingStatus();
                paymentStatus = myWalletModel.getWhosinTicket().getPaymentStatus();
            } else if (isTravelDeskBooking && myWalletModel.getTraveldeskTicket() != null) {
                status = myWalletModel.getTraveldeskTicket().getBookingStatus();
                paymentStatus = myWalletModel.getTraveldeskTicket().getPaymentStatus();
            } else if (isBigBusBooking && myWalletModel.getOctoTicket() != null) {
                status = myWalletModel.getOctoTicket().getBookingStatus();
                paymentStatus = myWalletModel.getOctoTicket().getPaymentStatus();
            } else if (isJPHotelBooking && myWalletModel.getJuniperHotel() != null) {
                status = myWalletModel.getJuniperHotel().getBookingStatus();
                paymentStatus = myWalletModel.getJuniperHotel().getPaymentStatus();
            } else if (myWalletModel.getTicket() != null) {
                status = myWalletModel.getTicket().getBookingStatus();
                paymentStatus = myWalletModel.getTicket().getPaymentStatus();
            }

            notClickAndEnable = "initiated".equals(status);
            if (paymentStatus != null && paymentStatus.equals("paid") && (status.equals("rejected") || status.equals("failed"))){
                notClickAndEnable = true;
            }
        }


        if (notClickAndEnable){
            binding.downloadTicketBtn.setClickable(false);
            binding.downloadTicketBtn.setEnabled(false);
            binding.addTicketWallet.setVisibility(View.GONE);

            if (isWithinTenMinutes(myWalletModel.getCreatedAt())) {
                binding.btnSubTitle.setVisibility(View.VISIBLE);
                binding.downloadTicketBtn.setBackgroundColor(ContextCompat.getColor(activity, R.color.wallet_ticket_download_btn_bg));
                binding.btnTitle.setTextColor(ContextCompat.getColor(activity,R.color.white));
                TimerUtils.startCountdown(activity,myWalletModel.getCreatedAt(), binding.btnTitle, binding.fillProgress,this::finish);
            }else {
                binding.btnTitle.setTextColor(ContextCompat.getColor(activity,R.color.white));
                binding.btnTitle.setText(getValue("ticket_will_be_updated_soon"));
            }
        }



        if (isWhosinBooking){
            if (myWalletModel != null && myWalletModel.getWhosinTicket() != null){
                setWhosinTicketBookingDetail();
            }
        } else if (isTravelDeskBooking) {
            if (myWalletModel != null && myWalletModel.getTraveldeskTicket() != null){
                setTravelDeskBookingDetail();
            }
        } else if (isBigBusBooking) {
            if (myWalletModel != null && myWalletModel.getOctoTicket()!= null){
                setBigBusBookingDetail();
            }
        } else if (isJPHotelBooking) {
            if (myWalletModel != null && myWalletModel.getJuniperHotel()!= null){
                setJuniperHotelDetail();
            }
        } else {
            if (myWalletModel != null && myWalletModel.getTicket() != null){
                setBookingDetail();
            }
        }

        if (isJPHotelBooking){
          setPassengersDetailForJpHotel();
        }else {
            setPassengersDetail();
        }


    }

    @Override
    protected void setListeners() {

        binding.addTicketWallet.setOnClickListener(v -> {
            if (myWalletModel == null) return;
            String bookingId = "";
            if (isWhosinBooking) {
                if (myWalletModel.getWhosinTicket() != null) {
                    bookingId = myWalletModel.getWhosinTicket().getId();
                }
            } else if (isTravelDeskBooking) {
                if (myWalletModel.getTraveldeskTicket() != null) {
                    bookingId = myWalletModel.getTraveldeskTicket().getId();
                }
            } else if (isBigBusBooking) {
                if (myWalletModel.getOctoTicket() != null) {
                    bookingId = myWalletModel.getOctoTicket().getId();
                }
            }else if (isJPHotelBooking) {
                if (myWalletModel.getJuniperHotel() != null) {
                    bookingId = myWalletModel.getJuniperHotel().getId();
                }
            } else {
                if (myWalletModel.getTicket() != null) {
                    bookingId = myWalletModel.getTicket().getId();
                }
            }
            requestAddToGoogleWallet(bookingId);
        });

        binding.downloadTicketBtn.setOnClickListener(v -> {
            Utils.preventDoubleClick(v);

            if (myWalletModel == null) return;

            String url = null;
            if (isWhosinBooking) {
                if (myWalletModel.getWhosinTicket() == null) return;
                url = myWalletModel.getWhosinTicket().getDownloadTicket();
            } else if (isTravelDeskBooking) {
                if (myWalletModel.getTraveldeskTicket() == null) return;
                url = myWalletModel.getTraveldeskTicket().getDownloadTicket();
            }else if (isBigBusBooking) {
                if (myWalletModel.getOctoTicket() == null) return;
                url = myWalletModel.getOctoTicket().getDownloadTicket();
            }else if (isJPHotelBooking) {
                if (myWalletModel.getJuniperHotel() == null) return;
                url = myWalletModel.getJuniperHotel().getDownloadTicket();
            } else {
                if (myWalletModel.getTicket() == null) return;
                url = myWalletModel.getTicket().getDownloadTicket();
            }

            if (url == null || url.isEmpty()) return;

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.setPackage("com.android.chrome");
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }
        });

        binding.constraintHeader.ivClose.setOnClickListener(v -> finish());

    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityWalletTicketDetailBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected Map<View, String> getTranslationMap() {
        Map<View, String> map = new HashMap<>();
        map.put(binding.referenceLabel, "booking_refrence");
        map.put(binding.amountLabel, "total_amount");
        map.put(binding.btnTitle, "download_ticket");
        map.put(binding.tvAddToGoogleWallet, "add_to_google_wallet");
        map.put(binding.btnSubTitle, "ticket_will_be_updated_in");

        map.put(binding.guestInfoTitle, "guest_info");
        map.put(binding.firstNameLabel, "first_name");
        map.put(binding.lastNameLabel, "last_name");
        map.put(binding.emailLabel, "email");
        map.put(binding.mobileNumberLabel, "mobileNumber");
        map.put(binding.nationalityLabel, "nationality");

        map.put(binding.jpHotelDetailview.totalAmountLabel, "total_amount");
        map.put(binding.jpHotelDetailview.bookingStatus, "cancelled");

        binding.jpHotelDetailview.btnCancelBooking.setTxtTitle(getValue("cancel_booking"));

        map.put(binding.jpHotelDetailview.tvCancellationTitle, "cancellation_time");
        map.put(binding.jpHotelDetailview.tvRefundTitle, "refund");
        map.put(binding.jpHotelDetailview.tvGuestTitle, "guest_details");


        return map;
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------


    private void setPassengersDetail() {
        JPPassengerModel raynaPassengerModel = null;
        if (isWhosinBooking){
            raynaPassengerModel = myWalletModel.getWhosinTicket().getPassengers().get(0);
        } else if (isTravelDeskBooking) {
            raynaPassengerModel = myWalletModel.getTraveldeskTicket().getPassengers().get(0);
        } else if (isBigBusBooking) {
            raynaPassengerModel = myWalletModel.getOctoTicket().getPassengers().get(0);
        } else {
            raynaPassengerModel = myWalletModel.getTicket().getPassengers().get(0);
        }
        if (raynaPassengerModel == null) return;
        binding.tvFName.setText(raynaPassengerModel.getFirstName());
        binding.tvLName.setText(raynaPassengerModel.getLastName());
        binding.tvEmail.setText(raynaPassengerModel.getEmail());
        binding.tvPhoneNumber.setText(raynaPassengerModel.getMobile());
        binding.tvNationality.setText(raynaPassengerModel.getNationality());



        binding.ticketRecycle.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));

        if (isWhosinBooking){
            if (!myWalletModel.getWhosinTicket().getBookingStatus().equals("initiated")){

                if (myWalletModel.getWhosinTicket().getBookingStatus().equals("cancelled") && !myWalletModel.getWhosinTicket().getTourDetails().isEmpty()){
                    myWalletModel.getWhosinTicket().getTourDetails().forEach(q -> q.setStatus("Cancelled"));
                }
                binding.ticketRecycle.setAdapter(ticketAdapter);
                activity.runOnUiThread(() -> ticketAdapter.updateData(myWalletModel.getWhosinTicket().getTourDetails()));

            }
        } else if (isTravelDeskBooking) {
            if (!myWalletModel.getTraveldeskTicket().getBookingStatus().equals("initiated")){

                if (myWalletModel.getTraveldeskTicket().getBookingStatus().equals("cancelled") && !myWalletModel.getTraveldeskTicket().getTourDetails().isEmpty()){
                    myWalletModel.getTraveldeskTicket().getTourDetails().forEach(q -> q.setStatus("Cancelled"));
                }
                binding.ticketRecycle.setAdapter(travelDeskTourBookingAdapter);
                activity.runOnUiThread(() -> travelDeskTourBookingAdapter.updateData(myWalletModel.getTraveldeskTicket().getTourDetails()));

            }
        } else if (isBigBusBooking) {
            if (myWalletModel.getOctoTicket() != null && !myWalletModel.getOctoTicket().getBookingStatus().equals("initiated")){

                if (myWalletModel.getOctoTicket().getBookingStatus().equals("cancelled") && !myWalletModel.getOctoTicket().getTourDetails().isEmpty()){
                    myWalletModel.getOctoTicket().getTourDetails().forEach(q -> q.setStatus("Cancelled"));
                }
                binding.ticketRecycle.setAdapter(bigBusTourBookingAdapter);
                activity.runOnUiThread(() -> bigBusTourBookingAdapter.updateData(myWalletModel.getOctoTicket().getTourDetails()));

            }
        } else {
            if (!myWalletModel.getTicket().getBookingStatus().equals("initiated")){


                if (myWalletModel.getTicket().getBookingStatus().equals("cancelled") && !myWalletModel.getTicket().getTourDetails().isEmpty()){
                    myWalletModel.getTicket().getDetails().forEach(q -> q.setStatus("Cancelled"));
                }

                List<RaynaTourDetailModel> matchedList = new ArrayList<>();

                List<RaynaTourDetailModel> tourDetails = myWalletModel.getTicket().getTourDetails();
                List<RaynaTicketDownloadModel> downloadDetails = myWalletModel.getTicket().getDetails();

                if (!tourDetails.isEmpty() && !downloadDetails.isEmpty()) {
                    matchedList = tourDetails.stream()
                            .filter(detail -> downloadDetails.stream()
                                    .anyMatch(download -> detail.getServiceUniqueId() == Integer.parseInt(download.getServiceUniqueId())))
                            .collect(Collectors.toList());
                }

                if (!matchedList.isEmpty()) {
                    List<RaynaTourDetailModel> finalMatchedList = matchedList;
                    binding.ticketRecycle.setAdapter(ticketAdapter);
                    activity.runOnUiThread(() -> ticketAdapter.updateData(finalMatchedList));
                }

            }
        }
    }

    private void setBookingDetail() {
//        binding.successMessage.setText(Html.fromHtml("<b>Thank you for booking with us.</b> You can download Invoice Tickets."));

        Utils.setStyledText(activity,binding.amountValue,Utils.roundFloatValue(myWalletModel.getTicket().getAmount()));

        TourOptionDetailModel tourOptionDetailModel = myWalletModel.getTicket().getTourDetails().get(0).getTour();
        if (tourOptionDetailModel == null) return;
        if (tourOptionDetailModel.getCustomData().getImages() != null && !tourOptionDetailModel.getCustomData().getImages().isEmpty()){
            Graphics.loadImage(tourOptionDetailModel.getCustomData().getImages().get(0), binding.imgGallary);
        }

        binding.tourTitle.setText(tourOptionDetailModel.getTourName());
        binding.tourDescription.setText(Html.fromHtml(tourOptionDetailModel.getCustomData().getDescription()));
        binding.referenceValue.setText(myWalletModel.getTicket().getReferenceNo());


        if ("failed".equals(myWalletModel.getTicket().getBookingStatus())) {
            String status = Objects.equals(myWalletModel.getTicket().getPaymentStatus(), "refunded")
                    ? getValue("booking_failed_refunded")
                    : getValue("booking_failed");
            binding.btnTitle.setText(status);
            binding.downloadTicketBtn.setClickable(false);
            binding.downloadTicketBtn.setEnabled(false);
            binding.downloadTicketBtn.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
            binding.btnTitle.setTextColor(ContextCompat.getColor(activity, R.color.red));
            binding.addTicketWallet.setVisibility(View.GONE);
        } else if ("cancelled".equals(myWalletModel.getTicket().getBookingStatus())) {
//            String status = "Booking cancelled (" + myWalletModel.getTicket().getPaymentStatus() + ")";
            String status = setValue("booking_cancelled",myWalletModel.getTicket().getPaymentStatus());
            binding.btnTitle.setText(status);
            binding.downloadTicketBtn.setClickable(false);
            binding.downloadTicketBtn.setEnabled(false);
            binding.downloadTicketBtn.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
            binding.btnTitle.setTextColor(ContextCompat.getColor(activity, R.color.brand_pink));
            binding.addTicketWallet.setVisibility(View.GONE);
        } else if ("paid".equals(myWalletModel.getTicket().getPaymentStatus()) && "confirmed".equals(myWalletModel.getTicket().getBookingStatus())) {
            if (TextUtils.isEmpty(myWalletModel.getTicket().getDownloadTicket())) {
                binding.downloadTicketBtn.setBackgroundColor(ContextCompat.getColor(activity, R.color.gray));
                binding.downloadTicketBtn.setClickable(false);
            }
        } else if ("paid".equals(myWalletModel.getTicket().getPaymentStatus()) && "completed".equals(myWalletModel.getTicket().getBookingStatus())) {
            if (TextUtils.isEmpty(myWalletModel.getTicket().getDownloadTicket())) {
                binding.downloadTicketBtn.setBackgroundColor(ContextCompat.getColor(activity, R.color.gray));
                binding.downloadTicketBtn.setClickable(false);
            }
        }

    }

    private void setWhosinTicketBookingDetail() {

        Utils.setStyledText(activity,binding.amountValue,Utils.roundFloatValue(myWalletModel.getWhosinTicket().getAmount()));

        RaynaTourDetailModel tourOptionDetailModel = myWalletModel.getWhosinTicket().getTourDetails().get(0);

        if (tourOptionDetailModel != null) {
            HomeTicketsModel customModel = tourOptionDetailModel.getCustomData() != null
                    ? tourOptionDetailModel.getCustomData()
                    : tourOptionDetailModel.getCustomTicket();

            if (customModel != null) {
                List<String> images = customModel.getImages();
                if (images != null && !images.isEmpty()) {
                    Graphics.loadImage(images.get(0),  binding.imgGallary);
                }
                binding.tourTitle.setText(customModel.getTitle());
                if (!TextUtils.isEmpty(customModel.getDescription())) {
                    binding.tourDescription.setText(Html.fromHtml(customModel.getDescription()));
                    binding.tourDescription.setVisibility(View.VISIBLE);
                } else {
                    binding.tourDescription.setVisibility(View.GONE);
                }
            }
        }

        binding.referenceValue.setText(myWalletModel.getWhosinTicket().getBookingCode());


        if ("failed".equals(myWalletModel.getWhosinTicket().getBookingStatus())) {
            String status = Objects.equals(myWalletModel.getWhosinTicket().getPaymentStatus(), "refunded")
                    ? getValue("booking_failed_refunded")
                    : getValue("booking_failed");
            binding.btnTitle.setText(status);
            binding.downloadTicketBtn.setClickable(false);
            binding.downloadTicketBtn.setEnabled(false);
            binding.downloadTicketBtn.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
            binding.btnTitle.setTextColor(ContextCompat.getColor(activity, R.color.red));
            binding.addTicketWallet.setVisibility(View.GONE);
        } else if ("cancelled".equals(myWalletModel.getWhosinTicket().getBookingStatus())) {
//            String status = "Booking cancelled (" + myWalletModel.getWhosinTicket().getPaymentStatus() + ")";
            String status = setValue("booking_cancelled",myWalletModel.getWhosinTicket().getPaymentStatus());
            binding.btnTitle.setText(status);
            binding.downloadTicketBtn.setClickable(false);
            binding.downloadTicketBtn.setEnabled(false);
            binding.downloadTicketBtn.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
            binding.btnTitle.setTextColor(ContextCompat.getColor(activity, R.color.brand_pink));
            binding.addTicketWallet.setVisibility(View.GONE);
        }else if ("paid".equals(myWalletModel.getWhosinTicket().getPaymentStatus()) && "confirmed".equals(myWalletModel.getWhosinTicket().getBookingStatus())) {
            if (TextUtils.isEmpty(myWalletModel.getWhosinTicket().getDownloadTicket())){
                binding.downloadTicketBtn.setBackgroundColor(ContextCompat.getColor(activity, R.color.gray));
                binding.downloadTicketBtn.setClickable(false);
            }
        } else if ("paid".equals(myWalletModel.getWhosinTicket().getPaymentStatus()) && "completed".equals(myWalletModel.getWhosinTicket().getBookingStatus())) {
            if (TextUtils.isEmpty(myWalletModel.getWhosinTicket().getDownloadTicket())){
                binding.downloadTicketBtn.setBackgroundColor(ContextCompat.getColor(activity, R.color.gray));
                binding.downloadTicketBtn.setClickable(false);
            }
        }

    }

    private void setTravelDeskBookingDetail() {

        Utils.setStyledText(activity,binding.amountValue,Utils.roundFloatValue(myWalletModel.getTraveldeskTicket().getAmount()));

        RaynaTourDetailModel tourOptionDetailModel = myWalletModel.getTraveldeskTicket().getTourDetails().get(0);
        if (tourOptionDetailModel != null){
            HomeTicketsModel travelDeskOptionDataModel = tourOptionDetailModel.getCustomTicket();
            if (travelDeskOptionDataModel != null) {
                String name = travelDeskOptionDataModel.getTitle();
                binding.tourTitle.setText(!TextUtils.isEmpty(name) ? name : "");
                String description = travelDeskOptionDataModel.getDescription();
                if (!TextUtils.isEmpty(description)) {
                    binding.tourDescription.setText(Html.fromHtml(description));
                    binding.tourDescription.setVisibility(View.VISIBLE);
                } else {
                    binding.tourDescription.setVisibility(View.GONE);
                }

                if (!travelDeskOptionDataModel.getImages().isEmpty()){
                    String image = travelDeskOptionDataModel.getImages().get(0);
                    Graphics.loadImage(image, binding.imgGallary);
                }

            }
        }


          binding.referenceValue.setText(myWalletModel.getTraveldeskTicket().getBookingCode());

        if ("failed".equals(myWalletModel.getTraveldeskTicket().getBookingStatus())) {
            String status = Objects.equals(myWalletModel.getTraveldeskTicket().getPaymentStatus(), "refunded")
                    ? getValue("booking_failed_refunded")
                    : getValue("booking_failed");
            binding.btnTitle.setText(status);
            binding.downloadTicketBtn.setClickable(false);
            binding.downloadTicketBtn.setEnabled(false);
            binding.downloadTicketBtn.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
            binding.btnTitle.setTextColor(ContextCompat.getColor(activity, R.color.red));
            binding.addTicketWallet.setVisibility(View.GONE);
        } else if ("cancelled".equals(myWalletModel.getTraveldeskTicket().getBookingStatus())) {
//            String status = "Booking cancelled (" + myWalletModel.getTraveldeskTicket().getPaymentStatus() + ")";
            String status = setValue("booking_cancelled",myWalletModel.getTraveldeskTicket().getPaymentStatus());
            binding.btnTitle.setText(status);
            binding.downloadTicketBtn.setClickable(false);
            binding.downloadTicketBtn.setEnabled(false);
            binding.downloadTicketBtn.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
            binding.btnTitle.setTextColor(ContextCompat.getColor(activity, R.color.brand_pink));
            binding.addTicketWallet.setVisibility(View.GONE);
        }else if ("paid".equals(myWalletModel.getTraveldeskTicket().getPaymentStatus()) && "confirmed".equals(myWalletModel.getTraveldeskTicket().getBookingStatus())) {
            if (TextUtils.isEmpty(myWalletModel.getTraveldeskTicket().getDownloadTicket())){
                binding.downloadTicketBtn.setBackgroundColor(ContextCompat.getColor(activity, R.color.gray));
                binding.downloadTicketBtn.setClickable(false);
            }
        } else if ("paid".equals(myWalletModel.getTraveldeskTicket().getPaymentStatus()) && "completed".equals(myWalletModel.getTraveldeskTicket().getBookingStatus())) {
            if (TextUtils.isEmpty(myWalletModel.getTraveldeskTicket().getDownloadTicket())){
                binding.downloadTicketBtn.setBackgroundColor(ContextCompat.getColor(activity, R.color.gray));
                binding.downloadTicketBtn.setClickable(false);
            }
        }

    }

    private void setBigBusBookingDetail() {

        if (myWalletModel.getOctoTicket() == null) return;

        RaynaTicketBookingModel model =  myWalletModel.getOctoTicket();

        Utils.setStyledText(activity,binding.amountValue,Utils.roundFloatValue(model.getAmount()));

        RaynaTourDetailModel tourOptionDetailModel = model.getTourDetails().get(0);
        if (tourOptionDetailModel != null){
            HomeTicketsModel travelDeskOptionDataModel = tourOptionDetailModel.getCustomTicket();
            if (travelDeskOptionDataModel != null) {
                String name = travelDeskOptionDataModel.getTitle();
                binding.tourTitle.setText(!TextUtils.isEmpty(name) ? name : "");
                String description = travelDeskOptionDataModel.getDescription();
                if (!TextUtils.isEmpty(description)) {
                    binding.tourDescription.setText(Html.fromHtml(description));
                    binding.tourDescription.setVisibility(View.VISIBLE);
                } else {
                    binding.tourDescription.setVisibility(View.GONE);
                }

                if (!travelDeskOptionDataModel.getImages().isEmpty()){
                    String image = travelDeskOptionDataModel.getImages().get(0);
                    Graphics.loadImage(image, binding.imgGallary);
                }

            }
        }


          binding.referenceValue.setText(model.getBookingCode());

        if ("failed".equals(model.getBookingStatus())) {
            String status = Objects.equals(model.getPaymentStatus(), "refunded")
                    ? getValue("booking_failed_refunded")
                    : getValue("booking_failed");
            binding.btnTitle.setText(status);
            binding.downloadTicketBtn.setClickable(false);
            binding.downloadTicketBtn.setEnabled(false);
            binding.downloadTicketBtn.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
            binding.btnTitle.setTextColor(ContextCompat.getColor(activity, R.color.red));
            binding.addTicketWallet.setVisibility(View.GONE);
        } else if ("cancelled".equals(model.getBookingStatus())) {
//            String status = "Booking cancelled (" + model.getPaymentStatus() + ")";
            String status = setValue("booking_cancelled",model.getPaymentStatus());

            binding.btnTitle.setText(status);
            binding.downloadTicketBtn.setClickable(false);
            binding.downloadTicketBtn.setEnabled(false);
            binding.downloadTicketBtn.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
            binding.btnTitle.setTextColor(ContextCompat.getColor(activity, R.color.brand_pink));
            binding.addTicketWallet.setVisibility(View.GONE);
        }else if ("paid".equals(model.getPaymentStatus()) && "confirmed".equals(model.getBookingStatus())) {
            if (TextUtils.isEmpty(model.getDownloadTicket())){
                binding.downloadTicketBtn.setBackgroundColor(ContextCompat.getColor(activity, R.color.gray));
                binding.downloadTicketBtn.setClickable(false);
            }
        } else if ("paid".equals(model.getPaymentStatus()) && "completed".equals(model.getBookingStatus())) {
            if (TextUtils.isEmpty(model.getDownloadTicket())){
                binding.downloadTicketBtn.setBackgroundColor(ContextCompat.getColor(activity, R.color.gray));
                binding.downloadTicketBtn.setClickable(false);
            }
        }

    }

    private void setJuniperHotelDetail() {

        if (myWalletModel.getJuniperHotel() == null) return;

        RaynaTicketBookingModel model =  myWalletModel.getJuniperHotel();

        Utils.setStyledText(activity,binding.amountValue,Utils.roundFloatValue(model.getAmount()));

        RaynaTourDetailModel tourOptionDetailModel = model.getTourDetails().get(0);
        if (tourOptionDetailModel != null){
            HomeTicketsModel travelDeskOptionDataModel = tourOptionDetailModel.getCustomTicket();
            if (travelDeskOptionDataModel != null) {
                String name = travelDeskOptionDataModel.getTitle();
                binding.tourTitle.setText(!TextUtils.isEmpty(name) ? name : "");
                String description = travelDeskOptionDataModel.getDescription();
                if (!TextUtils.isEmpty(description)) {
                    binding.tourDescription.setText(Html.fromHtml(description));
                    binding.tourDescription.setVisibility(View.VISIBLE);
                } else {
                    binding.tourDescription.setVisibility(View.GONE);
                }

                if (!travelDeskOptionDataModel.getImages().isEmpty()){
                    String image = travelDeskOptionDataModel.getImages().get(0);
                    Graphics.loadImage(image, binding.imgGallary);
                }

            }
        }


        binding.referenceValue.setText(model.getBookingCode());

        if ("failed".equals(model.getBookingStatus())) {
            String status = Objects.equals(model.getPaymentStatus(), "refunded") ? getValue("booking_failed_refunded") : getValue("booking_failed");
            binding.btnTitle.setText(status);
            binding.downloadTicketBtn.setClickable(false);
            binding.downloadTicketBtn.setEnabled(false);
            binding.downloadTicketBtn.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
            binding.btnTitle.setTextColor(ContextCompat.getColor(activity, R.color.red));
            binding.addTicketWallet.setVisibility(View.GONE);
        } else if ("cancelled".equals(model.getBookingStatus())) {
//            String status = "Booking cancelled (" + model.getPaymentStatus() + ")";
            String status = setValue("booking_cancelled",model.getPaymentStatus());

            binding.btnTitle.setText(status);
            binding.downloadTicketBtn.setClickable(false);
            binding.downloadTicketBtn.setEnabled(false);
            binding.downloadTicketBtn.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
            binding.btnTitle.setTextColor(ContextCompat.getColor(activity, R.color.brand_pink));
            binding.addTicketWallet.setVisibility(View.GONE);
        }else if ("paid".equals(model.getPaymentStatus()) && "confirmed".equals(model.getBookingStatus())) {
            if (TextUtils.isEmpty(model.getDownloadTicket())){
                binding.downloadTicketBtn.setBackgroundColor(ContextCompat.getColor(activity, R.color.gray));
                binding.downloadTicketBtn.setClickable(false);
            }
        } else if ("paid".equals(model.getPaymentStatus()) && "completed".equals(model.getBookingStatus())) {
            if (TextUtils.isEmpty(model.getDownloadTicket())){
                binding.downloadTicketBtn.setBackgroundColor(ContextCompat.getColor(activity, R.color.gray));
                binding.downloadTicketBtn.setClickable(false);
            }
        }

    }


    private void setPassengersDetailForJpHotel() {
        binding.jpHotelDetailview.getRoot().setVisibility(View.VISIBLE);
        binding.ticketRecycle.setVisibility(View.GONE);
        binding.guestInfoCard.setVisibility(View.GONE);
        if (myWalletModel == null) return;
        if (myWalletModel.getJuniperHotel() == null) return;;

        binding.jpHotelDetailview.guestDetailView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        binding.jpHotelDetailview.guestDetailView.setAdapter(guestDetailAdapter);
        guestDetailAdapter.updateData(myWalletModel.getJuniperHotel().getPassengers());


        if (myWalletModel.getJuniperHotel().getBookingStatus().equals("initiated") || myWalletModel.getJuniperHotel().getBookingStatus().equals("failed")){
            binding.jpHotelDetailview.cardConstraint.setVisibility(View.GONE);
            binding.jpHotelDetailview.bookingCard.setVisibility(View.GONE);

        }else {
            binding.jpHotelDetailview.cancellationView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
            binding.jpHotelDetailview.cancellationView.setAdapter(jpHotelCancellationPolicyAdapter);

            binding.jpHotelDetailview.roomListView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
            binding.jpHotelDetailview.roomListView.setAdapter(hotelRoomListAdapter);


            if (myWalletModel.getJuniperHotel().getCancellationPolicy().isEmpty()) {
                binding.jpHotelDetailview.cardConstraint.setVisibility(View.GONE);
            } else {
                binding.jpHotelDetailview.cardConstraint.setVisibility(View.VISIBLE);
                List<JPHotelPolicyRuleModel> ruleModels = myWalletModel.getJuniperHotel()
                        .getCancellationPolicy()
                        .stream()
                        .map(JPHotelPolicyRuleModel::new)
                        .collect(Collectors.toList());
                jpHotelCancellationPolicyAdapter.updateData(ruleModels);
            }

            RaynaTourDetailModel tourOptionDetailModel = myWalletModel.getJuniperHotel().getTourDetails().get(0);
            if (tourOptionDetailModel != null){
                String title = "";

                if (tourOptionDetailModel.getOptionData() != null && tourOptionDetailModel.getOptionData().getBoard() != null && !TextUtils.isEmpty(tourOptionDetailModel.getOptionData().getBoard().getFullBoardName())) {
                    title = tourOptionDetailModel.getOptionData().getBoard().getFullBoardName();
                }

                binding.jpHotelDetailview.title.setText(title);


                String startDate = tourOptionDetailModel.getStartDate();
                String endDate = tourOptionDetailModel.getEndDate();

                binding.jpHotelDetailview.tvFromDate.setText(startDate);
                binding.jpHotelDetailview.tvToDate.setText(endDate);

                if (tourOptionDetailModel.getTourData() != null) {
                    String checkIn  = tourOptionDetailModel.getTourData().getCheckIn();
                    String checkOut = tourOptionDetailModel.getTourData().getCheckOut();

                    boolean hasCheckIn  = checkIn != null && !checkIn.trim().isEmpty();
                    boolean hasCheckOut = checkOut != null && !checkOut.trim().isEmpty();

                    if (hasCheckIn || hasCheckOut) {
                        binding.jpHotelDetailview.checkInCheckOutLayout.setVisibility(View.VISIBLE);

                        // Check-in section
                        if (hasCheckIn) {
                            binding.jpHotelDetailview.checkInLayout.setVisibility(View.VISIBLE);
                            binding.jpHotelDetailview.tvCheckInTime.setText(checkIn);
                        } else {
                            binding.jpHotelDetailview.checkInLayout.setVisibility(View.GONE);
                        }

                        // Check-out section
                        if (hasCheckOut) {
                            binding.jpHotelDetailview.checkOutLayout.setVisibility(View.VISIBLE);
                            binding.jpHotelDetailview.tvCheckOutTime.setText(checkOut);
                        } else {
                            binding.jpHotelDetailview.checkOutLayout.setVisibility(View.GONE);
                        }

                    } else {
                        // Dono empty/null
                        binding.jpHotelDetailview.checkInCheckOutLayout.setVisibility(View.GONE);
                    }
                } else {
                    binding.jpHotelDetailview.checkInCheckOutLayout.setVisibility(View.GONE);
                }


                String guestName = Utils.setLangValue("numberOfPaxHotel",String.valueOf(tourOptionDetailModel.getAdult()),String.valueOf(tourOptionDetailModel.getChild()));
                binding.jpHotelDetailview.tvGuestDetails.setText(guestName);

                Utils.setStyledText(activity,binding.jpHotelDetailview.tvAmount,tourOptionDetailModel.getWhosinTotal());


                if (tourOptionDetailModel.getOptionData() != null && !tourOptionDetailModel.getOptionData().getRooms().isEmpty()){
                    hotelRoomListAdapter.updateData(tourOptionDetailModel.getOptionData().getRooms());
                }

            }

            if (isFromHistory && myWalletModel.getJuniperHotel().getBookingStatus().equals("cancelled")) {
                binding.jpHotelDetailview.bookingStatus.setVisibility(View.VISIBLE);
                binding.jpHotelDetailview.btnCancelBooking.setVisibility(View.GONE);
            } else if (isFromHistory && myWalletModel.getJuniperHotel().getBookingStatus().equals("completed")) {
                binding.jpHotelDetailview.bookingStatus.setVisibility(View.GONE);
                binding.jpHotelDetailview.btnCancelBooking.setVisibility(View.GONE);
            } else if (isFromHistory) {
                binding.jpHotelDetailview.bookingStatus.setVisibility(View.GONE);
                binding.jpHotelDetailview.btnCancelBooking.setVisibility(View.GONE);
            } else {
                binding.jpHotelDetailview.bookingStatus.setVisibility(View.GONE);
                binding.jpHotelDetailview.btnCancelBooking.setVisibility(View.VISIBLE);
            }
        }

        binding.jpHotelDetailview.btnCancelBooking.setOnClickListener(v -> {
            Utils.preventDoubleClick(v);
            if (myWalletModel == null) return;
            if (myWalletModel.getJuniperHotel() == null) return;
            if (myWalletModel.getJuniperHotel().getTourDetails() == null || myWalletModel.getJuniperHotel().getTourDetails().isEmpty()) return;
            CancelBookingDialog cancelBookingDialog = new CancelBookingDialog();
            cancelBookingDialog.refundAmount = String.valueOf(calculateRefundAmountForJpHotel(Double.parseDouble(myWalletModel.getJuniperHotel().getTourDetails().get(0).getWhosinTotal()),myWalletModel.getJuniperHotel().getCancellationPolicy()));
            cancelBookingDialog.submitCallback = reason -> {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("cancellationReason", reason);
                jsonObject.addProperty("bookingId", myWalletModel.getJuniperHotel().getBookingCode());
                jsonObject.addProperty("bookingType", "juniper-hotel");
                jsonObject.addProperty("_id", myWalletModel.getJuniperHotel().getId());
                String title = binding.tourTitle.getText().toString();
                requestOctaBookingCancel(jsonObject, title);
            };
            cancelBookingDialog.show(getSupportFragmentManager(), "1");
        });

    }



    private JsonObject getJsonObject(RaynaTourDetailModel model){
        JsonObject jsonObject = new JsonObject();
//        jsonObject.addProperty("tourId", model.getTourId());
        Object tourId = model.getTourId();
        jsonObject.add("tourId", new Gson().toJsonTree(tourId));
        jsonObject.addProperty("tourOptionId",  model.getTourOption().getTourOptionId());
//        jsonObject.addProperty("contractId", model.getTour().getContractId());
        Object contractId = model.getTour().getContractId();
        jsonObject.add("contractId", new Gson().toJsonTree(contractId));
        jsonObject.addProperty("transferId", model.getTransferId());
        jsonObject.addProperty("date", model.getTourDate());
        jsonObject.addProperty("time",model.getStartTime());
        jsonObject.addProperty("noOfAdult", model.getAdult());
        jsonObject.addProperty("noOfChild", model.getChild());
        jsonObject.addProperty("noOfInfant", model.getInfant());
        return jsonObject;
    }

    private JsonObject getWhosinTicketJsonObject(RaynaTourDetailModel model){
        JsonObject jsonObject = new JsonObject();
        if (model.getTourOption() != null){
            jsonObject.addProperty("ticketId", model.getTourOption().getCustomTicketId());
            jsonObject.addProperty("optionId", model.getTourOption().getId());
            jsonObject.addProperty("date", model.getTourDate());
            if (!TextUtils.isEmpty(model.getTimeSlot())) {
                jsonObject.addProperty("time", model.getTimeSlot());
            } else if (model.getTourOption().getAvailabilityType().equals("regular") && !TextUtils.isEmpty(model.getTourOption().getAvailabilityTime())) {
                jsonObject.addProperty("time", model.getTourOption().getAvailabilityTime());
            }
            jsonObject.addProperty("adults", model.getAdult());
            jsonObject.addProperty("childs", model.getChild());
            jsonObject.addProperty("infants", model.getInfant());
        }else {
            Object tourId = model.getTourId();
            jsonObject.add("tourId", new Gson().toJsonTree(tourId));
            jsonObject.add("tourOptionId", new Gson().toJsonTree(model.getOptionId()));
            jsonObject.addProperty("slotId", model.getTimeSlotId());
            jsonObject.addProperty("date", model.getTourDate());
            jsonObject.addProperty("time", model.getTimeSlot());
            jsonObject.addProperty("adults", model.getAdult());
            jsonObject.addProperty("childs", model.getChild());
        }

        return jsonObject;
    }

    private JsonObject getTravelDeskTicketJsonObject(RaynaTourDetailModel model){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("tourId", model.getTourOption().getTourId());
        jsonObject.addProperty("optionId", model.getTourOption().getOptionId());
        jsonObject.addProperty("date", model.getTourDate());
        jsonObject.addProperty("adults", model.getAdult());
        jsonObject.addProperty("childs", model.getChild());
        jsonObject.addProperty("infant", model.getInfant());
        return jsonObject;
    }

    private JsonObject getBigBusTicketJsonObject(RaynaTourDetailModel model){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("tourId", String.valueOf(model.getTourId()));
        jsonObject.addProperty("optionId", String.valueOf(model.getOptionId()));
        jsonObject.addProperty("date", model.getTourDate());
        return jsonObject;
    }

    private void openInBrowser(String url) {
        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            browserIntent.addCategory(Intent.CATEGORY_BROWSABLE);
            browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(browserIntent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(activity, "No browser available to open the link.", Toast.LENGTH_SHORT).show();
        }
    }

    private void progressBarHideAndShow(boolean showProgress) {
        binding.tvAddToGoogleWallet.setVisibility(showProgress ? View.GONE : View.VISIBLE);
        binding.imgGoogleWallet.setVisibility(showProgress ? View.GONE : View.VISIBLE);
        binding.addToWalletProgressView.setVisibility(showProgress ? View.VISIBLE : View.GONE);
    }

    private void openCancellationDialog(RaynaTourDetailModel model,List<RaynaWhosinBookingRulesModel> policy){
        CancelBookingDialog cancelBookingDialog = new CancelBookingDialog();
        cancelBookingDialog.refundAmount = String.valueOf(Utils.roundDoubleValueToDouble(calculateRefundAmountForWhosin(parseDoubleSafe(model.getWhosinTotal()),policy)));
        cancelBookingDialog.submitCallback = reason -> {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("cancellationReason",reason);
            jsonObject.addProperty("bookingId",model.getBookingId());
            jsonObject.addProperty("_id",myWalletModel.getWhosinTicket().getId());
            String name = "";
            if (model.getOptionData() != null){
                name = model.getOptionData().getDisplayName();
            }
            requestRaynaWhosinCustomTourBookingCancel(jsonObject,name);
        };
        cancelBookingDialog.show(getSupportFragmentManager(), "1");
    }

    private double parseDoubleSafe(String value) {
        if (value != null && !value.isEmpty()) {
            try {
                return Double.parseDouble(value);
            } catch (NumberFormatException e) {
                e.printStackTrace(); // Optional: log the error
            }
        }
        return 0.0;
    }


    // endregion
    // --------------------------------------
    // region Public
    // --------------------------------------

    public static boolean isWithinTenMinutes(String createdDateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date createdDate = sdf.parse(createdDateStr);
            Date currentDate = new Date();
            long diffInMillis = Math.abs(currentDate.getTime() - createdDate.getTime());
            long diffInMinutes = diffInMillis / (60 * 1000);
            return diffInMinutes <= 15;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Double calculateRefundAmount(double amount, List<TourOptionsModel> policies) {
        Date currentDate = new Date();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);

        for (TourOptionsModel policy : policies) {
            try {
                Date fromDate = dateFormatter.parse(policy.getFromDate());
                Date toDate = dateFormatter.parse(policy.getToDate());

                if (fromDate != null && toDate != null &&
                        currentDate.compareTo(fromDate) >= 0 &&
                        currentDate.compareTo(toDate) <= 0) {

                    int nonRefundablePercentage = policy.getPercentage();
                    double refundablePercentage = 100 - nonRefundablePercentage;

                    return amount * refundablePercentage / 100.0;
                }
            } catch (ParseException e) {
                e.printStackTrace();
                return 0.0;
            }
        }

        return 0.0;
    }

    public static Double calculateRefundAmountForWhosin(double amount, List<RaynaWhosinBookingRulesModel> policies) {
        Date currentDate = new Date();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);

        for (RaynaWhosinBookingRulesModel policy : policies) {
            try {
                Date fromDate = dateFormatter.parse(policy.getFromDate());
                Date toDate = dateFormatter.parse(policy.getToDate());

                if (fromDate != null && toDate != null &&
                        currentDate.compareTo(fromDate) >= 0 &&
                        currentDate.compareTo(toDate) <= 0) {

                    int nonRefundablePercentage = policy.getPercentage();
                    double refundablePercentage = 100 - nonRefundablePercentage;

                    return amount * refundablePercentage / 100.0;
                }
            } catch (ParseException e) {
                e.printStackTrace();
                return 0.0;
            }
        }

        return 0.0;
    }

    public static boolean isNonRefundable(List<TourOptionsModel> policies) {
        if (policies == null) return  false;
        if (policies.isEmpty())return  false;
        Date currentDate = new Date();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);

        for (TourOptionsModel policy : policies) {
            try {
                Date fromDate = dateFormatter.parse(policy.getFromDate());
                Date toDate = dateFormatter.parse(policy.getToDate());

                if (fromDate != null && toDate != null) {
                    if (!currentDate.before(fromDate) && !currentDate.after(toDate)) {
                        return policy.getRefundPercentage() == 0;
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace(); // Handle parse error
            }
        }
        return false;
    }

    public static Double calculateRefundAmountForJpHotel(double amount, List<TourOptionsModel> policies) {
        Date currentDate = new Date();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        for (TourOptionsModel policy : policies) {
            try {
                Date fromDate = dateFormatter.parse(policy.getDateFrom());
                Date toDate = dateFormatter.parse(policy.getDateTo());

                if (fromDate != null && toDate != null &&
                        currentDate.compareTo(fromDate) >= 0 &&
                        currentDate.compareTo(toDate) <= 0) {

                    int nonRefundablePercentage = Integer.parseInt(policy.getPercentPrice());
                    double refundablePercentage = 100 - nonRefundablePercentage;
                    double refund = amount * refundablePercentage / 100.0;
                    return new BigDecimal(refund)
                            .setScale(2, RoundingMode.DOWN)
                            .doubleValue();
                }
            } catch (ParseException e) {
                e.printStackTrace();
                return 0.0;
            }
        }

        return 0.0;
    }

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestRaynaTourBookingCancel(String reason, int id, String _id ,RaynaTourDetailModel tmpModel) {
        showProgress();
        DataService.shared(activity).requestRaynaTourBookingCancel(reason, id, _id,new RestCallback<ContainerModel<RaynaTicketBookingModel>>(this) {
            @Override
            public void result(ContainerModel<RaynaTicketBookingModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (RaynaTicketManager.shared.callback != null){
                    RaynaTicketManager.shared.callback.onReceive(tmpModel.getTour().getTourName() + " Cancelled Successfully.");
                }
                activity.finish();
            }
        });
    }

    private void requestRaynaWhosinTourBookingCancel(String bookingID, String _id ,String name) {
        showProgress();
        DataService.shared(activity).requestRaynaWhosinTourBookingCancel(bookingID, _id,new RestCallback<ContainerModel<RaynaTicketBookingModel>>(this) {
            @Override
            public void result(ContainerModel<RaynaTicketBookingModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (RaynaTicketManager.shared.callback != null){
                    RaynaTicketManager.shared.callback.onReceive(name + " Cancelled Successfully.");
                }
                activity.finish();
            }
        });
    }

    private void requestTravleDeskBookingCancel(String bookingID, String _id, String name) {
        showProgress();
        DataService.shared(activity).requestTravleDeskBookingCancel(bookingID, _id, "travel-desk", new RestCallback<ContainerModel<RaynaTicketBookingModel>>(this) {
            @Override
            public void result(ContainerModel<RaynaTicketBookingModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), error);
                    return;
                }

                if (RaynaTicketManager.shared.callback != null) {
                    RaynaTicketManager.shared.callback.onReceive(name + " Cancelled Successfully.");
                }
                activity.finish();
            }
        });
    }

    private void requestOctaBookingCancel(JsonObject jsonObject,String name) {
        showProgress();
        DataService.shared(activity).requestOctaBookingCancel(jsonObject, new RestCallback<>(this) {
            @Override
            public void result(ContainerModel<RaynaTicketBookingModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    if (error.contains("Booking cancelled!!")) {
                        if (RaynaTicketManager.shared.callback != null) {
                            RaynaTicketManager.shared.callback.onReceive(name + " Cancelled Successfully.");
                        }
                        activity.finish();
                    }
                    Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), error);
                    return;
                }

                if (RaynaTicketManager.shared.callback != null) {
                    RaynaTicketManager.shared.callback.onReceive(name + " Cancelled Successfully.");
                }
                activity.finish();
            }
        });
    }

    private void requestAddToGoogleWallet(String bookingId) {
        progressBarHideAndShow(true);
        DataService.shared(activity).requestAddToGoogleWallet(bookingId,new RestCallback<ContainerModel<List<String>>>(this) {
            @Override
            public void result(ContainerModel<List<String>> model, String error) {
                progressBarHideAndShow(false);
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (model.getData() != null && !model.getData().isEmpty()) {
                    String url = model.getData().get(0);
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        if (intent.resolveActivity(activity.getPackageManager()) != null) {
                            activity.startActivity(intent);
                        } else {
                            openInBrowser(url);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        openInBrowser(url);
                    }
                }

            }
        });
    }

    private void requestWhosinCustomTourPolicy(JsonObject jsonObject, RaynaTourDetailModel raynaTourDetailModel) {
        showProgress();
        DataService.shared(activity).requestWhosinCustomTicketTourPolicy(jsonObject, new RestCallback<ContainerListModel<RaynaWhosinBookingRulesModel>>(this) {
            @Override
            public void result(ContainerListModel<RaynaWhosinBookingRulesModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), error);
                    openCancellationDialog(raynaTourDetailModel,new ArrayList<>());
                    return;
                }

                if (model.data != null && !model.data.isEmpty()) {
                    openCancellationDialog(raynaTourDetailModel, model.data);
                }else {
                    openCancellationDialog(raynaTourDetailModel,new ArrayList<>());
                }
            }
        });
    }

    private void requestRaynaWhosinCustomTourBookingCancel(JsonObject jsonObject, String name) {
        showProgress();
        DataService.shared(activity).requestRaynaWhosinCustomTourBookingCancel(jsonObject, new RestCallback<ContainerModel<RaynaTicketBookingModel>>(this) {
            @Override
            public void result(ContainerModel<RaynaTicketBookingModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (RaynaTicketManager.shared.callback != null) {
                    RaynaTicketManager.shared.callback.onReceive(name + " Cancelled Successfully.");
                }
                activity.finish();
            }
        });
    }

    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    private class TourBookingAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = UiUtils.getViewBy(parent, R.layout.item_ticket_detail);
            ViewGroup.LayoutParams params = view.getLayoutParams();
            view.setLayoutParams(params);
            return new ViewHolder(view);

        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

            ViewHolder viewHolder = (ViewHolder) holder;
            RaynaTourDetailModel model = (RaynaTourDetailModel) getItem(position);
            if (model == null) return;
            activity.runOnUiThread(() -> {

                viewHolder.mBinding.totalAmountLabel.setText(getValue("total_amount"));
                viewHolder.mBinding.bookingStatus.setText(getValue("cancelled"));
                viewHolder.mBinding.btnCancellationPolicy.setText(getValue("cancellation_policy"));
                viewHolder.mBinding.btnCancelBooking.setTxtTitle(getValue("cancel_booking"));

                if (model.getTourOption() != null) {
                    viewHolder.mBinding.title.setText(Utils.notNullString(model.getTourOption().getOptionName()));
                }else {
                    if (model.getOptionData() != null){
                        viewHolder.mBinding.title.setText(Utils.notNullString(model.getOptionData().getDisplayName()));
                    }
                }


//                String guestName = Utils.setLangValue("numberOfPax",String.valueOf(model.getAdult()),String.valueOf(model.getChild()),String.valueOf(model.getInfant()));
                String guestName = Utils.setLangValue("numberOfPax",String.valueOf(model.getAdult()),model.getAdultTitle(),String.valueOf(model.getChild()),model.getChildTitle(),String.valueOf(model.getInfant()),model.getInfantTitle());
                viewHolder.mBinding.tvPerson.setText(guestName);

                String tourDate = model.getTourDate();

                if (tourDate != null && !tourDate.isEmpty()) {
                    if (tourDate.contains("T")) {
                        try {
                            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
                            isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

                            Date date = isoFormat.parse(tourDate);
                            SimpleDateFormat desiredFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                            String formattedDate = desiredFormat.format(date);

                            viewHolder.mBinding.tvDate.setText(formattedDate);
                        } catch (ParseException e) {
                            viewHolder.mBinding.tvDate.setText(tourDate);
                        }
                    } else {
                        viewHolder.mBinding.tvDate.setText(tourDate);
                    }
                } else {
                    viewHolder.mBinding.tvDate.setText("");
                }

                if (isWhosinBooking) {
                    if (model.getTourOption() != null){
                        if (!TextUtils.isEmpty(model.getTimeSlot())) {
                            viewHolder.mBinding.tvTime.setText(model.getTimeSlot());
                        } else if (model.getTourOption().getAvailabilityType().equals("regular") && !TextUtils.isEmpty(model.getTourOption().getAvailabilityTime())) {
                            viewHolder.mBinding.tvTime.setText(model.getTourOption().getAvailabilityTime());
                        }

                    }else {
                        if (!TextUtils.isEmpty(model.getTimeSlot())) {
                            viewHolder.mBinding.tvTime.setText(model.getTimeSlot());
                        } else if (!TextUtils.isEmpty(model.getStartTime())) {
                            viewHolder.mBinding.tvTime.setText(model.getStartTime());
                        }
                    }
                } else {
                    viewHolder.mBinding.tvTime.setText(model.getStartTime());
                }

//
                if (isWhosinBooking){
                    viewHolder.mBinding.transferTypeLayout.setVisibility(View.GONE);
                }else {
                    viewHolder.mBinding.transferTypeLayout.setVisibility(View.VISIBLE);
                    viewHolder.mBinding.tvTranstype.setText(Utils.getTransferName(model.getTransferId()));
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
                double addonAmount = model.getAddons().stream()
                        .mapToDouble(addon -> {
                            try {
                                return Double.parseDouble(addon.getWhosinTotal());
                            } catch (Exception e) {
                                return 0.0;
                            }
                        })
                        .sum();

                double totalAmount =
                        Double.parseDouble(model.getWhosinTotal()) + addonAmount;

                Utils.setStyledText(activity,viewHolder.mBinding.tvAmount,String.valueOf(totalAmount));
                Utils.setStyledText(activity,viewHolder.mBinding.ticketAmounttv,model.getWhosinTotal());
                Utils.setStyledText(activity,viewHolder.mBinding.tvAmount,model.getWhosinTotal());


                if (isWhosinBooking){
                    if (model.getTourOption() != null && model.getTourOption().getCancellationPolicy().equalsIgnoreCase("Non Refundable")){
                        viewHolder.mBinding.btnCancellationPolicy.setText(getValue("non_refundable"));
                        viewHolder.mBinding.cancellationPolicyLayout.setBackgroundColor(ContextCompat.getColor(activity,R.color.ticket_non_ref_colour));
                        viewHolder.mBinding.btnCancelBooking.setVisibility(View.GONE);
                    }else {
                        viewHolder.mBinding.btnCancellationPolicy.setText(getValue("cancellation_policy"));
                        viewHolder.mBinding.cancellationPolicyLayout.setBackgroundColor(ContextCompat.getColor(activity,R.color.cancellation_policy__bg));
                        viewHolder.mBinding.btnCancelBooking.setVisibility(View.VISIBLE);
                    }
                } else if (myWalletModel.getType().equals("whosin-ticket")) {
                    if (model.getOptionData() != null && model.getOptionData().getCancellationPolicy().trim().equalsIgnoreCase("Non Refundable")){
                        viewHolder.mBinding.btnCancellationPolicy.setText(getValue("non_refundable"));
                        viewHolder.mBinding.cancellationPolicyLayout.setBackgroundColor(ContextCompat.getColor(activity,R.color.ticket_non_ref_colour));
                        viewHolder.mBinding.btnCancelBooking.setVisibility(View.GONE);
                    }else {
                        viewHolder.mBinding.btnCancellationPolicy.setText(getValue("cancellation_policy"));
                        viewHolder.mBinding.cancellationPolicyLayout.setBackgroundColor(ContextCompat.getColor(activity,R.color.cancellation_policy__bg));
                        viewHolder.mBinding.btnCancelBooking.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (myWalletModel != null
                            && myWalletModel.getTicket() != null
                            && myWalletModel.getTicket().getTourDetails() != null
                            && !myWalletModel.getTicket().getTourDetails().isEmpty()
                            && myWalletModel.getTicket().getTourDetails().get(0) != null
                            && myWalletModel.getTicket().getTourDetails().get(0).getTourOption() != null
                            && myWalletModel.getTicket().getTourDetails().get(0).getTourOption().getCancellationPolicy() != null
                            && myWalletModel.getTicket().getTourDetails().get(0).getTourOption().getCancellationPolicy().equalsIgnoreCase("Non Refundable")) {
                        viewHolder.mBinding.btnCancellationPolicy.setText(getValue("non_refundable"));
                        viewHolder.mBinding.cancellationPolicyLayout.setBackgroundColor(ContextCompat.getColor(activity,R.color.ticket_non_ref_colour));
                        viewHolder.mBinding.btnCancelBooking.setVisibility(View.GONE);
                    } else {
                        viewHolder.mBinding.btnCancellationPolicy.setText(getValue("cancellation_policy"));
                        viewHolder.mBinding.cancellationPolicyLayout.setBackgroundColor(ContextCompat.getColor(activity,R.color.cancellation_policy__bg));
                        viewHolder.mBinding.btnCancelBooking.setVisibility(View.VISIBLE);
                    }
                }

                if (isWhosinBooking){
                    String status = model.getStatus();
                    if (status.equals("Success")) {
                        viewHolder.mBinding.btnCancelBooking.setVisibility(View.VISIBLE);
                        viewHolder.mBinding.bookingStatus.setVisibility(View.GONE);

                    } else {
                        viewHolder.mBinding.btnCancelBooking.setVisibility(View.GONE);
                        viewHolder.mBinding.bookingStatus.setVisibility(View.VISIBLE);
                    }
                }else {
                    Optional<RaynaTicketDownloadModel> tmpDetailModel = myWalletModel.getTicket().getDetails().stream().filter(p -> model.getServiceUniqueId() == Integer.parseInt(p.getServiceUniqueId())).findFirst();
                    if (tmpDetailModel.isPresent()) {
                        String status = tmpDetailModel.get().getStatus();
                        if (status.equals("Success")) {
                            viewHolder.mBinding.btnCancelBooking.setVisibility(View.VISIBLE);
                            viewHolder.mBinding.bookingStatus.setVisibility(View.GONE);

                        } else {
                            viewHolder.mBinding.btnCancelBooking.setVisibility(View.GONE);
                            viewHolder.mBinding.bookingStatus.setVisibility(View.VISIBLE);
                        }
                    }
                }


                viewHolder.mBinding.cancellationPolicyLayout.setOnClickListener(view -> {
                    if (isWhosinBooking){
                        if (model.getTourOption() != null && model.getTourOption().getCancellationPolicy().equalsIgnoreCase(getValue("non_refundable"))){
                            return;
                        }
                    }else {
                        if (myWalletModel != null
                                && myWalletModel.getTicket() != null
                                && myWalletModel.getTicket().getTourDetails() != null
                                && !myWalletModel.getTicket().getTourDetails().isEmpty()
                                && myWalletModel.getTicket().getTourDetails().get(0) != null
                                && myWalletModel.getTicket().getTourDetails().get(0).getTourOption() != null
                                && myWalletModel.getTicket().getTourDetails().get(0).getTourOption().getCancellationPolicy() != null
                                && myWalletModel.getTicket().getTourDetails().get(0).getTourOption().getCancellationPolicy().equalsIgnoreCase("Non Refundable")) {
                            return;
                        }
                    }

                    Utils.preventDoubleClick(view);
                    CancellationPolicyBottomSheet cancellationPolicyBottomSheet = new CancellationPolicyBottomSheet();
                    if (isWhosinBooking){
                        cancellationPolicyBottomSheet.jsonObject = getWhosinTicketJsonObject(model);
                    }else {
                        cancellationPolicyBottomSheet.jsonObject = getJsonObject(model);
                    }
                    if (model.getTourOption() != null) {
                        cancellationPolicyBottomSheet.policyText = model.getTourOption().getCancellationPolicyDescription();
                        cancellationPolicyBottomSheet.isWhosinTypeTicket = isWhosinBooking;
                    } else {
                        cancellationPolicyBottomSheet.isWhosinCustomTypeTicket = isWhosinBooking;
                    }
                    cancellationPolicyBottomSheet.isFormViewTicket = true;
                    cancellationPolicyBottomSheet.activity = activity;
                    cancellationPolicyBottomSheet.show(getSupportFragmentManager(), "1");
                });

                List<TourOptionsModel> policy;

                if (isWhosinBooking) {
                    if (myWalletModel != null &&
                            myWalletModel.getWhosinTicket() != null &&
                            myWalletModel.getWhosinTicket().getCancellationPolicy() != null) {

                        policy = myWalletModel.getWhosinTicket()
                                .getCancellationPolicy()
                                .stream()
                                .filter(p -> p != null &&
                                        String.valueOf(p.getOptionId()).equals(String.valueOf(model.getOptionId())))
                                .collect(Collectors.toList());
                    } else {
                        policy = new ArrayList<>();
                    }
                } else {
                    if (myWalletModel != null &&
                            myWalletModel.getTicket() != null &&
                            myWalletModel.getTicket().getCancellationPolicy() != null) {

//                        policy = myWalletModel.getTicket()
//                                .getCancellationPolicy()
//                                .stream()
//                                .filter(p -> p != null &&
//                                        String.valueOf(p.getOptionId()).equals(String.valueOf(model.getOptionId())))
//                                .collect(Collectors.toList());

                        policy = myWalletModel.getTicket()
                                .getCancellationPolicy()
                                .stream()
                                .filter(p -> {
                                    try {
                                        if (p == null || p.getOptionId() == null || model.getOptionId() == null) return false;

                                        BigDecimal id1 = new BigDecimal(p.getOptionId().toString().trim());
                                        BigDecimal id2 = new BigDecimal(model.getOptionId().toString().trim());

                                        return id1.compareTo(id2) == 0;
                                    } catch (Exception e) {
                                        return false;
                                    }
                                })
                                .collect(Collectors.toList());
                    } else {
                        policy = new ArrayList<>();
                    }
                }

                boolean isNonRefundable = isNonRefundable(policy);
                boolean isCompleted ;
                if (isWhosinBooking) {
                    isCompleted = myWalletModel != null && myWalletModel.getWhosinTicket() != null && "completed".equalsIgnoreCase(myWalletModel.getWhosinTicket().getBookingStatus());
                } else {
                    isCompleted = myWalletModel != null && myWalletModel.getTicket() != null && "completed".equalsIgnoreCase(myWalletModel.getTicket().getBookingStatus());
                }

                if (isNonRefundable || isCompleted) {
                    viewHolder.mBinding.btnCancelBooking.setVisibility(View.GONE);
                }


                viewHolder.mBinding.btnCancelBooking.setOnClickListener(view -> {
                    if (isWhosinBooking){
                        if (policy.isEmpty()){
                            requestWhosinCustomTourPolicy(getWhosinTicketJsonObject(model),model);
                            return;
                        }
                        String message = "";
                        String refundAmount = String.valueOf(Utils.roundDoubleValueToDouble(calculateRefundAmount(parseDoubleSafe(model.getWhosinTotal()),policy)));
                        if (TextUtils.isEmpty(refundAmount)) refundAmount = "0.0";
                        String descriptionText = "";
                        if (Objects.equals(refundAmount, "0.0")) {
                            descriptionText = getValue("noRefundText");
                            message = setValue("cancelConfirmationTicket",model.getTourOption().getOptionName(),descriptionText);
                            Graphics.showAlertDialogWithOkCancel(activity, activity.getString(R.string.app_name), message, getValue("yes"), getValue("no"), isConfirmed -> {
                                if (isConfirmed) {
                                    requestRaynaWhosinTourBookingCancel(model.getBookingId(),myWalletModel.getWhosinTicket().getId(),model.getTourOption().getOptionName());
                                }
                            });
                        } else {
                            SpannableString styledPrice = Utils.getStyledText(activity, refundAmount);
                            SpannableStringBuilder fullText = new SpannableStringBuilder()
                                    .append(getValue("are_you_sure_cancel"))
                                    .append(model.getTourOption().getOptionName())
                                    .append("\n")
                                    .append(getValue("refund_message"))
                                    .append(" ")
                                    .append(styledPrice)
                                    .append(getValue("customer_support_message"));

                            Graphics.alertDialogYesNoBtnWithUIFlag(activity, activity.getString(R.string.app_name), fullText, false, getValue("no"), getValue("yes"), aBoolean -> {
                                if (aBoolean) {
                                    requestRaynaWhosinTourBookingCancel(model.getBookingId(),myWalletModel.getWhosinTicket().getId(),model.getTourOption().getOptionName());
                                }
                            });
                        }


                    }else {
                        CancelBookingDialog cancelBookingDialog = new CancelBookingDialog();
                        cancelBookingDialog.refundAmount = String.valueOf(Utils.roundDoubleValueToDouble(calculateRefundAmount(parseDoubleSafe(model.getWhosinTotal()),policy)));
                        cancelBookingDialog.submitCallback = reason -> {
                            Optional<RaynaTicketDownloadModel> tmpDetailModel = myWalletModel.getTicket().getDetails().stream().filter(p -> model.getServiceUniqueId() == Integer.parseInt(p.getServiceUniqueId())).findFirst();
                            tmpDetailModel.ifPresent(raynaTicketDownloadModel -> requestRaynaTourBookingCancel(reason, raynaTicketDownloadModel.getBookingId(), myWalletModel.getTicket().getId(), model));
                        };
                        cancelBookingDialog.show(getSupportFragmentManager(), "1");
                    }

                });

            });
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final ItemTicketDetailBinding mBinding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemTicketDetailBinding.bind(itemView);
            }

        }


    }

    private class TravelDeskTourBookingAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = UiUtils.getViewBy(parent, R.layout.item_ticket_detail);
            ViewGroup.LayoutParams params = view.getLayoutParams();
            view.setLayoutParams(params);
            return new ViewHolder(view);

        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

            ViewHolder viewHolder = (ViewHolder) holder;
            RaynaTourDetailModel model = (RaynaTourDetailModel) getItem(position);
            if (model == null) return;
            activity.runOnUiThread(() -> {

                viewHolder.mBinding.totalAmountLabel.setText(getValue("total_amount"));
                viewHolder.mBinding.bookingStatus.setText(getValue("cancelled"));
                viewHolder.mBinding.btnCancellationPolicy.setText(getValue("cancellation_policy"));
                viewHolder.mBinding.btnCancelBooking.setTxtTitle(getValue("cancel_booking"));

                if (model.getOptionData() != null && !TextUtils.isEmpty(model.getOptionData().getName())) {
                    viewHolder.mBinding.title.setVisibility(View.VISIBLE);
                    viewHolder.mBinding.title.setText(Utils.notNullString(model.getOptionData().getName()));
                } else {
                    viewHolder.mBinding.title.setVisibility(View.GONE);
                }


//                String guestName = Utils.setLangValue("numberOfPax",String.valueOf(model.getAdult()),String.valueOf(model.getChild()),String.valueOf(model.getInfant()));
                String guestName = Utils.setLangValue("numberOfPax",String.valueOf(model.getAdult()),model.getAdultTitle(),String.valueOf(model.getChild()),model.getChildTitle(),String.valueOf(model.getInfant()),model.getInfantTitle());
                viewHolder.mBinding.tvPerson.setText(guestName);

                String tourDate = model.getTourDate();

                if (tourDate != null && !tourDate.isEmpty()) {
                    if (tourDate.contains("T")) {
                        try {
                            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
                            isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

                            Date date = isoFormat.parse(tourDate);
                            SimpleDateFormat desiredFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                            String formattedDate = desiredFormat.format(date);

                            viewHolder.mBinding.tvDate.setText(formattedDate);
                        } catch (ParseException e) {
                            viewHolder.mBinding.tvDate.setText(tourDate);
                        }
                    } else {
                        viewHolder.mBinding.tvDate.setText(tourDate);
                    }
                } else {
                    viewHolder.mBinding.tvDate.setText("");
                }
                viewHolder.mBinding.tvTime.setText(model.getTimeSlot());

                if (!TextUtils.isEmpty(model.getPickup())) {
                    viewHolder.mBinding.transferTypeLayout.setVisibility(View.VISIBLE);
                    viewHolder.mBinding.tvTranstype.setText(model.getPickup());
                } else {
                    viewHolder.mBinding.transferTypeLayout.setVisibility(View.GONE);
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


                Utils.setStyledText(activity,viewHolder.mBinding.tvAmount,model.getWhosinTotal());

                if (model.getCustomTicket() == null){
                    viewHolder.mBinding.btnCancellationPolicy.setText(getValue("non_refundable"));
                    viewHolder.mBinding.cancellationPolicyLayout.setBackgroundColor(ContextCompat.getColor(activity, R.color.ticket_non_ref_colour));
                    viewHolder.mBinding.btnCancelBooking.setVisibility(View.GONE);
                }else {
                    if (model.getCustomTicket() != null && !model.getCustomTicket().isFreeCancellation()) {
                        viewHolder.mBinding.btnCancellationPolicy.setText(getValue("non_refundable"));
                        viewHolder.mBinding.cancellationPolicyLayout.setBackgroundColor(ContextCompat.getColor(activity, R.color.ticket_non_ref_colour));
                        viewHolder.mBinding.btnCancelBooking.setVisibility(View.GONE);
                    } else {
                        viewHolder.mBinding.btnCancellationPolicy.setText(getValue("cancellation_policy"));
                        viewHolder.mBinding.cancellationPolicyLayout.setBackgroundColor(ContextCompat.getColor(activity, R.color.cancellation_policy__bg));
                        viewHolder.mBinding.btnCancelBooking.setVisibility(View.VISIBLE);

                    }
                }


                String status = model.getStatus();
                if (status.equals("Cancelled")) {
                    viewHolder.mBinding.bookingStatus.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.mBinding.bookingStatus.setVisibility(View.GONE);
                }



//                viewHolder.mBinding.cancellationPolicyLayout.setOnClickListener(view -> {
//                    if (!model.getCustomTicket().isFreeCancellation()) {
//                       return;
//                    }
//
//                    Utils.preventDoubleClick(view);
//                    CancellationPolicyBottomSheet cancellationPolicyBottomSheet = new CancellationPolicyBottomSheet();
//                    cancellationPolicyBottomSheet.jsonObject = getTravelDeskTicketJsonObject(model);
//                    cancellationPolicyBottomSheet.isTravelDeskTicket = true;
//                    cancellationPolicyBottomSheet.isWhosinTypeTicket = isWhosinBooking;
//                    cancellationPolicyBottomSheet.activity = activity;
////                    cancellationPolicyBottomSheet.policyText = model.getTourOption().getCancellationPolicyDescription();
//                    cancellationPolicyBottomSheet.show(getSupportFragmentManager(), "1");
//                });


                viewHolder.mBinding.btnCancelBooking.setOnClickListener(view -> {
                    String message = "";
//                    String refundAmount = String.valueOf(Utils.roundDoubleValueToDouble(calculateRefundAmount(myWalletModel.getWhosinTicket().getAmount(),policy)));
                    String refundAmount = "";
                    if (TextUtils.isEmpty(refundAmount)) refundAmount = "0.0";
                    String descriptionText = "";
                    if (Objects.equals(refundAmount, "0.0")) {
                        descriptionText = getValue("noRefundText");
                        message = setValue("cancelConfirmationTicket", model.getOptionData().getName(),descriptionText);
                        Graphics.showAlertDialogWithOkCancel(activity, activity.getString(R.string.app_name), message, getValue("yes"), getValue("no"), isConfirmed -> {
                            if (isConfirmed) {
                                requestTravleDeskBookingCancel(model.getBookingId(),myWalletModel.getTraveldeskTicket().getId(),model.getOptionData().getName());
                            }
                        });
                    } else {
                        SpannableString styledPrice = Utils.getStyledText(activity, refundAmount);
                        SpannableStringBuilder fullText = new SpannableStringBuilder()
                                .append(getValue("are_you_sure_cancel"))
                                .append(model.getTourOption().getName())
                                .append("\n")
                                .append(getValue("refund_message"))
                                .append(" ")
                                .append(styledPrice)
                                .append(getValue("customer_support_message"));

                        Graphics.alertDialogYesNoBtnWithUIFlag(activity, "WHOS'IN Business", fullText, false, getValue("no"), getValue("yes"), aBoolean -> {
                            if (aBoolean) {
                                requestTravleDeskBookingCancel(model.getBookingId(),myWalletModel.getTraveldeskTicket().getId(),model.getOptionData().getName());
                            }
                        });
                    }
                });

            });
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final ItemTicketDetailBinding mBinding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemTicketDetailBinding.bind(itemView);
            }

        }


    }

    private class BigBusTourBookingAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = UiUtils.getViewBy(parent, R.layout.item_ticket_detail);
            ViewGroup.LayoutParams params = view.getLayoutParams();
            view.setLayoutParams(params);
            return new ViewHolder(view);

        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

            ViewHolder viewHolder = (ViewHolder) holder;
            RaynaTourDetailModel model = (RaynaTourDetailModel) getItem(position);
            if (model == null) return;
            activity.runOnUiThread(() -> {

                viewHolder.mBinding.totalAmountLabel.setText(getValue("total_amount"));
                viewHolder.mBinding.bookingStatus.setText(getValue("cancelled"));
                viewHolder.mBinding.btnCancellationPolicy.setText(getValue("cancellation_policy"));
                viewHolder.mBinding.btnCancelBooking.setTxtTitle(getValue("cancel_booking"));

                if (model.getOptionData() != null && !TextUtils.isEmpty(model.getOptionData().getTitle())) {
                    viewHolder.mBinding.title.setVisibility(View.VISIBLE);
                    viewHolder.mBinding.title.setText(Utils.notNullString(model.getOptionData().getTitle()));
                } else {
                    viewHolder.mBinding.title.setVisibility(View.GONE);
                }


//                String guestName = model.getAdult() + "x Adult, " + model.getChild() + "x Child, " + model.getInfant() + "x Infant";
//                String guestName = Utils.setLangValue("numberOfPax",String.valueOf(model.getAdult()),String.valueOf(model.getChild()),String.valueOf(model.getInfant()));
                String guestName = Utils.setLangValue("numberOfPax",String.valueOf(model.getAdult()),model.getAdultTitle(),String.valueOf(model.getChild()),model.getChildTitle(),String.valueOf(model.getInfant()),model.getInfantTitle());
                viewHolder.mBinding.tvPerson.setText(guestName);

                String tourDate = model.getTourDate();

                if (tourDate != null && !tourDate.isEmpty()) {
                    if (tourDate.contains("T")) {
                        try {
                            Date date = null;

                            if (tourDate.endsWith("Z")) {
                                // Old format: 2025-10-10T03:00:00.000Z
                                SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",Locale.ENGLISH);
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
                viewHolder.mBinding.tvTime.setText(model.getTimeSlot());



                Utils.setStyledText(activity,viewHolder.mBinding.tvAmount,model.getWhosinTotal());


                String status = model.getStatus();
                if (status.equals("Cancelled")) {
                    viewHolder.mBinding.bookingStatus.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.mBinding.bookingStatus.setVisibility(View.GONE);
                }


                if (!TextUtils.isEmpty(model.getPickup())) {
                    viewHolder.mBinding.transferTypeLayout.setVisibility(View.VISIBLE);
                    viewHolder.mBinding.tvTranstype.setText(model.getPickup());
                } else {
                    viewHolder.mBinding.transferTypeLayout.setVisibility(View.GONE);
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

                List<RaynaTicketDownloadModel> details = myWalletModel.getOctoTicket().getDetails();
                if (!details.isEmpty()){
                    Object objectId = model.getOptionId();
                    String optionId = (objectId instanceof String || objectId instanceof Number) ? String.valueOf(objectId) : "";
                    Optional<RaynaTicketDownloadModel> detailModel = details.stream().filter(p -> optionId.equals(String.valueOf(p.getOptionId()))).findFirst();
                    if (detailModel.isPresent()){
                        if (detailModel.get().isCancellable()){
                            viewHolder.mBinding.btnCancellationPolicy.setText(getValue("cancellation_policy"));
                            viewHolder.mBinding.btnCancellationPolicy.setEnabled(true);
                            viewHolder.mBinding.cancellationPolicyLayout.setBackgroundColor(ContextCompat.getColor(activity, R.color.cancellation_policy__bg));
                            viewHolder.mBinding.btnCancelBooking.setVisibility(isFromHistory ? View.GONE : View.VISIBLE);
                        }else {
                            viewHolder.mBinding.btnCancellationPolicy.setText(getValue("non_refundable"));
                            viewHolder.mBinding.btnCancellationPolicy.setEnabled(false);
                            viewHolder.mBinding.cancellationPolicyLayout.setBackgroundColor(ContextCompat.getColor(activity, R.color.ticket_non_ref_colour));
                            viewHolder.mBinding.btnCancelBooking.setVisibility(View.GONE);
                        }
                    }else {
                        viewHolder.mBinding.cancellationPolicyLayout.setVisibility(View.GONE);
                        viewHolder.mBinding.btnCancelBooking.setVisibility(View.GONE);
                    }
                }else {
                    viewHolder.mBinding.cancellationPolicyLayout.setVisibility(View.GONE);
                    viewHolder.mBinding.btnCancelBooking.setVisibility(View.GONE);
                }


                viewHolder.mBinding.cancellationPolicyLayout.setOnClickListener(view -> {
                    Utils.preventDoubleClick(view);
                    CancellationPolicyBottomSheet cancellationPolicyBottomSheet = new CancellationPolicyBottomSheet();
                    cancellationPolicyBottomSheet.jsonObject = getBigBusTicketJsonObject(model);
                    cancellationPolicyBottomSheet.isFromBigBusTicket = true;
                    cancellationPolicyBottomSheet.isFormViewTicket = true;
                    cancellationPolicyBottomSheet.activity = activity;
                    cancellationPolicyBottomSheet.show(getSupportFragmentManager(), "1");
                });


                viewHolder.mBinding.btnCancelBooking.setOnClickListener(v -> {
                    Utils.preventDoubleClick(v);
                    CancelBookingDialog cancelBookingDialog = new CancelBookingDialog();
                    cancelBookingDialog.isOctaBooking = true;
                    cancelBookingDialog.refundAmount = getValue("refund_full_message");
                    cancelBookingDialog.submitCallback = reason -> {
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.addProperty("cancellationReason", reason);
                        jsonObject.addProperty("bookingId", model.getBookingId());
                        jsonObject.addProperty("bookingType", "octo");
                        jsonObject.addProperty("_id", myWalletModel.getOctoTicket().getId());
                        String title;
                        if (model.getOptionData() != null && !TextUtils.isEmpty(model.getOptionData().getTitle())) {
                            title = model.getOptionData().getTitle();
                        } else {
                            title = "";
                        }
                        requestOctaBookingCancel(jsonObject, title);
                    };
                    cancelBookingDialog.show(getSupportFragmentManager(), "1");
                });

            });
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final ItemTicketDetailBinding mBinding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemTicketDetailBinding.bind(itemView);
            }

        }


    }

    private class HotelRoomListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {


        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_jp_sub_hotel_room));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            ViewHolder viewHolder = (ViewHolder) holder;

            JpHotelRoomModel model = (JpHotelRoomModel) getItem(position);

            if (model == null) return;

            if (!TextUtils.isEmpty(model.getFullNameForWallet())) {
                viewHolder.binding.tvHotelName.setVisibility(View.VISIBLE);
                viewHolder.binding.tvHotelName.setText(model.getFullNameForWallet());
            } else {
                viewHolder.binding.tvHotelName.setVisibility(View.GONE);
            }

            if (model.getRoomOccupancy() != null && !TextUtils.isEmpty(model.getRoomOccupancy().getMaxOccupancy()) && !model.getRoomOccupancy().getMaxOccupancy().equals("0")) {
                viewHolder.binding.tvHotelType.setVisibility(View.VISIBLE);
                String type = setValue("max_occupancy",model.getRoomOccupancy().getMaxOccupancy());
                viewHolder.binding.tvHotelType.setText(type);
            } else {
                viewHolder.binding.tvHotelType.setVisibility(View.GONE);
            }

            viewHolder.binding.infoIcon.setVisibility(model.getFeatures().isEmpty() ? View.GONE : View.VISIBLE);


            viewHolder.binding.infoIcon.setOnClickListener(v -> {
                JPHotelFeaturesSheet jpHotelFeaturesSheet = new JPHotelFeaturesSheet();
                jpHotelFeaturesSheet.activity = activity;
                jpHotelFeaturesSheet.features.addAll(model.getFeatures());
                jpHotelFeaturesSheet.show(getSupportFragmentManager(),"");
            });

            viewHolder.binding.tvHotelAdultChildCount.setVisibility(View.GONE);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final ItemJpSubHotelRoomBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemJpSubHotelRoomBinding.bind(itemView);
            }
        }

    }

    // --------------------------------------
    // endregion
}
