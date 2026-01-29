package com.whosin.business.service.models;

import android.net.Uri;

import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Utils;
import com.whosin.business.service.models.newExploreModels.ExploreBlockModel;

import java.util.List;

public class RatingModel implements DiffIdentifier,ModelProtocol {

    private int id = 0;

    public String review="";

    public String date="";

    public String name="";

    private String image="";

    private Uri uri ;

    private String type = "";

    private int count = 0;

    private List<PromoterEventModel> promoterEventModelList;

    private ExploreBlockModel exploreBlockModel;



    public RatingModel(int id ,String name) {
        this.setName( name );
        this.setId(id);
    }

    public RatingModel(String image) {
        this.setImage( image );
    }

    public RatingModel() {
    }

    public RatingModel(Uri uri) {
        this.uri = uri;
    }

    public RatingModel(Uri uri,String image) {
        this.uri = uri;
        this.image = image;
    }

    public RatingModel(String  type , List<PromoterEventModel> list){
        this.type = type;
        this.promoterEventModelList = list;
    }
    public RatingModel(String  type , ExploreBlockModel model){
        this.type = type;
        this.exploreBlockModel = model;
    }
    public RatingModel(List<PromoterEventModel> list){
        this.promoterEventModelList = list;
    }

    public RatingModel(String type , int count){
        this.type = type;
        this.count = count;
    }


    public boolean isSelected = false;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RatingModel(String review, String date, String name) {
        this.review = review;
        this.date = date;
        this.name = name;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
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


    public String getImage() {
        return Utils.notNullString(image);
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<PromoterEventModel> getPromoterEventModelList() {
        return promoterEventModelList;
    }

    public void setPromoterEventModelList(List<PromoterEventModel> promoterEventModelList) {
        this.promoterEventModelList = promoterEventModelList;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public ExploreBlockModel getExploreBlockModel() {
        return exploreBlockModel;
    }

    public void setExploreBlockModel(ExploreBlockModel exploreBlockModel) {
        this.exploreBlockModel = exploreBlockModel;
    }
}
