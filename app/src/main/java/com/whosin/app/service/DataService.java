package com.whosin.app.service;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.whosin.app.R;
import com.whosin.app.comman.AppConstants;
import com.whosin.app.comman.AppExecutors;
import com.whosin.app.comman.Preferences;
import com.whosin.app.comman.Utils;
import com.whosin.app.service.manager.LocationManager;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.manager.UrlManager;
import com.whosin.app.service.models.ActivityDetailModel;
import com.whosin.app.service.models.ActivityFetchModel;
import com.whosin.app.service.models.AdListModel;
import com.whosin.app.service.models.AppSettingModel;
import com.whosin.app.service.models.BannerModel;
import com.whosin.app.service.models.BigBusModels.OctaTourAvailabilityModel;
import com.whosin.app.service.models.BrunchListModel;
import com.whosin.app.service.models.BucketChatMainProfileModel;
import com.whosin.app.service.models.BucketEventListModel;
import com.whosin.app.service.models.BucketListModel;
import com.whosin.app.service.models.CategoriesModel;
import com.whosin.app.service.models.ChatMessageModel;
import com.whosin.app.service.models.ChatResponseModel;
import com.whosin.app.service.models.ChatModel;
import com.whosin.app.service.models.ClaimHistoryModel;
import com.whosin.app.service.models.ClaimOfferModel;
import com.whosin.app.service.models.CommanMsgModel;
import com.whosin.app.service.models.CommanSearchModel;
import com.whosin.app.service.models.CommonModel;
import com.whosin.app.service.models.ComplimentaryProfileModel;
import com.whosin.app.service.models.ContactChatRepliesModel;
import com.whosin.app.service.models.ContactListModel;
import com.whosin.app.service.models.ContainerBaseModel;
import com.whosin.app.service.models.ContainerListModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.CreateBucketListModel;
import com.whosin.app.service.models.CurrentUserRatingModel;
import com.whosin.app.service.models.EventChatListModel;
import com.whosin.app.service.models.EventDetailModel;
import com.whosin.app.service.models.EventGuestListModel;
import com.whosin.app.service.models.EventInOutPenaltyModel;
import com.whosin.app.service.models.EventOrgDateModel;
import com.whosin.app.service.models.ExclusiveDealModel;
import com.whosin.app.service.models.ExploreModel;
import com.whosin.app.service.models.FollowUnfollowModel;
import com.whosin.app.service.models.GetPrefrenceModel;
import com.whosin.app.service.models.HomeBlockModel;
import com.whosin.app.service.models.HomeObjectModel;
import com.whosin.app.service.models.HomeTicketsModel;
import com.whosin.app.service.models.ImageListUploadModel;
import com.whosin.app.service.models.ImageUploadModel;
import com.whosin.app.service.models.InAppListUserModel;
import com.whosin.app.service.models.InAppNotificationModel;
import com.whosin.app.service.models.InviteFriendModel;
import com.whosin.app.service.models.InvitedUserModel;
import com.whosin.app.service.models.JuniperHotelModels.JPHotelTourAvailabilityModel;
import com.whosin.app.service.models.JuniperHotelModels.JpHotelBookingRuleModel;
import com.whosin.app.service.models.LoginRequestModel;
import com.whosin.app.service.models.MainNotificationModel;
import com.whosin.app.service.models.MemberShipModel;
import com.whosin.app.service.models.ModelProtocol;
import com.whosin.app.service.models.MyUserFeedModel;
import com.whosin.app.service.models.MyWalletModel;
import com.whosin.app.service.models.NotificationModel;
import com.whosin.app.service.models.OffersModel;
import com.whosin.app.service.models.PaymentCredentialModel;
import com.whosin.app.service.models.PenaltyListModel;
import com.whosin.app.service.models.PromoCodeModel;
import com.whosin.app.service.models.PromoterAddRingModel;
import com.whosin.app.service.models.PromoterChatModel;
import com.whosin.app.service.models.PromoterCirclesModel;
import com.whosin.app.service.models.PromoterEventInviteModel;
import com.whosin.app.service.models.PromoterEventModel;
import com.whosin.app.service.models.PromoterInvitedUserModel;
import com.whosin.app.service.models.PromoterNewEventListModel;
import com.whosin.app.service.models.PromoterPaidPassModel;
import com.whosin.app.service.models.PromoterPenaltyModel;
import com.whosin.app.service.models.PromoterProfileModel;
import com.whosin.app.service.models.PromoterVenueModel;
import com.whosin.app.service.models.PromotionalBannerModels.PromotionalMainModel;
import com.whosin.app.service.models.RatingListModel;
import com.whosin.app.service.models.ReportUseListModel;
import com.whosin.app.service.models.ReviewModel;
import com.whosin.app.service.models.ReviewReplayModel;
import com.whosin.app.service.models.SearchEventModel;
import com.whosin.app.service.models.SpecialOfferModel;
import com.whosin.app.service.models.StoryObjectModel;
import com.whosin.app.service.models.SubscriptionModel;
import com.whosin.app.service.models.SubscriptionVoucherListModel;
import com.whosin.app.service.models.TotalRatingModel;
import com.whosin.app.service.models.TravelDeskModels.TravelDeskCancellationPolicyModel;
import com.whosin.app.service.models.TravelDeskModels.TravelDeskPickUpListModel;
import com.whosin.app.service.models.TravelDeskModels.TravelDeskTourAvailabilityModel;
import com.whosin.app.service.models.UpdateStatusModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.models.UserTokenModel;
import com.whosin.app.service.models.VenueFiltersModel;
import com.whosin.app.service.models.VenueObjectModel;
import com.whosin.app.service.models.VenuePromoCodeModel;
import com.whosin.app.service.models.VenueRecommendedModel;
import com.whosin.app.service.models.VoucherModel;
import com.whosin.app.service.models.YachtAvailableSlotsModel;
import com.whosin.app.service.models.YachtClubModel;
import com.whosin.app.service.models.YachtDetailModel;
import com.whosin.app.service.models.YachtsOfferModel;
import com.whosin.app.service.models.juniper.JuniperTourDataModel;
import com.whosin.app.service.models.myCartModels.MyCartMainModel;
import com.whosin.app.service.models.newExploreModels.ExploreObjectModel;
import com.whosin.app.service.models.rayna.RaynaCheckReviewModel;
import com.whosin.app.service.models.rayna.RaynaTicketBookingModel;
import com.whosin.app.service.models.rayna.RaynaTicketDetailModel;
import com.whosin.app.service.models.rayna.RaynaTimeSlotModel;
import com.whosin.app.service.models.rayna.TourOptionDetailModel;
import com.whosin.app.service.models.rayna.TourOptionsModel;
import com.whosin.app.service.models.whosinTicketModel.RaynaWhosinBookingRulesModel;
import com.whosin.app.service.models.whosinTicketModel.RaynaWhosinInfoModel;
import com.whosin.app.service.models.whosinTicketModel.RaynaWhosinModel;
import com.whosin.app.service.models.whosinTicketModel.RaynaWhosinMoreInfoModel;
import com.whosin.app.service.models.whosinTicketModel.WhosinAvailabilityModel;
import com.whosin.app.service.rest.HttpCommon;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.service.rest.RestClient;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Url;


public class DataService {

    private static volatile DataService _instance = null;
    private Context _context;

    public static DataService shared(Context ctx) {
        if (_instance == null) {
            synchronized (DataService.class) {
                _instance = new DataService();
            }
        }
        if (ctx != null) {
            _instance._context = ctx;
        }
        return _instance;
    }

    public DataService() {
        //empty
    }

