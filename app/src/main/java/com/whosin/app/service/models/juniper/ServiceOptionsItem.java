package com.whosin.app.service.models.juniper;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;
import com.whosin.app.service.models.ModelProtocol;

public class ServiceOptionsItem implements DiffIdentifier, ModelProtocol {

	@SerializedName("Order")
	@Expose
	private int order;

	@SerializedName("NumberOfDays")
	@Expose
	private int numberOfDays;

	@SerializedName("Descriptions")
	@Expose
	private List<JuniperDescriptionsItemModel> descriptions;

	@SerializedName("MinimumPax")
	@Expose
	private int minimumPax;

	@SerializedName("StartTime")
	@Expose
	private String startTime;

	@SerializedName("Name")
	@Expose
	private String name;

	@SerializedName("Zones")
	@Expose
	private List<JuniperZonesItemModel> zones;

	public void setOrder(int order){
		this.order = order;
	}

	public int getOrder(){
		return order;
	}

	public void setNumberOfDays(int numberOfDays){
		this.numberOfDays = numberOfDays;
	}

	public int getNumberOfDays(){
		return numberOfDays;
	}

	public void setDescriptions(List<JuniperDescriptionsItemModel> descriptions){
		this.descriptions = descriptions;
	}

	public List<JuniperDescriptionsItemModel> getDescriptions(){
		return Utils.notEmptyList(descriptions);
	}

	public void setMinimumPax(int minimumPax){
		this.minimumPax = minimumPax;
	}

	public int getMinimumPax(){
		return minimumPax;
	}

	public void setStartTime(String startTime){
		this.startTime = startTime;
	}

	public String getStartTime(){
		return Utils.notNullString(startTime);
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return Utils.notNullString(name);
	}

	public void setZones(List<JuniperZonesItemModel> zones){
		this.zones = zones;
	}

	public List<JuniperZonesItemModel> getZones(){
		return Utils.notEmptyList(zones);
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