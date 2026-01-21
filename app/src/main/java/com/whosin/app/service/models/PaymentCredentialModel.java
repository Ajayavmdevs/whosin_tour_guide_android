package com.whosin.app.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaymentCredentialModel implements ModelProtocol{

    @SerializedName("_id")
    @Expose
    public String id;

    @SerializedName("client_secret")
    @Expose
    public String clientSecret;

    @SerializedName("secret_key")
    @Expose
    public String secretKey;

    @SerializedName("publishable_key")
    @Expose
    public String publishableKey;

    @SerializedName("tabby")
    @Expose
    public PaymentTabbyModel tabbyModel;

    @SerializedName("reference")
    @Expose
    private String reference;

    @SerializedName("outletId")
    @Expose
    private String outletId;

    @SerializedName("amount")
    @Expose
    private AmountModel amount;

    @SerializedName("_links")
    @Expose
    private LinksModel links;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getReference() {
        return reference != null ? reference : "";
    }

    public String getOutletId() {
        return outletId != null ? outletId : "";
    }

    public AmountModel getAmount() {
        return amount;
    }

    public LinksModel getLinks() {
        return links;
    }

    public String getPublishableKey() {
        return publishableKey;
    }

    public void setPublishableKey(String publishableKey) {
        this.publishableKey = publishableKey;
    }

    public PaymentTabbyModel getTabbyModel() {
        return tabbyModel;
    }

    public void setTabbyModel(PaymentTabbyModel tabbyModel) {
        this.tabbyModel = tabbyModel;
    }

    @Override
    public boolean isValidModel() {
        return true;
    }

    public static class AmountModel {
        @SerializedName("currencyCode")
        @Expose
        public String currencyCode;

        @SerializedName("value")
        @Expose
        public long value;   // 6900 means 69.00 AED
    }


    public static class LinksModel {

        @SerializedName("payment")
        @Expose
        public PaymentLink payment;

        @SerializedName("cancel")
        @Expose
        public PaymentLink cancel;

        @SerializedName("payment-authorization")
        @Expose
        public PaymentLink paymentAuthorization;

        @SerializedName("cnp:payment-link")
        @Expose
        public PaymentLink cnpPaymentLink;

        @SerializedName("self")
        @Expose
        public PaymentLink self;

        public static class PaymentLink {
            @SerializedName("href")
            @Expose
            public String href;
        }
    }

}
