package com.whosin.app.service.models;

import android.util.Log;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.whosin.app.comman.AppConstants;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Preferences;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CartModel implements DiffIdentifier, ModelProtocol {
    public String id = "";
    public String type = "";
    public int qty = 0;
    public String date = "";
    public String time = "";
    public int maxQty = 0;

    public VenueObjectModel venueModel;
    public ActivityDetailModel activityModel;
    public VoucherModel voucherModel;
    public PackageModel packageModel;
    public OffersModel offerModel;
    public ExclusiveDealModel dealModel;

    public List<CartModel> cartModel = new ArrayList<>();

    private int discountAmount = 0 ;

    public CartModel() {
    }

    public CartModel(PackageModel packageModel, OffersModel offerModel ,VenueObjectModel venueModel, int qty , int maxQty , int discountAmount) {
        this.id = packageModel.getId();
        this.type = "offer";
        this.qty = qty;
        this.offerModel = offerModel;
        this.packageModel = packageModel;
        this.venueModel = venueModel;
        this.maxQty = maxQty;
        this.discountAmount = discountAmount;
    }

    public CartModel(PackageModel packageModel, VenueObjectModel venueModel, int qty,int time, int maxQty, int discountAmount) {
        this.id = packageModel.getId();
        this.type = "event";
        this.qty = qty;
        this.packageModel = packageModel;
        this.venueModel = venueModel;
        this.maxQty = maxQty;
        this.discountAmount = discountAmount;

    }

    public CartModel(ActivityDetailModel activityModel, String date, String time, int qty, int maxQty, int discountAmount) {
        this.id = activityModel.getId();
        this.type = "activity";
        this.qty = qty;
        this.date = date;
        this.time = time;
        this.activityModel = activityModel;
        this.maxQty = maxQty;
        this.discountAmount = discountAmount;
    }


    public CartModel(VoucherModel voucherModel, ExclusiveDealModel dealModel,VenueObjectModel venueModel, String date, String time, int qty, int discountAmount) {
        this.id = voucherModel.getId();
        this.type = "deal";
        this.qty = qty;
        this.venueModel = venueModel;
        this.voucherModel = voucherModel;
        this.dealModel = dealModel;
        this.maxQty = maxQty;
        this.discountAmount = discountAmount;

    }


    public static void addToCart(String id, String itemType, Object itemModel, OffersModel offerModel ,VenueObjectModel venueModel, int qty, String date, String time, ExclusiveDealModel dealModel , int maxQty , int discountAmount) {
        List<CartModel> history = getCartHistory();
        if (history != null && !history.isEmpty()) {
            Optional<CartModel> existingCartItem = history.stream().filter(p -> p.id.equals(id)).findFirst();
            if (existingCartItem.isPresent()) {
                existingCartItem.get().setQty(qty);
                updatePreferences(history);
            } else {
                addToHistory(history, itemType, itemModel, offerModel , venueModel, qty, date, time, dealModel , maxQty,discountAmount);
            }
        } else {
            addToHistory(history, itemType, itemModel, offerModel ,venueModel, qty, date, time, dealModel , maxQty,discountAmount);
        }
    }

    private static void updatePreferences(List<CartModel> cartHistory) {
        String updatedJson = new Gson().toJson(cartHistory);
        Preferences.shared.setString("add_cart_item", updatedJson);
    }

    private static void addToHistory(List<CartModel> history, String itemType, Object itemModel, OffersModel offerModel, VenueObjectModel venueModel, int qty, String date, String time, ExclusiveDealModel dealModel , int maxQty, int discountAmount) {
        if (history == null) {
            history = new ArrayList<>();
        }
        if ("offer".equals(itemType)) {
            history.add(new CartModel((PackageModel) itemModel, offerModel,  venueModel, qty , maxQty,discountAmount));
        } else if ("event".equals(itemType)) {
            history.add(new CartModel((PackageModel) itemModel, venueModel, qty,1,maxQty,discountAmount));
        } else if ("activity".equals(itemType)) {
            history.add(new CartModel((ActivityDetailModel) itemModel, date, time, qty,maxQty,discountAmount));
        } else if ("deal".equals(itemType)) {
            history.add(new CartModel((VoucherModel) itemModel, dealModel,venueModel, date, time, qty,discountAmount));
        }

        Preferences.shared.setString("add_cart_item", new Gson().toJson(history));
    }


    public static List<CartModel> getCartHistory() {
        String json = Preferences.shared.getString("add_cart_item");
        Type type = new TypeToken<List<CartModel>>() {
        }.getType();
        List<CartModel> list = new Gson().fromJson(json, type);
        return list != null ? list : new ArrayList<>();
    }

    public static void clearCart() {
        List<CartModel> emptyList = new ArrayList<>();
        Preferences.shared.setString("add_cart_item", new Gson().toJson(emptyList));
    }


    public AppConstants.CartBlockType getBlockType() {
        switch (getType()) {
            case "offer":
                return AppConstants.CartBlockType.OFFER;
            case "activity":
                return AppConstants.CartBlockType.ACTIVITY;
            case "deal":
                return AppConstants.CartBlockType.DEAL;
            case "event":
                return AppConstants.CartBlockType.Event;


            default:
                return AppConstants.CartBlockType.NONE;
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public VenueObjectModel getVenueModel() {
        return venueModel;
    }

    public void setVenueModel(VenueObjectModel venueModel) {
        this.venueModel = venueModel;
    }

    public ActivityDetailModel getActivityModel() {
        return activityModel;
    }

    public void setActivityModel(ActivityDetailModel activityModel) {
        this.activityModel = activityModel;
    }

    public VoucherModel getVoucherModel() {
        return voucherModel;
    }

    public void setVoucherModel(VoucherModel voucherModel) {
        this.voucherModel = voucherModel;
    }

    public PackageModel getPackageModel() {
        return packageModel;
    }

    public void setPackageModel(PackageModel packageModel) {
        this.packageModel = packageModel;
    }

    public OffersModel getOfferModel() {
        return offerModel;
    }

    public void setOfferModel(OffersModel offerModel) {
        this.offerModel = offerModel;
    }

    public List<CartModel> getCartModel() {
        return cartModel;
    }

    public void setCartModel(List<CartModel> cartModel) {
        this.cartModel = cartModel;
    }

    public ExclusiveDealModel getDealModel() {
        return dealModel;
    }

    public void setDealModel(ExclusiveDealModel dealModel) {
        this.dealModel = dealModel;
    }

    public int getMaxQty() {
        return maxQty;
    }

    public void setMaxQty(int maxQty) {
        this.maxQty = maxQty;
    }


    public int getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(int discountAmount) {
        this.discountAmount = discountAmount;
    }

    @Override
    public boolean isValidModel() {
        return true;
    }

    @Override
    public int getIdentifier() {
        return 0;
    }


    public double getPriceAmount() {
        if (getBlockType() == AppConstants.CartBlockType.OFFER) {
            return getQty() * getDiscountAmount();
        } else if (getBlockType() == AppConstants.CartBlockType.Event) {
            return getQty() * getDiscountAmount();
        } else if (getBlockType() == AppConstants.CartBlockType.DEAL) {
            return getQty() * voucherModel.getDiscountedPrice();
        } else {
            return getQty() * getDiscountAmount();
        }
    }


//    public double getPriceAmount() {
//        if (getBlockType() == AppConstants.CartBlockType.OFFER) {
//            return getQty() * Integer.parseInt(getPackageModel().getAmount());
//        } else if (getBlockType() == AppConstants.CartBlockType.Event) {
//            return getQty() * Integer.parseInt(getPackageModel().getAmount());
//        } else if (getBlockType() == AppConstants.CartBlockType.DEAL) {
//            return getQty() * voucherModel.getDiscountedPrice();
//        } else {
//            return getQty() * activityModel.getPrice();
//        }
//    }
}
