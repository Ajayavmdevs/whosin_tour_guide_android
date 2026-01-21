package com.whosin.app.ui.fragment.SubAdminPromoter;

import static com.whosin.app.comman.AppDelegate.activity;

import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Preferences;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.FragmentSubAdminNotificationBinding;
import com.whosin.app.databinding.ItemNotificationUserListBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.PromoterProfileManager;
import com.whosin.app.service.models.ContainerListModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.PromoterAddRingModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.CmProfile.CmPublicProfileActivity;
import com.whosin.app.ui.fragment.comman.BaseFragment;

public class SubAdminNotificationFragment extends BaseFragment {


    private FragmentSubAdminNotificationBinding binding;

    private PromoterNotificationUserAdapter<UserDetailModel> adapter = new PromoterNotificationUserAdapter<>();


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    public void initUi(View view) {

        binding = FragmentSubAdminNotificationBinding.bind(view);

        binding.userRecycler.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        binding.userRecycler.setAdapter(adapter);

        Graphics.applyBlurEffect(requireActivity(), binding.headerBlurView);

        binding.swipeRefreshLayout.setProgressViewOffset(false,0,280);


        requestPromoterMyRingMemberForSubAdmin(true);

        PromoterProfileManager.shared.callbackForHeader = data -> {
            String name = data.getFirstName() + " " + data.getLastName();
            binding.headertitle.setText(name);
            Graphics.loadImageWithFirstLetter(data.getImage(), binding.headerIv, name);
        };


    }

    @Override
    public void setListeners() {

        binding.swipeRefreshLayout.setOnRefreshListener(() -> requestPromoterMyRingMemberForSubAdmin(false));

    }

    @Override
    public void populateData(boolean getDataFromServer) {

    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_sub_admin_notification;
    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    public void requestPromoterMyRingMemberForSubAdmin(boolean isShowProgressBar) {
        if (isShowProgressBar){Graphics.showProgress(requireActivity());}else {binding.swipeRefreshLayout.setRefreshing(true);}
        DataService.shared(requireActivity()).requestPromoterMyRingMemberForSubAdmin(Preferences.shared.getString("promoterId"), new RestCallback<ContainerListModel<UserDetailModel>>(this) {
            @Override
            public void result(ContainerListModel<UserDetailModel> model, String error) {
                Graphics.hideProgress(requireActivity());
                binding.swipeRefreshLayout.setRefreshing(false);
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (model.data != null && !model.data.isEmpty()) {
                    adapter.updateData(model.data);
                    binding.emptyPlaceHolderView.setVisibility(View.GONE);
                } else {
                    binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
                    binding.userRecycler.setVisibility(View.GONE);
                }
            }
        });
    }

    private void requestPromoterRingUpdateBySubAdmin(String status, String id, ItemNotificationUserListBinding vBinding, boolean isApprove) {
        if (isApprove) {
            vBinding.btnApprove.startProgress();
        } else {
            vBinding.btnRejected.startProgress();
        }
        DataService.shared(activity).requestPromoterRingUpdateBySubAdmin(id, status, new RestCallback<ContainerModel<PromoterAddRingModel>>(this) {
            @Override
            public void result(ContainerModel<PromoterAddRingModel> model, String error) {
                vBinding.btnApprove.stopProgress();
                vBinding.btnRejected.stopProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(context, model.message, Toast.LENGTH_SHORT).show();
                requestPromoterMyRingMemberForSubAdmin(false);

            }
        });
    }

    // --------------------------------------
    // region Adapter
    // --------------------------------------

    private class PromoterNotificationUserAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_notification_user_list));

        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            UserDetailModel model = (UserDetailModel) getItem(position);
            if (model == null) {
                return;
            }

            if (model.getStatus() != null && !model.getStatus().isEmpty()) {
                viewHolder.binding.buttonsLayout.setVisibility(model.getStatus().equals("pending") ? View.VISIBLE : View.GONE);
            }

            if (model.getStatus().equals("rejected")) {
                viewHolder.binding.viewProfileLayout.setBackgroundColor(ContextCompat.getColor(activity, R.color.light_black_color));
                viewHolder.binding.viewProfile.setText("Rejected");
                viewHolder.binding.viewProfile.setTextColor(Color.RED);
                viewHolder.binding.viewProfile.setOnClickListener(null);
            } else {
                viewHolder.binding.viewProfile.setVisibility(View.VISIBLE);
            }

//

            viewHolder.binding.userName.setText(model.getFirstName());
            Graphics.loadImageWithFirstLetter(model.getImage(), viewHolder.binding.imgProfile, model.getFullName());

            viewHolder.binding.descriptionTv.setText(model.getStatus().equals("pending") ? "requested to join your ring" : "has joind your ring");

            viewHolder.binding.btnApprove.setOnClickListener(v -> {
                requestPromoterRingUpdateBySubAdmin("accepted", model.getId(), viewHolder.binding, true);
            });


            viewHolder.binding.btnRejected.setOnClickListener(v -> {
                requestPromoterRingUpdateBySubAdmin("rejected", model.getId(), viewHolder.binding, false);

            });

            viewHolder.binding.getRoot().setOnClickListener(view -> {
                if (model.getStatus().equals("rejected")) {
                    return;
                }
                requireActivity().startActivity(new Intent(requireActivity(), CmPublicProfileActivity.class)
                        .putExtra("isFromSubAdmin",true)
                        .putExtra("promoterUserId", model.getUserId()));
            });
            boolean isLastItem = getItemCount() - 1 == position;

            if (isLastItem) {
                int marginBottom = Utils.getMarginBottom(holder.itemView.getContext(), 0.12f);
                Utils.setBottomMargin(holder.itemView, marginBottom);
            } else {
                Utils.setBottomMargin(holder.itemView, 0);
            }

        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private ItemNotificationUserListBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemNotificationUserListBinding.bind(itemView);


            }
        }
    }


    // endregion
    // --------------------------------------


}