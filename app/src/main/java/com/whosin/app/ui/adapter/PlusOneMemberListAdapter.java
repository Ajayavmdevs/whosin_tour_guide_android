package com.whosin.app.ui.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.ItemMyRingViewBinding;
import com.whosin.app.service.models.InvitedUserModel;
import com.whosin.app.ui.activites.Profile.OtherUserProfileActivity;

public class PlusOneMemberListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

    private Activity activity;
    private FragmentManager fragmentManager;

    public PlusOneMemberListAdapter(Activity activity, FragmentManager fragmentManager) {
        this.activity = activity;
        this.fragmentManager = fragmentManager;
    }

    public PlusOneMemberListAdapter(Activity activity) {
        this.activity = activity;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_my_ring_view));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        InvitedUserModel model = (InvitedUserModel) getItem(position);

        ViewGroup.LayoutParams params = viewHolder.mBinding.image.getLayoutParams();
        params.width = activity.getResources().getDimensionPixelSize(R.dimen.one_plus_member);
        params.height = activity.getResources().getDimensionPixelSize(R.dimen.one_plus_member);
        viewHolder.mBinding.image.setLayoutParams(params);

        viewHolder.mBinding.tvName.setTypeface(viewHolder.mBinding.tvName.getTypeface(), Typeface.NORMAL);
        viewHolder.mBinding.tvName.setTextAppearance(android.R.style.TextAppearance_Material_Body1);
        viewHolder.mBinding.tvName.setTextSize(activity.getResources().getDimension(R.dimen.one_plus_member_name));

        viewHolder.mBinding.tvName.setText(model.getFirstName());
        Graphics.loadImageWithFirstLetter(model.getImage(),viewHolder.mBinding.image, model.getFirstName());

        if (position == 0) {
            ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) viewHolder.itemView.getLayoutParams();
            int marginStart = activity.getResources().getDimensionPixelSize(R.dimen.chat_event_title);
            marginParams.setMarginStart(marginStart);
            viewHolder.itemView.setLayoutParams(marginParams);
        }

        viewHolder.mBinding.getRoot().setOnClickListener(view -> {
            Utils.preventDoubleClick(view);
            activity.startActivity(new Intent(activity, OtherUserProfileActivity.class).putExtra("friendId", model.getUserId()));
        });
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemMyRingViewBinding mBinding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mBinding = ItemMyRingViewBinding.bind(itemView);
        }
    }
}
