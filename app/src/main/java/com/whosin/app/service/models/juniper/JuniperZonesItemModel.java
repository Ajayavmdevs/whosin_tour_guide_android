package com.whosin.app.service.models.juniper;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;
import com.whosin.app.service.models.ModelProtocol;

public class JuniperZonesItemModel implements DiffIdentifier, ModelProtocol {

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