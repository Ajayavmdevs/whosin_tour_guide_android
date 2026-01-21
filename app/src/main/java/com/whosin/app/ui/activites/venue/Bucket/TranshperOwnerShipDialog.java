package com.whosin.app.ui.activites.venue.Bucket;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.tapadoo.alerter.Alerter;
import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.ChangeOwnershipContactFreindItemBinding;
import com.whosin.app.databinding.TranshperOwnershipDialogBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.models.BucketListModel;
import com.whosin.app.service.models.ContactListModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.CreateBucketListModel;
import com.whosin.app.service.rest.RestCallback;

import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;


public class TranshperOwnerShipDialog extends DialogFragment {

    private TranshperOwnershipDialogBinding binding;

    private PlayerAdapter<ContactListModel>playerAdapter = new PlayerAdapter();
    public CommanCallback<Boolean> callback;

    private BucketListModel bucketListModel ;
    private String id = "";
    private List<ContactListModel> userDetailModel ;

    List<ContactListModel> shareUser;

    private String selectedUser="";

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    public TranshperOwnerShipDialog(String id, List<ContactListModel> userDetailModel,BucketListModel bucketListModel) {

        this.id = id;

        this.userDetailModel = userDetailModel;

        this.bucketListModel = bucketListModel;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate( getLayoutRes(), container, false );
        initUi( view );
        setListeners();
        return view;
    }

    private void initUi(View view) {
        binding = TranshperOwnershipDialogBinding.bind( view );
        getDialog().getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
        setInviteFriendAdapter(userDetailModel);
        Glide.with( requireActivity() ).load( R.drawable.icon_close_btn ).into( binding.ivClose );
    }

    private void setListeners() {
        binding.ivClose.setOnClickListener( v -> dismiss());
    }

    private int getLayoutRes() {
        return R.layout.transhper_ownership_dialog;
    }

    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new BottomSheetDialog( requireActivity(), R.style.BottomSheetDialogThemeNoFloating );
    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void setInviteFriendAdapter(List<ContactListModel> strings) {

        binding.contactRecycler.setLayoutManager( new LinearLayoutManager( requireActivity(), LinearLayoutManager.VERTICAL, false ) );
        binding.contactRecycler.setAdapter( playerAdapter );

        List<ContactListModel> shareUser = bucketListModel.getUsers().stream().filter( p -> strings.contains( p.getId() ) ).collect( Collectors.toList() );
        StringJoiner nameShare = new StringJoiner( ", " );
        for(ContactListModel model : shareUser) {
            nameShare.add( model.getFirstName());
            nameShare.add( model.getImage() );
           // binding.shareBtn.setOnClickListener(v -> requestTransferOwnerShip(selectedUser) );
        }
        if (bucketListModel.getUsers() != null) {
            binding.shareBtn.setOnClickListener(v -> requestTransferOwnerShip(selectedUser) );
            playerAdapter.updateData( strings );
        } else {
            Toast.makeText( requireActivity(), "Data Null", Toast.LENGTH_SHORT ).show();
        }


    }


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestTransferOwnerShip(String userId) {
        binding.progress.setVisibility(View.VISIBLE);
        DataService.shared( requireActivity() ).requestTransferBucketOwnerShip( id, userId, new RestCallback<ContainerModel<CreateBucketListModel>>(this) {
            @Override
            public void result(ContainerModel<CreateBucketListModel> model, String error) {
                if (!Utils.isNullOrEmpty( error ) || model == null) {
                    Toast.makeText( requireActivity(), error, Toast.LENGTH_SHORT ).show();
                    return;
                }
                Alerter.create(requireActivity()).setTitle("Thank you!").setText(model.message).setTextAppearance(R.style.AlerterText).setTitleAppearance(R.style.AlerterTitle).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                if (callback != null) {
                    callback.onReceive(true);
                }
                binding.progress.setVisibility(View.GONE);
                dismiss();

            }
        } );
    }


    public class PlayerAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {
        private int selectedPosition = -1;

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = UiUtils.getViewBy( parent, R.layout.change_ownership_contact_freind_item );

            return new ViewHolder( view );
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            ContactListModel model = (ContactListModel) getItem( position );

            Graphics.loadImageWithFirstLetter( model.getImage(), viewHolder.vBinding.ivUserProfile, model.getFirstName() );
            viewHolder.vBinding.tvUserName.setText( model.getFirstName() );
            viewHolder.vBinding.ivCheck.setChecked(position == selectedPosition);

            viewHolder.vBinding.ivCheck.setOnCheckedChangeListener((compoundButton, b) -> {
                if (b) {
                    selectedPosition = viewHolder.getAdapterPosition();
                    selectedUser = model.getId();
                    notifyDataSetChanged();
                }
            });

        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            private final ChangeOwnershipContactFreindItemBinding vBinding;

            public ViewHolder(@NonNull View itemView) {
                super( itemView );
                vBinding = ChangeOwnershipContactFreindItemBinding.bind( itemView );
            }
        }

    }

}