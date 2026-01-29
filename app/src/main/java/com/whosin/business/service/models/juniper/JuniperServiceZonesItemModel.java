package com.whosin.business.service.models.juniper;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Utils;
import com.whosin.business.service.models.ModelProtocol;

public class JuniperServiceZonesItemModel implements DiffIdentifier, ModelProtocol {

	@SerializedName("ZoneCode")
	@Expose
	private int zoneCode;

	@SerializedName("Name")
	@Expose
	private String name;

	public void setZoneCode(int zoneCode){
		this.zoneCode = zoneCode;
	}

	public int getZoneCode(){
		return zoneCode;
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