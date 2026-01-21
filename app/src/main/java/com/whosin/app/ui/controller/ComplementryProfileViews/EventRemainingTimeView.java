package com.whosin.app.ui.controller.ComplementryProfileViews;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;
import androidx.asynclayoutinflater.view.AsyncLayoutInflater;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.whosin.app.R;
import com.whosin.app.databinding.CmEventRemainingTimeViewBinding;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.ComplimentaryProfileModel;
import com.whosin.app.service.models.MessageEvent;
import com.whosin.app.service.models.PromoterEventModel;

import org.greenrobot.eventbus.EventBus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Locale;


public class EventRemainingTimeView extends ConstraintLayout {

    private CmEventRemainingTimeViewBinding binding;

    private String date = "";

    private String time = "";

    private Context context;

    public boolean isColorTranspart = false;

    public Activity activity;


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    public EventRemainingTimeView(Context context) {
        this(context, null);
    }

    public EventRemainingTimeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EventRemainingTimeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;

        LayoutInflater.from(context).inflate(R.layout.offer_info_view_loader, this, true);
        AsyncLayoutInflater asyncLayoutInflater = new AsyncLayoutInflater(context);
        asyncLayoutInflater.inflate(R.layout.cm_event_remaining_time_view, this, (view, resid, parent) -> {
            binding = CmEventRemainingTimeViewBinding.bind( view );

            if (!TextUtils.isEmpty(date) && !TextUtils.isEmpty(time)) if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                setTimerForDubaiEvent(date,time);
            }

            if (binding != null && isColorTranspart){
                binding.roundTimer1.setBackgroundColor(ContextCompat.getColor(activity, R.color.deal_time_bg));
                binding.roundTimer2.setBackgroundColor(ContextCompat.getColor(activity, R.color.deal_time_bg));
                binding.roundTimer3.setBackgroundColor(ContextCompat.getColor(activity, R.color.deal_time_bg));
                binding.roundTimer4.setBackgroundColor(ContextCompat.getColor(activity, R.color.deal_time_bg));
                binding.mainLinerLayout.setPadding(0, 0, 0, 0);
                binding.mainLinerLayout.setGravity(Gravity.START);
                ViewGroup.LayoutParams params = binding.mainLinerLayout.getLayoutParams();
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                binding.mainLinerLayout.setLayoutParams(params);

            }


            EventRemainingTimeView.this.removeAllViews();
            EventRemainingTimeView.this.addView( view );
        });
    }




    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    public void setBackgroundColorForTimer(Activity activity){


    }


    public void setUpData(String date , String time) {
        this.date = date;
        this.time = time;

        if (binding == null) {
             this.post(() -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    setTimerForDubaiEvent(date, time);
                }
            });
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                setTimerForDubaiEvent(date, time);
            }
        }

    }

    public void setUpData(String date , String time,boolean setColor,Activity activity) {
        this.date = date;
        this.time = time;

        if (binding == null) {
             this.post(() -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    setTimerForDubaiEvent(date, time);
                }
            });
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                setTimerForDubaiEvent(date, time);

            }
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public  void setTimerForDubaiEvent(String date, String startTime) {
        if (binding == null) return;
        // Cancel any existing timer
        if (binding.tvEventSec.getTag() != null) {
            CountDownTimer existingTimer = (CountDownTimer) binding.tvEventSec.getTag();
            existingTimer.cancel();
        }

        // Get the current time in Dubai
        ZonedDateTime dubaiCurrentTime = ZonedDateTime.now(ZoneId.of("Asia/Dubai"));

        // Combine date and time strings into a LocalDateTime object
        LocalDate eventDate = LocalDate.parse(date);
        LocalTime eventTime = LocalTime.parse(startTime);
        LocalDateTime eventDateTime = LocalDateTime.of(eventDate, eventTime);

        // Convert the event date and time to ZonedDateTime in Dubai timezone
        ZonedDateTime eventDateTimeInDubai = eventDateTime.atZone(ZoneId.of("Asia/Dubai"));

        // Check if the event date is in the past
        if (eventDateTimeInDubai.toInstant().toEpochMilli() <= dubaiCurrentTime.toInstant().toEpochMilli()) {
            binding.tvEventDays.setText("00");
            binding.tvEventHours.setText("00");
            binding.tvEventMin.setText("00");
            binding.tvEventSec.setText("00");
            return;
        }

        // Calculate the duration until the event
        long durationMillis = eventDateTimeInDubai.toInstant().toEpochMilli() - dubaiCurrentTime.toInstant().toEpochMilli();

        // Create a new CountDownTimer
        CountDownTimer countDownTimer = new CountDownTimer(durationMillis, 1000) {
            @SuppressLint("DefaultLocale")
            @Override
            public void onTick(long millisUntilFinished) {
                long secondsRemaining = millisUntilFinished / 1000;
                long days = secondsRemaining / (3600 * 24);
                long hours = (secondsRemaining % (3600 * 24)) / 3600;
                long minutes = (secondsRemaining % 3600) / 60;
                long seconds = secondsRemaining % 60;

                binding.tvEventDays.setText(String.format(Locale.ENGLISH, "%02d", days));
                binding.tvEventHours.setText(String.format(Locale.ENGLISH, "%02d", hours));
                binding.tvEventMin.setText(String.format(Locale.ENGLISH, "%02d", minutes));
                binding.tvEventSec.setText(String.format(Locale.ENGLISH, "%02d", seconds));

            }

            @Override
            public void onFinish() {
                if (!SessionManager.shared.getUser().isRingMember() && !SessionManager.shared.getUser().isPromoter()) {
                    EventBus.getDefault().post(new PromoterEventModel());
                } else if (SessionManager.shared.getUser().isRingMember()) {
                    EventBus.getDefault().post(new ComplimentaryProfileModel());
                    EventBus.getDefault().post(new MessageEvent());
                }

                binding.tvEventDays.setText("00");
                binding.tvEventHours.setText("00");
                binding.tvEventMin.setText("00");
                binding.tvEventSec.setText("00");
            }
        };

        // Start the timer and set it as a tag to the TextView
        countDownTimer.start();
        binding.tvEventSec.setTag(countDownTimer);
    }

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------


    // --------------------------------------
    // region Adapter
    // --------------------------------------


    // endregion
    // --------------------------------------
}