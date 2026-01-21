package com.whosin.app.ui.fragment.PromoterCreateEvent;

import static com.whosin.app.comman.AppDelegate.activity;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Build;
import android.os.Parcel;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.whosin.app.R;
import com.whosin.app.comman.AppConstants;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.FragmentPromoterInvitesBinding;
import com.whosin.app.databinding.ItemMyCircelsDetailListBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.PromoterProfileManager;
import com.whosin.app.service.models.ContainerListModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.InvitedUserModel;
import com.whosin.app.service.models.PromoterCirclesModel;
import com.whosin.app.service.models.PromoterEventModel;
import com.whosin.app.service.models.PromoterSpecificDateModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.fragment.comman.BaseFragment;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

public class PromoterInvitesFragment extends BaseFragment {

    private FragmentPromoterInvitesBinding binding;

    private final PromoterProfileManager promoterManager = PromoterProfileManager.shared;

    private final JsonObject promoterEventObject = promoterManager.promoterEventObject;

    private final PromoterEventModel promoterEventModel = promoterManager.promoterEventModel;

    private RingUserAdapter<UserDetailModel> ringUserAdapter = new RingUserAdapter<>();

    private boolean isRingData = false;

    private boolean isConfirm = false;
    private boolean isManual = false;

    private String invitedGender = "";

    private String repeat = "";

