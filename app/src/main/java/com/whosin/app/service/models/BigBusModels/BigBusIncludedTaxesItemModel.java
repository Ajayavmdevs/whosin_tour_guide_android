package com.whosin.app.service.models.BigBusModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;
import com.whosin.app.service.models.ModelProtocol;

public class BigBusIncludedTaxesItemModel implements DiffIdentifier, ModelProtocol {

	@SerializedName("original")
	@Expose
	private int original;

	@SerializedName("name")
	@Expose
	private String name;

	@SerializedName("shortDescription")
	@Expose
	private String shortDescription = "";

	@SerializedName("net")
	@Expose
	private int net;

	@SerializedName("retail")
	@Expose
	private int retail;

	public int getOriginal() {
		return original;
	}

	public void setOriginal(int original) {
		this.original = original;
	}

	public String getName() {
		return Utils.notNullString(name);
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getShortDescription() {
		return Utils.notNullString(shortDescription);
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	public int getNet() {
		return net;
	}

	public void setNet(int net) {
		this.net = net;
	}

	public int getRetail() {
		return retail;
	}

	public void setRetail(int retail) {
		this.retail = retail;
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