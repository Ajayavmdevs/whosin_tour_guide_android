package com.whosin.app.ui.activites.bucket;

import android.annotation.SuppressLint;
import android.app.Dialog;
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
import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.BooleanResult;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.comman.ui.roundcornerlayout.CornerType;
import com.whosin.app.databinding.TransferOwnershipBottomsheetBinding;
import com.whosin.app.databinding.TransferOwnershipListItemBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.ContactListModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.InviteFriendModel;
import com.whosin.app.service.rest.RestCallback;

public class TransferOwnershipBottomSheet extends DialogFragment {

    private TransferOwnershipBottomsheetBinding binding;
    public InviteFriendModel inviteFriendModel;
    public BooleanResult callback;
    private final InvitedPeopleAdapter<ContactListModel> invitedPeopleAdapter = new InvitedPeopleAdapter<>();
    public String newOwnerId="";


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutRes(), container, false);
        initUi(view);
        setListener();
        return view;

    }

    public void initUi(View view) {
        binding = TransferOwnershipBottomsheetBinding.bind(view);
        binding.followingList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        binding.followingList.setAdapter(invitedPeopleAdapter);
        inviteFriendModel.getInvitedUser().removeIf(contact -> contact.getUserId().equals(SessionManager.shared.getUser().getId()));
        invitedPeopleAdapter.updateData(inviteFriendModel.getInvitedUser());

        if (!inviteFriendModel.getInvitedUser().isEmpty() && inviteFriendModel.getInvitedUser() != null){
            binding.followingList.setVisibility(View.VISIBLE);
            binding.emptyPlaceHolderView.setVisibility(View.GONE);
        } else {
            binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
            binding.followingList.setVisibility(View.GONE);
        }

    }

    public void setListener() {
        Glide.with(requireActivity()).load(R.drawable.icon_close_btn).into(binding.ivClose);
        binding.ivClose.setOnClickListener(view -> dismiss());

        binding.transferBtn.setOnClickListener(v -> {
            if (!newOwnerId.isEmpty()) {
                requestChangeOwnership();
            }
            else {
                Toast.makeText(requireActivity(), "Please select user", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public int getLayoutRes() {
        return R.layout.transfer_ownership_bottomsheet;
    }


    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new BottomSheetDialog(requireActivity(), R.style.BottomSheetDialogThemeNoFloating);
    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------


    private void requestChangeOwnership( ) {
        DataService.shared(requireActivity()).requestChangeOwnership(newOwnerId,inviteFriendModel.getId(), new RestCallback<ContainerModel<InviteFriendModel>>(this) {
            @Override
            public void result(ContainerModel<InviteFriendModel> model, String error) {

                if (!Utils.isNullOrEmpty(error) || model == null) {
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                    }
                    return;
                }

                if (callback != null){
                    callback.success(true, "");
                }
                dismiss();
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), model.message, Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    public class InvitedPeopleAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {
        private int selectedPosition = -1;
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.transfer_ownership_list_item));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            ContactListModel model = (ContactListModel) getItem(position);

            viewHolder.binding.tvUserName.setText(model.getFirstName() + " " + model.getLastName());
            Graphics.loadImageWithFirstLetter(model.getImage(), viewHolder.binding.ivUserProfile, model.getFirstName());
            if(selectedPosition == position){
                viewHolder.binding.ivCheck.setChecked(true);
            }
            else {
                viewHolder.binding.ivCheck.setChecked(false);
            }

            viewHolder.binding.contactLayout.setCornerRadius(0, CornerType.ALL);
            boolean isFirstCell = false;
            boolean isLastCell = getItemCount() - 1 == position;

            if (position == 0) {
                isFirstCell = true;
            }

//            }
            float cornerRadius = getActivity().getResources().getDimensionPixelSize(com.intuit.sdp.R.dimen._10sdp);
            if (isFirstCell) {
                viewHolder.binding.contactLayout.setCornerRadius(cornerRadius, CornerType.TOP_LEFT);
                viewHolder.binding.contactLayout.setCornerRadius(cornerRadius, CornerType.TOP_RIGHT);
            }
            if (isLastCell) {
                viewHolder.binding.contactLayout.setCornerRadius(cornerRadius, CornerType.BOTTOM_RIGHT);
                viewHolder.binding.contactLayout.setCornerRadius(cornerRadius, CornerType.BOTTOM_LEFT);
            }
            viewHolder.binding.view.setVisibility(isLastCell ? View.GONE  : View.VISIBLE);

            viewHolder.binding.ivCheck.setOnClickListener(v -> {
                newOwnerId = model.getUserId();
                selectedPosition= position;
                notifyDataSetChanged();
            });

            viewHolder.itemView.setOnClickListener(v -> {
                newOwnerId = model.getUserId();
                selectedPosition= position;
                notifyDataSetChanged();
            });
        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            private final TransferOwnershipListItemBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = TransferOwnershipListItemBinding.bind(itemView);
            }
        }
    }

    // endregion
    // --------------------------------------

}