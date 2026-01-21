package com.whosin.app.service.models.rayna;

import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.service.models.ModelProtocol;

public class PaymentSelectModel implements DiffIdentifier, ModelProtocol {

    private int image;

    private String title;

    private String description;

    private boolean isSelect = false;

    private int id = 0;


    public PaymentSelectModel(){}

    public PaymentSelectModel(int id , int image ,String title ,String description , boolean isSelect){
      this.id = id;
      this.image = image;
      this.title = title;
      this.description = description;
      this.isSelect = isSelect;
    }


    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
