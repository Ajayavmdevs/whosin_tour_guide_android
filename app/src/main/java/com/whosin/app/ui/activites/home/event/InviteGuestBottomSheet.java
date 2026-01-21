package com.whosin.app.ui.activites.home.event;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.JsonObject;
import com.ncorti.slidetoact.SlideToActView;
import com.whosin.app.R;
import com.whosin.app.comman.AppExecutors;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.BucketListItemBinding;
import com.whosin.app.databinding.ImageListBinding;
import com.whosin.app.databinding.InviteGuestBottomSheetBinding;
import com.whosin.app.databinding.ItemSelectGuesetBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.models.BucketListModel;
import com.whosin.app.service.models.ContactListModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.CreateBucketListModel;
import com.whosin.app.service.models.EventDetailModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.venue.Bucket.ContactShareBottomSheet;
import com.whosin.app.ui.activites.venue.Bucket.CreateBucketListBottomDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class InviteGuestBottomSheet extends DialogFragment implements SlideToActView.OnSlideCompleteListener {
    private InviteGuestBottomSheetBinding binding;
    public CommanCallback<String> listener;
    private List<ContactListModel> selectedUsers = new ArrayList<>();
    private final SelectContactAdapter<ContactListModel> contactAdapter = new SelectContactAdapter<>();
    public String eventId;
    private int selectGuest;

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
        binding = InviteGuestBottomSheetBinding.bind(view);

        binding.tvBucketTitle.setText(Utils.getLangValue("we_are_excited_to_see"));
        binding.inviteTv.setText(Utils.getLangValue("invite_your_friends"));
        binding.tvSomeExtraGuest.setText(Utils.getLangValue("have_some_extra_guest"));
        binding.tvConfirmTitle.setText(Utils.getLangValue("confirm"));

        binding.contactRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.contactRecycler.setAdapter(contactAdapter);
        binding.contactRecycler.setNestedScrollingEnabled(false);
        contactAdapter.updateData(selectedUsers);
    }

    public void setListener() {
        binding.confirmGuest.setOnClickListener(v -> {
            requestEventGuestList();
        });


        binding.tvContact.setOnClickListener(view -> {
            Utils.preventDoubleClick( view );
            ContactShareBottomSheet contactDialog = new ContactShareBottomSheet();
            contactDialog.defaultUsersList = selectedUsers.stream().map(ContactListModel::getId).collect(Collectors.toList());
            contactDialog.setShareListener(data -> {
                selectedUsers = data;
                AppExecutors.get().mainThread().execute(() -> {
                    contactAdapter.updateData(selectedUsers);
                    contactAdapter.notifyDataSetChanged();
                });

            });
            contactDialog.show(getChildFragmentManager(), "1");
        });

        binding.ivPlus.setOnClickListener(view -> {
            selectGuest++;
            binding.tvTotal.setText(String.valueOf(selectGuest));
        });

        binding.ivMinus.setOnClickListener(view -> {
            if (selectGuest == 0) {
                Toast.makeText(requireActivity(), Utils.getLangValue("please_select_guest"), Toast.LENGTH_SHORT).show();
            } else {
                selectGuest--;
                binding.tvTotal.setText(String.valueOf(selectGuest));
            }
        });
    }


    public int getLayoutRes() {
        return R.layout.invite_guest_bottom_sheet;
    }

    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new BottomSheetDialog(requireActivity(), R.style.BottomSheetDialogThemeNoFloating);
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

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    public void onSlideComplete(@NonNull SlideToActView slideToActView) {

        AppExecutors.get().networkIO().execute(() -> {
            requestEventGuestList();
        });
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
    // region Data/Service
    // --------------------------------------

    private void requestEventGuestList() {

        binding.progress.setVisibility(View.VISIBLE);
        List<String> users = selectedUsers.stream().map(ContactListModel::getId).collect(Collectors.toList());
        Log.d("TAG", "requestEventGuestList: "+eventId+" "+users);
        DataService.shared(requireActivity()).requestEventInviteGuest(eventId, users, selectGuest, new RestCallback<ContainerModel<EventDetailModel>>(this) {
            @Override
            public void result(ContainerModel<EventDetailModel> model, String error) {
                binding.progress.setVisibility(View.GONE);
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(requireActivity(), error, Toast.LENGTH_SHORT).show();
                    return;
                }
                model.getData();
                requestEventInviteStatus(eventId, "in");
            }
        });
    }

    private void requestEventInviteStatus(String eventId, String inviteStatus) {
        DataService.shared(getActivity()).requestEventInviteStatus(eventId, inviteStatus, new RestCallback<ContainerModel<EventDetailModel>>(this) {
            @Override
            public void result(ContainerModel<EventDetailModel> model, String error) {
                binding.progress.setVisibility(View.GONE);
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (listener != null) {
                    listener.onReceive("in");
                }
                Toast.makeText(getActivity(), model.message, Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
    }
    

    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------


    public static class SelectContactAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = UiUtils.getViewBy(parent, R.layout.item_select_gueset);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) view.getLayoutParams();
            layoutParams.rightMargin = view.getContext().getResources().getDimensionPixelSize(com.intuit.sdp.R.dimen._minus8sdp);
            if (viewType == 0) {
                layoutParams.width = view.getContext().getResources().getDimensionPixelSize(com.intuit.sdp.R.dimen._48sdp);
            }
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            ContactListModel model = (ContactListModel) getItem(position);
            Graphics.loadImageWithFirstLetter(model.getImage(), viewHolder.binding.guestImage, model.getFirstName());
        }

        public int getItemViewType(int position) {
            if (getItemCount() == position + 1) {
                return 0;
            } else {
                return 1;
            }
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            private final ItemSelectGuesetBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemSelectGuesetBinding.bind(itemView);
            }
        }
    }




    public static class PlayerAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public PlayerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = UiUtils.getViewBy(parent, R.layout.image_list);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) view.getLayoutParams();
            layoutParams.rightMargin = view.getContext().getResources().getDimensionPixelSize(com.intuit.sdp.R.dimen._minus8sdp);
            if (viewType == 0) {
                layoutParams.width = view.getContext().getResources().getDimensionPixelSize(com.intuit.sdp.R.dimen._48sdp);
            }
            return new PlayerAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            ContactListModel model = (ContactListModel) getItem(position);
            Graphics.loadImageWithFirstLetter(model.getImage(), viewHolder.vBinding.civPlayers, model.getFirstName());


        }

        @Override
        public int getItemViewType(int position) {
            if (getItemCount() == position + 1) {
                return 0;
            } else {
                return 1;
            }
        }


        public static class ViewHolder extends RecyclerView.ViewHolder {
            private final ImageListBinding vBinding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                vBinding = ImageListBinding.bind(itemView);
            }
        }

    }


}