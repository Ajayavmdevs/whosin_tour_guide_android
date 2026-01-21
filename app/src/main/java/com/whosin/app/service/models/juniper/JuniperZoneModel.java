package com.whosin.app.service.models.juniper;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;
import com.whosin.app.service.models.ModelProtocol;

public class JuniperZoneModel implements DiffIdentifier, ModelProtocol {

	@SerializedName("code")
	@Expose
	private String code;

	@SerializedName("name")
	@Expose
	private String name;

	public void setCode(String code){
		this.code = code;
	}

	public String getCode(){
		return Utils.notNullString(code);
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return Utils.notNullString(name);
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