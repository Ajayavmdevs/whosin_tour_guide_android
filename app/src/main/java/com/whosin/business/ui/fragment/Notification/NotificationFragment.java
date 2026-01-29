package com.whosin.business.ui.fragment.Notification;

import static android.view.View.VISIBLE;

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

import com.whosin.business.R;
import com.whosin.business.comman.DiffAdapter;
import com.whosin.business.comman.DiffIdentifier;
import com.whosin.business.comman.Graphics;
import com.whosin.business.comman.Utils;
import com.whosin.business.comman.interfaces.CommanCallback;
import com.whosin.business.comman.ui.UiUtils;
import com.whosin.business.databinding.FragmentNotificationBinding;
import com.whosin.business.databinding.NotificationItemBinding;
import com.whosin.business.service.DataService;
import com.whosin.business.service.manager.AppSettingManager;
import com.whosin.business.service.models.CommanMsgModel;
import com.whosin.business.service.models.ContainerListModel;
import com.whosin.business.service.models.ContainerModel;
import com.whosin.business.service.models.MainNotificationModel;
import com.whosin.business.service.models.NotificationModel;
import com.whosin.business.service.models.UserDetailModel;
import com.whosin.business.service.models.VenueObjectModel;
import com.whosin.business.service.models.rayna.RaynaTicketDetailModel;
import com.whosin.business.service.rest.RestCallback;
import com.whosin.business.ui.activites.Profile.FollowRequestActivity;
import com.whosin.business.ui.activites.raynaTicket.RaynaTicketDetailActivity;
import com.whosin.business.ui.fragment.comman.BaseFragment;

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
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.notification_item));
        }

        @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            NotificationModel model = (NotificationModel) getItem(position);

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

            } else {
                if (model.getType().equals("ticket") || model.getType().equals("ticket-booking")) {
                    viewHolder.mBinding.notificationIcon.setVisibility(VISIBLE);
                    String imageUrl = TextUtils.isEmpty(model.getImage()) ? viewHolder.getNotificationImage(model) : model.getImage();
                    Graphics.loadImageWithFirstLetter(imageUrl, viewHolder.mBinding.notificationIcon, model.getTitle().trim());

                }
            }




            Optional<VenueObjectModel> tVenueModel = notificationModel.getVenue().stream().filter(p -> p.getId().equalsIgnoreCase(model.getTypeId())).findFirst();

            viewHolder.itemView.setOnClickListener(view -> {
                if (model.getType().equals("link")) {
                    Utils.openUrl(requireActivity(), model.getTypeId());

                } else if (model.getType().equals("ticket")) {
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

    }

    // endregion
    // --------------------------------------

}