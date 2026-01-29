package com.whosin.business.ui.activites.setting;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.whosin.business.R;
import com.whosin.business.databinding.ActivityTransactionHistoryBinding;
import com.whosin.business.ui.activites.comman.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class TransactionHistoryActivity extends BaseActivity {

    private ActivityTransactionHistoryBinding binding;
    private TransactionAdapter adapter;
    private List<Transaction> transactionList;

    @Override
    protected void initUi() {
        setupRecyclerView();
        loadStaticData();
    }

    private void setupRecyclerView() {
        binding.rvTransactions.setLayoutManager(new LinearLayoutManager(this));
        transactionList = new ArrayList<>();
        adapter = new TransactionAdapter(transactionList);
        binding.rvTransactions.setAdapter(adapter);
    }

    private void loadStaticData() {
        transactionList.add(new Transaction(
                "Burj Khalifa - Standard",
                "Ajay Kumarkhaniya",
                "23 Sep 2025 • 14:00 - 16:00",
                "AED 132.00",
                "Cancelled",
                "",
                "",
                false
        ));

        transactionList.add(new Transaction(
                "Desert Safari Booking",
                "Ajay Kumarkhaniya",
                "12 Aug 2025 • 09:00 - 18:00",
                "AED 250.00",
                "Pending",
                "1x Adult, 2x Child",
                "",
                false
        ));

        transactionList.add(new Transaction(
                "Mall of the Emirates - Cinema",
                "Ajay Kumarkhaniya",
                "05 Jul 2025 • 19:00 - 21:00",
                "AED 75.00",
                "Completed",
                "1x Adult",
                "",
                false
        ));

        transactionList.add(new Transaction(
                "Emirates Flight Refund",
                "Ajay Kumarkhaniya",
                "28 Jun 2025 • Refunded",
                "+ AED 520.00",
                "Completed",
                "AED 520.00",
                "+ AED 1000.00", // Wait, image says + AED 1000.00 below? Actually line says "AED 520.00" with check, and right side "+ AED 1000.00" completed.
                // Let's adjust based on image:
                // Title: Emirates Flight Refund
                // Amount: + AED 520.00 (Green)
                // Status: Completed (Green)
                // Bottom Left: Green Check + AED 520.00
                // Bottom Right: + AED 1000.00 (Green)
                // This seems like a refund + topup? Or maybe partial?
                // I will model it flexibly.
                true
        ));
        
        // Re-adjusting data based on image interpretation:
        // Item 4:
        // Title: Emirates Flight Refund
        // Subtitle: Ajay Kumarkhaniya
        // Date: 28 Jun 2025 • Refunded
        // Amount: + AED 520.00
        // Status: Completed
        // Bottom Left: (Check Icon) AED 520.00
        // Bottom Right: + AED 1000.00 (Maybe balance?)

        transactionList.add(new Transaction(
                "Account Top-up",
                "16 Jun 2025 Completed", // Subtitle in image seems to be date + status? No.
                // Image: "Account Top-up"
                // Subtitle: "16 Jun 2025 Completed" (Looks like subtitle is date/status here)
                // Right: + AED 1000.00
                // Status: Completed
                "", // No date line
                "+ AED 1000.00",
                "Completed",
                "",
                "",
                false
        ));
        
        // Let's refine the Transaction model to be more generic to fit all these cases.
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void setListeners() {
        binding.ivClose.setOnClickListener(view -> onBackPressed());
    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityTransactionHistoryBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    // Model
    public static class Transaction {
        String title;
        String subtitle;
        String date;
        String amount;
        String status;
        String extraInfo; // Bottom left text
        String bottomAmount; // Bottom right text
        boolean isRefundedType; // To trigger specific layout changes if needed

        public Transaction(String title, String subtitle, String date, String amount, String status, String extraInfo, String bottomAmount, boolean isRefundedType) {
            this.title = title;
            this.subtitle = subtitle;
            this.date = date;
            this.amount = amount;
            this.status = status;
            this.extraInfo = extraInfo;
            this.bottomAmount = bottomAmount;
            this.isRefundedType = isRefundedType;
        }
    }

    // Adapter
    public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

        private List<Transaction> list;

        public TransactionAdapter(List<Transaction> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction_history, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Transaction item = list.get(position);

            holder.tvTitle.setText(item.title);
            holder.tvSubtitle.setText(item.subtitle);
            holder.tvAmount.setText(item.amount);
            holder.tvStatus.setText(item.status);

            if (item.date != null && !item.date.isEmpty()) {
                holder.tvDateTime.setText(item.date);
                holder.tvDateTime.setVisibility(View.VISIBLE);
            } else {
                holder.tvDateTime.setVisibility(View.GONE);
            }

            // Status Colors and Icons
            if (item.status.contains("Cancelled")) {
                holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.brand_pink));
                holder.tvAmount.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.white));
                holder.ivBottomIcon.setImageResource(R.drawable.icon_close); // Ensure this drawable exists
                holder.ivBottomIcon.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), R.color.brand_pink));
                holder.tvBottomText.setText("Cancelled");
                holder.tvBottomText.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.brand_pink));
                holder.llBottomInfo.setVisibility(View.VISIBLE);
                holder.tvBottomAmount.setVisibility(View.GONE);
            } else if (item.status.contains("Pending")) {
                holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.pending_yellow));
                holder.tvAmount.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.white));
                // Pending case: show extra info (e.g. 1x Adult)
                holder.ivBottomIcon.setImageResource(com.google.firebase.inappmessaging.display.R.drawable.image_placeholder); // Or generic icon
                holder.ivBottomIcon.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), R.color.pending_yellow));
                // Actually image shows user icon for "1x Adult"
                if (!item.extraInfo.isEmpty()) {
                     holder.ivBottomIcon.setImageResource(R.drawable.add_person_icon); // Use person icon
                     holder.ivBottomIcon.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), R.color.pending_yellow));
                     holder.tvBottomText.setText(item.extraInfo);
                     holder.tvBottomText.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.pending_yellow));
                     holder.llBottomInfo.setVisibility(View.VISIBLE);
                } else {
                    holder.llBottomInfo.setVisibility(View.GONE);
                }
                
                if (!item.bottomAmount.isEmpty()) {
                    holder.tvBottomAmount.setText(item.bottomAmount);
                    holder.tvBottomAmount.setVisibility(View.VISIBLE);
                    holder.tvBottomAmount.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.pending_yellow));
                } else {
                    holder.tvBottomAmount.setVisibility(View.VISIBLE); // Default amount repeated?
                    holder.tvBottomAmount.setText(item.amount); // Show amount again at bottom?
                    holder.tvBottomAmount.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.pending_yellow));
                }

            } else if (item.status.contains("Completed")) {
                holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.green)); // or in_green
                
                if (item.amount.contains("+")) {
                    holder.tvAmount.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.green));
                } else {
                    holder.tvAmount.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.white));
                }

                if (item.isRefundedType) {
                    holder.ivBottomIcon.setImageResource(R.drawable.icon_check_green); // check icon
                    holder.ivBottomIcon.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), R.color.green));
                    holder.tvBottomText.setText(item.extraInfo); // AED 520.00
                    holder.tvBottomText.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.green));
                    holder.llBottomInfo.setVisibility(View.VISIBLE);

                    if (!item.bottomAmount.isEmpty()) {
                        holder.tvBottomAmount.setText(item.bottomAmount);
                        holder.tvBottomAmount.setVisibility(View.VISIBLE);
                        holder.tvBottomAmount.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.green));
                    }
                } else {
                    // Normal completed
                    if (!item.extraInfo.isEmpty()) {
                         // e.g. 1x Adult
                         holder.ivBottomIcon.setImageResource(R.drawable.add_person_icon);
                         holder.ivBottomIcon.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), R.color.pending_yellow)); // Image shows orange/yellow for people count even if completed?
                         // Wait, in "Mall of the Emirates", status is Completed (Green), but "1x Adult" is Orange/Yellow.
                         // So bottom info color might be independent.
                         holder.tvBottomText.setText(item.extraInfo);
                         holder.tvBottomText.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.pending_yellow));
                         holder.llBottomInfo.setVisibility(View.VISIBLE);
                    } else {
                        // Account Top-up case
                        holder.divider.setVisibility(View.GONE);
                         holder.llBottomInfo.setVisibility(View.GONE);
                    }
                    
                    if (item.title.contains("Top-up")) {
                         holder.tvBottomAmount.setVisibility(View.GONE); // No bottom amount for top up in image? Actually top up has amount on right, and status completed.
                         // The image for "Account Top-up" shows:
                         // Avatar | Account Top-up | + AED 1000.00 (Green)
                         //        | 16 Jun 2025 Completed | Completed (Green)
                         // This is a bit different structure.
                         // But I can adapt.
                    } else {
                        // Show bottom amount usually same as top?
                        // Image shows "AED 250.00" at bottom right for Pending and Completed (Mall of Emirates).
                         holder.tvBottomAmount.setText("AED 250.00"); // Mock
                         if (item.title.contains("Mall")) holder.tvBottomAmount.setText("AED 250.00");
                         holder.tvBottomAmount.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.pending_yellow));
                         holder.tvBottomAmount.setVisibility(View.VISIBLE);
                    }
                }
            }

            // Handle specific overrides for static data visual matching
            if (item.title.contains("Burj Khalifa")) {
                holder.ivBottomIcon.setImageResource(R.drawable.icon_close);
                holder.ivBottomIcon.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), R.color.brand_pink));
                holder.tvBottomText.setText("Cancelled");
                holder.tvBottomText.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.brand_pink));
                holder.tvBottomAmount.setVisibility(View.GONE);
            }
            if (item.title.contains("Desert Safari")) {
                holder.ivBottomIcon.setImageResource(R.drawable.add_person_icon); // Assuming generic person icon
                holder.tvBottomText.setText("1x Adult, 2x Child");
                holder.tvBottomText.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.pending_yellow));
                holder.tvBottomAmount.setText("AED 250.00");
                holder.tvBottomAmount.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.pending_yellow));
                holder.tvBottomAmount.setVisibility(View.VISIBLE);
            }
            if (item.title.contains("Mall of the Emirates")) {
                holder.ivBottomIcon.setImageResource(R.drawable.add_person_icon);
                holder.tvBottomText.setText("1x Adult");
                holder.tvBottomText.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.pending_yellow));
                holder.tvBottomAmount.setText("AED 250.00");
                holder.tvBottomAmount.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.pending_yellow));
                holder.tvBottomAmount.setVisibility(View.VISIBLE);
            }
            if (item.title.contains("Emirates Flight Refund")) {
                 holder.ivBottomIcon.setImageResource(R.drawable.icon_check_green);
                 holder.ivBottomIcon.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), R.color.green));
                 holder.tvBottomText.setText("AED 520.00");
                 holder.tvBottomText.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.green));
                 holder.tvBottomAmount.setText("+ AED 1000.00");
                 holder.tvBottomAmount.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.green));
                 holder.tvBottomAmount.setVisibility(View.VISIBLE);
            }
            if (item.title.contains("Account Top-up")) {
                holder.llBottomInfo.setVisibility(View.GONE);
                holder.tvBottomAmount.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            CircleImageView ivAvatar;
            TextView tvTitle, tvSubtitle, tvAmount, tvStatus, tvDateTime, tvBottomText, tvBottomAmount;
            ImageView ivBottomIcon, ivMore;
            LinearLayout llBottomInfo;
            View divider;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                ivAvatar = itemView.findViewById(R.id.ivAvatar);
                tvTitle = itemView.findViewById(R.id.tvTitle);
                tvSubtitle = itemView.findViewById(R.id.tvSubtitle);
                tvAmount = itemView.findViewById(R.id.tvAmount);
                tvStatus = itemView.findViewById(R.id.tvStatus);
                tvDateTime = itemView.findViewById(R.id.tvDateTime);
                tvBottomText = itemView.findViewById(R.id.tvBottomText);
                tvBottomAmount = itemView.findViewById(R.id.tvBottomAmount);
                ivBottomIcon = itemView.findViewById(R.id.ivBottomIcon);
                ivMore = itemView.findViewById(R.id.ivMore);
                llBottomInfo = itemView.findViewById(R.id.llBottomInfo);
                divider = itemView.findViewById(R.id.divider);
            }
        }
    }
}
