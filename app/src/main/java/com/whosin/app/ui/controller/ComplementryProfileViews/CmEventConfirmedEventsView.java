package com.whosin.app.ui.controller.ComplementryProfileViews;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.EventsConfirmedViewBinding;
import com.whosin.app.databinding.ItemConfirmedEventsBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.models.ContainerListModel;
import com.whosin.app.service.models.PromoterEventModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.Promoter.ComplementaryEventDetailActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class CmEventConfirmedEventsView extends ConstraintLayout {

    private EventsConfirmedViewBinding binding;

    private CmConfirmedEventListAdapter<PromoterEventModel> cmEventListAdapter = new CmConfirmedEventListAdapter<>();

    private Context context;

    private Activity activity;

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    public CmEventConfirmedEventsView(Context context) {
        this(context, null);
    }

    public CmEventConfirmedEventsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CmEventConfirmedEventsView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;

        View view = LayoutInflater.from(context).inflate(R.layout.events_confirmed_view, this);
        binding = EventsConfirmedViewBinding.bind(view);


    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    public void setUpData(Activity activity) {
        this.activity = activity;

        if (binding == null) {
            return;
        }

        binding.confiremdEventsRecycleView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        binding.confiremdEventsRecycleView.setAdapter(cmEventListAdapter);
        LinearSnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(binding.confiremdEventsRecycleView);

        requestPromoterEventListUser();


        binding.confiremdEventsRecycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int visiblePosition = layoutManager.findFirstVisibleItemPosition();
                int dotSelection = 0;
                int totalItems = cmEventListAdapter.getItemCount();
                if (totalItems >= 3) {
                    if (visiblePosition == 0) {
                        dotSelection = 0;
                    } else if (visiblePosition >= totalItems - 1) {
                        dotSelection = 2;
                    } else {
                        dotSelection = 1;
                    }
                } else if (totalItems == 2) {
                    dotSelection = visiblePosition == 0 ? 0 : 1;
                }

                binding.dotsIndicator.setDotSelection(dotSelection);
            }
        });

    }


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------


    private void requestPromoterEventListUser() {
        DataService.shared(activity).requestCmEventConfirmedLis(new RestCallback<ContainerListModel<PromoterEventModel>>(null) {
            @SuppressLint("NewApi")
            @Override
            public void result(ContainerListModel<PromoterEventModel> model, String error) {

                if (!Utils.isNullOrEmpty(error) || model == null) {
                    binding.confiremdEventsRecycleView.setVisibility(View.GONE);
                    binding.dotsIndicator.setVisibility(GONE);
                    return;
                }

                if (model.data != null && !model.data.isEmpty()) {
                    binding.confiremdEventsRecycleView.setVisibility(View.VISIBLE);
                    binding.viewLine.setVisibility(View.VISIBLE);
                    binding.dotsIndicator.setVisibility(View.VISIBLE);
                    cmEventListAdapter.updateData(getSortedEventsByDateAndTime(model.data));
                    if (model.data.size() == 1) binding.dotsIndicator.setVisibility(GONE);
                    if (model.data.size() == 2) binding.dotsIndicator.initDots(2);
                    if (model.data.size() >= 3) binding.dotsIndicator.initDots(3);
                } else {
                    binding.confiremdEventsRecycleView.setVisibility(View.GONE);
                    binding.dotsIndicator.setVisibility(GONE);
                    binding.viewLine.setVisibility(GONE);
                }

            }
        });
    }

    public void setTimerForDubaiEvent(String date, String startTime, TextView textView, ItemConfirmedEventsBinding vBinding) {
        vBinding.reamingTitle.setText(Utils.getLangValue("remaining"));
        if (textView.getTag() != null) {
            CountDownTimer existingTimer = (CountDownTimer) textView.getTag();
            existingTimer.cancel();
        }

        // Get the current time in Dubai
        ZonedDateTime dubaiCurrentTime = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            dubaiCurrentTime = ZonedDateTime.now(ZoneId.of("Asia/Dubai"));
        }

        // Combine date and time strings into a LocalDateTime object
        LocalDate eventDate = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            eventDate = LocalDate.parse(date);
        }
        LocalTime eventTime = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            eventTime = LocalTime.parse(startTime);
        }
        LocalDateTime eventDateTime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            eventDateTime = LocalDateTime.of(eventDate, eventTime);
        }

        // Convert the event date and time to ZonedDateTime in Dubai timezone
        ZonedDateTime eventDateTimeInDubai = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            eventDateTimeInDubai = eventDateTime.atZone(ZoneId.of("Asia/Dubai"));
        }

        // Check if the event date is in the past
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (eventDateTimeInDubai.toInstant().toEpochMilli() <= dubaiCurrentTime.toInstant().toEpochMilli()) {
                vBinding.reamingTitle.setVisibility(View.INVISIBLE);
                vBinding.timerCountLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.im_in));
                textView.setText(Utils.setLangValue("started"));
                return;
            }
        }

        // Calculate the duration until the event
        long durationMillis = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            durationMillis = eventDateTimeInDubai.toInstant().toEpochMilli() - dubaiCurrentTime.toInstant().toEpochMilli();
        }

        // Create a new CountDownTimer
        CountDownTimer countDownTimer = new CountDownTimer(durationMillis, 1000) {
            @SuppressLint("DefaultLocale")
            @Override
            public void onTick(long millisUntilFinished) {
                long secondsRemaining = millisUntilFinished / 1000;
                long days = secondsRemaining / (3600 * 24);
                vBinding.timerCountLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.gray));
                if (days == 0) {
                    vBinding.reamingTitle.setVisibility(View.INVISIBLE);
                    textView.setText(String.format(startTime));
                } else {
                    vBinding.reamingTitle.setVisibility(View.VISIBLE);
                    textView.setText(String.format(days + " days"));
                }



            }

            @Override
            public void onFinish() {
                vBinding.reamingTitle.setVisibility(View.INVISIBLE);
                vBinding.timerCountLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.im_in));
                textView.setText(Utils.setLangValue("started"));

            }
        };

        countDownTimer.start();
        textView.setTag(countDownTimer);
    }

    private List<PromoterEventModel> getSortedEventsByDateAndTime(List<PromoterEventModel> eventList) {
        List<PromoterEventModel> sortedEvents = new ArrayList<>(eventList);
        Collections.sort(sortedEvents, (e1, e2) -> {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Date event1DateTime = dateFormat.parse(e1.getDate() + " " + e1.getStartTime());
                Date event2DateTime = dateFormat.parse(e2.getDate() + " " + e2.getStartTime());
                return event1DateTime.compareTo(event2DateTime);
            } catch (ParseException ex) {
                ex.printStackTrace();
            }
            return 0;
        });
        return sortedEvents;
    }




    // --------------------------------------
    // region Adapter
    // --------------------------------------


    private class CmConfirmedEventListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = UiUtils.getViewBy(parent, R.layout.item_confirmed_events);
            return new ViewHolder(view);
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @SuppressLint("DefaultLocale")
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;

            PromoterEventModel model = (PromoterEventModel) getItem(position);
            if (model == null) return;

            if (model.getVenueType().equals("custom")) {
                if (model.getCustomVenue() != null) {
                    Graphics.loadImage(model.getCustomVenue().getImage(), viewHolder.binding.eventLogo);
                    viewHolder.binding.txtName.setText(model.getCustomVenue().getName());
                    viewHolder.binding.txtAddress.setText(model.getCustomVenue().getAddress());
                    Log.d("IMAGE", "TITLE" + model.getCustomVenue().getName());
                }
            } else {
                if (model.getVenue() != null) {
                    Graphics.loadImage(model.getVenue().getCover(), viewHolder.binding.eventLogo);
                    viewHolder.binding.txtName.setText(model.getVenue().getName());
                    viewHolder.binding.txtAddress.setText(model.getVenue().getAddress());
                    Log.d("IMAGE", "TITLE1" + model.getVenue().getName());
                }
            }


            viewHolder.binding.txttime.setText(Utils.formatDateTime(model.getDate(),model.getStartTime(),model.getEndTime()));

            setTimerForDubaiEvent(model.getDate(),model.getStartTime(),viewHolder.binding.eventCounterTimer,viewHolder.binding);

            viewHolder.binding.getRoot().setOnClickListener(v -> {
                Utils.preventDoubleClick(v);
                Intent intent = new Intent(activity, ComplementaryEventDetailActivity.class);
                intent.putExtra("eventId", model.getId());
                intent.putExtra("type", "complementary");
                activity.startActivity(intent);

            });

        }


        public class ViewHolder extends RecyclerView.ViewHolder {

            private final ItemConfirmedEventsBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemConfirmedEventsBinding.bind(itemView);
            }


        }

    }

    // endregion
    // --------------------------------------
}
