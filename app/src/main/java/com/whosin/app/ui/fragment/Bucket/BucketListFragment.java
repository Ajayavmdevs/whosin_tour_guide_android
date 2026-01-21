package com.whosin.app.ui.fragment.Bucket;

import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.whosin.app.R;
import com.whosin.app.comman.AppConstants;
import com.whosin.app.comman.AppExecutors;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.HorizontalSpaceItemDecoration;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.comman.ui.roundcornerlayout.CornerType;
import com.whosin.app.databinding.BucketEventListItemBinding;
import com.whosin.app.databinding.BucketListLayoutBinding;
import com.whosin.app.databinding.FragmentBuckListBinding;
import com.whosin.app.databinding.FrindListItemBinding;
import com.whosin.app.databinding.GroupChatLayoutBinding;
import com.whosin.app.databinding.ItemCmEventsNewDesignBinding;
import com.whosin.app.databinding.ItemMyPlusOneBinding;
import com.whosin.app.databinding.LayoutSectionHeaderBinding;
import com.whosin.app.databinding.MyBucketlistItemBinding;
import com.whosin.app.databinding.OutingListItemBinding;
import com.whosin.app.databinding.OutingListLayoutBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.Repository.ChatRepository;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.BucketEventListModel;
import com.whosin.app.service.models.BucketListModel;
import com.whosin.app.service.models.ContactListModel;
import com.whosin.app.service.models.ContainerListModel;
import com.whosin.app.service.models.CreateBucketListModel;
import com.whosin.app.service.models.InviteFriendModel;
import com.whosin.app.service.models.InvitedUserModel;
import com.whosin.app.service.models.MyPlanContainerModel;
import com.whosin.app.service.models.PromoterEventModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.Profile.OtherUserProfileActivity;
import com.whosin.app.ui.activites.Promoter.ComplementaryEventDetailActivity;
import com.whosin.app.ui.activites.bucket.EventListActivity;
import com.whosin.app.ui.activites.bucket.MyInvitationActivity;
import com.whosin.app.ui.activites.bucket.OutingListActivity;
import com.whosin.app.ui.activites.home.event.EventDetailsActivity;
import com.whosin.app.ui.activites.home.event.InviteGuestListBottomSheet;
import com.whosin.app.ui.activites.venue.Bucket.BucketListDetailActivity;
import com.whosin.app.ui.activites.venue.Bucket.CreateBucketListBottomDialog;
import com.whosin.app.ui.activites.venue.Bucket.DeleteBucketDialog;
import com.whosin.app.ui.activites.venue.Bucket.EditBucketListDialog;
import com.whosin.app.ui.activites.venue.Bucket.ExitFromBucketDialog;
import com.whosin.app.ui.activites.venue.Bucket.TranshperOwnerShipDialog;
import com.whosin.app.ui.activites.wallet.RedeemActivity;
import com.whosin.app.ui.adapter.ComplementaryEventsListAdapter;
import com.whosin.app.ui.adapter.PlusOneMemberListAdapter;
import com.whosin.app.ui.fragment.Chat.BucketChatFragment;
import com.whosin.app.ui.fragment.comman.BaseFragment;
import com.whosin.app.ui.fragment.home.InviteFriendBottomSheet;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;


public class BucketListFragment extends BaseFragment {
    private FragmentBuckListBinding binding;
    private final MyPlanAdapter<MyPlanContainerModel> myPlanAdapter = new MyPlanAdapter<>();
    private BucketListModel bucketListModel;


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    public void initUi(View view) {
        binding = FragmentBuckListBinding.bind(view);

        binding.listRecycler.setLayoutManager(new LinearLayoutManager(Graphics.context, LinearLayoutManager.VERTICAL, false));
        binding.listRecycler.setAdapter(myPlanAdapter);
        binding.listRecycler.setHasFixedSize(true);
        binding.listRecycler.setItemViewCacheSize(20);
        binding.listRecycler.getRecycledViewPool().setMaxRecycledViews(0, 0);
        requestBucketList(false, false);
    }

    @Override
    public void setListeners() {
        binding.swipeRefreshLayout.setOnRefreshListener(() -> requestBucketList(true, true));
    }

