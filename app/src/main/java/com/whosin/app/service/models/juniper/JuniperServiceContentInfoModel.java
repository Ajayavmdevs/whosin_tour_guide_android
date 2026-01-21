package com.whosin.app.service.models.juniper;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;
import com.whosin.app.service.models.ModelProtocol;

public class JuniperServiceContentInfoModel implements DiffIdentifier, ModelProtocol {

	@SerializedName("Descriptions")
	@Expose
	private List<JuniperDescriptionsItemModel> descriptions;

	@SerializedName("ServiceName")
	@Expose
	private String serviceName;

	@SerializedName("Images")
	@Expose
	private List<JuniperImagesItemModel> images;

	public void setDescriptions(List<JuniperDescriptionsItemModel> descriptions){
		this.descriptions = descriptions;
	}

	public List<JuniperDescriptionsItemModel> getDescriptions(){
		return Utils.notEmptyList(descriptions);
	}

	public void setServiceName(String serviceName){
		this.serviceName = serviceName;
	}

	public String getServiceName(){
		return Utils.notNullString(serviceName);
	}

	public void setImages(List<JuniperImagesItemModel> images){
		this.images = images;
	}

	public List<JuniperImagesItemModel> getImages(){
		return Utils.notEmptyList(images);
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