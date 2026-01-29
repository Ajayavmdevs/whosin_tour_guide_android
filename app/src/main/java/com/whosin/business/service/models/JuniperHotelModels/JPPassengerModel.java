package com.whosin.business.service.models.JuniperHotelModels;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Utils;
import com.whosin.business.service.models.ModelProtocol;

public class JPPassengerModel implements DiffIdentifier, ModelProtocol {

    @SerializedName("serviceType")
    @Expose
    private String serviceType = "Hotel";

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("prefix")
    @Expose
    private String prefix;

    @SerializedName("firstName")
    @Expose
    private String firstName;

    @SerializedName("lastName")
    @Expose
    private String lastName;

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("mobile")
    @Expose
    private String mobile;

    @SerializedName("nationality")
    @Expose
    private String nationality;

    @SerializedName("leadPassenger")
    @Expose
    private int leadPassenger;

    @SerializedName("paxType")
    @Expose
    private String paxType;

    @SerializedName("countryCode")
    @Expose
    private String countryCode;

    @SerializedName("age")
    @Expose
    private String age;

    @Expose(serialize = false, deserialize = false)
    private String tmpCountryCode;

    public JPPassengerModel() {
    }

    public JPPassengerModel(String id, String prefix, String firstName, String lastName, String email, String mobile, String nationality, int leadPassenger, String paxType, String countryCode,String age) {
        this.id = id;
        this.prefix = prefix;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.mobile = mobile;
        this.nationality = nationality;
        this.leadPassenger = leadPassenger;
        this.paxType = paxType;
        this.tmpCountryCode = countryCode;
        this.age = age;
    }

    public JPPassengerModel(String id,String prefix,int leadPassenger,String paxType,String email,String mobile,String nationality,String countryCode) {
        this.id = id;
        this.prefix = prefix;
        this.leadPassenger = leadPassenger;
        this.paxType = paxType;
        this.email = email;
        this.mobile = mobile;
        this.nationality = nationality;
        this.tmpCountryCode = countryCode;
    }

    public JPPassengerModel(JPPassengerModel other) {
        this.id = other.id;
        this.firstName = other.firstName;
        this.lastName = other.lastName;
        this.email = other.email;
        this.mobile = other.mobile;
        this.age = other.age;
        this.prefix = other.prefix;
        this.paxType = other.paxType;
        this.serviceType = other.serviceType;
        this.leadPassenger = other.leadPassenger;
        this.nationality = other.nationality;
        this.countryCode = other.countryCode;
        this.tmpCountryCode = other.tmpCountryCode;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getFirstName() {
        return Utils.notNullString(firstName);
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return Utils.notNullString(lastName);
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return Utils.notNullString(email);
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return Utils.notNullString(mobile);
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public int getLeadPassenger() {
        return leadPassenger;
    }

    public void setLeadPassenger(int leadPassenger) {
        this.leadPassenger = leadPassenger;
    }

    public String getPaxType() {
        return paxType;
    }

    public void setPaxType(String paxType) {
        this.paxType = paxType;
    }

    public String getTmpCountryCode() {
        return Utils.notNullString(tmpCountryCode);
    }

    public void setTmpCountryCode(String tmpCountryCode) {
        this.tmpCountryCode = tmpCountryCode;
    }

    public String getAge() {
        return Utils.notNullString(age);
    }

    public void setAge(String age) {
        this.age = age;
    }

    @Override
    public int getIdentifier() {
        return 0;
    }

    @Override
    public boolean isValidModel() {
        return true;
    }


    public boolean allValueFilled() {
        if (TextUtils.isEmpty(firstName)) return false;
        if (TextUtils.isEmpty(lastName)) return false;
        if (TextUtils.isEmpty(age)) return false;
        if (!isValidAge()) return false;
        if (TextUtils.isEmpty(email) || !Utils.isValidEmail(email)) return false;
        if (TextUtils.isEmpty(mobile) || !Utils.isValidPhoneNumber(countryCode, mobile)) return false;
        return !TextUtils.isEmpty(nationality);
    }

    public boolean isValidAge() {
        if (TextUtils.isEmpty(age)) return false;
        String cleanedAge = age.trim().replaceAll("[^0-9]", "");
        if (TextUtils.isEmpty(cleanedAge)) return false;
        int tmpAge;
        try {
            tmpAge = Integer.parseInt(cleanedAge);
        } catch (NumberFormatException e) {
            return false;
        }

        if ("adult".equalsIgnoreCase(paxType)) {
            return tmpAge >= 18 && tmpAge <= 120;
        } else {
            return tmpAge >= 0 && tmpAge <= 17;
        }
    }


}
