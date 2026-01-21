package com.whosin.app.comman;

import android.content.Context;
import android.content.res.Resources;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.stripe.android.paymentsheet.PaymentSheet;

import java.util.HashMap;
import java.util.Map;

public class AppConstants {

    // region Language
    // --------------------------------------

    public static final PaymentSheet.GooglePayConfiguration.Environment GPAY_ENV = PaymentSheet.GooglePayConfiguration.Environment.Production;
    public static final String GPAY_REGION = "AE";

    public static final String LANGUAGE_CODE_EN = "en";
    public static final String LANGUAGE_CODE_VI = "vi";

    public static final int CARD_PAYMENT = 1;
    public static final int PAY_WITH_LINK = 2;
    public static final int TABBY_PAYMENT = 3;
    public static final int GOOGLE_PAY = 4;
    public static final int SAMSUNG_PAY = 5;
    public static final int NGINUES_PAY = 6;

    // --------------------------------------
    // region Common
    // --------------------------------------

    public static final String DATABASE_NAME = "WhosIn.db";
    public static final int DATABASE_VERSION = 6;

    public static final String EMPTY_STRING = "";
    public static final String PREFERENCE = "PREFERENCE";

    public static final String ADULTS = "adult";
    public static final String CHILD = "child";
    public static final String INFANT = "infant";
    public static final String DATE = "date";

    public static final String ADTYPE = "home-ad";


    // endregion
    // --------------------------------------
    // region Preferences
    // --------------------------------------

    public static final String PREF_DEVICEUUID = "deviceuuid";


    // endregion
    // --------------------------------------
    // region Enum
    // --------------------------------------
    public enum ContextType {
        MY_RING,
        CIRCLE_DETAIL,
        CREATE_EVENT,
        ADD_USER,
        SUB_ADMIN
    }

    public enum InviteType {
        NONE,
        OWNER,
        ADMIN,
        INVITY
    }

    public enum HomeBlockType {
        NONE( 0 ),
        VENUE_LARGE( 1 ),
        VENUE_SMALL( 2 ),
        OFFER_SMALL( 3 ),
        OFFER_LARGE( 4 ),
        VIDEO( 5 ),
        CUSTOM_VENUE( 6 ),
        CUSTOM_OFFER( 7 ),
        CUSTOM_COMPOMENTS( 8 ),
        DEALS( 9 ),
        STORIES( 10 ),
        CATEGORIES( 11 ),

        ACTIVITIES( 12 ),

        EVENTS( 13 ),
        MY_OUTING( 14 ),
        SUGGESTED_USERS( 15 ),
        VENUE_SUGGESTION( 16 ),
        COMPLETE_PROFILE( 17 ),
        MEMBERSHIP_PACKAGE ( 18),
        YACTCH ( 19),
        YACTCH_OFFERS (20),
        APPLY_PROMOTER(21),
        APPLY_RING(22),
        PROMOTER_EVENT(23),
        TICKET(24),
        TICKET_CATEGORY(25),
        CITY(26),
        BIG_CATEGORY(27),
        SMALL_CATEGORY(28),
        BANNER(29),
        CUSTOM_COMPONENT(30),
        TICKET_FAVORITE (31),
        HOME_AD(32),
        JUNIPER_HOTEL(33),
        CONTACT_US(34);


        private final int value;
        private static final Map map = new HashMap<>();

        HomeBlockType(int value) {
            this.value = value;
        }

        static {
            for (HomeBlockType envType : HomeBlockType.values()) {
                map.put( envType.value, envType );
            }
        }

        @Nullable
        public static HomeBlockType valueOf(int envType) {
            return (HomeBlockType) map.get( envType );
        }

        public int getValue() {
            return value;
        }
    }

    public enum ExploreBlockType {
        NONE( 0 ),
        TICKET(1),
        CITY(2),
        CATEGORY(3),
        BIG_CATEGORY(4),
        BANNER(5),
        CUSTOM_COMPONENT(6),
        SMALL_CATEGORY(7),
        HOME_AD(8),
        JUNIPER_HOTEL(9),
        CONTACT_US(10);

        private final int value;
        private static final Map map = new HashMap<>();

        ExploreBlockType(int value) {
            this.value = value;
        }

        static {
            for (ExploreBlockType envType : ExploreBlockType.values()) {
                map.put( envType.value, envType );
            }
        }

