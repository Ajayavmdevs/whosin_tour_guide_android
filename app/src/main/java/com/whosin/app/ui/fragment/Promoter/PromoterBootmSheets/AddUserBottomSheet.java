package com.whosin.app.ui.fragment.Promoter.PromoterBootmSheets;

import static com.whosin.app.comman.AppDelegate.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.whosin.app.databinding.AddUserBottomSheetBinding;
import com.whosin.app.databinding.InviteContactFreindItemBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.models.ContainerListModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.rest.RestCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AddUserBottomSheet extends DialogFragment {

    private AddUserBottomSheetBinding binding;
    private final MyCirclesDetailAdapter<UserDetailModel> myCirclesDetailAdapter = new MyCirclesDetailAdapter<>();
    private List<UserDetailModel> searchingList = new ArrayList<>();
    private CommanCallback<List<UserDetailModel>> listener;
    public List<String> selectedUserId = new ArrayList<>();
    public List<String> alreadySelectedMemberId = new ArrayList<>();
    private String searchQuery = "";
    private final Runnable runnable = () -> filterAndDisplay();

    public CommanCallback<UserDetailModel> callback;
    private final Handler handler = new Handler();


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutRes(), container, false);
        initUi(view);
        setListener();
        return view;


    }

    private void initUi(View v) {
        binding = AddUserBottomSheetBinding.bind(v);

        binding.tvBucketTitle.setText(Utils.getLangValue("add_user"));
        binding.edtSearch.setHint(Utils.getLangValue("find_friends"));
        binding.emptyPlaceHolderView.setEmptyPlaceTxtTitle(Utils.getLangValue("your_friends_list"));
        binding.btnInvite.setTxtTitle(Utils.getLangValue("invite"));

        binding.addUserRecycler.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false));
        binding.addUserRecycler.setAdapter(myCirclesDetailAdapter);
        requestPromoterMyRingMember();
    }
    private void setListener() {

        binding.btnInvite.setOnClickListener(view -> {
            if (listener != null) {
                if (myCirclesDetailAdapter.getData() != null && !myCirclesDetailAdapter.getData().isEmpty()) {
                    List<UserDetailModel> selectedRingUsers = myCirclesDetailAdapter.getData().stream().filter(UserDetailModel::isRingUserSelect).collect(Collectors.toList());
                    if (!alreadySelectedMemberId.isEmpty()){
                        selectedRingUsers.removeIf(p -> alreadySelectedMemberId.contains(p.getUserId()));
                    }

                    if (selectedRingUsers.isEmpty()) {
                        listener.onReceive(new ArrayList<>());
                    } else {
                        listener.onReceive(selectedRingUsers);
                    }
                }
            }
            dismiss();
        });

        binding.ivClose.setOnClickListener(view -> dismiss());

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
        return R.layout.add_user_bottom_sheet;
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

    @Override
    public void onResume() {
        super.onResume();
    }


    public void setShareListener(CommanCallback<List<UserDetailModel>> listener) {
        this.listener = listener;
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void filterAndDisplay() {

        List<UserDetailModel> filteredList = new ArrayList<>();

        if (Utils.isNullOrEmpty(searchQuery)) {
            filteredList.addAll(searchingList);
        }else {
            filteredList = myCirclesDetailAdapter.getData().stream().filter(model -> model.getFullName().trim().toLowerCase().contains(searchQuery.toLowerCase().trim())).collect(Collectors.toList());
        }

        myCirclesDetailAdapter.updateData(filteredList);
    }


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestPromoterMyRingMember() {
        Graphics.showProgress(requireActivity());
        DataService.shared(activity).requestPromoterMyRingMember(new RestCallback<ContainerListModel<UserDetailModel>>(this) {
            @Override
            public void result(ContainerListModel<UserDetailModel> model, String error) {
                Graphics.hideProgress(requireActivity());
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (model.data != null && !model.data.isEmpty()) {
                    if (!selectedUserId.isEmpty()) {
                        model.data.stream().filter(p -> selectedUserId.contains(p.getUserId())).forEach(p -> p.setRingUserSelect(true));
                    }
                    searchingList.addAll(model.data);
                    if (!alreadySelectedMemberId.isEmpty()){
                        model.data.stream().filter(p -> alreadySelectedMemberId.contains(p.getUserId())).forEach(p -> p.setRingUserSelect(true));
                    }
                    myCirclesDetailAdapter.updateData(model.data);
                } else {
                    binding.addUserRecycler.setVisibility(View.GONE);
                    binding.btnInvite.setVisibility(View.GONE);
                    binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    private class MyCirclesDetailAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.invite_contact_freind_item));

        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            UserDetailModel model = (UserDetailModel) getItem(position);
            if (model == null){return;}

            viewHolder.binding.view.setVisibility(View.GONE);
            viewHolder.binding.tvUserNumber.setVisibility(View.GONE);
            viewHolder.binding.ivCheck.setVisibility(View.VISIBLE);

            Graphics.loadImageWithFirstLetter(model.getImage(), viewHolder.binding.ivUserProfile, model.getFirstName());
            viewHolder.binding.tvUserName.setText(model.getFullName());

            if (model.getBio() != null && !model.getBio().isEmpty()) {
                viewHolder.binding.tvContactBookName.setText(model.getBio());
                viewHolder.binding.tvContactBookName.setVisibility(View.VISIBLE);
            } else {
                viewHolder.binding.tvContactBookName.setVisibility(View.GONE);
            }

            viewHolder.binding.ivCheck.setOnCheckedChangeListener(null);

            viewHolder.binding.ivCheck.setChecked(model.isRingUserSelect());
            viewHolder.binding.ivCheck.setPadding(model.isRingUserSelect() ? 0 : 4, 0, 0, 0);
            viewHolder.binding.ivCheck.setButtonDrawable(model.isRingUserSelect() ? R.drawable.check_box_select_unselect_owner : R.drawable.complete_icon_unselected);



            viewHolder.binding.constrain.setCornerRadius(0, CornerType.ALL);
            boolean isFirstCell = false;
            boolean isLastCell = getItemCount() - 1 == position;

            if (position == 0) {
                isFirstCell = true;
            }

            float cornerRadius = activity.getResources().getDimensionPixelSize(com.intuit.sdp.R.dimen._10sdp);
            if (isFirstCell) {
                viewHolder.binding.constrain.setCornerRadius(cornerRadius, CornerType.TOP_LEFT);
                viewHolder.binding.constrain.setCornerRadius(cornerRadius, CornerType.TOP_RIGHT);
            }
            if (isLastCell) {
                viewHolder.binding.constrain.setCornerRadius(cornerRadius, CornerType.BOTTOM_RIGHT);
                viewHolder.binding.constrain.setCornerRadius(cornerRadius, CornerType.BOTTOM_LEFT);
            }
            viewHolder.binding.view.setVisibility(isLastCell ? View.GONE : View.VISIBLE);

            viewHolder.binding.getRoot().setOnClickListener(v -> {
                if (!alreadySelectedMemberId.isEmpty() && alreadySelectedMemberId.contains(model.getUserId())){return;}
                boolean isSelected = !model.isRingUserSelect();
                model.setRingUserSelect(isSelected);
                viewHolder.binding.ivCheck.setChecked(isSelected);
                notifyDataSetChanged();
            });

            viewHolder.binding.ivCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (!alreadySelectedMemberId.isEmpty() && alreadySelectedMemberId.contains(model.getUserId())){return;}
                model.setRingUserSelect(isChecked);
                notifyDataSetChanged();
            });


        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final InviteContactFreindItemBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = InviteContactFreindItemBinding.bind(itemView);

            }
        }
    }

    // endregion
    // --------------------------------------
}