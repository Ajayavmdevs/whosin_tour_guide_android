package com.whosin.app.ui.activites.Promoter;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;


import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;
import com.tapadoo.alerter.Alerter;
import com.whosin.app.R;
import com.whosin.app.comman.AppConstants;
import com.whosin.app.comman.AppExecutors;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Preferences;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.ActivityComplementaryEventDetailBinding;
import com.whosin.app.databinding.ItemEventImageDesignBinding;
import com.whosin.app.databinding.ItemEventVideoDesignBinding;
import com.whosin.app.databinding.ItemRequirementBenefitBinding;
import com.whosin.app.databinding.ItemSocialAccountAddWithTitleBinding;
import com.whosin.app.databinding.ItemSpaceDesignBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.PromoterProfileManager;
import com.whosin.app.service.models.ChatModel;
import com.whosin.app.service.models.CommonModel;
import com.whosin.app.service.models.ComplimentaryProfileModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.EventInOutPenaltyModel;
import com.whosin.app.service.models.InvitedUserModel;
import com.whosin.app.service.models.NotificationModel;
import com.whosin.app.service.models.PaymentCredentialModel;
import com.whosin.app.service.models.PromoterEventModel;
import com.whosin.app.service.models.PromoterPaidPassModel;
import com.whosin.app.service.models.RatingModel;
import com.whosin.app.service.models.SocialAccountsToMentionModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.CmProfile.EventPdfDownloadActivity;
import com.whosin.app.ui.activites.comman.BaseActivity;
import com.whosin.app.ui.activites.home.Chat.ChatMessageActivity;
import com.whosin.app.ui.activites.raynaTicket.BottomSheets.SelectPaymentOptionBottomSheet;
import com.whosin.app.ui.activites.raynaTicket.BottomSheets.TabbyPaymentBottomSheet;
import com.whosin.app.ui.activites.venue.VenueShareActivity;
import com.whosin.app.ui.adapter.PlusOneMemberListAdapter;
import com.whosin.app.ui.fragment.CmProfile.CmBottomSheets.AddPlusOneGuestBottomSheet;
import com.whosin.app.ui.fragment.CmProfile.CmBottomSheets.EventFAQDialog;
import com.whosin.app.ui.fragment.CmProfile.CmBottomSheets.FAQBottomSheet;
import com.whosin.app.ui.fragment.Promoter.PromoterBootmSheets.CancellationPenaltyDialog;
import com.whosin.app.ui.fragment.Promoter.PromoterBootmSheets.CustomMultiOptionAlertVC;
import com.whosin.app.ui.fragment.Promoter.PromoterBootmSheets.PaidPassPopupVCdialog;
import com.whosin.app.ui.fragment.wallet.PurchaseSuccessFragment;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ComplementaryEventDetailActivity extends BaseActivity {

    private ActivityComplementaryEventDetailBinding binding;

    private final RequireMentsAdapter<RatingModel> requirementsAdapter = new RequireMentsAdapter<>();

    private final RequireMentsAdapter<RatingModel> benefitsAdapter = new RequireMentsAdapter<>();

    private final SocialItemListAdapter<SocialAccountsToMentionModel> socialItemListAdapter = new SocialItemListAdapter<>();

    private final EventGalleryAdapter<RatingModel> eventGalleryAdapter = new EventGalleryAdapter<>();

    private PlusOneMemberListAdapter<InvitedUserModel> plusMemberAdapter;

    private PromoterEventModel promoterEventModel = null;

    private String eventId = "";

    private String type = "";

    private boolean isFromHistory = false;

    private boolean isReloadEventListApi = false;

    private PaymentSheet paymentSheet;

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    protected void initUi() {

        applyTranslations();

        binding.eventGalleryRecyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        binding.eventGalleryRecyclerView.setAdapter(eventGalleryAdapter);
        PagerSnapHelper snapHelper = new PagerSnapHelper ();
        snapHelper.attachToRecyclerView(binding.eventGalleryRecyclerView);

        binding.requirementRecycler.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        binding.requirementRecycler.setAdapter(requirementsAdapter);

        binding.benefitRecycler.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        binding.benefitRecycler.setAdapter(benefitsAdapter);

        binding.socialListRecycleView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        binding.socialListRecycleView.setAdapter(socialItemListAdapter);


        Graphics.applyBlurEffect(activity, binding.layoutTimer);

        eventId = getIntent().getStringExtra("eventId");
        type = getIntent().getStringExtra("type");
        isFromHistory = getIntent().getBooleanExtra("isFromHistory", false);

        paymentSheet = new PaymentSheet(ComplementaryEventDetailActivity.this, paymentSheetResult -> {
            if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
                Log.d("TAG", "Canceled");
            } else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
                Log.e("TAG", "Got error: ", ((PaymentSheetResult.Failed) paymentSheetResult).getError());
            } else if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
                requestPromoterUpdateInviteStatus(promoterEventModel.getInvite().getId(), "in");
            }
        });


        if (!Utils.isNullOrEmpty(eventId)) {
            assert type != null;
            if (type.equals("Promoter")) {
                requestPromoterEventDetail(eventId, true);
                Log.d("ID", "Ids: " + eventId);
                binding.linearBtn.setVisibility(View.GONE);
            } else if (type.equals("PlusOneEvent")) {
                requestPlusOnePromoterEventDetail(eventId, true);
            } else {
                requestPromoterEventDetailUser(eventId, true);
            }
        }



    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void setListeners() {

        binding.ivClose.setOnClickListener(view -> onBackPressed());

        binding.btnEditEvent.setOnClickListener(v -> {
            Utils.preventDoubleClick(v);
            binding.btnEditEvent.startProgress();
            PromoterProfileManager.shared.isEventEdit = true;
            Intent intent = new Intent(activity, PromoterCreateEventActivity.class);
            activityLauncher.launch(intent, result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    boolean isReload = result.getData().getBooleanExtra("isReload", false);
                    if (isReload) {
                        requestPromoterEventDetail(eventId, false);
                        isReloadEventListApi = true;
                    }
                }
            });

            binding.btnEditEvent.stopProgress();
        });

        binding.btnFavorite.setOnClickListener(view -> {
            Utils.preventDoubleClick(view);
            if (promoterEventModel == null) {
                return;
            }
            requestPromoterToggleWishList(promoterEventModel);
        });

        binding.btnCompleteEvent.setOnClickListener(view -> {
            Utils.preventDoubleClick(view);
            requestPromoterEventComplete();
        });

        binding.btnCancelEvent.setOnClickListener(view -> {
            Utils.preventDoubleClick(view);
            String delete_current_event = getValue("delete_current_event");
            String delete_all_event = getValue("delete_all_event");
            String confirm_delete = getValue("confirm_delete");
            String cancel = getValue("cancel");

            Graphics.showAlertDialogForDeleteEvent(
                    activity,
                    getValue("delete_event"),
                    getValue("event_cancellation_confirm_alert"),
                    delete_current_event,
                    cancel,
                    delete_all_event,
                    action -> {
                        if (action.equals(delete_current_event)) {
                            Graphics.showAlertDialogWithOkCancel(
                                    activity,
                                    confirm_delete,
                                    getValue("are_you_sure_cancel_this_event"),
                                    isConfirmed -> {
                                        if (isConfirmed) {
                                            requestPromoterEventCancel(false);
                                        }
                                    }
                            );
                        } else if (action.equals(delete_all_event)) {
                            Graphics.showAlertDialogWithOkCancel(
                                    activity,
                                    confirm_delete,
                                    getValue("are_you_sure_cancel_all_recurring_events"),
                                    isConfirmed -> {
                                        if (isConfirmed) {
                                            requestPromoterEventCancel(true);
                                        }
                                    }
                            );
                        } else if (action.equals(cancel)) {
                            // do nothing
                        }
                    }
            );
        });


        binding.eventImOut.setOnClickListener(view -> {
            Utils.preventDoubleClick(view);

            if (promoterEventModel == null) {
                return;
            }
            ArrayList<String> data = new ArrayList<>();
            data.add(getValue("cancel_confirmation"));

            Graphics.showActionSheetRedTitle(activity, activity.getString(R.string.app_name), data, (data1, position1) -> {
                if (data1.equals(getValue("cancel_confirmation"))) {
                    if (promoterEventModel.getInvite().getInviteStatus().equals(getValue("pending"))) {
                        requestPromoterUpdateInviteStatus(promoterEventModel.getInvite().getId(), "out");
                        return;
                    }

                    String tmpString = "";
                    try {
                        tmpString = String.valueOf(Utils.stringToDateWithUTCForEvent(promoterEventModel.getInvite().getUpdatedAt(), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (!TextUtils.isEmpty(tmpString) && Utils.checkUpdateTimeAndShowAlert(tmpString)) {
                        Graphics.showAlertDialogWithOkButton(activity, getValue("please_wait"), getValue("wait_before_changing_response"));
                    } else {
                        if (!promoterEventModel.isConfirmationRequired() && Utils.isDateToday(promoterEventModel.getDate())) {
                            if (Utils.isWithinDubaiTimeTwoHours(promoterEventModel.getStartTime())) {
                                String title = (promoterEventModel.getVenueType().equals("venue") && promoterEventModel.getVenue() != null) ?
                                        promoterEventModel.getVenue().getName() :
                                        (promoterEventModel.getCustomVenue() != null) ? promoterEventModel.getCustomVenue().getName() : null;
                                Graphics.showAlertDialogWithOkButton(activity, getValue("cancellation_not_allowed"),
                                        setValue("cannot_cancel_value_less_than_2_hours",title));
                            } else {
                                String title = getValue("confirm_cancel_attendance_warning");
                                showCancelDialog(title);
//                                Graphics.showAlertDialogWithOkCancel(activity, getString(R.string.app_name), title, "Yes", "No", isConfirmed -> {
//                                    if (isConfirmed) {
//                                        requestPromoterUpdateInviteStatus(promoterEventModel.getInvite().getId(), "out");
//                                    }
//                                });
                            }
                        } else {
                            String title = getValue("confirm_cancel_attendance_warning");
//
                            if (binding.eventImOutTv.getText().equals(getValue("pending"))) {
                                title = getValue("cancel_your_interest");
                            }
                            showCancelDialog(title);
//                            Graphics.showAlertDialogWithOkCancel(activity, getString(R.string.app_name), title, "Yes", "No", isConfirmed -> {
//                                if (isConfirmed) {
//                                    requestPromoterUpdateInviteStatus(promoterEventModel.getInvite().getId(), "out");
//                                }
//                            });
                        }
                    }
                }
            });
        });

        binding.eventImIn.setOnClickListener(view -> {
            Utils.preventDoubleClick(view);
            if (promoterEventModel == null) {
                return;
            }
            if (binding.eventImIn.getTxtTitle().equalsIgnoreCase(getValue("event_full")) || binding.eventImIn.getTxtTitle().equalsIgnoreCase(getValue("sorry_event_is_full"))) {
                return;
            }

//            if (ComplementaryProfileManager.checkEventInDateTime(promoterEventModel.getDate(), promoterEventModel.getStartTime())) {
//                Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), "You cannot be in different events at the same time");
//                return;
//            }

            if (promoterEventModel.getInvite().getInviteStatus().equalsIgnoreCase(getValue("pending"))) {
                String message = binding.eventImIn.getTxtTitle().equalsIgnoreCase(getValue("interested")) ? getValue("confirm_mark_interested_event") : getValue("spot_reserved_alert");
                Graphics.showAlertDialogWithOkCancel(activity, activity.getString(R.string.app_name), message, getValue("yes"), getValue("no"), isConfirmed -> {
                    if (isConfirmed) {
                        if (promoterEventModel.isPlusOneAccepted() && promoterEventModel.isPlusOneMandatory()) {
                            showPlusOneInviteSheet(true);
                        } else {
                            requestPromoterUpdateInviteStatus(promoterEventModel.getInvite().getId(), "in");
                        }
                    }
                });
                return;
            }

            String tmpString;
            try {
                tmpString = String.valueOf(Utils.stringToDateWithUTCForEvent(promoterEventModel.getInvite().getUpdatedAt(), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if (!TextUtils.isEmpty(tmpString) && Utils.checkUpdateTimeAndShowAlert(tmpString)) {
                Graphics.showAlertDialogWithOkButton(activity, getValue("please_wait"), getValue("wait_before_changing_response"));
            } else {
                String message = binding.eventImIn.getTxtTitle().equalsIgnoreCase(getValue("interested"))
                        ? getValue("confirm_mark_interested_event") : getValue("spot_reserved_alert");
                Graphics.showAlertDialogWithOkCancel(activity, activity.getString(R.string.app_name), message, getValue("yes"), getValue("no"), isConfirmed -> {
                    if (isConfirmed) {
                        if (promoterEventModel.isPlusOneAccepted() && promoterEventModel.isPlusOneMandatory()) {
                            showPlusOneInviteSheet(true);
                        } else {
                            requestPromoterUpdateInviteStatus(promoterEventModel.getInvite().getId(), "in");
                        }
                    }
                });

            }
        });

        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            if (type.equals("Promoter")) {
                requestPromoterEventDetail(eventId, false);
                binding.linearBtn.setVisibility(View.GONE);
            } else if (type.equals("PlusOneEvent")) {
                requestPlusOnePromoterEventDetail(eventId, false);
            } else {
                requestPromoterEventDetailUser(eventId, false);
            }
        });

        binding.ivShareEvent.setOnClickListener(v -> {
            Utils.preventDoubleClick(v);
            startActivity(new Intent(activity, VenueShareActivity.class)
                    .putExtra("promoterEvent", new Gson().toJson(promoterEventModel))
                    .putExtra("type", "promoterEvent"));
        });

        binding.btnMessageAdmin.setOnClickListener(v -> {
            Utils.preventDoubleClick(v);
            UserDetailModel model1 = new UserDetailModel();
            model1.setId(promoterEventModel.getUser().getId());
            model1.setFirstName(promoterEventModel.getUser().getFirstName());
            model1.setLastName(promoterEventModel.getUser().getLastName());
            model1.setImage(promoterEventModel.getUser().getImage());
            ChatModel chatModel = new ChatModel(model1);
            Intent intent = new Intent(activity, ChatMessageActivity.class);
            intent.putExtra("chatModel", new Gson().toJson(chatModel));
            intent.putExtra("isFromMessageAdmin", true);
            intent.putExtra("eventModel", new Gson().toJson(promoterEventModel));
            startActivity(intent);
        });

        binding.btnHideEvent.setOnClickListener(view -> {
            Utils.preventDoubleClick(view);
            requestPromoterEventHideShow(eventId, !promoterEventModel.isHidden());
        });

        binding.locationLayout.setOnClickListener(v -> {
            Utils.preventDoubleClick(v);
            openGoogleMap();
        });

        binding.viewTicket.setOnClickListener(view -> {
            Utils.preventDoubleClick(view);
            startActivity(new Intent(activity, EventPdfDownloadActivity.class).putExtra("eventModel", new Gson().toJson(promoterEventModel)));
        });

        binding.btnCloseEvent.setOnClickListener(view -> {
            Utils.preventDoubleClick(view);
            Graphics.showAlertDialogWithOkCancel(activity, getString(R.string.app_name), getValue("close_entry_alert"), isConfirmed -> {
                if (isConfirmed) {
                    requestPromoterCloseSport();
                }
            });

        });

        binding.invitePlusOneBtn.setOnClickListener(view -> {
            Utils.preventDoubleClick(view);
            showPlusOneInviteSheet(false);
        });

        binding.btnPlusOneOut.setOnClickListener(view -> {
            Utils.preventDoubleClick(view);
            String tmpString = "";
            try {
                tmpString = String.valueOf(Utils.stringToDateWithUTCForEvent(promoterEventModel.getInvite().getUpdatedAt(), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (!TextUtils.isEmpty(tmpString) && Utils.checkUpdateTimeAndShowAlert(tmpString)) {
                Graphics.showAlertDialogWithOkButton(activity, getValue("please_wait"), getValue("wait_before_changing_response"));
            } else {
                String title = getValue("confirm_cancel_attendance_warning");
                Graphics.showAlertDialogWithOkCancel(activity, getString(R.string.app_name), title, getValue("yes"), getValue("no"), isConfirmed -> {
                    if (isConfirmed) {
                        requestPromoterPlusOneEvenInviteStatus(promoterEventModel.getInvite().getId(), "out");
                    }
                });
            }

        });

        binding.btnPlusIn.setOnClickListener(view -> {
            Utils.preventDoubleClick(view);
            String tmpString = "";
            try {
                tmpString = String.valueOf(Utils.stringToDateWithUTCForEvent(promoterEventModel.getInvite().getUpdatedAt(), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (!TextUtils.isEmpty(tmpString) && Utils.checkUpdateTimeAndShowAlert(tmpString)) {
                Graphics.showAlertDialogWithOkButton(activity, getValue("please_wait"), getValue("wait_before_changing_response"));
            } else {
                String message = getValue("spot_reserved_alert");
                Graphics.showAlertDialogWithOkCancel(activity, activity.getString(R.string.app_name), message, isConfirmed -> {
                    if (isConfirmed) {
                        requestPromoterPlusOneEvenInviteStatus(promoterEventModel.getInvite().getId(), "in");
                    }
                });
            }
        });

        binding.nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            int[] location = new int[2];
            binding.eventGalleryRecyclerView.getLocationOnScreen(location);

            int recyclerViewTop = location[1];
            int recyclerViewBottom = recyclerViewTop + binding.eventGalleryRecyclerView.getHeight();
            int nestedScrollViewHeight = binding.nestedScrollView.getHeight();

            if (recyclerViewTop >= 0 && recyclerViewBottom <= nestedScrollViewHeight) {
                controlVideoPlayback(true);
            } else {
                controlVideoPlayback(false);
            }
        });

        binding.eventGalleryRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager == null) return;

                int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
                int lastCompletelyVisiblePosition = layoutManager.findLastCompletelyVisibleItemPosition();
                int totalItems = eventGalleryAdapter.getItemCount();
                int dotSelection = 0;

                // Determine dot selection based on visible items
                if (totalItems >= 3) {
                    if (firstVisiblePosition == 0) {
                        dotSelection = 0; // First item
                    } else if (lastCompletelyVisiblePosition == totalItems - 1) {
                        dotSelection = 2; // Last item
                    } else {
                        dotSelection = 1; // Middle items
                    }
                } else if (totalItems == 2) {
                    dotSelection = firstVisiblePosition == 0 ? 0 : 1; // First or second item
                } else if (totalItems == 1) {
                    dotSelection = 0; // Only one item
                }

                binding.dotsIndicator.setDotSelection(dotSelection); // Update dot indicator
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager == null) return;

                int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
                int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();

                // Manage video playback for visible items
                for (int i = firstVisiblePosition; i <= lastVisiblePosition; i++) {
                    RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(i);
                    if (viewHolder instanceof EventGalleryAdapter.EventVideoHolder) {
                        View itemView = viewHolder.itemView;
                        if (isView90PercentVisibleHorizontally(recyclerView, itemView)) {
                            ((EventGalleryAdapter.EventVideoHolder) viewHolder).startVideo();
                        } else {
                            ((EventGalleryAdapter.EventVideoHolder) viewHolder).pauseVideo();
                        }
                    }
                }
            }
        });


    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityComplementaryEventDetailBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("isReload", isReloadEventListApi);
        setResult(RESULT_OK, intent);
        finish();
        PromoterProfileManager.shared.promoterEventModel = null;
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (eventGalleryAdapter != null) {
            eventGalleryAdapter.releaseAllPlayers();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (eventGalleryAdapter != null) {
            eventGalleryAdapter.pauseAllVideos();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (eventGalleryAdapter != null) {
            eventGalleryAdapter.resumeAllVideos();
        }
    }

    @Override
    protected Map<View, String> getTranslationMap() {
        Map<View, String> map = new HashMap<>();
        map.put(binding.tvDressCode, "dress_code");
        map.put(binding.tvGenderTitle, "invited_gender");
        map.put(binding.tvRepetitiveTitle, "repetitive");
        map.put(binding.tvRequire, "requirements");
        map.put(binding.tvBenefit, "benefits");
        map.put(binding.tvPlusOne, "plusOne_specification");
        map.put(binding.txtSpots, "spot");
        map.put(binding.txtStatus, "plus_one_status");
        map.put(binding.cancelStatus, "plus_one_cancel_your_invitation");
        map.put(binding.txtGender, "guest_gender");
        map.put(binding.txtAge, "guest_age");
        map.put(binding.txtdressCode, "guest_dresscode");
        map.put(binding.txtNationality, "guest_nationality");
        map.put(binding.memberTv, "plus_one_members");
        map.put(binding.initeText, "invite_plus_one");
        map.put(binding.socialName, "social_account_to_tag");
        map.put(binding.txtEventStatus, "cancelled");
        map.put(binding.eventImOutTv, "confirmed");
        map.put(binding.viewTicketTv, "view_ticket");

        binding.userInvitedView.setTitle(getValue("users_invited"));
        binding.userCircledView.setTitle(getValue("circles_invited"));
        binding.plusOneView.setTitle(getValue("plus_one_members"));
        binding.intrestedUsers.setTitle(getValue("interested_users"));
        binding.cancelledUsers.setTitle(getValue("cancelled_users"));

        binding.btnPlusIn.setTxtTitle(getValue("im_in"));
        binding.btnPlusOneOut.setTxtTitle(getValue("im_out"));
        binding.btnEditEvent.setTxtTitle(getValue("edit_event"));
        binding.btnCancelEvent.setTxtTitle(getValue("cancel_event"));
        binding.btnCloseEvent.setTxtTitle(getValue("close_event"));
        binding.btnHideEvent.setTxtTitle(getValue("hide_event"));

        binding.eventImIn.setTxtTitle(getValue("im_in"));
        binding.btnMessageAdmin.setTxtTitle(getValue("message_admin"));

        return map;
    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void controlVideoPlayback(boolean shouldPlay) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) binding.eventGalleryRecyclerView.getLayoutManager();
        if (layoutManager == null) return;

        int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
        int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();

        for (int i = firstVisiblePosition; i <= lastVisiblePosition; i++) {
            RecyclerView.ViewHolder viewHolder = binding.eventGalleryRecyclerView.findViewHolderForAdapterPosition(i);
            if (viewHolder instanceof EventGalleryAdapter.EventVideoHolder) {
                if (shouldPlay) {
                    ((EventGalleryAdapter.EventVideoHolder) viewHolder).startVideo();
                } else {
                    ((EventGalleryAdapter.EventVideoHolder) viewHolder).pauseVideo();
                }
            }
        }
    }

    private boolean isView90PercentVisibleHorizontally(RecyclerView recyclerView, View view) {
        int[] viewLocation = new int[2];
        int[] recyclerViewLocation = new int[2];

        view.getLocationOnScreen(viewLocation);
        recyclerView.getLocationOnScreen(recyclerViewLocation);

        int viewStart = viewLocation[0];
        int viewEnd = viewStart + view.getWidth();

        int recyclerViewStart = recyclerViewLocation[0];
        int recyclerViewEnd = recyclerViewStart + recyclerView.getWidth();

        int visibleStart = Math.max(viewStart, recyclerViewStart);
        int visibleEnd = Math.min(viewEnd, recyclerViewEnd);

        int visibleWidth = Math.max(0, visibleEnd - visibleStart);
        float visibilityPercentage = (visibleWidth / (float) view.getWidth()) * 100;

        return visibilityPercentage >= 90;
    }

    @SuppressLint({"DefaultLocale", "NewApi"})
    private void setDetail(PromoterEventModel promoterEventModel) {
        if (promoterEventModel == null) {
            return;
        }


        PromoterProfileManager.shared.promoterEventModel = promoterEventModel;
        this.promoterEventModel = promoterEventModel;


        if (!promoterEventModel.getEventGallery().isEmpty()){
            if (promoterEventModel.getEventGallery().size() == 2) binding.dotsIndicator.initDots(2);
            if (promoterEventModel.getEventGallery().size() >= 3) binding.dotsIndicator.initDots(3);
            if (promoterEventModel.getEventGallery().size() >= 2) {
                binding.dotsIndicator.setVisibility(VISIBLE);
            } else {
                binding.dotsIndicator.setVisibility(GONE);
            }
            eventGalleryAdapter.updateData(promoterEventModel.getEventGallery().stream().map(RatingModel::new).collect(Collectors.toList()));
        }else {
            List<RatingModel> ratingModels = new ArrayList<>();
            String venueType = promoterEventModel.getVenueType();

            if ("custom".equals(venueType) && promoterEventModel.getCustomVenue() != null) {
                ratingModels.add(new RatingModel(promoterEventModel.getCustomVenue().getImage()));
            } else if (promoterEventModel.getVenue() != null && !TextUtils.isEmpty(promoterEventModel.getImage())) {
                ratingModels.add(new RatingModel(promoterEventModel.getImage()));
            }

            eventGalleryAdapter.updateData(ratingModels);
            binding.dotsIndicator.setVisibility(View.GONE);

        }


        setEventImageAndDetails();  // This is for setting the venue image, details, event time, event date, Offer

        setRequirementsAndBenefits(); // This method is used to set the requirements and benefits for the event


        if (promoterEventModel.getRepeat().equals("specific-date")) {
            binding.tvRepetitive.setText(promoterEventModel.getRepeatDate());
        } else {
            binding.tvRepetitive.setText(promoterEventModel.getRepeat());
        }

        binding.roundRepetitiveLayout.setVisibility(promoterEventModel.getRepeat().equals("none") ? GONE : VISIBLE);


        if (type.equals("Promoter")) {

            // This method is used to set invited users, invited circles, in-members, interested members, and the invite cancel list
            setEventMemberAllLists();

            binding.tvSpot.setText(String.format(setValue("spots",String.valueOf(promoterEventModel.getMaxInvitee()))));
            int count = promoterEventModel.getMaxInvitee() - promoterEventModel.getTotalInMembers();
            binding.tvCount.setText(setValue("remaining",String.valueOf(count)));


            binding.tvInvitedGender.setText(promoterEventModel.getInvitedGender());


        } else {

            // This hides the user invited list, invited circles, in-member list, canceled users, interested users, and sport views
            Utils.hideViews(binding.roundSpot, binding.userInvitedView,
                    binding.userCircledView, binding.userSeatsdView, binding.plusOneView, binding.intrestedUsers,
                    binding.invitedConstraint, binding.roundInvitedGender, binding.cancelledUsers);


            int drawableId = promoterEventModel.isWishlisted() ? R.drawable.icon_fav_selected : R.drawable.icon_fav_unselected;
            binding.ivFavourite.setImageDrawable(ContextCompat.getDrawable(activity, drawableId));

            if (promoterEventModel.getStatus().equals("completed")) {
                binding.btnShare.setVisibility(GONE);
                binding.btnFavorite.setVisibility(GONE);
            } else {
                binding.btnShare.setVisibility(VISIBLE);
                binding.btnFavorite.setVisibility(VISIBLE);
            }

            if (promoterEventModel.isPlusOneAccepted()) {
                boolean isShowButton = promoterEventModel.getInvite().getInviteStatus().equals("in") && promoterEventModel.getInvite().getPromoterStatus().equals("accepted") && promoterEventModel.getStatus().equals("upcoming");
                binding.invitePlusOneBtn.setVisibility(isShowButton ? VISIBLE : GONE);
            } else {
                binding.OnePlusLayout.setVisibility(GONE);
            }
        }


        binding.tvDressCodeName.setText(promoterEventModel.getDressCode());


        if (promoterEventModel.getSocialAccountsToMention() != null && !promoterEventModel.getSocialAccountsToMention().isEmpty()) {
            socialItemListAdapter.updateData(promoterEventModel.getSocialAccountsToMention());
        }

        if (promoterEventModel.isPlusOneAccepted() && !type.equals("PlusOneEvent")) {

            if (promoterEventModel.isPlusOneMandatory()) {
                binding.statusLinear.setVisibility(VISIBLE);
            } else {
                binding.statusLinear.setVisibility(GONE);
            }

            binding.OnePlusLayout.setVisibility(VISIBLE);
            binding.txtCount.setText(promoterEventModel.getPlusOneQty() + " " + getValue("seats"));

            if (promoterEventModel.getExtraGuestType().equals("anyone")) {
                if (promoterEventModel.getExtraGuestGender().equals("both")) {
                    binding.genderLinear.setVisibility(GONE);
                } else {
                    binding.genderLinear.setVisibility(VISIBLE);
                    binding.tvGender.setText(promoterEventModel.getExtraGuestGender());
                }
                binding.ageLinear.setVisibility(GONE);
                binding.dressCodeLinear.setVisibility(GONE);
                binding.nationalityLinear.setVisibility(GONE);
            } else {
                binding.genderLinear.setVisibility(VISIBLE);
                binding.tvGender.setText(promoterEventModel.getExtraGuestGender());
            }

            if (!TextUtils.isEmpty(promoterEventModel.getExtraGuestAge())) {
                binding.tvAge.setText(promoterEventModel.getExtraGuestAge());
            } else {
                binding.ageLinear.setVisibility(GONE);
            }

            if (!TextUtils.isEmpty(promoterEventModel.getExtraGuestDressCode())) {
                binding.tvDress.setText(promoterEventModel.getExtraGuestDressCode());
            } else {
                binding.dressCodeLinear.setVisibility(GONE);
            }
            if (!TextUtils.isEmpty(promoterEventModel.getExtraGuestNationality())) {
                binding.tvNationality.setText(promoterEventModel.getExtraGuestNationality());
            } else {
                binding.nationalityLinear.setVisibility(GONE);
            }

            plusMemberAdapter = new PlusOneMemberListAdapter<>(activity, getSupportFragmentManager());
            binding.memberRecyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
            binding.memberRecyclerView.setAdapter(plusMemberAdapter);

            if (promoterEventModel.getPlusOneMembers() != null && !promoterEventModel.getPlusOneMembers().isEmpty()) {
                binding.memberTv.setVisibility(VISIBLE);
                binding.memberRecyclerView.setVisibility(VISIBLE);
                plusMemberAdapter.updateData(promoterEventModel.getPlusOneMembers());
            } else {
                binding.memberTv.setVisibility(GONE);
                binding.memberRecyclerView.setVisibility(GONE);
            }

        } else {
            binding.OnePlusLayout.setVisibility(GONE);
        }


        if (!TextUtils.isEmpty(type) && type.equals("Promoter")) {
            AppExecutors.get().mainThread().execute(() -> {

                binding.txtEventStatus.setVisibility(GONE);
                binding.btnEditEvent.setVisibility(GONE);
                binding.btnCancelEvent.setVisibility(GONE);
                binding.btnHideEvent.setVisibility(GONE);
                String status = this.promoterEventModel.getStatus();

                if (status.equals("cancelled") || status.equals("completed")) {
                    binding.txtEventStatus.setVisibility(VISIBLE);
                    String name = status.equals("completed") ? getValue("event_completed") : getValue("cancelled");
                    binding.txtEventStatus.setText(name);
                    binding.txtEventStatus.setTextColor(ContextCompat.getColor(activity, R.color.buy_button));
                } else if (status.equals("in-progress")) {
                    binding.btnEditEvent.setVisibility(VISIBLE);
                    binding.btnCancelEvent.setVisibility(VISIBLE);
                    binding.btnCompleteEvent.setVisibility(VISIBLE);

                    if (promoterEventModel.getSpotCloseType().equals("manual") && !promoterEventModel.isEventFull() && Utils.isSpotOpen(promoterEventModel.getSpotCloseAt()) && !promoterEventModel.isSpotClosed()) {
                        binding.btnCloseEvent.setVisibility(VISIBLE);
                    } else {
                        binding.btnCloseEvent.setVisibility(GONE);
                    }

                } else {
                    binding.btnEditEvent.setVisibility(VISIBLE);
                    binding.btnCancelEvent.setVisibility(VISIBLE);

                    if (!promoterEventModel.isSpotClosed()) {
                        binding.btnCloseEvent.setVisibility(VISIBLE);
                    } else {
                        binding.btnCloseEvent.setVisibility(GONE);
                    }
                }

                if (status.equals("completed")) {
                    binding.btnHideEvent.setVisibility(VISIBLE);
                    if (this.promoterEventModel.isHidden()) {
                        binding.btnHideEvent.setTxtTitle(getValue("hide_event"));
                    } else {
                        binding.btnHideEvent.setTxtTitle(getValue("show_event"));
                    }
                }
            });
        }

        if (type.equals("PlusOneEvent")) {
            binding.btnShare.setVisibility(View.GONE);
            binding.btnFavorite.setVisibility(GONE);
        }


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setEventImageAndDetails() {
        if (promoterEventModel == null) return;
        if (promoterEventModel.getVenueType().equals("venue") && promoterEventModel.getVenue() != null) {
            Graphics.loadImageWithFirstLetter(promoterEventModel.getVenue().getLogo(), binding.eventLogo, promoterEventModel.getVenue().getLogo());
            binding.titleText.setText(promoterEventModel.getVenue().getName());
            binding.subTitleText.setText(promoterEventModel.getVenue().getAddress());
            binding.tvAdress.setText(promoterEventModel.getVenue().getAddress());

        } else {
            if (promoterEventModel.getCustomVenue() != null) {
                binding.titleText.setText(promoterEventModel.getCustomVenue().getName());
                binding.subTitleText.setText(promoterEventModel.getCustomVenue().getAddress());
                binding.tvAdress.setText(promoterEventModel.getCustomVenue().getAddress());
                if (promoterEventModel.getCustomVenue().getImage() != null && !promoterEventModel.getCustomVenue().getImage().isEmpty()) {
//                    Graphics.loadImageWithFirstLetter(promoterEventModel.getCustomVenue().getImage(), binding.eventCoverImage, promoterEventModel.getCustomVenue().getName());
                } else {
                    Graphics.loadImageWithFirstLetter(promoterEventModel.getCustomVenue().getImage(), binding.eventLogo, promoterEventModel.getCustomVenue().getName());
                }
                Graphics.loadImageWithFirstLetter(promoterEventModel.getCustomVenue().getImage(), binding.eventLogo, promoterEventModel.getCustomVenue().getName());
            }
        }

        if (Utils.isWithinDubaiTimeTwoHours(this.promoterEventModel.getStartTime()) && Utils.isDateToday(this.promoterEventModel.getDate()) && !Utils.isCurrentTimeEqualForDubaiTime(this.promoterEventModel.getStartTime())) {
            binding.timerConstraint.setVisibility(VISIBLE);
            binding.layoutTimer.setVisibility(VISIBLE);
            Utils.setTimerForDubaiEvent(activity, this.promoterEventModel.getDate(), this.promoterEventModel.getStartTime(), binding.timer);
        } else {
            binding.timerConstraint.setVisibility(GONE);
            binding.timerConstraint.setVisibility(View.GONE);
        }

        binding.tvDate.setText(Utils.changeDateFormat(promoterEventModel.getDate(), AppConstants.DATEFORMAT_SHORT, AppConstants.DATEFORMT_MM_DATE));
        binding.tvDescription.setText(promoterEventModel.getDescription());
        binding.tvTime.setText(promoterEventModel.getStartTime() + " - " + promoterEventModel.getEndTime());

        if (promoterEventModel.getOffer() != null) {
            binding.offerInfoView.setOfferDetail(promoterEventModel.getOffer(), activity, getSupportFragmentManager());
            binding.tvOfferDescription.setText(promoterEventModel.getOffer().getDescription());
            binding.txtTitle.setText(promoterEventModel.getOffer().getTitle());
        } else {
            binding.offerDetailContainer.setVisibility(GONE);
        }
        if (promoterEventModel.getOffer() != null) {
            binding.offerInfoView.setOfferDetail(promoterEventModel.getOffer(), activity, getSupportFragmentManager());
            binding.tvOfferDescription.setText(promoterEventModel.getOffer().getDescription());
            binding.txtTitle.setText(promoterEventModel.getOffer().getTitle());
        } else {
            binding.offerDetailContainer.setVisibility(GONE);
        }
    }

    private void setRequirementsAndBenefits() {
        List<RatingModel> requrimentsList = new ArrayList<>();
        List<RatingModel> benefitsList = new ArrayList<>();

        addToList(requrimentsList, promoterEventModel.getRequirementsAllowed(), "allowed");
        addToList(requrimentsList, promoterEventModel.getRequirementsNotAllowed(), "notallowed");
        if (!requrimentsList.isEmpty()) {
            requirementsAdapter.updateData(requrimentsList);
        }


        addToList(benefitsList, promoterEventModel.getBenefitsIncluded(), "allowed");
        addToList(benefitsList, promoterEventModel.getBenefitsNotIncluded(), "notallowed");
        if (!benefitsList.isEmpty()) {
            benefitsAdapter.updateData(benefitsList);

        }
    }

    private void setEventMemberAllLists() {

        binding.userInvitedView.setTitle(getValue("users_invited"));
        binding.userCircledView.setTitle(getValue("circles_invited"));
        binding.plusOneView.setTitle(getValue("plus_one_members"));
        binding.intrestedUsers.setTitle(getValue("interested_users"));
        binding.cancelledUsers.setTitle(getValue("cancelled_users"));

        if (promoterEventModel.getInvitedUsers() != null && !promoterEventModel.getInvitedUsers().isEmpty()) {
            binding.userInvitedView.getTotalInvitedUsers = promoterEventModel.getInvitedUsers().size();
            binding.userInvitedView.setVisibility(View.VISIBLE);
            binding.userInvitedView.promoterEventModel = promoterEventModel;
            binding.userInvitedView.isFromUsersInvited = true;
            binding.userInvitedView.isFormInterestedMembers = false;
            binding.userInvitedView.isFormEventHistory = isFromHistory;
            binding.userInvitedView.callback = data -> {
                if (data) {
                    isReloadEventListApi = true;
                    requestPromoterEventDetail(eventId, false);
                }
            };
            binding.userInvitedView.setUpData(promoterEventModel.getInvitedUsers(), activity, getSupportFragmentManager(), false, false);
        } else {
            binding.userInvitedView.setVisibility(View.GONE);
        }

        if (promoterEventModel.getInvitedCircles() != null && !promoterEventModel.getInvitedCircles().isEmpty()) {
            binding.userCircledView.setVisibility(View.VISIBLE);
            binding.userCircledView.setUpData(promoterEventModel.getInvitedCircles(), activity, getSupportFragmentManager(), true, false);
        } else {
            binding.userCircledView.setVisibility(View.GONE);
        }

        if (promoterEventModel.getInMembers() != null && !promoterEventModel.getInMembers().isEmpty()) {
            binding.userSeatsdView.setVisibility(View.VISIBLE);
            binding.userSeatsdView.maxInvitee = promoterEventModel.getMaxInvitee();
            binding.userSeatsdView.promoterEventModel = promoterEventModel;
            binding.userSeatsdView.totalInMembers = promoterEventModel.getTotalInMembers();
            binding.userSeatsdView.isFormEventHistory = isFromHistory;
            binding.userSeatsdView.callback = data -> {
                if (data) {
                    isReloadEventListApi = true;
                    requestPromoterEventDetail(eventId, false);
                }
            };
            binding.userSeatsdView.setUpData(promoterEventModel.getInMembers(), activity, getSupportFragmentManager(), false, true);
        } else {
            binding.userSeatsdView.setVisibility(View.GONE);
        }

        if (promoterEventModel.getPlusOneMembers() != null && !promoterEventModel.getPlusOneMembers().isEmpty()) {
            binding.plusOneView.setVisibility(View.VISIBLE);
            binding.plusOneView.promoterEventModel = promoterEventModel;
            binding.plusOneView.isFromPlusOneUser = true;
            binding.plusOneView.isFormEventHistory = isFromHistory;
            binding.plusOneView.callback = data -> {
                if (data) {
                    isReloadEventListApi = true;
                    requestPromoterEventDetail(eventId, false);
                }
            };
            binding.plusOneView.setUpData(promoterEventModel.getPlusOneMembers(), activity, getSupportFragmentManager(), false, false);
        } else {
            binding.plusOneView.setVisibility(View.GONE);
        }

        if (promoterEventModel.getInterestedMembers() != null && !promoterEventModel.getInterestedMembers().isEmpty()) {
            binding.intrestedUsers.setVisibility(View.VISIBLE);
            binding.intrestedUsers.getTotalInvitedUsers = promoterEventModel.getInterestedMembers().size();
            binding.intrestedUsers.promoterEventModel = promoterEventModel;
            binding.intrestedUsers.isFormEventHistory = isFromHistory;
            binding.intrestedUsers.isFormInterestedMembers = true;
            binding.intrestedUsers.callback = data -> {
                if (data) {
                    isReloadEventListApi = true;
                    requestPromoterEventDetail(eventId, false);
                }
            };
            binding.intrestedUsers.setUpData(promoterEventModel.getInterestedMembers(), activity, getSupportFragmentManager(), false, false);
        } else {
            binding.intrestedUsers.setVisibility(View.GONE);
        }

        if (promoterEventModel.getInviteCancelList() != null && !promoterEventModel.getInviteCancelList().isEmpty()) {
            binding.cancelledUsers.setVisibility(View.VISIBLE);
            binding.cancelledUsers.getTotalInvitedUsers = promoterEventModel.getInterestedMembers().size();
            binding.cancelledUsers.promoterEventModel = promoterEventModel;
            binding.cancelledUsers.isCancellesUser = true;
            binding.cancelledUsers.isFormEventHistory = isFromHistory;
            binding.cancelledUsers.setUpData(promoterEventModel.getInviteCancelList(), activity, getSupportFragmentManager(), false, false);
        } else {
            binding.cancelledUsers.setVisibility(View.GONE);
        }


        if (binding.intrestedUsers.getVisibility() == View.GONE && binding.userSeatsdView.getVisibility() == View.GONE
                && binding.userCircledView.getVisibility() == View.GONE && binding.userInvitedView.getVisibility() == View.GONE
                && binding.cancelledUsers.getVisibility() == View.GONE && binding.plusOneView.getVisibility() == View.GONE
        ) {
            binding.invitedConstraint.setVisibility(View.GONE);
        }
    }

    private void setInviteStatus(PromoterEventModel model) {
        if (model == null) {
            return;
        }
        if (type.equals("complementary")) {

            binding.btnEditEvent.setVisibility(View.GONE);
            binding.btnCancelEvent.setVisibility(View.GONE);
            binding.viewTicketConsLayout.setVisibility(GONE);

            binding.txtEventStatus.setVisibility(VISIBLE);
            binding.eventImIn.setVisibility(GONE);
            binding.outFavButtonsLayout.setVisibility(GONE);
            binding.btnCompleteEvent.setVisibility(GONE);
            binding.eventImOut.setVisibility(GONE);

            if (model.getStatus().equals("completed")) {
                String status = getValue("event_completed");
                binding.txtEventStatus.setText(status);
                binding.txtEventStatus.setTextColor(ContextCompat.getColor(activity, R.color.red_color));
            } else if ("in-progress".equals(model.getStatus())) {
                if (!model.isSpotClosed() && model.getSpotCloseType().equals("manual") && !model.getInvite().getPromoterStatus().equals("accepted") && Utils.isSpotOpen(model.getSpotCloseAt())) {
                    binding.eventImIn.setVisibility(VISIBLE);
                    binding.eventImOut.setVisibility(GONE);
                    binding.outFavButtonsLayout.setVisibility(VISIBLE);
                    binding.txtEventStatus.setVisibility(GONE);
                    hideAndShowButtons(model);
                } else if (!model.getInvite().getPromoterStatus().equals("accepted") && model.isSpotClosed() && !model.getInvite().getInviteStatus().equals("in")) {
                    binding.eventImIn.setVisibility(VISIBLE);
                    binding.eventImOut.setVisibility(GONE);
                    binding.outFavButtonsLayout.setVisibility(VISIBLE);
                    binding.txtEventStatus.setVisibility(GONE);
                    hideAndShowButtons(model);
                } else {
                    binding.txtEventStatus.setText(getValue("event_started"));
                    binding.txtEventStatus.setTextColor(ContextCompat.getColor(activity, R.color.green_medium));
                    binding.invitePlusOneBtn.setVisibility(GONE);
                }
            } else {
                binding.eventImIn.setVisibility(VISIBLE);
                binding.eventImOut.setVisibility(GONE);
                binding.outFavButtonsLayout.setVisibility(VISIBLE);
                binding.txtEventStatus.setVisibility(GONE);
                hideAndShowButtons(model);
            }
            if (model.getInvite().getPromoterStatus().equals("accepted") && model.getInvite().getInviteStatus().equals("in")) {
                binding.viewTicketConsLayout.setVisibility(VISIBLE);
            } else {
                binding.viewTicketConsLayout.setVisibility(GONE);
            }

        }
    }

    private void hideAndShowButtons(PromoterEventModel model) {
        AppExecutors.get().mainThread().execute(() -> {
            binding.txtEventStatus.setVisibility(GONE);

            binding.outFavButtonsLayout.setVisibility(VISIBLE);

            updateButtonVisibility(model.getInvite().getInviteStatus(), model.isConfirmationRequired(), model);
        });
    }

    private void updateButtonVisibility(String inviteStatus, boolean isConfirmationRequired, PromoterEventModel model) {
        boolean isPending = inviteStatus.equals("pending");
        boolean isOut = inviteStatus.equals("out");
        boolean isIn = inviteStatus.equals("in");
        boolean isEventFull = false;
        if (!model.getInvite().getPromoterStatus().equals("accepted")) {
            isEventFull = ("rejected".equals(model.getInvite().getPromoterStatus()) || model.getRemainingSeats() <= 0 || model.getStatus().equals("cancelled") || model.isEventFull());
            if (model.isSpotClosed() && !model.getInvite().getInviteStatus().equals("in")) {
                isEventFull = true;
            }
        }


        binding.eventImIn.setVisibility(isPending || isOut || isEventFull ? VISIBLE : GONE);
        binding.eventImOut.setVisibility(isEventFull ? GONE : ((isIn) ? VISIBLE : GONE));


        String tmpStatus = model.getInvite().getPromoterStatus().equals("accepted") ? getValue("confirmed") : getValue("pending");
        binding.eventImOutTv.setText(isConfirmationRequired ? tmpStatus : getValue("confirmed"));
        if (binding.eventImOutTv.getText().toString().equalsIgnoreCase(getValue("confirmed"))) {
            binding.eventImOut.setBackgroundColor(activity.getColor(R.color.im_in));
        } else {
            binding.eventImOut.setBackgroundColor(activity.getColor(R.color.amber_color));
        }

        String eventFullText = "";
        if (!"in".equals(model.getInvite().getPromoterStatus())) {
            eventFullText = getValue("event_full");
        }
        if (model.isSpotClosed() && !model.getInvite().getInviteStatus().equals("in")) {
            eventFullText = getValue("sorry_event_is_full");
        }


        binding.eventImIn.setTxtTitle(isEventFull ? eventFullText : (model.isConfirmationRequired() ? getValue("interested") : getValue("im_in")));

        binding.btnMessageAdmin.setVisibility(isEventFull ? View.GONE : View.VISIBLE);

        int imInColor = isConfirmationRequired ? R.color.intrested_color : R.color.im_in;
        int color = isEventFull ? R.color.event_full_color : imInColor;
        binding.eventImIn.setBgColor(activity.getColor(color));

        if (binding.eventImOutTv.getText().toString().equalsIgnoreCase(getValue("confirmed")) && binding.eventImOut.getVisibility() == VISIBLE) {
            binding.viewTicketConsLayout.setVisibility(VISIBLE);
        } else {
            binding.viewTicketConsLayout.setVisibility(GONE);
        }


    }

    private void showActionSheet() {
        ArrayList<String> data = new ArrayList<>();
        data.add("Share");
        Graphics.showActionSheet(activity, binding.titleText.getText().toString(), data, (data1, position1) -> {
            switch (position1) {
                case 0:
                    requestLinkCreate();
                    break;
            }
        });
    }

    private void setPlusOneInviteStatus(PromoterEventModel model) {
        Utils.hideViews(binding.eventStatusLayout, binding.editCompleteBtnLayout, binding.linearBtn, binding.plusOneButtonLayout);
        binding.eventStatusLayout.setVisibility(VISIBLE);

        String status = model.getStatus();
        boolean isPromoterNotAccepted = !model.getInvite().getPromoterStatus().equals("accepted");

        if ("completed".equals(status)) {
            updateEventStatus(getValue("event_completed"), R.color.red_color);
        } else if ("in-progress".equals(status)) {
            boolean isManualSpotOpen = !model.isSpotClosed() && "manual".equals(model.getSpotCloseType()) && Utils.isSpotOpen(model.getSpotCloseAt());
            boolean isSpotClosedNotIn = model.isSpotClosed() && !"in".equals(model.getInvite().getInviteStatus());

            if (isPromoterNotAccepted && (isManualSpotOpen || isSpotClosedNotIn)) {
                setPlusButtonVisibility(model.getInvite().getInviteStatus());
            } else {
                updateEventStatus(getValue("event_started"), R.color.green_medium);
            }
        } else {
            setPlusButtonVisibility(model.getInvite().getInviteStatus());
        }
    }

    private void updateEventStatus(String statusText, @ColorRes int colorResId) {
        binding.txtEventStatus.setText(statusText);
        binding.txtEventStatus.setTextColor(ContextCompat.getColor(activity, colorResId));
    }

    private void setPlusButtonVisibility(String inviteStatus){
        binding.eventStatusLayout.setVisibility(GONE);
        binding.plusOneButtonLayout.setVisibility(VISIBLE);
        switch (inviteStatus) {
            case "in":
                binding.btnPlusIn.setVisibility(GONE);
                binding.btnPlusOneOut.setVisibility(VISIBLE);
                break;
            case "out":
                binding.btnPlusIn.setVisibility(VISIBLE);
                binding.btnPlusOneOut.setVisibility(GONE);
                break;
            default:
                binding.btnPlusIn.setVisibility(VISIBLE);
                binding.btnPlusOneOut.setVisibility(VISIBLE);
                break;
        }

    }

    private void openGoogleMap() {

        double lat = 0.0;
        double lang = 0.0;

        if (promoterEventModel.getVenueType().equals("venue")) {
            lat = promoterEventModel.getVenue().getLat();
            lang = promoterEventModel.getVenue().getLng();
        } else {
            lat = promoterEventModel.getCustomVenue().getLat();
            lang = promoterEventModel.getCustomVenue().getLng();
        }


        String geoUri = "google.navigation:q=" + lat + "," + lang + "&label=" + binding.titleText.getText().toString() + "&directionsmode=driving";
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(geoUri));
        intent.setPackage("com.google.android.apps.maps");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            String webUrl = "http://maps.google.com/maps?q=loc:" + lat + "," + lang + " (" + binding.titleText.getText().toString() + ")";
            Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(webUrl));

            webIntent.setPackage("com.android.chrome");
            if (webIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(webIntent);
            } else {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(webUrl)));
            }

        }
    }

    private static void addToList(List<RatingModel> list, List<String> items, String type) {
        if (items != null && items.size() > 0) {
            for (String item : items) {
                RatingModel model = new RatingModel();
                model.setType(type);
                model.setName(item.trim());
                list.add(model);
            }
        }
    }

    private void showPlusOneInviteSheet(boolean isInvitePlusOne) {
        AddPlusOneGuestBottomSheet bottomSheet = new AddPlusOneGuestBottomSheet();
        bottomSheet.eventID = eventId;
        bottomSheet.isInvitePlusOne = isInvitePlusOne;
        bottomSheet.promoterEventModel = promoterEventModel;
        bottomSheet.callback = data -> {
            if (data.equalsIgnoreCase("CallEventIn")) {
                requestPromoterUpdateInviteStatus(promoterEventModel.getInvite().getId(), "in");
            } else if (data.equalsIgnoreCase("CallEventDetail")) {
                requestPromoterEventDetailUser(eventId, false);
            }
        };
        bottomSheet.show(getSupportFragmentManager(), "AddPlusOneGuestBottomSheet");
    }

    private void showBtnOutProgress(){
        binding.btnOutProgressView.setVisibility(VISIBLE);
        binding.eventImOutTv.setVisibility(View.INVISIBLE);
        binding.separatorView.setVisibility(View.INVISIBLE);
        binding.dropDownIcon.setVisibility(View.INVISIBLE);
    }

    private void hideBtnOutProgress(){
        binding.btnOutProgressView.setVisibility(GONE);
        binding.eventImOutTv.setVisibility(View.INVISIBLE);
        binding.separatorView.setVisibility(View.INVISIBLE);
        binding.dropDownIcon.setVisibility(View.INVISIBLE);
    }

    private void showPenaltyDialog(EventInOutPenaltyModel data) {
        CancellationPenaltyDialog dialog = new CancellationPenaltyDialog();
        dialog.callback = data1 -> {
            if (data1) {
                SelectPaymentOptionBottomSheet bottmSheet = new SelectPaymentOptionBottomSheet();
                bottmSheet.amount = (double) data.getAmount();
                bottmSheet.callback = q -> requestStripeToken(data,false,null,q);
                bottmSheet.show(getSupportFragmentManager(), "");
            }else {
                showFAQDialog();
            }
        };
        dialog.model = data;
        dialog.show(getSupportFragmentManager(), "");

    }

    private void showFAQDialog(){
        FAQBottomSheet dialog = new FAQBottomSheet();
        dialog.faqsting = promoterEventModel.getFaq();
        dialog.show(getSupportFragmentManager(),"");
    }

    private void showCancelDialog(String message) {
        EventFAQDialog dialog = new EventFAQDialog();
        dialog.message = message;
        dialog.faqCallBack = data -> {
            showFAQDialog();
        };
        dialog.callback = data -> {
            if (data) {
                requestPromoterUpdateInviteStatus(promoterEventModel.getInvite().getId(), "out");
            }
        };
        dialog.show(getSupportFragmentManager(),"");
    }


    private void showEventPass(String error){
        CustomMultiOptionAlertVC dialog = new CustomMultiOptionAlertVC();
        dialog.error = error;
        dialog.callback = data -> {
            requestPaidPassByID();
        };
        dialog.show(getSupportFragmentManager(),"");
    }


    private void showEventPaidPassPop(PromoterPaidPassModel model){
        PaidPassPopupVCdialog dialog = new PaidPassPopupVCdialog();
        dialog.promoterPaidPassModel = model;
        dialog.callback = data -> {
            SelectPaymentOptionBottomSheet bottmSheet = new SelectPaymentOptionBottomSheet();
            bottmSheet.amount = (double) model.getAmount();
            bottmSheet.callback = q -> requestStripeToken(null,true,model,q);
            bottmSheet.show(getSupportFragmentManager(), "");
        };
        dialog.show(getSupportFragmentManager(),"");
    }



    private void startStripeCheckOut(PaymentCredentialModel model) {
        if (model.publishableKey == null || model.clientSecret == null) {
            Toast.makeText(activity, "Invalid payment configuration", Toast.LENGTH_SHORT).show();
            return;
        }
        PaymentConfiguration.init(activity, model.publishableKey);
        final PaymentSheet.Configuration configuration = new PaymentSheet.Configuration.Builder("Whosin, Inc.").allowsDelayedPaymentMethods(true).build();
        paymentSheet.presentWithPaymentIntent(model.clientSecret, configuration);
    }

    private void startGooglePayCheckOut(PaymentCredentialModel model) {
        if (model.publishableKey == null || model.clientSecret == null) {
            Toast.makeText(activity, "Invalid payment configuration", Toast.LENGTH_SHORT).show();
            return;
        }
        PaymentConfiguration.init(activity, model.publishableKey);
        final PaymentSheet.GooglePayConfiguration googlePayConfiguration = new PaymentSheet.GooglePayConfiguration(AppConstants.GPAY_ENV, AppConstants.GPAY_REGION);
        final PaymentSheet.Configuration configuration = new PaymentSheet.Configuration.Builder("Whosin, Inc.").allowsDelayedPaymentMethods(true).googlePay(googlePayConfiguration).build();
        paymentSheet.presentWithPaymentIntent(model.clientSecret, configuration);
    }


    private void tabbyCheckOut(PaymentCredentialModel model) {
        TabbyPaymentBottomSheet sheet = new TabbyPaymentBottomSheet();
        sheet.paymentTabbyModel = model.getTabbyModel();
        sheet.callback = p -> {
            if (!TextUtils.isEmpty(p)) {
                switch (p) {
                    case "success":
                        PurchaseSuccessFragment purchaseSuccessFragment = new PurchaseSuccessFragment();
                        purchaseSuccessFragment.callBackForRaynaBooking = q -> {
                            if (q) {
                                requestPromoterUpdateInviteStatus(promoterEventModel.getInvite().getId(), "in");
                            }
                        };
                        purchaseSuccessFragment.show(getSupportFragmentManager(), "");

                        break;
                    case "cancel":

                        break;
                    case "failure":

                        break;
                }
            }
        };
        sheet.show(getSupportFragmentManager(), "");
    }

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestPaidPassByID() {
        showProgress();
        DataService.shared(activity).requestPromoterPaidPassByEventId(promoterEventModel.getId(), new RestCallback<ContainerModel<PromoterPaidPassModel>>(this) {
            @Override
            public void result(ContainerModel<PromoterPaidPassModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (model.getData() != null){
                    showEventPaidPassPop(model.getData());

                }
            }
        });
    }



    private void requestStripeToken(EventInOutPenaltyModel model,boolean isFromPaidPassByEventId,PromoterPaidPassModel promoterPaidPassModel,int paymentMod) {
        JsonObject jsonObject = new JsonObject();
        if (isFromPaidPassByEventId){
            if (promoterPaidPassModel == null) return;
            jsonObject.addProperty("amount",promoterPaidPassModel.getAmount());
            jsonObject.addProperty("type","paid-pass");
            jsonObject.addProperty("venueId",promoterEventModel.getVenue().getId());
            jsonObject.addProperty("eventId",promoterEventModel.getId());
            jsonObject.addProperty("paidPassId",promoterPaidPassModel.getId());
        }else {
            jsonObject.addProperty("amount",model.getAmount());
            jsonObject.addProperty("currency",model.getCurrency());
            jsonObject.addProperty("type",model.getType());
        }

        if (AppConstants.CARD_PAYMENT == paymentMod) {
            jsonObject.addProperty("paymentMethod", "stripe");
        } else if (AppConstants.PAY_WITH_LINK == paymentMod) {
            jsonObject.addProperty("paymentMethod", "stripe");
        } else if (AppConstants.TABBY_PAYMENT == paymentMod) {
            jsonObject.addProperty("paymentMethod", "tabby");
        } else if (AppConstants.GOOGLE_PAY == paymentMod || AppConstants.SAMSUNG_PAY == paymentMod) {
            jsonObject.addProperty("paymentMethod", "stripe");
        }


        showProgress();
        DataService.shared(activity).requestPromoterStripePayment(jsonObject, new RestCallback<ContainerModel<PaymentCredentialModel>>(this) {
            @Override
            public void result(ContainerModel<PaymentCredentialModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (model.getData() != null) {
                    if (AppConstants.CARD_PAYMENT == paymentMod) {
                        startStripeCheckOut(model.getData());
                    } else if (AppConstants.PAY_WITH_LINK == paymentMod) {
                        startStripeCheckOut(model.getData());
                    }else if (AppConstants.GOOGLE_PAY == paymentMod || AppConstants.SAMSUNG_PAY == paymentMod) {
                        startGooglePayCheckOut(model.getData());
                    } else if (AppConstants.TABBY_PAYMENT == paymentMod) {
                        if (Utils.isAvailableTabby(model.getData().getTabbyModel())) {
                            tabbyCheckOut(model.getData());
                        } else {
                            Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), getValue("tabby_payment_failed"));
                        }

                    }

                }
            }
        });
    }


    // endregion
    // --------------------------------------
    // region Complementary Event Data Service
    // --------------------------------------

    private void requestPromoterUpdateInviteStatus(String inviteId, String inviteStatus) {

        if (inviteStatus.equals("in")) {
            binding.eventImIn.startProgress();
        }else {
            showBtnOutProgress();
        }

        DataService.shared(activity).requestPromoterUpdateInviteStatus(inviteId, inviteStatus, new RestCallback<ContainerModel<EventInOutPenaltyModel>>(this) {
            @Override
            public void result(ContainerModel<EventInOutPenaltyModel> model, String error) {
                if (inviteStatus.equals("in")) {
                    binding.eventImIn.stopProgress();
                }else {
                    hideBtnOutProgress();
                }

                if (!Utils.isNullOrEmpty(error) || model == null) {
                    String searchString = "You've recently enjoyed a complimentary visit to this venue";
                    if (error.contains(searchString)){
                        showEventPass(error);
                    }else {
                        Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    }
                    return;
                }


                if (model.message.equals("cancellation-penalty")){
                    showPenaltyDialog(model.data);
                }else {
                    if (inviteStatus.equals("in")) {
                        String titleMsg = (promoterEventModel != null && promoterEventModel.isConfirmationRequired())
                                ? getValue("show_intrest_promoter")
                                : getValue("thank_you_for_joining");

                        String subtitleMsg = (promoterEventModel != null && promoterEventModel.isConfirmationRequired())
                                ? getValue("admin_will_review_request")
                                : getValue("check_details_and_be_on_time");
                        Alerter.create(activity).setTitle(titleMsg).setText(subtitleMsg).setTextAppearance(R.style.AlerterText).setTitleAppearance(R.style.AlerterTitle).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                    } else {
                        Alerter.create(activity).setTitle(getValue("invitation_cancel_successfully")).setTitleAppearance(R.style.AlerterTitle).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();

                    }

                    EventBus.getDefault().post(new PromoterEventModel());
                    EventBus.getDefault().post(new ComplimentaryProfileModel());
                    EventBus.getDefault().post(new NotificationModel());
                    requestPromoterEventDetailUser(eventId, false);
                }



//                ComplementaryProfileManager.shared.requestPromoterUpdateInviteStatus(activity);

            }
        });
    }

    private void requestPromoterToggleWishList(PromoterEventModel promoterEventModel) {
        DataService.shared(activity).requestPromoterToggleWishList(promoterEventModel.getId(), "event", new RestCallback<ContainerModel<CommonModel>>(this) {
            @Override
            public void result(ContainerModel<CommonModel> model, String error) {
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }

                int drawableId = promoterEventModel.isWishlisted()
                        ? R.drawable.icon_fav_unselected
                        : R.drawable.icon_fav_selected;

                binding.ivFavourite.setImageDrawable(ContextCompat.getDrawable(activity, drawableId));


                Alerter.create(activity).setText(binding.titleText.getText() + " " + model.message).setTextAppearance(R.style.AlerterText).setTitleAppearance(R.style.AlerterTitle).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                EventBus.getDefault().post(new PromoterEventModel());
                EventBus.getDefault().post(new ComplimentaryProfileModel());
                EventBus.getDefault().post(new NotificationModel());
                requestPromoterEventDetailUser(eventId, false);


            }
        });
    }

    private void requestPromoterEventDetailUser(String eventId, boolean showProgress) {
        if (showProgress) {
            showProgress();
        } else {
            binding.swipeRefreshLayout.setRefreshing(true);
        }

        DataService.shared(activity).requestPromoterEventDetailUser(eventId, new RestCallback<ContainerModel<PromoterEventModel>>(this) {
            @Override
            public void result(ContainerModel<PromoterEventModel> model, String error) {
                hideProgress();
                binding.swipeRefreshLayout.setRefreshing(false);
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.d("Id", "Ids: " + eventId);
                if (model.getData() != null) {
                    setDetail(model.getData());
                    setInviteStatus(model.getData());
                    binding.nestedScrollView.setVisibility(View.VISIBLE);
                    binding.eventLogoLayout.setVisibility(View.VISIBLE);
                    binding.eventDetailLayout.setVisibility(View.VISIBLE);
                    binding.allButtonMainLayout.setVisibility(View.VISIBLE);

                }

            }
        });
    }


    // endregion
    // --------------------------------------
    // region Promoter Event Data Service
    // --------------------------------------

    private void requestPromoterEventDetail(String eventId, boolean showProgress) {
        if (showProgress) {
            showProgress();
        } else {
            binding.swipeRefreshLayout.setRefreshing(true);
        }
        DataService.shared(activity).requestPromotereventDetail(eventId, new RestCallback<ContainerModel<PromoterEventModel>>(this) {
            @Override
            public void result(ContainerModel<PromoterEventModel> model, String error) {
                hideProgress();
                binding.swipeRefreshLayout.setRefreshing(false);
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (model.getData() != null) {
                    setDetail(model.getData());
                    binding.nestedScrollView.setVisibility(View.VISIBLE);
                    binding.eventLogoLayout.setVisibility(View.VISIBLE);
                    binding.eventDetailLayout.setVisibility(View.VISIBLE);
                    binding.allButtonMainLayout.setVisibility(View.VISIBLE);

                    if (isReloadEventListApi) {
                        EventBus.getDefault().post(new UserDetailModel());
                    }

                }

            }
        });
    }

    // endregion
    // --------------------------------------
    // region Event Cancel Data Service
    // --------------------------------------

    private void requestPromoterEventCancel(boolean deleteAllEvent) {
        if (Utils.isNullOrEmpty(eventId)) {
            return;
        }
        binding.btnCancelEvent.startProgress();
        DataService.shared(activity).requestPromoterEventCancel(eventId, deleteAllEvent, new RestCallback<ContainerModel<PromoterEventModel>>(this) {
            @Override
            public void result(ContainerModel<PromoterEventModel> model, String error) {
                binding.btnCancelEvent.stopProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (model.status == 1) {
                    isReloadEventListApi = true;
                    binding.btnEditEvent.setVisibility(View.GONE);
                    binding.btnCancelEvent.setVisibility(View.GONE);
                    binding.txtEventStatus.setVisibility(View.VISIBLE);
                }

            }
        });
    }

    private void requestPromoterEventComplete() {
        binding.btnCompleteEvent.startProgress();
        DataService.shared(activity).requestPromoterEventComplete(eventId, new RestCallback<ContainerModel<PromoterEventModel>>(this) {
            @Override
            public void result(ContainerModel<PromoterEventModel> model, String error) {
                binding.btnCompleteEvent.stopProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (model.status == 1) {
                    isReloadEventListApi = true;
                    requestPromoterEventDetail(eventId, false);
                    finish();
                }

            }
        });
    }

    private void requestPromoterEventHideShow(String eventId, Boolean isHidden) {
        binding.btnHideEvent.startProgress();
        DataService.shared(activity).requestPromotereventHideShow(eventId, isHidden, new RestCallback<ContainerModel<PromoterEventModel>>(this) {
            @Override
            public void result(ContainerModel<PromoterEventModel> model, String error) {
                binding.btnHideEvent.stopProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (model.status == 1) {
                    isReloadEventListApi = true;
                    requestPromoterEventDetail(eventId, false);
                }
                Alerter.create(activity).setTitle(model.message).setTextAppearance(R.style.AlerterText).setTitleAppearance(R.style.AlerterTitle).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();

            }
        });
    }

    private void requestPromoterCloseSport() {
        binding.btnCloseEvent.startProgress();
        DataService.shared(activity).requestPromoterCloseSport(eventId, new RestCallback<ContainerModel<PromoterEventModel>>(this) {
            @Override
            public void result(ContainerModel<PromoterEventModel> model, String error) {
                binding.btnCloseEvent.stopProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(activity, model.message, Toast.LENGTH_SHORT).show();
                if (type.equals("Promoter")) {
                    requestPromoterEventDetail(eventId, false);
                    binding.linearBtn.setVisibility(View.GONE);
                }


            }
        });
    }

    // endregion
    // --------------------------------------
    // region Event Share Link Create
    // --------------------------------------

    private void requestLinkCreate() {
        if (activity == null) {
            return;
        }

        String eventImage = "";
        String eventDescription = "";

        if (promoterEventModel.getVenueType().equals("venue") && promoterEventModel.getVenue() != null && !TextUtils.isEmpty(promoterEventModel.getVenue().getCover())) {
            eventImage = promoterEventModel.getVenue().getCover();
            eventDescription = promoterEventModel.getVenue().getAddress();
        } else {
            if (promoterEventModel.getCustomVenue() != null && !TextUtils.isEmpty(promoterEventModel.getCustomVenue().getImage())) {
                eventImage = promoterEventModel.getCustomVenue().getImage();
                eventDescription = promoterEventModel.getCustomVenue().getDescription();
            }
        }

        String formattedOutput = formatLists(promoterEventModel.getRequirementsAllowed(), promoterEventModel.getBenefitsIncluded());

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("title", binding.titleText.getText().toString());
        jsonObject.addProperty("description", eventDescription);
        jsonObject.addProperty("image", eventImage);
        jsonObject.addProperty("itemId", promoterEventModel.getId());
        jsonObject.addProperty("itemType", "promoter-event");

        DataService.shared(activity).requestLinkCreate(jsonObject, new RestCallback<ContainerModel<String>>(this) {

            @Override
            public void result(ContainerModel<String> model, String error) {
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }

                String shareMsg = String.format("%s\n\n%s\n\n%s\n\n%s",
                        jsonObject.get("title").getAsString(),
                        jsonObject.get("description").getAsString(),
                        formattedOutput,
                        model.getData());
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, shareMsg);
                activity.startActivity(Intent.createChooser(intent, "Share"));

            }
        });
    }


    private static String formatLists(List<String> requirements, List<String> benefits) {
        String requirementsString = requirements.stream()
                .map(req -> " • " + req)
                .collect(Collectors.joining("\n"));

        String benefitsString = benefits.stream()
                .map(ben -> " • " + ben)
                .collect(Collectors.joining("\n"));

        return "Requirements : \n" + requirementsString + "\n\nBenefits : \n" + benefitsString;
    }


    // endregion
    // --------------------------------------
    // region plus one event
    // --------------------------------------

    private void requestPlusOnePromoterEventDetail(String eventId, boolean showProgress) {
        if (showProgress) {
            showProgress();
        } else {
            binding.swipeRefreshLayout.setRefreshing(true);
        }
        DataService.shared(activity).requestPromoterPlusOneEvenDetail(eventId, new RestCallback<ContainerModel<PromoterEventModel>>(this) {
            @Override
            public void result(ContainerModel<PromoterEventModel> model, String error) {
                hideProgress();
                binding.swipeRefreshLayout.setRefreshing(false);
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (model.getData() != null) {
                    setDetail(model.getData());
                    setPlusOneInviteStatus(model.getData());
                    binding.nestedScrollView.setVisibility(View.VISIBLE);
                    binding.eventLogoLayout.setVisibility(View.VISIBLE);
                    binding.eventDetailLayout.setVisibility(View.VISIBLE);
                    binding.allButtonMainLayout.setVisibility(View.VISIBLE);

                }

            }
        });
    }


    private void requestPromoterPlusOneEvenInviteStatus(String inviteId, String inviteStatus) {

        if (inviteStatus.equals("in")) {
            binding.btnPlusIn.startProgress();
        } else {
            binding.btnPlusOneOut.startProgress();
        }

        DataService.shared(activity).requestPromoterPlusOneEvenInviteStatus(inviteId, inviteStatus, new RestCallback<ContainerModel<PromoterEventModel>>(this) {
            @Override
            public void result(ContainerModel<PromoterEventModel> model, String error) {
                if (inviteStatus.equals("in")) {
                    binding.btnPlusIn.stopProgress();
                } else {
                    binding.btnPlusOneOut.startProgress();
                }
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (inviteStatus.equals("in")) {
                    String titleMsg = (promoterEventModel != null && promoterEventModel.isConfirmationRequired())
                            ? getValue("show_intrest_promoter")
                            : getValue("thank_you_for_joining");

                    String subtitleMsg = (promoterEventModel != null && promoterEventModel.isConfirmationRequired())
                            ? getValue("admin_will_review_request")
                            : getValue("check_details_and_be_on_time");
                    Alerter.create(activity).setTitle(titleMsg).setText(subtitleMsg).setTextAppearance(R.style.AlerterText).setTitleAppearance(R.style.AlerterTitle).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                } else {
                    Alerter.create(activity).setTitle(model.message).setTitleAppearance(R.style.AlerterTitle).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();

                }


                EventBus.getDefault().post(new PromoterEventModel());
                EventBus.getDefault().post(new ComplimentaryProfileModel());
                requestPlusOnePromoterEventDetail(eventId, false);
            }
        });
    }


    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    private class RequireMentsAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            switch (viewType) {
                case 1:
                    return new RequirementsAllowedHolder(UiUtils.getViewBy(parent, R.layout.item_requirement_benefit));
                case 2:
                    return new RequirementsNotAllowedHolder(UiUtils.getViewBy(parent, R.layout.item_requirement_benefit));
                case 3:
                    return new SpacedHolder(UiUtils.getViewBy(parent, R.layout.item_space_design));
                default:
                    return new RequirementsAllowedHolder(UiUtils.getViewBy(parent, R.layout.item_requirement_benefit));
            }

        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            RatingModel model = (RatingModel) getItem(position);
            if (getItemViewType(position) == 1) {
                ((RequirementsAllowedHolder) holder).setupData(model);
            } else if (getItemViewType(position) == 2) {
                ((RequirementsNotAllowedHolder) holder).setupData(model);
            } else {
                ((SpacedHolder) holder).setupData(model);
            }
        }

        public int getItemViewType(int position) {
            RatingModel model = (RatingModel) getItem(position);
            if (model.getType().equals("allowed")) {
                return 1;
            } else if (model.getType().equals("notallowed")) {
                return 2;
            } else {
                return 3;
            }
        }

        public class RequirementsAllowedHolder extends RecyclerView.ViewHolder {

            private final ItemRequirementBenefitBinding binding;

            public RequirementsAllowedHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemRequirementBenefitBinding.bind(itemView);
            }

            public void setupData(RatingModel model) {

                binding.ivForAllowedOrNot.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.icon_add_requirements));
                binding.ivTitle.setText(model.getName());

            }
        }

        public class RequirementsNotAllowedHolder extends RecyclerView.ViewHolder {

            private final ItemRequirementBenefitBinding binding;

            public RequirementsNotAllowedHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemRequirementBenefitBinding.bind(itemView);
            }

            public void setupData(RatingModel model) {
                binding.ivForAllowedOrNot.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.icon_not_requirements));
                binding.ivTitle.setText(model.getName());
            }
        }

        public class SpacedHolder extends RecyclerView.ViewHolder {

            private final ItemSpaceDesignBinding binding;

            public SpacedHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemSpaceDesignBinding.bind(itemView);
            }

            public void setupData(RatingModel model) {
                binding.tvForSpace.setText(model.getName());
            }
        }

    }

    private class SocialItemListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_social_account_add_with_title));
        }

        @SuppressLint({"UseCompatLoadingForDrawables", "ClickableViewAccessibility"})
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

            ViewHolder viewHolder = (ViewHolder) holder;
            SocialAccountsToMentionModel model = (SocialAccountsToMentionModel) getItem(position);

            viewHolder.binding.socialEditText.setFocusable(false);
            viewHolder.binding.socialEditText.setClickable(true);
            viewHolder.binding.socialEditText.setCursorVisible(false);

            if (model.getTitle() != null && !model.getTitle().isEmpty()) {
                viewHolder.binding.titleAccount.setText(model.getTitle());
            } else {
                viewHolder.binding.titleAccount.setVisibility(View.GONE);
            }

            viewHolder.binding.socialEditText.setText(model.getAccount());

            if (!Utils.isNullOrEmpty(model.getPlatform())) {
                Drawable drawable = Utils.getPlatformIcon(model.getPlatform());
                viewHolder.binding.socialEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, null, ContextCompat.getDrawable(activity, R.drawable.icon_copy), null);
            } else {
                viewHolder.binding.socialEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null);
            }

            viewHolder.binding.roundLayout.setBackgroundColor(ContextCompat.getColor(activity, R.color.accounts_bg));

            viewHolder.binding.socialEditText.setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    EditText editText = viewHolder.binding.socialEditText;
                    Drawable endDrawable = editText.getCompoundDrawablesRelative()[2];
                    if (endDrawable != null) {
                        int drawableStart = editText.getWidth() - editText.getPaddingEnd() - endDrawable.getIntrinsicWidth();
                        if (event.getRawX() >= drawableStart) {
                            ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("label", model.getAccount());
                            clipboard.setPrimaryClip(clip);
                            Toast.makeText(activity, getValue("copied"), Toast.LENGTH_SHORT).show();
                            return true;
                        }
                    }
                }
                return false;
            });

            viewHolder.binding.socialEditText.setOnClickListener(v -> {
                Utils.preventDoubleClick(v);
                Utils.openSoicalSheet(activity,model.getPlatform(), model.getAccount());
            });

            viewHolder.binding.roundLayout.setOnClickListener(v -> {
                Utils.preventDoubleClick(v);
                Utils.openSoicalSheet(activity,model.getPlatform(), model.getAccount());
            });

        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            private final ItemSocialAccountAddWithTitleBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemSocialAccountAddWithTitleBinding.bind(itemView);
            }
        }
    }

    private class EventGalleryAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        private final List<ExoPlayer> players = new ArrayList<>();

        private final List<Integer> playingPositions = new ArrayList<>();


        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            switch (viewType) {
                case 1:
                    View view = UiUtils.getViewBy(parent, R.layout.item_event_image_design);
                    view.getLayoutParams().width = (int) (getItemCount() > 1 ? Graphics.getScreenWidth(activity) * 0.93 : Graphics.getScreenWidth(activity) * 0.95);
                    return new EventImageHolder(view);
                case 2:
                    View view2 = UiUtils.getViewBy(parent, R.layout.item_event_video_design);
                    view2.getLayoutParams().width = (int) (getItemCount() > 1 ? Graphics.getScreenWidth(activity) * 0.93 : Graphics.getScreenWidth(activity) * 0.95);
                    return new EventVideoHolder(view2);
                default:
                    return new EventImageHolder(UiUtils.getViewBy(parent, R.layout.item_event_image_design));
            }

        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            RatingModel model = (RatingModel) getItem(position);
            if (getItemViewType(position) == 1) {
                ((EventImageHolder) holder).setupData(model.getImage());
            } else if (getItemViewType(position) == 2) {
                EventVideoHolder videoHolder = (EventVideoHolder) holder;
                videoHolder.setupData(model.getImage());
                if (!players.contains(videoHolder.player)) {
                    players.add(videoHolder.player);
                }
            }
        }

        public int getItemViewType(int position) {
            RatingModel model = (RatingModel) getItem(position);
            if (isVideo(model.getImage())) {
                return 2;
            } else {
                return 1;
            }
        }

        public void pauseAllVideos() {
            playingPositions.clear();
            for (int i = 0; i < players.size(); i++) {
                ExoPlayer player = players.get(i);
                if (player.isPlaying()) {
                    playingPositions.add(i);
                    player.pause();
                }
            }
        }

        public void resumeAllVideos() {
            for (int position : playingPositions) {
                if (position >= 0 && position < players.size()) {
                    players.get(position).play();
                }
            }
            playingPositions.clear();
        }

        public void releaseAllPlayers() {
            for (ExoPlayer player : players) {
                player.release();
            }
            players.clear();
            playingPositions.clear();
        }

        private boolean isVideo(String url) {
            return url.endsWith(".mp4") || url.endsWith(".avi") || url.endsWith(".mov");
        }

        public class EventImageHolder extends RecyclerView.ViewHolder {

            private final ItemEventImageDesignBinding binding;

            public EventImageHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemEventImageDesignBinding.bind(itemView);

                binding.getRoot().setOnClickListener(view -> {
                    Utils.preventDoubleClick(view);
                    openGalleryView(getPosition());
                });
            }

            public void setupData(String imageUrl) {
                Graphics.loadImageWithFirstLetter(imageUrl, binding.eventImage, "W");
            }
        }

        public class EventVideoHolder extends RecyclerView.ViewHolder {

            private final ItemEventVideoDesignBinding binding;

            private ExoPlayer player;

            public EventVideoHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemEventVideoDesignBinding.bind(itemView);
                player = new ExoPlayer.Builder(itemView.getContext()).build();
                binding.eventVideoView.setPlayer(player);

                binding.iconSoundOff.setChecked(Preferences.shared.getBoolean("isMuteEventVideo"));
                boolean isMute = Preferences.shared.getBoolean("isMuteEventVideo");
                player.setVolume(isMute ? 0f : 1f);


                binding.getRoot().setOnClickListener(view -> {
                    Utils.preventDoubleClick(view);
                    openGalleryView(getPosition());
                });

                player.addListener(new Player.Listener() {
                    @Override
                    public void onPlaybackStateChanged(int playbackState) {
                        if (playbackState == Player.STATE_ENDED) {
                            player.seekTo(0);
                            player.pause();
                        }
                    }
                });


                binding.iconSoundOff.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (player != null) {
                        player.setVolume(!isChecked ? 1f : 0f);
                        Preferences.shared.setBoolean("isMuteEventVideo", isChecked);
                    }
                });

            }

            public void setupData(String videoUrl) {
                player.setMediaItem(MediaItem.fromUri(videoUrl));
                player.prepare();
                player.pause();
            }

            public void startVideo() {
                player.play();
            }

            public void pauseVideo() {
                player.pause();
            }

        }

        private void openGalleryView(int position){
            startActivity(new Intent(activity, EventGalleryViewActivity.class).putExtra("model",new Gson().toJson(promoterEventModel)).putExtra("scrollToPosition",position));
        }
    }

    // --------------------------------------
    // endregion

}
