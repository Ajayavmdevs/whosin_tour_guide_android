package com.whosin.app.service.models.bankDetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BankDetailsModel {

    @SerializedName("bankName")
    @Expose
    private String bankName;

    @SerializedName("accountNumber")
    @Expose
    private String accountNumber;

    @SerializedName("iban")
    @Expose
    private String iban;

    @SerializedName("ifsc")
    @Expose
    private String ifsc;

    @SerializedName("routingNumber")
    @Expose
    private String routingNumber;

    @SerializedName("sortCode")
    @Expose
    private String sortCode;

    @SerializedName("bsb")
    @Expose
    private String bsb;

    @SerializedName("swiftCode")
    @Expose
    private String swiftCode;

    public String getBankName() {
        return bankName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getIban() {
        return iban;
    }

    public String getIfsc() {
        return ifsc;
    }

    public String getRoutingNumber() {
        return routingNumber;
    }

    public String getSortCode() {
        return sortCode;
    }

    public String getBsb() {
        return bsb;
    }

    public String getSwiftCode() {
        return swiftCode;
    }
}
