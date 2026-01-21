package com.whosin.app.ui.fragment.Promoter.notification;

import static android.view.View.VISIBLE;
import static com.whosin.app.comman.AppDelegate.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.whosin.app.R;
import com.whosin.app.comman.AppConstants;
import com.whosin.app.comman.AppExecutors;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.FragmentPromoterNotificationUserBinding;
import com.whosin.app.databinding.ItemNotificationUserListBinding;
import com.whosin.app.databinding.PagerItemBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.MainNotificationModel;
import com.whosin.app.service.models.NotificationModel;
import com.whosin.app.service.models.PromoterAddRingModel;
import com.whosin.app.service.models.PromoterCirclesModel;
import com.whosin.app.service.rest.RestCallback;
import com.whosin.app.ui.activites.CmProfile.AddToCircleBottomSheet;
import com.whosin.app.ui.activites.CmProfile.CmPublicProfileActivity;
import com.whosin.app.ui.fragment.comman.BaseFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PromoterNotificationUserFragment extends BaseFragment {

    private FragmentPromoterNotificationUserBinding binding;

    private PromoterNotificationUserAdapter<NotificationModel> adapter = new PromoterNotificationUserAdapter<>();

    private MainNotificationModel notificationModel;

    private int page = 1;

    private Handler handler = new Handler();

    private String filter="",query="";

    private boolean isHideSearchView = false;

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    public PromoterNotificationUserFragment(boolean isHideSearchView) {
        this.isHideSearchView = isHideSearchView;
    }

    @Override
    public void initUi(View view) {
        binding = FragmentPromoterNotificationUserBinding.bind(view);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        binding.edtSearch.setHint(getValue("search"));
        binding.emptyPlaceHolderView.setEmptyPlaceTxtTitle(getValue("my_user_list_empty"));

        binding.userRecycler.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        binding.userRecycler.setAdapter(adapter);
        binding.searchContainer.setVisibility(isHideSearchView ? View.GONE : VISIBLE);
        requestPromoterUserNotification(true,false);

    }

    @Override
    public void setListeners() {

        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            page = 1;
            adapter.clearAllData();
            notificationModel = null;
            requestPromoterUserNotification(false,false);
        });


        binding.userRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) binding.userRecycler.getLayoutManager();
                int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                int lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();

                if (firstVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition != RecyclerView.NO_POSITION) {
                    if (lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == adapter.getData().size() - 1) {
                        if (adapter.getData().size() % 20 == 0) {
                            if (!adapter.getData().isEmpty()) {
                                page++;
                                requestPromoterUserNotification(false, true);
                            }
                        }
                    }

                }
            }
        });

        binding.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                query = s.toString();
                searchAndFilterData();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        binding.imgFilter.setOnClickListener(v -> {
            Utils.preventDoubleClick( v );
            openFilterActionSheet();
        });
    }

    @Override
    public void populateData(boolean getDataFromServer) {

    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_promoter_notification_user;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(PromoterCirclesModel model) {
        page = 1;
        adapter.clearAllData();
        notificationModel = null;
        requestPromoterUserNotification(false,false);
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void searchAndFilterData() {
        if (notificationModel != null && notificationModel.getNotification() != null) {
            List<NotificationModel> originalList = notificationModel.getNotification();
            List<NotificationModel> filteredList;
            if (!filter.isEmpty() && !filter.equalsIgnoreCase("All")) {
                filteredList = originalList.stream()
                        .filter(notification -> notification.getRequestStatus().toLowerCase().contains(filter.toLowerCase()))
                        .collect(Collectors.toList());
            } else {
                filteredList = new ArrayList<>(originalList);
            }
            if (query != null && !query.isEmpty()) {
                filteredList = filteredList.stream()
                        .filter(notification -> notification.getTitle().toLowerCase().contains(query.toLowerCase()))
                        .collect(Collectors.toList());
            }
            adapter.updateData(filteredList);
            binding.emptyPlaceHolderView.setVisibility(filteredList.isEmpty() ? VISIBLE : View.GONE);
            binding.userRecycler.setVisibility(filteredList.isEmpty() ? View.GONE : VISIBLE);
        }
    }


    private void openFilterActionSheet() {
        ArrayList<String> data = new ArrayList<>(Arrays.asList(getValue("all"), getValue("accepted"), getValue("rejected"), getValue("pending")));
        Graphics.showActionSheet(requireActivity(), getValue("filter"), data, (data1, position) -> {
            if (position >= 0 && position < data.size()) {
                String value = data.get(position);
                if (value.equalsIgnoreCase(getValue("all"))){
                    filter = "All";
                } else if (value.equalsIgnoreCase(getValue("accepted"))) {
                    filter = "Accepted";
                } else if (value.equalsIgnoreCase(getValue("rejected"))) {
                    filter = "Rejected";
                }else {
                    filter = "Pending";
                }
                showFilterCount();
            }
        });
    }

    private void showFilterCount() {
        if (filter.isEmpty()) {
            binding.filterBadge.setVisibility(View.GONE);
        } else {
            binding.filterBadge.setVisibility(VISIBLE);
        }
        searchAndFilterData();
    }

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    public void requestPromoterUserNotification(boolean isShowProgress,boolean isLoader) {
        if (isLoader){
            binding.progress.setVisibility(VISIBLE);
        }else {
            if (isShowProgress) {
                showProgress();
            } else {
                binding.swipeRefreshLayout.setRefreshing(true);
            }
        }

        DataService.shared(activity).requestPromoterUserNotification(page,new RestCallback<ContainerModel<MainNotificationModel>>(this) {
            @Override
            public void result(ContainerModel<MainNotificationModel> model, String error) {
                hideProgress();
                binding.progress.setVisibility(View.GONE);
                binding.swipeRefreshLayout.setRefreshing(false);
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (model.data != null) {
                    if (notificationModel == null) {
                        notificationModel = model.getData();
                    } else {
                        notificationModel.getNotification().addAll(model.data.getNotification());
                    }
                    if (!notificationModel.getNotification().isEmpty()) {
                        adapter.updateData(notificationModel.getNotification());
                    }
                }

                binding.emptyPlaceHolderView.setVisibility(adapter.getData().isEmpty() ? VISIBLE : View.GONE);
                binding.userRecycler.setVisibility(adapter.getData().isEmpty() ? View.GONE : VISIBLE);
            }
        });
    }


    private void requestPromoterRingUpdateStatus(String status, String id, ItemNotificationUserListBinding vBinding, boolean isApprove) {
        if (isApprove) {
            vBinding.btnApprove.startProgress();
        } else {
            vBinding.btnRejected.startProgress();
        }
        DataService.shared(activity).requestPromoterRingUpdateStatus(id, status, new RestCallback<ContainerModel<PromoterAddRingModel>>(this) {
            @Override
            public void result(ContainerModel<PromoterAddRingModel> model, String error) {
                vBinding.btnApprove.stopProgress();
                vBinding.btnRejected.stopProgress();
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(context, model.message, Toast.LENGTH_SHORT).show();
                EventBus.getDefault().post(new PromoterCirclesModel());

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
            NotificationModel model = (NotificationModel) getItem(position);
            if (model == null) {
                return;
            }

            viewHolder.binding.viewProfile.setText(getValue("view_profile"));
            viewHolder.binding.btnRejected.setTxtTitle(getValue("reject"));
            viewHolder.binding.btnApprove.setTxtTitle(getValue("approve"));

            viewHolder.binding.tvAppliedDate.setVisibility(View.VISIBLE);
            viewHolder.binding.dotsIndicator.setVisibility(View.VISIBLE);
            viewHolder.binding.viewPager.setVisibility(View.VISIBLE);
            viewHolder.binding.imgProfile.setVisibility(View.GONE);

            viewHolder.setupData( model.getImages(),model);


            AppExecutors.get().mainThread().execute(() -> {
                viewHolder.binding.buttonsLayout.setVisibility(View.VISIBLE);
                viewHolder.binding.btnRejected.setVisibility(View.VISIBLE);
                viewHolder.binding.btnApprove.setVisibility(View.VISIBLE);

                if (model.getRequestStatus() != null && !model.getRequestStatus().isEmpty()) {
                    viewHolder.binding.buttonsLayout.setVisibility(model.getRequestStatus().equals("pending") ? View.VISIBLE : View.GONE);
                }
            });


            if (model.getRequestStatus().equals("rejected")) {
                viewHolder.binding.viewProfileLayout.setBackgroundColor(ContextCompat.getColor(activity, R.color.light_black_color));
                viewHolder.binding.viewProfile.setText(getValue("rejected"));
                viewHolder.binding.viewProfile.setTextColor(Color.RED);
                viewHolder.binding.viewProfile.setOnClickListener(null);
            } else {
                viewHolder.binding.viewProfile.setVisibility(View.VISIBLE);
                viewHolder.binding.viewProfileLayout.setBackgroundColor(ContextCompat.getColor(activity, R.color.date_red));
                viewHolder.binding.viewProfile.setTextColor(Color.WHITE);
                viewHolder.binding.viewProfile.setText(getValue("view_profile"));
            }

//            String firstLetter = "";
//            if (model.getTitle() != null && !model.getTitle().isEmpty()) {
//                String[] words = model.getTitle().split("\\s+");
//                if (words.length > 0 && !words[0].isEmpty()) {
//                    firstLetter = String.valueOf(words[0].charAt(0));
//                }
//            }
            viewHolder.binding.userName.setText(model.getTitle());
//            Graphics.loadImageWithFirstLetter(model.getImage(), viewHolder.binding.imgProfile, firstLetter);

            viewHolder.binding.descriptionTv.setText(model.getRequestStatus().equals("pending") ? model.getDescription() : getValue("has_joind_your_ring"));

            viewHolder.binding.btnApprove.setOnClickListener(v -> {
                Utils.preventDoubleClick(v);

                String approveAndAddToCircle = getValue("approve_and_add_to_circle");
                String approveOnly = getValue("approve_only");
                String cancel = getValue("cancel");

                Graphics.showAlertDialog(requireActivity(),
                        getString(R.string.app_name),
                        setValue("accept_confirm_alert", model.getTitle()),
                        approveAndAddToCircle, cancel, approveOnly,
                        action -> {
                            if (action.equals(approveAndAddToCircle)) {
                                AddToCircleBottomSheet bottomSheet = new AddToCircleBottomSheet();
                                bottomSheet.isUserApprove = true;
                                bottomSheet.userId = model.getTypeId();
                                bottomSheet.listener = data -> {
                                    if ("true".equals(data)) {
                                        requestPromoterRingUpdateStatus("accepted", model.getTypeId(), viewHolder.binding, true);
                                    }
                                };
                                bottomSheet.show(getChildFragmentManager(), "AddToCircleBottomSheet");

                            } else if (action.equals(approveOnly)) {
                                requestPromoterRingUpdateStatus("accepted", model.getTypeId(), viewHolder.binding, true);

                            } else if (action.equals(cancel)) {
                                // Do nothing, just close
                            }
                        });
            });

            viewHolder.binding.btnRejected.setOnClickListener(v -> {
                Graphics.showAlertDialogWithOkCancel(requireActivity(), getString(R.string.app_name), setValue("reject_confirm_alert",model.getTitle()),
                        getValue("yes"), getValue("cancel"), aBoolean -> {
                            if (aBoolean) {
                                requestPromoterRingUpdateStatus("rejected", model.getTypeId(), viewHolder.binding, false);
                            }
                        });

            });

            viewHolder.binding.getRoot().setOnClickListener(view -> {
                if (model.getRequestStatus().equals("rejected")) {
                    return;
                }
                activity.startActivity(new Intent(activity, CmPublicProfileActivity.class).putExtra("promoterUserId", model.getTypeId()));
            });

            String date = Utils.convertDateFormat(model.getUpdatedAt(), AppConstants.DATEFORMAT_LONG_TIME,AppConstants.DATEFORMT_DATE_AND_12TIMEFORMAT);
            viewHolder.binding.tvAppliedDate.setText(date);

            boolean isLastItem = getItemCount() - 1 == position;

            if (isLastItem) {
                int marginBottom = Utils.getMarginBottom(holder.itemView.getContext(), 0.14f);
                Utils.setBottomMargin(holder.itemView, marginBottom);
            } else {
                Utils.setBottomMargin(holder.itemView, 0);
            }

        }


        public class ViewHolder extends RecyclerView.ViewHolder {

            private final ItemNotificationUserListBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemNotificationUserListBinding.bind(itemView);

            }

            public void setupData(List<String> bannerModels, NotificationModel model) {
               MyPagerAdapter adapter = new MyPagerAdapter( activity, bannerModels,model );
                binding.viewPager.setAdapter( adapter );
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            int nextPage = binding.viewPager.getCurrentItem() + 1;
                            if (nextPage == adapter.getCount()) {
                                nextPage = 0;
                            }
                            binding.viewPager.setCurrentItem( nextPage, true );
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            handler.postDelayed( this, 4000 );
                        }
                    }
                };
                handler.postDelayed( runnable, 4000 );

                binding.viewPager.addOnPageChangeListener( new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                        updateDotsIndicator(position);

                    }

                    @Override
                    public void onPageSelected(int position) {
                        handler.removeCallbacks( runnable );
                        handler.postDelayed( runnable, 4000 );

                        updateDotsIndicator(position);
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                    }
                } );

                binding.dotsIndicator.attachTo( binding.viewPager );
            }

            private void updateDotsIndicator(int currentPosition) {
                for (int i = 0; i < binding.dotsIndicator.getChildCount(); i++) {
                    View dot = binding.dotsIndicator.getChildAt(i);
                    if (dot instanceof ImageView) {
                        ImageView dotImageView = (ImageView) dot;
                        if (i == currentPosition) {
                             dotImageView.setColorFilter(ContextCompat.getColor(activity, R.color.light_sky), PorterDuff.Mode.SRC_IN);
                        } else {
                             dotImageView.setColorFilter(ContextCompat.getColor(activity, R.color.dot_unselect_color), PorterDuff.Mode.SRC_IN);
                        }
                    }
                }
            }

        }

        public void clearAllData(){
            adapter.updateData(new ArrayList<>());
            adapter.notifyDataSetChanged();
        }


    }

    public class MyPagerAdapter extends PagerAdapter {
        private Context context;
        private List<String> bannerModels;
        private NotificationModel model;
        public MyPagerAdapter(Context context, List<String> bannerModels , NotificationModel model) {
            this.context = context;
            this.bannerModels = bannerModels;
            this.model = model;
        }

        @Override
        public int getCount() {
            return bannerModels.size();
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            LayoutInflater inflater = LayoutInflater.from( requireActivity() );
            PagerItemBinding binding = PagerItemBinding.inflate( inflater, container, false );

            binding.aedLayout.setVisibility(View.GONE);
            binding.ivMenu.setVisibility(View.GONE);

            String bannerModel = bannerModels.get( position );
            if (!bannerModel.isEmpty()) {
                Glide.with(requireActivity()).load(bannerModel).into(binding.imageView);
            }

            binding.getRoot().setOnClickListener(view -> {
                if (model.getRequestStatus().equals("rejected")) {
                    return;
                }
                activity.startActivity(new Intent(activity, CmPublicProfileActivity.class).putExtra("promoterUserId", model.getTypeId()));
            });

            container.addView( binding.getRoot() );
            return binding.getRoot();

        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView( (View) object );
        }
    }



    // endregion
    // --------------------------------------
}