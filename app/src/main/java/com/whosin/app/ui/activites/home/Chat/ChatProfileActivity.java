package com.whosin.app.ui.activites.home.Chat;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.tapadoo.alerter.Alerter;
import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.comman.ui.roundcornerlayout.CornerType;
import com.whosin.app.databinding.ActivityChatProfileBinding;
import com.whosin.app.databinding.ImageListBinding;
import com.whosin.app.databinding.InviteContactFreindItemBinding;
import com.whosin.app.databinding.MediaItemBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.Repository.ChatRepository;
import com.whosin.app.service.manager.AppSettingManager;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.BucketChatMainProfileModel;
import com.whosin.app.service.models.BucketEventListModel;
import com.whosin.app.service.models.BucketListModel;
import com.whosin.app.service.models.ChatMessageModel;
import com.whosin.app.service.models.ChatModel;
import com.whosin.app.service.models.CommonModel;
import com.whosin.app.service.models.ContactListModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.CreateBucketListModel;
import com.whosin.app.service.models.EventDetailModel;
import com.whosin.app.service.models.EventGuestListModel;
import com.whosin.app.service.models.FollowUnfollowModel;
import com.whosin.app.service.models.InviteFriendModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.Profile.FollowingActivity;
import com.whosin.app.ui.activites.Profile.FollowresActivity;
import com.whosin.app.ui.activites.Profile.OtherUserProfileActivity;
import com.whosin.app.ui.activites.bucket.MyInvitationActivity;
import com.whosin.app.ui.activites.comman.BaseActivity;
import com.whosin.app.ui.activites.home.event.EventDetailsActivity;
import com.whosin.app.ui.activites.reportedUser.ReportedUseSuccessDialog;
import com.whosin.app.ui.activites.venue.Bucket.BucketListDetailActivity;
import com.whosin.app.ui.activites.venue.VenueGalleryActivity;
import com.whosin.app.ui.fragment.Chat.ReportAndBlockBottomSheet;
import com.whosin.app.ui.fragment.Chat.ReportBottomSheet;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import io.realm.RealmList;

public class ChatProfileActivity extends BaseActivity {

    private ActivityChatProfileBinding binding;
    private final MediaImageAdapter<ChatMessageModel> mediaImageAdapter = new MediaImageAdapter<>();
    private BucketChatMainProfileModel bucketModelList;
    private CreateBucketListModel bucketListModel = new CreateBucketListModel();
    private final InviteFriendAdapter inviteFriendAdapter = new InviteFriendAdapter<>();
    private UserDetailModel userDetailModel;

    private ChatModel chatModel;

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------


    @Override
    protected void initUi() {

        applyTranslations();

        String chatModelString = getIntent().getStringExtra( "chatModel" );
        chatModel = new Gson().fromJson( chatModelString, ChatModel.class );
        if (chatModel == null) {
            finish();
            return;
        }

        if (chatModel.getChatType().equals( "friend" )) {
            UserDetailModel userDetail = chatModel.getUser();
            if (userDetail != null) {
                binding.constraintHeader.setVisibility( View.VISIBLE );
                userDetailModel = userDetail;
                setChatProfileDetalis( userDetailModel );
            }
            String userId = chatModel.getMembers().stream().filter( p -> !p.equals( SessionManager.shared.getUser().getId() ) ).findAny().orElse( "" );
            if (!userId.isEmpty()) {
                requestUserProfile( userId );
            } else {
                Toast.makeText( activity, "User not found", Toast.LENGTH_SHORT ).show();
                finish();
            }
        } else if (chatModel.getChatType().equals( "event" )) {
            binding.progress.setVisibility(View.VISIBLE);
            BucketListModel chatList = ChatRepository.shared( ChatProfileActivity.this ).getGroupChatFromCache();
            if (chatList != null) {
                Optional<BucketEventListModel> outingModel = chatList.getEventModels().stream().filter(p -> p.getId().equals( chatModel.getChatId() ) ).findAny();
                if (outingModel.isPresent()) {
                    binding.constraintHeader.setVisibility( View.VISIBLE );
                    setEventDetail( outingModel.get() );
                }
            }
            requestEventDetail(chatModel.getChatId());
        } else if (chatModel.getChatType().equals( "outing" )) {
            BucketListModel chatList = ChatRepository.shared( ChatProfileActivity.this ).getGroupChatFromCache();
            if (chatList != null) {
                Optional<InviteFriendModel> outingModel = chatList.getOutingModels().stream().filter( p -> p.getId().equals( chatModel.getChatId() ) ).findAny();
                if (outingModel.isPresent()) {
                    setOutingDetail( outingModel.get() );
                }
            }
        }else if (chatModel.getChatType().equals( "promoter_event" )) {
            if (chatModel.isComplementry() && !Utils.isNullOrEmpty(chatModel.getEventOwnerIdForCm())) {
                requestUserProfile(chatModel.getEventOwnerIdForCm());
            }
        }
        setMediaImageAdapter();
    }