        @Nullable
        public static ExploreBlockType valueOf(int envType) {
            return (ExploreBlockType) map.get( envType );
        }

        public int getValue() {
            return value;
        }
    }





    public enum CartBlockType {
        NONE( 0 ),
        OFFER( 1 ),
        DEAL( 2 ),
        ACTIVITY( 3 ),
        Event( 4 );
        private final int value;
        private static final Map map = new HashMap<>();

        CartBlockType(int value) {
            this.value = value;
        }

        static {
            for (CartBlockType envType : CartBlockType.values()) {
                map.put( envType.value, envType );
            }
        }

        @Nullable
        public static CartBlockType valueOf(int envType) {
            return (CartBlockType) map.get( envType );
        }

        public int getValue() {
            return value;
        }
    }

    public enum CLAIMTYPE {
        NONE( 0 ),
        CLAIM_TOTAL( 1 ),
        CLAIM_BRUNCH( 2 );

        private final int value;
        private static final Map map = new HashMap<>();

        CLAIMTYPE(int value) {
            this.value = value;
        }

        static {
            for (CLAIMTYPE envType : CLAIMTYPE.values()) {
                map.put( envType.value, envType );
            }
        }

        @Nullable
        public static CLAIMTYPE valueOf(int envType) {
            return (CLAIMTYPE) map.get( envType );
        }

        public int getValue() {
            return value;
        }
    }

    public enum OrderListType {
        NONE( 0 ),
        OFFER( 1 ),
        ACTIVITY( 2 ),
        DEAL( 3 ),

        EVENT( 4 ),
        TICKET( 5),
        WHOSIN_TICKET( 6),
        TRAVEL_DESK(7),
        BIG_BUS(8),
        HERO_BALLOON(9),
        JUNIPER_HOTEL(10);


        private final int value;
        private static final Map map = new HashMap<>();

        OrderListType(int value) {
            this.value = value;
        }

        static {
            for (OrderListType envType : OrderListType.values()) {
                map.put( envType.value, envType );
            }
        }

        @Nullable
        public static OrderListType valueOf(int envType) {
            return (OrderListType) map.get( envType );
        }

        public int getValue() {
            return value;
        }
    }

    public enum SearchHomeType {

        NONE( 0 ),
        CATEGORIES( 1 ),
        DEALS( 2 ),
        VENUE( 3 );

        private final int value;
        private static final Map map = new HashMap<>();

        SearchHomeType(int value) {
            this.value = value;
        }

        static {
            for (SearchHomeType envType : SearchHomeType.values()) {
                map.put( envType.value, envType );
            }
        }

        @Nullable
        public static SearchHomeType valueOf(int envType) {
            return (SearchHomeType) map.get( envType );
        }

        public int getValue() {
            return value;
        }

    }


    public enum SearchResultType {

        NONE( 0 ),
        VENUE( 1 ),
        OFFER( 2 ),
        USER( 3 ),
        EVENT( 4 ),
        ACTIVITY( 5 ),
        TICKET( 6 ),
        HOME_AD( 7);

        private final int value;
        private static final Map map = new HashMap<>();

        SearchResultType(int value) {
            this.value = value;
        }

        static {
            for (SearchResultType envType : SearchResultType.values()) {
                map.put( envType.value, envType );
            }
        }

        @Nullable
        public static SearchResultType valueOf(int envType) {
            return (SearchResultType) map.get( envType );
        }

        public int getValue() {
            return value;
        }

    }

    public enum ExploreResultType {

        NONE( 0 ),
        OFFER( 1 ),
        EVENT( 2 ),
        ACTIVITY( 3 ),
        SUGGESTED_VENUE( 4 ),
        SUGGESTED_USER( 5 );

        private final int value;
        private static final Map map = new HashMap<>();

        ExploreResultType(int value) {
            this.value = value;
        }

        static {
            for (ExploreResultType envType : ExploreResultType.values()) {
                map.put( envType.value, envType );
            }
        }

        @Nullable
        public static ExploreResultType valueOf(int envType) {
            return (ExploreResultType) map.get( envType );
        }

        public int getValue() {
            return value;
        }

    }

    public enum UserFeedType {

