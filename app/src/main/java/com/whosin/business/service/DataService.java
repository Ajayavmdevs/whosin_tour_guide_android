package com.whosin.business.service;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.whosin.business.R;
import com.whosin.business.comman.AppConstants;
import com.whosin.business.comman.AppExecutors;
import com.whosin.business.comman.Preferences;
import com.whosin.business.comman.Utils;
import com.whosin.business.service.manager.SessionManager;
import com.whosin.business.service.manager.UrlManager;
import com.whosin.business.service.models.AdListModel;
import com.whosin.business.service.models.AppSettingModel;
import com.whosin.business.service.models.BigBusModels.OctaTourAvailabilityModel;
import com.whosin.business.service.models.ChatMessageModel;
import com.whosin.business.service.models.ChatModel;
import com.whosin.business.service.models.CommanMsgModel;
import com.whosin.business.service.models.CommanSearchModel;
import com.whosin.business.service.models.CommonModel;
import com.whosin.business.service.models.ContactChatRepliesModel;
import com.whosin.business.service.models.ContactListModel;
import com.whosin.business.service.models.ContainerListModel;
import com.whosin.business.service.models.ContainerModel;
import com.whosin.business.service.models.CurrentUserRatingModel;
import com.whosin.business.service.models.EventChatListModel;
import com.whosin.business.service.models.FollowUnfollowModel;
import com.whosin.business.service.models.GetPrefrenceModel;
import com.whosin.business.service.models.HomeObjectModel;
import com.whosin.business.service.models.HomeTicketsModel;
import com.whosin.business.service.models.ImageUploadModel;
import com.whosin.business.service.models.InAppListUserModel;
import com.whosin.business.service.models.JuniperHotelModels.JPHotelTourAvailabilityModel;
import com.whosin.business.service.models.JuniperHotelModels.JpHotelBookingRuleModel;
import com.whosin.business.service.models.LoginRequestModel;
import com.whosin.business.service.models.MainNotificationModel;
import com.whosin.business.service.models.ModelProtocol;
import com.whosin.business.service.models.MyUserFeedModel;
import com.whosin.business.service.models.MyWalletModel;
import com.whosin.business.service.models.NotificationModel;
import com.whosin.business.service.models.PaymentCredentialModel;
import com.whosin.business.service.models.ReportUseListModel;
import com.whosin.business.service.models.ReviewModel;
import com.whosin.business.service.models.ReviewReplayModel;
import com.whosin.business.service.models.TotalRatingModel;
import com.whosin.business.service.models.TravelDeskModels.TravelDeskCancellationPolicyModel;
import com.whosin.business.service.models.TravelDeskModels.TravelDeskPickUpListModel;
import com.whosin.business.service.models.TravelDeskModels.TravelDeskTourAvailabilityModel;
import com.whosin.business.service.models.UpdateStatusModel;
import com.whosin.business.service.models.UserDetailModel;
import com.whosin.business.service.models.UserTokenModel;
import com.whosin.business.service.models.VenuePromoCodeModel;
import com.whosin.business.service.models.bankDetails.UserBankDetailModel;
import com.whosin.business.service.models.myCartModels.MyCartMainModel;
import com.whosin.business.service.models.newExploreModels.ExploreObjectModel;
import com.whosin.business.service.models.rayna.RaynaCheckReviewModel;
import com.whosin.business.service.models.rayna.RaynaTicketBookingModel;
import com.whosin.business.service.models.rayna.RaynaTicketDetailModel;
import com.whosin.business.service.models.rayna.RaynaTimeSlotModel;
import com.whosin.business.service.models.rayna.TourOptionsModel;
import com.whosin.business.service.models.statistics.StatisticsModel;
import com.whosin.business.service.models.statistics.TransactionModel;
import com.whosin.business.service.models.whosinTicketModel.RaynaWhosinBookingRulesModel;
import com.whosin.business.service.models.whosinTicketModel.RaynaWhosinMoreInfoModel;
import com.whosin.business.service.models.whosinTicketModel.WhosinAvailabilityModel;
import com.whosin.business.service.rest.HttpCommon;
import com.whosin.business.service.rest.RestCallback;
import com.whosin.business.service.rest.RestClient;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;
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

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<UserDetailModel>> requestUpdateProfile(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<UserDetailModel>> requestUserByIds(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @GET()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<AppSettingModel>> requestAppSetting(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token);

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
        Call<ContainerListModel<ContactListModel>> requestContactList(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<ReviewModel>> requestRatingList(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<TotalRatingModel>> requestRatingSummary(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

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
        Call<ContainerModel<CommonModel>> requestBlockUserAdd(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

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

        @GET()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<UserDetailModel>> requestUserProfile(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<MyWalletModel>> requestWalletMyItem(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<MyUserFeedModel>> requestUserFeed(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<MyWalletModel>> requestHistoryList(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<MainNotificationModel>> requestUserNotificationList(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<ChatModel>> requestDeleteChat(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<String>> requestLinkCreate(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

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

        @GET()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<UpdateStatusModel>> requestUpdatesStatus(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<CommanMsgModel>> requestUpdatesRead(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<HomeObjectModel>> requestSearchGetHomeBlock(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token);

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
        Call<ContainerModel<LoginRequestModel>> requestUserAuthRequest(@Url String url, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<ReviewReplayModel>> requestReplayAddUpdateReview(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @HTTP(method = "DELETE", hasBody = true)
        @Headers({"Accept: application/json"})
        Call<ContainerModel<ReviewReplayModel>> requestDeleteReview(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

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


        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<RaynaWhosinMoreInfoModel>> requestRaynaMoreInfo(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

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

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<UserBankDetailModel>> requestUpdateBankDetails(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<UserBankDetailModel>> requestGetBankDetails(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<StatisticsModel>> requestGetStatistics(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<TransactionModel>> requestGetTransactions(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<TransactionModel>> requestAddMarkup(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerListModel<RaynaTicketDetailModel>> requestMarkupList(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);

        @POST()
        @Headers({"Accept: application/json"})
        Call<ContainerModel<TransactionModel>> requestRemoveMarkup(@Url String url, @Header(HttpCommon.HTTPRequestHeaderAuthorization) String token, @Body JsonObject bodyRequest);
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

    public void requestLoginWithGoogle(JsonObject object, final RestCallback<ContainerModel<UserTokenModel>> delegate) {
        object.addProperty("deviceId", Utils.getDeviceUniqueId(_context));
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.AUTH_GOOGLE_LOGIN_ENDPOINT);
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

    public void requestGetContact(List<String> number, List<String> email, final RestCallback<ContainerListModel<ContactListModel>> delegate) {
        JsonObject object = new JsonObject();
        JsonElement numbers = new Gson().toJsonTree(number);
        JsonElement emails = new Gson().toJsonTree(email);
        object.add("email", emails);
        object.add("phone", numbers);
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.CONTACT_LIST);
        _getService().requestContactList(url, _getToken(), object).enqueue(new CommonContainerListCallback<>(delegate));
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

    public void requestBlockUser(String blockId, final RestCallback<ContainerModel<CommonModel>> delegate) {
        String url = UrlManager.shared.getServiceUrlV2(EndpointConstants.BLOCK_USER_ADD);
        JsonObject object = new JsonObject();
        object.addProperty("blockId", blockId);
        _getService().requestBlockUserAdd(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
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

    public void requestUserProfile(String userId, final RestCallback<ContainerModel<UserDetailModel>> delegate) {
        String url = UrlManager.shared.getServiceUrlV2(EndpointConstants.USER_PROFILE);
        url = url + userId;
        Log.d("CurrentUserProfile", "requestUserProfile: " + url);
        _getService().requestUserProfile(url, _getToken()).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestWalletMyItem(final RestCallback<ContainerListModel<MyWalletModel>> delegate) {
        String url = UrlManager.shared.getServiceUrlV2(EndpointConstants.WALLET_MY_ITEM);
        _getService().requestWalletMyItem(url, _getToken()).enqueue(new CommonContainerListCallback<>(delegate));
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

    public void requestFriendFeed(int page, int limit, String friendId, final RestCallback<ContainerListModel<MyUserFeedModel>> delegate) {
        String url = UrlManager.shared.getServiceUrlV2(EndpointConstants.USER_FEED_MY_FRIEND);
        JsonObject object = new JsonObject();
        object.addProperty("page", page);
        object.addProperty("limit", limit);
        object.addProperty("friendId", friendId);
        _getService().requestUserFeed(url, _getToken(), object).enqueue(new CommonContainerListCallback<>(delegate));
    }

    public void requestNotificationList(int page, int limit, final RestCallback<ContainerModel<MainNotificationModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.USER_NOTIFICATION_LIST);
        JsonObject object = new JsonObject();
        object.addProperty("page", page);
        object.addProperty("limit", limit);
        _getService().requestUserNotificationList(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestDeleteChat(String chatId, final RestCallback<ContainerModel<ChatModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.DELETE_CHAT);
        JsonObject object = new JsonObject();
        object.addProperty("chatId", chatId);
        _getService().requestDeleteChat(url, _getToken(), object).enqueue(new CommonContainerCallback<>(delegate));
    }

    public void requestLinkCreate(JsonObject jsonObject, final RestCallback<ContainerModel<String>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.LINK_CREATE);
        String modifiedUrl = url.replace("/v1", "");
        Log.d("URl", "requestLinkCreate: " + modifiedUrl);
        _getService().requestLinkCreate(modifiedUrl, _getToken(), jsonObject).enqueue(new CommonContainerDataCallback<>(delegate));
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

    public void requestUserAuthRequest(String reqId, final RestCallback<ContainerModel<LoginRequestModel>> delegate) {
        JsonObject object = new JsonObject();
        object.addProperty("reqId", reqId);
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.USER_AUTH_REQUEST);
        _getService().requestUserAuthRequest(url, object).enqueue(new CommonContainerCallback<>(delegate));
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

    public void requestGetBankDetails(JsonObject jsonObject, final RestCallback<ContainerModel<UserBankDetailModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.GET_BANK_DETAILS);
        _getService().requestGetBankDetails(url, _getToken(), jsonObject).enqueue(new CommonContainerDataCallback<>(delegate));
    }

    public void requestUpdateBankDetails(JsonObject jsonObject, final RestCallback<ContainerModel<UserBankDetailModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.UPDATE_BANK_DETAILS);
        _getService().requestUpdateBankDetails(url, _getToken(), jsonObject).enqueue(new CommonContainerDataCallback<>(delegate));
    }

    public void requestGetStatistics(JsonObject jsonObject, final RestCallback<ContainerModel<StatisticsModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.TOUR_GUIDE_STATS);
        _getService().requestGetStatistics(url, _getToken(), jsonObject).enqueue(new CommonContainerDataCallback<>(delegate));
    }

    public void requestGetTransactions(JsonObject jsonObject, final RestCallback<ContainerModel<TransactionModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.TOUR_GUIDE_TRANSACTIONS);
        _getService().requestGetTransactions(url, _getToken(), jsonObject).enqueue(new CommonContainerDataCallback<>(delegate));
    }

    public void requestAddMarkup(JsonObject jsonObject, final RestCallback<ContainerModel<TransactionModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.ADD_MARKUP);
        _getService().requestAddMarkup(url, _getToken(), jsonObject).enqueue(new CommonContainerDataCallback<>(delegate));
    }

    public void requestMarkupList(JsonObject jsonObject, final RestCallback<ContainerListModel<RaynaTicketDetailModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.MARKUP_LIST);
        _getService().requestMarkupList(url, _getToken(), jsonObject).enqueue(new CommonContainerListCallback<>(delegate));
    }

    public void requestRemoveMarkup(JsonObject jsonObject, final RestCallback<ContainerModel<TransactionModel>> delegate) {
        String url = UrlManager.shared.getServiceUrl(EndpointConstants.REMOVE_MARKUP);
        _getService().requestRemoveMarkup(url, _getToken(), jsonObject).enqueue(new CommonContainerDataCallback<>(delegate));
    }

    // endregion
    // --------------------------------------
}
