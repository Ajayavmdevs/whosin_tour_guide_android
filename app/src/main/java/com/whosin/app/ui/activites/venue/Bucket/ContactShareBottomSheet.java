package com.whosin.app.ui.activites.venue.Bucket;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.comman.ui.roundcornerlayout.CornerType;
import com.whosin.app.databinding.ContactShareBottomSheetBinding;
import com.whosin.app.databinding.InviteContactFreindItemBinding;
import com.whosin.app.databinding.ItemSectionHeaderBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.ContactListModel;
import com.whosin.app.service.models.ContainerListModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.Profile.OtherUserProfileActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ContactShareBottomSheet extends DialogFragment {
    private ContactShareBottomSheetBinding binding;
    private final ContactsAdapter<ContactListModel> contactsAdapter = new ContactsAdapter<>();
    private List<ContactListModel> selectedContacts = new ArrayList<>();
    private List<ContactListModel> selectedContactsForInvite = new ArrayList<>();
    private CommanCallback<List<ContactListModel>> listener;
    public boolean myWallet = false;
    public List<String> defaultUsersList;
    private String searchQuery = "";
    private Runnable runnable = () -> updateList();
    private Handler handler = new Handler();
    private List<ContactListModel> followingList = new ArrayList<>();
    List<ContactListModel> syncedContacts;
    public CommanCallback<ContactListModel> callbackForAddToRing;
    public boolean isChangeTitle = false;

    public String bucketUserId="";

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
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
        binding = ContactShareBottomSheetBinding.bind(view);
        Glide.with(requireActivity()).load(R.drawable.icon_close_btn).into(binding.ivClose);

        binding.tvBucketTitle.setText(Utils.getLangValue("contacts"));
        binding.edtSearch.setHint(Utils.getLangValue("find_friends"));
        binding.shareBtn.setHint(Utils.getLangValue("next"));

        binding.contactRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        binding.contactRecycler.setAdapter(contactsAdapter);
        binding.shareBtn.setVisibility(myWallet ? View.GONE : View.VISIBLE);
        EventBus.getDefault().register(this);
        if (isChangeTitle){
            binding.tvBucketTitle.setText(Utils.getLangValue("add_user"));
        }

        requestFollowingList();


//        if (hasContactPermission()) {
//            ContactManager.shared.requestContact(requireActivity());
//            requestContact();
//        } else {
//            new Handler().postDelayed(() -> {
//                CollectionBottomSheet dialog = new CollectionBottomSheet();
//                dialog.setListener(data -> {
//                    ContactManager.shared.context = getActivity();
//                    ContactManager.shared.requestPermission(true,data1 -> {
//                        if (data1) {
//                            ContactManager.shared.requestContact(requireActivity());
//
//                            requestContact();
//                        }
//                    });
//                });
//                dialog.show(getParentFragmentManager(), "CollectionBottomSheet");
//            }, 1000);
//        }

    }

    public void setListener() {
        binding.ivClose.setOnClickListener(view -> dismiss());

        binding.shareBtn.setOnClickListener(view -> {
            binding.progress.setVisibility(View.VISIBLE);
            if (listener != null) {
                List<ContactListModel> uniqueContacts = new ArrayList<>(selectedContacts.stream()
                        .collect(Collectors.toMap(ContactListModel::getId, contact -> contact, (existing, replacement) -> existing))
                        .values());
                listener.onReceive(uniqueContacts);
                binding.progress.setVisibility(View.GONE);
            }
            dismiss();
        });

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
                handler.postDelayed(runnable, 1000);

            }
        });
    }

    public int getLayoutRes() {
        return R.layout.contact_share_bottom_sheet;
    }


    @Override
    public void onStart() {
        super.onStart();
//        if (getView() != null) {
//            getView().post(() -> {
//                View parent = (View) getView().getParent();
//                BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(parent);
//                int peekHeight = getResources().getDisplayMetrics().heightPixels;
//                behavior.setPeekHeight(peekHeight);
//            });
//
//        }

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = new BottomSheetDialog(requireActivity(), R.style.BottomSheetDialogThemeNoFloating);
        dialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
            FrameLayout bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            assert bottomSheet != null;
            ViewGroup.LayoutParams layoutParam = bottomSheet.getLayoutParams();
            layoutParam.height = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParam.width = ViewGroup.LayoutParams.MATCH_PARENT;
            bottomSheet.setLayoutParams(layoutParam);
//            bottomSheet.setLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
        });
        return dialog;
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
//    private boolean hasContactPermission() {
//
//        return ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
//    }

