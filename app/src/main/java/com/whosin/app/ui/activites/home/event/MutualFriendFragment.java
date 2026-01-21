package com.whosin.app.ui.activites.home.event;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.whosin.app.R;
import com.whosin.app.comman.Utils;
import com.whosin.app.databinding.FragmentMutualFriendBinding;
import com.whosin.app.service.models.ContactListModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.ui.adapter.FriendsAdapter;

import java.util.List;
import java.util.stream.Collectors;


public class MutualFriendFragment extends DialogFragment {
    private FragmentMutualFriendBinding binding;
    public UserDetailModel mutualFriendsList;
    private FriendsAdapter<ContactListModel> invitedPeopleAdapter;


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutRes(), container, false);
        initUi(view);
        setListener();
        return view;

    }

    public void initUi(View view) {
        binding = FragmentMutualFriendBinding.bind(view);

        binding.cancelBtn.setText(Utils.getLangValue("cancel"));
        binding.doneBtn.setText(Utils.getLangValue("done"));
        binding.inviteGuestTv.setText(Utils.getLangValue("mutual_friends"));


        binding.invitedGuestRecycle.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false));
        invitedPeopleAdapter = new FriendsAdapter<>(getActivity());
        invitedPeopleAdapter.setCallback(data -> {

        });
        binding.invitedGuestRecycle.setAdapter(invitedPeopleAdapter);
        if (mutualFriendsList.getMutualFriends() != null && !mutualFriendsList.getMutualFriends().isEmpty()) {
            List<ContactListModel> list = mutualFriendsList.getMutualFriends().stream().peek(model -> model.setFollow("approved")).collect(Collectors.toList());
            invitedPeopleAdapter.updateData(list);
        }


    }

    public void setListener() {
        binding.cancelBtn.setOnClickListener(view -> dismiss());
        binding.doneBtn.setOnClickListener(view -> dismiss());

    }

    public int getLayoutRes() {
        return R.layout.fragment_mutual_friend;
    }


    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new BottomSheetDialog(requireActivity(), R.style.BottomSheetDialogThemeNoFloating);
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

}