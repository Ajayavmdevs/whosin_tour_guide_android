package com.whosin.business.service.models.BigBusModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Utils;
import com.whosin.business.service.models.ModelProtocol;

public class BigBusGalleryImagesItemModel implements DiffIdentifier, ModelProtocol {

	@SerializedName("caption")
	@Expose
	private String caption = "";

	@SerializedName("title")
	@Expose
	private String title = "";

	@SerializedName("url")
	@Expose
	private String url = "" ;


	public String getCaption() {
		return Utils.notNullString(caption);
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getTitle() {
		return Utils.notNullString(title);
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return Utils.notNullString(url);
	}

	public void setUrl(String url) {
		this.url = url;
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