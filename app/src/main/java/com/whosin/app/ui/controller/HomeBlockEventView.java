package com.whosin.app.ui.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ParseException;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.asynclayoutinflater.view.AsyncLayoutInflater;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.whosin.app.R;
import com.whosin.app.comman.AppExecutors;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.HorizontalSpaceItemDecoration;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.comman.ui.UserInfoView;
import com.whosin.app.comman.ui.roundcornerlayout.RoundCornerLinearLayout;
import com.whosin.app.databinding.HomeBlockLargeOfferViewBinding;
import com.whosin.app.databinding.HomeEventItemBinding;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.EventModel;
import com.whosin.app.service.models.OffersModel;
import com.whosin.app.service.models.VenueObjectModel;
import com.whosin.app.ui.activites.home.event.EventDetailsActivity;
import com.whosin.app.ui.activites.home.event.EventOrganizerDetailsActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class HomeBlockEventView extends ConstraintLayout {

    private HomeBlockLargeOfferViewBinding binding;

    private Context context;
    private Activity activity;
    private FragmentManager supportFragmentManager;
    private List<EventModel> eventList;

    private EventAdapter<EventModel> eventAdapter;

    public HomeBlockEventView(Context context) {
        this( context, null );
    }

    public HomeBlockEventView(Context context, AttributeSet attrs) {
        this( context, attrs, 0 );
    }

    public HomeBlockEventView(Context context, AttributeSet attrs, int defStyleAttr) {
        super( context, attrs, 0 );
        this.context = context;
        LayoutInflater.from( context ).inflate( R.layout.offer_info_view_loader, this, true );

        AsyncLayoutInflater asyncLayoutInflater = new AsyncLayoutInflater( context );
        asyncLayoutInflater.inflate( R.layout.home_block_large_offer_view, this, (view, resid, parent) -> {
            binding = HomeBlockLargeOfferViewBinding.bind( view );
            setupRecycleHorizontalManager( binding.offerLargeRecycler );
            eventAdapter = new EventAdapter<>();
            binding.offerLargeRecycler.setAdapter( eventAdapter );
            if (eventList != null) {
                activity.runOnUiThread(() -> eventAdapter.updateData( eventList ));
            }
            HomeBlockEventView.this.removeAllViews();
            HomeBlockEventView.this.addView( view );
        } );
    }

    private void setupRecycleHorizontalManager(RecyclerView recyclerView) {
        recyclerView.setLayoutManager( new LinearLayoutManager( context, LinearLayoutManager.HORIZONTAL, false ) );
        int spacing = getResources().getDimensionPixelSize( com.intuit.ssp.R.dimen._10ssp );
        recyclerView.addItemDecoration( new HorizontalSpaceItemDecoration( spacing ) );
//        recyclerView.setNestedScrollingEnabled( false );
        recyclerView.offsetChildrenHorizontal( 1 );
    }


    public void setupData(List<EventModel> eventList, Activity activity, FragmentManager fragmentManager) {
        this.eventList = eventList;
        this.activity = activity;
        this.supportFragmentManager = fragmentManager;
        if (eventList == null) {return;}
        if (binding == null) { return; }
        activity.runOnUiThread(() -> eventAdapter.updateData( eventList ));
    }

    public class EventAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = UiUtils.getViewBy(parent, R.layout.home_event_item);
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.width = (int) (Graphics.getScreenWidth(context) * (getItemCount() > 1 ? 0.89 : 0.93));
            view.setLayoutParams(params);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            EventModel model = (EventModel) getItem(position);
            Graphics.loadImage(model.getImage(), viewHolder.mBinding.cover);
            viewHolder.mBinding.txtEventName.setText(model.getTitle());

            if (model.getOrgData() != null) {
                Graphics.loadRoundImage(model.getOrgData().getLogo(), viewHolder.mBinding.venuLogo);
                viewHolder.mBinding.textVenuName.setText(model.getOrgData().getName());
            }

            if (model.getCustomVenueObject() != null) {
                viewHolder.mBinding.venueContainer.setVenueDetail(model.getCustomVenueObject());
            } else {
                setVenueInfo(model.getCustomVenueId(), viewHolder.mBinding.venueContainer,viewHolder.mBinding.blurView);
            }

            viewHolder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(activity, EventDetailsActivity.class);
                intent.putExtra("eventId", model.getId());
                if (model.getOrgData() != null) {
                    intent.putExtra("name", model.getOrgData().getName());
                    intent.putExtra("address", model.getOrgData().getWebsite());
                    intent.putExtra("image", model.getOrgData().getLogo());
                }
                if (model.getCustomVenueObject() != null) {
                    intent.putExtra("venueName", model.getCustomVenueObject().getName());
                    intent.putExtra("venueAddress", model.getCustomVenueObject().getAddress());
                    intent.putExtra("venueImage", model.getCustomVenueObject().getLogo());
                    intent.putExtra("venueId", model.getCustomVenueObject().getId());
                    intent.putExtra("venueModel", new Gson().toJson(model.getCustomVenueObject()));
                }
                activity.startActivity(intent);

            });

            viewHolder.mBinding.linearEvent.setOnClickListener(v -> {
                if (model.getOrgData() != null) {
                    activity.startActivity(new Intent(activity, EventOrganizerDetailsActivity.class).putExtra("org_id", model.getOrgId()).putExtra("type", "events_organizers").putExtra("name", model.getOrgData().getName()).putExtra("webSite", model.getOrgData().getWebsite()).putExtra("image", model.getOrgData().getLogo()));
                }
            });

            if (model.getEventTime() != null) {
                Utils.setTimer(model.getEventTime(), viewHolder.mBinding.countTimer);
                try {
                    Date givenDate = Utils.stringToDate(model.getEventTime(), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    if (givenDate != null) {
                        Date currentDate = new Date();
                        if (givenDate.before(currentDate)) {
                            viewHolder.mBinding.layoutTimer.setVisibility(View.INVISIBLE);
                        } else if (givenDate.after(currentDate)) {
                            viewHolder.mBinding.layoutTimer.setVisibility(View.VISIBLE);
                        } else {
                            viewHolder.mBinding.layoutTimer.setVisibility(View.INVISIBLE);
                        }
                    } else {
                        viewHolder.mBinding.layoutTimer.setVisibility(View.INVISIBLE);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private HomeEventItemBinding mBinding;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = HomeEventItemBinding.bind(itemView);
            }
        }

        private void setVenueInfo(String venueId, UserInfoView infoView, RoundCornerLinearLayout blurView) {
            Thread backgroundThread = new Thread(() -> {
                Optional<VenueObjectModel> venueObjectModel = SessionManager.shared.geHomeBlockData().getVenues().stream().filter(p -> p.getId().equals(venueId)).findFirst();
                venueObjectModel.ifPresent(objectModel -> AppExecutors.get().mainThread().execute(() -> infoView.setVenueDetail(objectModel)));

                if (!venueObjectModel.isPresent()) {
                    blurView.setVisibility(View.INVISIBLE);
                }
            });
            backgroundThread.start();
        }

    }

}
