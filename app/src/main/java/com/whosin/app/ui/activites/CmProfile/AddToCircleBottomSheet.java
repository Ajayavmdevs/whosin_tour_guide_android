package com.whosin.app.ui.activites.CmProfile;

import static com.whosin.app.comman.AppDelegate.activity;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

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
import com.whosin.app.databinding.FragmentAddToCircleBottomSheetBinding;
import com.whosin.app.databinding.InviteContactFreindItemBinding;
import com.whosin.app.databinding.ItemNotificationUserListBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.PromoterProfileManager;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.PromoterAddRingModel;
import com.whosin.app.service.models.PromoterCirclesModel;
import com.whosin.app.service.models.PromoterEventModel;
import com.whosin.app.service.rest.RestCallback;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class AddToCircleBottomSheet extends DialogFragment {

    private FragmentAddToCircleBottomSheetBinding binding;

    private final MyCircleAdapter<PromoterCirclesModel> myCircleAdapter = new MyCircleAdapter<>();

    private List<String> selectedCircleIds = new ArrayList<>();

    public List<String> alreadyAddedCircleIds = new ArrayList<>();

    public CommanCallback<String> listener;

    public String userId = "";

    public boolean isUserApprove = false;

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
        binding = FragmentAddToCircleBottomSheetBinding.bind(view);

        binding.userName.setText(Utils.getLangValue("add_to_circle"));
        binding.emptyPlaceHolderView.setEmptyPlaceTxtTitle(Utils.getLangValue("no_users_available"));
        binding.constraintAddToCircle.setTxtTitle(Utils.getLangValue("add"));

        Glide.with(requireActivity()).load(R.drawable.icon_close_btn).into(binding.ivClose);


        binding.circlesRecycler.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        binding.circlesRecycler.setAdapter(myCircleAdapter);

        if (!PromoterProfileManager.shared.promoterProfileModel.getCircles().isEmpty() && PromoterProfileManager.shared.promoterProfileModel.getCircles() != null) {
            if (!alreadyAddedCircleIds.isEmpty()) {
                List<PromoterCirclesModel> tmpList = new ArrayList<>();
                tmpList.addAll(PromoterProfileManager.shared.promoterProfileModel.getCircles());
                tmpList.removeIf(p -> p.getId() != null && alreadyAddedCircleIds.contains(p.getId()));
                if (!tmpList.isEmpty()) {
                    myCircleAdapter.updateData(tmpList);
                    hideAndShow(false);
                } else {
                    hideAndShow(true);
                }
            } else {
                myCircleAdapter.updateData(PromoterProfileManager.shared.promoterProfileModel.getCircles());
            }
        } else {
            hideAndShow(true);
        }

    }

    public void setListener() {

        binding.ivClose.setOnClickListener(view -> {
            Utils.preventDoubleClick(view);
            if (isUserApprove){
                Graphics.showAlertDialogWithOkCancel(requireActivity(), getString(R.string.app_name), Utils.getLangValue("approve_without_adding_to_circle"), Utils.getLangValue("yes"), Utils.getLangValue("no"), isConfirmed -> {
                    if (isConfirmed) {
                        if (listener != null){
                            listener.onReceive("true");
                            dismiss();
                        }
                    }
                });
            }else {
                dismiss();
            }
        });

        binding.constraintAddToCircle.setOnClickListener(view -> {
            if (selectedCircleIds.isEmpty()) {
                Toast.makeText(requireActivity(), Utils.getLangValue("select_circle"), Toast.LENGTH_SHORT).show();
                return;
            }
            if (isUserApprove){
                requestPromoterRingUpdateStatus(userId);
            }else {
                if (TextUtils.isEmpty(userId)) return;
                requestPromoterCircleAddMember();
            }
        });


    }


    public int getLayoutRes() {
        return R.layout.fragment_add_to_circle_bottom_sheet;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = new BottomSheetDialog(requireActivity(), R.style.BottomSheetDialogThemeNoFloating);

        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        dialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
            FrameLayout bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                // Disable drag-to-dismiss
                BottomSheetBehavior<?> behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setDraggable(false);

                // Set the bottom sheet to expand fully
                ViewGroup.LayoutParams layoutParam = bottomSheet.getLayoutParams();
                layoutParam.height = ViewGroup.LayoutParams.MATCH_PARENT;
                layoutParam.width = ViewGroup.LayoutParams.MATCH_PARENT;
                bottomSheet.setLayoutParams(layoutParam);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        dialog.setOnKeyListener((dialogInterface, keyCode, event) -> keyCode == KeyEvent.KEYCODE_BACK);


        return dialog;
    }


    @Override
    public void onResume() {
        super.onResume();
    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------


    private void hideAndShow(boolean showEmptyPlaceholder) {
        if (showEmptyPlaceholder) {
            binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
            binding.circlesRecycler.setVisibility(View.GONE);
            binding.constraintAddToCircle.setVisibility(View.GONE);
        } else {
            binding.emptyPlaceHolderView.setVisibility(View.GONE);
            binding.circlesRecycler.setVisibility(View.VISIBLE);
            binding.constraintAddToCircle.setVisibility(View.VISIBLE);
        }
    }

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestPromoterCircleAddMember() {
        binding.constraintAddToCircle.startProgress();
        DataService.shared(requireActivity()).requestPromoterAddToCircle(selectedCircleIds ,userId,new RestCallback<ContainerModel<PromoterEventModel>>(this) {
            @Override
            public void result(ContainerModel<PromoterEventModel> model, String error) {
               binding.constraintAddToCircle.stopProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(requireActivity(), error, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (isUserApprove) {
                    EventBus.getDefault().post(new PromoterCirclesModel());
                } else {
                    if (listener != null) {
                        listener.onReceive("true");
                    }
                }
                dismiss();

            }
        });
    }

    private void requestPromoterRingUpdateStatus(String id) {
        binding.constraintAddToCircle.stopProgress();
        DataService.shared(requireActivity()).requestPromoterRingUpdateStatus(id, "accepted", new RestCallback<ContainerModel<PromoterAddRingModel>>(this) {
            @Override
            public void result(ContainerModel<PromoterAddRingModel> model, String error) {
                binding.constraintAddToCircle.stopProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(requireActivity(), error, Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(requireActivity(), model.message, Toast.LENGTH_SHORT).show();
                requestPromoterCircleAddMember();
            }
        });
    }

    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    private class MyCircleAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.invite_contact_freind_item));

        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            PromoterCirclesModel model = (PromoterCirclesModel) getItem(position);
            if (model == null) {
                return;
            }

            viewHolder.binding.view.setVisibility(View.GONE);
            viewHolder.binding.tvUserNumber.setVisibility(View.GONE);
            viewHolder.binding.ivCheck.setVisibility(View.VISIBLE);

            Graphics.loadImageWithFirstLetter(model.getAvatar(), viewHolder.binding.ivUserProfile, model.getTitle());
            viewHolder.binding.tvUserName.setText(model.getTitle());

            if (model.getDescription() != null && !model.getDescription().isEmpty()) {
                viewHolder.binding.tvContactBookName.setText(model.getDescription());
                viewHolder.binding.tvContactBookName.setVisibility(View.VISIBLE);
            } else {
                viewHolder.binding.tvContactBookName.setVisibility(View.GONE);
            }


            viewHolder.binding.ivCheck.setOnCheckedChangeListener(null);
            boolean isChecked = selectedCircleIds.contains(model.getId());
            viewHolder.binding.ivCheck.setChecked(isChecked);
            viewHolder.binding.ivCheck.setPadding(isChecked ? 0 : 4, 0, 0, 0);
            viewHolder.binding.ivCheck.setButtonDrawable(isChecked ? R.drawable.check_box_select_unselect_owner : R.drawable.complete_icon_unselected);

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
                Utils.preventDoubleClick(v);
                viewHolder.selectUnSelectCircles(model);
            });

            viewHolder.binding.ivCheck.setOnCheckedChangeListener((buttonView, isChecked1) -> {
               viewHolder.selectUnSelectCircles(model);
            });


        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final InviteContactFreindItemBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = InviteContactFreindItemBinding.bind(itemView);

            }

            private void selectUnSelectCircles(PromoterCirclesModel model){
                if (selectedCircleIds.contains(model.getId())){
                    selectedCircleIds.remove(model.getId());
                }else {
                    selectedCircleIds.add(model.getId());
                }
                notifyDataSetChanged();
            }

        }
    }

    // endregion
    // --------------------------------------
}