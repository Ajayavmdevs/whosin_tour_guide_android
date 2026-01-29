package com.whosin.business.service.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Utils;

import java.util.List;

public class InAppListUserModel  implements DiffIdentifier, ModelProtocol{

    @SerializedName("total")
    @Expose
    private int total = 0;

    @SerializedName("page")
    @Expose
    private int page = 0;

    @SerializedName("list")
    @Expose
    private List<InAppNotificationModel> list;


    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public List<InAppNotificationModel> getList() {
        return Utils.notEmptyList(list);
    }

    public void setList(List<InAppNotificationModel> list) {
        this.list = list;
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
