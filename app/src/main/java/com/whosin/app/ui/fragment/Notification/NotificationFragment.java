package com.whosin.app.ui.fragment.Notification;

import static android.view.View.VISIBLE;

import static com.whosin.app.comman.AppDelegate.activity;

import android.annotation.SuppressLint;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.FragmentNotificationBinding;
import com.whosin.app.databinding.ItemPlusOneNotificationViewBinding;
import com.whosin.app.databinding.NotificationItemBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.AppSettingManager;
import com.whosin.app.service.manager.DialogManager;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.CategoriesModel;
import com.whosin.app.service.models.CommanMsgModel;
import com.whosin.app.service.models.ContainerListModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.MainNotificationModel;
import com.whosin.app.service.models.NotificationModel;
import com.whosin.app.service.models.OffersModel;
import com.whosin.app.service.models.PromoterEventModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.models.VenueObjectModel;
import com.whosin.app.service.models.rayna.RaynaTicketDetailModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.Profile.FollowRequestActivity;
import com.whosin.app.ui.activites.Profile.OtherUserProfileActivity;
import com.whosin.app.ui.activites.Profile.UpdateProfileActivity;
import com.whosin.app.ui.activites.Promoter.ApplicationRejectedActivity;
import com.whosin.app.ui.activites.Promoter.CompleteYoutProfileActivity;
import com.whosin.app.ui.activites.Promoter.PromoterActivity;
import com.whosin.app.ui.activites.bucket.MyInvitationActivity;
import com.whosin.app.ui.activites.category.CategoryActivity;
import com.whosin.app.ui.activites.home.activity.ActivityListDetail;
import com.whosin.app.ui.activites.home.event.EventDetailsActivity;
import com.whosin.app.ui.activites.offers.OfferDetailBottomSheet;
import com.whosin.app.ui.activites.offers.VoucherDetailScreenActivity;
import com.whosin.app.ui.activites.raynaTicket.RaynaTicketDetailActivity;
import com.whosin.app.ui.fragment.comman.BaseFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class NotificationFragment extends BaseFragment {

    private FragmentNotificationBinding binding;

    private MainNotificationModel notificationModel;

    private final NotificationAdapter<NotificationModel> notificationAdapter = new NotificationAdapter<>();

    private int page = 1;

    private CommanCallback<Boolean> callback;

    private CommanCallback<Boolean> isNotificationListPresent;


    public NotificationFragment(CommanCallback<Boolean> callback,CommanCallback<Boolean> isNotificationListPresent){
        this.callback = callback;
        this.isNotificationListPresent = isNotificationListPresent;
    }

    public NotificationFragment(){

    }

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    public void initUi(View view) {
        binding = FragmentNotificationBinding.bind(view);
        binding.notificationRecycler.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false));
        binding.notificationRecycler.setAdapter(notificationAdapter);

        binding.emptyPlaceHolderView.setEmptyPlaceTxtTitle(getValue("no_active_notifications"));

        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAbsoluteAdapterPosition();
                if (notificationAdapter.getData() != null && !notificationAdapter.getData().isEmpty()) {
                    NotificationModel model = notificationAdapter.getData().get(viewHolder.getAbsoluteAdapterPosition());
                    if (direction == ItemTouchHelper.LEFT) {
                        Graphics.showAlertDialogWithOkCancel(requireActivity(), getString(R.string.app_name), getValue("delete_notification_confirmation"), aBoolean -> {
                            if (aBoolean) {
                                requestUserNotificationUser(model);
                            } else {
                                notificationAdapter.notifyItemChanged(position);
                            }
                        });

                    }
                }

            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                    int actionState, boolean isCurrentlyActive) {

                float maxSwipeDistance = viewHolder.itemView.getWidth() * 0.3f;
                float newDx = Math.max(dX, -maxSwipeDistance);

                View itemView = viewHolder.itemView;
                float cornerRadius = 32f; // adjust as needed

                Paint paint = new Paint();
                paint.setColor(ContextCompat.getColor(requireActivity(), R.color.brand_pink));
                paint.setAntiAlias(true);

                // Define the rect where background will be drawn
                RectF background = new RectF(
                        itemView.getRight() + newDx,
                        itemView.getTop(),
                        itemView.getRight(),
                        itemView.getBottom()
                );

                // Define which corners to round: [top-left, top-right, bottom-right, bottom-left]
                float[] radii = new float[]{
                        0f, 0f,                 // top-left
                        cornerRadius, cornerRadius, // top-right
                        cornerRadius, cornerRadius, // bottom-right
                        0f, 0f                  // bottom-left
                };

                Path path = new Path();
                path.addRoundRect(background, radii, Path.Direction.CW);

                c.drawPath(path, paint);

                // Optional: draw delete icon
                Drawable icon = ContextCompat.getDrawable(requireActivity(), R.drawable.icon_delete);
                if (icon != null) {
                    int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                    int iconTop = itemView.getTop() + iconMargin;
                    int iconBottom = iconTop + icon.getIntrinsicHeight();
                    int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
                    int iconRight = itemView.getRight() - iconMargin;

                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                    icon.draw(c);
                }

                super.onChildDraw(c, recyclerView, viewHolder, newDx, dY, actionState, isCurrentlyActive);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(binding.notificationRecycler);

        requestNotificationList(page, false);
        requestUserFollowList();


        AppSettingManager.shared.deleteNotificationCallBack = data -> {
            if (data) {
                Graphics.showAlertDialogWithOkCancel(requireActivity(), getString(R.string.app_name), getValue("delete_all_notifications_confirmation"), aBoolean -> {
                    if (aBoolean) {
                        requestUserNotificationUser(notificationModel.getNotification());
                    }
                });
            }
        };


    }

    @Override
    public void setListeners() {

        binding.constrainFollowRequest.setOnClickListener(view -> startActivity(new Intent(requireActivity(), FollowRequestActivity.class)));

        binding.notificationRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) binding.notificationRecycler.getLayoutManager();
                int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                int lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();

                if (firstVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition != RecyclerView.NO_POSITION) {
                    if (lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == notificationAdapter.getData().size() - 1) {
                        if (notificationAdapter.getData().size() % 30 == 0) {
                            if (!notificationAdapter.getData().isEmpty()) {
                                page++;
                                requestNotificationList(page, true);
                            }
                        }
                    }

                    List<String> unreadNotificationIds = IntStream.rangeClosed(firstVisibleItemPosition, lastVisibleItemPosition)
                            .mapToObj(index -> notificationAdapter.getData().get(index))
                            .filter(notification -> !notification.isReadStatus())
                            .map(notification -> String.valueOf(notification.getId()))
                            .collect(Collectors.toList());
                    if (unreadNotificationIds != null && !unreadNotificationIds.isEmpty()) {
                        requestReadNotificationList("", unreadNotificationIds);
                    }
                }
            }
        });

    }

    @Override
    public void populateData(boolean getDataFromServer) {

    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_notification;

    }

    @Override
    public void onResume() {
        super.onResume();
        requestUserFollowList();
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private String getFirstAlphabeticCharacter(String input) {
        if (input == null || input.isEmpty()) {
            return "W";
        }

        StringBuilder result = new StringBuilder();
        int count = 0;

        for (char c : input.toCharArray()) {
            if (Character.isLetter(c)) {
                result.append(Character.toUpperCase(c));
                count++;
                if (count >= 2) {
                    break;
                }
            }
        }

        if (result.length() == 0) {
            return "W";
        }

        return result.toString();
    }


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestUserFollowList() {
        DataService.shared(requireActivity()).requestUserFollowList(new RestCallback<ContainerListModel<UserDetailModel>>(this) {
            @Override
            public void result(ContainerListModel<UserDetailModel> model, String error) {
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(requireActivity(), error, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (model.data != null && !model.data.isEmpty()) {
                    binding.constrainFollowRequest.setVisibility(VISIBLE);
                    Graphics.loadImageWithFirstLetter(model.data.get(0).getImage(), binding.userIcon, model.data.get(0).getFirstName());
                    if (model.data.size() == 1) {
                        binding.userDescription.setText(model.data.get(0).getFullName());
                    } else {
                        binding.userDescription.setText(model.data.get(0).getFullName() + " and " + String.valueOf(model.data.size() - 1) + " others");
                    }
                } else {
                    binding.constrainFollowRequest.setVisibility(View.GONE);
                }
            }
        });
    }

    private void requestNotificationList(int page, boolean isLoader) {
        if (notificationAdapter.getData().isEmpty()) {
            showProgress();
        }
        if (isLoader) {
            binding.progress.setVisibility(VISIBLE);
        }
        DataService.shared(requireActivity()).requestNotificationList(page, 30, new RestCallback<ContainerModel<MainNotificationModel>>(this) {
            @Override
            public void result(ContainerModel<MainNotificationModel> model, String error) {
                hideProgress();
                binding.progress.setVisibility(View.GONE);
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(requireActivity(), error, Toast.LENGTH_SHORT).show();
                    if (isNotificationListPresent != null){
                        isNotificationListPresent.onReceive(false);
                    }
                    return;
                }

                if (model.data != null) {
                    if (notificationModel == null) {
                        notificationModel = model.getData();
                    } else {
                        notificationModel.getNotification().addAll(model.data.getNotification());
                        notificationModel.getUser().addAll(model.data.getUser());
                        notificationModel.getOffer().addAll(model.data.getOffer());
                        notificationModel.getVenue().addAll(model.data.getVenue());
                        notificationModel.getCategory().addAll(model.data.getCategory());
                    }
                    if (notificationModel != null && !notificationModel.getNotification().isEmpty()) {
                        notificationAdapter.updateData(notificationModel.getNotification());
                    }
                }
                binding.notificationRecycler.setVisibility(notificationAdapter.getData().isEmpty() ? View.GONE : VISIBLE);
                binding.emptyPlaceHolderView.setVisibility(notificationAdapter.getData().isEmpty() ? VISIBLE : View.GONE);
                if (isNotificationListPresent != null){
                    isNotificationListPresent.onReceive(!notificationAdapter.getData().isEmpty());
                }
            }
        });
    }

    private void requestReadNotificationList(String notification, List<String> id) {
        DataService.shared(requireActivity()).requestUsersNotificationRead(notification, id, new RestCallback<ContainerModel<CommanMsgModel>>(this) {
            @Override
            public void result(ContainerModel<CommanMsgModel> model, String error) {
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(requireActivity(), error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void requetsApprovedPlusOne(NotificationModel notificationModel, ItemPlusOneNotificationViewBinding mBinding, boolean isApprove) {
        if (isApprove) {
            mBinding.btnApprove.startProgress();
        } else {
            mBinding.btnRejected.startProgress();
        }
        String status = isApprove ? "accepted" : "rejected";
        DataService.shared(requireActivity()).requestPromoterPlusOneInviteUserUpdateStatus(notificationModel.getTypeId(), status, new RestCallback<ContainerModel<PromoterEventModel>>(this) {
            @Override
            public void result(ContainerModel<PromoterEventModel> model, String error) {
                mBinding.btnRejected.stopProgress();
                mBinding.btnApprove.stopProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(requireActivity(), error, Toast.LENGTH_SHORT).show();
                    return;
                }

                notificationAdapter.getData().stream()
                        .filter(p -> p.getId().equals(notificationModel.getId()))
                        .findFirst()
                        .ifPresent(notification -> notification.setPlusOneStatus(status));
                notificationAdapter.notifyDataSetChanged();


            }
        });
    }

    private void requestPromoterUpdateSubadminStatus(NotificationModel notificationModel, ItemPlusOneNotificationViewBinding vBinding, boolean isApprove) {
        if (isApprove) {
            vBinding.btnApprove.startProgress();
        } else {
            vBinding.btnRejected.startProgress();
        }
        String status = isApprove ? "accepted" : "rejected";
        DataService.shared(activity).requestPromoterUpdateSubadminStatus(notificationModel.getTypeId(), status, new RestCallback<ContainerModel<NotificationModel>>(this) {
            @Override
            public void result(ContainerModel<NotificationModel> model, String error) {
                vBinding.btnRejected.stopProgress();
                vBinding.btnApprove.stopProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(context, model.message, Toast.LENGTH_SHORT).show();
                notificationAdapter.getData().stream()
                        .filter(notification -> notification.getId().equals(notificationModel.getId()))
                        .findFirst()
                        .ifPresent(notification -> notification.setSubAdminStatus(status));
                notificationAdapter.notifyDataSetChanged();

            }
        });
    }

    private void requestUserNotificationUser(NotificationModel tmpNotificationModel) {
        List<String> id = new ArrayList<>();
        id.add(tmpNotificationModel.getId());
        showProgress();
        DataService.shared(requireActivity()).requestUserNotificationUser(id, new RestCallback<ContainerModel<NotificationModel>>(this) {
            @Override
            public void result(ContainerModel<NotificationModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(requireActivity(), error, Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(requireActivity(), model.message, Toast.LENGTH_SHORT).show();
                if (notificationAdapter.getData() != null && !notificationAdapter.getData().isEmpty()) {
                    notificationModel.getNotification().removeIf(p -> p.getId().equals(tmpNotificationModel.getId()));
                    notificationAdapter.getData().removeIf(p -> p.getId().equals(tmpNotificationModel.getId()));
                    notificationAdapter.notifyDataSetChanged();
                    binding.notificationRecycler.setVisibility(notificationAdapter.getData().isEmpty() ? View.GONE : VISIBLE);
                    binding.emptyPlaceHolderView.setVisibility(notificationAdapter.getData().isEmpty() ? VISIBLE : View.GONE);
                    if (isNotificationListPresent != null){
                        isNotificationListPresent.onReceive(!notificationAdapter.getData().isEmpty());
                    }
                }
            }
        });
    }

    private void requestUserNotificationUser(List<NotificationModel> notificationList) {
        List<String> id = new ArrayList<>();
        for (NotificationModel model : notificationList) {
            id.add(model.getId());
        }
        showProgress();
        DataService.shared(requireActivity()).requestUserNotificationUser(id, new RestCallback<ContainerModel<NotificationModel>>(this) {
            @Override
            public void result(ContainerModel<NotificationModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(requireActivity(), error, Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(requireActivity(), model.message, Toast.LENGTH_SHORT).show();
                if (notificationAdapter.getData() != null && !notificationAdapter.getData().isEmpty()) {
                    notificationModel.getNotification().clear();
                    notificationAdapter.getData().clear();
                    notificationAdapter.notifyDataSetChanged();
                    page = 1;
                    requestNotificationList(page, false);
                }
            }
        });
    }


    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    private class NotificationAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == 0) {
                return new AddPlusOneViewHolder(UiUtils.getViewBy(parent, R.layout.item_plus_one_notification_view));
            } else if (viewType == 1) {
                return new SubAdminHolder(UiUtils.getViewBy(parent, R.layout.item_plus_one_notification_view));
            }
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.notification_item));
        }

        @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            NotificationModel model = (NotificationModel) getItem(position);

            if (model.getType().equalsIgnoreCase("add-to-plusone")) {
                AddPlusOneViewHolder viewHolder = (AddPlusOneViewHolder) holder;
                viewHolder.mBinding.userName.setText(model.getTitle());
                Graphics.loadImageWithFirstLetter(model.getImage(), viewHolder.mBinding.imgProfile, model.getTitle());

                if (model.getPlusOneStatus().equals("pending")) {
                    viewHolder.mBinding.buttonsLayout.setVisibility(VISIBLE);
                } else {
                    viewHolder.mBinding.buttonsLayout.setVisibility(View.GONE);
                    String plusOneStatus = model.getPlusOneStatus();
                    String adminStatusOnPlusOne = model.getAdminStatusOnPlusOne();
                    int amberColor = ContextCompat.getColor(context, R.color.amber_color);
                    int redColor = ContextCompat.getColor(context, R.color.red);

                    if ("pending".equals(plusOneStatus) || ("accepted".equals(plusOneStatus) && "pending".equals(adminStatusOnPlusOne))) {
                        viewHolder.mBinding.buttonsLayout.setVisibility(VISIBLE);
                        viewHolder.mBinding.btnRejected.setTxtTitle(getValue("waiting_for_admin_approval"));
                        viewHolder.mBinding.btnRejected.setBg(null);
                        viewHolder.mBinding.btnRejected.setTintColor(amberColor);
                        viewHolder.mBinding.btnRejected.setEnabled(false);
                        viewHolder.mBinding.btnApprove.setVisibility(View.GONE);
                    } else if ("accepted".equals(plusOneStatus) && "rejected".equals(adminStatusOnPlusOne)) {
                        viewHolder.mBinding.buttonsLayout.setVisibility(VISIBLE);
                        viewHolder.mBinding.btnRejected.setTxtTitle(getValue("rejected_by_admin"));
                        viewHolder.mBinding.btnRejected.setBg(null);
                        viewHolder.mBinding.btnRejected.setTintColor(redColor);
                        viewHolder.mBinding.btnRejected.setEnabled(false);
                        viewHolder.mBinding.btnApprove.setVisibility(View.GONE);
                    } else if ("accepted".equals(plusOneStatus) && "accepted".equals(adminStatusOnPlusOne)) {
                        viewHolder.mBinding.buttonsLayout.setVisibility(View.GONE);
                    }
                }

                String title = model.getTitle();

                if (model.getPlusOneStatus().equals("accepted") && "accepted".equals(model.getAdminStatusOnPlusOne())) {
                    viewHolder.mBinding.descriptionTv.setText(setValue("plus_one_accepted",title));
                } else if (!model.getPlusOneStatus().equals("pending") && "rejected".equals(model.getAdminStatusOnPlusOne())) {
                    viewHolder.mBinding.descriptionTv.setText(setValue("plus_one_rejected",title));
                } else {
                    viewHolder.mBinding.descriptionTv.setText(model.getDescription());
                }




                viewHolder.mBinding.btnApprove.setOnClickListener(view -> {
                    Utils.preventDoubleClick(view);
                    if (!Utils.isProfileComplete(SessionManager.shared.getUser())) {
                        Graphics.showAlertDialogWithOkCancel(requireActivity(), getString(R.string.app_name), getValue("complete_profile_plus_one"), getValue("edit_profile"), getValue("cancel"), isConfirmed -> {
                            if (isConfirmed) {
                                startActivity(new Intent(requireActivity(), UpdateProfileActivity.class));
                            }
                        });
                    } else {
                        Graphics.showAlertDialogWithOkCancel(requireActivity(), getString(R.string.app_name), getValue("are_you_sure_accept_plus_one_request"), getValue("yes"), getValue("no"), isConfirmed -> {
                            if (isConfirmed) {
                                requetsApprovedPlusOne(model, viewHolder.mBinding, true);
                            }
                        });
                    }
                });

                viewHolder.mBinding.btnRejected.setOnClickListener(view -> {
                    Utils.preventDoubleClick(view);
                    Graphics.showAlertDialogWithOkCancel(requireActivity(), getString(R.string.app_name),
                            getValue("are_you_sure_reject_plus_one_request"),
                            getValue("yes"), getValue("no"), isConfirmed -> {
                                if (isConfirmed) {
                                    requetsApprovedPlusOne(model, viewHolder.mBinding, false);
                                }
                            });

                });


                viewHolder.mBinding.getRoot().setOnClickListener(view -> {
                    Utils.preventDoubleClick(view);
                    if (!TextUtils.isEmpty(model.getUserId())) {
                        startActivity(new Intent(requireActivity(), OtherUserProfileActivity.class).putExtra("friendId", model.getUserId()));
                    }
                });

            } else if (model.getType().equalsIgnoreCase("promoter-subadmin")) {
                SubAdminHolder viewHolder = (SubAdminHolder) holder;
                viewHolder.binding.userName.setText(model.getTitle());
                Graphics.loadImageWithFirstLetter(model.getImage(), viewHolder.binding.imgProfile, model.getTitle());
                viewHolder.binding.descriptionTv.setText(model.getDescription());

                if (model.getSubAdminStatus().equals("pending")) {
                    viewHolder.binding.buttonsLayout.setVisibility(VISIBLE);
                } else {
                    viewHolder.binding.buttonsLayout.setVisibility(View.GONE);
                }


                viewHolder.binding.btnApprove.setOnClickListener(view -> {
                    Utils.preventDoubleClick(view);
                    Graphics.showAlertDialogWithOkCancel(requireActivity(), getString(R.string.app_name), getValue("confirm_accept_sub_admin"), getValue("yes"), getValue("no"), isConfirmed -> {
                        if (isConfirmed) {
                            requestPromoterUpdateSubadminStatus(model, viewHolder.binding, true);
                            DialogManager.getInstance(activity).showRestartAppDialog("subadmin-approve");
                        }
                    });
                });

                viewHolder.binding.btnRejected.setOnClickListener(view -> {
                    Utils.preventDoubleClick(view);
                    Graphics.showAlertDialogWithOkCancel(requireActivity(), getString(R.string.app_name),
                            getValue("confirm_reject_sub_admin"),
                            getValue("yes"), getValue("no"), isConfirmed -> {
                                if (isConfirmed) {
                                    requestPromoterUpdateSubadminStatus(model, viewHolder.binding, false);
                                }
                            });

                });

//                viewHolder.binding.getRoot().setOnClickListener(view -> {
//                    Utils.preventDoubleClick(view);
//                    if (!TextUtils.isEmpty(model.getUserId())) {
//                        startActivity(new Intent(requireActivity(), OtherUserProfileActivity.class).putExtra("friendId", model.getTypeId()));
//                    }
//                });
            } else {
                ViewHolder viewHolder = (ViewHolder) holder;
                viewHolder.mBinding.optionContainer.setVisibility(View.GONE);
                viewHolder.mBinding.notificationTitle.setText(Utils.notNullString(model.getTitle()));
                viewHolder.mBinding.notificationDescription.setText(Utils.notNullString(model.getDescription()));
                viewHolder.mBinding.notificationTime.setText(Utils.getTimeAgo(model.getUpdatedAt(), requireActivity()));
                viewHolder.mBinding.notificationUnRead.setVisibility(model.isReadStatus() ? View.GONE : VISIBLE);

                viewHolder.mBinding.notificationUserIcon.setVisibility(View.GONE);
                viewHolder.mBinding.notificationText.setVisibility(View.GONE);
                viewHolder.mBinding.notificationIcon.setVisibility(View.GONE);
                viewHolder.mBinding.nextImage.setVisibility(View.GONE);
                if (model.getType().equals("follow")) {
                    if (model.getImage() != null && !model.getImage().isEmpty()) {
                        viewHolder.mBinding.notificationUserIcon.setVisibility(VISIBLE);
                        Graphics.loadImageWithFirstLetter(model.getImage(), viewHolder.mBinding.notificationUserIcon, model.getTitle().trim());
                    } else {
                        viewHolder.mBinding.notificationText.setVisibility(VISIBLE);
                        viewHolder.mBinding.notificationText.setText(getFirstAlphabeticCharacter(model.getTitle().trim()));
                    }

                } else if (model.getType().equals("ring-request-rejected") || model.getType().equals("promoter-request-rejected") ||
                        model.getType().equals("ring-request-accepted") || model.getType().equals("promoter-request-accepted") || model.getType().equals("add-to-ring")) {

                    viewHolder.mBinding.notificationUserIcon.setVisibility(VISIBLE);
                    viewHolder.mBinding.notificationText.setVisibility(View.GONE);

                    viewHolder.mBinding.optionContainer.setVisibility(View.GONE);
                    viewHolder.mBinding.timeLayout.setVisibility(View.GONE);
                    viewHolder.mBinding.nextImage.setVisibility(View.GONE);
                    if (model.getType().equals("ring-request-rejected") || model.getType().equals("promoter-request-rejected")) {
                        Glide.with(requireActivity()).load(R.drawable.icon_rejected_close).into(viewHolder.mBinding.notificationUserIcon);
                    } else if (model.getType().equals("ring-request-accepted") || model.getType().equals("promoter-request-accepted")) {
                        Glide.with(requireActivity()).load(R.drawable.icon_double_check).into(viewHolder.mBinding.notificationUserIcon);
                    } else if (model.getType().equals("add-to-ring")) {
                        viewHolder.mBinding.nextImage.setVisibility(VISIBLE);
                        Glide.with(requireActivity()).load(R.drawable.promoter_notification_icon).into(viewHolder.mBinding.notificationUserIcon);
                    }

                } else {
                    if (model.getType().equals("offer") || model.getType().equals("category")
                            || model.getType().equals("venue") || model.getType().equals("outing") || model.getType().equals("event")
                            || model.getType().equals("activity") || model.getType().equals("deal") || model.getType().equals("ticket")
                            || model.getType().equals("ticket-booking")) {

                        viewHolder.mBinding.notificationIcon.setVisibility(VISIBLE);
                        String imageUrl = TextUtils.isEmpty(model.getImage()) ? viewHolder.getNotificationImage(model) : model.getImage();
                        Graphics.loadImageWithFirstLetter(imageUrl, viewHolder.mBinding.notificationIcon, model.getTitle().trim());

                    }
                }




                Optional<VenueObjectModel> tVenueModel = notificationModel.getVenue().stream().filter(p -> p.getId().equalsIgnoreCase(model.getTypeId())).findFirst();

                viewHolder.itemView.setOnClickListener(view -> {
                    if (model.getType().equals("offer")) {
                        OfferDetailBottomSheet dialog = new OfferDetailBottomSheet();
                        dialog.offerId = model.getTypeId();
                        dialog.show(getChildFragmentManager(), "");
                    } else if (model.getType().equals("category")) {
                        startActivity(new Intent(requireActivity(), CategoryActivity.class).putExtra("categoryId", model.getTypeId()));
                    } else if (model.getType().equals("venue")) {
                        if (tVenueModel.isPresent()) {
                            Graphics.openVenueDetail(requireActivity(), tVenueModel.get().getId());
                        }
                    } else if (model.getType().equals("link")) {
                        Utils.openUrl(requireActivity(), model.getTypeId());

                    } else if (model.getType().equals("follow")) {
                        if (!TextUtils.isEmpty(model.getTypeId())) {
                            startActivity(new Intent(requireActivity(), OtherUserProfileActivity.class).putExtra("friendId", model.getTypeId()));
                        }
                    } else if (model.getType().equals("outing")) {
                        startActivity(new Intent(requireActivity(), MyInvitationActivity.class)
                                .putExtra("id", model.getTypeId())
                                .putExtra("notificationType", "notification"));

                    } else if (model.getType().equals("event")) {
                        startActivity(new Intent(requireActivity(), EventDetailsActivity.class)
                                .putExtra("eventId", model.getTypeId())
                                .putExtra("name", model.getTitle())
                                .putExtra("address", model.getDescription())
                                .putExtra("image", model.getImage()));
                    } else if (model.getType().equals("activity")) {
                        startActivity(new Intent(requireActivity(), ActivityListDetail.class)
                                .putExtra("activityId", model.getTypeId()));
                    } else if (model.getType().equals("deal")) {
                        startActivity(new Intent(requireActivity(), VoucherDetailScreenActivity.class).putExtra("id", model.getTypeId()));
                    } else if (model.getType().equals("ring-request-rejected") || model.getType().equals("promoter-request-rejected")) {
                        startActivity(new Intent(requireActivity(), ApplicationRejectedActivity.class).putExtra("model", new Gson().toJson(model)));
                    } else if (model.getType().equals("ring-request-accepted") || model.getType().equals("promoter-request-accepted")) {
                        if (SessionManager.shared.getUser().isRingMember() || SessionManager.shared.getUser().isPromoter()) {
                            return;
                        }
                        startActivity(new Intent(requireActivity(), CompleteYoutProfileActivity.class));
                    } else if (model.getType().equals("add-to-ring")) {
                        if (SessionManager.shared.getUser().isRingMember() || SessionManager.shared.getUser().isPromoter()) {
                            return;
                        }
                        startActivity(new Intent(requireActivity(), PromoterActivity.class)
                                .putExtra("isPromoter", false).putExtra("isFromNotification", true)
                                .putExtra("notificationTypeId", model.getTypeId())
                                .putExtra("isEditProfile", false));
                        requireActivity().finish();
                    }else if (model.getType().equals("ticket")) {
                        if (!TextUtils.isEmpty(model.getTypeId())){
                            startActivity(new Intent(requireActivity(), RaynaTicketDetailActivity.class)
                                    .putExtra("ticketId", model.getTypeId()));
                        }
                    }else if (model.getType().equals("ticket-booking")) {
                        if (callback != null) callback.onReceive(true);
                    }
                });

                UserDetailModel matchingUser = notificationModel.getUser().stream()
                        .filter(user -> model.getTypeId().equals(user.getId()))
                        .findFirst()
                        .orElse(null);

                if (matchingUser != null) {
                    if (!model.getType().equals("add-to-ring")) {
                        viewHolder.mBinding.notificationDescription.setVisibility(View.GONE);
                    }
                    if (!model.getType().equals("ring-request-rejected") || !model.getType().equals("promoter-request-rejected") || !model.getType().equals("ring-request-accepted") || !model.getType().equals("promoter-request-accepted")) {
                        viewHolder.mBinding.optionContainer.setVisibility(VISIBLE);
                        viewHolder.mBinding.optionContainer.setupView(matchingUser, requireActivity(), data -> {
                        });
                    }
                } else {
                    viewHolder.mBinding.notificationDescription.setVisibility(VISIBLE);
                }

                if (model.getType().equals("authentication") || model.getType().equals("add-to-ring")) {
                    viewHolder.mBinding.optionContainer.setVisibility(View.GONE);
                }


            }
        }

        @Override
        public int getItemViewType(int position) {
            NotificationModel model = (NotificationModel) getItem(position);
            if (model.getType().equalsIgnoreCase("add-to-plusone")) {
                return 0;
            } else if (model.getType().equalsIgnoreCase("promoter-subadmin")) {
                return 1;
            }
            return 2;
        }



        public class ViewHolder extends RecyclerView.ViewHolder {

            private NotificationItemBinding mBinding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = NotificationItemBinding.bind(itemView);
            }


            private String getNotificationImage(NotificationModel model) {
                if (model == null || model.getType() == null) {
                    return "";
                }

                switch (model.getType()) {
                    case "category": {
                        Optional<CategoriesModel> categoryModel = notificationModel.getCategory().stream()
                                .filter(p -> p.getId().equals(model.getTypeId()))
                                .findFirst();

                        return categoryModel.map(CategoriesModel::getImage).orElse("");
                    }
                    case "offer": {
                        Optional<OffersModel> productModel = notificationModel.getOffer().stream()
                                .filter(p -> p.getId().equals(model.getTypeId()))
                                .findFirst();

                        return productModel.map(OffersModel::getImage).orElse("");
                    }
                    case "venue": {
                        Optional<VenueObjectModel> productModel = notificationModel.getVenue().stream()
                                .filter(p -> p.getId().equals(model.getTypeId()))
                                .findFirst();

                        return productModel.map(VenueObjectModel::getLogo).orElse("");
                    }
                    case "user": {
                        Optional<UserDetailModel> productModel = notificationModel.getUser().stream()
                                .filter(p -> p.getId().equals(model.getTypeId()))
                                .findFirst();

                        return productModel.map(UserDetailModel::getImage).orElse("");
                    }case "ticket": {
                        Optional<RaynaTicketDetailModel> productModel = notificationModel.getTickets().stream()
                                .filter(p -> p.getId().equals(model.getTypeId()))
                                .findFirst();

                        String tmpImage = "";

                        if (productModel.isPresent() && productModel.get().getImages() != null && !productModel.get().getImages().isEmpty()){
                            for (String image : productModel.get().getImages()) {
                                if (!Utils.isVideo(image)) {
                                    tmpImage = image;
                                    break;
                                }
                            }
                        }
                        return tmpImage;
                    }

                    default:
                        return "";
                }
            }

        }

        public class AddPlusOneViewHolder extends RecyclerView.ViewHolder {

            private final ItemPlusOneNotificationViewBinding mBinding;

            public AddPlusOneViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemPlusOneNotificationViewBinding.bind(itemView);

                mBinding.btnApprove.setTxtTitle(getValue("approve"));
                mBinding.btnRejected.setTxtTitle(getValue("reject"));
            }
        }

        public class SubAdminHolder extends RecyclerView.ViewHolder {

            private final ItemPlusOneNotificationViewBinding binding;

            public SubAdminHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemPlusOneNotificationViewBinding.bind(itemView);

                binding.btnApprove.setTxtTitle(getValue("approve"));
                binding.btnRejected.setTxtTitle(getValue("reject"));
            }
        }

    }

    // endregion
    // --------------------------------------

}