package com.whosin.app.ui.fragment.PromoterCreateEvent;

import android.annotation.SuppressLint;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.whosin.app.R;
import com.whosin.app.comman.AppConstants;
import com.whosin.app.comman.AppExecutors;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.FragmentPromoterEventInfoBinding;
import com.whosin.app.databinding.LayoutUploadedImageDesignBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.PromoterProfileManager;
import com.whosin.app.service.models.ContainerListModel;
import com.whosin.app.service.models.OffersModel;
import com.whosin.app.service.models.PromoterEventModel;
import com.whosin.app.service.models.RatingModel;
import com.whosin.app.service.models.VenueObjectModel;
import com.whosin.app.service.models.VenueTimingModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.Promoter.PromoterCreateEventActivity;
import com.whosin.app.ui.activites.offers.SelectOfferBottomSheet;
import com.whosin.app.ui.fragment.Promoter.PromoterBootmSheets.PromoterCustomEventBottomSheet;
import com.whosin.app.ui.fragment.comman.BaseFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


public class PromoterEventInfoFragment extends BaseFragment {

    private FragmentPromoterEventInfoBinding binding;

    private JsonObject promoterEventObject = PromoterProfileManager.shared.promoterEventObject;

    private PromoterEventModel promoterEventModel = PromoterProfileManager.shared.promoterEventModel;

    private final ImageListsAdapter<RatingModel> imageListsAdapter = new ImageListsAdapter<>();

    private List<VenueObjectModel> venueList = new ArrayList<>();

    private List<OffersModel> offerList = new ArrayList<>();

    private OffersModel offersModel;

    private VenueObjectModel venueObjectModel = null;

    private boolean isVenueEdit = false;

    private String venueId = "";

    private JsonObject customVenueObject = new JsonObject();

