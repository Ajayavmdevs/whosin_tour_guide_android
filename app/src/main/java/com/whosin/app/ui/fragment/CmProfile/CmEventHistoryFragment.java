package com.whosin.app.ui.fragment.CmProfile;

import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.View;
import android.widget.Toast;

import com.whosin.app.R;
import com.whosin.app.comman.Utils;
import com.whosin.app.databinding.FragmentCmEventHistoryBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.models.ContainerListModel;
import com.whosin.app.service.models.PromoterEventModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.adapter.ComplementaryEventsListAdapter;
import com.whosin.app.ui.fragment.comman.BaseFragment;

import java.util.ArrayList;
import java.util.List;

public class CmEventHistoryFragment extends BaseFragment {

    private FragmentCmEventHistoryBinding binding;

    private ComplementaryEventsListAdapter<PromoterEventModel> historyListAdapter ;

    private List<PromoterEventModel> historyEventList = new ArrayList<>();

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    public void initUi(View view) {
        binding = FragmentCmEventHistoryBinding.bind(view);

        binding.txtHighLite.setText(getValue("event_history"));
        binding.emptyPlaceHolderView.setEmptyPlaceTxtTitle(getValue("you_not_applied_any_event_yet"));

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false));
        historyListAdapter = new ComplementaryEventsListAdapter<>(requireActivity(),false);
        binding.recyclerView.setAdapter(historyListAdapter);

        requestPromoterEventHistoryUser(true,false);

    }

    @Override
    public void setListeners() {

    }

    @Override
    public void populateData(boolean getDataFromServer) {

    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_cm_event_history;
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestPromoterEventHistoryUser(boolean isShowProgress, boolean isPaggination) {
        if (isShowProgress) {
            showProgress();
        }
        DataService.shared(requireActivity()).requestPromoterEventHistoryUser(1, new RestCallback<ContainerListModel<PromoterEventModel>>(this) {
            @Override
            public void result(ContainerListModel<PromoterEventModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(requireActivity(), error, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (model.data != null && !model.data.isEmpty()) {
                    historyEventList.addAll(model.data);
                }

                historyListAdapter.updateData(historyEventList);
                binding.recyclerView.setVisibility(historyEventList.isEmpty() ? View.GONE : View.VISIBLE);
                binding.emptyPlaceHolderView.setVisibility(historyEventList.isEmpty() ? View.VISIBLE : View.GONE);
                binding.txtHighLite.setVisibility(historyEventList.isEmpty() ? View.GONE : View.VISIBLE);
            }
        });
    }

    // --------------------------------------
    // region Adapter
    // --------------------------------------


    // endregion
    // --------------------------------------
}