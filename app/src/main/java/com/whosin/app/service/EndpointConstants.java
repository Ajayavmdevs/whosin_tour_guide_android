package com.whosin.app.service;

public class EndpointConstants {

    public static final String USER_GET_TOKEN = "user/get-token";
    public static final String AUTH_GOOGLE_LOGIN_ENDPOINT = "user/login/google";
    public static final String AUTH_FACEBOOK_LOGIN_ENDPOINT = "user/login/facebook";
    public static final String AUTH_PHONE_LOGIN_ENDPOINT = "user/login";
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

    public static final String VENUE_DETAILS_ENDPOINT = "venue/detail";
    public static final String ADD_RATINGS_ENDPOINT = "rating/add";

    public static final String FOLLOW_UNFOLLOW_ENDPOINT = "follow/add";

    public static final String DEAL_DETAILS_ENDPOINT = "homeblock/deal-package/detail";

    public static final String CATEGORY_DETAILS_ENDPOINT = "venue/category/detail";

    public static final String CATEGORY_OFFER_LIST = "venue/offer/list";

    //subscription
    public static final String SUBSCRIPTION_DETAIL = "subscription/active";
    public static final String SUBSCRIPTION_VOUCHER_LIST = "subscription/voucher/list";
    public static final String STRIPE_PAYMENT_INTENT = "subscription/stripe/paymentIntent/create";
    public static final String SUBSCRIPTION_CUSTOM = "subscription/custom/get";
    public static final String SUBSCRIPTION_VOUCHER_GIFT_LIST = "subscription/voucher/gift/list";
    public static final String SEND_AS_GIFTS = "subscription/voucher/sendasgift";
    public static final String CONTACT_LIST = "user/contact-sync";

    public static final String BUCKET_LIST = "bucket/list";
    public static final String CREATE_BUCKET_LIST = "bucket/create";
    public static final String UPDATE_BUCKET_LIST = "bucket/update";
    public static final String ADD_BUCKET_LIST = "bucket/item/add";
    public static final String DELETE_BUCKET= "bucket/";

    public static final String BUCKET_DETAIL = "bucket/";

    public static final String UPDATE_BUCKET_ITEM = "bucket/item/addRemove";
    public static final String  ADD_BUCKET_GALLERY = "bucket/gallery/add";

    public static final String  DELETE_BUCKET_GALLERY = "bucket/gallery/delete";

    public static final String  BUCKET_SHARE_UPDATE = "bucket/update";
    public static final String  ACTIVITY_DETAIL = "activity/detail";

    public static final String  BANNER_LIST = "activity/banner/list";

    public static final String ACTIVITY_LIST = "activity/list";

    public static final String ACTIVITY_FETCH_DATE = "activity/dates";

    public static final String ADD_ACTIVITY_RATING = "rating/activity/add";

    public static final String ACTIVITY_FETCH_SLOT = "activity/slots";
    public static final String ALL_REVIEW_LIST = "review/list";
    public static final String RATING_SUMMARY = "review/summary";
    public static final String EVENT_DETAIL = "event/detail";

    public static final String EVENT_ORGANIZER_DETAIL = "event/org/detail";

    public static final String EVENT_INVITE_GUEST = "event/invite/guest";
    public static final String EVENT_INVITE_STATUS = "event/invite/status";

    public static final String EVENT_GUEST_LIST = "event/guest/list";

    public static final String SUBMIT_RATE = "review/addUpdate";


    public static final String FOLLOW_UNFOLLOW_USER = "user/follow/add";

    public static final String FOLLOWERS_LIST = "user/followers/list";

    public static final String FOLLOWING_LIST = "user/following/list";
    public static final String VENUE_FOLLOW  = "venue/follow/toggle";

    public static final String EVENT_FOLLOW  = "event/org/follow";
    public static final String CHAT_FRIEND_LIST  = "chat/friend/list";
    public static final String CHAT_MSG_LIST  = "chat/messages";

