package com.whosin.business.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Utils;

public class CurrencyModel implements DiffIdentifier, ModelProtocol{

    @SerializedName("_id")
    @Expose
    private String _id = "";

    @SerializedName("currency")
    @Expose
    private String currency = "";

    @SerializedName("rate")
    @Expose
    private Float rate ;

    @SerializedName("symbol")
    @Expose
    private String symbol = "" ;

    @SerializedName("flag")
    @Expose
    private String flag = "" ;

    public String get_id() {
        return Utils.notNullString(_id);
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getCurrency() {
        return Utils.notNullString(currency);
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Float getRate() {
        return rate;
    }

    public void setRate(Float rate) {
        this.rate = rate;
    }

    public String getSymbol() {
        return Utils.notNullString(symbol);
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
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
