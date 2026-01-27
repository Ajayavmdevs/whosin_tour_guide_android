package com.whosin.app.service;

public class EndpointConstants {

    public static final String AUTH_GOOGLE_LOGIN_ENDPOINT = "user/login/google";
    public static final String AUTH_PHONE_LOGIN_NEW_ENDPOINT = "user/login-new";
    public static final String GUEST_LOGIN_ENDPOINT = "user/login/guest";
    public static final String AUTH_VERIFY_OTP_ENDPOINT = "user/verify";
    public static final String USER_SENT_OTP = "user/send/otp";
    public static final String USER_VERIFY_OTP = "user/verify/otp";
    public static final String USER_UPDATE_FCM_TOKEN = "user/fcm/token";

    public  static final String USER_TWO_AUTH_EMAIL_REQUEST = "user/authenticate/email";
    public  static final String USER_LOGOUT = "user/logout";
    public static final String AUTH_VERIFY_PASSWORD_ENDPOINT = "auth/verify/password";
    public static final String UPLOAD_IMAGE_ENDPOINT = "comman/img/upload";
    public static final String UPDATE_PROFILE_ENDPOINT = "user/update";

    public static final String USER_LIST_BY_IDS_ENDPOINT = "user/ids";
    public static final String APP_SETTING_ENDPOINT = "comman/setting/get";
    public static final String SELECTED_PREFERENCES_ENDPOINT = "user/preference/update";

    public static final String SELECTED_PREFERENCES_GET_ENDPOINT = "user/preference/get";
    public static final String HOME_BLOCK_LIST = "homeblock/get-blocks";
    public static final String CONTACT_LIST = "user/contact-sync";
    public static final String ALL_REVIEW_LIST = "review/list";
    public static final String RATING_SUMMARY = "review/summary";

    public static final String SUBMIT_RATE = "review/addUpdate";


    public static final String FOLLOW_UNFOLLOW_USER = "user/follow/add";

    public static final String FOLLOWERS_LIST = "user/followers/list";

    public static final String FOLLOWING_LIST = "user/following/list";

    public static final String CHAT_CREATE  = "chat/friend/create";
    public static final String EVENT_CHAT_LIST  = "event/chat/list";
    public static final String CHAT_UPLOAD_LIST  = "chat/upload";

    public static final String BLOCK_USER_ADD  = "user/block/add";

    public static final String CHANGE_USER_EMAIL_PHONE = "user/user/link/email-phone";
    public static final String USER_OTP_UPDATE = "user/verify/otp/update";
    public static final String COMMAN_SEARCH = "comman/search";


    public static final String  USER_PROFILE = "user/profile/";
    public static final String  WALLET_MY_ITEM = "subscription/order/list";
    public static final String  USER_FEED_MY_ENDPOINT = "user/feed/my";
    public static final String  USER_FEED_MY_FRIEND = "user/feed/friend";
    public static final String  WALLET_HISTORY = "subscription/order/history";


    public static final String  USER_NOTIFICATION_LIST  = "user/notification/list";

    public static final String  SYNC_CHAT_MSG_LIST = "chat/messages/unreceived";
    public static final String  DELETE_CHAT = "chat/delete";
    public static final String LINK_CREATE = "link/create";

    public static final String COMMAN_CONTACT_US_ADD_QUERY = "comman/contact-us/add-query";
    public static final String COMMAN_CONTACT_US_QUERY_LIST = "comman/contact-us/query-list";
    public static final String COMMAN_CONTACT_US_QUERY_REPLY = "comman/contact-us/query-reply";

    //User Block

    public static final String USER_BLOCK_LIST= "user/block/list";
    public static final String USER_BLOCK_REMOVE= "user/block/remove";
    public static final String USER_DELETE_ACCOUNT= "user/delete/account";
    public static final String USER_NOTIFICATION_READ= "user/notification/read";
    public static final String USER_NOTIFICATION_UNREAD_COUNT= "user/notification/unread-count";
    public static final String APPROVE_LOGIN_REQUEST = "user/approve/login/request";
    public static final String COMMAN_UPDATE_STATUS= "comman/updates/get";
    public static final String COMMAN_UPDATES_READS= "comman/updates/read";
    public static final String SEARCH_GET_HOME_BLOCK= "homeblock/search/get-blocks";
    public static final String USER_UPDATE_SETTINGS = "user/update-settings";
    public static final String USER_FOLLOW_REQUEST_LIST = "user/follow-request/list";
    public static final String USER_FOLLOW_REQUEST_ACTION = "user/follow-request/action";
    public static final String CONTACT_US_REPLY_MARK_AS_READ = "comman/contact-us/reply/mark-as-read";
    public static final String USER_AUTH_REQUEST = "user/auth/request";

    public static final String REPLAY_ADD_UPDATE_REVIEW = "review/reply/addUpdate";
    public static final String DELETE_REVIEW = "review/reply/delete";

    //Rayna