    public static final String CHAT_CREATE  = "chat/friend/create";
    public static final String EVENT_CHAT_LIST  = "event/chat/list";
    public static final String CHAT_UPLOAD_LIST  = "chat/upload";

    public static final String CHAT_UNRECEIVED_MSG  = "chat/messages/unreceived";
    public static final String BUCKET_LIST_DEAL  = "homeblock/deal/list/forBucket";
    public static final String BLOCK_USER_ADD  = "user/block/add";
    public static final String REPORT_USER_ADD  = "user/report/add";

    public static final String BUCKET_OWNER_CHANGE  = "bucket/change/owner";
    public static final String BUCKET_EXIT  = "bucket/exit";

    public static final String USER_SEARCH = "user/search";
    public static final String VENUE_OFFER_SERACH = "venue/offer/search";
    public static final String VENUE_SERACH = "venue/search";
    public static final String ACTIVITY_SEARCH = "activity/search";
    public static final String EVENT_SEARCH = "event/search";
    public static final String VENUE_RECOMMENDE = "venue/recommended";
    public static final String CHANGE_USER_EMAIL_PHONE = "user/user/link/email-phone";
    public static final String USER_OTP_UPDATE = "user/verify/otp/update";
    public static final String COMMAN_SEARCH = "comman/search";


    public static final String  USER_PROFILE = "user/profile/";
    public static final String  COMMAN_EXPLORE = "comman/explore";
    public static final String  CLAIM_SPECIAL_OFFER = "venue/special-offer/claim";
    public static final String  CLAIM_HISTORY = "venue/special-offer-claim/history";
    public static final String  WALLET_MY_ITEM = "subscription/order/list";
    public static final String  WALLET_GIFT = "subscription/gift/list";
    public static final String  USER_FEED_MY_ENDPOINT = "user/feed/my";
    public static final String  USER_FEED_MY_FRIEND = "user/feed/friend";
    public static final String  WALLET_HISTORY = "subscription/order/history";
    public static final String  WALLET_SEND_GIFT = "subscription/send-gift";
    public static final String  BRUNCH_LIST = "venue/offer/brunch-list";
    public static final String  BRUNCH_OFFER_LIST = "venue/offer/brunch-by-special-offer";
    public static final String  STORY_CREATE_BY_USER = "homeblock/story/create-by-user";
    public static final String  INVITE_FRIEND  = "outing/create";
    public static final String  MY_OUTING_LIST  = "outing/my-outing-list";
    public static final String  UPDATE_OUTING_INVITE_STATUS  = "outing/update/invite-status";
    public static final String  UPDATE_OUTING  = "outing/update";

    public static final String OUTING_DETAIL  = "outing/";


    public static final String  USER_NOTIFICATION_LIST  = "user/notification/list";
    public static final String  PACKAGE_REDEEM  = "subscription/package-redeem";
    public static final String  BUCKET_EVENTS  = "event/my-event-list";
    public static final String  HOME_DEAL_LIST  = "homeblock/deal-package/list-user";

    //    public static final String  USER_NOTIFICATION_LIST = "user/notification/list";
    public static final String  SYNC_CHAT_MSG_LIST = "chat/messages/unreceived";
    public static final String  MY_BUCKET_LIST = "bucket/my-bucket-list";
    public static final String  EVENT_HISTORY_LIST = "event/my-event-history";
    public static final String  EVENT_UPCOMING_LIST = "event/my-upcoming-event";
    public static final String  DELETE_CHAT = "chat/delete";
    public static final String  USER_RECOMMENDATION = "user/recommendation/add";
    public static final String LINK_CREATE = "link/create";
    public static final String CHANGE_OWNERSHIP = "outing/owner/change";

