package com.whosin.app.ui.activites.home.activity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Paint;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.ActivityYourOrderBinding;
import com.whosin.app.databinding.ItemActivitySelectDateBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.AppSettingManager;
import com.whosin.app.service.models.ActivityDetailModel;
import com.whosin.app.service.models.ActivityFetchModel;
import com.whosin.app.service.models.CartModel;
import com.whosin.app.service.models.ContainerListModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.comman.BaseActivity;
import com.whosin.app.ui.activites.offers.DisclaimerBottomSheet;
import com.whosin.app.ui.activites.venue.ui.MyCartActivity;
import com.whosin.app.ui.activites.wallet.WalletActivity;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class YourOrderActivity extends BaseActivity {

    private ActivityYourOrderBinding binding;
    private final SelectActivityTimeAdapter<ActivityFetchModel>  activityTimeAdapter = new SelectActivityTimeAdapter<>();
    private final SelectActivitySlotAdapter<ActivityFetchModel>  activitySlotAdapter = new SelectActivitySlotAdapter<>();
    private ActivityDetailModel activityDetailModel;
    private TimeAdapter timeAdapter;
    private List<String> timeList =new ArrayList<>();
    private int selectSeat = 0;
    private int totalSeat=0;
    private String date;
    private String time;
    private int selectedPosition = -1;
    private int selectedPositionSlot = -1;
    private int selectedPositionTimeAdaptere = -1;
    private int  discountAmount = 0;


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------


    @Override
    protected void initUi() {

        applyTranslations();

        Utils.changeStatusBarColor(getWindow(),getResources().getColor(R.color.buy_screen_header));

        String model = getIntent().getStringExtra("activityModel");
        activityDetailModel = new Gson().fromJson(model, ActivityDetailModel.class);
        setConformtionValue(activityDetailModel);

        binding.tvTermsCondition.setVisibility(!activityDetailModel.getDisclaimerTitle().isEmpty() ? View.VISIBLE : View.GONE);

        List<CartModel> cartListItem = CartModel.getCartHistory();
        int cartSize = !cartListItem.isEmpty() ? cartListItem.size() : 0;
        binding.cartItemLayout.setVisibility(cartSize > 0 ? View.VISIBLE : View.GONE);
        binding.tvCartItem.setText(String.valueOf(cartSize));
    }


    @Override
    protected void setListeners() {

        binding.checkoutButton.setOnClickListener(v -> {
            if (TextUtils.isEmpty(date)) {
                Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), getValue("select_date"));
                return;
            }
            if (TextUtils.isEmpty(time)) {
                Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), getValue("select_time"));
                return;
            }
            if (selectSeat == 0) {
                Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), getValue("please_select_quantity"));
                return;
            }
            if (selectSeat > 0)  {
                OrderConfirmationDialog dialog = new OrderConfirmationDialog(activityDetailModel, selectSeat, date, time);
                dialog.callback = data -> {
                    finish();
                    if (data) {
                        startActivity(new Intent(Graphics.context, WalletActivity.class));
                    }
                };
                dialog.show(getSupportFragmentManager(), "1");
            }
        });

        binding.addToCartButton.setOnClickListener(view -> {

            if (TextUtils.isEmpty(date)) {
                Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), getValue("select_date"));
                return;
            }
            if(TextUtils.isEmpty(time)){
                Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), getValue("select_time"));
                return;
            }

            if (selectSeat == 0) {
                Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), getValue("please_select_quantity"));
                return;
            }

            CartModel.addToCart(activityDetailModel.getId(),"activity",activityDetailModel,null, null,selectSeat,date,time,null,0,discountAmount);
            Intent intent = new Intent(activity, MyCartActivity.class);
            activityLauncher.launch( intent, result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    boolean isClose = result.getData().getBooleanExtra("close",false);
                    if (isClose) {
                        AppSettingManager.shared.tmpCartList.clear();
                        finish();
                    }
                }
            } );

        });


        binding.tvTermsCondition.setOnClickListener(v -> {
            if (!activityDetailModel.getDisclaimerTitle().isEmpty()) {
                DisclaimerBottomSheet disclaimerBottomSheet = new DisclaimerBottomSheet();
                disclaimerBottomSheet.disclaimerTitle = activityDetailModel.getDisclaimerTitle();
                disclaimerBottomSheet.disclaimerDescription = activityDetailModel.getDisclaimerDescription();
                disclaimerBottomSheet.show(getSupportFragmentManager(), "DisclaimerBottomSheet");
            }
        });

    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityYourOrderBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected void onResume() {

        super.onResume();
        List<CartModel> cartListItem = CartModel.getCartHistory();
        int cartSize = !cartListItem.isEmpty() ? cartListItem.size() : 0;
        binding.cartItemLayout.setVisibility(cartSize > 0 ? View.VISIBLE : View.GONE);
        binding.tvCartItem.setText(String.valueOf(cartSize));
    }



    @Override
    protected Map<View, String> getTranslationMap() {
        Map<View, String> map = new HashMap<>();
        map.put(binding.txtTitle, "your_order");
        map.put(binding.tvReservationFromTitle, "reservation_from");
        map.put(binding.tvReservationToTitle, "reservation_to");
        map.put(binding.tvPickDateAndTimeTitle, "pick_date_and_time");
        map.put(binding.tvYourSavingsTitle, "your_savings");
        map.put(binding.tvCheckOutTitle, "checkout");

        binding.tvTermsCondition.setText(Html.fromHtml(getValue("terms"), Html.FROM_HTML_MODE_LEGACY));

        return map;
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void setConformtionValue(ActivityDetailModel activityDetailModel) {

        binding.activityTimeRecycler.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        binding.tvName.setText(Utils.notNullString(activityDetailModel.getName()));
        binding.tvAddress.setText(Utils.notNullString(activityDetailModel.getProvider().getAddress()));
        binding.tvTitle.setText(Utils.notNullString(activityDetailModel.getProvider().getName()));
        Graphics.loadImage(activityDetailModel.getProvider().getLogo(), binding.iconImg);
        binding.tvDescription.setText(Utils.notNullString(activityDetailModel.getDescription()));
        Graphics.loadImage(activityDetailModel.getGalleries().get(0), binding.imgCover);


        Utils.setStyledText(activity,binding.tvTotalPrice,"0");



        int discount = Integer.parseInt( activityDetailModel.getDiscount() );
        int amount = activityDetailModel.getPrice();
        int  value = discount * amount/ 100;
         discountAmount = amount - value;

        if ("0".equals( activityDetailModel.getDiscount() )) {
            binding.tvAED.setVisibility( View.GONE );
            int colorTransparent = activity.getResources().getColor(R.color.transparent);
            binding.roundLinear.setBackgroundColor(colorTransparent);
//            binding.tvPrice.setText( String.valueOf(activityDetailModel.getPrice()   ) );
            Utils.setStyledText(activity,binding.tvPrice,String.valueOf(activityDetailModel.getPrice()));
        } else {
            if (amount == discountAmount) {
                binding.tvAED.setVisibility( View.GONE );
//                binding.tvPrice.setText(String.valueOf(activityDetailModel.getPrice()   ) );
                Utils.setStyledText(activity,binding.tvPrice,String.valueOf(activityDetailModel.getPrice()));
            } else {
                binding.tvAED.setVisibility( View.VISIBLE );
//                binding.tvAED.setText( String.valueOf(activityDetailModel.getPrice()));
//                binding.tvPrice.setText( String.valueOf( discountAmount ) );
                Utils.setStyledText(activity,binding.tvPrice,String.valueOf(discountAmount));
                Utils.setStyledText(activity,binding.tvAED,String.valueOf(activityDetailModel.getPrice()));

            }
        }

        binding.roundLinear.setVisibility(discountAmount == 0 ? View.GONE : View.VISIBLE);
        binding.addQuantityLayout.setVisibility(discountAmount == 0 ? View.GONE : View.VISIBLE);



        binding.tvAED.setPaintFlags(binding.tvAED.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        binding.activityTimeRecycler.setAdapter(activityTimeAdapter);
        requestFetchDate();


        int discountPrice = Integer.parseInt(String.valueOf(activityDetailModel.getPrice() - activityDetailModel.getPrice() * Integer.parseInt(activityDetailModel.getDiscount().split("%")[0]) / 100));

        binding.ivPlus.setOnClickListener(view -> {
            selectSeat++;
            binding.tvTotal.setText(String.valueOf(selectSeat));
            Utils.setStyledText(activity,binding.tvTotalPrice,String.valueOf((selectSeat * discountPrice)));
            showSavingText(selectSeat * discountPrice);
        });

        binding.ivMinus.setOnClickListener(view -> {
            if(selectSeat > 0){
                selectSeat--;
                binding.tvTotal.setText(String.valueOf(selectSeat));
                Utils.setStyledText(activity,binding.tvTotalPrice,String.valueOf((selectSeat * discountPrice)));
                showSavingText(selectSeat * discountPrice);
            }
        });

        binding.imageMyCart.setOnClickListener(view -> startActivity(new Intent(YourOrderActivity.this, MyCartActivity.class)));

        binding.ivClose.setOnClickListener(view -> onBackPressed());

        binding.tvStartTime.setText(Utils.convertMainDateFormat(activityDetailModel.getStartDate()));
        binding.tvEndDate.setText(Utils.convertMainDateFormat(activityDetailModel.getEndDate()));

        if (activityDetailModel.getActivityTime().getStart() != null) {
            binding.startTime.setText(getValue("at") + activityDetailModel.getActivityTime().getStart().toString());

        } else {
            binding.startTime.setText(" ");
        }
        if (activityDetailModel.getActivityTime().getEnd() != null) {
            binding.endTime.setText(getValue("at") + activityDetailModel.getActivityTime().getEnd().toString());

        } else {
            binding.startTime.setText(" ");
        }
    }



    private   List<String> getTimeArray(String startTime, String endTime) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm");

        try {
            Date startDate = dateFormatter.parse(startTime);
            Date endDate = dateFormatter.parse(endTime);

            if (startDate == null || endDate == null) {
                return timeList;
            }

            Calendar currentTime = Calendar.getInstance();
            currentTime.setTime(startDate);

            while (currentTime.getTime().compareTo(endDate) <= 0) {
                String currentTimeString = dateFormatter.format(currentTime.getTime());
                timeList.add(currentTimeString);

                currentTime.add(Calendar.HOUR_OF_DAY, 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return timeList;
    }


    @SuppressLint("NewApi")
    private static void filterTimes(List<String> timeList, String date) {
        LocalDate currentDate = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            currentDate = LocalDate.now();
        }
        LocalDate inputDate = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            inputDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }

        // Check if the date is today
        assert currentDate != null;
        if (currentDate.equals(inputDate)) {
            LocalTime currentTime = null;
            currentTime = LocalTime.now();
            DateTimeFormatter formatter = null;
            formatter = DateTimeFormatter.ofPattern("HH:mm");
            String currentTimeString = null;
            currentTimeString = currentTime.format(formatter);

            Iterator<String> iterator = timeList.iterator();

            while (iterator.hasNext()) {
                String time = iterator.next();
                if (time.compareTo(currentTimeString) <= 0) {
                    iterator.remove();
                }
            }
        }
     }


    private void showSavingText(int i) {
        if (i == 0) {
            return;
        }
        int savingAmount = activityDetailModel.getPrice() - i;
        binding.YourSavingLayout.setVisibility(savingAmount > 0 ? View.VISIBLE : View.GONE);
//        binding.tvYourSaving.setText("Your saving : AED " + savingAmount);


        SpannableString styledPrice = Utils.getStyledText(activity,String.valueOf(String.valueOf(savingAmount)));
        SpannableStringBuilder fullText = new SpannableStringBuilder();
        fullText.append(" ");
        fullText.append(getValue("your_savings")).append(styledPrice);
        binding.tvYourSaving.setText(fullText);
    }


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestFetchDate() {
        DataService.shared(activity).requestActivityFetchDate(activityDetailModel.getId(), new RestCallback<ContainerListModel<ActivityFetchModel>>(this) {
            @Override
            public void result(ContainerListModel<ActivityFetchModel> model, String error) {
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }
                assert model.data != null;
                if (!model.data.isEmpty()){
                    activityTimeAdapter.updateData(model.data);
                }
            }
        });
    }

    private void requestFetchTimeSlot(String date) {
        DataService.shared(activity).requestActivityFetchTimeSlot(activityDetailModel.getId(), date, new RestCallback<ContainerListModel<ActivityFetchModel>>(this) {
            @Override
            public void result(ContainerListModel<ActivityFetchModel> model, String error) {
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }
                assert model.data != null;
                if (!model.data.isEmpty()) {
                    activitySlotAdapter.updateData(model.data);
                }
            }
        });
    }

    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    public class SelectActivityTimeAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_activity_select_date));
        }

        @SuppressLint({"SimpleDateFormat", "SetTextI18n", "UseCompatLoadingForDrawables", "NotifyDataSetChanged"})
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

            ViewHolder viewHolder = (ViewHolder) holder;
            ActivityFetchModel model = (ActivityFetchModel) getItem(position);


            viewHolder.binding.iconText.setText(Utils.convertDateFormat(model.getDate(),"yyyy-MM-dd","dd MMM") + " " + "(" + model.getRemainingSeat() + ")");

            if (position == selectedPosition) {
                viewHolder.binding.linearMainView.setBackground(activity.getResources().getDrawable(R.drawable.green_button_bg));
            } else {
                viewHolder.binding.linearMainView.setBackground(activity.getResources().getDrawable(R.drawable.light_black_bg));
            }

            viewHolder.binding.linearMainView.setOnClickListener(view -> {
                if (activityDetailModel.getActivityTime().getType().equals("slot")) {
                    requestFetchTimeSlot(model.getDate());
                    binding.activitySlotRecycler.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
                    binding.activitySlotRecycler.setAdapter(activitySlotAdapter);

                } else {
                    if (!timeList.isEmpty()){
                        timeList.clear();
                    }
                    getTimeArray(activityDetailModel.getActivityTime().getStart().toString(), activityDetailModel.getActivityTime().getEnd());
                    filterTimes(timeList,model.getDate());
                    binding.activitySlotRecycler.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
                    timeAdapter = new TimeAdapter(timeList);
                    binding.activitySlotRecycler.setAdapter(timeAdapter);

                }

                selectedPosition = position;
                totalSeat = model.getSeat();
                date = model.getDate();
                notifyDataSetChanged();


            });

        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final ItemActivitySelectDateBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemActivitySelectDateBinding.bind(itemView);
            }
        }
    }

    public class SelectActivitySlotAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {


        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_activity_select_date));
        }

        @SuppressLint({"SimpleDateFormat", "UseCompatLoadingForDrawables", "NotifyDataSetChanged"})
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

            ViewHolder viewHolder = (ViewHolder) holder;
            ActivityFetchModel model = (ActivityFetchModel) getItem(position);
            viewHolder.binding.iconText.setText(model.getTime());


            if (position == selectedPositionSlot) {
                viewHolder.binding.linearMainView.setBackground(activity.getResources().getDrawable(R.drawable.green_button_bg));
            } else {
                viewHolder.binding.linearMainView.setBackground(activity.getResources().getDrawable(R.drawable.light_black_bg));

            }
            viewHolder.binding.getRoot().setOnClickListener(view -> {
                selectedPositionSlot = position;
                time = model.getTime();
                notifyDataSetChanged();
            });

        }


        public class ViewHolder extends RecyclerView.ViewHolder {

            private final ItemActivitySelectDateBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemActivitySelectDateBinding.bind(itemView);
            }
        }

    }

    public class TimeAdapter extends RecyclerView.Adapter<TimeAdapter.TimeViewHolder> {
        private List<String> timeList;

        public TimeAdapter(List<String> timeList) {
            this.timeList = timeList;
        }

        @NonNull
        @Override
        public TimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activity_select_date, parent, false);
            return new TimeViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull TimeViewHolder holder, @SuppressLint("RecyclerView") int position) {
            TimeViewHolder viewHolder = (TimeViewHolder) holder;
            String slotTime = timeList.get(position);
            viewHolder.binding.iconText.setText(slotTime);


            if (position == selectedPositionTimeAdaptere) {
                viewHolder.binding.linearMainView.setBackground(activity.getResources().getDrawable(R.drawable.green_button_bg));
            } else {
                viewHolder.binding.linearMainView.setBackground(activity.getResources().getDrawable(R.drawable.light_black_bg));

            }

            viewHolder.binding.getRoot().setOnClickListener(view -> {
                selectedPositionTimeAdaptere = position;
                time = slotTime;
                notifyDataSetChanged();
            });

        }

        @Override
        public int getItemCount() {
            return timeList.size();
        }

        public class TimeViewHolder extends RecyclerView.ViewHolder {
            private final ItemActivitySelectDateBinding binding;

            public TimeViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemActivitySelectDateBinding.bind(itemView);
            }
        }

    }


    // endregion
    // --------------------------------------

}