    public static final String RAYNA_CUSTOM_USER_DETAIL = "rayna/custom/detail-user";
    public static final String RAYNA_SEARCH = "rayna/search";
    public static final String RAYNA_TOUR_OPTIONS = "rayna/tour-options";
    public static final String RAYNA_TOUR_TIMESLOT = "rayna/tour-timeslot";
    public static final String RAYNA_TOUR_POLICY = "rayna/tour-policy";
    public static final String RAYNA_TOUR_BOOKING = "comman/booking";
    public static final String RAYNA_TOUR_BOOKING_CANCEL = "rayna/tour-booking-cancel";


    // Promo Code
    public static final String PROMO_CODE_APPLY = "venue/promo-code/apply";


    public static final String NEW_EXPLORE_BLOCK = "homeblock/explore-block/get-blocks";
    public static final String RAYNA_CUSTOM_TICKET_LIST = "rayna/custom-ticket/list-user";


    public static final String USER_SESSION_CHECK = "user/session-check";

    public static final String USER_REPORT_ADD = "user/report-add";

    public static final String USER_REPORT_REMOVE = "user/report-remove";

    public static final String USER_REPORT_LIST_USER = "user/report-list/user";

    public static final String USER_REPORT_DETAIL = "user/report-detail";

    public static final String MY_REVIEW_LIST = "review/my-review/list";

    public static final String REVIEW_DELETE = "review/delete";


    public static final String AD_LIST = "ad/list";

    public static final String HOMEBLOCK_FAVORITE_ADD_UPDATE = "homeblock/favorite/add-update";

    public static final String RAYNA_CHECK_REVIEW = "rayna/check-review";

    public static final String RAYNA_UPDATE_REVIEW_STATUS = "rayna/update/review-status";

    public static final String USER_NOTIFICATION_USER = "user/notification/user";

    public static final String SUBSCRIPTION_ORDER = "subscription/order/";

    public static final String NOTIFICATION_IN_APP_READ = "user/notification/in-app/read";

    public static final String NOTIFICATION_IN_APP_LIST_USER = "user/notification/in-app/list/user";


    // Whosin Ticket

    public static final String RAYNA_WHOSIN_AVAILABILITY = "rayna/whosin/availability";

    public static final String RAYNA_WHOSIN_BOOKING_RULES = "rayna/whosin/booking-rules";

    public static final String RAYNA_MORE_INFO = "rayna/more-info";

    public static final String RAYNA_WHOSIN_TOUR_BOOKING_CANCEL = "rayna/whosin/tour-booking-cancel";

    public static final String WHOSIN_ADD_ON_AVAILABILITY = "rayna/whosin/addon/availability";


    // Travel Desk

    public static final String TRAVEL_DESK_PICKUP_LIST = "traveldesk/pickup-list";

    public static final String TRAVEL_DESK_TOUR_AVAILABILITY = "traveldesk/tour-availability";

    public static final String TRAVEL_DESK_BOOKING_CANCEL= "comman/booking/cancel";

    public static final String TRAVELDESK_BOOKING_RULES = "traveldesk/tour-policy";


    // Cart management


    public static final String CART_VIEW = "subscription/cart/view";

    public static final String ADD_TO_CART = "subscription/cart/add";

    public static final String REMOVE_TO_CART = "subscription/cart/remove";

    public static final String CART_BOOKING = "subscription/cart/checkout";

    public static final String CART_REMOVE_OPTION = "subscription/cart/remove-option";

    public static final String CART_UPDATE = "subscription/cart/update";

    public static final String SUBSCRIPTION_CART_REMOVE_PROMO = "subscription/cart/remove-promo";

    public static final String SUBSCRIPTION_BOOKING_GOOGLE_WALLET = "subscription/booking/google/wallet";

    public static final String RAYNA_SEARCH_SUGGESTIONS = "rayna/search/suggestions";


    // whosin ticket

    public static final String WHOSIN_TICKET_SLOTS = "whosin-ticket/slots";

    public static final String WHOSIN_TICKET_BOOKING_RULES = "whosin-ticket/booking-rules";

    public static final String WHOSIN_TICKET_TOUR_BOOKING_CANCEL = "whosin-ticket/tour-booking-cancel";

    public static final String WHOSIN_TICKET_AVAILABILITY = "whosin-ticket/availability";


    // Octa

    public static final String OCTO_TOUR_AVILABILITY = "octo/tour-availability";

    public static final String OCTO_TOUR_POLICY = "octo/tour-policy";


    // Comman Language

    public static final String COMMAN_LANGUAGE_FILE = "comman/language-file";


    // Juniper Hotel Booking

    public static final String HOTEL_AVAILABILITY = "hotel/availability";

    public static final String HOTEL_BOOKING_RULES = "hotel/booking-rules";

    public static final String TICKET_SUGGESTIONS = "rayna/get-ticket-suggestions";



    // Bank Details

    public static final String GET_BANK_DETAILS = "user/tour-guide/get-bank-detail";

    public static final String UPDATE_BANK_DETAILS = "user/tour-guide/update-bank-detail";
}