    @Override
    protected void setListeners() {
        binding.imgBack.setOnClickListener( v -> {
            onBackPressed();
        });

        binding.tvBlock.setOnClickListener( v -> {
            if (chatModel.getChatType().equals( "friend" )) {
                ReportAndBlockBottomSheet bottomSheet = new ReportAndBlockBottomSheet();
                bottomSheet.reportSheetCallBack = data -> {
                    if (data) {
                        ReportBottomSheet reportBottomSheet = new ReportBottomSheet();
                        reportBottomSheet.chatModel = chatModel;
                        reportBottomSheet.isFromChat = true;
                        reportBottomSheet.isOnlyReport = true;
                        reportBottomSheet.callback = data1 -> {
                            ReportedUseSuccessDialog dialog = new ReportedUseSuccessDialog();
                            dialog.callBack = data2 -> {
                                if (AppSettingManager.shared.venueReloadCallBack != null) {
                                    AppSettingManager.shared.venueReloadCallBack.onReceive(true);
                                    finish();
                                }
                            };
                            dialog.show(getSupportFragmentManager(),"");
                        };
                        reportBottomSheet.show(getSupportFragmentManager(), "");
                    }
                };
                bottomSheet.reportAndBlockCallBack = data -> {
                    if (data) {
                        ReportBottomSheet reportBottomSheet = new ReportBottomSheet();
                        reportBottomSheet.chatModel = chatModel;
                        reportBottomSheet.isFromChat = true;
                        reportBottomSheet.callback = data1 -> {
                            ReportedUseSuccessDialog dialog = new ReportedUseSuccessDialog();
                            dialog.callBack = data2 -> {
                                requestBlockUserAdd();
                            };
                            dialog.show(getSupportFragmentManager(),"");

                        };
                        reportBottomSheet.show(getSupportFragmentManager(), "");
                    }
                };
                bottomSheet.callback = data -> {
                    if (data) {
                        Graphics.showAlertDialogWithOkCancel(activity, activity.getString(R.string.app_name), Utils.setLangValue("block_user_alert",chatModel.getUser().getFullName()), Utils.getLangValue("yes"), Utils.getLangValue("no"), isConfirmed -> {
                            if (isConfirmed) {
                                requestBlockUserAdd();
                            }
                        });
                    }
                };
                bottomSheet.show(getSupportFragmentManager(), "");
            }
            else {
                Graphics.showAlertDialogWithOkCancel( activity, getString(R.string.app_name), "Are you sure want to clear the chat?", aBoolean -> {
                    if (aBoolean) {
                        requestDeleteChat(chatModel.getChatId());
                    }
                });
            }
        });
        binding.tvReport.setOnClickListener( v -> {
            if (chatModel.getChatType().equals( "friend" )) {
                ReportAndBlockBottomSheet bottomSheet = new ReportAndBlockBottomSheet();
                bottomSheet.reportSheetCallBack = data -> {
                    if (data) {
                        ReportBottomSheet reportBottomSheet = new ReportBottomSheet();
                        reportBottomSheet.chatModel = chatModel;
                        reportBottomSheet.isFromChat = true;
                        reportBottomSheet.isOnlyReport = true;
                        reportBottomSheet.callback = data1 -> {
                            ReportedUseSuccessDialog dialog = new ReportedUseSuccessDialog();
                            dialog.callBack = data2 -> {
                                if (AppSettingManager.shared.venueReloadCallBack != null) {
                                    AppSettingManager.shared.venueReloadCallBack.onReceive(true);
                                    finish();
                                }
                            };
                            dialog.show(getSupportFragmentManager(),"");
                        };
                        reportBottomSheet.show(getSupportFragmentManager(), "");
                    }
                };
                bottomSheet.reportAndBlockCallBack = data -> {
                    if (data) {
                        ReportBottomSheet reportBottomSheet = new ReportBottomSheet();
                        reportBottomSheet.chatModel = chatModel;
                        reportBottomSheet.isFromChat = true;
                        reportBottomSheet.callback = data1 -> {
                            ReportedUseSuccessDialog dialog = new ReportedUseSuccessDialog();
                            dialog.callBack = data2 -> {
                                requestBlockUserAdd();
                            };
                            dialog.show(getSupportFragmentManager(),"");

                        };
                        reportBottomSheet.show(getSupportFragmentManager(), "");
                    }
                };
                bottomSheet.callback = data -> {
                    if (data) {
                        Graphics.showAlertDialogWithOkCancel(activity, activity.getString(R.string.app_name), Utils.setLangValue("block_user_alert",chatModel.getUser().getFullName()), Utils.getLangValue("yes"), Utils.getLangValue("no"), isConfirmed -> {
                            if (isConfirmed) {
                                requestBlockUserAdd();
                            }
                        });
                    }
                };
                bottomSheet.show(getSupportFragmentManager(), "");
            }
            else if (chatModel.getChatType().equals( "bucket" )) {
                Graphics.showAlertDialogWithOkCancel( activity, getString(R.string.app_name), "Are you sure you want to exit the chat?", aBoolean -> {
                    if (aBoolean) {
                        requestExitBucket();
                    }
                });
            }
            else {
                Graphics.showAlertDialogWithOkCancel( activity, getString(R.string.app_name), "Are you sure you want to exit the chat?", aBoolean -> {
                    if (aBoolean) {

                    }
                });
            }
        } );


        binding.changBgLayout.setOnClickListener( view -> {
            Intent intent = new Intent( activity, ChatWallpaperActivity.class );
            intent.putExtra( "id", chatModel.getChatId() );
            activityLauncher.launch( intent, result -> finish() );
        } );

        binding.followingLayout.setOnClickListener( view -> {
            String userId = chatModel.getMembers().stream().filter( p -> !p.equals( SessionManager.shared.getUser().getId() ) ).findAny().orElse( "" );
            if (userId.isEmpty()){return;}
            startActivity( new Intent( activity, FollowingActivity.class ).putExtra( "id", userId ) );
        } );
        binding.followersLayout.setOnClickListener(view -> {
            String userId = chatModel.getMembers().stream().filter( p -> !p.equals( SessionManager.shared.getUser().getId() ) ).findAny().orElse( "" );
            if (userId.isEmpty()){return;}
            startActivity(new Intent(activity, FollowresActivity.class).putExtra("id", userId));
        });


    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityChatProfileBinding.inflate( getLayoutInflater() );
        return binding.getRoot();
    }


