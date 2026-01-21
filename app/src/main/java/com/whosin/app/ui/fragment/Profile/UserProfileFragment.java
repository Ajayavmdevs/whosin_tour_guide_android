package com.whosin.app.ui.fragment.Profile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.king.image.imageviewer.ImageViewer;
import com.king.image.imageviewer.loader.GlideImageLoader;
import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.FragmentProfileBinding;
import com.whosin.app.databinding.ItemMainProfileTabDesignBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.ComplementaryProfileManager;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.ComplimentaryProfileModel;
import com.whosin.app.service.models.ContainerListModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.NotificationModel;
import com.whosin.app.service.models.PromoterEventModel;
import com.whosin.app.service.models.RatingModel;
import com.whosin.app.service.models.UpdateStatusModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.Profile.FollowingActivity;
import com.whosin.app.ui.activites.Profile.FollowresActivity;
import com.whosin.app.ui.activites.Profile.ProfileFullScreenImageActivity;
import com.whosin.app.ui.activites.Profile.UpdateProfileActivity;
import com.whosin.app.ui.activites.Promoter.PromoterActivity;
import com.whosin.app.ui.fragment.CmProfile.CmEventHistoryFragment;
import com.whosin.app.ui.fragment.CmProfile.CmTmpProfileFragment;
import com.whosin.app.ui.fragment.CmProfile.MyActionFragment;
import com.whosin.app.ui.fragment.comman.BaseFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.imaginativeworld.whynotimagecarousel.listener.CarouselListener;
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserProfileFragment extends BaseFragment {

    private FragmentProfileBinding binding;

    private List<Fragment> fragmentList = new ArrayList<>();

    private CommanCallback<Integer> callback;

    private ComplimentaryProfileModel complimentaryProfileModel = null;

    private ItemListAdapter<RatingModel> itemListAdapter = new ItemListAdapter<>();

    private List<RatingModel> tabList = new ArrayList<>();


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------


    @Override
    public void initUi(View view) {
        binding = FragmentProfileBinding.bind(view);

        applyTranslations();

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }


        if (!SessionManager.shared.getUser().isRingMember()) {
            setupUserData(SessionManager.shared.getUser());
        }

        if (SessionManager.shared.getUser().isRingMember()) {
            tabList.add(0, new RatingModel(getValue("complimentary"), 0));
            tabList.add(new RatingModel(getValue("my_actions"), 0));
            tabList.add(new RatingModel(getValue("history"), 0));
            tabList.add(new RatingModel(getValue("feed"), 0));

            fragmentList.add(0, new CmTmpProfileFragment());
            fragmentList.add(new MyActionFragment());
            fragmentList.add(new CmEventHistoryFragment());
            fragmentList.add(new FeedFragment());
        } else {
            tabList.add(new RatingModel(getValue("feed")));
            tabList.add(new RatingModel(getValue("my_actions")));

            fragmentList.add(new FeedFragment());
            fragmentList.add(new PlusOneFragment());
        }

        binding.viewPager.setAdapter(new ViewPagerAdapter(requireActivity(), fragmentList));
        binding.viewPager.setUserInputEnabled(false);

        binding.eventFilterTabRecycleView.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.eventFilterTabRecycleView.setAdapter(itemListAdapter);
        itemListAdapter.updateData(tabList);

        callback = data -> {
            binding.viewPager.setCurrentItem(data);
        };

        if (SessionManager.shared.getUser().isRingMember()) {
            requestPromoterEventListUser();
            requestComplimentaryProfile(false);
        } else {
            requestUserProfile();
        }


        binding.editProfileLayout.setVisibility(Utils.isGuestLogin() ? View.GONE : View.VISIBLE);

    }

    @Override
    public void setListeners() {

        binding.appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            binding.swipeRefreshLayout.setEnabled(verticalOffset == 0);
        });

        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            if (SessionManager.shared.getUser().isRingMember()) {
                EventBus.getDefault().post(new ComplimentaryProfileModel());
                requestPromoterEventListUser();
                requestComplimentaryProfile(false);
            } else {
                requestUserProfile();
                if (!SessionManager.shared.getUser().isPromoter() && !SessionManager.shared.getUser().isRingMember()) {
                    EventBus.getDefault().post(new PromoterEventModel());
                }
            }
            binding.swipeRefreshLayout.setRefreshing(false);
        });

        binding.followingLayout.setOnClickListener(view -> startActivity(new Intent(requireActivity(), FollowingActivity.class).putExtra("id", SessionManager.shared.getUser().getId())));

        binding.followersLayout.setOnClickListener(view -> startActivity(new Intent(requireActivity(), FollowresActivity.class).putExtra("id", SessionManager.shared.getUser().getId())));

        binding.editProfileLayout.setOnClickListener(v -> {
            Utils.preventDoubleClick(v);
            if (SessionManager.shared.getUser().isRingMember()) {
                if (complimentaryProfileModel == null) return;
                if (complimentaryProfileModel.getProfile() == null) return;
                startActivity(new Intent(requireActivity(), PromoterActivity.class).putExtra("isPromoter", false)
                        .putExtra("isEditProfile", true)
                        .putExtra("userProfileModel", new Gson().toJson(complimentaryProfileModel.getProfile())));
            } else {
                startActivity(new Intent(requireActivity(), UpdateProfileActivity.class));
            }
        });


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
                if (complimentaryProfileModel == null) return;
                if (complimentaryProfileModel.getProfile().getImages().isEmpty()) return;
                ImageViewer.load(complimentaryProfileModel.getProfile().getImages())
                        .selection(i)
                        .imageLoader(new GlideImageLoader())
                        .indicator(true)
                        .start(requireActivity());
            }

            @Override
            public void onLongClick(int i, @NonNull CarouselItem carouselItem) {

            }
        });


    }

    @Override
    public void populateData(boolean getDataFromServer) {

    }


    @Override
    public int getLayoutRes() {
        return R.layout.fragment_profile;
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UpdateStatusModel event) {}

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ComplimentaryProfileModel model) {
        requestPromoterEventListUser();
        requestComplimentaryProfile(false);
    }


    @Override
    protected Map<View, String> getTranslationMap() {
        Map<View, String> map = new HashMap<>();
        map.put(binding.tvTabTitle, "edit_profile");
        map.put(binding.tvFollowersTitle, "followers");
        map.put(binding.tvFollowingTitle, "following");

        return map;
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void setupUserData(UserDetailModel model) {
        if (model == null) return;
        binding.tvName.setText(model.getFullName());
        binding.tvFollowerCount.setText(String.valueOf(model.getFollower()));
        binding.tvFollowingCount.setText(String.valueOf(model.getFollowing()));
        Graphics.loadImageWithFirstLetter(model.getImage(), binding.imageProfile, model.getFullName());

        if (model.getBio() != null && !model.getBio().isEmpty()) {
            binding.tvBio.setText(model.getBio());
            binding.tvBio.post(() -> {
                int lineCount = binding.tvBio.getLineCount();
                if (lineCount > 2) {
                    Utils.makeTextViewResizable(binding.tvBio, 2, 2, "... " + getValue("see_more"), true);
                }
            });
        } else {
            binding.tvBio.setVisibility(View.GONE);
        }


        if (SessionManager.shared.getUser().isRingMember()) {
            setBanner();
        } else {
            List<CarouselItem> carouselItems = new ArrayList<>();
            carouselItems.add(new CarouselItem(R.drawable.app_icon));
            binding.imageCarousel.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
            binding.imageCarousel.setData(carouselItems);
            binding.imageCarousel.setAutoPlay(false);
        }

    }

    private void openLightbox(String imageUrl) {
        Intent intent = new Intent(requireActivity(), ProfileFullScreenImageActivity.class);
        intent.putExtra(ProfileFullScreenImageActivity.EXTRA_IMAGE_URL, imageUrl);
        startActivity(intent);
    }

    private void setBanner() {

        List<CarouselItem> carouselItems = new ArrayList<>();


        if (!complimentaryProfileModel.getProfile().getImages().isEmpty()) {
            for (String imageLink : complimentaryProfileModel.getProfile().getImages()) {
                carouselItems.add(new CarouselItem(imageLink, "Static Banner Title"));
                Log.d("TAG", imageLink );
            }
        } else {
            carouselItems.add(new CarouselItem(R.drawable.app_icon));
        }

        binding.imageCarousel.registerLifecycle(getLifecycle());
        binding.imageCarousel.setData(carouselItems);
    }

    public void getAllEventsCount(List<PromoterEventModel> eventList) {
        int acceptedEventsCount = (int) eventList.stream()
                .filter(p -> p.getInvite().getInviteStatus().equals("in") && p.getInvite().getPromoterStatus().equals("accepted"))
                .count();

        int pendingEventsCount = (int) eventList.stream()
                .filter(p -> (p.getInvite().getInviteStatus().equals("in") && p.getInvite().getPromoterStatus().equals("pending")) && !p.isEventFull())
                .count();

        int wishlistedEventsCount = (int) eventList.stream()
                .filter(PromoterEventModel::isWishlisted)
                .count();


        int totalCount = acceptedEventsCount + pendingEventsCount + wishlistedEventsCount;
        RatingModel model = new RatingModel(getValue("my_actions"), totalCount);
        tabList.remove(1);
        tabList.add(1, model);
        itemListAdapter.updateData(tabList);
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

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------


    private void requestPromoterEventListUser() {
        DataService.shared(requireActivity()).requestPromoterEventListUser(new RestCallback<>(this) {
            @SuppressLint("NewApi")
            @Override
            public void result(ContainerListModel<PromoterEventModel> model, String error) {
                hideProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
//                    Toast.makeText(requireActivity(), error, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (model.data != null && !model.data.isEmpty()) {
                    getAllEventsCount(model.data);
                }

            }
        });
    }


    public void requestUserProfile() {
        SessionManager.shared.getCurrentUserProfile(requireActivity(), (success, error) -> {
            binding.swipeRefreshLayout.setRefreshing(false);
            if (!Utils.isNullOrEmpty(error)) {
                Toast.makeText(Graphics.context, error, Toast.LENGTH_SHORT).show();
                return;
            }
            if (SessionManager.shared.getUser() != null){
                setupUserData(SessionManager.shared.getUser());
            }

        });
    }


    private void requestComplimentaryProfile(boolean isShowProgress) {
        if (isShowProgress) {
            showProgress();
        }
        DataService.shared(requireActivity()).requestComplimentaryProfile(new RestCallback<>(this) {
            @Override
            public void result(ContainerModel<ComplimentaryProfileModel> model, String error) {
                hideProgress();
                binding.swipeRefreshLayout.setRefreshing(false);
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    return;
                }
                if (model.getData() != null) {
                    SessionManager.shared.saveCmUserData(model.getData());
                    ComplementaryProfileManager.shared.complimentaryProfileModel = model.getData();
                    complimentaryProfileModel = model.getData();
                    EventBus.getDefault().post(new NotificationModel());
                    setupUserData(model.getData().getProfile());
                }
            }
        });
    }

    private void requestLinkCreate() {

        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("title", SessionManager.shared.getUser().getFullName());
        jsonObject.addProperty("description", SessionManager.shared.getUser().getBio().isEmpty() ? " " : SessionManager.shared.getUser().getBio());
        jsonObject.addProperty("image", SessionManager.shared.getUser().getImage().isEmpty() ? "https://ui-avatars.com/api/?name=" + SessionManager.shared.getUser().getFirstName() : SessionManager.shared.getUser().getImage());
        jsonObject.addProperty("itemId", SessionManager.shared.getUser().getId());
        jsonObject.addProperty("itemType", "user");

        DataService.shared(requireActivity()).requestLinkCreate(jsonObject, new RestCallback<ContainerModel<String>>(this) {

            @Override
            public void result(ContainerModel<String> model, String error) {
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(requireActivity(), error, Toast.LENGTH_SHORT).show();
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

            viewHolder.mbinding.tabBackground.setBackgroundColor(ContextCompat.getColor(requireActivity(), position == selectedPosition ? R.color.brand_pink :
                    R.color.promoter_profile_btn_bg));


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