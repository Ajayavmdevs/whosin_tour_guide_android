package com.whosin.app.ui.fragment.CmProfile.CmBottomSheets;

import static android.view.View.VISIBLE;
import static com.whosin.app.comman.AppDelegate.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.tapadoo.alerter.Alerter;
import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.comman.ui.roundcornerlayout.CornerType;
import com.whosin.app.databinding.FragmentAddPlusOneGuestBottomSheetBinding;
import com.whosin.app.databinding.ItemMyPlusOneMemberBinding;
import com.whosin.app.databinding.ItemSectionHeaderBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.ComplimentaryProfileModel;
import com.whosin.app.service.models.ContactListModel;
import com.whosin.app.service.models.ContainerListModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.PromoterEventModel;
import com.whosin.app.service.models.PromoterListModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.Profile.OtherUserProfileActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;

public class AddPlusOneGuestBottomSheet extends DialogFragment {

    private FragmentAddPlusOneGuestBottomSheetBinding binding;

    public PromoterEventModel promoterEventModel = null;
    public boolean isInvitePlusOne = false;

    public CommanCallback<String> callback;
    public String type = "";
    public String eventID = "";
    private String selectedId = "";
    private int page = 1;

    private boolean isLoading = false;

    private List<UserDetailModel> selectedContacts = new ArrayList<>();
    private List<UserDetailModel> plusOneGroupList = new ArrayList<>();

    private MyPlusGroupListAdapter<UserDetailModel> myPlusGroupListAdapter = new MyPlusGroupListAdapter<>();

    public List<PromoterListModel> models;

    private final ContactsAdapter<ContactListModel> contactsAdapter = new ContactsAdapter<>();
    private List<ContactListModel> syncedContacts;
    private List<ContactListModel> followingList = new ArrayList<>();

    private String searchQuery = "";
    private final Runnable runnable = () -> serachByName();
    private final Handler handler = new Handler();

    private Call<ContainerListModel<ContactListModel>> service = null;


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
        binding = FragmentAddPlusOneGuestBottomSheetBinding.bind(view);

        binding.tvBucketTitle.setText(Utils.getLangValue("invite"));
        binding.edtSearch.setText(Utils.getLangValue("find_friends"));
        binding.tvForHighLight.setText(Utils.getLangValue("you_can_only_search"));
        binding.inviteYourFriends.setText(Utils.getLangValue("invite_your_friends"));
        binding.emptyPlaceHolderView.setEmptyPlaceTxtTitle(Utils.getLangValue("your_friends_list"));
        binding.btnInvite.setTxtTitle(Utils.getLangValue("invite"));

        Glide.with(requireActivity()).load(R.drawable.icon_close_btn).into(binding.ivClose);

