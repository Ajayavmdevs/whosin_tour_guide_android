package com.whosin.app.ui.activites.bigBusTicket;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.FragmentTravelDeskPickUpListSheetBinding;
import com.whosin.app.databinding.ItemTravelDeskPickUpListBinding;
import com.whosin.app.service.models.BigBusModels.BigBusPickupPointsModel;
import com.whosin.app.service.models.TravelDeskModels.TravelDeskPickUpListModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class BigBusPickUpListSheet extends DialogFragment {

    private FragmentTravelDeskPickUpListSheetBinding binding;

    private Activity activity;

    public String selectedSlotPosition = "";

    public CommanCallback<BigBusPickupPointsModel> callback;

    private final PickUpLocationListAdapter<BigBusPickupPointsModel> pickUpLocationListAdapter = new PickUpLocationListAdapter<>();

    public List<BigBusPickupPointsModel> pickUpList;

    private String searchQuery = "";

    private final Handler handler = new Handler();

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    public BigBusPickUpListSheet(Activity activity ,String optionId) {
        this.activity = activity;
        this.selectedSlotPosition = optionId;
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

        if (activity == null) {activity = requireActivity();}

        binding.pickUpListRecycler.setLayoutManager(new LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false));
        binding.pickUpListRecycler.setAdapter(pickUpLocationListAdapter);

        if (pickUpList.isEmpty()) {
            pickUpList = new ArrayList<>();
        }else {
            pickUpLocationListAdapter.updateData(pickUpList);
        }

    }

    private void setListener() {

        binding.ivClose.setOnClickListener(v -> dismiss());

        binding.btnSelectPickUp.setOnClickListener(v -> {
            if (callback != null && pickUpLocationListAdapter.getData() != null && !pickUpLocationListAdapter.getData().isEmpty()) {
                Optional<BigBusPickupPointsModel> model = pickUpLocationListAdapter.getData().stream().filter(p -> p.getId().equals(selectedSlotPosition)).findFirst();
                if (model.isPresent()) {
                    callback.onReceive(model.get());
                    dismiss();
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
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private final Runnable runnable = () -> executor.execute(() -> {
        String query = searchQuery.trim().toLowerCase();

        List<BigBusPickupPointsModel> results = Utils.isNullOrEmpty(query)
                ? pickUpList
                : pickUpList.stream()
                .filter(model -> model.getName() != null &&
                        model.getName().toLowerCase().contains(query))
                .collect(Collectors.toList());

        List<BigBusPickupPointsModel> filteredList = new ArrayList<>(results);

        new Handler(Looper.getMainLooper()).post(() ->
                pickUpLocationListAdapter.updateData(filteredList));
    });


    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------


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

            BigBusPickupPointsModel model = (BigBusPickupPointsModel) getItem(position);

            viewHolder.binding.tvName.setText(model.getName());

            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) viewHolder.binding.tvName.getLayoutParams();
            int verticalMarginPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, viewHolder.binding.tvName.getContext().getResources().getDisplayMetrics());
            params.topMargin = verticalMarginPx;
            params.bottomMargin = verticalMarginPx;

            viewHolder.binding.tvName.setLayoutParams(params);

            viewHolder.binding.tvRegionName.setVisibility(View.GONE);


            viewHolder.binding.btnSelectTimeSlot.setOnCheckedChangeListener(null);
            viewHolder.binding.btnSelectTimeSlot.setChecked(selectedSlotPosition.equals(model.getId()));

            if (model.getId().equals(selectedSlotPosition)) {
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
