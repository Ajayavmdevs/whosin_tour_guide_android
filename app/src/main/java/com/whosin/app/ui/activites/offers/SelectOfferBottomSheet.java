package com.whosin.app.ui.activites.offers;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.whosin.app.R;
import com.whosin.app.comman.DiffAdapter;
import com.whosin.app.comman.DiffIdentifier;
import com.whosin.app.comman.Graphics;
import com.whosin.app.comman.Utils;
import com.whosin.app.comman.interfaces.CommanCallback;
import com.whosin.app.comman.ui.UiUtils;
import com.whosin.app.databinding.ItemSelectOfferDesignBinding;
import com.whosin.app.databinding.SelectOfferBottomSheetBinding;
import com.whosin.app.service.models.OffersModel;

import java.util.List;


public class SelectOfferBottomSheet extends DialogFragment {

    private SelectOfferBottomSheetBinding binding;
    public List<OffersModel> offerList;
    private final OfferSelectAdapter<OffersModel> adapter = new OfferSelectAdapter<>();
    public CommanCallback<OffersModel> callback;
    public String offerId = "";


    // --------------------------------------
    // region LifeCycle
    // --------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutRes(), container, false);
        initUi(view);
        setListener();
        return view;
    }

    private void initUi(View v) {
        binding = SelectOfferBottomSheetBinding.bind(v);

        binding.emptyPlaceHolderView.setEmptyPlaceTxtTitle(Utils.getLangValue("offer_empty"));
        binding.tvBucketTitle.setText(Utils.getLangValue("offers"));

        binding.offerRecycler.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        binding.offerRecycler.setAdapter(adapter);
        if (offerList != null) {
            if (!offerList.isEmpty()) {
                adapter.updateData(offerList);
                binding.emptyPlaceHolderView.setVisibility(View.GONE);
            } else {
                binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
            }
        } else {
            binding.emptyPlaceHolderView.setVisibility(View.VISIBLE);
        }
    }

    private void setListener() {
        binding.ivClose.setOnClickListener(view -> dismiss());

    }

    private int getLayoutRes() {
        return R.layout.select_offer_bottom_sheet;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new BottomSheetDialog(requireActivity(), R.style.BottomSheetDialogThemeNoFloating);
    }


    // endregion
    // --------------------------------------
    // region Private
    // --------------------------------------


    // endregion
    // --------------------------------------
    // region Adapter
    // --------------------------------------

    public class OfferSelectAdapter<T extends DiffIdentifier> extends DiffAdapter<T, RecyclerView.ViewHolder> {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(UiUtils.getViewBy(parent, R.layout.item_select_offer_design));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            OffersModel model = (OffersModel) getItem(position);

            viewHolder.mBinding.view.setVisibility(View.GONE);
            viewHolder.mBinding.tvUserNumber.setVisibility(View.GONE);


            if (!TextUtils.isEmpty(offerId)){
                viewHolder.mBinding.ivCheck.setChecked(model.getId().equals(offerId));
            }

            Graphics.loadImage(model.getImage(), viewHolder.mBinding.ivUserProfile);
            viewHolder.mBinding.tvUserName.setText(model.getTitle());
            if (model.getDescription() != null && !model.getDescription().isEmpty()) {
                viewHolder.mBinding.tvContactBookName.setText(model.getDescription());
                viewHolder.mBinding.tvContactBookName.setVisibility(View.VISIBLE);
            }
            else {
                viewHolder.mBinding.tvContactBookName.setVisibility(View.GONE);
            }

            viewHolder.mBinding.getRoot().setOnClickListener(view -> {
                if (callback != null) {
                    callback.onReceive(model);
                }
                dismiss();
            });

            viewHolder.mBinding.ivCheck.setOnClickListener(view -> {
                if (callback != null) {
                    callback.onReceive(model);
                }
                dismiss();
            });
        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            private final ItemSelectOfferDesignBinding mBinding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mBinding = ItemSelectOfferDesignBinding.bind(itemView);

            }
        }
    }

    // endregion
    // --------------------------------------


}