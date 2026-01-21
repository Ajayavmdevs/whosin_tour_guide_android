package com.whosin.app.ui.activites.bucket;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.whosin.app.R;
import com.whosin.app.comman.AppExecutors;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.ActivityMyInvitationBinding;
import com.whosin.app.databinding.FrindListItemBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.ChatModel;
import com.whosin.app.service.models.CommanMsgModel;
import com.whosin.app.service.models.CommonModel;
import com.whosin.app.service.models.ContactListModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.InviteFriendModel;
import com.whosin.app.service.models.PackageModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.models.VenueObjectModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.comman.BaseActivity;
import com.whosin.app.ui.activites.home.Chat.ChatMessageActivity;
import com.whosin.app.ui.activites.home.event.InviteGuestListBottomSheet;
import com.whosin.app.ui.activites.offers.OfferDetailBottomSheet;
import com.whosin.app.ui.adapter.OfferPackagesAdapter;
import com.whosin.app.ui.fragment.home.InviteFriendBottomSheet;

import java.util.ArrayList;
import java.util.List;

public class MyInvitationActivity extends BaseActivity {

    private ActivityMyInvitationBinding binding;
    private InviteFriendModel inviteFriendModel;
    private final FriendListAdapter<ContactListModel> friendListAdapter = new FriendListAdapter<>();
    private final OfferPackagesAdapter<PackageModel> subAdapter = new OfferPackagesAdapter<>();
    String notificationType = "";
    String outingId ="";
    String invitedId="";

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    protected void initUi() {

        outingId = getIntent().getStringExtra( "id" );
        notificationType = getIntent().getStringExtra( "notificationType" );
        Log.d("TAG", "initUi: "+outingId);
        binding.friendRecycler.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        binding.friendRecycler.setAdapter(friendListAdapter);

        Graphics.applyBlurEffect(activity, binding.blurViewHeader);

        String model = getIntent().getStringExtra("myInvitation");
        if (!TextUtils.isEmpty(model)) {
            inviteFriendModel = new Gson().fromJson(model, InviteFriendModel.class);
            setDetail();
        }
    }


