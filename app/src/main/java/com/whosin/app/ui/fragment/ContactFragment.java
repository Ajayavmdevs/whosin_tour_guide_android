package com.whosin.app.ui.fragment;

import android.content.Intent;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.whosin.app.CollectionBottomSheet;
import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.comman.ui.roundcornerlayout.CornerType;
import com.whosin.app.databinding.FragmentContactBinding;
import com.whosin.app.databinding.ItemFriendContactBinding;
import com.whosin.app.databinding.ItemSectionHeaderBinding;
import com.whosin.app.service.Repository.ContactRepository;
import com.whosin.app.service.manager.ContactManager;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.ContactListModel;
import com.whosin.app.ui.activites.Profile.OtherUserProfileActivity;
import com.whosin.app.ui.fragment.comman.BaseFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ContactFragment extends BaseFragment {

    private FragmentContactBinding binding;
    private FriendsAdapter<ContactListModel> inviteFriendAdapter;
    private List<ContactListModel> allContacts;
    private String searchQuery = "";
    private Runnable runnable = () -> updateList();
    private Handler handler = new Handler();

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------


    public ContactFragment() {
    }

    @Override
    public void initUi(View view) {
        binding = FragmentContactBinding.bind(view);
        binding.contactRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        inviteFriendAdapter = new FriendsAdapter<>();
        binding.contactRecycler.setAdapter(inviteFriendAdapter);

//        if (hasContactPermission()) {
//            ContactManager.shared.requestContact(requireActivity());
//            requestContact();
//        } else {
//            binding.contactRecycler.setVisibility(View.GONE);
//            binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
//            binding.emptyPlaceHolderView.setEmptyPlaceTxtTitle("Allow contact sharing to get the best experience and connect with your friends on Whosin!");
//
//
//            if (!Preferences.shared.isExist("contact_permission")) {
//                requestPermission();
//            }
//        }
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

    }

    private void requestPermission() {
        new Handler().postDelayed(() -> {
            CollectionBottomSheet dialog = new CollectionBottomSheet();
            dialog.setListener(data -> {
                ContactManager.shared.context = getActivity();
                ContactManager.shared.requestPermission(true,data1 -> {
                    if (data1) {
                        requestContact();
                    }
                });
            });
            dialog.show(getParentFragmentManager(), "CollectionBottomSheet");
        }, 1000);
    }


    @Override
    public void setListeners() {
        binding.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                searchQuery = editable.toString();
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, 200);

            }
        });
    }

    @Override
    public void populateData(boolean getDataFromServer) {

    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_contact;
    }

    @Override
    public void onResume() {
        super.onResume();
//        if (hasContactPermission()) {
//            requestContact();
//        }
//        else if (Preferences.shared.isExist("contact_permission")) {
//            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//            intent.setData(Uri.fromParts("package", "com.whosin.me", null));
//            context.startActivity(intent);
//            requestPermission();
//        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ContactListModel event) {
        updateList();
    }
    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------
    private void requestContact() {
        updateList();
    }

    private void updateList() {
        if (TextUtils.isEmpty(searchQuery)) {
            allContacts = ContactRepository.shared(context).getAllContacts();
        } else {
            allContacts = ContactRepository.shared(context).searchContacts(searchQuery);
        }
        allContacts.removeIf(p -> p.getId().equals(SessionManager.shared.getUser().getId()));
        if (!allContacts.isEmpty()) { hideProgress(); }

//        if (allContacts != null && !allContacts.isEmpty()) {
//            binding.contactRecycler.setVisibility(View.VISIBLE);
//            binding.emptyPlaceHolderView.setVisibility(View.GONE);
//            if (hasContactPermission()) {
//                inviteFriendAdapter.updateData(allContacts);
//                inviteFriendAdapter.notifyDataSetChanged();
//            } else {
//                binding.contactRecycler.setVisibility(View.GONE);
//            }
//        } else {
//            binding.contactRecycler.setVisibility(View.GONE);
//            binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
//        }
    }

