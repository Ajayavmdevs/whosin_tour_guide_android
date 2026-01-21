package com.whosin.app.ui.activites.home.event;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.ActivityEventDetailsBinding;
import com.whosin.app.databinding.ImageListBinding;
import com.whosin.app.databinding.ItemHighlightBinding;
import com.whosin.app.databinding.ItemSelectFriendsBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.Repository.ChatRepository;
import com.whosin.app.service.models.BucketEventListModel;
import com.whosin.app.service.models.ChatMessageModel;
import com.whosin.app.service.models.ChatModel;
import com.whosin.app.service.models.ContactListModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.EventDetailModel;
import com.whosin.app.service.models.EventInviteGuestModel;
import com.whosin.app.service.models.EventModel;
import com.whosin.app.service.models.MessageEvent;
import com.whosin.app.service.models.PackageModel;
import com.whosin.app.service.models.VenueObjectModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.Profile.OtherUserProfileActivity;
import com.whosin.app.ui.activites.comman.BaseActivity;
import com.whosin.app.ui.activites.home.Chat.ChatMessageActivity;
import com.whosin.app.ui.adapter.OfferPackagesAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.realm.RealmList;

public class EventDetailsActivity extends BaseActivity {
    private ActivityEventDetailsBinding binding;
    private EventModel eventModel;
    private EventDetailModel eventDetailModel;
    private final InvitePeopleAdapter<ContactListModel> invitePepoleAdapter = new InvitePeopleAdapter<>();
    private final EventAdminAdapter<ContactListModel> adminAdapter = new EventAdminAdapter<>();
    private final OfferPackagesAdapter<PackageModel> packageAdapter = new OfferPackagesAdapter<>();
    private final HighLightsAdapter<ChatMessageModel> highLightsAdapter = new HighLightsAdapter<>();
    private final InGuestAdapter<ContactListModel> inGuestAdapter = new InGuestAdapter<>();
    private String eventId = "", venueId = "", title = "", image = "";
    private BucketEventListModel bucketEventListModel;
    private VenueObjectModel venueObjectModel;
    RealmList<String> userIdsList = new RealmList<>();

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    protected void initUi() {

        applyTranslations();

        Graphics.applyBlurEffect(activity, binding.blurViewHeader);
        Graphics.applyBlurEffect(activity, binding.blurViewTime);
        String model = getIntent().getStringExtra("eventsList");
        String name = getIntent().getStringExtra("name");
        String address = getIntent().getStringExtra("address");
        String image = getIntent().getStringExtra("image");

        if (image != null && !image.isEmpty() && name != null && !name.isEmpty()) {
            Graphics.loadImageWithFirstLetter(image, binding.iconImg, name);
            binding.tvName.setText(name);
        }

        if (!Utils.isNullOrEmpty(address)) {
            binding.tvWebsite.setText(address);
        } else {
            binding.tvWebsite.setVisibility(View.GONE);
        }

        if (model != null) {
            bucketEventListModel = new Gson().fromJson(model, BucketEventListModel.class);
            requestEventDetail(bucketEventListModel.getId());
        } else {
            eventId = getIntent().getStringExtra("eventId");
            if (!Utils.isNullOrEmpty(eventId)) {
                requestEventDetail(eventId);
            }
        }
        setAdapter();
    }

