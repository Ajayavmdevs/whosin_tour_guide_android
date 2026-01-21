package com.whosin.app.service.models.PromotionalBannerModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;
import com.whosin.app.service.models.ModelProtocol;
import com.whosin.app.service.models.rayna.RaynaTicketDetailModel;

import java.util.List;

public class PromotionalMainModel implements DiffIdentifier, ModelProtocol {

    @SerializedName("list")
    @Expose
    private List<PromotionalListModel> list;

    @SerializedName("tickets")
    @Expose
    private List<RaynaTicketDetailModel> tickets;


    public List<PromotionalListModel> getList() {
        return Utils.notEmptyList(list);
    }

    public void setList(List<PromotionalListModel> list) {
        this.list = list;
    }

    public List<RaynaTicketDetailModel> getTickets() {
        return Utils.notEmptyList(tickets);
    }

    public void setTickets(List<RaynaTicketDetailModel> tickets) {
        this.tickets = tickets;
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
