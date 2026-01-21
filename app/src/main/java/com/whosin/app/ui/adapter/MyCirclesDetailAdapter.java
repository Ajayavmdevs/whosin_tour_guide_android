package com.whosin.app.ui.adapter;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.whosin.app.R;
import com.whosin.app.comman.AppConstants;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.ItemMyCircelsDetailListBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.ChatModel;
import com.whosin.app.service.models.CommonModel;
import com.whosin.app.service.models.ContainerListModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.PromoterCirclesModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.CmProfile.CmPublicProfileActivity;
import com.whosin.app.ui.activites.home.Chat.ChatMessageActivity;
import com.whosin.app.ui.fragment.Promoter.PromoterBootmSheets.PromoterCirclesBottomSheet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MyCirclesDetailAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

    private Activity activity;
    private AppConstants.ContextType contextType;
    private CommanCallback<Boolean> reloadUserList;
    private String circleId = "";
    private FragmentManager fragmentManager;

    private final Map<String, List<PromoterCirclesModel>> userCirclesMap = new HashMap<>();


    public MyCirclesDetailAdapter(Activity activity, FragmentManager fragmentManager, AppConstants.ContextType contextType) {
        this.activity = activity;
        this.fragmentManager = fragmentManager;
        this.contextType = contextType;
    }

    public MyCirclesDetailAdapter(Activity activity, FragmentManager fragmentManager, String circleId, AppConstants.ContextType contextType, CommanCallback<Boolean> reloadList) {
        this.activity = activity;
        this.fragmentManager = fragmentManager;
        this.contextType = contextType;
        this.reloadUserList = reloadList;
        this.circleId = circleId;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_my_circels_detail_list));
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ViewHolder viewHolder = (ViewHolder) holder;
        UserDetailModel model = (UserDetailModel) getItem(position);
        String promoterUserId = model.getId();

        viewHolder.binding.emailTv.setText(Utils.getLangValue("request_to_join_your_ring"));
        viewHolder.binding.tvViewMore.setText(Utils.getLangValue("view_more"));

        viewHolder.binding.emailTv.setVisibility(View.VISIBLE);

        Graphics.loadImageWithFirstLetter(model.getImage(), viewHolder.binding.image, model.getFullName());
        viewHolder.binding.tvName.setText(model.getFullName());

        if (contextType.equals(AppConstants.ContextType.MY_RING)) {
            if (viewHolder.isUserIdPresent(model.getUserId())){
                if (!viewHolder.getDataByUserId(model.getUserId()).isEmpty()) {
                    viewHolder.binding.circleMemberRecycle.setVisibility(View.VISIBLE);
                    viewHolder.circlesAdapter.updateData(viewHolder.getDataByUserId(model.getUserId()).stream().limit(4).collect(Collectors.toList()));
                    viewHolder.binding.tvViewMore.setVisibility(viewHolder.getDataByUserId(model.getUserId()).size() > 4 ? View.VISIBLE : View.GONE);
                    viewHolder.binding.emailTv.setVisibility(View.GONE);
                } else {
                    viewHolder.binding.circleMemberRecycle.setVisibility(View.GONE);
                    viewHolder.binding.tvViewMore.setVisibility(View.GONE);
                }
            }else {
                viewHolder.requestPromoterCirclesByUserId(model,false,position);
            }
            viewHolder.binding.emailTv.setVisibility(View.GONE);
        } else {
            viewHolder.binding.circleMemberRecycle.setVisibility(View.GONE);
            viewHolder.binding.tvViewMore.setVisibility(View.GONE);
            viewHolder.binding.emailTv.setVisibility(View.VISIBLE);

            if (model.getEmail() != null && !model.getEmail().isEmpty()) {
                viewHolder.binding.emailTv.setText(model.getEmail());
                viewHolder.binding.emailTv.setVisibility(View.VISIBLE);
            } else {
                viewHolder.binding.emailTv.setVisibility(View.GONE);
            }
        }

        viewHolder.binding.ivCheck.setOnCheckedChangeListener(null);
        viewHolder.binding.ivCheck.setChecked(model.isRingUserSelect());
        viewHolder.binding.btnChat.setVisibility(View.VISIBLE);

        if (model.getBanStatus().equals("permanent") || model.getBanStatus().equals("temporary")) {
            viewHolder.binding.tvBanStatus.setVisibility(View.VISIBLE);
            viewHolder.binding.tvBanStatus.setText(String.format("%s\nbanned", model.getBanStatus()));
            viewHolder.binding.ivCheck.setVisibility(View.GONE);
            viewHolder.binding.ivMenu.setVisibility(View.GONE);
            viewHolder.binding.btnChat.setVisibility(View.GONE);
        } else {
            viewHolder.binding.tvBanStatus.setVisibility(View.GONE);
            if (contextType == AppConstants.ContextType.CREATE_EVENT) {
                viewHolder.binding.ivMenu.setVisibility(View.GONE);
                viewHolder.binding.ivCheck.setVisibility(View.VISIBLE);
                viewHolder.binding.getRoot().setBackgroundColor(ContextCompat.getColor(activity, R.color.black));
                viewHolder.binding.ivCheck.setChecked(model.isRingUserSelect());
            } else if (contextType == AppConstants.ContextType.ADD_USER) {
                viewHolder.binding.ivMenu.setVisibility(View.GONE);
                viewHolder.binding.ivCheck.setVisibility(View.VISIBLE);
                viewHolder.binding.ivCheck.setChecked(model.isRingUserSelect());
            } else {
                viewHolder.binding.ivMenu.setVisibility(View.VISIBLE);
                viewHolder.binding.ivCheck.setVisibility(View.GONE);
            }
        }

        viewHolder.binding.tvViewMore.setOnClickListener(view -> {
            if (fragmentManager == null) {
                return;
            }
            PromoterCirclesBottomSheet promoterCirclesBottomSheet = new PromoterCirclesBottomSheet();
            promoterCirclesBottomSheet.otherUserId = model.getUserId();
            promoterCirclesBottomSheet.callback = data -> {
                if (data) {
                    viewHolder.requestPromoterCirclesByUserId(model,true,position);
                }
            };
            promoterCirclesBottomSheet.show(fragmentManager, "");
        });

        if ((contextType == AppConstants.ContextType.MY_RING && model.getUserId().equals(SessionManager.shared.getUser().getId())) ||
                (contextType == AppConstants.ContextType.SUB_ADMIN) ||
                (contextType == AppConstants.ContextType.CIRCLE_DETAIL && model.getId().equals(SessionManager.shared.getUser().getId()))) {
            viewHolder.binding.ivCheck.setVisibility(View.GONE);
            viewHolder.binding.ivMenu.setVisibility(View.GONE);
            viewHolder.binding.btnChat.setVisibility(View.GONE);
        }


        viewHolder.binding.getRoot().setOnClickListener(view -> {
            activity.startActivity(new Intent(activity, CmPublicProfileActivity.class)
                    .putExtra("promoterUserId", promoterUserId));
        });

        viewHolder.binding.ivMenu.setOnClickListener(view -> {
            Utils.preventDoubleClick(view);

            String temporaryBan = Utils.getLangValue("temporary_ban");
            String permanentBan = Utils.getLangValue("permanent_ban");
            String remove_from_circle = Utils.getLangValue("remove_from_circle");
            String remove_from_ring = Utils.getLangValue("remove_from_ring");

            JsonObject object = new JsonObject();
            ArrayList<String> data = new ArrayList<>();
            data.add(temporaryBan);
            data.add(permanentBan);
            if (contextType == AppConstants.ContextType.CIRCLE_DETAIL) {
                data.add(remove_from_circle);
            }
            data.add("Remove from Ring");
            Graphics.showActionSheet(activity, model.getFullName(), data, (data1, position1) -> {
                String action = data.get(position1);

                if (action.equals(temporaryBan)) {
                    Graphics.showAlertDialogWithOkCancel(activity, temporaryBan, Utils.getLangValue("temporary_ban_alert"), aBoolean -> {
                        if (aBoolean) {
                            if (contextType == AppConstants.ContextType.CIRCLE_DETAIL) {
                                object.addProperty("banId", model.getId());
                            } else {
                                object.addProperty("banId", model.getUserId());
                            }
                            object.addProperty("type", "temporary");
                            viewHolder.requestPromoterMemberBan(object);
                        }
                    });

                } else if (action.equals(permanentBan)) {
                    Graphics.showAlertDialogWithOkCancel(activity, permanentBan, Utils.getLangValue("permanent_ban_alert"), aBoolean -> {
                        if (aBoolean) {
                            if (contextType == AppConstants.ContextType.CIRCLE_DETAIL) {
                                object.addProperty("banId", model.getId());
                            } else {
                                object.addProperty("banId", model.getUserId());
                            }
                            object.addProperty("type", "permanent");
                            viewHolder.requestPromoterMemberBan(object);
                        }
                    });

                } else if (action.equals(remove_from_circle)) {
                    Graphics.showAlertDialogWithOkCancel(activity, remove_from_circle, Utils.getLangValue("remove_circle_alert"), aBoolean -> {
                        if (aBoolean) {
                            viewHolder.requestPromoterCircleRemoveMember(model.getId());
                        }
                    });

                } else if (action.equals(remove_from_ring) || action.equals("Remove from Ring")) {
                    Graphics.showAlertDialogWithOkCancel(activity, remove_from_ring, Utils.getLangValue("remove_ring_requiest_alert"), aBoolean -> {
                        if (aBoolean) {
                            if (contextType == AppConstants.ContextType.MY_RING) {
                                viewHolder.requestPromoterMyRingRemoveMember(model.getUserId());
                            } else {
                                viewHolder.requestPromoterMyRingRemoveMember(model.getId());
                            }
                        }
                    });
                }
            });


        });

        viewHolder.binding.ivCheck.setOnCheckedChangeListener((buttonView, isChecked1) -> {
            if (isChecked1) {
                model.setRingUserSelect(true);
            } else {
                model.setRingUserSelect(false);
            }
            viewHolder.binding.ivCheck.post(() -> notifyDataSetChanged());
        });

        viewHolder.binding.getRoot().setOnClickListener(v -> {
            if (contextType == AppConstants.ContextType.CREATE_EVENT || contextType == AppConstants.ContextType.ADD_USER) {
                model.setRingUserSelect(!model.isRingUserSelect());
                notifyDataSetChanged();
            } else if (contextType == AppConstants.ContextType.MY_RING) {
                if (!model.getUserId().equals(SessionManager.shared.getUser().getId())) {
                    activity.startActivity(new Intent(activity, CmPublicProfileActivity.class)
                            .putExtra("promoterUserId", model.getUserId()));
                }
            } else if (contextType == AppConstants.ContextType.CIRCLE_DETAIL) {
                if (!model.getId().equals(SessionManager.shared.getUser().getId())) {
                    activity.startActivity(new Intent(activity, CmPublicProfileActivity.class)
                            .putExtra("promoterUserId", model.getId()));

                }

            } else if ((contextType == AppConstants.ContextType.SUB_ADMIN)) {
                activity.startActivity(new Intent(activity, CmPublicProfileActivity.class)
                        .putExtra("isFromSubAdmin", true)
                        .putExtra("promoterUserId", model.getUserId()));
            }
        });

        viewHolder.binding.btnChat.setOnClickListener(v -> {
            Utils.preventDoubleClick(v);
            UserDetailModel userDetailModel = new UserDetailModel();
            if (contextType == AppConstants.ContextType.MY_RING) {
                if (!model.getUserId().equals(SessionManager.shared.getUser().getId())) {
                    userDetailModel.setId(model.getUserId());
                } else {
                    return;
                }
            }
            if (contextType == AppConstants.ContextType.CIRCLE_DETAIL) {
                if (!model.getId().equals(SessionManager.shared.getUser().getId())) {
                    userDetailModel.setId(model.getId());
                } else {
                    return;
                }
            }
            userDetailModel.setFirstName(model.getFirstName());
            userDetailModel.setLastName(model.getLastName());
            userDetailModel.setImage(model.getImage());
            ChatModel chatModel = new ChatModel(userDetailModel);
            Intent intent = new Intent(activity, ChatMessageActivity.class);
            intent.putExtra("chatModel", new Gson().toJson(chatModel));
            activity.startActivity(intent);
        });

    }


    private class ViewHolder extends RecyclerView.ViewHolder {

        private ItemMyCircelsDetailListBinding binding;

        private final UserCircleListAdapter<PromoterCirclesModel> circlesAdapter = new UserCircleListAdapter<>(activity);

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemMyCircelsDetailListBinding.bind(itemView);
            binding.circleMemberRecycle.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
            binding.circleMemberRecycle.setAdapter(circlesAdapter);
        }

        public void requestPromoterMyRingRemoveMember(String id) {
            Graphics.showProgress(activity);
            DataService.shared(activity).requestPromoterMyRingRemoveMember(id, new RestCallback<ContainerListModel<UserDetailModel>>() {
                @Override
                public void result(ContainerListModel<UserDetailModel> model, String error) {
                    Graphics.hideProgress(activity);
                    if (!Utils.isNullOrEmpty(error) || model == null) {
                        Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (model.status == 1) {
                        if (reloadUserList != null) {
                            reloadUserList.onReceive(true);
                        }
                    }
                }
            });
        }

        public void requestPromoterCircleRemoveMember(String id) {
            JsonObject object = new JsonObject();

            JsonArray jsonArray = new JsonArray();
            jsonArray.add(id);

            object.add("memberIds", jsonArray);
            object.addProperty("id", circleId);

            Graphics.showProgress(activity);
            DataService.shared(activity).requestPromoterCircleRemoveMember(object, new RestCallback<ContainerModel<PromoterCirclesModel>>(null) {
                @Override
                public void result(ContainerModel<PromoterCirclesModel> model, String error) {
                    Graphics.hideProgress(activity);
                    if (!Utils.isNullOrEmpty(error) || model == null) {
                        Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (model.status == 1) {
                        if (reloadUserList != null) {
                            reloadUserList.onReceive(true);
                        }
                    }
                }
            });
        }

        public void requestPromoterMemberBan(JsonObject object) {
            Graphics.showProgress(activity);
            DataService.shared(activity).requestPromoterMemberBan(object, new RestCallback<ContainerModel<CommonModel>>(null) {
                @Override
                public void result(ContainerModel<CommonModel> model, String error) {
                    Graphics.hideProgress(activity);
                    if (!Utils.isNullOrEmpty(error) || model == null) {
                        Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (model.status == 1) {
                        Toast.makeText(activity, model.message, Toast.LENGTH_SHORT).show();
                        if (reloadUserList != null) {
                            reloadUserList.onReceive(true);
                        }
                    }
                }
            });
        }

        public void requestPromoterCirclesByUserId(UserDetailModel model, boolean isRemoveMap,int positon) {
            if (TextUtils.isEmpty(model.getUserId())) return;
            DataService.shared(activity).requestPromoterCirclesByUserId(model.getUserId(), new RestCallback<ContainerListModel<PromoterCirclesModel>>(null) {
                @Override
                public void result(ContainerListModel<PromoterCirclesModel> promoterCiclesModel, String error) {
                    if (!Utils.isNullOrEmpty(error) || promoterCiclesModel == null) {
                        Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (isRemoveMap){
                        removeToMap(model.getUserId());
                    }

                    if (promoterCiclesModel.data != null && !promoterCiclesModel.data.isEmpty()) {
                        addDataToMap(model.getUserId(),promoterCiclesModel.data);
                        binding.circleMemberRecycle.setVisibility(View.VISIBLE);
                        circlesAdapter.updateData(promoterCiclesModel.data.stream().limit(4).collect(Collectors.toList()));
                        binding.tvViewMore.setVisibility(promoterCiclesModel.data.size() > 4 ? View.VISIBLE : View.GONE);
                        binding.emailTv.setVisibility(View.GONE);
                    } else {
                        addDataToMap(model.getUserId(),new ArrayList<>());
                        binding.circleMemberRecycle.setVisibility(View.GONE);
                        binding.tvViewMore.setVisibility(View.GONE);
                    }

                    if (isRemoveMap){
                        notifyItemChanged(positon);
                    }

                }
            });
        }

        public boolean isUserIdPresent(String userId) {
            return userCirclesMap.containsKey(userId);
        }

        public void addDataToMap(String userId, List<PromoterCirclesModel> newData) {
            userCirclesMap.computeIfAbsent(userId, k -> new ArrayList<>()).addAll(newData);
        }

        public List<PromoterCirclesModel> getDataByUserId(String userId) {
            return userCirclesMap.getOrDefault(userId, new ArrayList<>());
        }

        public void removeToMap(String userId){
           userCirclesMap.remove(userId);
        }

    }


}
