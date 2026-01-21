package com.whosin.app.ui.fragment.wallet;

import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.whosin.app.R;
import com.whosin.app.comman.AppExecutors;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.ItemBottomSheetPackageBinding;
import com.whosin.app.databinding.PackageBottomSheetBinding;
import com.whosin.app.databinding.SendGiftBottomSheetBinding;
import com.whosin.app.databinding.SendGiftDialogBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.models.CommanMsgModel;
import com.whosin.app.service.models.ContactListModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.ItemModel;
import com.whosin.app.service.models.MyWalletModel;
import com.whosin.app.service.models.PackageModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.venue.Bucket.ContactShareBottomSheet;
import com.whosin.app.ui.activites.wallet.RedeemActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


public class SendGiftBottomSheet extends DialogFragment {

    private SendGiftBottomSheetBinding vBinding;
    public MyWalletModel myWalletModel;
    public PackageModel packageModel;
    public CommanCallback<Boolean> callback;
    private int val = 0;
    private String friendId = "";
    public List<ContactListModel> selectedUsers = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate( getLayoutRes(), container, false );
        initUi( view );
        setListener();
        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes(params);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable( Color.TRANSPARENT));
        ((BottomSheetDialog) getDialog()).getBehavior().setState(STATE_EXPANDED);
    }
    @SuppressLint("SetTextI18n")
    public void initUi(View view) {
        vBinding = SendGiftBottomSheetBinding.bind( view );

        if (myWalletModel.getType().equals("offer")) {
            vBinding.venueContainer.setVisibility(View.GONE);
            vBinding.linear.setVisibility(View.VISIBLE);

            if (myWalletModel.getOffer() != null){
                Graphics.loadImage(myWalletModel.getOffer().getImage(), vBinding.image);
                vBinding.tvTitle.setText(myWalletModel.getOffer().getTitle());
                vBinding.tvAddress.setText(myWalletModel.getOffer().getDescription());
            }


            vBinding.subTitle.setText(packageModel.getTitle());

            if (!TextUtils.isEmpty(packageModel.getDescription())) {
                vBinding.tvActivityAddress.setText(Utils.notNullString(packageModel.getDescription()));
                vBinding.tvActivityAddress.setVisibility(View.VISIBLE);
            } else {
                vBinding.tvActivityAddress.setVisibility(View.GONE);
            }

            if (packageModel.getDiscount().equals("0")){
                vBinding.tvDiscount.setVisibility(View.GONE);
            }else {
                vBinding.tvDiscount.setVisibility(View.VISIBLE);
                vBinding.tvDiscount.setText(Utils.addPercentage(packageModel.getDiscount()));
            }


            vBinding.tvDate.setText(convertEndDate(myWalletModel.getOffer().getEndTime()));
            vBinding.tvRemainingQty.setText(packageModel.getRemainingQty() + "");



        } else if (myWalletModel.getType().equals("deal")) {
            if (myWalletModel.getDeal().getVenue() != null) {
                vBinding.venueContainer.setVenueDetail(myWalletModel.getDeal().getVenue());
            }
            vBinding.subTitle.setText(myWalletModel.getDeal().getTitle());
            vBinding.tvDiscount.setText(Utils.addPercentage(myWalletModel.getDeal().getDiscountValue()));

            vBinding.tvDate.setText(convertEndDate(myWalletModel.getDeal().getEndDate()));

            vBinding.tvActivityAddress.setText(myWalletModel.getDeal().getDescription());
            vBinding.tvRemainingQty.setText(packageModel.getRemainingQty() + "");
        } else if (myWalletModel.getType().equals("event")) {
            if (myWalletModel.getEvent().getVenue() != null) {
                vBinding.venueContainer.setVenueDetail(myWalletModel.getEvent().getVenue());
            }
            vBinding.subTitle.setText(packageModel.getTitle());
            if (!TextUtils.isEmpty(packageModel.getDescription())) {
                vBinding.tvActivityAddress.setText(Utils.notNullString(packageModel.getDescription()));
                vBinding.tvActivityAddress.setVisibility(View.VISIBLE);
            } else {
                vBinding.tvActivityAddress.setVisibility(View.GONE);
            }

            if (packageModel.getDiscount().equals("0")){
                vBinding.tvDiscount.setVisibility(View.GONE);
            }else {
                vBinding.tvDiscount.setVisibility(View.VISIBLE);
                vBinding.tvDiscount.setText(Utils.addPercentage(packageModel.getDiscount()));
            }
//            vBinding.tvDiscount.setText(Utils.addPercentage(packageModel.getDiscount()));

            vBinding.tvRemainingQty.setText(packageModel.getRemainingQty() + "");
            vBinding.tvDate.setText(convertEndDate(myWalletModel.getEvent().getEventTime()));
        } else if (myWalletModel.getType().equals("activity")) {

            vBinding.tvDiscount.setVisibility(View.GONE);
            vBinding.subTitle.setText(myWalletModel.getActivity().getName());


            String detectedFormat = Utils.detectDateFormat(myWalletModel.getItems().get(0).getDate());
            if (detectedFormat != null) {
                String formattedDate = Utils.convertDateFormat(myWalletModel.getItems().get(0).getDate(), detectedFormat, "dd/MM/yyyy");
                vBinding.tvDate.setText(formattedDate);
            } else {
                vBinding.tvDate.setText("");
            }



            vBinding.linear.setVisibility(View.VISIBLE);
            vBinding.venueContainer.setVisibility(View.GONE);
            Graphics.loadImageWithFirstLetter(myWalletModel.getActivity().getProvider().getLogo(), vBinding.image, myWalletModel.getActivity().getProvider().getName());
            if (myWalletModel.getActivity().getProvider() != null){
                vBinding.tvTitle.setText(myWalletModel.getActivity().getProvider().getName());
                vBinding.tvAddress.setText(myWalletModel.getActivity().getProvider().getAddress());
                vBinding.tvActivityAddress.setText(myWalletModel.getActivity().getDescription());
            }


            vBinding.tvRemainingQty.setText(packageModel.getRemainingQty() + "");
        }


    }

    public void setListener() {
        Glide.with( requireActivity() ).load( R.drawable.icon_close_btn ).into( vBinding.ivClose );
        vBinding.ivClose.setOnClickListener( view -> dismiss() );

        vBinding.ivPlus.setOnClickListener( v -> {
            if (val < packageModel.getRemainingQty()) {
                val++;
                vBinding.tvTotal.setText( String.valueOf( val ) );
            }
        } );


        vBinding.ivMinus.setOnClickListener( v -> {
            if (val == 0) {
                Toast.makeText( requireActivity(), "Please Select qty", Toast.LENGTH_SHORT ).show();
            } else {
                val--;
                vBinding.tvTotal.setText( String.valueOf( val ) );
            }
        } );

        vBinding.ivAdd.setOnClickListener( view -> {
            Utils.preventDoubleClick( view );

            openContactDialog();
        } );

        vBinding.tvEdit.setOnClickListener( view -> {
            Utils.preventDoubleClick( view );
            openContactDialog();
        } );

        vBinding.btnSendGift.setOnClickListener( view -> {
            requestSendGift();
        } );
    }

    public int getLayoutRes() {
        return R.layout.send_gift_bottom_sheet;
    }


    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new BottomSheetDialog( requireActivity(), R.style.BottomSheetDialogThemeNoFloating );
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------


    public String convertEndDate(String endDateString) {
        try {
            Date date = Utils.stringToDate(endDateString, "yyyy-MM-dd");
            return Utils.formatDate(date, "dd/MM/yyyy");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }





    private void openContactDialog() {
        ContactShareBottomSheet contactDialog = new ContactShareBottomSheet();
        contactDialog.myWallet = true;
        contactDialog.defaultUsersList = selectedUsers.stream().map( ContactListModel::getId ).collect( Collectors.toList() );
        contactDialog.setShareListener( data -> {
            selectedUsers = data;
            AppExecutors.get().mainThread().execute( () -> {
                selectedUsers = data;
                if (selectedUsers != null) {
                    vBinding.ivAdd.setVisibility( View.GONE );
                    vBinding.layoutContact.setVisibility( View.VISIBLE );
                    for (ContactListModel contact : selectedUsers) {
                        friendId = contact.getId();
                        vBinding.tvUserName.setText( contact.getFullName() );
                        if (!Utils.isNullOrEmpty( contact.getEmail() )) {
                            vBinding.tvUserNumber.setVisibility( View.VISIBLE );
                            vBinding.tvUserNumber.setText( contact.getEmail() );
                        } else {
                            vBinding.tvUserNumber.setVisibility( View.GONE );
                        }

                        Graphics.loadImageWithFirstLetter( contact.getImage(), vBinding.ivUserProfile, contact.getFullName() );
                    }
                } else {
                    vBinding.ivAdd.setVisibility( View.VISIBLE );
                    vBinding.layoutContact.setVisibility( View.GONE );
                }
            } );
        } );

        contactDialog.show( getChildFragmentManager(), "1" );

    }

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------
    private void requestSendGift() {
        if (vBinding.tvTotal.getText().toString().equals("0")) {
            Toast.makeText( getContext(), "Please add Quantity", Toast.LENGTH_SHORT ).show();
            return;
        }
        JsonObject object = new JsonObject();
        if (TextUtils.isEmpty(friendId)) return;
        object.addProperty( "friendId", friendId );
        object.addProperty( "type",  myWalletModel.getType() );

        if ( myWalletModel.getType().equals("offer")) {
            if (TextUtils.isEmpty(packageModel.getId())) return;
            object.addProperty("packageId", packageModel.getId());
            object.addProperty("qty", vBinding.tvTotal.getText().toString());
        } else if ( myWalletModel.getType().equals("activity")) {
            if (TextUtils.isEmpty(myWalletModel.getActivityId())) return;
            if (TextUtils.isEmpty(myWalletModel.getItems().get(0).getDate())) return;
            if (TextUtils.isEmpty(myWalletModel.getItems().get(0).getTime())) return;
            object.addProperty("activityId", myWalletModel.getActivityId());
            object.addProperty("date", myWalletModel.getItems().get(0).getDate());
            object.addProperty("time", myWalletModel.getItems().get(0).getTime());
            object.addProperty("qty", vBinding.tvTotal.getText().toString());
        } else if ( myWalletModel.getType().equals("event")) {
            if (TextUtils.isEmpty(packageModel.getId())) return;
            object.addProperty("packageId", packageModel.getId());
            object.addProperty("qty", vBinding.tvTotal.getText().toString());
        } else {
            if (TextUtils.isEmpty(myWalletModel.getDealId())) return;
            object.addProperty("dealId", myWalletModel.getDealId());
            object.addProperty("qty", vBinding.tvTotal.getText().toString());
        }

        object.addProperty("giftMessage",vBinding.tvGiftMessage.getText().toString());

        vBinding.progressBar.setVisibility(View.VISIBLE);
        DataService.shared( getActivity() ).requestSendGift( object, new RestCallback<ContainerModel<CommanMsgModel>>(this) {
            @Override
            public void result(ContainerModel<CommanMsgModel> model, String error) {
                vBinding.progressBar.setVisibility(View.GONE);
                if (!Utils.isNullOrEmpty( error ) || model == null) {
                    Toast.makeText( getContext(), error, Toast.LENGTH_SHORT ).show();
                    return;
                }
                Toast.makeText( getContext(), model.message, Toast.LENGTH_SHORT ).show();
                if (callback != null) {
                    callback.onReceive(true);
                }
                dismiss();
            }
        } );
    }

    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

}