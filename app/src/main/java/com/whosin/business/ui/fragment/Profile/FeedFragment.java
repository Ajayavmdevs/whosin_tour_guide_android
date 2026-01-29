package com.whosin.business.ui.fragment.Profile;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.whosin.business.R;
import com.whosin.business.comman.AppExecutors;
import com.whosin.business.comman.Utils;
import com.whosin.business.databinding.FragmentFeedBinding;
import com.whosin.business.service.DataService;
import com.whosin.business.service.manager.SessionManager;
import com.whosin.business.service.models.AppSettingTitelCommonModel;
import com.whosin.business.service.models.ContainerListModel;
import com.whosin.business.service.models.MyUserFeedModel;
import com.whosin.business.service.rest.RestCallback;
import com.whosin.business.ui.fragment.comman.BaseFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FeedFragment extends BaseFragment {

    private FragmentFeedBinding binding;

    private List<MyUserFeedModel> usedFeedData = new ArrayList<>();

    private int page = 1;

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    public void initUi(View view) {

        binding = FragmentFeedBinding.bind(view);

        binding.emptyPlaceHolderView.setEmptyPlaceTxtTitle(getValue("feed_fragment_empty_message"));

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        Thread backgroundThread = new Thread(() -> {
            List<MyUserFeedModel> venueList = SessionManager.shared.getProfileFeed();
            if(venueList != null){
                usedFeedData = venueList;
                filterData();
            }
        });
        backgroundThread.start();

    }

    @Override
    public void setListeners() {
        binding.swipeRefreshLayout.setOnRefreshListener(() -> requestMyUserFeed(false));

        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) binding.recyclerView.getLayoutManager();
                if (linearLayoutManager == null) { return; }
                int lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();


            }
        });
    }

    @Override
    public void populateData(boolean getDataFromServer) {
        requestMyUserFeed(false);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_feed;
    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void filterData() {
        if (usedFeedData.isEmpty()){return;}
        List<MyUserFeedModel> otherUserFeed = usedFeedData.stream().filter(model -> model.getType().equals("friend_updates") || model.getType().equals("venue_updates") || model.getType().equals("event_checkin")).collect(Collectors.toList());
        AppExecutors.get().mainThread().execute(() -> {
            hideProgress();
        });

    }


    private void setTitleFromList(List<String> categoryIds, List<AppSettingTitelCommonModel> commonModels, String type, TextView textView) {
        if (categoryIds == null || categoryIds.isEmpty() || commonModels == null) {
            textView.setVisibility(View.GONE);
            return;
        }
        Thread backgroundThread = new Thread(() -> {
            String cuisineText = commonModels.stream().filter(p -> categoryIds.contains(p.getId())).map(AppSettingTitelCommonModel::getTitle).collect(Collectors.joining(", "));
            if (!cuisineText.isEmpty()) {
                SpannableString spannableString = new SpannableString(type + cuisineText);
                ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.WHITE);
                spannableString.setSpan(colorSpan, 0, type.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                requireActivity().runOnUiThread(() -> {
                    textView.setText(spannableString);
                    textView.setVisibility(View.VISIBLE);
                });
            } else {
                requireActivity().runOnUiThread(() -> textView.setVisibility(View.GONE));
            }
        });
        backgroundThread.start();
    }


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestMyUserFeed(boolean showPaginationLoader) {
        if (showPaginationLoader){
            binding.pagginationProgressBar.setVisibility(View.VISIBLE);
        }
        DataService.shared(requireActivity()).requestUserFeed(page, new RestCallback<ContainerListModel<MyUserFeedModel>>(this) {
            @Override
            public void result(ContainerListModel<MyUserFeedModel> model, String error) {
                hideProgress();
                binding.pagginationProgressBar.setVisibility(View.GONE);
                binding.swipeRefreshLayout.setRefreshing( false );
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (model.data != null && !model.data.isEmpty()) {
                    if (page == 1) { usedFeedData.clear(); }
                    usedFeedData.addAll(model.data);
                    SessionManager.shared.saveProfileFeed( model.data );
                    filterData();
                }
                binding.swipeRefreshLayout.setVisibility(usedFeedData.isEmpty() ? View.GONE : View.VISIBLE);
                binding.recyclerView.setVisibility(usedFeedData.isEmpty() ? View.GONE : View.VISIBLE);
                binding.emptyPlaceHolderView.setVisibility(usedFeedData.isEmpty() ? View.VISIBLE : View.GONE);
            }
        });
    }



    // --------------------------------------
    // region Adapter
    // --------------------------------------

    // endregion
    // --------------------------------------
}