    @Override
    public void populateData(boolean getDataFromServer) {
        requestBucketList(false, false);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_buck_list;
    }

    @Override
    public void onResume() {
        super.onResume();
        requestBucketList(false, false);
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestBucketList(boolean isShow, boolean shouldRefresh) {
        if (isShow) {
            showProgress();
        }
        ChatRepository.shared(requireActivity()).getBucketChatList(shouldRefresh, data -> {
            AppExecutors.get().mainThread().execute(() -> {
                binding.swipeRefreshLayout.setRefreshing(false);
                hideProgress();
                if (data != null) {
                    bucketListModel = data;
                    List<MyPlanContainerModel> list = new ArrayList<>();
                    list.add(new MyPlanContainerModel("4", "", null, new ArrayList<>()));
                    list.add(new MyPlanContainerModel("1", "", null, data.getOutingModels()));
                    list.add(new MyPlanContainerModel("-1", "My Buckets", null, new ArrayList<>()));
                    data.getBucketsModels().forEach(p -> list.add(new MyPlanContainerModel(p.getId(), "", p, new ArrayList<>())));

                    myPlanAdapter.updateData(list);
                }
            });
        });
    }

    private void requestPromoterPlusOneList() {
        DataService.shared(requireActivity()).requestPromoterPlusOneList(new RestCallback<ContainerListModel<PromoterEventModel>>(this) {
            @Override
            public void result(ContainerListModel<PromoterEventModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(requireActivity(), error, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (model.data != null && !model.data.isEmpty()) {
//                    adapter.updateData(model.data);
                }
            }
        });
    }

    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    public class MyPlanAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());

            switch (viewType) {
                case 4:
                    return new PlusOneHolder(inflater.inflate(R.layout.item_my_plus_one, parent, false));
                case 2:
                    return new OutingHolder(inflater.inflate(R.layout.outing_list_layout, parent, false));
                case 3:
                    return new BucketHolder(inflater.inflate(R.layout.my_bucketlist_item, parent, false));
                default:
                    return new TitleHolder(inflater.inflate(R.layout.layout_section_header, parent, false));
            }

        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            boolean isLastItem = position == getItemCount() - 1;
            MyPlanContainerModel planModel = (MyPlanContainerModel) getItem(position);
            if (getItemViewType(position) == 2) {
                ((OutingHolder) holder).setupData(planModel.getOutings());
            } else if (getItemViewType(position) == 3) {
                BucketHolder viewHolder = (BucketHolder) holder;
                CreateBucketListModel model = planModel.getBucketModel();
                Graphics.loadImageWithFirstLetter(model.getCoverImage(), viewHolder.mBinding.ivCover, model.getName());
                if (model.getUser() != null) {
                    Graphics.loadImageWithFirstLetter(model.getUser().getImage(), viewHolder.mBinding.imgBucketCreateUserLogo, model.getUser().getFirstName());
                    viewHolder.mBinding.imgBucketCreateUserName.setText(model.getUser().getFirstName() + " " + model.getUser().getLastName());
                }

                viewHolder.setupUsers(model.getSharedWith());
                viewHolder.mBinding.tvBucketName.setText(model.getName());

                String formattedDate = Utils.convertDateFormat(model.getCreatedAt(), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "E, d MMM yyyy");
                viewHolder.mBinding.tvBucketCreateDate.setText(formattedDate);

                viewHolder.mBinding.tvTotalMemberInBucket.setText(model.getSharedWith().size() + " " + "members");

                viewHolder.mBinding.ivMenu.setOnClickListener(view -> {
                    Utils.preventDoubleClick(view);
                    viewHolder.exitBucketSheet(model, model.getSharedWith());
                });

                viewHolder.itemView.setOnClickListener(view -> startActivity(new Intent(requireActivity(), BucketListDetailActivity.class).putExtra("bucketId", model.getId()).putExtra("name", model.getName())));

                if (isLastItem) {
                    int marginBottom = Utils.getMarginBottom(holder.itemView.getContext(), 0.10f);
                    Utils.setBottomMargin(holder.itemView, marginBottom);
                } else {
                    Utils.setBottomMargin(holder.itemView, 0);
                }
            } else if (getItemViewType(position) == 4) {
                PlusOneHolder viewHolder = (PlusOneHolder) holder;
                viewHolder.callApi();
            } else {
                TitleHolder viewHolder = (TitleHolder) holder;
                viewHolder.mBinding.tvInvite.setOnClickListener(v -> {
                    Utils.preventDoubleClick(v);
                    CreateBucketListBottomDialog dialog = new CreateBucketListBottomDialog();
                    dialog.callback = create -> {
                        if (create != null) {
                            requestBucketList(false, true);
                        }
                    };
                    dialog.show(getChildFragmentManager(), "1");
                });
            }

            if (isLastItem) {
                int marginBottom = Utils.getMarginBottom(holder.itemView.getContext(), 0.18f);
                Utils.setBottomMargin(holder.itemView, marginBottom);
            } else {
                Utils.setBottomMargin(holder.itemView, 0);
            }

        }


