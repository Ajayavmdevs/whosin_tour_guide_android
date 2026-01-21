package com.whosin.app.ui.fragment.Bucket.event;

import android.view.View;
import android.widget.Toast;
import com.whosin.app.R;
import com.whosin.app.comman.Utils;
import com.whosin.app.databinding.FragmentUpcomingEventBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.models.BucketEventListModel;
import com.whosin.app.service.models.ContainerListModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.fragment.comman.BaseFragment;



public class UpcomingEventFragment extends BaseFragment {

    private FragmentUpcomingEventBinding binding;

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    public void initUi(View view) {
        binding = FragmentUpcomingEventBinding.bind( view );
        setAdapter();
        showProgress();
        requestEvents();
    }

    @Override
    public void setListeners() {
        binding.swipeRefreshLayout.setOnRefreshListener( () -> requestEvents() );
    }

    @Override
    public void populateData(boolean getDataFromServer) {
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_upcoming_event;
    }

    @Override
    public void onResume() {
        super.onResume();
        requestEvents();
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------
    private void setAdapter() {

    }

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestEvents() {

        DataService.shared( requireActivity() ).requestUpcomingHistory( new RestCallback<ContainerListModel<BucketEventListModel>>(this) {
            @Override
            public void result(ContainerListModel<BucketEventListModel> model, String error) {
                hideProgress();
                binding.swipeRefreshLayout.setRefreshing( false );
                if (!Utils.isNullOrEmpty( error ) || model == null) {
                    Toast.makeText( requireActivity(), error, Toast.LENGTH_SHORT ).show();
                    return;
                }

                if (model.data != null && !model.data.isEmpty()) {
                    binding.emptyPlaceHolderView.setVisibility( View.GONE );
                    binding.upcomingRecycler.setVisibility( View.VISIBLE );
                } else {
                    binding.upcomingRecycler.setVisibility( View.GONE );
                    binding.emptyPlaceHolderView.setVisibility( View.VISIBLE );
                }
            }
        } );
    }

    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------


    // --------------------------------------
    // endregion
}