    public static final String VENUE_OFFER_DETAIL = "venue/offer/detail";
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
    public static final String DELETE_OUTING_INVITATION= "outing/delete-invite";
    public static final String OUTING_OWNER_DELETE = "outing/owner/delete";
    public static final String SUGGESTED_USERS = "user/suggested-users";
    public static final String SUGGESTED_VENUES = "venue/suggested-venues";
    public static final String USER_REMOVE_SUGGESTION = "user/remove-suggestion";
    public static final String USER_UPDATE_SETTINGS = "user/update-settings";
    public static final String USER_FOLLOW_REQUEST_LIST = "user/follow-request/list";
    public static final String USER_FOLLOW_REQUEST_ACTION = "user/follow-request/action";
    public static final String CONTACT_US_REPLY_MARK_AS_READ = "comman/contact-us/reply/mark-as-read";
    public static final String MEMBERSHIP_PURCHASE = "subscription/membership/purchase";
    public static final String MEMBERSHIP_PACKAGE_DETAIL = "subscription/membership/package-detail";
    public static final String SUBSCRIPTION_PROMO_CODE_VALIDATION = "subscription/promocode/validate";
    public static final String USER_AUTH_REQUEST = "user/auth/request";

    public static final String YACHT_DETAIL = "yacht/detail";
    public static final String YACHT_CLUB_DETAIL = "yacht/club/detail";
    public static final String YACHT_OFFER_DETAIL = "yacht/offer/detail";
    public static final String YACHT_OFFER_PACKAGE_AVAILABLE_SLOTS = "yacht/offer/package/available-slots";


    //promoters

    public static final String PROMOTER_REQUEST_CREATE = "promoter/request/create";
    public static final String PROMOTER_RING_REQUEST_CREATE = "promoter/ring/request/create";
    public static final String PROMOTER_RING_UPDATE = "promoter/ring/update";


    public static final String PROMOTER_GET_PROFILE = "promoter/get-profile";

    public static final String PROMOTER_CREATE_CIRCLE = "promoter/create/circle";
    public static final String PROMOTER_CIRCLE_DETAIL = "promoter/circle-detail";

    public static final String PROMOTER_VENUE_USER = "promoter/get-my-venues";
    public static final String PROMOTER_RING_MEMBER = "promoter/my-ring-members";
    public static final String PROMOTER_RING_REMOVE_MEMBER = "promoter/ring/remove-member";
    public static final String PROMOTER_UPDATE_CIRCLE = "promoter/update/circle";
    public static final String PROMOTER_DELETE_CIRCLE = "promoter/delete/circle";
    public static final String PROMOTER_CIRCLE_ADD_MEMBER = "promoter/circle/add-member";
    public static final String PROMOTER_CIRCLE_REMOVE_MEMBER = "promoter/circle/remove-member";
    public static final String PROMOTER_MEMBER_BAN = "promoter/member/ban";
    public static final String  PROMOTER_ADD_RING = "promoter/add-to-ring";
    public static final String  PROMOTER_VENUE_REMOVE = "promoter/venue/remove";

    public static final String PROMOTER_EVENT_DETAIL = "promoter/event/detail";
    public static final String PROMOTER_EVENT_DETAIL_USER = "promoter/event/detail-user";
    public static final String PROMOTER_EVENT_LIST_USER = "promoter/event/list-user";
    public static final String PROMOTER_EVENT_HIDE_SHOW = "promoter/event/hide-show";

    public static final String PROMOTER_INVITATION_CREATE = "promoter/event/create";
    public static final String PROMOTER_EVENT_UPDATE = "promoter/event/update";


    public static final String PROMOTER_MY_EVENT_LIST = "promoter/my-event-list";
    public static final String PROMOTER_MY_EVENT_LIST_NEW = "promoter/my-event-list-new";
    public static final String PROMOTER_MY_EVENT_CANCEL = "promoter/event/cancel";
    public static final String PROMOTER_GET_COMPLIMENTARY_PROFILE = "promoter/get-complimentary-profile";
    public static final String PROMOTER_CHAT_CONTACT_LIST = "chat/promoter/contact/list";
    public static final String COMPLIMENTARTY_CHAT_CONTACT_LIST = "chat/complimentary/contact/list";
    public static final String EVENT_CONFIRMED_LIST = "promoter/event/list-user/confirmed";

