package com.whosin.app.ui.adapter;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.JpHotelGuestPrviewViewBinding;
import com.whosin.app.service.models.JuniperHotelModels.JPPassengerModel;

public class JPHotelGuestDetailAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(UiUtils.getViewBy(parent, R.layout.jp_hotel_guest_prview_view));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        JPPassengerModel model = (JPPassengerModel) getItem(position);

        if (model == null) return;

        String guestName = model.getPrefix() + " " + model.getFirstName() + " " + model.getLastName();
        viewHolder.binding.tvGuestNameValue.setText(guestName);
        viewHolder.binding.tvGuestEmailValue.setText(model.getEmail());
        viewHolder.binding.tvGuestMobileValue.setText(model.getMobile());
        viewHolder.binding.tvGuestAgeValue.setText(model.getAge());
        viewHolder.binding.tvNationalityValue.setText(model.getNationality());

        int lastPos = getItemCount() - 1;
        viewHolder.binding.viewLine1.setVisibility(position == lastPos ? View.GONE : View.VISIBLE);

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final JpHotelGuestPrviewViewBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = JpHotelGuestPrviewViewBinding.bind(itemView);
            binding.tvGuestNameTitle.setText(getvalue("name"));
            binding.tvGuestEmailTitle.setText(getvalue("email"));
            binding.tvGuestMobileTitle.setText(getvalue("mobile"));
            binding.tvGuestAgeTitle.setText(getvalue("jp_guest_age"));
            binding.tvGuestNationalityTitle.setText(getvalue("nationality"));

        }

        public String getvalue(String key){
            return Utils.getLangValue(key) + " : ";
        }
    }
}