        public int getItemViewType(int position) {
            MyPlanContainerModel model = (MyPlanContainerModel) getItem(position);
            if (model.getId().equals("-1")) {
                return 1;
            } else if (model.getId().equals("1")) {
                return 2;
            } else if (model.getId().equals("4")) {
                return 4;
            } else {
                return 3;
            }
        }

        public class BucketHolder extends RecyclerView.ViewHolder {
            private final MyBucketlistItemBinding mBinding;
            private final InBucketListMemberAdapter<ContactListModel> adapter = new InBucketListMemberAdapter<>();

            public BucketHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = MyBucketlistItemBinding.bind(itemView);
                mBinding.friendRecycler.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false));
                mBinding.friendRecycler.setAdapter(adapter);
            }

            private void setupUsers(List<ContactListModel> sharedUsers) {
                if (sharedUsers != null && !sharedUsers.isEmpty()) {
                    adapter.updateData(sharedUsers);
                } else {
                    mBinding.friendRecycler.setVisibility(View.GONE);
                }
            }

            private void exitBucketSheet(CreateBucketListModel model, List<ContactListModel> user) {

                if (!model.getUserId().equals(SessionManager.shared.getUser().getId())) {

                    ArrayList<String> data = new ArrayList<>();
                    data.add("Exit From Bucket");
                    Graphics.showActionSheet(getContext(), getString(R.string.app_name), data, (data1, position1) -> {
                        ExitFromBucketDialog dialog = new ExitFromBucketDialog(model.getId());
                        dialog.callback = exit -> {
                            if (exit != null) {
                                requestBucketList(false, true);
                            }
                        };
                        dialog.show(getChildFragmentManager(), "1");
                    });
                } else {
                    ArrayList<String> data = new ArrayList<>();
                    data.add("Rename");
                    data.add("Delete");
                    data.add("Change Ownership");
                    Graphics.showActionSheet(getContext(), getString(R.string.app_name), data, (data1, position1) -> {
                        switch (position1) {
                            case 0:
                                EditBucketListDialog dialog = new EditBucketListDialog(model);
                                dialog.callback = edit -> {
                                    if (edit != null) {
                                        requestBucketList(false, true);
                                    }
                                };
                                dialog.show(getChildFragmentManager(), "1");
                                break;
                            case 1:
                                DeleteBucketDialog deleteDialog = new DeleteBucketDialog(model.getId());
                                deleteDialog.callback = delete -> {
                                    if (delete != null) {
                                        requestBucketList(false, true);
                                    }
                                };
                                deleteDialog.show(getChildFragmentManager(), "1");
                                break;
                            case 2:
                                if (user != null && !user.isEmpty()) {
                                    TranshperOwnerShipDialog ownerShipDialog = new TranshperOwnerShipDialog(model.getId(), user, bucketListModel);
                                    ownerShipDialog.callback = ownerShip -> {
                                        if (ownerShip != null) {
                                            requestBucketList(false, true);
                                        }
                                    };
                                    ownerShipDialog.show(getChildFragmentManager(), "1");
                                } else {
                                    Toast.makeText(requireActivity(), "There is no user in your bucket", Toast.LENGTH_SHORT).show();
                                }
                        }
                    });
                }
            }
        }

        public class OutingHolder extends RecyclerView.ViewHolder {
            private final OutingListLayoutBinding mBinding;
            private final OutingListAdapter<InviteFriendModel> adapter = new OutingListAdapter<>();

            public OutingHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = OutingListLayoutBinding.bind(itemView);
                mBinding.outingRecycler.setLayoutManager(new LinearLayoutManager(Graphics.context, LinearLayoutManager.HORIZONTAL, false));
                int spacing = getResources().getDimensionPixelSize(com.intuit.ssp.R.dimen._10ssp);
                mBinding.outingRecycler.addItemDecoration(new HorizontalSpaceItemDecoration(spacing));
                mBinding.outingRecycler.setAdapter(adapter);

            }

            public void setupData(List<InviteFriendModel> outingModels) {
                if (!outingModels.isEmpty()) {
                    adapter.updateData(outingModels);
                }
                mBinding.outingRecycler.setVisibility(outingModels.isEmpty() ? View.GONE : View.VISIBLE);
                mBinding.emptyPlaceHolderView.setVisibility(outingModels.isEmpty() ? View.VISIBLE : View.GONE);
                mBinding.tvSeeAll.setOnClickListener(view -> startActivity(new Intent(context, OutingListActivity.class)));
            }
        }

        public class TitleHolder extends RecyclerView.ViewHolder {
            private final LayoutSectionHeaderBinding mBinding;

            public TitleHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = LayoutSectionHeaderBinding.bind(itemView);
            }
        }

        public class PlusOneHolder extends RecyclerView.ViewHolder {

            private final ItemMyPlusOneBinding mBinding;

            private final ComplementaryEventsListAdapter<PromoterEventModel> adapter = new ComplementaryEventsListAdapter<>();

            public PlusOneHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemMyPlusOneBinding.bind(itemView);
                mBinding.plusOneEventList.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false));
                mBinding.plusOneEventList.setAdapter(adapter);
            }

            public void callApi() {
                DataService.shared(requireActivity()).requestPromoterPlusOneList(new RestCallback<ContainerListModel<PromoterEventModel>>(null) {
                    @Override
                    public void result(ContainerListModel<PromoterEventModel> model, String error) {
                        if (!Utils.isNullOrEmpty(error) || model == null) {
                            Toast.makeText(requireActivity(), error, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (model.data != null && !model.data.isEmpty()) {
                            mBinding.layoutPlusOne.setVisibility(VISIBLE);
                            mBinding.plusOneEventList.setVisibility(VISIBLE);
                            adapter.updateData(model.data);
                        } else {
                            mBinding.layoutPlusOne.setVisibility(GONE);
                            mBinding.plusOneEventList.setVisibility(GONE);

                        }
                    }
                });
            }
        }
    }


    public class InBucketListMemberAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.frind_list_item));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            ContactListModel model = (ContactListModel) getItem(position);
            viewHolder.mBinding.iconStatus.setVisibility(View.GONE);
            Graphics.loadImageWithFirstLetter(model.getImage(), viewHolder.mBinding.imgUserLogo, model.getFirstName());
            viewHolder.mBinding.txtUserName.setText(model.getFirstName());
            viewHolder.itemView.setOnClickListener(view -> {
                startActivity(new Intent(requireActivity(), OtherUserProfileActivity.class).putExtra("friendId", model.getId()));
            });
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final FrindListItemBinding mBinding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = FrindListItemBinding.bind(itemView);
            }
        }
    }

    public class OutingListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = UiUtils.getViewBy(parent, R.layout.outing_list_item);
            ViewGroup.LayoutParams params = view.getLayoutParams();
            int itemCount = getItemCount();
            params.width = (int) (Graphics.getScreenWidth(context) * (itemCount > 1 ? 0.85 : 0.93));
            view.setLayoutParams(params);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            boolean isLastItem = position == getItemCount() - 1;
            InviteFriendModel model = (InviteFriendModel) getItem(position);

            if (model != null) {
                viewHolder.mBinding.tvOutingTitle.setText(model.getTitle());
                viewHolder.mBinding.txtExtraGuest.setText(String.valueOf(model.getExtraGuest()));
                viewHolder.mBinding.txtCancelled.setVisibility(View.GONE);
                viewHolder.mBinding.btnCancel.setVisibility(View.GONE);
                viewHolder.mBinding.layoutPending.setVisibility(View.GONE);

                if (model.getVenue() != null) {
                    viewHolder.mBinding.txtUserName.setText(model.getVenue().getName());
                    viewHolder.mBinding.tvAddress.setText(model.getVenue().getAddress());
                    Graphics.loadImage(model.getVenue().getCover(), viewHolder.mBinding.ivCover);
                    Graphics.loadImageWithFirstLetter(model.getVenue().getLogo(), viewHolder.mBinding.imgUserLogo, model.getVenue().getName());
                }

                viewHolder.mBinding.txtDate.setText(Utils.convertDateFormat(model.getDate(), "yyyy-MM-dd"));
                viewHolder.mBinding.txtTime.setText(String.format("%s - %s", Utils.convert24HourTimeFormat(model.getStartTime()), Utils.convert24HourTimeFormat(model.getEndTime())));


                if (model.getInvitedUser() != null && !model.getInvitedUser().isEmpty()) {
                    viewHolder.friendListAdapter.updateData(model.getInvitedUser());
                }


                if (SessionManager.shared.getUser().getId().equals(model.getUserId())) {
                    if (model.getUser() != null) {
                        Graphics.loadImageWithFirstLetter(model.getUser().getImage(), viewHolder.mBinding.imageLogo, model.getUser().getFirstName());
                    }
                    viewHolder.mBinding.txtOutingDescribe.setText("You created");
                    viewHolder.mBinding.txtOutingTitle.setVisibility(View.GONE);
                    viewHolder.mBinding.constraint.setBackground(ContextCompat.getDrawable(context, R.drawable.stroke_gradiant_line_me));
                    viewHolder.mBinding.layoutStatus.setVisibility(View.INVISIBLE);
                    if (model.getStatus().equals("completed") || model.getStatus().equals("cancelled")) {
                        viewHolder.mBinding.layoutEdit.setVisibility(View.GONE);
                    } else {
                        viewHolder.mBinding.layoutEdit.setVisibility(View.VISIBLE);
                    }
                } else {
                    viewHolder.mBinding.txtOutingTitle.setVisibility(View.VISIBLE);
                    viewHolder.mBinding.txtOutingDescribe.setText("invited you to:");
                    if (model.getUser() != null) {
                        viewHolder.mBinding.txtOutingTitle.setText(model.getUser().getFirstName());
                        Graphics.loadImageWithFirstLetter(model.getUser().getImage(), viewHolder.mBinding.imageLogo, model.getUser().getFirstName());
                    }
                    ContactListModel invitedUser = model.getInvitedUser().stream().filter(model1 -> model1.getUserId().equals(SessionManager.shared.getUser().getId())).findFirst().orElse(null);
                    viewHolder.mBinding.txtStatus.setText(model.getStatus());
                    if (invitedUser != null) {
                        if (invitedUser.getInviteStatus().equals("pending")) {
                            viewHolder.mBinding.layoutStatus.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.pending_yellow));
                            viewHolder.mBinding.constraint.setBackground(ContextCompat.getDrawable(context, R.drawable.stroke_gradiant_line_pending));
                        } else if (invitedUser.getInviteStatus().equals("in")) {
                            viewHolder.mBinding.layoutStatus.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.in_green));
                            viewHolder.mBinding.constraint.setBackground(ContextCompat.getDrawable(context, R.drawable.stroke_gradiant_line_in));
                        } else {
                            viewHolder.mBinding.layoutStatus.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.out_red));
                            viewHolder.mBinding.constraint.setBackground(ContextCompat.getDrawable(context, R.drawable.stroke_gradiant_line_out));
                        }
                    }

                    viewHolder.mBinding.txtOutingDescribe.setVisibility(View.VISIBLE);
                    viewHolder.mBinding.layoutEdit.setVisibility(View.GONE);
                    viewHolder.mBinding.layoutStatus.setVisibility(View.VISIBLE);
                    viewHolder.mBinding.layout2.setOnClickListener(view -> {
                        if (model.getUser() != null) {
                            startActivity(new Intent(requireActivity(), OtherUserProfileActivity.class).putExtra("friendId", model.getUser().getId()));
                        }
                    });
                }

                viewHolder.mBinding.layoutEdit.setOnClickListener(view -> {
                    Utils.preventDoubleClick(view);
                    InviteFriendBottomSheet inviteFriendDialog = new InviteFriendBottomSheet();
                    inviteFriendDialog.inviteFriendModel = model;
                    inviteFriendDialog.setShareListener(data -> AppExecutors.get().mainThread().execute(() -> requestBucketList(false, true)));
                    inviteFriendDialog.callback = data -> {
                        if (data != null) {
                            requestBucketList(false, false);
                        }
                    };
                    inviteFriendDialog.show(getChildFragmentManager(), "1");
                });

                viewHolder.mBinding.constraint.setOnClickListener(view -> {
                    Intent intent = new Intent(getActivity(), MyInvitationActivity.class);
                    intent.putExtra("id", model.getId());
                    intent.putExtra("notificationType", "notification");
                    activityLauncher.launch(intent, result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            boolean isClose = result.getData().getBooleanExtra("close", false);
                            if (isClose) {
                                requestBucketList(false, true);
                            }
                        }
                    });
                });

                String formattedDate = Utils.convertDateFormat(model.getCreatedAt(), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "E, d MMM yyyy");
                viewHolder.mBinding.createDate.setText(String.format("Created date: %s", formattedDate));

                viewHolder.mBinding.btnSeeAll.setOnClickListener(view -> {
                    Utils.preventDoubleClick(view);
                    InviteGuestListBottomSheet inviteGuestListBottomSheet = new InviteGuestListBottomSheet();
                    inviteGuestListBottomSheet.model = model.getInvitedUser();
                    inviteGuestListBottomSheet.type = "outing";
                    inviteGuestListBottomSheet.show(getChildFragmentManager(), "");
                });
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final OutingListItemBinding mBinding;
            private final FriendListAdapter<ContactListModel> friendListAdapter = new FriendListAdapter<>();

            @SuppressLint("ClickableViewAccessibility")
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = OutingListItemBinding.bind(itemView);
                mBinding.friendRecycler.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false));
                mBinding.friendRecycler.setAdapter(friendListAdapter);
            }
        }
    }

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
            if (model == null) {
                return;
            }

            viewHolder.mBinding.txtUserName.setText(SessionManager.shared.getUser().getId().equals(model.getUserId()) ? "Me" : model.getFirstName());


            if (model.getInviteStatus().equals("pending")) {
                viewHolder.mBinding.iconStatus.setImageResource(R.drawable.icon_pending);
            } else if (model.getInviteStatus().equals("in")) {
                viewHolder.mBinding.iconStatus.setImageResource(R.drawable.icon_complete);
            } else if (model.getInviteStatus().equals("out")) {
                viewHolder.mBinding.iconStatus.setImageResource(R.drawable.icon_deleted);
            }


            Graphics.loadImageWithFirstLetter(model.getImage(), viewHolder.mBinding.imgUserLogo, model.getFirstName());

        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private FrindListItemBinding mBinding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = FrindListItemBinding.bind(itemView);
            }
        }
    }

    public class ComplementaryEventsListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = UiUtils.getViewBy(parent, R.layout.item_cm_events_new_design);
            ViewGroup.LayoutParams params = view.getLayoutParams();
            int itemCount = getItemCount();
            params.width = (int) (Graphics.getScreenWidth(requireActivity()) * (itemCount == 1 ? 0.94 : 0.90));
            view.setLayoutParams(params);
            return new ViewHolder(view);
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @SuppressLint("DefaultLocale")
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            PromoterEventModel model = (PromoterEventModel) getItem(position);
            if (model == null) return;

            viewHolder.binding.eventCategoriLayout.setVisibility(View.GONE);

            // Check Event Type
            if (model.getVenueType().equals("custom")) {
                if (model.getCustomVenue() != null) {
                    Graphics.loadImage(model.getCustomVenue().getImage(), viewHolder.binding.imageVenue);
                    Graphics.loadImage(model.getCustomVenue().getImage(), viewHolder.binding.image);
                    viewHolder.binding.titleText.setText(model.getCustomVenue().getName());
                    viewHolder.binding.subTitleText.setText(model.getCustomVenue().getAddress());
                }
            } else {
                if (model.getVenue() != null) {
                    Graphics.loadImage(model.getVenue().getCover(), viewHolder.binding.imageVenue);
                    Graphics.loadImage(model.getVenue().getLogo(), viewHolder.binding.image);
                    viewHolder.binding.titleText.setText(model.getVenue().getName());
                    viewHolder.binding.subTitleText.setText(model.getVenue().getAddress());
                }
            }

            viewHolder.handleBadge(model);

            viewHolder.binding.tvForStatus.setText("");
            viewHolder.binding.tvForStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
            viewHolder.binding.eventTimerView.setVisibility(View.GONE);
            viewHolder.binding.tvForStatus.setVisibility(View.VISIBLE);

            // Plus One Members
            viewHolder.handlePlusOneMember(model);

            switch (model.getStatus()) {
                case "completed":
                    viewHolder.binding.tvForStatus.setText("Completed");
                    viewHolder.binding.tvForStatus.setTextColor(ContextCompat.getColor(requireActivity(), R.color.green_medium));
                    break;
                case "in-progress":
                    viewHolder.binding.tvForStatus.setText("Event has started");
                    viewHolder.binding.tvForStatus.setTextColor(ContextCompat.getColor(requireActivity(), R.color.green_medium));
                    viewHolder.binding.interestConstraint.setVisibility(View.GONE);
                    break;
                default:
                    viewHolder.binding.tvForStatus.setVisibility(GONE);
                    viewHolder.binding.eventTimerView.setVisibility(View.VISIBLE);
                    viewHolder.binding.eventTimerView.setUpData(model.getDate(), model.getStartTime());
                    break;
            }

            viewHolder.binding.txtDate.setText(Utils.changeDateFormat(model.getDate(), AppConstants.DATEFORMAT_SHORT, AppConstants.DATEFORMT_DD_MM_DATE));
            viewHolder.binding.txtTime.setText(model.getStartTime() + " - " + model.getEndTime());

            int seatsCount = model.getMaxInvitee() - model.getTotalInMembers();
            boolean isEventFull = (!model.getInvite().getInviteStatus().equals("in")) && ("rejected".equals(model.getInvite().getPromoterStatus()) || seatsCount <= 0 || model.getStatus().equals("cancelled"));
            if (isEventFull) {
                viewHolder.binding.eventTimerView.setVisibility(View.GONE);
                viewHolder.binding.tvForStatus.setVisibility(View.VISIBLE);
                viewHolder.binding.tvForStatus.setText("EVENT IS FULL");
                viewHolder.binding.tvForStatus.setTextColor(ContextCompat.getColor(requireActivity(), R.color.brand_pink));
                viewHolder.binding.interestConstraint.setVisibility(View.GONE);
                viewHolder.binding.newEventTv.setVisibility(View.GONE);
                viewHolder.binding.constraint.setBackgroundTintList(ContextCompat.getColorStateList(requireActivity(), R.color.event_full_color));
            }


            // Event Category
            if (!TextUtils.isEmpty(model.getCategory()) && !model.getCategory().equalsIgnoreCase("None")) {
                viewHolder.binding.eventCategori.setText(model.getCategory());
                viewHolder.binding.eventCategoriLayout.setVisibility(View.VISIBLE);
                int marginTop = Utils.getMarginTop(holder.itemView.getContext(), 0.02f);
                setTopMargin(viewHolder.binding.eventCategoriLayout, marginTop);
            } else {
                viewHolder.binding.eventCategoriLayout.setVisibility(View.GONE);
            }

            int defaultMarginTop = Utils.getMarginTop(holder.itemView.getContext(), 0.02f);
            if (viewHolder.binding.txtTime.getVisibility() == View.VISIBLE) {
                setTopMargin(viewHolder.binding.txtTime, defaultMarginTop);
            }