        NONE(0),
        FRIENDS_UPDATE(1),
        VENUE_UPDATE(2),
        EVENY_UPDATE(3),
        ACTIVITY_RECOMMENDATION(4),
        VENUE_RECOMMENDATION(5),
        OFFER_RECOMMENDATION(6);

        private final int value;
        private static final Map map = new HashMap<>();

        UserFeedType(int value) {
            this.value = value;
        }

        static {
            for (UserFeedType envType : UserFeedType.values()) {
                map.put(envType.value, envType);
            }
        }

        @Nullable
        public static UserFeedType valueOf(int envType) {
            return (UserFeedType) map.get(envType);
        }

        public int getValue() {
            return value;
        }

    }

    public enum HolderType {
        None( -1 ),
        ImageSlider( 0 ),
        Category( 1 ),
        BusinessList( 2 ),
        OfferSlider( 3 ),
        SmallBusiness( 4 ),
        NearBy( 5 ),
        VideoPlayer( 6 ),
        Offers( 7 ),
        CategoryOption( 8 );


        private final int value;
        private static final Map map = new HashMap<>();

        HolderType(int value) {
            this.value = value;
        }

        static {
            for (HolderType envType : HolderType.values()) {
                map.put( envType.value, envType );
            }
        }

        @Nullable
        public static HolderType valueOf(int envType) {
            return (HolderType) map.get( envType );
        }

        public int getValue() {
            return value;
        }
    }

    public enum TabOption {

        Home( 0 ),

        Chat( 1 ),

        Profile(2),

        Explore( 3 ),

        Wallet( 4 );

        public final int id;
        private static final Map map = new HashMap<>();

        TabOption(final int id) {
            this.id = id;
        }

        static {
            for (TabOption envType : TabOption.values()) {
                map.put( envType.id, envType );
            }
        }

        public int getId() {
            return id;
        }

        @Nullable
        public static TabOption valueOf(int id) {
            return (TabOption) map.get( id );
        }
    }

    public enum SubAdminTabOption {

        Home( 0 ),

        Chat(1),

        Notification( 2);


        public final int id;
        private static final Map map = new HashMap<>();

        SubAdminTabOption(final int id) {
            this.id = id;
        }

        static {
            for (SubAdminTabOption envType : SubAdminTabOption.values()) {
                map.put( envType.id, envType );
            }
        }

        public int getId() {
            return id;
        }

        @Nullable
        public static SubAdminTabOption valueOf(int id) {
            return (SubAdminTabOption) map.get( id );
        }
    }


    public enum PromoterTabOption {

        Profile( 0 ),
        Event( 1 ),
        Event_History(2),
        Chat(3),
        Notification(4);


        public final int id;
        private static final Map map = new HashMap<>();

        PromoterTabOption(final int id) {
            this.id = id;
        }

        static {
            for (PromoterTabOption envType : PromoterTabOption.values()) {
                map.put( envType.id, envType );
            }
        }

        public int getId() {
            return id;
        }

        @Nullable
        public static PromoterTabOption valueOf(int id) {
            return (PromoterTabOption) map.get( id );
        }
    }

    public enum CmProfileTabOption {

        Profile(3),
        Event(0),
        Chat(2),
        Notification(1);


        public final int id;
        private static final Map map = new HashMap<>();

        CmProfileTabOption(final int id) {
            this.id = id;
        }

        static {
            for (CmProfileTabOption envType : CmProfileTabOption.values()) {
                map.put(envType.id, envType);
            }
        }

        public int getId() {
            return id;
        }

        @Nullable
        public static CmProfileTabOption valueOf(int id) {
            return (CmProfileTabOption) map.get(id);
        }
    }


    public enum MsgType {
        NONE( "0" ),
        TEXT( "text" ),
        IMAGE( "image" ),
        AUDIO( "audio" ),
        STORY( "story" ),
        USER( "user" ),
        OFFER( "offer" ),
        PROMOTEEvent( "promoterEvent" ),
        VENUE( "venue" ),
        TICKET( "ticket" );

        public final String id;
        private static final Map map = new HashMap<>();

        MsgType(final String id) {
            this.id = id;
        }

        static {
            for (MsgType envType : MsgType.values()) {
                map.put( envType.id, envType );
            }
        }

        public String getId() {

            return id;
        }

        @Nullable
        public static MsgType valueOf1(String id) {
            return (MsgType) map.get( id );
        }

    }


    public enum DurationType {
        PREPARE, ANSWER
    }

