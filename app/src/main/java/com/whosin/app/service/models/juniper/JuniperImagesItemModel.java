package com.whosin.app.service.models.juniper;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;
import com.whosin.app.service.models.ModelProtocol;

public class JuniperImagesItemModel implements DiffIdentifier, ModelProtocol {

	@SerializedName("Type")
	@Expose
	private String type;

	@SerializedName("FileName")
	@Expose
	private String fileName;

	public void setType(String type){
		this.type = type;
	}

	public String getType(){
		return Utils.notNullString(type);
	}

	public void setFileName(String fileName){
		this.fileName = fileName;
	}

	public String getFileName(){
		return Utils.notNullString(fileName);
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