        binding.addUserRecycler.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        if (!TextUtils.isEmpty(type) && type.equals("MyPlusOne")) {
            binding.tvForHighLight.setVisibility(View.GONE);
            binding.addUserRecycler.setAdapter(contactsAdapter);
            requestFollowingList();
        } else {
            binding.inviteYourFriends.setVisibility(View.GONE);
            String showText = Utils.setLangValue("min_friends_required",String.valueOf(promoterEventModel.getPlusOneQty()));
            binding.tvForHighLight.setText(showText);
            binding.addUserRecycler.setAdapter(myPlusGroupListAdapter);
            requestPlusOneGroupList();
        }


    }

    public void setListener() {

        binding.ivClose.setOnClickListener(v -> dismiss());


        binding.btnInvite.setOnClickListener(view -> {
            Utils.preventDoubleClick(view);
            if (!TextUtils.isEmpty(type) && type.equals("MyPlusOne")) {
                requestAddMemberToPlusOne(selectedId);
            } else {
                if (selectedContacts.isEmpty()) {
                    Toast.makeText(requireActivity(), Utils.getLangValue("please_select_contacts_to_invite"), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (isInvitePlusOne && selectedContacts.size() < promoterEventModel.getPlusOneQty()) {
                    Graphics.showAlertDialogWithOkButton(
                            requireActivity(),
                            getString(R.string.app_name),
                            Utils.setLangValue("please_invite_user_alert",String.valueOf(promoterEventModel.getPlusOneQty()))
                    );
                    return;
                }

                requestPromoterEventPlusOneInvite();
            }

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
                requestUserSearchAll(false);
            }
        });

    }


    public int getLayoutRes() {
        return R.layout.fragment_add_plus_one_guest_bottom_sheet;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = new BottomSheetDialog(requireActivity(), R.style.BottomSheetDialogThemeNoFloating);
        dialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
            FrameLayout bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            ViewGroup.LayoutParams layoutParam = bottomSheet.getLayoutParams();
            layoutParam.height = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParam.width = ViewGroup.LayoutParams.MATCH_PARENT;
            bottomSheet.setLayoutParams(layoutParam);
            if (bottomSheet != null) {
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
        return dialog;
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void serachByName() {
        List<UserDetailModel> filteredList = new ArrayList<>();
        List<ContactListModel> contactList = new ArrayList<>();

        if (Utils.isNullOrEmpty(searchQuery)) {
            if (!TextUtils.isEmpty(type) && type.equals("MyPlusOne")) {
                updateList();
            } else {
                myPlusGroupListAdapter.updateData(plusOneGroupList);
            }
        } else {
            if (!TextUtils.isEmpty(type) && type.equals("MyPlusOne")) {
                requestUserSearchAll(true);
//                contactList = followingList.stream().filter(model -> model.getFullName().trim().toLowerCase().contains(searchQuery.toLowerCase().trim())).collect(Collectors.toList());
//                contactsAdapter.updateData(contactList);
            } else {
                filteredList = myPlusGroupListAdapter.getData().stream().filter(model -> model.getFullName().trim().toLowerCase().contains(searchQuery.toLowerCase().trim())).collect(Collectors.toList());
                myPlusGroupListAdapter.updateData(filteredList);
            }
        }

    }

//    private boolean hasContactPermission() {
//        return ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
//    }


    private void updateList() {

//        syncedContacts = ContactRepository.shared(getContext()).getAllInviteFriendContacts();
        List<ContactListModel> finalList = new ArrayList<>();
        if (!followingList.isEmpty()) {
            followingList.removeIf(ContactListModel::isPromoter);
            followingList.removeIf(p -> (!TextUtils.isEmpty(p.getPlusOneStatus()) && p.getPlusOneStatus().equals("accepted")) &&
                    (!TextUtils.isEmpty(p.getAdminStatusOnPlusOne()) && p.getAdminStatusOnPlusOne().equals("accepted"))
            );
            finalList.addAll(followingList);
        }
        finalList.addAll(syncedContacts);
        finalList.removeIf(contact -> contact.getId().equals(SessionManager.shared.getUser().getId()));
        contactsAdapter.updateData(finalList);
    }


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestFollowingList() {
        Graphics.showProgress(requireActivity());
        DataService.shared(requireActivity()).requestFollowingList(SessionManager.shared.getUser().getId(), new RestCallback<ContainerListModel<ContactListModel>>(this) {
            @Override
            public void result(ContainerListModel<ContactListModel> model, String error) {
                Graphics.hideProgress(requireActivity());
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    if (!requireActivity().isDestroyed() && !requireActivity().isFinishing()) {
                        Toast.makeText(requireActivity(), R.string.service_message_something_wrong, Toast.LENGTH_SHORT).show();
                    }
                    updateList();
                    return;
                }

//                if (hasContactPermission()) {
//                    updateList();
//                }
                if (model.data != null && !model.data.isEmpty()) {
                    followingList = model.data;
                    List<ContactListModel> finalList = new ArrayList<>();
                    finalList.add(new ContactListModel("-1", "Following"));
                    finalList.addAll(followingList);
                    contactsAdapter.updateData(finalList);
                    //                    if (hasContactPermission()) {
//                        updateList();
//                    } else {
//
//                    }
                    SessionManager.shared.saveFollowingData(model.data);
                }
//                else {
//                    if (hasContactPermission()) {
//                        updateList();
//                    } else {
//                        new Handler().postDelayed(() -> {
//                            CollectionBottomSheet dialog = new CollectionBottomSheet();
//                            dialog.setListener(data -> {
//                                ContactManager.shared.context = getActivity();
//                                ContactManager.shared.requestPermission(true, data1 -> {
//                                    if (data1) {
//                                        ContactManager.shared.requestContact(requireActivity());
//                                        updateList();
//                                    }
//                                });
//                            });
//                            dialog.show(getParentFragmentManager(), "CollectionBottomSheet");
//                        }, 1000);
//                    }
//                }
            }
        });
    }

    private void requestPlusOneGroupList() {
        Graphics.showProgress(requireActivity());
        DataService.shared(requireActivity()).requestPromoterPlusOneMyGroup(new RestCallback<ContainerListModel<UserDetailModel>>(this) {
            @Override
            public void result(ContainerListModel<UserDetailModel> model, String error) {
                Graphics.hideProgress(requireActivity());
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(requireActivity(), error, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (model.data != null && !model.data.isEmpty()) {
                    plusOneGroupList.clear();
                    plusOneGroupList = model.data;
                    myPlusGroupListAdapter.updateData(model.data);
                }
            }
        });
    }

    private void requestPromoterEventPlusOneInvite() {
        if (selectedContacts.isEmpty()) return;
        if (TextUtils.isEmpty(eventID)) return;
        List<String> selectedID = selectedContacts.stream().map(UserDetailModel::getId).collect(Collectors.toList());
        Graphics.showProgress(requireActivity());
        DataService.shared(requireActivity()).requsetPromoterEventPlusOneInvite(eventID, selectedID, new RestCallback<ContainerModel<PromoterEventModel>>(this) {
            @Override
            public void result(ContainerModel<PromoterEventModel> model, String error) {
                Graphics.hideProgress(requireActivity());
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(requireActivity(), error, Toast.LENGTH_SHORT).show();
                    return;
                }

                Alerter.create(activity).setTitle(model.message).setTitleAppearance(R.style.AlerterTitle).setBackgroundColorRes(R.color.AlerterSuccessBg).hideIcon().show();
                EventBus.getDefault().post(new ComplimentaryProfileModel());
                if (callback != null) {
                    if (isInvitePlusOne) {
                        callback.onReceive("CallEventIn");
                    } else {
                        callback.onReceive("CallEventDetail");
                    }
                }
                dismiss();

            }
        });
    }

    private void requestAddMemberToPlusOne(String id) {
        if (TextUtils.isEmpty(id)) {
            Toast.makeText(requireActivity(), "Please select member", Toast.LENGTH_SHORT).show();
            return;
        }

        Graphics.showProgress(requireActivity());
        DataService.shared(activity).requestPromoterPlusOneInviteUser(id, new RestCallback<ContainerModel<PromoterEventModel>>(this) {
            @Override
            public void result(ContainerModel<PromoterEventModel> model, String error) {
                Graphics.hideProgress(requireActivity());
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(activity, model.message, Toast.LENGTH_SHORT).show();
                EventBus.getDefault().post(new ComplimentaryProfileModel());
                dismiss();

            }
        });
    }

    private void requestUserSearchAll(boolean isShowProgress) {
        if (TextUtils.isEmpty(searchQuery)) return;
        if (service != null) {
            service.cancel();
        }

        service = DataService.shared(requireActivity()).requestUserSearchAll(searchQuery, page, new RestCallback<ContainerListModel<ContactListModel>>(this) {
            @Override
            public void result(ContainerListModel<ContactListModel> model, String error) {
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(requireActivity(), error, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (model.data != null && !model.data.isEmpty()) {
                    model.data.removeIf(ContactListModel::isPromoter);
                    contactsAdapter.updateData(model.data);
                }

                binding.addUserRecycler.setVisibility(model.data != null && !model.data.isEmpty() ? VISIBLE : View.GONE);
                binding.emptyPlaceHolderView.setVisibility(model.data != null && !model.data.isEmpty() ? View.GONE : VISIBLE);
                binding.emptyPlaceHolderView.setEmptyPlaceTxtTitle("There is no user available");
            }
        });
    }

    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    private class ContactsAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == 1) {
                return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_my_plus_one_member));
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
                viewHolder.vBinding.ivCheck.setVisibility(View.VISIBLE);
                viewHolder.vBinding.tvBio.setVisibility(View.GONE);
                viewHolder.vBinding.optionContainer.setVisibility(View.GONE);
                viewHolder.vBinding.contactLayout.setCornerRadius(0, CornerType.ALL);
                boolean isFirstCell = position == 0;
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
                        Log.d("TAG", "onBindViewHolder: " + "1");
                        isLastCell = true;
                    }
                }
                float cornerRadius = getActivity().getResources().getDimensionPixelSize(com.intuit.sdp.R.dimen._10sdp);
                if (isFirstCell) {
                    viewHolder.vBinding.contactLayout.setCornerRadius(cornerRadius, CornerType.TOP_LEFT);
                    viewHolder.vBinding.contactLayout.setCornerRadius(cornerRadius, CornerType.TOP_RIGHT);
                }
                if (isLastCell) {
                    viewHolder.vBinding.contactLayout.setCornerRadius(cornerRadius, CornerType.BOTTOM_RIGHT);
                    viewHolder.vBinding.contactLayout.setCornerRadius(cornerRadius, CornerType.BOTTOM_LEFT);
                }
                viewHolder.vBinding.viewLine.setVisibility(isLastCell ? View.GONE : View.VISIBLE);

                viewHolder.vBinding.tvUserStatus.setVisibility(listModel.showPlusOneStatus() ? View.VISIBLE : View.GONE);
                viewHolder.vBinding.btnInvite.setVisibility(listModel.showPlusOneStatus() ? View.VISIBLE : View.GONE);
                viewHolder.vBinding.ivCheck.setVisibility(listModel.showPlusOneStatus() ? View.GONE : View.VISIBLE);

                // TODO : Reduce condition
                if ((listModel.getFollow().equals("pending") || listModel.getFollow().equals("cancelled") || listModel.getFollow().equals("none") && !listModel.isRingMember())){
                    viewHolder.vBinding.ivCheck.setVisibility(View.GONE);
                    if (listModel.getFollow().equals("approved")){
                        viewHolder.vBinding.tvUserStatus.setVisibility(View.GONE);
                        viewHolder.vBinding.btnInvite.setVisibility(View.GONE);
                        viewHolder.vBinding.ivCheck.setVisibility(View.VISIBLE);
                        viewHolder.vBinding.optionContainer.setVisibility(View.GONE);
                        viewHolder.vBinding.ivCheck.setVisibility(VISIBLE);
                    }else {
                        viewHolder.vBinding.btnInvite.setVisibility(View.GONE);
                        viewHolder.vBinding.tvUserStatus.setVisibility(View.GONE);
                        viewHolder.vBinding.optionContainer.setVisibility(VISIBLE);
                        viewHolder.vBinding.optionContainer.setupView(listModel, requireActivity(), data -> {
                            if (data){
                                contactsAdapter.getData().stream()
                                        .filter(p -> p.getId().equals(listModel.getId()))
                                        .findFirst()
                                        .ifPresent(p -> p.setFollow("approved"));
                                contactsAdapter.notifyDataSetChanged();

                            }
                        });

                    }
                } else if (listModel.getFollow().equals("approved") && listModel.showPlusOneStatus() && !TextUtils.isEmpty(listModel.getPlusOneStatus())) {
                    viewHolder.vBinding.btnInvite.setText(Utils.getLangValue("resend"));
                    if (listModel.getPlusOneStatus().equalsIgnoreCase("rejected")) {
                        viewHolder.vBinding.tvUserStatus.setTextColor(ContextCompat.getColor(requireActivity(), R.color.red));
                        viewHolder.vBinding.tvUserStatus.setText(Utils.getLangValue("rejected"));
                    } else if (listModel.getAdminStatusOnPlusOne().equals("pending") && listModel.getPlusOneStatus().equals("accepted")) {
                        viewHolder.vBinding.tvUserStatus.setTextColor(ContextCompat.getColor(requireActivity(), R.color.pending_yellow));
                        viewHolder.vBinding.tvUserStatus.setText(Utils.getLangValue("waiting_for_admin_approval"));
                        viewHolder.vBinding.btnInvite.setVisibility(View.GONE);
                        viewHolder.vBinding.ivCheck.setVisibility(View.GONE);
                    } else if (listModel.getPlusOneStatus().equals("pending")) {
                        viewHolder.vBinding.tvUserStatus.setTextColor(ContextCompat.getColor(requireActivity(), R.color.pending_yellow));
                        viewHolder.vBinding.tvUserStatus.setText(Utils.getLangValue("pending"));

                    } else {
                        viewHolder.vBinding.tvUserStatus.setTextColor(ContextCompat.getColor(requireActivity(), R.color.pending_yellow));
                    }
                } else if (listModel.getFollow().equals("approved") && listModel.showPlusOneStatus() && !TextUtils.isEmpty(listModel.getAdminStatusOnPlusOne()) && listModel.getAdminStatusOnPlusOne().equals("pending")) {
                    viewHolder.vBinding.tvUserStatus.setTextColor(ContextCompat.getColor(requireActivity(), R.color.pending_yellow));
                    viewHolder.vBinding.tvUserStatus.setText(Utils.getLangValue("waiting_for_admin_approval"));
                    viewHolder.vBinding.btnInvite.setVisibility(View.GONE);
                    viewHolder.vBinding.ivCheck.setVisibility(View.GONE);
                } else {
                    viewHolder.vBinding.ivCheck.setVisibility(View.GONE);
                    if (listModel.getFollow().equals("approved")){
                        viewHolder.vBinding.tvUserStatus.setVisibility(View.GONE);
                        viewHolder.vBinding.btnInvite.setVisibility(View.GONE);
                        viewHolder.vBinding.ivCheck.setVisibility(View.VISIBLE);
                        viewHolder.vBinding.optionContainer.setVisibility(View.GONE);
                        viewHolder.vBinding.ivCheck.setVisibility(VISIBLE);
                    }else {
                        viewHolder.vBinding.btnInvite.setVisibility(View.GONE);
                        viewHolder.vBinding.tvUserStatus.setVisibility(View.GONE);
                        viewHolder.vBinding.optionContainer.setVisibility(VISIBLE);
                        viewHolder.vBinding.optionContainer.setupView(listModel, requireActivity(), data -> {
                            if (data){
                                contactsAdapter.getData().stream()
                                        .filter(p -> p.getId().equals(listModel.getId()))
                                        .findFirst()
                                        .ifPresent(p -> p.setFollow("approved"));
                                contactsAdapter.notifyDataSetChanged();

                            }
                        });

                    }
                }


                if (!listModel.isSynced() && TextUtils.isEmpty(searchQuery)) {
                    viewHolder.vBinding.btnInvite.setVisibility(View.VISIBLE);
                    viewHolder.vBinding.btnInvite.setText(Utils.getLangValue("invite"));
                    viewHolder.vBinding.ivCheck.setVisibility(View.GONE);
                    viewHolder.vBinding.optionContainer.setVisibility(View.GONE);
                }

                viewHolder.vBinding.tvUserName.setText(listModel.getFullName());
                Graphics.loadImageWithFirstLetter(listModel.getImage(), viewHolder.vBinding.ivUserProfile, listModel.getFullName());

                viewHolder.vBinding.ivCheck.setOnCheckedChangeListener(null);

                if (!TextUtils.isEmpty(selectedId)) {
                    viewHolder.vBinding.ivCheck.setChecked(selectedId.equals(listModel.getId()));
                    viewHolder.vBinding.ivCheck.setButtonDrawable(selectedId.equals(listModel.getId()) ? R.drawable.check_box_select_unselect_owner : R.drawable.complete_icon_unselected);
                } else {
                    viewHolder.vBinding.ivCheck.setButtonDrawable(R.drawable.complete_icon_unselected);
                }


                viewHolder.vBinding.ivUserProfile.setOnClickListener(view -> {
                    if (!Utils.isUser(listModel.getId()) && listModel.isSynced()) {
                        startActivity(new Intent(requireActivity(), OtherUserProfileActivity.class).putExtra("friendId", listModel.getId()));
                    }
                });


                viewHolder.vBinding.getRoot().setOnClickListener(v -> {
                    if (viewHolder.vBinding.ivCheck.getVisibility() == View.VISIBLE) {
                        if (TextUtils.isEmpty(selectedId)) {
                            selectedId = listModel.getId();
                        } else {
                            if (selectedId.equals(listModel.getId())) {
                                selectedId = "";
                            } else {
                                selectedId = listModel.getId();
                            }
                        }
                        contactsAdapter.notifyDataSetChanged();
                    }
                });

                viewHolder.vBinding.ivCheck.setOnCheckedChangeListener((buttonView, isChecked1) -> {
                    if (viewHolder.vBinding.ivCheck.getVisibility() == View.VISIBLE) {
                        if (TextUtils.isEmpty(selectedId)) {
                            selectedId = listModel.getId();
                        } else {
                            if (selectedId.equals(listModel.getId())) {
                                selectedId = "";
                            } else {
                                selectedId = listModel.getId();
                            }
                        }
                        contactsAdapter.notifyDataSetChanged();
                    }
                });

                viewHolder.vBinding.btnInvite.setOnClickListener(view -> {
                    Utils.preventDoubleClick(view);
                    if (viewHolder.vBinding.btnInvite.getText().toString().equalsIgnoreCase(Utils.getLangValue("resend"))) {
                        requestAddMemberToPlusOne(listModel.getId());
                    } else {
                        ArrayList<String> sendSmsList = new ArrayList<>();
                        sendSmsList.add(listModel.getPhone());
                        Utils.openSmsApp(requireActivity(), sendSmsList);
                    }
                });


            } else {
                TitleHolder viewHolder = (TitleHolder) holder;
                if (TextUtils.isEmpty(searchQuery)) {
                    viewHolder.vBinding.titleText.setVisibility(VISIBLE);
                    viewHolder.vBinding.titleText.setText(listModel.getFirstName());
                    viewHolder.vBinding.tvInvite.setVisibility(View.GONE);
                } else {
                    viewHolder.vBinding.titleText.setVisibility(View.GONE);
                    viewHolder.vBinding.tvInvite.setVisibility(View.GONE);
                }


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
            public final ItemMyPlusOneMemberBinding vBinding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                vBinding = ItemMyPlusOneMemberBinding.bind(itemView);
            }
        }

        public class TitleHolder extends RecyclerView.ViewHolder {
            public final ItemSectionHeaderBinding vBinding;

            public TitleHolder(@NonNull View itemView) {
                super(itemView);
                vBinding = ItemSectionHeaderBinding.bind(itemView);
                vBinding.titleText.setTextColor(ContextCompat.getColor(requireActivity(), R.color.white));

            }
        }

    }

    private class MyPlusGroupListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_my_plus_one_member));

        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            UserDetailModel model = (UserDetailModel) getItem(position);
            if (model == null) {
                return;
            }

            viewHolder.binding.ivCheck.setVisibility(View.VISIBLE);
            Utils.hideViews(viewHolder.binding.viewLine,viewHolder.binding.tvUserStatus,viewHolder.binding.btnInvite,viewHolder.binding.tvNotEligible);

            Graphics.loadImageWithFirstLetter(model.getImage(), viewHolder.binding.ivUserProfile, model.getFirstName());
            viewHolder.binding.tvUserName.setText(model.getFullName());

            if (model.getBio() != null && !model.getBio().isEmpty()) {
                viewHolder.binding.tvBio.setText(model.getBio());
                viewHolder.binding.tvBio.setVisibility(View.VISIBLE);
            } else {
                viewHolder.binding.tvBio.setVisibility(View.GONE);
            }


            viewHolder.binding.ivCheck.setOnCheckedChangeListener(null);

            boolean isSelected = selectedContacts.contains(model);
            viewHolder.binding.ivCheck.setChecked(isSelected);
            viewHolder.binding.ivCheck.setButtonDrawable(isSelected ? R.drawable.check_box_select_unselect_owner : R.drawable.complete_icon_unselected);

            viewHolder.binding.contactLayout.setCornerRadius(0, CornerType.ALL);
            boolean isFirstCell = false;
            boolean isLastCell = getItemCount() - 1 == position;

            if (position == 0) {
                isFirstCell = true;
            }

            float cornerRadius = activity.getResources().getDimensionPixelSize(com.intuit.sdp.R.dimen._10sdp);
            if (isFirstCell) {
                viewHolder.binding.contactLayout.setCornerRadius(cornerRadius, CornerType.TOP_LEFT);
                viewHolder.binding.contactLayout.setCornerRadius(cornerRadius, CornerType.TOP_RIGHT);
            }

            if (isLastCell) {
                viewHolder.binding.contactLayout.setCornerRadius(cornerRadius, CornerType.BOTTOM_RIGHT);
                viewHolder.binding.contactLayout.setCornerRadius(cornerRadius, CornerType.BOTTOM_LEFT);
            }

            viewHolder.binding.viewLine.setVisibility(isLastCell ? View.GONE : View.VISIBLE);

            viewHolder.binding.ivCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedContacts.add(model);
                } else {
                    selectedContacts.remove(model);
                }
                notifyDataSetChanged();
            });

            viewHolder.binding.getRoot().setOnClickListener(v -> {
                if (selectedContacts.contains(model)) {
                    selectedContacts.remove(model);
                } else {
                    selectedContacts.add(model);
                }
                notifyDataSetChanged();
            });

            boolean isEligible = viewHolder.checkUserEligibleForEvent(model);

            viewHolder.binding.tvNotEligible.setVisibility(isEligible ? View.GONE : View.VISIBLE);
            viewHolder.binding.ivCheck.setVisibility(isEligible ? View.VISIBLE : View.GONE);
            viewHolder.itemView.setClickable(isEligible);
            viewHolder.itemView.setEnabled(isEligible);
            viewHolder.binding.ivUserProfile.setAlpha(isEligible ? 1.0f : 0.5f);
            viewHolder.binding.detailLayout.setAlpha(isEligible ? 1.0f : 0.5f);


        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final ItemMyPlusOneMemberBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemMyPlusOneMemberBinding.bind(itemView);

            }

            private boolean checkUserEligibleForEvent(UserDetailModel user) {
                if (promoterEventModel == null || user == null) return false;
//                int age = 0;
//                if (!TextUtils.isEmpty(user.getDateOfBirth())){
//                    age = Utils.calculateAge(user.getDateOfBirth());
//                    age = Math.max(age, 0);
//                }
                int age = 0; // Initialize age
                if (!TextUtils.isEmpty(user.getDateOfBirth())) {
                    Integer calculatedAge = Utils.calculateAge(user.getDateOfBirth());
                    age = (calculatedAge != null) ? Math.max(calculatedAge, 0) : 0;
                }



                // Check gender condition
                boolean genderCondition = "both".equalsIgnoreCase(promoterEventModel.getExtraGuestGender()) ||
                        promoterEventModel.getExtraGuestGender().equalsIgnoreCase(user.getGender());

                // Check age condition
                boolean ageCondition = false;
                String[] ageRange = promoterEventModel.getExtraGuestAge().split("-");
                if (ageRange.length == 2) {
                    try {
                        int minAge = Integer.parseInt(ageRange[0].trim());
                        int maxAge = Integer.parseInt(ageRange[1].trim());
                        ageCondition = minAge <= age && age <= maxAge;
                    } catch (NumberFormatException e) {
                        ageCondition = false; // Handle invalid age range format
                    }
                }

                // Check nationality condition
                boolean nationalityCondition = "Not Specified".equalsIgnoreCase(promoterEventModel.getExtraGuestNationality()) ||
                        promoterEventModel.getExtraGuestNationality().equalsIgnoreCase(user.getNationality());

                // Combine all conditions
                return genderCondition &&
                        ("anyone".equalsIgnoreCase(promoterEventModel.getExtraGuestType()) || (ageCondition && nationalityCondition));
            }

        }
    }


    // endregion
    // --------------------------------------


}