//    private boolean hasContactPermission() {
//        if (context == null) {
//            return false;
//        }
//        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
//    }

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestFollowingList() {
        ProfileFragment parentActivity = (ProfileFragment) getActivity();
        if (parentActivity != null) {
            parentActivity.requestUserProfile();
        }
    }

    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------


    public class FriendsAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        private ArrayList<String> selectedPhones = new ArrayList<>();

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == 1) {
                return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_friend_contact));
            }
            return new TitleHolder(UiUtils.getViewBy(parent, R.layout.item_section_header));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ContactListModel model = (ContactListModel) getItem(position);
            if (model == null) { return; }
            if (!model.getId().equals("-1")) {
                ViewHolder viewHolder = (ViewHolder) holder;
                viewHolder.binding.contactLayout.setCornerRadius(0, CornerType.ALL);
                boolean isFirstCell = false;
                boolean isLastCell = getItemCount() - 1 == position;
                if (position > 0) {
                    ContactListModel previousModel = (ContactListModel) getItem(position - 1);
                    if (previousModel.getId().equals("-1")) {
                        isFirstCell = true;
                    }
                }
                if (getItemCount() > position + 1) {
                    ContactListModel nextModel = (ContactListModel) getItem(position + 1);
                    if (nextModel.getId().equals("-1")) {
                        isLastCell = true;
                    }
                }
                float cornerRadius = context.getResources().getDimensionPixelSize(com.intuit.sdp.R.dimen._10sdp);
                if (isFirstCell) {
                    viewHolder.binding.contactLayout.setCornerRadius(cornerRadius, CornerType.TOP_LEFT);
                    viewHolder.binding.contactLayout.setCornerRadius(cornerRadius, CornerType.TOP_RIGHT);
                }
                if (isLastCell) {
                    viewHolder.binding.contactLayout.setCornerRadius(cornerRadius, CornerType.BOTTOM_RIGHT);
                    viewHolder.binding.contactLayout.setCornerRadius(cornerRadius, CornerType.BOTTOM_LEFT);
                }
                viewHolder.binding.viewLine.setVisibility(isLastCell ? View.GONE  : View.VISIBLE);

                viewHolder.binding.tvUserName.setText(model.getFullName());
                Graphics.loadImageWithFirstLetter(model.getImage(), viewHolder.binding.ivUserProfile, model.getFullName());

                String userStatusText = TextUtils.isEmpty(model.getPhone()) ? model.getEmail() : model.getPhone();
                viewHolder.binding.tvUserStatus.setText(userStatusText);
                viewHolder.binding.tvUserStatus.setVisibility(userStatusText.isEmpty() ? View.GONE : View.VISIBLE);

                viewHolder.binding.ivCheck.setVisibility(model.isSynced() ? View.GONE : View.VISIBLE);
                viewHolder.binding.optionContainer.setVisibility(model.isSynced() ? View.VISIBLE : View.GONE);
                if (!model.isSynced()) {
                    viewHolder.binding.ivCheck.setOnCheckedChangeListener(null);
                    viewHolder.binding.ivCheck.setChecked(selectedPhones.contains(model.getPhone()));
                }

                viewHolder.binding.optionContainer.setupView(model, getActivity(), data -> {
                    requestFollowingList();
                    inviteFriendAdapter.notifyDataSetChanged();
//                    ContactRepository.shared(getActivity()).updateUserFollowStatus(model);
                });

                viewHolder.setListeners(model);
            }
            else {
                TitleHolder viewHolder = (TitleHolder) holder;
                viewHolder.binding.titleText.setText(model.getFirstName());
                viewHolder.binding.tvInvite.setVisibility(model.isSynced() ? View.GONE : View.VISIBLE);
                viewHolder.binding.tvInvite.setText(String.format(Locale.ENGLISH,"Invite(%d)", selectedPhones.size()));
                viewHolder.binding.tvInvite.setOnClickListener(view -> {
                    Utils.openSmsApp(requireActivity(), selectedPhones);
                });
            }
        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            private final ItemFriendContactBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemFriendContactBinding.bind(itemView);
            }

            private void setListeners(ContactListModel model) {

                // Open OtherUser Profile Activity
                binding.getRoot().setOnClickListener(view -> {
                    if (model.isSynced()) {
                        if (!Utils.isUser(model.getId())) {
                            startActivity(new Intent(requireActivity(), OtherUserProfileActivity.class).putExtra("friendId", model.getId()));
                        }
                    } else {
                        if (selectedPhones.contains(model.getPhone())) {
                            selectedPhones.remove(model.getPhone());
                        } else {
                            selectedPhones.add(model.getPhone());
                        }
                        notifyDataSetChanged();
                    }
                });

                binding.ivCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (model.isSynced()) { return; }
                    if (isChecked) {
                        selectedPhones.add(model.getPhone());
                    } else {
                        selectedPhones.remove(model.getPhone());
                    }
                    notifyDataSetChanged();
                });

            }

        }

        @Override
        public int getItemViewType(int position) {
            ContactListModel inModel = (ContactListModel) getItem(position);
            if (inModel.getId().equals("-1")) {
                return 0;
            } else {
                return 1;
            }
        }

        public class TitleHolder extends RecyclerView.ViewHolder {
            private final ItemSectionHeaderBinding binding;

            public TitleHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemSectionHeaderBinding.bind(itemView);
            }
        }
    }


    // endregion
    // --------------------------------------

}