package com.whosin.app.ui.activites.venue.Bucket;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.tapadoo.alerter.Alerter;
import com.whosin.app.R;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.databinding.ExitFromBucketDialogBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.models.CommonModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.rest.RestCallback;


public class ExitFromBucketDialog extends DialogFragment {

    private ExitFromBucketDialogBinding binding;
    public CommanCallback<Boolean> callback;
    private String id = "";

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    public ExitFromBucketDialog(String id){

        this.id = id;
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

    private void initUi(View v) {

        binding = ExitFromBucketDialogBinding.bind( v );
        getDialog().getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
    }

    private void setListeners() {
        binding.tvDone.setOnClickListener( v->requestExitBucket() );
        binding.tvCancel.setOnClickListener( view -> dismiss() );

    }

    private int getLayoutRes() {
        return R.layout.exit_from_bucket_dialog;
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestExitBucket() {
        DataService.shared( requireActivity() ).requestBucketExit( id, new RestCallback<ContainerModel<CommonModel>>(this) {
            @Override
            public void result(ContainerModel<CommonModel> model, String error) {
                if (!Utils.isNullOrEmpty( error ) || model == null) {
                    Toast.makeText( requireActivity(), error, Toast.LENGTH_SHORT ).show();
                    return;
                }
                if (callback != null){
                    callback.onReceive(true);
                }
                dismiss();
                Alerter.create(requireActivity()).setTitle("Thank you!").setText(model.message).setTextAppearance(R.style.AlerterText).setTitleAppearance(R.style.AlerterTitle).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
            }
        } );
    }

    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------


    // endregion
    // --------------------------------------

}