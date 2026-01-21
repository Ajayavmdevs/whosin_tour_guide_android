package com.whosin.app.ui.adapter;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.ItemCancellationViewBinding;
import com.whosin.app.service.models.JuniperHotelModels.JPHotelPolicyRuleModel;

public class JPHotelCancellationPolicyAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = UiUtils.getViewBy(parent, R.layout.item_cancellation_view);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        JPHotelPolicyRuleModel model = (JPHotelPolicyRuleModel) getItem(position);
        if (model == null) return;

        viewHolder.mBinding.tvFromTitle.setText(Utils.getLangValue("and_from"));
        viewHolder.mBinding.tvToDate.setText(Utils.getLangValue("and_to"));

        if (position == getItemCount() - 1) {
            viewHolder.mBinding.viewLine.setVisibility(View.GONE);
        } else {
            viewHolder.mBinding.viewLine.setVisibility(View.VISIBLE);
        }

        String inputFrom = model.getDateFrom() + " " + model.getDateFromHour();
        if (TextUtils.isEmpty(inputFrom.trim())) {
            viewHolder.mBinding.fromDate.setVisibility(View.GONE);
        } else {
            String fromDate = Utils.changeDateFormat(inputFrom, "yyyy-MM-dd HH:mm", "hh:mm a, EEE, dd MMM yyyy");
            viewHolder.mBinding.fromDate.setText(fromDate);
            viewHolder.mBinding.fromDate.setVisibility(View.VISIBLE);
        }


        String inputTo = model.getDateTo() + " " + model.getDateToHour();
        if (TextUtils.isEmpty(inputTo.trim())) {
            viewHolder.mBinding.toDate.setVisibility(View.GONE);
        } else {
            String fromTo = Utils.changeDateFormat(inputTo, "yyyy-MM-dd HH:mm", "hh:mm a, EEE, dd MMM yyyy");
            viewHolder.mBinding.toDate.setText(fromTo);
            viewHolder.mBinding.toDate.setVisibility(View.VISIBLE);
        }



        int refundAmount = 100 - Integer.parseInt(model.getPercentPrice());
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