package com.whosin.app.ui.fragment.Bucket;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.ViewGroup;

import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.FragmentEventBinding;
import com.whosin.app.databinding.ItemBucketEventListBinding;
import com.whosin.app.service.models.CreateBucketListModel;
import com.whosin.app.service.models.EventDetailModel;
import com.whosin.app.service.models.EventModel;
import com.whosin.app.ui.activites.home.event.EventDetailsActivity;
import com.whosin.app.ui.activites.venue.Bucket.BucketListBottomSheet;
import com.whosin.app.ui.activites.venue.Bucket.BucketListDetailActivity;
import com.whosin.app.ui.fragment.Profile.RemoveBucketDialog;
import com.whosin.app.ui.fragment.comman.BaseFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class EventFragment extends BaseFragment {

    private FragmentEventBinding binding;
    private CreateBucketListModel bucketListModel;
    private EventDetailAdapter<EventModel> EventDetailAdapter = new EventDetailAdapter();

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    public EventFragment(CreateBucketListModel bucketListModel) {
        this.bucketListModel = bucketListModel;
    }

    @Override
    public void initUi(View view) {
        binding = FragmentEventBinding.bind(view);
        setBucketDetailAdapter();
    }

    @Override
    public void setListeners() {
        binding.eventListRecycler.setNestedScrollingEnabled(false);
    }

    @Override
    public void populateData(boolean getDataFromServer) {
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_event;
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void setBucketDetailAdapter() {
        binding.eventListRecycler.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false));
        binding.eventListRecycler.setAdapter(EventDetailAdapter);

        if (bucketListModel.getEvents() != null && !bucketListModel.getEvents().isEmpty()) {
            EventDetailAdapter.updateData(bucketListModel.getEvents());
            binding.emptyPlaceHolderView.setVisibility(View.GONE);
            binding.eventListRecycler.setVisibility(View.VISIBLE);
        } else {
            binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
            binding.eventListRecycler.setVisibility(View.GONE);
        }
    }


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------
    private void reloadData() {
        BucketListDetailActivity parentActivity = (BucketListDetailActivity) getActivity();
        if (parentActivity != null) {
            parentActivity.requestBucketDetail(bucketListModel.getId());
        }
    }

    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------


    public class EventDetailAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {


        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_bucket_event_list));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            boolean isLastItem = position == getItemCount() - 1;
            EventModel model = (EventModel) getItem(position);

            if (model != null) {

                viewHolder.binding.eventTitle.setText(model.getTitle());

                if (model.getCustomVenueObject() != null) {
                    viewHolder.binding.venueContainer.setVenueDetail(model.getCustomVenueObject());
                }

                //SET TIMER
                if (model.getEventTime() != null) {
                    Utils.setTimer(model.getEventTime(), viewHolder.binding.countTimer);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    try {
                        Date givenDate = dateFormat.parse(model.getEventTime());
                        Date currentDate = new Date();
                        if (givenDate.before(currentDate)) {
                            viewHolder.binding.layoutTimer.setVisibility(View.INVISIBLE);
                        } else {
                            viewHolder.binding.layoutTimer.setVisibility(View.VISIBLE);
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                Graphics.applyBlurEffect(getActivity(), viewHolder.binding.blurView);
                Graphics.applyBlurEffect(getActivity(), viewHolder.binding.blurViewTime);
                viewHolder.binding.eventDescription.setText(model.getDescription());
                Graphics.loadImage(model.getImage(),viewHolder.binding.cover);

                try {
                    viewHolder.binding.eventStartTime.setText(Utils.convertMainTimeFormat(model.getReservationTime()) + " - " + Utils.convertMainTimeFormat(model.getEventTime()));

                    viewHolder.binding.eventDate.setText(new SimpleDateFormat("E, dd MM yyyy", Locale.ENGLISH).format(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).parse(model.getEventTime())));

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                viewHolder.itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(getActivity(), EventDetailsActivity.class);
                    intent.putExtra("eventId", model.getId())
                            .putExtra("name", model.getOrgData().getName())
                            .putExtra("address", model.getOrgData().getWebsite())
                            .putExtra("image", model.getOrgData().getLogo()
                            );
                    startActivity(intent);
                });

                viewHolder.binding.ivMenu.setOnClickListener(view -> {
                    Utils.preventDoubleClick( view );

                    ArrayList<String> data = new ArrayList<>();
                    data.add("Remove");
                    data.add("Move to another bucket");
                    Graphics.showActionSheet(getContext(), getString(R.string.app_name), data, (data1, position1) -> {
                        switch (position1) {
                            case 0:
                                RemoveBucketDialog dialog = new RemoveBucketDialog(bucketListModel.getId());
                                dialog.eventId = model.getId();
                                dialog.callback = data2 -> {
                                    reloadData();
                                };
                                dialog.show(getChildFragmentManager(), "");
                                break;
                            case 1:
                                BucketListBottomSheet bucketDialog = new BucketListBottomSheet();
                                bucketDialog.eventId = model.getId();
                                bucketDialog.isBucketRemove = true;
                                bucketDialog.bucketId = bucketListModel.getId();
                                bucketDialog.callBack = data2 -> {
                                    if (data2) {
                                        reloadData();
                                    }
                                };

                                bucketDialog.show(getChildFragmentManager(), "");
                                break;
                        }
                    });

                });
            }

            if (isLastItem) {
                int marginBottom = Utils.getMarginBottom(holder.itemView.getContext(), 0.10f);
                Utils.setBottomMargin(holder.itemView, marginBottom);
            } else {
                Utils.setBottomMargin(holder.itemView, 0);
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private ItemBucketEventListBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemBucketEventListBinding.bind(itemView);
            }
        }
    }
}