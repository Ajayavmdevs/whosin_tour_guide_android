package com.whosin.app.ui.activites.travelDeskTicket;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.whosin.app.R;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.databinding.FragmentAddOtherLocationDialogBinding;
import com.whosin.app.databinding.FragmentEditBucketListDialogBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.CreateBucketListModel;
import com.whosin.app.service.models.MessageEvent;
import com.whosin.app.service.rest.RestCallback;

import org.greenrobot.eventbus.EventBus;

import java.util.Objects;


public class AddOtherLocationDialog extends DialogFragment {

    private FragmentAddOtherLocationDialogBinding binding;

    public CommanCallback<String> callback;

    public String location = "";


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

        binding = FragmentAddOtherLocationDialogBinding.bind( view );

        binding.tvDialogTitle.setText(Utils.getLangValue("entre_pickup_location"));
        binding.tvCancel.setText(Utils.getLangValue("cancel"));
        binding.tvDone.setText(Utils.getLangValue("submit"));
        binding.tvName.setHint(Utils.getLangValue("entre_pickup_location"));

        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );

        if (!TextUtils.isEmpty(location)){
            binding.tvName.setText(location);
        }
        binding.tvName.requestFocus();

    }

    private void setListeners() {

        binding.tvCancel.setOnClickListener( view -> dismiss() );

        binding.tvDone.setOnClickListener( view -> {
            if (TextUtils.isEmpty(binding.tvName.getText().toString())) return;
            callback.onReceive(binding.tvName.getText().toString());
            dismiss();
        } );

    }

    private int getLayoutRes() {
        return R.layout.fragment_add_other_location_dialog;
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------


    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------


    // endregion
    // --------------------------------------


}