    private List<String> selectedUserRingList = new ArrayList<>();
    private int totalAvailableSeats = 0;
    public boolean selectAllUsers = false;
    private List<String> selectedDays = new ArrayList<>();
    private String repeatStartDate = "";
    private String repeatEndDate = "";



    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void initUi(View view) {

        binding = FragmentPromoterInvitesBinding.bind(view);

        applyTranslations();

        binding.layoutCustomSpecificDates.activity = requireActivity();
        binding.layoutCustomSpecificDates.weekDays = new ArrayList<>();
        binding.layoutCustomSpecificDates.repeatStartDate = "";
        binding.layoutCustomSpecificDates.repeatEndDate = "";
        binding.layoutCustomSpecificDates.dateList = new ArrayList<>();

        binding.layoutCustomSpecificDates.callbackFormDate = data -> {
            if (data){
                repeat = "specific-dates";
                binding.selectweekDayLayout.setVisibility(View.GONE);
                binding.tvRepeatEvent.setText("Specific-dates");
            }
        };


        binding.layoutCustomSpecificDates.callbackFormHideAndShow = data -> {
          if (data) binding.layoutCustomSpecificDates.setVisibility(View.GONE);
        };

        binding.ringsRecycleviews.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false));
        binding.ringsRecycleviews.setAdapter(ringUserAdapter);

        binding.layoutCustomSpecificDates.fragmentManager = getChildFragmentManager();

        binding.tabLayoutConfirm.addTab(binding.tabLayoutConfirm.newTab().setText(getValue("show_interest")));
        binding.tabLayoutConfirm.addTab(binding.tabLayoutConfirm.newTab().setText(getValue("confirmed")));
        binding.tabLayoutConfirm.selectTab(binding.tabLayoutConfirm.getTabAt(1));

        binding.tabLayoutManual.addTab(binding.tabLayoutManual.newTab().setText("Automatic"));
        binding.tabLayoutManual.addTab(binding.tabLayoutManual.newTab().setText("Manual"));
        binding.tabLayoutManual.selectTab(binding.tabLayoutManual.getTabAt(0));

        promoterEventObject.addProperty("spotCloseAt", promoterEventObject.get("startTime").getAsString());

        binding.radioBoth.setChecked(true);
        invitedGender = "both";
        binding.specificSeatsLayout.setVisibility(View.VISIBLE);
        repeat = "none";

        promoterEventObject.addProperty("category", "none");

        boolean check = promoterManager.isEventEdit || promoterManager.isEventSaveToDraft || promoterManager.isEventRepost;


        requestPromoterMyRingMember();

        if (promoterEventModel != null && check) {

            binding.switchForavailable.setChecked(!promoterEventModel.getType().equals("private"));

            binding.selectGenderLayout.setVisibility(!binding.switchForavailable.isChecked() ? View.GONE : View.VISIBLE);

            totalAvailableSeats = promoterEventModel.getMaxInvitee();


            if (promoterEventModel.isConfirmationRequired()) {
                binding.tabLayoutConfirm.selectTab(binding.tabLayoutConfirm.getTabAt(0));
                isConfirm = true;
            } else {
                binding.tabLayoutConfirm.selectTab(binding.tabLayoutConfirm.getTabAt(1));
                isConfirm = false;
            }
            int visibility = !binding.switchForavailable.isChecked() ? View.VISIBLE : View.GONE;

            setViewVisibility(binding.customCircaleLayout, visibility);

            String gender = promoterEventModel.getInvitedGender();
            invitedGender = gender;

            switch (gender.toLowerCase()) {
                case "male":
                    binding.specificSeatsLayout.setVisibility(View.GONE);
                    binding.radioMale.setChecked(true);
                    break;
                case "female":
                    binding.specificSeatsLayout.setVisibility(View.GONE);
                    binding.radioFemale.setChecked(true);
                    break;
                default:
                    binding.specificSeatsLayout.setVisibility(View.VISIBLE);
                    binding.radioBoth.setChecked(true);
                    binding.maleSeatsCount.setText(String.valueOf(promoterEventModel.getMaleSeats()));
                    binding.femaleSeatsCount.setText(String.valueOf(promoterEventModel.getFemaleSeats()));
                    break;
            }


            repeat = promoterEventModel.getRepeat();
            if (!TextUtils.isEmpty(promoterEventModel.getRepeat())) {
                binding.tvRepeatEvent.setText(String.format("%s%s", promoterEventModel.getRepeat().substring(0, 1).toUpperCase(), promoterEventModel.getRepeat().substring(1).toLowerCase()));
            }

//            if (!TextUtils.isEmpty(repeat) && repeat.equals("specific-date") && !TextUtils.isEmpty(promoterEventModel.getRepeatDate())) {
//                binding.selectDateLayout.setVisibility(View.VISIBLE);
//                binding.tvSelectDateSpecificDate.setText(Utils.changeDateFormat(promoterEventModel.getRepeatDate(), AppConstants.DATEFORMAT_SHORT, "EEE, dd/MM/yyyy"));
//                specificDate = Utils.changeDateFormat(promoterEventModel.getRepeatDate(), "EEE, dd/MM/yyyy", AppConstants.DATEFORMAT_SHORT);
//            } else {
//                binding.selectDateLayout.setVisibility(View.GONE);
//            }

            if (!TextUtils.isEmpty(promoterEventModel.getRepeatStartDate()) && !TextUtils.isEmpty(promoterEventModel.getRepeatEndDate())){
                repeatStartDate = promoterEventModel.getRepeatStartDate();
                repeatEndDate = promoterEventModel.getRepeatEndDate();

                if (!TextUtils.isEmpty(repeat) && !repeat.equalsIgnoreCase("none")){
                    binding.layoutCustomSpecificDates.repeatStartDate = promoterEventModel.getRepeatStartDate();
                    binding.layoutCustomSpecificDates.repeatEndDate = promoterEventModel.getRepeatEndDate();
                }

            }

            if (!TextUtils.isEmpty(repeat) && repeat.equals("weekly") || repeat.equals("daily") || repeat.equals("specific-dates")) {
                binding.layoutCustomSpecificDates.setVisibility(View.VISIBLE);
                binding.tvRepeatEvent.setText(promoterEventModel.getRepeat());
                binding.selectDateLayout.setVisibility(View.VISIBLE);

                if (repeat.equals("weekly")) {
                    binding.selectweekDayLayout.setVisibility(View.VISIBLE);
                    if (promoterEventModel.getRepeatDays() != null && !promoterEventModel.getRepeatDays().isEmpty()){
                        binding.layoutCustomSpecificDates.weekDays = promoterEventModel.getRepeatDays();
                        handleWeekDay(promoterEventModel.getRepeatDays());
                    }
                } else {
                    binding.selectweekDayLayout.setVisibility(View.GONE);
                }

                if (repeat.equalsIgnoreCase("specific-dates")){
                    binding.layoutCustomSpecificDates.dateList = promoterEventModel.getRepeatDatesAndTime();
                }

                if (!TextUtils.isEmpty(repeatStartDate) && !TextUtils.isEmpty(repeatEndDate)){
                    binding.tvSelectDateSpecificDate.setText(String.format("%s To %s", Utils.changeDateFormat(repeatStartDate, "yyyy-MM-dd", "dd MMM yyyy"), Utils.changeDateFormat(repeatEndDate, "yyyy-MM-dd", "dd MMM yyyy")));
                }

                binding.layoutCustomSpecificDates.setData(repeat);
            } else {
                binding.selectDateLayout.setVisibility(View.GONE);
            }


//            if (!TextUtils.isEmpty(repeat) && !repeat.equals("none")) {
//                repetition_count = promoterEventModel.getRepeatCount();
//                binding.repeatCountMainLayout.setVisibility(View.VISIBLE);
//                binding.tvRepeatEventCount.setText(String.valueOf(promoterEventModel.getRepeatCount()));
//            } else {
//                binding.repeatCountMainLayout.setVisibility(View.GONE);
//            }

            if (TextUtils.isEmpty(promoterEventModel.getSpotCloseType())) {
                binding.tabLayoutManual.selectTab(binding.tabLayoutManual.getTabAt(0));
            } else {
                if (promoterEventModel.getSpotCloseType().equals("manual")) {
                    binding.tabLayoutManual.selectTab(binding.tabLayoutManual.getTabAt(1));
                    binding.selectedClosingConstraint.setVisibility(View.VISIBLE);
                    binding.tvSelectTime.setText("Selected Closing Time is " + convertTimeRange(promoterEventModel.getSpotCloseAt()));
                    promoterEventObject.addProperty("spotCloseAt", promoterEventModel.getSpotCloseAt());
                    isManual = true;
                } else {
                    binding.tabLayoutManual.selectTab(binding.tabLayoutManual.getTabAt(0));
                    binding.selectedClosingConstraint.setVisibility(View.GONE);
                    isManual = false;
                    promoterEventObject.addProperty("spotCloseAt", promoterEventModel.getStartTime());

                }
            }

            promoterEventObject.addProperty("category", promoterEventModel.getCategory());
            binding.tvForSelctEventType.setText(promoterEventModel.getCategory());
            if (TextUtils.isEmpty(promoterEventModel.getCategory())) {
                promoterEventObject.addProperty("category", "none");
                binding.tvForSelctEventType.setText("None");
            }


            binding.customCircaleLayout.isPinkBackground = true;
            binding.customCircaleLayout.setUpDataForRing(PromoterProfileManager.shared.promoterProfileModel.getCircles(), requireActivity(), getChildFragmentManager(), true, true);

            if (promoterEventModel.getInvitedCircles() != null && !promoterEventModel.getInvitedCircles().isEmpty()) {
                List<String> invitedCircleIds = promoterEventModel.getInvitedCircles().stream()
                        .map(InvitedUserModel::getId)
                        .collect(Collectors.toList());
                binding.customCircaleLayout.filterIdList.addAll(invitedCircleIds);
            }

            binding.layoutAvailableSport.setText(String.valueOf(promoterEventModel.getMaxInvitee()));

        }else {
            binding.layoutCustomSpecificDates.addDataInModel();
        }


        if (promoterManager.promoterProfileModel != null &&
                promoterManager.promoterProfileModel.getCircles() != null &&
                !promoterManager.promoterProfileModel.getCircles().isEmpty()) {
            binding.customCircaleLayout.isPinkBackground = true;
            binding.customCircaleLayout.setUpDataForRing(promoterManager.promoterProfileModel.getCircles(), requireActivity(), getChildFragmentManager(), true, true);
        }


    }

    @Override
    public void setListeners() {


        binding.selectDateLayout.setOnClickListener(view -> {
            Utils.preventDoubleClick(view);
            String date = promoterEventObject.get("date").getAsString();
            if (!TextUtils.isEmpty(date)){
                DateRangePickerDialog(date);
            }else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                DateRangePickerDialog(sdf.format(new Date()));
            }
        });

        binding.selectweekDayLayout.setOnClickListener(view -> {
            Utils.preventDoubleClick(view);
            SelectDaysBottomSheet bottomSheet = new SelectDaysBottomSheet();
            if (!selectedDays.isEmpty()) bottomSheet.selectedDays.addAll(selectedDays);
            bottomSheet.callback = data -> {
                selectedDays.clear();
                handleWeekDay(data);
            };
            bottomSheet.show(getChildFragmentManager(), "");
        });

        binding.selectedClosingConstraint.setOnClickListener(view -> {
            Utils.preventDoubleClick(view);
            showTimeDialog(promoterEventObject.get("startTime").getAsString(),promoterEventObject.get("endTime").getAsString());
        });

        binding.tabLayoutConfirm.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                switch (position) {
                    case 1:
                        isConfirm = false;
                        break;
                    case 0:
                        isConfirm = true;
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        binding.tabLayoutManual.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                switch (position) {
                    case 0:
                        isManual = false;
                        binding.selectedClosingConstraint.setVisibility(View.GONE);
                        break;
                    case 1:
                        isManual = true;
                        binding.selectedClosingConstraint.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        binding.switchForavailable.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (compoundButton.isPressed()) {
                int visibility = !isChecked ? View.VISIBLE : View.GONE;
                setViewVisibility(binding.customCircaleLayout, visibility);
                if (!isChecked && isRingData) {
                    setViewVisibility(binding.ringTitleLayout, View.VISIBLE);
                    setViewVisibility(binding.ringsRecycleviews, View.VISIBLE);
                    binding.selectGenderLayout.setVisibility(View.GONE);
                } else {
                    setViewVisibility(binding.ringTitleLayout, View.GONE);
                    setViewVisibility(binding.ringsRecycleviews, View.GONE);
                    binding.selectGenderLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        binding.seletAllUser.setOnClickListener(v -> {
            if (ringUserAdapter.getData() != null && !ringUserAdapter.getData().isEmpty()) {
                List<UserDetailModel> userList = ringUserAdapter.getData();
                if (binding.tvSelectAll.getText().toString().equalsIgnoreCase(getValue("select_all"))) {
                    selectAllUsers = true;
                    userList.forEach(p -> {
                        p.setRingUserSelect(true);
                        selectedUserRingList.add(p.getUserId());
                    });
                    binding.tvSelectAll.setText(getValue("deselect_all"));
                } else {
                    selectAllUsers = false;
                    userList.forEach(p -> p.setRingUserSelect(false));
                    selectedUserRingList.clear();
                    binding.tvSelectAll.setText(getValue("select_all"));
                }
                ringUserAdapter.updateData(userList);
            }
        });

        binding.radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.radioMale:
                    binding.specificSeatsLayout.setVisibility(View.GONE);
                    invitedGender = "male";
                    break;
                case R.id.radioFemale:
                    binding.specificSeatsLayout.setVisibility(View.GONE);
                    invitedGender = "female";
                    break;
                case R.id.radioBoth:
                    invitedGender = "both";
                    binding.specificSeatsLayout.setVisibility(View.VISIBLE);
                    break;
            }
        });

        binding.repeatEventSelection.setOnClickListener(v -> {
            Utils.preventDoubleClick(v);
            openRepeatEventSheet();
        });

        binding.selectedEventTypeSelection.setOnClickListener(v -> {
            List<String> titles = new ArrayList<>();
            if (!PromoterProfileManager.shared.eventCustomCategory.isEmpty()){
                titles.addAll(PromoterProfileManager.shared.eventCustomCategory);
            }
            titles.add("Custom");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                showPopupMenu(activity, v, titles);
            }
//            requestPromoterEventGetCustomCategory(data -> {
//                titles.addAll(data);
//
//            });
//            requestPromoterEventGetCustomCategory(titles::addAll);

        });

        binding.checkbox.setOnCheckedChangeListener((compoundButton, b) -> {
            binding.extraGuestLayout.setVisibility(b ? View.VISIBLE : View.GONE);
        });

        binding.layoutAvailableSport.setOnAvailableSpotsTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                totalAvailableSeats = 0;

                if (!text.isEmpty()) {
                    try {
                        totalAvailableSeats = Integer.parseInt(text);
                    } catch (NumberFormatException e) {

                        totalAvailableSeats = Integer.parseInt(binding.layoutAvailableSport.getText().toString());
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        final boolean[] isUpdating = {false};

        binding.maleSeatsCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().isEmpty() && !isUpdating[0]) {
                    int maleSeats = Integer.parseInt(charSequence.toString());

                    isUpdating[0] = true;

                    if (maleSeats > totalAvailableSeats) {
                        binding.maleSeatsCount.setText(String.valueOf(totalAvailableSeats));
                        binding.femaleSeatsCount.setText("0");
                    } else {
                        int femaleSeats = totalAvailableSeats - maleSeats;
                        binding.femaleSeatsCount.setText(String.valueOf(femaleSeats));
                    }

                    isUpdating[0] = false;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        binding.femaleSeatsCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().isEmpty() && !isUpdating[0]) {
                    int femaleSeats = Integer.parseInt(charSequence.toString());

                    isUpdating[0] = true;

                    if (femaleSeats > totalAvailableSeats) {
                        binding.femaleSeatsCount.setText(String.valueOf(totalAvailableSeats));
                        binding.maleSeatsCount.setText("0");
                    } else {
                        int maleSeats = totalAvailableSeats - femaleSeats;
                        binding.maleSeatsCount.setText(String.valueOf(maleSeats));
                    }

                    isUpdating[0] = false;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }

    @Override
    public void populateData(boolean getDataFromServer) {

    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_promoter_invites;
    }


    public void showPopupMenu(Context context, View anchorView, List<String> itemList) {
        // Create a PopupMenu
        PopupMenu popupMenu = new PopupMenu(context, anchorView);

        // Dynamically add items to the menu
        for (int i = 0; i < itemList.size(); i++) {
            // Create a new menu item with a custom layout
            MenuItem menuItem = popupMenu.getMenu().add(0, i, i, itemList.get(i));
            View view = LayoutInflater.from(context).inflate(R.layout.popup_menu_item, null);
            TextView textView = view.findViewById(R.id.popup_item);
            textView.setText(itemList.get(i));
            menuItem.setActionView(view);

        }

        popupMenu.setOnMenuItemClickListener(item -> {
            String selectedItem = itemList.get(item.getItemId());
            if (selectedItem.equalsIgnoreCase("Custom")){
                RequirementsAddDialog dialog = new RequirementsAddDialog();
                dialog.requirementTitle = "Enter custom category type";
                dialog.hintText = "Enter text";
                dialog.isEdit = false;
                dialog.editSting = "";
                dialog.callback = data -> {
                    if (!Utils.isNullOrEmpty(data)) {
                        binding.tvForSelctEventType.setText(data);
                        promoterEventObject.addProperty("category", data);
                    }
                };
                dialog.show(getChildFragmentManager(), "");
            }else {
                binding.tvForSelctEventType.setText(selectedItem);
                promoterEventObject.addProperty("category", selectedItem);
            }

            return true;
        });

        // Apply custom background with rounded corners
        try {
            Field mPopupField = popupMenu.getClass().getDeclaredField("mPopup");
            mPopupField.setAccessible(true);
            Object mPopup = mPopupField.get(popupMenu);
            if (mPopup != null) {
                Method setBackgroundDrawableMethod = mPopup.getClass().getMethod("setBackgroundDrawable", android.graphics.drawable.Drawable.class);
                setBackgroundDrawableMethod.invoke(mPopup, ContextCompat.getDrawable(context, R.drawable.popup_menu_bg));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Show the popup menu
        popupMenu.show();
    }

    @Override
    protected Map<View, String> getTranslationMap() {
        Map<View, String> map = new HashMap<>();

        map.put(binding.title, "event_visibility");
        map.put(binding.tvEventPublicOrPrivate, "toggle_to_make_your_event_public_to_your_ring_or_keep_it_private_to_your_invitees");
        map.put(binding.inviteGenderTitle, "invitee_gender");
        map.put(binding.maleSeatsTitle, "male_seats");
        map.put(binding.maleSeatsCount, "male_seats");

        map.put(binding.femaleSeatsTitle, "female_seats");
        map.put(binding.femaleSeatsCount, "female_seats");

        map.put(binding.tvTitle, "confirmation_required");
        map.put(binding.repeatEventTitle, "repeat_event");

        map.put(binding.tvSelectAll, "select_all");


        binding.layoutAvailableSport.setHintText(getValue("five_spots"));
        binding.layoutAvailableSport.setTitleText(getValue("available_spots"));

        return map;
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void setViewVisibility(View view, int visibility) {
        view.setVisibility(visibility);
    }

    private void setSelectUserInvitedForEditEvent() {
        if (promoterEventModel == null) {
            return;
        }
        if (!binding.switchForavailable.isChecked() && isRingData) {
            setViewVisibility(binding.ringTitleLayout, View.VISIBLE);
            setViewVisibility(binding.ringsRecycleviews, View.VISIBLE);

            if (promoterManager.invitedUserList != null && !promoterManager.invitedUserList.isEmpty()) {
                if (ringUserAdapter.getData() != null && !ringUserAdapter.getData().isEmpty()) {
                    ringUserAdapter.getData().forEach(user -> {
                        if (promoterManager.invitedUserList.contains(user.getUserId())) {
                            user.setRingUserSelect(true);
                            selectedUserRingList.add(user.getUserId());
                        }
                    });
                    ringUserAdapter.notifyDataSetChanged();

                    if (promoterManager.invitedUserList.size() == ringUserAdapter.getData().size()) {
                        binding.tvSelectAll.setText(getValue("deselect_all"));
                    } else {
                        binding.tvSelectAll.setText(getValue("select_all"));
                    }
                }
            }

        } else {
            setViewVisibility(binding.ringTitleLayout, View.GONE);
            setViewVisibility(binding.ringsRecycleviews, View.GONE);
        }


    }

    private void addToEventObject(List<String> requirementList, String key) {
        JsonArray jsonArray = new JsonArray();
        if (requirementList != null && !requirementList.isEmpty()) {
            requirementList.forEach(jsonArray::add);
        }
        promoterEventObject.add(key, jsonArray);
    }

    private void openRepeatEventSheet() {
        ArrayList<String> data = new ArrayList<>();
        data.add("None");
        data.add("Daily");
        data.add("Weekly");
        data.add("Specific-dates");

        Map<Integer, String> repeatOptions = new HashMap<>();
        repeatOptions.put(0, "none");
        repeatOptions.put(1, "daily");
        repeatOptions.put(2, "weekly");
        repeatOptions.put(3, "specific-dates");

        Graphics.showActionSheet(requireActivity(), "Repeat event", data, (data1, position) -> {
            repeat = repeatOptions.get(position).toLowerCase();
            String displayText = repeatOptions.get(position).substring(0, 1).toUpperCase() + repeatOptions.get(position).substring(1).toLowerCase();
            binding.tvRepeatEvent.setText(displayText);

            if (position == 0) {
                binding.selectDateLayout.setVisibility(View.GONE);
            } else {
                binding.selectDateLayout.setVisibility(View.VISIBLE);
            }

            if (position == 2) {
                binding.selectweekDayLayout.setVisibility(View.VISIBLE);
            } else {
                binding.selectweekDayLayout.setVisibility(View.GONE);
            }

            updateDateSummary();

        });
    }

    private void DateRangePickerDialog(String disableBeforeDate) {
        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText("Select a date range");

        // Set date range constraints
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date disabledDate = sdf.parse(disableBeforeDate);

            if (disabledDate != null) {
                long disabledTime = disabledDate.getTime();
                constraintsBuilder.setValidator(new CalendarConstraints.DateValidator() {
                    @Override
                    public boolean isValid(long date) {
                        return date > disabledTime;
                    }

                    @Override
                    public int describeContents() {
                        return 0;
                    }

                    @Override
                    public void writeToParcel(Parcel dest, int flags) {
                    }
                });
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        builder.setCalendarConstraints(constraintsBuilder.build());
        builder.setTheme(R.style.CustomDatePickerTheme);

        // Convert stored string date (yyyy-MM-dd) to Long for the date range picker
        if (repeatStartDate != null && repeatEndDate != null) {
            try {
                SimpleDateFormat sdfDisplay = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                sdfDisplay.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date startDate = sdfDisplay.parse(repeatStartDate);
                Date endDate = sdfDisplay.parse(repeatEndDate);

                if (startDate != null && endDate != null) {
                    // Set the date range picker selection (in milliseconds)
                    builder.setSelection(new Pair<>(startDate.getTime(), endDate.getTime()));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        MaterialDatePicker<Pair<Long, Long>> datePicker = builder.build();
        datePicker.addOnPositiveButtonClickListener(selection -> {
            Long startDate = selection.first;
            Long endDate = selection.second;

            // Format Long values back to String format "yyyy-MM-dd"
            SimpleDateFormat sdfDisplay = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            sdfDisplay.setTimeZone(TimeZone.getTimeZone("UTC"));

            repeatStartDate = sdfDisplay.format(new Date(startDate));
            repeatEndDate = sdfDisplay.format(new Date(endDate));

            binding.tvSelectDateSpecificDate.setText(
                    Utils.changeDateFormat(repeatStartDate, "yyyy-MM-dd", "dd MMM yyyy") + " To " +
                            Utils.changeDateFormat(repeatEndDate, "yyyy-MM-dd", "dd MMM yyyy"));

            updateDateSummary();
        });

        datePicker.show(getParentFragmentManager(), "DATE_PICKER");
    }

    private void handleWeekDay(List<String> dayString) {
        selectedDays.addAll(dayString);

        if (isWeekend(selectedDays)) {
            binding.tvSelectedWeekDay.setText("Weekend");
        } else if (isWeekdays(selectedDays)) {
            binding.tvSelectedWeekDay.setText("Weekdays");
        } else if (isAlldays(selectedDays)) {
            binding.tvSelectedWeekDay.setText("All days");
        } else {
            String formattedDays = selectedDays.stream()
                    .map(day -> day.substring(0, 1).toUpperCase() + day.substring(1).toLowerCase())
                    .collect(Collectors.joining(", "));
            binding.tvSelectedWeekDay.setText(formattedDays);
        }

        updateDateSummary();

    }

    private boolean isWeekend(List<String> days) {
        return days.size() == 2 && days.contains("sunday") && days.contains("saturday");
    }

    private boolean isWeekdays(List<String> days) {
        return new HashSet<>(days).containsAll(List.of("monday", "tuesday", "wednesday", "thursday", "friday"))
                && !days.contains("saturday") && !days.contains("sunday");
    }
    private boolean isAlldays(List<String> days) {
        return new HashSet<>(days).containsAll(List.of("monday", "tuesday", "wednesday", "thursday", "friday","saturday","sunday"));
    }

    @SuppressLint("NewApi")
    private void updateDateSummary(){
        if (repeat.equalsIgnoreCase("none") || (TextUtils.isEmpty(repeatStartDate) && TextUtils.isEmpty(repeatEndDate))) {
            binding.layoutCustomSpecificDates.setVisibility(View.GONE);
        } else {
            binding.layoutCustomSpecificDates.setVisibility(View.VISIBLE);
        }

        if (repeat.equalsIgnoreCase("weekly")) {
            binding.layoutCustomSpecificDates.weekDays = selectedDays;

        } else {
            binding.layoutCustomSpecificDates.weekDays = new ArrayList<>();
        }
        binding.layoutCustomSpecificDates.repeatEndDate = repeatEndDate;
        binding.layoutCustomSpecificDates.repeatStartDate = repeatStartDate;
        binding.layoutCustomSpecificDates.setData(repeat);
    }


    private void changeButtonTitle() {
        int selectedSize = (selectedUserRingList != null) ? selectedUserRingList.size() : 0;
        int adapterSize = (ringUserAdapter.getData() != null) ? ringUserAdapter.getData().size() : 0;

        if (selectedSize == adapterSize) {
            binding.tvSelectAll.setText(getValue("deselect_all"));
        } else {
            binding.tvSelectAll.setText(getValue("select_all"));
        }
    }

    private static String convertTimeRange(String startTime) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);

        try {
            Date startDate = _24HourSDF.parse(startTime);

            if (startDate != null) {
                String startTime12Hour = _12HourSDF.format(startDate);
                return startTime12Hour; // Returns in format like "5:45 PM" or "10:12 AM"
            } else {
                return null;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressLint("NewApi")
    private void showTimeDialog(String startTime, String endTime) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm", Locale.ENGLISH);

        Calendar startCalendar = Calendar.getInstance();
        Calendar endCalendar = Calendar.getInstance();

        try {
            startCalendar.setTime(timeFormat.parse(startTime));
            endCalendar.setTime(timeFormat.parse(endTime));
        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }

        // Extract hour, minute, and AM/PM values for 12-hour format
        int startHour = startCalendar.get(Calendar.HOUR) == 0 ? 12 : startCalendar.get(Calendar.HOUR);
        int startMinute = startCalendar.get(Calendar.MINUTE);
        String startAmPm = startCalendar.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM";

        int endHour = endCalendar.get(Calendar.HOUR) == 0 ? 12 : endCalendar.get(Calendar.HOUR);
        int endMinute = endCalendar.get(Calendar.MINUTE);
        String endAmPm = endCalendar.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM";

        // Pass extracted times and AM/PM to openTimeDialog
        openTimeDialog(startHour, startMinute, startAmPm, endHour, endMinute, endAmPm);
    }


    private void openTimeDialog(int startHour, int startMinute, String startAmPm, int endHour, int endMinute, String endAmPm) {
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);

        // Create Calendar instances for the start and end times
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.set(Calendar.HOUR, startHour);
        startCalendar.set(Calendar.MINUTE, startMinute);
        startCalendar.set(Calendar.AM_PM, startAmPm.equals("AM") ? Calendar.AM : Calendar.PM);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.set(Calendar.HOUR, endHour);
        endCalendar.set(Calendar.MINUTE, endMinute);
        endCalendar.set(Calendar.AM_PM, endAmPm.equals("AM") ? Calendar.AM : Calendar.PM);

        // Handle the case where the end time is before the start time (crosses midnight)
        if (endCalendar.before(startCalendar)) {
            endCalendar.add(Calendar.DATE, 1); // Add one day to the end time
        }

        // Format the start and end times for display in the toast
        String formattedStartTime = String.format(Locale.ENGLISH, "%02d:%02d %s", startHour, startMinute, startAmPm);
        String formattedEndTime = String.format(Locale.ENGLISH, "%02d:%02d %s", endHour, endMinute, endAmPm);

        // Open the TimePickerDialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), R.style.OrangeDialogTheme, (view, selectedHour, selectedMinute) -> {
            // Create a Calendar instance for the selected time
            Calendar selectedCalendar = Calendar.getInstance();
            selectedCalendar.set(Calendar.HOUR, selectedHour % 12 == 0 ? 12 : selectedHour % 12);  // Adjust for 12-hour format
            selectedCalendar.set(Calendar.MINUTE, selectedMinute);
            selectedCalendar.set(Calendar.AM_PM, selectedHour < 12 ? Calendar.AM : Calendar.PM);

            // Handle cross-midnight case: if the selected time is after the end time, add 1 day
            if (selectedCalendar.before(startCalendar) || selectedCalendar.after(endCalendar)) {
                Toast.makeText(getContext(), "Please select a time within the range: " + formattedStartTime + " to " + formattedEndTime, Toast.LENGTH_SHORT).show();
                openTimeDialog(startHour, startMinute, startAmPm, endHour, endMinute, endAmPm);
                return;
            }

            // Format and set the selected time
            String selectedTime = String.format(Locale.ENGLISH, "%02d:%02d %s", selectedHour % 12 == 0 ? 12 : selectedHour % 12, selectedMinute, selectedHour < 12 ? "AM" : "PM");
            binding.tvSelectTime.setText("Selected Closing Time is " + selectedTime);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                try {
                    // Parse the 12-hour format time string
                    SimpleDateFormat twelveHourFormat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
                    SimpleDateFormat twentyFourHourFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);

                    // Convert to 24-hour format
                    String timeIn24HourFormat = twentyFourHourFormat.format(twelveHourFormat.parse(selectedTime));
                    promoterEventObject.addProperty("spotCloseAt", timeIn24HourFormat);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

        }, currentHour, currentMinute, false);

        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();
    }




    // endregion
    // --------------------------------------
    // region Public
    // --------------------------------------


    public boolean isDataValid() {

        if (Utils.isNullOrEmpty(binding.layoutAvailableSport.getText())) {
            Toast.makeText(requireActivity(), "Please enter available spots", Toast.LENGTH_SHORT).show();
            return false;
        }

        int tmpValue = Integer.parseInt(binding.layoutAvailableSport.getText());
        if (tmpValue == 0) {
            Toast.makeText(requireActivity(), getValue("valid_spot_available_alert"), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (binding.switchForavailable.isChecked() && invitedGender.equals("both")) {
            if (Utils.isNullOrEmpty(binding.maleSeatsCount.getText().toString())) {
                Toast.makeText(requireActivity(), getValue("enter_number_available_seats"), Toast.LENGTH_SHORT).show();
                return false;
            }

            if (Utils.isNullOrEmpty(binding.femaleSeatsCount.getText().toString())) {
                Toast.makeText(requireActivity(), getValue("enter_number_available_female_seats"), Toast.LENGTH_SHORT).show();
                return false;
            }

            int availableSportCount = Integer.parseInt(binding.layoutAvailableSport.getText());
            int maleSetasCount = Integer.parseInt(binding.maleSeatsCount.getText().toString());
            int femaleSetaCount = Integer.parseInt(binding.femaleSeatsCount.getText().toString());

            if (availableSportCount != (maleSetasCount + femaleSetaCount)) {
                Toast.makeText(requireActivity(), getValue("total_male_female_seats"), Toast.LENGTH_SHORT).show();
                return false;
            }

        }


        if (repeat.equals("weekly") || repeat.equals("daily") || repeat.equals("specific-dates")) {

            if (Utils.isNullOrEmpty(repeatStartDate) && Utils.isNullOrEmpty(repeatEndDate)) {
                Toast.makeText(requireActivity(), getValue("select_repetition_date_range"), Toast.LENGTH_SHORT).show();
                return false;
            }

            if (repeat.equals("weekly")) {
                if (selectedDays.isEmpty()) {
                    Toast.makeText(requireActivity(), getValue("select_weekly_repetition_days"), Toast.LENGTH_SHORT).show();
                    return false;
                }
            }

            if (repeat.equals("specific-dates")){
                List<PromoterSpecificDateModel> tmpList = binding.layoutCustomSpecificDates.getSelectedDateList();
                if (tmpList.isEmpty()){
                    Toast.makeText(requireActivity(), getValue("error_invalid_dates_times"), Toast.LENGTH_SHORT).show();
                    return false;
                }

                for (PromoterSpecificDateModel tmpModel : tmpList){
                    if (TextUtils.isEmpty(tmpModel.getDate())){
                        Toast.makeText(requireActivity(), getValue("error_invalid_dates_times"), Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    if (TextUtils.isEmpty(tmpModel.getStartTime()) && TextUtils.isEmpty(tmpModel.getEndTime())){
                        Toast.makeText(requireActivity(), getValue("error_invalid_dates_times"), Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }
            }
        }

        if (isManual && TextUtils.isEmpty(binding.tvSelectTime.getText().toString())){
            Toast.makeText(requireActivity(), getValue("event_closing_time"), Toast.LENGTH_SHORT).show();
            return false;
        }


        if (!binding.switchForavailable.isChecked()) {

            if (selectedUserRingList.isEmpty()){
                if (binding.customCircaleLayout.filterIdList.isEmpty()){
                    Toast.makeText(requireActivity(), getValue("invite_user_or_circle_required"), Toast.LENGTH_SHORT).show();
                    return false;
                }
            }

//            if (binding.customCircaleLayout.filterIdList.isEmpty() || selectedUserRingList.isEmpty()){
//                Toast.makeText(requireActivity(), "Please invite at least one user or circle to create a private event.", Toast.LENGTH_SHORT).show();
//                return false;
//            }

            addToEventObject(binding.customCircaleLayout.filterIdList, "invitedCircles");
            addToEventObject(selectedUserRingList, "invitedUser");

            if (promoterEventObject.has("invitedGender")) {
                promoterEventObject.remove("invitedGender");
            }

            if (promoterEventObject.has("femaleSeats")) promoterEventObject.remove("femaleSeats");
            if (promoterEventObject.has("maleSeats")) promoterEventObject.remove("maleSeats");


        } else {
            if (promoterEventObject.has("invitedCircles")) {
                promoterEventObject.remove("invitedCircles");
            }
            if (promoterEventObject.has("invitedUser")) {
                promoterEventObject.remove("invitedUser");
            }

            promoterEventObject.addProperty("invitedGender", invitedGender);

            String femaleSeatsText = binding.femaleSeatsCount.getText().toString();
            String maleSeatsText = binding.maleSeatsCount.getText().toString();

            int femaleSeats = TextUtils.isEmpty(femaleSeatsText) ? 0 : Integer.parseInt(femaleSeatsText);
            int maleSeats = TextUtils.isEmpty(maleSeatsText) ? 0 : Integer.parseInt(maleSeatsText);

            promoterEventObject.addProperty("femaleSeats", femaleSeats);
            promoterEventObject.addProperty("maleSeats", maleSeats);

        }

        promoterEventObject.addProperty("maxInvitee", Integer.parseInt(binding.layoutAvailableSport.getText()));
        String eventType = !binding.switchForavailable.isChecked() ? "private" : "public";
        promoterEventObject.addProperty("type", eventType);
        promoterEventObject.addProperty("isConfirmationRequired", isConfirm);
        if (TextUtils.isEmpty(repeat)) repeat = "none";
        promoterEventObject.addProperty("repeat", repeat);

        promoterEventObject.addProperty("repeatStartDate", repeatStartDate);
        promoterEventObject.addProperty("repeatEndDate", repeatEndDate);

        if (repeat.equals("daily") || repeat.equals("weekly")) {
            if (repeat.equals("weekly")) {
                JsonArray repeatDaysArray = new JsonArray();
                selectedDays.forEach(repeatDaysArray::add);
                promoterEventObject.add("repeatDays", repeatDaysArray);
            } else {
                if (promoterEventObject.has("repeatDays")) promoterEventObject.remove("repeatDays");
            }
            if (promoterEventObject.has("repeatDatesAndTime")) promoterEventObject.remove("repeatDatesAndTime");
        } else if (repeat.equalsIgnoreCase("specific-dates")) {
            List<PromoterSpecificDateModel> tmpList = binding.layoutCustomSpecificDates.getSelectedDateList();
            JsonArray jsonArray = new JsonArray();
            for (PromoterSpecificDateModel tmpModel : tmpList){
                JsonObject object = new JsonObject();
                object.addProperty("date", Utils.changeDateFormat(tmpModel.getDate(),AppConstants.DATEFORMT_EEE_d_MMM_yyyy, AppConstants.DATEFORMAT_SHORT));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    object.addProperty("startTime", Utils.convertTo24HourFormat(tmpModel.getStartTime()));
                    object.addProperty("endTime", Utils.convertTo24HourFormat(tmpModel.getEndTime()));

                }
                jsonArray.add(object);
            }

            promoterEventObject.add("repeatDatesAndTime", jsonArray);

        } else {
            if (promoterEventObject.has("repeatStartDate"))
                promoterEventObject.remove("repeatStartDate");
            if (promoterEventObject.has("repeatEndDate"))
                promoterEventObject.remove("repeatEndDate");
            if (promoterEventObject.has("repeatDays")) promoterEventObject.remove("repeatDays");
            if (promoterEventObject.has("repeatDatesAndTime")) promoterEventObject.remove("repeatDatesAndTime");
        }

//        if (!TextUtils.isEmpty(specificDate)) {
//            promoterEventObject.addProperty("repeatDate", specificDate);
//        } else {
//            promoterEventObject.addProperty("repeatDate", "");
//        }

        promoterEventObject.addProperty("selectAllCircles", binding.customCircaleLayout.selectAllCircles);
        promoterEventObject.addProperty("selectAllUsers",selectAllUsers);

        if (isManual){
            promoterEventObject.addProperty("spotCloseType","manual");
        }else {
            boolean check = promoterManager.isEventEdit || promoterManager.isEventSaveToDraft || promoterManager.isEventRepost;
            if (check) {
                promoterEventObject.addProperty("spotCloseAt",  promoterEventObject.get("startTime").getAsString());
            } else {
                promoterEventObject.addProperty("spotCloseAt", promoterEventObject.get("startTime").getAsString());
            }
            promoterEventObject.addProperty("spotCloseType","auto");

        }



        return true;
    }

    public void saveToDraft() {

        if (!Utils.isNullOrEmpty(binding.layoutAvailableSport.getText())) {
            promoterEventObject.addProperty("maxInvitee", Integer.parseInt(binding.layoutAvailableSport.getText()));
        }

        if (!binding.switchForavailable.isChecked()) {
            // Store matching circles
            if (!binding.customCircaleLayout.filterIdList.isEmpty()) {
                List<PromoterCirclesModel> matchingCircles = PromoterProfileManager.shared.promoterProfileModel.getCircles().stream()
                        .filter(circle -> binding.customCircaleLayout.filterIdList.contains(circle.getId()))
                        .collect(Collectors.toList());
                promoterEventObject.add("invitedCircles", new Gson().toJsonTree(matchingCircles).getAsJsonArray());
            }

            // Store selected ring users
            List<UserDetailModel> selectedRingUserList = ringUserAdapter.getData().stream()
                    .filter(UserDetailModel::isRingUserSelect)
                    .collect(Collectors.toList());
            List<InvitedUserModel> invitedUserList = selectedRingUserList.stream()
                    .map(userDetailModel -> {
                        InvitedUserModel invitedUser = new InvitedUserModel();
                        invitedUser.setUserId(userDetailModel.getUserId());
                        return invitedUser;
                    })
                    .collect(Collectors.toList());

            if (!invitedUserList.isEmpty()) {
                promoterEventObject.add("invitedUsers", new Gson().toJsonTree(invitedUserList).getAsJsonArray());
            }

            if (promoterEventObject.has("invitedGender")) {
                promoterEventObject.remove("invitedGender");
            }

        } else {
            if (promoterEventObject.has("invitedCircles")) {
                promoterEventObject.remove("invitedCircles");
            }
            if (promoterEventObject.has("invitedUser")) {
                promoterEventObject.remove("invitedUser");
            }

            promoterEventObject.addProperty("invitedGender", invitedGender);



            String femaleSeatsText = binding.femaleSeatsCount.getText().toString();
            String maleSeatsText = binding.maleSeatsCount.getText().toString();

            int femaleSeats = TextUtils.isEmpty(femaleSeatsText) ? 0 : Integer.parseInt(femaleSeatsText);
            int maleSeats = TextUtils.isEmpty(maleSeatsText) ? 0 : Integer.parseInt(maleSeatsText);

            promoterEventObject.addProperty("femaleSeats", femaleSeats);
            promoterEventObject.addProperty("maleSeats", maleSeats);

        }

        String eventType = !binding.switchForavailable.isChecked() ? "private" : "public";
        promoterEventObject.addProperty("type", eventType);
        promoterEventObject.addProperty("isConfirmationRequired", isConfirm);

        promoterEventObject.addProperty("repeat", repeat);
        promoterEventObject.addProperty("repeatStartDate", repeatStartDate);
        promoterEventObject.addProperty("repeatEndDate", repeatEndDate);

        if (repeat.equals("daily") || repeat.equals("weekly")) {
            if (repeat.equals("weekly")) {
                JsonArray repeatDaysArray = new JsonArray();
                selectedDays.forEach(repeatDaysArray::add);
                promoterEventObject.add("repeatDays", repeatDaysArray);
            } else {
                if (promoterEventObject.has("repeatDays")) promoterEventObject.remove("repeatDays");
            }
            if (promoterEventObject.has("repeatDatesAndTime")) promoterEventObject.remove("repeatDatesAndTime");
        } else if (repeat.equalsIgnoreCase("specific-dates")) {
            List<PromoterSpecificDateModel> tmpList = binding.layoutCustomSpecificDates.getSelectedDateList();
            JsonArray jsonArray = new JsonArray();
            for (PromoterSpecificDateModel tmpModel : tmpList){
                JsonObject object = new JsonObject();
                object.addProperty("date", Utils.changeDateFormat(tmpModel.getDate(),AppConstants.DATEFORMT_EEE_d_MMM_yyyy, AppConstants.DATEFORMAT_SHORT));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    object.addProperty("startTime", Utils.convertTo24HourFormat(tmpModel.getStartTime()));
                    object.addProperty("endTime", Utils.convertTo24HourFormat(tmpModel.getEndTime()));

                }
                jsonArray.add(object);
            }

            promoterEventObject.add("repeatDatesAndTime", jsonArray);

        } else {
            if (promoterEventObject.has("repeatStartDate"))
                promoterEventObject.remove("repeatStartDate");
            if (promoterEventObject.has("repeatEndDate"))
                promoterEventObject.remove("repeatEndDate");
            if (promoterEventObject.has("repeatDays")) promoterEventObject.remove("repeatDays");
            if (promoterEventObject.has("repeatDatesAndTime")) promoterEventObject.remove("repeatDatesAndTime");
        }


        String layoutAvailableSportString = binding.layoutAvailableSport.getText();
        int layoutAvailableSport = TextUtils.isEmpty(layoutAvailableSportString) ? 0 : Integer.parseInt(layoutAvailableSportString);
        promoterEventObject.addProperty("maxInvitee", layoutAvailableSport);

    }
    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestPromoterMyRingMember() {
        showProgress();
        DataService.shared(activity).requestPromoterMyRingMember(new RestCallback<ContainerListModel<UserDetailModel>>(this) {
            @Override
            public void result(ContainerListModel<UserDetailModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (model.data != null && !model.data.isEmpty()) {
                    if (!binding.switchForavailable.isChecked()) {
                        setViewVisibility(binding.ringTitleLayout, View.VISIBLE);
                        setViewVisibility(binding.ringsRecycleviews, View.VISIBLE);
                    }
                    ringUserAdapter.updateData(model.data);
                    isRingData = true;
                    boolean check = promoterManager.isEventEdit || promoterManager.isEventSaveToDraft || promoterManager.isEventRepost;
                    if (check) {
                        setSelectUserInvitedForEditEvent();
                    }
                } else {
                    binding.ringTitleLayout.setVisibility(View.GONE);
                    binding.ringsRecycleviews.setVisibility(View.GONE);
                }
            }
        });
    }

    private void requestPromoterEventGetCustomCategory(CommanCallback<List<String>> callback) {
        DataService.shared(requireActivity()).requestPromoterEventGetCustomCategory(new RestCallback<ContainerModel<List<String>>>(this) {
            @Override
            public void result(ContainerModel<List<String>> model, String error) {
                if (model.data != null && !model.data.isEmpty()){
                    activity.runOnUiThread(() -> callback.onReceive(model.data));
                }else {
                    callback.onReceive(new ArrayList<>());
                }
            }
        });
    }


    // --------------------------------------
    // region Adapter
    // --------------------------------------


    private class RingUserAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_my_circels_detail_list));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            ViewHolder viewHolder = (ViewHolder) holder;
            UserDetailModel model = (UserDetailModel) getItem(position);
            Graphics.loadImageWithFirstLetter(model.getImage(), viewHolder.binding.image, model.getFullName());
            viewHolder.binding.tvName.setText(model.getFullName());
            viewHolder.binding.ivMenu.setVisibility(View.GONE);
            viewHolder.binding.emailTv.setVisibility(View.GONE);
            viewHolder.binding.ivCheck.setVisibility(View.VISIBLE);
            if (activity != null) {
                viewHolder.binding.getRoot().setBackgroundColor(ContextCompat.getColor(activity, R.color.black));
            }

            viewHolder.binding.ivCheck.setOnCheckedChangeListener(null);

            viewHolder.binding.ivCheck.setChecked(model.isRingUserSelect());
            viewHolder.binding.ivCheck.setButtonDrawable(model.isRingUserSelect() ? R.drawable.promoter_select_check_icon : R.drawable.promoter_deselect_check_icon);


            viewHolder.binding.getRoot().setOnClickListener(v -> {

                boolean isCheck = !model.isRingUserSelect();

                viewHolder.binding.ivCheck.setChecked(isCheck);


                model.setRingUserSelect(isCheck);

                if (isCheck) {
                    if (!selectedUserRingList.contains(model.getUserId())) {
                        selectedUserRingList.add(model.getUserId());
                    }
                } else {
                    selectedUserRingList.remove(model.getUserId());
                }


                changeButtonTitle();

                notifyDataSetChanged();

            });

            viewHolder.binding.ivCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
                model.setRingUserSelect(isChecked);
                if (isChecked) {
                    selectedUserRingList.add(model.getUserId());
                } else {
                    selectedUserRingList.remove(model.getUserId());
                }
                changeButtonTitle();

                viewHolder.binding.ivCheck.post(() -> notifyDataSetChanged());
            });


        }

        private class ViewHolder extends RecyclerView.ViewHolder {

            private final ItemMyCircelsDetailListBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemMyCircelsDetailListBinding.bind(itemView);
            }
        }
    }

    // endregion
    // --------------------------------------
}