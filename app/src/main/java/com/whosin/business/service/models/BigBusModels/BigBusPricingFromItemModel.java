package com.whosin.business.service.models.BigBusModels;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Utils;
import com.whosin.business.service.models.ModelProtocol;

public class BigBusPricingFromItemModel implements DiffIdentifier, ModelProtocol {

	@SerializedName("currencyPrecision")
	@Expose
	private int currencyPrecision;

	@SerializedName("original")
	@Expose
	private int original;

	@SerializedName("withoutDiscountNet")
	@Expose
	private int withoutDiscountNet;

	@SerializedName("currency")
	@Expose
	private String currency = "";

	@SerializedName("unitId")
	@Expose
	private String unitId = "";

	@SerializedName("net")
	@Expose
	private int net;

	@SerializedName("netBeforeDiscount")
	@Expose
	private int netBeforeDiscount;

	@SerializedName("retail")
	@Expose
	private int retail;

	@SerializedName("includedTaxes")
	@Expose
	private List<BigBusIncludedTaxesItemModel> includedTaxes;

	public int getCurrencyPrecision() {
		return currencyPrecision;
	}

	public void setCurrencyPrecision(int currencyPrecision) {
		this.currencyPrecision = currencyPrecision;
	}

	public int getOriginal() {
		return original;
	}

	public void setOriginal(int original) {
		this.original = original;
	}

	public int getWithoutDiscountNet() {
		return withoutDiscountNet;
	}

	public void setWithoutDiscountNet(int withoutDiscountNet) {
		this.withoutDiscountNet = withoutDiscountNet;
	}

	public String getCurrency() {
		return Utils.notNullString(currency);
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public int getNet() {
		return net;
	}

	public int getNetBeforeDiscount() {
		return netBeforeDiscount;
	}

	public void setNetBeforeDiscount(int netBeforeDiscount) {
		this.netBeforeDiscount = netBeforeDiscount;
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

	public List<BigBusIncludedTaxesItemModel> getIncludedTaxes() {
		return Utils.notEmptyList(includedTaxes);
	}

	public void setIncludedTaxes(List<BigBusIncludedTaxesItemModel> includedTaxes) {
		this.includedTaxes = includedTaxes;
	}

	public String getUnitId() {
		return Utils.notNullString(unitId);
	}

	public void setUnitId(String unitId) {
		this.unitId = unitId;
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