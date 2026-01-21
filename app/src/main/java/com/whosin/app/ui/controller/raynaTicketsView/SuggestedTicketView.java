package com.whosin.app.ui.controller.raynaTicketsView;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.whosin.app.R;
import com.whosin.app.comman.HorizontalSpaceItemDecoration;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.comman.ui.VerticalSpaceItemDecoration;
import com.whosin.app.databinding.ViewSuggestedTicketsBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.models.ContainerListModel;
import com.whosin.app.service.models.HomeTicketsModel;
import com.whosin.app.service.models.rayna.RaynaTicketDetailModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.adapter.raynaTicketAdapter.SuggestedTicketAdapter;

import java.util.ArrayList;
import java.util.List;

public class SuggestedTicketView extends ConstraintLayout {

    private ViewSuggestedTicketsBinding binding;
    private Activity activity;
    private FragmentManager fragmentManager;
    private SuggestedTicketAdapter<HomeTicketsModel> adapter;
    private String ticketId;

    public SuggestedTicketView(@NonNull Context context) {
        this(context, null);
    }

    public SuggestedTicketView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SuggestedTicketView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        binding = ViewSuggestedTicketsBinding.inflate(LayoutInflater.from(context), this, true);
        setVisibility(GONE);
    }

    public void initData(Activity activity, FragmentManager fragmentManager, String ticketId) {
        this.activity = activity;
        this.fragmentManager = fragmentManager;
        this.ticketId = ticketId;
        setupRecyclerView();
        loadSuggestedTickets();
    }

    private void setupRecyclerView() {
        adapter = new SuggestedTicketAdapter<>(activity, fragmentManager);
        binding.rvSuggestedTickets.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        int spacing = getResources().getDimensionPixelSize(com.intuit.ssp.R.dimen._10ssp);
        binding.rvSuggestedTickets.addItemDecoration(new HorizontalSpaceItemDecoration(spacing));
        binding.rvSuggestedTickets.addItemDecoration(new VerticalSpaceItemDecoration(spacing));
        binding.rvSuggestedTickets.setNestedScrollingEnabled(false);
        binding.rvSuggestedTickets.setHasFixedSize(true);
        binding.rvSuggestedTickets.setAdapter(adapter);
    }

    private void loadSuggestedTickets() {
        DataService.shared(getContext()).requestSuggestedTicket(ticketId, new RestCallback<ContainerListModel<HomeTicketsModel>>(null) {
            @Override
            public void result(ContainerListModel<HomeTicketsModel> model, String error) {
                if (model == null || model.data == null || model.data.isEmpty()) {
                    setVisibility(GONE);
                    return;
                }

                List<HomeTicketsModel> suggestedTickets = model.data;
                adapter.updateData(suggestedTickets);
                setVisibility(VISIBLE);
            }
        });
    }

}
