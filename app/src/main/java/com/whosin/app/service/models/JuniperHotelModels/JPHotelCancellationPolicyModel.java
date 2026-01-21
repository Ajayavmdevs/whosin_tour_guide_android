package com.whosin.app.service.models.JuniperHotelModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;
import com.whosin.app.service.models.ModelProtocol;

import java.util.List;

public  class JPHotelCancellationPolicyModel implements DiffIdentifier, ModelProtocol {

    @SerializedName("Description")
    @Expose
    public String description = "";

    @SerializedName("PolicyRules")
    @Expose
    public List<JPHotelPolicyRuleModel> policyRules;

    public String getDescription() {
        return Utils.notNullString(description);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<JPHotelPolicyRuleModel> getPolicyRules() {
        return Utils.notEmptyList(policyRules);
    }

    public void setPolicyRules(List<JPHotelPolicyRuleModel> policyRules) {
        this.policyRules = policyRules;
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
