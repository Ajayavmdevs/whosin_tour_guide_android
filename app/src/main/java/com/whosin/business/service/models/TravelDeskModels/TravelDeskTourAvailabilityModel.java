package com.whosin.business.service.models.TravelDeskModels;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.service.models.ModelProtocol;

import java.util.Locale;

public class TravelDeskTourAvailabilityModel implements DiffIdentifier, ModelProtocol {

    @SerializedName("availability")
    @Expose
    private TravelDeskAvailabilityModel availability;

    @SerializedName("price")
    @Expose
    private TravelDeskPriceModel price;

    public String slotText = "";

    public TravelDeskAvailabilityModel getAvailability() {
        return availability;
    }

    public TravelDeskTourAvailabilityModel(){

    }

    public TravelDeskTourAvailabilityModel(String slotText){
        this.slotText = slotText;
    }



    public void setAvailability(TravelDeskAvailabilityModel availability) {
        this.availability = availability;
    }

    public TravelDeskPriceModel getPrice() {
        return price;
    }

    public void setPrice(TravelDeskPriceModel price) {
        this.price = price;
    }

    @Override
    public int getIdentifier() {
        return 0;
    }

    @Override
    public boolean isValidModel() {
        return true;
    }

    public String getSlotText() {
        if (!TextUtils.isEmpty(slotText)) return slotText;
        return convertMinutesToTime(availability.getStartTime()) + " - " + convertMinutesToTime(availability.getEndTime());
    }

    private String convertMinutesToTime(int minutes) {
        int hrs = minutes / 60;
        int mins = minutes % 60;
        return String.format(Locale.ENGLISH,"%02d:%02d", hrs, mins);
    }
}
