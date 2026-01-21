package com.whosin.app.ui.activites.home.Chat;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.ActivityAddChatFriendBinding;
import com.whosin.app.databinding.ItemWhosinChatContactBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.Repository.ContactRepository;
import com.whosin.app.service.manager.ContactManager;
import com.whosin.app.service.models.ChatModel;
import com.whosin.app.service.models.ContactListModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.comman.BaseActivity;

import java.util.List;

public class AddChatFriendActivity extends BaseActivity {

    private ActivityAddChatFriendBinding binding;
    private FriendChatListAdapter<ContactListModel> friendChatListAdapter = new FriendChatListAdapter<>();
    List<ContactListModel> allContacts;

    private String id = "";




    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    protected void initUi() {
        requestContact();
        binding.chatContactRecyle.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        binding.chatContactRecyle.setAdapter(friendChatListAdapter);

    }

    @Override
    protected void setListeners() {

        binding.ivBack.setOnClickListener(view -> onBackPressed());
    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityAddChatFriendBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------


    private void requestContact() {
//        ContactManager.shared.syncContacts(data -> {
//            updateList();
//        });
        updateList();
    }

    private void updateList() {
        allContacts = ContactRepository.shared(activity).getSyncedContacts();
        if (!allContacts.isEmpty()) {
            hideProgress();
        }
        friendChatListAdapter.updateData(allContacts);

    }


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestCreateFriendChat(ContactListModel contactListModel){
        DataService.shared( activity ).requestCreateChat(contactListModel.getId(),new RestCallback<ContainerModel<ChatModel>>(this) {
            @Override
            public void result(ContainerModel<ChatModel> model, String error) {
                if (!Utils.isNullOrEmpty( error ) || model == null) {
                    Toast.makeText( activity, error, Toast.LENGTH_SHORT ).show();
                    return;
                }
                Toast.makeText( activity, model.message, Toast.LENGTH_SHORT ).show();
            }
        } );
    }


    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    public class FriendChatListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new ContactHolder(UiUtils.getViewBy(parent, R.layout.item_whosin_chat_contact));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ContactListModel listModel = (ContactListModel) getItem(position);
            ContactHolder viewHolder = (ContactHolder) holder;
            Graphics.loadImageWithFirstLetter(listModel.getImage(), viewHolder.vBinding.ivUserProfile, listModel.getFullName());
            viewHolder.vBinding.tvChatUserName.setText(listModel.getFullName());

//            id = listModel.getId();

            viewHolder.itemView.setOnClickListener( view -> {requestCreateFriendChat(listModel);} );

        }
        public class ContactHolder extends RecyclerView.ViewHolder {
            public ItemWhosinChatContactBinding vBinding;
            public ContactHolder(@NonNull View itemView) {
                super(itemView);
                vBinding = ItemWhosinChatContactBinding.bind(itemView);
            }
        }

    }


    // endregion
    // --------------------------------------


}