package com.whosin.app.ui.activites.CmProfile;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.tapadoo.alerter.Alerter;
import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.ActivityMyGroupAllUserBinding;
import com.whosin.app.databinding.ItemMyCircelsDetailListBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.ChatModel;
import com.whosin.app.service.models.ComplimentaryProfileModel;
import com.whosin.app.service.models.ContainerListModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.PromoterEventModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.comman.BaseActivity;
import com.whosin.app.ui.activites.home.Chat.ChatMessageActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

public class MyGroupAllUserActivity extends BaseActivity {

    private ActivityMyGroupAllUserBinding binding;

    private MyGroupAllUserAdapter<UserDetailModel> memberAdapter = new MyGroupAllUserAdapter<>();

    private boolean isFromPlusOne = false;


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    protected void initUi() {

        binding.emptyPlaceHolderView.setEmptyPlaceTxtTitle(getValue("your_member_list_looking_bit_empty"));
        binding.titleVenuesText.setText(getValue("my_group"));

        binding.groupRecyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        binding.groupRecyclerView.setAdapter(memberAdapter);

        isFromPlusOne = getIntent().getBooleanExtra("isFromPlusOne", false);

        if (isFromPlusOne) {
            plusOneGroupListUser();
        } else {
            requestCmPlusOneGroupList();
        }

        if (isFromPlusOne) {
            binding.titleVenuesText.setText(getValue("my_plus_one"));
        }

    }

    @Override
    protected void setListeners() {
        binding.backIcon.setOnClickListener(view -> {
            finish();
        });
    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityMyGroupAllUserBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void isHideAndShowView(boolean isShow) {
        binding.groupRecyclerView.setVisibility(isShow ? View.VISIBLE : View.GONE);
        binding.emptyPlaceHolderView.setVisibility(isShow ? View.GONE : View.VISIBLE);
    }

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestCmPlusOneGroupList() {
        showProgress();
        DataService.shared(activity).requestPromoterPlusOneMyGroup(new RestCallback<ContainerListModel<UserDetailModel>>(this) {
            @Override
            public void result(ContainerListModel<UserDetailModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.d("token", "Token: " + SessionManager.shared.getToken());
                if (model.data != null && !model.data.isEmpty()) {
                    isHideAndShowView(true);
                    memberAdapter.updateData(model.data);
                } else {
                    isHideAndShowView(false);
                }
            }
        });
    }

    private void requestPromoterPlusOneInviteUserRemove(String id, String fullName) {
        showProgress();
        DataService.shared(activity).requestPromoterPlusOneInviteUserRemove(id, new RestCallback<ContainerModel<UserDetailModel>>(this) {
            @Override
            public void result(ContainerModel<UserDetailModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }
                Alerter.create(activity).setTitle(getValue("oh_snap")).setTitleAppearance(R.style.AlerterTitle).setTextAppearance(R.style.AlerterText).setText(getValue("you_have_remove")+ fullName).hideIcon().show();
                requestCmPlusOneGroupList();
                EventBus.getDefault().post(new ComplimentaryProfileModel());
            }
        });
    }


    private void plusOneGroupListUser() {
        DataService.shared(activity).requestPromoterPlusOneGroupListUser(new RestCallback<ContainerListModel<UserDetailModel>>(this) {
            @Override
            public void result(ContainerListModel<UserDetailModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (model.data != null && !model.data.isEmpty()) {
                    isHideAndShowView(true);
                    memberAdapter.updateData(model.data);
                } else {
                    isHideAndShowView(false);
                }
            }
        });
    }

