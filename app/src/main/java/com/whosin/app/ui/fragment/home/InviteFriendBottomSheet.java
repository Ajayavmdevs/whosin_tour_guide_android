package com.whosin.app.ui.fragment.home;

import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ncorti.slidetoact.SlideToActView;
import com.whosin.app.R;
import com.whosin.app.comman.AppExecutors;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.HorizontalSpaceItemDecoration;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.InviteFriendBottomSheetBinding;
import com.whosin.app.databinding.ItemSelectContactBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.models.BrunchListModel;
import com.whosin.app.service.models.ContactListModel;
import com.whosin.app.service.models.ContainerListModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.InviteFriendModel;
import com.whosin.app.service.models.OffersModel;
import com.whosin.app.service.models.TimeSlotModel;
import com.whosin.app.service.models.VenueObjectModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.offers.SelectOfferBottomSheet;
import com.whosin.app.ui.activites.venue.Bucket.ContactShareBottomSheet;
import com.whosin.app.ui.fragment.ProfileFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class InviteFriendBottomSheet extends DialogFragment implements SlideToActView.OnSlideCompleteListener {

    private InviteFriendBottomSheetBinding binding;
    private CommanCallback<List<BrunchListModel>> listener;
    public TimeSlotModel timeSlotModel;
    public List<OffersModel> offerList = new ArrayList<>();
    public VenueObjectModel venueObjectModel;
    public OffersModel offersModel;
    private final SelectContactAdapter<ContactListModel> contactAdapter = new SelectContactAdapter<>();
    public InviteFriendModel inviteFriendModel;
    public List<ContactListModel> defaultUserList;
    public CommanCallback<InviteFriendModel> callback;

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

    @Override
    public void onResume() {
        super.onResume();
        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes(params);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ((BottomSheetDialog) getDialog()).getBehavior().setState(STATE_EXPANDED);
    }

    public void initUi(View view) {
        binding = InviteFriendBottomSheetBinding.bind(view);
        if (venueObjectModel != null) {
            if (offersModel == null) {
                reqOfferDetails(venueObjectModel.getId());
            }
        }
//        Glide.with(requireActivity()).load(R.drawable.icon_close_btn).into(binding.ivClose);
        binding.contactRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        int spacing = getResources().getDimensionPixelSize(com.intuit.ssp.R.dimen._5ssp);
        binding.contactRecycler.addItemDecoration(new HorizontalSpaceItemDecoration(spacing));
        binding.contactRecycler.setAdapter(contactAdapter);
        if (defaultUserList != null && !defaultUserList.isEmpty()) {
            contactAdapter.updateData(defaultUserList);
        }
        setDetails();
    }


    public void setListener() {

        binding.ivClose.setOnClickListener(view -> dismiss());

        binding.layoutInvite.setOnClickListener(view -> {
            Utils.preventDoubleClick(view);
            openContactBottomSheet();
        });


        binding.ivPlus.setOnClickListener(view -> {
            int qty = Integer.parseInt(binding.tvQty.getText().toString());
            qty++;
            binding.tvQty.setText(String.valueOf(qty));
        });

        binding.ivMinus.setOnClickListener(view -> {
            int qty = Integer.parseInt(binding.tvQty.getText().toString());
            if (qty > 0) {
                qty--;
                binding.tvQty.setText(String.valueOf(qty));
            }

        });

        binding.txtDateTime.setOnClickListener(v -> {
            Utils.preventDoubleClick(v);

            SelectDateTimeBottomSheet selectDateTimeDialog = new SelectDateTimeBottomSheet();
            selectDateTimeDialog.offersModel = offersModel;
            selectDateTimeDialog.venueObjectModel = venueObjectModel;
            selectDateTimeDialog.setShareListener(data -> {
                if (data != null) {
                    timeSlotModel = data;
                    binding.txtDateTime.setText(String.format("%s (%s - %s)", timeSlotModel.getDate(), timeSlotModel.getStartTime(), timeSlotModel.getEndTime()));
                }
            });
            selectDateTimeDialog.show(getChildFragmentManager(), "1");
        });

        binding.btnInvite.setOnClickListener(view -> {
            Utils.preventDoubleClick(view);

            String title = binding.edMessage.getText().toString().trim();
            String extraGuest = binding.tvQty.getText().toString().trim();

            if (Utils.isNullOrEmpty(title)) {
                Toast.makeText(getContext(), "Please write invitation text", Toast.LENGTH_SHORT).show();
                return;
            }
            if (timeSlotModel == null) {
                Toast.makeText(getContext(), "Please select date", Toast.LENGTH_SHORT).show();
                return;
            }
            if (contactAdapter.getData().isEmpty()) {
                Toast.makeText(getContext(), "Please add friend", Toast.LENGTH_SHORT).show();
                return;
            }
            if (inviteFriendModel != null) {
                requestUpdateOuting(title, extraGuest, inviteFriendModel);
            } else {
                requestCreateOuting(title, extraGuest);
            }
        });

        binding.selectOfferBtn.setOnClickListener(view -> {
            Utils.preventDoubleClick(view);
            openOfferBottomSheet();
        });


        binding.layoutOfferDetail.setOnClickListener(view -> {
            Utils.preventDoubleClick(view);
            openOfferBottomSheet();
        });
    }

    public int getLayoutRes() {
        return R.layout.invite_friend_bottom_sheet;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getView() != null) {
            getView().post(() -> {
                View parent = (View) getView().getParent();
                BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(parent);
                int peekHeight = parent.getHeight();
                behavior.setPeekHeight(peekHeight);
            });
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new BottomSheetDialog(requireActivity(), R.style.BottomSheetDialogThemeNoFloating);
    }

    // endregion
    // --------------------------------------
    // region private
    // --------------------------------------

    private void openContactBottomSheet() {
        ContactShareBottomSheet contactDialog = new ContactShareBottomSheet();
        contactDialog.defaultUsersList = contactAdapter.getData().stream().map(ContactListModel::getId).collect(Collectors.toList());
        contactDialog.setShareListener(data -> {
            AppExecutors.get().mainThread().execute(() -> {
                if (data != null) {
                    contactAdapter.updateData(data);
                    contactAdapter.notifyDataSetChanged();
                }
            });
        });
        contactDialog.show(getChildFragmentManager(), "1");
    }

    private void setupVenueDetail() {
        if (venueObjectModel == null && inviteFriendModel != null) {
            venueObjectModel = inviteFriendModel.getVenue();
        }
        if (venueObjectModel != null) {
            binding.venueContainer.setVenueDetail(venueObjectModel);
//            Graphics.loadImage(venueObjectModel.getCover(), binding.ivCover);
        }
    }

    private void setDetails() {

        setupVenueDetail();
        setOfferDetail();

        if (inviteFriendModel != null) {
            if (inviteFriendModel.getOffersModel() != null) {
                offersModel = inviteFriendModel.getOffersModel();
            }
            binding.edMessage.setText(inviteFriendModel.getTitle());

            binding.tvQty.setText(String.valueOf(inviteFriendModel.getExtraGuest()));
            binding.layoutInvite.setVisibility(View.VISIBLE);
            contactAdapter.updateData(inviteFriendModel.getInvitedUser());

            timeSlotModel = new TimeSlotModel(inviteFriendModel.getStartTime(), inviteFriendModel.getEndTime());
            timeSlotModel.setDate(inviteFriendModel.getDate());

            if (inviteFriendModel.getDate() != null && inviteFriendModel.getStartTime() != null && inviteFriendModel.getEndTime() != null) {
                binding.txtDateTime.setText(Utils.formatDateString(inviteFriendModel.getDate()) + " (" + inviteFriendModel.getStartTime() + " - " + inviteFriendModel.getEndTime() + ")");
            } else {
                binding.txtDateTime.setText("N/A");
            }
            binding.btnInvite.setTxtTitle("Update");
        }

    }

    private void setOfferDetail() {
        if (offersModel == null && inviteFriendModel != null) {
            offersModel = inviteFriendModel.getOffersModel();
        }
        if (offersModel != null) {
            binding.selectOfferBtn.setVisibility(View.GONE);
            binding.layoutOfferDetail.setVisibility(View.VISIBLE);
            binding.txtTitle.setText(offersModel.getTitle());
            binding.tvDescription.setText(offersModel.getDescription());
            Graphics.loadImage(offersModel.getImage(), binding.imgCover);
            binding.txtDays.setText(offersModel.getDays());
            binding.startDate.setText(Utils.convertMainDateFormat(offersModel.getStartTime()));
            binding.endDate.setText(Utils.convertMainDateFormat(offersModel.getEndTime()));
            binding.txtDate.setText(String.format("%s - %s", Utils.convertMainTimeFormat(offersModel.getStartTime()), Utils.convertMainTimeFormat(offersModel.getEndTime())));
        }
    }

    private void openOfferBottomSheet() {
        if (offerList == null) { return; }
        SelectOfferBottomSheet offerBottomSheet = new SelectOfferBottomSheet();
        offerBottomSheet.offerList = offerList;
        offerBottomSheet.callback = data -> {
            if (data != null) {
                timeSlotModel = null;
                binding.txtDateTime.setText("select time");
                offersModel = data;
                setOfferDetail();
            }
        };
        offerBottomSheet.show(getChildFragmentManager(), "SelectOfferBottomSheet");
    }

    public void setShareListener(CommanCallback<List<BrunchListModel>> listener) {
        this.listener = listener;
    }

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestUpdateOuting(String title, String extraGuest, InviteFriendModel inviteFriendModel) {
        JsonArray jsonArray = new JsonArray();
        for (ContactListModel contact : contactAdapter.getData()) {
            jsonArray.add(contact.getId());
        }

        JsonObject object = new JsonObject();
        object.addProperty("outingId", inviteFriendModel.getId());
        object.addProperty("title", title);
        object.addProperty("venueId", inviteFriendModel.getVenueId());
        object.addProperty("date", timeSlotModel.getDate());
        object.addProperty("startTime", timeSlotModel.getStartTime());
        object.addProperty("endTime", timeSlotModel.getEndTime());
        object.addProperty("extraGuest", extraGuest);
        if (offersModel != null) {
            object.addProperty("offerId", offersModel.getId());
        }

        object.add("invitedUser", jsonArray);
        binding.btnInvite.startProgress();
        DataService.shared(getActivity()).requestUpdateOuting(object, new RestCallback<ContainerModel<InviteFriendModel>>() {
            @Override
            public void result(ContainerModel<InviteFriendModel> model, String error) {
                binding.btnInvite.stopProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (model.data != null) {
                    if (callback != null) {
                        callback.onReceive(model.data);
                    }
                }
                dismiss();
            }
        });
    }

    private void requestCreateOuting(String title, String extraGuest) {

        JsonArray jsonArray = new JsonArray();
        for (ContactListModel contact : contactAdapter.getData()) {
            jsonArray.add(contact.getId());
        }
        JsonObject object = new JsonObject();
        object.addProperty("title", title);
        object.addProperty("venueId", venueObjectModel.getId());
        object.addProperty("date", timeSlotModel.getDate());
        object.addProperty("startTime", timeSlotModel.getStartTime());
        object.addProperty("endTime", timeSlotModel.getEndTime());
        object.addProperty("extraGuest", extraGuest);
        if (offersModel != null) {
            object.addProperty("offerId", offersModel.getId());
        }
        object.add("invitedUser", jsonArray);
        binding.btnInvite.startProgress();
        DataService.shared(getActivity()).requestInviteFriend(object, new RestCallback<ContainerModel<InviteFriendModel>>(this) {
            @Override
            public void result(ContainerModel<InviteFriendModel> model, String error) {
                binding.btnInvite.stopProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (model.data != null) {
                    if (callback != null) {
                        callback.onReceive(model.data);
                    }
                }
                Toast.makeText(getContext(), model.message, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(), ProfileFragment.class).putExtra("type", "outing"));
                dismiss();

            }
        });
    }


    private void reqOfferDetails(String venueId) {
        binding.progressBar.setVisibility(View.VISIBLE);
        JsonObject object = new JsonObject();
        object.addProperty("venueId", venueId);
        object.addProperty("page", 1);
        object.addProperty("limit", 100);
        object.addProperty("day", "all");
        DataService.shared(requireActivity()).requestOfferList(object, new RestCallback<ContainerListModel<OffersModel>>(this) {
            @Override
            public void result(ContainerListModel<OffersModel> model, String error) {
                binding.progressBar.setVisibility(View.GONE);
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                    }
                    return;
                }
                if (model.data != null && !model.data.isEmpty()) {
                    offerList = model.data;
                    binding.selectOfferBtn.setEnabled(true);
                    if (getActivity() != null) {
                        binding.selectOfferBtn.setBgColor(ContextCompat.getColor(getActivity(), R.color.brand_pink));
                    }
                    binding.selectOfferBtn.setTintColor(getResources().getColor(R.color.white));
                } else {
                    binding.selectOfferBtn.setEnabled(false);
                }
            }
        });
    }

    @Override
    public void onSlideComplete(@NonNull SlideToActView slideToActView) {
        InviteFriendBottomSheet dialog = new InviteFriendBottomSheet();
        dialog.show(getChildFragmentManager(), "1");
        resetSlider(slideToActView);
    }

    private void resetSlider(SlideToActView slideToActView) {
        Animation animation = new TranslateAnimation(0, 0, 0, 0);
        animation.setDuration(0);
        slideToActView.startAnimation(animation);
        slideToActView.resetSlider();
    }
    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    public static class SelectContactAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = UiUtils.getViewBy(parent, R.layout.item_select_contact);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            ContactListModel model = (ContactListModel) getItem(position);
            if (model != null) {
                String formattedName = model.getFullName().replace(" ", "\n");
                viewHolder.binding.txtName.setText(formattedName);
                Graphics.loadImageWithFirstLetter(model.getImage(), viewHolder.binding.ivContact, model.getFirstName());
            }
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            private final ItemSelectContactBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemSelectContactBinding.bind(itemView);
            }
        }
    }
}