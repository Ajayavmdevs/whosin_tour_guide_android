package com.whosin.business.ui.activites.venue;

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
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.whosin.business.R;
import com.whosin.business.comman.AppExecutors;
import com.whosin.business.comman.Graphics;
import com.whosin.business.comman.Preferences;
import com.whosin.business.comman.Utils;
import com.whosin.business.comman.interfaces.CommanCallback;
import com.whosin.business.databinding.ActivityVenueShareBinding;
import com.whosin.business.service.DataService;
import com.whosin.business.service.manager.ChatManager;
import com.whosin.business.service.manager.SessionManager;
import com.whosin.business.service.models.ChatMessageModel;
import com.whosin.business.service.models.ChatModel;
import com.whosin.business.service.models.ContactListModel;
import com.whosin.business.service.models.ContainerListModel;
import com.whosin.business.service.models.ContainerModel;
import com.whosin.business.service.models.StoryObjectModel;
import com.whosin.business.service.models.UserDetailModel;
import com.whosin.business.service.models.VenueObjectModel;
import com.whosin.business.service.models.rayna.RaynaTicketDetailModel;
import com.whosin.business.service.rest.RestCallback;
import com.whosin.business.ui.activites.comman.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class VenueShareActivity extends BaseActivity {

    private ActivityVenueShareBinding binding;
    private String searchQuery = "";
    private String eventId = "";
    private Runnable runnable = () -> searchData();
    private String type = "";
    private Handler handler = new Handler();
    private VenueObjectModel venueObjectModel = new VenueObjectModel();

    private UserDetailModel userDetailModel = new UserDetailModel();

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

        String raynaTicket = getIntent().getStringExtra( "rayna" );
        if (!TextUtils.isEmpty( raynaTicket )) {
            raynaTicketDetailModel = new Gson().fromJson( raynaTicket, RaynaTicketDetailModel.class );
        }

        String user = getIntent().getStringExtra( "userModel" );
        if (!TextUtils.isEmpty( user )) {
            userDetailModel = new Gson().fromJson( user, UserDetailModel.class );
        }

        requestLinkCreate();

        Utils.setSelectedStatus( Utils.setType.NONE );
        binding.contactRecycler.setLayoutManager( new LinearLayoutManager( activity, LinearLayoutManager.VERTICAL, false ) );


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
            sendData();
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

    private String getMessageText() {
        JsonObject jsonObject = new JsonObject();
        switch (type) {
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
            case "ticket":
                if (raynaTicketDetailModel != null) {
                    jsonObject.addProperty("_id", raynaTicketDetailModel.getId());
                    jsonObject.addProperty("title", raynaTicketDetailModel.getTitle());
                    jsonObject.addProperty("description", raynaTicketDetailModel.getDescription());
                    jsonObject.addProperty("city", raynaTicketDetailModel.getCity());
                    Object startingAmount = raynaTicketDetailModel.getStartingAmount();
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
            }
        } );
    }





    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------


}