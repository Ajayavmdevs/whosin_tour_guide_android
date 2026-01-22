package com.whosin.app.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.google.gson.JsonObject;
import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.FragmentProfileBinding;
import com.whosin.app.databinding.ItemMainProfileTabDesignBinding;
import com.whosin.app.databinding.UserImageListBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.CheckUserSession;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.ContactListModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.RatingModel;
import com.whosin.app.service.models.UpdateStatusModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.Profile.FollowingActivity;
import com.whosin.app.ui.activites.Profile.FollowresActivity;
import com.whosin.app.ui.activites.Profile.ProfileFullScreenImageActivity;
import com.whosin.app.ui.activites.comman.BaseActivity;
import com.whosin.app.ui.fragment.Profile.FeedFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.imaginativeworld.whynotimagecarousel.listener.CarouselListener;
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends BaseActivity {

    private FragmentProfileBinding binding;

    private final MutualFriendsAdapter<ContactListModel> userAdapter = new MutualFriendsAdapter<>();

    private List<Fragment> fragmentList = new ArrayList<>();

    private CommanCallback<Integer> callback;

    private ItemListAdapter<RatingModel> itemListAdapter = new ItemListAdapter<>();

    private List<RatingModel> tabList = new ArrayList<>();


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    protected void initUi() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        if (!SessionManager.shared.getUser().isRingMember()) {
            setupUserData(SessionManager.shared.getUser());
        }

        if (SessionManager.shared.getUser().isRingMember()) {
            tabList.add(0, new RatingModel("Complimentary", 0));
            tabList.add(new RatingModel("My Actions", 0));
            tabList.add(new RatingModel("Feed", 0));

            fragmentList.add(new FeedFragment());
        } else {
            tabList.add(new RatingModel("Feed"));
            tabList.add(new RatingModel("My Actions"));

            fragmentList.add(new FeedFragment());
        }

        binding.viewPager.setAdapter(new ViewPagerAdapter(this, fragmentList));
        binding.viewPager.setUserInputEnabled(false);

        binding.eventFilterTabRecycleView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        binding.eventFilterTabRecycleView.setAdapter(itemListAdapter);
        itemListAdapter.updateData(tabList);

        callback = data -> {
            binding.viewPager.setCurrentItem(data);
        };


    }

    @Override
    public void setListeners() {


//        binding.appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
//            if (verticalOffset == 0) {
//                binding.swipeRefreshLayout.setEnabled(true);
//            } else {
//                binding.swipeRefreshLayout.setEnabled(false);
//            }
//        });

        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            binding.swipeRefreshLayout.setRefreshing(true);
            CheckUserSession.checkSessionAndProceed(activity,this::requestUserProfile);
        });
        binding.followingLayout.setOnClickListener(view -> startActivity(new Intent(activity, FollowingActivity.class).putExtra("id", SessionManager.shared.getUser().getId())));

        binding.followersLayout.setOnClickListener(view -> startActivity(new Intent(activity, FollowresActivity.class).putExtra("id", SessionManager.shared.getUser().getId())));


        binding.ivShare.setOnClickListener(v -> {
            Utils.preventDoubleClick(v);
            requestLinkCreate();
        });

        binding.imageProfile.setOnClickListener(view -> openLightbox(SessionManager.shared.getUser().getImage()));

        binding.imageCarousel.setCarouselListener(new CarouselListener() {
            @Nullable
            @Override
            public ViewBinding onCreateViewHolder(@NonNull LayoutInflater layoutInflater, @NonNull ViewGroup viewGroup) {
                return null;
            }

            @Override
            public void onBindViewHolder(@NonNull ViewBinding viewBinding, @NonNull CarouselItem carouselItem, int i) {

            }

            @Override
            public void onClick(int i, @NonNull CarouselItem carouselItem) {
            }

            @Override
            public void onLongClick(int i, @NonNull CarouselItem carouselItem) {

            }
        });


    }


    @Override
    public int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = FragmentProfileBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        CheckUserSession.checkSessionAndProceed(activity,this::requestUserProfile);

