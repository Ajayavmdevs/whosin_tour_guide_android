package com.whosin.app.service.models;

import com.whosin.app.comman.DiffIdentifier;

import java.util.ArrayList;
import java.util.List;

public class AddToCartModel implements DiffIdentifier,ModelProtocol {

    private List<CartModel> cartModelList = new ArrayList<>();

    public void addItem(String type, Object model) {
//        CartModel cartModel = new CartModel(type, model);
//        cartModelList.add(cartModel);
    }

    @Override
    public int getIdentifier() {
        return 0;
    }

    @Override
    public boolean isValidModel() {
        return false;
    }
}
