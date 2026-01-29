package com.whosin.business.ui.activites.home.Chat;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.TextAppearanceSpan;
import android.text.style.URLSpan;
import android.util.Base64;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.devlomi.record_view.OnRecordListener;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.gson.Gson;
import com.tapadoo.alerter.Alerter;
import com.whosin.business.R;
import com.whosin.business.comman.AppConstants;
import com.whosin.business.comman.DiffAdapter;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Graphics;
import com.whosin.business.comman.Utils;
import com.whosin.business.comman.interfaces.BooleanResult;
import com.whosin.business.comman.interfaces.CommanCallback;
import com.whosin.business.comman.ui.UiUtils;
import com.whosin.business.comman.ui.roundcornerlayout.RoundCornerConstraintLayout;
import com.whosin.business.databinding.ActivityChatMessageBinding;
import com.whosin.business.databinding.AudioMessageItemBinding;
import com.whosin.business.databinding.EventMessageItemBinding;
import com.whosin.business.databinding.ItemChatMsgBinding;
import com.whosin.business.databinding.ItemEventReplyToBinding;
import com.whosin.business.databinding.ItemRaynaTicketReceiveBinding;
import com.whosin.business.databinding.ItemRaynaTicketSentViewBinding;
import com.whosin.business.databinding.ItemRaynaticketReplyToBinding;
import com.whosin.business.databinding.MessageItemImageBinding;
import com.whosin.business.databinding.OfferViewItemBinding;
import com.whosin.business.databinding.SentAudioMessageItemBinding;
import com.whosin.business.databinding.SentEventMessageItemBinding;
import com.whosin.business.databinding.SentEventReplyToItemBinding;
import com.whosin.business.databinding.SentMessageItemBinding;
import com.whosin.business.databinding.SentMessageItemImageBinding;
import com.whosin.business.databinding.SentOfferViewItemBinding;
import com.whosin.business.databinding.SentRaynaticketItemBinding;
import com.whosin.business.databinding.SentStoryItemBinding;
import com.whosin.business.databinding.SentUserMessageItemBinding;
import com.whosin.business.databinding.SentVenueMessageItemBinding;
import com.whosin.business.databinding.StoryMessageItemBinding;
import com.whosin.business.databinding.UserMessageItemBinding;
import com.whosin.business.databinding.VenueMessageItemBinding;
import com.whosin.business.service.DataService;
import com.whosin.business.service.Repository.ChatRepository;
import com.whosin.business.service.manager.AppSettingManager;
import com.whosin.business.service.manager.BlockUserManager;
import com.whosin.business.service.manager.ChatManager;
import com.whosin.business.service.manager.CheckUserSession;
import com.whosin.business.service.manager.SessionManager;
import com.whosin.business.service.models.BucketEventListModel;
import com.whosin.business.service.models.BucketListModel;
import com.whosin.business.service.models.ChatMessageModel;
import com.whosin.business.service.models.ChatModel;
import com.whosin.business.service.models.ChatWallpaperModel;
import com.whosin.business.service.models.CommonModel;
import com.whosin.business.service.models.ContainerModel;
import com.whosin.business.service.models.CreateBucketListModel;
import com.whosin.business.service.models.FollowUnfollowModel;
import com.whosin.business.service.models.InviteFriendModel;
import com.whosin.business.service.models.OffersModel;
import com.whosin.business.service.models.PromoterEventModel;
import com.whosin.business.service.models.TypingEventModel;
import com.whosin.business.service.models.UserDetailModel;
import com.whosin.business.service.models.VenueObjectModel;
import com.whosin.business.service.models.rayna.RaynaTicketDetailModel;
import com.whosin.business.service.rest.RestCallback;
import com.whosin.business.ui.activites.Profile.OtherUserProfileActivity;
import com.whosin.business.ui.activites.Profile.ProfileFullScreenImageActivity;
import com.whosin.business.ui.activites.Story.StoryViewActivity;
import com.whosin.business.ui.activites.comman.BaseActivity;
import com.whosin.business.ui.activites.raynaTicket.RaynaTicketDetailActivity;
import com.whosin.business.ui.activites.reportedUser.ReportedUseSuccessDialog;
import com.whosin.business.ui.activites.venue.VenueShareActivity;
import com.whosin.business.ui.adapter.raynaTicketAdapter.RaynaTicketImageAdapter;
import com.whosin.business.ui.fragment.Chat.ReportAndBlockBottomSheet;
import com.whosin.business.ui.fragment.Chat.ReportBottomSheet;
import com.whosin.business.ui.fragment.ProfileFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ChatMessageActivity extends BaseActivity {

    private ActivityChatMessageBinding binding;
    private ChatMessageAdapter<ChatMessageModel> chatAdapter = new ChatMessageAdapter<>();
    private ChatModel chatModel;
    private File recordFile;
    private AudioRecorder audioRecorder;
    private final Handler handler = new Handler();
    private Runnable runnable;
    private boolean isRunning = false;
    private TypingEventModel typingEventModel;
    private Uri imageData;
    private PromoterEventModel promoterEventModel = null;
    private boolean isFromMessageAdmin = false;
    private Handler ticketImageHandler = new Handler();
    private String ticketJSON  = "";
    private boolean isFromRaynaTicket  = false;


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    private BucketEventListModel getEventModel(String id) {
        BucketListModel chatList = ChatRepository.shared(ChatMessageActivity.this).getGroupChatFromCache();
        if (chatList == null) {
            return null;
        }
        Optional<BucketEventListModel> model = chatList.getEventModels().stream().filter(p -> p.getId().equals(id)).findFirst();
        return model.orElse(null);
    }

    private CreateBucketListModel getBucketModel(String id) {
        BucketListModel chatList = ChatRepository.shared(ChatMessageActivity.this).getGroupChatFromCache();
        if (chatList == null) {
            return null;
        }
        Optional<CreateBucketListModel> bucketsModel = chatList.getBucketsModels().stream().filter(p -> p.getId().equals(id)).findFirst();
        return bucketsModel.orElse(null);
    }

    private InviteFriendModel getOutingModel(String id) {
        BucketListModel chatList = ChatRepository.shared(ChatMessageActivity.this).getGroupChatFromCache();
        if (chatList == null) {
            return null;
        }
        Optional<InviteFriendModel> outingModel = chatList.getOutingModels().stream().filter(p -> p.getId().equals(id)).findFirst();
        return outingModel.orElse(null);
    }

    @Override
    protected void initUi() {

        if (Build.VERSION.SDK_INT >= 35) {
            WindowCompat.setDecorFitsSystemWindows(getWindow(), true);

            View rootView = findViewById(android.R.id.content);
            ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
                Insets imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime());
                Insets navInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars());

                int bottomInset = Math.max(imeInsets.bottom, navInsets.bottom);

                v.setPadding(0, 0, 0, bottomInset);
                return insets;
            });
        }




        CheckUserSession.checkSessionAndProceed(activity, () -> {});

        audioRecorder = new AudioRecorder();
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
        layoutManager.setStackFromEnd(true);
        binding.recyclerChat.setLayoutManager(layoutManager);
        binding.recyclerChat.setAdapter(chatAdapter);

        String chat = getIntent().getStringExtra("chatModel");
        chatModel = new Gson().fromJson(chat, ChatModel.class);
        if (chatModel != null) {
            if (chatModel.getChatType() == null) {
                Graphics.showAlertDialogWithOkButton(Graphics.context, getString(R.string.app_name), "Invalid chat type found");
                finish();
                return;
            }
            if (chatModel.getChatType().equals("friend")) {
                binding.iconMenu.setVisibility(View.VISIBLE);
                UserDetailModel userDetailModel = chatModel.getUser();
                if (userDetailModel != null) {
                    binding.tvTitle.setText(userDetailModel.getFullName());
                    Graphics.loadImageWithFirstLetter(userDetailModel.getImage(), binding.iconImg, userDetailModel.getFullName());
                } else {
                    binding.tvTitle.setText(chatModel.getTitle());
                    Graphics.loadImageWithFirstLetter(chatModel.getImage(), binding.iconImg, chatModel.getTitle());
                }

                isFromMessageAdmin = getIntent().getBooleanExtra("isFromMessageAdmin", false);
                isFromRaynaTicket = getIntent().getBooleanExtra("isFromRaynaTicket", false);
                String tmpStr = getIntent().getStringExtra("eventModel");
                if (!TextUtils.isEmpty(tmpStr)) {
                    promoterEventModel = new Gson().fromJson(tmpStr, PromoterEventModel.class);
                }
                if (isFromRaynaTicket){
                    String ticketJson = getIntent().getStringExtra("ticketChatJSON");
                    if (!TextUtils.isEmpty(ticketJson))  ticketJSON = ticketJson;
                }

            } else if (chatModel.getChatType().equals("bucket")) {
                CreateBucketListModel model = getBucketModel(chatModel.getChatId());
                if (model != null) {
                    binding.tvTitle.setText(model.getName());
                    Graphics.loadImageWithFirstLetter(model.getCoverImage(), binding.iconImg, model.getName());
                } else {
                    binding.tvTitle.setText(chatModel.getTitle());
                    Graphics.loadImageWithFirstLetter(chatModel.getImage(), binding.iconImg, chatModel.getTitle());
                }
            } else if (chatModel.getChatType().equals("event")) {
                BucketEventListModel model = getEventModel(chatModel.getChatId());
                if (model != null) {
                    String subTitle = (model.getOrg() != null ? model.getOrg().getName() : "");
                    setChatTitle(model.getTitle(), subTitle);
                    Graphics.loadImageWithFirstLetter(model.getImage(), binding.iconImg, model.getTitle());
                } else {
                    binding.tvTitle.setText(chatModel.getTitle());
                    Graphics.loadImageWithFirstLetter(chatModel.getImage(), binding.iconImg, chatModel.getTitle());
                }
            } else if (chatModel.getChatType().equals("outing")) {
                InviteFriendModel model = getOutingModel(chatModel.getChatId());
                if (model != null) {
                    if (model.getVenue() != null && model.getUser() != null) {
                        setChatTitle(model.getVenue().getName(), model.getUser().getFullName());
                        Graphics.loadImageWithFirstLetter(model.getVenue().getCover(), binding.iconImg, model.getVenue().getName());
                    }
                } else {
                    binding.tvTitle.setText(chatModel.getTitle());
                    Graphics.loadImageWithFirstLetter(chatModel.getImage(), binding.iconImg, chatModel.getTitle());
                }
            } else if (chatModel.getChatType().equals("promoter_event")) {
                binding.tvTitle.setText(chatModel.getTitle());
                Graphics.loadImageWithFirstLetter(chatModel.getImage(), binding.iconImg, chatModel.getTitle());

            }
        }

        Graphics.applyBlurEffect(this, binding.blurView);
        binding.recordButton.setRecordView(binding.recordView);
        setChatWallPaper();

        AppSettingManager.shared.venueReloadCallBack = data -> {
            if (data) finish();
        };
    }

    @Override
    protected void setListeners() {

        binding.ivBack.setOnClickListener(view -> onBackPressed());

        binding.btnViewTicket.setOnClickListener(view -> {
            Utils.preventDoubleClick(view);
        });

        binding.eventDetailLayout.setOnClickListener(v -> {
            Utils.preventDoubleClick(v);
        });

        binding.btnCamera.setOnClickListener(view1 -> {
            Utils.preventDoubleClick(view1);
            getImagePicker();
        });


        binding.constraintHeader.setOnClickListener(view -> {
            if (chatModel.isPromoter()) {
                if (SessionManager.shared.isSubAdmin()) return;
            } else {
                String chatModelString = new Gson().toJson(chatModel);
                // startActivity(new Intent(activity, ChatProfileActivity.class).putExtra("chatModel", chatModelString));
                Intent intent = new Intent(activity, ChatProfileActivity.class);
                intent.putExtra("chatModel", chatModelString);
                activityLauncher.launch(intent, result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        boolean isClose = result.getData().getBooleanExtra("close", false);
                        String type = result.getData().getStringExtra("type");
                        if (isClose) {
                            finish();
                        }
                    }
                });
            }
        });

        binding.iconMenu.setOnClickListener(v -> {
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
                            finish();
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
                    if (chatModel == null) return;
                    if (chatModel.getUser() == null) return;
                    Graphics.showAlertDialogWithOkCancel(activity, activity.getString(R.string.app_name), Utils.setLangValue("block_user_alert",chatModel.getUser().getFullName()), Utils.getLangValue("yes"), Utils.getLangValue("no"), isConfirmed -> {
                        if (isConfirmed) {
                            requestBlockUserAdd();
                        }
                    });
                }
            };
            bottomSheet.show(getSupportFragmentManager(), "");
        });

        binding.sendBtn.setOnClickListener(v -> {
            if (chatModel == null) {
                return;
            }
            String msg = binding.editTextMessage.getText().toString().trim();
            if (TextUtils.isEmpty(msg)) {
                return;
            }
            sendMessage(msg, "text", 0);
            isFromRaynaTicket = false;
            binding.editTextMessage.setText("", TextView.BufferType.EDITABLE);

        });

        binding.editTextMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!isRunning) {
                    ChatManager.shared.sendTypingEvent(chatModel.getChatId(), chatModel.getMembers(), "user", true);
                }
                handler.removeCallbacks(runnable);
                runnable = () -> {
                    isRunning = false;
                    ChatManager.shared.sendTypingEvent(chatModel.getChatId(), chatModel.getMembers(), "user", false);
                };
                isRunning = true;
                handler.postDelayed(runnable, 3000);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().isEmpty()) {
                    binding.recordButton.setVisibility(View.VISIBLE);
                    binding.sendBtn.setVisibility(View.GONE);
                } else {
                    binding.recordButton.setVisibility(View.GONE);
                    binding.sendBtn.setVisibility(View.VISIBLE);
                }
            }
        });

        binding.recordView.setRecordPermissionHandler(() -> {
            boolean recordPermissionAvailable = ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
            if (recordPermissionAvailable) {
                return true;
            }
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECORD_AUDIO}, 0);
            return false;
        });

        binding.recordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {
                if (audioRecorder.isRecording()) return;
                binding.recordView.setVisibility(View.VISIBLE);
                binding.editTextMessage.setVisibility(View.GONE);
                binding.btnCamera.setVisibility(View.GONE);
                recordFile = new File(getFilesDir(), UUID.randomUUID().toString() + ".m4a");
                try {
                    audioRecorder.start(recordFile.getPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancel() {
                stopRecording(true);
//                Utils.showSoftKeyboard(activity,binding.editTextMessage);
                binding.recordView.setVisibility(View.GONE);
                binding.editTextMessage.setVisibility(View.VISIBLE);
                binding.btnCamera.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinish(long recordTime, boolean limitReached) {
                stopRecording(false);
                sendAudio(recordFile);
                binding.recordView.setVisibility(View.GONE);
                binding.editTextMessage.setVisibility(View.VISIBLE);
                binding.btnCamera.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLessThanSecond() {
                new Handler().postDelayed(() -> {
                    stopRecording(true);
                    binding.recordView.setVisibility(View.GONE);
                    binding.editTextMessage.setVisibility(View.VISIBLE);
                    binding.btnCamera.setVisibility(View.VISIBLE);
                }, 500);
            }

            @Override
            public void onLock() {
                Log.d("RecordView", "onLock");
            }


        });

        binding.recordView.setOnBasketAnimationEndListener(() -> {
            binding.recordView.setVisibility(View.GONE);
        });
    }

    public String millisecondsToMMSS(long milliseconds) {
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private long getDuration(File recordFile) {
        long durations = 0;
        try {
            durations = Utils.getAudioDuration(recordFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            return durations;
        }
    }

    public void makeLinksClickable(TextView textView, String dynamicText) {
        SpannableString spannableString = new SpannableString(dynamicText);

        Pattern urlPattern = Pattern.compile("(https?:\\/\\/\\S+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = urlPattern.matcher(dynamicText);

        GestureDetector gestureDetector = new GestureDetector(textView.getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                int position = textView.getOffsetForPosition(e.getX(), e.getY());
                URLSpan[] linkSpans = spannableString.getSpans(position, position, URLSpan.class);
                if (linkSpans.length > 0) {
                    String url = linkSpans[0].getURL();
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    textView.getContext().startActivity(browserIntent);
                    return true;
                }
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                int position = textView.getOffsetForPosition(e.getX(), e.getY());
                URLSpan[] linkSpans = spannableString.getSpans(position, position, URLSpan.class);
                if (linkSpans.length > 0) {
                    String url = linkSpans[0].getURL();
                    ClipboardManager clipboard = (ClipboardManager) textView.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Copied Link", url);
                    clipboard.setPrimaryClip(clip);

                    Toast.makeText(textView.getContext(), "Link copied to clipboard", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Apply ClickableSpan to make links clickable
        while (matcher.find()) {
            final String url = matcher.group();
            URLSpan urlSpan = new URLSpan(url) {
                @Override
                public void onClick(View widget) {

                }

                @Override
                public void updateDrawState(@NonNull TextPaint ds) {
                    ds.setColor(ContextCompat.getColor(activity, R.color.chat_link));
                    ds.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                }
            };
            spannableString.setSpan(urlSpan, matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        textView.setText(spannableString);
        textView.setMovementMethod(LinkMovementMethod.getInstance());

        textView.setOnTouchListener((v, event) -> {
            gestureDetector.onTouchEvent(event);
            return false;
        });
    }


    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityChatMessageBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    ActivityResultLauncher<Intent> startActivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            if (result.getData() != null) {
                imageData = result.getData().getData();
                if (imageData != null) {
                    binding.constraint.setVisibility(View.VISIBLE);
                }
                sendImageMessage(imageData);
            }
        }
    });

    @Override
    protected void onResume() {
        super.onResume();
        requestChatMsg();
        setChatWallPaper();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        //  resetBadgeCounterOfPushMessages();
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String event) {
        if (Objects.equals(event, chatModel.getChatId())) {
            requestChatMsg();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(TypingEventModel event) {
        if (Objects.equals(event.chatId, chatModel.getChatId()) && event.isForStartTyping) {
            typingEventModel = event;
            binding.tvTypingStatus.setText(String.format("Typing...", event.userName));
            binding.tvTypingStatus.setVisibility(View.VISIBLE);
            return;
        }
        if (typingEventModel == null) {
            return;
        }
        if (Objects.equals(typingEventModel.userId, event.userId) && !event.isForStartTyping) {
            binding.tvTypingStatus.setText("");
            binding.tvTypingStatus.setVisibility(View.GONE);
        }
    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void stopRecording(boolean deleteFile) {
        audioRecorder.stop();
        if (recordFile != null && deleteFile) {
            recordFile.delete();
        }
    }

    private void setChatWallPaper() {
        String chatId = chatModel.getChatId();
        String wallpaperPath = ChatWallpaperModel.setWallPaper(chatId);
        if (!wallpaperPath.isEmpty()) {
            byte[] decodedBytes = Base64.decode(wallpaperPath, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            binding.chatWallPaper.setImageBitmap(bitmap);
        } else {
            binding.getRoot().setBackgroundColor(getResources().getColor(R.color.video_bg));
        }
    }

    private void getImagePicker() {
        ArrayList<String> data = new ArrayList<>();
        data.add(getValue("gallery"));
        data.add(getValue("camera"));
        Graphics.showActionSheet(activity, getValue("choose_any_one"), data, (data1, position) -> {
            switch (position) {
                case 0:
                    ImagePicker.with(activity).galleryOnly().crop().createIntent(intent -> {
                        startActivity.launch(intent);
                        return null;
                    });
                    break;
                case 1:
                    ImagePicker.with(activity).cameraOnly().createIntent(intent -> {
                        startActivity.launch(intent);
                        return null;
                    });
                    break;
            }
        });
    }

    private void setChatTitle(String title, String subtitle) {
        String fullText = title + " - " + subtitle;
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(fullText);
        int start = title.length() + 3;
        int end = start + subtitle.length();
        spannableStringBuilder.setSpan(new TextAppearanceSpan(getApplicationContext(), R.style.txt9RegularWhite70), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.tvTitle.setText(spannableStringBuilder);
    }

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void sendMessage(String msg, String type, long audioDuration) {
        ChatMessageModel messageModel;
        if (isFromRaynaTicket){
            messageModel = new ChatMessageModel(msg, type, chatModel, chatModel.getMembers(),ticketJSON);
        }else {
            messageModel = new ChatMessageModel(msg, type, chatModel, chatModel.getMembers(), isFromMessageAdmin, promoterEventModel);
        }

        if (audioDuration > 0) {
            messageModel.setAudioDuration(String.valueOf(audioDuration));
        }
        Log.d("TAG", new Gson().toJson(messageModel));
        ChatRepository.shared(this).addMessage(messageModel, data -> {
            isFromMessageAdmin = false;
            if (chatAdapter.getData() == null || chatAdapter.getData().isEmpty()) {
                chatAdapter.updateData(Collections.singletonList(messageModel));
            } else {
                chatAdapter.getData().add(messageModel);
            }
            chatAdapter.notifyItemChanged(chatAdapter.getItemCount() - 1);
            binding.recyclerChat.scrollToPosition(chatAdapter.getItemCount() - 1);
            ChatManager.shared.sendMessage(messageModel);
        });
    }

    private void sendImageMessage(Uri msg) {
        String realPathFromURIPath = Utils.getRealPathFromURIPath(msg, activity);
        File file = new File(realPathFromURIPath);
        ChatMessageModel messageModel;
        if (isFromRaynaTicket){
            messageModel = new ChatMessageModel(file.getAbsolutePath(), "image", chatModel, chatModel.getMembers(),ticketJSON);
        }else {
            messageModel =new ChatMessageModel(file.getAbsolutePath(), "image", chatModel, chatModel.getMembers(), isFromMessageAdmin, promoterEventModel);
        }
        isFromRaynaTicket = false;
        ChatRepository.shared(this).addMessage(messageModel, data -> {
            isFromMessageAdmin = false;
            if (chatAdapter.getData() == null || chatAdapter.getData().isEmpty()) {
                chatAdapter.updateData(Collections.singletonList(messageModel));
            } else {
                chatAdapter.getData().add(messageModel);
            }
            chatAdapter.notifyItemChanged(chatAdapter.getItemCount() - 1);
            binding.recyclerChat.scrollToPosition(chatAdapter.getItemCount() - 1);
            requestAudioSend(file, "image", (success, model) -> {
                if (success) {
                    messageModel.setMsg(model);
                    ChatManager.shared.sendMessage(messageModel);
                }
            });
        });
    }

    private void sendAudio(File file) {
        long audioDuration = getDuration(file);
        String stringDuration = millisecondsToMMSS(audioDuration);

        ChatMessageModel messageModel = new ChatMessageModel(file.getAbsolutePath(), "audio", chatModel, chatModel.getMembers());
        messageModel.setAudioDuration(stringDuration);
        ChatRepository.shared(this).addMessage(messageModel, data -> {
            chatAdapter.getData().add(messageModel);
            chatAdapter.notifyItemChanged(chatAdapter.getItemCount() - 1);
            binding.recyclerChat.scrollToPosition(chatAdapter.getItemCount() - 1);
            requestAudioSend(file, "audio", (success, model) -> {
                if (success) {
                    messageModel.setMsg(model);
                    ChatManager.shared.sendMessage(messageModel);
                }
            });
        });
    }

    private void requestChatMsg() {
        List<ChatMessageModel> messages = ChatRepository.shared(this).getChatMessages(chatModel.getChatId());
        if (messages != null) {
            String userId = SessionManager.shared.isPromoterSubAdmin() ? SessionManager.shared.getPromoterId() : SessionManager.shared.getUser().getId();
            List<ChatMessageModel> unReadMsgs = messages.stream().filter(p -> !p.isSent() && !p.getSeenBy().contains(userId)).collect(Collectors.toList());
            if (!unReadMsgs.isEmpty()) {
                ChatManager.shared.sendSeenEvent(unReadMsgs);
            }
//            Collections.reverse(messages);  // Reverse chat list
            chatAdapter.updateData(messages);
            binding.recyclerChat.scrollToPosition(chatAdapter.getItemCount() - 1);
        }
    }

    private void requestAudioSend(File file, String type, BooleanResult callBack) {
        DataService.shared(activity).requestChatUploadList(activity, file, type, new RestCallback<ContainerModel<String>>(this) {
            @Override
            public void result(ContainerModel<String> model, String error) {
                if (!Utils.isNullOrEmpty(error)) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }

                callBack.success(true, model.getData());

            }
        });
    }

    private void reqFollowUnFollow(UserDetailModel userDetailModel, CommanCallback<String> callback) {

        DataService.shared(activity).requestUserFollowUnFollow(userDetailModel.getId(), new RestCallback<ContainerModel<FollowUnfollowModel>>(this) {
            @Override
            public void result(ContainerModel<FollowUnfollowModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null || model.getData() == null) {
                    Toast.makeText(activity, R.string.service_message_something_wrong, Toast.LENGTH_SHORT).show();
                    return;
                }
                callback.onReceive(model.getData().getStatus());
                switch (model.getData().getStatus()) {
                    case "unfollowed":
                        Alerter.create(activity).setTitle(getValue("oh_snap")).setTitleAppearance(R.style.AlerterTitle).setTextAppearance(R.style.AlerterText).setText(setValue("unfollow_toast",userDetailModel.getFullName())).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                        break;
                    case "approved":
                        Alerter.create(activity).setTitle(getValue("thank_you")).setText(setValue("following_toast",userDetailModel.getFullName())).setTextAppearance(R.style.AlerterText).setTitleAppearance(R.style.AlerterTitle).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                        break;
                    case "pending":
                        Alerter.create(activity).setTitle(getValue("thank_you")).setText(setValue("requested_for_follow",userDetailModel.getFullName())).setTextAppearance(R.style.AlerterText).setTitleAppearance(R.style.AlerterTitle).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                        break;
                    case "cancelled":
                        Alerter.create(activity).setTitle(getValue("oh_snap")).setText(setValue("requested_cancel_for_follow",userDetailModel.getFullName())).setTextAppearance(R.style.AlerterText).setTitleAppearance(R.style.AlerterTitle).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                        break;
                }
                EventBus.getDefault().post(userDetailModel);
            }
        });
    }

    private void requestBlockUserAdd() {
        DataService.shared(activity).requestBlockUser(chatModel.getUser().getId(), new RestCallback<ContainerModel<CommonModel>>(this) {
            @Override
            public void result(ContainerModel<CommonModel> model, String error) {
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }
                BlockUserManager.addBlockUserId(chatModel.getUser().getId());
                Alerter.create(activity).setTitle(getValue("oh_snap")).setText(getValue("you_have_blocked") + " " + chatModel.getUser().getFullName()).setTextAppearance(R.style.AlerterText).setTitleAppearance(R.style.AlerterTitle).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                finish();
            }
        });
    }

    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    public class ChatMessageAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        private MediaPlayer currentMediaPlayer;

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            switch (viewType) {
                case 0:
                    return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_chat_msg));
                case 1:
                    return new SendMessageHolder(UiUtils.getViewBy(parent, R.layout.sent_message_item));
                case 2:
                    return new AudioHolder(UiUtils.getViewBy(parent, R.layout.audio_message_item));
                case 3:
                    return new SendAudioHolder(UiUtils.getViewBy(parent, R.layout.sent_audio_message_item));
                case 4:
                    return new ImageViewHolder(UiUtils.getViewBy(parent, R.layout.message_item_image));
                case 5:
                    return new SentImageHolder(UiUtils.getViewBy(parent, R.layout.sent_message_item_image));
                case 6:
                    return new VenueViewHolder(UiUtils.getViewBy(parent, R.layout.venue_message_item));
                case 7:
                    return new SentVenueViewHolder(UiUtils.getViewBy(parent, R.layout.sent_venue_message_item));
                case 8:
                    return new StoryViewHolder(UiUtils.getViewBy(parent, R.layout.story_message_item));
                case 9:
                    return new SentStoryViewHolder(UiUtils.getViewBy(parent, R.layout.sent_story_item));
                case 10:
                    return new UserViewHolder(UiUtils.getViewBy(parent, R.layout.user_message_item));
                case 11:
                    return new SentUserViewHolder(UiUtils.getViewBy(parent, R.layout.sent_user_message_item));
                case 12:
                    return new OfferViewHolder(UiUtils.getViewBy(parent, R.layout.offer_view_item));
                case 13:
                    return new SentOfferViewHolder(UiUtils.getViewBy(parent, R.layout.sent_offer_view_item));
                case 16:
                    return new SentPromoterEventViewHolder(UiUtils.getViewBy(parent, R.layout.event_message_item));
                case 17:
                    return new PromoterEventViewHolder(UiUtils.getViewBy(parent, R.layout.sent_event_message_item));
                case 18:
                    return new PromoterEventReplyViewHolder(UiUtils.getViewBy(parent, R.layout.item_event_reply_to));
                case 19:
                    return new PromoterSentEventReplyToViewHolder(UiUtils.getViewBy(parent, R.layout.sent_event_reply_to_item));
                case 20:
                    return new RaynaTicketReceiveViewHolder(UiUtils.getViewBy(parent, R.layout.item_rayna_ticket_receive));
                case 21:
                    return new RaynaTicketSentViewHolder(UiUtils.getViewBy(parent, R.layout.item_rayna_ticket_sent_view));
                case 22:
                    return new RaynaTicketSentReplyToViewHolder(UiUtils.getViewBy(parent, R.layout.sent_raynaticket_item));
                case 23:
                    return new RaynaTicketRevisedReplyToViewHolder(UiUtils.getViewBy(parent, R.layout.item_raynaticket_reply_to));
                default:
                    return null;
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ChatMessageModel model = (ChatMessageModel) getItem(position);
            int type = getItemViewType(position);
            switch (type) {
                case 0:
                    ViewHolder viewHolder = (ViewHolder) holder;
                    viewHolder.mBinding.tvChat.setText(model.getMsg());
                    viewHolder.mBinding.tvTime.setText(model.getDate(AppConstants.DATEFORMAT_12HOUR));
                    viewHolder.mBinding.txtSender.setText(model.getAuthorName());
                    makeLinksClickable(viewHolder.mBinding.tvChat, model.getMsg());
                    break;
                case 1:
                    SendMessageHolder messageHolder = (SendMessageHolder) holder;
                    messageHolder.vBinding.tvMsg.setText(model.getMsg());
                    makeLinksClickable(messageHolder.vBinding.tvMsg, model.getMsg());
                    messageHolder.vBinding.txtTime.setText(model.getSendTime());
                    updateReplyByTextView(messageHolder.vBinding.tvTitleReplyBy, model.getReply_by());
                    messageHolder.vBinding.imgMsgStatus.setImageDrawable(model.getStatusIcon());
                    break;
                case 2:
                    AudioHolder audioHolder = (AudioHolder) holder;
                    audioHolder.vBinding.txtSender.setText(model.getAuthorName());
                    audioHolder.vBinding.txtMsgTime.setText(model.getDate(AppConstants.DATEFORMAT_12HOUR));
                    audioHolder.setupPlayer(model.getMsg());
                    break;
                case 3:
                    SendAudioHolder sendAudioHolder = (SendAudioHolder) holder;
                    sendAudioHolder.vBinding.txtTime.setText(model.getSendTime());
                    sendAudioHolder.vBinding.txtAudioTime.setText(model.getAudioDuration());
                    sendAudioHolder.setupPlayer(model.getMsg());
                    sendAudioHolder.vBinding.imgMsgStatus.setImageDrawable(model.getStatusIcon());
                    updateReplyByTextView(sendAudioHolder.vBinding.tvTitleReplyBy, model.getReply_by());

                    break;
                case 4:
                    ImageViewHolder imageViewHolder = (ImageViewHolder) holder;
                    Graphics.loadImage(model.getMsg(), imageViewHolder.binding.ivMsg);
                    imageViewHolder.binding.txtSender.setText(model.getAuthorName());
                    imageViewHolder.binding.txtTime.setText(model.getDate(AppConstants.DATEFORMAT_12HOUR));
                    imageViewHolder.binding.ivMsg.setOnClickListener(view -> {
                        Intent intent = new Intent(activity, ProfileFullScreenImageActivity.class);
                        intent.putExtra(ProfileFullScreenImageActivity.EXTRA_IMAGE_URL, model.getMsg());
                        startActivity(intent);
                    });
                    break;
                case 5:
                    SentImageHolder sentImageHolder = (SentImageHolder) holder;
                    Graphics.loadImage(model.getMsg(), sentImageHolder.vBinding.ivMsg);
                    sentImageHolder.vBinding.txtTime.setText(model.getSendTime());
                    sentImageHolder.vBinding.imgMsgStatus.setImageDrawable(model.getStatusIcon());
                    updateReplyByTextView(sentImageHolder.vBinding.tvTitleReplyBy, model.getReply_by());

                    sentImageHolder.vBinding.ivMsg.setOnClickListener(view -> {
                        Intent intent = new Intent(activity, ProfileFullScreenImageActivity.class);
                        intent.putExtra(ProfileFullScreenImageActivity.EXTRA_IMAGE_URL, model.getMsg());
                        startActivity(intent);
                    });
                    break;
                case 6:
                    VenueViewHolder venueViewHolder = (VenueViewHolder) holder;
                    venueViewHolder.mBinding.txtSender.setText(model.getAuthorName());
                    venueViewHolder.mBinding.txtTime.setText(model.getDate(AppConstants.DATEFORMAT_12HOUR));
                    VenueObjectModel venueObjectModel = new Gson().fromJson(model.getMsg(), VenueObjectModel.class);
                    venueViewHolder.mBinding.venueContainer.setVenueDetail(venueObjectModel);
                    Graphics.loadImage(venueObjectModel.getCover(), venueViewHolder.mBinding.cover);
                    venueViewHolder.mBinding.layoutSend.setOnClickListener(v -> {
                        startActivity(new Intent(activity, VenueShareActivity.class)
                                .putExtra("venue", model.getMsg())
                                .putExtra("type", "venue"));
                    });
                    venueViewHolder.setListener(venueObjectModel);
                    break;
                case 7:
                    SentVenueViewHolder sentMessageHolder = (SentVenueViewHolder) holder;
                    VenueObjectModel venueObjectModel1 = new Gson().fromJson(model.getMsg(), VenueObjectModel.class);
                    updateReplyByTextView(sentMessageHolder.mBinding.tvTitleReplyBy, model.getReply_by());
                    sentMessageHolder.mBinding.venueContainer.setVenueDetail(venueObjectModel1);
                    sentMessageHolder.mBinding.txtTime.setText(model.getSendTime());
                    sentMessageHolder.mBinding.imgMsgStatus.setImageDrawable(model.getStatusIcon());
                    Graphics.loadImage(venueObjectModel1.getCover(), sentMessageHolder.mBinding.cover);
                    sentMessageHolder.mBinding.layoutSend.setOnClickListener(v -> {
                        startActivity(new Intent(activity, VenueShareActivity.class)
                                .putExtra("venue", model.getMsg())
                                .putExtra("type", "venue"));
                    });
                    sentMessageHolder.setListener(venueObjectModel1);
                    break;
                case 8:
                    StoryViewHolder storyViewHolder = (StoryViewHolder) holder;
                    storyViewHolder.mBinding.txtSender.setText(model.getAuthorName());
                    storyViewHolder.mBinding.txtTime.setText(model.getDate(AppConstants.DATEFORMAT_12HOUR));
                    VenueObjectModel storyVenueObjectModel = new Gson().fromJson(model.getMsg(), VenueObjectModel.class);
                    storyViewHolder.mBinding.layoutSend.setOnClickListener(v -> {
                        startActivity(new Intent(activity, VenueShareActivity.class)
                                .putExtra("venue", model.getMsg())
                                .putExtra("type", "story"));
                    });
                    storyViewHolder.setupData(storyVenueObjectModel);
                    break;
                case 9:
                    SentStoryViewHolder sentStoryViewHolder = (SentStoryViewHolder) holder;
                    sentStoryViewHolder.mBinding.txtTime.setText(model.getSendTime());
                    sentStoryViewHolder.mBinding.imgMsgStatus.setImageDrawable(model.getStatusIcon());
                    VenueObjectModel sentStoryVenueObjectModel = new Gson().fromJson(model.getMsg(), VenueObjectModel.class);
                    sentStoryViewHolder.mBinding.layoutSend.setOnClickListener(v -> {
                        startActivity(new Intent(activity, VenueShareActivity.class)
                                .putExtra("venue", model.getMsg())
                                .putExtra("type", "story"));
                    });
                    sentStoryViewHolder.setupData(sentStoryVenueObjectModel);
                    break;
                case 10:
                    UserViewHolder userViewHolder = (UserViewHolder) holder;
                    userViewHolder.setup(model);
                    break;
                case 11:
                    SentUserViewHolder sentUserViewHolder = (SentUserViewHolder) holder;
                    sentUserViewHolder.setup(model);
                    break;
                case 12:
                    OfferViewHolder offerViewHolder = (OfferViewHolder) holder;
                    offerViewHolder.setup(model);
                    break;
                case 13:
                    SentOfferViewHolder sentOfferViewHolder = (SentOfferViewHolder) holder;
                    sentOfferViewHolder.setup(model);
                    break;
                case 16:
                    SentPromoterEventViewHolder sentPromoterEventViewHolder = (SentPromoterEventViewHolder) holder;
                    sentPromoterEventViewHolder.binding.txtSender.setText(model.getAuthorName());
                    sentPromoterEventViewHolder.binding.txtTime.setText(model.getDate(AppConstants.DATEFORMAT_12HOUR));
                    sentPromoterEventViewHolder.setUp(model);
                    break;
                case 17:
                    PromoterEventViewHolder promoterEventViewHolder = (PromoterEventViewHolder) holder;
                    promoterEventViewHolder.binding.txtTime.setText(model.getSendTime());
                    promoterEventViewHolder.binding.imgMsgStatus.setImageDrawable(model.getStatusIcon());
                    promoterEventViewHolder.setUp(model);
                    break;
                case 18:
                    PromoterEventReplyViewHolder promoterEventReplyViewHolder = (PromoterEventReplyViewHolder) holder;
                    if (model.getType().equals("image")) {
                        promoterEventReplyViewHolder.binding.tvChat.setVisibility(View.GONE);
                        promoterEventReplyViewHolder.binding.imageLayout.setVisibility(View.VISIBLE);
                        Graphics.loadImage(model.getMsg(), promoterEventReplyViewHolder.binding.ivMsg);
                        promoterEventReplyViewHolder.binding.ivMsg.setOnClickListener(view -> {
                            Intent intent = new Intent(activity, ProfileFullScreenImageActivity.class);
                            intent.putExtra(ProfileFullScreenImageActivity.EXTRA_IMAGE_URL, model.getMsg());
                            startActivity(intent);
                        });

                    } else {
                        promoterEventReplyViewHolder.binding.tvChat.setVisibility(View.VISIBLE);
                        promoterEventReplyViewHolder.binding.imageLayout.setVisibility(View.GONE);
                        promoterEventReplyViewHolder.binding.tvChat.setText(model.getMsg());
                        makeLinksClickable(promoterEventReplyViewHolder.binding.tvChat, model.getMsg());
                    }


                    promoterEventReplyViewHolder.binding.txtTime.setText(model.getDate(AppConstants.DATEFORMAT_12HOUR));
                    promoterEventReplyViewHolder.binding.txtSender.setText(model.getAuthorName());
                    promoterEventReplyViewHolder.setUp(model);
                    break;
                case 19:
                    PromoterSentEventReplyToViewHolder promoterSentEventReplyToViewHolder = (PromoterSentEventReplyToViewHolder) holder;
                    if (model.getType().equals("image")) {
                        promoterSentEventReplyToViewHolder.binding.tvMsg.setVisibility(View.GONE);
                        promoterSentEventReplyToViewHolder.binding.imageLayout.setVisibility(View.VISIBLE);
                        Graphics.loadImage(model.getMsg(), promoterSentEventReplyToViewHolder.binding.ivMsg);
                        promoterSentEventReplyToViewHolder.binding.ivMsg.setOnClickListener(view -> {
                            Intent intent = new Intent(activity, ProfileFullScreenImageActivity.class);
                            intent.putExtra(ProfileFullScreenImageActivity.EXTRA_IMAGE_URL, model.getMsg());
                            startActivity(intent);
                        });

                    } else {
                        makeLinksClickable(promoterSentEventReplyToViewHolder.binding.tvMsg, model.getMsg());
                        promoterSentEventReplyToViewHolder.binding.tvMsg.setText(model.getMsg());
                        promoterSentEventReplyToViewHolder.binding.tvMsg.setVisibility(View.VISIBLE);
                        promoterSentEventReplyToViewHolder.binding.imageLayout.setVisibility(View.GONE);
                    }

                    promoterSentEventReplyToViewHolder.setUp(model);
                    promoterSentEventReplyToViewHolder.binding.txtTime.setText(model.getSendTime());
                    promoterSentEventReplyToViewHolder.binding.imgMsgStatus.setImageDrawable(model.getStatusIcon());
                    break;
                case 20:
                    RaynaTicketReceiveViewHolder raynaTicketReceiveViewHolder = (RaynaTicketReceiveViewHolder) holder;
                    raynaTicketReceiveViewHolder.binding.txtSender.setText(model.getAuthorName());
                    raynaTicketReceiveViewHolder.binding.txtTime.setText(model.getDate(AppConstants.DATEFORMAT_12HOUR));
                    raynaTicketReceiveViewHolder.setUp(model);
                    break;

                case 21:
                    RaynaTicketSentViewHolder raynaTicketSentViewHolder = (RaynaTicketSentViewHolder) holder;
                    raynaTicketSentViewHolder.binding.txtTime.setText(model.getSendTime());
                    raynaTicketSentViewHolder.binding.imgMsgStatus.setImageDrawable(model.getStatusIcon());
                    raynaTicketSentViewHolder.setUp(model);
                    break;
                case 22:
                    RaynaTicketSentReplyToViewHolder raynaTicketSentReplyToViewHolder = (RaynaTicketSentReplyToViewHolder) holder;
//                    makeLinksClickable(raynaTicketSentReplyToViewHolder.binding.tvMsg, model.getMsg());
//                    raynaTicketSentReplyToViewHolder.binding.tvMsg.setText(model.getMsg());
//                    raynaTicketSentReplyToViewHolder.binding.tvMsg.setVisibility(View.VISIBLE);
//                    raynaTicketSentReplyToViewHolder.binding.imageLayout.setVisibility(View.GONE);
//                    raynaTicketSentReplyToViewHolder.setUp(model);
//                    raynaTicketSentReplyToViewHolder.binding.txtTime.setText(model.getSendTime());
//                    raynaTicketSentReplyToViewHolder.binding.imgMsgStatus.setImageDrawable(model.getStatusIcon());
                    if (model.getType().equals("image")) {
                        raynaTicketSentReplyToViewHolder.binding.tvMsg.setVisibility(View.GONE);
                        raynaTicketSentReplyToViewHolder.binding.imageLayout.setVisibility(View.VISIBLE);
                        Graphics.loadImage(model.getMsg(), raynaTicketSentReplyToViewHolder.binding.ivMsg);
                        raynaTicketSentReplyToViewHolder.binding.ivMsg.setOnClickListener(view -> {
                            Intent intent = new Intent(activity, ProfileFullScreenImageActivity.class);
                            intent.putExtra(ProfileFullScreenImageActivity.EXTRA_IMAGE_URL, model.getMsg());
                            startActivity(intent);
                        });

                    } else {
                        makeLinksClickable(raynaTicketSentReplyToViewHolder.binding.tvMsg, model.getMsg());
                        raynaTicketSentReplyToViewHolder.binding.tvMsg.setText(model.getMsg());
                        raynaTicketSentReplyToViewHolder.binding.tvMsg.setVisibility(View.VISIBLE);
                        raynaTicketSentReplyToViewHolder.binding.imageLayout.setVisibility(View.GONE);
                    }

                    raynaTicketSentReplyToViewHolder.setUp(model);
                    raynaTicketSentReplyToViewHolder.binding.txtTime.setText(model.getSendTime());
                    raynaTicketSentReplyToViewHolder.binding.imgMsgStatus.setImageDrawable(model.getStatusIcon());
                    break;
                case 23:
                    RaynaTicketRevisedReplyToViewHolder raynaTicketRevisedReplyToViewHolder = (RaynaTicketRevisedReplyToViewHolder) holder;
                    if (model.getType().equals("image")) {
                        raynaTicketRevisedReplyToViewHolder.binding.tvChat.setVisibility(View.GONE);
                        raynaTicketRevisedReplyToViewHolder.binding.imageLayout.setVisibility(View.VISIBLE);
                        Graphics.loadImage(model.getMsg(), raynaTicketRevisedReplyToViewHolder.binding.ivMsg);
                        raynaTicketRevisedReplyToViewHolder.binding.ivMsg.setOnClickListener(view -> {
                            Intent intent = new Intent(activity, ProfileFullScreenImageActivity.class);
                            intent.putExtra(ProfileFullScreenImageActivity.EXTRA_IMAGE_URL, model.getMsg());
                            startActivity(intent);
                        });

                    } else {
                        raynaTicketRevisedReplyToViewHolder.binding.tvChat.setVisibility(View.VISIBLE);
                        raynaTicketRevisedReplyToViewHolder.binding.imageLayout.setVisibility(View.GONE);
                        raynaTicketRevisedReplyToViewHolder.binding.tvChat.setText(model.getMsg());
                        makeLinksClickable(raynaTicketRevisedReplyToViewHolder.binding.tvChat, model.getMsg());
                    }
                    raynaTicketRevisedReplyToViewHolder.binding.txtTime.setText(model.getDate(AppConstants.DATEFORMAT_12HOUR));
                    raynaTicketRevisedReplyToViewHolder.binding.txtSender.setText(model.getAuthorName());
                    raynaTicketRevisedReplyToViewHolder.setUp(model);
                    break;
                default:


            }

        }

        @Override
        public int getItemViewType(int position) {
            ChatMessageModel model = (ChatMessageModel) getItem(position);
            switch (model.getType()) {
                case "text":
                    if (model.isRaynaTicketMessage()) {
                        return model.isSent() ? 22 : 23;
                    } else if (model.isCmAdminMessage()) {
                        return model.isSent() ? 19 : 18;
                    } else {
                        return model.isSent() ? 1 : 0;
                    }
                case "audio":
                    return !model.isSent() ? 2 : 3;
                case "image":
                    if (model.isRaynaTicketMessage()) {
                        return model.isSent() ? 22 : 23;
                    } else if (model.isCmAdminMessage()) {
                        return model.isSent() ? 19 : 18;
                    } else {
                        return model.isSent() ? 5 : 4;
                    }
//                    return model.isCmAdminMessage() ? !model.isSent() ? 18 : 19 : !model.isSent() ? 4 : 5;
                case "venue":
                    return !model.isSent() ? 6 : 7;
                case "story":
                    return !model.isSent() ? 8 : 9;
                case "user":
                    return !model.isSent() ? 10 : 11;
                case "offer":
                    return !model.isSent() ? 12 : 13;
                case "yachtClub":
                    return !model.isSent() ? 14 : 15;
                case "promoterEvent":
                    return !model.isSent() ? 16 : 17;
                case "ticket":
                    return !model.isSent() ? 20 : 21;
                default:
                    return 6;
            }
        }

        // TEXT HOLDER
        public class ViewHolder extends RecyclerView.ViewHolder {
            private final ItemChatMsgBinding mBinding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemChatMsgBinding.bind(itemView);
            }
        }

        public class SendMessageHolder extends RecyclerView.ViewHolder {

            private final SentMessageItemBinding vBinding;

            public SendMessageHolder(@NonNull View itemView) {
                super(itemView);
                vBinding = SentMessageItemBinding.bind(itemView);
            }
        }

        // AUDIO HOLDER
        public class AudioHolder extends RecyclerView.ViewHolder {

            private final AudioMessageItemBinding vBinding;
            private MediaPlayer mediaPlayer;
            private Timer timer;
            private boolean mediaPlayerWasPlaying = false;


            public AudioHolder(@NonNull View itemView) {
                super(itemView);
                vBinding = AudioMessageItemBinding.bind(itemView);
            }

            private void setTime() {
                int duration = mediaPlayer.getDuration();
                if (duration < 0) {
                    return;
                }
                String time = String.format(Locale.ENGLISH, "%d:%d", TimeUnit.MILLISECONDS.toMinutes(duration), TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
                if (TextUtils.isEmpty(time)) {
                    time = "00:00";
                }
                vBinding.txtAudioTime.setText(time);
            }

            private void setupPlayer(String url) {
                mediaPlayer = setupMediaPlayer(url);
                if (mediaPlayer == null) {
                    return;
                }
                setListener();
            }

            private void setListener() {
                mediaPlayer.setOnPreparedListener(mp -> {
                    vBinding.slider.setMax(mediaPlayer.getDuration());
                    setTime();
                });

                mediaPlayer.setOnCompletionListener(mp -> {
                    if (timer != null) {
                        timer.cancel();
                    }
                    setTime();
                    vBinding.slider.setProgress(0);
                    vBinding.playButton.setImageResource(R.drawable.img_play_btn);
                });

                vBinding.playButton.setOnClickListener(v -> {
                    if (mediaPlayer.isPlaying()) {
                        vBinding.playButton.setImageResource(R.drawable.img_play_btn);
                        mediaPlayer.pause();
                    } else {
                        vBinding.playButton.setImageResource(R.drawable.img_puase_btn);
                        if (currentMediaPlayer != null && currentMediaPlayer.isPlaying()) {
                            currentMediaPlayer.seekTo(currentMediaPlayer.getDuration());
                        }
                        mediaPlayer.start();
                        currentMediaPlayer = mediaPlayer;
                        if (timer != null) {
                            timer.cancel();
                        }
                        timer = new Timer();
                        timer.scheduleAtFixedRate(new TimerTask() {
                            @Override
                            public void run() {
                                int duration = mediaPlayer.getCurrentPosition();
                                String time = String.format(Locale.ENGLISH, "%d:%d", TimeUnit.MILLISECONDS.toMinutes(duration), TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
                                if (TextUtils.isEmpty(time)) {
                                    time = "00:00";
                                }
                                vBinding.txtAudioTime.setText(time);
                                vBinding.slider.setProgress(mediaPlayer.getCurrentPosition());
                            }
                        }, 100, 100);
                    }
                });

                vBinding.slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser) {
                            mediaPlayer.seekTo(vBinding.slider.getProgress());
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        mediaPlayerWasPlaying = mediaPlayer.isPlaying();
                        if (mediaPlayerWasPlaying) {
                            mediaPlayer.pause();
                            if (timer != null) {
                                timer.cancel();
                            }
                        }
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        mediaPlayer.seekTo(seekBar.getProgress());
                        if (mediaPlayerWasPlaying) {
                            mediaPlayer.start();
                            startPlaybackProgressTimer();
                        }

                    }
                });
            }

            private void startPlaybackProgressTimer() {
                if (timer != null) {
                    timer.cancel();
                }
                timer = new Timer();
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        int duration = mediaPlayer.getCurrentPosition();
                        String time = String.format(Locale.ENGLISH, "%d:%02d",
                                TimeUnit.MILLISECONDS.toMinutes(duration),
                                TimeUnit.MILLISECONDS.toSeconds(duration) % 60);
                        // Update UI elements with the current playback progress
                        if (TextUtils.isEmpty(time)) {
                            time = "00:00";
                        }
                        vBinding.txtAudioTime.setText(time);
                        vBinding.slider.setProgress(duration);
                    }
                }, 0, 1000);
            }
        }

        public class SendAudioHolder extends RecyclerView.ViewHolder {

            private SentAudioMessageItemBinding vBinding;
            private MediaPlayer mediaPlayer;
            private Timer timer;
            private boolean mediaPlayerWasPlaying = false;


            public SendAudioHolder(@NonNull View itemView) {
                super(itemView);
                vBinding = SentAudioMessageItemBinding.bind(itemView);
                vBinding.slider.setMax(100);
            }

            private void setTime() {
                int duration = mediaPlayer.getDuration();
                if (duration < 0) {
                    return;
                }
                String time = String.format(Locale.ENGLISH, "%d:%d", TimeUnit.MILLISECONDS.toMinutes(duration), TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
                if (TextUtils.isEmpty(time)) {
                    time = "00:00";
                }
                vBinding.txtAudioTime.setText(time);
            }

            private void setupPlayer(String url) {
                mediaPlayer = setupMediaPlayer(url);
                if (mediaPlayer == null) {
                    return;
                }
                setListener();
            }

            private void setListener() {
                mediaPlayer.setOnPreparedListener(mp -> {
                    vBinding.slider.setMax(mediaPlayer.getDuration());
                    setTime();
                });

                mediaPlayer.setOnCompletionListener(mp -> {
                    if (timer != null) {
                        timer.cancel();
                    }
                    setTime();
                    vBinding.slider.setProgress(0);
                    vBinding.playButton.setImageResource(R.drawable.img_play_btn);
                });

                vBinding.playButton.setOnClickListener(v -> {
                    if (mediaPlayer.isPlaying()) {
                        vBinding.playButton.setImageResource(R.drawable.img_play_btn);
                        mediaPlayer.pause();
                        timer.cancel();
                    } else {
                        vBinding.playButton.setImageResource(R.drawable.img_puase_btn);
                        if (currentMediaPlayer != null && currentMediaPlayer.isPlaying()) {
                            currentMediaPlayer.seekTo(currentMediaPlayer.getDuration());
                        }
                        mediaPlayer.start();
                        currentMediaPlayer = mediaPlayer;
                        if (timer != null) {
                            timer.cancel();
                        }
                        timer = new Timer();
                        timer.scheduleAtFixedRate(new TimerTask() {
                            @Override
                            public void run() {
                                int duration = mediaPlayer.getCurrentPosition();
                                String time = String.format(Locale.ENGLISH, "%d:%d", TimeUnit.MILLISECONDS.toMinutes(duration), TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
                                if (TextUtils.isEmpty(time)) {
                                    time = "00:00";
                                }
                                vBinding.txtAudioTime.setText(time);
                                vBinding.slider.setProgress(mediaPlayer.getCurrentPosition());
                            }
                        }, 100, 100);
                    }
                });

                vBinding.slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser) {
                            mediaPlayer.seekTo(vBinding.slider.getProgress());
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        mediaPlayerWasPlaying = mediaPlayer.isPlaying();
                        if (mediaPlayerWasPlaying) {
                            mediaPlayer.pause();
                            if (timer != null) {
                                timer.cancel();
                            }
                        }
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        mediaPlayer.seekTo(seekBar.getProgress());
                        if (mediaPlayerWasPlaying) {
                            mediaPlayer.start();
                            startPlaybackProgressTimer();
                        }

                    }
                });
            }

            private void startPlaybackProgressTimer() {
                if (timer != null) {
                    timer.cancel();
                }
                timer = new Timer();
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        int duration = mediaPlayer.getCurrentPosition();
                        String time = String.format(Locale.ENGLISH, "%d:%02d",
                                TimeUnit.MILLISECONDS.toMinutes(duration),
                                TimeUnit.MILLISECONDS.toSeconds(duration) % 60);
                        // Update UI elements with the current playback progress
                        if (TextUtils.isEmpty(time)) {
                            time = "00:00";
                        }
                        vBinding.txtAudioTime.setText(time);
                        vBinding.slider.setProgress(duration);
                    }
                }, 0, 1000);
            }

        }

        private MediaPlayer setupMediaPlayer(String url) {
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                    .build()
            );
            try {
                mediaPlayer.setDataSource(url);
                mediaPlayer.prepareAsync();
                return mediaPlayer;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        // IMAGE HOLDER
        public class ImageViewHolder extends RecyclerView.ViewHolder {
            private MessageItemImageBinding binding;

            public ImageViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = MessageItemImageBinding.bind(itemView);
            }
        }

        public class SentImageHolder extends RecyclerView.ViewHolder {
            private SentMessageItemImageBinding vBinding;

            public SentImageHolder(@NonNull View itemView) {
                super(itemView);
                vBinding = SentMessageItemImageBinding.bind(itemView);
            }
        }

        // VENUE HOLDER
        public class VenueViewHolder extends RecyclerView.ViewHolder {
            private final VenueMessageItemBinding mBinding;

            public VenueViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = VenueMessageItemBinding.bind(itemView);
            }

            private void setListener(VenueObjectModel model) {
//                mBinding.txtInviteFriend.setOnClickListener( v -> {
//                    Utils.preventDoubleClick( v );
//                    InviteFriendBottomSheet inviteFriendDialog = new InviteFriendBottomSheet();
//                    inviteFriendDialog.venueObjectModel = model;
//                    inviteFriendDialog.setShareListener( data -> {
//
//                    } );
//                    inviteFriendDialog.show( getSupportFragmentManager(), "1" );
//                } );

                mBinding.getRoot().setOnClickListener(v -> {
                    Utils.preventDoubleClick(v);
                });

            }
        }

        public class SentVenueViewHolder extends RecyclerView.ViewHolder {
            private final SentVenueMessageItemBinding mBinding;

            public SentVenueViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = SentVenueMessageItemBinding.bind(itemView);
            }

            private void setListener(VenueObjectModel model) {
//                mBinding.txtInviteFriend.setOnClickListener( v -> {
//                    Utils.preventDoubleClick( v );
//                    InviteFriendBottomSheet inviteFriendDialog = new InviteFriendBottomSheet();
//                    inviteFriendDialog.venueObjectModel = model;
//                    inviteFriendDialog.setShareListener( data -> {
//
//                    } );
//                    inviteFriendDialog.show( getSupportFragmentManager(), "1" );
//                } );

                mBinding.getRoot().setOnClickListener(v -> {
                    Utils.preventDoubleClick(v);
                });

            }
        }

        //USER HOLDER
        public class UserViewHolder extends RecyclerView.ViewHolder {

            private final UserMessageItemBinding binding;

            public UserViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = UserMessageItemBinding.bind(itemView);
            }

            private void setup(ChatMessageModel model) {
                UserDetailModel userDetailModel = new Gson().fromJson(model.getMsg(), UserDetailModel.class);
                binding.txtTime.setText(model.getSendTime());
                Graphics.loadImageWithFirstLetter(userDetailModel.getImage(), binding.ivUserProfile, userDetailModel.getFullName());
                binding.tvUserName.setText(userDetailModel.getFullName());
                binding.txtSender.setText(model.getAuthorName());
                binding.btnFollow.setText(Utils.followButtonTitle(userDetailModel.getFollow()));

                if (userDetailModel.getId().equals(SessionManager.shared.getUser().getId())) {
                    binding.btnFollow.setVisibility(View.GONE);
                } else {
                    binding.btnFollow.setVisibility(View.VISIBLE);
                }
                binding.btnFollow.setOnClickListener(v -> {
                    if (!binding.btnFollow.getText().equals("Requested")) {
                        reqFollowUnFollow(userDetailModel, data -> {
                            userDetailModel.setFollow(data);
                            binding.btnFollow.setText(Utils.followButtonTitle(userDetailModel.getFollow()));
                        });
                    }
                });
                binding.getRoot().setOnClickListener(v -> openProfile(userDetailModel.getId()));
                binding.layoutSend.setOnClickListener(v -> {
                    startActivity(new Intent(activity, VenueShareActivity.class)
                            .putExtra("userModel", model.getMsg())
                            .putExtra("type", "user"));
                });

            }
        }

        public class SentUserViewHolder extends RecyclerView.ViewHolder {

            private final SentUserMessageItemBinding binding;

            public SentUserViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = SentUserMessageItemBinding.bind(itemView);
            }

            private void setup(ChatMessageModel model) {
                UserDetailModel userDetailModel = new Gson().fromJson(model.getMsg(), UserDetailModel.class);
                updateReplyByTextView(binding.tvTitleReplyBy, model.getReply_by());

                binding.txtTime.setText(model.getSendTime());
                binding.imgMsgStatus.setImageDrawable(model.getStatusIcon());
                Graphics.loadImageWithFirstLetter(userDetailModel.getImage(), binding.ivUserProfile, userDetailModel.getFullName());
                binding.tvUserName.setText(userDetailModel.getFullName());
                binding.btnFollow.setText(Utils.followButtonTitle(userDetailModel.getFollow()));
                if (userDetailModel.getId().equals(SessionManager.shared.getUser().getId())) {
                    binding.btnFollow.setVisibility(View.GONE);
                } else {
                    binding.btnFollow.setVisibility(View.VISIBLE);
                }
                binding.btnFollow.setOnClickListener(v -> {
                    if (!binding.btnFollow.getText().equals("Requested")) {
                        reqFollowUnFollow(userDetailModel, data -> {
                            userDetailModel.setFollow(data);
                            binding.btnFollow.setText(Utils.followButtonTitle(userDetailModel.getFollow()));
                        });
                    }
                });
                binding.getRoot().setOnClickListener(v -> openProfile(userDetailModel.getId()));
                binding.layoutSend.setOnClickListener(v -> {
                    startActivity(new Intent(activity, VenueShareActivity.class)
                            .putExtra("userModel", model.getMsg())
                            .putExtra("type", "user"));
                });
            }
        }


        // STORY HOLDER
        public class StoryViewHolder extends RecyclerView.ViewHolder {
            private final StoryMessageItemBinding mBinding;

            public StoryViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = StoryMessageItemBinding.bind(itemView);
            }

            private void setupData(VenueObjectModel model) {
                mBinding.tvTitle.setText(model.getName());
                Graphics.loadImageWithFirstLetter(model.getLogo(), mBinding.iconImg, model.getName());
                storyHideShow(model, mBinding.roundCornerConstraintLayout, mBinding.txtStoryUnavailable);
                setStoryThumb(model, mBinding.ivMsg);
                setListener(model);
            }

            private void setListener(VenueObjectModel model) {
                mBinding.constraintHeader.setOnClickListener(v -> {
                    Utils.preventDoubleClick(v);
                    if (model == null) {
                        return;
                    }
                });
                mBinding.getRoot().setOnClickListener(v -> {
                    Utils.preventDoubleClick(v);
                    if (mBinding.roundCornerConstraintLayout.getVisibility() == View.GONE) {
                        return;
                    }
                    if (model == null) {
                        return;
                    }
                    if (model.getStories() == null) {
                        return;
                    }
                    if (model.getStories().isEmpty()) {
                        return;
                    }
                    openStory(model);
                });

            }
        }

        public class SentStoryViewHolder extends RecyclerView.ViewHolder {
            private final SentStoryItemBinding mBinding;

            public SentStoryViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = SentStoryItemBinding.bind(itemView);
            }

            private void setupData(VenueObjectModel model) {
                mBinding.tvTitle.setText(model.getName());
                Graphics.loadImageWithFirstLetter(model.getLogo(), mBinding.iconImg, model.getName());
                storyHideShow(model, mBinding.roundCornerConstraintLayout, mBinding.txtStoryUnavailable);
                setStoryThumb(model, mBinding.ivMsg);
                setListener(model);
            }

            private void setListener(VenueObjectModel model) {
                mBinding.constraintHeader.setOnClickListener(v -> {
                    Utils.preventDoubleClick(v);
                    if (model == null) {
                        return;
                    }
                });
                mBinding.getRoot().setOnClickListener(v -> {
                    Utils.preventDoubleClick(v);
                    if (mBinding.roundCornerConstraintLayout.getVisibility() == View.GONE) {
                        return;
                    }
                    if (model == null) {
                        return;
                    }
                    if (model.getStories() == null) {
                        return;
                    }
                    if (model.getStories().isEmpty()) {
                        return;
                    }
                    openStory(model);
                });
            }
        }

        private void setStoryThumb(VenueObjectModel model, ImageView imageView) {
            if (!model.getStories().isEmpty()) {
                if (model.getStories().get(0).getMediaType().equals("photo")) {
                    Graphics.loadImage(model.getStories().get(0).getMediaUrl(), imageView);
                } else {
                    Graphics.loadVideoThumbnail(model.getStories().get(0).getMediaUrl(), imageView);
                }
            }
        }

        private void storyHideShow(VenueObjectModel model, RoundCornerConstraintLayout layout, TextView txtStoryUnavailable) {
            if (model.getStories() == null || model.getStories().isEmpty()) {
                layout.setVisibility(View.GONE);
                txtStoryUnavailable.setVisibility(View.VISIBLE);
            } else {
                Date storyDate = model.getStories().get(0).getExpiryDate();
                Date newDate = Utils.incrementDateByNHour(storyDate, 24);
                if (Utils.isFutureDate(newDate)) {
                    layout.setVisibility(View.VISIBLE);
                    txtStoryUnavailable.setVisibility(View.GONE);
                } else {
                    layout.setVisibility(View.GONE);
                    txtStoryUnavailable.setVisibility(View.VISIBLE);
                }
            }
        }

        //OFFER HOLDER
        public class OfferViewHolder extends RecyclerView.ViewHolder {
            private final OfferViewItemBinding binding;

            public OfferViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = OfferViewItemBinding.bind(itemView);
            }

            private void setup(ChatMessageModel model) {
                binding.txtTime.setText(model.getSendTime());
                OffersModel offerModel = new Gson().fromJson(model.getMsg(), OffersModel.class);
                binding.txtTitle.setText(offerModel.getTitle());
                binding.txtDescription.setText(offerModel.getDescription());

                Graphics.loadImage(offerModel.getImage(), binding.imgOffer);
                Drawable drawableCalender = ContextCompat.getDrawable(Graphics.context, R.drawable.icon_venue_calender);
                drawableCalender.setBounds(0, 0, drawableCalender.getIntrinsicWidth(), drawableCalender.getIntrinsicHeight());
                binding.txtDays.setCompoundDrawables(drawableCalender, null, null, null);
                binding.txtDays.setText(offerModel.getDays());

                binding.txtFromDate.setText(TextUtils.isEmpty(offerModel.getStartTime()) ? "Ongoing" : Utils.convertMainDateFormat(offerModel.getStartTime()));
                binding.tillDateLayout.setVisibility(TextUtils.isEmpty(offerModel.getStartTime()) ? View.GONE : View.VISIBLE);
                if (!TextUtils.isEmpty(offerModel.getStartTime())) {
                    binding.txtTillDate.setText(String.format("%s", Utils.convertMainDateFormat(offerModel.getEndTime())));
                }
                if (offerModel.getVenue() != null) {
                    binding.venueContainer.setVenueDetail(offerModel.getVenue());
                }

                Drawable drawable = ContextCompat.getDrawable(Graphics.context, R.drawable.icon_time);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                binding.txtOfferTime.setCompoundDrawables(drawable, null, null, null);
                binding.txtOfferTime.setText(offerModel.getOfferTiming());

//                binding.inviteButton.setOnClickListener( view -> {
//                    Utils.preventDoubleClick( view );
//                    if (offerModel.getVenue() == null) { return; }
//                    Utils.openInviteButtonSheet( offerModel, offerModel.getVenue(), getSupportFragmentManager() );
//                } );

                binding.getRoot().setOnClickListener(v -> {
                });

                binding.layoutSend.setOnClickListener(v -> {
                    startActivity(new Intent(activity, VenueShareActivity.class)
                            .putExtra("offer", model.getMsg())
                            .putExtra("type", "offer"));
                });
            }
        }

        public class SentOfferViewHolder extends RecyclerView.ViewHolder {

            private final SentOfferViewItemBinding binding;

            public SentOfferViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = SentOfferViewItemBinding.bind(itemView);
            }

            private void setup(ChatMessageModel model) {
                binding.txtTime.setText(model.getSendTime());
                binding.imgMsgStatus.setImageDrawable(model.getStatusIcon());
                OffersModel offerModel = new Gson().fromJson(model.getMsg(), OffersModel.class);
                binding.txtTitle.setText(offerModel.getTitle());
                binding.txtDescription.setText(offerModel.getDescription());

                Graphics.loadImage(offerModel.getImage(), binding.imgOffer);
                Drawable drawableCalender = ContextCompat.getDrawable(Graphics.context, R.drawable.icon_venue_calender);
                drawableCalender.setBounds(0, 0, drawableCalender.getIntrinsicWidth(), drawableCalender.getIntrinsicHeight());
                binding.txtDays.setCompoundDrawables(drawableCalender, null, null, null);
                binding.txtDays.setText(offerModel.getDays());

                binding.txtFromDate.setText(TextUtils.isEmpty(offerModel.getStartTime()) ? "Ongoing" : Utils.convertMainDateFormatReview(offerModel.getStartTime()));
                binding.tillDateLayout.setVisibility(TextUtils.isEmpty(offerModel.getStartTime()) ? View.GONE : View.VISIBLE);
                if (!TextUtils.isEmpty(offerModel.getStartTime())) {
                    binding.txtTillDate.setText(Utils.convertMainDateFormat(offerModel.getEndTime()));
                }

                Drawable drawable = ContextCompat.getDrawable(Graphics.context, R.drawable.icon_time);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                binding.txtOfferTime.setCompoundDrawables(drawable, null, null, null);
                binding.txtOfferTime.setText(offerModel.getOfferTiming());

                if (offerModel.getVenue() != null) {
                    binding.venueContainer.setVenueDetail(offerModel.getVenue());
                }

//                binding.inviteButton.setOnClickListener( view -> {
//                    Utils.preventDoubleClick( view );
//                    if (offerModel.getVenue() == null) { return; }
//                    Utils.openInviteButtonSheet( offerModel, offerModel.getVenue(), getSupportFragmentManager() );
//                } );

                binding.getRoot().setOnClickListener(v -> {
                });

                binding.layoutSend.setOnClickListener(v -> {
                    startActivity(new Intent(activity, VenueShareActivity.class)
                            .putExtra("offer", model.getMsg())
                            .putExtra("type", "offer"));
                });
            }
        }

        public class SentPromoterEventViewHolder extends RecyclerView.ViewHolder {

            private EventMessageItemBinding binding;

            public SentPromoterEventViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = EventMessageItemBinding.bind(itemView);
            }

            private void setUp(ChatMessageModel model) {
                PromoterEventModel promoterEventModel = new Gson().fromJson(model.getMsg(), PromoterEventModel.class);
                Graphics.loadImage(promoterEventModel.getCustomVenue().getImage(), binding.imageVenue);
                Graphics.loadImage(promoterEventModel.getCustomVenue().getLogo(), binding.image);

                binding.titleText.setText(promoterEventModel.getCustomVenue().getName());
                binding.subTitleText.setText(promoterEventModel.getCustomVenue().getAddress());
                binding.txtDate.setText(Utils.changeDateFormat(promoterEventModel.getDate(), AppConstants.DATEFORMAT_SHORT, "EEE\ndd\nMMM"));


                binding.txtStartTime.setText(promoterEventModel.getStartTime());
                binding.txtendTime.setText(promoterEventModel.getEndTime());

                binding.getRoot().setOnClickListener(view -> {
                    Utils.preventDoubleClick(view);
                });

                binding.layoutSend.setOnClickListener(v -> {
                    startActivity(new Intent(activity, VenueShareActivity.class)
                            .putExtra("promoterEvent", new Gson().toJson(promoterEventModel))
                            .putExtra("type", "promoterEvent"));
                });
            }
        }

        public class PromoterEventViewHolder extends RecyclerView.ViewHolder {

            private SentEventMessageItemBinding binding;

            public PromoterEventViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = SentEventMessageItemBinding.bind(itemView);
            }

            private void setUp(ChatMessageModel model) {
                binding.txtTime.setText(model.getSendTime());
                PromoterEventModel promoterEventModel = new Gson().fromJson(model.getMsg(), PromoterEventModel.class);
                Graphics.loadImage(promoterEventModel.getCustomVenue().getImage(), binding.imageVenue);
                Graphics.loadImage(promoterEventModel.getCustomVenue().getLogo(), binding.image);

                binding.txtStartTime.setText(promoterEventModel.getStartTime());
                binding.txtendTime.setText(promoterEventModel.getEndTime());

                binding.titleText.setText(promoterEventModel.getCustomVenue().getName());
                binding.subTitleText.setText(promoterEventModel.getCustomVenue().getAddress());
                binding.txtDate.setText(Utils.changeDateFormat(promoterEventModel.getDate(), AppConstants.DATEFORMAT_SHORT, "EEE\ndd\nMMM"));

                binding.getRoot().setOnClickListener(view -> {
                    Utils.preventDoubleClick(view);
                });

            }
        }

        public class PromoterEventReplyViewHolder extends RecyclerView.ViewHolder {

            private ItemEventReplyToBinding binding;

            public PromoterEventReplyViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemEventReplyToBinding.bind(itemView);
            }

            private void setUp(ChatMessageModel model) {
                PromoterEventModel promoterEventModel = new Gson().fromJson(model.getReplyToModel().getData(), PromoterEventModel.class);
                Graphics.loadImage(promoterEventModel.getCustomVenue().getImage(), binding.imageVenue);
                Graphics.loadImage(promoterEventModel.getCustomVenue().getLogo(), binding.image);

                binding.txtStartTime.setText(promoterEventModel.getStartTime());
                binding.txtendTime.setText(promoterEventModel.getEndTime());

                binding.titleText.setText(promoterEventModel.getCustomVenue().getName());
                binding.subTitleText.setText(promoterEventModel.getCustomVenue().getAddress());
                binding.txtDate.setText(Utils.changeDateFormat(promoterEventModel.getDate(), AppConstants.DATEFORMAT_SHORT, "EEE\ndd\nMMM"));

                binding.getRoot().setOnClickListener(view -> {
                    Utils.preventDoubleClick(view);
                });

            }
        }

        public class PromoterSentEventReplyToViewHolder extends RecyclerView.ViewHolder {

            private SentEventReplyToItemBinding binding;

            public PromoterSentEventReplyToViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = SentEventReplyToItemBinding.bind(itemView);
            }

            private void setUp(ChatMessageModel model) {
                binding.txtTime.setText(model.getSendTime());
                PromoterEventModel promoterEventModel = new Gson().fromJson(model.getReplyToModel().getData(), PromoterEventModel.class);
                Graphics.loadImage(promoterEventModel.getCustomVenue().getImage(), binding.imageVenue);
                Graphics.loadImage(promoterEventModel.getCustomVenue().getLogo(), binding.image);

                binding.txtStartTime.setText(promoterEventModel.getStartTime());
                binding.txtendTime.setText(promoterEventModel.getEndTime());

                binding.titleText.setText(promoterEventModel.getCustomVenue().getName());
                binding.subTitleText.setText(promoterEventModel.getCustomVenue().getAddress());
                binding.txtDate.setText(Utils.changeDateFormat(promoterEventModel.getDate(), AppConstants.DATEFORMAT_SHORT, "EEE\ndd\nMMM"));

                binding.getRoot().setOnClickListener(view -> {
                    Utils.preventDoubleClick(view);
                });

            }
        }

        public class RaynaTicketSentViewHolder extends RecyclerView.ViewHolder {

            private ItemRaynaTicketSentViewBinding binding;

            public RaynaTicketSentViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemRaynaTicketSentViewBinding.bind(itemView);
            }

            public void setupRaynaImagesData(List<String> imaegs, RaynaTicketDetailModel raynaTicketDetailModel) {
                RaynaTicketImageAdapter adapter = new RaynaTicketImageAdapter(activity, raynaTicketDetailModel, imaegs,true);
                binding.viewPager.setAdapter(adapter);
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            int nextPage = binding.viewPager.getCurrentItem() + 1;
                            if (nextPage == adapter.getCount()) {
                                nextPage = 0;
                            }
                            binding.viewPager.setCurrentItem(nextPage, true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            handler.postDelayed(this, 4000);
                        }
                    }
                };
                handler.postDelayed(runnable, 4000);

                binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        handler.removeCallbacks(runnable);
                        handler.postDelayed(runnable, 4000);
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                    }
                });

            }

            private void setUp(ChatMessageModel model) {

                binding.txtTime.setText(model.getSendTime());

                RaynaTicketDetailModel raynaTicketDetailModel = new Gson().fromJson(model.getMsg(), RaynaTicketDetailModel.class);


                activity.runOnUiThread(() -> {
                    if (raynaTicketDetailModel.getImages() != null && !raynaTicketDetailModel.getImages().isEmpty()) {
                        List<String> urls = raynaTicketDetailModel.getImages();
                        urls.removeIf(Utils::isVideo);
                        setupRaynaImagesData(raynaTicketDetailModel.getImages(), raynaTicketDetailModel);
                    }

                    binding.txtTitle.setText(Utils.notNullString(raynaTicketDetailModel.getTitle()));

                    binding.ticketAddress.setText(raynaTicketDetailModel.getCity());

                    String startingAmount = raynaTicketDetailModel.getStartingAmount() != null ? String.valueOf(raynaTicketDetailModel.getStartingAmount()) : "N/A";


                    if (raynaTicketDetailModel.getDiscount() != 0) {
                        binding.tvDiscount.setVisibility(View.VISIBLE);
                        binding.tvDiscount.setText(String.valueOf(raynaTicketDetailModel.getDiscount()) + "%");
                    } else {
                        binding.tvDiscount.setVisibility(View.GONE);
                    }


                    if (startingAmount.equals("N/A")) {
                        Utils.setStyledText(activity, binding.tvAED, "0");
                    } else {
                        Utils.setStyledText(activity, binding.tvAED, Utils.roundFloatValue(Float.valueOf(startingAmount)));
                    }

                    if (!"N/A".equals(startingAmount)) {
                        String amount = Utils.roundFloatValue(Float.valueOf(startingAmount));
                        SpannableString styledPrice = Utils.getStyledText(activity, amount);
                        SpannableStringBuilder fullText = new SpannableStringBuilder()
                                .append("From ")
                                .append(styledPrice);

                        binding.ticketFromAmount.setText(fullText);
                    } else {
                        SpannableString styledPrice = Utils.getStyledText(activity, "0");
                        SpannableStringBuilder fullText = new SpannableStringBuilder()
                                .append("From ")
                                .append(styledPrice);
                        binding.ticketFromAmount.setText(fullText);
                    }

                });

                binding.getRoot().setOnClickListener(view -> {
                    Utils.preventDoubleClick(view);
                    activity.startActivity(new Intent(activity, RaynaTicketDetailActivity.class).putExtra("ticketId", raynaTicketDetailModel.getId()));
                });

                binding.layoutSend.setOnClickListener(v -> startActivity(new Intent(activity, VenueShareActivity.class)
                        .putExtra("rayna", new Gson().toJson(raynaTicketDetailModel))
                        .putExtra("type", "ticket")));




            }
        }

        public class RaynaTicketReceiveViewHolder extends RecyclerView.ViewHolder {

            private ItemRaynaTicketReceiveBinding binding;

            public RaynaTicketReceiveViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemRaynaTicketReceiveBinding.bind(itemView);
            }

            public void setupRaynaImagesData(List<String> imaegs, RaynaTicketDetailModel raynaTicketDetailModel) {
                RaynaTicketImageAdapter adapter = new RaynaTicketImageAdapter(activity, raynaTicketDetailModel, imaegs,true);
                binding.viewPager.setAdapter(adapter);
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            int nextPage = binding.viewPager.getCurrentItem() + 1;
                            if (nextPage == adapter.getCount()) {
                                nextPage = 0;
                            }
                            binding.viewPager.setCurrentItem(nextPage, true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            ticketImageHandler.postDelayed(this, 4000);
                        }
                    }
                };
                ticketImageHandler.postDelayed(runnable, 4000);

                binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        ticketImageHandler.removeCallbacks(runnable);
                        ticketImageHandler.postDelayed(runnable, 4000);
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                    }
                });

            }

            private void setUp(ChatMessageModel model) {
                binding.txtTime.setText(model.getSendTime());

                RaynaTicketDetailModel raynaTicketDetailModel = new Gson().fromJson(model.getMsg(), RaynaTicketDetailModel.class);

                activity.runOnUiThread(() -> {
                    if (raynaTicketDetailModel.getImages() != null && !raynaTicketDetailModel.getImages().isEmpty()) {
                        List<String> urls = raynaTicketDetailModel.getImages();
                        urls.removeIf(Utils::isVideo);
                        setupRaynaImagesData(raynaTicketDetailModel.getImages(), raynaTicketDetailModel);
                    }

                    binding.txtTitle.setText(Utils.notNullString(raynaTicketDetailModel.getTitle()));

                    binding.ticketAddress.setText(raynaTicketDetailModel.getCity());

                    String startingAmount = raynaTicketDetailModel.getStartingAmount() != null ? String.valueOf(raynaTicketDetailModel.getStartingAmount()) : "N/A";


                    if (raynaTicketDetailModel.getDiscount() != 0) {
                        binding.tvDiscount.setVisibility(View.VISIBLE);
                        binding.tvDiscount.setText(String.valueOf(raynaTicketDetailModel.getDiscount()) + "%");
                    } else {
                        binding.tvDiscount.setVisibility(View.GONE);
                    }


                    if (startingAmount.equals("N/A")) {
                        Utils.setStyledText(activity, binding.tvAED, "0");
                    } else {
                        Utils.setStyledText(activity, binding.tvAED, Utils.roundFloatValue(Float.valueOf(startingAmount)));
                    }

                    if (!"N/A".equals(startingAmount)) {
                        String amount = Utils.roundFloatValue(Float.valueOf(startingAmount));
                        SpannableString styledPrice = Utils.getStyledText(activity, amount);
                        SpannableStringBuilder fullText = new SpannableStringBuilder()
                                .append("From ")
                                .append(styledPrice);

                        binding.ticketFromAmount.setText(fullText);
                    } else {
                        SpannableString styledPrice = Utils.getStyledText(activity, "0");
                        SpannableStringBuilder fullText = new SpannableStringBuilder()
                                .append("From ")
                                .append(styledPrice);
                        binding.ticketFromAmount.setText(fullText);
                    }

                });

                binding.getRoot().setOnClickListener(view -> {
                    Utils.preventDoubleClick(view);
                    activity.startActivity(new Intent(activity, RaynaTicketDetailActivity.class).putExtra("ticketId", raynaTicketDetailModel.getId()));
                });

                binding.layoutSend.setOnClickListener(v -> startActivity(new Intent(activity, VenueShareActivity.class)
                        .putExtra("rayna", new Gson().toJson(raynaTicketDetailModel))
                        .putExtra("type", "ticket")));
            }
        }

        public class RaynaTicketSentReplyToViewHolder extends RecyclerView.ViewHolder {

            private SentRaynaticketItemBinding binding;

            public RaynaTicketSentReplyToViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = SentRaynaticketItemBinding.bind(itemView);
            }

            private void setUp(ChatMessageModel model) {
                binding.txtTime.setText(model.getSendTime());

                RaynaTicketDetailModel raynaTicketDetailModel = new Gson().fromJson(model.getReplyToModel().getData(), RaynaTicketDetailModel.class);

                if (raynaTicketDetailModel != null){
                    activity.runOnUiThread(() -> {
                        if (raynaTicketDetailModel.getImages() != null && !raynaTicketDetailModel.getImages().isEmpty()) {
                            List<String> urls = raynaTicketDetailModel.getImages();
                            urls.removeIf(Utils::isVideo);
                            setupRaynaImagesData(raynaTicketDetailModel.getImages(), raynaTicketDetailModel);
                        }

                        binding.txtTitle.setText(Utils.notNullString(raynaTicketDetailModel.getTitle()));

                        binding.ticketAddress.setText(raynaTicketDetailModel.getCity());

                        String startingAmount = raynaTicketDetailModel.getStartingAmount() != null ? String.valueOf(raynaTicketDetailModel.getStartingAmount()) : "N/A";


                        if (raynaTicketDetailModel.getDiscount() != 0) {
                            binding.tvDiscount.setVisibility(View.VISIBLE);
                            binding.tvDiscount.setText(String.valueOf(raynaTicketDetailModel.getDiscount()) + "%");
                        } else {
                            binding.tvDiscount.setVisibility(View.GONE);
                        }


                        if (startingAmount.equals("N/A")) {
                            Utils.setStyledText(activity, binding.tvAED, "0");
                        } else {
                            Utils.setStyledText(activity, binding.tvAED, Utils.roundFloatValue(Float.valueOf(startingAmount)));
                        }

                        if (!"N/A".equals(startingAmount)) {
                            String amount = Utils.roundFloatValue(Float.valueOf(startingAmount));
                            SpannableString styledPrice = Utils.getStyledText(activity, amount);
                            SpannableStringBuilder fullText = new SpannableStringBuilder()
                                    .append("From ")
                                    .append(styledPrice);

                            binding.ticketFromAmount.setText(fullText);
                        } else {
                            SpannableString styledPrice = Utils.getStyledText(activity, "0");
                            SpannableStringBuilder fullText = new SpannableStringBuilder()
                                    .append("From ")
                                    .append(styledPrice);
                            binding.ticketFromAmount.setText(fullText);
                        }

                    });
                }


                binding.getRoot().setOnClickListener(view -> {
                    Utils.preventDoubleClick(view);
                    activity.startActivity(new Intent(activity, RaynaTicketDetailActivity.class).putExtra("ticketId", raynaTicketDetailModel.getId()));
                });

                binding.getRoot().setOnClickListener(view -> {
                    Utils.preventDoubleClick(view);

                });

            }

            public void setupRaynaImagesData(List<String> imaegs, RaynaTicketDetailModel raynaTicketDetailModel) {
                RaynaTicketImageAdapter adapter = new RaynaTicketImageAdapter(activity, raynaTicketDetailModel, imaegs,true);
                binding.viewPager.setAdapter(adapter);
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            int nextPage = binding.viewPager.getCurrentItem() + 1;
                            if (nextPage == adapter.getCount()) {
                                nextPage = 0;
                            }
                            binding.viewPager.setCurrentItem(nextPage, true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            ticketImageHandler.postDelayed(this, 4000);
                        }
                    }
                };
                ticketImageHandler.postDelayed(runnable, 4000);

                binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        ticketImageHandler.removeCallbacks(runnable);
                        ticketImageHandler.postDelayed(runnable, 4000);
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                    }
                });

            }
        }

        public class RaynaTicketRevisedReplyToViewHolder extends RecyclerView.ViewHolder {

            private ItemRaynaticketReplyToBinding binding;

            public RaynaTicketRevisedReplyToViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemRaynaticketReplyToBinding.bind(itemView);
            }

            private void setUp(ChatMessageModel model) {
                binding.txtTime.setText(model.getSendTime());

                RaynaTicketDetailModel raynaTicketDetailModel = new Gson().fromJson(model.getReplyToModel().getData(), RaynaTicketDetailModel.class);

                if (raynaTicketDetailModel != null){
                    activity.runOnUiThread(() -> {
                        if (raynaTicketDetailModel.getImages() != null && !raynaTicketDetailModel.getImages().isEmpty()) {
                            List<String> urls = raynaTicketDetailModel.getImages();
                            urls.removeIf(Utils::isVideo);
                            setupRaynaImagesData(raynaTicketDetailModel.getImages(), raynaTicketDetailModel);
                        }

                        binding.txtTitle.setText(Utils.notNullString(raynaTicketDetailModel.getTitle()));

                        binding.ticketAddress.setText(raynaTicketDetailModel.getCity());

                        String startingAmount = raynaTicketDetailModel.getStartingAmount() != null ? String.valueOf(raynaTicketDetailModel.getStartingAmount()) : "N/A";


                        if (raynaTicketDetailModel.getDiscount() != 0) {
                            binding.tvDiscount.setVisibility(View.VISIBLE);
                            binding.tvDiscount.setText(String.valueOf(raynaTicketDetailModel.getDiscount()) + "%");
                        } else {
                            binding.tvDiscount.setVisibility(View.GONE);
                        }


                        if (startingAmount.equals("N/A")) {
                            Utils.setStyledText(activity, binding.tvAED, "0");
                        } else {
                            Utils.setStyledText(activity, binding.tvAED, Utils.roundFloatValue(Float.valueOf(startingAmount)));
                        }

                        if (!"N/A".equals(startingAmount)) {
                            String amount = Utils.roundFloatValue(Float.valueOf(startingAmount));
                            SpannableString styledPrice = Utils.getStyledText(activity, amount);
                            SpannableStringBuilder fullText = new SpannableStringBuilder()
                                    .append("From ")
                                    .append(styledPrice);

                            binding.ticketFromAmount.setText(fullText);
                        } else {
                            SpannableString styledPrice = Utils.getStyledText(activity, "0");
                            SpannableStringBuilder fullText = new SpannableStringBuilder()
                                    .append("From ")
                                    .append(styledPrice);
                            binding.ticketFromAmount.setText(fullText);
                        }

                    });
                }


                binding.getRoot().setOnClickListener(view -> {
                    Utils.preventDoubleClick(view);
                    activity.startActivity(new Intent(activity, RaynaTicketDetailActivity.class).putExtra("ticketId", raynaTicketDetailModel.getId()));
                });

                binding.getRoot().setOnClickListener(view -> {
                    Utils.preventDoubleClick(view);

                });

            }

            public void setupRaynaImagesData(List<String> imaegs, RaynaTicketDetailModel raynaTicketDetailModel) {
                RaynaTicketImageAdapter adapter = new RaynaTicketImageAdapter(activity, raynaTicketDetailModel, imaegs,true);
                binding.viewPager.setAdapter(adapter);
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            int nextPage = binding.viewPager.getCurrentItem() + 1;
                            if (nextPage == adapter.getCount()) {
                                nextPage = 0;
                            }
                            binding.viewPager.setCurrentItem(nextPage, true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            ticketImageHandler.postDelayed(this, 4000);
                        }
                    }
                };
                ticketImageHandler.postDelayed(runnable, 4000);

                binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        ticketImageHandler.removeCallbacks(runnable);
                        ticketImageHandler.postDelayed(runnable, 4000);
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                    }
                });

            }
        }


    }

    private void openStory(VenueObjectModel model) {
        List<VenueObjectModel> venueObjectModelList = new ArrayList<>();
        venueObjectModelList.add(model);
        Intent intent = new Intent(activity, StoryViewActivity.class);
        intent.putExtra("stories", new Gson().toJson(venueObjectModelList));
        intent.putExtra("selectedPosition", 0);
        startActivity(intent);
    }

    private void openProfile(String userId) {
        if (SessionManager.shared.getUser().getId().equals(userId)) {
            startActivity(new Intent(activity, ProfileFragment.class));
        } else {
            startActivity(new Intent(activity, OtherUserProfileActivity.class).putExtra("friendId", userId));
        }
    }

    public void updateReplyByTextView(TextView textView, String replyById) {
        textView.setVisibility(View.GONE);
    }

    // endregion
    // --------------------------------------
}


