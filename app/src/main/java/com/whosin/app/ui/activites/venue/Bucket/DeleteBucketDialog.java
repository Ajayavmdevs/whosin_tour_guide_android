package com.whosin.app.ui.activites.venue.Bucket;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.whosin.app.R;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.databinding.DeleteBucketDialogBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.TranslationManager;
import com.whosin.app.service.models.CreateBucketListModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.MessageEvent;
import com.whosin.app.service.rest.RestCallback;

import org.greenrobot.eventbus.EventBus;


public class DeleteBucketDialog extends DialogFragment {

    private DeleteBucketDialogBinding binding;
    private String bucketId = "";
    public CommanCallback<Boolean> callback;
    public DeleteBucketDialog(String id) {
        this.bucketId = id;
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


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------


    public void initUi(View view) {
        binding = DeleteBucketDialogBinding.bind( view );
        getDialog().getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );

        binding.tvConfirmTitle.setText(TranslationManager.shared.get("delete_bucket_alert"));
    }

    public void setListeners() {
        binding.tvCancel.setOnClickListener( view -> getDialog().onBackPressed() );
        binding.tvYes.setOnClickListener( view -> requestRemoveBucket() );

    }

    public int getLayoutRes() {
        return R.layout.delete_bucket_dialog;
    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------


    private void requestRemoveBucket() {
        binding.progress.setVisibility( View.VISIBLE );
        DataService.shared( requireContext() ).requestRemoveBucket( bucketId, new RestCallback<ContainerModel<CreateBucketListModel>>(this) {
            @Override
            public void result(ContainerModel<CreateBucketListModel> model, String error) {
                if (!Utils.isNullOrEmpty( error ) || model == null) {
                    Toast.makeText( requireActivity(), error, Toast.LENGTH_SHORT ).show();
                    return;
                }
                model.getData();
                Toast.makeText( requireContext(), model.message, Toast.LENGTH_SHORT ).show();
                if (callback != null) {
                    callback.onReceive(true);
                }
                binding.progress.setVisibility( View.GONE );
                dismiss();
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