package com.whosin.business.service.models.BigBusModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Utils;
import com.whosin.business.service.models.ModelProtocol;

public class BigBusContactModel implements DiffIdentifier, ModelProtocol {

	@SerializedName("website")
	@Expose
	private String website = "";

	@SerializedName("address")
	@Expose
	private String address = "";

	@SerializedName("name")
	@Expose
	private String name = "";

	@SerializedName("telephone")
	@Expose
	private Object telephone;

	@SerializedName("email")
	@Expose
	private Object email;

	public String getWebsite() {
		return Utils.notNullString(website);
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getAddress() {
		return Utils.notNullString(address);
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getName() {
		return Utils.notNullString(name);
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object getTelephone() {
		return telephone;
	}

	public void setTelephone(Object telephone) {
		this.telephone = telephone;
	}

	public Object getEmail() {
		return email;
	}

	public void setEmail(Object email) {
		this.email = email;
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