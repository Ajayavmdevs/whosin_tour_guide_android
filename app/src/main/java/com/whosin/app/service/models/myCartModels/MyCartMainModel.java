package com.whosin.app.service.models.myCartModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;
import com.whosin.app.service.models.ModelProtocol;
import com.whosin.app.service.models.ContactUsBlockModel;
import com.whosin.app.service.models.rayna.RaynaTicketDetailModel;

import java.util.List;

public class MyCartMainModel implements DiffIdentifier, ModelProtocol {

    @SerializedName("items")
    @Expose
    private List<MyCartItemsModel> items;

    @SerializedName("customTickets")
    @Expose
    private List<RaynaTicketDetailModel> customTickets;

    @SerializedName("contactUsBlock")
    @Expose
    private ContactUsBlockModel contactUsBlock;

    public List<MyCartItemsModel> getItems() {
        return Utils.notEmptyList(items);
    }

    public void setItems(List<MyCartItemsModel> items) {
        this.items = items;
    }

    public List<RaynaTicketDetailModel> getCustomTickets() {
        return Utils.notEmptyList(customTickets);
    }

    public void setCustomTickets(List<RaynaTicketDetailModel> customTickets) {
        this.customTickets = customTickets;
    }

    public ContactUsBlockModel getContactUsBlock() {
        return contactUsBlock;
    }

    public void setContactUsBlock(ContactUsBlockModel contactUsBlock) {
        this.contactUsBlock = contactUsBlock;
    }

    @Override
    public int getIdentifier() {
        return 0;
    }

    @Override
    public boolean isValidModel() {
        return true;
    }
}
