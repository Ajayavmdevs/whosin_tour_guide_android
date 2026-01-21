package com.whosin.app.ui.activites.Promoter;

import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.whosin.app.comman.AppConstants;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.databinding.ActivityMyRingDetailBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.Repository.ContactRepository;
import com.whosin.app.service.manager.PromoterProfileManager;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.ContainerListModel;
import com.whosin.app.service.models.PromoterCirclesModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.comman.BaseActivity;
import com.whosin.app.ui.adapter.MyCirclesDetailAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MyRingDetailActivity extends BaseActivity {

    private ActivityMyRingDetailBinding binding;

    private MyCirclesDetailAdapter<UserDetailModel> myCirclesDetailAdapter;

    private List<UserDetailModel> myRingList = new ArrayList<>();

    private boolean isFormSubAdmin = false;
    private String searchQuery = "";
    private Runnable runnable = () -> updateList();
    private Handler handler = new Handler();

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    protected void initUi() {

        binding.tvMainTitle.setText(getValue("my_rings"));
        binding.edtSearch.setHint(getValue("find_friends"));
        binding.emptyPlaceHolderView.setEmptyPlaceTxtTitle(getValue("ring_list_empty"));

        isFormSubAdmin = getIntent().getBooleanExtra("isFromSubAdmin",false);

        if (isFormSubAdmin){
            myCirclesDetailAdapter = new MyCirclesDetailAdapter<>(activity, getSupportFragmentManager(),AppConstants.ContextType.SUB_ADMIN);
        }else {
            myCirclesDetailAdapter = new MyCirclesDetailAdapter<>(activity , getSupportFragmentManager(),"", AppConstants.ContextType.MY_RING, data -> {
                if (data) {
                    binding.edtSearch.setText("");
                    binding.edtSearch.clearFocus();
                    binding.edtSearch.setHint(getValue("find_friends"));
                    Utils.hideKeyboard(activity);
                    EventBus.getDefault().post(new PromoterCirclesModel());
                    requestPromoterMyRingMember();
                }
            });
        }


        binding.usersDetailRecycler.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        binding.usersDetailRecycler.setAdapter(myCirclesDetailAdapter);

        requestPromoterMyRingMember();
    }

    @Override
    protected void setListeners() {
        binding.ivClose.setOnClickListener(view -> {
            onBackPressed();
        });

        binding.edtSearch.addTextChangedListener(new TextWatcher() {
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
                handler.postDelayed(runnable, 200);

            }
        });


    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityMyRingDetailBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    // endregion
    // --------------------------------------
    // region Public
    // --------------------------------------


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void updateList() {
        List<UserDetailModel> displayList;

        if (TextUtils.isEmpty(searchQuery)) {
            displayList = myRingList;
        } else {
            displayList = myRingList.stream().filter(model -> model.getFullName().trim().toLowerCase().contains(searchQuery.toLowerCase().trim())).collect(Collectors.toList());
        }

        if (!displayList.isEmpty()) {
            myCirclesDetailAdapter.updateData(displayList);
            binding.usersDetailRecycler.setVisibility(View.VISIBLE);
            binding.emptyPlaceHolderView.setVisibility(View.GONE);
        } else {
            binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
            binding.usersDetailRecycler.setVisibility(View.GONE);
        }


    }


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    public void requestPromoterMyRingMember() {
        showProgress();
        DataService.shared(activity).requestPromoterMyRingMember(new RestCallback<ContainerListModel<UserDetailModel>>(null) {
            @Override
            public void result(ContainerListModel<UserDetailModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (model.data != null && !model.data.isEmpty()) {
                    myRingList.clear();
                    myRingList.addAll(model.data);
                    myCirclesDetailAdapter.updateData(model.data);
                    binding.emptyPlaceHolderView.setVisibility(View.GONE);
                } else {
                    binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
                    binding.usersDetailRecycler.setVisibility(View.GONE);
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