    private void requestPromoterPlusOneGroupLeave(String id, String fullName) {
        showProgress();
        DataService.shared(activity).requestPromoterPlusOneGroupLeave(id, new RestCallback<ContainerModel<UserDetailModel>>(this) {
            @Override
            public void result(ContainerModel<UserDetailModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }

                Alerter.create(activity).setTitle(getValue("oh_snap")).setTitleAppearance(R.style.AlerterTitle).setTextAppearance(R.style.AlerterText).setText(getValue("you_have_remove") + fullName).hideIcon().show();
                Toast.makeText(activity, model.message, Toast.LENGTH_SHORT).show();
                plusOneGroupListUser();
                EventBus.getDefault().post(new ComplimentaryProfileModel());
                EventBus.getDefault().post(new PromoterEventModel());

            }
        });
    }


    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    private class MyGroupAllUserAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_my_circels_detail_list));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            UserDetailModel userDetailModel = (UserDetailModel) getItem(position);
            if (userDetailModel == null) return;
            viewHolder.binding.tvName.setText(userDetailModel.getFullName());

            viewHolder.binding.btnChat.setVisibility(View.GONE);
            viewHolder.binding.ivMenu.setVisibility(View.GONE);

            Graphics.loadImageWithFirstLetter(userDetailModel.getImage(), viewHolder.binding.image, userDetailModel.getFirstName());
            viewHolder.binding.image.setBorderWidth(0);

            if (!TextUtils.isEmpty(userDetailModel.getPhone())) {
                viewHolder.binding.emailTv.setText(userDetailModel.getPhone());
            } else {
                viewHolder.binding.emailTv.setText(userDetailModel.getEmail());
            }

            if (userDetailModel.getPlusOneStatus().equalsIgnoreCase("pending") && userDetailModel.getAdminStatusOnPlusOne().equalsIgnoreCase("pending")) {
                viewHolder.binding.emailTv.setText(getValue("pending"));
                viewHolder.binding.emailTv.setTextColor(ContextCompat.getColor(activity, R.color.pending_yellow));
            }else if (userDetailModel.getPlusOneStatus().equalsIgnoreCase("pending") && userDetailModel.getAdminStatusOnPlusOne().equalsIgnoreCase("accepted")) {
                viewHolder.binding.emailTv.setTextColor(ContextCompat.getColor(activity, R.color.pending_yellow));
                viewHolder.binding.emailTv.setText(getValue("pending"));
            } else if (userDetailModel.getPlusOneStatus().equalsIgnoreCase("rejected") && userDetailModel.getAdminStatusOnPlusOne().equalsIgnoreCase("pending")) {
                viewHolder.binding.emailTv.setTextColor(ContextCompat.getColor(activity, R.color.red));
                viewHolder.binding.emailTv.setText(getValue("rejected"));
            } else if (userDetailModel.getAdminStatusOnPlusOne().equals("pending") && userDetailModel.getPlusOneStatus().equals("accepted")) {
                viewHolder.binding.emailTv.setTextColor(ContextCompat.getColor(activity, R.color.pending_yellow));
                viewHolder.binding.emailTv.setText(getValue("waiting_for_admin_approval"));
            } else {
                viewHolder.binding.emailTv.setTextColor(ContextCompat.getColor(activity, R.color.white_70));
                viewHolder.binding.btnChat.setVisibility(View.VISIBLE);
                viewHolder.binding.ivMenu.setVisibility(View.VISIBLE);
            }


            viewHolder.binding.ivMenu.setOnClickListener(view -> {
                Utils.preventDoubleClick(view);
                ArrayList<String> data = new ArrayList<>();
                if (isFromPlusOne) {
                    data.add(getValue("leave_plusone_group"));
                } else {
                    data.add(getValue("remove_from_group"));
                }


                Graphics.showActionSheet(activity, userDetailModel.getFullName(), data, (data1, position1) -> {
                    if (isFromPlusOne) {
                        Graphics.showAlertDialogWithOkCancel(activity, getString(R.string.app_name), getValue("leave_plusone_group_alert"), getValue("yes"), getValue("cancel"), isConfirmed -> {
                            if (isConfirmed) {
                                requestPromoterPlusOneGroupLeave(userDetailModel.getId(), userDetailModel.getFullName());
                            }
                        });

                    } else {
                        Graphics.showAlertDialogWithOkCancel(activity, getString(R.string.app_name), getValue("remove_from_group_alert"), getValue("yes"), getValue("cancel"), isConfirmed -> {
                            if (isConfirmed) {
                                requestPromoterPlusOneInviteUserRemove(userDetailModel.getId(), userDetailModel.getFullName());
                            }
                        });
                    }
                });
            });

            viewHolder.binding.btnChat.setOnClickListener(v -> {
                Utils.preventDoubleClick(v);
                ChatModel chatModel = new ChatModel(userDetailModel);
                Intent intent = new Intent(activity, ChatMessageActivity.class);
                intent.putExtra("chatModel", new Gson().toJson(chatModel));
                activity.startActivity(intent);
            });

        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final ItemMyCircelsDetailListBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemMyCircelsDetailListBinding.bind(itemView);

            }
        }
    }

    // --------------------------------------
    // endregion
}