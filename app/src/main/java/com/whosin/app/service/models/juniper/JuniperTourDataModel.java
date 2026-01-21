package com.whosin.app.service.models.juniper;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;
import com.whosin.app.service.models.ModelProtocol;

public class JuniperTourDataModel implements DiffIdentifier, ModelProtocol {

	@SerializedName("serviceType")
	@Expose
	private List<JuniperServiceTypeItemModel> serviceType;

	@SerializedName("code")
	@Expose
	private String code;

	@SerializedName("serviceContentInfo")
	@Expose
	private JuniperServiceContentInfoModel serviceContentInfo;

	@SerializedName("zone")
	@Expose
	private JuniperZoneModel zone;

	@SerializedName("name")
	@Expose
	private String name;

	@SerializedName("serviceCodes")
	@Expose
	private List<String> serviceCodes;

	@SerializedName("intCode")
	@Expose
	private String intCode;

	@SerializedName("_id")
	@Expose
	private String id;

	@SerializedName("serviceOptions")
	@Expose
	private List<ServiceOptionsItem> serviceOptions;

	@SerializedName("serviceZones")
	@Expose
	private List<JuniperServiceZonesItemModel> serviceZones;

	@SerializedName("serviceTypeCode")
	@Expose
	private String serviceTypeCode;

	public void setServiceType(List<JuniperServiceTypeItemModel> serviceType){
		this.serviceType = serviceType;
	}

	public List<JuniperServiceTypeItemModel> getServiceType(){
		return Utils.notEmptyList(serviceType);
	}

	public void setCode(String code){
		this.code = code;
	}

	public String getCode(){
		return Utils.notNullString(code);
	}

	public void setServiceContentInfo(JuniperServiceContentInfoModel serviceContentInfo){
		this.serviceContentInfo = serviceContentInfo;
	}

	public JuniperServiceContentInfoModel getServiceContentInfo(){
		return serviceContentInfo;
	}

	public void setZone(JuniperZoneModel zone){
		this.zone = zone;
	}

	public JuniperZoneModel getZone(){
		return zone;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return Utils.notNullString(name);
	}

	public void setServiceCodes(List<String> serviceCodes){
		this.serviceCodes = serviceCodes;
	}

	public List<String> getServiceCodes(){
		return Utils.notEmptyList(serviceCodes);
	}

	public void setIntCode(String intCode){
		this.intCode = intCode;
	}

	public String getIntCode(){
		return Utils.notNullString(intCode);
	}

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return Utils.notNullString(id);
	}

	public void setServiceOptions(List<ServiceOptionsItem> serviceOptions){
		this.serviceOptions = serviceOptions;
	}

	public List<ServiceOptionsItem> getServiceOptions(){
		return Utils.notEmptyList(serviceOptions);
	}

	public void setServiceZones(List<JuniperServiceZonesItemModel> serviceZones){
		this.serviceZones = serviceZones;
	}

	public List<JuniperServiceZonesItemModel> getServiceZones(){
		return Utils.notEmptyList(serviceZones);
	}

	public void setServiceTypeCode(String serviceTypeCode){
		this.serviceTypeCode = serviceTypeCode;
	}

	public String getServiceTypeCode(){
		return Utils.notNullString(serviceTypeCode);
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