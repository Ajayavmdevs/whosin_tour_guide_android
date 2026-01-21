package com.whosin.app.ui.fragment.SubAdminPromoter;

import static com.whosin.app.comman.AppDelegate.activity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Preferences;
import com.whosin.app.comman.Utils;
import com.whosin.app.databinding.FragmentSubAdminProfileBinding;
import com.whosin.app.databinding.ItemMyProfileMyRingViewBinding;
import com.whosin.app.databinding.ItemPomoterProfileMyCirclesViewBinding;
import com.whosin.app.databinding.ItemPromoterProfileVenueItemBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.PromoterProfileManager;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.MyPlanContainerModel;
import com.whosin.app.service.models.PromoterProfileModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.fragment.comman.BaseFragment;

import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;

import java.util.ArrayList;
import java.util.List;


public class SubAdminProfileFragment extends BaseFragment {


    private FragmentSubAdminProfileBinding binding;

    private PromoterProfileModel promoterProfileModel;

    private final PromoterProfileAdapter<MyPlanContainerModel> profileAdapter = new PromoterProfileAdapter<>();

    private boolean blurViewVisible = false;


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    public void initUi(View view) {

        binding = FragmentSubAdminProfileBinding.bind(view);


        binding.recyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        binding.recyclerView.setAdapter(profileAdapter);

        requestPromoterPublicProfile(true);

    }