//    private void requestContact() {
//        updateList();
//    }

    private void updateList() {

//        if (TextUtils.isEmpty(searchQuery)) {
//            syncedContacts = ContactRepository.shared(getContext()).getAllContacts();
//        } else {
//            syncedContacts = ContactRepository.shared(getContext()).searchContacts(searchQuery);
//        }
        List<ContactListModel> finalList = new ArrayList<>();
        if (!followingList.isEmpty()) {
            finalList.add(new ContactListModel("-1", Utils.getLangValue("following")));
            if (!TextUtils.isEmpty(searchQuery)) {
                List<ContactListModel> searchResult = followingList.stream().filter(p -> p.getFullName().toLowerCase().contains(searchQuery.toLowerCase())).collect(Collectors.toList());
                if (!searchResult.isEmpty()) {
                    finalList.addAll(searchResult);
                }
            } else {
                finalList.addAll(followingList);
            }
        }
        if (isChangeTitle){
            List<ContactListModel> filteredContacts = new ArrayList<>();
            boolean foundInviteYourFriends = false;

            for (ContactListModel contact : syncedContacts) {
                if (!foundInviteYourFriends && contact.getFirstName().startsWith("Invite your friends")) {
                    foundInviteYourFriends = true;
                }
                if (!foundInviteYourFriends) {
                    filteredContacts.add(contact);
                }
            }

            finalList.addAll(filteredContacts);
//            finalList.addAll(syncedContacts);
        }else {
            finalList.addAll(syncedContacts);

        }
        if (defaultUsersList != null) {
            List<ContactListModel> tmpList = finalList.stream().filter(p -> defaultUsersList.contains(p.getId())).collect(Collectors.toList());
            selectedContacts.addAll(tmpList);
        }
        finalList.removeIf(contact -> contact.getId().equals(SessionManager.shared.getUser().getId()));
        contactsAdapter.updateData(finalList);
    }

    private void checkPerMissionForSms() {
        Utils.openSmsApp(requireActivity(), getSmsList());
    }

    private ArrayList<String> getSmsList() {
        ArrayList<String> phoneList = new ArrayList<>();
        for (ContactListModel model : selectedContactsForInvite) {
            if (!model.isSynced()) {
                phoneList.add(model.getPhone());
            }
        }
        return phoneList;
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
    private void requestFollowingList() {
        followingList = SessionManager.shared.getFollowingData();
        if (followingList.isEmpty()) {
            binding.progress.setVisibility(View.VISIBLE);
        }
        String id = SessionManager.shared.isPromoterSubAdmin() ? SessionManager.shared.getPromoterId() : SessionManager.shared.getUser().getId();
        DataService.shared(requireActivity()).requestFollowingList(id, new RestCallback<ContainerListModel<ContactListModel>>(this) {
            @Override
            public void result(ContainerListModel<ContactListModel> model, String error) {
                binding.progress.setVisibility(View.GONE);
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    if (!requireActivity().isDestroyed() && !requireActivity().isFinishing()) {
                        Toast.makeText(requireActivity(), R.string.service_message_something_wrong, Toast.LENGTH_SHORT).show();
                    }
//                    requestContact();
                    return;
                }
                if (model.data != null && !model.data.isEmpty()) {
                    followingList = model.data;
                    List<ContactListModel> finalList = new ArrayList<>();
                    finalList.add(new ContactListModel("-1", Utils.getLangValue("following")));
                    finalList.addAll(followingList);
                    contactsAdapter.updateData(finalList);
                    //                    if (hasContactPermission()) {
//                        requestContact();
//                    }else {
//                        if (!followingList.isEmpty()) {
//                            List<ContactListModel> finalList = new ArrayList<>();
//                            finalList.add(new ContactListModel("-1", "Following"));
//                            finalList.addAll(followingList);
//                            contactsAdapter.updateData(finalList);
//                        }
//                    }


//                    requestContact();

                    if (!SessionManager.shared.isPromoterSubAdmin()){
                        SessionManager.shared.saveFollowingData(model.data);
                    }

                }
            }
        });
    }

    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------
    public class ContactsAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == 1) {
                return new ViewHolder(UiUtils.getViewBy(parent, R.layout.invite_contact_freind_item));
            } else {
                return new TitleHolder(UiUtils.getViewBy(parent, R.layout.item_section_header));
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ContactListModel listModel = (ContactListModel) getItem(position);
            boolean isLastItem = position == getItemCount() - 1;
            if (getItemViewType(position) == 1) {
                ViewHolder viewHolder = (ViewHolder) holder;
                viewHolder.vBinding.optionContainer.setVisibility(View.GONE);
                viewHolder.vBinding.ivCheck.setVisibility(View.VISIBLE);

                viewHolder.vBinding.constrain.setCornerRadius(0, CornerType.ALL);
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
                        Log.d("TAG", "onBindViewHolder: "+"1");
                        isLastCell = true;
                    }
                }
                float cornerRadius = getActivity().getResources().getDimensionPixelSize(com.intuit.sdp.R.dimen._10sdp);
                if (isFirstCell) {
                    viewHolder.vBinding.constrain.setCornerRadius(cornerRadius, CornerType.TOP_LEFT);
                    viewHolder.vBinding.constrain.setCornerRadius(cornerRadius, CornerType.TOP_RIGHT);
                }
                if (isLastCell) {
                    viewHolder.vBinding.constrain.setCornerRadius(cornerRadius, CornerType.BOTTOM_RIGHT);
                    viewHolder.vBinding.constrain.setCornerRadius(cornerRadius, CornerType.BOTTOM_LEFT);
                }
                viewHolder.vBinding.view.setVisibility(isLastCell ? View.GONE  : View.VISIBLE);



                viewHolder.vBinding.tvUserName.setText(listModel.getFullName());
                Graphics.loadImageWithFirstLetter(listModel.getImage(), viewHolder.vBinding.ivUserProfile, listModel.getFullName());
                if (listModel.getPhone() != null && !listModel.getPhone().isEmpty()) {
                    viewHolder.vBinding.tvUserNumber.setVisibility(View.VISIBLE);
                    viewHolder.vBinding.tvUserNumber.setText(TextUtils.isEmpty(listModel.getPhone()) ? listModel.getEmail() : listModel.getPhone());
                } else {
                    viewHolder.vBinding.tvUserNumber.setVisibility(View.GONE);
                }
                boolean isChecked;

                viewHolder.vBinding.ivCheck.setPadding(listModel.isSynced() ? 0 : 4, 0, 0, 0);
                viewHolder.vBinding.ivCheck.setButtonDrawable(listModel.isSynced() ? R.drawable.check_box_select_unselect_owner : R.drawable.check_box_select_unselect);

                isChecked = (listModel.isSynced() ? selectedContacts : selectedContactsForInvite).stream().anyMatch(p -> p.getId().equals(listModel.getId()));

                if (listModel.isSynced()) {

                    viewHolder.vBinding.ivUserProfile.setOnClickListener(view -> {
                        if (!Utils.isUser(listModel.getId())) {
                            startActivity(new Intent(requireActivity(), OtherUserProfileActivity.class).putExtra("friendId", listModel.getId()));
                        }
                    });
                }

                viewHolder.vBinding.ivCheck.setOnCheckedChangeListener(null);
                viewHolder.vBinding.ivCheck.setChecked(isChecked);

                viewHolder.vBinding.ivCheck.setOnCheckedChangeListener((buttonView, isChecked1) -> {
                    if (listModel.isSynced()) {
                        if (myWallet) {
                            selectedContacts.clear();
                        }
                        boolean isAlreadySelected = selectedContacts.removeIf(p -> p.getId().equals(listModel.getId()));
                        if (!isAlreadySelected) {
                            selectedContacts.add(listModel);
                            if (myWallet && listener != null) {
                                listener.onReceive(selectedContacts);
                                dismiss();
                            }
                            if (myWallet && callbackForAddToRing != null){
                                callbackForAddToRing.onReceive(listModel);
                                dismiss();
                            }

                        }
                    } else {
                        if (myWallet) {
                            selectedContactsForInvite.clear();
                        }

                        boolean isAlreadySelectedForInvite = selectedContactsForInvite.removeIf(p -> p.getId().equals(listModel.getId()));
                        if (!isAlreadySelectedForInvite) {
                            selectedContactsForInvite.add(listModel);
                            selectedContacts.add(listModel);
                        }
                    }
                    notifyDataSetChanged();
                });

                viewHolder.itemView.setOnClickListener(view -> {
                    if (listModel.isSynced()) {
                        if (myWallet) {
                            selectedContacts.clear();
                        }

                        boolean isAlreadySelected = selectedContacts.removeIf(p -> p.getId().equals(listModel.getId()));
                        if (!isAlreadySelected) {
                            selectedContacts.add(listModel);
                            if (myWallet && listener != null) {
                                listener.onReceive(selectedContacts);
                                dismiss();
                            }
                            if (myWallet && callbackForAddToRing != null){
                                callbackForAddToRing.onReceive(listModel);
                                dismiss();
                            }
                        }
                    } else {
                        if (myWallet) {
                            selectedContactsForInvite.clear();
                        }

                        boolean isAlreadySelectedForInvite = selectedContactsForInvite.removeIf(p -> p.getId().equals(listModel.getId()));
                        if (!isAlreadySelectedForInvite) {
                            selectedContactsForInvite.add(listModel);
                            selectedContacts.add(listModel);
                        }
                    }
                    notifyDataSetChanged();
                });

                if (Utils.isUser(listModel.getId())) {
                    viewHolder.vBinding.ivCheck.setVisibility(View.GONE);
                    viewHolder.vBinding.optionContainer.setVisibility(View.GONE);
                }

                if (bucketUserId != null && !bucketUserId.isEmpty()) {
                    if (bucketUserId.equals(listModel.getId())) {
                        viewHolder.vBinding.ivCheck.setVisibility(View.GONE);
                        viewHolder.vBinding.optionContainer.setVisibility(View.GONE);
                    }
                }

            } else {
                TitleHolder viewHolder = (TitleHolder) holder;
                viewHolder.vBinding.titleText.setText(listModel.getFirstName());
                viewHolder.vBinding.tvInvite.setVisibility(listModel.isSynced() ? View.GONE : View.VISIBLE);
                viewHolder.vBinding.tvInvite.setText(String.format("Invite(%d)", selectedContactsForInvite.size()));

                viewHolder.vBinding.tvInvite.setOnClickListener(view -> {
                    checkPerMissionForSms();
                });
            }

            if (isLastItem) {
                int marginBottom = Utils.getMarginBottom(holder.itemView.getContext(), 0.22f);
                Utils.setBottomMargin(holder.itemView, marginBottom);
            } else {
                Utils.setBottomMargin(holder.itemView, 0);
            }
        }

        public int getItemViewType(int position) {
            ContactListModel inModel = (ContactListModel) getItem(position);
            if (inModel.getId().equals("-1")) {
                return 0;
            } else {
                return 1;
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final InviteContactFreindItemBinding vBinding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                vBinding = InviteContactFreindItemBinding.bind(itemView);
            }
        }

        public class TitleHolder extends RecyclerView.ViewHolder {
            public final ItemSectionHeaderBinding vBinding;

            public TitleHolder(@NonNull View itemView) {
                super(itemView);
                vBinding = ItemSectionHeaderBinding.bind(itemView);
            }
        }

    }

    // endregion
    // --------------------------------------
}