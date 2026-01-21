package com.whosin.app.ui.activites.home.contact;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.whosin.app.databinding.ActivityContactChatRepliesBinding;
import com.whosin.app.databinding.ContactRepliesItemBinding;
import com.whosin.app.databinding.ItemChatMsgBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.ContactChatRepliesModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.comman.BaseActivity;
import com.whosin.app.ui.activites.home.Chat.ChatMessageActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class ContactChatRepliesActivity extends BaseActivity {

    private ActivityContactChatRepliesBinding binding;
    private final ContactRepliesAdapter<ContactChatRepliesModel> adapter = new ContactRepliesAdapter<>();
    private UserDetailModel userDetailModel;
    private List<ContactChatRepliesModel> tmpList = new ArrayList<>();


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------
    @Override
    protected void initUi() {


        applyTranslations();

        String model = getIntent().getStringExtra( "model" );
        if (!TextUtils.isEmpty( model )) {
            userDetailModel = new Gson().fromJson( model, UserDetailModel.class );
        }

        binding.tvName.setText( SessionManager.shared.getUser().getSubject() );

        binding.sendBtn.setEnabled( false );


        if (userDetailModel != null) {
            Graphics.loadImageWithFirstLetter( userDetailModel.getImage(), binding.btnCamera, userDetailModel.getFirstName() );
            binding.contactReplyRecycler.setLayoutManager( new LinearLayoutManager( activity, LinearLayoutManager.VERTICAL, false ) );
            binding.contactReplyRecycler.setAdapter( adapter );
            tmpList.addAll( userDetailModel.getReplies() );
            if (!tmpList.isEmpty()) {
                adapter.updateData( tmpList );
            }
        }

        binding.editTextMessage.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    binding.sendBtn.setEnabled( false );
                } else {
                    binding.sendBtn.setVisibility( View.VISIBLE );
                    binding.sendBtn.setEnabled( true );
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        } );
        Graphics.applyBlurEffect( this, binding.blurView );


    }

    @Override
    protected void setListeners() {

        binding.ivClose.setOnClickListener( v -> {
            onBackPressed();
        } );

        binding.tvName.setText( userDetailModel.getSubject() );


        binding.sendBtn.setOnClickListener( v -> {
            requestContactQueryReply();
        } );

    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityContactChatRepliesBinding.inflate( getLayoutInflater() );
        return binding.getRoot();
    }



    @Override
    protected Map<View, String> getTranslationMap() {
        Map<View, String> map = new HashMap<>();
        map.put(binding.tvName, "contact_us");
        map.put(binding.editTextMessage, "message_here");
        return map;
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void requestContactQueryReply() {
        ContactChatRepliesModel model1 = new ContactChatRepliesModel();
        model1.setReply( binding.editTextMessage.getText().toString() );
        model1.setReplyBy( "user" );
        model1.setCreatedAt( getCurrentDateTime() );
        tmpList.add( model1 );
        adapter.updateData( tmpList );
        if (binding.contactReplyRecycler.getLayoutManager() != null) {
            binding.contactReplyRecycler.smoothScrollToPosition( tmpList.size() - 1 );
        }
        DataService.shared( activity ).requestContactQueryReply( binding.editTextMessage.getText().toString(), userDetailModel.getId(), new RestCallback<ContainerModel<ContactChatRepliesModel>>(this) {
            @Override
            public void result(ContainerModel<ContactChatRepliesModel> model, String error) {

                if (!Utils.isNullOrEmpty( error ) || model == null) {
                    Toast.makeText( activity, error, Toast.LENGTH_SHORT ).show();
                    return;
                }

                binding.editTextMessage.setText( "" );
            }
        } );
    }

    public static String getCurrentDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US );
        dateFormat.setTimeZone( TimeZone.getTimeZone( "UTC" ) ); // Set the time zone to UTC

        return dateFormat.format( new Date() );
    }

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------


    public class ContactRepliesAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            switch (viewType) {
                case 0:
                    return new ViewHolder( UiUtils.getViewBy( parent, R.layout.contact_replies_item ) );
                case 1:
                    return new AdminViewHolder( UiUtils.getViewBy( parent, R.layout.item_chat_msg ) );
                default:
                    return null;
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ContactChatRepliesModel model = (ContactChatRepliesModel) getItem( position );
            if (model.getReplyBy().equals( "admin" )) {
                AdminViewHolder adminViewHolder = (AdminViewHolder) holder;
                adminViewHolder.mBinding.tvChat.setText( model.getReply() );
                adminViewHolder.mBinding.txtSender.setText( "Whos 'In Admin" );
                adminViewHolder.mBinding.tvTime.setText( Utils.convertTimestamp( model.getCreatedAt(), "hh:mm a" ) );
            } else {
                ViewHolder viewHolder = (ViewHolder) holder;
                viewHolder.binding.tvMsg.setText( model.getReply() );
                viewHolder.binding.tvChatTime.setText( Utils.convertTimestamp( model.getCreatedAt(), "hh:mm a" ) );
            }



            /*if (model.getReplyBy().equals( "admin" )) {
                viewHolder.binding.tvChatUserName.setText( "Whos 'In Admin" );
                Glide.with( activity ).load( R.drawable.app_icon ).into( viewHolder.binding.ivUserProfile );
            } else {
                viewHolder.binding.tvChatUserName.setText( SessionManager.shared.getUser().getFullName() );
                Graphics.loadImageWithFirstLetter( SessionManager.shared.getUser().getImage(), viewHolder.binding.ivUserProfile, SessionManager.shared.getUser().getName() );
            }*/


        }

        @Override
        public int getItemViewType(int position) {
            ContactChatRepliesModel model = (ContactChatRepliesModel) getItem( position );
            if (model != null && model.getReplyBy().equals( "admin" )) {
                return 1; // Admin view type
            } else {
                return 0; // Other view type
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final ContactRepliesItemBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super( itemView );
                binding = (ContactRepliesItemBinding.bind( itemView ));
            }
        }

        public class AdminViewHolder extends RecyclerView.ViewHolder {

            private final ItemChatMsgBinding mBinding;

            public AdminViewHolder(@NonNull View itemView) {
                super( itemView );
                mBinding = ItemChatMsgBinding.bind( itemView );
            }
        }
    }


    // endregion
    // --------------------------------------
}