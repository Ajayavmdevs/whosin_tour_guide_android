package com.whosin.business.ui.fragment.transaction;

import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.whosin.business.R;
import com.whosin.business.comman.DiffAdapter;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Graphics;
import com.whosin.business.comman.Utils;
import com.whosin.business.comman.ui.UiUtils;
import com.whosin.business.service.models.rayna.RaynaTicketDetailModel;
import com.whosin.business.service.models.statistics.TransactionListModel;

import java.util.List;

public class TransactionsAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_transaction));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        TransactionListModel model = (TransactionListModel) getItem(position);

        if (model == null) return;

        Utils.setStyledText(viewHolder.itemView.getContext(), viewHolder.tvTotalSale, String.valueOf(model.getTotalSale()));
        Utils.setStyledText(viewHolder.itemView.getContext(), viewHolder.tvTotalCost, String.valueOf(model.getTotalCost()));
        Utils.setStyledText(viewHolder.itemView.getContext(), viewHolder.tvTotalProfit, String.valueOf(model.getTotalProfit()));

        String formattedDate = model.getCreatedAt();
        if (formattedDate != null && !formattedDate.isEmpty()) {
            String outputFormat = "dd MMM yyyy, h:mm a";
            // Try parsing ISO format with Z
            String result = Utils.changeDateFormat(formattedDate, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", outputFormat);
            if (result.equals(formattedDate)) {
                result = Utils.changeDateFormat(formattedDate, "yyyy-MM-dd'T'HH:mm:ss.SSS", outputFormat);
            }
            // If still original, try simple date
            if (result.equals(formattedDate)) {
                result = Utils.changeDateFormat(formattedDate, "yyyy-MM-dd", "dd MMM yyyy");
            }
            formattedDate = result;
        }
        viewHolder.tvCreatedDate.setText("Created: " + formattedDate);

        RaynaTicketDetailModel ticketInfo = model.getTicketInfo();
        if (ticketInfo != null) {
            viewHolder.tvTitle.setText(ticketInfo.getTitle());

            CharSequence subtitle = "";
            if (ticketInfo.getDescription() != null && !ticketInfo.getDescription().isEmpty()) {
                subtitle = Html.fromHtml(ticketInfo.getDescription());
            }

            viewHolder.tvSubtitle.setText(subtitle);
            viewHolder.tvSubtitle.setVisibility(subtitle.length() == 0 ? View.GONE : View.VISIBLE);

            List<String> images = ticketInfo.getImages();
            if (images != null && !images.isEmpty()) {
                Graphics.loadImage(images.get(0), viewHolder.ivImage);
            } else {
                viewHolder.ivImage.setImageResource(R.color.gray); // Placeholder
            }
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvTitle;
        TextView tvSubtitle;
        TextView tvTiming;
        TextView tvCreatedDate;
        TextView tvTotalSale;
        TextView tvTotalCost;
        TextView tvTotalProfit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvSubtitle = itemView.findViewById(R.id.tvSubtitle);
            tvTiming = itemView.findViewById(R.id.tvTiming);
            tvCreatedDate = itemView.findViewById(R.id.tvCreatedDate);
            tvTotalSale = itemView.findViewById(R.id.tvTotalSale);
            tvTotalCost = itemView.findViewById(R.id.tvTotalCost);
            tvTotalProfit = itemView.findViewById(R.id.tvTotalProfit);
        }
    }
}