    @Override
    protected Map<View, String> getTranslationMap() {
        Map<View, String> map = new HashMap<>();
        map.put(binding.tvFollowersTitle, "followers");
        map.put(binding.tvFollowingTitle, "following");
        map.put(binding.tvChangeChatBgTitle, "change_chat_background");
        map.put(binding.tvMediaTitle, "media");
        map.put(binding.tvMediaSeeAll, "see_all");

        return map;
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void setChatProfileDetalis(UserDetailModel userDetailModel) {
        String name = userDetailModel.getFirstName() + " " + userDetailModel.getLastName();
        binding.tvName.setText( name );
        Graphics.loadImageWithFirstLetter( userDetailModel.getImage(), binding.ivProfile, userDetailModel.getFullName() );
        binding.tvBlock.setText( getValue("block") + " " + name );
        binding.tvReport.setText( getValue("report") + " " + name );
        binding.followersCount.setText( String.valueOf( userDetailModel.getFollower() ) );
        binding.followingCount.setText( String.valueOf( userDetailModel.getFollowing() ) );

        binding.ivProfile.setOnClickListener( v -> startActivity( new Intent( activity, OtherUserProfileActivity.class ).putExtra( "friendId", userDetailModel.getId() ) ));
        binding.linearName.setOnClickListener( v -> startActivity( new Intent( activity, OtherUserProfileActivity.class ).putExtra( "friendId", userDetailModel.getId() ) ));

    }

    private void setMediaImageAdapter() {
        binding.chatMediaRecycler.setLayoutManager( new LinearLayoutManager( activity, LinearLayoutManager.HORIZONTAL, false ) );
        binding.chatMediaRecycler.setAdapter( mediaImageAdapter );

        List<ChatMessageModel> imageMessages = ChatRepository.shared( this ).getMediaMessages( chatModel.getChatId() );
        if (imageMessages != null) {
            binding.mediaLayout.setVisibility( View.VISIBLE );
            binding.chatMediaRecycler.setVisibility( View.VISIBLE );
            mediaImageAdapter.updateData( imageMessages );
            List<String> msgList = imageMessages.stream()
                    .map(ChatMessageModel::getMsg)
                    .filter(msg -> !TextUtils.isEmpty(msg))
                    .collect(Collectors.toList());
            binding.tvMediaSeeAll.setOnClickListener( v ->{
                startActivity(new Intent(activity, VenueGalleryActivity.class).putExtra("galleries", new Gson()
                        .toJson(msgList)));

            } );

        } else {
            binding.mediaLayout.setVisibility( View.GONE );
            binding.chatMediaRecycler.setVisibility( View.GONE );
        }


    }

    private void setBucketList(CreateBucketListModel bucketListModel) {
        binding.bucketTitle.setText(getValue("members"));
        binding.tvName.setText( bucketListModel.getName() );
        Graphics.loadImageWithFirstLetter( bucketListModel.getCoverImage(), binding.ivProfile, bucketListModel.getName() );
        binding.tvBlock.setText( getValue("clear_chat") );
        binding.tvReport.setText(getValue("exit_group"));
        binding.linearFollowing.setVisibility( View.GONE );

        binding.bucketRecycler.setLayoutManager( new LinearLayoutManager( activity, LinearLayoutManager.VERTICAL, false ) );
        binding.bucketRecycler.setAdapter( inviteFriendAdapter );
        binding.bucketRecycler.setNestedScrollingEnabled( false );
        inviteFriendAdapter.updateData( bucketListModel.getSharedWith() );
        binding.ivProfile.setOnClickListener( view -> startActivity( new Intent( activity, BucketListDetailActivity.class ).putExtra( "bucketId", bucketListModel.getId() ).putExtra( "name", bucketListModel.getName() ) ) );
        binding.linearName.setOnClickListener( view -> startActivity( new Intent( activity, BucketListDetailActivity.class ).putExtra( "bucketId", bucketListModel.getId() ).putExtra( "name", bucketListModel.getName() ) ) );
    }

    private void setEventList(EventDetailModel eventDetailModel) {
        binding.linearFollowing.setVisibility( View.GONE );

        binding.bucketTitle.setText(getValue("members"));
        binding.tvName.setText( eventDetailModel.getEventModel().getTitle() );
        Graphics.loadImageWithFirstLetter( eventDetailModel.getEventModel().getImage(), binding.ivProfile, eventDetailModel.getEventModel().getTitle() );
        binding.tvBlock.setText( getValue("clear_chat") );
        binding.tvReport.setText(getValue("exit_group"));
        if (eventDetailModel.getEventModel().getCustomVenueObject() != null) {
            binding.venueContainer.setVisibility(View.VISIBLE);
            binding.venueContainer.setVenueDetail(eventDetailModel.getEventModel().getCustomVenueObject());
        } else {
            binding.venueContainer.setVisibility(View.GONE);
        }

        if (eventDetailModel.getEventModel().getOrgData() != null){
            binding.eventOrganizeName.setVisibility(View.VISIBLE);
            binding.eventOrganizeName.setText(eventDetailModel.getEventModel().getOrgData().getName());
            if (eventDetailModel.getEventModel().getOrgData().getId().equals(SessionManager.shared.getUser().getId())){
                binding.changBgLayout.setVisibility(View.VISIBLE);
            }else {
                binding.changBgLayout.setVisibility( View.GONE );

            }
        }else {
            binding.eventOrganizeName.setVisibility(View.GONE);
        }

        binding.bucketRecycler.setLayoutManager( new LinearLayoutManager( activity, LinearLayoutManager.VERTICAL, false ) );
        binding.bucketRecycler.setAdapter( inviteFriendAdapter );
        binding.bucketRecycler.setNestedScrollingEnabled( false );
        requestEventGuestList(eventDetailModel.getEventModel().getId());


        binding.ivProfile.setOnClickListener( view -> {
            String orgName = (eventDetailModel.getEventModel().getOrgData() != null && eventDetailModel.getEventModel().getOrgData().getName() != null) ? eventDetailModel.getEventModel().getOrgData().getName() : "";
            String orgWebsite = (eventDetailModel.getEventModel().getOrgData() != null && eventDetailModel.getEventModel().getOrgData().getWebsite() != null) ? eventDetailModel.getEventModel().getOrgData().getWebsite() : "";
            String orgLogo = (eventDetailModel.getEventModel().getOrgData() != null && eventDetailModel.getEventModel().getOrgData().getLogo() != null) ? eventDetailModel.getEventModel().getOrgData().getLogo() : "";

            startActivity( new Intent( activity, EventDetailsActivity.class )
                    .putExtra( "eventId", eventDetailModel.getEventModel().getId() )
                    .putExtra( "name", orgName )
                    .putExtra( "address", orgWebsite )
                    .putExtra( "image", orgLogo )
            );
        } );
        binding.linearName.setOnClickListener( view -> {
            String orgName = (eventDetailModel.getEventModel().getOrgData() != null && eventDetailModel.getEventModel().getOrgData().getName() != null) ? eventDetailModel.getEventModel().getOrgData().getName() : "";
            String orgWebsite = (eventDetailModel.getEventModel().getOrgData() != null && eventDetailModel.getEventModel().getOrgData().getWebsite() != null) ? eventDetailModel.getEventModel().getOrgData().getWebsite() : "";
            String orgLogo = (eventDetailModel.getEventModel().getOrgData() != null && eventDetailModel.getEventModel().getOrgData().getLogo() != null) ? eventDetailModel.getEventModel().getOrgData().getLogo() : "";

            startActivity( new Intent( activity, EventDetailsActivity.class )
                    .putExtra( "eventId", eventDetailModel.getEventModel().getId() )
                    .putExtra( "name", orgName )
                    .putExtra( "address", orgWebsite )
                    .putExtra( "image", orgLogo )
            );
        } );

    }
    private void setEventDetail(BucketEventListModel eventDetailModel) {
        binding.linearFollowing.setVisibility( View.GONE );

        binding.bucketTitle.setText(getValue("members"));
        binding.tvName.setText( eventDetailModel.getTitle() );
        Graphics.loadImageWithFirstLetter( eventDetailModel.getImage(), binding.ivProfile, eventDetailModel.getTitle() );
        binding.tvBlock.setText( getValue("clear_chat") );
        binding.tvReport.setText(getValue("exit_group"));
        if (eventDetailModel.getVenue() != null) {
            binding.venueContainer.setVisibility(View.VISIBLE);
            binding.venueContainer.setVenueDetail(eventDetailModel.getVenue());
        }else {
            binding.venueContainer.setVisibility(View.GONE);
        }

        if (eventDetailModel.getOrg() != null){
            binding.eventOrganizeName.setVisibility(View.VISIBLE);
            binding.eventOrganizeName.setText(eventDetailModel.getOrg().getName());
            if (eventDetailModel.getOrg().getId().equals(SessionManager.shared.getUser().getId())){
                binding.changBgLayout.setVisibility(View.VISIBLE);
            }else {
                binding.changBgLayout.setVisibility( View.GONE );

            }
        }else {
            binding.eventOrganizeName.setVisibility(View.GONE);
        }

        binding.bucketRecycler.setLayoutManager( new LinearLayoutManager( activity, LinearLayoutManager.VERTICAL, false ) );
        binding.bucketRecycler.setAdapter( inviteFriendAdapter );
        binding.bucketRecycler.setNestedScrollingEnabled( false );
        requestEventGuestList(eventDetailModel.getId());


        binding.ivProfile.setOnClickListener( view -> {
            String orgName = (eventDetailModel.getOrg() != null && eventDetailModel.getOrg().getName() != null) ? eventDetailModel.getOrg().getName() : "";
            String orgWebsite = (eventDetailModel.getOrg() != null && eventDetailModel.getOrg().getWebsite() != null) ? eventDetailModel. getOrg().getWebsite() : "";
            String orgLogo = (eventDetailModel.getOrg() != null && eventDetailModel.getOrg().getLogo() != null) ? eventDetailModel.getOrg().getLogo() : "";

            startActivity( new Intent( activity, EventDetailsActivity.class )
                    .putExtra( "eventId", eventDetailModel.getId() )
                    .putExtra( "name", orgName )
                    .putExtra( "address", orgWebsite )
                    .putExtra( "image", orgLogo )
            );
        } );
        binding.linearName.setOnClickListener( view -> {
            String orgName = (eventDetailModel.getOrg() != null && eventDetailModel.getOrg().getName() != null) ? eventDetailModel.getOrg().getName() : "";
            String orgWebsite = (eventDetailModel.getOrg() != null && eventDetailModel.getOrg().getWebsite() != null) ? eventDetailModel.getOrg().getWebsite() : "";
            String orgLogo = (eventDetailModel.getOrg() != null && eventDetailModel.getOrg().getLogo() != null) ? eventDetailModel.getOrg().getLogo() : "";

            startActivity( new Intent( activity, EventDetailsActivity.class )
                    .putExtra( "eventId", eventDetailModel.getId() )
                    .putExtra( "name", orgName )
                    .putExtra( "address", orgWebsite )
                    .putExtra( "image", orgLogo )
            );
        } );

    }



    private void setOutingDetail(InviteFriendModel outingModel) {
        binding.constraintHeader.setVisibility( View.VISIBLE );
        binding.linearFollowing.setVisibility( View.GONE );

        binding.bucketTitle.setText(getValue("members"));
        binding.tvName.setText( outingModel.getTitle() );
        if (outingModel.getVenue() != null) {
            Graphics.loadImageWithFirstLetter( outingModel.getVenue().getCover(), binding.ivProfile, outingModel.getTitle() );
        } else {
            Graphics.loadImageWithFirstLetter( "", binding.ivProfile, outingModel.getTitle() );
        }
        binding.tvBlock.setText( getValue("clear_chat") );
        binding.tvReport.setText(getValue("exit_group"));
        if (outingModel.getUser() != null){
            binding.eventOrganizeName.setVisibility(View.VISIBLE);
            binding.eventOrganizeName.setText(outingModel.getUser().getFullName());

        }else {
            binding.eventOrganizeName.setVisibility(View.GONE);
        }

        binding.bucketRecycler.setLayoutManager( new LinearLayoutManager( activity, LinearLayoutManager.VERTICAL, false ) );
        binding.bucketRecycler.setAdapter( inviteFriendAdapter );
        binding.bucketRecycler.setNestedScrollingEnabled( false );
        inviteFriendAdapter.updateData( outingModel.getInvitedUser()
        );
        binding.ivProfile.setOnClickListener( view -> {
            startActivity( new Intent( activity, MyInvitationActivity.class ).putExtra("id", outingModel.getId()).putExtra("notificationType", "notification"));
        } );
        binding.linearName.setOnClickListener( view -> {
            startActivity( new Intent( activity, MyInvitationActivity.class ).putExtra("id", outingModel.getId()).putExtra("notificationType", "notification"));
        } );

    }

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------
    private void requestEventGuestList(String eventId) {

        DataService.shared( activity ).requestEventGuestList( 1,30, eventId, "", new RestCallback<ContainerModel<EventGuestListModel>>(this) {
            @Override
            public void result(ContainerModel<EventGuestListModel> model, String error) {
                binding.progress.setVisibility(View.GONE);
                if (!Utils.isNullOrEmpty( error ) || model == null) {
                    Toast.makeText( activity, error, Toast.LENGTH_SHORT ).show();
                    return;

                }

                if (model.getData() != null){
                    inviteFriendAdapter.updateData( model.getData().getUserModel());

                }
            }
        } );
    }

    private void requestEventDetail(String eventId) {
        DataService.shared( activity ).requestEventDetail( eventId, new RestCallback<ContainerModel<EventDetailModel>>(this) {
            @Override
            public void result(ContainerModel<EventDetailModel> model, String error) {

                if (!Utils.isNullOrEmpty( error ) || model == null) {
                    Toast.makeText( activity, error, Toast.LENGTH_SHORT ).show();
                    return;
                }

                if (model.getData() != null) {
                    setEventList( model.getData() );
                    binding.constraintHeader.setVisibility( View.VISIBLE );
                }

            }
        } );
    }

    private void requestBlockUserAdd() {
        showProgress();
        DataService.shared( activity ).requestBlockUser( userDetailModel.getId(), new RestCallback<ContainerModel<CommonModel>>(this) {
            @Override
            public void result(ContainerModel<CommonModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty( error ) || model == null) {
                    Toast.makeText( activity, error, Toast.LENGTH_SHORT ).show();
                    return;
                }
                Alerter.create( activity ).setTitle(getValue("oh_snap")).setText( getValue("you_have_blocked") + userDetailModel.getFullName() ).setTextAppearance( R.style.AlerterText ).setTitleAppearance( R.style.AlerterTitle ).setBackgroundColorRes( R.color.AlerterSuccessBg ).hideIcon().show();
                if (AppSettingManager.shared.venueReloadCallBack != null) {
                    AppSettingManager.shared.venueReloadCallBack.onReceive(true);
                    finish();
                }

            }
        } );
    }

