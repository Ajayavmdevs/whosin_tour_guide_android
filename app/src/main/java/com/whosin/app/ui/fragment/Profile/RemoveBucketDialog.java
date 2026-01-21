package com.whosin.app.ui.fragment.Profile;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.  DialogFragment;

import com.google.gson.JsonObject;
import com.whosin.app.R;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.databinding.FragmentCreateBucketDialogBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.CreateBucketListModel;
import com.whosin.app.service.rest.RestCallback;

public class RemoveBucketDialog extends DialogFragment {

    private FragmentCreateBucketDialogBinding binding;

    public String offerId = "";

    public String eventId = "";

    public String activityId = "";

    private String bucketId = "";

    public CommanCallback<Boolean> callback;

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    public RemoveBucketDialog(String bucketId) {
        this.bucketId = bucketId;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate( getLayoutRes(), container, false );
        initUi( view );
        setListeners();
        return view;
    }

    private int getLayoutRes() {
        return R.layout.fragment_create_bucket_dialog;
    }

    private void initUi(View view) {
        binding = FragmentCreateBucketDialogBinding.bind( view );
        getDialog().getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
    }

    private void setListeners() {

        binding.tvYes.setOnClickListener( v -> {
            binding.progress.setVisibility(View.VISIBLE);
            if (eventId.isEmpty() && activityId.isEmpty()) {
                requestAddBucket(bucketId, offerId, "0", "0");
            } else if (activityId.isEmpty() && offerId.isEmpty()) {
                requestAddBucket(bucketId, "0", "0", eventId);
            } else {
                requestAddBucket(bucketId, "0", activityId, "0");
            }

        } );

        binding.tvCancel.setOnClickListener( view -> getDialog().onBackPressed() );


    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------



    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestAddBucket(String bucketId, String offerId ,String activityId , String eventId) {
//        Graphics.showProgress(getActivity());
        JsonObject object = new JsonObject();
        object.addProperty("id", bucketId);
        object.addProperty("action", "delete");
        if (eventId.equals("0") && activityId.equals("0")){
            object.addProperty("offerId", offerId);
        } else if (activityId.equals("0") && offerId.equals("0")) {
            object.addProperty("eventId", eventId);
        }else {
            object.addProperty("activityId", activityId);
        }
        DataService.shared(requireActivity()).requestUpdateBucket(object, new RestCallback<ContainerModel<CreateBucketListModel>>(this) {
            @Override
            public void result(ContainerModel<CreateBucketListModel> model, String error) {
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(requireActivity(), error, Toast.LENGTH_SHORT).show();
                    return;
                }
                model.getData();
                Toast.makeText(requireActivity(), model.message, Toast.LENGTH_SHORT).show();
//                Graphics.hideProgress(getActivity());
                if (callback != null) {
                    callback.onReceive(true);
                }
                binding.progress.setVisibility(View.GONE);
                dismiss();
            }
        });
    }


    // --------------------------------------
    // region Adapter
    // --------------------------------------


    // endregion
    // --------------------------------------
}