    public static final String PROMOTER_RING_UPDATE_MEMBER_STATUS = "promoter/ring/update-member-status";

    public static final String PROMOTER_USER_NOTIFICATION = "promoter/user-notification";
    public static final String PROMOTER_RING_UPDATE_STATUS = "promoter/ring/update-prmoter-status";
    public static final String PROMOTER_EVENT_NOTIFICATION = "promoter/event-notification";
    public static final String GET_PROFILE = "promoter/get-profile/";
    public static final String PROMOTER_TOGGLE_WISHLIST = "promoter/toggle-wishlist";
    public static final String PROMOTER_UPDATE_INVITE_STATUS = "promoter/update/invite-status";
    public static final String CM_PROMOTER_UPDATE = "promoter/update";
    public static final String CM_EVENT_NOTIFICATION = "promoter/complementary/event-notification";
    public static final String CM_USER_NOTIFICATION = "promoter/complementary/user-notification";
    public static final String REPLAY_ADD_UPDATE_REVIEW = "review/reply/addUpdate";
    public static final String DELETE_REVIEW = "review/reply/delete";
    public static final String PROMOTER_INVITED_UPDATE_STATUS = "promoter/update/invite-status-promoter";
    public static final String PROMOTER_PLUS_ONE_INVITED_STATUS = "promoter/event/plus-one/invite-status-promoter";
    //    public static final String PROMOTER_EVENT_INVITE_LIST = "promoter/event/invite/list";
    public static final String PROMOTER_EVENT_INVITE_LIST = "promoter/event/invite/list-new";

    public static final String  PROMOTER_JOIN_MY_Ring = "promoter/join-my-ring";
    public static final String  PROMOTER_LEAVE_RING_COMPLIMENTARY_USER = "promoter/leave-ring/complimentary-user";
    public static final String  PROMOTER_EVENT_HISTORY = "promoter/my-event-history";
    public static final String  PROMOTER_EVENT_DELETE = "promoter/event/delete";
    public static final String  PROMOTER_EVENT_COMPLETE = "promoter/event/complete";
    public static final String  PROMOTER_USER_IN_EVENT = "promoter/user/in/events";

    public static final String  PROMOTER_SUBADMIN_RING_REQUEST = "promoter/ring/request/list-by-promoterId";
    public static final String  PROMOTER_RING_REQUEST_VERIFY = "promoter/ring/request/verify";

    public static final String VENUE_ALL_FILTERS = "venue/all-filters";

    public static final String PROMOTER_EVENT_USER_HISTORY="promoter/event/history-user";
    public static final String PROMOTER_EVENT_INVITE_USER="promoter/event/invited-users";
    public static final String PROMOTER_EVENT_CLOSE_SPORT="promoter/event/close-spot";
    public static final String PROMOTER_EVENT_GET_CUSTOM_CATEGORY="promoter/event/get-custom-category";
    public static final String PROMOTER_ADD_MEMBER_TO_CIRCLES = "promoter/add-member-to-circles";
    public static final String PROMOTER_CIRCLES_BY_USER_ID = "promoter/circles/by-userId";

    // Plus One

    public static final String PROMOTER_PLUS_ONE_GROUP_LIST_USER = "promoter/plus-one/group/list-user";

    public static final String PROMOTER_PLUS_ONE_GROUP_LEAVE = "promoter/plus-one/group/leave";

    public static final String PROMOTER_EVENT_PLUS_ONE_INVITE="promoter/event/plus-one/invite";

    public static final String PROMOTER_EVENT_PLUS_ONE_LIST="promoter/event/plus-one/list";

