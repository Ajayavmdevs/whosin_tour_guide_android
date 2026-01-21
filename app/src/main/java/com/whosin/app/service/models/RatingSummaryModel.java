package com.whosin.app.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;

public class RatingSummaryModel implements DiffIdentifier,ModelProtocol {


    @SerializedName("1")
    @Expose
    private RatingSummaryPercentageModel _1;
    @SerializedName("2")
    @Expose
    private RatingSummaryPercentageModel _2;
    @SerializedName("3")
    @Expose
    private RatingSummaryPercentageModel _3;
    @SerializedName("4")
    @Expose
    private RatingSummaryPercentageModel _4;
    @SerializedName("5")
    @Expose
    private RatingSummaryPercentageModel _5;

    public RatingSummaryPercentageModel get1() {
        return _1;
    }

    public void set1(RatingSummaryPercentageModel _1) {
        this._1 = _1;
    }

    public RatingSummaryPercentageModel get2() {
        return _2;
    }

    public void set2(RatingSummaryPercentageModel _2) {
        this._2 = _2;
    }

    public RatingSummaryPercentageModel get3() {
        return _3;
    }

    public void set3(RatingSummaryPercentageModel _3) {
        this._3 = _3;
    }

    public RatingSummaryPercentageModel get4() {
        return _4;
    }

    public void set4(RatingSummaryPercentageModel _4) {
        this._4 = _4;
    }

    public RatingSummaryPercentageModel get5() {
        return _5;
    }

    public void set5(RatingSummaryPercentageModel _5) {
        this._5 = _5;
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
