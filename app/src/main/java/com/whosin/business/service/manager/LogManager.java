package com.whosin.business.service.manager;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.whosin.business.comman.Utils;
import com.whosin.business.service.models.UserDetailModel;

public class LogManager {

    private static final String TAG = "LogManager";
    public static LogManager shared = LogManager.getInstance();

    private static volatile LogManager instance = null;
    private FirebaseAnalytics mFirebaseAnalytics;
    private Context context;

    private LogManager() {
    }

    public static synchronized LogManager getInstance() {
        if (instance == null) {
            instance = new LogManager();
        }
        return instance;
    }

    public void init(Context context) {
        this.context = context;
        this.mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    private FirebaseAnalytics getAnalytics() {
        if (mFirebaseAnalytics == null) {
            if (context != null) {
                mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
            } else if (SessionManager.shared.getContext() != null) {
                mFirebaseAnalytics = FirebaseAnalytics.getInstance(SessionManager.shared.getContext());
            }
        }
        return mFirebaseAnalytics;
    }

    private String getUserId() {
        UserDetailModel user = SessionManager.shared.getUser();
        if (user != null && !Utils.isNullOrEmpty(user.getId())) {
            return user.getId();
        }
        return "Guest";
    }

    public enum LogEventType {
        viewTicket,
        viewCart,
        removeCart,
        getTicket,
        addToCart,
        purchase,
        addToWishlist,
        paymentInitiated,
        addUserInfo,
        paymentFailed,
        paymentCancelled
    }

    public void logTicketEvent(LogEventType type, String id, String name, Double price, String transactionId, String currency) {
        FirebaseAnalytics analytics = getAnalytics();
        if (analytics == null) {
            Log.e(TAG, "FirebaseAnalytics instance is null. Cannot log event.");
            return;
        }

        String userId = getUserId();
        analytics.setUserId(userId);

        Bundle params = new Bundle();
        params.putString(FirebaseAnalytics.Param.ITEM_ID, id);
        params.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
        params.putString("user_id", userId);

        if (price != null) {
            params.putDouble(FirebaseAnalytics.Param.PRICE, price);
            params.putDouble(FirebaseAnalytics.Param.VALUE, price);
            params.putString(FirebaseAnalytics.Param.CURRENCY, currency != null ? currency : "AED");
        }

        if (transactionId != null) {
            params.putString(FirebaseAnalytics.Param.TRANSACTION_ID, transactionId);
        }

        String eventName = "";

        switch (type) {
            case viewTicket:
                eventName = "view_ticket";
                break;
            case getTicket:
                eventName = "get_ticket";
                break;
            case addToCart:
                eventName = FirebaseAnalytics.Event.ADD_TO_CART;
                break;
            case viewCart:
                eventName = FirebaseAnalytics.Event.VIEW_CART;
                break;
            case removeCart:
                eventName = FirebaseAnalytics.Event.REMOVE_FROM_CART;
                break;
            case purchase:
                eventName = FirebaseAnalytics.Event.PURCHASE;
                break;
            case addToWishlist:
                eventName = FirebaseAnalytics.Event.ADD_TO_WISHLIST;
                break;
            case paymentInitiated:
                eventName = "payment_initiated";
                break;
            case addUserInfo:
                eventName = "add_guest_detail";
                break;
            case paymentFailed:
                eventName = "payment_failed";
                break;
            case paymentCancelled:
                eventName = "payment_cancelled";
                break;
        }

        if (!eventName.isEmpty()) {
            Log.d(TAG, "Logging event: " + eventName + " params: " + params.toString());
            analytics.logEvent(eventName, params);
        } else {
            Log.w(TAG, "Event name is empty for type: " + type);
        }
    }
}
