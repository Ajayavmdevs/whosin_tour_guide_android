package com.whosin.business.service.models.JuniperHotelModels;

import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Utils;
import com.whosin.business.service.models.ModelProtocol;

public class PaxModel implements DiffIdentifier, ModelProtocol {


    private String age = "";

    private int id = 0;

    public String getAge() {
        return Utils.notNullString(age);
    }

    public void setAge(String age) {
        this.age = age;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int getIdentifier() {
        return id;
    }

    @Override
    public boolean isValidModel() {
        return true;
    }
}
