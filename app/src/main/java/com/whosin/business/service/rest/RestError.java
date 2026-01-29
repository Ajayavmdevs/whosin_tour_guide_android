package com.whosin.business.service.rest;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

public enum RestError {
    NO_ERROR(1001, "No Error", "error_code_1001"),
    INVALID_REQUEST(1002, "Invalid Request", "error_code_1002"),
    LOGIN_ERROR(1003, "Login Error", "error_code_1003"),
    STORED_PROCEDURE_ERROR(1004, "Stored Procedure not found any record", "error_code_1004"),
    EXCEPTION_ERROR(1005, "Exception during procedure", "error_code_1005"),
    INSUFFICIENT_PERMISSION(1006, "Insufficient Permission to perform the operation", "error_code_1006"),
    DUPLICATE_DATA(1007, "Record have been taken, please try with different input parameters", "error_code_1007"),
    RECORD_NOT_FOUND(1008, "Record not found in the database", "error_code_1008"),
    RESOURCE_BUSY(1009, "Resource is busy", "error_code_1009"),
    SERVER_COMMAND_ERROR(1010, "Server Command error", "error_code_1010"),
    CLIENT_COMMAND_ERROR(1011, "Client Command error", "error_code_1011"),
    USER_BUSY(1012, "User busy", "error_code_1012"),/*using for intercom session*/
    HARDWARE_API_ERROR(1013, "Hareware API error", "error_code_1013"),
    OPENHAB_ITEM_NOT_FOUND(1014, "OpenHab Item Name '%s' Not Found", "error_code_1014"),
    RULE_CAN_NOT_ACTIVE(1015, "Rule can not active. Please check device again.", "error_code_1015"),
    TIMEOUT_RECEIVE_DEVICE_RESPONSE_ERROR(1016, "Timeout receive response from device.", "error_code_1016"),
    IO_EXCEPTION(1017, "IO Exception", "error_code_1017"),
    REQUEST_TIMEOUT(1018, "Request timeout or network unstable to transport data", "error_code_1018"),
    RULE_IS_ACTIVATING(1019, "Arm mode is activating. Please waiting.", "error_code_1019"),
    SCENE_IS_ACTIVATING(1020, "Scene mode is applying. Please waiting.", "error_code_1020"),
    BULK_COMMAND_SENT_SUCCESSFUL(10017, "Bulk Command sent successful to center hub.", "error_code_10017"),
    BULK_COMMAND_NOT_SUPPORT(10018, "Bulk Command is not support.", "error_code_10018"),
    FAVORITE_DEVICE_LIMIT(2060301, "Favorite device limit to 6", "error_code_2060301"),
    ERROR_CODE_2020102(2020102, "Some device not online now. can not active arm mode, please check again.", "error_code_2020102"),
    ERROR_CODE_8000100(8000100, "Account not found or you don't have permission to access to this account", "error_code_8000100"),
    ERROR_CODE_8050300(8050300, "Room not found or you don't have permission to access to this room", "error_code_8050300"),
    ERROR_CODE_8000400(8000400, "Permission not found", "error_code_8000400"),
    ERROR_CODE_8050200(8050200, "Floor not found or you don't have permission to access to this floor", "error_code_8050200"),
    ERROR_CODE_8010100(8010100, "Device not found or you don't have permission to access to this device", "error_code_8010100"),
    ERROR_CODE_2000401(2000401, "Permission type error", "error_code_2000401"),
    ERROR_CODE_2000402(2000402, "Your Role can not grant permission, Please check master account.", "error_code_2000402"),
    ERROR_CODE_2070109(2070109, "This automation is not create by you, You can not edit it", "error_code_2070109"),
    ERROR_CODE_2070106(2070106, "Please check input trigger action for automation", "error_code_2070106"),
    ERROR_CODE_2070105(2070105, "Please check input condition for automation", "error_code_2070105"),
    ERROR_CODE_2070103(2070103, "Automation name is exists", "error_code_2070103"),
    ERROR_CODE_2020100(2020100, "Arm Custom name is exists", "error_code_2020100"),
    ERROR_CODE_2070204(2070204, "Scene name is exists", "error_code_2070204"),
    ERROR_CODE_2070108(2070108, "Please check input time input", "error_code_2070108"),
    TIME_OUT(0, "Time out", "error_code_0"),
    INTERNET_CONNECT_FAILED(404,"The internet connection appears to be offline", "network_connection_error"),
    SERVER_OFFLINE(1, "Server is offline", "error_code_1"),
    UNKNOWN(-1, "Something went wrong", "service_message_something_wrong"),
    UNAUTHORIZED(401, "", "");

    private static final Map<Integer, RestError> mapping = new HashMap<>();
    private Integer code;
    private String defaultMessage;
    private String keyMessage;

    static {
        for (RestError re : RestError.values()) {
            mapping.put(re.code, re);
        }
    }

    RestError(Integer code, String defaultMessage, String keyMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.keyMessage = keyMessage;
    }

    public Integer getCode() {
        return code;
    }

    public String getKeyMessage() {
        return keyMessage;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }

    @Nullable
    public static RestError from(Integer code) {
        return mapping.getOrDefault(code, UNKNOWN);
    }

    public String getLabel(Context context) {
        Resources res = context.getResources();
        int resId = res.getIdentifier(getKeyMessage(), "string", context.getPackageName());
        if (0 != resId) {
            return (res.getString(resId));
        }
        return (getKeyMessage());
    }

    public static void handleError(Context context, RestCallback<?> delegate, String error, Integer errorCode) {
        Log.d("TAG_ERROR", "Errorcode = " + errorCode + " : " + error);
        if (context == null) {
            return;
        }

        if (errorCode == 1003) {
            error = RestError.from(LOGIN_ERROR.getCode()).getLabel(context);
            delegate.result(null,error);
            return;
        }

        if (error.contains("Unable to resolve host") || error.contains("Failed to connect") || error.contains("time out")) {
            delegate.result(null,RestError.from(INTERNET_CONNECT_FAILED.getCode()).getLabel(context));
        } else if (error.contains("Deskspace is offline")) {
            delegate.result(null,RestError.from(SERVER_OFFLINE.getCode()).getLabel(context));
        } else if (error.contains("404") || error.contains("503")) {
            delegate.result(null,RestError.from(INTERNET_CONNECT_FAILED.getCode()).getLabel(context));
        } else {
            if (errorCode != -1 && RestError.from(errorCode).getCode().equals(RestError.UNKNOWN.getCode())) {
                delegate.result(null, error);
            } else {
                delegate.result(null, RestError.from(errorCode).getLabel(context));
            }
        }
    }
}
