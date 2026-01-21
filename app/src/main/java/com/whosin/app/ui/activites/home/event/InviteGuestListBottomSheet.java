package com.whosin.app.ui.activites.home.event;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.whosin.app.R;
import com.whosin.app.comman.Utils;
import com.whosin.app.databinding.FragmentInviteGuestListBottomSheetBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.models.ContactListModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.EventGuestListModel;
import com.whosin.app.service.models.InviationsModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.adapter.FriendsAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class InviteGuestListBottomSheet extends DialogFragment {
    private FragmentInviteGuestListBottomSheetBinding binding;
    private EventGuestListModel eventGuestListModel;
    private FriendsAdapter<ContactListModel> friendsAdapter;

    public String eventId;
    public String type = "";

    public List<ContactListModel> model = new ArrayList<>();
    private int page = 1;

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate( getLayoutRes(), container, false );
        initUi( view );
        setListener();
        return view;

    }

    public void initUi(View view) {
        binding = FragmentInviteGuestListBottomSheetBinding.bind( view );

        binding.cancelBtn.setText(Utils.getLangValue("cancel"));
        binding.doneBtn.setText(Utils.getLangValue("done"));
        binding.inviteGuestTv.setText(Utils.getLangValue("invite_guests"));

        binding.invitedGuestRecycle.setLayoutManager( new LinearLayoutManager( requireActivity(), LinearLayoutManager.VERTICAL, false ) );
        friendsAdapter = new FriendsAdapter<>(requireActivity(),true);
        binding.invitedGuestRecycle.setAdapter( friendsAdapter );

        if (!Utils.isNullOrEmpty( eventId )) {
            requestEventGuestList( eventId );
        } else {
            friendsAdapter.updateData( model );
        }
    }

    public void setListener() {
        binding.cancelBtn.setOnClickListener( view -> dismiss() );
        binding.doneBtn.setOnClickListener( view -> dismiss() );

        binding.invitedGuestRecycle.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) binding.invitedGuestRecycle.getLayoutManager();
                int lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();

                if (lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == friendsAdapter.getData().size() - 1) {
                    if (friendsAdapter.getData().size() % 30 == 0) {
                        page++;
                        requestEventGuestList( eventId );
                    }
                }

            }
        });

    }

    public int getLayoutRes() {
        return R.layout.fragment_invite_guest_list_bottom_sheet;
    }


    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new BottomSheetDialog( requireActivity(), R.style.BottomSheetDialogThemeNoFloating );
    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestEventGuestList(String eventId) {
        binding.progress.setVisibility( View.VISIBLE );
        DataService.shared( requireActivity() ).requestEventGuestList( page, 30, eventId, "", new RestCallback<ContainerModel<EventGuestListModel>>(this) {
            @Override
            public void result(ContainerModel<EventGuestListModel> model, String error) {
                binding.progress.setVisibility( View.GONE );
                if (!Utils.isNullOrEmpty( error ) || model == null) {
                    Toast.makeText( getContext(), error, Toast.LENGTH_SHORT ).show();
                    return;

                }

                if (model.getData() != null) {
                    eventGuestListModel = model.getData();
                    List<InviationsModel> invitedUser = eventGuestListModel.getInviationsModels();
                    if (type.equalsIgnoreCase("IN")) {
                        invitedUser = eventGuestListModel.getInviationsModels().stream().filter( model1 -> model1.getInviteStatus().equals( "in" ) ).collect( Collectors.toList() );
                    }

                    List<ContactListModel> contactListModels = new ArrayList<>();
                    invitedUser.forEach(p -> {
                        Optional<ContactListModel> userModel = eventGuestListModel.getUserModel().stream().filter(u -> u.getId().equals(p.getUserId())).findFirst();
                        if (userModel.isPresent()) {
                            ContactListModel model1 = userModel.get();
                            model1.setInviteStatus(p.getInviteStatus());
                            contactListModels.add(model1);
                        }
                    });

                    List<ContactListModel> exitingData = friendsAdapter.getData();
                    if (exitingData.isEmpty()) {
                        friendsAdapter.updateData(contactListModels);
                    } else {
                        contactListModels.addAll(exitingData);
                        friendsAdapter.updateData(exitingData);
                    }
                }
            }
        } );
    }

    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------


}