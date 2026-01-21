package com.whosin.app.ui.activites.venue.Bucket;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.whosin.app.R;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.databinding.FragmentEditBucketListDialogBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.CreateBucketListModel;
import com.whosin.app.service.models.MessageEvent;
import com.whosin.app.service.rest.RestCallback;

import org.greenrobot.eventbus.EventBus;


public class EditBucketListDialog extends DialogFragment {

    private FragmentEditBucketListDialogBinding binding;
    private CreateBucketListModel bucketModel;

    public CommanCallback<Boolean> callback;
    public EditBucketListDialog(CreateBucketListModel model) {
        this.bucketModel = model;

    }

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

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

    public void initUi(View view) {
        binding = FragmentEditBucketListDialogBinding.bind( view );
        getDialog().getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );

        binding.tvName.setText( bucketModel.getName() );
    }

    private void setListeners() {

        binding.tvCancel.setOnClickListener( view -> getDialog().onBackPressed() );

        binding.tvDone.setOnClickListener( view -> {
            requestUpdateBucket();
        } );

    }

    private int getLayoutRes() {
        return R.layout.fragment_edit_bucket_list_dialog;
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------


    private void requestUpdateBucket() {
        String name = binding.tvName.getText().toString();
        DataService.shared( requireActivity() ).requestBucketShare( bucketModel.getId(), "", name, "",  new RestCallback<ContainerModel<CreateBucketListModel>>(this) {
            @Override
            public void result(ContainerModel<CreateBucketListModel> model, String error) {
                if (!Utils.isNullOrEmpty( error ) || model == null) {
                    Toast.makeText( requireActivity(), R.string.service_message_something_wrong, Toast.LENGTH_SHORT ).show();
                    return;
                }
                EventBus.getDefault().post( new MessageEvent() );
                Toast.makeText( requireActivity(), model.message, Toast.LENGTH_SHORT ).show();
                if (callback != null) {
                    callback.onReceive(true);
                }
                dismiss();
            }
        } );
    }

/*
    private void requestBucketUpdate(List<ContactListModel> selectedContacts) {
        String userIds = TextUtils.join( ",", selectedContacts.stream().map( ContactListModel::getId ).collect( Collectors.toList() ) );

        DataService.shared( requireActivity() ).requestBucketShare( bucketModel.getId(), userIds, new RestCallback<ContainerModel<CreateBucketListModel>>() {
            @Override
            public void result(ContainerModel<CreateBucketListModel> model, String error) {

                if (!Utils.isNullOrEmpty( error ) || model == null) {
                    Toast.makeText( requireActivity(), error, Toast.LENGTH_SHORT ).show();
                    return;
                }

                Toast.makeText( requireActivity(), model.message, Toast.LENGTH_SHORT ).show();

            }
        } );
    }
*/


    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------


    // endregion
    // --------------------------------------


}