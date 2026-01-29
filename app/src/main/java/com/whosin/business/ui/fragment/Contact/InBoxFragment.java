package com.whosin.business.ui.fragment.Contact;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.Gson;
import com.whosin.business.R;
import com.whosin.business.comman.DiffAdapter;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Graphics;
import com.whosin.business.comman.Utils;
import com.whosin.business.comman.interfaces.CommanCallback;
import com.whosin.business.comman.ui.UiUtils;
import com.whosin.business.databinding.ContactChatListItemBinding;
import com.whosin.business.databinding.FragmentInBoxBinding;
import com.whosin.business.service.DataService;
import com.whosin.business.service.models.CommanMsgModel;
import com.whosin.business.service.models.ContactChatRepliesModel;
import com.whosin.business.service.models.ContainerListModel;
import com.whosin.business.service.models.ContainerModel;
import com.whosin.business.service.models.UserDetailModel;
import com.whosin.business.service.rest.RestCallback;
import com.whosin.business.ui.activites.home.contact.ContactChatRepliesActivity;
import com.whosin.business.ui.fragment.comman.BaseFragment;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class InBoxFragment extends BaseFragment {

    private FragmentInBoxBinding binding;
    private ViewPager viewPager;
    private final ContactListAdapter<UserDetailModel> adapter = new ContactListAdapter<>();
    public CommanCallback<Boolean> callback;


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    public void initUi(View view) {
        binding = FragmentInBoxBinding.bind( view );

        binding.emptyPlaceHolderView.setEmptyPlaceTxtTitle(getValue("empty_inbox"));

        viewPager = requireActivity().findViewById( R.id.viewPagerContactUs );

        binding.contactListRecycler.setLayoutManager( new LinearLayoutManager( requireActivity(), LinearLayoutManager.VERTICAL, false ) );
        binding.contactListRecycler.setAdapter( adapter );
        binding.swipeRefreshLayout.setOnRefreshListener(this::requestContactUsQueryList);

    }

    @Override
    public void setListeners() {
        viewPager.addOnPageChangeListener( new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Not needed for visibility detection
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 1) {
                    requestContactUsQueryList();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // Not needed for visibility detection
            }
        } );
    }

    @Override
    public void populateData(boolean getDataFromServer) {

    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_in_box;
    }


    public void onResume() {
        super.onResume();
        requestContactUsQueryList();
    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------
    public void requestContactUsQueryList() {
        DataService.shared( requireActivity() ).requestContactQueryList( new RestCallback<ContainerListModel<UserDetailModel>>(this) {
            @Override
            public void result(ContainerListModel<UserDetailModel> model, String error) {
                binding.swipeRefreshLayout.setRefreshing( false );
                if (!Utils.isNullOrEmpty( error ) || model == null) {
                    Toast.makeText( requireActivity(), error, Toast.LENGTH_SHORT ).show();
                    return;
                }

                if (model.data != null && !model.data.isEmpty()) {
                    binding.emptyPlaceHolderView.setVisibility( View.GONE );
                    List<UserDetailModel> sortedConversations = model.data.stream()
                            .filter( conversation -> !conversation.getReplies().isEmpty() )
                            .sorted( Comparator.comparing( conversation -> conversation.getReplies().get( conversation.getReplies().size() - 1 ).getCreatedAt(), Comparator.reverseOrder() ) )
                            .collect( Collectors.toList() );
                    adapter.updateData( sortedConversations );
                } else {
                    binding.emptyPlaceHolderView.setVisibility( View.VISIBLE );
                }
            }
        } );
    }

    private void requestContactReplyRead(List<String> id) {
        DataService.shared( requireActivity() ).requestContactUsReplyRead( id, new RestCallback<ContainerModel<CommanMsgModel>>(this) {
            @Override
            public void result(ContainerModel<CommanMsgModel> model, String error) {
                if (!Utils.isNullOrEmpty( error ) || model == null) {
                    Toast.makeText( requireActivity(), error, Toast.LENGTH_SHORT ).show();
                    return;
                }

            }
        } );
    }


    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------


    public class ContactListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder( UiUtils.getViewBy( parent, R.layout.contact_chat_list_item ) );
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            ViewHolder viewHolder = (ViewHolder) holder;
            UserDetailModel model = (UserDetailModel) getItem( position );
            viewHolder.binding.linearNumber.setVisibility( View.GONE );

            if (model == null) {
                return;
            }

            viewHolder.binding.tvChatUserName.setText( model.getSubject() );
            Graphics.loadImageWithFirstLetter( model.getImage(), viewHolder.binding.ivUserProfile, model.getFullName().trim() );
            //            viewHolder.binding.tvChatTime.setText( Utils.convertTimestamp( model.getReplies().get( position ).getCreatedAt(), "hh:mm a" ) );

            if (model.getReplies() != null && !model.getReplies().isEmpty()) {
                ContactChatRepliesModel lastReply = model.getReplies().get( model.getReplies().size() - 1 );
                if (lastReply != null) {
                    viewHolder.binding.tvMsg.setText( !TextUtils.isEmpty( lastReply.getReply() ) ? lastReply.getReply() : model.getMessage() );
                    viewHolder.binding.tvChatTime.setText( Utils.convertTimestamp( lastReply.getCreatedAt(), "hh:mm a" ) );

                }
            } else {
                viewHolder.binding.tvMsg.setText( model.getMessage() );
            }
            List<String> unreadReplyIds = new ArrayList<>();
            boolean isReadChat = false;
            if (model.getReplies() != null) {
                for (ContactChatRepliesModel reply : model.getReplies()) {
                    if (!reply.isRead()) {
                        unreadReplyIds.add( reply.getId() );
                        if (!reply.isRead()) {
                            isReadChat = true;
                            viewHolder.binding.notificationUnRead.setVisibility( View.VISIBLE );
                        } else {
                            viewHolder.binding.notificationUnRead.setVisibility( View.GONE );

                        }

                    }
                }
            }

            viewHolder.binding.getRoot().setOnClickListener( v -> {


                startActivity( new Intent( requireActivity(), ContactChatRepliesActivity.class )
                        .putExtra( "model", new Gson().toJson( model ) ) );

                if(!unreadReplyIds.isEmpty()){
                    requestContactReplyRead( unreadReplyIds);

                }



            } );


        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final ContactChatListItemBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super( itemView );
                binding = (ContactChatListItemBinding.bind( itemView ));
            }
        }
    }

    // endregion
    // --------------------------------------
}