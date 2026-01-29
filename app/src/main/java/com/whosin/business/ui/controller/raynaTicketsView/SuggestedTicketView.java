package com.whosin.business.ui.controller.raynaTicketsView;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.whosin.business.R;
import com.whosin.business.comman.HorizontalSpaceItemDecoration;
import com.whosin.business.comman.ui.VerticalSpaceItemDecoration;
import com.whosin.business.databinding.ViewSuggestedTicketsBinding;
import com.whosin.business.service.DataService;
import com.whosin.business.service.models.ContainerListModel;
import com.whosin.business.service.models.HomeTicketsModel;
import com.whosin.business.service.rest.RestCallback;
import com.whosin.business.ui.adapter.raynaTicketAdapter.SuggestedTicketAdapter;

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
