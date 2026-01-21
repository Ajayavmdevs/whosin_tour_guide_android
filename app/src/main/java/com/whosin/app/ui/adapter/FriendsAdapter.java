package com.whosin.app.ui.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.comman.ui.roundcornerlayout.CornerType;
import com.whosin.app.databinding.InvitedGuestListItemBinding;
import com.whosin.app.service.models.ContactListModel;

public class FriendsAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

    private final Activity activity;
    private final boolean isShowInviteStatus;
    private CommanCallback<Boolean> callback;
    public void setCallback(CommanCallback<Boolean> callback) {
        this.callback = callback;
    }

    public FriendsAdapter(Activity activity, boolean isShowInviteStatus) {
        this.activity = activity;
        this.isShowInviteStatus = isShowInviteStatus;
    }

    public FriendsAdapter(Activity activity) {
        this.activity = activity;
        this.isShowInviteStatus = false;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(UiUtils.getViewBy(parent, R.layout.invited_guest_list_item));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ContactListModel model = (ContactListModel) getItem(position);
        if (model == null) { return; }
        ViewHolder viewHolder = (ViewHolder) holder;

        viewHolder.binding.contactLayout.setCornerRadius(0, CornerType.ALL);
        boolean isFirstCell = false;
        boolean isLastCell = getItemCount() - 1 == position;

        if (position == 0) {
            isFirstCell = true;
        }

        float cornerRadius = activity.getResources().getDimensionPixelSize(com.intuit.sdp.R.dimen._10sdp);
        if (isFirstCell) {
            viewHolder.binding.contactLayout.setCornerRadius(cornerRadius, CornerType.TOP_LEFT);
            viewHolder.binding.contactLayout.setCornerRadius(cornerRadius, CornerType.TOP_RIGHT);
        }
        if (isLastCell) {
            viewHolder.binding.contactLayout.setCornerRadius(cornerRadius, CornerType.BOTTOM_RIGHT);
            viewHolder.binding.contactLayout.setCornerRadius(cornerRadius, CornerType.BOTTOM_LEFT);
        }
        viewHolder.binding.view.setVisibility(isLastCell ? View.GONE  : View.VISIBLE);


        viewHolder.binding.tvUserName.setText(model.getFullName());
        Graphics.loadImageWithFirstLetter(model.getImage(), viewHolder.binding.ivUserProfile, model.getFullName());

        if (isShowInviteStatus) {
            viewHolder.binding.tvUserStatus.setVisibility(View.VISIBLE);
            viewHolder.binding.tvUserStatus.setText(model.getInviteStatus());
            viewHolder.binding.inviteStatus.setVisibility(isShowInviteStatus ? View.VISIBLE : View.GONE);
            Utils.setInvitationStatus(activity,viewHolder.binding.inviteStatus, model.getInviteStatus(),viewHolder.binding.tvUserStatus);
        } else {
            String userStatusText = TextUtils.isEmpty(model.getPhone()) ? model.getEmail() : model.getPhone();
            viewHolder.binding.tvUserStatus.setText(userStatusText);
            viewHolder.binding.tvUserStatus.setVisibility(userStatusText.isEmpty() ? View.GONE : View.VISIBLE);
        }

        viewHolder.binding.optionContainer.setVisibility(Utils.isUser(model.getId()) ? View.GONE : View.VISIBLE);
        viewHolder.binding.optionContainer.setupView(model, activity, data -> {
            if (callback != null) {
                callback.onReceive(data);
            }
        });

        viewHolder.binding.getRoot().setOnClickListener(view -> {
            Utils.preventDoubleClick(view);
            Utils.openProfile(activity,model.getId());
        });
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private final InvitedGuestListItemBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = InvitedGuestListItemBinding.bind(itemView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        ContactListModel inModel = (ContactListModel) getItem(position);
        if (inModel.getId().equals("-1")) {
            return 0;
        } else {
            return 1;
        }
    }
}