    @Override
    public void setListeners() {

        binding.swipeRefreshLayout.setOnRefreshListener(() -> requestPromoterPublicProfile(false));


        binding.nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            int scrollDistance = scrollY - oldScrollY;
            if (Math.abs(scrollDistance) > 10) {
                if (scrollDistance > 0) {
                    if (!blurViewVisible) {
                        binding.headerBlurView.setBlurEnabled(true);
                        Graphics.applyBlurEffect(requireActivity(), binding.headerBlurView);
                        binding.headerLayout.setVisibility(View.VISIBLE);
                        String name = promoterProfileModel.getProfile().getFirstName() + " " + promoterProfileModel.getProfile().getLastName();
                        binding.headertitle.setText(name);
                        Graphics.loadImageWithFirstLetter(promoterProfileModel.getProfile().getImage(), binding.headerIv, name);
                        blurViewVisible = true;
                    }
                } else {
                    if (scrollY <= 10) {
                        if (blurViewVisible) {
                            binding.headerBlurView.setBackgroundColor(Color.TRANSPARENT);
                            binding.headerBlurView.setBackground(null);
                            binding.headerBlurView.setBlurEnabled(false);
                            binding.headerLayout.setVisibility(View.GONE);
                            blurViewVisible = false;
                        }
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
        return R.layout.fragment_sub_admin_profile;
    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void setUpData() {

        if (promoterProfileModel == null) {
            return;
        }
        setBanner();

        String name = promoterProfileModel.getProfile().getFirstName() + " " + promoterProfileModel.getProfile().getLastName();
        binding.tvUserName.setText(name);
        binding.tvBio.setText(promoterProfileModel.getProfile().getBio());

        Graphics.loadImageWithFirstLetter(promoterProfileModel.getProfile().getImage(), binding.imageProfile, name);

        List<MyPlanContainerModel> tmpList = new ArrayList<>();

        tmpList.add(new MyPlanContainerModel("1"));
        tmpList.add(new MyPlanContainerModel("2"));
        tmpList.add(new MyPlanContainerModel("3"));

        profileAdapter.updateData(tmpList);
    }

    private void setBanner() {
        if (promoterProfileModel == null) {
            return;
        }

        List<CarouselItem> carouselItems = new ArrayList<>();


        if (!promoterProfileModel.getProfile().getImages().isEmpty()) {
            for (String imageLink : promoterProfileModel.getProfile().getImages()) {
                carouselItems.add(new CarouselItem(imageLink, "Static Banner Title"));
            }
        } else {
            carouselItems.add(new CarouselItem(R.drawable.app_icon));
        }

        binding.imageCarousel.registerLifecycle(getLifecycle());
        binding.imageCarousel.setData(carouselItems);
    }


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    public void requestPromoterPublicProfile(boolean isShowProgress) {
        if (isShowProgress) {
            showProgress();
        } else {
            binding.swipeRefreshLayout.setRefreshing(true);
        }
        DataService.shared(requireActivity()).requestGetProfile(Preferences.shared.getString("promoterId"), new RestCallback<ContainerModel<PromoterProfileModel>>(this) {
            @Override
            public void result(ContainerModel<PromoterProfileModel> model, String error) {
                hideProgress();
                binding.swipeRefreshLayout.setRefreshing(false);
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(requireActivity(), error, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (model.getData() != null) {
                    PromoterProfileManager.shared.promoterProfileModel = model.getData();
                    UserDetailModel userDetailModel = model.getData().getProfile();
                    userDetailModel.setId(model.getData().getProfile().getUserId());
                    SessionManager.shared.saveUserData(userDetailModel);
                    promoterProfileModel = model.getData();
                    if (PromoterProfileManager.shared.callbackForHeader != null) PromoterProfileManager.shared.callbackForHeader.onReceive(model.getData().getProfile());
                    setUpData();
                }
            }
        });
    }


    // --------------------------------------
    // region Adapter
    // --------------------------------------


    public class PromoterProfileAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());

            switch (viewType) {
                case 1:
                    return new MyRingHolder(inflater.inflate(R.layout.item_my_profile_my_ring_view, parent, false));
                case 2:
                    return new MyCirclesHolder(inflater.inflate(R.layout.item_pomoter_profile_my_circles_view, parent, false));
                case 3:
                    return new MyVenuesHolder(inflater.inflate(R.layout.item_promoter_profile_venue_item, parent, false));
                default:
                    return new MyRingHolder(inflater.inflate(R.layout.item_my_profile_my_ring_view, parent, false));
            }

        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            MyPlanContainerModel model = (MyPlanContainerModel) getItem(position);

            boolean isLastItem = position == getItemCount() - 1;


            if (getItemViewType(position) == 1) {
                ((MyRingHolder) holder).setupData();
            } else if (getItemViewType(position) == 2) {
                MyCirclesHolder viewHolder = (MyCirclesHolder) holder;
                viewHolder.setupData();
            } else if (getItemViewType(position) == 3) {
                MyVenuesHolder viewHolder = (MyVenuesHolder) holder;
                viewHolder.setupData();
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
            switch (model.getId()) {
                case "1":
                    return 1;
                case "2":
                    return 2;
                case "3":
                    return 3;
                default:
                    return 4;
            }
        }


        public class MyRingHolder extends RecyclerView.ViewHolder {
            private final ItemMyProfileMyRingViewBinding mBinding;

            public MyRingHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemMyProfileMyRingViewBinding.bind(itemView);

            }

            private void setupData() {
                mBinding.maleCount.setText(String.valueOf(promoterProfileModel.getRings().getMaleCount()));
                mBinding.femaleCount.setText(String.valueOf(promoterProfileModel.getRings().getFemaleCount()));
                mBinding.otherGenderCount.setText(String.valueOf(promoterProfileModel.getRings().getPreferNotToSay()));
                mBinding.customeRing.ringMemberCout = promoterProfileModel.getRings().getCount();
                mBinding.customeRing.isFromSubAdminPromoter = true;
                mBinding.customeRing.setUpData(promoterProfileModel.getRings().getList(), requireActivity(), getChildFragmentManager());
            }
        }

        public class MyCirclesHolder extends RecyclerView.ViewHolder {

            private final ItemPomoterProfileMyCirclesViewBinding mBinding;

            public MyCirclesHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemPomoterProfileMyCirclesViewBinding.bind(itemView);

            }

            private void setupData() {
                mBinding.customCircale.isFromSubAdmin = true;
                mBinding.customCircale.setUpData(promoterProfileModel.getCircles(), requireActivity(), getChildFragmentManager());
            }

        }

        public class MyVenuesHolder extends RecyclerView.ViewHolder {
            private final ItemPromoterProfileVenueItemBinding mBinding;

            public MyVenuesHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemPromoterProfileVenueItemBinding.bind(itemView);
            }

            private void setupData() {
                mBinding.myVenue.setUpData(promoterProfileModel.getVenues().getList(), requireActivity(), getChildFragmentManager());

            }
        }

    }


    // endregion
    // --------------------------------------

}