    @Override
    protected void setListeners() {
        binding.ivClose.setOnClickListener(view ->finish());

        binding.layoutEdit.setOnClickListener(view -> {
            Utils.preventDoubleClick( view );

            InviteFriendBottomSheet inviteFriendDialog = new InviteFriendBottomSheet();
            inviteFriendDialog.inviteFriendModel = inviteFriendModel;
            inviteFriendDialog.setShareListener(data -> {});
            inviteFriendDialog.callback = data -> {
                    finish();
                    startActivity( getIntent() );

            };
            inviteFriendDialog.show(getSupportFragmentManager(), "1");
        });

        binding.btnImOut.setOnClickListener(v -> requestUpdateInviteStatus("out"));

        binding.btnLetsGo.setOnClickListener(v -> requestUpdateInviteStatus("in"));

        binding.btnDeletePermanently.setOnClickListener(v -> {
            Graphics.showAlertDialogWithOkCancel( activity, getString(R.string.app_name), "Are you sure want to delete invitation ?", aBoolean -> {
                if (aBoolean) {
                    requestDeleteInvitation(invitedId);
                }
            });
        });



        binding.btnCancelInvitation.setOnClickListener(v -> {
            Utils.preventDoubleClick( v );
            ArrayList<String> data = new ArrayList<>();
            data.add("Change Ownership");
            data.add("Cancel Invitation");
            data.add("Delete Invitation");

            Graphics.showActionSheet(activity, getString(R.string.app_name), data, (data1, position1) -> {
                if (position1 == 0) {
                    showTransferOwnership();
                } else if (position1 == 1) {
                    requestUpdateOuting();
                }else {
                    requestOutingOwnerDelete();
                }
            });
        });

        binding.chatBtnOffer.setOnClickListener(v -> {
            ChatModel chatModel = new ChatModel(inviteFriendModel);
            Intent intent = new Intent(activity, ChatMessageActivity.class);
            intent.putExtra("chatModel", new Gson().toJson(chatModel));
            startActivity(intent);
        });

        binding.tvSeelAl.setOnClickListener(view -> {
            Utils.preventDoubleClick( view );

            InviteGuestListBottomSheet inviteGuestListBottomSheet = new InviteGuestListBottomSheet();
            inviteGuestListBottomSheet.model = inviteFriendModel.getInvitedUser();
            inviteGuestListBottomSheet.type = "outing";
            inviteGuestListBottomSheet.show(getSupportFragmentManager(), "");
        });

        binding.offerDetail.setOnClickListener(v -> {
            if (inviteFriendModel == null) { return; }
            if (inviteFriendModel.getOffersModel() == null) { return; }
            OfferDetailBottomSheet dialog = new OfferDetailBottomSheet();
            dialog.offerId = inviteFriendModel.getOffersModel().getId();
            dialog.show(getSupportFragmentManager(), "");
        });

        binding.ivMenu.setOnClickListener(view -> {
            Utils.preventDoubleClick( view );
            ArrayList<String> data = new ArrayList<>();
            data.add("Change Ownership");
            data.add("Delete Invitation");
            Graphics.showActionSheet(activity, getString(R.string.app_name), data, (data1, position1) -> {
                if (position1 == 0) {
                    showTransferOwnership();
                }else {
                    requestOutingOwnerDelete();
                }
            });

        });
    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityMyInvitationBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        requestOutingDetail(inviteFriendModel == null);
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void showTransferOwnership() {
        TransferOwnershipBottomSheet transferOwnershipBottomSheet = new TransferOwnershipBottomSheet();
        transferOwnershipBottomSheet.inviteFriendModel = inviteFriendModel;
        transferOwnershipBottomSheet.callback = (success, error) -> {
            if (success) {
                requestOutingDetail(false);
            }else {
                binding.ivMenu.setVisibility(View.VISIBLE);
                binding.layoutEdit.setVisibility(View.VISIBLE);
            }
        };
        transferOwnershipBottomSheet.show(getSupportFragmentManager(), "1");
    }

    private void setVenueDetail(VenueObjectModel venueDetail) {
        if (venueDetail != null) {
            binding.layoutDetail.setVisibility(View.VISIBLE);
            binding.layoutImage.setVisibility(View.VISIBLE);
            binding.offerDetail.setVisibility(View.GONE);
            binding.chatBtnOffer.setVisibility(View.VISIBLE);
            setVenueDetails(binding.txtFeatures, venueDetail.getFeature(), binding.ilFeatures);
            setVenueDetails(binding.txtCuisine, venueDetail.getCuisine(), binding.ilCuisine);
            setVenueDetails(binding.tvMusic, venueDetail.getMusic(), binding.ilMusic);

            Graphics.loadImage(venueDetail.getCover(), binding.ivCover);
            if (inviteFriendModel.isOwnerOfOuting()) {
                binding.userVenueContainer.setVenueDetail(venueDetail);
            } else {
                AppExecutors.get().mainThread().execute(() -> binding.venueContainer.setVenueDetail(venueDetail));
            }
            binding.tvAbout.setText(inviteFriendModel.getVenue().getAbout());
        }
    }

    private void setUpOfferData() {
        if (inviteFriendModel.getOffersModel() != null) {
            binding.layoutDetail.setVisibility(View.GONE);
            binding.layoutImage.setVisibility(View.GONE);
            binding.offerDetail.setVisibility(View.VISIBLE);
            binding.chatBtnOffer.setVisibility(View.VISIBLE);
            binding.txtTitle.setText(inviteFriendModel.getOffersModel().getTitle());
            binding.tvDescription.setText(inviteFriendModel.getOffersModel().getDescription());

            AppExecutors.get().mainThread().execute(() -> binding.offerVenueContainer.setVenueDetail(inviteFriendModel.getVenue()));

            binding.offerInfoView.setOfferDetail(inviteFriendModel.getOffersModel(), MyInvitationActivity.this, getSupportFragmentManager());
            binding.txtOfferStatus.setText(inviteFriendModel.getStatus());

            binding.packageRecycler.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
            binding.packageRecycler.setAdapter(subAdapter);
            if (!inviteFriendModel.getOffersModel().getPackages().isEmpty()) {
                subAdapter.updateData(inviteFriendModel.getOffersModel().getPackages());
            }
        }

    }

    private void setDetail() {
        binding.btnLetsGo.setVisibility(View.GONE);
        binding.btnImOut.setVisibility(View.GONE);
        binding.btnCancelInvitation.setVisibility(View.GONE);
        binding.txtOutingStatus.setVisibility(View.GONE);

        if (inviteFriendModel != null) {

            binding.layoutEdit.setVisibility( inviteFriendModel.isAllowEdit() ? View.VISIBLE : View.GONE);
            binding.venueContainer.setVisibility(inviteFriendModel.isOwnerOfOuting() ? View.GONE : View.VISIBLE);
            binding.layoutUser.setVisibility(inviteFriendModel.isOwnerOfOuting() ? View.VISIBLE : View.GONE);
            binding.roundLinearUser.setVisibility(!inviteFriendModel.isOwnerOfOuting() ? View.VISIBLE : View.GONE);
            binding.layout.setVisibility(!inviteFriendModel.isOwnerOfOuting() ? View.VISIBLE : View.GONE);
            setVenueDetail(inviteFriendModel.getVenue());

            if (inviteFriendModel.getOffersModel() != null) {
                setUpOfferData();
            }

            binding.tvOutingTitle.setText(inviteFriendModel.getTitle());
            friendListAdapter.updateData(inviteFriendModel.getInvitedUser());

            Graphics.applyBlurEffect(activity, binding.blurView);
            binding.txtExtraGuest.setText(String.valueOf(inviteFriendModel.getExtraGuest()));
            binding.txtDate.setText(Utils.convertDateFormat(inviteFriendModel.getDate(), "yyyy-MM-dd"));
            binding.txtTime.setText(String.format("%s - %s", Utils.convert24HourTimeFormat(inviteFriendModel.getStartTime()), Utils.convert24HourTimeFormat(inviteFriendModel.getEndTime())));

            if (inviteFriendModel.getUser() != null) {
                binding.txtOutingTitle.setText(String.format("%s %s", inviteFriendModel.getUser().getFirstName(), inviteFriendModel.getUser().getLastName()));
                binding.txtOutingDescribe.setText("invited you to");
                Graphics.loadImageWithFirstLetter(inviteFriendModel.getUser().getImage(), binding.imgLogo, inviteFriendModel.getUser().getFirstName());
            }

            binding.txtStatus.setText(inviteFriendModel.getStatus());
            if (inviteFriendModel.getStatus().equals("completed")) {
                binding.txtOutingStatus.setText("Completed");
                binding.txtOutingStatus.setTextColor(getColor(R.color.green));
                binding.txtOutingStatus.setVisibility(View.VISIBLE);
                binding.chatBtnOffer.setVisibility(View.GONE);
            }
            else if (inviteFriendModel.getStatus().equals("cancelled")) {
                binding.txtOutingStatus.setText("Cancelled");
                binding.txtOutingStatus.setTextColor(getColor(R.color.redColor));
                binding.txtOutingStatus.setVisibility(View.VISIBLE);
                binding.chatBtnOffer.setVisibility(View.GONE);
            }
            else if (inviteFriendModel.isOwnerOfOuting()) {
                binding.btnCancelInvitation.setVisibility(View.VISIBLE);
                binding.chatBtnOffer.setVisibility(View.VISIBLE);
            }
            else {
                binding.chatBtnOffer.setVisibility(View.VISIBLE);
                ContactListModel invitedUser = inviteFriendModel.getInvitedUser().stream().filter(model1 -> model1.getUserId().equals(SessionManager.shared.getUser().getId())).findFirst().orElse(null);

                if (invitedUser != null) {
                    invitedId = invitedUser.getInviteId();
                    Log.d("TAG", "requestDeleteInvitation: "+invitedId);
                    Log.d("MyInvitationActivity", "setDetail: " + invitedUser.getInviteStatus());
                    binding.btnLetsGo.setVisibility(View.VISIBLE);
                    binding.btnImOut.setVisibility(View.VISIBLE);

                    if (invitedUser.getInviteStatus().equals("out")) {
                        binding.btnImOut.setVisibility(View.GONE);
                        binding.btnDeletePermanently.setVisibility(View.VISIBLE);
                    }
                    else if (invitedUser.getInviteStatus().equals("in")) {
                        binding.btnLetsGo.setVisibility(View.GONE);
                        binding.btnDeletePermanently.setVisibility(View.GONE);
                        binding.btnImOut.setTxtTitle("Cancel");
                    }
                    updateInviteStatusUI(invitedUser.getInviteStatus());
                }
            }
        }
    }

    private void setVenueDetails(TextView textView, List<String> data, View viewToHide) {
        if (data != null && !data.isEmpty()) {
            textView.setText(TextUtils.join(", ", data));
        } else {
            viewToHide.setVisibility(View.GONE);
        }
    }


    private void updateInviteStatusUI(String status) {
        int backgroundColor = 0;
        switch (status) {
            case "pending":
                backgroundColor = ContextCompat.getColor(getApplicationContext(), R.color.pending_yellow);
                break;
            case "in":
                backgroundColor = ContextCompat.getColor(getApplicationContext(), R.color.in_green);
                break;
            default:
                backgroundColor = ContextCompat.getColor(getApplicationContext(), R.color.out_red);
                break;
        }

        if (inviteFriendModel.getOffersModel() != null) {
            binding.statusOfferLayout.setBackgroundColor(backgroundColor);
        } else {
            binding.layoutStatus.setBackgroundColor(backgroundColor);

        }
    }


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestUpdateInviteStatus(String status) {
        showProgress();
        DataService.shared(activity).requestUpdateInviteStatus(inviteFriendModel.getId(), status, new RestCallback<ContainerModel<InviteFriendModel>>(this) {
            @Override
            public void result(ContainerModel<InviteFriendModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }
                requestOutingDetail(false);
                Toast.makeText(activity, model.message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void requestDeleteInvitation(String outingId) {

        showProgress();
        DataService.shared(activity).requestDeleteInvitation(outingId, new RestCallback<ContainerModel<UserDetailModel>>(this) {
            @Override
            public void result(ContainerModel<UserDetailModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra("close",true);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void requestOutingDetail(boolean isLoader) {
        String  id;
        if (notificationType != null && !notificationType.isEmpty() && notificationType.equals("notification")) {
            id = outingId;
        } else {
            id = inviteFriendModel.getId();
        }
        if(isLoader){
            showProgress();

        }
        DataService.shared(activity).requestOutingDetail(id, new RestCallback<ContainerModel<InviteFriendModel>>(this) {
            @Override
            public void result(ContainerModel<InviteFriendModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }
                inviteFriendModel = model.getData();
                AppExecutors.get().mainThread().execute(() -> setDetail());
                binding.scroll.setVisibility( View.VISIBLE );
            }
        });
    }

    private void requestUpdateOuting() {
        showProgress();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("status", "cancelled");
        jsonObject.addProperty("outingId", inviteFriendModel.getId());
        DataService.shared(activity).requestUpdateOuting(jsonObject, new RestCallback<ContainerModel<InviteFriendModel>>() {
            @Override
            public void result(ContainerModel<InviteFriendModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }
                requestOutingDetail(false);
                Toast.makeText(activity, model.message, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void requestOutingOwnerDelete(){
        showProgress();
        DataService.shared( activity ).requestOutingOwnerDelete(inviteFriendModel.getId(), new RestCallback<ContainerModel<InviteFriendModel>>(this) {
            @Override
            public void result(ContainerModel<InviteFriendModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
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

    public class FriendListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.frind_list_item));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            ContactListModel model = (ContactListModel) getItem(position);
            if (SessionManager.shared.getUser().getId().equals(model.getUserId())) {
                viewHolder.mBinding.txtUserName.setText("Me");
            } else {
                viewHolder.mBinding.txtUserName.setText(model.getFirstName());
            }

            if (model.getInviteStatus().equals("pending")) {
                viewHolder.mBinding.iconStatus.setImageResource(R.drawable.icon_pending);
            } else if (model.getInviteStatus().equals("in")) {
                viewHolder.mBinding.iconStatus.setImageResource(R.drawable.icon_complete);
            } else if (model.getInviteStatus().equals("out")) {
                viewHolder.mBinding.iconStatus.setImageResource(R.drawable.icon_deleted);
            }

            Graphics.loadImageWithFirstLetter(model.getImage(), viewHolder.mBinding.imgUserLogo, model.getFirstName());
            viewHolder.itemView.setOnClickListener(v -> {
                Utils.preventDoubleClick( v );
                InviteGuestListBottomSheet inviteGuestListBottomSheet = new InviteGuestListBottomSheet();
                inviteGuestListBottomSheet.model = inviteFriendModel.getInvitedUser();
                inviteGuestListBottomSheet.type = "outing";
                inviteGuestListBottomSheet.show(getSupportFragmentManager(), "");            });
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final FrindListItemBinding mBinding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = FrindListItemBinding.bind(itemView);
            }
        }
    }

    // endregion

}