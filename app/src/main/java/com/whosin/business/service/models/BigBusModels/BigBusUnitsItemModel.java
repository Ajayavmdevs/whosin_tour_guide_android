package com.whosin.business.service.models.BigBusModels;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Utils;
import com.whosin.business.service.models.ModelProtocol;

public class BigBusUnitsItemModel implements DiffIdentifier, ModelProtocol {

	@SerializedName("reference")
	@Expose
	private Object reference;

	@SerializedName("requiredContactFields")
	@Expose
	private List<Object> requiredContactFields;

	@SerializedName("internalName")
	@Expose
	private String internalName = "";

	@SerializedName("visibleContactFields")
	@Expose
	private List<Object> visibleContactFields;

	@SerializedName("subtitle")
	@Expose
	private String subtitle = "";

	@SerializedName("pricingFrom")
	@Expose
	private List<BigBusPricingFromItemModel> pricingFrom;

	@SerializedName("restrictions")
	@Expose
	private BigBusRestrictionsModel restrictions;

	@SerializedName("id")
	@Expose
	private String id = "";

	@SerializedName("type")
	@Expose
	private String type = "";

	@SerializedName("title")
	@Expose
	private String title = "";

	@SerializedName("tags")
	@Expose
	private List<String> tags;

	@SerializedName("titlePlural")
	@Expose
	private String titlePlural = "";


	public Object getReference() {
		return reference;
	}

	public void setReference(Object reference) {
		this.reference = reference;
	}

	public List<Object> getRequiredContactFields() {
		return requiredContactFields;
	}

	public void setRequiredContactFields(List<Object> requiredContactFields) {
		this.requiredContactFields = requiredContactFields;
	}

	public String getInternalName() {
		return Utils.notNullString(internalName);
	}

	public void setInternalName(String internalName) {
		this.internalName = internalName;
	}

	public List<Object> getVisibleContactFields() {
		return Utils.notEmptyList(visibleContactFields);
	}

	public void setVisibleContactFields(List<Object> visibleContactFields) {
		this.visibleContactFields = visibleContactFields;
	}

	public String getSubtitle() {
		return Utils.notNullString(subtitle);
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	public List<BigBusPricingFromItemModel> getPricingFrom() {
		return Utils.notEmptyList(pricingFrom);
	}

	public void setPricingFrom(List<BigBusPricingFromItemModel> pricingFrom) {
		this.pricingFrom = pricingFrom;
	}

	public BigBusRestrictionsModel getRestrictions() {
		return restrictions;
	}

	public void setRestrictions(BigBusRestrictionsModel restrictions) {
		this.restrictions = restrictions;
	}

	public String getId() {
		return Utils.notNullString(id);
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return Utils.notNullString(type);
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTitle() {
		return Utils.notNullString(title);
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<String> getTags() {
		return Utils.notEmptyList(tags);
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public String getTitlePlural() {
		return Utils.notNullString(titlePlural);
	}

	public void setTitlePlural(String titlePlural) {
		this.titlePlural = titlePlural;
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