package com.whosin.business.service.models.JuniperHotelModels;

import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Utils;
import com.whosin.business.service.models.ModelProtocol;

import java.util.ArrayList;
import java.util.List;

public class PaxesItemModel implements DiffIdentifier, ModelProtocol {

    private int roomId = 0;

    private int adultCount = 1;

    private int childCount = 0;

    private List<PaxModel> pax;


    public PaxesItemModel(){

    }

    public PaxesItemModel(int roomId){
       this.roomId = roomId;
    }

    public List<PaxModel> getPax() {
        return Utils.notEmptyList(pax);
    }

    public void setPax(List<PaxModel> pax) {
        this.pax = pax;
    }

    @Override
    public int getIdentifier() {
        return roomId;
    }

    @Override
    public boolean isValidModel() {
        return true;
    }

    public int getAdultCount() {
        return adultCount;
    }

    public void setAdultCount(int adultCount) {
        this.adultCount = adultCount;
    }

    public int getChildCount() {
        return childCount;
    }

    public void setChildCount(int childCount) {
        this.childCount = childCount;
    }

    public void addPaxModel(){
        if (pax == null || pax.isEmpty()){
            pax = new ArrayList<>();
        }
        PaxModel paxModel = new PaxModel();
        paxModel.setId(pax.size() + 2);
        pax.add(paxModel);
    }

    public void removePax(){
        if (pax != null && !pax.isEmpty()){
            pax.remove(pax.size() - 1);
        }
    }
}