    private JsonObject venueObject = new JsonObject();


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    public void initUi(View view) {

        binding = FragmentPromoterEventInfoBinding.bind(view);


        applyTranslations();

        binding.uploadedImagesRecycleView.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.uploadedImagesRecycleView.setAdapter(imageListsAdapter);

        requestPromoterVenues();

        PromoterProfileManager.shared.requestPromoterPaidPass(requireActivity());

        binding.layoutCalender.manager = getChildFragmentManager();
        binding.startEndTimeLayout.manager = getChildFragmentManager();

        binding.layoutCalender.isOpenCalender = true;
        binding.startEndTimeLayout.isOpenTime = true;

        binding.layoutCalender.isCustomVenue = true;


        binding.layoutCalender.setUpdata(false, this);
        binding.startEndTimeLayout.setUpdata(false, this);

        binding.dresscodeLayout.setUpdata(true, this);

        boolean check = PromoterProfileManager.shared.isEventEdit || PromoterProfileManager.shared.isEventSaveToDraft || PromoterProfileManager.shared.isEventRepost;
        if (promoterEventModel != null && check) {

            if (PromoterProfileManager.shared.isEventEdit) {
                promoterEventObject.addProperty("eventId", promoterEventModel.getId());
            }

            if (!promoterEventModel.getEventGallery().isEmpty()) {
                binding.chooseEventGalleryConstraint.setVisibility(View.VISIBLE);
                imageListsAdapter.updateData(promoterEventModel.getEventGallery().stream().map(st -> new RatingModel(null, st)).collect(Collectors.toList()));
            }

            hideAndShowButtons(false);

            PromoterProfileManager.shared.requestPromoterEventInviteUser(promoterEventModel.getId(), requireActivity());

            if (promoterEventModel.getVenueType().equals("custom") && promoterEventModel.getCustomVenue() != null) {
                customVenueObject.addProperty("name", promoterEventModel.getCustomVenue().getName());
                customVenueObject.addProperty("address", promoterEventModel.getCustomVenue().getAddress());
                customVenueObject.addProperty("description", promoterEventModel.getCustomVenue().getDescription());
                customVenueObject.addProperty("image", promoterEventModel.getCustomVenue().getImage());
                if (promoterEventModel.getLatitude() != 0.0) {
                    customVenueObject.addProperty("latitude", promoterEventModel.getLatitude());
                }
                if (promoterEventModel.getLongitude() != 0.0) {
                    customVenueObject.addProperty("longitude", promoterEventModel.getLongitude());
                }

                if (promoterEventModel.getEventGallery().isEmpty()){
                    if (promoterEventModel.getCustomVenue() != null && !TextUtils.isEmpty(promoterEventModel.getCustomVenue().getImage())) {
                        binding.chooseEventGalleryConstraint.setVisibility(View.VISIBLE);
                        imageListsAdapter.addItem(new RatingModel(null,promoterEventModel.getCustomVenue().getImage()));
                    } else if (!TextUtils.isEmpty(promoterEventModel.getImage())) {
                        binding.chooseEventGalleryConstraint.setVisibility(View.VISIBLE);
                        imageListsAdapter.addItem(new RatingModel(null,promoterEventModel.getImage()));
                    }
                }



                binding.venueName.setText(promoterEventModel.getCustomVenue().getName());
                binding.subTitleText.setText(promoterEventModel.getCustomVenue().getAddress());
                Graphics.loadImage(promoterEventModel.getCustomVenue().getImage(), binding.image);

                binding.layoutCalender.isCustomVenue = true;
                binding.layoutOfferDetail.setVisibility(View.GONE);
                binding.selectOfferBtn.setVisibility(View.GONE);

            } else {
                if (promoterEventModel.getVenue() != null && !TextUtils.isEmpty(promoterEventModel.getVenueId())) {
                    binding.selectOfferBtn.setVisibility(View.VISIBLE);
                    binding.layoutCalender.isCustomVenue = false;
                    binding.layoutCalender.venueObjectModel = promoterEventModel.getVenue();
                    isVenueEdit = true;
                    venueId = promoterEventModel.getVenueId();
                    venueObjectModel = promoterEventModel.getVenue();

                    if (promoterEventModel.getEventGallery().isEmpty()){
                        if (!TextUtils.isEmpty(promoterEventModel.getImage())) {
                            binding.chooseEventGalleryConstraint.setVisibility(View.VISIBLE);
                            imageListsAdapter.addItem(new RatingModel(null, promoterEventModel.getImage()));
                        }
                    }


                    if (promoterEventModel.getVenue() != null && !TextUtils.isEmpty(promoterEventModel.getVenue().getName()) && !TextUtils.isEmpty(promoterEventModel.getVenue().getLogo())
                            && !TextUtils.isEmpty(promoterEventModel.getVenue().getAddress())) {
                        binding.venueName.setText(promoterEventModel.getVenue().getName());
                        binding.subTitleText.setText(promoterEventModel.getVenue().getAddress());
                        if (!TextUtils.isEmpty(promoterEventModel.getImage())) {
                            Graphics.loadImage(promoterEventModel.getImage(), binding.image);
                        } else {
                            Graphics.loadImage(promoterEventModel.getVenue().getCover(), binding.image);
                        }

                    } else {
                        Optional<VenueObjectModel> tmpVenueModel = venueList.stream().filter(p -> p.getId().equals(promoterEventModel.getVenueId())).findFirst();
                        if (tmpVenueModel.isPresent()) {
                            binding.venueName.setText(tmpVenueModel.get().getName());
                            binding.subTitleText.setText(tmpVenueModel.get().getAddress());
                            Graphics.loadImage(tmpVenueModel.get().getCover(), binding.image);
                            venueObjectModel = tmpVenueModel.get();
                        }
                    }

                    reqOfferDetails(promoterEventModel.getVenueId());

                }

            }

            if (Utils.isNullOrEmpty(promoterEventModel.getVenueType())) {
                hideAndShowButtons(true);
            }

            @SuppressLint({"NewApi", "LocalSuppress"}) boolean isPastDate = Utils.isDateBeforeToday(promoterEventModel.getDate());

            if (!isPastDate) {
                String date = Utils.changeDateFormat(promoterEventModel.getDate(), AppConstants.DATEFORMAT_SHORT, AppConstants.DATEFORMT_EEE_d_MMM_yyyy);
                binding.layoutCalender.setText(date);
            }


            binding.dresscodeLayout.setText(promoterEventModel.getDressCode());

            binding.editGetEventInformation.setText(promoterEventModel.getDescription());

            binding.startEndTimeLayout.setText(convertTimeRange(promoterEventModel.getStartTime(), promoterEventModel.getEndTime()));
            promoterEventObject.addProperty("startTime", promoterEventModel.getStartTime());
            promoterEventObject.addProperty("endTime", promoterEventModel.getEndTime());

            checkIfDataIsFilled();

            if (PromoterProfileManager.shared.isEventEdit && promoterEventModel.getStatus().equals("in-progress")) {
                String date = Utils.changeDateFormat(promoterEventModel.getDate(), AppConstants.DATEFORMT_EEE_d_MMM_yyyy, AppConstants.DATEFORMAT_SHORT);
                if (Utils.isPastEvent(date, promoterEventModel.getStartTime())) {
                    binding.btnEdit.setVisibility(View.GONE);
                    binding.btnRemove.setVisibility(View.GONE);
                    binding.selectOfferBtn.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public void setListeners() {

        binding.btnCreateCustom.setOnClickListener(v -> {
            Utils.preventDoubleClick(v);
            isVenueEdit = false;
            openCustomSheet();

        });

        binding.btnSelectVenue.setOnClickListener(v -> {
            Utils.preventDoubleClick(v);
            openVenueSheet();
            isVenueEdit = true;
        });

        binding.btnRemove.setOnClickListener(v -> {
            String message = isVenueEdit ? "Are you sure want to remove this venue?" : "Are you sure want to remove this custom venue?";
            Graphics.showAlertDialogWithOkCancel(requireActivity(), getString(R.string.app_name), message,
                    "Yes", "Cancel", aBoolean -> {
                        if (aBoolean) {
                            offersModel = null;
                            binding.selectOfferBtn.setVisibility(View.GONE);
                            binding.layoutOfferDetail.setVisibility(View.GONE);
                            binding.chooseEventGalleryConstraint.setVisibility(View.GONE);
                            imageListsAdapter.updateData(new ArrayList<>());
                            binding.layoutCalender.isCustomVenue = true;
                            binding.layoutCalender.venueObjectModel = null;
                            binding.startEndTimeLayout.isCustomVenue = true;
                            binding.startEndTimeLayout.venueObjectModel = null;
                            PromoterProfileManager.shared.timeSlotModel = null;
                            binding.layoutCalender.setText("");
                            binding.startEndTimeLayout.setText("");
                            AppExecutors.get().mainThread().execute(() -> {
                                hideAndShowButtons(true);
                            });
                            customVenueObject = new JsonObject();
                            venueObject = new JsonObject();
                            venueId = "";
                        }
                    });
        });

        binding.btnEdit.setOnClickListener(v -> {
            if (isVenueEdit) {
                openVenueSheet();
            } else {
                openCustomSheet();
            }
        });

        binding.editGetEventInformation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkIfDataIsFilled();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.selectOfferBtn.setOnClickListener(v -> {
            Utils.preventDoubleClick(v);
            openOfferBottomSheet();
        });

        binding.layoutOfferDetail.setOnClickListener(v -> {
            Utils.preventDoubleClick(v);
            openOfferBottomSheet();
        });

        binding.btnUploadImage.setOnClickListener(view -> {
            Utils.preventDoubleClick(view);
            SelectImageBottomsheet selectImageBottomsheet = new SelectImageBottomsheet();
            if (venueObjectModel != null && !TextUtils.isEmpty(venueObjectModel.getId())){
                selectImageBottomsheet.venueId = venueObjectModel.getId();
            }
            selectImageBottomsheet.callback = data -> {
                imageListsAdapter.addItem(new RatingModel(null,data));
            };
            selectImageBottomsheet.show(getChildFragmentManager(), "1");
        });

    }


    @Override
    public void populateData(boolean getDataFromServer) {

    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_promoter_event_info;
    }

    @Override
    protected Map<View, String> getTranslationMap() {
        Map<View, String> map = new HashMap<>();
        map.put(binding.tvSelectVenueTitle, "select_venue");
        map.put(binding.tvOrTitle, "or");
        map.put(binding.tvCreateCustomTitle, "create_custom");
        map.put(binding.selectOfferTextColor, "select_offer");
        map.put(binding.tvFromTitle, "and_from");
        map.put(binding.tvTillDateTitle, "and_till");
        map.put(binding.chooseEventTv, "choose_event_gallery");
        map.put(binding.title, "event_information");
        map.put(binding.editGetEventInformation, "multiline_description");

        binding.layoutCalender.setHintText(getValue("select_date"));
        binding.layoutCalender.setTitleText(getValue("pick_date"));

        binding.startEndTimeLayout.setHintText(getValue("select_start_and_end_time"));
        binding.startEndTimeLayout.setTitleText(getValue("add_start_and_end_time"));

        binding.dresscodeLayout.setHintText(getValue("lower_dress_code"));
        binding.dresscodeLayout.setTitleText(getValue("specify_dresscode"));

        return map;
    }



    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void hideAndShowButtons(boolean isShowButton) {
        binding.venueDetailLayout.setVisibility(!isShowButton ? View.VISIBLE : View.GONE);
        binding.buttonLayout.setVisibility(isShowButton ? View.VISIBLE : View.GONE);
        checkIfDataIsFilled();
    }

    private void openVenueSheet() {
        SelectVenueBottomSheet venueBottomSheet = new SelectVenueBottomSheet();
        venueBottomSheet.venueList = venueList;
        if (isVenueEdit) {
            venueBottomSheet.venueId = venueId;
        }
        venueBottomSheet.callback = data -> {
            if (data != null) {
                if (isVenueEdit) imageListsAdapter.updateData(new ArrayList<>());
                binding.chooseEventGalleryConstraint.setVisibility(View.VISIBLE);
                hideAndShowButtons(false);
                binding.layoutOfferDetail.setVisibility(View.GONE);
                binding.layoutCalender.venueObjectModel = data;
                binding.startEndTimeLayout.venueObjectModel = data;
                binding.layoutCalender.isCustomVenue = false;
                binding.startEndTimeLayout.isCustomVenue = false;
                PromoterProfileManager.shared.timeSlotModel = null;

                binding.layoutCalender.setText("");
                binding.startEndTimeLayout.setText("");


                venueObjectModel = data;


                binding.subTitleText.setText(data.getAddress());
                Graphics.loadImage(data.getCover(), binding.image);
                binding.venueName.setText(data.getName());
                venueId = data.getId();
                venueObject.addProperty("name", data.getName());
                venueObject.addProperty("address", data.getAddress());
                venueObject.addProperty("logo", data.getCover());
                promoterEventObject.addProperty("image", data.getCover());
                imageListsAdapter.addItem(new RatingModel(null,data.getCover()));

                binding.selectOfferBtn.setVisibility(View.VISIBLE);
                reqOfferDetails(venueObjectModel.getId());

            }
        };
        venueBottomSheet.show(getChildFragmentManager(), "");
    }

    private void openCustomSheet() {
        PromoterCustomEventBottomSheet bottomSheet = new PromoterCustomEventBottomSheet();
        if (!isVenueEdit) {
            if (!customVenueObject.isEmpty()) {
                bottomSheet.isCustomEventEdit = true;
                bottomSheet.object = customVenueObject;
            }
        }
        bottomSheet.callback = data -> {
            if (data != null) {
                hideAndShowButtons(false);
                binding.selectOfferBtn.setVisibility(View.GONE);
                binding.chooseEventGalleryConstraint.setVisibility(View.VISIBLE);
                customVenueObject = data;
                binding.layoutCalender.isCustomVenue = true;
                binding.startEndTimeLayout.isCustomVenue = true;
                binding.venueName.setText(data.get("name").getAsString());
                binding.subTitleText.setText(data.get("address").getAsString());
                Graphics.loadImage(data.get("image").getAsString(), binding.image);
                imageListsAdapter.addItem(new RatingModel(null,data.get("image").getAsString()));

            }
        };
        bottomSheet.show(getChildFragmentManager(), "");
    }

    private static String convertTimeRange(String startTime, String endTime) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mma");

        try {
            Date startDate = _24HourSDF.parse(startTime);
            Date endDate = _24HourSDF.parse(endTime);

            assert startDate != null;
            String startTime12Hour = _12HourSDF.format(startDate).toLowerCase();
            assert endDate != null;
            String endTime12Hour = _12HourSDF.format(endDate).toLowerCase();

            // Remove leading zero if exists
            if (startTime12Hour.startsWith("0")) {
                startTime12Hour = startTime12Hour.substring(1);
            }
            if (endTime12Hour.startsWith("0")) {
                endTime12Hour = endTime12Hour.substring(1);
            }

            return String.format("from %s till %s", startTime12Hour, endTime12Hour);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void openOfferBottomSheet() {
        if (offerList == null) {
            return;
        }
        SelectOfferBottomSheet offerBottomSheet = new SelectOfferBottomSheet();
        if (offersModel != null) {
            offerBottomSheet.offerId = offersModel.getId();
        }
        offerBottomSheet.offerList = offerList;
        offerBottomSheet.callback = data -> {
            if (data != null) {
                binding.layoutOfferDetail.setVisibility(View.VISIBLE);
                offersModel = data;
                setOfferDetail();
            }
        };
        offerBottomSheet.show(getChildFragmentManager(), "SelectOfferBottomSheet");
    }

    private void setOfferDetail() {
        if (offersModel == null) {
            return;
        }
        binding.selectOfferBtn.setVisibility(View.GONE);
        binding.layoutOfferDetail.setVisibility(View.VISIBLE);
        binding.txtTitle.setText(offersModel.getTitle());
        binding.tvDescription.setText(offersModel.getDescription());
        Graphics.loadImage(offersModel.getImage(), binding.imgCover);
        binding.txtDays.setText(offersModel.getDays());
        binding.startDate.setText(Utils.convertMainDateFormat(offersModel.getStartTime()));
        binding.endDate.setText(Utils.convertMainDateFormat(offersModel.getEndTime()));
        binding.txtDate.setText(String.format("%s - %s", Utils.convertMainTimeFormat(offersModel.getStartTime()), Utils.convertMainTimeFormat(offersModel.getEndTime())));
    }

    // endregion
    // --------------------------------------
    // region Public
    // --------------------------------------


    public void saveToDraft() {

        if (PromoterProfileManager.shared.isEventSaveToDraft && !Utils.isNullOrEmpty(promoterEventModel.getId())) {
            promoterEventObject.addProperty("_id", promoterEventModel.getId());
        }

        if (!Utils.isNullOrEmpty(binding.layoutCalender.getText())) {
            promoterEventObject.addProperty("date", Utils.changeDateFormat(binding.layoutCalender.getText(), AppConstants.DATEFORMT_EEE_d_MMM_yyyy, AppConstants.DATEFORMAT_SHORT));
        }

        if (!Utils.isNullOrEmpty(binding.dresscodeLayout.getText())) {
            promoterEventObject.addProperty("dressCode", binding.dresscodeLayout.getText());
        }

        if (!Utils.isNullOrEmpty(binding.editGetEventInformation.getText().toString().trim())) {
            promoterEventObject.addProperty("description", binding.editGetEventInformation.getText().toString().trim());
        }

        if (promoterEventObject.has("venueId")) {
            promoterEventObject.remove("venueId");
            promoterEventObject.remove("venue");
        }

        if (promoterEventObject.has("offerId")) {
            promoterEventObject.remove("offerId");
        }

        if (promoterEventObject.has("customVenue")) {
            promoterEventObject.remove("customVenue");
        }

        if (!Utils.isNullOrEmpty(venueId)) {
            promoterEventObject.addProperty("venueType", "venue");
            promoterEventObject.addProperty("venueId", venueId);
            if (offersModel != null && !TextUtils.isEmpty(offersModel.getId())) {
                promoterEventObject.addProperty("offerId", offersModel.getId());
            }

//            if (venueObjectModel != null){
//                Gson gson = new Gson();
//                String json = gson.toJson(venueObjectModel);
//                promoterEventObject.addProperty("venue",json);
//            }

            VenueObjectModel venue = venueObjectModel;
            if (venue != null) {
                venueObject.addProperty("name", venue.getName());
                venueObject.addProperty("address", venue.getAddress());
                venueObject.addProperty("logo", venue.getLogo());


                List<VenueTimingModel> timing = venue.getTiming();
                if (timing != null && !timing.isEmpty()) {
                    JsonArray timingArray = new JsonArray();
                    timing.forEach(t -> {
                        JsonObject timingObject = new JsonObject();
                        timingObject.addProperty("day", t.getDay());
                        timingObject.addProperty("openingTime", t.getOpeningTime());
                        timingObject.addProperty("closingTime", t.getClosingTime());
                        timingArray.add(timingObject);
                    });
                    venueObject.add("timings", timingArray);
                }


            }

            promoterEventObject.add("venue", venueObject);

        } else {
            if (customVenueObject != null && !customVenueObject.isEmpty()) {
                promoterEventObject.addProperty("venueType", "custom");
                promoterEventObject.add("customVenue", customVenueObject);
            }

        }

        if (imageListsAdapter.getData() != null && !imageListsAdapter.getData().isEmpty()) {
            JsonArray imageArray = new JsonArray();
            for (RatingModel model : imageListsAdapter.getData()) {
                imageArray.add(model.getImage());
            }
            promoterEventObject.add("eventGallery", imageArray);
        }

    }

    public boolean isDataValid() {

        if (binding.buttonLayout.getVisibility() == View.VISIBLE) {
            Toast.makeText(context, getValue("select_venue_or_create_custom"), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (Utils.isNullOrEmpty(binding.layoutCalender.getText())) {
            Toast.makeText(context, getValue("please_select_date"), Toast.LENGTH_SHORT).show();
            return false;
        }

        String date = Utils.changeDateFormat(binding.layoutCalender.getText(), AppConstants.DATEFORMT_EEE_d_MMM_yyyy, AppConstants.DATEFORMAT_SHORT);

        boolean isPastDate = false;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            isPastDate = Utils.isDateBeforeToday(date);
        }

        if (isPastDate) {
            Toast.makeText(context, getValue("please_select_valid_date"), Toast.LENGTH_SHORT).show();
            return false;
        }


        if (Utils.isNullOrEmpty(binding.startEndTimeLayout.getText())) {
            Toast.makeText(context, getValue("please_select_time"), Toast.LENGTH_SHORT).show();
            return false;
        }

        String time = promoterEventObject.get("startTime").getAsString();
        boolean check = PromoterProfileManager.shared.isEventEdit || PromoterProfileManager.shared.isEventRepost;
        if (!check) {
            if (Utils.isPastEvent(date, time)) {
                Toast.makeText(context, getValue("please_select_valid_date_and_time"), Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        if (Utils.isNullOrEmpty(binding.dresscodeLayout.getText())) {
            Toast.makeText(context, getValue("please_enter_dresscode"), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (Utils.isNullOrEmpty(binding.editGetEventInformation.getText().toString().trim())) {
            Toast.makeText(context, getValue("please_enter_event_information"), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (promoterEventObject.has("venueId")) {
            promoterEventObject.remove("venueId");
        }
        if (promoterEventObject.has("latitude")) {
            promoterEventObject.remove("latitude");
        }
        if (promoterEventObject.has("longitude")) {
            promoterEventObject.remove("longitude");
        }

        if (promoterEventObject.has("offerId")) {
            promoterEventObject.remove("offerId");
        }

        if (promoterEventObject.has("customVenue")) {
            promoterEventObject.remove("customVenue");
        }

        if (!Utils.isNullOrEmpty(venueId)) {
            promoterEventObject.addProperty("venueType", "venue");
            promoterEventObject.addProperty("venueId", venueId);
            if (offersModel != null && !TextUtils.isEmpty(offersModel.getId())) {
                promoterEventObject.addProperty("offerId", offersModel.getId());
            }

        } else {
            if (customVenueObject != null) {
                if (customVenueObject.has("latitude")) {
                    promoterEventObject.addProperty("latitude", customVenueObject.get("latitude").getAsDouble());
                    customVenueObject.remove("latitude");
                }
                if (customVenueObject.has("longitude")) {
                    promoterEventObject.addProperty("longitude", customVenueObject.get("longitude").getAsDouble());
                    customVenueObject.remove("longitude");
                }
            }
            promoterEventObject.addProperty("venueType", "custom");
            promoterEventObject.add("customVenue", customVenueObject);


        }


        if (imageListsAdapter.getData() != null && !imageListsAdapter.getData().isEmpty()) {
            JsonArray imageArray = new JsonArray();
            for (RatingModel model : imageListsAdapter.getData()) {
                imageArray.add(model.getImage());
            }
            promoterEventObject.add("eventGallery", imageArray);
        }

        promoterEventObject.addProperty("date", Utils.changeDateFormat(binding.layoutCalender.getText(), AppConstants.DATEFORMT_EEE_d_MMM_yyyy, AppConstants.DATEFORMAT_SHORT));
        promoterEventObject.addProperty("dressCode", binding.dresscodeLayout.getText());
        promoterEventObject.addProperty("description", binding.editGetEventInformation.getText().toString().trim());

        return true;
    }

    public void checkIfDataIsFilled() {
        boolean check = binding.buttonLayout.getVisibility() != View.VISIBLE;

        boolean isFilled = check
                && !Utils.isNullOrEmpty(binding.layoutCalender.getText())
                && !Utils.isNullOrEmpty(binding.startEndTimeLayout.getText())
                && !Utils.isNullOrEmpty(binding.dresscodeLayout.getText())
                && !Utils.isNullOrEmpty(binding.editGetEventInformation.getText().toString().trim());

        PromoterCreateEventActivity activity = (PromoterCreateEventActivity) getActivity();
        if (activity != null) {
            activity.callback.onReceive(isFilled);
        }

    }


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------


    private void requestPromoterVenues() {
        showProgress();
        DataService.shared(requireActivity()).requestPromoterVenues(new RestCallback<ContainerListModel<VenueObjectModel>>(this) {
            @Override
            public void result(ContainerListModel<VenueObjectModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(requireActivity(), error, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (model.data != null) {
                    venueList.addAll(model.data);
                }

            }
        });
    }

    private void reqOfferDetails(String venueId) {

        boolean isEditOrDraftOrRepost = PromoterProfileManager.shared.isEventEdit || PromoterProfileManager.shared.isEventSaveToDraft || PromoterProfileManager.shared.isEventRepost;


        binding.selectOfferBtn.setEnabled(false);
        binding.selectOfferBtn.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.gray_700));
        binding.selectOfferTextColor.setTextColor(getResources().getColor(R.color.white_20));

        showProgress();
        JsonObject object = new JsonObject();
        object.addProperty("venueId", venueId);
        object.addProperty("page", 1);
        object.addProperty("limit", 100);
        object.addProperty("day", "all");
        Log.d("selectOfferBtn", "reqOfferDetails: " + object);
        DataService.shared(requireActivity()).requestOfferList(object, new RestCallback<ContainerListModel<OffersModel>>(this) {
            @Override
            public void result(ContainerListModel<OffersModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                    }
                    return;
                }
                if (model.data != null && !model.data.isEmpty()) {
                    if (isEditOrDraftOrRepost && !TextUtils.isEmpty(promoterEventModel.getOfferId())) {
                        model.data.stream()
                                .filter(p -> p.getId().equals(promoterEventModel.getOfferId()))
                                .findFirst()
                                .ifPresent(offerModel -> {
                                    offersModel = offerModel;
                                    binding.selectOfferBtn.setVisibility(View.GONE);
                                    setOfferDetail();
                                });
                    }
                    offerList = model.data;
                    binding.selectOfferBtn.setEnabled(true);
                    if (getActivity() != null) {
                        binding.selectOfferBtn.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.brand_pink));
                    }
                    binding.selectOfferTextColor.setTextColor(getResources().getColor(R.color.white));
                } else {
                    binding.selectOfferBtn.setEnabled(false);
                }
            }
        });
    }


    // --------------------------------------
    // region Adapter
    // --------------------------------------

    private class ImageListsAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.layout_uploaded_image_design));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            RatingModel model = (RatingModel) getItem(position);

            if (model.getUri() != null) {
                viewHolder.vBinding.imagePicker.setImageURI(model.getUri());
            } else {
                Graphics.loadImageWithFirstLetter(model.getImage(), viewHolder.vBinding.imagePicker, "W");
                viewHolder.vBinding.videoPlayIcon.setVisibility(viewHolder.isVideo(model.getImage()) ? View.VISIBLE : View.GONE);
            }

            viewHolder.vBinding.closeBtn.setOnClickListener(v -> {
                int adapterPosition = viewHolder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    removeItem(position);
                    notifyDataSetChanged();
                }
            });
        }


        public class ViewHolder extends RecyclerView.ViewHolder {

            private final LayoutUploadedImageDesignBinding vBinding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                vBinding = LayoutUploadedImageDesignBinding.bind(itemView);
            }

            private boolean isVideo(String url) {
                return url.endsWith(".mp4") || url.endsWith(".avi") || url.endsWith(".mov");
            }
        }
    }

    // endregion
    // --------------------------------------

}