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
import com.whosin.app.databinding.ActivityChatProfileBinding;
import com.whosin.app.databinding.MediaItemBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.Repository.ChatRepository;
import com.whosin.app.service.manager.AppSettingManager;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.ChatMessageModel;
import com.whosin.app.service.models.ChatModel;
import com.whosin.app.service.models.CommonModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.Profile.FollowingActivity;
import com.whosin.app.ui.activites.Profile.FollowresActivity;
import com.whosin.app.ui.activites.Profile.OtherUserProfileActivity;
import com.whosin.app.ui.activites.comman.BaseActivity;
import com.whosin.app.ui.activites.reportedUser.ReportedUseSuccessDialog;
import com.whosin.app.ui.activites.venue.VenueGalleryActivity;
import com.whosin.app.ui.fragment.Chat.ReportAndBlockBottomSheet;
import com.whosin.app.ui.fragment.Chat.ReportBottomSheet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ChatProfileActivity extends BaseActivity {

    private ActivityChatProfileBinding binding;
    private final MediaImageAdapter<ChatMessageModel> mediaImageAdapter = new MediaImageAdapter<>();
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

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

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


    // endregion
    // --------------------------------------
}