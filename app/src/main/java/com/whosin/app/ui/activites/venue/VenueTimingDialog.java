package com.whosin.app.ui.activites.venue;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;
import com.whosin.app.databinding.ActivityVenueTimingDialogBinding;
import com.whosin.app.databinding.ItemVenueTimeBinding;
import com.whosin.app.service.models.VenueTimingModel;

import java.util.List;
import java.util.Objects;

public class VenueTimingDialog extends DialogFragment {


    private ActivityVenueTimingDialogBinding dialog;
    private  VenueTimeAdapter adapter;

    private List<VenueTimingModel> timingModels;

    private Activity activity;


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    public VenueTimingDialog(List<VenueTimingModel> timingModels, Activity activity) {
        this.timingModels = timingModels;
        this.activity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate( getLayoutRes(), container, false );
        initUi( view );
        setListeners();
        return view;
    }

    public void initUi(View view) {
        dialog = ActivityVenueTimingDialogBinding.bind( view );

        dialog.tvOpen.setText(Utils.getLangValue("opening"));
        dialog.tvClose.setText(Utils.getLangValue("closing"));
        dialog.tvCancel.setText(Utils.getLangValue("close"));

        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).setBackgroundDrawable(new ColorDrawable( Color.TRANSPARENT));
        setEventAdapter();

    }

    public void setListeners() {
        dialog.tvCancel.setOnClickListener( view -> dismiss());

    }

    public int getLayoutRes() {
        return R.layout.activity_venue_timing_dialog;
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void setEventAdapter() {

        dialog.venueRecycler.setLayoutManager(new LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false));
        adapter = new VenueTimeAdapter<>();
        dialog.venueRecycler.setAdapter(adapter);
        adapter.updateData(timingModels);
        adapter.notifyDataSetChanged();

    }

    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------


    public class VenueTimeAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView;
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_venue_time, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            VenueTimingModel model = (VenueTimingModel)getItem( position );
            viewHolder.binding.tvDay.setText(model.getDay());
            viewHolder.binding.openTime.setText(model.getOpeningTime());
            viewHolder.binding.closeTime.setText(model.getClosingTime());
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private ItemVenueTimeBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemVenueTimeBinding.bind(itemView);
            }
        }
    }
}