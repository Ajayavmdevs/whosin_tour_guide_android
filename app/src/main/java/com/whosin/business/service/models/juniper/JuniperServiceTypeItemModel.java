package com.whosin.business.service.models.juniper;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.service.models.ModelProtocol;

public class JuniperServiceTypeItemModel implements DiffIdentifier, ModelProtocol {

	@SerializedName("Code")
	@Expose
	private int code = 0;

	public void setCode(int code){
		this.code = code;
	}

	public int getCode(){
		return code;
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