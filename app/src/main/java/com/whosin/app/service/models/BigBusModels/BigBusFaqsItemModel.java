package com.whosin.app.service.models.BigBusModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;
import com.whosin.app.service.models.ModelProtocol;

public class BigBusFaqsItemModel implements DiffIdentifier, ModelProtocol {

    @SerializedName("question")
    @Expose
    private String question = "";

    @SerializedName("answer")
    @Expose
    private String answer = "";

    public String getQuestion() {
        return Utils.notNullString(question);
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return Utils.notNullString(answer);
    }

    public void setAnswer(String answer) {
        this.answer = answer;
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