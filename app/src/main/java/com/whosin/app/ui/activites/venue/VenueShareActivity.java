package com.whosin.app.ui.activites.venue;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.whosin.app.R;
import com.whosin.app.comman.AppExecutors;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Preferences;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.comman.ui.roundcornerlayout.CornerType;
import com.whosin.app.databinding.ActivityVenueShareBinding;
import com.whosin.app.databinding.InviteContactFreindItemBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.ChatManager;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.ChatMessageModel;
import com.whosin.app.service.models.ChatModel;
import com.whosin.app.service.models.ContactListModel;
import com.whosin.app.service.models.ContainerListModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.OffersModel;
import com.whosin.app.service.models.PromoterEventModel;
import com.whosin.app.service.models.StoryObjectModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.models.VenueObjectModel;
import com.whosin.app.service.models.YachtClubModel;
import com.whosin.app.service.models.rayna.RaynaTicketDetailModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.comman.BaseActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class VenueShareActivity extends BaseActivity {

    private ActivityVenueShareBinding binding;
    private ContactsAdapter<ContactListModel> followersListAdaptere;
    private String searchQuery = "";
    private String eventId = "";
    private Runnable runnable = () -> searchData();
    private String type = "";
    private Handler handler = new Handler();
    private VenueObjectModel venueObjectModel = new VenueObjectModel();
    private OffersModel offersModel = new OffersModel();

    private UserDetailModel userDetailModel = new UserDetailModel();

    private YachtClubModel yachtClubModel = new YachtClubModel();
    private PromoterEventModel promoterEventModel = new PromoterEventModel();
    List<ContactListModel> followerList = new ArrayList<>();
    private CommanCallback<List<ContactListModel>> listener;
    private List<ContactListModel> selectedContacts = new ArrayList<>();
    private RaynaTicketDetailModel raynaTicketDetailModel ;


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    protected void initUi() {

        binding.tvBucketTitle.setText(getValue("share"));
        binding.btnSend.setText(getValue("share"));
        binding.copyBtn.setText(getValue("copy"));

        type = getIntent().getStringExtra( "type" );

        if (!TextUtils.isEmpty(type)){
            if (type.equalsIgnoreCase("plusOneAdd") || type.equalsIgnoreCase("plusOneGuestAdd")  ){
                binding.tvBucketTitle.setText(getValue("invite"));
                binding.btnSend.setText(getValue("invite"));
                if (!TextUtils.isEmpty(getIntent().getStringExtra( "eventId"))) eventId = getIntent().getStringExtra( "eventId");
            }
        }


        String venue = getIntent().getStringExtra( "venue" );
        if (!TextUtils.isEmpty( venue )) {
            venueObjectModel = new Gson().fromJson( venue, VenueObjectModel.class );
        }

        String raynaTicket = getIntent().getStringExtra( "rayna" );
        if (!TextUtils.isEmpty( raynaTicket )) {
            raynaTicketDetailModel = new Gson().fromJson( raynaTicket, RaynaTicketDetailModel.class );
        }

        String offer = getIntent().getStringExtra( "offer" );
        if (!TextUtils.isEmpty( offer )) {
            offersModel = new Gson().fromJson( offer, OffersModel.class );
        }
        String user = getIntent().getStringExtra( "userModel" );
        if (!TextUtils.isEmpty( user )) {
            userDetailModel = new Gson().fromJson( user, UserDetailModel.class );
        }
        String yachtClub = getIntent().getStringExtra( "yachtClub" );
        if (!TextUtils.isEmpty( yachtClub )) {
            yachtClubModel = new Gson().fromJson( yachtClub, YachtClubModel.class );
        }

        String promoterEvent = getIntent().getStringExtra( "promoterEvent" );
        if (!TextUtils.isEmpty( promoterEvent )) {
            promoterEventModel = new Gson().fromJson( promoterEvent, PromoterEventModel.class );
        }

        requestLinkCreate();

        followersListAdaptere = new ContactsAdapter();
        Utils.setSelectedStatus( Utils.setType.NONE );
        binding.contactRecycler.setLayoutManager( new LinearLayoutManager( activity, LinearLayoutManager.VERTICAL, false ) );

        binding.contactRecycler.setAdapter( followersListAdaptere );

        followerList = SessionManager.shared.getFollowingData();
        if (!followerList.isEmpty()) {
            searchData();
        } else {
            Graphics.showProgress( activity );
            DataService.shared(this).requestFollowingList(SessionManager.shared.getUser().getId(), new RestCallback<ContainerListModel<ContactListModel>>(this) {
                @Override
                public void result(ContainerListModel<ContactListModel> model, String error) {
                    Graphics.hideProgress( activity );
                    if (!Utils.isNullOrEmpty(error) || model == null) {
                        Toast.makeText(VenueShareActivity.this, R.string.service_message_something_wrong, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (model.data != null && !model.data.isEmpty()) {
                        followerList = model.data;
                        SessionManager.shared.saveFollowingData(model.data);
                        AppExecutors.get().mainThread().execute(() -> searchData());
                    }
                }
            });
        }
        overridePendingTransition( R.anim.slide_up, R.anim.slide_down );
    }

    @Override
    protected void setListeners() {
        binding.btnShare.setOnClickListener( view -> {
            Intent intent = new Intent( Intent.ACTION_SEND );
            intent.setType( "text/plain" );
            intent.putExtra( Intent.EXTRA_TEXT, SessionManager.shared.getVenueShareJson() );
            activity.startActivity( Intent.createChooser( intent, "Share" ) );
        } );

        binding.btnSend.setOnClickListener( view -> {
            Utils.preventDoubleClick( view );
            if (type.equalsIgnoreCase("plusOneAdd")){
                requestAddMemberinPlusOne();
            } else {
                sendData();
            }

        } );

        binding.ivClose.setOnClickListener( view -> onBackPressed() );

        binding.edtSearch.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                searchQuery = editable.toString();
                handler.removeCallbacks( runnable );
                handler.postDelayed( runnable, 1000 );
            }
        } );


        binding.btnCopy.setOnClickListener(view -> {
            Utils.preventDoubleClick(view);
            ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboard != null) {
                String tmpString = Preferences.shared.getString("shareList");
                ClipData clip = ClipData.newPlainText("label", tmpString);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(activity, getValue("link_copied"), Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityVenueShareBinding.inflate( getLayoutInflater() );
        return binding.getRoot();
    }

    private void searchData() {
        if (SessionManager.shared.getUser() != null && !followerList.isEmpty()) {
            followerList.removeIf(p -> p.getId().equals(SessionManager.shared.getUser().getId()));
        }
        if (type.equalsIgnoreCase("plusOneAdd") || type.equalsIgnoreCase("plusOneGuestAdd")) {
            followerList.removeIf(p -> p.isPromoter() || p.isRingMember());
        }
        if (type.equalsIgnoreCase("promoterEvent")){
            followerList.removeIf(p -> !p.isRingMember());
        }
        if (!TextUtils.isEmpty( searchQuery ) && followerList != null) {
            List<ContactListModel> searchList = new ArrayList<>();
            for (ContactListModel model : followerList) {
                if (model.getFullName().toLowerCase().contains( searchQuery.toLowerCase() )) {
                    searchList.add( model );
                }
            }
            followersListAdaptere.updateData( searchList );
        } else {
            followersListAdaptere.updateData( followerList );
        }
    }

    private JsonObject getVenueJson(VenueObjectModel venueModel) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty( "_id", venueModel.getId() );
        jsonObject.addProperty( "name", venueModel.getName() );
        jsonObject.addProperty( "logo", venueModel.getLogo() );
        jsonObject.addProperty( "cover", venueModel.getCover() );
        jsonObject.addProperty( "address", venueModel.getAddress() );
        return jsonObject;
    }

    private JsonObject getEventJson(PromoterEventModel promoterEventModel){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("eventId",promoterEventModel.getId());
        jsonObject.addProperty("type",promoterEventModel.getType());
        jsonObject.addProperty("venueType",promoterEventModel.getVenueType());
        jsonObject.addProperty("date",promoterEventModel.getDate());
        jsonObject.addProperty("startTime",promoterEventModel.getStartTime());
        jsonObject.addProperty("endTime",promoterEventModel.getEndTime());
        jsonObject.addProperty("status",promoterEventModel.getStatus());
        if (promoterEventModel.getVenueType().equals("venue")){
            JsonObject venueJson = new JsonObject();
            venueJson.addProperty("name", promoterEventModel.getVenue() != null ? promoterEventModel.getVenue().getName() : "");
            venueJson.addProperty("address", promoterEventModel.getVenue() != null ? promoterEventModel.getVenue().getAddress() : "");
            venueJson.addProperty("image", promoterEventModel.getVenue() != null ? promoterEventModel.getVenue().getCover() : "");
            venueJson.addProperty("image", promoterEventModel.getVenue() != null ? promoterEventModel.getVenue().getCover() : "");
            venueJson.addProperty("logo", promoterEventModel.getVenue() != null ? promoterEventModel.getVenue().getLogo() : "");
            jsonObject.add("customVenue",venueJson);
        }else {
            JsonObject customVenueJson = new JsonObject();
            customVenueJson.addProperty("name", promoterEventModel.getCustomVenue() != null ? promoterEventModel.getCustomVenue().getName() : "");
            customVenueJson.addProperty("address", promoterEventModel.getCustomVenue() != null ? promoterEventModel.getCustomVenue().getAddress() : "");
            customVenueJson.addProperty("image", promoterEventModel.getCustomVenue() != null ? promoterEventModel.getCustomVenue().getImage() : "");
            customVenueJson.addProperty("description", promoterEventModel.getCustomVenue() != null ? promoterEventModel.getCustomVenue().getDescription() : "");
            customVenueJson.addProperty("logo",  promoterEventModel.getCustomVenue() != null ? promoterEventModel.getCustomVenue().getImage() : "");

            jsonObject.add("customVenue",customVenueJson);
        }

        return jsonObject;

    }

    private String getMessageText() {
        JsonObject jsonObject = new JsonObject();
        switch (type) {
            case "venue":
                jsonObject = getVenueJson(venueObjectModel);
                break;
            case "promoterEvent":
                jsonObject = getEventJson(promoterEventModel);
                 break;
            case "story":
                if (!venueObjectModel.getStories().isEmpty()) {
                    jsonObject = getVenueJson(venueObjectModel);
                    StoryObjectModel storyObjectModel = venueObjectModel.getStories().get(0);
                    JsonObject storyObject = new JsonObject();
                    storyObject.addProperty("_id", storyObjectModel.getId());
                    storyObject.addProperty("buttonText", storyObjectModel.getButtonText());
                    storyObject.addProperty("createdAt", storyObjectModel.getCreatedAt());
                    storyObject.addProperty("venueId", venueObjectModel.getId());
                    storyObject.addProperty("ticketId", storyObjectModel.getTicketId());
                    storyObject.addProperty("userId", "");
                    storyObject.addProperty("offerId", storyObjectModel.getOfferId());
                    storyObject.addProperty("mediaType", storyObjectModel.getMediaType());
                    storyObject.addProperty("thumbnail", String.valueOf( storyObjectModel.getThumbnail()));
                    storyObject.addProperty("contentType", storyObjectModel.getContentType());
                    storyObject.addProperty("duration", String.valueOf(storyObjectModel.getDuration()));
                    storyObject.addProperty("mediaUrl", storyObjectModel.getMediaUrl());
                    JsonArray jsonArray = new JsonArray();
                    jsonArray.add(storyObject);
                    jsonObject.add("stories", jsonArray);
                }
                break;
            case "user":
                jsonObject.addProperty("_id", userDetailModel.getId());
                jsonObject.addProperty("first_name", userDetailModel.getFirstName());
                jsonObject.addProperty("last_name", userDetailModel.getLastName());
                jsonObject.addProperty("image", userDetailModel.getImage());
                jsonObject.addProperty("description", userDetailModel.getBio());
                jsonObject.addProperty("follow", userDetailModel.getFollow());
                break;
            case "offer":
                jsonObject.addProperty("_id", offersModel.getId());
                jsonObject.addProperty("title", offersModel.getTitle());
                jsonObject.addProperty("image", offersModel.getImage());
                jsonObject.addProperty("description", offersModel.getDescription());
                jsonObject.addProperty("days", offersModel.days);
                jsonObject.addProperty("startTime", offersModel.getStartTime());
                jsonObject.addProperty("endTime", offersModel.getEndTime());
                if (offersModel.getVenue() != null) {
                    JsonObject venueJson = getVenueJson(offersModel.getVenue());
                    jsonObject.add("venue", venueJson);
                }
                break;
            case "yachtClub":
                if(yachtClubModel != null){
                    jsonObject.addProperty("_id", yachtClubModel.getId());
                    jsonObject.addProperty("name", yachtClubModel.getName());
                    jsonObject.addProperty("cover", yachtClubModel.getCover());
                    jsonObject.addProperty("logo", yachtClubModel.getLogo());
                    jsonObject.addProperty("address", yachtClubModel.getAddress());
                }

                break;
            case "ticket":
                if (raynaTicketDetailModel != null) {
                    jsonObject.addProperty("_id", raynaTicketDetailModel.getId());
                    jsonObject.addProperty("title", raynaTicketDetailModel.getTitle());
                    jsonObject.addProperty("description", raynaTicketDetailModel.getDescription());
                    jsonObject.addProperty("city", raynaTicketDetailModel.getCity());
                    Object startingAmount = raynaTicketDetailModel.getStartingAmount();
//                    jsonObject.addProperty("startingAmount", startingAmount != null ? startingAmount.toString() : "");
                    if (startingAmount != null) {
                        try {
                            double amount = Double.parseDouble(startingAmount.toString());
                            jsonObject.addProperty("startingAmount", amount);
                        } catch (NumberFormatException e) {
                            jsonObject.addProperty("startingAmount", 0.0);
                        }
                    } else {
                        jsonObject.addProperty("startingAmount", 0.0);
                    }
                    jsonObject.add("images", new Gson().toJsonTree(raynaTicketDetailModel.getImages()).getAsJsonArray());
                    jsonObject.addProperty("discount",raynaTicketDetailModel.getDiscount());

                }

                break;
        }
        return new Gson().toJson( jsonObject );
    }

    private void sendData() {
        String jsonString = getMessageText();
        if (TextUtils.isEmpty(jsonString)) {
            Toast.makeText( VenueShareActivity.this, "Something went wrong. Please try again", Toast.LENGTH_SHORT ).show();
            return;
        }
//        selectedContacts.forEach( c -> {
//            ChatModel chatModel = new ChatModel( c );
//            ChatMessageModel messageModel = new ChatMessageModel( jsonString, type, chatModel, chatModel.getMembers() );
//            ChatRepository.shared( this ).addMessage( messageModel, data -> ChatManager.shared.sendMessage( messageModel ) );
//        } );

        Graphics.showProgressBarDialog(this, "message_sending",getValue("message_sending"));
        final int delayInMillis = 1000; // 1 second
        final Handler handler1 = new Handler();
        final int[] index = {0};
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                ContactListModel currentItem = selectedContacts.get(index[0]);
                ChatModel chatModel = new ChatModel( currentItem );
                ChatMessageModel messageModel = new ChatMessageModel( jsonString, type, chatModel, chatModel.getMembers() );
                Log.d("Promoter Json", "setListeners: " + new Gson().toJson( messageModel ));
                ChatManager.shared.sendMessage(messageModel);
                index[0]++;
                if (index[0] < selectedContacts.size()) {
                    handler1.postDelayed(this, delayInMillis);
                } else {
                    Graphics.hideDialog(VenueShareActivity.this, "message_sending");
                    onBackPressed();
                }
            }
        };
        handler1.post(runnable);

//        new Handler().postDelayed( () -> onBackPressed(), 2000 );
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
    // region public
    // --------------------------------------

    public void setShareListener(CommanCallback<List<ContactListModel>> listener) {
        this.listener = listener;
    }

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestLinkCreate() {
        if (activity == null) {return;}
        if (type.equalsIgnoreCase("plusOneAdd") || type.equalsIgnoreCase("plusOneGuestAdd")) return;
        JsonObject jsonObject = new JsonObject();
        String formattedOutput = "";
        if (type.equals( "user" )) {

            if (userDetailModel == null) {
                return;
            }
            jsonObject.addProperty( "title", userDetailModel.getFullName() );
            jsonObject.addProperty( "description", userDetailModel.getBio().isEmpty() ? " " : userDetailModel.getBio() );
            jsonObject.addProperty( "image", userDetailModel.getImage().isEmpty() ? "https://ui-avatars.com/api/?name=" + userDetailModel.getFirstName() : userDetailModel.getImage() );
            jsonObject.addProperty( "itemId", userDetailModel.getId() );
            jsonObject.addProperty( "itemType", "user" );
        } else if (type.equals( "offer" )) {

            if (offersModel == null) {
                return;
            }
            jsonObject.addProperty( "title", offersModel.getTitle() );
            jsonObject.addProperty( "description", offersModel.getDescription() );
            jsonObject.addProperty( "image", offersModel.getImage() );
            jsonObject.addProperty( "itemId", offersModel.getId() );
            jsonObject.addProperty( "itemType", "offer" );
        } else if (type.equals("promoterEvent")) {
            if (promoterEventModel == null) return;

            String eventImage = "";
            String eventDescription = "";

            if (promoterEventModel.getVenueType().equals("venue") && promoterEventModel.getVenue() != null && !TextUtils.isEmpty(promoterEventModel.getVenue().getCover())) {
                eventImage = promoterEventModel.getVenue().getCover();
                eventDescription = promoterEventModel.getVenue().getAddress();
                jsonObject.addProperty("title", promoterEventModel.getVenue().getName());

            } else {
                if (promoterEventModel.getCustomVenue() != null && !TextUtils.isEmpty(promoterEventModel.getCustomVenue().getImage())) {
                    eventImage = promoterEventModel.getCustomVenue().getImage();
                    eventDescription = promoterEventModel.getCustomVenue().getDescription();
                    jsonObject.addProperty("title", promoterEventModel.getCustomVenue().getName());
                }
            }

            formattedOutput = formatLists(promoterEventModel.getRequirementsAllowed(), promoterEventModel.getBenefitsIncluded());

            jsonObject.addProperty("description", eventDescription);
            jsonObject.addProperty("image", eventImage);
            jsonObject.addProperty("itemId", promoterEventModel.getId());
            jsonObject.addProperty("itemType", "promoter-event");
        } else if (type.equals( "yachtClub" )) {
            if (yachtClubModel == null) {
                return;
            }
            jsonObject.addProperty( "title", yachtClubModel.getName() );
            jsonObject.addProperty( "description", yachtClubModel.getAddress() );
            jsonObject.addProperty( "image", yachtClubModel.getCover() );
            jsonObject.addProperty( "itemId", yachtClubModel.getId() );
            jsonObject.addProperty( "itemType", "yachtClub" );
        } else if (type.equals( "ticket" )) {
            if (raynaTicketDetailModel == null) {
                return;
            }
            jsonObject.addProperty( "title", raynaTicketDetailModel.getTitle() );
            jsonObject.addProperty( "description", raynaTicketDetailModel.getDescription() );
            jsonObject.addProperty( "image", raynaTicketDetailModel.getImages().get(0) );
            jsonObject.addProperty( "itemId", raynaTicketDetailModel.getId() );
            jsonObject.addProperty( "itemType", "ticket" );
        } else {
            if (venueObjectModel == null) {
                return;
            }
            jsonObject.addProperty( "title", venueObjectModel.getName() );
            if (!venueObjectModel.getAbout().isEmpty()) {
                jsonObject.addProperty( "description", venueObjectModel.getAbout() );
            } else {
                jsonObject.addProperty( "description", venueObjectModel.getAddress() );
            }
            jsonObject.addProperty( "image", venueObjectModel.getCover() );
            jsonObject.addProperty( "itemId", venueObjectModel.getId() );
            jsonObject.addProperty( "itemType", "venue" );
        }
        String finalFormattedOutput = formattedOutput;
        DataService.shared( activity ).requestLinkCreate( jsonObject, new RestCallback<ContainerModel<String>>(this) {

            @Override
            public void result(ContainerModel<String> model, String error) {
                if (!Utils.isNullOrEmpty( error ) || model == null) {
                    Toast.makeText( activity, error, Toast.LENGTH_SHORT ).show();
                    return;
                }

                // 1. Get raw description (HTML or plain text)
                String rawDescription = jsonObject.get("description").getAsString();

                // 2. Convert to plain text (remove all HTML tags)
                Spanned spanned = Html.fromHtml(rawDescription, Html.FROM_HTML_MODE_LEGACY);
                String plainText = spanned.toString().trim();

                // 3. Replace all kinds of whitespace (tabs, newlines, multiple spaces) with single space
                plainText = plainText.replaceAll("\\s+", " ");

                // 4. Limit total length to 150 characters (including spaces, punctuation, etc.)
                String trimmedText;
                if (plainText.length() > 150) {
                    trimmedText = plainText.substring(0, 150).trim() + "...";
                } else {
                    trimmedText = plainText;
                }

                // 5. Create share message
                String shareMsg = "";
                if (type.equals("promoterEvent")) {
                    shareMsg = String.format("%s\n\n%s\n\n%s\n\n%s",
                            jsonObject.get("title").getAsString(),
                            trimmedText,
                            finalFormattedOutput,
                            model.getData());
                } else {
                    shareMsg = String.format("%s\n\n%s\n\n%s",
                            jsonObject.get("title").getAsString(),
                            trimmedText,
                            model.getData());
                }

                // 6. Save and show
                SessionManager.shared.saveShareVenue(shareMsg);
                binding.shareBtnLayout.setVisibility(View.VISIBLE);

//                String shareMsg = "";
//                if (type.equals("promoterEvent")) {
//                    shareMsg = String.format("%s\n\n%s\n\n%s\n\n%s", jsonObject.get("title").getAsString(), jsonObject.get("description").getAsString(), finalFormattedOutput, model.getData());
//                } else {
//                    shareMsg = String.format("%s\n\n%s\n\n%s", jsonObject.get("title").getAsString(), jsonObject.get("description").getAsString(), model.getData());
//                }
//                SessionManager.shared.saveShareVenue( shareMsg );
//                binding.shareBtnLayout.setVisibility( View.VISIBLE );
            }
        } );
    }

    private void requestAddMemberinPlusOne(){
        String id = selectedContacts.get(0).getId();
        Log.d("requestAddMemberinPlusOne", "requestAddMemberinPlusOne: " + id);
        DataService.shared( activity ).requestPromoterPlusOneInviteUser( id, new RestCallback<ContainerModel<PromoterEventModel>>(this) {
            @Override
            public void result(ContainerModel<PromoterEventModel> model, String error) {
                if (!Utils.isNullOrEmpty( error ) || model == null) {
                    Toast.makeText( activity, error, Toast.LENGTH_SHORT ).show();
                    return;
                }

                Toast.makeText(activity, model.message, Toast.LENGTH_SHORT).show();
                finish();

            }
        } );
    }





    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------
    public class ContactsAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder( UiUtils.getViewBy( parent, R.layout.invite_contact_freind_item ) );
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ContactListModel listModel = (ContactListModel) getItem( position );
            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.vBinding.tvUserName.setText( listModel.getFullName() );
            Graphics.loadImageWithFirstLetter( listModel.getImage(), viewHolder.vBinding.ivUserProfile, listModel.getFullName() );

            viewHolder.vBinding.ivCheck.setVisibility( View.VISIBLE );
            viewHolder.vBinding.optionContainer.setVisibility( View.GONE );
            viewHolder.vBinding.tvContactBookName.setVisibility( View.GONE );

            viewHolder.vBinding.ivCheck.setOnCheckedChangeListener( null );
            viewHolder.vBinding.ivCheck.setChecked( selectedContacts.contains( listModel ) );

            viewHolder.vBinding.ivCheck.setOnCheckedChangeListener( (buttonView, isChecked1) -> {
                if (selectedContacts.contains( listModel )) {
                    selectedContacts.remove( listModel );
                } else {
                    if (type.equalsIgnoreCase("plusOneAdd")) selectedContacts.clear();
                    selectedContacts.add( listModel );
                }
                isAnyCheckBoxSelected();
                notifyDataSetChanged();
            } );

            viewHolder.vBinding.getRoot().setOnClickListener( v -> {
                if (selectedContacts.contains( listModel )) {
                    selectedContacts.remove( listModel );
                } else {
                    if (type.equalsIgnoreCase("plusOneAdd")) selectedContacts.clear();
                    selectedContacts.add( listModel );
                }
                isAnyCheckBoxSelected();
                notifyDataSetChanged();
            } );


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
            viewHolder.vBinding.view.setVisibility(isLastCell ? View.GONE : View.VISIBLE);

        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final InviteContactFreindItemBinding vBinding;

            public ViewHolder(@NonNull View itemView) {
                super( itemView );
                vBinding = InviteContactFreindItemBinding.bind( itemView );
            }
        }

        private void isAnyCheckBoxSelected() {
            binding.sendBtnContainer.setVisibility( !selectedContacts.isEmpty() ? View.VISIBLE : View.GONE );
            binding.shareBtnLayout.setVisibility( !selectedContacts.isEmpty() ? View.GONE : View.VISIBLE );
        }


    }


}