    public static final String PROMOTER_PLUS_ONE_INVITE_USER = "promoter/plus-one/invite/user";

    public static final String PROMOTER_PLUS_ONE_INVITE_USER_UPDATE_STATUS="promoter/plus-one/invite/user/update-status";

    public static final String PROMOTER_PLUS_ONE_INVITE_USER_REMOVE="promoter/plus-one/invite/user/remove";

    public static final String PROMOTER_PLUS_ONE_MY_GROUP ="promoter/plus-one/my-group";

    public static final String PROMOTER_PLUS_ONE_EVENT_DETAIL = "promoter/event/detail/plus-one";

    public static final String PROMOTER_EVENT_PLUS_ONE_INVITE_STATUS = "promoter/event/plus-one/invite-status";
    public static final String PROMOTER_VENUE_SET_FREQUENCY_CM_VISIT = "promoter/venue/set-frequency-for-cm-visit";

    public static final String PROMOTER_SUB_ADMIN_LIST = "user/promoter/sub-admin/list";
    public static final String VENUE_GET_MEDIA_URLS = "venue/get-venue-media-urls";
    public static final String USER_SEARCH_ALL = "user/search/all";

    public static final String PROMOTER_PENALTY_LIST = "promoter/penalty/list";
    public static final String PROMOTER_PENALTY_REMOVE = "promoter/penalty/remove";
    public static final String PROMOTER_UPDATE_SUBADMIN_STATUS = "promoter/update/subadmin/status";

    public static final String PROMOTER_PAYMENT_CREATE = "promoter/payment/create";
    public static final String PROMOTER_PAID_PASS_LIST = "promoter/paid-pass/list";
    public static final String PROMOTER_PAID_PASS_BY_EVENTID = "promoter/paid-pass-by-eventId";

    //Rayna

    public static final String RAYNA_CUSTOM_USER_DETAIL = "rayna/custom/detail-user";
    public static final String RAYNA_SEARCH = "rayna/search";
    public static final String RAYNA_TOUR_OPTIONS = "rayna/tour-options";
    public static final String RAYNA_TOUR_OPTIONS_DETAIL_BY_TOUR_ID = "rayna/tour-option-detail-by-tour-id";
    public static final String RAYNA_TOUR_TIMESLOT = "rayna/tour-timeslot";
    public static final String RAYNA_TOUR_POLICY = "rayna/tour-policy";
    public static final String RAYNA_TOUR_BOOKING = "comman/booking";
    public static final String RAYNA_TOUR_BOOKING_CANCEL = "rayna/tour-booking-cancel";


    // Promo Code
    public static final String PROMO_CODE_APPLY = "venue/promo-code/apply";


    public static final String NEW_EXPLORE_BLOCK = "homeblock/explore-block/get-blocks";
    public static final String RAYNA_CUSTOM_TICKET_LIST = "rayna/custom-ticket/list-user";


    public static final String USER_SESSION_CHECK = "user/session-check";
    public static final String CM_PROFILE_TICKETS_BLOCK = "homeblock/cm-profile-tickets";

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

    public static final String RAYNA_WHOSIN_BOOKING = "rayna/whosin/booking";

    public static final String RAYNA_MORE_INFO = "rayna/more-info";

    public static final String RAYNA_WHOSIN_TOUR_BOOKING_CANCEL = "rayna/whosin/tour-booking-cancel";

    public static final String WHOSIN_ADD_ON_AVAILABILITY = "rayna/whosin/addon/availability";


    // Juniper Ticket

    public static final String JUNIPER_AVAILABILITY = "juniper/availability";

    public static final String JUNIPER_CHECK_AVAILABILITY = "juniper/check-availability";

    public static final String JUNIPER_BOOKING_RULES = "juniper/booking-rules";


    // Promotional Banner

    public static final String  HOMEBLOCK_BANNER_LIST = "homeblock/promotional-banner/list";


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
}
