package com.whosin.app.ui.fragment.Promoter.PromoterBootmSheets;

import static com.whosin.app.comman.AppDelegate.activity;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.FragmentPromoterCirclesBottomSheetBinding;
import com.whosin.app.databinding.ItemMyCircelsDetailListBinding;
import com.whosin.app.service.DataService;
import com.whosin.app.service.manager.SessionManager;
import com.whosin.app.service.models.ContainerListModel;
import com.whosin.app.service.models.ContainerModel;
import com.whosin.app.service.models.PromoterCirclesModel;
import com.whosin.app.service.rest.RestCallback;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PromoterCirclesBottomSheet extends DialogFragment {

    private FragmentPromoterCirclesBottomSheetBinding binding;

    private final PromoterCirclesAdapter<PromoterCirclesModel> circlesAdapter = new PromoterCirclesAdapter<>();

    public String otherUserId = "";

    public CommanCallback<Boolean> callback;


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

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

    public void setListener() {
        binding.ivClose.setOnClickListener(v -> dismiss());

    }

    public void initUi(View view) {
        binding = FragmentPromoterCirclesBottomSheetBinding.bind(view);

        binding.tvCircleTitle.setText(Utils.getLangValue("circle_list"));

        Glide.with(requireActivity()).load(R.drawable.icon_close_btn).into(binding.ivClose);

        binding.circlesRecycler.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false));
        binding.circlesRecycler.setAdapter(circlesAdapter);

        requestPromoterCirclesByUserId();
    }


    public int getLayoutRes() {
        return R.layout.fragment_promoter_circles_bottom_sheet;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = new BottomSheetDialog(requireActivity(), R.style.BottomSheetDialogThemeNoFloating);
        dialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
            FrameLayout bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            ViewGroup.LayoutParams layoutParam = bottomSheet.getLayoutParams();
            layoutParam.height = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParam.width = ViewGroup.LayoutParams.MATCH_PARENT;
            bottomSheet.setLayoutParams(layoutParam);
            BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
        });
        return dialog;
    }

    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------

    // endregion
    // --------------------------------------
    // region Data/Service
    // --------------------------------------

    public void requestPromoterCirclesByUserId() {
        if (TextUtils.isEmpty(otherUserId)) return;
        Graphics.showProgress(requireActivity());
        DataService.shared(requireActivity()).requestPromoterCirclesByUserId(otherUserId, new RestCallback<ContainerListModel<PromoterCirclesModel>>(this) {
            @Override
            public void result(ContainerListModel<PromoterCirclesModel> model, String error) {
                Graphics.hideProgress(requireActivity());
                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (model.data != null && !model.data.isEmpty()) {
                    circlesAdapter.updateData(model.data);
                }
            }
        });
    }

    private void requestPromoterCircleRemoveMember(String id) {
        JsonObject object = new JsonObject();

        JsonArray jsonArray = new JsonArray();
        jsonArray.add(otherUserId);

        object.add("memberIds", jsonArray);
        object.addProperty("id", id);


        DataService.shared(requireActivity()).requestPromoterCircleRemoveMember(object, new RestCallback<ContainerModel<PromoterCirclesModel>>(this) {
            @Override
            public void result(ContainerModel<PromoterCirclesModel> model, String error) {
                Graphics.hideProgress(requireActivity());

                if (!Utils.isNullOrEmpty(error) || model == null) {
                    Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (model.status == 1) {
                    if (callback !=null) callback.onReceive(true);
                    requestPromoterCirclesByUserId();
                }
            }
        });
    }


    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    private class PromoterCirclesAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_my_circels_detail_list));

        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            PromoterCirclesModel model = (PromoterCirclesModel) getItem(position);
            if (model == null) {
                return;
            }

            viewHolder.binding.tvViewMore.setText(Utils.getLangValue("view_more"));

            viewHolder.binding.ivMenu.setVisibility(View.VISIBLE);
            viewHolder.binding.getRoot().setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.bottom_bg_color));

            Graphics.loadImageWithFirstLetter(model.getAvatar(), viewHolder.binding.image, model.getTitle());
            viewHolder.binding.tvName.setText(model.getTitle());
            viewHolder.binding.emailTv.setText(model.getDescription());

            viewHolder.binding.ivMenu.setOnClickListener(view -> {
                Log.d("RecyclerView", "ivMenu clicked at position: " + position);
                Utils.preventDoubleClick(view);
                ArrayList<String> data = new ArrayList<>();
                data.add(Utils.getLangValue("remove_from_circle"));
                Graphics.showActionSheet(requireActivity(), model.getTitle(), data, (data1, position1) -> {
                    switch (position1) {
                        case 0:
                            Graphics.showAlertDialogWithOkCancel(requireActivity(), Utils.getLangValue("remove_from_circle"), Utils.getLangValue("remove_circle_alert"), aBoolean -> {
                                if (aBoolean) {
                                    List<String> memberIds = Collections.singletonList(model.getId());
                                    requestPromoterCircleRemoveMember(model.getId());

                                    Log.d("Id", "Ids: " + model.getId());
                                }
                            });
                            break;
                    }
                });
            });


        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final ItemMyCircelsDetailListBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemMyCircelsDetailListBinding.bind(itemView);
            }
        }
    }

    // endregion
    // --------------------------------------

}