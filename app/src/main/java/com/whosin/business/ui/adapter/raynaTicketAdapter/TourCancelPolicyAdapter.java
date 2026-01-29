package com.whosin.business.ui.adapter.raynaTicketAdapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.whosin.business.R;
import com.whosin.business.comman.DiffAdapter;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Utils;
import com.whosin.business.comman.ui.UiUtils;
import com.whosin.business.databinding.ItemCancellationViewBinding;
import com.whosin.business.service.models.rayna.TourOptionsModel;


public class TourCancelPolicyAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = UiUtils.getViewBy(parent, R.layout.item_cancellation_view);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        TourOptionsModel model = (TourOptionsModel) getItem(position);
        if (model == null) return;

        viewHolder.mBinding.tvFromTitle.setText(Utils.getLangValue("and_from"));
        viewHolder.mBinding.tvToDate.setText(Utils.getLangValue("and_to"));

        if (position == getItemCount() - 1) {
            viewHolder.mBinding.viewLine.setVisibility(View.GONE);
        } else {
            viewHolder.mBinding.viewLine.setVisibility(View.VISIBLE);
        }

        viewHolder.mBinding.fromDate.setText(Utils.changeDateFormat(model.getFromDate(), "yyyy-MM-dd'T'HH:mm:ss", "h:mm a, EEE, dd MMM yyyy").replace("pm", "PM").replace("am", "AM"));
        viewHolder.mBinding.toDate.setText(Utils.changeDateFormat(model.getToDate(), "yyyy-MM-dd'T'HH:mm:ss", "h:mm a, EEE, dd MMM yyyy").replace("pm", "PM").replace("am", "AM"));
//        viewHolder.mBinding.fromDate.setText(Utils.changeDateFormat(model.getFromDate(), "yyyy-MM-dd'T'HH:mm:ss", "h:mm a, EEE, dd \nMMM yyyy").replace("pm", "PM").replace("am", "AM"));
//        viewHolder.mBinding.toDate.setText(Utils.changeDateFormat(model.getToDate(), "yyyy-MM-dd'T'HH:mm:ss", "h:mm a, EEE, dd \nMMM yyyy").replace("pm", "PM").replace("am", "AM"));

        int refundAmount = 100 - model.getPercentage();
//        viewHolder.mBinding.refundTextView.setText(refundAmount + "% Refund");
        viewHolder.mBinding.refundTextView.setText(String.format("%s%% %s", refundAmount, Utils.getLangValue("refund")));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemCancellationViewBinding mBinding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mBinding = ItemCancellationViewBinding.bind(itemView);
        }
    }
}
