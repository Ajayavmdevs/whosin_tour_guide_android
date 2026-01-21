package com.whosin.app.ui.activites.travelDeskTicket;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.JsonObject;
import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.FragmentTravelDeskPickUpListSheetBinding;
import com.whosin.app.databinding.ItemTravelDeskPickUpListBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.models.ContactListModel;
import com.whosin.app.service.models.ContainerListModel;
import com.whosin.app.service.models.TravelDeskModels.TravelDeskPickUpListModel;
import com.whosin.app.service.models.UserDetailModel;
import com.whosin.app.service.rest.RestCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import retrofit2.Call;

public class TravelDeskPickUpListSheet extends DialogFragment {

    private FragmentTravelDeskPickUpListSheetBinding binding;

    private Activity activity;

    public int selectedSlotPosition = -1;

    private String id = "";

    private Call<ContainerListModel<TravelDeskPickUpListModel>> slotApiCall;

    public CommanCallback<TravelDeskPickUpListModel> callback;

    private final PickUpLocationListAdapter<TravelDeskPickUpListModel> pickUpLocationListAdapter = new PickUpLocationListAdapter<>();

    private List<TravelDeskPickUpListModel> pickUpList = new ArrayList<>();

    private String searchQuery = "";

    private final Handler handler = new Handler();

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public String location = "";

//    public boolean isDirectReport = true;

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    public TravelDeskPickUpListSheet(Activity activity ,String optionId) {
        this.activity = activity;
        this.id = optionId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutRes(), container, false);
        initUi(view);
        setListener();
        return view;
    }

    private void initUi(View v) {
        binding = FragmentTravelDeskPickUpListSheetBinding.bind(v);

        binding.tvBucketTitle.setText(Utils.getLangValue("select_pickup_location"));
        binding.btnSelectPickUp.setTxtTitle(Utils.getLangValue("done"));
        binding.edtSearch.setHint(Utils.getLangValue("find_location"));

        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Glide.with(requireActivity()).load(R.drawable.icon_close).into(binding.ivClose);

        if (activity == null) {
            activity = requireActivity();
        }

        binding.pickUpListRecycler.setLayoutManager(new LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false));
        binding.pickUpListRecycler.setAdapter(pickUpLocationListAdapter);


        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("tourOptionId",id);
        requestRaynaTourTimeSlot(jsonObject);

    }

    private void setListener() {

        binding.ivClose.setOnClickListener(v -> dismiss());

        binding.btnSelectPickUp.setOnClickListener(v -> {
            if (callback != null && selectedSlotPosition != -1 && pickUpLocationListAdapter.getData() != null && !pickUpLocationListAdapter.getData().isEmpty()) {
//                model = pickUpLocationListAdapter.getData().get(selectedSlotPosition);
                Optional<TravelDeskPickUpListModel> model = pickUpLocationListAdapter.getData().stream().filter(p -> p.getId() == selectedSlotPosition).findFirst();
                if (model.isPresent()) {
                    if (model.get().getName().equals("Other Location") && model.get().getId() == 0){
                        AddOtherLocationDialog addOtherLocationDialog = new AddOtherLocationDialog();
                        addOtherLocationDialog.location = location;
                        TravelDeskPickUpListModel finalModel = model.get();
                        addOtherLocationDialog.callback = data -> {
                            if (!TextUtils.isEmpty(data)) {
                                finalModel.setName(data);
                                finalModel.setId(0);
                                callback.onReceive(finalModel);
                                dismiss();
                            }
                        };
                        addOtherLocationDialog.show(getChildFragmentManager(),"");
                    }else {
                        callback.onReceive(model.get());
                        dismiss();
                    }

                }
            }
        });

        binding.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchQuery = charSequence.toString().trim().toLowerCase();
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, 200);
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });

    }

    public int getLayoutRes() {
        return R.layout.fragment_travel_desk_pick_up_list_sheet;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = new BottomSheetDialog(requireActivity(), R.style.BottomSheetDialogThemeNoFloating);
        dialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
            FrameLayout bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            assert bottomSheet != null;
            ViewGroup.LayoutParams layoutParam = bottomSheet.getLayoutParams();
            layoutParam.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            layoutParam.width = ViewGroup.LayoutParams.MATCH_PARENT;
            bottomSheet.setLayoutParams(layoutParam);
            BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
        });
        return dialog;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (executor != null && !executor.isShutdown()){
            executor.shutdownNow();
        }
        if (slotApiCall != null && !slotApiCall.isCanceled()) {
            slotApiCall.cancel();
        }
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private final Runnable runnable = () -> executor.execute(() -> {
        String query = searchQuery.trim().toLowerCase();

        List<TravelDeskPickUpListModel> filteredList = new ArrayList<>();

        filteredList.add(new TravelDeskPickUpListModel() {{
            setName("Other Location");
            setId(0);
        }});

//        if (isDirectReport){
//            filteredList.add(new TravelDeskPickUpListModel() {{
//                setName("Other Location");
//                setId(0);
//            }});
//        }


        List<TravelDeskPickUpListModel> results = Utils.isNullOrEmpty(query)
                ? pickUpList
                : pickUpList.stream()
                .filter(model -> model.getName() != null &&
                        model.getName().toLowerCase().contains(query))
                .collect(Collectors.toList());

        filteredList.addAll(results);

        new Handler(Looper.getMainLooper()).post(() ->
                pickUpLocationListAdapter.updateData(filteredList));
    });


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    private void requestRaynaTourTimeSlot(JsonObject jsonObject) {
        binding.pagginationProgressBar.setVisibility(View.VISIBLE);
        slotApiCall = DataService.shared(activity).requestTravelDeskPickUpList(jsonObject, new RestCallback<ContainerListModel<TravelDeskPickUpListModel>>(this) {
            @Override
            public void result(ContainerListModel<TravelDeskPickUpListModel> model, String error) {
                binding.pagginationProgressBar.setVisibility(View.GONE);
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Graphics.showAlertDialogWithOkButton(activity, getString(R.string.app_name), error);
                    binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
                    return;
                }

                if (model.data != null && !model.data.isEmpty()) {
                    pickUpList.addAll(model.data);
                    List<TravelDeskPickUpListModel> tmpList = new ArrayList<>();
                    TravelDeskPickUpListModel travelDeskPickUpListModel = new TravelDeskPickUpListModel();
                    travelDeskPickUpListModel.setName("Other Location");
                    travelDeskPickUpListModel.setId(0);
                    tmpList.add(travelDeskPickUpListModel);
//                    if (isDirectReport){
//                        TravelDeskPickUpListModel travelDeskPickUpListModel = new TravelDeskPickUpListModel();
//                        travelDeskPickUpListModel.setName("Other Location");
//                        travelDeskPickUpListModel.setId(0);
//                        tmpList.add(travelDeskPickUpListModel);
//                    }
                    tmpList.addAll(model.data);
                    pickUpLocationListAdapter.updateData(tmpList);
                    Utils.showViews(binding.pickUpListRecycler);
                    binding.emptyPlaceHolderView.setVisibility(View.GONE);
                } else {
                    Utils.hideViews(binding.pickUpListRecycler);
                    binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    private class PickUpLocationListAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_travel_desk_pick_up_list));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            ViewHolder viewHolder = (ViewHolder) holder;

            TravelDeskPickUpListModel model = (TravelDeskPickUpListModel) getItem(position);

            viewHolder.binding.tvName.setText(model.getName());

            if (!TextUtils.isEmpty(model.getRegionName()) && !TextUtils.isEmpty(model.getCityName())) {
                viewHolder.binding.tvRegionName.setVisibility(View.VISIBLE);
                viewHolder.binding.tvRegionName.setText(model.getRegionName() + ", " + model.getCityName());
            } else {
                viewHolder.binding.tvRegionName.setVisibility(View.GONE);
            }


            viewHolder.binding.btnSelectTimeSlot.setOnCheckedChangeListener(null);
            viewHolder.binding.btnSelectTimeSlot.setChecked(selectedSlotPosition == model.getId());

            if (model.getId() == selectedSlotPosition) {
                viewHolder.binding.btnSelectTimeSlot.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(viewHolder.binding.btnSelectTimeSlot.getContext(), R.color.ticket_selected_colour)));
            } else {
                viewHolder.binding.btnSelectTimeSlot.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(viewHolder.binding.btnSelectTimeSlot.getContext(), R.color.white)));
            }

            viewHolder.itemView.setOnClickListener(v -> {
                Utils.preventDoubleClick(v);
                selectedSlotPosition = model.getId();
                notifyDataSetChanged();
            });

            viewHolder.binding.btnSelectTimeSlot.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedSlotPosition = model.getId();
                    notifyDataSetChanged();
                }
            });


        }


        public class ViewHolder extends RecyclerView.ViewHolder {

            private final ItemTravelDeskPickUpListBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemTravelDeskPickUpListBinding.bind(itemView);
            }
        }
    }


    // endregion
    // --------------------------------------

}