    @Override
    protected void setListeners() {
        Glide.with(activity).load(R.drawable.icon_left_back_arrow).into(binding.ivClose);
        binding.ivClose.setOnClickListener(view -> onBackPressed());
        binding.letsGoBtn.setOnClickListener(view -> {
            InviteGuestBottomSheet dialog = new InviteGuestBottomSheet();
            dialog.eventId = eventId;
            dialog.listener = data -> {
                if (data.equals("in")) {
                    binding.letsGoBtn.setVisibility(View.GONE);
                    binding.imOutBtn.setVisibility(View.GONE);
                    binding.cancelBtn.setVisibility(View.VISIBLE);
                }
            };
            dialog.show(getSupportFragmentManager(), "");
        });

        binding.imOutBtn.setOnClickListener(view -> {
            requestEventInviteStatus(eventId, "out");
        });

        binding.cancelBtn.setOnClickListener(view -> {
            requestEventInviteStatus(eventId, "out");
        });

//        binding.imgSend.setOnClickListener(view -> {
////            String message = binding.edtMessage.getText().toString();
////            if (Utils.isNullOrEmpty(message)) {
////                Toast.makeText(activity, "Please type message", Toast.LENGTH_SHORT).show();
////                return;
////            }
////            ChatMessageModel messageModel = new ChatMessageModel(message, "text", eventId, userIdsList);
////            ChatRepository.shared(this).addMessage(messageModel, data -> {
////                getMessages();
////                binding.edtMessage.setText("");
////                ChatManager.shared.sendMessage(messageModel);
////            });
//        });

        binding.eventInviteLayout.setOnClickListener(view -> {
            Utils.preventDoubleClick( view );
            InviteGuestListBottomSheet inviteGuestListBottomSheet = new InviteGuestListBottomSheet();
            inviteGuestListBottomSheet.eventId = eventId;
            inviteGuestListBottomSheet.show(getSupportFragmentManager(), "");
        });


        binding.buyNowBtn.setOnClickListener(view -> {
            startActivity(new Intent(activity, EventBuyNowActivity.class).putExtra("eventListDetail", new Gson().toJson(eventModel)).putExtra("venueModel", new Gson().toJson(venueObjectModel)));
        });

        binding.constrainHeader.setOnClickListener(view -> {
            String orgName = (eventModel.getOrgData() != null && eventModel.getOrgData().getName() != null) ? eventModel.getOrgData().getName() : "";
            String orgWebsite = (eventModel.getOrgData() != null && eventModel.getOrgData().getWebsite() != null) ? eventModel.getOrgData().getWebsite() : "";
            String orgLogo = (eventModel.getOrgData() != null && eventModel.getOrgData().getLogo() != null) ? eventModel.getOrgData().getLogo() : "";

            startActivity(new Intent(activity, EventOrganizerDetailsActivity.class)
                    .putExtra("org_id", eventModel.getOrgId())
                    .putExtra("type", "events_organizers")
                    .putExtra("name", orgName)
                    .putExtra("webSite", orgWebsite)
                    .putExtra("image", orgLogo));
        });

        binding.btnMessageUs.setOnClickListener(view -> {
            if (eventDetailModel != null) {
                ChatModel chatModel = new ChatModel(eventDetailModel);
                Intent intent = new Intent(activity, ChatMessageActivity.class);
                intent.putExtra("chatModel", new Gson().toJson(chatModel));
                startActivity(intent);
            }
        });

    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityEventDetailsBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        requestEventDetail(eventId);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        getMessages();
    }