    // endregion
    // --------------------------------------
    // region DateFormat
    // --------------------------------------

    public static final String DATEFORMAT_ = "yyyy-MM-dd 00:00:00";
    public static final String DATEFORMAT_LONG_TIME = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String DATEFORMAT_SHORT = "yyyy-MM-dd";
    public static final String DATEFORMAT_TIME = "YYYY-MM-DDTHH:mm:ss";
    public static final String DATEFORMAT_TIMESHORT = "yyyy-MM-dd HH:mm";
    public static final String DATEFORMAT_TIMELONG = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String DATEFORMAT_US_TIME24 = "MM/dd/yyy HH:mm";
    public static final String DATEFORMAT_US_TIME24SHORT = "M/dd/yy HH:mm";
    public static final String DATEFORMAT_US_TIMEAMPM = "MM/dd/yyy hh:mm a";
    public static final String DATEFORMAT_US_TIME_MINUTE = "yyyy/MM/dd hh:mm:ss";
    public static final String DATEFORMAT_GMT_LONG = "EEE, dd MMM HH:mm a";
    public static final String DATEFORMAT_UTC = "yyyy-MM-dd HH:mm:ss ZZZ";
    public static final String DATEFORMAT_ISO8601SHORT = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String DATEFORMAT_ISO8601 = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    public static final String DATEFORMAT_ISO8601UTC = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public static final String DATEFORMAT_24HOUR = "HH:mm";
    public static final String DATEFORMAT_12HOUR = "hh:mm a";
    public static final String DATEFORMAT_US = "MM/dd/yyyy";
    public static final String DATEFORMAT_USSHORT = "M/dd/yy";
    public static final String DATEFORMAT_ID = "MMddyyyy";
    public static final String DATEFORMAT_CCTV_PLAYBACK = "yyyyMMdd'T'HHmmss'Z'";
    public static final String DATEFORMAT_TIMESHORT_PAIN = "yyyyMMddHHmmss";
    public static final String DATEFORMAT_DATEONLY_PAIN = "yyyyMMdd";

    public static final String DATEFORMAT_ONLY_HOUR = "MM/dd/yyy HH";
    public static final String DATEFORMAT_DD_MM_YYYY = "dd/MM/yyyy";
    public static final String DATEFORMAT_DD_MM_YYYY_HH_MM_SS_VN = "dd/MM/yyyy HH:mm:ss";
    public static final String DATEFORMAT_DD_MM_YYYY_HH_MM_SS_US = "MM/dd/yyyy HH:mm:ss";

    public static final String DATEFORMAT_DD_MM_YYYY_HH_MM_VN = "dd/MM/yyyy HH:mm";
    public static final String DATEFORMAT_DD_MM_YYYY_HH_MM_US = "MM/dd/yyyy HH:mm";

    public static final String DATEFORMAT_HH_MM_SS = "HH:mm:ss";
    public static final String DATEFORMAT_MONTHDAYYEARSHORT_NOT_CROSS = "MMM dd yyyy HH:mm";
    public static final String DATEFORMAT_DAYMONTHYEARSHORT_NOT_CROSS = "dd MMM, HH:mm";
    public static final String DATEFORMAT_DD_MM_YYYY_HH_MM_SS_A = "dd/MM/yyyy hh:mm:ss a";
    public static final String DATEFORMAT_MM_DD_YYYY_HH_MM_SS_A = "MM/dd/yyyy hh:mm:ss a";
    public static final String DATEFORMAT_INTERVIEW_SUMMERY = "d MMM yyyy hh:mm a";
    public static final String DATEFORMT_MM_DATE = "E, dd MMM yyyy";
    public static final String DATEFORMT_DD_MM_DATE = "EEEE dd - MMM";
    public static final String DATEFORMT_EEE_d_MMM_yyyy = "EEE, d MMM yyyy";
    public static final String DATEFORMT_DATE_AND_12TIMEFORMAT = "dd MMM, hh:mm a";
    public static final String DATEFORMT_DD_MM_YYYY = "dd MMM yyyy";
    // endregion
    // --------------------------------------

    public static int getNavigationbarHeight(@NonNull Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier( "navigation_bar_height", "dimen", "android" );
        if (resourceId > 0) {
            return resources.getDimensionPixelSize( resourceId );
        }
        return 0;
    }


}
