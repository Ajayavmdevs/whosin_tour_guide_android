package com.whosin.app.service.models.BigBusModels;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;
import com.whosin.app.service.models.ModelProtocol;

public class BigBusCategoriesItemModel implements DiffIdentifier, ModelProtocol {

	@SerializedName("default")
	@Expose
	private boolean jsonMemberDefault = false;

	@SerializedName("coverImageUrl")
	@Expose
	private String coverImageUrl = "";

	@SerializedName("bannerImageUrl")
	@Expose
	private String bannerImageUrl = "";

	@SerializedName("name")
	@Expose
	private String name = "";

	@SerializedName("id")
	@Expose
	private String id = "";

	@SerializedName("shortDescription")
	@Expose
	private String shortDescription = "";

	@SerializedName("title")
	@Expose
	private String title = "";

	@SerializedName("tags")
	@Expose
	private List<Object> tags;

	public boolean isJsonMemberDefault() {
		return jsonMemberDefault;
	}

	public void setJsonMemberDefault(boolean jsonMemberDefault) {
		this.jsonMemberDefault = jsonMemberDefault;
	}

	public String getCoverImageUrl() {
		return Utils.notNullString(coverImageUrl);
	}

	public void setCoverImageUrl(String coverImageUrl) {
		this.coverImageUrl = coverImageUrl;
	}

	public String getBannerImageUrl() {
		return Utils.notNullString(bannerImageUrl);
	}

	public void setBannerImageUrl(String bannerImageUrl) {
		this.bannerImageUrl = bannerImageUrl;
	}

	public String getName() {
		return Utils.notNullString(name);
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return Utils.notNullString(id);
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getShortDescription() {
		return Utils.notNullString(shortDescription);
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	public String getTitle() {
		return Utils.notNullString(title);
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<Object> getTags() {
		return Utils.notEmptyList(tags);
	}

	public void setTags(List<Object> tags) {
		this.tags = tags;
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