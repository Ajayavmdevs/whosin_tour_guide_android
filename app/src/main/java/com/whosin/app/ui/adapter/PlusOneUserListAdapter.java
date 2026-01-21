package com.whosin.app.ui.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.LayoutCmComplementryMygroupViewBinding;
import com.whosin.app.service.models.UserDetailModel;

public class PlusOneUserListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

    private Activity activity;

    public PlusOneUserListAdapter(Activity activity) {
        this.activity = activity;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(UiUtils.getViewBy(parent, R.layout.layout_cm_complementry_mygroup_view));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        UserDetailModel model = (UserDetailModel) getItem(position);

        Graphics.loadImageWithFirstLetter(model.getImage(), viewHolder.mBinding.ivPlusOne, model.getFirstName());
        viewHolder.mBinding.civName.setText(model.getFirstName());

    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private final LayoutCmComplementryMygroupViewBinding mBinding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mBinding = LayoutCmComplementryMygroupViewBinding.bind(itemView);

        }
    }
}