    // region IRequestService
    private interface IRequestService {

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<UserTokenModel>> requestAuth(@Url String url, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<UserDetailModel>> requestLoginWithPhone(@Url String url, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<UserTokenModel>> verifyOtp(@Url String url, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<UserTokenModel>> verifyPassword(@Url String url, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<UserDetailModel>> requestTwoAuthEmail(@Url String url, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<UserDetailModel>> requestLogout(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<UserDetailModel>> requestApprovedLogin(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<UserDetailModel>> requestUpdateFcmToken(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @Multipart
        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<ImageUploadModel>> requestUploadImage(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Part MultipartBody.Part part);

        @Multipart
        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<ImageListUploadModel>> requestUploadImageList(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Part List<MultipartBody.Part> images);


        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<UserDetailModel>> requestUpdateProfile(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<UserDetailModel>> requestUserByIds(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @GET()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<AppSettingModel>> requestAppSetting(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token);

        @GET()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<VenueFiltersModel>> requestVenueFilter(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token);


        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<GetPrefrenceModel>> requestSelectedPreference(@Url String id, @Body JsonObject bodyRequest, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token);

        @GET
        @Headers({"Accept: application/json"})
        Call<ContainerModel<GetPrefrenceModel>> requestGetSelectedPreference(@Url String id, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<HomeObjectModel>> requestHomeBlockList(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<OffersModel>> requestOfferDetails(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<VenueObjectModel>> requestVenueDetails(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<FollowUnfollowModel>> requestFollowUnFollow(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<ExclusiveDealModel>> requestDealDetails(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<CategoriesModel>> requestCategory(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<VoucherModel>> requestOfferList(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

//        @POST()
//        @Headers({"Accept: application/json"})
//        Call<ContainerListModel<CategoryOfferListModel>> requestCategoryOfferList(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<MemberShipModel>> requestSubscription(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token);

        @POST
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<SubscriptionVoucherListModel>> requestSubscriptionVoucherList(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<PaymentCredentialModel>> requestStripePaymentIntent(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<PromoterPaidPassModel>> requestPromoterPaidPassList(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<PromoterPaidPassModel>> requestPromoterPaidPassByEventId(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<ContactListModel>> requestContactList(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);


        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<CreateBucketListModel>> requestCreateBucketList(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token,
                                                                            @Body JsonObject bodyRequest);

        @GET()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<BucketListModel>> requestBucketList(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token);

        @GET()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<BucketChatMainProfileModel>> requestChatProfileBucketList(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token);


        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<BucketListModel>> requestUpdateBucketList(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<CreateBucketListModel>> requestAddBucketList(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @DELETE()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<CreateBucketListModel>> requestRemoveBucket(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token);

        @GET()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<CreateBucketListModel>> requestBucketDetail(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<CreateBucketListModel>> requestUpdateBucket(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<CreateBucketListModel>> requestAddBucketGallery(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<CreateBucketListModel>> requestDeleteBucketGallery(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<CreateBucketListModel>> requestBucketUpadte(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @GET()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<ActivityDetailModel>> requestActivityDetail(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token);

        @GET()
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<ActivityDetailModel>> requestActivityLIST(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token);

        @GET()
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<BannerModel>> requestBannerList(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token);

        @GET()
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<ActivityFetchModel>> requestActivityFetch(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token);

        @GET()
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<ActivityFetchModel>> requestActivityFetchTimeSlot(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<ReviewModel>> requestRatingList(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<TotalRatingModel>> requestRatingSummary(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<EventDetailModel>> requestEventDetail(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<EventDetailModel>> requestEventInviteGuest(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @PUT()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<EventDetailModel>> requestEventInviteStatus(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<EventGuestListModel>> requestEventGuestList(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<EventOrgDateModel>> requestEventOrganizerDetail(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<FollowUnfollowModel>> requestUserFollowUnFollow(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<CurrentUserRatingModel>> requestAddRatings(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<ContactListModel>> requestFollowingList(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<ContactListModel>> requestFollowersList(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<FollowUnfollowModel>> requestVenueFollow(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<FollowUnfollowModel>> requestEventFollow(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<ChatResponseModel>> requestChatFriendList(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<ChatMessageModel>> requestChatMsg(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<ChatModel>> requestChatCreate(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @GET()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<EventChatListModel>> requestEventChat(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token);

        @Multipart
        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<String>> requestChatUpload(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Part MultipartBody.Part part);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<ChatResponseModel>> requestChatUnReceivedMsg(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token);

        @GET()
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<ExclusiveDealModel>> requestBucketListDeal(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<CommonModel>> requestBlockUserAdd(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<CommonModel>> requestReportUser(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<CreateBucketListModel>> requestBucketOwnerChange(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<CommonModel>> requestBucketExit(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);


        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<ContactListModel>> requestUserSearch(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<OffersModel>> requestVenueOfferSearch(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<ActivityDetailModel>> requestActivitySearch(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<VenueObjectModel>> requestVenueSearch(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<SearchEventModel>> requestEventSearch(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<VenueRecommendedModel>> requestVenueRecommended(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token);

        @GET()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<SubscriptionModel>> requestSubcriptionPlan(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<UserDetailModel>> requestSentOtp(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<UserDetailModel>> requestUserVerifyOtp(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<UserDetailModel>> requestVerifyPhoneChange(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<UserDetailModel>> requestChangePhone(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<CommanSearchModel>> requestCommanSearch(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<CommanSearchModel>> requestSendGifts(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @GET()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<UserDetailModel>> requestUserProfile(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<SubscriptionVoucherListModel>> requestSubscriptionGiftList(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token);


        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<ExploreModel>> requestCommanExplore(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<ClaimOfferModel>> requestClaimSpecialOffer(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<ClaimHistoryModel>> requestClaimHistory(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<MyWalletModel>> requestWalletMyItem(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<MyWalletModel>> requestWalletGift(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<MyUserFeedModel>> requestUserFeed(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<MyWalletModel>> requestHistoryList(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token);


        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<CommanMsgModel>> requestSendGift(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<BrunchListModel>> requestBrunchList(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<SpecialOfferModel>> requestBrunchBySpecialOffer(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<StoryObjectModel>> requestStoryCreateByUser(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<InviteFriendModel>> requestInviteFriend(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @GET()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<BucketListModel>> requestMyBucketList(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<InviteFriendModel>> requestMyOutingList(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<InviteFriendModel>> requestUpdateInviteStatus(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<InviteFriendModel>> requestUpdateOuting(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<MainNotificationModel>> requestUserNotificationList(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<CommanMsgModel>> requestPackageRedeem(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<BucketEventListModel>> requestEvents(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<BucketEventListModel>> requestEventsHistory(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<BucketEventListModel>> requestUpcomingHistory(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<ChatModel>> requestDeleteChat(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<UserDetailModel>> requestFeedRecommandation(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<String>> requestLinkCreate(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<InviteFriendModel>> requestChangeOwnership(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<OffersModel>> requestVenueOfferDetail(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @GET()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<InviteFriendModel>> requestOutingDetail(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<UserDetailModel>> requestContactAddQuery(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<UserDetailModel>> requestContactQueryList(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<ContactChatRepliesModel>> requestContactAddQueryReply(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<UserDetailModel>> requestUserBlockList(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token);

        @HTTP(method = "DELETE", hasBody = true)
        @Headers({"Accept: application/json"})
        Call<ContainerModel<CommonModel>> requestUserBlockRemove(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<UserDetailModel>> requestUserDeleteAccount(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<CommanMsgModel>> requestUserNotificationRead(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<MainNotificationModel>> requestUserNotificationUnReadCount(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<UserDetailModel>> requestUserDeactivateAccount(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token);

        @GET()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<UpdateStatusModel>> requestUpdatesStatus(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<CommanMsgModel>> requestUpdatesRead(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<HomeObjectModel>> requestSearchGetHomeBlock(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token);

        @HTTP(method = "DELETE", hasBody = true)
        @Headers({"Accept: application/json"})
        Call<ContainerModel<UserDetailModel>> requestDeleteInvitation(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @HTTP(method = "DELETE", hasBody = true)
        @Headers({"Accept: application/json"})
        Call<ContainerModel<InviteFriendModel>> requestOutingOwnerDelete(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<UserDetailModel>> requestSuggestedUser(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<VenueObjectModel>> requestSuggestedVenues(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<CommonModel>> requestUserRemoveSuggestion(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @PUT()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<UserDetailModel>> requestUserUpdateSettings(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<UserDetailModel>> requestUserFollowList(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token);

        @PUT()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<CommonModel>> requestUserFollowAction(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<CommanMsgModel>> requestContactReplyRead(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<PaymentCredentialModel>> requestPurchaseMembership(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<MemberShipModel>> requestMembershipPackageDetail(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<PromoCodeModel>> requestSubscriptionPromoCodeValidation(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<LoginRequestModel>> requestUserAuthRequest(@Url String url, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<YachtDetailModel>> requestYachtDetail(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<YachtClubModel>> requestYachtClubDetail(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<YachtsOfferModel>> requestYachtOfferDetail(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<YachtAvailableSlotsModel>> requestYachtOfferPackageSlots(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<UserDetailModel>> requestPromoterRequestCreate(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<UserDetailModel>> requestPromoterRingUpdate(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @GET()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<PromoterProfileModel>> requestPromoterGetProfile(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<CreateBucketListModel>> requestPromoterCreateCircle(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<PromoterCirclesModel>> requestPromoterCircleDetail(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<VenueObjectModel>> requestPromoterVenues(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<UserDetailModel>> requestPromoterMyRingMember(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<UserDetailModel>> requestPromoterMyRingRemoveMember(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<UserDetailModel>> requestPromoterMyRingMemberForSubAdmin(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);


        @PUT()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<PromoterCirclesModel>> requestPromoterUpdateCircle(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @HTTP(method = "DELETE", hasBody = true)
        @Headers({"Accept: application/json"})
        Call<ContainerModel<PromoterCirclesModel>> requestPromoterDeleteCircle(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<PromoterCirclesModel>> requestPromoterCircleAddMember(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<PromoterCirclesModel>> requestPromoterCircleRemoveMember(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<PromoterAddRingModel>> requestPromoterAddToRing(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<PromoterAddRingModel>> requestPromoterJoinMyRing(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);


        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<PromoterVenueModel>> requestPromoterVenueRemove(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<UserDetailModel>> requestPromoterInvitationCreate(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<PromoterEventModel>> requestPromotereventDetail(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<PromoterEventModel>> requestPromotereventHideShow(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<PromoterNewEventListModel>> requestPromoterEventList(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<PromoterEventModel>> requestPromoterEventCancel(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @GET()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<ComplimentaryProfileModel>> requestComplimentaryProfile(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<MainNotificationModel>> requestPromoterUserNotification(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<PromoterAddRingModel>> requestPromoterRingUpdateStatus(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);


        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<PromoterAddRingModel>> requestPromoterRingUpdateBySubAdmin(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);


        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<MainNotificationModel>> requestPromoterEventNotification(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<PromoterEventModel>> requestPromoterEventupdate(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<PromoterEventModel>> requestPromotereventDetailUser(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @GET()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<PromoterProfileModel>> requestGetProfile(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<PromoterEventModel>> requestPromoterEventListUser(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<CommonModel>> requestPromoterToggleWishList(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<EventInOutPenaltyModel>> requestPromoterUpdateInviteStatus(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<UserDetailModel>> requestPromoterUpdate(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<MainNotificationModel>> requestCmEventNotification(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<MainNotificationModel>> requestCmUserNotification(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<CommonModel>> requestPromoterMemberBan(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<ReviewReplayModel>> requestReplayAddUpdateReview(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @HTTP(method = "DELETE", hasBody = true)
        @Headers({"Accept: application/json"})
        Call<ContainerModel<ReviewReplayModel>> requestDeleteReview(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<PromoterChatModel>> requestChatPromoterContactList(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<InvitedUserModel>> requestPromoterInviteUpdateStatus(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<InvitedUserModel>> requestPromoterPlusOneInviteStatus(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<PromoterEventInviteModel>> requestPromoterEventInvite(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<PromoterEventModel>> requestPromoterEventHistory(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @HTTP(method = "DELETE", hasBody = true)
        @Headers({"Accept: application/json"})
        Call<ContainerModel<PromoterEventModel>> requestPromoterEventDelete(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST
        @Headers({"Accept: application/json"})
        Call<ContainerModel<PromoterEventModel>> requestPromoterEventComplete(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<PromoterEventModel>> requestPromoterUserInEvent(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token);


        @GET
        Call<ResponseBody> requestgetCountryCode(@Url String url);

        @POST
        @Headers({"Accept: application/json"})
        Call<ContainerModel<PromoterEventModel>> requestPromoterPlusOneInviteUser(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST
        @Headers({"Accept: application/json"})
        Call<ContainerModel<PromoterEventModel>> requestPromoterPlusOneInviteUserUpdateStatus(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST
        @Headers({"Accept: application/json"})
        Call<ContainerModel<UserDetailModel>> requestPromoterPlusOneInviteUserRemove(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<UserDetailModel>> requestPromoterPlusOneMyGroup(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<PromoterEventModel>> requsetPromoterEventPlusOneInvite(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<PromoterEventModel>> requestPromoterPlusOneList(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<PromoterEventModel>> requestPromoterEventHistoryUser(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<PromoterInvitedUserModel>> requestPromoterEventInviteUser(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<PromoterEventModel>> requestPromoterCloseSport(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<PromoterEventModel>> requestPromoterAddToCircle(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @GET()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<List<String>>> requestPromoterEventGetCustomCategory(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<UserDetailModel>> requestPromoterPlusOneGroupListUser(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<UserDetailModel>> requestPromoterPlusOneGroupLeave(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<PromoterEventModel>> requestPromoterPlusOneEvenDetail(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<PromoterVenueModel>> requestPromoterVenueSetFrequencyCmVisit(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<UserDetailModel>> requestPromoterSubAdminList(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<List<String>>> requestGetVenueMediaUrls(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<ContactListModel>> requestUserSearchAll(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<PromoterPenaltyModel>> requestPromoterPenaltyList(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<PenaltyListModel>> requestPromoterPenaltyRemove(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<PromoterCirclesModel>> requestPromoterCirclesByUserId(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<NotificationModel>> requestPromoterUpdateSubadminStatus(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<String>> requestGetToken(@Url String url, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<RaynaTicketDetailModel>> requestRaynaCustomUserDetail(
                @Url String url,
                @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token,
                @Body JsonObject bodyRequest
        );


        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<RaynaTicketDetailModel>> requestRaynaSearch(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<TourOptionsModel>> requestRaynaTourOptions(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<TourOptionDetailModel>> requestRaynaTourOptionDetailByTourId(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<RaynaTimeSlotModel>> requestRaynaTourTimeSlot(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<TourOptionsModel>> requestRaynaTourAvailability(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<TourOptionsModel>> requestRaynaTourPolicy(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<RaynaWhosinBookingRulesModel>> requestWhosinTicketTourPolicy(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<TravelDeskCancellationPolicyModel>> requestTravelTicketTourPolicy(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<PaymentCredentialModel>> requestRaynaTourBooking(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<RaynaTicketBookingModel>> requestRaynaTourBookingCancel(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<VenuePromoCodeModel>> requestVenuePromoCode(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<ExploreObjectModel>> requestNewExplore(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<RaynaTicketDetailModel>> requestGetRaynaCustomTicketList(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);


        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<UserDetailModel>> requestCheckUserSession(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<ExploreObjectModel>> requestCmProfileTicketsBlock(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<UserDetailModel>> requestReportAdd(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<ReportUseListModel>> requestReportUserList(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<ReportUseListModel>> requestReportDetail(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<CurrentUserRatingModel>> requestMyReviewList(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token);

        @HTTP(method = "DELETE", hasBody = true)
        @Headers({"Accept: application/json"})
        Call<ContainerModel<CurrentUserRatingModel>> requestMyReviewDelete(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<AdListModel>> requestAdList(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<AdListModel>> requestFavTicket(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<RaynaCheckReviewModel>> requestCheckReview(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<RaynaCheckReviewModel>> requestCheckReview(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<NotificationModel>> requestInAppNotificationRead(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<InAppListUserModel>> requestInAppListUser(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);


        @HTTP(method = "DELETE", hasBody = true)
        @Headers({"Accept: application/json"})
        Call<ContainerModel<NotificationModel>> requestUserNotificationUser(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);


        @Multipart
        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<JuniperTourDataModel>> requestJuniperAvailability(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Part("data") RequestBody jsonEncodedObject);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<RaynaWhosinMoreInfoModel>> requestRaynaMoreInfo(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<PromotionalMainModel>> requestPromotionBanner(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<TravelDeskPickUpListModel>> requestTravelDeskPickUpList(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<TravelDeskTourAvailabilityModel>> requestTravelDeskTourAvailability(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<MyCartMainModel>> requestMyCart(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<List<String>>> requestAddToGoogleWallet(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<WhosinAvailabilityModel>> requestCheckAvailability(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<OctaTourAvailabilityModel>> requestOctaTourAvailability(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @GET()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<Map<String, Map<String, String>>>> requestCommanLang(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<JPHotelTourAvailabilityModel>> requestJpHotelAvailability(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<JpHotelBookingRuleModel>> requestJpHotelBookingRule(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<HomeTicketsModel>> requestSuggestedTicketList(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

    }


    // endregion
    private class CommonContainerDataCallback<T> implements Callback<ContainerModel<T>> {

        private final @Nullable
        RestCallback<ContainerModel<T>> delegate;

        public CommonContainerDataCallback(final RestCallback<ContainerModel<T>> delegate) {
            this.delegate = delegate;
        }

//        private boolean isValid() {
//            return delegate != null && _context != null;
//        }

        private boolean isValid() {
            if (delegate == null) {
                return false;
            }
            if (delegate.getLifecycleOwner() != null) {
                if (!delegate.getLifecycleOwner().getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.CREATED)) {
                    return false;
                }
            }
            return _context != null;

        }

        @Override
        public void onResponse(@NonNull Call<ContainerModel<T>> call, @NonNull Response<ContainerModel<T>> response) {
            AppExecutors.get().mainThread().execute(() -> {

                if (!isValid()) {
                    return;
                }

                if (!response.isSuccessful()) {
                    if (response.errorBody() == null) {
                        delegate.result(null, _context.getString(R.string.service_message_code_failed) + response.code());
                        return;
                    }
                    try {
                        String errorBody = response.errorBody().string();
                        if (!TextUtils.isEmpty(errorBody)) {
                            try {
                                ContainerModel obj = new Gson().fromJson(errorBody, ContainerModel.class);
                                if (obj != null && !TextUtils.isEmpty(obj.message)) {
                                    delegate.result(null, obj.message);
                                    return;
                                }
                            } catch (JsonSyntaxException e) {
                                e.printStackTrace();
                                delegate.result(null, e.getMessage());
                                return;
                            }

                        }
                        delegate.result(null, _context.getString(R.string.service_message_code_failed) + response.code());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return;
                }

                ContainerModel<T> body = response.body();
                if (body == null) {
                    delegate.result(null, _context.getString(R.string.service_message_body_empty));
                    return;
                }
                if (body.status == 0) {
                    delegate.result(null, body.message);
                    return;
                }
                delegate.result(body, "");
            });
        }

        @Override
        public void onFailure(@NonNull Call<ContainerModel<T>> call, @NonNull Throwable t) {
            if (isIgnorableNetworkError(t)) {
                return;
            }
            AppExecutors.get().mainThread().execute(() -> {
                if (delegate != null) {
                    delegate.setThrowable(t);
                    delegate.result(null, t.getMessage());
                }
            });
        }
    }

    private class CommonContainerCallback<T extends ModelProtocol> implements Callback<ContainerModel<T>> {

        private final @Nullable
        RestCallback<ContainerModel<T>> delegate;

        public CommonContainerCallback(final RestCallback<ContainerModel<T>> delegate) {
            this.delegate = delegate;
        }

        private boolean isValid() {
            if (delegate == null) {
                return false;
            }
            if (delegate.getLifecycleOwner() != null) {
                if (!delegate.getLifecycleOwner().getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.CREATED)) {
                    return false;
                }
            }
            return _context != null;

        }

        @Override
        public void onResponse(@NonNull Call<ContainerModel<T>> call, @NonNull Response<ContainerModel<T>> response) {
            AppExecutors.get().mainThread().execute(() -> {
                if (!isValid()) {
                    return;
                }
                if (!response.isSuccessful()) {

                    if (response.errorBody() == null) {
                        delegate.result(null, _context.getString(R.string.service_message_code_failed) + response.code());
                        return;
                    }

                    try {
                        String errorBody = response.errorBody().string();
                        if (!TextUtils.isEmpty(errorBody)) {
                            try {
                                ContainerModel obj = new Gson().fromJson(errorBody, ContainerModel.class);
                                if (obj != null && !TextUtils.isEmpty(obj.message)) {
                                    delegate.result(null, obj.message);
                                    return;
                                }
                            } catch (Exception e) {
                                delegate.result(null, e.getMessage());
                                return;
                            }
                        }
                        delegate.result(null, _context.getString(R.string.service_message_code_failed) + response.code());
                    } catch (IOException e) {
                        delegate.result(null, e.getMessage());
                    }
                    return;
                }

                ContainerModel<T> body = response.body();
                if (body == null) {
                    delegate.result(null, _context.getString(R.string.service_message_body_empty));
                    return;
                }
                if (body.status == 0) {
                    delegate.result(null, body.message);
                    return;
                }
                if (body.data == null) {
                    delegate.result(body, "");
                    return;
                }
                if (body.data.isValidModel()) {
                    delegate.result(body, "");
                } else {
                    delegate.result(null, _context.getString(R.string.error_object_invalid));
                }
            });
        }

        @Override
        public void onFailure(@NonNull Call<ContainerModel<T>> call, @NonNull Throwable t) {
            if (isIgnorableNetworkError(t)) {
                return;
            }
            AppExecutors.get().mainThread().execute(() -> {
                if (delegate != null && !call.isCanceled()) {
                    delegate.setThrowable(t);
                    delegate.result(null, t.getMessage());
                }
            });
        }
    }

    private class CommonContainerListCallback<T extends ModelProtocol> implements Callback<ContainerListModel<T>> {

        private final @Nullable
        RestCallback<ContainerListModel<T>> delegate;

        public CommonContainerListCallback(final RestCallback<ContainerListModel<T>> delegate) {
            this.delegate = delegate;
        }

        private boolean isValid() {
            if (delegate == null) {
                return false;
            }
            if (delegate.getLifecycleOwner() != null) {
                if (!delegate.getLifecycleOwner().getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.CREATED)) {
                    return false;
                }
            }
            return _context != null;

        }

        @Override
        public void onResponse(@NonNull Call<ContainerListModel<T>> call, @NonNull Response<ContainerListModel<T>> response) {
            AppExecutors.get().mainThread().execute(() -> {

                if (!isValid()) {
                    return;
                }

                if (delegate == null) {
                    return;
                }

                if (!response.isSuccessful()) {
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            if (!TextUtils.isEmpty(errorBody)) {
                                try {
                                    ContainerModel obj = new Gson().fromJson(errorBody, ContainerModel.class);
                                    if (obj != null && !TextUtils.isEmpty(obj.message)) {
                                        delegate.result(null, obj.message);
                                        return;
                                    }
                                } catch (JsonSyntaxException e) {
                                    e.printStackTrace();
                                    delegate.result(null, e.getMessage());
                                    return;
                                }

                            }
                        }
                    } catch (IOException e) {
                        delegate.result(null, e.getLocalizedMessage());
                    }
                    delegate.result(null, _context.getString(R.string.service_message_code_failed) + response.code());
                    return;
                }

                ContainerListModel<T> body = response.body();
                // Empty body
                if (body == null) {
                    delegate.result(null, _context != null ? _context.getString(R.string.service_message_body_empty) : "Body is empty!");
                    return;
                }
                if (body.status == 0) {
                    delegate.result(null, body.message);
                    return;
                }

                // null data so the the request is successful
                if (body.data == null) {
                    delegate.result(body, "");
                    return;
                }

                // all the models of body.data should be valid
                if (body.data.stream().allMatch(p -> p == null || p.isValidModel())) {
                    delegate.result(body, "");
                } else {
                    delegate.result(null, _context != null ? _context.getString(R.string.error_object_invalid) : "Invalid object!");
                }
            });
        }

        @Override
        public void onFailure(@NotNull Call<ContainerListModel<T>> call, @NotNull Throwable t) {
            if (isIgnorableNetworkError(t)) {
                return;
            }
            AppExecutors.get().mainThread().execute(() -> {
                if (delegate != null && !call.isCanceled()) {
                    delegate.setThrowable(t);
                    delegate.result(null, t.getMessage());
                }
            });
        }
    }

    private boolean isIgnorableNetworkError(Throwable t) {
        return t instanceof java.net.UnknownHostException ||
                t instanceof java.net.ConnectException ||
                t instanceof java.net.SocketTimeoutException;
    }


    // --------------------------------------
    // region Private
    // --------------------------------------

    private String _getToken() {
        return String.format("Bearer %s", SessionManager.shared.getToken());
    }

    private IRequestService _getService() {
        return RestClient.getService().create(IRequestService.class);
    }

    // --------------------------------------
    // region Authentication
    // --------------------------------------

    public void requestGetToken(final RestCallback<ContainerModel<String>> delegate) {
        JsonObject object = new JsonObject();
        object.addProperty("deviceId", Utils.getDeviceUniqueId(_context));
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.USER_GET_TOKEN);
        _getService().requestGetToken(url, object).enqueue(new CommonContainerDataCallback<>(delegate));
    }

    public void requestLoginWithGoogle(JsonObject object, final RestCallback<ContainerModel<UserTokenModel>> delegate) {
        object.addProperty("deviceId", Utils.getDeviceUniqueId(_context));
        String url = UrlManager.shared.getServiceUrlV2(EndpointConstants.AUTH_GOOGLE_LOGIN_ENDPOINT);
        _getService().requestAuth(url, object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestLoginWithFb(JsonObject object, final RestCallback<ContainerModel<UserTokenModel>> delegate) {
        String url = UrlManager.shared.getServiceUrlV2(EndpointConstants.AUTH_FACEBOOK_LOGIN_ENDPOINT);
        _getService().requestAuth(url, object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestLoginWithPhone(JsonObject object, final RestCallback<ContainerModel<UserDetailModel>> delegate) {
        object.addProperty("platform", "android");
        object.addProperty("deviceId", Utils.getDeviceUniqueId(_context));
        Log.d("requestLoginWithPhone", "requestLoginWithPhone: " + object);
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.AUTH_PHONE_LOGIN_NEW_ENDPOINT);
        _getService().requestLoginWithPhone(url, object).enqueue(new CommonContainerCallback<>(delegate));

    }

    public void requestGuestLogin(final RestCallback<ContainerModel<UserTokenModel>> delegate) {
        JsonObject object = new JsonObject();
        object.addProperty("deviceId", Utils.getDeviceUniqueId(_context));
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.GUEST_LOGIN_ENDPOINT);
        _getService().requestAuth(url, object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestVerifyPassword(JsonObject object, final RestCallback<ContainerModel<UserTokenModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.AUTH_VERIFY_PASSWORD_ENDPOINT);
        _getService().verifyPassword(url, object).enqueue(new CommonContainerCallback<>(delegate));

    }

    public void requestApprovedLogin(JsonObject object, final RestCallback<ContainerModel<UserDetailModel>> delegate) {
        String url = UrlManager.shared.getServiceUrlV2(EndpointConstants.APPROVE_LOGIN_REQUEST);
        _getService().requestApprovedLogin(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));

    }

    public void requestTwoAuthEmail(JsonObject object, final RestCallback<ContainerModel<UserDetailModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.USER_TWO_AUTH_EMAIL_REQUEST);
        _getService().requestTwoAuthEmail(url, object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestLogout(JsonObject object, final RestCallback<ContainerModel<UserDetailModel>> delegate) {
        String url = UrlManager.shared.getServiceUrlV2(EndpointConstants.USER_LOGOUT);
        _getService().requestLogout(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestUploadImage(Activity activity, Uri filePart, final RestCallback<ContainerModel<ImageUploadModel>> delegate) {
        String realPathFromURIPath = Utils.getRealPathFromURIPath(filePart, activity);
        File file = new File(realPathFromURIPath);
        MultipartBody.Part part = MultipartBody.Part.createFormData("image", file.getName(),
                RequestBody.create(MediaType.parse("image/jpeg"), file));
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.UPLOAD_IMAGE_ENDPOINT);
        _getService().requestUploadImage(url, _getToken(), part).enqueue(new CommonContainerDataCallback<>(delegate));
    }

    public void requestUploadListImages(Activity activity, List<Uri> fileParts, final RestCallback<ContainerModel<ImageListUploadModel>> delegate) {
        Set<Uri> uniqueFileParts = new HashSet<>(fileParts);  // Remove duplicate URIs
        List<MultipartBody.Part> parts = new ArrayList<>();
        int index = 1;
        for (Uri filePart : uniqueFileParts) {
            String realPathFromURIPath = Utils.getRealPathFromURIPathForListImages(filePart, activity);

            if (realPathFromURIPath != null && !realPathFromURIPath.isEmpty()) {
                File file = new File(realPathFromURIPath);

                if (file.exists()) {
                    String mimeType = Utils.getMimeType(filePart, activity);
                    String imageName = "image" + index;
                    MultipartBody.Part part = MultipartBody.Part.createFormData(
                            imageName,
                            file.getName(),
                            RequestBody.create(MediaType.parse(mimeType), file)
                    );

                    parts.add(part);
                    index++;
                }
            }
        }

        String url = UrlManager.shared.getServiceUrl(EndpointConstants.UPLOAD_IMAGE_ENDPOINT);
        _getService().requestUploadImageList(url, _getToken(), parts).enqueue(new CommonContainerDataCallback<>(delegate));
    }

    public void requestEditUploadImage(MultipartBody.Part filePart, final RestCallback<ContainerModel<ImageUploadModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.UPLOAD_IMAGE_ENDPOINT);
        _getService().requestUploadImage(url, _getToken(), filePart).enqueue(new CommonContainerDataCallback<>(delegate));
    }

    public void requestVerifyOtp(JsonObject object, final RestCallback<ContainerModel<UserTokenModel>> delegate) {
        Log.d("OTP", "verifyOtp: " + object);
        String url = UrlManager.shared.getServiceUrlV2(EndpointConstants.AUTH_VERIFY_OTP_ENDPOINT);
        _getService().verifyOtp(url, object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestUpdateFcmToken(String playerID, String deviceId, final RestCallback<ContainerModel<UserDetailModel>> delegate) {
        JsonObject object = new JsonObject();
        object.addProperty("token", "");
        object.addProperty("deviceId", deviceId);
        object.addProperty("platform", "android");
        object.addProperty("provider", "onesignal");
        object.addProperty("playerId", playerID);
        String url = UrlManager.shared.getServiceUrlV2(EndpointConstants.USER_UPDATE_FCM_TOKEN);
        Log.d("requestUpdateFcmToken", "requestUpdateFcmToken: " + object);
        _getService().requestUpdateFcmToken(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestUpdateProfile(JsonObject object, final RestCallback<ContainerModel<UserDetailModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.UPDATE_PROFILE_ENDPOINT);
        _getService().requestUpdateProfile(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestAppSetting(final RestCallback<ContainerModel<AppSettingModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.APP_SETTING_ENDPOINT);
        _getService().requestAppSetting(url, _getToken()).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestVenueAllFilters(final RestCallback<ContainerModel<VenueFiltersModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.VENUE_ALL_FILTERS);
        _getService().requestVenueFilter(url, _getToken()).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestSelectedPreference(JsonObject jsonObject, final RestCallback<ContainerModel<GetPrefrenceModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.SELECTED_PREFERENCES_ENDPOINT);
        _getService().requestSelectedPreference(url, jsonObject, _getToken()).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestGetPreference(final RestCallback<ContainerModel<GetPrefrenceModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.SELECTED_PREFERENCES_GET_ENDPOINT);
        _getService().requestGetSelectedPreference(url, _getToken()).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestHomeBlockList(final RestCallback<ContainerModel<HomeObjectModel>> delegate) {
        if (TextUtils.isEmpty(_getToken())) {
            delegate.result(null, "");
            return;
        }
        String url = UrlManager.shared.getServiceUrlV2(EndpointConstants.HOME_BLOCK_LIST);
        _getService().requestHomeBlockList(url, _getToken()).enqueue(new CommonContainerDataCallback<>(delegate));
    }

    public void requestUsersByIds(List<String> userIds, final RestCallback<ContainerListModel<UserDetailModel>> delegate) {
        JsonObject object = new JsonObject();
        JsonArray jsonArray = new Gson().toJsonTree(userIds).getAsJsonArray();
        object.add("userIds", jsonArray);
        object.addProperty("type", "chat");
        String url = UrlManager.shared.getServiceUrlV2(EndpointConstants.USER_LIST_BY_IDS_ENDPOINT);
        _getService().requestUserByIds(url, _getToken(), object).enqueue(new CommonContainerListCallback<>(delegate));
    }

    public void requestOfferList(JsonObject object, final RestCallback<ContainerListModel<OffersModel>> delegate) {
        object.addProperty("lat", LocationManager.shared.lat);
        object.addProperty("long", LocationManager.shared.lng);
        String url = UrlManager.shared.getServiceUrlV2(EndpointConstants.CATEGORY_OFFER_LIST);
        _getService().requestOfferDetails(url, _getToken(), object).enqueue(new CommonContainerListCallback<>(delegate));
    }

    public void requestVenueDetail(String venueId, final RestCallback<ContainerModel<VenueObjectModel>> delegate) {
        String url = UrlManager.shared.getServiceUrlV2(EndpointConstants.VENUE_DETAILS_ENDPOINT);
        JsonObject object = new JsonObject();
        object.addProperty("venueId", venueId);
        object.addProperty("lat", LocationManager.shared.lat);
        object.addProperty("long", LocationManager.shared.lng);


       /* url = url + "?venueId=" + object;
        if (LocationManager.shared.lat != 0.0) {
            url = url + "&lat=" + LocationManager.shared.lat + "&long=" + LocationManager.shared.lng;
        }*/
        _getService().requestVenueDetails(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }


    public void requestFollowUnFollow(String id, String type, final RestCallback<ContainerModel<FollowUnfollowModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.FOLLOW_UNFOLLOW_ENDPOINT);
        JsonObject object = new JsonObject();
        object.addProperty("id", id);
        object.addProperty("type", type);
        _getService().requestFollowUnFollow(url, _getToken(), object).enqueue(new CommonContainerDataCallback<>(delegate));
    }

    public void requestDealDetail(String dealId, final RestCallback<ContainerModel<ExclusiveDealModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.DEAL_DETAILS_ENDPOINT);
        JsonObject object = new JsonObject();
        object.addProperty("dealId", dealId);
        _getService().requestDealDetails(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestCategoryDetail(String categoryId, final RestCallback<ContainerModel<CategoriesModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.CATEGORY_DETAILS_ENDPOINT);
        JsonObject object = new JsonObject();
        object.addProperty("categoryId", categoryId);
        _getService().requestCategory(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestCategoryDealList(JsonObject object, final RestCallback<ContainerListModel<VoucherModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.HOME_DEAL_LIST);
        _getService().requestOfferList(url, _getToken(), object).enqueue(new CommonContainerListCallback<>(delegate));
    }

    public void requestSubscriptionDetail(final RestCallback<ContainerModel<MemberShipModel>> delegate) {
        String url = UrlManager.shared.getServiceUrlV2(EndpointConstants.SUBSCRIPTION_DETAIL);
        _getService().requestSubscription(url, _getToken()).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestSubscriptionVoucherList(final RestCallback<ContainerListModel<SubscriptionVoucherListModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.SUBSCRIPTION_VOUCHER_LIST);
        _getService().requestSubscriptionVoucherList(url, _getToken()).enqueue(new CommonContainerListCallback<>(delegate));
    }

    public void requestStripePaymentIntent(JsonObject object, final RestCallback<ContainerModel<PaymentCredentialModel>> delegate) {
        String url = UrlManager.shared.getServiceUrlV2(EndpointConstants.STRIPE_PAYMENT_INTENT);
        _getService().requestStripePaymentIntent(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestGetContact(List<String> number, List<String> email, final RestCallback<ContainerListModel<ContactListModel>> delegate) {
        JsonObject object = new JsonObject();
        JsonElement numbers = new Gson().toJsonTree(number);
        JsonElement emails = new Gson().toJsonTree(email);
        object.add("email", emails);
        object.add("phone", numbers);
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.CONTACT_LIST);
        _getService().requestContactList(url, _getToken(), object).enqueue(new CommonContainerListCallback<>(delegate));
    }

    public void requestBucketList(final RestCallback<ContainerModel<BucketListModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.BUCKET_LIST);
        _getService().requestBucketList(url, _getToken()).enqueue(new CommonContainerDataCallback<>(delegate));
    }

    public void requestBucketListForChatProfile(final RestCallback<ContainerModel<BucketChatMainProfileModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.BUCKET_LIST);
        _getService().requestChatProfileBucketList(url, _getToken()).enqueue(new CommonContainerDataCallback<>(delegate));
    }

    public void requestCreateBucketList(String name, String userIds,
                                        String image, final RestCallback<ContainerModel<CreateBucketListModel>> delegate) {
        JsonObject object = new JsonObject();
        object.addProperty("name", name);
        object.addProperty("image", image);
        object.addProperty("userIds", userIds);
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.CREATE_BUCKET_LIST);
        _getService().requestCreateBucketList(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestBucketUpdate(JsonObject object, final RestCallback<ContainerModel<BucketListModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.UPDATE_BUCKET_LIST);
        _getService().requestUpdateBucketList(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestBucketShare(String id, String userIds, String name, String image, final RestCallback<ContainerModel<CreateBucketListModel>> delegate) {
        JsonObject object = new JsonObject();
        object.addProperty("id", id);
        if (!TextUtils.isEmpty(name)) {
            object.addProperty("name", name);
        }
        if (!TextUtils.isEmpty(image)) {
            object.addProperty("image", image);
        }
        if (!TextUtils.isEmpty(userIds)) {
            object.addProperty("userIds", userIds);
        }
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.BUCKET_SHARE_UPDATE);
        _getService().requestBucketUpadte(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestAddBucket(String bucketId, String itemId, final RestCallback<ContainerModel<CreateBucketListModel>> delegate) {
        JsonObject object = new JsonObject();
        object.addProperty("bucketId", bucketId);
        object.addProperty("itemId", itemId);
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.ADD_BUCKET_LIST);
        _getService().requestAddBucketList(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestRemoveBucket(String bucketId, final RestCallback<ContainerModel<CreateBucketListModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.DELETE_BUCKET);
        url = url + bucketId;
        _getService().requestRemoveBucket(url, _getToken()).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestBucketDetail(String bucketId, final RestCallback<ContainerModel<CreateBucketListModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.BUCKET_DETAIL);
        url = url + bucketId;
        _getService().requestBucketDetail(url, _getToken()).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestUpdateBucket(JsonObject object, final RestCallback<ContainerModel<CreateBucketListModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.UPDATE_BUCKET_ITEM);
        _getService().requestUpdateBucket(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestAddBucketGallery(String id, String image, final RestCallback<ContainerModel<CreateBucketListModel>> delegate) {
        JsonObject object = new JsonObject();
        object.addProperty("id", id);
        object.addProperty("image", image);
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.ADD_BUCKET_GALLERY);
        _getService().requestAddBucketGallery(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestDeleteBucketGallery(String bucketId, String image, final RestCallback<ContainerModel<CreateBucketListModel>> delegate) {
        JsonObject object = new JsonObject();
        object.addProperty("bucketId", bucketId);
        object.addProperty("image", image);
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.DELETE_BUCKET_GALLERY);
        _getService().requestDeleteBucketGallery(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestActivityDetail(String activityId, final RestCallback<ContainerModel<ActivityDetailModel>> delegate) {
        JsonObject object = new JsonObject();
        object.addProperty("activityId", activityId);
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.ACTIVITY_DETAIL);
        url = url + "?activityId=" + activityId;
        _getService().requestActivityDetail(url, _getToken()).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestActivityList(String activity_type, final RestCallback<ContainerListModel<ActivityDetailModel>> delegate) {
//        JsonObject object = new JsonObject();
//        object.addProperty("type", activity_type);
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.ACTIVITY_LIST);

        if (activity_type == null && activity_type.isEmpty()) {
            url = url + "?limit=10&page=1";
        } else {
            url = url + "?type=" + activity_type + "&limit=10&page=1";
        }
        _getService().requestActivityLIST(url, _getToken()).enqueue(new CommonContainerListCallback<>(delegate));
    }

    public void requestBannerList(final RestCallback<ContainerListModel<BannerModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.BANNER_LIST);
        _getService().requestBannerList(url, _getToken()).enqueue(new CommonContainerListCallback<>(delegate));
    }

    public void requestActivityFetchDate(String activityId, final RestCallback<ContainerListModel<ActivityFetchModel>> delegate) {
        JsonObject object = new JsonObject();
        object.addProperty("activityId", activityId);
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.ACTIVITY_FETCH_DATE);
        url = url + "?activityId=" + activityId;
        _getService().requestActivityFetch(url, _getToken()).enqueue(new CommonContainerListCallback<>(delegate));
    }

    public void requestActivityFetchTimeSlot(String activityId, String date, final RestCallback<ContainerListModel<ActivityFetchModel>> delegate) {
        JsonObject object = new JsonObject();
        object.addProperty("activityId", activityId);
        object.addProperty("date", date);
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.ACTIVITY_FETCH_SLOT);
        url = url + "?activityId=" + activityId + "&date=" + date;
        _getService().requestActivityFetch(url, _getToken()).enqueue(new CommonContainerListCallback<>(delegate));
    }

    public void requestRatingList(String type, String id, int limit, int page, final RestCallback<ContainerModel<ReviewModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.ALL_REVIEW_LIST);
        JsonObject object = new JsonObject();
        object.addProperty("type", type);
        object.addProperty("itemId", id);
        object.addProperty("limit", limit);
        object.addProperty("page", page);
        _getService().requestRatingList(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestRatingSummary(String id, final RestCallback<ContainerModel<TotalRatingModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.RATING_SUMMARY);
        JsonObject object = new JsonObject();
        object.addProperty("itemId", id);
        _getService().requestRatingSummary(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestEventDetail(String eventId, final RestCallback<ContainerModel<EventDetailModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.EVENT_DETAIL);
        JsonObject object = new JsonObject();
        object.addProperty("eventId", eventId);
        _getService().requestEventDetail(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestEventInviteGuest(String eventId, List<String> userIds, int extraGuest, final RestCallback<ContainerModel<EventDetailModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.EVENT_INVITE_GUEST);
        JsonObject object = new JsonObject();
        JsonArray jsonArray = new JsonArray();
        userIds.forEach(jsonArray::add);
        object.addProperty("eventId", eventId);
        object.addProperty("extraGuest", extraGuest);
        object.add("userIds", jsonArray);
        _getService().requestEventInviteGuest(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestEventInviteStatus(String eventId, String inviteStatus, final RestCallback<ContainerModel<EventDetailModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.EVENT_INVITE_STATUS);
        JsonObject object = new JsonObject();
        object.addProperty("eventId", eventId);
        object.addProperty("inviteStatus", inviteStatus);
        _getService().requestEventInviteStatus(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestEventGuestList(int page, int limit, String eventId, String inviteStatus, final RestCallback<ContainerModel<EventGuestListModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.EVENT_GUEST_LIST);
        JsonObject object = new JsonObject();
        object.addProperty("page", page);
        object.addProperty("limit", limit);
        object.addProperty("eventId", eventId);
        object.addProperty("inviteStatus", inviteStatus);
        _getService().requestEventGuestList(url, _getToken(), object).enqueue(new CommonContainerDataCallback(delegate));
    }

    public void requestEventOrganizerDetail(String orgId, final RestCallback<ContainerModel<EventOrgDateModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.EVENT_ORGANIZER_DETAIL);
        JsonObject object = new JsonObject();
        object.addProperty("org_id", orgId);
        _getService().requestEventOrganizerDetail(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestAddRatings(String id, float rating, String type, String reviews, String status, final RestCallback<ContainerModel<CurrentUserRatingModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.SUBMIT_RATE);
        JsonObject object = new JsonObject();
        object.addProperty("itemId", id);
        object.addProperty("stars", rating);
        object.addProperty("type", type);
        object.addProperty("review", reviews);
        Log.d("TAG", "requestAddRatings: " + object);
        _getService().requestAddRatings(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }


    public void requestUserFollowUnFollow(String followId, final RestCallback<ContainerModel<FollowUnfollowModel>> delegate) {
        String url = UrlManager.shared.getServiceUrlV2(EndpointConstants.FOLLOW_UNFOLLOW_USER);
        JsonObject object = new JsonObject();
        object.addProperty("followId", followId);
        _getService().requestUserFollowUnFollow(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestFollowingList(String id, final RestCallback<ContainerListModel<ContactListModel>> delegate) {
        String url = UrlManager.shared.getServiceUrlV2(EndpointConstants.FOLLOWING_LIST);
        JsonObject object = new JsonObject();
        object.addProperty("id", id);
        _getService().requestFollowingList(url, _getToken(), object).enqueue(new CommonContainerListCallback<>(delegate));
    }

    public void requestFollowersList(String id, final RestCallback<ContainerListModel<ContactListModel>> delegate) {
        String url = UrlManager.shared.getServiceUrlV2(EndpointConstants.FOLLOWERS_LIST);
        JsonObject object = new JsonObject();
        object.addProperty("id", id);
        _getService().requestFollowersList(url, _getToken(), object).enqueue(new CommonContainerListCallback<>(delegate));
    }

    public void requestVenueFollow(String id, final RestCallback<ContainerModel<FollowUnfollowModel>> delegate) {
        String url = UrlManager.shared.getServiceUrlV2(EndpointConstants.VENUE_FOLLOW);
        JsonObject object = new JsonObject();
        object.addProperty("followId", id);
        _getService().requestVenueFollow(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestEventFollow(String id, final RestCallback<ContainerModel<FollowUnfollowModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.EVENT_FOLLOW);
        JsonObject object = new JsonObject();
        object.addProperty("org_id", id);
        _getService().requestEventFollow(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }


    public void requestChatFriendList(final RestCallback<ContainerModel<ChatResponseModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.CHAT_FRIEND_LIST);
        _getService().requestChatFriendList(url, _getToken()).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestSyncChatMsg(final RestCallback<ContainerListModel<ChatMessageModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.SYNC_CHAT_MSG_LIST);
        String date = Preferences.shared.getString("lastSyncedDate");
        if (TextUtils.isEmpty(date)) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH, -1);
            SimpleDateFormat dateFormatGmt = new SimpleDateFormat(AppConstants.DATEFORMAT_LONG_TIME);
            dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
            date = dateFormatGmt.format(calendar.getTime());
        }
        JsonObject object = new JsonObject();
        object.addProperty("syncDate", date);
        _getService().requestChatMsg(url, _getToken(), object).enqueue(new CommonContainerListCallback<>(delegate));

    }

    public void requestCreateChat(String id, final RestCallback<ContainerModel<ChatModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.CHAT_CREATE);
        JsonObject object = new JsonObject();
        object.addProperty("friendId", id);
        _getService().requestChatCreate(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestEventChatList(final RestCallback<ContainerModel<EventChatListModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.EVENT_CHAT_LIST);
        _getService().requestEventChat(url, _getToken()).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestChatUploadList(Activity activity, File file, String uploadType, final RestCallback<ContainerModel<String>> delegate) {
        String realPathFromURIPath = "";
        String mediaType = "image/*";
        if (uploadType.equals("video")) {
            mediaType = "video/*";
            //realPathFromURIPath = Utils.getVideoRealPathFromURIPath(filePart, activity);
        } else if (uploadType.equals("audio")) {
            mediaType = "audio/*";
            //realPathFromURIPath = Utils.getRealPathFromURIPath(filePart, activity);
        }
        //File file = new File(realPathFromURIPath);
        MultipartBody.Part part = MultipartBody.Part.createFormData("image", file.getName(),
                RequestBody.create(MediaType.parse(mediaType), file));

        String url = UrlManager.shared.getServiceUrl(EndpointConstants.CHAT_UPLOAD_LIST);
        _getService().requestChatUpload(url, _getToken(), part).enqueue(new CommonContainerDataCallback<>(delegate));
    }

    public void requestChatUnreceivedMsg(File file, final RestCallback<ContainerModel<ChatResponseModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.CHAT_UNRECEIVED_MSG);
        _getService().requestChatUnReceivedMsg(url, _getToken()).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestBucketListDeal(final RestCallback<ContainerListModel<ExclusiveDealModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.BUCKET_LIST_DEAL);
        _getService().requestBucketListDeal(url, _getToken()).enqueue(new CommonContainerListCallback<>(delegate));
    }

    public void requestBlockUser(String blockId, final RestCallback<ContainerModel<CommonModel>> delegate) {
        String url = UrlManager.shared.getServiceUrlV2(EndpointConstants.BLOCK_USER_ADD);
        JsonObject object = new JsonObject();
        object.addProperty("blockId", blockId);
        _getService().requestBlockUserAdd(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestReportUser(String userId, String reason, final RestCallback<ContainerModel<CommonModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.REPORT_USER_ADD);
        JsonObject object = new JsonObject();
        object.addProperty("userId", userId);
        object.addProperty("reason", reason);
        _getService().requestReportUser(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestTransferBucketOwnerShip(String id, String ownerId, final RestCallback<ContainerModel<CreateBucketListModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.BUCKET_OWNER_CHANGE);
        JsonObject object = new JsonObject();
        object.addProperty("id", id);
        object.addProperty("ownerId", ownerId);
        _getService().requestBucketOwnerChange(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestBucketExit(String id, final RestCallback<ContainerModel<CommonModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.BUCKET_EXIT);
        JsonObject object = new JsonObject();
        object.addProperty("id", id);
        _getService().requestBucketExit(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestUserSearch(String search, String page, String limit, final RestCallback<ContainerListModel<ContactListModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.USER_SEARCH);
        JsonObject object = new JsonObject();
        object.addProperty("search", search);
        object.addProperty("page", page);
        object.addProperty("limit", limit);
        _getService().requestUserSearch(url, _getToken(), object).enqueue(new CommonContainerListCallback<>(delegate));
    }

    public void requestVenueOfferSearch(String search, String page, String limit, final RestCallback<ContainerListModel<OffersModel>> delegate) {
        String url = UrlManager.shared.getServiceUrlV2(EndpointConstants.VENUE_OFFER_SERACH);
        JsonObject object = new JsonObject();
        object.addProperty("search", search);
        object.addProperty("page", page);
        object.addProperty("limit", limit);
        _getService().requestVenueOfferSearch(url, _getToken(), object).enqueue(new CommonContainerListCallback<>(delegate));
    }

    public void requestVenueSearch(String search, String page, String limit, final RestCallback<ContainerListModel<VenueObjectModel>> delegate) {
        String url = UrlManager.shared.getServiceUrlV2(EndpointConstants.VENUE_SERACH);
        JsonObject object = new JsonObject();
        object.addProperty("search", search);
        object.addProperty("page", page);
        object.addProperty("limit", limit);
        _getService().requestVenueSearch(url, _getToken(), object).enqueue(new CommonContainerListCallback<>(delegate));
    }


    public void requestActivitySearch(String search, String page, String limit, final RestCallback<ContainerListModel<ActivityDetailModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.ACTIVITY_SEARCH);
        JsonObject object = new JsonObject();
        object.addProperty("search", search);
        object.addProperty("page", page);
        object.addProperty("limit", limit);
        _getService().requestActivitySearch(url, _getToken(), object).enqueue(new CommonContainerListCallback<>(delegate));
    }

    public void requestEventSearch(String search, String page, String limit, final RestCallback<ContainerListModel<SearchEventModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.EVENT_SEARCH);
        JsonObject object = new JsonObject();
        object.addProperty("search", search);
        object.addProperty("page", page);
        object.addProperty("limit", limit);
        _getService().requestEventSearch(url, _getToken(), object).enqueue(new CommonContainerListCallback<>(delegate));
    }

    public void requestVenueRecommended(final RestCallback<ContainerListModel<VenueRecommendedModel>> delegate) {
        String url = UrlManager.shared.getServiceUrlV2(EndpointConstants.VENUE_RECOMMENDE);
        _getService().requestVenueRecommended(url, _getToken()).enqueue(new CommonContainerListCallback<>(delegate));
    }

    public void requestSubscriptionPlan(final RestCallback<ContainerModel<SubscriptionModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.SUBSCRIPTION_CUSTOM);
        _getService().requestSubcriptionPlan(url, _getToken()).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestSentOtp(JsonObject object, final RestCallback<ContainerModel<UserDetailModel>> delegate) {
        String url = UrlManager.shared.getServiceUrlV2(EndpointConstants.USER_SENT_OTP);
        _getService().requestSentOtp(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestUserVerifyOtp(JsonObject object, final RestCallback<ContainerModel<UserDetailModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.USER_VERIFY_OTP);
        _getService().requestUserVerifyOtp(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestVerifyChangePhone(String otp, String type, String phone, String countcode, final RestCallback<ContainerModel<UserDetailModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.USER_OTP_UPDATE);
        JsonObject object = new JsonObject();
        object.addProperty("otp", otp);
        object.addProperty("type", type);
        object.addProperty("phone", phone);
        object.addProperty("country_code", countcode);
        _getService().requestVerifyPhoneChange(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestSendOtpNewPhone(JsonObject object, final RestCallback<ContainerModel<UserDetailModel>> delegate) {
        String url = UrlManager.shared.getServiceUrlV2(EndpointConstants.CHANGE_USER_EMAIL_PHONE);
//        JsonObject object = new JsonObject();
//        object.addProperty("phone", phone);
//        object.addProperty("country_code", countryCode);
        _getService().requestChangePhone(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public Call<ContainerListModel<CommanSearchModel>> requestCommanSearch(JsonObject object, final RestCallback<ContainerListModel<CommanSearchModel>> delegate) {
//        JsonObject object = new JsonObject();
//        object.addProperty("query", query);
//        object.addProperty("lat", LocationManager.shared.lat);
//        object.addProperty("long", LocationManager.shared.lng);
        String url = UrlManager.shared.getServiceUrlV2(EndpointConstants.COMMAN_SEARCH);

        Call<ContainerListModel<CommanSearchModel>> service = _getService().requestCommanSearch(url, _getToken(), object);

        service.enqueue(new CommonContainerListCallback<>(delegate));
        return service;
    }

    public void requestSendGifts(String friendId, String voucherId, int qty, final RestCallback<ContainerModel<CommanSearchModel>> delegate) {
        String url = UrlManager.shared.getServiceUrlV2(EndpointConstants.SEND_AS_GIFTS);
        JsonObject object = new JsonObject();
        object.addProperty("friendId", friendId);
        object.addProperty("voucherId", voucherId);
        object.addProperty("qty", qty);
        _getService().requestSendGifts(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestUserProfile(String userId, final RestCallback<ContainerModel<UserDetailModel>> delegate) {
        String url = UrlManager.shared.getServiceUrlV2(EndpointConstants.USER_PROFILE);
        url = url + userId;
        Log.d("CurrentUserProfile", "requestUserProfile: " + url);
        _getService().requestUserProfile(url, _getToken()).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestSubscriptionGiftVoucherList(final RestCallback<ContainerListModel<SubscriptionVoucherListModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.SUBSCRIPTION_VOUCHER_GIFT_LIST);
        _getService().requestSubscriptionGiftList(url, _getToken()).enqueue(new CommonContainerListCallback<>(delegate));
    }


    public Call<ContainerListModel<ExploreModel>> requestCommanExplore(String search, String dateBefore, int limit, List<String> id, final RestCallback<ContainerListModel<ExploreModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.COMMAN_EXPLORE);
        JsonObject object = new JsonObject();
        JsonArray jsonArray = new JsonArray();
        id.forEach(jsonArray::add);
        if (!TextUtils.isEmpty(search)) {
            object.addProperty("search", search);
        }
        object.addProperty("dateBefore", dateBefore);
        object.addProperty("limit", limit);
        object.add("query", jsonArray);
        object.addProperty("lat", LocationManager.shared.lat);
        object.addProperty("long", LocationManager.shared.lng);

//        _getService().requestCommanExplore( url, _getToken(), object ).enqueue( new CommonContainerListCallback<>( delegate ) );

        Call<ContainerListModel<ExploreModel>> service = _getService().requestCommanExplore(url, _getToken(), object);

        service.enqueue(new CommonContainerListCallback<>(delegate));
        return service;
    }

    public void requestClaimSpecialOffer(JsonObject object, final RestCallback<ContainerModel<ClaimOfferModel>> delegate) {
        String url = UrlManager.shared.getServiceUrlV2(EndpointConstants.CLAIM_SPECIAL_OFFER);
        _getService().requestClaimSpecialOffer(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }


    public void requestClaimHistory(final RestCallback<ContainerListModel<ClaimHistoryModel>> delegate) {
        String url = UrlManager.shared.getServiceUrlV2(EndpointConstants.CLAIM_HISTORY);
        _getService().requestClaimHistory(url, _getToken()).enqueue(new CommonContainerListCallback<>(delegate));
    }

    public void requestWalletMyItem(final RestCallback<ContainerListModel<MyWalletModel>> delegate) {
        String url = UrlManager.shared.getServiceUrlV2(EndpointConstants.WALLET_MY_ITEM);
        _getService().requestWalletMyItem(url, _getToken()).enqueue(new CommonContainerListCallback<>(delegate));
    }

    public void requestWalletGift(final RestCallback<ContainerListModel<MyWalletModel>> delegate) {
        String url = UrlManager.shared.getServiceUrlV2(EndpointConstants.WALLET_GIFT);
        _getService().requestWalletGift(url, _getToken()).enqueue(new CommonContainerListCallback<>(delegate));
    }


    public void requestUserFeed(int page, final RestCallback<ContainerListModel<MyUserFeedModel>> delegate) {
        String url = UrlManager.shared.getServiceUrlV2(EndpointConstants.USER_FEED_MY_ENDPOINT);
        JsonObject object = new JsonObject();
        object.addProperty("page", page);
        object.addProperty("limit", 30);
        _getService().requestUserFeed(url, _getToken(), object).enqueue(new CommonContainerListCallback<>(delegate));
    }

    public void requestHistoryList(final RestCallback<ContainerListModel<MyWalletModel>> delegate) {
        String url = UrlManager.shared.getServiceUrlV2(EndpointConstants.WALLET_HISTORY);
        _getService().requestHistoryList(url, _getToken()).enqueue(new CommonContainerListCallback<>(delegate));
    }


    public void requestSendGift(JsonObject object, final RestCallback<ContainerModel<CommanMsgModel>> delegate) {
        String url = UrlManager.shared.getServiceUrlV2(EndpointConstants.WALLET_SEND_GIFT);
        _getService().requestSendGift(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestBrunchList(String venueId, String day, final RestCallback<ContainerListModel<BrunchListModel>> delegate) {
        String url = UrlManager.shared.getServiceUrlV2(EndpointConstants.BRUNCH_LIST);
        JsonObject object = new JsonObject();
        object.addProperty("venueId", venueId);
        object.addProperty("day", day);
        _getService().requestBrunchList(url, _getToken(), object).enqueue(new CommonContainerListCallback<>(delegate));
    }

    public void requestBrunchBySpecialOffer(String specialOfferId, final RestCallback<ContainerModel<SpecialOfferModel>> delegate) {
        String url = UrlManager.shared.getServiceUrlV2(EndpointConstants.BRUNCH_OFFER_LIST);
        JsonObject object = new JsonObject();
        object.addProperty("specialOfferId", specialOfferId);
        _getService().requestBrunchBySpecialOffer(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestUserStoryCreate(String mediaType, String duration, String thumbnail, String mediaUrl, final RestCallback<ContainerModel<StoryObjectModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.STORY_CREATE_BY_USER);
        JsonObject object = new JsonObject();
        object.addProperty("mediaType", mediaType);
        object.addProperty("duration", duration);
        object.addProperty("thumbnail", thumbnail);
        object.addProperty("mediaUrl", mediaUrl);
        _getService().requestStoryCreateByUser(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestFriendFeed(int page, int limit, String friendId, final RestCallback<ContainerListModel<MyUserFeedModel>> delegate) {
        String url = UrlManager.shared.getServiceUrlV2(EndpointConstants.USER_FEED_MY_FRIEND);
        JsonObject object = new JsonObject();
        object.addProperty("page", page);
        object.addProperty("limit", limit);
        object.addProperty("friendId", friendId);
        _getService().requestUserFeed(url, _getToken(), object).enqueue(new CommonContainerListCallback<>(delegate));
    }

    public void requestInviteFriend(JsonObject object, final RestCallback<ContainerModel<InviteFriendModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.INVITE_FRIEND);
        _getService().requestInviteFriend(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestMyOutingList(final RestCallback<ContainerListModel<InviteFriendModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.MY_OUTING_LIST);
        _getService().requestMyOutingList(url, _getToken()).enqueue(new CommonContainerListCallback<>(delegate));
    }

    public void requestUpdateInviteStatus(String outingId, String inviteStatus, final RestCallback<ContainerModel<InviteFriendModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.UPDATE_OUTING_INVITE_STATUS);
        JsonObject object = new JsonObject();
        object.addProperty("outingId", outingId);
        object.addProperty("inviteStatus", inviteStatus);
        _getService().requestUpdateInviteStatus(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestUpdateOuting(JsonObject object, final RestCallback<ContainerModel<InviteFriendModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.UPDATE_OUTING);
        _getService().requestUpdateOuting(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }


    public void requestNotificationList(int page, int limit, final RestCallback<ContainerModel<MainNotificationModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.USER_NOTIFICATION_LIST);
        JsonObject object = new JsonObject();
        object.addProperty("page", page);
        object.addProperty("limit", limit);
        _getService().requestUserNotificationList(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }


    public void requestPackageRedeem(JsonObject object, final RestCallback<ContainerModel<CommanMsgModel>> delegate) {
        String url = UrlManager.shared.getServiceUrlV2(EndpointConstants.PACKAGE_REDEEM);
        _getService().requestPackageRedeem(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestEvents(final RestCallback<ContainerListModel<BucketEventListModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.BUCKET_EVENTS);
        _getService().requestEvents(url, _getToken()).enqueue(new CommonContainerListCallback<>(delegate));
    }

    public void requestMyBucketList(final RestCallback<ContainerModel<BucketListModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.MY_BUCKET_LIST);
        _getService().requestMyBucketList(url, _getToken()).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestEventsHistory(final RestCallback<ContainerListModel<BucketEventListModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.EVENT_HISTORY_LIST);
        _getService().requestEventsHistory(url, _getToken()).enqueue(new CommonContainerListCallback<>(delegate));
    }

    public void requestUpcomingHistory(final RestCallback<ContainerListModel<BucketEventListModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.EVENT_UPCOMING_LIST);
        _getService().requestUpcomingHistory(url, _getToken()).enqueue(new CommonContainerListCallback<>(delegate));
    }

    public void requestDeleteChat(String chatId, final RestCallback<ContainerModel<ChatModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.DELETE_CHAT);
        JsonObject object = new JsonObject();
        object.addProperty("chatId", chatId);
        _getService().requestDeleteChat(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestFeedRecommandation(String id, String type, final RestCallback<ContainerModel<UserDetailModel>> delegate) {
        JsonObject object = new JsonObject();
        object.addProperty("id", id);
        object.addProperty("type", type);
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.USER_RECOMMENDATION);
        _getService().requestFeedRecommandation(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestLinkCreate(JsonObject jsonObject, final RestCallback<ContainerModel<String>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.LINK_CREATE);
        String modifiedUrl = url.replace("/v1", "");
        Log.d("URl", "requestLinkCreate: " + modifiedUrl);
        _getService().requestLinkCreate(modifiedUrl, _getToken(), jsonObject).enqueue(new CommonContainerDataCallback<>(delegate));
    }

    public void requestChangeOwnership(String newOwnerId, String outingId, final RestCallback<ContainerModel<InviteFriendModel>> delegate) {
        JsonObject object = new JsonObject();
        object.addProperty("newOwnerId", newOwnerId);
        object.addProperty("outingId", outingId);
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.CHANGE_OWNERSHIP);
        _getService().requestChangeOwnership(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestVenueOfferDetail(String offerId, final RestCallback<ContainerModel<OffersModel>> delegate) {
        JsonObject object = new JsonObject();
        object.addProperty("offerId", offerId);
        String url = UrlManager.shared.getServiceUrlV2(EndpointConstants.VENUE_OFFER_DETAIL);
        _getService().requestVenueOfferDetail(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestOutingDetail(String id, final RestCallback<ContainerModel<InviteFriendModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.OUTING_DETAIL);
        url = url + id;
        _getService().requestOutingDetail(url, _getToken()).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestContactAddQuery(JsonObject jsonObject, final RestCallback<ContainerModel<UserDetailModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.COMMAN_CONTACT_US_ADD_QUERY);
        _getService().requestContactAddQuery(url, _getToken(), jsonObject).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestContactQueryList(final RestCallback<ContainerListModel<UserDetailModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.COMMAN_CONTACT_US_QUERY_LIST);
        _getService().requestContactQueryList(url, _getToken()).enqueue(new CommonContainerListCallback<>(delegate));
    }

    public void requestContactQueryReply(String reply, String conctactUsId, final RestCallback<ContainerModel<ContactChatRepliesModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.COMMAN_CONTACT_US_QUERY_REPLY);
        JsonObject object = new JsonObject();
        object.addProperty("reply", reply);
        object.addProperty("conctactUsId", conctactUsId);
        _getService().requestContactAddQueryReply(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestUserBlockList(final RestCallback<ContainerListModel<UserDetailModel>> delegate) {
        String url = UrlManager.shared.getServiceUrlV2(EndpointConstants.USER_BLOCK_LIST);
        _getService().requestUserBlockList(url, _getToken()).enqueue(new CommonContainerListCallback<>(delegate));
    }


    public void requestUserBlockRemove(String blockId, final RestCallback<ContainerModel<CommonModel>> delegate) {
        JsonObject object = new JsonObject();
        object.addProperty("blockId", blockId);
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.USER_BLOCK_REMOVE);
        _getService().requestUserBlockRemove(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestUserDeleteAccount(String type, final RestCallback<ContainerModel<UserDetailModel>> delegate) {
        String url = UrlManager.shared.getServiceUrlV2(EndpointConstants.USER_DELETE_ACCOUNT);
        JsonObject object = new JsonObject();
        object.addProperty("type", type);
        _getService().requestUserDeleteAccount(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestUsersNotificationRead(String notificationId, List<String> id, final RestCallback<ContainerModel<CommanMsgModel>> delegate) {
        JsonObject object = new JsonObject();
        JsonArray jsonArray = new Gson().toJsonTree(id).getAsJsonArray();
        object.add("notificationIds", jsonArray);
        object.addProperty("notificationId", notificationId);
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.USER_NOTIFICATION_READ);
        _getService().requestUserNotificationRead(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestUserNotificationUnreadCount(final RestCallback<ContainerModel<MainNotificationModel>> delegate) {
        if (TextUtils.isEmpty(_getToken())) {
            delegate.result(null, "");
            return;
        }
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.USER_NOTIFICATION_UNREAD_COUNT);
        _getService().requestUserNotificationUnReadCount(url, _getToken()).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestUpdatesStatus(final RestCallback<ContainerModel<UpdateStatusModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.COMMAN_UPDATE_STATUS);
        _getService().requestUpdatesStatus(url, _getToken()).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestUpdatesRead(String type, final RestCallback<ContainerModel<CommanMsgModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.COMMAN_UPDATES_READS);
        JsonObject object = new JsonObject();
        object.addProperty("type", type);
        _getService().requestUpdatesRead(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestSearchGetHomeBlock(final RestCallback<ContainerModel<HomeObjectModel>> delegate) {
        String url = UrlManager.shared.getServiceUrlV2(EndpointConstants.SEARCH_GET_HOME_BLOCK);
        _getService().requestSearchGetHomeBlock(url, _getToken()).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestDeleteInvitation(String id, final RestCallback<ContainerModel<UserDetailModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.DELETE_OUTING_INVITATION);
        JsonObject object = new JsonObject();
        object.addProperty("inviteId", id);
        _getService().requestDeleteInvitation(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestOutingOwnerDelete(String outingId, final RestCallback<ContainerModel<InviteFriendModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.OUTING_OWNER_DELETE);
        JsonObject object = new JsonObject();
        object.addProperty("outingId", outingId);
        _getService().requestOutingOwnerDelete(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestSuggestedUser(String id, final RestCallback<ContainerListModel<UserDetailModel>> delegate) {
        JsonObject object = new JsonObject();
        object.addProperty("id", id);
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.SUGGESTED_USERS);
        _getService().requestSuggestedUser(url, _getToken(), object).enqueue(new CommonContainerListCallback<>(delegate));
    }

    public void requestSuggestedVenue(String id, final RestCallback<ContainerListModel<VenueObjectModel>> delegate) {
        String url = UrlManager.shared.getServiceUrlV2(EndpointConstants.SUGGESTED_VENUES);
        JsonObject object = new JsonObject();
        object.addProperty("id", id);
        object.addProperty("lat", LocationManager.shared.lat);
        object.addProperty("long", LocationManager.shared.lng);
        _getService().requestSuggestedVenues(url, _getToken(), object).enqueue(new CommonContainerListCallback<>(delegate));
    }

    public void requestUserRemoveSuggestion(String type, String typeId, final RestCallback<ContainerModel<CommonModel>> delegate) {
        JsonObject object = new JsonObject();
        object.addProperty("type", type);
        object.addProperty("typeId", typeId);
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.USER_REMOVE_SUGGESTION);
        _getService().requestUserRemoveSuggestion(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestUserUpdateSettings(JsonObject object, final RestCallback<ContainerModel<UserDetailModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.USER_UPDATE_SETTINGS);

        _getService().requestUserUpdateSettings(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestUserFollowList(final RestCallback<ContainerListModel<UserDetailModel>> delegate) {
        String url = UrlManager.shared.getServiceUrlV2(EndpointConstants.USER_FOLLOW_REQUEST_LIST);
        _getService().requestUserFollowList(url, _getToken()).enqueue(new CommonContainerListCallback<>(delegate));
    }

    public void requestUserFollowAction(String id, String status, final RestCallback<ContainerModel<CommonModel>> delegate) {
        JsonObject object = new JsonObject();
        object.addProperty("id", id);
        object.addProperty("status", status);
        String url = UrlManager.shared.getServiceUrlV2(EndpointConstants.USER_FOLLOW_REQUEST_ACTION);
        _getService().requestUserFollowAction(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestContactUsReplyRead(List<String> replyIds, final RestCallback<ContainerModel<CommanMsgModel>> delegate) {
        JsonObject object = new JsonObject();
        JsonArray jsonArray = new Gson().toJsonTree(replyIds).getAsJsonArray();
        object.add("replyIds", jsonArray);
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.CONTACT_US_REPLY_MARK_AS_READ);
        _getService().requestContactReplyRead(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestPurchaseMembership(String membershipPackageId, String amount, String promoCode, String currency, final RestCallback<ContainerModel<PaymentCredentialModel>> delegate) {
        JsonObject object = new JsonObject();
        object.addProperty("membershipPackageId", membershipPackageId);
        object.addProperty("amount", amount);
        object.addProperty("promoCode", promoCode);
        object.addProperty("currency", currency);
        String url = UrlManager.shared.getServiceUrlV2(EndpointConstants.MEMBERSHIP_PURCHASE);
        _getService().requestPurchaseMembership(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestMembershipPackageDetail(String packageId, final RestCallback<ContainerModel<MemberShipModel>> delegate) {
        JsonObject object = new JsonObject();
        object.addProperty("packageId", packageId);
        String url = UrlManager.shared.getServiceUrlV2(EndpointConstants.MEMBERSHIP_PACKAGE_DETAIL);
        _getService().requestMembershipPackageDetail(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestSubscriptionPromoCodeValidation(String promoCode, String amount, String type, String typeId, String apply, final RestCallback<ContainerModel<PromoCodeModel>> delegate) {
        JsonObject object = new JsonObject();
        object.addProperty("promoCode", promoCode);
        object.addProperty("amount", amount);
        object.addProperty("type", type);
        object.addProperty("typeId", typeId);
        object.addProperty("apply", apply);
        String url = UrlManager.shared.getServiceUrlV2(EndpointConstants.SUBSCRIPTION_PROMO_CODE_VALIDATION);
        _getService().requestSubscriptionPromoCodeValidation(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestUserAuthRequest(String reqId, final RestCallback<ContainerModel<LoginRequestModel>> delegate) {
        JsonObject object = new JsonObject();
        object.addProperty("reqId", reqId);
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.USER_AUTH_REQUEST);
        _getService().requestUserAuthRequest(url, object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestYachtDetail(String yachtId, final RestCallback<ContainerModel<YachtDetailModel>> delegate) {
        JsonObject object = new JsonObject();
        object.addProperty("yachtId", yachtId);
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.YACHT_DETAIL);
        _getService().requestYachtDetail(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestYachtClubDetail(String yachtClubId, final RestCallback<ContainerModel<YachtClubModel>> delegate) {
        JsonObject object = new JsonObject();
        object.addProperty("yachtClubId", yachtClubId);
        object.addProperty("lat", LocationManager.shared.lat);
        object.addProperty("long", LocationManager.shared.lng);

        String url = UrlManager.shared.getServiceUrl(EndpointConstants.YACHT_CLUB_DETAIL);
        _getService().requestYachtClubDetail(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestYachtOfferDetail(String offerId, final RestCallback<ContainerModel<YachtsOfferModel>> delegate) {
        JsonObject object = new JsonObject();
        object.addProperty("offerId", offerId);
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.YACHT_OFFER_DETAIL);
        _getService().requestYachtOfferDetail(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestYachtOfferPackageSlots(String packageId, final RestCallback<ContainerListModel<YachtAvailableSlotsModel>> delegate) {
        JsonObject object = new JsonObject();
        object.addProperty("packageId", packageId);
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.YACHT_OFFER_PACKAGE_AVAILABLE_SLOTS);
        _getService().requestYachtOfferPackageSlots(url, _getToken(), object).enqueue(new CommonContainerListCallback<>(delegate));
    }


    public void requestPromoterRequestCreate(JsonObject object, final RestCallback<ContainerModel<UserDetailModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.PROMOTER_REQUEST_CREATE);
        _getService().requestPromoterRequestCreate(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestPromoterRingRequestCreate(JsonObject object, final RestCallback<ContainerModel<UserDetailModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.PROMOTER_RING_REQUEST_CREATE);
        _getService().requestPromoterRequestCreate(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestPromoterRingUpdate(JsonObject object, final RestCallback<ContainerModel<UserDetailModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.PROMOTER_RING_UPDATE);
        _getService().requestPromoterRingUpdate(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestPromoterGetProfile(final RestCallback<ContainerModel<PromoterProfileModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.PROMOTER_GET_PROFILE);
        _getService().requestPromoterGetProfile(url, _getToken()).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestPromoterCreateCircle(JsonObject object, final RestCallback<ContainerModel<CreateBucketListModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.PROMOTER_CREATE_CIRCLE);
        _getService().requestPromoterCreateCircle(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestPromoterCircleDetail(String id, final RestCallback<ContainerModel<PromoterCirclesModel>> delegate) {
        JsonObject object = new JsonObject();
        object.addProperty("id", id);
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.PROMOTER_CIRCLE_DETAIL);
        _getService().requestPromoterCircleDetail(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestPromoterVenues(final RestCallback<ContainerListModel<VenueObjectModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.PROMOTER_VENUE_USER);
        _getService().requestPromoterVenues(url, _getToken()).enqueue(new CommonContainerListCallback<>(delegate));
    }

    public void requestPromoterMyRingMember(final RestCallback<ContainerListModel<UserDetailModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.PROMOTER_RING_MEMBER);
        _getService().requestPromoterMyRingMember(url, _getToken()).enqueue(new CommonContainerListCallback<>(delegate));
    }

    public void requestPromoterMyRingMemberForSubAdmin(String id, final RestCallback<ContainerListModel<UserDetailModel>> delegate) {
        JsonObject object = new JsonObject();
        object.addProperty("promoterId", id);
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.PROMOTER_SUBADMIN_RING_REQUEST);
        _getService().requestPromoterMyRingMemberForSubAdmin(url, _getToken(), object).enqueue(new CommonContainerListCallback<>(delegate));
    }

    public void requestPromoterMyRingRemoveMember(String id, final RestCallback<ContainerListModel<UserDetailModel>> delegate) {
        JsonObject object = new JsonObject();
        object.addProperty("memberId", id);
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.PROMOTER_RING_REMOVE_MEMBER);
        _getService().requestPromoterMyRingRemoveMember(url, _getToken(), object).enqueue(new CommonContainerListCallback<>(delegate));
    }

    public void requestPromoterUpdateCircle(JsonObject object, final RestCallback<ContainerModel<PromoterCirclesModel>> delegate) {

        String url = UrlManager.shared.getServiceUrl(EndpointConstants.PROMOTER_UPDATE_CIRCLE);
        _getService().requestPromoterUpdateCircle(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestPromoterDeleteCircle(String id, final RestCallback<ContainerModel<PromoterCirclesModel>> delegate) {
        JsonObject object = new JsonObject();
        object.addProperty("id", id);
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.PROMOTER_DELETE_CIRCLE);
        _getService().requestPromoterDeleteCircle(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestPromoterCircleAddMember(String id, List<String> memberIds, final RestCallback<ContainerModel<PromoterCirclesModel>> delegate) {
        JsonObject object = new JsonObject();
        JsonArray jsonArray = new Gson().toJsonTree(memberIds).getAsJsonArray();
        object.add("memberIds", jsonArray);
        object.addProperty("id", id);
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.PROMOTER_CIRCLE_ADD_MEMBER);
        _getService().requestPromoterCircleAddMember(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestPromoterCircleRemoveMember(JsonObject object, final RestCallback<ContainerModel<PromoterCirclesModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.PROMOTER_CIRCLE_REMOVE_MEMBER);
        _getService().requestPromoterCircleRemoveMember(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestPromoterAddToRing(String memberId, final RestCallback<ContainerModel<PromoterAddRingModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.PROMOTER_ADD_RING);
        JsonObject object = new JsonObject();
        object.addProperty("memberId", memberId);
        _getService().requestPromoterAddToRing(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestPromoterJoinMyRing(String memberId, final RestCallback<ContainerModel<PromoterAddRingModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.PROMOTER_JOIN_MY_Ring);
        JsonObject object = new JsonObject();
        object.addProperty("promoterId", memberId);
        _getService().requestPromoterJoinMyRing(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestPromoterLeaveMyRing(String memberId, final RestCallback<ContainerModel<PromoterAddRingModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.PROMOTER_LEAVE_RING_COMPLIMENTARY_USER);
        JsonObject object = new JsonObject();
        object.addProperty("promoterId", memberId);
        _getService().requestPromoterJoinMyRing(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestPromoterVenueRemove(String venueIds, final RestCallback<ContainerModel<PromoterVenueModel>> delegate) {
        JsonObject object = new JsonObject();
        object.addProperty("venueIds", venueIds);
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.PROMOTER_VENUE_REMOVE);
        _getService().requestPromoterVenueRemove(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));

    }

    public void requestPromoterInvitationCreate(JsonObject object, final RestCallback<ContainerModel<UserDetailModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.PROMOTER_INVITATION_CREATE);
        _getService().requestPromoterInvitationCreate(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestPromoterEventupdate(JsonObject object, final RestCallback<ContainerModel<PromoterEventModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.PROMOTER_EVENT_UPDATE);
        _getService().requestPromoterEventupdate(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestPromotereventDetail(String id, final RestCallback<ContainerModel<PromoterEventModel>> delegate) {
        JsonObject object = new JsonObject();
        object.addProperty("id", id);
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.PROMOTER_EVENT_DETAIL);
        _getService().requestPromotereventDetail(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));

    }

    public void requestPromotereventHideShow(String eventId, Boolean isHidden, final RestCallback<ContainerModel<PromoterEventModel>> delegate) {
        JsonObject object = new JsonObject();
        object.addProperty("eventId", eventId);
        object.addProperty("isHidden", isHidden);
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.PROMOTER_EVENT_HIDE_SHOW);
        _getService().requestPromotereventHideShow(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));

    }

    public Call<ContainerModel<PromoterNewEventListModel>> requestPromoterEventList(String serach , int pageNo, String sortBy, final RestCallback<ContainerModel<PromoterNewEventListModel>> delegate) {
        JsonObject object = new JsonObject();
        object.addProperty("page", pageNo);
        object.addProperty("limit", 20);
        object.addProperty("search", serach);
        object.addProperty("sortBy",sortBy);
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.PROMOTER_MY_EVENT_LIST_NEW);
        Call<ContainerModel<PromoterNewEventListModel>> service = _getService().requestPromoterEventList(url, _getToken(), object);
        service.enqueue(new CommonContainerCallback<>(delegate));
        return service;
    }


    public void requestPromoterEventCancel(String eventId,boolean deleteAllEvent ,final RestCallback<ContainerModel<PromoterEventModel>> delegate) {
        JsonObject object = new JsonObject();
        object.addProperty("eventId", eventId);
        object.addProperty("deleteAllEvent", deleteAllEvent);
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.PROMOTER_MY_EVENT_CANCEL);
        _getService().requestPromoterEventCancel(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestComplimentaryProfile(final RestCallback<ContainerModel<ComplimentaryProfileModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.PROMOTER_GET_COMPLIMENTARY_PROFILE);
        _getService().requestComplimentaryProfile(url, _getToken()).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestComplimentaryPublicProfile(String id, final RestCallback<ContainerModel<ComplimentaryProfileModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.PROMOTER_GET_COMPLIMENTARY_PROFILE);
        url = url + "/" + id;
        _getService().requestComplimentaryProfile(url, _getToken()).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestPromoterUserNotification(int page, final RestCallback<ContainerModel<MainNotificationModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.PROMOTER_USER_NOTIFICATION);
        JsonObject object = new JsonObject();
        object.addProperty("page", page);
        object.addProperty("limit", 20);
        Log.d( "Token", "requestPromoterUserNotification: " + SessionManager.shared.getToken());
        _getService().requestPromoterUserNotification(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestPromoterRingUpdateStatus(String memberId, String status, final RestCallback<ContainerModel<PromoterAddRingModel>> delegate) {
        JsonObject object = new JsonObject();
        object.addProperty("memberId", memberId);
        object.addProperty("status", status);
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.PROMOTER_RING_UPDATE_STATUS);
        _getService().requestPromoterRingUpdateStatus(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestPromoterRingUpdateBySubAdmin(String memberId, String status, final RestCallback<ContainerModel<PromoterAddRingModel>> delegate) {
        JsonObject object = new JsonObject();
        object.addProperty("requestId", memberId);
        object.addProperty("status", status);
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.PROMOTER_RING_REQUEST_VERIFY);
        _getService().requestPromoterRingUpdateBySubAdmin(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestPromoterRingUpdateMemberStatus(String memberId, String status, final RestCallback<ContainerModel<PromoterAddRingModel>> delegate) {
        JsonObject object = new JsonObject();
        object.addProperty("promoterId", memberId);
        object.addProperty("status", status);
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.PROMOTER_RING_UPDATE_MEMBER_STATUS);
        _getService().requestPromoterRingUpdateStatus(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestPromoterEventNotification(final RestCallback<ContainerModel<MainNotificationModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.PROMOTER_EVENT_NOTIFICATION);
        _getService().requestPromoterEventNotification(url, _getToken()).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestPromoterEventDetailUser(String id, final RestCallback<ContainerModel<PromoterEventModel>> delegate) {
        JsonObject object = new JsonObject();
        object.addProperty("eventId", id);
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.PROMOTER_EVENT_DETAIL_USER);
        _getService().requestPromotereventDetailUser(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));

    }

    public void requestGetProfile(String id, final RestCallback<ContainerModel<PromoterProfileModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.GET_PROFILE);
        url = url + id;
        _getService().requestGetProfile(url, _getToken()).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestPromoterEventListUser(final RestCallback<ContainerListModel<PromoterEventModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.PROMOTER_EVENT_LIST_USER);
        JsonObject object = new JsonObject();
        object.addProperty("latitude", LocationManager.shared.lat);
        object.addProperty("longitude", LocationManager.shared.lng);
        _getService().requestPromoterEventListUser(url, _getToken(), object).enqueue(new CommonContainerListCallback<>(delegate));
    }

    public void requestCmEventConfirmedLis(final RestCallback<ContainerListModel<PromoterEventModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.EVENT_CONFIRMED_LIST);
        JsonObject object = new JsonObject();
        object.addProperty("latitude", LocationManager.shared.lat);
        object.addProperty("longitude", LocationManager.shared.lng);
        _getService().requestPromoterEventListUser(url, _getToken(), object).enqueue(new CommonContainerListCallback<>(delegate));
    }

    public void requestPromoterToggleWishList(String typeId, String type, final RestCallback<ContainerModel<CommonModel>> delegate) {
        JsonObject object = new JsonObject();
        object.addProperty("typeId", typeId);
        object.addProperty("type", type);
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.PROMOTER_TOGGLE_WISHLIST);
        _getService().requestPromoterToggleWishList(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }


    public void requestPromoterUpdateInviteStatus(String inviteId, String inviteStatus, final RestCallback<ContainerModel<EventInOutPenaltyModel>> delegate) {
        JsonObject object = new JsonObject();
        object.addProperty("inviteId", inviteId);
        object.addProperty("inviteStatus", inviteStatus);
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.PROMOTER_UPDATE_INVITE_STATUS);
        _getService().requestPromoterUpdateInviteStatus(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestCmPromoterUpdate(JsonObject object, final RestCallback<ContainerModel<UserDetailModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.CM_PROMOTER_UPDATE);
        _getService().requestPromoterUpdate(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestCmEventNotification(final RestCallback<ContainerModel<MainNotificationModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.CM_EVENT_NOTIFICATION);
        _getService().requestCmEventNotification(url, _getToken()).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestCmUserNotification(final RestCallback<ContainerModel<MainNotificationModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.CM_USER_NOTIFICATION);
        _getService().requestCmUserNotification(url, _getToken()).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestPromoterMemberBan(JsonObject object, final RestCallback<ContainerModel<CommonModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.PROMOTER_MEMBER_BAN);
        _getService().requestPromoterMemberBan(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestReplayAddUpdateReview(JsonObject object, final RestCallback<ContainerModel<ReviewReplayModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.REPLAY_ADD_UPDATE_REVIEW);
        _getService().requestReplayAddUpdateReview(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestDeleteReview(String reviewId, final RestCallback<ContainerModel<ReviewReplayModel>> delegate) {
        JsonObject object = new JsonObject();
        object.addProperty("reviewId", reviewId);
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.DELETE_REVIEW);
        _getService().requestDeleteReview(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestChatPromoterContactList(boolean isPromoter, final RestCallback<ContainerListModel<PromoterChatModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(isPromoter ? EndpointConstants.PROMOTER_CHAT_CONTACT_LIST : EndpointConstants.COMPLIMENTARTY_CHAT_CONTACT_LIST);
        _getService().requestChatPromoterContactList(url, _getToken()).enqueue(new CommonContainerListCallback<>(delegate));
    }
    public Call<ContainerListModel<PromoterChatModel>> requestChatPromoterCmChat(boolean isPromoter, final RestCallback<ContainerListModel<PromoterChatModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(isPromoter ? EndpointConstants.PROMOTER_CHAT_CONTACT_LIST : EndpointConstants.COMPLIMENTARTY_CHAT_CONTACT_LIST);
        Call<ContainerListModel<PromoterChatModel>> service = _getService().requestChatPromoterContactList(url, _getToken());
        service.enqueue(new CommonContainerListCallback<>(delegate));
        return service;
    }

    public void requestPromoterInviteUpdateStatus(String inviteId, String promoterStatus, final RestCallback<ContainerModel<InvitedUserModel>> delegate) {
        JsonObject object = new JsonObject();
        object.addProperty("inviteId", inviteId);
        object.addProperty("promoterStatus", promoterStatus);
        Log.d("Updatestatus", "requestPromoterInviteUpdateStatus: " + object);
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.PROMOTER_INVITED_UPDATE_STATUS);
        _getService().requestPromoterInviteUpdateStatus(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestPromoterPlusOneInviteStatus(String inviteId, String promoterStatus, final RestCallback<ContainerModel<InvitedUserModel>> delegate) {
        JsonObject object = new JsonObject();
        object.addProperty("inviteId", inviteId);
        object.addProperty("promoterStatus", promoterStatus);
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.PROMOTER_PLUS_ONE_INVITED_STATUS);
        _getService().requestPromoterPlusOneInviteStatus(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestPromoterInviteList(int page ,String eventId, final RestCallback<ContainerModel<PromoterEventInviteModel>> delegate) {
        JsonObject object = new JsonObject();
        object.addProperty("page", page);
        object.addProperty("limit", 50);
        object.addProperty("eventId", eventId);
        Log.d("JsonObject", "requestPromoterInviteList: " + object);
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.PROMOTER_EVENT_INVITE_LIST);
        _getService().requestPromoterEventInvite(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public Call<ContainerListModel<PromoterEventModel>> requestPromoterEventHistory(String serach, int page, final RestCallback<ContainerListModel<PromoterEventModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.PROMOTER_EVENT_HISTORY);
        JsonObject object = new JsonObject();
        object.addProperty("page", page);
        object.addProperty("limit", 20);
        object.addProperty("search", serach);
        Call<ContainerListModel<PromoterEventModel>> service = _getService().requestPromoterEventHistory(url, _getToken(), object);
        service.enqueue(new CommonContainerListCallback<>(delegate));
        return service;
    }

    public void requestPromoterEventDelete(String id, final RestCallback<ContainerModel<PromoterEventModel>> delegate) {
        JsonObject object = new JsonObject();
        object.addProperty("id", id);
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.PROMOTER_EVENT_DELETE);
        _getService().requestPromoterEventDelete(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestPromoterEventComplete(String id, final RestCallback<ContainerModel<PromoterEventModel>> delegate) {
        JsonObject object = new JsonObject();
        object.addProperty("eventId", id);
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.PROMOTER_EVENT_COMPLETE);
        _getService().requestPromoterEventComplete(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestPromoterUserInEvent(final RestCallback<ContainerListModel<PromoterEventModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.PROMOTER_USER_IN_EVENT);
        _getService().requestPromoterUserInEvent(url, _getToken()).enqueue(new CommonContainerListCallback<>(delegate));
    }

    public void requestPromoterPlusOneInviteUser(String id, final RestCallback<ContainerModel<PromoterEventModel>> delegate) {
        JsonObject object = new JsonObject();
        object.addProperty("plusOneId", id);
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.PROMOTER_PLUS_ONE_INVITE_USER);
        _getService().requestPromoterPlusOneInviteUser(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestPromoterPlusOneInviteUserUpdateStatus(String id, String status, final RestCallback<ContainerModel<PromoterEventModel>> delegate) {
        JsonObject object = new JsonObject();
        object.addProperty("requstId", id);
        object.addProperty("status", status);
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.PROMOTER_PLUS_ONE_INVITE_USER_UPDATE_STATUS);
        _getService().requestPromoterPlusOneInviteUserUpdateStatus(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestPromoterPlusOneInviteUserRemove(String id, final RestCallback<ContainerModel<UserDetailModel>> delegate) {
        JsonObject object = new JsonObject();
        object.addProperty("plusOneId", id);
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.PROMOTER_PLUS_ONE_INVITE_USER_REMOVE);
        _getService().requestPromoterPlusOneInviteUserRemove(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestPromoterPlusOneMyGroup(final RestCallback<ContainerListModel<UserDetailModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.PROMOTER_PLUS_ONE_MY_GROUP);
        _getService().requestPromoterPlusOneMyGroup(url, _getToken()).enqueue(new CommonContainerListCallback<>(delegate));
    }

    public void requsetPromoterEventPlusOneInvite(String eventId, List<String> inviteIds, final RestCallback<ContainerModel<PromoterEventModel>> delegate) {
        JsonObject object = new JsonObject();
        object.addProperty("eventId", eventId);
        JsonArray jsonArray = new Gson().toJsonTree(inviteIds).getAsJsonArray();
        object.add("inviteIds", jsonArray);
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.PROMOTER_EVENT_PLUS_ONE_INVITE);
        _getService().requsetPromoterEventPlusOneInvite(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestPromoterPlusOneList(final RestCallback<ContainerListModel<PromoterEventModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.PROMOTER_EVENT_PLUS_ONE_LIST);
        _getService().requestPromoterPlusOneList(url, _getToken()).enqueue(new CommonContainerListCallback<>(delegate));
    }

    public void requestPromoterEventHistoryUser(int page,final RestCallback<ContainerListModel<PromoterEventModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.PROMOTER_EVENT_USER_HISTORY);
        JsonObject object = new JsonObject();
        object.addProperty("latitude", LocationManager.shared.lat);
        object.addProperty("longitude", LocationManager.shared.lng);
        object.addProperty("page", page);
        object.addProperty("limit", 10);
        _getService().requestPromoterEventHistoryUser(url, _getToken(), object).enqueue(new CommonContainerListCallback<>(delegate));
    }

    public void requestPromoterEventInviteUser(String eventId, final RestCallback<ContainerModel<PromoterInvitedUserModel>> delegate) {
        JsonObject object = new JsonObject();
        object.addProperty("eventId", eventId);
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.PROMOTER_EVENT_INVITE_USER);
        _getService().requestPromoterEventInviteUser(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestPromoterCloseSport(String eventId, final RestCallback<ContainerModel<PromoterEventModel>> delegate) {
        JsonObject object = new JsonObject();
        object.addProperty("eventId", eventId);
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.PROMOTER_EVENT_CLOSE_SPORT);
        _getService().requestPromoterCloseSport(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestPromoterEventGetCustomCategory(final RestCallback<ContainerModel<List<String>>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.PROMOTER_EVENT_GET_CUSTOM_CATEGORY);
        _getService().requestPromoterEventGetCustomCategory(url, _getToken()).enqueue(new CommonContainerDataCallback<>(delegate));
    }

    public void requestPromoterAddToCircle(List<String> circleIds , String memberId, final RestCallback<ContainerModel<PromoterEventModel>> delegate) {
        JsonObject object = new JsonObject();
        JsonArray jsonArray = new Gson().toJsonTree(circleIds).getAsJsonArray();
        object.add("circleIds", jsonArray);
        object.addProperty("memberId", memberId);
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.PROMOTER_ADD_MEMBER_TO_CIRCLES);
        _getService().requestPromoterAddToCircle(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestPromoterPlusOneGroupListUser(final RestCallback<ContainerListModel<UserDetailModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.PROMOTER_PLUS_ONE_GROUP_LIST_USER);
        _getService().requestPromoterPlusOneGroupListUser(url, _getToken()).enqueue(new CommonContainerListCallback<>(delegate));
    }

    public void requestPromoterPlusOneGroupLeave(String id, final RestCallback<ContainerModel<UserDetailModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.PROMOTER_PLUS_ONE_GROUP_LEAVE);
        JsonObject object = new JsonObject();
        object.addProperty("id", id);
        _getService().requestPromoterPlusOneGroupLeave(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestPromoterPlusOneEvenDetail(String id, final RestCallback<ContainerModel<PromoterEventModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.PROMOTER_PLUS_ONE_EVENT_DETAIL);
        JsonObject object = new JsonObject();
        object.addProperty("eventId", id);
        _getService().requestPromoterPlusOneEvenDetail(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestPromoterPlusOneEvenInviteStatus(String inviteId,String status, final RestCallback<ContainerModel<PromoterEventModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.PROMOTER_EVENT_PLUS_ONE_INVITE_STATUS);
        JsonObject object = new JsonObject();
        object.addProperty("inviteId", inviteId);
        object.addProperty("status", status);
        _getService().requestPromoterPlusOneEvenDetail(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestPromoterVenueSetFrequencyCmVisit(String venueId,int days, final RestCallback<ContainerModel<PromoterVenueModel>> delegate) {
        JsonObject object = new JsonObject();
        object.addProperty("venueId", venueId);
        object.addProperty("days", days);
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.PROMOTER_VENUE_SET_FREQUENCY_CM_VISIT);
        _getService().requestPromoterVenueSetFrequencyCmVisit(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestPromoterSubAdminList(final RestCallback<ContainerListModel<UserDetailModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.PROMOTER_SUB_ADMIN_LIST);
        _getService().requestPromoterSubAdminList(url, _getToken()).enqueue(new CommonContainerListCallback<>(delegate));
    }

    public void requestGetVenueMediaUrls(String venueId, final RestCallback<ContainerModel<List<String>>> delegate) {
        JsonObject object = new JsonObject();
        object.addProperty("venueId", venueId);
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.VENUE_GET_MEDIA_URLS);
        _getService().requestGetVenueMediaUrls(url, _getToken(), object).enqueue(new CommonContainerDataCallback<>(delegate));
    }

    public Call<ContainerListModel<ContactListModel>> requestUserSearchAll(String search, int page, final RestCallback<ContainerListModel<ContactListModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.USER_SEARCH_ALL);
        JsonObject object = new JsonObject();
        object.addProperty("search", search);
        object.addProperty("page", page);
        object.addProperty("limit", 100);
        Call<ContainerListModel<ContactListModel>> service = _getService().requestUserSearchAll(url, _getToken(), object);
        service.enqueue(new CommonContainerListCallback<>(delegate));
        return service;
    }

    public void requestPromoterPenaltyList(String cmUserId, int page, final RestCallback<ContainerModel<PromoterPenaltyModel>> delegate) {
        JsonObject object = new JsonObject();
        object.addProperty("cmUserId", cmUserId);
        object.addProperty("page", page);
        object.addProperty("limit", 10);
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.PROMOTER_PENALTY_LIST);
        _getService().requestPromoterPenaltyList(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestPromoterPenaltyRemove(String id, final RestCallback<ContainerModel<PenaltyListModel>> delegate) {
        JsonObject object = new JsonObject();
        object.addProperty("id", id);
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.PROMOTER_PENALTY_REMOVE);
        _getService().requestPromoterPenaltyRemove(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));

    }

    public void requestPromoterCirclesByUserId(String otherUserId, final RestCallback<ContainerListModel<PromoterCirclesModel>> delegate) {
        JsonObject object = new JsonObject();
        object.addProperty("otherUserId", otherUserId);
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.PROMOTER_CIRCLES_BY_USER_ID);
        _getService().requestPromoterCirclesByUserId(url, _getToken(), object).enqueue(new CommonContainerListCallback<>(delegate));

    }

    public void requestPromoterUpdateSubadminStatus(String requestId, String status, final RestCallback<ContainerModel<NotificationModel>> delegate) {
        JsonObject object = new JsonObject();
        object.addProperty("requestId", requestId);
        object.addProperty("status", status);
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.PROMOTER_UPDATE_SUBADMIN_STATUS);
        _getService().requestPromoterUpdateSubadminStatus(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestPromoterStripePayment(JsonObject object, final RestCallback<ContainerModel<PaymentCredentialModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.PROMOTER_PAYMENT_CREATE);
        _getService().requestStripePaymentIntent(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestPromoterPaidPassList(final RestCallback<ContainerListModel<PromoterPaidPassModel>> delegate) {
        JsonObject object = new JsonObject();
        object.addProperty("type","event");
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.PROMOTER_PAID_PASS_LIST);
        _getService().requestPromoterPaidPassList(url, _getToken(), object).enqueue(new CommonContainerListCallback<>(delegate));
    }

    public void requestPromoterPaidPassByEventId(String eventId,final RestCallback<ContainerModel<PromoterPaidPassModel>> delegate) {
        JsonObject object = new JsonObject();
        object.addProperty("eventId",eventId);
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.PROMOTER_PAID_PASS_BY_EVENTID);
        _getService().requestPromoterPaidPassByEventId(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestRaynaCustomUserDetail(String customTicketId, final RestCallback<ContainerModel<RaynaTicketDetailModel>> delegate) {
        JsonObject object = new JsonObject();
        object.addProperty("customTicketId", customTicketId);
        String url = UrlManager.shared.getServiceUrlV2(EndpointConstants.RAYNA_CUSTOM_USER_DETAIL);
        _getService().requestRaynaCustomUserDetail(url, _getToken() ,object).enqueue(new CommonContainerCallback<>(delegate));

    }

    public Call<ContainerListModel<RaynaTicketDetailModel>> requestRaynaSearch(String search, int page, String type,final RestCallback<ContainerListModel<RaynaTicketDetailModel>> delegate) {
        String url = UrlManager.shared.getServiceUrlV2(EndpointConstants.RAYNA_SEARCH);
        JsonObject object = new JsonObject();
        object.addProperty("search", search);
        object.addProperty("page", page);
        object.addProperty("limit", 10);
        object.addProperty("type", type);
        Call<ContainerListModel<RaynaTicketDetailModel>> service = _getService().requestRaynaSearch(url, _getToken(), object);
        service.enqueue(new CommonContainerListCallback<>(delegate));
        return service;
    }

    public void requestRaynaTourOptions(JsonObject object, final RestCallback<ContainerListModel<TourOptionsModel>> delegate) {
        String url = UrlManager.shared.getServiceUrlV2(EndpointConstants.RAYNA_TOUR_OPTIONS);
        _getService().requestRaynaTourOptions(url, _getToken(), object).enqueue(new CommonContainerListCallback<>(delegate));
    }

    public void requestRaynaTourOptionDetailByTourId(int countryId, int cityId, String tourId, int contractId, int date, final RestCallback<ContainerListModel<TourOptionDetailModel>> delegate) {
        JsonObject object = new JsonObject();
        object.addProperty("countryId", countryId);
        object.addProperty("cityId", cityId);
        object.addProperty("tourId", tourId);
        object.addProperty("contractId", contractId);
        object.addProperty("date", date);
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.RAYNA_TOUR_OPTIONS_DETAIL_BY_TOUR_ID);
        _getService().requestRaynaTourOptionDetailByTourId(url, _getToken(), object).enqueue(new CommonContainerListCallback<>(delegate));
    }

    public void requestRaynaTourTimeSlot(JsonObject object, final RestCallback<ContainerListModel<RaynaTimeSlotModel>> delegate) {
        String url = UrlManager.shared.getServiceUrlV2(EndpointConstants.RAYNA_TOUR_TIMESLOT);
        _getService().requestRaynaTourTimeSlot(url, _getToken(), object).enqueue(new CommonContainerListCallback<>(delegate));
    }

    public Call<ContainerListModel<RaynaTimeSlotModel>> requestRaynaTourTimeSlotForBottomSheet(JsonObject object, final RestCallback<ContainerListModel<RaynaTimeSlotModel>> delegate) {
        String url = UrlManager.shared.getServiceUrlV2(EndpointConstants.RAYNA_TOUR_TIMESLOT);
        Call<ContainerListModel<RaynaTimeSlotModel>> service = _getService().requestRaynaTourTimeSlot(url, _getToken(), object);
        service.enqueue(new CommonContainerListCallback<>(delegate));
        return service;
    }


    public void requestRaynaTourPolicy(JsonObject requestObject, final RestCallback<ContainerListModel<TourOptionsModel>> delegate) {
        String url = UrlManager.shared.getServiceUrlV2(EndpointConstants.RAYNA_TOUR_POLICY);
        _getService().requestRaynaTourPolicy(url, _getToken(), requestObject).enqueue(new CommonContainerListCallback<>(delegate));
    }

    public void requestRaynaTourBooking(JsonObject object, final RestCallback<ContainerModel<PaymentCredentialModel>> delegate) {
        String url = UrlManager.shared.getServiceUrlV2(EndpointConstants.RAYNA_TOUR_BOOKING);
        _getService().requestRaynaTourBooking(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestRaynaTourBookingCancel(String cancellationReason,int bookingId, String _id,final RestCallback<ContainerModel<RaynaTicketBookingModel>> delegate) {
        JsonObject object = new JsonObject();
        object.addProperty("cancellationReason", cancellationReason);
        object.addProperty("bookingId", bookingId);
        object.addProperty("_id", _id);
        String url = UrlManager.shared.getServiceUrlV2(EndpointConstants.RAYNA_TOUR_BOOKING_CANCEL);
        _getService().requestRaynaTourBookingCancel(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestRaynaWhosinTourBookingCancel(String bookingId, String _id,final RestCallback<ContainerModel<RaynaTicketBookingModel>> delegate) {
        JsonObject object = new JsonObject();
        object.addProperty("bookingId", bookingId);
        object.addProperty("_id", _id);
        String url = UrlManager.shared.getServiceUrlV2(EndpointConstants.RAYNA_WHOSIN_TOUR_BOOKING_CANCEL);
        _getService().requestRaynaTourBookingCancel(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestVenuePromoCode(JsonObject object ,final RestCallback<ContainerModel<VenuePromoCodeModel>> delegate) {
        String url = UrlManager.shared.getServiceUrlV2(EndpointConstants.PROMO_CODE_APPLY);
        _getService().requestVenuePromoCode(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestNewExplore(final RestCallback<ContainerModel<ExploreObjectModel>> delegate) {
        String url = UrlManager.shared.getServiceUrlV2(EndpointConstants.NEW_EXPLORE_BLOCK);
        _getService().requestNewExplore(url, _getToken()).enqueue(new CommonContainerCallback<>(delegate));
    }

    public Call<ContainerListModel<RaynaTicketDetailModel>> requestGetRaynaCustomTicketList(JsonObject object, final RestCallback<ContainerListModel<RaynaTicketDetailModel>> delegate) {
        String url = UrlManager.shared.getServiceUrlV2(EndpointConstants.RAYNA_CUSTOM_TICKET_LIST);
        Call<ContainerListModel<RaynaTicketDetailModel>> service = _getService().requestGetRaynaCustomTicketList(url, _getToken(), object);
        service.enqueue(new CommonContainerListCallback<>(delegate));
        return service;
    }

    public void requestCheckUserSession(final RestCallback<ContainerModel<UserDetailModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.USER_SESSION_CHECK);
        _getService().requestCheckUserSession(url, _getToken()).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestCmProfileTicketsBlock(final RestCallback<ContainerModel<ExploreObjectModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.CM_PROFILE_TICKETS_BLOCK);
        _getService().requestCmProfileTicketsBlock(url, _getToken()).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestUserReportAdd(JsonObject jsonObject,final RestCallback<ContainerModel<UserDetailModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.USER_REPORT_ADD);
        _getService().requestReportAdd(url, _getToken(),jsonObject).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestUserReportRemove(String id, final RestCallback<ContainerModel<UserDetailModel>> delegate) {
        JsonObject object = new JsonObject();
        object.addProperty("id", id);
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.USER_REPORT_REMOVE);
        _getService().requestReportAdd(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestReportList(final RestCallback<ContainerListModel<ReportUseListModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.USER_REPORT_LIST_USER);
        _getService().requestReportUserList(url, _getToken()).enqueue(new CommonContainerListCallback<>(delegate));
    }

    public void requestReportDetail(JsonObject jsonObject,final RestCallback<ContainerModel<ReportUseListModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.USER_REPORT_DETAIL);
        _getService().requestReportDetail(url, _getToken(),jsonObject).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestMyReviewList(final RestCallback<ContainerListModel<CurrentUserRatingModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.MY_REVIEW_LIST);
        _getService().requestMyReviewList(url, _getToken()).enqueue(new CommonContainerListCallback<>(delegate));
    }

    public void requestMyReviewDelete(JsonObject jsonObject,final RestCallback<ContainerModel<CurrentUserRatingModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.REVIEW_DELETE);
        _getService().requestMyReviewDelete(url, _getToken(),jsonObject).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestAdList(final RestCallback<ContainerListModel<AdListModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.AD_LIST);
        _getService().requestAdList(url, _getToken()).enqueue(new CommonContainerListCallback<>(delegate));
    }

    public void requestRaynaTicketFavorite(JsonObject jsonObject,final RestCallback<ContainerModel<AdListModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.HOMEBLOCK_FAVORITE_ADD_UPDATE);
        _getService().requestFavTicket(url, _getToken(),jsonObject).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestRaynaCheckReview(final RestCallback<ContainerModel<RaynaCheckReviewModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.RAYNA_CHECK_REVIEW);
        _getService().requestCheckReview(url, _getToken()).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestRaynaTicketUpdateReview(JsonObject jsonObject,final RestCallback<ContainerModel<RaynaCheckReviewModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.RAYNA_UPDATE_REVIEW_STATUS);
        _getService().requestCheckReview(url, _getToken(),jsonObject).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestUserNotificationUser(List<String> id, final RestCallback<ContainerModel<NotificationModel>> delegate) {
        JsonObject object = new JsonObject();
        JsonArray jsonArray = new Gson().toJsonTree(id).getAsJsonArray();
        object.add("ids", jsonArray);
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.USER_NOTIFICATION_USER);
        _getService().requestUserNotificationUser(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestDeleteSubscriptionOrder(List<String> id, final RestCallback<ContainerModel<NotificationModel>> delegate) {
        JsonObject object = new JsonObject();
        JsonArray jsonArray = new Gson().toJsonTree(id).getAsJsonArray();
        object.add("ids", jsonArray);
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.SUBSCRIPTION_ORDER);
        _getService().requestUserNotificationUser(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestInAppNotifiactionRead(String id, final RestCallback<ContainerModel<NotificationModel>> delegate) {
        JsonObject object = new JsonObject();
        object.addProperty("notificationId", id);
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.NOTIFICATION_IN_APP_READ);
        _getService().requestInAppNotificationRead(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestInAppListUser(int page, final RestCallback<ContainerModel<InAppListUserModel>> delegate) {
        JsonObject object = new JsonObject();
        object.addProperty("page", page);
        object.addProperty("limit", 150);
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.NOTIFICATION_IN_APP_LIST_USER);
        _getService().requestInAppListUser(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestWhosinTicketTourPolicy(JsonObject requestObject, final RestCallback<ContainerListModel<RaynaWhosinBookingRulesModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.RAYNA_WHOSIN_BOOKING_RULES);
        _getService().requestWhosinTicketTourPolicy(url, _getToken(), requestObject).enqueue(new CommonContainerListCallback<>(delegate));
    }

    public void requestTravelDeskTicketTourPolicy(JsonObject requestObject, final RestCallback<ContainerListModel<TravelDeskCancellationPolicyModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.TRAVELDESK_BOOKING_RULES);
        _getService().requestTravelTicketTourPolicy(url, _getToken(), requestObject).enqueue(new CommonContainerListCallback<>(delegate));
    }

    public void requestRaynaWhosinAvailability(JsonObject object, final RestCallback<ContainerListModel<TourOptionsModel>> delegate) {
        String url = UrlManager.shared.getServiceUrlV2(EndpointConstants.RAYNA_WHOSIN_AVAILABILITY);
        _getService().requestRaynaTourAvailability(url, _getToken(), object).enqueue(new CommonContainerListCallback<>(delegate));
    }

    public void requestWhosinAddOnAvailability(JsonObject object, final RestCallback<ContainerListModel<TourOptionsModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.WHOSIN_ADD_ON_AVAILABILITY);
        _getService().requestRaynaTourAvailability(url, _getToken(), object).enqueue(new CommonContainerListCallback<>(delegate));
    }

    public void requestRaynaMoreInfo(JsonObject object, final RestCallback<ContainerListModel<RaynaWhosinMoreInfoModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.RAYNA_MORE_INFO);
        _getService().requestRaynaMoreInfo(url, _getToken(), object).enqueue(new CommonContainerListCallback<>(delegate));
    }

    public void requestPromotionalBanner(final RestCallback<ContainerModel<PromotionalMainModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.HOMEBLOCK_BANNER_LIST);
        _getService().requestPromotionBanner(url, _getToken()).enqueue(new CommonContainerCallback<>(delegate));
    }


    public void requestJuniperAvailability(JsonObject object, final RestCallback<ContainerListModel<JuniperTourDataModel>> delegate) {
        String url = UrlManager.shared.getServiceUrlV2(EndpointConstants.JUNIPER_AVAILABILITY);
        RequestBody jsonPart = RequestBody.create(object.toString(), MediaType.parse("application/json"));
        _getService().requestJuniperAvailability(url, _getToken(), jsonPart).enqueue(new CommonContainerListCallback<>(delegate));
    }

    public void requestJuniperCheckAvailability(JsonObject object, final RestCallback<ContainerListModel<JuniperTourDataModel>> delegate) {
        String url = UrlManager.shared.getServiceUrlV2(EndpointConstants.JUNIPER_CHECK_AVAILABILITY);
        RequestBody jsonPart = RequestBody.create(object.toString(), MediaType.parse("application/json"));
        _getService().requestJuniperAvailability(url, _getToken(), jsonPart).enqueue(new CommonContainerListCallback<>(delegate));
    }

    public void requestJuniperBookingRules(JsonObject object, final RestCallback<ContainerListModel<JuniperTourDataModel>> delegate) {
        String url = UrlManager.shared.getServiceUrlV2(EndpointConstants.JUNIPER_BOOKING_RULES);
        RequestBody jsonPart = RequestBody.create(object.toString(), MediaType.parse("application/json"));
        _getService().requestJuniperAvailability(url, _getToken(), jsonPart).enqueue(new CommonContainerListCallback<>(delegate));
    }


    public Call<ContainerListModel<TravelDeskPickUpListModel>> requestTravelDeskPickUpList(JsonObject object, final RestCallback<ContainerListModel<TravelDeskPickUpListModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.TRAVEL_DESK_PICKUP_LIST);
        Call<ContainerListModel<TravelDeskPickUpListModel>> service = _getService().requestTravelDeskPickUpList(url, _getToken(), object);
        service.enqueue(new CommonContainerListCallback<>(delegate));
        return service;
    }

    public Call<ContainerListModel<TravelDeskTourAvailabilityModel>> requestTravelDeskTourAvailability(JsonObject object, final RestCallback<ContainerListModel<TravelDeskTourAvailabilityModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.TRAVEL_DESK_TOUR_AVAILABILITY);
        Call<ContainerListModel<TravelDeskTourAvailabilityModel>> service =  _getService().requestTravelDeskTourAvailability(url, _getToken(), object);
        service.enqueue(new CommonContainerListCallback<>(delegate));
        return service;
    }

    public void requestTravleDeskBookingCancel(String bookingId, String _id, String bookingType, final RestCallback<ContainerModel<RaynaTicketBookingModel>> delegate) {
        JsonObject object = new JsonObject();
        object.addProperty("bookingId", bookingId);
        object.addProperty("_id", _id);
        object.addProperty("bookingType", bookingType);
        String url = UrlManager.shared.getServiceUrlV2(EndpointConstants.TRAVEL_DESK_BOOKING_CANCEL);
        _getService().requestRaynaTourBookingCancel(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestOctaBookingCancel(JsonObject jsonObject, final RestCallback<ContainerModel<RaynaTicketBookingModel>> delegate) {
        String url = UrlManager.shared.getServiceUrlV2(EndpointConstants.TRAVEL_DESK_BOOKING_CANCEL);
        _getService().requestRaynaTourBookingCancel(url, _getToken(), jsonObject).enqueue(new CommonContainerCallback<>(delegate));
    }


    public void requestCartRemove(JsonObject jsonObject, final RestCallback<ContainerModel<RaynaTicketBookingModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.REMOVE_TO_CART);
        _getService().requestRaynaTourBookingCancel(url, _getToken(), jsonObject).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestCartAdd(JsonObject jsonObject, final RestCallback<ContainerModel<RaynaTicketBookingModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.ADD_TO_CART);
        _getService().requestRaynaTourBookingCancel(url, _getToken(), jsonObject).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestMyCartList(final RestCallback<ContainerModel<MyCartMainModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.CART_VIEW);
        _getService().requestMyCart(url, _getToken()).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestCartBooking(JsonObject object, final RestCallback<ContainerModel<PaymentCredentialModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.CART_BOOKING);
        _getService().requestRaynaTourBooking(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestCartOptionRemove(JsonObject jsonObject, final RestCallback<ContainerModel<RaynaTicketBookingModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.CART_REMOVE_OPTION);
        _getService().requestRaynaTourBookingCancel(url, _getToken(), jsonObject).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestCartOptionUpdate(JsonObject jsonObject, final RestCallback<ContainerModel<RaynaTicketBookingModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.CART_UPDATE);
        _getService().requestRaynaTourBookingCancel(url, _getToken(), jsonObject).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestRemoveCartSubscription(final RestCallback<ContainerModel<MyCartMainModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.SUBSCRIPTION_CART_REMOVE_PROMO);
        _getService().requestMyCart(url, _getToken()).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestAddToGoogleWallet(String bookingId,final RestCallback<ContainerModel<List<String>>> delegate) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("bookingId",bookingId);
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.SUBSCRIPTION_BOOKING_GOOGLE_WALLET);
        _getService().requestAddToGoogleWallet(url, _getToken(),jsonObject).enqueue(new CommonContainerDataCallback<>(delegate));
    }

    public Call<ContainerModel<List<String>>> requestRaynaSearchSuggestions(String search,final RestCallback<ContainerModel<List<String>>> delegate) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("search",search);
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.RAYNA_SEARCH_SUGGESTIONS);
        Call<ContainerModel<List<String>>> service =  _getService().requestAddToGoogleWallet(url, _getToken(),jsonObject);
        service.enqueue(new CommonContainerDataCallback<>(delegate));
        return service;
    }

    public Call<ContainerListModel<RaynaTimeSlotModel>> requestWhosinTicketTourTimeSlotForBottomSheet(JsonObject object, final RestCallback<ContainerListModel<RaynaTimeSlotModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.WHOSIN_TICKET_SLOTS);
        Call<ContainerListModel<RaynaTimeSlotModel>> service = _getService().requestRaynaTourTimeSlot(url, _getToken(), object);
        service.enqueue(new CommonContainerListCallback<>(delegate));
        return service;
    }

    public void requestWhosinCustomTicketTourPolicy(JsonObject requestObject, final RestCallback<ContainerListModel<RaynaWhosinBookingRulesModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.WHOSIN_TICKET_BOOKING_RULES);
        _getService().requestWhosinTicketTourPolicy(url, _getToken(), requestObject).enqueue(new CommonContainerListCallback<>(delegate));
    }

    public void requestRaynaWhosinCustomTourBookingCancel(JsonObject jsonObject, final RestCallback<ContainerModel<RaynaTicketBookingModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.WHOSIN_TICKET_TOUR_BOOKING_CANCEL);
        _getService().requestRaynaTourBookingCancel(url, _getToken(), jsonObject).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestCheckWhosinAvailability(JsonObject jsonObject, final RestCallback<ContainerModel<WhosinAvailabilityModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.WHOSIN_TICKET_AVAILABILITY);
        _getService().requestCheckAvailability(url, _getToken(), jsonObject).enqueue(new CommonContainerCallback<>(delegate));
    }

    public Call<ContainerListModel<OctaTourAvailabilityModel>> requestOctoTourAvailability(JsonObject jsonObject, final RestCallback<ContainerListModel<OctaTourAvailabilityModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.OCTO_TOUR_AVILABILITY);
        Call<ContainerListModel<OctaTourAvailabilityModel>> service = _getService().requestOctaTourAvailability(url, _getToken(), jsonObject);
        service.enqueue(new CommonContainerListCallback<>(delegate));
        return service;
    }

    public void requestOctaCancellationPolicy(JsonObject jsonObject, final RestCallback<ContainerModel<String>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.OCTO_TOUR_POLICY);
        _getService().requestLinkCreate(url, _getToken(), jsonObject).enqueue(new CommonContainerDataCallback<>(delegate));
    }


    public void requestCommanLang(final RestCallback<ContainerModel<Map<String, Map<String, String>>>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.COMMAN_LANGUAGE_FILE);
        _getService().requestCommanLang(url, _getToken())
                .enqueue(new Callback<>() {
                    @Override
                    public void onResponse(Call<ContainerModel<Map<String, Map<String, String>>>> call,
                                           Response<ContainerModel<Map<String, Map<String, String>>>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            delegate.result(response.body(), "");
                        } else {
                            delegate.result(null, "Request failed: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<ContainerModel<Map<String, Map<String, String>>>> call, Throwable t) {
                        AppExecutors.get().mainThread().execute(() -> {
                            if (delegate != null && !call.isCanceled()) {
                                delegate.setThrowable(t);
                                delegate.result(null, t.getMessage());
                            }
                        });
                    }
                });
    }

    public void requestJpHotelAvailability(JsonObject jsonObject, final RestCallback<ContainerModel<JPHotelTourAvailabilityModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.HOTEL_AVAILABILITY);
        _getService().requestJpHotelAvailability(url, _getToken(), jsonObject).enqueue(new CommonContainerDataCallback<>(delegate));
    }

    public void requestJpHotelBookingRule(JsonObject jsonObject, final RestCallback<ContainerModel<JpHotelBookingRuleModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.HOTEL_BOOKING_RULES);
        _getService().requestJpHotelBookingRule(url, _getToken(), jsonObject).enqueue(new CommonContainerDataCallback<>(delegate));
    }

    public void requestSuggestedTicket(String id, final RestCallback<ContainerListModel<HomeTicketsModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.TICKET_SUGGESTIONS);
        JsonObject object = new JsonObject();
        object.addProperty("ticketId", id);
        _getService().requestSuggestedTicketList(url, _getToken(), object).enqueue(new CommonContainerListCallback<>(delegate));
    }

    // endregion
    // --------------------------------------
}
