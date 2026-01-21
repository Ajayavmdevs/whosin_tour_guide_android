package com.whosin.app.ui.activites.bucket;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.whosin.app.R;
import com.whosin.app.comman.Utils;
import com.whosin.app.databinding.DeleteBucketDialogBinding;
import com.whosin.app.databinding.MyOutingDialogBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.CreateBucketListModel;
import com.whosin.app.service.models.InviteFriendModel;
import com.whosin.app.service.models.MessageEvent;
import com.whosin.app.service.rest.RestCallback;

import org.greenrobot.eventbus.EventBus;


public class MyOutingDialog extends DialogFragment {
    private MyOutingDialogBinding binding;

    private InviteFriendModel inviteFriendModel;
    public MyOutingDialog(InviteFriendModel inviteFriendModel) {
        this.inviteFriendModel = inviteFriendModel;
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
        binding = MyOutingDialogBinding.bind( view );
        getDialog().getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );


    }

    public void setListeners() {

        binding.tvCancel.setOnClickListener(v -> {
            requestUpdateInviteStatus("out");
        });
        binding.tvChangeOwnership.setOnClickListener(v -> {
            TransferOwnershipBottomSheet transferOwnershipBottomSheet = new TransferOwnershipBottomSheet();
            transferOwnershipBottomSheet.inviteFriendModel = inviteFriendModel;
            transferOwnershipBottomSheet.show(getChildFragmentManager(), "1");
            dismiss();
        });


    }

    public int getLayoutRes() {
        return R.layout.my_outing_dialog;
    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestUpdateInviteStatus(String Status) {
        DataService.shared(getActivity()).requestUpdateInviteStatus(inviteFriendModel.getId(), Status, new RestCallback<ContainerModel<InviteFriendModel>>(this) {
            @Override
            public void result(ContainerModel<InviteFriendModel> model, String error) {
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(getActivity(), model.message, Toast.LENGTH_SHORT).show();
                 dismiss();
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