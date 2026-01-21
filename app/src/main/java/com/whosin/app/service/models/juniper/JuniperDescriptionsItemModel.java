package com.whosin.app.service.models.juniper;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;
import com.whosin.app.service.models.ModelProtocol;

public class JuniperDescriptionsItemModel implements DiffIdentifier, ModelProtocol {

	@SerializedName("Type")
	@Expose
	private String type;

	@SerializedName("Description")
	@Expose
	private String description;

	public void setType(String type){
		this.type = type;
	}

	public String getType(){
		return Utils.notNullString(type);
	}

	public void setDescription(String description){
		this.description = description;
	}

	public String getDescription(){
		return Utils.notNullString(description);
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