//        setWalletCount();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UpdateStatusModel event) {
//        setWalletCount();
    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void setupUserData(UserDetailModel model) {
        if (model == null) return;
        binding.tvName.setText(model.getFullName());
        binding.tvFollowerCount.setText(String.valueOf(SessionManager.shared.getUser().getFollower()));
        binding.tvFollowingCount.setText(String.valueOf(SessionManager.shared.getUser().getFollowing()));
        Graphics.loadImageWithFirstLetter(model.getImage(), binding.imageProfile, model.getFullName());

        if (model.getBio() != null && !model.getBio().isEmpty()) {
            binding.tvBio.setText(model.getBio());
            binding.tvBio.post(() -> {
                int lineCount = binding.tvBio.getLineCount();
                if (lineCount > 2) {
                    Utils.makeTextViewResizable(binding.tvBio, 2, 2, ".. See More", true);
                }
            });
        } else {
            binding.tvBio.setVisibility(View.GONE);
        }

//        binding.mutualFriendsRecycler.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
//        binding.mutualFriendsRecycler.setAdapter(userAdapter);
//        if (model.getMutualFriends() != null && !model.getMutualFriends().isEmpty()) {
//            if (model.getMutualFriends().size() > 4) {
//                List<ContactListModel> filteredList = model.getMutualFriends().subList(0, 4);
//                userAdapter.updateData(filteredList);
//                String shareText = model.getMutualFriends().subList(0, 2).stream().map(ContactListModel::getFirstName).collect(Collectors.joining(", "));
//                String followedCount = ", +" + (model.getMutualFriends().size() - 2);
//                binding.tvShareUserName.setText("followed by " + shareText + " " + followedCount);
//            } else {
//                userAdapter.updateData(model.getMutualFriends());
//                String shareText = model.getMutualFriends().stream().map(ContactListModel::getFirstName).collect(Collectors.joining(", "));
//                binding.tvShareUserName.setText("followed by " + shareText);
//            }
//        } else {
//            binding.mutualFriendsRecycler.setVisibility(View.GONE);
//            binding.tvShareUserName.setVisibility(View.GONE);
//        }

        if (SessionManager.shared.getUser().isRingMember()) setBanner();

    }

    private void openLightbox(String imageUrl) {
        Intent intent = new Intent(activity, ProfileFullScreenImageActivity.class);
        intent.putExtra(ProfileFullScreenImageActivity.EXTRA_IMAGE_URL, imageUrl);
        startActivity(intent);
    }

    private void setBanner() {

        List<CarouselItem> carouselItems = new ArrayList<>();


        carouselItems.add(new CarouselItem(R.drawable.app_icon));

        binding.imageCarousel.registerLifecycle(getLifecycle());
        binding.imageCarousel.setData(carouselItems);
    }

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    public void requestUserProfile() {
        SessionManager.shared.getCurrentUserProfile(this, (success, error) -> {
            binding.swipeRefreshLayout.setRefreshing(false);

            if (!Utils.isNullOrEmpty(error)) {
                Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                return;
            }
            setupUserData(SessionManager.shared.getUser());
        });
    }

    private void requestLinkCreate() {
        if (activity == null) {
            return;
        }
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("title", SessionManager.shared.getUser().getFullName());
        jsonObject.addProperty("description", SessionManager.shared.getUser().getBio().isEmpty() ? " " : SessionManager.shared.getUser().getBio());
        jsonObject.addProperty("image", SessionManager.shared.getUser().getImage().isEmpty() ? "https://ui-avatars.com/api/?name=" + SessionManager.shared.getUser().getFirstName() : SessionManager.shared.getUser().getImage());
        jsonObject.addProperty("itemId", SessionManager.shared.getUser().getId());
        jsonObject.addProperty("itemType", "user");


        DataService.shared(activity).requestLinkCreate(jsonObject, new RestCallback<ContainerModel<String>>(this) {

            @Override
            public void result(ContainerModel<String> model, String error) {
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }

                String shareMsg = "";
                shareMsg = String.format("%s\n\n%s\n\n%s", jsonObject.get("title").getAsString(), jsonObject.get("description").getAsString(), model.getData());
                SessionManager.shared.saveShareVenue(shareMsg);
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, shareMsg);
                startActivity(Intent.createChooser(intent, "Share"));
            }
        });
    }


    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    public class ViewPagerAdapter extends FragmentStateAdapter {

        private List<Fragment> fragmentList;

        public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<Fragment> fragmentList) {
            super(fragmentActivity);
            this.fragmentList = fragmentList;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getItemCount() {
            return fragmentList.size();
        }
    }

    private class MutualFriendsAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = UiUtils.getViewBy(parent, R.layout.user_image_list);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) view.getLayoutParams();
            layoutParams.rightMargin = view.getContext().getResources().getDimensionPixelSize(com.intuit.sdp.R.dimen._minus10sdp);
            if (viewType == 0) {
                layoutParams.width = view.getContext().getResources().getDimensionPixelSize(com.intuit.sdp.R.dimen._48sdp);
            }
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            ContactListModel model = (ContactListModel) getItem(position);
            Graphics.loadImageWithFirstLetter(model.getImage(), viewHolder.vBinding.civPlayers, Utils.notNullString(model.getFirstName()));
            viewHolder.itemView.setOnClickListener(view -> {
                Utils.preventDoubleClick(view);
            });
        }

        @Override
        public int getItemViewType(int position) {
            if (getItemCount() == position + 1) {
                return 0;
            } else {
                return 1;
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final UserImageListBinding vBinding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                vBinding = UserImageListBinding.bind(itemView);
            }
        }
    }

    private class ItemListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        private int selectedPosition = 0;

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_main_profile_tab_design));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

            ViewHolder viewHolder = (ViewHolder) holder;
            RatingModel model = (RatingModel) getItem(position);

            if (SessionManager.shared.getUser().isRingMember()) {
                if (model.getCount() == 0) {
                    viewHolder.mbinding.tvTabTitle.setText(model.getType());
                } else {
                    viewHolder.mbinding.tvTabTitle.setText(model.getType() + " (" + model.getCount() + ")");
                }

            } else {
                viewHolder.mbinding.tvTabTitle.setText(model.getImage());
            }


            viewHolder.mbinding.tabBackground.setBackgroundColor(ContextCompat.getColor(activity, position == selectedPosition ? R.color.brand_pink : R.color.promoter_profile_btn_bg));


            viewHolder.mbinding.getRoot().setOnClickListener(view -> {
                callback.onReceive(position);
                selectedPosition = position;
                notifyDataSetChanged();
            });

        }


        public class ViewHolder extends RecyclerView.ViewHolder {

            private final ItemMainProfileTabDesignBinding mbinding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mbinding = ItemMainProfileTabDesignBinding.bind(itemView);
            }
        }
    }


    // endregion
    // --------------------------------------
}