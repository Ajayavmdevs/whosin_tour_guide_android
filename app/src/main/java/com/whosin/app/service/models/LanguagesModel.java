package com.whosin.app.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;

public class LanguagesModel implements DiffIdentifier, ModelProtocol {

    @SerializedName("code")
    @Expose
    private String code = "";

    @SerializedName("name")
    @Expose
    private String name = "";

    @SerializedName("native_name")
    @Expose
    private String native_name = "";

    @SerializedName("flag")
    @Expose
    private String flag = "";

    public String getCode() {
        return Utils.notNullString(code);
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return Utils.notNullString(name);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNative_name() {
        return Utils.notNullString(native_name);
    }

    public void setNative_name(String native_name) {
        this.native_name = native_name;
    }

    public String getFlag() {
        return Utils.notNullString(flag);
    }

    public void setFlag(String flag) {
        this.flag = flag;
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
