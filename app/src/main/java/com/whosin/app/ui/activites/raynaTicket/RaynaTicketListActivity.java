package com.whosin.app.ui.activites.raynaTicket;

import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import com.whosin.app.comman.Utils;
import com.whosin.app.databinding.ActivityTicketDetailBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.RaynaTicketManager;
import com.whosin.app.service.models.ContainerListModel;
import com.whosin.app.service.models.rayna.RaynaTicketDetailModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.comman.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class RaynaTicketListActivity extends BaseActivity {

    private ActivityTicketDetailBinding binding;

    private List<RaynaTicketDetailModel> ticketsModels = new ArrayList<>();

    private Call<ContainerListModel<RaynaTicketDetailModel>> service = null;

    private String searchQuery = "";

    private String type = "";

    private Runnable runnable = () -> updateList();
    private Handler handler = new Handler();

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------


    @Override
    protected void initUi() {


        binding.editTvSearch.setHint(getValue("search_tickets"));
        binding.emptyPlaceHolderView.setEmptyPlaceTxtTitle(getValue("no_ticket_detail"));

        ticketsModels = RaynaTicketManager.shared.raynaTicketList;

        binding.ticketRecyclerView.isVertical = true;
        binding.ticketRecyclerView.activity = activity;

        binding.ticketRecyclerView.setupData(RaynaTicketManager.shared.raynaTicketList,activity,true,true);


        String description = getIntent().getStringExtra("Description");
        type = getIntent().getStringExtra("type");

        binding.titleTv.setText(Html.fromHtml(description));
    }

    @Override
    protected void setListeners() {
        binding.ivClose.setOnClickListener(view -> {
            RaynaTicketManager.shared.raynaTicketList.clear();
            onBackPressed();
        });

        binding.editTvSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
 
            }

            @Override
            public void afterTextChanged(Editable editable) {
                searchQuery = editable.toString();
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, 400);

            }
        });

        binding.editTvSearch.setOnEditorActionListener((textView, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                Utils.hideKeyboard(activity);
                return true;
            }
            return false;
        });

    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityTicketDetailBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------


    private void updateList(){
        if (TextUtils.isEmpty(searchQuery)){
            if (ticketsModels == null || ticketsModels.isEmpty()) {
                binding.ticketRecyclerView.setVisibility(View.GONE);
                binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
            } else {
                binding.ticketRecyclerView.setVisibility(View.VISIBLE);
                binding.emptyPlaceHolderView.setVisibility(View.GONE);
                binding.ticketRecyclerView.isVertical = true;
                binding.ticketRecyclerView.activity = activity;
                binding.ticketRecyclerView.setupData(ticketsModels,activity,true,true);
//                ticketAdapter.updateData(ticketsModels);
            }
        }else {
            requestRaynaSearchList();
        }
    }


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------


    private void requestRaynaSearchList() {
        if (service != null) {
            service.cancel();
        }
        binding.searchProgressBar.setVisibility(View.VISIBLE);
        service = DataService.shared(activity).requestRaynaSearch(binding.editTvSearch.getText().toString(),1, type,new RestCallback<ContainerListModel<RaynaTicketDetailModel>>(this) {
            @Override
            public void result(ContainerListModel<RaynaTicketDetailModel> model, String error) {
                binding.searchProgressBar.setVisibility(View.GONE);
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (model.data != null && !model.data.isEmpty()) {
                    binding.ticketRecyclerView.setupData(model.data,activity,true,true);
                    binding.ticketRecyclerView.setVisibility(View.VISIBLE);
                    binding.emptyPlaceHolderView.setVisibility(View.GONE);
                } else {
                    binding.ticketRecyclerView.setVisibility(View.GONE);
                    binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
                }

            }
        });
    }

    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------



    // endregion
    // --------------------------------------

}