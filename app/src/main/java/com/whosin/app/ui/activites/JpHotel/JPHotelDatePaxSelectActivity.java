package com.whosin.app.ui.activites.JpHotel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.HorizontalGridSpacingItemDecoration;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.ActivityJphotelDatePaxSelectctivityBinding;
import com.whosin.app.databinding.ItemSelectJpHotelBinding;
import com.whosin.app.databinding.LayoutJpAddChildrenViewBinding;
import com.whosin.app.service.manager.JPTicketManager;
import com.whosin.app.service.manager.RaynaTicketManager;
import com.whosin.app.service.models.JuniperHotelModels.PaxModel;
import com.whosin.app.service.models.JuniperHotelModels.PaxesItemModel;
import com.whosin.app.ui.activites.comman.BaseActivity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JPHotelDatePaxSelectActivity extends BaseActivity {

    private ActivityJphotelDatePaxSelectctivityBinding binding;

    private RoomAndPaxSelectAdapter<PaxesItemModel> adapter = new RoomAndPaxSelectAdapter<>();

    private JPTicketManager jpTicketManager = JPTicketManager.shared;

    // --------------------------------------
    // region LifeCycle
    // --------------------------------------


    @Override
    protected void initUi() {

        jpTicketManager.activityList.add(activity);

        binding.constraintHeader.tvTitle.setText(getValue("select_dates_and_rooms"));
        binding.tvAddRoom.setText(getValue("add_room"));
        binding.tvRoomTitle.setText(getValue("select_rooms_and_guests"));
        binding.tvNext.setText(getValue("next"));


        if (RaynaTicketManager.shared.raynaTicketDetailModel != null) {
            jpTicketManager.hotelRequestModel.setHotelCode(RaynaTicketManager.shared.raynaTicketDetailModel.getCode());
        }

        binding.fromDateLayout.fromDateCallBack = data -> {
            if (!TextUtils.isEmpty(data)) {
                jpTicketManager.hotelRequestModel.setStartDate(data);
                String nextDate = "";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    nextDate = LocalDate.parse(data).plusDays(1).toString();
                }
                binding.toDateLayout.setUpData(nextDate);
            }
        };
        binding.toDateLayout.toDateCallBack = data -> jpTicketManager.hotelRequestModel.setEndDate(data);;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding.fromDateLayout.setUpData(LocalDate.now().toString());
        } else {
            String today = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                    .format(new java.util.Date());
            binding.fromDateLayout.setUpData(today);
        }


        binding.fromDateLayout.setConfig(activity, getSupportFragmentManager(), getValue("select_start_dates"), false);
        binding.toDateLayout.setConfig(activity, getSupportFragmentManager(), getValue("select_end_dates"), true);

        binding.selectRoomRecyclerView.setLayoutManager(new LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false));
        DefaultItemAnimator animator = new DefaultItemAnimator();
        animator.setRemoveDuration(200);
        binding.selectRoomRecyclerView.setItemAnimator(animator);
        binding.selectRoomRecyclerView.setAdapter(adapter);
        addRoom();
    }

    @Override
    protected void setListeners() {

        binding.constraintHeader.ivClose.setOnClickListener(v -> finish());

        binding.btnAddRoom.setOnClickListener(v -> addRoom());

        binding.nextButton.setOnClickListener(v -> {
            if (adapter != null) {
                List<PaxesItemModel> paxModelList = new ArrayList<>(adapter.getData());
                jpTicketManager.hotelRequestModel.setPaxes(paxModelList);
                startActivity(new Intent(activity, JPHotelListActivity.class));
//                startActivity(new Intent(activity, JPHotelGuestListActivity.class));
            }
        });
    }

    @Override
    protected int getLayoutRes() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityJphotelDatePaxSelectctivityBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        jpTicketManager.activityList.remove(activity);
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    private void addRoom() {
        adapter.addItem(new PaxesItemModel((int)System.currentTimeMillis() % 100000));
        if (adapter != null){
            int itemCount = adapter.getItemCount();
            binding.selectRoomRecyclerView.scrollToPosition(itemCount - 1);
        }
    }

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------


    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    private class RoomAndPaxSelectAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_select_jp_hotel));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

            ViewHolder viewHolder = (ViewHolder) holder;

            PaxesItemModel model = (PaxesItemModel) getItem(position);

            viewHolder.mBinding.childrenRecycleView.setVisibility(model.getPax().isEmpty() ? View.GONE : View.VISIBLE);
            viewHolder.updateData(model);

            viewHolder.mBinding.btnRemove.setVisibility(position != 0 ? View.VISIBLE : View.GONE);

            String roomName = Utils.setLangValue("room",String.valueOf(viewHolder.getBindingAdapterPosition() + 1));
            viewHolder.mBinding.tvRoomName.setText(roomName);



            viewHolder.mBinding.adultView.setUpData(activity, getValue("adults_title"), getValue("age_18"), model.getAdultCount(),true, data -> {
            }, model::setAdultCount);

            viewHolder.mBinding.childView.setUpData(activity, getValue("children_title"), getValue("age_0_18"), model.getChildCount(),false, data -> {
                if (data) {
                    model.addPaxModel();
                } else {
                    model.removePax();
                }
                viewHolder.updateData(model);
            }, model::setChildCount);


            viewHolder.mBinding.btnRemove.setOnClickListener(v -> {
                int pos = holder.getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    adapter.removeItem(pos);
                    notifyItemRemoved(pos);
                    notifyItemRangeChanged(pos, getItemCount() - pos);
                }
            });
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final ItemSelectJpHotelBinding mBinding;

            private ChildAgeSelectAdapter<PaxModel> childAgeSelectAdapter = new ChildAgeSelectAdapter<>();

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemSelectJpHotelBinding.bind(itemView);

                mBinding.childrenRecycleView.setRecycledViewPool(new RecyclerView.RecycledViewPool());
                mBinding.childrenRecycleView.setLayoutManager(new GridLayoutManager(activity,2));
                mBinding.childrenRecycleView.setAdapter(childAgeSelectAdapter);


                mBinding.childrenRecycleView.setItemAnimator(null);

                if (mBinding.childrenRecycleView.getItemAnimator() instanceof SimpleItemAnimator) {
                    ((SimpleItemAnimator) mBinding.childrenRecycleView.getItemAnimator())
                            .setSupportsChangeAnimations(false); // avoids blink
                }

                if (mBinding.childrenRecycleView.getItemAnimator() instanceof SimpleItemAnimator) {
                    ((SimpleItemAnimator) mBinding.childrenRecycleView.getItemAnimator())
                            .setSupportsChangeAnimations(false);
                }


                if (mBinding.childrenRecycleView.getItemDecorationCount() == 0) {
                    mBinding.childrenRecycleView.addItemDecoration(new HorizontalGridSpacingItemDecoration(6));
                }

            }
            public void updateData(PaxesItemModel room){
                Log.d("roomData", "updateData: ");
                mBinding.childrenRecycleView.setVisibility(room.getPax().isEmpty() ? View.GONE : View.VISIBLE);
                childAgeSelectAdapter.refreshData(room.getPax());
            }
        }
    }

    private class ChildAgeSelectAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        List<String> ageList = new ArrayList<>();

        public ChildAgeSelectAdapter() {
            for (int i = 0; i <= 11; i++) {
                ageList.add(i + " yrs");
            }
            ageList.add("12+ yrs");
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.layout_jp_add_children_view));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            ViewHolder viewHolder = (ViewHolder) holder;
            PaxModel model = (PaxModel) getItem(position);


            String children = setValue("children_1",String.valueOf(viewHolder.getBindingAdapterPosition() + 1));
            viewHolder.binding.tvPaxTitle.setText(children);


            Spinner spinner = viewHolder.binding.spinnerOptions;

            if (spinner.getAdapter() == null) {
                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                        spinner.getContext(),
                        android.R.layout.simple_spinner_item,
                        ageList
                );
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(spinnerAdapter);
            }

            // Remove old listener before setting selection
            spinner.setOnItemSelectedListener(null);

            // Find the correct position in ageList for the model's stored value
            int selectedPos = 0; // default to first item

            if (model.getAge() != null && !model.getAge().isEmpty()) {
                int index = ageList.indexOf(model.getAge());
                if (index != -1) {
                    selectedPos = index;
                }
            }else {
                model.setAge(ageList.get(0));
            }

            spinner.setSelection(selectedPos, false); // setSelection without triggering listener

            // Reattach listener
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    // Store the actual selected value
                    String selectedAge = ageList.get(pos);
                    model.setAge(selectedAge);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final LayoutJpAddChildrenViewBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = LayoutJpAddChildrenViewBinding.bind(itemView);
            }
        }
    }

    // endregion
    // --------------------------------------
}