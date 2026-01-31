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
import com.whosin.business.comman.ui.UiUtils;
import com.whosin.business.service.models.rayna.RaynaTicketDetailModel;
import com.whosin.business.service.models.statistics.TransactionListModel;

import java.util.List;

public class TopSalesAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_top_sales));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        TransactionListModel model = (TransactionListModel) getItem(position);
        
        if (model == null) return;

        viewHolder.tvQuantityValue.setText(String.valueOf(model.getQuantity()));
        
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
        TextView tvQuantityValue;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvSubtitle = itemView.findViewById(R.id.tvSubtitle);
            tvQuantityValue = itemView.findViewById(R.id.tvQuantityValue);
        }
    }
}