    @Override
    protected Map<View, String> getTranslationMap() {
        Map<View, String> map = new HashMap<>();
        map.put(binding.txtEventAdminTitle, "event_admins");
        map.put(binding.tvBuyNowTitle, "buy_now");
        map.put(binding.tvExpiredTitle, "expired");
        map.put(binding.tvForHighLightTitle, "highlights");
        map.put(binding.eventViewAllHL, "view_all");
        map.put(binding.edtMessage, "type_your_message_here");
        map.put(binding.tvCancelTitle, "cancel");
        map.put(binding.btnBucketList, "lets_go");
        map.put(binding.txtMenu, "i_am_out");
        map.put(binding.tvMessageUsTitle, "message_us");

        binding.eventViewAll.setText(getValue("view_all") + " >");

        return map;
    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void getMessages() {
        if (eventId != null && !eventId.isEmpty()) {
            List<ChatMessageModel> messages = ChatRepository.shared(this).getChatMessages(eventId);
            if (messages != null && !messages.isEmpty()) {
                int totalMessages = messages.size();
                int startIndex = Math.max(0, totalMessages - 3);
                List<ChatMessageModel> latestMessages = messages.subList(startIndex, totalMessages);
                if (latestMessages != null && !latestMessages.isEmpty()) {
                    highLightsAdapter.updateData(latestMessages);
                }
            }
        }
    }

    private void setAdapter() {
        binding.eventPackageRecycleView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        binding.eventPackageRecycleView.setAdapter(packageAdapter);
//        binding.rvHighLights.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
//        binding.rvHighLights.setAdapter(highLightsAdapter);
        binding.eventAdminRecycle.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        binding.eventAdminRecycle.setAdapter(adminAdapter);
    }

    @SuppressLint("DefaultLocale")
    private void setEventDetails(EventModel eventModel) {
        if (eventModel != null) {

            eventId = eventModel.getId();
            title = eventModel.getTitle();
            image = eventModel.getImage();

//            getMessages();

            Graphics.loadImageWithFirstLetter(eventModel.getImage(), binding.iconImg, eventModel.getTitle());
            binding.tvName.setText(eventModel.getTitle());

            if (!Utils.isNullOrEmpty(eventModel.getImage())) {
                binding.tvWebsite.setText(eventModel.getImage());
            } else {
                binding.tvWebsite.setVisibility(View.GONE);
            }


            //VENUE
            if (eventModel.getCustomVenueObject() != null && eventModel.getCustomVenueObject().getName() != null && !eventModel.getCustomVenueObject().getName().isEmpty()) {
                binding.blurView.setVisibility(View.VISIBLE);
                Graphics.applyBlurEffect(activity, binding.blurView);
                binding.venueContainer.setVenueDetail(eventModel.getCustomVenueObject());
                venueObjectModel = eventModel.getCustomVenueObject();
            } else {
                Graphics.applyBlurEffect(activity, binding.blurView);
                binding.blurView.setVisibility(View.GONE);
            }


            //EVENT DETAIL
            if (eventModel.getOrgData() != null) {
                binding.tvName.setText(eventModel.getOrgData().getName());
                binding.tvWebsite.setText(eventModel.getOrgData().getWebsite());
                Graphics.loadImageWithFirstLetter(eventModel.getOrgData().getLogo(), binding.iconImg, eventModel.getOrgData().getName());
            }

            //SET TIMER
            if (eventModel.getEventTime() != null) {
                Utils.setTimer(eventModel.getEventTime(), binding.countTimer);
            }

            binding.eventTitle.setText(eventModel.getTitle());
            Graphics.loadImage(eventModel.getImage(), binding.cover);
            binding.eventDescription.setText(eventModel.getDescription());

            //ADMIN
            if (eventModel.getAdmins() != null && !eventModel.getAdmins().isEmpty()) {
                binding.eventsAdminLayout.setVisibility(View.VISIBLE);
                setAdminDetails(eventModel.getAdmins());
            } else {
                binding.eventsAdminLayout.setVisibility(View.GONE);
            }

            //INVITED USER
            if (eventModel.getInvitedGuests() != null && !eventModel.getInvitedGuests().isEmpty()) {
                setInvitedPeopleDetails(eventModel.getInvitedGuests());
            }

            if (eventModel.getInvitedGuestsCount() != 0) {
                binding.eventPepoleCount.setText(setValue("event_people_count",String.valueOf(eventModel.getInvitedGuestsCount()),String.valueOf(eventModel.getExtraGuestCount())));
            }


            //IN USER
            if (eventModel.getInGuests() != null && !eventModel.getInGuests().isEmpty()) {
                setInGuestAdapter(eventModel.getInGuests());
            }

            //PACKAGES
            if (eventModel.getPackages() != null && !eventModel.getPackages().isEmpty()) {
                binding.eventPackage.setVisibility(View.VISIBLE);
                binding.buyNowBtn.setVisibility(View.VISIBLE);
                packageAdapter.updateData(eventModel.getPackages());
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                try {
                    Date givenDate = dateFormat.parse(eventModel.getEventTime());
                    Date currentDate = new Date();
                    if (givenDate.before(currentDate)) {
                        binding.buyNowBtn.setVisibility(View.GONE);
                        binding.layoutButtons.setVisibility(View.GONE);
                        binding.layoutExpired.setVisibility(View.VISIBLE);
                        binding.layoutTimer.setVisibility(View.GONE);
                    } else if (givenDate.after(currentDate)) {
                        binding.buyNowBtn.setVisibility(View.VISIBLE);
                        binding.layoutExpired.setVisibility(View.GONE);
                        binding.layoutTimer.setVisibility(View.VISIBLE);

                    } else {
                        binding.layoutTimer.setVisibility(View.GONE);
                        binding.buyNowBtn.setVisibility(View.VISIBLE);
                        binding.layoutExpired.setVisibility(View.GONE);
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                binding.buyNowBtn.setVisibility(View.GONE);
                binding.eventPackage.setVisibility(View.GONE);
            }

            binding.eventStartTime.setText(Utils.convertMainTimeFormat(eventModel.getReservationTime()));
            binding.eventEndTime.setText(Utils.convertMainTimeFormat(eventModel.getEventTime()));
            binding.eventDate.setText(Utils.convertMainDateFormat(eventModel.getEventTime()));

            if (eventModel.getMyInviteStatus() != null) {
                if (eventModel.getMyInviteStatus().equals("in")) {
                    binding.letsGoBtn.setVisibility(View.GONE);
                    binding.imOutBtn.setVisibility(View.GONE);
                    binding.cancelBtn.setVisibility(View.VISIBLE);

                } else if (eventModel.getMyInviteStatus().equals("pending")) {
                    binding.letsGoBtn.setVisibility(View.VISIBLE);
                    binding.imOutBtn.setVisibility(View.VISIBLE);
                    binding.cancelBtn.setVisibility(View.GONE);

                } else {
                    binding.imOutBtn.setVisibility(View.GONE);
                    binding.cancelBtn.setVisibility(View.GONE);
                    binding.letsGoBtn.setVisibility(View.VISIBLE);
                }
            }

            if (eventModel.getEvent_status().equals("completed")) {
                binding.txtOutingStatus.setText(getValue("completed"));
                binding.txtOutingStatus.setTextColor(getColor(R.color.green));
                binding.txtOutingStatus.setVisibility(View.VISIBLE);
                binding.layoutButtons.setVisibility(View.GONE);
                binding.buyNowBtn.setVisibility(View.GONE);
                binding.layoutExpired.setVisibility(View.GONE);
                binding.layoutTimer.setVisibility(View.GONE);

            } else if (eventModel.getEvent_status().equals("cancelled")) {
                binding.txtOutingStatus.setText(getValue("cancelled"));
                binding.txtOutingStatus.setTextColor(getColor(R.color.redColor));
                binding.txtOutingStatus.setVisibility(View.VISIBLE);
                binding.layoutButtons.setVisibility(View.GONE);
                binding.buyNowBtn.setVisibility(View.GONE);
                binding.layoutExpired.setVisibility(View.GONE);
                binding.layoutTimer.setVisibility(View.GONE);

            } else {
//                binding.layoutMessage.setOnClickListener(view -> {
//                    if (eventDetailModel != null) {
//                        ChatModel chatModel = new ChatModel(eventDetailModel);
//                        Intent intent = new Intent(activity, ChatMessageActivity.class);
//                        intent.putExtra("chatModel", new Gson().toJson(chatModel));
//                        startActivity(intent);
//                    }
//                });
//
//                binding.imgSend.setOnClickListener(view -> {
//                    if (eventDetailModel != null) {
//                        ChatModel chatModel = new ChatModel(eventDetailModel);
//                        Intent intent = new Intent(activity, ChatMessageActivity.class);
//                        intent.putExtra("chatModel", new Gson().toJson(chatModel));
//                        startActivity(intent);
//                    }
//                });
//
//                binding.eventViewAllHL.setOnClickListener(view -> {
//                    if (eventDetailModel != null) {
//                        ChatModel chatModel = new ChatModel(eventDetailModel);
//                        Intent intent = new Intent(activity, ChatMessageActivity.class);
//                        intent.putExtra("chatModel", new Gson().toJson(chatModel));
//                        startActivity(intent);
//                    }
//                });
            }

        }
    }

    private void setInvitedPeopleDetails(List<EventInviteGuestModel> invitedGuests) {
        if (invitedGuests.isEmpty()) {
            binding.eventInvitedPepole.setVisibility(View.GONE);
        } else {
            List<String> id = new ArrayList<>();
            for (EventInviteGuestModel adminId : invitedGuests) {
                id.add(adminId.getUserId());
            }

            List<ContactListModel> shareUser = eventDetailModel.getUsers().stream().filter(p -> id.contains(p.getId())).collect(Collectors.toList());

            binding.eventInvitedPepole.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
            binding.eventInvitedPepole.setAdapter(invitePepoleAdapter);
            invitePepoleAdapter.updateData(shareUser.size() > 4 ? shareUser.subList(0, 4) : shareUser);


            List<EventInviteGuestModel> invitedUsers = invitedGuests;
            for (EventInviteGuestModel user : invitedUsers) {
                String userId = user.getUserId();
                userIdsList.add(userId);
            }
        }
    }

    private void setAdminDetails(List<String> admins) {
        if (admins.isEmpty()) {
            binding.eventsAdminLayout.setVisibility(View.GONE);
        } else {
            if (eventDetailModel != null && eventDetailModel.getUsers() != null) {

                List<ContactListModel> adminList = eventDetailModel.getUsers().stream().filter(p -> admins.contains(p.getId())).collect(Collectors.toList());
                if (adminList != null && !adminList.isEmpty()) {
                    adminAdapter.updateData(adminList);
                    binding.eventsAdminLayout.setVisibility(View.VISIBLE);
                } else {
                    binding.eventsAdminLayout.setVisibility(View.GONE);
                }
            } else {


            }
        }
    }

    private void setInGuestAdapter(List<EventInviteGuestModel> inGuest) {
        binding.invitedGuestRecycler.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        binding.invitedGuestRecycler.setAdapter(inGuestAdapter);

        if (inGuest.isEmpty()) {
            binding.invitedGuestRecycler.setVisibility(View.GONE);
        } else {
            List<String> id = new ArrayList<>();
            for (EventInviteGuestModel model : inGuest) {
                id.add(model.getUserId());
            }

            List<ContactListModel> inList = eventDetailModel.getUsers().stream().filter(p -> id.contains(p.getId())).collect(Collectors.toList());
            inGuestAdapter.updateData(inList.size() > 4 ? inList.subList(0, 3) : inList);

            List<String> invitedUserNames = inList.stream().limit(3).map(model -> model.getFirstName()).collect(Collectors.toList());
            String name = invitedUserNames.toString().substring(1, invitedUserNames.toString().length() - 1);

            if (inList.size() == 1) {
                binding.eventInPepoleCount.setText(name);
            } else if (inList.size() > 1) {
//                binding.eventInPepoleCount.setText(invitedUserNames.get(0).toString() + " and " + eventModel.getInGuestsCount() + " " + "people are In ");
                binding.eventInPepoleCount.setText(setValue("event_in_people_count",String.valueOf(invitedUserNames.get(0)),String.valueOf(eventModel.getInGuestsCount())));
            } else {
                binding.eventInUserLayout.setVisibility(View.GONE);
            }

            binding.eventInUserLayout.setOnClickListener(view -> {
                Utils.preventDoubleClick( view );
                InviteGuestListBottomSheet inviteGuestListBottomSheet = new InviteGuestListBottomSheet();
                inviteGuestListBottomSheet.eventId = eventId;
                inviteGuestListBottomSheet.type = "IN";
                inviteGuestListBottomSheet.show(getSupportFragmentManager(), "");
            });

        }
    }


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestEventDetail(String eventId) {
        showProgress();
        DataService.shared(activity).requestEventDetail(eventId, new RestCallback<ContainerModel<EventDetailModel>>(this) {
            @Override
            public void result(ContainerModel<EventDetailModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (model.getData() != null) {
                    eventDetailModel = model.getData();
                    binding.layoutButtons.setVisibility(View.VISIBLE);

                    if (model.getData().getEventModel() != null) {
                        binding.linear.setVisibility(View.VISIBLE);
                        eventModel = model.getData().getEventModel();
                        setEventDetails(model.getData().getEventModel());
                    }
                }
            }
        });
    }

    private void requestEventInviteStatus(String eventId, String inviteStatus) {
        showProgress();
        DataService.shared(activity).requestEventInviteStatus(eventId, inviteStatus, new RestCallback<ContainerModel<EventDetailModel>>(this) {
            @Override
            public void result(ContainerModel<EventDetailModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (inviteStatus.equals("in")) {
                    binding.letsGoBtn.setVisibility(View.GONE);
                    binding.imOutBtn.setVisibility(View.GONE);
                    binding.cancelBtn.setVisibility(View.VISIBLE);
                } else if (inviteStatus.equals("pending")) {
                    binding.letsGoBtn.setVisibility(View.VISIBLE);
                    binding.imOutBtn.setVisibility(View.VISIBLE);
                    binding.cancelBtn.setVisibility(View.GONE);
                } else {
                    binding.imOutBtn.setVisibility(View.GONE);
                    binding.cancelBtn.setVisibility(View.GONE);
                    binding.letsGoBtn.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    public class InvitePeopleAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = UiUtils.getViewBy(parent, R.layout.image_list);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) view.getLayoutParams();
            layoutParams.rightMargin = view.getContext().getResources().getDimensionPixelSize(com.intuit.sdp.R.dimen._minus8sdp);
            if (viewType == 0) {
                layoutParams.width = view.getContext().getResources().getDimensionPixelSize(com.intuit.sdp.R.dimen._48sdp);
            }
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            ContactListModel userModel = (ContactListModel) getItem(position);
            if (userModel != null) {
                if (position < 5) {
                    Graphics.loadImageWithFirstLetter(userModel.getImage(), viewHolder.vBinding.civPlayers, userModel.getFirstName());
                }
                viewHolder.itemView.setOnClickListener(view -> {
                    Utils.preventDoubleClick( view );
                    InviteGuestListBottomSheet inviteGuestListBottomSheet = new InviteGuestListBottomSheet();
                    inviteGuestListBottomSheet.eventId = eventId;
                    inviteGuestListBottomSheet.show(getSupportFragmentManager(), "");
                });
            }

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
                super(itemView);
                vBinding = ImageListBinding.bind(itemView);
            }
        }

    }

    public class EventAdminAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_select_friends));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            ContactListModel model = (ContactListModel) getItem(position);

            if (model != null) {
                viewHolder.binding.tvName.setText(model.getFirstName());
                Graphics.loadImageWithFirstLetter(model.getImage(), viewHolder.binding.addImage, model.getFirstName());

                viewHolder.itemView.setOnClickListener(v -> {
                    startActivity(new Intent(activity, OtherUserProfileActivity.class).putExtra("friendId", model.getId()));
                });
            }

        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private ItemSelectFriendsBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemSelectFriendsBinding.bind(itemView);
            }
        }
    }

    public class InGuestAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = UiUtils.getViewBy(parent, R.layout.image_list);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) view.getLayoutParams();
            layoutParams.rightMargin = view.getContext().getResources().getDimensionPixelSize(com.intuit.sdp.R.dimen._minus8sdp);
            if (viewType == 0) {
                layoutParams.width = view.getContext().getResources().getDimensionPixelSize(com.intuit.sdp.R.dimen._48sdp);
            }
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            ContactListModel userModel = (ContactListModel) getItem(position);
            if (position < 3) {
                Graphics.loadImageWithFirstLetter(userModel.getImage(), viewHolder.binding.civPlayers, userModel.getFirstName());
            }
            viewHolder.itemView.setOnClickListener(view -> {
                Utils.preventDoubleClick( view );
                InviteGuestListBottomSheet inviteGuestListBottomSheet = new InviteGuestListBottomSheet();
                inviteGuestListBottomSheet.eventId = eventId;
                inviteGuestListBottomSheet.type = "IN";
                inviteGuestListBottomSheet.show(getSupportFragmentManager(), "");
            });
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

            private ImageListBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ImageListBinding.bind(itemView);
            }
        }
    }

    public class HighLightsAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_highlight));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            ChatMessageModel userModel = (ChatMessageModel) getItem(position);
            if (userModel != null) {
                Graphics.loadImageWithFirstLetter(userModel.getAuthorImage(), viewHolder.vBinding.image, userModel.getAuthorName());
                viewHolder.vBinding.tvTime.setText(Utils.convertToMinutesFormat(userModel.getDate()));
                Utils.applyFormatting(activity, viewHolder.vBinding.tvTitle, userModel.getAuthorName(), userModel.getMsg());
            }
        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            private final ItemHighlightBinding vBinding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                vBinding = ItemHighlightBinding.bind(itemView);
            }
        }

    }


    // endregion
    // --------------------------------------

}
