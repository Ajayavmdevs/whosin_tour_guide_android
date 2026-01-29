package com.whosin.business.service.models.JuniperHotelModels;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Utils;
import com.whosin.business.service.models.ModelProtocol;

public class JPHotelBoardForWalletModel implements DiffIdentifier, ModelProtocol {

    @SerializedName("description")
    @Expose
    public String board = "";

    @SerializedName("type")
    @Expose
    public String type = "";

    public String getBoard() {
        return Utils.notNullString(board);
    }

    public void setBoard(String board) {
        this.board = board;
    }

    public String getType() {
        return Utils.notNullString(type);
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFullBoardName() {
        if (board == null) return "";

        StringBuilder boardName = new StringBuilder();

        if (!TextUtils.isEmpty(board)) {
            boardName.append(board).append(" | ");
        }

        if (!TextUtils.isEmpty(type)) {
            boardName.append(type);
        }

        return boardName.toString();
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