    private void requestDeleteChat(String chatId) {
        showProgress();
        DataService.shared( activity ).requestDeleteChat(chatId, new RestCallback<ContainerModel<ChatModel>>(this) {
            @Override
            public void result(ContainerModel<ChatModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty( error ) || model == null) {
                    Toast.makeText( activity, error, Toast.LENGTH_SHORT ).show();
                    return;
                }
                ChatRepository.shared( activity ).clearChat( chatId, data -> {
                    Intent intent = new Intent();
                    intent.putExtra("close",true);
                    intent.putExtra("type","clear");
                    intent.putExtra("id",chatId);
                    setResult(RESULT_OK, intent);
                    finish();
                });

            }
        } );
    }

    private void requestReportUserAdd() {
        showProgress();
        DataService.shared( activity ).requestReportUser( SessionManager.shared.getUser().getId(), "black mail", new RestCallback<ContainerModel<CommonModel>>(this) {
            @Override
            public void result(ContainerModel<CommonModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty( error ) || model == null) {
                    Toast.makeText( activity, error, Toast.LENGTH_SHORT ).show();
                    return;
                }
                Alerter.create( activity ).setTitle( "Thank you!" ).setText( "Report Created" ).setTextAppearance( R.style.AlerterText ).setTitleAppearance( R.style.AlerterTitle ).setBackgroundColorRes( R.color.AlerterSuccessBg ).hideIcon().show();
            }
        } );
    }

    private void requestExitBucket() {
        showProgress();
        DataService.shared( activity ).requestBucketExit( chatModel.getChatId(), new RestCallback<ContainerModel<CommonModel>>(this) {
            @Override
            public void result(ContainerModel<CommonModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty( error ) || model == null) {
                    Toast.makeText( activity, error, Toast.LENGTH_SHORT ).show();
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra("close",true);
                intent.putExtra("type","clear");
                intent.putExtra("id",chatModel.getChatId());
                setResult(RESULT_OK, intent);
                finish();
            }
        } );
    }

    private void requestUserProfile(String id) {
        DataService.shared( activity ).requestUserProfile( id, new RestCallback<ContainerModel<UserDetailModel>>(this) {
            @Override
            public void result(ContainerModel<UserDetailModel> model, String error) {
                if (!Utils.isNullOrEmpty( error ) || model == null) {
                    Toast.makeText( activity, error, Toast.LENGTH_SHORT ).show();
                    return;
                }
                binding.constraintHeader.setVisibility( View.VISIBLE );
                userDetailModel = model.getData();
                setChatProfileDetalis( userDetailModel );
            }
        } );
    }

    private void reqFollowUnFollow(ContactListModel userDetailModel) {
        showProgress();
        DataService.shared( activity ).requestUserFollowUnFollow( userDetailModel.getId(), new RestCallback<ContainerModel<FollowUnfollowModel>>(this) {
            @Override
            public void result(ContainerModel<FollowUnfollowModel> model, String error) {
                hideProgress();
                if (!Utils.isValidActivity(activity)) { return; }
                if (!Utils.isNullOrEmpty( error ) || model == null || model.getData() == null) {
                    Toast.makeText( activity, R.string.service_message_something_wrong, Toast.LENGTH_SHORT ).show();
                    return;
                }
                userDetailModel.setFollow(model.getData().getStatus());
                switch (model.getData().getStatus()) {
                    case "unfollowed":
                        Alerter.create(activity).setTitle("Oh Snap!").setTitleAppearance(R.style.AlerterTitle).setTextAppearance(R.style.AlerterText).setText("You have unfollowed " + userDetailModel.getFullName()).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                        break;
                    case "approved":
                        Alerter.create(activity).setTitle("Thank you!").setText("For following " + userDetailModel.getFullName()).setTextAppearance(R.style.AlerterText).setTitleAppearance(R.style.AlerterTitle).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                        break;
                    case "pending":
                        Alerter.create(activity).setTitle("Thank you!").setText("You have requested for follow " + userDetailModel.getFullName()).setTextAppearance(R.style.AlerterText).setTitleAppearance(R.style.AlerterTitle).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                        break;
                    case "cancelled":
                        Alerter.create(activity).setTitle("Oh Snap!").setText("You have cancelled follow request of " + userDetailModel.getFullName()).setTextAppearance(R.style.AlerterText).setTitleAppearance(R.style.AlerterTitle).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                        break;
                }
                inviteFriendAdapter.notifyDataSetChanged();
            }
        } );
    }


    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    public class MediaImageAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder( UiUtils.getViewBy( parent, R.layout.media_item ) );
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            ChatMessageModel model = (ChatMessageModel) getItem( position );
            Graphics.loadImage( model.getMsg(), viewHolder.binding.ivMedia );

        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final MediaItemBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super( itemView );
                binding = MediaItemBinding.bind( itemView );
            }
        }
    }


    public class InviteFriendAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ContactHolder( UiUtils.getViewBy( parent, R.layout.invite_contact_freind_item ) );
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            ContactHolder viewHolder = (ContactHolder) holder;
            ContactListModel listModel = (ContactListModel) getItem( position );

            viewHolder.vBinding.ivCheck.setVisibility( View.GONE );
            viewHolder.vBinding.optionContainer.setVisibility( View.GONE );
            viewHolder.vBinding.tvUserNumber.setVisibility( View.GONE );

            viewHolder.vBinding.constrain.setCornerRadius(0, CornerType.ALL);
            boolean isFirstCell = false;
            boolean isLastCell = getItemCount() - 1 == position;

            if (position == 0) {
                isFirstCell = true;
            }

            float cornerRadius = activity.getResources().getDimensionPixelSize(com.intuit.sdp.R.dimen._10sdp);
            if (isFirstCell) {
                viewHolder.vBinding.constrain.setCornerRadius(cornerRadius, CornerType.TOP_LEFT);
                viewHolder.vBinding.constrain.setCornerRadius(cornerRadius, CornerType.TOP_RIGHT);
            }
            if (isLastCell) {
                viewHolder.vBinding.constrain.setCornerRadius(cornerRadius, CornerType.BOTTOM_RIGHT);
                viewHolder.vBinding.constrain.setCornerRadius(cornerRadius, CornerType.BOTTOM_LEFT);
            }
            viewHolder.vBinding.view.setVisibility(isLastCell ? View.GONE  : View.VISIBLE);


            viewHolder.vBinding.tvUserName.setText( listModel.getFullName() );
            Graphics.loadImageWithFirstLetter( listModel.getImage(), viewHolder.vBinding.ivUserProfile, listModel.getFullName() );
            if (!TextUtils.isEmpty( listModel.getEmail() )) {
                viewHolder.vBinding.tvUserNumber.setVisibility( View.VISIBLE );
                viewHolder.vBinding.tvUserNumber.setText( listModel.getEmail() );
            }

            if (!listModel.getId().equals( SessionManager.shared.getUser().getId() )) {
                viewHolder.vBinding.optionContainer.setVisibility(View.VISIBLE);
                viewHolder.vBinding.optionContainer.setupView(listModel, activity, data -> {
                    inviteFriendAdapter.notifyDataSetChanged();
                });

                viewHolder.vBinding.getRoot().setOnClickListener( v -> {
                    startActivity( new Intent( activity, OtherUserProfileActivity.class ).putExtra( "friendId", listModel.getId() ) );
                } );
            }
        }

        public class ContactHolder extends RecyclerView.ViewHolder {
            private final InviteContactFreindItemBinding vBinding;

            public ContactHolder(@NonNull View itemView) {
                super( itemView );
                vBinding = InviteContactFreindItemBinding.bind( itemView );
            }
        }


        private void openChat(ContactListModel listModel) {
            ChatModel chatModel = new ChatModel();
            chatModel.setImage(listModel.getImage());
            chatModel.setTitle(listModel.getFullName());
            chatModel.setChatType("friend");
            RealmList<String> idList = new RealmList<>();
            idList.add(SessionManager.shared.getUser().getId());
            idList.add(listModel.getId());
            chatModel.setMembers(idList);
            Collections.sort(idList);
            String joinedString = String.join(",", idList);
            chatModel.setChatId(joinedString);

            Intent intent = new Intent(activity, ChatMessageActivity.class);
            intent.putExtra("chatModel", new Gson().toJson(chatModel));
            intent.putExtra("isChatId", true);
            intent.putExtra("type", "friend");
            startActivity(intent);
        }



    }


    public class PlayerAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = UiUtils.getViewBy( parent, R.layout.image_list );
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) view.getLayoutParams();
            layoutParams.rightMargin = view.getContext().getResources().getDimensionPixelSize( com.intuit.sdp.R.dimen._minus8sdp );
            if (viewType == 0) {
                layoutParams.width = view.getContext().getResources().getDimensionPixelSize( com.intuit.sdp.R.dimen._48sdp );
            }
            return new PlayerAdapter.ViewHolder( view );
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            ContactListModel model = (ContactListModel) getItem( position );
            Graphics.loadImageWithFirstLetter( model.getImage(), viewHolder.vBinding.civPlayers, model.getFirstName() );

        }

        @Override
        public int getItemViewType(int position) {
            if (getItemCount() == position + 1) {
                return 0;
            } else {
                return 1;
            }
        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            private final ImageListBinding vBinding;

            public ViewHolder(@NonNull View itemView) {
                super( itemView );
                vBinding = ImageListBinding.bind( itemView );
            }
        }

    }


    // endregion
    // --------------------------------------
}