//            if (viewHolder.binding.plusOneTv.getVisibility() == View.VISIBLE) {
//                setTopMargin(viewHolder.binding.plusOneTv, defaultMarginTop);
//            }
            if (
                    viewHolder.binding.txtTime.getVisibility() == View.VISIBLE &&
                    viewHolder.binding.txtDate.getVisibility() == View.VISIBLE &&
                    viewHolder.binding.eventCategoriLayout.getVisibility() == View.VISIBLE) {

                int marginTop = Utils.getMarginTop(holder.itemView.getContext(), 0.01f);
//                setTopMargin(viewHolder.binding.plusOneTv, marginTop);
                setTopMargin(viewHolder.binding.txtTime, marginTop);
                setTopMargin(viewHolder.binding.txtDate, 0);
                setTopMargin(viewHolder.binding.eventCategoriLayout, marginTop);
            }


            viewHolder.setListeners(model);

        }

        public void setTopMargin(View view, int marginTop) {
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            layoutParams.topMargin = marginTop;
            view.setLayoutParams(layoutParams);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final ItemCmEventsNewDesignBinding binding;

            private PlusOneMemberListAdapter<InvitedUserModel> plusMemberAdapter = new PlusOneMemberListAdapter<>(requireActivity());


            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemCmEventsNewDesignBinding.bind(itemView);
            }

            private void handlePlusOneMember(PromoterEventModel model) {
//                if (model.isPlusOneAccepted()) {
//                    if (model.getPlusOneMembers() != null && !model.getPlusOneMembers().isEmpty()) {
//                        binding.plusOneTv.setVisibility(VISIBLE);
//                        binding.memberRecyclerView.setVisibility(VISIBLE);
//                        plusMemberAdapter.updateData(model.getPlusOneMembers());
//                    } else {
//                        binding.plusOneTv.setVisibility(GONE);
//                        binding.memberRecyclerView.setVisibility(GONE);
//                    }
//                } else {
//                    binding.plusOneTv.setVisibility(GONE);
//                    binding.memberRecyclerView.setVisibility(GONE);
//                }
            }

            private void setListeners(PromoterEventModel model) {

                binding.getRoot().setOnClickListener(v -> {
                    Utils.preventDoubleClick(v);
                    Intent intent = new Intent(requireActivity(), ComplementaryEventDetailActivity.class);
                    intent.putExtra("eventId", model.getId());
                    intent.putExtra("type", "complementary");
                    requireActivity().startActivity(intent);

                });
            }

            private void handleBadge(PromoterEventModel model) {
                boolean isInviteIn = model.getInvite().getInviteStatus().equals("in");
                boolean isPromoterAccepted = model.getInvite().getPromoterStatus().equals("accepted");
                binding.newEventTv.setVisibility(View.GONE);
                if (model.isConfirmationRequired() && isInviteIn) {
                    binding.interestConstraint.setVisibility(View.VISIBLE);
                    updateInterestConstraint(isPromoterAccepted);
                } else if (isInviteIn && isPromoterAccepted) {
                    binding.interestConstraint.setVisibility(View.VISIBLE);
                    updateInterestConstraint(true);
                } else {
                    binding.interestConstraint.setVisibility(View.GONE);
                    binding.constraint.setBackgroundTintList(ContextCompat.getColorStateList(requireActivity(), R.color.brand_pink));

                    if (Utils.isWithinSevenHours(model.getCreatedAt())) {
                        handleNewEventAndSeatRemainingDisplay(R.color.red);
                    }
                }
            }

            private void updateInterestConstraint(boolean isAccepted) {
                String statusText = isAccepted ? "Confirmed" : "Pending";
                int colorRes = isAccepted ? R.color.im_in : R.color.amber_color;

                binding.interestedTv.setText(statusText);
                binding.interestConstraint.setBackgroundColor(ContextCompat.getColor(requireActivity(), colorRes));
                binding.constraint.setBackgroundTintList(ContextCompat.getColorStateList(requireActivity(), colorRes));
            }

            // Helper method to handle new event display logic
            private void handleNewEventAndSeatRemainingDisplay(int colorRes) {
                if (binding.interestConstraint.getVisibility() == View.GONE) {
                    float cornerRadius = requireActivity().getResources().getDimensionPixelSize(com.intuit.sdp.R.dimen._5sdp);
                    binding.newEventTv.setCornerRadius(cornerRadius, CornerType.BOTTOM_LEFT);
                }
                binding.newEventTv.setVisibility(View.VISIBLE);
                binding.constraint.setBackgroundTintList(ContextCompat.getColorStateList(requireActivity(), colorRes));
                binding.newEventTv.setBackgroundTintList(ContextCompat.getColorStateList(requireActivity(), colorRes));
            }

        }

    }


    // endregion
    // --------------------------------------


}