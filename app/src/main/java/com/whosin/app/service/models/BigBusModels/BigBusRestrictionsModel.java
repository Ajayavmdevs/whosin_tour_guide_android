package com.whosin.app.service.models.BigBusModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;
import com.whosin.app.service.models.ModelProtocol;

import java.util.List;

public class BigBusRestrictionsModel implements DiffIdentifier, ModelProtocol {

	@SerializedName("maxPaxCount")
	@Expose
	private Object maxPaxCount;

	@SerializedName("minPaxCount")
	@Expose
	private Object minPaxCount;

	@SerializedName("maxUnits")
	@Expose
	private int maxUnits;

	@SerializedName("minUnits")
	@Expose
	private int minUnits;

	@SerializedName("accompaniedBy")
	@Expose
	private List<Object> accompaniedBy;

	@SerializedName("paxCount")
	@Expose
	private int paxCount;

	@SerializedName("minQuantity")
	@Expose
	private int minQuantity;

	@SerializedName("maxAge")
	@Expose
	private int maxAge;

	@SerializedName("maxQuantity")
	@Expose
	private int maxQuantity;

	@SerializedName("minAge")
	@Expose
	private int minAge;

	@SerializedName("idRequired")
	@Expose
	private boolean idRequired = false;

	@SerializedName("required")
	@Expose
	private boolean required = false;

	@SerializedName("accompaniedByRatio")
	@Expose
	private Object accompaniedByRatio;

	public Object getMaxPaxCount() {
		return maxPaxCount;
	}

	public void setMaxPaxCount(int maxPaxCount) {
		this.maxPaxCount = maxPaxCount;
	}

	public Object getMinPaxCount() {
		return minPaxCount;
	}

	public void setMinPaxCount(Object minPaxCount) {
		this.minPaxCount = minPaxCount;
	}

	public int getMaxUnits() {
		return maxUnits;
	}

	public void setMaxUnits(int maxUnits) {
		this.maxUnits = maxUnits;
	}

	public int getMinUnits() {
		return minUnits;
	}

	public void setMinUnits(int minUnits) {
		this.minUnits = minUnits;
	}

	public List<Object> getAccompaniedBy() {
		return Utils.notEmptyList(accompaniedBy);
	}

	public void setAccompaniedBy(List<Object> accompaniedBy) {
		this.accompaniedBy = accompaniedBy;
	}

	public int getPaxCount() {
		return paxCount;
	}

	public void setPaxCount(int paxCount) {
		this.paxCount = paxCount;
	}

	public int getMinQuantity() {
		return minQuantity;
	}

	public void setMinQuantity(int minQuantity) {
		this.minQuantity = minQuantity;
	}

	public int getMaxAge() {
		return maxAge;
	}

	public void setMaxAge(int maxAge) {
		this.maxAge = maxAge;
	}

	public int getMaxQuantity() {
		return maxQuantity;
	}

	public void setMaxQuantity(int maxQuantity) {
		this.maxQuantity = maxQuantity;
	}

	public int getMinAge() {
		return minAge;
	}

	public void setMinAge(int minAge) {
		this.minAge = minAge;
	}

	public boolean isIdRequired() {
		return idRequired;
	}

	public void setIdRequired(boolean idRequired) {
		this.idRequired = idRequired;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public Object getAccompaniedByRatio() {
		return accompaniedByRatio;
	}

	public void setAccompaniedByRatio(Object accompaniedByRatio) {
		this.accompaniedByRatio = accompaniedByRatio;
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